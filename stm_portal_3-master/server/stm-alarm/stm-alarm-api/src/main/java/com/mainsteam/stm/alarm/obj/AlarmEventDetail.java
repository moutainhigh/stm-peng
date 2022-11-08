package com.mainsteam.stm.alarm.obj;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;

import java.util.Date;

/**
 * 告警详情,主要用于保存告警详情页面
 */
public class AlarmEventDetail {

    private long eventId;

    private String content;

    private String recoveryKey;

    private InstanceStateEnum level;

    private Date collectionTime;

    private String snapshotResult;

    private SysModuleEnum sysModuleEnum;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecoveryKey() {
        return recoveryKey;
    }

    public void setRecoveryKey(String recoveryKey) {
        this.recoveryKey = recoveryKey;
    }

    public InstanceStateEnum getLevel() {
        return level;
    }

    public void setLevel(InstanceStateEnum level) {
        this.level = level;
    }

    public Date getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Date collectionTime) {
        this.collectionTime = collectionTime;
    }

    public String getSnapshotResult() {
        return snapshotResult;
    }

    public void setSnapshotResult(String snapshotResult) {
        this.snapshotResult = snapshotResult;
    }

    public SysModuleEnum getSysModuleEnum() {
        return sysModuleEnum;
    }

    public void setSysModuleEnum(SysModuleEnum sysModuleEnum) {
        this.sysModuleEnum = sysModuleEnum;
    }

    @Override
    public String toString() {
        return "AlarmEventDetail{" +
                "eventId=" + eventId +
                ", content='" + content + '\'' +
                ", recoveryKey='" + recoveryKey + '\'' +
                ", level=" + level +
                ", collectionTime=" + collectionTime +
                ", snapshotResult='" + snapshotResult + '\'' +
                ", sysModuleEnum=" + sysModuleEnum +
                '}';
    }
}
