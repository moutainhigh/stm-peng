<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String tmp = request.getParameter("topo_name");
	if(null==tmp){
		tmp="二层拓扑";
	}
	String topoName = new String(tmp.getBytes("iso-8859-1"),"UTF-8");
	String holderId = "topo_api_"+topoName;
	String fsId = "fs_"+topoName;
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
		var topoName="<%=topoName%>";
		var holderId ="<%=holderId%>";
		var fsId = "<%=fsId%>";
		var api = new TopoApiFacade({
			topoName:topoName,
			holder:$("#"+holderId)
		});
		var fsbtn = $("#"+fsId);
		fsbtn.appendTo($("#"+holderId));
		fsbtn.click(function(){
			api.fullScreen();
		});
	});
</script>
