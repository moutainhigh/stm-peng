/**
 * 
 */
package com.mainsteam.stm.metric.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricBind;

/**
 * @author ziw
 * 
 */
public class CustomMetricCache {
	private static final Log logger = LogFactory
			.getLog(CustomMetricCache.class);

	private IMemcache<CustomMetric> memcache;
	private IMemcache<String[]> instanceBindCache;
	private IMemcache<String[]> metricBindCache;

	/**
	 * 
	 */
	public CustomMetricCache() {
	}

	public void start(List<CustomMetricBind> customMetricBinds,
			List<CustomMetric> customMetrics) {
		memcache = MemCacheFactory.getRemoteMemCache(CustomMetric.class);
		instanceBindCache = MemCacheFactory.getRemoteMemCache(String[].class);
		metricBindCache = MemCacheFactory.getRemoteMemCache(String[].class);

		if (customMetricBinds != null) {
			deleteCacheCustomMetricBinds(customMetricBinds);
			addCacheCustomMetricBinds(customMetricBinds);
		}

		if (customMetrics != null) {
			for (CustomMetric customMetric : customMetrics) {
				memcache.delete(customMetric.getCustomMetricInfo().getId());
				cacheCustomMetric(customMetric);
			}
		}

	}

	public CustomMetric getCustomMetric(String metricId) {
		return memcache.get(metricId);
	}

	public void clearCacheCustomMetricBinds(Long instanceId) {
		String key = String.valueOf(instanceId);
		String[] binds = instanceBindCache.get(key);

		if (binds != null && binds.length > 0) {
			List<String> metricIds = new ArrayList<String>();
			for (int i = 0; i < binds.length; i += 2) {
				String metricId = binds[i];
				metricIds.add(metricId);
				// metricBindCache.delete(metricId);
			}
			if (!metricIds.isEmpty()) {
				for (String metricId : metricIds) {
					String[] instanceBins = metricBindCache.get(metricId);
					int newCacheLength = instanceBins.length;
					List<String> newCaches = new ArrayList<String>();
					for (int i = 0; i < instanceBins.length; i += 2) {
						String cacheInstanId = instanceBins[i];
						String cachePlushId = instanceBins[i + 1];
						if (cacheInstanId != null && cacheInstanId.equals(key)) {
							newCacheLength -= 2;
							continue;
						}
						newCaches.add(cacheInstanId);
						newCaches.add(cachePlushId);
					}
					String[] newCacheValues = new String[newCacheLength];
					metricBindCache.set(metricId,
							newCaches.toArray(newCacheValues));
				}
			}
			instanceBindCache.delete(key);
		}
	}

	public void deleteCacheCustomMetricBinds(
			List<CustomMetricBind> customMetricBinds) {
		Map<Long, List<String>> instanceBinds = new HashMap<>();
		Map<String, List<String>> metricBinds = new HashMap<>();
		for (CustomMetricBind customMetricBind : customMetricBinds) {
			Long rid = customMetricBind.getInstanceId();
			if (instanceBinds.containsKey(rid)) {
				instanceBinds.get(rid).add(customMetricBind.getMetricId());
				instanceBinds.get(rid).add(customMetricBind.getPluginId());
			} else {
				List<String> s = new ArrayList<>();
				s.add(customMetricBind.getMetricId());
				s.add(customMetricBind.getPluginId());
				instanceBinds.put(rid, s);
			}

			String metricId = customMetricBind.getMetricId();
			if (metricBinds.containsKey(metricId)) {
				metricBinds.get(metricId).add(
						String.valueOf(customMetricBind.getInstanceId()));
				metricBinds.get(metricId).add(customMetricBind.getPluginId());
			} else {
				List<String> s = new ArrayList<>();
				s.add(String.valueOf(customMetricBind.getInstanceId()));
				s.add(customMetricBind.getPluginId());
				metricBinds.put(metricId, s);
			}
		}
		for (Entry<Long, List<String>> set : instanceBinds.entrySet()) {
			String rid = String.valueOf(set.getKey());
			List<String> binds = set.getValue();
			String[] bindCaches = instanceBindCache.get(rid);
			String[] newCaches = getNewCaches(bindCaches, binds);
			instanceBindCache.set(rid, newCaches);
		}

		for (Entry<String, List<String>> set : metricBinds.entrySet()) {
			String mid = String.valueOf(set.getKey());
			List<String> binds = set.getValue();
			String[] bindCaches = metricBindCache.get(mid);
			String[] newCaches = getNewCaches(bindCaches, binds);
			metricBindCache.set(mid, newCaches);
		}
	}

	private String[] getNewCaches(String[] bindCaches, List<String> binds) {
		List<String> bindCacheList = new ArrayList<String>();
		List<String> bindsList = new ArrayList<String>();
		if (bindCaches != null && bindCaches.length > 0) {
			for (int i = 0; i < bindCaches.length; i += 2) {
				String keyId = bindCaches[i];
				String pluginId = bindCaches[i + 1];
				bindCacheList.add(keyId + "," + pluginId);
			}
			for (int i = 0; i < binds.size(); i += 2) {
				String bKeyId = binds.get(i);
				String bPluginId = binds.get(i + 1);
				bindsList.add(bKeyId + "," + bPluginId);
			}
			for (String bCheck : bindsList) {
				if (bindCacheList.contains(bCheck)) {
					bindCacheList.remove(bCheck);
				}
			}
			String[] newCaches = new String[bindCacheList.size() * 2];
			int i = 0;
			for (String remain : bindCacheList) {
				String[] keyIdAndPluginId = remain.split(",");
				newCaches[i] = keyIdAndPluginId[0].trim();
				newCaches[i + 1] = keyIdAndPluginId[1].trim();
				i += 2;
			}
			return newCaches;
		}
		return new String[0];
	}

	public void clearCacheCustomMetricBinds(String metricId) {
		String key = metricId;
		String[] binds = metricBindCache.get(key);
		if (logger.isInfoEnabled()) {
			logger.info("clearCacheCustomMetricBinds binds = "
					+ Arrays.toString(binds) + "," + "metricId = " + key);
		}
		List<String> instanceIds = new ArrayList<String>();
		if (binds != null && binds.length > 0) {
			for (int i = 0; i < binds.length; i += 2) {
				String instanceId = binds[i];
				instanceIds.add(instanceId);
			}
			if (instanceIds != null) {
				for (String instanceId : instanceIds) {
					String[] cacheMetricBins = instanceBindCache
							.get(instanceId);
					if (logger.isDebugEnabled()) {
						logger.debug("cacheMetricBins = "
								+ Arrays.toString(cacheMetricBins));
					}
					List<String> newCaches = new ArrayList<String>();
					int newCacheLength = cacheMetricBins.length;
					for (int i = 0; i < cacheMetricBins.length; i += 2) {
						String cacheMetricId = cacheMetricBins[i];
						String cachePlushId = cacheMetricBins[i + 1];
						if (cacheMetricId.equals(metricId)) {
							newCacheLength -= 2;
							continue;
						}
						newCaches.add(cacheMetricId);
						newCaches.add(cachePlushId);
					}
					if (logger.isInfoEnabled()) {
						logger.info("newCaches = " + newCaches
								+ "newCacheLength = " + newCacheLength);
					}
					String[] newCacheValues = new String[newCacheLength];
					instanceBindCache.set(instanceId,
							newCaches.toArray(newCacheValues));
				}
			}
			metricBindCache.delete(key);
		}
	}

	public void removeCacheCustomMetricBinds(String metricId, String pluginId) {
		String key = metricId;
		String[] binds = metricBindCache.get(key);
		List<String> newBinds = new ArrayList<>();
		if (binds != null && binds.length > 0) {
			for (int i = 1; i < binds.length; i += 2) {
				String pid = binds[i];
				if (pluginId.equals(pid)) {
					continue;
				}
				newBinds.add(pluginId);
			}
			binds = new String[newBinds.size()];
			newBinds.toArray(binds);
			metricBindCache.set(key, binds);
		}
	}

	public void addCacheCustomMetricBinds(
			List<CustomMetricBind> customMetricBinds) {
		Map<Long, List<String>> instanceBinds = new HashMap<>();
		Map<String, List<String>> metricBinds = new HashMap<>();
		for (CustomMetricBind customMetricBind : customMetricBinds) {
			Long rid = customMetricBind.getInstanceId();
			if (instanceBinds.containsKey(rid)) {
				instanceBinds.get(rid).add(customMetricBind.getMetricId());
				instanceBinds.get(rid).add(customMetricBind.getPluginId());
			} else {
				List<String> s = new ArrayList<>();
				s.add(customMetricBind.getMetricId());
				s.add(customMetricBind.getPluginId());
				instanceBinds.put(rid, s);
			}

			String metricId = customMetricBind.getMetricId();
			if (metricBinds.containsKey(metricId)) {
				metricBinds.get(metricId).add(
						String.valueOf(customMetricBind.getInstanceId()));
				metricBinds.get(metricId).add(customMetricBind.getPluginId());
			} else {
				List<String> s = new ArrayList<>();
				s.add(String.valueOf(customMetricBind.getInstanceId()));
				s.add(customMetricBind.getPluginId());
				metricBinds.put(metricId, s);
			}
		}
		for (Entry<Long, List<String>> set : instanceBinds.entrySet()) {
			String rid = String.valueOf(set.getKey());
			List<String> binds = set.getValue();
			String[] bindCaches = instanceBindCache.get(rid);

			if (logger.isDebugEnabled()) {
				logger.debug("instanceBindCache add cache in = "
						+ Arrays.toString(bindCaches));
			}
			if (bindCaches == null) {
				bindCaches = new String[binds.size()];
				instanceBindCache.set(rid, binds.toArray(bindCaches));
			} else {
				String[] newCaches = new String[binds.size()
						+ bindCaches.length];
				System.arraycopy(bindCaches, 0, newCaches, 0, bindCaches.length);
				for (int i = 0; i < binds.size(); i++) {
					newCaches[i + bindCaches.length] = binds.get(i);
				}
				instanceBindCache.set(rid, newCaches);
			}
		}

		for (Entry<String, List<String>> set : metricBinds.entrySet()) {
			String mid = String.valueOf(set.getKey());
			List<String> binds = set.getValue();
			String[] bindCaches = metricBindCache.get(mid);

			if (logger.isDebugEnabled()) {
				logger.debug("metricBindCache add cache in = "
						+ Arrays.toString(bindCaches));
			}

			if (bindCaches == null) {
				bindCaches = new String[binds.size()];
				metricBindCache.set(mid, binds.toArray(bindCaches));
			} else {
				String[] newCaches = new String[binds.size()
						+ bindCaches.length];
				System.arraycopy(bindCaches, 0, newCaches, 0, bindCaches.length);
				for (int i = 0; i < binds.size(); i++) {
					newCaches[i + bindCaches.length] = binds.get(i);
				}
				metricBindCache.set(mid, newCaches);
			}
		}
	}

	public void cacheCustomMetric(CustomMetric m) {
		memcache.set(m.getCustomMetricInfo().getId(), m);
	}

	public void updateCustomMetric(CustomMetric m) {
		memcache.update(m.getCustomMetricInfo().getId(), m);
	}

	public void removeCustomMetric(String metricId) {
		memcache.delete(metricId);
		clearCacheCustomMetricBinds(metricId);
	}

	public String[] getMetricPlugins(long instanceId) {
		return instanceBindCache.get(String.valueOf(instanceId));
	}

	public void setMetricPlugins(long instanceId, String[] ps) {
		instanceBindCache.set(String.valueOf(instanceId), ps);
	}

	public String[] getInstancePlugins(String metricId) {
		return metricBindCache.get(metricId);
	}

	public void setInstancePlugins(String metricId, String[] ps) {
		metricBindCache.set(metricId, ps);
	}
}
