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
 * <li>文件名称: IIfTerminalDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IIfTerminalDao {
	
	/**
	 * 查询接口终端流量
	 *
	 * @param page
	 * @return
	 */
	List<NetflowBo> ifTerminalPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 查询接口所有终端流量
	 *
	 * @param bo
	 * @return
	 */
	long getTotalIfTerminalNetflows(NetflowParamBo bo);

	/**
	 * 查询流量，包，连接数总量
	 *
	 * @param bo
	 * @return
	 */
	Whole getIfTerminalTotals(NetflowParamBo bo);

	/**
	 * 查询接口终端在各个时间点的流量
	 *
	 * @param bo
	 * @return
	 */
	List<NetflowBo> getIfTerminalChartData(NetflowParamBo bo);
	
	/**
	 * 接口终端总包数
	 *
	 * @param bo
	 * @return
	 */
	Long getTotalIfTerminalPackets(NetflowParamBo bo);
	
	/**
	 * 接口终端总连接数
	 *
	 * @param bo
	 * @return
	 */
	Long getTotalIfTerminalConnects(NetflowParamBo bo);
	
}
