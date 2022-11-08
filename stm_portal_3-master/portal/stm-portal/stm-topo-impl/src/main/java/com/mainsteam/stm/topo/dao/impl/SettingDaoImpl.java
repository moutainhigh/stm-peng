package com.mainsteam.stm.topo.dao.impl;


import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.dao.ISettingDao;

public class SettingDaoImpl extends BaseDao<SettingBo> implements ISettingDao{
	
	@Override
	public List<SettingBo> getOtherSetting(Long subTopoId) {
		return this.getSession().selectList(getNamespace()+"getOtherSetting",subTopoId);
	}
	
	private ISequence seq;
	public SettingDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, ISettingDao.class.getName());
		this.seq=seq;
	}
	public int save(SettingBo bo){
		if(null == bo.getId()) bo.setId(this.getSeq().next());
		return getSession().insert(getNamespace()+"save", bo);
	}
	@Override
	public SettingBo getCfg(String key) {
		SettingBo sp = getSession().selectOne(getNamespace()+"getCfg", key);
		return sp;
	}
	@Override
	public int updateCfg(SettingBo bo) {
		return getSession().update(getNamespace()+"updateCfg",bo);
	}
	@Override
	public ISequence getSeq() {
		return this.seq;
	}
	@Override
	public List<SettingBo> getAll() {
		return this.getSession().selectList(getNamespace()+"getAll");
	}
	

	@Override
	public void truncateAll() {
		this.getSession().delete(getNamespace()+"truncateAll");
	}
	@Override
	public int deleteCfg(String key) {
		return this.getSession().delete(getNamespace()+"deleteCfg",key);
	}
}
