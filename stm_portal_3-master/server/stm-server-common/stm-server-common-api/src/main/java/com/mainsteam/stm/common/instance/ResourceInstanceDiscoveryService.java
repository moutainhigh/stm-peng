/**
 * 
 */
package com.mainsteam.stm.common.instance;

import com.mainsteam.stm.common.exception.ResourceInstanceDiscoveryException;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.discovery.obj.DiscoverResourceIntanceResult;

/**
 * 资源实例发现服务
 * 
 * @author ziw
 * 
 */
public interface ResourceInstanceDiscoveryService {
	/**
	 * 发现资源实例
	 * 
	 * @param parameter
	 *            发现参数
	 * @return ResourceInstance 资源实例发现结果
	 * @throws ResourceInstanceDiscoveryException
	 */
	DiscoverResourceIntanceResult discoveryResourceInstance(
			ResourceInstanceDiscoveryParameter parameter);
	
	/**
	 * 拓扑发现资源实例
	 * 
	 * @param parameter
	 *            发现参数
	 * @return ResourceInstance 资源实例发现结果
	 * @throws ResourceInstanceDiscoveryException
	 */
	void topoDiscoveryResourceInstance(
			ResourceInstanceDiscoveryParameter parameter);
	
}
