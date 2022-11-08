/**
 * 
 */
package com.mainsteam.stm.metric.dao.pojo;

import java.util.Date;

/**
 * @author ziw
 *
 */
public class CustomMetricChangeDO {

	private long change_id;
	
	private String metric_id;
	
	private String operateMode;
	
	private Date occur_time;
	
	private Date change_time;
	
	private int operate_state;
	
	private Long instance_id;
	
	private String plugin_id;
	
	
	/**
	 * 
	 */
	public CustomMetricChangeDO() {
	}


	/**
	 * @return the change_id
	 */
	public final long getChange_id() {
		return change_id;
	}


	/**
	 * @param change_id the change_id to set
	 */
	public final void setChange_id(long change_id) {
		this.change_id = change_id;
	}


	/**
	 * @return the metric_id
	 */
	public final String getMetric_id() {
		return metric_id;
	}


	/**
	 * @param metric_id the metric_id to set
	 */
	public final void setMetric_id(String metric_id) {
		this.metric_id = metric_id;
	}


	/**
	 * @return the operateMode
	 */
	public final String getOperateMode() {
		return operateMode;
	}


	/**
	 * @param operateMode the operateMode to set
	 */
	public final void setOperateMode(String operateMode) {
		this.operateMode = operateMode;
	}


	/**
	 * @return the occur_time
	 */
	public final Date getOccur_time() {
		return occur_time;
	}


	/**
	 * @param occur_time the occur_time to set
	 */
	public final void setOccur_time(Date occur_time) {
		this.occur_time = occur_time;
	}


	/**
	 * @return the change_time
	 */
	public final Date getChange_time() {
		return change_time;
	}


	/**
	 * @param change_time the change_time to set
	 */
	public final void setChange_time(Date change_time) {
		this.change_time = change_time;
	}


	/**
	 * @return the operate_state
	 */
	public final int getOperate_state() {
		return operate_state;
	}


	/**
	 * @param operate_state the operate_state to set
	 */
	public final void setOperate_state(int operate_state) {
		this.operate_state = operate_state;
	}


	/**
	 * @return the instance_id
	 */
	public final Long getInstance_id() {
		return instance_id;
	}


	/**
	 * @param instance_id the instance_id to set
	 */
	public final void setInstance_id(Long instance_id) {
		this.instance_id = instance_id;
	}


	/**
	 * @return the plugin_id
	 */
	public final String getPlugin_id() {
		return plugin_id;
	}


	/**
	 * @param plugin_id the plugin_id to set
	 */
	public final void setPlugin_id(String plugin_id) {
		this.plugin_id = plugin_id;
	}
}
