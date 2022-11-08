package com.mainsteam.stm.topo.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonEnableInfo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmLevelEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.topo.api.TopoAlarmExApi;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.dao.IMacBaseDao;
import com.mainsteam.stm.topo.dao.IMacRuntimeDao;
import com.mainsteam.stm.topo.dao.INodeDao;

/**
 * <li>封装调用其他模块的接口</li>
 * <li>文件名称: ThirdServiceImpl2.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年11月29日
 * @author zwx
 */
@Service
public class TopoAlarmExImpl extends ThirdServiceBase implements TopoAlarmExApi{
	Logger logger = Logger.getLogger(TopoAlarmExImpl.class);
	@Autowired
	private IMacRuntimeDao macRuntimeDao;
	@Autowired
	private IMacBaseDao macBaseDao;
	@Autowired
	private INodeDao nodeDao;
	
	/**
	 * 计算刷新的ipmac是否需要告警
	 * @param base
	 * @param runtime
	 * @param alarmSetting
	 * @return
	 */
	public void sendAlarmNotice(MacBaseBo base,MacRuntimeBo runtime,String alarmSetting){
		boolean isAlarm = false;
		JSONObject setting = (JSONObject) JSONObject.parseObject(alarmSetting);
		JSONObject warnLeavel = setting.getJSONObject("warnLeavel");
		JSONArray warnPolicy = JSONObject.parseArray(setting.getString("warnPolicy"));
		
		if(warnPolicy.contains(base.getIp())) return;	//基准表的设备如果设置了IP不告警，则不产生告警信息
		
		String baseMac = "设备【mac="+base.getMac()+"】告警：";
		List<Map<String, Object>> alrams = new ArrayList<Map<String,Object>>();
		Map<String, Object> levelMap = new HashMap<String, Object>();
		
		//1.设备绑定的IP地址变化(排除策略中不告警IP)
		if(!base.getIp().equals(runtime.getIp())){
			isAlarm = true;
			int alarmLevel = warnLeavel.getIntValue("ip");
			levelMap.put("notifyLevel",this.parseInstanceAlarmLevel(alarmLevel));
			levelMap.put("metricLevel",this.parseAlarmLevel(alarmLevel));
			levelMap.put("msg",new StringBuffer(baseMac).append("绑定的IP地址由【").append(base.getIp()).append("】改变为【").append(runtime.getIp()).append("】").toString());
			alrams.add(levelMap);
		}
		
		//2.绑定的上联设备的名称或IP发生变化
		boolean upDeviceIpChange = !base.getUpDeviceIp().equals(runtime.getUpDeviceIp());			//上联设备ip发生变化
		boolean upDeviceNameChange = !base.getUpDeviceName().equals(runtime.getUpDeviceName());		//上联设备名称发生变化
		if(upDeviceIpChange || upDeviceNameChange){
			levelMap = new HashMap<String, Object>();
			isAlarm = true;
			int alarmLevel = warnLeavel.getIntValue("upDevice");
			levelMap.put("notifyLevel",this.parseInstanceAlarmLevel(alarmLevel));
			levelMap.put("metricLevel",this.parseAlarmLevel(alarmLevel));
			String msg = baseMac;
			if(upDeviceIpChange){
				msg += new StringBuffer("绑定的上联设备的IP地址由【").append(base.getUpDeviceIp()).append("】改变为【").append(runtime.getUpDeviceIp()).append("】").toString();
			}
			if(upDeviceNameChange){
				if(upDeviceIpChange) msg += "; ";
				msg += new StringBuffer("绑定的上联设备名称由【").append(base.getUpDeviceName()).append("】改变为【").append(runtime.getUpDeviceName()).append("】").toString();
			}
			levelMap.put("msg",msg);
			alrams.add(levelMap);
		}
		
		//3.绑定的上联设备接口的名称发生变化
		if(!base.getUpDeviceInterface().equals(runtime.getUpDeviceInterface())){
			levelMap = new HashMap<String, Object>();
			isAlarm = true;
			int alarmLevel = warnLeavel.getIntValue("upDeviceInterface");
			levelMap.put("notifyLevel",this.parseInstanceAlarmLevel(alarmLevel));
			levelMap.put("metricLevel",this.parseAlarmLevel(alarmLevel));
			levelMap.put("msg",new StringBuffer(baseMac).append("绑定的上联设备接口由【").append(base.getUpDeviceInterface()).append("】改变为【").append(runtime.getUpDeviceInterface()).append("】").toString());
			alrams.add(levelMap);
		}
		base.setAlarms(alrams);		//设置变化的基准mac告警信息
		
		//发送告警
		if(isAlarm){
			try {
				this.ipMacAlarmNotify(base, alarmSetting);
			} catch (Exception e) {
				logger.error("发送ip-mac-port告警信息发生异常!",e);
			}
		}
	}
	
	/**
	 * 解析告警级别
	 * @param alarmLevel
	 * @return
	 */
	private InstanceStateEnum parseInstanceAlarmLevel(int alarmLevel){
		InstanceStateEnum levelEnum = null;
		switch(alarmLevel){
		case 1:	//警告
			levelEnum = InstanceStateEnum.WARN;break;
		case 2:	//严重
			levelEnum = InstanceStateEnum.SERIOUS;break;
		case 3:	//致命
			levelEnum = InstanceStateEnum.CRITICAL;break;
		default:
			levelEnum = InstanceStateEnum.WARN;
		}
		return levelEnum;
	}
	
	/**
	 * 解析指标告警级别
	 * @param alarmLevel
	 * @return
	 */
	private AlarmLevelEnum parseAlarmLevel(int alarmLevel){
		AlarmLevelEnum levelEnum = null;
		switch(alarmLevel){
		case 1:	//警告
			levelEnum = AlarmLevelEnum.metric_warn;break;
		case 2:	//严重
			levelEnum = AlarmLevelEnum.metric_error;break;
		case 3:	//致命
			levelEnum = AlarmLevelEnum.down;break;
		default:
			levelEnum = AlarmLevelEnum.metric_error;
		}
		return levelEnum;
	}
	
	/**
	 * 发送ipmac告警
	 * @param baseMac
	 * @param alramSet
	 */
	private void ipMacAlarmNotify(MacBaseBo baseMac,String alramSet){
		//解析合并【短信、邮件、Alert】告警人
		List<Map<String, Object>> senders = this.parseIpMacAlarmSenders(alramSet);
		
		List<Map<String, Object>> alarms = baseMac.getAlarms();
		for(Map<String, Object> alarmMap:alarms){
			//封装成一个告警对象
			String alarmMsg = alarmMap.get("msg").toString();	//告警信息
			String id = String.valueOf(baseMac.getId());
			AlarmSenderParamter alarmSenderParamter = new AlarmSenderParamter();
			alarmSenderParamter.setDefaultMsg(alarmMsg);									//告警信息
			alarmSenderParamter.setGenerateTime(new Date());								//推送告警时间
			alarmSenderParamter.setLevel((InstanceStateEnum) alarmMap.get("notifyLevel"));	//告警级别
			alarmSenderParamter.setProvider(AlarmProviderEnum.OC4);
			alarmSenderParamter.setSysID(SysModuleEnum.IP_MAC_PORT);
			alarmSenderParamter.setSourceID(id);
			alarmSenderParamter.setSourceName("设备MAC【"+baseMac.getMac()+"】");
			alarmSenderParamter.setSourceIP(baseMac.getIp());
			alarmSenderParamter.setRuleType(AlarmRuleProfileEnum.ip_mac_port);
//			alarmSenderParamter.setRecoverKeyValue(new String[]{id});	/告警恢复(不需要)
			alarmSenderParamter.setNotifyRules(this.parseToAlarmRule(alarmMap,senders));
			alarmSenderParamter.setExt0(SysModuleEnum.IP_MAC_PORT.name());
			
			logger.info("基准表告警的IP="+baseMac.getIp()+"; 告警信息="+alarmSenderParamter.getDefaultMsg());
			//获取拓扑发现的节点
			List<NodeBo> nodes = nodeDao.getAllInstances();
			for(int i=nodes.size()-1;i>=0;i--){
				NodeBo node = nodes.get(i);
				String ip = node.getIp();
//				logger.error("node.getIp()=["+ip+"],baseMac.getIp()=["+baseMac.getIp()+"],ip.contains(baseMac.getIp())="+(ip.contains(baseMac.getIp())));
				
				//通过ip匹配，找出实例id？？
				if(null != ip && ip.contains(baseMac.getIp())){
					logger.error("IP-MAC-PORT告警的IP="+ip);
					ResourceInstanceBo res =  resourceApi.getResource(node.getInstanceId());
					if(null!=res){
						String[] parentResource = resourceApi.getCategoryParents(res.getCategoryId());
						if (parentResource.length > 2) {
							logger.error("setExt1="+parentResource[0]);
							logger.error("setExt2="+parentResource[1]);
							alarmSenderParamter.setExt1(parentResource[0]);
							alarmSenderParamter.setExt2(parentResource[1]);
						}else{
							logger.error("因为resourceApi.getCategoryParents(res.getCategoryId()).length="+parentResource.length+",长度不大于2->所以不会设置setExt1和setExt2");
							alarmSenderParamter.setExt2(SysModuleEnum.IP_MAC_PORT.name());
						}
					}else{
						logger.error("因为resourceApi.getResource(node.getInstanceId())= null->所以不会设置setExt1和setExt2");
					}
					break;
				}
			}
			alarmSenderParamter.setExt2(SysModuleEnum.IP_MAC_PORT.name());

			//发送告警消息
			alarmService.notify(alarmSenderParamter);
		}
	}
	
	/**
	 * 解析合并【短信、邮件、Alert】告警人
	 * @param alramSetting
	 * @return List<Map<String, Object>>
	 */
	private List<Map<String, Object>> parseIpMacAlarmSenders(String alaramSetting){
		JSONObject setting = (JSONObject) JSONObject.parseObject(alaramSetting);	//告警设置信息
		JSONArray smsSenders = setting.getJSONArray("smsSenders");
		JSONArray emailSenders = setting.getJSONArray("emailSenders");
		JSONArray alertSenders = setting.getJSONArray("alertSenders");
		//短信告警发送人
		List<Map<String, Object>> smsSendsTmp = new ArrayList<Map<String,Object>>();
		for(Object sender:smsSenders){
			JSONObject send = (JSONObject) JSONObject.toJSON(sender);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", send.getLongValue("id"));
			map.put("userName", send.getString("name"));
			map.put("sendSms", true);
			map.put("sendEmail", false);
			map.put("sendAlert", false);
			smsSendsTmp.add(map);
		}
		//邮件告警发送人
		List<Map<String, Object>> emailSendsTmp = new ArrayList<Map<String,Object>>();
		for(Object sender:emailSenders){
			JSONObject send = (JSONObject) JSONObject.toJSON(sender);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", send.getLongValue("id"));
			map.put("userName", send.getString("name"));
			map.put("sendSms", false);
			map.put("sendEmail",true);
			map.put("sendAlert", false);
			emailSendsTmp.add(map);
		}
		//Alert告警发送人
		List<Map<String, Object>> alertSendsTmp = new ArrayList<Map<String,Object>>();
		for(Object sender:alertSenders){
			JSONObject send = (JSONObject) JSONObject.toJSON(sender);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", send.getLongValue("id"));
			map.put("userName", send.getString("name"));
			map.put("sendSms", false);
			map.put("sendEmail", false);
			map.put("sendAlert", true);
			alertSendsTmp.add(map);
		}
		
		//合并短信、邮件、Alert告警数据
		List<Map<String, Object>> senders = this.mergeIpMacAlarmSenders(smsSendsTmp, emailSendsTmp, alertSendsTmp);
		return senders;
	}
	
	/**
	 * 合并短信、邮件、Alert告警数据
	 * 1.合并[短信]与[邮件]接收人数据
	 * 2.合并[短信、邮件]与[Alert]接收人数据
	 * @param smsAlarmUsers
	 * @param emailAlarmUsers
	 * @param alertAlarmUsers
	 * @return
	 */
	private List<Map<String, Object>> mergeIpMacAlarmSenders(List<Map<String, Object>> smsAlarmUsers,List<Map<String, Object>> emailAlarmUsers,List<Map<String, Object>> alertAlarmUsers){
		//1.合并[短信]与[邮件]接收人数据
		for(Map<String, Object> smsUser:smsAlarmUsers){
			for(int i=emailAlarmUsers.size()-1; i>=0 ;i--){
				Map<String, Object> emailUser = emailAlarmUsers.get(i);
				if(smsUser.get("userId").toString().equals(emailUser.get("userId").toString())){
					smsUser.put("sendEmail",true);
					emailAlarmUsers.remove(i);
					break;
				}
			}
		}
		//处理不在[短信告警]而在[邮件告警]的接收人
		smsAlarmUsers.addAll(emailAlarmUsers);
		
		//2.合并[短信、邮件]与[Alert]接收人数据
		for(Map<String, Object> smsUser:smsAlarmUsers){
			for(int i=alertAlarmUsers.size()-1; i>=0 ;i--){
				Map<String, Object> alertUser = alertAlarmUsers.get(i);
				if(smsUser.get("userId").toString().equals(alertUser.get("userId").toString())){
					smsUser.put("sendAlert",true);
					alertAlarmUsers.remove(i);
					break;
				}
			}
		}
		//处理不在[短信、邮件告警]而在[邮件告警]的接收人
		smsAlarmUsers.addAll(alertAlarmUsers);
				
		return smsAlarmUsers;
	}
	
	
	/**
	 * 解析告警设置信息为告警规则
	 * @param alarmMap
	 * @param senders
	 * @return List<AlarmRule>
	 */
	private List<AlarmRule> parseToAlarmRule(Map<String, Object> alarmMap,List<Map<String, Object>> senders){
		//告警规则（方式：短信、邮件、Alert、接受人）
		List<AlarmRule> notifyRules = new ArrayList<AlarmRule>();
		AlarmLevelEnum level = (AlarmLevelEnum) alarmMap.get("metricLevel");
		for(Map<String, Object> sender:senders){
			AlarmRule rule = new AlarmRule();
			notifyRules.add(rule);
			rule.setProfileId(0);									//拓扑告警规则所属的策略id(约定写死为0)
			rule.setProfileType(AlarmRuleProfileEnum.ip_mac_port);	//告警规则绑定的策略类型
			rule.setUserId(sender.get("userId").toString());//告警接受人id
			//告警规则列表
			List<AlarmSendCondition> sendConditions = new ArrayList<AlarmSendCondition>();
			rule.setSendConditions(sendConditions);
			if((boolean)sender.get("sendSms")){
				AlarmSendCondition condition = this.createAlarmSendCondition(SendWayEnum.sms,level);
				sendConditions.add(condition);
			}
			if((boolean)sender.get("sendEmail")){
				AlarmSendCondition condition = this.createAlarmSendCondition(SendWayEnum.email,level);
				sendConditions.add(condition);
			}
			if((boolean)sender.get("sendAlert")){
				AlarmSendCondition condition = this.createAlarmSendCondition(SendWayEnum.alert,level);
				sendConditions.add(condition);
			}
		}
		
		return notifyRules;
	}
	
	private AlarmSendCondition createAlarmSendCondition(SendWayEnum sendWay,AlarmLevelEnum level){
		//告警条件
		AlarmSendCondition condition = new AlarmSendCondition();
		condition.setEnabled(true);		//是否启用
		condition.setAllTime(true);		//发送时间
		condition.setSendWay(sendWay);	//告警方式
		
		//告警规则列表
		List<AlarmLevelEnum> alarmLevels = new ArrayList<AlarmLevelEnum>();
		condition.setAlarmLevels(alarmLevels);
		//告警规则
		alarmLevels.add(level);
		
		return condition;
	}
		
	@Override
	public void updateAlarmConditon(long ruleId,AlarmSendCondition condition) {
		alarmRuleService.updateAlarmConditon(ruleId, condition);
	}
	
	@Override
	public void changeLinkAlarmConditionEnabled(List<AlarmConditonEnableInfo> enableInfos) {
		alarmRuleService.changeAlarmConditionEnabled(enableInfos);
	}
	
	@Override
	public void deleteAlarmSetting(long[] ids) {
		alarmRuleService.deleteAlarmRuleById(ids);
	}

	/**
	 * 获取告警人
	 * 1.获取所有系统用户
	 * 2.根据业务获取相应已有的用户
	 * 3.过滤数据
	 * @param alarmType 告警类型（linkalarm、ipmacport）
	 * @return List<User>
	 */
	@Override
	public List<User> getAlarmSenders(String alarmType,String type) {
		// 1.获取所有系统用户
		List<User> senders = userApi.queryAllUserNoPage();
		
		// 2.根据业务获取相应已有的用户
		if(StringUtils.isNotBlank(alarmType)){
			switch (alarmType) {
			case "linkalarm":
				//获取链路告警设置已经发送的用户
				List<AlarmRule> alarmRules = getLinkAlarmSetting();
				
				// 3.过滤数据
				for(AlarmRule alarm:alarmRules){
					for(int i=senders.size()-1; i>=0 ;i--){
						User user = senders.get(i);
						if(Long.valueOf(alarm.getUserId()).longValue() == user.getId().longValue()){
							senders.remove(i);
							break;
						}
					}
				}
				break;
			case "ipmacport"://获取ipmacport告警设置用户
				if("email".equals(type)){
					for(int i=senders.size()-1; i>=0 ;i--){
						User user = senders.get(i);
						if(StringUtils.isBlank(user.getEmail())){	//去掉没有注册邮件的用户
							senders.remove(i);
						}
					}
				}else if("sms".equals(type)){
					for(int i=senders.size()-1; i>=0 ;i--){
						User user = senders.get(i);
						if(StringUtils.isBlank(user.getMobile())){	//去掉没有注册手机号码的用户
							senders.remove(i);
						}
					}
				}
				break;
			default:break;
			}
		}
		
		return senders;
	}

	@Override
	public List<AlarmRule> getLinkAlarmSetting() {
		//拓扑链路告警规则所属的策略id(约定写死为0)
		return alarmRuleService.getAlarmRulesByProfileId(0, AlarmRuleProfileEnum.link);
	}

	/**
	 * 保存链路告警设置信息
	 * @param alarmSetting
	 */
	@Override
	public void saveLinkAlarmSetting(AlarmRule alarmRule) {
		alarmRuleService.addAlarmRule(alarmRule);
	}
}
