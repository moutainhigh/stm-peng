function TopoAttr(args){
	this.args = $.extend({
		topoShower:null,
		topoId:0,
		onReady:function(){}
	},args),
	ctx = this;
	if(this.args.topoShower && this.args.topoShower){
		this.state=new TopoState({
			topoShower:this.args.topoShower
		});
		this.init();
	}else{
		throw "bad arguments";
	}
};
TopoAttr.prototype={
	init:function(){
		var ctx = this;
		//加载拓扑的配置
		$.post(oc.resource.getUrl("topo/setting/get/globalSetting.htm"),function(cfg){
			ctx.setCfg($.extend({
				"effect" : {
					"flow" : false
				},
				"link" : {
					"multiNo" : "",
					"singleSettingTag" : true,
					"colorWarning" : "totalFlow",
					"hasTag" : false,
					"showDownDirection" : false,
					"showType" : "single"
				},
				"topo" : {
					"fontColor" : "rgb(255, 255, 255)",
					"freshType" : "auto",
					"refreshTime":300000,
					"fontSize" : 10,
					"fontsizeReserve" : false,
					"fontColorReserve" : false,
					"showRes" : [ "net", "server" ]
				},
				"device" : {
					"colorWarning" : "device",
					"tagField" : "ip"
				}
			},cfg));
		},"json");
		
	},
	setCfg:function(cfg){
		var ctx = this;
		this.state.setCfg({
			auto:cfg.topo.freshType=="auto",
			interval:cfg.topo.refreshTime,
			linkMetricId:cfg.link.colorWarning,
			nodeMetricId:cfg.device.colorWarning
		});
		//拓扑属性
		var topId = this.args.topoId || 0;
		var subTopoId = (topId == 10)?0:topId;	//特殊处理id=10，大屏二层拓扑不知道为什么传递的就是10(有点无奈……)
		$.post(oc.resource.getUrl("topo/subtopo/getAttr.htm"),{id:subTopoId},function(attr){
			//更新背景
			ctx.args.topoShower.args.holder.css({
				"background-image":"url("+(attr.bgsrc||"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/bg/bg_default.png")+")",
				"background-size":"100% 100%"
			});
			ctx.args.onReady.call(ctx);
		},"json");
		this.cfg = cfg;
		this.refreshAttr();
	},
	//刷新拓扑属性
	refreshAttr:function(){
		var cfg = this.cfg;
		//拓扑其他属性
		for(var key in this.args.topoShower.els.nodes){
			var nd = this.args.topoShower.els.nodes[key];
			//字体颜色，字体大小
			nd.setFontColor(cfg.topo.fontColor);
			nd.setFontSize(cfg.topo.fontSize);
			//文字内容
			var txt = nd.d[cfg.device.tagField];
			if(txt){
				nd.setTitle(txt);
			}
			nd.refresh();
		}
	},
	getRange:function(){
		var range = {x1:10000,y1:10000,x2:-1000,y2:-1000,width:0,height:0};
		//计算节点的边界
		this._calcRange(range, this.args.topoShower.els.nodes);
		//计算其他节点的边界
		this._calcRange(range, this.args.topoShower.els.others);
		return {
			x:range.x1-50,y:range.y1-50,w:(range.x2-range.x1+range.width+100),h:(range.y2-range.y1+range.height+100)
		};
	},
	_calcRange:function(range,eles){
		for(var key in eles){
			var nd = eles[key];
			var p = nd.getPos();
			if(p.x<range.x1){
				range.x1=p.x;
			}
			if(p.y<range.y1){
				range.y1=p.y;
			}
			if((p.x+p.w)>range.x2){
				range.x2=p.x;
				range.width=p.w;
			}
			if((p.y+p.h)>range.y2){
				range.y2=p.y;
				range.height=p.h;
			}
		}
	}
};