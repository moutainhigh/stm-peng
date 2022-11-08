package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

public class AlarmRuleContentVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -829437988086801592L;

	private String isSmsAlarm_SMS;
	private String[] alarmLevel_SMS;
	private String sendTimeSet_SMS;
	private int sendTimeNum_SMS;
	private String sendTimeType_SMS;
	
	private String isSmsAlarm_EMAIL;
	private String[] alarmLevel_EMAIL;
	private String sendTimeSet_EMAIL;
	private int sendTimeNum_EMAIL;
	private String sendTimeType_EMAIL;
	
	private String isSmsAlarm_ALERT;
	private String[] alarmLevel_ALERT;
	private String sendTimeSet_ALERT;
	private int sendTimeNum_ALERT;
	private String sendTimeType_ALERT;
	
	private String[] receiveUser;
	private String profileType;
	private Long profileTypeId;
	
	//allTime   everyDay  everyWeek
	private String alarmSendTimeTypeSms;
	private String alarmSendTimeTypeEmail;
	private String alarmSendTimeTypeAlert;
	//是否发送非告警时间告警
	private String ifSendUnalarmTimeAlarmSms;
	private String ifSendUnalarmTimeAlarmEmail;
	private String ifSendUnalarmTimeAlarmAlert;
	//周几-开始时间-结束时间          0表示每天          1-120-180(周一的2-3点)
	private String[] alarmForDayDataSms;
	private String[] alarmForWeekDataSms;
	private String[] alarmForDayDataEmail;
	private String[] alarmForWeekDataEmail;
	private String[] alarmForDayDataAlert;
	private String[] alarmForWeekDataAlert;
//模板id
	private long  templateIdSms;
	private long  templateIdEmail;
	private long  templateIdAlert;
	//短信告警最多发送次数 dfw 20170104
	private int sendTimes_SMS;
	//邮件告警最多发送次数 dfw 20170104
	private int sendTimes_EMAIL;
	
	
	public String getIfSendUnalarmTimeAlarmSms() {
		return ifSendUnalarmTimeAlarmSms;
	}

	public void setIfSendUnalarmTimeAlarmSms(String ifSendUnalarmTimeAlarmSms) {
		this.ifSendUnalarmTimeAlarmSms = ifSendUnalarmTimeAlarmSms;
	}

	public String getIfSendUnalarmTimeAlarmEmail() {
		return ifSendUnalarmTimeAlarmEmail;
	}

	public void setIfSendUnalarmTimeAlarmEmail(String ifSendUnalarmTimeAlarmEmail) {
		this.ifSendUnalarmTimeAlarmEmail = ifSendUnalarmTimeAlarmEmail;
	}

	public String getIfSendUnalarmTimeAlarmAlert() {
		return ifSendUnalarmTimeAlarmAlert;
	}

	public void setIfSendUnalarmTimeAlarmAlert(String ifSendUnalarmTimeAlarmAlert) {
		this.ifSendUnalarmTimeAlarmAlert = ifSendUnalarmTimeAlarmAlert;
	}

	public String getAlarmSendTimeTypeSms() {
		return alarmSendTimeTypeSms;
	}

	public void setAlarmSendTimeTypeSms(String alarmSendTimeTypeSms) {
		this.alarmSendTimeTypeSms = alarmSendTimeTypeSms;
	}

	public String getAlarmSendTimeTypeEmail() {
		return alarmSendTimeTypeEmail;
	}

	public void setAlarmSendTimeTypeEmail(String alarmSendTimeTypeEmail) {
		this.alarmSendTimeTypeEmail = alarmSendTimeTypeEmail;
	}

	public String getAlarmSendTimeTypeAlert() {
		return alarmSendTimeTypeAlert;
	}

	public void setAlarmSendTimeTypeAlert(String alarmSendTimeTypeAlert) {
		this.alarmSendTimeTypeAlert = alarmSendTimeTypeAlert;
	}

	public String[] getAlarmForWeekDataSms() {
		return alarmForWeekDataSms;
	}

	public void setAlarmForWeekDataSms(String[] alarmForWeekDataSms) {
		this.alarmForWeekDataSms = alarmForWeekDataSms;
	}

	public String[] getAlarmForWeekDataEmail() {
		return alarmForWeekDataEmail;
	}

	public void setAlarmForWeekDataEmail(String[] alarmForWeekDataEmail) {
		this.alarmForWeekDataEmail = alarmForWeekDataEmail;
	}
	
	public String[] getAlarmForDayDataSms() {
		return alarmForDayDataSms;
	}

	public void setAlarmForDayDataSms(String[] alarmForDayDataSms) {
		this.alarmForDayDataSms = alarmForDayDataSms;
	}

	public String[] getAlarmForDayDataEmail() {
		return alarmForDayDataEmail;
	}

	public void setAlarmForDayDataEmail(String[] alarmForDayDataEmail) {
		this.alarmForDayDataEmail = alarmForDayDataEmail;
	}

	public String[] getAlarmForDayDataAlert() {
		return alarmForDayDataAlert;
	}

	public void setAlarmForDayDataAlert(String[] alarmForDayDataAlert) {
		this.alarmForDayDataAlert = alarmForDayDataAlert;
	}

	public String[] getAlarmForWeekDataAlert() {
		return alarmForWeekDataAlert;
	}

	public void setAlarmForWeekDataAlert(String[] alarmForWeekDataAlert) {
		this.alarmForWeekDataAlert = alarmForWeekDataAlert;
	}

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	public Long getProfileTypeId() {
		return profileTypeId;
	}

	public void setProfileTypeId(Long profileTypeId) {
		this.profileTypeId = profileTypeId;
	}

	public String getIsSmsAlarm_SMS() {
		return isSmsAlarm_SMS;
	}

	public void setIsSmsAlarm_SMS(String isSmsAlarm_SMS) {
		this.isSmsAlarm_SMS = isSmsAlarm_SMS;
	}

	public String[] getAlarmLevel_SMS() {
		return alarmLevel_SMS;
	}

	public void setAlarmLevel_SMS(String[] alarmLevel_SMS) {
		this.alarmLevel_SMS = alarmLevel_SMS;
	}

	public String getSendTimeSet_SMS() {
		return sendTimeSet_SMS;
	}

	public void setSendTimeSet_SMS(String sendTimeSet_SMS) {
		this.sendTimeSet_SMS = sendTimeSet_SMS;
	}

	public int getSendTimeNum_SMS() {
		return sendTimeNum_SMS;
	}

	public void setSendTimeNum_SMS(int sendTimeNum_SMS) {
		this.sendTimeNum_SMS = sendTimeNum_SMS;
	}

	public String getSendTimeType_SMS() {
		return sendTimeType_SMS;
	}

	public void setSendTimeType_SMS(String sendTimeType_SMS) {
		this.sendTimeType_SMS = sendTimeType_SMS;
	}

	public String getIsSmsAlarm_EMAIL() {
		return isSmsAlarm_EMAIL;
	}

	public void setIsSmsAlarm_EMAIL(String isSmsAlarm_EMAIL) {
		this.isSmsAlarm_EMAIL = isSmsAlarm_EMAIL;
	}

	public String[] getAlarmLevel_EMAIL() {
		return alarmLevel_EMAIL;
	}

	public void setAlarmLevel_EMAIL(String[] alarmLevel_EMAIL) {
		this.alarmLevel_EMAIL = alarmLevel_EMAIL;
	}

	public String getSendTimeSet_EMAIL() {
		return sendTimeSet_EMAIL;
	}

	public void setSendTimeSet_EMAIL(String sendTimeSet_EMAIL) {
		this.sendTimeSet_EMAIL = sendTimeSet_EMAIL;
	}

	public int getSendTimeNum_EMAIL() {
		return sendTimeNum_EMAIL;
	}

	public void setSendTimeNum_EMAIL(int sendTimeNum_EMAIL) {
		this.sendTimeNum_EMAIL = sendTimeNum_EMAIL;
	}

	public String getSendTimeType_EMAIL() {
		return sendTimeType_EMAIL;
	}

	public void setSendTimeType_EMAIL(String sendTimeType_EMAIL) {
		this.sendTimeType_EMAIL = sendTimeType_EMAIL;
	}

	public String getIsSmsAlarm_ALERT() {
		return isSmsAlarm_ALERT;
	}

	public void setIsSmsAlarm_ALERT(String isSmsAlarm_ALERT) {
		this.isSmsAlarm_ALERT = isSmsAlarm_ALERT;
	}

	public String[] getAlarmLevel_ALERT() {
		return alarmLevel_ALERT;
	}

	public void setAlarmLevel_ALERT(String[] alarmLevel_ALERT) {
		this.alarmLevel_ALERT = alarmLevel_ALERT;
	}

	public String getSendTimeSet_ALERT() {
		return sendTimeSet_ALERT;
	}

	public void setSendTimeSet_ALERT(String sendTimeSet_ALERT) {
		this.sendTimeSet_ALERT = sendTimeSet_ALERT;
	}

	public int getSendTimeNum_ALERT() {
		return sendTimeNum_ALERT;
	}

	public void setSendTimeNum_ALERT(int sendTimeNum_ALERT) {
		this.sendTimeNum_ALERT = sendTimeNum_ALERT;
	}

	public String getSendTimeType_ALERT() {
		return sendTimeType_ALERT;
	}

	public void setSendTimeType_ALERT(String sendTimeType_ALERT) {
		this.sendTimeType_ALERT = sendTimeType_ALERT;
	}

	public String[] getReceiveUser() {
		return receiveUser;
	}

	public void setReceiveUser(String[] receiveUser) {
		this.receiveUser = receiveUser;
	}

	public long getTemplateIdSms() {
		return templateIdSms;
	}

	public void setTemplateIdSms(long templateIdSms) {
		this.templateIdSms = templateIdSms;
	}

	public long getTemplateIdEmail() {
		return templateIdEmail;
	}

	public void setTemplateIdEmail(long templateIdEmail) {
		this.templateIdEmail = templateIdEmail;
	}

	public long getTemplateIdAlert() {
		return templateIdAlert;
	}

	public void setTemplateIdAlert(long templateIdAlert) {
		this.templateIdAlert = templateIdAlert;
	}

	public int getSendTimes_SMS() {
		return sendTimes_SMS;
	}

	public void setSendTimes_SMS(int sendTimes_SMS) {
		this.sendTimes_SMS = sendTimes_SMS;
	}

	public int getSendTimes_EMAIL() {
		return sendTimes_EMAIL;
	}

	public void setSendTimes_EMAIL(int sendTimes_EMAIL) {
		this.sendTimes_EMAIL = sendTimes_EMAIL;
	}
}
