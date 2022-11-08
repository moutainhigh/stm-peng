/**
 * 
 */
package com.mainsteam.stm.metric.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.metric.dao.CustomMetricChangeResultDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeResultDO;

/**
 * @author ziw
 *
 */
public class CustomMetricChangeResultDAOImpl implements
		CustomMetricChangeResultDAO {

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
	public CustomMetricChangeResultDAOImpl() {
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.metric.dao.CustomMetricChangeApplyDAO#select(long[])
	 */
	@Override
	public List<CustomMetricChangeResultDO> select(long[] changeIds) {
		if(changeIds == null || changeIds.length<=0){
			return new ArrayList<>(0);
		}
		return session.selectList("getMetricChangeApplyDOs", changeIds);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.metric.dao.CustomMetricChangeApplyDAO#insert(java.util.List)
	 */
	@Override
	public void insert(List<CustomMetricChangeResultDO> applyDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricChangeResultDO customMetricChangeApplyDO : applyDOs) {
				batchSession.insert("insertMetricChangeApply", customMetricChangeApplyDO);
			}
			batchSession.commit();
		}
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.metric.dao.CustomMetricChangeApplyDAO#remove(java.util.Date)
	 */
	@Override
	public void remove(Date expireDate) {
		session.delete("removeMetricChangeApplyByDate", expireDate);
	} 

	@Override
	public void updateCustomMetricChangeDOToApply(
			List<CustomMetricChangeResultDO> applyDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricChangeResultDO customMetricChangeApplyDO : applyDOs) {
				batchSession.insert("updateMetricChangeApplyState", customMetricChangeApplyDO);
			}
			batchSession.commit();
		}
	}
}
