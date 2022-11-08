/**
 * 
 */
package com.mainsteam.stm.node.service;

import java.util.List;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeManager;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.NodeTableSummary;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;

/**
 * @author ziw
 * 
 */
public class NodeManagerImpl implements NodeManager {

	private NodeManagerProxy proxy;

	/**
	 * @param proxy
	 *            the proxy to set
	 */
	public final void setProxy(NodeManagerProxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * 
	 */
	public NodeManagerImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeManagerMbean#registerNode(com.mainsteam
	 * .oc.node.Node)
	 */
	@Override
	public NodeTable registerNode(Node node) throws NodeException {
		return proxy.addNode(node, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeManagerMbean#loadGroupNodeId(java.lang.String
	 * , int)
	 */
	@Override
	public int loadGroupNodeId(String nodeIp, int nodePort)
			throws NodeException {
		return proxy.loadGroupNodeId(nodeIp, nodePort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeManagerMbean#getGroups(com.mainsteam.stm
	 * .node.NodeFunc)
	 */
	@Override
	public List<NodeGroup> getGroups(NodeFunc func) throws NodeException {
		return proxy.getGroups(func);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeManagerMbean#getNodeTable()
	 */
	@Override
	public NodeTable getNodeTable() throws NodeException {
		return proxy.getNodeTable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeManagerMbean#removeNodeById(int)
	 */
	@Override
	public void removeNodeById(int nodeId) throws NodeException {
		proxy.removeNodeById(nodeId);
	}

	@Override
	public NodeTableSummary selectNodeTableSummary() {
		return proxy.selectNodeTableSummary();
	}

	@Override
	public NodeTable registerChildNode(Node node) throws NodeException {
		return proxy.addNode(node, true);
	}

	@Override
	public void startNode(int nodeId, long startTime) {
		proxy.startNode(nodeId, startTime);
	}

	@Override
	public void heartbeat(NodeHeartbeat heartbeat) {
		proxy.heartbeat(heartbeat);
	}
}
