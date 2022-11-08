package com.mainsteam.stm.video.report.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频在线率
 * @author zhouwei
 *
 */
public class VedioOnlineRate {

	private String id;
	private String name;
	private int onlineQuality = 0;	//在线数量
	private int intactQuality = 0;		//完好数
	private int vedioSum = 0;		//总数
	private String onlineRate = "0.0%";	//在线率
	private String intactRate = "0.0%";	//完好率
	private String score = "0.0";		//得分
	private int sbnum = 0;	//上报数数量
	private int gisnum = 0;		//Gis完整数
	private int allNoraml=0;
	private int online=0;

	private String sbnumRate = "0.0%";	//接入率
	private String gisRate = "0.0%";	//GIS率
	private List<String> insList = new ArrayList<String>();	//组织下所有摄像头ID
	private List<String> orgList = new ArrayList<String>();	//所有子组织ID
	
	private int legibility;	//清晰度故障
	private int lostSignal;	//信号丢失
	private int brightness;	//亮度故障
	private int streakDisturb;	//条纹干扰
	private int snowflakeDisturb;	//雪花干扰
	private int colourCast;	//画面偏色
	private int PTZSpeed;	//云镜速度
	private int cloudPlatFormInvalid;	//云镜角度
	private int screenFreezed;	//画面冻结
	private int sightChange;	//场景变换
	private int keepOut;	//人为遮挡
	private int damagedEquipment;	//损坏的设备总数
	
	private long indicatorNumber;	//考核指标数
	private String accessRate;		//接入率
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOnlineQuality() {
		return onlineQuality;
	}
	public void setOnlineQuality(int onlineQuality) {
		this.onlineQuality = onlineQuality;
	}
	public int getVedioSum() {
		return vedioSum;
	}
	public void setVedioSum(int vedioSum) {
		this.vedioSum = vedioSum;
	}
	public String getOnlineRate() {
		return onlineRate;
	}
	public void setOnlineRate(String onlineRate) {
		this.onlineRate = onlineRate;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public List<String> getInsList() {
		return insList;
	}
	public void setInsList(List<String> insList) {
		this.insList = insList;
	}
	public int getIntactQuality() {
		return intactQuality;
	}
	public void setIntactQuality(int intactQuality) {
		this.intactQuality = intactQuality;
	}
	public String getIntactRate() {
		return intactRate;
	}
	public void setIntactRate(String intactRate) {
		this.intactRate = intactRate;
	}
	public List<String> getOrgList() {
		return orgList;
	}
	public void setOrgList(List<String> orgList) {
		this.orgList = orgList;
	}
	public int getLegibility() {
		return legibility;
	}
	public int getLostSignal() {
		return lostSignal;
	}
	public int getBrightness() {
		return brightness;
	}
	public int getStreakDisturb() {
		return streakDisturb;
	}
	public int getSnowflakeDisturb() {
		return snowflakeDisturb;
	}
	public int getColourCast() {
		return colourCast;
	}
	public int getPTZSpeed() {
		return PTZSpeed;
	}
	public int getCloudPlatFormInvalid() {
		return cloudPlatFormInvalid;
	}
	public int getScreenFreezed() {
		return screenFreezed;
	}
	public int getSightChange() {
		return sightChange;
	}
	public int getKeepOut() {
		return keepOut;
	}
	public void setLegibility(int legibility) {
		this.legibility = legibility;
	}
	public void setLostSignal(int lostSignal) {
		this.lostSignal = lostSignal;
	}
	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}
	public void setStreakDisturb(int streakDisturb) {
		this.streakDisturb = streakDisturb;
	}
	public void setSnowflakeDisturb(int snowflakeDisturb) {
		this.snowflakeDisturb = snowflakeDisturb;
	}
	public void setColourCast(int colourCast) {
		this.colourCast = colourCast;
	}
	public void setPTZSpeed(int pTZSpeed) {
		PTZSpeed = pTZSpeed;
	}
	public void setCloudPlatFormInvalid(int cloudPlatFormInvalid) {
		this.cloudPlatFormInvalid = cloudPlatFormInvalid;
	}
	public void setScreenFreezed(int screenFreezed) {
		this.screenFreezed = screenFreezed;
	}
	public void setSightChange(int sightChange) {
		this.sightChange = sightChange;
	}
	public void setKeepOut(int keepOut) {
		this.keepOut = keepOut;
	}
	public int getDamagedEquipment() {
		return damagedEquipment;
	}
	public void setDamagedEquipment(int damagedEquipment) {
		this.damagedEquipment = damagedEquipment;
	}
	public int getSbnum() {
		return sbnum;
	}
	public void setSbnum(int sbnum) {
		this.sbnum = sbnum;
	}
	public int getGisnum() {
		return gisnum;
	}
	public void setGisnum(int gisnum) {
		this.gisnum = gisnum;
	}
	public String getSbnumRate() {
		return sbnumRate;
	}
	public void setSbnumRate(String sbnumRate) {
		this.sbnumRate = sbnumRate;
	}
	public String getGisRate() {
		return gisRate;
	}
	public void setGisRate(String gisRate) {
		this.gisRate = gisRate;
	}
	public int getAllNoraml() {
		return allNoraml;
	}
	public void setAllNoraml(int allNoraml) {
		this.allNoraml = allNoraml;
	}
	public int getOnline() {
		return online;
	}
	public void setOnline(int online) {
		this.online = online;
	}
	public long getIndicatorNumber() {
		return indicatorNumber;
	}
	public void setIndicatorNumber(long indicatorNumber) {
		this.indicatorNumber = indicatorNumber;
	}
	public String getAccessRate() {
		return accessRate;
	}
	public void setAccessRate(String accessRate) {
		this.accessRate = accessRate;
	}
	
	
}
