package com.mainsteam.stm.topo.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.topo.bo.MapNodeBo;
import com.mainsteam.stm.topo.dao.GfMapDao;
@Repository
public class GfMapDaoImpl extends BaseDao<MapNodeBo> implements GfMapDao {
	@Autowired
	public GfMapDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, GfMapDao.class.getName());
	}

	@Override
	public List<MapNodeBo> getAllInstanceNodes() {
		List<MapNodeBo> nodes = getSession().selectList(getNamespace()+"getAllInstanceNodes");
		return nodes;
	}

}
