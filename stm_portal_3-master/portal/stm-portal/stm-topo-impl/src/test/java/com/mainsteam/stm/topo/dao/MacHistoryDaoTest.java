package com.mainsteam.stm.topo.dao;

import javax.annotation.Resource;

import org.junit.Test;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.bo.MacHistoryBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class MacHistoryDaoTest extends AbstractDaoest{
	
	@Resource(name="macHistoryDao")
	IMacHistoryDao dao;

	/**
	 * 说明：分页查询
	 */
	@Test
	public void testPageSelect(){
		Page<MacHistoryBo, MacHistoryBo> page = new Page<MacHistoryBo, MacHistoryBo>();
		MacHistoryBo params = new MacHistoryBo();
		String startTime = "2014-09-01 00:00:00";
		String endTime = "2014-09-13 00:00:00";
//		params.setMac("cc");
		params.setStartTime(startTime);
		params.setEndTime(endTime);
		
		page.setStartRow(0);
		page.setRowCount(10);
		page.setCondition(params);
		
		dao.selectByPage(page);
		
//		Assert.assertNotEquals(0, page.getTotalRecord());
		
		System.out.println("每页条数："+page.getRowCount());
		System.out.println("总条数："+page.getTotalRecord());
	}
	
}