<%@page import="com.mainsteam.stm.dataprocess.bigData.job.ResourceInstanceSyncJob"%>
<%@page import="java.util.Date"%>

<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>资源实例查看</title>
</head>
<body>
<form action="testSync.jsp?param=1">
		<%
		
			ResourceInstanceSyncJob job = (ResourceInstanceSyncJob) SpringBeanUtil.getObject("resourceInstanceSyncJob");
			job.execute(null);
			
		%>
</form>
</body>
</html>