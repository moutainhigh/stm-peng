/**
 * 
 */
package com.mainsteam.stm.node.task;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeManager;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.NodeTableSummary;
import com.mainsteam.stm.node.RemoteNodeManagerMBean;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;
import com.mainsteam.stm.node.manager.LocaleTableManager;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.util.OSUtil;

/**
 * @author ziw
 * 
 */
public class LocalNodeTableCheckTaskForCollector implements Runnable {

	private static final Log logger = LogFactory
			.getLog(LocalNodeTableCheckTaskForCollector.class);

	private LocaleTableManager tableManager;

	private OCRPCClient client;

	private long interval = 10000;// 10 second.

	private boolean running = false;

	private int heartbeatCount;

	/**
	 * @param tableManager
	 *            the tableManager to set
	 */
	public final void setTableManager(LocaleTableManager tableManager) {
		this.tableManager = tableManager;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public final void setClient(OCRPCClient client) {
		this.client = client;
	}

	/**
	 * 
	 */
	public LocalNodeTableCheckTaskForCollector() {
	}

	public void start() {
		if (running) {
			return;
		}
		if (OSUtil.getEnv("testCase") != null) {
			return;
		}
		Node parentGroupNode = getParentNode();
		Node currentNode = tableManager.getCurrentNode();
		RemoteNodeManagerMBean manager = null;
		try {
			manager = client.getRemoteSerivce(parentGroupNode,
					RemoteNodeManagerMBean.class);
			/**
			 * 记录启动时间
			 */
			manager.startNode(currentNode.getId(), System.currentTimeMillis());
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("run", e);
			}
			return;
		}
		check();
		running = true;
		new Thread(this, "LocalNodeTableCheckTaskForCollector").start();
	}

	private Node getParentNode() {
		NodeGroup group = tableManager.getCurrentGroup();
		NodeGroup processerGroup = group.getPre();
		while (processerGroup != null
				&& processerGroup.getFunc() != NodeFunc.processer) {
			processerGroup = group.getPre();
		}
		Node parentGroupNode = processerGroup.selectNode();
		return parentGroupNode;
	}

	private void check() {
		if (logger.isInfoEnabled()) {
			logger.info("check node start.");
		}
		RemoteNodeManagerMBean manager = null;
		boolean isUpdate = false;
		NodeTable dbTable = null;
		NodeTable currentTable = null;
		NodeGroup currentNodeGroup = null;
		Node currentNode = null;
		try {
			currentNode = tableManager.getCurrentNode();
			currentNodeGroup = tableManager.getCurrentGroup();
			NodeTable table = tableManager.getNodeTable();
			Node parentGroupNode = getParentNode();
			try {
				manager = client.getRemoteSerivce(parentGroupNode,
						RemoteNodeManagerMBean.class);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("run", e);
				}
				return;
			}

			NodeTableSummary summary = manager.selectNodeTableSummary();
			dbTable = manager.getNodeTable();
			currentTable = tableManager.getNodeTable();
			isUpdate = LocalNodeTableChecker.checkUpdate(currentTable,
					summary.getUpdateTime(), summary.getNodeGroupSize(),
					summary.getNodesize(),currentNode,currentNodeGroup,dbTable);
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("check", e1);
			}
			return;
		}
		if (isUpdate) {
			try {
				tableManager.saveNodeTable(currentNode, dbTable);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("check", e);
				}
				return;
			}
		}
		try {
			heartbeat(manager, currentNode.getId());
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("checkNode", e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("check node end.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (running) {
			check();
			synchronized (this) {
				try {
					this.wait(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void heartbeat(NodeManager manager, int nodeId) {
		heartbeatCount++;
		if (logger.isInfoEnabled()) {
			logger.info("heartbeat start.heartbeatCount=" + heartbeatCount);
		}
		long currentTime = System.currentTimeMillis();
		long nextTime = currentTime + interval;
		long expireOccurtime = currentTime + interval * 3;// 3次心跳为失效时间

		NodeHeartbeat heartbeat = new NodeHeartbeat();
		heartbeat.setNodeId(nodeId);
		heartbeat.setOccurCount(heartbeatCount);
		heartbeat.setOccurtime(new Date(currentTime));
		heartbeat.setNextOccurtime(new Date(nextTime));
		heartbeat.setExpireOccurtime(new Date(expireOccurtime));
		manager.heartbeat(heartbeat);
		if (logger.isInfoEnabled()) {
			logger.info("heartbeat end.");
		}
	}
}
