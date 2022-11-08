/**
 * 
 */
package com.mainsteam.stm.node;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.manager.LocaleTableManager;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * 该类用来提供给bat脚本执行，并提供给安装包使用，用来注册分布式节点
 * 
 * @author ziw
 * 
 */
public class NodeUnRegister {

	private static final Log logger = LogFactory.getLog(NodeUnRegister.class);

	/**
	 * 
	 */
	public NodeUnRegister() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String parentIp = null;
		int port = -1;
		int nodeId = -1;
		int state = -1;
		for (String arg : args) {
			switch (arg) {
			case "-h":
				state = 1;
				break;
			case "-p":
				state = 2;
				break;
			case "-n":
				state = 3;
				break;
			default:
				switch (state) {
				case 1:
					parentIp = arg;
					break;
				case 2:
					try {
						port = Integer.parseInt(arg);
					} catch (Throwable e) {
						e.printStackTrace();
						NodeRegister.error("Error:1,port must be number.");
						exit();
					}
					break;
				case 3:
					try {
						nodeId = Integer.parseInt(arg);
					} catch (Throwable e) {
						e.printStackTrace();
						NodeRegister.error("Error:2,nodeId must be number.");
						exit();
					}
					break;
				default:
					break;
				}
				break;
			}
		}
		doUnRegister(parentIp, port, nodeId);
		System.exit(0);
	}

	private static void doUnRegister(String parentIp, int port, int nodeId) {
		NodeRegister.info("doUnRegister start.");
		NodeRegister.validateRunning();
		Node currentNode = null;
		try {
			LocaleNodeService localeNodeService = SpringBeanUtil
					.getBean(LocaleNodeService.class);
			if (localeNodeService != null) {
				currentNode = localeNodeService.getCurrentNode();
				if (currentNode != null) {
					OCRPCClient client = (OCRPCClient) SpringBeanUtil
							.getObject("OCRPClient");

					NodeManager nodeManager = null;
					if (currentNode.getFunc() == NodeFunc.collector) {
						if (parentIp != null && port > 0) {
							Node node = new Node();
							node.setIp(parentIp);
							node.setPort(port);
							nodeManager = client.getDirectRemoteSerivce(node,
									RemoteNodeManagerMBean.class);
						} else {
							nodeManager = client.getParentRemoteSerivce(
									NodeFunc.processer,
									RemoteNodeManagerMBean.class);
						}
					} else {
						nodeManager = (NodeManager) SpringBeanUtil
								.getObject("nodeManager");
					}
					if (nodeManager != null) {
						nodeManager.removeNodeById(currentNode.getId());
					}
				} else {

					NodeRegister
							.info("No current was founded from local nodeTables.");
				}
			}
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("doUnRegister", e);
			}
			e.printStackTrace();
			NodeRegister
					.error("Remove node error.Please remove it from database by hand.");
		} finally {
			if (currentNode != null) {
				LocaleTableManager manager = (LocaleTableManager) SpringBeanUtil
						.getObject("localeTableManager");
				if (manager != null) {
					NodeTable table = new NodeTable();
					table.setGroups(new ArrayList<NodeGroup>());
					try {
						manager.saveNodeTable(currentNode, table);
					} catch (NodeException e) {
						if (logger.isErrorEnabled()) {
							logger.error("doRegister", e);
						}
						e.printStackTrace();
						
								NodeRegister.error("Error:502:saveNodetable to local error.");
						exit();
					}
				}
				NodeRegister.info("remove current node from system ok.");
			}
		}
		NodeRegister.info("Success:0,OK.");
	}

	private static void exit() {
		NodeRegister.error(buildCommonInfo());
		System.exit(0);
	}

	private static String buildCommonInfo() {
		StringBuilder b = new StringBuilder();
		b.append("[-h parentIp] [-p parentPort] [-n LocalGroupId]\n");
		return b.toString();
	}
}
