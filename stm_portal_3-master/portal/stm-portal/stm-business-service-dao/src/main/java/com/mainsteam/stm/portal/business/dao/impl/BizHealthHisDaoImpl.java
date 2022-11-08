package com.mainsteam.stm.portal.business.dao.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizHealthHisBo;
import com.mainsteam.stm.portal.business.dao.IBizHealthHisDao;

public class BizHealthHisDaoImpl extends BaseDao<BizHealthHisBo> implements IBizHealthHisDao {

	public BizHealthHisDaoImpl(SqlSessionTemplate session) {
		super(session, IBizHealthHisDao.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getBizStatus(long id) {
		// TODO Auto-generated method stub
		SqlSessionTemplate session = getSession();
		Object value = session.selectOne(getNamespace() + "getBizStatus",id);
		if(value == null){
			return 0;
		}
		int status = (int)value;
		return status;
	}

	@Override
	public int insertHealthHis(BizHealthHisBo healthHis) {
		// TODO Auto-generated method stub
		return getSession().insert(getNamespace() + "insertHealthHis",healthHis);
	}

	@Override
	public BizHealthHisBo getBizHealthHis(long id) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getBizHealthHis",id);
	}

	@Override
	public int deleteHealthHisByBizId(long bizId) {
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace() + "deleteHealthHisByBizId",bizId);
	}

	/**
	 * 根据时间范围获取业务的历史健康度信息
	 * @param map
	 * @return
	 */
	@Override
	public List<BizHealthHisBo> getHealthByTimeScope(long bizId,Date startTime,Date endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", bizId);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		return getSession().selectList(getNamespace() + "getHealthByTimeScope",map);
	}

	@Override
	public BizHealthHisBo getHealthFrontFirstScopeByStartTime(long bizId, Date startTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", bizId);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		map.put("startTime", format.format(startTime));
		return getSession().selectOne(getNamespace() + "getHealthFrontFirstScopeByStartTime",map);
	}

}
