package com.mainsteam.stm.lock;

public interface Lock {
	
	/** 加锁
	 * @return
	 */
	public boolean lock();
	
	/**解锁
	 * @return
	 */
	public boolean unlock();
	
	/** 同步代码块，不允许与{@link #lock()}一起使用
	 * @param lock
	 * @return
	 */
	public <T> T sync(LockCallback<T> lock);

	
}
