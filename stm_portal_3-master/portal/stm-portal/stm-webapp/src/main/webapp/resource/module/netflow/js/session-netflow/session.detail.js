var datagridObj_netflow_session_app = null;
var datagridObj_netflow_session_proto = null;
(function($){
	
//	var mainDiv = $('.device-2-session-main');
	
	//设备详细页面的参数
//	var timePerid = getTimePerid();
//	var se = getSETime();
//	var startTime = se[0], endTime = se[1];
//	var type = getType();
	
//	var timeInterval = $('.device-session').data('timeInterval');
//	var type = $('.device-session').data('type');
//	var startTime = $('.device-session').data('startTime');
//	var endTime = $('.device-session').data('endTime');

	var tab = $('.sessionsingle-netflow-main-tabs').tabs('getSelected');
	
	var type = $('.netflow-main').data('sessiontype');
	var startTime = $('.netflow-main').data('sessionstartTime');
	var endTime = $('.netflow-main').data('sessionendTime');
	var timeInterval = $('.netflow-main').data('sessiontimeInterval');		    	 
	var rowCount = 5;
	
	 var src_ip = $('.netflow-main').data('src_ip');
	 var dst_ip = $('.netflow-main').data('dst_ip');
	
	oc.ui.combobox({
		selector: tab.find('.session-detail-timeinterval'),
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
				tab.find('.session-detail-custom-date').removeClass('hide');
				tab.find('#session_detail_search').removeClass('hide');
			} else {
				tab.find('.session-detail-custom-date').addClass('hide');
				tab.find('#session_detail_search').addClass('hide');
				
				var data = {
					'rowCount' : rowCount,
					'onePageRows' : rowCount,
					"recordCount":rowCount,
					"sort" : "in_flows",
					"order" : 'desc',
					"showpagination":false,
					"timePerid": d.id,
					"currentSrcIp" : src_ip,
					"currentDstIp" : dst_ip
				};
				
				tab.find('.session-proto-datagrid').datagrid('load', data);
				tab.find('.session-app-datagrid').datagrid('load', data);
			}
		}
	});
	
	oc.ui.combobox({
		placeholder: null,
		selector: tab.find('.session-detail-indecator'),
		data:[
		    { id: 1, name: '流量'}, 
		    { id: 2, name: '包数' }, 
		    { id: 3, name: '连接数'}/*, 
		    { id: 4, name: '带宽使用率'}*/
		],
		selected: '1',
		onSelect: function(d) {
			
			changeIndicatorDetail(tab.find('.session-proto-datagrid'), d.id, 'proto', 2, false);
			changeIndicatorDetail(tab.find('.session-app-datagrid'), d.id, 'app', 2, false);
		}
	});
	
	tab.find('.session-detail-custom-starttime').datetimebox();
	tab.find('.session-detail-custom-endtime').datetimebox();
	
	tab.find('.session-detail-custom-date').addClass('hide');
	tab.find('#session_detail_search').addClass('hide');
	
	//设置详细页面传递过来的参数
	if(type) {
		tab.find('.session-detail-indecator').combobox('setValue', type);
	}
	if(timeInterval) {
		tab.find('.session-detail-timeinterval').combobox('setValue', timeInterval);
		if(timeInterval == 'custom') {
			tab.find('.session-detail-custom-date').removeClass('hide');
			tab.find('#session_detail_search').removeClass('hide');
			
			tab.find('.session-detail-custom-starttime').datetimebox('setValue', startTime);
			tab.find('.session-detail-custom-endtime').datetimebox('setValue', endTime);
		}
	}

	//导出
	tab.find('#session_detail_export').linkbutton('RenderLB', {
		iconCls: 'icon-word',
		onClick: function() {
			var type = tab.find('.session-detail-indecator').combobox('getValue');
			top.location = oc.resource.getUrl("netflow/two/export/session.htm?type="+type);
		}
	});

	//搜索
	tab.find('#session_detail_search').linkbutton('RenderLB', {
		iconCls: 'icon-search',
		onClick: function() {
			var st = tab.find('.session-detail-custom-starttime').datetimebox('getValue');
			var et = tab.find('.session-detail-custom-endtime').datetimebox('getValue');
			var t = tab.find('.session-detail-timeinterval').combobox('getValue');

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
						"timePerid": t,
						"currentSrcIp" : src_ip,
						"currentDstIp" : dst_ip
				};
				$('.session-proto-datagrid').datagrid('load', data);
				$('.session-app-datagrid').datagrid('load', data);
			}
			
		}
	});
	
})(jQuery);