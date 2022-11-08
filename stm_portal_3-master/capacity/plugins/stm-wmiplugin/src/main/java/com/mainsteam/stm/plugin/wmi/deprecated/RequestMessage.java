package com.mainsteam.stm.plugin.wmi.deprecated;

/**
 * wmi查询数据包，用于记录查询信息，发送到服务端。
 */

public class RequestMessage {

	/**
	 * 登陆目的机器的用户名和用户名所在的域名称。域名可以在windows系统属性中查询到。例
	 * 如：qz-test-win2000\administrator
	 */
	private String wmi_domain_user;

	/**
	 * 登陆目的机器的用户名对应的登陆密码
	 */
	private String wmi_password;

	/**
	 * 目的主机的IP和wmi命名空间
	 */
	private String wmi_namespace;

	/**
	 * Wmi的查询命令，wql格式
	 */
	private String wmi_query_cmd;

	/**
	 * 查询命令的唯一识别码，源端必须保证唯一性。本程序原样返回给源端
	 */
	private String wmi_query_uuid;

	/**
	 * 结束字符
	 */
	public static final Byte END_CHAR = ';';

	public String getWmi_domain_user() {
		return wmi_domain_user;
	}

	public void setWmi_domain_user(String wmi_domain_user) {
		this.wmi_domain_user = wmi_domain_user;
	}

	public String getWmi_password() {
		return wmi_password;
	}

	public void setWmi_password(String wmi_password) {
		this.wmi_password = wmi_password;
	}

	public String getWmi_namespace() {
		return wmi_namespace;
	}

	public void setWmi_namespace(String wmi_namespace) {
		this.wmi_namespace = wmi_namespace;
	}

	public String getWmi_query_cmd() {
		return wmi_query_cmd;
	}

	public void setWmi_query_cmd(String wmi_query_cmd) {
		this.wmi_query_cmd = wmi_query_cmd;
	}

	public String getWmi_query_uuid() {
		return wmi_query_uuid;
	}

	public void setWmi_query_uuid(String wmi_query_uuid) {
		this.wmi_query_uuid = wmi_query_uuid;
	}

}
