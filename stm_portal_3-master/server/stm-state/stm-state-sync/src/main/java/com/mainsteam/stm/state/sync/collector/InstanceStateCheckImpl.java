package com.mainsteam.stm.state.sync.collector;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.pluginserver.util.InstanceMonitorCheck;

public class InstanceStateCheckImpl implements InstanceMonitorCheck,InitializingBean {
	private static final Log logger=LogFactory.getLog(InstanceStateCheckImpl.class);
	
	private CapacityService capacityService;
	private List<String> availableMetricList=new ArrayList<>();
	
	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	@Override
	public void check(MetricCalculateData data) {
		if(availableMetricList.contains(data.getMetricId()+"-"+data.getResourceId())){
			MetricExecuteFilterByCaculate.InstanceFaultMap.put(data.getResourceInstanceId(), data.getMetricData()==null || data.getMetricData().length<1 || !"1".equals(data.getMetricData()[0]));
		}
		
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		for(ResourceDef rdef:capacityService.getResourceDefList()){
			for(ResourceMetricDef mdef:rdef.getMetricDefs()){
				if(MetricTypeEnum.AvailabilityMetric ==mdef.getMetricType()){
					availableMetricList.add(mdef.getId()+"-"+rdef.getId());
				}
			}
		}
	}
}
