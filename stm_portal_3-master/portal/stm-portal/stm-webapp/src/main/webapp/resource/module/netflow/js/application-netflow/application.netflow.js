var app_datagrid_netflow = null;

(function($){
	
	var mainDiv = $('#oc_module_netflow_application_main').attr("id", oc.util.generateId()).panel({
		fit: true,
		isOcAutoWidth: true
	});

	//timeInterval
	oc.ui.combobox({
		selector: mainDiv.find('.app-timeinterval'),
		data:[
			{id:'1hour',name:'最近一小时'},
			{id:'6hour',name:'最近六小时'},
			{id:'1day',name:'最近一天'},
			{id:'7day',name:'最近一周'},
			{id:'30day',name:'最近一个月'},
			{id:'custom',name:'自定义'}
		],
		placeholder: null,
		selected: '1hour',
		onSelect: function(d) {
			
			if(d.id == 'custom') {
				mainDiv.find('.app-custom-time-tab1').removeClass('hide');
				mainDiv.find('.app-search-tab1').removeClass('hide');
			} else {
				mainDiv.find('.app-custom-time-tab1').addClass('hide');
				mainDiv.find('.app-search-tab1').addClass('hide');
				
				var rowCount = mainDiv.find('.app-displaycount').combobox('getValue');
				var  data = {
					'onePageRows': rowCount,
					"recordCount": rowCount,
					"rowCount": rowCount,
					'onePageRows': rowCount,
					"sort": "in_flows",
					"order": 'desc',
					"showpagination": false,
					"timePerid": d.id
				};
				datagridDiv.datagrid('load', data);
			}
			
		}
	});
	
	//displaycount
	oc.ui.combobox({
		selector: mainDiv.find('.app-displaycount'),
		data: [
		    {id:'5',name:'5'},
		    {id:'10',name:'10'}
		],
		width: 90,
		placeholder: null,
		selected: '5',
		onSelect: function(d) {
			var t = mainDiv.find('.app-timeinterval').combobox('getValue');
			var rowCount = d.id;
			var startTime = mainDiv.find('.app-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.app-custom-date-endtime-tab1').datetimebox('getValue');
			datagridDiv.datagrid('getPager').data("pagination").options.pageSize = rowCount;
			var data = {
				'starttime': startTime,
				'endtime': endTime,
				'rowCount' : rowCount,
				'onePageRows' : rowCount,
				"recordCount":rowCount,
				"sort" : "in_flows",
				"order" : 'desc',
				"showpagination":false,
				"timePerid":t
			};
			datagridDiv.datagrid('load', data);
		}
	});
	
	//type
	oc.ui.combobox({
		selector: mainDiv.find('#app_indicator'),
		width: 90,
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		placeholder: null,
		onSelect: function(d) {
			changeAppIndicator(datagridDiv, d.id);
		}
	});
	
	//datetimebox init
	mainDiv.find('.app-custom-date-starttime-tab1').datetimebox();
	mainDiv.find('.app-custom-date-endtime-tab1').datetimebox();
	
	//search button
	mainDiv.find('.app-search-tab1').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var t = mainDiv.find('.app-timeinterval').combobox('getValue');
			var rowCount = mainDiv.find('.app-displaycount').combobox('getValue');
			var startTime = mainDiv.find('.app-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.app-custom-date-endtime-tab1').datetimebox('getValue');
			
			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
						'starttime': startTime,
						'endtime': endTime,
						'rowCount' : rowCount,
						'onePageRows' : rowCount,
						"recordCount":rowCount,
						"sort" : "in_flows",
						"order" : 'desc',
						"showpagination":false,
						"timePerid": t
				};
				datagridDiv.datagrid('load', data);
			}
		}
	});
	
	//export button  
	mainDiv.find('#app_ex').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('#app_indicator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/application/export.htm?type="+type);
		}
	});
	
	//init status
	mainDiv.find('.app-custom-time-tab1').addClass('hide');
	mainDiv.find('.app-search-tab1').addClass('hide');
	
	var timeInterval = mainDiv.find('.app-timeinterval').combobox('getValue');
	var startTime = mainDiv.find('.app-custom-date-starttime-tab1').datetimebox('getValue');
	var endTime = mainDiv.find('.app-custom-date-endtime-tab1').datetimebox('getValue');
	var displayCount = mainDiv.find('.app-displaycount').combobox('getValue');
	/**
	 * 初始化datagrid
	 */
	var	datagridDiv=mainDiv.find('#oc_module_netflow_application_main_datagrid');
	 app_datagrid_netflow =oc.ui.datagrid({
		selector: datagridDiv,
		url: oc.resource.getUrl('netflow/application/getapplication.htm'),
		queryParams: {
			starttime: startTime,
			endtime: endTime,
			timePerid: timeInterval,
			rowCount: displayCount,
			recordCount: displayCount,
			sort: "in_flows",
			order: 'desc',
			showpagination: true
		},
		sortOrder: 'desc',
		sortName: 'in_flows',
		pageSize : displayCount,
		pageList: [5, 10],
		columns:[[
	         {field:'app_name',title:'应用名称',sortable:false,width:20,align:'center'},
	         {field:'in_flows',title:'流入流量',sortable:true, width:20,align:'center',order: 'desc',formatter:function(value,row,index) {
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
	         {field:'flows_rate',title:'占比',sortable:true,wdith:20,order: 'desc',formatter:function(val,rowData,rowIndex){
	        	return pctgeFormatter(val);
	         }}
	     ]],
	     onLoadSuccess: function(data) {
	    	//不显示每页条数列表
			var p = datagridDiv.datagrid('getPager');
			$(p).pagination({
				showPageList: false
			});
			var param =data.appBo;
    		oc.util.ajax({
    		
    			url:oc.resource.getUrl('netflow/application/getapplicationchart.htm'),
    			data:{
    				applicationConditionVo : param,
    			},
    			successMsg:null,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				data = data.data;
    				var timepoint = data.timepoints;
    				var chatdatas = data.applicationChartDataModel;
    				var sortcolum = data.sortColum;
    				var unit = parseForJROfAppTerminalSession(sortcolum);
    				var ti = mainDiv.find('.app-timeinterval').combobox('getValue');
    				var step = getStep(ti, timepoint);
    				var st = mainDiv.find('.app-custom-date-starttime-tab1').datetimebox('getValue');
    				var et = mainDiv.find('.app-custom-date-endtime-tab1').datetimebox('getValue');

    				genMainPageChart(mainDiv.find('#oc_module_netflow_application_chart'), timepoint, chatdatas, unit, sortcolum, step, ti, st, et);
    				//饼图
    				var dst = dataTransfer(data.applicationChartDataModel);
    				genMainPagePieChart(mainDiv.find('#oc_module_netflow_application_pie_chart'), dst);
    			}
    		});
	     },
	     onClickRow: function(rowIndex, rowData) {
	    	 $('.netflow-main').data('appid', rowData.app_id);
	    	 
	    	 var appName = rowData.app_name;
	    	 if(appName && appName.length > 15) {
	    		 appName = appName.substring(0, 15);
	    	 }
	    	 
	    	 var tab = $('.app-netflow-main-tabs').tabs();
	    	 tab.tabs('add', {
	    		 title: appName,
	    		 selected: true,
	    		 closable: true,
	    		 href: 'module/netflow/application-netflow/application_detail.html'
	    	 });

	    	 //将主页面的参数带过去
			var type = mainDiv.find('#app_indicator').combobox('getValue');
			var startTime = mainDiv.find('.app-custom-date-starttime-tab1').datetimebox('getValue');
			var endTime = mainDiv.find('.app-custom-date-endtime-tab1').datetimebox('getValue');
			var t = mainDiv.find('.app-timeinterval').combobox('getValue');
			
			$('.netflow-main').data('apptype', type);
			$('.netflow-main').data('appstartTime', startTime);
			$('.netflow-main').data('appendTime', endTime);
			$('.netflow-main').data('apptimeInterval', t);		    	 
	     }
	});
	 
//	 changeAppIndicator(datagridDiv, 1);
	 
})(jQuery);