package com.mainsteam.stm.system.um.resourcegroup.service.impl;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.portal.resource.bo.CustomGroupEnumType;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.resourcegroup.api.IResourceGroupApi;
import com.mainsteam.stm.system.um.resourcegroup.bo.DomainResourceGroupRel;
import com.mainsteam.stm.system.um.resourcegroup.dao.IResourceGroupDao;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;

@Service("resourceGroupApi")
public class ResourceGroupImpl implements IResourceGroupApi {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ResourceGroupImpl.class);

	@Resource
	private IResourceGroupDao resourceGroupDao;
	
	@Resource(name="customResourceGroupApi")
	private ICustomResourceGroupApi resourceGroupApi;
	
	@Resource(name="stm_system_DomainApi")
	private IDomainApi domainApi;
	
	@Resource(name="stm_system_userApi")
	private IUserApi userApi;
	
	@Override
	public boolean addDomainResourceGroupRel(long domainId, long groupId) {
		if (logger.isDebugEnabled()) {
			logger.debug("addDomainResourceGroupRel(long, long) - start"); //$NON-NLS-1$
		}

		DomainResourceGroupRel rel = new DomainResourceGroupRel();
		rel.setId(groupId);
		rel.setDomainId(domainId);
		boolean returnboolean = resourceGroupDao.insert(rel) > 0 ? true : false;
		if (logger.isDebugEnabled()) {
			logger.debug("addDomainResourceGroupRel(long, long) - end"); //$NON-NLS-1$
		}
		return returnboolean;
	}

	@Override
	public boolean addResourceGroup(DomainResourceGroupRel rel) {
		if (logger.isDebugEnabled()) {
			logger.debug("addResourceGroup(DomainResourceGroupRel) - start"); //$NON-NLS-1$
		}

		rel.setGroupType(CustomGroupEnumType.DOMAIN);//设置资源组类型为与域关联
		rel.setEntryDatetime(Calendar.getInstance().getTime());
		int result = resourceGroupApi.insert(rel);
		if(result>0){
			this.addDomainResourceGroupRel(rel.getDomainId(), result);
		}else if(result==-1){
			logger.debug("addResourceGroup(DomainResourceGroupRel): groupName-"+rel.getName()+"is exsit");
			throw new RuntimeException("group name is exsit");
		}
		boolean returnboolean = result > 0 ? true : false;
		if (logger.isDebugEnabled()) {
			logger.debug("addResourceGroup(DomainResourceGroupRel) - end"); //$NON-NLS-1$
		}
		return returnboolean;
	}

	@Override
	public String delResourceGroup(long[] groupIds) {
		if (logger.isDebugEnabled()) {
			logger.debug("delResourceGroup(long[]) - start"); //$NON-NLS-1$
		}

		String isRelGroup = "";
		if(groupIds!=null){
			List<Long> isDeleteGroupIds = new ArrayList<>();
			for (long groupId : groupIds) {
				if(userApi.checkResourceGroupIsRelUser(groupId)){
					isRelGroup+= groupId+";";
				}else{
					isDeleteGroupIds.add(groupId);
				}
			}
			if(!StringUtils.isEmpty(isRelGroup)){
				isRelGroup = isRelGroup.substring(0,isRelGroup.length()-1);
			}
			
			if(isDeleteGroupIds!=null && isDeleteGroupIds.size()>0){
				long[] delIds = new long[isDeleteGroupIds.size()];
				for(int i=0;i<isDeleteGroupIds.size();i++){
					delIds[i]=isDeleteGroupIds.get(i);
				}
				
				resourceGroupDao.batchDel(delIds);//删除资源组与域的关联关系
				for (long id : isDeleteGroupIds) {
					resourceGroupApi.del(id);//删除资源组信息
				}
			}
			
		}
		return isRelGroup;
	}

	@Override
	public boolean updateResourceGroup(DomainResourceGroupRel rel) {
		if (logger.isDebugEnabled()) {
			logger.debug("updateResourceGroup(DomainResourceGroupRel) - start"); //$NON-NLS-1$
		}

		//调用resource Service更新资源组信息
		boolean returnboolean = resourceGroupApi.update(rel) > 0 ? true : false;
		long[] ids = new long[]{rel.getId()}; 
		resourceGroupDao.batchDel(ids);//删除域与组的关联关系
		this.addDomainResourceGroupRel(rel.getDomainId(), rel.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("updateResourceGroup(DomainResourceGroupRel) - end"); //$NON-NLS-1$
		}
		return returnboolean;
	}

	@Override
	public List<DomainResourceGroupRel> queryAllGroupByDomain(long domainId) {
		if (logger.isDebugEnabled()) {
			logger.debug("queryAllGroupByDomain(long) - start"); //$NON-NLS-1$
		}

		List<DomainResourceGroupRel> groupRels = resourceGroupDao.queryRelByDomain(domainId);
		if(groupRels!=null){
			for (DomainResourceGroupRel rel : groupRels) {
				CustomGroupBo groupBo = resourceGroupApi.getCustomGroup(rel.getId());
				BeanUtils.copyProperties(groupBo, rel);
				Domain domain = domainApi.get(rel.getDomainId());
				rel.setDomainId(domain.getId());
				rel.setDomainName(domain.getName());
				if(rel.getEntryId()!=null){
					User user = userApi.get(rel.getEntryId());
					rel.setCreateUserName(user.getName());
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("queryAllGroupByDomain(long) - end"); //$NON-NLS-1$
		}
		return groupRels;
	}

	@Override
	public List<DomainResourceGroupRel> queryResourceGroupPage(
			Page<DomainResourceGroupRel, DomainResourceGroupRel> page) {
		if (logger.isDebugEnabled()) {
			logger.debug("queryResourceGroupPage(Page<DomainResourceGroupRel,DomainResourceGroupRel>) - start"); //$NON-NLS-1$
		}

		List<DomainResourceGroupRel> groupRels = resourceGroupDao.pageSelect(page);
		if(groupRels!=null){
			for (DomainResourceGroupRel rel : groupRels) {
				CustomGroupBo groupBo = resourceGroupApi.getCustomGroup(rel.getId());
				BeanUtils.copyProperties(groupBo, rel);
				Domain domain = domainApi.get(rel.getDomainId());
				if(domain!=null){
					rel.setDomainId(domain.getId());
					rel.setDomainName(domain.getName());
				}
				if(rel.getEntryId()!=null){
					User user = userApi.get(rel.getEntryId());
					if(user!=null){
						rel.setCreateUserName(user.getName());
					}
				}
			}
			page.setDatas(groupRels);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("queryResourceGroupPage(Page<DomainResourceGroupRel,DomainResourceGroupRel>) - end"); //$NON-NLS-1$
		}
		return groupRels;
	}

	@Override
	public DomainResourceGroupRel getResourceGroupRel(long groupId) {
		if (logger.isDebugEnabled()) {
			logger.debug("getResourceGroupRel(long) - start"); //$NON-NLS-1$
		}

		DomainResourceGroupRel resourceGroupRel = resourceGroupDao.get(groupId);
		if(resourceGroupRel!=null){
			CustomGroupBo groupBo = resourceGroupApi.getCustomGroup(resourceGroupRel.getId());
			BeanUtils.copyProperties(groupBo, resourceGroupRel);
			Domain domain = domainApi.get(resourceGroupRel.getDomainId());
			resourceGroupRel.setDomainId(domain.getId());
			resourceGroupRel.setDomainName(domain.getName());
			if(resourceGroupRel.getEntryId()!=null){
				User user = userApi.get(resourceGroupRel.getEntryId());
				resourceGroupRel.setCreateUserName(user.getName());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getResourceGroupRel(long) - end"); //$NON-NLS-1$
		}
		return resourceGroupRel;
	}
}
