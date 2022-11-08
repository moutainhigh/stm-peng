package com.mainsteam.stm.state.ext.aop.interceptor;

import com.mainsteam.stm.alarm.confirm.AlarmConfirmService;
import com.mainsteam.stm.alarm.obj.AlarmConfirm;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.state.ext.aop.ValidateInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/11.
 */
@Component("alarmConfirmInterceptor")
public class AlarmConfirmInterceptor implements ValidateInterceptor {

    private static final Log logger = LogFactory.getLog(AlarmConfirmInterceptor.class);

    public static final int PRIORITY = 3;//拦截器执行优先级

    @Autowired
    private AlarmConfirmService alarmConfirmService;

    @Override
    public boolean validate(MetricCalculateData metricCalculateData, ResourceMetricDef resourceMetricDef, CustomMetric customMetric, Map<String, Object> map) {
        // 判断是否已进行告警确认
        AlarmConfirm condition = new AlarmConfirm();
        condition.setInstanceId(metricCalculateData.getResourceInstanceId());
        condition.setMetricId(metricCalculateData.getMetricId());
        AlarmConfirm alarmConfirm = alarmConfirmService.findAlarmConfirm(condition);
        if (alarmConfirmService.getAlarmConfirmBlocked(alarmConfirm)) {
            if (logger.isDebugEnabled()){
                logger.debug("Restrain to compute it(data:" + metricCalculateData.getResourceInstanceId()
                        + "/" + metricCalculateData.getMetricId() + ").");
            }
            return true;
        }
        return false;
    }

    @Override
    public int getOrder() {
        return PRIORITY;
    }
}
