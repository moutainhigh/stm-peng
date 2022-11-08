//package com.mainsteam.stm.instancelib.service;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.mainsteam.stm.instancelib.RelationshipService;
//import com.mainsteam.stm.instancelib.dao.InstanceDependenceRelationResultDAO;
//import com.mainsteam.stm.instancelib.dao.pojo.InstanceDependenceResultPO;
//import com.mainsteam.stm.instancelib.obj.InstanceDependenceReultEnum;
//
//
//public class RelationshipServiceImpl implements RelationshipService {
//
//	private static final Log logger = LogFactory.getLog(RelationshipServiceImpl.class);
//	
//	private InstanceDependenceRelationResultDAO instanceDependenceRelationResultDAO;
//	
//	@Override
//	public String getRootRelationByAlarmEventId(long alarmEventId) {
//		if(logger.isTraceEnabled()){
//			logger.trace("getRootRelationByAlarmEvent start");
//		}
//		InstanceDependenceResultPO instanceDependenceResultPO = null;
//		try {
//			instanceDependenceResultPO = instanceDependenceRelationResultDAO.getDependenceResultByAlarmEventIdAndType(alarmEventId, InstanceDependenceReultEnum.ROOT.name());
//		} catch (Exception e) {
//			if(logger.isErrorEnabled()){
//				logger.error(e);
//			}
//		}
//		String result = null;
//		if(instanceDependenceResultPO != null){
//			result = instanceDependenceResultPO.getResultValue();
//		}
//		if(logger.isTraceEnabled()){
//			logger.trace("getRootRelationByAlarmEvent end");
//		}
//		return result;
//	}
//
//	@Override
//	public String getAffectedRelationByAlarmEventId(long alarmEventId) {
//		if(logger.isTraceEnabled()){
//			logger.trace("getAffectedRelationByAlarmEvent start");
//		}
//		InstanceDependenceResultPO instanceDependenceResultPO = null;
//		try {
//			instanceDependenceResultPO = instanceDependenceRelationResultDAO.getDependenceResultByAlarmEventIdAndType(alarmEventId, InstanceDependenceReultEnum.AFFECTED.name());
//		} catch (Exception e) {
//			if(logger.isErrorEnabled()){
//				logger.error(e);
//			}
//		}
//		String result = null;
//		if(instanceDependenceResultPO != null){
//			result = instanceDependenceResultPO.getResultValue();
//		}
//		if(logger.isTraceEnabled()){
//			logger.trace("getAffectedRelationByAlarmEvent end");
//		}
//		return result;
//	}
//	
//	public void setInstanceDependenceRelationResultDAO(
//			InstanceDependenceRelationResultDAO instanceDependenceRelationResultDAO) {
//		this.instanceDependenceRelationResultDAO = instanceDependenceRelationResultDAO;
//	}
//
//}
