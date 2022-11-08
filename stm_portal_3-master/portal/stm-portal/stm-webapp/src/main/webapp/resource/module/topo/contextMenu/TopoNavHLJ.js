function TopoNav(args){
	this.args=args;
	var ctx = this;
	this.subtopoId=0;
	$.get(oc.resource.getUrl("resource/module/topo/contextMenu/TopoNav.html"),function(html){
		ctx.theme=oc.topo.theme;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/subtopo/isTopoRoomEnabled.htm"),
			success:function(result){
				ctx.topoRoomEnabled=result.enabled;
				ctx.init(html);
			}
		});
	},"html");
};
TopoNav.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.root.appendTo(this.args.parent);
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//结果面板
		this.fields.searchResultPanel.panel("RenderP",{
			closable:true,
			onClose:function(){
				//切换回树面板
				ctx.fields.searchResultPanel.hide();
				ctx.fields.topoAccord.fadeIn();
			}
		});
		//调整关闭按钮位置
		this.fields.searchResultPanel.parent().find(".panel-tool").css({
			height:"24px",
			top:"14px",
			right:"10px"
		});
		if(!this.topoRoomEnabled){
			this.fields.roomView.remove();
		}
		//初始化子拓扑列表
		this.fields.topoAccord.accordion({
			fit:true,
			width:196,
			animate:true
		});
		//初始化菜单
		this.fields.menu.menu();
		//初始化二层网络拓扑列表
		this._treeCustom(this.fields.twoLevel,0);
		//初始化三层网络拓扑列表
		this._treeCustom(this.fields.threeLevel,1);
		//初始化机房视图列表
		if(this.topoRoomEnabled){
			this._treeCustom(this.fields.roomView,2);
		}
		//初始化机房视图列表
		this._treeCustom(this.fields.multiplyView,-1);
		//初始化固定图标，使其可拖动
		this.enableDrag(this.fields.panels.find(".icon"));
		this.regEvent();
		//更新背景主题
		this.root.find("img").each(function(idx,dom){
			var tmp = $(dom);
			var src = tmp.attr("src"),bg = tmp.attr("data-bg");
			if(src){
				tmp.attr("src",src.replace("default",ctx.theme));
			}
			if(bg){
				tmp.attr("data-bg",bg.replace("default",ctx.theme));
			}
		});
		//默认隐藏
		this.shrink();
	},
	//自定义tree的显示方式
	_treeCustom:function(parent,parentTopoId){
		var ctx = this;
		//按钮模板
		var btn=$("<span class='ico' style='background-position-y:-45px;position:absolute'></span>");
		btn.css({
			"background-position-y":"-46px",
			position:"absolute",
			top:"6px",
			right:"4px"
		});
		//初始化tree
		if(parentTopoId>=0){
			parent.tree({
				url: oc.resource.getUrl("topo/"+parentTopoId+"/subTopos.htm"),
				method:"post",
				onClick:function(node){
					ctx.curNode=node;
					var target = $(node.target);
					//检查这次点击事件是否由按钮触发
					var flag = target.attr("data-btn-flag");
					if(!flag){
						eve("topo.loadsubtopo",this,node.id);
					}else{
						ctx.subtopoId=node.id;
						ctx.subtopoName=node.text;
						target.removeAttr("data-btn-flag");
					}
				}
			});
			//鼠标移动到每一项时，显示设置按钮
			parent.on("mouseover",".tree-node",function(){
				var cur = $(this);
				var tmpBtn = cur.find("span.ico");
				if(tmpBtn.length==0){
					cur.css("position","relative");
					var tmpBtn = btn.clone();
					tmpBtn.on("click",function(e){
						cur.attr("data-btn-flag",true);
						cur.trigger("click");
						ctx.showMenu(e);
						e.stopPropagation();
						return false;
					});
					cur.append(tmpBtn);
				}
				tmpBtn.show();
			});
			//鼠标离开隐藏设置按钮
			parent.on("mouseleave",".tree-node",function(){
				var cur = $(this);
				var tmpBtn = cur.find("span.ico");
				if(tmpBtn.length>0){
					tmpBtn.hide();
				}
			});
			//标题栏添加按钮
			var titleDiv = parent.parent().find(".panel-title");
			if(titleDiv){
				titleDiv.css("position","relative");
				var tmpBtn=btn.clone();
				tmpBtn.on("click",function(e){
					ctx.subtopoId=parentTopoId;
					ctx.showMenu(e);
					e.stopPropagation();
					return false;
				});
				titleDiv.append(tmpBtn);
				//标题栏点击事件
				titleDiv.on("click",function(e){
					if(parentTopoId<0){
						if(!oc.topo.mapViewer){
							ctx.loadMultiView();
						}
					}else if(parentTopoId!=2 && parentTopoId>=0){
						if(oc.topo.mapViewer){
							oc.topo.mapViewer.hide();
						}
						eve("topo.loadsubtopo",this,parentTopoId);
					}else{
						if(oc.topo.mapViewer){
							oc.topo.mapViewer.hide();
						}
					}
				});
			}
		}else{
			var titleDiv = parent.parent().find(".panel-title");
			titleDiv.on("click",function(e){
				try{
					ctx.loadMultiView();
				}catch(e){
					console.log(e);
				}
			});
		}
	},
	//加载多级拓扑
	loadMultiView:function(){
		var ctx = this;
		try{
			oc.resource.loadScript("resource/module/topo/heiLongJiang/TopoHLJMain.js", function(){
				if(!oc.topo.mapViewer){
					oc.topo.mapViewer=new TopoHLJMain({
						root:ctx.args.parent
					});
					oc.topo.mapViewer.show();
				}else{
					oc.topo.mapViewer.show();
				}
			});
		}catch(e){
			console.log(e);
		}
	},
	showMenu:function(e){
		var ctx = this;
		var delItem = this.fields.menu.menu("findItem","删除");
		var editItem = this.fields.menu.menu("findItem","编辑");
		var newRoom = this.fields.menu.menu("findItem","新建机房");
		var indexpage=this.fields.menu.menu("findItem","设为拓扑首页");
		var newSubtopoItem = this.fields.menu.menu("findItem","新建子拓扑");
		$(indexpage.target).show();
		if(this.subtopoId==0 || this.subtopoId==1 || this.subtopoId==2){
			$(delItem.target).hide();
			$(editItem.target).hide();
			switch(this.subtopoId){
			case 0:
				$(newSubtopoItem.target).show();
				$(newRoom.target).hide();
				break;
			case 1:
				$(newSubtopoItem.target).hide();
				$(newRoom.target).hide();
				break;
			case 2:
				$(newSubtopoItem.target).hide();
				$(newRoom.target).show();
				$(indexpage.target).hide();
				break;
			}
		}else{
			$(delItem.target).show();
			$(editItem.target).show();
			$(newSubtopoItem.target).show();
			$(newRoom.target).hide();
		}
		//根据拓扑的类型觉得那些菜单项不显示
		$.post(oc.resource.getUrl("topo/subtopo/getTopoType.htm"),{
			topoId:this.subtopoId
		},function(topo){
			if(topo.id==2){
				$(newSubtopoItem.target).hide();
				$(newRoom.target).show();
			}
			ctx.fields.menu.menu('show', {
				left: e.pageX,
				top: e.pageY
			});
		},"json");
	},
	enableDrag:function(dom){
		dom.draggable({
			proxy:function(src){
				var p = $(src).clone();
				p.find("img").attr("width",55);
				p.css({
					"z-index":"100",
					position:"absolute"
				});
				p.appendTo("body");
				return p;
			},
			revert:true
		});
	},
	//展开
	expand:function(){
		var fd = this.fields;
		this.root.animate({
			right:"0px"
		},200,"linear",function(){
			fd.switchTopo.removeClass("hide");
			fd.switchResource.removeClass("hide");
		});
		fd.hook.addClass("topo_hook_off");
		fd.hook.attr("title","关闭面板");
		this.isExpand=true;
	},
	//收缩
	shrink:function(){
		var fd = this.fields;
		this.root.animate({
			right:"-196px"
		},500,"linear",function(){
			fd.switchTopo.addClass("hide");
			fd.switchResource.addClass("hide");
		});
		fd.hook.removeClass("topo_hook_off");
		fd.hook.attr("title","打开面板");
		this.isExpand=false;
	},
	regEvent:function(){
		var ctx = this;
		var tabs = this.fields.tabs.find("[data-panel-key]");
		tabs.on("click",function(){
			var tmp = $(this);
			tabs.css("z-index",0);
			tmp.css("z-index",1);
			var panelKey = tmp.attr("data-panel-key");
			ctx.fields.panels.find("[data-panel]").hide();
			ctx.fields.panels.find("[data-panel='"+panelKey+"']").fadeIn();
			if(panelKey=="resource"){
				if(ctx.currentPanel){
					ctx.currentPanel.fadeIn();
				}
			}
			ctx.expand();
		});
		//默认第一个
		$(tabs.get(0)).trigger("click");
		//资源tab
		var resTabs = this.fields.resTabs.find("[data-panel-key]");
		resTabs.on("click",function(){
			var tmp = $(this);
			resTabs.removeClass("active");
			tmp.addClass("active");
			var panelKey = tmp.attr("data-panel-key");
			ctx.currentPanelKey=panelKey;
			ctx.fields.resPanels.find("[data-panel]").hide();
			var panel = ctx.fields.resPanels.find("[data-panel='"+panelKey+"']");
			ctx.currentPanel=panel;
			if(!ctx[panelKey+"flag"]){
				ctx.refreshPanel();
				ctx[panelKey+"flag"]=true;
			}
			//初始化图标
			panel.fadeIn();
		});
		//默认第一个
		$(resTabs.get(0)).trigger("click");
		//图标添加按钮
		this.fields.iconAddBtn.on("click",function(){
			if(ctx.currentPanelKey){
				var ud = new UploadImgDia({type:ctx.currentPanelKey});
				ud.on("success",function(){
					ctx.refreshPanel();
				});
			}
		});
		//图标删除按钮
		this.fields.iconDelBtn.on("click",function(){
			var removeBtn = $(this);
			var isRemove=removeBtn.attr("removeFlag");
			if(!isRemove || isRemove=="false"){//开启删除模式
				isRemove=false;
				removeBtn.removeClass("ico-del");
				removeBtn.addClass("icon-gabage-open");
				ctx.currentPanel.find(".icon,.bgicon").off().on("click",function(){
					var tmp = $(this);
					var id = tmp.find("img").attr("data-id");
					if(!id) return ;
					tmp.toggleClass("selected");
				});
			}else{
				isRemove=true;
				removeBtn.addClass("ico-del");
				removeBtn.removeClass("icon-gabage-open");
				//克隆选中的节点
				var nodes = ctx.currentPanel.find(".selected").clone();
				nodes.removeClass("bgicon");
				nodes.addClass("icon");
				//如果有选择的图片
				if(nodes.length>0){
					//显示确认对话框
					var dia = $("<div style='height:30px;line-height:30px;overflow-y:auto;height:200px;padding:4px;'>确定要删除下列自定义图标么？<div style='margin-top:5px;'></div></div>");
					dia.find("div").append(nodes);
					dia.find(".icon").css({
						display:"inline-block",
						margin:"2px"
					});
					dia.find(".icon img").css({width:55,height:55});
					dia.dialog({
						width:600,
						heigth:200,
						title:"提示",	
						onClose:function(){
							ctx.currentPanel.find(".icon").removeClass("selected");
						},
						buttons:[{
							text:"确定",handler:function(){
								var ids = [];
								dia.find("img").each(function(idx,node){
									var id = $(node).attr("data-id");
									ids.push(id);
								});
								var idsStr=ids.join(",");
								//开始后台删除
								oc.util.ajax({
									url:oc.resource.getUrl("topo/image/del.htm"),
									data:{ids:idsStr},
									success:function(msg){
										if(msg.code==200){
											//删除界面上的节点
											ctx.currentPanel.find(".icon img,.bgicon img").each(function(idx,node){
												var id = $(node).attr("data-id");
												if(idsStr.indexOf(id)>=0){
													$(node).parent().remove();
												}
											});
										}
										alert(msg.data);
										dia.dialog("close");
									}
								});
							}
						},{
							text:"取消",handler:function(){
								dia.dialog("close");
								//重置选中状态
							}
						}]
					});
				}
			}
			ctx.currentPanel.find(".icon").draggable({ 
				disabled:!isRemove
			});
			removeBtn.attr("removeFlag",!isRemove);
		});
		//按钮
		this.fields.searchBtn.on("click",function(){
			ctx.search();
		});
		//搜索输入IP的框
		this.fields.searchIpt.on("keyup",function(e){
			if(e.keyCode==13){
				ctx.search();
			}
		});
		//菜单项点击
		this.fields.menu.menu({
			onClick:function(item){
				switch(item.text){
				case "编辑":
					oc.resource.loadScripts(["resource/module/topo/contextMenu/CreateSubTopo.js","resource/module/topo/contextMenu/CreateRoom.js"], function(){
						$.post(oc.resource.getUrl("topo/subtopo/getTopoType.htm"),{
							topoId:ctx.subtopoId
						},function(topo){
							if(topo.id==2){//机房视图的编辑
								var cr = new CreateRoom({
									title:"编辑机房",
									value:{
										id:ctx.subtopoId,
										name:ctx.subtopoName
									},
									onOk:function(){
										eve("oc.topo.addSubTopo.finished",ctx,2);
									}
								});
							}else{
								var cs = new CreateSubTopo({
									subTopoId:ctx.subtopoId,
									title:"编辑子拓扑"
								});
								cs.on("save",function(info){
									eve("oc.topo.addSubTopo.finished",ctx,0,ctx.fields.twoLevel);
									eve("topo.loadsubtopo",this,info.id);
								});
							}
						},"json");
						
					});
					break;
				case "新建子拓扑":
					 oc.resource.loadScript("resource/module/topo/contextMenu/CreateSubTopo.js", function(){
						var cs = new CreateSubTopo({
							parentId:ctx.subtopoId
						});
						cs.on("save",function(info){
							eve("oc.topo.addSubTopo.finished",ctx,0,ctx.fields.twoLevel);
							eve("topo.loadsubtopo",this,info.id);
						});
					 });
					break;
				case "设为拓扑首页":
					oc.util.ajax({
						url:oc.resource.getUrl("topo/setting/save.htm"),
						data:{
							key:"oc_topo_graph_homepage",
							value:JSON.stringify({id:ctx.subtopoId})
						},
						success:function(){
							alert("首页设置成功");
						}
					});
					break;
				case "删除":
					$.messager.confirm("警告 正在删除  "+(ctx.subtopoName||""),"确定要删除该拓扑吗？",function(r){
						if(r){
							$.post(oc.resource.getUrl("topo/subtopo/getAttr.htm"),{id:ctx.subtopoId},
							function(info){
								oc.util.ajax({
									url:oc.resource.getUrl("topo/subtopo/deleteRoom.htm"),
									data:{
										id:ctx.subtopoId
									},
									success:function(){
										alert("删除成功");
										eve("topo.loadsubtopo",ctx,0);
										var attr = JSON.parse(info.attr||"{}");
										if(attr.type && attr.type=="room"){
											eve("oc.topo.addSubTopo.finished",ctx,2,ctx.fields.roomView);
										}else{
											eve("oc.topo.addSubTopo.finished",ctx,0,ctx.fields.twoLevel);
										}
									},
									dataType:"text"
								});	
							},"json");
						}
					});
					break;
				case "新建机房":
					oc.resource.loadScript("resource/module/topo/contextMenu/CreateRoom.js", function(){
						new CreateRoom({
							parentId:ctx.subtopoId,
							onOk:function(info){
								eve("oc.topo.addSubTopo.finished",ctx,2,ctx.fields.roomView);
								eve("topo.loadsubtopo",ctx,info.id);
							}
						});
					 });
					break;
				}
			}
		});
		//监听子拓扑添加完成事件
		eve.on("oc.topo.addSubTopo.finished",function(pid){
			switch(pid){
			case 0:
				$.post(oc.resource.getUrl("topo/0/subTopos.htm"),function(topos){
					ctx.fields.twoLevel.tree("loadData",topos);
				},"json");
				break;
			case 1:
				$.post(oc.resource.getUrl("topo/1/subTopos.htm"),function(topos){
					ctx.fields.threeLevel.tree("loadData",topos);
				},"json");
				break;
			case 2:
				$.post(oc.resource.getUrl("topo/2/subTopos.htm"),function(topos){
					ctx.fields.roomView.tree("loadData",topos);
				},"json");
				break;
			default:
				$.post(oc.resource.getUrl("topo/0/subTopos.htm"),function(topos){
					ctx.fields.twoLevel.tree("loadData",topos);
				},"json");
				$.post(oc.resource.getUrl("topo/1/subTopos.htm"),function(topos){
					ctx.fields.threeLevel.tree("loadData",topos);
				},"json");
				$.post(oc.resource.getUrl("topo/2/subTopos.htm"),function(topos){
					ctx.fields.roomView.tree("loadData",topos);
				},"json");
				break;
			}
		});
		//监听背景更换
		this.fields.bgcontainer.on("click",".bgicon",function(){
			if(!ctx.fields.iconDelBtn.hasClass("icon-gabage-open")){
				var item = $(this).find("img");
				var src=item.attr("data-bg")||item.attr("src");
				eve("topo.changebg",this,src);
			}
		});
		//展开收缩
		this.fields.hook.on("click",function(){
			if(ctx.isExpand){
				ctx.shrink();
			}else{
				ctx.expand();
			}
		});
	},
	//刷新一个面板的图标
	refreshPanel:function(){
		var ctx= this;
		var type=this.currentPanelKey;
		var parent = this.currentPanel;
		//获取一个模板
		var template = parent.find(".template");
		//从服务器获取图片
		$.get(oc.resource.getUrl("topo/image/getByType/"+type+".htm"),function(imgs){
			if(imgs){
				//清空以前的自定义id，重新更新
				ctx.currentPanel.find("img[data-id]").parent().remove();
				//绘制当前图标
				for(var i=0;i<imgs.length;++i){
					var item = imgs[i];
					var tmp = template.clone();
					var image = tmp.find("img");
					image.attr({
						src:oc.resource.getUrl("topo/image/change.htm?path="+item.fileId),
						width:item.width,
						height:item.height,
						"data-id":item.id
					});
					tmp.removeClass("hide template");
					template.after(tmp);
					if(type!="bg"){
						ctx.enableDrag(tmp);
					}
				}
			}
		},"json");
	},
	search:function(){
		var ctx = this;
		//获取搜索条件
		var val = ctx.fields.searchIpt.val();
		//请求后台服务器进行搜索
		if(val){
			$.post(oc.resource.getUrl("topo/getSubToposByIp.htm"),{
				ip:val
			},function(info){
				//切换回搜索结果面板
				ctx.fields.searchResultPanel.panel("open");
				ctx.fields.searchResultPanel.show();
				ctx.fields.topoAccord.hide();
				//展示搜索结果
				ctx.fields.searchResultPanel.html("");
				oc.ui.navsublist({
					selector:ctx.fields.searchResultPanel,
					textField:"name",
					valueField:"id",
					data:info,
					click:function(href,item,e){
						eve("topo.loadsubtopo",this,item.id);
					}
				});
			},"json");
		}
	}
};