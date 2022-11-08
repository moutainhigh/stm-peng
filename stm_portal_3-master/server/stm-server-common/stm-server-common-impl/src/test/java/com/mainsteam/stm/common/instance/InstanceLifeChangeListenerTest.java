package com.mainsteam.stm.common.instance;

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

import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class InstanceLifeChangeListenerTest {

	private static final Log logger=LogFactory.getLog(InstanceLifeChangeListenerTest.class);
	@Autowired InstanceLifeChangeListener instanceLifeChangeListener;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	
	
	@Test
	public void testInterceptor() throws Exception{
		ResourceInstance instance=new ResourceInstance();
		instance.setId(123123l);
		instance.setLifeState(InstanceLifeStateEnum.MONITORED);
		InstancelibEvent instancelibEvent=new InstancelibEvent(instance, instance, null);
		
		
		instanceLifeChangeListener.interceptor(instancelibEvent);
	}
}
