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
 * <li>文件名称: IIfProtoDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IIfProtoDao {
	
	/**
	 * 接口协议流量总数
	 *
	 * @param bo
	 * @return
	 */
	@Deprecated
	long getTotalIfProtoNetflows(NetflowParamBo bo);
	
	/**
	 * 接口协议分页查询
	 *
	 * @param page
	 * @return
	 */
	List<NetflowBo> ifProtoPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 接口协议图表数据查询	
	 *
	 * @param bo
	 * @return
	 */
	List<NetflowBo> getIfProtoChartData(NetflowParamBo bo);
	
	/**
	 * 接口流量，包，连接数总量
	 *
	 * @param bo
	 * @return
	 */
	Whole getIfProtoTotals(NetflowParamBo bo);

}
