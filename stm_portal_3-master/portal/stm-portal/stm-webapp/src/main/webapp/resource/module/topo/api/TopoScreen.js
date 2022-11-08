function TopoScreen(args){
	this.args=$.extend({
		holder:null,
		id:null
	},args);
	if(this.args.holder && this.args.id!=null){
		this.init();
	}else{
		throw "holder can't be null";
	}
};
TopoScreen.prototype={
	load:function(id,currentHolder){
		with(this){
			var w=currentHolder.width(),h=currentHolder.height();
			holder.css({
				"width":w,
				"height":h
			});
			svgHolder.css({
				"width":w,
				"height":h
			});
			svgHolder.find("svg").attr({
				"width":w,
				"height":h
			});
			mask.css({
				"width":w,
				"height":h
			});
			holder.appendTo(currentHolder);
			topo.loadData(oc.resource.getUrl("topo/getSubTopo/"+id+ ".htm"));
		}
	},
	init:function(){
		var ctx = this;
		with(this){
			//加载数据
			var innerMainDivId = oc.util.generateId();
			this.holder=$("<div>");
			this.holder.attr("id","topo-holder-" + innerMainDivId);
			holder.css({
				"position":"relative",
				"width":"100%",
				"height":"100%",
				"overflow":"hidden"
			});
			this.svgHolder=$("<div>");
			this.svgHolder.attr("id","topo-svg-" + innerMainDivId);
			svgHolder.css({
				"width":"100%",
				"height":"100%",
				"overflow":"hidden"
			});
			svgHolder.appendTo(holder);
			holder.appendTo(args.holder);
			oc.resource.loadScripts([
                 "resource/module/topo/graph/topo.js",
                 "resource/module/topo/api/TopoScale.js",
                 "resource/module/topo/util/Tools.js",
                 "resource/module/topo/util/BendLine.js",
                 "resource/third/canvas/raphael.js",
                 "resource/module/topo/util/svg.js",
                 "resource/module/topo/graph/layout.js",
                /*新增首页topo中tip信息 huangping 2017/12/5 start*/
                "resource/module/topo/contextMenu/TopoNewTip.js",
                /*新增首页topo中tip信息 huangping 2017/12/5 end*/
                 "resource/module/topo/api/TopoFlow.js"
    		],function(){
                /*新增首页topo中tip信息 huangping 2017/12/5 start*/
                oc.ns("oc.topo");
                oc.topo.tips = new TopoNewTip();
                /*新增首页topo中tip信息 huangping 2017/12/5 end*/
    			ctx.topo = new Topo({
    				holder:svgHolder.get(0),
    				id:args.id,
    				onDrawed:function(){
    					var ts = new TopoScale({
    						holder:svgHolder,
    						showBgIm:ctx.args.showBgIm
    					});
    					//自适应边框
    					ts.selfAdapt(this.getRange());
    					ctx.topoScale = ts;

    					//回调函数
    					if(ctx.args.afterInit)
    						ctx.args.afterInit(ctx);

    				},
    				showBgIm:ctx.args.showBgIm
    			});
    		});
			//防止遮罩层
            /*this.mask = $("<div>");
            this.mask.attr("id","topo-mask-" + innerMainDivId);
            mask.css({
                "position":"absolute",
                "top":0,
                "left":0,
                "width":holder.width(),
                "height":holder.height()
            });
            holder.append(mask);*/
		}
	}
};