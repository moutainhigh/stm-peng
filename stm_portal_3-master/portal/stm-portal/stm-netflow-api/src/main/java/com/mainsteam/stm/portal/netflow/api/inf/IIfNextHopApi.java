/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.inf;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IIfNextHopApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IIfNextHopApi {
	
	/**
	 * 接口下一跳流量
	 * 
	 * @param bo
	 * @return
	 * NetflowPageBo    返回类型
	 */
	NetflowPageBo ifNextHopPageSelect(NetflowParamBo bo);
	
	/**
	 * 接口下一跳图表数据
	 * 
	 * @param bo
	 * @return
	 * NetflowCharWrapper    返回类型
	 */
	NetflowCharWrapper getIfNextHopChartData(NetflowParamBo bo);

}
