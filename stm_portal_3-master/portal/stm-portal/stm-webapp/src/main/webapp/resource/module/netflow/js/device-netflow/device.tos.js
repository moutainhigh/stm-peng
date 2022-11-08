(function($){
	var curTab = $('.device-netflow-main-tabs').tabs('getSelected');
	
	var displayCount = 5;
	var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
	var type = curTab.find('.device-detail-indecator').combobox('getValue');
	var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
	var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
	
	var tosDatagridDiv=curTab.find('.device-tos-datagrid');
	var deviceIp = $('.netflow-main').data('deviceIp');

	datagridObj_netflow_tos =oc.ui.datagrid({
			selector:tosDatagridDiv,
			url:oc.resource.getUrl('netflow/device/tos/deviceTosNetflowPageSelect.htm'),
			queryParams: {
				timePerid: timeInterval,
				deviceIp: deviceIp,
				rowCount: displayCount,
				needPagination: false,
				querySize: displayCount,
				startTime: stime,
				endTime: etime
			},
			pagination: false,
			sortOrder: 'desc',
			sortName: 'flowIn',
			pageSize: displayCount,
			pageList: [5, 10, 20, 30, 40, 50],
			columns:[[
	         {field:'name',title:'TOS',sortable:false,width:'20%',align:'center'},
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
	     onLoadSuccess: function(data) {
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/device/tos/getDeviceTosChartData.htm'),
    			data:data.paramBo,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){

    				var ti = curTab.find('.device-detail-timeinterval').combobox('getValue');
    				var st = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
    				var et = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');

    				var step = getStep(ti, data.data.timeLine);
    				genNetflowChart(curTab.find('.device-tos-hc'), 660, 135, data.data.timeLine, data.data.bos, data.data.unit, data.data.yAxisName, step, ti, st, et);
    			}
    		});
	     }
	});

	changeIndicatorDetail(curTab.find('.device-tos-datagrid'), type, 'tos', 1, false);
	
})(jQuery);