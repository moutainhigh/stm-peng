package com.mainsteam.stm.portal.config.collector.mbean.bean;

import java.io.Serializable;
/**
 * 
 * <li>文件名称: ConfigReq.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月16日
 * @author   liupeng
 */
public class ConfigReq implements Serializable {
	private static final long serialVersionUID = 9220672530700010978L;
	private String ip;
	private String userName;
	private String password;
	private String fileName;
	private String cmd;
	private String enableUserName;
	private String enablePassword;
	
	public ConfigReq() {
		super();
	}
	
	public ConfigReq(String ip, String userName, String password,
			String fileName, String cmd, String enableUserName,
			String enablePassword) {
		this.ip = ip;
		this.userName = userName;
		this.password = password;
		this.fileName = fileName;
		this.cmd = cmd;
		this.enableUserName = enableUserName;
		this.enablePassword = enablePassword;
	}

	public String getCmd() {
		return cmd==null?"":cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getEnableUserName() {
		return enableUserName==null?"":enableUserName;
	}
	public void setEnableUserName(String enableUserName) {
		this.enableUserName = enableUserName;
	}
	public String getEnablePassword() {
		return enablePassword==null?"":enablePassword;
	}
	public void setEnablePassword(String enablePassword) {
		this.enablePassword = enablePassword;
	}
	public String getIp() {
		return ip==null?"":ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUserName() {
		return userName==null?"":userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password==null?"":password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFileName() {
		return fileName==null?"":fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
