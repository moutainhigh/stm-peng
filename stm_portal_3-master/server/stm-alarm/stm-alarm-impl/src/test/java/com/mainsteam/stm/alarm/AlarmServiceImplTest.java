package  com.mainsteam.stm.alarm;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional 
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class AlarmServiceImplTest {
	@Autowired AlarmService alarmService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	@Test
	public void testNotify(){
		AlarmSenderParamter paramter=new AlarmSenderParamter();
		paramter.setRuleType(AlarmRuleProfileEnum.model_profile);
		paramter.setDefaultMsg("testestestset");
		paramter.setGenerateTime(new Date());
		paramter.setLevel(InstanceStateEnum.SERIOUS);
		paramter.setSysID(SysModuleEnum.MONITOR);
		paramter.setProfileID(1231);
		paramter.setSourceID("1111");
		paramter.setSysID(SysModuleEnum.MONITOR);
		paramter.setSourceName("testName");
		paramter.setSourceIP("127.0.0.1");
		paramter.setExt2("ext2");
		paramter.setProvider(AlarmProviderEnum.OC4);
		
		alarmService.notify(paramter);
	}
}
