package test;


import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ContextConfiguration({
	"classpath:META-INF/services/spring-mvc.xml",
	"classpath:META-INF/services/public-mybatis-beans.xml",
	"classpath:META-INF/services/public-jpa-beans.xml",
	"classpath:META-INF/services/portal-platform-beans.xml",
	"classpath*:META-INF/services/public-portal-topo-beans.xml"})
public class AbstractContextControllerTests {

	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).build();
	}
}