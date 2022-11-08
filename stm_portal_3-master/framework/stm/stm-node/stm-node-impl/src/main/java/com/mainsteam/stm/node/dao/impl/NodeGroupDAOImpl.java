/**
 * 
 */
package com.mainsteam.stm.node.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.node.dao.NodeGroupDAO;
import com.mainsteam.stm.node.dao.pojo.NodeGroupDO;

/**
 * @author ziw
 * 
 */
public class NodeGroupDAOImpl implements NodeGroupDAO {

	private SqlSession session;

	/**
	 * 
	 */
	public NodeGroupDAOImpl() {
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeGroupDAO#insert(com.mainsteam.stm.node
	 * .dao.pojo.NodeGroupDO)
	 */
	@Override
	public int insert(NodeGroupDO groupDO) {
		return this.session
				.insert("com.mainsteam.stm.node.dao.pojo.NodeGroupDAO.insert",
						groupDO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeGroupDAO#updateExampleById(com.mainsteam
	 * .oc.node.dao.pojo.NodeGroupDO)
	 */
	@Override
	public int updateExampleById(NodeGroupDO groupDO) {
		return this.session
				.insert("com.mainsteam.stm.node.dao.pojo.NodeGroupDAO.update",
						groupDO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.dao.NodeGroupDAO#selectByExample(com.mainsteam
	 * .oc.node.dao.pojo.NodeGroupDO)
	 */
	@Override
	public List<NodeGroupDO> selectByExample(NodeGroupDO groupDO) {
		return this.session
				.selectList(
						"com.mainsteam.stm.node.dao.pojo.NodeGroupDAO.selectByExample",
						groupDO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.dao.NodeGroupDAO#removeById(int)
	 */
	@Override
	public int removeById(int groupId) {
		return this.session
				.insert("com.mainsteam.stm.node.dao.pojo.NodeGroupDAO.delete",
						groupId);
	}

	@Override
	public NodeGroupDO selectById(int groupId) {
		return this.session.selectOne(
				"com.mainsteam.stm.node.dao.pojo.NodeGroupDAO.selectById",
				groupId);
	}

	@Override
	public int selectGroupCount() {
		return this.session
				.selectOne("com.mainsteam.stm.node.dao.pojo.NodeGroupDAO.selectGroupCount");
	}

	@Override
	public long selectMaxUpdateTime() {
		/**
		 * 单元测试，数据库无值时，需要返回0
		 */
		Long time = this.session
				.selectOne("com.mainsteam.stm.node.dao.pojo.NodeGroupDAO.selectMaxUpdateTime");
		return time == null ? 0 : time.longValue();
	}
}
