/**
 * 
 */
package com.mainsteam.stm.common.metric;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.dao.MetricDataDAO;
import com.mainsteam.stm.common.metric.dao.MetricSummaryDAO;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * @author ziw
 * 
 * 
 *         created at 2016年7月25日 下午2:26:32
 */
public class MetricSummaryRunner {
	private static final Log logger = LogFactory
			.getLog(MetricSummaryRunner.class);
	private static final MetricSummaryRunner _self = new MetricSummaryRunner();
	private int maxThread = 1;

	private Executor executor;

	/**
	 * 
	 */
	private MetricSummaryRunner() {
	}

	public static MetricSummaryRunner getInstance() {
		return _self;
	}

	public void init() {
		int coreThread = maxThread;
		final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(
				maxThread);
		executor = new ThreadPoolExecutor(coreThread, maxThread, 0L,
				TimeUnit.MILLISECONDS, queue, new ThreadFactory() {
					int counter = 0;

					@Override
					public Thread newThread(Runnable r) {
						synchronized (this) {
							counter++;
						}
						return new Thread(r, "MetricSummaryJob-" + counter);
					}
				}, new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r,
							ThreadPoolExecutor executor) {
						try {
							queue.put(r);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
	}

	public synchronized void countMetricSummary(final Date start,
			final Date end, final MetricSummaryType type) {
		if (logger.isInfoEnabled())
			logger.info("countMetricSummary[" + type.name() + "," + start
					+ "--" + end + "] started!");
		if(executor == null){
			if (logger.isWarnEnabled()) {
				logger.warn("countMetricSummary has not been inited.");
			}
			return;
		}
		MetricDataDAO metricRealtimeDAO = SpringBeanUtil
				.getBean(MetricDataDAO.class);
		final MetricSummaryDAO metricSummaryDAO = SpringBeanUtil
				.getBean(MetricSummaryDAO.class);
		CapacityService capacityService = SpringBeanUtil
				.getBean(CapacityService.class);
		if (metricRealtimeDAO == null) {
			return;
		}
		Map<String,String> table_str = new HashMap<>(1000);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				// 自定义指标
				logger.info("countCustomMetricSummary[" + type.name() + ","
						+ start + "--" + end + "] started!");
				metricSummaryDAO.addCustomMetricSummary(start, end, type);
				logger.info("countCustomMetricSummary finish!");
			}
		});
		for (ResourceDef rdef : capacityService.getResourceDefList()) {
			for (final ResourceMetricDef mdef : rdef.getMetricDefs()) {
				if (MetricTypeEnum.PerformanceMetric == mdef.getMetricType()
						&& !table_str.containsKey(mdef.getId().toUpperCase())) {
					if (mdef.getId().length() > 23)
						continue;
					executor.execute(new Runnable() {
						@Override
						public void run() {
							logger.info("computeMetricSummary[" + mdef.getId()
									+ "," + type.name() + "," + start + "--"
									+ end + "] started!");
							try {
								metricSummaryDAO.summaryMetricData(
										mdef.getId(),
										new Timestamp(start.getTime()),
										new Timestamp(end.getTime()), type);
							} catch (Exception e) {
								if (logger.isErrorEnabled())
									logger.error(e.getMessage(), e);
							}
							logger.info("computeMetricSummary[" + mdef.getId()
									+ "," + type.name() + "," + start + "--"
									+ end + "] finish!");
						}
					});
					table_str.put(mdef.getId().toUpperCase(), null);
				}
			}
		}
	}

	public void runSummaryJob(final Date start, final Date end,
			final MetricSummaryType type) {
		countMetricSummary(start, end, type);
	}
}
