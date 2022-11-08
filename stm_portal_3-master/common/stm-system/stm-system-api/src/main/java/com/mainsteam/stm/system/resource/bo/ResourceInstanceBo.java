package com.mainsteam.stm.system.resource.bo;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

/**
 * <li>文件名称: ResourceInstanceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月11日
 * @author   ziwenwen
 */
public class ResourceInstanceBo implements Serializable{
	private static final long serialVersionUID = 3375316548520750920L;
	private Long id;
	private Long domainId;
	private Long userId;
	private String name;
	private String discoverNode;
	private DiscoverWayEnum discoverWay;
	private InstanceLifeStateEnum lifeState;
	private String resourceId;
	private String categoryId;
	private String discoverIP;
	private Long parentId;
	private String showName;
	private ResourceInstance ri;
	
	public ResourceInstance src(){
		return ri;
	}
	
	public void setSrc(ResourceInstance src){
		this.ri=src;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getDiscoverIP() {
		return discoverIP;
	}
	public void setDiscoverIP(String discoverIP) {
		this.discoverIP = discoverIP;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getDiscoverNode() {
		return discoverNode;
	}
	public void setDiscoverNode(String discoverNode) {
		this.discoverNode = discoverNode;
	}
	public DiscoverWayEnum getDiscoverWay() {
		return discoverWay;
	}
	public void setDiscoverWay(DiscoverWayEnum discoverWay) {
		this.discoverWay = discoverWay;
	}
	public InstanceLifeStateEnum getLifeState() {
		return lifeState;
	}
	public void setLifeState(InstanceLifeStateEnum lifeState) {
		this.lifeState = lifeState;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	@Override
	public int hashCode() {
		return id.intValue();
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((ResourceInstanceBo)obj).getId().longValue()==this.getId();
	}
}


