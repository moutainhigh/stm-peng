package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.IpGroupBo;

public interface IIpGroupDao {

	int save(String name, String ips, String description);

	List<IpGroupBo> list(Page<IpGroupBo, IpGroupBo> page);

	int del(int[] ids);

	IpGroupBo get(int id);

	int getCount(String name, Integer id);
}
