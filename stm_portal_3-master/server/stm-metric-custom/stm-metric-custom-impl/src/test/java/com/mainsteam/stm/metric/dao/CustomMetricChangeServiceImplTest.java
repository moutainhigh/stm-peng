/**
 * 
 */
package com.mainsteam.stm.metric.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.mainsteam.stm.metric.CustomMetricChangeService;
import com.mainsteam.stm.metric.obj.CustomMetricChange;
import com.mainsteam.stm.metric.obj.CustomMetricChangeResult;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class CustomMetricChangeServiceImplTest extends
		DataSourceBasedDBTestCase {

	@Resource(name = "defaultDataSource")
	private DataSource source;

	@Resource(name = "customMetricChangeService")
	private CustomMetricChangeService changeService;

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
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricChangeServiceImpl#selectChangesWithNotApply(int)}
	 * .
	 */
	@Test
	public void testSelectChangesWithNotApply() {
		int count = 2;
		List<CustomMetricChange> changes = changeService
				.selectChangesWithNotApply(count);
		assertNotNull(changes);
		assertTrue(changes.size() > 0);
		assertEquals(count, changes.size());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricChangeServiceImpl#updateCustomMetricChangeToApply(long[])}
	 * .
	 */
	@Test
	public void testUpdateCustomMetricChangeToApply() {
		long[] change_ids = { 100, 101 };
		changeService.updateCustomMetricChangeToApply(change_ids);
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

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricChangeServiceImpl#getCustomMetricChangeResults(long[])}
	 * .
	 */
	@Test
	public void testGetCustomMetricChangeResults() {
		long[] change_ids = { 100, 101 };
		List<CustomMetricChangeResult> results = changeService
				.getCustomMetricChangeResults(change_ids);
		assertNotNull(results);
		assertEquals(4, results.size());
		int equalCount = 0;
		for (int i = 0; i < change_ids.length; i++) {
			for (CustomMetricChangeResult customMetricChangeResult : results) {
				if (customMetricChangeResult.getChangeId() == change_ids[i]) {
					equalCount++;
				}
			}
		}
		assertEquals(4, equalCount);
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricChangeServiceImpl#insertCustomMetricChangeResults(java.util.List)}
	 * .
	 */
	@Test
	public void testInsertCustomMetricChangeResults() {
		List<CustomMetricChangeResult> results = new ArrayList<>(2);
		Date d = new Date();
		long changeId = 900;
		CustomMetricChangeResult result = new CustomMetricChangeResult();
		result.setChangeId(changeId);
		result.setDcsGroupId(1);
		result.setOperateTime(d);
		result.setResultState(false);
		results.add(result);

		CustomMetricChangeResult result1 = new CustomMetricChangeResult();
		result1.setChangeId(changeId);
		result1.setDcsGroupId(2);
		result1.setOperateTime(d);
		result1.setResultState(true);
		results.add(result1);
		changeService.insertCustomMetricChangeResults(results);
		try {
			ITable table = getConnection().createTable(
					"stm_custom_metric_change_result");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			int isFind = 0;
			for (int i = 0; i < rowCount; i++) {
				long change_id = ((java.math.BigInteger) table.getValue(i,
						"change_id")).longValue();
				if (changeId == change_id) {
					if (((Integer) table.getValue(i, "dcs_group_id"))
							.intValue() == result.getDcsGroupId()) {
						assertEquals(result.isResultState(),
								((Boolean) table.getValue(i, "result_state"))
										.booleanValue());
					} else if (((Integer) table.getValue(i, "dcs_group_id"))
							.intValue() == result1.getDcsGroupId()) {
						assertEquals(result1.isResultState(),
								((Boolean) table.getValue(i, "result_state"))
										.booleanValue());
					}
					isFind++;
				}
			}
			assertEquals(results.size(), isFind);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricChangeServiceImpl#removeCustomMetricChanges(java.util.Date)}
	 * .
	 */
	@Test
	public void testRemoveCustomMetricChanges() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date expireDate = null;
		try {
			expireDate = dateFormat.parse("2014-11-2");
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
		changeService.removeCustomMetricChanges(expireDate);
		try {
			ITable table = getConnection().createTable(
					"stm_custom_metric_change");
			int rowCount = table.getRowCount();
			assertTrue(rowCount > 0);
			for (int i = 0; i < rowCount; i++) {
				assertTrue(expireDate.compareTo((Date) table.getValue(i,
						"occur_time")) < 0);
			}

			table = getConnection().createTable(
					"stm_custom_metric_change_result");
			rowCount = table.getRowCount();
			assertTrue(rowCount > 0);
			for (int i = 0; i < rowCount; i++) {
				assertTrue(expireDate.compareTo((Date) table.getValue(i,
						"operate_time")) < 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			expireDate = dateFormat.parse("2014-12-2");
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
		changeService.removeCustomMetricChanges(expireDate);
		try {
			ITable table = getConnection().createTable(
					"stm_custom_metric_change");
			int rowCount = table.getRowCount();
			assertTrue(rowCount <= 0);

			table = getConnection().createTable(
					"stm_custom_metric_change_result");
			rowCount = table.getRowCount();
			assertTrue(rowCount <= 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.CustomMetricChangeServiceImpl#updateCustomMetricChangeResults(java.util.List)}
	 * .
	 */
	@Test
	public void testUpdateCustomMetricChangeResults() {
		List<CustomMetricChangeResult> results = new ArrayList<>(2);
		Date d = new Date();
		CustomMetricChangeResult result = new CustomMetricChangeResult();
		result.setChangeId(100);
		result.setDcsGroupId(1);
		result.setOperateTime(d);
		result.setResultState(false);
		results.add(result);

		CustomMetricChangeResult result1 = new CustomMetricChangeResult();
		result1.setChangeId(101);
		result1.setDcsGroupId(1);
		result1.setOperateTime(d);
		result1.setResultState(true);
		results.add(result1);
		changeService.updateCustomMetricChangeResults(results);
		try {
			ITable table = getConnection().createTable(
					"stm_custom_metric_change_result");
			int rowCount = table.getRowCount();
			assert (rowCount >= 1);
			int isFind = 0;
			for (int i = 0; i < rowCount; i++) {
				long change_id = ((java.math.BigInteger) table.getValue(i,
						"change_id")).longValue();
				if (change_id == result.getChangeId()) {
					if (((Integer) table.getValue(i, "dcs_group_id"))
							.intValue() == result.getDcsGroupId()) {
						assertEquals(true, ((Boolean) table.getValue(i,
								"result_state")).booleanValue());
						isFind++;
					}
				} else if (change_id == result1.getChangeId()) {
					if (((Integer) table.getValue(i, "dcs_group_id"))
							.intValue() == result1.getDcsGroupId()) {
						assertEquals(true, ((Boolean) table.getValue(i,
								"result_state")).booleanValue());
						isFind++;
					}
				}
			}
			assertEquals(results.size(), isFind);
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
