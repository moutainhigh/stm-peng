package com.mainsteam.stm.profilelib.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.service.ResourceInstanceExtendService;
import com.mainsteam.stm.profilelib.service.ProfileExtendService;

/**
 * 添加资源实例删除拦截类
 * 
 * @author xiaoruqiang
 */
public class InstancelibInterceptorRemoveImpl implements InstancelibInterceptor {

	private static final Log logger = LogFactory
			.getLog(InstancelibInterceptorRemoveImpl.class);

	private ProfileExtendService profileExtendService;

	//private ProfileService profileService;
	
	private ResourceInstanceExtendService resourceInstanceExtendService;
	
	//private ProfileAutoRediscoverService profileAutoRediscoveryService;

//	public void setProfileService(ProfileService profileService) {
//		this.profileService = profileService;
//	}
	
	public void setProfileExtendService(ProfileExtendService profileExtendService) {
		this.profileExtendService = profileExtendService;
	}
	
//	public void setProfileAutoRediscoveryService(ProfileAutoRediscoverService profileAutoRediscoveryService) {
//		this.profileAutoRediscoveryService = profileAutoRediscoveryService;
//	}
	
	//private ResourceInstanceService resourceInstanceService;
	
//	public void setResourceInstanceService(
//			ResourceInstanceService resourceInstanceService) {
//		this.resourceInstanceService = resourceInstanceService;
//	}


	public void setResourceInstanceExtendService(
			ResourceInstanceExtendService resourceInstanceExtendService) {
		this.resourceInstanceExtendService = resourceInstanceExtendService;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) {
		if (instancelibEvent.getEventType() == EventEnum.INSTANCE_DELETE_EVENT) {
			try {
				List<Long> deleteIds = (List<Long>) instancelibEvent.getSource();
				//HashSet<String> resourceIds = new HashSet<String>();
				List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
				// 删除所有的子实例，如果删除的设备模型，再没有其他设备监控，该策略页面不可用显示出来。
				for (long deleteInstanceId : deleteIds) {
					ResourceInstance resourceInstance = resourceInstanceExtendService
							.getResourceInstanceById(deleteInstanceId);
					if(resourceInstance != null){
						//resourceIds.add(resourceInstance.getResourceId());
						instances.add(resourceInstance);
//						if(resourceInstance.getParentId()<=0){
//							//如果是主资源删除资源自动重新发现策略
//							profileAutoRediscoveryService.deleteAutoRediscoverProfileInstanceByInstanceId(resourceInstance.getId());
//						}
					}
				}
				// 根据模型id 查找设备，如果没有设备，策略状态变成无可用
				if (!deleteIds.isEmpty()) {
					profileExtendService.deleteInstances(instances);
//					List<ResourceInstance> resourceInstances = resourceInstanceExtendService.getExistParentInstanceByResourceId(resourceIds);
//					HashSet<String> result = null;
//					if(resourceInstances != null){
//						result = new HashSet<>();
//						for(ResourceInstance instance : resourceInstances){
//							result.add(instance.getResourceId());
//						}
//					}
//					if(result != null && !result.isEmpty()){
//						//过滤有资源实例的模型，剩下的就是没有实例的模型
//						resourceIds.removeAll(result);
//					}
//					if(!resourceIds.isEmpty()){
//						//没有实例的模型修改策略的状态
//						profileService.updateDefaultProfileStateByResourceId(new ArrayList<String>(resourceIds),false);
//					}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("deleteInstances error！", e);
				}
			}
		}
		if (instancelibEvent.getEventType() == EventEnum.INSTANCE_CHILDREN_DELETE_EVENT) {
			try {
				List<ResourceInstance> deleteIds = (List<ResourceInstance>) instancelibEvent.getSource();
				//HashSet<String> resourceIds = new HashSet<String>();
				List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
				// 删除所有的子实例，如果删除的设备模型，再没有其他设备监控，该策略页面不可用显示出来。
				for (ResourceInstance deleteInstanceId : deleteIds) {
					ResourceInstance resourceInstance = resourceInstanceExtendService
							.getResourceInstanceById(deleteInstanceId.getId());
					if(resourceInstance != null){
						instances.add(resourceInstance);
					}
				}
				// 根据模型id 查找设备，如果没有设备，策略状态变成无可用
				if (!deleteIds.isEmpty()) {
					profileExtendService.deleteInstances(instances);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("deleteInstances error！", e);
				}
			}
		}
	}
}
