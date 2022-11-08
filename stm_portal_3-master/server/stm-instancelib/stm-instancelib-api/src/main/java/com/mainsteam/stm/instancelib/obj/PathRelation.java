package com.mainsteam.stm.instancelib.obj;

import com.mainsteam.stm.instancelib.obj.AbstractRelation;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;

/**
 * 路径关系类
 * @author xiaoruqiang
 */
public class PathRelation extends AbstractRelation {

	/**
	 * 源实例ID
	 */
	private long fromInstanceId;
	/**
	 * 目的实例ID
	 */
	private long toInstanceId;
	/**
	 * 源实例类型 
	 */
	private InstanceTypeEnum fromInstanceType;
	/**
	 * 目的实例类型
	 */
	private InstanceTypeEnum toInstanceType;
	

	public long getFromInstanceId() {
		return fromInstanceId;
	}

	public void setFromInstanceId(long fromInstanceId) {
		this.fromInstanceId = fromInstanceId;
	}

	public long getToInstanceId() {
		return toInstanceId;
	}

	public void setToInstanceId(long toInstanceId) {
		this.toInstanceId = toInstanceId;
	}

	public InstanceTypeEnum getFromInstanceType() {
		return fromInstanceType;
	}

	public void setFromInstanceType(InstanceTypeEnum fromInstanceType) {
		this.fromInstanceType = fromInstanceType;
	}

	public InstanceTypeEnum getToInstanceType() {
		return toInstanceType;
	}

	public void setToInstanceType(InstanceTypeEnum toInstanceType) {
		this.toInstanceType = toInstanceType;
	}

	@Override
	public String getRelationType() {
		return PathRelation.class.getSimpleName();
	}
}
