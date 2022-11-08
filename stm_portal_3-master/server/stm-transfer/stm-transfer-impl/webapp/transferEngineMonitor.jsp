<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@page import="com.mainsteam.stm.transfer.DataDispatchMonitorResult"%>
<%@page import="com.mainsteam.stm.transfer.obj.TransferDataType"%>
<%@page import="com.mainsteam.stm.transfer.TransferDataDispatcher"%>
<%@page import="com.mainsteam.stm.transfer.TransferDataDispatcherImpl"%>
<%@page import="java.lang.management.ManagementFactory"%>
<%@page import="java.lang.management.ThreadMXBean"%>
<%@page import="java.lang.management.MonitorInfo"%>
<%@page import="java.lang.management.ThreadInfo"%>
<%@page import="java.util.ListResourceBundle"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.util.MissingResourceException"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%
TransferDataDispatcherImpl transferDataDispatcher = (TransferDataDispatcherImpl) SpringBeanUtil
	.getObject("dataDispatcher");
	String action = request.getParameter("action");
	boolean autoFresh = false;
	if (action != null) {
		String dataType = request.getParameter("dataType");
		if(dataType!=null){
			TransferDataType dataTypeObj = TransferDataType.valueOf(dataType);
				if (action.equals("start")) {
			transferDataDispatcher
			.startMonitor(dataTypeObj);
			autoFresh = true;
				} else if (action.equals("stop")) {
			transferDataDispatcher
			.stopMonitor(dataTypeObj);
				}
		}
	}
	TransferDataType[] datatypes = TransferDataType.values();
%>
<html>
<head>
<title>TransferEngineMonitorForMetricData</title>
<script type="text/javascript">
	function startMonitor(dataType) {
		window.location.href =  loadBaseURL() + "?action=start&dataType="+dataType+"#"+dataType;
	}

	function stopMonitor(dataType) {
		window.location.href =  loadBaseURL() + "?action=stop&dataType="+dataType+"#"+dataType;
	}
	
	function loadBaseURL(){
		var local = window.location.href;
		if(local.indexOf("?")>0){
			return local.substring(0,local);
		}else{
			return local;
		}
	}

	function reload() {
		window.location.href = window.location.href;
	}
<%if(autoFresh){%>
	setTimeout("reload()", 5000);
<%}%>
</script>
</head>
<body>
	<div>
	<ul>
	<%
			
			for (TransferDataType dataType : datatypes) {
	%>
	
		<li><span><%=dataType.name()%></span><input type="button" value="开始监控" onclick="startMonitor('<%=dataType.name()%>')"></input> <input
			type="button" value="取消监控" onclick="stopMonitor('<%=dataType.name()%>')"></input></li>
	<%
			}
	%>
	</ul>
	</div>
	<div>
		<%
			boolean gray = false;
			for (TransferDataType dataType : datatypes) {
				DataDispatchMonitorResult result = transferDataDispatcher
						.getDataDispatchMonitorResults(dataType);
				if (result != null) {
					String color = gray ? "#CCCCCC":"#99CCCC";
					gray = !gray;
		%>
		<ul style="background:<%=color%>" >
			<li><a name="<%=result.getDataType()%>"><label>数据类型：</label><label><%=result.getDataType()%></label></a></li>
			<li><label>待处理的个数：</label><label><%=result.getRemainingCount()%></label></li>
			<li><label>已经处理的总个数：</label><label><%=result.getAllFlowCount()%></label></li>
			<li><label>正在处理的个数：</label><label><%=result.getActiveCount()%></label></li>
			<li><label>吞吐量：</label><label><%=result.getThroughput()%></label></li>
		</ul>
		<%
				}
			}
		%>
	</div>
	<iframe height="0" width="0" id="actionF" name="actionF"></iframe>
</body>
</html>