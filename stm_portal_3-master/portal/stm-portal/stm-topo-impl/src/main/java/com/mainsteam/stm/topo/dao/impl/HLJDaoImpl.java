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
import com.mainsteam.stm.topo.bo.HLJNode;
import com.mainsteam.stm.topo.dao.HLJDao;
@Repository
public class HLJDaoImpl extends BaseDao<HLJNode> implements HLJDao {
	@Autowired
	@Qualifier(value="stm_topo_mapnode_seq")
	private ISequence seq;
	@Autowired
	public HLJDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, HLJDao.class.getName());
	}
	@Override
	public void addNode(HLJNode node) {
		if(node.getMapId().equals(node.getNextMapId())){
			node.setNextMapId(null);
		}
		node.setId(seq.next());
		getSession().insert(getNamespace()+"addNode",node);
	}
	@Override
	public void updateInstanceIds(HLJNode dbNode) {
		getSession().update(getNamespace()+"updateInstanceIds",dbNode);
	}
	@Override
	public HLJNode getById(Long id) {
		return getSession().selectOne(getNamespace()+"getById",id);
	}
	@Override
	public List<HLJNode> getNodesByMapId(String projection,Integer key) {
		Map<String,Object> param = new HashMap<String,Object>();
		if(projection==null){
			projection="*";
		}
		param.put("projection", projection);
		param.put("mapId", key);
		return getSession().selectList(getNamespace()+"getNodesByMapId",param);
	}
	@Override
	public void delNullRelation(Integer parentMapId, Integer mapId) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("mapId", parentMapId);
		param.put("nextMapId", mapId);
		getSession().selectList(getNamespace()+"delNullRelation",param);
	}
	@Override
	public boolean existRelation(Integer parentMapId, Integer mapId) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("mapId", parentMapId);
		param.put("nextMapId", mapId);
		return (int)getSession().selectOne(getNamespace()+"existRelation",param)>0;
	}
	@Override
	public boolean existNullRelation(Integer parentMapId, Integer mapId) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("mapId", parentMapId);
		param.put("nextMapId", mapId);
		return (int)getSession().selectOne(getNamespace()+"existNullRelation",param)>0;
	}
	@Override
	public List<HLJNode> getNodesByNextMapId(Integer mapId) {
		return getSession().selectList(getNamespace()+"getNodesByNextMapId",mapId);
	}
}
