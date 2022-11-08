/**
 * 
 */
package com.mainsteam.stm.route.logic;

import java.net.InetSocketAddress;

import com.mainsteam.stm.route.logic.connection.ServerConnection;

/**
 * 逻辑连接监听
 * 
 * @author ziw
 * 
 */
public interface LogicServer {
	/**
	 * 开始监听
	 */
	public void startServer(LogicAppEnum appEnum);
	
	/**
	 * 停止监听
	 * 
	 * @param appEnum
	 */
	public void stopServer(LogicAppEnum appEnum);

	/**
	 * 得到一个连接建立请求
	 * 
	 * @return ServerConnection
	 */
	public ServerConnection accept(LogicAppEnum appEnum);
	
	/**
	 * 移除一个连接
	 * 
	 * @param conn
	 */
	public void removeServerConnection(ServerConnection conn);

	public InetSocketAddress getServerHost();
}
