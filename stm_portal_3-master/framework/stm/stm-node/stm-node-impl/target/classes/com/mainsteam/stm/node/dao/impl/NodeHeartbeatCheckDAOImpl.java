/**
 * 
 */
package com.mainsteam.stm.node.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.node.dao.NodeHeartbeatCheckDAO;
import com.mainsteam.stm.node.dao.pojo.NodeHeartbeatCheckDO;

/**
 * @author ziw
 * 
 */
public class NodeHeartbeatCheckDAOImpl implements NodeHeartbeatCheckDAO {

	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	/**
	 * 
	 */
	public NodeHeartbeatCheckDAOImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeHeartbeatCheckDAO#selectNodeHeartbeatCheckDOs
	 * ()
	 */
	@Override
	public List<NodeHeartbeatCheckDO> selectNodeHeartbeatCheckDOs() {
		return session
				.selectList("com.mainsteam.stm.node.dao.pojo.NodeHeartbeatCheckDAO.select");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeHeartbeatCheckDAO#updateNodeHeartbeatCheckDO
	 * (com.mainsteam.stm.node.dao.pojo.NodeHeartbeatCheckDO)
	 */
	@Override
	public int updateNodeHeartbeatCheckDO(NodeHeartbeatCheckDO checkDO) {
		return session
				.insert("com.mainsteam.stm.node.dao.pojo.NodeHeartbeatCheckDAO.update",
						checkDO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeHeartbeatCheckDAO#insertNodeHeartbeatCheckDO
	 * (com.mainsteam.stm.node.dao.pojo.NodeHeartbeatCheckDO)
	 */
	@Override
	public int insertNodeHeartbeatCheckDO(NodeHeartbeatCheckDO checkDO) {
		return session
				.insert("com.mainsteam.stm.node.dao.pojo.NodeHeartbeatCheckDAO.insert",
						checkDO);
	}

}
