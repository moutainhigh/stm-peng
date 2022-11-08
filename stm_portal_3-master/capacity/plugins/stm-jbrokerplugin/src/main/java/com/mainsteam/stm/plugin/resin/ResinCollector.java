package com.mainsteam.stm.plugin.resin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class ResinCollector {
	private static final String OBJECTNAME_TYPE_SERVER = "resin:type=Server";
	private static final String OBJECTNAME_WEB_APP = "resin:type=WebApp,*";
	private static final String OBJECTNAME_CONNECTION_POOL = "resin:type=ConnectionPool,*";
	private static final String OBJECTNAME_THREADPOOL = "resin:type=ThreadPool";
	private static final String OBJECTNAME_PORT = "resin:type=Port,*";
	private static final String OBJECTNAME_RESIN = "resin:type=Resin";
	private static final String OBJECTNAME_OS = "java.lang:type=OperatingSystem";
	
	private static final String ATTRIBUTE_RESIN_HOME = "ResinHome";
	private static final String ATTRIBUTE_LOCAL_HOST = "LocalHost";
	private static final String ATTRIBUTE_NAME = "Name";
	private static final String ATTRIBUTE_STATE = "State";
	private static final String ATTRIBUTE_VERSION = "Version";
	
	private static final Log log = LogFactory.getLog(ResinCollector.class);
	private JMXConnector conn;
	private JBrokerParameter obj;
	public ResinCollector(JBrokerParameter obj) {
			this.obj=obj;
	}
	
	public String getAvailability() {
		try {
			getJMXConnector();
		} catch (Exception e) {
			log.error("getAvailability error!"+e.getMessage(), e);
		} 
		return isAvailability()?"1":"0";
	}
	
	private boolean isAvailability() {
		try {
			ObjectName name = new ObjectName(OBJECTNAME_TYPE_SERVER);
			String str = conn.getMBeanServerConnection().getAttribute(name, ATTRIBUTE_STATE).toString();
			boolean isAva=str.equalsIgnoreCase("active");
			return isAva;
		} catch (Exception e) {
			log.error("getAvailability error!"+e.getMessage(), e);
			
		}
		return false;
	}
	
	public String getResDisplayName() {
		return obj.getIp() + ":" + obj.getPort();
	}
	
	public String getVersion() {
		try {
			ObjectName objectName=new ObjectName(OBJECTNAME_RESIN);
			String result=getJMXConnector().getMBeanServerConnection().getAttribute(objectName, ATTRIBUTE_VERSION).toString();
			return result;
		} 
		 catch (Exception e) {
				log.error("get resin version error!"+e.getMessage(),e);
			}
		return "";
	}
	
	public String getOS() {
		try {
			ObjectName objName = new ObjectName(OBJECTNAME_OS);
			String result= getJMXConnector().getMBeanServerConnection().getAttribute(objName, ATTRIBUTE_NAME).toString();
			return result;
		} catch (Exception e) {
			log.error("getOS error!"+e.getMessage(), e);
		}
		return "";
	}
	
	public String getHostName() {
		try {
			ObjectName objName = new ObjectName(OBJECTNAME_RESIN);
			return getJMXConnector().getMBeanServerConnection().getAttribute(objName, ATTRIBUTE_LOCAL_HOST).toString();
		} catch (Exception e) {
			log.error("getHostName error!"+e.getMessage(), e);
		}
		return "";
	}
	
	public String getHomeDir() {
		try {
			ObjectName objName = new ObjectName(OBJECTNAME_RESIN);
			String home = getJMXConnector().getMBeanServerConnection().getAttribute(objName, ATTRIBUTE_RESIN_HOME).toString();
			home = home.replaceAll("\\\\", "/");
			return home;
		} catch (Exception e) {
			log.error("getHomeDir error!"+e.getMessage(), e);
		}
		return "";
	}
	
	public String getHttpPort() {
		try {
			ObjectName obj = new ObjectName(OBJECTNAME_PORT);
			Set<ObjectName> instances = getJMXConnector().getMBeanServerConnection().queryNames(obj, null);
			for(ObjectName name : instances) {
				Map<String, String> attrs = ResinJmxUtil.getAttributes(getJMXConnector().getMBeanServerConnection(), name, "Port", "ProtocolName");
				if(attrs.get("ProtocolName").equals("http")) 
					return attrs.get("Port");
			}
		} catch (Exception e) {
			log.error("getHttpPort error!"+e.getMessage(), e);
		}
		return "";
	}
	
	public String getThreadPoolOverView() {
		try {
			ObjectName name = new ObjectName(OBJECTNAME_THREADPOOL);
			Map<String, String> attrs = ResinJmxUtil.getAttributes(getJMXConnector().getMBeanServerConnection(), name, "ThreadMax", "ThreadActiveCount");
			List<Map<String, String>> list = new ArrayList<Map<String,String>>();
			list.add(attrs);
			return ResinJmxUtil.attrMapsToString(list, new String[]{"ThreadMax", "ThreadActiveCount"});
		} catch (Exception e) {
			log.error("getThreadPoolOverView error!"+e.getMessage(), e);
		}
		return "";
	}
	
	public String getJdbcConnDetail() {
		try {
			ObjectName obj = new ObjectName(OBJECTNAME_CONNECTION_POOL);
			Set<ObjectName> instances = getJMXConnector().getMBeanServerConnection().queryNames(obj, null);
			List<Map<String, String>> list = new ArrayList<Map<String,String>>();
			for(ObjectName name : instances) {
				Map<String, String> attrs = ResinJmxUtil.getAttributes(getJMXConnector().getMBeanServerConnection(), name, ATTRIBUTE_NAME, "ConnectionIdleCount", "ConnectionActiveCount", "MaxConnections");
				attrs.put("Availability", "1");
				list.add(attrs);
			}
			return ResinJmxUtil.attrMapsToString(list, new String[]{"Name", "Availability", "ConnectionIdleCount", "ConnectionActiveCount", "MaxConnections"});
		} catch (Exception e) {
			log.error("getJdbcConnDetail error!"+e.getMessage(), e);
		}
		return "";
	}
	
	public String getWebAppDetail() {
		try {
			ObjectName obj = new ObjectName(OBJECTNAME_WEB_APP);
			Set<ObjectName> instances = getJMXConnector().getMBeanServerConnection().queryNames(obj, null);
			List<Map<String, String>> list = new ArrayList<Map<String,String>>();
			for(ObjectName name : instances) {
				Map<String, String> attrs = ResinJmxUtil.getAttributes(getJMXConnector().getMBeanServerConnection(), name, "ContextPath");
				String appPath = attrs.get("ContextPath");
				String id = DigestUtils.md5Hex(appPath);
				attrs.put("AppId", id);
				attrs.put("Availability", "1");
				if(!attrs.get("ContextPath").equals(""))
					list.add(attrs);
			}
			return ResinJmxUtil.attrMapsToString(list, new String[]{"AppId", "ContextPath", "Availability"});
		} catch (Exception e) {
			log.error("getWebAppDetail error!"+e.getMessage(), e);
		}
		return "";
	}
	
	public JMXConnector getJMXConnector() {
			try {
				conn=ResinConnect.getConnector(obj);
				return conn;
			} catch (Exception e) {
				log.error("get MBeanServerConnection error!"+e.getMessage(), e);
				return null;
			} 
		
	}
	
}
