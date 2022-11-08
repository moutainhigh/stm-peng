package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.metric.obj.CustomMetricCollectParameter;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.portal.resource.bo.PortalThreshold;

public class CustomMetricVo implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1603323732816146653L;

	private String id;
	private String name;
	//单位
	private String unit;
	//指标类型
	private String metricType;
	//告警
	private boolean alert;
	//监控
	private boolean monitor;
	//监控频度
	private String frequent;

	private List<PortalThreshold> thresholdsMap;
	
	private String warnThresholdDesc;
	
	private String seriousThresholdDesc;

	//发现方式:SNMP telnet/ssh
	private String discoverWay;
	// SNMP 的OID
	private String oid;
	// SNMP 的
	private String dataProcessWay;
	// telnet/ssh 的命了行
	private String command;
	
	//操作时间
	private String dateTime;
	
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
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMetricType() {
		return metricType;
	}
	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}
	public boolean isAlert() {
		return alert;
	}
	public void setAlert(boolean alert) {
		this.alert = alert;
	}
	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
	public String getFrequent() {
		return frequent;
	}
	public void setFrequent(String frequent) {
		this.frequent = frequent;
	}
	public String getDiscoverWay() {
		return discoverWay;
	}
	public void setDiscoverWay(String discoverWay) {
		this.discoverWay = discoverWay;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getDataProcessWay() {
		return dataProcessWay;
	}
	public void setDataProcessWay(String dataProcessWay) {
		this.dataProcessWay = dataProcessWay;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public List<PortalThreshold> getThresholdsMap() {
		return thresholdsMap;
	}
	public void setThresholdsMap(List<PortalThreshold> thresholdsMap) {
		this.thresholdsMap = thresholdsMap;
	}
	public String getWarnThresholdDesc() {
		return warnThresholdDesc;
	}
	public void setWarnThresholdDesc(String warnThresholdDesc) {
		this.warnThresholdDesc = warnThresholdDesc;
	}
	public String getSeriousThresholdDesc() {
		return seriousThresholdDesc;
	}
	public void setSeriousThresholdDesc(String seriousThresholdDesc) {
		this.seriousThresholdDesc = seriousThresholdDesc;
	}
	
	
	
}
