package com.mainsteam.stm.topo.bo;

import java.util.List;
import java.util.Map;

/*
 * 基准表 对应stm_topo_mac_base表
 */
public class MacBaseBo {
    private Long id;
    private String mac;
    private String ip;
    //上联设备名称
    private String upDeviceName;
    //上联设备ip
    private String upDeviceIp;
    /**
     * 上联备注
     */
    private String upRemarks;
    //上联设备接口
    private String upDeviceInterface;
    //主机名
    private String hostName;

    /*扩展属性-用于查询*/
    private Long searchType;
    private String searchVal;
    private int positionFlag = 0;    //页面定位图标显示（0：不显示，1：显示）

    //告警级别（严重、致命、警告）
    private List<Map<String, Object>> alarms;

    public int getPositionFlag() {
        return positionFlag;
    }

    public void setPositionFlag(int positionFlag) {
        this.positionFlag = positionFlag;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUpDeviceName() {
        return upDeviceName;
    }

    public void setUpDeviceName(String upDeviceName) {
        this.upDeviceName = upDeviceName;
    }

    public String getUpDeviceIp() {
        return upDeviceIp;
    }

    public void setUpDeviceIp(String upDeviceIp) {
        this.upDeviceIp = upDeviceIp;
    }

    public String getUpDeviceInterface() {
        return upDeviceInterface;
    }

    public void setUpDeviceInterface(String upDeviceInterface) {
        this.upDeviceInterface = upDeviceInterface;
    }

    public Long getSearchType() {
        return searchType;
    }

    public void setSearchType(Long searchType) {
        this.searchType = searchType;
    }

    public String getSearchVal() {
        return searchVal;
    }

    public void setSearchVal(String searchVal) {
        this.searchVal = searchVal;
    }

    public List<Map<String, Object>> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Map<String, Object>> alarms) {
        this.alarms = alarms;
    }

    public String getUpRemarks() {
        return upRemarks;
    }

    public void setUpRemarks(String upRemarks) {
        this.upRemarks = upRemarks;
    }
}
