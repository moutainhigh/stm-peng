package com.mainsteam.stm.alarm.event;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.alarm.obj.*;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/** 事件查询接口服务
 * @author heshengchao
 *
 */
public interface AlarmEventService {

	/**查询告警事件
	 * @param query
	 * @param startRow
	 * @param pageSize
	 * 
	 * @return
	 * @deprecated  replace by {@link AlarmEventService#findAlarmEvent(AlarmEventQuery2, int, int)}
	 */
	@Deprecated
	Page<AlarmEvent,AlarmEventQuery> findAlarmEvent(AlarmEventQuery query,int startRow,int pageSize);
	@Deprecated
	Page<AlarmEvent,AlarmEventQuery2> findAlarmEvent(AlarmEventQuery2 query,int startRow,int pageSize);

	/**
	 * 告警管理查询新接口
	 * @param query
	 * @param startRow
	 * @param pageSize
     * @return
     */
	Page<AlarmEvent,AlarmEventQuery2> queryAlarmEvent(AlarmEventQuery2 query,int startRow,int pageSize);

	/**
	 * 首页，告警一览和告警24小时统计
	 * @param query
	 * @param startRow
	 * @param pageSize
     * @return
     */
	Page<AlarmEvent, AlarmEventQuery2> previewAlarmEvent(AlarmEventQuery2 query,int startRow,int pageSize);

	/**查询告警事件
	 * @param query
	 * @return
	 * @deprecated  replace by {@link AlarmEventService#findAlarmEvent(AlarmEventQuery2)}
	 */
	@Deprecated
	List<AlarmEvent> findAlarmEvent(AlarmEventQuery query);

	List<AlarmEvent> findAlarmEvent(AlarmEventQuery2 query);
	
	/** 更新告警的扩展信息
	 * @param event
	 */
	void updateAlarmEventExt(AlarmEvent event);
	
	
	/**查询告警事件
	 * @param eventID
	 * @param recovered 可为NULL
	 * @return
	 */
	AlarmEvent getAlarmEvent(long eventID,Boolean recovered);
	
	/**
	 * @param sourceIDes
	 * @param sysModule
	 * @param level
	 * @param start
	 * @param end
	 * @param isRecovered
	 * @return
	 */
	int countAlarmEvent(List<String> sourceIDes,SysModuleEnum sysModule,InstanceStateEnum[] level,Date start,Date end,Boolean isRecovered);

	/**
	 * @param event
	 */
	void recoverAlarmEvent(AlarmEvent event);
	
	/**
	 * @param sourceID
	 * @param type
	 */
	void recoverAlarmEventBySourceID(String sourceID, HandleType type);

	void recoverAlarmEventBySourceID(String sourceID, HandleType type, boolean isParent);

	/**
	 * @param recoverKey
	 * @param type
	 */
	void recoverAlarmEventByRecoverKey(String recoverKey, HandleType type);

	/**
	 * @param event
	 */
	boolean addAlarmEvent(AlarmEvent event);

	boolean addAlarmEvent(AlarmEvent event , boolean isUpdate);


	/** 更新Itsm状态
	 * @param itsmData
	 */
	void updateItsmOrderState(ItsmAlarmData itsmData);
	/**修改告警的操作类型，应产品要求，添加该 功能点
	 * @param eventID
	 * @param handleType
	 */
	void updateAlarmEventHandleType(long eventID, HandleType handleType);

	void recoveryDeletedAlarmEventBySourceID(String sourceId);

	void recoveryDeletedAlarmEventBySourceID(String sourceId, boolean isParent);

	void deleteAlarmEventByInstanceId(String instanceID);

	void deleteAlarmEventByInstanceId(String instanceID, boolean isParent);

	void deleteAlarmEventByRecoveryKey(String recoveryKey, SysModuleEnum sysModuleEnum);

	List<AlarmEventDetail> queryAlarmHistory(String recoveryKey, SysModuleEnum sysModuleEnum);

	void updateAlarmEvent(AlarmEvent set, AlarmEvent where);

	List<AlarmEvent> exportAlarmEvent(AlarmEventQuery2 query, boolean isRecovered);

}
