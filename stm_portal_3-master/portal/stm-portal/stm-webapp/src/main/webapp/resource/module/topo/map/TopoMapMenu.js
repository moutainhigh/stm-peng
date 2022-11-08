function TopoMapMenu(args){
	this.args=$.extend({
		onLoad:function(){}
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/map/TopoMapMenu.html"),
		success:function(htl){
			ctx.init(htl);
		},
		dataType:"html",
		type:"get"
	});
};
TopoMapMenu.prototype={
	init:function(htl){
		this.root=$(htl);
		this.menus={},ctx=this;
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.menus[tmp.attr("data-field")]=tmp;
			tmp.menu();
		});
		this.initMenu();
	},
	initMenu:function(){
		this.initNodeMenu();
		this.initLineMenu();
	},
	initNodeMenu:function(){
		var ctx = this;
		//地图节点菜单项
		this.menus.node.menu({
			onClick:function(item){
				var node = ctx.getNode(),
				db = node.data("db")||{};
				switch(item.text){
				case "关联资源实例":
					oc.resource.loadScript("resource/module/topo/contextMenu/RelateResourceInstance.js", function(){
						var lastInstanceId=db.instanceId;
						new RelateResourceInstance({
							id:1,
							instanceId:db?db.instanceId:null,
							onConfirm:function(){
								var row = this.getValue(),
								   _ctx = this;
									if(row){
										oc.util.ajax({
											url:oc.resource.getUrl("topo/map/node/relateResourceInstance.htm"),
											data:{
												nodeid:node.data("id"),
												instanceId:row.instanceId,
												mapid:oc.topo.map.graph.getMapId(),
												nextMapId:oc.topo.map.graph.getMapIdForPoint(node),
												level:oc.topo.map.graph.level
											},
											success:function(result){
												if(result.status==200){
													alert(result.msg);
													var map = {};
													map[row.instanceId]=[node];
													oc.topo.map.graph.nodeData[result.node.id] = result.node;
													oc.topo.map.graph.refreshState([row.instanceId],[],map);
													//刷新健康度概览
													oc.topo.map.graph.refreshHealthyStatus();
													oc.topo.map.flowlist.lastMapId=null;
													node.data("db",result.node);
													if(lastInstanceId){
														oc.topo.map.graph.cancelLinkByNode(node);
													}
													_ctx.root.dialog("close");
												}else{
													alert(result.msg,"warning");
												}
											}
										});
									}
							}
						});
					});
					break;
				case "取消关联":
					oc.util.ajax({
						url:oc.resource.getUrl("topo/map/node/cancelRelateResourceInstance.htm"),
						data:{
							nodeid:node.data("id"),
							mapid:oc.topo.map.graph.getMapId(),
							level:oc.topo.map.graph.level
						},
						success:function(result){
							if(result.status==200){
								alert(result.msg);
								oc.topo.map.graph.setState(node,"none");
								oc.topo.map.graph.nodeData[node.id] = {};
								oc.topo.map.graph.cancelLinkByNode(node);
								node.data("db",null);
							}else{
								alert(result.msg,"warning");
							}
						}
					});
					break;
				case "下级资源管理":
					oc.resource.loadScript("resource/module/topo/map/TopoMapCountryInfo.js", function(){
						new TopoMapCountryInfo({
							onOk:function(){
								var value = this.getValue(),
									_ctx = this;
								$.post(oc.resource.getUrl("topo/map/node/updateAttr.htm"),{
									attr:JSON.stringify({country:value}),
									id:db.id
								},function(result){
									if(result.status==200){
										alert(result.msg);
										_ctx.root.dialog("close");
									}else{
										alert(result.msg,"warning");
									}
								},"json");
							},
							onLoad:function(){
								var _ctx = this;
								$.post(oc.resource.getUrl("topo/map/node/updateAttr.htm"),{
									attr:"{}",
									id:db.id
								},function(result){
									if(result.status==200){
										_ctx.setValue(result.result.attrJson.country);
									}else{
										alert(result.msg,"warning");
									}
								},"json");
							}
						});
					});
					break;
				}
			}
		});
	},
	initLineMenu:function(){
		var ctx = this;
		this.menus.line.menu({
			onClick:function(item){
				var lineDom = ctx.getNode(),
					db = lineDom.data("db"),
					line = oc.topo.map.graph.lines[lineDom.attr("id")];
				switch(item.text){
				case "转为链路":
					oc.resource.loadScript("resource/module/topo/contextMenu/TopoMapAddLink.js", function(){
						var fn=line.fromNode,tn=line.toNode,fndb=fn.data("db")||{},tndb=tn.data("db")||{};
						new TopoAddLink({
							from:{
								d:{
									id:fndb.id,
									instanceId:fndb.instanceId,
									ip:fndb.ip
								}
							},to:{
								d:{
									id:tndb.id,
									instanceId:tndb.instanceId,
									ip:tndb.ip
								}
							},
							onOk:function(linkInfo){
								linkInfo.id=db.id;
								oc.util.ajax({
									url:oc.resource.getUrl("topo/map/line/convertToLink.htm"),
									data:{
										linkInfo:JSON.stringify(linkInfo)
									},
									success:function(result){
										if(result.status==200){
											var map = {};
											map[result.instanceId]=[line];
											db.instanceId=result.instanceId;
											lineDom.data("db",db);
											//刷新当前链路的状态
											oc.topo.map.graph.refreshState([],[result.instanceId],map);
											alert(result.msg);
										}else{
											alert(result.msg,"warning");
										}
									}
								});
							}
						});
					});
					break;
				case "删除":
					oc.util.ajax({
						url:oc.resource.getUrl("topo/map/line/remove.htm"),
						data:{
							id:db.id
						},
						success:function(result){
							if(result.status==200){
								alert(result.msg);
								line.remove();
							}else{
								alert(result.msg,"warning");
							}
						}
					});
					break;
				case "编辑链路":
					oc.resource.loadScript("resource/module/topo/contextMenu/EditLink.js",function(){
						new EditLinkDia({
							d:{
								instanceId:db.instanceId
							}
						});
					});
					break;
				case "详细信息":
					//检查是否有权限查看详细信息
					if(db.instanceId){
						oc.resource.loadScript("resource/module/topo/contextMenu/TopoLinkInfo.js",function(){
							new TopoLinkInfo({
								onLoad:function(){
									this.load(db.instanceId,"map");
								}
							});
						});
					}
					break;
				}
			}
		});
	},
	getNode:function(){
		return this.node;
	},
	show:function(menuText,e,node){
		var menu=this.menus[menuText];
		this.node=node;
		var db = this.node.data("db");
		if(menu && this.node){
			switch(menuText){
			case "node":
				var cancelMenu = menu.menu("findItem","取消关联");
				var countryMenu = menu.menu("findItem","下级资源管理");
				$(countryMenu.target).hide();
				if(db && db.instanceId){
					$(cancelMenu.target).show();
					if(oc.topo.map.graph.level==3 || oc.module.topo.map.isCharteredCities(oc.topo.map.graph.mapid)){//四级拓扑
						$(countryMenu.target).show();
					}
				}else{
					$(cancelMenu.target).hide();
				}
				break;
			case "line":
				var convertMenu = menu.menu("findItem","转为链路"),
					detailMenu  = menu.menu("findItem","详细信息"),
					editMenu  = menu.menu("findItem","编辑链路");
				if(db && db.instanceId){
					$(convertMenu.target).hide();
					$(detailMenu.target).show();
					$(editMenu.target).show();
				}else{
					$(convertMenu.target).show();
					$(detailMenu.target).hide();
					$(editMenu.target).hide();
				}
				break;
			}
			menu.menu("show",{
				left:e.pageX,
				top:e.pageY
			});
		}
		return menu;
	}
};