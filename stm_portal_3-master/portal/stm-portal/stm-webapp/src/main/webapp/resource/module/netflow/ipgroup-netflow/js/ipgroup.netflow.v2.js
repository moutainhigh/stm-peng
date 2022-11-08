(function(){

	//生成tab标签页,默认选中第一个标签页
	var domId=oc.util.generateId();
	var tab=$('#ipgroup_netflow_main_tabs').attr('id',domId).tabs({
		select: 0,
		onSelect: function(title, index) {
			if(index == 1) {
				datagridDivTab2.datagrid('reload');
			}
		}
	});
	
	var rowCount = 20;

	var mainDiv = tab.find('.ipgroup-list-main');
	var datagridTab2;
	var ipgroupViewForm = oc.ui.form({
		selector: mainDiv.find('.ipgroup-list-param-form'),
		combobox:[{
			placeholder: null,
			selector: mainDiv.find('.ipgroup-timeinterval'),
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
					mainDiv.find('.ipgroup-custom-time-tab1').removeClass('hide')
				} else {
					mainDiv.find('.ipgroup-custom-time-tab1').addClass('hide')
					
					var data = {
						'timePerid': d.id,
						'onePageRows': rowCount,
						'rowCount': rowCount,
						needPagination: true,
						querySize: rowCount
					};
					datagridDivTab2.datagrid('load', data);
				}
			}
		}, {
			selector: '#ipgroup_indicator',
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
				changeIpGroupIndicator(datagridDivTab2, d.id);
			}
		}],
		datetimebox:[{
			width: 145,
			selector: '.ipgroup-custom-date-starttime-tab1'
		}, {
			width: 145,
			selector: '.ipgroup-custom-date-endtime-tab1'
		}]
	});
	
	//默认不显示自定义时间选择控件
	mainDiv.find('.ipgroup-custom-time-tab1').addClass('hide')
	
	var timeInterval = $('.ipgroup-timeinterval').combobox('getValue');
	var rowCount = 20;

	var datagridDivTab2 = mainDiv.find('#ipgroup_list_datagrid');
	datagridTab2=oc.ui.datagrid({
		queryForm: ipgroupViewForm,
		selector: datagridDivTab2,
		url:oc.resource.getUrl('netflow/ipgroup/ipGroupPageSelect.htm'),
		hideRest:true,
		hideSearch:true,
		queryParams: {
			timePerid: timeInterval,
			rowCount: rowCount,
			needPagination: true,
			querySize: rowCount,
			onePageRows: rowCount,
		},
		pageSize: rowCount,
		octoolbar:{
			left: [ipgroupViewForm.selector, {
				iconCls: 'icon-search',
				text:"查询",
				onClick: function(){
					
					var s = mainDiv.find('.ipgroup-custom-date-starttime-tab1').datetimebox('getValue');
					var e = mainDiv.find('.ipgroup-custom-date-endtime-tab1').datetimebox('getValue');
					var t = mainDiv.find('.ipgroup-timeinterval').combobox('getValue');
					
					if(t == 'custom') {
						var checkMsg = checkCustomTime(s, e);
						if('' != checkMsg) {
							alert(checkMsg);
							return;
						}
					}
					
					var search = mainDiv.find('#ipg_b_search').val();
					var ipgName = undefined;
					if(search && search != '') {
						ipgName = search;
					}
					var data = {
							'startTime': s,
							'endTime': e,
							'timePerid': t,
							'onePageRows': rowCount,
							'rowCount': rowCount,
							'needPagination': true,
							'querySize': rowCount	
					};
					if(ipgName) {
						data = $.extend(data, {'name': ipgName});
					}
					datagridDivTab2.datagrid('load', data);
				}
			}],
			right: [{
				text:'导出',
				iconCls:'icon-word ipgroup-export',
				onClick:function(){
					var type = mainDiv.find('#ipgroup_indicator').combobox('getValue');
					top.location = oc.resource.getUrl("netflow/ipgroup/exportAll.htm?type="+type);
				}
			}]
		},
		sortOrder: 'desc',
		sortName: 'flowIn',
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
	     onClickRow: function(rowIndex, rowData) {
	    	 $('.netflow-main').data('ipGroupId', rowData.ipGroupId);
	    	 var ipgName = rowData.name || '';
	    	 if(ipgName && ipgName.length > 10) {
	    		 ipgName = ipgName.substring(0, 10);
	    	 }
	    	 $('.ipgroup-netflow-main-tabs').tabs('add', {
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
	    	 
	     },
	     onLoadSuccess: function(data) {
	    	 
	     }
	});
	
//	changeIpGroupIndicator(datagridDivTab2, 1);

})();