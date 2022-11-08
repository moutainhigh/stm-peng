/**
 * 
 */
package com.mainsteam.stm.node;

import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.manager.LocaleTableManager;

/**
 * @author ziw
 * 
 */
public abstract class AbstractLocaleNodeService implements LocaleNodeService {

//	private static final Log logger = LogFactory
//			.getLog(AbstractLocaleNodeService.class);

	protected LocaleTableManager tableManager;

//	private boolean running = true;

	/**
	 * 
	 */
	public AbstractLocaleNodeService() {
	}

	public void start() {
		/**
		 * 定时刷新本地节点表数据
		 */
//		new Thread(new NodeFresh(), "tLocaleNodeFresh").start();
	}

	/**
	 * @param tableManager
	 *            the tableManager to set
	 */
	public final void setTableManager(LocaleTableManager tableManager) {
		this.tableManager = tableManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.LocaleNodeService#getCurrentNode()
	 */
	@Override
	public Node getCurrentNode() throws NodeException {
		return tableManager.getCurrentNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.LocaleNodeService#getCurrentNodeGroup()
	 */
	@Override
	public NodeGroup getCurrentNodeGroup() throws NodeException {
		return tableManager.getCurrentGroup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.LocaleNodeService#getLocalNodeTable()
	 */
	@Override
	public NodeTable getLocalNodeTable() throws NodeException {
		return tableManager.getNodeTable();
	}

	public void saveNodeTable(Node currentNode, NodeTable table)
			throws NodeException {
		tableManager.saveNodeTable(currentNode, table);
	}

	/**
	 * 从 数据库中加载节点表
	 * 
	 * @return
	 * @throws NodeException
	 */
//	public abstract NodeTable getNodeTableFromDb() throws NodeException;
//
//	private class NodeFresh implements Runnable {
//		@Override
//		public void run() {
//			while (running) {
//				try {
//					Node currentNode = getCurrentNode();
//					if (currentNode != null) {
//						NodeTable t = getNodeTableFromDb();
//						tableManager.saveNodeTable(currentNode, t);
//					}
//				} catch (NodeException e) {
//					if (logger.isErrorEnabled()) {
//						logger.error("run", e);
//					}
//				}
//				/**
//				 * 每过10秒钟，刷新一次本地节点
//				 */
//				synchronized (this) {
//					try {
//						this.wait(10000);
//					} catch (InterruptedException e) {
//					}
//				}
//			}
//
//		}
//
//	}
}
