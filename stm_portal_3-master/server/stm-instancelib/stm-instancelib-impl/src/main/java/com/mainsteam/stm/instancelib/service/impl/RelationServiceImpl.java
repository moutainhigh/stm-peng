package com.mainsteam.stm.instancelib.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.RelationService;
import com.mainsteam.stm.instancelib.dao.RelationDAO;
import com.mainsteam.stm.instancelib.dao.pojo.RelationPO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.instancelib.service.RelationExtendService;

public class RelationServiceImpl implements RelationService,RelationExtendService {

	private static final Log logger = LogFactory.getLog(RelationServiceImpl.class);
	
	private RelationDAO relationDAO;
	
	//拦截器
	private InstancelibEventManager instancelibEventManager;

	
	public PathRelation convertToPathRelation(RelationPO PO){
		PathRelation relation = new PathRelation();
		relation.setInstanceId(PO.getInstanceId());
		relation.setFromInstanceId(PO.getFromInstanceId());
		relation.setToInstanceId(PO.getToInstanceId());
		relation.setFromInstanceType(InstanceTypeEnum.valueOf(PO.getFromInstanceType()));
		relation.setToInstanceType(InstanceTypeEnum.valueOf(PO.getToInstanceType()));
		return relation;
	}
	
	public RelationPO convertToRelationPO(PathRelation pathRelation){
		RelationPO	relationPO = new RelationPO();
		relationPO.setInstanceId(pathRelation.getInstanceId());
		relationPO.setFromInstanceId(pathRelation.getFromInstanceId());
		relationPO.setToInstanceId(pathRelation.getToInstanceId());
		relationPO.setFromInstanceType(pathRelation.getFromInstanceType().toString());
		relationPO.setToInstanceType(pathRelation.getToInstanceType().toString());
		relationPO.setRelationType(pathRelation.getRelationType());
		return relationPO;
	}

	@Override
	public void updateRelation(List<Relation> listRelations) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateRelation start");
		}
		if(listRelations == null || listRelations.isEmpty()){
			throw new InstancelibException(InstancelibException.CODE_ERROR_VALIDATE, "update relation of compositeInstance for listRelations parameter is null or empty");
		}
		List<RelationPO> relationPOs = new ArrayList<>();
		for (Relation relation : listRelations) {
			RelationPO relationPO = null;
			// 后续添加的relation 需要通过relationType(类名判断获取相应的类实例)。
			if(relation instanceof PathRelation){
				PathRelation pathRelation = (PathRelation) relation;
				relationPO = convertToRelationPO(pathRelation);
			}
			if (relationPO != null) {
				relationPOs.add(relationPO);
			}
		}
		
		if(!relationPOs.isEmpty()){
			try {
				relationDAO.updateRelationPOs(relationPOs);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error(e);
				}
			}
		}
		//添加拦截器
		final InstancelibEvent instancelibEvent = new InstancelibEvent(
					listRelations, null, EventEnum.INSTANCE_RELATION_UPDATE_EVENT);
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateRelation end");
		}
	}
	
	@Override
	public List<Relation> getRelationByInstanceId(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getRelationByInstanceId start instanceId=" + instanceId);
		}
		List<RelationPO> listRelationPOs = null;
		try {
			listRelationPOs = relationDAO.getRelationPOsByInstanceId(instanceId);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e);
			}
		}
		List<Relation> listRelations = null;
		/*
		 * 实例关系
		 */
		if (listRelationPOs != null && !listRelationPOs.isEmpty()) {
			listRelations = new ArrayList<>();
			for (RelationPO relationPO : listRelationPOs) {
				// 后续添加的relation 需要通过relationType(类名判断获取相应的类实例)。
				if (PathRelation.class.getSimpleName().equals(relationPO.getRelationType())) {
					PathRelation relation = convertToPathRelation(relationPO);
					listRelations.add(relation);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getRelationByInstanceId end");
		}
		return listRelations;
	}
	
	@Override
	public List<Relation> getRelationByInstanceType(
			InstanceTypeEnum instanceTypeEnum) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getRelationByInstanceType start instanceType=" + instanceTypeEnum);
		}
		List<RelationPO> listRelationPOs = null;
		try {
			listRelationPOs = relationDAO.getRelationPOsByInstanceType(instanceTypeEnum.toString());
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e);
			}
		}
		List<Relation> listRelations = null;
		/*
		 * 实例关系
		 */
		if (listRelationPOs != null && !listRelationPOs.isEmpty()) {
			listRelations = new ArrayList<>();
			for (RelationPO relationPO : listRelationPOs) {
				// 后续添加的relation 需要通过relationType(类名判断获取相应的类实例)。
				if (PathRelation.class.getSimpleName().equals(relationPO.getRelationType())) {
					PathRelation relation = convertToPathRelation(relationPO);
					listRelations.add(relation);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getRelationByInstanceType end instanceType=" + instanceTypeEnum);
		}
		return listRelations;
	}
	
	public void insertRelationPOs(List<Relation> listRelations) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("insertRelationPOs start");
		}
		if(listRelations == null || listRelations.isEmpty()){
			throw new InstancelibException(InstancelibException.CODE_ERROR_VALIDATE, "insert relation of compositeInstance for listRelations parameter is null or empty");
		}
		List<RelationPO> relationPOs = new ArrayList<>();
		for (Relation relation : listRelations) {
			RelationPO relationPO = null;
			// 后续添加的relation 需要通过relationType(类名判断获取相应的类实例)。
			if(relation instanceof PathRelation){
				PathRelation pathRelation = (PathRelation) relation;
				relationPO = convertToRelationPO(pathRelation);
			}
			if (relationPO != null) {
				relationPOs.add(relationPO);
			}
		}
		
		if(!relationPOs.isEmpty()){
			try {
				relationDAO.insertRelationPOs(relationPOs);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("insertRelationPOs eror!",e);
				}
			}
		}
		//添加拦截器
		final InstancelibEvent instancelibEvent = new InstancelibEvent(
				listRelations, null, EventEnum.INSTANCE_RELATION_ADD_EVENT);
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("insertRelationPOs end");
		}
	}
	
	public void removeRelationPOByInstanceId(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeRelationPOByInstanceId start instanceId =" + instanceId);
		}
		try {
			relationDAO.removeRelationPOByInstanceId(instanceId);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("removeRelationPOByInstanceId eror!",e);
			}
		}
		//添加拦截器
		final InstancelibEvent instancelibEvent = new InstancelibEvent(
				instanceId, null, EventEnum.INSTANCE_RELATION_DELETE_EVENT);
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("removeRelationPOByInstanceId end");
		}
	}

	@Override
	public void removeRelation(long instanceId, InstanceTypeEnum type) throws InstancelibException{
		if (logger.isTraceEnabled()) {
			logger.trace("removeRelation start instanceId=" + instanceId + " type=" + type); 
		}
		try {
			relationDAO.removeRelationPOByInstanceIdAndType(instanceId, type.toString());
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("removeRelation eror!",e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeRelation end instanceId=" + instanceId + " type=" + type); 
		}
	}
	
	@Override
	public void removeRelation(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeRelation start instanceId=" + instanceId); 
		}
		try {
			relationDAO.removeRelationPOByInstanceId(instanceId);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("removeRelation eror!",e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeRelation end instanceId=" + instanceId); 
		}
	}
	
	/**
	 * 前置拦截（数据入库之前拦截）
	 * 
	 * @param instancelibEvent
	 *            事件
	 * @throws Exception
	 */
	private void listenerNotification(final InstancelibEvent instancelibEvent) {
		// 后置监听通知
		try {
			instancelibEventManager.doListen(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("",e);
			}
		}
	}
	
	public void setRelationDAO(RelationDAO relationDAO) {
		this.relationDAO = relationDAO;
	}

	public void setInstancelibEventManager(
			InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
	}

	
	
}
