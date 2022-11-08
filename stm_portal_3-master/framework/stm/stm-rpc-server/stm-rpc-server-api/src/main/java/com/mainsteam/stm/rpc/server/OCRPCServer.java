/**
 * 
 */
package com.mainsteam.stm.rpc.server;

import java.io.IOException;
import java.util.List;

/**
 * 提供OC系统内远程调用服务
 * 
 * @author ziw
 * 
 */
public interface OCRPCServer {
	/**
	 * 注册远程调用服务
	 * 
	 * @param remoteService
	 *            对系统其它组件提供的服务对象
	 * @param 对外暴露的接口
	 * @return true:成功,false:失败
	 */
	public <T> boolean registerService(T remoteService, Class<T> interfaceClass);

	/**
	 * 取消指定服务对象的注册
	 * 
	 * @param remoteService
	 * @return true:成功,false:失败
	 */
	public boolean unregisterService(Object remoteService);

	/**
	 * 取消指定服务对象的注册
	 * 
	 * @param remoteService
	 * @return true:成功,false:失败
	 */
	public boolean unregisterService(long remoteServiceId);

	/**
	 * 将所有的远程服务的信息返回。
	 * 
	 * @return
	 */
	public List<RemoteServiceInfo> listService();

	/**
	 * 启动rpc服务
	 * 
	 * @throws IOException
	 */
	public void startServer() throws IOException;

	/**
	 * 关闭rpc服务
	 */
	public void stopServer() throws IOException;

	/**
	 * 判断server是否已经启动
	 * 
	 * @return true:已经启动,false:未启动
	 */
	public boolean isStarted();
}
