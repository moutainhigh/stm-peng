package com.mainsteam.stm.system.auditlog.bo;

/**
 * <li>文件名称: com.mainsteam.stm.system.auditlog.bo.MethodEntity.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年12月15日
 */
public class MethodEntity{
	private String key;	//主键
	private String moduleName;	//模块名
	private String moduleId;	//模块ID
	private String type;	//操作类型
	private String description;	//描述
	private Boolean isAfter = true;	//是否在方法执行之前(默认在方法执行之后)
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getIsAfter() {
		return isAfter;
	}
	public void setIsAfter(Boolean isAfter) {
		this.isAfter = isAfter;
	}
}
