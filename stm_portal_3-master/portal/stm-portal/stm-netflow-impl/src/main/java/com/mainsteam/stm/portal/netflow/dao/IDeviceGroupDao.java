package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ConfDeviceBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupBo;
import com.mainsteam.stm.portal.netflow.po.DeviceGroupPo;

public interface IDeviceGroupDao extends IBaseDao<DeviceGroupPo> {

	List<ConfDeviceBo> getAllDevice(String name, int[] notIds);

	int save(String name, String description, String deviceIds);

	List<DeviceGroupBo> list(Page<DeviceGroupBo, DeviceGroupBo> page);

	List<ConfDeviceBo> getDevice(int[] ids);

	int del(int[] ids);

	DeviceGroupBo getDeviceGroup(long id);

	int update(int id, String name, String description, String deviceIds);

	int getCount(String name, Integer id);
}
