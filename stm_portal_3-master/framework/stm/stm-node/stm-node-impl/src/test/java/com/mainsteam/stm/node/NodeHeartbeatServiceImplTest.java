/**
 * 
 */
package com.mainsteam.stm.node;

import java.math.BigInteger;
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

import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeatCount;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeatService;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
		"classpath*:META-INF/services/server-processer-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class NodeHeartbeatServiceImplTest extends DataSourceBasedDBTestCase {

	@Resource(name = "defaultDataSource")
	private DataSource dataSource;

	@Resource(name = "nodeHeartbeatService")
	private NodeHeartbeatService nodeHeartbeatService;

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
	 * Test method for
	 * {@link com.mainsteam.stm.node.service.heartbeat.NodeHeartbeatServiceImpl#addNodeHeartbeat(com.mainsteam.stm.node.heartbeat.NodeHeartbeat)}
	 * .
	 */
	@Test
	public void testAddNodeHeartbeat() {
		NodeHeartbeat h = new NodeHeartbeat();
		long currentTime = System.currentTimeMillis();
		h.setExpireOccurtime(new Date(currentTime + 10000 * 3));
		h.setId(10909);
		h.setNextOccurtime(new Date(currentTime + 10000));
		h.setNodeId(1090);
		h.setOccurCount(2);
		h.setOccurtime(new Date(currentTime));
		nodeHeartbeatService.addNodeHeartbeat(h);
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE_HEARTBEAT");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				long id = ((BigInteger) table.getValue(i, "ID")).longValue();
				if (id == h.getId()) {
					System.out
							.println("find the NodeHeartbeat in the database.");
					assertEquals(h.getNodeId(),
							((Integer) table.getValue(i, "NODEID")).intValue());
					assertEquals(h.getOccurCount(),
							((BigInteger) table.getValue(i, "OCCURCOUNT"))
									.longValue());
					assertEquals(h.getExpireOccurtime().getTime(),
							((BigInteger) table.getValue(i, "EXPIREOCCURTIME"))
									.longValue());
					assertEquals(h.getNextOccurtime().getTime(),
							((BigInteger) table.getValue(i, "NEXTOCCURTIME"))
									.longValue());
					assertEquals(h.getOccurtime().getTime(),
							((BigInteger) table.getValue(i, "OCCURTIME"))
									.longValue());
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
	 * {@link com.mainsteam.stm.node.service.heartbeat.NodeHeartbeatServiceImpl#getLatestNodeHearbeats()}
	 * .
	 */
	@Test
	public void testGetLatestNodeHearbeats() {
		List<NodeHeartbeat> heartbeats = nodeHeartbeatService
				.getLatestNodeHeartbeats();
		assertNotNull(heartbeats);
		assertTrue(heartbeats.size() > 0);

		/**
		 * 验证，每个node只有一条记录
		 */
		int lastNodeId = -1;
		for (NodeHeartbeat nodeHeartbeat : heartbeats) {
			assertNotSame(lastNodeId, nodeHeartbeat.getNodeId());
			lastNodeId = nodeHeartbeat.getNodeId();
		}

		/**
		 * 验证，每个node的记录，其nodeId都是最大的，即其产生时间是最大的
		 */
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE_HEARTBEAT");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			for (NodeHeartbeat h : heartbeats) {
				boolean find = false;
				for (int i = 0; i < rowCount; i++) {
					long id = ((BigInteger) table.getValue(i, "ID"))
							.longValue();
					int nodeId = ((Integer) table.getValue(i, "NODEID"))
							.intValue();
					if (id == h.getId()) {
						System.out
								.println("find the NodeHeartbeat in the database.");
						assertEquals(h.getNodeId(), ((Integer) table.getValue(
								i, "NODEID")).intValue());
						assertEquals(h.getOccurCount(),
								((BigInteger) table.getValue(i, "OCCURCOUNT"))
										.longValue());
						assertEquals(h.getExpireOccurtime().getTime(),
								((BigInteger) table.getValue(i,
										"EXPIREOCCURTIME")).longValue());
						assertEquals(h.getNextOccurtime().getTime(),
								((BigInteger) table
										.getValue(i, "NEXTOCCURTIME"))
										.longValue());
						assertEquals(h.getOccurtime().getTime(),
								((BigInteger) table.getValue(i, "OCCURTIME"))
										.longValue());
						find = true;
						System.out
								.println("the node is equals with the content from database.");
					} else if (nodeId == h.getNodeId()) {
						assertTrue(h.getId() > id);
						assertTrue(h.getOccurtime().getTime() > ((BigInteger) table
								.getValue(i, "OCCURTIME")).longValue());
					}
				}
				assertTrue(find);
				System.out.println("success.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.service.heartbeat.NodeHeartbeatServiceImpl#getNodeHearbeats(java.util.Date, java.util.Date)}
	 * .
	 */
	@Test
	public void testGetNodeHearbeatsDateDate() {
		Date start = new Date(0);
		Date end = new Date();
		List<NodeHeartbeat> heartbeats = nodeHeartbeatService
				.getNodeHeartbeats(start, end);
		assertNotNull(heartbeats);
		assertTrue(heartbeats.size() > 0);
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE_HEARTBEAT");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			for (NodeHeartbeat h : heartbeats) {
				boolean find = false;
				for (int i = 0; i < rowCount; i++) {
					long id = ((BigInteger) table.getValue(i, "ID"))
							.longValue();
					if (id == h.getId()) {
						System.out
								.println("find the NodeHeartbeat in the database.");
						assertEquals(h.getNodeId(), ((Integer) table.getValue(
								i, "NODEID")).intValue());
						assertEquals(h.getOccurCount(),
								((BigInteger) table.getValue(i, "OCCURCOUNT"))
										.longValue());
						assertEquals(h.getExpireOccurtime().getTime(),
								((BigInteger) table.getValue(i,
										"EXPIREOCCURTIME")).longValue());
						assertEquals(h.getNextOccurtime().getTime(),
								((BigInteger) table
										.getValue(i, "NEXTOCCURTIME"))
										.longValue());
						assertEquals(h.getOccurtime().getTime(),
								((BigInteger) table.getValue(i, "OCCURTIME"))
										.longValue());
						find = true;
						System.out
								.println("the node is equals with the content from database.");
						break;
					}
				}
				assertTrue(find);
				System.out.println("success.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.service.heartbeat.NodeHeartbeatServiceImpl#removeNodeHearbeats(java.util.Date)}
	 * .
	 */
	@Test
	public void testRemoveNodeHearbeats() {
		Date end = new Date(9000000 - 1);
		int count = nodeHeartbeatService.removeNodeHeartbeats(end);
		assertTrue(count >= 2);
		/**
		 * 验证，数据库中的数据，所有的时间都小于结束时间
		 */
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE_HEARTBEAT");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			for (int i = 0; i < rowCount; i++) {
				assertTrue(end.getTime() < ((BigInteger) table.getValue(i,
						"OCCURTIME")).longValue());
			}
			System.out.println("success.");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.service.heartbeat.NodeHeartbeatServiceImpl#removeNodeHearbeats(java.util.Date)}
	 * .
	 */
	@Test
	public void testRemoveNodeHearbeatsById() {
		int nodeId = 9001;
		Date end = new Date(874000 - 1);
		int count = nodeHeartbeatService.removeNodeHeartbeats(nodeId, end);
		System.out.println(count);
		assertTrue(count == 1);
		/**
		 * 验证，数据库中的数据，所有的时间都小于结束时间
		 */
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE_HEARTBEAT");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			for (int i = 0; i < rowCount; i++) {
				int nd = ((Integer) table.getValue(i, "NODEID")).intValue();
				if (nd == nodeId) {
					assertTrue(end.getTime() < ((BigInteger) table.getValue(i,
							"OCCURTIME")).longValue());
				}
			}
			System.out.println("success.");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSelectNodeHeartbeatCounts() {
		List<NodeHeartbeatCount> heartbeatCounts = nodeHeartbeatService
				.selectNodeHeartbeatCounts();
		assertNotNull(heartbeatCounts);
		assertTrue(heartbeatCounts.size() > 0);
		for (NodeHeartbeatCount nodeHeartbeatCount : heartbeatCounts) {
			System.out.println(" nodeId=" + nodeHeartbeatCount.getNodeId()
					+ " checkCount=" + nodeHeartbeatCount.getCheckCount()
					+ " heartbeanCount="
					+ nodeHeartbeatCount.getHeartbeatCount());
		}
	}

	@Test
	public void testUpdateNodeHeartbeatCount() {
		Date currentDate = new Date();
		List<NodeHeartbeatCount> heartbeatCounts = new ArrayList<>();
		NodeHeartbeatCount updateCount = new NodeHeartbeatCount();
		updateCount.setCheckCount(0);
		updateCount.setHeartbeatCount(100);
		updateCount.setNodeId(9002);
		updateCount.setHeartbeatOccurtime(currentDate);
		heartbeatCounts.add(updateCount);

		NodeHeartbeatCount insertCount = new NodeHeartbeatCount();
		insertCount.setCheckCount(0);
		insertCount.setHeartbeatCount(100);
		insertCount.setNodeId(1000092);
		insertCount.setHeartbeatOccurtime(currentDate);
		heartbeatCounts.add(insertCount);
		nodeHeartbeatService.updateNodeHeartbeatCount(heartbeatCounts);
		try {
			IDataSet dataSet = getConnection().createDataSet();
			ITable table = dataSet.getTable("STM_NODE_HEARTBEAT_CHECK");
			int rowCount = table.getRowCount();
			assertTrue(rowCount >= 1);
			boolean findUpdate = false;
			boolean findInsert = false;
			for (int i = 0; i < rowCount; i++) {
				int nd = ((Integer) table.getValue(i, "NODEID")).intValue();
				if (nd == updateCount.getNodeId()) {
					assertEquals(updateCount.getCheckCount(),
							((BigInteger) table.getValue(i, "CKCOUNT"))
									.longValue());
					assertEquals(updateCount.getHeartbeatCount(),
							((BigInteger) table.getValue(i, "HBCOUNT"))
									.longValue());
					assertEquals(updateCount.getHeartbeatOccurtime().getTime(),
							((BigInteger) table.getValue(i, "HBOCCURTIME"))
									.longValue());
					findUpdate = true;
				} else if (nd == insertCount.getNodeId()) {
					assertEquals(insertCount.getCheckCount(),
							((BigInteger) table.getValue(i, "CKCOUNT"))
									.longValue());
					assertEquals(insertCount.getHeartbeatCount(),
							((BigInteger) table.getValue(i, "HBCOUNT"))
									.longValue());
					assertEquals(insertCount.getHeartbeatOccurtime().getTime(),
							((BigInteger) table.getValue(i, "HBOCCURTIME"))
									.longValue());
					findInsert = true;
				}
			}
			assertTrue(findUpdate);
			assertTrue(findInsert);
			System.out.println("success.");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Override
	protected DataSource getDataSource() {
		return this.dataSource;
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/oc_node_heartbeat.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}
}
