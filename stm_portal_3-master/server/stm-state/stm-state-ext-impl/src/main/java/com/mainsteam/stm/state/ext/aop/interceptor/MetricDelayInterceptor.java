package com.mainsteam.stm.state.ext.aop.interceptor;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.state.ext.aop.ValidateInterceptor;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/10.
 * 采集时间拦截器.如果当前采集时间早于缓存中最新的采集时间，则需要抛弃掉
 */
@Component("metricDelayFilter")
public class MetricDelayInterceptor implements ValidateInterceptor {

    private static final Log logger = LogFactory.getLog(MetricDelayInterceptor.class);

    private static final int PRIORITY = 1;

    @Autowired
    private StateCatchUtil stateCatchUtil;

    @Override
    public boolean validate(MetricCalculateData metricCalculateData, ResourceMetricDef resourceMetricDef, CustomMetric customMetric, Map<String, Object> map) {
        long instanceId = metricCalculateData.getResourceInstanceId();
        String metricId = metricCalculateData.getMetricId();
        Date collectTime = metricCalculateData.getCollectTime();
        // 为了在指标状态比较时，不会和失效的数据比较（保证线程安全性）
        MetricStateData preMetricStateData = stateCatchUtil.getMetricState(instanceId, metricId);
        // 如果当前采集时间早于缓存中最新的采集时间，则需要抛弃掉
        if (null != preMetricStateData) {
            if (collectTime.before(preMetricStateData.getCollectTime())) {
                if (logger.isInfoEnabled()) {
                    logger.info("{" + instanceId + "/" + metricId + "} had delayed(date:"+collectTime+"), drop it.");
                }
                return true;
            } else {
                map.put("preMetricStateData", preMetricStateData);
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return PRIORITY;
    }
}
