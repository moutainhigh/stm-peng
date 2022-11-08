var Dashbord=function(args){
	this.args=$.extend({
		state:"nodata"
	},args);
	this.setState(this.args.state);
	this.paper=args.paper;
	this.pos=args.pos;
	this.value=0;
	this.timeId=null;
	this.init(this.pos);
};
Dashbord.prototype={
	_setValue:function(v){
		var a=Math.PI*v;
		var pos = this.pos;
		this.proc.attr("path",[
			["M",pos.x-pos.r,pos.y],
			["A", pos.r, pos.r, 0, +false, 1, pos.x-pos.r*Math.cos(a),pos.y-pos.r*Math.sin(a)],
			["L",pos.x-(pos.r+pos.rr)*Math.cos(a),pos.y-(pos.r+pos.rr)*Math.sin(a)],
			["A", pos.r+pos.rr, pos.r+pos.rr, 0, +false, 0, pos.x-pos.r-pos.rr,pos.y],
			["L",pos.x-pos.r,pos.y],
			["Z"]
		]);
		this.text.attr("text",Math.floor(v*100)+"%");
		this.proc.attr("fill",this.color);
		//this.text.attr("fill",this.color);
		this.center.attr("fill",this.color);
	},
	changeTheme:function(theme){
		var colorSector="#4F5955",colorCircle="#1A2020";
		switch(theme){
		case "blue":
			colorSector="#5589c4";
			colorCircle="#D7D7D7";
			break;
		case "default":
			colorSector="#054B9A";
			colorCircle="#D7D7D7";
			break;
		}
		this.circle.attr("fill",colorCircle);
		this.sector.attr("fill",colorSector);
	},
	setState:function(state){
		state = state || "nodata";
		switch(state){
		case "NORMAL":
			this.color="#47c21f";
			break;
		case "NORMAL_CRITICA":
			this.color="#47c21f";
			break;
		case "NORMAL_UNKNOWN":
			this.color="#47c21f";
			break;
		case "NORMAL_NOTHING":
			this.color="#47c21f";
			break;
		case "MONITORED":
			this.color="#47c21f";
			break;
		case "NOT_MONITORED":
			this.color="white";
			break;
		case "WARN":
			this.color="yellow";
			break;
		case "SERIOUS":
			this.color="orange";
			break;
		case "CRITICAL":
			this.color="red";
			break;
		case "CRITICAL_NOTHING":
			this.color="red";
			break;
		case "UNKOWN":
			this.color="white";
			break;
		case "UNKNOWN_NOTHING":
			this.color="white";
			break;
		case "DELETED":
			this.color="white";
			break;
		}
	},
	setValue:function(v){
		if(isNaN(v)){
			this._setValue(0);
			this.text.attr("text","N/A");
			return ;
		}
		if(v>1){
			v=1;
		}
		if(v<0){
			v=0;
		}
		var ctx = this;
		var lessFlag = this.value>v;
		if(!this.timeId){
			this.timeId = setInterval(function(){
				if(lessFlag){
					ctx.value-=0.01;
					if(v>=ctx.value){
						clearInterval(ctx.timeId);
						ctx.timeId=null;
						ctx.value=v;
					}
				}else{
					ctx.value+=0.01;
					if(v<=ctx.value){
						clearInterval(ctx.timeId);
						ctx.timeId=null;
						ctx.value=v;
					}
				}
				ctx._setValue(ctx.value);
			},10);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(this.timeId);
			}else{
				tasks = new Array();
				tasks.push(this.timeId);
				oc.index.indexLayout.data("tasks", tasks);
			}

		}else{
			ctx._setValue(this.tmpOldVal);
			clearInterval(this.timeId);
			this.timeId=null;
			this.setValue(v);
		}
		this.tmpOldVal=v;
	},
	init:function(pos){
		this.circle = this.paper.path("M0 0L0 0").attr({"fill":"#1A2020","stroke":"#2E3432"});
		this.circle.attr("path",[
			["M",pos.x+pos.r,pos.y],
			["A", pos.r, pos.r, 0, +false, 0, pos.x-pos.r, pos.y],
			["L",pos.x-pos.r-pos.rr,pos.y],
			["A", pos.r+pos.rr, pos.r+pos.rr, 0, +true, 1, pos.x+pos.r+pos.rr, pos.y],
			["L",pos.x+pos.r,pos.y]
		]).attr("stroke","none");
		this.center = this.paper.path("M0 0L0 0").attr({"fill":"#cbcbcb","stroke":null});
		this.center.attr("path",[
			["M",pos.x+pos.cr,pos.y],
			["A",pos.cr,pos.cr,0,+false,0,pos.x-pos.cr,pos.y],
			["Z"]
		]);
		this.sector=this.paper.path("M0 0L0 0").attr({"fill":"#4F5955","stroke":"none"});
		this.sector.attr("path",[
			["M",pos.x+pos.r,pos.y],
			["A",pos.r,pos.r,0,+false,0,pos.x-pos.r,pos.y],
			["Z"]
		]);
		this.text = this.paper.text(pos.x,pos.y-pos.cr-5,"").attr({
			fill:"white",
			"font-size":10
		});
		this.proc = this.paper.path("M0 0L0 0").attr({"fill":"red","stroke":"none"});
		this.center.toFront();
	}
};
