package com.mainsteam.stm.profilelib;


import java.util.ArrayList;
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

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.profilelib.dao.ProfileChangeHistoryDAO;
import com.mainsteam.stm.profilelib.po.ProfileChangeHistoryPO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class ProfileChangeHistoryDAOTest extends DataSourceBasedDBTestCase{

	@Resource
	private ProfileChangeHistoryDAO profileChangeHistoryDAO;
	
	@Resource(name = "defaultDataSource")
	private DataSource source;
	
	@Resource
	private CapacityService capacityService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
		config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
	}

	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
		"src/test/resources/profileChangeHistory_dataset.xml"));	
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}
	
	@Test
	public void testInsertProfileHistoryChange(){
		long profileChangeHistroyId = 3333;
		
		ProfileChangeHistoryPO po = new ProfileChangeHistoryPO();
		po.setDcsGroupId(100100);
		po.setOperateTime(new Date());
		po.setProfileChangeHistoryId(profileChangeHistroyId);
		po.setProfileChangeId(11);
		po.setResultState(0);
		try {
			profileChangeHistoryDAO.insertProfileChangeHistory(po);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileChangeHistroyTable = actualDataset.getTable("stm_profilelib_change_history");
			
			//验证策略存在
			boolean find = false;
			for (int i = 0; i < profileChangeHistroyTable.getRowCount(); i++) {
				if (profileChangeHistroyId == Long.parseLong(profileChangeHistroyTable.getValue(i,
						"profile_change_history_id").toString())) {
					System.out.println("找到了profile_change_history_id=" + profileChangeHistroyId+"的数据");
					assertEquals(profileChangeHistroyTable.getValue(i,
							"DCS_GroupId"), po.getDcsGroupId());
					assertEquals(profileChangeHistroyTable.getValue(i,
							"result_state"), 1==po.getResultState());
					assertEquals(Long.parseLong(profileChangeHistroyTable.getValue(i,
							"profile_change_id").toString()),po.getProfileChangeId());
					find = true;
					break;
				}
			}
			assertTrue(find);	
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

	}
	
	@Test
	public void testGetHistoryByProfileChangeIds(){
		List<Long> list = new ArrayList<>();
		list.add(11L);
		list.add(22L);
		try {
			List<ProfileChangeHistoryPO> profileChangeHistoryPOs = profileChangeHistoryDAO.getHistoryByProfileChangeIds(list);
			int findCount = 0;
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileChangeHistroyTable = actualDataset
					.getTable("stm_profilelib_change_history");
			int rowCount = profileChangeHistroyTable.getRowCount();
			
			for (int i = 0; i < rowCount; i++) {
				for(ProfileChangeHistoryPO profileChangeHistoryPO : profileChangeHistoryPOs){
					if (profileChangeHistoryPO.getProfileChangeHistoryId() == Long
							.parseLong(profileChangeHistroyTable
									.getValue(i, "profile_change_history_id").toString())) {
						System.out.println("找到了profile_change_history_id=" + profileChangeHistoryPO.getProfileChangeHistoryId()+"的数据");
						assertEquals(profileChangeHistroyTable.getValue(i,
								"DCS_GroupId"),profileChangeHistoryPO.getDcsGroupId());
						assertEquals(profileChangeHistroyTable.getValue(i,
								"result_state"), 1==profileChangeHistoryPO.getResultState());
						assertEquals(Long.parseLong(profileChangeHistroyTable.getValue(i,
								"profile_change_id").toString()),profileChangeHistoryPO.getProfileChangeId());
						findCount++;
					}
				}
			}
			assertEquals(profileChangeHistoryPOs.size(), findCount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

