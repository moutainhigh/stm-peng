/**
 * 
 */
package com.mainsteam.stm.persister;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziw
 * 
 */
public class DefaultObjectFileDatabaseFactory implements
		ObjectFileDatabaseFactory {

	private Map<String, ObjectFileDatabase<?>> databasesMap;

	/**
	 * 
	 */
	public DefaultObjectFileDatabaseFactory() {
		databasesMap = new HashMap<String, ObjectFileDatabase<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.persister.ObjectFileDatabaseFactory#
	 * createObjectFileDatabase(java.lang.String)
	 */
	@Override
	public synchronized <T> ObjectFileDatabase<T> createObjectFileDatabase(
			String moduleKey) {
		return createObjectFileDatabase(moduleKey, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> ObjectFileDatabase<T> createObjectFileDatabase(
			String moduleKey, Map<String, Class<?>> alias) {
		String key = moduleKey.toLowerCase();
		if (databasesMap.containsKey(key)) {
			return (ObjectFileDatabase<T>) databasesMap.get(key);
		} else {
			DefaultObjectFileDatabase<T> d = new DefaultObjectFileDatabase<>(
					key, alias);
			databasesMap.put(key, d);
			return d;
		}
	}
}
