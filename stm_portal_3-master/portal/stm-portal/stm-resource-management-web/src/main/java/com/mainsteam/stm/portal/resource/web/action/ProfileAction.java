package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ProfileApi;
import com.mainsteam.stm.portal.resource.bo.StrategyPageBo;
import com.mainsteam.stm.portal.resource.web.vo.StrategyPageVo;
import com.mainsteam.stm.portal.resource.web.vo.StrategyVo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;

/**
 * <li>文件名称: ProfileAction.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: 策略</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月
 * @author xhf
 */
@Controller
@RequestMapping("/strategy/strategyAll")
public class ProfileAction extends BaseAction {

	private static Logger logger = Logger.getLogger(ProfileAction.class);
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ProfileApi profileApi;
	
	@Resource
     private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private ProfileService profileService;
	
	@Resource
	private IDomainApi domainApi;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	private final static String VM_CATEGORY_ID = "VM";
	/**
	 * 获取系统默认策略数据
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/getDefaultStrategyAll")
	public JSONObject getDefaultStrategyAll(StrategyPageVo pageVo,HttpSession session){
		ILoginUser user = getLoginUser(session);
		StrategyPageBo pageBo = null;
		try {
			if(user.isSystemUser() || user.isDomainUser()){
				pageBo = profileApi.getDefaultStrategyAll(pageVo.getStartRow(), pageVo.getRowCount(), pageVo.getCondition(),pageVo.getSort(),pageVo.getOrder());
				pageVo.setTotalRecord(pageBo.getTotalRecord());
			
				List<StrategyVo> strategyVoList =new ArrayList<StrategyVo>();
				if(pageBo.getProfileInfos() != null && pageBo.getProfileInfos().size() > 0){
					for(ProfileInfo profileInfo : pageBo.getProfileInfos()){
						strategyVoList.add(profileInfoToStrategyVo(profileInfo));
					}
				pageVo.setStrategys(strategyVoList);
			 }
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return toSuccess(pageVo);
	}
	
	
	
	/**
	 * 获取系统自定义策略数据
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/getCustomStrategyAll")
	public JSONObject getCustomStrategyAll(StrategyPageVo pageVo,HttpSession session){
		ILoginUser user = getLoginUser(session);
		StrategyPageBo pageBo = null;
		try {
			pageBo = profileApi.
					getCustomStrategyAll(pageVo.getStartRow(), pageVo.getRowCount(), pageVo.getCondition(),user,pageVo.getSort(),pageVo.getOrder());
			pageVo.setTotalRecord(pageBo.getTotalRecord());
			
			 List<StrategyVo> strategyVoList =new ArrayList<StrategyVo>();
			 if(pageBo.getProfileInfos() != null && pageBo.getProfileInfos().size() > 0){
				for(ProfileInfo profileInfo : pageBo.getProfileInfos()){
					//如果查询全部，则过滤虚拟化资源的策略
					if(null == pageVo.getCondition()) {
						ResourceDef resDef = capacityService.getResourceDefById(profileInfo.getResourceId());
						if(VM_CATEGORY_ID.equals(resDef.getCategory().getParentCategory().getParentCategory().getId())) {
							continue;
						}
					}
					strategyVoList.add(profileInfoToStrategyVo(profileInfo));
				}
				pageVo.setStrategys(strategyVoList);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage());
			
		}
		return toSuccess(pageVo);
	}
	

	/**
	 * 获取系统个性化策略数据
	 * @param pageVo
	 * @return
	 */
	@RequestMapping("/getPersonalizeStrategyAll")
	public JSONObject getPersonalizeStrategyAll(StrategyPageVo pageVo,HttpSession session){
		ILoginUser user = getLoginUser(session);
		StrategyPageBo pageBo = null;
		try {
			pageBo = profileApi.
					getPersonalizeStrategyAll(pageVo.getStartRow(), pageVo.getRowCount(), pageVo.getCondition(),user,pageVo.getSort(),pageVo.getOrder());
			pageVo.setTotalRecord(pageBo.getTotalRecord());
			
			 List<StrategyVo> strategyVoList =new ArrayList<StrategyVo>();
			 if(pageBo.getProfileInfos() != null && pageBo.getProfileInfos().size() > 0){
				for(ProfileInfo profileInfo : pageBo.getProfileInfos()){
					strategyVoList.add(profileInfoToPersonalize(profileInfo));
				}
				pageVo.setStrategys(strategyVoList);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage());
			
		}
		return toSuccess(pageVo);
	}
	
	/**
	 * 获取资源策略模型父节点
	 * @return
	 */
	@RequestMapping("/getPrentStrategyType")
	public JSONObject getPrentStrategyType(){
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		try {
			CategoryDef categoryDef = capacityService.getRootCategory();
			//一级菜单
			CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
			if(baseCategoryDef != null && baseCategoryDef.length > 0){
				for (CategoryDef c : baseCategoryDef) {
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
	 * 告警管理中，获取资源策略模型父节点
	 * @return
	 */
	@RequestMapping("/getAlarmPrentStrategyType")
	public JSONObject getAlarmPrentStrategyType() {

		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		try {
			CategoryDef categoryDef = capacityService.getRootCategory();
			//一级菜单
			CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
			if(baseCategoryDef != null && baseCategoryDef.length > 0){
				for (CategoryDef c : baseCategoryDef) {
					Map<String,String> result  =  new HashMap<String,String>();
					result.put("id",c.getId() );
					result.put("name", c.getName());
					baseList.add(result);
				}
			
			  }
			} catch (Exception e) {
				logger.error(e.getMessage());
			
			}
		return toSuccess(baseList);
	
	}
	
	
	/**
	 * 获取资源策略模型父节点
	 * @return
	 */
	@RequestMapping("/getPrentStrategyTypeForAlarmManagement")
	public JSONObject getPrentStrategyTypeForAlarmManagement(){
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		try {
			CategoryDef categoryDef = capacityService.getRootCategory();
			//一级菜单
			CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
			if(baseCategoryDef != null && baseCategoryDef.length > 0){
				for (CategoryDef c : baseCategoryDef) {
					if(null!=c.getId() && c.getId().equals("VM")){
						Map<String,String> result  =  new HashMap<String,String>();
						result.put("id",c.getId() );
						result.put("name", c.getName());
						baseList.add(result);
					}else if(c.isDisplay()){
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
	 * 根据一级菜单ID查询二级菜单
	 * @return
	 */
	@RequestMapping("/getChildCategoryDefById")
	public JSONObject getChildCategoryDefById(String id){
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		try {
			CategoryDef categoryDef = capacityService.getCategoryById(id);
			//二级菜单
			CategoryDef[] childCategoryDef = categoryDef.getChildCategorys();
			if(childCategoryDef != null && childCategoryDef.length > 0){
				for (CategoryDef c : childCategoryDef) {
					
					CategoryDef[] thirdCategoryDef = c.getChildCategorys();
					//虚拟化资源的二级菜单
					if(thirdCategoryDef != null && thirdCategoryDef.length > 0) {
						for (CategoryDef category : thirdCategoryDef) {
							Map<String,String> result  =  new HashMap<String,String>();
							result.put("id",category.getId() );
							result.put("name", category.getName());
							baseList.add(result);
						}
					}
					else if(c.isDisplay()){//其它资源的二级菜单
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
	 * 根据一级category获取所有的资源模型
	 * @param id
	 * @return
	 */
	@RequestMapping("/getParentResourceDefAll")
	public JSONObject getParentResourceDefAll(String id){
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();
		try {
			CategoryDef categoryDef = capacityService.getCategoryById(id);
			CategoryDef[] childCategoryDef  = categoryDef.getChildCategorys();
			if(childCategoryDef != null && childCategoryDef.length > 0){
				for(int i=0; i<childCategoryDef.length; i++){
					ResourceDef[] rd =	childCategoryDef[i].getResourceDefs();
					if(rd != null && rd.length > 0){
						for(int j = 0;j < rd.length; j++ ){
							Map<String,String> result  =  new HashMap<String,String>(); 
							result.put("id",rd[j].getId() );
							result.put("name", rd[j].getName());
							baseList.add(result);
						}
					}	 
				}
			
			}
			}catch (Exception e) {
				logger.error(e.getMessage());
			}
		return toSuccess(baseList);
	}
	
	/**
	 * 根据二级category获取所有的资源模型
	 * @param id
	 * @return
	 */
	@RequestMapping("/getResourceDefAll")
	public JSONObject getResourceDefAll(String id){
		CategoryDef categoryDef = capacityService.getCategoryById(id);
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();	
				 ResourceDef[] resourceDefs =	categoryDef.getResourceDefs();
				 if(resourceDefs != null && resourceDefs.length > 0){
					 for(ResourceDef rdef : resourceDefs){
						 Map<String,String> result  =  new HashMap<String,String>(); 
						 result.put("id",rdef.getId() );
						 result.put("name", rdef.getName());
						 baseList.add(result);
					 }
				 }	 
		
		return toSuccess(baseList);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/getChildResourceAll")
	public JSONObject getChildResourceAll(String id){
		ResourceDef resourceDef = capacityService.getResourceDefById(id);
		List<Map<String,String>> baseList = new ArrayList<Map<String,String>>();	
				 ResourceDef[] resourceDefs =	resourceDef.getChildResourceDefs();
				 if(resourceDefs != null && resourceDefs.length > 0){
					 for(ResourceDef rdef : resourceDefs){
						 Map<String,String> result  =  new HashMap<String,String>(); 
						 result.put("id",rdef.getId() );
						 result.put("name", rdef.getName());
						 baseList.add(result);
					 }
				 }	 
		
		return toSuccess(baseList);
	}
	
	/**
	 * 系统默认策略复制
	 * @param strategyVo
	 * @return
	 */
	@RequestMapping("/copyDefaultStrategy")
	public JSONObject copyDefaultStrategy(StrategyVo strategyVo,HttpSession session){
		ILoginUser user = getLoginUser(session);
		strategyVo.setCreateUser(user.getId());
		int count = 0;
		try {
			 count = profileApi.copyDefaultStrategy(strategyVoToProfile(strategyVo));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return toSuccess(count);
	}
	
	/**
	 * 新增用户自定义策略
	 * @param strategyVo
	 * @return
	 */
	@RequestMapping("/insertCustomStrategy")
	public JSONObject insertCustomStrategy(StrategyVo strategyVo, HttpSession session){
		ILoginUser user = getLoginUser(session);
		strategyVo.setCreateUser(user.getId());
		int count = 0;
		boolean exist = profileApi.customStrategyExist(strategyVoToProfile(strategyVo));
		if (!exist) {//当不存在的时候再添加
			try {
				count =	profileApi.insertCustomStrategy(strategyVoToProfile(strategyVo), strategyVo.getChildProfileIds());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		return toSuccess(count);
	}
	
	
	/**
	 * 删除用户自定义策略
	 * @param id
	 * @return
	 */
	@RequestMapping("/delCustomStrategy")
	public JSONObject delCustomStrategy(long id){
		
		int count = 0;
		try {
			count = profileApi.delCustomStrategy(id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return toSuccess(count);
	}
	
	/**
	 * 批量删除用户自定义策略
	 * @param ids
	 * @return
	 */
	@RequestMapping("/batchDelCustomStrategy")
	public JSONObject batchDelCustomStrategy(long[] ids){
		
		int count = 0;
		try {
			count = profileApi.batchDelCustomStrategy(ids);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return toSuccess(count);
	}
	
	/**
	 * 删除个性化策略
	 * @param id
	 * @return
	 */
	@RequestMapping("/delPersonalizeStrategy")
	public JSONObject delPersonalizeStrategy(long id){
		int count = 0;
		try {
			count = profileApi.delPersonalizeStrategy(id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return toSuccess(count);
	}
	
	/**
	 * 批量删除个性化策略
	 * @param ids
	 * @return
	 */
	@RequestMapping("/batchDelPersonalizeStrategy")
	public JSONObject batchDelPersonalizeStrategy(long[] ids){
		int count = 0;
		try {
			count = profileApi.batchDelPersonalizeStrategy(ids);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return toSuccess(count);
	}
	
	/**
	 * 根据策略ID查询策略对象
	 * @param id
	 * @return
	 */
	@RequestMapping("/getDefaultStrategy")
	public JSONObject getDefaultStrategy(long id) {
		Profile profile = null;
		try {
			profile =  profileApi.getDefaultStrategy(id);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return toSuccess(profileToStrategyVo(profile));
	}
	
	
	/**
	 * 策略对象StrategyVo对象
	 * @param profileInfo
	 * @return
	 */
	private StrategyVo profileInfoToStrategyVo(ProfileInfo profileInfo){
		StrategyVo  vo = new StrategyVo();
		vo.setId(profileInfo.getProfileId());
		vo.setStrategyName(profileInfo.getProfileName());
		vo.setStrategyDesc(profileInfo.getProfileDesc());
	    vo.setStrategyType(null==capacityService.getResourceDefById(profileInfo.getResourceId())?"":capacityService.getResourceDefById(profileInfo.getResourceId()).getName());
	    vo.setCreateUser(profileInfo.getCreateUser());
	    if(profileInfo.getDomainId() != 0){
	    	 vo.setDomainName(null==domainApi.get(profileInfo.getDomainId())?"":domainApi.get(profileInfo.getDomainId()).getName());
	    }
	    //add by wxh
	    vo.setResourceId(profileInfo.getResourceId());
		return vo;
	}
	

	
	/**
	 * 通过策略Id查找资源模型属性IP
	 * @param profileInfoId
	 * @return
	 */
	private Map<String, Object> getResourceIpAdrress(long profileInfoId){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<Long> idList = profileService.getResourceInstanceByProfileId(profileInfoId);
			List<Map<String, String>> ipList = new ArrayList<Map<String, String>>();
			if(null != idList && idList.size() > 0){
				for(Long id : idList){
					ResourceInstance  instance = resourceInstanceService.getResourceInstance(id);
					String[] ips = instance.getModulePropBykey("IPAddress");
			
					for(int i = 0; ips != null && i < ips.length; i++){
						String ip = ips[i];
						Map<String, String> ipMap = new HashMap<String, String>();
						ipMap.put("id", ip);
						ipMap.put("name", ip);
						ipList.add(ipMap);
					}
					if(ipList.isEmpty()){
						Map<String, String> ip = new HashMap<String, String>();
						ip.put("id", instance.getShowIP());
						ip.put("name", instance.getShowIP());
						ipList.add(ip);
					}
				}
				result.put("instanceIP", ipList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			return result;
		}
	
	/**
	 * 策略对象StrategyVo对象
	 * @param profile
	 * @return
	 */
	private StrategyVo profileToStrategyVo(Profile profile){
		
		StrategyVo  vo = new StrategyVo();
		long profileId = profile.getProfileInfo().getProfileId();
		vo.setId(profileId);
		vo.setStrategyName("复制"+profile.getProfileInfo().getProfileName());		
		vo.setStrategyDesc(profile.getProfileInfo().getProfileDesc());
		vo.setPrentCapacityName(this.getCapacityName(profile.getProfileInfo().getResourceId()).getParentCategory().getName());
		vo.setCapacityName(this.getCapacityName(profile.getProfileInfo().getResourceId()).getName());
		return vo;
	}
	
	/**
	 * 根据resourceId查询CategoryDef对象
	 * @param resourceId
	 * @return
	 */
	 private CategoryDef getCapacityName(String resourceId){
		 ResourceDef resourceDef  = capacityService.getResourceDefById(resourceId);
		 return resourceDef.getCategory();
	 }
	 
	/**
	 * StrategyVo对象 转换策略对象
	 * @param profile
	 * @return
	 */
	private Profile strategyVoToProfile(StrategyVo strategyVo){
		
		Profile  profile = new Profile();
		ProfileInfo pInfo = new ProfileInfo();
		if(null !=strategyVo.getId()){
			pInfo.setProfileId(strategyVo.getId());
		}
		pInfo.setDomainId(strategyVo.getDomainId());
		pInfo.setProfileName(strategyVo.getStrategyName());
		pInfo.setProfileDesc(strategyVo.getStrategyDesc());
		pInfo.setResourceId(strategyVo.getResourceName());
		pInfo.setCreateUser(strategyVo.getCreateUser());
		profile.setProfileInfo(pInfo);
		
		return profile;
	}
	
	
	/**
	 * 策略对象StrategyVo对象
	 * @param profileInfo
	 * @return
	 */
	private StrategyVo profileInfoToPersonalize(ProfileInfo profileInfo){
		
		StrategyVo  vo = new StrategyVo();
		vo.setId(profileInfo.getProfileId());
		vo.setStrategyName(profileInfo.getProfileName());
		vo.setStrategyDesc(profileInfo.getProfileDesc());
	    vo.setStrategyType(capacityService.getResourceDefById(profileInfo.getResourceId()).getName());
	    vo.setStrategyIps(this.getResourceIpAdrress(profileInfo.getProfileId()));
	    if(profileInfo.isUse()){
	    	vo.setIsUse("启用");
	    }else{
	    	vo.setIsUse("未启用");
	    }
	    
		return vo;
	}
}
