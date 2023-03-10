package com.mainsteam.stm.metric.cleanJob;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.InitializingBean;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.metric.cleanJob.dao.CleanJobDAO;
import com.mainsteam.stm.metric.cleanJob.dao.CleanJobDAOImpl;
import com.mainsteam.stm.util.DateUtil;
import com.mainsteam.stm.util.PropertiesFileUtil;
import com.mainsteam.stm.util.SpringBeanUtil;

public class CleanJob implements Job, InitializingBean {
	private static final Log logger = LogFactory.getLog(CleanJob.class);

	private ScheduleManager scheduleManager;

	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		cleanHistory();
		cleanSummery();
	}
	
	public void start(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Date d = calcHistoryDate();
				if(d != null){
					if (logger.isInfoEnabled()) {
						logger.info("start clean cleanHistoryOmit metricData");
					}
					CleanJobDAO cleanJobDao = SpringBeanUtil.getBean(CleanJobDAO.class);
					try {
						cleanJobDao.cleanHistoryByComputeMinTime(d);
					} catch (Exception e) {
						if (logger.isErrorEnabled())
							logger.error("cleanHistoryOmit:", e);
					}
					if (logger.isInfoEnabled()) {
						logger.info("cleanHistoryOmit end.");
					}
				}
			}
		},"CleanJob-cleanHistoryOmit").start();
	}

	/**
	 * 
	 */
	private void cleanHistory() {
		//logger.info("start clean history metricData");
		try {
			Date calcDate = calcHistoryDate();
			if(calcDate != null){
				CleanJobDAO cleanJobDao = SpringBeanUtil.getBean(CleanJobDAO.class);
				cleanJobDao.cleanHistory(calcDate);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}
	}
	
	private Date calcHistoryDate(){
		try {
			Properties pp = PropertiesFileUtil.getProperties(CleanJobDAOImpl.class.getClassLoader(), "properties/stm.properties");
			Integer lifetime =  Integer.parseInt(pp.getProperty("metric.history.lifetime", "7"));
			Calendar cal=Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DAY_OF_YEAR, -lifetime);
			return cal.getTime(); 
		} catch (Exception e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}
		return null; 
	}

	/**
	 */
	private void cleanSummery() {
		if (logger.isInfoEnabled())
		logger.info("start clean summary metricData");

		Properties pp = getProperties();

		CleanJobDAO cleanJobDao = SpringBeanUtil.getBean(CleanJobDAO.class);

		for (MetricSummaryType type : MetricSummaryType.values()) {
			try {
				String name = "metric.summery." + type.name().toLowerCase()
						+ ".lifetime";
				Integer lifetime = Integer
						.parseInt(pp.getProperty(name, "365"));
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR,-lifetime);
				if (logger.isInfoEnabled())
				logger.info("find MetricSummaryType's name:"
						+ name
						+ ",lifetime:"
						+ lifetime
						+ ",clean time:"
						+ DateUtil.format(cal.getTime(),
								DateUtil.DEFAULT_DATETIME_FORMAT));

				cleanJobDao.cleanSummery(type, cal.getTime());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private Properties getProperties() {
		return PropertiesFileUtil.getProperties(
				CleanJob.class.getClassLoader(), "properties/stm.properties");
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		final String JOBKEY = "metricCleanJob";

//		Properties pp = getProperties();
		/**
		 * ????????????????????????????????????????????????????????????job??????????????????????????????????????????????????????????????????dhs?????????????????????????????????
		 * 
		 * ???????????????????????????????????????????????????????????????????????????????????????
		 * 
		 * ?????????????????????????????????job???????????????????????????????????????????????????????????????????????????????????????
		 * 
		 * modify by ziw at 2016???8???9??? ??????10:32:03
		 */
		String cornExpress = "59 59 23 * * ?";

		/** ???????????? */
		logger.info("add metricCleanJob!");
		try {
			if (scheduleManager.isExists(JOBKEY)) {
				scheduleManager.updateJob(JOBKEY, new IJob(JOBKEY, this,
						cornExpress));
			} else {
				// ????????????1???????????????Job
				scheduleManager
						.scheduleJob(new IJob(JOBKEY, this, cornExpress));
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ProfileDispatchJob error!", e);
			}
		}
	}
}
