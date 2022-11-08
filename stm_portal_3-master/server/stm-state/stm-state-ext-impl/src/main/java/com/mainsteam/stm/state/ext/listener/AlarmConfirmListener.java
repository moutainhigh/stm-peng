package com.mainsteam.stm.state.ext.listener;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmConfirm;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.sync.MetricProfileChange;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.state.thirdparty.ThirdPartyMetricStateService;
import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("alarmConfirmListener")
public class AlarmConfirmListener extends AbstractProfileChangesListener {

    private static final Log logger = LogFactory.getLog(AlarmConfirmListener.class);

    @Autowired
    private ThirdPartyMetricStateService thirdPartyMetricStateService;
    @Autowired
    private MetricProfileChangesListener metricProfileChangesListener;
    @Autowired
    @Qualifier("thirdPartyStateListener")
    private ThirdPartyStateListener thirdPartyStateListener;

    @Override
    public void process(DataSyncPO dataSyncPO) throws Exception {

        String data = dataSyncPO.getData();
        AlarmConfirm alarmConfirm = JSON.parseObject(data, AlarmConfirm.class);

        if(logger.isInfoEnabled()) {
            logger.info("alarm confirm starts : " + alarmConfirm);
        }
        process(alarmConfirm);

    }

    private void process(AlarmConfirm alarmConfirm) {
        if(alarmConfirm.isOtherAlarm()) {
            if(alarmConfirm.getInstanceId() == 0L) {
                return;
            }

            ThirdPartyMetricStateData preState = thirdPartyMetricStateService.findThirdPartyMetricState(alarmConfirm.getInstanceId(), alarmConfirm.getMetricId());

            if(preState != null && preState.getState() != MetricStateEnum.NORMAL) {

                ThirdPartyMetricStateData thirdPartyMetricStateData = new ThirdPartyMetricStateData();
                thirdPartyMetricStateData.setInstanceID(alarmConfirm.getInstanceId());
                thirdPartyMetricStateData.setMetricID(alarmConfirm.getMetricId());
                thirdPartyMetricStateData.setUpdateTime(alarmConfirm.getConfirmTime());
                thirdPartyMetricStateData.setState(MetricStateEnum.NORMAL);
                thirdPartyMetricStateService.updateIfExistsOrAddState(thirdPartyMetricStateData);

                thirdPartyStateListener.process(thirdPartyMetricStateData, preState.getState());
            }


        }else {
            MetricProfileChange metricProfileChange = new MetricProfileChange();
            //告警确认从效果来看和指标取消告警是一致，都需要恢复成正常状态
            metricProfileChange.setIsAlarm(false);
            metricProfileChange.setMetricID(alarmConfirm.getMetricId());
            metricProfileChange.setAlarmConfirm(true);
            metricProfileChange.setInstanceId(alarmConfirm.getInstanceId());
            metricProfileChange.setProfileID(0L);
            metricProfileChangesListener.process(metricProfileChange);
        }

    }

    @Override
    public DataSyncTypeEnum get() {
        return DataSyncTypeEnum.ALARM_CONFIRM;
    }
}
