package com.mainsteam.stm.state.ext.aop.interceptor;

import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.tools.AvailStateComputeUtil;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.obj.InstanceStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 采集状态计算拦截器,由于采集状态为非核心业务,并为了和资源状态计算独立开
 * 先暂时去掉可采集状态，但保留代码
 */
@Component("collectStateInterceptor")
@Aspect
public class CollectStateInterceptor {

    private static final Log logger = LogFactory.getLog(CollectStateInterceptor.class);

//    @Autowired
//    private AvailStateComputeUtil availStateComputeUtil;
    @Autowired
    private StateCatchUtil stateCatchUtil;

    public static final int METRIC_DATA_LENGTH = 1; //只有一个可用性指标缓存标志
    //public static final int PARENT_INSTANCE_FLAG = 0; //主资源标志

    @Pointcut(value="execution(* com.mainsteam.stm.state.ext.process.impl.InstStateQueueProcessor.process(..))")
    public void pointcut(){}

    @Around(value = "pointcut()")
    public Object filterAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object[] objects = proceedingJoinPoint.getArgs();
        StateComputeContext context = (StateComputeContext)objects[0];
        MetricCalculateData metricData = context.getMetricData();
        /*
        由于资源状态可能有多个可用性指标联合计算，即使在上一步指标状态已经发生改变，并且满足flapping的情况下，仍然需要使用可用性指标缓存数据进行资源状态计算,
        但有一种情况不需要再计算资源状态：当前资源只有一个可用性指标时，可用性指标状态就是资源状态
         */
        Map<String, MetricStateEnum> availCacheData = stateCatchUtil.getAvailabilityMetricState(metricData.getResourceInstanceId());
        Map<String, Object> additions = context.getAdditions();
//        if(null != availCacheData){ //需要保存上一次的值
//            Map<Long, Map<String,String>> historyAvailCache = new HashMap<>(availCacheData.size());
//            historyAvailCache.put(metricData.getResourceInstanceId(),availCacheData); //shallow copy, it's enough
//            additions.put("historyAvailCache", historyAvailCache);
//        }
//        CollectStateEnum collectStateEnum = null;
        ResourceInstance resourceInstance = (ResourceInstance) additions.get("resourceInstance");

        if(null != availCacheData &&
                ((!availCacheData.isEmpty() && !availCacheData.containsKey(metricData.getMetricId())) || (availCacheData.size() > METRIC_DATA_LENGTH))) {
            availCacheData.put(metricData.getMetricId(), (MetricStateEnum) additions.get("metricState"));
            if(logger.isDebugEnabled()) {
                logger.debug("inst{"+ resourceInstance.getId() +"} context avail map cache :" + availCacheData);
            }
            //当某个资源有2个以上的可用性指标时，需要将可用性指标告警出来
            Boolean availStateChanged = (Boolean) additions.get("availStateChanged");
            if(availStateChanged == Boolean.TRUE)
                additions.put("isAlarmAvail", Boolean.TRUE);
            additions.put("currentAvailCache", availCacheData);
        }
//        if(null != resourceInstance && resourceInstance.getParentId() <= PARENT_INSTANCE_FLAG) { //只有主资源才计算可采集状态
//            //考虑到可采集状态是由任一指标为16即为不采集，故不作多采集值缓存计算，后期这一功能可能去掉
//            Map<String, String> collectStateMapData = new HashMap<>(1);
//            collectStateMapData.put(metricData.getMetricId(), additions.get("availMetricValue").toString());
//            collectStateEnum = availStateComputeUtil.calculateCollectState(resourceInstance, collectStateMapData);
//            additions.put("collectState", collectStateEnum);
//        }

        Object proceed = proceedingJoinPoint.proceed(objects);
        if(additions.get("removeAvailMetricState") == Boolean.TRUE) {
            //取消某个可用性指标时，不需要产生一条恢复告警
            Map<String, MetricStateEnum> availabilityMetricState = stateCatchUtil.getAvailabilityMetricState(resourceInstance.getId());
            if(availabilityMetricState != null && availabilityMetricState.size() <=0)
                additions.put("isAlarmAvail", Boolean.FALSE);
        }
//        if(null != resourceInstance && resourceInstance.getParentId() <= PARENT_INSTANCE_FLAG ) {
//
//            Map<Long, InstanceStateData> persistenceInstStates = (Map<Long, InstanceStateData>) context.getAdditions().get("persistenceInstStates");
//            if(null == persistenceInstStates || persistenceInstStates.isEmpty()) {
//                synchronized (InstanceSyncUtils.getSyncObj(resourceInstance.getId())) {
//                    InstanceStateData preInstanceState = stateCatchUtil.getInstanceState(resourceInstance.getId());
//                    if(preInstanceState.getCollectStateEnum() != collectStateEnum) {
//                        if(logger.isInfoEnabled()) {
//                            logger.info("instance collection state changes("+resourceInstance.getId() +"/"
//                                    + preInstanceState.getCollectStateEnum()
//                                    + "-->" + collectStateEnum+"),data:" + availCacheData);
//                        }
//                        InstanceStateData instanceStateData = new InstanceStateData(preInstanceState.getInstanceID(),preInstanceState.getResourceState(),
//                                preInstanceState.getAlarmState(),metricData.getCollectTime(),preInstanceState.getCauseBymetricID(),
//                                collectStateEnum);
//                        stateCatchUtil.saveInstanceState(String.valueOf(resourceInstance.getId()), instanceStateData);
//                        if(null == persistenceInstStates)
//                            persistenceInstStates = new HashMap<>(1);
//                        Map<Long,InstanceStateData> historyInstStates = new HashMap<>(1);
//                        persistenceInstStates.put(instanceStateData.getInstanceID(), instanceStateData);
//                        historyInstStates.put(preInstanceState.getInstanceID(), preInstanceState);
//                        additions.put("persistenceInstStates", persistenceInstStates);
//                        additions.put("historyInstanceStates", historyInstStates);
//                    }
//
//                }
//            }
//
//        }

        return proceed;
    }
}
