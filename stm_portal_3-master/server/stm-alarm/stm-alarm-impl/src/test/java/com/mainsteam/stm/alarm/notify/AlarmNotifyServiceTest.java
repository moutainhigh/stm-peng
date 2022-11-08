package com.mainsteam.stm.alarm.notify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmNotify;
import com.mainsteam.stm.alarm.obj.NotifyState;
import com.mainsteam.stm.alarm.obj.NotifyTypeEnum;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;

@TransactionConfiguration
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class AlarmNotifyServiceTest {
	private Log logger=LogFactory.getLog(AlarmNotifyServiceTest.class);

	@Autowired private AlarmNotifyService alarmNotifyService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	
	@Test
	public void testAdd(){
		AlarmNotify notify=new AlarmNotify();
		notify.setNotifyID(32456l);
		notify.setSourceType("OC4");
		notify.setNotifyAddr("heshencao@qq.com");
		notify.setAlarmID(3234234);
		notify.setNotifyTime(new Date());
		notify.setNotifyType(SendWayEnum.email);
		notify.setNotifyUserID(123123);
		notify.setNotifyContent("测试测试");
		//alarmNotifyService.addNotify(notify,"",NotifyWaiteType.immediate);
	}
	@Test
	public void testUpdateNotifyState(){
		List<Long> notifyIDes=new ArrayList<>();
		notifyIDes.add(234l);
		alarmNotifyService.updateNotifyState(notifyIDes,NotifyState.SUCCESS);
	}
	@Test
	public void testFind(){
		List<AlarmNotify> list=alarmNotifyService.findByAlarmID(3234234);
		logger.info("testFind:"+JSON.toJSONString(list));
	}
	@Test
	public void testGetNotifyByID(){
		AlarmNotify list=alarmNotifyService.getNotifyByID(134003l);
		logger.info("testFind:"+JSON.toJSONString(list));
	}
	
	@Test
	public void testFindByTime(){
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.MONTH, 0);
		
		List<AlarmNotify> list=alarmNotifyService.findByTime(NotifyTypeEnum.email,null, cal.getTime(), null);
		logger.info("testFind:"+JSON.toJSONString(list));
	}
}
