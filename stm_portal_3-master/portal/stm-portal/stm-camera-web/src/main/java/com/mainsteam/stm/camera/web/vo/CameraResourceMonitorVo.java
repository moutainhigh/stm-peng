package com.mainsteam.stm.camera.web.vo;

import java.io.Serializable;

public class CameraResourceMonitorVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String sourceName;
	private String ipAddress;
	private String monitorType;
	private String cpuAvailability;
	private String cpuStatus;
	private boolean cpuIsAlarm;
	private String memoryAvailability;
	private String memoryStatus;
	private boolean memoryIsAlarm;
	private String responseTime;
	private String dcsGroupName;
	private String instanceState;
	private String instanceStatus;
	private String categoryId;
	private String resourceId;
	private String categoryIds;
	private String isCustomResGroup;
	private Long domainId;
	private String domainName;
	private String iPorName;
	private boolean hasRight;
	private String liablePerson;
	private String maintainStaus;
	private String maintainStartTime;
	private String maintainEndTime;
	private boolean hasTelSSHParams;
	
	private String onlineStatus;
	
	private String diagnoseResult;
	
	private String dignoseMetrics;
	
	
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
	
	private String alarmTips;//告警提醒
	
	private String cameraType;
	private String cameraAddress;
	private String cameraGroup;
	private String loginUser;
	private String loginPassword;
	private int cameraPort;
	
	private String gisx;//取到x坐标
   	
   	private String gisy;//取到x坐标
   	
   	
 	private String groupId;//分组ID

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(String monitorType) {
		this.monitorType = monitorType;
	}

	public String getCpuAvailability() {
		return cpuAvailability;
	}

	public void setCpuAvailability(String cpuAvailability) {
		this.cpuAvailability = cpuAvailability;
	}

	public String getCpuStatus() {
		return cpuStatus;
	}

	public void setCpuStatus(String cpuStatus) {
		this.cpuStatus = cpuStatus;
	}

	public boolean isCpuIsAlarm() {
		return cpuIsAlarm;
	}

	public void setCpuIsAlarm(boolean cpuIsAlarm) {
		this.cpuIsAlarm = cpuIsAlarm;
	}

	public String getMemoryAvailability() {
		return memoryAvailability;
	}

	public void setMemoryAvailability(String memoryAvailability) {
		this.memoryAvailability = memoryAvailability;
	}

	public String getMemoryStatus() {
		return memoryStatus;
	}

	public void setMemoryStatus(String memoryStatus) {
		this.memoryStatus = memoryStatus;
	}

	public boolean isMemoryIsAlarm() {
		return memoryIsAlarm;
	}

	public void setMemoryIsAlarm(boolean memoryIsAlarm) {
		this.memoryIsAlarm = memoryIsAlarm;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getDcsGroupName() {
		return dcsGroupName;
	}

	public void setDcsGroupName(String dcsGroupName) {
		this.dcsGroupName = dcsGroupName;
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

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getIsCustomResGroup() {
		return isCustomResGroup;
	}

	public void setIsCustomResGroup(String isCustomResGroup) {
		this.isCustomResGroup = isCustomResGroup;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getiPorName() {
		return iPorName;
	}

	public void setiPorName(String iPorName) {
		this.iPorName = iPorName;
	}

	public boolean isHasRight() {
		return hasRight;
	}

	public void setHasRight(boolean hasRight) {
		this.hasRight = hasRight;
	}

	public String getLiablePerson() {
		return liablePerson;
	}

	public void setLiablePerson(String liablePerson) {
		this.liablePerson = liablePerson;
	}

	public String getMaintainStaus() {
		return maintainStaus;
	}

	public void setMaintainStaus(String maintainStaus) {
		this.maintainStaus = maintainStaus;
	}

	public String getMaintainStartTime() {
		return maintainStartTime;
	}

	public void setMaintainStartTime(String maintainStartTime) {
		this.maintainStartTime = maintainStartTime;
	}

	public String getMaintainEndTime() {
		return maintainEndTime;
	}

	public void setMaintainEndTime(String maintainEndTime) {
		this.maintainEndTime = maintainEndTime;
	}

	public boolean isHasTelSSHParams() {
		return hasTelSSHParams;
	}

	public void setHasTelSSHParams(boolean hasTelSSHParams) {
		this.hasTelSSHParams = hasTelSSHParams;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getDiagnoseResult() {
		return diagnoseResult;
	}

	public void setDiagnoseResult(String diagnoseResult) {
		this.diagnoseResult = diagnoseResult;
	}

	public String getDignoseMetrics() {
		return dignoseMetrics;
	}

	public void setDignoseMetrics(String dignoseMetrics) {
		this.dignoseMetrics = dignoseMetrics;
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
		this.availability = availability;
	}

	public String getCameraType() {
		return cameraType;
	}

	public void setCameraType(String cameraType) {
		this.cameraType = cameraType;
	}

	public String getCameraAddress() {
		return cameraAddress;
	}

	public void setCameraAddress(String cameraAddress) {
		this.cameraAddress = cameraAddress;
	}

	public String getCameraGroup() {
		return cameraGroup;
	}

	public void setCameraGroup(String cameraGroup) {
		this.cameraGroup = cameraGroup;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public int getCameraPort() {
		return cameraPort;
	}

	public void setCameraPort(int cameraPort) {
		this.cameraPort = cameraPort;
	}

	public String getAlarmTips() {
		return alarmTips;
	}

	public void setAlarmTips(String alarmTips) {
		this.alarmTips = alarmTips;
	}

	public String getGisx() {
		return gisx;
	}

	public void setGisx(String gisx) {
		this.gisx = gisx;
	}

	public String getGisy() {
		return gisy;
	}

	public void setGisy(String gisy) {
		this.gisy = gisy;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	
	
	
	
	

}
