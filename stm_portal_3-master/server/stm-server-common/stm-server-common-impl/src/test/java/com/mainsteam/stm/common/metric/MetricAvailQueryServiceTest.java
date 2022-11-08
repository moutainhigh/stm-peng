//package com.mainsteam.stm.common.metric;
//
//
//import java.util.List;
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//import org.dbunit.DataSourceBasedDBTestCase;
//import org.dbunit.database.DatabaseConfig;
//import org.dbunit.dataset.IDataSet;
//import org.dbunit.dataset.xml.FlatXmlDataSet;
//import org.dbunit.dataset.xml.FlatXmlProducer;
//import org.dbunit.operation.DatabaseOperation;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.transaction.TransactionConfiguration;
//import org.springframework.transaction.annotation.Transactional;
//import org.xml.sax.InputSource;
//
//import com.mainsteam.stm.common.metric.obj.AvailMetricData;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@TransactionConfiguration
//@Transactional
//@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
//		"classpath*:META-INF/services/server-processer-*-beans.xml" })
//public class MetricAvailQueryServiceTest extends DataSourceBasedDBTestCase{
//
//	@Resource(name = "metricAvailQueryService")
//	private MetricAvailQueryService metricAvailQueryService;
//	
//	@Resource(name = "defaultDataSource")
//	private DataSource source;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		System.setProperty("caplibs.path", "src/test/config/cap_libs");
//		System.setProperty("testCase","true");
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	@Test
//	public void testGetParentInsanceAvailMetricDatas() {
//		int nodeGroupId = 111111;
//		int start = 0;
//		int length = 2;
//		List<AvailMetricData> availMetricDatas = metricAvailQueryService.getParentInsanceAvailMetricDatas(nodeGroupId, start, length);
//		assertNotNull(availMetricDatas);
//		assertEquals(length,availMetricDatas.size());
//		for (AvailMetricData availMetricData : availMetricDatas) {
//			assertFalse(7777 == availMetricData.getInstanceId());
//			assertFalse(111112 == availMetricData.getInstanceId());
//			assertFalse(111113 == availMetricData.getInstanceId());
//		}
//		int oldLength = length;
//		length = Integer.MAX_VALUE;
//		availMetricDatas = metricAvailQueryService.getParentInsanceAvailMetricDatas(nodeGroupId, start, length);
//		assertNotNull(availMetricDatas);
//		assertTrue(availMetricDatas.size()>oldLength);
//		for (AvailMetricData availMetricData : availMetricDatas) {
//			assertFalse(7777 == availMetricData.getInstanceId());
//			assertFalse(111112 == availMetricData.getInstanceId());
//			assertFalse(111113 == availMetricData.getInstanceId());
//		}
//	}
//	
//	@Before
//	public void setUp() throws Exception {
//		super.setUp();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	protected void setUpDatabaseConfig(DatabaseConfig config) {
//		config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
//		config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
//	}
//
//	protected DatabaseOperation getSetUpOperation() throws Exception {
//		return DatabaseOperation.CLEAN_INSERT;
//	}
//
//	@Override
//	protected DataSource getDataSource() {
//		return source;
//	}
//
//	@Override
//	protected IDataSet getDataSet() throws Exception {
//		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
//				"src/test/resources/metricAvail_dataset.xml"));
//		IDataSet dataSet = new FlatXmlDataSet(producer);
//		return dataSet;
//	}
//}
