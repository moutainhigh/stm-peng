//package com.mainsteam.stm.instancelib;
//
//import com.mainsteam.stm.instancelib.exception.InstancelibRelationException;
//
//public interface RelationshipService {
//
//	/**
//	 * 通过资源实例告警值查询根源关系
//	 * 只针对致命级别告警查询
//	 * @param alarmEventId     告警事件
//	 * @return 影响关系路径
//	 * @throws InstancelibRelationException
//	 */
//	public String getRootRelationByAlarmEventId(long alarmEventId) throws InstancelibRelationException;
//	
//	
//	/**
//	 * 根据资源实例告警值查询受影响关系
//	 * 只针对致命级别告警查询
//	 * @param alarmEventId     告警事件
//	 * @return 影响关系路径
//	 * @throws InstancelibRelationException
//	 */
//	public String getAffectedRelationByAlarmEventId(long alarmEventId) throws InstancelibRelationException;
//
//}
