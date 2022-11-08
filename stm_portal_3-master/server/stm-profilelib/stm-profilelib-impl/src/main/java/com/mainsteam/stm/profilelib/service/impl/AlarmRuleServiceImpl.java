package com.mainsteam.stm.profilelib.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.profilelib.util.AlarmRuleCacheUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonEnableInfo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmLevelEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmNotifyPeriodForDay;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmNotifyPeriodForWeek;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.ContinusUnitEnum;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO;
import com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO;
import com.mainsteam.stm.profilelib.po.AlarmRuleConditionPO;
import com.mainsteam.stm.profilelib.po.AlarmRuleMainPO;

/**
 * @author ziw
 */
public class AlarmRuleServiceImpl implements AlarmRuleService {

	private static final Log logger = LogFactory.getLog(AlarmRuleService.class);

	private AlarmRuleMainDAO mainDAO;

	private AlarmRuleConditionDAO conditionDAO;

	private ISequence sequence;

	public AlarmRuleServiceImpl() { }

	/**
	 * @param mainDAO
	 *            the mainDAO to set
	 */
	public final void setMainDAO(AlarmRuleMainDAO mainDAO) {
		this.mainDAO = mainDAO;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public final void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @param conditionDAO
	 *            the conditionDAO to set
	 */
	public final void setConditionDAO(AlarmRuleConditionDAO conditionDAO) {
		this.conditionDAO = conditionDAO;
	}

	public void start() {
		//sendCondtionCache = MemCacheFactory.getRemoteMemCache(List.class);
	}

	private AlarmRuleConditionPO toPo(AlarmSendCondition c, long ruleId) {
		AlarmRuleConditionPO po = new AlarmRuleConditionPO();
		StringBuilder b = new StringBuilder();
		List<AlarmLevelEnum> alarmLevels = c.getAlarmLevels();
		if (alarmLevels != null && alarmLevels.size() > 0) {
			int length = alarmLevels.size() - 1;
			for (int i = 0; i < length; i++) {
				b.append(alarmLevels.get(i).name()).append(',');
			}
			b.append(alarmLevels.get(length).name());
		}
		po.setAlarmLevels(b.toString());
		po.setContinus(c.isContinus() ? 1 : 0);
		po.setContinusCount(c.getContinusCount());
		if (c.isContinus()) {
			po.setContinusUnit(c.getContinusCountUnit().name());
		} else {
			po.setContinusUnit(c.getContinusCountUnit() == null ? null : c.getContinusCountUnit().name());
		}
		po.setEnabled(c.isEnabled() ? 1 : 0);
		po.setRuleId(ruleId);
		po.setSendWay(c.getSendWay().name());
		po.setAllTime(c.isAllTime());
		po.setSendIntime(c.isSendIntime());
		
		po.setWeekPeriodes(JSON.toJSONString(c.getWeekPeriodes()));
		po.setDayPeriodes(JSON.toJSONString(c.getDayPeriodes()));
		po.setTemplateId(c.getTemplateId());
		po.setSendTimes(c.getSendTimes());
		return po;
	}

	private AlarmRuleMainPO toPo(AlarmRule rule) {
		AlarmRuleMainPO po = new AlarmRuleMainPO();
		po.setId(rule.getId());
		po.setProfileId(rule.getProfileId());
		po.setProfileType(rule.getProfileType().name());
		po.setUserId(rule.getUserId());
		return po;
	}

	@Override
	public void addAlarmConditon(long ruleId, AlarmSendCondition condition) {
		if (logger.isTraceEnabled()) {
			logger.trace("addAlarmConditon start");
		}
		AlarmRuleConditionPO conditionPO = toPo(condition, ruleId);
		try {
			conditionDAO.insert(conditionPO);
			//加入缓存
			if(condition.getRuleId() == 0L)
				condition.setRuleId(ruleId);
			AlarmRuleCacheUtil.saveAlarmSendCondition(condition);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addAlarmConditon", e);
			}
			AlarmRuleCacheUtil.removeAlarmSendConditionBySendWay(String.valueOf(ruleId), condition.getSendWay());
		}
		if (logger.isTraceEnabled()) {
			logger.trace("addAlarmConditon end");
		}
	}

	@Override
	public void addAlarmRule(AlarmRule rule) {
		if (logger.isTraceEnabled()) {
			logger.trace("addAlarmRule start");
		}
		/* 生成ruleId */
		long ruleId = sequence.next();
		rule.setId(ruleId);

		/* 验证规则 */
		AlarmRuleMainPO mainPO = toPo(rule);
		mainDAO.insert(mainPO);
		String key = rule.getProfileId()+rule.getProfileType().name();
		boolean b = AlarmRuleCacheUtil.saveAlarmRules(rule, key);
		if(logger.isInfoEnabled()) {
			logger.info("save alarm rule cache:" + key + ",with data:" + rule + ",result:" + b);
		}
		List<AlarmSendCondition> alarmRuleConditions = rule.getSendConditions();
		if(alarmRuleConditions != null && alarmRuleConditions.size() > 0) {
			for (AlarmSendCondition alarmSendCondition : alarmRuleConditions) {
				conditionDAO.insert(toPo(alarmSendCondition, rule.getId()));
				if(alarmSendCondition.getRuleId() == 0L)
					alarmSendCondition.setRuleId(rule.getId());
			}
			AlarmRuleCacheUtil.saveAllAlarmSendCondition(alarmRuleConditions, String.valueOf(rule.getId()));
		}
		if (logger.isTraceEnabled()) {
			logger.trace("addAlarmRule end");
		}
	}

	@Override
	public void deleteAlarmRuleById(long[] ruleIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("deleteAlarmRuleById start");
		}
		AlarmRuleMainPO mainPo = new AlarmRuleMainPO();
		for (long ruleId : ruleIds) {
			mainPo.setId(ruleId);
			List<AlarmRuleMainPO> alarmRuleMainPOS = mainDAO.get(mainPo);
			if(alarmRuleMainPOS != null) {
				for(AlarmRuleMainPO po : alarmRuleMainPOS) {
					AlarmRuleCacheUtil.removeAlarmRulesByRuleID(po.getProfileId() + po.getProfileType(), ruleId);
					AlarmRuleCacheUtil.removeAlarmSendCondition(String.valueOf(ruleId));
					mainDAO.delete(po);
					conditionDAO.delete(ruleId);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("deleteAlarmRuleById end");
		}
	}

	@Override
	public List<AlarmRule> getAlarmRulesByProfileId(long profileId,
			AlarmRuleProfileEnum profileType) {
		if (logger.isTraceEnabled()) {
			logger.trace("getAlarmRulesByProfileId start profileId="
					+ profileId + " profileType=" + profileType);
		}
		List<AlarmRule> alarmRuleList = AlarmRuleCacheUtil.catchAlarmRules(profileId + profileType.name());
		if(null != alarmRuleList && !alarmRuleList.isEmpty()){
			//还需要将AlarmSendCondition的缓存加入进来
			for(AlarmRule rule : alarmRuleList){
				rule.setSendConditions(getAlarmSendConditionByRule(rule.getId()));
			}
			return alarmRuleList;
		}
		AlarmRuleMainPO mainPo = new AlarmRuleMainPO();
		mainPo.setProfileId(profileId);
		mainPo.setProfileType(profileType.name());
		List<AlarmRule> alarmRules = getAlarmRules(mainPo);
		if (logger.isTraceEnabled()) {
			logger.trace("getAlarmRulesByProfileId end");
		}
		//加入缓存
		AlarmRuleCacheUtil.saveAlarmRules(alarmRules, profileId + profileType.name());
		return alarmRules;
	}

	private List<AlarmSendCondition> getAlarmSendConditionByRule(long ruleId) {
		//先从缓存读取
		List<AlarmSendCondition> alarmSendConditions = AlarmRuleCacheUtil.catchAlarmSendCondition(String.valueOf(ruleId));
		if(null == alarmSendConditions || alarmSendConditions.isEmpty()){
			AlarmRuleConditionPO conditionPO = new AlarmRuleConditionPO();
			conditionPO.setRuleId(ruleId);
			List<AlarmRuleConditionPO> alarmRuleConditionPOs = conditionDAO.get(conditionPO);
			if (alarmRuleConditionPOs != null && alarmRuleConditionPOs.size() > 0) {
				alarmSendConditions = new ArrayList<>(alarmRuleConditionPOs.size());
				for (AlarmRuleConditionPO alarmRuleConditionPO : alarmRuleConditionPOs) {
					alarmSendConditions.add(toCondition(alarmRuleConditionPO));
				}
				//加入缓存
				AlarmRuleCacheUtil.saveAllAlarmSendCondition(alarmSendConditions, String.valueOf(ruleId));
			}
		}
		return alarmSendConditions;
	}

	private List<AlarmRule> getAlarmRules(AlarmRuleMainPO mainPo) {
		List<AlarmRuleMainPO> alarmRuleMainPOs = mainDAO.get(mainPo);
		List<AlarmRule> alarmRules = new ArrayList<>(alarmRuleMainPOs.size());
		for (AlarmRuleMainPO alarmRuleMainPO : alarmRuleMainPOs) {
			AlarmRule rule = toAlarmRule(alarmRuleMainPO);
			rule.setSendConditions(getAlarmSendConditionByRule(rule.getId()));
			alarmRules.add(rule);
		}
		return alarmRules;
	}

	private AlarmSendCondition toCondition(AlarmRuleConditionPO conditionPO) {
		AlarmSendCondition sendCondition = new AlarmSendCondition();
		if (conditionPO.getAlarmLevels() != null && conditionPO.getAlarmLevels().length() > 0) {
			String[] levels = conditionPO.getAlarmLevels().split(",");
			List<AlarmLevelEnum> alarmLevelEnums = new ArrayList<>(levels.length);
			for (int i = 0; i < levels.length; i++) {
				alarmLevelEnums.add(AlarmLevelEnum.valueOf(levels[i]));
			}
			sendCondition.setAlarmLevels(alarmLevelEnums);
		}
		sendCondition.setContinus(conditionPO.getContinus() == 1);
		sendCondition.setContinusCount(conditionPO.getContinusCount());
		if (conditionPO.getContinusUnit() != null) {
			sendCondition.setContinusCountUnit(ContinusUnitEnum.valueOf(conditionPO.getContinusUnit()));
		}
		sendCondition.setEnabled(conditionPO.getEnabled() == 1);
		sendCondition.setSendWay(SendWayEnum.valueOf(conditionPO.getSendWay()));
		
		sendCondition.setAllTime(conditionPO.isAllTime());
		sendCondition.setSendIntime(conditionPO.isSendIntime());
		
		sendCondition.setDayPeriodes(JSON.parseArray(conditionPO.getDayPeriodes(),AlarmNotifyPeriodForDay.class));
		sendCondition.setWeekPeriodes(JSON.parseArray(conditionPO.getWeekPeriodes(),AlarmNotifyPeriodForWeek.class));
		sendCondition.setTemplateId(conditionPO.getTemplateId());
		sendCondition.setRuleId(conditionPO.getRuleId());
		sendCondition.setSendTimes(conditionPO.getSendTimes());
		return sendCondition;
	}

	private AlarmRule toAlarmRule(AlarmRuleMainPO mainPO) {
		AlarmRule rule = new AlarmRule();
		rule.setId(mainPO.getId());
		rule.setProfileId(mainPO.getProfileId());
		rule.setProfileType(AlarmRuleProfileEnum.valueOf(mainPO.getProfileType()));
		rule.setUserId(mainPO.getUserId());
		return rule;
	}

	@Override
	public List<AlarmRule> getAlarmRulesByUserId(String userId, AlarmRuleProfileEnum profileType) {
		if (logger.isTraceEnabled()) {
			logger.trace("getAlarmRulesByUserId start userId=" + userId + " profileType=" + profileType);
		}
		AlarmRuleMainPO mainPo = new AlarmRuleMainPO();
		mainPo.setUserId(userId);
		if (profileType != null) {
			mainPo.setProfileType(profileType.name());
		}
		List<AlarmRule> alarmRules = getAlarmRules(mainPo);
		if (logger.isTraceEnabled()) {
			logger.trace("getAlarmRulesByUserId end");
		}
		return alarmRules;
	}

	@Override
	public AlarmSendCondition getAlarmSendCondition(long ruleId, SendWayEnum sendWay) {
		if (logger.isTraceEnabled()) {
			logger.trace("getAlarmSendCondition start");
		}
		AlarmSendCondition condition = null;
		//先从缓存读取
		List<AlarmSendCondition> alarmSendConditionCache = AlarmRuleCacheUtil.catchAlarmSendCondition(String.valueOf(ruleId));
		if(null != alarmSendConditionCache && !alarmSendConditionCache.isEmpty()) {
			for(AlarmSendCondition alarmSendCondition : alarmSendConditionCache) {
				if(alarmSendCondition.getSendWay() == sendWay)
					return alarmSendCondition;
			}
		}
		AlarmRuleConditionPO conditionPO = new AlarmRuleConditionPO();
		conditionPO.setRuleId(ruleId);
		conditionPO.setSendWay(sendWay.name());
		List<AlarmRuleConditionPO> alarmRuleConditionPOs = conditionDAO.get(conditionPO);
		if (alarmRuleConditionPOs != null && alarmRuleConditionPOs.size() > 0) {
			assert alarmRuleConditionPOs.size() == 1;
			condition = toCondition(alarmRuleConditionPOs.get(0));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("getAlarmSendCondition end");
		}
		//加入缓存
		AlarmRuleCacheUtil.saveAlarmSendCondition(condition);
		return condition;
	}

	@Override
	public List<AlarmRule> getAllAlarmRules(AlarmRuleProfileEnum profileType) {
		if (logger.isTraceEnabled()) {
			logger.trace("getAllAlarmRules start profileType=" + profileType);
		}
		AlarmRuleMainPO mainPo = new AlarmRuleMainPO();
		mainPo.setProfileType(profileType.name());
		List<AlarmRule> alarmRules = getAlarmRules(mainPo);
		if (logger.isTraceEnabled()) {
			logger.trace("updateAlarmConditon end");
		}
		return alarmRules;
	}

	@Override
	public void updateAlarmConditon(long ruleId, AlarmSendCondition condition) {
		if (logger.isTraceEnabled()) {
			logger.trace("updateAlarmConditon start ruleId=" + ruleId+ " sendWay=" + condition.getSendWay());
		}
		AlarmRuleConditionPO conditionPO = toPo(condition, ruleId);
		conditionDAO.updateById(conditionPO);
		if(0L == condition.getRuleId())
			condition.setRuleId(ruleId);
		AlarmRuleCacheUtil.updateAlarmSendConditionBySendWay(condition);
		if (logger.isTraceEnabled()) {
			logger.trace("updateAlarmRule end");
		}
	}

	@Override
	public void changeAlarmConditionEnabled(
			List<AlarmConditonEnableInfo> conditions) {
		if (logger.isTraceEnabled()) {
			logger.trace("changeAlarmConditionEnabled start");
		}
		AlarmRuleConditionPO conditionPO = new AlarmRuleConditionPO();
		for (AlarmConditonEnableInfo alarmConditonEnableInfo : conditions) {
			conditionPO.setRuleId(alarmConditonEnableInfo.getRuleId());
			conditionPO.setSendWay(alarmConditonEnableInfo.getSendWay().name());
			conditionPO.setEnabled(alarmConditonEnableInfo.isEnabled() ? 1 : 0);
			conditionDAO.updateEnabledById(conditionPO);
			AlarmRuleCacheUtil.updateAlarmSendConditionEnabled(String.valueOf(alarmConditonEnableInfo.getRuleId()),alarmConditonEnableInfo.getSendWay(),
					alarmConditonEnableInfo.isEnabled());
		}
		if (logger.isTraceEnabled()) {
			logger.trace("changeAlarmConditionEnabled end");
		}
	}

	@Override
	public void deleteAlarmCondition(long ruleID, SendWayEnum sendWay) {
		conditionDAO.delete(ruleID,sendWay);
		AlarmRuleCacheUtil.removeAlarmSendConditionBySendWay(String.valueOf(ruleID), sendWay);
	}

	@Override
	public List<AlarmSendCondition> getAlarmSendConditionByTemplateId(long templateId, SendWayEnum sendWay) {
		AlarmRuleConditionPO po = new AlarmRuleConditionPO();
		po.setTemplateId(templateId);
		if(null != sendWay)
			po.setSendWay(sendWay.name());
		List<AlarmRuleConditionPO> alarmRuleConditionPOList = conditionDAO.get(po);
		if(null != alarmRuleConditionPOList && !alarmRuleConditionPOList.isEmpty()) {
			List<AlarmSendCondition> result = new ArrayList<>(alarmRuleConditionPOList.size());
			for(AlarmRuleConditionPO alarmRuleConditionPO : alarmRuleConditionPOList) {
				AlarmSendCondition alarmSendCondition = toCondition(alarmRuleConditionPO);
				result.add(alarmSendCondition);
			}
			return result;
		}
		return null;
	}

}
