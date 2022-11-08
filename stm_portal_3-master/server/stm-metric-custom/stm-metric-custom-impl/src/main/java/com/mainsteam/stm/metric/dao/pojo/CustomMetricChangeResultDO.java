/**
 * 
 */
package com.mainsteam.stm.metric.dao.pojo;

import java.util.Date;

/**
 * @author ziw
 *
 */
public class CustomMetricChangeResultDO {

	private long change_id;
	
	private int dcs_group_id;
	
	private int result_state;
	
	private Date operate_time;
	
	/**
	 * 
	 */
	public CustomMetricChangeResultDO() {
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
	 * @return the dcs_group_id
	 */
	public final int getDcs_group_id() {
		return dcs_group_id;
	}

	/**
	 * @param dcs_group_id the dcs_group_id to set
	 */
	public final void setDcs_group_id(int dcs_group_id) {
		this.dcs_group_id = dcs_group_id;
	}

	/**
	 * @return the result_state
	 */
	public final int getResult_state() {
		return result_state;
	}

	/**
	 * @param result_state the result_state to set
	 */
	public final void setResult_state(int result_state) {
		this.result_state = result_state;
	}

	/**
	 * @return the operate_time
	 */
	public final Date getOperate_time() {
		return operate_time;
	}

	/**
	 * @param operate_time the operate_time to set
	 */
	public final void setOperate_time(Date operate_time) {
		this.operate_time = operate_time;
	}
}
