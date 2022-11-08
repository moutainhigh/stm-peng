package com.mainsteam.stm.ct.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.api.ICtAlarmService;
import com.mainsteam.stm.ct.api.IProbeService;
import com.mainsteam.stm.ct.api.IProbeTaskApi;
import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.ct.bo.MsProbe;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.ct.util.HttpUtil;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;


@Component
public class ProbeTaskImpl implements IProbeTaskApi{
	Logger log=Logger.getLogger(ProbeTaskImpl.class);
	@Resource
	private IProbeService probeService;
	
	@Resource
    private ICtAlarmService ctAlarmService;

	private final Log logger = LogFactory.getLog(ProbeTaskImpl.class);
	
	private static final String PROBE_KEY = "probe_status";
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private ScheduleManager scheduleManager;
	@PostConstruct
	public void startProbeJobTask() {
		try {
			ProbeJob alarmJob = new ProbeJob();
			String cronExpression = "0 0/5 * * * ?";
			
			IJob ipMacJob = new IJob(PROBE_KEY, alarmJob, cronExpression);
			this.scheduleManager.updateJob(PROBE_KEY, ipMacJob);
			logger.error("探针状态Job启动成功!\n[" + PROBE_KEY + "],jobTime["+ cronExpression);
			logger.info("探针状态Job启动成功!\n[" + PROBE_KEY + "],jobTime["+ cronExpression );
		} catch (Exception e) {
			logger.error("类：ProbeTask ,方法：startProbeJobTask：开启探针状态Job任务发生异常!", e);
		}
	}
	
	@Override
	public void checkStatus(){
		logger.error("探针checkStatus");
		Page<MsProbe, MsProbe> page=new Page<>();
		MsProbe msProbe=new MsProbe();
		page.setCondition(msProbe);
		probeService.getProbeList(page);
		List<MsProbe> list = page.getDatas();
		for(MsProbe probe : list){
			 String url=probe.getProbe_ip()+":"+probe.getProbe_port();
             String method="/third/check";
             boolean flag=true;
             JSONObject httppost=null;
             try {
            	 logger.error("url:"+url);
            	 httppost = HttpUtil.httppost(url, method, "");
            	 if(httppost==null&&probe.getProbe_status()==1){
            		 flag=false;
            	 }
             } catch (Exception e) {
				// TODO: handle exception
				flag=false;
				logger.error("探针不在线",e);
             }
             try {
            	 Page<MsCtAlarm, MsCtAlarm> alarmPage=new Page<>();
            	 if(!flag){
                	 probe.setProbe_status(0);
                	 int i = probeService.editProbe(probe);
                	 MsCtAlarm msCtAlarm=new MsCtAlarm();
                	 msCtAlarm.setAlarm_level(2);
                	 msCtAlarm.setAlarm_time(sdf.format(new Date()));
                	 msCtAlarm.setConfirmed(0);
                	 msCtAlarm.setCreate_time(new Date());
                	 msCtAlarm.setMessage("指针["+probe.getProbe_site()+"]不在线");
                	 msCtAlarm.setResource_id("probeAlarm");
                	 //指针id存入resultid以作为标识
                	 msCtAlarm.setResult_id(probe.getId().toString());
                	 alarmPage.setCondition(msCtAlarm);
					ctAlarmService.getAlarmPage(alarmPage);
					if(alarmPage.getDatas().size()>0){
						return;
					}
                	 ctAlarmService.insertAlarm(msCtAlarm);
                 }else if(httppost.get("code")==null&&probe.getProbe_status()==1){
                	 probe.setProbe_status(0);
                	 int i = probeService.editProbe(probe);
                	 MsCtAlarm msCtAlarm=new MsCtAlarm();
                	 msCtAlarm.setAlarm_level(2);
                	 msCtAlarm.setAlarm_time(sdf.format(new Date()));
                	 msCtAlarm.setConfirmed(0);
                	 msCtAlarm.setCreate_time(new Date());
                	 msCtAlarm.setMessage("指针【"+probe.getProbe_site()+"】不在线");
                	 msCtAlarm.setResource_id("probeAlarm");
                	 //指针id存入resultid以作为标识
                	 msCtAlarm.setResult_id(probe.getId().toString());
                	 alarmPage.setCondition(msCtAlarm);
 					ctAlarmService.getAlarmPage(alarmPage);
 					if(alarmPage.getDatas().size()>0){
 						return;
 					}
                	 ctAlarmService.insertAlarm(msCtAlarm);
                 }else if (httppost.get("code")!=null&&probe.getProbe_status()==0) {
                	 probe.setProbe_status(1);
                	 int i = probeService.editProbe(msProbe);
    			}
			} catch (Exception e) {
				// TODO: handle exception
				log.error("生成告警出错",e);
				log.error("httpost:"+httppost);
			}
             
		}
		
	}
}
