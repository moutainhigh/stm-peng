/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.device;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IDeviceProtoNetflowPageSelect.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 查询设备协议流量信息接口</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月3日
 * @author   lil
 */
public interface IDeviceProtoApi {

	/**
	 * 查询设备协议流量使用-datagrid
	 * 
	 * @param bo
	 * @return
	 * List<DeviceFlowBo>    返回类型
	 */
	NetflowPageBo deviceProtoNetflowPageSelect(NetflowParamBo bo);
	
	/**
	 * 获取设备协议流量使用-highcharts
	 * 
	 * @param bo
	 * @return
	 * DeviceProtoChartShowBo    返回类型
	 */
	NetflowCharWrapper getDeviceProtoChartData(NetflowParamBo bo);
}
