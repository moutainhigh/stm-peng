package com.mainsteam.stm.simple.search.bo;

/**
 * <li>文件名称: ModuleResourceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class ModuleResourceBo {
	private Long resourceId;
	
	/**
	 * 可取资源管理、拓扑管理、配置文件管理、机房管理、巡查管理、知识库
	 */
	private String moduleName;
	
	/**
	 * 在模块中的业务名称
	 */
	private String name;
	
	private String ip;

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
}


