package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ResourceDeviceBo;
import com.mainsteam.stm.portal.netflow.dao.IDeviceDao;
import com.mainsteam.stm.portal.netflow.po.DevicePo;

public class DeviceDaoImpl extends BaseDao<DevicePo> implements IDeviceDao {

	public DeviceDaoImpl(SqlSessionTemplate session) {
		super(session, IDeviceDao.class.getName());
	}

	@Override
	public int save(String name, String ip, String type, int resourceId,
			String manufacturers) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("ip", ip);
		map.put("type", type);
		map.put("resourceId", resourceId);
		map.put("manufacturers", manufacturers);
		return this.getSession().insert("saveDeviceByManager", map);
	}

	@Override
	public int updateSaveDeviceByManager(String name, String ip, String type,
			int resourceId, String manufacturers) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("ip", ip);
		map.put("type", type);
		map.put("resourceId", resourceId);
		map.put("manufacturers", manufacturers);
		return this.getSession().insert("updateSaveDeviceByManager", map);
	}

	@Override
	public List<Map<String, Object>> getDevice() {
		return this.getSession().selectList("getDeviceByManager");
	}

	@Override
	public Page<ResourceDeviceBo, ResourceDeviceBo> list(
			Page<ResourceDeviceBo, ResourceDeviceBo> page) {
		page.setDatas(this.getSession().<ResourceDeviceBo> selectList(
				"queryDeviceByManager", page));
		return page;
	}

	@Override
	public int addInterface(int deviceId, int interfaceId, String name,
			int index, Long ifSpeed) {
		Map<String, Object> map = new HashMap<>();
		map.put("deviceId", deviceId);
		map.put("interfaceId", interfaceId);
		map.put("name", name);
		map.put("index", index);
		map.put("ifSpeed", ifSpeed);
		return this.getSession().insert("addInterfaceByManager", map);
	}

	@Override
	public List<Integer> isInterface(Set<Long> interfaceIds) {
		Map<String, Object> map = new HashMap<>();
		map.put("interfaceIds", interfaceIds);
		return this.getSession().selectList("isInterfaceByManager", map);
	}

	@Override
	public int delInterface(int[] interfaceIds) {
		Map<String, Object> map = new HashMap<>();
		map.put("interfaceIds", interfaceIds);
		return this.getSession().delete("delInterfaceByManager", map);
	}

	@Override
	public int delDevice(int[] ids) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		return this.getSession().delete("delDeviceByManager", map);
	}

	@Override
	public int queryInterfaceCount() {
		return this.getSession().selectOne("device_queryInterfaceCount");
	}

	@Override
	public int updateInterface(int deviceId, int interfaceId, String name,
			int index, Long ifSpeed) {
		Map<String, Object> map = new HashMap<>();
		map.put("deviceId", deviceId);
		map.put("interfaceId", interfaceId);
		map.put("name", name);
		map.put("index", index);
		map.put("ifSpeed", ifSpeed);
		return this.getSession().update("updateInterface", map);
	}

	@Override
	public int delDeviceByManagerByInterface(int[] ids) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		return this.getSession().delete("delDeviceByManagerByInterface", map);
	}

}
