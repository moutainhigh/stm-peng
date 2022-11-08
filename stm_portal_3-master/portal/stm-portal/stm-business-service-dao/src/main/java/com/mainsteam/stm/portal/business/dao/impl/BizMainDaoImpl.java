package com.mainsteam.stm.portal.business.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.dao.IBizMainDao;

public class BizMainDaoImpl  extends BaseDao<BizMainBo> implements IBizMainDao {

	public BizMainDaoImpl(SqlSessionTemplate session) {
		super(session, IBizMainDao.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public int insertBasicInfo(BizMainBo bo) {
		// TODO Auto-generated method stub
		return getSession().insert(getNamespace() + "insertBasicInfo", bo);
	}

	@Override
	public BizMainBo getBasicInfo(long id) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getBasicInfo", id);
	}
	
	@Override
	public String getCanvasStatusDefine(long id) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getCanvasStatusDefine", id);
	}

	@Override
	public List<BizMainBo> getAllList() {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getAllList");
	}

	@Override
	public List<BizMainBo> getAllPermissionsInfoList() {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getAllPermissionsInfoList");
	}

	@Override
	public List<BizMainBo> getAllStatusDefineList() {
		return getSession().selectList(getNamespace() + "getAllStatusDefineList");
	}
	
	@Override
	public int updateBizStatusDefine(BizMainBo bo) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace() + "updateBizStatusDefine", bo);
	}

	@Override
	public List<BizMainBo> getBizListForSearch(String name) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getBizListForSearch", name);
	}
	
	@Override
	public List<BizMainBo> getBizList(BizMainBo bo) {
		return getSession().selectList(getNamespace() + "getBizList", bo);
	}

	@Override
	public int checkBizNameIsExsit(String name,String oldName) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("oldName", oldName);
		return getSession().selectOne(getNamespace() + "checkBizNameIsExsit", map);
	}

	@Override
	public int updateBasicInfo(BizMainBo bo) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace() + "updateBasicInfo", bo);
	}

	@Override
	public int deleteBizById(long id) {
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace() + "deleteBizById", id);
	}

	@Override
	public Date getCreateTime(long id) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getCreateTime", id);
	}

	@Override
	public int getBizCountByManagerId(long manangerId) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getBizCountByManagerId", manangerId);
	}

}
