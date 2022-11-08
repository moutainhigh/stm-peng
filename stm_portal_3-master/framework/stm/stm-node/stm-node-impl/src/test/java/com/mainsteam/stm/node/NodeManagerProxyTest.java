package com.mainsteam.stm.node;

import java.math.BigInteger;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.service.NodeManagerProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
		"classpath*:META-INF/services/server-processer-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class NodeManagerProxyTest extends DataSourceBasedDBTestCase {

	@Resource
	private NodeManagerProxy nodeManagerProxy;

	@Resource(name = "defaultDataSource")
	private DataSource source;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

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

	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.NONE;
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

	@Test
	public void testAddNode() {
		Node collectorNode = new Node();
		collectorNode.setIp("192.168.10.111");
		collectorNode.setPort(2015);
		collectorNode.setFunc(NodeFunc.collector);
		collectorNode.setInstallPath("ziw");
		collectorNode.setName(collectorNode.getIp() + ':'
				+ collectorNode.getPort());
		collectorNode.setGroupId(-1);
		try {
			nodeManagerProxy.addNode(collectorNode, false);
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		ITable table = null;
		try {
			table = getConnection()
					.createQueryTable(
							"RESULT_NAME",
							"select n.*,rel.DOMAIN_ID,rel.IS_CHECKED from STM_NODE n,STM_SYSTEM_DOMAIN_DCS_REL rel where rel.DCS_ID = n.ID and n.ID="
									+ collectorNode.getId());
			assertNotNull(table);
			assertTrue(table.getRowCount() >= 1);
			assertEquals(1L,
					((BigInteger) table.getValue(0, "DOMAIN_ID")).longValue());
			assertEquals(1,
					((Integer) table.getValue(0, "IS_CHECKED")).intValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}
}
