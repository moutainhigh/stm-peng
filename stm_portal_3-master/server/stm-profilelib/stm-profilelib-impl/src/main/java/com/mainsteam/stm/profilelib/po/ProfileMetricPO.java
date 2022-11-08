package com.mainsteam.stm.profilelib.po;

public class ProfileMetricPO {

	private long mkId;
	private long profileId;
	private String metricId;
	private String dictFrequencyId;
	private String isUse;
	private long timelineId;
	String isAlarm;
	
	int alarmRepeat;
	
	public ProfileMetricPO(){
	}

	public long getMkId() {
		return mkId;
	}

	public void setMkId(long mkId) {
		this.mkId = mkId;
	}

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getDictFrequencyId() {
		return dictFrequencyId;
	}

	public void setDictFrequencyId(String dictFrequencyId) {
		this.dictFrequencyId = dictFrequencyId;
	}

	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public long getTimelineId() {
		return timelineId;
	}

	public String getIsAlarm() {
		return isAlarm;
	}

	

	public int getAlarmRepeat() {
		return alarmRepeat;
	}

	public void setTimelineId(long timelineId) {
		this.timelineId = timelineId;
	}

	public void setIsAlarm(String isAlarm) {
		this.isAlarm = isAlarm;
	}

	public void setAlarmRepeat(int alarmRepeat) {
		this.alarmRepeat = alarmRepeat;
	}

}
