package com.mainsteam.stm.alarm.obj;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;

import java.util.Map;

public class AlarmEventTemplate {
    private Map<InstanceStateEnum,String> content; //模板内容
    private String uniqueKey; //模板唯一id
    private long profileId; //策略Id
    private long timelineId; //基线Id
    private String metricId; //指标Id
    private boolean isMainProfile; //是否为主策略
    private SysModuleEnum moduleType ;//模块类型

    public Map<InstanceStateEnum, String> getContent() {
        return content;
    }

    public void setContent(Map<InstanceStateEnum, String> content) {
        this.content = content;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public SysModuleEnum getModuleType() {
        return moduleType;
    }

    public void setModuleType(SysModuleEnum moduleType) {
        this.moduleType = moduleType;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public long getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(long timelineId) {
        this.timelineId = timelineId;
    }

    public boolean isMainProfile() {
        return isMainProfile;
    }

    public void setMainProfile(boolean mainProfile) {
        isMainProfile = mainProfile;
    }

    @Override
    public String toString() {
        return "AlarmEventTemplate{" +
                "content=" + content +
                ", uniqueKey='" + uniqueKey + '\'' +
                ", profileId=" + profileId +
                ", timelineId=" + timelineId +
                ", metricId='" + metricId + '\'' +
                ", isMainProfile=" + isMainProfile +
                ", moduleType=" + moduleType +
                '}';
    }
}
