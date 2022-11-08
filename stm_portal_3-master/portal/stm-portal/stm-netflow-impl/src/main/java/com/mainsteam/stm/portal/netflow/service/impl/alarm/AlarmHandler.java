package com.mainsteam.stm.portal.netflow.service.impl.alarm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.job.CronExpressionHelper;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmContent;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmResourceBo;
import com.mainsteam.stm.portal.netflow.dao.alarm.IAlarmProfileDao;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;

@Service
public class AlarmHandler {
	@Resource(name = "AlarmDao")
	private IAlarmProfileDao alarmDao;
	@Resource
	private AlarmService alarmService;
	@Resource
	private AlarmRuleService alarmRuleService;
	@Resource(name="scheduleManager")
	private ScheduleManager scheduleManager;

	@PostConstruct
	public void task() {
			Set<String> cronExpressions = new HashSet<String>();
			CronExpressionHelper ce = new CronExpressionHelper();
			ce.set(CronExpressionHelper.MINUTE, "2");
			cronExpressions.add(ce.toString());
			try {
				List<IJob> jobList = new ArrayList<IJob>();
				IJob job = new IJob("netFlowAlarmHandler", new AlarmJob(),
						cronExpressions, null);
				jobList.add(job);
				if(!this.scheduleManager.isExists(job)){
					this.scheduleManager.addJobs(jobList);
				}
			} catch (ClassNotFoundException | SchedulerException e) {
				e.printStackTrace();
			}
	}
	
	public void sendAlarm(){
		List<AlarmContent> list = alarmDao.getAlarmEvents();
		if (list != null && list.size() > 0) {
			for (AlarmContent bo : list) {
				AlarmSenderParamter asp = new AlarmSenderParamter();
				AlarmResourceBo arb = alarmDao.getPrfileLevel(bo
						.getThresholdId());// 根据告警内容中的域ID，去查询告警对应的profileID和对应的告警级别
				List<AlarmRule> alarmList = alarmRuleService
						.getAlarmRulesByProfileId(new Long(arb.getPrefix()),
								AlarmRuleProfileEnum.netFlow);
				asp.setProfileID(0);
				asp.setRuleType(AlarmRuleProfileEnum.netFlow);
				asp.setProvider(AlarmProviderEnum.OC4);
				asp.setSysID(SysModuleEnum.NETFLOW);
				asp.setSourceID(bo.getServerName());
				asp.setSourceName(bo.getModuleName());
				if (arb.getSubfix() != null && !"".equals(arb.getSubfix())
						&& "1".equals(arb.getSubfix())) {// 对应为，1对应严重，2对应告警
					asp.setLevel(InstanceStateEnum.SERIOUS);
				} else {
					asp.setLevel(InstanceStateEnum.WARN);
				}
				// 告警IP地址，在流量分析中不存在，不传asp.setSourceIp();
				asp.setDefaultMsgTitle("流量分析告警");
				asp.setDefaultMsg(bo.getAlarmDetail());
				asp.setGenerateTime(bo.getCreateTime());
				asp.setNotifyRules(alarmList);
				try{
				alarmService.notify(asp);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			String[] ids = new String[list.size()];
			for (int loop = 0; loop < list.size(); loop++) {
				ids[loop] = list.get(loop).getId();
			}
			try{
				alarmDao.updateAlarmEventState(ids);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
