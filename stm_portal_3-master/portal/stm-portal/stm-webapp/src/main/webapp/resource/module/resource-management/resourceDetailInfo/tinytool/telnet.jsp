<%
String host=request.getParameter("host");
String port=request.getParameter("port");
%>
<center>
<applet CODEBASE="."
        ARCHIVE="applet_lib/client.jar"
        CODE="de.mud.jta.Applet" 
        WIDTH="100%" HEIGHT="100%">
    <%if(host != null && !"".equals(host)){ %>
	<param name = "Socket.host" value ="<%=host%>"/>
	<%} %>
	<%if(port != null && !"".equals(port)){ %>
	<param name = "Socket.port" value ="<%=port%>"/>
	<%}else{ %>
	<param name = "Socket.port" value ="23"/>	
	<%} %>
	<param name = "Applet.disconnect" value ="true"/>
	</applet>
</center>
