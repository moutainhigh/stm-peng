/**
 * 
 */
package com.mainsteam.stm.errorcode;

/**
 * 能力库内组件的常量类
 * 
 * code的命名规则ERR_SERVER_模块简称_错误简称
 * 
 * 
 * 我们的错误编码范围是从800-899
 * 
 * @author sunsht
 * 
 */
public final class CapcityErrorCodeConstant implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6842391527048485795L;

	/**
	 * 没有采集值
	 */
	public static final int ERR_CAPCITY_COLLECT_NULLVALUES = 801;

	/**
	 * 没有采集命令
	 */
	public static final int ERR_CAPCITY_COLLECT_NULLCOMMAND = 802;

	/**
	 * 采集失败
	 */
	public static final int ERR_CAPCITY_COLLECT_FAILED = 803;
	/**
	 * 反射调用失败
	 */
	public static final int ERR_CAPCITY_REFLECT_FAILED = 804;

	/**
	 * 连接失败
	 */
	public static final int ERR_CAPCITY_CONNECTION_FAILED = 805;

	/**
	 * 发现参数错误
	 */
	public static final int ERR_CAPCITY_DISCOVERY_PARAMS = 806;

	/**
	 * 登录失败
	 */
	public static final int ERR_CAPCITY_LOGIN_FAILED = 807;
	/**
	 * 加载配置文件失败
	 */
	public static final int ERR_CAPCITY_LOAD_CONFIG_FAILED = 808;

	/**
	 * 用户名或密码错误
	 */
	public static final int ERR_CAPCITY_USERNAME_OR_PASSWORD = 809;
	
	/**
	 * JDBC连接URL错误
	 */
	public static final int ERR_CAPCITY_JDBC_URL = 810;

	/**
	 * 当前用户权限不足
	 */
	public static final int ERR_CAPCITY_USER_NO_PERMISSIONS = 811;

	/**
	 * 服务器拒绝连接，请确定IP和端口是否正确，以及相关服务是否启动
	 */
	public static final int ERR_CAPCITY_REJECTED_CONNECTION = 812;

	/**
	 * 密码中包含无效字符
	 */
	public static final int ERR_CAPCITY_PASSWORD_ILLEGAL_CHARACTER = 813;

	/**
	 * 连接WMI Agent失败
	 */
	public static final int ERR_CAPCITY_WMI_AGENT_FAILED = 814;

	/**
	 * 采集指标超时
	 */
	public static final int ERR_CAPCITY_COLLECT_METRIC_TIMEOUT = 815;

	/**
	 * JDBC登录连接超时，请检查发现参数是否正确以及网络环境是否稳定
	 */
	public static final int ERR_CAPCITY_JDBC_CONNECTION_TIMEOUT = 816;

	/**
	 * Telnet命令读取数据超时
	 */
	public static final int ERR_CAPCITY_COLLECT_TIMEOUT = 817;

	/**
	 * SSH认证失败，请检查用户名密码是否输入正确，以及SSH连接是否达到最大连接数
	 */
	public static final int ERR_CAPCITY_SSH_AUTH_FAILED = 818;

	/**
	 * SQL执行失败
	 */
	public static final int ERR_CAPCITY_JDBC_SQL = 819;

	/**
	 * sql采集异常
	 */
	public static final int ERR_CAPCITY_JDBC_COLLECT = 820;
	
	/**
	 * 采集命令错误
	 */
	public static final int ERR_CAPCITY_ERROR_COMMAND = 821;
	
	/**
	 * Java反射安全限制
	 */
	public static final int ERR_CAPCITY_JAVA_SECURITY = 822;
	
	/**
	 * 采集方法无法访问
	 */
	public static final int ERR_CAPCITY_ACCESS_METHOD = 823;
	
	/**
	 * 传入采集方法的参数错误
	 */
	public static final int ERR_CAPCITY_METHOD_OF_PARAMS = 824;
	
	/**
	 * LDAP服务器用户名或密码错误
	 */
	public static final int ERR_CAPCITY_LDAP_USERNAME_OR_PASSWORD = 825;
	
	/**
	 * IP或者LDAP服务器端口错误
	 */
	public static final int ERR_CAPCITY_LDAP_IP_OR_PORT = 826;
	
	/**
	 * WebSphere MQ队列管理器不可用
	 */
	public static final int ERR_CAPCITY_MQ_QM_UNAVAILABLE = 827;
	
	/**
	 * 空参数
	 */
	public static final int ERR_CAPCITY_CONVERT_NULLPARAMETER = 861;
	/**
	 * 没标题列
	 */
	public static final int ERR_CAPCITY_CONVERT_NOTITLE = 862;
	/**
	 * 转换器空值错误
	 */
	public static final int ERR_CAPCITY_CONVERT_NULLINSTVALUE = 863;
	/**
	 * 实例key的值是空
	 */
	public static final int ERR_CAPCITY_CONVERT_EMPTYINSTKEY = 864;
	/**
	 * 数据库类型是空
	 */
	public static final int ERR_CAPCITY_CONVERT_DBTYPEERROR = 865;

}
