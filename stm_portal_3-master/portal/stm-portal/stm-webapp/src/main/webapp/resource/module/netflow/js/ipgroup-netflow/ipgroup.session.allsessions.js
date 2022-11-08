(function($){

//	var timeInterval = $('#timeInterval').combobox('getValue');
//

	var displayCount = 20;

	var mainDiv = $('.ipgroup-detail-session-more-main');
	
	var timeInterval = $('.ipgroup-detail-timeinterval').combobox('getValue');
	var startTime = $('.ipgroup-detail-custom-starttime').datetimebox('getValue');
	var endTime = $('.ipgroup-detail-custom-endtime').datetimebox('getValue');
	var type = $('.ipgroup-detail-indecator').combobox('getValue');
	var rowCount = 20;
	
	//timeInterval init
	oc.ui.combobox({
		selector: mainDiv.find('.ipgroup-detail-session-more-timeinterval'),
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
		placeholder: null,
		onSelect: function(d) {
			
			if(d.id == 'custom') {
				mainDiv.find('.ipgroup-detail-session-more-custom-date').removeClass('hide');
				mainDiv.find('#ipgroup_detail_session_more_search').removeClass('hide');
			} else {
				mainDiv.find('.ipgroup-detail-session-more-custom-date').addClass('hide');
				mainDiv.find('#ipgroup_detail_session_more_search').addClass('hide');
				
				var startTime = mainDiv.find('.ipgroup-detail-session-more-custom-starttime').datetimebox('getValue');
				var endTime = mainDiv.find('.ipgroup-detail-session-more-custom-endtime').datetimebox('getValue');
				
				var data = {
					'startTime': startTime,
					'endTime': endTime,
					'onePageRows' : rowCount,
					"sort" : "flowIn",
					"order" : 'desc',
					"needPagination":false,
					"showpagination" : false,
					"recordCount" : rowCount,
					"querySize" : rowCount,
					"rowCount":rowCount,
					"ipGroupId":$('.netflow-main').data('ipGroupId'),
					'timePerid': d.id
				};
				
				ipgSessionAllDatagridDiv.datagrid('load', data);
			}			
		}
	});
	
	//type init
	oc.ui.combobox({
		selector : mainDiv.find('.ipgroup-detail-session-more-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		placeholder: null,
		selected: '1',
		onSelect : function(d) {
			changeIndicatorDetail(ipgSessionAllDatagridDiv, d.id, 'session', 1, true);
		}
	});
	
	//datetimebox init
	mainDiv.find('.ipgroup-detail-session-more-custom-starttime').datetimebox();
	mainDiv.find('.ipgroup-detail-session-more-custom-endtime').datetimebox();
	
	//search button init
	var ipGroupId = $('.netflow-main').data('ipGroupId');
	mainDiv.find('#ipgroup_detail_session_more_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var startTime = mainDiv.find('.ipgroup-detail-session-more-custom-starttime').datetimebox('getValue');
			var endTime = mainDiv.find('.ipgroup-detail-session-more-custom-endtime').datetimebox('getValue');
			var t = mainDiv.find('.ipgroup-detail-session-more-timeinterval').combobox('getValue');
			
			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
					'startTime': startTime,
					'endTime': endTime,
					'onePageRows' : rowCount,
					"sort" : "flowIn",
					"order" : 'desc',
					"needPagination":false,
					"showpagination" : false,
					"recordCount" : rowCount,
					"querySize" : rowCount,
					"rowCount":rowCount,
					"ipGroupId":$('.netflow-main').data('ipGroupId'),
					'timePerid': t
				};
				
				ipgSessionAllDatagridDiv.datagrid('load', data);
			}
		}
	});
	
	//export button
	mainDiv.find('#ipgroup_detail_session_more_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('.ipgroup-detail-session-more-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/ipgroup/session/exportSession.htm?type="+type);
		}
	});
	
	//init status
	mainDiv.find('.ipgroup-detail-session-more-custom-date').addClass('hide');
	mainDiv.find('#ipgroup_detail_session_more_search').addClass('hide');
	
	if(timeInterval) {
		mainDiv.find('.ipgroup-detail-session-more-timeinterval').combobox('setValue', timeInterval);
		
		if(timeInterval == 'custom') {
			mainDiv.find('.ipgroup-detail-session-more-custom-date').removeClass('hide');
			mainDiv.find('#ipgroup_detail_session_more_search').removeClass('hide');
			
			mainDiv.find('.ipgroup-detail-session-more-custom-starttime').datetimebox('setValue', startTime);
			mainDiv.find('.ipgroup-detail-session-more-custom-endtime').datetimebox('setValue', endTime);
		}
	}
	
	if(type) {
		mainDiv.find('.ipgroup-detail-session-more-indecator').combobox('setValue', type);
	}
	
	/**
	 * 初始化datagrid
	 */
	var ipgSessionAllDatagridDiv=$('.ipgroup-session-allsessions');
	datagridObj_netflow_session=oc.ui.datagrid({
			selector:ipgSessionAllDatagridDiv,
			url:oc.resource.getUrl('netflow/ipgroup/session/ipGroupSessionPageSelect.htm'),
			queryParams: {
				startTime: startTime,
				endTime: endTime,
				timePerid: timeInterval,
				ipGroupId: ipGroupId,
				rowCount: displayCount,
				needPagination: true,
				querySize: displayCount
			},
			pagination: true,
			sortOrder: 'desc',
			sortName: 'flowIn',
			pageSize: displayCount,
			pageList: [5, 10, 20, 30, 40, 50],
			columns:[[
		         {field:'srcIp',title:'源IP',sortable:false,align:'center',width:'8%'},
		         {field:'dstIp',title:'目的IP',sortable:false,align:'center',width:'8%'},
		         {field:'flowIn',title:'流入流量',sortable:true, align:'center',width:'9%',order: 'desc',formatter:function(value,row,index) {
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
		         {field:'flowOut',title:'流出流量',sortable:true,width:'9%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
		         {field:'flowTotal',title:'总流量',sortable:true,width:'10%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
		         {field:'speedTotal',title:'总速率',sortable:true,width:'8%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
	     }	    
	});
	
	changeIndicatorDetail(ipgSessionAllDatagridDiv, type, 'session', 1, true);

})(jQuery);