package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.util.DateUtil;

public class HighChartsDataBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5717372973856961772L;

	private String  minTimeType;
	private String[] xAxis;
	private String[] xAxisFull;
	private double[] dataDouble;
	private String[] dataDoubleStr;
	private String[] dataStrArr;
	private String metricUnitName;
	private Date dateStart;
	//与标准时区相差时间分钟数timezoneOffset
	private int timezoneOffset;
	private Map<String, List<Map<String,String>>> dataMap = new HashMap<String,List<Map<String,String>>>();
	
	
	public Map<String, List<Map<String, String>>> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<String, List<Map<String, String>>> dataMap) {
		this.dataMap = dataMap;
	}
	public int getTimezoneOffset() {
		return timezoneOffset;
	}
	public void setTimezoneOffset(int timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
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
	public Date getDateStart() {
		return dateStart;
	}
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
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
	public String[] getxAxis() {
		return xAxis;
	}
	public void setxAxis(String[] xAxis) {
		this.xAxis = xAxis;
	}
	public double[] getDataDouble() {
		return dataDouble;
	}
	public void setDataDouble(double[] dataDouble) {
		this.dataDouble = dataDouble;
	}
	public String getMetricUnitName() {
		return metricUnitName;
	}
	public void setMetricUnitName(String metricUnitName) {
		this.metricUnitName = metricUnitName;
	}
	
	
}
