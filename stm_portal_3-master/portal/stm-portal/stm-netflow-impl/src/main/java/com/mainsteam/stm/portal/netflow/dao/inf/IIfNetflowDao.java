/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.inf;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

/**
 * <li>文件名称: IIfNetflowDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IIfNetflowDao {

	/**
	 * 获取接口的总流量
	 * 
	 * @param bo
	 * @return
	 * long    返回类型
	 */
	long getTotalIfNetflows(NetflowParamBo bo);
	
	/**
	 * 统计设备接口流量-datagrid
	 * 
	 * @param bo
	 * @return
	 * NetflowPageBo    返回类型
	 */
	List<NetflowBo> ifListPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 获取接口流量图表数据-highcharts
	 * 
	 * @param bo
	 * @return
	 * NetflowCharWrapper    返回类型
	 */
	List<NetflowBo> getIfNetflowChartData(NetflowParamBo bo);
	
	/**
	 * 通过接口分组ID查询所有接口ID
	 *
	 * @param ifGroupId
	 * @return
	 */
	List<Long> getIfIdsByIfGroupId(long ifGroupId);
	
	/**
	 * 接口总包数
	 *
	 * @param bo
	 * @return
	 */
	long getTotalIfPackets(NetflowParamBo bo);
	
	/**
	 * 接口总连接数
	 *
	 * @param bo
	 * @return
	 */
	long getTotalConnects(NetflowParamBo bo);
	
}
