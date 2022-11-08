/**
 * 
 */
package com.mainsteam.stm.plugin.vmware.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年7月18日 下午3:29:55
 * @version 1.0
 */
/** 
 */
public class VmwareBeansCachedecorator {

	private static Map<URL, VmwareBeansCache> beansCache = new HashMap<>();

	/**
	 * 
	 */
	public VmwareBeansCachedecorator() {
	}

	private synchronized static VmwareBeansCache createCacheBean(ServiceInstance si) {
		URL url = si.getServerConnection().getUrl();
		if (beansCache.containsKey(url)) {
			return beansCache.get(url);
		} else {
			VmwareBeansCache cache = new VmwareBeansCache(si);
			beansCache.put(url, cache);
			return cache;
		}
	}

	public static void initCache(ServiceInstance si) {
		createCacheBean(si).init();
	}

	public static void destoryCache(ServiceInstance si) {
		VmwareBeansCache cache = beansCache.get(si.getServerConnection().getUrl());
		if (cache != null) {
			cache.destory();
			synchronized (VmwareBeansCachedecorator.class) {
				beansCache.remove(si.getServerConnection().getUrl());
			}
		}
	}

	public static void cacheVm(String uuid, VirtualMachine machine) {
		VmwareBeansCache cache = beansCache.get(machine.getServerConnection().getUrl());
		if (cache != null) {
			cache.cacheVm(uuid, machine);
		}
	}

	public static VirtualMachine getVm(ServiceInstance si, String uuid) {
		VmwareBeansCache cache = beansCache.get(si.getServerConnection().getUrl());
		if (cache != null) {
			return cache.getVm(uuid);
		}
		return null;
	}

	public static void cacheHost(String uuid, HostSystem machine) {
		VmwareBeansCache cache = beansCache.get(machine.getServerConnection().getUrl());
		if (cache != null) {
			cache.cacheHost(uuid, machine);
		}
	}

	public static HostSystem getHost(ServiceInstance si, String uuid) {
		VmwareBeansCache cache = beansCache.get(si.getServerConnection().getUrl());
		if (cache != null) {
			return cache.getHost(uuid);
		}
		return null;
	}

	public static void cacheDatastore(String uuid, Datastore st) {
		VmwareBeansCache cache = beansCache.get(st.getServerConnection().getUrl());
		if (cache != null) {
			cache.cacheDatastore(uuid, st);
		}
	}

	public static Datastore getDatastore(ServiceInstance si, String uuid) {
		VmwareBeansCache cache = beansCache.get(si.getServerConnection().getUrl());
		if (cache != null) {
			return cache.getDatastore(uuid);
		}
		return null;
	}

	public static Integer getCounter(ServiceInstance si, String name) {
		VmwareBeansCache cache = beansCache.get(si.getServerConnection().getUrl());
		if (cache != null) {
			return cache.getCounter(name);
		}
		return null;
	}

	public static String getCounterName(ServiceInstance si, Integer counter) {
		VmwareBeansCache cache = beansCache.get(si.getServerConnection().getUrl());
		if (cache != null) {
			return cache.getCounterName(counter);
		}
		return null;
	}
}
