<%@page import="com.mainsteam.stm.scheduler.obj.TimelineScheduleTask"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.mainsteam.stm.scheduler.obj.SecondPeriodFire"%>
<%@page import="com.mainsteam.stm.scheduler.obj.MetricScheduleTask"%>
<%@page import="com.mainsteam.stm.scheduler.obj.ProfileScheduleTask"%>
<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@page import="com.mainsteam.stm.scheduler.SchedulerService"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>调度器监控</title>
<script type="text/javascript">
	function openNewLevel(level) {
		var locationValue = window.location.href;
		var flagIndex = locationValue.indexOf("?");
		if (flagIndex > 0) {
			locationValue = locationValue.substring(0, flagIndex)
					+ "?coreLevel=" + level;
		} else {
			locationValue = locationValue + "?coreLevel=" + level;
		}

		window.location.href = locationValue;
	}

	function reload() {
		window.location.href = window.location.href;
	}
	//setInterval(reload, 5000);
</script>
</head>
<body>
	<%
		SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
			SchedulerService schedulerService = (SchedulerService) SpringBeanUtil
			.getObject("schedulerService");
			List<Long> profileTaskIds = schedulerService
			.listProfileScheduleTasks();
			int allMetricCount = 0;
			int allMinuteMetricCount = 0;
			if (profileTaskIds != null && profileTaskIds.size() > 0) {
		for (Long profileId : profileTaskIds) {
			ProfileScheduleTask scheduleTask = schedulerService
					.getProfileScheduleTask(profileId.longValue());
			long[] resourceInstIds = scheduleTask.getResourceInstIds();
	%>
	<div>
		<table border="1" width="100%">
			<tr>
				<td colspan="5">
					<div style="text-align: center">
						<br /> 策略id:<%=scheduleTask.getProfileId()%>
					</div> <br /> 策略中包含的资源实例id：<%=resourceInstIds == null ? "" : Arrays
							.toString(resourceInstIds)%><br />
				</td>
			</tr>
			<tr bgcolor="grey">
				<td>指标id</td>
				<td>是否监控</td>
				<td>上一次执行时间</td>
				<td>下一次执行时间</td>
				<td>监控频度</td>
			</tr>
			<%
				List<MetricScheduleTask> metricScheduleTasks = scheduleTask
									.getTasks();
							if (metricScheduleTasks != null
									&& metricScheduleTasks.size() > 0) {
								allMetricCount+=(resourceInstIds == null ? 1:resourceInstIds.length)*metricScheduleTasks.size();
								for (MetricScheduleTask mt : metricScheduleTasks) {
									String metricId = mt.getMetricId();
									long nextFireTime = mt.getNextFireTime();
									long lastFireTime = mt.getLastFireTime();
									long second = 0;
									if(mt
											.getTimeFire() == null){
										second = -1;
									}else{
										second = ((SecondPeriodFire) mt
												.getTimeFire()).getSecond();
									}
									if(second<3600){
										allMinuteMetricCount+=resourceInstIds == null ? 1:resourceInstIds.length;
									}
									boolean isMonitored = mt.isActive();
			%>
			<tr>
				<td><%=metricId%></td>
				<td><%=isMonitored ? "是" : "否"%></td>
				<td><%=dateFormat.format(new Date(lastFireTime))%></td>
				<td><%=dateFormat.format(new Date(nextFireTime))%></td>
				<td>
					<%
						if (second >= 3600) {
														long time = second / 3600;
														out.println(time + "小时");
													} else if (second >= 60) {
														long time = second / 60;
														out.println(time + "分钟");
													} else {
														out.println("秒");
													}
					%>
				</td>
			</tr>
			<%
				}
							}
							
							List<TimelineScheduleTask> timelineScheduleTasks = scheduleTask
									.getTimelineScheduleTasks();
							if (timelineScheduleTasks != null
									&& timelineScheduleTasks.size() > 0) {
								for (TimelineScheduleTask timelineScheduleTask : timelineScheduleTasks) {
									List<MetricScheduleTask> tmScheduleTasks = timelineScheduleTask
											.getTasks();
			%>
			<tr>
				<td>基线id：</td>
				<td><%=timelineScheduleTask.getTimelineId()%></td>
				<td>基线时间限制</td>
				<td><%=dateFormat.format(new Date(timelineScheduleTask.getLimitedPeriodTimeFire().getStart()))%></td>
				<td>to <%=dateFormat.format(new Date(timelineScheduleTask.getLimitedPeriodTimeFire().getEnd()))%></td>
			</tr>
			<%
				for (MetricScheduleTask mt : tmScheduleTasks) {
										String metricId = mt.getMetricId();
										long nextFireTime = mt.getNextFireTime();
										long lastFireTime = mt.getLastFireTime();
										long second = 0;
										if(mt
												.getTimeFire() == null){
											second = -1;
										}else{
											second = ((SecondPeriodFire) mt
													.getTimeFire()).getSecond();
										}
										boolean isMonitored = mt.isActive();
			%>
			<tr>
				<td><%=metricId%></td>
				<td><%=isMonitored ? "是" : "否"%></td>
				<td><%=dateFormat.format(new Date(
										lastFireTime))%></td>
				<td><%=dateFormat.format(new Date(
										nextFireTime))%></td>
				<td>
					<%
						if (second >= 3600) {
															long time = second / 3600;
															out.println(time + "小时");
														} else if (second >= 60) {
															long time = second / 60;
															out.println(time + "分钟");
														} else {
															out.println("秒");
														}
					%>
				</td>
			</tr>
			<%
				}
								}
							}
						}
			%>
			<tr>
				<td colspan="5" height="100"></td>
			</tr>
		</table>
		<%
			} else {
		%>
		<div>没有策略被监控</div>
		<%
			}
		%>

	</div>
<div><span>监控的指标总个数：</span><span><%=allMetricCount%></span><span>分钟指标总个数：</span><span><%=allMinuteMetricCount%></span>
</div>
</body>
</html>