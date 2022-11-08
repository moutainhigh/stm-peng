package com.mainsteam.stm.common.metric;

import java.util.List;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.dao.MetricDataDAO;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;

public class InstanceAddlistener implements InstancelibListener{

	private MetricDataDAO metricDataDAO;
	private CapacityService capacityService;
	public void setMetricDataDAO(MetricDataDAO metricDataDAO) {
		this.metricDataDAO = metricDataDAO;
	}
	
	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	@Override
	public void listen(InstancelibEvent instancelibEvent) throws Exception {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_ADD_EVENT){
			List<ResourceInstance> instances= (List<ResourceInstance>) instancelibEvent.getSource();
			for (ResourceInstance instance : instances) {
				ResourceDef resourceDef=capacityService.getResourceDefById(instance.getResourceId());
				for(ResourceMetricDef  mdef :resourceDef.getMetricDefs()){
					if(MetricTypeEnum.PerformanceMetric.equals(mdef.getMetricType())){
						metricDataDAO.createRealtimeMetricTable(mdef.getId());
						metricDataDAO.createHistoryMetricTable(mdef.getId());
					}
				}
			}
		}
	}
}
