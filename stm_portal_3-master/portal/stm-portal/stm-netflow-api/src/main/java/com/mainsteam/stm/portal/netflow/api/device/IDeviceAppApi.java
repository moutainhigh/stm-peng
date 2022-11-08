/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.device;

import java.util.List;

import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IDeviceAppApi.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月16日
 * @author lil
 */
public interface IDeviceAppApi {

	/**
	 * 根据设备IP，起止时间等信息，分页查找设备应用流量-datagrid
	 * 
	 * @param bo
	 * @return DeviceAppPageBo 返回类型
	 */
	NetflowPageBo deviceAppNetflowPageSelect(NetflowParamBo bo);

	/**
	 * 根据设备IP，时间段内的时间点等信息，分页查找设备应用流量-highcharts
	 * 
	 * @param bo
	 * @return DeviceAppChartShowBo 返回类型
	 */
	NetflowCharWrapper findDeviceAppChartData(NetflowParamBo bo,
			List<NetflowBo> apps);

}
