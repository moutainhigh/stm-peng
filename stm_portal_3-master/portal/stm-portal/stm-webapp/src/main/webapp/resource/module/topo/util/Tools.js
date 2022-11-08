
/*
	依赖-ChoserBox
		-Sizer
		-TopoTextArea
		-ZLineDrawer
		-Ellipse
*/
function DrawUtil(args){
	this.paper = args.paper;
	this.holder = args.holder;
	this.cb = new ChoserBox({
		paper:this.paper,
		holder:this.holder
	});
	this.sizer = new Sizer({
		paper:this.paper,
		node:{getPos:function(){return {x:10,y:10,w:10,h:10}}}
	});
	this.sizer.hide();
	this.els = {};
	this.idx = -1;
	this.mode=null;
	this.init();
	this.regEvent();
};

DrawUtil.prototype={
	pushEl:function(el){
		if(el){
			if(!el.id){
				el.id = this.idx--;
			}
			this.els[el.id]=el;
			el.d={id:"other"+el.id,rawId:el.id,visible:el.visible,subTopoId:el.subTopoId};
			el.linktype="other";
		}
	},
	getEl:function(id){
		return this.els[id];
	},
	//获取所有机柜
	getCabinets:function(){
		var cabinets=[];
		$.each(this.els,function(idx,el){
			if(el.d && el.d.dataType=="cabinet"){
				cabinets.push(el);
			}
		});
		return cabinets;
	},
	setMode:function(mode){
		this.mode=mode;
		if(!this.mode) return ;
		var el = null;
		switch(mode){
			case "line" :
				el = new TopoLine({
					paper:this.paper,
					holder:this.holder
				});
				this.cb.setEditable(false);
			break;
			default :
				this.cb.setEditable(true);
			break;
		}
		if(el){
			this.pushEl(el);
		}
	},
	init:function(){
		this.cb.setEditable(false);
	},
	onFinished:function(el){},
	setEditable:function(flag){
		$.each(this.els,function(key,el){
			el.setEditable(flag);
		});
	},
	getValue:function(){
		var r = [];
		$.each(this.els,function(key,el){
			try{
				var elv = el.getValue();
				if(elv){
					elv.id = el.id;
					elv.subTopoId=el.d.subTopoId;
					elv.dataType=el.d.dataType;
					r.push(elv);
				}
			}catch(e){
				// console.log(e);
			}
		});
		return r;
	},
	setValue:function(v){
		for(var i=0;i<v.length;++i){
			var ev = v[i];
			var el = null;
			switch(ev.type){
				case "line" :
					el = new TopoLine({
						paper:this.paper,
						holder:this.holder
					});
				break;
				case "rect" :
					el = new Rect({
						paper:this.paper,
						holder:this.holder
					});
				break;
				case "ellipse" :
					el = new Ellipse({
						paper:this.paper,
						holder:this.holder
					});
				break;
				case "circle" :
					el = new Ellipse({
						paper:this.paper,
						holder:this.holder,
						type:"circle"
					});
				break;
				case "textarea" :
					el = new TopoTextArea({
						paper:this.paper,
						holder:this.holder
					});
				break;
				case "image" :
					el = new Image({
						paper:this.paper,
						holder:this.holder,
						attr:ev
					});
				break;
			}
			if(el){
				el.id=ev.id;
				this.pushEl(el);
				el.d.visible=ev.visible||true;
				el.d.subTopoId=ev.subTopoId;
				el.d.dataType=ev.dataType;
				el.setValue(ev);
				el.d=$.extend(el.d,ev);
				//置顶置底功能的实现
				if(1==el.d.zIndex){
					el.toFront();
				}
				if(-1==el.d.zIndex){
					el.toBack();
				}
				if(ev.dataType=="map"){
					el.toBack();
				}
			}
		}
	},
	reset:function(){
		var ctx = this;
		$.each(this.els,function(key,el){
			if(el){
				el.remove(true);
				ctx.els[key]=null;
			}
		});
		this.els={};
		this.sizer.hide();
	},
	remove:function(){
		this.sizer.remove();
		this.sizer=null;
		this.reset();
	},
	removeById:function(id){
		if(this.els[id]){
			this.els[id].remove();
			this.els[id]=null;
		}
	},
	regEvent:function(){
		var ctx = this;
		eve.on("element.choserbox.finished",function(p){
			if(this==ctx.cb){
				var el = null;
				switch(ctx.mode){
					case "textbox" :
						el = new TopoTextArea({
							paper:ctx.paper,
							holder:ctx.holder,
							pos:{x:p.x1,y:p.y1,w:p.w,h:p.h}
						});
						el.setText("双击输入文字");
						ctx.sizer.setNode(el);
					break;
					case "ellipse" :
						el = new Ellipse({
							paper:ctx.paper,
							holder:ctx.holder,
							pos:p
						});
					break;
					case "circle" :
						el = new Ellipse({
							paper:ctx.paper,
							holder:ctx.holder,
							pos:p,
							type:"circle"
						});
					break;
					case "rect" :
						el = new Rect({
							paper:ctx.paper,
							holder:ctx.holder,
							pos:{x:p.x1,y:p.y1,w:p.w,h:p.h,r:0}
						});
					break;
				};
				ctx.cb.setEditable(false);
				ctx.onFinished(el,function(result){
					if(result.state!=200){
						el.remove();
						alert(result.msg,"warning");
					}else{
						el.id=result.id;
						this.pushEl(el);
						el.d.visible=true;
						el.d.subTopoId=result.subTopoId;
						alert(result.msg);
						ctx.sizer.show();
					}
				});
				ctx.mode=null;
			}
		});
		//当前选择
		eve.on("element.click.*",function(){
			if(ctx.paper==this.paper){
				ctx.current = this;
				if(ctx.sizer.setNode){
					ctx.sizer.setNode(this);
				}
				ctx.sizer.show();
				ctx.sizer.toFront();
			}
		});
		//删除当前选择
		eve.on("topo.remove.command",function(){
			if(ctx.current){
				ctx.current.remove();
				ctx.current=null;
			}
		});
		//元素遭删除
		eve.on("element.remove",function(){
			ctx.sizer.hide();
			if(this.id && ctx.els[this.id]){
				ctx.els[this.id]=null;
				if(this.connects){
					for(var i=0;i<this.connects.length;++i){
						var line = this.connects[i];
						if(line && line.remove){
							line.remove();
						}
					}
				}
			}
		});
		//监听绘图事件
		eve.on("topo.draw.command",function(type,data){
			var cur = ctx.current;
			switch(type){
				case "textBox":
					ctx.setMode("textbox");
				break;
				case "fontSize":
					if(cur){
						if(cur instanceof TopoTextArea){
							cur.fontSize=parseInt(data.text);
							cur.refresh(cur.getPos());
						}
					}
				break;
				case "fontColor":
					if(cur){
						if(cur instanceof TopoTextArea){
							cur.fontColor=data;
							cur.refresh(cur.getPos());
						}
					}
				break;
				case "lineStyle":
					if(cur){
						if(cur instanceof TopoTextArea){
							cur.bg.attr("stroke-dasharray",data.typeStr||"");
						}else if(cur instanceof Rect){
							cur.rect.attr("stroke-dasharray",data.typeStr||"");
						}else if(cur instanceof Ellipse){
							cur.ellipse.attr("stroke-dasharray",data.typeStr||"");
						}else if(cur instanceof Connect){
							cur.line.setStyle(data.typeStr);
						}
					}
				break;
				case "lineWeight":
					if(cur){
						if(cur instanceof TopoTextArea){
							cur.bg.attr("stroke-width",data.weight);
						}else if(cur instanceof Rect){
							cur.rect.attr("stroke-width",data.weight);
						}else if(cur instanceof Ellipse){
							cur.ellipse.attr("stroke-width",data.weight);
						}else if(cur instanceof Connect){
							cur.line.setWeight(data.weight);
						}
					}
				break;
				case "shape":
					if(data.value){
						ctx.setMode(data.value);
					}
				break;
				case "borderColor":
					if(cur){
						if(cur instanceof TopoTextArea){
							cur.bg.attr("stroke",data);
						}else if(cur instanceof Rect){
							cur.rect.attr("stroke",data);
						}else if(cur instanceof Ellipse){
							cur.ellipse.attr("stroke",data);
						}else if(cur instanceof Connect){
							cur.line.setColor(data);
						}
					}
				break;
				case "fillColor":
					if(cur){
						if(cur instanceof TopoTextArea){
							cur.bg.attr("fill",data);
						}else if(cur instanceof Rect){
							cur.rect.attr("fill",data);
						}else if(cur instanceof Ellipse){
							cur.ellipse.attr("fill",data);
						}else if(cur instanceof Connect){
							cur.line.setColor(data);
						}
					}
				break;
				case "fill-opacity":
					if(cur){
						if(cur instanceof Rect){
							cur.rect.attr("fill-opacity",data);
						}else if(cur instanceof Ellipse){
							cur.ellipse.attr("fill-opacity",data);
						}
					}
					break;
				case "stroke-opacity":
					if(cur){
						if(cur instanceof Rect){
							cur.rect.attr("stroke-opacity",data);
						}else if(cur instanceof Ellipse){
							cur.ellipse.attr("stroke-opacity",data);
						}
					}
					break;
			}
		});
	}
};/*
	依赖 - Sizer
*/
function Ellipse(args){
	this.paper = args.paper;
	this.holder = args.holder;
	this.pos = $.extend({cx:0,cy:0,rx:0,ry:0},args.pos);
	this.editable = true;
	this.type=args.type || "ellipse";
	this.init();
	this.regEvent();
};
Ellipse.prototype={
	init:function(){
		var ctx = this;
		this.ellipse = this.paper.ellipse(0,0,0,0).attr("fill","#F2F2F2");
		this.ellipse.getPos = function(){
			var r = {
				cx:this.attr("cx"),
				rx:this.attr("rx"),
				cy:this.attr("cy"),
				ry:this.attr("ry")
			};
			r.x = r.cx-r.rx;
			r.y = r.cy-r.ry;
			r.w = 2*r.rx;
			r.h = 2*r.ry;
			return r;
		}
		this.refresh(this.pos);
	},
	getPos:function(){
		return this.ellipse.getPos();
	},
	regEvent:function(){
		var ctx = this;
		this.ellipse.drag(function(dx,dy){
			if(ctx.editable){
				var p = this.getPos();
				p.cx=dx+this.ocx;
				p.cy=dy+this.ocy;
				ctx.refresh(p);
				eve("element.drag.move",ctx,p);
			}
		},function(){
			this.ocx=this.attr("cx");
			this.ocy=this.attr("cy");
		});
		//单击
		this.ellipse.click(function(){
			if(ctx.onclick){
				ctx.onclick();
			}
			eve("element.click."+ctx.getMaster().id,ctx);
		});
		//右键
		$(this.ellipse.node).on("contextmenu",function(e){
			if(ctx.editable){
				eve("element.contextmenu",ctx,e,ctx);
			}
			e.stopPropagation(); 
			return false;
		});
	},
	getMaster:function(){
		return this.ellipse;
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	},
	rangeCheck:function(pos){
		if(pos.w<10||pos.h<10){
			return false;
		}else{
			return true;
		}
	},
	remove:function(){
		eve("element.remove."+this.getMaster().id,this);
		this.ellipse.remove();
	},
	refresh:function(pos){
		var rx = pos.rx,ry=pos.ry;
		if(pos.w) rx=pos.w/2;
		if(pos.h) ry=pos.h/2;
		if(this.type == "circle"){
			rx = ry = rx>ry?ry:rx;
		}
		this.ellipse.attr({
			cx:pos.cx,
			cy:pos.cy,
			rx:rx,
			ry:ry
		});
		eve("element.refresh."+this.getMaster().id,this,pos);
	},
	setEditable:function(flag){
		this.editable=!!flag;
	},
	toFront:function(){
		this.ellipse.toFront();
	},
	
	toBack:function(){
		this.ellipse.toBack();
	},
	
	getValue:function(){
		var p = this.ellipse.getPos();
		p.type=this.type;
		p.attr=this.ellipse.attrs;
		return p;
	},
	setValue:function(v){
		this.ellipse.attr(v);
		if(v.attr){
			this.ellipse.attr(v.attr);
		}
	}
};//依赖于sizer
function Image(args){
	this.paper = args.paper;
	this.holder = args.holder;
	this.noCtxMenu=args.noCtxMenu;
	this.attr = args.attr;
	this.angle = 0;
	this.editable = true;
	this.init();
	this.regEvent();
}
Image.prototype={
	init:function(){
		this.src=this.attr.src;
		this.img = this.paper.image(this.attr.src,this.attr.x,this.attr.y,this.attr.w,this.attr.h);
		if(this.attr.text){
			this.setText(this.attr.text);
		}
		this.img.parent = this;
	},
	setState:function(state){
		switch(state){
			case "choosed":
				this.img.attr({
					opacity:0.7
				});
			break;
			case "unchoosed":
				this.img.attr({
					opacity:1
				});
			break;
		}
	},
	setEditable:function(flag){
		this.editable = !!flag;
	},
	remove:function(silence){
		if(!silence){
			eve("element.remove."+this.getMaster().id,this);
		}
		this.img.remove();
		if(this.text){
			this.text.remove();
			this.text=null;
		}
		this.img=null;
	},
	getPos:function(){
		if(this.img){
			var r = {
				x:this.img.attr("x"),
				y:this.img.attr("y"),
				w:this.img.attr("width"),
				h:this.img.attr("height")
			};
			r.cx = r.x + r.w/2;
			r.cy = r.y + r.h/2;
			return r;
		}
	},
	rotage:function(angle){
		this.angle = angle || this.angle;
		var p = this.getPos();
		this.img.transform("r0");
		if((this.angle/90)%2!=0){
			this.img.transform([
				["r",this.angle,p.cx,p.cy],
				["s",p.h/p.w,p.w/p.h]
			]);
		}else{
			this.img.transform("r"+this.angle+","+p.cx+","+p.cy);
		}
	},
	on:function(key,callBack){
		key = "on"+key;
		if(!this[key]){
			this[key]=[];
		}
		this[key].push(callBack);
	},
	exe:function(key,a,b,c,d,e,f){
		var callBacks = this[key];
		if(callBacks){
			for(var i=0;i<callBacks.length;++i){
				callBacks[i].call(this,a,b,c,d,e,f);
			}
		}
	},
	regEvent:function(){
		var ctx = this;
		this.img.click(function(e){
			ctx.exe("onclick",e);
			eve("element.click."+this.id,ctx,e);
		});
		//双击
		$(this.img.node).on("dblclick",function(){
			eve("element.dblclick.img."+ctx.img.id,ctx);
		});
		this.img.drag(function(dx,dy){
			if(ctx.editable){
				var pos={
					x:dx,
					y:dy
				};
				eve("element.drag.beforemove",ctx,pos);
				eve("element.drag.move",ctx,pos);
				ctx.refresh({
					x:pos.x+this.ox,
					y:pos.y+this.oy,
					w:this.attr("width"),
					h:this.attr("height")
				});
			}
		},function(){
			this.ox = this.attr("x");
			this.oy = this.attr("y");
			eve("element.drag.start");
		},function(){
		});
		//右键
		if(!this.noCtxMenu){
			$(this.img.node).on("contextmenu",function(e){
				eve("element.contextmenu",ctx,e,ctx);
				e.stopPropagation(); 
				return false;
			});
		}
	},
	setValue:function(v){
		this.img.attr("src",v.src);
		this.src=v.src;
		this.rotage(v.angle);
		if(v.text){
			this.setText(v.text);
		}
		if(v.dataType && v.dataType=="map"){
			this.setEditable(v.editable);
		}
		this.refresh(v);
	},
	setImageSrcWithoutSave:function(src){
		this.img.attr("src",src);
	},
	//设置文字颜色
	setImageTextColor:function(color){
		this.text.attr("fill",color);
	},
	getValue:function(){
		if(this.img && this.img.attrs){
			var r = this.getPos();
			r.src = this.src;
			r.angle = this.angle;
			r.type="image";
			if(this.text){
				r.text=this.text.attr("text");
			}
			if(this.d){
				r.dataType=this.d.dataType;
				if(r.dataType=="map"){
					r.editable=this.editable;
				}
			}
			return r;
		}
	},
	toFront:function(){
		this.img.toFront();
		if(this.text){
			this.text.toFront();
		}
	},
	toBack:function(){
		this.img.toBack();
		if(this.text){
			this.text.toBack();
		}
	},
	refresh:function(p,silence){
		if(this.editable){
			this.img.transform("");
			this.img.attr({
				x:p.x,
				y:p.y,
				width:p.w,
				height:p.h
			});
			if(this.text){
				this.text.attr({
					x:p.x+p.w/2,
//					y:p.y+p.h+10
					y:p.y+15
				});
			}
			this.rotage();
			if(!silence){
				eve("element.refresh."+this.getMaster().id,this,p);
			}
		}
	},
	setText:function(text){
		if(!this.text){
			this.text=this.paper.text(0,0,text||"").attr({
				"text-anchor":"middle","fill":"white","font-weight":"bold"
			});
		}else{
			this.text.attr("text",text);
		}
		this.refresh(this.getPos(),true);
	},
	getMaster:function(){
		return this.img;
	}
};
function Device(args){
	this.holder=args.holder;
	this.paper=args.paper;
	this.attr=args.attr;
	this.util = new TopoUtil(args);
	this.init();
}
Device.prototype={
	init:function(){
		//内边距
		this.padding=8;
		this.fontSize=12;
		//组
		this.deviceSet = this.paper.set();
		var attr=this.attr;
		//背景
		this.bg = this.paper.path(this.util.getRoundRectPath({
			x:attr.x,y:attr.y,w:attr.w,h:attr.h,r:4
		})).attr({
			fill:"none",
			stroke:"none",
			"fill-opacity":0.7
		});
		//图标
		this.img = this.paper.image(attr.src,0,0);
		this.img.node.removeAttribute("preserveAspectRatio");
		this.text = this.paper.text(0,0,attr.text).attr({
			fill:"black",
			"font-size":this.fontSize
		});
		//前景
		this.fg=this.paper.rect(attr.x,attr.y,attr.w,attr.h).attr({
			fill:"white",
			stroke:"none",
			"fill-opacity":0
		});
		this.deviceSet.push(this.bg,this.img,this.text);
		this.refresh(attr);
		this.regEvent();
	},
	getPos:function(){
		var r = {
			x:this.fg.attr("x"),
			y:this.fg.attr("y"),
			w:this.fg.attr("width"),
			h:this.fg.attr("height")
		};
		r.cx=r.x+r.w/2;
		r.cy=r.y+r.h/2;
		return r;
	},
	toFront:function(){
		this.img.toFront();
		this.text.toFront();
		this.fg.toFront();
	},
	setTitle:function(text){
		if(text){
			this.text.attr("text",text);
		}
	},
	refresh:function(pos,isSilence){
		this.bg.attr("path",this.util.getRoundRectPath({
			x:pos.x,y:pos.y,w:pos.w,h:pos.h,r:4
		}));
		this.fg.attr({
			x:pos.x,
			y:pos.y,
			width:pos.w,
			height:pos.h
		});
		this.img.attr({
			x:pos.x+this.padding,y:pos.y+this.padding,
			width:pos.w-2*this.padding,
			height:pos.h-(this.fontSize+2*this.padding)
		});
		this.text.attr({
			x:pos.x+pos.w/2,
			y:this.img.attr("y")+this.img.attr("height")+this.fontSize/2+this.padding
		});
		if(!isSilence){
			eve("element.refresh."+this.getMaster().id,this,pos);
		}
	},
	getValue:function(){
		var p = this.getPos();
		p.text = this.text.attr("text");
		return p;
	},
	getMaster:function(){
		return this.fg;
	},
	regEvent:function(){
		var ctx = this;
		this.fg.drag(function(dx,dy){
			ctx.refresh({
				x:this.ox+dx,
				y:this.oy+dy,
				w:this.attr("width"),
				h:this.attr("height")
			});
		},function(){
			this.ox = this.attr("x");
			this.oy = this.attr("y");
		},function(){
		});
		//hover效果
		this.fg.mouseover(function(){
			ctx.oldBgColor = ctx.bg.attr("fill");
			ctx.bg.attr("fill","lightgrey");
		});
		//鼠标离开
		this.fg.mouseout(function(){
			if(ctx.oldBgColor){
				ctx.bg.attr("fill",ctx.oldBgColor);
			}
		});
		//鼠标按下
		this.fg.click(function(){
			eve("element.click",ctx);
		});
	}
};
function Connect(args){
	this.paper = args.paper;
	this.holder = args.holder;
	this.from=args.from;
	this.to=args.to;
	//装饰两个节点，使其具有需要的方法
	this.decorate(this.from);
	this.decorate(this.to);
	this.init();
	this.regEvent();
}
Connect.prototype={
	init:function(){
		var p1 = this.from.getPos(),p2=this.to.getPos();
		this.line = new TopoLine({
			paper:this.paper,
			holder:this.holder,
			p1:this.from.getConnectPoint({x:p2.cx,y:p2.cy}),
			p2:this.to.getConnectPoint({x:p1.cx,y:p1.cy})
		});
		this.synToNode(this.from);
		this.synToNode(this.to);
	},
	synToNode:function(node){
		var connects = node.getConnects();
		var flag = false;
		for(var i=0;i<connects.length;++i){
			var con = connects[i];
			if(con==this){
				flag=true;
				break;
			}
		}
		if(!flag){
			connects.push(this);
		}
	},
	toFront:function(){
		this.line.toFront();
	},
	toBack:function(){
		this.line.toBack();
	},
	regEvent:function(){
		var ctx = this;
		if(ctx.from && ctx.from.getMaster){
			var ma = ctx.from.getMaster();
			eve.on("element.refresh."+ma.id,function(){
				ctx.refresh();
			});
			eve.on("element.remove."+ma.id,function(){
				ctx.remove();
			});
			eve.on("element.tofront."+ma.id,function(){
				ctx.toFront();
			});
		}
		if(ctx.to && ctx.to.getMaster){
			var ma = ctx.to.getMaster();
			eve.on("element.refresh."+ma.id,function(){
				ctx.refresh();
			});
			eve.on("element.remove."+ma.id,function(){
				ctx.remove();
			});
			eve.on("element.tofront."+ma.id,function(){
				ctx.toFront();
			});
		}
	},
	getValue:function(){
		return {
			from:this.from.getValue(),
			to:this.to.getValue()
		};
	},
	setText:function(title){
		this.title=title;
		if(!this.text){
			this.text=this.paper.text(0,0,this.title||"");
		}
		this.refresh();
		return this.text;
	},
	remove:function(){
		//删除两端的连线信息
		if(this.from && this.from.connects){
			for(var i=0;i<this.from.connects.length;++i){
				var con = this.from.connects[i];
				if(con==this){
					delete this.from.connects[i];
				}
			}
		}
		if(this.to && this.to.connects){
			for(var i=0;i<this.to.connects.length;++i){
				var con = this.to.connects[i];
				if(con==this){
					delete this.to.connects[i];
				}
			}
		}
		this.from=null;
		this.to=null;
		if(this.text){
			this.text.remove();
		}
		this.line.remove();
	},
	refresh:function(){
		if(this.from && this.to){
			var p1 = this.from.getPos(),p2=this.to.getPos();
			this.line.firstPoint=this.from.getConnectPoint({x:p2.cx,y:p2.cy});
			this.line.secondPoint=this.to.getConnectPoint({x:p1.cx,y:p1.cy});
			this.line.refresh();
			var p = this.line.getPos();
			if(this.title && this.text){
				this.text.attr({
					text:this.title,
					x:p.cx,
					y:p.cy
				});
			}
			//刷新多链路数字
			if(this.multiRect && this.multiNumber){
				var w=this.multiRect.attr("width"),h=this.multiRect.attr("height");
				this.multiRect.attr({x:p.cx-w/2,y:p.cy-h/2});
				this.multiNumber.attr({x:p.cx,y:p.cy});
			}
			//上下行流量
			if(this.upSpeedLineTitle) this.upSpeedLineTitle.refresh();
			if(this.downSpeedLineTitle) this.downSpeedLineTitle.refresh();
		}
	},
	decorate:function(node){
		//初始化该node包含的线列表
		if(!node.getConnectPoint){
			node.getConnectPoint=function(p){
				var tmp = this.getPos();
				if(!p){
					p={x:tmp.cx,y:tmp.cy};
				}
				if(p.x==tmp.cx && p.y==tmp.cy){
					return p;
				}
				var angle = Raphael.angle(tmp.cx+10,tmp.cy,p.x,p.y,tmp.cx,tmp.cy)+360;
				var rawAngle = Raphael.angle(tmp.cx+10,tmp.cy,tmp.x+tmp.w,tmp.y,tmp.cx,tmp.cy)+360;
				var retn = {};
				if(90<angle && angle<270){
					angle = angle-180;
				}
				if(270<angle && angle<360){
					angle = angle-360;
				}
				var deg = Raphael.rad(angle);
				if(angle==0){
					retn.x=tmp.x;
					retn.y=tmp.cy;
				}else if(angle==360){
					retn.x=tmp.x+tmp.w;
					retn.y=tmp.cy;
				}else if(Math.abs(angle)<rawAngle){
					if(tmp.cx<p.x){
						retn.x=tmp.x+tmp.w;
					}else{
						retn.x=tmp.x;
					}
					if(tmp.cy<p.y){
						retn.y=tmp.cy+Math.abs((tmp.w/2)*Math.tan(deg));
					}else{
						retn.y=tmp.cy-Math.abs((tmp.w/2)*Math.tan(deg));
					}
				}else{
					if(tmp.cy<p.y){
						retn.y=tmp.y+tmp.h;
					}else{
						retn.y=tmp.y;
					}
					if(tmp.cx<p.x){
						retn.x=tmp.cx+Math.abs((tmp.h/2)*Math.tan(Math.PI/2-deg));
					}else{
						retn.x=tmp.cx-Math.abs((tmp.h/2)*Math.tan(Math.PI/2-deg));
					}
				}
				return retn;
			}
		}
		if(!node.getConnects){
			node.getConnects=function(){
				if(!this.connects){
					this.connects=[];
				}
				return this.connects;
			}
		}
	}
};
function BendConnect(args){
	this.args=args;
	this.paper = args.paper;
	this.holder = args.holder;
	this.from=args.from;
	this.to=args.to;
	//装饰两个节点，使其具有需要的方法
	this.decorate(this.from);
	this.decorate(this.to);
	this.init();
	this.regEvent();
}
BendConnect.prototype={
	init:function(){
		var p1 = this.from.getPos(),p2=this.to.getPos();
		this.line = new BendLine({
			draw:this.args.drawer,
			p1:this.from.getConnectPoint({x:p2.cx,y:p2.cy}),
			p2:this.to.getConnectPoint({x:p1.cx,y:p1.cy})
		});
		this.synToNode(this.from);
		this.synToNode(this.to);
	},
	removeBendText:function(){
		if(this.line.txtUp){
			this.line.txtUp.remove();
			this.line.txtUp=null;
		}
		if(this.line.txtDown){
			this.line.txtDown.remove();
			this.line.txtDown=null;
		}
	},
	setTextAttr:function(ars){
		if(this.line.txtUp){
			this.line.txtUp.attr(ars);
		}
		if(this.line.txtDown){
			this.line.txtDown.attr(ars);
		}
	},
	synToNode:function(node){
		var connects = node.getConnects();
		var flag = false;
		for(var i=0;i<connects.length;++i){
			var con = connects[i];
			if(con==this){
				flag=true;
				break;
			}
		}
		if(!flag){
			connects.push(this);
		}
	},
	toFront:function(){
		this.line.toFront();
	},
	toBack:function(){
		this.line.toBack();
	},
	regEvent:function(){
		var ctx = this;
		if(ctx.from && ctx.from.getMaster){
			var ma = ctx.from.getMaster();
			eve.on("element.refresh."+ma.id,function(){
				ctx.refresh();
			});
			eve.on("element.remove."+ma.id,function(){
				ctx.remove();
			});
			eve.on("element.tofront."+ma.id,function(){
				ctx.toFront();
			});
		}
		if(ctx.to && ctx.to.getMaster){
			var ma = ctx.to.getMaster();
			eve.on("element.refresh."+ma.id,function(){
				ctx.refresh();
			});
			eve.on("element.remove."+ma.id,function(){
				ctx.remove();
			});
			eve.on("element.tofront."+ma.id,function(){
				ctx.toFront();
			});
		}
	},
	getValue:function(){
		return {
			from:this.from.getValue(),
			to:this.to.getValue()
		};
	},
	setText:function(title){
		this.line.setTitle(title,"up");
		return this.line.txtUp;
	},
	remove:function(){
		//删除两端的连线信息
		if(this.from && this.from.connects){
			for(var i=0;i<this.from.connects.length;++i){
				var con = this.from.connects[i];
				if(con==this){
					delete this.from.connects[i];
				}
			}
		}
		if(this.to && this.to.connects){
			for(var i=0;i<this.to.connects.length;++i){
				var con = this.to.connects[i];
				if(con==this){
					delete this.to.connects[i];
				}
			}
		}
		this.from=null;
		this.to=null;
		if(this.text){
			this.text.remove();
		}
		this.line.remove();
	},
	refresh:function(){
		if(this.from && this.to){
			var p1 = this.from.getPos(),p2=this.to.getPos();
			this.line.firstPoint=this.from.getConnectPoint({x:p2.cx,y:p2.cy});
			this.line.secondPoint=this.to.getConnectPoint({x:p1.cx,y:p1.cy});
			this.line.refresh(this.line.firstPoint,this.line.secondPoint);
		}
	},
	decorate:function(node){
		//初始化该node包含的线列表
		if(!node.getConnectPoint){
			node.getConnectPoint=function(p){
				var tmp = this.getPos();
				if(!p){
					p={x:tmp.cx,y:tmp.cy};
				}
				if(p.x==tmp.cx && p.y==tmp.cy){
					return p;
				}
				var angle = Raphael.angle(tmp.cx+10,tmp.cy,p.x,p.y,tmp.cx,tmp.cy)+360;
				var rawAngle = Raphael.angle(tmp.cx+10,tmp.cy,tmp.x+tmp.w,tmp.y,tmp.cx,tmp.cy)+360;
				var retn = {};
				if(90<angle && angle<270){
					angle = angle-180;
				}
				if(270<angle && angle<360){
					angle = angle-360;
				}
				var deg = Raphael.rad(angle);
				if(angle==0){
					retn.x=tmp.x;
					retn.y=tmp.cy;
				}else if(angle==360){
					retn.x=tmp.x+tmp.w;
					retn.y=tmp.cy;
				}else if(Math.abs(angle)<rawAngle){
					if(tmp.cx<p.x){
						retn.x=tmp.x+tmp.w;
					}else{
						retn.x=tmp.x;
					}
					if(tmp.cy<p.y){
						retn.y=tmp.cy+Math.abs((tmp.w/2)*Math.tan(deg));
					}else{
						retn.y=tmp.cy-Math.abs((tmp.w/2)*Math.tan(deg));
					}
				}else{
					if(tmp.cy<p.y){
						retn.y=tmp.y+tmp.h;
					}else{
						retn.y=tmp.y;
					}
					if(tmp.cx<p.x){
						retn.x=tmp.cx+Math.abs((tmp.h/2)*Math.tan(Math.PI/2-deg));
					}else{
						retn.x=tmp.cx-Math.abs((tmp.h/2)*Math.tan(Math.PI/2-deg));
					}
				}
				return retn;
			}
		}
		if(!node.getConnects){
			node.getConnects=function(){
				if(!this.connects){
					this.connects=[];
				}
				return this.connects;
			}
		}
	}
};
function ChoserBox(args){
	this.paper=args.paper;
	this.util = new TopoUtil(args);
	this.$svg=this.util.getSvg();
	this.editable = true;
	this.init();
	this.regEvent();
};
ChoserBox.prototype={
	remove:function(silence){
		this.areaPath=null;
		this.border.remove();
		this.border=null;
		clearInterval(this.timeId);
		this.timeId=null;
		delete this.firstPoint;
		this.firstPoint=null;
		delete this.secondPoint;
		this.secondPoint=null;
		if(!silence){
			eve("element.remove",this);
		}
	},
	getValue:function(){
		if(this.firstPoint && this.secondPoint){
			var r = {
				x1:this.firstPoint.x || 0,
				y1:this.firstPoint.y || 0,
				x2:this.secondPoint.x || 0,
				y2:this.secondPoint.y || 0
			};
			if(r.x1>r.x2){
				r.x1=this.secondPoint.x;
				r.x2=this.firstPoint.x;
			}
			if(r.y1>r.y2){
				r.y1=this.secondPoint.y;
				r.y2=this.firstPoint.y;
			}
			r.cx=(r.x1+r.x2)/2;
			r.cy=(r.y1+r.y2)/2;
			r.w=Math.abs(r.x2-r.x1);
			r.h=Math.abs(r.y2-r.y1);
			return r;
		}
		return null;
	},
	setEditable:function(flag){
		this.editable = !!flag;
		if(this.editable){
			this.$svg.css("cursor","crosshair");
		}else{
			this.$svg.css("cursor","default");
		}
	},
	regEvent:function(){
		var ctx = this;
		eve.on("svg_mouse_down",function(p){
			if(ctx.editable){
				var vb = ctx.util.getViewBox();
				ctx.firstPoint={
					x:(p.x||p.pageX),
					y:(p.y||p.pageY)
				};
				//鼠标单击的时候才开始初始化
				ctx.border = ctx.paper.path(ctx.util.getRoundRectPath({
					x:0,y:0,r:0,w:0,h:0
				})).attr({
					"stroke":"black",
					"fill":"white",
					"opacity":0.5
				});
				//打开定时器，造成一个蚂蚁线效果
				ctx.timeId=setInterval(function(){
					ctx.border.attr({
						"stroke-dasharray":ctx.border.flag?"--":"- "
					});
					ctx.border.flag=!!!ctx.border.flag;
				},100);
				var tasks = oc.index.indexLayout.data("tasks");
				if(tasks && tasks.length > 0){
					oc.index.indexLayout.data("tasks").push(ctx.timeId);
				}else{
					tasks = new Array();
					tasks.push(ctx.timeId);
					oc.index.indexLayout.data("tasks", tasks);
				}
			}
		});
		eve.on("svg_mouse_up",function(p){
			if(ctx.editable){
				ctx.secondPoint=p;
				eve("element.choserbox.finished",ctx,ctx.getValue());
				clearInterval(ctx.timeId);
				ctx.border.remove();
				ctx.firstPoint=null;
				delete ctx.firstPoint;
				ctx.secondPoint=null;
				delete ctx.secondPoint;
			}
		});
		eve.on("svg_mouse_move",function(p,e){
			if(ctx.editable){//切鼠标左键按下
				if(!ctx.secondPoint){
					ctx.secondPoint={};
				}
				//eve("element.drag.beforemove",ctx,p);
				ctx.secondPoint.x=p.x;
				ctx.secondPoint.y=p.y;
				ctx.refresh();
			}
		});
	},
	refresh:function(){
		if(this.firstPoint){
			this.areaPath=this.util.getRoundRectPath({
				x:this.firstPoint.x,
				y:this.firstPoint.y,
				r:0,
				w:this.secondPoint.x-this.firstPoint.x,
				h:this.secondPoint.y-this.firstPoint.y
			});
			this.border.attr("path",this.areaPath);
		}
	},
	init:function(){
	}
};
function ImageSet(args){
	this.setList(args.list);
	this.paper = args.paper;
	this.editable=true;
	this.init();
};
ImageSet.prototype={
	setList:function(list){
		this.list = list || [];
		this.pos = this._getContainerBox();
	},
	getPos:function(){
		var r = {
			x:this.fg.attr("x"),
			y:this.fg.attr("y"),
			w:this.fg.attr("width"),
			h:this.fg.attr("height")
		};
		r.cx = r.x+r.w/2;
		r.cy = r.y+r.h/2;
		return r;
	},
	init:function(){
		//绘制遮罩
		this.fg = this.paper.rect(this.pos.x,this.pos.y,this.pos.w,this.pos.h).attr({
			fill:"white",
			"fill-opacity":0,
			"stroke":null
		});
		this.regEvent();
	},
	setState:function(state){
		for(var i=0;i<this.list.length;++i){
			var item = this.list[i];
			if(item.setState){
				item.setState(state);
			}
		}
	},
	getMaster:function(){
		return this.fg;
	},
	getValue:function(){
		var retn = {
			type:"imageSet",
			list:[]
		};
		for(var i=0;i<this.list.length;++i){
			var item = this.list[i];
			if(item.getValue){
				retn.list.push(item.getValue());
			}
		}
		return retn;
	},
	//重置，发布删除事件
	reset:function(){
		for(var i=0;i<this.list.length;++i){
			this.list[i].remove(true);
		}
		this.fg.attr({
			x:0,y:0,w:0,h:0
		});
		this.setList();
	},
	//设置值，需要一个回调函数，回调函数负责将元素绘制出来,callBack 返回绘制的元素
	setValue:function(info,callBack){
		//列表
		if(callBack){
			//绘制元素
			if(info.list){
				this.reset();
				var elemlist = [];
				for(var i=0;i<info.list.length;++i){
					var item = info.list[i];
					var el = callBack.call(this,item);
					elemlist.push(el);
				}
				this.setList(elemlist);
				this.refresh(this.pos);
				this.fg.toFront();
			}
		}
	},
	refresh:function(pos,silence){
		this.pos=$.extend(this.pos,pos);
		var wratio=this.pos.w/this.width,hratio=this.pos.h/this.height;
		for(var i=0;i<this.list.length;++i){
			var item = this.list[i];
			var p = item.getPos();
			var relativePos = item.relativePos;
			p.x=(relativePos.x)*wratio+this.pos.x;
			p.y=(relativePos.y)*hratio+this.pos.y;
			p.w=relativePos.w*wratio;
			p.h=relativePos.h*hratio;
			item.refresh(p,true);
		}
		this.fg.attr({
			x:this.pos.x,
			y:this.pos.y,
			width:this.pos.w,
			height:this.pos.h
		});
		if(!silence){
			eve("element.refresh",this,this.pos);
		}
	},
	regEvent:function(){
		var ctx = this;
		this.fg.drag(function(dx,dy){
			if(ctx.editable){
				ctx.refresh({
					x:dx+this.ox,
					y:dy+this.oy
				});
			}
		},function(){
			this.ox=this.attr("x");
			this.oy=this.attr("y");
		},function(){
		});
		this.fg.click(function(e){
			if(ctx.onclick){
				ctx.onclick();
			}
			eve("element.click",ctx,e);
		});
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	},
	remove:function(silence){
		for(var i=0;i<this.list.length;++i){
			this.list[i].remove(true);
		}
		this.fg.remove();
		if(!silence){
			eve("element.remove",this);
		}
	},
	//包容边框
	_getContainerBox:function(){
		if(this.list.length>0){
			var box = this.list[0].getPos();
			//计算框
			for(var i=1;i<this.list.length;++i){
				var item = this.list[i];
				if(item.getPos){
					var pos = item.getPos();
					if(box.x>pos.x){
						box.x=pos.x;
					}
					if(box.y>pos.y){
						box.y=pos.y;
					}
					var tmpW = pos.x+pos.w;
					if(box.w<tmpW){
						box.w=tmpW-box.x;
					}
					var tmpH = pos.y+pos.h;
					if(box.h<tmpH){
						box.h=tmpH-box.y;
					}
				}
			}
			//计算每一个项目的相对位置
			for(var i=0;i<this.list.length;++i){
				var item = this.list[i];
				var pos = item.getPos();
				item.relativePos={
					x:pos.x-box.x,
					y:pos.y-box.y,
					w:pos.w,
					h:pos.h
				};
			}
			this.width = box.w;
			this.height = box.h;
			return box;
		}else{
			return {x:0,y:0,w:0,h:0};
		}
	},
	setEditable:function(flag){
		this.editable=!!flag;
		for(var i=0;i<this.list.length;++i){
			var item = this.list[i];
			item.setEditable(this.editable);
		}
	}
};
function TopoLine(args){
	this.args=$.extend({
		paper:null,
		holder:null,
		id:null,
		p1:null,
		p2:null
	},args);
	if(!this.args.paper || !this.args.holder){
		throw "Line holder || paper can't be null";
	}
	this.paper = args.paper;
	this.holder = args.holder;
	this.util = new TopoUtil(this.args);
	this.editable = !(args.p1 && args.p2);
	this.firstPoint = null;
	this.secondPoint = null;
	this.color="black";
	this.weight=1;
	this.style="solid";
	this.type=args.type || "line";
	this.br = 1;
	this.attr = args.attr;
	this.init();
	this.regEvent();
	if(!this.editable){
		this.firstPoint=args.p1;
		this.secondPoint=args.p2;
		this.refresh();
	}
}
TopoLine.prototype={
	init:function(){
		this.line = this.paper.path("M0 0L0 0");
		/*this.glowSet=this.line.glow({
			opacity:0.4,
			color:"blue",
			width:6+this.weight
		});*/
	},
	toFront:function(){
		//this.glowSet.toFront();
		this.line.toFront();
		if(this.text){
			this.text.toFront();
		}
	},
	toBack:function(){
		this.line.toBack();
		//this.glowSet.toBack();
	},
	setEditable:function(flag){
		this.editable = !!flag;
	},
	remove:function(){
		this.line.remove();
		//this.glowSet.remove();
		if(this.arrow) this.arrow.remove();
		this.clear();
	},
	onFinished:function(){
		this.setEditable(false);
	},
	hide:function(){
		this.line.hide();
		//this.glowSet.hide();
		if(this.arrow) this.arrow.hide();
	},
	show:function(){
		this.line.show();
		//this.glowSet.show();
		if(this.arrow) this.arrow.show();
	},
	regEvent:function(){
		var ctx = this;/* 
		this.line.click(function(){
			eve("element.click."+this.id,ctx);
		});
		this.glowSet.click(function(){
			eve("element.click."+ctx.line.id,ctx);
		});
		if(this.text){
			this.text.click(function(){
				eve("element.click."+ctx.line.id,ctx);
			});
		} */
	},
	getPos:function(){
		if(this.firstPoint && this.secondPoint){
			var r = {
				x1:this.firstPoint.x,
				y1:this.firstPoint.y,
				x2:this.secondPoint.x,
				y2:this.secondPoint.y
			};
			r.cx=(r.x1+r.x2)/2;
			r.cy=(r.y1+r.y2)/2;
			r.length = Math.sqrt(Math.pow(r.x1-r.x2,2)+Math.pow(r.y1-r.y2,2));
			return r;
		}
		return null;
	},
	getValue:function(){
		var v = this.getPos();
		v.angle = this.getAngle();
		v.type="line";
		v.color = this.color;
		v.style = this.style;
		v.weight = this.weight;
		return v;
	},
	setColor:function(color){
		this.color = color || this.color;
		this.line.attr("stroke",this.color);
		if(this.arrow) this.arrow.attr({
			stroke:this.color,
			fill:this.color
		});
		this.refresh();
	},
	setBr:function(br){
		this.br = br;
	},
	setStyle:function(style){
		this.style = style || this.style;
		switch(this.style){
			case "solid" :
				this.line.attr("stroke-dasharray","");
			break;
			case "dash" :
				this.line.attr("stroke-dasharray",". ");
			break;
			case "interval" :
				this.line.attr("stroke-dasharray","-");
			break;
			case "intervalLong" :
				this.line.attr("stroke-dasharray","--");
			break;
			default:
				this.line.attr("stroke-dasharray",style);
		}
		this.refresh();
	},
	setWeight:function(weight){
		this.weight = weight || this.weight;
		this.line.attr("stroke-width",this.weight);
		this.refresh();
	},
	setValue:function(v){
		this.firstPoint={
			x:v.x1,y:v.y1
		};
		this.secondPoint={
			x:v.x2,y:v.y2
		};
		this.setColor(v.color);
		this.setWeight(v.weight);
		this.setStyle(v.style);
		this.refresh();
	},
	getAngle:function(){
		var p = this.getPos();
		var tp = this.line.getPointAtLength(p.length-1);
		var angle = Raphael.angle(tp.x,tp.y,p.x2,p.y2-1,p.x2,p.y2);
		return angle+180;
	},
	showArrow:function(flag){
		var ctx = this;
		this.showArrowFlag=!!flag;
		if(this.showArrowFlag){
			if(!this.arrow){
				this.arrow=this.paper.path("M0 0L0 0").attr({
					fill:this.line.attr("stroke"),
					"stroke":null
				});
				this.arrow.refresh=function(){
					var p = ctx.getPos();
					if(p){
						p.x=p.x2;
						p.y=p.y2;
						p.w=3*parseInt(ctx.line.attr("stroke-width"));
						p.w = p.w<8?p.w+8:p.w;
						var h = (Math.sin(Math.PI/3)*p.w)/2;
						this.attr("path",[
							["M",p.x,p.y-h],
							["L",p.x-p.w/2,p.y+h],
							["L",p.x,p.y],
							["L",p.x+p.w/2,p.y+h]
						]);
						this.transform("r"+ctx.getAngle()+","+p.x+","+p.y);
					}
				};
			}
			this.arrow.show();
			this.arrow.refresh();
		}else{
			if(this.arrow){
				this.arrow.hide();
			}
		}
	},
	clear:function(){
		this.firstPoint = null;
		this.secondPoint = null;
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	},
	refresh:function(){
		if(this.firstPoint && this.secondPoint){
			var p = this.getPos();
			//直线
			if(this.type == "line"){
				this.line.attr("path",[
					["M",this.firstPoint.x,this.firstPoint.y],
					["L",this.secondPoint.x,this.secondPoint.y]
				])
			}
			//弧线
			else if(this.type == "arc"){
				var angle = Raphael.angle(p.x1,p.y1,p.x2,p.y2-1,p.x2,p.y2)+180;
				var rad = Raphael.rad(angle);
				var dx = Math.sin(rad)*this.br,dy=Math.cos(rad)*this.br;
				p.cx=p.cx+dy,p.cy=p.cy+dx;
				this.line.attr("path",[
					["M",p.x1,p.y1],
					["C",p.cx+dx,p.cy-dy,p.cx-dx,p.cy+dy,p.x2,p.y2]
				]);
			}
			//光晕
			/*this.glowSet.attr({
				stroke:this.color,
				path:this.line.attr("path"),
				"stroke-width":this.weight+2
			});*/
			if(this.arrow){
				this.arrow.refresh();
			}
			if(this.onrefresh){
				this.onrefresh(p);
			}
		}
	}
};/**
	连接两个节点，依赖于line.js
*/
function Link(args){
	this.paper=args.paper;
	this.holder=args.holder;
	this.$svg = $(this.holder).find("svg");
	this.editable=false;
	this.init();
	this.regEvent();
};
Link.prototype={
	add:function(node){
		if(!this.editable) return;
		if(this.fromNode){
			this.toNode=node;
			this._onEnd(this.fromNode,this.toNode);
		}else{
			this.fromNode=node;
			this.decorate(this.fromNode);
			this.show();
		}
	},
	init:function(){
		this.fromNode=null;
		this.toNode=null;
		this.line = this.paper.path("M0 0L0 0").attr("stroke","blue");
		/*this.glowSet = this.line.glow({
			color:"blue",
			opacity:0.3,
			width:2
		});*/
		this.hide();
	},
	hide:function(){
		this.line.hide();
		//this.glowSet.hide();
	},
	show:function(){
		this.line.show();
		//this.glowSet.show();
	},
	setEditable:function(flag){
		this.editable=!!flag;
		if(this.editable){
			this.$svg.css("cursor","crosshair");
		}else{
			this.$svg.css("cursor","default");
		}
	},
	remove:function(){
		this.line.remove();
		//this.glowSet.remove();
	},
	refresh:function(p1,p2){
		this.line.attr("path",[
			["M",p1.x,p1.y],
			["L",p2.x,p2.y]
		]);
		//this.glowSet.attr("path",this.line.attr("path"));
	},
	regEvent:function(){
		var ctx = this;
		eve.on("svg_mouse_move",function(p){
			if(ctx.fromNode && ctx.editable){
				var p1 = ctx.fromNode.getConnectPoint(p);
				ctx.refresh(p1,p);
			}
		});
		//ESC取消连线功能
		eve.on("doc_key_down",function(e){
			if(e.keyCode==27){
				if(!ctx.line.node){
					ctx.init();
				}
				ctx.clear();
				ctx.setEditable(false);
			}
			if(e.altKey && e.keyCode==90){
				if (!ctx.line.node || !ctx.line.node.parentNode) {
                    ctx.init();
                };
				ctx.setEditable(true);
			}
		});
		//加入连线
		eve.on("element.click",function(){
			ctx.add(this);
		});
	},
	decorate:function(node){
		if(node.getConnectPoint) return ;
		node.getConnectPoint=function(p){
			var tmp = this.getPos();
			if(!p){
				p={x:tmp.cx,y:tmp.cy};
			}
			if(p.x==tmp.cx && p.y==tmp.cy){
				return p;
			}
			var angle = Raphael.angle(tmp.cx+10,tmp.cy,p.x,p.y,tmp.cx,tmp.cy)+360;
			var rawAngle = Raphael.angle(tmp.cx+10,tmp.cy,tmp.x+tmp.w,tmp.y,tmp.cx,tmp.cy)+360;
			var retn = {};
			if(90<angle && angle<270){
				angle = angle-180;
			}
			if(270<angle && angle<360){
				angle = angle-360;
			}
			var deg = Raphael.rad(angle);
			if(angle==0){
				retn.x=tmp.x;
				retn.y=tmp.cy;
			}else if(angle==360){
				retn.x=tmp.x+tmp.w;
				retn.y=tmp.cy;
			}else if(Math.abs(angle)<rawAngle){
				if(tmp.cx<p.x){
					retn.x=tmp.x+tmp.w;
				}else{
					retn.x=tmp.x;
				}
				if(tmp.cy<p.y){
					retn.y=tmp.cy+Math.abs((tmp.w/2)*Math.tan(deg));
				}else{
					retn.y=tmp.cy-Math.abs((tmp.w/2)*Math.tan(deg));
				}
			}else{
				if(tmp.cy<p.y){
					retn.y=tmp.y+tmp.h;
				}else{
					retn.y=tmp.y;
				}
				if(tmp.cx<p.x){
					retn.x=tmp.cx+Math.abs((tmp.h/2)*Math.tan(Math.PI/2-deg));
				}else{
					retn.x=tmp.cx-Math.abs((tmp.h/2)*Math.tan(Math.PI/2-deg));
				}
			}
			return retn;
		}
	},
	clear:function(){
		this.line.attr("path","M0 0L0 0");
		//this.glowSet.attr("path",this.line.attr("path"));
		this.fromNode=null;
		this.toNode=null;
		this.setEditable(false);
		this.hide();
	},
	onEnd:function(fromNode,toNode){
		var con = new Connect({
			paper:this.paper,holder:this.holder,from:fromNode,to:toNode
		});
		eve("element.link");
	},
	//连线结束事件
	_onEnd:function(fromNode,toNode){
		if(fromNode == toNode){
			return ;
		}
		this.onEnd(fromNode,toNode);
		this.clear();
	}
};

function Rect(args){
	this.paper = args.paper;
	this.holder = args.holder;
	this.util = new TopoUtil(args);
	this.editable=true;
	this.pos=$.extend({x:0,y:0,w:0,h:0,r:0},args.pos);
	this.init();
	this.regEvent();
}
Rect.prototype={
	init:function(){
		this.rect = this.paper.path("M0 0L0 0Z").attr("fill","#F2F2F2");
		this.rect.getPos=function(){
			return $.extend({x:0,y:0,w:0,h:0,r:0},this.pos);
		};
		this.refresh(this.pos);
		this.toFront();
	},
	getPos:function(){
		var p = this.rect.getPos();
		if(p){
			p.cx = p.x+p.w/2;
			p.cy = p.y+p.h/2;
			return p;
		}
	},
	setEditable:function(flag){
		this.editable = !!flag;
	},
	refresh:function(pos){
		this.rect.pos = pos;
		if(this.editable){
			this.rect.attr("path",this.util.getRoundRectPath(pos));
			eve("element.refresh."+this.getMaster().id,this,pos);
			eve("element.drag.move",this,pos);
		}
	},
	setValue:function(v){
		this.refresh(v);
		if(v.attr){
			this.rect.attr(v.attr);
		}
	},
	getValue:function(){
		var p = this.rect.pos;
		p.type="rect";
		p.attr=this.rect.attrs;
		return p;
	},
	rangeCheck:function(pos){
		if(pos.w<10||pos.h<10){
			return false;
		}else{
			return true;
		}
	},
	remove:function(){
		eve("element.remove."+this.getMaster().id,this);
		this.rect.remove();
	},
	regEvent:function(){
		var ctx = this;
		this.rect.drag(function(dx,dy){
			if(ctx.editable){
				ctx.refresh($.extend(this.pos,{
					x:dx+this.ox,
					y:dy+this.oy
				}));
			}
		},function(){
			if(ctx.editable){
				this.ox = this.pos.x;
				this.oy = this.pos.y;
				//处理单击或者右键时，自定义矩形图形始终置于图层的顶端的现象
				//ctx.toFront();
			}
		},function(){
			eve("element.click."+ctx.getMaster().id,ctx);
		});
		//点击事件
		this.rect.click(function(e){
			eve("element.click."+ctx.getMaster().id,ctx,e);
		});
		//右键
		$(this.rect.node).on("contextmenu",function(e){
			if(ctx.editable){
				eve("element.contextmenu",ctx,e,ctx);
			}
			e.stopPropagation();
			return false;
		});
	},
	getMaster:function(){
		return this.rect;
	},
	toFront:function(){
		this.rect.toFront();
	},
	
	toBack:function(){
		this.rect.toBack();
	}
	
};
function Sizer(args){
	args=$.extend({
		node:{
			getPos:function(){
				return {
					x:0,y:0,w:0,h:0
				};
			}
		},
		paper:null
	},args);
	this.paper=args.paper;
	this.node=args.node;
	this.editable=true;
	this.angle = 0;
	this.dragSet = this.paper.set();
	this.init();
	this.regEvent();
};
Sizer.prototype={
	regEvent:function(){
		var ctx = this;
		this.dragSet.drag(function(dx,dy){
			ctx.dragMove(dx,dy,this);
		},function(){
			ctx.dragStart(this);
		},function(){
			ctx.dragEnd(this);
		});
		eve.on("doc_key_down",function(e){
			if(ctx.editable && ctx.node && ctx.node.getPos){
				var pos = ctx.node.getPos();
				//微调
				if(e.keyCode==37){//左键
					pos.x-=1;
					pos.cx-=1;
					ctx.refresh(pos);
				}else if(e.keyCode==38){//上键
					pos.y-=1;
					pos.cy-=1;
					ctx.refresh(pos);
				}else if(e.keyCode==39){//右键
					pos.x+=1;
					pos.cx+=1;
					ctx.refresh(pos);
				}else if(e.keyCode==40){//下键
					pos.y+=1;
					pos.cy+=1;
					ctx.refresh(pos);
				}else if(e.keyCode==16){//shift键
					ctx.node.d.isShiftDown = true;
				}
			}
		});
		eve.on("doc_key_up",function(e){
			if(ctx.editable && ctx.node && ctx.node.getPos){
				var pos = ctx.node.getPos();
				if(e.keyCode==16){//shift键
					ctx.node.d.isShiftDown = false;
				}
			}
		});
		//监听元素的刷新
		eve.on("element.refresh.*",function(pos){
			if(ctx.node == this){
				ctx.setValue(pos);
			}
		});
		//接收删除事件
		eve.on("element.remove.*",function(){
			if(this==ctx.node){
				ctx.hide();
			}
		});
	},
	getMaster:function(){
		return this.rect;
	},
	setEditable:function(flag){
		this.editable=!!flag;
		if(this.editable){
			this.show();
		}else{
			this.hide();
		}
	},
	setNode:function(node){
		if(!node||!node.getPos) return ;
		var ctx = this;
		this.node=node;
		this.refresh(this.node.getPos());
	},
	setValue:function(pos){
		if(pos && pos.w>0 && pos.h>0){
			var r=3;
			this.tl.attr({
				x:pos.x-r,y:pos.y-r
			});
			this.tc.attr({
				x:pos.x-r+pos.w/2,y:pos.y-r
			});
			this.tr.attr({
				x:pos.x-r+pos.w,y:pos.y-r
			});
			this.rc.attr({
				x:pos.x-r+pos.w,y:pos.y-r+pos.h/2
			});
			this.br.attr({
				x:pos.x-r+pos.w,y:pos.y-r+pos.h
			});
			this.bc.attr({
				x:pos.x-r+pos.w/2,y:pos.y-r+pos.h
			});
			this.bl.attr({
				x:pos.x-r,y:pos.y-r+pos.h
			});
			this.lc.attr({
				x:pos.x-r,y:pos.y-r+pos.h/2
			});
			this.rect.attr({
				x:pos.x,
				y:pos.y,
				width:pos.w,
				height:pos.h
			});
		}
	},
	rotage:function(angle){
		this.angle = angle || this.angle;
		var p = this.node.getPos();
		var trs = [
			["r",this.angle,p.cx,p.cy]
		];
		this.dragSet.transform(trs);
		this.rect.transform(trs);
		if(this.node.rotage){
			this.node.rotage(trs);
		}
	},
	//安全范围检查
	_rangeCheck:function(pos){
		if(this.node.rangeCheck){
			return this.node.rangeCheck(pos);
		}
		if(pos.w<=0 || pos.h<=0){
			return false;
		}else{
			return true;
		}
	},
	getPos:function(){
		var r = {
			x:this.rect.attr("x"),
			y:this.rect.attr("y"),
			w:this.rect.attr("width"),
			h:this.rect.attr("height")
		};
		r.cx=r.x+r.w/2;
		r.cy=r.y+r.h/2;
		return r;
	},
	dragMove:function(dx,dy,re){
		if(this.editable){
			var pos = re.pos;
			var p = {};
			var w = re.attr("width"),h = re.attr("height"),type = re.type;
			switch(type){
				case "tl":
					p.x=pos.x+dx;
					p.y=pos.y+dy;
					p.w=pos.w-dx;
					p.h=pos.h-dy;
				break;
				case "tr":
					p.x=pos.x;
					p.y=pos.y+dy;
					p.w=pos.w+dx;
					p.h=pos.h-dy;
				break;
				case "br":
					p.x=pos.x;
					p.y=pos.y;
					p.w=pos.w+dx;
					p.h=pos.h+dy;
				break;
				case "bl":
					p.x=pos.x+dx;
					p.y=pos.y;
					p.w=pos.w-dx;
					p.h=pos.h+dy;
				break;
				case "tc":
					p.x=pos.x;
					p.y=pos.y+dy;
					p.w=pos.w;
					p.h=pos.h-dy;
				break;
				case "rc":
					p.x=pos.x;
					p.y=pos.y;
					p.w=pos.w+dx;
					p.h=pos.h;
				break;
				case "bc":
					p.x=pos.x;
					p.y=pos.y;
					p.w=pos.w;
					p.h=pos.h+dy;
				break;
				case "lc":
					p.x=pos.x+dx;
					p.y=pos.y;
					p.w=pos.w-dx;
					p.h=pos.h;
				break;
			};
			p.cx=p.x+p.w/2;
			p.cy=p.y+p.h/2;
			
			//处理等比例拖动
			if(this.node.d.isShiftDown){	//不存在图片比例百分比则添加上该属性
				if(this.node && this.node.d && !this.node.d.percent) this.node.d.percent = p.h/p.w;
				
				if(type == "lc" || type == "rc"){	//水平拖动
					p.h = this.node.d.percent*p.w;
				}else{	//其他位置拖动
					p.w = p.h/this.node.d.percent;
				}
			}
			eve("element.drag.move",this,p);
			if(this._rangeCheck(p)){
				this.refresh(p);
			}
		}
	},
	dragStart:function(re){
		if(this.editable){
			re.ox=re.attr("x");
			re.oy=re.attr("y");
			re.pos=this.node.getPos();
		}
	},
	dragEnd:function(node){
		eve("element.sizer.dragend."+this.getMaster().id,this);
	},
	refresh:function(pos){
		this.setValue(pos);
		if(this.editable){
			this.onFresh(pos);
			if(this.node && this.node.refresh){
				this.node.refresh(pos);
			}
			this.toFront();
		}
	},
	remove:function(){
		this.dragSet.remove();
		this.rect.remove();
	},
	hide:function(){
		this.dragSet.hide();
		this.rect.hide();
	},
	show:function(){
		if(this.editable && this.node.getPos && this.node.editable){
			this.dragSet.show();
			this.rect.show();
		}else{
			this.hide();
		}
	},
	onFresh:function(pos){},
	init:function(){
		var pos = this.node.getPos();
		var w=6,r=3;
		this.rect = this.paper.rect(pos.x,pos.y,pos.w,pos.h);
		var title="按下Shift键等比缩放";
		this.tl=this.paper.rect(pos.x-r,pos.y-r,w,w).attr({fill:"white",cursor:"nw-resize","title":title});
		this.tl.type="tl";
		this.tc=this.paper.rect(pos.x-r+pos.w/2,pos.y-r,w,w).attr({fill:"white",cursor:"n-resize","title":title});
		this.tc.type="tc";
		this.tr=this.paper.rect(pos.x-r+pos.w,pos.y-r,w,w).attr({fill:"white",cursor:"ne-resize","title":title});
		this.tr.type="tr";
		this.rc=this.paper.rect(pos.x-r+pos.w,pos.y-r+pos.h/2,w,w).attr({fill:"white",cursor:"e-resize","title":title});
		this.rc.type="rc";
		this.br=this.paper.rect(pos.x-r+pos.w,pos.y-r+pos.h,w,w).attr({fill:"white",cursor:"se-resize","title":title});
		this.br.type="br";
		this.bc=this.paper.rect(pos.x-r+pos.w/2,pos.y-r+pos.h,w,w).attr({fill:"white",cursor:"s-resize","title":title});
		this.bc.type="bc";
		this.bl=this.paper.rect(pos.x-r,pos.y-r+pos.h,w,w).attr({fill:"white",cursor:"sw-resize","title":title});
		this.bl.type="bl";
		this.lc=this.paper.rect(pos.x-r,pos.y-r+pos.h/2,w,w).attr({fill:"white",cursor:"w-resize","title":title});
		this.lc.type="lc";
		this.dragSet.push(this.tl,this.tc,this.tr,this.rc,this.br,this.bc,this.bl,this.lc);
	},
	toFront:function(){
		this.rect.toFront();
		this.dragSet.toFront();
	}
};
function SvgEvent(args){
	var $svg = $(args.holder).find(">svg");
	if(Raphael.vml){
		$svg=$(args.holder).find(">div");
	}
	this.$svg = $svg;
	var util = new TopoUtil(args);
	$svg.off();
	$svg.on("mousemove",function(e){
		var r = util.getEventPosition(e);
		eve("svg_mouse_move",this,r,e);
	});
	$svg.on("mousedown",function(e){
		var r = util.getEventPosition(e);
		eve("svg_mouse_down",this,r,e);
	});
	$svg.on("mouseup",function(e){
		var p = util.getEventPosition(e);
		eve("svg_mouse_up",this,p,e);
	});
	$svg.on("click",function(e){
		if(e.target && e.target.tagName=="svg"){
			var p = util.getEventPosition(e);
			eve("svg_mouse_click",this,p,e);
		}
	});
	$svg.on("contextmenu",function(e){
		var p = util.getEventPosition(e);
		eve("svg.contextmenu",this,p,e);
		e.stopPropagation();
		return false;
	});
	$(document).on("keydown",function(e){
		eve("doc_key_down",this,e);
	});
	$(document).on("keyup",function(e){
		eve("doc_key_up",this,e);
	});
	eve.on("element.drag.beforemove",function(pos){
		util.realPos(pos);
	});
};
SvgEvent.prototype={
	setEditable:function(flag){
		this.editable=!!flag;
		if(!this.editable){
			this.$svg.off();
		}
	}
};
//依赖于Sizer
function TopoTextArea(args){
	this.paper=args.paper;
	this.pos=$.extend({x:0,y:0,w:100,h:100},args.pos);
	if(this.pos.w<10 || this.pos.h<10){
		this.pos.h=100;this.pos.w=100;
	}
	this.holder=args.holder;
	this.fontSize=12;
	this.fontColor="black";
	this.$svg = $(this.holder);
	this.editable=true;
	this.util = new TopoUtil(args);
	this.init();
};
TopoTextArea.prototype={
	init:function(){
		var ctx= this;
		//前景
		this.fp = this.paper.rect(this.pos.x,this.pos.y,this.pos.w,this.pos.h).attr({
			fill:"white",
			"fill-opacity":"0",
			"stroke":null
		});
		//背景
		this.bg=this.paper.rect(this.pos.x,this.pos.y,this.pos.w,this.pos.h).attr({
			fill:"white",
			"fill-opacity":"0",
			"stroke":null
		});
		this.bg.toBack();
		this.texts = this.paper.set();
		this.ipt = $("<textarea></textarea>");
		this.ipt.css({
			"width":"100%",
			"height":"100%",
			"display":"block",
			"min-width":"auto"
		});
		this.dialog=$("<div></div>");
		this.regEvent();
	},
	openDialog:function(){
		var ctx = this;
		this.ipt.appendTo(this.dialog);
		this.dialog.dialog({
			title:"编辑文本",
			width:300,
			height:200,
			buttons:[{
				text:"确定",
				handler:function(){
					ctx.dialog.dialog("close");
					var txt = ctx.ipt.val();
					ctx.setText(txt);
				}
			},{
				text:"取消",
				handler:function(){
					ctx.dialog.dialog("close");
				}
			}]
		});
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	},
	regEvent:function(){
		var ctx = this;
		this.fp.dbcount=0;
		this.fp.drag(function(dx,dy){
			if(ctx.editable){
				var p = ctx.getPos();
				var tmp={x:dx,y:dy};
				eve("element.drag.beforemove",ctx,tmp);
				eve("element.drag.move",ctx,p);
				p.x=this.ox+tmp.x;
				p.y=this.oy+tmp.y;
				ctx.refresh(p);
			}
		},function(){
			this.begin=new Date().getTime();
			if(ctx.editable){
				this.ox=this.attr("x");
				this.oy=this.attr("y");
			}
		},function(e){
			var _ctx = this;
			var diff =  new Date().getTime()-this.begin;
			if(diff<450){//模拟鼠标
				++this.dbcount;
				if(this.dbcount>=2){//双击
					this.dbcount=0;
					if(ctx.editable){
						var pos = ctx.getPos();
						ctx.ipt.val(ctx.text);
						ctx.refresh(pos);
						ctx.openDialog();
					}
				}else{
					setTimeout(function(){
						_ctx.dbcount=0;
						eve("element.click."+ctx.getMaster().id,ctx,e);
					},350);
				}
			}
		});
		//右键
		$(this.fp.node).on("contextmenu",function(e){
			if(ctx.editable){
				eve("element.contextmenu",ctx,e,ctx);
			}
			e.stopPropagation(); 
			return false;
		});
	},
	setEditable:function(flag){
		this.editable=!!flag;
		if(this.editable){
			this.bg.attr("stroke",this.oldBgColor||"black");
		}
		if(!this.editable){
			this.oldBgColor=this.bg.attr("stroke");
			this.bg.attr("stroke",null);
		}
	},
	toFront:function(){
		this.bg.toFront();
		this.texts.toFront();
		this.fp.toFront();
	},
	toBack:function(){
		/*BUG #45186 网络拓扑图标置于底层后无法再选择该图标 huangping 2017/9/8 start*/
        /*this.bg.toFront();
        this.texts.toFront();
        this.fp.toFront();*/
        this.fp.toBack();
        this.texts.toBack();
        this.bg.toBack();
        /*BUG #45186 网络拓扑图标置于底层后无法再选择该图标 huangping 2017/9/8 start*/
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
			var text = this.paper.text(x,y,"").attr({
				"font-size":this.fontSize,
				"text-anchor":"start",
				"space":"preserve",
				fill:this.fontColor
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
			this.toFront();
		}
	},
	getValue:function(){
		var p = this.getPos();
		p.text = this.text || "";
		p.type = "textarea";
		p.fontSize = this.fontSize;
		p.fontColor = this.fontColor;
		this.bg.attrs=$(this.bg.attrs,{
			width:p.w,height:p.h
		});
		p.bgAttr=this.bg.attrs;
		return p;
	},
	setValue:function(cfg){
		this.refresh(cfg);
		this.fontSize= cfg.fontSize || this.fontSize;
		this.fontColor= cfg.fontColor || this.fontColor;
		this.setText(cfg.text);
		if(cfg.bgAttr){
			this.bg.attr($(cfg.bgAttr,{
				width:cfg.w,height:cfg.h
			}));
		}
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
			/*this.ipter.css({
				left:pos.x-vb.x,
				top:pos.y-vb.y,
				"width":pos.w,
				"height":pos.h,
				"backgroundColor":"white"
			});*/
			this.setText(this.text);
			eve("element.refresh."+this.getMaster().id,this,pos);
		}
	},
	rangeCheck:function(pos){
		var t = this.fontSize*2;
		if(pos.w<=t||pos.h<=t){
			return false;
		}else{
			return true;
		}
	},
	getMaster:function(){
		return this.fp;
	},
	remove:function(){
		eve("element.remove."+this.getMaster().id,this);
		this.fp.remove();
		this.bg.remove();
		this.texts.remove();
	}
};function TopoUtil(args){
	this.paper = args.paper;
	this.holder = args.holder;
	this.$svg = $(this.holder).find(">svg,div");
	this.rawViewBox = this.getViewBox();
};
TopoUtil.prototype = {
	getViewBox:function(){
		var svg = this.$svg.get(0);
		if(Raphael.svg){
			var vb = svg.getAttribute("viewBox");
			var r = {x:0,y:0,w:parseFloat(svg.getAttribute("width")),h:parseFloat(svg.getAttribute("height"))};
			if(vb){
				var vs = vb.split(" ");
				r.x=parseFloat(vs[0]);
				r.y=parseFloat(vs[1]);
				r.w=parseFloat(vs[2]);
				r.h=parseFloat(vs[3]);
			}else{
				r.w=parseFloat(this.$svg.width());
				r.h=parseFloat(this.$svg.height());
				r.x=0;
				r.y=0;
			}
			if(r.w<=0) r.w=$("body").width();
			if(r.h<=0) r.h=$("body").height()-200;
			r.cx=r.x+r.w/2;
			r.cy=r.y+r.h/2;
			return r;
		}else if(Raphael.vml){
			var r = {
				x:parseInt(this.$svg.css("left")),
				y:parseInt(this.$svg.css("top")),
				w:parseInt(this.$svg.css("width")),
				h:parseInt(this.$svg.css("height"))
			};
			r.cx=r.x+r.w/2;
			r.cy=r.y+r.h/2;
			return r;
		}else {
			//var clip = this.$svg.css("CLIP");
		}
	},
	getSvg:function(){
		return this.$svg;
	},
	getRoundRectPath:function(args){
		args=$.extend({x:0,y:0,w:0,r:0,cx:0,cy:0,h:0},args);
		var x = args.x,y=args.y,w=args.w,h=args.h,r=args.r||0,rtl=args.rtl||args.r,rtr=args.rtr||args.r,rbr=args.rbr||args.r,rbl=args.rbl||args.r;
		var npath = [];
		npath.push([
			"M",x+rtl,y
		],[
			"H",x+w-rtr
		],[
			"C",x+w-rtr,y,x+w,y,x+w,y+rtr
		],[
			"V",y+h-rbr
		],[
			"C",x+w,y+h-rbr,x+w,y+h,x+w-rbr,y+h
		],[
			"H",x+rbl
		],[
			"C",x+rbl,y+h,x,y+h,x,y+h-rbl
		],[
			"V",y+rtl
		],[
			"C",x,y+rtl,x,y,x+rtl,y
		],["Z"]);
		return npath;
	},
	//加上比例
	realPos:function(pos){
		var newvb = this.getViewBox(),
		    oldvb = this.rawViewBox;
		var xr=newvb.w/oldvb.w,yr=newvb.h/oldvb.h;
		pos.x=pos.x*xr;
		pos.y=pos.y*yr;
		return pos;
	},
	getEventPosition:function(e){
		var of = this.$svg.offset();
		var newvb = this.getViewBox(),
			oldvb = this.rawViewBox;
		var r = {
			pageX:e.pageX,
			pageY:e.pageY,
			x:(e.pageX-of.left)*(newvb.w/oldvb.w)+newvb.x,
			y:(e.pageY-of.top)*(newvb.h/oldvb.h)+newvb.y
		};
		return r;
	}
};/*
	依赖于 ChoserBox
	--使用类必须要求实现check,getState,setState方法
*/
function Chooser(args){
	this.paper = args.paper;
	this.holder = args.holder;
	this.cb = new ChoserBox(args);
	this.setEditable(false);
	this.els = args.els || {};
	this.chosed = [];
	this.regEvent();
}
Chooser.prototype = {
	regEvent:function(){
		var ctx = this;
		//框选完成
		eve.on("element.choserbox.finished",function(p){
			if(this==ctx.cb){
				ctx.check(p);
				setTimeout(function(){
					ctx.cb.setEditable(false);
				},100);
			}
		});
		//空白处点击，取消选择
		eve.on("svg_mouse_click",function(p,e){
			ctx.uncheck();
		});
		eve.on("element.drag.start",function(){
			for(var i = 0;i<ctx.chosed.length;++i){
				var e = ctx.chosed[i];
				if(e){
					e._choser_old_pos = e.getPos();
				}
			}
		});
		eve.on("element.drag.move",function(pos){
			for(var i = 0;i<ctx.chosed.length;++i){
				var e = ctx.chosed[i];
				if(e){
					var p = e.getPos();
					var op = e._choser_old_pos;
					p.x=pos.x+op.x;
					p.y=pos.y+op.y;
					e.refresh(p);
				}
			}
		});
		//alt键按下-进入框选状态
		eve.on("doc_key_down",function(e){
			if(e.altKey && e.keyCode==67){
				ctx.setEditable(true);
			}
		});
		//监听元素删除
		eve.on("element.remove",function(){
			if(ctx.paper==this.paper){
				for(var i=0;i<ctx.chosed.length;++i){
					if(ctx.chosed[i]==this){
						delete ctx.chosed[i];
						break;
					}
				}
			}
		});
		//监听元素单击-取消选择
		eve.on("element.click",function(e){
			if(this.getMaster){
				var master = this.getMaster();
				if(!master.checked){
					if(e && e.ctrlKey){//如果ctrl键按下就加入选择
						ctx._checkEl(this);
					}else{
						ctx.uncheck();
					}
				}else{
					if(e && e.ctrlKey){//如果ctrl键按下就取消选择
						ctx._unCheckEl(this);
						//从选择列表中删除
						for(var i=0;i<ctx.chosed.length;++i){
							if(ctx.chosed[i]==this){
								delete ctx.chosed[i];
								break;
							}
						}
					}
				}
			}
		});
	},
	setEditable:function(flag){
		this.editable = !!flag;
		this.cb.setEditable(this.editable);
	},
	setEls:function(els){
		this.els = els;
	},
	_unCheckEl:function(el){
		if(el && el.getMaster){
			el.getMaster().checked=false;
			if(el.setState){
				el.setState("unchoosed");
			}else if(el.getMaster){
				el.getMaster().attr("opacity",1);
			}
		}
	},
	uncheck:function(){
		if(this.chosed){
			for(var i = 0;i<this.chosed.length;++i){
				var e = this.chosed[i];
				this._unCheckEl(e);
			}
			this.chosed=[];
		}
	},
	_checkEl:function(el){
		if(el && el.getMaster){
			var master = el.getMaster();
			this.chosed.push(el);
			master.checked=true;
			if(el.setState){
				el.setState("choosed");
			}else{
				master.attr("opacity",0.5);
			}
		}
	},
	check:function(p){
		var ctx = this;
		this.uncheck();
		if(this.els && this.editable){
			$.each(this.els,function(key,el){
				if(el && el.getMaster){
					var master = el.getMaster();
					if(master){
						var b1 = master.getBBox();
						var b2 = ctx.cb.border.getBBox();
						if(Raphael.isBBoxIntersect(b1,b2)){
							ctx._checkEl(el);
						}
					}
				}
			});
		}
	}
};
