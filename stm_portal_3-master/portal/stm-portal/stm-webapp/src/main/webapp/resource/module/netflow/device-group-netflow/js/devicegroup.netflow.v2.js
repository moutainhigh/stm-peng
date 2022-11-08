var tabCounter = 0;
(function(){
	
	//生成tab标签页,默认选中第一个标签页
	var domId=oc.util.generateId();
	var tab=$('#device_netflow_main_tabs').attr('id',domId).tabs({
		select: 0,
		onSelect: function(title, index) {
			if(index == 1) {
				datagridDiv.datagrid('reload');
			}
		}
	});

	var mainDiv = tab.find('.devicegrp-view-list-main');
	var datagridTab2;
	var deviceViewForm = oc.ui.form({
		selector: mainDiv.find('.device-list-param-form'),
		combobox:[{
			placeholder: null,
			selector: mainDiv.find('.device-view-timeinterval'),
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
					mainDiv.find('#device_view_custom_date').removeClass('hide')
				} else {
					mainDiv.find('#device_view_custom_date').addClass('hide')
					
					var data = {
						'timePerid': d.id,
						'onePageRows': 20,
						'rowCount': 20
					};
					datagridDiv.datagrid('load', data);
				}
			}
		}, {
			selector: mainDiv.find('#device_view_indicator'),
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
				changeDeviceGroupIndicator(datagridDiv, d.id);
			}
		}],
		datetimebox:[{
			width: 145,
			selector: mainDiv.find('.device-view-custom-date-starttime')
		}, {
			width: 145,
			selector: mainDiv.find('.device-view-custom-date-endtime')
		}]
	});
	
	//默认不显示自定义时间选择控件
	mainDiv.find('#device_view_custom_date').addClass('hide')
	
	var timeInterval = mainDiv.find('.device-view-timeinterval').combobox('getValue');
	var startTime = mainDiv.find('.device-view-custom-date-starttime').datetimebox('getValue');
	var endTime = mainDiv.find('.device-view-custom-date-endtime').datetimebox('getValue');
	var rowCount = 20;

	var datagridDiv = $('#device_view_list_datagrid');
	datagridTab2=oc.ui.datagrid({
		queryForm: deviceViewForm,
		selector: datagridDiv,
		url: oc.resource.getUrl('netflow/deviceGroupNetflow/findManagedDeviceList.htm'),
		hideRest:true,
		hideSearch:true,
		queryParams: {
			startTime: startTime,
			endTime: endTime,
			timePerid: timeInterval,
			rowCount: rowCount,
			needPagination: true,
			querySize: rowCount
		},
		pageSize: rowCount,
		octoolbar:{
			left: [deviceViewForm.selector, {
				iconCls: 'icon-search',
				text:"查询",
				onClick: function(){

					var st = mainDiv.find('.device-view-custom-date-starttime').datetimebox('getValue');
					var et = mainDiv.find('.device-view-custom-date-endtime').datetimebox('getValue');
					var t = mainDiv.find('.device-view-timeinterval').combobox('getValue');
					
					if(t == 'custom') {
						var checkMsg = checkCustomTime(st, et);
						if('' != checkMsg) {
							alert(checkMsg);
							return;
						}
					} 
					
					var search = mainDiv.find('#devicegrp_b_search').val();
					var deviceGrpName = undefined;
					if(search && search != '') {
						deviceGrpName = search;
					}
					var data = {
							'startTime': st,
							'endTime': et,
							'timePerid': t,
							'onePageRows': rowCount,
							'rowCount': rowCount,
							'needPagination': true,
							'querySize': rowCount	
					};
					if(deviceGrpName) {
						data = $.extend(data, {'name': deviceGrpName});
					}
					datagridDiv.datagrid('load', data);
				}
			}],
			right: [{
				text:'导出',
				iconCls:'icon-word device-view-export',
				onClick:function(){
					var type = $('#device_view_indicator').combobox('getValue');
					top.location = oc.resource.getUrl("netflow/device/exportAll.htm?type="+type);
				}
			}]
		},
		sortOrder: 'desc',
		sortName: 'flowIn',
		columns:[[
	         {
	        	 field:'name',title:'设备组名称',sortable:false,width:30,align:'left',
	        	 formatter: function(value, row, index) {
	        		 return '<span title="'+(value || '')+'">'+(value || '')+'</span>';
	        	 }
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
	    	 oc.util.ajax({
    			url:oc.resource.getUrl('netflow/deviceGroupNetflow/getDeviceIdsByGroupId.htm'),
    			data: {
    				deviceGroupId: rowData.id
    			},
    			successMsg:null,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				var ids = data.data;
    				if(ids && ids != null && ids != '' && data.code == 200) {
    					$('.netflow-main').data('deviceIp', ids);
    					var deviceGroupName = rowData.name;
    					var tab = $('.device-netflow-main-tabs').tabs();
    					tab.tabs('add', {
    						title: deviceGroupName.substring(0, 10),
    						selected: true,
    						closable: true,
    						href: 'module/netflow/device-netflow/device_detail.html'
    					});
    					
    					var timeInterval = mainDiv.find('.device-view-timeinterval').combobox('getValue');
    					var startTime = mainDiv.find('.device-view-custom-date-starttime').datetimebox('getValue');
    					var endTime = mainDiv.find('.device-view-custom-date-endtime').datetimebox('getValue');
    					var type = mainDiv.find('#device_view_indicator').combobox('getValue');
    					
    					$('.netflow-main').data('timeInterval', timeInterval);
    					$('.netflow-main').data('startTime', startTime);
    					$('.netflow-main').data('endTime', endTime);
    					$('.netflow-main').data('type', type);

    				}
    			}
    		});
	     },
	     onLoadSuccess: function(data) {
	     }
	});

})();