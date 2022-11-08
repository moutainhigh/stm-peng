(function($){
	
	var mainDiv = $('.ifgroup-netflow-main-tabs').find('#oc_module_netflow_interface_main').attr("id", oc.util.generateId()).panel({
		fit: true,
		isOcAutoWidth: true
	});
	
	oc.ui.combobox({
		selector : '.interface-timeinterval',
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
		width: 110,
		onSelect : function(d) {
			if(d.id == 'custom') {
				mainDiv.find('.interface-custom-time-tab1').removeClass('hide');
				mainDiv.find('.interface-search-tab1').removeClass('hide');
			} else {
				mainDiv.find('.interface-custom-time-tab1').addClass('hide');
				mainDiv.find('.interface-search-tab1').addClass('hide');

				var rowCount = mainDiv.find('.interface-displaycount').combobox('getValue');
				var startTime = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
				var endTime = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
				
				datagridDiv.datagrid('load', {
					'startTime' : startTime,
					'endTime' : endTime,
					'timePerid': d.id,
					'rowCount' : rowCount,
					'onePageRows' : rowCount,
					'needPagination' : true
				});
			}
			
		}
	});

	oc.ui.combobox({
		selector : '.interface-displaycount',
		data : [ {
			id : '5',
			name : '5'
		}, {
			id : '10',
			name : '10'
		}],
		width: 90,
		placeholder: null,
		selected: '5',
		onSelect : function(d) {
			var t = mainDiv.find('.interface-timeinterval').combobox('getValue');
			var startTime = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
			var rowCount = d.id;
			datagridDiv.datagrid('getPager').data("pagination").options.pageSize = rowCount;
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

	//datetimebox
	mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox();
	mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox();

	//indicator type
	oc.ui.combobox({
		selector: '#interface_indicator',
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
			changeIfGroupIndicator(datagridDiv, d.id);
		}
	});
	
	//export button
	mainDiv.find('#interface_export_tab1').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('#interface_indicator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/ifgroup/export.htm?type="+type);
		}
	});
	
	//search button
	mainDiv.find('.interface-search-tab1').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {

			var timeInterval = mainDiv.find('.interface-timeinterval').combobox('getValue');
			var rowCount = mainDiv.find('.interface-displaycount').combobox('getValue');
			var startTime = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
			
			datagridDiv.datagrid('load', {
				'startTime' : startTime,
				'endTime' : endTime,
				'timePerid': timeInterval,
				'rowCount' : rowCount,
				'onePageRows' : rowCount,
				"needPagination" : true
			});

		}
	});
	
	//init status
	mainDiv.find('.interface-custom-time-tab1').addClass('hide');
	mainDiv.find('.interface-search-tab1').addClass('hide');
	
	var timeInterval = mainDiv.find('.interface-timeinterval').combobox('getValue');
	var displayCount = mainDiv.find('.interface-displaycount').combobox('getValue');
	var startTime = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
	var endTime = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
	
	/**
	 * 初始化datagrid
	 */
	var	datagridDiv=mainDiv.find('#oc_module_netflow_ifgroup_main_datagrid');
	var interface_netflow_index=oc.ui.datagrid({
		selector: datagridDiv,
		url: oc.resource.getUrl('netflow/ifgroup/ifGroupPageSelect.htm'),
		queryParams: {
			needPagination: true,
			startTime: startTime,
			endTime: endTime,
			timePerid: timeInterval
		},
		height: 210,
		pagination: true,
		pageSize: displayCount,
		pageList: [3, 5 ,10, 20, 30, 40, 50],
		sortOrder: 'desc',
		sortName: 'flowIn',
		fit: true,
		columns: [[
		     {field:'name',title:'接口组名称',sortable:false,width:20,align:'center'},
	         {field:'flowIn',title:'流入流量',sortable:true, width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, null);
	         }},
	         {field:'flowOut',title:'流出流量',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, null);
	         }},
	         {field:'flowTotal',title:'总流量',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, null);
	         }},
	         {field:'speedIn',title:'流入速率',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'speedOut',title:'流出速率',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'speedTotal',title:'总速率',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return flowFormatter(value, 'speed');
	         }},
	         {field:'flowPctge',title:'占比',width:20,sortable:true,align:'center',order: 'desc',formatter:function(value,row,index) {
	        	 return pctgeFormatter(value);
	         }}
	     ]],
	     onLoadSuccess: function(data) {
	    	//不显示每页条数列表
			var p = datagridDiv.datagrid('getPager');
			$(p).pagination({
				showPageList: false
			});
    		oc.util.ajax({
    			url:oc.resource.getUrl('netflow/ifgroup/getIfGroupChartData.htm'),
    			data: data.paramBo,
    			successMsg:null,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				var ti = mainDiv.find('.interface-timeinterval').combobox('getValue');
    				//生成线图
					var step = getStep(ti);
					genMainPageChart(mainDiv.find('#oc_module_netflow_ifgroup_chart'), data.data.timeLine, data.data.bos, data.data.sortColumn, data.data.yAxisName, step, ti);
					//生成饼图
    				var dst = dataTransfer(data.data.bos);
    				genMainPagePieChart(mainDiv.find('#oc_module_netflow_ifgroup_pie_chart'), dst);
 
    			}
    		});
	     },
	     onClickRow: function(rowIndex, rowData) {
	    	 oc.util.ajax({
    			url: oc.resource.getUrl('netflow/ifgroup/getIfIdsByGroupId.htm'),
    			data: {
    				ifGroupId: rowData.ifGroupId
    			},
    			successMsg:null,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				var ids = data.data;
    				if(ids && ids != null && ids != '' && data.code == 200) {
    					$('.netflow-main').data('ifId', ids);
    					var deviceGroupName = rowData.name;
    					var tab = $('.interface-netflow-main-tabs').tabs();
    					tab.tabs('add', {
    						title: deviceGroupName.substring(0, 10),
    						selected: true,
    						closable: true,
    						href: 'module/netflow/interface-netflow/interface_detail.html'
    					});
    					
    					var s = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
    					var e = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
    					var t = mainDiv.find('.interface-timeinterval').combobox('getValue');
    					var type = mainDiv.find('#interface_indicator').combobox('getValue');

    					$('.netflow-main').data('timeInterval', t);
    					$('.netflow-main').data('startTime', s);
    					$('.netflow-main').data('endTime', e);
    					$('.netflow-main').data('type', type);

    				}
    			}
    		});
	     }
	});
		
//	changeIfGroupIndicator(datagridDiv, 1);
	
})(jQuery);