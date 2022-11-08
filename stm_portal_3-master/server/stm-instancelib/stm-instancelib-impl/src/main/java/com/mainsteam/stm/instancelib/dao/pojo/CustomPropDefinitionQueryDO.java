/**
 * 
 */
package com.mainsteam.stm.instancelib.dao.pojo;

/**
 * @author ziw
 *
 */
public class CustomPropDefinitionQueryDO extends CustomPropDefinitionDO {

	private long startTime;
	private long endTime;
	
	/**
	 * 
	 */
	public CustomPropDefinitionQueryDO() {
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
