package com.mainsteam.stm.alarm.notify;

import java.util.*;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.obj.*;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.profilelib.alarm.obj.*;
import com.mainsteam.stm.system.um.user.bo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.notify.dao.AlarmNotifyDAO;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.lock.LockCallback;
import com.mainsteam.stm.lock.LockService;
import com.mainsteam.stm.message.MessageSendHelper;
import com.mainsteam.stm.message.ResultMessage;
import com.mainsteam.stm.message.mail.MailClient;
import com.mainsteam.stm.message.mail.MailSenderInfo;
import com.mainsteam.stm.message.smsmodem.SentRecord;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.system.um.user.api.IUserApi;

public class AlarmNotifyServiceImpl implements AlarmNotifyService {
	
	private static final Log logger=LogFactory.getLog(AlarmNotifyServiceImpl.class);
	private static final int FIRST_ENTITY = 0;
	private static final int NULL_TEMPLATE = 0;
	private AlarmNotifyDAO alarmNotifyDao;
	private AlarmRuleService alarmRuleService;
	private ISequence sequence;
	private LockService lockService;
	private IUserApi userApi;
	private SmsOrEmailNotifyTemplateService smsOrEmailNotifyTemplateService;

	ExecutorService fixedThreadPool = new ThreadPoolExecutor(20,100,3000L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),
		new ThreadFactory() {
			private long number = 0;
			@Override public Thread newThread(Runnable r) {
				return new Thread(r, "AlarmNotifySender-"+number++);
			}
		});



	public void setUserApi(IUserApi userApi) {
		this.userApi = userApi;
	}
	public void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}
	public void setAlarmRuleService(AlarmRuleService alarmRuleService) {
		this.alarmRuleService = alarmRuleService;
	}

	
	public void setAlarmNotifyDao(AlarmNotifyDAO alarmNotifyDao) {
		this.alarmNotifyDao = alarmNotifyDao;
	}

	public void setSmsOrEmailNotifyTemplateService(SmsOrEmailNotifyTemplateService smsOrEmailNotifyTemplateService) {
		this.smsOrEmailNotifyTemplateService = smsOrEmailNotifyTemplateService;
	}

	@Override
	public AlarmNotify getNotifyByID(long notifyID) {
		return alarmNotifyDao.getNotifyByID(notifyID);
	}

	@Override
	public List<AlarmNotify> findByTime(NotifyTypeEnum type,Long userID,Date start, Date end) {
		return alarmNotifyDao.findByTime(type,userID,start,end);
	}
	@Override
	public List<AlarmNotify> findByAlarmID(long alarmID){
		return alarmNotifyDao.findByAlarmID(alarmID);
	}
	
	@Override 
	public void updateNotifyState(List<Long> notifyIDes,NotifyState state){
		alarmNotifyDao.updateNotifyState(notifyIDes,state);
	}

	@Override
	public void addNotify(List<AlarmNotify> notifies, String recoveryKey, long alarmEventID) {
		if(logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Send alarm notify with alarmEventID ");
			sb.append(alarmEventID);
			logger.debug(sb.toString());
		}
		alarmNotifyDao.deleteNotifyWaitByRecoverKey(recoveryKey, alarmEventID);
		List<AlarmNotify> immediateNotifies = new ArrayList<>(notifies.size()/2); //???????????????????????????
		final List<AlarmEventWait> alarmEventWaitList = new ArrayList<>(notifies.size()/2); //???????????????
		for(final AlarmNotify alarmNotify : notifies) {
			final long alarmNotifyId = this.sequence.next();
			alarmNotify.setNotifyID(alarmNotifyId);
			if(logger.isDebugEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append("send alarm notify with notify id");
				sb.append(alarmNotify.getNotifyID());
				sb.append(",sendWay is ");
				sb.append(alarmNotify.getNotifyType());
				sb.append(",alarmEventID is ");
				sb.append(alarmEventID);
				logger.debug(sb.toString());
			}
			//AlarmSendCondition sendCondition = alarmRuleService.getAlarmSendCondition(alarmNotify.getAlarmRuleID(), alarmNotify.getNotifyType());
			AlarmSendCondition sendCondition = alarmNotify.getAlarmSendCondition();
			alarmNotify.setRemainSendTimes(sendCondition.getSendTimes() == 0 ? -1 : sendCondition.getSendTimes());
			boolean isHandle = dispatcher(sendCondition, new CallBack(){
				@Override
				public boolean handle(AlarmSendCondition sendCondition) {//???????????????????????????
					return doHandleOnce(sendCondition, alarmNotify, alarmNotifyId, alarmEventWaitList);
				}

				@Override public boolean handleError() {return false;}

				@Override
				public boolean handleContinuous(AlarmSendCondition sendCondition) {//??????????????????????????????????????????
					return doHandleRepeatly(sendCondition, alarmNotify, alarmNotifyId, alarmEventWaitList);
				}

			});

			//??????????????????????????????
			if(isHandle) {
				alarmNotify.setState(NotifyState.SENDING);
				alarmNotify.setContinusNum(1);
				immediateNotifies.add(alarmNotify);
			}
		}

		if(!immediateNotifies.isEmpty()) {
			if(logger.isDebugEnabled()) {
				logger.debug("Starts to send alarm notify with AlarmEventID " + alarmEventID);
			}
			send(immediateNotifies);
		}

		if(notifies != null && !notifies.isEmpty()) {
			alarmNotifyDao.addNotifyBatch(notifies);
			for(AlarmNotify notify : notifies) {
				if(notify.getNotifyType() == SendWayEnum.alert) {
					IMemcache<List> alertUserList = MemCacheFactory.getRemoteMemCache(List.class);
					if(null != alertUserList) {
						List<Map<String, String>> userIPList = alertUserList.get(SendWayEnum.alert.toString() + notify.getNotifyUserID());
						if(null != userIPList && !userIPList.isEmpty()) {
							for(Map<String, String> map : userIPList) {
								map.put("refresh", "true");
							}
							alertUserList.set(SendWayEnum.alert.toString() + notify.getNotifyUserID(), userIPList);
						}
					}
				}
			}
		}
		if(alarmEventWaitList != null && !alarmEventWaitList.isEmpty()){
			try{
				alarmNotifyDao.replaceNotifyWaitBatch(alarmEventWaitList);
			}catch(Exception e) {
				if(logger.isErrorEnabled()) {
					logger.error("Merge Table 'STM_ALARM_NOTIFY_WAIT' error! Data is " + alarmEventWaitList);
				}
				throw e;
			}
		}
	}

	/**
	 * ?????????????????????????????????????????????????????????
	 * @param sendCondition
	 * @return
     */
	private boolean computeSendPeriods(final AlarmSendCondition sendCondition) {

		final int currentMinutes = getMinOfDay();
		boolean isNeedSend = false;
		List<AlarmNotifyPeriodForDay> dayPeriods = sendCondition.getDayPeriodes(); //?????????????????????
		List<AlarmNotifyPeriodForWeek> weekPeriods = sendCondition.getWeekPeriodes(); //?????????????????????

		if(null != dayPeriods && !dayPeriods.isEmpty()){
			for(AlarmNotifyPeriodForDay pd : dayPeriods){
				if(pd.getStart()<=currentMinutes && pd.getEnd()>=currentMinutes){
					isNeedSend = true;
					break;
				}
			}
		}else if(weekPeriods!=null && !weekPeriods.isEmpty()){
			int dw = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			for(AlarmNotifyPeriodForWeek wp : weekPeriods){
				if(wp.getDayOfWeek()==dw){
					if(wp.getStart() <=currentMinutes && wp.getEnd() >=currentMinutes) {
						isNeedSend = true;
						break;
					}
				}
			}
		}
		return isNeedSend;
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????
	 * @param sendCondition
	 * @param call
     * @return
     */
	private boolean dispatcher(AlarmSendCondition sendCondition, CallBack call){
		if(null==sendCondition){
			return call.handleError();
		}
		if(sendCondition.isEnabled()) {
			if(sendCondition.isContinus()) {//??????????????????????????????
				return call.handleContinuous(sendCondition);
			}else {
				return call.handle(sendCondition);//???????????????????????????
			}
		}else {
			return false;
		}
	}

	/**
	 * ??????????????????????????????????????????
	 * @param sendCondition
	 * @param alarmNotify
	 * @param alarmEventWaitId
	 * @param alarmEventWaits
     * @return
     */
	private boolean doHandleOnce(final AlarmSendCondition sendCondition, final AlarmNotify alarmNotify,
										final long alarmEventWaitId, final List<AlarmEventWait> alarmEventWaits) {
		boolean isNeedSend = false;//???????????????????????????true??????????????????
		boolean isNeedPutIntoWait = false;//????????????????????????????????????????????????true????????????
		if(sendCondition.isAllTime())
			isNeedSend = true;
		else {
			isNeedSend = computeSendPeriods(sendCondition);
			if(sendCondition.isSendIntime() && !isNeedSend) {//?????????????????????????????????
				isNeedPutIntoWait = true;
			}
		}
		if(isNeedPutIntoWait) {
			AlarmEventWait wait = new AlarmEventWait();
			wait.setEventID(alarmNotify.getAlarmID());
			wait.setId(alarmEventWaitId);
			alarmNotify.setRemainSendTimes(1);
			wait.setContent(JSON.toJSONString(alarmNotify));
			wait.setRecoverKey(alarmNotify.getRecoverKey());
			wait.setRuleID(alarmNotify.getAlarmRuleID());
			wait.setExecTime(new Date());
			alarmEventWaits.add(wait);
		}
		return isNeedSend;
	}

	private boolean doHandleRepeatly(final AlarmSendCondition sendCondition, final AlarmNotify alarmNotify,
								 final long alarmEventWaitId, final List<AlarmEventWait> alarmEventWaits) {
		boolean isNeedSend = false; //?????????????????????????????????true???????????????false???????????????
		boolean isPutIntoWait = false; //??????????????????????????????????????????true????????????
		if(sendCondition.isAllTime()) {//7*24?????????????????????
			isPutIntoWait = true;
			isNeedSend = true;
		}else {
			/*
			??????????????????????????????
			1.????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????, ???????????????????????????
			2.???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			 */
			if(sendCondition.isSendIntime()) {
				isPutIntoWait = true;
			}
			isNeedSend = computeSendPeriods(sendCondition);
		}
		if(alarmNotify.getRemainSendTimes() != -1 && ((sendCondition.getSendTimes() - alarmNotify.getRemainSendTimes()) >= sendCondition.getSendTimes())){
			isPutIntoWait = false;
			isNeedSend = false;
		}
		if(isPutIntoWait || isNeedSend) {
			if(alarmNotify.getLevel() != InstanceStateEnum.NORMAL){//????????????????????????????????????????????????????????????????????????
				AlarmEventWait wait = new AlarmEventWait();
				wait.setEventID(alarmNotify.getAlarmID());
				wait.setId(alarmEventWaitId);
				if(isNeedSend && alarmNotify.getRemainSendTimes() != -1) {
					alarmNotify.setRemainSendTimes(alarmNotify.getRemainSendTimes()-1);
				}
				wait.setContent(JSON.toJSONString(alarmNotify));
				wait.setRecoverKey(alarmNotify.getRecoverKey());
				wait.setRuleID(alarmNotify.getAlarmRuleID());
				int timeType = ContinusUnitEnum.hour == sendCondition.getContinusCountUnit()?Calendar.HOUR_OF_DAY:Calendar.MINUTE;
				Calendar cal = Calendar.getInstance();
				cal.set(timeType, cal.get(timeType) + sendCondition.getContinusCount());
				wait.setExecTime(cal.getTime());
				alarmEventWaits.add(wait);
			}
		}
		return isNeedSend;
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????
	 * ????????????????????????????????????????????????
	 * 1.????????????????????????7*24???????????????????????????????????????????????????????????????????????????
	 * 2.???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????????????????????????????
	 * ????????????????????????????????????
	 * 1.????????????????????????7*24??????????????????????????????????????????????????????????????????????????????
	 * 2.???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????????????????????????????
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 */
	private void doLoopSendMsg(){
		final List<AlarmEventWait> alarmEventWaitList = alarmNotifyDao.findNotifyWait(new Date());
		if(alarmEventWaitList ==null || alarmEventWaitList.isEmpty()){
			return;
		}
		final List<AlarmEventWait> keepAlarmEventWaitList = new ArrayList<>(alarmEventWaitList.size()/2);
		final List<AlarmNotify> alarmNotifies = new ArrayList<>(alarmEventWaitList.size());

		for(final AlarmEventWait wait : alarmEventWaitList){
			if(logger.isDebugEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append("Starts to handle AlarmEventWait, id is ");
				sb.append(wait.getId());
				sb.append(", AlarmEventID is ");
				sb.append(wait.getEventID());
				logger.debug(sb.toString());
			}
			final AlarmNotify ntf = JSON.parseObject(wait.getContent(), AlarmNotify.class);

			ntf.setAlarmRuleID(wait.getRuleID());
			ntf.setNotifyID(this.sequence.next());
			User user = userApi.get(ntf.getNotifyUserID());
			if(null != user) {
				if(SendWayEnum.email == ntf.getNotifyType()){
					ntf.setNotifyAddr(user.getEmail());
				}else if(SendWayEnum.sms == ntf.getNotifyType()){
					ntf.setNotifyAddr(user.getMobile());
				}else if(SendWayEnum.alert == ntf.getNotifyType())
					ntf.setNotifyAddr(user.getName());
			}else{
				if(logger.isWarnEnabled()) {
					StringBuffer sb = new StringBuffer();
					sb.append("Do not find user info with ");
					sb.append(ntf.getNotifyUserID());
					sb.append(",alarmEventWait ID is ");
					sb.append(wait.getId());
				}
				continue;
			}
			AlarmSendCondition sendCondition = alarmRuleService.getAlarmSendCondition(ntf.getAlarmRuleID(), ntf.getNotifyType());
			boolean isHandle = dispatcher(sendCondition, new CallBack(){

				@Override
				public boolean handle(AlarmSendCondition sendCondition) {//????????????????????????

					//??????????????????????????????????????????
					boolean isMatched = findMatchState(ntf.getLevel(), sendCondition.getAlarmLevels());
					if(!isMatched){
						if(logger.isWarnEnabled()) {
							StringBuffer sb = new StringBuffer();
							sb.append("handleOnce:do not match alarm level, so delete AlarmEventWait with ");
							sb.append(wait.getId());
							sb.append(", AlarmEventID is ");
							sb.append(wait.getEventID());
							logger.warn(sb.toString());
						}
						alarmNotifyDao.deleteNotifyWait(wait.getId());
						return false;
					}

					boolean flag = doHandleOnce(sendCondition, ntf, wait.getId(), keepAlarmEventWaitList);
					if(flag || (!flag && !sendCondition.isSendIntime())) {
						if(logger.isDebugEnabled()) {
							StringBuffer stringBuffer = new StringBuffer();
							stringBuffer.append("doHandleOnce: delete AlarmEventWait with ");
							stringBuffer.append(wait.getId());
							stringBuffer.append(",AlarmEventID is ");
							stringBuffer.append(wait.getEventID());
							logger.debug(stringBuffer.toString());
						}
						alarmNotifyDao.deleteNotifyWait(wait.getId());
					}
					return flag;
				}

				@Override
				public boolean handleError() {
					if(logger.isWarnEnabled()) {
						StringBuffer sb = new StringBuffer();
						sb.append("Do not find sending condition {");
						sb.append(ntf.getAlarmRuleID());
						sb.append(":");
						sb.append(ntf.getNotifyType());
						sb.append("}, so delete AlarmEventWait record,");
						sb.append(wait.getId());
						logger.warn(sb.toString());
					}
					alarmNotifyDao.deleteNotifyWait(wait.getId());
					return false;
				}

				@Override
				public boolean handleContinuous(AlarmSendCondition sendCondition) {
					//??????????????????????????????????????????
					boolean isMatched = findMatchState(ntf.getLevel(), sendCondition.getAlarmLevels());
					if(!isMatched){
						if(logger.isWarnEnabled()) {
							StringBuffer sb = new StringBuffer();
							sb.append("handleContinuous???do not match alarm level, so delete AlarmEventWait with ");
							sb.append(wait.getId());
							sb.append(", AlarmEventID is ");
							sb.append(wait.getEventID());
							logger.warn(sb.toString());
						}
						alarmNotifyDao.deleteNotifyWait(wait.getId());
						return false;
					}
					boolean flag = doHandleRepeatly(sendCondition, ntf, wait.getId(), keepAlarmEventWaitList);
					if(!flag && !sendCondition.isSendIntime()) { //????????????wait???????????????
						if(logger.isDebugEnabled()) {
							StringBuffer stringBuffer = new StringBuffer();
							stringBuffer.append("doHandleRepeatly: delete AlarmEventWait with ");
							stringBuffer.append(wait.getId());
							stringBuffer.append(",AlarmEventID is ");
							stringBuffer.append(wait.getEventID());
							logger.debug(stringBuffer.toString());
						}
						alarmNotifyDao.deleteNotifyWait(wait.getId());
					}
					return flag;
				}
			});
			if(isHandle) {
				ntf.setNotifyTime(new Date());
				ntf.setContinusNum(1);
				ntf.setState(NotifyState.SENDING);
				SmsOrEmailNotifyTemplate template = null;
				if(sendCondition.getTemplateId() == NULL_TEMPLATE) {
					template = smsOrEmailNotifyTemplateService.findDefaultTemplateByType(ntf.getNotifyType().name(), ntf.getSysModuleEnum());
				}else{
					SmsOrEmailNotifyTemplate smsOrEmailNotifyTemplate = new SmsOrEmailNotifyTemplate();
					smsOrEmailNotifyTemplate.setTemplateID(sendCondition.getTemplateId());
					List<SmsOrEmailNotifyTemplate> smsOrEmailNotifyTemplates = smsOrEmailNotifyTemplateService.findNotifyTemplateByCondition(smsOrEmailNotifyTemplate);
					if(null != smsOrEmailNotifyTemplates && !smsOrEmailNotifyTemplates.isEmpty()) {
						template = smsOrEmailNotifyTemplates.get(FIRST_ENTITY);
					}
				}
				if(null != template) {
					String content = template.getContent();
					String title = template.getTitle();
					Map<String, Object> parameters = ntf.getParameters();
					if(null != parameters && !parameters.isEmpty()) {
						Set<String> keySet = parameters.keySet();
						Iterator<String> iterator = keySet.iterator();
						while (iterator.hasNext()) {
							String key = iterator.next();
							content = content.replaceAll("\\$\\{" + key + "\\}", parameters.get(key).toString());
							title = title.replaceAll("\\$\\{" + key + "\\}", parameters.get(key).toString());
						}
						ntf.setNotifyContent(content);
						ntf.setNotifyTitle(title);
					}
				}

				alarmNotifyDao.addNotify(ntf);
				alarmNotifies.add(ntf);
			}
		}

		if(!alarmNotifies.isEmpty()){
			send(alarmNotifies);
		}
		if(!keepAlarmEventWaitList.isEmpty()) {
			for(AlarmEventWait alarmEventWait : keepAlarmEventWaitList) {
				if(logger.isDebugEnabled()) {
					StringBuffer sb = new StringBuffer();
					sb.append("do loop alarm event wait, alarm event id is ");
					sb.append(alarmEventWait.getEventID());
					sb.append(", wait id is ");
					sb.append(alarmEventWait.getId());
					sb.append(", execute time is ");
					sb.append(alarmEventWait.getExecTime());
					logger.debug(sb.toString());
				}
				alarmNotifyDao.replaceNotifyWait(alarmEventWait);
			}
		}

	}

	private int getMinOfDay(){
		Calendar cal=Calendar.getInstance();
		return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
	}

	private boolean findMatchState(InstanceStateEnum state, List<AlarmLevelEnum> levels) {
		boolean isMatched = false;
		if(null != levels && !levels.isEmpty()) {
			for(AlarmLevelEnum level : levels) {
				switch(level){
					case metric_recover:
						if (InstanceStateEnum.NORMAL ==state){
							isMatched = true;
							break;
						}
					case metric_warn:
						if(InstanceStateEnum.WARN ==state){
							isMatched = true;
							break;
						}
					case metric_error:
						if(InstanceStateEnum.SERIOUS ==state){
							isMatched = true;
							break;
						}
					case down:
						if(InstanceStateEnum.CRITICAL ==state|| InstanceStateEnum.NORMAL_CRITICAL==state){
							isMatched = true;
							break;
						}
				}
			}
		}
		return isMatched;

	}
	
	private void  send(List<AlarmNotify> alarmNotifies) {
		List<AlarmNotify> smsNotify = new ArrayList<>(alarmNotifies.size()/2);
		List<AlarmNotify> emailNotify = new ArrayList<>(alarmNotifies.size()/2);
		for(AlarmNotify alarmNotify : alarmNotifies) {
			if(SendWayEnum.sms == alarmNotify.getNotifyType()){
				smsNotify.add(alarmNotify);
			}else if(SendWayEnum.email == alarmNotify.getNotifyType()){
				emailNotify.add(alarmNotify);
			}
		}
		if(!smsNotify.isEmpty()) {
			sendSms(smsNotify);
		}
		if(!emailNotify.isEmpty()){
			sendEmail(emailNotify);
		}

	}
	
	private void sendEmail(final List<AlarmNotify> notifies) {

		try {
			fixedThreadPool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    String notifyMessage = null;
                    String notifyTitle = null;
                    String[] notifyAddress = new String[notifies.size()];
                    int index = 0;
					for(AlarmNotify notifyUpdate : notifies) {
                        //notifyUpdate.setState(NotifyState.SENDING);
                        //alarmNotifyDao.update(notifyUpdate);
                        notifyMessage = notifyUpdate.getNotifyContent();
                        notifyTitle = notifyUpdate.getNotifyTitle();
                        notifyAddress[index] = notifyUpdate.getNotifyAddr();
                        index++;
                    }

                    MailSenderInfo senderInfo=new MailSenderInfo();
                    senderInfo.setContent(notifyMessage);
                    senderInfo.setSubject(notifyTitle);
                    senderInfo.setToAddress(notifyAddress);
                    boolean sendSuccess = false;
                    try {
                        if(logger.isInfoEnabled()) {
                            logger.info("Starts to send email with {" + JSONObject.toJSONString(notifyMessage) + "}. email address is "
                                    + notifyAddress);
                        }
                        sendSuccess = MailClient.sendHtmlMail(senderInfo);
                    } catch (Exception e) {
                        if(logger.isErrorEnabled())
                            logger.error(e.getMessage(),e);
                        sendSuccess = false;
                    }
                    for(AlarmNotify notifyUpdate : notifies) {
                        notifyUpdate.setState(sendSuccess ? NotifyState.SUCCESS : NotifyState.NOT_SUCCESS);
                        notifyUpdate.setNotifyTime(new Date());
                        alarmNotifyDao.update(notifyUpdate);
                    }
                    return sendSuccess;
                }
            }).get(45, TimeUnit.SECONDS);
		} catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void sendSms(final List<AlarmNotify> notifies) {
		try {
			fixedThreadPool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    String notifyMessage = null;
                    List<String> userList=new ArrayList<>();
                    for(AlarmNotify notifyUpdate : notifies) {
                        //notifyUpdate.setState(NotifyState.SENDING);
                        //alarmNotifyDao.update(notifyUpdate);
                        userList.add(notifyUpdate.getNotifyAddr());
                        if(null == notifyMessage)
                            notifyMessage = notifyUpdate.getNotifyContent();
                    }
                    ResultMessage resultMessage = null;
                    try {
                        if(logger.isInfoEnabled()) {
                            logger.info("Starts to send sms with [" + notifyMessage + "], userList is " + userList);
                        }
                        resultMessage = MessageSendHelper.sendMessage(userList, notifyMessage);
                    } catch (Exception e) {
                        if(logger.isErrorEnabled()) {
                            logger.error(e.getMessage(),e);
                        }
                    }
                    if(resultMessage !=null && resultMessage.isSuccess()) {
						for(AlarmNotify notifyUpdate : notifies) {
							notifyUpdate.setState(NotifyState.SUCCESS);
							notifyUpdate.setExtInfo(resultMessage.getMessage());
							notifyUpdate.setNotifyTime(new Date());
							alarmNotifyDao.update(notifyUpdate);
						}

					}
                    return resultMessage;
                }
            }).get(45, TimeUnit.SECONDS);
		} catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		}

	}
	
	public void startRunner(){
		Thread runner=new Thread(new Runnable() {
			@Override public void run() {
				if(logger.isInfoEnabled())
					logger.info("AlarmNotifySenderManager statrted!");
				while(true){
					try{
						lockService.sync("AlarmNotifySenderManager", new LockCallback<Integer>() {
							@Override public Integer doAction() {
								try{
									doLoopSendMsg();
								}catch(Exception e){
									logger.error(e.getMessage(),e);	
								}
								return 1;
							}
						}, 10);
						
						Thread.sleep(30*1000);
					}catch(Exception e){
						logger.error(e.getMessage(),e);	
					}
				}
			}
		 });
		 runner.setName("AlarmNotifySenderManager");
		 runner.start();
		 
		 Thread checkState=new Thread(new Runnable() {
				@Override public void run() {
					while(true){
						try{
							List<AlarmNotify> list=alarmNotifyDao.findByState(NotifyState.SENDING, NotifyTypeEnum.sms);
							if(list!=null && list.size()>0){
								for(AlarmNotify notify:list){
									if(notify.getExtInfo()==null)
										continue;
									SentRecord rst=MessageSendHelper.getSMSModemSentRecord(Long.parseLong(notify.getExtInfo()), notify.getNotifyAddr());
									if(rst!=null){
										if(SentRecord.MESSAGE_SEND_FLAG_SUCCESS==rst.getSendFlag())
											notify.setState(NotifyState.SUCCESS);
										else if(SentRecord.MESSAGE_SEND_FLAG_FAIL==rst.getSendFlag())
											notify.setState(NotifyState.NOT_SUCCESS);
										else
											return ;
										alarmNotifyDao.update(notify);
									}
								}
							}
							Thread.sleep(30*1000);
						}catch(Exception e){
							logger.error(e.getMessage(),e);	
						}
					}
				}
			 });
		 checkState.setName("AlarmNotify_SMS_check");
		 checkState.start();
	}
	
	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}

	interface CallBack{
		//???????????????
		boolean handle(AlarmSendCondition sendCondition);
		boolean handleError();
		boolean handleContinuous(AlarmSendCondition sendCondition);
	}

	@Override
	public void deleteAlarmNotifyWaitByEventId(long eventID) {
		if(logger.isInfoEnabled()) {
			logger.info("delete AlarmNotifyWait with eventID : " + eventID);
		}
		alarmNotifyDao.deleteNotifyWaitByEventId(eventID);
		
	}

	@Override
	public void deleteAlarmNotifyWaitByRecoveryKey(String s) {
		alarmNotifyDao.deleteNotifyWaitByRecoveryKey(s);
	}

	@Override
	public List<AlarmEventWait> findNotifyWaitByRecoveryKey(String recoveryKey) {
		AlarmEventWait alarmEventWait = new AlarmEventWait();
		return alarmNotifyDao.findNotifyWaitByRecoveryKey(alarmEventWait);
	}

	@Override
	public void deleteAlarmNotifyWaitById(long id) {
		alarmNotifyDao.deleteNotifyWait(id);
	}

	@Override
	public void deleteAlarmWaitByRule(String recoveryKey, long ruleId) {
		alarmNotifyDao.deleteNotifyWaitByRule(recoveryKey, ruleId);
	}

	@Override
	public void deleteAlarmNotifyWaitByInsts(boolean isParent, long instanceId) {
		Map<String, String> params = new HashMap<>(1);
		if(isParent) {
			params.put("ext8", String.valueOf(instanceId));
		}else {
			params.put("sourceId", String.valueOf(instanceId));
		}
		alarmNotifyDao.deleteNotifyWaitByParameters(params);
	}

}
