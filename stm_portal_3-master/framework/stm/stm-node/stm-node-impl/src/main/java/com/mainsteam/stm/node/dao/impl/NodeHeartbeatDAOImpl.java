/**
 * 
 */
package com.mainsteam.stm.node.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.node.dao.NodeHeartbeatDAO;
import com.mainsteam.stm.node.dao.pojo.NodeHeartbeatDO;

/**
 * @author ziw
 * 
 */
public class NodeHeartbeatDAOImpl implements NodeHeartbeatDAO {

	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	/**
	 * 
	 */
	public NodeHeartbeatDAOImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.dao.NodeHeartbeatDAO#insert(java.util.List)
	 */
	@Override
	public int insert(NodeHeartbeatDO heartbeatDO) {
		return this.session.insert(
				"com.mainsteam.stm.node.dao.pojo.NodeHeartbeatDAO.insert",
				heartbeatDO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.dao.NodeHeartbeatDAO#delete(long)
	 */
	@Override
	public int delete(long endTime) {
		return this.session.insert(
				"com.mainsteam.stm.node.dao.pojo.NodeHeartbeatDAO.delete",
				endTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.dao.NodeHeartbeatDAO#selectByTime(long,
	 * long)
	 */
	@Override
	public List<NodeHeartbeatDO> selectByTime(long start, long end) {
		NodeHeartbeatDO query = new NodeHeartbeatDO();
		query.setOccurtime(start);
		query.setExpireOccurtime(end);
		return this.session
				.selectList(
						"com.mainsteam.stm.node.dao.pojo.NodeHeartbeatDAO.selectByDate",
						query);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.dao.NodeHeartbeatDAO#selectLatest()
	 */
	@Override
	public List<NodeHeartbeatDO> selectLatest() {
		return this.session
				.selectList("com.mainsteam.stm.node.dao.pojo.NodeHeartbeatDAO.selectLatest");
	}

	@Override
	public int delete(int nodeId, long endTime) {
		NodeHeartbeatDO condition = new NodeHeartbeatDO();
		condition.setNodeId(nodeId);
		condition.setOccurtime(endTime);
		return this.session
				.insert("com.mainsteam.stm.node.dao.pojo.NodeHeartbeatDAO.deleteByExample",
						condition);
	}
}
