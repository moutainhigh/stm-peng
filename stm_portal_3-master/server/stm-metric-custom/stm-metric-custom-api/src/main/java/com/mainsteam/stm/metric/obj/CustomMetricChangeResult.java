/**
 * 
 */
package com.mainsteam.stm.metric.obj;

import java.util.Date;

/**
 * @author ziw
 * 
 */
public class CustomMetricChangeResult {
	private long changeId;
	
	private int dcsGroupId;
	
	private boolean resultState;
	
	private Date operateTime;

	/**
	 * 
	 */
	public CustomMetricChangeResult() {
	}

	/**
	 * @return the changeId
	 */
	public final long getChangeId() {
		return changeId;
	}

	/**
	 * @param changeId the changeId to set
	 */
	public final void setChangeId(long changeId) {
		this.changeId = changeId;
	}

	/**
	 * @return the dcsGroupId
	 */
	public final int getDcsGroupId() {
		return dcsGroupId;
	}

	/**
	 * @param dcsGroupId the dcsGroupId to set
	 */
	public final void setDcsGroupId(int dcsGroupId) {
		this.dcsGroupId = dcsGroupId;
	}

	/**
	 * @return the resultState
	 */
	public final boolean isResultState() {
		return resultState;
	}

	/**
	 * @param resultState the resultState to set
	 */
	public final void setResultState(boolean resultState) {
		this.resultState = resultState;
	}

	/**
	 * @return the operateTime
	 */
	public final Date getOperateTime() {
		return operateTime;
	}

	/**
	 * @param operateTime the operateTime to set
	 */
	public final void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
}
