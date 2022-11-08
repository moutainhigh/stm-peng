package com.mainsteam.stm.alarm.event;


import java.util.*;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.AlarmSyncService;
import com.mainsteam.stm.alarm.obj.*;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.event.dao.AlarmEventDAO;
import com.mainsteam.stm.alarm.po.AlarmEventPO;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQuery.OrderField;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class AlarmEventServiceImpl implements AlarmEventService,BeanPostProcessor {
	private static final Log logger=LogFactory.getLog(AlarmEventServiceImpl.class);

	private AlarmEventDAO alarmEventDao;
	private ISequence sequence;
	private AlarmSyncService alarmEventSyncService;

	private ExecutorService threadExecutor =new ThreadPoolExecutor(5,50,30L, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(),new ThreadFactory() {
		private int counter = 0;
		@Override
		public Thread newThread(Runnable runnable) {
			Thread t = new Thread(runnable,"alarmNotifyThead-"+ counter++);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	});

	private List<AlarmEventMonitor> handles=new ArrayList<AlarmEventMonitor>();

	public void setAlarmEventDao(AlarmEventDAO resourceEventDao) {
		this.alarmEventDao = resourceEventDao;
	}

	public void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	public void setAlarmEventSyncService(AlarmSyncService alarmEventSyncService) {
		this.alarmEventSyncService = alarmEventSyncService;
	}

	@Override	@Deprecated
	public List<AlarmEvent> findAlarmEvent(AlarmEventQuery query) {
		if(query.getOrderFieldes()==null){
			query.setOrderFieldes(new OrderField[]{OrderField.COLLECTION_TIME});
		}
		return alarmEventDao.findResourceEvent(query);
	}
	@Override 	@Deprecated
	public Page<AlarmEvent,AlarmEventQuery> findAlarmEvent(AlarmEventQuery query, int startRow, int pageSize) {
		if(query.getOrderFieldes()==null){
			query.setOrderFieldes(new OrderField[]{OrderField.COLLECTION_TIME});
		}
		Page<AlarmEvent,AlarmEventQuery> page=new Page<>(startRow,pageSize,query);
		alarmEventDao.findResourceEventDeprecated(page);
		return page;
	}

	@Override @Deprecated
	public Page<AlarmEvent, AlarmEventQuery2> findAlarmEvent(AlarmEventQuery2 query, int startRow, int pageSize) {

		if(query.getOrderFieldes()==null){
			query.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.COLLECTION_TIME});
		}
		Page<AlarmEvent,AlarmEventQuery2> page=new Page<>(startRow,pageSize,query);
		alarmEventDao.findResourceEvent(page);
		return page;
	}

	@Override
	public Page<AlarmEvent, AlarmEventQuery2> queryAlarmEvent(AlarmEventQuery2 query, int startRow, int pageSize) {

		if(null == query.getOrderCollections() || query.getOrderCollections().isEmpty()) {
			Map<AlarmEventQuery2.OrderField, AlarmEventQuery2.OrderAscOrDesc> orders = new LinkedHashMap<>(1);
			if(query.getOrderFieldes()==null){
				orders.put(AlarmEventQuery2.OrderField.COLLECTION_TIME, AlarmEventQuery2.OrderAscOrDesc.DESC);
				query.setOrderCollections(orders);
			}else {
				for(AlarmEventQuery2.OrderField orderField : query.getOrderFieldes()) {
					orders.put(orderField, query.isOrderASC() ? AlarmEventQuery2.OrderAscOrDesc.ASC : AlarmEventQuery2.OrderAscOrDesc.DESC);
				}
				query.setOrderCollections(orders);
			}

		}
		Page<AlarmEvent,AlarmEventQuery2> page=new Page<>(startRow,pageSize,query);
		alarmEventDao.queryResourceEvent(page);
		return page;
	}

	@Override
	public List<AlarmEvent> findAlarmEvent(AlarmEventQuery2 query) {
		if(query.getOrderFieldes()==null){
			query.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.COLLECTION_TIME});
		}
		return alarmEventDao.findResourceEvent(query);
	}


	@Override
	public int countAlarmEvent(List<String> sourceIDes,SysModuleEnum sysModule,InstanceStateEnum[] level,Date start,Date end,Boolean isRecovered) {
		return alarmEventDao.countResourceEvent(sourceIDes,sysModule,level,start,end,isRecovered);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.alarm.event.AlarmEventService#getResourceEvent(long, boolean)
	 */
	@Override
	public AlarmEvent getAlarmEvent(long eventID, Boolean recovered) {
		return alarmEventDao.getResourceEvent(eventID,recovered);
	}

	@Override
	public Page<AlarmEvent, AlarmEventQuery2> previewAlarmEvent(AlarmEventQuery2 query, int startRow, int pageSize) {
		if(logger.isDebugEnabled()) {
			logger.debug("starts to preview the last alarm " + JSONObject.toJSONString(query));
		}
		if(query.getOrderFieldes()==null){
			query.setOrderFieldes(new AlarmEventQuery2.OrderField[]{AlarmEventQuery2.OrderField.COLLECTION_TIME});
		}
		Page<AlarmEvent,AlarmEventQuery2> page=new Page<>(startRow,pageSize,query);

		alarmEventDao.previewResourceEvent(page);
		return page;
	}

	@Override
	public void recoverAlarmEventBySourceID(String sourceID,HandleType type) {
		Map<String,Object> param=new HashMap<>();
		param.put("sourceID", sourceID);
		param.put("handleType", type);
		param.put("updateTime", new Date());
		alarmEventDao.recoverAlarmEventBySourceID(param);
	}

	@Override
	public void recoverAlarmEventBySourceID(String sourceID, HandleType type, boolean isParent) {
		Map<String,Object> param=new HashMap<>();
		if(isParent) {
			param.put("ext8", sourceID);
		}else{
			param.put("sourceID", sourceID);
		}
		param.put("handleType", type);
		param.put("updateTime", new Date());
		alarmEventDao.recoverAlarmEventBySourceID(param);
	}

	@Override
	public void recoverAlarmEventByRecoverKey(String recoverKey,HandleType type) {
		alarmEventDao.recoverAlarmEventByRecoverKey(recoverKey,type);
	}

	@Override
	public void recoveryDeletedAlarmEventBySourceID(String sourceId) {
		Map<String, String> params = new HashMap<>(1);
		params.put("sourceId", sourceId);
		alarmEventDao.recoverDeletedAlarmEventBySourceID(params);
	}

	@Override
	public void recoveryDeletedAlarmEventBySourceID(String sourceId, boolean isParent) {
		Map<String, String> params = new HashMap<>(1);
		if(isParent){
			params.put("ext8", sourceId);
		}else {
			params.put("sourceId", sourceId);
		}
		alarmEventDao.recoverDeletedAlarmEventBySourceID(params);
	}

	@Override
	public void recoverAlarmEvent(AlarmEvent recoveryAlarmEvent) {
		alarmEventDao.recoverAlarmEvent(convertTOPO(recoveryAlarmEvent));
	}

	@Override
	public boolean addAlarmEvent(final AlarmEvent event) {
		if(logger.isTraceEnabled())
			logger.trace("addAlarmEvent:"+JSON.toJSONString(event));

		AlarmEventPO po = convertTOPO(event);
		if(po.getEventID() == 0L) {
			po.setEventID(sequence.next());
		}
		ItsmAlarmData itms=new ItsmAlarmData();
		itms.setState(ItsmOrderStateEnum.NOT_SEND);
		itms.setUpdateTime(new Date());
		po.setItsmData(itms);
		int effectedRowsNumber = 0;
		boolean isSuccess = false;
		if(po.isRecoveryEvent() || InstanceStateEnum.NORMAL==po.getLevel()){
			po.setHandleType(HandleType.AUTO);
			effectedRowsNumber = alarmEventDao.recoverAlarmEvent(po);
			po.setRecovered(true);
		}else{
			po.setHandleType(HandleType.NONE);
			effectedRowsNumber = -1;
		}
		//避免直接生成告警已恢复告警，逻辑上必须是先有告警信息，才能有恢复告警
		if(effectedRowsNumber > 0 || InstanceStateEnum.NORMAL != po.getLevel()) {
			alarmEventDao.addAlarmEvent(po);
			isSuccess = true;
		}

		threadExecutor.execute(new Runnable() {
			@Override public void run() {
				for(AlarmEventMonitor handle: handles){
					try{
						if(logger.isTraceEnabled())
							logger.trace("sync AlarmEvent to handle:"+handle.getClass());
						handle.handleEvent(event);
					}catch(Exception e){
						if(logger.isErrorEnabled())
							logger.error(e.getMessage(),e);
					}
				}
			}
		});

		threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try{
					AlarmSenderParamter alarmSenderParamter = new AlarmSenderParamter();
					alarmSenderParamter.setSourceID(event.getSourceID());
					alarmSenderParamter.setSourceName(event.getSourceName());
					alarmSenderParamter.setSourceIP(event.getSourceIP());
					alarmSenderParamter.setLevel(event.getLevel());
					alarmSenderParamter.setExt0(event.getExt0());
					alarmSenderParamter.setExt3(event.getExt3());
					alarmSenderParamter.setDefaultMsg(event.getContent());
					alarmSenderParamter.setGenerateTime(event.getCollectionTime());
					if(logger.isDebugEnabled()) {
						logger.debug("starts to sync itba data : " + JSONObject.toJSONString(alarmSenderParamter));
					}
					alarmEventSyncService.sync(alarmSenderParamter);
				}catch (Exception e) {
					if(logger.isWarnEnabled()) {
						logger.warn("sync itba data error, " + e.getMessage(), e);
					}
				}
			}
		});

		return  isSuccess;
	}

	@Override
	public boolean addAlarmEvent(AlarmEvent event , boolean isUpdate) {
		if(event instanceof AlarmEventPO){
			if(event.getEventID() == 0L){
				event.setEventID(sequence.next());
			}
			alarmEventDao.addAlarmEvent((AlarmEventPO) event);
			return true;
		}
		return false;
	}

	private AlarmEventPO convertTOPO(AlarmEvent event){
		AlarmEventPO po;
		if(event instanceof AlarmEventPO) {
			po = (AlarmEventPO) event;
		}else {
			po = new AlarmEventPO();
			po.setCollectionTime(event.getCollectionTime());
			po.setContent(event.getContent());
			po.setEventID(event.getEventID());
			po.setLevel(event.getLevel());
			po.setProvider(event.getProvider());
			po.setSysID(event.getSysID());
			po.setSourceID(event.getSourceID());
			po.setSourceIP(event.getSourceIP());
			po.setSourceName(event.getSourceName());
			po.setItsmData(event.getItsmData());
			po.setRecoverKey(event.getRecoverKey());
			if(InstanceStateEnum.NORMAL==event.getLevel()){
				po.setRecovered(true);
				po.setRecoveryEvent(true);
				po.setRecoveryEventID(event.getEventID());
				po.setRecoveryTime(event.getCollectionTime());
			}

			po.setExt0(event.getExt0());
			po.setExt1(event.getExt1());
			po.setExt2(event.getExt2());
			po.setExt3(event.getExt3());
			po.setExt4(event.getExt4());
			po.setExt5(event.getExt5());
			po.setExt6(event.getExt6());
			po.setExt7(event.getExt7());
			po.setExt8(event.getExt8());
			po.setExt9(event.getExt9());
		}
		return po;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.alarm.event.AlarmEventService#updateAlarmEventExt(com.mainsteam.stm.alarm.obj.AlarmEvent)
	 */
	@Override
	public void updateAlarmEventExt(AlarmEvent event) {
		AlarmEvent po=alarmEventDao.getResourceEvent(event.getEventID(),null);
		if(po.isRecovered()){
			alarmEventDao.updateAlarmEventExtForRecovered(event);
		}else{
			alarmEventDao.updateAlarmEventExtForNotRecover(event);
		}
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)	throws BeansException {
		if(bean instanceof AlarmEventMonitor){
			handles.add((AlarmEventMonitor)bean);
		}
		return bean;
	}

	@Override
	public void updateItsmOrderState(ItsmAlarmData itsmData) {
		AlarmEvent po=alarmEventDao.getResourceEvent(itsmData.getAlarmEventID(),null);
		if(po==null){
			logger.error("can't find alarm:"+itsmData.getAlarmEventID()+", for data:"+JSON.toJSONString(itsmData));
			return;
		}
		itsmData.setUpdateTime(new Date());
		alarmEventDao.updateItsmOrderState(itsmData);
	}

	/** 修改告警的操作类型，应产品要求，添加该 功能点
	 * @param eventID
	 * @param handleType
	 */
	@Override
	public void updateAlarmEventHandleType(long eventID, HandleType handleType){
		alarmEventDao.updateAlarmEventHandleType(eventID, handleType);
	}

	@Override
	public void deleteAlarmEventByInstanceId(String instanceID) {
		Map<String, String> params = new HashMap<>(1);
		params.put("sourceId", instanceID);
		alarmEventDao.deleteAlarmEventByInstanceId(params);
	}

	@Override
	public void deleteAlarmEventByRecoveryKey(String recoveryKey, SysModuleEnum sysModuleEnum) {
		alarmEventDao.deleteAlarmByRecoveryKey(recoveryKey,sysModuleEnum);
	}

	@Override
	public void deleteAlarmEventByInstanceId(String instanceID, boolean isParent) {
		Map<String, String> params = new HashMap<>(1);
		if(isParent){
			params.put("ext8", instanceID);
		}else{
			params.put("sourceId", instanceID);
		}
		alarmEventDao.deleteAlarmEventByInstanceId(params);
	}

	@Override
	public List<AlarmEventDetail> queryAlarmHistory(String recoveryKey, SysModuleEnum sysModuleEnum) {
		return alarmEventDao.queryAlarmHistory(recoveryKey, sysModuleEnum);
	}

	@Override
	public void updateAlarmEvent(AlarmEvent set, AlarmEvent where) {
		Map<String, AlarmEventPO> parameters = new HashMap<>(2);
		parameters.put("parameterSet", convertTOPO(set));
		parameters.put("parameterWhere", convertTOPO(where));

		alarmEventDao.updateAlarmEvent(parameters);
	}

	@Override
	public List<AlarmEvent> exportAlarmEvent(AlarmEventQuery2 query, boolean isRecovered) {
		return alarmEventDao.exportAlarmEvent(query, isRecovered);
	}
}
