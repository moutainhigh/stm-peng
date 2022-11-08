var datagridObj_netflow_ipg_app = null;
var datagridObj_netflow_ipg_proto = null;
var datagridObj_netflow_ipg_tos = null;
var datagridObj_netflow_ipg_terminal = null;
var datagridObj_netflow_ipg_session = null;
(function($){
	
	//隐藏Y轴滚动条
	$('.netflow-main').parents('.panel-body:first').css({'overflow':'hidden'});

	var tab = $('.ipgroup-netflow-main-tabs').tabs('getSelected');
	
	 var timeInterval = $('.netflow-main').data('ipgTimeInterval');
	 var startTime = $('.netflow-main').data('ipgStartTime');
	 var endTime = $('.netflow-main').data('ipgEndTime');
	 var type = $('.netflow-main').data('ipgType');
	 
	 var rowCount = 5;

	oc.ui.combobox({
		selector : tab.find('.ipgroup-detail-timeinterval'),
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
		selected : '1hour',
		placeholder: null,
		onSelect : function(d) {
			
			if(d.id == 'custom') {
				tab.find('.ipgroup-detail-custom-date').removeClass('hide');
				tab.find('#ipgroup_detail_search').removeClass('hide');
			} else {
				tab.find('.ipgroup-detail-custom-date').addClass('hide');
				tab.find('#ipgroup_detail_search').addClass('hide');
				
				var startTime = tab.find('.ipgroup-detail-custom-starttime').datetimebox('getValue');
				var endTime = tab.find('.ipgroup-detail-custom-endtime').datetimebox('getValue');
				
				var data = {
//					'startTime': startTime,
//					'endTime': endTime,
					'onePageRows' : rowCount,
					"sort" : "flowIn",
					"order" : 'desc',
					"needPagination":false,
					"showpagination" : false,
					"recordCount" : rowCount,
					"querySize" : rowCount,
					"rowCount":rowCount,
					"ipGroupId":$('.netflow-main').data('ipGroupId'),
					'timePerid': d.id
				};
				
				tab.find('.ipgroup-app-datagrid-selector').datagrid('load', data);
				tab.find('.ipgroup-session-datagrid-selector').datagrid('load', data);
				tab.find('.ipgroup-terminal-datagrid-selector').datagrid('load', data);
				tab.find('.ipgroup-proto-datagrid-selector').datagrid('load', data);
				tab.find('.ipgroup-tos-datagrid-selector').datagrid('load', data);
			}
			
		}
	});

	oc.ui.combobox({
		selector : tab.find('.ipgroup-detail-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		placeholder: null,
		selected: '1',
		onSelect : function(d) {
			
			changeIndicatorDetail(tab.find('.ipgroup-app-datagrid-selector'), d.id, 'app', 1, false);
			changeIndicatorDetail(tab.find('.ipgroup-session-datagrid-selector'), d.id, 'session', 1, true);
			changeIndicatorDetail(tab.find('.ipgroup-terminal-datagrid-selector'), d.id, 'terminal', 1, false);
			changeIndicatorDetail(tab.find('.ipgroup-proto-datagrid-selector'), d.id, 'proto', 1, false);
			changeIndicatorDetail(tab.find('.ipgroup-tos-datagrid-selector'), d.id, 'tos', 1, false);
			
		}
	});
	
	//datetimebox init
	tab.find('.ipgroup-detail-custom-starttime').datetimebox();
	tab.find('.ipgroup-detail-custom-endtime').datetimebox();
	
	//search button
	tab.find('#ipgroup_detail_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			//TODO
			var startTime = tab.find('.ipgroup-detail-custom-starttime').datetimebox('getValue');
			var endTime = tab.find('.ipgroup-detail-custom-endtime').datetimebox('getValue');
			var t = tab.find('.ipgroup-detail-timeinterval').combobox('getValue');

			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
					'startTime': startTime,
					'endTime': endTime,
					"sort" : "flowIn",
					"order" : 'desc',
					"needPagination":false,
					"querySize" : rowCount,
					"rowCount":rowCount,
					"ipGroupId":$('.netflow-main').data('ipGroupId'),
					'timePerid': t
				};
				tab.find('.ipgroup-app-datagrid-selector').datagrid('load', data);
				tab.find('.ipgroup-session-datagrid-selector').datagrid('load', data);
				tab.find('.ipgroup-terminal-datagrid-selector').datagrid('load', data);
				tab.find('.ipgroup-proto-datagrid-selector').datagrid('load', data);
				tab.find('.ipgroup-tos-datagrid-selector').datagrid('load', data);
			}
		}
	});

	//export button
	tab.find('#ipgroup_detail_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = tab.find('.ipgroup-detail-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/two/export/ipGroup.htm?type="+type);
		}
	});

	//init page control status
	tab.find('.ipgroup-detail-custom-date').addClass('hide');
	tab.find('#ipgroup_detail_search').addClass('hide');
	
	if(timeInterval) {
		tab.find('.ipgroup-detail-timeinterval').combobox('setValue', timeInterval);
		
		if(timeInterval == 'custom') {
			tab.find('.ipgroup-detail-custom-date').removeClass('hide');
			tab.find('#ipgroup_detail_search').removeClass('hide');
			
			tab.find('.ipgroup-detail-custom-starttime').datetimebox('setValue', startTime);
			tab.find('.ipgroup-detail-custom-endtime').datetimebox('setValue', endTime);
		}
	}
	
	if(type) {
		tab.find('.ipgroup-detail-indecator').combobox('setValue', type);
	}
	
	tab.find('.r-flip').click(function() {
		$(this).hide();
		tab.find("#first_page").css({
			position: 'relative'
		}).animate({
			left: '-100%'
		});
		tab.find('.l-flip').show();
	});
	
	tab.find('.l-flip').click(function() {
		$(this).hide();
		tab.find('#first_page').css({
			position: 'relative'
		}).animate({
			left: '0px'
		});
		tab.find('.r-flip').show();
	});

})(jQuery);