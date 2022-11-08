package com.mainsteam.stm.state.ext.persistence;

import com.mainsteam.stm.state.ext.exception.StateComputeException;
import com.mainsteam.stm.state.ext.process.bean.AlarmEventBean;
import com.mainsteam.stm.state.ext.tools.ThreadPoolUtil;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 告警时间持久化辅助类，由于在spring事务控制内无法使用多线程，所以需要在线程内使用事务，而非在事务中使用多线程
 */
@Component("alarmEventPersistHandler")
public class AlarmEventPersistHandler implements ApplicationListener<ContextRefreshedEvent> {

    private static final Log logger = LogFactory.getLog(AlarmEventPersistHandler.class);

    @Autowired
    @Qualifier("alarmEventPersist")
    private AlarmEventPersist alarmEventPersist;
    @Autowired
    private StateComputeException stateComputeException;

    @Autowired
    @Qualifier("stateThreadPoolUtil")
    private ThreadPoolUtil threadPoolUtil;

    private ExecutorService workers;

    public void run() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try{
                        final AlarmEventBean event = alarmEventPersist.poll();
                        if(null != event) {
                            Future<?> future = workers.submit(new Callable<Set<String>>() {
                                @Override
                                public Set<String> call() throws Exception {
                                    try{
                                        return alarmEventPersist.occurEvent(event);
                                    }catch (Throwable throwable) {
                                        Map<String, Object> additions = event.getAdditions();
                                        //持久化过程中如果发生异常，那么所有指标状态，指标值，告警队列，实例状态都要回滚
                                        MetricStateData curMetricState = (MetricStateData) additions.get("persistenceMetricState");
                                        if(logger.isErrorEnabled()) {
                                            logger.error("AlarmEvent persist failed, everything should rollback," + throwable.getMessage()
                                                    + ",data:("+
                                                    (null!=curMetricState? (curMetricState.getInstanceID() + "/" + curMetricState.getMetricID()) : "")
                                                    +")", throwable);
                                        }
                                        stateComputeException.dealWithException(additions, false);
                                    }
                                    return null;
                                }
                            });
                            Set<String> result = null;
                            try{
                                result = (Set<String>) future.get(threadPoolUtil.getAlarmEventTimeout(), TimeUnit.SECONDS);
                            }catch (Exception e) {
                                if(null != result) {
                                    Iterator<String> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        String next = iterator.next();
                                        if(StringUtils.equals(next, "alarmEvent")){
                                            event.setAlarmEventList(null);
                                        }else{
                                            event.getAdditions().remove(next);
                                        }
                                    }
                                    if(logger.isErrorEnabled()) {
                                        logger.error("exception for alarm event thread executed ,persists again:" + event , e);
                                    }
                                    try{
                                        alarmEventPersist.occurEvent(event);
                                    }catch (Throwable throwable) {
                                        if(logger.isErrorEnabled()) {
                                            logger.error(throwable.getMessage(), throwable);
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                    }catch (Exception e) {
                        if(logger.isErrorEnabled()) {
                            logger.error("alarm event persists error:" + e.getMessage(), e);
                        }
                    }
                }
            }
        }, "alarmEventPersistence-init");
        t.start();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(null == contextRefreshedEvent.getApplicationContext().getParent()){
            workers = Executors.newFixedThreadPool(threadPoolUtil.getAlarmEventThread(), new ThreadFactory() {
                AtomicInteger index = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "AlarmEventPersist-" + index.getAndIncrement());
                }
            });
            run();
        }
    }

}
