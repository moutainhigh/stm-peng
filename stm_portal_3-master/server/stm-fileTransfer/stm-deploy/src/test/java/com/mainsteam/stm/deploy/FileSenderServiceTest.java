package com.mainsteam.stm.deploy;

import java.net.URL;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.deploy.obj.DeployRecord;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
"classpath*:META-INF/services/portal-*-beans.xml" })
public class FileSenderServiceTest {

	@Autowired FileSenderService fileSenderService;
	@Autowired LocaleNodeService localNodeService;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	@Test
	public void testSendFile(){
		URL url=FileSenderServiceTest.class.getResource("transferTest.zip");
		fileSenderService.sendFile(url.getFile(), "transferTest.zip","123123");
	}
	
	@Test
	public void testSendFileToNode() throws NodeException{
		URL url=FileSenderServiceTest.class.getResource("transferTest.zip");
		
		NodeTable nodeTable=localNodeService.getLocalNodeTable();
		List<NodeGroup> groupList=nodeTable.getGroups();
		
		for(NodeGroup group:groupList){
			List<Node> nodes=group.getNodes();
			 
			fileSenderService.sendFile(url.getFile(), "transferTest.zip", nodes, "234234");
		}
		
	}
	
	
	@Test
	public void testGetDeployRecordBySourceID(){
		List<DeployRecord> rst= fileSenderService.getDeployRecordBySourceID("123123");
		System.out.println("result:"+JSON.toJSONString(rst));
	}
}
