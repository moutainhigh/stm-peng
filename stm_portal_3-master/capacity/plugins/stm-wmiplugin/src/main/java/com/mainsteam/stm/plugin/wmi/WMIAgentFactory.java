package com.mainsteam.stm.plugin.wmi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import com.mainsteam.stm.plugin.wmi.bio.BIOAgent;
import com.mainsteam.stm.plugin.wmi.serial.SerialAgent;
import com.mainsteam.stm.util.PropertiesFileUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WMIAgentFactory {
	private static final Log logger = LogFactory.getLog(WMIAgentFactory.class);

	private static final String DEFAULT_IP = "127.0.0.1";
	private static final int DEFAULT_PORT = 12345;
	private static final String DEFAULT_TYPE = WMIAgent.TYPE_SERIAL;
	private static final int DEFAULT_TIMEOUT = 10000;

	private static final String PROPERTY_FILE = "WMIAgent.properties";
	
	private static final String PROPERTY_IP = "wmiagent_ipaddress";
	private static final String PROPERTY_PORT = "wmiagent_port";
	private static final String PROPERTY_TYPE = "wmiagent_type";
	private static final String PROPERTY_TIMEOUT = "wmitimeout";
	
	private static final HashMap<WMIConnetionInfo, WMIAgent> agents = new HashMap<>();

	private WMIAgentFactory() {
	}

	public static WMIAgent getDefaultAgent() throws IOException {
		String ip = DEFAULT_IP;
		int port = DEFAULT_PORT;
		int timeout = DEFAULT_TIMEOUT;
		String type = DEFAULT_TYPE;
		
		// load WMIAgent properties file
		Properties properties = PropertiesFileUtil.getProperties(PROPERTY_FILE);;
		if (properties == null) {
			properties = new Properties();
			InputStream in = ClassLoader.getSystemResourceAsStream(PROPERTY_FILE);
			if (in != null) {
				properties.load(in);
			}
		}
		String propertyIp = properties.getProperty(PROPERTY_IP);
		if (propertyIp != null)
			ip = propertyIp;
		String propertyType = properties.getProperty(PROPERTY_TYPE);
		if (propertyType != null)
			type = propertyType;
		String propertyPort = properties.getProperty(PROPERTY_PORT);
		if (propertyPort != null)
			port = Integer.valueOf(propertyPort);
		String propertyTimeout = properties.getProperty(PROPERTY_TIMEOUT);
		if (propertyTimeout != null)
			timeout = Integer.valueOf(propertyTimeout);
		
		return getAgent(ip, port, type, timeout);
	}

	public static synchronized WMIAgent getAgent(String ip, int port, String type, int timeout) {
		WMIConnetionInfo connetionInfo = new WMIConnetionInfo(ip, port, type);
		if (logger.isInfoEnabled())
			logger.info("Getting agent: " + connetionInfo);
		WMIAgent agent = agents.get(connetionInfo);
		if (agent == null) {
			switch (type) {
			case WMIAgent.TYPE_BIO:
				agent = new BIOAgent(ip, port, timeout);
				break;
			case WMIAgent.TYPE_SERIAL:
				agent = new SerialAgent(ip, port, timeout);
				break;
			default:
				throw new IllegalArgumentException("Unsupported WMI Agent type: " + type);
			}
			agents.put(connetionInfo, agent);
		}
		return agent;
	}

}
