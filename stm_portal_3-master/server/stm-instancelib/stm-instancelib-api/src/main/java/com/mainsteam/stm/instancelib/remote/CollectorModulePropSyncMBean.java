package com.mainsteam.stm.instancelib.remote;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ModuleProp;

/**
 * 资源实例在同步到采集器操作
 * @author xiaoruqiang
 */
public interface CollectorModulePropSyncMBean {

	/**
	 * 模型属性数据从处理器同步到采集器
	 */
	public void updateModulePropSyncToCollector(List<ModuleProp> moduleProps) throws Exception;
	
}
