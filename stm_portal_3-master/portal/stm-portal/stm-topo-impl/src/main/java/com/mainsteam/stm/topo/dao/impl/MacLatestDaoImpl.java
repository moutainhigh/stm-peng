package com.mainsteam.stm.topo.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MacLatestBo;
import com.mainsteam.stm.topo.dao.IMacLatestDao;

public class MacLatestDaoImpl extends BaseDao<MacLatestBo> implements IMacLatestDao{
	private ISequence seq;
	
	@Override
	public int updateByMac(String mac, String hostName) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mac", mac);
		map.put("hostName", hostName);
		return this.getSession().update(getNamespace()+"updateByMac", map);
	}
	
	@Override
	public int deleteAll() {
		return this.getSession().delete(getNamespace()+"delAll");
	}
	
	@Override
	public int deleteMacLatestByIds(List<Long> ids) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("ids", ids);
		return this.del("delByIds", map);
	}
	
	@Override
	public int insert(MacLatestBo macLatestBo) {
		if(null == macLatestBo.getId()) macLatestBo.setId(this.getSeq().next());
		return this.getSession().insert(this.getNamespace()+"insert", macLatestBo);
	}
	
	@Override
	public List<MacLatestBo> getAllMacLatestBos() {
		return this.getSession().selectList(getNamespace()+"selectAll");
	}
	
	@Override
	public List<MacLatestBo> getMacLatestBosByIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		return this.select("selectByIds", map);
	}

	@Override
	public void selectByPage(Page<MacLatestBo, MacLatestBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	public MacLatestDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IMacLatestDao.class.getName());
		this.seq = seq;
	}

	@Override
	public ISequence getSeq() {
		return this.seq;
	}

	@Override
	public List<MacLatestBo> getAll() {
		return this.getSession().selectList(getNamespace()+"getAll");
	}

}
