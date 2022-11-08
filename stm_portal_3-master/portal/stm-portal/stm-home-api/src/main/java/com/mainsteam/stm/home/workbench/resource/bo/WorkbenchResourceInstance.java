/**
 * 
 */
package com.mainsteam.stm.home.workbench.resource.bo;

/**
 * 
 * <li>文件名称:ResourceInstance.java</li>
 * <li>公 司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有:版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2017年3月23日 上午10:56:26
 * @author tandl
 */
public class WorkbenchResourceInstance {
	
	private static final long serialVersionUID = 2810802187720606332L;
	
	private long id;
	private String showName;
	private String discoverIP;
	private String resourceId;
	private String resourceName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getDiscoverIP() {
		return discoverIP;
	}

	public void setDiscoverIP(String discoverIP) {
		this.discoverIP = discoverIP;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

}
