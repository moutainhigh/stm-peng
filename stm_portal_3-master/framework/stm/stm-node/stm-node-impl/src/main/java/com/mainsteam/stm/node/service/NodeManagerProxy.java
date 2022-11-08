/**
 * 
 */
package com.mainsteam.stm.node.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.node.LocaleTableManagerImpl;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeGroupService;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.NodeTableSummary;
import com.mainsteam.stm.node.dao.NodeDAO;
import com.mainsteam.stm.node.dao.NodeGroupDAO;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeatService;

/**
 * @author ziw
 * 
 */
public class NodeManagerProxy {

	private static final Log logger = LogFactory.getLog(NodeManagerProxy.class);

	private NodeService nodeService;

	private NodeGroupService groupService;

	private NodeHeartbeatService nodeHeartbeatService;

	private LocaleTableManagerImpl tableManager;

	private NodeDAO nodeDAO;

	private NodeGroupDAO groupDAO;

	/**
	 * @param groupDAO
	 *            the groupDAO to set
	 */
	public final void setGroupDAO(NodeGroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}

	/**
	 * @param nodeHeartbeatService
	 *            the nodeHeartbeatService to set
	 */
	public final void setNodeHeartbeatService(
			NodeHeartbeatService nodeHeartbeatService) {
		this.nodeHeartbeatService = nodeHeartbeatService;
	}

	/**
	 * @param nodeDAO
	 *            the nodeDAO to set
	 */
	public final void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}

	/**
	 * @param tableManager
	 *            the tableManager to set
	 */
	public final void setTableManager(LocaleTableManagerImpl tableManager) {
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
	 * @param groupService
	 *            the groupService to set
	 */
	public final void setGroupService(NodeGroupService groupService) {
		this.groupService = groupService;
	}

	/**
	 * 
	 */
	public NodeManagerProxy() {
	}

	public NodeTable addNode(Node node, boolean registerChild)
			throws NodeException {
		if (logger.isInfoEnabled()) {
			logger.info("addNode start parameter of node="
					+ JSON.toJSONString(node).toString());
		}
		NodeTable t = nodeService.getNodeTable();
		Node selectCurrentNode = t.selectNode(node.getIp(), node.getPort());
		if (selectCurrentNode != null) {
			if (!node.getFunc().equals(selectCurrentNode.getFunc())) {
				throw new NodeException(1009,
						"Node has exist.but register' node is not match to the database.");
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("addNode node has exist." + node);
				}
			}
		} else {
			Node currentNode = null;
			NodeGroup group = groupService.getNodeGroupById(node.getGroupId());
			if (group != null) {
				nodeService.addNode(node);
				currentNode = node;
			} else {
				group = new NodeGroup();
				group.setFunc(node.getFunc());
				group.setNodeLevel(1);
				StringBuilder b = new StringBuilder();
				b.append(node.getIp()).append(':').append(node.getPort());
				group.setName(b.toString());
				if (registerChild) {
					NodeGroup g = tableManager.getCurrentGroup();
					group.setPre(g);
					group.setNodeLevel(g.getNodeLevel() + 1);
					if (logger.isInfoEnabled()) {
						logger.info("addNode currentGroup="
								+ JSON.toJSONString(g).toString());
					}
					currentNode = tableManager.getCurrentNode();
				} else {
					currentNode = node;
				}
				if (logger.isInfoEnabled()) {
					logger.info("addNode currentNode="
							+ JSON.toJSONString(currentNode).toString());
				}
				List<Node> nodeList = new ArrayList<>();
				nodeList.add(node);
				group.setNodes(nodeList);
				int groupId = groupService.addGroup(group);
				node.setGroupId(groupId);
				int nodeId = nodeService.addNode(node);
				if (node.getFunc().equals(NodeFunc.collector)) {
					nodeDAO.insertNodeDomain(nodeId);
				}
			}
			t = nodeService.getNodeTable();
			tableManager.saveNodeTable(currentNode, t);
		}
		if (logger.isInfoEnabled()) {
			logger.info("addNode end");
		}
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeManagerMbean#loadGroupNodeId(java.lang.String
	 * , int)
	 */
	public int loadGroupNodeId(String nodeIp, int nodePort)
			throws NodeException {
		NodeTable table = nodeService.getNodeTable();
		Node node = table.selectNode(nodeIp, nodePort);
		return node == null ? -1 : node.getGroupId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeManagerMbean#getGroups(com.mainsteam.stm
	 * .node.NodeFunc)
	 */
	public List<NodeGroup> getGroups(NodeFunc func) throws NodeException {
		NodeGroup currentNodeGroup = tableManager.getCurrentGroup();
		List<NodeGroup> funcGroups = new ArrayList<>();
		List<NodeGroup> nextGroups = currentNodeGroup.getNextGroups();
		if (nextGroups != null && nextGroups.size() > 0) {
			for (NodeGroup nodeGroup : nextGroups) {
				/**
				 * clone 取消对上下group的引用，减少序列化需要的数据量
				 */
				NodeGroup cloneGroup = new NodeGroup();
				cloneGroup.setFunc(func);
				cloneGroup.setId(nodeGroup.getId());
				cloneGroup.setName(nodeGroup.getName());
				cloneGroup.setNodeLevel(nodeGroup.getNodeLevel());
				cloneGroup.setNodes(nodeGroup.getNodes());
				if (nodeGroup.getFunc().equals(func)) {
					funcGroups.add(cloneGroup);
				}
			}
		}
		return funcGroups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeManagerMbean#getNodeTable()
	 */
	public NodeTable getNodeTable() throws NodeException {
		return nodeService.getNodeTable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeManagerMbean#removeNodeById(int)
	 */
	public void removeNodeById(int nodeId) throws NodeException {
		nodeService.removeNodeById(nodeId);
	}

	public NodeTableSummary selectNodeTableSummary() {
		NodeTableSummary summary = new NodeTableSummary();
		long updateTime = nodeDAO.selectMaxUpdateTime();
		long groupUpTime = groupDAO.selectMaxUpdateTime();
		summary.setUpdateTime(new Date(groupUpTime > updateTime ? groupUpTime
				: updateTime));
		summary.setNodesize(nodeDAO.selectNodeCount());
		summary.setNodeGroupSize(groupDAO.selectGroupCount());
		return summary;
	}

	public Date getTableLatestUpdateTime() {
		if (logger.isTraceEnabled()) {
			logger.trace("getTableLatestUpdateTime start");
		}
		Date updateTime = null;
		long time = nodeDAO.selectMaxUpdateTime();
		long groupTime = groupDAO.selectMaxUpdateTime();
		updateTime = new Date(time > groupTime ? time : groupTime);
		if (logger.isTraceEnabled()) {
			logger.trace("getTableLatestUpdateTime end");
		}
		return updateTime;
	}

	public int getNodeSize() {
		return nodeDAO.selectNodeCount();
	}

	public int getNodeGroupSize() {
		return groupDAO.selectGroupCount();
	}

	public void startNode(int nodeId, long startTime) {
		if (logger.isTraceEnabled()) {
			logger.trace("startNode start nodeId=" + nodeId + " startTime="
					+ startTime);
		}
		nodeService.updateNodeStartupTime(nodeId, new Date(startTime));
		if (logger.isTraceEnabled()) {
			logger.trace("startNode end");
		}
	}

	public void heartbeat(NodeHeartbeat heartbeat) {
		if (logger.isTraceEnabled()) {
			logger.trace("heartbeat start nodeId=" + heartbeat.getNodeId());
		}
		nodeHeartbeatService.addNodeHeartbeat(heartbeat);
		if (logger.isTraceEnabled()) {
			logger.trace("heartbeat end");
		}
	}
}
