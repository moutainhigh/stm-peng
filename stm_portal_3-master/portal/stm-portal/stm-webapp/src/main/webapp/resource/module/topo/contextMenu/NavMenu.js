function NavMenu(args,holder){
	this.args = args;
	this.holder = holder;
	this.initBottomMenuWith = "775px";
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/NavMenu.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
NavMenu.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		//搜索域
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.root.appendTo(this.holder);
		this.initBootomMenu();
		this.shrink();
	},
	//收缩工具栏
	shrink:function(){
		var ctx = this;
		//收缩动画
		this.fields.contentBox.animate({
			width:"0px"//24
		},600,function(){
			ctx.fields.shrinkBtn.removeClass("expandState");
			ctx.fields.shrinkBtn.addClass("shrinkState");
			ctx.root.css("width","90px");
		});
	},
	//展开菜单
	expand:function(){
		var ctx = this;
		this.fields.shrinkBtn.addClass("expandState");
		this.fields.shrinkBtn.removeClass("shrinkState");
		this.fields.contentBox.animate({
			width:ctx.initBottomMenuWith
			
		},100,function(){
			ctx.root.css("width","870px");//850
		});
	},
	//初始化拓扑底部菜单权限
	initBootomMenu:function(){
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl('topo/navMenu/check.htm'),
			type: 'POST',
			dataType: "json", 
			successMsg:null,
			success:function(data){
				ctx.regEvent(data.data);
			}
		});
	},
	regEvent:function(isSysUser){
		var ctx = this;
		var items = this.root.find(".topo_nav_menu_item");
		//一览表
		this.fields.listTable.on("click",function(){
			$("<div/>").dialog({
				title: '一览表',
				draggable:true,
				href:oc.resource.getUrl("resource/module/topo/instanceTable/instanceTabs.html"),
				width: 1200,    
				height:620,    
				cache: false,
				modal: true
			});
			items.removeClass("active");
			$(this).addClass("active");
		});
		//面板管理
		this.fields.backboardManage.on("click",function(){
			if(!ctx.backboard){
				ctx.backboard=new BackBoard({
					appendTo:$('#topo .topo-content')
				});
			}else{
				ctx.backboard.show();
			}
			items.removeClass("active");
			$(this).addClass("active");
		});
		//ip-mac-port
		this.fields.ipMacPort.on("click",function(){
			if(!ctx.ipMacPort){
				ctx.ipMacPort=$("<div/>");
				$('#topo .topo-content').append(ctx.ipMacPort);
				ctx.ipMacPort.load(oc.resource.getUrl('resource/module/topo/ip-mac-port/ip_mac_port_tabs.html'));
				eve("oc.topo.ip-mac-port.show");
			}else{
				ctx.ipMacPort.load(oc.resource.getUrl('resource/module/topo/ip-mac-port/ip_mac_port_tabs.html'));
				eve("oc.topo.ip-mac-port.show");
			}
			items.removeClass("active");
			$(this).addClass("active");
		});
		
		//系统管理员才有下列菜单
		if(isSysUser){
			//拓扑权限
			this.fields.topoAuthSetting.on("click",function(){
				var topoAuthSetting = null;
				var dia = null;
				dia=$("<div/>").dialog({
					title: '拓扑权限设置',
					draggable:true,
					href:oc.resource.getUrl("resource/module/topo/topoAuthSetting/topoAuthSetting.html"),
					onLoad:function(){
						topoAuthSetting = new TopoAuthSetting();
						topoAuthSetting.init();
					},
					width: 900,    
					height: 500,
					onOpen:function(){	//覆盖oc扩展的onPen方法，解决左侧换肤后样式（蓝色皮肤）与应用按钮样式冲突不显示问题
						var that=$(this),_html,opts=that.dialog('options');
						opts._onOpen&&opts._onOpen();
						if(opts.isOcOpened)return;
						opts.isOcOpened=true;
						setTimeout(function(){
							that.parent('.panel').find('.dialog-button .l-btn-left').each(function(){
								_html=$('<div class="btn-l">'+
										'  <div class="btn-r">'+
										'    <div class="btn-m"></div>'+
										'  </div>'+
										'</div>'),
										btn_m=_html.find('.btn-m').append($(this).children());
										$(this).append(_html);
							});
						},1);
					},
					cache: false,    
					modal: true ,
					buttons:[{
						text:'应用',
						handler:function(){
							topoAuthSetting.save(dia);
						}
					}]
				});
				items.removeClass("active");
				$(this).addClass("active");
			});
			//topo发现设置
			this.fields.topoFindSetting.on("click",function(){
				oc.resource.loadScript("resource/module/topo/contextMenu/FindSettingDia.js", function(){
					new FindSettingDia();
				});
				items.removeClass("active");
				$(this).addClass("active");
			});
			//topo数据维护
			this.fields.backupRecovery.on("click",function(){
				oc.resource.loadScript("resource/module/topo/contextMenu/BackupRecovery.js", function(){
					new BackupRecovery();
				});
				items.removeClass("active");
				$(this).addClass("active");
			});
		}else{
			this.fields.topoAuthSetting.hide();
			this.fields.topoFindSetting.hide();
			this.fields.backupRecovery.hide();
			this.initBottomMenuWith = "465px";
		}
		//全局设置
		this.fields.topoSetting.on("click",function(){
			oc.resource.loadScript("resource/module/topo/topoSetting/topoSetting.js", function(){
				new TopoGlobalSettingDia();
			});
			items.removeClass("active");
			$(this).addClass("active");
		});
		//回收站
		this.fields.recycleStation.on("click",function(){
			oc.resource.loadScript("resource/module/topo/contextMenu/TopoRecycleStation.js", function(){
				new TopoRecycleStation();
			});
			items.removeClass("active");
			$(this).addClass("active");
		});
		//收缩按钮点击
		this.fields.shrinkBtn.on("click",function(){
			if($(this).hasClass("shrinkState")){
				ctx.expand();
			}else{
				ctx.shrink();
			}
		});
		//展开按钮点击
		this.fields.expandBtn.on("click",function(){
			ctx.expand();
		});
		//监听面板隐藏的消息
		eve.on("oc.topo.*.hide",function(){
			ctx.root.fadeIn();
		});
		//监听面板显示的消息
		eve.on("oc.topo.*.show",function(){
			ctx.root.hide();
		});
		eve.on("topo.navmenu.show",function(){
			ctx.root.show();
		});
		eve.on("topo.navmenu.hide",function(){
			ctx.root.hide();
		});
	}
};