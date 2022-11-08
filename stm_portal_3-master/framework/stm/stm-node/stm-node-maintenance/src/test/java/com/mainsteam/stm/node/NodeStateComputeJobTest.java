/**
 * 
 */
package com.mainsteam.stm.node;

import java.sql.Statement;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.quartz.JobExecutionException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeatService;
import com.mainsteam.stm.node.job.NodeStateComputeJob;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
		"classpath*:META-INF/services/server-processer-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class NodeStateComputeJobTest extends DataSourceBasedDBTestCase {
	
	private static final Log logger = LogFactory
			.getLog(NodeStateComputeJobTest.class);

	@Resource(name = "defaultDataSource")
	private DataSource dataSource;

	@Resource(name = "nodeStateComputeJob")
	private NodeStateComputeJob job;

	@Resource(name = "nodeHeartbeatService")
	private NodeHeartbeatService heartbeatService;

	@Resource(name = "nodeService")
	private NodeService nodeService;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	 * {@link com.mainsteam.stm.node.job.NodeStateComputeJob#execute(org.quartz.JobExecutionContext)}
	 * .
	 */
	@Test
	public void testExecute() {
		Statement st;
		try {
			st = getConnection().getConnection().createStatement();
			st.execute("delete from STM_NODE_HEARTBEAT_CHECK");
			st.execute("delete from STM_NODE_HEARTBEAT");
			st.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		}
		/**
		 * 插入一条heartbeat
		 */
		int nodeId = 184001;
		long interval = 1000;
		long current = System.currentTimeMillis();
		long next = current + interval;
		long expire = current + interval * 3;
		NodeHeartbeat heartbeat = new NodeHeartbeat();
		heartbeat.setOccurCount(1);
		heartbeat.setOccurtime(new Date(current));
		heartbeat.setNextOccurtime(new Date(next));
		heartbeat.setExpireOccurtime(new Date(expire));
		heartbeat.setNodeId(nodeId);
		heartbeatService.addNodeHeartbeat(heartbeat);
		try {
			job.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
			fail();
		}
		Node node = null;
		try {
			node = nodeService.getNodeById(nodeId);
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(node.isAlive());
		for (int i = 0; i < 3; i++) {
			try {
				job.execute(null);
			} catch (JobExecutionException e) {
				e.printStackTrace();
				fail();
			}
		}
//		try {
//			Thread.sleep(expire - current+20);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			fail();
//		}
//		try {
//			job.execute(null);
//			node = nodeService.getNodeById(nodeId);
//		} catch (NodeException | JobExecutionException e) {
//			e.printStackTrace();
//			fail();
//		}
		try {
			node = nodeService.getNodeById(nodeId);
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(node.isAlive());

		current = System.currentTimeMillis();
		next = current + interval;
		expire = current + interval * 3;
		heartbeat = new NodeHeartbeat();
		heartbeat.setOccurCount(2);
		heartbeat.setOccurtime(new Date(current));
		heartbeat.setNextOccurtime(new Date(next));
		heartbeat.setExpireOccurtime(new Date(expire));
		heartbeat.setNodeId(nodeId);
		heartbeatService.addNodeHeartbeat(heartbeat);
		if (logger.isDebugEnabled()) {
			logger.debug("testExecute ready to make alive.");
		}
		try {
			job.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
			fail();
		}
		System.out.println("run 1.");
		try {
			node = nodeService.getNodeById(nodeId);
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(node.isAlive());

		System.out.println("run over.");
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
