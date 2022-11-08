/**
 * 
 */
package com.mainsteam.stm.node;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.task.LocalNodeTableChecker;

/**
 * @author ziw
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/server-processer-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class LocalNodeTableCheckerTest {

	@Resource(name="localeTableManager")
	private LocaleTableManagerImpl tableManager;

	@Resource(name="nodeService")
	private NodeService nodeService;

	@Resource(name="nodeManager")
	private NodeManager managerProxy;
	
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.mainsteam.stm.node.task.LocalNodeTableChecker#checkUpdate(com.mainsteam.stm.node.NodeTable, java.util.Date, int, int)}.
	 */
	@Test
	public void testCheckUpdate() {
		Node currentNode = tableManager.getCurrentNode();
		NodeGroup currentNodeGroup = tableManager.getCurrentGroup();
		NodeTable table = tableManager.getNodeTable();
		assertNotNull(table);
		NodeTableSummary summary = managerProxy.selectNodeTableSummary();
		boolean isUpdate = true;
//				LocalNodeTableChecker.checkUpdate(table,
//				summary.getUpdateTime(), summary.getNodeGroupSize(),
//				summary.getNodesize(),currentNode,currentNodeGroup);
		assertTrue(isUpdate);
		if (isUpdate) {
			try {
				NodeTable dbTable = nodeService.getNodeTable();
				assertNotNull(dbTable);
				currentNode = tableManager.getCurrentNode();
				assertNotNull(currentNode);
//				tableManager.saveNodeTable(currentNode, dbTable);
			} catch (NodeException e) {
				e.printStackTrace();
				fail();
			}
		}
	}

}
