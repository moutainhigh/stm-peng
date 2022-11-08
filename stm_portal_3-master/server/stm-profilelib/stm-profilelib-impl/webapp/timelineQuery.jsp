<%@page import="java.util.Map"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="com.mainsteam.stm.TimelineService"%>
<%@page import="com.mainsteam.stm.profilelib.obj.TimelineInfo"%>
<%@page import="com.mainsteam.stm.profilelib.obj.Timeline"%>
<%@page import="com.mainsteam.stm.profilelib.obj.ProfileMetric"%>
<%@page import="com.mainsteam.stm.profilelib.obj.ProfileThreshold"%>


<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>基线查看</title>
</head>
<body>
<form action="timelineQuery.jsp" method="post">
   <table>
    	<tr>
	   		<td colspan="2"></td>
	   </tr>
	   <tr>
	   		<td>timelineId:<input type="text" name="timelineId" /></td>
	   		<td><input type="submit" value="query"/></td>
	   </tr>
	</table>
	<table border="1">
		<tr>
			<th>timelineId</th>
			<td>profileId</td>
			<th>timelineName</th>
			<th>timelineType</th>
			<th>startTime</th>
			<th>endTime</th>
		</tr>
		<%
			String timelineId = request.getParameter("timelineId");
		    TimelineService timelineService = (TimelineService) SpringBeanUtil
					.getObject("timelineService");
			
			if (timelineId != null && !"".equals(timelineId.trim())) {
				long id = 0;
				try{
					id = Long.parseLong(timelineId);
				}catch(Exception e){
					out.print("no data...");
					return;
				}
				Timeline timeline = timelineService
						.getTimelinesById(id);
				if(timeline == null){
					out.print("no data...");
				}
				TimelineInfo info = timeline.getTimelineInfo();
				List<ProfileMetric> profileMetrics = timeline.getMetricSetting().getMetrics();
		%>
		<tr>
			<td><%=info.getTimeLineId()%></td>
			<td><%=info.getProfileId()%></td>
			<td><%=info.getName()%></td>
			<td><%=info.getTimeLineType()%></td>
			<td><%=info.getStartTime()%></td>
			<td><%=info.getEndTime()%></td>
		</tr>
		<tr>
			<td colspan="7">指标列表</td>
		</tr>
		<tr>
			<td>metric_id</td>
			<td>dict_frequency_id</td>
			<td>is_use</td>
			<td>is_alarm</td>
			<td>alarm_repeat</td>
			<td>threshold</td>
		</tr>
		<%
			for(ProfileMetric metric : profileMetrics){
				%>
			<tr>
				<td><%=metric.getMetricId() %></td>
				<td><%=metric.getDictFrequencyId() %></td>
				<td><%=metric.isMonitor() %></td>
				<td><%=metric.isAlarm() %></td>
				<td><%=metric.getAlarmFlapping() %></td>
				<td>
					<table border="1">
						<tr>
							<td>dict_metric_state</td>
							<td>operator</td>
							<td>desc</td>
							<td>threshold_value</td>
						</tr>
						<%
							List<ProfileThreshold> profileThresholds = metric.getMetricThresholds();
						if(profileThresholds != null){
							
						
							for(ProfileThreshold profileThreshold : profileThresholds){
						%>
						<tr>
							<td><%=profileThreshold.getPerfMetricStateEnum() %></td>
							<td><%=profileThreshold.getExpressionOperator() %></td>
							<td><%=profileThreshold.getExpressionDesc() %></td>
							<td><%=profileThreshold.getThresholdValue() %></td>
						</tr>
						<%
							}
						}
						%>
					</table>
				</td>
			</tr>	
			<%
			}
		%>
	</table>
	<%
			}
		%>
</form>
	
</body>
</html>