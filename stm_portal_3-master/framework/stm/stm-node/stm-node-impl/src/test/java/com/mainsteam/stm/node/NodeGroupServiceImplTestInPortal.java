/**
 * 
 */
package com.mainsteam.stm.node;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
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

import com.mainsteam.stm.node.service.NodeGroupServiceImpl;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
"classpath*:META-INF/services/portal-*-beans.xml" })
public class NodeGroupServiceImplTestInPortal extends DataSourceBasedDBTestCase {

	@Resource(name="groupService")
	private NodeGroupService serivce;

	@Resource(name = "defaultDataSource")
	private DataSource source;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

	@Test
	public void testAddGroup() {
		try {
			NodeGroupServiceImplTestLogic.testAddGroup(this.serivce,
					getConnection());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRemoveGroup() {
		try {
			NodeGroupServiceImplTestLogic.testRemoveGroup(this.serivce,
					getConnection());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateNodeGroupRelation() {
		try {
			NodeGroupServiceImplTestLogic.testUpdateNodeGroupRelation(
					this.serivce, getConnection());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetNodeGroupById() {
		try {
			NodeGroupServiceImplTestLogic.testGetNodeGroupById(this.serivce,
					getConnection());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/node_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}
}
