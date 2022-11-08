var datagridObj_netflow_app_more;
(function($){

//	var timeInterval = $('.device-detail-timeinterval').combobox('getValue');
//	var type = $('.device-detail-indecator').combobox('getValue');
	var timeInterval = $('.device-app-more-wrapper').data('timeInterval');
	var type = $('.device-app-more-wrapper').data('type')
	var displayCount = 20;
	var startTime = $('.device-app-more-wrapper').data('starttime');
	var endTime = $('.device-app-more-wrapper').data('endtime');

	oc.ui.combobox({
		selector : '.device-detail-app-more-timeinterval',
		placeholder: null,
		data : [ {
			id : '1hour',
			name : '最近一小时'
		}, {
			id : '6hour',
			name : '最近六小时'
		}, {
			id : '1day',
			name : '最近一天'
		}, {
			id : '7day',
			name : '最近一周'
		}, {
			id : '30day',
			name : '最近一个月'
		}, {
			id: 'custom',
			name: '自定义'
		} ],
		selected : '1hour',
		onSelect : function(d) {
			
			if(d.id == 'custom') {
				$('.device-detail-app-more-custom-date').removeClass('hide');
				$('#device_detail_app_more_search').removeClass('hide');
				
			} else {
				$('.device-detail-app-more-custom-date').addClass('hide');
				$('#device_detail_app_more_search').addClass('hide');
				
				var data = {
						'startTime': startTime,
						'endTime': endTime,
						'onePageRows' : displayCount,
						"deviceIp" : $('.netflow-main').data('deviceIp'),
						"needPagination":true,
						"showpagination" : true,
						"recordCount" : displayCount,
						"querySize" : displayCount,
						"rowCount":displayCount,
						"recordCount":displayCount,
						'timePerid': d.id
				};
				datagridDiv.datagrid('load', data);
			}
			
		}
	});

	oc.ui.combobox({
		selector : '.device-detail-app-more-indecator',
		placeholder: null,
		data : [{
			id : 1,
			name : '流量'
		}, {
			id: 2,
			name: '包数'
		}, {
			id: 3,
			name: '连接数'
		}/*, {
			id: 4,
			name: '带宽使用率'
		}*/],
		selected: '1',
		onSelect : function(d) {
			changeIndicatorDetail(datagridDiv, d.id, 'app', 1, false);
		}
	});
	
	$('#device_detail_app_more_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = $('.device-detail-app-more-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/device/app/exportDevice.htm?type="+type);
		}
	});
	
	$('#device_detail_app_more_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var stime = $('.device-detail-app-more-custom-starttime').datetimebox('getValue');
			var etime = $('.device-detail-app-more-custom-endtime').datetimebox('getValue');
			var msg = checkCustomTime(stime, etime);
			if('' != msg) {
				alert(msg);
				return;
			} else {
				var ti = $('.device-detail-app-more-timeinterval').combobox('getValue');
				var data = {
					'startTime': stime,
					'endTime': etime,
					'onePageRows' : displayCount,
					"deviceIp" : $('.netflow-main').data('deviceIp'),
					"needPagination":true,
					"showpagination" : true,
					"recordCount" : displayCount,
					"querySize" : displayCount,
					"rowCount":displayCount,
					"recordCount":displayCount,
					'timePerid': ti
				};
				datagridDiv.datagrid('load', data);
			}
		}
	});
	
	if(type) {
		$('.device-detail-app-more-indecator').combobox('setValue', type);
	}
	
	$('.device-detail-app-more-custom-starttime').datetimebox();
	$('.device-detail-app-more-custom-endtime').datetimebox();

	$('.device-detail-app-more-custom-date').addClass('hide');
	$('#device_detail_app_more_search').addClass('hide');
	if(timeInterval) {
		$('.device-detail-app-more-timeinterval').combobox('setValue', timeInterval);

		if(timeInterval == 'custom') {
			$('.device-detail-app-more-custom-date').removeClass('hide');
			$('#device_detail_app_more_search').removeClass('hide');

			var sstime = $('.device-app-more-wrapper').data('starttime');
			var eetime = $('.device-app-more-wrapper').data('endtime');
			$('.device-detail-app-more-custom-starttime').datetimebox('setValue', sstime);
			$('.device-detail-app-more-custom-endtime').datetimebox('setValue', eetime);
		}
	}
	
	var timeInterval2 = $('.device-detail-app-more-timeinterval').combobox('getValue');

	/**
	 * 初始化datagrid
	 */
	var datagridDiv=$('#oc_module_netflow_device_app_more_datagrid');
	var	deviceIp = $('.netflow-main').data('deviceIp');
	datagridObj_netflow_app_more=oc.ui.datagrid({
			selector:datagridDiv,
			url: oc.resource.getUrl('netflow/device/app/deviceAppNetflowPageSelect.htm'),
			queryParams: {
				startTime: startTime,
				endTime: endTime,
				timePerid: timeInterval2,
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
	         {field:'name',title:'应用名称',sortable:false,width:'10%',align:'center'},
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
	
//	initMoreTimeInterval('.device-detail-app-more-timeinterval', '.device-detail-app-more-custom-date', 
//			'#device_detail_app_more_search', '.device-detail-app-more-indecator', datagridDiv);
//	
//	initMoreType('.device-detail-app-more-indecator', datagridDiv, 'app', 2, false);
//	
//	initExportBtn('#device_detail_app_more_export', function() {
//		
//	});
//	
//	initSearchBtn('#device_detail_app_more_search', '.device-detail-app-more-custom-starttime', 
//			'.device-detail-app-more-custom-endtime', function() {
//		var startTime = $('.device-detail-app-more-custom-starttime').datetimebox('getValue');
//		var endTime = $('.device-detail-app-more-custom-endtime').datetimebox('getValue');
//	});
//	
//	initCustomDateStatus('.device-detail-app-more-indecator', '.device-detail-app-more-custom-starttime', '.device-detail-app-more-custom-endtime', 
//			'.device-detail-app-more-custom-date', '#device_detail_app_more_search', '.device-detail-app-more-timeinterval', 
//			'.device-app-more-wrapper', '.device-detail-timeinterval', '.device-detail-indecator');

	
	changeIndicatorDetail(datagridDiv, type, 'app', 1, false);

})(jQuery);