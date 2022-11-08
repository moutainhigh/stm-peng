package com.mainsteam.stm.auto.rediscover.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.auto.rediscover.dao.ProfileAutoRefreshDao;
import com.mainsteam.stm.profilelib.obj.ProfileAutoRefresh;

public class ProfileAutoRefreshDaoImpl implements ProfileAutoRefreshDao {

	private SqlSession session;

	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	public void setMyBatisSqlSessionFactory(SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	
	@Override
	public void addAutoRefreshProfile(ProfileAutoRefresh autoRefresh) {
		session.insert("addAutoRefreshProfile", autoRefresh);
	}

	@Override
	public void addAutoRefreshProfile(List<ProfileAutoRefresh> autoRefresh) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileAutoRefresh profileAutoRefresh : autoRefresh) {
				batchSession.insert("addAutoRefreshProfile",profileAutoRefresh);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeAutoRefreshProfile(long instanceId) {
		session.delete("removeAutoRefreshProfile",instanceId);

	}

	@Override
	public void removeAutoRefreshProfile(List<Long> instanceIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH, false)){
			for (Long instanceId : instanceIds) {
				batchSession.delete("removeAutoRefreshProfile",instanceId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateAutoRefreshProfile(ProfileAutoRefresh autoRefresh) {
		session.update("updateAutoRefreshProfile",autoRefresh);
	}

	@Override
	public void updateAutoRefreshProfile(List<ProfileAutoRefresh> autoRefreshs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH, false)){
			for (ProfileAutoRefresh refresh : autoRefreshs) {
				batchSession.update("updateAutoRefreshProfile",refresh);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<ProfileAutoRefresh> getAllAutoRefreshProfile() {
		return session.selectList("getAllAutoRefreshProfile");
	}
	@Override
	public ProfileAutoRefresh getAutoRefreshProfileByInstance(long instanceId) {
		return session.selectOne("getAutoRefreshProfileByInstance",instanceId);
	}
	@Override
	public List<ProfileAutoRefresh> getAutoRefreshProfileById(long id) {
		return session.selectList("getAutoRefreshProfileById", id);
	}

}
