package com.mainsteam.stm.camera.bo;

import com.mainsteam.stm.util.StringUtil;

import java.io.Serializable;

public class CameraBo implements Serializable{

    private Long id;
    private String showName;
    private String devIP;
    private String instanceState;
    private String instanceStatus;
    /**
     * 指标状态集合
     */
    private String metricStatus;

    private String brightness;//亮度

    private String legibility;//清晰度

    private String screenFreezed;//画面冻结

    private String colourCast;//画面偏色

    private String lostSignal;//信号缺失

    private String sightChange;//场景变换

    private String ptzSpeed;//PTZ速度

    private String keepOut;//人为遮挡

    private String streakDisturb;//条纹干扰

    private String PTZDegree;//云台控制失效

    private String snowflakeDisturb;//雪花干扰

    private String dignoseTime;//诊断时间

    private String availability;//在线状态

    private String lastCollectTime;//最近的采集时间

    private String monitorType;//资源类型


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstanceState() {
        return instanceState;
    }

    public void setInstanceState(String instanceState) {
        this.instanceState = instanceState;
    }

    public String getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(String instanceStatus) {
        this.instanceStatus = instanceStatus;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getDevIP() {
        return devIP;
    }

    public void setDevIP(String devIP) {
        this.devIP = devIP;
    }

    public String getMetricStatus() {
        return metricStatus;
    }

    public void setMetricStatus(String metricStatus) {
        if(!StringUtil.isNull(metricStatus)){
            String[] split = metricStatus.split(",");
            for(String metric : split){
                String[] metricInfo = metric.split("-");
                String key = metricInfo[0];
                String value = metricInfo[1];
                switch (key){
                    case "PTZDegree" :
                        setPTZDegree(value);
                        break;
                    case "PTZSpeed" :
                        setPtzSpeed(value);
                        break;
                    case "brightness" :
                        setBrightness(value);
                        break;
                    case "colourCast" :
                        setColourCast(value);
                        break;
                    case "keepOut" :
                        setKeepOut(value);
                        break;
                    case "legibility" :
                        setLegibility(value);
                        break;
                    case "lostSignal" :
                        setLostSignal(value);
                        break;
                    case "screenFrozen" :
                        setScreenFreezed(value);
                        break;
                    case "sightChange" :
                        setSightChange(value);
                        break;
                    case "snowflakeDisturb" :
                        setSnowflakeDisturb(value);
                        break;
                    case "streakDisturb" :
                        setStreakDisturb(value);
                        break;
                }
            }
        }
        this.metricStatus = metricStatus;
    }

    public String getBrightness() {
        return brightness;
    }

    public void setBrightness(String brightness) {
        this.brightness = brightness;
    }

    public String getLegibility() {
        return legibility;
    }

    public void setLegibility(String legibility) {
        this.legibility = legibility;
    }

    public String getScreenFreezed() {
        return screenFreezed;
    }

    public void setScreenFreezed(String screenFreezed) {
        this.screenFreezed = screenFreezed;
    }

    public String getColourCast() {
        return colourCast;
    }

    public void setColourCast(String colourCast) {
        this.colourCast = colourCast;
    }

    public String getLostSignal() {
        return lostSignal;
    }

    public void setLostSignal(String lostSignal) {
        this.lostSignal = lostSignal;
    }

    public String getSightChange() {
        return sightChange;
    }

    public void setSightChange(String sightChange) {
        this.sightChange = sightChange;
    }

    public String getPtzSpeed() {
        return ptzSpeed;
    }

    public void setPtzSpeed(String ptzSpeed) {
        this.ptzSpeed = ptzSpeed;
    }

    public String getKeepOut() {
        return keepOut;
    }

    public void setKeepOut(String keepOut) {
        this.keepOut = keepOut;
    }

    public String getStreakDisturb() {
        return streakDisturb;
    }

    public void setStreakDisturb(String streakDisturb) {
        this.streakDisturb = streakDisturb;
    }

    public String getPTZDegree() {
        return PTZDegree;
    }

    public void setPTZDegree(String PTZDegree) {
        this.PTZDegree = PTZDegree;
    }

    public String getSnowflakeDisturb() {
        return snowflakeDisturb;
    }

    public void setSnowflakeDisturb(String snowflakeDisturb) {
        this.snowflakeDisturb = snowflakeDisturb;
    }

    public String getDignoseTime() {
        return dignoseTime;
    }

    public void setDignoseTime(String dignoseTime) {
        this.dignoseTime = dignoseTime;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.instanceState = availability;
        this.instanceStatus = this.getInstanceStateColor(availability);

        this.availability = availability;
    }

    public String getLastCollectTime() {
        return lastCollectTime;
    }

    public void setLastCollectTime(String lastCollectTime) {
        this.dignoseTime = lastCollectTime.substring(0, 19);

        this.lastCollectTime = lastCollectTime.substring(0, 19);
    }

    public String getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(String monitorType) {
        this.monitorType = monitorType;
    }

    private static String getInstanceStateColor(String  state) {
        String ise = null;
        switch (state) {
            case "CRITICAL":
                ise = "res_critical";
                break;
            case "SERIOUS":
                ise = "res_serious";
                break;
            case "WARN":
                ise = "res_warn";
                break;
            case "NORMAL":
            case "NORMAL_NOTHING":
                ise = "res_normal_nothing";
                break;
            default:
                ise = "res_normal_nothing";
        }
        return ise;
    }
}
