package com.mainsteam.stm.alarm.soap.itsm;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;



@TransactionConfiguration
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class AlarmEventSyncItsmTest {
	@Autowired AlarmEventSyncItsm alarmEventSyncItsm;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
 	@Test
	 public void testHandle(){
 		AlarmEvent event=new AlarmEvent();
 		event.setEventID(123123l);
 		event.setLevel(InstanceStateEnum.CRITICAL);
 		event.setCollectionTime(new Date());
		 alarmEventSyncItsm.handleEvent(event);
	 }
}
