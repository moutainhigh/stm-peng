package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.portal.netflow.bo.SystemParameterBo;
import com.mainsteam.stm.portal.netflow.dao.ISystemParameterDao;

@Repository("systemParameterDao")
public class SystemParameterDaoImpl implements ISystemParameterDao {

	@Resource(name = "portal_netflow_sqlSession")
	private SqlSessionTemplate session;

	@Override
	public SystemParameterBo getServiePort() {
		return this.session.selectOne("getServiePort");
	}

	@Override
	public int update(int port) {
		Map<String, Object> map = new HashMap<>();
		map.put("port", port + "");
		return this.session.update("updateServicePort", map);
	}
}
