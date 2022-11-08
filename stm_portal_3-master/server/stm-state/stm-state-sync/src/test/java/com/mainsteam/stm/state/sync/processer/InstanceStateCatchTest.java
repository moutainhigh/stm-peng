package com.mainsteam.stm.state.sync.processer;

import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.sync.processer.InstanceStateCatchMBean;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class InstanceStateCatchTest {
	
	@Autowired InstanceStateCatchMBean instanceStateCatch;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testCatchState(){
		List<InstanceStateData>  list=instanceStateCatch.catchState();
		
		System.out.println("result:"+JSON.toJSONString(list));
	}

}
