package com.mainsteam.stm.state.ext.aop.interceptor;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
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
@Component("instLifeCycleInterceptor")
public class InstLifeCycleInterceptor implements ValidateInterceptor {

    public static final int PRIORITY = 0;//拦截器执行优先级
    private static final Log logger = LogFactory.getLog(InstLifeCycleInterceptor.class);
    private static final int INVALID_INST = 0; //无效资源实例ID
    @Autowired
    private ResourceInstanceService resourceInstanceService;

    @Override
    public boolean validate(MetricCalculateData metricCalculateData, ResourceMetricDef resourceMetricDef, CustomMetric customMetric, Map<String, Object> map) {
        if (metricCalculateData.getResourceInstanceId() <= INVALID_INST) {
            return true;
        }
        // 获取资源状态
        ResourceInstance resourceInstance = null;
        try {
            resourceInstance = resourceInstanceService.getResourceInstance(metricCalculateData.getResourceInstanceId());
        } catch (InstancelibException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Query instance [" + metricCalculateData.getResourceInstanceId() + "] occurs exception.", e);
            }
            return true;
        }
        if (null ==resourceInstance || resourceInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
            if (logger.isWarnEnabled()) {
                StringBuilder bf = new StringBuilder(100);
                bf.append("Instance [");
                bf.append(metricCalculateData.getResourceInstanceId());
                bf.append("] is not found or not monitored.");
                logger.warn(bf.toString());
            }
            return true;
        }
        map.put("resourceInstance", resourceInstance);
        return false;
    }

    @Override
    public int getOrder() {
        return PRIORITY;
    }
}
