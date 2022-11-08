package com.mainsteam.stm.metric.cleanJob.dao;

import java.util.Date;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;

public interface CleanJobDAO {
	
	public void cleanHistory(Date fromTime);
	
	public void cleanSummery(MetricSummaryType type, Date fromTime);
	
	public Date cleanHistoryByComputeMinTime(Date fromTime);
}
