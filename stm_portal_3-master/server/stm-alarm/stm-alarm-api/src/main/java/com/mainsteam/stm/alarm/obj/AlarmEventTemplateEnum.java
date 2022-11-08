package com.mainsteam.stm.alarm.obj;

/**
 * 告警模板参数枚举，
 * 定义的顺序必须保持一致，不能修改
 */
public enum AlarmEventTemplateEnum {
    RESOURCE_NAME("resourceName","资源名称","告警资源名称"),
    RESOURCE_TYPE("resourceType","资源类型","资源类型"),
    METRIC_NAME("metricName", "指标名称", "指标名称"),
    METRIC_VALUE("metricValue","采集值","本轮采集值"),
    //THRESHOLD("threshold","阈值","告警阈值"),
    ALARM_TIME("alarmTime","告警时间","告警产生的时间"),
    ALARM_LEVEL("alarmLevel","告警级别","告警级别"),
    PRE_METRIC_VALUE("preMetricValue", "上次采集值", "上次采集值");

    private String key;
    private String value;
    private String description;

    AlarmEventTemplateEnum(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String getKeyFromValue(String value) {
        for (AlarmEventTemplateEnum template:
             AlarmEventTemplateEnum.values()) {
            if(template.value.equals(value)){
                return template.key;
            }
        }
        return null;
    }

}
