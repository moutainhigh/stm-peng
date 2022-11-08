package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IDeviceDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月26日
 * @author   lil
 */
public interface IDeviceFlowDao {
	
	/**
	 * 
	 * 根据参数分页查询设备流量 
	 * 
	 */
	List<NetflowBo> deviceListPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	/**
	 * 根据时间查询所有设备接口的总流量，用于计算某一设备的占比 
	 * 
	 */
	Whole findDeviceListTotalFlows(NetflowParamBo bo);
	
	/**
	 * 
	 * 
	 * @param m
	 * @return
	 * List<DeviceFlowChartBo>    返回类型
	 */
	List<NetflowBo> queryDeviceFlowOfTimePoint(NetflowParamBo bo);

}
