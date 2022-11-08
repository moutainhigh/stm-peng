/**
 * 
 */
(function(){
	oc.ns("oc.home.workbench");
	
	function HomeResourceTab(cfg){
		if(cfg.selector!=undefined && cfg.selector!=null ){
			this.selector = (typeof cfg.selector == 'string') ? $(cfg.selector): cfg.selector;
			this._cfg = cfg;
			this.init();
		}
	};
	
	HomeResourceTab.prototype={
			selector:undefined,
			constructor : HomeResourceTab,
			_cfg:undefined,
			init:function(){
				var html = $('<div class="h-slide">' + '<div class="h-slide-l">'
						+ '<div class="h-slide-r">' + '<div class="h-slide-m">'
						+ '<div class="h-slide-m-con indexviewswitch">' +
						'</div>' + '</div>' + '</div>' + '</div>' + '</div>'),that = this;
				html.appendTo(this.selector);
				if(this._cfg.tabs!=null && this._cfg.tabs!=undefined){
					for(var i=0;i<this._cfg.tabs.length;i++){
						var tab = this._cfg.tabs[i];
						var tabHtml = "";
						if(i==0){
							tabHtml = '<a class="active" value="'+tab.value+'">'+tab.text+'</a>';
						}else{
							tabHtml = '<a value="'+tab.value+'">'+tab.text+'</a>';
						}
						this.selector.find(".indexviewswitch").append(
							$(tabHtml).bind("click ",(function(t){
								return function(){
									that.selector.find(".indexviewswitch").find("a").removeClass("active");
									$(this).addClass("active");
									t.onClick&&t.onClick();
								};
							})(tab))
						);
					}
				}
			},
			select:function(value){
				var that = this;
				that.selector.find(".indexviewswitch").find("a").each(function(){
					if($(this).attr("value")==value){
						that.selector.find(".indexviewswitch").find("a").removeClass("active");
						$(this).addClass("active");
					}
				});
			},
			getSelect:function(){
				var that = this;
				var selectText= {text:"",value:""};
				that.selector.find(".indexviewswitch").find("a").each(function(){
					if($(this).hasClass("active")){
						selectText.text = $(this).text();
						selectText.value = $(this).attr("value");
					}
				});
				return selectText;
			}
	};
	
	oc.home.workbench ={
		homeResourceTab:function(cfg) {
			return new HomeResourceTab(cfg);
		}
	};
})();