package com.mainsteam.stm.plugin.parameter;

import javax.management.MBeanServerConnection;

import com.mainsteam.stm.plugin.apache.ApacheBo;
import com.mainsteam.stm.plugin.nginx.NginxBo;
import com.mainsteam.stm.plugin.oracleas.OracleasBo;
import com.mainsteam.stm.plugin.sunjes.SunjesBo;
import com.mainsteam.stm.plugin.weblogic.WeblogicBo;

/**
 * 传入各个session的参数类
 * @author xiaop_000
 *
 */
public class JBrokerParameter {
	/**
	 * IP地址
	 */
	private String ip;
	/**
	 * 端口
	 */
	private int port;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 安装目录
	 */
	private String installPath;
	/**
	 * 连接超时时间
	 */
	private int timeout = 10000;
	/**
	 * tomcat用|tomcat版本
	 */
	private String tomcatVersion;
	/**
	 * apache
	 */
	private ApacheBo apacheBo;
	/**
	 * Niginx
	 */
	private NginxBo niginxBo;
	
	
	private IBMWebSphereParameter ibmwsParam;
	
	/**
	 * 编码字符集（暂时只有IBMMQ用到）
	 */
	private String charset;
	/**
	 * 域名称
	 */
	private String domainName;
	/**
	 * Oracleas
	 */
	private OracleasBo oracleasBo;
	/**
	 * 公共jmx连接
	 */
	private MBeanServerConnection mBeanServerConnection;
	public MBeanServerConnection getmBeanServerConnection() {
		return mBeanServerConnection;
	}

	public void setmBeanServerConnection(MBeanServerConnection mBeanServerConnection) {
		this.mBeanServerConnection = mBeanServerConnection;
	}

	/**
	 * Sunjes
	 */
	private SunjesBo sunjesBo;
	/**
	 * webLogic
	 */
	private WeblogicBo weblogicBo;
	
	/**
	 * 连接对象
	 */
	private Object connection;
	
	
	public WeblogicBo getWeblogicBo() {
		return weblogicBo;
	}

	public void setWeblogicBo(WeblogicBo weblogicBo) {
		this.weblogicBo = weblogicBo;
	}

	public ApacheBo getApacheBo() {
		return apacheBo;
	}

	public void setApacheBo(ApacheBo apacheBo) {
		this.apacheBo = apacheBo;
	}

	public SunjesBo getSunjesBo() {
		return sunjesBo;
	}

	public void setSunjesBo(SunjesBo sunjesBo) {
		this.sunjesBo = sunjesBo;
	}

	public OracleasBo getOracleasBo() {
		return oracleasBo;
	}

	public void setOracleasBo(OracleasBo oracleasBo) {
		this.oracleasBo = oracleasBo;
	}


	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getTomcatVersion() {
		return tomcatVersion;
	}

	public void setTomcatVersion(String tomcatVersion) {
		this.tomcatVersion = tomcatVersion;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getInstallPath() {
		return installPath;
	}

	public void setInstallPath(String installPath) {
		this.installPath = installPath;
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

	public IBMWebSphereParameter getIBMWebSphereParameter() {
		return ibmwsParam;
	}

	public void setIBMWebSphereParameter(IBMWebSphereParameter ibmwsParam) {
		this.ibmwsParam = ibmwsParam;
	}

	public Object getConnection() {
		return connection;
	}

	public void setConnection(Object connection) {
		this.connection = connection;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public NginxBo getNiginxBo() {
		return niginxBo;
	}

	public void setNiginxBo(NginxBo niginxBo) {
		this.niginxBo = niginxBo;
	}

}
