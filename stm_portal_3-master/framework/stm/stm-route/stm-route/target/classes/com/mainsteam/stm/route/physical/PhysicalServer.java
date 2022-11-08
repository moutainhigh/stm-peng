/**
 * 
 */
package com.mainsteam.stm.route.physical;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.mainsteam.stm.route.RouteEntrySupporter;

/**
 * 物理连接监听
 * 
 * @author ziw
 * 
 */
public interface PhysicalServer {

	/**
	 * 设置server的监控ip和端口
	 * 
	 * @param listenIp
	 *            监听的ip
	 * @param listenPort
	 *            监听的端口
	 * @return false:已经被设置过了，server启动后不能重新设置
	 * @throws IOException
	 */
	public boolean setConfig(String listenIp, int listenPort);

	/**
	 * 停止server
	 * 
	 * @throws IOException
	 */
	public void stopServer() throws IOException;

	/**
	 * 开始监听
	 * 
	 * @throws IOException
	 */
	public void startServer() throws IOException;

	/**
	 * 设置路由表查询接口
	 * 
	 * @param supporter
	 */
	public void setSupporter(RouteEntrySupporter supporter);

	public InetSocketAddress getServerHost();
}
