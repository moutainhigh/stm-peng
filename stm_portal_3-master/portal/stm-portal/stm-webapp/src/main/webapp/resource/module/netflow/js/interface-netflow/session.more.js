(function($){

//	var timeInterval = $('#timeInterval').combobox('getValue'),
//		timeObj = parseTime(timeInterval),
//		startTime=timeObj.startTime, 
//		endTime=timeObj.endTime;
	
	var startTime = $('.interface-session-more-wrapper').data('starttime');
	var endTime = $('.interface-session-more-wrapper').data('endtime');

	var timeInterval = $('.interface-session-more-wrapper').data('timeInterval');
	var type = $('.interface-session-more-wrapper').data('type');

	/**
	 * 初始化datagrid
	 */
	var datagridDiv=$('#oc_module_netflow_interface_session_more_datagrid'),
		ifId = $('.netflow-main').data('ifId');
	oc.ui.datagrid({
			selector:datagridDiv,
			url:oc.resource.getUrl('netflow/if/session/ifSessionPageSelect.htm'),
			queryParams: {
				startTime: startTime,
				endTime: endTime,
				timePerid: timeInterval,
				ifId: ifId,
				rowCount: 20,
				needPagination: true,
				querySize: 20
			},
			pagination: true,
			sortOrder: 'desc',
			sortName: 'flowIn',
			pageSize: 20,
			pageList: [5, 10, 20, 30, 40, 50],
			columns:[[
	         {field:'srcIp',title:'源IP',sortable:false,align:'center',width:'8%'},
	         {field:'dstIp',title:'目的IP',sortable:false,align:'center',width:'8%'},
	         {field:'flowIn',title:'流入流量',sortable:true, align:'center',width:'8%',order: 'desc',formatter:function(value,row,index) {
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
	         {field:'flowOut',title:'流出流量',sortable:true,width:'8%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
	         {field:'flowTotal',title:'总流量',sortable:true,width:'9%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
	         {field:'packetIn',title:'流入包数',sortable:true,width:'8%',align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'packetOut',title:'流出包数',sortable:true,width:'8%',align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'packetTotal',title:'总包数',sortable:true,width:'9%',align:'center',order: 'desc',formatter:function(value, row, index) {
	        	 if(value == null) {
	        		 return 0;
	        	 } else {
	        		 return value;
	        	 }
	         }},
	         {field:'speedIn',title:'流入速率',sortable:true,width:'9%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
	         {field:'speedOut',title:'流出速率',sortable:true,width:'9%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
	         {field:'speedTotal',title:'总速率',sortable:true,width:'9%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
	         {field:'flowPctge',title:'占比',sortable:true,width:'7%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
	
	initMoreTimeInterval('.interface-detail-session-more-timeinterval', '.interface-detail-session-more-custom-date', 
			'#interface_detail_session_more_search', '.interface-detail-session-more-indecator', datagridDiv, true);
	
	initMoreType('.interface-detail-session-more-indecator', datagridDiv, 'session', 1, true);
	
	initExportBtn('#interface_detail_session_more_export', function() {
		var type = $('.interface-detail-session-more-indecator').combobox('getValue');
		top.location = oc.resource.getUrl("netflow/if/session/interfaceSession.htm?type="+type);
	});
	
	initSearchBtn('#interface_detail_session_more_search', '.interface-detail-session-more-custom-starttime', 
			'.interface-detail-session-more-custom-endtime', function() {
		var t = $('.interface-detail-session-more-timeinterval').combobox('getValue');
		var st = $('.interface-detail-session-more-custom-starttime').datetimebox('getValue');
		var et = $('.interface-detail-session-more-custom-endtime').datetimebox('getValue');
		
		var checkMsg = checkCustomTime(st, et);
		if('' != checkMsg) {
			alert(checkMsg);
		} else {
			datagridDiv.datagrid('load', {
				startTime: st,
				endTime: et,
				timePerid: t,
				ifId: ifId,
				rowCount: 20,
				needPagination: true,
				querySize: 20
			});
		}
	});
	
	initCustomDateStatus('.interface-detail-session-more-indecator', '.interface-detail-session-more-custom-starttime', '.interface-detail-session-more-custom-endtime', 
			'.interface-detail-session-more-custom-date', '#interface_detail_session_more_search', '.interface-detail-session-more-timeinterval', 
			'.interface-session-more-wrsessioner', '.interface-detail-timeinterval', '.interface-detail-indecator');

	changeIndicatorDetail(datagridDiv, type, 'session', 1, true);

})(jQuery);