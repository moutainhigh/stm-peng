package com.mainsteam.stm.portal.statist.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMainBo;
import com.mainsteam.stm.portal.statist.dao.IStatistQueryDetailDao;
import com.mainsteam.stm.portal.statist.po.StatistQueryInstancePo;
import com.mainsteam.stm.portal.statist.po.StatistQueryMainPo;
import com.mainsteam.stm.portal.statist.po.StatistQueryMetricPo;

public class StatistQueryDetailDaoImpl extends BaseDao<StatistQueryMainBo> implements IStatistQueryDetailDao {

	public StatistQueryDetailDaoImpl(SqlSessionTemplate session) {
		super(session, IStatistQueryDetailDao.class.getName());
	}

	@Override
	public int insertStatQMain(StatistQueryMainPo statQMainPo) {
		return getSession().insert(getNamespace() + "insertSQMain", statQMainPo);
	}
	
	@Override
	public int insertStatQMetric(StatistQueryMetricPo statQMetricPo){
		return getSession().insert(getNamespace() + "insertSQMetric", statQMetricPo);
	}

	@Override
	public int insertStatQInstance(StatistQueryInstancePo statQInstancePo) {
		return getSession().insert(getNamespace() + "insertSQInstance", statQInstancePo);
	}

	@Override
	public List<StatistQueryMainPo> getAllSQMain() {
		return getSession().selectList(getNamespace() + "getAllSQMain");
	}

	@Override
	public StatistQueryMainPo getSQMainByStatQId(Long statQId) {
		return getSession().selectOne(getNamespace() + "getSQMainByStatQId", statQId);
	}

	@Override
	public List<StatistQueryInstancePo> getSQInstanceByStatQId(Long statQId) {
		return getSession().selectList(getNamespace() + "getSQInstanceByStatQId", statQId);
	}

	@Override
	public List<StatistQueryMetricPo> getSQMetricByStatQId(Long statQId) {
		return getSession().selectList(getNamespace() + "getSQMetricByStatQId", statQId);
	}

	@Override
	public int delSQMainByStatQId(Long statQMainId) {
		return getSession().update(getNamespace() + "delSQMainByStatQId", statQMainId);
	}

	@Override
	public int updateStatQMain(StatistQueryMainPo statQMainPo) {
		return getSession().update(getNamespace() + "updateStatQMain", statQMainPo);
	}

	@Override
	public int delSQInstanceByStatQId(Long statQMainId) {
		return getSession().delete(getNamespace() + "delSQInstanceByStatQId", statQMainId);
	}

	@Override
	public int delSQMetricByStatQId(Long statQMainId) {
		return getSession().delete(getNamespace() + "delSQMetricByStatQId", statQMainId);
	}

	@Override
	public List<Long> getSQMainIdByInstIdList(List<Long> instIdList) {
		return getSession().selectList(getNamespace() + "getSQMainIdByInstIdList", instIdList);
	}

	@Override
	public int countSQMainByTypeAndName(StatistQueryMainPo statQMainPo) {
		return getSession().selectOne(getNamespace() + "countSQMainByTypeAndName", statQMainPo);
	}
	
}
