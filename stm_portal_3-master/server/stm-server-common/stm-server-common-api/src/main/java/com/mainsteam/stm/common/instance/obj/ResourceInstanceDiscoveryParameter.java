/**
 * 
 */
package com.mainsteam.stm.common.instance.obj;

import java.util.Map;

/**
 * 资源实例发现信息
 * 
 * @author ziw
 *
 */
public class ResourceInstanceDiscoveryParameter {

	/**
	 * 发现输入信息
	 */
	private Map<String,String> discoveryInfos;
	
	/**
	 * 模型id
	 */
	private String resourceId;
	
	/**
	 * 发现方式
	 */
	private String discoveryWay;
	
	/**
	 * 节点分组ID
	 */
	private int nodeGroupId;
	
	/**
	 * 发现实例域
	 */
	private long domainId;
	
	/**
	 * 是否用来发现网络设备，而且自动匹配网络设备模型。
	 */
	private boolean anonymousNetworkDevice = false;
	
	/**
	 * false 发现资源后入库，true 只做发现
	 */
	private boolean onlyDiscover = false;
	
	/**
	 * 用于拓扑发现传入资源实例Id
	 */
	private long resourceInstanceId;
	
	/**
	 * 核心节点ip(只针对网络设备)
	 */
	private String coreNodeIp;
	/**
	 * 
	 */
	public ResourceInstanceDiscoveryParameter() {
	}

	public Map<String, String> getDiscoveryInfos() {
		return discoveryInfos;
	}

	public void setDiscoveryInfos(Map<String, String> discoveryInfos) {
		this.discoveryInfos = discoveryInfos;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getDiscoveryWay() {
		return discoveryWay;
	}

	public void setDiscoveryWay(String discoveryWay) {
		this.discoveryWay = discoveryWay;
	}
	
	public int getNodeGroupId() {
		return nodeGroupId;
	}

	public void setNodeGroupId(int nodeGroupId) {
		this.nodeGroupId = nodeGroupId;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	/**
	 * @return the anonymousNetworkDevice
	 */
	public final boolean isAnonymousNetworkDevice() {
		return anonymousNetworkDevice;
	}

	/**
	 * @param anonymousNetworkDevice the anonymousNetworkDevice to set
	 */
	public final void setAnonymousNetworkDevice(boolean anonymousNetworkDevice) {
		this.anonymousNetworkDevice = anonymousNetworkDevice;
	}

	public boolean isOnlyDiscover() {
		return onlyDiscover;
	}

	public void setOnlyDiscover(boolean onlyDiscover) {
		this.onlyDiscover = onlyDiscover;
	}

	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	public String getCoreNodeIp() {
		return coreNodeIp;
	}

	public void setCoreNodeIp(String coreNodeIp) {
		this.coreNodeIp = coreNodeIp;
	}
}
