package com.mainsteam.stm.portal.netflow.api;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupNetflowQueryBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;

public interface IDeviceGroupNetflowApi {

	Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> list(
			Page<DeviceGroupNetflowBo, DeviceGroupNetflowQueryBo> page);
	
	NetflowPageBo deviceListPageSelect(NetflowParamBo bo);
	
	NetflowCharWrapper deviceFlowChartPageSelect(NetflowParamBo bo);
	
	String getDeviceIdsByGroupId(Long deviceGroupId);
}
