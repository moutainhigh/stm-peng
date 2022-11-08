function Sizer(args){
	this.paper=args.paper;
	this.holder = args.holder;
	this.node=args.node;
	this.$svg = $(this.holder).find("svg");
	this.mode="both";
	this.editable=true;
	this.centerFixed = false;
	this.angle = 0;
	this.dragSet = this.paper.set();
	this.init();
	this.regEvent();
};
Sizer.prototype={
	regEvent:function(){
		var ctx = this;
		this.dragSet.drag(function(dx,dy){ctx.dragMove(dx,dy,this)},function(){ctx.dragStart(this)},function(){ctx.dragEnd(this)});
		$(this.holder).on("keyup",function(e){
			//alert("ok");
		});
	},
	setEditable:function(flag){
		this.editable=!!flag;
		if(this.editable){
			this.show();
		}else{
			this.hide();
		}
	},
	setValue:function(pos){
		if(pos && pos.w>0 && pos.h>0){
			this.tl.attr({
				x:pos.x-2,y:pos.y-2
			});
			this.tc.attr({
				x:pos.x-2+pos.w/2,y:pos.y-2
			});
			this.tr.attr({
				x:pos.x-2+pos.w,y:pos.y-2
			});
			this.rc.attr({
				x:pos.x-2+pos.w,y:pos.y-2+pos.h/2
			});
			this.br.attr({
				x:pos.x-2+pos.w,y:pos.y-2+pos.h
			});
			this.bc.attr({
				x:pos.x-2+pos.w/2,y:pos.y-2+pos.h
			});
			this.bl.attr({
				x:pos.x-2,y:pos.y-2+pos.h
			});
			this.lc.attr({
				x:pos.x-2,y:pos.y-2+pos.h/2
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
	dragMove:function(dx,dy,re){
		if(this.editable){
			var pos = re.pos;
			switch(re.type){
				case "tl":
					re.attr({
						x:dx+re.ox,
						y:dy+re.oy
					});
					pos.w=pos.x-re.attr("x")+pos.w;
					pos.h=pos.y-re.attr("y")+pos.h;
					pos.x=re.attr("x");
					pos.y=re.attr("y");
				break;
				case "tr":
					re.attr({
						x:dx+re.ox,
						y:dy+re.oy
					});
					pos.w=re.attr("x")-pos.x;
					pos.h=pos.y-re.attr("y")+pos.h;
					pos.x=re.attr("x")-pos.w;
					pos.y=re.attr("y");
				break;
				case "br":
					re.attr({
						x:dx+re.ox,
						y:dy+re.oy
					});
					pos.w=re.attr("x")-pos.x;
					pos.h=re.attr("y")-pos.y;
					pos.x=re.attr("x")-pos.w;
					pos.y=re.attr("y")-pos.h;
				break;
				case "bl":
					re.attr({
						x:dx+re.ox,
						y:dy+re.oy
					});
					pos.w=pos.x-re.attr("x")+pos.w;
					pos.h=re.attr("y")-pos.y;
					pos.x=re.attr("x");
					pos.y=re.attr("y")-pos.h;
				break;
				case "tc":
					re.attr({
						y:dy+re.oy
					});
					pos.h=pos.y-re.attr("y")+pos.h;
					pos.y=re.attr("y");
				break;
				case "rc":
					re.attr({
						x:dx+re.ox
					});
					pos.w=re.attr("x")-pos.x;
					pos.x=re.attr("x")-pos.w;
				break;
				case "bc":
					re.attr({
						y:dy+re.oy
					});
					pos.h=re.attr("y")-pos.y;
					pos.y=re.attr("y")-pos.h;
				break;
				case "lc":
					re.attr({
						x:dx+re.ox
					});
					pos.w=pos.x-re.attr("x")+pos.w;
					pos.x=re.attr("x");
				break;
			};
			pos.cx=pos.x+pos.w/2;
			pos.cy=pos.y+pos.h/2;
			this.refresh(pos);
		}
	},
	dragStart:function(re){
		if(this.editable){
			re.ox=re.attr("x");
			re.oy=re.attr("y");
			re.pos=this.node.getPos();
		}
	},
	dragEnd:function(){
	},
	refresh:function(pos){
		if(this.editable){
			this.setValue(pos);
			this.onFresh(pos);
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
		this.dragSet.show();
		this.rect.show();
	},
	onFresh:function(pos){},
	init:function(){
		var pos = this.node.getPos();
		this.rect = this.paper.rect(pos.x,pos.y,pos.w,pos.h);
		this.tl=this.paper.rect(pos.x-2,pos.y-2,4,4).attr({fill:"white",cursor:"nw-resize"});
		this.tl.type="tl";
		this.tc=this.paper.rect(pos.x-2+pos.w/2,pos.y-2,4,4).attr({fill:"white",cursor:"n-resize"});
		this.tc.type="tc";
		this.tr=this.paper.rect(pos.x-2+pos.w,pos.y-2,4,4).attr({fill:"white",cursor:"ne-resize"});
		this.tr.type="tr";
		this.rc=this.paper.rect(pos.x-2+pos.w,pos.y-2+pos.h/2,4,4).attr({fill:"white",cursor:"e-resize"});
		this.rc.type="rc";
		this.br=this.paper.rect(pos.x-2+pos.w,pos.y-2+pos.h,4,4).attr({fill:"white",cursor:"se-resize"});
		this.br.type="br";
		this.bc=this.paper.rect(pos.x-2+pos.w/2,pos.y-2+pos.h,4,4).attr({fill:"white",cursor:"s-resize"});
		this.bc.type="bc";
		this.bl=this.paper.rect(pos.x-2,pos.y-2+pos.h,4,4).attr({fill:"white",cursor:"sw-resize"});
		this.bl.type="bl";
		this.lc=this.paper.rect(pos.x-2,pos.y-2+pos.h/2,4,4).attr({fill:"white",cursor:"w-resize"});
		this.lc.type="lc";
		this.dragSet.push(this.tl,this.tc,this.tr,this.rc,this.br,this.bc,this.bl,this.lc);
	},
	toFront:function(){
		this.rect.toFront();
		this.dragSet.toFront();
	}
};