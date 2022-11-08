/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.Serializable;
import java.util.Date;

/**
 * 分布式节点
 * 
 * @author ziw
 * 
 */
public class Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1387242827157836402L;

	/**
	 * 节点id
	 */
	private int id;

	/**
	 * 节点功能类型
	 */
	private NodeFunc func;

	/**
	 * 节点显示名称
	 */
	private String name;

	/**
	 * 节点绑定的ip地址
	 */
	private String ip;

	/**
	 * 节点的远程管理端口
	 */
	private int port;

	/**
	 * 节点分组id
	 */
	private int groupId;

	/**
	 * 节点在节点组中的优先级
	 */
	private int priority;

	/**
	 * 判断节点是否可用
	 */
	private boolean isAlive;
	
	/**
	 * 判断节点是否隔离墙节点
	 * true 隔离节点  false 非隔离节点
	 */
	private boolean isolated = false;
	
	private Date startupTime;
	
	
	private String installPath;

	private String description;
	
	/**
	 * 
	 */
	public Node() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public NodeFunc getFunc() {
		return func;
	}

	public void setFunc(NodeFunc func) {
		this.func = func;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the isAlive
	 */
	public final boolean isAlive() {
		return isAlive;
	}

	/**
	 * @param isAlive
	 *            the isAlive to set
	 */
	public final void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	/**
	 * 给系统管理页面使用，这个接口后续会删除。
	 * 
	 * @deprecated 这个接口后续会删除。
	 * @return the parentNodeId
	 */
	public int getParentNodeId() {
		return -1;
	}

	/**
	 * @return the installPath
	 */
	public final String getInstallPath() {
		return installPath;
	}

	/**
	 * @param installPath the installPath to set
	 */
	public final void setInstallPath(String installPath) {
		this.installPath = installPath;
	}
	
	/**
	 * @return the startupTime
	 */
	public final Date getStartupTime() {
		return startupTime;
	}

	/**
	 * @param startupTime the startupTime to set
	 */
	public final void setStartupTime(Date startupTime) {
		this.startupTime = startupTime;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.ip).append(':').append(this.port);
		return b.toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isIsolated() {
		return isolated;
	}

	public void setIsolated(boolean isolated) {
		this.isolated = isolated;
	}
}
