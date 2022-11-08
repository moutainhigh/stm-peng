/**
 * 
 */
package com.mainsteam.stm.route.physical.connection;

import java.io.IOException;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicConnection;

/**
 * 物理连接
 * 
 * @author ziw
 * 
 */
public interface PhysicalConnection {

	/**
	 * 获取连接id
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 判断连接是否可用
	 * 
	 * @return true:可用,false:不可用
	 */
	public boolean isValid();

	/**
	 * 创建一个逻辑连接
	 * 
	 * @param appEnum
	 *            连接的应用类型
	 * @return LogicConnection
	 * @throws IOException
	 */
	public LogicConnection createLogicConnection(LogicAppEnum appEnum,
			String distIp, int distPort)
			throws IOException;
	
	public void close() throws IOException;
}
