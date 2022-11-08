package com.mainsteam.stm.plugin.oracleas;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * OracleAS采集插件
 * @author 
 *
 */
public class OracleASPluginSession extends BaseSession {
	
	private static final Log logger = LogFactory.getLog(OracleASPluginSession.class);
	private static final String ORACLEAS_PLUGIN_IP="IP";
	private static final String ORACLEAS_PLUGIN_PORT="oracleasPort";
	private static final String ORACLEAS_PLUGIN_USERNAME="oracleasUsername";
	private static final String ORACLEAS_PLUGIN_PWD="oracleasPassword";
	private static final String ORACLEAS_PLUGIN_INSTANCENAME="oracleasInstancename";
	private boolean isAlive=false;
	JMXConnector connection=null;
	JMXConnector clusterConnection=null;
	OracleasBo oracleasBo=new OracleasBo();
	@Override
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters=init.getParameters();
		for(int i=0;i<initParameters.length;i++){
			switch (initParameters[i].getKey()) {
			case ORACLEAS_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());;
				break;
			case ORACLEAS_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));			
				break;
			case ORACLEAS_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case ORACLEAS_PLUGIN_PWD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			case ORACLEAS_PLUGIN_INSTANCENAME:
				
				oracleasBo.setOc4jInstanceName(initParameters[i].getValue());
				super.getParameter().setOracleasBo(oracleasBo);
				break;
			default:
				if (logger.isWarnEnabled()) {
						logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
								+ initParameters[i].getValue());
					}
					break;

			}
		}
		String domain=super.getParameter().getOracleasBo().getOc4jInstanceName();
		
		try {
			clusterConnection=createConn("cluster");
			connection = createConn(domain);
			if(connection!=null&&clusterConnection!=null){
				isAlive=true;
				oracleasBo.setConnection(connection);
				oracleasBo.setClusterConnection(clusterConnection);
				super.getParameter().setOracleasBo(oracleasBo);
			}
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		
	}

	public JMXConnector createConn(String domain) throws MalformedURLException, IOException {
		String ip=super.getParameter().getIp();
		int port=super.getParameter().getPort();
		Map<String, String> t_credentials = new HashMap<String, String>();
		t_credentials.put("login", super.getParameter().getUsername());
		t_credentials.put("password", super.getParameter().getPassword());
		Map<String, Object> t_env = new Hashtable<String, Object>();
		t_env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
				"oracle.oc4j.admin.jmx.remote");
		t_env.put(JMXConnector.CREDENTIALS, t_credentials);
		String t_urlString = "service:jmx:rmi:///opmn://"+ip+":"+port+"/" + domain;
		logger.info("Will connect to :" + t_urlString);
		JMXServiceURL t_serviceURL = new JMXServiceURL(t_urlString);
		JMXConnector t_connect=null;
		try {
			t_connect = JMXConnectorFactory.newJMXConnector(t_serviceURL,t_env);
			t_connect.connect();
		} catch (Exception e) {
			logger.error(e);
		}
		return t_connect;
	}
	@Override
	public void destory() {
		if(connection!=null&&clusterConnection!=null&&isAlive()){
			try {
				connection.close();
				clusterConnection.close();
				isAlive=false;
			} catch (IOException e) {
				logger.error("close connection failed"+e.getMessage(),e);
			}
		}
	}

	@Override
	public void reload() {

	}

	@Override
	public boolean isAlive() {
		try {
			if (connection.getConnectionId() != null && clusterConnection.getConnectionId() != null) {
				isAlive = true;
			} else {
				isAlive = false;
			}
		} catch (IOException e) {
			isAlive = false;
		}
		logger.info("oracleas-isAlive-->" + isAlive);
		return isAlive;
	}
	@Override
	public boolean check(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters=init.getParameters();
		for(int i=0;i<initParameters.length;i++){
			switch (initParameters[i].getKey()) {
			case ORACLEAS_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());;
				break;
			case ORACLEAS_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));			
				break;
			case ORACLEAS_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case ORACLEAS_PLUGIN_PWD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			case ORACLEAS_PLUGIN_INSTANCENAME:
				
				oracleasBo.setOc4jInstanceName(initParameters[i].getValue());
				super.getParameter().setOracleasBo(oracleasBo);
				break;
			default:
				if (logger.isWarnEnabled()) {
						logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
								+ initParameters[i].getValue());
					}
					break;
			}
		}
		return OracleASCollectorUtil.check(super.getParameter());
	}
}
