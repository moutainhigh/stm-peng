function TopoMapGraph(args){
	this.args=$.extend({
		draw:oc.topo.map.draw.next()
	},args);
	this.lines={};
	this.nodeData={};
	this.linesIds = [];	//记录被画过的线
};
TopoMapGraph.prototype={
	toMap:function(id,level){
		oc.topo.map.util.beginLog("TopoMapGraph.toMap");
		//搜索节点
		var nodes = this.searchNode(),ctx = this;
		this.mapid=id;
		this.level=level;
		//绘制线条
		ctx.remove();
		$.post(oc.resource.getUrl("topo/map/graph/get.htm"),{
			id:this.mapid
		},function(result){
			var map={};
			var linkInstanceIds=ctx.drawLine(nodes,result.data.lines,map);
			var nodeInstanceIds=ctx.drawNodes(nodes,result.data.nodes,map);
			//更新链路节点状态
			ctx.refreshState(nodeInstanceIds,linkInstanceIds,map);
			oc.topo.map.util.endLog("TopoMapGraph.toMap");
		},"json");
		oc.topo.map.flowlist.toMap(id,level);
		this.refreshHealthyStatus();
	},
	//刷新健康度列表
	refreshHealthyStatus:function(){
		//告警概览
		var url=oc.resource.getUrl("topo/map/graph/levelInfo.htm");
		if(oc.topo.util.module=="GF"){
			url=oc.resource.getUrl("topo/gf/levelInfo.htm");
		}
		$.post(url,{
			level:this.level,
			mapid:this.mapid
		},function(result){
			if(result.status==200){
				oc.topo.map.levelInfo.setValue(result);
				if(oc.topo.util.module!="FTMS"){
					oc.topo.map.flowlist.setAlarmListValue({
						monitorCount:result.totalAmount,
						available:result.totalAvailable,
						unavailable:result.totalAmount-result.totalAvailable,
						unavailableNodes:result.unavailableNodes
					});
				}
			}else{
				alert(result.msg,"warning");
			}
		},"json");
	},
	setState:function(obj,state){
		if(obj instanceof TopoMapLine){
			obj.setState(state);
		}else{
			var href = "";
			switch (state.toUpperCase()) {
			case "NORMAL":
				href="module/map/img/gaofa_green.png";
				break;
			case "NORMAL_CRITICAL":
				href="module/map/img/gaofa_green.png";
				break;
			case "NORMAL_UNKNOWN":
				href="module/map/img/gaofa_green.png";
				break;
			case "NORMAL_NOTHING":
				href="module/map/img/gaofa_green.png";
				break;
			case "MONITORED":
				href="module/map/img/gaofa_green.png";
				break;
			case "NOT_MONITORED":
				href="module/map/img/gaofa_gray.png";
				break;
			case "WARN":
				href="module/map/img/gaofa_yellow.png";
				break;
			case "SERIOUS":
				href="module/map/img/gaofa_orange.png";
				break;
			case "CRITICAL":
				href="module/map/img/gaofa_red.png";
				break;
			case "CRITICAL_NOTHING":
				href="module/map/img/gaofa_red.png";
				break;
			case "UNKOWN":
				href="module/map/img/gaofa_gray.png";
				break;
			case "UNKNOWN_NOTHING":
				href="module/map/img/gaofa_gray.png";
				break;
			case "DELETED":
				href="module/map/img/gaofa_white.png";
				break;
			case "NONE":
				href="module/map/img/gaofa_white.png";
				break;
			break;
			}
			obj.attr("href",oc.resource.getUrl("resource/"+href));
		}
	},
	refreshState:function(nodeIds,linkIds,map){
		var ctx = this;
		function _refreshState(linkType,nodeType){
			$.post(oc.resource.getUrl("topo/map/graph/refreshState.htm"), {
				nodeInstIds : nodeIds.join(","),
				linkInstIds : linkIds.join(","),
				linkMetricId : linkType,
				nodeMetricId : nodeType
			}, function(items) {
				if(items){
					for(var i=0;i<items.length;i++){
						var d= items[i];
						var wocaos = map[d.instanceId];
						if(!wocaos) continue;
						for(var j=0;j<wocaos.length;j++){
							var wc = wocaos[j];
							ctx.setState(wc,d.state);
						}
					}
				}
			},"json");
		};
		$.post(oc.resource.getUrl("topo/setting/get/globalSetting.htm"), function(cfg) {
			cfg=$.extend({
				link:{colorWarning:"device"},
				device:{colorWarning:"device"}
			},cfg);
			_refreshState(cfg.link.colorWarning,cfg.device.colorWarning);
		}, "json");
		
	},
	//取消和node关联的链路，把他们重置为连线
	cancelLinkByNode:function(node){
		var lines={},ids=[],db=node.data("db");
		for(var key in this.lines){
			var line = this.lines[key];
			var fn=line.fromNode,tn=line.toNode;
			var fndb=fn.data("db")||{},tndb=tn.data("db")||{};
			if(fndb.id==db.id || tndb.id==db.id){
				var db = line.line.data("db");
				if(db && db.id){
					ids.push(db.id);
					lines[db.id]=line;
				}
			}
		}
		//取消链路绑定
		oc.util.ajax({
			url:oc.resource.getUrl("topo/map/line/unbindLinks.htm"),
			data:{
				ids:ids.join(",")
			},
			success:function(result){
				if(result.status==200){
					for(var i=0;i<result.ids.length;i++){
						var id = result.ids[i];
						if(lines[id]){
							lines[id].unbind();
						}
					}
				}
			}
		});
	},
	searchNode:function(){
		var nodes={},imageDraw=this.args.draw;
		if(imageDraw && imageDraw.each){
			imageDraw.each(function(i,children){
				nodes[this.data("id")]=this;
			});
		}
		return nodes;
	},
	getMapId:function(){
		return this.mapid;
	},
	drawLine:function(nodes,lines,map){
		var instIds = [];
		//删除旧的链路
		var isNew = this._removeOldLines(lines);
		if(isNew){
			for(var i=0;i<lines.length;i++){
				var line = lines[i];
				if(this.linesIds[line.id]){
					continue;
				}
				
				for(var i=0;i<lines.length;i++){
					var line = lines[i];
					var fromNode = nodes[line.fromId],
					toNode	 = nodes[line.toId];
					if(fromNode && toNode){
						var lineDom = this.addLine({
							from:fromNode,
							to:toNode
						});
						if(lineDom){
							lineDom.setData(line);
						}
						if(line.instanceId){
							instIds.push(line.instanceId);
							if(!map[line.instanceId]){
								map[line.instanceId]=[];
							}
							map[line.instanceId].push(lineDom);
						}
					}
				}
			}
		}
		return instIds;
	},
	drawNodes:function(imgNodes,dbNodes,map){
		var instIds = [],updateNodes=[];
		this.nodeData = {};
		//关联nodes的资源示例
		for(var i=0;i<dbNodes.length;i++){
			var node = dbNodes[i],
				imgNode=imgNodes[node.nodeid];
			if(imgNode){
				imgNode.data("db",node);
				this.nodeData[node.id] = node;
				if(node.instanceId){
					instIds.push(node.instanceId);
					if(!map[node.instanceId]){
						map[node.instanceId]=[];
					}
					map[node.instanceId].push(imgNode);
				}
				//更新nextMapId
				if(node.instanceId&&!node.nextMapId){
					var mapid = this.getMapIdForPoint(imgNode);
					updateNodes.push({
						nextMapId:mapid,
						level:this.level,
						id:node.id
					});
					node.nextMapId=mapid;
					node.level=this.level;
					imgNode.data("db",node);
				}
				imgNode.attr("key",node.nextMapId);
			}
		}
		//更新
		if(updateNodes.length>0){
			$.post(oc.resource.getUrl("topo/map/node/updateNextMapIdAndLevel.htm"),{
				nodes:JSON.stringify(updateNodes)
			},function(result){
				if(result.status==200){
					alert("更新成功!");
				}else{
					alert(result.msg,"warning");
				}
			},"json");
		}
		return instIds;
	},
	getMapIdForPoint:function(node){
		var points = oc.module.topo.map.getErrorAreaPonit();
		var nodeid = node.data("id");
		if(nodeid && points[nodeid]){
			return points[nodeid];
		}
		if(!node.attr("key") || node.attr("key") == 'undefined'){
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
	getValue:function(){
		var lines = [];
		for(var i=0;i<this.lines.length;i++){
			var line = this.lines[i];
			lines.push({
				from:line.fromNode.data("id"),
				to:line.toNode.data("id")
			});
		}
		return lines;
	},
	reset:function(){
		this.lines={};
	},
	addLine:function(attr){
		if(attr.from && attr.to){
			var p1 = {
				x:parseFloat(attr.from.attr("x"))+parseFloat(attr.from.attr("width"))/2,
				y:parseFloat(attr.from.attr("y"))+parseFloat(attr.from.attr("height"))/2
			},p2 = {
				x:parseFloat(attr.to.attr("x"))+parseFloat(attr.to.attr("width"))/2,
				y:parseFloat(attr.to.attr("y"))+parseFloat(attr.to.attr("height"))/2
			};
			var line = null;
			if(p1.x<=p2.x){
				line=new TopoMapLine({
					from:this.convertPoint(p1),
					to:this.convertPoint(p2),
					style:attr.style
				});
			}else{
				line=new TopoMapLine({
					from:this.convertPoint(p2),
					to:this.convertPoint(p1),
					style:attr.style,
					glowReverse:true
				});
			}
			line.addFlow();
			line.setState("橙色");
			line.fromNode=attr.from;
			line.toNode=attr.to;
			this.lines[line.line.attr("id")]=line;
			return line;
		}
	},
	//切换地图时清空上次地图的链路
	_removeOldLines:function(lines){
		var isNew = false;
		for(var i=0;i<lines.length;i++){
			var line = lines[i];
			if(!this.linesIds[line.id]){
				isNew = true;
				break;
			}
		}
		if(isNew || lines.length ==0){
			this.remove();	//清空连线
			this.linesIds = [];	//重置记录
		}
		return isNew;
	},
	remove:function(){
		for(var k in this.lines){
			this.lines[k].remove();
		}
		this.reset();
	},
	convertPoint:function(p){
		if(oc.topo.map.scale){
			var scale = oc.topo.map.scale;
			p.x=p.x*scale.x;
			p.y=p.y*scale.y;
		}
		if(oc.topo.map.move){
			var move = oc.topo.map.move;
			p.x=p.x+move.x;
			p.y=p.y+move.y;
		}
		return p;
	}
};