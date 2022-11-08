package com.mainsteam.stm.state.ext.process.bean;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

import java.io.Serializable;

/**
 * 用于保存该资源下面所有的告警状态
 */
public class CompareInstanceState implements Serializable, Comparable<CompareInstanceState>{

    private static final long serialVersionUID = 1650296279056252532L;
    private MetricStateEnum alarmState; //告警状态
    private String id; //告警状态唯一标识

    public CompareInstanceState() {}

    public CompareInstanceState(String id, MetricStateEnum alarmState) {
        this.alarmState = alarmState;
        this.id = id;
    }

    public MetricStateEnum getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(MetricStateEnum alarmState) {
        this.alarmState = alarmState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompareInstanceState that = (CompareInstanceState) o;

        if (alarmState != that.alarmState) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = alarmState != null ? alarmState.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompareInstanceState{" +
                "alarmState=" + alarmState +
                ", id='" + id + '\'' +
                '}';
    }
    @Override
    public int compareTo(CompareInstanceState o) {
        int p = this.alarmState.getStateVal();
        int f = o.alarmState.getStateVal();
        return p > f ? -1 : (p < f ? 1 : 0);
    }
}
