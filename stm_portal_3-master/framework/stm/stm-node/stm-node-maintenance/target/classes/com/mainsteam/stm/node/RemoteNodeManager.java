/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.license.remote.RemoteLicenseMBean;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;
import com.mainsteam.stm.rpc.client.OCRPCClient;

/**
 * @author ziw
 * 
 */
public class RemoteNodeManager implements RemoteNodeManagerMBean {

	private static final Log logger = LogFactory
			.getLog(RemoteNodeManager.class);

	private NodeManager nodeManager;

	private OCRPCClient client;

	/**
	 * @param client
	 *            the client to set
	 */
	public final void setClient(OCRPCClient client) {
		this.client = client;
	}

	/**
	 * @param nodeManager
	 *            the nodeManager to set
	 */
	public final void setNodeManager(NodeManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	/**
	 * 
	 */
	public RemoteNodeManager() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeManager#registerNode(com.mainsteam.stm.node
	 * .Node)
	 */
	@Override
	public NodeTable registerNode(Node node) throws NodeException {
		throw new NodeException(1001, "not support.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeManager#loadGroupNodeId(java.lang.String,
	 * int)
	 */
	@Override
	public int loadGroupNodeId(String nodeIp, int nodePort)
			throws NodeException {
		return nodeManager.loadGroupNodeId(nodeIp, nodePort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeManager#getGroups(com.mainsteam.stm.node
	 * .NodeFunc)
	 */
	@Override
	public List<NodeGroup> getGroups(NodeFunc func) throws NodeException {
		return nodeManager.getGroups(func);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeManager#getNodeTable()
	 */
	@Override
	public NodeTable getNodeTable() throws NodeException {
		return nodeManager.getNodeTable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeManager#removeNodeById(int)
	 */
	@Override
	public void removeNodeById(int nodeId) throws NodeException {
		nodeManager.removeNodeById(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeManager#selectNodeTableSummary()
	 */
	@Override
	public NodeTableSummary selectNodeTableSummary() {
		return nodeManager.selectNodeTableSummary();
	}

	@Override
	public NodeTable registerChildNode(Node node) throws NodeException {
		if (node.getFunc() == NodeFunc.collector) {
			// check dcs node count license
			int count = 0;
			NodeTable nodeTable = nodeManager.getNodeTable();
			List<Node> collectorNodes = nodeTable
					.selectNodesByType(NodeFunc.collector);
			if (collectorNodes != null && collectorNodes.size() > 0) {
				count += collectorNodes.size();
			}
			if (logger.isInfoEnabled()) {
				logger.info("registerChildNode current node count=" + count);
			}
			if (count > 0) {
				int licenseCount = Integer.MAX_VALUE;
				List<Node> portalNodes = nodeTable
						.selectNodesByType(NodeFunc.portal);
				RemoteLicenseMBean licenseMBean = null;
				Node portalNode = null;
				if (portalNodes != null && portalNodes.size() > 0) {
					for (Node portal : portalNodes) {
						if (logger.isInfoEnabled()) {
							logger.info("registerChildNode get licenseMBean from "
									+ portal);
						}
						try {
							licenseMBean = client.getDirectRemoteSerivce(
									portal, RemoteLicenseMBean.class);
							portalNode = portal;
							break;
						} catch (IOException e) {
							if (logger.isErrorEnabled()) {
								logger.error("registerChildNode", e);
							}
						}
					}
				}

				// get licenseCount from portal.
				if (licenseMBean == null) {
					throw new NodeException(0,
							"LicenseCheck found error.licenseMBean is invalid.");
				} else {
					try {
						licenseCount = licenseMBean.getDcsCount();
						if (logger.isInfoEnabled()) {
							logger.info("registerChildNode get licenseCount="
									+ licenseCount + " from portal node="
									+ portalNode);
						}
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("registerChildNode", e);
						}
						throw new NodeException(0,
								"LicenseCheck found error.licenseMBean.getDcsCount is invalid."
										+ e.getMessage());
					}
				}
				if (count >= licenseCount) {
					if (logger.isErrorEnabled()) {
						logger.error("registerChildNode error.Dcs's count ReachLicenseCount.licenseCount="
								+ licenseCount);
					}
					throw new NodeException(0, "ReachLicenseCount");
				}
			}
		}
		return nodeManager.registerChildNode(node);
	}

	@Override
	public void startNode(int nodeId, long startTime) {
		nodeManager.startNode(nodeId, startTime);
	}

	@Override
	public void heartbeat(NodeHeartbeat heartbeat) {
		nodeManager.heartbeat(heartbeat);
	}
}
