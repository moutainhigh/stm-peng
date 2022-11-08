package com.mainsteam.stm.instancelib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;

public class CustomModulePropCache {
	@SuppressWarnings("rawtypes")
	private IMemcache<Map> cache;

	public CustomModulePropCache() {
		cache = MemCacheFactory.getRemoteMemCache(Map.class);
	}
	
	@SuppressWarnings("unchecked")
	public void add(long instanceId,CustomModuleProp customModuleProp){
		if(cache != null && cache.isActivate()){
			String intanceIdkey = String.valueOf(instanceId);
			//map key : 实例id + modulekey  map value: 属性值
			Map<String,CustomModuleProp> map = cache.get(intanceIdkey);
			if(map == null){
				map = new HashMap<>();
			}
			String key = intanceIdkey + customModuleProp.getKey();
			map.put(key, customModuleProp);
			cache.set(intanceIdkey,map);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<CustomModuleProp> get(long instanceId){
		List<CustomModuleProp> result = null;
		if(cache != null && cache.isActivate()){
			String intanceIdkey = String.valueOf(instanceId);
			Map<String,CustomModuleProp> map = cache.get(intanceIdkey);
			if (map != null) {
				result = new ArrayList<>(map.values());
				return result;
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public CustomModuleProp get(long instanceId,String key){
		if(cache != null && cache.isActivate()){
			String intanceIdkey = String.valueOf(instanceId);
			//map key : 实例id + modulekey  map value: 属性值
			Map<String,CustomModuleProp> map = cache.get(intanceIdkey);
			if(map == null){
				return null;
			}
			String mapKey  = intanceIdkey + key;
			return map.get(mapKey);
		}
		return null;
	}
	
	public void remove(long instanceId){
		if(cache != null){
			String key = String.valueOf(instanceId);
			cache.delete(key);
		}
	}
	
	public void update(long instanceId,CustomModuleProp customModuleProp){
		add(instanceId,customModuleProp);
	}
	
	@SuppressWarnings("unchecked")
	public void remove(long instanceId,String key){
		if(cache != null && cache.isActivate()){
			String intanceIdkey = String.valueOf(instanceId);
			//map key : 实例id + modulekey  map value: 属性值
			Map<String,CustomModuleProp> map = cache.get(intanceIdkey);
			if(map != null){
				String mapKey  = intanceIdkey + key;
				map.remove(mapKey);
				if(map.isEmpty()){
					cache.delete(intanceIdkey);
				}else{
					cache.set(intanceIdkey,map);
				}
			}
		}
	}
	
}
