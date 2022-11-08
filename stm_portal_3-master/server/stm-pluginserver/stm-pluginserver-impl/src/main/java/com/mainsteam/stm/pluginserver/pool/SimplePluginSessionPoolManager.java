/**
 * 
 */
package com.mainsteam.stm.pluginserver.pool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * @author fevergreen
 * 
 */
public class SimplePluginSessionPoolManager implements PluginSessionPoolManager {

	private static final Log logger = LogFactory
			.getLog(SimplePluginSessionPoolManager.class);

	private Map<String, PluginSessionPool> poolManagerMap;

	private CapacityService capacityService;

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	/**
	 * 
	 */
	public SimplePluginSessionPoolManager() {
		poolManagerMap = new HashMap<>();
	}

	private String buildSessionKey(PluginRequest request) {
		return request.getPluginSessionKey();
	}

	@Override
	public synchronized PluginSessionPool getOrCreatePluginSessionPool(
			final PluginRequest request) {
		String key = buildSessionKey(request);
		if (poolManagerMap.containsKey(key)) {
			return poolManagerMap.get(request.getPluginSessionKey());
		}
		PluginSessionPool p = new PluginSessionPool() {
			private PluginSession session;

			@Override
			public void returnSession(PluginSession s) {
				this.session = s;
			}

			@Override
			public void destory() {
				if(this.session!=null){
					this.session.destory();
				}
			}

			@Override
			public PluginSession borrowSession() {
				if (this.session == null) {
					PluginSession s = null;
					try {
						s = createPluginSession(request.getPluginId());
						s.init(request.getPluginInitParameter());
						this.session = s;
					} catch (InstantiationException | IllegalAccessException
							| ClassNotFoundException e) {
						if (logger.isErrorEnabled()) {
							logger.error("borrowSession", e);
						}
						throw new RuntimeException(e);
					} catch (PluginSessionRunException e) {
						if (logger.isErrorEnabled()) {
							logger.error("borrowSession", e);
						}
						throw new RuntimeException(e);
					}
				}
				return this.session;
			}

			@Override
			public void destory(PluginSession session) throws Exception {
				session.destory();
			}

			@Override
			public int getMaxActive() {
				return 0;
			}

			@Override
			public int getMaxIdle() {
				return 0;
			}

			@Override
			public String getPluginId() {
				return null;
			}

			@Override
			public String getPluginSessionKey() {
				return null;
			}

			@Override
			public PluginInitParameter getInitParameter() {
				return null;
			}
		};
		poolManagerMap.put(key, p);
		return p;
	}

	private PluginSession createPluginSession(String pluginId)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		assert pluginId != null;
		PluginDef pluginDef = capacityService.getPluginDef(pluginId);
		String classurl = null;
		if (pluginDef == null) {
			String msg = "createPluginSession plugin not exist.pluginId="
					+ pluginId;
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			throw new RuntimeException(msg);
		} else {
			classurl = pluginDef.getClassUrl();
		}

		if (classurl == null || classurl.equals("")) {
			if (logger.isErrorEnabled()) {
				String msg = "createPluginSession plugin classurl not is emtpy.pluginId="
						+ pluginId;
				logger.error(msg);
				throw new RuntimeException(msg);
			}
		}

		@SuppressWarnings("unchecked")
		Class<? extends PluginSession> c = (Class<? extends PluginSession>) Class
				.forName(classurl);
		return c.newInstance();
	}

	@Override
	public PluginSessionPool getPluginSessionPool(PluginRequest request) {
		String key = buildSessionKey(request);
		if (poolManagerMap.containsKey(key)) {
			return poolManagerMap.get(request.getPluginSessionKey());
		}
		return null;
	}

	@Override
	public void closePluginSessionPool(PluginRequest request) {
		String key = buildSessionKey(request);
		if (poolManagerMap.containsKey(key)) {
			try {
				poolManagerMap.remove(key).destory();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("closePluginSessionPool", e);
				}
			}
		}
	}
}
