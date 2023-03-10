package com.mainsteam.stm.portal.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.api.BizCanvasApi;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.bo.BizCanvasBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasDataBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasLinkBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeTree;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;
import com.mainsteam.stm.portal.business.dao.IBizCanvasDao;
import com.mainsteam.stm.portal.business.service.util.BizNodeTypeDefine;
import com.mainsteam.stm.portal.business.service.util.BizStatusDefine;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;


public class BizCanvasImpl implements BizCanvasApi,InstancelibInterceptor{

	@Resource
	private IBizCanvasDao bizCanvasDao;
	
	@Resource
	private ISequence bizNodeSequence;
	
	@Resource
	private ISequence bizLinkSequence;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ProfileService profileService;
	
	@Resource
	private BizMainApi bizMainApi;
	
	@Resource
	private CustomMetricService customMetricService;
	
	@Resource
	private InstanceStateService instanceStateService;
	
	@Resource
	private MetricStateService metricStateService;
	
	private static final Log logger = LogFactory.getLog(BizCanvasImpl.class);
	
	@Override
	public boolean deleteCanvas(long bizId) {
		
		try {
			//???????????????????????????
			List<Long> nodeIds = bizCanvasDao.getCanvasNodeIds(bizId);
			
			//????????????????????????
			bizCanvasDao.deleteCanvasInfoByBizId(bizId);
			
			//????????????????????????
			bizCanvasDao.deleteCanvasNodeByBizId(bizId);
			
			//??????????????????
			if(nodeIds != null && nodeIds.size() > 0){
				for(long nodeId : nodeIds){
					bizCanvasDao.deleteCanvasLinkByFromNode(nodeId);
					bizCanvasDao.deleteCanvasLinkByToNode(nodeId);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		
		return true;
	}

	@Override
	public long insertCanvasNode(BizCanvasNodeBo canvasNode) {
		long newId = bizNodeSequence.next();
		canvasNode.setId(newId);
		if(canvasNode.getFileId() <= 0){
			//????????????
			if(canvasNode.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				try {
					ResourceInstance instance = resourceInstanceService.getResourceInstance(canvasNode.getInstanceId());
					CategoryDef def = capacityService.getCategoryById(instance.getCategoryId());
					System.out.println(def.getParentCategory().getId());
					if(def.getParentCategory().getId().equals(CapacityConst.HOST)){
						//??????
						canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_2.getFileId());
					}else if(def.getParentCategory().getId().equals(CapacityConst.DATABASE)){
						//?????????
						canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_1.getFileId());
					}else if(def.getParentCategory().getId().equals(CapacityConst.MIDDLEWARE) || 
							def.getParentCategory().getId().equals(CapacityConst.J2EEAPPSERVER) || 
							def.getParentCategory().getId().equals(CapacityConst.WEBSERVER)){
						//?????????
						if(def.getId().equals("CICS")){
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_8.getFileId());
						}else if(def.getId().equals("Ice")){
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_9.getFileId());
						}else if(def.getId().equals("SharePoint")){
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_10.getFileId());
						}else if(def.getId().equals("Tomcat")){
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_11.getFileId());
						}else if(def.getId().equals("TongLINKQ")){
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_12.getFileId());
						}else if(def.getId().equals("Tuxedo")){
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_13.getFileId());
						}else if(def.getId().equals("Weblogic")){
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_14.getFileId());
						}else if(def.getId().equals("WebSphereMQ")){
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_15.getFileId());
						}else{
							canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_3.getFileId());
						}
					}else if(def.getParentCategory().getId().equals(CapacityConst.NETWORK_DEVICE)){
						//????????????
						canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_4.getFileId());
					}else if(def.getParentCategory().getId().equals(CapacityConst.STORAGE)){
						//??????
						canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_5.getFileId());
					}else if(def.getParentCategory().getParentCategory().getId().equals(CapacityConst.VM)){
						//?????????
						canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_6.getFileId());
					}else if(def.getId().equals(CapacityConst.URL)){
						//URL
						canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_7.getFileId());
					}else{
						canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_RES_IMG_3.getFileId());
					}
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
			}else{
				canvasNode.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_MAIN_IMG_1.getFileId());
			}
		}
		
		if(bizCanvasDao.insertCanvasNode(canvasNode) > 0){
			return newId;
		}else{
			return -1;
		}
	}

	@Override
	public long insertCanvasLink(BizCanvasLinkBo canvasLink) {
		
		if(canvasLink.getFromNode() <= 0 || canvasLink.getToNode() <= 0 || 
				bizCanvasDao.getCanvasLinkCountByPoints(canvasLink) <= 0){
			long newId = bizLinkSequence.next();
			canvasLink.setId(newId);
			if(bizCanvasDao.insertCanvasLink(canvasLink) > 0){
				return newId;
			}else{
				return -1;
			}
		}else{
			return -1;
		}
	}

	@Override
	public boolean updateCanvas(BizCanvasDataBo canvasData) {
		
		try {
			BizCanvasBo canvas = new BizCanvasBo();
			canvas.setId(canvasData.getId());
			canvas.setAttr(canvasData.getAttr());
			
			bizCanvasDao.updateCanvasInfo(canvas);
			
			List<BizCanvasNodeBo> nodeList = canvasData.getNodes();
			if(nodeList != null && nodeList.size() > 0){
				for(BizCanvasNodeBo canvasNode : nodeList){
					bizCanvasDao.updateCanvasNodeAttrInfo(canvasNode);
				}
			}
			
			List<BizCanvasLinkBo> linkList = canvasData.getLinks();
			if(linkList != null && linkList.size() > 0){
				for(BizCanvasLinkBo link : linkList){
					//????????????????????????????????????
					bizCanvasDao.updateCanvasLink(link);
				}
			}
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		
		return true;
	}

	@Override
	public BizCanvasDataBo getCanvas(long bizId) {

		BizCanvasDataBo canvasData = new BizCanvasDataBo();
		
		BizCanvasBo canvas = bizCanvasDao.getCanvasInfoByBizId(bizId);
		
		canvasData.setId(canvas.getId());
		canvasData.setBizId(bizId);
		canvasData.setAttr(canvas.getAttr());
		
		List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(bizId);
		
		if(nodes == null || nodes.size() <= 0){
			canvasData.setNodes(new ArrayList<BizCanvasNodeBo>());
			canvasData.setLinks(new ArrayList<BizCanvasLinkBo>());
			return canvasData;
		}
		
		canvasData.setNodes(nodes);
		
		List<Long> nodeIds = new ArrayList<>();
		
		for(BizCanvasNodeBo node : nodes){
			nodeIds.add(node.getId());
		}
		
		Set<BizCanvasLinkBo> linkSet = new HashSet<BizCanvasLinkBo>();
		
		List<BizCanvasLinkBo> fromLinks = bizCanvasDao.getCanvasLinksByFromNode(nodeIds);
		
		if(fromLinks != null && fromLinks.size() >= 0){
			linkSet.addAll(fromLinks);
		}
		
		List<BizCanvasLinkBo> toLinks = bizCanvasDao.getCanvasLinksByToNode(nodeIds);
		
		if(toLinks != null && toLinks.size() >= 0){
			linkSet.addAll(toLinks);
		}
		
		List<BizCanvasLinkBo> linkList = new ArrayList<>(linkSet);
		
		canvasData.setLinks(linkList);

		return canvasData;
	}

	@Override
	public List<BizCanvasNodeBo> getCanvasNode(long bizId) {
		// TODO Auto-generated method stub
		return bizCanvasDao.getCanvasNodes(bizId);
	}

	@Override
	public BizCanvasNodeBo getCanvasNodeById(long id) {
		// TODO Auto-generated method stub
		return bizCanvasDao.getCanvasNode(id);
	}

	@Override
	public boolean deleteCanvasNode(long[] ids) {
		
		List<Long> instanceAndBizNodeList = new ArrayList<Long>();
		long bizId = -1;
		
		BizCanvasNodeBo firstDeleteNode = null;
		
		for(long id : ids){
			
			BizCanvasNodeBo canvasNode = bizCanvasDao.getCanvasNode(id);
			
			if(canvasNode.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE 
					|| canvasNode.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE
					|| canvasNode.getNodeType() == BizNodeTypeDefine.DELETE_INSTANCE_TYPE){
				if(firstDeleteNode == null && canvasNode.getNodeType() != BizNodeTypeDefine.DELETE_INSTANCE_TYPE){
					firstDeleteNode = canvasNode;
				}
				instanceAndBizNodeList.add(id);
				bizId = canvasNode.getBizId();
			}
			
			if(bizCanvasDao.deleteCanvasNode(id) <= 0){
				return false;
			}
			
			//????????????????????????
			bizCanvasDao.deleteCanvasNodeBindRelation(id);
			
		}
		
		if(instanceAndBizNodeList.size() > 0){
			//??????????????????????????????????????????
			bizMainApi.updateStatusDefineByDeleteNode(instanceAndBizNodeList,bizId,firstDeleteNode);
		}
		return true;
	}
	
	@Override
	public boolean deleteCanvasLink(String[] ids) {
		
		for(String id : ids){
			
			if(bizCanvasDao.deleteCanvasLink(Long.parseLong(id)) <= 0){
				return false;
			}
			
		}
		
		return true;
	}

	/**
	 * ?????????????????????????????????
	 */
	@Override
	public BizInstanceNodeBo getInstanceNode(long canvasNodeId) {
		// TODO Auto-generated method stub
		return bizCanvasDao.getInstanceNode(canvasNodeId);
	}

	/**
	 * ????????????????????????????????????
	 */
	@Override
	public List<BizInstanceNodeTree> getSelectChildInstanceTree(long canvasNodeId) {
		
		List<BizInstanceNodeTree> instanceTree = new ArrayList<BizInstanceNodeTree>();
		
		List<BizNodeMetricRelBo> relList = bizCanvasDao.getChildInstanceBindRelation(canvasNodeId);
		BizCanvasNodeBo node = bizCanvasDao.getCanvasNode(canvasNodeId);
		ResourceInstance instance = null;
		try {
			instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}
		
		ResourceDef resourceDef = capacityService.getResourceDefById(instance.getResourceId());
		
		List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
		
		for(BizNodeMetricRelBo nodeRel : relList){
			try {
				ResourceInstance childInstance = resourceInstanceService.getResourceInstance(nodeRel.getChildInstanceId());
				instances.add(childInstance);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		//?????????
		if(resourceDef.getChildResourceDefs() == null){
			return instanceTree;
		}
		for(ResourceDef childResourceDef : resourceDef.getChildResourceDefs()){
			
			BizInstanceNodeTree resourceNode = new BizInstanceNodeTree();
			resourceNode.setId(childResourceDef.getId());
			resourceNode.setPid(null);
			resourceNode.setName(childResourceDef.getName());
			
			List<BizInstanceNodeTree> childInstanceTree = new ArrayList<BizInstanceNodeTree>();
			
			for(ResourceInstance nodeRel : instances){
				if(nodeRel.getResourceId().equals(childResourceDef.getId())){
					BizInstanceNodeTree instanceNode = new BizInstanceNodeTree();
					instanceNode.setId(nodeRel.getId() + "");
					instanceNode.setPid(childResourceDef.getId());
					instanceNode.setName(nodeRel.getShowName() == null ? nodeRel.getName() : nodeRel.getShowName());
					instanceNode.setStatus(nodeRel.getLifeState() == InstanceLifeStateEnum.MONITORED ? 0 : 1);
					
					childInstanceTree.add(instanceNode);
				}
			}
			
			if(childInstanceTree != null && childInstanceTree.size() > 0){
				resourceNode.setChildren(childInstanceTree);
				instanceTree.add(resourceNode);
			}
		}
		
		return instanceTree;
	}

	/**
	 * ?????????????????????????????????
	 */
	@Override
	public List<BizInstanceNodeTree> getSelectMetricTree(long canvasNodeId) {
		
		List<BizInstanceNodeTree> instanceTree = new ArrayList<BizInstanceNodeTree>();
		
		List<BizNodeMetricRelBo> relList = bizCanvasDao.getChildInstanceMetricBindRelation(canvasNodeId);
		
		Set<Long> childInstanceList = new HashSet<Long>();
		
		for(BizNodeMetricRelBo nodeRel : relList){
			if(nodeRel.getChildInstanceId() <= 0){
				//??????????????????
				try {
					
					BizCanvasNodeBo node = bizCanvasDao.getCanvasNode(canvasNodeId);
					
					ResourceInstance childInstance = resourceInstanceService.getResourceInstance(node.getInstanceId());
					ResourceMetricDef metricDef = capacityService.getResourceMetricDef(childInstance.getResourceId(), nodeRel.getMetricId());
					
					if(metricDef == null){
						//????????????????????????
						try {
							CustomMetric customMetric = customMetricService.getCustomMetric(nodeRel.getMetricId());
							if(customMetric != null){
								BizInstanceNodeTree instanceNode = new BizInstanceNodeTree();
								instanceNode.setId(customMetric.getCustomMetricInfo().getId());
								instanceNode.setPid(null);
								instanceNode.setName(customMetric.getCustomMetricInfo().getName());
								instanceNode.setStatus(customMetric.getCustomMetricInfo().isAlert() ? 0 : 1);
								
								instanceTree.add(instanceNode);
							}
						} catch (CustomMetricException e) {
							logger.error(e.getMessage(),e);
						}
						
					}else{
						BizInstanceNodeTree instanceNode = new BizInstanceNodeTree();
						instanceNode.setId(metricDef.getId());
						instanceNode.setPid(null);
						instanceNode.setName(metricDef.getName());
						ProfileMetric profileMetric = profileService.getMetricByInstanceIdAndMetricId(node.getInstanceId(), metricDef.getId());
						if(profileMetric == null){
							instanceNode.setStatus(1);
						}else{
							instanceNode.setStatus(profileMetric.isAlarm() ? 0 : 1);
						}
						
						instanceTree.add(instanceNode);
						
					}
					
					
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				} catch (ProfilelibException e) {
					logger.error(e.getMessage(),e);
				}
			}else{
				//??????????????????
				
				childInstanceList.add(nodeRel.getChildInstanceId());
				
			}
		}
		
		
		for(long id : childInstanceList){
			try {
				ResourceInstance childInstance = resourceInstanceService.getResourceInstance(id);
				
				BizInstanceNodeTree instanceNode = new BizInstanceNodeTree();
				instanceNode.setId(childInstance.getId() + "");
				instanceNode.setPid(null);
				instanceNode.setName(childInstance.getShowName() == null ? childInstance.getName() : childInstance.getShowName());
				
				List<BizInstanceNodeTree> metricTree = new ArrayList<BizInstanceNodeTree>();
				//????????????
				for(BizNodeMetricRelBo nodeMetricRel : relList){
					if(nodeMetricRel.getChildInstanceId() == id){
						
						ResourceMetricDef metricDef = capacityService.getResourceMetricDef(childInstance.getResourceId(), nodeMetricRel.getMetricId());
						
						BizInstanceNodeTree metricNode = new BizInstanceNodeTree();
						metricNode.setId(metricDef.getId() + "_" + childInstance.getId());
						metricNode.setPid(childInstance.getId() + "");
						metricNode.setName(metricDef.getName());
						ProfileMetric profileMetric = profileService.getMetricByInstanceIdAndMetricId(id, metricDef.getId());
						if(profileMetric == null){
							metricNode.setStatus(1);
						}else{
							metricNode.setStatus(profileMetric.isAlarm() ? 0 : 1);
							
						}
						
						metricTree.add(metricNode);
						
					}
				}
				
				instanceNode.setChildren(metricTree);
				
				instanceTree.add(instanceNode);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		return instanceTree;
	}

	@Override
	public List<BizInstanceNodeTree> getUnSelectChildInstanceTree(long canvasNodeId,String childResource,String searchContent) {
		
		List<BizInstanceNodeTree> instanceTree = new ArrayList<BizInstanceNodeTree>();
		
		List<BizNodeMetricRelBo> relList = bizCanvasDao.getChildInstanceBindRelation(canvasNodeId);
		BizCanvasNodeBo node = bizCanvasDao.getCanvasNode(canvasNodeId);
		ResourceInstance instance = null;
		try {
			instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}
		
		List<Long> selectInstanceId = new ArrayList<>();
		
		for(BizNodeMetricRelBo nodeRel : relList){
			selectInstanceId.add(nodeRel.getChildInstanceId());
		}
		
		//?????????
		ResourceDef resourceDef = capacityService.getResourceDefById(instance.getResourceId());
		if(resourceDef.getChildResourceDefs() == null){
			return instanceTree;
		}
		for(ResourceDef childResourceDef : resourceDef.getChildResourceDefs()){
			
			if(childResource != null && !childResource.equals("")){
				if(!(childResourceDef.getId().equals(childResource))){
					continue;
				}
			}
			
			BizInstanceNodeTree resourceNode = new BizInstanceNodeTree();
			resourceNode.setId(childResourceDef.getId());
			resourceNode.setPid(null);
			resourceNode.setName(childResourceDef.getName());
			
			List<BizInstanceNodeTree> childInstanceTree = new ArrayList<BizInstanceNodeTree>();
			
			try {
				List<ResourceInstance> childResourceInstances = resourceInstanceService.getResourceInstanceByResourceId(childResourceDef.getId());
				
				for(ResourceInstance childResourceInstance : childResourceInstances){
					if(!(childResourceInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))
							&& !(childResourceInstance.getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED))){
						continue;
					}
					if(node.getInstanceId() != childResourceInstance.getParentId()){
						continue;
					}
					if(selectInstanceId.contains(childResourceInstance.getId())){
						continue;
					}
					String instanceName = childResourceInstance.getShowName() == null ? childResourceInstance.getName() : childResourceInstance.getShowName();
					if(searchContent != null && !searchContent.equals("")){
						if(!(instanceName.toUpperCase().contains(searchContent.toUpperCase()))){
							continue;
						}
					}
					BizInstanceNodeTree instanceNode = new BizInstanceNodeTree();
					instanceNode.setId(childResourceInstance.getId() + "");
					instanceNode.setPid(childResourceDef.getId());
					instanceNode.setName(instanceName);
					instanceNode.setStatus(childResourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED ? 0 : 1);
					
					childInstanceTree.add(instanceNode);
				}
				
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
			
			if(childInstanceTree != null && childInstanceTree.size() > 0){
				resourceNode.setChildren(childInstanceTree);
				instanceTree.add(resourceNode);
			}
		}
		
		return instanceTree;
		
	}

	@Override
	public List<BizInstanceNodeTree> getUnSelectMetricTree(long canvasNodeId,String childResource,String searchContent) {
		
		List<BizInstanceNodeTree> instanceTree = new ArrayList<BizInstanceNodeTree>();
		
		List<BizNodeMetricRelBo> relList = bizCanvasDao.getChildInstanceMetricBindRelation(canvasNodeId);
		
		BizCanvasNodeBo node = bizCanvasDao.getCanvasNode(canvasNodeId);
		
		ResourceInstance instance = null;
		try {
			instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}
		ResourceDef resourceDef = capacityService.getResourceDefById(instance.getResourceId());
		ResourceMetricDef[] metrics = resourceDef.getMetricDefs();
		if(metrics != null && metrics.length > 0){
			
			out : for(ResourceMetricDef metric : metrics){
				
//				if(metric.getMetricType().equals(MetricTypeEnum.InformationMetric)){
//					continue;
//				}
				
				for(BizNodeMetricRelBo nodeRel : relList){
					
					if(nodeRel.getChildInstanceId() <= 0){
						
						if(metric.getId().equals(nodeRel.getMetricId())){
							continue out;
						}
						
					}
					
				}
				
				if(searchContent != null && !searchContent.equals("")){
					if(!(metric.getName().toUpperCase().contains(searchContent.toUpperCase()))){
						continue;
					}
				}
				
				BizInstanceNodeTree instanceNode = new BizInstanceNodeTree();
				instanceNode.setId(metric.getId());
				instanceNode.setPid(null);
				instanceNode.setName(metric.getName());
				ProfileMetric profileMetric = null;
				try {
					profileMetric = profileService.getMetricByInstanceIdAndMetricId(node.getInstanceId(), metric.getId());
				} catch (ProfilelibException e) {
					logger.error(e.getMessage(),e);
				}
				if(profileMetric == null){
					
					instanceNode.setStatus(1);
				}else{
					instanceNode.setStatus(profileMetric.isAlarm() ? 0 : 1);
					
				}
				
				instanceTree.add(instanceNode);
				
			}
			
		}
		
		//???????????????
		try {
			List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(node.getInstanceId());
			
			if(customMetrics != null && customMetrics.size() > 0){
				out : for(CustomMetric metric : customMetrics){
					if(metric == null || metric.getCustomMetricInfo() == null || metric.getCustomMetricInfo().getId() == null){
						continue;
					}
//					if(metric.getCustomMetricInfo().getStyle().equals(MetricTypeEnum.InformationMetric)){
//						continue;
//					}
					for(BizNodeMetricRelBo nodeRel : relList){
						
						if(nodeRel.getChildInstanceId() <= 0){
							
							if(metric.getCustomMetricInfo().getId().equals(nodeRel.getMetricId())){
								continue out;
							}
							
						}
						
					}
					
					if(searchContent != null && !searchContent.equals("")){
						if(!(metric.getCustomMetricInfo().getName().toUpperCase().contains(searchContent.toUpperCase()))){
							continue;
						}
					}
					
					BizInstanceNodeTree instanceNode = new BizInstanceNodeTree();
					instanceNode.setId(metric.getCustomMetricInfo().getId());
					instanceNode.setPid(null);
					instanceNode.setName(metric.getCustomMetricInfo().getName());
						
					instanceNode.setStatus(metric.getCustomMetricInfo().isAlert() ? 0 : 1);
					
					instanceTree.add(instanceNode);
				}
			}
			
		} catch (CustomMetricException e1) {
			logger.error(e1.getMessage(),e1);
		}
		
		List<ResourceInstance> childInstances = instance.getChildren();
		
		if(childInstances != null && childInstances.size() > 0){
			
			for(ResourceInstance childInstance : childInstances){
				
				if(childResource != null && !childResource.equals("")){
					if(!(childInstance.getResourceId().equals(childResource))){
						continue;
					}
				}
				
				String instanceName = childInstance.getShowName() == null ? childInstance.getName() : childInstance.getShowName();
				
				if(searchContent != null && !searchContent.equals("")){
					if(!(instanceName.toUpperCase().contains(searchContent.toUpperCase()))){
						continue;
					}
				}
				
				ResourceDef childResourceDef = capacityService.getResourceDefById(childInstance.getResourceId());
				
				BizInstanceNodeTree instanceNode = new BizInstanceNodeTree();
				instanceNode.setId(childInstance.getId() + "");
				instanceNode.setPid(null);
				instanceNode.setName(instanceName);
				
				List<BizInstanceNodeTree> metricTree = new ArrayList<BizInstanceNodeTree>();
				
				ResourceMetricDef[] childMetrics = childResourceDef.getMetricDefs();
				
				if(childMetrics != null && childMetrics.length > 0){
					
					out : for(ResourceMetricDef childMetric : childMetrics){
						
//						if(childMetric.getMetricType().equals(MetricTypeEnum.InformationMetric)){
//							continue;
//						}
						
						for(BizNodeMetricRelBo nodeRel : relList){
							
							if(nodeRel.getChildInstanceId() > 0){
								
								if(childMetric.getId().equals(nodeRel.getMetricId()) && nodeRel.getChildInstanceId() == childInstance.getId()){
									continue out;
								}
								
							}
							
						}
						
						if(searchContent != null && !searchContent.equals("")){
							if(!(childMetric.getName().toUpperCase().contains(searchContent.toUpperCase()))){
								continue;
							}
						}
						
						BizInstanceNodeTree childInstanceNode = new BizInstanceNodeTree();
						childInstanceNode.setId(childMetric.getId() + "_" + childInstance.getId());
						childInstanceNode.setPid(childInstance.getId() + "");
						childInstanceNode.setName(childMetric.getName());
						ProfileMetric profileMetric = null;
						try {
							profileMetric = profileService.getMetricByInstanceIdAndMetricId(childInstance.getId(), childMetric.getId());
						} catch (ProfilelibException e) {
							logger.error(e.getMessage(),e);
						}
						if(profileMetric == null){
							
							childInstanceNode.setStatus(1);
						}else{
							childInstanceNode.setStatus(profileMetric.isAlarm() ? 0 : 1);
						}
						
						metricTree.add(childInstanceNode);
						
					}
					
				}
				
				instanceNode.setChildren(metricTree);
				
				instanceTree.add(instanceNode);
				
			}
			
		}
		
		return instanceTree;
	}

	@Override
	public boolean updateInstanceNode(BizInstanceNodeBo instanceNode) {
		
		try {
			
			BizCanvasNodeBo node = new BizCanvasNodeBo();
			node.setId(instanceNode.getId());
			node.setFileId(instanceNode.getFileId());
			node.setShowName(instanceNode.getShowName());
			node.setNameHidden(instanceNode.getNameHidden());
			
			if(bizCanvasDao.updateCanvasNodeBaseInfo(node) == -1){
				logger.error("Update node error,node name exsit : " + instanceNode.getShowName());
				return false;
			}
			
			List<BizNodeMetricRelBo> oldRelationList = bizCanvasDao.getInstanceBindRelation(instanceNode.getId());
			List<BizNodeMetricRelBo> oldFinalRelationList = new ArrayList<BizNodeMetricRelBo>(oldRelationList);
			int oldType = 1;
			if(oldRelationList == null || oldRelationList.size() <= 0){
				oldType = 1;
			}else{
				String metricId = oldRelationList.get(0).getMetricId();
				if(metricId != null && !metricId.equals("")){
					oldType = 3;
				}else{
					oldType = 2;
				}
			}
			
			List<BizNodeMetricRelBo> relationList = instanceNode.getBind();
			
			//??????????????????????????????
			boolean isUpdate = false;
			if(oldType != instanceNode.getType()){
				//?????????
				isUpdate = true;
			}else{
				if(oldType != 1 && instanceNode.getType() != 1){
					if(relationList.size() != oldRelationList.size()){
						//?????????
						isUpdate = true;
					}else{
						//?????????
						oldRelationList.retainAll(relationList);
						if(oldRelationList == null || oldFinalRelationList.size() != oldRelationList.size()){
							//?????????
							isUpdate = true;
						}
					}
				}
			}
			
			if(isUpdate){
				
				//??????????????????
				//??????????????????????????????
				boolean isAllCancelMonitor = true;
				
				int highestStatus = BizStatusDefine.NONE_STATUS;
				if(instanceNode.getType() == 1){
					ResourceInstance childInstance = resourceInstanceService.getResourceInstance(instanceNode.getInstanceId());
					if(childInstance != null && childInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						isAllCancelMonitor = false;
						highestStatus = BizStatusDefine.NORMAL_STATUS;
					}
					InstanceStateData state = instanceStateService.getStateAdapter(instanceNode.getInstanceId());
					if(state != null){
						if(getInstanceState(state.getState()) > highestStatus){
							highestStatus = getInstanceState(state.getState());
						}
					}
				}else if(instanceNode.getType() == 2){
					//???????????????
					for(BizNodeMetricRelBo relation : relationList){
						ResourceInstance childInstance = null;
						try {
							childInstance = resourceInstanceService.getResourceInstance(relation.getChildInstanceId());
						} catch (InstancelibException e) {
							logger.error(e.getMessage(),e);
						}
						if(isAllCancelMonitor){
							System.out.println("Check childInstance is all cancel monitor");
							if(childInstance!=null){
								System.out.println("Child instance state : " + childInstance.getLifeState());
							}
							if(childInstance != null && childInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
								isAllCancelMonitor = false;
							}
						}
						if(childInstance == null || childInstance.getLifeState() != InstanceLifeStateEnum.MONITORED){
							continue;
						}
						InstanceStateData state = instanceStateService.getStateAdapter(relation.getChildInstanceId());
						int curState = -1;
						if(state == null){
							curState = BizStatusDefine.NORMAL_STATUS;
						}else{
							curState = getInstanceState(state.getState());
						}

						if(curState > highestStatus){
							highestStatus = curState;
							if(highestStatus == BizStatusDefine.DEATH_STATUS){
								break;
							}
						}
					}
				}else if(instanceNode.getType() == 3){
					//????????????
					for(BizNodeMetricRelBo relation : relationList){
						if(relation.getChildInstanceId() <= 0){
							//??????????????????
							if(isAllCancelMonitor){
								CustomMetric customMetric = null;
								try {
									ProfileMetric profileMetric = profileService.getMetricByInstanceIdAndMetricId(instanceNode.getInstanceId(), relation.getMetricId());
									if(profileMetric == null){
										//??????????????????????????????
										customMetric = customMetricService.getCustomMetric(relation.getMetricId());
										if(customMetric != null && customMetric.getCustomMetricInfo() != null && customMetric.getCustomMetricInfo().isMonitor()){
											isAllCancelMonitor = false;
										}
									}else if(profileMetric != null && profileMetric.isMonitor()){
										isAllCancelMonitor = false;
									}
									
									if(profileMetric == null){
										if(customMetric == null || customMetric.getCustomMetricInfo() == null || !customMetric.getCustomMetricInfo().isMonitor()){
											continue;
										}
									}else if(!profileMetric.isMonitor()){
										continue;
									}
									
								} catch (ProfilelibException e) {
									logger.error(e.getMessage(),e);
								}
							}
							MetricStateData stateData = metricStateService.getMetricState(instanceNode.getInstanceId(), relation.getMetricId());
							int curState = BizStatusDefine.NORMAL_STATUS;
							if(stateData != null){
								curState = getInstanceState(stateData.getState());
							}
							if(curState > highestStatus){
								highestStatus = curState;
								if(highestStatus == BizStatusDefine.DEATH_STATUS){
									break;
								}
							}
						}else{
							//??????????????????
							if(isAllCancelMonitor){
								try {
									ProfileMetric profileMetric = profileService.getMetricByInstanceIdAndMetricId(relation.getChildInstanceId(), relation.getMetricId());
									if(profileMetric != null && profileMetric.isMonitor()){
										isAllCancelMonitor = false;
									}
									if(profileMetric == null || !profileMetric.isMonitor()){
										continue;
									}
								} catch (ProfilelibException e) {
									logger.error(e.getMessage(),e);
								}
							}
							MetricStateData stateData = metricStateService.getMetricState(relation.getChildInstanceId(), relation.getMetricId());
							if(stateData != null){
								if(getInstanceState(stateData.getState()) > highestStatus){
									highestStatus = getInstanceState(stateData.getState());
									if(highestStatus == BizStatusDefine.DEATH_STATUS){
										break;
									}
								}
							}
						}
					}
				}
				
				if(isAllCancelMonitor){
					highestStatus = BizStatusDefine.NONE_STATUS;
				}
				
				node.setNodeStatus(highestStatus);
				node.setStatusTime(new Date());
				bizCanvasDao.updateCanvasNodeStatusById(node);
				
				bizCanvasDao.deleteCanvasNodeBindRelation(instanceNode.getId());
				if(instanceNode.getType() == 2 || instanceNode.getType() == 3){
					for(BizNodeMetricRelBo relation : relationList){
						relation.setNodeId(instanceNode.getId());
						bizCanvasDao.insertCanvasNodeBindRelation(relation);
					}
				}
				
				//?????????????????????
				bizMainApi.calculateBizHealth(instanceNode.getBizId(), 3, false, null,instanceNode.getInstanceId());
				
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		
		return true;
	}
	
	private int getInstanceState(InstanceStateEnum state){
		if(state == InstanceStateEnum.CRITICAL){
			return BizStatusDefine.DEATH_STATUS;
		}else if(state == InstanceStateEnum.SERIOUS){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(state == InstanceStateEnum.WARN){
			return BizStatusDefine.WARN_STATUS;
		}else if(state == InstanceStateEnum.NORMAL){
			return BizStatusDefine.NORMAL_STATUS;
		}
		return BizStatusDefine.NORMAL_STATUS;
	}
	
	private int getInstanceState(MetricStateEnum state){
		if(state == MetricStateEnum.CRITICAL){
			return BizStatusDefine.DEATH_STATUS;
		}else if(state == MetricStateEnum.SERIOUS){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(state == MetricStateEnum.WARN){
			return BizStatusDefine.WARN_STATUS;
		}else if(state == MetricStateEnum.NORMAL){
			return BizStatusDefine.NORMAL_STATUS;
		}
		return BizStatusDefine.NORMAL_STATUS;
	}

	@Override
	public Set<Long> getResponeTimeMetricInstanceList(long bizId) {
		
		Set<Long> ids = new HashSet<Long>();
		
		List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(bizId);
		
		if(nodes == null || nodes.size() <= 0){
			return ids;
		}
		
		for(BizCanvasNodeBo node : nodes){
			if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				try {
					ResourceInstance instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
					if(instance == null){
						continue;
					}
					if(!(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
						continue;
					}
					if(instance.getCategoryId().equals(CapacityConst.URL)){
						ids.add(instance.getId());
					}
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		return ids;
	}

	@Override
	public Set<Long> getCalculateMetricInstanceList(long bizId) {
		
		Set<Long> ids = new HashSet<Long>();
		
		List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(bizId);
		
		if(nodes == null || nodes.size() <= 0){
			return ids;
		}
		
		for(BizCanvasNodeBo node : nodes){
			if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				try {
					ResourceInstance instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
					if(instance == null){
						continue;
					}
					if(!(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
						continue;
					}
					String categoryId = instance.getCategoryId();
					CategoryDef categoryDef = capacityService.getCategoryById(categoryId);
					if(categoryDef.getParentCategory().getId().equals(CapacityConst.HOST) || categoryId.equals("VirtualVM") || 
							categoryId.equals("VirtualHost") || categoryId.equals("XenHosts") || categoryId.equals("FusionComputeHosts")){
						ids.add(instance.getId());
					}
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		return ids;
	}
	
	@Override
	public Set<Long> getStoreMetricInstanceList(long bizId) {
		
		Set<Long> ids = new HashSet<Long>();
		
		List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(bizId);
		
		if(nodes == null || nodes.size() <= 0){
			return ids;
		}
		
		for(BizCanvasNodeBo node : nodes){
			if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				try {
					ResourceInstance instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
					if(instance == null){
						continue;
					}
					if(!(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
						continue;
					}
					
					if(instance.getCategoryId().equals("VirtualStorage") || instance.getCategoryId().equals("XenSRs") || instance.getCategoryId().equals("FusionComputeDataStores")){
						ids.add(instance.getId());
						continue;
					}
					
					List<ResourceInstance> childInstances = instance.getChildren();
					if(childInstances != null && childInstances.size() > 0){
						for(ResourceInstance childInstance : childInstances){
							if(!(childInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
								continue;
							}
							if(childInstance.getChildType().equals("Partition")){
								ids.add(childInstance.getId());
							}
						}
					}
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		return ids;
	}

	@Override
	public Set<Long> getDatabaseMetricInstanceList(long bizId) {
		Set<Long> ids = new HashSet<Long>();
		
		List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(bizId);
		
		if(nodes == null || nodes.size() <= 0){
			return ids;
		}
		
		for(BizCanvasNodeBo node : nodes){
			if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				try {
					ResourceInstance instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
					if(instance == null){
						continue;
					}
					if(!(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
						continue;
					}
					List<ResourceInstance> childInstances = instance.getChildren();
					if(childInstances != null && childInstances.size() > 0){
						for(ResourceInstance childInstance : childInstances){
							if(!(childInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
								continue;
							}
							if(childInstance.getChildType().equals("TableSpace")){
								ids.add(childInstance.getId());
							}
						}
					}
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		return ids;
	}
	
	@Override
	public Set<Long> getBandwidthMetricInstanceList(long bizId) {
		Set<Long> ids = new HashSet<Long>();
		
		List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(bizId);
		
		if(nodes == null || nodes.size() <= 0){
			return ids;
		}
		
		for(BizCanvasNodeBo node : nodes){
			if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				try {
					ResourceInstance instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
					if(instance == null){
						continue;
					}
					if(!(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
						continue;
					}
					String categoryId = instance.getCategoryId();
					CategoryDef categoryDef = capacityService.getCategoryById(categoryId);
					if(categoryDef.getParentCategory().getId().equals(CapacityConst.NETWORK_DEVICE)){
						List<ResourceInstance> childInstances = instance.getChildren();
						if(childInstances != null && childInstances.size() > 0){
							for(ResourceInstance childInstance : childInstances){
								if(!(childInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
									continue;
								}
								if(childInstance.getChildType().equals("NetInterface")){
									ids.add(childInstance.getId());
								}
							}
						}
					}
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		return ids;
	}

	@Override
	public List<Map<String, Object>> getChildResourceList(long instanceId) {
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		ResourceInstance instance = null;
		try {
			instance = resourceInstanceService.getResourceInstance(instanceId);
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}
		
		if(instance == null){
			logger.error("Get resource intance error , id : " + instanceId);
			return null;
		}
		
		ResourceDef resourceDef = capacityService.getResourceDefById(instance.getResourceId());
		
		if(resourceDef.getChildResourceDefs() == null || resourceDef.getChildResourceDefs().length <= 0){
			return result;
		}
		
		for(int i = 0 ; i < resourceDef.getChildResourceDefs().length ; i ++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", resourceDef.getChildResourceDefs()[i].getId());
			map.put("name",  resourceDef.getChildResourceDefs()[i].getName());
			result.add(map);
		}
		
		return result;
	}

	@Override
	public List<BizCanvasNodeBo> getNodeListByBiz(long bizId) {
		
		List<BizCanvasNodeBo> result = new ArrayList<BizCanvasNodeBo>();
		List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(bizId);
		
		if(nodes == null || nodes.size() <= 0){
			return result;
		}
		
		for(BizCanvasNodeBo node : nodes){
			BizCanvasNodeBo newNode = new BizCanvasNodeBo();
			if(node.getBizId() == node.getInstanceId()){
				continue;
			}
			
			if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE || node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				
				newNode.setId(node.getId());
				newNode.setShowName(node.getShowName());
				newNode.setInstanceId(node.getInstanceId());
				newNode.setNodeType(node.getNodeType());
				if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
					List<BizNodeMetricRelBo> relList = bizCanvasDao.getInstanceBindRelation(node.getId());
					if(relList == null || relList.size() <= 0){
						newNode.setNodeStatus(1);
					}else{
						String metricId = relList.get(0).getMetricId();
						if(metricId != null && !metricId.equals("")){
							newNode.setNodeStatus(3);
						}else{
							newNode.setNodeStatus(2);
						}
					}
				}
				
				result.add(newNode);
			}
		}
		
		return result;
	}
	
	@Override
	public BizCanvasNodeBo getSingleInstanceNodeType(long nodeId) {
		
		BizCanvasNodeBo node = bizCanvasDao.getCanvasNode(nodeId);
		
		if(node == null){
			return new BizCanvasNodeBo();
		}
		
		BizCanvasNodeBo newNode = new BizCanvasNodeBo();
		
		newNode.setId(node.getId());
		newNode.setBizId(node.getBizId());
		newNode.setShowName(node.getShowName());
		newNode.setInstanceId(node.getInstanceId());
		newNode.setNodeType(node.getNodeType());
		if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
			List<BizNodeMetricRelBo> relList = bizCanvasDao.getInstanceBindRelation(node.getId());
			if(relList == null || relList.size() <= 0){
				newNode.setNodeStatus(1);
			}else{
				String metricId = relList.get(0).getMetricId();
				if(metricId != null && !metricId.equals("")){
					newNode.setNodeStatus(3);
				}else{
					newNode.setNodeStatus(2);
				}
			}
		}
		
		return newNode;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void interceptor(InstancelibEvent event) throws Exception {
		// TODO Auto-generated method stub
		if(event.getEventType().equals(EventEnum.INSTANCE_CHILDREN_DELETE_EVENT)
				|| event.getEventType().equals(EventEnum.INSTANCE_DELETE_EVENT)){
			
		List<Long> deleteParentInstanceList = new ArrayList<>();
		List<Long> deleteChildInstanceList = new ArrayList<>();
		
		if (event.getEventType().equals(EventEnum.INSTANCE_DELETE_EVENT)) {
			List<Long> deleteIds = (List<Long>)event.getSource();		
			
			for(long id : deleteIds){
				ResourceInstance instance = resourceInstanceService.getResourceInstance(id);
				
				if(instance.getParentId() > 0){
					//?????????
					deleteChildInstanceList.add(id);
				}else{
					//?????????
					deleteParentInstanceList.add(id);
				}
			}
		}else {
			List<ResourceInstance> deleteIns = (List<ResourceInstance>)event.getSource();
			
			for (ResourceInstance ri : deleteIns) {
				if(ri.getParentId() > 0){
					//?????????
					deleteChildInstanceList.add(ri.getId());
				}else{
					//?????????
					deleteParentInstanceList.add(ri.getId());
				}
			}
		}
			
		
		if(deleteChildInstanceList.size() > 0){
			bizMainApi.deleteChildInstanceChangeBizStatus(deleteChildInstanceList);
		}
		
		if(deleteParentInstanceList.size() > 0){
			bizMainApi.calculateBizHealthForDeleteMainInstances(deleteParentInstanceList);
		}
		
		
	}
	}
	
	/**
	 * ????????????ID??????????????????????????????
	 * @param nodeId
	 * @return
	 */
	@Override
	public List<BizNodeMetricRelBo> getChildInstanceByNodeId(long nodeId) {
		return bizCanvasDao.getChildInstanceBindRelation(nodeId);
	}

	/**
	 * ????????????ID???????????????????????????
	 * @param nodeId
	 * @return
	 */
	@Override
	public List<BizNodeMetricRelBo> getMetricByNodeId(long nodeId) {
		return bizCanvasDao.getChildInstanceMetricBindRelation(nodeId);
	}


}
