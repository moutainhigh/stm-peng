package com.mainsteam.stm.lock;

public class CollectorLockServiceImpl implements LockService {

	@Override
	public Lock getlock(String lockName) {
		return getlock(lockName, 3);
	}

	@Override
	public Lock getlock(final String lockName, final int second) {
		// 采集器锁不需要竞争
		return new CollectorLockImpl(lockName);
	}

	@Override
	public <T> T sync(final String lockName, LockCallback<T> lock,
			final int second) {
		// 采集器锁不需要竞争
		return new CollectorLockImpl(lockName).sync(lock);
	}

	@Override
	public Lock getGreedlock(String lockName) {
		return getlock(lockName);
	}

	@Override
	public <T> T greedSync(final String lockName, LockCallback<T> lock) {
		return getlock(lockName).sync(lock);
	}
}
