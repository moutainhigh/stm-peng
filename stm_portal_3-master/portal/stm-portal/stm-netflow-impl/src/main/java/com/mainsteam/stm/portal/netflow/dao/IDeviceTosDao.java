/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IDeviceTosDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public interface IDeviceTosDao {
	
	/**
	 * 查询设备tos的总流量
	 * 
	 * @param bo
	 * @return
	 * long    返回类型
	 */
	Whole getTotalTosNetflowsOfDevice(NetflowParamBo bo);
	
	/**
	 * 查询设备tos流量dao方法-datagrid
	 * 
	 * @param bo
	 * @return
	 * List<NetflowPageBo>    返回类型
	 */
	List<NetflowBo> deviceTosNetflowPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 查询设备tos流量dao方法-highcharts
	 * 
	 * @param bo
	 * @return
	 * NetflowCharWrapper    返回类型
	 */
	List<NetflowBo> getDeviceTosChartData(NetflowParamBo bo);

}
