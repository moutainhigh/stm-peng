package com.mainsteam.stm.plugin.ldap;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchConstraints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * LDAP采集插件
 * @author xiaop_000
 *
 */
public class LDAPPluginSession extends BaseSession {
	
	private static final String LDAPPLUGIN_INSTALLPATH = "installPath";

	private static final Log logger = LogFactory.getLog(LDAPPluginSession.class);
	
	public static final String LDAPPLUGIN_PASSWORD = "ldapPassword";

	public static final String LDAPPLUGIN_USERNAME = "ldapUsername";

	public static final String LDAPPLUGIN_PORT = "ldapPort";

	public static final String LDAPPLUGIN_IP = "IP";
	
	public static final String DOMAIN_NAME = "domainName";
	
	private String ip;
	
	private int port;
	
	private String username;
	
	private String password;
	
	private String installPath;
	
	private String domain;
	
	private int timeout = 5000;
	
	private LDAPConnection connection; //LDAP连接
	
	private static int ERRORUSERINFO = 32;
	
	private static int ERRIPORPORT = 91;
	
	public boolean isAlive;
	
	@Override
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		// TODO Auto-generated method stub
		Parameter[] initParameters = init.getParameters();
		
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case LDAPPLUGIN_IP:
				this.setIp(initParameters[i].getValue());
				break;
			case LDAPPLUGIN_PORT:
				this.setPort(Integer.parseInt(initParameters[i].getValue()));
				break;
			case LDAPPLUGIN_USERNAME:
				this.setUsername(initParameters[i].getValue());
				break;
			case LDAPPLUGIN_PASSWORD:
				this.setPassword(initParameters[i].getValue());
				break;
			case LDAPPLUGIN_INSTALLPATH:
				this.setInstallPath(initParameters[i].getValue());
				break;
			case DOMAIN_NAME:
				this.setDomain(initParameters[i].getValue());
				break;
			default:
				if (logger.isWarnEnabled()) {
					logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
							+ initParameters[i].getValue());
				}
				break;
			}
		}
		
		try{
			this.createConnection();
		}catch(Exception e){
			if(logger.isWarnEnabled()){
				logger.warn("LDAP连接失败；" + e.getMessage(), e);
			}
			this.destory();
			throw e;
		}
		super.getParameter().setConnection(connection);
		super.getParameter().setDomainName(domain);
		this.isAlive = true;

	}

	@Override
	public synchronized void destory() {
		// TODO Auto-generated method stub
		if(this.connection != null) {
			try {
				this.connection.disconnect();
			} catch (LDAPException e) {
				// TODO Auto-generated catch block
				logger.warn(e);
				this.connection = null;
			} finally {
				this.isAlive = false;
			}
		}
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isAlive() {
		// TODO Auto-generated method stub
		if(this.connection != null && (this.connection.isAuthenticated() || this.connection.isConnected() ))
			this.isAlive = true;
		else
			this.isAlive = false;
		return this.isAlive;
	}
	
	private synchronized void createConnection() throws PluginSessionRunException {
		
		try{
			this.connection = new LDAPConnection();
			/* Connect to the server. */
			this.connection.connect(this.getIp(), this.getPort());
			
			LDAPSearchConstraints constraints = this.connection.getSearchConstraints();
			constraints.setTimeLimit(this.getTimeout());
			this.connection.setSearchConstraints(this.connection.getSearchConstraints());
			this.connection.authenticate(this.getUsername(), this.getPassword());
			
		}catch(Exception e){
			if(e instanceof LDAPException){
				int resultCode = ((LDAPException) e).getLDAPResultCode();
				if(resultCode == ERRORUSERINFO){
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_LDAP_USERNAME_OR_PASSWORD, e);
				}else if(resultCode == ERRIPORPORT){
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_LDAP_IP_OR_PORT, e);
				}
			}
			throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED,"LDAP连接失败",e);
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

	public String getInstallPath() {
		return installPath;
	}

	public void setInstallPath(String installPath) {
		this.installPath = installPath;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}


	
}
