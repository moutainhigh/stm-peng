/*
* 设备联通性
*/
function connectionState(selector,opt){
	this.opt = {
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);
	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}
var csp = connectionState.prototype;

csp.init = function(){
	this.dataObj = $("<div/>");
	this.dataObj.css({width:'100%',height:'100%'});
	this.opt.selector.html(this.dataObj);
	this.genGrid();
}
csp.genGrid = function(){
	var that = this;
	
	//*全屏的时候字符串显示*//
	var isFullscreen = that.opt.index.toString().indexOf('fullscreen_');
	var fullClass = '';
	 if(isFullscreen >= 0){
		 fullClass = 'full';
	 }
	 //**//
	
	var opt = that.opt;
	var ids = [];
	for(var id in opt.selectedResource){
		ids.push(Number(id));
	}
	ids = ids.join(',');
	var parameter = {};
	parameter.ids = ids;
	parameter.metricTypes = 'AvailabilityMetric';
		oc.util.ajax({
			url: oc.resource.getUrl('home/workbench/resource/getNewMetrics.htm'),
			timeout:null,
			data:{
				ids : parameter.ids,
				metricTypes : parameter.metricTypes
			},
			success:function(data){
				var dataShow = [];
				for(var id in opt.selectedResource){
					if(!data.data[id].isExist){
						continue;
					}
					var result = {};
					result.state = data.data[id].statusCollection;
					result.ipAddress = opt.selectedResource[id].ip;
					result.resourceName = opt.selectedResource[id].name;
					result.resourceType = opt.selectedResource[id].resourceType;
					dataShow.push(result);
				}
				oc.ui.datagrid({
					selector : that.dataObj ,
					data:dataShow,
					pagination : false,
					width : '100%',
					height : 'auto',
					fit:true,
					columns : [ [
			             {
			            	 field : 'state',
			            	 title:'连通性',
			            	 align : 'left',
			            	 width : 80,
			            	 formatter : function(value, row, index) {
			            		 var st = 'ava_notavailable';
			            		 if(value == true)
			            			 st = 'ava_available';
			            		 return "<span class='alert-ico "+ st +"'></span>";
			            	 }
			             },
			             {
			            	 field : 'ipAddress',
			            	 title:'IP地址',
			            	 width : 100,
			            	 align : 'left',
			            	 ellipsis:true,
			            	 formatter : function(value, row, index) {
			            		 return "<lable class='"+ fullClass +"'>"+value+"</lable>";
			            	 }
			             },
			             {
			            	 field : 'resourceName',
			            	 title : '资源名称',
			            	 width : 100,
			            	 align : 'left',
			            	 ellipsis:true,
			            	 formatter : function(value, row, index) {
			            		 return "<lable class='"+ fullClass +"'>"+value+"</lable>";
			            	 }
			             }, 
			             {
			            	 field : 'resourceType',
			            	 title:'资源类型',
			            	 width : 100,
			            	 align : 'left',
			            	 ellipsis:true,
			            	 formatter : function(value, row, index) {
			            		 return "<lable class='"+ fullClass +"'>"+value+"</lable>";
			            	 }
			             }, ]],
			             onLoadSuccess:function(){
			            	 //全屏后字取消字符串截取
			            	 if(isFullscreen){
			            		 $(".full").parent().addClass("datagrid-full");
            	 			 }
			             }
				});
			}
		});
}

csp.refresh = function(){
	this.init();
}