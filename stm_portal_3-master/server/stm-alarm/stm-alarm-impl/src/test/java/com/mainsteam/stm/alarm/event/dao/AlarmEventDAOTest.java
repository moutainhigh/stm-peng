package com.mainsteam.stm.alarm.event.dao;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.alarm.event.dao.AlarmEventDAO;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.po.AlarmEventPO;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional 
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml" })
public class AlarmEventDAOTest {

	@Autowired AlarmEventDAO alarmEventDAO;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	@Test
	public void testRecoverAlarmEvent(){
		AlarmEventPO po=new AlarmEventPO();
		
		po.setContent("testestestset");
		po.setLevel(InstanceStateEnum.SERIOUS);
		po.setSysID(SysModuleEnum.MONITOR);
		po.setSourceID("1111");
		po.setSysID(SysModuleEnum.MONITOR);
		po.setSourceName("testName");
		po.setSourceIP("127.0.0.1");
		po.setExt2("ext2");
		po.setProvider(AlarmProviderEnum.OC4);
		
		
		alarmEventDAO.recoverAlarmEvent(po);
	}
	
	@Test
	public void testCount(){
		Calendar cal=Calendar.getInstance();
		Date start=cal.getTime();
		Date end=cal.getTime();
		List<String> sourceIDes=new ArrayList<String>();
		for(int i=0;i<1232;i++){
			sourceIDes.add(String.valueOf(i));
		}
		alarmEventDAO.countResourceEvent(sourceIDes, SysModuleEnum.MONITOR, new InstanceStateEnum[]{InstanceStateEnum.CRITICAL}, start, end, null);
	}
	
}
