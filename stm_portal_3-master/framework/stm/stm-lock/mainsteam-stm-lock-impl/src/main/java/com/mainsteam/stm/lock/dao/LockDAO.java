package com.mainsteam.stm.lock.dao;

import java.util.List;


public interface LockDAO {
	public List<LockRequestDO> selectLockRequests();
	public int insertLockRequest(LockRequestDO lockRequestDO);
	public int removeLockRequest(LockRequestDO request);
	public int setLockRequestToDelete(LockRequestDO request);
	public int insertLock(LockRequestDO lockRequest);
	public LockRequestDO getLock(String key);
	public int removeLock(String name);
	public List<LockRequestDO> selectDeleteTimeoutLockRequests(int offset);
	public List<LockRequestDO> selectDeleteTimeoutLock(int timeout);
	public int updateLockRequestHeartbeatTime(LockRequestDO node);
	public int updateLockHeartbeatTime(LockRequestDO node);
	public List<LockRequestDO> selectLocks();
}
