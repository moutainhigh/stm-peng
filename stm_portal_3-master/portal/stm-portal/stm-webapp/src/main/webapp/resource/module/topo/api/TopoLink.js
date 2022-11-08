function TopoLink(args){
	this.args=$.extend({
		paper:null,
		glow:{},
		from:null,
		to:null,
		line:{}
	},args);
	
	this.args.glow = $.extend({
		width:3,
		stroke:"blue",
		opacity:0.4
	},this.args.glow);
	
	this.args.line = $.extend({
		"stroke-width":2,
		"stroke":"blue",
		"stroke-dasharray":"",
		"stroke-opacity":1
	},this.args.glow);
	
	if(this.args.paper&&this.args.from&&this.args.to){
		this.paper=this.args.paper;
		this.init();
	}else{
		throw "bad arguments";
	}
}
TopoLink.prototype={
	init:function(){
		this.d=$.extend({},this.d);
		this.line = this.paper.path("M0 0L0 0");
		//this.glowSet=this.line.glow(this.args.glow);
		this.refresh();
		this.line.toBack();
		//this.glowSet.toBack();
	},
	refresh:function(){
		var fp = this.args.from.getPos();
		var tp = this.args.to.getPos();
		
		this.line.attr("path",[
			["M",fp.cx,fp.cy],
			["L",tp.cx,tp.cy]
		]).attr(this.args.line);
		
		/*this.glowSet.attr({
			stroke:this.args.glow.stroke,
			path:this.line.attr("path"),
			width:this.args.glow.width
		});*/
		//如果不可用的红叉存在
		if(this.dangerFlag){
			this.dangerFlag.attr({
				x:(fp.cx+tp.cx)/2,
				y:(fp.cy+tp.cy)/2
			});
		}
	},
	getPos:function(){
		var tp = this.line.attr("path");
		var x1=0,x2=0,y1=0,y2=0,cx=0,cy=0;
		if(tp && tp[0]){
			x1=tp[0][1];
			y1=tp[0][2];
		}
		if(tp && tp[1]){
			x2=tp[1][1];
			y2=tp[1][2];
		}
		cx=(x1+x2)/2;
		cy=(y1+y2)/2;
		return {
			x1:x1,y1:y1,
			x2:x2,y2:y2,
			cx:cx,cy:cy
		};
	},
	//添加不可用时的红色小叉叉
	addDangerFlag:function(){
		with(this){
			var p = getPos();
			var flag = paper.text(p.cx,p.cy,"×");
			flag.attr({
				"fill":"red",
				"font-size":16
			});
			return flag;
		}
	},
	getValue:function(){
		var value = {
			from:this.args.from.d.id,
			to:this.args.to.d.id,
			id:this.d.id||-1
		};
		var zIndex = this.line.node.getAttribute("z-index");
		if(zIndex){
			value.zIndex=zIndex;
		}
		return $.extend(this.d,value);
	},
	setState:function(state){
		var color = "#47C21F";
		//如果是直接切换状态的方式
		if(this.dangerFlag){
			this.dangerFlag.remove();
			this.dangerFlag=null;
		}
		switch(state){
			case "notlink":
				color="white";
			break;
			case "normal" :
				color="#47C21F";
			break;
			case "warning" :
				color="#F5CC00";
			break;
			case "danger":
				color="#71716D";
				this.dangerFlag = this.addDangerFlag();
			break;
			case "nodata":
				color="#71716D";
			break;
			case "disabled":
				color="#71716D";
			break;
			case "severity":
				color="#F96E03";
			break;
			case "hover":
				color="blue";
			break;
			case "not_monitored":
				color="blue";
			break;
		}
		this.line.attr("stroke",color);
		//this.glowSet.attr("stroke",color);
	}
};