var datagridObj_netflow_app = null;
var datagridObj_netflow_proto = null;
var datagridObj_netflow_tos = null;
var datagridObj_netflow_terminal = null;
var datagridObj_netflow_nexthop = null;
var datagridObj_netflow_session = null;
var datagridObj_netflow_ipg = null;
(function($) {

	// 隐藏Y轴滚动条
	$('.netflow-main').parents('.panel-body:first').css({
		'overflow' : 'hidden'
	});
	
	var curTab = $('.device-netflow-main-tabs').tabs('getSelected');
	
	var displayCount = 5;

	oc.ui.combobox({
		selector : curTab.find('.device-detail-timeinterval'),
		placeholder: null,
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
		onSelect : function(d) {
			
			if(d.id == 'custom') {
				curTab.find('.device-detail-custom-date').removeClass('hide');
				curTab.find('#device_detail_search').removeClass('hide');
			} else {
				curTab.find('.device-detail-custom-date').addClass('hide');
				curTab.find('#device_detail_search').addClass('hide');
				
				var indictor = curTab.find('.device-detail-indecator').combobox('getValue');
				var data = {
						'onePageRows' : displayCount,
						"deviceIp" : $('.netflow-main').data('deviceIp'),
						"sort" : "in_flows",
						"order" : 'desc',
						"needPagination":false,
						"showpagination" : false,
						"recordCount" : displayCount,
						"querySize" : displayCount,
						"rowCount":displayCount,
						"recordCount":displayCount,
						'timePerid': d.id
				};
				datagridObj_netflow_app.load(data);
				datagridObj_netflow_proto.load(data);
				datagridObj_netflow_tos.load(data);
				datagridObj_netflow_terminal.load(data);
				datagridObj_netflow_nexthop.load(data);
				datagridObj_netflow_session.load(data);
				datagridObj_netflow_ipg.load(data);
			}
			
		}
	});

	oc.ui.combobox({
		selector : curTab.find('.device-detail-indecator'),
		placeholder: null,
		data : [{
			id : 1,
			name : '流量'
		}, {
			id: 2,
			name: '包数'
		}, {
			id: 3,
			name: '连接数'
		}/*, {
			id: 4,
			name: '带宽使用率'
		}*/],
		selected: '1',
		onSelect : function(d) {
			
			changeIndicatorDetail(curTab.find('.device-terminal-datagrid'), d.id, 'terminal', 2, false);
			changeIndicatorDetail(curTab.find('.device-app-datagrid'), d.id, 'app', 1, false);
			changeIndicatorDetail(curTab.find('.device-session-datagrid'), d.id, 'session', 2, true);
			changeIndicatorDetail(curTab.find('.device-proto-datagrid'), d.id, 'proto', 1, false);
			changeIndicatorDetail(curTab.find('.device-nexthop-datagrid'), d.id, 'nexthop', 2, false);
			changeIndicatorDetail(curTab.find('.device-tos-datagrid'), d.id, 'tos', 1, false);
			changeIndicatorDetail(curTab.find('.device-ipgroup-datagrid'), d.id, 'ipg', 2, false);
		}
	});
	
	curTab.find('.device-detail-custom-starttime').datetimebox();
	curTab.find('.device-detail-custom-endtime').datetimebox();

	//如果不是自定义就隐藏时间选择控件
	curTab.find('.device-detail-custom-date').addClass('hide');
	curTab.find('#device_detail_search').addClass('hide');

	//获取主页面选择的时间，指标
//	var mainPageTimePerid = $('.device-timeinterval').combobox('getValue');
//	var mainPageIndicator = $('#device_indicator').combobox('getValue');
	
	var mainPageTimePerid = $('.netflow-main').data('timeInterval');
	var stime = $('.netflow-main').data('startTime');
	var etime = $('.netflow-main').data('endTime');
	var mainPageIndicator = $('.netflow-main').data('type');

	//设置详细页面的值为主页面选择的值
	if(mainPageTimePerid) {
		curTab.find('.device-detail-timeinterval').combobox('setValue', mainPageTimePerid);
		
		if(mainPageTimePerid == 'custom') {
			curTab.find('.device-detail-custom-date').removeClass('hide');
			curTab.find('#device_detail_search').removeClass('hide');		
			
			//获取主页面custom类型的startTime与endTime
//			var stime = $('.device-custom-date-starttime-tab1').datetimebox('getValue');
//			var etime = $('.device-custom-date-endtime-tab1').datetimebox('getValue');
			curTab.find('.device-detail-custom-starttime').datetimebox('setValue', stime);
			curTab.find('.device-detail-custom-endtime').datetimebox('setValue', etime);
		}
	}
	if(mainPageIndicator) {
		if(mainPageIndicator > 3) {
			mainPageIndicator = 1;
		}
		curTab.find('.device-detail-indecator').combobox('setValue', mainPageIndicator);
	}
	
	//导出按钮
	curTab.find("#device_detail_export").linkbutton('RenderLB', {
		iconCls: 'icon-word', 
		onClick: function() {
			var type = curTab.find('.device-detail-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/devices/exportTwo.htm?type="+type);
		}
	});
	
	//查询按钮
	curTab.find("#device_detail_search").linkbutton('RenderLB', {
		iconCls: 'icon-search', 
		onClick: function() {
			var t = curTab.find('.device-detail-timeinterval').combobox('getValue');
			if(t == 'custom') {
				
				var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
				var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
				var msg = checkCustomTime(stime, etime);
				if('' != msg) {
					alert(msg);
					return;
				}
				
				var data = {
						'onePageRows' : displayCount,
						"deviceIp" : $('.netflow-main').data('deviceIp'),
						"sort" : "in_flows",
						"order" : 'desc',
						"needPagination":false,
						"showpagination" : false,
						"recordCount" : displayCount,
						"querySize" : displayCount,
						"rowCount":displayCount,
						"recordCount":displayCount,
						'timePerid': t,
						'startTime': stime,
						'endTime': etime,
						'starttime': stime,
						'endtime': etime
				};
				
				datagridObj_netflow_app.load(data);
				datagridObj_netflow_proto.load(data);
				datagridObj_netflow_tos.load(data);
				datagridObj_netflow_terminal.load(data);
				datagridObj_netflow_nexthop.load(data);
				datagridObj_netflow_session.load(data);
				datagridObj_netflow_ipg.load(data);
			}
		}
	});
	
	var index = $('.device-netflow-main-tabs').tabs('getTabIndex',curTab);
	
	curTab.find('.r-flip').click(function() {
//		curTab.attr('flag', 1);
		$(this).attr('tabindex', index);
		$(this).hide();
		curTab.find("#first_page").css({
			position : 'relative'
		}).animate({
			left : '-100%'
		});
		curTab.find('.l-flip').show();
	});

	curTab.find('.l-flip').click(function() {
//		curTab.attr('flag', 2);
		$(this).hide();
		curTab.find('#first_page').css({
			position : 'relative'
		}).animate({
			left : '0px'
		});
		curTab.find('.r-flip').show();
	});
	
	//终端窗口最大化
	curTab.find('.terminal-more').click(function() {
		$('<div class="device-terminal-more-wrapper"></div>').dialog({
			title: '流量分析-设备-终端',
			width: 1200,    
		    height: 605,    
		    top:'5%',
			modal: true,
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/device-netflow/terminal_more.html")
		});
		var stime = curTab.find('.device-detail-custom-starttime').datetimebox('getValue');
		var etime = curTab.find('.device-detail-custom-endtime').datetimebox('getValue');
		$('.device-terminal-more-wrapper').data('starttime', stime);
		$('.device-terminal-more-wrapper').data('endtime', etime);
		
		var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.device-detail-indecator').combobox('getValue');
		$('.device-terminal-more-wrapper').data('timeInterval', timeInterval);
		$('.device-terminal-more-wrapper').data('type', type);
		
		
	});
	
	//应用窗口最大化
	curTab.find('.app-more').click(function() {
		$('<div class="device-app-more-wrapper"></div>').dialog({
			title: '流量分析-设备-应用',
			width: 1200,    
		    height: 605,    
		    top:'5%',
			modal: true,
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/device-netflow/app_more.html")
		});

		var stime = $('.device-detail-custom-starttime').datetimebox('getValue');
		var etime = $('.device-detail-custom-endtime').datetimebox('getValue');
		$('.device-app-more-wrapper').data('starttime', stime);
		$('.device-app-more-wrapper').data('endtime', etime);
		
		var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.device-detail-indecator').combobox('getValue');
		$('.device-app-more-wrapper').data('timeInterval', timeInterval);
		$('.device-app-more-wrapper').data('type', type);
	});

	//会话窗口最大化
	curTab.find('.session-more').click(function() {
		$('<div class="device-session-more-wrapper"></div>').dialog({
			title: '流量分析-设备-会话',
			width: 1200,    
		    height: 605,    
		    top:'5%',
			modal: true,
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/device-netflow/session_more.html")
		});
		var stime = $('.device-detail-custom-starttime').datetimebox('getValue');
		var etime = $('.device-detail-custom-endtime').datetimebox('getValue');
		$('.device-session-more-wrapper').data('starttime', stime);
		$('.device-session-more-wrapper').data('endtime', etime);
		
		var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.device-detail-indecator').combobox('getValue');
		$('.device-session-more-wrapper').data('timeInterval', timeInterval);
		$('.device-session-more-wrapper').data('type', type);

	});

	//协议窗口最大化
	curTab.find('.proto-more').click(function() {
		$('<div class="device-proto-more-wrapper"></div>').dialog({
			title: '流量分析-设备-协议',
			width: 1200,    
		    height: 605,    
		    top:'5%',
			modal: true,
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/device-netflow/proto_more.html")
		});
		var stime = $('.device-detail-custom-starttime').datetimebox('getValue');
		var etime = $('.device-detail-custom-endtime').datetimebox('getValue');
		$('.device-proto-more-wrapper').data('starttime', stime);
		$('.device-proto-more-wrapper').data('endtime', etime);
		
		var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.device-detail-indecator').combobox('getValue');
		$('.device-proto-more-wrapper').data('timeInterval', timeInterval);
		$('.device-proto-more-wrapper').data('type', type);

	});

	//下一跳窗口最大化
	curTab.find('.nexthop-more').click(function() {
		$('<div class="device-nexthop-more-wrapper"></div>').dialog({
			title: '流量分析-设备-下一跳',
			width: 1200,    
		    height: 605,    
		    top:'5%',
			modal: true,
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/device-netflow/nexthop_more.html")
		});
		var stime = $('.device-detail-custom-starttime').datetimebox('getValue');
		var etime = $('.device-detail-custom-endtime').datetimebox('getValue');
		$('.device-nexthop-more-wrapper').data('starttime', stime);
		$('.device-nexthop-more-wrapper').data('endtime', etime);
		
		var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.device-detail-indecator').combobox('getValue');
		$('.device-nexthop-more-wrapper').data('timeInterval', timeInterval);
		$('.device-nexthop-more-wrapper').data('type', type);

	});

	//tos窗口最大化
	curTab.find('.tos-more').click(function() {
		$('<div class="device-tos-more-wrapper"></div>').dialog({
			title: '流量分析-设备-Tos',
			width: 1200,    
		    height: 605,    
		    top:'5%',
			modal: true,
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/device-netflow/tos_more.html")
		});
		var stime = $('.device-detail-custom-starttime').datetimebox('getValue');
		var etime = $('.device-detail-custom-endtime').datetimebox('getValue');
		$('.device-tos-more-wrapper').data('starttime', stime);
		$('.device-tos-more-wrapper').data('endtime', etime);
		
		var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.device-detail-indecator').combobox('getValue');
		$('.device-tos-more-wrapper').data('timeInterval', timeInterval);
		$('.device-tos-more-wrapper').data('type', type);

	});

	//IP分组窗口最大化
	curTab.find('.ipgroup-more').click(function() {
		$('<div class="device-ipgroup-more-wrapper"></div>').dialog({
			title: '流量分析-设备-IP分组',
			width: 1200,    
		    height: 605,    
		    top:'5%',
			modal: true,
			draggable: false,
			resizable: false,
			href: oc.resource.getUrl("resource/module/netflow/device-netflow/ipgroup_more.html")
		});
		var stime = $('.device-detail-custom-starttime').datetimebox('getValue');
		var etime = $('.device-detail-custom-endtime').datetimebox('getValue');
		$('.device-ipgroup-more-wrapper').data('starttime', stime);
		$('.device-ipgroup-more-wrapper').data('endtime', etime);
		
		var timeInterval = curTab.find('.device-detail-timeinterval').combobox('getValue');
		var type = curTab.find('.device-detail-indecator').combobox('getValue');
		$('.device-ipgroup-more-wrapper').data('timeInterval', timeInterval);
		$('.device-ipgroup-more-wrapper').data('type', type);

	});

})(jQuery);

function getTimePerid() {
	return $('.device-detail-timeinterval').combobox('getValue');
}

function getSETime() {
	var s = $('.device-detail-custom-starttime').datetimebox('getValue');
	var e = $('.device-detail-custom-endtime').datetimebox('getValue');
	return [s, e];
}

function getType() {
	return $('.device-detail-indecator').combobox('getValue');
}