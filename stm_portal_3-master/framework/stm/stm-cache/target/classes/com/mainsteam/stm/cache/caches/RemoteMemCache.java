package com.mainsteam.stm.cache.caches;

import java.util.Collection;

import org.ehcache.Cache;

import com.mainsteam.stm.cache.BaseMemcache;

public class RemoteMemCache<T> extends BaseMemcache<T> {
	private String base = null;
	private Cache<String, T> fastCache;

	public RemoteMemCache(Class<T> t, Cache<String, T> fastCache) {
		super();
		base = getClass().getSimpleName() + "-" + t.getSimpleName() + "-";
		this.fastCache = fastCache;
	}

	@Override
	public String getName() {
		return this.base;
	}

	@Override
	public T get(String key) {
		key = base + key;
		T v = fastCache.get(key);
		if (v == null) {
			v = super.get(key);
			if (v != null) {
				fastCache.put(key, v);
			}
		}
		return v;
	}

	@Override
	public boolean delete(String key) {
		key = base + key;
		fastCache.remove(key);
		return super.delete(key);
	}

	@Override
	public boolean set(String key, T value) {
		key = base + key;
		fastCache.put(key, value);
		return super.set(key, value);
	}

	@Override
	public boolean set(String key, T value, long expiry) {
		key = base + key;
		fastCache.put(key, value);
		return super.set(key, value, expiry);
	}

	@Override
	public boolean update(String key, T value) {
		key = base + key;
		fastCache.replace(key, value);
		return super.update(key, value);
	}

	@Override
	public boolean update(String key, T value, long expiry) {
		key = base + key;
		fastCache.replace(key, value);
		return super.update(key, value, expiry);
	}

	@Override
	public boolean load(String key, T value) {
		key = base + key;
		return super.load(key, value);
	}

	@Override
	public boolean load(String key, T value, long expiry) {
		key = base + key;
		return super.load(key, value, expiry);
	}

	@Override
	public boolean setCollection(String key, Collection<T> collections) {
		return super.setCollection(key, collections);
	}

	@Override
	public boolean setCollection(String key, Collection<T> collections,
			long expiry) {
		return super.setCollection(key, collections, expiry);
	}

	@Override
	public boolean updateCollection(String key, Collection<T> collections) {
		return super.updateCollection(key, collections);
	}

	@Override
	public boolean updateCollection(String key, Collection<T> collections,
			long expiry) {
		return super.updateCollection(key, collections, expiry);
	}

	@Override
	public Collection<T> getCollection(String key) {
		return super.getCollection(key);
	}

	@Override
	public boolean add(String key, T value) {
		if (super.add(key, value)) {
			this.fastCache.put(key, value);
			return true;
		} else {
			return false;
		}
	}
}
