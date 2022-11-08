/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.ipgroup.netflow;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IIpGroupProtoApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月22日
 * @author   lil
 */
public interface IIpGroupProtoApi {
	
	/**
	 * 查询IP分组各个协议流量
	 *
	 * @param bo
	 * @return
	 */
	NetflowPageBo ipGroupProtoPageSelect(NetflowParamBo bo);
	
	/**
	 * IP分组某协议在各点流量
	 *
	 * @param bo
	 * @return
	 */
	NetflowCharWrapper getIpGroupProtoChartData(NetflowParamBo bo);

}
