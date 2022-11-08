/**
 * 
 */
package com.mainsteam.stm.pluginserver.executor.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.util.ReentrantLock;
import com.mainsteam.stm.pluginsession.PluginResultSet;

/**
 * 
 * 用来缓存sesionkey的批次相关的数据
 * 
 * @author ziw
 * 
 */
public class PluginSessionResultCacheByBatch {

	private static final Log logger = LogFactory.getLog(PluginSessionResultCacheByBatch.class);

	private Map<String, PluginSessionBatchWrapper> batchMap;
	// private Map<String, PluginSessionBatchWrapper> switchBatchMap;

	private static final long clearExpireTimeLength = 300 * 60 * 1000;// 300
																		// minutes

	private static final long clearExpireTimeInterval = 30000;// 30 seconds

	private long lastClearTime;

	private final ThreadLocal<ReentrantLock> pluginSessionBatchLockStore;

	// private Lock batchMapWriteLock;
	private static final long lastBatchModifyTimeLength = 10 * 60 * 1000;// 10
																			// minute
	private long lastBatchModifyTime = 0;

	private volatile boolean started;

	/**
	 * 
	 */
	public PluginSessionResultCacheByBatch() {
		pluginSessionBatchLockStore = new ThreadLocal<>();
		batchMap = new ConcurrentHashMap<String, PluginSessionResultCacheByBatch.PluginSessionBatchWrapper>(10000);
	}

	private Map<String, PluginSessionBatchWrapper> clearTimeoutSessionCacheData(
			Map<String, PluginSessionBatchWrapper> switchBatchMap) {
		long current = System.currentTimeMillis();
		List<String> keys = new ArrayList<String>(batchMap.keySet());
		for (String pluginSessionExecuteIdentyfiedKey : keys) {
			PluginSessionBatchWrapper wrapper = batchMap.get(pluginSessionExecuteIdentyfiedKey);
			if (wrapper != null && wrapper.updateTime - current >= clearExpireTimeLength) {
				if (switchBatchMap == null) {
					switchBatchMap = new HashMap<>(batchMap);
				}
				try {
					switchBatchMap.remove(pluginSessionExecuteIdentyfiedKey);
				} catch (Exception e) {
				} finally {
					try {
						wrapper.lock.close();
					} catch (Exception e) {
					}
				}
				if (logger.isInfoEnabled()) {
					logger.info("done pluginSessionCacheClear to " + pluginSessionExecuteIdentyfiedKey);
				}
			}
		}
		return switchBatchMap;
	}

	public synchronized void start() {
		if (started) {
			return;
		}
		started = true;
		lastClearTime = System.currentTimeMillis();
	}

	public void prepare(PluginRequest request) {
		Map<String, PluginSessionBatchWrapper> switchBatchMap = null;
		if (!batchMap.containsKey(request.getPluginSessionExecuteIdentyfiedKey())) {
			PluginSessionBatchWrapper wrapper = new PluginSessionBatchWrapper();
			wrapper.lock = new ReentrantLock();// 锁
			if (lastBatchModifyTime > 0
					&& (System.currentTimeMillis() - lastBatchModifyTime > lastBatchModifyTimeLength)) {
				switchBatchMap = new HashMap<>(batchMap);
				switchBatchMap.put(request.getPluginSessionExecuteIdentyfiedKey(), wrapper);
			} else {
				batchMap.put(request.getPluginSessionExecuteIdentyfiedKey(), wrapper);
				lastBatchModifyTime = System.currentTimeMillis();
			}
		}
		if (System.currentTimeMillis() - lastClearTime >= clearExpireTimeInterval) {
			switchBatchMap = clearTimeoutSessionCacheData(switchBatchMap);
		}
		if (switchBatchMap != null) {
			batchMap = switchBatchMap;
			switchBatchMap = null;
			lastBatchModifyTime = System.currentTimeMillis();
		}
	}

	public void lock(PluginRequest request) {
		PluginSessionBatchWrapper wrapper = batchMap.get(request.getPluginSessionExecuteIdentyfiedKey());
		if (wrapper != null) {
			pluginSessionBatchLockStore.set(wrapper.lock);
			wrapper.lock.lock();
		} else {
			throw new RuntimeException("not found lock obj.requestId=" + request.getRequestId());
		}
	}

	public void unlock(PluginRequest request) {
		ReentrantLock lock = pluginSessionBatchLockStore.get();
		pluginSessionBatchLockStore.remove();
		if (lock != null) {
			lock.unlock();
		}
	}

	public PluginResultSet selectCachedResultSet(PluginRequest request) {
		PluginResultSet result = null;
		/**
		 * 只有监控取值才需要缓存
		 */
		if (request.getPluginRequestType() == PluginRequestEnum.monitor) {
			PluginSessionBatchWrapper wrapper = batchMap.get(request.getPluginSessionExecuteIdentyfiedKey());
			if (wrapper != null) {
				PluginSessionBatch pluginSessionBatch = wrapper.batch;
				if (pluginSessionBatch != null) {
					long expireLongTime = 0;
					if (pluginSessionBatch.batch == request.getBatch()) {
						if (pluginSessionBatch.cacheTime > 0) {
							expireLongTime = pluginSessionBatch.cacheTime;
						}
					} else {
						expireLongTime = wrapper.updateTime;
					}

					/**
					 * 如果缓存的数据超时，则移除缓存
					 */
					if (expireLongTime > 0) {
						// long current = request.getCollectTime().getTime();
						long current = System.currentTimeMillis();
						/**
						 * 最小超时时间为30s
						 */
						long cacheExpireTime = request.getCacheExpireTime() > 0 ? request.getCacheExpireTime()
								: 30000;
						long offset = 0;
						offset = current - expireLongTime;
						if (offset >= cacheExpireTime) {
							if (logger.isTraceEnabled()) {
								StringBuilder b = new StringBuilder();
								b.append(
										"selectCachedResultSet PluginResultSet cache expireTime and remove for requestId=")
										.append(request.getRequestId()).append(" instanceId=")
										.append(request.getResourceInstId()).append(" metricId=")
										.append(request.getMetricId()).append(" batch=").append(request.getBatch())
										.append(" key=").append(request.getPluginSessionExecuteIdentyfiedKey())
										.append(" expireTime=").append(offset);
								logger.trace(b.toString());
							}
							wrapper.batch = null;
						}
					}

					if (wrapper.batch != null) {
						/**
						 * 如果数据没有超时，允许运行相近的批次使用这些数据。
						 */
						result = pluginSessionBatch.set;
						if (pluginSessionBatch.batch == request.getBatch()) {
//							if (pluginSessionBatch.cacheTime != 0) {
//								/**
//								 * 如果被使用，更新其缓存时间
//								 */
//								pluginSessionBatch.cacheTime = System.currentTimeMillis();
//							}
						} else if (pluginSessionBatch.cacheTime == 0) {
							/**
							 * 缓存时间从第一个不是当前批次以后开始计算
							 */
							pluginSessionBatch.cacheTime = System.currentTimeMillis();
						}
					}
				}
			}
		}
		if (result != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"selectCachedResultSet get cache result ok." + request.getPluginSessionExecuteIdentyfiedKey());
			}
			try {
				return (PluginResultSet) result.clone();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("selectCachedResultSet", e);
				}
				return null;
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("selectCachedResultSet not found result from cache."
						+ request.getPluginSessionExecuteIdentyfiedKey());
			}
			return null;
		}
	}

	public void cacheResultSet(PluginRequest request, PluginResultSet set) {
		long current = System.currentTimeMillis();
		PluginSessionBatch batch = new PluginSessionBatch();
		batch.batch = request.getBatch();
		batch.cacheTime = 0;
		try {
			batch.set = set == null ? null : (PluginResultSet) set.clone();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("cacheResultSet", e);
			}
		}
		PluginSessionBatchWrapper wrapper = null;
		wrapper = batchMap.get(request.getPluginSessionExecuteIdentyfiedKey());
		if (wrapper != null) {
			wrapper.updateTime = current;
			wrapper.batch = batch;
		}
		// modify by ziw at 2017年9月14日 下午4:09:40 全部在prepare初始化，舍弃改部分逻辑。
		// else {
		// /**
		// * 如果plugin的maxActivity小于等于1，则需要在这里初始化wrapper。
		// *
		// * modify by fevergreen at 2017年3月30日 上午11:02:00
		// */
		// wrapper = new PluginSessionBatchWrapper();
		// wrapper.updateTime = current;
		// wrapper.batch = batch;
		// batchMap.put(request.getPluginSessionExecuteIdentyfiedKey(),
		// wrapper);
		// }
	}

	private class PluginSessionBatch {
		private PluginResultSet set;
		private long batch;
		/**
		 * cache发送的时间
		 */
		private long cacheTime;
	}

	private class PluginSessionBatchWrapper {
		private PluginSessionBatch batch;
		/**
		 * 最近更新时间
		 */
		private long updateTime;

		private ReentrantLock lock;
	}
}
