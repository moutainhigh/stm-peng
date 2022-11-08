/**
 * oc事件定义
 * @param $
 */
(function($){
	oc.events={
		form:{
			loaded:'oc.loaded'
		},
		resize:'oc.events.resize',
		home:{
			userWorkbenchReady:'oc_home_userWorkbenchReady'
		}
	};
})(jQuery);