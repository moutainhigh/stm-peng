package com.mainsteam.stm.auto.rediscover.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.auto.rediscover.dao.ProfileAutoRediscoverDao;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscoverInstance;

public class ProfileAutoRediscoverDaoImpl implements ProfileAutoRediscoverDao {

	private SqlSession session;

	private SqlSessionFactory myBatisSqlSessionFactory;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	public void setMyBatisSqlSessionFactory(SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}

	@Override
	public void addProfileAutoRediscover(ProfilelibAutoRediscover autoRediscover) {
		session.insert("addProfileAutoRediscover", autoRediscover);
	}

	@Override
	public void addProfileAutoRediscoverInstance(List<ProfilelibAutoRediscoverInstance> profilelibAutoRediscoverInstances) {
		try (SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
			for (ProfilelibAutoRediscoverInstance profilelibAutoRediscoverInstance : profilelibAutoRediscoverInstances) {
				batchSession.insert("addProfileAutoRediscoverInstance", profilelibAutoRediscoverInstance);
			}
			batchSession.commit();
		}

	}

	@Override
	public void deleteProfileAutoRediscover(long profileId) {
		session.delete("deleteProfileAutoRediscover", profileId);
	}

	@Override
	public void deleteProfileAutoRediscover(List<Long> profileIds) {
		try (SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
			for (Long profileId : profileIds) {
				batchSession.delete("deleteProfileAutoRediscover", profileId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void deleteProfileAutoRediscoverInstanceByProfileId(long profileId) {
		session.delete("deleteProfileAutoRediscoverInstanceByProfileId", profileId);
	}

	@Override
	public void deleteProfileAutoRediscoverInstanceByProfileId(List<Long> profileIds) {
		try (SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
			for (Long profileId : profileIds) {
				batchSession.delete("deleteProfileAutoRediscoverInstanceByProfileId", profileId);
			}
			batchSession.commit();
		}
	}

	@Override
	public ProfilelibAutoRediscover getProfileAutoRediscover(long profileId) {
		return session.selectOne("getProfileAutoRediscover", profileId);
	}

	@Override
	public List<ProfilelibAutoRediscover> getAllProfileAutoRediscover() {
		return session.selectList("getAllProfileAutoRediscover");
	}

	@Override
	public List<ProfilelibAutoRediscover> getUsedProfileAutoRediscover() {
		return session.selectList("getUsedProfileAutoRediscover");
	}

	@Override
	public void deleteProfileAutoRediscoverInstanceByInstanceId(long instanceId) {
		session.delete("deleteProfileAutoRediscoverInstanceByInstanceId", instanceId);
	}

	@Override
	public void deleteProfileAutoRediscoverInstanceByInstanceId(List<Long> instanceIds) {
		try (SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
			for (Long instanceId : instanceIds) {
				batchSession.delete("deleteProfileAutoRediscoverInstanceByInstanceId", instanceId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateProfileAutoRediscover(ProfilelibAutoRediscover profilelibAutoRediscover) {
		session.update("updateProfileAutoRediscover", profilelibAutoRediscover);
	}

	@Override
	public List<ProfilelibAutoRediscoverInstance> getAutoRediscoverProfileInstanceByProfileId(long profileId) {
		return session.selectList("getAutoRediscoverProfileInstanceByProfileId", profileId);
	}

	@Override
	public void updateProfileInstanceExecuteTime(ProfilelibAutoRediscoverInstance profilelibAutoRediscoverInstance) {
		session.update("updateProfileInstanceExecuteTime", profilelibAutoRediscoverInstance);
	}

	@Override
	public void updateProfileInstanceExecuteTime(List<ProfilelibAutoRediscoverInstance> profilelibAutoRediscoverInstances) {
		if (!CollectionUtils.isEmpty(profilelibAutoRediscoverInstances)) {
			try (SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
				for (ProfilelibAutoRediscoverInstance profilelibAutoRediscoverInstance : profilelibAutoRediscoverInstances) {
					batchSession.update("updateProfileInstanceExecuteTime", profilelibAutoRediscoverInstance);
				}
				batchSession.commit();
			}
		}
	}

	@Override
	public int getProfileInstanceCount() {
		return session.selectOne("getProfileInstanceCount");
	}

	@Override
	public ProfilelibAutoRediscoverInstance getProfilelibAutoRediscoverInstanceByInstanceId(long instanceId) {
		return session.selectOne("getProfilelibAutoRediscoverInstanceByInstanceId", instanceId);
	}
}
