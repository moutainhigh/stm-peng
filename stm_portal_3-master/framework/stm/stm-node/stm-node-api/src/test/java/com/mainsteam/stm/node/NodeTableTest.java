/**
 * 
 */
package com.mainsteam.stm.node;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author ziw
 * 
 */
public class NodeTableTest {

	private NodeTable table;
	private NodeGroup portalGroup;
	private NodeGroup h2Group;
	private NodeGroup dcs1Group;
	private NodeGroup dcs2Group;

	private NodeGroup h1Group;
	private NodeGroup dcs3Group;

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
		table = new NodeTable();
		List<NodeGroup> groups = new ArrayList<>();
		table.setGroups(groups);
		/**
		 * portal分组
		 */
		portalGroup = new NodeGroup();
		groups.add(portalGroup);
		portalGroup.setFunc(NodeFunc.portal);
		portalGroup.setId(0);
		portalGroup.setName("portal_group");
		portalGroup.setNodeLevel(1);
		portalGroup.setNodes(new ArrayList<Node>());
		Node p1 = new Node();
		p1.setId(1);
		p1.setIp("192.168.1.2");
		p1.setPort(9000);
		p1.setFunc(portalGroup.getFunc());
		p1.setGroupId(portalGroup.getId());
		portalGroup.getNodes().add(p1);

		Node p2 = new Node();
		p2.setId(2);
		p2.setIp("192.168.1.3");
		p2.setPort(9000);
		p2.setFunc(portalGroup.getFunc());
		p2.setGroupId(portalGroup.getId());
		portalGroup.getNodes().add(p2);

		/**
		 * DCH1
		 */
		h1Group = new NodeGroup();
		groups.add(h1Group);
		h1Group.setFunc(NodeFunc.processer);
		h1Group.setId(1);
		h1Group.setName("processer1");
		h1Group.setNodeLevel(1);
		h1Group.setNextGroups(new ArrayList<NodeGroup>());
		h1Group.setNodes(new ArrayList<Node>());
		Node h11 = new Node();
		h11.setId(3);
		h11.setIp("192.168.1.4");
		h11.setPort(9001);
		h11.setFunc(h1Group.getFunc());
		h11.setGroupId(h1Group.getId());
		h1Group.getNodes().add(h11);
		Node h12 = new Node();
		h12.setId(4);
		h12.setIp("192.168.1.5");
		h12.setPort(9001);
		h12.setFunc(h1Group.getFunc());
		h12.setGroupId(h1Group.getId());
		h1Group.getNodes().add(h11);

		/**
		 * DCS1,DCS2
		 */
		dcs1Group = new NodeGroup();
		groups.add(dcs1Group);
		dcs1Group.setFunc(NodeFunc.collector);
		dcs1Group.setId(2);
		dcs1Group.setName("collector1");
		dcs1Group.setNodeLevel(2);
		dcs1Group.setPre(h1Group);
		dcs1Group.setNodes(new ArrayList<Node>());

		Node dcs = new Node();
		dcs.setId(5);
		dcs.setIp("192.168.1.6");
		dcs.setPort(9002);
		dcs.setFunc(dcs1Group.getFunc());
		dcs.setGroupId(dcs1Group.getId());
		dcs1Group.getNodes().add(dcs);
		h1Group.getNextGroups().add(dcs1Group);

		dcs2Group = new NodeGroup();
		groups.add(dcs2Group);
		dcs2Group.setFunc(NodeFunc.collector);
		dcs2Group.setId(3);
		dcs2Group.setName("collector2");
		dcs2Group.setNodeLevel(2);
		dcs2Group.setPre(h1Group);
		dcs2Group.setNodes(new ArrayList<Node>());
		dcs = new Node();
		dcs.setId(6);
		dcs.setIp("192.168.1.7");
		dcs.setPort(9002);
		dcs.setFunc(dcs2Group.getFunc());
		dcs.setGroupId(dcs2Group.getId());
		dcs2Group.getNodes().add(dcs);
		h1Group.getNextGroups().add(dcs2Group);

		/**
		 * DCH2
		 */
		h2Group = new NodeGroup();
		groups.add(h2Group);
		h2Group.setFunc(NodeFunc.processer);
		h2Group.setId(4);
		h2Group.setName("processer2");
		h2Group.setNodeLevel(1);
		h2Group.setNextGroups(new ArrayList<NodeGroup>());
		h2Group.setNodes(new ArrayList<Node>());
		Node h21 = new Node();
		h21.setId(7);
		h21.setIp("192.168.1.8");
		h21.setPort(9001);
		h21.setFunc(h2Group.getFunc());
		h21.setGroupId(h2Group.getId());

		h2Group.getNodes().add(h21);
		/**
		 * DCS3
		 */
		dcs3Group = new NodeGroup();
		groups.add(dcs3Group);
		dcs3Group.setFunc(NodeFunc.collector);
		dcs3Group.setId(5);
		dcs3Group.setName("collector3");
		dcs3Group.setNodeLevel(2);
		dcs3Group.setPre(h2Group);
		dcs3Group.setNodes(new ArrayList<Node>());
		dcs = new Node();
		dcs.setId(8);
		dcs.setIp("192.168.1.9");
		dcs.setPort(9002);
		dcs.setFunc(dcs3Group.getFunc());
		dcs.setGroupId(dcs3Group.getId());
		dcs3Group.getNodes().add(dcs);
		h2Group.getNextGroups().add(dcs3Group);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		table = null;
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.NodeTable#getNodeInGroup(int)}.
	 */
	@Test
	public void testGetNodeInGroup() {
		int nodeGroupId = 1;
		Node node = table.getNodeInGroup(nodeGroupId);
		assertNotNull(node);
		assertEquals(NodeFunc.processer, node.getFunc());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.NodeTable#selectNode(java.lang.String, int)}
	 * .
	 */
	@Test
	public void testSelectNode() {
		Node node = table.selectNode("192.168.1.6", 9002);
		assertNotNull(node);
		assertEquals("192.168.1.6", node.getIp());
		assertEquals(9002, node.getPort());
		node = table.selectNode("192.168.1.6", 90002);
		assertNull(node);
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.NodeTable#selectNextNodeGroup(com.mainsteam.stm.node.NodeGroup, java.lang.String, int)}
	 * .
	 */
	@Test
	public void testSelectNextNodeGroup() {
		/**
		 * dcs1->dhs1
		 */
		Node distNode = h1Group.getNodes().get(0);
		NodeGroup nextGroup = table.selectNextNodeGroup(dcs1Group,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h1Group.getId(),nextGroup.getId());
		assertEquals(h1Group,nextGroup);
		
		
		/**
		 * dcs1->dhs2
		 */
		distNode = h2Group.getNodes().get(0);
		nextGroup = table.selectNextNodeGroup(dcs1Group,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h1Group.getId(),nextGroup.getId());
		assertEquals(h1Group,nextGroup);
		
		nextGroup = table.selectNextNodeGroup(nextGroup,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h2Group.getId(),nextGroup.getId());
		assertEquals(h2Group,nextGroup);
		
		/**
		 * dcs1->portal
		 */
		distNode = portalGroup.getNodes().get(0);
		nextGroup = table.selectNextNodeGroup(dcs1Group,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h1Group.getId(),nextGroup.getId());
		assertEquals(h1Group,nextGroup);
		
		nextGroup = table.selectNextNodeGroup(nextGroup,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(portalGroup.getId(),nextGroup.getId());
		assertEquals(portalGroup,nextGroup);
		
		/**
		 * dcs3->portal
		 */
		distNode = portalGroup.getNodes().get(0);
		nextGroup = table.selectNextNodeGroup(dcs3Group,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h2Group.getId(),nextGroup.getId());
		assertEquals(h2Group,nextGroup);
		
		nextGroup = table.selectNextNodeGroup(nextGroup,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(portalGroup.getId(),nextGroup.getId());
		assertEquals(portalGroup,nextGroup);
		
		/**
		 * dcs1->dcs3
		 */
		distNode = dcs3Group.getNodes().get(0);
		nextGroup = table.selectNextNodeGroup(dcs1Group,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h1Group.getId(),nextGroup.getId());
		assertEquals(h1Group,nextGroup);
		
		nextGroup = table.selectNextNodeGroup(nextGroup,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h2Group.getId(),nextGroup.getId());
		assertEquals(h2Group,nextGroup);
		
		nextGroup = table.selectNextNodeGroup(nextGroup,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(dcs3Group.getId(),nextGroup.getId());
		assertEquals(dcs3Group,nextGroup);
		
		
		/**
		 *dhs1-> dhs2
		 */
		distNode = h2Group.getNodes().get(0);
		nextGroup = table.selectNextNodeGroup(h1Group,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h2Group.getId(),nextGroup.getId());
		assertEquals(h2Group,nextGroup);
		
		/**
		 *dhs1-> dcs1
		 */
		distNode = dcs1Group.getNodes().get(0);
		nextGroup = table.selectNextNodeGroup(h1Group,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(dcs1Group.getId(),nextGroup.getId());
		assertEquals(dcs1Group,nextGroup);
		
		
		/**
		 *dhs1-> dcs3
		 */
		distNode = dcs3Group.getNodes().get(0);
		nextGroup = table.selectNextNodeGroup(h1Group,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(h2Group.getId(),nextGroup.getId());
		assertEquals(h2Group,nextGroup);
		
		nextGroup = table.selectNextNodeGroup(nextGroup,distNode.getIp(),distNode.getPort());
		assertNotNull(nextGroup);
		assertEquals(dcs3Group.getId(),nextGroup.getId());
		assertEquals(dcs3Group,nextGroup);
		
		
		
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.node.NodeTable#selectNodesByType(com.mainsteam.stm.node.NodeFunc)}
	 * .
	 */
	@Test
	public void testSelectNodesByType() {
		fail("Not yet implemented");
	}

}
