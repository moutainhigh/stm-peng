package com.mainsteam.stm.alarm.event.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.mainsteam.stm.alarm.obj.*;
import org.apache.ibatis.session.SqlSession;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportData;
import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportQuery;
import com.mainsteam.stm.alarm.po.AlarmEventPO;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class AlarmEventDAOImpl implements AlarmEventDAO {
	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}	
	
	@Override
	public void addAlarmEvent(AlarmEventPO event) {
		String databaseId = session.getConfiguration().getDatabaseId();
		if(StringUtils.isEmpty(event.getRecoverKey())) {
			event.setRecoverKey(event.getSysID() + "_" + event.getEventID());
		}
		if(StringUtils.equals(databaseId, "mysql")) {
			 session.update("addAlarmEventForMysql", event);
		}else if(StringUtils.equals(databaseId, "oracle")) {
			 session.update("addAlarmEventForOracle", event);
		}else {
			Map<String, String> params = new HashMap<>(2);
			params.put("recoveryKey",event.getRecoverKey());
			params.put("sysModuleEnum",event.getSysID().name());
			session.delete("deleteAlarmEvent", params);
			session.insert("addAlarmEvent", event);
		}
		AlarmEventDetail eventDetail = new AlarmEventDetail();
		eventDetail.setCollectionTime(event.getCollectionTime());
		eventDetail.setContent(event.getContent());
		eventDetail.setEventId(event.getEventID());
		eventDetail.setLevel(event.getLevel());
		eventDetail.setRecoveryKey(event.getRecoverKey());
		eventDetail.setSnapshotResult(event.getExt7());
		eventDetail.setSysModuleEnum(event.getSysID());
		session.insert("addAlarmEventDetail",eventDetail);
	}

	@Override
	public int recoverAlarmEvent(AlarmEventPO recoveryAlarmEvent) {
		return session.update("recoverAlarmEvent",recoveryAlarmEvent);
	}
	
	@Override
	public void recoverAlarmEventBySourceID(Map<String, Object> params) {
		session.update("deleteAlarmEventBySourceID",params);
	}

	@Override
	public void recoverDeletedAlarmEventBySourceID(Map<String, String> sourceID) {
		session.update("recoverDeletedAlarmEventBySourceID", sourceID);
	}

	@Override
	public void recoverAlarmEventByRecoverKey(String recoverKey, HandleType handleType) {
		Map<String,Object> param=new HashMap<String, Object>();
		param.put("recoverKey", recoverKey);
		param.put("handleType", handleType);
		param.put("updateTime", new Date());
		session.update("recoverAlarmEventByRecoverKey",param);
	}

	
	@Override
	public void updateAlarmEventHandleType(long eventID, HandleType handleType){
		Map<String,Object> param=new HashMap<String, Object>();
		param.put("eventID", eventID);
		param.put("handleType", handleType);
		session.update("updateAlarmEventHandleType",param);
	}

	
	@Override @Deprecated
	public List<AlarmEvent> findResourceEventDeprecated(Page<AlarmEvent,AlarmEventQuery> page) {
		return session.selectList("findAlarmEventDeprecated",page);
	}
	
	@Override @Deprecated
	public List<AlarmEvent> findResourceEvent(AlarmEventQuery query) {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("condition", query);
		return session.selectList("findAlarmEventDeprecated",param);
	}
	
	@Override @Deprecated
	public List<AlarmEvent> findResourceEvent(Page<AlarmEvent, AlarmEventQuery2> page) {
		return session.selectList("findAlarmEvent",page);
	}

	@Override
	public List<AlarmEvent> findResourceEvent(AlarmEventQuery2 query) {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("condition", query);
		return session.selectList("findAlarmEvent",param);
	}
	
	
	@Override
	public AlarmEvent getResourceEvent(long eventID,Boolean recovered) {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("eventID", eventID);
		param.put("recovered", recovered);
		return session.selectOne("getAlarmEvent",param);	
	}
	
	@Override
	public int countResourceEvent(List<String> sourceIDes,SysModuleEnum sysModule,InstanceStateEnum[] level,Date start,Date end,Boolean isRecovered) {
		Map<String,Object> param=new HashMap<>();
		param.put("sourceIDes",sourceIDes);
		param.put("sysModule",sysModule);
		param.put("level", level);
		param.put("start", start);
		param.put("end", end);
		param.put("isRecovered", isRecovered);
		Integer rst= session.selectOne("com.mainsteam.stm.alarm.event.dao.AlarmEventDAO.countAlarmEvent",param);
		return rst==null?0:rst;
	}
	
	@Override
	public List<InstanceAlarmEventReportData> findTotleAlarmReport(InstanceAlarmEventReportQuery query) {
		Map<String,InstanceAlarmEventReportData> map=new HashMap<String,InstanceAlarmEventReportData>();
		
		if(null!=query.getInstanceIDes()){
			for(long insID:query.getInstanceIDes()){
				InstanceAlarmEventReportData data=map.get(String.valueOf(insID));
				if(data==null){
					data=new InstanceAlarmEventReportData();
					data.setInstanceID(insID);
					map.put(String.valueOf(insID),data);
				}
			}
			
		}
		
		List<TotleAlarmReportObj> objList= session.selectList("findTotalAlarmReport",query);
		
		for(TotleAlarmReportObj obj : objList){
			InstanceAlarmEventReportData data=map.get(obj.getSourceID());
			if(data==null){
				data=new InstanceAlarmEventReportData();
				data.setInstanceID(Long.valueOf(obj.getSourceID()));
				map.put(obj.getSourceID(),data);
			}
			if(InstanceStateEnum.CRITICAL==obj.getLevel()){
				data.setCountCritical(obj.getCnt());
			}else if(InstanceStateEnum.SERIOUS==obj.getLevel()){
				data.setCountSerious(obj.getCnt());
			}else if(InstanceStateEnum.WARN == obj.getLevel()){
				data.setCountWarn(obj.getCnt());
			}else if(InstanceStateEnum.UNKOWN==obj.getLevel() ||InstanceStateEnum.NORMAL_UNKNOWN==obj.getLevel()){
				data.setCountUnkown(obj.getCnt());
			}
			data.setCountTotle(data.getCountTotle()+obj.getCnt());
			
			data.setCountRecover(data.getCountRecover()+ obj.getCover());
			data.setCountNotRecover(data.getCountNotRecover()+ obj.getNotCover());
		}
		return new ArrayList<>(map.values());
	}

	@Override
	public List<AlarmEvent> findTotleAlarmDetail(InstanceAlarmEventReportQuery query) {
		return session.selectList("findTotleAlarmDetail",query);
	}

	@Override
	public List<AlarmEvent> previewResourceEvent(Page<AlarmEvent, AlarmEventQuery2> page) {
		return session.selectList("previewAlarmEvent",page);
	}

	@Override
	public List<AlarmEvent> queryResourceEvent(Page<AlarmEvent, AlarmEventQuery2> page) {
		return session.selectList("queryAlarmEvent", page);
	}

	@Override
	public void updateAlarmEventExtForRecovered(AlarmEvent event) {
		session.update("updateAlarmEventExtForRecovered", event);
	}
	
	@Override
	public void updateAlarmEventExtForNotRecover(AlarmEvent event) {
		session.update("updateAlarmEventExtForNotRecover", event);
	}

	@Override
	public void updateItsmOrderState(ItsmAlarmData itsmData) {
		Map<String,Object> param=new HashMap<>();
		param.put("eventID",itsmData.getAlarmEventID());
		param.put("itsmJsonData",JSON.toJSONString(itsmData));
		
		session.update("updateItsmData", param);
	}

	@Override
	public void deleteAlarmEventByInstanceId(Map<String, String> instanceID) {
		session.delete("deleteAlarmEventByInstanceId", instanceID);
	}

	@Override
	public void deleteAlarmByRecoveryKey(String recoveryKey, SysModuleEnum sysModuleEnum) {
		Map<String, String> params = new HashMap<>(2);
		params.put("recoveryKey",recoveryKey);
		params.put("sysModuleEnum",sysModuleEnum.name());
		session.delete("deleteAlarmEvent", params);
		session.delete("deleteAlarmEventDetail", params);
	}

	@Override
	public List<AlarmEventDetail> queryAlarmHistory(String recoveryKey, SysModuleEnum sysModuleEnum) {
		Map<String, String> params = new HashMap<>(2);
		params.put("recoveryKey", recoveryKey);
		params.put("sysModuleEnum", sysModuleEnum.name());
		return session.selectList("queryAlarmHistory", params);
	}

	@Override
	public void updateAlarmEvent(Map<String, AlarmEventPO> parameters) {
		session.update("updateAlarmEvent", parameters);
	}

	@Override
	public List<AlarmEvent> exportAlarmEvent(AlarmEventQuery2 query, boolean isRecovery) {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("condition", query);
		if(isRecovery){
			return session.selectList("exportAlarmEventRecovered", param);
		}else{
			return session.selectList("exportAlarmEventUnrecovered", param);
		}
	}
}
