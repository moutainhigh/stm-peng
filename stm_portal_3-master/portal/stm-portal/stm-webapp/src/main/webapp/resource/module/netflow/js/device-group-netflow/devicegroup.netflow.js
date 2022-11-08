(function($){
	var mainDiv = $('#oc_module_netflow_device_main').attr("id", oc.util.generateId()).panel({
		fit: true,
		isOcAutoWidth: true
	});
	
	var tab = $('.devicegrp-netflow-main-tabs').tabs();
	mainDiv = tab.find('.devicegrp-main-div');

	oc.ui.combobox({
		selector: '.device-timeinterval',
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
		placeholder: null,
		selected: '1hour',
		onSelect: function(d) {
			if(d.id == 'custom') {
				mainDiv.find('.device-custom-time-tab1').removeClass('hide');
				mainDiv.find('.device-search-tab1').removeClass('hide');
			} else {
				mainDiv.find('.device-custom-time-tab1').addClass('hide');
				mainDiv.find('.device-search-tab1').addClass('hide');

				var startTime = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
				var endTime = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
				var rowCount = mainDiv.find('.device-displaycount').combobox('getValue');
				
				datagridDiv.datagrid('load', {
					'startTime': startTime,
					'endTime': endTime,
					'timePerid': d.id,
					'onePageRows': rowCount
				});
			}
		}
	});
	
	oc.ui.combobox({
		selector:'.device-displaycount',
		data: [
		    {id:'5',name:'5'},
		    {id:'10',name:'10'}
		],
		selected: '5',
		placeholder: null,
		width: 70,
		onSelect: function(d) {
			var t = mainDiv.find('.device-timeinterval').combobox('getValue');
			var startTime = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
			var rowCount = d.id;
		
			datagridDiv.datagrid('load', {
				'startTime': startTime,
				'endTime': endTime,
				'timePerid': t,
				'onePageRows': rowCount,
				'needPagination': true,
				'rowCount': rowCount
			});
		}
	});
	
	oc.ui.combobox({
		selector: '#device_indicator',
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
	});

	//init datetimebox
	mainDiv.find('.device-custom-date-starttime-tab1').datetimebox();
	mainDiv.find('.device-custom-date-endtime-tab1').datetimebox();
	
	//export button
	mainDiv.find('#device_ex').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			top.location = oc.resource.getUrl("netflow/deviceGroupNetflow/export.htm?type="+mainDiv.find('#device_indicator').combobox('getValue'));
		}
	});
	
	//search button
	mainDiv.find('.device-search-tab1').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {

			var timeInterval = mainDiv.find('.device-timeinterval').combobox('getValue');
			var startTime = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
			var displayCount = mainDiv.find('.device-displaycount').combobox('getValue');

			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				datagridDiv.datagrid('load', {
					startTime: startTime,
					endTime: endTime,
					timePerid: timeInterval,
					onePageRows: displayCount,
					rowCount: displayCount,
					needPagination: true
				});
			}
		}
	});

	//init status
	mainDiv.find('.device-custom-time-tab1').addClass('hide');
	mainDiv.find('.device-search-tab1').addClass('hide');
	
	//parameters for datagrid
	var timeInterval = mainDiv.find('.device-timeinterval').combobox('getValue');
	var startTime = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
	var endTime = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
	var displayCount = mainDiv.find('.device-displaycount').combobox('getValue');
	
	/**
	 * 初始化datagrid
	 */
	var	datagridDiv=mainDiv.find('#oc_module_netflow_devicegroup_main_datagrid'),
	datagridObj_div=oc.ui.datagrid({
		selector:datagridDiv,
		url: oc.resource.getUrl('netflow/deviceGroupNetflow/findManagedDeviceList.htm'),
		queryParams: {
			startTime: startTime,
			endTime: endTime,
			rowCount: 5,
			timePerid: timeInterval
		},
		pagination:true,
		pageSize: displayCount,
		pageList: [5],
		sortOrder: 'desc',
		sortName: 'flowIn',
		width:'auto',
		height:'auto',
		columns:[[
	         {field:'name',title:'设备组名称',sortable:false,width:20,align:'center'},
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
	         {field:'flowPctge',title:'占比',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
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
    			url:oc.resource.getUrl('netflow/deviceGroupNetflow/getDeviceChartData.htm'),
    			data: data.paramBo,
    			successMsg:null,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				var ti = mainDiv.find('.device-timeinterval').combobox('getValue');
					var step = getStep(ti);
					//生成线图
					genMainPageChart(mainDiv.find('#oc_module_netflow_devicegroup_chart'), data.data.timeLine, data.data.bos, data.data.sortColumn, data.data.yAxisName, step, ti);
					//生成饼图
    				var dst = dataTransfer(data.data.bos);
    				genMainPagePieChart(mainDiv.find('#oc_module_netflow_devicegroup_pie_chart'), dst);
    			}
    		});
	     },
	     onClickRow: function(rowIndex, rowData) {
	    	 //根据设备组ID，请求获取所有设备ID，以1,2,3....的形式存入data中
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
    					
    					var timeInterval = mainDiv.find('.device-timeinterval').combobox('getValue');
    					var startTime = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
    					var endTime = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
    					var type = mainDiv.find('#device_indicator').combobox('getValue');
    					
    					$('.netflow-main').data('timeInterval', timeInterval);
    					$('.netflow-main').data('startTime', startTime);
    					$('.netflow-main').data('endTime', endTime);
    					$('.netflow-main').data('type', type);

    				}
    			}
    		});
	    	 
	     }
	});
	
//	changeDeviceGroupIndicator(datagridDiv, 1);

})(jQuery);