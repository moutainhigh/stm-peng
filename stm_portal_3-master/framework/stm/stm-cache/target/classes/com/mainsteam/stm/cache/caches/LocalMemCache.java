package com.mainsteam.stm.cache.caches;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.util.Util;

/**
 * 
 * <li>文件名称: LocalMemCache.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 本地缓存策略</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月30日
 * @author wangxinghao
 */
public class LocalMemCache<T> implements IMemcache<T> {
	private String base = null;
	private static final long SECOND_TIME = 1000;
	private final Timer timer;
	private ConcurrentMap<String, T> cache;
	private ConcurrentMap<String, Collection<T>> cacheCollection;

	public LocalMemCache(Class<T> t) {
		base = getClass().getSimpleName() + "-" + t.getSimpleName() + "-";
		cache = new ConcurrentHashMap<String, T>();
		cacheCollection = new ConcurrentHashMap<String, Collection<T>>();
		this.timer = new Timer();
	}

	public String getBase() {
		return base;
	}

	@Override
	public T get(String key) {
		T v;
		v = cache.get(key);
		return v;
	}

	@Override
	public Collection<T> getCollection(String key) {
		Collection<T> collection;
		collection = cacheCollection.get(key);
		return collection;
	}

	@Override
	public boolean set(String key, T value) {
		boolean isSaved = false;
		if (Util.isEmpty(key) || Util.isEmpty(value)) {
			return isSaved;
		}
		cache.put(key, value);
		return true;
	}

	@Override
	public boolean set(String key, T value, long expiry) {
		if (Util.isEmpty(key) || Util.isEmpty(value)) {
			return false;
		}
		cache.put(key, value);
		timer.schedule(new TimeoutTimerTask(key), expiry * SECOND_TIME);
		return true;
	}

	@Override
	public boolean setCollection(String key, Collection<T> collections) {
		if (Util.isEmpty(key) || Util.isEmpty(collections)) {
			return false;
		}
		cacheCollection.put(key, collections);
		return true;
	}

	@Override
	public boolean setCollection(String key, Collection<T> collections,
			long expiry) {
		if (Util.isEmpty(key) || Util.isEmpty(collections)) {
			return false;
		}
		cacheCollection.put(key, collections);
		timer.schedule(new TimeoutTimerTask(key), expiry * SECOND_TIME);
		return true;
	}

	@Override
	public boolean update(String key, T value) {
		boolean isUpdate = false;
		if (Util.isEmpty(key) || Util.isEmpty(value)) {
			return isUpdate;
		}
		cache.replace(key, value);
		return isUpdate;
	}

	@Override
	public boolean update(String key, T value, long expiry) {
		boolean isUpdate = false;
		if (Util.isEmpty(key) || Util.isEmpty(value)) {
			return isUpdate;
		}
		cache.replace(key, value);
		timer.schedule(new TimeoutTimerTask(key), expiry * SECOND_TIME);
		return isUpdate;
	}

	@Override
	public boolean updateCollection(String key, Collection<T> collections) {
		boolean isUpdate = false;
		if (Util.isEmpty(key) || Util.isEmpty(collections)) {
			return isUpdate;
		}
		cacheCollection.replace(key, collections);
		return isUpdate;
	}

	@Override
	public boolean updateCollection(String key, Collection<T> collections,
			long expiry) {
		boolean isUpdate = false;
		if (Util.isEmpty(key) || Util.isEmpty(collections)) {
			return isUpdate;
		}
		cacheCollection.replace(key, collections);
		timer.schedule(new TimeoutTimerTask(key), expiry * SECOND_TIME);
		return isUpdate;
	}

	@Override
	public boolean delete(String key) {
		boolean isDelete = true;
		cache.remove(key);
		if (cache.get(key) != null) {
			isDelete = false;
		}
		return isDelete;
	}

	@Override
	public boolean load(String key, T value) {
		boolean load = false;
		if (cache.containsKey(key)) {
			return load;
		} else {
			set(key, value);
			load = true;
		}
		return load;
	}

	@Override
	public boolean load(String key, T value, long expiry) {
		boolean load = false;
		if (cache.containsKey(key)) {
			return load;
		} else {
			set(key, value);
			timer.schedule(new TimeoutTimerTask(key), expiry * SECOND_TIME);
			load = true;
		}
		return load;
	}

	/**
	 * 清除超时缓存定时服务类
	 */
	class TimeoutTimerTask extends TimerTask {
		private String ceKey;

		public TimeoutTimerTask(String key) {
			this.ceKey = key;
		}

		@Override
		public void run() {
			delete(ceKey);
		}
	}

	@Override
	public boolean isActivate() {
		return true;
	}

	@Override
	public long getGetCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGetmissCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getUpdateCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getUpdateMissCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDeleteCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDeleteMissCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return this.base;
	}

	@Override
	public boolean add(String key, T value) {
		return set(key, value);
	}

}
