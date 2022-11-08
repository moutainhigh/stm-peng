package com.mainsteam.stm.plugin.icmp.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Lich on 2017/6/5.
 */
public class RecycleTask implements Runnable {

    static final long GARBAGE_TIME = 2 * 24 * 60 * 60 * 1000L;
//    static final long GARBAGE_TIME = 60 * 1000L;
    private static final Log LOGGER = LogFactory.getLog(RecycleTask.class);
    private static final long TIME_WINDOW = 10000;
    private static final long SLEEP_TIME = 500;

    @Override
    public void run() {
        Thread.currentThread().setName("Ping-Recycle");
        BlockingQueue<Target> last = PingEngine.getLastQueue();
        BlockingQueue<Target> first = PingEngine.getFirstQueue();
        while (!Thread.interrupted()) {
            try {
                Target target = last.peek();
                if (target == null) {
                    Thread.sleep(TIME_WINDOW);
                } else {
                    if (System.currentTimeMillis() - target.getLastQueryTime() > GARBAGE_TIME) {
                        if (PingEngine.garbageCollect(target))
                            last.take();
                    } else {
                        long schedule = target.getLastExecuteTime() + TIME_WINDOW;
                        if (schedule > System.currentTimeMillis()) {
                            Thread.sleep(Math.max(schedule - System.currentTimeMillis(), SLEEP_TIME));
                        } else {
                            first.put(last.take());
                        }
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        LOGGER.error("Recycle thread interrupt.");
    }
}
