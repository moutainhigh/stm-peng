package com.mainsteam.stm.plugin.vmware.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年7月18日 上午9:17:31
 * @version 1.0
 */
public class VmwareBeansCache {

	private Map<String, VirtualMachine> vms = new HashMap<>(100);
	private ReadLock vmReadLock;
	private WriteLock vmWriteLock;
	
	private Map<String, HostSystem> hosts = new HashMap<>(100);
	private ReadLock hostReadLock;
	private WriteLock hostWriteLock;

	private Map<String, Datastore> dataStores = new HashMap<>(100);
	private ReadLock dataStoresReadLock;
	private WriteLock dataStoresWriteLock;
	private Map<String, Integer> C_COUNTERIDMAP = new HashMap<String, Integer>();
	private Map<Integer, String> C_COUNTERNAMEMAP = new HashMap<Integer, String>();
	public VmwareBeansCache(ServiceInstance si) {
		ReentrantReadWriteLock lock1 = new ReentrantReadWriteLock();
		vmReadLock = lock1.readLock();
		vmWriteLock = lock1.writeLock();
		lock1 = new ReentrantReadWriteLock();
		hostReadLock = lock1.readLock();
		hostWriteLock = lock1.writeLock();
		
		lock1 = new ReentrantReadWriteLock();
		dataStoresReadLock = lock1.readLock();
		dataStoresWriteLock = lock1.writeLock();
		
		initCounterMap(si);
	}
	
	private void initCounterMap(ServiceInstance si){
		PerformanceManager t_pm = si.getPerformanceManager();
		PerfCounterInfo[] t_pcis = t_pm.getPerfCounter();
		for (PerfCounterInfo t_pci : t_pcis) {
			Integer t_counterId = new Integer(t_pci.getKey());
			// construct counter name
			StringBuilder t_sb = new StringBuilder();
			t_sb.append(t_pci.getGroupInfo().getKey()).append(".");
			t_sb.append(t_pci.getNameInfo().getKey()).append(".");
			t_sb.append(t_pci.getRollupType().toString());
			C_COUNTERIDMAP.put(t_sb.toString(), t_counterId);
			C_COUNTERNAMEMAP.put(t_counterId, t_sb.toString());
		}
	}

	public synchronized void init() {
		if(vms == null){
			vms = new HashMap<>(100);
			hosts = new HashMap<>(100);
			dataStores = new HashMap<>(100);
		}
	}

	public synchronized void destory() {
		vms = null;
		hosts = null;
		dataStores = null;
	}

	public void cacheVm(String uuid, VirtualMachine machine) {
		vmWriteLock.lock();
		try {
			vms.put(uuid, machine);
		} finally {
			vmWriteLock.unlock();
		}
	}

	public VirtualMachine getVm(String uuid) {
		vmReadLock.lock();
		try {
			return vms.get(uuid);
		} finally {
			vmReadLock.unlock();
		}
	}

	public void cacheHost(String uuid, HostSystem host) {
		hostWriteLock.lock();
		try {
			hosts.put(uuid, host);
		} finally {
			hostWriteLock.unlock();
		}
	}

	public HostSystem getHost(String uuid) {
		hostReadLock.lock();
		try {
			return hosts.get(uuid);
		} finally {
			hostReadLock.unlock();
		}
	}
	
	
	public void cacheDatastore(String uuid, Datastore st) {
		dataStoresWriteLock.lock();
		try {
			dataStores.put(uuid, st);
		} finally {
			dataStoresWriteLock.unlock();
		}
	}

	public Datastore getDatastore(String uuid) {
		dataStoresReadLock.lock();
		try {
			return dataStores.get(uuid);
		} finally {
			dataStoresReadLock.unlock();
		}
	}
	
	public Integer getCounter(String name){
		return C_COUNTERIDMAP.get(name);
	}
	public String getCounterName(Integer counter){
		return C_COUNTERNAMEMAP.get(counter);
	}
}
