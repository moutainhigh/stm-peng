//package com.mainsteam.stm.instancelib.service.interceptor;
//
//import java.util.List;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.alibaba.fastjson.JSON;
//import com.mainsteam.stm.alarm.event.AlarmEventMonitor;
//import com.mainsteam.stm.alarm.obj.AlarmEvent;
//import com.mainsteam.stm.alarm.obj.SysModuleEnum;
//import com.mainsteam.stm.caplib.CapacityService;
//import com.mainsteam.stm.caplib.common.CategoryDef;
//import com.mainsteam.stm.caplib.dict.CapacityConst;
//import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
//import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
//import com.mainsteam.stm.common.metric.InstanceStateService;
//import com.mainsteam.stm.instancelib.CompositeInstanceService;
//import com.mainsteam.stm.instancelib.ResourceInstanceService;
//import com.mainsteam.stm.instancelib.dao.InstanceDependenceRelationDAO;
//import com.mainsteam.stm.instancelib.dao.InstanceDependenceRelationResultDAO;
//import com.mainsteam.stm.instancelib.dao.pojo.InstanceDependencePO;
//import com.mainsteam.stm.instancelib.dao.pojo.InstanceDependenceResultPO;
//import com.mainsteam.stm.instancelib.exception.InstancelibException;
//import com.mainsteam.stm.instancelib.obj.CompositeInstance;
//import com.mainsteam.stm.instancelib.obj.Instance;
//import com.mainsteam.stm.instancelib.obj.InstanceDependenceResult;
//import com.mainsteam.stm.instancelib.obj.InstanceDependenceReultEnum;
//import com.mainsteam.stm.instancelib.obj.RelationLink;
//import com.mainsteam.stm.instancelib.obj.RelationNode;
//import com.mainsteam.stm.instancelib.obj.ResourceInstance;
//import com.mainsteam.stm.instancelib.ojbenum.ResourceTypeEnum;
//import com.mainsteam.stm.portal.business.state.api.IBizSerStateApi;
////import com.mainsteam.stm.portal.business.api.IBizServiceApi;
//import com.mainsteam.stm.state.obj.InstanceStateData;
//
//public class CalcRelationship implements AlarmEventMonitor {
//
//	private static final Log logger = LogFactory.getLog(CalcRelationship.class);
//
//	private InstanceDependenceRelationResultDAO instanceDependenceRelationResultDAO;
//
//	private InstanceDependenceRelationDAO instanceDependenceRelationDAO;
//
//	private InstanceStateService instanceStateService;
//
//	private ResourceInstanceService resourceInstanceService;
//
//	private CompositeInstanceService compositeInstanceService;
//
//	private CapacityService capacityService;
//	
//	private IBizSerStateApi bizSerStateApi;
//
//	@Override
//	public void handleEvent(AlarmEvent alarmEvent) {
//		// 只计算业务告警和资源设备告警
////		if (alarmEvent.getSysID() == SysModuleEnum.BUSSINESS
////				|| alarmEvent.getSysID() == SysModuleEnum.MONITOR) {
////			try {
////				calcRootRelation(alarmEvent);
////				calcAffectedRelation(alarmEvent);
////			} catch (Exception e) {
////				if(logger.isErrorEnabled()){
////					logger.error("CalcRelationship error", e);
////				}
////			}
////		}
//	}
//
//	private void calcRootRelation(AlarmEvent alarmEvent) throws Exception {
//		if (logger.isTraceEnabled()) {
//			StringBuilder b = new StringBuilder();
//			b.append("CalcRootRelation start alarmEventId=").append(
//					alarmEvent.getEventID());
//			b.append(" alarmEventState=").append(alarmEvent.getLevel().name());
//			b.append(" instanceId=").append(alarmEvent.getSourceID());
//			b.append(" sysModule=").append(alarmEvent.getSysID().name());
//			logger.trace(b);
//		}
//		if (alarmEvent.getLevel() != InstanceStateEnum.CRITICAL) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("Not calculated for root,alarmEventId="
//						+ alarmEvent.getEventID() + "alarmEventState="
//						+ alarmEvent.getLevel().name());
//			}
//			if (logger.isTraceEnabled()) {
//				logger.trace("CalcRootRelation end alarmEventId="
//						+ alarmEvent.getEventID() + "alarmEventState="
//						+ alarmEvent.getLevel().name());
//			}
//			return;
//		}
//
//		long targetInstanceId = Long.parseLong(alarmEvent.getSourceID());
//		Instance targetInstance = null;
//		if (alarmEvent.getSysID() == SysModuleEnum.MONITOR) {
//			targetInstance  = getResourceInstanceById(targetInstanceId);
//		} else {
//			targetInstance = getCompositeInstanceById(targetInstanceId);
//		}
//		InstanceDependenceResult instanceDependenceResult = new InstanceDependenceResult();
//		calcRoot(instanceDependenceResult, targetInstance);
//		String resultValue = JSON.toJSONString(instanceDependenceResult);
//		InstanceDependenceResultPO instanceDependenceResultPO = new InstanceDependenceResultPO();
//		instanceDependenceResultPO.setAlarmEventId(alarmEvent.getEventID());
//		instanceDependenceResultPO
//				.setResultType(InstanceDependenceReultEnum.ROOT.name());
//		instanceDependenceResultPO.setResultValue(resultValue);
//		try {
//			instanceDependenceRelationResultDAO
//					.insertDependenceResult(instanceDependenceResultPO);
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {
//				logger.error("insertDependenceResult error.", e);
//			}
//		}
//		if (logger.isTraceEnabled()) {
//			StringBuilder b = new StringBuilder();
//			b.append("CalcRootRelation end alarmEventId=").append(
//					alarmEvent.getEventID());
//			b.append(" alarmEventState=").append(alarmEvent.getLevel().name());
//			b.append(" sysModule=").append(alarmEvent.getSysID().name());
//			logger.trace(b);
//		}
//	}
//
//	private void calcAffectedRelation(AlarmEvent alarmEvent) throws Exception{
//		if (logger.isTraceEnabled()){
//			StringBuilder b = new StringBuilder();
//			b.append("calcAffectedRelation start alarmEventId=").append(
//					alarmEvent.getEventID());
//			b.append(" calcAffectedRelation=").append(alarmEvent.getLevel().name());
//			b.append(" instanceId=").append(alarmEvent.getSourceID());
//			b.append(" sysModule=").append(alarmEvent.getSysID().name());
//			logger.trace(b);
//		}
//		if (alarmEvent.getLevel() != InstanceStateEnum.CRITICAL) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("Not calculated for Affected,alarmEventId="
//						+ alarmEvent.getEventID() + "alarmEventState="
//						+ alarmEvent.getLevel().name());
//			}
//			return;
//		}
//		long sourceInstanceId = Long.parseLong(alarmEvent.getSourceID());
//		Instance targetInstance = null;
//		InstanceDependenceResult instanceDependenceResult = new InstanceDependenceResult();
//		if (alarmEvent.getSysID() == SysModuleEnum.MONITOR) {
//			targetInstance  = getResourceInstanceById(sourceInstanceId);
//		} else {
//			targetInstance = getCompositeInstanceById(sourceInstanceId);
//		}
//		caclAffected(instanceDependenceResult, targetInstance,
//				sourceInstanceId);
//		String resultValue = JSON.toJSONString(instanceDependenceResult);
//		InstanceDependenceResultPO instanceDependenceResultPO = new InstanceDependenceResultPO();
//		instanceDependenceResultPO.setAlarmEventId(alarmEvent.getEventID());
//		instanceDependenceResultPO
//				.setResultType(InstanceDependenceReultEnum.AFFECTED.name());
//		instanceDependenceResultPO.setResultValue(resultValue);
//		try {
//			instanceDependenceRelationResultDAO
//					.insertDependenceResult(instanceDependenceResultPO);
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {
//				logger.error("insertDependenceResult error.", e);
//			}
//		}
//		if (logger.isTraceEnabled()){
//			StringBuilder b = new StringBuilder();
//			b.append("calcAffectedRelation end alarmEventId=").append(
//					alarmEvent.getEventID());
//			b.append(" calcAffectedRelation=").append(alarmEvent.getLevel().name());
//			b.append(" instanceId=").append(alarmEvent.getSourceID());
//			b.append(" sysModule=").append(alarmEvent.getSysID().name());
//			logger.trace(b);
//		}
//	}
//
//	private void calcRoot(InstanceDependenceResult instanceDependenceResult,
//			Instance targetInstance) throws Exception {
//		RelationNode node = new RelationNode();
//		// 获取上一层的关系
//		List<InstanceDependencePO> instanceDependencePOs = null;
//		ResourceInstance resourceInstance = null;
//		long previousId = 0;
//		boolean findParent = true;
//		try {
//			if (targetInstance instanceof ResourceInstance) {
//				resourceInstance = (ResourceInstance) targetInstance;
//				previousId = resourceInstance.getId();
//				ResourceInstance parent = null;
//				if (resourceInstance.getParentId() != 0) {
//					// 子
//					long parentId = resourceInstance.getParentId();
//					parent = resourceInstanceService
//								.getResourceInstance(parentId);
//						// 是网络设备接口，找接口实例Id。是主机接口，找主机实例Id
//						CategoryDef categotyDef = capacityService
//								.getCategoryById(parent.getCategoryId());
//						if (ResourceTypeConsts.TYPE_NETINTERFACE
//								.equals(resourceInstance.getChildType())) {
//								findParent = false;
//						}else{
//							previousId = resourceInstance.getParentId();
//							node.setParentDeviceType(categotyDef.getParentCategory().getId());
//							node.setParentIp(parent.getDiscoverIP());
//							node.setParentShowName(parent.getShowName());
//						}
//				}else{
//					previousId = resourceInstance.getId();
//					node.setParentDeviceType(resourceInstance.getParentCategoryId());
//					node.setParentIp(resourceInstance.getDiscoverIP());
//					node.setParentShowName(resourceInstance.getShowName());
//				}
//				if(findParent){
//					//找父实例Id
//					node.setParentId(previousId);
//					instanceDependencePOs = instanceDependenceRelationDAO
//								.getPreviousDependence(previousId);
//				}else{
//					//找子
//					node.setChildDeviceName(resourceInstance.getName());
//					node.setChildDeviceType(resourceInstance.getChildType());
//					node.setChildDeviceId(previousId);
//					node.setParentDeviceType(CapacityConst.NETWORK_DEVICE);
//					node.setParentShowName(parent.getShowName());
//						instanceDependencePOs = instanceDependenceRelationDAO
//								.getPreviousChildDependence(previousId);			
//				}
//				node.setId(previousId);
//			} else if (targetInstance instanceof CompositeInstance) {
//				node.setParentId(targetInstance.getId());
//				node.setParentDeviceType(ResourceTypeEnum.BUSINESS.name());
//				node.setParentIp("");
//				node.setParentShowName(targetInstance.getName());
//				node.setId(targetInstance.getId());
//				instanceDependencePOs = instanceDependenceRelationDAO
//							.getPreviousDependence(targetInstance.getId());
//			}
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {
//				logger.error("", e);
//			}
//			throw e;
//		}
//		node.setDown(true);
//		instanceDependenceResult.getNodes().add(node);
//		/*
//		 * 获取设备状态，如果上层设备是可用，那么是设备本身问题。 如果设备不可用，继续查找上一层设备。直到找到设备可用的状态。返回路径。
//		 */
//		if (instanceDependencePOs != null && !instanceDependencePOs.isEmpty()) {
//			for (InstanceDependencePO instanceDependencePO : instanceDependencePOs) {
//				// 如果核心设备没有设置，连线两端是网络设备不用参加计算
//				if ("0".equals(instanceDependencePO.getIsUse())) {
//					if (logger.isDebugEnabled()) {
//						logger.debug("Not set the core node:relationId="
//								+ instanceDependencePO.getRelationId());
//					}
//					continue;
//				}
//				// 找父设备
//				long srcInstanceId = instanceDependencePO.getSourceResource();
//				Instance nextResourceInstance = null;
//				if (instanceDependencePO.getSourceResourceType().equals(
//						ResourceTypeEnum.BUSINESS)) {
//					//业务
//					InstanceStateEnum state = bizSerStateApi.getStateById(srcInstanceId);
//					if(state.getStateVal() != InstanceStateEnum.CRITICAL.getStateVal()){
//						node.setRoot(true);
//						continue;
//					}
//					nextResourceInstance = getCompositeInstanceById(srcInstanceId);
//				} else {
//					InstanceStateData instanceStateData =  instanceStateService.getState(srcInstanceId);
//					if (instanceDependencePO.getSourceResourceType().equals(
//							ResourceTypeEnum.NetworkDevice)) {
//						// 先查找网络设备子设备
//						InstanceStateData childInstanceStateData = instanceStateService
//								.getState(instanceDependencePO
//										.getSourceChildResource());
//						if (childInstanceStateData.getState() != InstanceStateEnum.CRITICAL) {
//							// 继续下一个分支查找。当前分支查找结束,根源设备
//							node.setRoot(true);
//							continue;
//						}
//					}
//					//查找当前网络设备所有的端口，过滤受影响的连线，拿到依赖的连线(一般是网络设备的连线)然后再进行计算
//					//非网络设备，父实例直接判断值
//					if (instanceStateData.getState() != InstanceStateEnum.CRITICAL) {
//						// 继续下一个分支查找。当前分支查找结束,根源设备
//						node.setRoot(true);
//						continue;
//					}
//					nextResourceInstance = getResourceInstanceById(srcInstanceId);
//				}
//				// 如果上一层设备状态可用，停止查找，返回当前路径
//				// 继续查找，上一层设备
//				if (nextResourceInstance != null) {
//					RelationLink link = new RelationLink(
//							instanceDependencePO.getTargetResource(),
//							instanceDependencePO.getSourceResource());
//					instanceDependenceResult.getLinks().add(link);
//					calcRoot(instanceDependenceResult, nextResourceInstance);
//				}
//			}
//		} else {
//			node.setRoot(true);
//		}
//	}
//
//	private void caclAffected(
//			InstanceDependenceResult instanceDependenceResult,
//			Instance sourceInstance, long downDeviceId) throws Exception {
//		// 关系
//		List<InstanceDependencePO> instanceDependencePOs = null;
//		RelationNode node = new RelationNode();
//		// 获取上一层的关系
//		ResourceInstance resourceInstance = null;
//		boolean findParent = true;
//		long nextId = 0;
//		try {
//			if (sourceInstance instanceof ResourceInstance) {
//				resourceInstance = (ResourceInstance) sourceInstance;
//				nextId = resourceInstance.getId();
//				ResourceInstance parent = null;
//				if (resourceInstance.getParentId() != 0) {
//					// 子
//					if(logger.isDebugEnabled()){
//						logger.debug("caclAffected:child start");
//					}
//					long parentId = resourceInstance.getParentId();
//					parent = resourceInstanceService
//							.getResourceInstance(parentId);
//					// 是网络设备接口，找接口实例Id。是主机接口，找主机实例Id
//					CategoryDef categotyDef = capacityService
//							.getCategoryById(parent.getCategoryId());
//					if (ResourceTypeConsts.TYPE_NETINTERFACE
//							.equals(resourceInstance.getChildType())) {
//							findParent = false;
//							if(logger.isDebugEnabled()){
//								logger.debug("caclAffected:child");
//							}
//					}else{
//						nextId = parentId;
//						node.setParentDeviceType(categotyDef.getParentCategory().getId());
//						node.setParentIp(parent.getDiscoverIP());
//						node.setParentShowName(parent.getShowName());
//						if(logger.isDebugEnabled()){
//							logger.debug("caclAffected:parent");
//						}
//					}
//				}else{
//					if(logger.isDebugEnabled()){
//						logger.debug("caclAffected:parent start");
//					}
// 					node.setParentDeviceType(resourceInstance.getParentCategoryId());
//					node.setParentIp(resourceInstance.getDiscoverIP());
//					node.setParentShowName(resourceInstance.getShowName());
//				}
//				if(findParent){
//					//找父实例Id
//					node.setParentId(nextId);
//					instanceDependencePOs = instanceDependenceRelationDAO
//								.getNextDependence(nextId);
//					if(logger.isDebugEnabled()){
//						logger.debug("caclAffected:parent end");
//					}
//				}else{
//					//找子
//					node.setChildDeviceName(resourceInstance.getName());
//					node.setChildDeviceType(resourceInstance.getChildType());
//					node.setChildDeviceId(nextId);
//					node.setParentDeviceType(CapacityConst.NETWORK_DEVICE);
//					node.setParentShowName(parent.getShowName());
//					instanceDependencePOs = instanceDependenceRelationDAO
//								.getNextChildDependence(nextId);
//					if(logger.isDebugEnabled()){
//						logger.debug("caclAffected:child");
//					}
//				}
//				node.setId(nextId);
//			} else if (sourceInstance instanceof CompositeInstance) {
//				node.setParentId(sourceInstance.getId());
//				node.setParentDeviceType(ResourceTypeEnum.BUSINESS.name());
//				node.setParentIp("");
//				node.setParentShowName(sourceInstance.getName());
//				instanceDependencePOs = instanceDependenceRelationDAO.getNextDependence(sourceInstance.getId());
//			}
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {
//				logger.error("", e);
//			}
//			throw e;
//		}
//		node.setDown(true);
//		if (sourceInstance.getId() == downDeviceId) {
//			node.setDown(true);
//		} else {
//			node.setDown(false);
//		}
//		instanceDependenceResult.getNodes().add(node);
//		/**
//		 * 找到设备下面所有的子节点，直到找到底为止，返回路径。
//		 */
//		if (instanceDependencePOs != null && !instanceDependencePOs.isEmpty()) {
//			for (InstanceDependencePO instanceDependencePO : instanceDependencePOs) {
//				if ("0".equals(instanceDependencePO.getIsUse())) {
//					continue;
//				}
//				long targetInstanceId = instanceDependencePO
//						.getTargetResource();
//				Instance nextResourceInstance = null;
//				if (instanceDependencePO.getSourceResourceType().equals(
//						ResourceTypeEnum.BUSINESS)) {
//					nextResourceInstance = getCompositeInstanceById(targetInstanceId);
//				}else{
//					nextResourceInstance = getResourceInstanceById(targetInstanceId);
//				}
//				// 继续查找，下一层设备
//				RelationLink link = new RelationLink(
//						instanceDependencePO.getSourceResource(),
//						instanceDependencePO.getTargetResource());
//				instanceDependenceResult.getLinks().add(link);
//				if(nextResourceInstance != null){
//					caclAffected(instanceDependenceResult,
//							nextResourceInstance, downDeviceId);
//				}
//			}
//		}
//	}
//
//	private ResourceInstance getResourceInstanceById(long instanceId) {
//		try {
//			return resourceInstanceService.getResourceInstance(instanceId);
//		} catch (InstancelibException e) {
//			if (logger.isErrorEnabled()) {
//				logger.error("getResourceInstanceById error!", e);
//			}
//		}
//		return null;
//	}
//
//	private CompositeInstance getCompositeInstanceById(long instanceId) {
//		try {
//			return compositeInstanceService.getCompositeInstance(instanceId);
//		} catch (InstancelibException e) {
//			if (logger.isErrorEnabled()) {
//				logger.error("getCompositeInstanceById error!", e);
//			}
//		}
//		return null;
//	}
//
//	public void setInstanceDependenceRelationDAO(
//			InstanceDependenceRelationDAO instanceDependenceRelationDAO) {
//		this.instanceDependenceRelationDAO = instanceDependenceRelationDAO;
//	}
//
//	public void setInstanceStateService(
//			InstanceStateService instanceStateService) {
//		this.instanceStateService = instanceStateService;
//	}
//
//	public void setResourceInstanceService(
//			ResourceInstanceService resourceInstanceService) {
//		this.resourceInstanceService = resourceInstanceService;
//	}
//
//	public InstanceStateService getInstanceStateService() {
//		return instanceStateService;
//	}
//
//	public void setInstanceDependenceRelationResultDAO(
//			InstanceDependenceRelationResultDAO instanceDependenceRelationResultDAO) {
//		this.instanceDependenceRelationResultDAO = instanceDependenceRelationResultDAO;
//	}
//
//	public void setCapacityService(CapacityService capacityService) {
//		this.capacityService = capacityService;
//	}
//
//	public void setCompositeInstanceService(
//			CompositeInstanceService compositeInstanceService) {
//		this.compositeInstanceService = compositeInstanceService;
//	}
//
//	public void setBizSerStateApi(IBizSerStateApi bizSerStateApi) {
//		this.bizSerStateApi = bizSerStateApi;
//	}
//
//	
//}
