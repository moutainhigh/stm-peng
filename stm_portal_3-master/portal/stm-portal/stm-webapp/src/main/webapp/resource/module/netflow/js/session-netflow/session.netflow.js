(function($){
	var mainDiv = $('#oc_module_netflow_sessionsingle_main').attr("id", oc.util.generateId()).panel({
		fit: true,
		isOcAutoWidth: true
	});

	//timeinterval
	oc.ui.combobox({
		selector: mainDiv.find('.session-timeinterval1'),
		data:[
			{id:'1hour',name:'最近一小时'},
			{id:'6hour',name:'最近六小时'},
			{id:'1day',name:'最近一天'},
			{id:'7day',name:'最近一周'},
			{id: '30day', name: '最近一个月'},
			{id:'custom',name:'自定义'}
		],
		placeholder: null,
		width: 110,
		selected: '1hour',
		onSelect: function(d) {
			
			if(d.id == 'custom') {
				mainDiv.find('.session-custom-time-tab1').removeClass('hide');
				mainDiv.find('.session-search-tab1').removeClass('hide');
			} else {
				mainDiv.find('.session-custom-time-tab1').addClass('hide');
				mainDiv.find('.session-search-tab1').addClass('hide');
				
				var rowCount = mainDiv.find('.session-displaycount1').combobox('getValue');
				datagridDiv.datagrid('load', {
					"rowCount": rowCount,
					"onePageRows": rowCount,
					"recordCount": rowCount,
					"sort": "in_flows",
					"order": 'desc',
					"showpagination": false,
					"timePerid": d.id
					//"currentSrcIp": $('.device-session').data('srcip'),
					//"currentDstIp": $('.device-session').data('dstip')
				});
			}
		}
	});
	
	//type
	oc.ui.combobox({
		selector: mainDiv.find('#session_indicator1'),
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
			changeSessionIndicator(datagridDiv, d.id);
		}
	});


	//displaycount
	oc.ui.combobox({
		selector: mainDiv.find('.session-displaycount1'),
		data: [
		    {id:'5',name:'5'},
		    {id:'10',name:'10'}
		],
		placeholder: null,
		width: 70,
		selected: '5',
		onSelect: function(d) {
			var t = mainDiv.find('.session-timeinterval1').combobox('getValue');
			var rowCount = d.id;
			var startTime = mainDiv.find('.session-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.session-custom-date-endtime-tab1').datetimebox('getValue');
			datagridDiv.datagrid('getPager').data("pagination").options.pageSize = rowCount;
			datagridDiv.datagrid('load', {
				'starttime': startTime,
				'endtime': endTime,
				'rowCount' : rowCount,
				'onePageRows' : rowCount,
				"recordCount":rowCount,
				'onePageRows' : rowCount,
				"sort" : 'in_flows',
				"order" : 'desc',
				"showpagination": false,
				"timePerid": t
				//"currentSrcIp" : $('.device-session').data('srcip'),
				//"currentDstIp" : $('.device-session').data('dstip')
			});
		}
	});

	//datetimebox
	mainDiv.find('.session-custom-date-starttime-tab1').datetimebox();
	mainDiv.find('.session-custom-date-endtime-tab1').datetimebox();
	
	//search button
	mainDiv.find('.session-search-tab1').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			
			var t = mainDiv.find('.session-timeinterval1').combobox('getValue');
			var rc = mainDiv.find('#session_indicator1').combobox('getValue');
			var startTime = mainDiv.find('.session-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.session-custom-date-endtime-tab1').datetimebox('getValue');
			
			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				datagridDiv.datagrid('load', {
					'starttime': startTime,
					'endtime': endTime,
					'rowCount' : rc,
					'onePageRows' : rc,
					"recordCount":rc,
					'onePageRows' : rc,
					"sort" : 'in_flows',
					"order" : 'desc',
					"showpagination": true,
					"timePerid": t
				});
			}
		}
	});

	//export button
	mainDiv.find('#session_ex1').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('#session_indicator1').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/session/export.htm?type="+type);
		}
	});

	//init status
	mainDiv.find('.session-custom-time-tab1').addClass('hide');
	mainDiv.find('.session-search-tab1').addClass('hide');

	var timeInterval = mainDiv.find('.session-timeinterval1').combobox('getValue');
	var displayCount = mainDiv.find('#session_indicator1').combobox('getValue');
	var startTime = mainDiv.find('.session-custom-date-starttime-tab1').datetimebox('getValue');
	var endTime = mainDiv.find('.session-custom-date-endtime-tab1').datetimebox('getValue');
	
	/**
	 * 初始化datagrid
	 */
	var	datagridDiv=mainDiv.find('#oc_module_netflow_session_main_datagrid');
	var session_datagrid =oc.ui.datagrid({
			selector: datagridDiv,
			url: oc.resource.getUrl('netflow/session/getsessions.htm'),
			queryParams :{
				timePerid: timeInterval,
				rowCount: displayCount,
				recordCount : displayCount,
				sort : "in_flows",
				order : 'desc',
				showpagination : true
			},
			sortOrder: 'desc',
			sortName: 'in_flows',
			pageSize: displayCount,
			pageList: [5, 10],
			columns:[[
				{field:'src_ip',title:'原地址',width:20,sortable:false},
				{field:'dst_ip',title:'目的地址',width:20,sortable:false},
		         {field:'in_flows',title:'流入流量',sortable:true,align:'center',width:20,order: 'desc',formatter:function(value,row,index) {
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
		         {field:'flows_rate',title:'占比',width:20,sortable:true,order: 'desc',formatter:function(val,rowData,rowIndex){
		        	 return pctgeFormatter(val);
		         }}
		     ]],
		     onLoadSuccess: function(data) {
		    	//不显示每页条数列表
				var p = datagridDiv.datagrid('getPager');
				$(p).pagination({
					showPageList: false
				});
				var param =data.scBo;
	    		oc.util.ajax({
	    		
	    			url:oc.resource.getUrl('netflow/session//getsessionchartdata.htm'),
	    			data:{
	    				sessionCondition : param,
	    			},
	    			successMsg:null,
	    			failureMsg:'加载用户域数据失败！',
	    			async:false,
	    			success:function(data){
	    				data = data.data;
	    				var timepoint = data.timepoints;
	    				var chatdatas = data.sessionChartDataModel;
	    				var sortcolum = data.sortColum;
	    				var unit = parseForJROfAppTerminalSession(sortcolum);		
	    				var ti = mainDiv.find('.session-timeinterval1').combobox('getValue');
	    				var step = getStep(ti, timepoint);
	    				var st = mainDiv.find('.session-custom-date-starttime-tab1').datetimebox('getValue');
	    				var et = mainDiv.find('.session-custom-date-endtime-tab1').datetimebox('getValue');

	    				//线图
	    				genMainPageChart(mainDiv.find('#oc_module_netflow_session_chart'), timepoint, chatdatas, unit, sortcolum, step, ti, st, et);
	    				//饼图
	    				var dst = dataTransfer(data.sessionChartDataModel);
	    				genMainPagePieChart(mainDiv.find('#oc_module_netflow_session_pie_chart'), dst);
	    			}
	    		});
		     },
		     onClickRow: function(rowIndex, rowData) {
		    	 
		    	 var sessionsingleName = (rowData.src_ip || '') + '-' + (rowData.dst_ip || '');
		    	 if(sessionsingleName.length > 15) {
		    		 sessionsingleName = sessionsingleName.substring(0, 15);
		    	 }
		    	 var tab = $('.sessionsingle-netflow-main-tabs').tabs();
		    	 tab.tabs('add', {
		    		 title: sessionsingleName,
		    		 selected: true,
		    		 closable: true,
		    		 href: 'module/netflow/session-netflow/session_detail.html'
		    	 });
		    	 
		    	 $('.netflow-main').data('src_ip', rowData.src_ip);
		    	 $('.netflow-main').data('dst_ip', rowData.dst_ip);

		    	 //将主页面的参数带过去
				var type = mainDiv.find('#session_indicator1').combobox('getValue');
				var startTime = mainDiv.find('.session-custom-date-starttime-tab1').datetimebox('getValue');
				var endTime = mainDiv.find('.session-custom-date-endtime-tab1').datetimebox('getValue');
				var t = mainDiv.find('.session-timeinterval1').combobox('getValue');
				
				$('.netflow-main').data('sessiontype', type);
				$('.netflow-main').data('sessionstartTime', startTime);
				$('.netflow-main').data('sessionendTime', endTime);
				$('.netflow-main').data('sessiontimeInterval', t);		    	 
		     }
		});

//	changeSessionIndicator(datagridDiv, 1);

})(jQuery)
