package com.mainsteam.stm.portal.netflow.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ConfDeviceBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupBo;

public interface IDeviceGroupApi {

	List<ConfDeviceBo> getAllDevice(String name, int[] notIds);

	int save(String name, String description, String deviceIds);

	List<DeviceGroupBo> list(Page<DeviceGroupBo, DeviceGroupBo> page);

	int del(int[] ids);

	DeviceGroupBo get(long id);

	List<ConfDeviceBo> getDevice(int[] ids);

	int update(int id, String name, String description, String deviceIds);

	int getCount(String name, Integer id);
}
