package com.mainsteam.stm.state.obj;

import java.io.Serializable;
import java.util.Map;

/** 缓存可用性指标的值，包括自定义指标
 * Created by Xiaopf on 2016/6/2.
 */
public class AvailabilityMetricData implements Serializable{

    private long instanceID;
    private Map<String, String> metricData;
    private String resourceID;

    public long getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(long instanceID) {
        this.instanceID = instanceID;
    }

    public Map<String, String> getMetricData() {
        return metricData;
    }

    public void setMetricData(Map<String, String> metricData) {
        this.metricData = metricData;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }
}
