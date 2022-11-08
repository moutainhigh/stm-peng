<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Locale"%>
<%
String path1 = request.getContextPath();
String path = path1+"/setting";
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
%>
<html>
<title>MIB</title>
<body marginheight=0 marginwidth=0 leftmargin=0 topmargin=0 scroll=no onload="self.focus()" onmouseout="try{opener.document.body.setCapture();}catch(e){}" onbeforeunload="try{opener.document.body.releaseCapture();}catch(e){}"> 
<%
	String address = request.getParameter("address")==null?"":request.getParameter("address"); 
	//http://java.sun.com/update/1.4.2/jinstall-1_4-windows-i586.cab#Version=1,4,0,0
%>

<!--"CONVERTED_APPLET"-->
<!-- HTML CONVERTER -->
<!--     codebase = "/netfocus/applet/j2re-1_4_2_19-windows-i586-p.exe#Version=1,4,0,0" -->

<OBJECT 
    classid = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"
    codebase = "."
    WIDTH = 800 HEIGHT = 600 NAME = "Mocha Focus On Network" ALIGN = "left" VSPACE = 0 HSPACE = 0 >
    <PARAM NAME = CODE VALUE = "com.mdcl.mocha.mibbrowser.MibbleBrowser.class" >
    <PARAM NAME = ARCHIVE VALUE = "applet_lib/mibbrowser-2.0.0.jar,applet_lib/grammatica-bin-1.4.jar,applet_lib/mibble-mibs-2.6.jar,applet_lib/mibble-parser-2.6.jar,applet_lib/snmp4_13.jar,applet_lib/liquidlnf-0.2.9.jar" >
    <PARAM NAME = NAME VALUE = "Mocha Focus On Network" >
    <PARAM NAME = MAYSCRIPT VALUE = true >
    <PARAM NAME = "type" VALUE = "application/x-java-applet;version=1.4">
    <PARAM NAME = "scriptable" VALUE = "false">
    <PARAM NAME = "cache_option" VALUE="No">
    <PARAM NAME = "cache_archive" VALUE="mibbrowser-2.0.0.jar">
    <PARAM NAME = "address" VALUE="<%=address%>">

    <COMMENT>
	<EMBED 
            type = "application/x-java-applet;version=1.4" \
            CODE = "com.mdcl.mocha.mibbrowser.MibbleBrowser.class" \
            ARCHIVE = "applet_lib/mibbrowser-2.0.0.jar,applet_lib/grammatica-bin-1.4.jar,applet_lib/mibble-mibs-2.6.jar,applet_lib/mibble-parser-2.6.jar,applet_lib/snmp4_13.jar,applet_lib/liquidlnf-0.2.9.jar" \
            NAME = "Mocha Focus On Network" \
            WIDTH = 800 \
            HEIGHT = 600 \
            ALIGN = "left" \
            VSPACE = 0 \
            HSPACE = 0 \
            MAYSCRIPT = true \
            cache_option ="No" \
            cache_archive ="mibbrowser-2.0.0.jar" \
            address ="<%=address%>" \
	    scriptable = false \
	    pluginspage = "http://java.sun.com/products/plugin/index.html#download">
	    <NOEMBED>
            
            </NOEMBED>
	</EMBED>
    </COMMENT>
</OBJECT>
</body> 

</html>
