/**
 * 
 */
package com.mainsteam.stm.discovery;

import com.mainsteam.stm.discovery.exception.InstanceDiscoveryException;
import com.mainsteam.stm.discovery.obj.DiscoveryParameter;
import com.mainsteam.stm.discovery.obj.ModelResourceInstance;

/**
 * 通过指标数据采集来实现资源实例的发现
 * 
 * @author ziw
 * 
 */
public interface InstanceCollectDiscover {
	/**
	 * 对从plugin采集回来的数据，进行切分处理，形成多值。
	 */
	public static final String VALUE_SPLIT = ",";

	/**
	 * 资源实例模型属性的值，用来根据模型实例化属性生成的id
	 */
	public static final String INST_IDENTY_KEY = "INST_IDENTY_KEY";

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
