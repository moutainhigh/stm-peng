package com.mainsteam.stm.camera.bo;

import java.util.List;

public class CameraVo {
    /**
     * 指标状态集合
     */
    private List<String> states;
    /**
     * 指标ID集合
     */
    private List<String> metricIds;
    /**
     * 指定排序指标ID
     */
    private String orderByMetricId;
    /**
     * 监控状态
     */
    private String liftState;
    /**
     * 在线状态集合
     */
    private List<String> availabilityStates;
    /**
     * 排序顺序
     */
    private String order;
    /**
     * 排序字段
     */
    private String orderMetric;
    /**
     *过滤字段
     */
    private String filtrateStr;
    /**
     * 根据诊断结果字段判断
     */
    private String statusFlag;
    /**
     * 根据在想状态字段判断离线数和故障数
     */
    private String onlineFlag;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getFiltrateStr() {
        return filtrateStr;
    }

    public void setFiltrateStr(String filtrateStr) {
        this.filtrateStr = filtrateStr;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public List<String> getMetricIds() {
        return metricIds;
    }

    public void setMetricIds(List<String> metricIds) {
        this.metricIds = metricIds;
    }

    public String getOrderByMetricId() {
        return orderByMetricId;
    }

    public void setOrderByMetricId(String orderByMetricId) {
        this.orderByMetricId = orderByMetricId;
    }

    public String getLiftState() {
        return liftState;
    }

    public void setLiftState(String liftState) {
        this.liftState = liftState;
    }

    public List<String> getAvailabilityStates() {
        return availabilityStates;
    }

    public void setAvailabilityStates(List<String> availabilityStates) {
        this.availabilityStates = availabilityStates;
    }

    public String getOrderMetric() {
        return orderMetric;
    }

    public void setOrderMetric(String orderMetric) {
        this.orderMetric = orderMetric;
    }

    public String getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }

    public String getOnlineFlag() {
        return onlineFlag;
    }

    public void setOnlineFlag(String onlineFlag) {
        this.onlineFlag = onlineFlag;
    }
}
