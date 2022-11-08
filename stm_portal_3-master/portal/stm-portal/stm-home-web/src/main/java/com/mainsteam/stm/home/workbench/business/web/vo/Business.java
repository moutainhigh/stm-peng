package com.mainsteam.stm.home.workbench.business.web.vo;

import java.io.Serializable;
/**
 * 
 * <li>文件名称: Business</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日 下午5:15:47
 * @author   xiaolei
 */
public class Business implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1792512135894360195L;
	
	/**
	 * 业务ID
	 */
	private long id;
	/**
	 * 业务名称
	 */
	private String businessName;
	
	/**
	 * 告警个数
	 */
	private int alarmCount;
	
	/**
	 * 主机个数
	 */
	private int hostCount;
	
	/**
	 * 网络设备
	 */
	private int networkEquipmentCount;
	
	/**
	 * 数据库
	 */
	private int databaseCount;
	
	/**
	 * 中间件
	 */
	private int middlewareCount;

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public int getAlarmCount() {
		return alarmCount;
	}

	public void setAlarmCount(int alarmCount) {
		this.alarmCount = alarmCount;
	}

	public int getHostCount() {
		return hostCount;
	}

	public void setHostCount(int hostCount) {
		this.hostCount = hostCount;
	}

	public int getNetworkEquipmentCount() {
		return networkEquipmentCount;
	}

	public void setNetworkEquipmentCount(int networkEquipmentCount) {
		this.networkEquipmentCount = networkEquipmentCount;
	}

	public int getDatabaseCount() {
		return databaseCount;
	}

	public void setDatabaseCount(int databaseCount) {
		this.databaseCount = databaseCount;
	}

	public int getMiddlewareCount() {
		return middlewareCount;
	}

	public void setMiddlewareCount(int middlewareCount) {
		this.middlewareCount = middlewareCount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
