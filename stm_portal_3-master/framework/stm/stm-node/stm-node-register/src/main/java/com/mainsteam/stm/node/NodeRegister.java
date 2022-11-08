/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.manager.LocaleTableManager;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.util.OSUtil;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * 该类用来提供给bat脚本执行，并提供给安装包使用，用来注册分布式节点
 * 
 * @author ziw
 * 
 */
public class NodeRegister {

	private static final Log logger = LogFactory.getLog(NodeRegister.class);

	/**
	 * 
	 */
	public NodeRegister() {
	}

	public static void loadAndPrintFuncNodeList(NodeFunc func, String parentIp,
			int parentPort) {
		List<NodeGroup> groups = null;
		if (func == NodeFunc.collector) {
			OCRPCClient client = (OCRPCClient) SpringBeanUtil
					.getObject("OCRPClient");
			Node parentNode = new Node();
			parentNode.setIp(parentIp);
			parentNode.setPort(parentPort);
			try {
				RemoteNodeManagerMBean mbean = client.getRemoteSerivce(
						parentNode, RemoteNodeManagerMBean.class);
				groups = mbean.getGroups(NodeFunc.collector);
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadAndPrintFuncNodeList", e);
				}
				error("Error:400,IO Fail is occur.");
				exit();
			} catch (NodeException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadAndPrintFuncNodeList", e);
				}
				error("Error:401,Node Operation Fail is occur.");
				exit();
			}
		} else {
			NodeService localeNodeService = SpringBeanUtil
					.getBean(NodeService.class);
			try {
				NodeTable table = localeNodeService.getNodeTable();
				List<NodeGroup> allGroups = table.getGroups();
				groups = new ArrayList<>();
				if (allGroups != null && allGroups.size() > 0) {
					for (NodeGroup nodeGroup : allGroups) {
						if (func == nodeGroup.getFunc()) {
							groups.add(nodeGroup);
						}
					}
				}
			} catch (NodeException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadAndPrintFuncNodeList", e);
				}
				e.printStackTrace();
				error("Error:402,Node Operation Fail is occur. ");
				exit();
			}
		}
		StringBuilder b = new StringBuilder();
		b.append("Success:0,content is below.\n");
		if (groups != null && groups.size() > 0) {
			spellTable(groups, b);
		}
		info(b.toString());
	}

	private static void spellTable(List<NodeGroup> groups, StringBuilder b) {
		if (groups != null && groups.size() > 0) {
			for (int i = 0; i < groups.size(); i++) {
				NodeGroup g = groups.get(i);
				b.append(g.getId()).append(',').append(g.getName()).append(',')
						.append(g.getNodeLevel()).append(',')
						.append(g.getFunc().name()).append(',').append('[')
						.append('\n');
				List<Node> nodeList = g.getNodes();
				if (nodeList != null && nodeList.size() > 0) {
					int length = nodeList.size();
					for (int j = 0; j < length; j++) {
						Node n = nodeList.get(j);
						b.append(n.getId()).append(',').append(n.getIp())
								.append(',').append(n.getPort()).append(',')
								.append(n.getFunc().name()).append(',')
								.append(n.getPriority()).append('\n');
					}
				}
				b.append(']').append('\n');
			}
		}
	}

	private static void validateLocalIp(String localIp, int localPort) {
		if (localPort <= 0 && localPort > 65534) {
			error("Error:10001,Invalid port range.");
			exit();
		}
		String ipValue = null;
		String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
		if (localIp.matches(regex)) {
			ipValue = localIp;
		} else {
			// domain
			try {
				InetAddress address = InetAddress.getByName(localIp);
				ipValue = address.getHostAddress();
				info("get ip from domain domain=" + localIp + " ip=" + ipValue);
			} catch (UnknownHostException e) {
				info(e.getMessage());
			}
		}
		if(StringUtils.isEmpty(ipValue)){
			error("Error:10005,cannt get ipaddress for domain="+localIp);
			exit();
		}
		String[] localIps = OSUtil.getAllLocalHostIP();
		boolean validIp = false;
		for (int i = 0; i < localIps.length; i++) {
			if (ipValue.equals(localIps[i])) {
				validIp = true;
				break;
			}
		}
		if (validIp == false) {
			StringBuilder b = new StringBuilder();
			b.append("Error:1000,Invalid Local IP Address.Please select one from[");
			if (localIps.length > 0) {
				for (int i = 0; i < localIps.length - 1; i++) {
					if (localIps[i].equals("127.0.0.1")) {
						continue;
					}
					b.append(localIps[i]).append(',');
				}
				b.append(localIps[localIps.length - 1]);
			}
			b.append(']');
			error(b.toString());
			exit();
		}
	}

	protected static void validateRunning() {
		LocaleTableManager manager = (LocaleTableManager) SpringBeanUtil
				.getObject("localeTableManager");
		Node currentNode = manager.getCurrentNode();
		if (currentNode != null) {
			// info("validateRunning currentNode="+JSON.toJSONString(currentNode));
			String ip = currentNode.getIp();
			int port = currentNode.getPort();
			try {
				OCRPCClient client = (OCRPCClient) SpringBeanUtil
						.getObject("OCRPClient");
				Node n = new Node();
				n.setIp(ip);
				n.setPort(port);
				NodeCheckMBean mbean = client.getDirectRemoteSerivce(n,
						NodeCheckMBean.class);
				NodeState state = mbean.checkNode();
				if (state != null && state.isRunning()) {
					error("Error:601,Current Node is running.Please stop first.");
				}
			} catch (Throwable e) {
				if (logger.isInfoEnabled()) {
					logger.info("validateRunning", e);
				}
			}
		}
	}

	private static void validateHasRegistered() {
		LocaleTableManager manager = (LocaleTableManager) SpringBeanUtil
				.getObject("localeTableManager");
		Node currentNode = manager.getCurrentNode();
		info("validateHasRegistered currentNode="
				+ JSON.toJSONString(currentNode));
		if (currentNode != null) {
			String ip = currentNode.getIp();
			int port = currentNode.getPort();
			if (StringUtils.isNotEmpty(ip) && port >= 0) {
				error("Error:600,nodeTables.xml has been registered.Clear the file to register again.");
				exit();
			}
		}
	}

	private static void doRegister(NodeFunc func, String parentIp,
			int parentPort, String localIp, int localPort, int groupId,
			String nodePath) {
		info("doRegister start.");
		validateRunning();
		validateHasRegistered();
//		String isValidIp = OSUtil.getEnv("validateIp", "true");
//		if ("true".equalsIgnoreCase(isValidIp)) {
//			validateLocalIp(localIp, localPort);
//		}

		Node parentNode = new Node();
		parentNode.setIp(parentIp);
		parentNode.setPort(parentPort);

		Node currentNode = new Node();
		currentNode.setFunc(func);
		currentNode.setGroupId(groupId);
		currentNode.setIp(localIp);
		currentNode.setPort(localPort);
		currentNode.setName(localIp + ':' + localPort);
		currentNode.setInstallPath(nodePath);
		NodeTable table = null;
		RemoteNodeManagerMBean managerMbean = null;
		if (func == NodeFunc.collector) {
			OCRPCClient client = (OCRPCClient) SpringBeanUtil
					.getObject("OCRPClient");
			try {
				managerMbean = client.getRemoteSerivce(parentNode,
						RemoteNodeManagerMBean.class);
				try {
					info("ready to register current node to parent.");
					table = managerMbean.registerChildNode(currentNode);
					currentNode = table.selectNode(localIp, localPort);
					if (currentNode == null) {

						error("Error:505,currentNode cannt be found from table.");
						StringBuilder b = new StringBuilder();
						b.append("Get table content is \n");
						spellTable(table.getGroups(), b);
						info(b.toString());
						exit();
					}
					LocaleTableManager manager = (LocaleTableManager) SpringBeanUtil
							.getObject("localeTableManager");
					try {
						manager.saveNodeTable(currentNode, table);
					} catch (NodeException e) {
						if (logger.isErrorEnabled()) {
							logger.error("doRegister", e);
						}
						e.printStackTrace();

						error("Error:502:saveNodetable to local error.");
					}
					info("register current node ok.");
				} catch (NodeException e) {
					e.printStackTrace();
					if (logger.isErrorEnabled()) {
						logger.error("loadAndPrintFuncNodeList", e);
					}
					if (e.getMessage().equals("ReachLicenseCount")) {
						error("Error:503:ReachLicenseCount");
					} else {

						error("Error:501,Node Operation Fail is occur.");
					}
					exit();
				}
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadAndPrintFuncNodeList", e);
				}
				e.printStackTrace();
				error("Error:500,IO Fail is occur.");
				exit();
			}
		} else if (func == NodeFunc.processer || func == NodeFunc.trap
				|| func == NodeFunc.portal) {
			NodeManager nodeManager = (NodeManager) SpringBeanUtil
					.getObject("nodeManager");
			if (func == NodeFunc.portal) {
				currentNode.setGroupId(0);
			}
			try {
				nodeManager.registerNode(currentNode);
				// table = proxy.addNode(currentNode, false);
			} catch (NodeException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadAndPrintFuncNodeList", e);
				}
				e.printStackTrace();
				error("Error:501,Node Operation Fail is occur.");
				exit();
			}
		}
		info("Success:0,OK.");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			error("Error:100,Invalid parameter.");
			exit();
		}
		NodeFunc func = null;
		String ip = null;
		int port = -1;
		String command = null;
		int commonIndex = 1;
		if (args.length < commonIndex) {

			error("Error:0,NodeFunc must be input as the first Parameter.");
			exit();
		} else {
			func = NodeFunc.valueOf(args[commonIndex - 1]);
			info("get arg " + args[commonIndex - 1]);
			commonIndex++;
		}
		if (func == null) {
			error("Error:1,NodeFunc is invalid.Please input one of the below.");
			NodeFunc[] funcs = NodeFunc.values();
			for (NodeFunc nodeFunc : funcs) {
				error(nodeFunc.name());
			}
			System.err.println();
			exit();
		}

		if (args.length < commonIndex) {
			error("Error:7,InstallPath is miss.");
			exit();
		}
		String nodePath = args[commonIndex - 1];
		info("get arg " + args[commonIndex - 1]);
		commonIndex++;

		if (func == NodeFunc.collector) {
			if (args.length < commonIndex) {
				error("Error:3,IP and Port is miss");
				exit();
			}
			ip = args[commonIndex - 1];
			info("get arg " + args[commonIndex - 1]);
			commonIndex++;
			if (args.length < commonIndex) {
				error("Error:4,Port is miss.");
				exit();
			}
			try {
				port = Integer.parseInt(args[commonIndex - 1]);
				info("get arg " + args[commonIndex - 1]);
				commonIndex++;
			} catch (Exception e) {
				e.printStackTrace();
				error("Error:200,Port must be number.");
				exit();
			}
		}
		if (args.length < commonIndex) {
			error("Error:5,Command is miss.");
			exit();
		}
		command = args[commonIndex - 1];
		info("get arg " + args[commonIndex - 1]);
		commonIndex++;
		info("command=" + command);
		if ("ListGroups".equalsIgnoreCase(command)) {
			try {
				loadAndPrintFuncNodeList(func, ip, port);
				System.exit(0);
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				error("Error:203,find exception.");
				exit();
			}
		} else if ("Register".equalsIgnoreCase(command)) {
			String localIp = null;
			int localPort = -1;
			int nodeGroupId = -1;
			if (args.length < commonIndex) {
				error("Error:6,LocalIp is miss.");
				exit();
			}
			localIp = args[commonIndex - 1];
			info("get arg " + args[commonIndex - 1]);
			commonIndex++;
			if (args.length < commonIndex) {
				error("Error:4,LocalPort is miss.");
				exit();
			}
			try {
				localPort = Integer.parseInt(args[commonIndex - 1]);
				info("get arg " + args[commonIndex - 1]);
				commonIndex++;
			} catch (Exception e) {
				e.printStackTrace();
				error("Error:201,LocalPort must be number.");
				exit();
			}
			if (args.length >= commonIndex) {
				try {
					nodeGroupId = Integer.parseInt(args[commonIndex - 1]);
					info("get arg " + args[commonIndex - 1]);
					commonIndex++;
				} catch (Throwable e) {
					e.printStackTrace();
					error("Error:202,nodeGroupId must be number.");
					exit();
				}
			} else {
				nodeGroupId = -1;
			}
			try {
				doRegister(func, ip, port, localIp, localPort, nodeGroupId,
						nodePath);
			} catch (Throwable e) {
				e.printStackTrace();
				error("Error:203,find exception.");
				exit();
			}
		} else if ("unRegister".equalsIgnoreCase(command)) {
			doUnRegister();
		} else {
			error("Error:300,Command is invalid.");
			exit();
		}
		System.exit(0);
	}

	private static void doUnRegister() {
		info("doUnRegister start.");
		try {
			LocaleNodeService localeNodeService = SpringBeanUtil
					.getBean(LocaleNodeService.class);
			Node currentNode = localeNodeService.getCurrentNode();
			if (currentNode != null) {
				OCRPCClient client = (OCRPCClient) SpringBeanUtil
						.getObject("OCRPClient");

				NodeManager nodeManager = (NodeManager) SpringBeanUtil
						.getObject("nodeManager");
				if (currentNode.getFunc() == NodeFunc.collector) {
					nodeManager = client.getParentRemoteSerivce(
							NodeFunc.processer, RemoteNodeManagerMBean.class);
				} else {
					nodeManager = (NodeManager) SpringBeanUtil
							.getObject("nodeManager");
				}
				if (nodeManager != null) {
					nodeManager.removeNodeById(currentNode.getId());
					info("remove current node from system ok.");
				}
			} else {
				info("No current was founded from local nodeTables.");
			}
		} catch (Throwable e) {
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error("doUnRegister", e);
			}
			error("Remove node error.Please remove it from database by hand.");
		}
		info("Success:0,OK.");
	}

	private static void exit() {
		error(buildCommonInfo());
		System.exit(0);
	}

	private static String buildCommonInfo() {
		StringBuilder b = new StringBuilder();
		b.append("[parentIp] [parentPort] Command LocalIp LocalPort [LocalGroupId]\n");
		// b.append("LocalNodeFunc's value list[");
		// NodeFunc[] funcs = NodeFunc.values();
		// for (int i = 0; i < funcs.length - 1; i++) {
		// b.append(funcs[i].name()).append(',');
		// }
		// b.append(funcs[funcs.length - 1].name()).append("]\n");

		b.append("Command values has [").append("ListGroups").append(',')
				.append("Register").append(',').append("unRegister")
				.append(']');
		return b.toString();
	}

	protected static void info(String msg) {
		System.out.println(msg);
		if (logger.isInfoEnabled()) {
			logger.info(msg);
		}
	}

	protected static void error(String msg) {
		System.err.println(msg);
		if (logger.isErrorEnabled()) {
			logger.error(msg);
		}
	}

}
