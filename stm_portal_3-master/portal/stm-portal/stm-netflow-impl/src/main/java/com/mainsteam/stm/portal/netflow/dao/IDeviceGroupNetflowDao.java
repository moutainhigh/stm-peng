package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowQueryBo;
import com.mainsteam.stm.portal.netflow.bo.HighCharts;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.OptionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.po.FlowPo;

public interface IDeviceGroupNetflowDao {

	List<FlowPo> getInFlow(
			Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> page);

	List<FlowPo> getOutFlow(
			Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> page);

	int getDeviceGroupByCount();

	HighCharts getTu(long start, long end, String tableSuffix,
			int[] deviceGroupIds, String order, String asc);
	
	long findDeviceListTotalFlows(NetflowParamBo bo);
	
	List<NetflowBo> deviceListPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	List<NetflowBo> queryDeviceFlowOfTimePoint(NetflowParamBo bo);
	
	String getDeviceIdsByGroupId(Long deviceGroupId);
	
	Long getDeviceIpById(String id);
	
	/**
	 * 查询设备组下所有设备ID
	 *
	 * @return
	 */
	List<OptionBo> findAllDeviceGroupDeviceIps();
	
	/**
	 * 设备ID查询IP
	 *
	 * @param ids
	 * @return
	 */
	List<OptionBo> getDeviceIpsByIds(String ids);
	
	/**
	 * 设备组的流量，包，连接数
	 *
	 * @param bo
	 * @return
	 */
	Whole getDeviceGroupTotals(NetflowParamBo bo);
	
}
