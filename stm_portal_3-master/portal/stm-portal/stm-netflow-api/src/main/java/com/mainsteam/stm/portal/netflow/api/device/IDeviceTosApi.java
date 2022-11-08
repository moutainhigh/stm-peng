/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.device;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IDeviceTosApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 设备tos流量查询接口</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IDeviceTosApi {

	/**
	 * 查询设备tos流量接口方法-datagrid
	 * 
	 * @param bo
	 * @return
	 * List<NetflowPageBo>    返回类型
	 */
	NetflowPageBo deviceTosNetflowPageSelect(NetflowParamBo bo);
	
	/**
	 * 查询设备tos流量-highcharts
	 * 
	 * @param bo
	 * @return
	 * NetflowCharWrapper    返回类型
	 */
	NetflowCharWrapper getDeviceTosChartData(NetflowParamBo bo);
	
}
