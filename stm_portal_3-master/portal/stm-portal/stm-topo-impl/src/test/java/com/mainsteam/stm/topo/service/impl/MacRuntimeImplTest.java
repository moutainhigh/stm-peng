package com.mainsteam.stm.topo.service.impl;

import javax.annotation.Resource;

import org.junit.Test;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.api.IMacApi;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class MacRuntimeImplTest extends AbstractDaoest{

	@Resource(name="macApi")
	private IMacApi macApi;
	
	@Test
	public void testGetRuntimeListByPage() {
		Page<MacRuntimeBo, MacRuntimeBo> page = new Page<MacRuntimeBo, MacRuntimeBo>();
		MacRuntimeBo params = new MacRuntimeBo();
		params.setSearchType(1l);
//		params.setSearchVal("118.1");//172.16.118.1
		
		page.setStartRow(0);
		page.setRowCount(10);
		page.setCondition(params);
		
		macApi.selectMacRuntimeByPage(page);
		System.out.println("条数:"+page.getTotalRecord());
	}
	
}