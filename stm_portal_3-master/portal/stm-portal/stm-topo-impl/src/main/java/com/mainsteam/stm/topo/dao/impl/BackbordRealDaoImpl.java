package com.mainsteam.stm.topo.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.BackbordBo;
import com.mainsteam.stm.topo.dao.IBackbordRealDao;

public class BackbordRealDaoImpl extends BaseDao<BackbordBo> implements IBackbordRealDao{

	private ISequence seq;

	@Override
	public int save(BackbordBo backbordBo) {
		if(null == backbordBo.getId()) backbordBo.setId(this.getSeq().next());
		return this.getSession().update(getNamespace()+"insert", backbordBo);
	}
	
	@Override
	public void truncateAll() {
		this.getSession().delete(getNamespace()+"truncateAll");
	}
	
	@Override
	public List<BackbordBo> getAll() {
		return this.getSession().selectList(getNamespace()+"getAll");
	}

	@Override
	public int batchAddUpdateInfo(Long baseId, String info) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("baseId", baseId);
		map.put("info", info);
		
		return this.getSession().update(getNamespace()+"batchAddUpdateInfo", map);
	}
	
	@Override
	public int batchUpdateBackbordRealInfo(Long baseId, String info) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("baseId", baseId);
		map.put("info", info);
		
		return this.getSession().update(getNamespace()+"batchUpdateInfo", map);
	}
	
	@Override
	public int addUpdateInfo(Long instanceId, String info) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("instanceId", instanceId);
		map.put("info", info);
		return this.getSession().update(getNamespace()+"addUpdateInfo", map);
	}

	@Override
	public int addOrUpdateBackbordRealInfo(Long baseId,Long instanceId, String info) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.getSeq().next());
		map.put("baseId", baseId);
		map.put("instanceId", instanceId);
		map.put("info", info);
		return this.getSession().update(getNamespace()+"insertOrUpdate", map);
	}

	@Override
	public BackbordBo getBackbordRealInfo(Long instanceId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("instanceId", instanceId);
		return this.get("selectByInstanceId", map);
	}
	
	public BackbordRealDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IBackbordRealDao.class.getName());
		this.seq = seq;
	}
	
	@Override
	public ISequence getSeq() {
		return this.seq;
	}

}
