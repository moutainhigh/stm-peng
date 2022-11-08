package com.mainsteam.stm.portal.netflow.service.impl;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.IDeviceGroupApi;
import com.mainsteam.stm.portal.netflow.bo.ConfDeviceBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupBo;
import com.mainsteam.stm.portal.netflow.dao.IDeviceGroupDao;

public class DeviceGroupImpl implements IDeviceGroupApi {

	private IDeviceGroupDao deviceGroupDao;

	public void setDeviceGroupDao(IDeviceGroupDao deviceGroupDao) {
		this.deviceGroupDao = deviceGroupDao;
	}

	@Override
	public List<ConfDeviceBo> getAllDevice(String name, int[] notIds) {
		return this.deviceGroupDao.getAllDevice(name, notIds);
	}

	@Override
	public int save(String name, String description, String deviceIds) {
		return this.deviceGroupDao.save(name, description, deviceIds);
	}

	@Override
	public List<DeviceGroupBo> list(Page<DeviceGroupBo, DeviceGroupBo> page) {
		List<DeviceGroupBo> list = this.deviceGroupDao.list(page);
		for (DeviceGroupBo bo : list) {
			String[] idsStr = bo.getDeviceIds().split(",");
			int[] ids = new int[idsStr.length];
			for (int i = 0; i < idsStr.length; i++) {
				ids[i] = Integer.parseInt(idsStr[i]);
			}
			List<ConfDeviceBo> devices = this.deviceGroupDao.getDevice(ids);
			StringBuilder names = new StringBuilder();
			for (ConfDeviceBo confDeviceBo : devices) {
				names.append(confDeviceBo.getName()).append(",");
			}
			if (names.length() > 0) {
				bo.setDeviceIds(names.substring(0, names.length() - 1));
			}
		}
		return list;
	}

	@Override
	public int del(int[] ids) {
		return this.deviceGroupDao.del(ids);
	}

	@Override
	public DeviceGroupBo get(long id) {
		return this.deviceGroupDao.getDeviceGroup(id);
	}

	@Override
	public List<ConfDeviceBo> getDevice(int[] ids) {
		return this.deviceGroupDao.getDevice(ids);
	}

	@Override
	public int update(int id, String name, String description, String deviceIds) {
		return this.deviceGroupDao.update(id, name, description, deviceIds);
	}

	@Override
	public int getCount(String name, Integer id) {
		return this.deviceGroupDao.getCount(name, id);
	}
}
