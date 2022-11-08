<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String topoId = request.getParameter("topoId");
String holderId = "topo_api_"+topoId;
String fsId = "fs_"+topoId;
%>
<div class="topo_api_holder" id="<%=holderId%>" style="height:100%;position:relative;"></div>
<a id="<%=fsId%>" style="position:absolute;topo:2px;left:2px;">全屏</a>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoApiFacade.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoAttr.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoDevice.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoLink.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoScale.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoShower.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoState.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoImg.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoData.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/module/topo/api/TopoZIndex.js"></script>
<script type="text/javascript">
	$(function(){
		var topoId=<%=topoId%>;
		var holderId ="<%=holderId%>";
		var fsId = "<%=fsId%>";
		var api = new TopoApiFacade({
			topoId:topoId,
			holder:$("#"+holderId)
		});
		var fsbtn = $("#"+fsId);
		fsbtn.appendTo($("#"+holderId));
		fsbtn.click(function(){
			api.fullScreen();
		});
	});
</script>

