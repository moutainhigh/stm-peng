var datagridObj_netflow_app = null;
var datagridObj_netflow_proto = null;
var datagridObj_netflow_tos = null;
var datagridObj_netflow_terminal = null;
var datagridObj_netflow_nexthop = null;
var datagridObj_netflow_session = null;
var datagridObj_netflow_ipg = null;
(function($){
	//隐藏Y轴滚动条
	$('.netflow-main').parents('.panel-body:first').css({'overflow':'hidden'});
	
	var curTab = $('.interface-netflow-main-tabs').tabs('getSelected');
	var rowCount = 5;
	
	oc.ui.combobox({
		selector : '.interface-detail-timeinterval',
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
				curTab.find('.interface-detail-custom-date').removeClass('hide');
				curTab.find('#interface_detail_search').removeClass('hide');
			} else {
				curTab.find('.interface-detail-custom-date').addClass('hide');
				curTab.find('#interface_detail_search').addClass('hide');
				
				var startTime = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
				var endTime = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');

				
				var data = {
					'onePageRows' : rowCount,
					"sort" : "in_flows",
					"order" : 'desc',
					"needPagination":false,
					"showpagination" : false,
					"recordCount" : rowCount,
					"querySize" : rowCount,
					"rowCount":rowCount,
					"recordCount":rowCount,
					"ifId":$('.netflow-main').data('ifId'),
					'timePerid': d.id
				};
				curTab.find('.interface-terminal-datagrid').datagrid('load', data);
				curTab.find('.interface-app-datagrid').datagrid('load', data);
				curTab.find('.interface-session-datagrid').datagrid('load', data);
				curTab.find('.interface-proto-datagrid').datagrid('load', data);
				curTab.find('.interface-nexthop-datagrid').datagrid('load', data);
				curTab.find('.interface-tos-datagrid').datagrid('load', data);
				curTab.find('.interface-ipgroup-datagrid').datagrid('load', data);
			}
		}
	});

	oc.ui.combobox({
		selector : '.interface-detail-indecator',
		data : [{
			id : 1,
			name : '流量'
		}, {
			id: 2,
			name: '包数'
		}, {
			id: 3,
			name: '连接数'
		}],
		selected: '1',
		placeholder: null,
		onSelect : function(d) {
			changeIndicatorDetail(curTab.find('.interface-terminal-datagrid'), d.id, 'terminal', 1, false);
			changeIndicatorDetail(curTab.find('.interface-app-datagrid'), d.id, 'app', 1, false);
			changeIndicatorDetail(curTab.find('.interface-session-datagrid'), d.id, 'session', 1, true);
			changeIndicatorDetail(curTab.find('.interface-proto-datagrid'), d.id, 'proto', 1, false);
			changeIndicatorDetail(curTab.find('.interface-nexthop-datagrid'), d.id, 'nexthop', 1, false);
			changeIndicatorDetail(curTab.find('.interface-tos-datagrid'), d.id, 'tos', 1, false);
			changeIndicatorDetail(curTab.find('.interface-ipgroup-datagrid'), d.id, 'ipg', 1, false);
		}
	});
	
	curTab.find('#interface_detail_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
			var startTime = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
			var endTime = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');

			var checkMsg = checkCustomTime(startTime, endTime);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
						'onePageRows' : rowCount,
						"sort" : "in_flows",
						"order" : 'desc',
						"needPagination":false,
						"showpagination" : false,
						"recordCount" : rowCount,
						"querySize" : rowCount,
						"rowCount":rowCount,
						"recordCount":rowCount,
						'startTime' : startTime,
						'endTime' : endTime,
						"ifId":$('.netflow-main').data('ifId'),
						'timePerid': timeInterval
				};
				curTab.find('.interface-terminal-datagrid').datagrid('load', data);
				curTab.find('.interface-app-datagrid').datagrid('load', data);
				curTab.find('.interface-session-datagrid').datagrid('load', data);
				curTab.find('.interface-proto-datagrid').datagrid('load', data);
				curTab.find('.interface-nexthop-datagrid').datagrid('load', data);
				curTab.find('.interface-tos-datagrid').datagrid('load', data);
				curTab.find('.interface-ipgroup-datagrid').datagrid('load', data);
			}
		}
	});
	
	curTab.find('#interface_detail_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = curTab.find('.interface-detail-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/two/export/interface.htm?type="+type);
		}
	});
	
	curTab.find('.interface-detail-custom-starttime').datetimebox();
	curTab.find('.interface-detail-custom-endtime').datetimebox();

	//如果不是自定义就隐藏时间选择控件
	curTab.find('.interface-detail-custom-date').addClass('hide');
	curTab.find('#interface_detail_search').addClass('hide');

	//得到主页的参数值，并赋值给详细页面
//	var parentTimeInterval = $('.interface-timeinterval').combobox('getValue');
//	var parentType = $('#interface_indicator').combobox('getValue');
	
	var parentTimeInterval = $('.netflow-main').data('timeInterval');
	var st = $('.netflow-main').data('startTime');
	var et = $('.netflow-main').data('endTime');
	var parentType = $('.netflow-main').data('type');
	
	if(parentTimeInterval) {
		curTab.find('.interface-detail-timeinterval').combobox('setValue', parentTimeInterval);
		if(parentTimeInterval == 'custom') {
			curTab.find('.interface-detail-custom-date').removeClass('hide');
			curTab.find('#interface_detail_search').removeClass('hide');
			
//			var st = mainDiv.find('.interface-custom-date-starttime-tab1').datetimebox('getValue');
//			var et = mainDiv.find('.interface-custom-date-endtime-tab1').datetimebox('getValue');
			curTab.find('.interface-detail-custom-starttime').datetimebox('setValue', st);
			curTab.find('.interface-detail-custom-endtime').datetimebox('setValue', et);
		}
	}
	if(parentType) {
		curTab.find('.interface-detail-indecator').combobox('setValue', parentType);
	}
	
	curTab.find('.r-flip').click(function() {
		$(this).hide();
		curTab.find("#first_page").css({
			position: 'relative'
		}).animate({
			left: '-100%'
		});
		curTab.find('.l-flip').show();
	});
	
	curTab.find('.l-flip').click(function() {
		$(this).hide();
		curTab.find('#first_page').css({
			position: 'relative'
		}).animate({
			left: '0px'
		});
		curTab.find('.r-flip').show();
	});
	
	//终端窗口最大化
	curTab.find('.if-terminal-more').click(function() {
		$('<div class="interface-terminal-more-wrapper"></div>').dialog({
			title: '流量分析-接口-终端',
			width: 1200,    
		    height: 605,    
			top: '5%',
			modal: true,
			position:'center',
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/interface-netflow/terminal_more.html")
		});
		var st = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
		var et = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
		$('.interface-terminal-more-wrapper').data('starttime', st);
		$('.interface-terminal-more-wrapper').data('endtime', et);

		var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.interface-detail-indecator').combobox('getValue');
		$('.interface-terminal-more-wrapper').data('timeInterval', timeInterval);
		$('.interface-terminal-more-wrapper').data('type', type);
	});
	
	//应用窗口最大化
	curTab.find('.if-app-more').click(function() {
		$('<div class="interface-app-more-wrapper"></div>').dialog({
			title: '流量分析-接口-应用',
			width: 1200,    
		    height: 605,    
			top: '5%',
			modal: true,
			position:'center',
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/interface-netflow/app_more.html")
		});
		
		var st = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
		var et = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
		$('.interface-app-more-wrapper').data('starttime', st);
		$('.interface-app-more-wrapper').data('endtime', et);

		var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.interface-detail-indecator').combobox('getValue');
		$('.interface-app-more-wrapper').data('timeInterval', timeInterval);
		$('.interface-app-more-wrapper').data('type', type);

	});

	//会话窗口最大化
	curTab.find('.if-session-more').click(function() {
		$('<div class="interface-session-more-wrapper"></div>').dialog({
			title: '流量分析-接口-会话',
			width: 1200,    
		    height: 605,    
			top: '5%',
			modal: true,
			position:'center',
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/interface-netflow/session_more.html")
		});
		var st = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
		var et = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
		$('.interface-session-more-wrapper').data('starttime', st);
		$('.interface-session-more-wrapper').data('endtime', et);

		var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.interface-detail-indecator').combobox('getValue');
		$('.interface-session-more-wrapper').data('timeInterval', timeInterval);
		$('.interface-session-more-wrapper').data('type', type);
	});

	//协议窗口最大化
	curTab.find('.if-proto-more').click(function() {
		$('<div class="interface-proto-more-wrapper"></div>').dialog({
			title: '流量分析-接口-协议',
			width: 1200,    
		    height: 605,    
			top: '5%',
			modal: true,
			position:'center',
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/interface-netflow/proto_more.html")
		});
		var st = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
		var et = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
		$('.interface-proto-more-wrapper').data('starttime', st);
		$('.interface-proto-more-wrapper').data('endtime', et);

		var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.interface-detail-indecator').combobox('getValue');
		$('.interface-proto-more-wrapper').data('timeInterval', timeInterval);
		$('.interface-proto-more-wrapper').data('type', type);
	});

	//下一跳窗口最大化
	curTab.find('.if-nexthop-more').click(function() {
		$('<div class="interface-nexthop-more-wrapper"></div>').dialog({
			title: '流量分析-接口-下一跳',
			width: 1200,    
		    height: 605,    
			top: '5%',
			modal: true,
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/interface-netflow/nexthop_more.html")
		});
		var st = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
		var et = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
		$('.interface-nexthop-more-wrapper').data('starttime', st);
		$('.interface-nexthop-more-wrapper').data('endtime', et);

		var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.interface-detail-indecator').combobox('getValue');
		$('.interface-nexthop-more-wrapper').data('timeInterval', timeInterval);
		$('.interface-nexthop-more-wrapper').data('type', type);
	});

	//tos窗口最大化
	curTab.find('.if-tos-more').click(function() {
		$('<div class="interface-tos-more-wrapper"></div>').dialog({
			title: '流量分析-接口-Tos',
			width: 1200,    
		    height: 605,    
			top: '5%',
			modal: true,
			position:'center',
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/interface-netflow/tos_more.html")
		});
		var st = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
		var et = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
		$('.interface-tos-more-wrapper').data('starttime', st);
		$('.interface-tos-more-wrapper').data('endtime', et);

		var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.interface-detail-indecator').combobox('getValue');
		$('.interface-tos-more-wrapper').data('timeInterval', timeInterval);
		$('.interface-tos-more-wrapper').data('type', type);
	});

	//IP分组窗口最大化
	curTab.find('.if-ipgroup-more').click(function() {
		$('<div class="interface-ipgroup-more-wrapper"></div>').dialog({
			title: '流量分析-接口-IP分组',
			width: 1200,    
		    height: 605,    
			top: '5%',
			modal: true,
			position:'center',
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/interface-netflow/ipgroup_more.html")
		});
		var st = curTab.find('.interface-detail-custom-starttime').datetimebox('getValue');
		var et = curTab.find('.interface-detail-custom-endtime').datetimebox('getValue');
		$('.interface-ipgroup-more-wrapper').data('starttime', st);
		$('.interface-ipgroup-more-wrapper').data('endtime', et);

		var timeInterval = curTab.find('.interface-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.interface-detail-indecator').combobox('getValue');
		$('.interface-ipgroup-more-wrapper').data('timeInterval', timeInterval);
		$('.interface-ipgroup-more-wrapper').data('type', type);
	});

})(jQuery);