function TopoDeviceGlow(args){
	this.args = $.extend({
		device:null,
		paper:null,
		level:1
	},args);
	if(this.args.device && this.args.paper){
		this.init();
	}else{
		throw "bad arguments!";
	}
};
TopoDeviceGlow.prototype={
	init:function(){
		var args = this.args;
		this.colors=["red","#b1dab8","#73a574","#51b35e","#538047","purple"];
		var pos = args.device.getPos();
		this.glow = args.paper.circle(pos.cx,pos.cy,pos.w).attr({
			"fill-opacity":0.6,
			"stroke":"none",
			"fill":this.getColor()
		});
		this.baseCircle=args.paper.circle(pos.cx,pos.cy,pos.w).attr({
			"fill-opacity":0.6,
			"stroke":"none",
			"fill":this.getColor()
		});
		$(args.device.img.node).before($(this.glow.node));
		$(this.args.device.img.node).before($(this.baseCircle.node));
		this.startPlay();
	},
	getColor:function(){
		return this.colors[this.args.level||1];
	},
	remove:function(){
		this.glow.stop();
		this.glow.remove();
		this.baseCircle.remove();
	},
	stopPlay:function(){
		this.glow.stop();
		this.baseCircle.hide();
		this.glow.hide();
	},
	startPlay:function(){
		var pos = this.args.device.getPos();
		this.glow.attr({
			cx:pos.cx,
			cy:pos.cy
		});
		this.baseCircle.attr({
			cx:pos.cx,
			cy:pos.cy,
			r:pos.w,
			fill:this.getColor()
		});
		this.glow.show();
		this.baseCircle.show();
		this.play();
	},
	play:function(){
		var pos = this.args.device.getPos();
		var ctx = this;
		this.glow.animate({
			r:pos.w*1.5,
			"fill-opacity":0.2,
			"fill":this.getColor()
		},1500,"<",function(){
			ctx.glow.attr({
				r:pos.w*1.5,
				"fill-opacity":0.1
			});
			ctx.glow.animate({
				r:pos.w,
				"fill-opacity":0.2
			},1000,">",function(){
				ctx.play();
			});
		});
	}
};