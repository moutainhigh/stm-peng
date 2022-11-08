package com.mainsteam.stm.instancelib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.instancelib.bean.TempCacheValue;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.util.ResourceOrMetricConst;

/*
 * 网络设备判断重复   
 * 1： sysoid+资源名称
 * 2： sysoid + 70% mac + 70% ip
 * 
 * 主机判断重复   
 * 1：categoryId+资源名称 
 * 2：70% mac + 70% ip
 *
 * 除主机、网络设备外判断重复   
 * 1： categoryId+唯一标识
 */
public class UniqueInstanceCache {

	private HashMap<String, HashSet<Long>> device = new HashMap<String, HashSet<Long>>(1000);
	//key: sysoid 值   value:ip跟mac缓存值
	private HashMap<String,List<TempCacheValue>> networkDevice = new HashMap<>(500); 
	//key: categoryId   value:ip跟mac缓存值
	private HashMap<String,List<TempCacheValue>> hostDevice = new HashMap<>(500); 
	
	private UniqueInstanceCache(){}
	
	private static class SingleonHelper{
		private static UniqueInstanceCache uniqueInstanceCache = new UniqueInstanceCache();
	}
	
	public static UniqueInstanceCache getInstanceCache(){
		return SingleonHelper.uniqueInstanceCache;
	}
	
	private synchronized HashSet<Long> getDeviceUnique(String key){
		return device.get(key);
	}
	
	public synchronized List<TempCacheValue> getNetworkUnique(String sysoid){
		return networkDevice.get(sysoid);
	}
	
	public synchronized List<TempCacheValue> getHostUnique(String categoryId){
		return hostDevice.get(categoryId);
	}
	
	public synchronized void removeUnique(ResourceInstance deletedInstance){
		String uniqueKey = null;
		String[] uniqueKeys = deletedInstance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);
		if(uniqueKeys != null){
			uniqueKey = uniqueKeys[0];
			device.remove(deletedInstance.getCategoryId() + uniqueKey);
		}
	}
	
	private synchronized void addDeviceUnique(String key,long instanceId){
		HashSet<Long> result = device.get(key);
		if(result == null){
			result= new HashSet<>(1);
			device.put(key, result);
		}
		result.add(instanceId);
	}
	
	private synchronized void addNetWork(String sysoid,TempCacheValue cacheValue){
		List<TempCacheValue> tempValue = networkDevice.get(sysoid);
		if(tempValue == null){
			tempValue = new ArrayList<>();
			networkDevice.put(sysoid, tempValue);
		}
		tempValue.add(cacheValue);
	}
	
	private synchronized void addHost(String categoryId,TempCacheValue cacheValue){
		List<TempCacheValue> tempValue = hostDevice.get(categoryId);
		if(tempValue == null){
			tempValue = new ArrayList<>();
			hostDevice.put(categoryId, tempValue);
		}
		tempValue.add(cacheValue);
	}
	
	public void addUnique(ResourceInstance instance){
		String type = instance.getParentCategoryId();
		String categoryId = instance.getCategoryId();
		String instanceName = instance.getName();
		StringBuilder key = new StringBuilder(100);
		if(type == null){
			String uniqueKey = null;
			String[] uniqueKeys = instance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);
			if(uniqueKeys != null){
				uniqueKey = uniqueKeys[0];
				if(StringUtils.isNotEmpty(uniqueKey)){
					key.append(categoryId).append(uniqueKey);
				}
			}
		}else{
			switch (type) {
				case CapacityConst.HOST:
					key.append(categoryId).append(instanceName);
					TempCacheValue v = new TempCacheValue();
					v.setInstanceId(instance.getId());
					v.setIp(instance.getModulePropBykey(MetricIdConsts.METRIC_IP));
					v.setMac(instance.getModulePropBykey(MetricIdConsts.METRIC_MACADDRESS));
					addHost(categoryId, v);
					break;
				case CapacityConst.NETWORK_DEVICE:
					String sysoid = null;
					String[] sysoids = instance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_SYSOID);
					if(sysoids != null){
						sysoid = sysoids[0];
						if(StringUtils.isNotEmpty(sysoid)){
							key.append(sysoid).append(instanceName);
						}
					}
					TempCacheValue v1 = new TempCacheValue();
					v1.setInstanceId(instance.getId());
					v1.setIp(instance.getModulePropBykey(MetricIdConsts.METRIC_IP));
					v1.setMac(instance.getModulePropBykey(MetricIdConsts.METRIC_MACADDRESS));
					addNetWork(sysoid, v1);
					break;
				default:
					String uniqueKey = null;
					String[] uniqueKeys = instance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);
					if(uniqueKeys != null){
						uniqueKey = uniqueKeys[0];
						if(StringUtils.isNotEmpty(uniqueKey)){
							key.append(categoryId).append(uniqueKey);
						}
					}
					break;
				}
			}
		addDeviceUnique(key.toString(), instance.getId());
	}
	
	public HashSet<Long> getUnique(ResourceInstance instance){
		String type = instance.getParentCategoryId();
		String categoryId = instance.getCategoryId();
		String instanceName = instance.getName();
		StringBuilder key = new StringBuilder(100);
		if(type == null){
			String uniqueKey = null;
			String[] uniqueKeys = instance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);
			if(uniqueKeys != null){
				uniqueKey = uniqueKeys[0];
				if(StringUtils.isNotEmpty(uniqueKey)){
					key.append(categoryId).append(uniqueKey);
				}
			}
		}else{
			switch (type) {
				case CapacityConst.HOST:
					key.append(categoryId).append(instanceName);
					break;
				case CapacityConst.NETWORK_DEVICE:
					String sysoid = null;
					String[] sysoids = instance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_SYSOID);
					if(sysoids != null){
						sysoid = sysoids[0];
						if(StringUtils.isNotEmpty(sysoid)){
							key.append(sysoid).append(instanceName);
						}
					}
					break;
				default:
					String uniqueKey = null;
					String[] uniqueKeys = instance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);
					if(uniqueKeys != null){
						uniqueKey = uniqueKeys[0];
						if(StringUtils.isNotEmpty(uniqueKey)){
							key.append(categoryId).append(uniqueKey);
						}
					}
					break;
				}
		}
		return getDeviceUnique(key.toString());
	}
	
}
