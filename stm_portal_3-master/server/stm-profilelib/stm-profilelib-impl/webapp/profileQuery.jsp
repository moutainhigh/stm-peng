<%@page import="java.util.Map"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="com.mainsteam.stm.profilelib.ProfileService"%>
<%@page import="com.mainsteam.stm.profilelib.obj.Profile"%>
<%@page import="com.mainsteam.stm.profilelib.obj.ProfileInfo"%>
<%@page import="com.mainsteam.stm.profilelib.obj.ProfileMetric"%>
<%@page import="com.mainsteam.stm.profilelib.obj.ProfileThreshold"%>


<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>策略查看</title>
</head>
<body>
<form action="profileQuery.jsp" method="post">
   <table>
    	<tr>
	   		<td colspan="2"></td>
	   </tr>
	   <tr>
	   		<td>profileId:<input type="text" name="profileId" /></td>
	   		<td><input type="submit" value="query"/></td>
	   </tr>
	</table>
	<table border="1">
		<tr>
			<th>profileId</th>
			<th>profileName</th>
			<th>profileType</th>
			<th>parentProfileId</th>
			<th>resourceId</th>
		</tr>
		<%
			String profileId = request.getParameter("profileId");
			ProfileService profileService = (ProfileService) SpringBeanUtil
					.getObject("profileService");
			
			if (profileId != null && !"".equals(profileId.trim())) {
				long id = 0;
				try{
					id = Long.parseLong(profileId);
				}catch(Exception e){
					out.print("no data...");
					return;
				}
				Profile profile = profileService
						.getProfilesById(id);
				if(profile == null){
					out.print("no data...");
					return;
				}
				ProfileInfo info = profile.getProfileInfo();
				List<ProfileMetric> profileMetrics = profile.getMetricSetting().getMetrics();
		%>
		<tr>
			<td><%=info.getProfileId()%></td>
			<td><%=info.getProfileName()%></td>
			<td><%=info.getProfileType()%></td>
			<td><%=info.getParentProfileId()%></td>
			<td><%=info.getResourceId()%></td>
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