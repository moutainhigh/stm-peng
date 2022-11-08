package com.mainsteam.stm.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.listeners.SchedulerListenerSupport;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml" })
public class ScheduleManageTest {

	@Resource(name = "scheduleManager")
	private ScheduleManager scheduleManager;

	@Test
	public void test() throws Exception {
		
		JobA joba = new JobA();
//		JobB jobb = new JobB();

		IJob job1 = new IJob(joba, "*/3 * * * * ?");
//		IJob job2 = new IJob(jobb, "*/3 * * * * ?");

		scheduleManager.scheduleJob(job1);
	
		// scheduleManager.deleteJob(job1);

		Thread.sleep(10000);
		
		System.out.println("run2");
		scheduleManager.scheduleJob(job1);
//		scheduleManager.cleanJobs();
//		scheduleManager.shutdown();
		Thread.sleep(30000);
		scheduleManager.deleteJob(job1.getJobKey());

	}

	public static void main(String[] args) {
		 Set<String> cronExpression = new HashSet<String>();
		 cronExpression.add("xxx");
	}
}
