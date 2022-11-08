package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.api.ISettingApi;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <li>获取【资源实例-指标】数据封装接口</li>
 * <li>1.本类只用来获【资源实例-指标】的各种值</li>
 * <li>2.可自己扩展自己的接口封装</li>
 * @version  ms.stm
 * @since  2019年11月29日
 * @author zwx
 */
@Service
public class ResourceInstanceExImpl extends ThirdServiceBase implements IResourceInstanceExApi{
	Logger logger = Logger.getLogger(ResourceInstanceExImpl.class);
	private final String defaultConstant = "- -";	//默认字符串常量
	@Autowired
	private DataHelper dataHelper;
    @Autowired
    private ISettingApi settingApi;

    /**
	 * 获取登录用户所属域下的资源实例Ids
	 * @param domainSet
	 * @return
	 */
	public List<Long> getResourceIdsByDomainId(Set<Long> domainSet){
		List<ResourceInstance> resources = null;
		try {
			resources = resourceService.getParentResourceInstanceByDomainIds(domainSet);
		} catch (InstancelibException e) {
			logger.error("查询域下的资源异常",e);
		}
		List<Long> resourceIds = new ArrayList<Long>();
		if(null != resources && resources.size() > 0){
			for(ResourceInstance resource:resources){
				resourceIds.add(resource.getId());
			}
		}
		return resourceIds;
	}
	
	/**
	 * 把字符串数组转换成字符串
	 * @param data
	 * @return
	 */
	public String parseArrayToString(String[] data){
		if(data == null || data.length == 0){
			return "";
		}else{
			StringBuilder str = new StringBuilder();
			for(int i = 0; i < data.length; i++){
				if(data[i] != null && !"".equals(data[i].trim())){
					str.append(data[i].trim());
					if(i < data.length - 1){
						str.append(" , ");
					}
				}
			}
			return str.toString();
		}
	}
	
	/**
	 * 获取资源实例类型名称
	 * @param categoryId
	 * @return string	资源类型名称（如：交换机）
	 */
	public String getResourceTypeName(String categoryId){
		CategoryDef categoryDef = capacityService.getCategoryById(categoryId);
		return categoryDef==null?"":categoryDef.getName();
	}
	
	/**
	 * 批量查询【资源实例-性能指标】数据(PerformanceMetric：性能指标、InformationMetric：信息指标、AvailabilityMetric：可用性指标)
	 * @param metricIds	指标ids
	 * @param instanceIds	资源实例ids
	 * @return
	 */
	public List<Map<String, ?>> getMerictRealTimeVals(String[] metricIds,long[] instanceIds){
		if(null == instanceIds || instanceIds.length == 0){
			return new ArrayList<Map<String,?>>();
		}
		
		MetricRealtimeDataQuery query = new MetricRealtimeDataQuery();	//指标实时数据查询对象
		query.setMetricID(metricIds);
		query.setInstanceID(instanceIds);
		//默认分页查询1~100条
		Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(query, 1, 100);
		return page.getDatas();
	}
	
	/**
	 *  获取资源实例模型属性值
	 * @param resourceInstance
	 * @param property	模型属性
	 * @return
	 */
	public String getPropVal(ResourceInstance resourceInstance,String property){
		String[] values = null;
		if(null != resourceInstance){
			values = resourceInstance.getModulePropBykey(property);
		}
		return parseValToStr(values);
	}
	
	/**
	 * 获取资源实例当前状态
	 * @param resourceInstanceId
	 * @return InstanceStateEnum
	 */
	private InstanceStateEnum getResourceState(long resourceInstanceId){
		InstanceStateData stateData = instanceStateService.getState(resourceInstanceId);
		InstanceStateEnum stateEnum = null;
		if(null != stateData){
			stateEnum = stateData.getState();
		}
		return stateEnum;
	}
	
	/**
	 * 转换链路接口状态级别（主要用于前台展示,返回颜色与前台css对应）
	 * @param resourceInstance
	 * @return
	 */
	public String getLinkIfStateColor(ResourceInstance resourceInstance){
		String state = "nomonitoring";	//未监控
		if(null == resourceInstance)return state;
		
		//1.判断是否监控
		if(InstanceLifeStateEnum.MONITORED == resourceInstance.getLifeState()){	//监控中
			//2.获取监控中的状态
			InstanceStateEnum stateEnum = this.getResourceState(resourceInstance.getId());
			if(null == stateEnum) return state;
			
			switch (stateEnum) {
			case CRITICAL:
				state = "disable";break;	//严重
			case SERIOUS:
				state = "serious";break;	//告警
			case WARN:
				state = "exceeding";break;	//不可用
			case NORMAL:
				state = "";break;			//正常
			default:
				state = "nomonitoring";		//未监控
				break;
			}
		}
		return state;
	}
	
	/**
	 * 根据链路两端状态综合计算链路状态
	 * @param srcIfInstance 	链路实例
	 * @param srcIfInstance 	源端接口实例
	 * @param destIfInstance	目的端接口实例
	 * @return
	 */
	public String convertLinkStateColor(ResourceInstance linkInstance,ResourceInstance srcIfInstance,ResourceInstance destIfInstance){
		String color = "";
		String linkState = this.getResourceLinkStateColor(linkInstance, "link");
		if("unmonitor".equals(linkState)){	//链路本身未监控时，显示未监控
			color = "unmonitor";
		}else{
			String srcState = this.getResourceLinkStateColor(srcIfInstance,"link");
			String destState = this.getResourceLinkStateColor(destIfInstance,"link");
			String colorTmp = srcState+destState;
			if(colorTmp.contains("disabled")){		//链路断开
				color = "disabled";
			}else if(colorTmp.contains("red")){		//超荷负载
				color = "red";
			}else if(colorTmp.contains("yellow")){	//警戒负载
				color = "yellow";
			}else if(colorTmp.contains("green")){	//正常负载
				color = "green";
			}else{	//未监控
				color = "unmonitor";
			}
		}
		return color;
	}
	
	/**
	 * 转换链路-取值接口状态级别（主要用于前台展示,返回颜色与前台css对应）
	 * @param collInstance 资源实例
	 * @param type	(链路：link，接口：interface)
	 * @return
	 */
	public String getResourceLinkStateColor(ResourceInstance collInstance,String type) {
		String stateColor = "";
		String state = "unmonitor";	//未监控
		String stateInterface = "nomonitoring";	//未监控
		if(null == collInstance){
			switch (type) {
				case "link":stateColor = state;break;
				case "interface":stateColor = stateInterface;break;
			}
			logger.error("实例为null，获取的"+type+"状态="+stateColor);
			return stateColor;
		}
		logger.error("实例id="+collInstance.getId()+"	 监控状态="+collInstance.getLifeState().name());
		//1.判断是否监控
		if(InstanceLifeStateEnum.MONITORED == collInstance.getLifeState()){	//监控中
			//2.获取监控中的状态
			InstanceStateData stateData = null;
			List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(Arrays.asList(collInstance.getId()));
			if(instanceStateDataList != null && instanceStateDataList.size() > 0){
				stateData = instanceStateDataList.get(0);
			}
			logger.error("实例id="+collInstance.getId()+"	状态="+(stateData!=null?(stateData.getState()!=null?stateData.getState().name():"stateData为空"):"stateData为空"));
			if(null == stateData || InstanceStateEnum.UNKOWN == stateData.getState() || InstanceStateEnum.NORMAL == stateData.getState()){
				state = "green";		//正常
				stateInterface = "";	//可用
			}else if(InstanceStateEnum.CRITICAL == stateData.getState()){
				state = "disabled";		//致命（断开红叉）
				stateInterface = "exceeding";	//不可用
			}else if(InstanceStateEnum.SERIOUS == stateData.getState()){
				state = "red";			//严重（超负荷）
				stateInterface = "";	//可用
			}else if(InstanceStateEnum.WARN == stateData.getState()){
				state = "yellow";		//警告（警戒负载）
				stateInterface = "";	//可用
			}else if(InstanceStateEnum.NORMAL == stateData.getState()){
				state = "green";		//正常
				stateInterface = "";	//可用
			}
            /*BUG #48512 【告警优化】拓扑一览表中链路状态显示不正确 huangping 2017/12/4 start*/
            String setting = settingApi.getCfg("globalSetting");
            if (StringUtils.isNotBlank(setting) && !"{}".equals(setting)) {
                JSONObject tmp = JSONObject.parseObject(setting);
                JSONObject link = tmp.getJSONObject("link");
                String colorWarning = link.getString("colorWarning");
                //拓扑选择的是【链路状态】，并且接口有告警，但是不是状态的告警（是总流量或则带宽利用率等产生的，认为是正常的）
                if (null != stateData) {
                    String metricIdStr = stateData.getCauseBymetricID();
                    if ("link".equals(type) && "device".equals(colorWarning) && (state.equals("yellow") || state.equals("red")) && !StringUtils.equals(metricIdStr, MetricIdConsts.METRIC_AVAILABLE)) {
                        state = "green";        //正常
                    }
                }
            }
			/*//拓扑选择的是【链路状态】，并且接口有告警，但是不是状态的告警（是总流量或则带宽利用率等产生的，认为是正常的）
            if(null != stateData){
				String metricIdStr = stateData.getCauseBymetricID();
				if("link".equals(type) && (state.equals("yellow") || state.equals("red")) && !StringUtils.equals(metricIdStr, MetricIdConsts.METRIC_AVAILABLE)){
					state = "green";		//正常
				}
			}*/
            /*BUG #48512 【告警优化】拓扑一览表中链路状态显示不正确 huangping 2017/12/4 end*/
		}
		
		switch (type) {
		case "link":
			stateColor = state;
			break;
		case "interface":
			stateColor = stateInterface;
			break;
		}
		logger.error("实例id="+collInstance.getId()+"		，获取的"+type+"状态="+stateColor);
		return stateColor;
	}
	
	/**
	 *  获取指标当前【状灯态】颜色（主要用于前台展示,返回颜色与前台css对应）
	 * @param resourceInstanceId
	 * @param metricId
	 * @return String
	 */
	public String getMetricStateColor(long resourceInstanceId,String metricId) {
		String ise = "green";
		MetricStateData stateData = this.getMetricStateData(resourceInstanceId, metricId);
		if(null == stateData) return ise;
		
		MetricStateEnum stateEnum = stateData.getState();
		if(stateEnum != null){
			switch (stateEnum) {
			case CRITICAL:
				ise = "red";break;
			case SERIOUS:
				ise = "orange";break;
			case WARN:
				ise = "yellow";break;
			case NORMAL:
			case NORMAL_NOTHING:
				ise = "green";break;
			default:
				ise = "green";
				break;
			}
		}
		return ise;
	}
	
	/**
	 * 获取资源实例当前【状灯态】颜色（主要用于前台展示,返回颜色与前台css对应）
	 * @param stateEnum
	 * @return String
	 */
	public String getResourceStateColor(InstanceStateEnum stateEnum){
		String ise = null;
		switch (stateEnum) {
		case CRITICAL:
			ise = "res_critical";
			break;
		case CRITICAL_NOTHING:
			ise = "res_critical_nothing";
			break;
		case SERIOUS:
			ise = "res_serious";
			break;
		case WARN:
			ise = "res_warn";
			break;
		case NORMAL:
			break;
		case NORMAL_NOTHING:
			ise = "res_normal_nothing";
			break;
		case NORMAL_CRITICAL:
			ise = "res_normal_critical";
			break;
		case NORMAL_UNKNOWN:
			ise = "res_normal_unknown";
			break;
		case UNKNOWN_NOTHING:
			ise = "res_unknown_nothing";
			break;
		case NOT_MONITORED:
			ise = "res_not_monitored";
			break;
		case UNKOWN:
//			ise = "res_unkown";
			break;
		}
		return ise;
	}
	
	/**
	 * 获取资源实例指标值（定义了单位的同时返回单位，如：34%）
	 * @param resourceInstanceId	资源实例id
	 * @param metricType	指标类型
	 * @param metricId	指标id
	 * @param unit	指标单位
	 * @return String
	 */
	public String getMetricVal(Long resourceInstanceId,MetricTypeEnum metricType,String metricId,String unit){
		String value = defaultConstant;
		if(null != resourceInstanceId) {
			MetricData metricData = this.getMetricData(resourceInstanceId, metricType, metricId);	//指标对象
			if(null != metricData){
				value = this.parseValToStr(metricData.getData(),unit);
			}
		}
		return value;
	}
	
	/**
	 * 获取资源实例指标值（定义了单位的同时返回单位，如：34%）
	 * @param resourceInstanceId
	 * @param resourceMetricDefs
	 * @param metricId
	 * @return String
	 */
	public String getMetricVal(Long resourceInstanceId,ResourceMetricDef[] resourceMetricDefs,String metricId){
		String value = defaultConstant;
		MetricData metricData = this.getMetricData(resourceInstanceId, resourceMetricDefs, metricId);	//指标对象
		if(null != metricData){
			String unit = this.getMetricUnit(resourceMetricDefs, metricId);		//指标单位(如：bps)
			value = parseValToStr(metricData.getData(),unit);
		}
		return value;
	}
	
	/**
	 * 根据【指标map】+指标id+【指标单位定义map】获取资源实例指标值（定义了单位的同时返回单位，如：34%）
	 * @param metric	指标map集合（{"cpuRate":0,"cpuRateCollectTime":1418996627000,"instanceid":15344}）
	 * @param metricId	指标id
	 * @param metricsUnit	指标单位
	 * @return String	指标值
	 */
	public String getMetricVal(Map<String, ?> metric,String metricId,Map<String,String> metricsUnit){
		String val = defaultConstant;
		if(null != metric.get(metricId)){
			String valT = metric.get(metricId).toString();
			String unit = null == metricsUnit?"":metricsUnit.get(metricId);
			if(StringUtils.isNotBlank(valT) && "bps".equals(unit)){
				return dataHelper.autoAdjustBandWidthUnit(Double.valueOf(valT));
			}
			val = valT + unit;
		}
		return val;
	}
	
	/**
	 * 根据【资源实例】获取资源实例指标值（定义了单位的同时返回单位，如：34%）
	 * @param resourceInstance
	 * @param metricId
	 */
	public String getMetricVal(ResourceInstance resourceInstance,String metricId){
		String value = defaultConstant;
		MetricData metricData = this.getMetricData(resourceInstance, metricId);	//指标对象
		if(null != metricData){
			String unit = this.getMetricUnit(resourceInstance, metricId);		//指标单位(如：bps)
			value = parseValToStr(metricData.getData(),unit);
		}
		return value;
	}
	
	/**
	 * 根据【资源实例】获取指标状态对象
	 * @param resourceInstanceId
	 * @param metricId
	 * @return MetricStateData
	 */
	private MetricStateData getMetricStateData(long resourceInstanceId,String metricId){
		MetricStateData data = metricStateService.getPerformanceStateData(resourceInstanceId, metricId);
		return data;
	}
	
	/**
	 * 根据【资源实例的[资源指标列表]】获取所有指标单位(没有单位返回"")
	 * @param resourceMetricDefs
	 * @return Map<String,String>
	 */
	public Map<String,String> getMetricsUnitMap(ResourceMetricDef[] resourceMetricDefs){
		Map<String,String> metricsUnit = new HashMap<String,String>();
		for (ResourceMetricDef rmd : resourceMetricDefs) {
			metricsUnit.put(rmd.getId(), rmd.getUnit());
		}
		return metricsUnit;
	}
	
	/**
	 * 根据【资源实例的[资源指标列表]】获取指标单位(没有单位返回"")
	 * @param resourceMetricDefs
	 * @param metricId
	 * @return
	 */
	private String getMetricUnit(ResourceMetricDef[] resourceMetricDefs,String metricId){
		String unit = "";	//指标单位
		for (ResourceMetricDef rmd : resourceMetricDefs) {
			String mId = rmd.getId();
			if(metricId.equals(mId)){
				unit = rmd.getUnit();
				break;
			}
		}
		
		return unit;
	}
	
	/**
	 * 获取指标单位(没有单位返回"")
	 * @param resourceInstance
	 * @param metricId
	 * @return String
	 */
	private String getMetricUnit(ResourceInstance resourceInstance,String metricId){
		String unit = "";	//指标单位
		ResourceMetricDef[] resourceMetricDefs = this.getMetricDefs(resourceInstance);
		for (ResourceMetricDef rmd : resourceMetricDefs) {
			String mId = rmd.getId();
			if(metricId.equals(mId)){
				unit = rmd.getUnit();
				break;
			}
		}
		
		return unit;
	}
	
	/**
	 * 根据【资源实例】+指标id获取指标对象
	 * @param resourceInstance
	 * @param metricId	指标id
	 * @return MetricData
	 */
	public MetricData getMetricData(ResourceInstance resourceInstance,String metricId){
		ResourceMetricDef[] resourceMetricDefs = this.getMetricDefs(resourceInstance);
		MetricData data = this.getMetricData(resourceInstance.getId(), resourceMetricDefs, metricId);
		return data;
	}

	/**
	 * 根据资源id+【资源实例的[资源指标列表]】+指标id获取指标对象
	 * @param resourceInstanceId
	 * @param resourceMetricDefs
	 * @param metricId
	 * @return MetricData
	 */
	public MetricData getMetricData(long resourceInstanceId,ResourceMetricDef[] resourceMetricDefs,String metricId) {
		MetricData data = null;
		for (ResourceMetricDef rmd : resourceMetricDefs) {
			String mId = rmd.getId();
			if(metricId.equals(mId)){
				MetricTypeEnum metricType = rmd.getMetricType();		//指标取值类型
				switch (metricType) {
				case InformationMetric:
					data = infoMetricQueryAdaptService.getMetricInfoData(resourceInstanceId,mId);break;
				case PerformanceMetric:
					data = metricDataService.getMetricPerformanceData(resourceInstanceId,mId);break;
				case AvailabilityMetric:
					data = metricDataService.getMetricAvailableData(resourceInstanceId,mId);break;
				}
				break;
			}
		}
		return data;
	}
	
	/**
	 * 根据资源id+指标类型+指标id获取指标对象
	 * @param resourceInstanceId
	 * @param metricType	指标类型(PerformanceMetric：性能指标、AvailabilityMetric：可用性指标、InformationMetric：信息指标)
	 * @param metricId	指标id
	 * @return MetricData 指标数据
	 */
	public MetricData getMetricData(long resourceInstanceId,MetricTypeEnum metricType,String metricId) {
		MetricData data = null;
		switch (metricType) {
		case InformationMetric:
			data = infoMetricQueryAdaptService.getMetricInfoData(resourceInstanceId,metricId);break;
		case PerformanceMetric:
			data = metricDataService.getMetricPerformanceData(resourceInstanceId,metricId);break;
		case AvailabilityMetric:
			data = metricDataService.getMetricAvailableData(resourceInstanceId,metricId);break;
		}
		
		return data;
	}
	
	/**
	 * 获取资源实例定义
	 * @param resourceId
	 * @return ResourceDef
	 */
	private ResourceDef getResourceDef(ResourceInstance resourceInstance){
		ResourceDef resourceDef = capacityService.getResourceDefById(resourceInstance.getResourceId());
		return resourceDef;
	}
	
	/**
	 * 获取资源实例所有指标定义
	 * @param resourceInstance
	 * @return ResourceMetricDef[]
	 */
	public ResourceMetricDef[] getMetricDefs(ResourceInstance resourceInstance){
		ResourceDef resourceDef = this.getResourceDef(resourceInstance);
		ResourceMetricDef[] resourceMetricDefs = new ResourceMetricDef[0];
		if(null != resourceDef){
			resourceMetricDefs = resourceDef.getMetricDefs();
		}
		return resourceMetricDefs;
	}
	
	/**
	 * 将字符串数组转换为字符串
	 * @param values	指标值
	 * @return String
	 */
	private String parseValToStr(String[] values) {
		String val = "";
		if(null != values){
			for(String valT:values){
				if(StringUtils.isNotBlank(valT)){
					val += valT + ",";
				}
			}
			if(StringUtils.isNotBlank(val)){
				val = val.substring(0,val.length()-1);
			}
		}
		val = StringUtils.isBlank(val)?defaultConstant:val;
		return val;
	}
	
	/**
	 * 将字符串数组转换为字符串
	 * @param values	指标值
	 * @param unit 指标单位
	 * @return String
	 */
	private String parseValToStr(String[] values,String unit) {
		String val = "";
		if(null != values){
			for(String valT:values){
				if(StringUtils.isNotBlank(valT)){
					val += valT + ",";
				}
			}
			if(StringUtils.isNotBlank(val)){
				val = val.substring(0,val.length()-1);
			}
		}
		if(StringUtils.isNotBlank(val) && "bps".equals(unit)){
			DecimalFormat df = new DecimalFormat("0.00");
			val = df.format(Double.valueOf(val)/(1000*1000));
			unit = "Mbps";
		}
		val = StringUtils.isBlank(val)?defaultConstant:val+(StringUtils.isNotBlank(unit)?unit:"");
		return val;
	}
	
	/**
	 * 时间转换为字符串(如:0天14小时0分钟36秒)
	 * @param date
	 * @return string
	 */
	@Override
	public String parseDateToStr(Date date){
		String rst = "";
		long se = 1000;			//秒
		long mi = se*60;		//分钟
		long hour = mi*60;		//小时
		long day = hour*24;		//天
		
	    double time = (double)(new Date().getTime() - date.getTime());
	    double d = time/day;
	    int di = (int) d;
	    rst += di+"天";
	    time = (d-di)*day;
	    
	    double h = time/hour;
	    int hi = (int)h;
	    rst += hi+"小时";
	    time = (h-hi)*hour;
	    
	    double m = time/mi;
	    int mint = (int)m;
	    rst += mint+"分钟";
	    time = (m-mint)*mi;
	    
	    double s = time/se;
	    rst += (int)s+"秒";
	    
	    return rst;
	}
	
	/**
	 * 获取指标取值类型
	 * @param resourceInstance
	 * @param metricId
	 * @return MetricTypeEnum
	 */
//	@Deprecated
//	private MetricTypeEnum getMetricType(ResourceInstance resourceInstance,String metricId){
//		ResourceMetricDef[] resourceMetricDefs = this.getMetricDefs(resourceInstance);
//		
//		MetricTypeEnum metricType = null;	//指标取值类型
//		for (ResourceMetricDef rmd : resourceMetricDefs) {
//			String mId = rmd.getId();
//			if(metricId.equals(mId)){
//				metricType = rmd.getMetricType();
//				break;
//			}
//		}
//		return metricType;
//	}
}
