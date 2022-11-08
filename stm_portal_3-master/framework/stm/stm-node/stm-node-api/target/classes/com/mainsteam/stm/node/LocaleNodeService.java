/**
 * 
 */
package com.mainsteam.stm.node;

import com.mainsteam.stm.node.exception.NodeException;

/**
 * 
 * 本地节点管理
 * 
 * @author ziw
 * 
 */
public interface LocaleNodeService {
	/**
	 * 获取当前节点信息
	 * 
	 * @return Node 当前节点
	 */
	public Node getCurrentNode() throws NodeException;

	/**
	 * 获取当前节点组信息
	 * 
	 * @return NodeGroup 当前节点组
	 */
	public NodeGroup getCurrentNodeGroup() throws NodeException;
//	/**
//	 * 更新node是否可用的状态
//	 * 
//	 * @param nodeId 节点id
//	 * @param isAlive 是否活动
//	 */
//	public void updateNodeState(int nodeId, boolean isAlive);

	/**
	 * 获取本地节点表
	 * 
	 * @return NodeTable
	 */
	public NodeTable getLocalNodeTable() throws NodeException;

	// /**
	// * 刷新本地节点表。从数据库中拿数据，比较修改的步伐，并且更新到本地。
	// *
	// * @throws NodeException
	// */
	// public void freshLocalNodeTable() throws NodeException;

	// /**
	// * 注册本地节点到上级节点，并加载和本地相关的节点表()
	// *
	// * @param parentNodeIp
	// * 上级节点ip
	// * @param parentNodePort
	// * 上级节点端口
	// * @return NodeTable 节点表
	// * @throws NodeException
	// */
	// public NodeTable registerParentNodeTable(String parentNodeIp,
	// int parentNodePort) throws NodeException;
}
