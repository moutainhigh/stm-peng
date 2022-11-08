$(function(){
	window.topo = window.topo || {};
	var $root = $("#topo-menu");
	var rootPos=$root.position();
	$root.find('#instanceTable').on('click',function(){
		$("<div/>").dialog({
			title: '一览表',
			href:oc.resource.getUrl("resource/module/topo/instanceTable/instanceTabs.html"),
		    width: 1200,    
		    height:620,    
		    cache: false,    
		    modal: true
		});
	});
	//设为拓扑首页和新建子拓扑菜单项
	$(".menu-arrow").click(function(e){
		var p = $(this).position();
		$("#two-deck-menu").css({
			"display":"block"
		});
		e.stopPropagation();
	});
	
	$('#two-deck-menu').on('mouseleave',function(){
		$(this).css("display","block");
	});
	
	//设为拓扑首页
	$("#topo_menu_index_btn").click(function(){
		$("#two-deck-menu").css({
			"display":"none"
		});
	});
	//新建子拓扑
	$("#topo_menu_sub_topo_btn").click(function(e){
		var dia = new NewSubTopoDia();
		dia.on("success",function(id){
			eve("oc_topo_subtopo",this,id);
			window.topo.menu.updateMenu();
		});
		$("#two-deck-menu").css({
			"display":"none"
		});
	});
	//面板管理
	$root.find('#backboard').on('click',function(){
		$('#topo .topo-content').html("");
		new BackBoard({
			appendTo:$('#topo .topo-content')
		});
		$root.find(".short-cut-menu-drager").click();//TODO:要修改
	});
	//iP-mac-port
	$root.find('#ipmacport').on('click',function(){
		$('#topo .topo-content').load('module/topo/ip-mac-port/ip_mac_port_tabs.html');
	});
	//拓扑发现配置
	$root.find('#findSetting').on('click',oc.topo.findSetting.findSettingDia);
	//拓扑设置
	$root.find('#topoSetting').on('click',function(){
		var topoSetting = null;
		var dia=null;
		dia= $("<div/>").dialog({
			title: '拓扑设置',
			href:oc.resource.getUrl("resource/module/topo/topoSetting/topoSetting.html"),
			onLoad:function(){
				topoSetting=oc.topo.topoSetting;
				topoSetting.init();
			},
		    width: 500,    
		    height: 500,    
		    cache: false,    
		    modal: true ,
		    buttons:[{
				text:'保存',
				handler:function(){
					topoSetting.save();
					dia.dialog("close");
				}
			},{
				text:'取消',
				handler:function(){
					dia.dialog("close");
				}
			}]
		});
	});
	//拓扑权限设置
	$root.find('#topoAuthSetting').on('click',function(){
		var topoSetting = null;
		var dia=null;
		dia= $("<div/>").dialog({
			title: '拓扑权限设置',
			href:oc.resource.getUrl("resource/module/topo/topoAuthSetting/topoAuthSetting.html"),
			onLoad:function(){
				var topoAuthSetting = new TopoAuthSetting();
				topoAuthSetting.init();
			},
			width: 900,    
			height: 530,    
			cache: false,    
			modal: true ,
			buttons:[{
				text:'应用',
				handler:function(){
					topoSetting.save();
					dia.dialog("close");
				}
			}]
		});
	});
	window.topo.menu = {
		shortCutMenu:$root.find(".short-cut-menu"),
		init:function(){
			this.initUI();
			this.regEvent();
		},
		regEvent:function(){
			var ctx = this;
			$root.find(".short-cut-menu-drager").click(function(){
				var drager = $(this);
				var isopen = drager.attr("isopen");
				if(isopen && isopen == "true"){
					drager.attr("isopen",false);
					drager.attr("src","module/topo/img/menu/short-cut-menu-drager-open.png");
					ctx.shortCutMenu.css("left","-154px");
					$('#two-deck-menu,#thrid-deck-menu').hide();
				}else {
					drager.attr("isopen",true);
					drager.attr("src","module/topo/img/menu/short-cut-menu-drager-close.png");
					ctx.shortCutMenu.css("left","5px");
				}
			});
		},
		updateMenu:function(){
			//获取子拓扑列表
			$.get(oc.resource.getUrl("topo/subTopos.htm"),function(topos){
				$("#twoLevelTopo").parent().find(".two-level-sub-top").remove();
				$.each(topos,function(idx,t){
					var item = $("<div class='menu-item menu-sub-item two-level-sub-top' style='text-indent:4em;'></div>");
					var title = $("<span></span>");
					title.html(t.name);
					title.appendTo(item);
					item.attr("data-id",t.id);
					$("#twoLevelTopo").after(item);
					item.on("click",function(){
						var id = $(this).attr("data-id");
						eve("oc_topo_subtopo",this,id);
					});
				});
			},"json");
		},
		initUI:function(){
			this.shortCutMenu.layout();
			$root.find(".topo-nav").panel("RenderP",{
				headerCls:"topo-nav-header"
			});
			//拓扑导航
			$root.find(".topo-nav-header").click(function(){
				$("#topo .topo-content").load("module/topo/topo.html");
			});
			//进入二层网络拓扑
			$("#twoLevelTopo").on("click",function(){
				eve("oc_topo_subtopo",this,null);
			});
			//进入三层网络拓扑
			$("#threeLevelTopo").on("click",function(){
				eve("oc_topo_subtopo",this,1);
			});
			this.updateMenu();
		}
	};	
	window.topo.menu.init();
});