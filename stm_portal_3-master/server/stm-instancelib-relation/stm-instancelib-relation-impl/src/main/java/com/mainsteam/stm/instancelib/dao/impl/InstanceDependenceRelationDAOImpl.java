package com.mainsteam.stm.instancelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.instancelib.dao.InstanceDependenceRelationDAO;
import com.mainsteam.stm.instancelib.dao.pojo.InstanceDependencePO;

public class InstanceDependenceRelationDAOImpl implements
		InstanceDependenceRelationDAO {

	private SqlSession session;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	@Override
	public void insertDependence(InstanceDependencePO instanceDependencePO) {
		session.insert("insertDependence",instanceDependencePO);
	}

	@Override
	public void insertDependences(
			List<InstanceDependencePO> instanceDependencePOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (InstanceDependencePO instanceDependencePO : instanceDependencePOs) {
				batchSession.insert("insertDependence",instanceDependencePO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeDependence(long relationId) {
		session.delete("removeDependence",relationId);
	}

	@Override
	public void removeDependences(List<Long> relationIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long relationId : relationIds) {
				batchSession.delete("removeDependence",relationId);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<InstanceDependencePO> getAllDependence() throws Exception {
		return session.selectList("getAllDependence");
	}

	@Override
	public List<InstanceDependencePO> getPreviousDependence(
			long targertInstanceId) throws Exception {
		return session.selectList("getPreviousDependence",targertInstanceId);
	}

	@Override
	public List<InstanceDependencePO> getNextDependence(long sourceResource)
			throws Exception {
		return session.selectList("getNextDependence",sourceResource);
	}

	@Override
	public void removeTopoLinkDependences() throws Exception {
		session.delete("removeTopoLinkDependences");
	}

	@Override
	public void removeBusinessRelationByCompositeId(long compositeId) throws Exception {
		session.delete("removeBusinessRelationByCompositeId",compositeId);
	}

	@Override
	public List<InstanceDependencePO> getPreviousChildDependence(
			long targertChildInstanceId) throws Exception {
		return session.selectList("getPreviousChildDependence",targertChildInstanceId);
	}

	@Override
	public List<InstanceDependencePO> getNextChildDependence(long sourceResource)
			throws Exception {
		return session.selectList("getNextChildDependence",sourceResource);
	}

}
