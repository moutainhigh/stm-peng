(function($){

	//生成tab标签页,默认选中第一个标签页
	var domId=oc.util.generateId();
	var tab=$('#app_netflow_main_tabs').attr('id',domId).tabs({
		select: 0,
		onSelect: function(title, index) {
			if(index == 1) {
				appDatagridDivTab2.datagrid('reload');
			}
		}
	});
	
	var rowCount = 20;

	var mainDiv = tab.find('.app-list-main');
	var datagridTab2;
	var appViewForm = oc.ui.form({
		selector: mainDiv.find('.app-list-param-form'),
		combobox:[{
			placeholder: null,
			selector: mainDiv.find('.app-timeinterval2'),
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
					mainDiv.find('.app-custom-time-tab2').removeClass('hide')
				} else {
					mainDiv.find('.app-custom-time-tab2').addClass('hide')
					
					var data = {
						'timePerid': d.id,
						'onePageRows': rowCount,
						'rowCount': rowCount,
						needPagination: true,
						querySize: rowCount
					};
					appDatagridDivTab2.datagrid('load', data);
				}
			}
		}, {
			selector: '#app_indicator2',
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
				changeAppIndicator(appDatagridDivTab2, d.id);
			}
		}],
		datetimebox:[{
			width: 145,
			selector: mainDiv.find('.app-custom-date-starttime-tab2')
		}, {
			width: 145,
			selector: mainDiv.find('.app-custom-date-endtime-tab2')
		}]
	});

	//默认不显示自定义时间选择控件
	mainDiv.find('.app-custom-time-tab2').addClass('hide')
	
	var timeInterval = mainDiv.find('.app-timeinterval2').combobox('getValue');
	var displayCount = 20;
	var startTime = mainDiv.find('.app-custom-date-starttime-tab2').datetimebox('getValue');
	var endTime = mainDiv.find('.app-custom-date-endtime-tab2').datetimebox('getValue');

	var appDatagridDivTab2 = mainDiv.find('#app_list_datagrid');
	datagridTab2=oc.ui.datagrid({
		queryForm: appViewForm,
		selector: appDatagridDivTab2,
		url: oc.resource.getUrl('netflow/application/getapplication.htm'),
		hideRest: true,
		hideSearch: true,
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
		pageSize: rowCount,
		octoolbar:{
			left: [appViewForm.selector, {
				iconCls: 'icon-search',
				text:"查询",
				onClick: function(){
					
					var timeInterval = mainDiv.find('.app-timeinterval2').combobox('getValue');
					var startTime = mainDiv.find('.app-custom-date-starttime-tab2').datetimebox('getValue');
					var endTime = mainDiv.find('.app-custom-date-endtime-tab2').datetimebox('getValue');
					
					if(timeInterval == 'custom') {
						var checkMsg = checkCustomTime(startTime, endTime);
						if('' != checkMsg) {
							alert(checkMsg);
							return;
						}
					}
					
					var search = mainDiv.find('#app_b_search').val();
					var data = {
						'starttime': startTime,
						'endtime': endTime,
						'timePerid': timeInterval,
						'onePageRows': rowCount,
						'rowCount': rowCount,
						'needPagination': true,
						'querySize': rowCount
					};
					if(search && $.trim(search) != '') {
						data = $.extend(data, {'app_name': search});
					}
					appDatagridDivTab2.datagrid('load', data);
				}
			}],
			right: [{
				text:'导出',
				iconCls:'icon-word app-export',
				onClick:function(){
					top.location = oc.resource.getUrl("netflow/application/exportAll.htm?type="+$("#app_indicator2").combobox("getValue"));
				}
			}]
		},
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
			var type = mainDiv.find('#app_indicator2').combobox('getValue');
			var startTime = mainDiv.find('.app-custom-date-starttime-tab2').datetimebox('getValue');
			var endTime = mainDiv.find('.app-custom-date-endtime-tab2').datetimebox('getValue');
			var t = mainDiv.find('.app-timeinterval2').combobox('getValue');
			
			$('.netflow-main').data('apptype', type);
			$('.netflow-main').data('appstartTime', startTime);
			$('.netflow-main').data('appendTime', endTime);
			$('.netflow-main').data('apptimeInterval', t);		    	 
	     },
	     onLoadSuccess: function(data) {
	    	 
	     }
	});
	
//	changeAppIndicator(appDatagridDivTab2, 1);

})(jQuery);