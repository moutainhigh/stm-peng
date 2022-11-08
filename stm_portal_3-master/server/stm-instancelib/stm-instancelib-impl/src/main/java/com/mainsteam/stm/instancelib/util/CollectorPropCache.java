package com.mainsteam.stm.instancelib.util;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;

public class CollectorPropCache {

	@SuppressWarnings("rawtypes")
	private IMemcache<Map> cache;
	
	public CollectorPropCache(){
		cache =  MemCacheFactory.getLocalMemCache(Map.class);
		if(cache == null){
			throw new InstancelibRuntimeException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_CACHE_ERROR, "memcache not load!");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void add(long instanceId,PropTypeEnum typeEnum,InstanceProp prop){
		//map key : 属性key + 属性type  map value: 属性值
		Map<String,InstanceProp> map = cache.get(String.valueOf(instanceId));
		if(map == null){
			map = new HashMap<>();
		}
		String key = prop.getKey() + typeEnum.toString();
		map.put(key, prop);
		cache.set(String.valueOf(instanceId),map);
	}
	
	public void remove(long instanceId){
		String key = String.valueOf(instanceId);
		cache.delete(key);
	}
	
	@SuppressWarnings("unchecked")
	public void remove(long instanceId,String key,PropTypeEnum typeEnum){
		String intanceIdkey = String.valueOf(instanceId);
		//map key : 属性key + 属性type  map value: 属性值
		Map<String,InstanceProp> map = cache.get(intanceIdkey);
		if(map != null){
			String mapKey  = key + typeEnum.toString();
			map.remove(mapKey);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void update(long instanceId,PropTypeEnum typeEnum,InstanceProp prop){
		Map<String,InstanceProp> map = cache.get(String.valueOf(instanceId));
		if(map != null){
			String key = prop.getKey() + typeEnum.toString();
			InstanceProp cacheProp = map.put(key, prop);
			if(cacheProp != null){
				cacheProp.setValues(prop.getValues());
				map.put(key, cacheProp);
				cache.set(String.valueOf(instanceId),map);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public InstanceProp get(long instanceId,String key,PropTypeEnum typeEnum){
		String intanceIdkey = String.valueOf(instanceId);
		//map key : 属性key + 属性type  map value: 属性值
		Map<String,InstanceProp> map = cache.get(intanceIdkey);
		if(map == null){
			return null;
		}
		String mapKey  = key + typeEnum.toString();
		return map.get(mapKey);
	}
}
