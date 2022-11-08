/**
 * 
 */
package com.mainsteam.stm.node.dao.pojo;

/**
 * @author ziw
 * 
 */
public class NodeHeartbeatCheckDO {
	private int nodeId;
	private long hbCount;
	private long ckCount;
	private long hbOccurtime;
	
	/**
	 * 
	 */
	public NodeHeartbeatCheckDO() {
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
	 * @return the hbCount
	 */
	public final long getHbCount() {
		return hbCount;
	}

	/**
	 * @param hbCount
	 *            the hbCount to set
	 */
	public final void setHbCount(long hbCount) {
		this.hbCount = hbCount;
	}

	/**
	 * @return the ckCount
	 */
	public final long getCkCount() {
		return ckCount;
	}

	/**
	 * @param ckCount
	 *            the ckCount to set
	 */
	public final void setCkCount(long ckCount) {
		this.ckCount = ckCount;
	}

	/**
	 * @return the hbOccurtime
	 */
	public final long getHbOccurtime() {
		return hbOccurtime;
	}

	/**
	 * @param hbOccurtime the hbOccurtime to set
	 */
	public final void setHbOccurtime(long hbOccurtime) {
		this.hbOccurtime = hbOccurtime;
	}
}
