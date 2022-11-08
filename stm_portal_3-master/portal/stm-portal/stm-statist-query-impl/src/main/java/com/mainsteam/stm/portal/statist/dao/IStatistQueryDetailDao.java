package com.mainsteam.stm.portal.statist.dao;

import java.util.List;

import com.mainsteam.stm.portal.statist.po.StatistQueryInstancePo;
import com.mainsteam.stm.portal.statist.po.StatistQueryMainPo;
import com.mainsteam.stm.portal.statist.po.StatistQueryMetricPo;

public interface IStatistQueryDetailDao {
	
	public int insertStatQMain(StatistQueryMainPo statQMainPo);
	
	public int insertStatQMetric(StatistQueryMetricPo statQMetricPo);
	
	public int insertStatQInstance(StatistQueryInstancePo statQInstancePo);
	
	public List<StatistQueryMainPo> getAllSQMain();
	
	public StatistQueryMainPo getSQMainByStatQId(Long statQId);
	
	public List<StatistQueryInstancePo> getSQInstanceByStatQId(Long statQId);

	public List<StatistQueryMetricPo> getSQMetricByStatQId(Long statQId);

	public int delSQMainByStatQId(Long statQMainId);
	
	public int updateStatQMain(StatistQueryMainPo statQMainPo);
	
	public int delSQInstanceByStatQId(Long statQMainId);

	public int delSQMetricByStatQId(Long statQMainId);
	
	public List<Long> getSQMainIdByInstIdList(List<Long> instIdList);

	public int countSQMainByTypeAndName(StatistQueryMainPo statQMainPo);
}
