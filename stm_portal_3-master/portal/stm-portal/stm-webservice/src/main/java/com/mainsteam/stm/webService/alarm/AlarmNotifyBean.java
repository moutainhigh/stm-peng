package com.mainsteam.stm.webService.alarm;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;

public class AlarmNotifyBean {

	private long profileId;

	private String sourceId;

	private String sourceName;

	private String subfix;

	private String defaultMsgTitle;

	private String defaultMsg;

	private Date generateTime;

	private List<AlarmRule> notifyRules;

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSubfix() {
		return subfix;
	}

	public void setSubfix(String subfix) {
		this.subfix = subfix;
	}

	public String getDefaultMsgTitle() {
		return defaultMsgTitle;
	}

	public void setDefaultMsgTitle(String defaultMsgTitle) {
		this.defaultMsgTitle = defaultMsgTitle;
	}

	public String getDefaultMsg() {
		return defaultMsg;
	}

	public void setDefaultMsg(String defaultMsg) {
		this.defaultMsg = defaultMsg;
	}

	public Date getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(Date generateTime) {
		this.generateTime = generateTime;
	}

	public List<AlarmRule> getNotifyRules() {
		return notifyRules;
	}

	public void setNotifyRules(List<AlarmRule> notifyRules) {
		this.notifyRules = notifyRules;
	}

}
