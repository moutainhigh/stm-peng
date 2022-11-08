(function($) {

	var mainDiv = $('#oc_module_netflow_interface_main').attr("id", oc.util.generateId()).panel({
		fit : true,
		isOcAutoWidth : true
	});
	
	var interfaceNetflowDatagrid;

	oc.ui.combobox({
		width: 110,
		placeholder: null,
		selector : mainDiv.find('.interface-timeinterval'),
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
		}],
		selected : '1hour',
		onSelect : function(d) {
			
			if(d.id == 'custom') {
				mainDiv.find('.interface-custom-time-tab1').removeClass('hide');
				mainDiv.find('.interface-search-tab1').removeClass('hide');
			} else {
				mainDiv.find('.interface-custom-time-tab1').addClass('hide');
				mainDiv.find('.interface-search-tab1').addClass('hide');

				var rowCount = mainDiv.find('.interface-displaycount').combobox('getValue');
				var timeInterval = mainDiv.find('.interface-timeinterval').combobox('getValue');
				
				datagridDiv.datagrid('load', {
					'timePerid': d.id,
					'rowCount' : rowCount,
					'onePageRows' : rowCount,
					"needPagination" : true
				});
			}
		}
	});
	
	oc.ui.combobox({
		width: 110,
		placeholder: null,
		selector: mainDiv.find('#interface_indicator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}, 
		    { id: 4, name: '带宽使用率'}
		],
		selected: '1',
		onSelect: function(d) {
			changeIfIndicator(datagridDiv, d.id);
		}
	});

	oc.ui.combobox({
		width: 70,
		placeholder: null,
		selector : mainDiv.find('.interface-displaycount'),
		data : [ {
			id : '5',
			name : '5'
		}, {
			id : '10',
			name : '10'
		}],
		selected : '5',
		onSelect : function(d) {
			var t = mainDiv.find('.interface-timeinterval').combobox('getValue');
			var startTime = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
			var rowCount = d.id;
			datagridDiv.datagrid('getPager').data("pagination").options.pageSize = rowCount;
			if(t == 'custom') {
				var checkMsg = checkCustomTime(startTime, endTime);
				if('' != checkMsg) {
					alert(checkMsg);
					return;
				}
			}
			datagridDiv.datagrid('load', {
				'startTime' : startTime,
				'endTime' : endTime,
				'timePerid': t,
				'rowCount' : rowCount,
				'onePageRows' : rowCount,
				"needPagination" : true
			});
		}
	});
	
	mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox();
	mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox();
	
	mainDiv.find('.interface-search-tab1').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var t = mainDiv.find('.interface-timeinterval').combobox('getValue');
			var rowCount = mainDiv.find('.interface-displaycount').combobox('getValue');

			var startTime = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				mainDiv.find('#oc_module_netflow_interface_main_datagrid').datagrid('load', {
					'startTime' : startTime,
					'endTime' : endTime,
					'timePerid': t,
					'rowCount' : rowCount,
					'onePageRows' : rowCount,
					"needPagination" : true
				});
			}
		}
	});

	mainDiv.find('#interface_export_tab1').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			top.location = oc.resource.getUrl("netflow/if/export.htm?type="+mainDiv.find('#interface_indicator').combobox('getValue'));
		}
	});
	
	//默认隐藏自定义事件选择控件,查询按钮
	mainDiv.find('.interface-custom-time-tab1').addClass('hide');
	mainDiv.find('.interface-search-tab1').addClass('hide');

	var timeInterval = mainDiv.find('.interface-timeinterval').combobox('getValue');
	var startTime = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
	var endTime = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
	var displayCount = mainDiv.find('.interface-displaycount').combobox('getValue');

	/**
	 * 初始化datagrid
	 */
	var datagridDiv = mainDiv.find('#oc_module_netflow_interface_main_datagrid');
	interfaceNetflowDatagrid = oc.ui.datagrid({
		selector : datagridDiv,
		url : oc.resource.getUrl('netflow/if/ifListPageSelect.htm'),
		queryParams : {
			needPagination : true,
//			startTime : startTime,
//			endTime : endTime,
			timePerid: timeInterval,
			rowCount : displayCount
		},
		pagination : true,
		pageSize : displayCount,
		pageList : [5, 10],
		sortOrder : 'desc',
		sortName : 'flowIn',
		width : 'auto',
		height : 'auto',
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
			}
		]],
		onLoadSuccess : function(data) {
			// 不显示每页条数列表
			var p = datagridDiv.datagrid('getPager');
			$(p).pagination({
				showPageList : false
			});
			oc.util.ajax({
				url : oc.resource.getUrl('netflow/if/getIfNetflowChartData.htm'),
				data : data.paramBo,
				successMsg : null,
				failureMsg : '加载用户域数据失败！',
				async : false,
				success : function(data) {
					var ti = mainDiv.find('.interface-timeinterval').combobox('getValue');
					var st = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
					var et = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');

					var step = getStep(ti, data.data.timeLine);
					genMainPageChart(mainDiv.find('#oc_module_netflow_interface_chart'), data.data.timeLine, data.data.bos, data.data.unit, data.data.yAxisName, step, ti, st, et);
					//首页饼图
					var dst = dataTransfer(data.data.bos);
					genMainPagePieChart(mainDiv.find('#oc_module_netflow_interface_pie_chart'), dst);
				}
			});
		},
		onClickRow : function(rowIndex, rowData) {
//			mainDiv.data('ifId', rowData.ifId);
			$('.netflow-main').data('ifId', rowData.ifId);
			var ifName = rowData.ifName;	
			var tab = $('.interface-netflow-main-tabs').tabs();
			tab.tabs('add', {
				title: ifName.substring(0, 10),
				selected: true,
				closable: true,
				href: 'module/netflow/interface-netflow/interface_detail.html'
			});

			var timeInterval = mainDiv.find('.interface-timeinterval').combobox('getValue');
			var startTime = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
			var type = mainDiv.find('#interface_indicator').datetimebox('getValue');

			$('.netflow-main').data('timeInterval', timeInterval);
			$('.netflow-main').data('startTime', startTime);
			$('.netflow-main').data('endTime', endTime);
			$('.netflow-main').data('type', type);
			
		}
	});
	
//	changeIfIndicator(datagridDiv, 1);

})(jQuery);