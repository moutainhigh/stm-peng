package com.mainsteam.stm.capvalidate;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:META-INF/services/public-capvalidate-beans.xml" })
public class CapacityValidateTest {
	
	private static final Log logger = LogFactory.getLog(CapacityValidateTest.class);

	@Resource
	private CapacityValidateService capacityValidateService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String username = System.getProperty("user.name");
		String oc4Path = "";
		if ("sunsht".equals(username)) {
			oc4Path = "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs";
		} else if ("Administrator".equals(username)) {
			oc4Path = "D://Download//ly//svn//Capacity//cap_libs";
		} else {
			oc4Path = "D:\\OneCenter4\\Capacity\\cap_libs";
		}
		System.setProperty("caplibs.path", oc4Path);

		PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidateSchema() {
		String oc4Path = System.getProperty("caplibs.path");
		List<String> errors = capacityValidateService.validateSchema(oc4Path);
		if(null == errors || errors.size() == 0){
			System.out.println("格式检验成功");
		}else{
			for(String str : errors){
				System.err.println(str);
				logger.error(str);
			}
		}
		assertTrue(null == errors || errors.size() == 0);
	}
	
	@Test
	public void testValidateBusiness() {
		String oc4Path = System.getProperty("caplibs.path");
		List<String> errors = capacityValidateService.businessValidate(oc4Path);
		if(null == errors || errors.size() == 0){
			System.out.println("格式检验成功");
		}else{
			for(String str : errors){
				System.err.println(str);
				logger.error(str);
				System.err.println("=========================================================");
				logger.info("==============================================================");
			}
		}
		assertTrue(errors == null || errors.size() == 0);
	}
	
}
