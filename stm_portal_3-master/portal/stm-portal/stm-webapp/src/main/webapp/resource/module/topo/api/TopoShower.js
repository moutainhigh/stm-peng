function TopoShower(args){
	this.args = $.extend({
		onLoad:null,
		data:{
			bgSrc:null,
			links:null,
			nodes:null,
			others:null
		},
		holder:null
	},args);
	var ctx = this;
	if(this.args.holder && this.args.data){
		ctx.init();
	}else{
		throw "holder can't be null";
	}
};
TopoShower.prototype={
	init:function(){
		var data = this.args.data;
		this.els={
			nodes:{},
			links:{},
			others:{}
		};
		this.initPaper();
		//nodes
		this.drawNodes(data.nodes);
		//links
		this.drawLines(data.links);
		//others
		this.drawOthers(data.others);
		if(this.args.onLoad){
			this.args.onLoad.call(this);
		}
	},
	drawNodes:function(nodes){
		if(nodes){
			for(var i=0;i<nodes.length;i++){
				var node = nodes[i];
				try{
					var tmp = new TopoDevice({
						paper:this.paper,
						attr:{
							x:node.x,y:node.y,w:node.iconWidth,h:node.iconHeight,src:node.icon,text:node.ip
						}
					});
					tmp.d=node;
					if(node.attr){
						tmp.d.attr=JSON.parse(node.attr);
						tmp.d.showName=tmp.d.attr.name;
					}
					this.els.nodes[node.id]=tmp;
				}
				catch(e){
                    // console.error(e);
				}
			}
		}
	},
	drawLines:function(lines){
		if(lines){
			for(var i=0;i<lines.length;i++){
				var line = lines[i];
				try{
					var tmp = new TopoLink({
						paper:this.paper,
						from:this.els.nodes[line.from],
						to:this.els.nodes[line.to]
					});
					tmp.d=line;
					this.els.links[line.id]=tmp;
					if(tmp.d.type=="line"){
						tmp.setState("notlink");
					}
				}catch(e){
                    // console.error(e);
				}
			}
		}
	},
	drawOthers:function(others){
		if(others){
			for(var i=0;i<others.length;i++){
				var other = others[i];
				try{
					if(other.attr){
						var attr = JSON.parse(other.attr);
						var tmp = new TopoImg({
							src:attr.src,
							paper:this.paper,
							x:attr.x,y:attr.y,w:attr.w,h:attr.h
						});
						delete other.attr;
						tmp.d=other;
						//如果是地图
						if(attr.dataType=="map"){
							tmp.toBack();
						}
						this.els.others[other.id]=tmp;
					}
				}catch(e){
				}
			}
		}
	},
	getValue:function(){
		var retn = {
			nodes:[],
			links:[],
			others:[]
		};
		//计算节点
		$.each(this.els.nodes,function(key,el){
			retn.nodes.push(el.getValue());
		});
		//计算链路
		$.each(this.els.links,function(key,el){
			retn.links.push(el.getValue());
		});
		//计算节点
		$.each(this.els.others,function(key,el){
			retn.others.push(el.getValue());
		});
		
		return retn;
	},
	initPaper:function(){
		var hd = this.args.holder;
		this.paper = Raphael(hd.get(0),hd.width(),hd.height());
	}
};