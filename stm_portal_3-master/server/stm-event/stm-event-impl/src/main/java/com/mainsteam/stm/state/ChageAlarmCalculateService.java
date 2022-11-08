package com.mainsteam.stm.state;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.event.AlarmSnapshotUtils;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;

/**只针对(文件)“改变告警”
 * @author heshengchao
 *
 */
public class ChageAlarmCalculateService implements MetricDataProcessor {
	private static final Log logger=LogFactory.getLog(ChageAlarmCalculateService.class);
	private static final String METRIC_INFORMATION = "2";

	private ProfileService profileService;
	private TimelineService timelineService;
	private MetricDataService metricDataService;
	private ResourceInstanceService instanceService;
	private AlarmService alarmService;
	private AlarmSnapshotUtils alarmSnapshotUtils;
	
	
	public void setMetricDataService(MetricDataService metricDataService) {
		this.metricDataService = metricDataService;
	}
	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}
	public void setTimelineService(TimelineService timelineService) {
		this.timelineService = timelineService;
	}
	public void setInstanceService(ResourceInstanceService instance){
		this.instanceService=instance;
	}
	public void setAlarmService(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

	@Override
	public MetricDataPersistence process(MetricCalculateData metricData, ResourceMetricDef rdf, CustomMetric cm, Map<String, Object> contextData) throws Exception {
		if(rdf==null){//自定义指标
			if(logger.isTraceEnabled()){
				logger.trace("metric["+metricData.getResourceId()+","+metricData.getMetricId()+"] is customerMetric,exit!");
			}
			return null;
		}else if(!MetricTypeEnum.InformationMetric.equals(rdf.getMetricType())){//非可用性指标
			if(logger.isTraceEnabled()){
				logger.trace("metric["+rdf.getId()+","+rdf.getMetricType()+"] is not InformationMetric,exit!");
			}
			return null;
		}else{
			ProfileMetric  pf=null;
			if(metricData.getTimelineId()>0){
				pf=timelineService.getMetricByTimelineIdAndMetricId(metricData.getTimelineId(), metricData.getMetricId());
			}else{
				pf=profileService.getMetricByProfileIdAndMetricId(metricData.getProfileId(), metricData.getMetricId());
			}
			if(pf!=null){
				if(pf.isAlarm()){
					MetricData oldData=metricDataService.getMetricInfoData(metricData.getResourceInstanceId(), metricData.getMetricId());
					
					if(!StringUtils.equals(JSON.toJSONString(oldData.getData()),JSON.toJSONString(metricData.getMetricData()))){
						
						AlarmSenderParamter paramter=new AlarmSenderParamter();
						paramter.setProvider(AlarmProviderEnum.OC4);
						paramter.setSysID(SysModuleEnum.MONITOR);
						paramter.setRuleType(AlarmRuleProfileEnum.model_profile);
						paramter.setRecoverKeyValue(new String[]{String.valueOf(metricData.getResourceInstanceId()), metricData.getMetricId()});
						paramter.setSourceID(String.valueOf(metricData.getResourceInstanceId()));
						paramter.setGenerateTime(metricData.getCollectTime());
						paramter.setLevel(InstanceStateEnum.WARN);
						
						if(rdf.getResourceDef()!=null){
							ResourceDef cdef;
							if(null!= rdf.getResourceDef().getCategory() ){
								cdef=rdf.getResourceDef();
							}else{
								cdef=rdf.getResourceDef().getParentResourceDef();
							}
							paramter.setExt0(cdef.getId());
							paramter.setExt1(cdef.getCategory().getId());
							paramter.setExt2(cdef.getCategory().getParentCategory().getId());
						}
						
						//Ext3==metricID;
						paramter.setExt3(rdf.getId());
						paramter.setProfileID(pf.getProfileId());
						
						//ext4==已修复/未修复
						paramter.setExt4("0");
						//ext5==是否为信息指标
						paramter.setExt6(METRIC_INFORMATION);
						
						String msg="";
						String insName="";
						try {
							ResourceInstance instance=instanceService.getResourceInstance(metricData.getResourceInstanceId());
							
							if(instance!=null&&instance.getParentId()>0){
								ResourceInstance parent=instanceService.getResourceInstance(instance.getParentId());
								insName=instance!=null?(StringUtils.isEmpty(parent.getShowName())?parent.getName():parent.getShowName()):null;
								paramter.setSourceName(insName);
								paramter.setSourceIP(parent.getShowIP());
								paramter.setExt8(String.valueOf(parent.getId()));
								msg+="【"+instance.getName()+"】";
							}else{
								insName=instance!=null?(StringUtils.isEmpty(instance.getShowName())?instance.getName():instance.getShowName()):null;
								paramter.setSourceName(insName);
								paramter.setSourceIP(instance.getShowIP());
								paramter.setExt8(instance!=null?String.valueOf(instance.getId()):null);
							}
							paramter.setSourceIP(instance.getShowIP());

						} catch (InstancelibException e) {
				        	logger.error(e.getMessage(),e);
						}
						
						msg+="【"+rdf.getName()+"】发生改变";
						
						
						paramter.setDefaultMsg(msg+"，新的内容："+metricData.getMetricData()[0]);
						paramter.setDefaultMsgTitle("【"+insName+"】"+msg);
						
						
						paramter.setExt7(alarmSnapshotUtils.handleAlarm(metricData.getResourceInstanceId(), metricData.getMetricId(),paramter.getSourceIP(),paramter.getLevel()));
						
						alarmService.notify(paramter);
						
						
					}
				}
			}
			
		}

		return null;
	}

	@Override
	public int getOrder() {
		return 20;
	}
	public AlarmSnapshotUtils getAlarmSnapshotUtils() {
		return alarmSnapshotUtils;
	}
	public void setAlarmSnapshotUtils(AlarmSnapshotUtils alarmSnapshotUtils) {
		this.alarmSnapshotUtils = alarmSnapshotUtils;
	}

}
