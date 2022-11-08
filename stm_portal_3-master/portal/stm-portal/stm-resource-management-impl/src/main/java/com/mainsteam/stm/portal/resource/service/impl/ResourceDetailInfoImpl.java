package com.mainsteam.stm.portal.resource.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.MetricSummaryService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IMetricChartsDataApi;
import com.mainsteam.stm.portal.resource.api.IResourceApplyApi;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.portal.resource.dao.IResVmResourceTreeDao;
import com.mainsteam.stm.portal.resource.po.ResVmResourceTreePo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.DateUtil;
import com.mainsteam.stm.util.UnitTransformUtil;
import com.mainsteam.stm.util.XmlUtil;

public class ResourceDetailInfoImpl implements IResourceDetailInfoApi {
	Logger logger = Logger.getLogger(ResourceDetailInfoImpl.class);
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private CapacityService capacityService;
	@Resource
	private MetricDataService metricDataService;
	@Resource
	private ProfileService profileService;
	@Resource
	private MetricStateService metricStateService;
	@Resource
	private InstanceStateService instanceStateService;
	@Resource
	private MetricSummaryService metricSummaryService;
	@Resource
	private IDomainApi domainApi;
	@Resource
	private CustomMetricService customMetricService;
	@Resource
	private TimelineService timelineService;
	@Resource
	private IResVmResourceTreeDao resVmResourceTreeDao;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	@Resource
	private IFileClientApi fileClient;
	@Resource
	private CustomPropService customPropService;
	@Resource
	private DiscoverPropService discoverPropService;
	@Autowired
	private IUserApi userApi;
	@Resource
	private AlarmEventService resourceEventService;
	@Resource
	private IMetricChartsDataApi metricChartsDataApi;
	@Resource
	private IResourceApplyApi resourceApplyApi;
	// @Resource(name="homeAlarmApi")
	// private IHomeAlarmApi homeAlarmApi;

	private static final String DOWLOAD_PATH = "common" + File.separator
			+ "classes" + File.separator + "config";
	private static final String DOWLOAD_FILENAME = "resourceDetailLayout.xml";
	private static final String CATALINA_HOME = "catalina.home";

	private static final String resource_img = "resource_img";
	private String[] dataStrings;

	@Override
	public long saveResourceImg(MultipartFile file, Long instanceId) {
		long fileId = -1;
		try {
			fileId = fileClient.upLoadFile(FileGroupEnum.STM_PORTAL, file);
		} catch (Exception e) {
			logger.error("file upload " + e.getMessage());
			return 0;
		}
		CustomProp cp = new CustomProp();
		cp.setInstanceId(instanceId);
		cp.setKey(resource_img);
		cp.setValues(new String[] { String.valueOf(fileId) });

		try {
			if (null != customPropService.getPropByInstanceAndKey(instanceId,
					resource_img)) {
				customPropService.removePropByInstanceAndKey(instanceId,
						resource_img);
			} else {
				customPropService.addProp(cp);
			}
		} catch (InstancelibException e) {
			logger.error("file upload resource_img add " + e.getMessage());
			return 0;
		}

		return fileId;
	}

	/**
	 * 
	 * @param instance
	 * @param flag
	 * @return
	 */
	private Map<String, Object> createInstance(ResourceInstance instance) {
		Map<String, Object> myInstance = new HashMap<String, Object>();
		if (instance != null) {
			myInstance.put("name", instance.getShowName());
			myInstance.put("instanceId", instance.getId());
			myInstance.put("parentCategoryId",
					capacityService.getCategoryById(instance.getCategoryId())
							.getParentCategory().getId());
			myInstance.put("resourceId", instance.getResourceId());
			myInstance.put("resourceName", getInstanceResourceName(instance));
			myInstance.put("resourceType", getInstanceResourceType(instance));
			// 主资源添加相关信息
			myInstance.put("ip", getIpList(instance));
			myInstance.put("metric", getMetricData(instance));
			myInstance.put("lifeState", instance.getLifeState());
		}
		return myInstance;
	}

	private String getInstanceResourceName(ResourceInstance instance) {
		ResourceDef resourceDef = capacityService.getResourceDefById(instance
				.getResourceId());
		return resourceDef == null ? "" : resourceDef.getName();
	}

	private String getInstanceResourceType(ResourceInstance instance) {
		return capacityService.getCategoryById(instance.getCategoryId())
				.getName();
	}

	/**
	 * 获取几个特殊的指标
	 * 
	 * @param instance
	 * @return
	 */
	private Map<String, ?> getMetricData(ResourceInstance instance) {
		long instanceId = instance.getId();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> specialMetric = new HashMap<String, Object>();

		// 可用指标
		specialMetric.put("availability", queryInstanceState(instance));

		MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
		// 主资源指标查询(CPU，内存)availability、OS、sysUpTime
		String[] metricTypes = new String[] { MetricIdConsts.METRIC_CPU_RATE,
				MetricIdConsts.METRIC_MEME_RATE,
				MetricIdConsts.METRIC_APP_MEME_RATE,
				MetricIdConsts.METRIC_APP_CPU_RATE };
		mrdq.setMetricID(metricTypes);
		long[] instanceIDs = new long[] { instanceId };
		mrdq.setInstanceID(instanceIDs);
		Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService
				.queryRealTimeMetricDatas(mrdq, 0, 1000);
		Map<String, ?> profMetric = page.getDatas() != null
				&& page.getDatas().size() > 0 ? page.getDatas().get(0) : null;
		specialMetric.putAll(profMetric);
		// 指标状态
		specialMetric.put("cpuRateState",
				queryMetricState(instanceId, MetricIdConsts.METRIC_CPU_RATE));
		specialMetric.put("memRateState",
				queryMetricState(instanceId, MetricIdConsts.METRIC_MEME_RATE));
		specialMetric
				.put("appcpuRateState",
						queryMetricState(instanceId,
								MetricIdConsts.METRIC_APP_CPU_RATE));
		specialMetric.put(
				"appmemRateState",
				queryMetricState(instanceId,
						MetricIdConsts.METRIC_APP_MEME_RATE));
		// 信息指标
		List<String> infoMetric = new ArrayList<String>();
		infoMetric.add("osVersion");
		infoMetric.add(MetricIdConsts.SYS_UPTIME);
		infoMetric.add(MetricIdConsts.METRIC_MACADDRESS);
		// 查询信息指标需要过滤
		// List<MetricData> infoMetricDataList = metricDataService
		// .getMetricInfoDatas(instanceId,
		// infoMetric.toArray(new String[infoMetric.size()]));
		List<MetricData> infoMetricDataList = infoMetricQueryAdaptService
				.getMetricInfoDatas(instanceId,
						infoMetric.toArray(new String[infoMetric.size()]));
		for (int i = 0; i < infoMetricDataList.size(); i++) {
			MetricData metricData = infoMetricDataList.get(i);
			if (metricData != null) {
				specialMetric.put(metricData.getMetricId(),
						emptyFirstLastChar(metricData.getData()));
			}
		}

		// 子资源信息
		List<Map<String, Object>> partitions = new ArrayList<Map<String, Object>>();
		List<ResourceInstance> childrenIns = instance.getChildren();
		String[] metricIds = new String[] { MetricIdConsts.PARTITION_FILENAME,
				MetricIdConsts.PARTITION_FILERATE };
		for (int i = 0; childrenIns != null && i < childrenIns.size(); i++) {
			ResourceInstance childIns = childrenIns.get(i);
			if (ResourceTypeConsts.TYPE_PARTITION.equals(childIns
					.getChildType())) {
				long childInsId = childIns.getId();
				Map<String, Object> metric = new HashMap<String, Object>();
				for (int j = 0; j < metricIds.length; j++) {
					String metricId = metricIds[j];
					ResourceMetricDef rmd = capacityService
							.getResourceMetricDef(childIns.getResourceId(),
									metricId);
					if (rmd != null) {
						switch (rmd.getMetricType()) {
						case InformationMetric:
							metric.put(
									rmd.getId(),
									queryInfoMetric(metricId, childIns)
											+ rmd.getUnit());
							break;
						case PerformanceMetric:
							metric.put(rmd.getId(),
									queryPrefMetric(metricId, childIns));
							metric.put(metricId + "State",
									queryMetricState(childInsId, metricId));
							break;
						default:
							break;
						}
					}
				}
				if (!metric.isEmpty()) {
					metric.put(MetricIdConsts.PARTITION_FILENAME,
							childIns.getName());
					metric.put("state", queryInstanceState(childIns));
					partitions.add(metric);
				}
			}
		}
		result.put("Partition", partitions);

		result.putAll(specialMetric);
		return result;
	}

	/**
	 * 把字符串数组转换成数符串
	 * 
	 * @param data
	 * @return
	 */
	private String emptyFirstLastChar(String[] data) {
		if (data == null || data.length == 0) {
			return "";
		} else {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null && !"".equals(data[i].trim())) {
					str.append(data[i].trim());
					if (i < data.length - 1) {
						str.append(" , ");
					}
				}
			}
			return str.toString();
		}
	}

	/**
	 * xml定义的子资源类型转换成能力库定义的常量
	 * 
	 * @param metric
	 * @return
	 */
	private String switchChildInstanceType(String childType) {
		switch (childType) {
		case "Partition":
			childType = ResourceTypeConsts.TYPE_PARTITION;
			break;
		case "NetInterface":
			childType = ResourceTypeConsts.TYPE_NETINTERFACE;
			break;
		default:
			break;
		}
		return childType;
	}

	/**
	 * xml定义的指标名称转换成能力库定义的常量
	 * 
	 * @param metric
	 * @return
	 */
	private String switchMetricId(String metricId) {
		switch (metricId) {
		case "throughput":
			metricId = MetricIdConsts.METRIC_THROUGHPUT;
			break;
		case "cpuRate":
			metricId = MetricIdConsts.METRIC_CPU_RATE;
			break;
		case "memRate":
			metricId = MetricIdConsts.METRIC_MEME_RATE;
			break;
		case "appCpuRate":
			metricId = MetricIdConsts.METRIC_APP_CPU_RATE;
			break;
		case "appMemRate":
			metricId = MetricIdConsts.METRIC_APP_MEME_RATE;
			break;
		case "Version":
			metricId = MetricIdConsts.VERSION;
			break;
		case "sysUpTime":
			metricId = MetricIdConsts.SYS_UPTIME;
			break;
		case "osVersion":
			metricId = "osVersion";
			break;
		case "fileSysName":
			metricId = MetricIdConsts.PARTITION_FILENAME;
			break;
		case "fileSysUsedSize":
			metricId = MetricIdConsts.PARTITION_FILEUSEDSIZE;
			break;
		case "fileSysFreeSize":
			metricId = MetricIdConsts.PARTITION_FILEFREESIZE;
			break;
		case "fileSysRate":
			metricId = MetricIdConsts.PARTITION_FILERATE;
			break;
		default:
			break;
		}
		return metricId;
	}

	/**
	 * 遍历xml所有值
	 * 
	 * @param root
	 * @param instance
	 * @param container
	 */
	private void queryInstanceMetrics(Element root, ResourceInstance instance,
			Object container, ILoginUser user) {
		long  startTime=System.currentTimeMillis();

		if ("container".equals(root.getName())) {
			String categoryId = instance.getCategoryId();
			String resourceId = instance.getResourceId();
			logger.error("resourceId: "+resourceId);
			logger.error("categoryId: "+categoryId);
			logger.error("ParentCategoryId: "+instance.getParentCategoryId());
			Element el=null;
			if(categoryId.equals("DiskArray")){
				if(instance.getDiscoverWay().equals(DiscoverWayEnum.SNMP)){
					 el = findDivElByResource("DiskArraySnmp");
				}else{
					el = findDivElByResource(resourceId);
				}
			}else{
				 el = findDivElByResource(resourceId);
			}
			
			if (null != el) {
				queryInstanceMetrics(el, instance, container, user);
			} else {
				queryInstanceMetrics(findDivEl(categoryId), instance,
						container, user);
			}
		} else if ("div".equals(root.getName())) {
			List<Element> tables = findTableEls(root);
			for (int i = 0; i < tables.size(); i++) {
				queryInstanceMetrics(tables.get(i), instance, container, user);
			}
		} else if ("table".equals(root.getName())) {
			List<Element> trs = findTrEls(root);
			List<List<Object>> tableList = new ArrayList<List<Object>>();
			for (int i = 0; i < trs.size(); i++) {
				queryInstanceMetrics(trs.get(i), instance, tableList, user);
			}
			List divList = (List) container;
			divList.add(tableList);
		} else if ("tr".equals(root.getName())) {
			List<Element> tds = findTdEls(root);
			List<Object> trList = new ArrayList<Object>();
			for (int i = 0; i < tds.size(); i++) {
				queryInstanceMetrics(tds.get(i), instance, trList, user);
			}
			List tableList = (List) container;
			tableList.add(trList);
		} else if ("td".equals(root.getName())) {
			Object tdObj = createTdMap(root, instance, user);
			List trList = (List) container;
			trList.add(tdObj);
		}
		long  endTime=System.currentTimeMillis();
		long time=(endTime-startTime);
		logger.info("queryInstanceMetrics query time :"+time);
	}

	/**
	 * 创建每个td节点的值
	 * 
	 * @param e
	 * @param instance
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, Object> createTdMap(Element e,
			ResourceInstance instance, ILoginUser user) {
		Map<String, Object> tdMap = new HashMap<String, Object>();
		String metricId = getElementAttrVal(e, "name");
	
		String type = getElementAttrVal(e, "type");
	
		String width = getElementAttrVal(e, "width");
		String height = getElementAttrVal(e, "height");
		String colspan = getElementAttrVal(e, "colspan");
		String rowspan = getElementAttrVal(e, "rowspan");
		// 属性值
		String valType = getElementAttrVal(e, "valType");
		// 文本显示
		String content = getElementAttrVal(e, "content");
		String childtype = getElementAttrVal(e, "childtype");
		String childType = getElementAttrVal(e, "childType");
		String ischild = getElementAttrVal(e, "ischild");
		String companType = getElementAttrVal(e, "companType");
		colspan = colspan == null ? "1" : colspan;
		rowspan = rowspan == null ? "1" : rowspan;
		tdMap.put("id", metricId);
		if(type.equals("tabPanel")){
			metricId=getElementAttrVal(e, "metric");
			tdMap.put("id", getElementAttrVal(e, "name"));
		}
	
		tdMap.put("type", type);
		tdMap.put("width", width);
		tdMap.put("height", height);
		tdMap.put("colspan", colspan);
		tdMap.put("rowspan", rowspan);
		tdMap.put("companType", companType);

		// 文本列
		if (content != null) {
			tdMap.put("value", content);
			return tdMap;
		}
		// 属性值
		if ("property".equals(valType)) {
			Object val = queryPropMetric(metricId, instance);
			tdMap.put("value", val);
			return tdMap;
		}
		if ("resourceImg".equals(valType)) {
			String[] str = instance.getCustomPropBykey(resource_img);
			if (null != str && null != str[0] && !"".equals(str[0])) {
				tdMap.put("value", str[0]);
			} else {
				tdMap.put("value", null);
			}
			return tdMap;
		}

		// 如果是信息指标或性能指标做一个转换
		if (metricId != null
				&& ("prefmetric".equals(valType) || "infometric"
						.equals(valType))) {
			metricId = switchMetricId(metricId);
		}
		ResourceMetricDef rmd = capacityService.getResourceMetricDef(
				instance.getResourceId(), metricId);

		tdMap.put("name", rmd == null ? "" : rmd.getName());
		tdMap.put("unit", rmd == null ? "" : rmd.getUnit());
		// 查询指标状态
		if (rmd != null) {
			tdMap.put("status", queryMetricState(instance.getId(), metricId));
		}
		// 信息指标性能指标
		if ("true".equals(ischild)
				&& ("tabs".equals(type) || "tabs".equals(childType))) {
			// 存储 特殊处理 所有子资源
			if ("DiskArray".equals(instance.getCategoryId())
					&& "storageDevice,accessDevice".equals(childtype)) {
				List<Long> allChildInstanceIdList = new ArrayList<Long>();
				Map<String, List<Long>> instanceIdMap = new HashMap<String, List<Long>>();
				Map<String, Map<String, Object>> allChildMap = new HashMap<String, Map<String, Object>>();
				List<ResourceInstance> childrenRes = instance.getChildren();
				for (int i = 0; childrenRes != null && i < childrenRes.size(); i++) {
					int count = 0;
					ResourceInstance childRi = childrenRes.get(i);
					if (!allChildMap.containsKey(childRi.getChildType())) {
						Map<String, Object> childMap = new HashMap<String, Object>();
						childMap.put("type", childRi.getChildType());
						childMap.put("name", capacityService
								.getResourceDefById(childRi.getResourceId())
								.getName());
						childMap.put("critical", 0);
						allChildMap.put(childRi.getChildType(), childMap);
						count = 1;
						// 分组资源实例ID
						List<Long> childInstanceIdList = new ArrayList<Long>();
						childInstanceIdList.add(childRi.getId());
						instanceIdMap.put(childRi.getChildType(),
								childInstanceIdList);
					} else {
						count = (int) allChildMap.get(childRi.getChildType())
								.get("count") + 1;
						// 分组资源实例ID
						instanceIdMap.get(childRi.getChildType()).add(
								childRi.getId());
					}
					allChildMap.get(childRi.getChildType()).put("count", count);
					// 所有子资源实例ID
					allChildInstanceIdList.add(childRi.getId());
				}
				if (!allChildInstanceIdList.isEmpty()) {
					List<InstanceStateData> isdList = instanceStateService
							.findStates(allChildInstanceIdList);
					for (int i = 0; isdList != null && i < isdList.size(); i++) {
						InstanceStateData isd = isdList.get(i);
						if (isd.getState() == InstanceStateEnum.CRITICAL) {
							for (String key : instanceIdMap.keySet()) {
								if (instanceIdMap.get(key).contains(
										isd.getInstanceID())) {
									allChildMap.get(key).put(
											"critical",
											((int) allChildMap.get(key).get(
													"critical") + 1));
									break;
								}
							}
						}
					}
				}
				// 如果为空则显示为零的数据
				String[] childResourceIds = capacityService.getResourceDefById(
						instance.getResourceId()).getChildResourceIds();
				for (int i = 0; childResourceIds != null
						&& i < childResourceIds.length; i++) {
					ResourceDef childDef = capacityService
							.getResourceDefById(childResourceIds[i]);
					if (!allChildMap.containsKey(childDef.getType())) {
						Map<String, Object> childMap = new HashMap<String, Object>();
						childMap.put("type", childDef.getType());
						childMap.put("name", childDef.getName());
						childMap.put("count", 0);
						childMap.put("critical", 0);
						allChildMap.put(childDef.getType(), childMap);
					}
				}
				tdMap.put("value", allChildMap);
				tdMap.put("metrics", getElementAttrVal(e, "metrics"));
			}
			tdMap.put("title", getElementAttrVal(e, "title"));
			tdMap.put("childtype", getElementAttrVal(e, "childtype").split(","));
			return tdMap;
		}

		if ("alarm".equals(type) || "alarm".equals(childType)) {// 告警统计数据展示
			long  startTime=System.currentTimeMillis();
		try {
		
				List<Long> ids = new ArrayList<Long>();
				if (instance.getParentId() != 0) {
					/*	List<ResourceInstance> instances = instance.getChildren();
					if (instances.size() != 0) {
						for (int i = 0; i < instances.size(); i++) {
							ids.add(instance.getId());
						}
						ids.add(instance.getId());
					}*/
					if(instance.getParentInstance()!=null){
						ids.add(instance.getParentInstance().getId());
					}
					
				} else {
					ids.add(instance.getId());
				}
				Map<String, Integer> result = this.getHomeDataByID(ids);
				List<AlarmEvent> events = this.getOneAlarmInfo(instance.getId());

				tdMap.put("title", getElementAttrVal(e, "title"));
				tdMap.put("chartData", result);
				if ("alarm".equals(childType)) {
					tdMap.put("childtype", childType);
				}

				tdMap.put("divData", events!=null && events.size() != 0 ? events.get(0) : null);
				long  endTime=System.currentTimeMillis();
				long time=(endTime-startTime);
				logger.info("alarm query time :"+time);
				return tdMap;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error("query resourceDetail alarm error:"+e1);
			tdMap.put("title", getElementAttrVal(e, "title"));
			return tdMap;
		}
		}
		if ("mulitgraph".equals(type)) {// 性能信息曲线
			tdMap.put("title", getElementAttrVal(e, "title"));
			String metricid = getElementAttrVal(e, "metric");
			tdMap.put("metric", getElementAttrVal(e, "metric"));
			Date dateStart = new Date();
			Date dateEnd = DateUtil.subDay(dateStart, 1);

			// HighChartsDataBo dataBo=
			// metricChartsDataApi.getHighChartsDataByTime(instance.getId(),
			// metricid, dateEnd, dateStart);
			// tdMap.put("chartData", dataBo);
			return tdMap;
		}

		if ("tabPanel".equals(type)) {
			try {
				long  startTime=System.currentTimeMillis();

				tdMap.put("title", getElementAttrVal(e, "title"));
				tdMap.put("childtype", getElementAttrVal(e, "childType"));
				String metricIdStr=metricId;

				if(metricId.equals("AmbientTemps")){
					metricIdStr="AmbientTemp";
				}else if(metricId.equals("DiskUsagePercentages")){
					metricIdStr="DiskUsagePercentage";
				}
				MetricData data=	metricDataService.getMetricPerformanceData(instance.getId(), metricIdStr);
				if(data!=null){
				if(data.getData().length!=0){
					tdMap.put("value", data.getData()[0]);
				}else{
					tdMap.put("value", null);
				}
				
				}
			long  endTime=System.currentTimeMillis();
			long time=(endTime-startTime);
			logger.info("tabpanel query time :"+time);
				return tdMap;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.error("query resourceDetail tabPanel error:"+e1);
				tdMap.put("title", getElementAttrVal(e, "title"));
				tdMap.put("childtype", getElementAttrVal(e, "childType"));
				return tdMap;
			}

		}
		if ("bar".equals(type)) {
			long  startTime=System.currentTimeMillis();
	
	
			try {
				List<Long> idsTemp= new ArrayList<Long>(); 
				if(instance.getParentId()==0){

			List<ResourceInstance> instances=	instance.getChildren();
			if(instances!=null && instances.size()!=0){
				for (int i = 0; i < instances.size(); i++) {
					idsTemp.add(instances.get(i).getId());
				}
			}
			idsTemp.add(instance.getId());
		}else{
			idsTemp.add(instance.getId());
		}
		long[] ids= new long[idsTemp.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i]=idsTemp.get(i).longValue();
		}
				List<MetricData> metricdatas=	metricDataService.findTop("ifOctetsSpeed", ids, 5);
//				ResourceMetricDef 	def=	capacityService.getResourceMetricDef(instance.getResourceId(), "ifOctetsSpeed");
//			System.out.println(def==null?null:def.getUnit());
				//if(metricData!=null  && metricData){
				List<String> topcate = new ArrayList<String>();
				List<String> topdata = new ArrayList<String>();
				Map<String, Object> valueData = new HashMap<String, Object>();
				List<Map<String, Object>> topdatas = new ArrayList<Map<String, Object>>();
					if(metricdatas!=null  && metricdatas.size()!=0){
						for (int i = 0; i < metricdatas.size(); i++) {
					MetricStateData mdata=metricStateService.getMetricState(Long.valueOf(metricdatas.get(i).getResourceInstanceId()), metricdatas.get(i).getMetricId());
					Map<String, Object> dataObj = new HashMap<String, Object>();
				
						ResourceInstance resourceInstance=resourceInstanceService.getResourceInstance(Long.valueOf(metricdatas.get(i).getResourceInstanceId()));
						topcate.add(resourceInstance!=null?resourceInstance.getName():"");
						String nameStr=	UnitTransformUtil.transform(metricdatas.get(i).getData()[0],"bps");//通过接口查单位
					
						dataObj.put("name", nameStr);
						dataObj.put("y", metricdatas.get(i).getData()[0]);
						dataObj.put("color", "");//getMetricStateEnumString(mdata)
						dataObj.put("state", mdata==null ? "NORMAL" :mdata.getState());
						topdatas.add(dataObj);
					
						}
					}
			/*	// 接口流量top5
				List<TopChartPo> newlist = new ArrayList<TopChartPo>();
				List<Map<String, String>> list = resourceApplyApi
						.getMetricInfo(instance.getId(), "NetInterface",
								"NetworkDevice", user);

				Map<String, Object> valueData = new HashMap<String, Object>();
				if (list.size() != 0) {
					for (int i = 0; i < list.size(); i++) {
						Map<String, String> maptemp = new HashMap<String, String>();
						TopChartPo po = new TopChartPo();
						maptemp = list.get(i);
						po.setName(maptemp.get("resourceRealName"));
						*//*** 指标没有采集值或者指标状态为不可用不展示 **//*
						if (maptemp.get("ifOctetsSpeed_ForSort") == null
								|| maptemp.get("ifOctetsSpeed_ForSort").equals(
										"--")
								|| maptemp.get("resourceState").equals(
										"CRITICAL")
								|| maptemp.get("resourceState").equals(
										"CRITICAL_NOTHING")) {
							// po.setIfOctetsSpeed_forSort(0.0);
						} else {
							// ???
							po.setIfOctetsSpeed_forSort(Double
									.parseDouble(maptemp
											.get("ifOctetsSpeed_ForSort")));
							po.setIfOctetsSpeed(maptemp.get("ifOctetsSpeed"));
							MetricStateData data = metricStateService
									.getMetricState(Long.parseLong(maptemp
											.get("instanceid")),
											"ifOctetsSpeed");
							po.setStatues(getMetricStateEnumString(data));
							newlist.add(po);
						}

					}
					if (newlist != null || newlist.size() != 0) {
						Collections.sort(newlist, new Comparator<TopChartPo>() {
							@Override
							public int compare(TopChartPo o1, TopChartPo o2) {
								// TODO Auto-generated method stub
								if (o1.getIfOctetsSpeed_forSort() < o2
										.getIfOctetsSpeed_forSort()) {
									return 1;
								} else if (o1.getIfOctetsSpeed_forSort() > o2
										.getIfOctetsSpeed_forSort()) {
									return -1;
								} else {
									return 0;
								}
							}
						});
						List<String> cate = new ArrayList<String>();
						List<String> data = new ArrayList<String>();
						List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
						for (int i = 0; i < newlist.size(); i++) {
							Map<String, Object> dataObj = new HashMap<String, Object>();
							if (i < 5) {
								cate.add(newlist.get(i).getName());
								dataObj.put("name", newlist.get(i)
										.getIfOctetsSpeed());
								dataObj.put("y", newlist.get(i)
										.getIfOctetsSpeed_forSort());
								dataObj.put("color", newlist.get(i)
										.getStatues());

								datas.add(dataObj);
							}
						}
						valueData.put("categories", cate);
						valueData.put("data", data);
						valueData.put("Sortdata", datas);
					} else {
						valueData.put("categories", "");
						valueData.put("data", "");
						valueData.put("Sortdata", "");
					}

				}*/
				valueData.put("categories", topcate);
				valueData.put("data", topdata);
				valueData.put("Sortdata", topdatas);
				tdMap.put("title", getElementAttrVal(e, "title"));
				tdMap.put("value", valueData);
				long  endTime=System.currentTimeMillis();
				long time=(endTime-startTime);
				logger.info("bar query time :"+time);
				return tdMap;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.error("query resourceDetail bar error:"+e1);
				tdMap.put("title", getElementAttrVal(e, "title"));
				return tdMap;
			}
		}
		if (("tabs-full".equals(type) || "tabs".equals(type) || "tabs"
				.equals(childType))
				&& "alarm-unRestore,alarm-restore".equals(childtype)) {
			tdMap.put("title", getElementAttrVal(e, "title"));
			tdMap.put("childtype", getElementAttrVal(e, "childtype").split(","));

		}

		// 子panel块或tabs块
		if ("true".equals(getElementAttrVal(e, "ischild"))
				&& !"tabs".equals(childType)) {
			// 自定义子资源面板
			if ("commonChildrenPanel".equals(type)
					|| "commonOneChildrenPanel".equals(type)) {
				List<Map<String, Object>> val = queryCommonChild(e, instance);

				tdMap.put("value", val);
				tdMap.put("title", getElementAttrVal(e, "title"));
				// tdMap.put("childtype", type);
			} else {
				List<Map<String, Object>> val = queryChildMetric(e, instance);

				tdMap.put("value", val);
				tdMap.put("title", getElementAttrVal(e, "title"));
				tdMap.put("childtype",
						getElementAttrVal(e, "childtype").split(","));
			}
			return tdMap;
		}

		if ("commonOneMetricPanel".equals(type)) {
			long  startTime=System.currentTimeMillis();
		
			String metric = getElementAttrVal(e, "metric");
			String metricType = getElementAttrVal(e, "metricType");
			String resourceStateUIClass = getElementAttrVal(e,
					"resourceStateUIClass");
			String green = "", red = "";
			String valueResult = "";
			if (resourceStateUIClass.indexOf("||") > 0) {
				String[] classArr = resourceStateUIClass.split("\\|\\|");
				green = classArr[0];
				red = classArr[1];
				tdMap.put("normalClass", green);
			} else {
				tdMap.put("normalClass", resourceStateUIClass);
			}

			if (null != metric && !"".equals(metric)) {
				ResourceMetricDef rmdf = capacityService.getResourceMetricDef(
						instance.getResourceId(), metric);
				String unit =rmdf==null? "": rmdf.getUnit();
				valueResult =rmdf==null? "": rmdf.getName() + " : ";
				// String unit = "s";
				String value = "";
				if ("prefmetric".equals(metricType)) {
					// per metric
					value = queryPrefMetric(metric, instance);

				} else {
					// 默认信息指标
					value = queryInfoMetric(metric, instance);
				}
				if (null != value && !"".equals(value) && !"N/A".equals(value)) {
					valueResult += UnitTransformUtil.transform(value, unit);

					// 如果需要根据值,显示不同class
					if ("SignalDetec".equals(instance.getResourceId())) {
						if ("Video".equals(metric)) {
							switch (value) {
							case "恢复告警":// 正常
								break;
							default:
								tdMap.put("normalClass", red);
								break;
							}
						} else if ("LeftChannel".equals(metric)
								|| "RightChannel".equals(metric)) {
							switch (value) {
							case "恢复告警":// 正常
								break;
							default:
								tdMap.put("normalClass", red);
								break;
							}
						}
					}
				}
			} else {
				if ("tabPic".equals(type)) {
					String[] metrics = getElementAttrVal(e, "metrics").split(
							",");
					for (int i = 0; i < metrics.length; i++) {

					}
				}
			}

			tdMap.put("value", valueResult);
			// tdMap.put("value", "test");
			tdMap.put("title", getElementAttrVal(e, "title"));
			long  endTime=System.currentTimeMillis();
			long time=(endTime-startTime);
			logger.info("commonOneMetricPanel query time :"+time);
			return tdMap;

		}

		if ("solidgauge".equals(type)) {
			long  startTime=System.currentTimeMillis();
		
			tdMap.put("title", getElementAttrVal(e, "title"));
			tdMap.put("metricType", getElementAttrVal(e, "metricType"));
			tdMap.put("soliType", getElementAttrVal(e, "soliType"));
			String metricType = getElementAttrVal(e, "metricType");
			if ("instancestate".equals(metricType)) {
				String state = InstanceStateEnum.NORMAL.name();
				tdMap.put("collectTime", (new Date()).getTime());
				if (InstanceLifeStateEnum.NOT_MONITORED.equals(instance
						.getLifeState())) {
					state = InstanceLifeStateEnum.NOT_MONITORED.toString();
				} else {
					InstanceStateData isd = instanceStateService
							.getState(instance.getId());

					state = isd == null ? InstanceStateEnum.NORMAL.toString()
							: isd.getState().toString();
					tdMap.put("collectTime", isd.getCollectTime().getTime());
				}
				tdMap.put("status", state);
				return tdMap;
			} else {
				String metric = getElementAttrVal(e, "metric");
				ResourceMetricDef rmdf = capacityService.getResourceMetricDef(
						instance.getResourceId(), metric);
				String unit =rmdf==null?" ": rmdf.getUnit();
				// String unit = "mb";

				String stateStr = "";
				MetricStateData msd = queryMetricStateData(instance.getId(),
						metric);
				if (null != msd && null != msd.getState()) {
					stateStr = msd.getState().name();
					tdMap.put("collectTime", msd.getCollectTime().getTime());
				} else {
					stateStr = MetricStateEnum.NORMAL.name();
					tdMap.put("collectTime", (new Date()).getTime());
				}
				tdMap.put("status", stateStr);

				if ("infometric".equals(metricType)) {

					MetricData md = queryInfoMetricData(metric, instance);
					if (null != md) {
						String val = md.getData()[0];

						val = "".equals(val) ? val : UnitTransformUtil
								.transform(val, unit);
						tdMap.put("value", val);
						tdMap.put("collectTime", md.getCollectTime().getTime());
					} else {
						tdMap.put("value", "");
					}

				} else if ("prefmetric".equals(metricType)) {
					Map<String, ?> value = queryPrefMetricData(metric, instance);
					if (null != value && value.containsKey(metric)) {
						String resultValuePer = value.get(metric).toString();
						resultValuePer = "".equals(resultValuePer) ? resultValuePer
								: UnitTransformUtil.transform(resultValuePer,
										unit);
						tdMap.put("value", resultValuePer);
						String mc = metric + "CollectTime";
						if (value.containsKey(mc)) {
							tdMap.put("collectTime", value.get(mc));
						}
					} else {
						tdMap.put("value", "");
					}

					// String mc = metric+"CollectTime";
					// if(value.containsKey(mc)){
					// tdMap.put("collectTime", value.get(mc));
					// }
				}

				tdMap.put("unit", unit);
				long  endTime=System.currentTimeMillis();
				long time=(endTime-startTime);
				logger.info("solidgauge query time :"+time);
				return tdMap;
			}
		}

/*		// 曲线图要历史数据
		if ("prefmetric".equals(valType) && "graph".equals(type)) {
			Date dateEnd = new Date();
			Date dateStart = DateUtil.subHour(dateEnd, 4);
			List<MetricData> metricDataList = null;
			try {
				metricDataList = metricDataService.queryHistoryMetricData(
						metricId, instance.getId(), dateStart, dateEnd);
			} catch (Exception e1) {
				metricDataList = new ArrayList<MetricData>();
				logger.error("createTdMap", e1);
			}
			Object[] metricDataArray = new Object[0];
			Object[] metricDataColTimArray = new Object[0];
			if (metricDataList != null) {
				metricDataArray = new Object[metricDataList.size()];
				metricDataColTimArray = new Object[metricDataList.size()];

				for (int j = 0; j < metricDataList.size(); j++) {
					MetricData metricData = metricDataList.get(j);
					if (metricData.getData() == null
							|| metricData.getData()[0] == null) {
						metricDataArray[j] = null;
					} else {
						metricDataArray[j] = Double.valueOf(metricData
								.getData()[0]);
					}
					metricDataColTimArray[j] = new SimpleDateFormat("HH:mm")
							.format(metricData.getCollectTime());
				}
			}

			tdMap.put("historyData", metricDataArray);
			tdMap.put("historyDataColTim", metricDataColTimArray);
			tdMap.put("title", getElementAttrVal(e, "title"));
			return tdMap;
		}*/
		// tables
		if ("table".equals(type)) {
			String resourceid = getElementAttrVal(e, "resourceid");
			List<Map<String, Object>> val = new ArrayList<Map<String, Object>>();
			if ("VMWareDatastore".equals(resourceid)) {
				Map<String, Object> datastoreMap = getRelationDatastore(
						instance, false);
				List<Long> datastoreIdList = !datastoreMap.isEmpty() ? (List<Long>) datastoreMap
						.get("idList") : new ArrayList<Long>();
				try {
					for (Long instanceId : datastoreIdList) {
						ResourceInstance vmRi = resourceInstanceService
								.getResourceInstance(instanceId);
						Map<String, Object> metric = new HashMap<String, Object>();
						String[] metrics = getElementAttrVal(e, "metrics")
								.split(",");
						for (int i = 0; metrics != null && i < metrics.length; i++) {
							metric.putAll(queryMetric(metrics[i], vmRi));
						}
						if (!metric.isEmpty()) {
							val.add(metric);
						}
					}
				} catch (InstancelibException e1) {
					logger.error("table.equals(type)", e1);
				}
			}
			tdMap.put("value", val);
			tdMap.put("title", getElementAttrVal(e, "title"));
			return tdMap;
		}
		if ("metricTabs".equals(getElementAttrVal(e, "type"))) {
			long  startTime=System.currentTimeMillis();
			String[] metrics = getElementAttrVal(e, "metrics").split(",");
			String[] metricsName = getElementAttrVal(e, "metricsName").split(
					",");
			boolean flag = true;
			if (null == metricsName || metricsName.length < metrics.length) {
				flag = false;
			}

			List<Map<String, Object>> val = new ArrayList<Map<String, Object>>();
			for (int i = 0; metrics != null && i < metrics.length; i++) {
				Map<String, Object> map = queryMetricForListParse(metrics[i],
						instance);
				if (map.size() > 0) {
					if (flag && null != metricsName[i]
							&& !"".equals(metricsName[i])) {
						map.put("metricName", metricsName[i]);
					}
					val.add(map);
				}
			}
			tdMap.put("value", val);
			tdMap.put("title", getElementAttrVal(e, "title"));
			long  endTime=System.currentTimeMillis();
			long time=(endTime-startTime);
			logger.info("metricTabs query time :"+time);
			return tdMap;
		}
		if ("environmentPanel".equals(type) ||  "environmentPanel".equals(childType)) {
			String[] metrics = getElementAttrVal(e, "metrics").split(",");
			List<Map<String, Object>> val = new ArrayList<Map<String, Object>>();
			for (int i = 0; metrics != null && i < metrics.length; i++) {
				Map<String, Object> map = queryMetricForListParse(metrics[i],
						instance);
				if (map.size() > 0) {
					val.add(map);
				}
			}

			tdMap.put("value", val);
			tdMap.put("title", getElementAttrVal(e, "title"));
			return tdMap;
		}
		if ("tableMetric".equals(type)) {
			String[] metrics = getElementAttrVal(e, "metrics").split(",");
			List<Map<String, Object>> val = new ArrayList<Map<String, Object>>();
			for (int i = 0; metrics != null && i < metrics.length; i++) {
				Map<String, Object> map = queryMetricForListParse(metrics[i],
						instance);
				if (map.size() > 0) {
					val.add(map);
				}
			}
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put("value",
			// "rerqe,rqrqerqe,rqereqrq,rqre,rqrqrq,rqreq,re45tq4t4t,r4tt4gdgrehyy,jwthbs");
			// map.put("metricName", "test");
			// map.put("metricStr", metrics[0]);
			// map.put("metricStatus","NORMAL");
			// val.add(map);

			tdMap.put("value", val);
			tdMap.put("title", getElementAttrVal(e, "title"));
			return tdMap;
		}
		if ("statisticalData".equals(valType)) {

			String resourceid = getElementAttrVal(e, "resourceid");
			List<List<String>> val = new ArrayList<List<String>>();
			if ("VMWareVM".equals(resourceid)) {
				Map<String, Object> vmMap = getRelationVM(instance, false);
				List<Long> vmIdList = !vmMap.isEmpty() ? (List<Long>) vmMap
						.get("idList") : new ArrayList<Long>();
				try {
					List<ResourceInstance> riList = resourceInstanceService
							.getResourceInstances(vmIdList);
					for (int i = 0; riList != null && i < riList.size(); i++) {
						ResourceInstance vmRi = riList.get(i);
						String metricValue = queryInfoMetric(metricId, vmRi);
						if (!"".equals(metricValue)) {
							List<String> vm = new ArrayList<String>();
							vm.add(vmRi.getShowName());
							vm.add(metricValue);
							val.add(vm);
						}
					}
					String topN = getElementAttrVal(e, "TopN");
					if (topN != null && !"".equals(topN)
							&& val.size() > Integer.valueOf(topN)) {
						Collections.sort(val, new Comparator<List<String>>() {
							public int compare(List<String> o1, List<String> o2) {
								return (int) (Float.valueOf(o2.get(1)) - Float
										.valueOf(o1.get(1)));
							}
						});
						val = val.subList(0, Integer.valueOf(topN));
					}
					tdMap.put(
							"unit",
							capacityService.getResourceMetricDef("VMWareVM",
									metricId).getUnit());
					tdMap.put("value", val);
					tdMap.put("title", getElementAttrVal(e, "title"));
					return tdMap;
				} catch (InstancelibException e1) {
					logger.error("statisticalData.equals(valType)", e1);
				}
			} else if ("DiskArray".equals(instance.getCategoryId())) {
				tdMap.put("resourceId", instance.getResourceId());
				tdMap.put("categoryId", instance.getCategoryId());
				String unit = capacityService.getResourceMetricDef("IBMDS",
						"TotalManagedSpace").getUnit();
				String totalSpaceStr = queryInfoMetric("TotalManagedSpace",
						instance);
				String remainSpaceStr = queryInfoMetric("RemainManagedSpace",
						instance);
				if (!"".equals(totalSpaceStr) && !"".equals(remainSpaceStr)) {
					List<String> usedSpace = new ArrayList<String>();
					usedSpace.add("已分配");
					usedSpace.add(String.valueOf(Float.valueOf(totalSpaceStr)
							- Float.valueOf(remainSpaceStr)));
					val.add(usedSpace);
					List<String> remainSpace = new ArrayList<String>();
					remainSpace.add("未分配");
					remainSpace.add(remainSpaceStr);
					val.add(remainSpace);
					// tdMap.put("totalSpace", totalSpaceStr + unit);
				}
				tdMap.put("unit", unit);
				tdMap.put("value", val);
				tdMap.put("title", getElementAttrVal(e, "title"));
				return tdMap;
			}else if("FusionComputeClusters".equals(instance.getCategoryId())){
				String forType = getElementAttrVal(e, "forType");
				if(forType.equals("cpu")){//查询cpu容量
					String unit = capacityService.getResourceMetricDef("FusionComputeCluster",
							"totalCpuSize").getUnit();
					String totalCpuSize = queryInfoMetric("totalCpuSize",
							instance);
					String usableCpuSize = queryInfoMetric("usableCpuSize",
							instance);
					if (!"".equals(totalCpuSize) && !"".equals(usableCpuSize)) {
						List<String> usedSpace = new ArrayList<String>();
						usedSpace.add("已使用");
						usedSpace.add(String.valueOf(Float.valueOf(totalCpuSize)
								- Float.valueOf(usableCpuSize)));
						val.add(usedSpace);
						List<String> usableCpu = new ArrayList<String>();
						usableCpu.add("未使用");
						usableCpu.add(usableCpuSize);
						val.add(usableCpu);
						// tdMap.put("totalSpace", totalSpaceStr + unit);
					}
					tdMap.put("value", val);
					tdMap.put("title", getElementAttrVal(e, "title"));
					tdMap.put("unit", unit);
					return tdMap;
				}else{//查询内存容量
					String unit = capacityService.getResourceMetricDef("FusionComputeCluster",
							"totalMemSize").getUnit();
					//查询内存容量
					String memunit = capacityService.getResourceMetricDef("FusionComputeCluster",
							"totalMemSize").getUnit();
					String totalMemSize = queryInfoMetric("totalCpuSize",
							instance);
					String usableMemSize = queryInfoMetric("usableCpuSize",
							instance);
					if (!"".equals(totalMemSize) && !"".equals(usableMemSize)) {
						List<String> memusedSpace = new ArrayList<String>();
						memusedSpace.add("已使用");
						memusedSpace.add(String.valueOf(Float.valueOf(totalMemSize)
								- Float.valueOf(usableMemSize)));
						val.add(memusedSpace);
						List<String> usableMem = new ArrayList<String>();
						usableMem.add("未使用");
						usableMem.add(usableMemSize);
						val.add(usableMem);
						tdMap.put("unit", unit);
						tdMap.put("value", val);
						tdMap.put("title", getElementAttrVal(e, "title"));
					
						return tdMap;
				}
//				tdMap.put("resourceId", instance.getResourceId());
//				tdMap.put("categoryId", instance.getCategoryId());
				
			
					
					
				}
//				tdMap.put("resourceId", instance.getResourceId());
//				tdMap.put("categoryId", instance.getCategoryId());
				
			}
			
		}
		if ("commonMetricPie".equals(type)) {
			List<List<String>> val = new ArrayList<List<String>>();
			String[] metrics = getElementAttrVal(e, "metrics").split(",");
			// String metricType = getElementAttrVal(e, "metricType");
			ResourceMetricDef rmdf = capacityService.getResourceMetricDef(
					instance.getResourceId(), metrics[0]);
			String unit = rmdf.getUnit();// "Byte";//rmdf.getUnit();

			for (String metric : metrics) {
				ResourceMetricDef rmdfTemp = capacityService
						.getResourceMetricDef(instance.getResourceId(), metric);
				String value = queryPrefMetric(metric, instance);
				if (value == null || value.equals("N/A")) {
					value = queryInfoMetric(metric, instance);
					if (value == null || value.equals("N/A")) {
						value = "";
					}
				}

				List<String> usedSpace = new ArrayList<String>();
				usedSpace.add(rmdfTemp.getName());
				usedSpace.add(value);
				val.add(usedSpace);
			}

			// if("prefmetric".equals(metricType)){
			// //per metric
			// for(String metric:metrics){
			// ResourceMetricDef rmdfTemp =
			// capacityService.getResourceMetricDef(instance.getResourceId(),metric);
			// String value = queryPrefMetric(metric, instance);
			//
			// List<String> usedSpace = new ArrayList<String>();
			// usedSpace.add(rmdfTemp.getName());
			// usedSpace.add(value);
			// val.add(usedSpace);
			// }
			// }else{
			// //默认信息指标
			// for(String metric:metrics){
			// ResourceMetricDef rmdfTemp =
			// capacityService.getResourceMetricDef(instance.getResourceId(),metric);
			// String value = queryInfoMetric(metric, instance);
			//
			// List<String> usedSpace = new ArrayList<String>();
			// usedSpace.add(rmdfTemp.getName());
			// usedSpace.add(value);
			// val.add(usedSpace);
			// }
			// }
			// List<String> usedSpace = new ArrayList<String>();
			// usedSpace.add("测试测试测试");
			// usedSpace.add("12345");
			// List<String> usedSpace1 = new ArrayList<String>();
			// usedSpace1.add("测试2");
			// usedSpace1.add("22345");
			// List<String> usedSpace2 = new ArrayList<String>();
			// usedSpace2.add("测试3");
			// usedSpace2.add("32345");
			// List<String> usedSpace3 = new ArrayList<String>();
			// usedSpace3.add("测试测试测试");
			// usedSpace3.add("12345");
			// List<String> usedSpace4 = new ArrayList<String>();
			// usedSpace4.add("测试2");
			// usedSpace4.add("22345");
			// List<String> usedSpace5 = new ArrayList<String>();
			// usedSpace5.add("测试3");
			// usedSpace5.add("32345");
			// val.add(usedSpace);
			// val.add(usedSpace1);
			// val.add(usedSpace2);
			// val.add(usedSpace3);
			// val.add(usedSpace4);
			// val.add(usedSpace5);

			tdMap.put("unit", unit);
			tdMap.put("value", val);
			tdMap.put("title", getElementAttrVal(e, "title"));
		}

		if ("prefmetric".equals(valType)) {
			long  startTime=System.currentTimeMillis();
			String val = queryPrefMetric(metricId, instance);
			tdMap.put("title", getElementAttrVal(e, "title"));
			tdMap.put("value", val);
			// 是否有告警
			try {
				// ProfileMetric pm =
				// profileService.getMetricByInstanceIdAndMetricId(instance.getId(),metricId);
				ProfileMetric pm = profileService
						.getProfileMetricByInstanceIdAndMetricId(
								instance.getId(), metricId);
				List<Long> nums = new ArrayList<Long>();
				MetricStateData data=	metricStateService.getMetricState(instance.getId(),metricId );
				tdMap.put("color", this.getMetricStateEnumString(data));
				if (pm != null) {
					tdMap.put("isAlarm", pm.isAlarm());
				/*	List<ProfileThreshold> list = pm.getMetricThresholds();
					nums.add(0l);
					if (list != null && list.size() != 0) {
						for (int i = 1; i < list.size(); i++) {
							System.out.println(metricId+"阈值： "+list.get(i)
									.getThresholdValue());
							nums.add(list.get(i)
									.getThresholdValue()==null ? 0 : Long.parseLong(list.get(i)
											.getThresholdValue()));

						}
					}
					if (metricId.equals("icmpDelayTime")) {
						try {
							DiscoverProp prop = discoverPropService
									.getPropByInstanceAndKey(instance.getId(),
											"IcmpTimeout");
							if (prop != null) {
								long timeout = (long) (Long.parseLong(prop
										.getValues()[0]) * 1.5);

								nums.add(timeout);
							}
						} catch (InstancelibException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							nums.add(3000l);
						}// 查询最大时长
					} else {
						nums.add(100l);
					}

					tdMap.put("thresholdnum", nums);*/
				} else {
					tdMap.put("isAlarm", false);
				}
			} catch (ProfilelibException exception) {
				tdMap.put("isAlarm", false);
				logger.error("createTdMap getMetricByInstanceIdAndMetricId:",
						exception);
			}
			// 性能指标 内存利用率
			try {
				String parentCategoryId = capacityService
						.getCategoryById(instance.getCategoryId())
						.getParentCategory().getId();
				if (MetricIdConsts.METRIC_MEME_RATE.equals(metricId)
						|| "MEMPercent".equals(metricId)) {
					if (CapacityConst.NETWORK_DEVICE.equals(parentCategoryId)
							|| CapacityConst.HOST.equals(parentCategoryId)) {
						tdMap.putAll(queryMetric("totalMemSize", instance));
						tdMap.putAll(queryMetric("memPoolUsed", instance));
					} else if ("VMware".equals(parentCategoryId)
							|| "Xen".equals(parentCategoryId)
							|| "FusionCompute".equals(parentCategoryId)
							|| "FusionComputeOnePointThree"
									.equals(parentCategoryId)) {
						Map<String, Object> totalMemMap = queryMetric(
								"totalMemSize", instance), newTotalMemMap = new HashMap<String, Object>();
						totalMemMap = totalMemMap.isEmpty() ? queryMetric(
								"MEMVMSize", instance) : totalMemMap;
						Iterator<String> totalMemedIter = totalMemMap.keySet()
								.iterator();
						while (totalMemedIter.hasNext()) {
							String totalMemedKey = totalMemedIter.next();
							if (totalMemedKey.endsWith("Status")) {
								newTotalMemMap.put("totalMemSize" + "Status",
										totalMemMap.get(totalMemedKey));
							} else if (totalMemedKey.equals("MEMVMSize")) {
								newTotalMemMap.put("totalMemSize",
										totalMemMap.get(totalMemedKey));
							}
						}
						newTotalMemMap.putAll(totalMemMap);
						tdMap.putAll(newTotalMemMap);

						Map<String, Object> memedMap = queryMetric(
								"MEMActiveSize", instance), newMemedMap = new HashMap<String, Object>();
						memedMap = memedMap.isEmpty() ? queryMetric("MEMUsed",
								instance) : memedMap;
						// cluster 内存已使用 MEMConsume
						memedMap = memedMap.isEmpty() ? queryMetric("UsedMEM",
								instance) : memedMap;

						Iterator<String> memedIter = memedMap.keySet()
								.iterator();
						while (memedIter.hasNext()) {
							String memedKey = memedIter.next();
							if (memedKey.endsWith("Status")) {
								newMemedMap.put("memPoolUsed" + "Status",
										memedMap.get(memedKey));
							} else if (memedKey.equals("MEMActiveSize")) {
								newMemedMap.put("memPoolUsed",
										memedMap.get(memedKey));
							} else if (memedKey.equals("MEMUsed")) {
								newMemedMap.put("memPoolUsed",
										memedMap.get(memedKey));
							} else if (memedKey.equals("UsedMEM")) {
								newMemedMap.put("memPoolUsed",
										memedMap.get(memedKey));
							}
						}
						newMemedMap.putAll(memedMap);
						tdMap.putAll(newMemedMap);
					}
				}
			} catch (Exception e1) {
				logger.error("capacityService.getCategoryById", e1);
			}
			long  endTime=System.currentTimeMillis();
			long time=(endTime-startTime);
			logger.info("prefmetric query time :"+time);
			return tdMap;
		}
		if ("infometric".equals(valType)) {
			String val = queryInfoMetric(metricId, instance);
			val = "".equals(val) ? val : UnitTransformUtil.transform(val,
					(String) tdMap.get("unit"));
			tdMap.put("value", val);
			return tdMap;
		}
		if ("instancestate".equals(valType)) {
			String val = queryInstanceState(instance);
			tdMap.put("value", val);
			return tdMap;
		}
		if ("availability".equals(valType)) {
			String val = queryInstanceState(instance);
			tdMap.put("value", val);
			return tdMap;
		}

		return tdMap;
	}

	private String getMetricStateEnumString(MetricStateData metricStateData) {
		String rst = "#02bf00";
		if (metricStateData == null) {
			return "#02bf00";
		}
		switch (metricStateData.getState()) {
		case CRITICAL:
			rst = "#DE2022";
			break;
		case SERIOUS:
			rst = "#ff7d03";
			break;
		case WARN:
			rst = "#edb805";
			break;
		case NORMAL:
		case NORMAL_NOTHING:
			rst = "#02bf00";
			break;
		default:
			rst = "#02bf00";
			break;

		}
		return rst;
	}

	/**
	 * 查询属性值
	 * 
	 * @param propName
	 * @return
	 */
	private Object queryPropMetric(String propName, ResourceInstance instance) {
		Object propVal = null;
		switch (propName) {
		case "name":
			propVal = instance.getName();
			break;
		case "showname":
			propVal = instance.getShowName();
			break;
		case "ip":
			propVal = getIpList(instance);
			break;
		case "resourcename":
			propVal = getInstanceResourceType(instance);
			// propVal = propVal + "(" + queryInfoMetric("version", instance) +
			// ")";
			break;
		case "profile":
			try {
				ProfileInfo pfInfo = profileService
						.getBasicInfoByResourceInstanceId(instance.getId());
				propVal = pfInfo == null ? "" : pfInfo.getProfileName();
			} catch (ProfilelibException e) {
				logger.error("queryPropMetric-profile", e);
			}
			break;
		case "domain":
			Domain domain = domainApi.get(instance.getDomainId());
			propVal = domain == null ? "" : domain.getName();
			break;
		case "discoverIp":
			propVal = instance.getShowIP();
			break;
		case "discoverPort":
			propVal = queryInfoMetric(MetricIdConsts.SERVICE_PORT, instance);
			if (propVal == null || "".equals(propVal)) {
				String[] ports = null;
				List<DiscoverProp> discoverPropList = instance
						.getDiscoverProps();
				if (discoverPropList != null) {
					for (DiscoverProp prop : discoverPropList) {
						if (!"port".equalsIgnoreCase(prop.getKey())
								&& prop.getKey().toLowerCase().endsWith("port")) {
							ports = prop.getValues();
							break;
						}
					}
				}
				propVal = ports == null ? "" : ports[0];
			}
			break;
		case "liablePerson":
			try {
				CustomProp prop = customPropService.getPropByInstanceAndKey(
						instance.getId(), "liablePerson");
				if (null != prop && null != prop.getValues()[0]) {
					Long userId = Long.parseLong(prop.getValues()[0]);
					User user = userApi.get(userId);
					// 判断负责人是否存在，避免NullPointerException dfw 20170116
					if (user != null) {
						propVal = user.getName();
					}
				}
			} catch (InstancelibException ex) {
				logger.error(ex.getMessage());
				return propVal;
			}
			break;
		case "liablePersonPhoneNumber":
			try {
				CustomProp prop = customPropService.getPropByInstanceAndKey(
						instance.getId(), "liablePerson");
				if (null != prop && null != prop.getValues()[0]) {
					Long userId = Long.parseLong(prop.getValues()[0]);
					User user = userApi.get(userId);
					// 判断负责人是否存在，避免NullPointerException dfw 20170116
					if (user != null) {
						propVal = user.getMobile();
					}
				}
			} catch (InstancelibException ex) {
				logger.error(ex.getMessage());
				return propVal;
			}
			break;
		default:
			String[] props = instance.getDiscoverPropBykey(propName);

			propVal = props == null ? "" : props[0];
			break;
		}
		return propVal;
	}

	/**
	 * 获取ip地址 id\name
	 * 
	 * @param instance
	 * @return
	 */
	private List<Map<String, String>> getIpList(ResourceInstance instance) {
		List<Map<String, String>> ipList = new ArrayList<Map<String, String>>();
		if (instance.getShowIP() != null) {
			Map<String, String> discoverIpMap = new HashMap<String, String>();
			discoverIpMap.put("id", instance.getShowIP());
			discoverIpMap.put("name", instance.getShowIP());
			ipList.add(discoverIpMap);
		}

		String[] ips = instance.getModulePropBykey(MetricIdConsts.METRIC_IP);
		for (int i = 0; ips != null && i < ips.length; i++) {
			String ip = ips[i];
			Map<String, String> ipMap = new HashMap<String, String>();
			ipMap.put("id", ip);
			ipMap.put("name", ip);
			if (!ipList.contains(ipMap)) {
				ipList.add(ipMap);
			}
		}
		return ipList;
	}

	/**
	 * 查询信息指标
	 * 
	 * @param instanceId
	 * @param metricId
	 * @return
	 */
	private String queryInfoMetric(String metricId, ResourceInstance instance) {
		// 查询信息指标需要过滤
		// MetricData md =
		// metricDataService.getMetricInfoData(instance.getId(),metricId);
		MetricData md = infoMetricQueryAdaptService.getMetricInfoData(
				instance.getId(), metricId);
		String metricVal = md == null ? "" : emptyFirstLastChar(md.getData());
		return metricVal;
	}

	private MetricData queryInfoMetricData(String metricId,
			ResourceInstance instance) {
		// 查询信息指标需要过滤
		// MetricData md =
		// metricDataService.getMetricInfoData(instance.getId(),metricId);
		MetricData md = infoMetricQueryAdaptService.getMetricInfoData(
				instance.getId(), metricId);

		return md;
	}

	/**
	 * 查询接口可用性指标
	 * 
	 * @param instance
	 * @return
	 */
	private String queryInterfaceState(Long instId, String lifeState,
			String ifAdminStatus, String ifOperStatus) {
		// String state = InstanceStateEnum.UNKNOWN_NOTHING.toString();
		String state = InstanceStateEnum.NORMAL.toString();
		if (InstanceLifeStateEnum.NOT_MONITORED.toString().equals(lifeState)) {
			state = InstanceLifeStateEnum.NOT_MONITORED.toString();
		} else {
			InstanceStateData isd = instanceStateService.getState(instId);
			if (isd != null
					&& isd.getState() != InstanceStateEnum.UNKNOWN_NOTHING
					&& isd.getState() != InstanceStateEnum.UNKOWN) {
				if ("up".equals(ifAdminStatus) && "up".equals(ifOperStatus)) {
					state = InstanceStateEnum.NORMAL.toString();
				} else if ("down".equals(ifAdminStatus)
						&& "down".equals(ifOperStatus)) {
					state = InstanceStateEnum.CRITICAL.toString();
				} else if ("up".equals(ifAdminStatus)
						&& "down".equals(ifOperStatus)) {
					state = "ADMNORMAL_OPERCRITICAL";
				}
			} else {
				// state = InstanceStateEnum.UNKNOWN_NOTHING.toString();
				state = InstanceStateEnum.NORMAL.toString();
			}
		}
		return state;
	}

	/**
	 * 查询资源实例可用性指标
	 * 
	 * @param name
	 * @param instance
	 * @return
	 */
	private String queryInstanceState(ResourceInstance instance) {
		// String state = InstanceStateEnum.UNKNOWN_NOTHING.toString();
		String state = InstanceStateEnum.NORMAL.name();
		if (InstanceLifeStateEnum.NOT_MONITORED.equals(instance.getLifeState())) {
			state = InstanceLifeStateEnum.NOT_MONITORED.toString();
		} else {
			InstanceStateData isd = instanceStateService.getState(instance
					.getId());

			// state = isd == null ?
			// InstanceStateEnum.UNKNOWN_NOTHING.toString():
			// isd.getState().toString();
			state = isd == null ? InstanceStateEnum.NORMAL.toString() : isd
					.getState().toString();
		}
		return state;
	}

	private Map<String, String> queryInstanceStateAndCollectState(
			ResourceInstance instance) {
		// String availability = InstanceStateEnum.UNKNOWN_NOTHING.toString();
		String availability = InstanceStateEnum.NORMAL.toString();
		String CollectState = CollectStateEnum.COLLECTIBLE.name();

		if (InstanceLifeStateEnum.NOT_MONITORED.equals(instance.getLifeState())) {
			availability = InstanceLifeStateEnum.NOT_MONITORED.toString();
		} else {
			InstanceStateData isd = instanceStateService.getState(instance
					.getId());

			if (null != isd) {
				if (null != isd.getState()) {
					availability = isd.getState().toString();
				}

				// CollectStateEnum为空认为可采集
				if (null != isd.getCollectStateEnum()) {
					CollectState = isd.getCollectStateEnum().name();
				}
			}
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("availability", availability);
		map.put("CollectState", CollectState);
		return map;
	}

	private String queryMetricState(long instanceId, String metricId) {
		try {
			MetricStateData msd = metricStateService.getMetricState(instanceId,
					metricId);
			// String state = msd == null ? MetricStateEnum.UNKOWN.toString() :
			// msd.getState().toString();
			String state = msd == null ? MetricStateEnum.NORMAL.toString() : msd
					.getState().toString();
			return state;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return MetricStateEnum.NORMAL.toString();
		}
	}

	private MetricStateData queryMetricStateData(long instanceId,
			String metricId) {
		MetricStateData msd = metricStateService.getMetricState(instanceId,
				metricId);
		return msd;
	}

	/**
	 * 查询性能指标
	 * 
	 * @param metricId
	 * @param instance
	 * @return
	 */
	private String queryPrefMetric(String metricId, ResourceInstance instance) {
		// 判断这个指标是否在监控
		boolean isMonitor = isHaveTimeline(instance.getId(), metricId);
		// try {
		// // ProfileMetric pMetric =
		// profileService.getMetricByInstanceIdAndMetricId(instance.getId(),metricId);
		// ProfileMetric pMetric =
		// profileService.getProfileMetricByInstanceIdAndMetricId(instance.getId(),metricId);
		// if (pMetric != null && pMetric.isMonitor()) {
		// isMonitor = true;
		// }
		// } catch (ProfilelibException e) {
		// logger.error("queryPrefMetric", e);
		// }
		String metricVal = null;
		if (isMonitor) {
			MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
			mrdq.setInstanceID(new long[] { instance.getId() });
			mrdq.setMetricID(new String[] { metricId });
			Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService
					.queryRealTimeMetricDatas(mrdq, 0, 1000);
			metricVal = page != null && page.getDatas().size() > 0 ? page
					.getDatas().get(0).get(metricId) != null ? page.getDatas()
					.get(0).get(metricId).toString() : null : null;
		} else {
			metricVal = "N/A";
		}
		return metricVal;
	}

	private boolean isHaveTimeline(long instanceId, String metricId) {
		boolean flag = false;
		try {
			ProfileMetric pMetric = profileService
					.getMetricByInstanceIdAndMetricId(instanceId, metricId);

			if (null != pMetric
					&& (pMetric.isMonitor() || pMetric.getTimeLineId() > 0)) {
				flag = true;
			}
		} catch (ProfilelibException e) {
			logger.error("isHaveTimeline---", e);
		}
		return flag;
	}

	private Map<String, ?> queryPrefMetricData(String metricId,
			ResourceInstance instance) {
		// 判断这个指标是否在监控,只要有基线策略,就显示出来
		boolean isMonitor = isHaveTimeline(instance.getId(), metricId);

		// 由于有基线业务会影响指标监控状态,故修改接口方法
		// ProfileMetric pMetric =
		// profileService.getProfileMetricByInstanceIdAndMetricId(instance.getId(),
		// metricId);
		// if (pMetric != null && pMetric.isMonitor()) {
		// isMonitor = true;
		// }

		Map<String, ?> metricVal = null;
		if (isMonitor) {
			MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
			mrdq.setInstanceID(new long[] { instance.getId() });
			mrdq.setMetricID(new String[] { metricId });
			Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService
					.queryRealTimeMetricDatas(mrdq, 0, 1000);
			metricVal = page != null && page.getDatas().size() > 0 ? page
					.getDatas().get(0) != null ? page.getDatas().get(0) : null
					: null;
		} else {
			return metricVal;
		}
		return metricVal;
	}

	/**
	 * 查询指标信息
	 * 
	 * @param metricId
	 * @param ri
	 * @return
	 */
	private Map<String, Object> queryMetric(String metricId, ResourceInstance ri) {
		Map<String, Object> metric = new HashMap<String, Object>();
		String metricIdConst = switchMetricId(metricId);
		ResourceMetricDef rmd = capacityService.getResourceMetricDef(
				ri.getResourceId(), metricIdConst);
		if (rmd != null) {
			metric.put(metricId + "Status",
					queryMetricState(ri.getId(), metricIdConst));

			switch (rmd.getMetricType()) {
			case InformationMetric:
				String value = queryInfoMetric(metricIdConst, ri);
				String resultValueInfo = !"".equals(value) ? UnitTransformUtil
						.transform(value, rmd.getUnit()) : "";
				metric.put(metricId, resultValueInfo);
				break;
			case PerformanceMetric:
				try {
					// ProfileMetric pm =
					// profileService.getMetricByInstanceIdAndMetricId(ri.getId(),metricIdConst);
					ProfileMetric pm = profileService
							.getProfileMetricByInstanceIdAndMetricId(
									ri.getId(), metricIdConst);
					if (pm != null) {
						metric.put("isAlarm", pm.isAlarm());
					} else {
						metric.put("isAlarm", false);
					}
				} catch (ProfilelibException exception) {
					metric.put("isAlarm", false);
					logger.error(
							"queryChildMetric getMetricByInstanceIdAndMetricId:",
							exception);
				}
				String resultValuePer = queryPrefMetric(metricIdConst, ri);
				metric.put(metricId, resultValuePer);
				break;

			default:
				break;
			}
		}
		return metric;
	}

	private Map<String, Object> queryMetricForListParse(String metricId,
			ResourceInstance ri) {
		Map<String, Object> metric = new HashMap<String, Object>();
		String metricIdConst = switchMetricId(metricId);
		ResourceMetricDef rmd = capacityService.getResourceMetricDef(
				ri.getResourceId(), metricIdConst);
		if (rmd != null) {
			metric.put("metricName", rmd.getName());
			metric.put("metricStr", metricId);
			metric.put("metricStatus",
					queryMetricState(ri.getId(), metricIdConst));

			switch (rmd.getMetricType()) {
			case InformationMetric:
				String value = queryInfoMetric(metricIdConst, ri);
				String resultValueInfo = !"".equals(value) ? UnitTransformUtil
						.transform(value, rmd.getUnit()) : "";
				metric.put("value", resultValueInfo);
				break;
			case PerformanceMetric:
				try {
					// ProfileMetric pm =
					// profileService.getMetricByInstanceIdAndMetricId(ri.getId(),metricIdConst);
					ProfileMetric pm = profileService
							.getProfileMetricByInstanceIdAndMetricId(
									ri.getId(), metricIdConst);
					if (pm != null) {
						metric.put("isAlarm", pm.isAlarm());
					} else {
						metric.put("isAlarm", false);
					}
				} catch (ProfilelibException exception) {
					metric.put("isAlarm", false);
					logger.error(
							"queryChildMetric getMetricByInstanceIdAndMetricId:",
							exception);
				}
				String resultValuePer = queryPrefMetric(metricIdConst, ri);
				metric.put("value", resultValuePer);
				break;

			default:
				break;
			}
		}
		return metric;
	}

	/**
	 * 查询子资源对应指标
	 * 
	 * @param e
	 * @param instance
	 * @return
	 */
	private List<Map<String, Object>> queryChildMetric(Element e,
			ResourceInstance instance) {
		List<Map<String, Object>> tdList = new ArrayList<Map<String, Object>>();
		String childtype = getElementAttrVal(e, "childtype");
		childtype = switchChildInstanceType(childtype);
		String[] metrics = getElementAttrVal(e, "metrics").split(",");
		List<ResourceInstance> children = instance.getChildren();
		for (int i = 0; children != null && i < children.size(); i++) {
			ResourceInstance child = children.get(i);
			boolean ifNeedChild = child.getChildType().equals(childtype);
			// 只显示已监控
			if (child.getLifeState() != InstanceLifeStateEnum.MONITORED) {
				continue;
			}
			Map<String, Object> metric = new HashMap<String, Object>();

			InstanceStateData isd = instanceStateService
					.getState(child.getId());

			if (null != isd && null != isd.getState()) {
				InstanceStateEnum ise = isd.getState();
				metric.put("instance_state", ise.name());
			} else {
				metric.put("instance_state", InstanceStateEnum.NORMAL.name());
			}
			for (int j = 0; ifNeedChild && j < metrics.length; j++) {
				String metricId = metrics[j];
				// Map<String, Object> map = queryMetric(metricId, child);
				//
				// metric.put(metricId + "Status", map.get(metricId +
				// "Status"));
				// metric.put(metricId, map.get(metricId));
				metric.putAll(queryMetric(metricId, child));
			}

			String nameForShow = child.getShowName();
			if (null == nameForShow || "".equals(nameForShow)) {
				nameForShow = child.getName();
			}
			if (ifNeedChild && "Partition".equals(childtype)) {
				metric.put("fileSysName", nameForShow);
				tdList.add(metric);
			}
			if (ifNeedChild && "Fan".equals(childtype)) {
				metric.put("fanName", nameForShow);
				tdList.add(metric);
			}
			if (ifNeedChild && "Processor".equals(childtype)) {
				metric.put("processorName", nameForShow);
				tdList.add(metric);
			}
			if (ifNeedChild && "Power".equals(childtype)) {
				metric.put("powerName", nameForShow);
				tdList.add(metric);
			}
			if (ifNeedChild && "Camera".equals(childtype)) {
				metric.put("cameraName", nameForShow);
				tdList.add(metric);
			}
			if (ifNeedChild && "Microphone".equals(childtype)) {
				metric.put("microphoneName", nameForShow);
				tdList.add(metric);
			}
			if (ifNeedChild && "Display".equals(childtype)) {
				metric.put("displayName", nameForShow);
				tdList.add(metric);
			}
			if (ifNeedChild && "Node".equals(childtype)) {
				metric.put("nodeName", nameForShow);
				tdList.add(metric);
			}
			if (ifNeedChild && "VCSHost".equals(childtype)) {
				metric.put("fileSysName", nameForShow);
				tdList.add(metric);
			}
		}

		return tdList;
	}

	private List<Map<String, Object>> queryCommonChild(Element e,
			ResourceInstance instance) {
		List<Map<String, Object>> tdList = new ArrayList<Map<String, Object>>();
		String childResourceType = getElementAttrVal(e, "childResourceType");
		String resourceStateUIClass = getElementAttrVal(e,
				"resourceStateUIClass");
		String[] classArr = resourceStateUIClass.split("\\|\\|");
		String green = classArr[0];
		String red = classArr[1];

		List<ResourceInstance> children = instance.getChildren();
		for (int i = 0; children != null && i < children.size(); i++) {
			ResourceInstance child = children.get(i);
			boolean ifNeedChild = child.getChildType()
					.equals(childResourceType);
			if (!ifNeedChild) {
				continue;
			}
			// 只显示已监控
			if (child.getLifeState() != InstanceLifeStateEnum.MONITORED) {
				continue;
			}
			Map<String, Object> metric = new HashMap<String, Object>();

			InstanceStateData isd = instanceStateService
					.getState(child.getId());

			if (null != isd && null != isd.getState()) {
				InstanceStateEnum ise = isd.getState();
				metric.put("instance_state", ise.name());
			} else {
				metric.put("instance_state", InstanceStateEnum.NORMAL.name());
			}

			String nameForShow = child.getShowName();
			if (null == nameForShow || "".equals(nameForShow)) {
				nameForShow = child.getName();
			}
			metric.put("resourceName", nameForShow);
			metric.put("normalClass", green);
			metric.put("criticalClass", red);
			metric.put("width", getElementAttrVal(e, "width"));
			tdList.add(metric);
		}
		return tdList;
	}

	/**
	 * 获取xml文件中的rootElement
	 * 
	 * @return
	 */
	private Element findRootEl() {
		String filePath = System.getProperty(CATALINA_HOME) + File.separator
				+ DOWLOAD_PATH + File.separator + DOWLOAD_FILENAME;
		Element root = XmlUtil.getRootElement(filePath);
		return root;
	}

	/**
	 * 查找xml中相对应的category Element
	 * 
	 * @param root
	 * @param category
	 * @return
	 */
	private Element findDivEl(String category) {
		Element root = findRootEl();
		Element categoryEl = null;
		QName qName = new QName("div");
		Iterator iter = root.elementIterator(qName);
		while (iter.hasNext()) {
			Element el = (Element) iter.next();
			if (category.equals(el.attributeValue("category"))) {
				// if
				// ("ConferenceConductor".equals(el.attributeValue("category")))
				// {
				categoryEl = el;
				break;
			}
		}
		CategoryDef parentCdf = null;
		if (!"default".equals(category))
			parentCdf = capacityService.getCategoryById(category)
					.getParentCategory();
		if (categoryEl == null && parentCdf != null) {
			categoryEl = findDivEl(parentCdf.getId());
		}
		if (categoryEl == null) {
			categoryEl = findDivEl("default");
		}
		return categoryEl;
	}

	private Element findDivElByResource(String resourceId) {
		Element root = findRootEl();
		Element categoryEl = null;
		QName qName = new QName("div");
		Iterator iter = root.elementIterator(qName);
		while (iter.hasNext()) {
			Element el = (Element) iter.next();
			if (resourceId.equals(el.attributeValue("category"))) {
				// if
				// ("ConferenceConductor".equals(el.attributeValue("category")))
				// {
				categoryEl = el;
				break;
			}
		}
		return categoryEl;
	}

	/**
	 * 查询所有的table
	 * 
	 * @param categoryEl
	 * @return
	 */
	private List<Element> findTableEls(Element categoryEl) {
		QName qName = new QName("table");
		return categoryEl.elements(qName);
	}

	/**
	 * 查詢table下所有的tr
	 * 
	 * @param tableEl
	 * @return
	 */
	private List<Element> findTrEls(Element tableEl) {
		QName qName = new QName("tr");
		return tableEl.elements(qName);
	}

	/**
	 * 查詢tr下所有的td
	 * 
	 * @param trEl
	 * @return
	 */
	private List<Element> findTdEls(Element trEl) {
		QName qName = new QName("td");
		return trEl.elements(qName);
	}

	private List<Element> findLabelEls(Element labelEl) {
		QName qName = new QName("label");
		return labelEl.elements(qName);
	}

	/**
	 * 查詢element的屬性值
	 * 
	 * @param e
	 * @param attrName
	 * @return
	 */
	private String getElementAttrVal(Element e, String attrName) {
		Attribute attr = e.attribute(attrName);
		String val = attr == null ? null : attr.getValue();
		return val;
	}

	/**
	 * 创建指标域值字符串
	 * 
	 * @param proThreList
	 * @param unit
	 * @return
	 */
	private String createThresholds(List<ProfileThreshold> proThreList,
			String unit) {
		String minor = "", major = "";
		for (ProfileThreshold thre : proThreList) {
			switch (thre.getPerfMetricStateEnum()) {
			case Minor:
				minor = thre.getThresholdValue();
				break;
			case Major:
				major = thre.getThresholdValue();
				break;
			default:
				break;
			}
		}
		String threVal = "['" + unit + "'," + minor + "," + major + "]";
		return threVal;
	}

	/**
	 * 创建自定义指标域值字符串
	 * 
	 * @param cmts
	 * @param unit
	 * @return
	 */
	private String createCustomThresholds(List<CustomMetricThreshold> cmts,
			String unit) {
		String minor = "", major = "";
		for (int j = 0; j < cmts.size(); j++) {
			CustomMetricThreshold cmt = cmts.get(j);
			switch (cmt.getMetricState()) {
			case WARN:
				minor = cmt.getThresholdValue();
				break;
			case SERIOUS:
				major = cmt.getThresholdValue();
				break;
			default:
				break;
			}
		}
		String threVal = "['" + unit + "'," + minor + "," + major + "]";
		return threVal;
	}

	/**
	 * 封装一个指标的信息
	 * 
	 * @param instance
	 * @param profileMetric
	 * @return isQueryNotMonitor
	 */
	private Map<String, Object> createMetric(ResourceInstance instance,
			ProfileMetric profileMetric,Boolean isQueryNotMonitor) {
		Map<String, Object> metric = new HashMap<String, Object>();
	    String metricId = profileMetric.getMetricId();
	    ResourceMetricDef rmd = this.capacityService.getResourceMetricDef(instance.getResourceId(), metricId);
	    if(isQueryNotMonitor==true){
	    	 if ((rmd != null)  && (rmd.isDisplay()))
	 	    {
	 	      metric.put("id", metricId);
	 	      if(metricId.equals("ip")){
	 	    	  metric.put("text", rmd.getName().toUpperCase());
	 	      }else if(metricId.equals("address")){
	 	    	  metric.put("text", "IP地址");
	 	      }else{
	 	    	  metric.put("text", rmd.getName());
	 	      }
	 	      metric.put("isMonitor", profileMetric.isMonitor());
	 	     metric.put("profileId", profileMetric.getProfileId());
	 	     metric.put("unit", rmd.getUnit());
	 	      metric.put("isTable", Boolean.valueOf(rmd.isTable()));
	 	      metric.put("isAlarm", Boolean.valueOf(profileMetric.isAlarm()));

//	 	      List<ProfileThreshold> proThreList = profileMetric.getMetricThresholds();
//
//	 	      if ((proThreList != null) && (proThreList.size() > 1)) {
//	 	        metric.put("thresholds", createThresholds(proThreList, rmd.getUnit()));
//	 	      }

	 	      switch (rmd.getMetricType()) {
	 	      case PerformanceMetric:
	 	        metric.put("type", MetricTypeEnum.PerformanceMetric);
	 	        break;
	 	      case InformationMetric:
	 	        metric.put("type", MetricTypeEnum.InformationMetric);
	 	        break;
	 	      case AvailabilityMetric:
	 	        metric.put("type", MetricTypeEnum.AvailabilityMetric);
	 	        break;
	 	      default:
	 	        metric.clear();
	 	      }
	 	    }
	    }else{
	    	 if ((rmd != null) && (profileMetric.isMonitor()) && (rmd.isDisplay()))
	 	    {
	 	      metric.put("id", metricId);
	 	      if(metricId.equals("ip")){
	 	    	  metric.put("text", rmd.getName().toUpperCase());
	 	      }else if(metricId.equals("address")){
	 	    	  metric.put("text", "IP地址");
	 	      }else{
	 	    	  metric.put("text", rmd.getName());
	 	      }
	 	      metric.put("unit", rmd.getUnit());
	 	      metric.put("isTable", Boolean.valueOf(rmd.isTable()));
	 	      metric.put("isAlarm", Boolean.valueOf(profileMetric.isAlarm()));
	 	     metric.put("profileId", profileMetric.getProfileId());

//	 	      List<ProfileThreshold> proThreList = profileMetric.getMetricThresholds();
//
//	 	      if ((proThreList != null) && (proThreList.size() > 1)) {
//	 	        metric.put("thresholds", createThresholds(proThreList, rmd.getUnit()));
//	 	      }

	 	      switch (rmd.getMetricType()) {
	 	      case PerformanceMetric:
	 	        metric.put("type", MetricTypeEnum.PerformanceMetric);
	 	        break;
	 	      case InformationMetric:
	 	        metric.put("type", MetricTypeEnum.InformationMetric);
	 	        break;
	 	      case AvailabilityMetric:
	 	        metric.put("type", MetricTypeEnum.AvailabilityMetric);
	 	        break;
	 	      default:
	 	        metric.clear();
	 	      }
	 	    }	
	    }
	   

	    return metric;
	}

	/**
	 * 封装一个指标的信息
	 * 
	 * @param instance
	 * @param customMetric
	 * @return
	 */
	private Map<String, Object> createMetric(ResourceInstance instance,
			CustomMetric customMetric) {
		Map<String, Object> metric = new HashMap<String, Object>();
		CustomMetricInfo cmi = customMetric.getCustomMetricInfo();
		if (cmi.isMonitor()) {
			// id,名称
			metric.put("id", cmi.getId());
			metric.put("text", cmi.getName());
			metric.put("unit", cmi.getUnit());
			metric.put("isTable", false);
			metric.put("isAlarm", cmi.isAlert());
			// 阈值
//			List<CustomMetricThreshold> cmts = customMetric
//					.getCustomMetricThresholds();
//			if (cmts != null && cmts.size() > 1) {
//				metric.put("thresholds",
//						createCustomThresholds(cmts, cmi.getUnit()));
//			}
			// 指标状态
			switch (cmi.getStyle()) {
			case PerformanceMetric:
				metric.put("type", MetricTypeEnum.PerformanceMetric);
				break;
			case InformationMetric:
				metric.put("type", MetricTypeEnum.InformationMetric);
				break;
			case AvailabilityMetric:
				metric.put("type", MetricTypeEnum.AvailabilityMetric);
				break;
			default:
				metric.clear();
				break;
			}
		}
		return metric;
	}

	@Override
	public List<Map<String, Object>> getMetricByType(Long instanceId,
			String metricType ,Boolean isQueryNotMonitor) {
		List<Map<String, Object>> metricList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> metricMaps = new HashMap<String, Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 指标状态
		Map<String, MetricStateEnum> msdMaps = new HashMap<String, MetricStateEnum>();
		try {
			ResourceInstance instance = resourceInstanceService
					.getResourceInstance(instanceId.longValue());
			// 可用性指标List
			List<String> availabilityList = new ArrayList<String>();
			// 信息指标List
			List<String> InfoMetricList = new ArrayList<String>();
			List<String> infoMetricStateList = new ArrayList<String>();
			// 性能指标List
			List<String> perfMetricList = new ArrayList<String>();
			List<String> perfMetricStateList = new ArrayList<String>();
			// 指标基本信息
			// List<ProfileMetric> profileMetricList =
			// profileService.getMetricByInstanceId(instanceId);
			List<ProfileMetric> profileMetricList = profileService
					.getProfileMetricsByResourceInstanceId(instanceId.longValue());
			for (int i = 0; profileMetricList != null
					&& i < profileMetricList.size(); i++) {
				ProfileMetric profileMetric = profileMetricList.get(i);
				
				Map<String, Object> metric = createMetric(instance,
						profileMetric,isQueryNotMonitor);
				if (!metric.isEmpty()) {
					metric.put("isCustomMetric", false);
					if (MetricTypeEnum.PerformanceMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.PerformanceMetric.toString()
									.equals(metricType)) {
						metricMaps.put(profileMetric.getMetricId(), metric);
						perfMetricList.add(profileMetric.getMetricId());
						perfMetricStateList.add(profileMetric.getMetricId());
					} else if (MetricTypeEnum.InformationMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.InformationMetric.toString()
									.equals(metricType)) {
						metricMaps.put(profileMetric.getMetricId(), metric);
						InfoMetricList.add(profileMetric.getMetricId());
						// 初始化指标状态值
						infoMetricStateList.add(profileMetric.getMetricId());
					} else if (MetricTypeEnum.AvailabilityMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.AvailabilityMetric.toString()
									.equals(metricType)) {
						metricMaps.put(profileMetric.getMetricId(), metric);
						availabilityList.add(profileMetric.getMetricId());
					}
				}
			}
			// 自定义指标基本信息
			List<CustomMetric> customMetrics = customMetricService
					.getCustomMetricsByInstanceId(instanceId);
			for (int i = 0; customMetrics != null && i < customMetrics.size(); i++) {
				CustomMetric customMetric = customMetrics.get(i);
				Map<String, Object> metric = createMetric(instance,
						customMetric);
				if (!metric.isEmpty()) {
					metric.put("isCustomMetric", true);
					if (MetricTypeEnum.PerformanceMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.PerformanceMetric.toString()
									.equals(metricType)) {
						metricMaps.put(customMetric.getCustomMetricInfo()
								.getId(), metric);
						perfMetricStateList.add(customMetric
								.getCustomMetricInfo().getId());
					} else if (MetricTypeEnum.InformationMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.InformationMetric.toString()
									.equals(metricType)) {
						metricMaps.put(customMetric.getCustomMetricInfo()
								.getId(), metric);
						// 初始化指标状态值
						infoMetricStateList.add(customMetric.getCustomMetricInfo().getId());
					} else if (MetricTypeEnum.AvailabilityMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.AvailabilityMetric.toString()
									.equals(metricType)) {
						metricMaps.put(customMetric.getCustomMetricInfo()
								.getId(), metric);
					}
				}
			}
			// 性能指标 状态查询
			if (!perfMetricStateList.isEmpty()) {
				List<Long> instanceIdList = new ArrayList<Long>();
				instanceIdList.add(instanceId);
				List<MetricStateData> msdList = metricStateService
						.findMetricState(instanceIdList, perfMetricStateList);
				for (int i = 0; msdList != null && i < msdList.size(); i++) {
					MetricStateData msd = msdList.get(i);
					msdMaps.put(msd.getMetricID(), msd.getState());
				}
			}
			// 信息指标 状态查询
			if (!infoMetricStateList.isEmpty()) {
				List<Long> instanceIdList = new ArrayList<Long>();
				instanceIdList.add(instanceId);
				List<MetricStateData> msdList = metricStateService
						.findMetricState(instanceIdList, infoMetricStateList);
				for (int i = 0; msdList != null && i < msdList.size(); i++) {
					MetricStateData msd = msdList.get(i);
					msdMaps.put(msd.getMetricID(), msd.getState());
				}
			}
			// 查询指标值
			if (!InfoMetricList.isEmpty()) {
				// List<MetricData> infoMetrics = metricDataService
				// .getMetricInfoDatas(instanceId, InfoMetricList
				// .toArray(new String[InfoMetricList.size()]));
				// 查询信息指标需要过滤
				List<MetricData> infoMetrics = infoMetricQueryAdaptService
						.getMetricInfoDatas(instanceId, InfoMetricList
								.toArray(new String[InfoMetricList.size()]));
				for (int i = 0; i < infoMetrics.size(); i++) {
					MetricData infoMetric = infoMetrics.get(i);
					if (metricMaps.containsKey(infoMetric.getMetricId())) {
						Map<String, Object> allMetric = metricMaps
								.get(infoMetric.getMetricId());
						// 当前值
						String val = emptyFirstLastChar(infoMetric.getData());
						val = "".equals(val) ? val : UnitTransformUtil
								.transform(val, (String) allMetric.get("unit"));
						allMetric.put("currentVal", val);
						// 采集时间
						allMetric.put("lastCollTime",
								sdf.format(infoMetric.getCollectTime()));
						// 指标状态
						if (msdMaps.containsKey(infoMetric.getMetricId())) {
							allMetric.put("status",
									msdMaps.get(infoMetric.getMetricId())
											.toString());
						} else {
							allMetric.put("status",
									MetricStateEnum.NORMAL.toString());
						}
						//基线采集
						if(infoMetric.getTimelineId() != null && infoMetric.getTimelineId() > 0){
							Timeline timelineObject = timelineService.getTimelinesById(infoMetric.getTimelineId());
							// 基线有可能已被删除
							if (timelineObject != null) {
								allMetric.put("timelineid", infoMetric.getTimelineId());
							}
						}
					}
				}
			} else if (!perfMetricList.isEmpty()) {
				// 查询性能指标采集数据
				MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
				mrdq.setInstanceID(new long[] { instanceId });
				mrdq.setMetricID(perfMetricList
						.toArray(new String[perfMetricList.size()]));
				Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService
						.queryRealTimeMetricDatas(mrdq, 0, 1000);
				for (int i = 0; i < page.getDatas().size(); i++) {
					Map<String, ?> perfMetricMap = page.getDatas().get(i);
					perfMetricMap.remove("instanceid");
					if (!perfMetricMap.isEmpty()) {
						Iterator<String> iter = perfMetricMap.keySet()
								.iterator();
						while (iter.hasNext()) {
							String metricId = iter.next();
							// 采集时间、策略ID、基线ID
							if (metricId.toLowerCase().endsWith(
									"CollectTime".toLowerCase())
									|| metricId.toLowerCase().endsWith(
											"ProfileId".toLowerCase())
									|| metricId.toLowerCase().endsWith(
											"TimelineId".toLowerCase())) {
								continue;
							}
							if (metricMaps.containsKey(metricId)) {
								Map<String, Object> allMetric = metricMaps
										.get(metricId);
								// 当前值
								String currentVal = perfMetricMap.get(metricId) == null ? ""
										: perfMetricMap.get(metricId)
												.toString();
								// 自定义指标不带单位
								currentVal = UnitTransformUtil.transform(
										currentVal,
										(String) allMetric.get("unit"));
								allMetric.put("currentVal", currentVal);
								// 采集时间
								String time = perfMetricMap.get(metricId
										+ "CollectTime") == null ? "" : sdf
										.format(perfMetricMap.get(metricId
												+ "CollectTime"));
								allMetric.put("lastCollTime", time);
								// 指标状态
								if (msdMaps.containsKey(metricId)) {
									allMetric.put("status",
											msdMaps.get(metricId).toString());
								} else {
									// allMetric.put("status",MetricStateEnum.UNKOWN.toString());
									allMetric.put("status",
											MetricStateEnum.NORMAL.toString());
								}
								// 是否为基线采集
								if (perfMetricMap.containsKey(metricId
										+ "TimelineId")
										&& perfMetricMap.get(metricId
												+ "TimelineId") != null) {
									Long timelineId = null;
									if (perfMetricMap.get(metricId
											+ "TimelineId") instanceof BigDecimal) {
										logger.error("Object type is BigDecimal : "
												+ metricId + "TimelineId");
										timelineId = ((BigDecimal) perfMetricMap
												.get(metricId + "TimelineId"))
												.longValue();
									} else {
										timelineId = (Long) perfMetricMap
												.get(metricId + "TimelineId");
									}
									Timeline timelineObject = timelineService.getTimelinesById(timelineId);
									// 基线有可能已被删除
									if (timelineObject != null) {
										allMetric.put("timelineid", timelineId);
									}
								}
							}
						}
					}
				}
			} else if (!availabilityList.isEmpty()) {
				// 可用性指标
				for (int i = 0; i < availabilityList.size(); i++) {
					String metricId = availabilityList.get(i);
					if (metricMaps.containsKey(metricId)) {
						Map<String, Object> allMetric = metricMaps
								.get(metricId);
						allMetric.put("status",
								queryMetricState(instanceId, metricId));
						MetricData md = metricDataService
								.getMetricAvailableData(instanceId, metricId);
						String currentVal = md == null || md.getData() == null ? ""
								: md.getData()[0];
						allMetric.put("currentVal", currentVal);
						String time = md == null || md.getCollectTime() == null ? ""
								: sdf.format(md.getCollectTime());
						allMetric.put("lastCollTime", time);
					}
				}
			}
			// 自定义指标、并组装返回数据
			Iterator<String> iter = metricMaps.keySet().iterator();
			while (iter.hasNext()) {
				String metricId = iter.next();
				Map<String, Object> allMetric = metricMaps.get(metricId);
				if (allMetric.containsKey("isCustomMetric")
						&& (boolean) allMetric.get("isCustomMetric")) {
					MetricData customMetricData = metricDataService
							.getCustomerMetricData(instanceId, metricId);
					// 当前值
					String customCurrentVal = customMetricData == null
							|| customMetricData.getData() == null ? ""
							: customMetricData.getData()[0];
					customCurrentVal = MetricTypeEnum.AvailabilityMetric
							.toString().equals(metricType) ? customCurrentVal
							: UnitTransformUtil.transform(customCurrentVal,
									(String) allMetric.get("unit"));
					allMetric.put("currentVal", customCurrentVal);
					// 采集时间
					String customTime = customMetricData == null
							|| customMetricData.getCollectTime() == null ? ""
							: sdf.format(customMetricData.getCollectTime());
					allMetric.put("lastCollTime", customTime);
					// 指标状态
					if (msdMaps.containsKey(metricId)) {
						allMetric.put("status", msdMaps.get(metricId)
								.toString());
					} else {
						// allMetric.put("status",MetricStateEnum.UNKOWN.toString());
						allMetric.put("status",
								MetricStateEnum.NORMAL.toString());
					}
					// 自定义可用性指标状态
					if (MetricTypeEnum.AvailabilityMetric.toString().equals(
							metricType)) {
						allMetric.put("status",
								queryMetricState(instanceId, metricId));
					}
				}
				metricList.add(allMetric);
			}
		} catch (Exception e) {
			logger.error("getMetricByType", e);
		}
		return metricList;
	}

	@Override
	public Map<Long, Object> getResourceConnectivity(String[] ids,
			String metricType) {
		Map<Long, List<Map<String, Object>>> resultMap = new HashMap<Long, List<Map<String, Object>>>();
		Map<Long, Object> outMap = new HashMap<Long, Object>();
		List<String> idList = Arrays.asList(ids);
		List<InstanceStateData> isdList = null;
		List<Long> instanceIdList = null;
		try {
			instanceIdList = new ArrayList<Long>(idList.size());
			for (String id : idList) {
				instanceIdList.add(Long.parseLong(id));
			}
			isdList = instanceStateService.findStates(instanceIdList);
			for (Long id : instanceIdList) {
				List<Map<String, Object>> metricData = new ArrayList<>();
				metricData.addAll(this.getMetricByType(id, metricType,false));
				resultMap.put(id, metricData);
			}
		} catch (Exception e) {
			logger.error("getResourceConnectivity", e);
		}

		for (InstanceStateData isData : isdList) {
			Long id = isData.getInstanceID();
			List<Map<String, Object>> md = resultMap.get(id);
			if (md.size() > 0) {
				for (int i = 0; i < md.size(); ++i) {
					String status = md.get(i).get("status").toString();
					if (CollectStateEnum.COLLECTIBLE.equals(isData
							.getCollectStateEnum())
							&& !MetricStateEnum.CRITICAL.equals(status)) {
						outMap.put(id, true);
					} else {
						outMap.put(id, false);
					}
				}
			} else {
				outMap.put(id, false);
			}
		}

		return outMap;
	}

	/**
	 * 查询主资源、子资源类型
	 */
	@Override
	public Map<String, Object> getResourceDetailInfo(Long instanceId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> parent = new HashMap<String, Object>();
		List<Map<String, String>> childrenType = new ArrayList<Map<String, String>>();
		Set<String> childTypeId = new HashSet<String>();
		try {
			// 主资源
			ResourceInstance instance = resourceInstanceService
					.getResourceInstance(instanceId);
			// 首页如果资源已被删除 则返回一个空的hashMap
			if (instance == null) {
				return result;
			}
			parent.putAll(createInstance(instance));
			result.put("parent", parent);
			// 子资源类型
			List<ResourceInstance> childInstances = instance.getChildren();
			for (int i = 0; childInstances != null && i < childInstances.size(); i++) {
				ResourceInstance childInstance = childInstances.get(i);
				childTypeId.add(childInstance.getResourceId());
			}
			for (String typeId : childTypeId) {
				if (typeId == null) {
					continue;
				}
				Map<String, String> childType = new HashMap<String, String>();
				childType.put("id", typeId);
				ResourceDef resourceDef = capacityService
						.getResourceDefById(typeId);
				if (resourceDef != null) {
					childType.put("name", resourceDef.getName());
					childType.put("type", resourceDef.getType());
					childrenType.add(childType);
				}

			}
			result.put("childrenType", childrenType);
		} catch (InstancelibException e) {
			logger.error("getResourceDetailInfo", e);
		}
		return result;
	}

	/**
	 * 读取xml文件
	 */
	public List<List<List<Object>>> getMetricFromXML(Long instanceId,
			ILoginUser user) {
		List<List<List<Object>>> container = null;
		try {
			ResourceInstance instance = resourceInstanceService
					.getResourceInstance(instanceId);
			container = new ArrayList<List<List<Object>>>();
			queryInstanceMetrics(findRootEl(), instance, container, user);
		} catch (Exception e) {
			logger.error("getMetricFromXML", e);
		}
		return container;
	}

	@Override
	public Map<String, Object> getMetricInfo(Long instanceId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ResourceInstance instance = resourceInstanceService
					.getResourceInstance(instanceId);
			map = findMetricInfo(instance);
		} catch (Exception e) {
			logger.error("getMetricInfo", e);
		}
		return map;
	}

	public Map<String, Object> findMetricInfo(ResourceInstance instance) {
		Map<String, Object> tdMap = new HashMap<String, Object>();
		List<Map<String, Object>> val = new ArrayList<Map<String, Object>>();
		Map<String, Object> datastoreMap = getRelationDatastore(instance, false);
		List<Long> datastoreIdList = !datastoreMap.isEmpty() ? (List<Long>) datastoreMap
				.get("idList") : new ArrayList<Long>();
		try {
			for (Long instanceId : datastoreIdList) {
				ResourceInstance vmRi = resourceInstanceService
						.getResourceInstance(instanceId);
				Map<String, Object> metric = new HashMap<String, Object>();
				String[] metrics = { "DataStorageVolume",
						"DataStorageFreeSpace" };
				for (int i = 0; metrics != null && i < metrics.length; i++) {
					metric.putAll(queryMetric(metrics[i], vmRi));
				}
				if (!metric.isEmpty()) {
					val.add(metric);
				}
			}
		} catch (InstancelibException e1) {
			logger.error("findMetricInfo", e1);
		}

		tdMap.put("value", val);
		return tdMap;

	}

	@Override
	public List<Map<String, Object>> getChildInstance(Long instanceId,
			String childType) {
		childType = switchChildInstanceType(childType);
		List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
		Map<Long, Map<String, Object>> childrenMap = new HashMap<Long, Map<String, Object>>();
		try {
			ResourceInstance instance = resourceInstanceService
					.getResourceInstance(instanceId);
			List<ResourceInstance> childrenIns = instance.getChildren();
			Pattern pattern = Pattern.compile("[0-9]*");
			List<Long> childInstIdList = new ArrayList<Long>();
			for (int i = 0; childrenIns != null && i < childrenIns.size(); i++) {
				ResourceInstance childIns = childrenIns.get(i);
				if (childType.equals(childIns.getChildType())) {
					Map<String, Object> netInterface = new HashMap<String, Object>();
					netInterface.put("id", childIns.getId());
					netInterface.put("name", childIns.getName());
					netInterface.put("lifeState", childIns.getLifeState()
							.toString());
					netInterface.put("state", queryInstanceState(childIns));
					children.add(netInterface);
					childrenMap.put(childIns.getId(), netInterface);
					if ("NetInterface".equals(childType)) {
						// ifIndex
						String[] ifIndex = childIns
								.getModulePropBykey("ifIndex");
						int index = ifIndex != null && !"".equals(ifIndex[0])
								&& pattern.matcher(ifIndex[0]).matches() ? Integer
								.valueOf(ifIndex[0]) : Integer.MAX_VALUE;
						netInterface.put("ifIndex", index);
						// ifType
						String[] ifType = childIns.getModulePropBykey("ifType");
						if (ifType != null && null != ifType[0]
								&& !"".equals(ifType[0])) {
							netInterface.put("ifType", ifType[0]);
						}
					}
					childInstIdList.add(childIns.getId());
				}
			}

			if ("NetInterface".equals(childType) && children.size() > 0) {
				// 查询管理状态和操作状态
				long[] instIdArr = new long[childInstIdList.size()];
				for (int i = 0; i < childInstIdList.size(); i++) {
					instIdArr[i] = childInstIdList.get(i);
				}
				String[] infoMetrics = { "ifAdminStatus", "ifOperStatus",
						"ifType" };
				// List<MetricData> metricDataList = metricDataService
				// .getMetricInfoDatas(instIdArr, infoMetrics);
				// 查询信息指标需要过滤
				List<MetricData> metricDataList = infoMetricQueryAdaptService
						.getMetricInfoDatas(instIdArr, infoMetrics);
				for (int i = 0; i < metricDataList.size(); i++) {
					MetricData metricData = metricDataList.get(i);
					if (childrenMap.containsKey(metricData
							.getResourceInstanceId())) {
						Map<String, Object> netInterface = childrenMap
								.get(metricData.getResourceInstanceId());
						String data = metricData.getData() != null
								&& metricData.getData().length > 0 ? metricData
								.getData()[0] : "";
						if (!"ifType".equals(metricData.getMetricId())
								|| ("ifType".equals(metricData.getMetricId()) && !netInterface
										.containsKey("ifType"))) {
							netInterface.put(metricData.getMetricId(), data);
						}
					}
				}
				for (int i = 0; i < children.size(); i++) {
					Map<String, Object> child = children.get(i);
					Long id = Long.valueOf(child.get("id").toString());
					String state = queryInterfaceState(id,
							(String) child.get("lifeState"),
							(String) child.get("ifAdminStatus"),
							(String) child.get("ifOperStatus"));
					child.put("state", state);
				}
				Collections.sort(children,
						new Comparator<Map<String, Object>>() {
							public int compare(Map<String, Object> o1,
									Map<String, Object> o2) {
								if (!o1.containsKey("ifIndex")) {
									o1.put("ifIndex", Integer.MAX_VALUE);
								}
								if (!o2.containsKey("ifIndex")) {
									o2.put("ifIndex", Integer.MAX_VALUE);
								}
								return (Integer) o1.get("ifIndex")
										- (Integer) o2.get("ifIndex");
							}
						});
			}
		} catch (InstancelibException e) {
			logger.error("getChildInstance", e);
		}
		return children;
	}

	@Override
	public Map<String, Object> getResourceInfo(Long instanceId) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isExist", true);
		try {
			ResourceInstance ri = resourceInstanceService
					.getResourceInstance(instanceId);
			if(ri == null){
				result.put("isExist", false);
				return result;
			}
			//查询连通性状态
			InstanceStateData state = instanceStateService.getState(instanceId);
			result.put("state", state);
			// 判断是子资源还是主资源
			if (ri.getParentId() != 0) {
				instanceId = ri.getParentId();
				ri = resourceInstanceService.getResourceInstance(instanceId);
			}
			result.put("name", ri.getShowName() == null ? "" : ri.getShowName());
			result.put("instanceId", instanceId);
			result.put("ip", ri.getShowIP());
			result.put("resourceId", ri.getResourceId());
			String resourceType = null;
			CategoryDef categoryDef = capacityService.getCategoryById(ri
					.getCategoryId());
			if (categoryDef != null && categoryDef.getParentCategory() != null) {
				resourceType = categoryDef.getParentCategory().getId();
			}

			result.put("resourceType", resourceType);
			result.put("categoryId", ri.getCategoryId());
			result.put("discoverNode", ri.getDiscoverNode());
			result.put("discoverWay", ri.getDiscoverWay());
			result.put("lifeState", ri.getLifeState());

			Map<String, String> stateMap = queryInstanceStateAndCollectState(ri);
			result.put("availability", stateMap.get("availability"));
			result.put("CollectState", stateMap.get("CollectState"));
			
			// 子资源类型
			Set<Map<String, String>> childrenType = new HashSet<Map<String, String>>();
			// 模型中是否有进程、文件
			ResourceDef[] resourceDefs = capacityService.getResourceDefById(
					ri.getResourceId()).getChildResourceDefs();

			for (int i = 0; resourceDefs != null && i < resourceDefs.length; i++) {
				ResourceDef resourceDef = resourceDefs[i];
				if (ResourceTypeConsts.TYPE_PROCESS.equals(resourceDef
						.getType())) {
					Map<String, String> childType = new HashMap<String, String>();
					childType.put("id", resourceDef.getId());
					childType.put("name", resourceDef.getName());
					childType.put("type", resourceDef.getType());
					childrenType.add(childType);
				}
				if ("File".equals(resourceDef.getType())) {
					Map<String, String> childType = new HashMap<String, String>();
					childType.put("id", resourceDef.getId());
					childType.put("name", resourceDef.getName());
					childType.put("type", resourceDef.getType());
					childrenType.add(childType);
				}
				if (ResourceTypeConsts.VOLUME_GROUPS.equals(resourceDef
						.getType())) {
					Map<String, String> childType = new HashMap<String, String>();
					childType.put("id", resourceDef.getId());
					childType.put("name", resourceDef.getName());
					childType.put("type", resourceDef.getType());
					childrenType.add(childType);
				}
				if (ResourceTypeConsts.LOGICAL_VOLUMES.equals(resourceDef
						.getType())) {
					Map<String, String> childType = new HashMap<String, String>();
					childType.put("id", resourceDef.getId());
					childType.put("name", resourceDef.getName());
					childType.put("type", resourceDef.getType());
					childrenType.add(childType);
				}
			}
			// 子资源类型
			List<ResourceInstance> childInstances = ri.getChildren();
			for (int i = 0; childInstances != null && i < childInstances.size(); i++) {
				ResourceInstance childInstance = childInstances.get(i);
				if (InstanceLifeStateEnum.MONITORED == childInstance
						.getLifeState()) {
					Map<String, String> childType = new HashMap<String, String>();
					if (childInstance.getChildType() != null) {
						String resourceId = childInstance.getResourceId();
						String typeId = childInstance.getChildType();
						childType.put("id", resourceId);
						childType.put("name", capacityService
								.getResourceDefById(resourceId).getName());
						childType.put("type", typeId);
						childrenType.add(childType);
					}
				}
			}
			result.put("childrenType", childrenType);
			// 是否有自定义指标
			List<CustomMetric> customMetrics = customMetricService
					.getCustomMetricsByInstanceId(instanceId);
			result.put("hasCustomMetric", customMetrics != null
					&& !customMetrics.isEmpty() ? true : false);
			// 如果是虚拟化
			if ("VM".equals(resourceType)) {
				List<Map<String, Object>> relationResList = new ArrayList<Map<String, Object>>();
				if ("vmESXi".equals(ri.getResourceId())
						|| "VMWareCluster".equals(ri.getResourceId())
						|| "VMWareDatastore".equals(ri.getResourceId())) {
					Map<String, Object> vmMap = getRelationVM(ri, true);
					if (!vmMap.isEmpty()) {
						relationResList.add(vmMap);
					}
				}
				if ("VMWareDatastore".equals(ri.getResourceId())
						|| "VMWareCluster".equals(ri.getResourceId())) {
					Map<String, Object> vmHostMap = getRelationVmHost(ri, true);
					if (!vmHostMap.isEmpty()) {
						relationResList.add(vmHostMap);
					}
				}
				result.put("relationRes", relationResList);
			}
		} catch (Exception e) {
			logger.error("getResourceInfo", e);
		}
		return result;
	}

	/**
	 * 查询虚拟机列表
	 * 
	 * @param ri
	 * @param isMonitored
	 * @return
	 */
	private Map<String, Object> getRelationVM(ResourceInstance ri,
			boolean isMonitored) {
		Map<String, Object> relationResMap = new HashMap<String, Object>();
		try {
			List<Long> instanceIds = new ArrayList<Long>();
			List<Long> vmIdList = new ArrayList<Long>();
			String[] vmUuidList = queryInfoMetric("VMList", ri).split(",");
			List<ResVmResourceTreePo> vmTreePoList = resVmResourceTreeDao
					.selectListByUuids(Arrays.asList(vmUuidList));
			for (int i = 0; vmTreePoList != null && i < vmTreePoList.size(); i++) {
				ResVmResourceTreePo resVmTreePo = vmTreePoList.get(i);
				if (resVmTreePo.getInstanceid() != null) {
					instanceIds.add(resVmTreePo.getInstanceid());
				}
			}
			if (!instanceIds.isEmpty()) {
				List<ResourceInstance> riList = resourceInstanceService
						.getResourceInstances(instanceIds);
				for (int i = 0; riList != null && i < riList.size(); i++) {
					ResourceInstance relationRi = riList.get(i);
					if (isMonitored) {
						if (relationRi.getLifeState() == InstanceLifeStateEnum.MONITORED) {
							vmIdList.add(relationRi.getId());
						}
					} else {
						if (relationRi.getLifeState() == InstanceLifeStateEnum.MONITORED
								|| relationRi.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
							vmIdList.add(relationRi.getId());
						}
					}
				}
			}
			if (!vmIdList.isEmpty()) {
				relationResMap.put("name",
						capacityService.getResourceDefById("VMWareVM")
								.getName());
				relationResMap.put("idList", vmIdList);
				relationResMap.put("resourceId", "VMWareVM");
			}
		} catch (InstancelibException e) {
			logger.error("getRelationVM", e);
		}
		return relationResMap;
	}

	/**
	 * 查询主机列表
	 * 
	 * @param ri
	 * @param isMonitored
	 * @return
	 */
	private Map<String, Object> getRelationVmHost(ResourceInstance ri,
			boolean isMonitored) {
		Map<String, Object> relationResMap = new HashMap<String, Object>();
		try {
			List<Long> instanceIds = new ArrayList<Long>();
			List<Long> hostIdList = new ArrayList<Long>();
			String[] hostUuidList = queryInfoMetric("HostList", ri).split(",");
			List<ResVmResourceTreePo> vmTreePoList = resVmResourceTreeDao
					.selectListByUuids(Arrays.asList(hostUuidList));
			for (int i = 0; vmTreePoList != null && i < vmTreePoList.size(); i++) {
				ResVmResourceTreePo resVmTreePo = vmTreePoList.get(i);
				if (resVmTreePo.getInstanceid() != null) {
					instanceIds.add(resVmTreePo.getInstanceid());
				}
			}
			if (!instanceIds.isEmpty()) {
				List<ResourceInstance> riList = resourceInstanceService
						.getResourceInstances(instanceIds);
				for (int i = 0; riList != null && i < riList.size(); i++) {
					ResourceInstance relationRi = riList.get(i);
					if (isMonitored) {
						if (relationRi.getLifeState() == InstanceLifeStateEnum.MONITORED) {
							hostIdList.add(relationRi.getId());
						}
					} else {
						if (relationRi.getLifeState() == InstanceLifeStateEnum.MONITORED
								|| relationRi.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
							hostIdList.add(relationRi.getId());
						}
					}
				}
			}
			if (!hostIdList.isEmpty()) {
				relationResMap.put("name",
						capacityService.getResourceDefById("vmESXi").getName());
				relationResMap.put("idList", hostIdList);
				relationResMap.put("resourceId", "vmESXi");
			}
		} catch (InstancelibException e) {
			logger.error("getRelationVM", e);
		}
		return relationResMap;
	}

	/**
	 * 存储列表
	 * 
	 * @param ri
	 * @param isMonitored
	 * @return
	 */
	private Map<String, Object> getRelationDatastore(ResourceInstance ri,
			boolean isMonitored) {
		Map<String, Object> relationResMap = new HashMap<String, Object>();
		try {
			List<Long> instanceIds = new ArrayList<Long>();
			List<Long> datastoreIdList = new ArrayList<Long>();
			String[] datastoreUuidList = queryInfoMetric("DatastoreList", ri)
					.split(",");
			List<ResVmResourceTreePo> vmTreePoList = resVmResourceTreeDao
					.selectListByUuids(Arrays.asList(datastoreUuidList));
			for (int i = 0; vmTreePoList != null && i < vmTreePoList.size(); i++) {
				ResVmResourceTreePo resVmTreePo = vmTreePoList.get(i);
				if (resVmTreePo.getInstanceid() != null) {
					instanceIds.add(resVmTreePo.getInstanceid());
				}
			}
			if (!instanceIds.isEmpty()) {
				List<ResourceInstance> riList = resourceInstanceService
						.getResourceInstances(instanceIds);
				for (int i = 0; riList != null && i < riList.size(); i++) {
					ResourceInstance relationRi = riList.get(i);
					if (isMonitored) {
						if (relationRi.getLifeState() == InstanceLifeStateEnum.MONITORED) {
							datastoreIdList.add(relationRi.getId());
						}
					} else {
						if (relationRi.getLifeState() == InstanceLifeStateEnum.MONITORED
								|| relationRi.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
							datastoreIdList.add(relationRi.getId());
						}
					}
				}
			}
			if (!datastoreIdList.isEmpty()) {
				relationResMap.put("name",
						capacityService.getResourceDefById("vmESXi").getName());
				relationResMap.put("idList", datastoreIdList);
				relationResMap.put("resourceId", "vmESXi");
			}
		} catch (InstancelibException e) {
			logger.error("getRelationVM", e);
		}
		return relationResMap;
	}

	@Override
	public List<Map<String, Object>> getAllMetric(Long instanceId) {
		List<Map<String, Object>> metricData = getMetricByType(instanceId,
				"InformationMetric",false);
		metricData.addAll(getMetricByType(instanceId, "PerformanceMetric",false));
		return metricData;
	}

	/**
	 * 手动刷新信息指标
	 * 
	 * @param instanceId
	 * @return
	 */
	@Override
	public String getMetricHand(Long instanceId) {
		try {
			metricDataService.triggerMetricGather(instanceId, true);
		} catch (MetricExecutorException e) {
			e.printStackTrace();
			logger.error("getMetricHand", e);
			return "FALSE";
		}
		return "TRUE";
	}

	@Override
	public String isMetricHand(Long instanceId) {
		String result = "FALSE";
		try {
			Boolean bl = metricDataService.isMetricGather(instanceId);
			if (bl == true) {
				result = "TRUE";// 在采集
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("getMetricHand", e);
			return "FALSE";
		}
		return result;
	}

	/**
	 * 更改端口管理状态
	 * 
	 * @param instanceId
	 * @return
	 */
	@Override
	public String editPortStatus(long instanceId, String condition) {
		// TODO Auto-generated method stub
		MetricData metricData = null;
		if (condition != null) {
			try {
				metricData = metricDataService.catchRealtimeMetricData(
						instanceId, condition);
			} catch (MetricExecutorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			if (metricData != null && metricData.getData().length > 0) {
				dataStrings = metricData.getData();
				return dataStrings[0];
			}
		}
		return null;
	}

	public List<AlarmEvent> getOneAlarmInfo(Long resourceIds) {
		// TODO Auto-generated method stub
		List<String> ids = new ArrayList<String>();
		try {
			ResourceInstance source = resourceInstanceService
					.getResourceInstance(resourceIds);
			if (source != null) {
				if (source.getParentId() != 0) {
					ids.add(String.valueOf(source.getParentId()));
				} else {
					ids.add(String.valueOf(source.getId()));
					/*
					List<ResourceInstance> instances = source.getChildren();
					if (instances == null) {
						ids.add(String.valueOf(resourceIds));
					} else {
						if (instances.size() != 0) {
							ids.add(String.valueOf(resourceIds));
							for (int i = 0; i < instances.size(); i++) {
								ids.add(String
										.valueOf(instances.get(i).getId()));
							}
						}
					}

				*/}
			}

		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlarmEventQuery2 query = new AlarmEventQuery2();

		List<AlarmEventQueryDetail> details = new ArrayList<AlarmEventQueryDetail>();
		SysModuleEnum[] monitorList = { SysModuleEnum.MONITOR,SysModuleEnum.OTHER,SysModuleEnum.BUSSINESS  };
		for (int i = 0; i < monitorList.length; i++) {

			AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
			detail.setSourceIDes(ids);
			detail.setRecovered(false);

			// detail.set
			detail.setSysID(monitorList[i]);
			details.add(detail);
		}

		query.setFilters(details);
	//	List<AlarmEvent> events = resourceEventService.findAlarmEvent(query);
		Page<AlarmEvent, AlarmEventQuery2> page=	resourceEventService.queryAlarmEvent(query, 0, Integer.MAX_VALUE);
				
				if(null != page.getRows() && page.getRows().size() > 0){
					return page.getRows();
				}else{
					return null;
				}
		
	}

	public Map<String, Integer> getHomeDataByID(List<Long> resourceIds) {

		Map<String, Integer> result = new HashMap<String, Integer>();
		int critical = 0;
		int serious = 0;
		int warn = 0;
		// int unkown = 0;
		// int normal = 0;
		// 通过resource实例ID获取topn
		if (resourceIds != null && resourceIds.size() > 0) {
			List<String> ids = new ArrayList<>();
			for (Long id : resourceIds) {
				ids.add(String.valueOf(id));
			}

			critical = resourceEventService.countAlarmEvent(ids,
					SysModuleEnum.MONITOR,
					new InstanceStateEnum[] { InstanceStateEnum.CRITICAL },
					null, null, false);// 致命
			serious = resourceEventService.countAlarmEvent(ids,
					SysModuleEnum.MONITOR,
					new InstanceStateEnum[] { InstanceStateEnum.SERIOUS },
					null, null, false);// 严重
			warn = resourceEventService.countAlarmEvent(ids,
					SysModuleEnum.MONITOR,
					new InstanceStateEnum[] { InstanceStateEnum.WARN }, null,
					null, false);// 警告
			// unkown =
			// resourceEventService.countAlarmEvent(ids,SysModuleEnum.MONITOR,new
			// InstanceStateEnum[]{InstanceStateEnum.UNKOWN},start,end,false);//未知
			// normal = resourceEventService.countAlarmEvent(ids,
			// SysModuleEnum.MONITOR,new
			// InstanceStateEnum[]{InstanceStateEnum.NORMAL}, start,
			// end,false);//正常
		}
		result.put("critical", critical);
		result.put("serious", serious);
		result.put("warn", warn);
		// result.put("unkown",unkown);
		// result.put("normal", normal);
		// int tatal = critical+serious+warn+unkown+normal;
		int tatal = critical + serious + warn;
		result.put("total", tatal);
		return result;

	}

	@Override
	public List<Map<String, Object>> getMerticinfos(long instanceId, String type) {
		List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
		try {
			// 进程数据展示
			ResourceInstance instance = resourceInstanceService
					.getResourceInstance(instanceId);
			
			//数据转换
			DecimalFormat df = new DecimalFormat("###.##");
			
			if (instance != null) {
				logger.error("a: " + instance.getParentCategoryId() + "===="
						+ "b: " + instance.getCategoryId() + " c:=="
						+ instance.getResourceId());
				logger.info("a: " + instance.getParentCategoryId() + "===="
						+ "b: " + instance.getCategoryId() + " c:=="
						+ instance.getResourceId());
				// " c:=="+instance.getResourceId());
				List<Map<String, Object>> infolist = this.getMetricByType(
						instanceId, "InformationMetric",true);
				List<Map<String, Object>> perlist = this.getMetricByType(
						instanceId, "PerformanceMetric",true);
				// List<Map<String, Object>> values= new
				// ArrayList<Map<String,Object>>();
//				System.out.println("a: " + instance.getParentCategoryId() + "===="
//						+ "b: " + instance.getCategoryId() + " c:=="
//						+ instance.getResourceId());
				for (int j = 0; j < perlist.size(); j++) {
					infolist.add(perlist.get(j));
				}
				for (int i = 0; i < infolist.size(); i++) {
					Map<String, Object> map = infolist.get(i);
					
					if (!map.isEmpty()) {
						if (instance.getParentCategoryId().equals("Host")) {
							if (instance.getCategoryId().equals("Windows")) {// Windows
								if (instance.getResourceId().equals(
										"windowssnmp")) {

									if (map.get("id").equals("memPoolUsed")
											|| map.get("id").equals(
													"numOfProcess")
											|| map.get("id").equals(
													"icmpDelayTime")) {
										values.add(map);
									}
								} else {
									if (map.get("id").equals("memPoolUsed")
											|| map.get("id").equals(
													"numberOfThread")
											|| map.get("id").equals(
													"CoreMemUtil")
											|| map.get("id").equals(
													"VirtualMemUtil")) {
										values.add(map);
									}
								}

							} else if (instance.getCategoryId()
									.equals("Linuxs")) {
								if (instance.getResourceId()
										.equals("Linuxsnmp")) {
									if (map.get("id").equals("cpuIdleTimePer")
											|| map.get("id").equals(
													"icmpDelayTime")
											|| map.get("id").equals(
													"cpuUserModeTimePer")
											|| map.get("id").equals(
													"cpuSysModeTimePer")) {
										values.add(map);
									}

								} else {
									if (map.get("id").equals("numOfProcess")
											|| map.get("id").equals(
													"numOfZombieProcess")
											|| map.get("id").equals(
													"hardDiskIONumMain")
											|| map.get("id").equals(
													"pagingSpaceRatio")) {
										values.add(map);
									}

								}

							} else if (instance.getCategoryId()
									.equals("HPUnix")) {
								if (instance.getResourceId().equals("HPUXsnmp")) {
									if (map.get("id").equals("cpuIdleTimePer")
											|| map.get("id").equals(
													"icmpDelayTime")
											|| map.get("id").equals(
													"cpuUserModeTimePer")
											|| map.get("id").equals(
													"cpuSysModeTimePer")) {
										values.add(map);
									}

								} else {
									if (map.get("id").equals("numOfProcess")
											|| map.get("id").equals(
													"numOfZombieProcess")
											|| map.get("id").equals(
													"swapSpaceRatio")
											|| map.get("id").equals(
													"hardDiskIONumMain")) {
										values.add(map);
									}

								}
							} else if (instance.getCategoryId().equals(
									"Solarises")) {// Solarises
								if (instance.getResourceId().equals("Solaris")) {
									if (map.get("id").equals(
											"numOfZombieProcess")
											|| map.get("id").equals(
													"waitProcessQueue")
											|| map.get("id").equals(
													"swapSpaceRatio")
											|| map.get("id").equals(
													"swapSpaceRate")) {
										values.add(map);
									}

								} else {
									if (map.get("id").equals("icmpDelayTime")
											|| map.get("id").equals(
													"swapSpaceRatio")
											|| map.get("id").equals(
													"icmpInMsgsRate")
											|| map.get("id").equals(
													"icmpOutMsgsRate")) {
										values.add(map);
									}

								}

							} else if (instance.getCategoryId().equals("AIXs")) {
								if (instance.getResourceId().equals("AIX")) {
									if (map.get("id").equals("numOfProcess")
											|| map.get("id").equals(
													"numOfZombieProcess")
											|| map.get("id").equals(
													"pagingSpaceRatio")
											|| map.get("id").equals(
													"hardDiskIONum")) {
										values.add(map);
									}

								} else {
									if (map.get("id").equals("icmpDelayTime")
											|| map.get("id").equals(
													"numOfProcess")
											|| map.get("id").equals(
													"icmpMsgsRate")
											|| map.get("id").equals(
													"icmpInMsgsRate")) {
										values.add(map);
									}

								}
							} else if (instance.getCategoryId().equals(
									"ScoOpenServer")) {
								if (instance.getResourceId().equals(
										"Openserver")) {
									if (map.get("id").equals("numOfProcess")
											|| map.get("id").equals(
													"numOfZombieProcess")
											|| map.get("id").equals(
													"cpuUserModeTimePer")
											|| map.get("id").equals(
													"cpuSysModeTimePer")) {
										values.add(map);
									}

								}
							} else if (instance.getCategoryId().equals(
									"ScoUnixWare")) {
								if (map.get("id").equals("numOfProcess")
										|| map.get("id").equals(
												"numOfZombieProcess")
										|| map.get("id").equals(
												"cpuUserModeTimePer")
										|| map.get("id").equals(
												"cpuSysModeTimePer")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"FreeBSD")) {
								if (instance.getResourceId().equals("Freebsd")) {
									if (map.get("id").equals("numOfProcess")
											|| map.get("id").equals(
													"numOfZombieProcess")
											|| map.get("id").equals(
													"cpuUserModeTimePer")
											|| map.get("id").equals(
													"cpuSysModeTimePer")) {
										values.add(map);
									}

								}
							} else if (instance.getCategoryId().equals(
									"NeoKylin")) {
								if (instance.getResourceId().equals("Neokylin")) {
									if (map.get("id").equals("numOfProcess")
											|| map.get("id").equals(
													"numOfZombieProcess")
											|| map.get("id").equals(
													"cpuUserModeTimePer")
											|| map.get("id").equals(
													"cpuSysModeTimePer")) {
										values.add(map);
									}

								}
							}

						} else if (instance.getParentCategoryId().equals(
								"Database")) {
							if (instance.getCategoryId().equals("Oracles")) {
								if (map.get("id").equals("curSessionCount")
										|| map.get("id")
												.equals("deadLockCount")
										|| map.get("id").equals(
												"transactionsAltF")
										|| map.get("id").equals(
												"libCacheHitRate")) {
									values.add(map);
								}

							} else if (instance.getCategoryId()
									.equals("MySQLs")) {
								if (map.get("id").equals("connThreadCount")
										|| map.get("id").equals("curSessionCount")
										|| map.get("id")
												.equals("tableScanRate")
										|| map.get("id")
												.equals("cacheMissRate")) {
									values.add(map);
								}

							} else if (instance.getCategoryId().equals(
									"Sybases")) {
								if (map.get("id").equals("curSessionCount")
										|| map.get("id").equals("curLockCount")
										|| map.get("id")
												.equals("diskWirteRate")
										|| map.get("id").equals(
												"diskErrorPacketRate")) {
									values.add(map);
								}

							} else if (instance.getCategoryId().equals("DB2s")) {
								if (map.get("id").equals("dbLogRate")
										|| map.get("id").equals(
												"tranRollbackRate")
										|| map.get("id").equals("IOReadCount")
										|| map.get("id").equals("IOWriteCount")) {
									values.add(map);
								}

							} else if (instance.getCategoryId().equals(
									"Informixs")) {
								if (map.get("id").equals("curSessionCount")
										|| map.get("id")
												.equals("latchWaitRate")
										|| map.get("id").equals("phyWriteRate")
										|| map.get("id").equals("phyReadRate")) {
									values.add(map);
								}

							} else if (instance.getCategoryId().equals(
									"SQLServers")) {
								if (map.get("id").equals("curSessionCount")
										|| map.get("id").equals("deadLockCount")
										|| map.get("id").equals("cacheHitRate")
										|| map.get("id").equals(
												"TransactionRate")) {
									values.add(map);
								}

							} else if (instance.getCategoryId().equals(
									"PostgreSQLs")) {
								if (map.get("id").equals("curSessionCount")
										|| map.get("id")
												.equals("lockWaitCount")
										|| map.get("id").equals("cacheHitRate")
										|| map.get("id").equals(
												"tranRollbackRate")) {
									values.add(map);
								}

							} else if (instance.getCategoryId()
									.equals("caches")) {
								if (map.get("id").equals("cacheActConn")
										|| map.get("id").equals(
												"cacheResponseTime")
										|| map.get("id").equals(
												"cacheECPDatSvAcCon")
										|| map.get("id").equals("cacheReqRcvd")) {
									values.add(map);
								}

							} else if (instance.getCategoryId().equals(
									"ShenzhouSQLs")) {
								if (map.get("id").equals("szBuffers")
										|| map.get("id").equals("freeBuffers") || map.get("id").equals("logTotalSize") || map.get("id").equals("numberOfDatafile")) {
									values.add(map);
								}

							}

						} else if (instance.getParentCategoryId().equals(
								"WebServer")) {// WebServer
							if (instance.getCategoryId().equals(
									"ApacheHTTPServer")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("BSecond")
										|| map.get("id").equals("requestsSec")
										|| map.get("id").equals("BRequest")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals("IIS")) {// ?
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"BytesSentPersec")
										|| map.get("id").equals(
												"CurrConnections")
										|| map.get("id").equals(
												"GetRequestRate")) {
									values.add(map);
								}
							}
						} else if (instance.getParentCategoryId().equals(
								"MailServer")) {// WebServer
							if (instance.getCategoryId().equals(
									"MSExchangeMail")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"OutputQueueLength")
										|| map.get("id").equals(
												"BoxInQueueLength")) {
									values.add(map);
								}
							}

						} else if (instance.getParentCategoryId().equals(
								"Directory")) {// WebServer
							if (instance.getCategoryId().equals(
									"IBMDirectoryServer")) {
								if (map.get("id").equals("CurConnCount")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"SunJESDirectoryServer")) {
								// icmpDelayTime
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("CurConnCount")
										|| map.get("id").equals(
												"CurThreadCount")
												|| map.get("id").equals("CurReadwaitCount")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"WindowsServerAD")) {
								// icmpDelayTime
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"maxConnIdleTime")) {
									values.add(map);
								}
							}
						} else if (instance.getParentCategoryId().equals(
								"LotusDomino")) {// WebServer
							if (instance.getCategoryId().equals(
									"LotusDominoServer")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"HttpResponseTime")
										|| map.get("id").equals("sessioncount")
										|| map.get("id").equals(
												"DropSessionCount")) {
									values.add(map);
								}
							}
						} else if (instance.getParentCategoryId().equals(
								"VMware")) {// WebServer
							if (instance.getCategoryId().equals(
									"VirtualCluster")) {
								if (map.get("id").equals("AvailMEM")
										|| map.get("id").equals("AvailCPU")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"VirtualVM")) {
								if (map.get("id").equals("MEMVirtualIncrease")
										|| map.get("id").equals("DiskUsed")
										|| map.get("id").equals(
												"DiskAssignedSpace")
										|| map.get("id")
												.equals("DiskUsedSpace")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"VirtualHost")) {
								if (map.get("id").equals("MEMVirtualIncrease")
										|| map.get("id").equals("DiskUsed")) {
									values.add(map);
								}
							}
						} else if (instance.getParentCategoryId().equals("Xen")) {// 未找到
							if (instance.getCategoryId().equals("XenHosts")
									|| instance.getCategoryId()
											.equals("XenVMs")) {
								if (map.get("id").equals("sysUpTime")) {
									values.add(map);
								}
							} else {
								if (map.get("id").equals("physicalSize")
										|| map.get("id").equals(
												"physicalUtilisation")) {
									values.add(map);
								}
							}
							// 无
						} else if (instance.getParentCategoryId().equals("Kvm")) {
							if (instance.getCategoryId().equals("KvmHosts")) {
								if (map.get("id").equals("totalCpu")
										|| map.get("id").equals("totalMem")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"KvmDataStores")) {
								if (map.get("id").equals("physicalSize")
										|| map.get("id").equals(
												"actualFreeSize")
										|| map.get("id").equals(
												"physicalUtilisation")) {
									values.add(map);
								}
							}
						} else if (instance.getParentCategoryId().equals(
								"FusionCompute")) {// 未找到
							if (instance.getCategoryId().equals(
									"FusionComputeClusters")) {
								if (type.equals("memRate")) {
									if (map.get("id").equals("totalMemSize")
											|| map.get("id").equals(
													"usableMemSize")) {
										values.add(map);
									}
								} else {
									if (map.get("id").equals("totalCpuSize")
											|| map.get("id").equals(
													"usableCpuSize")) {
										values.add(map);
									}
								}

							}
							// 无
						} else if (instance.getParentCategoryId().equals(
								"LotusDomino")) {// 未找到
							if (instance.getCategoryId().equals(
									"LotusDominoServer")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"HttpResponseTime")
										|| map.get("id").equals("sessioncount")
										|| map.get("id").equals(
												"DropSessionCount")) {
									values.add(map);
								}
							}
						} else if (instance.getParentCategoryId().equals(
								"Middleware")) {// Middleware
							if (instance.getCategoryId().equals("CICS")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("totalMemSize")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"SharePoint")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"BytesSentPersec")
										|| map.get("id").equals(
												"BytesReceivedPersec")
										|| map.get("id").equals(
												"CacheTotalMisses")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals("Ice")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("GridLoadAvg")) {
									values.add(map);
								}
							} else if (instance.getCategoryId()
									.equals("Tuxedo")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("NumRq")
										|| map.get("id").equals("NumSvc")
										|| map.get("id").equals("NumSvr")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"WebSphereMQ")) {
								if (map.get("id").equals("QueueManagerStatus")
										|| map.get("id").equals(
												"ListenerStatus")
										|| map.get("id").equals("ChannelCount")
										|| map.get("id").equals("QueueCount")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"NginxHTTPServer")) {
								if (map.get("id").equals("appCpuRate")
										|| map.get("id").equals("Waiting")
										|| map.get("id").equals("requestpers")
										|| map.get("id").equals(
												"connopenedpers")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"TongLINKQ")) {
								if (map.get("id").equals("icmpDelayTime")) {
									values.add(map);
								}
							}
						} else if (instance.getParentCategoryId().equals(
								"J2EEAppServer")) {
							if (instance.getCategoryId().equals("SunJESAS")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("JVMMEMRate")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"Weblogic")) {
								if (map.get("id").equals("QueueLength")
										|| map.get("id").equals("Throughput")
										|| map.get("id").equals(
												"SuccessTransTime")
										|| map.get("id").equals(
												"HeapFreePercent")) {
									values.add(map);
								}
							} else if (instance.getCategoryId()
									.equals("Tomcat")) {
								if (map.get("id").equals("HeapMemoryUsage")
										|| map.get("id").equals(
												"connectionCount")
										|| map.get("id").equals(
												"currentThreadCount")
										|| map.get("id").equals(
												"currentThreadsBusy")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"JbossAS")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("maxThreads")
										|| map.get("id").equals(
												"currentThreadsBusy")
										|| map.get("id").equals(
												"currentThreadCount")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"Tongweb")) {
								if (map.get("id").equals("peakThreadCount")
										|| map.get("id").equals(
												"nowThreadCount")
										|| map.get("id").equals(
												"totalSTC")
										|| map.get("id").equals(
												"daemonThreadCount")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"OracleAS")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("JVMMEMRate")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals("Resin")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"ActiveConnCount")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals(
									"WebSphereAS")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("JVMMEMRate")
										|| map.get("id").equals(
												"ActiveThreadCnt")
										|| map.get("id").equals("PoolUsedRate")) {
									values.add(map);
								}
							} else if (instance.getCategoryId().equals("WPS")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals("JVMMEMRate")
										|| map.get("id").equals("AvgWaitTime")
										|| map.get("id").equals(
												"NumberOfWebApp")) {
									values.add(map);
								}
							}
						} else if (instance.getParentCategoryId().equals(
								"CacheServer")) {// WebServer
							if (instance.getCategoryId().equals("RedisServer")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"blockedClients")
										|| map.get("id").equals(
												"pubsubChannels")
										|| map.get("id").equals(
												"pubsubPatterns")) {
									values.add(map);
								}
							}
						}else if(instance.getParentCategoryId().equals("Storage")){//存储
							if (instance.getCategoryId().equals("DiskArray")) {
								if (map.get("id").equals("icmpDelayTime")
										|| map.get("id").equals(
												"cpuUserModeTimePer")
										|| map.get("id").equals(
												"cpuIdleTimePer")
										|| map.get("id").equals(
												"cpuSysModeTimePer")) {
									values.add(map);
								}
							}
							
						}

						// MSExchangeMail

					}
				}
			}

			// tdMap.put("metricId", getElementAttrVal(e, "title"));

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return values;
	}

	/**
	 * 查询指标名称
	 */
	@Override
	public List<Map<String, Object>> getcustomMetric(Long instanceId,
			String metricName) {
		List<Map<String, Object>> metricData = getMetricByTypeName(instanceId,
				"PerformanceMetric", metricName);
		metricData.addAll(getMetricByTypeName(instanceId, "InformationMetric",
				metricName));
		return metricData;
	}

	@Override
	public List<Map<String, Object>> getMetricByTypeName(Long instanceId,
			String metricType, String metricName) {
		List<Map<String, Object>> metricList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> metricMaps = new HashMap<String, Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 指标状态
		Map<String, MetricStateEnum> msdMaps = new HashMap<String, MetricStateEnum>();
		try {
			ResourceInstance instance = resourceInstanceService
					.getResourceInstance(instanceId);
			// 可用性指标List
			List<String> availabilityList = new ArrayList<String>();
			// 信息指标List
			List<String> InfoMetricList = new ArrayList<String>();
			// 性能指标List
			List<String> perfMetricList = new ArrayList<String>();
			List<String> perfMetricStateList = new ArrayList<String>();
			// 指标基本信息
			// List<ProfileMetric> profileMetricList =
			// profileService.getMetricByInstanceId(instanceId);
			List<ProfileMetric> profileMetricList = profileService
					.getProfileMetricsByResourceInstanceId(instanceId);
			logger.error("profileMetricList:" + profileMetricList);
			for (int i = 0; profileMetricList != null
					&& i < profileMetricList.size(); i++) {
				ProfileMetric profileMetric = profileMetricList.get(i);
				Map<String, Object> metric = createMetric(instance,
						profileMetric,false);
				logger.error("metric:" + metric);
				if (!metric.isEmpty()) {
					metric.put("isCustomMetric", false);
					if (MetricTypeEnum.PerformanceMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.PerformanceMetric.toString()
									.equals(metricType)) {
						// 指标名称查询
						if (metricName != null && metricName != "") {
							String str = (String) metric.get("text");
							if (str.toUpperCase().indexOf(metricName.toUpperCase()) != -1) {
								metricMaps.put(profileMetric.getMetricId(),
										metric);
								perfMetricList.add(profileMetric.getMetricId());
								perfMetricStateList.add(profileMetric
										.getMetricId());
							}
						} else {
							metricMaps.put(profileMetric.getMetricId(), metric);
							perfMetricList.add(profileMetric.getMetricId());
							perfMetricStateList
									.add(profileMetric.getMetricId());
						}
					} else if (MetricTypeEnum.InformationMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.InformationMetric.toString()
									.equals(metricType)) {
						// 指标名称查询
						if (metricName != null && metricName != "") {
							String str = (String) metric.get("text");
							if (str.toUpperCase().indexOf(metricName.toUpperCase()) != -1) {
								metricMaps.put(profileMetric.getMetricId(),
										metric);
								InfoMetricList.add(profileMetric.getMetricId());
								// 初始化指标状态值
//								msdMaps.put(profileMetric.getMetricId(),
//										MetricStateEnum.NORMAL);
							}
						} else {

							metricMaps.put(profileMetric.getMetricId(), metric);
							InfoMetricList.add(profileMetric.getMetricId());
							// 初始化指标状态值
//							msdMaps.put(profileMetric.getMetricId(),
//									MetricStateEnum.NORMAL);
						}
					} else if (MetricTypeEnum.AvailabilityMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.AvailabilityMetric.toString()
									.equals(metricType)) {
						metricMaps.put(profileMetric.getMetricId(), metric);
						availabilityList.add(profileMetric.getMetricId());
					}
				}
			}
			// 自定义指标基本信息
			List<CustomMetric> customMetrics = customMetricService
					.getCustomMetricsByInstanceId(instanceId);
			for (int i = 0; customMetrics != null && i < customMetrics.size(); i++) {
				CustomMetric customMetric = customMetrics.get(i);
				Map<String, Object> metric = createMetric(instance,
						customMetric);
				if (!metric.isEmpty()) {
					metric.put("isCustomMetric", true);
					if (MetricTypeEnum.PerformanceMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.PerformanceMetric.toString()
									.equals(metricType)) {
						if (metricName != null && metricName != "") {
							String str = (String) metric.get("text");
							if (str.toUpperCase().indexOf(metricName.toUpperCase()) != -1) {
								metricMaps.put(customMetric.getCustomMetricInfo().getId(),
										metric);
								//perfMetricList.add(customMetric.getCustomMetricInfo().getId());
								perfMetricStateList.add(customMetric.getCustomMetricInfo()
										.getId());
							}
						}else{
							metricMaps.put(customMetric.getCustomMetricInfo()
									.getId(), metric);
							perfMetricStateList.add(customMetric
									.getCustomMetricInfo().getId());
						}
					} else if (MetricTypeEnum.InformationMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.InformationMetric.toString()
									.equals(metricType)) {
						if (metricName != null && metricName != "") {
							String str = (String) metric.get("text");
							if (str.toUpperCase().indexOf(metricName.toUpperCase()) != -1) {
								metricMaps.put(customMetric.getCustomMetricInfo()
										.getId(), metric);
								InfoMetricList.add(customMetric
										.getCustomMetricInfo().getId());
								// 初始化指标状态值
//								msdMaps.put(customMetric.getCustomMetricInfo().getId(),
//										MetricStateEnum.NORMAL);
							}
						}else{
							metricMaps.put(customMetric.getCustomMetricInfo()
									.getId(), metric);
							InfoMetricList.add(customMetric
									.getCustomMetricInfo().getId());
							// 初始化指标状态值
//							msdMaps.put(customMetric.getCustomMetricInfo().getId(),
//									MetricStateEnum.NORMAL);
						}
					} else if (MetricTypeEnum.AvailabilityMetric.equals(metric
							.get("type"))
							&& MetricTypeEnum.AvailabilityMetric.toString()
									.equals(metricType)) {
						metricMaps.put(customMetric.getCustomMetricInfo()
								.getId(), metric);
					}
				}
			}
			// 性能指标 状态查询
			if (!perfMetricStateList.isEmpty()) {
				List<Long> instanceIdList = new ArrayList<Long>();
				instanceIdList.add(instanceId);
				List<MetricStateData> msdList = metricStateService
						.findMetricState(instanceIdList, perfMetricStateList);
				for (int i = 0; msdList != null && i < msdList.size(); i++) {
					MetricStateData msd = msdList.get(i);
					msdMaps.put(msd.getMetricID(), msd.getState());
				}
			}
			
			if(!InfoMetricList.isEmpty()){
				List<Long> instanceIdList = new ArrayList<Long>();
				instanceIdList.add(instanceId);
				List<MetricStateData> msdList = metricStateService
						.findMetricState(instanceIdList, InfoMetricList);
				for (int i = 0; msdList != null && i < msdList.size(); i++) {
					MetricStateData msd = msdList.get(i);
					msdMaps.put(msd.getMetricID(), msd.getState());
				}
			}
			
			// 查询指标值
			if (!InfoMetricList.isEmpty()) {
				// List<MetricData> infoMetrics = metricDataService
				// .getMetricInfoDatas(instanceId, InfoMetricList
				// .toArray(new String[InfoMetricList.size()]));
				// 查询信息指标需要过滤
				List<MetricData> infoMetrics = infoMetricQueryAdaptService
						.getMetricInfoDatas(instanceId, InfoMetricList
								.toArray(new String[InfoMetricList.size()]));
				for (int i = 0; i < infoMetrics.size(); i++) {
					MetricData infoMetric = infoMetrics.get(i);
					if (metricMaps.containsKey(infoMetric.getMetricId())) {
						Map<String, Object> allMetric = metricMaps
								.get(infoMetric.getMetricId());
						// 当前值
						String val = emptyFirstLastChar(infoMetric.getData());
						val = "".equals(val) ? val : UnitTransformUtil
								.transform(val, (String) allMetric.get("unit"));
						allMetric.put("currentVal", val);
						// 采集时间
						allMetric.put("lastCollTime",
								sdf.format(infoMetric.getCollectTime()));
						// 指标状态
						if (msdMaps.containsKey(infoMetric.getMetricId())) {
							allMetric.put("status",
									msdMaps.get(infoMetric.getMetricId())
											.toString());
						} else {
							allMetric.put("status",
									MetricStateEnum.NORMAL.toString());
						}
						
						//基线采集
						if(infoMetric.getTimelineId() != null && infoMetric.getTimelineId() > 0){
							Timeline timelineObject = timelineService.getTimelinesById(infoMetric.getTimelineId());
							// 基线有可能已被删除
							if (timelineObject != null) {
								allMetric.put("timelineid", infoMetric.getTimelineId());
							}
						}
						
					}
				}
			} else if (!perfMetricList.isEmpty()) {
				// 查询性能指标采集数据
				MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
				mrdq.setInstanceID(new long[] { instanceId });
				mrdq.setMetricID(perfMetricList
						.toArray(new String[perfMetricList.size()]));
				Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService
						.queryRealTimeMetricDatas(mrdq, 0, 1000);
				logger.error("page:"+page.getDatas());
				for (int i = 0; i < page.getDatas().size(); i++) {
					Map<String, ?> perfMetricMap = page.getDatas().get(i);
					perfMetricMap.remove("instanceid");
					if (!perfMetricMap.isEmpty()) {
						Iterator<String> iter = perfMetricMap.keySet()
								.iterator();
						while (iter.hasNext()) {
							String metricId = iter.next();
							// 采集时间、策略ID、基线ID
							if (metricId.toLowerCase().endsWith(
									"CollectTime".toLowerCase())
									|| metricId.toLowerCase().endsWith(
											"ProfileId".toLowerCase())
									|| metricId.toLowerCase().endsWith(
											"TimelineId".toLowerCase())) {
								continue;
							}
							if (metricMaps.containsKey(metricId)) {
								Map<String, Object> allMetric = metricMaps
										.get(metricId);
								DecimalFormat df = new DecimalFormat("###.##");
								// 当前值
								String currentVal = perfMetricMap.get(metricId) == null ? ""
										: df.format(perfMetricMap.get(metricId))
												.toString();
								// 自定义指标不带单位
								currentVal = UnitTransformUtil.transform(
										currentVal,
										(String) allMetric.get("unit"));
								allMetric.put("currentVal", currentVal);
								// 采集时间
								String time = perfMetricMap.get(metricId
										+ "CollTime") == null ? "" : sdf
										.format(perfMetricMap.get(metricId
												+ "CollTime"));
								allMetric.put("lastCollTime", time);
								// 指标状态
								if (msdMaps.containsKey(metricId)) {
									allMetric.put("status",
											msdMaps.get(metricId).toString());
								} else {
									// allMetric.put("status",MetricStateEnum.UNKOWN.toString());
									allMetric.put("status",
											MetricStateEnum.NORMAL.toString());
								}
								// 是否为基线采集
								if (perfMetricMap.containsKey(metricId
										+ "TimelineId")
										&& perfMetricMap.get(metricId
												+ "TimelineId") != null) {
									Long timelineId = null;
									if (perfMetricMap.get(metricId
											+ "TimelineId") instanceof BigDecimal) {
										logger.error("Object type is BigDecimal : "
												+ metricId + "TimelineId");
										timelineId = ((BigDecimal) perfMetricMap
												.get(metricId + "TimelineId"))
												.longValue();
									} else {
										timelineId = (Long) perfMetricMap
												.get(metricId + "TimelineId");
									}
									Timeline timelineObject = timelineService.getTimelinesById(timelineId);
									// 基线有可能已被删除
									if (timelineObject != null) {
										allMetric.put("timelineid", timelineId);
									}
								}
							}
						}
					}
				}
			} else if (!availabilityList.isEmpty()) {
				// 可用性指标
				for (int i = 0; i < availabilityList.size(); i++) {
					String metricId = availabilityList.get(i);
					if (metricMaps.containsKey(metricId)) {
						Map<String, Object> allMetric = metricMaps
								.get(metricId);
						allMetric.put("status",
								queryMetricState(instanceId, metricId));
						MetricData md = metricDataService
								.getMetricAvailableData(instanceId, metricId);
						String currentVal = md == null || md.getData() == null ? ""
								: md.getData()[0];
						allMetric.put("currentVal", currentVal);
						String time = md == null || md.getCollectTime() == null ? ""
								: sdf.format(md.getCollectTime());
						allMetric.put("lastCollTime", time);
					}
				}
			}
			// 自定义指标、并组装返回数据
			Iterator<String> iter = metricMaps.keySet().iterator();
			while (iter.hasNext()) {
				String metricId = iter.next();
				Map<String, Object> allMetric = metricMaps.get(metricId);
				if (allMetric.containsKey("isCustomMetric")
						&& (boolean) allMetric.get("isCustomMetric")) {
					MetricData customMetricData = metricDataService
							.getCustomerMetricData(instanceId, metricId);
					// 当前值
					String customCurrentVal = customMetricData == null
							|| customMetricData.getData() == null ? ""
							: customMetricData.getData()[0];
					customCurrentVal = MetricTypeEnum.AvailabilityMetric
							.toString().equals(metricType) ? customCurrentVal
							: UnitTransformUtil.transform(customCurrentVal,
									(String) allMetric.get("unit"));
					allMetric.put("currentVal", customCurrentVal);
					// 采集时间
					String customTime = customMetricData == null
							|| customMetricData.getCollectTime() == null ? ""
							: sdf.format(customMetricData.getCollectTime());
					allMetric.put("lastCollTime", customTime);
					// 指标状态
					if (msdMaps.containsKey(metricId)) {
						allMetric.put("status", msdMaps.get(metricId)
								.toString());
					} else {
						// allMetric.put("status",MetricStateEnum.UNKOWN.toString());
						allMetric.put("status",
								MetricStateEnum.NORMAL.toString());
					}
					// 自定义可用性指标状态
					if (MetricTypeEnum.AvailabilityMetric.toString().equals(
							metricType)) {
						allMetric.put("status",
								queryMetricState(instanceId, metricId));
					}
				}
				metricList.add(allMetric);
			}
		} catch (Exception e) {
			logger.error("getMetricByType", e);
		}
		return metricList;
	}

	@Override
	public int getDomainHave(long instanceid) {
		int num=0;
	try {
		ResourceInstance instance=	resourceInstanceService.getResourceInstance(instanceid);
		ILoginUser user=	BaseAction.getLoginUser();

		if(instance!=null){
			if(user!=null){
				Set<IDomain> iDomains=	user.getDomains();
				if(iDomains.size()!=0){
					for (IDomain iDomain : iDomains) {
						if(iDomain.getId()==instance.getDomainId()){
							num=1;
						}
					}
				}
				}
		}else{
			num=0;
		}
	} catch (InstancelibException e) {
	
		e.printStackTrace();
	}
	return num;
	}

	@Override
	public Map<String, Object> getMetricTop5(long instanceId) {
		Map<String, Object>	tdMap = new HashMap<String, Object>();
		long  startTime=System.currentTimeMillis();
	

		try {
			ResourceInstance instance=resourceInstanceService.getResourceInstance(instanceId);
		if(instance!=null){
			List<Long> idsTemp= new ArrayList<Long>(); 
			if(instance.getParentId()==0){

		List<ResourceInstance> instances=	instance.getChildren();
		if(instances!=null && instances.size()!=0){
			for (int i = 0; i < instances.size(); i++) {
				idsTemp.add(instances.get(i).getId());
			}
		}
		idsTemp.add(instance.getId());
	}else{
		idsTemp.add(instance.getId());
	}
	long[] ids= new long[idsTemp.size()];
	for (int i = 0; i < ids.length; i++) {
		ids[i]=idsTemp.get(i).longValue();
	}
			List<MetricData> metricdatas=	metricDataService.findTop("ifOctetsSpeed", ids, 5);
//			ResourceMetricDef 	def=	capacityService.getResourceMetricDef(instance.getResourceId(), "ifOctetsSpeed");
//		System.out.println(def==null?null:def.getUnit());
			//if(metricData!=null  && metricData){
			List<String> topcate = new ArrayList<String>();
			List<String> topdata = new ArrayList<String>();
			Map<String, Object> valueData = new HashMap<String, Object>();
			List<Map<String, Object>> topdatas = new ArrayList<Map<String, Object>>();
				if(metricdatas!=null  && metricdatas.size()!=0){
					for (int i = 0; i < metricdatas.size(); i++) {
				MetricStateData mdata=metricStateService.getMetricState(Long.valueOf(metricdatas.get(i).getResourceInstanceId()), metricdatas.get(i).getMetricId());
				Map<String, Object> dataObj = new HashMap<String, Object>();
			
					ResourceInstance resourceInstance=resourceInstanceService.getResourceInstance(Long.valueOf(metricdatas.get(i).getResourceInstanceId()));
					topcate.add(resourceInstance!=null?resourceInstance.getName():"");
					String nameStr=	UnitTransformUtil.transform(metricdatas.get(i).getData()[0],"bps");//通过接口查单位
				
					dataObj.put("name", nameStr);
					dataObj.put("y", metricdatas.get(i).getData()[0]);
					dataObj.put("color", "");//getMetricStateEnumString(mdata)
					dataObj.put("state", mdata==null ?null : mdata.getState());
					topdatas.add(dataObj);
				
					}
				}
		
			valueData.put("categories", topcate);
			valueData.put("data", topdata);
			valueData.put("Sortdata", topdatas);
			tdMap.put("title", "接口流量TOP5");
			tdMap.put("value", valueData);
			long  endTime=System.currentTimeMillis();
			long time=(endTime-startTime);
			logger.info("getMetricTop5 query time :"+time);
			return tdMap;
		}else{
			logger.error("query resourceDetail instance is null: "+instanceId);
			tdMap.put("title", "接口流量TOP5");
			return tdMap;
		}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error("getMetricTop5 resourceDetail error:"+e1);
			tdMap.put("title", "接口流量TOP5");
			return tdMap;
		}
	
	}

	
}
