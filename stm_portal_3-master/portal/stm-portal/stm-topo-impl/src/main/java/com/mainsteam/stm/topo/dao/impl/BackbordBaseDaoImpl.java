package com.mainsteam.stm.topo.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.BackbordBo;
import com.mainsteam.stm.topo.dao.IBackbordBaseDao;

public class BackbordBaseDaoImpl extends BaseDao<BackbordBo> implements IBackbordBaseDao{

	private ISequence seq;

	@Override
	public List<BackbordBo> getBackbordByVendor(String vendor) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("vendor", vendor);
		return this.select("selectByVendor", map);
	}

	@Override
	public int deleteAll() {
		return this.getSession().delete(getNamespace()+"delAll");
	}

	@Override
	public int save(BackbordBo bo) {
		bo.setId(this.seq.next());
		return getSession().insert(getNamespace()+"save", bo);
	}
	
	@Override
	public BackbordBo getBackbordBaseInfo(String vendor,String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("vendor", vendor);
		map.put("type", type);
		return this.get("selectByVentorType", map);
	}

	public BackbordBaseDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IBackbordBaseDao.class.getName());
		this.seq = seq;
	}
	
	@Override
	public ISequence getSeq() {
		return this.seq;
	}
}
