(function(){
	
	var curTab = $('.device-netflow-main-tabs').tabs('getSelected');
	
	var displayCount = 5;
	var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
	var type = curTab.find('.device-detail-indecator').combobox('getValue');
	var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
	
	var deviceIp = $('.netflow-main').data('deviceIp');
	
	datagridObj_netflow_session = oc.ui.datagrid({
		selector:curTab.find('.device-session-datagrid'),
		pagination: false,
		sortOrder : 'desc',
		sortName: 'in_flows',
		queryParams :{
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			deviceIp:deviceIp,
			sort : "in_flows",
			order : 'desc',
			showpagination : false,
			starttime: stime,
			endtime: etime
		},
		url:oc.resource.getUrl('netflow/devices/findsessionbydevice.htm'),
		columns:[[
	         {field:'src_ip',title:'原地址',sortable:false},
	         {field:'dst_ip',title:'目的地址',sortable:false},
	         {field:'in_flows',title:'流入流量',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
	         {field:'out_flows',title:'流出流量',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
	         {field:'total_flows',title:'总流量',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, null);
	         }},
//	         {field:'in_packages',title:'流入包数',sortable:true},
//	         {field:'out_packages',title:'流出包数',sortable:true},
//	         {field:'total_package',title:'总包数',sortable:true},
	         {field:'in_speed',title:'流入速率',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'out_speed',title:'流出速率',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'total_speed',title:'总速率',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'flows_rate',title:'占比',sortable:true,order:'desc',formatter:function(value,rowData,rowIndex){
	        	 return pctgeFormatter(value);
	         }}
	     ]],
	     onClickRow: function(rowIndex, rowData) {
	    	 $('<div class="device-session"></div>').dialog({    
	    		 	title: '流量分析 - 会话- ' + rowData.src_ip+"-"+rowData.dst_ip, 
	    		    width: 1200,
	    		    height: 580, 
	    		    top:'5%',
	    		    closed: false,    
	    		    cache: false,
	    		    position:'center',
	    		    draggable:true,
	    		    href: oc.resource.getUrl("resource/module/netflow/device-netflow/DeviceToSessionDialog.html"),    
	    		    modal: true   
	    	 });
	    	 $('.device-session').data('srcip',rowData.src_ip);
	    	 $('.device-session').data('dstip',rowData.dst_ip);
	    	 
	    	 var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue')
	    	 var type = curTab.find('.device-detail-indecator').combobox('getValue');
	    	 var startTime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	    	 var endTime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');

	    	 $('.device-session').data('timeInterval', timeInterval);
	    	 $('.device-session').data('type', type);
	    	 $('.device-session').data('startTime', startTime);
	    	 $('.device-session').data('endTime', endTime);
	     },
	     
	     onLoadSuccess : function(data){
    		var param =data.dcBo;
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/devices/sessionchartoncolum.htm'),
    			data:{
    				deviceCondition : param,
    				
    			},
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){ 
    				var timepoint = data.data.timepoints;
    				var terminals = data.data.deviceNetFlowChartDataModel;
    				var sortcolum = data.data.sortColum;
    				
    				var obj = parseForJR(sortcolum);
    				var unit = obj.unit;
    				var yAxisName = obj.yAxisName;
    				
//    				switch(sortcolum){
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
//    				}
    				var ti = curTab.find('.device-detail-timeinterval').combobox('getValue');
    				var st = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
    				var et = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
    				
    				var step = getStep(ti, timepoint);
    				genNetflowChart(curTab.find('.device-session-hc'), 660, 135, timepoint, terminals, unit, yAxisName, step, ti, st, et);
    			}
    		});
	     }
	});
	
	changeIndicatorDetail(curTab.find('.device-session-datagrid'), type, 'session', 2, true);

})();