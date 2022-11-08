package com.mainsteam.stm.common.sync.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.common.sync.DataSyncTypeEnum;

@TransactionConfiguration
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class AlarmSyncDAOTest {
	@Autowired DataSyncDAO alarmSyncDAO;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	@Test
	public void testUpdateForRunning(){
		List<Long> ides=new ArrayList<>();
		ides.add(123l);
		alarmSyncDAO.updateForRunning(ides);
	}
	
	@Test
	public void testCatchOne(){
		alarmSyncDAO.catchOne(DataSyncTypeEnum.ALARM);
	}
	
	@Test
	public void testCatchList(){
		alarmSyncDAO.catchList(DataSyncTypeEnum.ALARM);
	}
}
