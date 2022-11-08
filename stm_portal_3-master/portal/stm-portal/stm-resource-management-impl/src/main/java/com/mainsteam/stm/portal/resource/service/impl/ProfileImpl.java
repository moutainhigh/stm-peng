package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.resource.api.IProfileSortableBySingleFieldApi;
import com.mainsteam.stm.portal.resource.api.ProfileApi;
import com.mainsteam.stm.portal.resource.bo.MetricFrequentBo;
import com.mainsteam.stm.portal.resource.bo.MetricSettingBo;
import com.mainsteam.stm.portal.resource.bo.ProfileBo;
import com.mainsteam.stm.portal.resource.bo.ProfileInfoBo;
import com.mainsteam.stm.portal.resource.bo.ProfileMetricBo;
import com.mainsteam.stm.portal.resource.bo.StrategyPageBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.um.domain.api.IDomainReferencerRelationshipApi;
import com.mainsteam.stm.util.Util;

/**
 * <li>文件名称: DefaultStrategyImpl.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月
 * @author xhf
 */
public class ProfileImpl implements ProfileApi,IDomainReferencerRelationshipApi {
	
	private static final Log logger = LogFactory.getLog(ProfileImpl.class);
	
	@Resource
	private ProfileService  profileService;
	
	@Resource
	private IResourceApi stm_system_resourceApi;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private IProfileSortableBySingleFieldApi profileSortableBySingleFieldApi;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	@Resource
	private BizMainApi bizMainApi;
	
	private final static String VM_CATEGORY_ID = "VM";
	
	@Override
	public StrategyPageBo getDefaultStrategyAll(long startRecord, long pageSize,
			ProfileInfo profileInfo,String sort,String order) throws Exception {
		
		List<ProfileInfo> profileInfoList = new ArrayList<ProfileInfo>();
		List<ProfileInfo> profileInfoNewList = new ArrayList<ProfileInfo>();
		StrategyPageBo pageBo = new StrategyPageBo();
		if(profileInfo != null){
			List<String> resourceIdList = new ArrayList<String>();
			if(null != profileInfo.getResourceId() ){		
				resourceIdList = this.getCategoryByResourceIds(profileInfo.getResourceId());
			}else{
				resourceIdList = this.getResourceIds(profileInfo.getProfileDesc());
			}
			profileInfoNewList = profileService.getProfileBasicInfoByResourceId(resourceIdList,ProfileTypeEnum.DEFAULT);
		}else{
			 profileInfoList = profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.DEFAULT);
			/***
			 * resourseId 资源模型ID
			 * 1.通过resourseId找到二级类别，再依次找到父级类别
			 * 2.通过父级类别中的isDisplay属性判断是否作显示
			 */
			 if(profileInfoList !=null && !profileInfoList.isEmpty()){
				 for (ProfileInfo profile : profileInfoList) {
					 ResourceDef def=capacityService.getResourceDefById(profile.getResourceId());
					 if(!licenseCapacityCategory.isAllowCategory(def.getCategory().getParentCategory().getId())){
						continue;
					 }
					 //默认策略过滤虚拟化策略
					 if(VM_CATEGORY_ID.equals(def.getCategory().getParentCategory().getParentCategory().getId())) {
							continue;
					 }
					 if(def!=null){
						 CategoryDef categoryDef= def.getCategory();
						 CategoryDef parentCate= categoryDef.getParentCategory();
						 if(parentCate!=null && parentCate.isDisplay()){
							 profileInfoNewList.add(profile);
						 }
					 }
				 }
			 }
		}

		//对策略进行排序
		//对策略进行排序
		if((!Util.isEmpty(sort)) && (!Util.isEmpty(order))){
			profileInfoNewList = profileSortableBySingleFieldApi.sort(profileInfoNewList, sort, order);
		}
		pageBo = this.getStrategyAllPage(startRecord, pageSize, profileInfoNewList);
		return pageBo;
	}
	
	/**
	 * 通过categoryId返回ResourceId数组
	 * @param categoryId
	 * @return
	 */
	private List<String> getCategoryByResourceIds(String categoryId){
		List<String> list = new ArrayList<String>();
		CategoryDef  categoryDef = capacityService.getCategoryById(categoryId);
		String[] resourceIds = categoryDef.getResourceIds();
		if(null !=resourceIds && resourceIds.length > 0){
			for(String resourceId :  resourceIds){
				list.add(resourceId);
			}
		}
		return list;
	}
	
	/**
	 * 通过categoryId返回getResourceId数组
	 * @param categoryId
	 * @return
	 */
	private List<String> getResourceIds(String categoryId){
		List<String> list = new ArrayList<String>();
		CategoryDef  categoryDef = capacityService.getCategoryById(categoryId);
		 CategoryDef[] categoryDefList = categoryDef.getChildCategorys();
		 if(null != categoryDefList && categoryDefList.length > 0){
			 for(CategoryDef cd : categoryDefList){
				 String[] resourceIds = cd.getResourceIds();
				 if(null !=resourceIds && resourceIds.length > 0){
						for(String resourceId :  resourceIds){
							list.add(resourceId);
						}
					}
			 }
		 }
		 
		
		
		return list;
	}

	@Override
	public StrategyPageBo getCustomStrategyAll(long startRecord, long pageSize,
			ProfileInfo profileInfo,ILoginUser user,String sort,String order)  throws Exception{
		List<ProfileInfo> profileInfoList = new ArrayList<ProfileInfo>();
		List<ProfileInfo> profileInfoNewList = new ArrayList<ProfileInfo>();
		StrategyPageBo pageBo = new StrategyPageBo();
		if(profileInfo != null){
			List<String> resourceIdList = new ArrayList<String>();
			if(null != profileInfo.getResourceId() ){
				resourceIdList = this.getCategoryByResourceIds(profileInfo.getResourceId());
			}else{
				resourceIdList = this.getResourceIds(profileInfo.getProfileDesc());
			}
			profileInfoList = profileService.getProfileBasicInfoByResourceId(resourceIdList,ProfileTypeEnum.SPECIAL);
		}else{
			profileInfoList = profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.SPECIAL);
		}
		
		if(user.isSystemUser()){
			//对策略进行排序
			if((!Util.isEmpty(sort)) && (!Util.isEmpty(order))){
				profileInfoList = profileSortableBySingleFieldApi.sort(profileInfoList, sort, order);
			}
			pageBo = this.getStrategyAllPage(startRecord, pageSize, profileInfoList);
		}else if(user.getStatus() ==1 &&  user.getUserType() == 1){
			Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
			for(ProfileInfo pro :profileInfoList){
				ResourceDef def=capacityService.getResourceDefById(pro.getResourceId());
				 if(!licenseCapacityCategory.isAllowCategory(def.getCategory().getParentCategory().getId())){
					continue;
				 }
				long domainId = pro.getDomainId();
				for(IDomain dn : domains){
					if(domainId == dn.getId()){
						profileInfoNewList.add(pro);
					}
				}
			}
			//对策略进行排序
			if((!Util.isEmpty(sort)) && (!Util.isEmpty(order))){
				profileInfoNewList = profileSortableBySingleFieldApi.sort(profileInfoNewList, sort, order);
			}
			pageBo = this.getStrategyAllPage(startRecord, pageSize, profileInfoNewList);
		}
		
		return pageBo;
	}
	
	
	
	@Override
	public int copyDefaultStrategy(Profile profile) throws Exception{
		ProfileInfo prof = new ProfileInfo();
		prof.setDomainId(profile.getProfileInfo().getDomainId());
		prof.setProfileName(profile.getProfileInfo().getProfileName());
		prof.setProfileDesc(profile.getProfileInfo().getProfileDesc());
		prof.setCreateUser(profile.getProfileInfo().getCreateUser());
		profileService.copyProfile(profile.getProfileInfo().getProfileId(), prof);
		
		return 0;
	}

	@Override
	public int insertCustomStrategy(Profile profile, String childProfileIds) throws Exception{
		ProfileInfo profileinfo = new ProfileInfo();
		profileinfo.setProfileName(profile.getProfileInfo().getProfileName());
		profileinfo.setProfileDesc(profile.getProfileInfo().getProfileDesc());
		profileinfo.setDomainId(profile.getProfileInfo().getDomainId());
		profileinfo.setResourceId(profile.getProfileInfo().getResourceId());
		profileinfo.setProfileType(ProfileTypeEnum.SPECIAL);
		profileinfo.setCreateUser(profile.getProfileInfo().getCreateUser());
		List<ProfileInfo>  childList = new ArrayList<ProfileInfo>();
		if(!"".equals(childProfileIds)){
			String [] childProfileIdList = childProfileIds.split(",");
			for(int i=0; i < childProfileIdList.length; i++){
				ProfileInfo childProfile = new ProfileInfo();
				childProfile.setResourceId(childProfileIdList[i]);
				childProfile.setProfileName(capacityService.getResourceDefById(childProfileIdList[i]).getName());
				childProfile.setProfileType(ProfileTypeEnum.SPECIAL);
				childProfile.setDomainId(profile.getProfileInfo().getDomainId());
				childList.add(childProfile);
			}
		}
		Long porfId = profileService.createSpecialProfile(profileinfo, childList);
		
	     if(porfId != null){
	    	 return  porfId.intValue();
	     }
		return 0;
	}

	@Override
	public int delCustomStrategy(long profileId) throws Exception{
		List<Long> id = profileService.getResourceInstanceByProfileId(profileId);
		if(id != null && id.size() > 0){
			bizMainApi.instanceMonitorChangeBiz(id);
		}
		profileService.removeProfileById(profileId);
		return 0;
	}

	@Override
	public int batchDelCustomStrategy(long[] profileIds) throws Exception{
		if(profileIds != null && profileIds.length > 0 ){
			List<Long> cancelList =  new ArrayList<Long>();
			for (long item : profileIds) {
				cancelList.add(item);
			}
			profileService.removeProfileByIds(cancelList);
		}
		return 0;
	}

	@Override
	public int delPersonalizeStrategy(long profileId) throws Exception{
		profileService.removeProfileById(profileId);
		return 0;
	}

	@Override
	public int batchDelPersonalizeStrategy(long[] profileIds) throws Exception{
		if(profileIds != null && profileIds.length > 0 ){
			for(int i = 0 ; i < profileIds.length ; i++ ){
				profileService.removeProfileById(profileIds[i]);
			}
		}
		return 0;
	}

	@Override
	public StrategyPageBo getPersonalizeStrategyAll(long startRecord,
			long pageSize, ProfileInfo profileInfo,ILoginUser user,String sort,String order) throws Exception {
		List<ProfileInfo> oldProfileInfoList = new ArrayList<ProfileInfo>();
		List<ProfileInfo> profileInfoList = new ArrayList<ProfileInfo>();
		List<ProfileInfo> profileInfoNewList = new ArrayList<ProfileInfo>();
		StrategyPageBo pageBo = new StrategyPageBo();
		if(profileInfo != null){
			List<String> resourceIdList = new ArrayList<String>();
			if(null != profileInfo.getResourceId() ){
				resourceIdList = this.getCategoryByResourceIds(profileInfo.getResourceId());
			}else{
				resourceIdList = this.getResourceIds(profileInfo.getProfileDesc());
			}
			
			oldProfileInfoList = profileService.getProfileBasicInfoByResourceId(resourceIdList,ProfileTypeEnum.PERSONALIZE);
		}else{
			oldProfileInfoList = profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.PERSONALIZE);
		}
		for(ProfileInfo profile : oldProfileInfoList){
			  ResourceDef def=capacityService.getResourceDefById(profile.getResourceId());
			  if(!licenseCapacityCategory.isAllowCategory(def.getCategory().getParentCategory().getId())){
				  continue;
			  }
			  List<Long> number = profileService.getResourceInstanceByProfileId(profile.getProfileId());
			  if(null != number &&  number.size() > 0){
				  profileInfoList.add(profile);
			  }
		}
		if(user.isSystemUser()){
			//对策略进行排序
			if((!Util.isEmpty(sort)) && (!Util.isEmpty(order))){
				profileInfoList = profileSortableBySingleFieldApi.sort(profileInfoList, sort, order);
			}
			pageBo = this.getStrategyAllPage(startRecord, pageSize, profileInfoList);
		}else if(user.getStatus() ==1 && user.getUserType() == 1){
			Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
			for(ProfileInfo pro :profileInfoList){
				long instnaceId = pro.getPersonalize_instanceId();
				long domainId = stm_system_resourceApi.getResource(instnaceId).getDomainId();
				for(IDomain dn : domains){
					if(domainId == dn.getId()){
						profileInfoNewList.add(pro);
					}
				}
			}
			//对策略进行排序
			if((!Util.isEmpty(sort)) && (!Util.isEmpty(order))){
				profileInfoNewList = profileSortableBySingleFieldApi.sort(profileInfoNewList, sort, order);
			}
			pageBo = this.getStrategyAllPage(startRecord, pageSize, profileInfoNewList);
		}
		return pageBo;
	}
	
	private StrategyPageBo getStrategyAllPage(long startRecord,
			long pageSize,List<ProfileInfo>  profileInfoList){
		StrategyPageBo pageBo = new StrategyPageBo();
		if(profileInfoList == null || profileInfoList.size() <= 0){
			pageBo.setStartRow(startRecord);
			pageBo.setTotalRecord(0);
			pageBo.setRowCount(0);
			pageBo.setProfileInfos(null);
		}else{
			pageBo.setStartRow(startRecord);
			pageBo.setTotalRecord(profileInfoList.size());
			pageBo.setRowCount(pageSize);
			
			if((startRecord + pageSize) > profileInfoList.size()){
				profileInfoList =  profileInfoList.subList((int)startRecord, profileInfoList.size());
			}else{
				profileInfoList= profileInfoList.subList((int)startRecord, (int)(startRecord + pageSize));
			}
			pageBo.setProfileInfos(profileInfoList);
		}
		return pageBo;
	}
	
	
	
	@Override
	public Profile getDefaultStrategy(long profileId) throws Exception {
		Profile profile  = profileService.getProfilesById(profileId);
		return profile;
	}
	
	
	/**
	 * 通过资源ID 和 指标ID  获取  指标信息
	 * @param resourceId
	 * @param metricId
	 * @return
	 */
	@Override
	public ResourceMetricDef getMetricInfo(String resourceId,String metricId){
		 ResourceMetricDef rmd = capacityService.getResourceMetricDef(resourceId, metricId);
		 
		 return rmd;
	}
	
	
	/**
	 * 通过资源ID  获取指标信息
	 * @param profileId
	 * @return
	 * @throws ProfilelibException
	 */
	@Override
	public List<ProfileMetricBo> getProfileMetrics(long profileId) throws ProfilelibException {
		List<ProfileMetricBo> profileMetricPoList=new ArrayList<ProfileMetricBo>();
		//获取策略的基本信息
		Profile profile=profileService.getProfilesById(profileId);
		//可用性指标
		List<ProfileMetricBo> availabilityMetricList = new ArrayList<ProfileMetricBo>();

		//信息指标
		List<ProfileMetricBo> informationMetricList = new ArrayList<ProfileMetricBo>();
		
		//性能指标
		List<ProfileMetricBo> performanceMetricList = new ArrayList<ProfileMetricBo>();
		
		String resourceId=profile.getProfileInfo().getResourceId();
		//策略的指标列表
		List<ProfileMetric> profileMetrics=profile.getMetricSetting().getMetrics();
		
		for(ProfileMetric profileMetric:profileMetrics){
			String metricId=profileMetric.getMetricId();
			
			ResourceMetricDef resourceMetricDef=capacityService.getResourceMetricDef(resourceId, metricId);

			if(resourceMetricDef == null){
				continue;
			}
			
			if(!resourceMetricDef.isDisplay()){
				continue;
			}
			
			//获取到指标名称
			String metricName=resourceMetricDef.getName();
			//获取到指标类型
			MetricTypeEnum metricTypeEnum = resourceMetricDef.getMetricType();
//			过滤信息指标
//			if(metricTypeEnum == MetricTypeEnum.InformationMetric){
//				continue;
//			}
			
			String unit = resourceMetricDef.getUnit();
			
			List<MetricFrequentBo> metricFrequentBoList = new ArrayList<MetricFrequentBo>();
			
			//遍历出该指标支持的频度
			FrequentEnum[] frequents = resourceMetricDef.getSupportMonitorFreq();
			for(FrequentEnum singleFrequent : frequents){
				MetricFrequentBo frequentBo = new MetricFrequentBo();
				frequentBo.setId(singleFrequent.name());
				frequentBo.setName(FrequentEnum.valueOf(singleFrequent.name()).toString());
				metricFrequentBoList.add(frequentBo);
			}
			
			//显示可用性指标
			ProfileMetricBo profileMetricBo=new ProfileMetricBo();
			BeanUtils.copyProperties(profileMetric, profileMetricBo);
			profileMetricBo.setMetricName(metricName);
			profileMetricBo.setMetricTypeEnum(metricTypeEnum);
			profileMetricBo.setSupportFrequentList(metricFrequentBoList);
			profileMetricBo.setDisplayOrder(Long.parseLong(resourceMetricDef.getDisplayOrder()));
			profileMetricBo.setUnit(unit);
			
			if(metricTypeEnum == MetricTypeEnum.AvailabilityMetric){
				
				availabilityMetricList.add(profileMetricBo);
				
			}			
			
			if(metricTypeEnum == MetricTypeEnum.PerformanceMetric){
				
				informationMetricList.add(profileMetricBo);
				
			}
			
			if(metricTypeEnum == MetricTypeEnum.InformationMetric){
				
				performanceMetricList.add(profileMetricBo);
				
			}
			
		}
		
		Collections.sort(availabilityMetricList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		Collections.sort(informationMetricList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		Collections.sort(performanceMetricList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		profileMetricPoList.addAll(availabilityMetricList);
		profileMetricPoList.addAll(informationMetricList);
		profileMetricPoList.addAll(performanceMetricList);
		
		
		return profileMetricPoList;
	}
	
	/**
	 * 通过策略ID查询策略的信息
	 * @param profileId
	 * @return
	 * @throws ProfilelibException
	 */
	public ProfileBo getProfile(long profileId) throws ProfilelibException{

		//获取策略的基本信息
		Profile profile = profileService.getProfilesById(profileId);
		
		if(profile == null){
			if(logger.isErrorEnabled()){
				logger.error("profileService.getProfilesById(profileId) is null , id : " + profileId);
			}
			return null;
		}
		
		if(profile.getProfileInfo() == null){
			if(logger.isErrorEnabled()){
				logger.error("profileService.getProfilesById(profileId) ProfileInfo is null , id : " + profileId);
			}
			return null;
		}
		
		return getProfileBoByProfile(profile);
	}
	
	/**
	 * 将MetricSetting转为MetricSettingBo
	 * @return
	 */
	private MetricSettingBo metricSettingToBo(MetricSetting metricSetting,String resourceId){
		//策略的指标列表
		List<ProfileMetric> profileMetrics=metricSetting.getMetrics();
		
		//可用性指标
//		Map<Long, ProfileMetricBo> availabilityMetricMap = new TreeMap<Long, ProfileMetricBo>();
		List<ProfileMetricBo> availabilityMetricList = new ArrayList<ProfileMetricBo>();
		
		//信息指标(不需要显示) 文件告警需要
//		Map<Long, ProfileMetricBo> informationMetricMap = new TreeMap<Long, ProfileMetricBo>();
		List<ProfileMetricBo> informationMeticList = new ArrayList<ProfileMetricBo>();
		
		//性能指标
//		Map<Long, ProfileMetricBo> performanceMetricMap = new TreeMap<Long, ProfileMetricBo>();
		List<ProfileMetricBo> performanceMetricList = new ArrayList<ProfileMetricBo>();
		
		List<ProfileMetricBo> profileMetricBoList=new ArrayList<ProfileMetricBo>();
		
		for(ProfileMetric profileMetric:profileMetrics){
			String metricId=profileMetric.getMetricId();
			
			ResourceMetricDef resourceMetricDef=capacityService.getResourceMetricDef(resourceId, metricId);
			
			
			//获取到指标名称
			if(resourceMetricDef == null){
				if(logger.isErrorEnabled()){
					logger.error("resourceId : " + resourceId + ",metricId : " + metricId + ",getResourceMetricDef() resourceMetricDef is null");
				}
				continue;
			}
			if(!resourceMetricDef.isDisplay()){
				continue;
			}
			String metricName=resourceMetricDef.getName();
			//获取到指标类型
			MetricTypeEnum metricTypeEnum = resourceMetricDef.getMetricType();
			// LinuxHostFile fileModifyTime
//			if(metricTypeEnum == MetricTypeEnum.InformationMetric && !("fileModifyTime".equals(metricId) && "LinuxHostFile".equals(resourceId))){
//				continue;
//			}
			
			String unit = resourceMetricDef.getUnit();
			
			List<MetricFrequentBo> metricFrequentBoList = new ArrayList<MetricFrequentBo>();
			
			//遍历出该指标支持的频度
			FrequentEnum[] frequents = resourceMetricDef.getSupportMonitorFreq();
			for(FrequentEnum singleFrequent : frequents){
				MetricFrequentBo frequentBo = new MetricFrequentBo();
				frequentBo.setId(singleFrequent.name());
				frequentBo.setName(FrequentEnum.valueOf(singleFrequent.name()).toString());
				metricFrequentBoList.add(frequentBo);
			}
			
			//显示可用性指标
			ProfileMetricBo profileMetricBo=new ProfileMetricBo();
			BeanUtils.copyProperties(profileMetric, profileMetricBo);
			profileMetricBo.setMetricName(metricName);
			profileMetricBo.setMetricTypeEnum(metricTypeEnum);
			profileMetricBo.setSupportFrequentList(metricFrequentBoList);
			profileMetricBo.setDisplayOrder(Long.parseLong(resourceMetricDef.getDisplayOrder()));
			profileMetricBo.setUnit(unit);
			
			if(metricTypeEnum == MetricTypeEnum.AvailabilityMetric){
				availabilityMetricList.add(profileMetricBo);
//				availabilityMetricMap.put(Long.parseLong(resourceMetricDef.getDisplayOrder()), profileMetricBo);
				
			}
			

			
			if(metricTypeEnum == MetricTypeEnum.PerformanceMetric){
				performanceMetricList.add(profileMetricBo);
//				performanceMetricMap.put(Long.parseLong(resourceMetricDef.getDisplayOrder()), profileMetricBo);
				
			}
			
			if(metricTypeEnum == MetricTypeEnum.InformationMetric){
				informationMeticList.add(profileMetricBo);
//				informationMetricMap.put(Long.parseLong(resourceMetricDef.getDisplayOrder()), profileMetricBo);
			}
			
		}
		
//		profileMetricBoList.addAll(availabilityMetricMap.values());
//		profileMetricBoList.addAll(informationMetricMap.values());
//		profileMetricBoList.addAll(performanceMetricMap.values());
		
		Collections.sort(availabilityMetricList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		Collections.sort(informationMeticList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		Collections.sort(performanceMetricList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		profileMetricBoList.addAll(availabilityMetricList);
		profileMetricBoList.addAll(informationMeticList);
		profileMetricBoList.addAll(performanceMetricList);

		MetricSettingBo metricSettingBo=new MetricSettingBo();
		metricSettingBo.setMetrics(profileMetricBoList);

		return metricSettingBo;
		
	}
	
	private ProfileBo getProfileBoByProfile(Profile profile){
		
		String resourceId=profile.getProfileInfo().getResourceId();
		
		//策略的指标列表
		ProfileBo profileBo=new ProfileBo();
		BeanUtils.copyProperties(profile, profileBo);
		
		ProfileInfoBo infoBo = new ProfileInfoBo();
		BeanUtils.copyProperties(profile.getProfileInfo(), infoBo);
		
		MetricSetting metricSetting=profile.getMetricSetting();
		
		MetricSettingBo metricSettingBo = this.metricSettingToBo(metricSetting,resourceId);
		
		profileBo.setMetricSetting(metricSettingBo);
		profileBo.setProfileInfo(infoBo);
		
		if(profile.getChildren() != null && profile.getChildren().size() > 0){
			
			List<ProfileBo> childProFileBoList = new ArrayList<ProfileBo>();
			
			List<Profile> childProFileList = profile.getChildren();
			
			for(Profile childProfile : childProFileList){
				
				String childResourceId = childProfile.getProfileInfo().getResourceId();
				
				ProfileBo childProfileBo = new ProfileBo();
				BeanUtils.copyProperties(childProfile, childProfileBo);
				
				//查找子策略类型
				ProfileInfoBo childInfoBo = new ProfileInfoBo();
				BeanUtils.copyProperties(childProfile.getProfileInfo(), childInfoBo);
				ResourceDef def = capacityService.getResourceDefById(childProfile.getProfileInfo().getResourceId());
				childInfoBo.setProfileResourceType(def.getName());
				
				MetricSetting childMetricSetting = childProfile.getMetricSetting();
				
				MetricSettingBo childMetricSettingBo = this.metricSettingToBo(childMetricSetting,childResourceId);
				
				childProfileBo.setMetricSetting(childMetricSettingBo);
				childProfileBo.setProfileInfo(childInfoBo);
				
				childProFileBoList.add(childProfileBo);
				
			}
			
			profileBo.setChildren(childProFileBoList);
			
		}
		return profileBo;
	}

	/**
	 * 获取个性化策略的profile对象
	 */
	@Override
	public ProfileBo getPersonalizeProfile(String resourceId,Long instanceId) {
		// TODO Auto-generated method stub
		Profile profile = null;
		try {
			profile = profileService.getEmptyPersonalizeProfile(resourceId,instanceId);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		if(profile == null){
			if(logger.isErrorEnabled()){
				logger.error("Get empty personalize profile error , resourceId : " + resourceId);
			}
			return null;
		}
		
		return getProfileBoByProfile(profile);
	}

	@Override
	public boolean checkDomainIsRel(long domainId) {
		// TODO Auto-generated method stub
		List<ProfileInfo> profileList = null;
		try {
			profileList = profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.SPECIAL);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		if(profileList == null || profileList.size() <= 0){
			return false;
		}
		
		for(ProfileInfo profileInfo : profileList){
			if(profileInfo.getDomainId() == domainId){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean customStrategyExist(Profile profile) {
		List<ProfileInfo> profileInfoList = new ArrayList<ProfileInfo>();
		boolean exist = false;
		try {
			profileInfoList = profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.SPECIAL);
			for (ProfileInfo profileInfo : profileInfoList) {
				if (profile.getProfileInfo().getProfileName().equals(profileInfo.getProfileName())) {
					exist = true;
					break;
				}
			}
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(), e);
		}
		return exist;
	}
	
}
