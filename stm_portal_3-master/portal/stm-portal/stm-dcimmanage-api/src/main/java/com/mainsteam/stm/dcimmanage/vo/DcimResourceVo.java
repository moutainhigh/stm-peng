package com.mainsteam.stm.dcimmanage.vo;

public class DcimResourceVo {

    private String id;

    private String name;

    private String ip;

    private String type;

    private String resourceId;

    private Long stmId;

    private String showName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getStmId() {
        return stmId;
    }

    public void setStmId(Long stmId) {
        this.stmId = stmId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
