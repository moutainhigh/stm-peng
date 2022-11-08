/**
 * 
 */
package com.mainsteam.stm.node.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.node.dao.NodeDAO;
import com.mainsteam.stm.node.dao.pojo.NodeDO;

/**
 * @author ziw
 * 
 */
public class NodeDAOImpl implements NodeDAO {

	private SqlSession session;

	/**
	 * 
	 */
	public NodeDAOImpl() {
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeDAO#insert(com.mainsteam.stm.node.dao
	 * .pojo.NodeDO)
	 */
	@Override
	public int insert(NodeDO node) {
		return this.session.insert(
				"com.mainsteam.stm.node.dao.pojo.NodeDAO.insert", node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeDAO#updateExampleById(com.mainsteam
	 * .oc.node.dao.pojo.NodeDO)
	 */
	@Override
	public int updateExampleById(NodeDO node) {
		return this.session.update(
				"com.mainsteam.stm.node.dao.pojo.NodeDAO.update", node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.dao.NodeDAO#deleteById(int)
	 */
	@Override
	public int deleteById(int id) {
		return this.session.delete(
				"com.mainsteam.stm.node.dao.pojo.NodeDAO.delete", id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeDAO#getByExample(com.mainsteam.stm.node
	 * .dao.pojo.NodeDO)
	 */
	@Override
	public List<NodeDO> getByExample(NodeDO node) {
		return this.session.selectList(
				"com.mainsteam.stm.node.dao.pojo.NodeDAO.selectByExample",
				node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.dao.NodeDAO#getById(int)
	 */
	@Override
	public NodeDO getById(int id) {
		return this.session.selectOne(
				"com.mainsteam.stm.node.dao.pojo.NodeDAO.selectById", id);
	}

	@Override
	public List<String> selectFuncByGroup(int groupId) {
		return this.session.selectList(
				"com.mainsteam.stm.node.dao.pojo.NodeDAO.selectFuncByGroup",
				groupId);
	}

	@Override
	public int selectNodeCount() {
		return this.session
				.selectOne("com.mainsteam.stm.node.dao.pojo.NodeDAO.selectNodeCount");
	}

	@Override
	public long selectMaxUpdateTime() {
		Long updateTime = this.session
				.selectOne("com.mainsteam.stm.node.dao.pojo.NodeDAO.selectMaxUpdateTime");
		return updateTime == null ? 0 : updateTime.longValue();
	}

	@Override
	public int updateNodeStartupTime(int nodeId, Date startupTime) {
		NodeDO nodeDO = new NodeDO();
		nodeDO.setId(nodeId);
		nodeDO.setStartupTime(startupTime.getTime());
		return this.session.update(
				"com.mainsteam.stm.node.dao.pojo.NodeDAO.updateStartupTime",
				nodeDO);
	}

	@Override
	public int deleteChildNodesByGroupId(int groupId) {
		return this.session.delete(
				"com.mainsteam.stm.node.dao.pojo.NodeDAO.deleteChildNodes",
				groupId);
	}

	@Override
	public int insertNodeDomain(long nodeId) {
		return this.session
				.insert("com.mainsteam.stm.node.dao.pojo.NodeDAO.insertDomain",
						nodeId);
	}
}
