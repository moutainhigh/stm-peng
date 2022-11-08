package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

public class HighChartsDataVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1873434168269937415L;

	private long instanceId;
	private String[] metricId;
	private String[] xAxis;
	private String[] xAxisFull;
	private double[] dataDouble;
	private String[] dataStrArr;
	private String queryTimeType;
	private int highChartsQueryNum;
	private String metricUnitName;
	private String  minTimeType;
	private String[] dataDoubleStr;
	private boolean ifSpecialMetricChart;
	private String metricDataType;
	private Map<String, List<Map<String,String>>> dataMap;
	
	//randomTime
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")  
	private Date dateStart;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")  
	private Date dateEnd;
	
	
	public Map<String, List<Map<String, String>>> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<String, List<Map<String, String>>> dataMap) {
		this.dataMap = dataMap;
	}
	public boolean isIfSpecialMetricChart() {
		return ifSpecialMetricChart;
	}
	public void setIfSpecialMetricChart(boolean ifSpecialMetricChart) {
		this.ifSpecialMetricChart = ifSpecialMetricChart;
	}
	public String getMetricDataType() {
		return metricDataType;
	}
	public void setMetricDataType(String metricDataType) {
		this.metricDataType = metricDataType;
	}
	public String[] getDataDoubleStr() {
		return dataDoubleStr;
	}
	public void setDataDoubleStr(String[] dataDoubleStr) {
		this.dataDoubleStr = dataDoubleStr;
	}
	public String getMinTimeType() {
		return minTimeType;
	}
	public void setMinTimeType(String minTimeType) {
		this.minTimeType = minTimeType;
	}
	public String[] getxAxisFull() {
		return xAxisFull;
	}
	public void setxAxisFull(String[] xAxisFull) {
		this.xAxisFull = xAxisFull;
	}
	public String[] getDataStrArr() {
		return dataStrArr;
	}
	public void setDataStrArr(String[] dataStrArr) {
		this.dataStrArr = dataStrArr;
	}
	public Date getDateStart() {
		return dateStart;
	}
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}
	public Date getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	public int getHighChartsQueryNum() {
		return highChartsQueryNum;
	}
	public void setHighChartsQueryNum(int highChartsQueryNum) {
		this.highChartsQueryNum = highChartsQueryNum;
	}
	public String getMetricUnitName() {
		return metricUnitName;
	}
	public void setMetricUnitName(String metricUnitName) {
		this.metricUnitName = metricUnitName;
	}
	public String[] getMetricId() {
		return metricId;
	}
	public void setMetricId(String[] metricId) {
		this.metricId = metricId;
	}
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public double[] getDataDouble() {
		return dataDouble;
	}
	public void setDataDouble(double[] dataDouble) {
		this.dataDouble = dataDouble;
	}
	public String[] getxAxis() {
		return xAxis;
	}
	public void setxAxis(String[] xAxis) {
		this.xAxis = xAxis;
	}
	public String getQueryTimeType() {
		return queryTimeType;
	}
	public void setQueryTimeType(String queryTimeType) {
		this.queryTimeType = queryTimeType;
	}
}
