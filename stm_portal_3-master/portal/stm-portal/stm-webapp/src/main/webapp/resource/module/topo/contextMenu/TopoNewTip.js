function TopoNewTip(){
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoNewTip.html"),
		success:function(html){
			ctx.init(html);
		},
		dataType:"html"
	});
};
TopoNewTip.prototype={
	init:function(html){
		this.root = $(html);
		this.tips={};
		this.findMap(this.root,"data-root",this.tips);
		this.tips.frame.appendTo($("body"));
		this.regEvent();
	},
	findMap:function(root,key,cont){
		root.find("["+key+"]").each(function(idx,dom){
			var tmp=$(dom);
			var tmpKey=tmp.attr(key);
			cont[tmpKey]=tmp;
		});
	},
	//设置html属性值,超过20个字符提示
	setDomAttr:function(panel,key,attr,value){
		if(value.length >= 20){
			panel.find("[data-field="+key+"]").each(function(idx,dom){
				var tmp=$(dom);
				tmp.attr(attr,value);
			});
		}
	},
	show:function(args){
		var ctx = this;
		args=$.extend({
			type:null,
			x:null,
			y:null,
			value:{
				instanceId:null
			}
		},args);
		var tpanel = ctx.tips[args.type];
		if(tpanel){
			var panel = tpanel.clone();
			ctx.reset(panel);
			ctx.tips.frame.html("");
			panel.appendTo(ctx.tips.frame);
			switch(args.type){
			case "node":
				args.x-=105;
				args.w=210;
				args.h=150;
				if(args.value.instanceId){
					$.post(oc.resource.getUrl("topo/resource/alarm.htm"),{
						instanceId:args.value.instanceId
					},function(info){
						args.value.alarmInfo=info.alarmInfo;
						ctx.setValue(panel, args.value);
						ctx.showFrame(args);
					},"json");
				}
				break;
			case "link":
				args.x-=210;
				args.w=420;
				args.h=510;
				/*args.y=args.y-40;*/
				if(args.value){
					$.post(oc.resource.getUrl("topo/link/tip.htm"),{instanceId:args.value.instanceId},function(result){
						if(result && result.src && result.des){
							var value={
								alarmInfo:result.alarmInfo,
								stitle:"源端",
								dtitle:"目的端"
							};
							for(var key in result.src){
								value["s"+key]=result.src[key];
							}
							for(var key in result.des){
								value["d"+key]=result.des[key];
							}
							if(value.sshot=="UNCOLLECTIBLE"){
								value.sshot="以下为快照信息";
							}else{
								value.sshot=" ";
							}
							if(value.dshot=="UNCOLLECTIBLE"){
								value.dshot="以下为快照信息";
							}else{
								value.dshot=" ";
							}
							ctx.setValue(panel,value);
							ctx.showFrame(args);
						}else{
							alert(result.msg,"warning");
							throw result.more;
						}
					},"json");
				}
				break;
			case "port":
				args.x-=105;
				args.w=210;
				args.h=390;
				if(args.value){
					$.post(oc.resource.getUrl("topo/backboard/tip.htm"),{instanceId:args.value.instanceId},function(result){
						if(result){
							result.index=args.value.ifIndex;
							ctx.setValue(panel,result);
							ctx.showFrame(args);
						}
					},"json");
				}
				break;
			case "gaofa":
				args.x=1500;
				args.y=1500;
				args.w=460;
				args.h=340;
				var map={};
				this.findMap(panel,"data-field",map);
				map.src.attr("src",args.value.src);
				ctx.showFrame(args);
			break;
			case "cabinetAlarm":
				args.w=230;
				args.h=60;
				args.x=args.x-115;
				args.y=args.y-40;
				ctx.setValue(panel,args.value);
				ctx.showFrame(args);
				break;
			case "ftmsLine":
				args.x-=210;
				args.w=420;
				args.h=126;
				ctx.setValue(panel,args.value);
				ctx.showFrame(args);
				break;
				
			}
		}
	},
	showFrame:function(args){
		var ctx = this;
		var w = args.w;
		var h = args.h;
		var bw = $("body").width();
		var bh = $("body").height();
		if(!bh){
			bh = $('svg').height();
		}
		if(args.x<=0){
			args.x=0;
		}
		if(args.y<=0){
			args.y=0;
		}
		if((args.x+w)>bw){
			args.x=bw-w-10;
		}
		if((args.y+h)>bh){
			args.y=bh-h-20;
		}
		ctx.tips.frame.show();
		ctx.tips.frame.css({
			position:"absolute",
			top:args.y-40,
			left:args.x,
			width:w,
			height:h,
			"border":"none",
			"background-color":"",
			padding:"0"
		});
	},
	hide:function(duration){
		if(this.tips && this.tips.frame){
			this.tips.frame.hide();
		}
	},
	reset:function(panel){
		if(panel){
			var map={};
			this.findMap(panel,"data-field",map);
			for(var key in map){
				map[key].text("");
			}
		}
	},
	setValue:function(panel,val){
		if(panel){
			var map={};
			this.findMap(panel,"data-field",map);
			for(var key in map){
				var tmpValue = val[key]+"";
				if(tmpValue.indexOf(".")==0){
					tmpValue="0"+tmpValue;
				}
				if(tmpValue!=undefined){
					map[key].attr("title",tmpValue);
					map[key].text(tmpValue);
				}else{
					map[key].text("- -");
				}
			}
		}
	},
	regEvent:function(){
		var ctx = this;
		eve.on("oc.topo.newtip.hide",function(){
			ctx.hide();
		});
		ctx.tips.frame.on("mouseout",function(){
			ctx.hide();
		});
		ctx.tips.frame.on("mouseover",function(){
			$(this).show();
		});
		$(document).on("click",function(){
			ctx.hide();
		});
	}
};