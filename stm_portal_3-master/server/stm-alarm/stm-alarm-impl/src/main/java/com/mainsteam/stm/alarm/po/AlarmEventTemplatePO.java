package com.mainsteam.stm.alarm.po;

public class AlarmEventTemplatePO {

    private String jsonContent; //模板内容
    private String uniqueKey; //模板唯一id

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

}
