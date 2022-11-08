var datagridObj_netflow_terminal_app = null;
var datagridObj_netflow_terminal_session = null;
var datagridObj_netflow_terminal_tos = null;
var datagridObj_netflow_terminal_proto = null;
(function($){

	//隐藏Y轴滚动条
	$('.netflow-main').parents('.panel-body:first').css({'overflow':'hidden'});
	
	//=============================
	var mainDiv = $('.device-2-terminal-main');

	//设备详细页面的参数
//	var timePerid = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();

	var startTime = $('.device-terminal').data('startTime');
	var endTime = $('.device-terminal').data('endTime');
	var timePerid = $('.device-terminal').data('timeInterval');
	var type = $('.device-terminal').data('type');
	
	var rowCount = 5;

	oc.ui.combobox({
		placeholder: null,
		selector : '#device_2_terminal_timeInterval',
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
				mainDiv.find('.device-2-terminal-custom-time').addClass('hide');
				mainDiv.find('#device_2_terminal_search').addClass('hide');
				
				var data = {
						'starttime': startTime,
						'endtime': endTime,
						'onePageRows' : rowCount,
						'sort' : "in_flows",
						'order' : 'desc',
						'showpagination' : false,
						'recordCount' : rowCount,
						"rowCount":rowCount,
						'timePerid': d.id,
						'terminal_ip': $('.device-terminal').data('terminal_ip')
				};
				
				$('.terminal-app-datagrid').datagrid('load', data);
				$('.terminal-session-datagrid').datagrid('load', data);
				$('.terminal-tos-datagrid').datagrid('load', data);
				$('.terminal-proto-datagrid').datagrid('load', data);
			} else {
				mainDiv.find('.device-2-terminal-custom-time').removeClass('hide');
				mainDiv.find('#device_2_terminal_search').removeClass('hide');
			}
		}
	});
	
	oc.ui.combobox({
		placeholder: null,
		selector: mainDiv.find('.device-2-terminal-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		selected: '1',
		onSelect: function(d) {
			
			changeIndicatorDetail($('.terminal-tos-datagrid'), d.id, 'tos', 2, false);
			changeIndicatorDetail($('.terminal-session-datagrid'), d.id, 'session', 2, true);
			changeIndicatorDetail($('.terminal-proto-datagrid'), d.id, 'proto', 2, false);
			changeIndicatorDetail($('.terminal-app-datagrid'), d.id, 'app', 2, false);

		}
	});
	
	mainDiv.find('.device-2-terminal-custom-starttime').datetimebox();
	mainDiv.find('.device-2-terminal-custom-endtime').datetimebox();
	
	mainDiv.find('.device-2-terminal-custom-time').addClass('hide');
	mainDiv.find('#device_2_terminal_search').addClass('hide');
	
	//设置详细页面传递过来的参数
	if(timePerid) {
		mainDiv.find('#device_2_terminal_timeInterval').combobox('setValue', timePerid);
		if(timePerid == 'custom') {
			mainDiv.find('.device-2-terminal-custom-time').removeClass('hide');
			mainDiv.find('#device_2_terminal_search').removeClass('hide');
			
			mainDiv.find('.device-2-terminal-custom-starttime').datetimebox('setValue', startTime);
			mainDiv.find('.device-2-terminal-custom-endtime').datetimebox('setValue', endTime);
		}
	}
	if(type) {
		mainDiv.find('.device-2-terminal-indecator').combobox('setValue', type);
	}

	//导出
	mainDiv.find('#device_2_terminal_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('.device-2-terminal-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/three/export/terminal.htm?type="+type);
		}
	});

	//搜索
	mainDiv.find('#device_2_terminal_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var st = mainDiv.find('.device-2-terminal-custom-starttime').datetimebox('getValue');
			var et = mainDiv.find('.device-2-terminal-custom-endtime').datetimebox('getValue');
			var t = mainDiv.find('#device_2_terminal_timeInterval').combobox('getValue');
			var checkMsg = checkCustomTime(st, et);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
						'starttime': st,
						'endtime': et,
						'onePageRows' : rowCount,
						'sort' : "in_flows",
						'order' : 'desc',
						'showpagination' : false,
						'recordCount' : rowCount,
						"rowCount":rowCount,
						'timePerid': t,
						'terminal_ip': $('.device-terminal').data('terminal_ip')
				};
				$('.terminal-app-datagrid').datagrid('load', data);
				$('.terminal-session-datagrid').datagrid('load', data);
				$('.terminal-tos-datagrid').datagrid('load', data);
				$('.terminal-proto-datagrid').datagrid('load', data);
			}
			
		}
	});

})(jQuery);