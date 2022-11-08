/**
 * 
 */
package com.mainsteam.stm.pluginserver.context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * @author ziw
 * 
 */
public class PluginSessionContextImpl implements PluginSessionContext {

	private Map<String, Object> runtimeValueMap;

	private ThreadLocal<CollectContext> threadCollectContext = new ThreadLocal<>();

	/**
	 * 
	 */
	public PluginSessionContextImpl() {
		runtimeValueMap = new HashMap<String, Object>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginsession.PluginSessionContext#getRuntimeValue
	 * (java.lang.String)
	 */
	@Override
	public synchronized Object getRuntimeValue(String key) {
		return runtimeValueMap.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginsession.PluginSessionContext#setRuntimeValue
	 * (java.lang.String, java.lang.Object)
	 */
	@Override
	public synchronized void setRuntimeValue(String key, Object value) {
		runtimeValueMap.put(key, value);
	}

	private CollectContext getCollectContext() {
		CollectContext c = threadCollectContext.get();
		if (c == null) {
			c = new CollectContext();
			threadCollectContext.set(c);
		}
		return c;
	}

	@Override
	public Date getMetricCollectTime() {
		return getCollectContext().metricCollectTime;
	}

	@Override
	public String getMetricId() {
		return getCollectContext().metricId;
	}

	@Override
	public String getResourceId() {
		return getCollectContext().resourceId;
	}

	@Override
	public long getResourceInstanceId() {
		return getCollectContext().resourceInstanceId;
	}

	public String setMetricId(String metricId) {
		return getCollectContext().metricId = metricId;
	}

	public String setResourceId(String resourceId) {
		return getCollectContext().resourceId = resourceId;
	}

	public long setResourceInstanceId(long resourceInstanceId) {
		return getCollectContext().resourceInstanceId = resourceInstanceId;
	}

	public void setMetricCollectTime(Date metricCollectTime) {
		getCollectContext().metricCollectTime = metricCollectTime;
	}

	private class CollectContext {
		private String resourceId;

		private String metricId;

		private long resourceInstanceId;

		private Date metricCollectTime;

		private Map<String, Object> runtimeValueMap;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.mainsteam.stm.pluginsession.PluginSessionContext#getRuntimeValue
		 * (java.lang.String)
		 */
		public Object getRuntimeValue(String key) {
			if (CollectContext.this.runtimeValueMap != null) {
				return CollectContext.this.runtimeValueMap.get(key);
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.mainsteam.stm.pluginsession.PluginSessionContext#setRuntimeValue
		 * (java.lang.String, java.lang.Object)
		 */
		public void setRuntimeValue(String key, Object value) {
			if (CollectContext.this.runtimeValueMap == null) {
				CollectContext.this.runtimeValueMap = new HashMap<String, Object>();
			}
			CollectContext.this.runtimeValueMap.put(key, value);
		}
	}

	@Override
	public Object getThreadLocalRuntimeValue(String key) {
		return getCollectContext().getRuntimeValue(key);
	}

	@Override
	public void setThreadLocalRuntimeValue(String key, Object value) {
		getCollectContext().setRuntimeValue(key, value);
	}

	public void clear() {
		this.threadCollectContext.remove();
	}
}
