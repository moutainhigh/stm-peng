<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String uuid = UUID.randomUUID().toString();
	String id = request.getParameter("id");
	String isMax = request.getParameter("isMax");
%>
<div id="<%=uuid %>" style="width:100%;height:100%;"></div>
<script type="text/javascript">
	oc.resource.loadScripts([
	    "resource/module/topo/api/TopoScreenApi.js",
	    "resource/module/topo/api/TopoAttr.js",
	    "resource/module/topo/api/TopoDevice.js",
	    "resource/module/topo/api/TopoLink.js",
	    "resource/module/topo/api/TopoScale.js",
	    "resource/module/topo/api/TopoShower.js",
	    "resource/module/topo/api/TopoState.js",
	    "resource/module/topo/api/TopoImg.js",
	    "resource/module/topo/api/TopoData.js",
	    "resource/module/topo/api/TopoZIndex.js",
	    "resource/module/topo/graph/topo.js"
	],function(){
		var id=JSON.parse(<%=id%>);
		var isMax = <%=isMax%>;
		$.post(oc.resource.getUrl("topo/help/currentTheme.htm"),function(tm){
			oc.ns("oc.topo.screen");
			oc.topo.theme=tm.theme;
			function start(){
				new TopoScreenApi({
					holder:$("#<%=uuid%>"),
					id:parseInt(id),
					onOver:function(){
						if(!isMax){
							$("#<%=uuid%>").find("text").remove();
						}
					}
				});
			}
			start();
		},"json");
	});
	//# sourceURL=topoScreen.js
</script>
