function TopoHLJMain(args){
	this.args=$.extend({
		root:null
	},args);
	if(this.args.root){
		var ctx = this;
		this.root = div = $("<div>");
		div.css({
			width:"100%",height:"100%","position":"relative"
		});
		this.args.root.append(div);
		div.load(oc.resource.getUrl("resource/module/map/map.html"),function(){
			oc.resource.loadScripts(["resource/module/topo/map/TopoMapGraph.js",
			    "resource/module/topo/heiLongJiang/TopoHLJMenu.js",
			    "resource/module/topo/heiLongJiang/TopoHLJSatisticPanel.js"], function(){
				eve("topo.toolbar.hide");//隐藏工具栏
				eve("topo.navmenu.hide");//隐藏导航栏
				var timeId = setInterval(function(){
					var holder = $("#court-map>svg").get(0);
					if(holder){
						ctx.init(holder);
						clearInterval(timeId);
					}
				},500);
				var tasks = oc.index.indexLayout.data("tasks");
				if(tasks && tasks.length > 0){
					oc.index.indexLayout.data("tasks").push(timeId);
				}else{
					tasks = new Array();
					tasks.push(timeId);
					oc.index.indexLayout.data("tasks", tasks);
				}
			});
		});
	}else{
		throw "root must not be empty";
	}
};
TopoHLJMain.prototype={
	init:function(holder){
		this.holder=holder;
		$(holder).parent().css({
			position:"relative"
		});
		var draw=SVG(holder),draw1=draw.first();
		oc.topo.hlj={
			draw0:draw,
			draw1:draw1.get(0),
			draw2:draw1.get(1),
			draw3:draw1.get(2),
			main:this,
			imgDraw:draw1.get(2).next()
		};
		//到黑龙江
		oc.module.topo.map.loadMapData(230000,1);
		this.history=[{
			key:230000,
			name:"黑龙江省"
		}];
		this.menu=new TopoHLJMenu({});
		this.statisticPanel = new TopoHLJSatisticPanel({
			onLoad:function(){
				$(holder).parent().append(this.root);
			}
		});
		this.paperPath=Raphael(holder,1,1).path("M0,0L1,1");
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this;
		//监听右键
		$(this.holder).on("contextmenu","image",function(e){
			e.preventDefault();
			var tmp = $(this);
			ctx.menu.setNode(tmp);
			ctx.menu.show(e.pageX,e.pageY);
			if(!tmp.attr("key")){
				ctx.findKey(tmp);
			}
		});
		$(this.holder).on("dblclick",".topoMapPathClass",function(e){
			ctx.lastStatisticValue=null;
			ctx.currentPath=$(this);
		});
		$(this.holder).on("click",function(){
			ctx.statisticPanel.setValue(ctx.lastStatisticValue);
		});
		$(this.holder).on("click",".topoMapPathClass",function(e){
			var text=$(this).next();
			if(!ctx.lastStatisticValue){
				ctx.lastStatisticValue=ctx.statisticPanel.lastValue;
			}
			if(text && text.attr("key")){
				ctx.statisticPanel.toMap(text.attr("key"),{name:text.text()},null,ctx.level+1);
			}
			e.stopPropagation();
		});
		$(this.holder).on("dblclick","text",function(e){
			ctx.currentPath=$(this);
		});
		$("#back-china").on("click",function(){
			ctx.history.pop();
			ctx.currentPath=null;
			ctx.lastStatisticValue=null;
		});
		//切换视图
		eve.on("topo.map.dbclick",function(id,level,isMinusOne){
			ctx.level=level;
			id=parseInt(id);
			if(ctx.key==id) return;
			var name="";
			if(ctx.currentPath){
				if(ctx.currentPath.get(0).tagName=="TEXT"){
					name=ctx.currentPath.text();
				}else{
					name=ctx.currentPath.parent().find("text").text();
				}
			}
			if(id!=230000){
				ctx.history.push({
					key:id,
					name:name
				});
			}
			ctx.level=level;
			ctx.key=id;
			ctx.initmap();
		});
	},
	initmap:function(){
		var nodes={},flag=false;
		var ctx = this;
		var timeId = setInterval(function(){
			if(!flag){
				if(flag){
					clearInterval(timeId);
				}
				//获取点对应关系
				oc.topo.hlj.imgDraw.each(function(i,children){
					flag=true;
					nodes[this.data("id")]=this;
					//ctx.setState(this,i%2==0?"red":"green",Math.floor(Math.random()*100));
				});
				ctx.refreshState(nodes);
			}
		},200);
		var tasks = oc.index.indexLayout.data("tasks");
		if(tasks && tasks.length > 0){
			oc.index.indexLayout.data("tasks").push(timeId);
		}else{
			tasks = new Array();
			tasks.push(timeId);
			oc.index.indexLayout.data("tasks", tasks);
		}
		if(this.key==230000){
			$("#back-china").hide();
		}else{
			$("#back-china").show();
		}
	},
	getAreas:function(){
		var retn=[];
		var draw = null;
		switch(this.level){
		case 0:draw=oc.topo.hlj.draw1;break;
		case 1:draw=oc.topo.hlj.draw2;break;
		case 2:draw=oc.topo.hlj.draw3;break;
		}
		if(draw){
			$(draw.node).find("g").each(function(idx,dom){
				var text=$(dom).find("text");
				retn.push({
					name:text.text(),
					key:text.attr("key")+","+text.text()
				});
			});
		}
		return retn;
	},
	getParentKey:function(){
		if(this.history.length>=2){
			return this.history[this.history.length-2].key;
		}else{
			return undefined;
		}
	},
	refreshState:function(nodes){
		//不传nodes就刷新当前状态
		if(!nodes){
			nodes=this.nodes;
		}else{
			this.nodes=nodes;
		}
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/hlj/refreshState.htm"),
			data:{
				key:this.key
			},
			success:function(result){
				if(result.status==200){
					for(var i=0;i<result.items.length;i++){
						var item = result.items[i];
						var node = nodes[item.nodeId];
						node.attr("key",item.nextMapId);
						node.attr("dbid",item.id);
						ctx.setState(node,item.state,item.count);
					}
				}
			}
		});
		this.statisticPanel.toMap(this.key,this.history[this.history.length-1],null,ctx.level);
	},
	findKey:function(imgNode){
		var name = imgNode.data("address");
		var tmpTexts=$(this.holder).find(".level2 text");
		for(var i=0;i<tmpTexts.length;i++){
			var tt = $(tmpTexts[i]);
			if(name.indexOf(tt.text())>=0){
				imgNode.attr("key",tt.attr("key"));
				break;
			}
		}
	},
	setState:function(node,state,count){
		var h=6,w=10;
		var trans = oc.topo.hlj.imgDraw.transform();
		var xr=3.497/trans.scaleX,yr=3.497/trans.scaleY;
		node.attr({
			href:oc.resource.getUrl("topo/hlj/badge.htm?size=36&badge="+count+"&type="+state),
			height:h*yr,width:w*xr
		});
	},
	getMapIdForPoint:function(node){
		var points = oc.module.topo.map.getErrorAreaPonit();
		var nodeid = node.data("id");
		if(nodeid && points[nodeid]){
			return points[nodeid];
		}
		if(!node.attr("key")){
			var draw = oc.topo.map["draw"+this.level];
			var path = oc.topo.map.paperPath;
			if(draw){
				var cont = $(draw.node);
				var x = parseFloat(node.attr("x")),y=parseFloat(node.attr("y"));
				var doms = cont.find(".topoMapPathClass");
				for(var i=0;i<doms.length;i++){
					var dom = doms[i];
					var tmp=$(dom);
					path.attr("path",tmp.attr("d"));
					if(path.isPointInside(x,y)){
						var mapid = tmp.attr("key");
						node.attr("key",mapid);
						return mapid;
					}
				}
			}
		}else{
			return node.attr("key");
		}
	},
	show:function(){
		this.root.removeClass("hide");
		$("#topo_holder").addClass("hide");
		if(oc.topo.topo&&oc.topo.topo.topoSizerBar){
			oc.topo.topo.topoSizerBar.hide();
		}
		eve("topo.toolbar.hide");
		eve("topo.navmenu.hide");
	},
	hide:function(){
		this.root.addClass("hide");
		$("#topo_holder").removeClass("hide");
		if(oc.topo.topo.topoSizerBar){
			oc.topo.topo.topoSizerBar.show();
		}
		eve("topo.toolbar.show");
		eve("topo.navmenu.show");
	}
};