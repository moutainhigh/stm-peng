(function(){
	
	//设备详细页面的参数
//	var timeInterval = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();
	
	var startTime = $('.device-terminal').data('startTime');
	var endTime = $('.device-terminal').data('endTime');
	var timeInterval = $('.device-terminal').data('timeInterval');
	var type = $('.device-terminal').data('type');
	var displayCount = 5;

	var terminal_name = $('.netflow-main').data('terminal_name');
	var terminal_ip = $('.device-terminal').data('terminal_ip');
	if(terminal_ip != null && ''!= terminal_ip && undefined != terminal_ip){
		terminal_name = terminal_ip;
	}
	
	var tarDiv = $('.terminal-tos-datagrid');
	datagridObj_netflow_terminal_tos = oc.ui.datagrid({
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
			terminal_name : terminal_name
		},
		url:oc.resource.getUrl('netflow/terminal/gettosbyterminal.htm'),
		columns:[[
	       
	         {field:'tos_name',title:'tos名称',sortable:false},
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
	    	 var param =data.tcBo;
	    		oc.util.ajax({
	    			url:oc.resource.getUrl('netflow/terminal/gettoschartbyterminal.htm'),
	    			data:{
	    				terminalConditionVo : param,
	    				
	    			},
	    			failureMsg:'加载用户域数据失败！',
	    			async:false,
	    			success:function(data){ 
	    				var timepoint = data.data.timepoints;
	    				var terminals = data.data.terminalChartDataModel;
	    				var sortcolum = data.data.sortColum;
	    				var obj = parseForJR(sortcolum);
	    				var unit = obj.unit;
	    				var yname = obj.yAxisName;
	    				
	    				var ti = timeInterval;
	    				var step = getStep(ti, timepoint);
	    				var st = $('.device-terminal').data('startTime');
	    				var et = $('.device-terminal').data('endTime');

	    				genNetflowChart($('.terminal-tos-hc'), 660, 135, timepoint, terminals, unit, yname, step, ti, st, et);

	    			}
	    		});
	     }
	});
	
	changeIndicatorDetail(tarDiv, type, 'tos', 2, false);

})();

function showTerminalToss(){
	var rows = $(".terminal-tos-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="terminal-tos-alltos"></div>').dialog({    
		 	title: "TOS",
			width: 1100,    
		    height: 570,   
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/terminal-netflow/terminal_tos.html"),    
		    modal: true
		});
	}
}