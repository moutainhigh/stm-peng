package com.mainsteam.stm.ct.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.mainsteam.stm.ct.api.IAverageWebService;
import com.mainsteam.stm.ct.api.IWebsiteMetricService;
import com.mainsteam.stm.ct.bo.MsAverageWeb;
import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;

@Component
public class AverageWebTask {
	Logger log=Logger.getLogger(AverageWebTask.class);
    @Autowired
    private IWebsiteMetricService websiteMetricService;

    @Autowired
    private IAverageWebService averageWebService;
    private final Log logger = LogFactory.getLog(AverageWebTask.class);
    @Autowired
	private ScheduleManager scheduleManager;
    private static final String AVERAGE_KEY = "AverageWeb_JOB";
    @PostConstruct
    public void init(){
    	try {
    		AverageWebJob alarmJob = new AverageWebJob();
			String cronExpression = "0 0/5 * * * ?";
			
			IJob ipMacJob = new IJob(AVERAGE_KEY, alarmJob, cronExpression);
			this.scheduleManager.updateJob(AVERAGE_KEY, ipMacJob);
			logger.error("探针状态Job启动成功!\n[" + AVERAGE_KEY + "],jobTime["+ cronExpression);
			
		} catch (Exception e) {
			logger.error("类：ProbeTask ,方法：startProbeJobTask：开启探针状态Job任务发生异常!", e);
		}
    }
    
    public void averageWebVal(){
        log.info("web平均值计算");
        try {
            Calendar ca = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            ca.setTime(new Date());
            ca.add(Calendar.MINUTE, -30);
            Date date1 = ca.getTime();
            String startTime = sdf.format(date1);
            String endTime = sdf.format(date);
            WebsiteMetricVo vo=new WebsiteMetricVo();
            vo.setStartTime(date1);
            vo.setEndTime(date);
            MsWebsiteMetric mwm = websiteMetricService.getAvg(vo);

            if( mwm != null){
            	if(null==mwm.getFirst_char()&&null==mwm.getDom_ready()&&null==mwm.getPage_ready()){
            		return;
            	}
                MsAverageWeb maw = new MsAverageWeb();
                maw.setFirst_char(mwm.getFirst_char());
                maw.setDom_ready(mwm.getDom_ready());
                maw.setPage_ready(mwm.getPage_ready());
                maw.setDns_select(mwm.getDns_select());
                maw.setTcp_collect(mwm.getTcp_collect());
                maw.setReq_answer(mwm.getReq_answer());
                maw.setSend_content(mwm.getSend_content());
                maw.setDom_parse(mwm.getDom_parse());
                maw.setResource_load(mwm.getResource_load());
                maw.setCreate_time(endTime);

                averageWebService.save(maw);
            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
