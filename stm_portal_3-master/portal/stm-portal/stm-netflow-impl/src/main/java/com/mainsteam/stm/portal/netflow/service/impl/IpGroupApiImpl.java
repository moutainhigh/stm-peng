package com.mainsteam.stm.portal.netflow.service.impl;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.IIpGroupApi;
import com.mainsteam.stm.portal.netflow.bo.IpGroupBo;
import com.mainsteam.stm.portal.netflow.dao.IIpGroupDao;

public class IpGroupApiImpl implements IIpGroupApi {

	private IIpGroupDao ipGroupDao;

	public void setIpGroupDao(IIpGroupDao ipGroupDao) {
		this.ipGroupDao = ipGroupDao;
	}

	@Override
	public int save(String name, String ips, String description) {
		return this.ipGroupDao.save(name, ips, description);
	}

	@Override
	public List<IpGroupBo> list(Page<IpGroupBo, IpGroupBo> page) {
		return this.ipGroupDao.list(page);
	}

	@Override
	public int del(int[] ids) {
		return this.ipGroupDao.del(ids);
	}

	@Override
	public IpGroupBo get(int id) {
		return this.ipGroupDao.get(id);
	}

	@Override
	public int update(int id, String name, String ips, String description) {
		this.ipGroupDao.del(new int[] { id });
		return this.ipGroupDao.save(name, ips, description);
	}

	@Override
	public int getCount(String name, Integer id) {
		return this.ipGroupDao.getCount(name, id);
	}

}
