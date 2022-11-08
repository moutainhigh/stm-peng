package com.mainsteam.stm.home.layout.bo;
/**
 * 页面布局
 * <li>文件名称: HomeLayoutBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年6月13日 
 * @author   zhouw
 */
public class HomeDefaultInterfaceBo {
	/**
	 * ID
	 */
	private long id;
	/**
	 * 用户ID
	 */
	private long userId;
	/**
	 * 资源ID
	 */
	private long resourceId;
	/**
	 * 默认网络接口ID
	 */
	private long defaultInterfaceId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public long getDefaultInterfaceId() {
		return defaultInterfaceId;
	}
	public void setDefaultInterfaceId(long defaultInterfaceId) {
		this.defaultInterfaceId = defaultInterfaceId;
	}
	
	@Override
	public String toString() {
		return "HomeDefaultInterfaceBo [id=" + id + ", userId=" + userId
				+ ", resourceId=" + resourceId + ", defaultInterfaceId="
				+ defaultInterfaceId + "]";
	}
}
