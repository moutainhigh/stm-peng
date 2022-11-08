/**
 * 
 */
package com.mainsteam.stm.pluginserver.util;

import java.util.LinkedList;

/**
 * @author ziw
 * 
 */
public class ReentrantLock {

	private final LinkedList<Long> threadLockQueue;

	private boolean lock = true;

	/**
	 * 
	 */
	public ReentrantLock() {
		/**
		 * 使用concurrent工具包，在锁对象太多的情况下，太消耗内存了。
		 */
		threadLockQueue = new LinkedList<Long>();
	}

	public synchronized void lock() {
		if (lock) {
			Long tid = Long.valueOf(Thread.currentThread().getId());
			threadLockQueue.add(tid);
			if (threadLockQueue.size() > 1) {
				do {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
					Long firstThreadId = threadLockQueue.getFirst();
					if (firstThreadId == tid) {
						break;
					}
				} while (lock);
			}
		}
	}

	public synchronized void unlock() {
//		if (lock) {
			if(threadLockQueue.size()>0){
				threadLockQueue.removeFirst();
			}
			this.notifyAll();
//		}
	}

	public synchronized void close() {
		lock = false;
		this.notifyAll();
	}
}
