(function($){

	var mainDiv = $('.device-2-session-main');
	
	//设备详细页面的参数
//	var timePerid = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();

	var timeInterval = $('.device-session').data('timeInterval');
	var type = $('.device-session').data('type');
	var startTime = $('.device-session').data('startTime');
	var endTime = $('.device-session').data('endTime');
	var rowCount = 5;
	
	oc.ui.combobox({
		selector:'#device_session_timeInterval',
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
		selected: '1hour',
		onSelect: function(d) {
			if(d.id == 'custom') {
				mainDiv.find('.device-2-session-custom-time').removeClass('hide');
				mainDiv.find('#device_2_session_search').removeClass('hide');
			} else {
				mainDiv.find('.device-2-session-custom-time').addClass('hide');
				mainDiv.find('#device_2_session_search').addClass('hide');
				
				var st = mainDiv.find('.device-2-session-custom-starttime').datetimebox('getValue');
				var et = mainDiv.find('.device-2-session-custom-endtime').datetimebox('getValue');
				
				var checkMsg = checkCustomTime(st, et);
				if('' != checkMsg) {
					alert(checkMsg);
				} else {
					var data = {
							'starttime': st,
							'endtime': et,
							'rowCount' : rowCount,
							'onePageRows' : rowCount,
							"recordCount":rowCount,
							"sort" : "in_flows",
							"order" : 'desc',
							"showpagination":false,
							"timePerid": d.id,
							"currentSrcIp" : $('.device-session').data('srcip'),
							"currentDstIp" : $('.device-session').data('dstip')
					};
					$('.session-proto-datagrid').datagrid('load', data);
					$('.session-app-datagrid').datagrid('load', data);
				}
			}
		}
	});
	
	oc.ui.combobox({
		placeholder: null,
		selector: mainDiv.find('.device-2-session-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		selected: '1',
		onSelect: function(d) {
			
			changeIndicatorDetail($('.session-proto-datagrid'), d.id, 'proto', 2, false);
			changeIndicatorDetail($('.session-app-datagrid'), d.id, 'app', 2, false);
		}
	});
	
	mainDiv.find('.device-2-session-custom-starttime').datetimebox();
	mainDiv.find('.device-2-session-custom-endtime').datetimebox();
	
	mainDiv.find('.device-2-session-custom-time').addClass('hide');
	mainDiv.find('#device_2_session_search').addClass('hide');

	//设置详细页面传递过来的参数
	if(type) {
		mainDiv.find('.device-2-session-indecator').combobox('setValue', type);
	}
	if(timeInterval) {
		mainDiv.find('#device_session_timeInterval').combobox('setValue', timeInterval);
		if(timeInterval == 'custom') {
			mainDiv.find('.device-2-session-custom-time').removeClass('hide');
			mainDiv.find('#device_2_session_search').removeClass('hide');
			
			mainDiv.find('.device-2-session-custom-starttime').datetimebox('setValue', startTime);
			mainDiv.find('.device-2-session-custom-endtime').datetimebox('setValue', endTime);
		}
	}

	//导出
	mainDiv.find('#device_three_session').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = mainDiv.find('.device-2-session-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/three/export/session.htm?type="+type);
		}
	});

	//搜索
	mainDiv.find('#device_2_session_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var st = mainDiv.find('.device-2-session-custom-starttime').datetimebox('getValue');
			var et = mainDiv.find('.device-2-session-custom-endtime').datetimebox('getValue');
			var t = mainDiv.find('#device_session_timeInterval').combobox('getValue');
			
			var checkMsg = checkCustomTime(st, st);
			if('' != checkMsg) {
				alert(checkMsg);
			} else {
				var data = {
					'starttime': st,
					'endtime': et,
					'rowCount' : rowCount,
					'onePageRows' : rowCount,
					"recordCount":rowCount,
					"sort" : "in_flows",
					"order" : 'desc',
					"showpagination":false,
					"timePerid": t,
					"currentSrcIp" : $('.device-session').data('srcip'),
					"currentDstIp" : $('.device-session').data('dstip')
				};
				$('.session-proto-datagrid').datagrid('load', data);
				$('.session-app-datagrid').datagrid('load', data);
			}
		}
	});
	
})(jQuery);