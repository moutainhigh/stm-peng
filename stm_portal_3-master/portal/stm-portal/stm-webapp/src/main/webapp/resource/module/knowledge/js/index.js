(function($) {
	oc.ns('oc.module.knowledge');
	oc.resource.loadScripts(
			[ oc.resource.getI18nUrl('oc.module.knowledge.js') ],
			function() {
				var tabs = $('#oc-knowledge').attr('id',oc.util.generateId()).tabs();
				//云端知识库
				tabs.tabs('add', {
					title : oc.local.module.knowledge.tabs.title_cloudy,
					href : 'module/knowledge/cloudy/cloudyKnowledge.html',
					closabel : false,
					selected:true
				});
				// 本地知识库
				tabs.tabs('add', {
					title :  oc.local.module.knowledge.tabs.title_local,
					href : 'module/knowledge/local/localKnowledge.html',
					closabel : false,
					selected: false
				});
			});

})(jQuery);