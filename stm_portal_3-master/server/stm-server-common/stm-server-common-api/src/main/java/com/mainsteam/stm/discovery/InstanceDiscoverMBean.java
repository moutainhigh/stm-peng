/**
 * 
 */
package com.mainsteam.stm.discovery;

import com.mainsteam.stm.discovery.exception.InstanceDiscoveryException;
import com.mainsteam.stm.discovery.obj.DiscoveryParameter;
import com.mainsteam.stm.discovery.obj.ModelResourceInstance;

/**
 * 通过指标数据采集来实现资源实例的发现的远程调用接口
 * 
 * @author ziw
 * 
 */
public interface InstanceDiscoverMBean {
	/**
	 * 执行发现实例
	 * 
	 * @param discoveryParameter
	 *            发现参数
	 * @return ModelResourceInstance
	 */
	public ModelResourceInstance discovery(DiscoveryParameter discoveryParameter)
			throws InstanceDiscoveryException;
}
