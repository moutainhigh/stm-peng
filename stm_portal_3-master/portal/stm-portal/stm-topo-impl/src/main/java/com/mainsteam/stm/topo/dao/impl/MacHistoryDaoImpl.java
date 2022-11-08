package com.mainsteam.stm.topo.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MacHistoryBo;
import com.mainsteam.stm.topo.dao.IMacHistoryDao;

public class MacHistoryDaoImpl extends BaseDao<MacHistoryBo> implements IMacHistoryDao{
	private ISequence seq;
	
	@Override
	public void truncateAll() {
		this.getSession().delete(getNamespace()+"truncateAll");
	}
	
	@Override
	public int insert(MacHistoryBo macHistoryBo) {
		if(null == macHistoryBo.getId()) macHistoryBo.setId(this.getSeq().next());
		return this.getSession().insert(this.getNamespace()+"insert", macHistoryBo);
	}
	
	@Override
	public int deleteHistoryIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("ids", ids);
		return this.del("delByIds", map);
	}

	@Override
	public List<MacHistoryBo> getMacHistoryBosByIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		return this.select("selectByIds", map);
	}
	
	@Override
	public int deleteHistoryByMac(String[] macs) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("macs", macs);
		return this.getSession().delete("delByMacs", map);
	}
	
	@Override
	public void selectSubHistoryByPage(Page<MacHistoryBo, MacHistoryBo> page) {
		this.select("selectSubPage", page);
	}
	
	@Override
	public void selectByPage(Page<MacHistoryBo, MacHistoryBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	public MacHistoryDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IMacHistoryDao.class.getName());
		this.seq = seq;
	}

	@Override
	public ISequence getSeq() {
		return this.seq;
	}

	@Override
	public List<MacHistoryBo> getAll() {
		return this.getSession().selectList(getNamespace()+"getAll");
	}

}
