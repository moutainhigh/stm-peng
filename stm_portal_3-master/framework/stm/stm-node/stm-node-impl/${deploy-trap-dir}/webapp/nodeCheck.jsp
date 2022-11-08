<%@page import="com.mainsteam.stm.node.LocaleNodeService"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.mainsteam.stm.node.Node"%>
<%@page import="com.mainsteam.stm.node.NodeGroup"%>
<%@page import="java.util.List"%>
<%@page import="com.mainsteam.stm.node.NodeTable"%>
<%@page import="com.mainsteam.stm.util.SpringBeanUtil"%>
<%@page import="com.mainsteam.stm.node.NodeService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>节点检查</title>
</head>
<body>
	<%
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		NodeService nodeService = (NodeService) SpringBeanUtil
				.getObject("nodeService");
		LocaleNodeService localNodeService = (LocaleNodeService) SpringBeanUtil
				.getObject("localNodeService");
		NodeTable table = null;
		Node currentNode = localNodeService.getCurrentNode();
	%>
	<div>当前节点信息</div>
	<table border="1">
		<tr bgcolor="grey">
			<td>节点id</td>
			<td>节点名称</td>
			<td>节点功能</td>
			<td>节点IP</td>
			<td>节点端口</td>
		</tr>
		<tr>
			<td><%=currentNode.getId()%></td>
			<td><%=currentNode.getName()%></td>
			<td><%=currentNode.getFunc()%></td>
			<td><%=currentNode.getIp()%></td>
			<td><%=currentNode.getPort()%></td>
		</tr>
	</table>
	<br><br><br>
	<div>节点表信息</div>
	<table border="1">
		<tr bgcolor="grey">
			<td>节点id</td>
			<td>节点名称</td>
			<td>节点功能</td>
			<td>节点IP</td>
			<td>节点端口</td>
			<td>节点路径</td>
			<td>节点启动时间</td>
			<td>节点状态</td>
			<td>节点所在分组id</td>
		</tr>
		<%
			if (nodeService == null) {
				table = localNodeService.getLocalNodeTable();
			} else {
				table = nodeService.getNodeTable();
			}
			List<NodeGroup> groups = table.getGroups();
			for (NodeGroup group : groups) {
				List<Node> nodes = group.getNodes();
				if (nodes == null) {
					continue;
				}
				for (Node node : nodes) {
					String color = null;
					if (node.isAlive()) {
						color = "green";
					} else {
						color = "red";
					}
		%>
		<tr bgcolor="<%=color%>">
			<td><%=node.getId()%></td>
			<td><%=node.getName()%></td>
			<td><%=node.getFunc()%></td>
			<td><%=node.getIp()%></td>
			<td><%=node.getPort()%></td>
			<td><%=node.getInstallPath()%></td>
			<td><%=dateFormat.format(node.getStartupTime())%></td>
			<td><%=node.isAlive() ? "可用" : "不可用"%></td>
			<td><%=node.getGroupId()%></td>
		</tr>
		<%
			}
			}
		%>
	</table>
</body>
</html>