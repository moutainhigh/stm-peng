var tabCounter = 0;
(function(){
	
	//生成tab标签页,默认选中第一个标签页
	var domId=oc.util.generateId();
	var tab=$('#interface_netflow_main_tabs').attr('id',domId).tabs({
		select: 0,
		onSelect: function(title, index) {
			if(index == 1) {
				tarDiv.datagrid('reload');
			}
		}
	});

	var curTab = tab;
	var datagridTab2;
	
	var interfaceViewForm = oc.ui.form({
		selector: curTab.find('.interface-list-param-form'),
		combobox:[{
			placeholder: null,
			selector: curTab.find('.interface-view-timeinterval'),
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
					curTab.find('#interface_view_custom_date').removeClass('hide')
				} else {
					curTab.find('#interface_view_custom_date').addClass('hide')
					var data = {
						timePerid: d.id,
						onePageRows: 20,
						rowCount: 20,
						needPagination: true,
						querySize: rowCount
					};
					tarDiv.datagrid('load', data);
				}
			}
		}, {
			selector: '#interface_view_indicator',
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
				changeIfGroupIndicator(tarDiv, d.id);
			}
		}],
		datetimebox:[{
			width: 145,
			selector: '.interface-view-custom-date-starttime'
		}, {
			width: 145,
			selector: '.interface-view-custom-date-endtime'
		}]
	});
	
	//默认不显示自定义时间选择控件
	curTab.find('#interface_view_custom_date').addClass('hide');
	
	var timeInterval = $('.interface-view-timeinterval').combobox('getValue');
	var rowCount = 20;

	var tarDiv = curTab.find('#interface_view_list_datagrid');
	datagridTab2=oc.ui.datagrid({
		queryForm: interfaceViewForm,
		selector: tarDiv,
		url: oc.resource.getUrl('netflow/ifgroup/ifGroupPageSelect.htm'),
		hideRest:true,
		hideSearch:true,
		queryParams: {
			needPagination: true,
			timePerid: timeInterval,
			querySize: rowCount,
			rowCount: rowCount
		},
		pageSize: rowCount,
		octoolbar:{
			left: [interfaceViewForm.selector, {
				iconCls: 'icon-search',
				text:"查询",
				onClick: function(){
			
					var s = curTab.find('.interface-view-custom-date-starttime').datetimebox('getValue');
					var e = curTab.find('.interface-view-custom-date-endtime').datetimebox('getValue');
					var t = curTab.find('.interface-view-timeinterval').combobox('getValue');
					
					if(t == 'custom') {
						var checkMsg = checkCustomTime(s, e);
						if('' != checkMsg) {
							alert(checkMsg);
						}
					}
					
					var search = curTab.find('#ifgrp_b_search').val();
					var data = {
						startTime: s,
						endTime: e,
						timePerid: t,
						onePageRows: rowCount,
						rowCount: rowCount,
						needPagination: true,
						querySize: rowCount
					};
					if(search && search != '') {
						data = $.extend(data, {'name': search});
					}
					tarDiv.datagrid('load', data);
				}
			}],
			right: [{
				text:'导出',
				iconCls:'icon-word interface-view-export',
				onClick:function(){
					top.location =  oc.resource.getUrl('netflow/ifgroup/exportAll.htm?type='+$("#interface_view_indicator").combobox("getValue"));
				}
			}]
		},
		sortOrder: 'desc',
		sortName: 'flowIn',
		columns: [[
		     {field:'name',title:'接口组名称',sortable:false,width:20,align:'center'},
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
	    	 oc.util.ajax({
    			url: oc.resource.getUrl('netflow/ifgroup/getIfIdsByGroupId.htm'),
    			data: {
    				ifGroupId: rowData.ifGroupId
    			},
    			successMsg:null,
    			failureMsg:'加载用户域数据失败！',
    			async:false,
    			success:function(data){
    				var ids = data.data;
    				if(ids && ids != null && ids != '' && data.code == 200) {
    					$('.netflow-main').data('ifId', ids);
    					var deviceGroupName = rowData.name;
    					var tab = $('.interface-netflow-main-tabs').tabs();
    					tab.tabs('add', {
    						title: deviceGroupName.substring(0, 10),
    						selected: true,
    						closable: true,
    						href: 'module/netflow/interface-netflow/interface_detail.html'
    					});
    					
    					var s = curTab.find('.interface-view-custom-date-starttime').datetimebox('getValue');
    					var e = curTab.find('.interface-view-custom-date-endtime').datetimebox('getValue');
    					var t = curTab.find('.interface-view-timeinterval').combobox('getValue');
    					var type = curTab.find('#interface_view_indicator').combobox('getValue');

    					$('.netflow-main').data('timeInterval', t);
    					$('.netflow-main').data('startTime', s);
    					$('.netflow-main').data('endTime', e);
    					$('.netflow-main').data('type', type);
    				}
    			}
    		});
	     },
	     onLoadSuccess: function(data) {
	     }
	});
	
//	changeIfGroupIndicator(tarDiv, 1);

})();