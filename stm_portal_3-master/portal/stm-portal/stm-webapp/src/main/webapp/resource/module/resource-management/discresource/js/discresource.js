(function() {
	function discResource(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	discResource.prototype = {
		constructor : discResource,
		form : undefined, // 操作表单
		cfg : undefined, // 外界传入的配置参数 json对象格式
		isClickFirst : false,
		isClickSecond : false,
		isClickThrid : false,
		discoveryDom : undefined,
		discoveryFlag : false,
		dlg : undefined,
		open : function() { // 初始化页面方法
			var dlg = $("<div/>"), that = this;
			var windowHeight = $(window).height(), dlgHeight = 610, dlgWidth = 1018;
			// 有可能显示区域太小 关闭按钮不显示问题
			var dlgTop = (windowHeight != undefined && windowHeight != null && windowHeight > dlgHeight) ? null : 0;
			this.dlg = dlg;
			dlg.dialog({
				title : '发现资源', //oc.local.module.resource.discovery.discoverResource,
				href : oc.resource
						.getUrl('resource/module/resource-management/discresource/discresource.html'),
				width : dlgWidth,
				height : dlgHeight,
				top : dlgTop,
				onLoad : function() {
					var id = oc.util.generateId();
					that.discoveryDom = $("#oc_module_resmanagement_discovery").attr('id', id);
					that._init();
				},
				onClose : function(){
					// 是否有发现资源
					if(that.discoveryFlag){
						that.cfg.resourceListObject.reLoadWestAndList();
					}
				}
			});
		},
		_defaults : {},
		_init : function() {
			var that = this;
			var account = that.discoveryDom.find(".nav-leftbody-con");
			var accounttwo = that.discoveryDom.find(".accounttwo");
			that.showHideAccounttwo(account, accounttwo, 0);
			this.discoveryDom.layout({
				fit : true
			});
			this.discoveryDom.find(".discoveryContent:first").panel({
				fit : true,
				border : false,
				href : oc.resource.getUrl('resource/module/resource-management/discresource/disresource_singlediscover.html')
			});
			this.isClickFirst = true;
			// 为导航按钮绑定点击事件
			that.discoveryDom.find(".nav_single_btn").on('click', function(){
				that.discoveryDom.find(".discoveryContent").hide();
				that.discoveryDom.find(".discoveryContent:first").show();
				if(!that.isClickFirst){
					that.discoveryDom.find(".discoveryContent:first").panel({
						fit : true,
						href : oc.resource.getUrl('resource/module/resource-management/discresource/disresource_singlediscover.html')
					});
					that.isClickFirst = true;
				}
				that.showHideAccounttwo(account, accounttwo, 0);
			});
			that.discoveryDom.find(".nav_batch_btn").on('click', function(){
				that.discoveryDom.find(".discoveryContent").hide();
				that.discoveryDom.find(".discoveryContent:eq(1)").show();
				if(!that.isClickSecond){
					that.discoveryDom.find(".discoveryContent:eq(1)").panel({
						fit : true,
						href : oc.resource.getUrl('resource/module/resource-management/discresource/disresource_batchdiscover.html'),
						onLoad : function(){
							oc.resource.loadScript('resource/module/resource-management/discresource/js/disresource_batchdiscover.js', function(){
								oc.module.resmanagement.discresource.batchdiscover.open({});
							});
						}
					});
					that.isClickSecond = true;
				}
				that.showHideAccounttwo(account, accounttwo, 1);
			});
			that.discoveryDom.find(".nav_account_btn").on('click', function(){
				that.discoveryDom.find(".discoveryContent").hide();
				that.discoveryDom.find(".discoveryContent:last").show();
				if(!that.isClickThrid){
					that.discoveryDom.find(".discoveryContent:last").panel({
						fit : true,
						href : oc.resource.getUrl('resource/module/resource-management/discresource/disresource_account.html')
					});
					that.isClickThrid = true;
				}
				that.showHideAccounttwo(account, accounttwo, 2);
			});
		},
		showHideAccounttwo : function(account, accounttwo, No){
			for(var i = 0; i < 3; i ++){
				if(i == parseInt(No)){
					$(account.get(i)).addClass('active');
					$(accounttwo.get(i)).show();
				}else{
					$(account.get(i)).removeClass('active');
					$(accounttwo.get(i)).hide();
				}
			}
		},
		close : function(){
			this.dlg.dialog('close');
		},
		isDiscovery : function(){
			this.discoveryFlag = true;
		}
	};

	// 命名空间
	oc.ns('oc.module.resmanagement.discresource.disc');
	// 对外提供入口方法
	var discovery = undefined;
	oc.module.resmanagement.discresource.disc = {
		open : function(cfg) {
			discovery = new discResource(cfg);
			discovery.open();
		},
		close : function(){
			if(discovery != undefined){
				discovery.close();
			}
		},
		setDiscoveryFlag : function(){
			if(discovery != undefined){
				discovery.isDiscovery();
			}
		}
	};
})(jQuery);