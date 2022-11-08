package com.mainsteam.stm.state.ext.process.impl;

import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.process.StateProcessor;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.obj.InstanceStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Xiaopf on 2017/7/11.
 * 链路状态计算接口
 */
@Component("linkProcessor")
public class LinkProcessor implements StateProcessor {

    private static final Log logger = LogFactory.getLog(LinkProcessor.class);
    @Autowired
    private ResourceInstanceService resourceInstanceService;
    @Autowired
    private StateCatchUtil stateCatchUtil;
    /*
     * 链路可用性计算规则为，取连线两端接口的状态之间的最高级别状态，与之前的链路状态比较，
     * 如果变化则入库和告警并且更新缓存当前链路状态，否则不处理。
     */
    @Override
    public Object process(StateComputeContext context) {

        ResourceMetricDef metricDef = context.getMetricDef();
        //可用性指标进来才需要计算链路的资源状态，告警状态按照正常的性能指标计算逻辑处理
        ResourceInstance resourceInstance = (ResourceInstance) context.getAdditions().get("resourceInstance");
        if(metricDef.getMetricType() == MetricTypeEnum.AvailabilityMetric) {
            String[] moduleProps = new String[]{LinkResourceConsts.PROP_SRC_SUBINST_ID, LinkResourceConsts.PROP_DEST_SUBINST_ID};
            List<Long> endpointIds = new ArrayList<>(2);
            for(String prop : moduleProps) {
                try {
                    String[] array = resourceInstance.getModulePropBykey(prop);
                    if (array != null && array.length > 0) {
                        endpointIds.add(Long.parseLong(array[0]));
                    }else{
                        if(logger.isDebugEnabled()) {
                            logger.debug("link {" + resourceInstance.getId() +"{ cancel compute state,cause null prop :" + prop);
                        }
                        return null;
                    }
                }catch (Exception e){
                    if(logger.isWarnEnabled()) {
                        logger.warn("convert link instance id error(data:"+ endpointIds +").", e);
                    }
                    return null;
                }

            }

            List<InstanceStateData> collectionState = new ArrayList<>(2);
            List<ResourceInstance> resourceInstances;
            try {
                resourceInstances = resourceInstanceService.getResourceInstances(endpointIds);
            } catch (InstancelibException e) {
                if(logger.isWarnEnabled()) {
                    logger.warn("Query instance error(link:"+resourceInstance.getId()+"/"+endpointIds+")." + e.getMessage(), e);
                }
                return null;
            }
            synchronized (InstanceSyncUtils.getSyncObj(resourceInstance.getId())) {
                for(ResourceInstance current : resourceInstances) {
                    if(current.getLifeState() == InstanceLifeStateEnum.MONITORED) {
                        InstanceStateData instanceStateData = stateCatchUtil.getInstanceState(current.getId());
                        if(null != instanceStateData) {
                            collectionState.add(instanceStateData);
                        }
                    }
                }
                InstanceStateData curInstanceStateData;
                InstanceStateData preInstanceStateData;
                if(collectionState.isEmpty()) {//两端接口和两端设备状态都为空的情况下，默认为正常
                    return null;
                }else {
                    curInstanceStateData = Collections.max(collectionState);
                }

                preInstanceStateData = stateCatchUtil.getInstanceState(resourceInstance.getId());
                //链路的资源状态告警
                if (null == preInstanceStateData || curInstanceStateData.getResourceState() !=preInstanceStateData.getResourceState()) {
                    if (logger.isInfoEnabled()) {
                        StringBuilder logs = new StringBuilder();
                        logs.append("Link state has changed(data:");
                        logs.append(resourceInstance.getId());
                        if(null != preInstanceStateData) {
                            logs.append("/");
                            logs.append(preInstanceStateData.getResourceState());
                        }
                        logs.append(";cause interface:");
                        logs.append(curInstanceStateData.getResourceState());
                        logs.append("/");
                        logs.append(curInstanceStateData.getInstanceID());
                        logs.append(").");
                        logger.info(logs.toString());
                    }
                    curInstanceStateData.setInstanceID(resourceInstance.getId());
                    if(null != preInstanceStateData) {
                        /*
                        链路计算资源状态不改变causeInstance,不改变causeMetric.这主要是为了和普通的性能指标告警状态计算对应上
                         */
                        curInstanceStateData.setAlarmState(preInstanceStateData.getAlarmState());
                        Map<Long,InstanceStateData> historyInstanceStates = new HashMap<>(1);
                        historyInstanceStates.put(preInstanceStateData.getInstanceID(),preInstanceStateData);
                        context.getAdditions().put("historyInstanceStates", historyInstanceStates);
                    }
                    curInstanceStateData.setCollectTime(context.getMetricData().getCollectTime());
                    Map<Long, InstanceStateData> persistenceInstStates = new HashMap<>(1);
                    if(logger.isInfoEnabled()) {
                        logger.info("link {" + resourceInstance.getId() + "} cache state:" + curInstanceStateData);
                    }
                    stateCatchUtil.saveInstanceState(String.valueOf(curInstanceStateData.getInstanceID()), curInstanceStateData);
                    persistenceInstStates.put(curInstanceStateData.getInstanceID(),curInstanceStateData);
                    context.getAdditions().put("persistenceInstStates", persistenceInstStates);

                    context.getAdditions().put("linkCurInstState", curInstanceStateData.getResourceState());
                    context.getAdditions().put("isAlarm", Boolean.TRUE);
                    return StateProcessorEnum.ALARM_EVNET_PROCESSOR;
                }
            }
            return null;

        }else {
            InstanceStateData instanceState = stateCatchUtil.getInstanceState(resourceInstance.getId());
            if(null != instanceState && instanceState.getResourceState() == InstanceStateEnum.CRITICAL)
                return null;
            return StateProcessorEnum.PERF_METRIC_PROCESSOR;
        }
    }

    @Override
    public StateProcessorEnum processOrder() {
        return StateProcessorEnum.LINK_PROCESSOR;
    }

    @Override
    public String toString() {
        return "LinkProcessor{"+processOrder()+"}";
    }
}
