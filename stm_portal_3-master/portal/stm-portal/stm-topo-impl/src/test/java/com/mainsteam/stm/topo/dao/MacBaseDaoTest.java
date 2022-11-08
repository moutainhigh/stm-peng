package com.mainsteam.stm.topo.dao;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class MacBaseDaoTest extends AbstractDaoest{
	
	@Resource(name="stm_topo_macBaseDao")
	IMacBaseDao dao;
	
	/**
	 * 说明：分页查询
	 */
	@Test
	public void testPageSelect(){
		Page<MacBaseBo, MacBaseBo> page = new Page<MacBaseBo, MacBaseBo>();
		MacBaseBo params = new MacBaseBo();
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


