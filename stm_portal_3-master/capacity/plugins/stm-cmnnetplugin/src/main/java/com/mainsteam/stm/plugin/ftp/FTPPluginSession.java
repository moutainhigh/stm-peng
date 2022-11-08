package com.mainsteam.stm.plugin.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
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

public class FTPPluginSession implements PluginSession {

	private static final Log logger = LogFactory.getLog(FTPPluginSession.class);

	public static final String FTPPLUGIN_NAME = "name";
	public static final String FTPPLUGIN_HOST = "host";
	public static final String FTPPLUGIN_PORT = "port";
	public static final String FTPPLUGIN_USERNAME = "username";
	public static final String FTPPLUGIN_PASSWORD = "password";
	public static final String FTPPLUGIN_PATH = "path";

	private static final int FTPPLUGIN_DEFAULT_PORT = 21;

	private FTPClient ftpClient;
	private boolean sessionAlive;

	private String name;
	private InetAddress address;
	private int port = FTPPLUGIN_DEFAULT_PORT;
	private String username = "anonymous";
	private String password = "anonymous";

	@Override
	public void init(PluginInitParameter init) throws PluginSessionRunException {
		if (logger.isDebugEnabled())
			logger.debug("FTPPluginSession initializing Starts");
		Parameter[] initParameters = init.getParameters();
		String host = null;
		for (Parameter parameter : initParameters) {
			switch (parameter.getKey()) {
			case FTPPLUGIN_HOST:
				try {
					host = parameter.getValue();
					address = InetAddress.getByName(host);
				} catch (UnknownHostException e) {
					if (logger.isErrorEnabled())
						logger.error("Invalid hostname/ip to initialize the FTPPluginSession", e);
					sessionAlive = false;
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS, "Invalid hostname/ip to initialize the FTPPluginSession", e);
				}
				break;
			case FTPPLUGIN_PORT:
				try {
					port = Integer.valueOf(parameter.getValue());
				} catch (NumberFormatException e) {
					if (logger.isWarnEnabled())
						logger.warn("Invalid port number format to initialize the FTPPluginSession", e);
				}
				break;
			case FTPPLUGIN_USERNAME:
				username = parameter.getValue();
				break;
			case FTPPLUGIN_PASSWORD:
				password = parameter.getValue();
				break;
			default:
				if (logger.isWarnEnabled())
					logger.warn("Unkown initialize parameter key : " + parameter.getKey());
				break;
			}
		}
		if (address == null) {
			if (logger.isErrorEnabled())
				logger.error("No ftp server address information to initialize the FTPPluginSession");
			sessionAlive = false;
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS, "No ftp server address information to initialize the FTPPluginSession");
		}
		name = host + ":" + port;
		ftpClient = new FTPClient();
		if (testConnect() < 0)
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS, "FTP connection failed");
		sessionAlive = true;
		if (logger.isDebugEnabled()) {
			logger.debug("FTPPluginSession initializing Finished");
		}
	}

	@Override
	public void destory() {
		sessionAlive = false;
		ftpClient = null;
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
		// return: name(0), ip(1), port(2), availability(3), connectTime(4), loginTime(5), downloadSpeed(6), status(7)
		if (logger.isDebugEnabled())
			logger.debug("FTPPluginSession Executing Starts");
		if (!sessionAlive) {
			if (logger.isErrorEnabled())
				logger.error("Try to execute the non-alive FTPPluginSession");
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, "The FTPPluginSession is not alive");
		}
		String remotePath = null;
		if (executorParameter instanceof PluginArrayExecutorParameter) {
			Parameter[] parameters = ((PluginArrayExecutorParameter) executorParameter).getParameters();
			for (Parameter parameter : parameters) {
				switch (parameter.getKey()) {
				case FTPPLUGIN_PATH:
					remotePath = parameter.getValue();
					break;
				default:
					if (logger.isWarnEnabled())
						logger.warn("Unkown execute parameter key : " + parameter.getKey());
					break;
				}
			}
		}
		// initialize the resultSet
		PluginResultSet result = new PluginResultSet();
		result.putValue(0, 0, name);
		result.putValue(0, 1, address.getHostAddress());
		result.putValue(0, 2, String.valueOf(port));

		ftpClient.setControlEncoding("GBK");
		ftpClient.setAutodetectUTF8(true);
		long connectTime, loginTime;
		double downloadSpeed;

		if ((connectTime = testConnect()) >= 0) {
			result.putValue(0, 3, String.valueOf(AvailableStateEnum.Normal.getStateVal()));
			result.putValue(0, 4, String.valueOf(connectTime));
			if ((loginTime = testLogin(username, password)) >= 0) {
				result.putValue(0, 5, String.valueOf(loginTime));
				if (!StringUtils.isEmpty(remotePath)) {
					if ((downloadSpeed = testDownload(remotePath)) >= 0) {
						result.putValue(0, 6, String.valueOf(downloadSpeed));
						result.putValue(0, 3, String.valueOf(AvailableStateEnum.Normal.getStateVal()));
					}
				} 
			}
		}else{
			result.putValue(0, 3, String.valueOf(AvailableStateEnum.Critical.getStateVal()));
		}
		result.putValue(0, 7, ftpClient.getReplyString());

		try {
			if (ftpClient.isConnected())
				ftpClient.disconnect();
		} catch (IOException e) {
			if (logger.isErrorEnabled())
				logger.error("IOException: Fail to close the connection", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("FTPPluginSession Executing Finished");
		return result;
	}

	// return the connect time
	private long testConnect() {
		if (ftpClient == null)
			return -1;
		try {
			long start = System.currentTimeMillis();
			ftpClient.connect(address, port);
			long end = System.currentTimeMillis();
			return end - start;
		} catch (IOException e) {
			if (logger.isErrorEnabled())
				logger.error("IOException: Fail to connect to the server", e);
			return -1;
		}
	}

	private long testLogin(String username, String password) {
		if (ftpClient == null)
			return -1;
		ftpClient.enterLocalPassiveMode();
		long start = 0, end = -1;
		boolean flag;
		try {
			start = System.currentTimeMillis();
			flag = ftpClient.login(username, password);
			end = System.currentTimeMillis();
		} catch (IOException e) {
			if (logger.isErrorEnabled())
				logger.error("IOException: Fail to login the server", e);
			flag = false;
		}
		if (!flag)
			return -1;
		return end - start;
	}

	private double testDownload(String path) {
		if (ftpClient == null)
			return -1;
		File tempFile = new File("temp" + System.currentTimeMillis());
		try {
			FileOutputStream tempStream = new FileOutputStream(tempFile);
			long start = System.currentTimeMillis();
			if (!ftpClient.retrieveFile(path, tempStream)){
				tempStream.close();
				return -1;
			}
			long end = System.currentTimeMillis();;
			tempStream.close();
			return (double) tempFile.length() * 1000 / (end - start);
		} catch (FileNotFoundException e) {
			if (logger.isErrorEnabled())
				logger.error("IOException: Fail to create the temp file", e);
			return -1;
		} catch (IOException e) {
			if (logger.isErrorEnabled())
				logger.error("IOException: Fail to download the file", e);
			return -1;
		} finally {
			
			tempFile.delete();
		}
	}

	@Override
	public boolean check(PluginInitParameter initParameters) throws PluginSessionRunException {
		return false;
	}
}
