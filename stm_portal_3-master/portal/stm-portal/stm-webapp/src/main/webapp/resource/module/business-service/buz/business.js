/**
 * Created by tandl on 2015/8/8 0013.
 */
(function(p){

	function businessTopo(args){
		this.args = $.extend({
			bizId:undefined,
			showControlPanel:undefined,
			editAble:undefined
		},args);

		this.showControlPanel = undefined;
		if(args.showControlPanel != undefined){
			this.showControlPanel = args.showControlPanel;
		}

		this.editAble = undefined;
		if(args.editAble != undefined){
		 	this.editAble = args.editAble;
		}

		this.selector = undefined;
		if(args.selector){
			this.selector = args.selector;
		}else{
			this.selector = $(".panel").parent();
		}
		var ids = oc.index.indexLayout.data("bizIds");
        if (ids && ids.length > 0) {
        	oc.index.indexLayout.data("bizIds").push(args.bizId);
        } else {
        	ids = new Array();
        	ids.push(args.bizId);
        	oc.index.indexLayout.data("bizIds", ids);
        }
		this.init();
	}

	businessTopo.prototype={
		init:function(){
		 	var _this = this;
		 	/*if(window.requirejs || window.require){
		 		window.requirejs = undefined;
		 		window.require = undefined;
		 		window.define = undefined;
		 	}//*/
		 var skin=Highcharts.theme.currentSkin;
		 	oc.resource.loadCss("resource/module/business-service/buz/lib/spectrum/spectrum.css",false);
		 	oc.resource.loadCss("resource/module/business-service/buz/themes/default/plotingCrontrol.css",false);

			oc.resource.loadScripts([
				//"resource/module/business-service/buz/lib/svg.js",
				//"resource/module/topo/util/svg.js",
				//"resource/module/business-service/buz/lib/spectrum/spectrum.js",
				//"resource/module/business-service/buz/lib/svg.draggable.min.js",
				"resource/module/business-service/buz/lib/buzrequire.js",
				],function(){
					//$.getScript("module/business-service/buz/lib/require.js",function(){//});
					var count = 10;
					var iner = setInterval(function(){
						try{
							_this._init();
							clearInterval(iner);
						}catch(e){
                            // console.log(e);
							!(count --) && clearInterval(iner);
						}
					},100);
			});

			window.stopBubble = function(e) { 
		        //如果提供了事件对象，则这是一个非IE浏览器 
		        if ( e && e.stopPropagation ) 
		            //因此它支持W3C的stopPropagation()方法 
		            e.stopPropagation(); 
		        else
		            //否则，我们需要使用IE的方式来取消事件冒泡 
		            window.event.cancelBubble = true; 
		    }

		    window.stopEvent = function(evt) {
		        var evt = evt || window.event; 
		        if (evt.preventDefault) { 
		            evt.preventDefault(); 
		        } else { 
		            evt.returnValue = false; 
		        } 
		    }

		 },
		_init:function(){
			var _this = this;
			try{
				buzrequirejs.config({
					baseUrl:"module/business-service/buz/lib",
					paths:{
						"bz":"../business",
						"svg":"svg",
						"spectrum":"spectrum/spectrum",
						"svg.draggable.min":"svg.draggable.min",
					},
					waitSeconds: 0
				});

			}catch(e){
                // console.log(e);
			}

			
			buzrequire(["spectrum"],function(t){});
			buzrequire(["svg","bz/Start"],function(SVG,Start){
				if(!Start){
                    // console.log("require 加载 Start.js 失败！");
					return;
				}
				buzrequire(["svg.draggable.min"],function(t){});

				_this.topoSVG = new Start({
					//root:$root,
					//holder:$holder,
					selector: _this.selector,
					showControlPanel:_this.showControlPanel,
					editAble:_this.editAble,
					bizId:_this.args.bizId
				});
			});
			
		}
	}

	p.businessTopo = businessTopo;

}(this));