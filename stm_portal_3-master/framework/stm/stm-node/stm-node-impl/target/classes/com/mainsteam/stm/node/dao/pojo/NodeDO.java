/**
 * 
 */
package com.mainsteam.stm.node.dao.pojo;

/**
 * @author ziw
 *
 */
public class NodeDO {
	private Integer id;
	private String name;
	private String ip;
	private Integer port;
	private String func;
	private Integer groupId;
	private Integer priority;
	private Integer alive;
	private String installPath;
	private long updateTime;
	private long startupTime;	
	private String description;
	/**
	 * 
	 */
	public NodeDO() {
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
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


	public Integer getPort() {
		return port;
	}


	public void setPort(Integer port) {
		this.port = port;
	}


	public String getFunc() {
		return func;
	}


	public void setFunc(String func) {
		this.func = func;
	}


	public Integer getGroupId() {
		return groupId;
	}


	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getPriority() {
		return priority;
	}


	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	/**
	 * @return the alive
	 */
	public final Integer getAlive() {
		return alive;
	}


	/**
	 * @param alive the alive to set
	 */
	public final void setAlive(Integer alive) {
		this.alive = alive;
	}


	/**
	 * @return the udateTime
	 */
	public final long getUpdateTime() {
		return updateTime;
	}


	/**
	 * @param udateTime the udateTime to set
	 */
	public final void setUpdateTime(long udateTime) {
		this.updateTime = udateTime;
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
	public final long getStartupTime() {
		return startupTime;
	}


	/**
	 * @param startupTime the startupTime to set
	 */
	public final void setStartupTime(long startupTime) {
		this.startupTime = startupTime;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
}
