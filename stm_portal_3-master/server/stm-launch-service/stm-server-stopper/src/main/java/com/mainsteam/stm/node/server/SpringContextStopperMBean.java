/**
 * 
 */
package com.mainsteam.stm.node.server;

/**
 * 提供容器停止的远程调用
 * 
 * @author ziw
 *
 */
public interface SpringContextStopperMBean {
	public void stop() throws Exception;
}
