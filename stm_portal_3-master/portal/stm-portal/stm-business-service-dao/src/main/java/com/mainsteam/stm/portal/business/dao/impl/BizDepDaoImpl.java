package com.mainsteam.stm.portal.business.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizDepBo;
import com.mainsteam.stm.portal.business.dao.IBizDepDao;

public class BizDepDaoImpl extends BaseDao<BizDepBo> implements IBizDepDao {

	public BizDepDaoImpl(SqlSessionTemplate session) {
		super(session, IBizDepDao.class.getName());
	}

	@Override
	public List<BizDepBo> getList(Integer type) {
		return getSession().selectList(getNamespace() + "getList",type);
	}

	@Override
	public List<BizDepBo> getListByIds(List<Long> ids) {
		return getSession().selectList(getNamespace() + "getListByIds",ids);
	}
	@Override
	public int checkGroupNameIsExsit(String name,String oldName,int type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("oldName", oldName);
		map.put("type", type);
		return getSession().selectOne(getNamespace() + "checkGroupNameIsExsit", map);
	}
}
