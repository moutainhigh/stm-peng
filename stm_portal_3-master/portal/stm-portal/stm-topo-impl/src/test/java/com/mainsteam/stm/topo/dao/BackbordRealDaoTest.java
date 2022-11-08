package com.mainsteam.stm.topo.dao;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.mainsteam.stm.topo.test.AbstractDaoest;

public class BackbordRealDaoTest extends AbstractDaoest{
	
	@Resource(name="stm_topo_backbord_realDao")
	IBackbordRealDao dao;

	@Test
	public void testAddOrUpdateBackbordRealInfo(){
		Long instanceId = 22l;
		String info = "32132132321";
//		int rows = dao.addOrUpdateBackbordRealInfo(instanceId, info);
		
//		Assert.assertTrue(rows != 0);
	}
	
}


