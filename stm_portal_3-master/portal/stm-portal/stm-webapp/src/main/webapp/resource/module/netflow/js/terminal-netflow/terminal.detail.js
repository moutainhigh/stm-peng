var datagridObj_netflow_app = null;
var datagridObj_netflow_session = null;
var datagridObj_netflow_tos = null;
var datagridObj_netflow_proto = null;
(function($){

	//隐藏Y轴滚动条
	$('.netflow-main').parents('.panel-body:first').css({'overflow':'hidden'});
	
	var tab = $('.terminal-netflow-main-tabs').tabs('getSelected');
	
	var timeInterval = $('.netflow-main').data('terminaltimeInterval');
	var startTime = $('.netflow-main').data('terminalstartTime');
	var endTime = $('.netflow-main').data('terminalendTime');
	var type = $('.netflow-main').data('terminaltype');
	var terminalIp = $('.netflow-main').data('terminal_name');
	 
	var rowCount = 5;
	
	//timeinterval
	oc.ui.combobox({
		placeholder: null,
		selector: tab.find('.terminal-detail-timeinterval'),
		data:[
			{id:'1hour',name:'最近一小时'},
			{id:'6hour',name:'最近六小时'},
			{id:'1day',name:'最近一天'},
			{id:'7day',name:'最近一周'},
			{id: '30day', name:'最近一个月'},
			{id:'custom',name:'自定义'}
		],
		selected: '1hour',
		onSelect: function(d) {
			
			if(d.id == 'custom') {
				tab.find('.terminal-detail-custom-date').removeClass('hide');
				tab.find('#terminal_detail_search').removeClass('hide');
			} else {
				tab.find('.terminal-detail-custom-date').addClass('hide');
				tab.find('#terminal_detail_search').addClass('hide');

				var data = {
					'onePageRows' : rowCount,
					'sort' : "in_flows",
					'order' : 'desc',
					'showpagination' : false,
					'recordCount' : rowCount,
					"rowCount":rowCount,
					'timePerid': d.id,
					'terminal_ip': terminalIp
				};
				
				tab.find('.terminal-app-datagrid').datagrid('load', data);
				tab.find('.terminal-session-datagrid').datagrid('load', data);
				tab.find('.terminal-tos-datagrid').datagrid('load', data);
				tab.find('.terminal-proto-datagrid').datagrid('load', data);

//				datagridObj_netflow_app.load(data);
//				datagridObj_netflow_session.load(data);
//				datagridObj_netflow_tos.load(data);
//				datagridObj_netflow_proto.load(data);
			}
			
		}
	});
	
	//indicator
	oc.ui.combobox({
		selector: tab.find('.terminal-detail-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		selected: '1',
		placeholder: null,
		onSelect: function(d) {
			changeIndicatorDetail(tab.find('.terminal-app-datagrid'), d.id, 'app', 2, false);
			changeIndicatorDetail(tab.find('.terminal-session-datagrid'), d.id, 'session', 2, true);
			changeIndicatorDetail(tab.find('.terminal-tos-datagrid'), d.id, 'tos', 2, false);
			changeIndicatorDetail(tab.find('.terminal-proto-datagrid'), d.id, 'proto', 2, false);
		}
	});
	
	//datetimebox
	tab.find('.terminal-detail-custom-starttime').datetimebox();
	tab.find('.terminal-detail-custom-endtime').datetimebox();
	
	//search button
	tab.find('#terminal_detail_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var startTime = tab.find('.terminal-detail-custom-starttime').datetimebox('getValue');
			var endTime = tab.find('.terminal-detail-custom-endtime').datetimebox('getValue');
			var timeInterval = tab.find('.terminal-detail-timeinterval').combobox('getValue');
			
			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
						'starttime': startTime,
						'endtime': endTime,
						'onePageRows': rowCount,
						'sort': "in_flows",
						'order': 'desc',
						'showpagination': false,
						'recordCount': rowCount,
						'rowCount': rowCount,
						'timePerid': timeInterval,
						'terminal_ip': terminalIp
				};
				
				tab.find('.terminal-app-datagrid').datagrid('load', data);
				tab.find('.terminal-session-datagrid').datagrid('load', data);
				tab.find('.terminal-tos-datagrid').datagrid('load', data);
				tab.find('.terminal-proto-datagrid').datagrid('load', data);
			}
		}
	});
	
	//export button
	tab.find('#terminal_detail_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = tab.find('.terminal-detail-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/two/export/termainl.htm?type="+type);
		}
	});
	
	//init status
	tab.find('.terminal-detail-custom-date').addClass('hide');
	tab.find('#terminal_detail_search').addClass('hide');
	
	if(timeInterval) {
		tab.find('.terminal-detail-timeinterval').combobox('setValue', timeInterval);
		
		if(timeInterval == 'custom') {
			tab.find('.terminal-detail-custom-date').removeClass('hide');
			tab.find('#terminal_detail_search').removeClass('hide');
			
			tab.find('.terminal-detail-custom-starttime').datetimebox('setValue', startTime);
			tab.find('.terminal-detail-custom-endtime').datetimebox('setValue', endTime);
		}
	}
	
	if(type) {
		tab.find('.terminal-detail-indecator').combobox('setValue', type);
	}

})(jQuery);