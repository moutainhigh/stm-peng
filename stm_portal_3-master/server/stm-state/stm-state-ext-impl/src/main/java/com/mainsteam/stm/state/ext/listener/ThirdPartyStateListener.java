package com.mainsteam.stm.state.ext.listener;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.process.impl.AlarmEventProcessor;
import com.mainsteam.stm.state.ext.process.impl.AlarmStateQueueProcessor;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.thirdparty.ThirdPartyMetricStateService;
import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方告警同步后，状态变化
 */
@Component("thirdPartyStateListener")
public class ThirdPartyStateListener extends AbstractProfileChangesListener {

    private static final Log logger = LogFactory.getLog(ThirdPartyStateListener.class);

    @Autowired
    private AlarmStateQueueProcessor alarmStateQueueProcessor;
    @Autowired
    private AlarmEventProcessor alarmEventProcessor;
    @Autowired
    private ResourceInstanceService resourceInstanceService;

    @Override
    public void process(DataSyncPO dataSyncPO) throws Exception {
        String data = dataSyncPO.getData();
        ThirdPartyMetricStateData thirdPartyMetricStateData = JSON.parseObject(data, ThirdPartyMetricStateData.class);
        process(thirdPartyMetricStateData, null);
    }

    void process(ThirdPartyMetricStateData thirdPartyMetricStateData, MetricStateEnum preMetricStateEnum) {
        if(logger.isInfoEnabled()) {
            logger.info("starts to compute thirdParty state :" + thirdPartyMetricStateData);
        }
        long instanceId = thirdPartyMetricStateData.getInstanceID();
        StateComputeContext context = new StateComputeContext();

        Map<String, Object> additions = new HashMap<>(10);
        try {
            ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
            additions.put("resourceInstance", resourceInstance);
        } catch (Exception e) {
            if(logger.isWarnEnabled()) {
                logger.warn("instance query failed("+instanceId+")," + e.getMessage(), e);
            }
            return;
        }
        if(null != preMetricStateEnum) {
            MetricStateData preMetricState = new MetricStateData();
            preMetricState.setType(MetricTypeEnum.PerformanceMetric);
            preMetricState.setState(preMetricStateEnum);
            preMetricState.setMetricID(thirdPartyMetricStateData.getMetricID());
            preMetricState.setInstanceID(instanceId);
            preMetricState.setCollectTime(thirdPartyMetricStateData.getUpdateTime());
            additions.put("preMetricStateData", preMetricState);
        }

        additions.put("metricType", MetricTypeEnum.PerformanceMetric);
        additions.put("metricState", thirdPartyMetricStateData.getState());
        additions.put("isThirdPartyState", Boolean.TRUE);
        context.setAdditions(additions);

        MetricCalculateData metricData = new MetricCalculateData();
        metricData.setCollectTime(thirdPartyMetricStateData.getUpdateTime());
        metricData.setMetricId(thirdPartyMetricStateData.getMetricID());
        metricData.setResourceInstanceId(instanceId);
        context.setMetricData(metricData);

        ResourceMetricDef resourceMetricDef = new ResourceMetricDef();
        resourceMetricDef.setMetricType(MetricTypeEnum.PerformanceMetric);
        resourceMetricDef.setId(thirdPartyMetricStateData.getMetricID());
        context.setMetricDef(resourceMetricDef);
        context.setCustomMetric(null);

        StateProcessorEnum process = (StateProcessorEnum) alarmStateQueueProcessor.process(context);
        if(process == StateProcessorEnum.ALARM_EVNET_PROCESSOR) {
            context.getAdditions().put("isAlarm", Boolean.FALSE);
            alarmEventProcessor.process(context);

        }
    }

    @Override
    public DataSyncTypeEnum get() {
        return DataSyncTypeEnum.OTHER_STATE;
    }
}
