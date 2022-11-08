package com.mainsteam.stm.common.metric.report;

import java.util.List;

public interface AvailableMetricDataReportService {
	public List<AvailableMetricCountData> findAvailableCount(AvailableMetricCountQuery query);
}
