/**
 * 
 */
package com.mainsteam.stm.node.dao.pojo;


/**
 * @author ziw
 *
 */
public class NodeHeartbeatDO {
	
	private long id;
	private int nodeId;

	/**
	 * 心跳发生时间
	 */
	private long occurtime;

	/**
	 * 下一次心跳的时间
	 */
	private long nextOccurtime;

	/**
	 * 节点启动以后，心跳的次数
	 */
	private int occurCount;

	/**
	 * 节点心跳过期时间。如果当前时间超过该时间，则可以认为节点不可用。
	 * 
	 */
	private long expireOccurtime; 

	/**
	 * 
	 */
	public NodeHeartbeatDO() {
	}

	/**
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the nodeId
	 */
	public final int getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId the nodeId to set
	 */
	public final void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the occurtime
	 */
	public final long getOccurtime() {
		return occurtime;
	}

	/**
	 * @param occurtime the occurtime to set
	 */
	public final void setOccurtime(long occurtime) {
		this.occurtime = occurtime;
	}

	/**
	 * @return the nextOccurtime
	 */
	public final long getNextOccurtime() {
		return nextOccurtime;
	}

	/**
	 * @param nextOccurtime the nextOccurtime to set
	 */
	public final void setNextOccurtime(long nextOccurtime) {
		this.nextOccurtime = nextOccurtime;
	}

	/**
	 * @return the occurCount
	 */
	public final int getOccurCount() {
		return occurCount;
	}

	/**
	 * @param occurCount the occurCount to set
	 */
	public final void setOccurCount(int occurCount) {
		this.occurCount = occurCount;
	}

	/**
	 * @return the expireOccurtime
	 */
	public final long getExpireOccurtime() {
		return expireOccurtime;
	}

	/**
	 * @param expireOccurtime the expireOccurtime to set
	 */
	public final void setExpireOccurtime(long expireOccurtime) {
		this.expireOccurtime = expireOccurtime;
	}
}
