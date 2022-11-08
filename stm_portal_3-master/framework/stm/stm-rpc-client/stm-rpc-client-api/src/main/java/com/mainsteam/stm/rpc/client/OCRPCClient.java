/**
 * 
 */
package com.mainsteam.stm.rpc.client;

import java.io.IOException;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;

/**
 * 提供OC系统内远程调用客户端服务
 * 
 * @author ziw
 * 
 */
public interface OCRPCClient {
	
	/**
	 * 得到远程接口,面对节点组
	 * 
	 * @param nodeGroupId
	 *            指定的节点组id
	 * @param remoteInterfaceClass
	 *            远程接口类
	 * @return T 远程接口代理类
	 * @throws IOException
	 */
	public <T> T getRemoteSerivce(int nodeGroupId, Class<T> remoteInterfaceClass)
			throws IOException;
	
	/**
	 * 得到远程接口,面对节点组
	 * 
	 * @param node
	 *            指定的节点
	 * @param remoteInterfaceClass
	 *            远程接口类
	 * @return T 远程接口代理类
	 * @throws IOException
	 */
	public <T> T getRemoteSerivce(Node node, Class<T> remoteInterfaceClass)
			throws IOException;

	/**
	 * 直接得到指定Node的远程接口，不面对节点组
	 * 
	 * @param node
	 *            指定的节点
	 * @param remoteInterfaceClass
	 *            远程接口类
	 * @return T 远程接口代理类
	 * @throws IOException
	 */
	public <T> T getDirectRemoteSerivce(Node node, Class<T> remoteInterfaceClass)
			throws IOException;

	/**
	 * 得到上级的指定类型的远程接口,面对节点组
	 * 
	 * @param func
	 *            指定的父节点的类型
	 * @param remoteInterfaceClass
	 *            远程接口类
	 * @return T 远程接口代理类
	 * @throws IOException
	 */
	public <T> T getParentRemoteSerivce(NodeFunc func,
			Class<T> remoteInterfaceClass) throws IOException;
}
