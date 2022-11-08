/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.ipgroup.netflow;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IIpGroupTosApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月22日
 * @author   lil
 */
public interface IIpGroupTosApi {
	
	/**
	 * 查询IP分组各tos流量
	 *
	 * @param bo
	 * @return
	 */
	NetflowPageBo ipGroupTosPageSelect(NetflowParamBo bo);
	
	/**
	 * 查询IP分组下某tos不同时间点的流量
	 *
	 * @param bo
	 * @return
	 */
	NetflowCharWrapper getIpGroupAppChartData(NetflowParamBo bo);

}
