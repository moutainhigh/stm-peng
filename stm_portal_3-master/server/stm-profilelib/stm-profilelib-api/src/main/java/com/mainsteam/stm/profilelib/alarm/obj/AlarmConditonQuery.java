/**
 * 
 */
package com.mainsteam.stm.profilelib.alarm.obj;


/**
 * 根据条件，查询告警规则
 * 
 * @author ziw
 * 
 */
public class AlarmConditonQuery {
	
	private long ruleId;
	
	private SendWayEnum sendWay;
	/**
	 * 
	 */
	public AlarmConditonQuery() {
	}
	
	/**
	 * @return the ruleId
	 */
	public final long getRuleId() {
		return ruleId;
	}


	/**
	 * @param ruleId the ruleId to set
	 */
	public final void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}



	/**
	 * @return the sendWay
	 */
	public final SendWayEnum getSendWay() {
		return sendWay;
	}
	/**
	 * @param sendWay the sendWay to set
	 */
	public final void setSendWay(SendWayEnum sendWay) {
		this.sendWay = sendWay;
	}
}
