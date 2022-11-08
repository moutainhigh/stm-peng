/**
 * 
 */
package com.mainsteam.stm.node;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.node.exception.NodeException;

/**
 * 节点管理服务
 * 
 * @author ziw
 * 
 */
public interface NodeService {

	/**
	 * 查询node节点
	 * 
	 * @return
	 */
	public Node getNodeById(int nodeId) throws NodeException;

	/**
	 * 删除指定的node节点
	 * 
	 * @param nodeId
	 * @return
	 */
	public boolean removeNodeById(int nodeId) throws NodeException;

	/**
	 * 更新节点信息
	 * 
	 * @param node
	 * @return
	 */
	public boolean updateNode(Node node) throws NodeException;
	
	/**
	 * 更新节点的状态
	 * 
	 * @param nodes
	 */
	public void updateNodesState(List<Node> nodes);

	/**
	 * 添加节点
	 * 
	 * @param node
	 * @return
	 */
	public int addNode(Node node) throws NodeException;

	/**
	 * 更新节点的启动时间
	 * 
	 * @param nodeId
	 *            节点id
	 * @param updateTime
	 *            节点启动时间
	 */
	public void updateNodeStartupTime(int nodeId, Date updateTime);

	/**
	 * 获取完整的节点表
	 * 
	 * @return NodeTable 节点表
	 */
	public NodeTable getNodeTable() throws NodeException;
}
