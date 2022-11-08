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
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IResourceMonitorApi;
import com.mainsteam.stm.portal.resource.bo.ResourceMonitorBo;
import com.mainsteam.stm.portal.resource.bo.ResourceMonitorPageBo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceMonitorPageVo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceMonitorVo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.DomainDcs;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;

/**
 * <li>文件名称: ResourceMonitorAction.java</li>
 * <li>公 司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xhf
 */
@Controller
@RequestMapping("/resource/resourceMonitor")
public class ResourceMonitorAction extends BaseAction {

	private static Logger logger = Logger.getLogger(ResourceMonitorAction.class);

	@Resource
	private CapacityService capacityService;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private IResourceMonitorApi resourceMonitorApi;

	@Resource
	private ICustomResourceGroupApi customResourceGroupApi;

	@Resource
	private IDomainApi domainApi;

	@Resource
	private IUserApi userApi;

	@Resource
	private IDomainApi stm_system_DomainApi;
	@Resource
	private CustomPropService customPropService;

	/**
	 * 开启监控
	 * 
	 * @return
	 */
	@RequestMapping("/openMonitor")
	public JSONObject openMonitor(long id) {
		int result = 0;
		try {
			result = resourceMonitorApi.openMonitor(id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return toSuccess(result);
	}

	/**
	 * 批量开启监控
	 * 
	 * @return
	 */
	@RequestMapping("/batchOpenMonitor")
	public JSONObject batchOpenMonitor(String[] ids) {
		int result = 0;
		try {
			result = resourceMonitorApi.batchOpenMonitor(ids);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return toSuccess(result);
	}

	/**
	 * 取消监控
	 * 
	 * @return
	 */
	@RequestMapping("/closeMonitor")
	public JSONObject closeMonitor(long id) {
		int count = 0;
		try {
			count = resourceMonitorApi.closeMonitor(id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return toSuccess(count);
	}

	/**
	 * 批量取消监控
	 * 
	 * @return
	 */
	@RequestMapping("/batchCloseMonitor")
	public JSONObject batchCloseMonitor(long[] ids) {
		int count = 0;
		try {
			count = resourceMonitorApi.batchCloseMonitor(ids);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return toSuccess(count);
	}

	/**
	 * 批量删除资源
	 * 
	 * @return
	 */
	@RequestMapping("/batchDelResource")
	public JSONObject batchDelResource(long[] ids) {
		int count = 0;
		try {
			count = resourceMonitorApi.batchDelResource(ids);
		} catch (Exception e) {
			logger.error("delete resource error:", e);
		}
		return toSuccess(count);
	}

	/**
	 * 获取已监控数据
	 * 
	 * @return
	 */
	@RequestMapping("/getHaveMonitor")
	public JSONObject getHaveMonitor(ResourceMonitorPageVo pageVo, HttpSession session) {
		// 获取当前登录用户
		ILoginUser user = getLoginUser(session);
		ResourceMonitorPageBo pageBo = null;
		try {
			ResourceMonitorVo rmv = pageVo.getCondition();		
			pageBo = resourceMonitorApi.getMonitored(user, pageVo.getStartRow(), pageVo.getRowCount(),
					rmv.getInstanceStatus(), rmv.getiPorName(), rmv.getDomainId(), rmv.getCategoryId(),
					rmv.getCategoryIds(), rmv.getResourceId(), rmv.getIsCustomResGroup(), pageVo.getSort(),
					pageVo.getOrder());
			pageVo.setTotalRecord(pageBo.getTotalRecord());
			List<ResourceMonitorVo> resourceVoList = new ArrayList<ResourceMonitorVo>();
			List<ResourceMonitorBo> monitorBoList = pageBo.getResourceMonitorBosExtends();

			for (int i = 0; monitorBoList != null && i < monitorBoList.size(); i++) {
//				System.out.println("monitorBoList.get(i).getCpuAvailability() == " + monitorBoList.get(i).getCpuAvailability());
				resourceVoList.add(instanceBoToResourceVo(monitorBoList.get(i)));
			}
			pageVo.setResourceMonitors(resourceVoList);
			pageVo.setResourceCategoryBos(pageBo.getResourceCategoryBos());
		} catch (Exception e) {
			logger.error("getHaveMonitor:", e);
		}
		return toSuccess(pageVo);
	}

	/**
	 * 获取未监控数据
	 * 
	 * @return
	 */
	@RequestMapping("/getNotMonitor")
	public JSONObject getNotMonitor(ResourceMonitorPageVo pageVo, HttpSession session) {
		// 获取当前登录用户
		ILoginUser user = getLoginUser(session);
		ResourceMonitorPageBo pageBo = null;
		try {
			ResourceMonitorVo rmv = pageVo.getCondition();		
			pageBo = resourceMonitorApi.getUnMonitored(user, pageVo.getStartRow(), pageVo.getRowCount(),
					rmv.getiPorName(), rmv.getDomainId(), rmv.getCategoryId(), rmv.getCategoryIds(),
					rmv.getResourceId(), rmv.getIsCustomResGroup(), pageVo.getSort(), pageVo.getOrder());
			pageVo.setTotalRecord(pageBo.getTotalRecord());
			List<ResourceMonitorVo> resourceVoList = new ArrayList<ResourceMonitorVo>();
			List<ResourceMonitorBo> monitorBoList = pageBo.getResourceMonitorBosExtends();
			for (int i = 0; monitorBoList != null && i < monitorBoList.size(); i++) {
				resourceVoList.add(instanceBoToResourceVo(monitorBoList.get(i)));
			}
			pageVo.setResourceMonitors(resourceVoList);
			pageVo.setResourceCategoryBos(pageBo.getResourceCategoryBos());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return toSuccess(pageVo);
	}

	private ResourceMonitorVo instanceBoToResourceVo(ResourceMonitorBo resourceMonitorBo) {

		ResourceMonitorVo resourceVo = new ResourceMonitorVo();
		resourceVo.setId(resourceMonitorBo.getInstanceId());
		resourceVo.setSourceName(resourceMonitorBo.getShowName());
		resourceVo.setIpAddress(resourceMonitorBo.getIp());
		resourceVo.setDomainName(null == domainApi.get(resourceMonitorBo.getDomainId()) ? ""
				: domainApi.get(resourceMonitorBo.getDomainId()).getName());
		resourceVo.setDomainId(resourceMonitorBo.getDomainId()); // 域ID需传到前端页面
		// resourceVo.setMonitorType(this.getMonitorTypeName(resourceMonitorBo.getCategoryId(),
		// resourceMonitorBo.getDiscoverWay()));
		resourceVo.setMonitorType(resourceMonitorBo.getResourceName());
		resourceVo.setInstanceState(resourceMonitorBo.getLifeState().toString());
		resourceVo.setCpuStatus(resourceMonitorBo.getCpuStatus());
		resourceVo.setCpuAvailability(resourceMonitorBo.getCpuAvailability());
		resourceVo.setMemoryStatus(resourceMonitorBo.getMemoryStatus());
		resourceVo.setMemoryAvailability(resourceMonitorBo.getMemoryAvailability());
		resourceVo.setResponseTime(resourceMonitorBo.getResponseTime());
		resourceVo.setInstanceStatus(resourceMonitorBo.getInstanceStatus());
		resourceVo.setResourceId(resourceMonitorBo.getResourceId());
		resourceVo.setHasRight(resourceMonitorBo.getHasRight());
		resourceVo.setCpuIsAlarm(resourceMonitorBo.getCpuIsAlarm());
		resourceVo.setMemoryIsAlarm(resourceMonitorBo.getMemoryIsAlarm());
		resourceVo.setDcsGroupName(resourceMonitorBo.getDcsGroupName());
		resourceVo.setHasTelSSHParams(resourceMonitorBo.getHasTelSSHParams());
		resourceVo.setSourceIp(resourceMonitorBo.getSourceIp());
		resourceVo.setDistIP(resourceMonitorBo.getDistIP());
		// 根据资源id获取责任人
		CustomProp prop = null;
		try {
			prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(), "liablePerson");
		} catch (InstancelibException e) {

		}
		User user = new User();
		if (prop != null) {
			String[] accountIds = prop.getValues();
			if (accountIds.length > 0) {
				user = userApi.get(Long.parseLong(accountIds[0]));
				if(user!=null){
					resourceVo.setLiablePerson(user.getName());
				}				
			}
		}
		// 根据资源id获取设备维修模式	
		try {
			prop=null;
			prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(), "maintainStaus");
		} catch (InstancelibException e) {

		}		
		if (prop != null) {					
			resourceVo.setMaintainStaus(prop.getValues()[0]);
			prop=null;			
			try{
				prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(), "maintainStartTime");
			}
			catch(InstancelibException e){
				
			}
			if(prop!=null){			
				resourceVo.setMaintainStartTime(prop.getValues()[0]);
			}
			prop=null;
			try{
				prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(), "maintainEndTime");
			}
			catch(InstancelibException e){
				
			}
			if(prop!=null){			
				resourceVo.setMaintainEndTime(prop.getValues()[0]);
			}
		}	
		return resourceVo;
	}

	private String getMonitorTypeName(String categoryId, DiscoverWayEnum discoverWay) {
		String monitorTypeAndWay = "";
		CategoryDef cd = capacityService.getCategoryById(categoryId);
		if (cd != null) {
			if (discoverWay != null) {
				return monitorTypeAndWay = cd.getName() + "(" + discoverWay.toString() + ")";
			}
			return monitorTypeAndWay = cd.getName();
		}
		return monitorTypeAndWay;
	}

	/**
	 * 资源发现参数
	 * 
	 * @param instanceId
	 * @return
	 */
	@RequestMapping("/getInstanceDiscoverParamter")
	public JSONObject getInstanceDiscoverParamter(long instanceId) {
		return toSuccess(resourceMonitorApi.getDiscoverParamter(instanceId));
	}

	/**
	 * 获取域、DCS关联关系数据，设置是否被选中
	 */
	@RequestMapping("getDomainDcs")
	public JSONObject getDomainDcs(Long domainId) {
		List<DomainDcs> ddList = stm_system_DomainApi.getDomainDcs(domainId);
		return toSuccess(ddList);
	}

	@RequestMapping(value = "/update_resource_domain")
	public JSONObject updateResourceDomain(ResourceMonitorVo monitorVo) {
		if (monitorVo.getId() == null || monitorVo.getDomainId() == null) {
			return toJsonObject(201, -1);
		}
		Map<Long, Long> argMap = new HashMap<Long, Long>();
		argMap.put(monitorVo.getId(), monitorVo.getDomainId());
		int code = 200;
		try {
			resourceInstanceService.updateResourceInstanceDomain(argMap);
		} catch (InstancelibException e) {
			e.printStackTrace();
			logger.error("调用接口失败", e);
			code = 202;
		}
		return toJsonObject(code, -1);
	}

	@RequestMapping(value = "/update_resource_domain_batch")
	public JSONObject updateResourceDomainBatch(long[] instanceIds, long dominId) {
		JSONObject rel = toJsonObject(200, -1);
		for (int i = 0; i < instanceIds.length; i++) {
			ResourceMonitorVo monitorVo = new ResourceMonitorVo();
			monitorVo.setId(instanceIds[i]);
			monitorVo.setDomainId(dominId);
			rel = updateResourceDomain(monitorVo);
			if (!"200".equals(rel.get("code").toString())) {
				break;
			}
		}
		return rel;
	}

	/**
	 * 保存资源责任人
	 * 
	 * @param instanceIds
	 * @param userId
	 * @return
	 */
	@RequestMapping("/saveLiablePerson")
	public JSONObject saveLiablePerson(long[] instanceIds, String userId) {
		String[] userIds = new String[] { userId };
		if (resourceMonitorApi.saveLiablePerson(instanceIds, userIds)) {
			return toJsonObject(200, -1);
		} else {
			return toJsonObject(202, -1);
		}

	}

	/**
	 * 清除资源责任人
	 * 
	 * @param instanceIds
	 * @return
	 */
	@RequestMapping("/clearLiablePerson")
	public JSONObject saveLiablePerson(long[] instanceIds) {
		if (resourceMonitorApi.clearLiablePerson(instanceIds)) {
			return toJsonObject(200, -1);
		} else {
			return toJsonObject(202, -1);
		}
	}
}
