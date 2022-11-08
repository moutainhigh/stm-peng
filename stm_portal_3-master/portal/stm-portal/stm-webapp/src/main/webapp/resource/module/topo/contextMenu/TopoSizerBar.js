function TopoSizerBar(args){
	var ctx = this;
	this.args=$.extend({
		holder:$("body"),
		pos:{
			x:0,y:0
		},
		id:new Date().getTime(),
		slave:null,
		slaveHolder:null
	},args);
	ctx.id=this.args.id;
	this.slaveUtil = new TopoUtil({
		holder:this.args.slaveHolder,
		paper:this.args.slave
	});
	this.rawSlaveViewBox = this.slaveUtil.getViewBox();
	//遮罩层
	this.mask=$("<div/>");
	this.mask.css({
		width:this.args.holder.css("width"),
		height:this.args.holder.css("height"),
		position:"absolute",
		top:"0",
		left:"0",
		"z-index":this.args.holder.css("z-index")+1,
		"display":"none",
		"cursor":"move"
	});
	this.mask.appendTo(this.args.holder);
	$.get(oc.resource.getUrl("resource/module/topo/contextMenu/TopoSizerBar.html"),function(html){
		ctx.init(html);
	},"html");
};
TopoSizerBar.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.root.appendTo(this.args.holder);
		this.root.css({
			"position":"absolute",
			"left":this.args.pos.x,
			"top":this.args.pos.y
		});
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.regEvent();
		this.setRatio(100);
	},
	//计算百分比
	_caculate:function(current){
		var ratio = 2-current/137.5;
		return Math.floor(ratio*100);
	},
	show:function(){
		this.root.show();
	},
	hide:function(){
		this.setRatio(100);
		this.root.hide();
	},
	setRatio:function(ratio){
		if(ratio>=0 && ratio<=200){
			this.ratio=ratio;
			var top = 288-ratio*137.5/100;
			this.fields.scaler.css({
				top:top
			});
			this.fields.tip.css({
				top:top-4
			});
			this.fields.tip.text(Math.floor(ratio)+"%");
			eve("sizerbar.refresh",this,this.args.slave,this._getCurrentRatio());
		}
	},
	setValue:function(value){
		if(value){
			this.setRatio(value.ratio||100);
			var nvb = this.slaveUtil.getViewBox();
			var vb = value.viewbox||{};
			this.args.slave.setViewBox(vb.x||0,vb.y||0,vb.w||nvb.w,vb.h||nvb.h);

			if(!value.ratio)
				eve("sizerbar.change."+this.id,this);
		}
	},
	getValue:function(){
		return {
			ratio:this._getCurrentRatio(),
			viewbox:this.slaveUtil.getViewBox()
		};
	},
	_getCurrentRatio:function(){
		if(this.ratio){
			return this.ratio;
		}else{
			return (284-parseInt(this.fields.tip.css("top")))*100/137.5;
		}
	},
	regEvent:function(){
		var ctx = this;
		//拖动块
		this.fields.scaler.draggable({
			onDrag:function(e){
				var d = e.data;
				d.left=44;
				if (d.top < 13){d.top = 13;};
				if (d.top > 288){d.top = 288;};
				ctx.setRatio(ctx._caculate(d.top-13));
			},
			onStopDrag:function(e){
				eve("sizerbar.change."+ctx.id,ctx);
			}
		});
		//放大
		this.fields.zoomIn.on("click",function(){
			ctx.setRatio(ctx._getCurrentRatio()+1);
			eve("sizerbar.change."+ctx.id,ctx);
		});
		//缩小
		this.fields.zoomOut.on("click",function(){
			ctx.setRatio(ctx._getCurrentRatio()-1);
			eve("sizerbar.change."+ctx.id,ctx);
		});
		//向上
		this.fields.up.on("click",function(){
			var vb = ctx.slaveUtil.getViewBox();
			ctx.args.slave.setViewBox(vb.x,vb.y+1,vb.w,vb.h);
			eve("sizerbar.change."+ctx.id,ctx);
		});
		//向左
		this.fields.left.on("click",function(){
			var vb = ctx.slaveUtil.getViewBox();
			ctx.args.slave.setViewBox(vb.x+1,vb.y,vb.w,vb.h);
			eve("sizerbar.change."+ctx.id,ctx);
		});
		//向下
		this.fields.down.on("click",function(){
			var vb = ctx.slaveUtil.getViewBox();
			ctx.args.slave.setViewBox(vb.x,vb.y-1,vb.w,vb.h);
			eve("sizerbar.change."+ctx.id,ctx);
		});
		//向右
		this.fields.right.on("click",function(){
			var vb = ctx.slaveUtil.getViewBox();
			ctx.args.slave.setViewBox(vb.x-1,vb.y,vb.w,vb.h);
			eve("sizerbar.change."+ctx.id,ctx);
		});
		//刷新
		eve.on("sizerbar.refresh",function(paper,value){
			if(paper==ctx.args.slave){
				var vb = ctx.rawSlaveViewBox;
				var tmpVb = ctx.slaveUtil.getViewBox();
				if(value<=50){
					ctx.textHide=true;
//					$(ctx.args.slaveHolder).find("text").fadeOut();
				}
				if(value<=5){
					value=5;
				}else if(ctx.textHide && value>50){
//					$(ctx.args.slaveHolder).find("text").fadeIn();
					ctx.textHide=false;
				}
				var ratio=value/100;
				var pos = {
					w:vb.w/ratio,
					h:vb.h/ratio
				};
				pos.x=tmpVb.cx-pos.w/2;
				pos.y=tmpVb.cy-pos.h/2;
				paper.setViewBox(pos.x,pos.y,pos.w,pos.h);
			}
		});
		//鼠标拖动
		this.fields.dragger.click(function(){
			var tmp = $(this);
			if(ctx.mask.prop("show")){
				ctx.mask.hide();
				ctx.mask.prop("show",false);
				tmp.removeClass("center_hover");
			}else{
				ctx.mask.show();
				ctx.mask.prop("show",true);
				tmp.addClass("center_hover");
			}
		});
		var oldp,canMove=false;
		//监听鼠标移动
		this.mask.on("mousedown",function(e){
			var tmp = $(this);
			if(tmp.prop("show") && e.which==1){
				oldp = ctx.slaveUtil.getEventPosition(e);
				canMove=true;
			}
		});
		this.mask.on("mousemove",function(e){
			var tmp = $(this);
			if(tmp.prop("show") && canMove && oldp){//鼠标左键按下
				var p = ctx.slaveUtil.getEventPosition(e);
				var vb = ctx.slaveUtil.getViewBox();
				ctx.args.slave.setViewBox(vb.x+oldp.x-p.x,vb.y+oldp.y-p.y,vb.w,vb.h);
			}
		});
		this.mask.on("mouseup",function(e){
			canMove=false;
			eve("sizerbar.change."+ctx.id,ctx);
		});
		//监听esc按钮
		eve.on("doc_key_down",function(e){
			if(e.keyCode==27){
				ctx.mask.hide();
				ctx.mask.prop("show",false);
			}
		});
		//重置到最佳比例状态
		eve.on("sizerbar.reset",function(){
			ctx.setRatio(100);
		});
		eve.on("sizerbar.hide",function(){
			ctx.hide();
		});
		eve.on("sizerbar.show",function(){
			ctx.show();
		});
		//监听设置值
		eve.on("topo.sizerbar.setvalue."+ctx.id,function(paper,value){
			if(paper==ctx.args.slave){
				ctx.setValue(value);
			}
		});
	}
};