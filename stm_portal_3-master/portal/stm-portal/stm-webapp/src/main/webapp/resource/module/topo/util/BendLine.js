function BendLine(args){
	this.args=$.extend({
		from:{x:0,y:0},
		to:{x:0,y:0},
		draw:null,
		angle:Math.PI*0.25,
		direction:"up",
		upFlag:null,
		downFlag:null
	},args);
	if(this.args.draw){
		this.init();
	}else{
		throw "draw can't be null";
	}
};
BendLine.prototype={
	init:function(){
		with(this.args){
			var qp=this.getCtrPoint();
			this.line = draw.path([
  				["M",from.x,from.y],
  				["Q",qp.x,qp.y,to.x,to.y]
  			]).stroke("red").fill("none");
			this.text={
				textUp:"",textDown:""
			};
		}
	},
	refresh:function(from,to){
		this.args.from=from,this.args.to=to;
		var qp=this.getCtrPoint();
		with(this.args){
			//刷新连线
			this.line.plot([
				["M",from.x,from.y],
				["Q",qp.x,qp.y,to.x,to.y]
			]);
			//刷新文本
			this.refreshTitle();
		}
	},
	getCtrPoint:function(){
		with(this.args){
			angle = this.getCtrAngle();
			//中点
			var cp = {
				x:(from.x+to.x)/2,
				y:(from.y+to.y)/2
			};
			//本直线的斜率
			var k = 0;
			try{
				k = (to.y-from.y)/(to.x-from.x);
			}catch(e){
                // console.log(e);
				k = Math.PI/2;
			}
			//过from直线的斜率
			var k1 = Math.tan(Math.atan(k)+angle);
			//过本直线中点并垂直于本直线的斜率
			var k2 = 0;
			try{
				k2 = -1/k;
			}catch(e){
                // console.log(e);
				k2 = Math.PI/2;
			}
			//k1,k2两条直线的交点
			var x = (k1*from.x-k2*cp.x+cp.y-from.y)/(k1-k2),
				y = k1*(x-from.x)+from.y;
			//辅助线-原理图
			/*
			this.drawLine(k1,from.x,from.y);
			this.drawLine(k2,cp.x,cp.y);
			this.drawLine(k,cp.x,cp.y);
			*/
			this.k=k;
			if(isNaN(x)){
				x=(from.x+to.x)/2;
			}
			if(isNaN(y)){
				y=(from.y+to.y)/2;
			}
			return {x:x,y:y};
		}
	},
	setTitle:function(text,dire){
		if(text==undefined || text=="") return;
		if(dire=="up"){
			if(!this.txtUp){
				var ntxt = text;
				if(this.args.upFlag){
					ntxt=this.args.upFlag+" "+text;
				}
				this.txtUp = this.args.draw.text(ntxt);
				this.txtUpSpan=this.txtUp.tspan(ntxt);
				this.txtUp.path("M0,0L1000,1000");
				if(!this.txtPathId){
					this.txtPathId=$(this.txtUp.node).find("textPath").attr("href");
				}else{
					var tp = $(this.txtUp.node).find("textPath");
					$(tp.attr("href")).remove();
					tp.attr("href",this.txtPathId);
				}
				this.txtUpLen = this.getTextLength(ntxt);
			}
			this.text.textUp=text;
		}
		if(dire=="down"){
			if(!this.txtDown){
				var ntxt = text;
				if(this.args.downFlag){
					ntxt=text+" "+this.args.downFlag;
				}
				this.txtDown = this.args.draw.text(ntxt);
				this.txtDownSpan=this.txtDown.tspan(ntxt);
				this.txtDown.path("M0,0L1000,1000");
				this.txtDownLen = this.getTextLength(ntxt);
				if(!this.txtPathId){
					this.txtPathId=$(this.txtDown.node).find("textPath").attr("href");
				}else{
					var tp = $(this.txtDown.node).find("textPath");
					$(tp.attr("href")).remove();
					tp.attr("href",this.txtPathId);
				}
			}
			this.text.textDown=text;
		}
		this.refreshTitle();
	},
	remove:function(){
		if(this.txtUp){
			this.txtUp.remove();
			this.txtUp=null;
			this.txtUpSpan=null;
			this.txtUpLen=null;
		}
		if(this.txtDown){
			this.txtDown.remove();
			this.txtDownSpan=null;
			this.txtDown=null;
			this.txtDownLen=null;
		}
		if(this.dangerText){
			this.dangerText.remove();
			this.dangerText=null;
		}
		if(this.glow){
			this.glow.remove();
			this.glow = null;
		}
		$(this.txtPathId).remove();
		this.line.remove();
		this.text=null;
		this.line=null;
		this.txtPathId=null;
	},
	show:function(){
		if(this.txtUp){
			this.txtUp.show();
		}
		if(this.txtDown){
			this.txtDown.show();
		}
		this.line.show();
	},
	hide:function(){
		if(this.txtUp){
			this.txtUp.hide();
		}
		if(this.txtDown){
			this.txtDown.hide();
		}
		this.line.hide();
	},
	getTextLength:function(t){
		var tmp = this.args.draw.text(t);
		var box = tmp.bbox();
		tmp.remove();
		return box.w;
	},
	danger:function(){
		this.dangerText=this.args.draw.text("×").fill("red").attr("font-size",20);
		this.refreshTitle();
	},
	refreshTitle:function(){
		var l=this.line.length();
		with(this.args){
			var parray = this.line.array().value;
			var x1=parray[0][1];
			var x2=parray[1][3];
			if(this.txtUp){
				var far = this.line.pointAt((l+this.txtUpLen)/2);
				var near = this.line.pointAt((l-this.txtUpLen)/2);
				if(x1>x2){
					this.txtUp.plot([
						["M",far.x,far.y],
						["L",near.x,near.y]
					]);
					if(upFlag){
						$(this.txtUpSpan.node).text(upFlag+" "+this.text.textUp);
					}
					this.txtUpSpan.attr("dy",-10);
				}else{
					//ok
					this.txtUp.plot([
						["M",near.x,near.y],
						["L",far.x,far.y]
					]);
					if(downFlag){
						$(this.txtUpSpan.node).text(this.text.textUp+" "+downFlag);
					}
					this.txtUpSpan.attr("dy",-10);
				}
			}
			if(this.txtDown){
				var far = this.line.pointAt((l+this.txtDownLen)/2);
				var near = this.line.pointAt((l-this.txtDownLen)/2);
				if(x1>x2){
					this.txtDown.plot([
						["M",far.x,far.y],
						["L",near.x,near.y]
					]);
					if(downFlag){
						$(this.txtDownSpan.node).text(this.text.textDown+" "+downFlag);
					}
					this.txtDownSpan.attr("dy",20);
				}else{
					this.txtDown.plot([
						["M",near.x,near.y],
						["L",far.x,far.y]
					]);
					if(upFlag){
						$(this.txtDownSpan.node).text(upFlag+" "+this.text.textDown);
					}
					this.txtDownSpan.attr("dy",20);
				}
			}
			if(this.dangerText){
				var cp = this.line.pointAt(l/2);
				this.dangerText.move(cp.x-10,cp.y-10);
			}
		}
	},
	toFront:function(){},
	setColor:function(color){
		this.line.stroke(color);
	},
	getPos:function(){
		var l=this.line.length();
		with(this.args){
			var p = this.line.pointAt(l/2);
			return {
				x1:from.x,y1:from.y,x2:to.x,y2:to.y,cx:p.x,cy:p.y
			};
		}
	},
	getCtrAngle:function(){
		with(this.args){
			//计算距离
			var d = Math.sqrt(Math.pow(from.x-to.x,2)+Math.pow(from.y-to.y,2));
			if(d<100){
				return Math.PI/3;
			}else if(d<350){
				return Math.PI/7;
			}else if(d<600){
				return Math.PI/12;
			}else if(d<900){
				return Math.PI/16;
			}else{
				return Math.PI/20;
			}
		}
	},
	drawLine:function(k,x,y){
		var x1=0,y1=-k*x+y,
			x2=1000,y2=k*(1000-x)+y;
		with(this.args){
			draw.line(x1,y1,x2,y2).stroke("orange");
		}
	},
	addFlow:function(){
		var effect = oc.topo.map.effect,
			draw = this.args.draw,
			ctx = this;
		if(effect && draw){
			var p = this.args.from;
			this.glow = draw.use(effect.lineEffect).translate(p.x,p.y).style({
				fill:"white"
			});
			var length=this.line.length();
			//修改1000这个值，值越小，速度越快
			this.glow.animate(parseFloat(length)/30*1000).during(function(r){
				var l=r*length;
				var p = null;
				if(ctx.args.glowReverse){
					p = ctx.line.pointAt(length-l);
				}else{
					p = ctx.line.pointAt(l);
				}
				ctx.glow.translate(p.x,p.y);
			}).loop(true,true);
			this.glow.back();
		}
	}
};