package com.mainsteam.stm.topo.service.impl;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;

import com.mainsteam.stm.topo.api.ISettingApi;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class SettingImplTest extends AbstractDaoest{

	@Resource(name="settingApi")
	private ISettingApi settingApi;
	
	@Test
	public void testAddSettingInfo() {
		SettingBo settingBo = new SettingBo();
		settingBo.setKey("service-testKey");
		settingBo.setValue("jsonValTest");
		int count = settingApi.addSettingInfo(settingBo);
		
		assertEquals(1,count);
		System.out.println("影响条数"+count);
	}

}


