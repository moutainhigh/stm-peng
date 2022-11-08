package com.mainsteam.stm.portal.inspect.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.portal.inspect.api.IInspectPlanApi;
import com.mainsteam.stm.util.SpringBeanUtil;

@DisallowConcurrentExecution
public class PlanJob implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Long id = (Long) context.getJobDetail().getJobDataMap().get("id");
		IInspectPlanApi inspectPlanApi = (IInspectPlanApi) SpringBeanUtil
				.getObject("InspectPlanApi");
		if (id != null) {
			inspectPlanApi.saveExec(id);
		}
	}

}
