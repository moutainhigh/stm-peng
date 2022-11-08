<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page
	import="com.mainsteam.stm.dataprocess.util.InstanceProcessLogController"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>处理器接收采集数据打印</title>
<style>
body {
	margin: 0 atuto;
}
</style>
<script type="text/javascript">
	function addInstanceIdLog() {
		var instId = document.getElementById("instanceId").value;
		var param = "?action=add&instId="+instId;
		actionF.location.href = window.location.href+param;
	}

	function closeInstanceIdLog(instId){
		var param = "?action=close&instId="+instId;
		actionF.location.href = window.location.href+param;
	}

	function closeAllInstanceIdLog() {
		var param = "?action=closeAll";
		actionF.location.href = window.location.href+param;
	}

	function reload() {
		window.location.href = window.location.href;
	}
</script>
</head>
<body>
<%
String action = request.getParameter("action");
if(action!=null){
	if(action.equals("add")){
		String instId = request.getParameter("instId");	
		long instanceId = Long.parseLong(instId);
		InstanceProcessLogController.getInstance().openLog(instanceId);
	}else if(action.equals("close")){
		String instId = request.getParameter("instId");	
		long instanceId = Long.parseLong(instId);
		InstanceProcessLogController.getInstance().closeLog(instanceId);
	}else if(action.equals("closeAll")){
		InstanceProcessLogController.getInstance().closeAllLog();
	}else{
		out.print("invalid action.action="+action);
	}
%>
<script type="text/javascript">
parent.reload();
</script>
<%	
	return;
}else{
%>
	<label >日志级别info. 添加后，在DHS\dataprocess.log通过资源实例id查询</label>
	<br>
	<br>
	<input type="text" title="输入资源实例id" name="instanceId" id="instanceId" value="">
	<input type="button"  value="添加资源实例日志" onclick="addInstanceIdLog()">&nbsp;
	<input type="button" value="取消所有资源实例日志" onclick="closeAllInstanceIdLog()">
	
	<br>
	<ul>
		<%
			List<Long> instanceLogIds = InstanceProcessLogController
					.getInstance().getInstanceIds();
			for (Long instid : instanceLogIds) {
		%>
		<li><label><%=instid%></label><input type="button" value="取消日志"
			onclick="closeInstanceIdLog('<%=instid%>')"></input></li>
		<%
			}
		%>
	</ul>
	<iframe id="actionF" name="actionF"></iframe>
<%
	}
%>
</body>
</html>