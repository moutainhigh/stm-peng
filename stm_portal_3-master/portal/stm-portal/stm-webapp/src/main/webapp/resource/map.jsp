<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
  <head>
    <title>OC4四级拓扑</title>
 
	<link href="<%=basePath %>resource/themes/blue/css/topo.css" rel="stylesheet" id="skin_topo">
	<link href="<%=basePath %>resource/third/jquery-ui-1.11.1.custom/jquery-ui.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=basePath %>resource/third/ACE/icomoon/style.css" />
	<link rel="stylesheet" href="<%=basePath %>resource/third/ACE/assets/css/font-awesome.css" />
	<link href="<%=basePath %>resource/third/jquery-easyui-1.4/themes/blue/easyui.css" rel="stylesheet" id="skin_easyui">
	<link href="<%=basePath %>resource/third/jquery-easyui-1.4/themes/icon.css" rel="stylesheet" id="skin_icon">
	<link href="<%=basePath %>resource/third/zTree_v3/css/zTreeStyle/zTreeStyle.css" rel="stylesheet">
	<link href="<%=basePath %>resource/third/uploadify/uploadify.css" rel="stylesheet">
	<link href="<%=basePath %>resource/third/umeditor/themes/classic/css/umeditor.css" rel="stylesheet">
	<link href="<%=basePath %>resource/themes/blue/css/Skin.css"  rel="stylesheet">
	<link href="<%=basePath %>resource/themes/blue/css/icon.css"  rel="stylesheet">
	<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/module/map/css/home.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/module/map/css/courtMap.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/module/map/css/courtList.css">
    <link rel="stylesheet" href="<%=basePath %>resource/third/ACE/assets/css/menu.css" />
	<link href="<%=basePath %>resource/themes/blue/css/main.css" rel="stylesheet" id="skin_oc">
	<link rel="stylesheet" href="<%=basePath %>resource/themes/blue/css/Newskin.css" />
	<link rel="shortcut icon" href="favicon.ico">
	<link rel="Bookmark" href="favicon.ico">
	
	<script src="<%=basePath %>resource/third/jquery-1.7.2.js"></script>
	<script src="<%=basePath %>resource/third/jquery-ui-1.11.1.custom/jquery-ui.js"></script>

	<script src="<%=basePath %>resource/third/jquery-easyui-1.4/jquery.easyui.min.js"></script>
	<script src="<%=basePath %>resource/third/jquery-easyui-1.4/locale/easyui-lang-zh_CN.js"></script>
	
	<script src="<%=basePath %>resource/third/jquery.form.js"></script>
	<script src="<%=basePath %>resource/third/jquery.multifile.js"></script>
	<script src="<%=basePath %>resource/third/jquery.cookie.js"></script>
	<!-- <script src="third/jquery.upload.js"></script> -->
	<script src="<%=basePath %>resource/third/zTree_v3/js/jquery.ztree.all-3.5.js"></script>
	
	<script src="<%=basePath %>resource/third/highcharts/js/highcharts.js"></script>
	<script src="<%=basePath %>resource/third/highcharts/js/highcharts-more.js"></script>
	<script src="<%=basePath %>resource/third/highcharts/js/exporting.js"></script>
	<script src="<%=basePath %>resource/third/moment/moment.min.js"></script>
	<script src="<%=basePath %>resource/third/canvas/raphael.js"></script>
	<script src="<%=basePath %>resource/third/canvas/raphael-fn.js"></script>
	<script src="<%=basePath %>resource/third/crypto-js-3.1.6/crypto-js.js"></script>
	
	<script src="<%=basePath %>resource/js/oc.js"></script>

	<script src="<%=basePath %>resource/js/ui/oc.ui.js"></script>
	<script src="<%=basePath %>resource/js/ui/jquery.colorpicker.js"></script>
	<script src="<%=basePath %>resource/third/uploadify/jquery.uploadify.js" type="text/javascript"></script>
	<script src="<%=basePath %>resource/third/umeditor/umeditor.config.js" type="text/javascript"></script>
	<script src="<%=basePath %>resource/third/umeditor/umeditor.min.js" type="text/javascript"></script>
	<script src="<%=basePath %>resource/third/umeditor/lang/zh-cn/zh-cn.js" type="text/javascript"></script>
	<script src="<%=basePath %>resource/third/echarts3.0/echarts.min.gzjs" type="text/javascript"></script>
	<script src="<%=basePath %>resource/third/echarts3.0/liquidfill.gzjs" type="text/javascript"></script>
	
	
	<script src="<%=basePath %>resource/module/topo/util/svg.js"></script>
	<script src="<%=basePath %>resource/module/topo/contextMenu/TopoNewTip.js"></script>
	<script>
		$('#indexContent').on('click','.datebox',function(){
			$('.validatebox-text').css({'padding-top':'4px','padding-bottom':'4px'});
		})
	</script>
    <script type="text/javascript">
    	$(function(){
    		oc.ns("oc.topo");
    		oc.util.ajax({
				url:oc.resource.getUrl('system/login/login.htm'),
				data:{u:oc.util.AESencrypt('admin'),p:oc.util.MD5('admin')},//zgjk2015pw
				success:function(d){
					if(d.code==200){
			    		oc.resource.loadScripts(["resource/index_user.js","resource/module/topo/map/TopoMapView.js",
						"resource/module/topo/map/TopoSvgElement.js"], function(){
			    			oc.topo.util={
			    				module:""
			    			};
			    			oc.index.indexLayout = $('.topo_map');
			    			oc.index.loadLoginUser(function(user){
								var tm = new TopoMapView();
				    			eve.on("topo.map.tomap",function(key,level){
				    				var tid = setInterval(function(){
				    					if(tm.graph){
				    						tm.graph.toMap(key,level);
				    						clearInterval(tid);
				    					}
				    				},200);
				    			});
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
  </body>
</html>
