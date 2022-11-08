package com.mainsteam.stm.cache.daemon;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.danga.MemCached.MemCachedClient;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月29日 下午3:17:52
 * @version 1.0
 */
public class MemcachedDaemon implements Runnable {

	private static final Log logger = LogFactory.getLog(MemcachedDaemon.class);

	private final MemCachedClient client;

	private volatile boolean isActivated;

	/**
	 * 检查memcached进程是否已经停止的时间间隔
	 */
	private final static long CHECK_DOWN_INTERVAL = 60000;
	
	/**
	 * 检查memcached进程是否已经可用的时间间隔
	 */
	private final static long CHECK_ACTIVATE_INTERVAL = 100;

	public MemcachedDaemon(MemCachedClient client) {
		this.client = client;
		this.isActivated = true;
		testActivated0();
		new Thread(this, "MemcachedDaemon").start();
	}

	public boolean isActivated() {
		return isActivated;
	}
	
	public Map<String, Map<String, String>> stats(){
		return client.stats();
	}

	public void testActivated() {
		this.notify();
	}

	private void testActivated0() {
		Map<String, Map<String, String>> stats = null;
		try {
			stats = client.stats();
		} catch (Exception e) {
		}
		if (stats != null && stats.size() > 0) {
			isActivated = true;
			if (logger.isDebugEnabled()) {
				logger.debug("stats =" + stats);
			}
		} else {
			isActivated = false;
			if (logger.isErrorEnabled()) {
				logger.error("testActivated memcached is not activated now.");
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			if (isActivated) {
				synchronized (this) {
					try {
						this.wait(CHECK_DOWN_INTERVAL);
					} catch (InterruptedException e) {
					}
				}
			} else {
				synchronized (this) {
					try {
						this.wait(CHECK_ACTIVATE_INTERVAL);
					} catch (InterruptedException e) {
					}
				}
			}
			testActivated0();
		}
	}
}
