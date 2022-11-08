oc.resource.loadScripts([
    "resource/module/business-service/config/business/BizLink.js",
    "resource/module/business-service/config/business/Bizextend.js"
],function(){
	window.BizPolyLink=Bizextend(function(args){
		BizLink.apply(this,arguments);
	},BizLink,{
		init:function(){
			this.node=this.args.drawer.group();
			this.node.lines=[
			    this.node.line(0,0,1,1),//from 开始线段
			    this.node.line(0,0,1,1),//to 开始线段
			    this.node.line(0,0,1,1),//from 折线
			    this.node.line(0,0,1,1),//to 折线
			    this.node.line(0,0,1,1)//连接线和操作线
			];
		},
		refresh:function(args){
			
		},
		drawFirstLine:function(draw){
			var frbox=this.fromNode.attr("rbox"),
				trbox=this.toNode.attr("rbox");
			var farray=this.node.fromStartLine.array().value,
				tarray=this.node.toStartLine.array().value;
			var rh=frbox.y-trbox.y,rw=frbox.x-trbox.x;
			if(rh>0){
				var fsecondLine=draw.line(farray[1][0],farray[1][1],farray[1][0],farray[1][1]-rh/2).attr("stroke","red");
				var tsecondLine=draw.line(tarray[1][0],tarray[1][1],tarray[1][0],tarray[1][1]+rh/2).attr("stroke","red");
				draw.line(farray[1][0],farray[1][1]-rh/2,tarray[1][0],tarray[1][1]+rh/2).attr("stroke","red");
			}else{
				var fsecondLine=draw.line(farray[1][0],farray[1][1],farray[1][0],farray[1][1]+rh/2).attr("stroke","red");
				var tsecondLine=draw.line(tarray[1][0],tarray[1][1],tarray[1][0],tarray[1][1]-rh/2).attr("stroke","red");
				draw.line(farray[1][0],farray[1][1]+rh/2,tarray[1][0],tarray[1][1]-rh/2).attr("stroke","red");
			}
		},
		//绘制连接点
		drawConnectPoint:function(node,dire,draw){
			var length=8,line=undefined;
			var rbox = node.attr("rbox");
			switch(dire){
			case "l":
				line=draw.line(rbox.x,rbox.y+rbox.h/2,rbox.x-length,rbox.y+rbox.h/2);
				break;
			case "r":
				line=draw.line(rbox.x+rbox.w,rbox.y+rbox.h/2,rbox.x+rbox.w+length,rbox.y+rbox.h/2);
				break;
			case "t":
				line=draw.line(rbox.x+rbox.w/2,rbox.y,rbox.x+rbox.w/2,rbox.y-length);
				break;
			case "b":
				line=draw.line(rbox.x+rbox.w/2,rbox.y+rbox.h,rbox.x+rbox.w/2,rbox.y+rbox.h+length);
				break;
			}
			line.attr("stroke","red");
			return line;
		},
		setValue:function(){}
	});
});