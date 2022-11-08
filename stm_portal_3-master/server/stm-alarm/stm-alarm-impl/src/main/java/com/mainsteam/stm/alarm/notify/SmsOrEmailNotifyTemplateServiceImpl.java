package com.mainsteam.stm.alarm.notify;

import java.util.*;

import com.mainsteam.stm.alarm.notify.dao.SmsOrEmailNotifyTemplateDAO;
import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplate;
import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplateEnum;
import com.mainsteam.stm.alarm.obj.NotifyTypeEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

public class SmsOrEmailNotifyTemplateServiceImpl implements SmsOrEmailNotifyTemplateService {

	private static final int DEFAULT_TEMPLATE_SIZE = 1;
	private SmsOrEmailNotifyTemplateDAO smsOrEmailNotifyTemplateDao;
	private ISequence sequence;
	private AlarmRuleService alarmRuleService;

	private Log logger = LogFactory.getLog(SmsOrEmailNotifyTemplateServiceImpl.class);
	
	public void setSmsOrEmailNotifyTemplateDao(SmsOrEmailNotifyTemplateDAO smsOrEmailNotifyTemplateDao) {
		this.smsOrEmailNotifyTemplateDao = smsOrEmailNotifyTemplateDao;
	}

	public void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	public void setAlarmRuleService(AlarmRuleService alarmRuleService) {
		this.alarmRuleService = alarmRuleService;
	}

	/**
	 * 根据查询条件（模板ID，模板类型，创建用户，模块类型）查找告警通知模板，如果需要查询查询所有模板，则对象属性不设置值即可。
	 * @param template
	 * @return
     */
	@Override
	public List<SmsOrEmailNotifyTemplate> findNotifyTemplateByCondition(SmsOrEmailNotifyTemplate template) {
		if(logger.isDebugEnabled()) {
			logger.debug("find SmsOrEmailNotifyTemplate collection with " + template.toString());
		}
		if(template.getTemplateID() > 0) {
			//如果有设置模板ID，则先查询缓存
			SmsOrEmailNotifyTemplate smsOrEmailNotifyTemplate = SmsOrEmailNotifyTemplateUtil.queryCacheById(template.getTemplateID());
			if(null != smsOrEmailNotifyTemplate){
				List<SmsOrEmailNotifyTemplate> result = new ArrayList<>(1);
				result.add(smsOrEmailNotifyTemplate);
				return result;
			}
		}
		List<SmsOrEmailNotifyTemplate> smsOrEmailNotifyTemplateList = smsOrEmailNotifyTemplateDao.findTemplateByCondition(template);
		Map<String, List<SmsOrEmailNotifyTemplate>> notifyTypeEnumMap = new HashMap<String, List<SmsOrEmailNotifyTemplate>>(2);
		if(null != smsOrEmailNotifyTemplateList && !smsOrEmailNotifyTemplateList.isEmpty()) {
			if (logger.isInfoEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append("find SmsOrEmailNotifyTemplate size ");
				sb.append(smsOrEmailNotifyTemplateList.size());
				sb.append(" with parameters ");
				sb.append(template);
				logger.info(sb.toString());
			}
			Iterator<SmsOrEmailNotifyTemplate> iterator = smsOrEmailNotifyTemplateList.iterator();
			while (iterator.hasNext()) {
				SmsOrEmailNotifyTemplate entity = iterator.next();
				if(entity.isDefaultTemplate()) {
					if(notifyTypeEnumMap.keySet().contains(entity.getTemplateType())){
						notifyTypeEnumMap.get(entity.getTemplateType().name()+entity.getSysModuleEnum().name()).add(entity);
					}else{
						List<SmsOrEmailNotifyTemplate> list = new ArrayList<SmsOrEmailNotifyTemplate>(2);
						list.add(entity);
						notifyTypeEnumMap.put(entity.getTemplateType().name() + entity.getSysModuleEnum().name(), list);
					}
					iterator.remove();
				}else {
					//放入缓存
					SmsOrEmailNotifyTemplate smsOrEmailNotifyTemplate = SmsOrEmailNotifyTemplateUtil.queryCacheById(entity.getTemplateID());
					if(null == smsOrEmailNotifyTemplate)
						SmsOrEmailNotifyTemplateUtil.saveCacheById(template);
				}
			}
		}else {
			if(logger.isWarnEnabled()) {
				logger.warn("Can not find any SmsOrEmailNotifyTemplate with parameters " + template.toString());
			}
		}
		if(smsOrEmailNotifyTemplateList.isEmpty() && notifyTypeEnumMap.isEmpty()) {
			if(logger.isWarnEnabled()) {
				logger.warn("Deal SmsOrEmailNotifyTemplate collection error. " + template.toString());
			}
			return smsOrEmailNotifyTemplateList;
		}
		if(!notifyTypeEnumMap.isEmpty() && !notifyTypeEnumMap.keySet().isEmpty()) {
			Iterator<String> iterator = notifyTypeEnumMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				SmsOrEmailNotifyTemplate max = Collections.max(notifyTypeEnumMap.get(key));
				smsOrEmailNotifyTemplateList.add(max);
				SmsOrEmailNotifyTemplate smsOrEmailNotifyTemplate = SmsOrEmailNotifyTemplateUtil.queryCacheById(max.getTemplateID());
				if(null == smsOrEmailNotifyTemplate){
					SmsOrEmailNotifyTemplateUtil.saveCacheById(max);
				}
			}
		}
		return smsOrEmailNotifyTemplateList;
	}

	/**
	 * 新增告警通知，该参数对象的所有属性（除模板ID以外）都不能为空，否则失败
	 * @param tmp
	 * @return true 表示新增成功，false表示失败
     */
	@Override
	public boolean addNotifyTemplate(SmsOrEmailNotifyTemplate tmp) {
		tmp.setTemplateID(sequence.next());
		boolean flag = smsOrEmailNotifyTemplateDao.addTemplate(tmp);
		SmsOrEmailNotifyTemplateUtil.saveCacheById(tmp);
		return flag;
	}

	/**
	 * 根据模板ID批量删除模板
	 * @param templateIDs
	 * @param sysModuleEnum
	 * @return true表示删除成功，false表示删除失败
     */
	@Override
	public boolean deleteNotifyTemplate(List<Long> templateIDs, SysModuleEnum sysModuleEnum) {
		for(Long templateId : templateIDs) {//更新绑定模板
			List<AlarmSendCondition> alarmSendConditionList = alarmRuleService.getAlarmSendConditionByTemplateId(templateId, null);
			if(null != alarmSendConditionList && !alarmSendConditionList.isEmpty()) {
				for(AlarmSendCondition alarmSendCondition : alarmSendConditionList) {
					SmsOrEmailNotifyTemplate defaultTemplate = findDefaultTemplateByType(alarmSendCondition.getSendWay().name(), sysModuleEnum);
					alarmSendCondition.setTemplateId(defaultTemplate.getTemplateID());
					if(logger.isInfoEnabled()) {
						StringBuffer sb = new StringBuffer();
						sb.append("update alarm send condition while deleting template.pre template id is ");
						sb.append(templateId);
						sb.append(", reset default template is ");
						sb.append(defaultTemplate.getTemplateID());
						logger.info(sb.toString());
					}
					alarmRuleService.updateAlarmConditon(alarmSendCondition.getRuleId(), alarmSendCondition);
				}

			}
		}
		return smsOrEmailNotifyTemplateDao.deleteTemplate(templateIDs);
	}

	/**
	 * 根据模板ID更新模板，只能更改模板名称，模板内容，更新时间，其他属性确保不变，尤其是模板ID
	 * @param tmp 如果编辑的是默认模板，isDefaultTemplate务必设置true，否则按照更新自定义模板处理
	 * @return true表示更新成功，false表示更新失败
     */
	@Override
	public boolean updateNotifyTemplate(SmsOrEmailNotifyTemplate tmp) {
		if(tmp.isDefaultTemplate()) { //如果是编辑默认模板，则相当于新增一条默认模板，只是ID和update都需要改变
			SmsOrEmailNotifyTemplate defaultSmsOrEmailNotifyTemplate = new SmsOrEmailNotifyTemplate();
			defaultSmsOrEmailNotifyTemplate.setTemplateType(tmp.getTemplateType());
			defaultSmsOrEmailNotifyTemplate.setSysModuleEnum(tmp.getSysModuleEnum());
			List<SmsOrEmailNotifyTemplate> result = smsOrEmailNotifyTemplateDao.findDefaultTemplate(defaultSmsOrEmailNotifyTemplate);
			if(logger.isInfoEnabled()) {
				logger.info("Update alarmNotifyTemplate with " + tmp);
			}
			if(null != result && result.size() > DEFAULT_TEMPLATE_SIZE) {
				SmsOrEmailNotifyTemplate newDefaultTemplate = Collections.max(result);
				newDefaultTemplate.setUpdateTime(new Date());
				newDefaultTemplate.setTemplateName(tmp.getTemplateName());
				newDefaultTemplate.setContent(tmp.getContent());
				newDefaultTemplate.setTitle(tmp.getTitle());
				if(logger.isInfoEnabled()) {
					logger.info("update default alarmNotifyTemplate with " + newDefaultTemplate);
				}
				SmsOrEmailNotifyTemplateUtil.saveCacheById(newDefaultTemplate);
				SmsOrEmailNotifyTemplateUtil.saveDefaultTemplateByType(newDefaultTemplate);
				return smsOrEmailNotifyTemplateDao.updateTemplate(newDefaultTemplate);
			}else {
				SmsOrEmailNotifyTemplateUtil.removeCacheById(tmp.getTemplateID());
				SmsOrEmailNotifyTemplateUtil.removeDefaultTemplateByType(tmp.getTemplateType(), tmp.getSysModuleEnum());
				SmsOrEmailNotifyTemplate newDefaultTemplate = new SmsOrEmailNotifyTemplate();
				BeanUtils.copyProperties(tmp, newDefaultTemplate);
				newDefaultTemplate.setTemplateID(sequence.next());
				newDefaultTemplate.setUpdateTime(new Date());
				if(logger.isInfoEnabled()) {
					logger.info("Add a new default alarmNotifyTemplate with " + newDefaultTemplate);
				}
				boolean flag = smsOrEmailNotifyTemplateDao.addTemplate(newDefaultTemplate);

				if(flag) {//需要更新告警规则中已引用的告警模板ID，因为编辑默认模板实际上是新增了一条模板信息，所以告警规则中的模板ID必须更新为最新的模板ID
					SmsOrEmailNotifyTemplateUtil.saveDefaultTemplateByType(newDefaultTemplate);
					SmsOrEmailNotifyTemplateUtil.saveCacheById(newDefaultTemplate);
					List<AlarmSendCondition> alarmSendConditionList = alarmRuleService.getAlarmSendConditionByTemplateId(-1L,
							SendWayEnum.valueOf(tmp.getTemplateType().name()));
					if (null != alarmSendConditionList && !alarmSendConditionList.isEmpty()) {
						StringBuffer sb = new StringBuffer();
						sb.append("update alarm rule condition template id ");

						for(AlarmSendCondition alarmSendCondition : alarmSendConditionList) {
							if(alarmSendCondition.getTemplateId() == tmp.getTemplateID()) {
								alarmSendCondition.setTemplateId(newDefaultTemplate.getTemplateID());
								alarmRuleService.updateAlarmConditon(alarmSendCondition.getRuleId(), alarmSendCondition);

								sb.append(",rule id is ").append(alarmSendCondition.getRuleId());
								sb.append("{").append(tmp.getTemplateID()).append(":").append(newDefaultTemplate.getTemplateID());
								sb.append("}");
							}
						}
						if(logger.isInfoEnabled()) {
							logger.info(sb.toString());
						}
					}
				}
				return flag;
			}
		}else{
			boolean flag = smsOrEmailNotifyTemplateDao.updateTemplate(tmp);
			if(flag) {
				SmsOrEmailNotifyTemplateUtil.saveCacheById(tmp);
			}
			return flag;
		}
	}

	/**
	 * 返回所有告警模板需要的参数
	 * @return
     */
	@Override
	public SmsOrEmailNotifyTemplateEnum[] findNotifyTemplateParameters() {
		return SmsOrEmailNotifyTemplateEnum.values();
	}

	/**
	 * 重置告警模板
	 * @param notifyTypeEnum 默认模板类型，NotifyTypeEnum.sms表示短信，NotifyTypeEnum.email表示邮件，NotifyTypeEnum.alert不支持
     * @return 返回初始化默认模板,null表示无需重置，其它返回初始模板实体对象
     */
	@Override
	public SmsOrEmailNotifyTemplate resetDefaultNotifyTemplate(NotifyTypeEnum notifyTypeEnum, SysModuleEnum sysModuleEnum) {
		SmsOrEmailNotifyTemplate smsOrEmailNotifyTemplate = new SmsOrEmailNotifyTemplate();
		smsOrEmailNotifyTemplate.setTemplateType(notifyTypeEnum);
		smsOrEmailNotifyTemplate.setSysModuleEnum(sysModuleEnum);
		List<SmsOrEmailNotifyTemplate> smsOrEmailNotifyTemplateList = smsOrEmailNotifyTemplateDao.findDefaultTemplate(smsOrEmailNotifyTemplate);
		if(null != smsOrEmailNotifyTemplateList && smsOrEmailNotifyTemplateList.size() > DEFAULT_TEMPLATE_SIZE) {
			if(logger.isInfoEnabled()) {
				logger.info("Reset default smsOrEmailNotifyTemplate " + smsOrEmailNotifyTemplate.toString());
			}
			SmsOrEmailNotifyTemplate restoreNotifyTemplate = Collections.min(smsOrEmailNotifyTemplateList);
			List<AlarmSendCondition> alarmSendConditionList = alarmRuleService.getAlarmSendConditionByTemplateId(-1L, SendWayEnum.valueOf(notifyTypeEnum.name()));
			boolean flag = true;
			if(null == alarmSendConditionList || alarmSendConditionList.isEmpty()){
				flag = false;
			}
			for(SmsOrEmailNotifyTemplate template : smsOrEmailNotifyTemplateList) {
				if(restoreNotifyTemplate.getTemplateID() != template.getTemplateID()) {
					smsOrEmailNotifyTemplateDao.resetDefaultTemplate(template);
					SmsOrEmailNotifyTemplateUtil.removeCacheById(template.getTemplateID());
					SmsOrEmailNotifyTemplateUtil.removeDefaultTemplateByType(template.getTemplateType(), template.getSysModuleEnum());
					//需要更新告警规则里面引用的模板Id
					if(flag){
						StringBuffer sb = new StringBuffer();
						sb.append("reset alarm rule template id, ");

						for(AlarmSendCondition alarmSendCondition : alarmSendConditionList) {
							if(alarmSendCondition.getTemplateId() == template.getTemplateID()) {
								alarmSendCondition.setTemplateId(restoreNotifyTemplate.getTemplateID());
								alarmRuleService.updateAlarmConditon(alarmSendCondition.getRuleId(), alarmSendCondition);

								sb.append("rule id is ").append(alarmSendCondition.getRuleId());
								sb.append(",{").append(alarmSendCondition.getTemplateId()).append(":");
								sb.append(restoreNotifyTemplate.getTemplateID());
								sb.append("}");
							}
						}
						if(logger.isInfoEnabled()) {
							logger.info(sb.toString());
						}
					}
				}
			}
			return restoreNotifyTemplate;
		}
		return null;
	}

	/**
	 * 获取当前默认模板,传入模板类型，模块类型
	 * @param notifyType
	 * @param sysModuleEnum
	 * @return
     */
	@Override
	public SmsOrEmailNotifyTemplate findDefaultTemplateByType(String notifyType, SysModuleEnum sysModuleEnum) {
		SmsOrEmailNotifyTemplate smsOrEmailNotifyTemplate = new SmsOrEmailNotifyTemplate();
		smsOrEmailNotifyTemplate.setTemplateType(NotifyTypeEnum.valueOf(notifyType));
		smsOrEmailNotifyTemplate.setSysModuleEnum(sysModuleEnum);
		List<SmsOrEmailNotifyTemplate> smsOrEmailNotifyTemplateList = smsOrEmailNotifyTemplateDao.findDefaultTemplate(smsOrEmailNotifyTemplate);
		if(null != smsOrEmailNotifyTemplateList && !smsOrEmailNotifyTemplateList.isEmpty()) {
			SmsOrEmailNotifyTemplate defaultTemplate = Collections.max(smsOrEmailNotifyTemplateList);
			return defaultTemplate;
		}else
			return null;
	}

	/**
	 * 判断当前模板是否已经被策略引用,false表示被占用，true表示未占用
	 * @param templateId
	 * @param notifyTypeEnum
     * @return
     */
	@Override
	public boolean checkNotifyTemplateEnabled(long templateId, NotifyTypeEnum notifyTypeEnum) {
		List<AlarmSendCondition> result = alarmRuleService.getAlarmSendConditionByTemplateId(templateId, SendWayEnum.valueOf(notifyTypeEnum.name()));
		if(null == result || result.isEmpty())
			return true;
		else
			return false;
	}
}
