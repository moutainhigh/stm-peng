package com.mainsteam.stm.node;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.mainsteam.stm.node.exception.NodeException;

public class LocaleTableManagerTest {

	@Resource
	private LocaleTableManagerImpl manager;

	@Autowired
	private ApplicationContext applicationContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("setUpBeforeClass");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("tearDownAfterClass1");
//		try {
//			((ClassPathXmlApplicationContext) applicationContext).destroy();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		System.out.println("down the spring context1");
	}
	
	@Before
	public void setUp() throws Exception {
		if(manager == null){
			manager = new LocaleTableManagerImpl();
		}
		String classpath =this.getClass().getResource("").getFile();
		String rootPath =classpath.substring(0,classpath.indexOf("/com"));
		FileUtils.copyFile(new File("src/test/config/nodeTables.xml"), new File(rootPath+"/node/nodeTables.xml"));
		manager.start();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSaveNodeTable() {
		Node currentNode = new Node();
		currentNode.setAlive(true);
		currentNode.setFunc(NodeFunc.collector);
		currentNode.setGroupId(2);
		currentNode.setId(100);
		currentNode.setIp("192.168.3.2");
		currentNode.setPort(9000);
		currentNode.setName("nodeTest");
		NodeTable t = new NodeTable();
		List<NodeGroup> groups = new ArrayList<>();
		NodeGroup collectorGroup = new NodeGroup();
		collectorGroup.setFunc(NodeFunc.collector);
		collectorGroup.setId(2);
		collectorGroup.setName("collectorGroup");
		collectorGroup.setNodeLevel(2);
		collectorGroup.setNodes(new ArrayList<Node>());
		collectorGroup.getNodes().add(currentNode);
		groups.add(collectorGroup);

		Node processerNode = new Node();
		processerNode.setAlive(true);
		processerNode.setFunc(NodeFunc.processer);
		processerNode.setGroupId(1);
		processerNode.setId(50);
		processerNode.setIp("192.168.3.5");
		processerNode.setPort(9000);
		processerNode.setName("processerNodeTest");
		NodeGroup processerGroup = new NodeGroup();
		processerGroup.setFunc(NodeFunc.collector);
		processerGroup.setId(2);
		processerGroup.setName("processerNodegroup");
		processerGroup.setNodeLevel(2);
		processerGroup.setNodes(new ArrayList<Node>());
		processerGroup.getNodes().add(processerNode);
		groups.add(processerGroup);

		t.setGroups(groups);
		try {
			manager.saveNodeTable(currentNode, t);
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetCurrentNode() {
		Node currentNode = manager.getCurrentNode();
		assertNotNull(currentNode);
		assertEquals(1, currentNode.getId());
		assertEquals("localhost", currentNode.getIp());
		assertEquals(9999, currentNode.getPort());
		assertEquals("portal", currentNode.getFunc().name());
		assertEquals(0, currentNode.getGroupId());
		assertEquals(0, currentNode.getPriority());
	}

	@Test
	public void testGetNodeTable() {
		NodeTable t = manager.getNodeTable();
		assertNotNull(t);
	}

	@Test
	public void testGetCurrentGroup() {
		// fail("Not yet implemented");
	}

	@Test
	public void testRecoverConfig() {
		testSaveNodeTable();
		File nodeFile= new File("bin/node/nodeTables.xml");
		String content = null;
		try {
			content = FileUtils.readFileToString(nodeFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			fail();
		}
		assertNotNull(content);
		System.out.println("read first from file = "+content);
		nodeFile.delete();
		assertFalse(nodeFile.exists());
		try {
			manager.start();
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(nodeFile.exists());
		String newContent = null;
		try {
			newContent = FileUtils.readFileToString(nodeFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			fail();
		}
		assertNotNull(newContent);
		System.out.println("read first newContent from file = "+newContent);
		assertEquals(content,newContent);
		
		try {
			FileUtils.write(nodeFile, "");
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		try {
			assertEquals("",FileUtils.readFileToString(nodeFile));
		} catch (IOException e2) {
			e2.printStackTrace();
			fail();
		}
		try {
			manager.start();
		} catch (NodeException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(nodeFile.exists());
		try {
			newContent = FileUtils.readFileToString(nodeFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			fail();
		}
		assertNotNull(newContent);
		System.out.println("read second newContent from file = "+newContent);
		assertEquals(content,newContent);
	}
}
