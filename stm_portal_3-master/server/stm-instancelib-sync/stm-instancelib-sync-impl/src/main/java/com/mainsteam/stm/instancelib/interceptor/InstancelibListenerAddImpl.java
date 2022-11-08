package com.mainsteam.stm.instancelib.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
//import com.mainsteam.stm.instancelib.RelationshipOperateService;
import com.mainsteam.stm.instancelib.obj.InstanceDependence;
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
public class InstancelibListenerAddImpl implements InstancelibListener {

	private static final Log logger = LogFactory
			.getLog(InstancelibListenerAddImpl.class);
	
	
	//private RelationshipOperateService relationshipOperateService;
	
	private CapacityService capacityService;
		
	
	@Override
	public void listen(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_ADD_EVENT){
			ResourceInstance resourceInstance = (ResourceInstance)instancelibEvent.getSource();
			if(logger.isTraceEnabled()){
				logger.trace("add resource instance listen start instanceId=" + resourceInstance.getId() + " instanceName="+resourceInstance.getName());
			}
			String resourceId = resourceInstance.getResourceId();
			/*
			 * 设备添加时候，需要添加父子关系到关系库中。
			 */
			if(!LinkResourceConsts.RESOURCE_LAYER2LINK_ID.equals(resourceId)){
				addDeviceRelation(resourceInstance);
			}
			if(logger.isTraceEnabled()){
				logger.trace("add resource instance listen end instanceId=" + resourceInstance.getId() + " instanceName="+resourceInstance.getName());
			}
		}
	}
	
	private void addDeviceRelation(ResourceInstance resourceIntance){
		if(resourceIntance.getParentInstance() != null){
			return;
		}
		CategoryDef categoryDef = capacityService.getCategoryById(resourceIntance.getCategoryId());
		String categoryId = null;
		if(categoryDef != null){
			categoryId = categoryDef.getParentCategory().getId();
			if(logger.isDebugEnabled()){
				logger.error("resourceId="+resourceIntance.getId() + " categoryId=" + resourceIntance.getCategoryId() + " parentCategoryId=" + categoryId);
			}
		}
		List<ResourceInstance> children = resourceIntance.getChildren();
		if(children != null && !children.isEmpty()){
			List<InstanceDependence> instanceDependences = new ArrayList<InstanceDependence>();
			for (ResourceInstance childResourceInstance : children) {
				InstanceDependence dependence = new InstanceDependence();
				dependence.setRelationType(RelationshipTypeEnum.CONTAIN);
				dependence.setSourceResource(resourceIntance.getId());
				dependence.setSourceResourceType(categoryId);
				dependence.setUse(true);
				dependence.setTargetResource(childResourceInstance.getId());
				dependence.setTargetResourceType(ResourceTypeEnum.SUB_RESOURCE.toString());
				dependence.setDataSource(LinkDataRelationSourceEnum.TOPO);
				instanceDependences.add(dependence);
			}
			//将资源实例与子资源实例包含关系入库
			/*if(!instanceDependences.isEmpty()){
				relationshipOperateService.addRelationships(instanceDependences);
			}*/
		}
	}

	/*public void setRelationshipOperateService(
			RelationshipOperateService relationshipOperateService) {
		this.relationshipOperateService = relationshipOperateService;
	}*/

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
}
