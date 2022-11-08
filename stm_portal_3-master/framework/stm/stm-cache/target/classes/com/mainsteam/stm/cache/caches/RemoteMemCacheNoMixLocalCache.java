package com.mainsteam.stm.cache.caches;

import java.util.Collection;

import com.mainsteam.stm.cache.BaseMemcache;

public class RemoteMemCacheNoMixLocalCache<T> extends BaseMemcache<T> {
	private String base = null;

	public RemoteMemCacheNoMixLocalCache(Class<T> t) {
		super();
		base = getClass().getSimpleName() + "-" + t.getSimpleName() + "-";
	}

	@Override
	public String getName() {
		return this.base;
	}

	@Override
	public T get(String key) {
		key = base + key;
		T v = super.get(key);
		return v;
	}

	@Override
	public boolean delete(String key) {
		key = base + key;
		return super.delete(key);
	}

	@Override
	public boolean set(String key, T value) {
		key = base + key;
		return super.set(key, value);
	}

	@Override
	public boolean set(String key, T value, long expiry) {
		key = base + key;
		return super.set(key, value, expiry);
	}

	@Override
	public boolean update(String key, T value) {
		key = base + key;
		return super.update(key, value);
	}

	@Override
	public boolean update(String key, T value, long expiry) {
		key = base + key;
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
}
