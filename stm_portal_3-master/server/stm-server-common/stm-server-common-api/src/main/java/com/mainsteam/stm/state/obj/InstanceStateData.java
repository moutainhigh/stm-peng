package com.mainsteam.stm.state.obj;

import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;

/**资源实例状态
 * 为了更好的扩展性，将实例状态拆分成资源状态和告警状态
 * @author cx
 * @modify by xiaopf
 */
public class InstanceStateData implements Comparable<Object>, Serializable, Cloneable {
	
	private static final long serialVersionUID = 8878947179374260205L;

	private long instanceID;//资源实例ID
	private transient InstanceStateEnum state; //（资源状态+告警状态，向上兼容需要用到，通过resourceState和alarmState计算得到）
	private InstanceStateEnum resourceState; //资源状态
	private InstanceStateEnum alarmState; //告警状态
	private Date collectTime; //采集时间
	/*
	对于causeBymetricID和causeByInstance需要遵循一条规则：对于主资源，只有告警状态发生改变才会引起causeBymetricID和causeByInstance的变化，也就是说，
	对于主资源，causeBymetricID和causeByInstance只和告警状态的变化有关；对于子资源，因为主资源的致命或者恢复正常都会影响到子资源的资源状态，所以，
	对于子资源来说，主资源致命-->正常，正常-->致命，也会影响causeBymetricID和causeByInstance的变化（即子资源的causeByMetricID和causeByInstance不仅和
	自身的告警状态有关，还和主资源的资源状态有关）。
	 */
	private String causeBymetricID;//引起资源的告警状态的指标
	private long causeByInstance;//引起资源的告警状态的实例
	private CollectStateEnum collectStateEnum; //可采集状态
	private transient Date updateTime; //数据库更新时间

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public long getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}

	public InstanceStateEnum getState() {
		if(InstanceStateEnum.CRITICAL == this.resourceState)
			return InstanceStateEnum.CRITICAL;
		else if(InstanceStateEnum.CRITICAL == this.alarmState)
			return InstanceStateEnum.NORMAL_CRITICAL;
		else
			return this.alarmState;
	}

	/**
	 * use setResourceState() or setAlarmState() instead
	 * @param state
	 */
	@Deprecated
	public void setState(InstanceStateEnum state) {
		this.state = state;
		if(InstanceStateEnum.CRITICAL == state){
			this.resourceState = InstanceStateEnum.CRITICAL;
			//alarmState保留上次状态
		}else {
			this.resourceState = InstanceStateEnum.NORMAL;
			if(InstanceStateEnum.NORMAL_CRITICAL == state) {
				this.alarmState = InstanceStateEnum.CRITICAL;
			}else
				this.alarmState = state;
		}
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public long getCauseByInstance() {
		return causeByInstance;
	}

	public void setCauseByInstance(long causeByInstance) {
		this.causeByInstance = causeByInstance;
	}

	public String getCauseBymetricID() {
		return causeBymetricID;
	}

	public void setCauseBymetricID(String causeBymetricID) {
		this.causeBymetricID = causeBymetricID;
	}

	public CollectStateEnum getCollectStateEnum() {
		return collectStateEnum;
	}

	public void setCollectStateEnum(CollectStateEnum collectStateEnum) {
		this.collectStateEnum = collectStateEnum;
	}

	public InstanceStateData(){
	}

	public InstanceStateData(long instanceID, InstanceStateEnum state, Date collectTime, String causeBymetricID, long causeByInstance, CollectStateEnum collectStateEnum) {
		this(instanceID,state,collectTime,causeBymetricID,collectStateEnum);
		this.causeByInstance = causeByInstance;
	}

	public InstanceStateData(long instanceID, InstanceStateEnum state, Date collectTime, String causeBymetricID, CollectStateEnum collectStateEnum) {
		this.instanceID = instanceID;
		this.state = state;
		if(InstanceStateEnum.CRITICAL == state) {
			this.resourceState = InstanceStateEnum.CRITICAL;
		}else{
			if(InstanceStateEnum.NORMAL_CRITICAL == state) {
				this.alarmState = InstanceStateEnum.CRITICAL;
			}else{
				this.alarmState = state;
			}
			this.resourceState = InstanceStateEnum.NORMAL;
		}
		this.collectTime = collectTime;
		this.causeBymetricID = causeBymetricID;
		this.collectStateEnum = collectStateEnum;

	}

	public InstanceStateData(long instanceID, InstanceStateEnum resourceState, InstanceStateEnum alarmState, Date collectTime, String causeBymetricID, long causeByInstance, CollectStateEnum collectStateEnum) {
		this(instanceID,resourceState,alarmState,collectTime,causeBymetricID,collectStateEnum);
		this.causeByInstance = causeByInstance;
	}

	public InstanceStateData(long instanceID, InstanceStateEnum resourceState, InstanceStateEnum alarmState, Date collectTime, String causeBymetricID, CollectStateEnum collectStateEnum) {
		this.instanceID = instanceID;
		this.alarmState = alarmState;
		this.resourceState = resourceState;
		this.collectTime = collectTime;
		this.causeBymetricID = causeBymetricID;
		this.collectStateEnum = collectStateEnum;

	}


	@Override
	public int compareTo(Object o) {
		InstanceStateData instanceStateData = (InstanceStateData)o;
		if(this.getState().getStateVal() > instanceStateData.getState().getStateVal())
			return 1;
		else if(this.getState().getStateVal() < instanceStateData.getState().getStateVal())
			return -1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "InstanceStateData{" +
				"instanceID=" + instanceID +
				", resourceState=" + resourceState +
				", alarmState=" + alarmState +
				", collectTime=" + collectTime +
				", causeBymetricID='" + causeBymetricID + '\'' +
				", causeByInstance=" + causeByInstance +
				", collectStateEnum=" + collectStateEnum +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InstanceStateData that = (InstanceStateData) o;

		if (instanceID != that.instanceID) return false;
		if (causeByInstance != that.causeByInstance) return false;
		if (resourceState != that.resourceState) return false;
		if (alarmState != that.alarmState) return false;
		if (!collectTime.equals(that.collectTime)) return false;
		if (!causeBymetricID.equals(that.causeBymetricID)) return false;
		return collectStateEnum == that.collectStateEnum;
	}

	@Override
	public int hashCode() {
		int result = (int) (instanceID ^ (instanceID >>> 32));
		result = 31 * result + resourceState.hashCode();
		result = 31 * result + alarmState.hashCode();
		result = 31 * result + collectTime.hashCode();
		result = 31 * result + causeBymetricID.hashCode();
		result = 31 * result + (int) (causeByInstance ^ (causeByInstance >>> 32));
		result = 31 * result + collectStateEnum.hashCode();
		return result;
	}

	@Override
	public InstanceStateData clone() throws CloneNotSupportedException {
		InstanceStateData clone = new InstanceStateData();
		clone.setCollectTime((Date) this.getCollectTime().clone());
		clone.setAlarmState(this.alarmState);
		clone.setResourceState(this.resourceState);
		clone.setState(this.state);
		clone.setCauseBymetricID(this.causeBymetricID);
		clone.setCauseByInstance(this.causeByInstance);
		clone.setCollectStateEnum(this.collectStateEnum);
		clone.setInstanceID(this.instanceID);
		return clone;
	}

	/**
	 * 获取资源状态
	 * @return
	 */
	public InstanceStateEnum getResourceState() {
		return this.resourceState;
	}

	/**
	 * 获取告警状态
	 * @return
	 */
	public InstanceStateEnum getAlarmState() {
		return this.alarmState;
	}

	public void setResourceState(InstanceStateEnum resourceState) {
		this.resourceState = resourceState;
	}

	public void setAlarmState(InstanceStateEnum alarmState) {
		this.alarmState = alarmState;
	}

}
