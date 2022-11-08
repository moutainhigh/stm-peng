package com.mainsteam.stm.topo.service.impl;

import javax.annotation.Resource;

import org.junit.Test;

import com.mainsteam.stm.topo.api.IBackbordApi;
import com.mainsteam.stm.topo.bo.BackbordBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class BackbordImplTest extends AbstractDaoest{

	@Resource(name="backbordApi")
	private IBackbordApi backbordApi;
	
	@Test
	public void testGetBackbordBo() {
		long instanceId = 6220645l;
//		BackbordBo bo = backbordApi.getBackbordBo(instanceId);
//		System.out.println("背板信息:"+bo.getInfo());
	}
	
}