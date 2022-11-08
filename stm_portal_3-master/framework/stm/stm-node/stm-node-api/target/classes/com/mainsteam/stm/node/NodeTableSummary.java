/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ziw
 *
 */
public class NodeTableSummary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3887755942003614448L;
	
	private Date updateTime;
	private int nodesize;
	private int nodeGroupSize;
	
	/**
	 * 
	 */
	public NodeTableSummary() {
	}

	/**
	 * @return the updateTime
	 */
	public final Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public final void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * @return the nodesize
	 */
	public final int getNodesize() {
		return nodesize;
	}

	/**
	 * @param nodesize the nodesize to set
	 */
	public final void setNodesize(int nodesize) {
		this.nodesize = nodesize;
	}

	/**
	 * @return the nodeGroupSize
	 */
	public final int getNodeGroupSize() {
		return nodeGroupSize;
	}

	/**
	 * @param nodeGroupSize the nodeGroupSize to set
	 */
	public final void setNodeGroupSize(int nodeGroupSize) {
		this.nodeGroupSize = nodeGroupSize;
	}
}
