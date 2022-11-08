package com.mainsteam.stm.portal.vm.bo;

import java.util.List;

/**
 * <li>文件名称: VmCategoryResourceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 模型和资源bo</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年4月8日
 * @author   caoyong
 */
public class VmCategoryResourceBo {
	
	private Long instanceId;

	private Long instancePid;

	private String resourceId;
	
	private String resourcePid;

	private String categoryId;
	
	private String categoryPid;
	
	private String metricId;
	
	private String name;
	
	private String id;
	
	private List<VmCategoryResourceBo> childrens;

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public Long getInstancePid() {
		return instancePid;
	}

	public void setInstancePid(Long instancePid) {
		this.instancePid = instancePid;
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

	public String getResourcePid() {
		return resourcePid;
	}

	public void setResourcePid(String resourcePid) {
		this.resourcePid = resourcePid;
	}

	public List<VmCategoryResourceBo> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<VmCategoryResourceBo> childrens) {
		this.childrens = childrens;
	}

	public String getCategoryPid() {
		return categoryPid;
	}

	public void setCategoryPid(String categoryPid) {
		this.categoryPid = categoryPid;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VmCategoryResourceBo other = (VmCategoryResourceBo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
