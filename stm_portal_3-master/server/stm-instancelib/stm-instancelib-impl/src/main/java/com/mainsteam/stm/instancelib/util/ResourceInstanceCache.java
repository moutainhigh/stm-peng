package com.mainsteam.stm.instancelib.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.instancelib.bean.ResourceInstanceCacheKey;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

/**
 * 处理器缓存实现
 * @author xiaoruqiang
 *
 */
public class ResourceInstanceCache {

	
	private static final Log logger = LogFactory.getLog(ResourceInstanceCache.class);
	
	//存放资源实例
    private IMemcache<ResourceInstance> cache;
   
    private static int PARENT_RESOURCE_SIZE = 2000;
    
    private static String PARENT_RESOURCE = "parent_resource";
	
    //用于存放资源实例ID
    private IMemcache<ResourceInstanceCacheKey> keyCache;
	
	public ResourceInstanceCache(){
		cache = MemCacheFactory.getRemoteMemCache(ResourceInstance.class);
		keyCache = MemCacheFactory.getRemoteMemCache(ResourceInstanceCacheKey.class);
		if(cache == null || keyCache == null){
			if(logger.isWarnEnabled()){
				logger.warn("memcache not load!");
			}
		}
	}
	
	public boolean getResourceInstanceCacheActivate(){
		return cache.isActivate();
	}
	
//	/**
//	 * 系统启动的时候加载实例
//	 * @param resourceInstanceId
//	 * @param value
//	 */
//	public void load(long resourceInstanceId , ResourceInstance value){
//		if(cache != null){
//			cache.set(String.valueOf(resourceInstanceId), value);
//			/*
//			 * 够造所有的父实例已经父实例跟子实例的列表，存放到缓存,包含已删除的资源实例
//			 * （连线已经通过sql过滤）
//			 */
//			if(value.getParentId() == 0){
//				//存放所有父实例
//				ResourceInstanceCacheKey resourceInstanceCacheKey = keyCache.get(PARENT_RESOURCE);
//				if(resourceInstanceCacheKey != null){
//					resourceInstanceCacheKey.getInstanceIds().add(resourceInstanceId);
//				}else{
//					resourceInstanceCacheKey = new ResourceInstanceCacheKey(new HashSet<Long>(PARENT_RESOURCE_SIZE));
//					resourceInstanceCacheKey.getInstanceIds().add(resourceInstanceId);
//				}
//				keyCache.set(PARENT_RESOURCE, resourceInstanceCacheKey);
//			} else {
//				//存放父实例下的子实例
//				String key = String.valueOf(value.getParentId());
//				ResourceInstanceCacheKey childResourceInstanceCacheKey = keyCache.get(key);
//				if(childResourceInstanceCacheKey != null){
//					childResourceInstanceCacheKey.getInstanceIds().add(resourceInstanceId);
//				}else{
//					childResourceInstanceCacheKey = new ResourceInstanceCacheKey(new HashSet<Long>(150));
//					childResourceInstanceCacheKey.getInstanceIds().add(resourceInstanceId);
//				}
//				keyCache.set(key, childResourceInstanceCacheKey);
//			}
//		}
//	}
	
	/**
	 * 系统启动的时候加载实例
	 * @param resourceInstanceId
	 * @param value
	 */
	public void load(List<ResourceInstance> value,HashMap<Long,HashSet<Long>> relations){
		if(cache != null && cache.isActivate()){
			Map<String, HashSet<Long>> categoryInstances = new HashMap<String, HashSet<Long>>();
			//存放所有实例
			for (ResourceInstance resourceInstance : value) {
				cache.set(String.valueOf(resourceInstance.getId()), resourceInstance);
				if(resourceInstance.getParentId()<=0){
					//将资源区分类型，放入不同的缓存中
					if(resourceInstance.getParentCategoryId()!=null && !resourceInstance.getParentCategoryId().isEmpty()){
						String categoryId = resourceInstance.getParentCategoryId();
						if(categoryInstances.containsKey(categoryId)){
							categoryInstances.get(categoryId).add(resourceInstance.getId());
						}else{
							categoryInstances.put(categoryId, new HashSet<Long>());
							categoryInstances.get(categoryId).add(resourceInstance.getId());
						}
					}
				}
			}
			
			if(categoryInstances!=null && categoryInstances.size()>0){
				for (Map.Entry<String, HashSet<Long>> ci : categoryInstances.entrySet()) {
					ResourceInstanceCacheKey instanceCategoryCacheKey = keyCache.get(ci.getKey());
					if(instanceCategoryCacheKey==null){
						instanceCategoryCacheKey = new ResourceInstanceCacheKey(ci.getValue());
					}else{
						HashSet<Long> instanceIdSet = instanceCategoryCacheKey.getInstanceIds();
						instanceIdSet.addAll(ci.getValue());
						instanceCategoryCacheKey.setInstanceIds(instanceIdSet);
					}
					keyCache.set(ci.getKey(), instanceCategoryCacheKey);
					//System.out.println(keyCache.get(ci.getKey()).getInstanceIds().size());
				}
			}
			
			//存放所有父实例
			ResourceInstanceCacheKey resourceInstanceCacheKey = new ResourceInstanceCacheKey(new HashSet<Long>(PARENT_RESOURCE_SIZE));
			for (Entry<Long,HashSet<Long>> item : relations.entrySet()) {
				//所有父
				long parentId = item.getKey();
				resourceInstanceCacheKey.getInstanceIds().add(parentId);
				if(item.getValue() != null && !item.getValue().isEmpty()){
					String childKey = String.valueOf(parentId);
					ResourceInstanceCacheKey childResourceInstanceCacheKey =  new ResourceInstanceCacheKey(item.getValue());
					//存放父实例-子实例对应关系
					keyCache.set(childKey, childResourceInstanceCacheKey);
				}
			}
			//所有父存放到缓存
			if(!resourceInstanceCacheKey.getInstanceIds().isEmpty()){
				keyCache.set(PARENT_RESOURCE, resourceInstanceCacheKey);
			}
		}
	}
	
	/**
	 * 存放父实例对应的子实例列表，如果不存在父实例，不做任何操作
	 * @param parentId 父实例列表
	 * @param children 子实例列表
	 */
	public void addChildInstanceList(long parentId,HashSet<Long> children){
		if(keyCache != null ){
			String key = String.valueOf(parentId);
			ResourceInstanceCacheKey parentResourceInstanceCacheKey = keyCache.get(key);
			if(parentResourceInstanceCacheKey != null){
				synchronized (parentResourceInstanceCacheKey) {
					HashSet<Long> tempInstanceIds = new HashSet<>(parentResourceInstanceCacheKey.getInstanceIds());
					tempInstanceIds.addAll(children);
					parentResourceInstanceCacheKey.setInstanceIds(tempInstanceIds);
					keyCache.set(key, parentResourceInstanceCacheKey);
				}
			}
		}
	}
	
	/**
	 * 存放所有的父实例列表，如果列表不存在父实例，不做任何操作
	 * @param parentId 需要添加的父实例
	 */
	public void addParentInsatnceList(long parentId){
		if(keyCache != null ){
			//存放所有父实例
			ResourceInstanceCacheKey childResourceInstanceCacheKey = keyCache.get(PARENT_RESOURCE);
			if(childResourceInstanceCacheKey != null){
				synchronized (childResourceInstanceCacheKey) {
					HashSet<Long> tempInstanceIds = new HashSet<>(childResourceInstanceCacheKey.getInstanceIds());
					tempInstanceIds.add(parentId);
					childResourceInstanceCacheKey.setInstanceIds(tempInstanceIds);
					keyCache.set(PARENT_RESOURCE, childResourceInstanceCacheKey);
				}
			}
		}
	}
	
	/**
	 * 只做单一的资源实例添加到缓存
	 * @param resourceInstanceId 资源实例Id
	 * @param value 资源实例值
	 */
	public void queryAdd(long resourceInstanceId , ResourceInstance value){
		cache.set(String.valueOf(resourceInstanceId), value); 
		if(value!=null && value.getParentId()<=0){
			String categoryId = value.getParentCategoryId();
			//将资源区分类型，放入不同的缓存中
			if(categoryId!=null && !categoryId.isEmpty()){
				ResourceInstanceCacheKey instanceCategoryCacheKey = keyCache.get(categoryId);
				if(instanceCategoryCacheKey==null){
					HashSet<Long> parentIds=new HashSet<Long>();
					parentIds.add(resourceInstanceId);
					instanceCategoryCacheKey = new ResourceInstanceCacheKey(parentIds);
					keyCache.set(categoryId, instanceCategoryCacheKey);
				}else{
					synchronized (instanceCategoryCacheKey) {
						HashSet<Long> instanceIdSet =  new HashSet<>(instanceCategoryCacheKey.getInstanceIds());
						instanceIdSet.add(resourceInstanceId);
						instanceCategoryCacheKey.setInstanceIds(instanceIdSet);
						keyCache.set(categoryId, instanceCategoryCacheKey);
					}
				}
			}
		}
	}
	
	
//	/**
//	 * 只做单一的资源实例添加到缓存
//	 * @param resourceInstanceId 资源实例Id
//	 * @param value 资源实例值
//	 */
//	public void queryAddList(ResourceInstance value){
//		cache.set(String.valueOf(resourceInstanceId), value); 
//		if(value!=null && value.getParentId()<=0){
//			String categoryId = value.getParentCategoryId();
//			//将资源区分类型，放入不同的缓存中
//			if(categoryId!=null && !categoryId.isEmpty()){
//				ResourceInstanceCacheKey instanceCategoryCacheKey = keyCache.get(categoryId);
//				if(instanceCategoryCacheKey==null){
//					HashSet<Long> parentIds=new HashSet<Long>();
//					parentIds.add(resourceInstanceId);
//					instanceCategoryCacheKey = new ResourceInstanceCacheKey(parentIds);
//				}else{
//					HashSet<Long> instanceIdSet = instanceCategoryCacheKey.getInstanceIds();
//					instanceIdSet.add(resourceInstanceId);
//					instanceCategoryCacheKey.setInstanceIds(instanceIdSet);
//				}
//				keyCache.set(categoryId, instanceCategoryCacheKey);
//			}
//		}
//	}
	
	
	
	/**
	 * 移除资源实例
	 * @param resourceInstanceId 资源实例ID
	 */
	public void remove(long resourceInstanceId){
		cache.delete(String.valueOf(resourceInstanceId));
	}
	
	/**
	 * 通过资源实例ID获取资源实例，没有值返回null
	 * @param resourceInstanceId 资源Id
	 * @return 资源实例
	 */
	public ResourceInstance get(long resourceInstanceId){
		return cache.get(String.valueOf(resourceInstanceId));
	}
	
	/**
	 * 获取所有的父资源实例Id列表
	 * @return 资源实例
	 */
	public HashSet<Long> getParentInstances(){
		HashSet<Long> result = null;
		//存放所有的父实例
		ResourceInstanceCacheKey instanceCacheKey = keyCache.get(PARENT_RESOURCE);
		if(instanceCacheKey != null){
			result = instanceCacheKey.getInstanceIds();
		}
		return result;
	}
	
	public HashSet<Long> getParentInstance(String categoryId){
		HashSet<Long> result = null;
		//存放所有的父实例
		ResourceInstanceCacheKey instanceCacheKey = keyCache.get(categoryId);
		if(instanceCacheKey != null){
			result = instanceCacheKey.getInstanceIds();
		}
		return result;
	}
	
	/**
	 * 获取某一个父实例对应的子资源实例Id
	 * @return 父资源实例
	 */
	public HashSet<Long> getChildInstance(long parentId){
		HashSet<Long> result = null;
		String key = String.valueOf(parentId);
		ResourceInstanceCacheKey instanceId = keyCache.get(key);
		if(instanceId != null){
			result = instanceId.getInstanceIds();
		}
		return result;
	}
	
	/**
	 * 一次性存放系统所有的父实例，之前的数据会覆盖。
	 * @param set 父实例列表
	 */
	public void setParentInstance(HashSet<Long> set){
		ResourceInstanceCacheKey instanceCacheKey = keyCache.get(PARENT_RESOURCE);
		if(instanceCacheKey != null){
			instanceCacheKey.setInstanceIds(set);
		}else{
			instanceCacheKey = new ResourceInstanceCacheKey(set);
		}
		keyCache.set(PARENT_RESOURCE,instanceCacheKey);
	}
	
	/**
	 * 一次性存放某个父实例所有的子实例，之前对应的数据会被覆盖。
	 * @param set 
	 */
	public void setChildInstance(long parentId,HashSet<Long> child){
		String key = String.valueOf(parentId);
		ResourceInstanceCacheKey instanceCacheKey = keyCache.get(key);
		if(instanceCacheKey != null){
			instanceCacheKey.setInstanceIds(child);
		}else{
			instanceCacheKey = new ResourceInstanceCacheKey(child);
		}
		keyCache.set(key,instanceCacheKey);
	}
	
	/**
	 * 更新资源实例缓存
	 * @param resourceInstanceId 资源实例Id
	 * @param value 资源实例
	 */
	public void update(long resourceInstanceId , ResourceInstance value){
		cache.set(String.valueOf(resourceInstanceId), value);
	}
	
}
