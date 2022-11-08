package com.mainsteam.stm.simple.engineer.workbench.bo;

import java.io.Serializable;

/**
 * <li>文件名称: RemoteAcessBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 远程访问协议对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class RemoteAcessBo implements Serializable{
	
	private static final long serialVersionUID = -4174832008541415582L;
	
	/**自动修复协议类型*/
	public static final int REPAIR_TYPE_TELENT = 1;
	public static final int REPAIR_TYPE_SSH = 2;
	public static final int REPAIR_TYPE_WMI = 3;
	
	/**故障解决方案ID*/
	private long resolveId;
	
	/**要修复的告警ID*/
	private long eventId;
	/**自动修复协议类型*/
	private int repairType;
	/**目标IP地址*/
	private String ip;
	/**目标端口*/
	private String port;
	/**目标主机登录账户*/
	private String account;
	/**目标主机登录密码*/
	private String password;
	public int getRepairType() {
		return repairType;
	}
	public void setRepairType(int repairType) {
		this.repairType = repairType;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public long getResolveId() {
		return resolveId;
	}
	public void setResolveId(long resolveId) {
		this.resolveId = resolveId;
	}
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	
}


