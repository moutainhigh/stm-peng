<%@ page import="com.mainsteam.stm.platform.web.vo.ILoginUser"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HTML SSH Page</title>
</head>

<%

	String host = request.getParameter("host");
	String domain = "http://" + request.getServerName() + ":" + request.getServerPort(); 
	String localAddr = request.getLocalAddr();
	String sessId = request.getRequestedSessionId();
	ILoginUser user = (ILoginUser) session.getAttribute(ILoginUser.SESSION_LOGIN_USER);
	String usrName =user.getAccount();
	
%>


<body>
	<br>
	<center>
	
	
	<applet code="SwingShell.class" archive="applet_lib/ssh2.jar" name="sshViewerApplet" width="1024" height="800">
		<param name= "host" value="<%=host%>">
    	<param name = "domain" value="<%=domain%>">
    	<param name = "localAddr" value="<%=localAddr%>">
    	<param name = "sessId" value="<%=sessId%>">
    	<param id="usrName" name = "usrName" value="<%=usrName%>">
	</applet>

	</center>
</body>


</html>