package com.mainsteam.stm.alarm;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional 
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml" })
public class AlarmServiceSyncTest {
	
	@Autowired AlarmServiceSync alarmServiceSync;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	@Test
	public void testNotify(){
		String json="{'defaultMsg':'(juniper)172.16.10.254配置文件/startup-config发生变更','ext0':'7436501','ext1':'7447503','ext2':'startup-config_14.12.09_09.58.txt','ext3':'startup-config_14.12.09_10.41.txt','generateTime':1418092880161,'level':'WARN','profileID':0,'provider':'OC4','ruleType':'configfile_manager','sourceID':'7892152','sourceIP':'172.16.10.254','sourceName':'juniper','sysID':'CONFIG_MANAGER'}";
		AlarmSenderParamter paramter=JSON.parseObject(json, AlarmSenderParamter.class);
		alarmServiceSync.notify(paramter);
	}

}
