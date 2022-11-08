/**
 * 
 */
package com.mainsteam.stm.route;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author ziw
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class ProxyConnectionTest {
	
}
