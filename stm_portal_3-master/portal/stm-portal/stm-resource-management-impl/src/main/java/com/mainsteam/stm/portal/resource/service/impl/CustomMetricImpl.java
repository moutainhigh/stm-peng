package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.alarm.event.AlarmEventTemplateService;
import com.mainsteam.stm.alarm.obj.AlarmEventTemplate;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.dict.PluginIdEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dsl.expression.dict.TerminalSymbolConst;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CollectMeticSetting;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import com.mainsteam.stm.metric.obj.CustomMetricCollectParameter;
import com.mainsteam.stm.metric.obj.CustomMetricDataProcess;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.obj.CustomMetricQuery;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.metric.objenum.CustomMetricDataProcessWayEnum;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomMetricApi;
import com.mainsteam.stm.portal.resource.bo.CustomMetricBo;
import com.mainsteam.stm.portal.resource.bo.CustomMetricResourceBo;
import com.mainsteam.stm.portal.resource.bo.PortalThreshold;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.util.DateUtil;

public class CustomMetricImpl implements ICustomMetricApi {
	private static Logger logger = Logger.getLogger(CustomMetricImpl.class);
	
	@Resource
	private CapacityService capacityService;
	@Resource
	private ProfileService profileService;
	@Resource
	private CustomMetricService customMetricService;
	@Resource(name="stm_system_resourceApi")
	private IResourceApi resourceApi;
	@Resource
	private IDomainApi domainApi;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private AlarmEventTemplateService alarmEventTemplateService;

	private static final String KEY_COMMAND = "COMMAND";
	private static final String KEY_METHOD = "method";
	private static final String KEY_GET = "get";
	private static final String KEY_WALK = "walk";
	private static final String KEY_SQL = "SQL";
	private static final String KEY_URL= "urlSite";
	
	private static final String JDBC_RESOURCE_ID = "JDBC";
	
	private Map<String,ResourceDef> resourceDefMap=new HashMap<String, ResourceDef>();
	
	@Override
	public List<CustomMetricBo> getCustomMetricsByInstanceId(long instanceId){
		List<CustomMetricBo> customMetricDatas=new ArrayList<CustomMetricBo>();
		
		List<CustomMetric> customMetrics=new ArrayList<CustomMetric>();
		try {
			customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
		} catch (CustomMetricException e) {
			logger.error("Can't find CustomMetric By InstanceId="+instanceId,e);
		}
		if(customMetrics!=null){
			customMetricDatas=customMetricsToCustomMetricBos(customMetrics);
		}

		return customMetricDatas;
	}
	
	/**
	 * 分页查询
	 */
	@Override
	public List<CustomMetricBo> getCustomMetrics(Long startRow, Long pageSize,CustomMetricBo customMetricBo) {

		CustomMetricQuery metricQuery=new CustomMetricQuery();
		//预留的查询条件
		
		//设置指标类型
		MetricTypeEnum metricTypeEnum = null;
		if(customMetricBo.getMetricType()!=null){
			switch (customMetricBo.getMetricType()) {
			case "AvailabilityMetric":
				metricTypeEnum = MetricTypeEnum.AvailabilityMetric;
				break;
			case "InformationMetric":
				metricTypeEnum = MetricTypeEnum.InformationMetric;
				break;
			case "PerformanceMetric":
				metricTypeEnum = MetricTypeEnum.PerformanceMetric;
				break;
			}
		}
		
		metricQuery.setCustomMetricStyle(metricTypeEnum);
		List<CustomMetric> customMetrics=null;
		try {
			customMetrics = customMetricService.getCustomMetrics(metricQuery, startRow.intValue(), pageSize.intValue());
		} catch (CustomMetricException e) {
			// TODO Auto-generated catch block
			logger.error("getCustomMetrics Faild", e);
		}
		
		List<CustomMetricBo> customMetricDatas=new ArrayList<CustomMetricBo>();
		
		if(customMetrics!=null && customMetrics.size()>0){
			
			//将能力库模型预先放进resourceDefMap
			List<ResourceDef> resourceDefs=capacityService.getResourceDefList();

			for(ResourceDef resourceDef:resourceDefs){
				resourceDefMap.put(resourceDef.getId(), resourceDef);
			}
			
			customMetricDatas=customMetricsToCustomMetricBos(customMetrics);
		}
		//根据条件筛选List结果
		Iterator<CustomMetricBo> it = customMetricDatas.iterator();
		while(it.hasNext()){
			CustomMetricBo bo = (CustomMetricBo)it.next();
			if(customMetricBo.getDiscoverWay()!=null&&!"".equals(customMetricBo.getDiscoverWay())){
				if(!bo.getDiscoverWay().equals(customMetricBo.getDiscoverWay())){
					it.remove();
					continue;
				}
			}else if (customMetricBo.getName()!=null&&!"".equals(customMetricBo.getName())) {
				if(!bo.getName().toLowerCase().contains(customMetricBo.getName().toLowerCase())){
					it.remove();
					continue;
				}
			}
		}
		for(int i=0;i<customMetricDatas.size();i++){
			if(customMetricBo.getDiscoverWay()!=null&&!"".equals(customMetricBo.getDiscoverWay())){
				if(!customMetricDatas.get(i).getDiscoverWay().equals(customMetricBo.getDiscoverWay())){
					customMetricDatas.remove(i);
					continue;
				}
			}else if (customMetricBo.getName()!=null&&!"".equals(customMetricBo.getName())) {
				if(customMetricDatas.get(i).getName().indexOf(customMetricBo.getName())==-1){
					customMetricDatas.remove(i);
					continue;
				}
			}
		}
		return customMetricDatas;
	}
	/**
	 * 查询单个指标
	 */
	@Override
	public CustomMetricBo getCustomMetric(String metricId) {
		CustomMetric customMetric=null;
		try {
			customMetric = customMetricService.getCustomMetric(metricId);
		} catch (CustomMetricException e) {
			logger.error("Can't find CustomMetric ID="+metricId,e);
		}
		CustomMetricBo bo=new CustomMetricBo();
		if(customMetric!=null){
			bo=customMetricToCustomMetricBo(customMetric);
		}
		
		return bo;
	}
	
	
	@Override
	public String createCustomMetric(CustomMetricBo metricBo) {
		
		CustomMetric customMetric=customMetricBoToCustomMetric(metricBo);
		String metricId=null;
		try {
			metricId = customMetricService.createCustomMetric(customMetric);
		} catch (CustomMetricException e) {
			logger.error("Create Custom Metric faild!",e);
		}
		return metricId;
	}

	@Override
	public void updateCustomMetric(CustomMetricBo metricBo) {
		CustomMetric customMetric=customMetricBoToCustomMetric(metricBo);
		//更新基础信息
		try {
			CustomMetricInfo customMetricInfo=customMetric.getCustomMetricInfo();

			String pluginId=metricBo.getDiscoverWay();
			
			List<CustomMetricThreshold> customMetricThresholds=customMetric.getCustomMetricThresholds();
			List<CustomMetricCollectParameter> customMetricCollectParameters=customMetric.getCustomMetricCollectParameters();

			String metricId=metricBo.getId();
			boolean monitor=metricBo.isMonitor();
			boolean alert=metricBo.isAlert();
			CollectMeticSetting collectMeticSetting=new CollectMeticSetting();
			collectMeticSetting.setMetricId(metricId);
			collectMeticSetting.setMonitor(monitor);
			collectMeticSetting.setAlert(alert);
			collectMeticSetting.setFlapping(1);
			collectMeticSetting.setFreq(FrequentEnum.valueOf(metricBo.getFrequent()));
			
			
			CustomMetricDataProcess customMetricDataProcess = new CustomMetricDataProcess();
			customMetricDataProcess.setMetricId(metricId);
			customMetricDataProcess.setPluginId(pluginId);
			customMetricDataProcess.setDataProcessWay(CustomMetricDataProcessWayEnum.valueOf(metricBo.getDataProcessWay()));
			
			customMetricService.updateCustomMetricBasicInfo(customMetricInfo);
			customMetricService.updateCustomMetricThreshold(customMetricThresholds);
			customMetricService.updateCustomMetricCollects(metricBo.getId(), pluginId, customMetricCollectParameters);
			customMetricService.updateCustomMericSetting(collectMeticSetting);
			customMetricService.updateCustomMetricDataProcess(customMetricDataProcess);
		} catch (CustomMetricException e) {
			logger.error("Update Custom Metric faild!",e);
		}
	}

	@Override
	public void deleteCustomMetric(String metricId) {
		try {
			customMetricService.deleteCustomMetric(metricId);
		} catch (CustomMetricException e) {
			logger.error("Delete Custom Metric Error:",e);
		}
	}
	@Override
	public void deleteCustomMetrics(List<String> metricIds){
		try {
			customMetricService.deleteCustomMetric(metricIds);
		} catch (CustomMetricException e) {
			logger.error("Delete Custom Metric Error:",e);
		}
	}
	
	/**
	 * 更新是否  告警/监控
	 * @param alart
	 */
	public void updateCustomMeticSetting(CollectMeticSetting collectMeticSetting){
		try {
			customMetricService.updateCustomMericSetting(collectMeticSetting);
		} catch (CustomMetricException e) {
			logger.error("update Custom Metic Setting faild!",e);
		}
	}

	
	@Override
	public List<CustomMetricResourceBo> getResourceInstances(ILoginUser user,String categoryId,Long domainId,PluginIdEnum pluginId){
		//get All domains
		List<Domain> domains=domainApi.getAllDomains();
		
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		List<String> categoryIds=new ArrayList<String>();
		categoryIds.add(categoryId);
		queryBo.setCategoryIds(categoryIds);

		List<Long> domainIds=new ArrayList<Long>();
		if(domainId!=null && domainId>0){
			domainIds.add(domainId);
		}else{
			for(Domain domain:domains){
				domainIds.add(domain.getId());
			}
		}
		queryBo.setDomainIds(domainIds);
		List<ResourceInstanceBo>  resourceInstanceBos=resourceApi.getResources(queryBo);

		Map<Long,String> domanMap=new HashMap<Long,String>(domains.size());
		
		for(Domain domain:domains){
			domanMap.put(domain.getId(), domain.getName());
		}
		
		List<CustomMetricResourceBo> customMetricResourceBos=new ArrayList<CustomMetricResourceBo>();
		//筛选已监控数据
		for(ResourceInstanceBo resourceInstanceBo:resourceInstanceBos){
			String resourceId=resourceInstanceBo.getResourceId();
			ResourceDef reDef=resourceDefMap.get(resourceId);
			Set<String> pluginIds=reDef.getConfigPluginIds();
			String reDefName=reDef.getName();
			
			boolean pluginIsMatching = false;
			
			if(resourceId.equals(JDBC_RESOURCE_ID)){
				//特殊处理JDBC
				pluginIsMatching = true;
			}else{
				pluginIsMatching = pluginIds.contains(pluginId.name());
			}
			
			//获取已监控 和 相同的发现类型
			if(pluginIsMatching && resourceInstanceBo.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				CustomMetricResourceBo customMetricResourceBo=new CustomMetricResourceBo();
				customMetricResourceBo.setId(resourceInstanceBo.getId());
				customMetricResourceBo.setResourceName(resourceInstanceBo.getShowName());
				customMetricResourceBo.setResourceIP(resourceInstanceBo.getDiscoverIP());
				customMetricResourceBo.setCategoryId(categoryId);
				customMetricResourceBo.setCategoryName(reDefName);
				customMetricResourceBo.setDomainId(resourceInstanceBo.getDomainId());
				customMetricResourceBo.setDomainName(domanMap.get(resourceInstanceBo.getDomainId()));
				customMetricResourceBos.add(customMetricResourceBo);
			}
		}
		
		return customMetricResourceBos;
	}
	
	@Override
	public List<CustomMetricResourceBo> getCustomMetricBinds(String metricId){
		
		List<CustomMetricBind> customMetricBinds=null;
		try {
			customMetricBinds = customMetricService.getCustomMetricBindsByMetricId(metricId);
		} catch (CustomMetricException e) {
			logger.error("getCustomMetricBinds Faild", e);
		}
		
		if(customMetricBinds==null){
			return null;
		}
		
		List<Long> resourceIds=new ArrayList<Long>();
		for(CustomMetricBind customMetricBind:customMetricBinds){
			resourceIds.add(customMetricBind.getInstanceId());
		}
		
		List<ResourceInstanceBo> resourceInstanceBos=resourceApi.getResource(resourceIds);
		//域信息
		List<Domain> domains=domainApi.getAllDomains();
		Map<Long,String> domanMap=new HashMap<Long,String>(domains.size());
		
		for(Domain domain:domains){
			domanMap.put(domain.getId(), domain.getName());
		}
		
		List<CustomMetricResourceBo> customMetricResourceBos=new ArrayList<CustomMetricResourceBo>();
		//筛选已监控数据
		for(ResourceInstanceBo resourceInstanceBo:resourceInstanceBos){
			String resourceId=resourceInstanceBo.getResourceId();
			ResourceDef reDef=resourceDefMap.get(resourceId);
			
			String reDefName=reDef.getName();
			
			//获取已监控
			if(resourceInstanceBo.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				CustomMetricResourceBo customMetricResourceBo=new CustomMetricResourceBo();
				customMetricResourceBo.setMetricId(metricId);
				customMetricResourceBo.setId(resourceInstanceBo.getId());
				customMetricResourceBo.setResourceName(resourceInstanceBo.getShowName());

				customMetricResourceBo.setResourceIP(resourceInstanceBo.getDiscoverIP());
				customMetricResourceBo.setCategoryId(resourceInstanceBo.getCategoryId());
				customMetricResourceBo.setCategoryName(reDefName);
				customMetricResourceBo.setDomainId(resourceInstanceBo.getDomainId());
				customMetricResourceBo.setDomainName(domanMap.get(resourceInstanceBo.getDomainId()));

				customMetricResourceBos.add(customMetricResourceBo);
			}
		}
		
		
		return customMetricResourceBos ;
	}
	
	@Override
	public void updateResuorceBind(String metricId,String pluginId,List<CustomMetricResourceBo> bindResources){
		List<CustomMetricBind> customMetricBinds=new ArrayList<CustomMetricBind>();
		
		for(CustomMetricResourceBo resourceBo:bindResources){
			CustomMetricBind customMetricBind=new CustomMetricBind();
			customMetricBind.setMetricId(metricId);
			customMetricBind.setInstanceId(resourceBo.getId());
			customMetricBind.setPluginId(pluginId);
			
			customMetricBinds.add(customMetricBind);
		}
		
		
		try {
			customMetricService.getCustomMetric(metricId).getCustomMetricCollectParameters().get(0).getPluginId();
			customMetricService.clearCustomMetricBinds(metricId, pluginId);
			customMetricService.addCustomMetricBinds(customMetricBinds);
			
		} catch (CustomMetricException e) {
			logger.error("Bind Resuorce faild:", e);
		}
		
	}
	
	
	
	//===============================Util========================================

	
	public List<CustomMetricCollectParameter> customMetricBo2collectParameters(CustomMetricBo bo){
		List<CustomMetricCollectParameter> collectParameters=new ArrayList<CustomMetricCollectParameter>();
		
		
		return collectParameters;
	}
	
	/**
	 * 指标集合转换
	 * @param customMetrics
	 * @return
	 */
	public List<CustomMetricBo> customMetricsToCustomMetricBos(List<CustomMetric> customMetrics){
		if(customMetrics==null){
			return null;
		}
		
		List<CustomMetricBo> customMetricBos=new ArrayList<CustomMetricBo>(customMetrics.size());
		
		for(CustomMetric customMetric:customMetrics){
			CustomMetricBo customMetricBo=customMetricToCustomMetricBo(customMetric);
			customMetricBos.add(customMetricBo);
		}
		
		return customMetricBos;
	}

	/**
	 * 指标 转换1
	 * @param customMetric
	 * @return
	 */
	public CustomMetricBo customMetricToCustomMetricBo(CustomMetric customMetric){
		if(customMetric==null){
			return null;
		}
		CustomMetricBo customMetricBo=new CustomMetricBo();
		//基础信息
		CustomMetricInfo metricInfo=customMetric.getCustomMetricInfo();
		//阈值
		List<CustomMetricThreshold> metricThresholds=customMetric.getCustomMetricThresholds();
		//采集参数
		List<CustomMetricCollectParameter> metricCollectParameters=customMetric.getCustomMetricCollectParameters();

		String datetime="";
		if(metricInfo.getUpdateTime()!=null){
			datetime=DateUtil.format(metricInfo.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
		}
		
		//设置基本信息
		customMetricBo.setId(metricInfo.getId());
		customMetricBo.setName(metricInfo.getName());
		customMetricBo.setUnit(metricInfo.getUnit());
		customMetricBo.setMetricType(metricInfo.getStyle().name());
		customMetricBo.setAlert(metricInfo.isAlert());
		customMetricBo.setMonitor(metricInfo.isMonitor());
		customMetricBo.setFrequent(metricInfo.getFreq().name());
		customMetricBo.setDateTime(datetime);

		List<PortalThreshold> thresholds = new ArrayList<PortalThreshold>();
		if(metricThresholds != null && metricThresholds.size() > 0){
			
			AlarmEventTemplate alarmEvent = new AlarmEventTemplate();
			String alarmTemplateId = metricThresholds.get(0).getAlarmTemplate();
			Map<InstanceStateEnum, String> content = null;
			if(alarmTemplateId == null || alarmTemplateId.equals("")){
				content = alarmEventTemplateService.getDefaultTemplate(false, SysModuleEnum.MONITOR, customMetric.getCustomMetricInfo().getStyle()).getContent();
			}else{
				alarmEvent.setUniqueKey(alarmTemplateId);
				content = alarmEventTemplateService.getTemplate(alarmEvent).getContent();
			}
			
			//阈值设置
			for(CustomMetricThreshold metricThreshold:metricThresholds){
				
				PortalThreshold threshold = new PortalThreshold();
				threshold.setAlarmTemplate(alarmTemplateId);
				
				String expression = "";
				if(metricThreshold.getThresholdExpression() != null){
					expression = metricThreshold.getThresholdExpression().replaceAll(CustomMetricThreshold.PLACEHOLDER, metricInfo.getName());
				}
				
				if(metricThreshold.getMetricState().equals(MetricStateEnum.WARN)){
					threshold.setPerfMetricStateEnum(PerfMetricStateEnum.Minor);
					customMetricBo.setWarnThresholdDesc(expression);
					threshold.setAlarmContent(content.get(InstanceStateEnum.WARN));
				}
				if(metricThreshold.getMetricState().equals(MetricStateEnum.SERIOUS)){
					threshold.setPerfMetricStateEnum(PerfMetricStateEnum.Major);
					customMetricBo.setSeriousThresholdDesc(expression);
					threshold.setAlarmContent(content.get(InstanceStateEnum.SERIOUS));
				}
				if(metricThreshold.getMetricState().equals(MetricStateEnum.NORMAL)){
					threshold.setPerfMetricStateEnum(PerfMetricStateEnum.Normal);
					threshold.setAlarmContent(content.get(InstanceStateEnum.NORMAL));
				}
				
				threshold.setThresholdExpression(expression);
				threshold.setMetricId(metricThreshold.getMetricId());
				
				thresholds.add(threshold);
				
			}
		}
		
		if(customMetric.getCustomMetricDataProcess() == null){
			logger.error("Get customMetric.getCustomMetricDataProcess() is null");
			return customMetricBo;
		}
		
		String pluginId=customMetric.getCustomMetricDataProcess().getPluginId();
		String parameterValue=null;
		String dataProcessWay=customMetric.getCustomMetricDataProcess().getDataProcessWay().name();
		
		//采集方式设置
		
		for(int i = 0; metricCollectParameters != null && i < metricCollectParameters.size(); i++){
			CustomMetricCollectParameter metricCollectParameter = metricCollectParameters.get(i);
			if(!metricCollectParameter.getParameterValue().isEmpty()){
				parameterValue=metricCollectParameter.getParameterValue();
			}
		}
		
		customMetricBo.setThresholdsMap(thresholds);
		customMetricBo.setDiscoverWay(pluginId);
		
		if(pluginId.equals("SnmpPlugin")){
			customMetricBo.setOid(parameterValue);
			customMetricBo.setDataProcessWay(dataProcessWay);
		}else{
			customMetricBo.setCommand(parameterValue);
		}
		
		return customMetricBo;
	}
	
	/**
	 * 指标 转换2
	 * @param customMetricBo
	 * @return
	 */
	public CustomMetric customMetricBoToCustomMetric(CustomMetricBo bo){
		if(bo==null){
			return null;
		}
		
		String metricId=bo.getName();
		
		CustomMetricInfo customMetricInfo=new CustomMetricInfo();
		customMetricInfo.setId(bo.getId());
		customMetricInfo.setName(bo.getName());
		customMetricInfo.setStyle(MetricTypeEnum.valueOf(bo.getMetricType()));
		customMetricInfo.setUnit(bo.getUnit() == null ? "" : bo.getUnit());
		customMetricInfo.setFreq(FrequentEnum.valueOf(bo.getFrequent()));
		customMetricInfo.setMonitor(bo.isMonitor());
		customMetricInfo.setAlert(bo.isAlert());
		customMetricInfo.setFlapping(1);
		
		
		//阈值设置-------------------------------------------
		List<CustomMetricThreshold> thresholds=new ArrayList<CustomMetricThreshold>();
		
		List<PortalThreshold> portalThresholds = bo.getThresholdsMap();
		
		String newId = null;
		if(portalThresholds != null && portalThresholds.size() > 0){
			
			Map<InstanceStateEnum, String> content = new HashMap<InstanceStateEnum, String>();
			
			for(PortalThreshold threshold : portalThresholds){
				
				if(threshold.getPerfMetricStateEnum() == null){
					continue;
				}
				
				if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Normal)){
					content.put(InstanceStateEnum.NORMAL, threshold.getAlarmContent());
				}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Minor)){
					content.put(InstanceStateEnum.WARN, threshold.getAlarmContent());
				}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Major)){
					content.put(InstanceStateEnum.SERIOUS, threshold.getAlarmContent());
				}
				
			}
			
			AlarmEventTemplate alarmEvent = new AlarmEventTemplate();
			alarmEvent.setMetricId(bo.getName());
			alarmEvent.setContent(content);
			newId = alarmEventTemplateService.updateTemplate(alarmEvent);
			
			for(PortalThreshold threshold : portalThresholds){
				
				if(threshold.getPerfMetricStateEnum() == null){
					continue;
				}
				
				CustomMetricThreshold customThreshold = new CustomMetricThreshold();
				customThreshold.setMetricId(bo.getId());
				if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Normal)){
					customThreshold.setMetricState(MetricStateEnum.NORMAL);
					content.put(InstanceStateEnum.NORMAL, threshold.getAlarmContent());
				}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Minor)){
					customThreshold.setMetricState(MetricStateEnum.WARN);
					content.put(InstanceStateEnum.WARN, threshold.getAlarmContent());
				}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Major)){
					customThreshold.setMetricState(MetricStateEnum.SERIOUS);
					content.put(InstanceStateEnum.SERIOUS, threshold.getAlarmContent());
				}
				
				customThreshold.setOperator(OperatorEnum.Equal);
				customThreshold.setThresholdValue("");
				customThreshold.setAlarmTemplate(newId);
				customThreshold.setThresholdExpression(threshold.getThresholdExpression());
				
				thresholds.add(customThreshold);
				
			}
			
		}
		
		if(bo.getMetricType().equals(MetricTypeEnum.AvailabilityMetric.toString())){
			
			CustomMetricThreshold red=new CustomMetricThreshold();
			
			red.setMetricId(bo.getId());
			
			red.setMetricState(MetricStateEnum.CRITICAL);
			red.setOperator(OperatorEnum.Equal);
			red.setAlarmTemplate(newId);
			red.setThresholdExpression(metricId + " == 0");
			
			thresholds.add(red);
			
		}
		

		
		//发现参数设置-------------------------------------------
		List<CustomMetricCollectParameter> metricCollectParameters=new ArrayList<CustomMetricCollectParameter>();
		

		String discoverWay=bo.getDiscoverWay();
		String oid=bo.getOid();
		String cmd=bo.getCommand();
		if("SnmpPlugin".equals(discoverWay)){
			CustomMetricCollectParameter collectParameter1=new CustomMetricCollectParameter();
			collectParameter1.setMetricId(metricId);
			collectParameter1.setPluginId(discoverWay);
			collectParameter1.setParameterKey(KEY_METHOD);
			collectParameter1.setParameterType("");
			
//			if(oid!=null && oid.endsWith(".0")){
				collectParameter1.setParameterValue(KEY_GET);
//			}else{
//				collectParameter1.setParameterValue(KEY_WALK);
//			}
			
			CustomMetricCollectParameter collectParameter2=new CustomMetricCollectParameter();
			collectParameter2.setMetricId(metricId);
			collectParameter2.setPluginId(discoverWay);
			collectParameter2.setParameterKey("");
			collectParameter2.setParameterType("");
			collectParameter2.setParameterValue(oid);
			
			metricCollectParameters.add(collectParameter1);
			metricCollectParameters.add(collectParameter2);
		}else{
			CustomMetricCollectParameter collectParameter1=new CustomMetricCollectParameter();
			collectParameter1.setMetricId(metricId);
			collectParameter1.setPluginId(discoverWay);
			if((PluginIdEnum.JdbcPlugin.toString()).equals(discoverWay)){
				collectParameter1.setParameterKey(KEY_SQL);
			}else if((PluginIdEnum.UrlPlugin.toString()).equals(discoverWay)){
				collectParameter1.setParameterKey(KEY_URL);
			}else{
				collectParameter1.setParameterKey(KEY_COMMAND);
			}
			collectParameter1.setParameterType("");
			collectParameter1.setParameterValue(cmd);
			
			metricCollectParameters.add(collectParameter1);

//			CustomMetricCollectParameter collectParameter2=new CustomMetricCollectParameter();
//			collectParameter2.setMetricId(metricId);
//			collectParameter2.setPluginId(TELNETPLUGIN_ID);
//			collectParameter2.setParameterKey("");
//			collectParameter2.setParameterType("COMMAND");
//			collectParameter2.setParameterValue(cmd);
//			metricCollectParameters.add(collectParameter2);
		}
		
		CustomMetricDataProcess customMetricDataProcess=new CustomMetricDataProcess();
		customMetricDataProcess.setMetricId(metricId);
		customMetricDataProcess.setPluginId(discoverWay);
		customMetricDataProcess.setDataProcessWay(CustomMetricDataProcessWayEnum.valueOf(bo.getDataProcessWay()));
		
		CustomMetric customMetric=new CustomMetric();
		
		customMetric.setCustomMetricInfo(customMetricInfo);
		customMetric.setCustomMetricCollectParameters(metricCollectParameters);
		customMetric.setCustomMetricThresholds(thresholds);
		customMetric.setCustomMetricDataProcess(customMetricDataProcess);
		
		return customMetric;
	}

	@Override
	public int getCustomMetricCount() {
		//预留的查询条件
		CustomMetricQuery metricQuery=new CustomMetricQuery();

		return customMetricService.getCustomMetricsCount(metricQuery);
		
	}
}
