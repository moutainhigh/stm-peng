package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IReAccountInstanceApi;
import com.mainsteam.stm.portal.resource.api.IResourceMonitorApi;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.portal.resource.bo.ResourceMonitorBo;
import com.mainsteam.stm.portal.resource.bo.ResourceMonitorPageBo;
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
import com.mainsteam.stm.util.SecureUtil;
import com.mainsteam.stm.util.Util;

/**
 * <li>????????????: ResourceMonitorAction.java</li>
 * <li>??? ???: ????????????????????????????????????</li>
 * <li>????????????: ????????????(C)2019-2020</li>
 * <li>????????????: ...</li>
 * <li>????????????: ...</li>
 * <li>????????????: ...</li>
 * 
 * @version ms.stm
 * @since 2019???7???29???
 * @author xhf
 */
public class ResourceMonitorImpl implements IResourceMonitorApi {
	private static Logger logger = Logger.getLogger(ResourceMonitorImpl.class);
	@Resource(name = "resourceInstanceService")
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private CapacityService capacityService;

	@Resource
	private ProfileService profileService;
	
	@Resource
	private ModulePropService modulePropService;

	@Resource
	private InstanceStateService instanceStateService;

	@Resource
	private MetricDataService metricDataService;

	@Resource
	private MetricStateService metricStateService;

	@Resource
	private ISearchApi searchApi;

	@Resource(name = "stm_system_resourceApi")
	private IResourceApi resourceApi;

	@Resource
	private ICustomResourceGroupApi customResourceGroupApi;

	@Resource
	private IReAccountInstanceApi resourceReAccountInstanceApi;

	@Resource
	private IResourceSortableBySingleFieldApi ResourceSortableBySingleFieldApi;

	@Resource
	private ResourceCategoryApi resourceCategoryApi;

	@Resource
	private NodeService nodeService;

	@Resource
	private BizMainApi bizMainApi;

	@Resource
	private CustomPropService customPropService;

	@Resource(name = "scheduleManager")
	private ScheduleManager scheduleManager;

	private final static String SEARCH_NAV = "????????????-????????????";

	private final static String ISCUSTOMGROUP_YES = "0";

	private List<String> hideCDefs = new ArrayList<String>();

	private static InstanceStateEnum getInstStateEnum(String instStateEnumString) {
		InstanceStateEnum ise = null;
		if (null == instStateEnumString) {
			return ise;
		} else {
			switch (instStateEnumString) {
			case "all":
				break;
			case "down":
				ise = InstanceStateEnum.CRITICAL;
				break;
			case "metric_error":
				ise = InstanceStateEnum.SERIOUS;
				break;
			case "metric_warn":
				ise = InstanceStateEnum.WARN;
				break;
			case "metric_recover":
				ise = InstanceStateEnum.NORMAL;
				break;
			case "metric_unkwon":
				ise = InstanceStateEnum.UNKOWN;
				break;
			}
		}

		return ise;
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
		// case UNKNOWN_NOTHING:
		// ise = "res_unknown_nothing";
		// break;
		// case UNKOWN:
		// ise = "res_unkown";
		// break;
		default:
			ise = "res_normal_nothing";
			break;
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
				break;
			}
		}
		return ise;
	}

	/**
	 * 2:??????License???????????? 1:???????????? 0:????????????
	 */
	@Override
	public int openMonitor(long resourceMonitorId) {
		int result = 0;
		try {
			profileService.enableMonitor(resourceMonitorId);
			List<Long> id = new ArrayList<Long>();
			id.add(resourceMonitorId);
			bizMainApi.instanceMonitorChangeBiz(id);
		} catch (Exception e) {
			logger.error("openMonitor", e);
			result = 0;
		}
		result = 1;
		return result;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param instanceId
	 */
	private void saveSearchRel(long instanceId) {
		ResourceBizRel rbr = new ResourceBizRel(instanceId, instanceId, SEARCH_NAV);
		searchApi.saveSearchResource(rbr);
	}

	/**
	 * 2:??????License???????????? 1:???????????? 0:????????????
	 */
	@Override
	public int batchOpenMonitor(String[] ids) {
		int result = 0;
		if (ids != null && ids.length > 0) {
			try {
				List<Long> list = new ArrayList<Long>();
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

	/**
	 * ???????????????????????????
	 * 
	 * @param instanceId
	 */
	private void delMonitorResource(long instanceId) {
		ResourceBizRel model = new ResourceBizRel();
		model.setBizId(instanceId);
		model.setResourceId(instanceId);
		searchApi.delCancelMonitorResource(model);
	}

	@Override
	public int closeMonitor(long resourceMonitorId) throws Exception {
		profileService.cancleMonitor(resourceMonitorId);
		List<Long> id = new ArrayList<Long>();
		id.add(resourceMonitorId);
		bizMainApi.instanceMonitorChangeBiz(id);
		return 0;
	}

	@Override
	public int batchCloseMonitor(long[] resourceMonitorIds) throws Exception {
		if (resourceMonitorIds != null && resourceMonitorIds.length > 0) {
			List<Long> cancelList = new ArrayList<Long>();
			for (long item : resourceMonitorIds) {
				cancelList.add(item);
				delMonitorResource(item);
			}
			profileService.cancleMonitor(cancelList);
			bizMainApi.instanceMonitorChangeBiz(cancelList);
		}
		return 0;
	}

	@Override
	public int batchDelResource(long[] resourceMonitorIds) throws Exception {
		if (resourceMonitorIds != null && resourceMonitorIds.length > 0) {
			List<Long> resourceList = new ArrayList<Long>();
			for (long item : resourceMonitorIds) {
				resourceList.add(item);
			}
			// ????????????
			resourceInstanceService.removeResourceInstances(resourceList);
		}
		return 0;
	}

	private List<ResourceMonitorBo> setMetricValue(List<ResourceMonitorBo> resourceMonitorBoList) {
		// cpurate,memerate????????????
		String[] metrics = { MetricIdConsts.METRIC_CPU_RATE, MetricIdConsts.METRIC_MEME_RATE };
		long[] instanceIdArray = new long[resourceMonitorBoList.size()];
		List<Long> instanceIdList = new ArrayList<Long>();
		// ??????????????????????????????????????????
		for (int i = 0; i < resourceMonitorBoList.size(); i++) {
			ResourceMonitorBo rmBo = resourceMonitorBoList.get(i);
			instanceIdArray[i] = rmBo.getInstanceId();
			instanceIdList.add(rmBo.getInstanceId());
			// ?????????CPU????????????
			rmBo.setCpuStatus(getMetricStateColor(null));
			rmBo.setCpuAvailability("N/A");
			rmBo.setMemoryStatus(getMetricStateColor(null));
			rmBo.setMemoryAvailability("N/A");
		}
		// ????????????????????????
		Map<Long, Map<String, MetricStateEnum>> iMStateMap = new HashMap<Long, Map<String, MetricStateEnum>>();
		List<MetricStateData> msdList = metricStateService.findMetricState(instanceIdList, Arrays.asList(metrics));
		System.out.println("malachi msdList.size() =========== " + msdList.size());

		for (int i = 0; msdList != null && i < msdList.size(); i++) {
			MetricStateData msd = msdList.get(i);
			Long instanceId = msd.getInstanceID();
			Map<String, MetricStateEnum> mStateMap = null;
			if (iMStateMap.containsKey(instanceId)) {
				mStateMap = iMStateMap.get(instanceId);
			} else {
				mStateMap = new HashMap<String, MetricStateEnum>();
				iMStateMap.put(instanceId, mStateMap);
			}
			mStateMap.put(msd.getMetricID(), msd.getState());
		}
		// ????????????????????????
		MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
		mrdq.setInstanceID(instanceIdArray);
		mrdq.setMetricID(metrics);
		Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(mrdq, 1, 10000);
		List<Map<String, ?>> metricMapList = page.getDatas();
		// ??????????????????????????????
		Map<String, Map<String, ?>> reLoadMetricMap = new HashMap<String, Map<String, ?>>();
		for (int i = 0; i < metricMapList.size(); i++) {
			Map<String, ?> metricMap = metricMapList.get(i);
			if (metricMap.get("instanceid") != null) {
				String instanceId = metricMap.get("instanceid").toString();
				reLoadMetricMap.put(instanceId, metricMap);
			}
		}
		// ??????????????????????????????????????????
		for (int i = 0; i < resourceMonitorBoList.size(); i++) {
			ResourceMonitorBo rmBo = resourceMonitorBoList.get(i);
			Long instanceId = rmBo.getInstanceId();
			// ?????????????????????????????????
			if (reLoadMetricMap.containsKey(instanceId.toString())) {
				boolean isMonitor_CPU = false, isMonitor_MEM = false;
				// ??????cpu????????????????????????CPU??????????????????
				try {
//					ProfileMetric cpuPMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId,MetricIdConsts.METRIC_CPU_RATE);
					ProfileMetric cpuPMetric = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId,MetricIdConsts.METRIC_CPU_RATE);
					if (cpuPMetric != null && cpuPMetric.isMonitor()) {
						isMonitor_CPU = true;
						rmBo.setCpuIsAlarm(cpuPMetric.isAlarm());
					}
//					ProfileMetric memPMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId,MetricIdConsts.METRIC_MEME_RATE);
					ProfileMetric memPMetric = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId,MetricIdConsts.METRIC_MEME_RATE);
					if (memPMetric != null && memPMetric.isMonitor()) {
						isMonitor_MEM = true;
						rmBo.setMemoryIsAlarm(memPMetric.isAlarm());
					}
				} catch (ProfilelibException e) {
					logger.error("setMetricValue", e);
				}
				Map<String, ?> metricMap = reLoadMetricMap.get(instanceId.toString());
				// CPU?????????
				// malachi ??????isMonitor_CPU???????????????rmDef
				if (isMonitor_CPU && metricMap.containsKey(MetricIdConsts.METRIC_CPU_RATE)) {
					if (metricMap.get(MetricIdConsts.METRIC_CPU_RATE) != null) {
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(rmBo.getResourceId(),
								MetricIdConsts.METRIC_CPU_RATE);
						if (rmDef != null) {
							rmBo.setCpuAvailability(
									metricMap.get(MetricIdConsts.METRIC_CPU_RATE).toString() + rmDef.getUnit());
						} else {
							rmBo.setCpuAvailability(null);
							logger.debug("rmDef is null");
						}

					} else {
						rmBo.setCpuAvailability("--");
					}
				}
				// ???????????????
				if (isMonitor_MEM && metricMap.containsKey(MetricIdConsts.METRIC_MEME_RATE)) {
					if (metricMap.get(MetricIdConsts.METRIC_MEME_RATE) != null) {
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(rmBo.getResourceId(),
								MetricIdConsts.METRIC_MEME_RATE);
						if (rmDef != null) {
							rmBo.setMemoryAvailability(
									metricMap.get(MetricIdConsts.METRIC_MEME_RATE).toString() + rmDef.getUnit());
						} else {
							rmBo.setMemoryAvailability(null);
						}

					} else {
						rmBo.setMemoryAvailability("--");
					}
				}
				// CPU????????????
				if (isMonitor_CPU && iMStateMap.containsKey(instanceId)) {
					if (iMStateMap.get(instanceId).containsKey(MetricIdConsts.METRIC_CPU_RATE)) {
						rmBo.setCpuStatus(
								getMetricStateColor(iMStateMap.get(instanceId).get(MetricIdConsts.METRIC_CPU_RATE)));
					}
				}
				// ??????????????????
				if (isMonitor_MEM && iMStateMap.containsKey(instanceId)) {
					if (iMStateMap.get(instanceId).containsKey(MetricIdConsts.METRIC_MEME_RATE)) {
						rmBo.setMemoryStatus(
								getMetricStateColor(iMStateMap.get(instanceId).get(MetricIdConsts.METRIC_MEME_RATE)));
					}
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
			for (int i = 0; childCategoryDefs != null && i < childCategoryDefs.length; i++) {
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

	/**
	 * ????????????IP??????
	 * 
	 * @param ribo
	 * @return
	 * @throws InstancelibException
	 */
	private String getResourceIP(ResourceInstanceBo ribo) throws InstancelibException {
		String ip = "";
		if (null == ribo.getDiscoverIP() || "".equals(ribo.getDiscoverIP())) {
			ResourceInstance ri = resourceInstanceService.getResourceInstance(ribo.getId());
			if ("GENEURL".equals(ri.getResourceId())) {
				String[] ips = ri.getDiscoverPropBykey("urlSite");
				ip = ips == null ? "" : ips[0];
			} else {
				String[] ips = ri.getDiscoverPropBykey("host");
				ips = ips == null ? ri.getModulePropBykey(CapacityConst.IP_ADDRESS) : ips;
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
		riBo.setDomainId(ri.getDomainId());
		riBo.setId(ri.getId());
		riBo.setLifeState(ri.getLifeState());
		riBo.setName(ri.getName());
		riBo.setParentId(ri.getParentId());
		riBo.setResourceId(ri.getResourceId());
		riBo.setShowName(ri.getShowName());
		return riBo;
	}

	private ResourceMonitorBo riBo2RmBo(ResourceInstanceBo riBo) throws InstancelibException {
		ResourceMonitorBo rmBo = new ResourceMonitorBo();
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
		String parentCategoryId = categoryDef != null
				? categoryDef.getParentCategory() != null ? categoryDef.getParentCategory().getId() : null : null;
		rmBo.setHasTelSSHParams(CapacityConst.NETWORK_DEVICE.equals(parentCategoryId));
		return rmBo;
	}

	private boolean matchResourceAvaAla(String instanceStatus, InstanceStateEnum stateEnum) {
		boolean flag = false;
		switch (instanceStatus) {
		case "availability": {
			switch (stateEnum) {
			case NORMAL:
			case NORMAL_NOTHING:
			case NORMAL_CRITICAL:
			case NORMAL_UNKNOWN:
			case SERIOUS:
			case WARN:
				flag = true;
				break;
			}
			break;
		}
		case "unavailability": {
			switch (stateEnum) {
			case CRITICAL:
			case CRITICAL_NOTHING:
				flag = true;
				break;
			}
			break;
		}
		case "unknown": {
			switch (stateEnum) {
			case UNKNOWN_NOTHING:
				flag = true;
				break;
			}
			break;
		}
		case "down": {
			switch (stateEnum) {
			case CRITICAL:
			case NORMAL_CRITICAL:
				flag = true;
				break;
			}
			break;
		}
		case "metric_error": {
			switch (stateEnum) {
			case SERIOUS:
				flag = true;
				break;
			}
			break;
		}
		case "metric_warn": {
			switch (stateEnum) {
			case WARN:
				flag = true;
				break;
			}
			break;
		}
		case "metric_recover": {
			switch (stateEnum) {
			case NORMAL:
			case NORMAL_NOTHING:
			case UNKNOWN_NOTHING:
				flag = true;
				break;
			}
			break;
		}
		case "metric_unkwon": {
			switch (stateEnum) {
			case UNKOWN:
			case NORMAL_UNKNOWN:
				flag = true;
				break;
			}
			break;
		}
		}
		return flag;
	}

	private boolean matchFilterCondition(ResourceInstanceBo riBo, String ipOrShowName, Long domainId, String categoryId,
			String parentCategoryId, InstanceLifeStateEnum lifeStateEnum) {
		// ?????? ?????????????????????
		if (null != domainId || (ipOrShowName != null && !"".equals(ipOrShowName))) {
			// ?????????????????????
			if (null != domainId) {
				if (domainId.longValue() != riBo.getDomainId().longValue()) {
					return false;
				}
			}
			// ????????????????????????
			if (ipOrShowName != null && !"".equals(ipOrShowName)) {
				String showName = riBo.getShowName() == null ? "" : riBo.getShowName();
				String discoverIp = riBo.getDiscoverIP() == null ? "" : riBo.getDiscoverIP();
				// ??????????????????
				showName = showName.toLowerCase();
				String ipOrShowNameTemp = ipOrShowName.toLowerCase();
				boolean isContainNameOrDiscoverIp = true;
				if (!showName.contains(ipOrShowNameTemp) && !discoverIp.contains(ipOrShowNameTemp)) {
					isContainNameOrDiscoverIp = false;
				}
				// ??????????????????IP?????????????????????
				String[] ips = riBo.src().getModulePropBykey(MetricIdConsts.METRIC_IP);
				boolean isContainModuleIp = false;
				if (ips != null && ips.length > 0) {
					for (String moduleIp : ips) {
						// ip??????????????????
						if (moduleIp == null) {
							continue;
						}
						moduleIp = moduleIp.toLowerCase();
						if (moduleIp.contains(ipOrShowNameTemp)) {
							isContainModuleIp = true;
							break;
						}
					}
				}
				if (!isContainNameOrDiscoverIp && !isContainModuleIp) {
					return false;
				}
			}
		} else {
			// ???????????????ip????????? ??? ??? ??????????????????
			if (null != parentCategoryId && !"".equals(parentCategoryId)) {
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
			if (null != categoryId && !"".equals(categoryId)) {
				if (!categoryId.equals(riBo.getCategoryId())) {
					return false;
				}
			}
		}
		// ??????????????????
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
		if (domainId != null) {
			if (domainId.longValue() != (long) riBo.getDomainId()) {
				return false;
			}
		}
		if (ipOrShowName != null && !"".equals(ipOrShowName)) {
			if (!ipOrShowName.equals(riBo.getDiscoverIP()) || !ipOrShowName.equals(riBo.getShowName())) {
				return false;
			}
		}
		return true;
	}

	private void getHideCategory(CategoryDef cDef, List<String> hideCDefs, boolean isParentDisplay) {
		if (!cDef.isDisplay() || !isParentDisplay) {
			hideCDefs.add(cDef.getId());
			isParentDisplay = false;
		}
		CategoryDef[] childrenCDef = cDef.getChildCategorys();
		for (int i = 0; childrenCDef != null && i < childrenCDef.length; i++) {
			getHideCategory(childrenCDef[i], hideCDefs, isParentDisplay);
		}
	}

	private ResourceQueryBo getRQB(ILoginUser user) {
		if (hideCDefs.isEmpty())
			getHideCategory(capacityService.getRootCategory(), hideCDefs, true);
		ResourceQueryBo rqb = new ResourceQueryBo(user);
		rqb.setFilter(new Filter() {
			@Override
			public boolean filter(ResourceInstanceBo riBo) {
				return hideCDefs.isEmpty() ? true : !hideCDefs.contains(riBo.getCategoryId());
			}
		});
		return rqb;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param user
	 *            ??????????????????
	 * @param startRow
	 *            ???????????????
	 * @param pageSize
	 *            ??????????????????
	 * @param instanceStatus
	 *            ??????????????????
	 * @param ipOrShowName
	 *            IP???????????????
	 * @param domainId
	 *            ???
	 * @param categoryId
	 *            ??????
	 * @param parentCategoryId
	 *            ?????????
	 * @param resourceIds
	 *            ????????????ID???????????????,????????????
	 * @param IsCustomResGroup
	 *            ????????????????????????
	 * @return
	 * @throws InstancelibException
	 */
	public ResourceMonitorPageBo getMonitored(ILoginUser user, long startRow, long pageSize, String instanceStatus,
			String ipOrShowName, Long domainId, String categoryId, String parentCategoryId, String resourceIds,
			String IsCustomResGroup, String sort, String order) {
		ResourceMonitorPageBo monitorPageBo = new ResourceMonitorPageBo();
		// DCSMap
		Map<String, String> dcsNodeGroupMap = new HashMap<String, String>();
		try {
			NodeTable nodeTable = nodeService.getNodeTable();
			if (nodeTable != null) {
				List<NodeGroup> nodeGroups = nodeTable.getGroups();
				for (int i = 0; nodeGroups != null && i < nodeGroups.size(); i++) {
					NodeGroup nodeGroup = nodeGroups.get(i);
					dcsNodeGroupMap.put(String.valueOf(nodeGroup.getId()), nodeGroup.getName());
				}
			}
		} catch (NodeException e1) {
			logger.error("get NodeTable error:", e1);
		}

		List<ResourceMonitorBo> filteredMonitorList = new ArrayList<ResourceMonitorBo>();
		// ?????????????????????
		List<ResourceInstanceBo> risbListFiltered = new ArrayList<ResourceInstanceBo>();
		// ???????????????????????????????????????????????????categoryId
		List<ResourceInstanceBo> risbListUnFilter = resourceApi.getResources(getRQB(user));
		for (int i = 0; i < risbListUnFilter.size(); i++) {
			ResourceInstanceBo riBo = risbListUnFilter.get(i);
			if (matchFilterCondition(riBo, ipOrShowName, domainId, categoryId, parentCategoryId,
					InstanceLifeStateEnum.MONITORED)) {
				risbListFiltered.add(riBo);
			}
		}
		// ???????????????????????????????????????(????????????????????????????????????)
		List<String> instanceIdList = new ArrayList<String>();
		if (null != resourceIds) {
			String[] instanceIds = resourceIds.split(",");
			instanceIdList = Arrays.asList(instanceIds);
		}
		List<Long> hasNotRightIds = new ArrayList<Long>();
		for (int i = 0; !instanceIdList.isEmpty() && i < instanceIdList.size(); i++) {
			try {
				long instanceId = Long.valueOf(instanceIdList.get(i));
				ResourceInstanceBo rb = new ResourceInstanceBo();
				rb.setId(instanceId);
				if (!risbListFiltered.contains(rb) && ("".equals(ipOrShowName) || ipOrShowName == null)) {
					ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
					if (ri != null) {
						rb = ri2riBo(ri);
						if (matchFilterConditionCustomGroup(rb, ipOrShowName, domainId,
								InstanceLifeStateEnum.MONITORED)) {
							risbListFiltered.add(rb);
							hasNotRightIds.add(instanceId);
						}
					}
				}
			} catch (InstancelibException e) {
				logger.error("getMonitored No. 572", e);
			}
		}
		// ?????????????????????
		if ((!Util.isEmpty(sort)) && (!Util.isEmpty(order))) {
			risbListFiltered = ResourceSortableBySingleFieldApi.sort(risbListFiltered, sort, order);
		}
		List<Long> filteredInstanceIds = new ArrayList<Long>();
		for (ResourceInstanceBo ribo : risbListFiltered) {
			try {
				// ????????????????????????
				if (ISCUSTOMGROUP_YES.equals(IsCustomResGroup) && null == resourceIds) {
					break;
				}
				if (!instanceIdList.isEmpty() && ("".equals(ipOrShowName) || ipOrShowName == null)) {
					if (!instanceIdList.contains(String.valueOf(ribo.getId()))) {
						continue;
					}
				}
				filteredInstanceIds.add(ribo.getId());
				
				ResourceMonitorBo monitorBo = riBo2RmBo(ribo);
				
				//??????Remote
				if(resourceInstanceService.getResourceInstance(ribo.getId()).getCategoryId().equals("RemotePings")){
					//???IP
					String[] sourceIp = modulePropService.getPropByInstanceAndKey(ribo.getId(), "sourceIp").getValues();
					//??????IP
					String[] distIP = modulePropService.getPropByInstanceAndKey(ribo.getId(), "distIP").getValues();
					monitorBo.setSourceIp(sourceIp[0]);
					monitorBo.setDistIP(distIP[0]);
				}
				
				// ??????????????????
				monitorBo.setHasRight(!hasNotRightIds.contains(monitorBo.getInstanceId()));
				// dcsNode??????
				monitorBo.setDcsGroupName(dcsNodeGroupMap.get(ribo.getDiscoverNode()));

				filteredMonitorList.add(monitorBo);
			} catch (InstancelibException e) {
				logger.error("getMonitored No. 597", e);
			}
		}
		// ??????????????????
		if (filteredInstanceIds.size() > 0) {
			// ??????????????????
			List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(filteredInstanceIds);
			// ??????????????????
			Map<Long, InstanceStateEnum> instanceStateDataMap = new HashMap<Long, InstanceStateEnum>();
			for (int i = 0; instanceStateDataList != null && i < instanceStateDataList.size(); i++) {
				InstanceStateData isd = instanceStateDataList.get(i);
				instanceStateDataMap.put(isd.getInstanceID(), isd.getState());
			}
			// ??????????????????
			List<ResourceMonitorBo> tmpMonitorList = new ArrayList<ResourceMonitorBo>();
			for (int i = 0; i < filteredMonitorList.size(); i++) {
				ResourceMonitorBo monitorBo = filteredMonitorList.get(i);
				InstanceStateEnum isd = null;
				if (instanceStateDataMap.containsKey(monitorBo.getInstanceId())) {
					isd = instanceStateDataMap.get(monitorBo.getInstanceId());
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
			filteredMonitorList = tmpMonitorList;
		}
		// ????????????
		monitorPageBo.setTotalRecord(filteredMonitorList.size());
		// ????????????
		int startIndex = (int) Math.min(startRow, filteredMonitorList.size());
		int endIndex = (int) Math.min(startRow + pageSize, filteredMonitorList.size());
		// ??????CPU???????????????????????????
		if ((!Util.isEmpty(sort)) && (!Util.isEmpty(order))) {
			// ??????????????????
			if (filteredMonitorList.size() > 0) {
				filteredMonitorList = setMetricValue(filteredMonitorList);
			}
			// CPU???????????????????????????
			filteredMonitorList = this.cpuSortable(filteredMonitorList, sort, order);
			// ??????
			filteredMonitorList = filteredMonitorList.subList(startIndex, endIndex);
		} else {
			// ??????
			filteredMonitorList = filteredMonitorList.subList(startIndex, endIndex);
			// ??????????????????
			if (filteredMonitorList.size() > 0) {
				filteredMonitorList = setMetricValue(filteredMonitorList);
			}
		}

		monitorPageBo.setResourceMonitorBosExtends(filteredMonitorList);
		monitorPageBo.setStartRow(startRow);
		monitorPageBo.setRowCount(pageSize);
		monitorPageBo.setResourceMonitorBos(null);
		// ??????????????????????????????
		monitorPageBo.setResourceCategoryBos(
				resourceCategoryApi.getResourceCategoryListByResources(risbListUnFilter, new ArrayList<String>()));
		return monitorPageBo;
	}
	
	@Override
	public ResourceMonitorPageBo getNewMonitored(ILoginUser user,
			String instanceStatus, String ipOrShowName, Long domainId,
			String categoryId, String parentCategoryId, String resourceIds,
			String IsCustomResGroup, String sort, String order) {
		ResourceMonitorPageBo monitorPageBo = new ResourceMonitorPageBo();
		// DCSMap
		Map<String, String> dcsNodeGroupMap = new HashMap<String, String>();
		try {
			NodeTable nodeTable = nodeService.getNodeTable();
			if (nodeTable != null) {
				List<NodeGroup> nodeGroups = nodeTable.getGroups();
				for (int i = 0; nodeGroups != null && i < nodeGroups.size(); i++) {
					NodeGroup nodeGroup = nodeGroups.get(i);
					dcsNodeGroupMap.put(String.valueOf(nodeGroup.getId()), nodeGroup.getName());
				}
			}
		} catch (NodeException e1) {
			logger.error("get NodeTable error:", e1);
		}

		List<ResourceMonitorBo> filteredMonitorList = new ArrayList<ResourceMonitorBo>();
		// ?????????????????????
		List<ResourceInstanceBo> risbListFiltered = new ArrayList<ResourceInstanceBo>();
		// ???????????????????????????????????????????????????categoryId
		List<ResourceInstanceBo> risbListUnFilter = resourceApi.getResources(getRQB(user));
		for (int i = 0; i < risbListUnFilter.size(); i++) {
			ResourceInstanceBo riBo = risbListUnFilter.get(i);
			if (matchFilterCondition(riBo, ipOrShowName, domainId, categoryId, parentCategoryId,
					InstanceLifeStateEnum.MONITORED)) {
				risbListFiltered.add(riBo);
			}
		}
		// ???????????????????????????????????????(????????????????????????????????????)
		List<String> instanceIdList = new ArrayList<String>();
		if (null != resourceIds) {
			String[] instanceIds = resourceIds.split(",");
			instanceIdList = Arrays.asList(instanceIds);
		}
		List<Long> hasNotRightIds = new ArrayList<Long>();
		for (int i = 0; !instanceIdList.isEmpty() && i < instanceIdList.size(); i++) {
			try {
				long instanceId = Long.valueOf(instanceIdList.get(i));
				ResourceInstanceBo rb = new ResourceInstanceBo();
				rb.setId(instanceId);
				if (!risbListFiltered.contains(rb) && ("".equals(ipOrShowName) || ipOrShowName == null)) {
					ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
					if (ri != null) {
						rb = ri2riBo(ri);
						if (matchFilterConditionCustomGroup(rb, ipOrShowName, domainId,
								InstanceLifeStateEnum.MONITORED)) {
							risbListFiltered.add(rb);
							hasNotRightIds.add(instanceId);
						}
					}
				}
			} catch (InstancelibException e) {
				logger.error("getNewMonitored No. 572", e);
			}
		}
		// ?????????????????????
		if ((!Util.isEmpty(sort)) && (!Util.isEmpty(order))) {
			risbListFiltered = ResourceSortableBySingleFieldApi.sort(risbListFiltered, sort, order);
		}
		List<Long> filteredInstanceIds = new ArrayList<Long>();
		for (ResourceInstanceBo ribo : risbListFiltered) {
			try {
				// ????????????????????????
				if (ISCUSTOMGROUP_YES.equals(IsCustomResGroup) && null == resourceIds) {
					break;
				}
				if (!instanceIdList.isEmpty() && ("".equals(ipOrShowName) || ipOrShowName == null)) {
					if (!instanceIdList.contains(String.valueOf(ribo.getId()))) {
						continue;
					}
				}
				filteredInstanceIds.add(ribo.getId());

				ResourceMonitorBo monitorBo = riBo2RmBo(ribo);
				// ??????????????????
				monitorBo.setHasRight(!hasNotRightIds.contains(monitorBo.getInstanceId()));
				// dcsNode??????
				monitorBo.setDcsGroupName(dcsNodeGroupMap.get(ribo.getDiscoverNode()));

				filteredMonitorList.add(monitorBo);
			} catch (InstancelibException e) {
				logger.error("getNewMonitored No. 597", e);
			}
		}
		// ??????????????????
		if (filteredInstanceIds.size() > 0) {
			// ??????????????????
			List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(filteredInstanceIds);
			// ??????????????????
			Map<Long, InstanceStateEnum> instanceStateDataMap = new HashMap<Long, InstanceStateEnum>();
			for (int i = 0; instanceStateDataList != null && i < instanceStateDataList.size(); i++) {
				InstanceStateData isd = instanceStateDataList.get(i);
				instanceStateDataMap.put(isd.getInstanceID(), isd.getState());
			}
			// ??????????????????
			List<ResourceMonitorBo> tmpMonitorList = new ArrayList<ResourceMonitorBo>();
			for (int i = 0; i < filteredMonitorList.size(); i++) {
				ResourceMonitorBo monitorBo = filteredMonitorList.get(i);
				InstanceStateEnum isd = null;
				if (instanceStateDataMap.containsKey(monitorBo.getInstanceId())) {
					isd = instanceStateDataMap.get(monitorBo.getInstanceId());
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
			filteredMonitorList = tmpMonitorList;
		}

		monitorPageBo.setResourceMonitorBosExtends(filteredMonitorList);
		return monitorPageBo;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param user
	 *            ??????????????????
	 * @param startRow
	 *            ???????????????
	 * @param pageSize
	 *            ??????????????????
	 * @param ipOrShowName
	 *            IP???????????????
	 * @param domainId
	 *            ???
	 * @param categoryId
	 *            ??????
	 * @param parentCategoryId
	 *            ?????????
	 * @param resourceIds
	 *            ????????????ID???????????????,????????????
	 * @param IsCustomResGroup
	 *            ????????????????????????
	 * @return
	 * @throws InstancelibException
	 */
	public ResourceMonitorPageBo getUnMonitored(ILoginUser user, long startRow, long pageSize, String ipOrShowName,
			Long domainId, String categoryId, String parentCategoryId, String resourceIds, String IsCustomResGroup,
			String sort, String order) {
		ResourceMonitorPageBo monitorPageBo = new ResourceMonitorPageBo();
		List<ResourceMonitorBo> filteredMonitorList = new ArrayList<ResourceMonitorBo>();
		// ?????????????????????
		List<ResourceInstanceBo> risbListFiltered = new ArrayList<ResourceInstanceBo>();
		// ???????????????????????????????????????????????????categoryId
		List<ResourceInstanceBo> risbListUnFilter = resourceApi.getResources(getRQB(user));
		for (int i = 0; i < risbListUnFilter.size(); i++) {
			ResourceInstanceBo riBo = risbListUnFilter.get(i);
			if (matchFilterCondition(riBo, ipOrShowName, domainId, categoryId, parentCategoryId,
					InstanceLifeStateEnum.NOT_MONITORED)) {
				risbListFiltered.add(riBo);
			}
		}
		// ???????????????????????????????????????(????????????????????????????????????)
		List<String> instanceIdList = new ArrayList<String>();
		if (null != resourceIds) {
			String[] instanceIds = resourceIds.split(",");
			instanceIdList = Arrays.asList(instanceIds);
		}
		List<Long> hasNotRightIds = new ArrayList<Long>();
		for (int i = 0; !instanceIdList.isEmpty() && i < instanceIdList.size(); i++) {
			try {
				long instanceId = Long.valueOf(instanceIdList.get(i));
				ResourceInstanceBo rb = new ResourceInstanceBo();
				rb.setId(instanceId);
				if (!risbListFiltered.contains(rb)) {
					ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
					if (ri != null) {
						rb = ri2riBo(ri);
						// ??????????????????
						if (matchFilterConditionCustomGroup(rb, ipOrShowName, domainId,
								InstanceLifeStateEnum.NOT_MONITORED)) {
							risbListFiltered.add(rb);
							hasNotRightIds.add(instanceId);
						}
					}
				}
			} catch (InstancelibException e) {
				logger.error("getUnMonitored No. 837:", e);
			}
		}
		// ?????????????????????
		if ((!Util.isEmpty(sort)) && (!Util.isEmpty(order))) {
			risbListFiltered = ResourceSortableBySingleFieldApi.sort(risbListFiltered, sort, order);
		}
		for (ResourceInstanceBo ribo : risbListFiltered) {
			try {
				// ????????????????????????
				if (ISCUSTOMGROUP_YES.equals(IsCustomResGroup) && null == resourceIds) {
					break;
				}
				if (!instanceIdList.isEmpty()) {
					if (!instanceIdList.contains(String.valueOf(ribo.getId()))) {
						continue;
					}
				}
				ResourceMonitorBo monitorBo = riBo2RmBo(ribo);
				// ??????????????????
				monitorBo.setHasRight(!hasNotRightIds.contains(monitorBo.getInstanceId()));
				filteredMonitorList.add(monitorBo);
			} catch (InstancelibException e) {
				logger.error("getUnMonitored No. 861:", e);
			}
		}
		monitorPageBo.setTotalRecord(filteredMonitorList.size());
		int startIndex = (int) Math.min(startRow, filteredMonitorList.size());
		int endIndex = (int) Math.min(startRow + pageSize, filteredMonitorList.size());
		filteredMonitorList = filteredMonitorList.subList(startIndex, endIndex);

		monitorPageBo.setResourceMonitorBosExtends(filteredMonitorList);
		monitorPageBo.setStartRow(startRow);
		monitorPageBo.setRowCount(pageSize);
		monitorPageBo.setResourceMonitorBos(null);
		// ??????????????????????????????
		monitorPageBo.setResourceCategoryBos(
				resourceCategoryApi.getResourceCategoryListByResources(risbListUnFilter, new ArrayList<String>()));

		return monitorPageBo;
	}

	/**
	 * CPU???????????????????????????
	 * 
	 * @param resources
	 *            ?????????????????????
	 * @param field
	 *            ????????????
	 * @param order
	 *            ????????????
	 * @return
	 */
	private List<ResourceMonitorBo> cpuSortable(List<ResourceMonitorBo> resources, final String field, String order) {
		if ("ASC".equals(order.toUpperCase())) {
			Collections.sort(resources, new Comparator<ResourceMonitorBo>() {
				@Override
				public int compare(ResourceMonitorBo o1, ResourceMonitorBo o2) {
					if ("cpuAvailability".equals(field)) {
						String cpuOne = o1.getCpuAvailability();
						if (cpuOne != null && (cpuOne.equals("N/A") || cpuOne.equals("--"))) {
							cpuOne = null;
						}
						String NewCpuOne = null;
						if (!Util.isEmpty(cpuOne)) {
							NewCpuOne = cpuOne.substring(0, cpuOne.length() - 1);
						}
						String cpuTwo = o2.getCpuAvailability();
						if (cpuTwo != null && (cpuTwo.equals("N/A") || cpuTwo.equals("--"))) {
							cpuTwo = null;
						}
						String NewCpuTwo = null;
						if (!Util.isEmpty(cpuTwo)) {
							NewCpuTwo = cpuTwo.substring(0, cpuTwo.length() - 1);
						}
						if (cpuOne == null && cpuTwo == null) {
							return 0;
						} else if (cpuOne == null && cpuTwo != null) {
							return -1;
						} else if (cpuOne != null && cpuTwo == null) {
							return 1;
						} else {
							if (Float.parseFloat(NewCpuOne) == Float.parseFloat(NewCpuTwo)) {
								return 0;
							} else if (Float.parseFloat(NewCpuOne) > Float.parseFloat(NewCpuTwo)) {
								return 1;
							} else {
								return -1;
							}
						}
					}

					if ("memoryAvailability".equals(field)) {
						String memoryOne = o1.getMemoryAvailability();
						if (memoryOne != null && (memoryOne.equals("N/A") || memoryOne.equals("--"))) {
							memoryOne = null;
						}
						String NewMemoryOne = null;
						if (!Util.isEmpty(memoryOne)) {
							NewMemoryOne = memoryOne.substring(0, memoryOne.length() - 1);
						}
						String memoryTwo = o2.getMemoryAvailability();
						if (memoryTwo != null && (memoryTwo.equals("N/A") || memoryTwo.equals("--"))) {
							memoryTwo = null;
						}
						String NewMemoryTwo = null;
						if (!Util.isEmpty(memoryTwo)) {
							NewMemoryTwo = memoryTwo.substring(0, memoryTwo.length() - 1);
						}
						if (memoryOne == null && memoryTwo == null) {
							return 0;
						} else if (memoryOne == null && memoryTwo != null) {
							return -1;
						} else if (memoryOne != null && memoryTwo == null) {
							return 1;
						} else {
							if (Float.parseFloat(NewMemoryOne) == Float.parseFloat(NewMemoryTwo)) {
								return 0;
							} else if (Float.parseFloat(NewMemoryOne) > Float.parseFloat(NewMemoryTwo)) {
								return 1;
							} else {
								return -1;
							}
						}
					}
					return 0;
				}

			});
		} else {
			Collections.sort(resources, new Comparator<ResourceMonitorBo>() {

				@Override
				public int compare(ResourceMonitorBo o1, ResourceMonitorBo o2) {
					if ("cpuAvailability".equals(field)) {
						String cpuOne = o1.getCpuAvailability();
						if (cpuOne != null && (cpuOne.equals("N/A") || cpuOne.equals("--"))) {
							cpuOne = null;
						}
						String NewCpuOne = null;
						if (!Util.isEmpty(cpuOne)) {
							NewCpuOne = cpuOne.substring(0, cpuOne.length() - 1);
						}
						String cpuTwo = o2.getCpuAvailability();
						if (cpuTwo != null && (cpuTwo.equals("N/A") || cpuTwo.equals("--"))) {
							cpuTwo = null;
						}
						String NewCpuTwo = null;
						if (!Util.isEmpty(cpuTwo)) {
							NewCpuTwo = cpuTwo.substring(0, cpuTwo.length() - 1);
						}
						if (cpuOne == null && cpuTwo == null) {
							return 0;
						} else if (cpuOne == null && cpuTwo != null) {
							return 1;
						} else if (cpuOne != null && cpuTwo == null) {
							return -1;
						} else {
							if (Float.parseFloat(NewCpuOne) == Float.parseFloat(NewCpuTwo)) {
								return 0;
							} else if (Float.parseFloat(NewCpuOne) > Float.parseFloat(NewCpuTwo)) {
								return -1;
							} else {
								return 1;
							}
						}
					}

					if ("memoryAvailability".equals(field)) {
						String memoryOne = o1.getMemoryAvailability();
						if (memoryOne != null && (memoryOne.equals("N/A") || memoryOne.equals("--"))) {
							memoryOne = null;
						}
						String NewMemoryOne = null;
						if (!Util.isEmpty(memoryOne)) {
							NewMemoryOne = memoryOne.substring(0, memoryOne.length() - 1);
						}
						String memoryTwo = o2.getMemoryAvailability();
						if (memoryTwo != null && (memoryTwo.equals("N/A") || memoryTwo.equals("--"))) {
							memoryTwo = null;
						}
						String NewMemoryTwo = null;
						if (!Util.isEmpty(memoryTwo)) {
							NewMemoryTwo = memoryTwo.substring(0, memoryTwo.length() - 1);
						}
						if (memoryOne == null && memoryTwo == null) {
							return 0;
						} else if (memoryOne == null && memoryTwo != null) {
							return 1;
						} else if (memoryOne != null && memoryTwo == null) {
							return -1;
						} else {
							if (Float.parseFloat(NewMemoryOne) == Float.parseFloat(NewMemoryTwo)) {
								return 0;
							} else if (Float.parseFloat(NewMemoryOne) > Float.parseFloat(NewMemoryTwo)) {
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

		return resources;
	}

	@Override
	public List<Map<String, String>> getDiscoverParamter(long instanceId) {
		List<Map<String, String>> props = new ArrayList<Map<String, String>>();
		try {
			ResourceInstance instance = resourceInstanceService.getResourceInstance(instanceId);
			List<DiscoverProp> discoverProps = instance.getDiscoverProps();
			for (int i = 0; discoverProps != null && i < discoverProps.size(); i++) {
				Map<String, String> prop = new HashMap<String, String>();
				DiscoverProp discoverProp = discoverProps.get(i);
				prop.put("key", discoverProp.getKey());
				// ????????????????????????
				if (SecureUtil.isPassswordKey(discoverProp.getKey())) {
					String decryptCom = SecureUtil.pwdDecrypt(discoverProp.getValues()[0]);
					prop.put("value", decryptCom);
				} else {
					prop.put("value", discoverProp.getValues()[0]);
				}
				props.add(prop);
			}
			Map<String, String> discoverNodeProp = new HashMap<String, String>();
			discoverNodeProp.put("key", "nodeGroupId");
			discoverNodeProp.put("value", instance.getDiscoverNode());
			props.add(discoverNodeProp);
			Map<String, String> domainIdProp = new HashMap<String, String>();
			domainIdProp.put("key", "domainId");
			domainIdProp.put("value", String.valueOf(instance.getDomainId()));
			props.add(domainIdProp);
		} catch (InstancelibException e) {
			logger.error("getDiscoverParamter", e);
		}
		return props;
	}

	@Override
	public boolean saveLiablePerson(long[] instanceIds, String[] userIds) {
		for (int i = 0; i < instanceIds.length; i++) {
			CustomProp prop;
			if (clearLiablePersonCore(instanceIds[i])) {
				try {
					prop = new CustomProp();
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

	@Override
	public boolean clearLiablePerson(long[] instanceIds) {
		for (int i = 0; i < instanceIds.length; i++) {
			if (!clearLiablePersonCore(instanceIds[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param instanceId
	 * @return
	 */
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
}
