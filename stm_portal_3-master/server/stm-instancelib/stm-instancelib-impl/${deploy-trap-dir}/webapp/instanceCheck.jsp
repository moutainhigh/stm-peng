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
<%@page import="com.mainsteam.stm.node.LocaleNodeService"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.mainsteam.stm.node.Node"%>
<%@page import="com.mainsteam.stm.node.NodeGroup"%>
<%@page import="com.mainsteam.stm.node.NodeTable"%>
<%@page import="com.mainsteam.stm.node.NodeService"%>
<%@page import="com.mainsteam.stm.node.NodeFunc"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>资源实例查看</title>
<script type="text/javascript">
function switchDisplayDiv(divId){
	var divObj = document.getElementById(divId);
	if(divObj.style.display == "none"){
			divObj.style.display = "block";
		}else{
			divObj.style.display = "none";
		}
}

function hiddenDiv(divId){
	var divObj = document.getElementById(divId);
	alert("hidden");
	divObj.style.display = "none";
	alert(divObj.style.display);
}

function showChildrens(instanceId){
	window.open(window.location.href+"?parentInstanceId="+instanceId,"children", 'height=screen.height, width=screen.width, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no');
}

</script>

</head>
<body>
	<%
	LocaleNodeService localNodeService = (LocaleNodeService) SpringBeanUtil
					.getObject("localNodeService");
			Node currentNode = localNodeService.getCurrentNode();
	%>
	<table border="1">
		<%
				if(currentNode != null){
					if(currentNode.getFunc() == NodeFunc.collector){
						%>	
						<th colspan="12">DCS资源查看是否监控，请查看scheduleMonitor.jsp页面,页面存在资源实例Id，表示该资源监控,反之未监控</th>
						<%
					}
				}
			%>
		<tr>
			<th>实例id</th>
			<th>实例名称</th>
			<th>模型</th>
			<th>类型</th>
			<th>父类型</th>
			<th>发现IP</th>
			<th>发现方式</th>
			<%
				if(currentNode != null){
					if(currentNode.getFunc() == NodeFunc.processer){
						%>	
						<th>生命周期</th>
						<%
					}
				}
			%>
			<th>nodegroupId</th>
			<th>发现属性个数</th>
			<th>模型属性个数</th>
			<th>子实例个数</th>
		</tr>
		<%
			String parentInstanceId = request.getParameter("parentInstanceId");
			ResourceInstanceService instanceService = (ResourceInstanceService) SpringBeanUtil
					.getObject("resourceInstanceService");
			CapacityService capacityService = (CapacityService) SpringBeanUtil
					.getObject("capacityService");
			
			List<ResourceInstance> displayInstaces = null;
			if (parentInstanceId != null) {
				ResourceInstance parentInstance = instanceService
						.getResourceInstance(Long.valueOf(parentInstanceId));
				System.out.println("parentInstance size:" + parentInstance.getDiscoverPropBykey("port"));
				if (parentInstance != null) {
					displayInstaces = parentInstance.getChildren();
				}
			} else {
				displayInstaces = instanceService.getAllParentInstance();
			}

			if (displayInstaces != null && displayInstaces.size() > 0) {
				for (ResourceInstance instance : displayInstaces) {
					List<ResourceInstance> childrens = instance.getChildren();
					ResourceDef resourceDef = capacityService
							.getResourceDefById(instance.getResourceId());
					Map<String, PluginInitParameter[]> pluginInitParameter = resourceDef
							.getPluginInitParameterMap();
					Set<String> discoverPropKeys = new HashSet<String>();
					for (PluginInitParameter[] parameters : pluginInitParameter
							.values()) {
						for (PluginInitParameter parameter : parameters) {
							discoverPropKeys.add(parameter.getId());
						}
					}

					String category = null;
					String parentCategoryId = "";
					String discoveryIp = null;
					if (instance.getParentInstance() != null) {
						category = instance.getChildType();
						discoveryIp = instance.getParentInstance()
								.getShowIP();
					} else {
						parentCategoryId = instance.getParentCategoryId();
						category = instance.getCategoryId();
						discoveryIp = instance.getShowIP();
					}
					int discoveryPropCount = 0;
					if (discoverPropKeys != null && discoverPropKeys.size() > 0) {
						for (String propKey : discoverPropKeys) {
							String[] propKeyValues = instance
									.getDiscoverPropBykey(propKey);
							if (propKeyValues != null) {
								discoveryPropCount++;
							}
						}
					}
					int modulePropCount = 0;
					ResourcePropertyDef[] propertyDefs = resourceDef
							.getPropertyDefs();
					if (propertyDefs != null && propertyDefs.length > 0) {
						for (ResourcePropertyDef pd : propertyDefs) {
							String[] propKeyValues = instance
									.getModulePropBykey(pd.getId());
							if (propKeyValues != null) {
								modulePropCount++;
							}
						}
					}
		%>
		<tr>
			<td><%=instance.getId()%></td>
			<td><%=instance.getName()%></td>
			<td><%=resourceDef == null ? instance.getResourceId()
							: resourceDef.getName()%></td>
			<td><%=category%></td>
			<td><%=parentCategoryId%></td>
			<td><%=discoveryIp%></td>
			<td><%=instance.getDiscoverWay()%></td>
			<%
				if(currentNode != null){
					if(currentNode.getFunc() == NodeFunc.processer){
						%>	
						<td><%=instance.getLifeState()%></td>
						<%
					}
				}
			%>
			<td><%=instance.getDiscoverNode()%></td>
			<td onclick="switchDisplayDiv('discoverProps<%=instance.getId()%>')"
				style="cursor: pointer;"><%=discoveryPropCount%>
				<div id="discoverProps<%=instance.getId()%>"
					style="display: none; position: absolute; z-index: 2; width: auto; height: auto; background-color: #778899;">
					<%
						if (discoverPropKeys != null && discoverPropKeys.size() > 0) {
									for (String propKey : discoverPropKeys) {
										String[] propKeyValues = instance
												.getDiscoverPropBykey(propKey);
										if (propKeyValues != null) {
					%>
					<li><span><%=propKey%></span><span>=</span><span><%=Arrays.toString(propKeyValues)%></span></li>
					<%
						}
									}
								}
					%>
				</div></td>
			<td onclick="switchDisplayDiv('moduleProps<%=instance.getId()%>')"
				style="cursor: pointer;"><%=modulePropCount%>
				<div id="moduleProps<%=instance.getId()%>"
					style="display: none; position: absolute; z-index: 3; width: auto; height: auto; background-color: #f0ffff;">
					<%
						if (propertyDefs != null && propertyDefs.length > 0) {
									for (ResourcePropertyDef pd : propertyDefs) {
										String[] propKeyValues = instance
												.getModulePropBykey(pd.getId());
										if (propKeyValues != null) {
					%>
					<li><span><%=pd.getId()%></span><span>=</span><span><%=Arrays.toString(propKeyValues)%></span></li>
					<%
						}
									}
								}
					%>
				</div></td>
			<td
				onclick="<%if (childrens != null && childrens.size() > 0) {%>showChildrens('<%=instance.getId()%>')<%}%>"
				style="cursor: pointer;"><%=childrens == null ? 0 : childrens.size()%></td>
		</tr>
		<%
			}
			}
		%>
	</table>
</body>
</html>