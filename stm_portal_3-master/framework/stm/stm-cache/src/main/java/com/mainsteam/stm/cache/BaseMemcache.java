package com.mainsteam.stm.cache;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.cache.daemon.MemcachedDaemon;
import com.mainsteam.stm.util.Util;
import com.danga.MemCached.MemCachedClient;

/**
 * <li>文件名称: BaseMemcache.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月19日
 * @author wangxinghao
 * @tags @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class BaseMemcache<T> implements IMemcache<T> {
	private static final long SECOND_TIME = 1000;

	private static final Log logger = LogFactory.getLog(BaseMemcache.class);

	private final static MemCachedClient memCachedClient;
	private final static MemcachedDaemon memcachedDaemon;

	private final transient AtomicLong get_counter;
	private final transient AtomicLong getmiss_counter;
	private final transient AtomicLong update_counter;
	private final transient AtomicLong updatemiss_counter;
	private final transient AtomicLong delete_counter;
	private final transient AtomicLong deletemiss_counter;
	static {
		memCachedClient = MemcacheManager.initMemCachedClient();
		memcachedDaemon = MemcacheManager.getMemcachedDaemon();
	}

	public BaseMemcache() {
		super();
		this.get_counter = new AtomicLong(1L);// 初始值为1，防止计算命中率时出现分母为0的情况。
		this.getmiss_counter = new AtomicLong(0L);
		this.update_counter = new AtomicLong(1L);
		this.updatemiss_counter = new AtomicLong(0L);
		this.delete_counter = new AtomicLong(1L);
		this.deletemiss_counter = new AtomicLong(0L);
	}
	@Override
	public boolean add(String key, T value) {
		boolean isSaved = false;
		try {
			isSaved = memCachedClient.add(key, value);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache Set Error:" + e.getMessage(),e);
			return false;
		}
		if (!isSaved) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isSaved;
	}
	
	@Override
	public boolean set(String key, T value) {
		boolean isSaved = false;
		try {
			isSaved = memCachedClient.set(key, value);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache Set Error:" + e.getMessage(),e);
			return false;
		}
		if (!isSaved) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isSaved;
	}

	@Override
	public boolean set(String key, T value, long expiry) {
		boolean isSaved = false;
		try {
			isSaved = memCachedClient.set(key, value, new Date(expiry
					* SECOND_TIME));
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache Set Error:" + e.getMessage(),e);
			return false;
		}
		if (!isSaved) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isSaved;
	}

	@Override
	public boolean setCollection(String key, Collection<T> collections) {
		boolean isSaved = false;
		try {
			isSaved = memCachedClient.set(key, collections);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache setCollection Error:" + e.getMessage(),e);
			return false;
		}
		if (!isSaved) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isSaved;
	}

	@Override
	public boolean setCollection(String key, Collection<T> collections,
			long expiry) {
		boolean isSaved = false;
		try {
			isSaved = memCachedClient.set(key, collections);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache setCollection Error:" + e.getMessage(),e);
			return false;
		}
		if (!isSaved) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isSaved;
	}

	@Override
	public boolean update(String key, T value) {
		boolean isUpdate = false;
		try {
			isUpdate = memCachedClient.replace(key, value);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache update Error:" + e.getMessage(),e);
			return false;
		}
		if (!isUpdate) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isUpdate;
	}

	@Override
	public boolean update(String key, T value, long expiry) {
		boolean isUpdate = false;
		try {
			isUpdate = memCachedClient.replace(key, value, new Date(expiry
					* SECOND_TIME));
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache update Error:" + e.getMessage(),e);
			return false;
		}
		if (!isUpdate) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isUpdate;
	}

	/**
	 * 更新对象集合到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 */
	@Override
	public boolean updateCollection(String key, Collection<T> collections) {
		boolean isUpdate = false;
		try {
			isUpdate = memCachedClient.replace(key, collections);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache updateCollection Error:" + e.getMessage(),e);
			return false;
		}
		if (!isUpdate) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isUpdate;
	}

	/**
	 * 更新对象集合到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 * @param expiry
	 *            过期时间，单位 (秒)
	 */
	@Override
	public boolean updateCollection(String key, Collection<T> collections,
			long expiry) {
		boolean isUpdate = false;
		try {
			isUpdate = memCachedClient.replace(key, collections, new Date(
					expiry * SECOND_TIME));
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache updateCollection Error:" + e.getMessage(),e);
			return false;
		}
		if (!isUpdate) {
			updatemiss_counter.incrementAndGet();
		}
		update_counter.incrementAndGet();
		return isUpdate;
	}

	@Override
	public T get(String key) {
		T value = null;
		try {
			value = (T) memCachedClient.get(key);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache get Error:" + e.getMessage(),e);
			return null;
		}
		if (value == null) {
			getmiss_counter.incrementAndGet();
		}
		get_counter.incrementAndGet();
		return value;
	}

	@Override
	public Collection<T> getCollection(String key) {
		Collection<T> collection = null;
		try {
			collection = (Collection<T>) memCachedClient.get(key);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache getCollection Error:" + e.getMessage(),e);
			return null;
		}
		if (collection == null) {
			getmiss_counter.incrementAndGet();
		}
		get_counter.incrementAndGet();
		return collection;
	}

	@Override
	public boolean delete(String key) {
		boolean isDelete = false;
		try {
			isDelete = memCachedClient.delete(key);
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache delete Error:" + e.getMessage(),e);
			return false;
		}
		if (!isDelete) {
			deletemiss_counter.incrementAndGet();
		}
		delete_counter.incrementAndGet();
		return isDelete;
	}

	@Override
	public boolean load(String key, T value) {
		boolean load = false;
		try {
			if (Util.isEmpty(get(key))) {
				set(key, value);
				load = true;
			}
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache load Error:" + e.getMessage(),e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("MemCached load:" + key);
		}
		if (!load) {
			logger.error("MemCached load Fail:" + key);
		}
		return load;
	}

	@Override
	public boolean load(String key, T value, long expiry) {
		boolean load = false;
		try {
			if (Util.isEmpty(get(key))) {
				set(key, value, expiry);
				load = true;
			}
		} catch (Exception e) {
			memcachedDaemon.testActivated();
			logger.error("Memcache load Error:" + e.getMessage(),e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("MemCached load:" + key);
		}
		if (!load) {
			logger.error("MemCached load Fail:" + key);
		}
		return load;
	}

	@Override
	public boolean isActivate() {
		return memcachedDaemon.isActivated();
	}

	@Override
	public long getGetCount() {
		return this.get_counter.get();
	}

	@Override
	public long getGetmissCount() {
		return this.getmiss_counter.get();
	}

	@Override
	public long getUpdateCount() {
		return this.update_counter.get();
	}

	@Override
	public long getUpdateMissCount() {
		return this.updatemiss_counter.get();
	}

	@Override
	public long getDeleteCount() {
		return this.delete_counter.get();
	}

	@Override
	public long getDeleteMissCount() {
		return this.deletemiss_counter.get();
	}
}
