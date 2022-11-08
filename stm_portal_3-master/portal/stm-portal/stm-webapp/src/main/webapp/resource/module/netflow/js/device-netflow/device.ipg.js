(function(){
	var curTab = $('.device-netflow-main-tabs').tabs('getSelected');
	
	var displayCount = 5;
	var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
	var type = curTab.find('.device-detail-indecator').combobox('getValue');
	var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
	
	var tarDiv = curTab.find('.device-ipgroup-datagrid');
	var deviceIp = $('.netflow-main').data('deviceIp');
	
	datagridObj_netflow_ipg = oc.ui.datagrid({
		selector: tarDiv,
		pagination: false,
		sortOrder : 'desc',
		sortName: 'in_flows',
		queryParams :{
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount : displayCount,
			deviceIp:deviceIp ,
			sort : "in_flows",
			order : 'desc',
			showpagination : false,
			starttime: stime,
			endtime: etime
		},
		url:oc.resource.getUrl('netflow/devices/findipgbydevice.htm'),
		columns:[[
	       
	         {field:'ipgroup_name',title:'IP分组名称',sortable:false},
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
	    	 $('<div class="terminal-ipg"></div>').dialog({   
    		 	title: '流量分析 - IP分组 - ' + rowData.ipgroup_name,   
    		 	width: 1200,
    		    height: 620,    
    		    top:'5%',
    		    closed: false,    
    		    cache: false,
    		    position:'center',
    		    draggable:true,
    		    href: oc.resource.getUrl("resource/module/netflow/device-netflow/DeviceToIPGDialog.html"),    
    		    modal: true   
	    	});
 
	    	$('.terminal-ipg').data('ipgroup_id',rowData.ipgroup_id);
	    	 //传值：timeInterval, type, startTime, endTime
	    	 var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue')
	    	 var type = curTab.find('.device-detail-indecator').combobox('getValue');
	    	 var startTime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	    	 var endTime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
	    	 $('.terminal-ipg').data('startTime', startTime);
	    	 $('.terminal-ipg').data('endTime', endTime);
	    	 $('.terminal-ipg').data('timeInterval', timeInterval);
	    	 $('.terminal-ipg').data('type', type);

	     },
	     onLoadSuccess : function(data){
	    	 var param =data.dcBo;
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/devices/ipgchartoncolum.htm'),
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
    				
    				var ti = curTab.find('.device-detail-timeinterval').combobox('getValue');
    				var st = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
    				var et = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');

    				var step = getStep(ti, timepoint);
    				genNetflowChart(curTab.find('.device-ipgroup-hc'), 660, 135, timepoint, terminals, unit, yAxisName, step, ti, st, et);
    			}
    		});
	     }
	});
	
	changeIndicatorDetail(curTab.find('.device-ipgroup-datagrid'), type, 'ipg', 2, false);

})();