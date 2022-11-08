package com.mainsteam.stm.instancelib.remote;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.DiscoverProp;

/**
 * 资源实例在同步到采集器操作
 * @author xiaoruqiang
 */
public interface CollectorDiscoverPropSyncMBean {

	/**
	 * 发现属性数据从处理器同步到采集器
	 */
	public void updateDiscoverPropSyncToCollector(List<DiscoverProp> discoverProps) throws Exception;
	
}
