var tabCounter = 0;
(function(){
	
	//生成tab标签页,默认选中第一个标签页
	var domId=oc.util.generateId();
	var tab=$('#device_netflow_main_tabs').attr('id',domId).tabs({
		select: 0,
		onSelect: function(title, index) {
			if(index == 1) {
				tarDiv.datagrid('reload');
			}
		}
	});

	var datagridTab2;
	//默认不显示自定义时间选择控件
	tab.find('#device_view_custom_date').addClass('hide')
	var deviceViewForm = oc.ui.form({
		selector: tab.find('.device-list-param-form'),
		combobox:[{
			placeholder: null,
			selector: tab.find('.device-view-timeinterval'),
			width: 110,
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
			selected: '1hour',
			onSelect: function(d) {
				if(d.id == 'custom') {
					tab.find('#device_view_custom_date').removeClass('hide')
				} else {
					tab.find('#device_view_custom_date').addClass('hide')
					
					var data = {
						'timePerid': d.id,
						'onePageRows': 20,
						'rowCount': 20
					};
					datagridTab2.load(data);
				}
			}
		}, {
			selector: tab.find('#device_view_indicator'),
			placeholder: null,
			width: 90,
			data:[
			    { id: 1, name: '流量'}, 
			    { id: 2, name: '包数' }, 
			    { id: 3, name: '连接数'}, 
			    { id: 4, name: '带宽使用率'}
			],
			selected: '1',
			onSelect: function(d) {
				changeIndicator(datagridTab2, d.id);
			}
		}],
		datetimebox:[{
			width: 145,
			selector: tab.find('.device-view-custom-date-startTime')
		}, {
			width: 145,
			selector: tab.find('.device-view-custom-date-endTime')
		}]
	});
	
	tab.find('.device-view-export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			top.location = oc.resource.getUrl("netflow/device/export.htm");
		}
	});
	
	var timeInterval = tab.find('.device-view-timeinterval').combobox('getValue');
	var rowCount = 20;

	var tarDiv = tab.find('#device_view_list_datagrid')
	datagridTab2=oc.ui.datagrid({
		queryForm: deviceViewForm,
		selector: tarDiv,
		url:oc.resource.getUrl('netflow/device/findManagedDeviceList.htm'),
		hideRest:true,
		hideSearch:true,
		queryParams: {
			timePerid: timeInterval,
			rowCount: rowCount,
			needPagination: true,
			querySize: rowCount
		},
		pageSize: rowCount,
		octoolbar:{
			left: [deviceViewForm.selector, {
				iconCls: 'icon-search',
				text: "查询",
				onClick: function(){
					var startTime = tab.find('.device-view-custom-date-startTime').datetimebox('getValue');
					var endTime = tab.find('.device-view-custom-date-endTime').datetimebox('getValue');
					var timeInterval = tab.find('.device-view-timeinterval').combobox('getValue');
					
					if(timeInterval == 'custom') {
						var checkMsg = checkCustomTime(startTime, endTime);
						if('' != checkMsg) {
							alert(checkMsg);
							return;
						}
					}

					var searchObj = tab.find('#device_b_search');
					var search = searchObj.val();
					var ip = undefined;
					var name = undefined;
					if(search && search != '') {
						if(isIp(search)) {
							ip = search;
						} else {
							name = search; 
						}
					}
					var data = {
							'startTime': startTime,
							'endTime': endTime,
							'timePerid': timeInterval,
							'onePageRows': 20,
							'rowCount': 20
					};
					if(ip) {
						data = $.extend(data, {'ipAddr': ip});
					} else if(name) {
						data = $.extend(data, {'name': name});
					}
					tarDiv.datagrid('load', data);
					
				}
			}],
			right: [{
				text:'导出',
				iconCls:'icon-word device-view-export',
				onClick:function(){
					top.location = oc.resource.getUrl("netflow/device/exportAll.htm?type="+tab.find('#device_view_indicator').combobox('getValue'));
				}
			}]
		},
		sortOrder: 'desc',
		sortName: 'flowIn',
		columns:[[
	         {
	        	 field:'name',title:'设备名称',sortable:false,width:30,align:'left',
	        	 formatter: function(value, row, index) {
	        		 return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
	        	 }
	         },
	         {
	        	 field:'ipAddr',title:'IP地址',sortable:false,width:20,align:'left'
	         },
	         {
	        	 field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
	        	 formatter:function(value,row,index) {
	        		 return flowFormatter(value, null);
	        	 }
	         },
	         {
	        	 field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
	        	 formatter:function(value,row,index) {
	        		 return flowFormatter(value, null);
	        	 }
	         },
	         {
	        	 field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
	        	 formatter:function(value,row,index) {
	        		 return flowFormatter(value, null);
	        	 }
	         },
	         {
	        	 field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
	        	 formatter:function(value,row,index) {
	        		 return flowFormatter(value, 'speed');
	        	 }
	         },
	         {
	        	 field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
	        	 formatter:function(value,row,index) {
	        		 return flowFormatter(value, 'speed');
	        	 }
	         },
	         {
	        	 field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
	        	 formatter:function(value,row,index) {
	        		 return flowFormatter(value, 'speed');
	        	 }
	         },
	         {
	        	 field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
	        	 formatter:function(value,row,index) {
	    		 	return pctgeFormatter(value);
	        	 }
	         }
	     ]],
	     onClickRow: function(rowIndex, rowData) {
	    	 $('.netflow-main').data('deviceIp', rowData.ip);
	    	 var deviceName = rowData.name;
	    	 var tab = $('.device-netflow-main-tabs').tabs();
	    	 tab.tabs('add', {
	    		 title: deviceName.substring(0, 10),
	    		 selected: true,
	    		 closable: true,
	    		 href: 'module/netflow/device-netflow/device_detail.html'
	    	 });
	    	 
	    	 var startTime = tab.find('.device-view-custom-date-startTime').datetimebox('getValue');
	    	 var endTime = tab.find('.device-view-custom-date-endTime').datetimebox('getValue');
	    	 var timeInterval = tab.find('.device-view-timeinterval').combobox('getValue');
	    	 var type = tab.find('#device_view_indicator').combobox('getValue');

	    	 $('.netflow-main').data('timeInterval', timeInterval);
	    	 $('.netflow-main').data('startTime', stime);
	    	 $('.netflow-main').data('endTime', etime);
	    	 $('.netflow-main').data('type', type);
	    	 
	     },
	     onLoadSuccess: function(data) {
	     }
	});

})();

/**
 * 1.流量-列
 * @returns {Array}
 */
function getFlowColumns() {
	var flowColumns = [[
         {
        	 field:'name',title:'设备名称',sortable:false,width:30,align:'left',
        	 formatter: function(value, row, index) {
        		 return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
        	 }
         },
         {
        	 field:'ipAddr',title:'IP地址',sortable:false,width:20,align:'left'
         },
         {
        	 field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order: 'desc',
        	 formatter:function(value,row,index) {
        		 return flowFormatter(value, null);
        	 }
         },
         {
        	 field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order: 'desc',
        	 formatter:function(value,row,index) {
        		 return flowFormatter(value, null);
        	 }
         },
         {
        	 field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order: 'desc',
        	 formatter:function(value,row,index) {
        		 return flowFormatter(value, null);
        	 }
         },
         {
        	 field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order: 'desc',
        	 formatter:function(value,row,index) {
        		 return flowFormatter(value, 'speed');
        	 }
         },
         {
        	 field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order: 'desc',
        	 formatter:function(value,row,index) {
        		 return flowFormatter(value, 'speed');
        	 }
         },
         {
        	 field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order: 'desc',
        	 formatter:function(value,row,index) {
        		 return flowFormatter(value, 'speed');
        	 }
         },
         {
        	 field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
        	 formatter:function(value,row,index) {
    		 	return pctgeFormatter(value);
        	 }
         }
	]];
	return flowColumns;
}

/**
 * 2.包-列
 * @returns {Array}
 */
function getPacketColumns() {
	var packetColumns = [[
         {
        	 field:'name',title:'设备名称',sortable:false,width:30,align:'left',
        	 formatter: function(value, row, index) {
        		 return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
        	 }
         },
         {
        	 field:'ipAddr',title:'IP地址',sortable:false,width:20,align:'left'
         },
         {
        	 field:'packetIn',title:'流入包数',sortable:true,width:20,align:'left',order: 'desc',
        	 formatter:function(value, row, index) {
        		 return packetFormatter(value, null);
        	 }
         },
         {
        	 field:'packetOut',title:'流出包数',sortable:true,width:20,align:'left',order: 'desc',
        	 formatter:function(value, row, index) {
        		 return packetFormatter(value, null);
        	 }
         },
         {
        	 field:'packetTotal',title:'总包数',sortable:true,width:20,align:'left',order: 'desc',
        	 formatter:function(value, row, index) {
        		 return packetFormatter(value, null);
        	 }
         },
         {
        	 field: 'packetInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
        	 formatter: function(value, row, index) {
        		 return packetFormatter(value, 'speed');
        	 }
         },
         {
        	 field: 'packetOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
        	 formatter: function(value, row, index) {
        		 return packetFormatter(value, 'speed');
        	 }
         },
         {
        	 field: 'packetTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
        	 formatter: function(value, row, index) {
        		 return packetFormatter(value, 'speed');
        	 }
         },
         {
        	 field:'packetPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
        	 formatter:function(value,row,index) {
    		 	return pctgeFormatter(value);
        	 }
         }
	]];
	return packetColumns;
}

/**
 * 3.连接数-列
 * @returns {Array}
 */
function getConnectColumns() {
	var connectColumns = [[
		{
			 field:'name',title:'设备名称',sortable:false,width:30,align:'left',
			 formatter: function(value, row, index) {
        		 return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
        	 }
		},
		{
			 field:'ipAddr',title:'IP地址',sortable:false,width:20,align:'left'
		},
		{
	       	 field: 'connectNumberIn', title: '流入连接数', sortable: true, width: 20, align: 'left',order: 'desc',
	       	 formatter: function(value, row, index) {
	       		 return connectFormatter(value, null);
	       	 }
        },
        {
	       	 field: 'connectNumberOut', title: '流出连接数', sortable: true, width: 20, align: 'left',order: 'desc',
	       	 formatter: function(value, row, index) {
	       		 return connectFormatter(value, null);
	       	 }
        },
        {
	       	 field: 'connectNumberTotal', title: '总连接数', sortable: true, width: 20, align: 'left',order: 'desc',
	       	 formatter: function(value, row, index) {
	       		 return connectFormatter(value, null);
	       	 }
        },
        {
	       	 field: 'connectNumberInSpeed', title: '流入速率', sortable: true, width: 20, align: 'left',order: 'desc',
	       	 formatter: function(value, row, index) {
	       		 return connectFormatter(value, 'speed');
	       	 }
        },
        {
	       	 field: 'connectNumberOutSpeed', title: '流出速率', sortable: true, width: 20, align: 'left',order: 'desc',
	       	 formatter: function(value, row, index) {
	       		 return connectFormatter(value, 'speed');
	       	 }
        },
        {
	       	 field: 'connectNumberTotalSpeed', title: '总速率', sortable: true, width: 20, align: 'left',order: 'desc',
	       	 formatter: function(value, row, index) {
	       		 return connectFormatter(value, 'speed');
	       	 }
        },
        {
	       	 field:'connectPctge',title:'占比',sortable:true,width:15,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	   		 	return pctgeFormatter(value);
	       	 }
        }
	]];
	return connectColumns;
}

/**
 * 4.带宽使用率-列
 * @returns {Array}
 */
function getBwColumns() {
	var bwColumns = [[
		{
			 field:'name',title:'设备名称',sortable:false,width:30,align:'left',
			 formatter: function(value, row, index) {
        		 return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
        	 }
		},
		{
			 field:'ipAddr',title:'IP地址',sortable:false,width:20,align:'left'
		},
		{
	       	 field:'flowInBwUsage',title:'流入带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'flowOutBwUsage',title:'流出带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        },
        {
	       	 field:'bwUsage',title:'带宽使用率',sortable:true,width:20,align:'left',order: 'desc',
	       	 formatter:function(value,row,index) {
	       		 return pctgeFormatter(value);
	       	 }
        }
	]];
	return bwColumns;
}

/**
 * 指标类型改变加载对应的列
 * @param type
 */
function changeIndicator(datagrid, type, ret, name) {
	if(1 == type) {
		datagrid.selector.datagrid({
			columns: getFlowColumns()
		});
	} else if(2 == type) {
		datagrid.selector.datagrid({
			columns: getPacketColumns()
		});
	} else if(3 == type) {
		datagrid.selector.datagrid({
			columns: getConnectColumns()
		});
	} else if(4 == type) {
		datagrid.selector.datagrid({
			columns: getBwColumns()
		});
	}
}