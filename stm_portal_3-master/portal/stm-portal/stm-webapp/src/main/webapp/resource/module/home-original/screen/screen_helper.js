/***
 * 拷贝原 js/ui/oc.ui.js中 最后一段 //oc.ui.svg.js 代码
 * 在此基础上进行扩展.使其适应图大小.
 * @auther weilu
 * @date 20150604
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
		//this._registerEvent();
		this.load(this.cfg.data);
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
		load : function(data) {
			if (data) {
	
				if (this.cfg.type == "biz") {
					//var canvas = $('<div style="width:150px;height:110px;"/>').appendTo(this.cfg.selector).canvas();
					var canvas = $(this.cfg.selector).canvas();
//					var canvas = new Canvas(this.cfg.selector)
					canvas.editable = this.cfg.isEditable;
					var jsonData = eval("(" + data + ")");

//					$(this.cfg.selector).css({
//		            	backgroundImage:'url('+jsonData.bg.src+')',
//		                backgroundRepeat:'no-repeat'
//		            });
					
					var param = {ifAdapt:true,id:this.cfg.id};
					canvas.restore(jsonData,param);
					var w = this.cfg.selector.width(), h = this.cfg.selector.height();
					
					//canvas.setViewBox(0, 0, jsonData.bg.w - 100, jsonData.bg.h);
					
					//canvas.raphael.setSize(w , h);
					
					this.cas = canvas;
				} else {
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
                         	"resource/module/topo/api/TopoScreen.js"
	                 	],function(){
	                 		$.post(oc.resource.getUrl("topo/help/currentTheme.htm"),function(tm){
	                 			oc.ns("oc.topo.screen");
	                 			oc.topo.theme=tm.theme;
	                 			if(!oc.topo.screenTopo){
	                 				oc.topo.screenTopo=new TopoScreen({
		                 				holder:_ctx.cfg.selector,
		                 				id:_ctx.cfg.id
		                 			});
	                 			}else{
	                 				oc.topo.screenTopo.load(_ctx.cfg.id,_ctx.cfg.selector);
	                 			}
	                 		},"json");
	                 	});
					}
				}
			}else{
				if(this.cfg.type=="map" && this.cfg.isMax){	//地图大屏
					oc.ns("oc.topo.screen");
					var _ctx=this;
					oc.resource.loadScripts(["resource/module/topo/map/TopoMapView.js",
					"resource/module/topo/map/TopoSvgElement.js","resource/module/topo/contextMenu/TopoNewTip.js",
					"resource/module/topo/util/svg.js"], function(){
		    			oc.topo={
		    				util:{
		    					module:"" 	//取值为""-主版本,"HLJ"-黑龙江招生考试,"GF"-北京高法,"FTMS"-添加北京地图到各个子地图
		    				}
		    			};
		    			var mapDiv = $("<div>");
		    			mapDiv.addClass("topo_map");
		    			mapDiv.appendTo(_ctx.cfg.selector);
						var tm = new TopoMapView({parent:_ctx.cfg.selector});
		    			eve.on("topo.map.sreen.tomap",function(key,level,flag){
		    				if(flag!=undefined){
    							flag.flag=true;
    						}
//		    				var tid = setInterval(function(){
		    					if(tm.graph){
		    						if(level){
		    							level = level - 1;
		    							oc.module.topo.map.loadMapData(key,level);
//		    							clearInterval(tid);
		    						}
		    					}
//		    				},200);
//							var tasks = oc.index.indexLayout.data("tasks");
//							if(tasks && tasks.length > 0){
//								oc.index.indexLayout.data("tasks").push(tid);
//							}else{
//								tasks = new Array();
//								tasks.push(tid);
//								oc.index.indexLayout.data("tasks", tasks);
//							}
		    			});
					});
				}
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
	oc.ui.svg = function(cfg) {
		return new svg(cfg)
	}
})();
//# sourceURL=screen_helper.js