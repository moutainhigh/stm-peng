package com.mainsteam.stm.lock.control.service;

import com.mainsteam.stm.lock.obj.LockRequest;

/** 
 * @author 作者：ziw
 * @date 创建时间：2016年12月22日 上午11:24:18
 * @version 1.0
 */
public class LockStatus extends LockRequest {
	private boolean localLocked = false;
	private boolean isPrepareRemove=false;
	
	public LockStatus() {
	}
	public LockStatus(LockRequest request){
		super(request);
		
	}
	public boolean isLocalLocked() {
		return localLocked;
	}
	public void setLocalLocked(boolean localLocked) {
		this.localLocked = localLocked;
		this.isPrepareRemove = false;
	}
	public boolean isPrepareRemove() {
		return isPrepareRemove;
	}
	
	public void prepareRemove() {
		this.isPrepareRemove = true;
	}
}
