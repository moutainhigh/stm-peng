/**
 * 
 */
package com.mainsteam.stm.route.physical;

import java.io.IOException;

import com.mainsteam.stm.route.connection.ConnectionProtocol;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.physical.connection.PhysicalConnection;

/**
 * 物理连接server管理
 * 
 * @author ziw
 * 
 */
public interface PhsicalConnectionManager {
	/**
	 * 获取到指定主机的一个物理连接
	 * 
	 * @param ip
	 *            指定主机
	 * @param port
	 *            指定主机端口
	 * @param protocol
	 *            连接协议
	 * @return PhysicalConnection
	 */
	public PhysicalConnection getConnection(String ip, int port,
			ConnectionProtocol protocol, LogicAppEnum appEnum)
			throws IOException;
}
