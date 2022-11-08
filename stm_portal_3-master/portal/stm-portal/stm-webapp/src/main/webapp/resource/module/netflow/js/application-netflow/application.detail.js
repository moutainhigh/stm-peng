var datagridObj_netflow_app_proto = null;
var datagridObj_netflow_app_terminal = null;
var datagridObj_netflow_app_session = null;
var datagridObj_netflow_app_ipg = null;
(function($){
	
	var tab = $('.app-netflow-main-tabs').tabs('getSelected');

	var type = $('.netflow-main').data('apptype');
	var startTime = $('.netflow-main').data('appstartTime');
	var endTime = $('.netflow-main').data('appendTime');
	var timeInterval = $('.netflow-main').data('apptimeInterval');
	
	var app_id = $('.netflow-main').data('app_id');
	
	var rowCount = 5;
	
	oc.ui.combobox({
		selector : tab.find('.app-detail-timeinterval'),
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
		placeholder: null,
		selected : '1hour',
		onSelect : function(d) {
			
			if(d.id == 'custom') {
				tab.find('.app-detail-custom-date').removeClass('hide');
				tab.find('#app_detail_search').removeClass('hide');
			} else {
				tab.find('.app-detail-custom-date').addClass('hide');
				tab.find('#app_detail_search').addClass('hide');

				var data = {
						'onePageRows' : rowCount,
						"app_id" : app_id,
						"sort" : "in_flows",
						"order" : 'desc',
						"showpagination" : false,
						"rowCount":rowCount,
						"recordCount":rowCount,
						"timePerid": d.id,
				};
				tab.find('.application-proto-datagrid').datagrid('load', data);
				tab.find('.application-terminal-datagrid').datagrid('load', data);
				tab.find('.application-session-datagrid').datagrid('load', data);
				tab.find('.application-ipg-datagrid').datagrid('load', data);
			}
		}
	});

	oc.ui.combobox({
		selector: tab.find('.app-detail-indecator'),
		width: 90,
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		placeholder: null,
		onSelect: function(d) {
			changeIndicatorDetail(tab.find('.application-terminal-datagrid'), d.id, 'terminal', 2, false);
			changeIndicatorDetail(tab.find('.application-session-datagrid'), d.id, 'session', 2, true);
			changeIndicatorDetail(tab.find('.application-ipg-datagrid'), d.id, 'ipg', 2, false);
			changeIndicatorDetail(tab.find('.application-proto-datagrid'), d.id, 'proto', 2, false);
		}
	});
	
	//datetimebox
	tab.find('.app-detail-custom-starttime').datetimebox();
	tab.find('.app-detail-custom-endtime').datetimebox();
	
	//search button
	tab.find('#app_detail_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var timeInterval = tab.find('.app-detail-timeinterval').combobox('getValue');
			var startTime = tab.find('.app-detail-custom-starttime').datetimebox('getValue');
			var endTime = tab.find('.app-detail-custom-endtime').datetimebox('getValue');
			
			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
						'starttime': startTime,
						'endtime': endTime,
						'onePageRows' : rowCount,
						"app_id" : app_id,
						"sort" : "in_flows",
						"order" : 'desc',
						"showpagination" : false,
						"rowCount":rowCount,
						"recordCount":rowCount,
						"timePerid": timeInterval
				};
				tab.find('.application-proto-datagrid').datagrid('load', data);
				tab.find('.application-terminal-datagrid').datagrid('load', data);
				tab.find('.application-session-datagrid').datagrid('load', data);
				tab.find('.application-ipg-datagrid').datagrid('load', data);
			}

		}
	});
	
	//export button
	tab.find('#app_detail_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = tab.find('.app-detail-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/two/export/app.htm?type="+type);
		}
	});
	
	//init status
	tab.find('.app-detail-custom-date').addClass('hide');
	tab.find('#app_detail_search').addClass('hide');
	
	if(timeInterval) {
		tab.find('.app-detail-timeinterval').combobox('setValue', timeInterval);
		
		if(timeInterval == 'custom') {
			tab.find('.app-detail-custom-date').removeClass('hide');
			tab.find('#app_detail_search').removeClass('hide');
			
			tab.find('.app-detail-custom-starttime').datetimebox('setValue', startTime);
			tab.find('.app-detail-custom-endtime').datetimebox('setValue', endTime);
		}
	}
	
	if(type) {
		tab.find('.app-detail-indecator').combobox('setValue', type);
	}
	
	
})(jQuery);