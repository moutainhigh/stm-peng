var datagridObj_netflow_ipgroup_app = null;
var datagridObj_netflow_ipgroup_proto = null;
var datagridObj_netflow_ipgroup_tos = null;
var datagridObj_netflow_ipgroup_terminal = null;
var datagridObj_netflow_ipgroup_session = null;
(function($){
	
	//隐藏Y轴滚动条
	$('.netflow-main').parents('.panel-body:first').css({'overflow':'hidden'});

	var mainDiv = $('.device-2-ipgroup-main');
	
	//设备详细页面的参数
//	var timePerid = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();

	 var startTime = $('.terminal-ipg').data('startTime');
	 var endTime = $('.terminal-ipg').data('endTime');
	 var timePerid = $('.terminal-ipg').data('timeInterval');
	 var type = $('.terminal-ipg').data('type');
	 var rowCount = 5;
	
	oc.ui.combobox({
		placeholder: null,
		selector : mainDiv.find('#device_ipgroup_timeInterval'),
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
			if(d.id == 'custom') {
				mainDiv.find('.device-2-ipgroup-custom-time').removeClass('hide');
				mainDiv.find('#device_2_ipgroup_search').removeClass('hide');
			} else {
				mainDiv.find('.device-2-ipgroup-custom-time').addClass('hide');
				mainDiv.find('#device_2_ipgroup_search').addClass('hide');

				var data = {
					'startTime': startTime,
					'endTime': endTime,
					'starttime': startTime,
					'endtime': endTime,
					'onePageRows' : rowCount,
					"sort" : "in_flows",
					"order" : 'desc',
					"needPagination":false,
					"showpagination" : false,
					"recordCount" : rowCount,
					"querySize" : rowCount,
					"rowCount":rowCount,
					"ipGroupId":$('.terminal-ipg').data('ipgroup_id'),
					'timePerid': d.id
				};
				
				$('.ipgroup-app-datagrid').datagrid('load', data);
				$('.ipgroup-session-datagrid').datagrid('load', data);
				$('.ipgroup-terminal-datagrid').datagrid('load', data);
				$('.ipgroup-proto-datagrid').datagrid('load', data);
				$('.ipgroup-tos-datagrid').datagrid('load', data);
			}
		}
	});
	
	oc.ui.combobox({
		placeholder: null,
		selector: mainDiv.find('.device-2-ipgroup-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		selected: '1',
		onSelect: function(d) {
			
			changeIndicatorDetail($('.ipgroup-app-datagrid'), d.id, 'app', 1, false);
			changeIndicatorDetail($('.ipgroup-session-datagrid'), d.id, 'session', 1, true);
			changeIndicatorDetail($('.ipgroup-terminal-datagrid'), d.id, 'terminal', 1, false);
			changeIndicatorDetail($('.ipgroup-proto-datagrid'), d.id, 'proto', 1, false);
			changeIndicatorDetail($('.ipgroup-tos-datagrid'), d.id, 'tos', 1, false);
		}
	});
	
	
	mainDiv.find('.device-2-ipgroup-custom-starttime').datetimebox();
	mainDiv.find('.device-2-ipgroup-custom-endtime').datetimebox();
	
	mainDiv.find('.device-2-ipgroup-custom-time').addClass('hide');
	mainDiv.find('#device_2_ipgroup_search').addClass('hide');
	
	//设置详细页面传递过来的参数
	if(type) {
		mainDiv.find('.device-2-ipgroup-indecator').combobox('setValue', type);
	}
	if(timePerid) {
		mainDiv.find('.#device_ipgroup_timeInterval').combobox('setValue', timePerid);
		if(timePerid == 'custom') {
			mainDiv.find('.device-2-ipgroup-custom-time').removeClass('hide');
			mainDiv.find('#device_2_ipgroup_search').removeClass('hide');
			
			mainDiv.find('.device-2-ipgroup-custom-starttime').datetimebox('setValue', startTime);
			mainDiv.find('.device-2-ipgroup-custom-endtime').datetimebox('setValue', endTime);
		}
	}

	//导出
	mainDiv.find('#device_2_ipgroup_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('.device-2-ipgroup-indecator').combobox('getValue')
			top.location = oc.resource.getUrl("netflow/three/export/IPGroup.htm?type="+type);
		}
	});

	//搜索
	mainDiv.find('#device_2_ipgroup_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var st = mainDiv.find('.device-2-ipgroup-custom-starttime').datetimebox('getValue');
			var et = mainDiv.find('.device-2-ipgroup-custom-endtime').datetimebox('getValue');
			var t = mainDiv.find('#device_ipgroup_timeInterval').combobox('getValue');
			
			var checkMsg = checkCustomTime(st, et);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
					'starttime': st,
					'endtime': et,
					'onePageRows': rowCount,
					"sort": "in_flows",
					"order": 'desc',
					"needPagination": false,
					"showpagination": false,
					"recordCount": rowCount,
					"querySize": rowCount,
					"rowCount": rowCount,
					"ipGroupId": $('.terminal-ipg').data('ipgroup_id'),
					'timePerid': t
				};
				
				$('.ipgroup-app-datagrid').datagrid('load', data);
				$('.ipgroup-session-datagrid').datagrid('load', data);
				$('.ipgroup-terminal-datagrid').datagrid('load', data);
				$('.ipgroup-proto-datagrid').datagrid('load', data);
				$('.ipgroup-tos-datagrid').datagrid('load', data);
			}
		}
	});

})(jQuery);