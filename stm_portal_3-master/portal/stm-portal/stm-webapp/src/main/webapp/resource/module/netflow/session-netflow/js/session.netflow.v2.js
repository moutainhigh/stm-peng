(function($){

	//生成tab标签页,默认选中第一个标签页
	var domId=oc.util.generateId();
	var tab=$('#sessionsingle_netflow_main_tabs').attr('id',domId).tabs({
		select: 0,
		onSelect: function(title, index) {
			if(index == 1) {
				sessionDatagridDivTab2.datagrid('reload');
			}
		}
	});
	
	var mainDiv = tab.find('.sessionsingle-list-main');

	var rowCount = 20;

	var datagridTab2;
	var sessionsingleViewForm = oc.ui.form({
		selector: mainDiv.find('.sessionsingle-list-param-form'),
		combobox:[{
			placeholder: null,
			selector: mainDiv.find('.sessionsingle-timeinterval2'),
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
					mainDiv.find('.sessionsingle-custom-time-tab2').removeClass('hide')
				} else {
					mainDiv.find('.sessionsingle-custom-time-tab2').addClass('hide')
					
					var data = {
						onePageRows: rowCount,
						timePerid: timeInterval,
						rowCount: displayCount,
						recordCount : displayCount,
						sort : "in_flows",
						order : 'desc',
						showpagination : true
					};
					sessionDatagridDivTab2.datagrid('load', data);
				}
			}
		}, {
			selector: '#sessionsingle_indicator2',
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
				changeSessionIndicator(sessionDatagridDivTab2, d.id);
			}
		}],
		datetimebox:[{
			width: 145,
			selector: mainDiv.find('.sessionsingle-custom-date-starttime-tab2')
		}, {
			width: 145,
			selector: mainDiv.find('.sessionsingle-custom-date-endtime-tab2')
		}]
	});

	//默认不显示自定义时间选择控件
	mainDiv.find('.sessionsingle-custom-time-tab2').addClass('hide')
	
	var timeInterval = mainDiv.find('.sessionsingle-timeinterval2').combobox('getValue');
	var displayCount = 20;

	var sessionDatagridDivTab2 = mainDiv.find('#sessionsingle_list_datagrid');
	datagridTab2=oc.ui.datagrid({
		queryForm: sessionsingleViewForm,
		selector: sessionDatagridDivTab2,
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
		hideRest:true,
		hideSearch:true,
		pageSize: displayCount,
		octoolbar:{
			left: [sessionsingleViewForm.selector, {
				iconCls: 'icon-search',
				text:"查询",
				onClick: function(){

					var timeInterval = mainDiv.find('.sessionsingle-timeinterval2').combobox('getValue');
					var st = mainDiv.find('.sessionsingle-custom-date-starttime-tab2').datetimebox('getValue');
					var et = mainDiv.find('.sessionsingle-custom-date-endtime-tab2').datetimebox('getValue');
					
					if(timeInterval == 'custom') {
						var checkMsg = checkCustomTime(st, et);
						if('' != checkMsg) {
							alert(checkMsg);
							return;
						}
					}
					
					var search = mainDiv.find('#session_b_search_orgin').val();
					var dest = mainDiv.find('#session_b_search_dest').val();
					var ip = undefined;
					if(search && search != '') {
						ip = search;
					}
					var data = {
						starttime: st,
						endtime: et,
						onePageRows: rowCount,
						timePerid: timeInterval,
						rowCount: displayCount,
						recordCount : displayCount,
						sort : "in_flows",
						order : 'desc',
						showpagination : true
					};
					if(ip) {
						data = $.extend(data, {'currentSrcIp': ip});
					}
					if(dest && dest != '') {
						data = $.extend(data, {'currentDstIp': dest});
					}
					sessionDatagridDivTab2.datagrid('load', data);
				}
			}],
			right: [{
				text:'导出',
				iconCls:'icon-word sessionsingle-export',
				onClick:function(){
					top.location =  oc.resource.getUrl('netflow/session/exportAll.htm?type='+$("#sessionsingle_indicator2").combobox("getValue"));
				}
			}]
		},
		columns:[[
			{field:'src_ip',title:'源地址',width:20,sortable:false},
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
	     onClickRow: function(rowIndex, rowData) {
			
	    	 mainDiv.data('src_ip', rowData.src_ip);
	    	 mainDiv.data('dst_ip', rowData.dst_ip);
	    	 
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

	    	 //将主页面的参数带过去
			var type = mainDiv.find('#sessionsingle_indicator2').combobox('getValue');
			var startTime = mainDiv.find('.sessionsingle-custom-date-starttime-tab2').datetimebox('getValue');
			var endTime = mainDiv.find('.sessionsingle-custom-date-endtime-tab2').datetimebox('getValue');
			var t = mainDiv.find('.sessionsingle-timeinterval2').combobox('getValue');
			
			$('.netflow-main').data('sessiontype', type);
			$('.netflow-main').data('sessionstartTime', startTime);
			$('.netflow-main').data('sessionendTime', endTime);
			$('.netflow-main').data('sessiontimeInterval', t);		    	 
	     },
	     onLoadSuccess: function(data) {
			
	     }
	});

//	changeSessionIndicator(sessionDatagridDivTab2, 1);

})(jQuery);