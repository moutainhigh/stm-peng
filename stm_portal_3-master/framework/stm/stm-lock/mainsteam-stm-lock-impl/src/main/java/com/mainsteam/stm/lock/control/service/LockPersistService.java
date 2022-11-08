package com.mainsteam.stm.lock.control.service;

import com.mainsteam.stm.lock.obj.LockRequest;




/** 
 * @author 作者：ziw
 * @date 创建时间：2016年12月22日 下午4:50:24
 * @version 1.0
 */
public interface LockPersistService {
	/**
	 * 插入一个LockRequest对象
	 * 
	 * @param request
	 */
	public boolean addLockRequest(LockRequest request);
	
	/**
	 * 新增一条锁信息
	 * 
	 * @param lockPersist
	 */
	public boolean addLockPersist(LockStatus lockPersist);
	
	/**
	 * 删除锁请求对象
	 * 
	 * @param request
	 * @return
	 */
	public boolean removeLockRequest(LockRequest request);
	
	/**
	 * 获得时间最远的一次LockRequest。
	 * 
	 * @param name
	 * @return 
	 */
	public LockRequest getTopLockRequest(String name);
}
