package com.mainsteam.stm.caplib.common;

import com.mainsteam.stm.caplib.dict.DeviceTypeEnum;

/**
 * 
 * <li>文件名称: DeviceType.java</li><br/>
 * <li>文件描述: 设备类型</li><br/>
 * <li>版权所有: 版权所有(C)2014</li><br/>
 * <li>公司: 美新翔盛</li> <li>内容摘要: 设备类型定义</li> <br/>
 * <li>其他说明:</li><br/>
 * <li>完成日期：2014.8.12</li><br/>
 * <li>修改记录1:新建</li>
 * 
 * @version 3
 * @author sunsht
 */
public class DeviceType implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 916935806882261267L;
	/** 厂商ID */
	private String vendorId;
	/** 厂商名称 */
	private String vendorName;
	/** 厂商英文名 */
	private String vendorNameEn;
	/** 厂商图标 */
	private String vendorIcon;

	/** 模型ID */
	private String resourceId;

	private String sysOid;
	/** 系列 */
	private String series;
	/** 型号 */
	private String modelNumber;
	/** 主机用 */
	private String osType;
	/** 拓扑用 */
	private DeviceTypeEnum type;
	/** 排序顺序号 */
	private int sortId;

	private boolean isSystem;
	
	public DeviceType() {

	}

	public DeviceType(DeviceType dt) {
		super();
		this.vendorId = dt.vendorId;
		this.vendorName = dt.vendorName;
		this.vendorNameEn = dt.vendorNameEn;
		this.vendorIcon = dt.vendorIcon;
		this.resourceId = dt.resourceId;
		this.sysOid = dt.sysOid;
		this.series = dt.series;
		this.modelNumber = dt.modelNumber;
		this.osType = dt.osType;
		this.type = dt.type;
		this.sortId = dt.sortId;
		this.isSystem = dt.isSystem;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorNameEn() {
		return vendorNameEn;
	}

	public void setVendorNameEn(String vendorNameEn) {
		this.vendorNameEn = vendorNameEn;
	}

	public String getVendorIcon() {
		return vendorIcon;
	}

	public void setVendorIcon(String vendorIcon) {
		this.vendorIcon = vendorIcon;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getSysOid() {
		return sysOid;
	}

	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}

	public String getSeries() {
		if (null == series) {
			return "";
		}
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getModelNumber() {
		if (null == modelNumber) {
			return "";
		}
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public DeviceTypeEnum getType() {
		return type;
	}

	public void setType(DeviceTypeEnum type) {
		this.type = type;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + ((sysOid == null) ? 0 : sysOid.hashCode());
		result = prime * result
				+ ((vendorId == null) ? 0 : vendorId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceType other = (DeviceType) obj;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (sysOid == null) {
			if (other.sysOid != null)
				return false;
		} else if (!sysOid.equals(other.sysOid))
			return false;
		if (vendorId == null) {
			if (other.vendorId != null)
				return false;
		} else if (!vendorId.equals(other.vendorId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceType [vendorId=");
		builder.append(vendorId);
		builder.append(", vendorName=");
		builder.append(vendorName);
		builder.append(", vendorNameEn=");
		builder.append(vendorNameEn);
		builder.append(", vendorIcon=");
		builder.append(vendorIcon);
		builder.append(", resourceId=");
		builder.append(resourceId);
		builder.append(", sysOid=");
		builder.append(sysOid);
		builder.append(", series=");
		builder.append(series);
		builder.append(", modelNumber=");
		builder.append(modelNumber);
		builder.append(", osType=");
		builder.append(osType);
		builder.append(", type=");
		builder.append(type);
		builder.append(", isSystem=");
		builder.append(isSystem);
		builder.append(", sortId=");
		builder.append(sortId);
		builder.append("]");
		return builder.toString();
	}

}
