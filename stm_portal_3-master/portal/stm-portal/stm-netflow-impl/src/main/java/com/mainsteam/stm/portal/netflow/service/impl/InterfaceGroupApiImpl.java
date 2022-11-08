package com.mainsteam.stm.portal.netflow.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.IInterfaceGroupApi;
import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupBo;
import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupPageBo;
import com.mainsteam.stm.portal.netflow.bo.zTreeBo;
import com.mainsteam.stm.portal.netflow.dao.IInterfaceGroupDao;
import com.mainsteam.stm.portal.netflow.po.InterfaceGroupPo;

public class InterfaceGroupApiImpl implements IInterfaceGroupApi {

	private IInterfaceGroupDao interfaceGroupDao;

	public void setInterfaceGroupDao(IInterfaceGroupDao interfaceGroupDao) {
		this.interfaceGroupDao = interfaceGroupDao;
	}

	@Override
	public List<zTreeBo> queryDeviceInterface(String name, int[] notIds,
			int[] ids) {
		List<Map<String, Object>> list = this.interfaceGroupDao
				.queryDeviceInterface(name, notIds, ids);
		List<zTreeBo> treeBos = new ArrayList<>();
		if (list != null) {
			zTreeBo tree = null;
			int oldId = -1;
			for (Map<String, Object> map : list) {
				int newId = (int) map.get("device_id");
				if (oldId != newId) {
					oldId = newId;
					tree = new zTreeBo();
					tree.setId("d_" + newId + "");
					tree.setName((String) map.get("device_name"));
					tree.setChildren(new ArrayList<zTreeBo>());
					tree.setIsParent(true);
					tree.setOpen(true);
					treeBos.add(tree);
				}
				zTreeBo e = new zTreeBo();
				e.setId(((int) map.get("interface_id")) + "");
				e.setName((String) map.get("interface_name"));
				tree.getChildren().add(e);
			}
		}
		return treeBos;
	}

	@Override
	public int save(String name, String interfaceIds, String description) {
		return this.interfaceGroupDao.save(name, interfaceIds, description);
	}

	@Override
	public List<InterfaceGroupBo> list() {
		List<InterfaceGroupPo> interfaceGroupPos = this.interfaceGroupDao
				.list();
		List<InterfaceGroupBo> interfaceGroupBos = new ArrayList<>();
		for (InterfaceGroupPo po : interfaceGroupPos) {
			InterfaceGroupBo bo = new InterfaceGroupBo();
			interfaceGroupBos.add(bo);
			bo.setId(po.getId());
			bo.setName(po.getName());
			bo.setDescription(po.getDescription());
			String[] idsStr = po.getInterfaceIds().split(",");
			int[] ids = new int[idsStr.length];
			for (int i = 0; i < idsStr.length; i++) {
				ids[i] = Integer.parseInt(idsStr[i]);
			}
			bo.setInterfaceBos(this.interfaceGroupDao.listInterface(ids));
		}
		return interfaceGroupBos;
	}

	@Override
	public InterfaceGroupPageBo pageSelect(long startRecord, long pageSize,
			String interfaceGroupName, String order) {
		InterfaceGroupPo ifgPo = new InterfaceGroupPo();
		if (interfaceGroupName != null && !"".equals(interfaceGroupName)) {
			ifgPo.setDescription(interfaceGroupName);
		}
		if (order != null && !"".equals(order)) {
			ifgPo.setInterfaceIds(order);
		}
		Page<InterfaceGroupPo, InterfaceGroupPo> page = new Page<InterfaceGroupPo, InterfaceGroupPo>(
				startRecord, pageSize, ifgPo);
		List<InterfaceGroupPo> interfaceGroupPos = this.interfaceGroupDao
				.pageSelect(page);
		List<InterfaceGroupBo> interfaceGroupBos = new ArrayList<>();
		for (InterfaceGroupPo po : interfaceGroupPos) {
			InterfaceGroupBo bo = new InterfaceGroupBo();
			interfaceGroupBos.add(bo);
			bo.setId(po.getId());
			bo.setName(po.getName());
			bo.setDescription(po.getDescription());
			if (po.getInterfaceIds() != null
					&& !"".equals(po.getInterfaceIds().trim())) {
				String[] idsStr = po.getInterfaceIds().split(",");
				int[] ids = new int[idsStr.length];
				for (int i = 0; i < idsStr.length; i++) {
					ids[i] = Integer.parseInt(idsStr[i]);
				}
				bo.setInterfaceBos(this.interfaceGroupDao.listInterface(ids));
			}
		}
		InterfaceGroupPageBo pageBo = new InterfaceGroupPageBo();
		pageBo.setInterfaceGroupBos(interfaceGroupBos);
		pageBo.setRowCount(page.getRowCount());
		pageBo.setStartRow(page.getStartRow());
		pageBo.setTotalRecord(page.getTotalRecord());
		return pageBo;
	}

	@Override
	public int del(int[] ids) {
		return this.interfaceGroupDao.del(ids);
	}

	@Override
	public InterfaceGroupBo get(int id) {
		return this.interfaceGroupDao.get(id);
	}

	@Override
	public int update(int id, String name, String interfaceIds,
			String description) {
		return this.interfaceGroupDao.update(id, name, interfaceIds,
				description);
	}

	@Override
	public int getCount(String name, Integer id) {
		return this.interfaceGroupDao.getCount(name, id);
	}
}
