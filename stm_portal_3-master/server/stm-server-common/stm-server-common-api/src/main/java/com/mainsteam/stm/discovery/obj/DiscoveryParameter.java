/**
 * 
 */
package com.mainsteam.stm.discovery.obj;

import java.io.Serializable;
import java.util.Map;

/**
 * 发现参数
 * 
 * @author ziw
 *
 */
public class DiscoveryParameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4152735857576147952L;

	public static final String IP_KEY = "IP";
	
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
	 * 是否用来发现网络设备，而且自动匹配网络设备模型。
	 */
	private boolean anonymousNetworkDevice = false;
	
	/**
	 * 资源发现实例域
	 */
	private long domainId;
	
	/**
	 * 
	 */
	public DiscoveryParameter() {
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
		if(null != discoveryWay){
			this.discoveryWay = discoveryWay.toUpperCase();
		}
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
}
