var datagridObj_netflow_proto = null;
var datagridObj_netflow_terminal = null;
var datagridObj_netflow_session = null;
var datagridObj_netflow_ipg = null;
(function($){

	var mainDiv = $('.device-2-app-main');
	
	//设备详细页面的参数
//	var timePerid = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();
	var startTime = $('.device-app-dlg').data('startTime');
	var endTime = $('.device-app-dlg').data('endTime');
	var timePerid = $('.device-app-dlg').data('timeInterval');
	var type = $('.device-app-dlg').data('type');
	var rowCount = 5;

	oc.ui.combobox({
		placeholder: null,
		selector : mainDiv.find('#device_app_timeInterval'),
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
		value : '1hour',
		onSelect : function(d) {
			if(d.id != 'custom') {
				mainDiv.find('.device-2-app-custom-time').addClass('hide');
				mainDiv.find('#device_2_app_search').addClass('hide');
				
				var data = {
					"startTime": startTime,
					"endTime": endTime,
					"onePageRows" : rowCount,
					"app_id" : $('.device-app-dlg').data('app_id'),
					"sort" : "in_flows",
					"order" : 'desc',
					"showpagination" : false,
					"rowCount":rowCount,
					"recordCount":rowCount,
					"timePerid": d.id,
				};
//				datagridObj_netflow_proto.load(data);
//				datagridObj_netflow_terminal.load(data);
//				datagridObj_netflow_session.load(data);
//				datagridObj_netflow_ipg.load(data);
				
				$('.application-terminal-datagrid').datagrid('load', data);
				$('.application-session-datagrid').datagrid('load', data);
				$('.application-proto-datagrid').datagrid('load', data);
				$('.application-ipg-datagrid').datagrid('load', data);
			} else {
				mainDiv.find('.device-2-app-custom-time').removeClass('hide');
				mainDiv.find('#device_2_app_search').removeClass('hide');
			}
		}
	});
	
	oc.ui.combobox({
		placeholder: null,
		selector: mainDiv.find('.device-2-app-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		selected: '1',
		onSelect: function(d) {
			
			changeIndicatorDetail($('.application-terminal-datagrid'), d.id, 'terminal', 2, false);
			changeIndicatorDetail($('.application-session-datagrid'), d.id, 'session', 2, true);
			changeIndicatorDetail($('.application-proto-datagrid'), d.id, 'proto', 2, false);
			changeIndicatorDetail($('.application-ipg-datagrid'), d.id, 'ipg', 2, false);

		}
	});
	
	
	mainDiv.find('.device-2-app-custom-starttime').datetimebox();
	mainDiv.find('.device-2-app-custom-endtime').datetimebox();
	
	mainDiv.find('.device-2-app-custom-time').addClass('hide');
	mainDiv.find('#device_2_app_search').addClass('hide');
	
	//设置详细页面传递过来的参数
	if(type) {
		mainDiv.find('.device-2-app-indecator').combobox('setValue', type);
	}
	if(timePerid) {
		mainDiv.find('.#device_app_timeInterval').combobox('setValue', timePerid);
		if(timePerid == 'custom') {
			mainDiv.find('.device-2-app-custom-time').removeClass('hide');
			mainDiv.find('#device_2_app_search').removeClass('hide');
			
			mainDiv.find('.device-2-app-custom-starttime').datetimebox('setValue', startTime);
			mainDiv.find('.device-2-app-custom-endtime').datetimebox('setValue', endTime);
		}
	}

	//导出
	$('#device_three_app').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('.device-2-app-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/three/export/app.htm?type="+type);
		}
	});

	//搜索
	mainDiv.find('#device_2_app_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var st = mainDiv.find('.device-2-app-custom-starttime').datetimebox('getValue');
			var et = mainDiv.find('.device-2-app-custom-endtime').datetimebox('getValue');
			var t = mainDiv.find('#device_app_timeInterval').combobox('getValue');
			
			var checkMsg = checkCustomTime(st, et);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
						'starttime': st,
						'endtime': et,
						'onePageRows' : rowCount,
						"app_id" : $('.device-app-dlg').data('app_id'),
						"sort" : "in_flows",
						"order" : 'desc',
						"showpagination" : false,
						"rowCount":rowCount,
						"recordCount":rowCount,
						"timePerid": t,
				};
				
				$('.application-terminal-datagrid').datagrid('load', data);
				$('.application-session-datagrid').datagrid('load', data);
				$('.application-proto-datagrid').datagrid('load', data);
				$('.application-ipg-datagrid').datagrid('load', data);
			}
		}
	});
	
})(jQuery);