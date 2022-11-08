<%@page import="java.util.concurrent.atomic.AtomicLong"%>
<%@page import="com.mainsteam.stm.state.chain.StateChainStep"%>
<%@page import="com.mainsteam.stm.state.chain.StateChainFactory"%>
<%@page import="com.mainsteam.stm.state.chain.DefaultStateComputeChain"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%
	boolean autoFresh = false;
%>
<html>
<head>
<title>StateChainStepMonitor</title>
<script type="text/javascript">
	function startMonitor(dataType) {
		window.location.href = loadBaseURL() + "?action=start&dataType="
				+ dataType + "#" + dataType;
	}

	function stopMonitor(dataType) {
		window.location.href = loadBaseURL() + "?action=stop&dataType="
				+ dataType + "#" + dataType;
	}

	function loadBaseURL() {
		var local = window.location.href;
		if (local.indexOf("?") > 0) {
			return local.substring(0, local);
		} else {
			return local;
		}
	}

	function reload() {
		window.location.href = window.location.href;
	}
	
	function filterStep(type,index){
		actionF.location.href = loadBaseURL() + "?filterIndex="+index+"&type="+type;
		reload();
	}
	function unFilterStep(type,index){
		actionF.location.href = loadBaseURL() + "?unFilterIndex="+index+"&type="+type;
		reload();
	}
<%if (autoFresh) {%>
	setTimeout("reload()", 5000);
<%}%>
	
</script>
</head>
<body>
	<div id="performance"
		style="position: absolute; left: 0px; top: 0px; z-index: 5;width:50%;">
		<div width="100%" style="text-align:center;font-weight:bold;padding-top:30px;">性能指标处理过程</div>
		<%
			{
				DefaultStateComputeChain chain = (DefaultStateComputeChain) StateChainFactory
						.findStateComputeChain("performance");
				if ("performance".equals(request.getParameter("type"))) {
					if (request.getParameter("filterIndex") != null) {
						int index = Integer.parseInt(request
								.getParameter("filterIndex"));
						chain.filterStep(index);
					} else if (request.getParameter("unFilterIndex") != null) {
						int index = Integer.parseInt(request
								.getParameter("unFilterIndex"));
						chain.unFilterStep(index);
					}
				}
				StateChainStep[] stateChainSteps = chain.getStateChainSteps();
				AtomicLong[] stateChainStepsOffsetTimes = chain
						.getStateChainStepsOffsetTimes();
				AtomicLong[] stateChainStepsTimes = chain
						.getStateChainStepsTimes();
				boolean[] filterStep = chain.getFilterStep();
				boolean gray = false;
				for (int i = 0; i < stateChainStepsTimes.length; i++) {
					long count = stateChainStepsTimes[i].get();
					long offset = stateChainStepsOffsetTimes[i].get();
					float avaOffset = -0.0f;
					if (count > 0) {
						avaOffset = offset / count;
					}
					String color = gray ? "#CCCCCC" : "#99CCCC";
					gray = !gray;
		%>
		<ul style="background:<%=color%>">
			<li><label>处理步骤：</label><label><%=stateChainSteps[i].getClass().getSimpleName()%></label></li>
			<li><label>处理的指标个数：</label><label><%=count%></label></li>
			<li><label>总耗时：</label><label><%=offset%></label></li>
			<li><label>平均耗时：</label><label><%=avaOffset%></label></li>
			<%
				if (filterStep[i]) {
			%>
			<li><button onclick="unFilterStep('performance',<%=i%>)">取消过滤</button></li>
			<%
				} else {
			%>
			<li><button onclick="filterStep('performance',<%=i%>)">过滤</button></li>
			<%
				}
			%>
		</ul>
		<%
			}
			}
		%>
	</div>
	<div id="available"
		style="position: absolute; right: 0px; top: 0px; z-index: 10;width:50%;">
		<div width="100%" style="text-align:center;font-weight:bold;padding-top:30px;">可用性指标处理过程</div>
		<%
			{
				DefaultStateComputeChain chain = (DefaultStateComputeChain) StateChainFactory
						.findStateComputeChain("available");
				if ("available".equals(request.getParameter("type"))) {
					if (request.getParameter("filterIndex") != null) {
						int index = Integer.parseInt(request
								.getParameter("filterIndex"));
						chain.filterStep(index);
					} else if (request.getParameter("unFilterIndex") != null) {
						int index = Integer.parseInt(request
								.getParameter("unFilterIndex"));
						chain.unFilterStep(index);
					}
				}
				StateChainStep[] stateChainSteps = chain.getStateChainSteps();
				AtomicLong[] stateChainStepsOffsetTimes = chain
						.getStateChainStepsOffsetTimes();
				AtomicLong[] stateChainStepsTimes = chain
						.getStateChainStepsTimes();
				boolean[] filterStep = chain.getFilterStep();
				boolean gray = false;
				for (int i = 0; i < stateChainStepsTimes.length; i++) {
					long count = stateChainStepsTimes[i].get();
					long offset = stateChainStepsOffsetTimes[i].get();
					float avaOffset = -0.0f;
					if (count > 0) {
						avaOffset = offset / count;
					}
					String color = gray ? "#CCCCCC" : "#99CCCC";
					gray = !gray;
		%>
		<ul style="background:<%=color%>">
			<li><label>处理步骤：</label><label><%=stateChainSteps[i].getClass().getSimpleName()%></label></li>
			<li><label>处理的指标个数：</label><label><%=count%></label></li>
			<li><label>总耗时：</label><label><%=offset%></label></li>
			<li><label>平均耗时：</label><label><%=avaOffset%></label></li>
			<%
				if (filterStep[i]) {
			%>
			<li><button onclick="unFilterStep('available',<%=i%>)">取消过滤</button></li>
			<%
				} else {
			%>
			<li><button onclick="filterStep('available',<%=i%>)">过滤</button></li>
			<%
				}
			%>
		</ul>
		<%
			}
		}
		%>
	</div>
	<iframe height="0" width="0" id="actionF" name="actionF"></iframe>
</body>
</html>