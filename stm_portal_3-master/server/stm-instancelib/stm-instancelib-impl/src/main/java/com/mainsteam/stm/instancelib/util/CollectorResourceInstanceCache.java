package com.mainsteam.stm.instancelib.util;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;

/**
 * 采集器缓存实现(没有数据库，直接操作缓存)
 * 
 * @author xiaoruqiang
 */
public class CollectorResourceInstanceCache {

	private IMemcache<ResourceInstance> cache;

	private CollectorPropCache propCache;
	
	public CollectorPropCache getPropCache() {
		return propCache;
	}

	public void setPropCache(CollectorPropCache propCache) {
		this.propCache = propCache;
	}

	// 存放所有的父实例
	private List<ResourceInstance> parentList;

	private static String PARENT_RESOURCE_KEY = "parent_resource";

	public CollectorResourceInstanceCache() {
		cache = MemCacheFactory.getLocalMemCache(ResourceInstance.class);
		if(cache == null){
			throw new InstancelibRuntimeException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_CACHE_ERROR, "memcache not load!");
		}
	    parentList = new ArrayList<>(500);
	    cache.setCollection(PARENT_RESOURCE_KEY,parentList);
	}

	/**
	 * 添加到缓存
	 * 
	 * @param resourceInstanceId
	 *            资源实例ID
	 * @param value
	 *            资源实例值
	 */
	public void add(long resourceInstanceId, ResourceInstance value) {
		cache.set(String.valueOf(resourceInstanceId), value);
		if (value.getParentId() == 0) {
			//父
			parentList.add(value);
		}
		List<DiscoverProp> discoverProps = value.getDiscoverProps();
		if (discoverProps != null) {
			for (DiscoverProp discoverProp : discoverProps) {
				discoverProp.setInstanceId(resourceInstanceId);
				propCache.add(discoverProp.getInstanceId(), PropTypeEnum.DISCOVER, discoverProp);
			}
		}
		List<ModuleProp> moduleProps = value.getModuleProps();
		if (moduleProps != null) {
			for (ModuleProp moduleProp : moduleProps) {
				moduleProp.setInstanceId(resourceInstanceId);
				propCache.add(moduleProp.getInstanceId(), PropTypeEnum.MODULE, moduleProp);
			}
		}
	}

	/**
	 * 移除资源实例
	 * 
	 * @param resourceInstanceId
	 *            资源实例ID
	 */
	public void remove(long resourceInstanceId) {
		String key = String.valueOf(resourceInstanceId);
		ResourceInstance resourceInstance = cache.get(key);
		if(resourceInstance != null){
			List<ResourceInstance> chirdren = resourceInstance.getChildren();
			if(chirdren != null && !chirdren.isEmpty()){
				for (ResourceInstance child : chirdren) {
					String childKey = String.valueOf(child.getId());
					cache.delete(childKey);
					propCache.remove(child.getId());
				}
			}
			if(resourceInstance.getParentId() == 0){
				parentList.remove(resourceInstance);
			}
			cache.delete(key);
			propCache.remove(resourceInstanceId);
		}
	}

	/**
	 * 通过资源实例ID获取资源实例
	 * 
	 * @param resourceInstanceId
	 *            资源Id
	 * @return 资源实例
	 */
	public ResourceInstance get(long resourceInstanceId) {
		return cache.get(String.valueOf(resourceInstanceId));
	}

	/**
	 * 所有的父资源实例
	 * 
	 * @return 资源实例
	 */
	public List<ResourceInstance> getParentInstances() {
		return (List<ResourceInstance>) cache.getCollection(PARENT_RESOURCE_KEY);
	}


	public void updateModuleProp(ModuleProp prop) {
		InstanceProp instanceProp = propCache.get(prop.getInstanceId(), prop.getKey(), PropTypeEnum.MODULE);
		if (instanceProp != null) {
			ModuleProp oldProp = (ModuleProp)instanceProp;
			oldProp.setValues(prop.getValues());
		}
	}
	
	public void updateDiscoveryProp(DiscoverProp prop) {
		InstanceProp instanceProp = propCache.get(prop.getInstanceId(), prop.getKey(), PropTypeEnum.DISCOVER);
		if (instanceProp != null) {
			DiscoverProp oldProp = (DiscoverProp)instanceProp;
			oldProp.setValues(prop.getValues());
		}
	}
}
