package com.mainsteam.stm.topo.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.TopoIconBo;
import com.mainsteam.stm.topo.dao.IIconDao;

public class IconDaoImpl extends BaseDao<TopoIconBo> implements IIconDao{

	private ISequence seq;

	@Override
	public List<TopoIconBo> getIiconsByIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("ids", ids);
		return this.getSession().selectList(getNamespace()+"getIconsByIds", map);
	}

	@Override
	public int deleteImgesByIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("ids", ids);
		return this.del("delByIds", map);
	}

	@Override
	public List<TopoIconBo> getIicons(String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type);
		return this.getSession().selectList(getNamespace()+"getIconsByType", map);
	}

	@Override
	public int save(TopoIconBo bo) {
		if(null == bo.getId()) bo.setId(this.getSeq().next());
		return getSession().insert(getNamespace()+"save", bo);
	}

	public IconDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IIconDao.class.getName());
		this.seq = seq;
	}
	
	@Override
	public ISequence getSeq() {
		return this.seq;
	}

	@Override
	public List<TopoIconBo> getAll() {
		return this.getSession().selectList(getNamespace()+"getAll");
	}

	@Override
	public void truncateAll() {
		this.getSession().delete(getNamespace()+"truncateAll");
	}
}
