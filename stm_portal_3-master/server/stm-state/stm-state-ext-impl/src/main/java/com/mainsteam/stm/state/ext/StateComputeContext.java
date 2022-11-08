package com.mainsteam.stm.state.ext;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/10.
 */
public class StateComputeContext {

    private MetricCalculateData metricData;
    private ResourceMetricDef metricDef;
    private CustomMetric customMetric;
    private Map<String, Object> additions;

    public StateComputeContext(MetricCalculateData metricData, ResourceMetricDef metricDef,
                               CustomMetric customMetric, Map<String, Object> additions) {
        this.metricData = metricData;
        if(null == additions)
            this.additions = new HashMap<>(18);
        this.additions = additions;
        this.metricDef = metricDef;
        this.customMetric = customMetric;
    }

    public StateComputeContext() {
    }

    public MetricCalculateData getMetricData() {
        return metricData;
    }

    public void setMetricData(MetricCalculateData metricData) {
        this.metricData = metricData;
    }

    public ResourceMetricDef getMetricDef() {
        return metricDef;
    }

    public void setMetricDef(ResourceMetricDef metricDef) {
        this.metricDef = metricDef;
    }

    public CustomMetric getCustomMetric() {
        return customMetric;
    }

    public void setCustomMetric(CustomMetric customMetric) {
        this.customMetric = customMetric;
    }

    public Map<String, Object> getAdditions() {
        return additions;
    }

    public void setAdditions(Map<String, Object> additions) {
        this.additions = additions;
    }

    @Override
    public String toString() {
        String profileId = "";
        String timeline = "";
        if(null != metricData) {
            profileId += metricData.getProfileId();
            timeline += metricData.getTimelineId();
        }
        return "StateComputeContext{" +
                "metricData=" + metricData +
                ", additions=" + additions +
                ", profileId=" + profileId +
                ", timeline=" + timeline +
                '}';
    }
}
