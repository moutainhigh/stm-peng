/**
 * 
 */
package com.mainsteam.stm.metric.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.metric.dao.CustomMetricChangeDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * @author ziw
 * 
 */
public class CustomMetricChangeDAOImpl implements CustomMetricChangeDAO {

	private SqlSession session;

	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}

	/**
	 * 
	 */
	public CustomMetricChangeDAOImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.metric.dao.CustomMetricChangeDAO#addCustomMetricChangeDOs
	 * (java.util.List)
	 */
	@Override
	public void addCustomMetricChangeDOs(List<CustomMetricChangeDO> changeDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricChangeDO customMetricChangeDO : changeDOs) {
				batchSession.insert("insertMetricChange", customMetricChangeDO);
			}
			batchSession.commit();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.metric.dao.CustomMetricChangeDAO#
	 * selectChangeDOsWithNotApply(int)
	 */
	@Override
	public List<CustomMetricChangeDO> selectChangeDOsWithNotApply(int limit) {
		Page<CustomMetricChangeDO, Object> page = new Page<>(0, limit);
		return session.selectList("getMetricChangeDOs", page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.metric.dao.CustomMetricChangeDAO#
	 * updateCustomMetricChangeDOToApply(long[])
	 */
	@Override
	public void updateCustomMetricChangeDOToApply(long[] change_id) {
		CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
		changeDO.setOccur_time(new Date());
		for (long id : change_id) {
			changeDO.setChange_id(id);
			session.update("updateMetricChangeState", changeDO);
		}
	}

	@Override
	public void remove(Date expireDate) {
		session.delete("removeMetricChangeByDate", expireDate);
	}

}
