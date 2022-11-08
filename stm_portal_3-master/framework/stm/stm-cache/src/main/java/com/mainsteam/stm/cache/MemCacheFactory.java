package com.mainsteam.stm.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.ValueSupplier;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expiry;

import com.mainsteam.stm.cache.caches.LocalMemCache;
import com.mainsteam.stm.cache.caches.RemoteMemCache;
import com.mainsteam.stm.cache.caches.RemoteMemCacheNoMixLocalCache;

/**
 * 缓存工厂 <li>文件名称: MemCacheFactory</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 缓存工厂类，用来实例化缓存对象</li> <li>
 * 其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月1日
 * @author wangxinghao
 */
@SuppressWarnings("unchecked")
public class MemCacheFactory {

	private static Logger logger = Logger.getLogger(MemCacheFactory.class);

	@SuppressWarnings("rawtypes")
	private static final Map<String, IMemcache> map = new ConcurrentHashMap<String, IMemcache>();

	private static final CacheManager cacheManager;
	static {
		cacheManager = ECacheManagerFactory.createCacheManager();
	}

	/**
	 * 获取本地缓存对象
	 * 
	 * @param t
	 * @return
	 */
	public static <T> IMemcache<T> getLocalMemCache(Class<T> t) {
		String cacheName = Strategy.LOCAL + "-" + t.getName();
		IMemcache<T> cache = map.get(cacheName);
		if (cache == null) {
			cache = createLocalMemCache(t, cacheName);
		}
		return cache;
	}

	/**
	 * 获取远程缓存对象
	 * 
	 * @param t
	 * @return
	 */
	public static <T> IMemcache<T> getRemoteMemCache(Class<T> t) {
		String cacheName = Strategy.REMOTE + "-" + t.getName();
		IMemcache<T> cache = map.get(cacheName);
		if (cache == null || !cache.isActivate()) {
			cache = createRemoteMemCache(t, cacheName,true);
		}
		return cache;
	}
	
	public static <T> IMemcache<T> getRemoteMemCacheRealtime(Class<T> t) {
		String cacheName = Strategy.REMOTE + "-" + t.getName();
		IMemcache<T> cache = map.get(cacheName);
		if (cache == null || !cache.isActivate()) {
			cache = createRemoteMemCache(t, cacheName,false);
		}
		return cache;
	}

	private static synchronized <T> IMemcache<T> createLocalMemCache(
			Class<T> t, String cacheName) {
		LocalMemCache<T> cache = new LocalMemCache<T>(t);
		map.put(cacheName, cache);
		if (logger.isInfoEnabled()) {
			logger.info("Create Local MemCache :" + cacheName);
		}
		return cache;
	}

	private static synchronized <T> IMemcache<T> createRemoteMemCache(
			Class<T> t, String cacheName, boolean useLocalCache) {
		IMemcache<T> cache = new RemoteMemCache<T>(t, getOrCreateCache(
				cacheName, t));
		if (useLocalCache) {
			cache = new RemoteMemCache<T>(t, getOrCreateCache(cacheName, t));
		} else {
			cache = new RemoteMemCacheNoMixLocalCache<T>(t);
		}
		map.put(cacheName, cache);
		if (logger.isInfoEnabled()) {
			logger.info("Create Remote MemCache :" + cacheName);
		}
		return cache;
	}

	private static <T> Cache<String, T> getOrCreateCache(String name, Class<T> t) {
		Cache<String, T> c = cacheManager.getCache(name, String.class, t);
		if (c != null) {
			return c;
		}
		CacheConfigurationBuilder<String, T> builder = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, t,
						ResourcePoolsBuilder.heap(5000));
		/**
		 * 首先，build只能设置一个Expiry。然后，设置的方法，不能单独来调用。必须放到上面附加。<br>
		 * 不能builder.with...，必须是builder=builder.with...
		 */
		builder = builder.withExpiry(new CustomExpiry(Duration.of(3L,
				TimeUnit.SECONDS), null));
		// builder =
		// builder.withExpiry(Expirations.timeToIdleExpiration(Duration.of(1L,
		// TimeUnit.SECONDS)));
		// builder =
		// builder.withExpiry(Expirations.timeToLiveExpiration(Duration.of(3,
		// TimeUnit.SECONDS)));
		c = cacheManager.createCache(name, builder);
		return c;
	}

	private static class CustomExpiry implements Expiry<Object, Object> {
		private final Duration create;
		private final Duration access;

		public CustomExpiry(final Duration create, final Duration access) {
			this.create = create;
			this.access = access;
		}

		public Duration getExpiryForCreation(Object key, Object value) {
			return this.create;
		}

		public Duration getExpiryForAccess(Object key, ValueSupplier<?> value) {
			return this.access;
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if ((o == null) || (getClass() != o.getClass()))
				return false;

			CustomExpiry that = (CustomExpiry) o;

			if (this.access != null ? !this.access.equals(that.access)
					: that.access != null)
				return false;
			if (this.create != null ? !this.create.equals(that.create)
					: that.create != null)
				return false;
			return true;
		}

		public int hashCode() {
			int result = this.create != null ? this.create.hashCode() : 0;
			result = 31 * result
					+ (this.access != null ? this.access.hashCode() : 0);
			return result;
		}

		public String toString() {
			return getClass().getSimpleName() + "{" + "create=" + this.create
					+ ", access=" + this.access + ", update=" + this.create;
		}

		public Duration getExpiryForUpdate(Object key,
				ValueSupplier<?> oldValue, Object newValue) {
			return create;
		}
	}

	/**
	 * 验证缓存服务器是否正常
	 * 
	 * @return
	 */
	public static boolean isActivate() {
		return MemcacheManager.getMemcachedDaemon() == null ? false
				: MemcacheManager.getMemcachedDaemon().isActivated();
	}

}
