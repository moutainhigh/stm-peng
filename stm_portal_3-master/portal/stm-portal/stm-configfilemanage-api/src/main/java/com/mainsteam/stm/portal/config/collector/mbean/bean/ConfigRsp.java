package com.mainsteam.stm.portal.config.collector.mbean.bean;

import java.io.Serializable;
/**
 * 
 * <li>文件名称: CfgBean.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   liupeng
 */
public class ConfigRsp implements Serializable{
	private static final long serialVersionUID = -3962806823598372645L;
	
	public ConfigRsp() {}
	/**
	 * 配置文件
	 */
	private String file;
	/**
	 * 远程端输出打印信息
	 */
	private String remoteInfo;
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getRemoteInfo() {
		return remoteInfo;
	}
	public void setRemoteInfo(String remoteInfo) {
		this.remoteInfo = remoteInfo;
	}
	
}
