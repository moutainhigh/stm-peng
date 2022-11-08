/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.inf;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IIfSessionDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IIfSessionDao {
	
	/**
	 * 查询接口会话所有流量
	 *
	 * @param bo
	 * @return
	 */
	long getTotalIfSessionNetflows(NetflowParamBo bo);
	
	/**
	 * 查询接口会话流量-datagrid
	 *
	 * @param bo
	 * @return
	 */
	List<NetflowBo> ifSessionPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 查询接口会话流量-highcharts
	 *
	 * @param bo
	 * @return
	 */
	List<NetflowBo> getIfSessionChartData(NetflowParamBo bo);
	
	/**
	 * 查询接口会话流量，包，连接数总素
	 *
	 * @param bo
	 * @return
	 */
	Whole getIfSessionTotals(NetflowParamBo bo);

}
