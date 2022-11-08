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
 * <li>文件名称: IIfTosDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月15日
 * @author   lil
 */
public interface IIfTosDao {
	
	/**
	 * 分页查询tos应用流量使用-datagrid
	 * 
	 * @param page
	 * @return
	 * List<DeviceFlowBo>    返回类型
	 */
	List<NetflowBo> ifTosPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 获取某一接口所有tos的流量
	 * 
	 * @param bo
	 * @return
	 * long    返回类型
	 */
	long getTotalIfTosNetflows(NetflowParamBo bo);
	
	/**
	 * 查询接口某一tos在各时间点的流量
	 * 
	 * @param m
	 * @return
	 * List<Long>    返回类型
	 */
	List<NetflowBo> getIfTosChartData(NetflowParamBo bo);
	
	/**
	 * 接口tos的流量，包，连接数
	 *
	 * @param bo
	 * @return
	 */
	Whole getIfTosTotals(NetflowParamBo bo);

}
