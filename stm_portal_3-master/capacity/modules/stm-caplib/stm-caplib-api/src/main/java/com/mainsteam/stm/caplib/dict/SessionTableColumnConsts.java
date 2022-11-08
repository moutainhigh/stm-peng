package com.mainsteam.stm.caplib.dict;

/**
 * 神舟数据库会话信息表
 * @author xiehf
 *
 */
public final class SessionTableColumnConsts {
	
	/**
	 * 会话ID
	 */
	public static final String SESSIONID = "Sessionid";
	/**
	 * 当前登录用户
	 */
	public static final String CURRENTUSER = "CurrentUser";
	/**
	 * 登录时间
	 */
	public static final String LOGONTIME = "LogonTime";
	/**
	 * 数据库名
	 */
	public static final String DBNAME = "DbName";
	/**
	 * 客户端应用程序名称
	 */
	public static final String APPNAME = "AppName";
	/**
	 * 正在执行的SQL语句
	 */
	public static final String CURRENTSQL = "CurrentSql";
	/**
	 * 会话执行的SQL语句总数
	 */
	public static final String SQLCOUNT = "SqlCount";

}
