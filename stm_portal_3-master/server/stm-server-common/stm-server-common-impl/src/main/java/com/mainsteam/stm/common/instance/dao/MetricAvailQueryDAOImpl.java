/**
 * 
 */
package com.mainsteam.stm.common.instance.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.common.instance.dao.obj.MetricAvailDataDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * @author ziw
 * 
 */
public class MetricAvailQueryDAOImpl implements MetricAvailQueryDAO {
	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	/**
	 * 
	 */
	public MetricAvailQueryDAOImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.common.instance.dao.MetricAvailQueryDAO#getAvailDataDOs
	 * (com.mainsteam.stm.platform.mybatis.plugin.pagination.Page)
	 */
	@Override
	public List<MetricAvailDataDO> getAvailDataDOs(
			Page<MetricAvailDataDO, String> page) {
		return session
				.selectList(
						"com.mainsteam.stm.common.instance.dao.MetricAvailQueryDAO.select",
						page);
	}
}
