package com.mainsteam.stm.portal.resource.dao;

import java.util.List;

import com.mainsteam.stm.portal.resource.bo.BatdisckvBo;

public interface IBatDiscKvDao {
	public List<BatdisckvBo> getList();

	public int initBatDiscKvTable(List<BatdisckvBo> list);
}
