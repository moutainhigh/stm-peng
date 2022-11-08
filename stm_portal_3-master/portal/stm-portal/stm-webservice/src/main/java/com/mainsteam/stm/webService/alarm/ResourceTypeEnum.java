package com.mainsteam.stm.webService.alarm;

/**
 * Created by Xiaopf on 2016/5/30.
 */
public enum ResourceTypeEnum {
    HOST("Host"), NETWORKDEVICE("NetworkDevice"), DATABASE("Database"), STANDARDSERVICE("StandardService"),
    WEBSERVER("WebServer"), MAILSERVER("MailServer"), DIRECTORY("Directory"),
    LOTUSDOMINO("LotusDomino"), SNMPOTHERS("SnmpOthers"), CACHESERVER("CacheServer");

    private String value;

    private ResourceTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
