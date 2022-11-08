function BizNav(args){
	this.args=$.extend({
		parent:undefined,		//组件要加载到的容器
		svgHolder:undefined,	//业务svg图层持有者
		changeBgImg:function(data){},	//改变背景后回调
		dragPicStop:function(data){}	//拖动图标结束后回调
	},args);
	this.theme = "blue";			//默认主题
	this.curDragImg = undefined;	//当前被拖动的图标
	var ctx = this;
	$.get(oc.resource.getUrl("resource/module/business-service/biz_navigation.html"),function(html){
		ctx.init(html);
	},"html");
};
BizNav.prototype={
	init:function(html){
		var ctx = this;
		//搜索域
		this._searchField(html);
		//初始化固定图标，使其可拖动
		this.enableDrag(this.fields.panels.find(".icon"));
		//注册事件
		this.regEvent();
	},
	//搜索域
	_searchField:function(html){
		var ctx = this;
		this.root=$(html);
		this.root.appendTo(this.args.parent);	//将导航栏添加到parent上
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
	},
	//转换上传图片类型与后台保存数字映射关系,图片类型(0:业务,1:资源,2:基本形状,3:背景)
	_switchType:function(type){
		var typeTmp = 0;
		switch(type){
		case "business":typeTmp = 0;break;
		case "resource":typeTmp = 1;break;
		case "base":typeTmp = 2;break;
		case "bg":typeTmp = 3;break;
		default:break;
		}
		return typeTmp;
	},
	//使图标可以拖动
	enableDrag:function(dom){
		var ctx = this;
		dom.draggable({
			proxy:function(source){
				var p = $(source).clone();
				p.find("img").attr("width",55);
				p.css({
					"z-index":"100",
					position:"absolute"
				});
				p.appendTo("body");
				return p;
			},
			revert:true,
			onStartDrag:function(e){
				ctx.curDragImg = $(this).draggable('proxy').find('img');
	        },
			onStopDrag:function(e){
				var index = ctx.curDragImg.attr("src").lastIndexOf("=");
				var fileId = ctx.curDragImg.attr("src").substring(index+1);
				var data = {
					type:ctx._switchType(ctx.currentPanelKey),	//图片类型(0:业务,1:资源,2:基本形状,3:背景)
					x : e.clientX-60,
					y : e.clientY-ctx.args.svgHolder.offset().top-30
				};
				
				if(0==data.type || 1==data.type) {
					data.fileId = parseInt(fileId);
					data.width = ctx.curDragImg.width();
					data.height = ctx.curDragImg.height();
				}else if(2==data.type){
					data.shape = ctx.curDragImg.parent().attr("title");
				}
				
				if(ctx.args.dragPicStop){
					ctx.args.dragPicStop(data);
				}
			}
		});
	},
	//展开
	expand:function(){
		var fd = this.fields;
		this.root.animate({
			right:"0px"
		},200,"linear",function(){});
		fd.hook.addClass("biz_hook_off");
		fd.hook.attr("title","关闭面板");
		this.isExpand=true;
		this.root.css("height","91%");
	},
	//收缩
	shrink:function(){
		var ctx = this;
		var fd = this.fields;
		this.root.animate({
			right:"-196px"/*,
			height:"54px"*/
		},500,"linear",function(){});
		fd.hook.removeClass("biz_hook_off");
		fd.hook.attr("title","打开面板");
		this.isExpand=false;
		setTimeout(function(){
			ctx.root.css("height",45);
		},1000);
	},
	//注册事件
	regEvent:function(){
		var ctx = this;
		var resTabs = this.fields.resTabs.find("[data-panel-key]");	//导航菜单
		resTabs.on("click",function(){
			resTabs.removeClass("active");
			var tmp = $(this);
			tmp.addClass("active");
			var panelKey = tmp.attr("data-panel-key");
			ctx.fields.resPanels.find("[data-panel]").hide();
			var panel = ctx.fields.resPanels.find("[data-panel='"+panelKey+"']");
			ctx.currentPanel = panel;
			ctx.currentPanelKey = panelKey;
			if("base" == panelKey){	//基本形状不允许上传和删除
				ctx.fields.bizPic_delDiv.hide();
			}else{
				ctx.fields.bizPic_delDiv.show();
			}
			//加载自定义上传图片（只加载一次）
			if(!ctx[panelKey+"Init"]){
				ctx.refreshPanel();
				ctx[panelKey+"Init"] = true;
			}
			//显示当前panel
			panel.fadeIn();
		});
		//默认第一个选中
		$(resTabs.get(0)).trigger("click");
		
		//图标添加按钮
		this.fields.iconAddBtn.on("click",function(){
			oc.resource.loadScript("resource/module/business-service/js/biz_picManage_upload.js",function(){
				if(ctx.currentPanelKey){
					new BizImgUpload({
						imgType:ctx._switchType(ctx.currentPanelKey),
						callBack:function(){
							ctx.refreshPanel();	//上传成功后刷新面板
						}
					});
				}
			});
		});
		//图标删除按钮
		this.fields.iconDelBtn.on("click",function(){
			var removeBtn = $(this);
			var isRemove = removeBtn.attr("removeFlag");
			if(!isRemove || isRemove=="false"){//开启删除模式
				isRemove=false;
				removeBtn.removeClass("ico-del");
				removeBtn.addClass("icon-gabage-open");
				ctx.currentPanel.find(".icon,.bgicon").off().on("click",function(){
					var tmp = $(this);
					var fileId = tmp.find("img").attr("data-id");
					//系统图标文件id小于2000不允许删除
					if(!fileId || fileId < 2000) return ;
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
				if(nodes.length > 0){
					//显示确认对话框
					var dia = $("<div style='overflow-y:auto;height:215px;padding:4px;'><div style='margin-top:5px;'></div></div>");
					dia.find("div").append(nodes);
					dia.find(".icon").css({
						display:"inline-block",
						margin:"2px"
					});
					dia.find(".icon img").css({width:55,height:55});
					dia.dialog({
						width:400,
						title:"确定要删除下列自定义图标么？",	
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
									url:oc.resource.getUrl("portal/biz/image/del.htm"),
									data:{ids:idsStr},
									success:function(data){
										if(data && data.code==200){
											//删除界面上的节点
											ctx.currentPanel.find(".icon img,.bgicon img").each(function(idx,node){
												var id = $(node).attr("data-id");
												if(idsStr.indexOf(id) >= 0){
													$(node).parent().remove();
												}
											});
										}
										alert(data.data);
										dia.dialog("close");
									}
								});
							}
						},{
							text:"取消",handler:function(){
								dia.dialog("close");
							}
						}]
					});
				}
			}
			ctx.currentPanel.find(".icon").draggable({
				disabled:!isRemove	//设置是否可拖动
			});
			removeBtn.attr("removeFlag",!isRemove);
		});
		//监听背景更换
		ctx.fields.bgcontainer.on("click",".bgicon",function(){
			if(!ctx.fields.iconDelBtn.hasClass("icon-gabage-open")){
				var src =  $(this).find("img").attr("src");
				var fileId = src.substring(src.lastIndexOf("=")+1);
				var data = {fileId:parseInt(fileId)};
				ctx.args.changeBgImg(data);
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
	//添加/删除图标后->刷新面板的图标
	refreshPanel:function(){
		var ctx= this;
		var type = ctx._switchType(this.currentPanelKey);	//图片类型(0:业务,1:资源,2:基本形状,3:背景)
		var parent = this.currentPanel;
		//获取一个模板
		var template = parent.find(".template");
		//从服务器获取图片
		oc.util.ajax({
			url:oc.resource.getUrl("portal/biz/image/list/"+type+".htm"),
			success:function(data){
				if(data && data.code == 200){
					//清空以前的上传的自定义图片（data-id区分），重新更新
					ctx.currentPanel.find("img[data-id]").parent().remove();
					//绘制当前图标
					for(var i=0;i<data.data.length;i++){
						var item = data.data[i];
						var tmp = template.clone();
						var image = tmp.find("img");
						image.attr({
							src:oc.resource.getUrl("platform/file/getFileInputStream.htm?fileId="+item.fileId),
							"data-id":item.fileId
						});
						tmp.removeClass("hide template");
						template.after(tmp);	//在模板后添加新图片
						if(type == 3){	//背景
							image.attr({
								"data-bg":item.fileId
							});
						}else{
							ctx.enableDrag(tmp);
						}
					}
				}
			}
		});
	}
};