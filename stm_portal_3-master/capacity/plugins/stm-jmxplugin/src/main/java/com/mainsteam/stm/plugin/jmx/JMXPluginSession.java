package com.mainsteam.stm.plugin.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

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

/**
 * JMX采集插件
 * @author xiaop_000
 *
 */
public class JMXPluginSession implements PluginSession {
	
	private static final String REGEX = ",";

	private static final String MULTI = "multi";

	private static final String ATTRIBUTES2 = "attributes";

	private static final String OBJECT_NAME = "objectName";

	private static final Log logger = LogFactory.getLog(JMXPluginSession.class);
	
	public static final String JMX_PLUGIN_PASSWORD = "jmxPassword";

	public static final String JMX_PLUGIN_USERNAME = "jmxUsername";

	public static final String JMX_PLUGIN_PORT = "jmxPort";

	public static final String JMX_PLUGIN_IP = "IP";
	
	public static final String JMX_PLUGIN_CONNECTORNAME = "connectorName";
	
	public static final String JMX_PLUGIN_AUTHTYPE = "authtype";
	
	public static final String JMX_APUSIC = "Apusic";
	
	private String authtype = null;
	
	private String ip;
	
	private int port;
	
	private String username;
	
	private String password;
	//JMX连接名称
	private String connectorName = null;
	
	private JMXConnector jmxConnector = null;
	
	private MBeanServerConnection mBeanServerConnection = null;
	
	private boolean isAlive = false;
	//连接服务器的IP地址
	private String newip =null;
	
	public JMXPluginSession(){
		
	}
	
	public JMXPluginSession(String ip, int port, String username, String password){
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext context) throws PluginSessionRunException {
		
		PluginResultSet result = new PluginResultSet();
		if (executorParameter instanceof PluginArrayExecutorParameter) {
			try {
				PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParameter;
				Parameter[] parameters = arrayP.getParameters();
				String objectName = null;
				String attributes = null;
				// 判断参数
				Map<String, String> replaces = new HashMap<String, String>(2); //参数替换
				Boolean isMulti = false;//标志是否查询多个MBean，一般用于子资源
				for (Parameter parameter : parameters) {
					
					if (parameter.getKey().equalsIgnoreCase(OBJECT_NAME)) {
						objectName = StringUtils.trimToEmpty(parameter.getValue());
						
					} else if (parameter.getKey().equalsIgnoreCase(ATTRIBUTES2)) {
						attributes = StringUtils.trimToEmpty(parameter.getValue());
					} else if(parameter.getKey().equalsIgnoreCase(MULTI)){
						isMulti = true;
					}else if(parameter.getKey().equalsIgnoreCase(JMX_PLUGIN_IP)){
						result.addRow(new String[]{newip});
						return result;
					}
					else{
						replaces.put(parameter.getKey(), parameter.getValue());
					}
				}
				if(StringUtils.isEmpty(objectName)){
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLCOMMAND,
							"Empty JMX objectName");
				}
				if(!replaces.isEmpty()) {
					Set<String> keySet = replaces.keySet();
					Iterator<String> iterator = keySet.iterator();
					while(iterator.hasNext()) {
						String key = iterator.next();
						objectName = StringUtils.replace(objectName, "${" + key + "}", StringUtils.trimToEmpty(replaces.get(key)));
					}
				}
				String[] attributeArray = null;
				if(StringUtils.isNotBlank(attributes))
					attributeArray = attributes.split(REGEX);
				else
					attributeArray = new String[]{};
				if(isMulti) {
					String[][] resultArray = this.queryMBeans(objectName, attributeArray);
					if(resultArray != null) {
						for(String[] array : resultArray) {
							result.addRow(array);
						}
					}
				}else{
					String[] resultArray = this.queryMBean(objectName, attributeArray);
					if(resultArray != null) {
						result.addRow(resultArray);
					}
				}
				
			} catch (Exception e) {
				if(logger.isWarnEnabled()) {
					logger.warn(e.getMessage(), e);
				}
				throw new PluginSessionRunException(
						CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED,
						e.getMessage(), e);
			}
			return result;
		}
		
		
		return null;
	}
	
	private MBeanServerConnection createSession() throws PluginSessionRunException {
		String jmxUrl = null;
		if (JMX_APUSIC.equals(authtype)) {
			connectorName = "#jmx/rmi/RMIConnectorServer";
			jmxUrl = "service:jmx:iiop:///jndi/corbaname::1.2@"+ip+":"+port+connectorName;
		}else {
			connectorName = "jmxrmi";
			jmxUrl = "service:jmx:rmi:///jndi/rmi://" + ip + ":" + port + "/" + connectorName; 
		}
		String credentials[] = new String[] { username, password };
		Map<String, String[]> env = new HashMap<String, String[]>();
		env.put(JMXConnector.CREDENTIALS, credentials);
		try {
			JMXServiceURL jmxServiceURL = new JMXServiceURL(jmxUrl);
			jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, env);
			mBeanServerConnection = jmxConnector.getMBeanServerConnection();
		
		} catch (MalformedURLException e) {
			if(logger.isWarnEnabled()){
				logger.warn("JMX connection failed. " + this.toString(), e);
			}
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_URL_ERR, e);
		} catch (IOException e) {
			if(logger.isWarnEnabled()){
				logger.warn("JMX connection IO Exception. " + this.toString(), e);
			}
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_JMX_CONN, e);
		}
		return mBeanServerConnection;
	}
	
	/**
	 * 查询多个MBean的情况，多用于子资源的查询
	 * @param objectNameStr
	 * @param attributeStrs
	 * @return
	 * @throws ReflectionException 
	 * @throws InstanceNotFoundException 
	 * @throws PluginSessionRunException 
	 */
	private String[][] queryMBeans(String objectNameStr, String[] attributeStrs) 
			throws MalformedObjectNameException,IOException, InstanceNotFoundException, ReflectionException, 
			PluginSessionRunException {
		ObjectName objectName = null;
		try {
			objectName = new ObjectName(objectNameStr);
		} catch (MalformedObjectNameException e) {
			if(logger.isWarnEnabled()) {
				logger.warn(e.getMessage() + ".ObjectName syntax error:" + objectNameStr + "." + this.toString(), e);
			}
			throw e;
		}
		Set<ObjectName> objectNameSet = null;
		try {
			objectNameSet = mBeanServerConnection.queryNames(objectName, null);
		} catch (IOException e) {
			if(logger.isWarnEnabled()){
				logger.warn(e.getMessage() + ".Occurs error when communicating with MBean server.Objectname is [" +
						objectName + ",attributes is [" + attributeStrs + "]",e);
			}
			//重新连接一次
			this.destory();
			try{
				this.createSession();
			}catch(PluginSessionRunException e1){
				throw e1;
			}
			objectNameSet = mBeanServerConnection.queryNames(objectName, null);
		}
		if(objectNameSet != null){
			Iterator<ObjectName> iterator = objectNameSet.iterator();
			String[][] resultArray = new String[objectNameSet.size()][];
			int index = 0;
			while(iterator.hasNext()) {
				ObjectName subObjectName = iterator.next();
				String[] temp = this.getAttributes(subObjectName, attributeStrs, false);
				String propertyListString = subObjectName.getKeyPropertyListString();
				if(temp != null && temp.length > 0) {
					String[] result = new String[temp.length+1];
					result[0] = propertyListString;
					for(int i = 1; i<result.length; i++){
						result[i] = temp[i-1];
					}
					resultArray[index] = result;
				}else{
					resultArray[index] = new String[]{propertyListString};
				}
				index ++;
			}
			return resultArray;
		}
		return null;
	}
	
	
	/**
	 * 查询MBean的数据
	 * @param objectNameStr
	 * @param attributes
	 * @return
	 * @throws PluginSessionRunException 
	 */
	private String[] queryMBean(String objectNameStr, String[] attributes) 
			throws MalformedObjectNameException,InstanceNotFoundException,ReflectionException,IOException,
			PluginSessionRunException {
		if(objectNameStr != null && attributes != null){
			ObjectName objectName = null;
			try {
				objectName = new ObjectName(objectNameStr);
			} catch (MalformedObjectNameException e) {
				if(logger.isWarnEnabled()) {
					logger.warn(e.getMessage() + ".ObjectName syntax error:" + objectNameStr + "." + this.toString(), e);
				}
				throw e;
			}
			return this.getAttributes(objectName, attributes, true);
		}
		return null;
	}
	
	/**
	 * 获取MBean 属性
	 * @param objectName
	 * @param attributes
	 * @param isRetry
	 * @return
	 * @throws InstanceNotFoundException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws PluginSessionRunException
	 */
	private String[] getAttributes(ObjectName objectName, String[] attributes, boolean isRetry) 
			throws InstanceNotFoundException, ReflectionException, IOException, PluginSessionRunException {
		String[] attributeValues = new String[attributes.length];
		try {
			AttributeList attributeList = mBeanServerConnection.getAttributes(objectName, attributes);
			if(attributeList != null && !attributeList.isEmpty()){
				List<Attribute> asList = attributeList.asList();
				for(int i = 0; i < asList.size(); i++) {
					Object attribute = asList.get(i).getValue();
					String result = DealWithData(attribute);
					attributeValues[i] = result;
				}
			}else{
				if(logger.isWarnEnabled()) {
					logger.warn("Can not get attributes with this ObjectName." + this.toString());
				}
			}
		} catch (InstanceNotFoundException e) {
			if(logger.isWarnEnabled()){
				logger.warn(e.getMessage() + ".ObjectName has not been registered in the server.Objectname is [" +
						objectName + ",attributes is [" + attributes + "]",e);
			}
			throw e;
		} catch (ReflectionException e) {
			if(logger.isWarnEnabled()){
				logger.warn(e.getMessage() + ".Throws exception when trying to invoke the Dynamic MBean.Objectname is [" +
						objectName + ",attributes is [" + attributes + "]",e);
			}
			throw e;
		} catch (IOException e) {
			if(logger.isWarnEnabled()){
				logger.warn(e.getMessage() + ".Occurs error when communicating with MBean server.Objectname is [" +
						objectName + ",attributes is [" + attributes + "]",e);
			}
			if(isRetry) {
				//重新连接一次
				this.destory();
				try{
					this.createSession();
				}catch(PluginSessionRunException e1){
					throw e1;
				}
				this.getAttributes(objectName, attributes, false);
			}
			throw e;
		}
		
		return attributeValues;
	}
	
	
	/**
	 * 处理JMX的返回结果。主要是处理CompositeDataSupport(复合数据类型)、TabularDataSupport(表格数据类型)
	 * @param object
	 * @return
	 */
	private String DealWithData(Object object) {
		if(object instanceof CompositeDataSupport) {//复合数据类型，里面包含了Integer,Long,Boolean,Date等等
			StringBuffer sb = new StringBuffer();
			CompositeDataSupport compositeDataSupport = (CompositeDataSupport)object;
			CompositeType compositeType = compositeDataSupport.getCompositeType();
			Set<String> keySet = compositeType.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()) {
				String key = iterator.next();
				sb.append(key).append("=").append(compositeDataSupport.get(key)).append(";");
			}
			return sb.toString();
		}else if(object instanceof TabularDataSupport) {//表格数据类型，每行都是一个CompositeDataSupport
			StringBuffer sb = new StringBuffer();
			TabularDataSupport tabularDataSupport = (TabularDataSupport)object;
			Set<Entry<Object, Object>> entrySet = tabularDataSupport.entrySet();
			Iterator<Entry<Object, Object>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Entry<Object, Object> entry = iterator.next();
				Object key = entry.getKey();
				List<?> keyList = (List<?>)key;
				if(keyList != null && !keyList.isEmpty()) {
					Object[] keys = new Object[keyList.size()];
					keyList.toArray(keys);
					CompositeData compositeData = tabularDataSupport.get(keys);
					CompositeType compositeType = compositeData.getCompositeType();
					Set<String> keySet = compositeType.keySet();
					Iterator<String> compositeTypeIterator = keySet.iterator();
					while(compositeTypeIterator.hasNext()) {
						String compositeTypeKey = compositeTypeIterator.next();
						sb.append(compositeTypeKey).append("=").append(compositeData.get(compositeTypeKey)).append(";");
					}
					sb.append("\n");
				}
			}
			return sb.toString();
		}
		 //Simple Data Types,such as Integer,Long,Boolean,Date
		return object.toString();
	}
	
	@Override
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case JMX_PLUGIN_IP:
				this.ip = StringUtils.trimToNull(initParameters[i].getValue());
				newip = ip;
				break;
			case JMX_PLUGIN_PORT:
				try{
					this.port = Integer.parseInt(initParameters[i].getValue());  
				}catch(Exception e){
					throw new PluginSessionRunException(
							CapcityErrorCodeConstant.ERR_CAPCITY_JMX_PORT, e);
				}
				break;
			case JMX_PLUGIN_USERNAME:
				this.username = StringUtils.trimToEmpty(initParameters[i].getValue()); 
				break;
			case JMX_PLUGIN_PASSWORD:
				this.password = StringUtils.trimToEmpty(initParameters[i].getValue()); 
				break;
			case JMX_PLUGIN_CONNECTORNAME:
				if(StringUtils.isNotBlank(initParameters[i].getValue()))
					this.connectorName = StringUtils.trimToEmpty(initParameters[i].getValue());
				break;
			case JMX_PLUGIN_AUTHTYPE:
				this.authtype = StringUtils.trimToEmpty(initParameters[i].getValue());
				break;
			default:
				if (logger.isWarnEnabled()) {
						logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
								+ initParameters[i].getValue());
					}
					break;
			}
		}
		this.createSession();
		this.isAlive = true;
	}
	
	@Override
	public void destory() {
		this.isAlive = false;
		try {
			mBeanServerConnection = null;
			this.jmxConnector.close();
		} catch (IOException e) {
			this.jmxConnector = null;
		}
	}

	@Override
	public void reload() {
	}

	@Override
	public boolean isAlive() {
		return this.isAlive;
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ip/port:").append(this.ip).append("/").append(this.port).append(",username is ").append(this.username);
		return sb.toString();
	}

}
