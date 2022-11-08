/**
 * 
 */
package com.mainsteam.stm.platform.system.config.bean;

/**
 * <li>文件名称: SystemConfigConstantEnum</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 系统配置文件ID占用Enum</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日 下午2:22:01
 * @author   俊峰
 */
public enum SystemConfigConstantEnum {

	SYSTEM_CONFIG_MESSAGE_SETTING_CFG(1,"系统告警设置配置文件"),
	SYSTEM_CONFIG_SYSTEMIMAGE_CFG(2,"系统图片管理配置文件"),
	SYSTEM_CONFIG_ACCESS_CONTROL_CFG(3,"访问控制配置文件"),
	SYSTEM_CONFIG_LOG_SYSLOG(4,"syslog端口号"),
	SYSTEM_CONFIG_LOG_SNMPTRAP(5,"snmptrap端口号"),
	SYSTEM_CONFIG_FAULT_KNOWLEDGE_DOWNLOAD_ADDR(6,"故障知识下载地址"),
	SYSTEM_CONFIG_CAPACITY_KNOWLEDGE_DOWNLOAD_ADDR(7,"能力知识下载地址"),
	SYSTEM_CONFIG_AUTHENTICATION_CFG(8,"系统管理，认证方式配置文件"),
	SYSTEM_CONFIG_SKIN(9,"皮肤"),
	SYSTEM_CONFIG_BIGDATA(10,"数智联大数据配置"),
	SYSTEM_CONFIG_ITSM(11,"ITSM配置"),
	SYSTEM_CONFIG_ITSM_ALARM_WEBSERVICE(12,"ITSM webservice 告警接口配置"),
	SYSTEM_CONFIG_CMDB_WEBSERVICE(13,"cmdb webservice 同步接口配置"),
	SYSTEM_CONFIG_SIMPLE_MODE(14,"极简模式状态配置文件");
	private long cfgId;
	private String cfgDesc;
	
	SystemConfigConstantEnum(long id,String description){
		this.cfgId = id;
		this.cfgDesc = description;
	}

	public long getCfgId() {
		return cfgId;
	}

	public void setCfgId(long cfgId) {
		this.cfgId = cfgId;
	}

	public String getCfgDesc() {
		return cfgDesc;
	}

	public void setCfgDesc(String cfgDesc) {
		this.cfgDesc = cfgDesc;
	}
	
}
