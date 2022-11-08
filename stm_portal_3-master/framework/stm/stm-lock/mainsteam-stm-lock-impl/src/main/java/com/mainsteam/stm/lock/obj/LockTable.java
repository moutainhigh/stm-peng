package com.mainsteam.stm.lock.obj;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.mainsteam.stm.lock.control.service.LockStatus;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年12月22日 上午11:17:11
 * @version 1.0
 */
public class LockTable {

	private Map<String, LockStatus> lockMap = new HashMap<>();
	private Map<String, LockRequest> lockRequestQueueMap = new HashMap<>();
	private ReadLock readLock;
	private WriteLock writeLock;
	private ReadLock readLockRequest;
	private WriteLock writeLockRequest;

	public LockTable() {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
		lock = new ReentrantReadWriteLock();
		readLockRequest = lock.readLock();
		writeLockRequest = lock.writeLock();
	}

	public LockStatus getLock(String key) {
		LockStatus status = null;
		readLock.lock();
		status = lockMap.get(key);
		readLock.unlock();
		return status;
	}

	public boolean tryRemoveLock(String key) {
		boolean result = false;
		writeLock.lock();
		LockStatus lockStatus = lockMap.get(key);
		if (lockStatus != null) {
			if (lockStatus.isLocalLocked()) {
				lockStatus.prepareRemove();
			} else {
				lockMap.remove(key);
				result = true;
			}
		}
		writeLock.unlock();
		return result;
	}

	public boolean makeLock(String key) {
		boolean result = false;
		writeLock.lock();
		LockStatus status = lockMap.get(key);
		if (status != null && !status.isPrepareRemove()) {
			status.setLocalLocked(true);
			result = true;
		} else {
			result = false;
		}
		writeLock.unlock();
		return result;
	}

	public boolean isLocked(String key) {
		boolean isLock = false;
		readLock.lock();
		isLock = lockMap.containsKey(key) && lockMap.get(key).isLocalLocked();
		readLock.unlock();
		return isLock;
	}

	public void releaseLock(String key) {
		writeLock.lock();
		lockMap.get(key).setLocalLocked(false);
		writeLock.unlock();
	}

	public void addLock(LockStatus lockPersist) {
		writeLock.lock();
		lockMap.put(lockPersist.getName(), lockPersist);
		writeLock.unlock();
	}

	public void removeLock(String key) {
		writeLock.lock();
		lockMap.remove(key);
		writeLock.unlock();
	}

	public void addLockRequest(LockRequest lockRequest) {
		writeLockRequest.lock();
		lockRequestQueueMap.put(lockRequest.getName(), lockRequest);
		writeLockRequest.unlock();
	}

	public LockRequest getLockRequest(String key) {
		LockRequest request = null;
		readLockRequest.lock();
		request = lockRequestQueueMap.get(key);
		readLockRequest.unlock();
		return request;
	}

	public LockRequest removeLockRequest(String key) {
		LockRequest request = null;
		writeLockRequest.lock();
		request = lockRequestQueueMap.remove(key);
		writeLockRequest.unlock();
		return request;
	}
}
