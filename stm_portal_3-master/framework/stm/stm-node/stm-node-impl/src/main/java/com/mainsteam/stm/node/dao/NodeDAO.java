/**
 * 
 */
package com.mainsteam.stm.node.dao;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.node.dao.pojo.NodeDO;

/**
 * @author ziw
 * 
 */
public interface NodeDAO {
	
	public int insert(NodeDO node);

	public int updateExampleById(NodeDO node);

	public int deleteById(int id);

	public int deleteChildNodesByGroupId(int groupId);

	public List<NodeDO> getByExample(NodeDO node);

	public NodeDO getById(int id);

	public List<String> selectFuncByGroup(int groupId);

	public int selectNodeCount();

	public long selectMaxUpdateTime();

	public int updateNodeStartupTime(int nodeId, Date startupTime);
	
	public int insertNodeDomain(long nodeId);
}
