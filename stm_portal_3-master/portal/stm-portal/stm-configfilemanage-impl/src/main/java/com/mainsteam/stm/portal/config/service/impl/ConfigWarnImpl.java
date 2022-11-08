package com.mainsteam.stm.portal.config.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQuery.OrderField;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.config.api.IConfigWarnApi;
import com.mainsteam.stm.portal.config.bo.ConfigWarnBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnResourceBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnRuleBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnViewBo;
import com.mainsteam.stm.portal.config.dao.IConfigDeviceDao;
import com.mainsteam.stm.portal.config.dao.IConfigWarnDao;
import com.mainsteam.stm.portal.config.vo.ConditionVo;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmLevelEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.DateUtil;
import com.mainsteam.stm.util.SortList;

public class ConfigWarnImpl implements IConfigWarnApi{
	private static Logger logger = Logger.getLogger(ConfigWarnImpl.class);
	private IConfigWarnDao configWarnDao;
	private IConfigDeviceDao configDeviceDao;
	private ISequence seq;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private AlarmRuleService alarmRuleService;
	@Resource
	private IUserApi stm_system_userApi;
	@Resource
	private AlarmEventService alarmEventService;
	
	@Override
	public void selectByPage(Page<ConfigWarnBo, ConfigWarnBo> page){
		configWarnDao.selectByPage(page);
		List<ConfigWarnBo> configWarnBos = page.getDatas();
		for(ConfigWarnBo bo:configWarnBos){
			bo.setConfigWarnRuleBos(this.getWarnRulesById(bo.getId()));
			bo.setConfigWarnResourceBos(configWarnDao.getWarnResourcesById(bo.getId()));
		}
		page.setDatas(configWarnBos);
	} 
	@Override
	public void selectWarnViewPage(Page<ConfigWarnViewBo, ConfigWarnViewBo> page,boolean all,boolean export,ConditionVo condition) throws Exception {
		AlarmEventQuery alarmEventQuery = new AlarmEventQuery();
		SysModuleEnum[] sysIDes = {SysModuleEnum.CONFIG_MANAGER};
		alarmEventQuery.setSysIDes(sysIDes);
		if(null!=condition){
			if(!StringUtils.isEmpty(condition.getTimeRadio())){
				if("0".equals(condition.getTimeRadio())){
					alarmEventQuery.setStart(getBeginTime(condition.getQueryTime()));
					alarmEventQuery.setEnd(getCurrentTime());
				}else if("1".equals(condition.getTimeRadio())){
					alarmEventQuery.setStart(condition.getStartTime());
					alarmEventQuery.setEnd(condition.getEndTime());
				}
			}
			if(!StringUtils.isEmpty(condition.getIpOrName())){
				alarmEventQuery.setLikeSourceIpOrName(condition.getIpOrName());
			}
		}
		if("warnTime".equals(page.getSort())){
				alarmEventQuery.setOrderASC("asc".equals(page.getOrder().toLowerCase())?true:false);
				OrderField[] orderField = 	{OrderField.COLLECTION_TIME};
				alarmEventQuery.setOrderFieldes(orderField);
		}else if("name".equals(page.getSort())){
			alarmEventQuery.setOrderASC("asc".equals(page.getOrder().toLowerCase())?true:false);
			OrderField[] orderField = 	{OrderField.SOURCE_NAME};
			alarmEventQuery.setOrderFieldes(orderField);
		}
		//查询所以配置文件的资源id
		List<Long> resourceid=configDeviceDao.getAllResourceIds();//查询所有的资源Id来过滤警告信息
		List<String> ids = new ArrayList<>();
		if(resourceid!=null && resourceid.size()>0){
			for (Long id : resourceid) {
				ids.add(String.valueOf(id));
			}
		}
			alarmEventQuery.setSourceIDes(ids);
			if(ids.size()!=0){
				Page<AlarmEvent, AlarmEventQuery> pageResult = alarmEventService.findAlarmEvent(alarmEventQuery, 
						all?0:(int)page.getStartRow(), all?(int)page.getTotalRecord():(int)page.getRowCount());
				List<ConfigWarnViewBo> datas = new ArrayList<ConfigWarnViewBo>();
				List<AlarmEvent> events = pageResult.getDatas();
				for(AlarmEvent event: events){
					ConfigWarnViewBo bo = new ConfigWarnViewBo();
					bo.setId(event.getEventID());
					bo.setSourceId(Long.parseLong(event.getSourceID()));
					bo.setContent(event.getContent());
					bo.setIpAddress(event.getSourceIP());
					bo.setName(event.getSourceName());
					bo.setWarnTime(event.getCollectionTime());
					if(!StringUtils.isEmpty(event.getExt3())){
						bo.setLastFileId(Long.parseLong(event.getExt3()));
						bo.setLastFileName(event.getExt5());
					}
					if(!StringUtils.isEmpty(event.getExt4())){
						bo.setCurrFileId(Long.parseLong(event.getExt4()));
						bo.setCurrFileName(event.getExt6());
					}
						
					Long warnId = configWarnDao.getWarnIdByResourceId(Long.parseLong(event.getSourceID()));
					if(warnId!=null){
						List<ConfigWarnRuleBo> rules = this.getWarnRulesById(warnId);
						if(export){
							String userNames = "";
							for(ConfigWarnRuleBo rule:rules){
								userNames += rule.getUserName()+",";
							}
							if(!StringUtils.isEmpty(userNames) && userNames.endsWith(",")){
								userNames = userNames.substring(0,userNames.lastIndexOf(","));
								bo.setUserNames(userNames);
							}
						}
						bo.setConfigWarnRuleBos(rules);
					}
					datas.add(bo);
				}
				if("ipAddress".equals(page.getSort())){
					SortList.sort(datas, "ipAddress", page.getOrder().toLowerCase());
				}
				page.setDatas(datas);
				page.setTotalRecord(pageResult.getTotalRecord());
			}else{
				page.setDatas(null);
				page.setTotalRecord(0);
			}
	
	}
	private Date getBeginTime(String queryTime) {
		Date date = null;
		if (null == queryTime) {
			return date;
		} else {
			switch (queryTime) {
			case "1":
				date = DateUtil.subHour(getCurrentTime(), 1);
				break;
			case "2":
				date = DateUtil.subHour(getCurrentTime(), 2);
				break;
			case "3":
				date = DateUtil.subHour(getCurrentTime(), 4);
				break;
			case "4":
				date = DateUtil.subDay(getCurrentTime(), 1);
				break;
			case "5":
				date = DateUtil.subDay(getCurrentTime(), 7);
				break;
			}

		}
		return date;
	}

	/**
	 * 获取系统当前日期
	 * 
	 * @return
	 */
	private static Date getCurrentTime() {
		Date date = Calendar.getInstance().getTime();
		return date;
	}
	@Override
	public int insert(ConfigWarnBo bo) {
		//获取主键
		long id=seq.next();
		bo.setEntryId(1l);
		bo.setId(id);
		bo.setEntryDateTime(new Date());
		int nameIsExsit = configWarnDao.checkNameIsExsit(bo.getName());
		if(nameIsExsit > 0){
			return -1;
		}
		//存储告警
		int count = configWarnDao.insert(bo);
		List<ConfigWarnResourceBo> resourceInstances=bo.getConfigWarnResourceBos();
		for(ConfigWarnResourceBo resourceBo:resourceInstances){
			resourceBo.setWarnId(id);
		}
		//存储告警 绑定的resource
		configWarnDao.batchInsertWarnResource(resourceInstances);
		//存储告警 绑定的resource
		List<ConfigWarnRuleBo> rules = bo.getConfigWarnRuleBos();
		//调用server组接口存入stm_alarmrule_condition stm_alarmrule_main表中
		for(ConfigWarnRuleBo rule:rules){
			rule.setWarnId(id);
			alarmRuleService.addAlarmRule(convertConfigWarnRule2AlarmRule(rule));
		}
		List<AlarmRule> alarmRules = alarmRuleService.getAlarmRulesByProfileId(id, AlarmRuleProfileEnum.configfile_manager);
		for(AlarmRule rule:alarmRules){
			for(ConfigWarnRuleBo ruleBo:rules){
				if(ruleBo.getUserId()==Long.parseLong(rule.getUserId())){
					if(ruleBo.getMessage()==1){
						alarmRuleService.addAlarmConditon(rule.getId(), convert2AlarmSendCondition(SendWayEnum.sms));
					}
					if(ruleBo.getEmail()==1){
						alarmRuleService.addAlarmConditon(rule.getId(), convert2AlarmSendCondition(SendWayEnum.email));
					}
					if(ruleBo.getAlert()==1){
						alarmRuleService.addAlarmConditon(rule.getId(), convert2AlarmSendCondition(SendWayEnum.alert));
					}
					break;
				}
			}
		}
		if(count > 0){
			return (int)id;
		}else{
			return 0;
		}
	}
	
	/**
	 * 根据发送方式获取AlarmSendCondition
	 * @param sendWayEnum
	 * @return
	 */
	private AlarmSendCondition convert2AlarmSendCondition(SendWayEnum sendWayEnum){
		AlarmSendCondition condition = new AlarmSendCondition();
		condition.setEnabled(true);
		condition.setAllTime(true);
		condition.setSendWay(sendWayEnum);
		List<AlarmLevelEnum> levels = new ArrayList<AlarmLevelEnum>();
		levels.add(AlarmLevelEnum.metric_warn);
		condition.setAlarmLevels(levels);
		return condition;
	}
	/**
	 * ConfigWarnRule转换成AlarmRule
	 * @param bo
	 * @return
	 */
	private AlarmRule convertConfigWarnRule2AlarmRule(ConfigWarnRuleBo bo){
		AlarmRule rule = new AlarmRule();
		rule.setProfileId(bo.getWarnId());
		rule.setProfileType(AlarmRuleProfileEnum.configfile_manager);
		rule.setUserId(String.valueOf(bo.getUserId()));
		return rule;
	}
	@Override
	public int update(ConfigWarnBo bo){
		bo.setUpdateId(1l);
		bo.setUpdateDateTime(new Date());
		int count = configWarnDao.update(bo);
		//在查找与之对应的资源ID列表，若没有改变，则不更新数据库
		List<ConfigWarnResourceBo> newResources = bo.getConfigWarnResourceBos();
		for(ConfigWarnResourceBo resourceBo:newResources){
			resourceBo.setWarnId(bo.getId());
		}
		List<ConfigWarnResourceBo> oldResources = configWarnDao.getWarnResourcesById(bo.getId());
		if(!newResources.equals(oldResources)){
			logger.info("Update warn-resource relationship!");
			//1.先删除旧对应关系
			configWarnDao.delWarnResourcesById(bo.getId());
			//2.插入新的对应关系
			configWarnDao.batchInsertWarnResource(newResources);
			
		}
		//先删除rules再添加新的rules
		List<AlarmRule> oldAlarmRules = alarmRuleService.getAlarmRulesByProfileId(bo.getId(),
				AlarmRuleProfileEnum.configfile_manager);
		long[] ids = new long[oldAlarmRules.size()];
		for(int i=0;i<oldAlarmRules.size();i++){
			ids[i] = oldAlarmRules.get(i).getId();
		}
		alarmRuleService.deleteAlarmRuleById(ids);
		List<ConfigWarnRuleBo> newRules = bo.getConfigWarnRuleBos();
		for(ConfigWarnRuleBo rule:newRules){
			rule.setWarnId(bo.getId());
			alarmRuleService.addAlarmRule(convertConfigWarnRule2AlarmRule(rule));
		}
		List<AlarmRule> newAlarmRules = alarmRuleService.getAlarmRulesByProfileId(bo.getId(),
				AlarmRuleProfileEnum.configfile_manager);
		for(AlarmRule rule:newAlarmRules){
			for(ConfigWarnRuleBo ruleBo:newRules){
				if(ruleBo.getUserId()==Long.parseLong(rule.getUserId())){
					if(ruleBo.getMessage()==1){
						alarmRuleService.addAlarmConditon(rule.getId(), convert2AlarmSendCondition(SendWayEnum.sms));
					}
					if(ruleBo.getEmail()==1){
						alarmRuleService.addAlarmConditon(rule.getId(), convert2AlarmSendCondition(SendWayEnum.email));
					}
					if(ruleBo.getAlert()==1){
						alarmRuleService.addAlarmConditon(rule.getId(), convert2AlarmSendCondition(SendWayEnum.alert));
					}
					break;
				}
			}
		}
		return count;
	}
	@Override
	public int batchDel(Long[] ids) {
		int count = configWarnDao.batchDelConfigWarn(ids);
		configWarnDao.batchDelConfigWarnResource(ids);
		for(Long id:ids){
			List<AlarmRule> oldAlarmRules = alarmRuleService.getAlarmRulesByProfileId(id,
					AlarmRuleProfileEnum.configfile_manager);
			long[] ruleIds = new long[oldAlarmRules.size()];
			for(int i=0;i<oldAlarmRules.size();i++){
				ruleIds[i] = oldAlarmRules.get(i).getId();
			}
			alarmRuleService.deleteAlarmRuleById(ruleIds);
		}
		return count;
	}
	@Override
	public ConfigWarnBo get(Long id) {
		ConfigWarnBo bo = configWarnDao.get(id);
		bo.setConfigWarnResourceBos(configWarnDao.getWarnResourcesById(bo.getId()));
		bo.setConfigWarnRuleBos(this.getWarnRulesById(bo.getId()));
		return bo;
	}
	@Override
	public List<ResourceInstance> getLeftResourceInstanceList(Long id,
			String searchKey) throws Exception {
		List<ResourceInstance> returnList = new ArrayList<ResourceInstance>();
		List<Long> allResourceIds = configDeviceDao.getAllResourceIds();
		List<ResourceInstance> rightInstances = this.getRightResourceInstanceList(id);
		for(Long resourceId: allResourceIds){
			boolean flag = true;
			ResourceInstance instance = resourceInstanceService.getResourceInstance(resourceId);
			Long warnId = configWarnDao.getWarnIdByResourceId(resourceId);
			if(instance==null || warnId!=null) continue;
			for(ResourceInstance resourceInstance: rightInstances){
				if(null==resourceInstance) continue;
				if(resourceInstance.getId() == instance.getId()){
					flag = false;
					break;
				}
			}
			if(null!=searchKey && !"".equals(searchKey)){
				if(!instance.getShowName().toUpperCase().contains(searchKey.toUpperCase()) && !instance.getShowIP().contains(searchKey)){
					flag = false;
				}
			}
			if(flag) returnList.add(instance);
		}
		return returnList;
	}
	@Override
	public List<ResourceInstance> getRightResourceInstanceList(Long id)
			throws Exception {
		List<ConfigWarnResourceBo> resourceBos = configWarnDao.getWarnResourcesById(id);
		List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
		if(resourceBos.size()>0 && null!=resourceBos){
			for(ConfigWarnResourceBo resourceBo: resourceBos){
				ResourceInstance instance = resourceInstanceService.getResourceInstance(resourceBo.getResourceId());
				instances.add(instance);
			}
		}
		return instances;
	}
	public IConfigWarnDao getConfigWarnDao() {
		return configWarnDao;
	}
	public void setConfigWarnDao(IConfigWarnDao configWarnDao) {
		this.configWarnDao = configWarnDao;
	}
	public ISequence getSeq() {
		return seq;
	}
	public void setSeq(ISequence seq) {
		this.seq = seq;
	}
	public IConfigDeviceDao getConfigDeviceDao() {
		return configDeviceDao;
	}
	public void setConfigDeviceDao(IConfigDeviceDao configDeviceDao) {
		this.configDeviceDao = configDeviceDao;
	}
	/**
	 * 根据告警规则查询configWarnRuleBos
	 * @param id
	 * @return
	 */
	@Override
	public List<ConfigWarnRuleBo> getWarnRulesById(Long id) {
		List<AlarmRule> alarmRules = alarmRuleService.getAlarmRulesByProfileId(id, 
				AlarmRuleProfileEnum.configfile_manager);
		return convertAlarmRule2ConfigWarnRule(alarmRules);
	}
	/**
	 * 转换AlarmRule2ConfigWarnRuleBo
	 * @param alarmRules
	 * @return
	 */
	private List<ConfigWarnRuleBo> convertAlarmRule2ConfigWarnRule(List<AlarmRule> alarmRules){
		List<ConfigWarnRuleBo> configWarnRuleBos = new ArrayList<ConfigWarnRuleBo>();
		for(AlarmRule alarmRule: alarmRules){
			ConfigWarnRuleBo bo = new ConfigWarnRuleBo();
			bo.setWarnId(alarmRule.getProfileId());
			bo.setUserId(Long.parseLong(alarmRule.getUserId()));
			User user= stm_system_userApi.get(Long.parseLong(alarmRule.getUserId()));
			if(null==user) continue;
			bo.setUserName(user.getName());
			List<AlarmSendCondition> conditions = alarmRule.getSendConditions();
			if(null!=conditions&&conditions.size()>0){
				for(AlarmSendCondition condition:conditions){
					if(SendWayEnum.email.name().equals(condition.getSendWay().name())){
						bo.setEmail(1);
						continue;
					}else if(SendWayEnum.sms.name().equals(condition.getSendWay().name())){
						bo.setMessage(1);
						continue;
					}else if(SendWayEnum.alert.name().equals(condition.getSendWay().name())){
						bo.setAlert(1);
						continue;
					}
				}
			}
			if(bo.getEmail()==null) bo.setEmail(0);
			if(bo.getMessage()==null) bo.setMessage(0);
			if(bo.getAlert()==null) bo.setAlert(0);
			configWarnRuleBos.add(bo);
		}
		return configWarnRuleBos;
	}
}
