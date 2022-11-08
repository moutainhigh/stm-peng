/**
 * 
 */
package com.mainsteam.stm.metric.obj;

import java.io.Serializable;
import java.util.List;

/**
 * @author ziw
 *
 */
public class CustomMetric implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7457296065658024542L;

	private CustomMetricInfo  customMetricInfo;

	private List<CustomMetricThreshold> customMetricThresholds;
	
	private List<CustomMetricCollectParameter> customMetricCollectParameters;
	
	private CustomMetricDataProcess customMetricDataProcess;
	
	/**
	 * 
	 */
	public CustomMetric() {
	}

	
	public List<CustomMetricThreshold> getCustomMetricThresholds() {
		return customMetricThresholds;
	}

	public List<CustomMetricCollectParameter> getCustomMetricCollectParameters() {
		return customMetricCollectParameters;
	}

	public void setCustomMetricThresholds(
			List<CustomMetricThreshold> customMetricThresholds) {
		this.customMetricThresholds = customMetricThresholds;
	}

	public void setCustomMetricCollectParameters(
			List<CustomMetricCollectParameter> customMetricCollectParameters) {
		this.customMetricCollectParameters = customMetricCollectParameters;
	}


	public CustomMetricInfo getCustomMetricInfo() {
		return customMetricInfo;
	}


	public void setCustomMetricInfo(CustomMetricInfo customMetricInfo) {
		this.customMetricInfo = customMetricInfo;
	}


	public CustomMetricDataProcess getCustomMetricDataProcess() {
		return customMetricDataProcess;
	}


	public void setCustomMetricDataProcess(
			CustomMetricDataProcess customMetricDataProcess) {
		this.customMetricDataProcess = customMetricDataProcess;
	}
}
