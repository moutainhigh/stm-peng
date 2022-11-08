var tabCounter = 0;
(function(){
	
	//生成tab标签页,默认选中第一个标签页
	var domId=oc.util.generateId();
	var tab=$('#interface_netflow_main_tabs').attr('id',domId).tabs({
		select: 0,
		onSelect: function(title, index) {
			if(index == 1) {
				tarDiv.datagrid('reload');
			}
		}
	});

	var curTab = tab.find('.interface-view-list-main');
	var datagridTab2;
	
	var interfaceViewForm = oc.ui.form({
		selector: curTab.find('.interface-list-param-form'),
		combobox:[{
			placeholder: null,
			selector: curTab.find('.interface-view-timeinterval'),
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
					curTab.find('#interface_view_custom_date').removeClass('hide')
				} else {
					curTab.find('#interface_view_custom_date').addClass('hide')
					var data = {
						'timePerid': d.id,
						'onePageRows': 20,
						'rowCount': 20,
						'needPagination': true
					};
					tarDiv.datagrid('load', data);
				}
			}
		}, {
			selector: curTab.find('#interface_view_indicator'),
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
				changeIfIndicator(tarDiv, d.id);
			}
		}],
		datetimebox:[{
			width: 145,
			selector: curTab.find('.interface-view-custom-date-starttime')
		}, {
			width: 145,
			selector: curTab.find('.interface-view-custom-date-endtime')
		}]
	});
	
	//默认不显示自定义时间选择控件
	curTab.find('#interface_view_custom_date').addClass('hide');
	
	var timeInterval = $('.interface-view-timeinterval').combobox('getValue');
	var rowCount = 20;

	var tarDiv = curTab.find('#interface_view_list_datagrid');
	datagridTab2=oc.ui.datagrid({
		queryForm: interfaceViewForm,
		selector: tarDiv,
		url : oc.resource.getUrl('netflow/if/ifListPageSelect.htm'),
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
			left: [interfaceViewForm.selector, {
				iconCls: 'icon-search',
				text:"查询",
				onClick: function(){

					var startTime = curTab.find('.interface-view-custom-date-starttime').datetimebox('getValue');
					var endTime = curTab.find('.interface-view-custom-date-endtime').datetimebox('getValue');
					var timeInterval = curTab.find('.interface-view-timeinterval').combobox('getValue');

					if(timeInterval == 'custom') {
						var checkMsg = checkCustomTime(startTime, endTime);
						if('' != checkMsg) {
							alert(checkMsg);
							return
						}
					}
					
					var search = tab.find('#if_b_search').val();
					var data = {
							'startTime': startTime,
							'endTime': endTime,
							'timePerid': timeInterval,
							'onePageRows': 20,
							'rowCount': 20,
							'needPagination': true
					};
					if(search && search != '') {
						data = $.extend(data, {'ifName': search});
						data = $.extend(data, {'name': search});
					}
					tarDiv.datagrid('load', data);
				}
			}],
			right: [{
				text:'导出',
				iconCls:'icon-word interface-view-export',
				onClick:function(){
					top.location =  oc.resource.getUrl('netflow/if/exportAll.htm?type='+$("#interface_view_indicator").combobox("getValue"));
				}
			}]
		},
		sortOrder: 'desc',
		sortName: 'flowIn',
		columns : [[
			{
				field : 'ifName',
				title : '接口名称',
				sortable : false,
				width : 20,
				align : 'left'
			},
			{
				field : 'name',
				title : '设备名称',
				sortable : false,
				width : 20,
				align : 'left'
			},
			{
				field : 'flowIn',
				title : '流入流量',
				sortable : true,
				width : 20,
				align : 'center',
				order: 'desc',
				formatter : function(value, row, index) {
		        	return flowFormatter(value, null);
				}
			},
			{
				field : 'flowOut',
				title : '流出流量',
				sortable : true,
				width : 20,
				align : 'center',
				order: 'desc',
				formatter : function(value, row, index) {
					return flowFormatter(value, null);
				}
			},
			{
				field : 'flowTotal',
				title : '总流量',
				sortable : true,
				width : 20,
				align : 'center',
				order: 'desc',
				formatter : function(value, row, index) {
					return flowFormatter(value, null);
				}
			},
			{
				field : 'speedIn',
				title : '流入速率',
				sortable : true,
				width : 20,
				align : 'center',
				order: 'desc',
				formatter : function(value, row, index) {
					return flowFormatter(value, 'speed');
				}
			},
			{
				field : 'speedOut',
				title : '流出速率',
				sortable : true,
				width : 20,
				align : 'center',
				order: 'desc',
				formatter : function(value, row, index) {
					return flowFormatter(value, 'speed');
				}
			},
			{
				field : 'speedTotal',
				title : '总速率',
				sortable : true,
				width : 20,
				align : 'center',
				order: 'desc',
				formatter : function(value, row, index) {
					return flowFormatter(value, 'speed');
				}
			},
			{
				field : 'flowPctge',
				title : '占比',
				sortable : true,
				width : 20,
				align : 'center',
				order: 'desc',
				formatter : function(value, row, index) {
					return pctgeFormatter(value);
				}
		 }]],
	     onClickRow: function(rowIndex, rowData) {
	    	 $('.netflow-main').data('ifId', rowData.ifId);
	    	 var interfaceName = rowData.ifName;
	    	 var tab = $('.interface-netflow-main-tabs').tabs();
	    	 tab.tabs('add', {
	    		 title: interfaceName.substring(0, 10),
	    		 selected: true,
	    		 closable: true,
	    		 href: 'module/netflow/interface-netflow/interface_detail.html'
	    	 });
	    	 
	    	 var timeInterval = curTab.find('.interface-view-timeinterval').combobox('getValue');
	    	 var type = curTab.find('#interface_view_indicator').combobox('getValue');
	    	 var startTime = curTab.find('.interface-view-custom-date-starttime').datetimebox('getValue');
	    	 var endTime = curTab.find('.interface-view-custom-date-endtime').datetimebox('getValue');
	    	 
	    	 $('.netflow-main').data('timeInterval', timeInterval);
	    	 $('.netflow-main').data('type', type);
	    	 $('.netflow-main').data('startTime', startTime);
	    	 $('.netflow-main').data('endTime', endTime);
	    	 
	     },
	     onLoadSuccess: function(data) {
	     }
	});
	
//	changeIfIndicator(tarDiv, 1);

})();