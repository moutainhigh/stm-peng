package com.mainsteam.stm.portal.business.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizStatusSelfBo;
import com.mainsteam.stm.portal.business.dao.IBizStatusSelfDao;

public class BizStatusSelfDaoImpl extends BaseDao<BizStatusSelfBo> implements IBizStatusSelfDao {

	public BizStatusSelfDaoImpl(SqlSessionTemplate session) {
		super(session, IBizStatusSelfDao.class.getName());
	}

	@Override
	public List<BizStatusSelfBo> getByBizSerId(long bizSerId) {
		return getSession().selectList(getNamespace()+"getByBizSerId", bizSerId);
	}

	@Override
	public void delByBizSerId(long bizSerId) {
		getSession().delete(getNamespace() + "delByBizSerId", bizSerId);
	}

	@Override
	public void delByBizSerIdAndInstanceIds(long bizSerId, Long[] instanceIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bizSerId", bizSerId);
		map.put("instanceIds", instanceIds);
		getSession().delete(getNamespace() + "delByBizSerIdAndInstanceIds", map);
	}

	@Override
	public void delByInstanceId(long instanceId) {
		getSession().delete(getNamespace() + "delByInstanceId", instanceId);
	}
}
