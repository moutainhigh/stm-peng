package com.mainsteam.stm.topo.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.dao.FTMSDao;
@Repository
public class FTMSDaoImpl extends BaseDao<MapLineBo> implements FTMSDao {
	@Autowired
	public FTMSDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, FTMSDao.class.getName());
	}
	@Override
	public List<MapLineBo> getLinesInMap(Long mapid) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("fields","id,instanceId,fromId,toId");
		param.put("mapId",mapid);
		return getSession().selectList(getNamespace()+"getLinesInMap",param);
	}
	@Override
	public MapLineBo getLineById(Long lineId) {
		return getSession().selectOne(getNamespace()+"getLineById",lineId);
	}
}
