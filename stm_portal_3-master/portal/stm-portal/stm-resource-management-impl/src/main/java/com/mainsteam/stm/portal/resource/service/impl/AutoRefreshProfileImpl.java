package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.AutoRefreshProfileApi;
import com.mainsteam.stm.profilelib.ProfileAutoRediscoverService;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscoverInstance;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;

public class AutoRefreshProfileImpl implements AutoRefreshProfileApi {
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private IResourceApi stm_system_resourceApi;
	
	@Resource
	private ModulePropService modulePropService;
	
	@Autowired
	private ProfileAutoRediscoverService profileAutoRediscoverService;
	
	private static final Log logger = LogFactory.getLog(AutoRefreshProfileImpl.class);
	
	/**
	 * 根据资源模型类别获取资源实例
	 */
	@Override
	public List<ResourceInstanceBo> getResourceInstancesByCategory(String categoryId,Long domainId,String searchContent,ILoginUser user) {
		List<ResourceInstanceBo> resourceInstanceList = null;
		try {
			ResourceQueryBo queryBo = new ResourceQueryBo(user);
			List<String> categoryIds = new ArrayList<String>();
			categoryIds.add(categoryId);
			queryBo.setCategoryIds(categoryIds);
			resourceInstanceList = stm_system_resourceApi.getResources(queryBo);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		List<ResourceInstanceBo> resultResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(resourceInstanceList == null || resourceInstanceList.size() <= 0){
			
			return resultResourceInstanceList;
			
		}
		for(int j = 0 ; j < resourceInstanceList.size() ; j ++){
			
			ResourceInstanceBo instanceBo = resourceInstanceList.get(j);
			
			if(instanceBo == null){
				continue;
			}
			
			//判断自定义策略的域是否和资源实例相同
			if(domainId != -1 && !instanceBo.getDomainId().equals(domainId)){
				continue;
			}
			
			if(domainId == -1){
				Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
				boolean isContain = false;
				for(IDomain domain : domains){
					if(resourceInstanceList.get(j).getDomainId().longValue() == domain.getId()){
						isContain = true;
						break;
					}
				}
				
				if(!isContain){
					continue;
				}
				
			}
			
			//获取已监控
			if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
					|| instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
					|| instanceBo.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
				continue;
			}
			
			//过滤搜索关键字
			if(searchContent != null && !searchContent.equals("")){
				if(instanceBo.getShowName() != null && 
						!instanceBo.getShowName().toLowerCase().contains(searchContent.toLowerCase()) && 
						!instanceBo.getDiscoverIP().toLowerCase().contains(searchContent.toLowerCase())){
					//不匹配关键字
					continue;
				}
			}
			
			resultResourceInstanceList.add(resourceInstanceList.get(j));
			
		}
			
		return resultResourceInstanceList;
	}

	@Override
	public long saveAutoRefreshProfileBasic(ProfilelibAutoRediscover rediscover) {

		profileAutoRediscoverService.addAutoRediscoverProfileBaseInfo(rediscover);
		
		return rediscover.getId();
	}

	@Override
	public List<ProfilelibAutoRediscover> getAutoProfileList(ILoginUser user) {
		
		List<ProfilelibAutoRediscover> profileList = profileAutoRediscoverService.queryAllAutoRediscoverProfile();
		List<ProfilelibAutoRediscover> resultProfileList = new ArrayList<ProfilelibAutoRediscover>();
		
		if(user.isSystemUser()){
			return profileList;
		}
		
		Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
		
		for(ProfilelibAutoRediscover profile : profileList){
			
			long profileDomain = profile.getDomainId();
			boolean isContainDomain = false;
			for(IDomain domain : domains){
				if(profileDomain == domain.getId()){
					isContainDomain = true;
					break;
				}
			}
			
			if(isContainDomain){
				resultProfileList.add(profile);
			}
			
		}
		
		
		return resultProfileList;
	}

	@Override
	public boolean updateUseStatus(long userId,long profileId) {

		try {
			
			profileAutoRediscoverService.updateAutoRediscoverProfileUsedState(userId, profileId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}

		return true;
		
	}

	@Override
	public ProfilelibAutoRediscover getProfilelibDetail(long id) {
		// TODO Auto-generated method stub
		return profileAutoRediscoverService.getAutoRediscoverProfile(id);
	}
	
	@Override
	public long updateAutoRefreshProfileBasic(ProfilelibAutoRediscover rediscover) {

		profileAutoRediscoverService.updateAutoRediscoverProfileBaseInfo(rediscover);
		
		return rediscover.getId();
	}

	@Override
	public boolean updateProfileResouces(long id,List<Long> instanceIds) {
		
		try {
			
			List<ProfilelibAutoRediscoverInstance> instances = new ArrayList<ProfilelibAutoRediscoverInstance>();
			
			for(long instanceId : instanceIds){
				ProfilelibAutoRediscoverInstance instance = new ProfilelibAutoRediscoverInstance();
				instance.setInstanceId(instanceId);
				instance.setProfileId(id);
				
				instances.add(instance);
			}
			
			profileAutoRediscoverService.updateProfileAutoRediscoverInstanceRelByProfile(id, instances);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		
		return true;
	}

	@Override
	public boolean removeProfile(List<Long> ids) {
		
		try {
			profileAutoRediscoverService.deleteAutoRediscoverProfile(ids);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		
		return true;
	}
	

}
