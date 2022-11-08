package com.mainsteam.stm.job;

import org.quartz.SchedulerException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mainsteam.stm.util.SpringBeanUtil;

public class CleanAllJobsTest {

	public static void main(String[] args) throws ClassNotFoundException,
			SchedulerException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath*:META-INF/services/public-*-beans.xml");
		context.start();
		ScheduleManager scheduleManager = (ScheduleManager) SpringBeanUtil
				.getObject("scheduleManager");
		scheduleManager.cleanJobs();
		context.close();
		System.out.println("the end.");
	}
}
