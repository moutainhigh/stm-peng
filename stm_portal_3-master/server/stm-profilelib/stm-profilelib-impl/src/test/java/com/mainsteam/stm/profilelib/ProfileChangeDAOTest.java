package com.mainsteam.stm.profilelib;


import java.util.Date;

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
import com.mainsteam.stm.profilelib.dao.ProfileChangeDAO;
import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;
import com.mainsteam.stm.profilelib.po.ProfileChangePO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class ProfileChangeDAOTest extends DataSourceBasedDBTestCase{

	@Resource
	private ProfileChangeDAO profileChangeDAO;
	
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
				"src/test/resources/profileChange_dataset.xml"));
//		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
//		"src/test/resources/profileChangeHistory_dataset.xml"));	
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}
	
	@Test
	public void testInsertProfileChange(){
		long profileChangeId = 3333;
		
		ProfileChangePO po = new ProfileChangePO();
		po.setChangeTime(new Date());
		po.setOperateMode(ProfileChangeEnum.ADD_MONITOR.toString());
		po.setOperateState(0);
		po.setProfileChangeId(profileChangeId);
		po.setSource("3333");
		po.setProfileId(121212);
		po.setTimelineId(222);
		try {
			profileChangeDAO.insertProfileChange(po);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileChangeTable = actualDataset.getTable("stm_profilelib_change");
			
			//验证策略存在
			boolean find = false;
			for (int i = 0; i < profileChangeTable.getRowCount(); i++) {
				if (profileChangeId == Long.parseLong(profileChangeTable.getValue(i,
						"profile_change_id").toString())) {
					System.out.println("找到了profile_change_id=" + profileChangeId+"的数据");
					assertEquals(profileChangeTable.getValue(i,
							"operate_mode"), po.getOperateMode());
					assertEquals(Long.parseLong(profileChangeTable.getValue(i,
							"source").toString()), po.getSource());
					assertEquals(profileChangeTable.getValue(i,
							"operate_state"), 1==po.getOperateState());
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
	public void testUpdateProfileChange(){
		long profileChangeId = 11;
		
		ProfileChangePO po = new ProfileChangePO();
		po.setChangeTime(new Date());
		po.setOperateMode(ProfileChangeEnum.CANCEL_MONITOR.toString());
		po.setOperateState(0);
		po.setProfileChangeId(profileChangeId);
		po.setSource("111");
		po.setProfileId(121212);
		try {
			profileChangeDAO.updateProfileChange(po);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileChangeTable = actualDataset.getTable("stm_profilelib_change");
			
			//验证策略存在
			boolean find = false;
			for (int i = 0; i < profileChangeTable.getRowCount(); i++) {
				if (profileChangeId == Long.parseLong(profileChangeTable.getValue(i,
						"profile_change_id").toString())) {
					System.out.println("找到了profile_change_id=" + profileChangeId+"的数据");
					assertEquals(profileChangeTable.getValue(i,
							"operate_mode"), po.getOperateMode());
					assertEquals(Long.parseLong(profileChangeTable.getValue(i,
							"source").toString()), po.getSource());
					assertEquals(profileChangeTable.getValue(i,
							"operate_state"), 1==po.getOperateState());
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
}
