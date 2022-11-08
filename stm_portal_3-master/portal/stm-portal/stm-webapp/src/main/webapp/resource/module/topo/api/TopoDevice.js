function TopoDevice(args){
	this.args = $.extend({
		holder:null,
		paper:null,
		attr:{
			x:0,y:0,w:0,h:0,r:0,src:null,text:null
		}
	},args);
	this.paper=args.paper;
	this.attr=args.attr;
	this.init();
}
TopoDevice.prototype={
	init:function(){
		this.d=$.extend({},this.d);
		//内边距
		this.padding=0;
		this.fontSize=12;
		//组
		this.deviceSet = this.paper.set();
		var attr=this.attr;
		//背景
		this.bg = this.paper.rect(attr.x,attr.y,attr.w,attr.h)
		.attr({
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
			"fill-opacity":0,
			r:attr.r||0
		});
		this.deviceSet.push(this.bg,this.img,this.text);
		this.refresh(attr);
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
	refresh:function(pos){
		if(!pos) pos=this.getPos();
		this.bg.attr({
			x:pos.x,y:pos.y,width:pos.w,height:pos.h,r:this.args.attr.r||0
		});
		this.fg.attr({
			x:pos.x,
			y:pos.y,
			width:pos.w,
			height:pos.h
		});
		this.img.attr({
			x:pos.x+this.padding,y:pos.y+this.padding,
			width:pos.w,
			height:pos.h
		});
		this.text.attr({
			x:pos.x+pos.w/2,
			y:this.img.attr("y")+this.img.attr("height")+this.fontSize/2+4+this.padding
		});
	},
	getValue:function(){
		var p = this.getPos();
		p.id=this.d.id||-1;
		var zIndex = this.fg.node.getAttribute("z-index");
		if(zIndex){
			p.zIndex=zIndex;
		}
		return $.extend(this.d,p);
	},
	getMaster:function(){
		return this.fg;
	},
	//添加未监控的红色图标
	addNotMonitoredIcon:function(){
		with(this){
			var p = getPos();
			var icon = paper.image(oc.resource.getUrl("resource/themes/"+Highcharts.theme.currentSkin+"/images/topo/not_monitored.png"),p.cx, p.cy, 15, 15);
			return icon;
		}
	},
	setLifeState:function(type){
		switch (type) {
		case "not_monitored":
			if(!this.icon){
				this.icon = this.addNotMonitoredIcon();
			}
			this.setState("disabled");
			this.icon.show();
			break;
		default:
			if(this.icon){
				this.icon.hide();
			}
			break;
		}
	},
	setState:function(state){
		if(state=="not_monitored"){
			this.setLifeState(state);
		}else{
			var attr= this.args.attr;
			if(attr.src.indexOf("topo/image/change.htm")<0){
				this.img.attr("src",oc.resource.getUrl("topo/image/change.htm?path="+attr.src+"&type="+state));
			}else{
				this.img.attr("src",oc.resource.getUrl(attr.src+"&type="+state));
			}
		}
	},
	setFontColor:function(color){
		this.text.attr("fill",color||"white");
	},
	setFontSize:function(size){
		this.fontSize=size||12;
		this.text.attr("font-size",size||12);
	}
};