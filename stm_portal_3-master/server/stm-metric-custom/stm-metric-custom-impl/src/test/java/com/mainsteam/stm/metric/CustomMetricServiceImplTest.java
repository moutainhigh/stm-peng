/**
 * 
 */
package com.mainsteam.stm.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CollectMeticSetting;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import com.mainsteam.stm.metric.obj.CustomMetricCollectParameter;
import com.mainsteam.stm.metric.obj.CustomMetricDataProcess;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.obj.CustomMetricQuery;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.metric.objenum.CustomMetricDataProcessWayEnum;
import com.mainsteam.stm.metric.service.impl.CustomMetricServiceInnerInterface;

/**
 * @author ziw
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:META-INF/services/portal-jpa-beans.xml",
		 		"classpath:META-INF/services/portal-mybatis-beans.xml",
		 		"classpath:portal-metric-custom-beans.xml" })
public class CustomMetricServiceImplTest extends DataSourceBasedDBTestCase {

	@Resource(name = "defaultDataSource")
	private DataSource source;

	@Autowired
	private CustomMetricServiceInnerInterface customMetricService;

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "src/test/config/cap_libs");
		System.setProperty("testCase", "true");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.DatabaseTestCase#getSetUpOperation()
	 */
	@Override
	public DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.DatabaseTestCase#getTearDownOperation()
	 */
	@Override
	public DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.NONE;
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#createCustomMetric(com.mainsteam.stm.metric.obj.CustomMetric)}
	 * .
	 */
	@Test
	public void testCreateCustomMetric() {
		CustomMetric metric = new CustomMetric();
		CustomMetricInfo info = new CustomMetricInfo();
		info.setAlert(true);
		info.setFlapping(2);
		info.setFreq(FrequentEnum.hour1);
		info.setMonitor(true);
		info.setName("customCpuRate");
		info.setStyle(MetricTypeEnum.PerformanceMetric);
		info.setUnit("%");
		List<CustomMetricCollectParameter> collectParameters = new ArrayList<CustomMetricCollectParameter>(
				2);
		CustomMetricCollectParameter methodParam = new CustomMetricCollectParameter();
		methodParam.setParameterKey("method");
		methodParam.setParameterType("");
		methodParam.setParameterValue("walk");
		methodParam.setPluginId("SnmpPlugin");
		collectParameters.add(methodParam);

		CustomMetricCollectParameter oidParam = new CustomMetricCollectParameter();
		oidParam.setParameterKey("");
		oidParam.setParameterType("");
		/* cpuRate */
		oidParam.setParameterValue("1.3.6.1.4.1.207.8.4.4.3.3.5");
		oidParam.setPluginId("SnmpPlugin");
		collectParameters.add(oidParam);
		metric.setCustomMetricCollectParameters(collectParameters);

		CustomMetricDataProcess dataProcess = new CustomMetricDataProcess();
		dataProcess.setDataProcessWay(CustomMetricDataProcessWayEnum.MAX);
		dataProcess.setPluginId("SnmpPlugin");
		metric.setCustomMetricDataProcess(dataProcess);
		metric.setCustomMetricInfo(info);

		List<CustomMetricThreshold> thresholds = new ArrayList<>();
		CustomMetricThreshold yellow = new CustomMetricThreshold();
		yellow.setMetricState(MetricStateEnum.WARN);
		yellow.setOperator(OperatorEnum.Great);
		yellow.setThresholdValue("10");
		thresholds.add(yellow);

		CustomMetricThreshold red = new CustomMetricThreshold();
		red.setMetricState(MetricStateEnum.CRITICAL);
		red.setOperator(OperatorEnum.Great);
		red.setThresholdValue("50");
		thresholds.add(red);
		metric.setCustomMetricThresholds(thresholds);
		String metricId = null;
		try {
			metricId = customMetricService.createCustomMetric(metric);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(metricId);
		try {
			ITable table = getConnection().createTable("stm_custom_metric_main");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			boolean isFind = false;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i,
						"customMetricId");
				if (metricId.equals(selectMetricId)) {
					assertEquals(info.getFlapping(), ((Integer) table.getValue(
							i, "flapping")).intValue());
					assertEquals(info.getName(),
							table.getValue(i, "customMetricName"));
					assertEquals(info.getUnit(),
							table.getValue(i, "customMetricUnit"));
					assertEquals(info.getFreq().name(),
							table.getValue(i, "freq"));
					assertEquals(info.getStyle().name(),
							table.getValue(i, "customMetricStyle"));
					assertEquals(
							info.isAlert(),
							((Integer) table.getValue(i, "isAlert")).intValue() == 1);
					assertEquals(info.isMonitor(), ((Integer) table.getValue(i,
							"isMonitor")).intValue() == 1);
					isFind = true;
					break;
				}
			}
			assertTrue(isFind);

			table = getConnection().createTable("stm_custom_metric_data_way");
			rowCount = table.getRowCount();
			assert (rowCount >= 1);
			isFind = false;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i, "metricId");
				String pluginId = (String) table.getValue(i, "pluginId");
				if (metricId.equals(selectMetricId)
						&& pluginId.equals(dataProcess.getPluginId())) {
					assertEquals(dataProcess.getDataProcessWay().name(),
							table.getValue(i, "dataWay"));
					isFind = true;
					break;
				}
			}
			assertTrue(isFind);

			table = getConnection().createTable("stm_custom_metric_threshold");
			rowCount = table.getRowCount();
			assert (rowCount >= thresholds.size());
			for (CustomMetricThreshold customMetricThreshold : thresholds) {
				isFind = false;
				for (int i = 0; i < rowCount; i++) {
					String selectMetricId = (String) table.getValue(i,
							"metricId");
					String metricState = (String) table.getValue(i,
							"metricState");
					if (metricId.equals(selectMetricId)
							&& metricState.equals(customMetricThreshold
									.getMetricState().name())) {
						assertEquals(customMetricThreshold.getOperator()
								.toString(), table.getValue(i,
								"expressionOperator"));
						assertEquals(customMetricThreshold.getThresholdValue(),
								table.getValue(i, "thresholdValue"));
						isFind = true;
						break;
					}
				}
				assertTrue(isFind);
			}

			table = getConnection().createTable("stm_custom_metric_collect");
			rowCount = table.getRowCount();
			assert (rowCount >= collectParameters.size());
			for (CustomMetricCollectParameter p : collectParameters) {
				isFind = false;
				for (int i = 0; i < rowCount; i++) {
					String selectMetricId = (String) table.getValue(i,
							"metricId");
					String pluginId = (String) table.getValue(i, "pluginId");
					String paramKey = (String) table.getValue(i, "paramKey");
					String paramType = (String) table.getValue(i, "paramType");
					String paramValue = (String) table
							.getValue(i, "paramValue");
					if (metricId.equals(selectMetricId)
							&& pluginId.equals(p.getPluginId())
							&& paramKey.equals(p.getParameterKey())
							&& paramType.equals(p.getParameterType())
							&& paramValue.equals(p.getParameterValue())) {
						isFind = true;
						break;
					}
				}
				assertTrue(isFind);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#getCustomMetric(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetCustomMetric() {
		String metricId = "test1";
		CustomMetric customMetric = null;
		try {
			customMetric = customMetricService.getCustomMetric(metricId);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(customMetric);
		assertNotNull(customMetric.getCustomMetricInfo());
		assertNotNull("test1Name", customMetric.getCustomMetricInfo().getName());
		assertNotNull("PerformanceMetric", customMetric.getCustomMetricInfo()
				.getStyle().name());
		assertNotNull(customMetric.getCustomMetricThresholds());
		assertEquals(2, customMetric.getCustomMetricThresholds().size());
		assertNotNull(customMetric.getCustomMetricDataProcess());
		assertEquals("MAX", customMetric.getCustomMetricDataProcess()
				.getDataProcessWay().name());
		assertNotNull(customMetric.getCustomMetricCollectParameters());
		assertEquals(2, customMetric.getCustomMetricCollectParameters().size());
		assertEquals("walk", customMetric.getCustomMetricCollectParameters()
				.get(0).getParameterValue());
		assertEquals("1.3.6.1.4.1.207.8.4.4.3.3.5", customMetric
				.getCustomMetricCollectParameters().get(1).getParameterValue());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#getCustomMetrics(com.mainsteam.stm.metric.obj.CustomMetricInfo, int, int)}
	 * .
	 */
	@Test
	public void testGetCustomMetrics() throws CustomMetricException {
		CustomMetricQuery query = new CustomMetricQuery();
		query.setCustomMetricName("test1Name");
		// query.setIsMonitor(true);
		List<CustomMetric> customMetrics = null;
		customMetrics = customMetricService.getCustomMetrics(query, 0, 10);
		assertNotNull(customMetrics);
		assertEquals(1, customMetrics.size());
		for (CustomMetric customMetric : customMetrics) {
			assertEquals(query.getCustomMetricName(), customMetric
					.getCustomMetricInfo().getName());
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#getCustomMetricsByInstanceId(long)}
	 * .
	 */
	@Test
	public void testGetCustomMetricsByInstanceId() {
		List<CustomMetric> customMetrics = null;
		try {
			customMetrics = customMetricService
					.getCustomMetricsByInstanceId(101);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(customMetrics);
		assertEquals(1, customMetrics.size());
		CustomMetric customMetric = customMetrics.get(0);
		assertEquals("test1", customMetric.getCustomMetricInfo().getId());
		assertNotNull(customMetric);
		assertNotNull(customMetric.getCustomMetricInfo());
		assertNotNull("test1Name", customMetric.getCustomMetricInfo().getName());
		assertNotNull("PerformanceMetric", customMetric.getCustomMetricInfo()
				.getStyle().name());
		assertNotNull(customMetric.getCustomMetricThresholds());
		assertEquals(2, customMetric.getCustomMetricThresholds().size());
		assertNotNull(customMetric.getCustomMetricDataProcess());
		assertEquals("MAX", customMetric.getCustomMetricDataProcess()
				.getDataProcessWay().name());
		assertNotNull(customMetric.getCustomMetricCollectParameters());
		assertEquals(2, customMetric.getCustomMetricCollectParameters().size());
		assertEquals("walk", customMetric.getCustomMetricCollectParameters()
				.get(0).getParameterValue());
		assertEquals("1.3.6.1.4.1.207.8.4.4.3.3.5", customMetric
				.getCustomMetricCollectParameters().get(1).getParameterValue());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#updateCustomMericSetting(com.mainsteam.stm.metric.obj.CollectMeticSetting)}
	 * .
	 */
	@Test
	public void testUpdateCustomMericSetting() {
		CollectMeticSetting collectMeticSetting = new CollectMeticSetting();
		collectMeticSetting.setMetricId("test1");
		collectMeticSetting.setAlert(Boolean.FALSE);
		collectMeticSetting.setFlapping(1);
		collectMeticSetting.setFreq(FrequentEnum.min1);
		collectMeticSetting.setMonitor(Boolean.FALSE);
		try {
			customMetricService.updateCustomMericSetting(collectMeticSetting);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		try {
			ITable table = getConnection().createTable("stm_custom_metric_main");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			boolean isFind = false;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i,
						"customMetricId");
				if (collectMeticSetting.getMetricId().equals(selectMetricId)) {
					assertEquals(collectMeticSetting.getFlapping().intValue(),
							((Integer) table.getValue(i, "flapping"))
									.intValue());
					assertEquals(collectMeticSetting.getFreq().name(),
							table.getValue(i, "freq"));
					assertEquals(
							collectMeticSetting.getAlert().booleanValue(),
							((Integer) table.getValue(i, "isAlert")).intValue() == 1);
					assertEquals(collectMeticSetting.getMonitor()
							.booleanValue(), ((Integer) table.getValue(i,
							"isMonitor")).intValue() == 1);
					isFind = true;
					break;
				}
			}
			assertTrue(isFind);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		/**
		 * 查询一次，加载到缓存
		 */
		CustomMetric customMetric = null;
		try {
			customMetric = customMetricService
					.getCustomMetric(collectMeticSetting.getMetricId());
		} catch (CustomMetricException e) {
			e.printStackTrace();
		}
		assertNotNull(customMetric);
		System.out.println(customMetricService.getClass());
		customMetric = customMetricService.getCustomMetricCache()
				.getCustomMetric(collectMeticSetting.getMetricId());
		assertNotNull(customMetric);

		collectMeticSetting.setMetricId("test1");
		collectMeticSetting.setAlert(Boolean.TRUE);
		collectMeticSetting.setFlapping(3);
		collectMeticSetting.setFreq(FrequentEnum.min5);
		collectMeticSetting.setMonitor(Boolean.TRUE);
		try {
			customMetricService.updateCustomMericSetting(collectMeticSetting);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		/**
		 * 验证缓存是否被修改
		 */
		try {
			customMetric = customMetricService
					.getCustomMetric(collectMeticSetting.getMetricId());
		} catch (CustomMetricException e) {
			e.printStackTrace();
		}
		assertNotNull(customMetric);
		CustomMetric cachedCustomMetric = customMetricService
				.getCustomMetricCache().getCustomMetric(
						collectMeticSetting.getMetricId());
		assertNotNull(cachedCustomMetric);
		assertNotNull(customMetric.getCustomMetricInfo());
		assertEquals(customMetric.getCustomMetricInfo().getId(),
				cachedCustomMetric.getCustomMetricInfo().getId());
		assertEquals(customMetric.getCustomMetricInfo().getName(),
				cachedCustomMetric.getCustomMetricInfo().getName());
		assertEquals(customMetric.getCustomMetricInfo().getUnit(),
				cachedCustomMetric.getCustomMetricInfo().getUnit());
		assertEquals(customMetric.getCustomMetricInfo().getFlapping(),
				cachedCustomMetric.getCustomMetricInfo().getFlapping());
		assertEquals(customMetric.getCustomMetricInfo().isAlert(),
				cachedCustomMetric.getCustomMetricInfo().isAlert());
		assertEquals(customMetric.getCustomMetricInfo().isMonitor(),
				cachedCustomMetric.getCustomMetricInfo().isMonitor());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#getCustomMetricCollectsByMetricId(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetCustomMetricCollectsByMetricId() {
		List<CustomMetricCollectParameter> collectParameters = null;
		try {
			collectParameters = customMetricService
					.getCustomMetricCollectsByMetricId("test1");
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(collectParameters);
		assertEquals(2, collectParameters.size());
		assertEquals("walk", collectParameters.get(0).getParameterValue());
		assertEquals("1.3.6.1.4.1.207.8.4.4.3.3.5", collectParameters.get(1)
				.getParameterValue());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#getCustomMetricCollectsByInstanceId(long)}
	 * .
	 */
	@Test
	public void testGetCustomMetricCollectsByInstanceId() {
		List<CustomMetricCollectParameter> collectParameters = null;
		try {
			collectParameters = customMetricService
					.getCustomMetricCollectsByInstanceId(101);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(collectParameters);
		assertEquals(2, collectParameters.size());
		assertEquals("walk", collectParameters.get(0).getParameterValue());
		assertEquals("1.3.6.1.4.1.207.8.4.4.3.3.5", collectParameters.get(1)
				.getParameterValue());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#getCustomMetricBindsByMetricId(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetCustomMetricBindsByMetricId() {
		String metricId = "test1";
		List<CustomMetricBind> binds = null;
		try {
			binds = customMetricService
					.getCustomMetricBindsByMetricId(metricId);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(binds);
		assertTrue(binds.size() > 0);
		for (CustomMetricBind customMetricBind : binds) {
			assertEquals(metricId, customMetricBind.getMetricId());
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#updateCustomMericSettings(java.util.List)}
	 * .
	 */
	@Test
	public void testUpdateCustomMericSettings() {
		CollectMeticSetting collectMeticSetting = new CollectMeticSetting();
		collectMeticSetting.setMetricId("test1");
		collectMeticSetting.setAlert(Boolean.FALSE);
		collectMeticSetting.setFlapping(1);
		collectMeticSetting.setFreq(FrequentEnum.min1);
		collectMeticSetting.setMonitor(Boolean.FALSE);

		List<CollectMeticSetting> collectMeticSettings = new ArrayList<>();
		collectMeticSettings.add(collectMeticSetting);
		try {
			customMetricService.updateCustomMericSettings(collectMeticSettings);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		try {
			ITable table = getConnection().createTable("stm_custom_metric_main");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			boolean isFind = false;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i,
						"customMetricId");
				if (collectMeticSetting.getMetricId().equals(selectMetricId)) {
					assertEquals(collectMeticSetting.getFlapping().intValue(),
							((Integer) table.getValue(i, "flapping"))
									.intValue());
					assertEquals(collectMeticSetting.getFreq().name(),
							table.getValue(i, "freq"));
					assertEquals(
							collectMeticSetting.getAlert().booleanValue(),
							((Integer) table.getValue(i, "isAlert")).intValue() == 1);
					assertEquals(collectMeticSetting.getMonitor()
							.booleanValue(), ((Integer) table.getValue(i,
							"isMonitor")).intValue() == 1);
					isFind = true;
					break;
				}
			}
			assertTrue(isFind);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#updateCustomMetricBasicInfo(com.mainsteam.stm.metric.obj.CustomMetricInfo)}
	 * .
	 */
	@Test
	public void testUpdateCustomMetricBasicInfo() {
		String metricId = "test1";
		CustomMetricInfo info = new CustomMetricInfo();
		info.setAlert(false);
		info.setFlapping(200);
		info.setFreq(FrequentEnum.min30);
		info.setMonitor(false);
		info.setName("test1NameUpdate");
		info.setStyle(MetricTypeEnum.AvailabilityMetric);
		info.setUnit("second");
		info.setId(metricId);
		try {
			customMetricService.updateCustomMetricBasicInfo(info);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		try {
			ITable table = getConnection().createTable("stm_custom_metric_main");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			boolean isFind = false;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i,
						"customMetricId");
				if (metricId.equals(selectMetricId)) {
					assertEquals(info.getFlapping(), ((Integer) table.getValue(
							i, "flapping")).intValue());
					assertEquals(info.getName(),
							table.getValue(i, "customMetricName"));
					assertEquals(info.getUnit(),
							table.getValue(i, "customMetricUnit"));
					assertEquals(info.getFreq().name(),
							table.getValue(i, "freq"));
					assertFalse(info.getStyle().name()
							.equals(table.getValue(i, "customMetricStyle")));
					assertEquals(
							info.isAlert(),
							((Integer) table.getValue(i, "isAlert")).intValue() == 1);
					assertEquals(info.isMonitor(), ((Integer) table.getValue(i,
							"isMonitor")).intValue() == 1);
					isFind = true;
					break;
				}
			}
			assertTrue(isFind);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#deleteCustomMetric(java.lang.String)}
	 * .
	 */
	@Test
	public void testDeleteCustomMetricString() {
		String metricId = "test2";
		try {
			customMetricService.deleteCustomMetric(metricId);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		try {
			ITable table = getConnection().createTable("stm_custom_metric_main");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			int count = 0;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i,
						"customMetricId");
				if (metricId.equals(selectMetricId)) {
					count++;
				}
			}
			assertEquals(0, count);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#deleteCustomMetric(java.util.List)}
	 * .
	 */
	@Test
	public void testDeleteCustomMetricListOfString() {
		List<String> metricIds = Arrays.asList("test1", "test2");
		try {
			customMetricService.deleteCustomMetric(metricIds);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		try {
			ITable table = getConnection().createTable("stm_custom_metric_main");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			int count = 0;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i, "metricId");
				for (String metricId : metricIds) {
					if (metricId.equals(selectMetricId)) {
						count++;
					}
				}
			}
			assertEquals(0, count);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#updateCustomMetricThreshold(java.util.List)}
	 * .
	 */
	@Test
	public void testUpdateCustomMetricThreshold() {
		String metricId = "test1";
		List<CustomMetricThreshold> customMetricThresholds = new ArrayList<>();
		CustomMetricThreshold customMetricThreshold = new CustomMetricThreshold();
		customMetricThreshold.setMetricId(metricId);
		customMetricThreshold.setMetricState(MetricStateEnum.SERIOUS);
		customMetricThreshold.setOperator(OperatorEnum.Equal);
		customMetricThreshold.setOperatorDesc("desc");
		customMetricThreshold.setThresholdValue("60");

		CustomMetricThreshold customMetricThreshold1 = new CustomMetricThreshold();
		customMetricThreshold1.setMetricId(metricId);
		customMetricThreshold1.setMetricState(MetricStateEnum.CRITICAL);
		customMetricThreshold1.setOperator(OperatorEnum.Equal);
		customMetricThreshold1.setOperatorDesc("desc");
		customMetricThreshold1.setThresholdValue("80");

		customMetricThresholds.add(customMetricThreshold);
		customMetricThresholds.add(customMetricThreshold1);
		try {
			customMetricService
					.updateCustomMetricThreshold(customMetricThresholds);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}

		try {
			ITable table = getConnection().createTable(
					"stm_custom_metric_threshold");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			int count = 0;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i, "metricId");
				String metricState = (String) table.getValue(i, "metricState");
				if (metricId.equals(selectMetricId)) {
					String expressionOperator = (String) table.getValue(i,
							"expressionOperator");
					String thresholdValue = (String) table.getValue(i,
							"thresholdValue");
					String expressionDesc = (String) table.getValue(i,
							"expressionDesc");
					if (metricState.equals(MetricStateEnum.SERIOUS.toString())) {
						assertEquals(expressionOperator, customMetricThreshold
								.getOperator().toString());
						assertEquals(thresholdValue,
								customMetricThreshold.getThresholdValue());
						assertEquals(expressionDesc,
								customMetricThreshold.getOperatorDesc());
						count++;
					} else if (metricState.equals(MetricStateEnum.CRITICAL
							.toString())) {
						assertEquals(expressionOperator, customMetricThreshold1
								.getOperator().toString());
						assertEquals(thresholdValue,
								customMetricThreshold1.getThresholdValue());
						assertEquals(expressionDesc,
								customMetricThreshold1.getOperatorDesc());
						count++;
					}
				}
			}
			assertEquals(customMetricThresholds.size(), count);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#getCustomMetricThresholds(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetCustomMetricThresholds() {
		String metricId = "test1";
		try {
			List<CustomMetricThreshold> customMetricThresholds = customMetricService
					.getCustomMetricThresholds(metricId);
			assertNotNull(customMetricThresholds);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#updateCustomMetricCollects(java.lang.String, java.lang.String, java.util.List)}
	 * .
	 */
	@Test
	public void testUpdateCustomMetricCollects() {
		List<CustomMetricCollectParameter> customMetricCollectParameters = new ArrayList<>();
		String metricId = "test1";
		String pluginId = "SnmpPlugin";
		CustomMetricCollectParameter customMetricCollectParameter = new CustomMetricCollectParameter();
		customMetricCollectParameter.setMetricId(metricId);
		customMetricCollectParameter.setParameterKey("method1");
		customMetricCollectParameter.setParameterType("type");
		customMetricCollectParameter.setParameterValue("get");
		customMetricCollectParameter.setPluginId(pluginId);

		CustomMetricCollectParameter customMetricCollectParameter1 = new CustomMetricCollectParameter();
		customMetricCollectParameter1.setMetricId(metricId);
		customMetricCollectParameter1
				.setParameterValue("1.3.6.1.4.1.207.8.4.4.3.3.6");
		customMetricCollectParameter1.setPluginId(pluginId);
		customMetricCollectParameters.add(customMetricCollectParameter1);
		try {
			customMetricService.updateCustomMetricCollects(metricId, pluginId,
					customMetricCollectParameters);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}

		try {
			ITable table = getConnection().createTable(
					"stm_custom_metric_collect");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			int count = 0;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i, "metricId");
				String selectPluginId = (String) table.getValue(i, "pluginId");
				if (metricId.equals(selectMetricId)
						&& pluginId.equals(selectPluginId)) {
					String paramKey = (String) table.getValue(i, "paramKey");
					String paramType = (String) table.getValue(i, "paramType");
					String paramValue = (String) table
							.getValue(i, "paramValue");

					if ("method1".equals(paramKey)) {
						if (paramType.equals(customMetricCollectParameter
								.getParameterType())) {
							if (paramValue.equals(customMetricCollectParameter
									.getParameterValue())) {
								count++;
							}
						}
					} else {
						if (paramValue.equals(customMetricCollectParameter1
								.getParameterValue())) {
							count++;
						}
					}
				}
			}
			assertEquals(customMetricCollectParameters.size(), count);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#getCustomMetricCollects(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetCustomMetricCollects() {
		String metricId = "test1";
		String pluginId = "SnmpPlugin";
		try {
			List<CustomMetricCollectParameter> customMetricCollectParameters = customMetricService
					.getCustomMetricCollects(metricId, pluginId);
			assertNotNull(customMetricCollectParameters);
			assertEquals(2, customMetricCollectParameters.size());
			assertEquals("walk", customMetricCollectParameters.get(0)
					.getParameterValue());
			assertEquals("1.3.6.1.4.1.207.8.4.4.3.3.5",
					customMetricCollectParameters.get(1).getParameterValue());
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#addCustomMetricBinds(java.util.List)}
	 * .
	 */
	@Test
	public void testAddCustomMetricBinds() {
		List<CustomMetricBind> customBinds = new ArrayList<>();
		CustomMetricBind customMetricBind = new CustomMetricBind();
		customMetricBind.setInstanceId(103);
		customMetricBind.setMetricId("custom3");
		customMetricBind.setPluginId("SnmpPlugin");
		customBinds.add(customMetricBind);

		CustomMetricBind customMetricBind1 = new CustomMetricBind();
		customMetricBind1.setInstanceId(104);
		customMetricBind1.setMetricId("custom4");
		customMetricBind1.setPluginId("SnmpPlugin");
		customBinds.add(customMetricBind1);
		try {
			customMetricService.addCustomMetricBinds(customBinds);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}

		try {
			ITable table = getConnection().createTable("stm_custom_metric_bind");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			int count = 0;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i, "metricId");
				String selectPluginId = (String) table.getValue(i, "pluginId");
				long instanceId = Long.parseLong(table
						.getValue(i, "instanceId").toString());
				for (CustomMetricBind bind : customBinds) {
					if (bind.getMetricId().equals(selectMetricId)
							&& bind.getPluginId().equals(selectPluginId)
							&& instanceId == bind.getInstanceId()) {
						count++;
					}
				}
			}
			assertEquals(customBinds.size(), count);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#deleteCustomMetricBinds(java.util.List)}
	 * .
	 */
	@Test
	public void testDeleteCustomMetricBinds() {
		List<CustomMetricBind> customBinds = new ArrayList<>();
		CustomMetricBind customMetricBind = new CustomMetricBind();
		customMetricBind.setInstanceId(101);
		customMetricBind.setMetricId("custom2");
		customMetricBind.setPluginId("SnmpPlugin");
		customBinds.add(customMetricBind);

		CustomMetricBind customMetricBind1 = new CustomMetricBind();
		customMetricBind1.setInstanceId(102);
		customMetricBind1.setMetricId("custom2");
		customMetricBind1.setPluginId("SnmpPlugin");
		customBinds.add(customMetricBind1);
		try {
			customMetricService.deleteCustomMetricBinds(customBinds);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}

		try {
			ITable table = getConnection().createTable("stm_custom_metric_bind");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			boolean isFind = false;
			start: for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i, "metricId");
				String selectPluginId = (String) table.getValue(i, "pluginId");
				long instanceId = Long.parseLong(table
						.getValue(i, "instanceId").toString());
				for (CustomMetricBind bind : customBinds) {
					if (bind.getMetricId().equals(selectMetricId)
							&& bind.getPluginId().equals(selectPluginId)
							&& instanceId == bind.getInstanceId()) {
						isFind = true;
						break start;
					}
				}

			}
			assertFalse(isFind);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#clearCustomMetricBinds(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testclearCustomMetricBinds() {
		String metricId = "custom2";
		String pluginId = "SnmpPlugin";
		try {
			customMetricService.clearCustomMetricBinds(metricId, pluginId);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}
		try {
			ITable table = getConnection().createTable("stm_custom_metric_bind");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			boolean isFind = false;
			for (int i = 0; i < rowCount; i++) {
				String selectMetricId = (String) table.getValue(i, "metricId");
				String selectPluginId = (String) table.getValue(i, "pluginId");
				if (metricId.equals(selectMetricId)
						&& pluginId.equals(selectPluginId)) {
					isFind = true;
					break;
				}
			}
			assertFalse(isFind);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricServiceImpl#clearCustomMetricBindsByInstanceIds(java.util.List)}
	 * .
	 */
	@Test
	public void testClearCustomMetricBindsByInstanceIds() {
		List<Long> instanceIds = Arrays.asList((long) 102, (long) 101);
		try {
			customMetricService
					.clearCustomMetricBindsByInstanceIds(instanceIds);
		} catch (CustomMetricException e) {
			e.printStackTrace();
			fail();
		}

		try {
			ITable table = getConnection().createTable("stm_custom_metric_bind");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			boolean isFind = false;
			start: for (int i = 0; i < rowCount; i++) {
				long instanceId = Long.parseLong(table
						.getValue(i, "instanceId").toString());
				for (long tempInstanceId : instanceIds) {
					if (instanceId == tempInstanceId) {
						isFind = true;
						break start;
					}
				}
			}
			assertFalse(isFind);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetCustomMetricCount() {
		CustomMetricQuery customMetricQuery = null;
		int count = customMetricService
				.getCustomMetricsCount(customMetricQuery);
		assertTrue(count > 0);
		try {
			ITable table = getConnection().createTable("stm_custom_metric_main");
			int rowCount = table.getRowCount();
			assertEquals(rowCount, count);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		customMetricQuery = new CustomMetricQuery();
		customMetricQuery.setCustomMetricName("test1%");
		count = customMetricService.getCustomMetricsCount(customMetricQuery);
		assertEquals(1, count);
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/custom_metric_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}
}
