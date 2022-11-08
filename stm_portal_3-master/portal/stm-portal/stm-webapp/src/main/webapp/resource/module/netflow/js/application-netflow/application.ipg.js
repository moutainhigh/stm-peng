(function(){
	
//	var timeInterval = $('#timeInterval').combobox('getValue'),
//	timeObj = parseTime(timeInterval),
//	startTime=timeObj.startTime, 
//	endTime=timeObj.endTime;
//	var displayCount = $('#displayCount').combobox('getValue');
	
	var tab = $('.app-netflow-main-tabs').tabs('getSelected');
	
	var timeInterval = tab.find('.app-detail-timeinterval').combobox('getValue');
	var startTime = tab.find('.app-detail-custom-starttime').datetimebox('getValue');
	var endTime = tab.find('.app-detail-custom-endtime').datetimebox('getValue');
	var type = tab.find('.app-detail-indecator').combobox('getValue');
	var displayCount = 5;
	
	var appid = $('.netflow-main').data('appid');
	var app_ip = $('.device-app-dlg').data('app_id');
	if(app_ip != null && ''!=app_ip&&undefined!=app_ip){
		appid = app_ip;
	}
	
	var tarDiv = tab.find('.application-ipg-datagrid');
	datagridObj_netflow_app_ipg = oc.ui.datagrid({
		selector: tarDiv,
		pagination: false,
		sortOrder : 'desc',
		sortColumn: 'in_flows',
		queryParams :{
			starttime: startTime,
			endtime: endTime,
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			sort : "in_flows",
			order : 'desc',
			app_id : appid,
			showpagination : false
		},
		url:oc.resource.getUrl('netflow/application/getipgbyapp.htm'),
		columns:[[
	       
	         {field:'ipgroup_name',title:'IP分组名称',sortable:false},
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
	    	 var param =data.appBo;
	    		oc.util.ajax({
	    			url:oc.resource.getUrl('netflow/application/getipgchartbyapp.htm'),
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
	    				
//	    				var unit = '';
//	    				switch(sortcolum){
//	    				case '流入流量': unit = 'MB'; break;
//	    				case '流出流量': unit = 'MB'; break;
//	    				case '总流量' : unit = 'MB'; break;
//	    				case '流入包数':unit = 'MB';break;
//	    				case '流出包数':unit = 'MB';break;
//	    				case '流入速率' : unit = 'Mbps';break;
//	    				case '流出速率':unit = 'Mbps' ;break;
//	    				case '总速率':unit = 'Mbps';break;
//	    				case '占比':unit = '%';break;
//	    				}
	    				
	    				var ti = tab.find('.app-detail-timeinterval').combobox('getValue');
	    				var step = getStep(ti, timepoint);
	    				var st = tab.find('.app-detail-custom-starttime').datetimebox('getValue');
	    				var et = tab.find('.app-detail-custom-endtime').datetimebox('getValue');

	    				genNetflowChart(tab.find('.application-ipg-hc'), 660, 135, timepoint, terminals, unit, yAxisName, step, ti, st, et);
	    			}
	    		});
	     }
	});
	
	changeIndicatorDetail(tarDiv, type, 'ipg', 2, false);

})();

function showApplicationipgs(){
	var rows = $(".Application-ipg-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="application-ipg-allipg"></div>').dialog({    
		 	title: "IP分组",
		    width: 1200,    
		    height: 605, 
		    top:"5%",
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/application-netflow/application_IPG.html"),    
		    modal: true
		});
	}
}