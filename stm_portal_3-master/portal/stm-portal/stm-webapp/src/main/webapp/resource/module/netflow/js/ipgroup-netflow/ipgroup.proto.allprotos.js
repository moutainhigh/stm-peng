(function($){

//	var timeInterval = $('#timeInterval').combobox('getValue');
//	var displayCount = 20;

	var mainDiv = $('.ipgroup-detail-proto-more-main');
	
	var timeInterval = $('.ipgroup-detail-timeinterval').combobox('getValue');
	var startTime = $('.ipgroup-detail-custom-starttime').datetimebox('getValue');
	var endTime = $('.ipgroup-detail-custom-endtime').datetimebox('getValue');
	var type = $('.ipgroup-detail-indecator').combobox('getValue');
	var displayCount = rowCount = 20;

	//timeInterval init
	oc.ui.combobox({
		selector: mainDiv.find('.ipgroup-detail-proto-more-timeinterval'),
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
				mainDiv.find('.ipgroup-detail-proto-more-custom-date').removeClass('hide');
				mainDiv.find('#ipgroup_detail_proto_more_search').removeClass('hide');
			} else {
				mainDiv.find('.ipgroup-detail-proto-more-custom-date').addClass('hide');
				mainDiv.find('#ipgroup_detail_proto_more_search').addClass('hide');
				
				var startTime = mainDiv.find('.ipgroup-detail-proto-more-custom-starttime').datetimebox('getValue');
				var endTime = mainDiv.find('.ipgroup-detail-proto-more-custom-endtime').datetimebox('getValue');
				
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
				
				ipgProtoAllDatagridDiv.datagrid('load', data);
			}			
		}
	});
	
	//type init
	oc.ui.combobox({
		selector : mainDiv.find('.ipgroup-detail-proto-more-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		placeholder: null,
		selected: '1',
		onSelect : function(d) {
			changeIndicatorDetail(ipgProtoAllDatagridDiv, d.id, 'proto', 1, false);
		}
	});
	
	//datetimebox init
	mainDiv.find('.ipgroup-detail-proto-more-custom-starttime').datetimebox();
	mainDiv.find('.ipgroup-detail-proto-more-custom-endtime').datetimebox();
	
	//search button init
	mainDiv.find('#ipgroup_detail_proto_more_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var startTime = mainDiv.find('.ipgroup-detail-proto-more-custom-starttime').datetimebox('getValue');
			var endTime = mainDiv.find('.ipgroup-detail-proto-more-custom-endtime').datetimebox('getValue');
			var t = mainDiv.find('.ipgroup-detail-proto-more-timeinterval').combobox('getValue');
			
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
				
				ipgProtoAllDatagridDiv.datagrid('load', data);
			}
		}
	});
	
	//export button
	mainDiv.find('#ipgroup_detail_proto_more_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('.ipgroup-detail-proto-more-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/ipgroup/proto/exportProto.htm?type="+type);
		}
	});
	
	//init status
	mainDiv.find('.ipgroup-detail-proto-more-custom-date').addClass('hide');
	mainDiv.find('#ipgroup_detail_proto_more_search').addClass('hide');
	
	if(timeInterval) {
		mainDiv.find('.ipgroup-detail-proto-more-timeinterval').combobox('setValue', timeInterval);
		
		if(timeInterval == 'custom') {
			mainDiv.find('.ipgroup-detail-proto-more-custom-date').removeClass('hide');
			mainDiv.find('#ipgroup_detail_proto_more_search').removeClass('hide');
			
			mainDiv.find('.ipgroup-detail-proto-more-custom-starttime').datetimebox('setValue', startTime);
			mainDiv.find('.ipgroup-detail-proto-more-custom-endtime').datetimebox('setValue', endTime);
		}
	}
	
	if(type) {
		mainDiv.find('.ipgroup-detail-proto-more-indecator').combobox('setValue', type);
	}
	
	var ipGroupId = $('.netflow-main').data('ipGroupId');
	/**
	 * 初始化datagrid
	 */
	var ipgProtoAllDatagridDiv= mainDiv.find('.ipgroup-proto-allprotos'),
		
		datagridObj_netflow_proto=oc.ui.datagrid({
			selector: ipgProtoAllDatagridDiv,
			url: oc.resource.getUrl('netflow/ipgroup/proto/ipGroupProtoPageSelect.htm'),
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
		         {field:'name',title:'协议名称',sortable:false,width:'10%',align:'center'},
		         {field:'flowIn',title:'流入流量',sortable:true,width:'10%', align:'center',order: 'desc',formatter:function(value,row,index) {
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
		         {field:'flowOut',title:'流出流量',sortable:true,width:'10%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
		         {field:'packetTotal',title:'总包数',sortable:true,width:'9.5%',align:'center',order: 'desc',formatter:function(value, row, index) {
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
		         {field:'speedOut',title:'流出速率',sortable:true,width:'10%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
		         {field:'speedTotal',title:'总速率',sortable:true,width:'10%',align:'center',order: 'desc',formatter:function(value,row,index) {
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
		         {field:'flowPctge',title:'占比',sortable:true,width:'8%',align:'center',order: 'desc',formatter:function(value,row,index) {
		        	 return (value == null ? 0 : (value*100).toFixed(2)) + '%';
		         }}
	     ]],
	     onLoadSuccess: function(data) {
		}
	});
	
	changeIndicatorDetail(ipgProtoAllDatagridDiv, type, 'proto', 1, false);

})(jQuery);