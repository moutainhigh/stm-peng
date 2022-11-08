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
		List<AlarmNotify> immediateNotifies = new ArrayList<>(notifies.size()/2); //立即执行的告警通知
		final List<AlarmEventWait> alarmEventWaitList = new ArrayList<>(notifies.size()/2); //告警等待表
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
				public boolean handle(AlarmSendCondition sendCondition) {//只发送告警通知一次
					return doHandleOnce(sendCondition, alarmNotify, alarmNotifyId, alarmEventWaitList);
				}

				@Override public boolean handleError() {return false;}

				@Override
				public boolean handleContinuous(AlarmSendCondition sendCondition) {//每隔一段时间循环发送告警通知
					return doHandleRepeatly(sendCondition, alarmNotify, alarmNotifyId, alarmEventWaitList);
				}

			});

			//满足发送即时通知条件
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
	 * 计算当前时间是否满足发送告警通知时间段
	 * @param sendCondition
	 * @return
     */
	private boolean computeSendPeriods(final AlarmSendCondition sendCondition) {

		final int currentMinutes = getMinOfDay();
		boolean isNeedSend = false;
		List<AlarmNotifyPeriodForDay> dayPeriods = sendCondition.getDayPeriodes(); //按天分时段发送
		List<AlarmNotifyPeriodForWeek> weekPeriods = sendCondition.getWeekPeriodes(); //按周分时段发送

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
	 * 根据不同的告警通知发送规则和发送发现调用不同的处理回调方法
	 * @param sendCondition
	 * @param call
     * @return
     */
	private boolean dispatcher(AlarmSendCondition sendCondition, CallBack call){
		if(null==sendCondition){
			return call.handleError();
		}
		if(sendCondition.isEnabled()) {
			if(sendCondition.isContinus()) {//处理连续发送告警通知
				return call.handleContinuous(sendCondition);
			}else {
				return call.handle(sendCondition);//只发送一次告警通知
			}
		}else {
			return false;
		}
	}

	/**
	 * 发送告警通知一次相关逻辑处理
	 * @param sendCondition
	 * @param alarmNotify
	 * @param alarmEventWaitId
	 * @param alarmEventWaits
     * @return
     */
	private boolean doHandleOnce(final AlarmSendCondition sendCondition, final AlarmNotify alarmNotify,
										final long alarmEventWaitId, final List<AlarmEventWait> alarmEventWaits) {
		boolean isNeedSend = false;//标志是否立即发送，true表示即时发送
		boolean isNeedPutIntoWait = false;//标志是否需要插入告警通知等代表，true表示需要
		if(sendCondition.isAllTime())
			isNeedSend = true;
		else {
			isNeedSend = computeSendPeriods(sendCondition);
			if(sendCondition.isSendIntime() && !isNeedSend) {//发送非告警时间段的告警
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
		boolean isNeedSend = false; //标志是否符合立即发送，true表示符合，false表示不符合
		boolean isPutIntoWait = false; //标志是否加入告警通知等待表，true表示需要
		if(sendCondition.isAllTime()) {//7*24小时全时段发送
			isPutIntoWait = true;
			isNeedSend = true;
		}else {
			/*
			分时段发送告警通知，
			1.如果勾选了“发送非告警时段产生的告警”，如果满足，则需要加入告警通知等待表，循环发送通知, 否则不加入等待表；
			2.其次，则需要判断当前时间是否满足发送时段，如果满足则立即发送通知并且加入等代表，否则废弃当前操作。
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
			if(alarmNotify.getLevel() != InstanceStateEnum.NORMAL){//只有故障告警才会循环发送通知，恢复告警只通知一次
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
	 * 循环处理告警通知等待表中的数据，处理逻辑如下描述：
	 * （一）对于只发送一次通知的告警：
	 * 1.如果发送时间段为7*24全时段发送，则立即发送，并且删除等待表中当前记录；
	 * 2.如果发送时间段为分时段发送，满足发送时间段，则立即发送，并且删除等待表中对应数据；否则判断是否勾选“发送非告警时间段产生的告警”，
	 * 如果勾选，则更新等待表中对应数据，否则删除当前数据。
	 * （二）循环发送告警通知：
	 * 1.如果发送时间段为7*24全时段发送，则立即发送，并且更新等待表中的对应记录；
	 * 2.如果发送时间段为分时段发送，满足发送时间段，则立即发送，并且更新等待表中对应数据；否则判断是否勾选“发送非告警时间段产生的告警”，
	 * 如果勾选，则更新等待表中对应数据，否则删除当前数据。
	 * （三）需要注意当前等待表中的告警记录的告警级别是否符合策略中设置的告警级别
	 * （四）需要注意告警通知相关规则变更的情况，例如发送联系人新增或删除，发送地址变更等等，处理规则如下：
	 * 如果是新增联系人，当前已经存在的告警通知将无法发送，删除联系人需要将对应的等待表中的数据删除掉，至于变化联系人信息需要通过实时获取联系人信息解决。
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
				public boolean handle(AlarmSendCondition sendCondition) {//处理单条发送记录

					//需要判断是否满足发送告警级别
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
					//需要判断是否满足发送告警级别
					boolean isMatched = findMatchState(ntf.getLevel(), sendCondition.getAlarmLevels());
					if(!isMatched){
						if(logger.isWarnEnabled()) {
							StringBuffer sb = new StringBuffer();
							sb.append("handleContinuous：do not match alarm level, so delete AlarmEventWait with ");
							sb.append(wait.getId());
							sb.append(", AlarmEventID is ");
							sb.append(wait.getEventID());
							logger.warn(sb.toString());
						}
						alarmNotifyDao.deleteNotifyWait(wait.getId());
						return false;
					}
					boolean flag = doHandleRepeatly(sendCondition, ntf, wait.getId(), keepAlarmEventWaitList);
					if(!flag && !sendCondition.isSendIntime()) { //需要删除wait表当前记录
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
		//分时段发送
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
