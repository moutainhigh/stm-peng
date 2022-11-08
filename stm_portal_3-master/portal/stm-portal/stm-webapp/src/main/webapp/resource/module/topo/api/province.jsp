<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
  <head>
    <title>OC4四级拓扑</title>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/third/bootstrap-3.2.0/css/bootstrap.css">
	<link href="<%=basePath %>resource/third/jquery-ui-1.11.1.custom/jquery-ui.css" rel="stylesheet">
	<link href="<%=basePath %>resource/third/jquery-easyui-1.4/themes/blue/easyui.css" rel="stylesheet" id="skin_easyui">
	<link href="<%=basePath %>resource/third/jquery-easyui-1.4/themes/icon.css" rel="stylesheet" id="skin_icon">
	<link href="<%=basePath %>resource/third/zTree_v3/css/zTreeStyle/zTreeStyle.css" rel="stylesheet">
	<link href="<%=basePath %>resource/third/uploadify/uploadify.css" rel="stylesheet">
	<link href="<%=basePath %>resource/third/umeditor/themes/classic/css/umeditor.css" rel="stylesheet">
	<link href="<%=basePath %>resource/themes/blue/css/oc.css" rel="stylesheet" id="skin_oc">
	<link href="<%=basePath %>resource/themes/blue/css/topo.css" rel="stylesheet" id="skin_topo">
	<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/module/map/css/home.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/module/map/css/courtMap.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/module/map/css/courtList.css">
	<script src="<%=basePath %>resource/third/oc.third.js" type="text/javascript"></script>
	<script src="<%=basePath %>resource/third/crypto-js-3.1.6/crypto-js.js"></script>
	<script src="<%=basePath %>resource/js/oc.js"></script>
	<script src="<%=basePath %>resource/js/ui/oc.ui.js"></script>
	<script src="<%=basePath %>resource/module/topo/util/svg.js"></script>
	<script src="<%=basePath %>resource/third/canvas/raphael.js"></script>
	<script src="<%=basePath %>resource/module/topo/contextMenu/TopoNewTip.js"></script>
	<script>
		$('#indexContent').on('click','.datebox',function(){
			$('.validatebox-text').css({'padding-top':'4px','padding-bottom':'4px'});
		})
	</script>
	<script type="text/javascript" src="<%=basePath %>resource/index_skin.js"></script>
    <script type="text/javascript">
    	$(function(){
    		oc.ns("oc.topo");
    		oc.util.ajax({
				url:oc.resource.getUrl('system/login/login.htm'),
				data:{u:oc.util.AESencrypt('admin'),p:oc.util.MD5('admin')},//zgjk2015pw
				success:function(d){
					if(d.code==200){
			    		oc.resource.loadScripts(["resource/module/topo/map/TopoMapView.js",
						"resource/module/topo/map/TopoSvgElement.js"], function(){
			    			oc.topo.util={
				    				module:""
			    			};
			    			var areaKey = oc.resource.href.split('?')[1].split('&')[0];
			    			if(areaKey){
			    				var parameter = {areakey:areaKey};
			    				var tm = new TopoMapView(parameter);
			    			}else{
								var tm = new TopoMapView();
			    			}
			    			eve.on("topo.map.tomap",function(key,level){
			    				var tid = setInterval(function(){
			    					if(tm.graph){
			    						tm.graph.toMap(key,level);
			    						clearInterval(tid);
			    					}
			    				},200);
			    			});
						});
					}
				},
				failureMsg:null,
				successMsg:null
			});
    	});
    </script>
  </head>
  
  <body style="padding:0px;margin:0px;overflow-y:hidden;">
  	<div id="topo_holder" style="width:100%;height:100%;"></div>
  	<div><label>北京</label></div>
  </body>
</html>
