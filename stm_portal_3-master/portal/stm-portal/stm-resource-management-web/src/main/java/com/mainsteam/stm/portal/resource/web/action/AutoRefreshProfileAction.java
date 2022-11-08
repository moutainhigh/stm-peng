package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.AutoRefreshProfileApi;
import com.mainsteam.stm.portal.resource.web.vo.AutoRefreshProfileInstance;
import com.mainsteam.stm.portal.resource.web.vo.AutoRefreshProfileInstancePage;
import com.mainsteam.stm.portal.resource.web.vo.ProfilelibAutoRediscoverPage;
import com.mainsteam.stm.portal.resource.web.vo.ProfilelibAutoRediscoverVo;
import com.mainsteam.stm.profilelib.ProfileAutoRediscoverService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscoverInstance;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.user.api.IUserApi;

/**
 * <li>文件名称: ProfileAction.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: 策略</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2016年4月
 * @author pl
 */
@Controller
@RequestMapping("portal/autoRefresh")
public class AutoRefreshProfileAction extends BaseAction {

	private static Logger logger = Logger.getLogger(AutoRefreshProfileAction.class);
	
	@Autowired
	private AutoRefreshProfileApi autoRefreshProfileApi;
	
	@Autowired
	private ProfileAutoRediscoverService profileAutoRediscoverService;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	@Autowired
	private IUserApi userApi;
	
	private static final int MAX_AUTO_REFRESH_INSTANCE_COUNT = 50;
	
	@Value("${stm.resource.auto.refresh.MaxCount}")
	private int instanceMaxCount;
	
	/**
	 * 获取自动刷新策略列表
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/getProfileList")
	public JSONObject getDefaultStrategyAll(ProfilelibAutoRediscoverPage page,HttpSession session){
		
		List<ProfilelibAutoRediscover> allProfile = autoRefreshProfileApi.getAutoProfileList(getLoginUser(session));
		
		if(allProfile == null || allProfile.size() <= 0){
			return toSuccess(page);
		}
		
		List<ProfilelibAutoRediscover> resultProfile = new ArrayList<ProfilelibAutoRediscover>();
		
		page.setTotalRecord(allProfile.size());
		
		if((page.getStartRow() + page.getRowCount()) > allProfile.size()){
			resultProfile = allProfile.subList((int)page.getStartRow(), allProfile.size());
		}else{
			resultProfile = allProfile.subList((int)page.getStartRow(), (int)(page.getStartRow() + page.getRowCount()));
		}
		
		List<ProfilelibAutoRediscoverVo> result = new ArrayList<ProfilelibAutoRediscoverVo>();
		
		for(ProfilelibAutoRediscover profile : resultProfile){
			ProfilelibAutoRediscoverVo vo = new ProfilelibAutoRediscoverVo();
			BeanUtils.copyProperties(profile, vo);
			vo.setCreateUserName(userApi.get(profile.getCreateUser()).getName());
			
			result.add(vo);
		}
		
		page.setResources(result);
		
		return toSuccess(page);
	}
	
	/**
	 * 保存策略基本信息
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/saveAutoRefreshProfileBasic")
	public JSONObject saveAutoRefreshProfileBasic(ProfilelibAutoRediscover rediscover){
		
		try {
			if(profileAutoRediscoverService.checkProfileNameIsExist(rediscover.getProfileName())){
				//名称重复
				return toFailForGroupNameExsit("策略名称已存在！");
			}else{
				if(autoRefreshProfileApi.saveAutoRefreshProfileBasic(rediscover) > 0){
					return toSuccess(rediscover);
				}else{
					return null;
				}
			}
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		
		
	}
	
	/**
	 * 修改策略基本信息
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/updateAutoRefreshProfileBasic")
	public JSONObject updateAutoRefreshProfileBasic(ProfilelibAutoRediscover rediscover){
		
		ProfilelibAutoRediscover oldProfile = autoRefreshProfileApi.getProfilelibDetail(rediscover.getId());
		
		rediscover.setCreateTime(oldProfile.getCreateTime());
		
		try {
			if(!(oldProfile.getProfileName().equals(rediscover.getProfileName())) && profileAutoRediscoverService.checkProfileNameIsExist(rediscover.getProfileName())){
				//名称重复
				return toFailForGroupNameExsit("策略名称已存在！");
			}else{
				if(autoRefreshProfileApi.updateAutoRefreshProfileBasic(rediscover) > 0){
					
					ProfilelibAutoRediscoverVo vo = new ProfilelibAutoRediscoverVo();
					
					BeanUtils.copyProperties(rediscover, vo);
					
					if(rediscover.getUpdateUser() != null && rediscover.getUpdateUser() > 0){
						vo.setUpdateUserName(userApi.get(rediscover.getUpdateUser()).getName());
					}
					
					return toSuccess(vo);
				}else{
					return null;
				}
			}
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		
		
	}
	
	/**
	 * 更新策略状态
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/updateProfileStatus")
	public JSONObject updateProfileStatus(long profileId,HttpSession session){
		
		return toSuccess(autoRefreshProfileApi.updateUseStatus(getLoginUser(session).getId(), profileId));
		
	}
	
	/**
	 * 更新策略状态
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/updateProfileResourceRelation")
	public JSONObject updateProfileResourceRelation(long profileId,String instanceIds){
		
		List<Long> instanceIdList = new ArrayList<Long>();
		
		for(String instanceId : instanceIds.split(",")){
			if(instanceId == null || instanceId.equals("") || instanceId.trim().equals("")){
				continue;
			}
			instanceIdList.add(Long.parseLong(instanceId));
		}
		
		List<ProfilelibAutoRediscoverInstance> nowInstance = profileAutoRediscoverService.getAutoRediscoverProfile(profileId).getProfileInstanceRel();
		
		int curProfileInstanceCount = 0;
		
		if(nowInstance != null && nowInstance.size() > 0){
			curProfileInstanceCount = nowInstance.size();
		}
		
		if(instanceMaxCount <= 0){
			instanceMaxCount = AutoRefreshProfileAction.MAX_AUTO_REFRESH_INSTANCE_COUNT;
		}
		
		if(profileAutoRediscoverService.countProfileInstanceSize() - curProfileInstanceCount + instanceIdList.size() > instanceMaxCount){
			return toJsonObject(202, "自动刷新资源数量超过" + instanceMaxCount + "个!");
		}
		
		return toSuccess(autoRefreshProfileApi.updateProfileResouces(profileId, instanceIdList));
		
	}
	
	/**
	 * 更新策略状态
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/delelteProfile")
	public JSONObject delelteProfile(String ids){
		
		List<Long> profileIdList = new ArrayList<Long>();
		
		for(String id : ids.split(",")){
			profileIdList.add(Long.parseLong(id));
		}
		
		return toSuccess(autoRefreshProfileApi.removeProfile(profileIdList));
		
	}
	
	/**
	 * 获取策略详情
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/getAutoProfileDetail")
	public JSONObject getAutoProfileDetail(long profileId){
		
		ProfilelibAutoRediscover profile = autoRefreshProfileApi.getProfilelibDetail(profileId);
		
		ProfilelibAutoRediscoverVo vo = new ProfilelibAutoRediscoverVo();
		
		BeanUtils.copyProperties(profile, vo);
		
		vo.setCreateUserName(userApi.get(profile.getCreateUser()).getName());
		if(profile.getUpdateUser() != null && profile.getUpdateUser() > 0){
			vo.setUpdateUserName(userApi.get(profile.getUpdateUser()).getName());
		}
		
		return toSuccess(vo);
		
	}
	
	/**
	 * 获取资源类别
	 * @return
	 */
	@RequestMapping("/getResourceCategory")
	public JSONObject getResourceCategory(){
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		try {
			CategoryDef categoryDef = capacityService.getRootCategory();
			//一级菜单
			CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
			if(baseCategoryDef != null && baseCategoryDef.length > 0){
				for (CategoryDef c : baseCategoryDef) {
					if(c.getId().equals(CapacityConst.STANDARDSERVICE)){
						//过滤标准服务类型
						continue;
					}
					if(!licenseCapacityCategory.isAllowCategory(c.getId())){
						continue;
					}
					if(c.isDisplay()){
						Map<String,String> result  =  new HashMap<String,String>();
						result.put("id",c.getId() );
						result.put("name", c.getName());
						baseList.add(result);
					}
				}
			
			  }
			} catch (Exception e) {
				logger.error(e.getMessage());
			
			}
		return toSuccess(baseList);
	}
	
	
	/**
	 * 分页获取所有符合条件的资源
	 * @param
	 * @return
	 */
	@RequestMapping("/getResourceInstances")
	public JSONObject getResourceInstances(String categoryId,Long domainId,Long profileId,Integer start,String searchContent,Integer pageSize,HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		AutoRefreshProfileInstancePage pageVo = new AutoRefreshProfileInstancePage();
		
		List<ResourceInstanceBo> resourceInstances = autoRefreshProfileApi.getResourceInstancesByCategory(categoryId,domainId,searchContent,user);
		
		pageVo.setTotalRecord(resourceInstances.size());
		
		//判断当前策略是否监控所有该资源类型下的设备
		boolean isSelectAll = true;
		for(ResourceInstanceBo instance : resourceInstances){
			ProfilelibAutoRediscover rediscoverProfile = profileAutoRediscoverService.getProfilelibAutoRediscoverByInstance(instance.getId());
			if(rediscoverProfile == null || rediscoverProfile.getId() != profileId){
				isSelectAll = false;
				break;
			}
		}
		
		List<AutoRefreshProfileInstance> voList = new ArrayList<AutoRefreshProfileInstance>();
		
		//分页处理
		List<ResourceInstanceBo> resultResourceInstance = new ArrayList<ResourceInstanceBo>();
		
		if((start + pageSize) > resourceInstances.size()){
			resultResourceInstance = resourceInstances.subList(start, resourceInstances.size());
		}else{
			resultResourceInstance = resourceInstances.subList(start, (start + pageSize));
		}
		
		for(ResourceInstanceBo instance : resultResourceInstance){

			AutoRefreshProfileInstance vo = new AutoRefreshProfileInstance();
			vo.setDomainId(instance.getDomainId());
			vo.setInstanceId(instance.getId());
			vo.setResourceName(instance.getResourceId());
			vo.setResourceIp(instance.getDiscoverIP());
			vo.setResourceShowName(instance.getShowName());
			ProfilelibAutoRediscover rediscoverProfile = profileAutoRediscoverService.getProfilelibAutoRediscoverByInstance(instance.getId());
			if(rediscoverProfile == null){
				vo.setProfileId(-1);
				vo.setProfileName("-");

			}else{
				vo.setProfileId(rediscoverProfile.getId());
				vo.setProfileName(rediscoverProfile.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		pageVo.setSelectAll(isSelectAll);
		pageVo.setResources(voList);
		
		return toSuccess(pageVo);
		
	}
	
	/**
	 * 获取所有符合条件的资源
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllResourceInstancesID")
	public JSONObject getAllResourceInstancesID(String categoryId,Long domainId,Long profileId,String searchContent,HttpSession session){
		
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstanceBo> resourceInstances = autoRefreshProfileApi.getResourceInstancesByCategory(categoryId,domainId,searchContent,user);
		
		List<Long> resourceIds = new ArrayList<Long>();
		
		for(ResourceInstanceBo bo : resourceInstances){
			resourceIds.add(bo.getId());
		}
		
		return toSuccess(resourceIds);
		
	}

}
