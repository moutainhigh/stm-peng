(function($){

	//生成tab标签页,默认选中第一个标签页
	var domId=oc.util.generateId();
	var tab=$('#terminal_netflow_main_tabs').attr('id',domId).tabs({
		select: 0,
		onSelect: function(title, index) {
			if(index == 1) {
				termDatagridDivTab2.datagrid('reload');
			}
		}
	});
	
	var rowCount = 20;

	var mainDiv = tab.find('.terminal-list-main');
	var datagridTab2;
	var terminalViewForm = oc.ui.form({
		selector: mainDiv.find('.terminal-list-param-form'),
		combobox:[{
			placeholder: null,
			selector: mainDiv.find('.terminal-timeinterval2'),
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
					mainDiv.find('.terminal-custom-time-tab2').removeClass('hide')
				} else {
					mainDiv.find('.terminal-custom-time-tab2').addClass('hide')
					
					var data = {
						'timePerid': d.id,
						'onePageRows': rowCount,
						'rowCount': rowCount,
						'needPagination': true,
						'querySize': rowCount
					};
					termDatagridDivTab2.datagrid('load', data);
				}
			}
		}, {
			selector: mainDiv.find('#terminal_indicator2'),
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
				changeTerminalIndicator(termDatagridDivTab2, d.id);
			}
		}],
		datetimebox:[{
			width: 145,
			selector: mainDiv.find('.terminal-custom-date-starttime-tab2')
		}, {
			width: 145,
			selector: mainDiv.find('.terminal-custom-date-endtime-tab2')
		}]
	});

	//默认不显示自定义时间选择控件
	mainDiv.find('.terminal-custom-time-tab2').addClass('hide')
	
	var timeInterval = $('.terminal-timeinterval2').combobox('getValue');
	var rowCount = 20;

	var termDatagridDivTab2 = mainDiv.find('#terminal_list_datagrid');
	datagridTab2=oc.ui.datagrid({
		queryForm: terminalViewForm,
		selector: termDatagridDivTab2,
		url:oc.resource.getUrl('netflow/terminal/getallterminals.htm'),
		hideRest:true,
		hideSearch:true,
		queryParams :{
			timePerid: timeInterval,
			rowCount: rowCount,
			recordCount : rowCount,
			sort : "in_flows",
			order : 'desc',
			showpagination : true
		},
		sortOrder:'desc',
		sortName: 'in_flows',
		pageSize: rowCount,
		octoolbar:{
			left: [terminalViewForm.selector, {
				iconCls: 'icon-search',
				text:"查询",
				onClick: function(){
					
					var startTime = mainDiv.find('.terminal-custom-date-starttime-tab2').datetimebox('getValue');
					var endTime = mainDiv.find('.terminal-custom-date-endtime-tab2').datetimebox('getValue');
					var rc = 20;
					var t = mainDiv.find('.terminal-timeinterval2').combobox('getValue');
					
					if(t == 'custom') {
						var checkMsg = checkCustomTime(startTime, endTime);
						if('' != checkMsg) {
							alert(checkMsg);
							return;
						}
					}
					
					var search = mainDiv.find('#terminal_b_search').val();
					var data = {
							'starttime': startTime,
							'endtime': endTime,
							'rowCount' : rc,
							'onePageRows' : rc,
							"recordCount":rc,
							'onePageRows' : rc,
							"sort" : "in_flows",
							"order" : 'desc',
							"needPagination" : true,
							"showpagination":true,
							"timePerid": t
					};
					if(search && $.trim(search) != '') {
						data = $.extend(data, {'terminal_name': search});
					}
					termDatagridDivTab2.datagrid('load', data);
					
				}
			}],
			right: [{
				text:'导出',
				iconCls:'icon-word terminal-export',
				onClick:function(){
					top.location = oc.resource.getUrl("netflow/terminal/exportAll.htm?type="+$('#terminal_indicator2').combobox('getValue'));
				}
			}]
		},
		sortOrder: 'desc',
		sortName: 'in_flows',
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
			var type = mainDiv.find('#terminal_indicator2').combobox('getValue');
			var startTime = mainDiv.find('.terminal-custom-date-starttime-tab2').datetimebox('getValue');
			var endTime = mainDiv.find('.terminal-custom-date-endtime-tab2').datetimebox('getValue');
			var t = mainDiv.find('.terminal-timeinterval2').combobox('getValue');
			
			$('.netflow-main').data('terminaltype', type);
			$('.netflow-main').data('terminalstartTime', startTime);
			$('.netflow-main').data('terminalendTime', endTime);
			$('.netflow-main').data('terminaltimeInterval', t);		    	 
	     },
	     onLoadSuccess: function(data) {
			
	     }
	});
	
//	changeTerminalIndicator(termDatagridDivTab2, 1);

})(jQuery);