/**
 * 
 */
package com.mainsteam.stm.node.dao;

import java.util.List;

import com.mainsteam.stm.node.dao.pojo.NodeHeartbeatDO;

/**
 * @author ziw
 *
 */
public interface NodeHeartbeatDAO {
	public int insert(NodeHeartbeatDO heartbeatDO);
	public int delete(long endTime);
	public int delete(int nodeId,long endTime);
	public List<NodeHeartbeatDO> selectByTime(long start,long end);
	public List<NodeHeartbeatDO> selectLatest();
}
