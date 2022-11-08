/**
 * 
 */
package com.mainsteam.stm.metric.obj;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

/**
 * @author ziw
 *
 */
public class CustomMetricThreshold implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4093976410398238271L;

	private String metricId;
	
	private MetricStateEnum metricState;
	
	private OperatorEnum operator;
	
	private String thresholdValue;
	
	private String alarmTemplate;
	
	private String thresholdExpression;
	
	public static final String PLACEHOLDER = "X";
	
	/**
	 * 
	 */
	public CustomMetricThreshold() {
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
	 * @return the metricState
	 */
	public final MetricStateEnum getMetricState() {
		return metricState;
	}



	/**
	 * @param metricState the metricState to set
	 */
	public final void setMetricState(MetricStateEnum metricState) {
		this.metricState = metricState;
	}



	/**
	 * @return the operator
	 */
	public final OperatorEnum getOperator() {
		return operator;
	}



	/**
	 * @param operator the operator to set
	 */
	public final void setOperator(OperatorEnum operator) {
		this.operator = operator;
	}



	/**
	 * @return the thresholdValue
	 */
	public final String getThresholdValue() {
		return thresholdValue;
	}



	/**
	 * @param thresholdValue the thresholdValue to set
	 */
	public final void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}


	public String getAlarmTemplate() {
		return alarmTemplate;
	}



	public void setAlarmTemplate(String alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}



	public String getThresholdExpression() {
		return thresholdExpression;
	}



	public void setThresholdExpression(String thresholdExpression) {
		this.thresholdExpression = thresholdExpression;
	}

}
