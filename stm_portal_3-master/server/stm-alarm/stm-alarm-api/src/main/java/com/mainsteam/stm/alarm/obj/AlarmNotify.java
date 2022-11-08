package com.mainsteam.stm.alarm.obj;

import java.util.Date;
import java.util.Map;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;

/**
 * @author cx
 *
 */
public class AlarmNotify {
	private Long notifyID;
	private String sourceType;
	private long alarmID;
	private NotifyState state;
	private InstanceStateEnum level;
	private long notifyUserID;
	private SendWayEnum notifyType;
	private Date notifyTime;
	private String notifyAddr;
	private String notifyTitle;
	private String notifyContent;
	private long alarmRuleID;
	
	/**持续次数 */
	private int continusNum;
	//不做存储
	private String recoverKey;
	private String extInfo;
	private NotifyWaitType notifyWaitType;
	@Deprecated
	private NotifyCondition notifyConditon;

	private AlarmSendCondition alarmSendCondition;

	/**
	 * 告警模板的参数替换，在循环告警中需要用到
	 */
	private Map<String, Object> parameters;
	private SysModuleEnum sysModuleEnum;
	//剩余发送次数
	private int remainSendTimes;


	
	public Long getNotifyID() {
		return notifyID;
	}
	public void setNotifyID(Long notifyID) {
		this.notifyID = notifyID;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public long getAlarmID() {
		return alarmID;
	}
	public void setAlarmID(long alarmID) {
		this.alarmID = alarmID;
	}
	public long getNotifyUserID() {
		return notifyUserID;
	}
	public void setNotifyUserID(long notifyUserID) {
		this.notifyUserID = notifyUserID;
	}
	public SendWayEnum getNotifyType() {
		return notifyType;
	}
	public void setNotifyType(SendWayEnum notifyType) {
		this.notifyType = notifyType;
	}
	public Date getNotifyTime() {
		return notifyTime;
	}
	public void setNotifyTime(Date notifyTime) {
		this.notifyTime = notifyTime;
	}
	public String getNotifyAddr() {
		return notifyAddr;
	}
	public void setNotifyAddr(String notifyAddr) {
		this.notifyAddr = notifyAddr;
	}
	public String getNotifyContent() {
		return notifyContent;
	}
	public void setNotifyContent(String notifyContent) {
		this.notifyContent = notifyContent;
	}
	public String getNotifyTitle() {
		return notifyTitle;
	}
	public void setNotifyTitle(String notifyTitle) {
		this.notifyTitle = notifyTitle;
	}
	public NotifyState getState() {
		return state;
	}
	public void setState(NotifyState state) {
		this.state = state;
	}
	public void setAlarmRuleID(long id) {
		this.alarmRuleID=id; 
	}
	public long getAlarmRuleID(){
		return this.alarmRuleID;
	}
	public String getRecoverKey() {
		return recoverKey;
	}
	public void setRecoverKey(String recoverKey) {
		this.recoverKey = recoverKey;
	}
	public int getContinusNum() {
		return continusNum;
	}
	public void setContinusNum(int continueNum) {
		this.continusNum = continueNum;
	}
	public InstanceStateEnum getLevel() {
		return level;
	}
	public void setLevel(InstanceStateEnum level) {
		this.level = level;
	}
	public String getExtInfo() {
		return extInfo;
	}
	public void setExtInfo(String extInfo) {
		this.extInfo = extInfo;
	}

	public NotifyWaitType getNotifyWaitType() {
		return notifyWaitType;
	}

	public void setNotifyWaitType(NotifyWaitType notifyWaitType) {
		this.notifyWaitType = notifyWaitType;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public SysModuleEnum getSysModuleEnum() {
		return sysModuleEnum;
	}

	public void setSysModuleEnum(SysModuleEnum sysModuleEnum) {
		this.sysModuleEnum = sysModuleEnum;
	}

	public int getRemainSendTimes() {
		return remainSendTimes;
	}

	public void setRemainSendTimes(int remainSendTimes) {
		this.remainSendTimes = remainSendTimes;
	}

	public AlarmSendCondition getAlarmSendCondition() {
		return alarmSendCondition;
	}

	public void setAlarmSendCondition(AlarmSendCondition alarmSendCondition) {
		this.alarmSendCondition = alarmSendCondition;
	}
}
