/**
 * 
 */
package com.mainsteam.stm.metric.obj;

import java.util.Date;

/**
 * @author ziw
 * 
 */
public class CustomMetricChange {
	private long changeId;

	private String metricId;

	private String operateMode;

	private Date occurTime;

	private Date changeTime;

	private boolean operateState;

	private long instanceId;

	private String pluginId;

	/**
	 * 
	 */
	public CustomMetricChange() {
	}

	/**
	 * @return the changeId
	 */
	public final long getChangeId() {
		return changeId;
	}

	/**
	 * @param changeId
	 *            the changeId to set
	 */
	public final void setChangeId(long changeId) {
		this.changeId = changeId;
	}

	/**
	 * @return the metricId
	 */
	public final String getMetricId() {
		return metricId;
	}

	/**
	 * @param metricId
	 *            the metricId to set
	 */
	public final void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	/**
	 * @return the operateMode
	 */
	public final String getOperateMode() {
		return operateMode;
	}

	/**
	 * @param operateMode
	 *            the operateMode to set
	 */
	public final void setOperateMode(String operateMode) {
		this.operateMode = operateMode;
	}

	/**
	 * @return the occurTime
	 */
	public final Date getOccurTime() {
		return occurTime;
	}

	/**
	 * @param occurTime
	 *            the occurTime to set
	 */
	public final void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}

	/**
	 * @return the changeTime
	 */
	public final Date getChangeTime() {
		return changeTime;
	}

	/**
	 * @param changeTime
	 *            the changeTime to set
	 */
	public final void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	/**
	 * @return the operateState
	 */
	public final boolean isOperateState() {
		return operateState;
	}

	/**
	 * @param operateState
	 *            the operateState to set
	 */
	public final void setOperateState(boolean operateState) {
		this.operateState = operateState;
	}

	/**
	 * @return the instanceId
	 */
	public final long getInstanceId() {
		return instanceId;
	}

	/**
	 * @param instanceId
	 *            the instanceId to set
	 */
	public final void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	/**
	 * @return the pluginId
	 */
	public final String getPluginId() {
		return pluginId;
	}

	/**
	 * @param pluginId
	 *            the pluginId to set
	 */
	public final void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
}
