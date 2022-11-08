package com.mainsteam.stm.caplib.resource;

import com.mainsteam.stm.caplib.dict.FrequentEnum;

public class ResourceMetricSetting implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1456345374331924752L;
	private boolean defaultEditable;
	private boolean defaultMonitor;
	private boolean defaultAlert;
	private int defaultFlapping;
	private FrequentEnum defaultMonitorFreq;
	private FrequentEnum[] supportMonitorFreqs;

	/**
	 * 是否默认编辑
	 * 
	 * @return
	 */
	public boolean isDefaultEditable() {
		return defaultEditable;
	}

	public void setDefaultEditable(boolean defaultEditable) {
		this.defaultEditable = defaultEditable;
	}

	/**
	 * 是否默认监控
	 * 
	 * @return
	 */
	public boolean isDefaultMonitor() {
		return defaultMonitor;
	}

	public void setDefaultMonitor(boolean defaultMonitor) {
		this.defaultMonitor = defaultMonitor;
	}

	/**
	 * 是否默认Alert
	 * 
	 * @return
	 */
	public boolean isDefaultAlert() {
		return defaultAlert;
	}

	public void setDefaultAlert(boolean defaultAlert) {
		this.defaultAlert = defaultAlert;
	}

	public void setDefaultFlapping(int defaultFlapping) {
		this.defaultFlapping = defaultFlapping;
	}

	public int getDefaultFlapping() {
		return this.defaultFlapping;
	}

	/**
	 * 获取默认监控频率
	 * 
	 * @return
	 */
	public FrequentEnum getDefaultMonitorFreq() {
		return defaultMonitorFreq;
	}

	public void setDefaultMonitorFreq(FrequentEnum defaultMonitorFreq) {
		this.defaultMonitorFreq = defaultMonitorFreq;
	}

	/**
	 * 获取support监控频率
	 * 
	 * @return
	 */
	public FrequentEnum[] getSupportMonitorFreqs() {
		return supportMonitorFreqs;
	}

	public void setSupportMonitorFreqs(FrequentEnum[] supportMonitorFreqs) {
		this.supportMonitorFreqs = supportMonitorFreqs;
	}

}
