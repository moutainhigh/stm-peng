package com.mainsteam.stm.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.mainsteam.stm.lock.control.service.GlobalLockControlService;

public class LockServiceImpl implements LockService {
	private GlobalLockControlService lockControlService;
	private volatile Map<String, java.util.concurrent.locks.Lock> lockMap = new HashMap<String, java.util.concurrent.locks.Lock>();

	public void setLockControlService(GlobalLockControlService lockControlService) {
		this.lockControlService = lockControlService;
		lockControlService.setProxy(lockControlService);
	}

	@Override
	public Lock getlock(String lockName) {
		return getlock(lockName, 3);
	}

	@Override
	public Lock getlock(final String lockName, final int second) {
		return getlock(lockName, second, false);
	}

	private Lock getlock(final String lockName, final int second, boolean greed) {
		java.util.concurrent.locks.Lock nativeLock;
		if (lockMap.containsKey(lockName)) {
			nativeLock = lockMap.get(lockName);
		} else {
			synchronized (lockMap) {
				if (lockMap.containsKey(lockName)) {
					nativeLock = lockMap.get(lockName);
				} else {
					nativeLock = new ReentrantLock();
					lockMap.put(lockName, nativeLock);
				}
			}
		}
		return new LockImpl(lockControlService, lockName, second, nativeLock, greed);
	}

	@Override
	public <T> T sync(final String lockName, LockCallback<T> lock, final int second) {
		return getlock(lockName, second).sync(lock);
	}

	@Override
	public Lock getGreedlock(String lockName) {
		return getlock(lockName, 0, true);
	}

	@Override
	public <T> T greedSync(String lockName, LockCallback<T> lock) {
		return getGreedlock(lockName).sync(lock);
	}
}
