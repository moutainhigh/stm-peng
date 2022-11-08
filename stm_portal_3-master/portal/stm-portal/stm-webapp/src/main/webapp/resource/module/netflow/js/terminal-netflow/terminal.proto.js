(function(){

//	var timeInterval = $('#timeInterval').combobox('getValue'),
//	timeObj = parseTime(timeInterval),
//	startTime=timeObj.startTime, 
//	endTime=timeObj.endTime;
//	var displayCount = $('#displayCount').combobox('getValue');

	var tab = $('.terminal-netflow-main-tabs').tabs('getSelected');

	var startTime = tab.find('.terminal-detail-custom-starttime').datetimebox('getValue');
	var endTime = tab.find('.terminal-detail-custom-endtime').datetimebox('getValue');
	var timeInterval = tab.find('.terminal-detail-timeinterval').combobox('getValue');
	var type = tab.find('.terminal-detail-indecator').combobox('getValue');
	var displayCount = 5;
	
	var terminal_name = $('.netflow-main').data('terminal_name');
	var terminal_ip = $('.device-terminal').data('terminal_ip');
	if(terminal_ip != null && ''!= terminal_ip && undefined != terminal_ip){
		terminal_name = terminal_ip;
	}
	
	var tarDiv = $('.terminal-proto-datagrid');
	datagridObj_netflow_proto = oc.ui.datagrid({
		selector:tarDiv,
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
		pagination: false,
		sortOrder : 'desc',
		sortName: 'in_flows',
		url:oc.resource.getUrl('netflow/terminal/getprotocolbyterminal.htm'),
		columns:[[
	          
	         {field:'proto_name',title:'协议名称',width:'20%',sortable:false},
	       
	         {field:'in_flows',title:'流入流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 var GB = 1024*1024*1024;
	        	 var MB = 1024*1024 ;
	        	 if(value == null || value == 0){
	        		 return '0MB';
	        	 }
	        	 else{
	        		 if(value >= GB ){
	        			 value = value/GB;
	        			 value = value.toFixed(2);
	        			 return value + 'GB';
	        		 }
	        		 value=value/MB;
	        		 value = value.toFixed(2);
	        		 return value + 'MB';
	        	 }
	         }},
	         {field:'out_flows',title:'流出流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 var GB = 1024*1024*1024;
	        	 var MB = 1024*1024 ;
	        	 if(value == null || value == 0){
	        		 return '0MB';
	        	 }
	        	 else{
	        		 if(value >= GB ){
	        			 value = value/GB;
	        			 value = value.toFixed(2);
	        			 return value + 'GB';
	        		 }
	        		 value=value/MB;
	        		 value = value.toFixed(2);
	        		 return value + 'MB';
	        	 }
	         }},
	         {field:'total_flows',title:'总流量',sortable:true,order: 'desc',formatter:function(value,rowData,rowIndex){
	        	 var GB = 1024*1024*1024;
	        	 var MB = 1024*1024 ;
	        	 if(value == null || value == 0){
	        		 return '0MB';
	        	 }
	        	 else{
	        		 if(value >= GB ){
	        			 value = value/GB;
	        			 value = value.toFixed(2);
	        			 return value + 'GB';
	        		 }
	        		 value=value/MB;
	        		 value = value.toFixed(2);
	        		 return value + 'MB';
	        	 }
	         }},
	         {field:'in_packages',title:'流入包数',sortable:true,order: 'desc'},
	         {field:'out_packages',title:'流出包数',sortable:true,order: 'desc'},
	         {field:'total_packages',title:'总包数',sortable:true,order: 'desc'},
	         {field:'in_speed',title:'流入速率',sortable:true,order: 'desc',formatter:function(val,rowData,rowIndex){
	        	 var GB = parseFloat(1024*1024*1024);
	        	 var MB = parseFloat(1024*1024); 
	        	 var KB = parseFloat(1024); 	 
	        	 if(val == null || val == 0){
	        		 return '0KBps';
	        	 }
	        	 else{	        		 
	        		 var speed = parseFloat(val);
	        		 if(speed >= GB ){
	        			 speed = speed/GB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'GBps';
	        		 } else if(speed >= MB ){
	        			 speed = speed/MB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'MBps';
	        		 } else if(speed > KB){
	        			 speed = speed / KB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'KBps';
	        		 }else{
	        			 return speed+"Bps";
	        		 }
	        	 }
	         }},
	         {field:'out_speed',title:'流出速率',sortable:true,order: 'desc',formatter:function(val,rowData,rowIndex){
	        	 var GB = parseFloat(1024*1024*1024);
	        	 var MB = parseFloat(1024*1024); 
	        	 var KB = parseFloat(1024); 	 
	        	 if(val == null || val == 0){
	        		 return '0KBps';
	        	 }
	        	 else{
	        		 var speed = parseFloat(val);
	        		 if(speed >= GB ){
	        			 speed = speed/GB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'GBps';
	        		 } else if(speed >= MB ){
	        			 speed = speed/MB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'MBps';
	        		 } else if(speed > KB){
	        			 speed = speed / KB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'KBps';
	        		 }else{
	        			 return speed+"Bps";
	        		 }
	        	 }
	         }},
	         {field:'total_speed',title:'总速率',sortable:true,order: 'desc',formatter:function(val,rowData,rowIndex){
	        	 var GB = parseFloat(1024*1024*1024);
	        	 var MB = parseFloat(1024*1024); 
	        	 var KB = parseFloat(1024); 	 
	        	 if(val == null || val == 0){
	        		 return '0KBps';
	        	 }
	        	 else{   		 
	        		 var speed = parseFloat(val);
	        		 if(speed >= GB ){
	        			 speed = speed/GB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'GBps';
	        		 } else if(speed >= MB ){
	        			 speed = speed/MB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'MBps';
	        		 } else if(speed > KB){
	        			 speed = speed / KB;
	        			 speed = speed.toFixed(2);
	        			 return speed + 'KBps';
	        		 }else{
	        			 return speed+"Bps";
	        		 }
	        	 }
	         }},
	         {field:'flows_rate',title:'占比',sortable:true,order: 'desc',formatter:function(val,rowData,rowIndex){
	        	if(val == null || val == 0){
	        		return '0%';
	        	}else{
	        		var rate = parseFloat(val);
	        		rate = rate*100;
	        		rate = rate.toFixed(2);
	        		return rate+'%';
	        	}
	        	
	        	
	         }}
	     ]],
	     onLoadSuccess : function(data){
	    	 var param =data.tcBo;
	    		oc.util.ajax({
	    			url:oc.resource.getUrl('netflow/terminal/getprotochartbyterminal.htm'),
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
	    				var yAxisName = obj.yAxisName;
	    				
//	    				var unit = '';
//	    				switch(sortcolum){
//	    				case '流入流量': unit = 'MB'; break;
//	    				case '流出流量': unit = 'MB'; break;
//	    				case '总流量' : unit = 'MB'; break;
//	    				case '流入包数':unit = 'MB';break;
//	    				case '流出包数':unit = 'MB';break;
//	    				case '总包数':unit = '';break;
//	    				case '流入速率' : unit = 'Mbps';break;
//	    				case '流出速率':unit = 'Mbps' ;break;
//	    				case '总速率':unit = 'Mbps';break;
//	    				case '占比':unit = '%';break;
//	    				}
	    				
	    				var ti = tab.find('.terminal-detail-timeinterval').combobox('getValue');
	    				var step = getStep(ti, timepoint);
	    				var st = tab.find('.terminal-detail-custom-starttime').datetimebox('getValue');
	    				var et = tab.find('.terminal-detail-custom-endtime').datetimebox('getValue');

	    				genNetflowChart(tab.find('.terminal-proto-hc'), 660, 135, timepoint, terminals, unit, yAxisName, step, ti, st, et);
	    				
	    			}
	    		});
	     }
	});
	
	changeIndicatorDetail(tarDiv, type, 'proto', 2, false);

})();

function showTerminalProtos(){
	var rows = $(".terminal-proto-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="terminal-proto-allproto"></div>').dialog({    
		 	title: "协议",
		    width: 1200,
		    height: 605, 
		    top: '5%',
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/terminal-netflow/terminal_proto.html"),    
		    modal: true
		});
	}
}