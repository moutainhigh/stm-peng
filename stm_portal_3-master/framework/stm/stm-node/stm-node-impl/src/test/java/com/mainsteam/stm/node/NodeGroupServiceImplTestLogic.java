/**
 * 
 */
package com.mainsteam.stm.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

/**
 * @author ziw
 * 
 */
public class NodeGroupServiceImplTestLogic {

	/**
	 * 
	 */
	public NodeGroupServiceImplTestLogic() {
	}

	public static void testAddGroup(NodeGroupService service,
			IDatabaseConnection conn) {
		NodeGroup g = new NodeGroup();
		g.setFunc(NodeFunc.collector);
		g.setName("test");
		g.setNodeLevel(1);
		int id = service.addGroup(g);
		assertTrue(id >= 0);
		ITable dataSet = null;
		try {
			dataSet = conn.createTable("STM_NODE_GROUP");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			assertTrue(rowCount > 0);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == id) {
					assertEquals(g.getName(), dataSet.getValue(i, "name"));
					assertEquals(g.getFunc().name(),
							dataSet.getValue(i, "func"));
					assertEquals(g.getNodeLevel(),
							((Integer) dataSet.getValue(i, "GROUPLEVEl")).intValue());
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

	public static void testRemoveGroup(NodeGroupService service,
			IDatabaseConnection conn) {
		int groupId = 0;
		service.removeGroup(groupId);
		ITable dataSet = null;
		try {
			dataSet = conn.createTable("STM_NODE_GROUP");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == groupId) {
					find = true;
					break;
				}
			}
			assertFalse(find);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public static void testUpdateNodeGroupRelation(NodeGroupService service,
			IDatabaseConnection conn) {
		int groupId = 0;
		int childGroupId = 1;
		service.updateNodeGroupRelation(childGroupId, groupId);
		ITable dataSet = null;
		try {
			dataSet = conn.createTable("STM_NODE_GROUP");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			assertTrue(rowCount > 0);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == childGroupId) {
					assertEquals(groupId, ((Integer) dataSet.getValue(i,
							"parentid")).intValue());
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

	public static void testGetNodeGroupById(NodeGroupService service,
			IDatabaseConnection conn) {
		int groupId = 1;
		NodeGroup g = service.getNodeGroupById(groupId);
		int id = service.addGroup(g);
		assertTrue(id >= 0);
		assertNotNull(g.getPre());
		ITable dataSet = null;
		try {
			dataSet = conn.createTable("STM_NODE_GROUP");
			assertNotNull(dataSet);
			int rowCount = dataSet.getRowCount();
			assertTrue(rowCount > 0);
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (((Integer) dataSet.getValue(i, "id")).intValue() == id) {
					assertEquals(g.getName(), dataSet.getValue(i, "name"));
					assertEquals(g.getFunc().name(),
							dataSet.getValue(i, "func"));
					assertEquals(g.getNodeLevel(),
							((Integer) dataSet.getValue(i, "GROUPLEVEl")).intValue());
					assertEquals(g.getPre().getId(),
							((Integer) dataSet.getValue(i, "parentid"))
									.intValue());
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

	public void testGetNodeGroups() {

	}
}
