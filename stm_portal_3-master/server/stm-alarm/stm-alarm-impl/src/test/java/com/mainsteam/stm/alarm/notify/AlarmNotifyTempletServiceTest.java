package com.mainsteam.stm.alarm.notify;

import java.util.ArrayList;
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

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
import com.mainsteam.stm.alarm.obj.AlarmNotifyTempletParamter;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;


//@TransactionConfiguration
//@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml" })
public class AlarmNotifyTempletServiceTest {
	private Log logger=LogFactory.getLog(AlarmNotifyTempletServiceTest.class);

	@Autowired private  AlarmNotifyTempletService alarmNotifyTempletService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	
	@Test
	public void testAdd(){
		AlarmNotifyTemplet tmp=new AlarmNotifyTemplet();
		tmp.setTmpID(123111);
		tmp.setLanguage("cn");
		tmp.setContent("123123123123123123123123");
		tmp.setProvider(AlarmProviderEnum.OC4);
		tmp.setUpdateTime(new Date());
		tmp.setSysID("mysql");
		List<AlarmNotifyTempletParamter> params=new ArrayList<>();
		for(int i=0;i<4;i++){
			AlarmNotifyTempletParamter p=new AlarmNotifyTempletParamter();
			p.setDesc("name");
			p.setName("name");
			p.setTmpID(tmp.getTmpID());
			
			params.add(p);
		}
		tmp.setParams(params);
		
		alarmNotifyTempletService.addTemplet(tmp);
	}
	
	@Test
	public void testFindTempletBySysID(){
		List<AlarmNotifyTemplet> list=alarmNotifyTempletService.findTempletBySysID(SysModuleEnum.MONITOR,AlarmProviderEnum.OC4);
		logger.info("findTempletBySysID:"+JSON.toJSONString(list));
	}
}
