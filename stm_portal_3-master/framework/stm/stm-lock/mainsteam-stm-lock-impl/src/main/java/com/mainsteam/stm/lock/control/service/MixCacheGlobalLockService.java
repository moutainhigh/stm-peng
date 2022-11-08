package com.mainsteam.stm.lock.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mainsteam.stm.lock.obj.LockRequest;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年1月26日 下午1:42:13
 * @version 1.0
 */
public class MixCacheGlobalLockService extends DatabaseGlobalLockControlService {

	private Map<String, LockStatus> statusMap = new HashMap<>();
	private List<LockRequest> requests = new LinkedList<LockRequest>();

	public MixCacheGlobalLockService() {
	}

	@Override
	public void addHeartbeatForLockRequest(String node) {
		Date d = new Date();
		super.addHeartbeatForLockRequest(node);
		synchronized (requests) {
			for (Iterator<LockRequest> iterator = requests.iterator(); iterator
					.hasNext();) {
				LockRequest r = (LockRequest) iterator.next();
				if (r.getNode().equals(node)) {
					r.setCurrentTime(d);
				}
			}
		}
	}

	@Override
	public List<LockRequest> selectTopLockRequests() {
		synchronized (requests) {
			return new ArrayList<>(requests);
		}
	}

	@Override
	public boolean addLockRequest(LockRequest lockRequest) {
		super.addLockRequest(lockRequest);
		synchronized (requests) {
			return requests.add(lockRequest);
		}
	}

	@Override
	public void removeLockRequest(LockRequest request) {
		super.removeLockRequest(request);
		synchronized (requests) {
			for (Iterator<LockRequest> iterator = requests.iterator(); iterator
					.hasNext();) {
				LockRequest r = (LockRequest) iterator.next();
				if (r.getNode().equals(request.getNode())
						&& r.getName().equals(request.getName())) {
					iterator.remove();
					break;
				}
			}
		}
	}

	@Override
	public boolean insertLock(LockStatus lockRequest) {
		super.insertLock(lockRequest);
		synchronized (statusMap) {
			if (statusMap.containsKey(lockRequest.getName())) {
				return false;
			}
			statusMap.put(lockRequest.getName(), lockRequest);
			return true;
		}
	}

	@Override
	public LockStatus getLock(String key) {
		return statusMap.get(key);
	}

	@Override
	public boolean removeLock(LockStatus lockRequest) {
		super.removeLock(lockRequest);
		synchronized (statusMap) {
			return statusMap.remove(lockRequest.getName()) != null;
		}
	}

	@Override
	public void deleteTimeoutLock(long timeout) {
		super.deleteTimeoutLock(timeout);
		long current = System.currentTimeMillis();
		List<String> toRemoveKeys = new ArrayList<>();
		synchronized (statusMap) {
			for (Entry<String, LockStatus> lock : statusMap.entrySet()) {
				if (current - lock.getValue().getCurrentTime().getTime() > timeout) {
					toRemoveKeys.add(lock.getKey());
				}
			}
			for (String key : toRemoveKeys) {
				statusMap.remove(key);
			}
		}
	}

	@Override
	public void deleteTimeoutLockRequest(long timeout) {
		super.deleteTimeoutLockRequest(timeout);
		long current = System.currentTimeMillis();
		synchronized (requests) {
			for (Iterator<LockRequest> iterator = requests.iterator(); iterator
					.hasNext();) {
				LockRequest r = (LockRequest) iterator.next();
				if (current - r.getCurrentTime().getTime() > timeout) {
					iterator.remove();
				}
			}
		}
	}

	@Override
	public void addHeartbeatForLock(String node) {
		super.addHeartbeatForLock(node);
		Date d = new Date();
		synchronized (statusMap) {
			for (Entry<String, LockStatus> lock : statusMap.entrySet()) {
				if (lock.getValue().getNode().equals(node)) {
					lock.getValue().setCurrentTime(d);
				}
			}
		}
	}

}
