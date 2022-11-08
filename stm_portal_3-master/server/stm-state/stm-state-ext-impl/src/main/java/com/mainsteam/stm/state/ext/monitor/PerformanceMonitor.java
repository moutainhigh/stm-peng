package com.mainsteam.stm.state.ext.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 后台性能监控
 */
@Component("performanceMonitor")
public class PerformanceMonitor implements ApplicationListener<ContextRefreshedEvent> {

    private Log logger = LogFactory.getLog(PerformanceMonitor.class);

    //指标数据进入StateComputeDispatcher队列计数，用于状态计算接收吞吐量
    private AtomicLong offerCount = new AtomicLong(0);
    //每当一个状态计算完成之后，该值增加1，用于状态计算发送吞吐量
    private AtomicLong pollCount = new AtomicLong(0);

    public void incrementOffer() {
        offerCount.compareAndSet(Long.MAX_VALUE, 0);
        offerCount.incrementAndGet();
    }

    public void incrementPoll() {
        pollCount.compareAndSet(Long.MAX_VALUE, 0);
        pollCount.incrementAndGet();
    }

    //每分钟接收吞吐量
    public void minuteOfferThroughput() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long pre = offerCount.longValue();
                    try {
                        TimeUnit.MINUTES.sleep(1L);
                    } catch (InterruptedException e) {
                        if(logger.isErrorEnabled()) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    long aft = offerCount.longValue();

                    logger.info("state compute offer throughput :" + (aft - pre) + " per min.");
                }
            }
        }, "minuteOfferThroughputThread");

        thread.start();
    }

    //每分钟发送吞吐量
    public void minutePollThroughput() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long pre = pollCount.longValue();
                    try {
                        TimeUnit.MINUTES.sleep(1L);
                    } catch (InterruptedException e) {
                        if(logger.isErrorEnabled()) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    long aft = pollCount.longValue();

                    logger.info("state compute poll throughput :" + (aft - pre) + " per min.");
                }
            }
        }, "minutePollThroughputThread");

        thread.start();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null) {
            minuteOfferThroughput();
            minutePollThroughput();
        }
    }
}
