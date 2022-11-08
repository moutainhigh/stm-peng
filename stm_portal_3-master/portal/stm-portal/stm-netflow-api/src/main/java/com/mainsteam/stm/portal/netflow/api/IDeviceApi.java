package com.mainsteam.stm.portal.netflow.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ResourceDeviceBo;
import com.mainsteam.stm.portal.netflow.bo.ResourceInterfaceBo;

public interface IDeviceApi {

	List<ResourceDeviceBo> getAllResourceDevice();

	List<ResourceInterfaceBo> getIntercface(long id, String type);

	boolean save(String[] infos);

	Page<ResourceDeviceBo, ResourceDeviceBo> list(
			Page<ResourceDeviceBo, ResourceDeviceBo> page);

	int addInterface(int deviceId, List<ResourceInterfaceBo> data);

	int delInterface(int[] interfaceIds);

	int delDevice(int[] ids);
}
