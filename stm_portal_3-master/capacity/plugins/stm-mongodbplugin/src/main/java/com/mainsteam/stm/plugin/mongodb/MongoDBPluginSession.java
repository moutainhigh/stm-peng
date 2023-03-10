package com.mainsteam.stm.plugin.mongodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDBPluginSession implements PluginSession {

	private static final Log logger = LogFactory
			.getLog(MongoDBPluginSession.class);

	public static final String JDBCPLUGIN_IP = "IP";
	public static final String JDBCPLUGIN_JDBC_PORT = "jdbcPort";
	public static final String JDBCPLUGIN_DB_USERNAME = "dbUsername";
	public static final String JDBCPLUGIN_DB_PASSWORD = "dbPassword";
	public static final String JDBCPLUGIN_DB_NAME = "dbName";

	public static final String REGEX = ",";
	private static final String SHELL = "shell";
	private static final String PARAMETER = "parameter";

	private String ipaddress;
	private int port;
	private String username;
	private String password;
	private String dbType;
	private String dbname;
	private boolean isAlive = false;
	
	private MongoClient mongoClient;
	private DB db;

	@Override
	public void init(PluginInitParameter initP)
			throws PluginSessionRunException {

		Parameter[] initParameters = initP.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case JDBCPLUGIN_IP:
				this.ipaddress = initParameters[i].getValue();
				break;
			case JDBCPLUGIN_JDBC_PORT:
				this.port = Integer.parseInt(initParameters[i].getValue());
				break;
			case JDBCPLUGIN_DB_USERNAME:
				this.username = initParameters[i].getValue();
				break;
			case JDBCPLUGIN_DB_PASSWORD:
				this.password = initParameters[i].getValue();
				break;
			case JDBCPLUGIN_DB_NAME:
				this.dbname = initParameters[i].getValue();
				break;
			default:
				if (logger.isWarnEnabled()) {
					logger.warn("warn:unkown initparameter "
							+ initParameters[i].getKey() + "="
							+ initParameters[i].getValue());
				}
				break;
			}
		}

		db = getMongoDb();
		this.isAlive = true;
	}

//	???????????????
	@SuppressWarnings({ "deprecation", "resource" })
	public DB getMongoDb() {
		disconnect();
		mongoClient = new MongoClient(ipaddress, port);
		db = mongoClient.getDB(dbname);
		return db;
	}

//	????????????
	public void disconnect() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}
	
	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return false;
	}

	@Override
	public void destory() {
		this.isAlive = false;
		disconnect();
	}

	@Override
	public void reload() {

	}

	@Override
	public boolean isAlive() {
		return this.isAlive;
	}

	
	
	public String runCommand(String command, List<String> parameter) {
		String result = "";
		String para = "";
		CommandResult commandResult = db.command(command);
		Map<String, Object> firstResMap = null;
		if (parameter.size() == 1) {
//			??????parameter????????????1???????????????????????????
			result = commandResult.getString(parameter.get(0));
		} else {
//			??????parameter????????????????????????????????????????????????"???,??????",???"?????????"??????"???"??????????????????"?????????"?????????,???????????????????????????????????????????????????????????????????????????
			try {
//				???commandResult???????????????????????????????????????????????????commandResult??????????????????Map,??????ClassCastException?????????
//				??????????????????????????????????????????????????????????????????
				firstResMap = (Map<String, Object>) commandResult.get(parameter.get(0));
				for (int i = 1; i < parameter.size(); i++) {
					para = parameter.get(i);
//					??????
					firstResMap = (Map<String, Object>) firstResMap.get(para);
				}
//				????????????????????????????????????????????????????????????????????????????????????????????????????????????
				result = firstResMap.toString();
			} catch (ClassCastException e) {
				result = firstResMap.get(para).toString();
			}

		}
		return result;
	}

	@Override
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext context) throws PluginSessionRunException {

		if (logger.isTraceEnabled()) {
			logger.trace("execute start");
		}
		PluginResultSet result = new PluginResultSet();
		String eCommand = "";
		String paras = "";
		if (executorParameter instanceof PluginArrayExecutorParameter) {
			try {
				PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParameter;
				Parameter[] parameters = arrayP.getParameters();
				List<String> commands = new ArrayList<String>(parameters.length);// shell??????
				List<String> parates = new ArrayList<String>();// shell??????????????????
				for (Parameter parameter : parameters) {
					if (parameter.getKey().equalsIgnoreCase(SHELL)) {
						commands.add(parameter.getValue());
					} else if (parameter.getKey().equalsIgnoreCase(PARAMETER)) {
						paras = parameter.getValue();
					}
				}
				String[] parasArrays = paras.split(REGEX);
				parates = Arrays.asList(parasArrays);

				if (!commands.isEmpty() || (commands.size() != 0)) {
					for (String command : commands) {
						eCommand = command;
						String commandResult = this.runCommand(eCommand,
								parates);
						result.putValue(0, 0, commandResult);
						return result;
					}
				}
			} catch (Exception e) {
				logger.error("db_ip:" + this.ipaddress + ",db_type:"
						+ this.dbType + ",db_user:" + this.username
						+ ",db_name:" + this.dbname + ",db_command:" + eCommand
						+ ",error message:" + e.getMessage(), e);
			}

		}

		return result;
	}

}
