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
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
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

import com.mainsteam.stm.node.exception.NodeException;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class NodeServiceImplTest extends DataSourceBasedDBTestCase {

	@Autowired
	protected static ApplicationContext ctx;

	@Resource
	private NodeService nodeSerivce;

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

	@Test
	public void testAddNode() {
		System.out.println("testAddNode-------------------->start");
		Node node = new Node();
		node.setAlive(true);
		node.setFunc(NodeFunc.portal);
		node.setIp("127.0.0.1");
		node.setName("nodeName");
		node.setPort(1000);
		node.setPriority(10);
		node.setInstallPath("e:\\oc4");
		int id = 0;
		try {
			id = nodeSerivce.addNode(node);
			System.out.println("insert node's id=" + id);
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		ITable dataSet = null;
		try {
			dataSet = getConnection().createTable("stm_node");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			assertTrue(rowCount > 0);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == id) {
					assertEquals(node.getName(), dataSet.getValue(i, "name"));
					assertEquals(node.getIp(), dataSet.getValue(i, "ip"));
					assertEquals(node.getFunc().name(),
							dataSet.getValue(i, "func"));
					assertEquals(node.getPort(),
							((Integer) dataSet.getValue(i, "port")).intValue());
					assertEquals(node.getPriority(),
							((Integer) dataSet.getValue(i, "priority"))
									.intValue());
					assertEquals(
							node.isAlive(),
							((Integer) dataSet.getValue(i, "alive")).intValue() == 1);
					assertEquals(node.getInstallPath(),
							dataSet.getValue(i, "INSTALLPATH"));
					find = true;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		Node node1 = new Node();
		node1.setAlive(true);
		node1.setFunc(NodeFunc.processer);
		node1.setIp("127.0.0.1");
		node1.setName("nodeName1");
		node1.setPort(1000);
		node1.setPriority(3);
		node1.setGroupId(2);
		node1.setInstallPath("e:\\oc4_1");
		try {
			id = nodeSerivce.addNode(node1);
			fail();
		} catch (NodeException e) {
			e.printStackTrace();
			System.out
					.println("insert node's ip and port is repeat validate ok");
		}

		node1.setPort(10000);
		node1.setFunc(NodeFunc.collector);
		try {
			id = nodeSerivce.addNode(node1);
			fail();
		} catch (NodeException e) {
			e.printStackTrace();
			System.out.println("insert node's group validate ok");
		}
		System.out.println("testAddNode-------------------->end");
	}

	@Test
	public void testGetNodeTable() {
		NodeTable table = null;
		try {
			table = nodeSerivce.getNodeTable();
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(table);
		List<NodeGroup> groups = table.getGroups();
		assertNotNull(groups);
		assertEquals(5, groups.size());
		System.out.println("5 groups is finded.");
		final int portalGroupId = 0;
		final int collectorGroupId = 1;
		final int processerGroupId = 2;
		NodeGroup portalGroup = null;
		NodeGroup collectorGroup = null;
		NodeGroup processerGroup = null;
		for (NodeGroup nodeGroup : groups) {
			if (nodeGroup.getId() == portalGroupId) {
				portalGroup = nodeGroup;
			} else if (nodeGroup.getId() == collectorGroupId) {
				collectorGroup = nodeGroup;
			} else if (nodeGroup.getId() == processerGroupId) {
				processerGroup = nodeGroup;
			}
		}
		assertNotNull(portalGroup);
		assertNotNull(collectorGroup);
		assertNotNull(processerGroup);

		/**
		 * 验证这3个组对象存在，且和数据库库里的信息相同
		 */
		int findCount = 0;
		ITable dataSet = null;
		try {
			dataSet = getConnection().createTable("stm_node_group");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			assertTrue(rowCount >= 3);
			for (int i = 0; i < rowCount; i++) {
				NodeGroup node = null;
				int dataId = ((Integer) dataSet.getValue(i, "id")).intValue();
				switch (dataId) {
				case portalGroupId:
					node = portalGroup;
					break;
				case collectorGroupId:
					node = collectorGroup;
					break;
				case processerGroupId:
					node = processerGroup;
					break;
				default:
					break;
				}
				if (node == null) {
					continue;
				}
				assertEquals(node.getName(), dataSet.getValue(i, "name"));
				assertEquals(node.getNodeLevel(),
						((Integer) dataSet.getValue(i, "GROUPLEVEl")).intValue());
				assertEquals(node.getFunc().name(), dataSet.getValue(i, "func"));
				findCount++;
			}
			assertEquals(3, findCount);
			System.out.println("validate groups over.");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		int collector1 = 1000;
		int collector2 = 1001;
		List<Node> nodes = collectorGroup.getNodes();
		assertNotNull(nodes);
		assertNotNull(collectorGroup.getPre());
		assertNull(collectorGroup.getNextGroups());

		assertEquals(processerGroup, collectorGroup.getPre());
		assertEquals(2, nodes.size());
		for (Node node : nodes) {
			assertTrue(node.getId() == collector1 || node.getId() == collector2);
			assertEquals(NodeFunc.collector, node.getFunc());
		}

		int processer = 1003;
		nodes = processerGroup.getNodes();
		assertNotNull(processerGroup.getNextGroups());
		assertEquals(1, processerGroup.getNextGroups().size());
		assertEquals(collectorGroup, processerGroup.getNextGroups().get(0));
		assertNotNull(nodes);
		assertEquals(1, nodes.size());
		for (Node node : nodes) {
			assertTrue(node.getId() == processer);
			assertEquals(NodeFunc.processer, node.getFunc());
		}

		int portal1 = 500;
		int portal2 = 600;
		nodes = portalGroup.getNodes();
		assertNotNull(nodes);
		assertNull(portalGroup.getPre());
		assertNull(portalGroup.getNextGroups());
		for (Node node : nodes) {
			assertTrue(node.getId() == portal1 || node.getId() == portal2);
			assertEquals(NodeFunc.portal, node.getFunc());
		}

		List<Node> list = table.selectNodesByType(NodeFunc.collector);
		for (Node node : list) {
			System.out.println(node.getId() + "--" + node.getParentNodeId());
		}
	}

	@Test
	public void testGetNodeById() {
		System.out.println("testGetNodeById--------------start");
		int nodeId = 500;
		Node node;
		try {
			node = nodeSerivce.getNodeById(nodeId);
			assertNotNull(node);
			System.out.println("id=" + node.getId());
			System.out.println("ip=" + node.getIp());
			System.out.println("port=" + node.getPort());
			ITable dataSet = getConnection().createTable("stm_node");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			assertTrue(rowCount > 0);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == node
						.getId()) {
					assertEquals(node.getName(), dataSet.getValue(i, "name"));
					assertEquals(node.getIp(), dataSet.getValue(i, "ip"));
					assertEquals(node.getFunc().name(),
							dataSet.getValue(i, "func"));
					assertEquals(node.getPort(),
							((Integer) dataSet.getValue(i, "port")).intValue());
					assertEquals(node.getPriority(),
							((Integer) dataSet.getValue(i, "priority"))
									.intValue());
					assertEquals(
							node.isAlive(),
							((Integer) dataSet.getValue(i, "alive")).intValue() == 1);
					assertEquals(node.getInstallPath(),
							dataSet.getValue(i, "INSTALLPATH"));
					find = true;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		System.out.println("testGetNodeById--------------end");
	}

	@Test
	public void testRemoveNodeById() {
		System.out.println("testRemoveNodeById-------------------->start");
		int nodeId = 500;
		Node node = null;
		try {
			node = nodeSerivce.getNodeById(nodeId);
			System.out.println("node is exist.");
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(node);
		boolean result = false;
		try {
			result = nodeSerivce.removeNodeById(nodeId);
			System.out.println("remove result=" + result);
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(result);
		try {
			ITable dataSet = getConnection().createTable("stm_node");
			assertNotNull(dataSet);
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == nodeId) {
					find = true;
					break;
				}
			}
			assertFalse(find);
			System.out.println("not found the nodeId " + nodeId);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		System.out.println("testRemoveNodeById-------------------->end");
	}

	@Test
	public void testUpdateNode() {
		System.out.println("testUpdateNode-------------------->start");
		int collector1 = 1000;
		Node node = new Node();
		node.setId(collector1);
		node.setAlive(true);
		node.setFunc(NodeFunc.collector);
		node.setIp("127.0.0.1");
		node.setName("nodeName");
		node.setPort(1000);
		node.setPriority(10);
		node.setInstallPath("d:\\temp");
		try {
			nodeSerivce.updateNode(node);
			System.out.println("update node's id=" + collector1);
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		ITable dataSet = null;
		try {
			dataSet = getConnection().createTable("stm_node");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			assertTrue(rowCount > 0);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == collector1) {
					assertEquals(node.getName(), dataSet.getValue(i, "name"));
					assertEquals(node.getIp(), dataSet.getValue(i, "ip"));
					assertEquals(node.getFunc().name(),
							dataSet.getValue(i, "func"));
					assertEquals(node.getPort(),
							((Integer) dataSet.getValue(i, "port")).intValue());
					assertEquals(node.getPriority(),
							((Integer) dataSet.getValue(i, "priority"))
									.intValue());
					assertEquals(
							node.isAlive(),
							((Integer) dataSet.getValue(i, "alive")).intValue() == 1);
					assertEquals(node.getInstallPath(),
							dataSet.getValue(i, "INSTALLPATH"));
					find = true;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		System.out.println("testUpdateNode-------------------->end");
	}

	@Test
	public void testUpdateNodeStartupTime() {
		System.out
				.println("testUpdateNodeStartupTime-------------------->start");
		int collector1 = 1000;
		Date currentDate = new Date();
		nodeSerivce.updateNodeStartupTime(collector1, currentDate);
		ITable dataSet = null;
		try {
			dataSet = getConnection().createTable("stm_node");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			assertTrue(rowCount > 0);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == collector1) {
					assertEquals(currentDate.getTime(),
							((BigInteger) dataSet.getValue(i, "STARTUPTIME"))
									.longValue());
					find = true;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		System.out.println("testUpdateNodeStartupTime-------------------->end");
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
