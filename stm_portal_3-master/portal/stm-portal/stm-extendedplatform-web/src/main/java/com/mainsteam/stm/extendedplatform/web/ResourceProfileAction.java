package com.mainsteam.stm.extendedplatform.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.api.ResourceProfileService;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.CategoryPo;
import com.mainsteam.stm.profilelib.po.ProfileInfoPO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;

@Controller
@RequestMapping("/extendedplatform/resourceprofile")
public class ResourceProfileAction {
	
	@Resource(name="resourceProfileService")
	private ResourceProfileService resourceProfileService;
	
	@RequestMapping("resourceList")
	public String resourceList(){
		List<CategoryPo> categoryDefs = resourceProfileService.queryAllResourceDefs();
		return JSONObject.toJSONString(categoryDefs);
	}
	
	@RequestMapping(value="loginAccount",method = RequestMethod.POST)
	public String queryLoginUserName(HttpSession session){
		ILoginUser user = (ILoginUser) session.getAttribute(ILoginUser.SESSION_LOGIN_USER);
		return user.getAccount();
	}
	
	@RequestMapping("parentResourceList")
	public String parentResource(){
		List<CategoryPo> categoryDefs = resourceProfileService.queryAllParentResource();
		return JSONObject.toJSONString(categoryDefs);
	}
	
	@RequestMapping(value="resource",method=RequestMethod.POST)
	public String getResourceDef(String id){
		ResourceDef resourceDef = resourceProfileService.getResourceDef(id);
		List<CategoryPo> result = new ArrayList<CategoryPo>();
		if(resourceDef!=null){
			result.add(convertMetricToPo(resourceDef));
			if(resourceDef.getChildResourceDefs()!=null && resourceDef.getChildResourceDefs().length>0){
				for (ResourceDef childResourceDef : resourceDef.getChildResourceDefs()) {
					result.add(convertMetricToPo(childResourceDef));
				}
			}
		}
		return JSONObject.toJSONString(result);
	} 
	
	private CategoryPo convertMetricToPo(ResourceDef resourceDef){
		CategoryPo po = new CategoryPo();
		
		if(resourceDef!=null){
			po.setId(resourceDef.getId());
			po.setName(resourceDef.getName());
			if(resourceDef.getMetricDefs()!=null && resourceDef.getMetricDefs().length>0){
				List<CategoryPo> categoryPos = new ArrayList<>();
				for (ResourceMetricDef metricDef : resourceDef.getMetricDefs()) {
					CategoryPo metricPo = new CategoryPo();
					metricPo.setId(metricDef.getId());
					metricPo.setName(metricDef.getName());
					categoryPos.add(metricPo);
				}
				po.setChildCategorys(categoryPos);
			}
		}
		return po;
	}
	
	@RequestMapping("parentProfiles")
	public String parentProfileList(){
		List<ProfileInfoPO> profileInfoPOs = resourceProfileService.getAllParentProfiles();
		return JSONObject.toJSONString(profileInfoPOs);
	}
	
	@RequestMapping(value="profilelistByResource",method=RequestMethod.POST)
	public String queryProfileListByResource(String resourceId){
		List<ProfileInfoPO> profileInfoPOs = resourceProfileService.queryProfileInfoByResource(resourceId);
		return JSONObject.toJSONString(profileInfoPOs);
	}
	
	@RequestMapping(value="profileMetrics",method=RequestMethod.POST)
	public String queryProfileMetrics(long profileId){
		List<ProfileMetricPO> metricPOs = resourceProfileService.getMetricsByProfile(profileId);
		return JSONObject.toJSONString(metricPOs);
	}
	
	@RequestMapping(value="removeProfile",method=RequestMethod.POST)
	public void removeProfile(long profileId){
		resourceProfileService.removeChildProfileById(profileId);
	}
	
	
	@RequestMapping(value="removeMetric",method=RequestMethod.POST)
	public void removeMetric(long profileId,String metricId){
		resourceProfileService.removeProfileMetric(profileId, metricId);
	}
	
	@RequestMapping(value="addMetric",method=RequestMethod.POST)
	public void addMetric(String resourceId,String metricId,long profileId){
		resourceProfileService.addProfileMetricByResource(resourceId, metricId, profileId);
	}
	
	@RequestMapping("searchByProfileId")
	public String searchProfile(long profileId){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("profileInfos", resourceProfileService.queryProfileInfoById(profileId));
		result.put("profileInstanceRel", resourceProfileService.queryProfileInstanceRel(profileId));
		result.put("profileInstanceLastRel", resourceProfileService.queryProfileInstanceLastRel(profileId));
		return JSONObject.toJSONString(result);
	}
	
	@RequestMapping("searchByResourceId")
	public String searchProfile(String resourceId){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("profileInfos", resourceProfileService.queryProfilInfoByResourceId(resourceId));
		result.put("profileInstanceRel", resourceProfileService.queryProfileInstanceRelByResourceId(resourceId));
		result.put("profileInstanceLastRel", resourceProfileService.queryProfileInstanceLastRelByResourceId(resourceId));
		return JSONObject.toJSONString(result);
	}
	
	@RequestMapping("deleteRel")
	public String deleteRel(long profileId){
		return resourceProfileService.deleteProfileInstanceRel(profileId)>0?"true":"false";
	}
	
	@RequestMapping("deleteLastRel")
	public String deleteLastRel(long profileId){
		return resourceProfileService.deleteProfileInstanceLastRel(profileId)>0?"true":"false";
	}
	
	@RequestMapping("deleteRelByResource")
	public String deleteRelByResource(String resourceId){
		return resourceProfileService.deleteProfileInstanceRelByResourceId(resourceId)>0?"true":"false";
	}
	
	@RequestMapping("deleteLastRelByResource")
	public String deleteLastRelByResource(String resourceId){
		return resourceProfileService.deleteProfileInstanceLastRelByResourceId(resourceId)>0?"true":"false";
	}
	
	@RequestMapping("deleteProfileInfos")
	public String deleteProfileInfos(String ids){
		List<Long> profileIds = JSONObject.parseArray(ids, Long.class);
		boolean result = resourceProfileService.removeProfileByIds(profileIds);
		return JSONObject.toJSONString(result);
	}
}
