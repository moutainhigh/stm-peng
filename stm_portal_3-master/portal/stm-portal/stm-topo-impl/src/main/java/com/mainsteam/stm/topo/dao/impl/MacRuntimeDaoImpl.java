package com.mainsteam.stm.topo.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import com.mainsteam.stm.topo.dao.IMacRuntimeDao;

public class MacRuntimeDaoImpl extends BaseDao<MacRuntimeBo> implements IMacRuntimeDao{
	private ISequence seq;
	

	@Override
	public List<MacRuntimeBo> getMacByHostIp(String hostIp) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ip", hostIp);
		return this.getSession().selectList(getNamespace()+"selectByHostIp",map);
	}

	@Override
	public List<MacRuntimeBo> getMacByHostMac(String hostMac) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mac", hostMac);
		return this.getSession().selectList(getNamespace()+"selectByHostMac",map);
	}
	
	@Override
	public int updateByMac(String mac, String hostName) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mac", mac);
		map.put("hostName", hostName);
		return this.getSession().update(getNamespace()+"updateByMac", map);
	}

	@Override
	public List<MacRuntimeBo> getMacInfos(String ip, String intface) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ip", ip);
		map.put("interface", intface);
		return this.getSession().selectList(getNamespace()+"selectUps",map);
	}
	
	@Override
	public List<MacRuntimeBo> getAllMacRuntimeBos() {
		return this.getSession().selectList(getNamespace()+"selectAll");
	}
	
	@Override
	public int deleteAll() {
		return this.getSession().delete(getNamespace()+"delAll");
	}

	@Override
	public int insert(MacRuntimeBo macRuntimeBo) {
		if(null == macRuntimeBo.getId()) macRuntimeBo.setId(this.getSeq().next());
		return this.getSession().update(this.getNamespace()+"insert", macRuntimeBo);
	}

	@Override
	public List<MacRuntimeBo> getMacRuntimeBosByIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		return this.select("selectByIds", map);
	}

	@Override
	public void selectByPage(Page<MacRuntimeBo, MacRuntimeBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	public MacRuntimeDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IMacRuntimeDao.class.getName());
		this.seq = seq;
	}

	@Override
	public ISequence getSeq() {
		return this.seq;
	}

	@Override
	public List<MacRuntimeBo> getAll() {
		return this.getSession().selectList(getNamespace()+"selectAll");
	}
}
