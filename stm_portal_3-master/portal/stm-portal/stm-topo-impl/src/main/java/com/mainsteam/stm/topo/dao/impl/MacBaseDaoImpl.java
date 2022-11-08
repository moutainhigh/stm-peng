package com.mainsteam.stm.topo.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.dao.IMacBaseDao;

public class MacBaseDaoImpl extends BaseDao<MacBaseBo> implements IMacBaseDao{
	private ISequence seq;
	
	/**
	 * 新增或修改
	 * @param macBaseBo
	 */
	public int addOrUpdate(MacBaseBo macBaseBo){
		//查询是否存在
		MacBaseBo bo = this.getMacBaseByMac(macBaseBo.getMac());
		int rows = 0;
		if(null == bo){	//新增
			rows = this.insert(macBaseBo);
		}else{	//修改
			macBaseBo.setId(bo.getId());
			rows = this.update(macBaseBo);
		}
		return rows;
	}
	
	@Override
	public int insert(MacBaseBo macBaseBo) {
		if(null == macBaseBo.getId()) macBaseBo.setId(this.getSeq().next());
		return this.getSession().update(this.getNamespace()+"insert", macBaseBo);
	}
	
	@Override
	public int update(MacBaseBo macBaseBo) {
		return this.getSession().update(getNamespace()+"updateById", macBaseBo);
	}
	
	@Override
	public List<MacBaseBo> getMacBaseBosByMacs(List<String> macs) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("macs", macs);
		return this.select("selectByMacs", map);
	}

	@Override
	public List<MacBaseBo> getAllMacBaseBos() {
		return this.getSession().selectList(getNamespace()+"selectAll");
	}
	
	@Override
	public MacBaseBo getMacBaseByMac(String mac) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mac", mac);
//		logger.info("getMacBaseByMac参数mac=>>"+mac+"<<");
		return this.getSession().selectOne(getNamespace()+"selectByMac", map);
	}

	@Override
	public int deleteAll() {
		return this.getSession().delete(getNamespace()+"delAll");
	}

	@Override
	public int deleteByIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		return this.getSession().delete(getNamespace()+"delByIds", map);
	}

	@Override
	public int insertOrUpdate(MacBaseBo macBaseBo) {
		if(null == macBaseBo.getId()) macBaseBo.setId(this.getSeq().next());
		return this.getSession().update(getNamespace()+"insertOrUpdate", macBaseBo);
	}

	@Override
	public List<MacBaseBo> getMacBaseBosByIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		return this.select("selectByIds", map);
	}

	@Override
	public void selectByPage(Page<MacBaseBo, MacBaseBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	public MacBaseDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IMacBaseDao.class.getName());
		this.seq = seq;
	}

	@Override
	public ISequence getSeq() {
		return this.seq;
	}

	@Override
	public List<MacBaseBo> getAll() {
		return this.getSession().selectList(getNamespace()+"selectAll");
	}

}
