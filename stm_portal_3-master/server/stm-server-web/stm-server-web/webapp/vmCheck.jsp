<%@ page language="java" contentType="text/html; charset=GBK"

    pageEncoding="GBK"%>
<%
long free=Runtime.getRuntime().freeMemory();
long max= Runtime.getRuntime().maxMemory();
long used = max - free;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=GBK">

<title>Insert title here</title>

</head>
<body>
Max mem:<%=(max/1024/1024)%>MB<br>
Free mem:<%=(free/1024/1024)%>MB<br>
Used mem:<%=(used/1024/1024)%>MB<br>
</body>
</html>