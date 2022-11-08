package com.mainsteam.stm.lock.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;


public class LockDAOImpl implements LockDAO {

	private SqlSession session;

	public LockDAOImpl(SqlSession session) {
		super();
		this.session = session;
	}

	@Override
	public List<LockRequestDO> selectLocks() {
		List<LockRequestDO> result = null;
		try {
			result = session.selectList("com.mainsteam.stm.lock.dao.LockDAO.selectLocks");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	@Override
	public List<LockRequestDO> selectLockRequests() {
		List<LockRequestDO> result = null;
		try {
			result = session.selectList("com.mainsteam.stm.lock.dao.LockDAO.selectLockRequests");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	@Override
	public int insertLockRequest(LockRequestDO lockRequestDO) {
		return session.insert("com.mainsteam.stm.lock.dao.LockDAO.insertLockRequest", lockRequestDO);
	}

	@Override
	public int removeLockRequest(LockRequestDO request) {
		return session.delete("com.mainsteam.stm.lock.dao.LockDAO.removeLockRequest", request);
	}

	@Override
	public int insertLock(LockRequestDO lockRequest) {
		return session.insert("com.mainsteam.stm.lock.dao.LockDAO.insertLock", lockRequest);
	}

	@Override
	public LockRequestDO getLock(String key) {
		return session.selectOne("com.mainsteam.stm.lock.dao.LockDAO.selectLock", key);
	}

	@Override
	public int removeLock(String name) {
		return session.delete("com.mainsteam.stm.lock.dao.LockDAO.removeLock", name);
	}

	@Override
	public List<LockRequestDO> selectDeleteTimeoutLockRequests(int offset) {
		return session.selectList("com.mainsteam.stm.lock.dao.LockDAO.deleteTimeoutLockRequest", offset);
	}

	@Override
	public List<LockRequestDO> selectDeleteTimeoutLock(int timeout) {
		return session.selectList("com.mainsteam.stm.lock.dao.LockDAO.deleteTimeoutLock", timeout);
	}

	@Override
	public int updateLockRequestHeartbeatTime(LockRequestDO node) {
		return session.update("com.mainsteam.stm.lock.dao.LockDAO.updateLockRequestHeartbeatTime", node);
	}

	@Override
	public int updateLockHeartbeatTime(LockRequestDO node) {
		return session.update("com.mainsteam.stm.lock.dao.LockDAO.updateLockHeartbeatTime", node);
	}

	@Override
	public int setLockRequestToDelete(LockRequestDO request) {
		return session.update("com.mainsteam.stm.lock.dao.LockDAO.setLockRequestToDelete", request);
	}
}
