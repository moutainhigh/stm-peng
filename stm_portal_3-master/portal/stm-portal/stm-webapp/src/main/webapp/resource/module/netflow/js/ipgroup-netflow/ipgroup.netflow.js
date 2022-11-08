(function($){

	var ramdomId = oc.util.generateId();
	var mainDiv = $('#oc_module_netflow_ipgroup_main').attr("id", ramdomId).panel({
		fit: true,
		isOcAutoWidth: true
	});

	oc.ui.combobox({
		placeholder: null,
		width: 110,
		selector : mainDiv.find('.ipgroup-timeinterval'),
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
		onSelect : function(d) {
			
			if(d.id == 'custom') {
				mainDiv.find('.ipgroup-custom-time-tab1').removeClass('hide');
				mainDiv.find('.ipgroup-search-tab1').removeClass('hide');
			} else {
				mainDiv.find('.ipgroup-custom-time-tab1').addClass('hide');
				mainDiv.find('.ipgroup-search-tab1').addClass('hide');

				var rowCount = mainDiv.find('.ipgroup-displaycount').combobox('getValue');
				datagridDivTab1.datagrid('load', {
					'timePerid': d.id,
					'rowCount' : rowCount,
					'onePageRows' : rowCount,
					'needPagination' : true
				});
			}
		}
	});

	oc.ui.combobox({
		selector : mainDiv.find('.ipgroup-displaycount'),
		data : [{
			id : '5',
			name : '5'
		}, {
			id : '10',
			name : '10'
		}],
		placeholder: null,
		width: 70,
		selected: '5',
		onSelect : function(d) {
			var t = mainDiv.find('.ipgroup-timeinterval').combobox('getValue');
			var startTime = mainDiv.find('.ipgroup-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.ipgroup-custom-date-endtime-tab1').datetimebox('getValue');
			var rowCount = d.id;
			datagridDivTab1.datagrid('getPager').data("pagination").options.pageSize = rowCount;
			datagridDivTab1.datagrid('load', {
				'startTime' : startTime,
				'endTime' : endTime,
				'timePerid': t,
				'rowCount' : rowCount,
				'onePageRows' : rowCount,
				'needPagination' : true
			});
		}
	});
	
	oc.ui.combobox({
		selector: mainDiv.find('#ipgroup_indicator'),
		placeholder: null,
		width: 90,
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		selected: '1',
		onSelect: function(d) {
			changeIpGroupIndicator(datagridDivTab1, d.id);
		}
	});
	
	//datetimebox
	mainDiv.find('.ipgroup-custom-date-starttime-tab1').datetimebox();
	mainDiv.find('.ipgroup-custom-date-endtime-tab1').datetimebox();
	
	//seach button
	mainDiv.find('.ipgroup-search-tab1').linkbutton('RenderLB', {
		iconCls: 'icon-search', 
		onClick: function() {
			var t = mainDiv.find('.ipgroup-timeinterval').combobox('getValue');
			var startTime = mainDiv.find('.ipgroup-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.ipgroup-custom-date-endtime-tab1').datetimebox('getValue');
			var rowCount = mainDiv.find('.ipgroup-displaycount').combobox('getValue');

			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				datagridDivTab1.datagrid('load', {
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
	
	//export button
	mainDiv.find('#ipgroup_ex').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('#ipgroup_indicator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/ipgroup/export.htm?type="+type);
		}
	});
	
	//init status
	mainDiv.find('.ipgroup-custom-time-tab1').addClass('hide');
	mainDiv.find('.ipgroup-search-tab1').addClass('hide');
	
	var timeInterval = mainDiv.find('.ipgroup-timeinterval').combobox('getValue');
	var startTime = mainDiv.find('.ipgroup-custom-date-starttime-tab1').datetimebox('getValue');
	var endTime = mainDiv.find('.ipgroup-custom-date-endtime-tab1').datetimebox('getValue');
	var displayCount = mainDiv.find('.ipgroup-displaycount').combobox('getValue');

	/**
	 * 初始化datagrid
	 */
	var	datagridDivTab1=mainDiv.find('#oc_module_netflow_ipgroup_main_datagrid');
		var interface_netflow_index = oc.ui.datagrid({
			selector:datagridDivTab1,
			url:oc.resource.getUrl('netflow/ipgroup/ipGroupPageSelect.htm'),
			queryParams: {
				needPagination: true,
				timePerid: timeInterval,
				'onePageRows' : displayCount,
			},
			pagination:true,
			pageSize: displayCount,
			sortOrder: 'desc',
			sortName: 'flowIn',
			width:'auto',
			height:'auto',
			columns:[[
			     {field:'name',title:'IP分组名称',sortable:false,width:20,align:'center'},
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
				var p = datagridDivTab1.datagrid('getPager');
				$(p).pagination({
					showPageList: false
				});
	    		oc.util.ajax({
	    			url:oc.resource.getUrl('netflow/ipgroup/getIpGroupChartData.htm'),
	    			data: data.paramBo,
	    			successMsg:null,
	    			failureMsg:'加载用户域数据失败！',
	    			async:false,
	    			success:function(data){
	    				var ti = mainDiv.find('.ipgroup-timeinterval').combobox('getValue');
	    				var step = getStep(ti);
	    				//生成线图
	    				genMainPageChart(mainDiv.find('#oc_module_netflow_ipgroup_chart'), data.data.timeLine, data.data.bos, data.data.sortColumn, data.data.yAxisName, step, ti);
	    				//生成饼图
	    				var dst = dataTransfer(data.data.bos);
	    				genMainPagePieChart(mainDiv.find('#oc_module_netflow_ipgroup_pie_chart'), dst);

	    			}
	    		});
		     },
		     onClickRow: function(rowIndex, rowData) {
		    	 $('.netflow-main').data('ipGroupId', rowData.ipGroupId);
		    	 var ipgName = rowData.name || '';
		    	 if(ipgName && ipgName.length > 10) {
		    		 ipgName = ipgName.substring(0, 10);
		    	 }
		    	 var newTab = $('.ipgroup-netflow-main-tabs').tabs('add', {
		    		 title: ipgName,
		    		 selected: true,
		    		 closable: true,
		    		 href: 'module/netflow/ipgroup-netflow/ipgroup_detail.html'
		    	 });
		    	 //设置TOPN页面的参数：时间，起止时间，指标
				 var timeInterval = mainDiv.find('.ipgroup-timeinterval').combobox('getValue');
				 var startTime = mainDiv.find('.ipgroup-custom-date-starttime-tab1').datetimebox('getValue');
				 var endTime = mainDiv.find('.ipgroup-custom-date-endtime-tab1').datetimebox('getValue');
				 var type = mainDiv.find('#ipgroup_indicator').combobox('getValue');
				 
		    	 $('.netflow-main').data('ipgTimeInterval', timeInterval);
		    	 $('.netflow-main').data('ipgStartTime', startTime);
		    	 $('.netflow-main').data('ipgEndTime', endTime);
		    	 $('.netflow-main').data('ipgType', type);
		     }
		});
		
//		changeIpGroupIndicator(datagridDivTab1, 1);

})(jQuery);