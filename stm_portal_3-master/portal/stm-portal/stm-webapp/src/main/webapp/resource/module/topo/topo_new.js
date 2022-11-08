function TopoGraph(args,holder){
	this.args=$.extend({
		callBack:function(){}
	},args);
	this.holder=holder;
	var ctx = this;
	$.get(oc.resource.getUrl("resource/module/topo/topo_new.html"),function(html){
		ctx.init(html);
		ctx.args.callBack();
	},"html");
};
TopoGraph.prototype={
	init:function(html){
		var ctx = this;
		this.topo=null;
		this.root=$(html);
		this.root.appendTo(this.holder);
		//加载拓扑首页
		$.post(oc.resource.getUrl("topo/hasTopo.htm"),function(home){
            /*BUG #49109 【中软国产化】拓扑图在没有设备的情况下，从资源面板拖拽设备进入拓扑无效。 huangping 2017/12/12 start*/
            //加载首页
            /*if(home.hasTopo){
                var topoId=0;
                if(home.homeTopo){
                    topoId=home.homeTopo.id;
                }
                ctx._loadTopo(topoId);
            }*/
            if (home.hasTopo) {
                var topoId = 0;
                if (home.homeTopo) {
                    topoId = home.homeTopo.id;
                }
                ctx._loadTopo(topoId);
            } else {
                ctx._loadTopo(0);
            }
            /*BUG #49109 【中软国产化】拓扑图在没有设备的情况下，从资源面板拖拽设备进入拓扑无效。 huangping 2017/12/12 end*/
		},"json");
		//加载工具栏
		oc.resource.loadScript("resource/module/topo/graph/toolbar_new.js", function(){
			ctx.toolbar = new TopoToolbarNew({
				holder:$("#topo_holder").parent()
			});
		});
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.topoNav = new TopoNav({
			parent:ctx.root
		});
		//初始化导航菜单
		this.navMenu = new NavMenu({},$(this.holder));
		this.regEvent();
	},
	_loadTopo:function(topoId,callBack){
		var ctx = this;
		if(!topoId) topoId=0;
		//清空tuopholder
		var $div = $("#topo_holder");
		if($div.prop("data-init")){
			//刷新
			eve("topo.loadsubtopo",ctx,topoId);
		}else{//需要初始化界面
			$div.html("");
			$div.prop("data-init",true);
			oc.resource.loadScripts([
			    "resource/module/topo/contextMenu/TopoTips.js",
			    "resource/module/topo/graph/topo.js",
			    "resource/module/topo/api/TopoBoxChoser.js",
			    "resource/module/topo/api/TopoSvg.js"
			],function(){
				new TopoTips({
					holder:$("#topo_holder").parent()
				});
				$("#topo_holder").on("contextmenu",function(e){
					eve("element.contextmenu",this,e,null);
					return false;
				});
				oc.topo.topo= ctx.topo = new Topo({
					holder:$("#topo_holder").get(0)
				});
				ctx.topoSizerBar = new TopoSizerBar({
					holder:$("#topo_holder").parent(),
					slave:ctx.topo.paper,
					slaveHolder:"#topo_holder",
					pos:{y:50}
				});
				ctx.topo.topoSizerBar=ctx.topoSizerBar;
				//拓扑缩放
				eve.on("sizerbar.change."+ctx.topoSizerBar.id,function(){
					var value = this.getValue();
					ctx.topo.updateAttr({attr:JSON.stringify(value)});
				});
				//初始化上下文菜单TODO:DFDSFAS
				oc.topo.menu=new TopoMenu({
					topo:ctx.topo,
					onLoad:function(){
						ctx.topo.id=topoId;
						ctx.topo.loadData(oc.resource.getUrl("topo/getSubTopo/"+topoId+".htm"),callBack);
					}
				},ctx.topo.holder);
				//拓扑框选
				var ts = new TopoSvg({
					holder:ctx.topo.holder
				});
				new TopoBoxChoser({
					holder:ctx.topo.holder,
					paper:ctx.topo.paper,
					svgUtil:ts,
					onEnd:function(bbox){
						if(!this.movingFlag){//正在拖动不能清空选框
							ctx.topo.chosed=[];
							var els = ctx.topo.els;
							for(key in els){
								if(key.indexOf("node")>=0){
									var el = els[key];
									if(el && el.getMaster){
										if(Raphael.isBBoxIntersect(bbox,el.getMaster().getBBox())){
											el.setState("choosed");
											ctx.topo.chosed.push(el);
										}
									}
								}
							}
						}else{
							this.movingFlag=false;
						}
					},
					//一起拖动
					onMove:function(p,source){
						if(ctx.topo.chosed.length>0){
							this.movingFlag=true;
						}
					},
					onMouseDown:function(){
						if(!this.movingFlag){
							ctx.topo.resetChose();
						}
					}
				});
			});
		}
	},
	regEvent:function(){
		var ctx = this;
		//拓扑发现大按钮
		this.fields.topoFindBtn.on("click",function(){
			oc.resource.loadScript("resource/module/topo/contextMenu/FindSettingDia.js", function(){
				new FindSettingDia();
			});
		});
		//搜索按钮
		this.fields.searchBtn.on("click",function(){
			ctx.search();
		});
		this.fields.searchIpt.on("keyup",function(e){
			if(e.keyCode==13){
				ctx.search();
			}
		});
		//搜索项单击
		this.fields.searchPane.on("click",".topo_graph_search_item",function(){
			if(ctx.oldSearchedEl){
				ctx.oldSearchedEl.setState("unfound");
			}
			var el = ctx.topo.getEl("node"+$(this).attr("data-id"));
			ctx.oldSearchedEl=el;
			el.setState("found");
			ctx.fields.searchPane.fadeOut();
		});
		//拓扑加载完成
		eve.on("topo.draw.finished",function(){
			ctx.refreshStatusBar();
		});
		//其他模块被显示
		eve.on("oc.topo.*.show",function(){
			ctx.hide();
		});
		//其他模块被隐藏隐藏
		eve.on("oc.topo.*.hide",function(){
			ctx.show();
		});
		//绘制二层拓扑
		eve.on("topo.newsecondtopo",function(topid,callBack){
			ctx._loadTopo(topid,callBack);
		});
		//监听状态栏添加新项目
		eve.on("topo.statusbar.additem",function(label,callBack){
			var spans = ctx.fields.statusbar.find("span");
			for(var i=0;i<spans.length;++i){
				var tmp = $(spans[i]);
				if(tmp.html()==label){
					return ;
				}
			}
			var item = $("<span/>");
			item.html(label);
			item.appendTo(ctx.fields.statusbar);
			item.on("click",callBack);
		});
		//监听状态栏删除项目
		eve.on("topo.statusbar.removeItem",function(label){
			var spans = ctx.fields.statusbar.find("span");
			for(var i=0;i<spans.length;++i){
				var tmp = $(spans[i]);
				if(tmp.html()==label){
					tmp.off();
					tmp.remove();
					return ;
				}
			}
		});
		//监听拓扑重置按钮
		eve.on("topo.graphreset",function(){
			//设置状态栏的计数器
			ctx.fields.total.text(0);
			ctx.fields.mcount.text(0);
			ctx.fields.umcount.text(0);
		});
		//状态栏更新
		eve.on("topo.toolbar.refresh",function(){
			ctx.refreshStatusBar();
		});
		//定位ip
		eve.on("topo.locate",function(args){
			args=$.extend({type:"ip",val:null},args);
			if(args.val && args.type=="ip"){
				ctx.locateByKeyWord(args.val);
			}
		});
	},
	hide:function(){
		this.root.hide();
	},
	show:function(){
		this.root.fadeIn();
		this.navMenu.expand();
	},
	//更新状态栏
	refreshStatusBar:function(){
		if(this.topo){
			var ctx = this;
			var total=0,mcount=0,umcount=0;
			$.each(this.topo.els,function(key,el){
				try{
					if(el.d.id.indexOf("node")>=0){
						if(el.d.instanceId){
							total++;
							if(el.d.lifeState=="monitored"){
								mcount++;
							}else{
								umcount++;
							}
						}
					}
				}catch(e){
					console.log(e);
				}
			});
			//设置状态栏的计数器
			this.fields.total.text(total);
			this.fields.mcount.text(mcount);
			this.fields.umcount.text(umcount);
			//获取所有ip
			this.topo.getAllIps(function(ipmap){
				ctx.ipmap = ipmap;
			});
		}
	},
	//通过输入关键字（IP/设备名称）定位
	locateByKeyWord:function(keyWord){
		var retn=[],ctx=this,keyWord=$.trim(keyWord);
		if(this.topo){
			if(!keyWord) keyWord="";
			var regexp = new RegExp(keyWord.replace("\.","\\."),"i");
			//先匹配名称
			$.each(this.topo.els,function(key,el){
				if(el.d && el.d.showName){
					if(regexp.test(el.d.showName)){
						retn.push(el);
					}
				}
			});
			//再匹配IP
			if(this.ipmap){
				for(var key in this.ipmap){
					if(regexp.test(key)){
						var ids = this.ipmap[key];
						for(var i=0;i<ids.length;i++){
							var el = this.topo.getEl("node"+ids[i]);
							if(el){
								retn.push(el);
							}
						}
					}
				}
			}else{
				$.each(this.topo.els,function(key,el){
					if(el.d && el.d.ip){
						if(el.d.ip.indexOf(keyWord)>=0){
							retn.push(el);
						}
					}
				});
			}
		}
		//清空上次搜索结果项的选中状态
		for(var i=0;i<ctx.topo.chosed.length;i++){
			ctx.topo.chosed[i].setState("unchoosed");
		}
		//将搜索的结果呈现给用户
		$.each(retn,function(idx,el){
			/*var tmp = ctx.fields.searchResultTemplate.clone();
			tmp.text(el.d.ip);
			tmp.removeClass("hide");
			tmp.removeAttr("data-field");
			tmp.attr("data-id",el.d.rawId);
			ctx.fields.searchResultTemplate.after(tmp);*/
			el.setState("choosed");
			ctx.topo.chosed.push(el);
		});
		return retn;
	},
	//通过ip搜索拓扑中的节点
	search:function(){
		var ctx = this;
		ctx.fields.searchPane.fadeIn();
		//清空上次搜索结果
		ctx.fields.searchPane.find("[data-id]").remove();
		//获取需要搜索的内容
		var keyWord = ctx.fields.searchIpt.val();
		this.locateByKeyWord(keyWord);
	},
	destroy:function(){
		this.topo.destroy();
		this.topo = null;
		this.topoNav=null;
		this.root.remove();
	}
};