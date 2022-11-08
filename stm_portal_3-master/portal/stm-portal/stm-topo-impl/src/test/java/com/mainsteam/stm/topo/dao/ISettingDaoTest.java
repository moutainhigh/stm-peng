package com.mainsteam.stm.topo.dao;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;

import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class ISettingDaoTest extends AbstractDaoest{
	
	@Resource(name="settingDao")
	ISettingDao dao;

	@Test
	public void testInsert() {
		SettingBo settingBo = new SettingBo();
		settingBo.setKey("dao-testKey");
		settingBo.setValue("jsonValTest");
		int count = dao.save(settingBo);
		
		assertEquals(1,count);
		System.out.println("影响条数"+count);
	}

}


