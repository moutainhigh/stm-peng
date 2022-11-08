(function($){

	var curTab = $('.device-netflow-main-tabs').tabs('getSelected');
	
	var displayCount = 5;
	var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
	var type = curTab.find('.device-detail-indecator').combobox('getValue');
	var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');


	var datagridDiv=curTab.find('.device-app-datagrid');
	var	deviceIp = $('.netflow-main').data('deviceIp');

	datagridObj_netflow_app=oc.ui.datagrid({
			selector:datagridDiv,
			url:oc.resource.getUrl('netflow/device/app/deviceAppNetflowPageSelect.htm'),
			queryParams: {
				timePerid: timeInterval,
				deviceIp: deviceIp,
				rowCount: displayCount,
				needPagination: false,
				querySize: displayCount,
				startTime: stime,
				endTime: etime
			},
			height: 150,
			pagination: false,
			sortOrder: 'desc',
			sortName: 'flowIn',
			pageSize: displayCount,
			pageList: [5],
			columns:[[
	         {field:'name',title:'应用名称',sortable:false,width:'20%',align:'center'},
	         {field:'flowIn',title:'流入流量',sortable:true, align:'center',order:'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, null);
	         }},
	         {field:'flowOut',title:'流出流量',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, null);
	         }},
	         {field:'flowTotal',title:'总流量',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, null);
	         }},
//	         {field:'packetIn',title:'流入包数',sortable:true,align:'center',formatter:function(value, row, index) {
//	        	 if(value == null) {
//	        		 return 0;
//	        	 } else {
//	        		 return value;
//	        	 }
//	         }},
//	         {field:'packetOut',title:'流出包数',sortable:true,align:'center',formatter:function(value, row, index) {
//	        	 if(value == null) {
//	        		 return 0;
//	        	 } else {
//	        		 return value;
//	        	 }
//	         }},
//	         {field:'packetTotal',title:'总包数',sortable:true,align:'center',formatter:function(value, row, index) {
//	        	 if(value == null) {
//	        		 return 0;
//	        	 } else {
//	        		 return value;
//	        	 }
//	         }},
	         {field:'speedIn',title:'流入速率',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'speedOut',title:'流出速率',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'speedTotal',title:'总速率',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'flowPctge',title:'占比',sortable:true,align:'center',order:'desc',formatter:function(value,row,index) {
	        	 return pctgeFormatter(value);
	         }}
	     ]],
	     onClickRow: function(rowIndex, rowData) {
	    	 $('<div class="device-app-dlg"></div>').dialog({    
	    		 	title: '流量分析 - 应用 - ' + rowData.name,
	    		    width: 1200,
	    		    height: 580, 
	    		    top: '5%',
	    		    closed: false,    
	    		    cache: false,
	    		    position:'center',
	    		    draggable:true,
	    		    href: oc.resource.getUrl("resource/module/netflow/device-netflow/DeviceToAppDialog.html"),    
	    		    modal: true   
	    		});
	    	 $('.device-app-dlg').data('app_id',rowData.appId);
	    	 //传值：timeInterval, type, startTime, endTime
	    	 var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue')
	    	 var type = curTab.find('.device-detail-indecator').combobox('getValue');
	    	 var startTime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	    	 var endTime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
	    	 $('.device-app-dlg').data('startTime', startTime);
	    	 $('.device-app-dlg').data('endTime', endTime);
	    	 $('.device-app-dlg').data('timeInterval', timeInterval);
	    	 $('.device-app-dlg').data('type', type);
	         
	     },
	     onLoadSuccess: function(data) {
	    	 oc.util.ajax({
	    		 url:oc.resource.getUrl('netflow/device/app/getDeviceAppChartData.htm'),
	    		 data: data.paramBo,
	    		 failureMsg:'加载用户域数据失败！',
	    		 async:false,
	    		 success:function(data){
	    			 var ti = curTab.find('.device-detail-timeinterval').combobox('getValue');
	    			 var st = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	    			 var et = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');

	    			 var step = getStep(ti, data.data.timeLine);
	    			 genNetflowChart(curTab.find('.device-app-hc'), 660, 135, data.data.timeLine, data.data.bos, data.data.unit, data.data.yAxisName, step, ti, st, et);
	    		 }
	    	 });
	     }
	});
	
	changeIndicatorDetail(curTab.find('.device-app-datagrid'), type, 'app', 1, false);

})(jQuery);

function showTerminalGroupApps(){
	var rows = $(".terminal-app-datagrid-selector").datagrid("getRows");
	if(rows != null && 0!=rows &&''!= rows){
	 $('<div class="terminal-app-allapp"></div>').dialog({    
		 	title: "应用",
		    width:750,    
		    height: 400,    
		    closed: false,    
		    cache: false,
		    position:'center',
		    draggable:false,
		    href: oc.resource.getUrl("resource/module/netflow/terminal-netflow/terminal_app.html"),    
		    modal: true
		});
	}
}