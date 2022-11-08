function TopoFlow(args){
	this.args=$.extend({
		holder:null,
		paper:null,
		onLoad:function(){}
	},args);

	if(this.args.holder && this.args.paper){
		this.init();
	}else{
		throw "bad arguments!";
	}
};
TopoFlow.prototype={
	init:function(){
		var args = this.args;
		this.point=args.paper.circle(100,100,2).attr({
			fill:"red",
			stroke:"none",
			"fill-opacity":1
		});
		this.point.hide();
		this.args.onLoad.call(this);
	},
	setLine:function(line){
		this.line = line;
		this.update();
	},
	remove:function(){
		this.point.stop();
		this.point.remove();
	},
	update:function(){
		if(this.line){
			try{
				var ctx = this;
				var pth =this.line.attr("path");
				if(pth && pth[0])
				this.point.attr({
					cx:pth[0][1],cy:pth[0][2],
					"fill":"white"
				});
			}catch(e){
			}
		}
	},
	nodeToFront:function(){
		$(this.line.node).after($(this.point.node));
	},
	startPlay:function(){
		this.stopPlay();
		var ctx = this;
		setTimeout(function(){
			ctx.point.show();
			ctx.playGlow();
			ctx.playLine(true,1);
		},(Math.random()*10000)/1);
	},
	stopPlay:function(){
		this.point.stop();
		var pos = this.getPos();
		this.point.attr({
			cx:pos.x1,cy:pos.y1
		});
		this.point.hide();
	},
	getPos:function(){
		try{
			var pth =this.line.attr("path");
			var p1 = {x:pth[0][1],y:pth[0][2]},p2 = {x:pth[1][1],y:pth[1][2]};
			var width = Math.sqrt(Math.pow(p1.x-p2.x,2),Math.pow(p1.y-p2.y,2));
			return {
				x1:p1.x,y1:p1.y,
				x2:p2.x,y2:p2.y,
				w:width
			};
		}catch(e){
			return {
				x1:-1000,y1:-1000,
				x2:-1000,y2:-1000,
				w:0
			};
		}
	},
	playGlow:function(){
		var ctx = this;
		this.point.animate({
			r:(this.line.attr("stroke-width")||1)*1.25,
			"fill-opacity":0.01
		},200,"<>",function(){
			ctx.point.animate({
				"stroke-width":ctx.line.attr("stroke-width"),
				"fill-opacity":1
			},100,"<>",function(){
				ctx.playGlow();
			});
		});
	},
	playLine:function(isFrom,count){
		var ctx = this;
		if(count==1){
			this.nodeToFront();
			var pth = this.line.attr("path"),pointAn=null;
			var p=null;
			try{
				p={
					x1:pth[0][1],
					x2:pth[1][1],
					y1:pth[0][2],
					y2:pth[1][2]
				};
			}catch(e){
				this.point.hide();
				return;
			}
			if(isFrom){
				pointAn = Raphael.animation({
					cx:p.x2,
					cy:p.y2
				},2000,"<",function(){
					ctx.point.attr({
						cx:p.x2,
						cy:p.y2
					});
					ctx.playLine(false, ++count);
				});
			}else{
				pointAn = Raphael.animation({
					cx:p.x1,
					cy:p.y1
				},2000,"<",function(){
					ctx.point.attr({
						cx:p.x1,
						cy:p.y1
					});
					ctx.playLine(true, ++count);
				});
				
			}
			//播放动画
			this.point.animate(pointAn);
			count=0;
		}
	}
};