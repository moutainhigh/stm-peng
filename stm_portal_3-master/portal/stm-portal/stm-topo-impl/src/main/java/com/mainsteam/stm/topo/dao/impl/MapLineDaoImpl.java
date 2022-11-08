package com.mainsteam.stm.topo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.dao.MapLineDao;
@Repository
public class MapLineDaoImpl extends BaseDao<MapLineDao> implements MapLineDao{
	@Autowired
	@Qualifier(value="stm_topo_mapline_seq")
	private ISequence seq;
	
	@Override
	public List<MapLineBo> getLinks() {
		return getSession().selectList("getLinks");
	}
	
	@Override
	public List<MapLineBo> findByInstanceIds(long[] instanceIds){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("instanceIds", instanceIds);
		return getSession().selectList("findByInstanceIds", map);
	}
	
	@Autowired
	public MapLineDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, MapLineDao.class.getName());
	}
	@Override
	public void add(MapLineBo line) {
		line.setId(seq.next());
		getSession().insert(getNamespace() + "add",line);
	}
	@Override
	public List<MapLineBo> getLines(Long id) {
		Map<String,Long> param = new HashMap<String,Long>(1);
		param.put("mapid", id);
		List<MapLineBo> lines = getSession().selectList(getNamespace() + "getLines",param);
		if(lines==null){
			return new ArrayList<MapLineBo>(0);
		}
		return lines;
	}
	@Override
	public boolean isExsisted(MapLineBo line) {
		int count = getSession().selectOne(getNamespace()+"isExsisted",line);
		return count>0;
	}
	@Override
	public void remove(Long id) {
		Map<String,Long> param = new HashMap<String,Long>(1);
		param.put("id", id);
		getSession().delete(getNamespace()+"remove",param);
	}
	@Override
	public MapLineBo getLineById(Long id) {
		Map<String,Long> param = new HashMap<String,Long>(1);
		param.put("id", id);
		MapLineBo line = getSession().selectOne(getNamespace()+"getLineById",param);
		return line;
	}
	@Override
	public void updateLink(MapLineBo line) {
		getSession().update(getNamespace()+"updateLink",line);
	}
	@Override
	public void unbindLink(Long id) {
		Map<String,Long> param = new HashMap<String,Long>(1);
		param.put("id", id);
		getSession().update(getNamespace()+"unbindLink",param);
	}

}
