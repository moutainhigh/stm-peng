/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.device;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IDeviceApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月25日
 * @author   lil
 */
public interface IDeviceFlowApi {

	/**
	 * 根据页面参数分页查询设备流量 
	 * 
	 * @param bo
	 * @return
	 * DeviceFlowPageBo    返回类型
	 */
	NetflowPageBo deviceListPageSelect(NetflowParamBo bo);
	
	/**
	 * 查询每台设备在时间范围内每个时间点的流量
	 * 
	 * @param bo
	 * @return
	 * DeviceFlowCharWrapper    返回类型
	 */
	NetflowCharWrapper deviceFlowChartPageSelect(NetflowParamBo bo);
	
}
