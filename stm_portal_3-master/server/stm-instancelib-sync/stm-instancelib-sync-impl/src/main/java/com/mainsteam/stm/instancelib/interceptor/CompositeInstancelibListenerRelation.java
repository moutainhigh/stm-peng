package com.mainsteam.stm.instancelib.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
//import com.mainsteam.stm.instancelib.RelationshipOperateService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.InstanceDependence;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.instancelib.ojbenum.LinkDataRelationSourceEnum;
import com.mainsteam.stm.instancelib.ojbenum.RelationshipTypeEnum;
import com.mainsteam.stm.instancelib.ojbenum.ResourceTypeEnum;

/**
 * 用于添加关系
 * @author xiaoruqiang
 *
 */
public class CompositeInstancelibListenerRelation implements InstancelibListener {

	private static final Log logger = LogFactory
			.getLog(CompositeInstancelibListenerRelation.class);
	
	//private RelationshipOperateService relationshipOperateService;
	
	private CapacityService capacityService;
	
	private ResourceInstanceService resourceInstanceService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void listen(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_RELATION_ADD_EVENT){
			List<Relation> listRelations = (List<Relation>)instancelibEvent.getSource();
			if(logger.isTraceEnabled()){
				logger.trace("add composite resource instance relation listen start");
			}
			add(listRelations);
			if(logger.isTraceEnabled()){
				logger.trace("add composite resource instance relation listen end");
			}
		}
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_RELATION_UPDATE_EVENT){
			List<Relation> listRelations = (List<Relation>)instancelibEvent.getSource();
			if(logger.isTraceEnabled()){
				logger.trace("update composite resource instance relation listen start");
			}
			long emptyRelation = -1;
			if(listRelations.isEmpty()){
				try {
					emptyRelation = Long.parseLong(instancelibEvent.getCurrent().toString());
				} catch (Exception e) {
					emptyRelation = -1;
				}
			}
			if(emptyRelation > 0){
				update(listRelations,emptyRelation);
			}
			if(logger.isTraceEnabled()){
				logger.trace("update composite resource instance relation listen end");
			}
		}
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_RELATION_DELETE_EVENT){
			long compositeId = -1;
			try {
				compositeId = Long.parseLong(instancelibEvent.getSource().toString());
			} catch (Exception e) {
				compositeId = -1;
			}
			if(logger.isTraceEnabled()){
				logger.trace("delete composite resource instance relation listen start");
			}
			/*if(compositeId > 0){
				relationshipOperateService.removeBusinessRelationByCompositeId(compositeId);
			}*/
			if(logger.isTraceEnabled()){
				logger.trace("delete composite resource instance relation listen end");
			}
		}
	}
	
	private void add(List<Relation> listRelations){
		List<InstanceDependence> instanceDependences = new ArrayList<InstanceDependence>(500);
		for (Relation relation : listRelations) {
			if(relation instanceof PathRelation){
				PathRelation pathRelation = (PathRelation) relation;
				InstanceDependence instanceDependence = new InstanceDependence();
				instanceDependence.setSourceResource(pathRelation.getFromInstanceId());
				if(InstanceTypeEnum.RESOURCE == pathRelation.getToInstanceType() 
						&& InstanceTypeEnum.BUSINESS_APPLICATION == pathRelation.getFromInstanceType()){
					//业务系统-资源关系连线，需要反向计算
					instanceDependence.setTargetResourceType(ResourceTypeEnum.BUSINESS.toString());
					instanceDependence.setTargetResource(pathRelation.getFromInstanceId());
					
					String categoryId = getResourceTypeByInstanceId(pathRelation.getToInstanceId());
					instanceDependence.setSourceResource(pathRelation.getToInstanceId());
					instanceDependence.setSourceResourceType(categoryId);
				}else{
					//业务这边按照连线关系计算
					instanceDependence.setTargetResourceType(ResourceTypeEnum.BUSINESS.toString());
					instanceDependence.setTargetResource(pathRelation.getToInstanceId());
		
					instanceDependence.setSourceResource(pathRelation.getFromInstanceId());
					instanceDependence.setSourceResourceType(ResourceTypeEnum.BUSINESS.toString());
				}
				instanceDependence.setCompositeId(pathRelation.getInstanceId());
				instanceDependence.setDataSource(LinkDataRelationSourceEnum.BUSINESS);
				instanceDependence.setRelationType(RelationshipTypeEnum.DEPENDENCE);
				instanceDependence.setUse(true);
				instanceDependences.add(instanceDependence);
			}
		}
		/*if(!instanceDependences.isEmpty()){
			relationshipOperateService.addRelationships(instanceDependences);
		}*/
	}
	
	private void update(List<Relation> listRelations,long emptyInstanceId){
		long compositeId = -1;
		if(listRelations.isEmpty()){
			compositeId = emptyInstanceId;
			//relationshipOperateService.removeBusinessRelationByCompositeId(compositeId);
		}else{
			//删除该复合实例对应的所有关系，每次操作都是最新的。
			compositeId = ((PathRelation)listRelations.get(0)).getInstanceId();
			//relationshipOperateService.removeBusinessRelationByCompositeId(compositeId);
			List<InstanceDependence> instanceDependences = new ArrayList<InstanceDependence>(500);
			for (Relation relation : listRelations) {
				if(relation instanceof PathRelation){
					PathRelation pathRelation = (PathRelation) relation;
					InstanceDependence instanceDependence = new InstanceDependence();
					instanceDependence.setSourceResource(pathRelation.getFromInstanceId());
					
					if(InstanceTypeEnum.RESOURCE == pathRelation.getToInstanceType() 
							&& InstanceTypeEnum.BUSINESS_APPLICATION == pathRelation.getFromInstanceType()){
						//业务系统-资源关系连线，需要反向计算
						instanceDependence.setTargetResourceType(ResourceTypeEnum.BUSINESS.toString());
						instanceDependence.setTargetResource(pathRelation.getFromInstanceId());
						
						String categoryId = getResourceTypeByInstanceId(pathRelation.getToInstanceId());
						instanceDependence.setSourceResource(pathRelation.getToInstanceId());
						instanceDependence.setSourceResourceType(categoryId);
					}else{
						//业务这边按照连线关系计算
						instanceDependence.setTargetResourceType(ResourceTypeEnum.BUSINESS.toString());
						instanceDependence.setTargetResource(pathRelation.getToInstanceId());
			
						instanceDependence.setSourceResource(pathRelation.getFromInstanceId());
						instanceDependence.setSourceResourceType(ResourceTypeEnum.BUSINESS.toString());
					}
					instanceDependence.setCompositeId(pathRelation.getInstanceId());
					instanceDependence.setDataSource(LinkDataRelationSourceEnum.BUSINESS);
					instanceDependence.setRelationType(RelationshipTypeEnum.DEPENDENCE);
					instanceDependences.add(instanceDependence);
				}
			}
			/*if(!instanceDependences.isEmpty()){
				relationshipOperateService.addRelationships(instanceDependences);
			}*/
		}
	}
	
	private String getResourceTypeByInstanceId(long instanceId){
		//资源实例对象，需要获取设备分类
		String categoryId = null;
		try {
			ResourceInstance srcResourceInstance = resourceInstanceService.getResourceInstance(instanceId);
			CategoryDef categoryDef = capacityService.getCategoryById(srcResourceInstance.getCategoryId());
			if(categoryDef != null){
				categoryId = categoryDef.getParentCategory().getId();
			}
		} catch (InstancelibException e) {
			if(logger.isErrorEnabled()){
				logger.error("getResourceTypeByInstanceId error!",e);
			}
		}
		return categoryId;
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
}
