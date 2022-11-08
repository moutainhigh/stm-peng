/**
 * 
 */
package com.mainsteam.stm.node.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.node.InnerNodeImpl;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeGroupService;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.dao.NodeDAO;
import com.mainsteam.stm.node.dao.NodeGroupDAO;
import com.mainsteam.stm.node.dao.pojo.NodeDO;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * @author ziw
 * 
 */
public class NodeServiceImpl implements NodeService {

	private static final Log logger = LogFactory.getLog(NodeService.class);

	private NodeDAO nodeDAO;

	private NodeGroupDAO groupDAO;

	private NodeGroupService groupService;

	private ISequence sequence;

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public final void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @param groupDAO
	 *            the groupDAO to set
	 */
	public final void setGroupDAO(NodeGroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}

	public void setGroupService(NodeGroupService groupService) {
		this.groupService = groupService;
	}

	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}

	/**
	 * 
	 */
	public NodeServiceImpl() {
	}

	public NodeTable getNodeTable() throws NodeException {
		if (logger.isTraceEnabled()) {
			logger.trace("getNodeTable start");
		}
		List<NodeDO> nodeDOs = nodeDAO.getByExample(null);
		List<NodeGroup> groups = groupService.getNodeGroups();
		long updateTime = nodeDAO.selectMaxUpdateTime();
		long groupUpTime = groupDAO.selectMaxUpdateTime();
		List<NodeGroup> nodeGroups = convertGroups(nodeDOs, groups);
		NodeTable table = new NodeTable();
		table.setUpdateTime(new Date(groupUpTime > updateTime ? groupUpTime
				: updateTime));
		table.setGroups(nodeGroups);
		if (logger.isTraceEnabled()) {
			logger.trace("getNodeTable end");
		}
		return table;
	}

	@Override
	public Node getNodeById(int nodeId) throws NodeException {
		if (logger.isTraceEnabled()) {
			logger.trace("getNodeById start nodeId=" + nodeId);
		}
		Node node = toNode(nodeDAO.getById(nodeId));
		if (logger.isTraceEnabled()) {
			logger.trace("getNodeById end node=" + node);
		}
		return node;
	}

	@Override
	public boolean removeNodeById(int nodeId) throws NodeException {
		if (logger.isInfoEnabled()) {
			logger.info("removeNodeById start nodeId=" + nodeId);
		}
		NodeDO nodeDO = nodeDAO.getById(nodeId);
		if (nodeDO != null) {
			nodeDAO.deleteById(nodeId);
			if (nodeDO.getGroupId() != null) {
				if (logger.isInfoEnabled()) {
					logger.info("deleteChildNodesByGroupId groupId="
							+ nodeDO.getGroupId());
				}
				nodeDAO.deleteChildNodesByGroupId(nodeDO.getGroupId()
						.intValue());
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("removeNodeById end");
		}
		return true;
	}

	@Override
	public boolean updateNode(Node node) throws NodeException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateNode start node=" + node);
		}
		nodeDAO.updateExampleById(toDo(node));
		if (logger.isTraceEnabled()) {
			logger.trace("updateNode end");
		}
		return false;
	}

	@Override
	public int addNode(Node node) throws NodeException {
		if (logger.isTraceEnabled()) {
			logger.trace("addNode start " + node);
		}

		/**
		 * 验证ip地址和端口冲突
		 */
		NodeDO nodeQuery = new NodeDO();
		nodeQuery.setIp(node.getIp());
		nodeQuery.setPort(node.getPort());
		List<NodeDO> ipPorts = nodeDAO.getByExample(nodeQuery);
		if (ipPorts != null && ipPorts.size() > 0) {
			throw new NodeException(NodeException.ERROR_CODE_IP_PORT_CONFLICT,
					"ip and port is conflict.");
		}
		node.setId((int) sequence.next());
		if (logger.isInfoEnabled()) {
			logger.info("addNode node=" + JSON.toJSONString(node).toString());
		}
		/**
		 * 验证组内节点类型是否相同
		 */
		List<String> nodeFunc = nodeDAO.selectFuncByGroup(node.getGroupId());
		if (nodeFunc != null && nodeFunc.size() > 1) {
			throw new NodeException(
					NodeException.ERROR_CODE_NODEFUN_NOT_MATCH_GROUP,
					"nodegroup not match node func " + node + " nodeId="
							+ node.getGroupId() + " nodeFuncList=" + nodeFunc);
		}
		if (nodeFunc != null && nodeFunc.size() == 1
				&& !nodeFunc.get(0).equals(node.getFunc().name())) {
			throw new NodeException(
					NodeException.ERROR_CODE_NODEGROUP_NOT_EXIST, "node func "
							+ node.getFunc() + " is not match group func="
							+ nodeFunc);
		}
		nodeDAO.insert(toDo(node));
		if (logger.isTraceEnabled()) {
			logger.trace("addNode end");
		}
		return node.getId();
	}

	private NodeDO toDo(Node n) {
		NodeDO nodeDO = new NodeDO();
		nodeDO.setFunc(n.getFunc().name());
		nodeDO.setGroupId(n.getGroupId());
		nodeDO.setId(n.getId());
		nodeDO.setIp(n.getIp());
		nodeDO.setName(n.getName());
		nodeDO.setPort(n.getPort());
		nodeDO.setPriority(n.getPriority());
		nodeDO.setAlive(n.isAlive() ? 1 : 0);
		nodeDO.setUpdateTime(new Date().getTime());
		nodeDO.setInstallPath(n.getInstallPath());
		nodeDO.setDescription(n.getDescription());
		return nodeDO;
	}

	private InnerNodeImpl toNode(NodeDO nodeDO) {
		InnerNodeImpl n = new InnerNodeImpl();
		n.setFunc(NodeFunc.valueOf(nodeDO.getFunc()));
		n.setGroupId(nodeDO.getGroupId());
		n.setId(nodeDO.getId());
		n.setIp(nodeDO.getIp());
		n.setName(nodeDO.getName());
		n.setPort(nodeDO.getPort());
		n.setPriority(nodeDO.getPriority());
		n.setAlive(nodeDO.getAlive().intValue() == 1);
		n.setInstallPath(nodeDO.getInstallPath());
		n.setStartupTime(new Date(nodeDO.getStartupTime()));
		n.setDescription(nodeDO.getDescription());
		return n;
	}

	private List<NodeGroup> convertGroups(List<NodeDO> nodes,
			List<NodeGroup> groups) {
		if (groups == null) {
			return null;
		}
		Map<Integer, NodeGroup> groupsMap = new HashMap<>();
		for (NodeGroup nodeGroup : groups) {
			groupsMap.put(nodeGroup.getId(), nodeGroup);
		}
		for (NodeDO nodeDO : nodes) {
			InnerNodeImpl node = toNode(nodeDO);
			if (groupsMap.containsKey(nodeDO.getGroupId())) {
				NodeGroup group = groupsMap.get(nodeDO.getGroupId());
				if (group.getNodes() == null) {
					group.setNodes(new ArrayList<Node>());
				}
				node.setNodeGroup(group);
				group.getNodes().add(node);
			}
		}
		return groups;
	}

	@Override
	public void updateNodeStartupTime(int nodeId, Date updateTime) {
		if (logger.isTraceEnabled()) {
			logger.trace("updateNodeStartupTime start");
		}
		nodeDAO.updateNodeStartupTime(nodeId, updateTime);
		if (logger.isTraceEnabled()) {
			logger.trace("updateNodeStartupTime end");
		}
	}

	@Override
	public void updateNodesState(List<Node> nodes) {
		if (logger.isTraceEnabled()) {
			logger.trace("updateNodesState start nodes.size="
					+ (nodes == null ? null : nodes.size()));
		}
		if (nodes != null && nodes.size() > 0) {
			long updateTime = System.currentTimeMillis();
			for (Node n : nodes) {
				NodeDO nd = new NodeDO();
				nd.setId(n.getId());
				nd.setAlive(n.isAlive() ? 1 : 0);
				nd.setUpdateTime(updateTime);
				nodeDAO.updateExampleById(nd);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateNodesState end");
		}
	}
}
