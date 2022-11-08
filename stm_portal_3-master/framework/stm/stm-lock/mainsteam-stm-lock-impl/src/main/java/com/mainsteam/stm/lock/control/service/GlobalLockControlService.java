package com.mainsteam.stm.lock.control.service;

import com.mainsteam.stm.lock.obj.LockRequest;

/** 
 * @author 作者：ziw
 * @date 创建时间：2016年12月22日 下午4:35:19
 * @version 1.0
 */
public interface GlobalLockControlService {
	public void lock(LockRequest lockRequest);
	public void unlock(String name);
	public void setProxy(GlobalLockControlService proxy);
	public abstract void deleteTimeoutLock(long timeout);
	public abstract void deleteTimeoutLockRequest(long timeout);

	/**
	 * 发送心跳数据
	 * 
	 * @param node
	 */
	public abstract void addHeartbeatForLockRequest(String node);

	/**
	 * 发送心跳数据
	 * 
	 * @param node
	 */
	public abstract void addHeartbeatForLock(String node);

	/**
	 * 获取检查LockRequest的频度
	 * 
	 * @return
	 */
	public abstract long getCheckFreq();

	/**
	 * 加载所有的LockRequest请求
	 * 
	 * @return
	 */
	public abstract java.util.List<LockRequest> selectTopLockRequests();

	/**
	 * 保存新增加的LockRequest
	 * 
	 * @param lockRequest
	 * @return
	 */
	public abstract boolean addLockRequest(LockRequest lockRequest);

	/**
	 * 删除LockRequest
	 * 
	 * @param request
	 */
	public abstract void removeLockRequest(LockRequest request);

	/**
	 * 保存锁
	 * 
	 * @param lockRequest
	 * @return
	 */
	public abstract boolean insertLock(LockStatus lockRequest);

	/**
	 * 查询锁
	 * 
	 * @param key
	 * @return
	 */
	public abstract LockStatus getLock(String key);

	/**
	 * 删除锁
	 * 
	 * @param lockRequest
	 * @return
	 */
	public abstract boolean removeLock(LockStatus lockRequest);
}
