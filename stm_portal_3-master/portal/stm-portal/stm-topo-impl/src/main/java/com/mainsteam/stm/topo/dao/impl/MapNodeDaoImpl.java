package com.mainsteam.stm.topo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MapNodeBo;
import com.mainsteam.stm.topo.dao.MapNodeDao;

@Repository
public class MapNodeDaoImpl extends BaseDao<MapNodeDao> implements MapNodeDao{
	@Autowired
	@Qualifier(value="stm_topo_mapnode_seq")
	private ISequence seq;
	
	@Autowired
	public MapNodeDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, MapNodeDao.class.getName());
	}

	@Override
	public MapNodeBo findByNodeIdAndLevel(String nodeid,Integer level) {
		Map<String,Object> param = new HashMap<String,Object>(2);
		param.put("nodeid",nodeid);
		param.put("level",level);
		return getSession().selectOne(getNamespace()+"findByNodeIdAndLevel",param);
	}

	@Override
	public void add(MapNodeBo node) {
		node.setId(seq.next());
		getSession().insert(getNamespace()+"addNode",node);
	}

	@Override
	public void update(MapNodeBo dbnode) {
		getSession().update(getNamespace()+"updateNode",dbnode);
	}
	
	@Override
	public List<MapNodeBo> getNodesByMapId(Long mapid) {
		Map<String,Long> param = new HashMap<String,Long>(1);
		param.put("mapid",mapid);
		List<MapNodeBo> nodes = getSession().selectList(getNamespace()+"getNodesByMapId",param);
		if(nodes==null){
			return new ArrayList<MapNodeBo>(0);
		}
		return nodes;
	}
	@Override
	public void removeByNodeId(String nodeid){
		Map<String,String> param = new HashMap<String,String>(1);
		param.put("nodeid",nodeid);
		getSession().delete(getNamespace()+"removeByNodeId",param);
	}
	@Override
	public MapNodeBo findById(Long id) {
		Map<String,Long> param = new HashMap<String,Long>(1);
		param.put("id",id);
		return getSession().selectOne(getNamespace()+"findById",param);
	}
	@Override
	public void updateNextMapIdAndLevel(Long id, Long nextMapId, Integer level) {
		Map<String,Object> param = new HashMap<String,Object>(3);
		param.put("id",id);
		param.put("nextMapId",nextMapId);
		param.put("level",level);
		getSession().update(getNamespace()+"updateNextMapIdAndLevel",param);
	}
	@Override
	public MapNodeBo getCountryByKey(Long key,Integer level) {
		Map<String,Object> param = new HashMap<String,Object>(3);
		param.put("key",key);
		param.put("level",level);
		List<MapNodeBo> nodes = getSession().selectList(getNamespace()+"getCountryByKey",param);
		if(!nodes.isEmpty()){
			return nodes.get(0);
		}else{
			return null;
		}
	}
	@Override
	public List<Long> instanceIdInLevel(int level) {
		Map<String,Object> param = new HashMap<String,Object>(3);
		param.put("level",level);
		return getSession().selectList(getNamespace()+"instanceIdInLevel",param);
	}

	@Override
	public List<Long> instanceIdInCountry() {
		Map<String,Object> param = new HashMap<String,Object>(1);
		param.put("attr","'%country%'");
		List<String> tmpNodeAttrs = getSession().selectList(getNamespace()+"instanceIdInCountry",param);
		Set<Long> ids = new HashSet<Long>(20);
		for(String attr : tmpNodeAttrs){
			JSONObject attrJson = JSON.parseObject(attr).getJSONObject("country");
			JSONArray tids = attrJson.getJSONArray("ids");
			for(Object tid : tids){
				ids.add(new Long(tid.toString()));
			}
		}
		return new ArrayList<Long>(ids);
	}

	@Override
	public MapNodeBo findAreaKeyByNodeIdAndMapId(MapNodeBo node) {
		return getSession().selectOne(getNamespace()+"findAreaKeyByNodeIdAndMapId",node);
	}
}
