package com.mainsteam.stm.camera.web.vo;

import java.io.Serializable;

import com.mainsteam.stm.portal.resource.web.vo.ResourceMonitorVo;

/**
 * 参照如下JSON
 * {"data":{"total":11,"totalRecord":11,"rows":
 * [{"id":"brightness","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"亮度","isTable":false,"status":"NORMAL","currentVal":"84.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"legibility","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"清晰度","isTable":false,"status":"NORMAL","currentVal":"99.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"screenFreezed","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"画面冻结","isTable":false,"status":"NORMAL","currentVal":"100.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"colourCast","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"画面偏色","isTable":false,"status":"NORMAL","currentVal":"100.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"lostSignal","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"信号缺失","isTable":false,"status":"NORMAL","currentVal":"100.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"sightChange","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"场景变换","isTable":false,"status":"SERIOUS","currentVal":"200.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"PTZSpOeed","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"PTZ速度","isTable":false,"status":"SERIOUS","currentVal":"200.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"keeput","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"人为遮挡","isTable":false,"status":"NORMAL","currentVal":"80.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"streakDisturb","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"条纹干扰","isTable":false,"status":"NORMAL","currentVal":"100.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"PTZDegree","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"云台控制失效","isTable":false,"status":"SERIOUS","currentVal":"200.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"},
 * {"id":"snowflakeDisturb","thresholds":"['',,100]","isAlarm":true,"unit":"","text":"雪花干扰","isTable":false,"status":"NORMAL","currentVal":"97.00","isCustomMetric":false,"lastCollTime":"2017-07-08 09:48:24","type":"PerformanceMetric"}]},"code":200}
 * @author Administrator
 *
 */

public class CameraMetricVo extends CameraResourceMonitorVo implements Serializable{
	
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
	
	private String cameraType;
	private String cameraAddress;
	private String cameraGroup;
	private String loginUser;
	private String loginPassword;
	private int cameraPort;

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

	public String getKeepOut() {
		return keepOut;
	}

	public void setKeepOut(String keepOut) {
		this.keepOut = keepOut;
	}

	public String getDignoseTime() {
		return dignoseTime;
	}

	public void setDignoseTime(String dignoseTime) {
		this.dignoseTime = dignoseTime;
	}

	public String getPtzSpeed() {
		return ptzSpeed;
	}

	public void setPtzSpeed(String ptzSpeed) {
		this.ptzSpeed = ptzSpeed;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	
	
	
	
	
	

}
