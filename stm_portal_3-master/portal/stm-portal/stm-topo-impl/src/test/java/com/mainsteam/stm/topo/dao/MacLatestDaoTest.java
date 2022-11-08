package com.mainsteam.stm.topo.dao;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.bo.MacLatestBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class MacLatestDaoTest extends AbstractDaoest{
	
	@Resource(name="stm_topo_latestMacDao")
	IMacLatestDao dao;
	
	/**
	 * 说明：查询所有
	 */
	@Test
	public void testGetMacLatestBosByIds(){
		Long[] ids = {2209007l,2209008l,2209099l};
		List<MacLatestBo> latestBos = dao.getMacLatestBosByIds(ids);
		
		Assert.assertTrue("全部查询正确", latestBos != null);;
		System.out.println("总条数："+latestBos.size());
	}
	
	/**
	 * 说明：查询所有
	 */
	@Test
	public void testGetAllMacLatestBos(){
		List<MacLatestBo> latestBos = dao.getAllMacLatestBos();
		
		Assert.assertTrue("全部查询正确", latestBos != null);;
		System.out.println("总条数："+latestBos.size());
	}

	/**
	 * 说明：分页查询
	 */
	@Test
	public void testPageSelect(){
		Page<MacLatestBo, MacLatestBo> page = new Page<MacLatestBo, MacLatestBo>();
		MacLatestBo params = new MacLatestBo();
//		params.setSearchType(1);
//		params.setSearchVal("118.1");//172.16.118.1
//		params.setSearchType(2l);
		params.setSearchVal("zx");
		
//		page.setStartRow(0);
//		page.setRowCount(10);
		page.setCondition(params);
		
		dao.selectByPage(page);
		
		Assert.assertNotEquals(0, page.getTotalRecord());
		
		System.out.println("条数："+page.getRowCount());
		System.out.println("总条数："+page.getTotalRecord());
	}
	
}
