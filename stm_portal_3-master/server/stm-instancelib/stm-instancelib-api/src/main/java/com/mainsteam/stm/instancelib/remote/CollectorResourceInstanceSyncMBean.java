package com.mainsteam.stm.instancelib.remote;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

/**
 * 资源实例在同步到采集器操作
 * @author xiaoruqiang
 */
public interface CollectorResourceInstanceSyncMBean {

	/**
	 * 实例后数据从处理器同步到采集器
	 */
	public void addResourceInstanceSyncToCollector(ResourceInstance resourceInstance) throws InstancelibException;
	
	/**
	 * 实例后数据从处理器同步到采集器
	 */
	public void addResourceInstancesSyncToCollector(List<ResourceInstance> resourceInstances) throws InstancelibException;
	/**
	 * 实例后数据从处理器同步到采集器
	 * @param resourceInstance
	 * @throws InstancelibException
	 */
	public void refreshResourceInstanceSyncToCollector(ResourceInstance resourceInstance) throws InstancelibException;
	
	
	/**
	 * 实例后数据从处理器同步到采集器
	 * @param resourceInstance
	 * @throws InstancelibException
	 */
	public void refreshResourceInstancesSyncToCollector(List<ResourceInstance> resourceInstances) throws InstancelibException;
}
