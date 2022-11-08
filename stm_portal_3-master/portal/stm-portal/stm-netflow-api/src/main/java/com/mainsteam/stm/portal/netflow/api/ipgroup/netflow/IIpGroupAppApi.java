/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.ipgroup.netflow;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IIpGroupAppApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月19日
 * @author   lil
 */
public interface IIpGroupAppApi {

	/**
	 * IP分组下的应用
	 *
	 * @param bo
	 * @return
	 */
	NetflowPageBo ipGroupAppPageSelect(NetflowParamBo bo);
	
	/**
	 * IP分组应用图表数据
	 *
	 * @param bo
	 * @return
	 */
	NetflowCharWrapper getIpGroupAppChartData(NetflowParamBo bo);
	
}
