/**
 * 
 */
package com.mainsteam.stm.node;

import java.util.List;

/**
 * 维护节点组
 * 
 * @author ziw
 * 
 */
public interface NodeGroupService {
	/**
	 * 新增nodegroup
	 * 
	 * @param group
	 */
	public int addGroup(NodeGroup group);

	/**
	 * 删除nodegroup
	 * 
	 * @param groupId
	 */
	public void removeGroup(int groupId);

	/**
	 * 更新group之间的关系
	 * 
	 * @param groupId
	 *            分组id
	 * 
	 * @param parentGroupId
	 *            上级分组id
	 */
	public void updateNodeGroupRelation(int groupId, int parentGroupId);

	/**
	 * 查询分组详细信息
	 * 
	 * @param groupId
	 *            分组id
	 * @return NodeGroup
	 */
	public NodeGroup getNodeGroupById(int groupId);

	/**
	 * 获取所有的节点组
	 * 
	 * @return List<NodeGroup>
	 */
	public List<NodeGroup> getNodeGroups();
}
