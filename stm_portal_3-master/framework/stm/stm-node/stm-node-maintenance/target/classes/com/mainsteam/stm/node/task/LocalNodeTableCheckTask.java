/**
 * 
 */
package com.mainsteam.stm.node.task;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeManager;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.NodeTableSummary;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;
import com.mainsteam.stm.node.manager.LocaleTableManager;
import com.mainsteam.stm.util.OSUtil;

/**
 * @author ziw
 * 
 */
public class LocalNodeTableCheckTask implements Runnable {

	private static final Log logger = LogFactory
			.getLog(LocalNodeTableCheckTask.class);

	private LocaleTableManager tableManager;

	private NodeService nodeService;

	private NodeManager nodeManager;

	private long interval = 10000;// 10 second.

	private boolean running = false;

	private int heartbeatCount = 0;

	/**
	 * @param nodeManager
	 *            the nodeManager to set
	 */
	public final void setNodeManager(NodeManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	/**
	 * @param tableManager
	 *            the tableManager to set
	 */
	public final void setTableManager(LocaleTableManager tableManager) {
		this.tableManager = tableManager;
	}

	/**
	 * @param nodeService
	 *            the nodeService to set
	 */
	public final void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * 
	 */
	public LocalNodeTableCheckTask() {
	}

	public synchronized void start() {
		if (running) {
			return;
		}
		if (OSUtil.getEnv("testCase") != null) {
			return;
		}
		Node currentNode = tableManager.getCurrentNode();
		if (currentNode != null) {
			/**
			 * 记录启动时间
			 */
			nodeManager.startNode(currentNode.getId(),
					System.currentTimeMillis());
		}
		check();
		running = true;
		new Thread(this, "LocalNodeTableCheckTask").start();
	}

	private void check() {
		if (logger.isInfoEnabled()) {
			logger.info("check node start.");
		}
		NodeTable dbTable = null;
		NodeTable currentTable = null;
		boolean isUpdate = false;
		Node currentNode = null;
		NodeGroup currentNodeGroup = null;
		try {
			currentNode = tableManager.getCurrentNode();
			currentNodeGroup = tableManager.getCurrentGroup();
			NodeTableSummary summary = nodeManager.selectNodeTableSummary();
			dbTable = nodeService.getNodeTable();
			currentTable = tableManager.getNodeTable();
			isUpdate = LocalNodeTableChecker.checkUpdate(currentTable,
					summary.getUpdateTime(), summary.getNodeGroupSize(),
					summary.getNodesize(),currentNode,currentNodeGroup,dbTable);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("checkNode", e);
			}
			return;
		}
		if (currentNode == null) {
			String msg = "currentNode is null.currentNodeId="
					+ tableManager.getCurrentNodeId();
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			throw new RuntimeException(msg);
		}
		if (isUpdate) {
			try {
				tableManager.saveNodeTable(currentNode, dbTable);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("checkNode", e);
				}
			}
		}
		try {
			heartbeat(nodeManager, currentNode.getId());
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
