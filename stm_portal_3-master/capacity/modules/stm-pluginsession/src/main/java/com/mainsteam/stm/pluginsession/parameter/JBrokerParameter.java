package com.mainsteam.stm.pluginsession.parameter;

/**
 * 传入各个session的参数类
 * 
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
	
	private String uuid ;

	/**
	 * 连接对象
	 */
	private Object connection;

	private BaseBO bo;

	public BaseBO getBaseBO() {
		return this.bo;
	}

	public void setBaseBO(BaseBO baseBO) {
		this.bo = baseBO;
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

	public Object getConnection() {
		return connection;
	}

	public void setConnection(Object connection) {
		this.connection = connection;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
