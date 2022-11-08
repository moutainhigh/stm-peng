package com.mainsteam.stm.alarm.event.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportData;
import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportQuery;
import com.mainsteam.stm.alarm.obj.*;
import com.mainsteam.stm.alarm.po.AlarmEventPO;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * @author cx
 *
 */
public interface AlarmEventDAO {
	
	/**
	 * @param event
	 */
	 void addAlarmEvent(AlarmEventPO event);

	/**恢复事件
	 * @return The number of rows effected.
	 */
	 int recoverAlarmEvent(AlarmEventPO recoveryAlarmEvent);

	/**
	 * 首页告警一览和24小时告警统计
	 * @param page
	 * @return
     */
	 List<AlarmEvent> previewResourceEvent(Page<AlarmEvent,AlarmEventQuery2> page);

	/**
	 * 告警管理查询新街口
	 * @param page
	 * @return
     */
	 List<AlarmEvent>  queryResourceEvent(Page<AlarmEvent,AlarmEventQuery2> page);
	/**
	 * @param page
	 * @return
	 */
	@Deprecated
	 List<AlarmEvent>  findResourceEventDeprecated(Page<AlarmEvent,AlarmEventQuery> page);
	@Deprecated
	 List<AlarmEvent>  findResourceEvent(Page<AlarmEvent,AlarmEventQuery2> page);


	@Deprecated
	 List<AlarmEvent>  findResourceEvent(AlarmEventQuery query);
	
	 List<AlarmEvent>  findResourceEvent(AlarmEventQuery2 query);

	/**
	 * @param eventID
	 * @param b 
	 * @return
	 */
	 AlarmEvent getResourceEvent(long eventID, Boolean b);


	 int countResourceEvent(List<String> sourceIDes,SysModuleEnum sysModule,InstanceStateEnum[] level,Date start,Date end,Boolean isRecovered) ;
	/**
	 * @param query
	 * @return
	 */
	 List<InstanceAlarmEventReportData> findTotleAlarmReport(InstanceAlarmEventReportQuery query);
	
	 List<AlarmEvent> findTotleAlarmDetail(InstanceAlarmEventReportQuery query);
	

	/** 更新告警的扩展信息
	 * @param event
	 */
	void updateAlarmEventExtForRecovered(AlarmEvent event);
	/** 更新告警的扩展信息
	 * @param event
	 */
	void updateAlarmEventExtForNotRecover(AlarmEvent event);
	
	 void updateItsmOrderState(ItsmAlarmData itsmData);

	 void recoverAlarmEventBySourceID(Map<String, Object> params);

	 void recoverDeletedAlarmEventBySourceID(Map<String, String> sourceID);

	 void recoverAlarmEventByRecoverKey(String recoverKey, HandleType type);

	 void deleteAlarmByRecoveryKey(String recoveryKey, SysModuleEnum sysModuleEnum);

	/** 修改告警的操作类型，应产品要求，添加该 功能点
	 * @param eventID
	 * @param handleType
	 */
	void updateAlarmEventHandleType(long eventID, HandleType handleType);

	void deleteAlarmEventByInstanceId(Map<String, String> params);

	List<AlarmEventDetail> queryAlarmHistory(String recoveryKey, SysModuleEnum sysModuleEnum);

	void updateAlarmEvent(Map<String, AlarmEventPO> parameters);

	/**
	 * export all alarm event
	 * @param query
	 * @param isRecovery
	 * @return
	 */
	List<AlarmEvent>  exportAlarmEvent(AlarmEventQuery2 query, boolean isRecovery);
}
