<%@page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="org.apache.log4j.PropertyConfigurator"%>
<%@page import="java.net.URL"%>
<html>
<head>
<%
	String parameter = request.getParameter("reloadFlag");
	String log4jURLValue = "";
	if ("true".equals(parameter)) {
		URL log4jURL = this.getClass().getResource(
				"/log4j.properties");
		String msg = "";
		if (log4jURL != null) {
			log4jURLValue = log4jURL.toString();
			PropertyConfigurator.configure(log4jURL);
			msg = "重载"+log4jURLValue+"成功。";
		}else{
			msg = "log4j.properties没有找到";
		}
%>
	<script type="text/javascript">
		parent.document.getElementById("msg").innerHTML='<%=msg%>';
	</script>
<%
	} else {
%>
<title>Log4j Reload</title>
</head>
<body>
	<div>
		<input type="button" name="reload" onclick="reloadFrame.location.href=window.location.href+'?reloadFlag=true'" value="重载Log4j配置文件">
	</div>
	<div id="msg"></div>
	<iframe width="0" height="0" id="reloadFrame" name="reloadFrame"></iframe>
</body>
<%
	}
%>
</html>