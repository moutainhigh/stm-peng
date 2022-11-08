package com.mainsteam.stm.lock.control.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;

import com.mainsteam.stm.lock.dao.LockDAO;
import com.mainsteam.stm.lock.dao.LockRequestDO;
import com.mainsteam.stm.lock.obj.LockRequest;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年12月29日 下午4:33:05
 * @version 1.0
 */
public class DatabaseGlobalLockControlService extends AbstractGlobalLockControlService {

	private LockDAO lockDAO;

	public void setLockDAO(LockDAO lockDAO) {
		this.lockDAO = lockDAO;
	}

	public DatabaseGlobalLockControlService() {
	}

	@Override
	public void addHeartbeatForLockRequest(String node) {
		List<LockRequestDO> lockRequestDOs = lockDAO.selectLockRequests();
		if(lockRequestDOs != null){
			for (LockRequestDO lockRequestDO : lockRequestDOs) {
				if (node.equals(lockRequestDO.getNode())) {
					lockDAO.updateLockRequestHeartbeatTime(lockRequestDO);
				}
			}
		}
	}

	@Override
	public long getCheckFreq() {
		return 1000;
	}

	@Override
	public List<LockRequest> selectTopLockRequests() {
		List<LockRequest> lockRequests = null;
		List<LockRequestDO> list = lockDAO.selectLockRequests();
		if (list != null) {
			lockRequests = new ArrayList<>(list.size());
			for (LockRequestDO lockRequestDO : list) {
				lockRequests.add(toLockRequest(lockRequestDO));
			}
		}
		return lockRequests;
	}

	@Override
	public boolean addLockRequest(LockRequest lockRequest) {
		return lockDAO.insertLockRequest(toLockRequestDO(lockRequest)) > 0;
	}

	@Override
	public void removeLockRequest(LockRequest request) {
		LockRequestDO requestDO = toLockRequestDO(request);
		// lockDAO.setLockRequestToDelete(requestDO);
		lockDAO.removeLockRequest(requestDO);
	}

	@Override
	public boolean insertLock(LockStatus lockRequest) {
		boolean result = false;
		try {
			result = lockDAO.insertLock(toLockRequestDO(lockRequest)) > 0;
		} catch (DuplicateKeyException e) {
		}
		return result;
	}

	@Override
	public LockStatus getLock(String key) {
		LockRequestDO request = lockDAO.getLock(key);
		if (request != null) {
			return toLockStatus(request);
		}
		return null;
	}

	@Override
	public boolean removeLock(LockStatus lockRequest) {
		return lockDAO.removeLock(lockRequest.getName()) > 0;
	}

	@Override
	public void deleteTimeoutLock(long timeout) {
		List<LockRequestDO> timeoutLocks = lockDAO.selectDeleteTimeoutLock((int) timeout);
		if(timeoutLocks != null){
			for (LockRequestDO lockRequestDO : timeoutLocks) {
				lockDAO.removeLock(lockRequestDO.getName());
			}
		}
	}

	@Override
	public void deleteTimeoutLockRequest(long timeout) {
		List<LockRequestDO> timeoutRequsts = lockDAO.selectDeleteTimeoutLockRequests((int) timeout);
		if(timeoutRequsts != null){
			for (LockRequestDO lockRequestDO : timeoutRequsts) {
				lockDAO.removeLockRequest(lockRequestDO);
			}
		}
	}

	@Override
	public void addHeartbeatForLock(String node) {
		List<LockRequestDO> lockRequestDOs = lockDAO.selectLocks();
		if(lockRequestDOs != null){
			for (LockRequestDO lockRequestDO : lockRequestDOs) {
				if (node.equals(lockRequestDO.getNode())) {
					lockDAO.updateLockHeartbeatTime(lockRequestDO);
				}
			}
		}
	}

	private static LockRequestDO toLockRequestDO(LockRequest lockRequest) {
		LockRequestDO requestDO = new LockRequestDO();
		requestDO.setGreed(lockRequest.isGreed() ? 1 : 0);
		requestDO.setName(lockRequest.getName());
		requestDO.setNode(lockRequest.getNode());
		requestDO.setRequestTime(lockRequest.getRequestTime());
		return requestDO;
	}

	private static LockRequest toLockRequest(LockRequestDO lockRequest) {
		LockRequest request = new LockRequest();
		request.setGreed(lockRequest.getGreed() == 1);
		request.setName(lockRequest.getName());
		request.setNode(lockRequest.getNode());
		request.setRequestTime(lockRequest.getRequestTime());
		request.setCurrentTime(lockRequest.getCurrentTime());
		return request;
	}

	private static LockStatus toLockStatus(LockRequestDO lockRequest) {
		LockStatus request = new LockStatus();
		request.setGreed(lockRequest.getGreed() == 1);
		request.setName(lockRequest.getName());
		request.setNode(lockRequest.getNode());
		request.setRequestTime(lockRequest.getRequestTime());
		request.setCurrentTime(lockRequest.getCurrentTime());
		return request;
	}
}
