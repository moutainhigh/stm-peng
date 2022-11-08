package com.mainsteam.stm.plugin.kvm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.kvm.bo.ConnectionInfo;
import com.mainsteam.stm.plugin.kvm.collector.KvmCollector;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * 
 * @author yuanlb TODO desc 下午3:15:37
 */
public class KvmPluginSession implements PluginSession {

	private static final Log LOGGER = LogFactory.getLog(KvmPluginSession.class);
	private static final String HOST = "IP";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String COMMAND = "COMMAND";
	private static final String UUID = "uuid";
	private static final String GET_RESOURCETREE = "getResourceTree";
	private static final String GET_POOL = "getHost";

	@SuppressWarnings("unused")
	private static final String GET_UUIDPERF = "getUuidPerf";

	private KvmCollector KvmCollector;

	@Override
	public void init(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		Parameter[] parameters = initParameters.getParameters();
		String serverIp = null, username = null, password = null;
		for (Parameter parameter : parameters) {
			switch (parameter.getKey()) {
			case HOST:
				serverIp = parameter.getValue();
				break;
			case USERNAME:
				username = parameter.getValue();
				break;
			case PASSWORD:
				password = parameter.getValue();
				break;
			default:
				if (LOGGER.isWarnEnabled())
					LOGGER.warn("Unkown initParameter: " + parameter.getKey()
							+ " = " + parameter.getValue());
			}
		}
		try {
			KvmCollector = new KvmCollector(new ConnectionInfo(serverIp,
					username, password));

		} catch (Exception e) {
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED
							+ " ;KVM初始化连接失败！kvm init connection faild！"
							+ "  time: "
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(new Date()),
					e);
		}
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return false;
	}

	@Override
	public void reload() {
	}

	@Override
	public boolean isAlive() {
		return KvmCollector != null;
	}

	@Override
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext context) throws PluginSessionRunException {
		PluginResultSet resultSet = new PluginResultSet();
		if (executorParameter instanceof PluginArrayExecutorParameter) {
			Parameter[] parameters = ((PluginArrayExecutorParameter) executorParameter)
					.getParameters();
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
						LOGGER.warn("Unkown execParameter: "
								+ parameter.getKey() + " = "
								+ parameter.getValue());
					break;
				}
			}
			try {

				if (StringUtils.startsWithIgnoreCase(cmd, GET_POOL)
						|| StringUtils.equalsIgnoreCase(cmd, GET_RESOURCETREE)) {
					// LOGGER.error(" 0 cmd--->" + cmd + " +  uuid--->" + uuid);
					Method method = KvmCollector.getClass().getMethod(cmd);
					resultSet.putValue(0, 0, method.invoke(KvmCollector)
							.toString());
				} else {
					// LOGGER.error(" 1cmd--->" + cmd + " +  uuid--->" + uuid);
					Method method = KvmCollector.getClass().getMethod(cmd,
							String.class);
					resultSet.putValue(0, 0, method.invoke(KvmCollector, uuid)
							.toString());
				}
			} catch (InvocationTargetException e) {
				InvocationTargetException targetEx = (InvocationTargetException) e;
				Exception exception = new Exception(targetEx.getMessage(),
						targetEx.getCause());
				exception.printStackTrace();
			} catch (Exception e) {
				throw new PluginSessionRunException(
						CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, e);
			}
		}
		return resultSet;
	}

	@Override
	public void destory() {
		if (KvmCollector != null) {
			KvmCollector.dispose();
			KvmCollector = null;
		}
	}
}
