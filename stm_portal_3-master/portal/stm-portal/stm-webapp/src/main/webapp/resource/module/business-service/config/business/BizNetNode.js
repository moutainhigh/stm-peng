window.BizNetNode=Bizextend(function(args){
	args=$.extend({
		attr:{
			x:0,y:0,w:50,h:60,src:"img/business/10_dbserver.png"
		}
	},args);
	BizNode.apply(this,arguments);
},BizNode,{
	init:function(){
		var drawer = this.args.drawer;
		this.node = drawer.group();
		with(this){
			this.node.img=node.image("",0,0);
			this.node.txt = node.text("").attr("text-anchor","middle");
			this.node.fg = node.rect(1,1).attr({
				"fill-opacity":0,
				"stroke":"#D0C5C5"
			});
			this.node.draggable();
			regEvent();
		}
	},
	regEvent:function(){
		var ctx = this;
		this.node.on("dragmove",function(){
			ctx.exe("dragmove",arguments);
		});
	},
	getValue:function(){
		var rbox=this.get_rbox();
		var bbox=this.node.img.bbox();
		var data=this.get_data();
		data.attr.x=rbox.x;
		data.attr.y=rbox.y;
		data.attr.w=bbox.w;
		data.attr.h=bbox.h;
		return data;
	},
	setValue:function(info){
		this.set_data(info);
		if(info.attr.src){
			this.node.img.size(info.attr.w,info.attr.h);
			this.node.img.load("module/business-service/config/"+info.attr.src);
		}
		if(info.ip){
			this.set_title(info.ip);
		}
		this.node.txt.move(info.attr.w/2,info.attr.h);
		this.refresh(info.attr);
	},
	set_title:function(title){
		if(title){
			var data=this.get_data(),
				attr=data.attr;
			$(this.node.txt.node).text(title);
			var tbox = this.node.txt.bbox();
			var w = (tbox.w>attr.w?tbox.w:attr.w)+10;
			this.node.fg.size(w,attr.h+tbox.h+20).move((attr.w-w)/2,-10);
		}
	},
	get_bbox:function(){
		return this.node.bbox();
	},
	set_data:function(info){
		this.data=info;
	},
	get_data:function(){
		return this.data;
	},
	refresh:function(attr){
		this.node.img.size(attr.w,attr.h);
		this.node.move(attr.x,attr.y);
	},
	get_rbox:function(){
		return this.node.rbox();
	}
});
