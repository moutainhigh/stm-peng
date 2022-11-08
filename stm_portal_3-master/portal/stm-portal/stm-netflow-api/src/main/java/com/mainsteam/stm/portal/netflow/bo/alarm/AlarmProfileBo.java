package com.mainsteam.stm.portal.netflow.bo.alarm;

import java.util.List;

public class AlarmProfileBo {
	private int id;
	private String netflowAlarmConfigName ;////配置名称
	private String netflowAlarmDesc; //描述
	private String netflowAlarmObj; //告警对象 1.接口 2.接口分组 3.IP分组
	private String netflowAlarmThresholdFilterInOut; //1.流入 2.流出,3流入流出
	private String netflowAlarmThresholdFilterType; //过滤类型 1.容量 2.数据包  3.速度 4.使用率
	private String netflowAlarmProtocal;//协议id，对应协议表中的记录id
	private String netflowAlarmProtoPort;//端口  1000或1001-7777的形式
	private String netflowAlarmApp;//应用的id,对应应用表中的记录id
//	private int netflowAlarmNetwork;//网络 1.IP地址  2.IP范围
	private String netflowNetworkIpAddrStart;//开始IP
	private String netflowNetworkIpAddrEnd;//结束IP
	private List<String> netflowAlarmThresholdMinute;//阈值分钟
	private List<String> netflowAlarmThresholdCount;//阈值次数
	private List<String> netflowAlarmThresholdValue;//阈值
	private List<String> netflowAlarmFlowUnit; //阈值流量单位
	private List<String> ids; //接受页面传送过来的多个设备的id值
	private List<String> netflowAlarmThresholdLevel;//阈值告警级别
	private String profileId;//对应的profile表的主键值
	
	public String getNetflowAlarmConfigName() {
		return netflowAlarmConfigName;
	}
	public void setNetflowAlarmConfigName(String netflowAlarmConfigName) {
		this.netflowAlarmConfigName = netflowAlarmConfigName;
	}
	public String getNetflowAlarmDesc() {
		return netflowAlarmDesc;
	}
	public void setNetflowAlarmDesc(String netflowAlarmDesc) {
		this.netflowAlarmDesc = netflowAlarmDesc;
	}


	public String getNetflowAlarmProtoPort() {
		return netflowAlarmProtoPort;
	}
	public void setNetflowAlarmProtoPort(String netflowAlarmProtoPort) {
		this.netflowAlarmProtoPort = netflowAlarmProtoPort;
	}

	public String getNetflowNetworkIpAddrStart() {
		return netflowNetworkIpAddrStart;
	}
	public void setNetflowNetworkIpAddrStart(String netflowNetworkIpAddrStart) {
		this.netflowNetworkIpAddrStart = netflowNetworkIpAddrStart;
	}
	public String getNetflowNetworkIpAddrEnd() {
		return netflowNetworkIpAddrEnd;
	}
	public void setNetflowNetworkIpAddrEnd(String netflowNetworkIpAddrEnd) {
		this.netflowNetworkIpAddrEnd = netflowNetworkIpAddrEnd;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public String getNetflowAlarmObj() {
		return netflowAlarmObj;
	}
	public void setNetflowAlarmObj(String netflowAlarmObj) {
		this.netflowAlarmObj = netflowAlarmObj;
	}
	public String getNetflowAlarmThresholdFilterInOut() {
		return netflowAlarmThresholdFilterInOut;
	}
	public void setNetflowAlarmThresholdFilterInOut(
			String netflowAlarmThresholdFilterInOut) {
		this.netflowAlarmThresholdFilterInOut = netflowAlarmThresholdFilterInOut;
	}
	public String getNetflowAlarmThresholdFilterType() {
		return netflowAlarmThresholdFilterType;
	}
	public void setNetflowAlarmThresholdFilterType(
			String netflowAlarmThresholdFilterType) {
		this.netflowAlarmThresholdFilterType = netflowAlarmThresholdFilterType;
	}
	public String getNetflowAlarmProtocal() {
		return netflowAlarmProtocal;
	}
	public void setNetflowAlarmProtocal(String netflowAlarmProtocal) {
		this.netflowAlarmProtocal = netflowAlarmProtocal;
	}
	public String getNetflowAlarmApp() {
		return netflowAlarmApp;
	}
	public void setNetflowAlarmApp(String netflowAlarmApp) {
		this.netflowAlarmApp = netflowAlarmApp;
	}
	public List<String> getNetflowAlarmThresholdMinute() {
		return netflowAlarmThresholdMinute;
	}
	public void setNetflowAlarmThresholdMinute(
			List<String> netflowAlarmThresholdMinute) {
		this.netflowAlarmThresholdMinute = netflowAlarmThresholdMinute;
	}
	public List<String> getNetflowAlarmThresholdCount() {
		return netflowAlarmThresholdCount;
	}
	public void setNetflowAlarmThresholdCount(
			List<String> netflowAlarmThresholdCount) {
		this.netflowAlarmThresholdCount = netflowAlarmThresholdCount;
	}
	public List<String> getNetflowAlarmThresholdValue() {
		return netflowAlarmThresholdValue;
	}
	public void setNetflowAlarmThresholdValue(
			List<String> netflowAlarmThresholdValue) {
		this.netflowAlarmThresholdValue = netflowAlarmThresholdValue;
	}
	public List<String> getNetflowAlarmThresholdLevel() {
		return netflowAlarmThresholdLevel;
	}
	public void setNetflowAlarmThresholdLevel(
			List<String> netflowAlarmThresholdLevel) {
		this.netflowAlarmThresholdLevel = netflowAlarmThresholdLevel;
	}
	public List<String> getNetflowAlarmFlowUnit() {
		return netflowAlarmFlowUnit;
	}
	public void setNetflowAlarmFlowUnit(List<String> netflowAlarmFlowUnit) {
		this.netflowAlarmFlowUnit = netflowAlarmFlowUnit;
	}
	
}
