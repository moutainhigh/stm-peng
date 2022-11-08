(function($) {
	oc.ui.combobox({
		selector : '[id=time]',
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
			name : '最近七天'
		} ],
		value : '1hour',
		onSelect : function() {
			device_group_netflow_datagrid.reLoad();
		}
	});

	oc.ui.combobox({
		selector : '[id=displayCount]',
		data : [ {
			id : '3',
			name : '3'
		}, {
			id : '5',
			name : '5'
		}, {
			id : '10',
			name : '10'
		}, {
			id : '20',
			name : '20'
		}, {
			id : '30',
			name : '30'
		}, {
			id : '40',
			name : '40'
		}, {
			id : '50',
			name : '50'
		} ],
		value : 3,
		onSelect : function() {
			device_group_netflow_datagrid.reLoad();
		}
	});

	$('#2b_js_fz').layout({
		fit : true
	});

	// $("#device_group_tu")
	// .append(
	// '<li><div style="width: 700px; height: 266px; border: 2px solid
	// #006100;"><div style="padding: 10px; border: 1px dashed #16870b;"><div
	// id="container" style="width: 680px; height:
	// 240px;"></div></div></div></li>');

	

})(jQuery);