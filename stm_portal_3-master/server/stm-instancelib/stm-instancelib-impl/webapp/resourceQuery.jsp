<%@page import="com.mainsteam.stm.instancelib.DiscoverPropService"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.mainsteam.stm.caplib.resource.ResourcePropertyDef"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="com.mainsteam.stm.caplib.plugin.PluginInitParameter"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.mainsteam.stm.caplib.common.CategoryDef"%>
<%@page import="com.mainsteam.stm.caplib.resource.ResourceDef"%>
<%@page import="com.mainsteam.stm.caplib.CapacityService"%>
<%@page import="com.mainsteam.stm.instancelib.obj.ModuleProp"%>
<%@page import="com.mainsteam.stm.instancelib.obj.DiscoverProp"%>
<%@page import="com.mainsteam.stm.instancelib.obj.ResourceInstance"%>
<%@page import="java.util.List"%>
<%@page import="com.mainsteam.stm.instancelib.ResourceInstanceService"%>
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
<form action="resourceQuery.jsp" method="post">
   <table>
    	<tr>
	   		<td colspan="2"></td>
	   </tr>
	   <tr>
	   		<td>instanceId:<input type="text" name="instanceId" /></td>
	   		<td><input type="submit" value="query"/></td>
	   </tr>
	</table>
	<table border="1">
		<tr>
			<th>instanceId</th>
			<th>parentId</th>
			<th>instanceName</th>
			<th>showName</th>
			<th>resourceId</th>
			<th>categoryId</th>
			<th>parentCategoryId</th>
			<th>showIP</th>
			<th>discoverWay</th>
			<th>lifeState</th>
			<th>可用性</th>
		</tr>
		<%
			String instanceId = request.getParameter("instanceId");
			ResourceInstanceService instanceService = (ResourceInstanceService) SpringBeanUtil
					.getObject("resourceInstanceService");
			
			if (instanceId != null && !"".equals(instanceId.trim())) {
				ResourceInstance instance = instanceService
						.getResourceInstance(Long.valueOf(instanceId));
				if(instance != null){
					String[] result = instance.getModulePropBykey("availability");
					if(result == null){
						result = new String[]{""};
					}
				
		%>
		<tr>
			<td><%=instance.getId()%></td>
			<td><%=instance.getParentId()%></td>
			<td><%=instance.getName()%></td>
			<td><%=instance.getShowName()%></td>
			<td><%=instance.getResourceId()%></td>
			<td><%=instance.getCategoryId()%></td>
			<td><%=instance.getParentCategoryId()%></td>
			<td><%=instance.getShowIP()%></td>
			<td><%=instance.getDiscoverWay()%></td>
			<td><%=instance.getLifeState()%></td>
			<td><%=result[0]%></td>
		</tr>
		
		<tr>
		<td colspan="11">
			<table border="1">
				<tr><td colspan="6">子实例列表</td></tr>
				<tr>
				    <th>instanceId</th>
					<th>parentId</th>
					<th>instanceName</th>
					<th>showName</th>
					<th>resourceId</th>
					<th>lifeState</th>
				</tr>
				<%
				List<ResourceInstance> children = instance.getChildren();
				if(children != null){
				
				for(ResourceInstance child : children){
					
			%>
			<tr>
			<td><%=child.getId()%></td>
			<td><%=child.getParentId()%></td>
			<td><%=child.getName()%></td>
			<td><%=child.getShowName()%></td>
			<td><%=child.getResourceId()%></td>
			<td><%=child.getLifeState()%></td>
			</tr>
			<%
			}
				}
		%>
			</table>
			</td>
		</tr>	
		<tr>
			<td  colspan="11">
			<table>
				<tr>
					<td>属性key</td>
					<td>属性值</td>
				</tr>
				<%
					DiscoverPropService discoverPropService = SpringBeanUtil.getBean(DiscoverPropService.class);
					List<DiscoverProp> ds = discoverPropService.getPropByInstanceId(instance.getId());
					if(ds!=null && ds.size()>0){
						for(DiscoverProp p:ds){
				%>
				<tr>
					<td><%=p.getKey() %></td>
					<td><%=StringUtils.join(p.getValues(),",") %></td>
				</tr>
				<%} }%>
			</table>
			</td>
		</tr>
		<tr>
			<td  colspan="11">
			<table>
				<tr>
					<td>属性key</td>
					<td>属性值</td>
				</tr>
				<%
				
					List<DiscoverProp> dss = instance.getDiscoverProps();
					if(dss!=null && dss.size()>0){
						for(DiscoverProp p:ds){
				%>
				<tr>
					<td><%=p.getKey() %></td>
					<td><%=StringUtils.join(p.getValues(),",") %></td>
				</tr>
				<%} }%>
			</table>
			</td>
		</tr>
	</table>
	<%
				}
			}
		%>
</form>
	
</body>
</html>