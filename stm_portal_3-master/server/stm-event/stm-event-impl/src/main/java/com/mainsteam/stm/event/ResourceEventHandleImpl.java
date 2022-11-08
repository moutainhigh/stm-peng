package com.mainsteam.stm.event;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.state.engine.StateHandle;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateChangeData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.util.UnitTransformUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ResourceEventHandleImpl implements StateHandle {
	private static final Log logger=LogFactory.getLog(ResourceEventHandleImpl.class);
	
	private CustomMetricService customMetricService;
	private ResourceInstanceService instanceService;
	private AlarmService alarmService;
	private ProfileService profileService;
	private AlarmSnapshotUtils alarmSnapshotUtils;
	private CapacityService capacityService;
	private MetricDataService metricDataService;
	private static int NO_PARENT_RESOURCE_INSTANCE = 0;
	private static final Map<MetricStateEnum,String> nameMap=new HashMap<>();
	
	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
	public void setInstanceService(ResourceInstanceService instance){
		this.instanceService=instance;
	}
	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}
	public void setCustomMetricService(CustomMetricService customMetricService) {
		this.customMetricService = customMetricService;
	}

	public void setMetricDataService(MetricDataService metricDataService) {
		this.metricDataService = metricDataService;
	}

	static {
		nameMap.put(MetricStateEnum.CRITICAL, "致命");
		nameMap.put(MetricStateEnum.SERIOUS, "严重");
		nameMap.put(MetricStateEnum.WARN, "警告");
		nameMap.put(MetricStateEnum.NORMAL, "正常");
		nameMap.put(MetricStateEnum.UNKOWN, "未知");
		nameMap.put(MetricStateEnum.NORMAL_UNKNOWN, "未知");

    }

	private void metricStateChange(MetricStateChangeData stateChange) {
	
		MetricStateData mstate=stateChange.getNewState();
		MetricCalculateData metricData=stateChange.getMetricData();
		ResourceMetricDef mdef=stateChange.getMetricDef();
		
		if(logger.isInfoEnabled()){
			logger.info("metricStateChange:"+JSON.toJSONString(mstate));
		}
		//初次加入监控，不告警
		//取消对未知状态的告警
		if(stateChange.getOldState()==null && mstate.getState()==MetricStateEnum.NORMAL){
			logger.warn("["+mstate.getInstanceID()+","+mstate.getMetricID()+","+mstate.getState()+"]first add monitor,don't alarm!");
			return ;
		}else if(!stateChange.isNotifiable()){
			logger.warn("["+mstate.getInstanceID()+","+mstate.getMetricID()+","+mstate.getState()+"]Notifiable is false,exit!");
			return ;
		}
		
		AlarmSenderParamter paramter=new AlarmSenderParamter();
		paramter.setProvider(AlarmProviderEnum.OC4);
		paramter.setSysID(SysModuleEnum.MONITOR);
		paramter.setRuleType(AlarmRuleProfileEnum.model_profile);
		
		paramter.setSourceID(String.valueOf(mstate.getInstanceID()));
		paramter.setGenerateTime(mstate.getCollectTime());

		paramter.setLevel(mstate.getState()==MetricStateEnum.NORMAL_UNKNOWN?InstanceStateEnum.UNKOWN:InstanceStateEnum.valueOf(mstate.getState().name()));
		paramter.setRecoverKeyValue(new String[]{String.valueOf(mstate.getInstanceID()),mstate.getMetricID()});
		//Ext0==resourceID;
		//Ext1==CategoryID;
		//Ext2==CategoryName;
		if(mdef.getResourceDef()!=null){
			ResourceDef cdef;
			if(null!= mdef.getResourceDef().getCategory() ){
				cdef=mdef.getResourceDef();
			}else{
				cdef=mdef.getResourceDef().getParentResourceDef();
			}
			if(StringUtils.equals(LinkResourceConsts.RESOURCE_LAYER2LINK_ID, cdef.getId())){
				paramter.setSysID(SysModuleEnum.LINK);
			}
			paramter.setExt0(cdef.getId());
			paramter.setExt1(cdef.getCategory().getId());
			paramter.setExt2(cdef.getCategory().getParentCategory().getId());
		}else {
			String resourceId = stateChange.getMetricData().getResourceId();
			if(resourceId != null) {
				ResourceDef resourceDef = capacityService.getResourceDefById(metricData.getResourceId());
				if(resourceDef !=null) {
					paramter.setExt0(resourceDef.getId());
					paramter.setExt1(resourceDef.getCategory().getId());
					paramter.setExt2(resourceDef.getCategory().getParentCategory().getId());
					
				}
				
			}
		}
		
		String insName="";
		ResourceInstance instance = null;
		try {
			instance=instanceService.getResourceInstance(mstate.getInstanceID());
			insName=instance!=null?(StringUtils.isEmpty(instance.getShowName())?instance.getName():instance.getShowName()):null;
			if(instance.getParentId() > NO_PARENT_RESOURCE_INSTANCE){
				insName =instance.getParentInstance()!=null?(StringUtils.isEmpty(instance.getParentInstance().getShowName())
						?instance.getParentInstance().getName():instance.getParentInstance().getShowName()):null;
				paramter.setExt8(String.valueOf(instance.getParentId()));
			}else{
				paramter.setExt8(String.valueOf(instance.getId()));
			}
			paramter.setSourceName(insName);
			paramter.setSourceIP(instance.getShowIP());
		} catch (InstancelibException e) {
			if(logger.isErrorEnabled())
        		logger.error(e.getMessage(),e);
		}
		//Ext3==metricID;
		paramter.setExt3(mstate.getMetricID());
		try {
			ProfileInfo pf = profileService.getBasicInfoByResourceInstanceId(mstate.getInstanceID());
			if(pf!=null){
				paramter.setProfileID(pf.getParentProfileId()>0?pf.getParentProfileId():pf.getProfileId());
			}
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled())
        		logger.error(e.getMessage(),e);
		}
		//ext4==已修复/未修复
		paramter.setExt4("0");
		//ext5==是否为可用性指标
		String type=null;
		if(mdef.getResourceDef()==null){
			type=MetricTypeEnum.AvailabilityMetric==mdef.getMetricType()?"3":"4";
		}else{
			type=MetricTypeEnum.InformationMetric==mdef.getMetricType()?"2":MetricTypeEnum.AvailabilityMetric==mdef.getMetricType()?"1":"0";
		}
		paramter.setExt6(type);
		if(mdef.getResourceDef() != null && StringUtils.equals(mdef.getResourceDef().getId(), "CameraRes")) {
			if(paramter.getLevel() == InstanceStateEnum.NORMAL) {
				paramter.setDefaultMsgTitle(mdef.getName() + "正常");
				paramter.setDefaultMsg(mdef.getName() + "正常");
			}else {
				paramter.setDefaultMsgTitle(mdef.getName() + "异常");
				paramter.setDefaultMsg(mdef.getName() + "异常");
			}
			MetricData metricInfoData = metricDataService.getMetricInfoData(instance.getId(), "collectTime");
			if(null == metricData || null== metricInfoData || null == metricInfoData.getData()) {
				if(logger.isWarnEnabled()) {
					logger.warn("CameraRes " + instance.getId() + ":" + mdef.getId() + " can't query collectTime");
				}
			}else {
				String s = metricInfoData.getData()[0];
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					paramter.setGenerateTime(format.parse(s));
				} catch (ParseException e) {
					if(logger.isWarnEnabled()) {
						logger.warn("CameraRes " + instance.getId() + ":" + mdef.getId() + " format collectTime failed:" + s);
					}
				}
			}
		}else {
			MsgMeta mm=generateMetricAlarmContent(mdef,mstate.getState(),metricData,stateChange.getThreshhold(), instance);
			paramter.setDefaultMsg(mm.msg);
			paramter.setDefaultMsgTitle(mm.title);

		}

		paramter.setExt7(alarmSnapshotUtils.handleAlarm(mstate.getInstanceID(), mstate.getMetricID(),paramter.getSourceIP(),paramter.getLevel()));

		alarmService.notify(paramter);
	}
	
	
	@Override
	public void onMetricStateChange(MetricStateChangeData stateChange) {
		try{
			this.metricStateChange(stateChange);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	private MsgMeta generateMetricAlarmContent(ResourceMetricDef def,MetricStateEnum state,MetricCalculateData metricData,
			Threshold threshhold, ResourceInstance instance){
		
		if(logger.isInfoEnabled())
			logger.info("["+metricData.getResourceInstanceId()+","+state.name()+"] change,cause by metric:"+def.getId());

		MsgMeta meta=new MsgMeta();
		StringBuilder sb=new StringBuilder();
		if(instance.getParentId() > NO_PARENT_RESOURCE_INSTANCE) {
			if(def!=null && def.getResourceDef() !=null)
				sb.append(def.getResourceDef().getName());
			sb.append(StringUtils.isNotBlank(instance.getShowName())?instance.getShowName():instance.getName());
		}

		 if (MetricTypeEnum.PerformanceMetric==def.getMetricType()){
			CustomMetricInfo cmi=null;
			sb.append(" ");
			if(metricData.isCustomMetric()){
				try {
					CustomMetric cm= customMetricService.getCustomMetric(metricData.getMetricId());
					cmi=cm.getCustomMetricInfo();
					sb.append("自定义指标：").append(cmi.getName());
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}else{
				sb.append(def.getName());
			}
			sb.append(" ");
			if(state.equals(MetricStateEnum.NORMAL)){
				try{
					String[] data=metricData.getMetricData();
					String value = UnitTransformUtil.transform(data!=null&&data.length>0?metricData.getMetricData()[0]:"", cmi!=null?cmi.getUnit():def.getUnit());
					sb.append("恢复正常，当前值：").append(value);
				}catch (Exception e) {
					if(logger.isWarnEnabled()) {
						logger.warn("Convert value error." + metricData.toString(), e);
					}
				}
			}else if( MetricStateEnum.isUnknown(state)){
				sb.append("状态未知");
			}else{
				sb.append(nameMap.get(state)).append("告警，");
				String[] data=metricData.getMetricData();
				{
					try{
						String value = UnitTransformUtil.transform(data!=null&&data.length>0?metricData.getMetricData()[0]:"", cmi!=null?cmi.getUnit():def.getUnit());
						sb.append("当前值：").append(value);
					}catch (Exception e){
						if(logger.isWarnEnabled()) {
							logger.warn("Convert value error." + metricData.toString(), e);
						}
					}
				}
				try{
					if(threshhold!=null){
						String threshold = UnitTransformUtil.transform(threshhold.getThresholdValue(), cmi!=null?cmi.getUnit():def.getUnit());
						sb.append("，阈值：").append(threshhold.getExpressionOperator()).append(threshold);
					}

				}catch (Exception e) {
					if(logger.isWarnEnabled()) {
						logger.warn("Convert value error." + metricData.toString(), e);
					}
				}
			}
		}


		long instanceId = instance.getParentId() > NO_PARENT_RESOURCE_INSTANCE ? instance.getParentId() : instance.getId();
		String mainInstanceName = "";

		try {
			ResourceInstance resourceInstance = instanceService.getResourceInstance(instanceId);
			mainInstanceName = StringUtils.isEmpty(resourceInstance.getShowName())?resourceInstance.getName():resourceInstance.getShowName();
		} catch (InstancelibException e) {
			if(logger.isWarnEnabled()) {
				logger.warn("can't find instance with " + instanceId,e);
			}
		}

		meta.msg=sb.toString();

		if(state.equals(MetricStateEnum.NORMAL)){
			meta.title="资源 "+mainInstanceName+" 指标状态恢复正常";
		}else{
			meta.title="资源 "+mainInstanceName+" 发生" + nameMap.get(state) + "告警";
		}
		return meta;
	}

	
	private void instanceStateChange(InstanceStateChangeData stateChage) {
		
//		boolean isParent=false;
		ResourceMetricDef mdef=stateChage.getCauseByMetricDef();
		MetricCalculateData metricData=stateChage.getMetricData();
		InstanceStateData mstate=stateChage.getNewState();
		//InstanceStateData oldState=stateChage.getOldState();
		
		//非可用性指标。不参与资源实例告警
		if(!stateChage.isNotifiable()){
			if(logger.isWarnEnabled())
				logger.warn("["+metricData.getResourceInstanceId()+","+mstate.getState()+"] is cause["+mstate.getCauseByInstance()+"] by other,don't record!");
			return ;
		}
		if(MetricTypeEnum.AvailabilityMetric!=mdef.getMetricType()){
			if(logger.isWarnEnabled())
				logger.warn("["+metricData.getResourceInstanceId()+","+stateChage.getNewState().getState()+"][cause by metric:"+stateChage.getCauseByMetricID()+"] alarm isn't cause by AvailabilityMetric,don't record!");
			return;
		}
//		if(oldState==null &&stateChage.getNewState().getState()==InstanceStateEnum.NORMAL){  //初次加入监控，不告警
//			if(logger.isWarnEnabled())
//				logger.warn("["+metricData.getResourceInstanceId()+","+stateChage.getNewState().getState()+"] is first change,don't record!");
//			return ;
//		}
		
		if(logger.isInfoEnabled())
			logger.info("["+metricData.getResourceInstanceId()+","+stateChage.getNewState().getState()+"] change,cause by metric:"+stateChage.getCauseByMetricID());
			
		
		AlarmSenderParamter paramter=new AlarmSenderParamter();
		
		paramter.setProvider(AlarmProviderEnum.OC4);
		paramter.setSysID(SysModuleEnum.MONITOR);
		
		paramter.setRuleType(AlarmRuleProfileEnum.model_profile);
		
		paramter.setSourceID(String.valueOf(metricData.getResourceInstanceId()));
		if(metricData.getCollectTime() !=null)
			paramter.setGenerateTime(metricData.getCollectTime());
		else
			paramter.setGenerateTime(new Date());
		//资源状态与告警状态切换
		if(mstate.getState()==InstanceStateEnum.CRITICAL){
			paramter.setLevel(InstanceStateEnum.CRITICAL);
		}else if(InstanceStateEnum.UNKOWN==mstate.getState()){
			paramter.setLevel(InstanceStateEnum.UNKOWN);
		}else{
			paramter.setLevel(InstanceStateEnum.NORMAL);
		}
		paramter.setRecoverKeyValue(new String[]{"instanceState_",String.valueOf(mstate.getInstanceID())});
		
		//Ext0==ResourceId;
		//Ext1==CategoryID;
		if(mdef.getResourceDef()!=null){
			ResourceDef cdef;
			if(null!= mdef.getResourceDef().getCategory() ){
				cdef=mdef.getResourceDef();
			}else{
				cdef=mdef.getResourceDef().getParentResourceDef();
			}
			
			if(StringUtils.equals(LinkResourceConsts.RESOURCE_LAYER2LINK_ID, cdef.getId())){
				paramter.setSysID(SysModuleEnum.LINK);
			}
			paramter.setExt0(cdef.getId());
			paramter.setExt1(cdef.getCategory().getId());
			paramter.setExt2(cdef.getCategory().getParentCategory().getId());
		}
		
		paramter.setExt3(mdef.getId());
		//Ext2==InstanceName;
		try {
			ResourceInstance instance=instanceService.getResourceInstance(mstate.getInstanceID());
			
			paramter.setSourceName(instance!=null?(StringUtils.isEmpty(instance.getShowName())?instance.getName():instance.getShowName()):null);
			paramter.setSourceIP(instance.getShowIP());
			
			String insName=instance!=null?(StringUtils.isEmpty(instance.getShowName())?instance.getName():instance.getShowName()):null;
			if(instance.getParentId() > NO_PARENT_RESOURCE_INSTANCE){
				ResourceInstance parentIns=instanceService.getResourceInstance(instance.getParentId());
				paramter.setSourceIP(parentIns.getShowIP());
				paramter.setSourceName(parentIns!=null?(StringUtils.isEmpty(parentIns.getShowName())?parentIns.getName():parentIns.getShowName()):null);
				paramter.setExt8(String.valueOf(instance.getParentId()));
			}else{
				paramter.setSourceName(insName);
				paramter.setSourceIP(instance.getShowIP());
				paramter.setExt8(String.valueOf(instance.getId()));
			}
			if(null != mdef && mdef.getResourceDef() != null && StringUtils.equals(mdef.getResourceDef().getId(), "CameraRes")) {
				String alarmContent = "";
				if(paramter.getLevel() == InstanceStateEnum.CRITICAL) {
					if(metricData != null && metricData.getMetricData() != null && metricData.getMetricData().length > 0) {
						String value = metricData.getMetricData()[0].trim();
						if(StringUtils.equals(value, "0")){
							alarmContent = "断线";
						}else if(StringUtils.equals(value, "2")){
							alarmContent = "登录异常";
						}else if(StringUtils.equals(value, "3")){
							alarmContent = "离线";
						}
					}
				}else {
					alarmContent = "连通性恢复正常";
				}
				paramter.setDefaultMsgTitle(alarmContent);
				paramter.setDefaultMsg(alarmContent);
				MetricData metricInfoData = metricDataService.getMetricInfoData(instance.getId(), "collectTime");
				if(null == metricData || null == metricInfoData || null == metricInfoData.getData()) {
					if(logger.isWarnEnabled()) {
						logger.warn("CameraRes " + instance.getId() + ":" + mdef.getId() + " can't query collectTime");
					}
				}else {
					String s = metricInfoData.getData()[0];
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						paramter.setGenerateTime(format.parse(s));
					} catch (ParseException e) {
						if(logger.isWarnEnabled()) {
							logger.warn("CameraRes " + instance.getId() + ":" + mdef.getId() + " format collectTime failed:" + s);
						}
					}
				}
			}else {
				MsgMeta meta=generateInstanceAlarmContent(paramter,insName,metricData,
						instance.getParentId() > NO_PARENT_RESOURCE_INSTANCE ? instance.getParentId() : instance.getId());
				paramter.setDefaultMsg(meta.msg);
				paramter.setDefaultMsgTitle(meta.title);

			}

		} catch (InstancelibException e) {
       		logger.error(e.getMessage(),e);
		}
		
		try {
			ProfileInfo pf = profileService.getBasicInfoByResourceInstanceId(mstate.getInstanceID());
			if(pf!=null){
				paramter.setProfileID(pf.getParentProfileId()>0?pf.getParentProfileId():pf.getProfileId());
			}
		} catch (ProfilelibException e) {
       		logger.error(e.getMessage(),e);
		}
		//自宝义可用性指标/可用性指标;
		paramter.setExt6(mdef.getResourceDef()==null?"3":"1");

		paramter.setExt7(alarmSnapshotUtils.handleAlarm(mstate.getInstanceID(), mstate.getCauseBymetricID(),paramter.getSourceIP(),paramter.getLevel()));
	
		alarmService.notify(paramter);
		
		if(logger.isInfoEnabled())
			logger.info("["+metricData.getResourceInstanceId()+","+stateChage.getNewState().getState()+"] finish!");
	}
	
	@Override
	public void onInstanceStateChange(InstanceStateChangeData stateChage) {
		try{
			this.instanceStateChange(stateChage);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}

	private MsgMeta generateInstanceAlarmContent(AlarmSenderParamter paramter,String insName, MetricCalculateData metricData, long parentInstanceId) {
		MsgMeta meta=new MsgMeta();
		StringBuilder sb=new StringBuilder();
		
		if(paramter.getSourceName() !=null && !insName.equals(paramter.getSourceName())){
			ResourceDef resDef=capacityService.getResourceDefById(metricData.getResourceId());
			if(resDef !=null)
				sb.append(resDef.getName());
		}else{
			if(paramter.getSysID() == SysModuleEnum.LINK)
				sb.append("链路");
			else
				sb.append("资源");
		}
		
		sb.append(" ").append(insName).append(" ");
		if(InstanceStateEnum.CRITICAL==paramter.getLevel()){
			sb.append("不可用");
		}else if(InstanceStateEnum.NORMAL==paramter.getLevel()){
			sb.append("恢复可用");
		}else if(InstanceStateEnum.isUnknownForIns(paramter.getLevel())){
			sb.append("可用性未知");
		}else{
			throw new RuntimeException("can't match InstanceState["+paramter.getLevel()+"] for intsnce["
					+metricData.getResourceInstanceId()+":"+paramter.getSourceName()+"]");
		}

		meta.msg= sb.toString();
		meta.title=sb.toString();
		
		return meta;
	}
	@Override
	public int getOrder() {
		return 5;
	}

	public void setAlarmService(AlarmService alarmService) {
		this.alarmService = alarmService;
	}
	public void setAlarmSnapshotUtils(AlarmSnapshotUtils alarmSnapshotUtils) {
		this.alarmSnapshotUtils = alarmSnapshotUtils;
	}


	class MsgMeta{
		public String msg;
		public String title;
	}
}
