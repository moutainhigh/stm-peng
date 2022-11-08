(function($){
	oc.ns('oc.module.alarm');
	
	oc.resource.loadScripts([
         oc.resource.getI18nUrl('oc.module.alarm.js')
	],function(){
		var tabs=$('#alarmIndex').attr('id',oc.util.generateId()).tabs();
		
		//告警-未恢复
		tabs.tabs('add',{
			title:oc.local.module.alarm.tabs.title_unRestore,
			href:'module/alarm/alarm.html',
			closabel:false
		});
		
		//告警-已恢复
		tabs.tabs('add',{
			title:oc.local.module.alarm.tabs.title_restored,
			href:'',
			closabel:false
		});
		
		//告警-日志告警
		tabs.tabs('add',{
			title:oc.local.module.alarm.tabs.title_log,
			href:'',
			closabel:false
		});
		
		//告警-未恢复
		tabs.tabs('add',{
			title:oc.local.module.alarm.tabs.title_third,
			href:'',
			closabel:false
		});
		tabs.tabs('select',oc.local.module.alarm.tabs.title_unRestore);
		oc.module.alarm.getTabs=function(){
			return tabs;
		};
	});

})(jQuery);