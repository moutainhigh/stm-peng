function TopoScale(args){
	this.args = $.extend({
		holder:null
	},args);
	if(this.args.holder){
		this.svg = this.getSvg();
	}else{
		throw "bad arguments";
	}
};
TopoScale.prototype={
	/**
	 * 自适应holder的宽度
	 * range:{x:0,y:0,w:0,h:0}
	 */
	selfAdapt:function(range,notCenter){
		//画布大小
		var svgW=parseFloat(this.svg.attr("width")),svgH=parseFloat(this.svg.attr("height"));
		if(svgW<=0 || svgH<=0) return;
		//首先中心点对齐
		var xx = ((range.x+range.w/2)-svgW/2);
		var yy = ((range.y+range.h/2)-svgH/2);
		//缩放到合适的比例
		if(!notCenter){//是否放到中心点
			this.setViewBox({x:xx,y:yy,w:svgW,h:svgH});
		}
		//如果范围宽度，高度都在容器范围内，不做任何调整
		if(range.w>svgW || range.h>svgH){
			//围绕中心点进行缩放
			//计算比例
			var rw=range.w/svgW,rh=range.h/svgH;
			var tmpW=range.w,tmpH=range.h,tmpXX=xx,tmpYY=yy;
			//if(rw>rh){//使用比例最大得那个
				tmpW=svgW*rw;
			//}else{
				tmpH=svgH*rh;
			//}
			tmpXX=xx+svgW/2-tmpW/2;
			tmpYY=yy+svgH/2-tmpH/2;
			this.setViewBox({x:tmpXX,y:tmpYY,w:tmpW,h:tmpH});
		}
	},
	getSvg:function(){
		return this.args.holder.find("svg");
	},
	//viewBox="0 0 1358 561"
	getViewBox:function(){
		var str = this.svg.get(0).getAttribute("viewBox");
		if(str){
			var tps = str.split(" ");
			return {
				x:parseFloat(tps[0]),
				y:parseFloat(tps[1]),
				w:parseFloat(tps[2]),
				h:parseFloat(tps[3]),
			};
		}
	},
	setViewBox:function(vb){
		this.svg.get(0).setAttribute("viewBox",vb.x+" "+vb.y+" "+vb.w+" "+vb.h);
	},
	fullScreen:function(range){
		if(!this.mask){
			var ctx = this;
			this.mask=$("<div></div>");
			this.exitFsBtn = $("<button class='topo_full_screen_btn' style='color:black;'>退出全屏</button>");
			this.svgHolder = $("<div></div>");
			this.svgHolder.appendTo(this.mask);
			this.exitFsBtn.appendTo(this.svgHolder);
			this.svgHolder.css({
				"width":"100%",
				"height":"100%",
				"position":"relative"
			});
			this.exitFsBtn.on("click",function(){
				ctx.exitFullScreen(range);
			});
		}
		var mask = this.mask;
		mask.show();
		mask.appendTo($("body"));
		var bgimg = this.args.holder.css("background-image");
		mask.css({
			"z-index":9055,
			"position":"absolute",
			"top":0,
			"left":0,
			"right":0,
			"bottom":0,
			"background-color":"black",
			"background-image":"url("+(bgimg=="none"?"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/bg/bg_default.png":bgimg)+")",
			"background-size":"100% 100%"
		});
		this.svgHolder.prepend(this.svg);
		this.svg.attr("width",mask.width());
		this.svg.attr("height",mask.height());
		if(range){
			this.selfAdapt(range);
		}
	},
	exitFullScreen:function(range){
		if(this.mask){
			this.mask.css("z-index",0);
			this.mask.hide();
		}
		this.svg.attr("width",this.args.holder.width());
		this.svg.attr("height",this.args.holder.height());
		this.svg.prependTo(this.args.holder);
		if(range){
			this.selfAdapt(range);
		}
	}
};