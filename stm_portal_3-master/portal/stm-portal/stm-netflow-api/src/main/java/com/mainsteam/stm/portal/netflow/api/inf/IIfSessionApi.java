/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.inf;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IIfSessionApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IIfSessionApi {
	
	/**
	 * 接口会话流量查询-datagrid
	 *
	 * @param bo
	 * @return
	 */
	NetflowPageBo ifSessionPageSelect(NetflowParamBo bo);

	/**
	 * 接口会话流量各时间点查询-highcharts
	 *
	 * @param bo
	 * @return
	 */
	NetflowCharWrapper getIfSessionChartData(NetflowParamBo bo);
	
}
