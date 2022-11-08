(function(){
	function svg(cfg){
		this.cfg=$.extend({
			showText:false
		},this._defaults,cfg);
		this._registerEvent();//注册事件
		this.load(this.cfg.data);
	}
	
	svg.prototype={
		constructor:svg,
		_defaults:{
			type:"topo",//topo|biz
			selector:0,//放置svg的容器jquery对象
			isEditable:false,
			data:0//svg数据
		},
		load:function(data){
			if(data){
				if(this.cfg.type=='biz'){
					this.canvas=$('<div style="width:100%;height:100%;"/>').appendTo(this.cfg.selector).canvas();
					
					this.canvas.isEditable =this.cfg.isEditable;
					var jsonData = eval("("+data+")");
					log(jsonData);
					this.canvas.restore(jsonData);
					var w=this.cfg.selector.width(),
					h=this.cfg.selector.height();
					this.canvas.setViewBox(0,0,jsonData.bg.w,jsonData.bg.h);
					this.canvas.raphael.setSize(w-20,h-20);
				}else{//拓扑大屏
					var ctx = this;
					oc.resource.loadScripts([
					"resource/module/topo/graph/layout.js",
					"resource/module/topo/util/Tools.js",
					"resource/module/topo/graph/topo.js",
					"resource/module/topo/contextMenu/TopoSizerBar.js"], function(){
						var topo=new Topo({
							holder:ctx.cfg.selector.get(0)
						});
						topo.setEditable(false);
						topo.loadData(oc.resource.getUrl("topo/getSubTopo/"+ctx.cfg.id+".htm"),function(){
							if(!ctx.cfg.showText){//缩略图
								topo.util.getSvg().find("text").hide();
								topo.paper.setViewBox(0,0,1024,768);
							}
						});
						//加载缩放工具栏
						/*new TopoSizerBar({
							holder:ctx.cfg.selector.parent(),
							slave:topo.paper,
							slaveHolder:topo.holder,
							pos:{x:0,y:10}
						});*/
						
					});
				}
			}
		},
		_autoSize:function(){
			//外层框的大小
			var w=this.cfg.selector.width(),
				h=this.cfg.selector.height();
			var svg = this.cfg.selector.find("svg");
			//svg的大小
			var sw=parseFloat(svg.width()),sh=parseFloat(svg.height());
			//将svg画布设为外层框大小
			svg.css({
				width:w,
				height:h
			});
			//删除svg的文字-太小的文字显示不正常
			if(!this.cfg.showText){
				svg.find("text").hide();
				if(svg && svg.length>0){
					//获取svg的原先的视口
					var oldvb = svg.get(0).getAttribute("viewBox");
					var x =0,y=0;
					if(oldvb){
						var tmp = oldvb.split(" ");
						x=tmp[0];
						y=tmp[1];
					}
					//设置svg的视口
					svg.get(0).setAttribute("viewBox",x+" "+y+" 2048 1024");
				}
			}
		},
		_registerEvent:function(){
			var ctx = this;
			$(window.document).resize(function(){
				ctx._autoSize();
			});
		}	
	};
	
	oc.ui.svg=function(cfg){
		return new svg(cfg);
	};
})();