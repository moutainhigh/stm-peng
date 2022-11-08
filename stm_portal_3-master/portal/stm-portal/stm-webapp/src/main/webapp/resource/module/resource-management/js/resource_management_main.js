(function($) {
	
	
	// 命名空间
	oc.ns('oc.module.resourcemanagement.main');
	
	
	$('#resource_management').layout({
		fit:true
	});
	var westPanel = $('#resource_management').layout('panel','west');
	westPanel.panel({
		title:'资源组',
		href:'module/resource-management/management_west.html',
	});
	
	// 对外提供入口方法
	oc.module.resourcemanagement.main = {
		switchResourceDataGrid : function(queryData,titleInfo,modifyResourceNumber) {
			/*
			$('#resource_management').layout('panel','center').panel('clear');
			$('#resource_management').layout('panel','center').panel({
				href : 'module/resource-management/resource_list_tabs.html',
				onLoad : function(){
					oc.resource.loadScript('resource/module/resource-management/js/resource_list.js',function(){
						//创建grid
						oc.resourced.manager.two.open();
						oc.resourced.manager.two.reloadGridData(queryData,titleInfo,modifyResourceNumber);
					});
				}
			});
			*/
		},
		removeGroupDataGrid : function(){
			oc.resourced.manager.two.showEmptyDataGrid();	
		}
	};
	
	oc.resource.loadI18n('oc.resource.management.js');

})(jQuery);

