package com.mainsteam.stm.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.lock.control.service.GlobalLockControlService;
import com.mainsteam.stm.lock.obj.LockRequest;

public class LockImpl implements Lock {
	private static final Log logger = LogFactory.getLog(LockImpl.class);

	private String lockName;
	private GlobalLockControlService lockControlService;
	private java.util.concurrent.locks.Lock nativeLock;
	private boolean greed = false;// 是否实现贪心锁

	public LockImpl(GlobalLockControlService lockControlService,
			String lockName, int second,
			java.util.concurrent.locks.Lock nativeLock) {
		this(lockControlService, lockName, second, nativeLock, false);
	}

	public GlobalLockControlService getLockControlService() {
		return lockControlService;
	}

	public LockImpl(GlobalLockControlService lockControlService,
			String lockName, int second,
			java.util.concurrent.locks.Lock nativeLock, boolean greed) {
		this.lockControlService = lockControlService;
		this.lockName = lockName;
		this.greed = greed;
		this.nativeLock = nativeLock;
	}

	@Override
	public boolean lock() {
		if (logger.isDebugEnabled()) {
			logger.debug("lock lockName="+lockName);
		}
		nativeLock.lock();
		if (logger.isDebugEnabled()) {
			logger.debug("lock lockName="+lockName+" ok.");
		}
		LockRequest request = new LockRequest();
		request.setName(lockName);
		request.setGreed(greed);
		try {
			lockControlService.lock(request);
		} catch (Exception e) {
			nativeLock.unlock();
			throw e;
		}
		return true;
	}

	@Override
	public boolean unlock() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("unlock lockName="+lockName);
			}
			lockControlService.unlock(lockName);
		} catch (RuntimeException e) {
			throw e;
		} finally {
			nativeLock.unlock();
		}
		return true;
	}

	public <T> T sync(LockCallback<T> callBack) {
		T t = null;
		lock();
		try {
			t = callBack.doAction();
		} finally {
			unlock();
		}
		return t;
	}
}
