package com.mainsteam.stm.portal.resource.service.impl;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:META-INF/services/*-beans.xml"})
public class ProfileImplTest {
	@Resource
	private CapacityService capacityService;
	
	
	@Test
	public void test() {
		String resourceId="LinuxProcess";
		String metricId="processName";
		ResourceMetricDef resourceMetricDef=capacityService.getResourceMetricDef(resourceId,metricId);
		
		
		System.out.println(resourceMetricDef);
		
		
		
	}

}
