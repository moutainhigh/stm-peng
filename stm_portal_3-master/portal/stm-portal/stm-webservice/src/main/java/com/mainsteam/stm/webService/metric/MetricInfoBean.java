package com.mainsteam.stm.webService.metric;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement; 

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MetricInfoBean")
public class MetricInfoBean {
	/* 资源实例id */
	@XmlElement(name = "instanceId", required = true)
	private long instanceId;
	/* 模型类型Id */
	@XmlElement(name = "categoryId", required = true)
	private String categoryId;
	/* 模型名称 */
	@XmlElement(name = "categoryName", required = true)
	private String categoryName;
	/* 资源Id */
	@XmlElement(name = "resourceId", required = true)
	private String resourceId;
	/* 设备名称 */
	@XmlElement(name = "deviceName", required = true)
	private String deviceName;
	/* 设备型号 */
	@XmlElement(name = "deviceModel", required = true)
	private String deviceModel;
	/* 设备软件版本 */
	@XmlElement(name = "deviceSoftVersion", required = true)
	private String deviceSoftVersion;
	/* 设备厂家 */
	@XmlElement(name = "vendorName", required = true)
	private String vendorName;
	/* IP地址 */
	@XmlElement(name = "ip", required = true)
	private String ip;
	/* 当前状态 */
	@XmlElement(name = "currentState", required = true)
	private String currentState;
	/* 设备序列号 */
	@XmlElement(name = "deviceSerialNo", required = true)
	private String deviceSerialNo;
	/* 端口数量 */
	@XmlElement(name = "portNum",required = true)
	private String portNum;
	/* 带宽能力 */
	@XmlElement(name = "netInterfaceList",required = true)
	List<InfoMetricSumaryDataBean> netInterfaceList;
    @XmlElement(name = "netFaceResourceId",required = true)
    private String netFaceResourceId;
    /*加入监控接口数*/
    @XmlElement(name = "monitoredPortNum",required = true)
    private int monitoredPortNum;
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getDeviceSoftVersion() {
		return deviceSoftVersion;
	}
	public void setDeviceSoftVersion(String deviceSoftVersion) {
		this.deviceSoftVersion = deviceSoftVersion;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCurrentState() {
		return currentState;
	}
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}
	public String getDeviceSerialNo() {
		return deviceSerialNo;
	}
	public void setDeviceSerialNo(String deviceSerialNo) {
		this.deviceSerialNo = deviceSerialNo;
	}
	public String getPortNum() {
		return portNum;
	}
	public void setPortNum(String portNum) {
		this.portNum = portNum;
	}
	public List<InfoMetricSumaryDataBean> getNetInterfaceList() {
		return netInterfaceList;
	}
	public void setNetInterfaceList(List<InfoMetricSumaryDataBean> netInterfaceList) {
		this.netInterfaceList = netInterfaceList;
	}
	 
	public String getNetFaceResourceId() {
		return netFaceResourceId;
	}
	public void setNetFaceResourceId(String netFaceResourceId) {
		this.netFaceResourceId = netFaceResourceId;
	}
	public int getMonitoredPortNum() {
		return monitoredPortNum;
	}
	public void setMonitoredPortNum(int monitoredPortNum) {
		this.monitoredPortNum = monitoredPortNum;
	}
     
}
