(function(){
	var curTab = $('.device-netflow-main-tabs').tabs('getSelected');
	
	var displayCount = 5;
	var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
	var type = curTab.find('.device-detail-indecator').combobox('getValue');
	var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');

	var tarDiv = curTab.find('.device-nexthop-datagrid');
	var deviceIp = $('.netflow-main').data('deviceIp');

	datagridObj_netflow_nexthop = oc.ui.datagrid({
		selector: tarDiv,
		sortOrder : 'desc',
		sortName: 'in_flows',
		pagination: false,
		queryParams :{
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			deviceIp:deviceIp,
			sort : "in_flows",
			order : 'desc',
			showpagination:false,
			starttime: stime,
			endtime: etime
		},
		url:oc.resource.getUrl('netflow/devices/findnexthopbydevice.htm'),
		width:'auto',
		height:'auto',
		columns:[[       
	         {field:'next_hop',title:'下一跳',sortable:false},
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
	     onLoadSuccess : function(data){
	    	var param =data.dcBo;
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/devices/nexthopchartoncolum.htm'),
    			data:{
    				deviceCondition : param,
    			},
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){ 
    				data = data.data;
    				var timepoint = data.timepoints;
    				var terminals = data.deviceNetFlowChartDataModel;
    				var sortcolum = data.sortColum;
    				
    				var obj = parseForJR(sortcolum);
    				var unit = obj.unit;
    				var yAxisName = obj.yAxisName;
    				
    				var st = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
    				var et = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
    				
    				var ti = curTab.find('.device-detail-timeinterval').combobox('getValue');;
    				var step = getStep(ti, timepoint);
    				genNetflowChart(curTab.find('.device-nexthop-hc'), 660, 135, timepoint, terminals, unit, yAxisName, step, ti, st, et);
    			}
    		});
	     }
	});
	
	changeIndicatorDetail(curTab.find('.device-nexthop-datagrid'), type, 'nexthop', 2, false);
	
})();