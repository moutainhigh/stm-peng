package com.mainsteam.stm.plugin.icmp.core;

/**
 * Created by Lich on 2017/5/25.
 */

import com.mainsteam.stm.plugin.icmp.config.PingConfig;
import com.mainsteam.stm.plugin.icmp.config.PingTaskConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author lich
 * @version 4.2.3
 * @since 4.2.3
 */
public class PingEngine {

    public static final int RUNNING = 1;
    public static final int STOPPED = 0;

    private static final Log LOGGER = LogFactory.getLog(PingEngine.class);
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final List<AbstractPingTask> pingTasks = new ArrayList<>();
    private static final List<BlockingQueue<Target>> pingQueues = new ArrayList<>();
    private static final HashMap<String, Target> ipMap = new HashMap<>();
    private static ExecutorService pingThreadPool;
    private volatile static int state = STOPPED;

    private PingEngine() {
    }

    public static boolean init() {
        if (isAvailable())
            return true;
        lock.writeLock().lock();
        if (!isAvailable()) {
            PingConfig pingConfig = PingConfig.getDefaultConfig();
            if (LOGGER.isInfoEnabled())
                LOGGER.info(pingConfig);
            int poolSize = 0;
            for (PingTaskConfig taskConfig : pingConfig.getTaskConfigList()) {
                try {
                    Constructor<? extends AbstractPingTask> constructor = taskConfig.clazz.getConstructor(int.class, int.class, int.class);
                    AbstractPingTask pingTask = constructor.newInstance(taskConfig.maxTargets, taskConfig.threads, pingTasks.size());
                    pingTask.init();

                    pingTasks.add(pingTask);
                    pingQueues.add(new LinkedBlockingQueue<Target>());
                    poolSize += pingTask.getThreads();
                } catch (Throwable e) {
                    LOGGER.error("Add pingTask error " + taskConfig.clazz.getName(), e);
                }
            }
            if (LOGGER.isInfoEnabled())
                LOGGER.info("PingThreadPool size = " + poolSize);
            if (!pingTasks.isEmpty()) {
                pingQueues.add(new LinkedBlockingQueue<Target>());
                pingThreadPool = Executors.newFixedThreadPool(poolSize + 1, new ThreadFactory() {

                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "Ping-" + threadNumber.getAndIncrement());
                        if (t.isDaemon())
                            t.setDaemon(false);
                        if (t.getPriority() != Thread.NORM_PRIORITY)
                            t.setPriority(Thread.NORM_PRIORITY);
                        t.setUncaughtExceptionHandler(PingThreadExceptionHandler.getHandler());
                        return t;
                    }
                });
                for (AbstractPingTask pingTask : pingTasks) {
                    for (int i = 0; i < pingTask.getThreads(); ++i) {
                        pingThreadPool.execute(pingTask);
                    }
                }
                pingThreadPool.execute(new RecycleTask());
                state = RUNNING;
            }
        }
        lock.writeLock().unlock();
        return isAvailable();
    }

    public static void stop() {
        if (!isAvailable())
            return;
        lock.writeLock().lock();
        if (isAvailable()) {
            pingThreadPool.shutdownNow();
            pingTasks.clear();
            pingQueues.clear();
            ipMap.clear();
            state = 0;
        }
        lock.writeLock().unlock();
    }

    public static boolean isAvailable() {
        return state == RUNNING;
    }

    public static boolean validate(Target target) {
        lock.readLock().lock();
        if (!isAvailable())
            throw new IllegalStateException("PingEngine need init.");
        for (AbstractPingTask pingTask : pingTasks) {
            try {
                if (pingTask.validate(target)) {
                    lock.readLock().unlock();
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        lock.readLock().unlock();
        return false;
    }

    public static Target getResult(String ip) {
        if (!isAvailable())
            throw new IllegalStateException("PingEngine need init.");
        lock.readLock().lock();
        Target result = ipMap.get(ip);
        if (result != null) {
            result.setLastQueryTime(System.currentTimeMillis());
        }
        lock.readLock().unlock();
        return result == null ? null : result.clone();
    }

    public static void initTargetState(Map<String, Boolean> targetStateMap) {
        for (Map.Entry<String, Boolean> ipAliveState : targetStateMap.entrySet()) {
            String ip = ipAliveState.getKey();
            Boolean alive = ipAliveState.getValue();
            Target target = new Target(ip, Target.DEFAULT_TIMEOUT, Target.DEFAULT_RETRY);
            if (alive)
                target.restoreHealth();
            else
                target.setHealth(0);
            addTarget(target);
        }
    }

    public static boolean addTarget(Target target) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Try to add target = " + target);
        if (!isAvailable())
            throw new IllegalStateException("PingEngine need init.");
        if (!validate(target))
            throw new IllegalArgumentException("Invalid target ip.");
        lock.writeLock().lock();
        Target origin = ipMap.get(target.getIp());
        if (origin == null) {
            ipMap.put(target.getIp(), target);
            try {
                getFirstQueue().put(target);
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Successfully add target  = " + target);
            } catch (InterruptedException e) {
                ipMap.remove(target.getIp());
                lock.writeLock().unlock();
                return false;
            }
        } else {
            origin.setTimeout(target.getTimeout());
            origin.setRetry(target.getRetry());
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Successfully renew target  = " + target);
        }
        lock.writeLock().unlock();
        return true;
    }

    static BlockingQueue<Target> getQueue(int index) {
        return pingQueues.get(index);
    }

    static BlockingQueue<Target> getFirstQueue() {
        return getQueue(0);
    }

    static BlockingQueue<Target> getLastQueue() {
        return getQueue(pingQueues.size() - 1);
    }

    static boolean garbageCollect(Target target) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Try to gc target = " + target);
        lock.writeLock().lock();
        if (System.currentTimeMillis() - target.getLastQueryTime() > RecycleTask.GARBAGE_TIME) {
            Target result = ipMap.remove(target.getIp());
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Successfully gc target = " + result);
            lock.writeLock().unlock();
            return true;
        }
        lock.writeLock().unlock();
        return false;
    }

    private static class PingThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

        private static final PingThreadExceptionHandler handler = new PingThreadExceptionHandler();

        public static Thread.UncaughtExceptionHandler getHandler() {
            return handler;
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOGGER.error("Uncaught exception! Thread stopped", e);
        }
    }
}
