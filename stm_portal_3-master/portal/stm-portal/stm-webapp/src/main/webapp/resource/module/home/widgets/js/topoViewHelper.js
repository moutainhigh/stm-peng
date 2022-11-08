//@ sourceURL=topoViewHelper.js
/***
 * 拷贝原 js/ui/oc.ui.js中 最后一段 //oc.ui.svg.js 代码
 * 在此基础上进行扩展.使其适应图大小.
 * @auther weilu
 * @date 20150604
 *
 * @Modify tandl
 * @Date 20170118
 */
 (function(_$){
        _$.fn.canvas = function(){
            return new Canvas(_$(this)[0]);
        }
	  })(jQuery);
(function() {
	function svg(cfg) {
		this.cfg = $.extend({
			showText : false
		}, this._defaults, cfg);
		this.load();
	}
	svg.prototype = {
		constructor : svg,
		cas:"",
		
		_defaults : {
			type : "topo",
			selector : 0,
			isEditable : false,
			data : 0,
			/**
			 * 此选项用于适应左侧大图使用.
			 * */
			isMax:true
		
		},
		load : function() {
			if(this.cfg.type == "topo") {
				var _ctx=this;
				//小屏
				if(!this.cfg.isMax){
					oc.resource.loadScripts([
                 	    "resource/module/topo/api/TopoScreenApi.js",
                 	    "resource/module/topo/api/TopoAttr.js",
                 	    "resource/module/topo/api/TopoDevice.js",
                 	    "resource/module/topo/api/TopoLink.js",
                 	    "resource/module/topo/api/TopoScale.js",
                 	    "resource/module/topo/api/TopoShower.js",
                 	    "resource/module/topo/api/TopoState.js",
                 	    "resource/module/topo/api/TopoImg.js",
                 	    "resource/module/topo/api/TopoData.js",
                 	    "resource/module/topo/api/TopoZIndex.js",
                 	    "resource/module/topo/graph/topo.js"
                 	],function(){
                 		$.post(oc.resource.getUrl("topo/help/currentTheme.htm"),function(tm){
                 			oc.ns("oc.topo.screen");
                 			oc.topo.theme=tm.theme;
                 			new TopoScreenApi({
             					holder:_ctx.cfg.selector,
             					id:_ctx.cfg.id,
             					onOver:function(){
         							_ctx.cfg.selector.find("text").remove();
             					}
             				});
                 		},"json");
                 	});
				}else{//大屏
					oc.resource.loadScripts([
                     	"resource/module/topo/api/TopoScreen.js",
                 	],function(){
                 		$.post(oc.resource.getUrl("topo/help/currentTheme.htm"),function(tm){
                 			oc.ns("oc.topo.screen");
                 			oc.topo.theme=tm.theme;
                 			//if(!oc.topo.screenTopo){
                 				var count = 10;
                 				var iner = setInterval(function(){//用于解决js加载完后还未执行的问题
								try{
									_ctx.tss = new TopoScreen({
		                 				holder:_ctx.cfg.selector,
		                 				id:_ctx.cfg.id,
		                 				//首页拓扑模块是否显示配置背景图
		                 				showBgIm:_ctx.cfg.showBgIm,
		                 				afterInit:function(ctx){
		                 					var pa = ctx.topo.paperAttr;
		                 					var vat = pa.attr.viewbox;
		                 					if(vat != null){
		                 						var vbx = vat.x + ' ' + vat.y + ' ' + vat.w + ' ' + vat.h;
		                 						//重新设置视图的窗体大小，避免显示不全的问题
		                 						ctx.topoScale.svg.get(0).setAttribute('viewBox',vbx);
		                 						
		                 						if(_ctx.cfg.showBgIm){
		                 							if(ctx.topo != null && ctx.topo.bgsrc != null && ctx.topo.bgsrc != ''){
		                 								ctx.topo.$holder.css("background-image","url("+ctx.topo.bgsrc+")");
		                 							}
		                 						}
		                 					}
		                 				}
	                 				});

									clearInterval(iner);
								}catch(e){
									!(count --) && clearInterval(iner);
								}
							},100);
                 				
                 			/*}else{
                 				oc.topo.screenTopo.load(_ctx.cfg.id,_ctx.cfg.selector);
                 			}//*/
                 		},"json");
                 	});
				}
			}else if(this.cfg.type=="map"){
				var _ctx=this;

				oc.resource.loadScripts(['resource/module/home/widgets/js/map.js'],function(){

					//var iner = setTimeout(function(){//用于等待js加载完成
					//},100);
					_ctx.map = new map(_ctx.cfg.selector,_ctx.cfg);	
					
				});
				
			}
		},
		_autoSize : function() {
			var w = this.cfg.selector.width(), h = this.cfg.selector.height();
			var svg = this.cfg.selector.find("svg");
			var sw = parseFloat(svg.width()), sh = parseFloat(svg.height());
			svg.css({
				width : w,
				height : h
			});
			if (!this.cfg.showText) {
				svg.find("text").hide();
				if (svg && svg.length > 0) {
					var oldvb = svg.get(0).getAttribute("viewBox");
					var x = 0, y = 0;
					if (oldvb) {
						var tmp = oldvb.split(" ");
						x = tmp[0];
						y = tmp[1];
					}
					svg.get(0).setAttribute("viewBox",x + " " + y + " 2048 1024");
				}
			}
		},
		_registerEvent : function() {
			var ctx = this;
			$(window.document).resize(function() {
				ctx._autoSize()
			})
		}
	}

	//这里调整oc.ui.svg为oc.ui.toposvg，避免与已有的产生冲突
	oc.ui.toposvg = function(cfg) {
		return new svg(cfg);
	}
})();
//# sourceURL=topoViewHelper.js