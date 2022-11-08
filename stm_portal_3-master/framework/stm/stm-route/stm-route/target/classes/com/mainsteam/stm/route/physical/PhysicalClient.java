/**
 * 
 */
package com.mainsteam.stm.route.physical;

import java.io.IOException;

import com.mainsteam.stm.route.connection.ConnectionProtocol;
import com.mainsteam.stm.route.physical.connection.PhysicalConnection;

/**
 * 创建一个物理连接
 * 
 * @author ziw
 * 
 */
public interface PhysicalClient {
	/**
	 * 创建一个物理连接，如果该连接已经存在，则使用已经存在的连接
	 * 
	 * @param ip
	 * @param port
	 * @param protocol
	 *            连接应用的协议
	 * @return PhysicalConnection
	 * @throws IOException
	 */
	public PhysicalConnection createPhysicalConnection(String ip, int port,
			ConnectionProtocol protocol) throws IOException;
}
