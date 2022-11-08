<%@ page language="java" contentType="text/html;charset=utf-8" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>HTML Ping Page</title>    
	<script language="javascript" src="js/check.js"></script>
	<script src="../../../../third/jquery-1.7.2.js"></script>
<STYLE>
	BODY {
        FONT-SIZE: 11pt; 
        CURSOR: default; 
        COLOR: rgb(160,160,160); 
        FONT-FAMILY: Fixedsys; 
        BACKGROUND-COLOR: black
	}

	.input {
        BORDER-TOP-WIDTH: 0px; 
        BORDER-LEFT-WIDTH: 0px; 
        FONT-SIZE: 10pt; 
        BORDER-BOTTOM-COLOR: rgb(0,0,0); 
        CURSOR: text; 
        COLOR: rgb(160,160,160); 
   		font-weight:bold;
        BACKGROUND-COLOR: black; 
        BORDER-RIGHT-WIDTH: 0px;
        padding:12px 0 0 0;
	}
</STYLE>
</head>

<SCRIPT language=javascript>

function getfocus() {
	document.form.dos.focus();
}

document.onkeydown = getfocus;

function pageload() {
	top.resizeTo(550, 600);
	self.moveTo(1, 1);
	document.form.dos.focus();
}

function ping(cmd,nodeGroupId) {
	if (cmd=='') return;
	if (cmd=='exit' || cmd=='quit') {
		window.close();
		return;
	}
	
	$.ajax({
		   type: "POST",
		   url: "../../../../../portal/resource/tinyTools/ping.htm",
		   data: "dataType=json&cmd="+cmd+"&nodeGroupId="+nodeGroupId,
		   success: function(data){
			   
				var msgContent = msg.innerHTML;
				
				msgContent += "<br>";
				msgContent += ">" + cmd + "<br>";	
				msgContent += data.data + "<br>";	
				msg.innerHTML=msgContent;
		   }
		});
	
	document.form.dos.value = "";
	document.form.dos.focus();
}
</SCRIPT>

<%
String host = request.getParameter("host");
String nodeGroupId = request.getParameter("nodeGroupId");
String cmd = "";
if (host!=null && host.length()>0) {
	cmd = "ping " + host;
}
%>

<BODY onload="pageload();" onselected="return false;">
<br>
<font id=msg></font>
<FORM name=form action="javascript:ping(document.form.dos.value,<%=nodeGroupId%>)">
><input class=input type=text name=dos size=60 onblur="this.focus();">
<INPUT style="DISPLAY: none" type=submit value=submit hidden>
</FORM>
</BODY>
<SCRIPT>
ping('<%=cmd%>',<%=nodeGroupId%>);
</SCRIPT>