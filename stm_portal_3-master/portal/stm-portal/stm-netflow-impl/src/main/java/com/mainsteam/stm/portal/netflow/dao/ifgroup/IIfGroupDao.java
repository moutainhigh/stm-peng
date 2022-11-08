/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.ifgroup;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.OptionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IIfGroupDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月17日
 * @author   lil
 */
public interface IIfGroupDao {
	
	/**
	 * 查询各个接口组流量
	 *
	 * @param page
	 * @return
	 */
	List<NetflowBo> ifGroupPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 各个接口组在各个时间点的流量
	 *
	 * @param bo
	 * @return
	 */
	List<NetflowBo> getIfGroupChartData(NetflowParamBo bo);
	
	/**
	 * 接口组总流量
	 *
	 * @param bo
	 * @return
	 */
	long getTotalIfGroupNetflows(NetflowParamBo bo);
	
	/**
	 * 根据接口组ID获取所有接口ID
	 *
	 * @param ifGroupId
	 * @return
	 */
	String getIfIdsByGroupId(Long ifGroupId);
	
	/**
	 * 获取所有接口组接口ID
	 *
	 * @return
	 */
	List<OptionBo> getIfGroupIfIds();
	
	/**
	 * 接口组流量，包，连接数
	 *
	 * @param bo
	 * @return
	 */
	Whole getIfGroupTotals(NetflowParamBo bo);

}
