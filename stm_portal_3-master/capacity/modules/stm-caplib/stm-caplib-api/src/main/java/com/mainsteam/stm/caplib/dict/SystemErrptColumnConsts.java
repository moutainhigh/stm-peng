package com.mainsteam.stm.caplib.dict;

/**
 * AIX 系统错误日志信息
 * @author xiehf
 *
 */
public final class SystemErrptColumnConsts {
	
	/**
	 * 错误编号
	 */
	public static final String IDENTIFIER = "Identifier";
	/**
	 * 出错时间
	 * 其格式:月月日日时时分分年年
	 */
	public static final String TIMESTAMP = "TimesTamp";
	/**
	 * 错误类型
	 * P:为永久错误
	 * T:为临时错误
	 */
	public static final String TYPE  = "Type";
	/**
	 * 错误种类
	 * H:Hardware  O:Errloger command messages
	 * S:Software  U:undetermined
	 * 
	 */
	public static final String CLASS = "Class";
	/**
	 * 错误来源
	 */
	public static final String RESOURCENAME  = "ResourceName";
	/**
	 * 错误描述
	 */
	public static final String DESCRIPTION = "Description";

}
