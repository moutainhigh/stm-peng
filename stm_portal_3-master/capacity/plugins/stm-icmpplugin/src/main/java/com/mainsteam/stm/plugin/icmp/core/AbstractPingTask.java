package com.mainsteam.stm.plugin.icmp.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Lich on 2017/5/25.
 */
abstract public class AbstractPingTask implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(AbstractPingTask.class);
    private static final int TARGETS_LENGTH = 20;
    private static final long SLEEP_TIME = 500;

    private final int maxTargets;
    private final int threads;
    private final int index;

    protected AbstractPingTask(int maxTargets, int threads, int index) {
        this.maxTargets = maxTargets;
        this.threads = threads;
        this.index = index;
    }

    @Override
    final public void run() {
        BlockingQueue<Target> previous = PingEngine.getQueue(index);
        BlockingQueue<Target> next = PingEngine.getQueue(index + 1);
        BlockingQueue<Target> last = PingEngine.getLastQueue();
        ArrayList<Target> targets = new ArrayList<>(TARGETS_LENGTH);
        ArrayList<Target> filterTargets = new ArrayList<>(TARGETS_LENGTH);
        String threadName = Thread.currentThread().getName();
        while (!Thread.interrupted()) {
            Thread.currentThread().setName(threadName);
            try {
                if (previous.drainTo(targets, maxTargets) > 0) {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info(this.getClass().getSimpleName() + " ready to handle size = " + targets.size() + ", " + targets);
                    for (Target target : targets) {
                        if (validate(target))
                            filterTargets.add(target);
                    }
                    try {
                        ping(filterTargets);
                    } catch (Throwable e) {
                        LOGGER.error("Ping error " + filterTargets, e);
                    }
                    for (Target target : targets) {
                        if (target.getState() == Target.AVAILABLE) {
                            last.put(target);
                        } else {
                            next.put(target);
                        }
                    }
                    targets.clear();
                    filterTargets.clear();
                } else {
                    Thread.sleep(SLEEP_TIME);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        LOGGER.error("Ping thread interrupt.");
    }

    abstract public void init();

    abstract public void ping(List<Target> targets);

    abstract public boolean validate(Target target);

    public int getMaxTargets() {
        return maxTargets;
    }

    public int getThreads() {
        return threads;
    }

    public int getIndex() {
        return index;
    }
}
