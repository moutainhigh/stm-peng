/**
 * 
 */
package com.mainsteam.stm.node;

import java.util.List;

import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;

/**
 * 
 * 节点管理
 * 
 * @author ziw
 * 
 */
public interface NodeManager {

	/**
	 * 注册一个下级节点到本节点分组下
	 * 
	 * @param node
	 *            下级节点
	 * @return 计算出的下级节点表
	 */
	public NodeTable registerChildNode(Node node) throws NodeException;

	/**
	 * 注册一个节点
	 * 
	 * @param node
	 *            指定节点
	 * @return NodeTable
	 */
	public NodeTable registerNode(Node node) throws NodeException;

	/**
	 * 加载指定节点所在的分组的节点表。
	 * 
	 * @param nodeIp
	 *            指定节点ip
	 * @param nodePort
	 *            指定节点端口
	 * @return 指定节点所在分组
	 * @throws NodeException
	 */
	public int loadGroupNodeId(String nodeIp, int nodePort)
			throws NodeException;

	/**
	 * 获取当前组件下，指定节点类型所有的节点分组对象
	 * 
	 * @param func
	 *            指定节点类型
	 * @return List<NodeGroup>
	 * @throws NodeException
	 */
	public List<NodeGroup> getGroups(NodeFunc func) throws NodeException;

	/**
	 * 获取完整的节点表
	 * 
	 * @return NodeTable
	 */
	public NodeTable getNodeTable() throws NodeException;

	/**
	 * 
	 * 从本地节点表中删除指定的节点
	 * 
	 * @param nodeId
	 *            节点id
	 */
	public void removeNodeById(int nodeId) throws NodeException;

	/**
	 * 查询节点表的概要信息
	 * 
	 * @return NodeTableSummary
	 */
	public NodeTableSummary selectNodeTableSummary();

	/**
	 * node启动
	 * 
	 * @param nodeId
	 * @param startTime
	 */
	public void startNode(int nodeId, long startTime);

	/**
	 * 更新node的心跳
	 */
	public void heartbeat(NodeHeartbeat heartbeat);
}
