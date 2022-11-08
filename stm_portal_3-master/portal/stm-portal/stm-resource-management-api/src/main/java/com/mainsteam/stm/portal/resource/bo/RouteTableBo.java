package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

public class RouteTableBo implements Serializable{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5567413374333857973L;


	private String distIPAddress;
	private String ifIndex;
	private String mainRoutingMetric;
	private String backupingRoutingMetric1;
	private String backupingRoutingMetric2;
	private String backupingRoutingMetric3;
	private String backupingRoutingMetric4;
	private String nextHop;
	private String routeType;
	private String routeProtocol;
	private String routeAge;
	private String subnetMask;
	private String routeInfo;
	

	public String getDistIPAddress() {
		return distIPAddress;
	}
	public void setDistIPAddress(String distIPAddress) {
		this.distIPAddress = distIPAddress;
	}
	public String getIfIndex() {
		return ifIndex;
	}
	public void setIfIndex(String ifIndex) {
		this.ifIndex = ifIndex;
	}
	public String getMainRoutingMetric() {
		return mainRoutingMetric;
	}
	public void setMainRoutingMetric(String mainRoutingMetric) {
		this.mainRoutingMetric = mainRoutingMetric;
	}
	public String getBackupingRoutingMetric1() {
		return backupingRoutingMetric1;
	}
	public void setBackupingRoutingMetric1(String backupingRoutingMetric1) {
		this.backupingRoutingMetric1 = backupingRoutingMetric1;
	}
	public String getBackupingRoutingMetric2() {
		return backupingRoutingMetric2;
	}
	public void setBackupingRoutingMetric2(String backupingRoutingMetric2) {
		this.backupingRoutingMetric2 = backupingRoutingMetric2;
	}
	public String getBackupingRoutingMetric3() {
		return backupingRoutingMetric3;
	}
	public void setBackupingRoutingMetric3(String backupingRoutingMetric3) {
		this.backupingRoutingMetric3 = backupingRoutingMetric3;
	}
	public String getBackupingRoutingMetric4() {
		return backupingRoutingMetric4;
	}
	public void setBackupingRoutingMetric4(String backupingRoutingMetric4) {
		this.backupingRoutingMetric4 = backupingRoutingMetric4;
	}
	public String getNextHop() {
		return nextHop;
	}
	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}
	public String getRouteType() {
		return routeType;
	}
	public void setRouteType(String routeType) {
		this.routeType = routeType;
	}
	public String getRouteProtocol() {
		return routeProtocol;
	}
	public void setRouteProtocol(String routeProtocol) {
		this.routeProtocol = routeProtocol;
	}
	public String getRouteAge() {
		return routeAge;
	}
	public void setRouteAge(String routeAge) {
		this.routeAge = routeAge;
	}
	public String getSubnetMask() {
		return subnetMask;
	}
	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}
	public String getRouteInfo() {
		return routeInfo;
	}
	public void setRouteInfo(String routeInfo) {
		this.routeInfo = routeInfo;
	}
}
