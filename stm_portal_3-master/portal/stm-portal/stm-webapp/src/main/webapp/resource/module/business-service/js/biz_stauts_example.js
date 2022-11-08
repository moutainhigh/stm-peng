(function(){

	function bizExample(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	bizExample.prototype = {
		constructor : bizExample,
		form : undefined, // 操作表单
		cfg : undefined, // 外界传入的配置参数 json对象格式
		isClickFirst : false,
		isClickSecond : false,
		isClickThrid : false,
		isClicklast : false,
		bizDom : undefined,
		discoveryFlag : false,
		dlg : undefined,
		open : function(cfg) { // 初始化页面方法
		var dlg = $("<div/>"), that = this;
		
		dlg.dialog({
			title : '状态定义示例', //oc.local.module.resource.discovery.discoverResource,
			href : oc.resource
					.getUrl('resource/module/business-service/biz_stauts_example.html'),
			width : 920,
			height : 610,
			onLoad : function() {
				var example=$("#oc-biz-example");
				var pagebtns=example.find(".d-pages");
				pagebtns.find("a").each(function(){
					$(this).click(function(){
					var currentIdx=	pagebtns.find("a.d-page-greendot").attr("idx");
					var clickIdx=$(this).attr("idx");
						if($(this).attr("idx")!=currentIdx){
							pagebtns.find("a.d-page-greendot").removeClass("d-page-greendot").addClass("d-page-dot");
							$(this).removeClass("d-page-dot").addClass("d-page-greendot");
							example.find("div img").each(function(){
								if(clickIdx==$(this).attr("idx")){
									$(this).attr("style","display:block;width:900px;height:520px;");
								}else{
									$(this).attr("style","display:none;width:900px;height:520px;");
								}
							});
						}
					});
				});
			},
			 onClose:function(){
			    	dlg.dialog("destroy");
			    }
		});
		}
		
	};

	// 命名空间
	oc.ns('oc.business.service.example');
	// 对外提供入口方法
	var example = undefined;
	oc.business.service.example = {
		open : function(cfg) {
			example = new bizExample(cfg);
			example.open();
		}
	};

	
	
})();