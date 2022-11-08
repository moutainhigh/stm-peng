package com.mainsteam.stm.topo.dao;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class MacRuntimeDaoTest extends AbstractDaoest{
	
	@Resource(name="stm_topo_macRuntimeDao")
	IMacRuntimeDao dao;

	@Test
	public void testGetMacBaseBosByIds(){
		Long[] ids = {1l,2l};
		List<MacRuntimeBo> macs  = dao.getMacRuntimeBosByIds(ids);
		
		Assert.assertTrue(macs.size() > 0);
		System.out.println("查询条数："+macs.size());
	}
	
	/**
	 * 说明：分页查询实时表
	 */
	@Test
	public void testPageSelect(){
		Page<MacRuntimeBo, MacRuntimeBo> page = new Page<MacRuntimeBo, MacRuntimeBo>();
		MacRuntimeBo params = new MacRuntimeBo();
//		params.setSearchType(1);
//		params.setSearchVal("118.1");//172.16.118.1
//		params.setSearchType(2);
//		params.setSearchVal("cc");
		
		page.setStartRow(0);
		page.setRowCount(10);
		page.setCondition(params);
		
		dao.selectByPage(page);
		
		Assert.assertNotEquals(0, page.getTotalRecord());
		
		System.out.println("条数："+page.getRowCount());
		System.out.println("总条数："+page.getTotalRecord());
	}
	
}


