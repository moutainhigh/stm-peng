package com.mainsteam.stm.plugin.resin3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class ResinConnect {
	private static final Log log=LogFactory.getLog(ResinConnect.class);
	private static final Map<String , JMXConnector> jmxcollectorMap=new HashMap<String, JMXConnector>();
	public static boolean check(JBrokerParameter obj){
		JMXConnector conn=null;
		String ip=obj.getIp();
		int port=obj.getPort();
		String username=obj.getUsername();
		String password=obj.getPassword();
		try {
			conn=initConnection(ip,port,username,password);
			conn.close();
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public static JMXConnector getConnector(JBrokerParameter obj){
		JMXConnector connector=null;
		String ip=obj.getIp();
		int port=obj.getPort();
		String username=obj.getUsername();
		String password=obj.getPassword();
		String key=obj.getIp()+":"+obj.getPort();
		if(jmxcollectorMap.get(key)!=null){
			try {
				jmxcollectorMap.get(key).getConnectionId();
				return jmxcollectorMap.get(key);
			} catch (IOException e) {
				log.error("Connection time-outs, began to establish connection", e);
				try {
					connector= initConnection(ip,port,username,password);
					jmxcollectorMap.put(key, connector);
					return connector;
				} catch (Exception e1) {
					log.error("To establish the connection failed!"+e1.getMessage(), e1);
					jmxcollectorMap.remove(key);
					return null;
				} 
			}
		}
		else{
			try {
				connector= initConnection(ip,port,username,password);
				jmxcollectorMap.put(key, connector);
				return connector;
			} catch (Exception e) {
				log.error("create jmx connection failed!"+e.getMessage(), e);
				return null;
			} 
		}
	}
	private static JMXConnector initConnection(String ip,int port,String userName,String password) throws MalformedURLException, IOException {
		JMXConnector conn=null;
		String credentials[]=new String[]{userName, password};
		JMXServiceURL jmxUrl=new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ip+":"+port+"/jmxrmi");
		Map<String,String[]> env = new HashMap<>();
		env.put(JMXConnector.CREDENTIALS, credentials);
		try {
			conn = JMXConnectorFactory.connect(jmxUrl, env);
			log.info("create a jmx connection");
			return conn;
		} catch (Exception e) {
			log.error("create jmx connection failed"+e.getMessage(), e);
			return null;
		}
	}
	
	public static void close(JBrokerParameter obj){
		String ip=obj.getIp();
		int port=obj.getPort();
		String key=ip+":"+port;
		JMXConnector jmxConnector=jmxcollectorMap.get(key);
		if(jmxConnector!=null){
			try {
				jmxConnector.getConnectionId();
				jmxConnector.close();
				jmxcollectorMap.remove(key);
			} catch (IOException e) {
				log.error("close jmx link failed",e);
			}
			
		}
	}
}
