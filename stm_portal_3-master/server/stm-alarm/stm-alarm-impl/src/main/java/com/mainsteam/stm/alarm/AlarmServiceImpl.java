package  com.mainsteam.stm.alarm;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.mainsteam.stm.alarm.notify.SmsOrEmailNotifyTemplateService;
import com.mainsteam.stm.alarm.obj.*;
import com.mainsteam.stm.alarm.po.AlarmEventPO;
import com.mainsteam.stm.common.sync.DataSyncService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.notify.AlarmNotifyService;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.lock.LockCallback;
import com.mainsteam.stm.lock.LockService;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmLevelEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;

public class AlarmServiceImpl implements AlarmService,InitializingBean {
	private static final int FIRST_ENTITY = 0;
	private static final int NULL_TEMPLATE = 0;
	private Log logger=LogFactory.getLog(AlarmServiceImpl.class);

	private IUserApi userApi;
	private AlarmNotifyService alarmNotifyService;
	private AlarmEventService alarmEventService;
	private ISequence sequence;
	private AlarmRuleService alarmRuleService;
	private LockService lockService;
	private DataSyncService dataSyncService;
	private SmsOrEmailNotifyTemplateService smsOrEmailNotifyTemplateService;

	/**
	 * 通过配置文件进行设置第三方告警的策略 modify by ziw at 2017年9月28日 上午11:07:25
	 */
	private long otherAlarmProfileIdByConfig = -1;
	private String otherAlarmProfileNameByConfig = "第三方告警规则策略";
	private List<AlarmRule> otherAlarmRules;
	private ProfileService profileService;

	//private Boolean isUsedDefault = true;
	private ExecutorService workersThread = Executors.newFixedThreadPool(20, new ThreadFactory() {
		AtomicInteger index = new AtomicInteger(1);
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "alarmNotifyWorker-" + index.getAndIncrement());
		}
	});

	public void setDataSyncService(DataSyncService dataSyncService) {
		this.dataSyncService = dataSyncService;
	}

	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}
	public void setAlarmRuleService(AlarmRuleService alarmRuleService) {
		this.alarmRuleService = alarmRuleService;
	}

	public void setAlarmNotifyService(AlarmNotifyService alarmNotifyService) {
		this.alarmNotifyService = alarmNotifyService;
	}
	public void setAlarmEventService(AlarmEventService alarmEventService) {
		this.alarmEventService = alarmEventService;
	}
	public void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	public void setSmsOrEmailNotifyTemplateService(SmsOrEmailNotifyTemplateService smsOrEmailNotifyTemplateService) {
		this.smsOrEmailNotifyTemplateService = smsOrEmailNotifyTemplateService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public boolean handleSyncData(){
		// malachi alarm分布式锁
		// TODO: 2018/1/26 分布式锁使用方式应该简化，不能长时间持有锁 
		List<DataSyncPO> result = lockService.sync("AlarmSync", new LockCallback<List<DataSyncPO>>() {
			@Override public List<DataSyncPO> doAction() {

				List<DataSyncPO> list = new ArrayList<DataSyncPO>(200);
					try{
						list = dataSyncService.selectBatch(DataSyncTypeEnum.ALARM,0, 200);
						if(list !=null && !list.isEmpty()){
							Set<Long> keySet = new HashSet<>(200);
							for(DataSyncPO po : list){
								try{
									AlarmSenderParamter parameter = JSON.parseObject(po.getData(), AlarmSenderParamter.class);
									AlarmServiceImpl.this.notify(parameter);
									keySet.add(po.getId());
								}catch(Exception e){
									if(logger.isErrorEnabled())
										logger.error(e.getMessage(), e);
									if(e instanceof RuntimeException) {
										keySet.add(po.getId());
									}
								}
							}
							list.clear();
							List<Long> keyList = new ArrayList<>(keySet.size());
							keyList.addAll(keySet);
							dataSyncService.delete(keyList);

						}

					}catch(Throwable t){
						if(logger.isErrorEnabled()) {
							logger.error("Alarm DataSync error." + t.getMessage(), t);
						}
					}
					return list;
				}
		}, 10);

		if(null == result || result.isEmpty()) {
			return true;
		}else
			return false;
	}

	// malachi告警产生
	//这里需要解决如果产生告警记录以后，后续行为报错的话，可能会导致重复告警的问题;通过捕获RuntimeException来处理删除的操作
	@Override
	public void notify(final AlarmSenderParamter parameter) throws RuntimeException {
		if(logger.isDebugEnabled()){
			logger.debug("AlarmService receive AlarmSenderParamter " + JSON.toJSONString(parameter));
		}
		final AlarmEvent alarmEvent = convert(parameter);
		//添加告警信息流程
		if(!alarmEventService.addAlarmEvent(alarmEvent))
			return;

		Future<?> future = workersThread.submit(new Runnable() {
			@Override
			public void run() {
				List<AlarmRule> rules = null;
				if(parameter.getNotifyRules() != null){
					rules = parameter.getNotifyRules();
				}else if(parameter.getSysID() == SysModuleEnum.LINK){
					rules = alarmRuleService.getAllAlarmRules(AlarmRuleProfileEnum.link);
				}else if (parameter.getSysID() == SysModuleEnum.OTHER) {
					rules = getOtherAlarmRules();
				}else if(parameter.getRuleType() != null){
					long profileId = parameter.getProfileID();
					if(0L != profileId) {
						rules = alarmRuleService.getAlarmRulesByProfileId(profileId, parameter.getRuleType());
					}
				}
				if(logger.isDebugEnabled()){
					logger.debug("find AlarmRule:" + JSON.toJSONString(rules));
				}
				//无告警规则，不发送告警通知
				if(rules == null || rules.isEmpty()){
					return;
				}
				//短信，邮件群发
				addNotifyCondition(rules, alarmEvent);
			}
		});
		try {
			future.get(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage() + "," + alarmEvent, e);
			}
			throw new RuntimeException(e.getMessage(), e);
		} catch (ExecutionException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + "," + alarmEvent, e);
			}
			throw new RuntimeException(e.getMessage(), e);
		} catch (TimeoutException e) {
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage() + "," + alarmEvent, e);
			}
			throw new RuntimeException(e.getMessage(), e);
		} catch (Throwable throwable) {
			throw new RuntimeException("alarm notify deal failed:" + alarmEvent, throwable.getCause());
		}

	}

	/**
	 * 生成告警规则及其告警通知内容
	 * @param rules
	 * @param alarmEvent
	 */
	private void addNotifyCondition(List<AlarmRule> rules, AlarmEvent alarmEvent) {
		/*
			有一种情况需要考虑：当告警规则勾选了每隔一段时间告警直到告警确认，但是告警恢复却没有勾选通知，这时需要再这里把待发送通知的告警删除，
			否则告警恢复之后还会不断发故障告警；或者同一个指标或者资源告警出现覆盖时也需要处理
		*/
		//短信，邮件群发
		Map<SendWayEnum, List<NotifyCondition>> notifyConditionMap = new HashMap<>(2);
		Set<Long> deleteAlarmWaitSet = new HashSet<>(rules.size());
		for(SendWayEnum sendWayEnum : SendWayEnum.values()) {
			notifyConditionMap.put(sendWayEnum, new ArrayList<NotifyCondition>(1));
		}
		List<AlarmNotify> alarmNotifyList = new ArrayList<>(rules.size()*2);
		boolean isMissing = true;
		for(AlarmRule alarmRule : rules) {
			if(null != alarmRule.getSendConditions()) {
				isMissing = false;
				for(AlarmSendCondition condition : alarmRule.getSendConditions()) {
					if(condition.isEnabled()) {
						if(containInstanceState(condition.getAlarmLevels(), alarmEvent.getLevel(), alarmEvent.getRecoverKey(), alarmEvent.getSysID())) {
							AlarmNotify alarmNotify = createAlarmNotify(alarmEvent, condition);
							if(!findUser(alarmNotify, Long.parseLong(alarmRule.getUserId()))){
								logger.warn("can't find user [" + alarmRule.getUserId() + "] with alarm rule(ruleId/eventId/conditionId):"
										+ condition.getRuleId() + alarmEvent.getEventID() + "/" + condition.getID());
								continue;
							}
							findAlarmNotifyTemplate(alarmNotify, condition.getTemplateId(), alarmEvent);
							alarmNotifyList.add(alarmNotify);
						}else {
							if(condition.isContinus()) {
								if(logger.isInfoEnabled()) {
									StringBuffer stringBuffer = new StringBuffer();
									stringBuffer.append("alarm wait should be deleted,cause not match state and continuous alarm.");
									stringBuffer.append("alarm recovery key is ").append(alarmEvent.getRecoverKey());
									stringBuffer.append(", alarm rule is ").append(alarmRule.getId());
									logger.info(stringBuffer.toString());
								}
								deleteAlarmWaitSet.add(alarmRule.getId());
							}
						}

					}
				}
			}
		}
		if(isMissing) {
			if(logger.isInfoEnabled()) {
				logger.info("don't alarm cause missing send condition :" + alarmEvent.getEventID() + "/" + alarmEvent.getContent());
			}
			return;
		}

		if(!deleteAlarmWaitSet.isEmpty()) {
			Iterator<Long> iterator = deleteAlarmWaitSet.iterator();
			while(iterator.hasNext()) {
				Long ruleId = iterator.next();
				alarmNotifyService.deleteAlarmWaitByRule(alarmEvent.getRecoverKey(), ruleId);
			}
		}

		if(!alarmNotifyList.isEmpty()) {
			if(logger.isDebugEnabled()) {
				logger.debug("Starts to send email or sms with alarm " + alarmEvent.getEventID());
			}
			try{
				alarmNotifyService.addNotify(alarmNotifyList, alarmEvent.getRecoverKey(), alarmEvent.getEventID());
			}catch(Exception e){
				if(logger.isWarnEnabled())
					logger.warn("exception for alarm:"+JSON.toJSONString(alarmEvent));

				logger.error(e.getMessage(),e);
			}

		}
	}

	private AlarmNotify createAlarmNotify(AlarmEvent alarmEvent, AlarmSendCondition sendCondition) {

		AlarmNotify ntf = new AlarmNotify();
		ntf.setAlarmID(alarmEvent.getEventID());
		ntf.setAlarmRuleID(sendCondition.getRuleId());
		ntf.setNotifyType(sendCondition.getSendWay());
		ntf.setLevel(alarmEvent.getLevel());
		ntf.setRecoverKey(alarmEvent.getRecoverKey());
		ntf.setSysModuleEnum(alarmEvent.getSysID());

		ntf.setSourceType(alarmEvent.getSysID().name());
		ntf.setNotifyTime(new Date());
		ntf.setAlarmSendCondition(sendCondition);

		return ntf;
	}

	private boolean findUser(AlarmNotify notify, long userId) {
		User user= userApi.get(userId);
		if(user==null){
			logger.error("alarm rule user["+ userId+"] not found, please check!");
			return false;
		}
		SendWayEnum sendWayEnum = notify.getNotifyType();
		if(SendWayEnum.email == sendWayEnum){
			notify.setNotifyAddr(user.getEmail());
		}else if(SendWayEnum.sms == sendWayEnum){
			notify.setNotifyAddr(user.getMobile());
		}else if(SendWayEnum.alert == sendWayEnum)
			notify.setNotifyAddr(user.getName());
		notify.setNotifyUserID(userId);
		return true;
	}

	private void findAlarmNotifyTemplate(AlarmNotify notify,  long templateId, AlarmEvent alarmEvent) {
		boolean isAlert = notify.getNotifyType() == SendWayEnum.alert ? true : false;

		if(!isAlert &&
				(alarmEvent.getSysID() == SysModuleEnum.MONITOR || alarmEvent.getSysID() == SysModuleEnum.BUSSINESS)) {
			SmsOrEmailNotifyTemplate template = null;
			if(templateId == NULL_TEMPLATE) {
				template = smsOrEmailNotifyTemplateService.findDefaultTemplateByType(notify.getNotifyType().name(), alarmEvent.getSysID());
			}else{
				SmsOrEmailNotifyTemplate smsOrEmailNotifyTemplate = new SmsOrEmailNotifyTemplate();
				smsOrEmailNotifyTemplate.setTemplateID(templateId);
				smsOrEmailNotifyTemplate.setSysModuleEnum(alarmEvent.getSysID());
				List<SmsOrEmailNotifyTemplate> smsOrEmailNotifyTemplates = smsOrEmailNotifyTemplateService.findNotifyTemplateByCondition(smsOrEmailNotifyTemplate);
				if(null != smsOrEmailNotifyTemplates && !smsOrEmailNotifyTemplates.isEmpty()) {
					template = smsOrEmailNotifyTemplates.get(FIRST_ENTITY);
				}
			}
			if(null != template) {
				String title = template.getTitle();
				String content = template.getContent();
				if(StringUtils.isNotBlank(title) || StringUtils.isNotBlank(content)) {
					SmsOrEmailNotifyTemplateEnum[] smsOrEmailNotifyTemplateEna = smsOrEmailNotifyTemplateService.findNotifyTemplateParameters();
					Map<String, Object> parameters = new HashMap<>(smsOrEmailNotifyTemplateEna.length); //用于变量保存，在循环发送通知时可能用得到
					for(SmsOrEmailNotifyTemplateEnum smsOrEmailNotifyTemplateEnum : smsOrEmailNotifyTemplateEna) {
						char[] methodChars = smsOrEmailNotifyTemplateEnum.getKey().toCharArray();
						methodChars[0] -=32;
						String getter = "get" + String.valueOf(methodChars);
						try {
							Method getMethod = alarmEvent.getClass().getMethod(getter, new Class[]{});
							Object value = getMethod.invoke(alarmEvent, new Object[]{});
							if(null != value){
								String result = null;
								if(smsOrEmailNotifyTemplateEnum == SmsOrEmailNotifyTemplateEnum.COLLECTION_TIME){
									DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									result = dateFormat.format((Date)value);
								}else if(smsOrEmailNotifyTemplateEnum == SmsOrEmailNotifyTemplateEnum.ALARM_STATE) {
									InstanceStateEnum tmpVar = InstanceStateEnum.valueOf(value.toString());
									result = InstanceStateEnum.getValue(tmpVar);
								} else
									result = value.toString();
								if(StringUtils.isNotBlank(content))
									content = content.replaceAll("\\$\\{" + smsOrEmailNotifyTemplateEnum.getKey() + "\\}", result);
								if(StringUtils.isNotBlank(title))
									title = title.replaceAll("\\$\\{" + smsOrEmailNotifyTemplateEnum.getKey() + "\\}", result);
								parameters.put(smsOrEmailNotifyTemplateEnum.getKey(), result);
							}
						} catch (Exception e) {
							if(logger.isWarnEnabled()) {
								logger.warn(e.getMessage(), e);
							}
							continue;
						}

					}

					notify.setParameters(parameters);
					if(StringUtils.isNotBlank(content))
						notify.setNotifyContent(content);
					if(StringUtils.isNotBlank(title))
						notify.setNotifyTitle(title);
				}
			}
		}
		if(StringUtils.isBlank(notify.getNotifyContent()) || StringUtils.isBlank(notify.getNotifyTitle())) {
			StringBuffer stringBuffer = new StringBuffer();
			if(StringUtils.isNotBlank(alarmEvent.getSourceIP())) {
				stringBuffer.append("【").append(alarmEvent.getSourceIP()).append("】");
			}
			if(StringUtils.isNotBlank(alarmEvent.getSourceName()))
				stringBuffer.append("【").append(alarmEvent.getSourceName()).append("】");
			stringBuffer.append(alarmEvent.getContent());
			if(StringUtils.isBlank(notify.getNotifyContent()))
				notify.setNotifyContent(stringBuffer.toString());
			if(StringUtils.isBlank(notify.getNotifyTitle()))
				notify.setNotifyTitle(stringBuffer.toString());
		}
	}

	public void setUserApi(IUserApi userApi) {
		this.userApi = userApi;
	}
	private boolean  containInstanceState(List<AlarmLevelEnum> alarmleveles, InstanceStateEnum state, String recoveryKey, SysModuleEnum sysModuleEnum){
		if(alarmleveles!=null){
			for(AlarmLevelEnum level:alarmleveles){
				if(findMatchState(state, level, recoveryKey, sysModuleEnum)){
					return true;
				}
			}
		}
		return false;
	}

	private boolean findMatchState(InstanceStateEnum state, AlarmLevelEnum level, String recovery, SysModuleEnum sysModuleEnum) {
		switch(level){
			case metric_unkwon:
				return (InstanceStateEnum.UNKOWN==state || InstanceStateEnum.NORMAL_UNKNOWN==state)?true:false;
			case metric_recover:
				if(sysModuleEnum == SysModuleEnum.MONITOR) {
					if(StringUtils.startsWith(recovery, "instanceState_")){
						return (InstanceStateEnum.NORMAL ==state)?true:false;
					}else{
						return false;
					}
				}else
					return (InstanceStateEnum.NORMAL ==state)?true:false;
			case perf_metric_recover:
				if(sysModuleEnum == SysModuleEnum.MONITOR) {
					if(!StringUtils.startsWith(recovery, "instanceState_")){
						return (InstanceStateEnum.NORMAL ==state)?true:false;
					}else{
						return false;
					}
				}else {
					return (InstanceStateEnum.NORMAL ==state)?true:false;
				}
			case metric_warn:
				return (InstanceStateEnum.WARN ==state)?true:false;
			case metric_error:
				return (InstanceStateEnum.SERIOUS ==state)?true:false;
			case down:
				return (InstanceStateEnum.CRITICAL ==state|| InstanceStateEnum.NORMAL_CRITICAL==state)?true:false;
			default:
				return false;
		}
	}

	private AlarmEvent convert(AlarmSenderParamter param) {
		AlarmEvent event = new AlarmEventPO();
		event.setCollectionTime(param.getGenerateTime());
		event.setContent(param.getDefaultMsg());
		event.setEventID(sequence.next());
		event.setLevel(param.getLevel());
		event.setSourceID(param.getSourceID());
		event.setSourceIP(param.getSourceIP());
		event.setSourceName(param.getSourceName());
		event.setProvider(param.getProvider()!=null?param.getProvider():AlarmProviderEnum.OC4);
		event.setSysID(param.getSysID());

		if(param.getRecoverKeyValue()!=null){
			event.setRecoverKey(StringUtils.join(param.getRecoverKeyValue(),"_"));
		}

		if(InstanceStateEnum.NORMAL.equals(param.getLevel())){
			event.setRecovered(true);
			event.setRecoveryEvent(true);
			event.setRecoveryEventID(event.getEventID());
			event.setRecoveryTime(event.getCollectionTime());
		}

		event.setExt0(param.getExt0());
		event.setExt1(param.getExt1());
		event.setExt2(param.getExt2());
		event.setExt3(param.getExt3());
		event.setExt4(param.getExt4());
		event.setExt5(param.getExt5());
		event.setExt6(param.getExt6());
		event.setExt7(param.getExt7());
		if(param.getSysID() != SysModuleEnum.MONITOR)
			event.setExt8(param.getSourceID());
		else
			event.setExt8(param.getExt8());
		event.setExt9(param.getExt9());

		return event;
	}

	private List<AlarmRule> getOtherAlarmRules() {
		return otherAlarmRules;
	}

	private void loadOtherAlarmRules() throws ProfilelibException {
		List<ProfileInfo> profileInfos = profileService.getParentProfileBasicInfo();
		if (profileInfos != null) {
			for (ProfileInfo profileInfo : profileInfos) {
				if (profileInfo.getProfileName().equals(otherAlarmProfileNameByConfig)) {
					// no
					otherAlarmRules = alarmRuleService.getAlarmRulesByProfileId(profileInfo.getProfileId(),
							AlarmRuleProfileEnum.model_profile);
					if (logger.isDebugEnabled()) {
						logger.debug("loadOtherAlarmRules profileId=" + profileInfo.getProfileId() + " profileName="
								+ profileInfo.getProfileName());
					}
					return;
				}
			}
		}
		if (otherAlarmProfileIdByConfig > 0) {
			try {
				otherAlarmRules = alarmRuleService.getAlarmRulesByProfileId(
						Long.valueOf(this.otherAlarmProfileIdByConfig), AlarmRuleProfileEnum.model_profile);
				if (logger.isDebugEnabled()) {
					logger.debug("loadOtherAlarmRules profileId=" + otherAlarmProfileIdByConfig);
				}
			} catch (Throwable throwable) {
				logger.warn("other alarm event found strategy error, " + throwable.getMessage(), throwable);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Thread th=new Thread(new Runnable(){
			@Override public void run() {
				try{
					while(true) {
						boolean flag = handleSyncData();
						if(flag) {
							Thread.sleep(1000*10);
						}
					}
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		});
		th.setName("AlarmSyncHandler");
		th.start();
		if(logger.isInfoEnabled())
			logger.info("thread[AlarmSyncHandler] has start!");

		//加载第三方告警策略
		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("/properties/sms.properties");
		if (inputStream != null) {
			try {
				properties.load(inputStream);
				String value = properties.getProperty("otherAlarmProfileId");
				if (NumberUtils.isNumber(value)) {
					this.otherAlarmProfileIdByConfig = Long.valueOf(value);
				}
				this.otherAlarmProfileNameByConfig = properties.getProperty("otherAlarmProfileName", "第三方告警规则策略");
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
			} finally {
				inputStream.close();
			}
		}
		if (this.otherAlarmProfileIdByConfig <= 0) {
			logger.warn("Can not find other alarm event strategy id in [properties/sms.properties]");
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						loadOtherAlarmRules();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
				}
			}
		}, "otherAlarmRulesFreshWorker").start();

	}
}
