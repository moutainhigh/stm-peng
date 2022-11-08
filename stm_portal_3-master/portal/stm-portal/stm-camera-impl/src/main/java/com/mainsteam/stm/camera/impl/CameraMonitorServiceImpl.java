package com.mainsteam.stm.camera.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mainsteam.stm.system.um.domain.bo.Domain;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.camera.api.ICameraDao;
import com.mainsteam.stm.camera.api.ICameraMonitorService;
import com.mainsteam.stm.camera.bo.CameraMonitorPageBo;
import com.mainsteam.stm.camera.bo.CameraResourceBo;
import com.mainsteam.stm.camera.bo.CameraResourcePageBo;
import com.mainsteam.stm.camera.bo.CaremaMonitorBo;
import com.mainsteam.stm.camera.bo.JDBCVo;
import com.mainsteam.stm.camera.bo.TreeVo;
import com.mainsteam.stm.camera.util.CameraUtils;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryMetric;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryInstanceDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryMetricDao;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryInstancePo;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryMetricPo;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryPo;
import com.mainsteam.stm.portal.report.po.ReportTemplatePo;
import com.mainsteam.stm.portal.report.service.impl.ReportModelMain;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IReAccountInstanceApi;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.resource.api.Filter;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.system.resource.sortable.api.IResourceSortableBySingleFieldApi;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.SecureUtil;
import com.mainsteam.stm.util.StringUtil;
import com.mainsteam.stm.util.Util;

public class CameraMonitorServiceImpl implements ICameraMonitorService {

	private  Logger logger = Logger.getLogger(CameraMonitorServiceImpl.class);

	@Resource(name = "resourceInstanceService")
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private CapacityService capacityService;

	@Resource
	private ProfileService profileService;

	@Resource
	private InstanceStateService instanceStateService;

	@Resource
	private MetricDataService metricDataService;

	@Resource
	private MetricStateService metricStateService;

	@Resource
	private IReportTemplateDirectoryDao iReportTemplateDirectoryDao;

	@Resource
	private ISearchApi searchApi;

	@Resource
	private IDomainApi domainApi;

	@Resource
	private IUserApi userApi;

	@Resource
	private IReportTemplateDao iReportTemplateDao;

	@Resource(name = "stm_system_resourceApi")
	private IResourceApi resourceApi;

	@Resource
	private IFileClientApi fileClient;

	@Resource
	private ISequence ReportTemplateDirectoryMetricSeq;

	@Resource
	private ISequence ReportTemplateDirectorySeq;

	@Resource
	private ICustomResourceGroupApi customResourceGroupApi;

	@Resource
	private IReAccountInstanceApi resourceReAccountInstanceApi;

	@Resource
	private IResourceSortableBySingleFieldApi ResourceSortableBySingleFieldApi;

	@Resource
	private ResourceCategoryApi resourceCategoryApi;

	@Resource
	private ISequence ReportTemplateDirectoryInstanceSeq;

	@Resource
	private NodeService nodeService;

	@Resource
	private BizMainApi bizMainApi;

	@Resource
	private CameraReportEngine cameraReportEngine;

	private static String DETAULT_TABLE_COLUMNS_COUNT = "1";

	@Resource
	private CustomPropService customPropService;

	@Resource(name = "scheduleManager")
	private ScheduleManager scheduleManager;

	private static final String SEARCH_NAV = "资源管理-资源列表";

	private static final String ISCUSTOMGROUP_YES = "0";
	private List<String> hideCDefs = new ArrayList();

	private static Map<Long, CaremaMonitorBo> metricQueryMap = new HashMap<Long, CaremaMonitorBo>();;

	@Resource
	private IReportTemplateDirectoryMetricDao iReportTemplateDirectoryMetricDao;

	private static long abnormalNum = 0;

	private static long offlineNum = 0;

	@Resource
	private IReportTemplateDirectoryInstanceDao iReportTemplateDirectoryInstanceDao;

	public static List<CaremaMonitorBo> cameraList;

	public List<CaremaMonitorBo> getCameraList() {
		return cameraList;
	}

	public Map<Long, CaremaMonitorBo> getMetricQueryMap(){
	    return metricQueryMap;
    }

	public void setCameraList(List<CaremaMonitorBo> cameraList) {
		this.cameraList = cameraList;
	}

	@Resource
	private ISequence ReportTemplateSeq;

	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;

	@Resource(name = "resourceDetailInfoApi")
	private IResourceDetailInfoApi resourceDetailInfoApi;

	@Resource
	private ICameraDao cameraDao;

    @Resource
    private ModulePropService modulePropService;


	public List<JDBCVo> getDataBaseList(String configFile) {
		return cameraDao.getDataBaseList(configFile);
	}

	public List<TreeVo> getCameraGroupList(String configFile, List<JDBCVo> jdbcList) {
		return cameraDao.getCameraGroupList(configFile, jdbcList);
	}

	public List<TreeVo> getCameraListByParentId(long parentId, List<JDBCVo> jdbcList,
			Map<String, List<CaremaMonitorBo>> groupMap) {
		return cameraDao.getCameraListByParentId(parentId, jdbcList, groupMap);
	}


	 public void loadChildCameraList(long parentId,List<TreeVo> treeList,List<Connection> connectList,Map<String,List<CaremaMonitorBo>> groupMap){
		 cameraDao.loadChildCameraList(parentId, treeList, connectList, groupMap);
	 }

	 public List<Connection> getConnection(List<JDBCVo> jdbcList){
		 return cameraDao.getConnection(jdbcList);
	 }

	/**
	 * 得到性能报表-摄像头-计划上报数
	 *
	 */
	public String getPlanNumber(String configFile, String elementName, String id, int dignoseNumber) {

		logger.info("开始计算计划上报数");
		List<Element> elements = CameraUtils.getListFromXML(configFile);

		Element groups = elements.get(0);

		List<DefaultElement> groupElements = groups.elements(elementName);
		for (int s = 0; s < groupElements.size(); s++) {
			DefaultElement attribute = groupElements.get(s);
			List<DefaultElement> eList = attribute.elements();
			boolean flag = false;
			for (DefaultElement child : eList) {
				String name = child.getName();
				String text = child.getText();
				if (name.equals("id") && text.equals(id)) {
					flag = true;
				}
				if (flag && name.equals("number")) {
					return text;
				}

			}
		}
		if (dignoseNumber == 0)
			return "0";
		else {
			return String.valueOf(dignoseNumber);
		}

	}

	/**
	 * 得到XML配置文件得到权重
	 *
	 */
	public Map<String, Double> getRationFromConfig(String configFile) {
		Map<String, Double> map = new HashMap<String, Double>();
		List<Element> elements = CameraUtils.getListFromXML(configFile);
		Element ratios = elements.get(2);
		Element connectivity = ratios.element("connectivity");
		String connectivityValue = connectivity.getStringValue();
		//接入率
		double connectivityDouble = 0.00;
		if (!StringUtil.isNull(connectivityValue)) {
			connectivityDouble = Double.parseDouble(connectivityValue);
		}
		map.put(connectivity.getName(), connectivityDouble);
		Element online = ratios.element("online");
		String onlineValue = online.getStringValue();
		//在线率
		double onlineDouble = 0.00;
		if (!StringUtil.isNull(onlineValue)) {
			onlineDouble = Double.parseDouble(onlineValue);
		}
		map.put(online.getName(), onlineDouble);

		Element completion = ratios.element("completion");
		String completionValue = completion.getStringValue();
		//图像完好率
		double completionDouble = 0.00;
		if (!StringUtil.isNull(completionValue)) {
			completionDouble = Double.parseDouble(completionValue);
		}
		map.put(completion.getName(), completionDouble);
		Element xycompletion = ratios.element("xycompletion");
		String xycompletionValue = xycompletion.getStringValue();
        //GIS率
		double xycompletionDouble = 0.00;
		if (!StringUtil.isNull(xycompletionValue)) {
			xycompletionDouble = Double.parseDouble(xycompletionValue);
		}

		map.put(xycompletion.getName(), xycompletionDouble);
		return map;
	}

	public List<TreeVo> getAllResourceCategoryNew() {
		List category = new ArrayList();
		//调用原有的信息，得到非摄像头的资源树状列表
		loadResourceCategory(category, this.capacityService.getRootCategory());
		//得到数据库连接信息
		List<JDBCVo> jdbcList = cameraDao.getDataBaseList("camera_config.xml");
         //通过数据库，查询得到摄像头的树状列表，并且拼接到原有的非摄像头的资源树
		logger.info("开始加载摄像头的资源树");
		List<TreeVo> cameraGroupList = cameraDao.getCameraGroupList("camera_config.xml", jdbcList);

		category.addAll(cameraGroupList);
		return category;
	}

	//得到摄像头的树状节点
	private void loadResourceCategory(List<TreeVo> resourceCategory, CategoryDef def) {
		if ((!(def.isDisplay())) && (!(def.getId().equals("VM")))) {
			return;
		}
		if (!(this.licenseCapacityCategory.isAllowCategory(def.getId()))) {
			return;
		}
		TreeVo category = new TreeVo();
		category.setId(def.getId());
		category.setName(def.getName());
		if (null != def.getParentCategory()) {
			category.setPid(def.getParentCategory().getId());
			category.setState("closed");
			category.setType(1);
			category.setIsCamera("N");
			resourceCategory.add(category);
		}

		if (null != def.getChildCategorys()) {
			CategoryDef[] categoryDefs = def.getChildCategorys();
			for (int i = 0; i < categoryDefs.length; ++i) {
				if (!categoryDefs[i].getId().equals("CameraPlatform")) {
					if ((categoryDefs[i].isDisplay()) || (categoryDefs[i].getId().equals("VM")))
						loadResourceCategory(resourceCategory, categoryDefs[i]);
				}
			}

		} else if (null != def.getResourceDefs()) {
			ResourceDef[] resourceDefs = def.getResourceDefs();
			for (int i = 0; i < resourceDefs.length; ++i) {
				ResourceDef resourceDef = resourceDefs[i];
				TreeVo resource = new TreeVo();
				resource.setId(resourceDef.getId());
				resource.setName(resourceDef.getName());
				resource.setType(2);
				resource.setIsCamera("N");
				resource.setPid(def.getId());
				resourceCategory.add(resource);
			}
		}

	}


	private static String getInstanceStateColor(InstanceStateEnum stateEnum) {
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
		case NORMAL_NOTHING:
			ise = "res_normal_nothing";
			break;
		case NORMAL_CRITICAL:
			ise = "res_normal_critical";
			break;
		case NORMAL_UNKNOWN:
			ise = "res_normal_unknown";
			break;

		default:
			ise = "res_normal_nothing";
		}

		return ise;
	}

	private static String getMetricStateColor(MetricStateEnum stateEnum) {
		String ise = "green";
		if (stateEnum != null) {
			switch (stateEnum) {
			case CRITICAL:
				ise = "red";
				break;
			case SERIOUS:
				ise = "orange";
				break;
			case WARN:
				ise = "yellow";
				break;
			case NORMAL:
			case NORMAL_NOTHING:
				ise = "green";
				break;
			default:
				ise = "green";
			}

		}
		return ise;
	}

	public int openMonitor(long resourceMonitorId) {
		int result = 0;
		try {
			profileService.enableMonitor(resourceMonitorId);
			List<Long> id = new ArrayList();
			id.add(Long.valueOf(resourceMonitorId));
			bizMainApi.instanceMonitorChangeBiz(id);
		} catch (Exception e) {
			logger.error("openMonitor", e);
			result = 0;
		}
		result = 1;
		return result;
	}

	private void saveSearchRel(long instanceId) {
		ResourceBizRel rbr = new ResourceBizRel(Long.valueOf(instanceId), Long.valueOf(instanceId), "资源管理-资源列表");
		searchApi.saveSearchResource(rbr);
	}

	public int batchOpenMonitor(String[] ids) {
		int result = 0;
		if ((ids != null) && (ids.length > 0)) {
			try {
				List<Long> list = new ArrayList();
				for (int i = 0; i < ids.length; i++) {
					list.add(Long.valueOf(ids[i]));
					saveSearchRel(Long.parseLong(ids[i]));
				}
				profileService.enableMonitor(list);
				bizMainApi.instanceMonitorChangeBiz(list);
				result = 1;
			} catch (Exception e) {
				result = 0;
				logger.error("batchOpenMonitor", e);
			}
		}
		return result;
	}

	private void delMonitorResource(long instanceId) {
		ResourceBizRel model = new ResourceBizRel();
		model.setBizId(Long.valueOf(instanceId));
		model.setResourceId(Long.valueOf(instanceId));
		searchApi.delCancelMonitorResource(model);
	}

	public int closeMonitor(long resourceMonitorId) throws Exception {
		profileService.cancleMonitor(resourceMonitorId);
		List<Long> id = new ArrayList();
		id.add(Long.valueOf(resourceMonitorId));
		bizMainApi.instanceMonitorChangeBiz(id);
		return 0;
	}

	public int batchCloseMonitor(long[] resourceMonitorIds) throws Exception {
		if ((resourceMonitorIds != null) && (resourceMonitorIds.length > 0)) {
			List<Long> cancelList = new ArrayList();
			for (long item : resourceMonitorIds) {
				cancelList.add(Long.valueOf(item));
				delMonitorResource(item);
			}
			profileService.cancleMonitor(cancelList);
			bizMainApi.instanceMonitorChangeBiz(cancelList);
		}
		return 0;
	}

	public int batchDelResource(long[] resourceMonitorIds) throws Exception {
		if ((resourceMonitorIds != null) && (resourceMonitorIds.length > 0)) {
			List<Long> resourceList = new ArrayList();
			for (long item : resourceMonitorIds) {
				resourceList.add(Long.valueOf(item));
			}

			resourceInstanceService.removeResourceInstances(resourceList);
		}
		return 0;
	}

	private List<CaremaMonitorBo> setMetricValue(List<CaremaMonitorBo> resourceMonitorBoList) {
		String[] metrics = { "cpuRate", "memRate" };
		long[] instanceIdArray = new long[resourceMonitorBoList.size()];
		List<Long> instanceIdList = new ArrayList();

		for (int i = 0; i < resourceMonitorBoList.size(); i++) {
			CaremaMonitorBo rmBo = (CaremaMonitorBo) resourceMonitorBoList.get(i);
			instanceIdArray[i] = rmBo.getInstanceId().longValue();
			instanceIdList.add(rmBo.getInstanceId());

			rmBo.setCpuStatus(getMetricStateColor(null));
			rmBo.setCpuAvailability("N/A");
			rmBo.setMemoryStatus(getMetricStateColor(null));
			rmBo.setMemoryAvailability("N/A");
		}

		Map<Long, Map<String, MetricStateEnum>> iMStateMap = new HashMap();
		List<MetricStateData> msdList = metricStateService.findMetricState(instanceIdList, Arrays.asList(metrics));
		for (int i = 0; (msdList != null) && (i < msdList.size()); i++) {
			MetricStateData msd = (MetricStateData) msdList.get(i);
			Long instanceId = Long.valueOf(msd.getInstanceID());
			Map<String, MetricStateEnum> mStateMap = null;
			if (iMStateMap.containsKey(instanceId)) {
				mStateMap = (Map) iMStateMap.get(instanceId);
			} else {
				mStateMap = new HashMap();
				iMStateMap.put(instanceId, mStateMap);
			}
			mStateMap.put(msd.getMetricID(), msd.getState());
		}

		MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
		mrdq.setInstanceID(instanceIdArray);
		mrdq.setMetricID(metrics);
		Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(mrdq, 1, 10000);
		List<Map<String, ?>> metricMapList = page.getDatas();

		Map<String, Map<String, ?>> reLoadMetricMap = new HashMap();
		for (int i = 0; i < metricMapList.size(); i++) {
			Map<String, ?> metricMap = (Map) metricMapList.get(i);
			if (metricMap.get("instanceid") != null) {
				String instanceId = metricMap.get("instanceid").toString();
				reLoadMetricMap.put(instanceId, metricMap);
			}
		}

		for (int i = 0; i < resourceMonitorBoList.size(); i++) {
			CaremaMonitorBo rmBo = (CaremaMonitorBo) resourceMonitorBoList.get(i);
			Long instanceId = rmBo.getInstanceId();

			if (reLoadMetricMap.containsKey(instanceId.toString())) {
				boolean isMonitor_CPU = false;
				boolean isMonitor_MEM = false;

				try {
					ProfileMetric cpuPMetric = profileService
							.getProfileMetricByInstanceIdAndMetricId(instanceId.longValue(), "cpuRate");
					if ((cpuPMetric != null) && (cpuPMetric.isMonitor())) {
						isMonitor_CPU = true;
						rmBo.setCpuIsAlarm(cpuPMetric.isAlarm());
					}

					ProfileMetric memPMetric = profileService
							.getProfileMetricByInstanceIdAndMetricId(instanceId.longValue(), "memRate");
					if ((memPMetric != null) && (memPMetric.isMonitor())) {
						isMonitor_MEM = true;
						rmBo.setMemoryIsAlarm(memPMetric.isAlarm());
					}
				} catch (ProfilelibException e) {
					logger.error("setMetricValue", e);
				}
				Map<String, ?> metricMap = (Map) reLoadMetricMap.get(instanceId.toString());

				if ((isMonitor_CPU) && (metricMap.containsKey("cpuRate"))) {
					if (metricMap.get("cpuRate") != null) {
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(rmBo.getResourceId(), "cpuRate");

						if (rmDef != null) {
							rmBo.setCpuAvailability(metricMap.get("cpuRate").toString() + rmDef.getUnit());
						} else {
							rmBo.setCpuAvailability(null);
							logger.debug("rmDef is null");
						}
					} else {
						rmBo.setCpuAvailability("--");
					}
				}

				if ((isMonitor_MEM) && (metricMap.containsKey("memRate"))) {
					if (metricMap.get("memRate") != null) {
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(rmBo.getResourceId(), "memRate");

						if (rmDef != null) {
							rmBo.setMemoryAvailability(metricMap.get("memRate").toString() + rmDef.getUnit());
						} else {
							rmBo.setMemoryAvailability(null);
						}
					} else {
						rmBo.setMemoryAvailability("--");
					}
				}

				if ((isMonitor_CPU) && (iMStateMap.containsKey(instanceId))
						&& (((Map) iMStateMap.get(instanceId)).containsKey("cpuRate"))) {
					rmBo.setCpuStatus(
							getMetricStateColor((MetricStateEnum) ((Map) iMStateMap.get(instanceId)).get("cpuRate")));
				}

				if ((isMonitor_MEM) && (iMStateMap.containsKey(instanceId))
						&& (((Map) iMStateMap.get(instanceId)).containsKey("memRate"))) {
					rmBo.setMemoryStatus(
							getMetricStateColor((MetricStateEnum) ((Map) iMStateMap.get(instanceId)).get("memRate")));
				}
			}
		}

		return resourceMonitorBoList;
	}

	private boolean isResOfCategory(CategoryDef categoryDef, String categoryId) {
		boolean flag = false;
		if (categoryDef.getId().equals(categoryId)) {
			flag = true;
		} else {
			boolean childFlag = false;
			CategoryDef[] childCategoryDefs = categoryDef.getChildCategorys();
			for (int i = 0; (childCategoryDefs != null) && (i < childCategoryDefs.length); i++) {
				CategoryDef childCategoryDef = childCategoryDefs[i];
				if (isResOfCategory(childCategoryDef, categoryId)) {
					childFlag = true;
					break;
				}
			}
			flag = childFlag;
		}
		return flag;
	}

	private String getResourceIP(ResourceInstanceBo ribo) throws InstancelibException {
		String ip = "";
		if ((null == ribo.getDiscoverIP()) || ("".equals(ribo.getDiscoverIP()))) {
			ResourceInstance ri = resourceInstanceService.getResourceInstance(ribo.getId().longValue());
			if ("GENEURL".equals(ri.getResourceId())) {
				String[] ips = ri.getDiscoverPropBykey("urlSite");
				ip = ips == null ? "" : ips[0];
			} else {
				String[] ips = ri.getDiscoverPropBykey("host");
				ips = ips == null ? ri.getModulePropBykey("IPAddress") : ips;
				ip = ips == null ? "" : ips[0];
			}
		} else {
			ip = ribo.getDiscoverIP();
		}
		return ip;
	}

	private ResourceInstanceBo ri2riBo(ResourceInstance ri) {
		ResourceInstanceBo riBo = new ResourceInstanceBo();
		riBo.setCategoryId(ri.getCategoryId());
		riBo.setDiscoverIP(ri.getShowIP());
		riBo.setDiscoverNode(ri.getDiscoverNode());
		riBo.setDiscoverWay(ri.getDiscoverWay());
		riBo.setDomainId(Long.valueOf(ri.getDomainId()));
		riBo.setId(Long.valueOf(ri.getId()));
		riBo.setLifeState(ri.getLifeState());
		riBo.setName(ri.getName());
		riBo.setParentId(Long.valueOf(ri.getParentId()));
		riBo.setResourceId(ri.getResourceId());
		riBo.setShowName(ri.getShowName());
		return riBo;
	}

	private CaremaMonitorBo riBo2RmBo(ResourceInstanceBo riBo) throws InstancelibException {
		CaremaMonitorBo rmBo = new CaremaMonitorBo();
		rmBo.setCategoryId(riBo.getCategoryId());
		rmBo.setInstanceId(riBo.getId());
		rmBo.setResourceId(riBo.getResourceId());
		ResourceDef rDef = capacityService.getResourceDefById(riBo.getResourceId());
		rmBo.setResourceName(rDef == null ? "--" : rDef.getName());
		rmBo.setShowName(riBo.getShowName());
		rmBo.setDomainId(riBo.getDomainId());
		rmBo.setLifeState(riBo.getLifeState());
		rmBo.setDiscoverWay(riBo.getDiscoverWay());
		rmBo.setIp(getResourceIP(riBo));
		rmBo.setInstanceStatus(null);
		CategoryDef categoryDef = capacityService.getCategoryById(riBo.getCategoryId());
		String parentCategoryId = categoryDef != null ? null
				: categoryDef.getParentCategory() != null ? categoryDef.getParentCategory().getId() : null;

		rmBo.setHasTelSSHParams("NetworkDevice".equals(parentCategoryId));
		return rmBo;
	}

	private boolean matchResourceAvaAla(String instanceStatus, InstanceStateEnum stateEnum) {
		boolean flag = false;
		switch (instanceStatus) {
            case "down":
                switch (stateEnum) {
                    case CRITICAL:
                    case NORMAL_CRITICAL:
                        flag = true;
                }

                break;

            case "metric_error":
                switch (stateEnum) {
                    case SERIOUS:
                        flag = true;
                }

                break;

            case "metric_warn":
                switch (stateEnum) {
                    case WARN:
                        flag = true;
                }

                break;

            case "metric_recover":
                switch (stateEnum) {
                    case NORMAL:
                    case NORMAL_NOTHING:
                    case UNKNOWN_NOTHING:
                        flag = true;
                }

                break;
            case "available":
                switch (stateEnum) {
                    case SERIOUS:
                    case WARN:
                    case NORMAL:
                    case NORMAL_NOTHING:
                    case NORMAL_CRITICAL:
                    case NORMAL_UNKNOWN:
                        flag = true;
                }

                break;

            case "unavailability":
                switch (stateEnum) {
                    case CRITICAL:
                    case CRITICAL_NOTHING:
                        flag = true;
                }

                break;

            case "availability":
			switch (stateEnum) {
			case SERIOUS:
			case WARN:
			case NORMAL:
			case NORMAL_NOTHING:
			case NORMAL_CRITICAL:
			case NORMAL_UNKNOWN:
				flag = true;
			}

			break;

		case "notavailable":
			switch (stateEnum) {
			case CRITICAL:
			case CRITICAL_NOTHING:
				flag = true;
			}

			break;

		case "unknown":
			switch (stateEnum) {
			case UNKNOWN_NOTHING:
				flag = true;
			}

			break;

		case "critical":
			switch (stateEnum) {
			case CRITICAL:
			case NORMAL_CRITICAL:
				flag = true;
			}

			break;

		case "serious":
			switch (stateEnum) {
			case SERIOUS:
				flag = true;
			}

			break;

		case "warn":
			switch (stateEnum) {
			case WARN:
				flag = true;
			}

			break;

		case "normal":
			switch (stateEnum) {
			case NORMAL:
			case NORMAL_NOTHING:
			case UNKNOWN_NOTHING:
				flag = true;
			}

			break;

		case "metric_unkwon":
			switch (stateEnum) {
			case NORMAL_UNKNOWN:
			case UNKOWN:
				flag = true;
			}

			break;
		}

		return flag;
	}

	/***
	 * 页面上的查询条件过滤，仅限于IP/NAME
	 * @param riBo
	 * @param ipOrShowName
	 * @param domainId
	 * @param categoryId
	 * @param parentCategoryId
	 * @param lifeStateEnum
	 * @return
	 */
	private boolean matchFilterCondition(ResourceInstanceBo riBo, String ipOrShowName, Long domainId, String categoryId,
			String parentCategoryId, InstanceLifeStateEnum lifeStateEnum) {
		if ((null != domainId) || ((ipOrShowName != null) && (!"".equals(ipOrShowName)))) {
			if ((null != domainId) && (domainId.longValue() != riBo.getDomainId().longValue())) {
				return false;
			}

			if ((ipOrShowName != null) && (!"".equals(ipOrShowName))) {
				String showName = riBo.getShowName() == null ? "" : riBo.getShowName();
				String discoverIp = riBo.getDiscoverIP() == null ? "" : riBo.getDiscoverIP();

				showName = showName.toLowerCase();
				String ipOrShowNameTemp = ipOrShowName.toLowerCase();
				boolean isContainNameOrDiscoverIp = true;
				if ((!showName.contains(ipOrShowNameTemp)) && (!discoverIp.contains(ipOrShowNameTemp))) {
					isContainNameOrDiscoverIp = false;
				}

				String[] ips = riBo.src().getModulePropBykey("ip");
				boolean isContainModuleIp = false;
				if ((ips != null) && (ips.length > 0)) {
					for (String moduleIp : ips) {
						if (moduleIp != null) {

							moduleIp = moduleIp.toLowerCase();
							if (moduleIp.contains(ipOrShowNameTemp)) {
								isContainModuleIp = true;
								break;
							}
						}
					}
				}
				if ((!isContainNameOrDiscoverIp) && (!isContainModuleIp)) {
					return false;
				}
			}
		} else {
			if ((null != parentCategoryId) && (!"".equals(parentCategoryId))) {
				CategoryDef categoryDef = capacityService.getCategoryById(riBo.getCategoryId());
				if (categoryDef == null) {
					return false;
				}
				CategoryDef parentCategoryDef = categoryDef.getParentCategory();
				if (parentCategoryDef == null) {
					return false;
				}
				if (!parentCategoryId.equals(parentCategoryDef.getId())) {
					return false;
				}
			}
			if ((null != categoryId) && (!"".equals(categoryId)) && (!categoryId.equals(riBo.getCategoryId()))) {
				return false;
			}
		}

		if (lifeStateEnum != riBo.getLifeState()) {
			return false;
		}
		return true;
	}

	private boolean matchFilterConditionCustomGroup(ResourceInstanceBo riBo, String ipOrShowName, Long domainId,
			InstanceLifeStateEnum lifeStateEnum) {
		if (!lifeStateEnum.equals(riBo.getLifeState())) {
			return false;
		}
		if ((domainId != null) && (domainId.longValue() != riBo.getDomainId().longValue())) {
			return false;
		}

		if ((ipOrShowName != null) && (!"".equals(ipOrShowName))
				&& ((!ipOrShowName.equals(riBo.getDiscoverIP())) || (!ipOrShowName.equals(riBo.getShowName())))) {
			return false;
		}

		return true;
	}

	private void getHideCategory(CategoryDef cDef, List<String> hideCDefs, boolean isParentDisplay) {
		if ((!cDef.isDisplay()) || (!isParentDisplay)) {
			hideCDefs.add(cDef.getId());
			isParentDisplay = false;
		}
		CategoryDef[] childrenCDef = cDef.getChildCategorys();
		for (int i = 0; (childrenCDef != null) && (i < childrenCDef.length); i++) {
			getHideCategory(childrenCDef[i], hideCDefs, isParentDisplay);
		}
	}

	private ResourceQueryBo getRQB(ILoginUser user) {
		//if (this.hideCDefs.isEmpty()) {
        //
		//}

		//getHideCategory(capacityService.getRootCategory(), hideCDefs, true);
		ResourceQueryBo rqb = new ResourceQueryBo(user);
		rqb.setFilter(new Filter() {
			public boolean filter(ResourceInstanceBo riBo) {
				return true;
			}
		});
		rqb.setLifeState(InstanceLifeStateEnum.MONITORED.toString());
		List<String> catIds = new ArrayList<String>();
		catIds.add("SurveillanceCamera");
		rqb.setCategoryIds(catIds);
		return rqb;
	}

	/**
	 * 摄像头报表专用
	 *
	 * @param user
	 * @param startRow
	 * @param pageSize
	 * @param instanceStatus
	 * @param ipOrShowName
	 * @param domainId
	 * @param categoryId
	 * @param parentCategoryId
	 * @param resourceIds
	 * @param IsCustomResGroup
	 * @param sort
	 * @param order
	 * @return
	 */
	public List<CaremaMonitorBo> getCaremaBoReportList(ILoginUser user, long startRow, long pageSize,
			String instanceStatus, String ipOrShowName, Long domainId, String categoryId, String parentCategoryId,
			String resourceIds, String IsCustomResGroup) {
		List<CaremaMonitorBo> filteredMonitorList = new ArrayList();

		List<ResourceInstanceBo> risbListFiltered = new ArrayList();

		List<ResourceInstanceBo> risbListUnFilter = this.resourceApi.getResources(getRQB(user));

        //查询摄像头IP并过滤摄像头
        for (int i = 0; i < risbListUnFilter.size(); i++) {
            ResourceInstanceBo riBo = (ResourceInstanceBo) risbListUnFilter.get(i);
            MetricData devIP = metricDataService.getMetricInfoData(riBo.getId(), "devIP");
            String[] devIPData = devIP.getData();
            if(devIPData != null && devIPData.length > 0){
                riBo.setDiscoverIP(devIPData[0]);
            }
            //过滤摄像头
            String showName = riBo.getShowName();
            String discoverIP = riBo.getDiscoverIP();
            if(!StringUtil.isNull(ipOrShowName)){
                if((!StringUtil.isNull(showName) && showName.contains(ipOrShowName)) ||
                        (!StringUtil.isNull(discoverIP) && discoverIP.contains(ipOrShowName))){
                    risbListFiltered.add(riBo);
                }
            }else{
                risbListFiltered.add(riBo);
            }
        }

		/*for (int i = 0; i < risbListUnFilter.size(); i++) {
			ResourceInstanceBo riBo = (ResourceInstanceBo) risbListUnFilter.get(i);
			if (matchFilterCondition(riBo, ipOrShowName, domainId, categoryId, parentCategoryId,
					InstanceLifeStateEnum.MONITORED)) {
				risbListFiltered.add(riBo);
			}
		}*/

		List<String> instanceIdList = new ArrayList();
		if (null != resourceIds) {
			String[] instanceIds = resourceIds.split(",");
			instanceIdList = Arrays.asList(instanceIds);
		}

		List<Long> hasNotRightIds = new ArrayList();
		for (int i = 0; (!instanceIdList.isEmpty()) && (i < instanceIdList.size()); i++) {
			try {
				long instanceId = Long.valueOf((String) instanceIdList.get(i)).longValue();
				ResourceInstanceBo rb = new ResourceInstanceBo();
				rb.setId(Long.valueOf(instanceId));
				if ((!risbListFiltered.contains(rb)) && (("".equals(ipOrShowName)) || (ipOrShowName == null))) {
					ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
					if (ri != null) {
						rb = ri2riBo(ri);
						if (matchFilterConditionCustomGroup(rb, ipOrShowName, domainId,
								InstanceLifeStateEnum.MONITORED)) {
							risbListFiltered.add(rb);
							hasNotRightIds.add(Long.valueOf(instanceId));
						}
					}
				}
			} catch (InstancelibException e) {
				logger.error("getCaremaBoReportList----------", e);
			}
		}

		List<Long> filteredInstanceIds = new ArrayList();
		for (ResourceInstanceBo ribo : risbListFiltered) {
			try {
				if (("0".equals(IsCustomResGroup)) && (null == resourceIds)) {
					break;
				}
				if ((instanceIdList.isEmpty()) || ((!"".equals(ipOrShowName)) && (ipOrShowName != null))
						|| (instanceIdList.contains(String.valueOf(ribo.getId())))) {
					String gisx = null;
					String gisy = null;
					String groupId = null;
					try {
						ResourceInstance ri = resourceInstanceService.getResourceInstance(ribo.getId().longValue());
                        String[] metricS = {"gisX","gisY","groupID"};
                        List<MetricData> metricInfoDatas = metricDataService.getMetricInfoDatas(ri.getId(), metricS);
                        for(MetricData metricData : metricInfoDatas){
                            String metricId = metricData.getMetricId();
                            String[] dataInfo = metricData.getData();
                            String singleData = dataInfo[0];
                            if(singleData == null){
                                singleData = "";
                            }
                            if("gisX".equals(metricId)){
                                gisx = singleData;
                            }else if ("gisY".equals(metricId)){
                                gisy = singleData;
                            }else if("groupID".equals(metricId)){
                                groupId = singleData;
                            }
                        }
						/*gisx = ri.getDiscoverPropBykey("gisx")[0];
						gisy = ri.getDiscoverPropBykey("gisy")[0];
						groupId = ri.getDiscoverPropBykey("groupId")[0];*/
					} catch (InstancelibException e) {
						logger.error("查询摄像头属性失败----------", e);
						e.printStackTrace();
					}

					filteredInstanceIds.add(ribo.getId());

					CaremaMonitorBo monitorBo = riBo2RmBo(ribo);

					monitorBo.setHasRight(!hasNotRightIds.contains(monitorBo.getInstanceId()));

					monitorBo.setGisx(gisx);

					monitorBo.setGisy(gisy);

					monitorBo.setGroupId(groupId);

					filteredMonitorList.add(monitorBo);
				}
			} catch (InstancelibException e) {
				logger.error("查询摄像头属性失败", e);
			}
		}

		return filteredMonitorList;
	}

	/**
	 * cameraList：摄像头的集合，缓存在内存中。
	 *
	 * 视频诊断专用
	 */
	public CameraMonitorPageBo getMonitored(ILoginUser user, long startRow, long pageSize, String instanceStatus,
			String ipOrShowName, Long domainId, String categoryId, String parentCategoryId, String resourceIds,
			String IsCustomResGroup, String sort, String order, Map<String, List<String>> queryMap,
			Map<String, List<String>> statusMap) {

		CameraMonitorPageBo monitorPageBo = new CameraMonitorPageBo();


		if (null == cameraList) {
			cameraList = new ArrayList<CaremaMonitorBo>();
		} else {
			cameraList.clear();
		}

		// 离线数
		long offlineNumber = 0;
		// 故障数
		long abnormalNumber = 0;

		Map<String, String> dcsNodeGroupMap = new HashMap();
		try {
			NodeTable nodeTable = nodeService.getNodeTable();
			if (nodeTable != null) {
				List<NodeGroup> nodeGroups = nodeTable.getGroups();
				for (int i = 0; (nodeGroups != null) && (i < nodeGroups.size()); i++) {
					NodeGroup nodeGroup = (NodeGroup) nodeGroups.get(i);
					dcsNodeGroupMap.put(String.valueOf(nodeGroup.getId()), nodeGroup.getName());
				}
			}
		} catch (NodeException e1) {
			logger.error("get NodeTable error:", e1);
		}

		List<CaremaMonitorBo> filteredMonitorList = new ArrayList();

		List<ResourceInstanceBo> risbListFiltered = new ArrayList();

        ResourceQueryBo rqb = getRQB(user);
        List<ResourceInstanceBo> risbListUnFilter = this.resourceApi.getResources(rqb);
        //查询摄像头IP并过滤摄像头
        for (int i = 0; i < risbListUnFilter.size(); i++) {
            ResourceInstanceBo riBo = (ResourceInstanceBo) risbListUnFilter.get(i);
            MetricData devIP = metricDataService.getMetricInfoData(riBo.getId(), "devIP");
            if(devIP != null){
                String[] devIPData = devIP.getData();
                if(devIPData != null && devIPData.length > 0){
                    riBo.setDiscoverIP(devIPData[0]);
                }
            }
            //过滤摄像头
            String showName = riBo.getShowName();
            String discoverIP = riBo.getDiscoverIP();
            if(!StringUtil.isNull(ipOrShowName)){
                if((!StringUtil.isNull(showName) && showName.contains(ipOrShowName)) ||
                        (!StringUtil.isNull(discoverIP) && discoverIP.contains(ipOrShowName))){
                    risbListFiltered.add(riBo);
                }
            }else{
                risbListFiltered.add(riBo);
            }
        }

       /* for (int i = 0; i < risbListUnFilter.size(); i++) {
			ResourceInstanceBo riBo = (ResourceInstanceBo) risbListUnFilter.get(i);
			//过滤得到摄像头
			if(riBo.getCategoryId().equals("Cameras")){
				if (matchFilterCondition(riBo, ipOrShowName, domainId, categoryId, parentCategoryId,
						InstanceLifeStateEnum.MONITORED)) {
					risbListFiltered.add(riBo);
				}
			}
		}*/

		List<String> instanceIdList = new ArrayList();
		if (null != resourceIds) {
			String[] instanceIds = resourceIds.split(",");
			instanceIdList = Arrays.asList(instanceIds);
		}
		List<Long> hasNotRightIds = new ArrayList();
		for (int i = 0; (!instanceIdList.isEmpty()) && (i < instanceIdList.size()); i++) {
			try {
				long instanceId = Long.valueOf((String) instanceIdList.get(i)).longValue();
				ResourceInstanceBo rb = new ResourceInstanceBo();
				rb.setId(Long.valueOf(instanceId));
				if ((!risbListFiltered.contains(rb)) && (("".equals(ipOrShowName)) || (ipOrShowName == null))) {
					ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
					if (ri != null) {
						rb = ri2riBo(ri);
						if (matchFilterConditionCustomGroup(rb, ipOrShowName, domainId,
								InstanceLifeStateEnum.MONITORED)) {
							risbListFiltered.add(rb);
							hasNotRightIds.add(Long.valueOf(instanceId));
						}
					}
				}
			} catch (InstancelibException e) {
				logger.error("查询摄像头失败", e);
			}
		}

		if ((!Util.isEmpty(sort)) && (!Util.isEmpty(order))) {
			if(sort.equals("ipAddress")||sort.equals("sourceName"))
				risbListFiltered = ResourceSortableBySingleFieldApi.sort(risbListFiltered, sort, order);
		}
		List<Long> filteredInstanceIds = new ArrayList();
		for (ResourceInstanceBo ribo : risbListFiltered) {
			try {
				if (("0".equals(IsCustomResGroup)) && (null == resourceIds)) {
					break;
				}
				if ((instanceIdList.isEmpty()) || ((!"".equals(ipOrShowName)) && (ipOrShowName != null))
						|| (instanceIdList.contains(String.valueOf(ribo.getId())))) {
					String gisx = null;
					String gisy = null;
					String groupId = null;
					String monitorType=null;
					try {
						ResourceInstance ri = resourceInstanceService.getResourceInstance(ribo.getId().longValue());
						String[] metricS = {"gisX","gisY","groupID"};
                        List<MetricData> metricInfoDatas = metricDataService.getMetricInfoDatas(ri.getId(), metricS);
                        for(MetricData metricData : metricInfoDatas){
                            String metricId = metricData.getMetricId();
                            String[] dataInfo = metricData.getData();
                            String singleData = dataInfo[0];
                            if(singleData == null){
                                singleData = "";
                            }
                            if("gisX".equals(metricId)){
                                gisx = singleData;
                            }else if ("gisY".equals(metricId)){
                                gisy = singleData;
                            }else if("groupID".equals(metricId)){
                                groupId = singleData;
                            }
                        }
                        /*gisx = ri.getDiscoverPropBykey("gisX")[0];
						gisy = ri.getDiscoverPropBykey("gisY")[0];
						groupId = ri.getDiscoverPropBykey("groupId")[0];*/
                        // 查询摄像头类型
                        ModuleProp cameraType = modulePropService.getPropByInstanceAndKey(ri.getId(), "cameraType");
                        if(cameraType != null){
                            String[] values = cameraType.getValues();
                            if(values.length > 0){
                                monitorType = values[0] == null ? "--" : values[0];
                            }
                        }
						//monitorType=ri.getDiscoverPropBykey("cameraType")==null?"":ri.getDiscoverPropBykey("cameraType")[0];
					} catch (Exception e) {
						e.printStackTrace();
					}

					filteredInstanceIds.add(ribo.getId());

					CaremaMonitorBo monitorBo = riBo2RmBo(ribo);

					monitorBo.setHasRight(!hasNotRightIds.contains(monitorBo.getInstanceId()));

					monitorBo.setDcsGroupName((String) dcsNodeGroupMap.get(ribo.getDiscoverNode()));

					monitorBo.setGisx(gisx);

					monitorBo.setGisy(gisy);

					monitorBo.setGroupId(groupId);

					monitorBo.setMonitorType(monitorType);


					filteredMonitorList.add(monitorBo);
				}
			} catch (InstancelibException e) {
				logger.error("查询摄像头属性失败------", e);
			}
		}

		if (filteredInstanceIds.size() > 0) {
			List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(filteredInstanceIds);

			Map<Long, InstanceStateEnum> instanceStateDataMap = new HashMap();
			for (int i = 0; (instanceStateDataList != null) && (i < instanceStateDataList.size()); i++) {
				InstanceStateData isd = (InstanceStateData) instanceStateDataList.get(i);
				instanceStateDataMap.put(Long.valueOf(isd.getInstanceID()), isd.getState());
			}

			List<CaremaMonitorBo> tmpMonitorList = new ArrayList();
			for (int i = 0; i < filteredMonitorList.size(); i++) {
				CaremaMonitorBo monitorBo = (CaremaMonitorBo) filteredMonitorList.get(i);
				InstanceStateEnum isd = null;
				if (instanceStateDataMap.containsKey(monitorBo.getInstanceId())) {
					isd = (InstanceStateEnum) instanceStateDataMap.get(monitorBo.getInstanceId());
				}
				isd = (isd == null ? InstanceStateEnum.UNKNOWN_NOTHING : isd);
				monitorBo.setInstanceStatus(getInstanceStateColor(isd));
				if ((null == instanceStatus) || ("all".equals(instanceStatus))
						|| (matchResourceAvaAla(instanceStatus, isd))) {

					tmpMonitorList.add(monitorBo);

				}
			}
			filteredMonitorList = tmpMonitorList;
		}

		boolean checkCollectTime=false;
		//logger.info("filteredMonitorList: " + JSONObject.toJSONString(filteredMonitorList));

		//遍历某一个摄像头的old采集时间是否和接口提供的新的采集时间一致,如果不一致，就可以更新缓存
		logger.info("--------开始研制缓存是否需要更新------------");
		if(null!=metricQueryMap){
			logger.info("--------缓存不为空------------");
			for(Map.Entry<Long, CaremaMonitorBo>entry:metricQueryMap.entrySet()){
				long instansId=entry.getKey();
				CaremaMonitorBo c=entry.getValue();
				// 在线状态
				List<Map<String, Object>> availableList = resourceDetailInfoApi.getMetricByType(instansId,
						"AvailabilityMetric",true);
				//最近采集时间
				Map<String, String> collectMap = getMapFromListNew(availableList,"lastCollTime");
				String newCollectTime=collectMap.get("availability");
				logger.info("对于instansId为"+instansId+"的摄像头--------新的采集时间是------------"+newCollectTime);
				String oldCollectTime=c.getLastCollectTime();
				logger.info("对于instansId为"+instansId+"的摄像头--------缓存里的的采集时间是------------"+oldCollectTime);
				if(!oldCollectTime.equals(newCollectTime)){
					checkCollectTime=true;
                    break;
				}
				else{
					checkCollectTime=false;
				}
			}
		}
		logger.info("Will load the data?" + checkCollectTime);

		// 如果缓存中，metricQueryMap是null,或者lastCollectTime与任何一个新的lastCollectTime不一致,就先将在线状态，指标和诊断时间放入到map
        if (null == metricQueryMap||metricQueryMap.isEmpty()|| checkCollectTime || (metricQueryMap.size() != filteredMonitorList.size())) {
            logger.info("--------开始更新缓存------------");
            loadMetricToMap(filteredMonitorList);
            logger.info("--------结束更新缓存------------");
        }

		// 根据在线状态，诊断结果和诊断指标对结果集进行过滤
		// 如果在线状态，诊断结果和诊断指标有任何一个不为空，先查询各个指标的状态，然后再过滤
		if (!queryMap.isEmpty() || !statusMap.isEmpty()) {
			List<CaremaMonitorBo> newListUnFilter = new ArrayList<CaremaMonitorBo>();
			for (int j = 0; j < filteredMonitorList.size(); j++) {
				boolean flag = true;
				int num = 0;
				CaremaMonitorBo c = filteredMonitorList.get(j);
				long instanseId = c.getInstanceId();
				CaremaMonitorBo caremaMonitorBo;
				//判断是否为空 防止刚添加的设备在map中值导致空指针异常
				if(metricQueryMap.containsKey(instanseId)){
					caremaMonitorBo = metricQueryMap.get(instanseId);
				}else{
					loadMetricToMap(filteredMonitorList);
					caremaMonitorBo = metricQueryMap.get(instanseId);
				}
				Map<String, String> performMap = getMapFromObject(caremaMonitorBo);
				// 在线状态,从缓存中取
				String availability = metricQueryMap.get(instanseId).getAvailability();
				if (!statusMap.isEmpty()) {
					if (!statusMap.get("status").contains(availability)) {
						flag = false;
						continue;
					}
				}
				// 诊断结果和诊断指标,从缓存中取
				if (!queryMap.isEmpty()) {
					// 如果诊断指标是空，诊断结果不为空，只要有满足任何诊断结果的数据都显示出来
					if (queryMap.containsKey("all")) {
						if (queryMap.get("all").contains(caremaMonitorBo.getBrightness())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getPTZDegree())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getColourCast())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getKeepOut())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getLegibility())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getPtzSpeed())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getLostSignal())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getSightChange())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getSnowflakeDisturb())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getScreenFreezed())) {
							num++;
						}
						if (queryMap.get("all").contains(caremaMonitorBo.getStreakDisturb())) {
							num++;
						}

						if (num > 0) {
							if (caremaMonitorBo.getAvailability().equals("CRITICAL")) {
								offlineNumber++;
							}

							if (performMap.containsValue("WARN") || performMap.containsValue("SERIOUS") || performMap.containsValue("CRITICAL")) {
								abnormalNumber++;
							}

							newListUnFilter.add(caremaMonitorBo);
						}

					}
					// 如果诊断结果是空，诊断指标不为空，则默认显示所选中的诊断指标的异常
					// 如果诊断指标不为空，诊断结果不为空
					else {
						for (Map.Entry<String, List<String>> entry : queryMap.entrySet()) {
							String queryKey = entry.getKey();
							List<String> queryValueList = entry.getValue();
							if(queryValueList.size() == 4 || queryValueList.size() == 3){
							    break;  //诊断结果都是空默认显示所有的
                            }
                            if (queryValueList.size() ==2) {
							    if(caremaMonitorBo.getAvailability().equals("CRITICAL")){
                                    break;
                                }else{
                                    String performValue = performMap.get(queryKey);
                                    if (queryValueList.contains(performValue)) {
                                        flag = true;
                                        break;
                                    }else{
                                        flag = false;
                                    }
                                }
                            }
                            if (queryValueList.size() == 1) {
                                if(caremaMonitorBo.getAvailability().equals("CRITICAL")){
                                    flag = false;
                                    break;
                                }else{
                                    String performValue = performMap.get(queryKey);
                                    if (queryValueList.contains(performValue)) {
                                        flag = true;
                                    }else{
                                        flag = false;
                                        break;
                                    }
                                }
                            }
							/*String performValue = performMap.get(queryKey);
							if (!queryValueList.contains(performValue)) {
								flag = false;
								//break;
							}else{
                                if(queryValueList.size() == 1){
                                    continue;
                                }
							    break;
                            }*/
						}
					}
				}

				if (flag&&num==0) {
					if (caremaMonitorBo.getAvailability().equals("CRITICAL")) {
						offlineNumber++;
					}

					if (performMap.containsValue("WARN") || performMap.containsValue("SERIOUS") || performMap.containsValue("CRITICAL")) {
						abnormalNumber++;
					}

					newListUnFilter.add(caremaMonitorBo);
				}

			}
			filteredMonitorList = newListUnFilter;

		}
		// 在此处，得到故障数和离线数
		else {

			List<CaremaMonitorBo> newMonitorList = new ArrayList();
			for (int s = 0; s < filteredMonitorList.size(); s++) {
				CaremaMonitorBo c = filteredMonitorList.get(s);
				long instanseId = c.getInstanceId();
				CaremaMonitorBo caremaMonitorBo = metricQueryMap.get(instanseId);
				if(!c.getShowName().equals(caremaMonitorBo.getShowName())){
				    caremaMonitorBo.setShowName(c.getShowName());
                }
				newMonitorList.add(caremaMonitorBo);
			}

			filteredMonitorList = newMonitorList;

			newMonitorList = null;

			if (filteredMonitorList.size() == metricQueryMap.size()) {
				offlineNumber = offlineNum;
				abnormalNumber = abnormalNum;
			} else {
				for (int s = 0; s < filteredMonitorList.size(); s++) {
					CaremaMonitorBo c = filteredMonitorList.get(s);
					long instanseId = c.getInstanceId();
					CaremaMonitorBo caremaMonitorBo = metricQueryMap.get(instanseId);
					Map<String, String> performMap = getMapFromObject(caremaMonitorBo);
					// 计算离线数和异常数
					if (caremaMonitorBo.getAvailability().equals("CRITICAL")) {
						offlineNumber++;
					}

					if (performMap.containsValue("WARN") ||performMap.containsValue("SERIOUS") || performMap.containsValue("CRITICAL")) {
						abnormalNumber++;
					}
				}
			}

		}

		cameraList = filteredMonitorList;

		if ((!Util.isEmpty(sort)) && (!Util.isEmpty(order))) {
			if(!sort.equals("ipAddress")&&!sort.equals("sourceName")){
				cameraList=sort4Monitor(cameraList,sort, order);
			}
		}


		monitorPageBo.setTotalRecord(filteredMonitorList.size());

		int startIndex = (int) Math.min(startRow, filteredMonitorList.size());
		int endIndex = (int) Math.min(startRow + pageSize, filteredMonitorList.size());

		if ((!Util.isEmpty(sort)) && (!Util.isEmpty(order))) {
			if (filteredMonitorList.size() > 0) {
				filteredMonitorList = setMetricValue(filteredMonitorList);
			}

			filteredMonitorList = filteredMonitorList.subList(startIndex, endIndex);
		} else {
			filteredMonitorList = filteredMonitorList.subList(startIndex, endIndex);

			if (filteredMonitorList.size() > 0) {
				filteredMonitorList = setMetricValue(filteredMonitorList);
			}
		}

		monitorPageBo.setResourceMonitorBosExtends(filteredMonitorList);
		monitorPageBo.setStartRow(startRow);
		monitorPageBo.setRowCount(pageSize);
		monitorPageBo.setResourceMonitorBos(null);
		logger.info("离线数是------------"+offlineNumber);
		monitorPageBo.setOfflineNumber(offlineNumber);
		logger.info("异常数是------------"+abnormalNumber);
		monitorPageBo.setAbnormalNumber(abnormalNumber);
		monitorPageBo.setResourceCategoryBos(
				resourceCategoryApi.getResourceCategoryListByResources(risbListUnFilter, new ArrayList()));

		return monitorPageBo;
	}

	/**
	 * 将调用接口取得的数据封装到map
	 *
	 */
	public Map<String, String> getMapFromList(List<Map<String, Object>> queryList) {
		Map<String, String> map = new HashMap<String, String>();
		for (Map<String, Object> objectMap : queryList) {
			String key = objectMap.get("id").toString();
			String value = objectMap.get("status").toString();
			map.put(key, value);
		}
		return map;
	}


	 private  Map<String, String> getMapFromListNew(List<Map<String, Object>> queryList,String valueName) {
			Map<String, String> map = new HashMap<String, String>();
			for (Map<String, Object> objectMap : queryList) {
				String key = objectMap.get("id").toString();
				String value = objectMap.get(valueName).toString();
				map.put(key, value);
			}
			return map;
		}

	 //将查询结果放入map
	 private void loadMetricToMap(List<CaremaMonitorBo> filteredMonitorList){
			logger.info("---------------Start loading, total count: " + filteredMonitorList.size() + "-------------------");
			long startTime = System.currentTimeMillis();
		   	offlineNum=0;
			abnormalNum=0;
			metricQueryMap = new HashMap<Long, CaremaMonitorBo>();
			for (int i = 0; i < filteredMonitorList.size(); i++) {
				CaremaMonitorBo caremaMonitorBo = filteredMonitorList.get(i);
				long instanseId = caremaMonitorBo.getInstanceId();
				// 在线状态
				List<Map<String, Object>> availableList = resourceDetailInfoApi.getMetricByType(instanseId,
						"AvailabilityMetric",true);
				// 诊断结果和诊断指标
				List<Map<String, Object>> performList = resourceDetailInfoApi.getMetricByType(instanseId,
						"PerformanceMetric",true);
				// 诊断时间
				//List<Map<String, Object>> infomationList = resourceDetailInfoApi.getMetricByType(instanseId,
				//		"InformationMetric",true);
                MetricData collectTime = metricDataService.getMetricInfoData(instanseId, "collectTime");
                Map<String, String> newStatusMap = getMapFromList(availableList);
                //最近采集时间
				Map<String, String> collectMap = getMapFromListNew(availableList,"lastCollTime");
                String availability = newStatusMap.get("availability");
                if(availability == null){
                    availability = "SERIOUS";
                }
				//String availability = getMapFromListNew(availableList,"currentVal").get("availability").equals("1")?"NORMAL":"SERIOUS";
				caremaMonitorBo.setAvailability(availability);

				String lastCollTime=collectMap.get("availability");

				if (availability.equals("NORMAL")) {
                    // 告警提示
                    caremaMonitorBo.setAlarmTips("NORMAL");
                }else if("CRITICAL".equals(availability)){
                    caremaMonitorBo.setAlarmTips("CRITICAL");
                }else if("WARN".equals(availability)){
                    caremaMonitorBo.setAlarmTips("WARN");
				} else {
					// 告警提示
					caremaMonitorBo.setAlarmTips("SERIOUS");
				}
				Map<String, String> performMap = getMapFromList(performList);
				// 亮度
				caremaMonitorBo.setBrightness(performMap.get("brightness"));
				// 清晰度
				caremaMonitorBo.setLegibility(performMap.get("legibility"));
				// 画面冻结screenFreezed
				caremaMonitorBo.setScreenFreezed(performMap.get("screenFrozen"));
				// 画面偏色
				caremaMonitorBo.setColourCast(performMap.get("colourCast"));
				// 信号缺失
				caremaMonitorBo.setLostSignal(performMap.get("lostSignal"));
				// 场景变换
				caremaMonitorBo.setSightChange(performMap.get("sightChange"));
				// PTZ速度
				caremaMonitorBo.setPtzSpeed(performMap.get("PTZSpeed"));
				// 人为遮挡
				caremaMonitorBo.setKeepOut(performMap.get("keepOut"));
				// 条纹干扰
				caremaMonitorBo.setStreakDisturb(performMap.get("streakDisturb"));
				// 云台控制失效
				caremaMonitorBo.setPTZDegree(performMap.get("PTZDegree"));
				// 雪花干扰
				caremaMonitorBo.setSnowflakeDisturb(performMap.get("snowflakeDisturb"));

				caremaMonitorBo.setLastCollectTime(lastCollTime);

				// 告警提示
				if (caremaMonitorBo.getAlarmTips().equals("NORMAL")) {
					if (performMap.containsValue("SERIOUS")) {
                        caremaMonitorBo.setAlarmTips("SERIOUS");
                    }else if(performMap.containsValue("CRITICAL")){
                        caremaMonitorBo.setAlarmTips("CRITICAL");
                    }else if(performMap.containsValue("WARN")){
                        caremaMonitorBo.setAlarmTips("WARN");
					} else {
						caremaMonitorBo.setAlarmTips("NORMAL");
					}
				}
				if(collectTime != null && collectTime.getData() != null && collectTime.getData().length > 0){
                    String dignoseTime = "";
                    String s = collectTime.getData()[0];
                    if (s.length() > 19) {
                        dignoseTime = s.substring(0, 19);
                    }
                    caremaMonitorBo.setDignoseTime(dignoseTime);
                }

				if (caremaMonitorBo.getAvailability().equals("CRITICAL")) {
					offlineNum++;
				}

				if (performMap.containsValue("WARN") || performMap.containsValue("SERIOUS") || performMap.containsValue("CRITICAL")) {
					abnormalNum++;
				}
				metricQueryMap.put(instanseId, caremaMonitorBo);
			}
			long endTime = System.currentTimeMillis();
			logger.info("---------------End loading, time cost: " + (endTime - startTime) + "-------------------");
	 }

	 /**
	  * 将对象转化为Map
	  * @param caremaMonitorBo
	  * @return
	  */
	public Map<String, String> getMapFromObject(CaremaMonitorBo caremaMonitorBo) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("brightness", caremaMonitorBo.getBrightness());
		map.put("legibility", caremaMonitorBo.getLegibility());
		map.put("screenFrozen", caremaMonitorBo.getScreenFreezed());
		map.put("colourCast", caremaMonitorBo.getColourCast());
		map.put("lostSignal", caremaMonitorBo.getLostSignal());
		map.put("sightChange", caremaMonitorBo.getSightChange());
		map.put("PTZSpeed", caremaMonitorBo.getPtzSpeed());
		map.put("keepOut", caremaMonitorBo.getKeepOut());
		map.put("streakDisturb", caremaMonitorBo.getStreakDisturb());
		map.put("PTZDegree", caremaMonitorBo.getPTZDegree());
		map.put("snowflakeDisturb", caremaMonitorBo.getSnowflakeDisturb());
		return map;
	}



	/**
	 * 根据field以order排序
	 * @param resources
	 * @param field
	 * @param order
	 * @return
	 */
	public List<CaremaMonitorBo> sort4Monitor(List<CaremaMonitorBo> resources, String field, String order){
		try{
			 if (!(Util.isEmpty(order))) {
				 if ("ASC".equals(order.toUpperCase())) {
					 if(field.equals("availability")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
								     String value4Bo1=bo1.getAvailability();
								     String value4Bo2=bo2.getAvailability();
									 if ((value4Bo1 == null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2!= null)){
										 return -1;
									 }
									 if ((value4Bo1!= null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("brightness")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
								 String value4Bo1=bo1.getBrightness();
							     String value4Bo2=bo2.getBrightness();
									 if ((value4Bo1 == null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }

					 if(field.equals("legibility")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getLegibility();
								     String value4Bo2=bo2.getLegibility();
									 if ((value4Bo1 == null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("screenFrozen")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getScreenFreezed();
								     String value4Bo2=bo2.getScreenFreezed();
									 if ((value4Bo1 == null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("colourCast")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getColourCast();
								     String value4Bo2=bo2.getColourCast();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("lostSignal")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getLostSignal();
								     String value4Bo2=bo2.getLostSignal();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }

					 if(field.equals("sightChange")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getSightChange();
								     String value4Bo2=bo2.getSightChange();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("ptzSpeed")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getPtzSpeed();
								     String value4Bo2=bo2.getPtzSpeed();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }



					 if(field.equals("keepOut")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getKeepOut();
								     String value4Bo2=bo2.getKeepOut();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("streakDisturb")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getStreakDisturb();
								     String value4Bo2=bo2.getStreakDisturb();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("PTZDegree")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getPTZDegree();
								     String value4Bo2=bo2.getPTZDegree();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("snowflakeDisturb")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getSnowflakeDisturb();
								     String value4Bo2=bo2.getSnowflakeDisturb();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return -1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }

					 if(field.equals("dignoseTime")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getDignoseTime();
								     String value4Bo2=bo2.getDignoseTime();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return -1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return 1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if (value4Bo1.compareToIgnoreCase(value4Bo2) > 0) {
											 return 1;
										 }
										 if (value4Bo1.compareToIgnoreCase(value4Bo2) == 0) {
											 return 0;
										 }
										 if (value4Bo1.compareToIgnoreCase(value4Bo2) < 0) {
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }
				 }


				 else{


					 if(field.equals("availability")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
								     String value4Bo1=bo1.getAvailability();
								     String value4Bo2=bo2.getAvailability();
									 if ((value4Bo1 == null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2!= null)){
										 return 1;
									 }
									 if ((value4Bo1!= null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("brightness")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
								 String value4Bo1=bo1.getBrightness();
							     String value4Bo2=bo2.getBrightness();
									 if ((value4Bo1 == null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }

					 if(field.equals("legibility")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getLegibility();
								     String value4Bo2=bo2.getLegibility();
									 if ((value4Bo1 == null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("screenFrozen")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getScreenFreezed();
								     String value4Bo2=bo2.getScreenFreezed();
									 if ((value4Bo1 == null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("colourCast")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getColourCast();
								     String value4Bo2=bo2.getColourCast();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("lostSignal")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getLostSignal();
								     String value4Bo2=bo2.getLostSignal();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }

					 if(field.equals("sightChange")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getSightChange();
								     String value4Bo2=bo2.getSightChange();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("ptzSpeed")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getPtzSpeed();
								     String value4Bo2=bo2.getPtzSpeed();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }



					 if(field.equals("keepOut")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getKeepOut();
								     String value4Bo2=bo2.getKeepOut();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("streakDisturb")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getStreakDisturb();
								     String value4Bo2=bo2.getStreakDisturb();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("PTZDegree")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getPTZDegree();
								     String value4Bo2=bo2.getPTZDegree();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


					 if(field.equals("snowflakeDisturb")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getSnowflakeDisturb();
								     String value4Bo2=bo2.getSnowflakeDisturb();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if(value4Bo1.equals("NORMAL")&&value4Bo2.equals("SERIOUS")){
											 return 1;
										 }
										 if(value4Bo1.equals("SERIOUS")&&value4Bo2.equals("NORMAL")){
											 return -1;
										 }
									 }
								 return 0;
							 }
						 });
					 }

					 if(field.equals("dignoseTime")){
						 Collections.sort(resources, new Comparator<CaremaMonitorBo>(){
							 @Override
							 public int compare(CaremaMonitorBo bo1, CaremaMonitorBo bo2) {
									 String value4Bo1=bo1.getDignoseTime();
								     String value4Bo2=bo2.getDignoseTime();
									 if ((value4Bo1== null) && (value4Bo2 == null)){
										 return 0;
									 }
									 if ((value4Bo1 == null) && (value4Bo2 != null)){
										 return 1;
									 }
									 if ((value4Bo1 != null) && (value4Bo2 == null)){
										 return -1;
									 }
									 if (value4Bo1 != null&& (value4Bo2!= null)){
										 if (value4Bo1.compareToIgnoreCase(value4Bo2) > 0) {
											 return -1;
										 }
										 if (value4Bo1.compareToIgnoreCase(value4Bo2) == 0) {
											 return 0;
										 }
										 if (value4Bo1.compareToIgnoreCase(value4Bo2) < 0) {
											 return 1;
										 }
									 }
								 return 0;
							 }
						 });
					 }


				 }
			 }

		}catch(Exception e){
			logger.error("排序失败----",e);
		}

		 return resources;

	}

	public List<Map<String, String>> getDiscoverParamter(long instanceId) {
		List<Map<String, String>> props = new ArrayList();
		try {
			ResourceInstance instance = resourceInstanceService.getResourceInstance(instanceId);
			List<DiscoverProp> discoverProps = instance.getDiscoverProps();
			for (int i = 0; (discoverProps != null) && (i < discoverProps.size()); i++) {
				Map<String, String> prop = new HashMap();
				DiscoverProp discoverProp = (DiscoverProp) discoverProps.get(i);
				prop.put("key", discoverProp.getKey());

				if (SecureUtil.isPassswordKey(discoverProp.getKey())) {
					String decryptCom = SecureUtil.pwdDecrypt(discoverProp.getValues()[0]);
					prop.put("value", decryptCom);
				} else {
					prop.put("value", discoverProp.getValues()[0]);
				}
				props.add(prop);
			}
			Map<String, String> discoverNodeProp = new HashMap();
			discoverNodeProp.put("key", "nodeGroupId");
			discoverNodeProp.put("value", instance.getDiscoverNode());
			props.add(discoverNodeProp);
			Map<String, String> domainIdProp = new HashMap();
			domainIdProp.put("key", "domainId");
			domainIdProp.put("value", String.valueOf(instance.getDomainId()));
			props.add(domainIdProp);
		} catch (InstancelibException e) {
			logger.error("getDiscoverParamter", e);
		}
		return props;
	}

	public boolean saveLiablePerson(long[] instanceIds, String[] userIds) {
		for (int i = 0; i < instanceIds.length; i++) {
			if (clearLiablePersonCore(instanceIds[i])) {
				try {
					CustomProp prop = new CustomProp();
					prop.setInstanceId(instanceIds[i]);
					prop.setKey("liablePerson");
					prop.setValues(userIds);
					customPropService.addProp(prop);
				} catch (InstancelibException e) {
					logger.error(e.getMessage());
					return false;
				}
			}
		}

		return true;
	}

	public boolean clearLiablePerson(long[] instanceIds) {
		for (int i = 0; i < instanceIds.length; i++) {
			if (!clearLiablePersonCore(instanceIds[i])) {
				return false;
			}
		}
		return true;
	}

	private boolean clearLiablePersonCore(long instanceId) {
		CustomProp prop;

		try {
			prop = customPropService.getPropByInstanceAndKey(instanceId, "liablePerson");
		} catch (InstancelibException e) {
			logger.error(e.getMessage());
			return false;
		}
		if (prop != null) {
			try {
				customPropService.removePropByInstanceAndKey(instanceId, "liablePerson");
			} catch (InstancelibException e) {
				logger.error(e.getMessage());
				return false;
			}
		}

		return true;
	}

	@Override
	public long addReportTemplate(ReportTemplate reportTemplate, ILoginUser user) {
		long newTemplateId = -1L;

		newTemplateId = addPerformanceReportTemplate(reportTemplate, user);

		return newTemplateId;
	}

	/**
	 * 添加性能报表
	 * @param reportTemplate
	 * @param user
	 * @return
	 */
	private long addPerformanceReportTemplate(ReportTemplate reportTemplate, ILoginUser user) {
		long newTemplateId = -1L;

		ReportModelMain rmm = new ReportModelMain(user.getId() + "", this.fileClient);

		rmm.addTitleReport();

		long templateId = this.ReportTemplateSeq.next();

		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for (ReportTemplateDirectory reportTemplateDirectory : directoryList) {
			if (!(addPerformanceSubReportTemplate(reportTemplateDirectory, templateId, rmm))) {
				if (logger.isDebugEnabled()) {
					logger.debug("addPerformanceSubReportTemplate() error...");
				}
				return newTemplateId;
			}
		}

		long modelId = rmm.writeAndComplieJrxmlFile().longValue();

		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId().longValue());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if (this.iReportTemplateDao.insert(templatePo) != 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("IReportTemplateDao.insert(templatePo) error...");
			}
			return newTemplateId;
		}
		newTemplateId = templateId;

		if (reportTemplate.getReportTemplateStatus() == 0) {
			startJob(reportTemplate);
		}
		return newTemplateId;
	}


	private boolean addPerformanceSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory, long templateId,
			ReportModelMain rmm) {
		long templateDirectoryId = this.ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if (this.iReportTemplateDirectoryDao.insert(directoryPo) != 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("IReportTemplateDirectoryDao.insert(directoryPo) error...");
			}
			return false;
		}

		return (addPerformanceInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm));
	}

	private boolean addPerformanceInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,
			long templateDirectoryId, ReportModelMain rmm) {
		String resourceId = "";

		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory
				.getDirectoryInstanceList();
		for (ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList) {
			ResourceInstanceBo instance = this.resourceApi.getResource(Long.valueOf(directoryInstance.getInstanceId()));
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if ((instance.getShowName() == null) || (instance.getShowName().equals("")))
				directoryInstance.setInstanceName(instance.getName());
			else {
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(this.ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if (this.iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1) {
				if (logger.isDebugEnabled()) {
					logger.debug("IReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) error...");
				}
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		rmm.addTitleReport();
		List columnsList = new ArrayList();
		ColumnsTitle columnsTitle = new ColumnsTitle();

		List columnsDetailList = new ArrayList();
		ColumnsTitle columnsDetailTitle = new ColumnsTitle();
		columnsDetailList.add(new Columns(null, DETAULT_TABLE_COLUMNS_COUNT));

		for (int i = 0; i < 4; ++i) {
			Columns columns = new Columns(null, DETAULT_TABLE_COLUMNS_COUNT);
			columnsList.add(columns);
		}

		for (ReportTemplateDirectoryMetric directoryMetric : directoryMetricList) {
			ResourceMetricDef def = this.capacityService.getResourceMetricDef(resourceId,
					directoryMetric.getMetricId());
			if (def != null) {
				directoryMetric.setMetricName(def.getName() + "(" + def.getUnit() + ")");
				directoryMetric.setMetricType(def.getMetricType());
			}

			Columns columns = new Columns(null, DETAULT_TABLE_COLUMNS_COUNT);
			columns.setApart("3");
			columnsDetailList.add(columns);
			columnsList.add(columns);
		}

		columnsDetailList.add(new Columns(null, DETAULT_TABLE_COLUMNS_COUNT));
		columnsDetailTitle.setColumns(columnsDetailList);

		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);

		for (ReportTemplateDirectoryMetric directoryMetric : directoryMetricList) {
			rmm.addStackedBarReport(directoryInstanceList.size());

			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(this.ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if (this.iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1) {
				if (logger.isDebugEnabled()) {
					logger.debug("IReportTemplateDirectoryMetricDao.insert(directoryMetricPo) error...");
				}
				return false;
			}
		}

		if (reportTemplateDirectory.getReportTemplateDirectoryIsDetail() == 0) {
			rmm.addTitleReport();
			for (ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList) {
				rmm.addTableReport(columnsDetailTitle);
			}
			for (ReportTemplateDirectoryMetric directoryMetric : directoryMetricList) {
				rmm.addLineReport(directoryInstanceList.size());
			}
		}
		return true;
	}

	private void startJob(ReportTemplate reportTemplate) {
		try {
			this.cameraReportEngine.startEngine(reportTemplate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean updateReportTemplate(ReportTemplate reportTemplate, ILoginUser user) {
		boolean isSuccess = false;

		isSuccess = updatePerformanceReportTemplate(reportTemplate, user);

		return isSuccess;
	}

	/**
	 * 修改性能报表模板
	 * @param reportTemplate
	 * @param user
	 * @return
	 */
	private boolean updatePerformanceReportTemplate(ReportTemplate reportTemplate, ILoginUser user) {
		boolean isSuccess = true;

		ReportModelMain rmm = new ReportModelMain(user.getId() + "", this.fileClient);

		rmm.addTitleReport();

		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List directoryIdList = new ArrayList();
		directoryIdList = this.iReportTemplateDirectoryDao
				.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for (ReportTemplateDirectory reportTemplateDirectory : directoryList) {
			if (reportTemplateDirectory.getReportTemplateDirectoryId() > 0L) {
				directoryIdList.remove(Long.valueOf(reportTemplateDirectory.getReportTemplateDirectoryId()));
			}
			if (!(updatePerformanceSubReportTemplate(reportTemplateDirectory, reportTemplate.getReportTemplateId(),
					rmm))) {
				if (logger.isDebugEnabled()) {
					logger.debug("addPerformanceSubReportTemplate() error...");
				}
				isSuccess = false;
				return isSuccess;
			}
		}
		if (!(deleteTemplateDirectorys(directoryIdList))) {
			isSuccess = false;
			return isSuccess;
		}
		long modelId = rmm.writeAndComplieJrxmlFile().longValue();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if (this.iReportTemplateDao.update(templatePo) != 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("IReportTemplateDao.update(templatePo) error...");
			}
			isSuccess = false;
			return isSuccess;
		}
		if (reportTemplate.getReportTemplateStatus() == 0) {
			updateJob(reportTemplate);
		} else {
			try {
				this.cameraReportEngine.stopEngine(reportTemplate.getReportTemplateId());
			} catch (ClassNotFoundException e) {
				logger.error("修改性能报表模板 ClassNotFoundException", e);
			} catch (SchedulerException e) {
				logger.error("修改性能报表模板 SchedulerException", e);
			}
		}
		return isSuccess;
	}

	private boolean updatePerformanceSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory, long templateId,
			ReportModelMain rmm) {
		long templateDirectoryId = addOrUpdateDirectory(reportTemplateDirectory, templateId);

		if (templateDirectoryId <= 0L) {
			return false;
		}

		return (addPerformanceInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm));
	}

	private boolean deleteTemplateDirectorys(List<Long> directoryIdList) {
		Iterator i$;
		if (directoryIdList.size() > 0) {
			for (i$ = directoryIdList.iterator(); i$.hasNext();) {
				long deleteTemplateDirectoryId = ((Long) i$.next()).longValue();
				if (this.iReportTemplateDirectoryDao.del(deleteTemplateDirectoryId) <= 0) {
					if (logger.isDebugEnabled()) {
						logger.debug("iReportTemplateDirectoryDao.del(deleteTemplateDirectoryId) error...");
					}
					return false;
				}

				if (this.iReportTemplateDirectoryInstanceDao
						.deleteInstanceRelationByDirectoryId(deleteTemplateDirectoryId) <= 0) {
					if (logger.isDebugEnabled()) {
						logger.debug(
								"iReportTemplateDirectoryInstanceDao.deleteInstanceRelationByDirectoryId(templateDirectoryId) error...");
					}
					return false;
				}
				if (this.iReportTemplateDirectoryMetricDao
						.deleteMetricRelationByDirectoryId(deleteTemplateDirectoryId) <= 0) {
					if (logger.isDebugEnabled()) {
						logger.debug(
								"iReportTemplateDirectoryMetricDao.deleteMetricRelationByDirectoryId(templateDirectoryId) error...");
					}
					return false;
				}
			}
		}
		return true;
	}

	private long addOrUpdateDirectory(ReportTemplateDirectory reportTemplateDirectory, long templateId) {
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		long templateDirectoryId = -1L;
		if (directoryPo.getReportTemplateDirectoryId() <= 0L) {
			templateDirectoryId = this.ReportTemplateDirectorySeq.next();
			directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
			directoryPo.setReportTemplateId(templateId);
			if (this.iReportTemplateDirectoryDao.insert(directoryPo) != 1) {
				if (logger.isDebugEnabled()) {
					logger.debug("IReportTemplateDirectoryDao.insert(directoryPo) error...");
				}
				return templateDirectoryId;
			}
		} else {
			templateDirectoryId = directoryPo.getReportTemplateDirectoryId();
			if (this.iReportTemplateDirectoryDao.update(directoryPo) != 1) {
				if (logger.isDebugEnabled()) {
					logger.debug("IReportTemplateDirectoryDao.update(directoryPo) error...");
				}
				return templateDirectoryId;
			}

			if (this.iReportTemplateDirectoryMetricDao.deleteMetricRelationByDirectoryId(templateDirectoryId) <= 0) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"iReportTemplateDirectoryMetricDao.deleteMetricRelationByDirectoryId(templateDirectoryId) error...");
				}
				return templateDirectoryId;
			}
			if (this.iReportTemplateDirectoryInstanceDao
					.deleteInstanceRelationByDirectoryId(templateDirectoryId) <= 0) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"iReportTemplateDirectoryInstanceDao.deleteInstanceRelationByDirectoryId(templateDirectoryId) zero...");
				}
				return templateDirectoryId;
			}
		}
		return templateDirectoryId;
	}

	private void updateJob(ReportTemplate reportTemplate) {
		try {
			this.cameraReportEngine.updateEngine(reportTemplate);
		} catch (ClassNotFoundException e1) {
             logger.error("修改定时任务失败-ClassNotFoundException--------",e1);
		} catch (InstancelibException e1) {
			 logger.error("修改定时任务失败-InstancelibException--------",e1);
		} catch (SchedulerException e1) {
			logger.error("修改定时任务失败-SchedulerException--------",e1);
		}
	}

	@Override
	public CameraResourcePageBo getHaveMonitorCamera(ILoginUser user,
			long startRow, long pageSize, String instanceStatus,
			String iPorName, String domainId, String sort, String order) {
		CameraResourcePageBo crpBo = new CameraResourcePageBo();
		try {
            // DCSMap
            Map<String, String> dcsNodeGroupMap = new HashMap<String, String>();
            NodeTable nodeTable = nodeService.getNodeTable();
            if (nodeTable != null) {
                List<NodeGroup> nodeGroups = nodeTable.getGroups();
                for (int i = 0; nodeGroups != null && i < nodeGroups.size(); i++) {
                    NodeGroup nodeGroup = nodeGroups.get(i);
                    dcsNodeGroupMap.put(String.valueOf(nodeGroup.getId()), nodeGroup.getName());
                }
            }


			List<CameraResourceBo> filterMonitorList = new ArrayList<CameraResourceBo>();
			List<ResourceInstance> risbListFiltered = new ArrayList<ResourceInstance>();
            List<Long> hasNotRightIds = new ArrayList<Long>();
            List<ResourceInstance> risbListUnFilter = resourceInstanceService.getResourceInstanceByResourceId("EastWitCamera");
			for(int i = 0; i < risbListUnFilter.size(); i++) {
				ResourceInstance ri = risbListUnFilter.get(i);
				if(matchFilterCondition(ri, iPorName, domainId, InstanceLifeStateEnum.MONITORED)){
					risbListFiltered.add(ri);
				}
			}
			List<Long> filteredInstanceIds = new ArrayList<Long>();
			for (int i = 0; i < risbListFiltered.size(); i++) {
				ResourceInstance ri = risbListFiltered.get(i);
				filteredInstanceIds.add(ri.getId());
				CameraResourceBo monitorBo = new CameraResourceBo();
				monitorBo.setId(ri.getId());
				monitorBo.setDomainId(String.valueOf(ri.getDomainId()));
				monitorBo.setSourceName(ri.getShowName());
				monitorBo.setDcsGroupName(ri.getDiscoverNode());
				filterMonitorList.add(monitorBo);
			}
			if (filteredInstanceIds.size() > 0) {
				// 查询资源状态
				List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(filteredInstanceIds);
                // 组装资源状态
				Map<Long, InstanceStateEnum> instanceStateDataMap = new HashMap<Long, InstanceStateEnum>();
				for (int i = 0; instanceStateDataList != null && i < instanceStateDataList.size(); i++) {
					InstanceStateData isd = instanceStateDataList.get(i);
                    instanceStateDataMap.put(isd.getInstanceID(), isd.getState());
				}
				// 过滤资源状态
				List<CameraResourceBo> tmpMonitorList = new ArrayList<CameraResourceBo>();
				for (int i = 0; i < filterMonitorList.size(); i++) {
					CameraResourceBo monitorBo = filterMonitorList.get(i);
					InstanceStateEnum isd = null;
					if (instanceStateDataMap.containsKey(monitorBo.getId())) {
						isd = instanceStateDataMap.get(monitorBo.getId());
					}
					isd = isd == null ? InstanceStateEnum.UNKNOWN_NOTHING : isd;
					monitorBo.setInstanceStatus(getInstanceStateColor(isd));
					if (null != instanceStatus && !"all".equals(instanceStatus)) {
						if (!matchResourceAvaAla(instanceStatus, isd)) {
							continue;
						}
					}
                    tmpMonitorList.add(monitorBo);
				}
				filterMonitorList = tmpMonitorList;
			}
			// 排序功能
			if(!Util.isEmpty(sort) && (!Util.isEmpty(order))) {
				this.sort(filterMonitorList, sort, order);
			}
			crpBo.setTotalRecord(filterMonitorList.size());
			int startIndex = (int) Math.min(startRow, filterMonitorList.size());
			int endIndex = (int) Math.min(startRow + pageSize, filterMonitorList.size());
			filterMonitorList = filterMonitorList.subList(startIndex, endIndex);
			//获取显示摄像头详情
            if(filterMonitorList != null && filterMonitorList.size() > 0){
                for(CameraResourceBo monitorBo : filterMonitorList){
                    long domainId1 = Long.parseLong(monitorBo.getDomainId());
                    Domain domain = domainApi.get(domainId1);
                    if(domain == null){
                        monitorBo.setDomainName("");
                    }else{
                        monitorBo.setDomainName(domain.getName());
                    }
                    Long id = monitorBo.getId();
                    String[] metrics = {"address","devIP","devUser","devPwd","devPort","groupName","name","platForm"};
                    List<MetricData> metricInfoDatas = metricDataService.getMetricInfoDatas(id, metrics);
                    for(MetricData info : metricInfoDatas){
                        String metricId = info.getMetricId();
                        String[] data = info.getData();
                        String dataInfo = "--";
                        if(data != null && data.length > 0){
                            dataInfo = data[0];
                        }
                        switch (metricId){
                            case "address" :
                                monitorBo.setAddress(dataInfo);
                                break;
                            case "devIP" :
                                monitorBo.setDevIP(dataInfo);
                                monitorBo.setIpAddress(monitorBo.getDevIP());
                                break;
                            case "devUser" :
                                monitorBo.setDevUser(dataInfo);
                                break;
                            case "devPwd" :
                                monitorBo.setDevPwd(dataInfo);
                                break;
                            case "devPort" :
                                monitorBo.setDevPort(dataInfo);
                                break;
                            case "groupName" :
                                monitorBo.setGroupName(dataInfo);
                                break;
                            case "name" :
                                monitorBo.setName(dataInfo);
                                break;
                            case "platForm" :
                                monitorBo.setPlatForm(dataInfo);
                                break;
                        }
                        if(StringUtil.isNull(monitorBo.getAddress())){
                            monitorBo.setAddress("--");
                        }
                        if(StringUtil.isNull(monitorBo.getDevIP())){
                            monitorBo.setDevIP("--");
                        }
                        if(StringUtil.isNull(monitorBo.getIpAddress())){
                            monitorBo.setIpAddress("--");
                        }
                        if(StringUtil.isNull(monitorBo.getDevUser())){
                            monitorBo.setDevUser("--");
                        }
                        if(StringUtil.isNull(monitorBo.getDevPwd())){
                            monitorBo.setDevPwd("--");
                        }
                        if(StringUtil.isNull(monitorBo.getDevPort())){
                            monitorBo.setDevPort("--");
                        }
                        if(StringUtil.isNull(monitorBo.getGroupName())){
                            monitorBo.setGroupName("--");
                        }
                        if(StringUtil.isNull(monitorBo.getName())){
                            monitorBo.setName("--");
                        }
                        if(StringUtil.isNull(monitorBo.getPlatForm())){
                            monitorBo.setPlatForm("--");
                        }
                    }
                    // 是否没有权限
                    monitorBo.setHasRight(!hasNotRightIds.contains(id));
                    // dcsNode名称
                    monitorBo.setDcsGroupName(dcsNodeGroupMap.get(monitorBo.getDcsGroupName()));
                    // 在线状态
                    List<Map<String, Object>> availableList = resourceDetailInfoApi.getMetricByType(id,
                            "AvailabilityMetric",true);
                    Map<String, String> collectMap = getMapFromListNew(availableList,"status");
                    String status=collectMap.get("availability");
                    monitorBo.setInstanceState(status);
                    // 查询摄像头类型
                    ModuleProp cameraType = modulePropService.getPropByInstanceAndKey(id, "cameraType");
                    if(cameraType != null){
                        String[] values = cameraType.getValues();
                        if(values.length > 0){
                            String va = values[0] == null ? "--" : values[0];
                            monitorBo.setCameraType(va);
                        }
                    }
                    // 查询责任人信息
                    CustomProp prop = customPropService.getPropByInstanceAndKey(id, "liablePerson");
                    if(prop != null) {
                        String[] accountIds = prop.getValues();
                        if(accountIds.length > 0) {
                            User tuser = userApi.get(Long.parseLong(accountIds[0]));
                            if(tuser != null) {
                                monitorBo.setLiablePerson(tuser.getName());
                            }
                        }
                    }
                }
            }

			crpBo.setResourceMonitors(filterMonitorList);
		} catch (InstancelibException e) {
			logger.error("查询资源实例错误" ,e);
		} catch (Exception e) {
            logger.error("获取node信息错误" ,e);
        }
        return crpBo;
	}

	@Override
	public CameraResourcePageBo getNotMonitorCamera(ILoginUser user,
			long startRow, long pageSize, String iPorName, String domainId,
			String sort, String order) {
		CameraResourcePageBo crpBo = new CameraResourcePageBo();
		try {
			List<CameraResourceBo> filterMonitorList = new ArrayList<CameraResourceBo>();
			List<ResourceInstance> risbListFiltered = new ArrayList<ResourceInstance>();
			List<ResourceInstance> risbListUnFilter = resourceInstanceService.getResourceInstanceByResourceId("EastWitCamera");
			for(int i = 0; i < risbListUnFilter.size(); i++) {
				ResourceInstance ri = risbListUnFilter.get(i);
				if(matchFilterCondition(ri, iPorName, domainId, InstanceLifeStateEnum.NOT_MONITORED)){
					risbListFiltered.add(ri);
				}
			}
			List<Long> filteredInstanceIds = new ArrayList<Long>();
			for (int i = 0; i < risbListFiltered.size(); i++) {
				ResourceInstance ri = risbListFiltered.get(i);
				filteredInstanceIds.add(ri.getId());
				CameraResourceBo monitorBo = new CameraResourceBo();
				monitorBo.setId(ri.getId());
				monitorBo.setDomainId(String.valueOf(ri.getDomainId()));
				monitorBo.setSourceName(ri.getShowName());
				//monitorBo.setDomainName(null == domainApi.get(ri.getDomainId()) ? "" : domainApi.get(ri.getDomainId()).getName());
//				monitorBo.setAddress(getInformationMetricValue(ri.getId(), "address"));
//				monitorBo.setDevIP(getInformationMetricValue(ri.getId(), "devIP"));
//				monitorBo.setGroupName(getInformationMetricValue(ri.getId(), "groupName"));
//				monitorBo.setName(getInformationMetricValue(ri.getId(), "name"));
//				monitorBo.setPlatForm(getInformationMetricValue(ri.getId(), "platForm"));
				
				//未监控资源获取不到指标值，因此展示监控平台的显示名称
				String paltformId = ri.getCustomPropBykey("platformId")[0];
				//ResourceInstance pri = resourceInstanceService.getResourceInstance(Long.parseLong(paltformId));
				monitorBo.setPlatForm(paltformId);
				
				filterMonitorList.add(monitorBo);
			}
			// 排序功能
			if(!Util.isEmpty(sort) && (!Util.isEmpty(order))) {
				sort(filterMonitorList, sort, order);
			}
			crpBo.setTotalRecord(filterMonitorList.size());
			int startIndex = (int) Math.min(startRow, filterMonitorList.size());
			int endIndex = (int) Math.min(startRow + pageSize, filterMonitorList.size());
			filterMonitorList = filterMonitorList.subList(startIndex, endIndex);
			//获取平台显示名称
            for(CameraResourceBo cr : filterMonitorList){
                if(cr.getPlatForm() != null){
                    ResourceInstance pri = resourceInstanceService.getResourceInstance(Long.parseLong(cr.getPlatForm()));
                    cr.setPlatForm(pri.getShowName());
                }
            }

			crpBo.setResourceMonitors(filterMonitorList);
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		return crpBo;
	}
	
	private boolean matchFilterCondition(ResourceInstance ri, String iPorName,
			String domainId, InstanceLifeStateEnum lifeStateEnum) {
		if (null != domainId || (iPorName != null && !"".equals(iPorName))) {
			if (null != domainId) {
				if (Long.parseLong(domainId) != ri.getDomainId()) {
					return false;
				}
			}
			if (iPorName != null && !"".equals(iPorName)) {
				String showname = ri.getShowName() == null ? "" : ri.getShowName();
				showname = showname.toLowerCase();
				String ipnameTemp = iPorName.toLowerCase();
				boolean isContainName = showname.contains(ipnameTemp);
				boolean isContainDevIP = false;
				MetricData devIP = metricDataService.getMetricInfoData(ri.getId(), "devIP");
				if (devIP != null && devIP.getData() != null
						&& devIP.getData().length > 0) {
					for (String tmp : devIP.getData()) {
						if (tmp == null) {
							continue;
						}
						if (tmp.toLowerCase().contains(ipnameTemp)) {
							isContainDevIP = true;
							break;
						}
					}
				}
				if (!isContainName && !isContainDevIP) {
					return false;
				}
			}
		}
		if (lifeStateEnum != ri.getLifeState()) {
			return false;
		}
		return true;
	}

	private String getInformationMetricValue(long instanceId, String metricId) {
		MetricData md = metricDataService.getMetricInfoData(instanceId, metricId);
		if(md == null) {
			return "--";
		}
		if(md.getData() == null || !(md.getData().length > 0)) {
			return "--";
		}
		return md.getData()[0];
	}

	private List<CameraResourceBo> sort(List<CameraResourceBo> resources,
			final String field, String order) {
		if (!Util.isEmpty(order)) {
			if ("ASC".equals(order.toUpperCase())) {
				Collections.sort(resources, new Comparator<CameraResourceBo>() {
					@Override
					public int compare(CameraResourceBo bo1, CameraResourceBo bo2) {
						if ("devIP".equals(field)) {
							if (bo1.getDevIP() == null && bo2.getDevIP() == null) {
								return 0;
							} else if (bo1.getDevIP() == null && bo2.getDevIP() != null) {
								return -1;
							} else if (bo1.getDevIP() != null && bo2.getDevIP() == null) {
								return 1;
							} else {
								if (bo1.getDevIP().compareToIgnoreCase(bo2.getDevIP()) == 0) {
									return 0;
								} else if (bo1.getDevIP().compareToIgnoreCase(bo2.getDevIP()) > 0) {
									return 1;
								} else {
									return -1;
								}
							}
						}
						if ("sourceName".equals(field)) {
							if (bo1.getSourceName() == null && bo2.getSourceName() == null) {
								return 0;
							} else if (bo1.getSourceName() == null && bo2.getSourceName() != null) {
								return -1;
							} else if (bo1.getSourceName() != null && bo2.getSourceName() == null) {
								return 1;
							} else {
								if (bo1.getSourceName().compareToIgnoreCase(bo2.getSourceName()) == 0) {
									return 0;
								} else if (bo1.getSourceName().compareToIgnoreCase(bo2.getSourceName()) > 0) {
									return 1;
								} else {
									return -1;
								}
							}
						}
						return 0;
					}
				});
			} else if ("DESC".equals(order.toUpperCase())) {
				Collections.sort(resources, new Comparator<CameraResourceBo>() {
					@Override
					public int compare(CameraResourceBo bo1, CameraResourceBo bo2) {
						if ("devIP".equals(field)) {
							if (bo1.getDevIP() == null && bo2.getDevIP() == null) {
								return 0;
							} else if (bo1.getDevIP() == null && bo2.getDevIP() != null) {
								return 1;
							} else if (bo1.getDevIP() != null && bo2.getDevIP() == null) {
								return -1;
							} else {
								if (bo1.getDevIP().compareToIgnoreCase(bo2.getDevIP()) == 0) {
									return 0;
								} else if (bo1.getDevIP().compareToIgnoreCase(bo2.getDevIP()) > 0) {
									return -1;
								} else {
									return 1;
								}
							}
						}
						if ("sourceName".equals(field)) {
							if (bo1.getSourceName() == null && bo2.getSourceName() == null) {
								return 0;
							} else if (bo1.getSourceName() == null && bo2.getSourceName() != null) {
								return 1;
							} else if (bo1.getSourceName() != null && bo2.getSourceName() == null) {
								return -1;
							} else {
								if (bo1.getSourceName().compareToIgnoreCase(bo2.getSourceName()) == 0) {
									return 0;
								} else if (bo1.getSourceName().compareToIgnoreCase(bo2.getSourceName()) > 0) {
									return -1;
								} else {
									return 1;
								}
							}
						}
						return 0;
					}
				});
			}
		}
		return resources;
	}
}
