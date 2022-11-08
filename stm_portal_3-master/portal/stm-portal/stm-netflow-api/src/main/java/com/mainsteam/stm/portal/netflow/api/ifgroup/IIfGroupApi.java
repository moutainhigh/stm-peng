/**
 * 
 */
package com.mainsteam.stm.portal.netflow.api.ifgroup;

import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * 
 * <li>文件名称: IIfNetflowApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月17日
 * @author   lil
 */
public interface IIfGroupApi {

	/**
	 * 统计各个接口组流量-datagrid
	 * 
	 * @param bo
	 * @return
	 * NetflowPageBo    返回类型
	 */
	NetflowPageBo ifGroupPageSelect(NetflowParamBo bo);
	
	/**
	 * 获取各个接口组流量图表数据-highcharts
	 * 
	 * @param bo
	 * @return
	 * NetflowCharWrapper    返回类型
	 */
	NetflowCharWrapper getIfGroupChartData(NetflowParamBo bo);
	
	/**
	 * 根据接口分组ID获取接口ID
	 *
	 * @param ifGroupId
	 * @return
	 */
	String getIfIdsByGroupId(Long ifGroupId);
}
