package com.mainsteam.stm.system.um.resourcegroup.web.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.resourcegroup.api.IResourceGroupApi;
import com.mainsteam.stm.system.um.resourcegroup.bo.DomainResourceGroupRel;

@Controller
@RequestMapping("system/resourceGroup")
public class ResourceGroupAction extends BaseAction {

	@Autowired
	private IResourceGroupApi resourceGroupApi;
	
	/**
	* @Title: queryAllResourceGroup
	* @Description: 分页查询所有资源组信息
	* @param page
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryAllResourceGroup")
	public JSONObject queryAllResourceGroup(Page<DomainResourceGroupRel, DomainResourceGroupRel> page){
		ILoginUser user = getLoginUser();
		if(user.isDomainUser()){
			DomainResourceGroupRel condition = new DomainResourceGroupRel();
			Set<IDomain> domains = getLoginUser().getDomainManageDomains();
			List<Long> domainIds = new ArrayList<Long>();
			if(domains!=null && domains.size()>0){
				for (IDomain iDomain : domains) {
					domainIds.add(iDomain.getId());
				}
			}
			condition.setDomainIds(domainIds.toArray(new Long[]{}));
			page.setCondition(condition);
		}
		resourceGroupApi.queryResourceGroupPage(page);
		return toSuccess(page);
	}
	
	@RequestMapping("getResourceGroup")
	public JSONObject getResourceGroup(long groupId){
		DomainResourceGroupRel rel = resourceGroupApi.getResourceGroupRel(groupId);
		return toSuccess(rel);
	}
	
	@RequestMapping("insertResourceGroup")
	public JSONObject insertResourceGroup(DomainResourceGroupRel rel){
		try {
			rel.setEntryId(getLoginUser().getId());
			return toSuccess(resourceGroupApi.addResourceGroup(rel));
		} catch (Exception e) {
			e.printStackTrace();
			return toSuccess(e.getMessage());
		}
	}
	
	@RequestMapping("updateResourceGroup")
	public JSONObject updateResourceGroup(DomainResourceGroupRel rel){
		DomainResourceGroupRel resourceGroupRel = resourceGroupApi.getResourceGroupRel(rel.getId());
		resourceGroupRel.setName(rel.getName());
		resourceGroupRel.setResourceInstanceIds(rel.getResourceInstanceIds());
		resourceGroupRel.setDomainId(rel.getDomainId());
		return toSuccess(resourceGroupApi.updateResourceGroup(resourceGroupRel));
	}
	
	@RequestMapping("deleteResourceGroup")
	public JSONObject deleteResourceGroup(long[] groupIds){
		return toSuccess(resourceGroupApi.delResourceGroup(groupIds));
	}
	
	@RequestMapping("queryAllResourceGroupByDomain")
	public JSONObject queryAllResourceGroupByDomain(long domainId){
		return toSuccess(resourceGroupApi.queryAllGroupByDomain(domainId));
	}
}
