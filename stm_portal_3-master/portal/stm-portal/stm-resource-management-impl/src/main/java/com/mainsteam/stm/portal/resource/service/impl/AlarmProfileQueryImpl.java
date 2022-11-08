package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricSummaryService;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.resource.api.IAlarmProfileQueryApi;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonEnableInfo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.constants.UserConstants;

public class AlarmProfileQueryImpl implements IAlarmProfileQueryApi{
	private static Logger logger = Logger.getLogger(AlarmProfileQueryImpl.class);
	@Resource
	private CapacityService capacityService;
	@Resource
	private AlarmRuleService alarmRuleService;
	@Resource
	private ProfileService profileService;
	@Resource
	private IUserApi stm_system_userApi;
	@Resource
	private MetricSummaryService  metricSummaryService;
	@Resource
	private MetricDataService metricDataService ;
	@Resource
	private IDomainApi domainApi;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	/**
	 * 获取所有维护人员用于告警规则添加
	 * 
	 * @return
	 */
	public void getUser(Page<User, User> page){
		
		List<User> list = stm_system_userApi.queryAllUserNoPage();
		
		page.setDatas(list);
	}
	
	/**
	 * 策略操作资源时,减少的域ID,根据减少的域ID删除,配置在策略上的告警接收人
	 * 
	 * @return
	 */
	public void filterUserByResourceDomainIdList(List<Long> domainIdList,Long profileId){
		if(domainIdList.size()==0){
			filterUserByResourceDomainIdArr(null,profileId);
		}else{
			Long[] ids = new Long[domainIdList.size()];
			domainIdList.toArray(ids);
			filterUserByResourceDomainIdArr(ids,profileId);
		}
		
	}
	
	public void filterUserByResourceDomainIdArr(Long[] domainIdArr,Long profileId){
		List<User> userList = new ArrayList<User>();
		List<AlarmRule> alarmRuleList = alarmRuleService.getAlarmRulesByProfileId(profileId, AlarmRuleProfileEnum.model_profile);
		
		if(null!=domainIdArr){
			userList = domainApi.queryUserByDomains(domainIdArr);
			List<User> listUserADMIN =stm_system_userApi.getUsersByType(UserConstants.USER_TYPE_SYSTEM_ADMIN);
			
			//策略下有资源的时候不能删除系统管理员,策略下资源全部移除,系统管理员也应该移除
			for(AlarmRule ar:alarmRuleList){
				boolean flag = true;
				Long userId = new Long(ar.getUserId());
				
				if(null!=listUserADMIN){
					for(User user:listUserADMIN){
						if(userId.longValue() == user.getId().longValue()){
						flag = false;
						break;
						}
					}
				}
				if(null!=userList && flag){
					for(User user:userList){
						if(userId.longValue() == user.getId().longValue()){
						flag = false;
						break;
						}
					}
				}
				if(flag){
					for(SendWayEnum se:SendWayEnum.values()){
						//delete 
						alarmRuleService.deleteAlarmCondition(ar.getId(),se);
					}
					
				}
			}
		}else{
			//domainIdArr为空,全部删除
			for(AlarmRule ar:alarmRuleList){
				for(SendWayEnum se:SendWayEnum.values()){
					//delete 
					alarmRuleService.deleteAlarmCondition(ar.getId(),se);
				}
			}
		}
		
		
		
	}
	
	/**
	 * 获取用户
	 * 
	 * @return
	 */
	public User getUserById(long userId){
		
		User user = stm_system_userApi.get(userId);
		
		return user;
	}
	
	/**
	 * 获取父级资源模型
	 * 
	 * @return
	 */
    public List<Map<String,String>> getParentCategory(){
    	
		CategoryDef categoryDef = capacityService.getRootCategory();
		
		//一级菜单
		CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		if(null != baseCategoryDef && baseCategoryDef.length > 0){
			for (CategoryDef c : baseCategoryDef) {
				if(!licenseCapacityCategory.isAllowCategory(c.getId())){
					continue;
				}
				Map<String,String> result  =  new HashMap<String,String>();
				result.put("id",c.getId() );
				result.put("name", c.getName());
				baseList.add(result);
			}
		}	
		
		return baseList;

	}
    
    /**
	 * 获取子资源模型
	 * 
	 * @return
	 */
	public List<Map<String,String>> getChildCategory(String profileID){
		
		CategoryDef categoryDef = capacityService.getCategoryById(profileID);
		
		//二级菜单
		CategoryDef[] childCategoryDef = categoryDef.getChildCategorys();
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		if(childCategoryDef != null && childCategoryDef.length > 0){
			
			for (CategoryDef c : childCategoryDef) {
				Map<String,String> result  =  new HashMap<String,String>();
				result.put("id",c.getId() );
				result.put("name", c.getName());
				baseList.add(result);
			}
		}
		
		return baseList;
	}
	
	/**
	 * 获取所有告警规则
	 * 
	 * @return
	 */
	public List<AlarmRule> getAllAlarmRules(String profileType){
		
		List<AlarmRule> list = alarmRuleService.getAllAlarmRules(getAlarmRuleProfileEnum(profileType));
		
	return list;
	
	}
	
	private AlarmRuleProfileEnum getAlarmRuleProfileEnum(String type){
		AlarmRuleProfileEnum arpf = AlarmRuleProfileEnum.model_profile ;
		for(AlarmRuleProfileEnum apf:AlarmRuleProfileEnum.values()){
			if(apf.name().equals(type)){
				arpf = apf;
				return arpf;
			}
		}
		return null;
	}
	
	/**
	 * 根据资源ID获取策略
	 * 
	 * @return
	 */
	public List<ProfileInfo> getProfileBasicInfoByResourceId(List<String> resourceIdList){
		List<ProfileInfo> list = new ArrayList<ProfileInfo>();
		try{
			list = profileService.getProfileBasicInfoByResourceIds(resourceIdList);
			return list;
			
		} catch (ProfilelibException e) {
			e.printStackTrace();
			return list;
		}
	}
	
	/**
	 * 根据策略ID获取资源实例Id
	 * 
	 * @return
	 */
	public List<Long> getProfileResourceInstance(long profileId){
		try {
			return profileService.getResourceInstanceByProfileId(profileId);
		} catch (ProfilelibException e) {
			return null;
		}
	}
	
	
	/**
	 * 只查询资源模型类别下的资源实例ID
	 * 
	 * @return
	 */
	public List<String> getChildCategoryIdList(String profileID){
		
		CategoryDef categoryDef = capacityService.getCategoryById(profileID);
		
		//二级菜单
		String[] resourceIds = categoryDef.getResourceIds();
		List<String> baseList = new ArrayList<String>();
		if(null != resourceIds && resourceIds.length > 0){
			for (String c : resourceIds) {
				baseList.add(c);
			}
		}
		return baseList;
	}
	
	/**
	 * 根据前台选择的资源模型,返回响应类型的策略
	 * 
	 * @return
	 */
	public List<ProfileInfo> getProfileInfoByChoseResource(String parentResourceId ,String childResourceId ){
		List<ProfileInfo> pfiAll = new ArrayList<ProfileInfo>();

		if (null != childResourceId && !"".equals(childResourceId)) {
			
			List<String> resourceIdList = new ArrayList<String>();
			resourceIdList.add(childResourceId);
			
			resourceIdList.addAll(getChildCategoryIdList(childResourceId)) ;
			
			pfiAll = getProfileBasicInfoByResourceId(resourceIdList);
			
		}else if (null != parentResourceId && !"".equals(parentResourceId)) {
			
			CategoryDef categoryDef = capacityService.getCategoryById(parentResourceId);
			
			//二级资源类别
			CategoryDef[] childCategoryDef = categoryDef.getChildCategorys();
			if(null != childCategoryDef  && childCategoryDef.length > 0){
				List<String> resourceIdList = new ArrayList<String>();
				for (CategoryDef c : childCategoryDef) {
					resourceIdList.addAll(getChildCategoryIdList(c.getId()));
				}
				pfiAll = getProfileBasicInfoByResourceId(resourceIdList);
			}	
		} else {
			//获取所有策略
			try{
				
				pfiAll = profileService.getAllProfileBasicInfo(null);
				
			}catch(ProfilelibException e){
				e.printStackTrace();
				return pfiAll;
			}
			
		}
		return pfiAll;
	}
	
	/**
	 * 根据资源ID获取告警规则
	 * 
	 * @return
	 */
	public List<AlarmRule> getAlarmRulesByProfileId(long profileId,String profileType){
		
		List<AlarmRule> list = alarmRuleService.getAlarmRulesByProfileId(profileId, getAlarmRuleProfileEnum(profileType));
		
	   return list;
	
	}
	
	/**
	 * 根据ruleID,type查询alarmSendCondition
	 * 
	 * @return
	 */
	public AlarmSendCondition getAlarmSendCondition(SendWayEnum type,long alarmRulesId){
		
		AlarmSendCondition as = alarmRuleService.getAlarmSendCondition(alarmRulesId, type);
		
		return as;
	}
	
	/**
	 * 添加alarmSendCondition
	 * 
	 * @return
	 */
	public void addAlarmCondition(AlarmSendCondition alarmSendCondition ,long ruleId){
		
		alarmRuleService.addAlarmConditon(ruleId, alarmSendCondition);
		
	}
	
	/**
	 * 编辑alarmSendCondition
	 * 
	 * @return
	 */
	public void updateAlarmCondition(AlarmSendCondition alarmSendCondition ,long ruleId){
		
		alarmRuleService.updateAlarmConditon(ruleId, alarmSendCondition);
		
	}
	
	/**
	 * 批量更新AlarmCondition发送状态
	 * 
	 * @return
	 */
	public void changeAlarmConditionEnabled(List<AlarmConditonEnableInfo> aceiList){
		
		alarmRuleService.changeAlarmConditionEnabled(aceiList);
		
	}
	
	/**
	 * 删除告警规则
	 * 
	 * @return
	 */
	public void deleteAlarmRule(long[] ruleId){
		
		alarmRuleService.deleteAlarmRuleById(ruleId);
		
	}
	
	/**
	 * 添加告警规则
	 * 
	 * @return
	 */
	public void addAlarmRule(AlarmRule ar){
		
		alarmRuleService.addAlarmRule(ar);
		
	}
	
	/**
	 * 根据userId获取告警规则
	 * 
	 * @return
	 */
	public List<AlarmRule> getAlarmRulesByUserId(String userId,String profileType){
		
		List<AlarmRule> list = alarmRuleService.getAlarmRulesByUserId(userId, getAlarmRuleProfileEnum(profileType));
		
		return list;
		
	}
	
	/**
	 * 将指定profileId的告警规则绑定到新的profileId上
	 * 
	 * @return
	 */
	public void bindAlarmRuleToNewProfile(Long profileIdSource,Long profileIdTarget,AlarmRuleProfileEnum profileType){
		
		List<AlarmRule> alarmRuleList = alarmRuleService.getAlarmRulesByProfileId(profileIdSource, profileType);
		//保存告警规则
		for(AlarmRule ar:alarmRuleList){
			AlarmRule aRule = new AlarmRule();
			aRule.setProfileType(ar.getProfileType());
			aRule.setProfileId(profileIdTarget);
			aRule.setUserId(ar.getUserId());
			alarmRuleService.addAlarmRule(aRule);
			
			//将原告警规则的告警设置,复制给新告警规则
			for(SendWayEnum swe:SendWayEnum.values()){
				AlarmSendCondition asc = alarmRuleService.getAlarmSendCondition(ar.getId(), swe);
				if(null!=asc){
					alarmRuleService.addAlarmConditon(aRule.getId(), asc);
				}
			}
		}	
	}
	
}
