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
 * <li>文件名称: IIfNextHopDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IIfNextHopDao {
	
	/**
	 * 查询接口所有下一跳流量
	 * 
	 * @param bo
	 * @return
	 * long    返回类型
	 */
	@Deprecated
	long getTotalIfNextHopNetflows(NetflowParamBo bo);

	/**
	 * 查询接口下一跳-datagrid
	 * 
	 * @param page
	 * @return
	 * List<NetflowBo>    返回类型
	 */
	List<NetflowBo> ifNextHopPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 查询接口下一跳-highcharts
	 * 
	 * @param bo
	 * @return
	 * List<NetflowBo>    返回类型
	 */
	List<NetflowBo> getIfNextHopChartData(NetflowParamBo bo);
	
	/**
	 * 接口下一跳流量，包，连接数的总量
	 *
	 * @param bo
	 * @return
	 */
	Whole getIfNextHopTotal(NetflowParamBo bo);
}
