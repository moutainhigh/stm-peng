/**
 * 
 */
package com.mainsteam.stm.node.dao;

import java.util.List;

import com.mainsteam.stm.node.dao.pojo.NodeHeartbeatCheckDO;

/**
 * @author ziw
 *
 */
public interface NodeHeartbeatCheckDAO {
	public List<NodeHeartbeatCheckDO> selectNodeHeartbeatCheckDOs();
	public int updateNodeHeartbeatCheckDO(NodeHeartbeatCheckDO checkDO);
	public int insertNodeHeartbeatCheckDO(NodeHeartbeatCheckDO checkDO);
}
