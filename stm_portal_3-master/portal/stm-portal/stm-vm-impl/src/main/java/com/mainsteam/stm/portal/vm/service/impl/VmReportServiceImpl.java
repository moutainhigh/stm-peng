package com.mainsteam.stm.portal.vm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateExpand;
//import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.vm.api.VmReportService;
import com.mainsteam.stm.portal.vm.bo.VmResourceTreeBo;
import com.mainsteam.stm.portal.vm.dao.IVmResourceTreeDao;
import com.mainsteam.stm.portal.vm.po.VmResourceTreePo;
//import com.mainsteam.stm.portal.vm.web.action.VmReportAction;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.util.UnitTransformUtil;

/**
 * <li>文件名称: VmReportServiceImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年4月8日
 * @author tongpl
 */
public class VmReportServiceImpl implements VmReportService {
	private static final Log logger = LogFactory
			.getLog(VmReportServiceImpl.class);

	// @Resource
	// private ReportTemplateApi reportTemplateApi;

	@Resource
	private CapacityService capacityService;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private ProfileService profileService;

	@Resource
	private MetricDataService metricDataService;

	@Resource
	private IVmResourceTreeDao vmResourceTreeDao;

	@Resource
	private ReportTemplateApi reportTemplateApi;

	@Resource
	private IResourceApi stm_system_resourceApi;

	/**
	 * 获取虚拟资源统计信息
	 * 
	 * @param
	 * @return
	 */
	public Map<String, List<Map<String, String>>> getResourceCountInfoByCategoryId(
			String categoryId) {
		Map<String, List<Map<String, String>>> mapList = new HashMap<String, List<Map<String, String>>>();
		CategoryDef categoryDef = capacityService.getCategoryById(categoryId);
		if (null != categoryDef) {
			CategoryDef[] childCategoryDef = categoryDef.getChildCategorys();
			if (null != childCategoryDef && childCategoryDef.length > 0) {
				try {
					List<String> categoryIds = new ArrayList<String>(
							childCategoryDef.length);
					List<String> resourceIds = new ArrayList<String>(
							childCategoryDef.length);
					for (CategoryDef child : childCategoryDef) {
						if (child == null || child.getResourceIds() == null
								|| child.getResourceIds().length <= 0) {
							continue;
						}
						categoryIds.add(child.getId());
						resourceIds.add(child.getResourceIds()[0]);
					}
					Map<String, VmResourceTreePo> instanceMap = new HashMap<String, VmResourceTreePo>();
					Map<String, VmResourceTreePo> uuidMap = new HashMap<String, VmResourceTreePo>();
					List<VmResourceTreePo> treeObjs = vmResourceTreeDao
							.selectByResourceId(resourceIds);
					for (int i = 0; i < treeObjs.size(); i++) {
						VmResourceTreePo tmp = treeObjs.get(i);
						uuidMap.put(tmp.getUuid(), tmp);
						instanceMap.put(tmp.getInstanceid() + "", tmp);
					}
					for (int i = 0; i < categoryIds.size(); i++) {
						List<ResourceInstance> riList = resourceInstanceService
								.getResourceInstanceByResourceId(resourceIds
										.get(i));
						setMapList(categoryIds.get(i), resourceIds.get(i),
								mapList, riList, uuidMap, instanceMap);
					}
				} catch (InstancelibException e) {
					e.printStackTrace();
					if (logger.isErrorEnabled()) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
		return mapList;
	}

	public VmResourceTreeBo selectByInstanceId(long instanceId) {
		VmResourceTreePo vrtp = vmResourceTreeDao
				.selectByInstanceId(instanceId);
		if (null != vrtp) {
			VmResourceTreeBo vrb = new VmResourceTreeBo();
			BeanUtils.copyProperties(vrtp, vrb);
			return vrb;
		} else {
			return null;
		}
	}

	public VmResourceTreeBo selectVCenterByInstanceId(long instanceId) {
		VmResourceTreePo vrtp = vmResourceTreeDao
				.selectVCenterByInstanceId(instanceId);
		if (null != vrtp) {
			VmResourceTreeBo vrb = new VmResourceTreeBo();
			BeanUtils.copyProperties(vrtp, vrb);
			return vrb;
		} else {
			return null;
		}
	}

	public VmResourceTreeBo selectClusterByInstanceId(long instanceId) {
		VmResourceTreePo vrtp = vmResourceTreeDao
				.selectClusterByInstanceId(instanceId);
		if (null != vrtp) {
			VmResourceTreeBo vrb = new VmResourceTreeBo();
			BeanUtils.copyProperties(vrtp, vrb);
			return vrb;
		} else {
			return null;
		}
	}

	public VmResourceTreeBo selectHostByInstanceId(long instanceId) {
		VmResourceTreePo vrtp = vmResourceTreeDao
				.selectHostByInstanceId(instanceId);
		if (null != vrtp) {
			VmResourceTreeBo vrb = new VmResourceTreeBo();
			BeanUtils.copyProperties(vrtp, vrb);
			return vrb;
		} else {
			return null;
		}
	}

	public List<VmResourceTreeBo> selectAllChildrenByInstanceId(long instanceId) {
		List<VmResourceTreePo> listPo = vmResourceTreeDao
				.selectAllChildrenByInstanceId(instanceId);
		List<VmResourceTreeBo> listBo = new ArrayList<VmResourceTreeBo>();
		for (VmResourceTreePo vrp : listPo) {
			VmResourceTreeBo vrb = new VmResourceTreeBo();
			BeanUtils.copyProperties(vrp, vrb);
			listBo.add(vrb);
		}
		return listBo;
	}

	private void setMapList(String categoryId, String resourceId,
			Map<String, List<Map<String, String>>> mapList,
			List<ResourceInstance> riList,
			Map<String, VmResourceTreePo> uuidMap,
			Map<String, VmResourceTreePo> instanceMap) {
		if (null == riList || riList.size() == 0) {
			return;
		}
		// 当前类型ID-指标对象集合的键值对
		Map<String, List<MetricData>> metircDataMap = new HashMap<String, List<MetricData>>();
		// 当前类型指标名称-指标单位的键值对
		Map<String, String> metricUnitMap = new HashMap<String, String>();

		Map<String, ResourceInstance> resourceInstanceMap = new HashMap<String, ResourceInstance>();
		// 获取当前类型的指标信息
		ResourceDef[] rdfArr = capacityService.getCategoryById(categoryId)
				.getResourceDefs();
		Set<String> metricPerList = new HashSet<String>();
		Set<String> metricInfoList = new HashSet<String>();
		Set<String> metricAvaList = new HashSet<String>();
		for (ResourceDef rd : rdfArr) {
			ResourceMetricDef[] rmdf = rd.getMetricDefs();
			for (int i = 0; i < rmdf.length; i++) {
				ResourceMetricDef rm = rmdf[i];
				if (rm.isDisplay()) {
					switch (rm.getMetricType()) {
					case AvailabilityMetric:
						metricAvaList.add(rm.getId());
						break;
					case InformationMetric:
						metricInfoList.add(rm.getId());
						break;
					case PerformanceMetric:
						metricPerList.add(rm.getId());
						break;
					}
					metricUnitMap.put(rm.getId(), rm.getUnit());
				}
			}
		}

		String[] metricPerforman = new String[metricPerList.size()];
		String[] metricInfo = new String[metricInfoList.size()];
		String[] metricAva = new String[metricAvaList.size()];

		metricPerList.toArray(metricPerforman);
		metricInfoList.toArray(metricInfo);
		metricAvaList.toArray(metricAva);

		for (int i = 0; i < riList.size(); i++) {
			long instanceId = riList.get(i).getId();
			List<MetricData> listMetricData = new ArrayList<MetricData>();
			if (metricInfo.length > 0) {
				listMetricData = metricDataService.getMetricInfoDatas(
						instanceId, metricInfo);
			}
			if (metricAva.length > 0) {
				if (listMetricData.size() > 0) {
					for (String metric : metricAva) {
						listMetricData.add(metricDataService
								.getMetricAvailableData(instanceId, metric));
					}
				}
			}
			metircDataMap.put(String.valueOf(instanceId), listMetricData);
			resourceInstanceMap.put(String.valueOf(instanceId), riList.get(i));
		}

		List<Map<String, String>> resultSet = new ArrayList<Map<String, String>>();
		// 虚拟机会批量查找宿主主机的指标值
		// Map<Long, List<MetricData>> vmHostMetricValue = null;
		// if ("VirtualVM".equals(categoryId)) {
		// vmHostMetricValue = new HashMap<Long, List<MetricData>>();
		// Set<Long> hostInstanceIdSet = new HashSet<Long>();
		// Map<Long, Long> vmIdHostId = new HashMap<Long, Long>();
		// String[] hostMetricStr = { "TotalCPU", "TotalMemSize" };
		// for (ResourceInstance ri : riList) {
		// //VmResourceTreeBo vmhost = this.selectHostByInstanceId(ri.getId());
		// VmResourceTreePo vmhost = treeMap.get(treeMap.get(ri.getId() +
		// "").getHostuuid());
		// if (null != vmhost) {
		// hostInstanceIdSet.add(vmhost.getInstanceid());
		// vmIdHostId.put(ri.getId(), vmhost.getInstanceid());
		// }
		// }
		// for (Long id : hostInstanceIdSet) {
		// List<MetricData> listMetricData = new ArrayList<MetricData>();
		// listMetricData = metricDataService.getMetricInfoDatas(id,
		// hostMetricStr);
		// for (Long vmId : vmIdHostId.keySet()) {
		// if (vmIdHostId.get(vmId).longValue() == id.longValue()) {
		// vmHostMetricValue.put(vmId, listMetricData);
		// }
		// }
		// }
		// getMetricUnitMap(capacityService, metricUnitMap, "VirtualHost");
		// // // 主机指标单位
		// // ResourceDef[] rdfArrVmHost = capacityService.getCategoryById(
		// // "VirtualHost").getResourceDefs();
		// // for (ResourceDef rd : rdfArrVmHost) {
		// // ResourceMetricDef[] rmdf = rd.getMetricDefs();
		// // for (int i = 0; i < rmdf.length; i++) {
		// // ResourceMetricDef rm = rmdf[i];
		// // if (rm.isDisplay()) {
		// // metricUnitMap.put(rm.getId(), rm.getUnit());
		// // }
		// // }
		// // }
		// }

		for (String key : metircDataMap.keySet()) {
			Map<String, String> mapInstance = new HashMap<String, String>();
			List<MetricData> listMetricData = metircDataMap.get(key);

			String unitStr = "_Unit";
			switch (categoryId) {
			case "VCenter":
			case "VCenter5.5":
			case "VCenter6":
			case "VCenter6.5":
				String VCenterInstanceId = key;

				mapInstance.put("id", VCenterInstanceId);
				mapInstance.put("vCenter",
						resourceInstanceMap.get(VCenterInstanceId)
								.getShowName());
				mapInstance.put("dataCenter", "--");
				mapInstance.put("cluster", "--");
				mapInstance.put("esxi", "--");
				mapInstance.put("vMare", "--");
				mapInstance.put("dataStorage", "--");
				mapInstance.put("CPUCountGhz", "--");
				mapInstance.put("memCount", "--");
				mapInstance.put("storageCount", "--");

				List<VmResourceTreeBo> vrtList = this
						.selectAllChildrenByInstanceId(Long
								.parseLong(VCenterInstanceId));
				// List<VmResourceTreePo> vrtList = vcenterMap.get(key);
				if (vrtList.size() > 0) {
					int clusterNum = 0, hostNum = 0, vMareNum = 0, dataStorageNum = 0;
					String[] hostMetricStr = { "TotalCPU", "TotalMemSize" };
					String[] storageMetricStr = { "DataStorageVolume" };
					getMetricUnitMap(capacityService, metricUnitMap,
							"VirtualStorage");
					getMetricUnitMap(capacityService, metricUnitMap,
							"VirtualHost");

					// // 主机,存储指标单位
					// ResourceDef[] rdfArrVmStrorage =
					// capacityService.getCategoryById("VirtualStorage").getResourceDefs();
					// ResourceDef[] rdfArrVmHost =
					// capacityService.getCategoryById("VirtualHost").getResourceDefs();
					// for (ResourceDef rd : rdfArrVmStrorage) {
					// ResourceMetricDef[] rmdf = rd.getMetricDefs();
					// for (int i = 0; i < rmdf.length; i++) {
					// ResourceMetricDef rm = rmdf[i];
					// if (rm.isDisplay()) {
					// metricUnitMap.put(rm.getId(), rm.getUnit());
					// }
					// }
					// }
					// for (ResourceDef rd : rdfArrVmHost) {
					// ResourceMetricDef[] rmdf = rd.getMetricDefs();
					// for (int i = 0; i < rmdf.length; i++) {
					// ResourceMetricDef rm = rmdf[i];
					// if (rm.isDisplay()) {
					// metricUnitMap.put(rm.getId(), rm.getUnit());
					// }
					// }
					// }

					float vcenterCPUcount = 0;
					float vcenterMEMcount = 0;
					float vcenterSTORAGEcount = 0;

					for (VmResourceTreeBo vrb : vrtList) {
						if (null != vrb.getResourceid()) {
							switch (vrb.getResourceid()) {
							case "VMWareCluster":
							case "VMWareCluster5.5":
							case "VMWareCluster6":
							case "VMWareCluster6.5":
								clusterNum += 1;
								break;
							case "vmESXi":
							case "vmESXi5.5":
							case "vmESXi6":
							case "vmESXi6.5":
								hostNum += 1;
								List<MetricData> hostListMetricData = metricDataService
										.getMetricInfoDatas(
												vrb.getInstanceid(),
												hostMetricStr);
								if (null != hostListMetricData
										&& hostListMetricData.size() > 0) {
									for (MetricData md : hostListMetricData) {
										switch (md.getMetricId()) {
										case "TotalCPU":
											if (null != md.getData()
													&& md.getData().length > 0) {
												for (String value : md
														.getData()) {
													vcenterCPUcount += Float
															.parseFloat(value);
												}
											}
											break;
										case "TotalMemSize":
											if (null != md.getData()
													&& md.getData().length > 0) {
												for (String value : md
														.getData()) {
													if (null != value
															&& !"null"
																	.equals(value)) {
														vcenterMEMcount += Float
																.parseFloat(value);
													}
												}
											}
											break;
										}
									}
								}
								break;
							case "VMWareVM":
							case "VMWareVM5.5":
							case "VMWareVM6":
							case "VMWareVM6.5":
								vMareNum += 1;
								break;
							case "VMWareDatastore":
							case "VMWareDatastore5.5":
							case "VMWareDatastore6":
							case "VMWareDatastore6.5":
								dataStorageNum += 1;
								List<MetricData> stroListMetricData = metricDataService
										.getMetricInfoDatas(
												vrb.getInstanceid(),
												storageMetricStr);
								if (null != stroListMetricData
										&& stroListMetricData.size() > 0) {
									for (MetricData md : stroListMetricData) {
										switch (md.getMetricId()) {
										case "DataStorageVolume":
											if (null != md.getData()
													&& md.getData().length > 0) {
												for (String value : md
														.getData()) {
													vcenterSTORAGEcount += Float
															.parseFloat(value);
												}
											}
											break;
										}
									}
								}
								break;
							}
						}
					}
					// mapInstance.put("dataCenter", "--");
					mapInstance.put("cluster", String.valueOf(clusterNum));
					mapInstance.put("esxi", String.valueOf(hostNum));
					mapInstance.put("vMare", String.valueOf(vMareNum));
					mapInstance.put("dataStorage",
							String.valueOf(dataStorageNum));
					mapInstance.put("CPUCountGhz" + unitStr,
							metricUnitMap.get("TotalCPU"));
					if (vcenterCPUcount > 0) {
						mapInstance.put("CPUCountGhz",
								String.valueOf(vcenterCPUcount));
					}
					mapInstance.put("memCount" + unitStr,
							metricUnitMap.get("TotalMemSize"));
					if (vcenterMEMcount > 0) {
						mapInstance.put("memCount",
								String.valueOf(vcenterMEMcount));
					}
					mapInstance.put("storageCount" + unitStr,
							metricUnitMap.get("DataStorageVolume"));
					if (vcenterSTORAGEcount > 0) {
						mapInstance.put("storageCount",
								String.valueOf(vcenterSTORAGEcount));
					}
				}
				break;

			case "VirtualCluster":
			case "VirtualCluster5.5":
			case "VirtualCluster6":
			case "VirtualCluster6.5":
				String clusterInstanceId = key;
				mapInstance.put("id", clusterInstanceId);
				mapInstance.put("cluster",
						resourceInstanceMap.get(clusterInstanceId)
								.getShowName());
				mapInstance.put("TotalMemSize", "--");
				mapInstance.put("DatastoreSize", "--");
				mapInstance.put("HostNum", "--");
				mapInstance.put("VMNum", "--");
				mapInstance.put("HAStatus", "--");
				mapInstance.put("DRSStatus", "--");
				mapInstance.put("EVCMode", "--");
				mapInstance.put("dataCenter", "--");
				mapInstance.put("TotalCPU", "--");

				// VmResourceTreeBo vCenter =
				// this.selectVCenterByInstanceId(Long.parseLong(clusterInstanceId));
				VmResourceTreePo vCenter = uuidMap.get(instanceMap.get(
						clusterInstanceId).getVcenteruuid());
				if (null != vCenter) {
					try {
						ResourceInstance ri = resourceInstanceService
								.getResourceInstance(vCenter.getInstanceid());
						if (null != ri) {
							mapInstance.put("vCenter", ri.getShowName());
						}
					} catch (InstancelibException e) {
						e.printStackTrace();
					}
				}

				mapInstance.put("VMNum" + unitStr, metricUnitMap.get("VMNum"));
				mapInstance.put("HostNum" + unitStr,
						metricUnitMap.get("HostNum"));
				mapInstance.put("EVCMode" + unitStr,
						metricUnitMap.get("EVCMode"));
				mapInstance.put("DRSStatus" + unitStr,
						metricUnitMap.get("DRSStatus"));
				mapInstance.put("HAStatus" + unitStr,
						metricUnitMap.get("HAStatus"));
				mapInstance.put("TotalCPU" + unitStr,
						metricUnitMap.get("TotalCPU"));
				mapInstance.put("DatastoreSize" + unitStr,
						metricUnitMap.get("DatastoreSize"));
				mapInstance.put("TotalMemSize" + unitStr,
						metricUnitMap.get("TotalMemSize"));

				for (MetricData md : listMetricData) {
					if (null == md || null == md.getMetricId()) {
						continue;
					}
					switch (md.getMetricId()) {
					case "VMNum":
						mapInstance.put("VMNum",
								parseArrayToString(md.getData(), ""));
						break;
					case "HostNum":
						mapInstance.put("HostNum",
								parseArrayToString(md.getData(), ""));
						break;
					case "EVCMode":
						mapInstance.put("EVCMode",
								parseArrayToString(md.getData(), ""));
						break;
					case "DRSStatus":
						mapInstance.put("DRSStatus",
								parseArrayToString(md.getData(), ""));
						break;
					case "HAStatus":
						mapInstance.put("HAStatus",
								parseArrayToString(md.getData(), ""));
						break;
					case "TotalCPU":
						mapInstance.put("TotalCPU",
								parseArrayToString(md.getData(), ""));
						break;
					case "DatastoreSize":
						mapInstance.put("DatastoreSize",
								parseArrayToString(md.getData(), ""));
						break;
					case "TotalMemSize":
						mapInstance.put("TotalMemSize",
								parseArrayToString(md.getData(), ""));
						break;
					}
				}
				break;

			case "VirtualHost":
			case "VirtualHost5.5":
			case "VirtualHost6":
			case "VirtualHost6.5":

				String virtualHostInstanceId = key;
				mapInstance.put("id", virtualHostInstanceId);
				mapInstance.put("ip",
						resourceInstanceMap.get(virtualHostInstanceId)
								.getShowName());
				mapInstance.put("TotalCPU", "--");
				mapInstance.put("TotalMemSize", "--");
				mapInstance.put("DatastoreSize", "--");
				mapInstance.put("VMNum", "--");
				mapInstance.put("VMotion", "--");
				mapInstance.put("EVC", "--");
				mapInstance.put("cluster", "--");
				// mapInstance.put("dataCenter", "--");
				mapInstance.put("vCenter", "--");

				// VmResourceTreeBo hostCluster =
				// this.selectClusterByInstanceId(Long.parseLong(virtualHostInstanceId));
				// VmResourceTreeBo hostVcenter =
				// this.selectVCenterByInstanceId(Long.parseLong(virtualHostInstanceId));
				VmResourceTreePo hostCluster = uuidMap.get(instanceMap.get(
						virtualHostInstanceId).getClusteruuid());
				VmResourceTreePo hostVcenter = uuidMap.get(instanceMap.get(
						virtualHostInstanceId).getVcenteruuid());

				if (null != hostCluster && null != hostCluster.getInstanceid()) {
					try {
						ResourceInstance riCluster = resourceInstanceService
								.getResourceInstance(hostCluster
										.getInstanceid());
						if (null != riCluster) {
							mapInstance.put("cluster", riCluster.getShowName());
						}
					} catch (InstancelibException e) {
						e.printStackTrace();
					}
				}
				if (null != hostVcenter && null != hostVcenter.getInstanceid()) {
					try {
						ResourceInstance riVcenter = resourceInstanceService
								.getResourceInstance(hostVcenter
										.getInstanceid());
						if (null != riVcenter) {
							mapInstance.put("vCenter", riVcenter.getShowName());
						}
					} catch (InstancelibException e) {
						e.printStackTrace();
					}
				}

				mapInstance.put("TotalCPU" + unitStr,
						metricUnitMap.get("TotalCPU"));
				mapInstance.put("TotalMemSize" + unitStr,
						metricUnitMap.get("TotalMemSize"));
				mapInstance.put("DatastoreSize" + unitStr,
						metricUnitMap.get("DatastoreSize"));
				mapInstance.put("VMNum" + unitStr, metricUnitMap.get("VMNum"));
				mapInstance.put("VMotion" + unitStr,
						metricUnitMap.get("VMotion"));
				mapInstance.put("EVC" + unitStr, metricUnitMap.get("EVC"));
				for (MetricData md : listMetricData) {
					if (null == md || null == md.getMetricId()) {
						continue;
					}
					switch (md.getMetricId()) {
					case "TotalCPU":
						mapInstance.put(md.getMetricId(),
								parseArrayToString(md.getData(), ""));
						break;
					case "TotalMemSize":
						mapInstance.put(md.getMetricId(),
								parseArrayToString(md.getData(), ""));
						break;
					case "DatastoreSize":
						mapInstance.put(md.getMetricId(),
								parseArrayToString(md.getData(), ""));
						break;
					case "VMNum":
						mapInstance.put(md.getMetricId(),
								parseArrayToString(md.getData(), ""));
						break;
					case "VMotion":
						mapInstance.put(md.getMetricId(),
								parseArrayToString(md.getData(), ""));
						break;
					case "EVC":
						mapInstance.put(md.getMetricId(),
								parseArrayToString(md.getData(), ""));
						break;
					}
				}
				break;

			case "VirtualVM":
			case "VirtualVM5.5":
			case "VirtualVM6":
			case "VirtualVM6.5":

				String virtualVMInstanceId = key;
				mapInstance.put("id", virtualVMInstanceId);
				mapInstance.put("vMareName",
						resourceInstanceMap.get(virtualVMInstanceId)
								.getShowName());
				mapInstance.put("osVersion", "--");
				mapInstance.put("DiskAssignedSpace", "--");
				mapInstance.put("hostCPUGhz", "--");
				mapInstance.put("hostMem", "--");
				mapInstance.put("hostPC", "--");
				// mapInstance.put("dataCenter", "dataCenter1");
				mapInstance.put("vCenter", "--");

				// VmResourceTreeBo vmhost =
				// this.selectHostByInstanceId(Long.parseLong(virtualVMInstanceId));
				// VmResourceTreeBo vmVcenter =
				// this.selectVCenterByInstanceId(Long.parseLong(virtualVMInstanceId));
				VmResourceTreePo vmhost = uuidMap.get(instanceMap.get(
						virtualVMInstanceId).getHostuuid());
				VmResourceTreePo vmVcenter = uuidMap.get(instanceMap.get(
						virtualVMInstanceId).getVcenteruuid());

				if (null != vmhost && null != vmhost.getInstanceid()) {
					try {
						ResourceInstance riVcenter = resourceInstanceService
								.getResourceInstance(vmhost.getInstanceid());
						if (null != riVcenter) {
							mapInstance.put("hostPC", riVcenter.getShowName());
						}
					} catch (InstancelibException e) {
						e.printStackTrace();
					}
				}
				if (null != vmVcenter && null != vmVcenter.getInstanceid()) {
					try {
						ResourceInstance riHost = resourceInstanceService
								.getResourceInstance(vmVcenter.getInstanceid());
						if (null != riHost) {
							mapInstance.put("vCenter", riHost.getShowName());
						}
					} catch (InstancelibException e) {
						e.printStackTrace();
					}
				}
				mapInstance.put("osVersion" + unitStr,
						metricUnitMap.get("osVersion"));
				mapInstance.put("DiskAssignedSpace" + unitStr,
						metricUnitMap.get("DiskAssignedSpace"));
				for (MetricData md : listMetricData) {
					if (null == md || null == md.getMetricId()) {
						continue;
					}
					switch (md.getMetricId()) {
					case "osVersion":
						mapInstance.put("osVersion",
								parseArrayToString(md.getData(), ""));
						break;
					case "DiskAssignedSpace":
						mapInstance.put("DiskAssignedSpace",
								parseArrayToString(md.getData(), ""));
						break;
					case "cpuNum":
						mapInstance.put("cpuNum",
								parseArrayToString(md.getData(), ""));
						break;
					case "MEMVMSize":
						mapInstance.put("MEMVMSize",
								parseArrayToString(md.getData(), ""));
						break;
					}
				}
				mapInstance
						.put("cpuNum" + unitStr, metricUnitMap.get("cpuNum"));
				mapInstance.put("MEMVMSize" + unitStr,
						metricUnitMap.get("MEMVMSize"));
				break;

			case "VirtualStorage":
			case "VirtualStorage5.5":
			case "VirtualStorage6":
			case "VirtualStorage6.5":

				String virtualStorageInstanceId = key;
				mapInstance.put("id", virtualStorageInstanceId);
				mapInstance.put("storageName",
						resourceInstanceMap.get(virtualStorageInstanceId)
								.getShowName());
				mapInstance.put("HostNum", "--");
				mapInstance.put("VMNum", "--");
				mapInstance.put("DataStorageVolume", "--");
				mapInstance.put("DataStorageFreeSpace", "--");
				mapInstance.put("HostNum" + unitStr,
						metricUnitMap.get("HostNum"));
				mapInstance.put("VMNum" + unitStr, metricUnitMap.get("VMNum"));
				mapInstance.put("DataStorageVolume" + unitStr,
						metricUnitMap.get("DataStorageVolume"));
				mapInstance.put("DataStorageFreeSpace" + unitStr,
						metricUnitMap.get("DataStorageFreeSpace"));
				for (MetricData md : listMetricData) {
					if (null == md || null == md.getMetricId()) {
						continue;
					}
					switch (md.getMetricId()) {
					case "HostNum":
						mapInstance.put("HostNum",
								parseArrayToString(md.getData(), ""));
						break;
					case "VMNum":
						mapInstance.put("VMNum",
								parseArrayToString(md.getData(), ""));
						break;
					case "DataStorageVolume":
						mapInstance.put("DataStorageVolume",
								parseArrayToString(md.getData(), ""));
						break;
					case "DataStorageFreeSpace":
						mapInstance.put("DataStorageFreeSpace",
								parseArrayToString(md.getData(), ""));
						break;
					}
				}
				break;
			}
			resultSet.add(mapInstance);
		}
		mapList.put(categoryId, resultSet);
	}

	public static String parseArrayToString(String[] strArr, String unit) {
		if (null == strArr || strArr.length == 0) {
			return "--";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strArr.length; i++) {
			if (i == 0) {
				sb.append(strArr[i]);
			} else {
				sb.append("," + strArr[i]);
			}
		}
		return UnitTransformUtil.transform(sb.toString(), unit);
	}

	public static void getMetricUnitMap(CapacityService capacityService,
			Map<String, String> map, String categoryId) {
		ResourceDef[] rds = capacityService.getCategoryById(categoryId)
				.getResourceDefs();
		for (int i = 0; i < rds.length; i++) {
			ResourceMetricDef[] rmds = rds[i].getMetricDefs();
			for (int j = 0; j < rmds.length; j++) {
				ResourceMetricDef rmd = rmds[j];
				if (rmd.isDisplay()) {
					map.put(rmd.getId(), rmd.getUnit());
				}
			}
		}
	}

	public static void getMetricValueMap(Map<String, String> map,
			String[] metrics, List<MetricData> datas) {
		for (int i = 0; i < datas.size(); i++) {
			MetricData md = datas.get(i);
			if (null == md || null == md.getMetricId()) {
				continue;
			}
			for (int j = 0; j < metrics.length; j++) {
				if (md.getMetricId().equals(metrics[j])) {
					map.put(metrics[j], parseArrayToString(md.getData(), ""));
				}
			}
		}
	}

	public List<ReportTemplateExpand> getVMReportTemplate(ILoginUser user) {
		List<ReportTemplateExpand> rtListP = reportTemplateApi
				.getAllReportTemplate(user);

		List<ReportTemplateExpand> vmList = new ArrayList<ReportTemplateExpand>();
		if (null == rtListP || rtListP.size() == 0) {
			return vmList;
		}
		for (ReportTemplateExpand rte : rtListP) {
			if (rte.getReportTemplateIsDelete() != 0) {
				continue;
			}
			switch (rte.getReportTemplateType()) {
			case 8:
				vmList.add(rte);
				break;
			case 9:
				vmList.add(rte);
				break;
			}
		}
		return vmList;
	}

	public List<ReportResourceInstance> getVmResourceByType(String categoryId,
			Long domainId, ILoginUser user) {
		List<ReportResourceInstance> instanceList = new ArrayList<ReportResourceInstance>();

		List<String> leafCategoryNameList = new ArrayList<String>();
		leafCategoryNameList.add(categoryId);

		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		queryBo.setCategoryIds(leafCategoryNameList);
		List<Long> domainIds = new ArrayList<Long>();
		domainIds.add(domainId);
		queryBo.setDomainIds(domainIds);
		List<ResourceInstanceBo> instances = stm_system_resourceApi
				.getResources(queryBo);
		if (instances != null && instances.size() > 0) {
			for (ResourceInstanceBo instance : instances) {
				// 判断实例是否监控和是否属于当前用户域
				if (instance.getLifeState() != InstanceLifeStateEnum.MONITORED) {
					continue;
				}
				ReportResourceInstance reportInstance = new ReportResourceInstance();
				BeanUtils.copyProperties(instance, reportInstance);
				if (reportInstance.getShowName() == null
						|| reportInstance.getShowName().equals("")) {
					reportInstance.setShowName(instance.getName());
				}
				ResourceDef resourceDef = capacityService
						.getResourceDefById(reportInstance.getResourceId());
				if (resourceDef == null) {
					if (logger.isErrorEnabled()) {
						logger.error("Get resourceDef is null ,resourceId : "
								+ reportInstance.getResourceId());
					}
				} else {
					reportInstance.setResourceName(resourceDef.getName());
					instanceList.add(reportInstance);
				}
			}
		}
		return instanceList;
	}

	/**
	 * 根据Category获取其需要显示的指标及其单位并填充到Map集合
	 * 
	 * @param metricUnitMap
	 * @param categoryId
	 */
	public void getMetricUnit(Map<String, String> metricUnitMap,
			String categoryId) {
		ResourceDef[] rdfs = capacityService.getCategoryById(categoryId)
				.getResourceDefs();
		for (int i = 0; i < rdfs.length; i++) {
			ResourceDef rd = rdfs[i];
			ResourceMetricDef[] rmdfs = rd.getMetricDefs();
			for (int j = 0; j < rmdfs.length; j++) {
				ResourceMetricDef rm = rmdfs[j];
				if (rm.isDisplay()) {
					metricUnitMap.put(rm.getId(), rm.getUnit());
				}
			}
		}
	}

}
