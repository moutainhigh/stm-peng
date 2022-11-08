package com.mainsteam.stm.alarm.notify.dao;

import com.alibaba.druid.util.StringUtils;
import com.mainsteam.stm.alarm.obj.AlarmNotify;
import com.mainsteam.stm.alarm.obj.NotifyState;
import com.mainsteam.stm.alarm.obj.NotifyTypeEnum;
import com.mainsteam.stm.alarm.obj.AlarmEventWait;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmNotifyDAOImpl implements AlarmNotifyDAO {

	private static final Log logger= LogFactory.getLog(AlarmNotifyDAOImpl.class);

	private SqlSession session;
	private SqlSessionFactory sqlSessionFactory;
	
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	public void setSession(SqlSession session) {
		this.session = session;
	}	
	@Override
	public void addNotify(AlarmNotify notify) {
		session.insert("addNotify",notify);
	}

	@Override
	public void addNotifyBatch(List<AlarmNotify> notifies) {
		session.insert("addNotifyBatch", notifies);
	}

	@Override
	public List<AlarmNotify> findByAlarmID(long alarmID) {
		return session.selectList("findByAlarmID",alarmID);
	}
	@Override
	public void update(AlarmNotify notify) {
		session.update("updateNotify",notify);
	}
	
	@Override
	public AlarmNotify getNotifyByID(long notifyID) {
		return session.selectOne("getNotifyByID",notifyID);
	}
	@Override
	public List<AlarmNotify> findByTime(NotifyTypeEnum type,Long userID,Date start, Date end) {
		Map<String,Object> param=new HashMap<>();
		param.put("type", type);
		param.put("userID", userID);
		param.put("start", start);
		param.put("end", end);
		
		return session.selectList("findByTime",param);
	}
	@Override
	public List<AlarmNotify> findByState(NotifyState state, NotifyTypeEnum sendWay) {
		Map<String,Object> param=new HashMap<>();
		param.put("state", state);
		param.put("sendWay", sendWay);
		
		return session.selectList("findByState",param);
	}
	
	@Override
	public void updateNotifyState(List<Long> notifyIDes,NotifyState state) {
		if(notifyIDes != null && !notifyIDes.isEmpty()) {
			try(SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)){
				for(Long notifyID : notifyIDes) {
					Map<String,Object> param=new HashMap<>();
					param.put("notifyID", notifyID);
					param.put("state", state);
					sqlSession.selectList("updateNotifyState",param);
					
				}
				sqlSession.commit();
			}
			
		}
	}
	
	
	@Override
	public void replaceNotifyWait(AlarmEventWait eventWait ){
		session.insert("replaceNotifyWait",eventWait);
	}

	@Override
	public void replaceNotifyWaitBatch(List<AlarmEventWait> eventWaits) {
		String databaseId = session.getConfiguration().getDatabaseId();
		if(StringUtils.equals(databaseId, "mysql")) {
			session.insert("replaceNotifyWaitBatch", eventWaits);
		}else {
			for (AlarmEventWait wait : eventWaits) {
				session.insert("replaceNotifyWait",wait);
			}
		}
	}

	@Override
	public void updateNotifyWait(AlarmEventWait eventWait){
		session.insert("updateNotifyWait",eventWait);
	}
	
	@Override
	public void deleteNotifyWait(long id){
		if(logger.isDebugEnabled())
			logger.debug("Delete Notify Wait by ID :" + id);
		session.insert("deleteNotifyWait",id);
	}

	@Override
	public void deleteNotifyWaitByEventId(long eventId) {
		if(logger.isDebugEnabled())
			logger.debug("Delete Notify Wait by Event ID :" + eventId);
		session.delete("deleteNotifyWaitByEventID", eventId);
		
	}
	@Override
	public void deleteNotifyWaitByRecoverKey(String recoverKey,long alarmEventID){
		Map<String,Object> params=new HashMap<>();
		params.put("recoverKey", recoverKey);
		params.put("alarmEventID", alarmEventID);
		if(logger.isDebugEnabled())
			logger.debug("Delete Notify Wait by Recovery Key :{recoveryKey:" + recoverKey + ",EventID:" + alarmEventID + "}");
		session.insert("deleteNotifyWaitByRecoverKey",params);
	}

	@Override
	public void deleteNotifyWaitByRule(String recoverKey, long ruleId) {
		Map<String,Object> params=new HashMap<>();
		params.put("recoverKey", recoverKey);
		params.put("ruleID", ruleId);
		session.insert("deleteNotifyWaitByRule",params);
	}

	@Override
	public void deleteNotifyWaitByRecoveryKey(String recoveryKey) {
		session.delete("deleteNotifyWaitByRecovery", recoveryKey);
	}

	@Override
	public List<AlarmEventWait> findNotifyWait(Date startTime) {

		return session.selectList("findNotifyWait",startTime);
	}

	@Override
	public List<AlarmEventWait> findNotifyWaitByRecoveryKey(AlarmEventWait alarmEventWait) {
		return session.selectList("findNotifyWaitByCondition", alarmEventWait);
	}

	@Override
	public void deleteNotifyWaitByParameters(Map<String, String> map) {
		 session.delete("deleteNotifyWaitByInsts", map);
	}
}
