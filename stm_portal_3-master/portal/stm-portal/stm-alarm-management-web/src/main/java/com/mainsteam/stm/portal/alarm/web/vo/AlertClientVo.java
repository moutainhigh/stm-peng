package com.mainsteam.stm.portal.alarm.web.vo;

import java.io.Serializable;

/**
 * <li>文件名称: AlertClientVo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年11月
 * @author xhf
 */
public class AlertClientVo implements Serializable{

	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = -3595971192698525713L;
	
	/**
	 * 告警ID
	 */
	private long seq;
	
	/**
	 * 告警级别
	 */
	private int serverity;
	
	/**
	 * 设备名称
	 */
	private String hostname;
	/**
	 * 设备类型
	 */
	private String devicetype;
	
	/**
	 * IP地址
	 */
	private String ipaddr;

	/**
	 * 首次发生时间
	 */
	private long firstoccur;
	
	/**
	 * 管理域
	 */
	private String domain;
	
	/**
	 * 告警消息
	 */
	private String message;
	
	/**
	 * 相关URL
	 */
	private String url;

	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

	public int getServerity() {
		return serverity;
	}

	public void setServerity(int serverity) {
		this.serverity = serverity;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIpaddr() {
		return ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}

	public long getFirstoccur() {
		return firstoccur;
	}

	public void setFirstoccur(long firstoccur) {
		this.firstoccur = firstoccur;
	}

	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}
	
	
	
}
