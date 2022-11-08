package com.mainsteam.stm.instancelib.obj;

import com.mainsteam.stm.instancelib.ojbenum.ResourceTypeEnum;

public class Link {

	private long srcInstanceId;
	
	private long srcChildInstanceId;
	
	private long destInstanceId;
	
	private long destChildInstanceId;
	
	private ResourceTypeEnum srcResourceTypeEnum;
	
	private ResourceTypeEnum destResourceTypeEnum;
	
	private String srcCategoryId;
	
	private String destCategoryId;

	public Link(){}
	
	public Link(long srcInstanceId,long srcChildInstanceId,
			   long destInstanceId,long destChildInstanceId,
			   ResourceTypeEnum srcResourceTypeEnum,ResourceTypeEnum destResourceTypeEnum,
			   String srcCategoryId,String destCategoryId){
		this.srcInstanceId = srcInstanceId;
		this.srcChildInstanceId = srcChildInstanceId;
		this.destInstanceId = destInstanceId;
		this.destChildInstanceId = destChildInstanceId;
		this.srcResourceTypeEnum = srcResourceTypeEnum;
		this.destResourceTypeEnum = destResourceTypeEnum;
		this.srcCategoryId = srcCategoryId;
		this.destCategoryId = destCategoryId;
	}
	
	public ResourceTypeEnum getSrcResourceTypeEnum() {
		return srcResourceTypeEnum;
	}
	public ResourceTypeEnum getDestResourceTypeEnum() {
		return destResourceTypeEnum;
	}
	public void setSrcResourceTypeEnum(ResourceTypeEnum srcResourceTypeEnum) {
		this.srcResourceTypeEnum = srcResourceTypeEnum;
	}
	public void setDestResourceTypeEnum(ResourceTypeEnum destResourceTypeEnum) {
		this.destResourceTypeEnum = destResourceTypeEnum;
	}
	public long getSrcChildInstanceId() {
		return srcChildInstanceId;
	}
	public long getDestChildInstanceId() {
		return destChildInstanceId;
	}
	public void setSrcChildInstanceId(long srcChildInstanceId) {
		this.srcChildInstanceId = srcChildInstanceId;
	}
	public void setDestChildInstanceId(long destChildInstanceId) {
		this.destChildInstanceId = destChildInstanceId;
	}
	public long getSrcInstanceId() {
		return srcInstanceId;
	}
	public long getDestInstanceId() {
		return destInstanceId;
	}
	public void setSrcInstanceId(long srcInstanceId) {
		this.srcInstanceId = srcInstanceId;
	}
	public void setDestInstanceId(long destInstanceId) {
		this.destInstanceId = destInstanceId;
	}

	public String getSrcCategoryId() {
		return srcCategoryId;
	}

	public String getDestCategoryId() {
		return destCategoryId;
	}

	public void setSrcCategoryId(String srcCategoryId) {
		this.srcCategoryId = srcCategoryId;
	}

	public void setDestCategoryId(String destCategoryId) {
		this.destCategoryId = destCategoryId;
	}
}
