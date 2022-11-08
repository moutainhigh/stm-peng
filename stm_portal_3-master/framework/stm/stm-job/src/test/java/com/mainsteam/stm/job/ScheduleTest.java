package com.mainsteam.stm.job;

import org.quartz.SchedulerException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mainsteam.stm.util.SpringBeanUtil;

public class ScheduleTest {

	public ScheduleTest() {
	}

	public static void main(String[] args) throws ClassNotFoundException, SchedulerException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/services/public-*-beans.xml");
		context.start();
		args = new String[1];
		args[0] = "true";
		if(args.length>0 && args[0].equals("true")){
			System.out.println("开始调度任务");
			ScheduleManager scheduleManager = (ScheduleManager) SpringBeanUtil.getObject("scheduleManager");
			IJob job1 = new IJob(new SimpleJob(), "*/5 * * * * ?");
			System.out.println("jobKey="+job1.getJobDetail().getKey());
			scheduleManager.scheduleJob(job1);
		}else{
			System.out.println("不调度任务");
		}
		System.out.println(new String("*/5 * * * * ?").hashCode());
		System.out.println(new String("*/5 * * * * ?").hashCode());
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
