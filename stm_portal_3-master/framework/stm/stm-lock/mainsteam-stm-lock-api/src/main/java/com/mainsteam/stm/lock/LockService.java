package com.mainsteam.stm.lock;
/**分布式锁服务 
 * @author cx
 *
 */
public interface LockService {

	/**获得锁 <br />
	 * 默认执行3秒
	 * @param lockName
	 * @param lock
	 * @return
	 */
	Lock getlock(String lockName);
	
	/**获得锁
	 * @param lockName
	 * @param lock
	 * @param second 可能执行时长
	 * @return
	 */
	Lock getlock(String lockName,int second);
	
	/**获得贪心锁 <br />
	 * 获取锁后，不主动释放，直到有其它节点申请。
	 * @param lockName
	 * @param lock
	 * @return
	 */
	Lock getGreedlock(String lockName);
	
	/**
	 * @param lockName
	 * @param lock
	 * @param second 可能执行时长
	 * @return
	 */
	<T> T sync(String lockName,LockCallback<T> lock,int second);
	
	/**
	 * 贪心锁定执行.<br>
	 *  获取锁后，不主动释放，直到有其它节点申请。
	 * @param lockName
	 * @param lock
	 * @param second
	 * @return
	 */
	<T> T greedSync(String lockName,LockCallback<T> lock);
}
