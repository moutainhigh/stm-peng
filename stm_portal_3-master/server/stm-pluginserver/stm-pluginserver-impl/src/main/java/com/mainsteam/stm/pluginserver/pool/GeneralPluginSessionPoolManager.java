/**
 * 
 */
package com.mainsteam.stm.pluginserver.pool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.mainsteam.stm.util.SecureUtil;

/**
 * @author ziw
 * 
 */
public class GeneralPluginSessionPoolManager implements
		PluginSessionPoolManager {

	private static final Log logger = LogFactory
			.getLog(GeneralPluginSessionPoolManager.class);

	private PluginSessoinFactory sessoinFactory;

	private Map<String, PluginSessionPool> pools;

	private CapacityService capacityService;

	/**
	 * @param sessoinFactory
	 *            the sessoinFactory to set
	 */
	public final void setSessoinFactory(PluginSessoinFactory sessoinFactory) {
		this.sessoinFactory = sessoinFactory;
	}

	/**
	 * @param capacityService
	 *            the capacityService to set
	 */
	public final void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	/**
	 * 
	 */
	public GeneralPluginSessionPoolManager() {
		pools = new HashMap<String, PluginSessionPool>(1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.pluginserver.pool.PluginSessionPoolManager#
	 * getPluginSessionPool
	 * (com.mainsteam.stm.pluginserver.message.PluginRequest)
	 */
	@Override
	public synchronized PluginSessionPool getPluginSessionPool(
			PluginRequest request) {
		if (pools.containsKey(request.getPluginSessionKey())) {
			return pools.get(request.getPluginSessionKey());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.pluginserver.pool.PluginSessionPoolManager#
	 * getOrCreatePluginSessionPool
	 * (com.mainsteam.stm.pluginserver.message.PluginRequest)
	 */
	@Override
	public synchronized PluginSessionPool getOrCreatePluginSessionPool(
			PluginRequest request) {
		if (pools.containsKey(request.getPluginSessionKey())) {
			return pools.get(request.getPluginSessionKey());
		} else {
			GeneralPluginSessionPool sessionPool = new GeneralPluginSessionPool(
					request.getPluginId(), request.getPluginSessionKey(),
					request.getPluginInitParameter());
			pools.put(request.getPluginSessionKey(), sessionPool);
			return sessionPool;
		}
	}

	private abstract class AbstractPluginSessionPool implements
			PluginSessionPool {

		protected String pluginId;
		protected String pluginSessionKey;
		protected PluginInitParameter initParameter;
		protected int maxActive;
		protected int maxIdle;

		/**
		 * @param pluginId
		 * @param pluginSessionKey
		 */
		public AbstractPluginSessionPool(String pluginId,
				String pluginSessionKey, PluginInitParameter initParameter) {
			this.pluginId = pluginId;
			this.pluginSessionKey = pluginSessionKey;
			this.initParameter = initParameter;
		}

		/**
		 * @return the maxActive
		 */
		public final int getMaxActive() {
			return maxActive;
		}

		/**
		 * @return the maxIdle
		 */
		public final int getMaxIdle() {
			return maxIdle;
		}

		/**
		 * @return the initParameter
		 */
		public final PluginInitParameter getInitParameter() {
			return initParameter;
		}

		/**
		 * @return the pluginId
		 */
		public final String getPluginId() {
			return pluginId;
		}

		/**
		 * @return the pluginSessionKey
		 */
		public final String getPluginSessionKey() {
			return pluginSessionKey;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.mainsteam.stm.pluginserver.pool.PluginSessionPool#destory()
		 */
		@Override
		public void destory() throws Exception {
			pools.remove(pluginSessionKey);
		}
	}

	private class GeneralPluginSessionPool extends AbstractPluginSessionPool {

		private StackObjectPool objectPool;

		public GeneralPluginSessionPool(final String pluginId,
				final String pluginSessionKey,
				final PluginInitParameter initParameter) {
			super(pluginId, pluginSessionKey, initParameter);
			final PluginDef pluginDef = capacityService.getPluginDef(pluginId);
			if (pluginDef.getMaxActiveSession() != null
					&& NumberUtils.isNumber(pluginDef.getMaxActiveSession())) {
				super.maxActive = Integer.parseInt(pluginDef
						.getMaxActiveSession());
			} else {
				super.maxActive = 1;
			}
			if (pluginDef.getMaxIdleSession() != null
					&& NumberUtils.isNumber(pluginDef.getMaxIdleSession())) {
				super.maxIdle = Integer.parseInt(pluginDef.getMaxIdleSession());
			} else {
				super.maxIdle = 0;
			}
			PoolableObjectFactory factory = new PoolableObjectFactory() {
				@Override
				public boolean validateObject(Object session) {
					return ((PluginSession) session).isAlive();
				}

				@Override
				public void passivateObject(Object session) throws Exception {
					// do nothing.
				}

				@Override
				public Object makeObject() throws Exception {
					return createSession(pluginDef, initParameter,
							pluginSessionKey);
				}

				@Override
				public void destroyObject(Object session) throws Exception {
					((PluginSession) session).destory();
				}

				@Override
				public void activateObject(Object session) throws Exception {
					// do nothing.
				}
			};
			objectPool = new StackObjectPool(factory, super.maxActive);
		}

		@Override
		public PluginSession borrowSession() throws Exception {
			try {
				return (PluginSession) objectPool.borrowObject();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("borrowSession sessionKey="
							+ super.pluginSessionKey, e);
				}
				throw e;
			}
		}

		@Override
		public void returnSession(PluginSession session) throws Exception {
			objectPool.returnObject(session);
		}

		@Override
		public void destory() throws Exception {
			objectPool.close();
		}

		@Override
		public void destory(PluginSession session) throws Exception {
			objectPool.invalidateObject(session);
		}
	}

	@SuppressWarnings("unused")
	private class SimplePluginSessoinPool extends AbstractPluginSessionPool {
		private PluginSession[] stack;
		private int top = -1;
		private int validateLength = 0;

		public SimplePluginSessoinPool(final String pluginId,
				final String pluginSessionKey,
				final PluginInitParameter initParameter) {
			super(pluginId, pluginSessionKey, initParameter);
			final PluginDef pluginDef = capacityService.getPluginDef(pluginId);
			if (pluginDef.getMaxActiveSession() != null
					&& NumberUtils.isNumber(pluginDef.getMaxActiveSession())) {
				super.maxActive = Integer.parseInt(pluginDef
						.getMaxActiveSession());
			} else {
				super.maxActive = 1;
			}
			if (pluginDef.getMaxIdleSession() != null
					&& NumberUtils.isNumber(pluginDef.getMaxIdleSession())) {
				super.maxIdle = Integer.parseInt(pluginDef.getMaxIdleSession());
			} else {
				super.maxIdle = 0;
			}
			stack = new PluginSession[getMaxActive()];
		}

		@Override
		public synchronized PluginSession borrowSession() throws Exception {
			if (top < 0) {
				if (validateLength < stack.length) {
					PluginSession session = createSession(
							capacityService.getPluginDef(pluginId),
							initParameter, pluginSessionKey);
					returnSession(session);
					validateLength++;
				} else {
					return null;
				}
			}
			PluginSession session = stack[top];
			stack[top--] = null;
			return session;
		}

		@Override
		public synchronized void returnSession(PluginSession session)
				throws Exception {
			stack[++top] = session;
		}

		@Override
		public synchronized void destory(PluginSession session)
				throws Exception {
			session.destory();
			boolean find = false;
			for (int i = 0; i < validateLength; i++) {
				if (stack[i] == session) {
					stack[i] = null;
					if (i < validateLength - 1) {
						System.arraycopy(stack, i + 1, stack, i, validateLength
								- i - 1);
					}
					validateLength--;
					find = true;
					break;
				}
			}
			if (find) {
				return;
			} else {
				validateLength--;
			}
		}
	}

	private String buildMsgInitParameters(PluginDef pluginDef,
			PluginInitParameter initParameter, String pluginSessionKey) {
//		com.mainsteam.stm.caplib.plugin.PluginInitParameter[] pinitPs = pluginDef
//				.getPluginInitParameters();
//		Map<String, Boolean> pwdParameterMap = new HashMap<>();
//		if (pinitPs != null && pinitPs.length > 0) {
//			for (com.mainsteam.stm.caplib.plugin.PluginInitParameter p : pinitPs) {
//				if (p.isPassword()) {
//					pwdParameterMap.put(p.getId(), Boolean.TRUE);
//				}
//			}
//		}
		StringBuilder b = new StringBuilder();
		b.append(
				"makeObject init plugin session.initParameters=\r\n sessionKey=")
				.append(pluginSessionKey);
		Parameter[] parameters = initParameter.getParameters();
		b.append('[');
		if (parameters != null && parameters.length > 0) {
			for (Parameter parameter : parameters) {
//				if (pwdParameterMap.containsKey(parameter.getKey())) {
//					b.append('{').append(parameter.getKey()).append(',')
//							.append("******").append('}').append(',');
//				} else {
					b.append('{').append(parameter.getKey()).append(',')
							.append(parameter.getValue()).append('}')
							.append(',');
//				}
			}
		}
		b.append(']');
		return b.toString();
	}

	private PluginInitParameter decodePasswordedInfo(PluginDef pluginDef,
			PluginInitParameter initParameter) {
		PluginInitParameterData decodedInitParameter = new PluginInitParameterData();
		com.mainsteam.stm.caplib.plugin.PluginInitParameter[] pinitPs = pluginDef
				.getPluginInitParameters();
		Map<String, Boolean> pwdParameterMap = new HashMap<>();
		if (pinitPs != null && pinitPs.length > 0) {
			for (com.mainsteam.stm.caplib.plugin.PluginInitParameter p : pinitPs) {
				if (p.isPassword()) {
					pwdParameterMap.put(p.getId(), Boolean.TRUE);
				}
			}
		}

		Parameter[] parameters = initParameter.getParameters();
		if (parameters != null && parameters.length > 0) {
			for (int i = 0; i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				if (pwdParameterMap.containsKey(parameter.getKey())) {
					String decodedValue = null;
					if(parameter.getValue()!=null){
						try {
							decodedValue = SecureUtil.pwdDecrypt(parameter.getValue());
						} catch (Throwable e) {
							if (logger.isErrorEnabled()) {
								logger.error(
										"error decode password for " + parameter.getKey(),
										e);
							}
						}
					}
					if (decodedValue != null) {
						ParameterValue p = new ParameterValue();
						p.setKey(parameter.getKey());
						p.setType(parameter.getType());
						p.setValue(decodedValue);
						decodedInitParameter.setParameter(p.getKey(), p);
					} else {
						decodedInitParameter.setParameter(parameter.getKey(),
								parameter);
					}
				} else {
					decodedInitParameter.setParameter(parameter.getKey(),
							parameter);
				}
			}
		}
		return decodedInitParameter;
	}

	private PluginSession createSession(PluginDef pluginDef,
			PluginInitParameter initParameter, String pluginSessionKey)
			throws PluginSessionRunException {
		PluginSession session = null;
		try {
			session = sessoinFactory.createPluginSession(pluginDef);
			PluginInitParameter decodedParameter = decodePasswordedInfo(
					pluginDef, initParameter);
			session.init(decodedParameter);
			if (session.isAlive() == false) {
				StringBuilder msgBuffer = new StringBuilder(
						"createSession fail.session.isAlive = false.\r\n");
				msgBuffer.append(buildMsgInitParameters(pluginDef,
						initParameter, pluginSessionKey));
				String msg = msgBuffer.toString();
				if (logger.isErrorEnabled()) {
					logger.error(msg);
				}
				throw new PluginSessionRunException(
						ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_PLUGINSESSION_INIT,
						msg);
			} else {
				if (logger.isInfoEnabled()) {
					String msg = buildMsgInitParameters(pluginDef,
							initParameter, pluginSessionKey);
					logger.info(msg);
				}
			}
		} catch (PluginSessionRunException e) {
			if (logger.isErrorEnabled()
					&& e.getCode() != ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_PLUGINSESSION_INIT) {
				logger.error(
						buildMsgInitParameters(pluginDef, initParameter,
								pluginSessionKey), e);
			}
			if (session != null) {
				try {
					session.destory();
				} catch (Throwable e1) {
					if (logger.isErrorEnabled()) {
						logger.error("createSession session.destory.", e1);
					}
				}
			}
			throw e;
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						buildMsgInitParameters(pluginDef, initParameter,
								pluginSessionKey), e);
			}
			if (session != null) {
				try {
					session.destory();
				} catch (Throwable e1) {
					if (logger.isErrorEnabled()) {
						logger.error("createSession session.destory.", e1);
					}
				}
			}
			throw new PluginSessionRunException(
					ServerErrorCodeConstant.ERR_SERVER_PLUGINSERVER_PLUGINSESSION_INIT,
					e);
		}
		return session;
	}

	@Override
	public synchronized void closePluginSessionPool(PluginRequest request) {
		if (pools.containsKey(request.getPluginSessionKey())) {
			try {
				pools.remove(request.getPluginSessionKey()).destory();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("closePluginSessionPool", e);
				}
			}
		}
	}
}
