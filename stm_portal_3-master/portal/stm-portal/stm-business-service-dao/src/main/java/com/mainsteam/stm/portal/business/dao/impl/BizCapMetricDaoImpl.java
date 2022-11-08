package com.mainsteam.stm.portal.business.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.bo.BizCapMetricBo;
import com.mainsteam.stm.portal.business.dao.IBizCapMetricDao;

public class BizCapMetricDaoImpl extends BaseDao<BizCapMetricBo> implements IBizCapMetricDao {
	public BizCapMetricDaoImpl(SqlSessionTemplate session) {
		super(session, IBizCapMetricDao.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<BizCapMetricBo> getAllByBizIdAndMetric(long bizid, String name) {
		// TODO Auto-generated method stub
		//getSession().selectOne(getNamespace() + "getAlarmInfo",infoBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bizid", bizid);
		map.put("name", name);
		return getSession().selectList(getNamespace() + "getAllByBizIdAndMetric", map);
	}

	@Override
	public List<Long> getInfoByBizIdAndMetric(long bizid, String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bizid", bizid);
		map.put("name", name);
		return getSession().selectList(getNamespace() + "getInfoByBizIdAndMetric", map);
	}

	@Override
	public int insertInfo(BizCapMetricBo infoBo) {
		// TODO Auto-generated method stub
		return getSession().insert(getNamespace()+"insertInfo", infoBo);
	}

	@Override
	public int deleteInfo(List<Long> idss) {
		// TODO Auto-generated method stub
		Long[] ids= new Long[idss.size()];
		for (int i=0;i<idss.size();i++) {
			ids[i]=idss.get(i);
		}
//		long[] idList = new long[idArray.length];
		return getSession().delete(getNamespace()+"deleteInfo", ids);
	}

	@Override
	public int deleteByInfo(long bizid, String metric) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bizid", bizid);
		map.put("metric", metric);
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace()+"deleteByInfo", map);
	}

}
