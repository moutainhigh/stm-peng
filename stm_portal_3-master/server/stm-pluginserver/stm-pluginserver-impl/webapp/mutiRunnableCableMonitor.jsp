<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.mainsteam.stm.pluginserver.cable.RunnerCoreCable"%>
<%@page
	import="com.mainsteam.stm.pluginserver.cable.MultiRunnerCoreCable"%>
<%@page
	import="com.mainsteam.stm.pluginserver.cable.RunnerCoreCableRangeLevel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page
	import="com.mainsteam.stm.pluginserver.cable.MultiRunnerCoreCableBalencer"%>
<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>多芯数据处理线路监控</title>
<style>
body {
	margin: 0 atuto;
}
</style>
<script type="text/javascript">
	function openNewLevel(level) {
		var filterCondition = document.getElementById("filterConditionValue").value
		var locationValue = window.location.href;
		var flagIndex = locationValue.indexOf("?");
		if (flagIndex > 0) {
			locationValue = locationValue.substring(0, flagIndex)
					+ "?coreLevel=" + level;
		} else {
			locationValue = locationValue + "?coreLevel=" + level;
		}
		locationValue += "&filter="+filterCondition
		window.location.href = locationValue;
	}

	function reloadWithCondition(level) {
		openNewLevel(level);
	}
	
	function clearFilterCondition(level){
		document.getElementById("filterCondition").value = "";
		document.getElementById("filterConditionValue").value = "";
		openNewLevel(level);
		/*
		var locationValue = window.location.href;
		var filterIndex = locationValue.indexOf("filter=");
		if (flagIndex > 0) {
			var filterNextIndex =  locationValue.indexOf("&",filterIndex);
			if(filterNextIndex>0){
				var flagIndex = locationValue.indexOf("?");
				if(filterIndex-flagIndex == 1){
					filterNextIndex++;
				}
				locationValue = locationValue.substring(0, filterIndex)+locationValue.substring(filterNextIndex, locationValue.length());
			}else{
				locationValue = locationValue.substring(0, filterIndex-1);
			}
			window.location.href = locationValue;
		}*/
	}

	function reload() {
		window.location.href = window.location.href;
	}
	//setInterval(reload, 5000);
</script>
</head>
<body>
<%
String filer = request.getParameter("filter");
filer = filer == null ? "":filer;
RunnerCoreCableRangeLevel requestLevel = null;
String coreLevel = request.getParameter("coreLevel");
if (coreLevel != null) {
	requestLevel = RunnerCoreCableRangeLevel.valueOf(coreLevel);
}
%>
	<center>
		<table border="1" cellpadding="5">
			<thead>
				<tr>
					<th>多芯数据处理线路级别</th>
					<th>数据处理线路个数</th>
					<th>最大活动线程个数</th>
					<th>最小空闲线程个数</th>
					<th>当前活动线程个数</th>
					<th>当前空闲线程个数</th>
					<th>处理的数据个数</th>
				</tr>
			</thead>
			<tbody>
				<%
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss.SSS");
					
					MultiRunnerCoreCable requestCoreCable = null;

					MultiRunnerCoreCableBalencer balencer = (MultiRunnerCoreCableBalencer) SpringBeanUtil
							.getObject("multiRunnerCoreCableBalencer");
					RunnerCoreCableRangeLevel[] rangeLevels = RunnerCoreCableRangeLevel
							.values();
					for (int i = 0; i < rangeLevels.length; i++) {
						MultiRunnerCoreCable multiRunnerCoreCable = balencer
								.getMultiRunnerCoreCable(rangeLevels[i]);
						String bgColore = "";
						if (requestLevel != null && requestLevel == rangeLevels[i]) {
							requestCoreCable = multiRunnerCoreCable;
							bgColore = "grey";
						}
						int coreCableSize = multiRunnerCoreCable
								.getRunnerCoreCableSize();
						int maxActive = multiRunnerCoreCable.getMaxActivity();
						int active = multiRunnerCoreCable.getActiveCount();
						int pooSize = multiRunnerCoreCable.getPoolSize();
						long allTaskSize = multiRunnerCoreCable.getAllTaskSize();
						int minIdle = multiRunnerCoreCable.getMinIdel();
						minIdle = minIdle > 0 ? minIdle : 0;
				%>
				<tr bgcolor="<%=bgColore%>">
					<td style="cursor: pointer;"
						onclick="openNewLevel('<%=rangeLevels[i].name()%>')"><%=rangeLevels[i].name()%></td>
					<td><%=coreCableSize%></td>
					<td><%=maxActive%></td>
					<td><%=minIdle%></td>
					<td><%=active%></td>
					<td><%=pooSize - active%></td>
					<td><%=allTaskSize%></td>
				</tr>
				<%
					}
				%>
			</tbody>
		</table>
		<br> <br> <br>
		<div align="left">
		<span>名称过滤：</span>
			<input type="text" name="filterCondition" id="filterCondition" value="<%=filer%>">
			<input type="hidden" name="filterConditionValue" id="filterConditionValue" value="<%=filer%>">
			<button
				onclick='document.getElementById("filterConditionValue").value=document.getElementById("filterCondition").value;reloadWithCondition("<%=coreLevel == null ? "":coreLevel%>")'>过滤</button>
			<button onclick='clearFilterCondition("<%=coreLevel == null ? "":coreLevel%>")'>清除过滤</button>
		</div>		
		<%
			if (requestCoreCable != null) {
		%>
		<table border="1" cellpadding="5">
			<thead>
				<tr>
					<th>数据处理线路名称</th>
					<th>数据处理线路最大活动数据个数</th>
					<th>正在处理数据个数</th>
					<th>待处理数据个数</th>
					<th>数据处理平均时间(毫秒)</th>
					<th>已经处理的总数据数</th>
					<th>处理数据的累加时间(毫秒)</th>
					<th>吞吐量处理的个数</th>
					<th>吞吐量处理的开始时间偏移量(毫秒)</th>
					<th>吞吐量率(个/秒)</th>
					<th>最近一次的执行完成时间</th>
					<th>-------</th>
				</tr>
			</thead>
			<%
				int coreCableSize = requestCoreCable.getRunnerCoreCableSize();
					for (int j = 0; j < coreCableSize; j++) {
						RunnerCoreCable coreCable = requestCoreCable
								.getRunnerCoreCable(j);
						String name = coreCable.getKey();
						if (filer != null && !filer.equals("") && name.indexOf(filer) == -1) {
							continue;
						}
						String title = name;
						if(name.length()>30){
							name = name.substring(0,30);
						}
						int maxActivity = coreCable.getMaxActivity();
						int activity = coreCable.getActivity();
						int remainingSize = coreCable.getRemainingSize();
						long avgComputeOffsetTime = coreCable
								.getAvgComputeOffsetTime();
						long computeCounter = coreCable.getComputeCounter();
						long sumComputeOffsetTime = coreCable
								.getSumComputeOffsetTime();
						long lastComputeTime = coreCable.getLastComputeTime();

						long throughputComputeCount = coreCable
								.getThroughputComputeCount();
						long throughputTime = coreCable.getThroughputTime();
						float throughput = coreCable.getThroughput();
						String closed = coreCable.isClosed() ? "关闭":"运行";
			%>
			<tr <%=coreCable.isClosed() ? "bgcolor=green":""%>>
				<td><span title="<%=title%>"><%=name%></span></td>
				<td><%=maxActivity%></td>
				<td><%=activity%></td>
				<td><%=remainingSize%></td>
				<td><%=avgComputeOffsetTime%></td>
				<td><%=computeCounter%></td>
				<td><%=sumComputeOffsetTime%></td>

				<td><%=throughputComputeCount%></td>
				<td><%=throughputTime%></td>
				<td><%=throughput%></td>

				<td><%=dateFormat.format(new Date(lastComputeTime))%></td>
				<td><%=closed%></td>
			</tr>
			<%
				}
			%>
		</table>
		<%
			}
		%>
	</center>
</body>
</html>