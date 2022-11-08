(function($){

	var mainDiv = $('#oc_module_netflow_device_main').attr("id", oc.util.generateId()).panel({
		fit: true,
		isOcAutoWidth: true
	});
	
	var datagridTab1;
	mainDiv.find('.device-custom-time-tab1').addClass('hide');
	
	oc.ui.combobox({
		width: 110,
		placeholder: null,
		selector: mainDiv.find('.device-timeinterval'),
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
		selected: '1hour',
		onSelect: function(d) {
			if(d.id != 'custom') {
				mainDiv.find('.device-search-tab1').addClass('hide');
				mainDiv.find('.device-custom-time-tab1').addClass('hide');
				var rowCount = mainDiv.find('.device-displaycount').combobox('getValue');
				datagridTab1.load({
					'timePerid': d.id,
					'onePageRows': rowCount,
					'needPagination': true
				});
			} else {
				mainDiv.find('.device-search-tab1').removeClass('hide');
				mainDiv.find('.device-custom-time-tab1').removeClass('hide');
			}
		}
	});
	
	mainDiv.find('.device-custom-date-starttime-tab1').datetimebox();
	mainDiv.find('.device-custom-date-endtime-tab1').datetimebox();
	
	oc.ui.combobox({
		width: 70,
		placeholder: null,
		selector: mainDiv.find('.device-displaycount'),
		data: [
		    {id: 5,name: 5},
		    {id: 10,name: 10}
		],
		selected: '5',
		onSelect: function(d) {
			var t = mainDiv.find('.device-timeinterval').combobox('getValue');
			var rowCount = d.id;
			var startTime = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
			datagridDiv.datagrid('getPager').data("pagination").options.pageSize = rowCount;
			datagridTab1.load({
				'startTime': startTime,
				'endTime': endTime,
				'timePerid': t,
				"onePageRows": rowCount
			});
		}
	});
	
	oc.ui.combobox({
		width: 110,
		placeholder: null,
		selector: mainDiv.find('#device_indicator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}, 
		    { id: 4, name: '带宽使用率'}
		],
		selected: '1',
		onSelect: function(d) {
			changeIndicator(datagridTab1, d.id);
		}
	});
	
	mainDiv.find("#device_ex").linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('#device_indicator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/device/export.htm?type="+type);
		}
	});
	
	mainDiv.find('.device-search-tab1').linkbutton('RenderLB', {
		iconCls: 'icon-search', 
		onClick: function() {
			var rowCount = mainDiv.find('.device-displaycount').combobox('getValue');
			var timeInterval = mainDiv.find('.device-timeinterval').combobox('getValue');
			var stime = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
			var etime = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
			var checkMsg = checkCustomTime(stime, etime);
			
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				datagridTab1.load({
					'timePerid': timeInterval,
					'onePageRows': rowCount,
					'startTime': stime,
					'endTime': etime
				});
			}
			
		}
	});
	
	mainDiv.find('.device-search-tab1').addClass('hide');
	
	var timeInterval = mainDiv.find('.device-timeinterval').combobox('getValue'),
		displayCount = mainDiv.find('.device-displaycount').combobox('getValue');

	/**
	 * 初始化datagrid
	 */
	var	datagridDiv=mainDiv.find('#oc_module_netflow_device_main_datagrid');
		datagridTab1=oc.ui.datagrid({
			selector:datagridDiv,
			url:oc.resource.getUrl('netflow/device/findManagedDeviceList.htm'),
			queryParams: {
				"rowCount": displayCount,
				'timePerid': timeInterval
			},
			pageSize: displayCount,
			pageList: [5, 10],
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
					 field:'flowIn',title:'流入流量',sortable:true, width:20,align:'left',order:'desc',
					 formatter:function(value,row,index) {
						 return flowFormatter(value, null);
					 }
				},
				{
					 field:'flowOut',title:'流出流量',sortable:true,width:20,align:'left',order:'desc',
					 formatter:function(value,row,index) {
						 return flowFormatter(value, null);
					 }
				},
				{
					 field:'flowTotal',title:'总流量',sortable:true,width:20,align:'left',order:'desc',
					 formatter:function(value,row,index) {
						 return flowFormatter(value, null);
					 }
				},
				{
					 field:'speedIn',title:'流入速率',sortable:true,width:20,align:'left',order:'desc',
					 formatter:function(value,row,index) {
						 return flowFormatter(value, 'speed');
					 }
				},
				{
					 field:'speedOut',title:'流出速率',sortable:true,width:20,align:'left',order:'desc',
					 formatter:function(value,row,index) {
						 return flowFormatter(value, 'speed');
					 }
				},
				{
					 field:'speedTotal',title:'总速率',sortable:true,width:20,align:'left',order:'desc',
					 formatter:function(value,row,index) {
						 return flowFormatter(value, 'speed');
					 }
				},
				{
					 field:'flowPctge',title:'占比',sortable:true,width:15,align:'left',order:'desc',
					 formatter:function(value,row,index) {
					 	return pctgeFormatter(value);
					 }
				}
		     ]],
		     onLoadSuccess: function(data) {
		    	//不显示每页条数列表
				var p = datagridDiv.datagrid('getPager');
				$(p).pagination({
					showPageList: false
				});
	    		oc.util.ajax({
	    			url:oc.resource.getUrl('netflow/device/getDeviceChartData.htm'),
	    			data: data.paramBo,
	    			successMsg:null,
	    			failureMsg:'加载用户域数据失败！',
	    			async:false,
	    			success:function(data){
	    				var ti = $('.device-timeinterval').combobox('getValue');
	    				var st = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
	    				var et = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
	    				var step = getStep(ti, data.data.timeLine);
	    				
	    				genMainPageChart($('#oc_module_netflow_device_chart'), data.data.timeLine, data.data.bos, data.data.unit, data.data.yAxisName, step, ti, st, et);
	    				//生成饼图
	    				var dst = dataTransfer(data.data.bos);
	    				genMainPagePieChart($('#oc_module_netflow_device_pie_chart'), dst);
	    			}
	    		});
		     },
		     onClickRow: function(rowIndex, rowData) {
		    	 mainDiv.data('deviceIp', rowData.ip);
		    	 var deviceName = rowData.name;
		    	 var tab = $('.device-netflow-main-tabs').tabs();
		    	 tab.tabs('add', {
		    		 title: deviceName.substring(0, 10),
		    		 selected: true,
		    		 closable: true,
		    		 href: 'module/netflow/device-netflow/device_detail.html'
		    	 });

		    	 var timeInterval = mainDiv.find('.device-timeinterval').combobox('getValue');
		    	 var stime = mainDiv.find('.device-custom-date-starttime-tab1').datetimebox('getValue');
		    	 var etime = mainDiv.find('.device-custom-date-endtime-tab1').datetimebox('getValue');
		    	 var type = mainDiv.find('#device_indicator').combobox('getValue');
		    	 
		    	 $('.netflow-main').data('timeInterval', timeInterval);
		    	 $('.netflow-main').data('startTime', stime);
		    	 $('.netflow-main').data('endTime', etime);
		    	 $('.netflow-main').data('type', type);
		    	 
		     }
		});
	
})(jQuery);