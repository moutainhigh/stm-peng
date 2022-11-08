function TopoMapView(args){
	this.args = $.extend({
		parent:$("#topo_holder").parent(),
		onLoad:function(){}
	},args);
	var parent = this.args.parent;
	this.map = parent.find(".topo_map");
	
	if(!this.map || this.map.length==0){
		this.map=$("<div/>");
		this.map.addClass("topo_map");
		$("#topo_holder").after(this.map);
	}
	this.map.css({
		"position":"absolute",
		"top":"0","left":"0","right":"0","bottom":"-30px"
	});
	this.map.show();
	var ctx = this;
	var begin = new Date().getTime();
	oc.topo.mapalready = false;
	this.map.load(oc.resource.getUrl("resource/module/map/map.html"),function(){
		oc.resource.loadScripts(["resource/module/topo/map/TopoMapLine.js",
 		"resource/module/topo/map/TopoMapFlow.js","resource/module/topo/map/TopoMapGraph.js",
 		"resource/module/topo/map/TopoMapEffect.js","resource/module/topo/map/TopoMapMenu.js",
 		"resource/module/topo/map/TopoMapFlowList.js","resource/module/topo/map/TopoMapCountryList.js",
 		"resource/module/topo/map/TopoMapLevelInfo.js","resource/module/topo/util/TopoMapUtil.js"], function(){
			eve("topo.toolbar.hide");//隐藏工具栏
			eve("topo.navmenu.hide");//隐藏导航栏
			var timeId = setInterval(function(){
				// console.log("加载地图用时"+(new Date().getTime()-begin)+"ms");
				if(oc.topo.mapalready){
					clearInterval(timeId);
					if(args && args.areakey){
						//直接访问省份
						oc.module.topo.map.loadMapData(args.areakey,1);
						ctx.init(args.areakey);
					}else{
						ctx.init();
					}
				}
			},500);
		});
	});
};
TopoMapView.prototype={
	init:function(key){
		oc.topo.mapCalled = false;
		var holder = $("#court-map>svg").get(0);
		if(holder){
			var draw=SVG(holder),draw1=draw.first();
			var lineDraw=draw1.group();
			lineDraw.backward();
			oc.topo.map={
				flowFlag:false,
				draw:lineDraw,
				draw0:draw,
				draw1:draw1.get(0),
				draw2:draw1.get(1),
				draw3:draw1.get(2),
				imgDraw:draw1.last(),
				holder:holder,
				menu:new TopoMapMenu(),
				effect:new TopoMapEffect(draw),
				util:new TopoMapUtil()
			};
			oc.topo.map.graph=new TopoMapGraph();
			oc.topo.map.flowlist=new TopoMapFlowList({
				holder:this.map,
				key:key
			});
			oc.topo.map.paperPath=Raphael(holder,1,1).path("M0,0L1,1");
			oc.topo.map.countryList = new TopoMapCountryList({
				holder:this.map,
				onLoad:function(){
					this.hide();
				}
			});
			oc.topo.map.levelInfo=new TopoMapLevelInfo({
				holder:this.map,
				onLoad:function(){
					
				},
				key:key
			});
			this.graph=oc.topo.map.graph;
			this.regEvent();
			if(!key){
				this.graph.toMap(1,1);
			}
		}
	},
	regEvent:function(){
		var colors=["#F96E03","#47C21F","#F10000","#F5CC00","#71716D"];
		var ctx = this;
		//切换图
		eve.on("topo.map.dbclick",function(id,level,isMinusOne){
			var val = id+"_"+level;
			if(val != oc.topo.mapCalled) {
				oc.topo.mapCalled = val;	//已经被访问过
				
				var map = oc.topo.map;
				var tmp=null;
				if(!isMinusOne){
					level+=1;
				}
				if(level==1){
					tmp=map.draw1;
				}else if(level==2){
					tmp=map.draw2;
				}else if(level==3){
					tmp=map.draw3;
				}else {
					tmp=map.draw1;
				}
				var ts=tmp.transform();
				map.scale={
						x:ts.scaleX,
						y:ts.scaleY
				};
				map.move={
						x:ts.x,
						y:ts.y
				};
//				ctx.graph.remove();
				if(oc.topo.util.module!="FTMS"){
					ctx.graph.toMap(id,level);
				}
				//清空连线-解决不同地图间连线问题
				ctx.fromNode=null;
			}
		});
		//地图单击
		this.map.on("click",".topoMapPathClass",function(e){
			e.stopPropagation();
			if(ctx.graph.level==3 || oc.module.topo.map.isCharteredCities(oc.topo.map.graph.mapid)){
				oc.topo.map.flowlist.hide();
				oc.topo.map.countryList.show();
				var tmp = $(this);
				//请求县级拓扑信息
				$.post(oc.resource.getUrl("topo/map/node/getCountryList.htm"),{
					key:tmp.attr("key"),
					level:ctx.graph.level
				},function(result){
					oc.topo.map.countryList.setValue({
						title:tmp.parent().find("text").text(),
						nodes:result.info
					});
				},"json");
			}else{
				oc.topo.map.flowlist.show();
				oc.topo.map.countryList.hide();
				var mapid=$(this).attr("key");
				if(mapid){
					oc.topo.map.flowlist.toMap(mapid,oc.topo.map.graph.level+1);
				}
			}
		});
		//地图外部单击
		this.map.on("click","svg",function(e){
			oc.topo.map.flowlist.show();
			oc.topo.map.countryList.hide();
			oc.topo.map.flowlist.toMap(oc.topo.map.graph.getMapId(),oc.topo.map.graph.level);
			e.stopPropagation();
		});
		//绘制线条
		this.map.on("click","image",function(e){
			e.stopPropagation();
			var tmp = $(this);
			var id = tmp.data("id");
			if(id){
				if(!ctx.fromNode){
					ctx.fromNode=tmp;
				}else{
					if(id!=ctx.fromNode.data("id")){
						$.post(oc.resource.getUrl("topo/map/line/addLine.htm"),{
							fromId:ctx.fromNode.data("id"),
							toId:id,
							mapid:ctx.graph.mapid||0
						},function(result){
							if(result.status==200){
								var line = ctx.graph.addLine({
									from:ctx.fromNode,
									to:tmp,
									style:{
										"stroke":colors[Math.floor(Math.random()*6)],
										"stroke-width":2
									}
								});
								line.setData(result.line);
								alert(result.msg);
							}else{
								alert(result.msg,"warning");
							}
							ctx.fromNode=null;
						},"json");
					}
				}
			}
		});
		//节点双击打开资源详情
		this.map.on("dblclick","image",function(){
			var db = $(this).data("db");
			//清除单击连线的副作用
			ctx.fromNode=null;
			//检查权限
			if(db&&db.instanceId){
				oc.util.ajax({
					url:oc.resource.getUrl("topo/resource/checkauth.htm"),
					data:{
						instanceId:db.instanceId
					},
					success:function(result){
						if(result.state==200){
							oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js", function(){
								oc.module.resmanagement.resdeatilinfonew.open({
									instanceId:db.instanceId
								});
							});
						}else{
							alert(result.msg,"warning");
						}
					}
				});
			}else{
				alert("此节点未关联资源实例，请右键关联","warning");
			}
		});
		//节点右键触发菜单
		this.map.on("contextmenu","image",function(e){
			//隐藏悬浮提示框
			ctx.hideTooltip();
			oc.topo.map.menu.show("node",e,$(this));
			e.stopPropagation();
			e.preventDefault();
		});
		//连线右键触发菜单
		this.map.on("contextmenu",".topo_map_line",function(e){
			oc.topo.map.menu.show("line",e,$(this));
			e.stopPropagation();
			e.preventDefault();
		});
		//整个地图右键禁用
		this.map.on("contextmenu",function(e){
			e.stopPropagation();
			e.preventDefault();
		});
		//节点鼠标悬浮提示
		this.map.on("mouseover","image",function(e){
			if(!oc.topo.tips){
				oc.topo.tips=new TopoNewTip();
			}
			var node = $(this),
				db = node.data("db");
			ctx.timeAfter(function(){
				if(db && db.instanceId){
					$.post(oc.resource.getUrl("topo/resource/nodeTooltip.htm"),{
						instanceId:db.instanceId
					},function(info){
						info.instanceId=db.instanceId;
						oc.topo.tips.show({
							type:"node",
							x:e.pageX,y:e.pageY,
							value:info
						});
					},"json");
				}else{
					alert("未关联资源","warning");
				}
			},600,"topo_map_node");
		});
		//节点鼠标移开
		this.map.on("mouseout","image",function(e){
			ctx.hideTooltip();
		});
	},
	hideTooltip:function(){
		clearTimeout(this["topo_map_node"]);
		if(oc.topo.tips){
			oc.topo.tips.hide();
		}
	},
	timeAfter:function(cb, dur, id, context) {
		var ctx = this;
		if (!ctx[id]) {
			ctx[id] = setTimeout(function() {
				cb.apply(context || ctx);
				ctx[id] = null;
			}, dur || 500);
		} else {
			clearTimeout(ctx[id]);
			ctx[id] = null;
		}
	},
	show:function(){
		this.map.removeClass("hide");
		$("#topo_holder").addClass("hide");
		if(oc.topo.topo&&oc.topo.topo.topoSizerBar){
			oc.topo.topo.topoSizerBar.hide();
		}
		eve("topo.toolbar.hide");
		eve("topo.navmenu.hide");
		$(".topo_frame_bottom").hide();
	},
	hide:function(){
		this.map.addClass("hide");
		$("#topo_holder").removeClass("hide");
		if(oc.topo.topo.topoSizerBar){
			oc.topo.topo.topoSizerBar.show();
		}
		eve("topo.toolbar.show");
		eve("topo.navmenu.show");
		$(".topo_frame_bottom").show();
	}
};