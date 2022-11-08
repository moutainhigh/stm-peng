package com.mainsteam.stm.profilelib.service.impl;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;

public class TestResource {
	private static final Log logger = LogFactory
			.getLog(TestResource.class);
	private CapacityService capacityService;
	
	public void start(){
		logger.info("calc start");
		HashSet<String> metricIds = new HashSet<String>();
		List<ResourceDef> resourceDefs = capacityService.getResourceDefList();
		for (ResourceDef resourceDef : resourceDefs) {
			ResourceMetricDef[] metricDef =	resourceDef.getMetricDefs();
			if(metricDef != null){
				for (ResourceMetricDef resourceMetricDef : metricDef) {
					if(resourceDef.getType().equals("NetInterface") && resourceMetricDef.getMetricType() == MetricTypeEnum.AvailabilityMetric){
						metricIds.add(resourceMetricDef.getId());
					}
				}
			}
		}
		StringBuilder bb = new StringBuilder(1500);
		bb.append("(");
		for (String id : metricIds) {
			bb.append("\"").append(id).append("\",");
		}
		bb.append(")");
		logger.info(bb);
	}

	public CapacityService getCapacityService() {
		return capacityService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
}
