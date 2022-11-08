/**
 * 
 */
package com.mainsteam.stm.metric.obj;

import com.mainsteam.stm.caplib.dict.FrequentEnum;

/**
 * @author ziw
 * 
 */
public class CollectMeticSetting {
	
	private String metricId;
	
	private Boolean alert;

	private Boolean monitor;

	private FrequentEnum freq;

	private Integer flapping;

	/**
	 * 
	 */
	public CollectMeticSetting() {
	}


	/**
	 * @return the metricId
	 */
	public final String getMetricId() {
		return metricId;
	}



	/**
	 * @param metricId the metricId to set
	 */
	public final void setMetricId(String metricId) {
		this.metricId = metricId;
	}



	/**
	 * @return the alert
	 */
	public final Boolean getAlert() {
		return alert;
	}

	/**
	 * @param alert the alert to set
	 */
	public final void setAlert(Boolean alert) {
		this.alert = alert;
	}

	/**
	 * @return the monitor
	 */
	public final Boolean getMonitor() {
		return monitor;
	}

	/**
	 * @param monitor the monitor to set
	 */
	public final void setMonitor(Boolean monitor) {
		this.monitor = monitor;
	}

	/**
	 * @return the freq
	 */
	public final FrequentEnum getFreq() {
		return freq;
	}

	/**
	 * @param freq the freq to set
	 */
	public final void setFreq(FrequentEnum freq) {
		this.freq = freq;
	}

	/**
	 * @return the flapping
	 */
	public final Integer getFlapping() {
		return flapping;
	}

	/**
	 * @param flapping the flapping to set
	 */
	public final void setFlapping(Integer flapping) {
		this.flapping = flapping;
	}
}
