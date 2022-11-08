package com.mainsteam.stm.plugin.cache;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.intersys.objects.CacheDatabase;
import com.intersys.objects.CacheException;
import com.intersys.objects.CacheQuery;
import com.intersys.objects.Database;

/**
 * Caché 数据库采集插件
 * @author Administrator
 *
 */
public class CachePluginSession implements PluginSession  {
	
	private static final Log logger = LogFactory.getLog(CachePluginSession.class);
	//常量值
	public static final String CACHE_IP = "IP";
	public static final String CACHE_PORT = "cachePort";
	public static final String CACHE_DB_NAME = "cacheDBName";
	public static final String CACHE_USERNAME = "cacheUsername";
	public static final String CACHE_PASSWORD = "cachePassword";
	public static final String CACHE_NAMESPACE = "CacheNamespace";
	//模型配置的关键字
	private static final String COMMAND = "COMMAND";
//	private static final String SQL_SELECT = "select";
//	private static final String SQL_FROM = "from";
//	private static final String SQL_WHERE = "where";
//	private static final String SQL_AND = "and";
	private static final String CLASS_SEPERATOR = ".";
	private static final String AVAIL_COMMAND = "show avail";
	private static final String AVAILABLE = "1";
	private static final String NOT_AVAILABLE = "0";
//	private static final String EQUAL_SEPERATOR = "=";
	//发现参数
	private String ip;
	private int port;
	private String dbName;
	private String username;
	private String password;
	private String namespace;
	private boolean isAlive;
	private Database database;
	

	@Override
	public void init(PluginInitParameter initP)
			throws PluginSessionRunException {
		if(logger.isDebugEnabled()){
			logger.debug("Caché plugin session start initing.");
		}
		Parameter[] initParameters = initP.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case CACHE_IP:
				this.setIp(StringUtils.trim(initParameters[i].getValue()));
				break;
			case CACHE_PORT:
				this.setPort(
						Integer.parseInt(StringUtils.trim(initParameters[i].getValue())));
				break;
			case CACHE_DB_NAME:
				this.setDbName(StringUtils.trim(initParameters[i].getValue()));
				break;
			
			case CACHE_NAMESPACE:
				this.setNamespace(StringUtils.trim(initParameters[i].getValue()));
				break;
				
			case CACHE_USERNAME:
				this.setUsername(StringUtils.trim(initParameters[i].getValue()));
				break;
				
			case CACHE_PASSWORD:
				this.setPassword(StringUtils.trim(initParameters[i].getValue()));
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
		this.getCacheConnection();
		this.isAlive = true;
	}
	
	@Override
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext context) throws PluginSessionRunException {
		
		if (executorParameter instanceof PluginArrayExecutorParameter) {

			PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParameter;
			Parameter[] parameters = arrayP.getParameters();
			if(parameters == null){
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLCOMMAND,"没有采集命令");
			}
			List<String> cmds = new ArrayList<String>(1);//采集命令
			Map<String, String> paramMap = new HashMap<String, String>(3, 0.5f);
			for (Parameter parameter : parameters) {
				if(logger.isInfoEnabled())
					logger.info("plugin excute[" + parameter.getKey() + ":" + parameter.getValue() + "]");
				if (StringUtils.equalsIgnoreCase(parameter.getKey(), COMMAND)) {
					cmds.add(parameter.getValue());
				}  else //参数项,key为属性名称，value为属性值
					paramMap.put(parameter.getKey(), parameter.getValue());
			}
			
//			if(cmds.isEmpty()) { //没有SQL查询语句，如果发现参数是ip+port的话，直接拼接后返回
//				if(paramMap.size() == DOUBLE_PARAMS) {
//					Set<String> keySet = paramMap.keySet();
//					if(keySet.contains(IP) && keySet.contains(POCOMMANDRT)){
//						PluginResultSet resultSet = new PluginResultSet();
//						resultSet.putValue(0, 0, this.parameter.getIp() + ":" + this.parameter.getPort());
//						return resultSet;
//					}
//				}
//				
//			}
			
			Set<String> keySet = paramMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				for(int i = 0; i < cmds.size(); i++){
					String cmd = cmds.get(i);
					cmd = StringUtils.replaceOnce(cmd, "${" + key + "}", paramMap.get(key));
					cmds.set(i, cmd);
				}
			}
			
			PluginResultSet resultSet = new PluginResultSet();
			for(String cmd : cmds){
				try{
					Object obj = this.executeCommand(cmd);
					this.putValues(resultSet, obj);
				}catch(Exception e){
					if(logger.isWarnEnabled()) {
						logger.warn(e.getMessage(), e);
					}
					this.destory();
					if(e instanceof PluginSessionRunException)
						throw (PluginSessionRunException)e;
					else
						throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED,"采集异常",e);
				}
			}
			return resultSet;
		} else {
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER,"采集参数为空");
		}
	}
	
	//执行命令
	private Object executeCommand(String command) throws PluginSessionRunException {
//		//首先需要分割command，按照分割SQL的方式进行，首先需要分割出select和from之间的属性列
//		String propertieStr = StringUtils.substringBetween(command, SQL_SELECT, SQL_FROM);
//		if(StringUtils.isNotBlank(propertieStr)){
//			String[] properties = StringUtils.split(propertieStr, COMMA_SEPERATOR);
//			String classStr = null;
//			if(StringUtils.containsOnly(command, SQL_WHERE)) {
//				classStr = StringUtils.substringBetween(command, SQL_FROM, SQL_WHERE);
//			}else
//				classStr = StringUtils.substringAfter(command, SQL_FROM);
//			String conditionStr = StringUtils.substringAfter(command, SQL_WHERE);
//			String[] conditions = null;
//			if(StringUtils.isNotBlank(conditionStr)){
//				conditions = StringUtils.split(conditionStr, SQL_AND);
//			}
//			Map<String, String> conditionMap = new HashMap<String, String>(1, 0.5f);
//			if(conditions != null){
//				for(String condition : conditions) {
//					String[] subCondition = StringUtils.split(condition, EQUAL_SEPERATOR);
//					if(subCondition != null){
//						conditionMap.put(StringUtils.trim(subCondition[0]), StringUtils.trim(subCondition[1]));
//					}
//				}
//			}
		//可用性判断
		if(StringUtils.equals(command, AVAIL_COMMAND)) {
			if(this.isAlive())
				return AVAILABLE;
			else
				return NOT_AVAILABLE;
		}
		String sql_match = "select\\s+\\S+\\s+from\\s+\\S+"; //匹配SQL语句
		Pattern pattern = Pattern.compile(sql_match);
		if(pattern.matcher(command).find()) { //SQL查询
			CacheQuery query;
			try {
				query = new CacheQuery(this.database, command);
				ResultSet resultSet = query.execute();
				return resultSet;
			} catch (CacheException e) {
				if(logger.isWarnEnabled()){
					logger.warn(e.getMessage(), e);
				}
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, e);
			}
		}else{//查询类及其属性
			return this.invokeCacheMethod(command);
		}
	}
	
	//通过反射调用读取Cache类属性，还有一种是通过SQL进行查询
	private Object invokeCacheMethod(String command) throws PluginSessionRunException {
		
		String anyClass = StringUtils.substringBeforeLast(command, CLASS_SEPERATOR); //截取到类
		String anyMethod = StringUtils.substringAfterLast(command, CLASS_SEPERATOR); //截取到方法
		
		Class<?> thisClass;
		try {
			thisClass = Class.forName(anyClass);
		} catch (ClassNotFoundException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + ".Cache collects failed,cause of can't find "
						+ "the class and method:" + command, e);
			}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLCOMMAND, e);
		}
		
		Object object;
		try {
			object = thisClass.newInstance();
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + ". The class [" + command + "] instantiation failed. Maybe this"
						+ "class has not null constructor method or can't access the constructor method.", e);
			}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_ERROR_COMMAND, e);
		} 
		
		Method method;
		try {
			method = thisClass.getMethod(anyMethod, Object[].class);
		} catch (NoSuchMethodException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + ".Cache class method error,cause of can't find "
						+ "the method of class :" + command, e);
			}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLCOMMAND, e);
		} catch (SecurityException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + ". Cause of java security,the failed class and method:" + command, e);
			}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_JAVA_SECURITY, e);
		}
		
		Object resultObj = null;
		try {
			Object args = Array.newInstance(Object.class, 2);  
            Array.set(args, 0, this.database);  
            Array.set(args, 1, this.dbName); 
			resultObj = method.invoke(object, args);
			return resultObj;
		} catch (IllegalAccessException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + ".Can't access the method of class[" + command + "]", e);
			}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_ACCESS_METHOD, e);
			
		} catch (IllegalArgumentException e) {
			/*
			 * 如果该方法是实例方法，且指定对象参数不是声明底层方法的类或接口（或其中的子类或实现程序）的实例；
			 * 如果实参和形参的数量不相同；如果基本参数的解包转换失败；如果在解包后，无法通过方法调用转换将参数值转换为相应的形参类型。
			 */
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage() + ". Invoke method failed ["+ command + "]", e);
			}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_METHOD_OF_PARAMS, e);
		} catch (InvocationTargetException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + ". Invoke method error.[" + command + "]", e);
			}
			Throwable throwable = e.getTargetException();
			if(throwable instanceof PluginSessionRunException)
				throw (PluginSessionRunException)throwable;
			else
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_REFLECT_FAILED, e);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + ". Invoke method error.[" + command + "]", e);
			}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, e);
		}
		
	}
	
	private void putValues(PluginResultSet result, Object ret) {
		// 因为可能执行多条SQL，所以需要在处理结果集数据前先判断，如果当前结果集已经存在数据则表示之前已经执行过数据，这时仅新增列即可。实际上多条命令的执行也是为了生成多列
		boolean isNewInsert = true;
		if (result.getRowLength() > 0 || result.getColumnLength() > 0)
			isNewInsert = false;

		if (ret == null) {
			if (isNewInsert)
				result.putValue(0, 0, null);
		} else {
			int resultRowIndex = 0; // SQL结果集的行下标
			int ColumnIndex = result.getColumnLength(); // 当前结果集的列数
			if(ret instanceof ResultSet){
				ResultSet jdbcResult = (ResultSet)ret;
				try{
					int resultColumnCount = jdbcResult.getMetaData().getColumnCount();
					while (jdbcResult.next()) {
						for (int i = 1; i <= resultColumnCount; i++) {
							String str = jdbcResult.getString(i);
							if (isNewInsert)
								result.putValue(resultRowIndex, i-1, str);
							else {
								result.putValue(resultRowIndex, ColumnIndex + i-1, str);
							}
						}
						resultRowIndex++;
					}
					
				}catch(SQLException e){
					e.printStackTrace();
					if(logger.isWarnEnabled()){
						logger.warn(e.getMessage(), e);
					}
					return;
				}
				
			}else{
				if (isNewInsert)
					result.putValue(resultRowIndex, 0, ret != null?ret.toString():null);
				else {
					result.putValue(resultRowIndex, ColumnIndex + 1, ret != null?ret.toString():null);
				}
				
			}

		}

	}
	
	@Override
	public void destory() {
		this.isAlive = false;
		try {
			if(this.database != null)
				this.database.close();
		} catch (CacheException e) {
			this.database = null;
		}
	}

	@Override
	public boolean isAlive() {
		if(this.database != null && this.database.isOpen()) {
			this.isAlive = true;
		}else
			this.isAlive = false;
		return this.isAlive;
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return true;
	}

	@Override
	public void reload() {
		
	}
	
	/**
	 * 获取Cache数据库连接
	 * @return
	 */
	private void getCacheConnection() throws PluginSessionRunException {
		String jdbcUrl = "jdbc:Cache://"+this.ip+":"+this.port+"/"+this.namespace;
		try {
			database = CacheDatabase.getDatabase(jdbcUrl, this.username, this.password);
		} catch (CacheException e) {
			if(logger.isWarnEnabled()){
				logger.warn("Cache Database gets connection failed." + e.getMessage() + "ip/port:" + this.ip
						+ "/" + this.port + ",username is " + this.username + ",password is " + this.password
						+ ",namespace is " + this.namespace + ",dbname is " + this.dbName, e);
			}
			if(database != null)
				try {
					database.close();
				} catch (CacheException e1) {
					database = null;
				}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED, e);
		}
	}
	

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

}
