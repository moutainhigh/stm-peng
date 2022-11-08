package com.mainsteam.stm.dataprocess.bigData;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.dataprocess.bigData.UdpSenderForBigData;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class UdpSenderTest {
	UdpSenderForBigData sender=UdpSenderForBigData.getInstance();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../Capacity/cap_libs");
	}
	
	
	@Test
	public void testSendMsg(){
		
		sender.sendMsg("testest".getBytes(),UdpSenderForBigData.SYNC_DATA_ALARM);
		
	}

}
