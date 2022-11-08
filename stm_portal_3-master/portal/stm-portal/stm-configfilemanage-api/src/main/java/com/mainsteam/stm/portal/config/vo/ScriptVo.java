package com.mainsteam.stm.portal.config.vo;

import java.io.Serializable;
/**
 * 脚本
 * <li>文件名称: Script.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月11日
 * @author   tongpl
 */
public class ScriptVo{
	
	/**
	 * 脚本内容
	 */
	private String cmd;
	/**
	 * 脚本类型(1:tftp 2:show)
	 */
	private String type;
	/**
	 * 配置文件名称
	 */
	private String fileName;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
