package com.mainsteam.stm.portal.statist.api;

import java.io.File;

import com.mainsteam.stm.portal.statist.bo.StatQChartBo;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMainBo;

public interface IStatistQueryDataApi {
	
	public StatQChartBo getStatQDataByStatQMainId(StatistQueryMainBo statQMainBo, String startTime, String endTime);
	
	public File fillStatQChart(StatistQueryMainBo statQMainBo, String startTime, String endTime, String type) throws Exception;
}
