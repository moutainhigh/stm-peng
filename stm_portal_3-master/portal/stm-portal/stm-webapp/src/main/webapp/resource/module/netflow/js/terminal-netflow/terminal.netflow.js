(function($){
	
	var mainDiv = $('#oc_module_netflow_terminal_main').attr("id", oc.util.generateId()).panel({
		fit: true,
		isOcAutoWidth: true
	});

	var rowCount = 5;
	
	oc.ui.combobox({
		selector: mainDiv.find('.terminal-timeinterval'),
		data:[
			{id:'1hour',name:'最近一小时'},
			{id:'6hour',name:'最近六小时'},
			{id:'1day',name:'最近一天'},
			{id:'7day',name:'最近一周'},
			{id: '30day', name:'最近一个月'},
			{id:'custom',name:'自定义'}
		],
		selected: '1hour',
		placeholder: null,
		width: 110,
		onSelect: function(d) {

			if(d.id == 'custom') {
				mainDiv.find('.terminal-custom-time-tab1').removeClass('hide');
				mainDiv.find('.terminal-search-tab1').removeClass('hide');
			} else {
				mainDiv.find('.terminal-custom-time-tab1').addClass('hide');
				mainDiv.find('.terminal-search-tab1').addClass('hide');

				var rowCount = mainDiv.find('.terminal-displaycount').combobox('getValue');
				termDatagridDiv.datagrid('load', {
					'rowCount' : rowCount,
					'onePageRows' : rowCount,
					"recordCount":rowCount,
					'onePageRows' : rowCount,
					"sort" : "in_flows",
					"order" : 'desc',
					"needPagination" : true,
					"showpagination":false,
					"timePerid": d.id,
				});
			}
		}
	});
	
	oc.ui.combobox({
		selector: mainDiv.find('.terminal-displaycount'),
		data: [
		    {id:'5',name:'5'},
		    {id:'10',name:'10'}
		],
		value: '5',
		width: 90,
		placeholder: null,
		onSelect: function(d) {
			var rowCount = d.id;
			var t = mainDiv.find('.terminal-timeinterval').combobox('getValue');
			termDatagridDiv.datagrid('getPager').data("pagination").options.pageSize = rowCount;
			termDatagridDiv.datagrid('load', {
				'rowCount' : rowCount,
				'onePageRows' : rowCount,
				"recordCount":rowCount,
				'onePageRows' : rowCount,
				"sort" : "in_flows",
				"order" : 'desc',
				"needPagination" : true,
				"showpagination":false,
				"timePerid": t
			});
		}
	});
	
	oc.ui.combobox({
		selector: mainDiv.find('#terminal_indicator'),
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
			changeTerminalIndicator(termDatagridDiv, d.id*1);
		}
	});
	
	//datetimebox
	mainDiv.find('.terminal-custom-date-starttime-tab1').datetimebox();
	mainDiv.find('.terminal-custom-date-endtime-tab1').datetimebox();
	
	//search button
	mainDiv.find('.terminal-search-tab1').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			
			var rc = mainDiv.find('.terminal-displaycount').combobox('getValue');
			var startTime = mainDiv.find('.terminal-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.terminal-custom-date-endtime-tab1').datetimebox('getValue');
			var t = mainDiv.find('.terminal-timeinterval').combobox('getValue');
			
			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				termDatagridDiv.datagrid('load', {
					'starttime': startTime,
					'endtime': endTime,
					'rowCount' : rc,
					'onePageRows' : rc,
					"recordCount":rc,
					'onePageRows' : rc,
					"sort" : "in_flows",
					"order" : 'desc',
					"needPagination" : true,
					"showpagination": true,
					"timePerid": t
				});
			}
		}
	});
	
	//export button
	mainDiv.find('#terminal_ex').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('#terminal_indicator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/terminal/export.htm?type="+type);
		}
	});
	
	//init status
	mainDiv.find('.terminal-custom-time-tab1').addClass('hide');
	mainDiv.find('.terminal-search-tab1').addClass('hide');
	
	var timeInterval = mainDiv.find('.terminal-timeinterval').combobox('getValue');
	var displayCount = mainDiv.find('.terminal-displaycount').combobox('getValue');
	
	/**
	 * 初始化datagrid
	 */
	var	termDatagridDiv=mainDiv.find('#oc_module_netflow_terminal_main_datagrid');
	
		var terminal_datagrid =oc.ui.datagrid({
			selector:termDatagridDiv,
			url:oc.resource.getUrl('netflow/terminal/getallterminals.htm'),
			queryParams :{
				timePerid: timeInterval,
				rowCount: displayCount,
				recordCount : displayCount,
				sort : "in_flows",
				order : 'desc',
				showpagination : true
			},
			sortOrder:'desc',
			sortName: 'in_flows',
			pageSize: displayCount,
			pageList: [5],
			columns:[[
		         {field:'terminal_name',title:'终端名称',sortable:false,width:20,align:'center'},
		         {field:'in_flows',title:'流入流量',sortable:true, width:20,align:'center',order:'desc',formatter:function(value,row,index) {
		        	 return flowFormatter(value, null);
		         }},
		         {field:'out_flows',title:'流出流量',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
		        	 return flowFormatter(value, null);
		         }},
		         {field:'total_flows',title:'总流量',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
		        	 return flowFormatter(value, null);
		         }},
		         {field:'in_speed',title:'流入速率',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
		        	 return flowFormatter(value, 'speed');
		         }},
		         {field:'out_speed',title:'流出速率',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
		        	 return flowFormatter(value, 'speed');
		         }},
		         {field:'total_speed',title:'总速率',sortable:true,width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
		        	 return flowFormatter(value, 'speed');
		         }},
		         {
		        	 field:'flows_rate',title:'占比',width:20,sortable:true,order: 'desc',formatter:function(val,rowData,rowIndex){
		        		 return pctgeFormatter(val);
		         	}
		         }
		     ]],
		     onLoadSuccess: function(data) {
		    	//不显示每页条数列表
				var p = termDatagridDiv.datagrid('getPager');
				$(p).pagination({
					showPageList: false
				});
				var param =data.tcBo;
	    		oc.util.ajax({
	    			url:oc.resource.getUrl('netflow/terminal/getallterminalschart.htm'),
	    			data:{
	    				terminalConditionVo : param,
	    			},
	    			successMsg:null,
	    			failureMsg:'加载用户域数据失败！',
	    			async:false,
	    			success:function(data){
	    				data = data.data;
	    				var timepoint = data.timepoints;
	    				var chatdatas = data.terminalChartDataModel;
	    				var sortcolum = data.sortColum;
	    				var unit = parseForJROfAppTerminalSession(sortcolum);						
	    				var ti = mainDiv.find('.terminal-timeinterval').combobox('getValue')
	    				var step = getStep(ti, timepoint);
	    				var st = mainDiv.find('.terminal-custom-date-starttime-tab1').datetimebox('getValue');
	    				var et = mainDiv.find('.terminal-custom-date-endtime-tab1').datetimebox('getValue');

	    				//线图
	    				genMainPageChart(mainDiv.find('#oc_module_netflow_terminal_chart'), timepoint, chatdatas, unit, sortcolum, step, ti, st, et);
						//生成饼图
	    				var dst = dataTransfer(data.terminalChartDataModel);
	    				genMainPagePieChart(mainDiv.find('#oc_module_netflow_terminal_pie_chart'), dst);
 	    			}
	    		});
		     },
		     onClickRow: function(rowIndex, rowData) {
		    	 $('.netflow-main').data('terminal_name', rowData.terminal_name);
		    	 
		    	 var terminalName = rowData.terminal_name;
		    	 if(terminalName && terminalName.length > 15) {
		    		 terminalName = terminalName.substring(0, 15);
		    	 }
		    	 
		    	 var tab = $('.terminal-netflow-main-tabs').tabs();
		    	 tab.tabs('add', {
		    		 title: terminalName,
		    		 selected: true,
		    		 closable: true,
		    		 href: 'module/netflow/terminal-netflow/terminal_detail.html'
		    	 });
		    	 
		    	 //将主页面的参数带过去
				var type = mainDiv.find('#terminal_indicator').combobox('getValue');
				var startTime = mainDiv.find('.terminal-custom-date-starttime-tab1').datetimebox('getValue');
				var endTime = mainDiv.find('.terminal-custom-date-endtime-tab1').datetimebox('getValue');
				var t = mainDiv.find('.terminal-timeinterval').combobox('getValue');
				$('.netflow-main').data('terminaltype', type);
				$('.netflow-main').data('terminalstartTime', startTime);
				$('.netflow-main').data('terminalendTime', endTime);
				$('.netflow-main').data('terminaltimeInterval', t);		    	 
		    }
		});
		
//		changeTerminalIndicator(termDatagridDiv, 1);

})(jQuery);