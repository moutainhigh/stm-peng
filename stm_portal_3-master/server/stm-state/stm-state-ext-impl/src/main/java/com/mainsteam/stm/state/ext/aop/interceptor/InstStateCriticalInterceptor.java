package com.mainsteam.stm.state.ext.aop.interceptor;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.state.ext.aop.ValidateInterceptor;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.obj.InstanceStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/11.
 */
@Component("instStateCriticalInterceptor")
public class InstStateCriticalInterceptor implements ValidateInterceptor{

    private static final Log logger = LogFactory.getLog(InstStateCriticalInterceptor.class);

    public static final int PRIORITY = 2; //拦截器执行优先级，数字越小，优先级越高
    public static final int INST_CHILD_FLAG = 0;//子资源标志
    @Autowired
    private StateCatchUtil stateCatchUtil;


    @Override
    public boolean validate(MetricCalculateData metricCalculateData, ResourceMetricDef resourceMetricDef, CustomMetric customMetric, Map<String, Object> map) {
        ResourceInstance resourceInstance = (ResourceInstance) map.get("resourceInstance");
        /**
         * 1.当前子资源状态为致命时，所有的性能指标和可用性指标都不进行状态计算
         * 2.如果当前为子资源，并且主资源状态为致命，则子资源不进行状态计算
         */
        InstanceStateData instanceStateData = stateCatchUtil.getInstanceState(resourceInstance.getId());
        if(null != instanceStateData && instanceStateData.getResourceState() == InstanceStateEnum.CRITICAL) {
            if(((null != resourceMetricDef && resourceMetricDef.getMetricType() != MetricTypeEnum.AvailabilityMetric)
                    || (null != customMetric && customMetric.getCustomMetricInfo().getStyle() != MetricTypeEnum.AvailabilityMetric))) {
                if (logger.isDebugEnabled()) {
                    StringBuilder stringBuilder = new StringBuilder(100);
                    stringBuilder.append("Instance has been critical. stop to compute it(Data:");
                    stringBuilder.append(resourceInstance.getId());
                    stringBuilder.append("/");
                    stringBuilder.append(metricCalculateData.getMetricId());
                    stringBuilder.append(").");
                    logger.debug(stringBuilder.toString());
                }
                return true;
            }
        }

        if (resourceInstance.getParentId() > INST_CHILD_FLAG ) { //子资源
            InstanceStateData mainInstanceState = stateCatchUtil.getInstanceState(resourceInstance.getParentId());
            if (null != mainInstanceState && InstanceStateEnum.CRITICAL == mainInstanceState.getState()) {
                if (logger.isDebugEnabled()) {
                    StringBuilder stringBuilder = new StringBuilder(100);
                    stringBuilder.append("Parent instance has been critical. stop to compute it(Data:");
                    stringBuilder.append(resourceInstance.getId());
                    stringBuilder.append("/");
                    stringBuilder.append(metricCalculateData.getMetricId());
                    stringBuilder.append(").");
                    logger.debug(stringBuilder.toString());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return PRIORITY;
    }
}
