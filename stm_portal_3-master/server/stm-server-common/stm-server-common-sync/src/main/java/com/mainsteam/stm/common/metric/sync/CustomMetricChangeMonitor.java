package com.mainsteam.stm.common.metric.sync;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncService;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.interceptor.CustomMetricAlarmChange;
import com.mainsteam.stm.metric.interceptor.CustomMetricMonitorChange;
import com.mainsteam.stm.metric.interceptor.CustomResourceMonitorChange;
import com.mainsteam.stm.metric.obj.CustomChangeData;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;

/**
 * 自定义指标取消监控，告警，取消绑定资源告警
 */
public class CustomMetricChangeMonitor implements CustomMetricAlarmChange,CustomMetricMonitorChange,CustomResourceMonitorChange{

    private static final Log logger = LogFactory.getLog(CustomMetricChangeMonitor.class);

    private DataSyncService dataSyncService;

    private CustomMetricService customMetricService;

    public void setDataSyncService(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    public void setCustomMetricService(CustomMetricService customMetricService) {
        this.customMetricService = customMetricService;
    }

    @Override
    public void metricAlarmChange(List<CustomChangeData> ProfileMetricChanges) throws Exception {
        customMetricChanges(ProfileMetricChanges);
    }

    @Override
    public void metricMonitorChange(List<CustomChangeData> ProfileMetricChanges) throws Exception {
        customMetricChanges(ProfileMetricChanges);
    }


    @Override
    public void monitorChange(List<CustomChangeData> ProfileMetricChanges) throws Exception {
        for (CustomChangeData custom: ProfileMetricChanges) {
            List<Long> instanceIds = custom.getCancelInstanceIds();
            if(instanceIds != null) {
                for (Long instanceId: instanceIds) {
                    DataSyncPO obj = createObj(false, false, custom.getMetricId(), instanceId);
                    dataSyncService.save(obj);
                    logger.info("save metric[" + instanceId + "/" + custom.getMetricId() + "] profile Change");
                }
            }
        }
    }

    private void customMetricChanges(List<CustomChangeData> ProfileMetricChanges) throws Exception{
        if(logger.isDebugEnabled()){
            logger.debug("custom metrics cancel alarm : " + ProfileMetricChanges);
        }
        for (CustomChangeData custom: ProfileMetricChanges) {
            List<CustomMetricBind> customMetricBindsByMetricId = customMetricService.getCustomMetricBindsByMetricId(custom.getMetricId());
            if(customMetricBindsByMetricId != null) {
                for (CustomMetricBind metricBind: customMetricBindsByMetricId) {
                    DataSyncPO obj = createObj(custom.getIsAlarm(), custom.getIsMonitor(), metricBind.getMetricId(), metricBind.getInstanceId());
                    dataSyncService.save(obj);
                    logger.info("save metric["+metricBind.getInstanceId()+"/"+metricBind.getMetricId()+"] profile Change");
                }
            }
        }
    }

    private DataSyncPO createObj(Boolean isAlarm, Boolean isMonitor, String metricId, long instanceId) {
        MetricProfileChange pc=new MetricProfileChange();
        pc.setMetricID(metricId);
        pc.setIsAlarm(isAlarm);
        pc.setIsMonitor(isMonitor);
        pc.setInstanceId(instanceId);
        pc.setCustomMetric(true);

        DataSyncPO po=new DataSyncPO();
        po.setCreateTime(new Date());
        po.setUpdateTime(po.getCreateTime());
        po.setType(DataSyncTypeEnum.METRIC_STATE);
        po.setData(JSON.toJSONString(pc));

        return po;
    }
}
