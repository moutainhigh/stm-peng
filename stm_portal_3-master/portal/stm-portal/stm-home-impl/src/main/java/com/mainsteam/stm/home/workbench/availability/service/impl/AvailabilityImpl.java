package com.mainsteam.stm.home.workbench.availability.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.home.workbench.availability.api.IAvailabilityApi;
import com.mainsteam.stm.home.workbench.availability.bo.AvailabilityBo;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.resource.bo.ResourceMonitorBo;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
@Service("stm_home_workbench_availability_availabilityApi")
public class AvailabilityImpl implements IAvailabilityApi{
	@Resource
	private InstanceStateService instanceStateService;
	@Resource
	private MetricDataService metricDataService;
	@Resource
	private MetricStateService metricStateService;
	@Resource
	private CapacityService capacityService;
	@Resource(name = "resourceInstanceService")
	private ResourceInstanceService resourceInstanceService;
	

	@Override
	public List<AvailabilityBo> getResourceDetailInfo(Long[] instanceIds) {
		List<AvailabilityBo> list = new ArrayList<AvailabilityBo>();
		for (int i = 0; i < instanceIds.length; i++) {
			try {
				ResourceInstance resourceInstance = null;
				resourceInstance = resourceInstanceService.getResourceInstance(instanceIds[i]);
				
				AvailabilityBo bo=new AvailabilityBo();
				bo.setResourceInstance(resourceInstance);
				InstanceStateEnum status = null;
				if(!InstanceLifeStateEnum.NOT_MONITORED.equals(resourceInstance.getLifeState())){
					InstanceStateData isd = instanceStateService.getState(resourceInstance.getId());
					if(isd!=null){
						status = isd.getState();
					}
				}
				bo.setInstanceStatus(getInstanceStateColor(status));
				list.add(bo);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			list=setMetricValue(list);
		}
		return list;
	}
	private List<AvailabilityBo> setMetricValue(
			List<AvailabilityBo> AvailabilityBoList) {

		List<AvailabilityBo> rmbList = new ArrayList<AvailabilityBo>();
		String[] metricArray = { "cpuRate", "memRate","PingResponseTime" };
		Map<String,String> metricUnitName = new HashMap<String,String>();
		long[] instanceIdArray = new long[AvailabilityBoList.size()];

		for (int i = 0; i < AvailabilityBoList.size(); i++) {
			AvailabilityBo AvailabilityBo = AvailabilityBoList.get(i);
			ResourceInstance resourceInstance = AvailabilityBo
					.getResourceInstance();

			ResourceDef rd = capacityService
					.getResourceDefById(resourceInstance.getResourceId());
			
			if(null != rd){
				ResourceMetricDef[] resourceMetricDefArray = rd.getMetricDefs();

				instanceIdArray[i] = resourceInstance.getId();
				for (ResourceMetricDef rmd : resourceMetricDefArray) {
					if (rmd.getId().equals("cpuRate")) {
						MetricStateData cpuRateMetric = metricStateService
								.getMetricState(resourceInstance.getId(), rmd.getId());
						if (null != cpuRateMetric) {
							AvailabilityBo
									.setCpuStatus(getMetricStateEnumString(cpuRateMetric
											.getState()));
						} else {
							AvailabilityBo
									.setCpuStatus(getMetricStateEnumString(null));
						}
						
						metricUnitName.put("cpuRate", rmd.getUnit());
					} else if (rmd.getId().equals("memRate")) {
						MetricStateData memRateMetric = metricStateService
								.getMetricState(resourceInstance.getId(), rmd.getId());
						if (null != memRateMetric) {
							AvailabilityBo
									.setMemoryStatus(getMetricStateEnumString(memRateMetric
											.getState()));
						} else {
							AvailabilityBo
									.setMemoryStatus(getMetricStateEnumString(null));
						}
						
						metricUnitName.put("memRate", rmd.getUnit());
					}else if(rmd.getId().equals("PingResponseTime")){
						
						metricUnitName.put("PingResponseTime", rmd.getUnit());
					}
				}
			}
			
			rmbList.add(AvailabilityBo);
		}

		MetricRealtimeDataQuery mr = new MetricRealtimeDataQuery();
		mr.setMetricID(metricArray);
		mr.setInstanceID(instanceIdArray);

		// 根据resourceInstance 获取指标对象 指标对象metricId String[] metricID 查询
		try {
			Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService
					.queryRealTimeMetricDatas(mr, 1, 100);
			List<Map<String, ?>> mapList = page.getDatas();
			
			for (AvailabilityBo rmb : rmbList) {
				for (Map<String, ?> map : mapList) {
					if (null != map.get("instanceid")&& rmb.getResourceInstance().getId() == new Long(map
									.get("instanceid").toString())) {
						
						
						if(null != map.get("cpuRate")) {
							rmb.setCpuAvailability(map.get("cpuRate").toString()+metricUnitName.get("cpuRate"));
						}
						if(null != map.get("memRate")) {
							rmb.setMemoryAvailability(map.get("memRate").toString()+metricUnitName.get("memRate"));
						}
						if(null != map.get("PingResponseTime")){
							rmb.setResponseTime(map.get("PingResponseTime").toString()+metricUnitName.get("PingResponseTime"));
						}else{
							rmb.setResponseTime("--");
						}
					}
				}
			}
		} catch (Exception e) {
			return rmbList;
		}
		
		return rmbList;
	}
	
	private static String getMetricStateEnumString(MetricStateEnum isEnum) {
		String ise = "green";
		if (null == isEnum) {
			return ise;
		} else {
			if (MetricStateEnum.CRITICAL == isEnum) {
				ise = "red";
			} else if (MetricStateEnum.SERIOUS == isEnum) {
				ise = "orange";
			} else if (MetricStateEnum.WARN == isEnum) {
				ise = "yellow";
			} else if (MetricStateEnum.NORMAL == isEnum) {
				ise = "green";
			}

		}
		return ise;
	}
	private static String getInstStateEnumString(InstanceStateEnum isEnum) {
		String ise = "green";
		if (null == isEnum) {
			return ise;
		} else {
			if (InstanceStateEnum.CRITICAL == isEnum) {
				ise = "red";
			} else if (InstanceStateEnum.SERIOUS == isEnum) {
				ise = "orange";
			} else if (InstanceStateEnum.WARN == isEnum) {
				ise = "yellow";
			} else if (InstanceStateEnum.NORMAL == isEnum) {
				ise = "green";
			}

		}
		return ise;
	}

	private static String getInstanceStateColor(InstanceStateEnum stateEnum) {
		String ise = null;
		if(stateEnum == null){
			return "res_normal_nothing";
		}
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
		case UNKNOWN_NOTHING:
			ise = "res_unknown_nothing";
			break;
//		case UNKOWN:
//			ise = "res_unkown";
//			break;
		default :
			ise = "res_normal_nothing";
			break;
		}
		return ise;
	}
}
