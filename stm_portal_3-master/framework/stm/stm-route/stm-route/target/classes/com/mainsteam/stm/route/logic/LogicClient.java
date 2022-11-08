package com.mainsteam.stm.route.logic;

import java.io.IOException;

import com.mainsteam.stm.route.RouteEntrySupporter;

/**
 * 逻辑连接客户端工厂
 * 
 * @author ziw
 * 
 */
public interface LogicClient {
	/**
	 * 创建指定类型的逻辑连接
	 * 
	 * @param ip
	 *            目标ip
	 * @param port
	 *            目标端口
	 * @param protocol
	 *            连接采用的协议
	 * @param appEnum
	 *            该连接的业务类型
	 * @return ClientConection
	 * @throws IOException
	 */
	public LogicConnection createConection(String ip, int port,
			LogicAppEnum appEnum) throws IOException;

	/**
	 * 设置路由表查询接口
	 * 
	 * @param supporter
	 */
	public void setSupporter(RouteEntrySupporter supporter);
}
