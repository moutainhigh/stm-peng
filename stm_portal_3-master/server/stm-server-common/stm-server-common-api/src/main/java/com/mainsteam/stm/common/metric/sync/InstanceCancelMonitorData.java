package com.mainsteam.stm.common.metric.sync;

import java.util.Map;
import java.util.Set;

public class InstanceCancelMonitorData {

    private long instanceId; //主资源id
    @Deprecated
    private Set<Long> children; //子资源id
    private Set<String> metricList; //主资源取消监控或告警的指标id集合
    private Map<Long,Set<String>> childrenMetrics; //子资源取消监控或告警的指标id集合

    public long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(long instanceId) {
        this.instanceId = instanceId;
    }

    public Set<Long> getChildren() {
        return children;
    }

    public void setChildren(Set<Long> children) {
        this.children = children;
    }

    public Set<String> getMetricList() {
        return metricList;
    }

    public void setMetricList(Set<String> metricList) {
        this.metricList = metricList;
    }

    public Map<Long, Set<String>> getChildrenMetrics() {
        return childrenMetrics;
    }

    public void setChildrenMetrics(Map<Long, Set<String>> childrenMetrics) {
        this.childrenMetrics = childrenMetrics;
    }

    @Override
    public String toString() {
        return "InstanceCancelMonitorData{" +
                "instanceId=" + instanceId +
                ", children=" + children +
                ", metricList=" + metricList +
                ", childrenMetrics=" + childrenMetrics +
                '}';
    }

    public boolean isEmpty() {
        return (children == null || children.isEmpty()) && (metricList == null || metricList.isEmpty()) && (childrenMetrics == null || childrenMetrics.isEmpty());
    }
}
