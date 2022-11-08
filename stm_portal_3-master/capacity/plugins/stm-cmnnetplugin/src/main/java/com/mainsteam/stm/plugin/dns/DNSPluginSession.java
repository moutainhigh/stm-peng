package com.mainsteam.stm.plugin.dns;

import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import com.mainsteam.stm.caplib.dict.AvailableStateEnum;
import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class DNSPluginSession implements PluginSession {

	private static final Log logger = LogFactory.getLog(DNSPluginSession.class);

	public static final String DNSPLUGIN_NAME = "name";
	public static final String DNSPLUGIN_IP = "IP";
	public static final String DNSPLUGIN_PORT = "port";
	public static final String DNSPLUGIN_TARGET = "target";
	public static final String DNSPlUGIN_TIMEOUT = "timeout";

	private static final int DNSPLUGIN_DEFAULT_PORT = 53;
	private static final int DNSPLUGIN_DEFAULT_TIMEOUT = 30000; // unit is ms

	private boolean sessionAlive;

	private String ip;
	private int port = DNSPLUGIN_DEFAULT_PORT;
	private SimpleResolver resolver;

	@Override
	public void init(PluginInitParameter init) throws PluginSessionRunException {
		if (logger.isDebugEnabled())
			logger.debug("DNSPluginSession initializing Starts");
		Parameter[] initParameters = init.getParameters();
		for (Parameter parameter : initParameters) {
			switch (parameter.getKey()) {
			case DNSPLUGIN_IP:
				ip = parameter.getValue();
				break;
			case DNSPLUGIN_PORT:
				try {
					port = Integer.valueOf(parameter.getValue());
				} catch (NumberFormatException e) {
					if (logger.isWarnEnabled())
						logger.warn("Invalid port number format to initialize the DNSPluginSession", e);
				}
				break;
			default:
				if (logger.isWarnEnabled())
					logger.warn("Unkown initialize parameter key : " + parameter.getKey());
				break;
			}
		}
		if (StringUtils.isEmpty(ip)) {
			if (logger.isErrorEnabled())
				logger.error("No dns-host or target information to initialize the DNSPluginSession");
			sessionAlive = false;
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS, "No dns-host information to initialize the DNSPluginSession");
		}
		try {
			resolver = new SimpleResolver(ip);
			resolver.setPort(port);
			sessionAlive = true;
		} catch (UnknownHostException e) {
			if (logger.isErrorEnabled())
				logger.error("Invalid dns-host ip to initialize the DNSPluginSession", e);
			sessionAlive = false;
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS, "Invalid dns-host ip to initialize the DNSPluginSession", e);
		}
		sessionAlive = true;
		if (logger.isDebugEnabled())
			logger.debug("DNSPluginSession initializing Finished : " + ip + ":" + port);
	}

	@Override
	public void destory() {
		sessionAlive = false;
		resolver = null;
	}

	@Override
	public void reload() {
	}

	@Override
	public boolean isAlive() {
		return sessionAlive;
	}

	@Override
	public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
		// name, ip, port, available, target, resolveResult, responseTime, status
		if (logger.isDebugEnabled())
			logger.debug("DNSPluginSession executing Starts : " + ip + ":" + port);
		if (!sessionAlive) { // if session is not alive
			if (logger.isErrorEnabled())
				logger.error("Try to execute the non-alive DNSPluginSession");
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, "The DNSPluginSession is not alive");
		}
		PluginResultSet result = new PluginResultSet();
		int timeout = DNSPLUGIN_DEFAULT_TIMEOUT;
		Name target = null;
		if (executorParameter instanceof PluginArrayExecutorParameter) {
			Parameter[] parameters = ((PluginArrayExecutorParameter) executorParameter).getParameters();
			for (Parameter parameter : parameters) {
				switch (parameter.getKey()) {
				case DNSPLUGIN_TARGET:
					try {
						target = Name.fromString(parameter.getValue());
					} catch (TextParseException e) {
						if (logger.isErrorEnabled())
							logger.error("Invalid target to execute the DNSPluginSession", e);
						sessionAlive = false;
						throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS, "Invalid target to execute the DNSPluginSession", e);
					}
					break;
				
				default:
					if (logger.isWarnEnabled())
						logger.warn("Unkown execute parameter key : " + parameter.getKey());
					break;
				}
			}
		}
		if (target == null)
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS, "No resolve target information to execute the DNSPluginSession");
		resolver.setTimeout(0, timeout);
		Lookup lookup = new Lookup(target);
		lookup.setResolver(resolver);
		lookup.setCache(null);
		long start = System.currentTimeMillis();
		Record[] records = lookup.run();
		long end = System.currentTimeMillis();
		if (lookup.getResult() == Lookup.SUCCESSFUL)
			result.addRow(new String[] { ip + ":" + port, ip, String.valueOf(port), String.valueOf(AvailableStateEnum.Normal.getStateVal()), target.toString(), records[0].rdataToString(), String.valueOf(end - start), lookup.getErrorString() });
		else
			result.addRow(new String[] { ip + ":" + port, ip, String.valueOf(port), String.valueOf(AvailableStateEnum.Critical.getStateVal()), target.toString(), "", String.valueOf(end - start), lookup.getErrorString() });
		if (logger.isDebugEnabled())
			logger.debug("DNSPluginSession executing Finished : " + ip + ":" + port);
		return result;
	}

	@Override
	public boolean check(PluginInitParameter initParameters) throws PluginSessionRunException {
		return false;
	}
}
