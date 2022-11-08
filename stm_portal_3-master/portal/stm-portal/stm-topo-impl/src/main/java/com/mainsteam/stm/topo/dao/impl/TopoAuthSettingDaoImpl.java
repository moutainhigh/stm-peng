package com.mainsteam.stm.topo.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.dao.ITopoAuthSettingDao;

@Repository
public class TopoAuthSettingDaoImpl extends BaseDao<TopoAuthSettingBo> implements ITopoAuthSettingDao{
	@Autowired
	@Qualifier(value="stm_topo_auth_setting_seq")
	private ISequence seq;

	@Override
	public List<TopoAuthSettingBo> getAll() {
		return this.getSession().selectList(getNamespace()+"getAll");
	}

	@Override
	public int deleteBySubtopoId(Long subtopoId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("subtopoId", subtopoId);
		return this.getSession().delete(getNamespace()+"delBySubtopoId", map);
	}

	@Override
	public int save(TopoAuthSettingBo settingBo) {
		if(null == settingBo.getId()) settingBo.setId(this.getSeq().next());
		return this.getSession().update(getNamespace()+"insert", settingBo);
	}
	
	@Autowired
	public TopoAuthSettingDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, ITopoAuthSettingDao.class.getName());
	}

	@Override
	public List<TopoAuthSettingBo> getAuthSettingBosByTopoId(Long topoId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("topoId", topoId);
		return this.select("selectByTopoId", map);
	}

	public ISequence getSeq() {
		return seq;
	}

	@Override
	public boolean hasAuth(Long userId, Long topoId, List<String> modes) {
		if(modes==null) return false;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("topoId", topoId);
		map.put("userId", userId);
		map.put("modes", modes);
		List<TopoAuthSettingBo> authes = this.getSession().selectList(getNamespace()+"hasAuth", map);
		if(modes.size()==1 && modes.get(0).equals(TopoAuthSettingBo.SELECT)){
			//如果没有设置权限，读权限返回true
			map.put("mode", TopoAuthSettingBo.SELECT);
			//表示设置了读权限
			int count = this.getSession().selectOne(getNamespace()+"hasSetAuth", map);
			if(count > 0){
				return authes.size()>0;
			}else{
				return true;
			}
		}
		return authes.size()>0;
	}

	@Override
	public List<Long> getAllReadOnlyTopo(Long userId) {
		if(null!=userId){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			return this.getSession().selectList(getNamespace()+"getAllReadOnlyTopo", map);
		}
		return null;
	}

	@Override
	public void truncateAll() {
		this.getSession().delete(getNamespace()+"truncateAll");
	}

	@Override
	public TopoAuthSettingBo getAuthSetting(Long userId, Long topoId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("topoId", topoId);
		map.put("userId", userId);
		
		return this.getSession().selectOne(getNamespace()+"selectByUserIdTopoId", map);
	}

}
