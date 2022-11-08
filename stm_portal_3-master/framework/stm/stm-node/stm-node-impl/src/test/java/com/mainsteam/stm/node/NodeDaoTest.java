/**
 * 
 */
package com.mainsteam.stm.node;

import java.math.BigInteger;
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

import com.mainsteam.stm.node.dao.impl.NodeDAOImpl;
import com.mainsteam.stm.node.dao.pojo.NodeDO;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/server-processer-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class NodeDaoTest extends DataSourceBasedDBTestCase {

	@Resource(name = "defaultDataSource")
	private DataSource dataSource;

	@Resource
	private NodeDAOImpl daoImpl;

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

	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
		config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
	}

	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.NONE;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		// OK,do nothing.
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.dao.impl.NodeDAOImpl#insert(com.mainsteam.stm.node.dao.pojo.NodeDO)}
	 * .
	 */
	@Test
	public void testInsert() {
		NodeDO node = new NodeDO();
		node.setId(1);
		node.setName("insert1");
		node.setIp("localhost");
		node.setPort(111);
		node.setFunc(NodeFunc.collector.name());
		node.setGroupId(3);
		node.setPriority(1);
		node.setAlive(1);
		node.setUpdateTime(new Date().getTime());
		int size = daoImpl.insert(node);
		System.out.println("I guess the node has been insert into database.");
		assertEquals(1, size);
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				int id = (Integer) table.getValue(i, "ID");
				if (id == node.getId().intValue()) {
					System.out.println("find the nodeId in the database.");
					assertEquals(node.getName(), table.getValue(i, "NAME"));
					assertEquals(node.getIp(), table.getValue(i, "IP"));
					assertEquals(node.getPort(), table.getValue(i, "PORT"));
					assertEquals(node.getFunc(), table.getValue(i, "FUNC"));
					assertEquals(node.getGroupId(),
							table.getValue(i, "GROUPID"));
					assertEquals(node.getPriority(),
							table.getValue(i, "PRIORITY"));
					assertEquals(node.getUpdateTime(), ((BigInteger) table.getValue(i,
							"UPDATETIME")).longValue());
					find = true;
					System.out
							.println("the node is equals with the content from database.");
					break;
				}
			}
			assertTrue(find);
			System.out.println("success.");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.dao.impl.NodeDAOImpl#updateExampleById(com.mainsteam.stm.node.dao.pojo.NodeDO)}
	 * .
	 */
	@Test
	public void testUpdateExampleById() {
		NodeDO node = new NodeDO();
		node.setId(1000);
		node.setName("insert1");
		node.setIp("localhost");
		node.setPort(111);
		node.setFunc(NodeFunc.collector.name());
		node.setGroupId(3);
		node.setPriority(1);
		node.setUpdateTime(new Date().getTime());
		int size = daoImpl.updateExampleById(node);
		System.out
				.println("I guess the node has been update into the database.");
		assertEquals(1, size);
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				int id = (Integer) table.getValue(i, "ID");
				if (id == node.getId().intValue()) {
					System.out.println("find the node from database.");
					assertEquals(node.getName(), table.getValue(i, "NAME"));
					assertEquals(node.getIp(), table.getValue(i, "IP"));
					assertEquals(node.getPort(), table.getValue(i, "PORT"));
					assertNotSame(node.getFunc(), table.getValue(i, "FUNC"));
					assertEquals(node.getGroupId(),
							table.getValue(i, "GROUPID"));
					assertEquals(node.getPriority(),
							table.getValue(i, "PRIORITY"));
					assertEquals(node.getUpdateTime(), ((BigInteger) table.getValue(i,
							"UPDATETIME")).longValue());
					System.out
							.println("the node is equals with the content from database but func of the node.");

					find = true;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.dao.impl.NodeDAOImpl#deleteById(int)}.
	 */
	@Test
	public void testDeleteById() {
		int nodeId = 1000;
		int size = daoImpl.deleteById(nodeId);
		System.out
				.println("I guess the node which id is 1000 has been deleted ");
		assertEquals(1, size);
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				int id = (Integer) table.getValue(i, "ID");
				if (id == nodeId) {
					find = true;
					break;
				}
			}
			assertFalse(find);
			System.out.println("So ,the node not found in the database.");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.dao.impl.NodeDAOImpl#getByExample(com.mainsteam.stm.node.dao.pojo.NodeDO)}
	 * .
	 */
	@Test
	public void testGetByExample() {
		NodeDO node = new NodeDO();
		node.setPort(9230);
		List<NodeDO> nodesList = daoImpl.getByExample(node);
		assertEquals(3, nodesList.size());
		System.out.println("the port 9230 has two nodes in the database.");
		for (NodeDO nodeDO : nodesList) {
			System.out.println("nodeIp=" + nodeDO.getIp());
			System.out.println("nodePort=" + nodeDO.getPort());
			System.out.println("\n");
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.dao.impl.NodeDAOImpl#getById(int)}.
	 */
	@Test
	public void testGetById() {
		int nodeId = 1000;
		NodeDO node = daoImpl.getById(nodeId);
		assertNotNull(node);
		assertEquals(nodeId, node.getId().intValue());
		System.out.println("get Node info is nodeId=" + node.getIp() + " ip="
				+ node.getIp() + " port=" + node.getPort());
	}

	@Override
	protected DataSource getDataSource() {
		return this.dataSource;
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/node_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

}
