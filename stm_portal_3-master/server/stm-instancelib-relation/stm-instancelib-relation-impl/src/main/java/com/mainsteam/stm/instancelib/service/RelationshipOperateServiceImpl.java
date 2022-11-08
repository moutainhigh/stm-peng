//package com.mainsteam.stm.instancelib.service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.mainsteam.stm.instancelib.RelationshipOperateService;
//import com.mainsteam.stm.instancelib.dao.InstanceDependenceRelationDAO;
//import com.mainsteam.stm.instancelib.dao.pojo.InstanceDependencePO;
//import com.mainsteam.stm.instancelib.obj.InstanceDependence;
//import com.mainsteam.stm.instancelib.ojbenum.ResourceTypeEnum;
//import com.mainsteam.stm.instancelib.util.RuleUtil;
//import com.mainsteam.stm.platform.sequence.service.ISequence;
//
//public class RelationshipOperateServiceImpl implements
//		RelationshipOperateService {
//
//	private static final Log logger = LogFactory.getLog(RelationshipOperateServiceImpl.class);
//	
//	private InstanceDependenceRelationDAO instanceDependenceRelationDAO;
//	
//    private ISequence dependenceSeq;
//	
//	@Override
//	public void addRelationships(List<InstanceDependence> instanceDependences) {
//		if(logger.isTraceEnabled()){
//			logger.trace("addRelationships start");
//		}
//		List<InstanceDependencePO> instanceDependencePOs = new ArrayList<InstanceDependencePO>(instanceDependences.size());
//		for (InstanceDependence instanceDependence : instanceDependences) {
//			instanceDependence.setRelationId(dependenceSeq.next());
//			InstanceDependencePO po = convertTOPO(instanceDependence);
//			instanceDependencePOs.add(po);
//		}
//		try {
//			instanceDependenceRelationDAO.insertDependences(instanceDependencePOs);
//		} catch (Exception e) {
//			if(logger.isErrorEnabled()){
//				logger.error("addRelationships error.",e);
//			}
//		}
//		if(logger.isTraceEnabled()){
//			logger.trace("addRelationships end");
//		}
//	}
//
////	@Override
////	public void removeRelationship(long relationId) {
////		if(logger.isTraceEnabled()){
////			logger.trace("removeRelationship start relationId=" + relationId);
////		}
////		try {
////			instanceDependenceRelationDAO.removeDependence(relationId);
////		} catch (Exception e) {
////			if(logger.isErrorEnabled()){
////				logger.error("removeRelationship error.",e);
////			}
////		}
////		if(logger.isTraceEnabled()){
////			logger.trace("removeRelationship end relationId=" + relationId);
////		}
////	}
//
////	@Override
////	public void removeRelationships(List<Long> relationIds) {
////		if(logger.isTraceEnabled()){
////			logger.trace("removeRelationships start relationIds" + relationIds);
////		}
////		try {
////			instanceDependenceRelationDAO.removeDependences(relationIds);
////		} catch (Exception e) {
////			if(logger.isErrorEnabled()){
////				logger.error("removeRelationships error.",e);
////			}
////		}
////		if(logger.isTraceEnabled()){
////			logger.trace("removeRelationships end relationIds" + relationIds);
////		}
////	}
//	
//	@Override
//	public void removeBusinessRelationByCompositeId(long compositeId) {
//		if(logger.isTraceEnabled()){
//			logger.trace("removeBusinessRelationByCompositeId start compositeId=" + compositeId);
//		}
//		try {
//			instanceDependenceRelationDAO.removeBusinessRelationByCompositeId(compositeId);
//		} catch (Exception e) {
//			if(logger.isErrorEnabled()){
//				logger.error("removeRelationships error.",e);
//			}
//		}
//		if(logger.isTraceEnabled()){
//			logger.trace("removeBusinessRelationByCompositeId end compositeId=" + compositeId);
//		}
//	}
//
//	@Override
//	public void removeTopoRelationships() {
//		if(logger.isTraceEnabled()){
//			logger.trace("removeTopoRelationships start");
//		}
//		try {
//			instanceDependenceRelationDAO.removeTopoLinkDependences();
//		} catch (Exception e) {
//			if(logger.isErrorEnabled()){
//				logger.error("removeRelationships error.",e);
//			}
//		}
//		if(logger.isTraceEnabled()){
//			logger.trace("removeTopoRelationships end");
//		}
//	}
//	
//	@Override
//	public int getDeviceDependenceLevel(ResourceTypeEnum type) {
//		return RuleUtil.getRuleLevel(type.toString());
//	}
//
//	private InstanceDependencePO convertTOPO(InstanceDependence instanceDependence){
//		InstanceDependencePO po = new InstanceDependencePO();
//		po.setIsUse(instanceDependence.isUse()?"1":"0");
//		po.setRelationId(instanceDependence.getRelationId());
//		po.setRelationType(instanceDependence.getRelationType().toString());
//		po.setSourceResource(instanceDependence.getSourceResource());
//		po.setSourceChildResource(instanceDependence.getSourceChildResource());
//		po.setSourceResourceType(instanceDependence.getSourceResourceType());
//		po.setTargetResource(instanceDependence.getTargetResource());
//		po.setTargetChildResource(instanceDependence.getTargetChildResource());
//		po.setTargetResourceType(instanceDependence.getTargetResourceType());
//		po.setLinkDataSource(instanceDependence.getDataSource().toString());
//		po.setCompositeId(instanceDependence.getCompositeId());
//		return po;
//	}
//	
//	public void setInstanceDependenceRelationDAO(
//			InstanceDependenceRelationDAO instanceDependenceRelationDAO) {
//		this.instanceDependenceRelationDAO = instanceDependenceRelationDAO;
//	}
//
//	public void setDependenceSeq(ISequence dependenceSeq) {
//		this.dependenceSeq = dependenceSeq;
//	}
//
//
//	
//
//}
