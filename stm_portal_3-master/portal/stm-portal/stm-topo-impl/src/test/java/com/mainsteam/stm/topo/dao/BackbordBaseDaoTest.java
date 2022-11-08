package com.mainsteam.stm.topo.dao;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.mainsteam.stm.topo.bo.BackbordBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class BackbordBaseDaoTest extends AbstractDaoest{
	
	@Resource(name="stm_topo_backbord_baseDao")
	IBackbordBaseDao dao;

	@Test
	public void testGetBackbordBaseInfo(){
		String vendor = "cisco";
		String machineType = "2501";
		BackbordBo bo = dao.getBackbordBaseInfo(vendor, machineType);
		
		Assert.assertTrue(null != bo);
		System.out.println("背板信息："+bo.getInfo());
	}
	
}


