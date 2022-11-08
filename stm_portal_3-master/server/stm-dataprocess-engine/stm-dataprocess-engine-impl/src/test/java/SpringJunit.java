import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.dataprocess.engine.MetricDataProcessEngineImpl;

/**
 * 
 */

/**
 * @author ziw
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class SpringJunit {
	
	@Resource
	private MetricDataProcessEngineImpl dataProcessEngineImpl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}

	@Test
	public void test(){
		assertNotNull(dataProcessEngineImpl);
		System.out.println("sucess.");
		dataProcessEngineImpl.handle(null);
	}
}
