package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ConfDeviceBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupBo;
import com.mainsteam.stm.portal.netflow.dao.IDeviceGroupDao;
import com.mainsteam.stm.portal.netflow.po.DeviceGroupPo;

public class DeviceGroupDaoImpl extends BaseDao<DeviceGroupPo> implements
		IDeviceGroupDao {

	public DeviceGroupDaoImpl(SqlSessionTemplate session) {
		super(session, IDeviceGroupDao.class.getName());
	}

	@Override
	public List<ConfDeviceBo> getAllDevice(String name, int[] notIds) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("notIds", notIds);
		return this.getSession().selectList("getAllDevice", map);
	}

	@Override
	public int save(String name, String description, String deviceIds) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("description", description);
		map.put("deviceIds", deviceIds);
		return this.getSession().insert("save_device_group", map);
	}

	@Override
	public List<DeviceGroupBo> list(Page<DeviceGroupBo, DeviceGroupBo> page) {
		return super.getSession().selectList("query_device_group", page);
	}

	@Override
	public List<ConfDeviceBo> getDevice(int[] ids) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		return this.getSession().selectList("getAllDevice", map);
	}

	@Override
	public int del(int[] ids) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		return this.getSession().delete("del_device_group", map);
	}

	@Override
	public DeviceGroupBo getDeviceGroup(long id) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		return super.getSession().selectOne("get_device_group", map);
	}

	@Override
	public int update(int id, String name, String description, String deviceIds) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("name", name);
		map.put("description", description);
		map.put("deviceIds", deviceIds);
		return this.getSession().update("update_device_group", map);
	}

	@Override
	public int getCount(String name, Integer id) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("id", id);
		return this.getSession().selectOne("device_getCount", map);
	}

}
