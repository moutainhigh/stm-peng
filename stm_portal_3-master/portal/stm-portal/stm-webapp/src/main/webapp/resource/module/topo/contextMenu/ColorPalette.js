function ColorPalette(args){
	this.args=$.extend({
		colorChosed:function(){},
		opacityChange:function(){},
		opacityBar:true
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/ColorPalette.html"),
		type:"get",
		dataType:"html",
		success:function(htl){
			oc.resource.loadScript("resource/module/topo/util/jscolor.js",function(){
				ctx.init(htl);
			});
		}
	});
};
ColorPalette.prototype={
	init:function(htl){
		this.root=$(htl);
		this.root.appendTo($("body"));
		this.fields={};
		var ctx = this;
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		if(this.args.opacityBar){
			this.drawSlider(this.fields.opacity);
			this.fields.opacityCont.removeClass("hide");
		}else{
			this.fields.opacityCont.addClass("hide");
		}
		this.regEvent();
		this.hide();
		this.w=this.getWidth();
	},
	regEvent:function(){
		var ctx = this;
		if(this.dragger){
			this.dragger.on("dragend",function(e){
				var x=this.attr("x");
				ctx.opacity=1-x/ctx.w;
				if(ctx.onselected){
					ctx.onselected.call(ctx,ctx.opacity);
					ctx.onselected=null;
				}
			});
		}
		this.root.find(".body").on("click","td",function(e){
			ctx.color=$(this).css("background-color");
			ctx.args.colorChosed.call(ctx,ctx.color);
			if(ctx.onselected){
				ctx.onselected.call(ctx);
				ctx.onselected=null;
			}
			ctx.hide();
		});
		this.root.on("hide",function(){
			ctx.jscolor.hide();
		});
		with(this.fields){
			colorPicker.on("click",function(){
				if(!ctx.jscolor){
					ctx.jscolor=this.jscolor;
					window.onColorEnd=function(type){
						if(type=="clicked"){
							setTimeout(function(){
								ctx.show(ctx.e);
							},20);
						}else{
							setTimeout(function(){
								ctx.root.hide();
							},200);
						}
					};
				}
			});
			
			colorCustomBtn.on("click",function(){
				if(ctx.onselected){
					ctx.color="#"+colorPicker.val();
					ctx.onselected.call(ctx);
					ctx.onselected=null;
				}
			});
		}
/*		this.fields.noColor.on("click",function(){
			ctx.color="none";
			ctx.args.colorChosed.call(ctx,ctx.color);
			if(ctx.onselected){
				ctx.onselected.call(ctx);
				ctx.onselected=null;
			}
			ctx.hide();
		});
*/	},
	initNumber:function(dom){
		var ctx = this;
		dom.on("change",function(){
			var tmp=$(this);
			try{
				var val = parseInt(tmp.val());
				if(val<0 || isNaN(val)){
					val=0;
					tmp.val(0);
				}
				if(val>255){
					val=255;
					tmp.val(255);
				}
				tmp.data("old",val);
				ctx.changeCustomColorBtn();
			}catch(e){
				tmp.val(tmp.data("old"));
			}
		});
	},
	changeCustomColorBtn:function(){
		with(this.fields){
			var r = R.val()||0,
				g = G.val()||0,
				b = B.val()||0;
			colorCustomBtn.css("background-color","rgb("+r+","+g+","+b+")");
		}
	},
	getValue:function(){
		return {
			color:this.color||"black",
			opacity:this.opacity||1
		};
	},
	drawSlider:function(cont){
		var w=cont.width(),h=cont.height();
		var drawer = SVG(cont.get(0)).size(w,h);
		drawer.rect(w,4).translate(0,(h-4)/2).fill("grey");
		var dc=(h-12)/2;
		var drager = this.dragger = drawer.rect(6,12).move(0,dc).fill("red");
		$(drager.node).css({
			cursor:"pointer"
		});
		drager.draggable(function(x,y){
			if(x<0) x=0;
			if(x>w-6) x=w-6;
			return {x:x,y:dc};
		});
		this.dc=dc;
	},
	show:function(e){
		oc.util.showFloat(this.root,e);
		if(!e){
			this.e=e;
		}
		if(this.dragger){
			this.dragger.translate(0,this.dragger.attr("y"));
		}
	},
	hide:function(){
		this.root.hide();
	},
	getWidth:function(){
		if(this.dragger){
			return this.fields.opacity.width()-this.dragger.bbox().w;
		}else{
			return this.fields.opacity.width();
		}
	},
	setValue:function(attr){
		this.opacity=attr.opacity;
		this.color=attr.color;
		if(this.dragger){
			this.dragger.move(this.w*(1-this.opacity),this.dc);
		}
	},
	onece:function(key,func){
		this["on"+key]=func;
	}
};