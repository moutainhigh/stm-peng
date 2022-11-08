package com.mainsteam.stm.state.ext.aop;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import org.springframework.core.Ordered;

import java.util.Map;

/** 拦截器校验接口
 * Created by Xiaopf on 2017/7/11.
 */
public interface ValidateInterceptor extends Ordered {

    boolean validate(MetricCalculateData metricCalculateData, ResourceMetricDef resourceMetricDef, CustomMetric customMetric, Map<String, Object> map);
}
