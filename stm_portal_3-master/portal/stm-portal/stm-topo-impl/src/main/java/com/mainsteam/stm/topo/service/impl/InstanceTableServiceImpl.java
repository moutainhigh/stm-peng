package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonEnableInfo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmLevelEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.ContinusUnitEnum;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.topo.api.IInstanceTableApi;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.api.ISettingApi;
import com.mainsteam.stm.topo.api.LinkService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.api.TopoAlarmExApi;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.MapLineDao;
import com.mainsteam.stm.util.UnitTransformUtil;
import com.mainsteam.stm.util.Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <li>拓扑发现业务实现</li>
 * <li>文件名称: TopoDiscoveryServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月15日
 * @author zwx
 */
@Service
public class InstanceTableServiceImpl extends ThirdServiceBase implements IInstanceTableApi{
	private final Logger logger = LoggerFactory.getLogger(InstanceTableServiceImpl.class);
	@Autowired
	private INodeDao nodeDao;
	@Autowired
	private ILinkDao linkDao;
	@Autowired
	private TopoAlarmExApi thirdService2;
	@Autowired
	private IResourceInstanceExApi resourceExApi;
	@Autowired
	private MapLineDao mapLineDao;
	@Autowired
	private ThirdService thirdSvc;
    @Autowired
    private ISettingApi settingApi;
    @Autowired
    private LinkService linkService;
    private final String defaultConstant = "- -";	//默认字符串常量
	
	/**
	 * 修改链路告规则
	 * 1.解析告警数据
	 * 2.修改告警规则
	 * @param alarmRules
	 */
	@Override
	public void updateAlarmRules(String alarmRules) {
		//1.解析告警数据
		JSONObject setting = (JSONObject) JSONObject.parse(alarmRules);
		AlarmSendCondition condition = parseEditAlarmCondition(setting);
		
		//2.保存告警规则
		thirdService2.updateAlarmConditon(setting.getLongValue("ruleId"), condition);
	}

	/**
	 * 解析封装告警条件
	 * @param setting
	 * @return
	 */
	private AlarmSendCondition parseEditAlarmCondition(JSONObject setting) {
		AlarmSendCondition condition = new AlarmSendCondition();
		condition.setEnabled(setting.getBooleanValue("enable"));
		condition.setContinus(setting.getBooleanValue("send"));
		condition.setContinusCount(setting.getIntValue("continus"));
		
		List<AlarmLevelEnum> alarmLevels = new ArrayList<AlarmLevelEnum>();
		if(setting.getBooleanValue("down")) alarmLevels.add(AlarmLevelEnum.down);
		if(setting.getBooleanValue("error")) alarmLevels.add(AlarmLevelEnum.metric_error);
		if(setting.getBooleanValue("warn")) alarmLevels.add(AlarmLevelEnum.metric_warn);
		if(setting.getBooleanValue("unknown")) alarmLevels.add(AlarmLevelEnum.metric_unkwon);
		if(setting.getBooleanValue("recover")) alarmLevels.add(AlarmLevelEnum.metric_recover);
        if (setting.getBooleanValue("metricRecover")) alarmLevels.add(AlarmLevelEnum.perf_metric_recover);
        condition.setAlarmLevels(alarmLevels);
		
		switch (setting.getString("sendWay")) {
			case "sms":condition.setSendWay(SendWayEnum.sms);break;
			case "email":condition.setSendWay(SendWayEnum.email);break;
			case "alert":condition.setSendWay(SendWayEnum.alert);break;
		}
		switch (setting.getString("continusUnit")) {
			case "minute":condition.setContinusCountUnit(ContinusUnitEnum.minute);break;
			case "hour":condition.setContinusCountUnit(ContinusUnitEnum.hour);break;
		}
		return condition;
	}
	
	/**
	 * 改变链路告规则发送条件是否启用
	 */
	@Override
	public void changeLinkAlarmConditionEnabled(String enables) {
		JSONArray ens = JSONArray.parseArray(enables);
		List<AlarmConditonEnableInfo> enableInfos = new ArrayList<AlarmConditonEnableInfo>();
		for(Object enable:ens){
			JSONObject en = (JSONObject) JSONObject.toJSON(enable);
			AlarmConditonEnableInfo enableInfo = new AlarmConditonEnableInfo();
			enableInfo.setRuleId(en.getLongValue("ruleId"));
			enableInfo.setEnabled(en.getBooleanValue("enabled"));
			switch (en.getString("sendWay")) {
			case "sms":enableInfo.setSendWay(SendWayEnum.sms);break;
			case "email":enableInfo.setSendWay(SendWayEnum.email);break;
			case "alert":enableInfo.setSendWay(SendWayEnum.alert);break;
			}
			enableInfos.add(enableInfo);
		}
		
		thirdService2.changeLinkAlarmConditionEnabled(enableInfos);
	}

	/**
	 * 获取链路告警设置信息
	 * 1. 获取所有用户链路告警设置信息
	 * 2. 解析用户
	 * @return JSONArray
	 */
	@Override
	public JSONArray getLinkAlarmSetting() {
		//1. 获取所有用户链路告警设置信息
		List<AlarmRule> alarmRules = thirdService2.getLinkAlarmSetting();
		
		//2.1  解析用户名字
		JSONArray alarms = new JSONArray();
		for(AlarmRule alarmRule:alarmRules){
			JSONObject sender = (JSONObject) JSONObject.toJSON(alarmRule);
			long userId = Long.valueOf(alarmRule.getUserId());
			User user = userApi.get(userId);
			sender.put("userName", user==null?"":user.getName());
			alarms.add(sender);
		}
		
		return alarms;
	}
	
	/**
	 * 保存链路告警设置信息
	 * 1. 删除已有告警规则
	 * 1. 解析告警数据
	 * 2. 调用接口保存告警设置信息
	 * @param alarmSetting
	 */
	@Override
	public void saveLinkAlarmSetting(String alarmSetting) {
		//1. 解析告警数据
		List<JSONObject> walarmRules = this.getAddAlarmRules(alarmSetting);
		
		//2. 调用接口保存告警设置信息
		for(Object alarm:walarmRules){
			JSONObject alarmTmp = (JSONObject) JSONObject.toJSON(alarm);
			AlarmRule alarmRule = this.parseToAlarmRule(alarmTmp);
			thirdService2.saveLinkAlarmSetting(alarmRule);
		}
	}
	
	/**
	 * 解析获取告新增警规则列表
	 * 1.解析告警数据
	 * 2.合并短信、邮件、Alert告警数据
	 * @param alarmSetting
	 * @return
	 */
	private List<JSONObject> getAddAlarmRules(String alarmSetting){
		//1. 解析告警数据
		JSONObject setting = (JSONObject) JSONObject.parse(alarmSetting);
		List<JSONObject> smsAlarmUsers = this.parseAlarmSenders(setting.getJSONObject("sms"),"sms");
		List<JSONObject> emailAlarmUsers = this.parseAlarmSenders(setting.getJSONObject("email"),"email");
		List<JSONObject> alertAlarmUsers = this.parseAlarmSenders(setting.getJSONObject("alert"),"alert");
		
		//2.合并短信、邮件、Alert告警数据
		return this.mergeAlarmRules(smsAlarmUsers, emailAlarmUsers, alertAlarmUsers);
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
	private List<JSONObject> mergeAlarmRules(List<JSONObject> smsAlarmUsers,List<JSONObject> emailAlarmUsers,List<JSONObject> alertAlarmUsers){
		//1.合并[短信]与[邮件]接收人数据
		for(JSONObject smsUser:smsAlarmUsers){
			for(int i=emailAlarmUsers.size()-1; i>=0 ;i--){
				JSONObject emailUser = emailAlarmUsers.get(i);
				if(smsUser.getLongValue("userId") == emailUser.getLongValue("userId")){
					smsUser.put("email", emailUser.getJSONObject("email"));
					emailAlarmUsers.remove(i);
					break;
				}
			}
		}
		//处理不在[短信告警]而在[邮件告警]的接收人
		for(JSONObject emailUser:emailAlarmUsers){
			smsAlarmUsers.add(emailUser);
		}
		
		//2.合并[短信、邮件]与[Alert]接收人数据
		for(JSONObject smsUser:smsAlarmUsers){
			for(int i=alertAlarmUsers.size()-1; i>=0 ;i--){
				JSONObject alertUser = alertAlarmUsers.get(i);
				if(smsUser.getLongValue("userId") == alertUser.getLongValue("userId")){
					smsUser.put("alert", alertUser.getJSONObject("alert"));
					alertAlarmUsers.remove(i);
					break;
				}
			}
		}
		//处理不在[短信、邮件告警]而在[邮件告警]的接收人
		for(JSONObject alertlUser:alertAlarmUsers){
			smsAlarmUsers.add(alertlUser);
		}
				
		return smsAlarmUsers;
	}
	
	/**
	 * 解析链路告警设置信息为告警人告警信息列表
	 * @param alarmRule
	 * @param alarmWay
	 * @return
	 */
	private List<JSONObject> parseAlarmSenders(JSONObject alarmRule,String alarmWay){
		List<JSONObject> alarmSenders = new ArrayList<JSONObject>();
		for(Object user:alarmRule.getJSONArray("senders")){
			JSONObject alarm = new JSONObject();
			alarm.put("enable", alarmRule.getBooleanValue("enable"));
			alarm.put("send", alarmRule.getJSONObject("condition").getBooleanValue("send"));
			alarm.put("continus", alarmRule.getJSONObject("condition").getIntValue("continus"));
			alarm.put("continusUnit", alarmRule.getJSONObject("condition").getString("continusUnit"));
			alarm.put("down", alarmRule.getJSONObject("rules").getBooleanValue("down"));
			alarm.put("error", alarmRule.getJSONObject("rules").getBooleanValue("error"));
			alarm.put("warn", alarmRule.getJSONObject("rules").getBooleanValue("warn"));
			alarm.put("unknown", alarmRule.getJSONObject("rules").getBooleanValue("unknown"));
			alarm.put("recover", alarmRule.getJSONObject("rules").getBooleanValue("recover"));
            alarm.put("metricRecover", alarmRule.getJSONObject("rules").getBooleanValue("metricRecover"));

			JSONObject smsUser  = new JSONObject();
			JSONObject userT = (JSONObject) JSONObject.toJSON(user);
			smsUser.put("userId", userT.getString("id"));
			smsUser.put("userName", userT.getString("name"));
			smsUser.put(alarmWay, alarm);
			
			switch (alarmWay) {
			case "sms":
				smsUser.put("email", getDefaultAlarmRule());
				smsUser.put("alert", getDefaultAlarmRule());
				break;
			case "email":
				smsUser.put("sms", getDefaultAlarmRule());
				smsUser.put("alert", getDefaultAlarmRule());
				break;
			case "alert":
				smsUser.put("sms", getDefaultAlarmRule());
				smsUser.put("email", getDefaultAlarmRule());
				break;
			default:
				break;
			}
			alarmSenders.add(smsUser);
		}
		return alarmSenders;
	}
	
	/**
	 * 获取默认告警规则
	 * @return
	 */
	private JSONObject getDefaultAlarmRule(){
		JSONObject alarm = new JSONObject();
		alarm.put("enable", false);
		alarm.put("send", false);
		alarm.put("continus", 1);
		alarm.put("continusUnit", "minute");
		alarm.put("down", false);
		alarm.put("error", false);
		alarm.put("warn", false);
		alarm.put("unkonwn", false);
		alarm.put("recover", false);
        alarm.put("metricRecover", false);
        return alarm;
	}
	
	/**
	 * 解析告警数据,组装成AlarmRule对象
	 * @param warnRule
	 * @return AlarmRule
	 */
	private AlarmRule parseToAlarmRule(JSONObject warnRule) {
		List<AlarmSendCondition> sendConditions = new ArrayList<AlarmSendCondition>();	//告警规则列表
		AlarmSendCondition smsCondition = this.setAlarmCondition(warnRule.getJSONObject("sms"));
		smsCondition.setSendWay(SendWayEnum.sms);
		smsCondition.setAllTime(true);		//24*7小时发送
		AlarmSendCondition emailCondition = this.setAlarmCondition(warnRule.getJSONObject("email"));
		emailCondition.setSendWay(SendWayEnum.email);
		emailCondition.setAllTime(true);	//24*7小时发送
		AlarmSendCondition alertCondition = this.setAlarmCondition(warnRule.getJSONObject("alert"));
		alertCondition.setSendWay(SendWayEnum.alert);
		alertCondition.setAllTime(true);	//24*7小时发送
		sendConditions.add(smsCondition);
		sendConditions.add(emailCondition);
		sendConditions.add(alertCondition);
		
		AlarmRule alarmRule = new AlarmRule();
		alarmRule.setUserId(warnRule.getString("userId"));
		alarmRule.setProfileId(0);								//拓扑链路所属的策略id(约定写死为0)
		alarmRule.setProfileType(AlarmRuleProfileEnum.link);	//告警规则绑定的策略类型
		alarmRule.setSendConditions(sendConditions);
		return alarmRule;
	}
	
	/**
	 * 设置告警条件
	 * @param rule
	 * @return
	 */
	private AlarmSendCondition setAlarmCondition(JSONObject alarmRule){
		AlarmSendCondition condition = new AlarmSendCondition();
		condition.setEnabled(alarmRule.getBooleanValue("enable"));			//是否启用
		condition.setContinus(alarmRule.getBooleanValue("send"));			//是否定时间隔发送
		condition.setContinusCount(alarmRule.getIntValue("continus"));		//间隔时间
		if("minute".equals(alarmRule.getString("continusUnit"))){			//间隔单位(分钟、小时)
			condition.setContinusCountUnit(ContinusUnitEnum.minute);
		}else{
			condition.setContinusCountUnit(ContinusUnitEnum.hour);
		}
		
		List<AlarmLevelEnum> alarmLevels = new ArrayList<AlarmLevelEnum>();
		if(alarmRule.getBooleanValue("down")) alarmLevels.add(AlarmLevelEnum.down);					//链路不可用（级别：致命）
		if(alarmRule.getBooleanValue("error")) alarmLevels.add(AlarmLevelEnum.metric_error);		//链路性能指标违反红色阈值（级别：严重）
		if(alarmRule.getBooleanValue("warn")) alarmLevels.add(AlarmLevelEnum.metric_warn);			//链路性能指标违反黄色阈值（级别：警告）
		if(alarmRule.getBooleanValue("unknown")) alarmLevels.add(AlarmLevelEnum.metric_unkwon);		//链路未知（级别：未知）
		if(alarmRule.getBooleanValue("recover")) alarmLevels.add(AlarmLevelEnum.metric_recover);	//链路性能恢复（信息）
        if (alarmRule.getBooleanValue("metricRecover"))
            alarmLevels.add(AlarmLevelEnum.perf_metric_recover);    //链路指标恢复正常（级别：正常）
        condition.setAlarmLevels(alarmLevels);
		
		return condition;
	}
	
	/**
	 * 转换资源实例为链路
	 * @param linkList
	 * @param instances		资源实例
	 * @param instanceIds	取值接口实例id
	 * @param instanceColl	资源实例id-取值接口id
	 * @return List<LinkBo>
	 * @throws InstancelibException 
	 * @throws NumberFormatException 
	 */
	private List<LinkBo> parseInstanceToLinkNode(List<LinkBo> linkList,List<ResourceInstance> instances,long[] instanceIds,Map<Long,Long> instanceColl) throws NumberFormatException, InstancelibException{
		//批量获取【性能指标】值
		String[] metricIds = {MetricIdConsts.METRIC_CPU_RATE,MetricIdConsts.METRIC_MEME_RATE,"ifInOctetsSpeed","ifOutOctetsSpeed","ifOutBandWidthUtil","ifInBandWidthUtil"};
		List<Map<String, ?>> merticMaps = resourceExApi.getMerictRealTimeVals(metricIds, instanceIds);
		ResourceMetricDef[] resourceMetricDefs = resourceExApi.getMetricDefs(instances.get(0));	//资源指标定义列表
		Map<String,String> metricsUnit = resourceExApi.getMetricsUnitMap(resourceMetricDefs);	//所有资源指标单位map
		List<LinkBo> links = new ArrayList<LinkBo>();
        /*BUG #48512 【告警优化】拓扑一览表中链路状态显示不正确 huangping 2017/12/4 start*/
        String setting = settingApi.getCfg("globalSetting");
        String metricId = "device";
        if (StringUtils.isNotBlank(setting) && !"{}".equals(setting)) {
            JSONObject tmp = JSONObject.parseObject(setting);
            JSONObject link = tmp.getJSONObject("link");
            String colorWarning = link.getString("colorWarning");
            metricId = colorWarning;
        }
        /*BUG #48512 【告警优化】拓扑一览表中链路状态显示不正确 huangping 2017/12/4 start*/
		for(ResourceInstance instance:instances){
			LinkBo link = new LinkBo();
			long insId = instance.getId();	//资源实例id
			link.setInstanceId(insId);
			for(LinkBo linkBo:linkList){
				if(linkBo.getInstanceId().longValue() == instance.getId()){
					link.setId(linkBo.getId());		//节点id
					link.setDownDirect(linkBo.isDirection()?"源->目的":"目的->源");
					break;
				}
			}
			link.setSrcMainInstIP(resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_SRC_MAININST_IP));		//源IP地址
			link.setDestMainInstIP(resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_DEST_MAININST_IP));	//目的IP地址
			String linkState = resourceExApi.getResourceLinkStateColor(instance,"link");
//			link.setInsStatus(linkState);							//链路状态（状态来自链路）
			
			//*******************链路取值来自取值接口  start****************************
//			ResourceInstance collInstance = resourceService.getResourceInstance(instanceColl.get(insId));			//取值接口实例
//			link.setInsStatus(resourceExApi.getResourceLinkStateColor(collInstance,"link"));						//链路状态(与取值接口一致)
			//*******************链路取值来自取值接口  end****************************
			
			link.setSrcMainInstName(resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_SRC_IFNAME));		//源接口名称
			String srcSubInstId = resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_SRC_SUBINST_ID);		//源接口id
			logger.error("链路 instanceId="+instance.getId()+"  ,srcSubInstId="+srcSubInstId);
			ResourceInstance srcIfInstance = null;
			if(!defaultConstant.equals(srcSubInstId)){
				long srcIfIdT = Long.valueOf(srcSubInstId);	
				srcIfInstance = resourceService.getResourceInstance(srcIfIdT);
			}
			link.setSrcIfColor(resourceExApi.getResourceLinkStateColor(srcIfInstance,"interface"));					//源接口状态颜色
			
			link.setDestMainInsName(resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_DEST_IFNAME));		//目的接口名称
			String destSubInstId = resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_DEST_SUBINST_ID);		//目的接口id
			logger.error("链路 instanceId="+instance.getId()+"  ,destSubInstId="+destSubInstId);
			ResourceInstance destIfInstance = null;
			if(!defaultConstant.equals(destSubInstId)){
				long destIfIdT = Long.valueOf(destSubInstId);
				destIfInstance = resourceService.getResourceInstance(destIfIdT);
			}
			link.setDestIfColor(resourceExApi.getResourceLinkStateColor(destIfInstance,"interface"));				//目的接口状态颜色
            /*BUG #48512 【告警优化】拓扑一览表中链路状态显示不正确 huangping 2017/12/4 start*/
//            link.setInsStatus(resourceExApi.convertLinkStateColor(instance,srcIfInstance,destIfInstance));			//链路状态来自两端最高级别状态
//            link.setInsStatus(resourceExApi.getResourceLinkStateColor(instance, "link"));
            JSONArray objects = linkService.convertLinkState(new Long[]{link.getId()}, metricId);
            String color = "";
            if (objects.size() > 0) {
                JSONObject jsonObject = (JSONObject) objects.get(0);
                color = (String) jsonObject.get("state");
                if (InstanceStateEnum.UNKOWN.name().equalsIgnoreCase(color) || InstanceStateEnum.NORMAL.name().equalsIgnoreCase(color)) {
                    color = "green";        //正常
                } else if (InstanceStateEnum.CRITICAL.name().equalsIgnoreCase(color)) {
                    color = "disabled";        //致命（断开红叉）
                } else if (InstanceStateEnum.SERIOUS.name().equalsIgnoreCase(color)) {
                    color = "red";            //严重（超负荷）
                } else if (InstanceStateEnum.WARN.name().equalsIgnoreCase(color)) {
                    color = "yellow";        //警告（警戒负载）
                } else if (InstanceStateEnum.NORMAL.name().equalsIgnoreCase(color)) {
                    color = "green";        //正常
                }
            }
            link.setInsStatus(color);
            /*BUG #48512 【告警优化】拓扑一览表中链路状态显示不正确 huangping 2017/12/4 end*/
			Map<String, String> directMap = this.parseDirectVals(instance);				
			link.setGetValInterface(directMap.get("getValInterface"));					//取值接口
			
			if(!"unmonitor".equals(linkState)){
				for (Map<String, ?> metric: merticMaps) {
					long metricInstanceId = Long.valueOf(metric.get("instanceid").toString()).longValue();
//					if (instanceColl.get(insId) == metricInstanceId) {//链路取值来自取值接口
					if (insId == metricInstanceId) {//链路取值来自链路
						link.setIfInOctetsSpeed(resourceExApi.getMetricVal(metric,"ifInOctetsSpeed", metricsUnit));			//上行流量
						link.setIfOutOctetsSpeed(resourceExApi.getMetricVal(metric,"ifOutOctetsSpeed", metricsUnit));		//下行流量
						link.setIfInBandWidthUtil(resourceExApi.getMetricVal(metric,"ifInBandWidthUtil", metricsUnit));		//上行带宽利用率
						link.setIfOutBandWidthUtil(resourceExApi.getMetricVal(metric,"ifOutBandWidthUtil", metricsUnit));	//下行带宽利用率
						String rateUnit = StringUtils.isNotBlank(metricsUnit.get("ifBroadPktsRate"))?metricsUnit.get("ifBroadPktsRate"):"包/秒";
						link.setBroadPktsRate(resourceExApi.getMetricVal(metricInstanceId, MetricTypeEnum.InformationMetric, "ifBroadPktsRate",rateUnit));			//广播包率
						//链路带宽取值来自取值接口
						link.setBandWidth(resourceExApi.getMetricVal(instanceColl.get(insId), MetricTypeEnum.InformationMetric, "ifSpeed", metricsUnit.get("ifSpeed")));	//链路带宽
						//链路带宽取值来自链路
//						link.setBandWidth(resourceExApi.getMetricVal(metricInstanceId, MetricTypeEnum.InformationMetric, "ifSpeed", metricsUnit.get("ifSpeed")));	//链路带宽
						break;
					}
				}
			}
			
			links.add(link);
		}
		return links;
	}
	
	/**
	 * 转换资源实例为链路
	 * @param linkList
	 * @param instances		资源实例
	 * @param instanceIds	取值接口实例id
	 * @param instanceColl	资源实例id-取值接口id
	 * @return List<LinkBo>
	 * @throws InstancelibException 
	 * @throws NumberFormatException 
	 */
	private List<LinkBo> homeParseInstanceToLinkNode(List<LinkBo> linkList,List<ResourceInstance> instances,long[] instanceIds,Map<Long,Long> instanceColl) throws NumberFormatException, InstancelibException{
		//批量获取【性能指标】值
//		String[] metricIds = {MetricIdConsts.METRIC_CPU_RATE,MetricIdConsts.METRIC_MEME_RATE,"ifInOctetsSpeed","ifOutOctetsSpeed","ifOutBandWidthUtil","ifInBandWidthUtil"};
//		List<Map<String, ?>> merticMaps = resourceExApi.getMerictRealTimeVals(metricIds, instanceIds);
//		ResourceMetricDef[] resourceMetricDefs = resourceExApi.getMetricDefs(instances.get(0));	//资源指标定义列表
//		Map<String,String> metricsUnit = resourceExApi.getMetricsUnitMap(resourceMetricDefs);	//所有资源指标单位map
		List<LinkBo> links = new ArrayList<LinkBo>();
		for(ResourceInstance instance:instances){
			LinkBo link = new LinkBo();
			long insId = instance.getId();	//资源实例id
			link.setInstanceId(insId);
			for(LinkBo linkBo:linkList){
				if(linkBo.getInstanceId().longValue() == instance.getId()){
					link.setId(linkBo.getId());		//节点id
					link.setDownDirect(linkBo.isDirection()?"源->目的":"目的->源");
					break;
				}
			}
			link.setSrcMainInstIP(resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_SRC_MAININST_IP));		//源IP地址
			link.setDestMainInstIP(resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_DEST_MAININST_IP));	//目的IP地址
//			String linkState = resourceExApi.getResourceLinkStateColor(instance,"link");
//			link.setInsStatus(linkState);							//链路状态（状态来自链路）
			
			//*******************链路取值来自取值接口  start****************************
//			ResourceInstance collInstance = resourceService.getResourceInstance(instanceColl.get(insId));			//取值接口实例
//			link.setInsStatus(resourceExApi.getResourceLinkStateColor(collInstance,"link"));						//链路状态(与取值接口一致)
			//*******************链路取值来自取值接口  end****************************
			
			link.setSrcMainInstName(resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_SRC_IFNAME));		//源接口名称
//			String srcSubInstId = resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_SRC_SUBINST_ID);		//源接口id
//			logger.error("链路 instanceId="+instance.getId()+"  ,srcSubInstId="+srcSubInstId);
//			ResourceInstance srcIfInstance = null;
//			if(!defaultConstant.equals(srcSubInstId)){
//				long srcIfIdT = Long.valueOf(srcSubInstId);	
//				srcIfInstance = resourceService.getResourceInstance(srcIfIdT);
//			}
//			link.setSrcIfColor(resourceExApi.getResourceLinkStateColor(srcIfInstance,"interface"));					//源接口状态颜色
			
			link.setDestMainInsName(resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_DEST_IFNAME));		//目的接口名称
//			String destSubInstId = resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_DEST_SUBINST_ID);		//目的接口id
//			logger.error("链路 instanceId="+instance.getId()+"  ,destSubInstId="+destSubInstId);
//			ResourceInstance destIfInstance = null;
//			if(!defaultConstant.equals(destSubInstId)){
//				long destIfIdT = Long.valueOf(destSubInstId);
//				destIfInstance = resourceService.getResourceInstance(destIfIdT);
//			}
//			link.setDestIfColor(resourceExApi.getResourceLinkStateColor(destIfInstance,"interface"));				//目的接口状态颜色
			
//			link.setInsStatus(resourceExApi.convertLinkStateColor(instance,srcIfInstance,destIfInstance));			//链路状态来自两端最高级别状态
			
//			Map<String, String> directMap = this.parseDirectVals(instance);				
//			link.setGetValInterface(directMap.get("getValInterface"));					//取值接口
			
//			if(!"unmonitor".equals(linkState)){
//				for (Map<String, ?> metric: merticMaps) {
//					long metricInstanceId = Long.valueOf(metric.get("instanceid").toString()).longValue();
//					if (instanceColl.get(insId) == metricInstanceId) {//链路取值来自取值接口
//					if (insId == metricInstanceId) {//链路取值来自链路
//						link.setIfInOctetsSpeed(resourceExApi.getMetricVal(metric,"ifInOctetsSpeed", metricsUnit));			//上行流量
//						link.setIfOutOctetsSpeed(resourceExApi.getMetricVal(metric,"ifOutOctetsSpeed", metricsUnit));		//下行流量
//						link.setIfInBandWidthUtil(resourceExApi.getMetricVal(metric,"ifInBandWidthUtil", metricsUnit));		//上行带宽利用率
//						link.setIfOutBandWidthUtil(resourceExApi.getMetricVal(metric,"ifOutBandWidthUtil", metricsUnit));	//下行带宽利用率
//						String rateUnit = StringUtils.isNotBlank(metricsUnit.get("ifBroadPktsRate"))?metricsUnit.get("ifBroadPktsRate"):"包/秒";
//						link.setBroadPktsRate(resourceExApi.getMetricVal(metricInstanceId, MetricTypeEnum.InformationMetric, "ifBroadPktsRate",rateUnit));			//广播包率
						//链路带宽取值来自取值接口
//						link.setBandWidth(resourceExApi.getMetricVal(instanceColl.get(insId), MetricTypeEnum.InformationMetric, "ifSpeed", metricsUnit.get("ifSpeed")));	//链路带宽
						//链路带宽取值来自链路
//						link.setBandWidth(resourceExApi.getMetricVal(metricInstanceId, MetricTypeEnum.InformationMetric, "ifSpeed", metricsUnit.get("ifSpeed")));	//链路带宽
//						break;
//					}
//				}
//			}
			
			links.add(link);
		}
		return links;
	}
	
	/**
	 * 解析有关方向的值（下行方向、取值接口）
	 * @param instance
	 * @return
	 */
	private Map<String, String> parseDirectVals(ResourceInstance instance){
		Map<String, String> rstMap = new HashMap<String, String>();
		rstMap.put("getValInterface", "");		//取值接口
		
		String srcSubInstId = resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_SRC_SUBINST_ID);	//源接口id
		String destSubInstId = resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_DEST_SUBINST_ID);	//目的接口id
		String collSubInstId = resourceExApi.getPropVal(instance, LinkResourceConsts.PROP_COLL_SUBINST_ID);	//取值设备id（决定取值接口）
		
		//取值接口
		if(StringUtils.isNotBlank(collSubInstId)){
			if(defaultConstant.equals(collSubInstId)){
				rstMap.put("getValInterface", defaultConstant);
			}else if(StringUtils.isNotBlank(srcSubInstId) && srcSubInstId.equals(collSubInstId)){
				rstMap.put("getValInterface", "源接口");
			}else if(StringUtils.isNotBlank(destSubInstId) && destSubInstId.equals(collSubInstId)){
				rstMap.put("getValInterface", "目的接口");
			}
		}
		
		return rstMap;
	}
	
	/**
	 * 分页查询资源实例发现的链路（网络设备之间连线）列表
	 */
	private List<LinkBo> getLinksByPage(Page<LinkBo, LinkBo> page) {
		//页面查询参数
		String searchVal = page.getCondition().getSearchVal();
		
		List<LinkBo>  linkList = null;
		if(StringUtils.isNotBlank(searchVal) || StringUtils.isNotBlank(page.getSort())){
			//有条件的查询需要查询所有资源实例，然后再根据条件从中搜索过滤
			linkList = linkDao.getAllLinkInstances();
		}else{
			//无条件查询
			linkDao.selectByPage(page);
			linkList = page.getDatas();
		}
		return linkList;
	}
	
	/**
	 * 根据资源实例ids获取链路数据
	 * 1. 根据资源实例ids获取节点链路
	 * 2. 根据ids查询资源实例列表
	 * 3. 封装转换指标等数据
	 * @param resourceIds
	 * @return List<LinkBo>
	 * @throws InstancelibException 
	 */
	public List<LinkBo> getLinksByInstanceIds(long[] resourceIds,String type) throws InstancelibException{
		//1. 根据资源实例ids获取节点链路
		List<LinkBo>  linkList = new ArrayList<LinkBo>();
		if(StringUtils.isBlank(type)){
			linkList = linkDao.getLinksByInstanceIds(resourceIds);
		}else if("map".equals(type)){
			List<MapLineBo> mapNodeList = mapLineDao.findByInstanceIds(resourceIds);
			for(MapLineBo line:mapNodeList){	//转换成linkbo
				LinkBo link = new LinkBo();
				link.setId(line.getId());
				link.setInstanceId(line.getInstanceId());
				linkList.add(link);
			}
		}
		if(null == linkList || linkList.size() == 0) return linkList;
		
		//2. 根据ids查询资源实例列表
		List<Long> instanceIds = new ArrayList<Long>();
		for(long resourceId:resourceIds){
			instanceIds.add(resourceId);
		}
		List<ResourceInstance> instances = resourceService.getResourceInstances(instanceIds);
		
		//3. 封装转换指标等数据
		Map<Long,Long> parentChildMap = new HashMap<Long, Long>();
//		long[] collInstanceIds = new long[instances.size()];			//资源实例ids
		for(int i=0;i<instances.size();i++){
			ResourceInstance parentInstance = instances.get(i);
			String collSubInstId = resourceExApi.getPropVal(parentInstance, LinkResourceConsts.PROP_COLL_SUBINST_ID);	//取值设备id（决定取值接口）
			long collResourceId = -1;
			if(!defaultConstant.equals(collSubInstId)){
				collResourceId = Long.valueOf(collSubInstId);
			}
			parentChildMap.put(parentInstance.getId(), collResourceId);		//链路实例id--取值接口实例id
//			collInstanceIds[i] = collResourceId;
		}
		
		//*******************链路取值来自取值接口  start****************************
//		linkList = this.parseInstanceToLinkNode(linkList, instances,collInstanceIds,parentChildMap);//取值接口实例ids
		//*******************链路取值来自取值接口  end****************************
		
		//*******************链路取值来自链路  start****************************
		linkList = this.parseInstanceToLinkNode(linkList, instances,resourceIds,parentChildMap);	//链路实例ids
		//*******************链路取值来自链路  end****************************
		
		return linkList;
	}

	/**
	 * 根据条件分页查询资源链路列表
	 * 1. 查询链路的资源实例ids
	 * 2. 根据ids查询资源实例列表
	 * 3. 根据查询条件【源/目的ip、显示链接PC链路】过滤数据
	 * 4. 封装转换指标等数据
	 * @param page
	 * @return List<LinkBo>
	 * @throws InstancelibException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void selectLinkByPage(Page<LinkBo, LinkBo> page)throws InstancelibException {
		// 1.查询链路的资源实例ids
		List<LinkBo>  linkList = this.getLinksByPage(page);
		if(null == linkList || linkList.size() == 0) return;

		long[] instanceIds = new long[linkList.size()];
		List<Long>  resourceIds = new ArrayList<Long>();
		for(int i=0;i<linkList.size();i++){
			Long instanceId = linkList.get(i).getInstanceId();
			resourceIds.add(instanceId);
			instanceIds[i] = instanceId;
		}
		//2. 根据ids查询资源实例列表
		List<ResourceInstance> instances = resourceService.getResourceInstances(resourceIds);
		
		//3. 根据查询条件【源/目的ip、显示链接PC链路】过滤数据
		Object[] instancTemps = this.filterResourceLinkList(page, instances);
		
		//4. 封装转换指标等数据
		instances = ((List<ResourceInstance>)instancTemps[0]);	//资源实例列表
		if(null != instances && instances.size()> 0){
			Map<Long,Long> parentChildMap = (Map<Long, Long>) instancTemps[2];	//资源实例id--取值接口id
			
			//*******************链路取值来自取值接口  start**************************
//			instanceIds = (long[]) instancTemps[1];		//取值接口ids
			//*******************链路取值来自取值接口  end****************************
			
			List<LinkBo> linkBos = this.parseInstanceToLinkNode(linkList, instances,instanceIds,parentChildMap);
			page.setDatas(linkBos);
		}else{
			page.setTotalRecord(0);
			page.setDatas(null);
		}
	}
	
	/**
	 * 首页根据条件分页查询资源链路列表
	 * 1. 查询链路的资源实例ids
	 * 2. 根据ids查询资源实例列表
	 * 3. 根据查询条件【源/目的ip、显示链接PC链路】过滤数据
	 * 4. 封装转换指标等数据
	 * @param page
	 * @return List<LinkBo>
	 * @throws InstancelibException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void homeSelectLinkByPage(Page<LinkBo, LinkBo> page)throws InstancelibException {
		// 1.查询链路的资源实例ids
		List<LinkBo>  linkList = this.getLinksByPage(page);
		if(null == linkList || linkList.size() == 0) return;
		
		
		long[] instanceIds = new long[linkList.size()];
		List<Long>  resourceIds = new ArrayList<Long>();
		for(int i=0;i<linkList.size();i++){
			Long instanceId = linkList.get(i).getInstanceId();
			resourceIds.add(instanceId);
			instanceIds[i] = instanceId;
		}
		//2. 根据ids查询资源实例列表
		List<ResourceInstance> instances = resourceService.getResourceInstances(resourceIds);
		
		//3. 根据查询条件【源/目的ip、显示链接PC链路】过滤数据
		Object[] instancTemps = this.filterResourceLinkList(page, instances);
		
		//4. 封装转换指标等数据
		instances = ((List<ResourceInstance>)instancTemps[0]);	//资源实例列表
		if(null != instances && instances.size()> 0){
			Map<Long,Long> parentChildMap = (Map<Long, Long>) instancTemps[2];	//资源实例id--取值接口id
			
			//*******************链路取值来自取值接口  start**************************
//			instanceIds = (long[]) instancTemps[1];		//取值接口ids
			//*******************链路取值来自取值接口  end****************************
			
			List<LinkBo> linkBos = this.homeParseInstanceToLinkNode(linkList, instances,instanceIds,parentChildMap);
			page.setDatas(linkBos);
		}else{
			page.setTotalRecord(0);
			page.setDatas(null);
		}
	}
	
	/**
	 * 按照用户域过滤链路实例
	 * @param domainSet
	 * @param instances
	 * @return
	 */
	private List<ResourceInstance> filterLinkByUserDomainIds(Set<Long> domainSet,List<ResourceInstance> instances){
		for(int i=instances.size()-1;i>=0;i--){
			ResourceInstance resource = instances.get(i);
			if(!domainSet.contains(resource.getDomainId())){
				instances.remove(resource);
			}
		}
		return instances;
	}
	
	/**
	 * 根据条件过滤资源实例列表数据
	 * @param page
	 * @param linkList
	 * @param instances
	 * @return
	 */
	private Object[] filterResourceLinkList(Page<LinkBo, LinkBo>  page,List<ResourceInstance> instances){
		//按照用户域过滤链路实例
		instances = this.filterLinkByUserDomainIds(page.getCondition().getDomainSet(), instances);
		
		Object[] instanceTemps = new Object[3];
		//页面查询参数
		String searchVal = page.getCondition().getSearchVal();
		
		//有条件查询,去掉不含条件的数据
		boolean haveConditon = StringUtils.isNotBlank(searchVal);
		if(haveConditon){
			for(int i=instances.size()-1;i>=0;i--){
				ResourceInstance resource = instances.get(i);
				//根据源IP过滤
				 boolean remove = !resourceExApi.getPropVal(resource, LinkResourceConsts.PROP_SRC_MAININST_IP).contains(searchVal);
				//根据目的IP过滤
				if(remove) { 
					remove = !resourceExApi.getPropVal(resource, LinkResourceConsts.PROP_DEST_MAININST_IP).contains(searchVal);
				}
				//删除不满足搜索条件的资源实例
				if(remove) instances.remove(resource);
			}
		}
		
		//排序
		if(StringUtils.isNotBlank(page.getSort())){
			instances = this.sort(instances, page.getSort(), page.getOrder());
		}
		
		if(haveConditon || StringUtils.isNotBlank(page.getSort())){
			//截取列表对象数据
			int size = instances.size();
			int startRow = (int) page.getStartRow();
			int rowCount = (int) page.getRowCount();
			int endRow = startRow + rowCount;
			page.setTotalRecord(size);
			
			//截取页面需要的条数的资源列表数据
			instanceTemps[0] = instances.subList(startRow,endRow > size ? size : endRow);
		}else {
			instanceTemps[0] = instances;	//资源实例
		}
		
		Map<Long,Long> parentChildMap = new HashMap<Long, Long>();
		long[] instanceIds = new long[instances.size()];			//取值资源实例ids
		for(int i=0;i<instances.size();i++){
			ResourceInstance parentInstance = instances.get(i);
			String collSubInstId = resourceExApi.getPropVal(parentInstance, LinkResourceConsts.PROP_COLL_SUBINST_ID);	//取值设备id（决定取值接口）
			long collResourceId = -1;
			if(!defaultConstant.equals(collSubInstId)){
				collResourceId = Long.valueOf(collSubInstId);
			}
			parentChildMap.put(parentInstance.getId(), collResourceId);		//链路实例id一一对应取值接口实例id
			instanceIds[i] = collResourceId;
		}
		
		instanceTemps[1] = instanceIds;		//取值资源实例ids
		instanceTemps[2] = parentChildMap;	//资源id--取值接口id
		return instanceTemps;
	}
	
	/**
	 * 分页查询资源实例发现的节点（网络设备）列表
	 */
	private List<NodeBo> selectNodesByPage(Page<NodeBo, NodeBo>  page) {
		String typeOrIp = page.getCondition().getSearchVal();	//页面查询参数
		
		List<NodeBo>  nodeList = nodeDao.getAllInstances();
/*		List<NodeBo>  nodeList = null;
		//有条件的查询需要查询所有资源实例，然后再根据条件从中搜索过滤
		if(StringUtils.isNotBlank(typeOrIp) || StringUtils.isNotBlank(page.getSort())){
			nodeList = nodeDao.getAllInstances();
		}else{
			//无条件查询
			nodeDao.selectByPage(page);
			nodeList = page.getDatas();
		}*/
		return nodeList;
	}
	
	/**
	 * 根据条件分页查询资源实例设备列表
	 * 1. 查询node的资源实例ids
	 * 2. 根据ids查询资源实例列表
	 * 3. 根据查询条件【ip/资源名称】过滤数据
	 * 4. 封装转换指标等数据
	 * @param page
	 * @throws InstancelibException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void selectDeviceByPage(Page<NodeBo, NodeBo>  page) throws InstancelibException {
		//1. 查询node的资源实例ids
		List<NodeBo>  nodeList = this.selectNodesByPage(page);
		if(null == nodeList || nodeList.size() == 0) return;		
		//查询出登录用户所属域的资源，并过滤
		List<Long> domainInstanceIds = resourceExApi.getResourceIdsByDomainId(page.getCondition().getDomainSet());
		
		List<Long>  resourceIds = new ArrayList<Long>();
		int i=0;
		NodeBo node = null;
		long instanceId = 0;
		
		for(i=0;domainInstanceIds.size() > 0 && i<nodeList.size();i++){
			node = nodeList.get(i);
			instanceId = node.getInstanceId();
			if(domainInstanceIds.contains(instanceId)){
				resourceIds.add(instanceId);
			}
		}
		//2. 根据ids查询资源实例列表
		List<ResourceInstance> instances = resourceService.getResourceInstances(resourceIds);
		//List<ResourceInstance> instances = resourceService.getResourceInstances(domainInstanceIds);
		//3.根据查询条件【ip/资源名称】过滤数据
		Object[] instancTemps = this.filterResourceList(page, nodeList, instances);
		
		//4. 封装转换指标等数据
		instances = ((List<ResourceInstance>)instancTemps[0]);
		if(null != instances && instances.size()> 0){
			long[] instanceIds = (long[]) instancTemps[1];
			List<NodeBo> nodeBosodeBos = this.parseResourceVal(nodeList, instances,instanceIds);
			page.setDatas(nodeBosodeBos);
		}else{
			page.setTotalRecord(0);
			page.setDatas(null);
		}
	}
	
	/**
	 * 根据提供的字段和排序规则进行排序操作
	 * @param resourceInstances 无序的资源集合
	 * @param field 要排序的字段
	 * @param order 排序的方向 可取 asc：正序 desc逆序
	 * @return
	 */
	private List<ResourceInstance> sort(List<ResourceInstance> resourceInstances,final String field,String order){
		if ("ASC".equals(order.toUpperCase())) {
			Collections.sort(resourceInstances, new Comparator<ResourceInstance>() {
				@Override
				public int compare(ResourceInstance bo1, ResourceInstance bo2) {
					//资源IP地址
                   if("ip".equals(field)){
                	   return compareIp("ASC",bo1.getShowIP(), bo2.getShowIP());
                   }
                   
                   //资源名称
                   if("instanceName".equals(field)){
                	   if(bo1.getName() == null && bo2.getName() == null){
                		   return 0;
                	   }else if(bo1.getName() == null && bo2.getName() != null){
                		   return -1;
                	   }else if(bo1.getName() != null && bo2.getName() == null){
                		   return 1;
                	   }else{
                		   if(bo1.getName().compareToIgnoreCase(bo2.getName()) == 0){
                			   return 0;
                		   }else if(bo1.getName().compareToIgnoreCase(bo2.getName()) > 0){
                			   return 1; 
                		   }else{
                			   return -1; 
                		   }
                	   }
                   }
                   
                   //链路源IP地址
                   String defaultVal = "- -";
                   if("srcMainInstIP".equals(field)){
                	   String srcIp = resourceExApi.getPropVal(bo1, LinkResourceConsts.PROP_SRC_MAININST_IP);
                	   String descIp = resourceExApi.getPropVal(bo2, LinkResourceConsts.PROP_SRC_MAININST_IP);
                	   srcIp = defaultVal.equals(srcIp)?null:srcIp;
                	   descIp = defaultVal.equals(descIp)?null:descIp;
                	   return compareIp("ASC",srcIp,descIp);
                   }
                   //链路目的IP地址
                   if("destMainInstIP".equals(field)){
                	   String srcIp = resourceExApi.getPropVal(bo1, LinkResourceConsts.PROP_DEST_MAININST_IP);
                	   String descIp = resourceExApi.getPropVal(bo2, LinkResourceConsts.PROP_DEST_MAININST_IP);
                	   srcIp = defaultVal.equals(srcIp)?null:srcIp;
                	   descIp = defaultVal.equals(descIp)?null:descIp;
                	   return compareIp("ASC",srcIp,descIp);
                   }
                   
                   //设备类型
                   if("typeName".equals(field)){
                	   if(bo1.getChildType() == null && bo2.getChildType() == null){
                		   return 0;
                	   }else if(bo1.getChildType() == null && bo2.getChildType() != null){
                		   return -1;
                	   }else if(bo1.getChildType() != null && bo2.getChildType() == null){
                		   return 1;
                	   }else{
                		   if(bo1.getChildType().compareToIgnoreCase(bo2.getChildType()) == 0){
                			   return 0;
                		   }else if(bo1.getChildType().compareToIgnoreCase(bo2.getChildType()) > 0){
                			   return 1; 
                		   }else{
                			   return -1; 
                		   }
                	   }
                   }
                   
                   return 0;
				}
			});
		}else{
			Collections.sort(resourceInstances, new Comparator<ResourceInstance>() {
				@Override
				public int compare(ResourceInstance bo1, ResourceInstance bo2) {
					//资源IP地址
                   if("ip".equals(field)){
                	   return compareIp("DESC",bo1.getShowIP(), bo2.getShowIP());
                   }
                   
                   //资源名称
                   if("instanceName".equals(field)){
                	   if(bo1.getName() == null && bo2.getName() == null){
                		   return 0;
                	   }else if(bo1.getName() == null && bo2.getName() != null){
                		   return 1;
                	   }else if(bo1.getName() != null && bo2.getName() == null){
                		   return -1;
                	   }else{
                		   if(bo1.getName().compareToIgnoreCase(bo2.getName()) == 0){
                			   return 0;
                		   }else if(bo1.getName().compareToIgnoreCase(bo2.getName()) > 0){
                			   return -1; 
                		   }else{
                			   return 1; 
                		   }
                	   }
                   }
                   
                   //链路源IP地址
                   if("srcMainInstIP".equals(field)){
                	   String srcIp = resourceExApi.getPropVal(bo1, LinkResourceConsts.PROP_SRC_MAININST_IP);
                	   String descIp = resourceExApi.getPropVal(bo2, LinkResourceConsts.PROP_SRC_MAININST_IP);
                	   return compareIp("DESC",srcIp,descIp);
                   }
                   //链路目的IP地址
                   if("destMainInstIP".equals(field)){
                	   String srcIp = resourceExApi.getPropVal(bo1, LinkResourceConsts.PROP_DEST_MAININST_IP);
                	   String descIp = resourceExApi.getPropVal(bo2, LinkResourceConsts.PROP_DEST_MAININST_IP);
                	   return compareIp("DESC",srcIp,descIp);
                   }
                   
                   //设备类型
                   if("typeName".equals(field)){
                	   if(bo1.getChildType() == null && bo2.getChildType() == null){
                		   return 0;
                	   }else if(bo1.getChildType() == null && bo2.getChildType() != null){
                		   return 1;
                	   }else if(bo1.getChildType() != null && bo2.getChildType() == null){
                		   return -1;
                	   }else{
                		   if(bo1.getChildType().compareToIgnoreCase(bo2.getChildType()) == 0){
                			   return 0;
                		   }else if(bo1.getChildType().compareToIgnoreCase(bo2.getChildType()) > 0){
                			   return -1; 
                		   }else{
                			   return 1; 
                		   }
                	   }
                   }
                   
                   return 0;
				}
			});
		}
		
		return resourceInstances;
	}
	
	/**
	 * 比较ip大小
	 * @param ipOne
	 * @param ipTwo
	 * @return
	 */
	private int compareIp(String order,String ipOne,String ipTwo){
		if ("ASC".equals(order.toUpperCase())) {
			if(ipOne == null && ipTwo == null){
				return 0;
			}else if(ipOne == null && ipTwo != null){
				return -1;
			}else if(ipOne != null && ipTwo == null){
				return 1;
			}else{
				if(Util.ip2Long(ipOne) == Util.ip2Long(ipTwo)){
					return 0;
				}else if(Util.ip2Long(ipOne) > Util.ip2Long(ipTwo)){
					return 1; 
				}else{
					return -1; 
				}
			}
		}else{
			if(ipOne == null && ipTwo == null){
				return 0;
			}else if(ipOne == null && ipTwo != null){
				return 1;
			}else if(ipOne != null && ipTwo == null){
				return -1;
			}else{
				if(Util.ip2Long(ipOne) == Util.ip2Long(ipTwo)){
					return 0;
				}else if(Util.ip2Long(ipOne) > Util.ip2Long(ipTwo)){
					return -1; 
				}else{
					return 1; 
				}
			}
		}
	}
	/**
	 * 根据条件过滤资源实例列表数据
	 * @param nodeList
	 * @param instances
	 * @return
	 */
	private Object[] filterResourceList(Page<NodeBo, NodeBo>  page,List<NodeBo> nodeList,List<ResourceInstance> resourceiInstances){
		Object[] instanceTemps = new Object[2];
		String typeOrIp = page.getCondition().getSearchVal();	//页面查询参数
		//有条件查询,去掉不含条件的数据
		
		ResourceInstance resource = null;
		boolean exist = false;
		
		if(StringUtils.isNotBlank(typeOrIp)){
			for(int i=resourceiInstances.size()-1;i>=0;i--){
				resource = resourceiInstances.get(i);
				exist = false;	//默认不存在
				if(StringUtils.isNotBlank(resource.getName()) && resource.getName().toLowerCase().contains(typeOrIp.toLowerCase())){
					exist = true;
				}
				if(!exist && StringUtils.isNotBlank(resource.getShowIP()) && resource.getShowIP().contains(typeOrIp)){
					exist = true;
				}
				if(!exist){	//去掉不含条件的数据
					resourceiInstances.remove(resource);
				}
			}
		}

        List<ResourceInstance> instances = resourceiInstances;
        /*List<ResourceInstance> instances = JSONObject.parseArray(JSONObject.toJSONString(resourceiInstances), ResourceInstance.class);
        //排序
		if(StringUtils.isNotBlank(page.getSort())){
			//转换设备名称才能排序
			for(ResourceInstance rs :instances){
				for(NodeBo bo:nodeList){
					if(null != bo.getInstanceId() && bo.getInstanceId().longValue() == rs.getId()){
						rs.setChildType(bo.getType());
						break;
					}
				}
			}
			instances = this.sort(instances, page.getSort(), page.getOrder());
		}*/

        //截取列表对象数据
		int size = instances.size();
		int startRow = (int) page.getStartRow();
		int rowCount = (int) page.getRowCount();
		int endRow = startRow + rowCount;
		page.setTotalRecord(size);
		
/*		if(StringUtils.isNotBlank(typeOrIp) || StringUtils.isNotBlank(page.getSort())){			
			//截取页面需要的条数的资源列表数据
			instanceTemps[0] = instances.subList(startRow,endRow > size ? size : endRow);
		}else {
			instanceTemps[0] = instances;
		}*/
		instanceTemps[0] = instances.subList(startRow,endRow > size ? size : endRow);
				
		//同时去掉节点list资源数据
		long resourceId = 0;
		NodeBo node = null;
				
		long[] instanceIds = new long[instances.size()];			//资源实例ids
		for(int i=0;i<instances.size();i++){
			resourceId = instances.get(i).getId();
			instanceIds[i] = resourceId;
			for(int j=nodeList.size()-1;j>=0;j--){
				node = nodeList.get(j);
				if(String.valueOf(resourceId).equals(node.getResourceId())){
					nodeList.remove(node);
					break;
				}
			}
		}
		instanceTemps[1] = instanceIds;
		return instanceTemps;
	}
	
	/**
	 * 封装转换指标等数据
	 * @param nodeList
	 * @param instances
	 * @return	List<NodeBo>
	 */
	private List<NodeBo> parseResourceVal(List<NodeBo> nodeList,List<ResourceInstance> instances,long[] instanceIds){
		//查询指标值
		String[] metricIds = {MetricIdConsts.METRIC_CPU_RATE,MetricIdConsts.METRIC_MEME_RATE};
		List<Map<String, ?>> merticMaps = resourceExApi.getMerictRealTimeVals(metricIds, instanceIds);
		
		// 查询资源状态
		List<Long> instanceTmps = new ArrayList<Long>();
		for(long instanceId:instanceIds){
			instanceTmps.add(instanceId);
		}
		List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(instanceTmps);
		// 组装资源状态
		Map<Long, InstanceStateEnum> instanceStateDataMap = new HashMap<Long, InstanceStateEnum>();
		InstanceStateData isd = null;
		for(int i = 0; instanceStateDataList != null && i < instanceStateDataList.size(); i++){
			isd = instanceStateDataList.get(i);
			instanceStateDataMap.put(isd.getInstanceID(), isd.getState());
		}
		
		List<NodeBo> nodes = new ArrayList<NodeBo>();
		NodeBo nodeBo = null;
		long resourceId = 0;
		InstanceStateEnum isd2 = null;
		MetricData metricData = null;
		String val = null;
		ResourceMetricDef[] resourceMetricDefs = null;
		Map<String,String> metricsUnit = null;
		
		for(ResourceInstance resource:instances){
			nodeBo = new NodeBo();
			for(NodeBo node:nodeList){
				if(Long.valueOf(node.getInstanceId()) == resource.getId()){
					nodeBo.setType(node.getType());
					nodeBo.setInstanceId(node.getInstanceId());
					break;
				}
			}
			resourceId = resource.getId();		//资源实例id
			nodeBo.setInstanceName(StringUtils.isNotBlank(resource.getName())?resource.getName():defaultConstant);	//名称
			nodeBo.setIp(resource.getShowIP());		//资源ip
			//获取设备状态颜色
			if(InstanceLifeStateEnum.MONITORED == resource.getLifeState()){
				isd2 = null;
				if(instanceStateDataMap.containsKey(resourceId)){
					isd2 = instanceStateDataMap.get(resourceId);
				}
				isd2 = isd2 == null ? InstanceStateEnum.UNKOWN : isd2;
				nodeBo.setInstanceColor(resourceExApi.getResourceStateColor(isd2));
			}else{
				nodeBo.setInstanceColor(InstanceLifeStateEnum.NOT_MONITORED.name());
			}
			//获取CPU利用率
			nodeBo.setCpuRateColor(resourceExApi.getMetricStateColor(resourceId, MetricIdConsts.METRIC_CPU_RATE));
			//获取内存利用率
			nodeBo.setMemeRateColor(resourceExApi.getMetricStateColor(resourceId, MetricIdConsts.METRIC_MEME_RATE));
			//获取运行时间指标
			metricData = resourceExApi.getMetricData(resourceId, MetricTypeEnum.InformationMetric, MetricIdConsts.SYS_UPTIME);
			val = resourceExApi.parseArrayToString(null==metricData?null:metricData.getData());
			nodeBo.setUptimeString("".equals(val) ? defaultConstant : UnitTransformUtil.transform(val, "秒"));
			resourceMetricDefs = resourceExApi.getMetricDefs(resource);			//资源指标定义列表
			metricsUnit = resourceExApi.getMetricsUnitMap(resourceMetricDefs);	//所有资源指标单位map
			for (Map<String, ?> metric: merticMaps) {
				if (null != metric.get("instanceid") && resourceId == Long.valueOf(metric.get("instanceid").toString()).longValue()) {
					nodeBo.setCpuRate(resourceExApi.getMetricVal(metric, MetricIdConsts.METRIC_CPU_RATE, metricsUnit));
					nodeBo.setMemeRate(resourceExApi.getMetricVal(metric, MetricIdConsts.METRIC_MEME_RATE, metricsUnit));
					break;
				}
			}
			nodes.add(nodeBo);
		}
		return nodes; 
	}
}
