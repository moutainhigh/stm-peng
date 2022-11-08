import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.profilelib.auto.rediscover.AutoRediscoverInstance;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class AutoRediscoverInstanceImplTest {

	@Autowired AutoRediscoverInstance autoRediscoverService;
	
	@Test
	public void test() {
		autoRediscoverService.reDiscover(2049);
	}

}
