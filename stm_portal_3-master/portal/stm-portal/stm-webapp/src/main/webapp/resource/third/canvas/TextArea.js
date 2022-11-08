//依赖于Sizer
function TextArea(args){
	this.paper=args.paper;
	this.pos=$.extend({x:0,y:0,w:0,h:0},args.pos);
	this.holder=args.holder;
	this.fontSize=16;
	this.fontColor="#fff";
	this.$svg = $(this.holder);
	this.editable=true;
	this.util = new Util(args);
	this.init();
};
TextArea.prototype={
	init:function(){
		//前景
		this.fp = this.paper.rect(this.pos.x,this.pos.y,this.pos.w,this.pos.h).attr({
			fill:"white",
			"fill-opacity":"0.01",
			"stroke":"#fff",
            "font-color":"#fff"
		});
		this.node = this.fp;
		//背景
		this.bg=this.paper.rect(this.pos.x,this.pos.y,this.pos.w,this.pos.h).attr({
			fill:"white",
			"fill-opacity":"0.15",
            "stroke":"#ccc",
            "font-color":"#fff"
		});
		this.bg.toBack();
		this.ipter = $("<textarea style=\"position:absolute;display:none;z-index:10000;color:black;\"/>");
		this.texts = this.paper.set();

		this.$svg.append(this.ipter);
		this.sizer = new Sizer({paper:this.paper,node:this});
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this;
		
		this.ipter.on("blur",function(){
			$(this).css({
				"display":"none"
			});
			ctx.setText($(this).val());
		});
		this.fp.drag(function(dx,dy){
			if(ctx.editable){
				this.attr({
					x:this.ox+dx,
					y:this.oy+dy
				});
				ctx.refresh(ctx.getPos());
				if(dx>0 || dy>0){
					this.clickTime=0;
				}
			}
		},function(){
			if(ctx.editable){
				this.ox=this.attr("x");
				this.oy=this.attr("y");
				ctx.sizer.hide();
				if(!this.clickTime) this.clickTime=0;
				setTimeout(function(){
					ctx.fp.clickTime=0;
					clearInterval(this);
				},500);
				this.clickTime++;
			}
			ctx.toFront();
			eve("oc_topo_chose",ctx);
		},function(e){
			if(ctx.editable){
				ctx.sizer.show();
				ctx.sizer.refresh(ctx.getPos());
				if(this.clickTime==2){
					var pos = ctx.getPos();
					ctx.ipter.css({
						"display":"block"
					});
					ctx.refresh(pos);
					ctx.ipter.focus();
					this.clickTime=0;
				}
			}
			return e;
		});
		//sizer更新时候
		this.sizer.onFresh=function(pos){
			ctx.refresh(pos);
		};
	},
	setEditable:function(flag){
		this.editable=!!flag;
		if(this.editable){
			this.sizer.show();
			this.bg.attr("stroke","black");
		}
		if(!this.editable){
			this.sizer.hide();
			this.bg.attr("stroke",null);
		}
	},
	toFront:function(){
		this.bg.toFront();
		this.texts.toFront();
		this.fp.toFront();
		this.sizer.toFront();
	},
	getPos:function(){
		var r = {
			w:this.fp.attr("width"),
			h:this.fp.attr("height"),
			x:this.fp.attr("x"),
			y:this.fp.attr("y")
		};
		r.cx=r.x+r.w/2;
		r.cy=r.y+r.h/2;
		return r;
	},
	drawText:function(t,x,y){
		var p = this.getPos();
		if(y<p.h+p.y){
			var text = this.paper.text(x,y+10, p.text).attr({
				"font-size":this.fontSize||16,
                "font-family":'微软雅黑',
				"font-weight":this.fontWeight||"normal",
				"text-anchor":"start",
				"space":"preserve",
				fill:this.fontColor||"#fff"

			});
			text.node.innerHTML=t.replace(/\s/g,"&nbsp;&nbsp;");
			this.texts.push(text);
		}
	},
	setText:function(t){
		if(t && this.editable){
			this.text = t;
			var p = this.getPos();
			var margin=4;
			var prw = p.w - 12-this.fontSize;
			this.texts.remove();
			//写文字
			var rw=0;var b=0;var preX=p.x+6;var preY=p.y+this.fontSize;
			for(var i=0;i<t.length;++i){
				if(t[i].match(/[^\x00-\xff]/)){
					rw+=this.fontSize;
				}else if(t[i].match(/[\-\,\.\?\:\;\'\"\!\`]/)){
					rw+=Math.floor(this.fontSize/4);
				}else if(t[i].match(/[\x00-\xff\S"]/)){
					rw+=Math.floor(this.fontSize/2);
				}
				if((rw>=prw || t[i].match(/[\n]/))){
					this.drawText(t.substring(b,i+1),preX,preY);
					rw=0;
					b=i+1;
					preY+=margin+this.fontSize;
				}
			}
			this.drawText(t.substring(b,i),preX,preY);
			this.ipter.text(t);
			this.toFront();
		}
	},
	getValue:function(){
		var p = this.getPos();
		p.text = this.text || "";
		p.type = "textarea";
		p.fontSize = this.fontSize;
		p.fontColor = this.fontColor;
		return p;
	},
	setValue:function(cfg){
		this.refresh(cfg);
		this.fontSize= cfg.fontSize || this.fontSize;
		this.fontColor= cfg.fontColor || this.fontColor;
		this.setText(cfg.text);
	},
	refresh:function(pos){
		if(this.editable && pos && pos.w>this.fontSize*2 && pos.h>0){
			var vb = this.util.getViewBox();
			this.fp.attr({
				x:pos.x,y:pos.y,width:pos.w,height:pos.h
			});
			this.bg.attr({
				x:pos.x,y:pos.y,width:pos.w,height:pos.h
			});
			this.ipter.css({
				left:pos.x,
				top:pos.y,
				"width":pos.w,
				"height":pos.h,
				"backgroundColor":"white",
                "opacity":0.7
			});
			this.setText(this.text);
		}
	},
	remove:function(){
		this.fp.remove();
		this.bg.remove();
		this.texts.remove();
        this.sizer.remove();
	}
};