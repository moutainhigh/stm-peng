$(function(){
	var $topo = $("#topo-graph");
	window.topo = window.topo || {};
	window.topo.graph = {
		topo:null,
		//暂存数据
		data:{
			panels:{}
		},
		doms:{
			panels:$topo.find(".draw-box > div"),
			tabBtns:$topo.find(".draw-menu li")
		},
		regEvent:function(){
			var ctx = this;
			//工具栏拖拽
			$topo.find(".icon").draggable({
				proxy:"clone",
				revert:true
			});
			//拖拽图标
			$("#holder").droppable({
				accept:".icon",
				onDrop:function(e,dom){
					var top = $(dom).parent().scrollTop()||0;
					var $dom = $(dom).draggable("proxy");
					var $img = $dom.find("img");
					var offset = $(this).offset();
					var box = ctx.topo.viewBox;
					var pos = {
						x:$dom.offset().left-offset.left+box.x,
						y:$dom.offset().top-offset.top+box.y-top
					};
					if($dom.data("cmd")=="group"){
						ctx.topo.getNode({
							x:pos.x,
							y:pos.y,
							w:200,
							h:200,
							type:"group",
							data:{id:"group"+ctx.topo.getIndex()}
						});
					}else{
						var w=parseInt($img.attr("width"))||55,h=parseInt($img.attr("height"))||55;
						ctx.topo.getNode({
							src:$img.attr("src"),
							x:pos.x,y:pos.y,w:w,h:h,iw:w,ih:h,type:"node",
							data:{id:"node"+ctx.topo.getIndex()}
						});
					}
				}
			});
			//工具栏命令菜单
			$topo.find(".tool").click(function(){
				var $tmp = $(this);
				var flag = !!$tmp.data("flag");
				var cmd = $tmp.attr("data-cmd");
				switch(cmd){
					case "drawline":
						ctx.topo.enableDrawLine(flag);
						break;
					case "drag":
						ctx.topo.enableDrag(flag);
						break;
					case "chose":
						ctx.topo.enableDrawLine(false);
						ctx.topo.enableDrag(false);
						break;
				}
				$tmp.data("flag",!flag);
			});
			//组的 设备管理按钮
			eve.on("topo.group.title.btn",function(){
				//打开对话框
				new DeviceManagerDia(this);
			});
		},
		init:function(){
			this.initUI();
			//拓扑图
			this.topo = new Topo({holder:"#holder",id:null});
			//工具栏
			this.toolbar = new TopoToolbar({
				paper : this.topo.cavas.raphael,
				holder : "#holder",
				topo:this.topo
			});
			this.topo.loadData(oc.resource.getUrl("topo/getSubTopo/.htm"));
			//注册事件
			this.regEvent();
		},
		//刷新一个面板的图标
		refreshPanel:function(parent,type){
			//获取一个模板
			parent.template = parent.find(".template");
			parent.template.img = parent.template.find("img");
			//从服务器获取图片
			oc.util.ajax({
				url:oc.resource.getUrl("topo/image/getByType/"+type+".htm"),
				success:function(imgs){
					if(imgs){
						parent.find(".icon img[id]").parent().remove();
						for(var i=0;i<imgs.length;++i){
							var img = imgs[i];
							parent.template.img.attr({
								src:oc.resource.getUrl("topo/image/down.htm?name="+img.path),
								width:img.width,
								height:img.height,
								id:img.id
							});
							var tmp = parent.template.clone();
							tmp.removeClass("hide template");
							parent.append(tmp);
							tmp.draggable({
								proxy:"clone",
								revert:true
							});
						}
					}
				},
				type:"get"
			});
		},
		//初始化面板
		initPanel:function(id,type){
			var ctx = this;
			var nodeIcon = $("#"+id);
			//工具按钮
			var tool = nodeIcon.parent().find(".panel-tool");
			var addBtn = $("<a class='fa fa-plus'></a>"),removeBtn = $("<a class='icon-del'></a>");
			tool.prepend(addBtn);
			tool.prepend(removeBtn);
			//绑定事件
			addBtn.click(function(){
				var ud=new UploadImgDia({type:type});
				ud.on("success",function(){
					ctx.refreshPanel(nodeIcon, type);
				});
			});
			//删除按钮
			removeBtn.click(function(){
				var isRemove=removeBtn.attr("removeFlag");
				if(!isRemove || isRemove=="false"){
					isRemove=false;
					removeBtn.removeClass("icon-del");
					removeBtn.addClass("icon-gabage-open");
					nodeIcon.find(".icon").off().on("click",function(){
						var tmp = $(this);
						var id = tmp.find("img").attr("id");
						if(!id) return ;
						tmp.toggleClass("selected");
					});
				}else{
					isRemove=true;
					removeBtn.addClass("icon-del");
					removeBtn.removeClass("icon-gabage-open");
					//克隆选中的节点
					var nodes = nodeIcon.find(".selected").clone();
					//如果有选择的图片
					if(nodes.length>0){
						//设置预览的样式
						nodes.css({
							"display":"inline-block",
							padding:"2px",margin:"2px"
						});
						nodes.find("img").css({
							height:"55px",width:"55px"
						});
						//显示确认对话框
						var dia = $("<div style='height:30px;line-height:30px;overflow-y:auto;height:200px;padding:4px;'>确定要删除自定义图标么？<div style='margin-top:5px;'></div></div>");
						dia.find("div").append(nodes);
						dia.dialog({
							width:600,
							heigth:200,
							title:"提示",	
							buttons:[{
								text:"确定",handler:function(){
									var ids = [];
									dia.find("img").each(function(idx,node){
										var id = $(node).attr("id");
										ids.push(id);
									});
									var idsStr=ids.join(",");
									//开始后台删除
									oc.util.ajax({
										url:oc.resource.getUrl("topo/image/del.htm"),
										data:{ids:idsStr},
										success:function(){
											//删除界面上的节点
											nodeIcon.find(".icon img").each(function(idx,node){
												var id = $(node).attr("id");
												if(idsStr.indexOf(id)>=0){
													$(node).parent().remove();
												}
											});
											dia.dialog("close");
										}
									});
								}
							},{
								text:"取消",handler:function(){
									dia.dialog("close");
									//重置选中状态
									nodeIcon.find(".icon").removeClass("selected");
								}
							}]
						});
					}
				}
				nodeIcon.find(".icon").draggable({ 
					disabled:!isRemove
				});
				removeBtn.attr("removeFlag",!isRemove);
			});
			this.refreshPanel(nodeIcon, type);
		},
		initUI:function(){
			var ctx = this;
			//浏览，编辑切换
			this.doms.tabBtns.click(function(){
				var $this = $(this);
				ctx.doms.panels.addClass("hide");
				ctx.doms.tabBtns.removeClass("active");
				var panel = $this.attr("data-panel");
				$this.addClass("active");
				ctx.data.panels[panel].removeClass("hide");
				//第一次要初始化面板，请求数据
				if(!$this.attr("data-flag")){
					ctx.data.panels[panel].find(".easyui-accordion").accordion({
						fit:true
					});
					ctx.initPanel("topo_graph_"+panel+"_icon", panel+"-icon");
					$this.attr("data-flag",true);
				}
			});
			this.doms.panels.each(function(idx,panel){
				panel = $(panel);
				ctx.data.panels[panel.attr("data-panel")]=panel;
			});
			$("#topo_graph_toolbox_accord").accordion({
				fit:true
			});
			$("#topo_graph_net_accord").accordion({
				fit:true
			});
			//节点图标上传
			this.initPanel("topo_graph_node_icon", "node-icon");
			//链路图标上传
			this.initPanel("topo_graph_link_icon", "link-icon");
		}
	};
	window.topo.graph.init();
});