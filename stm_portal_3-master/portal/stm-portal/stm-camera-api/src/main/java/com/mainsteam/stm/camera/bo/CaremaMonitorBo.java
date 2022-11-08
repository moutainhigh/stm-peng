package com.mainsteam.stm.camera.bo;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;

public class CaremaMonitorBo  implements Serializable{
	
	private static final long serialVersionUID = 8261111498483850117L;
    private String resourceId;
   private String resourceName;
	  private Long instanceId;
	   private String categoryId;
	  private String showName;
	   private Long domainId;
	   private InstanceLifeStateEnum lifeState;
	   private DiscoverWayEnum discoverWay;
	 private String instanceStatus;
	   private String ip;
	   private boolean hasRight;
	  private boolean hasTelSSHParams;
	  private String cpuAvailability;
  private String cpuStatus;
	  private boolean cpuIsAlarm;
	   private String memoryAvailability;
	  private String memoryStatus;
	  private boolean memoryIsAlarm;
	  private String responseTime;
       private String dcsGroupName;
       
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
   	
   	private String gisx;//取到x坐标
   	
   	private String gisy;//取到x坐标
   	
   	private String groupId;//分组ID
   	
   	private String lastCollectTime;//最近的采集时间
   	
   	private String monitorType;//资源类型

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public InstanceLifeStateEnum getLifeState() {
		return lifeState;
	}

	public void setLifeState(InstanceLifeStateEnum lifeState) {
		this.lifeState = lifeState;
	}

	public DiscoverWayEnum getDiscoverWay() {
		return discoverWay;
	}

	public void setDiscoverWay(DiscoverWayEnum discoverWay) {
		this.discoverWay = discoverWay;
	}

	public String getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean getHasRight() {
		return hasRight;
	}

	public void setHasRight(boolean hasRight) {
		this.hasRight = hasRight;
	}
	
	

	public String getLastCollectTime() {
		return lastCollectTime;
	}

	public void setLastCollectTime(String lastCollectTime) {
		this.lastCollectTime = lastCollectTime;
	}

	public boolean getHasTelSSHParams() {
		return hasTelSSHParams;
	}

	public void setHasTelSSHParams(boolean hasTelSSHParams) {
		this.hasTelSSHParams = hasTelSSHParams;
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

	public boolean getCpuIsAlarm() {
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

	public boolean getMemoryIsAlarm() {
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
	
	
	
	
	

	public String getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(String monitorType) {
		this.monitorType = monitorType;
	}

	public boolean equals(Object arg0)
	   {
	    return (((CaremaMonitorBo)arg0).getInstanceId().longValue() == getInstanceId().longValue());
	}
	
	   public int hashCode() {
	  int prime = 31;
	     int result = 1;
	      result = 31 * result + ((this.instanceId == null) ? 0 : this.instanceId.hashCode());
	    return result;
	 }
	   

}
