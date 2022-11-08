function BackBoard(args){
	this.args=$.extend({
		appendTo:$("body")
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/backboard/backboard.html"),
		success:function(html){
			//发布面板已经显示的消息
			oc.resource.loadScripts([
			  "resource/module/topo/backboard/BB.js"
			],function(){
				ctx.init(html);
				eve("oc.topo.backboard.show");
			});
		},
		dataType:"html",
		type:"get"
	});
};
BackBoard.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		//更新主题
		$.post(oc.resource.getUrl("topo/help/currentTheme.htm"),function(result){
			if(result.status==200){
				ctx.changeTheme(result.theme);
			}
		},"json");
		this.root.appendTo(this.args.appendTo);
		
		//检索域
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//初始化布局
		this.fields.main.layout({
			fit:true
		});
		//初始化左侧设备列表
		this.fields.backboardManage.accordion({});
		//中央面板
		this.fields.centerPanel.panel("RenderP");
		//初始化绘图面板
		var paper = Raphael("backboardRaphael",$("#backboardRaphael").width(),$("#backboardRaphael").height());
		//事件委托器
		new SvgEvent({
			paper:paper,
			holder:"#backboardRaphael"
		});
		//初始化绘图器
		this.bb = new BB({
			paper:paper,
			holder:"#backboardRaphael"
		});
		//预览按钮
		this.fields.previewBtn.linkbutton("RenderLB");
		//初始化左侧列表
		$.post(oc.resource.getUrl("topo/backboard/devices.htm"),function(devices){
			var others = [];
			ctx.devices=devices;
			for(var key in devices){
				var contanier = ctx.fields[key];
				if(contanier){
					contanier.list = oc.ui.navsublist({
						selector:contanier,
						textField:"ip",
						data:devices[key],
						click:function(href,item,e){
							ctx.menuClick(href,item,e);
						}
					});
				}//没有容器的，就放到others容器中
				else{
					var tmpArray = devices[key];
					for(var i=0;i<tmpArray.length;++i){
						others.push(tmpArray[i]);
					}
				}
			}
			//加入到others面板中
			ctx.fields.others.list = oc.ui.navsublist({
				selector:ctx.fields.others,
				textField:"ip",
				data:others,
				click:function(href,item,e){
					ctx.menuClick(href,item,e);
				}
			});
		},"json");
		//搜索结果面板
		this.fields.searchPanel.panel("RenderP",{
			closable:true,
			onClose:function(){
				ctx.fields.backboardManage.fadeIn();
				//跳转到当前项目位置
				if(ctx.currentItem){
					ctx.jumpTo(ctx.currentItem.id);
				}
			}
		});
		//调整搜索结果面板关闭按钮位置
		this.fields.searchPanel.parent().find(".panel-tool").css({
			height:"24px",
			top:"14px"
		});
		//默认隐藏搜索结果面板
		this.fields.searchPanel.panel("close");
		this.regEvent();
	},
	//修改主题
	changeTheme:function(theme){
		if(this.fields.drawCont && theme!="default"){
			this.fields.drawCont.find("img").each(function(idx,dom){
				var tmp = $(dom);
				tmp.attr("src",tmp.attr("src").replace("default",theme));
			});
		}
	},
	regEvent:function(){
		var ctx = this;
		//绘图对齐工具栏
		this.root.find(".backboard-align > li").click(function(){
			var $dom = $(this);
			var type = $dom.data("type");
			ctx.bb.align(type);
		});
		//绘图命令工具栏
		this.root.find(".backboar-tool").on("click","li",function(){
			var $dom = $(this),
				type = $dom.attr("class");
			switch(type){
				case "delete":
					ctx.bb.removeChosed();
				break;
				case "lock":
					ctx.fields.locker.attr({"class":"unlock"});
					ctx.bb.lock();
				break;
				case "choose":
					ctx.bb.choose(true);
				break;
				case "save":
					//打开确认对话框
					var dia = $("<div/>").dialog({
						title:"保存面板信息",width:400,height:170,
						content:ctx.fields.saveDia,
						buttons:[{
							text:"覆盖默认",handler:function(){
								dia.dialog("close");
								ctx.bb.save(true);
							}
						},{
							text:"保存",handler:function(){
								dia.dialog("close");
								ctx.bb.save(false);
							}
						},{
							text:"取消",handler:function(){
								dia.dialog("close");
							}
						}]
					});
				break;
				case "unlock":
					ctx.fields.locker.attr({"class":"lock"});
					ctx.bb.unlock();
					break;
				case "rotation":
					ctx.bb.rotation();
					break;
			}
		});
		//当前被单击的图标时变换图标
		eve.on("oc_topo_image_clicked",function(){
			ctx.fields.locker.attr({"class":this.editable?"lock":"unlock"});
		});
		//tab切换
		this.root.find('.tab').on('click','li',function(){
			ctx.root.find('.tab .active').removeClass('active');
			$(this).addClass('active');
			ctx.root.find('.oc-draw-container > div').hide().eq($(this).index()).fadeIn();
		});
		ctx.root.find('.oc-draw-container > div').eq(0).show();
		//收缩菜单面板
		this.root.find('.drawTool-right-top').on('click',function(){
			$(this).toggleClass('shrink');
			if($(this).hasClass('shrink')){
				$('.topo-content .drawTools').css({'height':'41px','overflow':'hidden'});
				ctx.root.find('.sh-btn').text('打开面板配置工具');
			}else{
				$('.topo-content .drawTools').css({'height':'85%','overflow':'visible'});
				ctx.root.find('.sh-btn').text('面板配置工具');
			}
			$('.close-drawTools').toggle();
		});
		/*//导入xml
		this.fields.importBtn.on("click",function(){
			oc.resource.loadScript("resource/module/topo/backboard/importXml.js", function(){
				new ImportXmlDia();
			});
		});
		//导出xml
		this.fields.exportBtn.on("click",function(){
			var cItem = ctx.currentItem;
			if(cItem){
				oc.resource.loadScript("resource/module/topo/exportFile.js", function(){
					var ef = new ExportFile();
					var url = oc.resource.getUrl("topo/backboard/export.htm?instanceId="+cItem.instanceId+"&ip="+cItem.ip);
					ef.exportFile(url);
				});
			}else{
				alert("请选中导出的面板节点","danger");
			}
		});*/
		//启用拖拽
		this.root.find(".icon").draggable({ 
			proxy:"clone",
			revert:true
		}); 
		//预览
		this.fields.previewBtn.on("click",function(){
			oc.resource.loadScript("resource/module/topo/contextMenu/BackBoardInfoDia.js", function(){
				new BackBoardInfoDia({
					value:ctx.bb.getValue(),
					instanceId:ctx.bb.instanceId,
					downDeviceFlag:false	//显示下联设备
				});
			});
		});
		//搜索按钮
		this.fields.searchBtn.on("click",function(){
			ctx.search(ctx.fields.keyWord.val());
		});
		//回到拓扑图按钮
		this.fields.backBtn.on("click",function(){
			ctx.hide();
		});
	},
	hide:function(){
		//自己隐藏掉
		this.root.hide();
		//发布面板已经隐藏的消息
		eve("oc.topo.backboard.hide");
	},
	show:function(){
		this.root.fadeIn();
		//发布面板已经显示的消息
		eve("oc.topo.backboard.show");
	},
	//左侧菜单项单击
	menuClick:function(href,item,e){
		var ctx = this;
		ctx.currentItem=item;//当前选中项
		ctx.bb.reset();
		ctx.bb.load(item.instanceId, function(info){
//			info.ip=item.ip;
//			info.venderName=item.venderName||"--";
//			info.series=item.series||"--";
//			info.name=item.name||"--";
//			info.typeName=item.typeName||"--"
			
			ctx.setTitleInfo(info);
		});
	},
	//搜索
	search:function(keyWord){
		var retn = [];
		var ctx = this;
		if(this.devices){
			for(var key in this.devices){
				var items = this.devices[key];
				for(var i=0;i<items.length;++i){
					var item = items[i];
					if(item.ip && item.ip.indexOf(keyWord)>=0){
						retn.push(item);
					}
				}
			}
		}
		this.fields.searchPanel.fadeIn();
		//展示搜索结果
		this.fields.searchPanel.html("");//清空
		oc.ui.navsublist({
			selector:this.fields.searchPanel,
			textField:"ip",
			data:retn,
			click:function(href,item,e){
				ctx.menuClick(href,item,e);
			}
		});
		//切换到搜索结果面板
		this.fields.backboardManage.hide();
		this.fields.searchPanel.panel("open");
	},
	//编辑某一项的面板信息-打开所在的index
	jumpTo:function(id){
		if(this.devices){
			//搜索位置
			var tmpKey=null,tmpIdx=-1;
			for(var key in this.devices){
				var items = this.devices[key];
				for(var i=0;i<items.length;++i){
					if(!this.fields[key] || key.indexOf("other")>=0){
						tmpIdx++;
					}
					var item = items[i];
					if(item.id == id){
						tmpKey=key;
						if(this.fields[key] && key.indexOf("other")<0){
							tmpIdx=i;
						}
						break;
					}
				}
				if(tmpKey){
					break;
				}
			}
			//跳转到相应的类型面板下，并且选中它
			var container = this.fields[tmpKey]; 
			if(!container){
				container = this.fields.others;
			}
			//定位到面板
			container.panel("expand");
			//定位到具体那一项
			var selectedItem = container.list.get(tmpIdx);
			if(selectedItem){
				$(selectedItem).trigger("click");
			}
		}
	},
	//设置标题信息
	setTitleInfo:function(info){
		var ip = (null == info.ip)?"- -":info.ip;
		var name = (null == info.name)?"- -":info.name;
		var typeName = (null == info.typeName)?"- -":info.typeName;
		var venderName = (null == info.venderName)?"- -":info.venderName;
		var series = (null == info.series)?"- -":info.series;
		this.fields.centerPanel.panel("RenderP",{
			title:"设备IP:"+ip+"  设备名称:"+name+"  设备类型:"+typeName+"  设备厂商:"+venderName+"  设备型号:"+series
		});
	}
};