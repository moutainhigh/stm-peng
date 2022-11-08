(function($) {
	
	// 命名空间
	oc.ns('oc.module.monitormanagement.main');
	
	$('#monitor_management').layout({
		fit:true
	});
	
	// 对外提供入口方法
	oc.module.monitormanagement.main = {
		switchResourceDataGrid : function(queryData,titleInfo,modifyResourceNumber) {
		},
		removeGroupDataGrid : function(){
			oc.resourced.manager.two.showEmptyDataGrid();	
		}
	};
	
	//oc.resource.loadI18n('oc.monitor.management.js');

})(jQuery);

