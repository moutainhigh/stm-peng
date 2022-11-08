/**
 * 
 */
package com.mainsteam.stm.bootstrap;

/**
 * @author ziw
 * 
 */
public class DefaultBootStrapEvn implements BootStrapEvn {

	/**
	 * 
	 */
	public DefaultBootStrapEvn() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.bootstrap.BootStrapEvn#getEnv(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getEnv(String key, String defaultValue) {
		String value = System.getenv(key);
		if (value == null) {
			value = System.getProperty(key);
		}
		return value == null ? defaultValue : value;
	}

	@Override
	public String getEnv(String key) {
		String value = System.getenv(key);
		if (value == null) {
			value = System.getProperty(key);
		}
		return value;
	}
}
