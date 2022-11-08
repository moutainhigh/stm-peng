function TopoImg(args){
	this.args=$.extend({
		paper:null,
		src:null,
		w:0,h:0,x:0,y:0
	},args);
	if(this.args.paper && this.args.src){
		this.init();
	}else{
		throw "bad arguments";
	}
};
TopoImg.prototype={
	init:function(){
		this.d=$.extend({},this.d);
		var args = this.args;
		this.img = args.paper.image(args.src,args.x,args.y,args.w,args.h);
	},
	getPos:function(){
		var r = {
			x:this.img.attr("x"),
			y:this.img.attr("y"),
			w:this.img.attr("width"),
			h:this.img.attr("height")
		};
		r.cx=r.x+r.w/2;
		r.cy=r.y+r.h/2;
		return r;
	},
	getValue:function(){
		var value = this.getPos();
		var zIndex = this.img.node.getAttribute("z-index");
		if(zIndex){
			value.zIndex=zIndex;
		}
		return $.extend(this.d,value);
	},
	toBack:function(){
		this.img.toBack();
	}
};