package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ConfInterfaceBo;
import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupBo;
import com.mainsteam.stm.portal.netflow.dao.IInterfaceGroupDao;
import com.mainsteam.stm.portal.netflow.po.InterfaceGroupPo;

public class InterfaceGroupDaoImpl extends BaseDao<InterfaceGroupPo> implements
		IInterfaceGroupDao {

	public InterfaceGroupDaoImpl(SqlSessionTemplate session) {
		super(session, IInterfaceGroupDao.class.getName());
	}

	@Override
	public List<Map<String, Object>> queryDeviceInterface(String name,
			int[] notIds, int[] ids) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("notIds", notIds);
		map.put("ids", ids);
		return super.getSession().selectList("device_interfacec_tree", map);
	}

	@Override
	public int save(String name, String interfaceIds, String description) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("interfaceIds", interfaceIds);
		map.put("description", description);
		return super.getSession().insert("save_device_interface", map);
	}

	@Override
	public List<InterfaceGroupPo> list() {
		return super.getSession().selectList("query_interface_group");
	}

	@Override
	public List<ConfInterfaceBo> listInterface(int[] ids) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		return super.getSession().selectList("query_interface_list", map);
	}

	@Override
	public List<InterfaceGroupPo> pageSelect(
			Page<InterfaceGroupPo, InterfaceGroupPo> page) {
		return super.getSession().selectList("query_interface_group", page);
	}

	@Override
	public int del(int[] ids) {
		Map<String, Object> map = new HashMap<>();
		map.put("ids", ids);
		return this.getSession().delete("delete_interface_group", map);
	}

	@Override
	public InterfaceGroupBo get(int id) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		return super.getSession().selectOne("get_interface_group", map);
	}

	@Override
	public int update(int id, String name, String interfaceIds,
			String description) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("name", name);
		map.put("interfaceIds", interfaceIds);
		map.put("description", description);
		return super.getSession().update("update_device_interface", map);
	}

	@Override
	public int getCount(String name, Integer id) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("id", id);
		return this.getSession().selectOne("interface_group_getCount", map);
	}
}
