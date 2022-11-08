/**
 * 
 */
package com.mainsteam.stm.metric.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeDO;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class CustomMetricChangeDAOImplTest extends DataSourceBasedDBTestCase {

	@Resource(name = "defaultDataSource")
	private DataSource source;

	@Resource
	private CustomMetricChangeDAO changeDAO;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase", "true");
		System.setProperty("caplibs.path", "..\\..\\..\\Capacity\\cap_libs");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.DatabaseTestCase#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.DatabaseTestCase#tearDown()
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
		config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.DatabaseTestCase#getSetUpOperation()
	 */
	@Override
	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dbunit.DatabaseTestCase#getTearDownOperation()
	 */
	@Override
	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.NONE;
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.dao.impl.CustomMetricChangeDAOImpl#addCustomMetricChangeDOs(java.util.List)}
	 * .
	 */
	@Test
	public void testAddCustomMetricChangeDOs() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		Date currentDate = c.getTime();
		CustomMetricChangeDO changeDo = new CustomMetricChangeDO();
		changeDo.setChange_id(999);
		changeDo.setChange_time(currentDate);
		changeDo.setInstance_id(100L);
		changeDo.setMetric_id("custom1");
		changeDo.setOccur_time(changeDo.getChange_time());
		changeDo.setOperate_state(1);
		changeDo.setOperateMode("add");
		changeDo.setPlugin_id("SnmpPlugin");
		List<CustomMetricChangeDO> changeDOs = new ArrayList<>(1);
		changeDOs.add(changeDo);
		changeDAO.addCustomMetricChangeDOs(changeDOs);
		try {
			ITable table = getConnection().createTable(
					"stm_custom_metric_change");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			boolean isFind = false;
			for (int i = 0; i < rowCount; i++) {
				long change_id = ((java.math.BigInteger) table.getValue(i,
						"change_id")).longValue();
				if (change_id == changeDo.getChange_id()) {
					assertEquals(changeDo.getMetric_id(),
							table.getValue(i, "metric_id"));
					assertEquals((Integer) changeDo.getOperate_state() == 1,
							table.getValue(i, "operate_state"));
					assertEquals(changeDo.getOperateMode(),
							table.getValue(i, "operateMode"));
					assertEquals(changeDo.getChange_time(),
							table.getValue(i, "occur_time"));
					assertEquals(changeDo.getInstance_id().longValue(),
							((java.math.BigInteger) table.getValue(i,
									"instance_id")).longValue());
					assertEquals(changeDo.getOccur_time(),
							table.getValue(i, "occur_time"));
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
	 * {@link com.mainsteam.stm.metric.dao.impl.CustomMetricChangeDAOImpl#selectChangeDOsWithNotApply(int)}
	 * .
	 */
	@Test
	public void testSelectChangeDOsWithNotApply() {
		int count = 2;
		List<CustomMetricChangeDO> changeDOs = changeDAO
				.selectChangeDOsWithNotApply(count);
		assertNotNull(changeDOs);
		assertTrue(changeDOs.size() > 0);
		assertEquals(count, changeDOs.size());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.dao.impl.CustomMetricChangeDAOImpl#updateCustomMetricChangeDOToApply(long[])}
	 * .
	 */
	@Test
	public void testUpdateCustomMetricChangeDOToApply() {
		long[] change_ids = { 100, 101 };
		changeDAO.updateCustomMetricChangeDOToApply(change_ids);
		try {
			ITable table = getConnection().createTable(
					"stm_custom_metric_change");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			int isFind = 0;
			for (int i = 0; i < rowCount; i++) {
				long change_id = ((java.math.BigInteger) table.getValue(i,
						"change_id")).longValue();
				if (Arrays.binarySearch(change_ids, change_id) >= 0) {
					assertEquals(true, table.getValue(i, "operate_state"));
					isFind++;
				}
			}
			assertEquals(change_ids.length, isFind);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
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
