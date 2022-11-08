package com.mainsteam.stm.instancelib;

/**
 * 核心设备设置
 * @author xiaoruqiang
 */
public interface CoreResourceInstanceService {

	/**
	 * 设置核心节点
	 * @param ip 核心节点IP
	 */
	public void setCoreResourceInstance(String ip);
	
	/**
	 * 设置核心节点
	 * @param instanceId 资源实例Id
	 */
	public void setCoreResourceInstance(long instanceId);
	
	/**
	 * 查询核心节点
	 * @return
	 */
	public long getCoreResourceInstance();
}
