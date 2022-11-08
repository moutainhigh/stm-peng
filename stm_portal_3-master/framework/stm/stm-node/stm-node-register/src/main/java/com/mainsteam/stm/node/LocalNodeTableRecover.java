package com.mainsteam.stm.node;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.node.manager.LocaleTableManager;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.util.SpringBeanUtil;

public class LocalNodeTableRecover {

	private static final Log logger = LogFactory
			.getLog(LocalNodeTableRecover.class);

	public LocalNodeTableRecover() {
	}

	protected static void info(String msg) {
		System.out.println(msg);
		if (logger.isInfoEnabled()) {
			logger.info(msg);
		}
	}

	protected static void error(String msg, Throwable e) {
		System.err.println(msg);
		if (e != null) {
			e.printStackTrace();
		}
		if (logger.isErrorEnabled()) {
			if (e != null) {
				logger.error(msg, e);
			} else {
				logger.error(msg);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		info("start reload node.");
		int currentNode = -1;
		if (args.length > 0) {
			if (StringUtils.isNotEmpty(args[0])
					&& NumberUtils.isNumber(args[0])) {
				currentNode = NumberUtils.toInt(args[0]);
			}
		}
		info("start reload node.input currentNode=" + currentNode);
		String parentIp = null;
		if (args.length > 1) {
			parentIp = args[1];
		}
		info("start reload node.parentIp=" + parentIp);
		int portPort = -1;
		if (args.length > 2) {
			if (StringUtils.isNotEmpty(args[2])
					&& NumberUtils.isNumber(args[2])) {
				portPort = NumberUtils.toInt(args[2]);
			}
		}
		info("start reload node.portPort=" + portPort);
		new LocalNodeTableRecover().reloadNodeTable(currentNode, parentIp,
				portPort);
	}

	public void reloadNodeTable(int currentNodeId, String parentIp, int portPort) {
		NodeManager nodeManager;
		LocaleTableManager localeTableManager = SpringBeanUtil
				.getBean(LocaleTableManager.class);
		String serviceType = System.getenv("serverType");
		info("start reload node.serviceType=" + serviceType);
		try {
			if ("collector".equals(serviceType)) {
				OCRPCClient client = SpringBeanUtil.getBean(OCRPCClient.class);
				if (parentIp != null && portPort > 0) {
					Node node = new Node();
					node.setIp(parentIp);
					node.setPort(portPort);
					nodeManager = client.getRemoteSerivce(node,
							RemoteNodeManagerMBean.class);
					info("Load Node From parent.parentIp=" + parentIp
							+ " portPort=" + portPort);
				} else {
					nodeManager = client.getParentRemoteSerivce(
							NodeFunc.processer, RemoteNodeManagerMBean.class);
				}
			} else {
				nodeManager = (NodeManager) SpringBeanUtil
						.getObject("nodeManager");
			}
			NodeTable nodeTable = nodeManager.getNodeTable();
			int oldCurrentNodeId = localeTableManager.getCurrentNodeId();
			info("load oldCurrentNodeId=" + oldCurrentNodeId);
			if (currentNodeId < 0 && oldCurrentNodeId >= 0) {
				currentNodeId = oldCurrentNodeId;
			}
			if (currentNodeId < 0) {
				error("currentNodeId is not valid.currentNodeId="
						+ currentNodeId, null);
				return;
			} else {
				info("used currentNodeId is " + currentNodeId);
			}
			boolean findCurrentNodeId = false;
			if (currentNodeId > 0) {
				List<NodeGroup> nodeGroups = nodeTable.getGroups();
				if (nodeGroups != null && nodeGroups.size() > 0) {
					for (NodeGroup nodeGroup : nodeGroups) {
						List<Node> nodes = nodeGroup.getNodes();
						if (nodes != null && nodes.size() > 0) {
							for (Node node : nodes) {
								info("list one nodeId=" + node.getId());
								if (node.getId() == currentNodeId) {
									findCurrentNodeId = true;
									// break;
									info("find node="+JSON.toJSONString(node));
								}
							}
						}
					}
				}
			}
			if (findCurrentNodeId == false) {
				error("Error:nodeId is not valid.nodeId=" + currentNodeId, null);
			} else {
				Node node = new Node();
				node.setId(currentNodeId);
				localeTableManager.saveNodeTable(node, nodeTable);
				info("Success:Reload ok.");
			}
		} catch (Exception e) {
			error("", e);
		} finally {
			System.exit(0);
		}
	}
}
