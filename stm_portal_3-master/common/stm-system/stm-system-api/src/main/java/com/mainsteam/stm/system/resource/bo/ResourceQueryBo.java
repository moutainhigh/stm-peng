package com.mainsteam.stm.system.resource.bo;

import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.resource.api.Filter;

/**
 * <li>文件名称: ResourceQueryBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月27日
 * @author   ziwenwen
 */
public class ResourceQueryBo {
	/**
	 * 用户id
	 */
	protected ILoginUser user;
	
	/**
	 * 域id
	 */
	private List<Long> domainIds;
	
	/**
	 * 资源种类id
	 */
	private List<String> categoryIds;
	
	/**
	 * 资源实例名称
	 */
	private String instanceName;
	
	/**
	 * 资源实例ip
	 */
	private String instanceIp;
	
	/**
	 * 模型id
	 */
	private String moduleId;
	
	private Filter filter;
	
	private InstanceLifeStateEnum lifeState;
	
	public InstanceLifeStateEnum getLifeState() {
		return lifeState;
	}

	public void setLifeState(String lifeState) {
		this.lifeState = InstanceLifeStateEnum.valueOf(lifeState);
	}

	private Collection<DiscoverWayEnum> discoverWayEnums;
	
	public ResourceQueryBo(ILoginUser user){
		this.user=user;
	}

	public List<Long> getDomainIds() {
		return domainIds;
	}

	public void setDomainIds(List<Long> domainIds) {
		this.domainIds = domainIds;
	}

	public String getInstanceIp() {
		return instanceIp;
	}

	public void setInstanceIp(String instanceIp) {
		this.instanceIp = instanceIp;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public ILoginUser getUser() {
		return user;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public List<String> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<String> categoryIds) {
		this.categoryIds = categoryIds;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Collection<DiscoverWayEnum> getDiscoverWayEnums() {
		return discoverWayEnums;
	}

	public void setDiscoverWayEnums(Collection<DiscoverWayEnum> discoverWayEnums) {
		this.discoverWayEnums = discoverWayEnums;
	}
}


