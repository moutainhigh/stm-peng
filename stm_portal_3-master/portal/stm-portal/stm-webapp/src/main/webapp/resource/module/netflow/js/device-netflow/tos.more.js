var datagridObj_netflow_tos_more;
(function($){
//	var timeInterval = $('.device-detail-timeinterval').combobox('getValue');
//	var type = $('.device-detail-indecator').combobox('getValue');
	var timeInterval = $('.device-tos-more-wrapper').data('timeInterval');
	var type = $('.device-tos-more-wrapper').data('type');
	var displayCount = 20;
	var startTime = $('.device-tos-more-wrapper').data('starttime');
	var endTime = $('.device-tos-more-wrapper').data('endtime');

	/**
	 * 初始化datagrid
	 */
	var datagridDiv=$('#oc_module_netflow_device_tos_more_datagrid');
	var	deviceIp = $('.netflow-main').data('deviceIp');
	datagridObj_netflow_tos_more=oc.ui.datagrid({
			selector:datagridDiv,
			url: oc.resource.getUrl('netflow/device/tos/deviceTosNetflowPageSelect.htm'),
			queryParams: {
				startTime: startTime,
    			endTime: endTime,
				timePerid: timeInterval,
				deviceIp: deviceIp,
				rowCount: 20,
				needPagination: true,
				querySize: 20
			},
			fitColumns: true,
			pagination: true,
			sortOrder: 'desc',
			sortName: 'flowIn',
			pageSize: 20,
			pageList: [5, 10, 20, 30, 40, 50],
			columns:[[
	         {field:'name',title:'协议名称',sortable:false,width:'10%',align:'center'},
	         {field:'flowIn',title:'流入流量',sortable:true,width:'10%', align:'center',order:'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'flowOut',title:'流出流量',sortable:true,width:'10%',align:'center',order:'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'flowTotal',title:'总流量',sortable:true,width:'10%',align:'center',order:'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var mb = 1024*1024;
	        		 var gb = 1024*mb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'GB';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'MB';
	        		 } else {
	        			 return (value/1024).toFixed(2) + "KB";
	        		 }
	        	 } else {
	        		 return '0KB';
	        	 }
	         }},
	         {field:'packetIn',title:'流入包数',sortable:true,width:'8%',align:'center',order:'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'packetOut',title:'流出包数',sortable:true,width:'8%',align:'center',order:'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'packetTotal',title:'总包数',sortable:true,width:'9.5%',align:'center',order:'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'speedIn',title:'流入速率',sortable:true,width:'9%',align:'center',order:'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'speedOut',title:'流出速率',sortable:true,width:'10%',align:'center',order:'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'speedTotal',title:'总速率',sortable:true,width:'10%',align:'center',order:'desc',formatter:function(value,row,index) {
	        	 if(value != null) {
	        		 var kb = 1024;
	        		 var mb = kb*kb;
	        		 var gb = mb*kb;
	        		 if(value >= gb) {
	        			 var ret = value / gb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Gbps';
	        		 } else if(value >= mb) {
	        			 var ret = value / mb;
	        			 ret = ret.toFixed(2);
	        			 return ret + 'Mbps';
	        		 } else {
	        			 return (value/kb).toFixed(2) + 'Kbps';
	        		 }
	        	 } else {
	        		 return '0Kbps';
	        	 }
	         }},
	         {field:'flowPctge',title:'占比',sortable:true,width:'9%',align:'center',order:'desc',formatter:function(value,row,index) {
	        	 return (value == null ? 0 : (value*100).toFixed(2)) + '%';
	         }}
	     ]],
	     onLoadSuccess: function(data) {
	    	//不显示每页条数列表
			var p = datagridDiv.datagrid('getPager');
			$(p).pagination({
				showPageList: false
			});
	     }
	});
	
	initMoreTimeInterval('.device-detail-tos-more-timeinterval', '.device-detail-tos-more-custom-date', 
			'#device_detail_tos_more_search', '.device-detail-tos-more-indecator', datagridDiv);
	
	initMoreType('.device-detail-tos-more-indecator', datagridDiv, 'tos', 1, false);
	
	initExportBtn('#device_detail_tos_more_export', function() {
		var type = $('.device-detail-tos-more-indecator').combobox('getValue');
		top.location = oc.resource.getUrl("netflow/device/tos/exportTos.htm?type="+type);
	});
	
	initSearchBtn('#device_detail_tos_more_search', '.device-detail-tos-more-custom-starttime', 
			'.device-detail-tos-more-custom-endtime', function() {
		var timeInterval = $('.device-detail-tos-more-timeinterval').combobox('getValue');
		var startTime = $('.device-detail-tos-more-custom-starttime').datetimebox('getValue');
		var endTime = $('.device-detail-tos-more-custom-endtime').datetimebox('getValue');
		var checkMsg = checkCustomTime(startTime, endTime);
		if('' != checkMsg) {
			alert(checkMsg);
		} else {
			datagridDiv.datagrid('load', {
				startTime: startTime,
				endTime: endTime,
				timePerid: timeInterval,
				deviceIp: deviceIp,
				rowCount: 20,
				needPagination: true,
				querySize: 20
			});
		}
	});
	
	initCustomDateStatus('.device-detail-tos-more-indecator', '.device-detail-tos-more-custom-starttime', '.device-detail-tos-more-custom-endtime', 
			'.device-detail-tos-more-custom-date', '#device_detail_tos_more_search', '.device-detail-tos-more-timeinterval', 
			'.device-tos-more-wrapper', '.device-detail-timeinterval', '.device-detail-indecator');

	changeIndicatorDetail(datagridDiv, type, 'tos', 1, false);

})(jQuery);