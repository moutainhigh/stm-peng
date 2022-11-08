package com.mainsteam.stm.portal.business.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizUserRelBo;
import com.mainsteam.stm.portal.business.dao.IBizUserRelDao;

public class BizUserRelDaoImpl extends BaseDao<BizUserRelBo> implements IBizUserRelDao {

	public BizUserRelDaoImpl(SqlSessionTemplate session) {
		super(session, IBizUserRelDao.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<BizUserRelBo> getUserlistByBizId(long bizId, String name, long domainId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("biz_id", bizId);
		map.put("account", name);
		map.put("domain_id", domainId);
		return getSession().selectList(getNamespace() + "getUserlistByBizId", map);
	}

	@Override
	public List<BizMainBo> getBizlistByUserId(long user_id) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getBizlistByUserId", user_id);
	}

	@Override
	public List<BizUserRelBo> checkUserView(long user_id, long biz_id) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("user_id", user_id);
		map.put("biz_id", biz_id);
		return getSession().selectList(getNamespace() + "checkUserView", map);
	}

	@Override
	public int deleteByBizId(List<Long> biz_ids, List<Long> user_ids) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("biz_ids", biz_ids);
		map.put("user_ids", user_ids);
		return getSession().delete(getNamespace() + "deleteByBizId", map);
	}

	@Override
	public int insertSet(BizUserRelBo bo) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("biz_id", bo.getBiz_id());
		map.put("user_id", bo.getUser_id());
		return getSession().insert(getNamespace() + "insertSet", map);
	}

	@Override
	public int getCount(long biz_id, List<Long> user_ids) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("biz_id", biz_id);
		map.put("user_ids", user_ids);
		return getSession().selectOne(getNamespace() + "getCount", map);
	}
}
