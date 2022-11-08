package com.mainsteam.stm.common.metric.report;

import java.util.List;
import com.mainsteam.stm.obj.TimePeriod;

public class AvailableMetricCountQuery {
	private List<TimePeriod> timePeriods;
	private List<Long> instanceIDes;
	public List<Long> getInstanceIDes() {
		return instanceIDes;
	}
	public void setInstanceIDes(List<Long> instanceIDes) {
		this.instanceIDes = instanceIDes;
	}
	public List<TimePeriod> getTimePeriods() {
		return timePeriods;
	}
	public void setTimePeriods(List<TimePeriod> timePeriods) {
		this.timePeriods = timePeriods;
	}
}
