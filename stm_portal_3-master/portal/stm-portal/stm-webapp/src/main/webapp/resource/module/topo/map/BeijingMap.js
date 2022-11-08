function BeijingMap(args){
	this.args=$.extend({
	},args);
	with(this){
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/map/BeijingMap.html"),
			type:"get",
			dataType:"text",
			success:function(htl){
				init(htl);
			}
		});
	}
};
BeijingMap.prototype={
	init:function(htl){
		this.root=$(htl);
		this.regEvent();
	},
	zoom:function(){
		if(!this.zoomFlag){
			var box = oc.topo.map.draw0.viewbox();
			box.x-=box.width/2;
			box.y-=box.height/2;
			box.width+=box.width;
			box.height+=box.height;
			oc.topo.map.draw0.viewbox(box);
			this.zoomFlag=true;
		}
	},
	regEvent:function(){
		var ctx = this;
		eve.on("topo.map.dbclick",function(id,level,isMinusOne){
			var draw = null;
			if(level<1 || id==110000){
				if(oc.topo.map.graph){
					oc.topo.map.graph.toMap(id,level+1);
				}
				return;
			}
			switch(level){
			case 1:
				draw=oc.topo.map.draw2;
				break;
			case 2:
				draw=oc.topo.map.draw3;
				break;
			}
			$("#back-china").css({
				bottom:"16px",left:"1078px"
			});
			var tid=setInterval(function(){
				try{
					if(draw.children().length>0){
						clearInterval(tid);
						ctx.draw(draw,level);
						oc.topo.map.graph.toMap(id,level+1);
					}
				}catch(e){
                    // console.log(e);
					clearInterval(tid);
				}
			},200);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(tid);
			}else{
				tasks = new Array();
				tasks.push(tid);
				oc.index.indexLayout.data("tasks", tasks);
			}
		});
	},
	draw:function(draw,level){
		if(this.map){
			this.map.remove();
			this.star.remove();
		}
		var p2=this.findBoundBox(draw);
		var g=draw.group();
		this.root.find(".topoMapPathClass").each(function(idx,pth){
			g.path($(pth).attr("d"));
		});
		var p1=g.bbox();
		var cx=p2.cx-p1.cx,cy=p2.cy-p1.cy;
		g.transform({x:cx,y:cy},true);
		var gbox=g.tbox();
		this.map=g;
		var sca = 150/gbox.w;
		g.transform({scale:sca},true);
		g.transform({x:85,y:-30},true);
		//五角星
		var imgDraw=oc.topo.map.imgDraw;
		var w=imgDraw.get(0).attr("width");
		this.star=imgDraw.image("/resource/module/map/img/gaofa_red.png",w,w).attr("data-id",p2.cx+"_"+p2.cy);
		var rect=imgDraw.rect(1,1),starScale=rect.tbox().w;
		this.star.move(p2.cx+536/starScale,p2.cy-170/starScale);
		rect.remove();
	},
	findBoundBox:function(draw){
		var rbox={
			x1:100000000,y1:100000000,x2:-100000000,y2:-100000000
		};
		draw.each(function(i, children) {
			if(this.get(0).attr("d")=="") return;
			var box = this.bbox();
			if(box.x<rbox.x1){
				rbox.x1=box.x;
			}
			if(box.y<rbox.y1){
				rbox.y1=box.y;
			}
			if(box.y2>rbox.y2){
				rbox.y2=box.y2;
			}
			if(box.x2>rbox.x2){
				rbox.x2=box.x2;
			}
		});
		rbox.w=rbox.x2-rbox.x1;
		rbox.h=rbox.y2-rbox.y1;
		rbox.cx=rbox.x1+rbox.w/2;
		rbox.cy=rbox.y1+rbox.h/2;
		return rbox;
	},
	appendTo:function(draw){
	},
	adjust:function(){
		
	}
};