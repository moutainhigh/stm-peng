package com.mainsteam.stm.system.um.service.web.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mainsteam.stm.node.NodeFunc;

public class NodeVo {

	private int id;
	private NodeFunc func;
	private String name;
	private String ip;
	private int port;
	private int groupId;
	private int priority;
	private boolean isAlive;
	private String installPath;
	private Date startupTime;
	private String startupTimeStr;
	private String description;
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
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public String getInstallPath() {
		return installPath;
	}
	public void setInstallPath(String installPath) {
		this.installPath = installPath;
	}
	public Date getStartupTime() {
		return startupTime;
	}
	public void setStartupTime(Date startupTime) {
		this.startupTime = startupTime;
	}
	public String getStartupTimeStr() {
		if(this.startupTime!=null){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(this.startupTime);
		}else {
			return startupTimeStr;
		}
	}
	public void setStartupTimeStr(String startupTimeStr) {
		this.startupTimeStr = startupTimeStr;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
