(function(){

	//设备详细页面的参数
//	var timeInterval = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();
	
	var startTime = $('.device-app-dlg').data('startTime');
	var endTime = $('.device-app-dlg').data('endTime');
	var timeInterval = $('.device-app-dlg').data('timeInterval');
	var type = $('.device-app-dlg').data('type');

	var displayCount = 5;

	var appid = $('.netflow-main').data('appid');
	var app_ip = $('.device-app-dlg').data('app_id');
	if(app_ip != null && ''!=app_ip&&undefined!=app_ip){
		appid = app_ip;
	}
	
	var tarDiv = $('.application-session-datagrid');
	datagridObj_netflow_session = oc.ui.datagrid({
		selector: tarDiv,
		pagination: false,
		sortOrder : 'desc',
		queryParams :{
			starttime: startTime,
			endtime: endTime,
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			sort : "in_flows",
			order : 'desc',
			showpagination : false,
			app_id : appid
		},
		url:oc.resource.getUrl('netflow/application/getsessionbyapp.htm'),
		columns:[[
	         {field:'src_ip',title:'原地址',sortable:false},
	         {field:'dst_ip',title:'目的地址',sortable:false},
	         {field:'in_flows',title:'流入流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
	         {field:'out_flows',title:'流出流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
	         {field:'total_flows',title:'总流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
//	         {field:'in_packages',title:'流入包数',sortable:true},
//	         {field:'out_packages',title:'流出包数',sortable:true},
//	         {field:'total_packages',title:'总包数',sortable:true},
	         {field:'in_speed',title:'流入速率',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'out_speed',title:'流出速率',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'total_speed',title:'总速率',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'flows_rate',title:'占比',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 return pctgeFormatter(value);
	         }}
	     ]],
	     onLoadSuccess : function(data){
	    	 var param =data.appBo;
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/application/getsessionchartbyapp.htm'),
    			data:{
    				applicationConditionVo : param,
    				
    			},
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){ 
    				var timepoint = data.data.timepoints;
    				var terminals = data.data.applicationChartDataModel;
    				var sortcolum = data.data.sortColum;
    				var obj = parseForJR(sortcolum);
    				var unit = obj.unit;
    				var yAxisName = obj.yAxisName;
    				
    				var ti = $('#device_app_timeInterval').combobox('getValue');
    				var step = getStep(ti, timepoint);
    				var st = $('.device-app-dlg').data('startTime');
    				var et = $('.device-app-dlg').data('endTime');

    				genNetflowChart($('.application-session-hc'), 660, 135, timepoint, terminals, unit, yAxisName, step, ti, st, et);
    			}
    		});
	     }
	});
	
	changeIndicatorDetail(tarDiv, type, 'session', 2, true);

})();
function showApplicationSessions(){
	var rows = $(".Application-ipg-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="application-session-allsession"></div>').dialog({    
		 	title: "会话",
		    width:1100,    
		    height: 570,    
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/application-netflow/application_session.html"),    
		    modal: true
		});
	}
}