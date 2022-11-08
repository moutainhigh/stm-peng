package com.mainsteam.stm.instancelib.interceptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.instancelib.CoreResourceInstanceService;
//import com.mainsteam.stm.instancelib.RelationshipOperateService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.InstanceDependence;
import com.mainsteam.stm.instancelib.obj.Link;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.ojbenum.LinkDataRelationSourceEnum;
import com.mainsteam.stm.instancelib.ojbenum.RelationshipTypeEnum;
import com.mainsteam.stm.instancelib.ojbenum.ResourceTypeEnum;

/**
 * 用于添加关系
 * @author xiaoruqiang
 *
 */
public class InstancelibListenerLinkAddImpl implements InstancelibListener {

	private static final Log logger = LogFactory
			.getLog(InstancelibListenerLinkAddImpl.class);
	
	
	//private RelationshipOperateService relationshipOperateService;
	
	private CapacityService capacityService;
	
	private ResourceInstanceService resourceInstanceService;
	
	private CoreResourceInstanceService coreResourceInstanceService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void listen(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_LINK_ADD_EVENT){
			List<ResourceInstance> resourceInstances = (List<ResourceInstance>)instancelibEvent.getSource();
			boolean isAll = (boolean)instancelibEvent.getCurrent();
			if(logger.isTraceEnabled()){
				logger.trace("add resource instance listen for link start");
			}
			/*if(isAll){
				//删除以前拓扑连线关系
				relationshipOperateService.removeTopoRelationships();
			}*/
			/*
			 * 拿到核心节点的资源实例Id,找到与核心设备的相连的网络设备，根据核心节点单独计算。
			 * 两端有一个不是网络设备的，根据规则计算。
			 * 如果连续有网络设备，需要存放端口实例Id。
			 */
			long coreId  = coreResourceInstanceService.getCoreResourceInstance();
			List<InstanceDependence> instanceDependences = new ArrayList<InstanceDependence>(1000);
			List<Link> networklink = new ArrayList<Link>(50);
			for (ResourceInstance resourceInstance : resourceInstances) {
				String[] src = resourceInstance.getModulePropBykey(LinkResourceConsts.PROP_SRC_MAININST_ID);
				String[] dest = resourceInstance.getModulePropBykey(LinkResourceConsts.PROP_DEST_MAININST_ID);
				if(src != null && src.length > 0 && dest != null && dest.length > 0){
					long srcId = Long.parseLong(src[0]);
					long destId = Long.parseLong(dest[0]);
					try {
						ResourceInstance srcResourceInstance = resourceInstanceService.getResourceInstance(srcId);
						ResourceInstance destResourceInstance = resourceInstanceService.getResourceInstance(destId);
						if(srcResourceInstance != null &&  destResourceInstance != null){
							long srcChildInstanceId = 0;
							long destChildInstanceId = 0;
							ResourceTypeEnum srcResourceType = null;
							CategoryDef srcCategoryDef = capacityService.getCategoryById(srcResourceInstance.getCategoryId());
							String srcCategoryId = null;
							if(srcCategoryDef != null){
								srcCategoryId = srcCategoryDef.getParentCategory().getId();
								srcResourceType = getResourceTypeEnumByCategoryId(srcCategoryId);
								if(ResourceTypeEnum.NetworkDevice == srcResourceType){
									String[] srcChild = resourceInstance.getModulePropBykey(LinkResourceConsts.PROP_SRC_SUBINST_ID);
									if(srcChild != null && srcChild.length > 0){
										srcChildInstanceId = Long.parseLong(srcChild[0]);
									}
								}
							}
							
							ResourceTypeEnum destResourceType = null;
							CategoryDef destCategoryDef = capacityService.getCategoryById(destResourceInstance.getCategoryId());
							String destCategoryId = null;
							if(destCategoryDef != null){
								destCategoryId = destCategoryDef.getParentCategory().getId();
								destResourceType = getResourceTypeEnumByCategoryId(destCategoryId);
								if(ResourceTypeEnum.NetworkDevice == destResourceType){
									String[] destChild = resourceInstance.getModulePropBykey(LinkResourceConsts.PROP_DEST_SUBINST_ID);
									if(destChild != null && destChild.length > 0){
										destChildInstanceId = Long.parseLong(destChild[0]);
									}
								}
							}
							
							if(destResourceType == ResourceTypeEnum.NetworkDevice && srcResourceType == ResourceTypeEnum.NetworkDevice){
								networklink.add(new Link(srcResourceInstance.getId(),srcChildInstanceId,destResourceInstance.getId(),destChildInstanceId,srcResourceType,destResourceType,srcCategoryId,destCategoryId));
							}else{
								//添加连线两端不是同时是网络设备的关系依赖
								InstanceDependence instanceDependence = addOtherLinkRelation(new Link(srcResourceInstance.getId(),srcChildInstanceId,destResourceInstance.getId(),destChildInstanceId,srcResourceType,destResourceType,srcCategoryId,destCategoryId));
								instanceDependences.add(instanceDependence);
							}
						}
					} catch (Exception e) {
						if(logger.isErrorEnabled()){
							logger.error("add link relation error!",e);
						}
					}
				}	
			}
			
			//通过核心节点计算连线两端都是网络设备的联系关系依赖，如果没有设置核心节点，入库的时候连线依赖关系不可用
			if(coreId == 0){
				for (Link link : networklink) {
					InstanceDependence	dependence = new InstanceDependence();
					dependence.setRelationType(RelationshipTypeEnum.CONNECTION);
					dependence.setUse(false);
					dependence.setSourceResource(link.getSrcInstanceId());
					dependence.setSourceChildResource(link.getSrcChildInstanceId());
					dependence.setSourceResourceType(link.getSrcCategoryId());
					dependence.setTargetResource(link.getDestInstanceId());
					dependence.setTargetChildResource(link.getDestChildInstanceId());
					dependence.setTargetResourceType(link.getDestCategoryId());
					dependence.setDataSource(LinkDataRelationSourceEnum.TOPO);
					instanceDependences.add(dependence);
				}
			}else{
				calcNetworkRelation(coreId,networklink,instanceDependences);
				if(!networklink.isEmpty()){
					//网络设备连线是单独的，没有与核心设备关联，连线还是需要存放到数据库中
					for (Link link : networklink) {
						InstanceDependence	dependence = new InstanceDependence();
						dependence.setRelationType(RelationshipTypeEnum.CONNECTION);
						dependence.setUse(false);
						dependence.setSourceResource(link.getSrcInstanceId());
						dependence.setSourceChildResource(link.getSrcChildInstanceId());
						dependence.setSourceResourceType(link.getSrcCategoryId());
						dependence.setTargetResource(link.getDestInstanceId());
						dependence.setTargetChildResource(link.getDestChildInstanceId());
						dependence.setTargetResourceType(link.getDestCategoryId());
						dependence.setDataSource(LinkDataRelationSourceEnum.TOPO);
						instanceDependences.add(dependence);
					}
				}
			}
			/*if(!instanceDependences.isEmpty()){
				relationshipOperateService.addRelationships(instanceDependences);
			}*/
			if(logger.isTraceEnabled()){
				logger.trace("add resource instance listen for link end");
			}
		}
	}
	
	private ResourceTypeEnum getResourceTypeEnumByCategoryId(String categoryId){
		ResourceTypeEnum resourceTypeEnum = null;
		switch(categoryId){
			case CapacityConst.NETWORK_DEVICE:
			case CapacityConst.HOST:
				resourceTypeEnum = ResourceTypeEnum.valueOf(categoryId);
				break;
			default:
				resourceTypeEnum = ResourceTypeEnum.OTHER;
				break;
		}
		return resourceTypeEnum;
	}
	 
	private void calcNetworkRelation(long srcId,List<Link> networklink,List<InstanceDependence> instanceDependences){
		List<Long> nextIds = new ArrayList<Long>();
		for (Iterator<Link> iterator = networklink.iterator(); iterator.hasNext();) {
			Link link = iterator.next();
			InstanceDependence instanceDependence = new InstanceDependence();
			boolean isDelete = false;
			if(link.getSrcInstanceId() == srcId){
				instanceDependence.setSourceResource(link.getSrcInstanceId());
				instanceDependence.setSourceResourceType(link.getSrcCategoryId());
				instanceDependence.setTargetResource(link.getDestInstanceId());
				instanceDependence.setTargetResourceType(link.getDestCategoryId());
				nextIds.add(link.getDestInstanceId());
				isDelete = true;
			}else if(link.getDestInstanceId() == srcId){
				instanceDependence.setSourceResource(link.getDestInstanceId());
				instanceDependence.setSourceResourceType(link.getDestCategoryId());
				instanceDependence.setTargetResource(link.getSrcInstanceId());
				instanceDependence.setTargetResourceType(link.getSrcCategoryId());
				nextIds.add(link.getSrcInstanceId());
				isDelete = true; 
			}
			if(isDelete){
				instanceDependence.setDataSource(LinkDataRelationSourceEnum.TOPO);
				instanceDependence.setRelationType(RelationshipTypeEnum.CONNECTION);
				instanceDependence.setUse(true);
				instanceDependences.add(instanceDependence);
				iterator.remove();
			}
		}
		for (Long id : nextIds) {
			calcNetworkRelation(id,networklink,instanceDependences);
		}
	}
	
	//添加不是同时是网络设备的是连线
	private InstanceDependence addOtherLinkRelation(Link link){
		InstanceDependence dependence = null;
		//int srcLevel = relationshipOperateService.getDeviceDependenceLevel(link.getSrcResourceTypeEnum());
		//int destLevel  = relationshipOperateService.getDeviceDependenceLevel(link.getDestResourceTypeEnum());
		dependence = new InstanceDependence();
		dependence.setRelationType(RelationshipTypeEnum.CONNECTION);
		dependence.setUse(true);
	/*	if(srcLevel > destLevel){
			dependence.setSourceResource(link.getSrcInstanceId());
			dependence.setSourceResourceType(link.getSrcCategoryId());
			dependence.setTargetResource(link.getDestInstanceId());
			dependence.setTargetResourceType(link.getDestCategoryId());
			if(ResourceTypeEnum.NetworkDevice == link.getSrcResourceTypeEnum()){
				dependence.setSourceChildResource(link.getSrcChildInstanceId());
			}
		}else{
			dependence.setSourceResource(link.getDestInstanceId());
			dependence.setSourceResourceType(link.getDestCategoryId());
			dependence.setTargetResource(link.getSrcInstanceId());
			dependence.setTargetResourceType(link.getSrcCategoryId());
			if(ResourceTypeEnum.NetworkDevice == link.getDestResourceTypeEnum()){
				dependence.setTargetChildResource(link.getDestChildInstanceId());
			}
		}*/
		dependence.setDataSource(LinkDataRelationSourceEnum.TOPO);
		return dependence;
	}
	

	/*public void setRelationshipOperateService(
			RelationshipOperateService relationshipOperateService) {
		this.relationshipOperateService = relationshipOperateService;
	}*/

	public CapacityService getCapacityService() {
		return capacityService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public ResourceInstanceService getResourceInstanceService() {
		return resourceInstanceService;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setCoreResourceInstanceService(
			CoreResourceInstanceService coreResourceInstanceService) {
		this.coreResourceInstanceService = coreResourceInstanceService;
	}
}
