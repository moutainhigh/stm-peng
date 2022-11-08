package com.mainsteam.stm.topo.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.dao.TopoDhsDao;
@Repository
public class TopoDhsDaoImpl extends BaseDao<OtherNodeBo> implements TopoDhsDao {
	@Autowired
	public TopoDhsDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, TopoDhsDao.class.getName());
	}
	@Override
	public List<OtherNodeBo> getCabinetByInstanceId(Long id) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", id);
		return getSession().selectList("getCabinetByInstanceId",param);
	}

	@Override
	public SubTopoBo getSubTopoById(Long subTopoId) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", subTopoId);
		return getSession().selectOne("getSubTopoById",param);
	}

}
