package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 资源自动刷新策略
 * @author Administrator
 *
 */
public class ProfileAutoRefresh implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5358507654197598211L;
	private long id;
	/**
	 * 资源实例ID
	 */
	private long instanceId;
	/**
	 * job当前执行时间
	 */
	private Date executeTime;
	/**
	 * 下次执行时间
	 */
	private Date nextExecuteTime;
	/**
	 * 执行频度
	 */
	private int executRepeat;
	
	private int isUse;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public Date getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
	public Date getNextExecuteTime() {
		return nextExecuteTime;
	}
	public void setNextExecuteTime(Date nextExecuteTime) {
		this.nextExecuteTime = nextExecuteTime;
	}
	public int getExecutRepeat() {
		return executRepeat;
	}
	public void setExecutRepeat(int executRepeat) {
		this.executRepeat = executRepeat;
	}
	
	
	public int getIsUse() {
		return isUse;
	}
	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}
	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer string = new StringBuffer("[");
		string.append("id:").append(this.id).append(",");
		string.append("instanceId:").append(this.instanceId).append(",");
		string.append("isUse:").append(this.isUse==1?"启用":"禁用").append(",");
		string.append("executeTime:").append(format.format(this.executeTime)).append(",");
		string.append("nextExecuteTime:").append(format.format(this.nextExecuteTime)).append(",");
		string.append("executRepeat:").append(this.executRepeat).append("]");
		return string.toString();
	}
}
