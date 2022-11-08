package com.mainsteam.stm.plugin.xen;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.xen.bo.ConnectionInfo;
import com.mainsteam.stm.plugin.xen.collect.XenCollector;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class XenPluginSession implements PluginSession {

	private static final Log LOGGER = LogFactory.getLog(XenPluginSession.class);

	private static final String HOST = "IP";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String COMMAND = "COMMAND";
	private static final String UUID = "uuid";
	private static final String GET_RESOURCETREE = "getResourceTree";
	private static final String GET_POOL = "getPool";

	private XenCollector collector;

	@Override
	public void init(PluginInitParameter initParameters) throws PluginSessionRunException {
		Parameter[] parameters = initParameters.getParameters();
		String host = null, username = null, password = null;
		for (Parameter parameter : parameters) {
			switch (parameter.getKey()) {
			case HOST:
				host = parameter.getValue();
				break;
			case USERNAME:
				username = parameter.getValue();
				break;
			case PASSWORD:
				password = parameter.getValue();
				break;
			default:
				if (LOGGER.isWarnEnabled())
					LOGGER.warn("Unkown initParameter: " + parameter.getKey() + " = " + parameter.getValue());
			}
		}
		try {
			collector = new XenCollector(new ConnectionInfo(host, username, password));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED, e);
		}
	}

	@Override
	public boolean check(PluginInitParameter initParameters) throws PluginSessionRunException {
		return false;
	}

	@Override
	public void destory() {
		if (collector != null)
			collector.dispose();
		collector = null;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isAlive() {
		return collector != null;
	}

	@Override
	public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
		PluginResultSet resultSet = new PluginResultSet();
		if (executorParameter instanceof PluginArrayExecutorParameter) {
			Parameter[] parameters = ((PluginArrayExecutorParameter) executorParameter).getParameters();
			String cmd = null, uuid = null;
			for (Parameter parameter : parameters) {
				switch (parameter.getKey()) {
				case COMMAND:
					cmd = parameter.getValue();
					break;
				case UUID:
					uuid = parameter.getValue();
					break;
				default:
					if (LOGGER.isWarnEnabled())
						LOGGER.warn("Unkown execParameter: " + parameter.getKey() + " = " + parameter.getValue());
					break;
				}
			}
			try {
				if (StringUtils.startsWithIgnoreCase(cmd, GET_POOL) || StringUtils.equalsIgnoreCase(cmd, GET_RESOURCETREE)) {
					Method method = collector.getClass().getMethod(cmd);
					resultSet.putValue(0, 0, method.invoke(collector).toString());
				} else {
					Method method = collector.getClass().getMethod(cmd, String.class);
					resultSet.putValue(0, 0, method.invoke(collector, uuid).toString());
				}
			} catch (Exception e) {
				LOGGER.error(e);
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, e);
			}
		}

		return resultSet;
	}

}
