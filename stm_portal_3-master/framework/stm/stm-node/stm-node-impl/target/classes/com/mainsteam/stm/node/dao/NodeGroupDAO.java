/**
 * 
 */
package com.mainsteam.stm.node.dao;

import java.util.List;

import com.mainsteam.stm.node.dao.pojo.NodeGroupDO;

/**
 * @author ziw
 * 
 */
public interface NodeGroupDAO {
	public int insert(NodeGroupDO groupDO);

	public int updateExampleById(NodeGroupDO groupDO);

	public List<NodeGroupDO> selectByExample(NodeGroupDO groupDO);

	public NodeGroupDO selectById(int groupId);

	public int removeById(int groupId);

	public int selectGroupCount();

	public long selectMaxUpdateTime();
}
