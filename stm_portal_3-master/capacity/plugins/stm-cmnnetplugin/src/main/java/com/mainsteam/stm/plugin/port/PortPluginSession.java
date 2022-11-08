package com.mainsteam.stm.plugin.port;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.AvailableStateEnum;
import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class PortPluginSession implements PluginSession {

	private static final Log logger = LogFactory
			.getLog(PortPluginSession.class);

	public static final String PORTPLUGIN_NAME = "name";
	public static final String PORTPLUGIN_HOST = "host";
	public static final String PORTPLUGIN_PORT = "port";

	private boolean sessionAlive;
	private String name;
	private InetAddress address;
	private int port = -1;

	@Override
	public void destory() {
		sessionAlive = false;
	}

	@Override
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext arg1) throws PluginSessionRunException {
		if (logger.isDebugEnabled()){
			logger.debug("PortPluginSession executing Starts");
		}
		if (!sessionAlive) { // if session is not available
			if (logger.isErrorEnabled()) {
				logger.error("Try to execute the non-alive PortPluginSession");
			}
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED,
					"The PortPluginSession is not alive");
		}
		PluginResultSet resultSet = new PluginResultSet();
		try {
			Socket socket = new Socket(address, port);
			resultSet.addRow(new String[] { name, address.getHostAddress(),
					Integer.toString(port),
					String.valueOf(AvailableStateEnum.Normal.getStateVal()) });
			socket.close();
		} catch (IOException e) {
			resultSet
					.addRow(new String[] {
							name,
							address.getHostAddress(),
							Integer.toString(port),
							String.valueOf(AvailableStateEnum.Critical
									.getStateVal()) });
		}
		if (logger.isDebugEnabled()){
			logger.debug("PortPluginSession executing Finished");
		}
		return resultSet;
	}

	@Override
	public void init(PluginInitParameter init) throws PluginSessionRunException {
		if (logger.isDebugEnabled()){
			logger.debug("PortPluginSession initializing Starts");
		}
		Parameter[] initParameters = init.getParameters();
		String host = "";
		for (Parameter initParameter : initParameters) {
			switch (initParameter.getKey()) {
			case PORTPLUGIN_NAME:
				name = initParameter.getValue();
				break;
			case PORTPLUGIN_HOST:
				host = initParameter.getValue();
				try {
					address = InetAddress.getByName(host);
				} catch (UnknownHostException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Invalid host/port to initialize the PortPluginSession");
					}
					sessionAlive = false;
					throw new PluginSessionRunException(
							CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS,
							"Invalid host to initialize the PortPluginSession");
				}
				break;
			case PORTPLUGIN_PORT:
				try {
					port = Integer.parseInt(initParameter.getValue());
				} catch (NumberFormatException e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Invalid port number format to initialize the PortPluginSession",
								e);
					}
					sessionAlive = false;
					throw new PluginSessionRunException(
							CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS,
							"Invalid port number format to initialize the PortPluginSession",
							e);
				}
				break;
			}
		}
		if (StringUtils.isEmpty(host) || port < 0) {
			if (logger.isErrorEnabled()) {
				logger.error("No host/port information to initialize the PortPluginSession");
			}
			sessionAlive = false;
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS,
					"No host/port information to initialize the PortPluginSession");
		}
		if (StringUtils.isEmpty(name)) {
			name = host + ":" + port;
		}
		sessionAlive = true;
		if (logger.isDebugEnabled()){
			logger.debug("PortPluginSession initializing Finished");
		}
	}

	@Override
	public boolean isAlive() {
		return sessionAlive;
	}

	@Override
	public void reload() {
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return false;
	}
}
