package com.mainsteam.stm.cache;

import java.io.File;
import java.net.URL;

import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.exceptions.XmlConfigurationException;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月30日 上午11:43:51
 * @version 1.0
 */
public class ECacheManagerFactory {

	private static final String CONFILE = "/config" + File.separator
			+ "ehcache-config.xml";

	public ECacheManagerFactory() {
	}

	public static CacheManager createCacheManager() {
		CacheManager myCacheManager = null;
		URL cacheFilePath = ECacheManagerFactory.class.getResource(CONFILE);
		if (cacheFilePath != null) {
			Configuration xmlConfig;
			try {
				xmlConfig = new XmlConfiguration(cacheFilePath);
			} catch (XmlConfigurationException e) {
				throw new RuntimeException(e);
			}
			myCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
			myCacheManager.init();
		} else {
			myCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
					.build(true);
		}
		return myCacheManager;
	}
}
