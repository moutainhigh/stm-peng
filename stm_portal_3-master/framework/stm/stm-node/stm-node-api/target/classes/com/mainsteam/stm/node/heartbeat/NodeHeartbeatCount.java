/**
 * 
 */
package com.mainsteam.stm.node.heartbeat;

import java.util.Date;

/**
 * @author ziw
 * 
 */
public class NodeHeartbeatCount {

	/**
	 * 节点id
	 */
	private int nodeId;

	/**
	 * 检查时心跳的次数
	 */
	private long heartbeatCount;

	/**
	 * 连续检查次数,检查时，心跳不再变化。
	 */
	private long checkCount;

	private Date heartbeatOccurtime;
	
	/**
	 * 
	 */
	public NodeHeartbeatCount() {
	}

	/**
	 * @return the nodeId
	 */
	public final int getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId
	 *            the nodeId to set
	 */
	public final void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the heartbeatCount
	 */
	public final long getHeartbeatCount() {
		return heartbeatCount;
	}

	/**
	 * @param heartbeatCount the heartbeatCount to set
	 */
	public final void setHeartbeatCount(long heartbeatCount) {
		this.heartbeatCount = heartbeatCount;
	}

	/**
	 * @return the heartbeatOccurtime
	 */
	public final Date getHeartbeatOccurtime() {
		return heartbeatOccurtime;
	}

	/**
	 * @param heartbeatOccurtime the heartbeatOccurtime to set
	 */
	public final void setHeartbeatOccurtime(Date heartbeatOccurtime) {
		this.heartbeatOccurtime = heartbeatOccurtime;
	}

	/**
	 * @return the checkCount
	 */
	public final long getCheckCount() {
		return checkCount;
	}

	/**
	 * @param checkCount
	 *            the checkCount to set
	 */
	public final void setCheckCount(long checkCount) {
		this.checkCount = checkCount;
	}
}
