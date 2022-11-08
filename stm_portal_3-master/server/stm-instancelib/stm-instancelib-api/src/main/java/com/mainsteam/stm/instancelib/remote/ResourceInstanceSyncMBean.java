package com.mainsteam.stm.instancelib.remote;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;

/**
 * 资源实例在采集器同步到处理器操作
 * @author xiaoruqiang
 */
public interface ResourceInstanceSyncMBean {

	/**
	 * 启动的时候数据从处理器同步到采集器
	 * @return 处理器最新所有资源实例信息
	 */
	public List<ResourceInstance> dataSyncToCollector(long nodeGroupId);
	
}
