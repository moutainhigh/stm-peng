//package com.mainsteam.stm.event;
//
//import java.util.Calendar;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import com.mainsteam.stm.common.event.ResourceEventService;
//import com.mainsteam.stm.job.IJob;
//import com.mainsteam.stm.job.ScheduleManager;
//import com.mainsteam.stm.util.SpringBeanUtil;
//
//
///** 告警事件存档JOB
// * @author cx
// *
// */
//public class ResourceEventHistoryJob implements Job{
//	private static final Log logger=LogFactory.getLog(ResourceEventHistoryJob.class);
//	
//	private ScheduleManager scheduleManager;
//	
//	public void setScheduleManager(ScheduleManager scheduleManager) {
//		this.scheduleManager = scheduleManager;
//	}
//	
//	public void init(){
//		try {
//			scheduleManager.deleteJob("countMetricSummaryForHour");
//			
//			//每月1号凌晨3点整执行
////			scheduleManager.scheduleJob(new IJob("countMetricSummaryForHour",this, "0 0 3 1 * ?"));
//		} catch (Exception e) {
//			if(logger.isErrorEnabled()){
//				logger.error(e.getMessage(),e);
//			}
//		}
//	}
//
//	/**对未恢复的告警信息只保留1个月。然后存档。<br />
//	 * 对已恢复的告警信息保留6个月。然后存档。
//	 * @param context
//	 * @throws JobExecutionException
//	 */
//	@Override
//	public void execute(JobExecutionContext context) throws JobExecutionException {
//		
//		ResourceEventService eventDao=(ResourceEventService) SpringBeanUtil.getObject("resourceEventDAO");
//		
//		Calendar eventTime=Calendar.getInstance();
//		eventTime.set(Calendar.MONTH, eventTime.get(Calendar.MONTH)-1);
//		
//		Calendar recoverEventTime=Calendar.getInstance();
//		recoverEventTime.set(Calendar.MONTH, eventTime.get(Calendar.MONTH)-6);
//		
//		eventDao.moveToHistory(eventTime.getTime(), recoverEventTime.getTime());
//	}
//	
//
//}
