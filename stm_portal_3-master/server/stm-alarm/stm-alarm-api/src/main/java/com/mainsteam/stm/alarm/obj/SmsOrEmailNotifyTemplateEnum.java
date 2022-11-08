package com.mainsteam.stm.alarm.obj;

/**
 * Created by Xiaopf on 8/10/2016.
 */
public enum SmsOrEmailNotifyTemplateEnum {

    IP("sourceIP", "IP地址"), NAME("sourceName", "资源名称"), COLLECTION_TIME("collectionTime", "产生时间"), ALARM_STATE("level", "告警级别"),
    RESOURCE_TYPE("ext0", "资源类型"), ALARM_COLLECTION_SOURCE("sourceName", "告警来源"), ALARM_CONTENT("content", "告警内容");

    private String key;
    private String value;

    SmsOrEmailNotifyTemplateEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValue(String name) {
        for(SmsOrEmailNotifyTemplateEnum notifyTemplateEnum : SmsOrEmailNotifyTemplateEnum.values()) {
            if(notifyTemplateEnum.getKey().equals(name))
                return notifyTemplateEnum.value;
        }
        return null;
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

    public static void main(String[] args) {
        for(SmsOrEmailNotifyTemplateEnum templateEnum : SmsOrEmailNotifyTemplateEnum.values()) {
            System.out.println(templateEnum.getKey());
        }
    }

}
