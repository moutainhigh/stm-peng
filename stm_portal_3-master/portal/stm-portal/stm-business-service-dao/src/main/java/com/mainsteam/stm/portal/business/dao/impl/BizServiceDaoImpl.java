package com.mainsteam.stm.portal.business.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.dao.IBizServiceDao;

public class BizServiceDaoImpl extends BaseDao<BizServiceBo> implements IBizServiceDao {

	public BizServiceDaoImpl(SqlSessionTemplate session) {
		super(session, IBizServiceDao.class.getName());
	}

	@Override
	public List<BizServiceBo> getList() throws Exception{
		return getSession().selectList(getNamespace() + "getList");
	}
	
	@Override
	public void getListPage(Page<BizServiceBo, Object> page) throws Exception{
		getSession().selectList(getNamespace() + "getList",page);
	}
	
	@Override
	public List<BizServiceBo> getByName(String name){
		return getSession().selectList(getNamespace() + "getByName",name);
	}

	@Override
	public List<BizServiceBo> getListByIds(List<Long> ids) {
		return getSession().selectList(getNamespace() + "getListByIds",ids);
	}

	@Override
	public int checkGroupNameIsExsit(String name,String oldName) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("oldName", oldName);
		return getSession().selectOne(getNamespace() + "checkGroupNameIsExsit", map);
	}

	@Override
	public List<String> getSourceIds() {
		return getSession().selectList(getNamespace()+"getSourceIds");
	}

	@Override
	public String getStateById(Long id) {
		return getSession().selectOne(getNamespace()+"getStateById", id);
	}

	@Override
	public BizServiceBo getBizBuessinessById(long id) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace()+"getBizBuessinessById", id);
	}
}
