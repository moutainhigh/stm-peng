package com.mainsteam.stm.webService.alarm;

/**
 * Created by Xiaopf on 2016/5/30.
 */
public enum DeviceTypeEnum {
    PING("Ping"), URL("URL"), PORT("Port"), FTP("FTP"), DNS("DNS"), POP3("POP3"), SMTP("SMTP"), PROCESS("Process");

    private String value;

    private DeviceTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
