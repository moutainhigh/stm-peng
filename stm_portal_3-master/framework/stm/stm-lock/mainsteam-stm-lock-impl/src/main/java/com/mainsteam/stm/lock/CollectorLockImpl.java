package com.mainsteam.stm.lock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CollectorLockImpl implements Lock {
	private static final Log logger=LogFactory.getLog(CollectorLockImpl.class);
	
	private final static Map<String,java.util.concurrent.locks.Lock> lockMap=Collections.synchronizedMap(new HashMap<String,java.util.concurrent.locks.Lock>());
	private java.util.concurrent.locks.Lock theadLock;
	
	public CollectorLockImpl(String lockName){
		synchronized (lockMap) {
			if(lockMap.containsKey(lockName)){
				this.theadLock=lockMap.get(lockName);
			}else{
				this.theadLock=new ReentrantLock();
				lockMap.put(lockName,theadLock );
			}
		}
	}
	@Override
	public boolean lock() {
		theadLock.lock();
		return true;
	}

	@Override
	public boolean unlock() {
		theadLock.unlock();
		return true;
	}
	
	public <T> T sync(LockCallback<T> callBack){
		T t=null;
		synchronized (theadLock) {
			try{
				t=callBack.doAction();
			}catch(Exception e){
				if(logger.isErrorEnabled())
					logger.error(e.getMessage(),e);
			}
		}
		return t;
	}
	
	
}
