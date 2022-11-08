package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ResourceDeviceBo;
import com.mainsteam.stm.portal.netflow.po.DevicePo;

public interface IDeviceDao extends IBaseDao<DevicePo> {

	int save(String name, String ip, String type, int resourceId,
			String manufacturers);

	int updateSaveDeviceByManager(String name, String ip, String type,
			int resourceId, String manufacturers);

	List<Map<String, Object>> getDevice();

	Page<ResourceDeviceBo, ResourceDeviceBo> list(
			Page<ResourceDeviceBo, ResourceDeviceBo> page);

	int addInterface(int deviceId, int interfaceId, String name, int index,
			Long ifSpeed);

	int updateInterface(int deviceId, int interfaceId, String name, int index,
			Long ifSpeed);

	List<Integer> isInterface(Set<Long> interfaceIds);

	int delInterface(int[] interfaceIds);

	int delDevice(int[] ids);

	int queryInterfaceCount();

	int delDeviceByManagerByInterface(int[] ids);
}
