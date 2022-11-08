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
 * <li>文件名称: IIfIpgroupDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IIfIpgroupDao {
	
	/**
	 * 查询接口IP分组流量
	 *
	 * @param page
	 * @return
	 */
	List<NetflowBo> ifIpgroupPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 查询接口IP分组highcharts流量
	 *
	 * @param bo
	 * @return
	 */
	List<NetflowBo> getIfIpgroupChartData(NetflowParamBo bo);
	
	/**
	 * 查询接口IP分组总流量
	 *
	 * @param bo
	 * @return
	 */
	long getTotalIfIpgroupNetflows(NetflowParamBo bo);
	
	/**
	 * 接口IP分组流量，包，连接数查询
	 *
	 * @param bo
	 * @return
	 */
	Whole getIfIpgroupTotals(NetflowParamBo bo);

}
