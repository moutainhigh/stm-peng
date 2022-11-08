package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ConfInterfaceBo;
import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupBo;
import com.mainsteam.stm.portal.netflow.po.InterfaceGroupPo;

public interface IInterfaceGroupDao extends IBaseDao<InterfaceGroupPo> {

	List<Map<String, Object>> queryDeviceInterface(String name, int[] notIds,
			int[] ids);

	int save(String name, String interfaceIds, String description);

	List<InterfaceGroupPo> list();

	List<ConfInterfaceBo> listInterface(int[] ids);

	List<InterfaceGroupPo> pageSelect(
			Page<InterfaceGroupPo, InterfaceGroupPo> page);

	int del(int[] ids);

	InterfaceGroupBo get(int id);

	int update(int id, String name, String interfaceIds, String description);

	int getCount(String name, Integer id);
}
