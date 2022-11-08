package com.mainsteam.stm.common.metric;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.dao.MetricSummaryDAO;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * 指标数据汇总JOB
 * 
 * @author cx
 * 
 */
@DisallowConcurrentExecution
public class MetricSummaryJob implements Job {

	private static final Log logger = LogFactory.getLog(MetricSummaryJob.class);

	private static final String KEY_SCHEDULE = "KEY_SCHEDULE";
	private ScheduleManager scheduleManager;
	private Map<String, Object> initedMetric = new HashMap<>();

	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}

	public void init() {
		MetricSummaryDAO metricSummaryDAO = SpringBeanUtil
				.getBean(MetricSummaryDAO.class);
		CapacityService capacityService = SpringBeanUtil
				.getBean(CapacityService.class);

		for (ResourceDef rdef : capacityService.getResourceDefList()) {
			for (ResourceMetricDef mdef : rdef.getMetricDefs()) {
				if (MetricTypeEnum.PerformanceMetric == mdef.getMetricType()) {
					if (initedMetric.containsKey(mdef.getId())) {
						continue;
					}
					metricSummaryDAO.createTable(mdef.getId(),
							MetricSummaryType.D);
					metricSummaryDAO.createTable(mdef.getId(),
							MetricSummaryType.SH);
					metricSummaryDAO.createTable(mdef.getId(),
							MetricSummaryType.H);
					metricSummaryDAO.createTable(mdef.getId(),
							MetricSummaryType.HH);
					initedMetric.put(mdef.getId(), mdef.getId());
				}
			}
		}

		try {

			{
				HashMap<String, Object> halfHourMap = new HashMap<>();
				halfHourMap.put(KEY_SCHEDULE, MetricSummaryType.HH);
				/** 按半小时汇总 */
				addJob(halfHourMap, "0 15/30 * * * ?",
						"countMetricSummaryForHalfHour");
			}
			{
				HashMap<String, Object> hourMap = new HashMap<>();
				hourMap.put(KEY_SCHEDULE, MetricSummaryType.H);

				/** 按小时汇总：每小时第16分钟执行 */
				addJob(hourMap, "0 16 * * * ?", "countMetricSummaryForHour");
			}
			{
				HashMap<String, Object> dayMap = new HashMap<>();
				dayMap.put(KEY_SCHEDULE, MetricSummaryType.D);
				/** 按天汇总：每天凌晨第19分钟执行 */
				addJob(dayMap, "0 19 0 * * ?", "countMetricSummaryForDay");
			}
			{
				HashMap<String, Object> halfDayMap = new HashMap<>();
				halfDayMap.put(KEY_SCHEDULE, MetricSummaryType.SH);
				/** 按6小时汇总：每6小时第17分钟执行 */
				addJob(halfDayMap, "0 17 0/6 * * ?",
						"countMetricSummaryForSixHour");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void addJob(HashMap<String, Object> hourMap, String cron,
			String jobKey) throws SchedulerException, ClassNotFoundException {
		if (scheduleManager.isExists(jobKey)) {
			scheduleManager.updateJob(jobKey, new IJob(jobKey, this, cron,
					hourMap));
		} else {
			scheduleManager.scheduleJob(new IJob(jobKey, this, cron, hourMap));
		}
	}

	public void countMetricSummary(final Date start, final Date end,
			final MetricSummaryType type) {
		MetricSummaryRunner runner = MetricSummaryRunner.getInstance();
		if (runner != null) {
			runner.countMetricSummary(start, end, type);
		}
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Object summaryJobType = context.getJobDetail().getJobDataMap()
				.get(KEY_SCHEDULE);
		if (logger.isInfoEnabled()) {
			logger.info("MetricSummaryJob[" + summaryJobType + "] start...");
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		Date start, end;
		MetricSummaryType summaryType = null;
		/**
		 * 按小时汇总
		 */
		if (MetricSummaryType.H == summaryJobType) {

			cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 1);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);

			end = cal.getTime();

			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			start = cal.getTime();
			summaryType = MetricSummaryType.H;
		} else if (MetricSummaryType.HH == summaryJobType) {
			int min = cal.get(Calendar.MINUTE);
			if (min >= 30) {
				cal.set(Calendar.MINUTE, 0);
			} else {
				cal.set(Calendar.MINUTE, 30);
				cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 1);
			}
			cal.set(Calendar.SECOND, 0);

			start = cal.getTime();
			if (min >= 30) {
				cal.set(Calendar.MINUTE, 29);
			} else {
				cal.set(Calendar.MINUTE, 59);
			}
			cal.set(Calendar.SECOND, 59);
			end = cal.getTime();
			summaryType = MetricSummaryType.HH;

		} else if (MetricSummaryType.D == summaryJobType) {
			cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 1);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);

			end = cal.getTime();

			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			start = cal.getTime();
			summaryType = MetricSummaryType.D;
		} else if (MetricSummaryType.SH == summaryJobType) {

			cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 1);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);

			end = cal.getTime();

			cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 5);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			start = cal.getTime();
			summaryType = MetricSummaryType.SH;
		} else {
			logger.error("error job,the job's dataMap:"
					+ JSON.toJSONString(context.getJobDetail().getJobDataMap()));
			return;
		}
		try {
			countMetricSummary(start, end, summaryType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
