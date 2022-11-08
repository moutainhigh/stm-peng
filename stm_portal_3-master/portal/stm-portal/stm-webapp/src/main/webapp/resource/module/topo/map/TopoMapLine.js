function TopoMapLine(args){
	this.args=$.extend({
		from:{x:0,y:0},
		to:{x:0,y:0},
		glowReverse:false,
		angle:Raphael.rad(40)
	},args);
	this.args.style=$.extend({
		fill:"none",
		stroke:"#4cc829",
		"stroke-width":2
	},this.args.style);
	this.init();
};
TopoMapLine.prototype={
	init:function(){
		var fp = this.args.from,
			tp = this.args.to,
			draw = oc.topo.map.draw;
		var cp=this.getControllPoint();
		var line=null;
		if(cp){
			line = draw.path("M"+fp.x+","+fp.y+"Q"+cp.x+","+cp.y+","+tp.x+","+tp.y);
		}else{
			line = draw.path("M"+fp.x+","+fp.y+"L"+tp.x+","+tp.y);
		}
		this.line=line;
		this.line.addClass("topo_map_line");
		this.line.attr(this.args.style);
		draw.add(this.line);
		this.regEvent();
	},
	setState:function(state){
		var color = null;
		if(this.glow){
			this.glow.show();
		}
		if(this.disabledTag){
			this.disabledTag.hide();
		}
		switch (state) {
		case "NORMAL":
			color = "#47C21F";//绿色
			break;
		case "NORMAL_CRITICAL":
			color = "#47C21F";//绿色
			break;
		case "NORMAL_UNKNOWN":
			color = "#47C21F";//绿色
			break;
		case "NORMAL_NOTHING":
			color = "#47C21F";//绿色
			break;
		case "MONITORED":
			color = "#47C21F";//绿色
			break;
		case "NOT_MONITORED":
			color = "blue";
			break;
		case "WARN":
			color = "#F5CC00";// 黄色
			break;
		case "SERIOUS":
			color = "#F96E03";// 橙色
			break;
		case "CRITICAL":
			color = "#71716D";//灰色
			if(this.glow){
				this.glow.hide();
			}
			if(this.disabledTag){
				this.disabledTag.show();
			}else{
				this.addDisable();
			}
			break;
		case "CRITICAL_NOTHING":
			color = "#71716D";//灰色
			if(this.glow){
				this.glow.hide();
			}
			if(this.disabledTag){
				this.disabledTag.show();
			}else{
				this.addDisable();
			}
			break;
		case "UNKOWN":
			color = "#71716D";//灰色
			break;
		case "UNKNOWN_NOTHING":
			color = "#71716D";//灰色
			break;
		case "DELETED":
			this.color="white";
			break;
		case "notlink":
			color = "white";
			break;
		case "normal":
			color = "#47C21F";//绿色
			break;
		case "warning":
			color = "#F5CC00";// 黄色
			break;
		case "disabled":
			color = "#71716D";//灰色
			break;
		case "nodata"://灰色
			color = "#71716D";
			break;
		case "danger"://断开有叉
			break;
		case "severity":
			color = "#F96E03";// 橙色
			break;
		case "hover"://鼠标hover
			color = "blue";
			break;
		case "not_monitored"://未监控
			color = "blue";
			break;
		default://默认白色
			color="#ffffff";
		}
		this.line.style("stroke",color);
		if(this.glow){
			//修改color这个值，如果想要和链路颜色一致就使用color这个变量，如果想固定一种颜色请使用this.glow.style("fill","你的颜色Hex值");
			if(oc.topo.util.module=="GF"){
				this.glow.style("fill","white");
				this.glow.front();
			}else{
				this.glow.style("fill",color);
			}
			
			//如果想要把光晕移动到线的前边把下边的代码的注释打开
			//this.glow.front();
		}
	},
	setData:function(data){
		this.data=data;
		this.line.data("db",data);
	},
	unbind:function(){
		var db = this.line.data("db");
		delete db.instanceId;
		this.line.data("db",db);
		this.setState("notlink");
	},
	getData:function(){
		return this.data||{};
	},
	regEvent:function(){
		var ctx = this;
		this.line.on("mouseover",function(e){
			ctx.line.attr({
				"stroke-width":3
			});
			if(!oc.topo.tips){
				oc.topo.tips=new TopoNewTip();
			}
			var db = ctx.line.data("db");
			if(db && db.instanceId){
				ctx.timeAfter(function(){
					oc.topo.tips.show({
						type:"link",
						x:e.pageX,y:e.pageY,
						value:{
							instanceId:db.instanceId
						}
					});
				},600,"topo_map");
			}else if(oc.topo.util.module=="FTMS"){
				ctx.timeAfter(function(){
					$.post(oc.resource.getUrl("topo/ftms/tip.htm"),{
						id:db.id,
						level:oc.topo.map.graph.level
					},function(result){
						if(result.status==200){
							oc.topo.tips.show({
								type:"ftmsLine",
								x:e.pageX,y:e.pageY,
								value:result.info
							});
						}else{
							alert(result.msg,"warning");
						}
					},"json");
				},600,"topo_map");
			}
		});
		this.line.on("mouseout",function(){
			ctx.line.attr({
				"stroke-width":ctx.args.style["stroke-width"]
			});
			clearTimeout(ctx["topo_map"]);
			if(oc.topo.tips){
				oc.topo.tips.hide();
			}
		});
		this.line.on("click",function(){
			if(oc.topo.util.module=="FTMS" && ctx.data && ctx.data.instanceId){
				oc.resource.loadScript("resource/module/topo/map/TopoMapLinkDetail.js",function(){
					new TopoMapLinkDetail({
						instanceId:ctx.data.instanceId,
						metricId:"ifInOctetsSpeed"
					});
				});
			}
		});
	},
	timeAfter:function(cb, dur, id, context) {
		var ctx = this;
		if (ctx[id]) {
			clearTimeout(ctx[id]);
		}
		ctx[id] = setTimeout(function() {
			cb.apply(context || ctx);
			ctx[id] = null;
		}, dur || 500);
	},
	//增加不可用效果
	addDisable:function(){
		var draw = oc.topo.map.draw;
		this.disabledTag = draw.text("×").font({
			size:16
		}).fill({
			color:"red"
		});
		var l = this.line.length();
		var cp = this.line.pointAt(l/2);
		this.disabledTag.move(cp.x-5,cp.y-9);
	},
	//增加效果
	addFlow:function(){
		var effect = oc.topo.map.effect,
			draw = oc.topo.map.draw,
			ctx = this;
		if(effect && draw){
			var p = this.args.from;
			this.glow = draw.use(effect.lineEffect).translate(p.x,p.y).style({
				fill:this.args.style.stroke
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
	},
	//求控制点
	getControllPoint:function(){
		var p1 = this.args.from,
			p2 = this.args.to,
			k0 = this.args.angle;
		//直线的斜率
		if(Math.abs(p1.x-p2.x)<10){
			return null;
		}
		var k=(p1.y-p2.y)/(p1.x-p2.x);
		if(!k||k>20){
			k=20;
		}
		k1=k-k0,
		k2=k+k0;
		var x=(p2.y-p1.y+k1*p1.x-k2*p2.x)/(k1-k2);
		var y=k2*(x-p2.x)+p2.y;
		return {x:x,y:y};
	},
	remove:function(){
		this.line.remove();
		if(this.glow){
			this.glow.remove();
		}
		if(this.disabledTag){
			this.disabledTag.remove();
		}
	}
};