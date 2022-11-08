function TopoSvg(args){
	this.args=$.extend({
		holder:null
	},args);
	if(!this.args.holder){
		throw "bad arguments";
	}else{
		this.svg=this.getSvg();
		this.svgAttr=this.getSvgAttr();
	}
};
TopoSvg.prototype={
	getSvg:function(){
		if(Raphael.svg){
			return $(this.args.holder).find("svg");
		}
	},
	getSvgAttr:function(){
		var offset=this.svg.offset();
		var w=this.svg.width(),
			h=this.svg.height();
		return {
			w:w,
			h:h,
			l:offset.left,
			t:offset.top
		};
	},
	getViewBox:function(){
		if(Raphael.svg){
			var svg = this.svg.get(0);
			var vb = svg.getAttribute("viewBox");
			var r = {x:0,y:0,w:parseFloat(svg.getAttribute("width")),h:parseFloat(svg.getAttribute("height"))};
			if(vb){
				var vs = vb.split(" ");
				r.x=parseFloat(vs[0]);
				r.y=parseFloat(vs[1]);
				r.w=parseFloat(vs[2]);
				r.h=parseFloat(vs[3]);
			}else{
				r.w=parseFloat(this.svg.width());
				r.h=parseFloat(this.svg.height());
				r.x=0;
				r.y=0;
			}
			r.cx=r.x+r.w/2;
			r.cy=r.y+r.h/2;
			return r;
		}
	},
	convertRealPos:function(p){
		var vb = this.getViewBox();
		var wr=vb.w/this.svgAttr.w,
			hr=vb.h/this.svgAttr.h;
		p.x=(p.x-this.svgAttr.l)*wr+vb.x;
		p.y=(p.y-this.svgAttr.t)*hr+vb.y;
	}
};