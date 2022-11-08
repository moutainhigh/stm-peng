/**
 * 
 */
package com.mainsteam.stm.node.heartbeat;

import java.io.Serializable;
import java.util.Date;

/**
 * 节点心跳
 * 
 * @author ziw
 * 
 */
public class NodeHeartbeat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1935165591208842912L;
	

	/**
	 * 心跳记录id
	 */
	private long id;

	/**
	 * 节点id
	 */
	private int nodeId;

	/**
	 * 心跳发生时间
	 */
	private Date occurtime;

	/**
	 * 下一次心跳的时间
	 */
	private Date nextOccurtime;

	/**
	 * 节点启动以后，心跳的次数
	 */
	private int occurCount;

	/**
	 * 节点心跳过期时间。如果当前时间超过该时间，则可以认为节点不可用。
	 * 
	 */
	private Date expireOccurtime;

	/**
	 * 
	 */
	public NodeHeartbeat() {
	}

	/**
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
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
	 * @param nodeId
	 *            the nodeId to set
	 */
	public final void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the occurtime
	 */
	public final Date getOccurtime() {
		return occurtime;
	}

	/**
	 * @param occurtime
	 *            the occurtime to set
	 */
	public final void setOccurtime(Date occurtime) {
		this.occurtime = occurtime;
	}

	/**
	 * @return the nextOccurtime
	 */
	public final Date getNextOccurtime() {
		return nextOccurtime;
	}

	/**
	 * @param nextOccurtime
	 *            the nextOccurtime to set
	 */
	public final void setNextOccurtime(Date nextOccurtime) {
		this.nextOccurtime = nextOccurtime;
	}

	/**
	 * @return the occurCount
	 */
	public final int getOccurCount() {
		return occurCount;
	}

	/**
	 * @param occurCount
	 *            the occurCount to set
	 */
	public final void setOccurCount(int occurCount) {
		this.occurCount = occurCount;
	}

	/**
	 * @return the expireOccurtime
	 */
	public final Date getExpireOccurtime() {
		return expireOccurtime;
	}

	/**
	 * @param expireOccurtime the expireOccurtime to set
	 */
	public final void setExpireOccurtime(Date expireOccurtime) {
		this.expireOccurtime = expireOccurtime;
	}
}
