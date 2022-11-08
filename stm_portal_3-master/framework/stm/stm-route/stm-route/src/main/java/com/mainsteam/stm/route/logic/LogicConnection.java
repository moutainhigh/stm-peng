package com.mainsteam.stm.route.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 连接，提供输入和输出
 * 
 * @author ziw
 * 
 */
public interface LogicConnection {

	/**
	 * 获取连接使用的应用方式
	 * 
	 * @return LogicAppEnum
	 */
	public LogicAppEnum getConnectionApp();

	/**
	 * 判断连接是否可用
	 * 
	 * @return true:可用,false:不可用
	 * @throws IOException
	 */
	public boolean isValid();

	/**
	 * 关闭连接
	 */
	public void close();

	/**
	 * 获取连接的输入流
	 * 
	 * @return InputStream
	 */
	public InputStream getInputStream();

	/**
	 * 获取连接的输出流
	 * 
	 * @return OutputStream
	 */
	public OutputStream getOutputStream();
}
