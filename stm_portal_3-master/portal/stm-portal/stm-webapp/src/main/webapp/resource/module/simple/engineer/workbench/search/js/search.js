(function($){
	function Search(args){
		this._cfg = $.extend({},args|{});
	}

	Search.prototype = {
			_id:'oc_simple_engineer_workbench_search',
			_page:0,
			_mainDiv:0,
			_contentDiv:0,
			_menuTitlesUl:0,
			_cfg:0,
			_keyword:0,
			_items:[],
			_searchType:0,
			_resourceMap:{},
			content:0,
			noDataDiv:0,
			_open:function(){
				var target = this;
				$.ajax({
					url:oc.resource.getUrl('resource/module/simple/engineer/workbench/search/search.html'),
					dataType : 'html',
					success:function(data){
						var reg = /<body[^>]*>((.|[\n\r])*)<\/body>/im;
						var body = reg.exec(data);
						target._page = $(body ? body[1] : data);
						target._page.filter(".transparent").css('opacity', 0.7);
						target._page.appendTo('body').hide().fadeIn();
						target._init();
					},
					error:function(e){
						alert("页面被管理员给搞丢了");
					}
				});
				return this;
			},
			close:function(){
				this._page.remove();
			},
			_init : function(){
				this._mainDiv = $("#" + this._id).attr("id", oc.util.generateId());
				this._contentDiv = this._mainDiv.find(".oc_simple_seach_content .seach_tab_box").first();
				this.noDataDiv = this._contentDiv.find(".no_data_div");
				this._menuTitlesUl = this._mainDiv.find(".search_menus").first();
				this._keyword = this._mainDiv.find(":input[name=keyword]");
				this._initTitle();
				var events = this._initEvents;
				for(var i in events){
					try{
						events[i].call(this);
					}catch(e){
						log(e,'初始化事件失败');
					}
				}
			},
			_exceptIds:[1,	//首页
			            2,	//系统管理
			            7,	//流量分析
			            9	//知识库管理
			           ],
			_initTitle:function(){
				try{
					this._contentDiv.append('<div class="content" style="display:none" data-field="all"/>');
					var rights = oc.index.getUser().rights, items = this._items = [];
					if(rights.length>10){
						//处理换行
					}
					for(var i=0; i<rights.length; i++){
					
						if(rights[i].pid==0&&rights[i].type==0&&this._exceptIds.indexOf(rights[i].id)==-1){
							var item = {},title = rights[i].name;
							item.title = title;
							item.type = rights[i].id;
							items.push(item);
							if(item.type!=15){//手动屏蔽虚拟化模块
								this._menuTitlesUl.append($('<li><a href="javascript:void(0)" title="'+title+'">'+title+'</a></li>').data("opts", item));
								this._contentDiv.append('<div class="content" style="display:none" data-field="'+item.type+'"/>');
							}
						}
					}
					var opts = {};
					opts.title = "知识库";
					opts.type = 'knowledge';
					this._menuTitlesUl.append($('<li><a href="javascript:void(0)" title="知识">知识</a></li>').data("opts", opts));
					this._contentDiv.append('<div class="content" style="display:none" data-field="knowledge"/>');
				}catch(e){
					log(e.message,'初始化页签失败');
				}
			},
			initTabContent:function(field){
				var contents = this._contentDiv.children().hide();
				this.noDataDiv.show();
				return contents.filter("[data-field="+field+"]").html('').show();
			},
			initResource:function(param, fn){
				oc.util.ajax({
		    		url:oc.resource.getUrl('simple/search/init.htm'),
		    		data: param,
		    		failureMsg:'初始化资源失败！',
		    		success:function(result){
		    			fn&&fn(result.data);
		    		}
		    	});
			},
			_initEvents : {
				search : function(){
					var target = this;
					this._keyword.keyup(function(e){
						e.keyCode==13&&$(this).next().trigger('click');
					}).next().click(function(){
						target._contentDiv.show();
						var keyword = target._keyword.val().trim();
						if(keyword!=''){
							var isIP =  oc.util.IPCheck(keyword),
							items = target._items, opts = target._menuTitlesUl.data("opts"),
							param = {keyword:keyword, isIP:isIP};
							var type = !opts ? 'all' : opts.type;
							var div = target.initTabContent(type);
							var single = opts ? true : false;
							
							if(!opts||type=='knowledge'){
								target._loadKnowledge.call(target,{
									type:'init',
									title:'知识',
									div:div,
									single:single,
									params:param
								});
							}
							if(type!='knowledge'){
								target.initResource(param,function(data){
									var resourceIds = data.resourceIds||[];
					    			target._resourceMap = data.resource||{};
					    			target._contentDiv.data("resourceId", {resourceIds:resourceIds.join()});
					    			if(opts){
										target._loadItems.call(target,{
											type:'init',
											title:opts.title,
											div:div,
											single:single,
											params:$.extend({},param, {type:type})
										});
									}else{
										for(var i=0; i<items.length; i++){
											target._loadItems.call(target,{
												type:'init',
												title:items[i].title,
												div:div,
												params:$.extend({},param, {type:items[i].type})
											});
										}
									}
								});
							}
						}
					})
					.dblclick(function(){
						return ;
					});
				},
				close: function(){
					var target = this;
					this._mainDiv.find(".oc_simple_close_btn").click(function(){
						target._page.remove();
					});
				},
				searchMenus:function(){
					
					var target = this,
					menus = null;
					menus = target._menuTitlesUl.find("a").click(function(){
						menus.removeClass("select_cha");
						$(this).addClass("select_cha");
						var opts = !$(this).attr("type") ? $(this).parent().data("opts") : false;
						target._menuTitlesUl.data("opts", opts);
						if(opts){
							target._contentDiv.show();
							var keyword = target._keyword.val().trim();
							if(keyword!=''){
								var isIP =  oc.util.IPCheck(keyword),
								items = target._items, opts = target._menuTitlesUl.data("opts"),
								param = {keyword:keyword, isIP:isIP};
								var div = target.initTabContent(opts.type);
								var type = opts.type;
								if(type!='knowledge'){
									target.initResource(param,function(data){
										var resourceIds = data.resourceIds||[];
						    			target._resourceMap = data.resource||{};
						    			target._contentDiv.data("resourceId", {resourceIds:resourceIds.join()});
					    				target._loadItems.call(target,{
											type:'init',
											title:opts.title,
											div:div,
											params:$.extend({},param, {type:type}),
											single:true
										});
									});
									
								}else{
									target._loadKnowledge.call(target,{
										type:'init',
										title:'知识',
										div:div,
										params:$.extend({},param, {type:type}),
										single:true
									});
								}
							}
						}else{
							target._keyword.next().trigger("click");
						}
					});
				},
				initItemClick:function(){
					var target = this;
					target._contentDiv.on('click', 'li', function(){
						var item = $(this).data("data"),
						opts = $(this).parent().data("opts");
						if(item){
							if(opts.type){
								opts.type += "";
							}else{
								opts.type = "knowledge";
							}
							var fn = target._goDetailPageFn[opts.type];
							fn&&fn.call(this, item);
						}
					});
				}
			},
			_render:function(opts, datas){
				var target = this;
				if(datas.length>0){
    				if(opts.type==='init'){
						var content = $('<div class="seach_tab_res"/>')
						.append($('<div class="seach_tab_title"/>').html("<h4>"+opts.title+"</h4>")),
						height = 320,
						ul = $("<ul class='scroll_content' style='height:"+height+"px;overflow-y: auto;'></ul>")
						.scroll(function(){
							var li = $(this).find("li"), 
							height = (li.first().height()+2)*li.size() - $(this).scrollTop() - $(this).height(),	//2px是border样式的像素
							that = this;
							if(height<=0){
								var keyword = target._keyword.val().trim();
								if(keyword!=''){
									var isIP =  oc.util.IPCheck(keyword),
									opts = $(this).data("opts");
									if(opts){
										target._loadItems.call(target,{
											type:'scroll',
											title:opts.title,
											params:{keyword:keyword, isIP:isIP, type:opts.type, startRow:$(that).find("li").size() + 1},
											contentDiv : $(that).parent()
										});
									}
								}
							}
						})
						.data("opts", $.extend({}, opts.params, {title:opts.title}));
						var map = target._resourceMap,
						params =  opts.params;
						for(var i=0; i<datas.length; i++){
							if(params.type==6){
								ul.append(
										$("<li>"+(map[datas[i].sourceID]||'未知的资源')+"-<span>"+opts.title+"-"+datas[i].content+"</span></li>")
										.css('cursor','pointer')
										.data("data", datas[i])
								);
							}else{
								ul.append(
										$("<li>"+(map[datas[i].resourceId]||'未知的资源')+"-<span>"+opts.title+"-"+datas[i].nav+"</span></li>")
										.css('cursor','pointer')
										.data("data", datas[i])
								);
							}
							
						}
						opts.div.append(content.append(ul));
					}else{
						var contentDiv = opts.contentDiv;
						if(contentDiv){
							var ul = contentDiv.find("ul"),
							map = target._resourceMap,
							params = ul.data("opts");
							for(var i=0; i<datas.length; i++){
								if(params.type==6){
									ul.append(
											$("<li>"+(map[datas[i].sourceID]||'未知的资源')+"-<span>"+opts.title+"-"+datas[i].content+"</span></li>")
											.css('cursor','pointer')
											.data("data", datas[i])
									);
								}else{
									ul.append(
											$("<li>"+(map[datas[i].resourceId]||'未知的资源')+"-<span>"+opts.title+"-"+datas[i].nav+"</span></li>")
											.css('cursor','pointer')
											.data("data", datas[i])
									);
								}
							}
						}
					}
    			}
			},
			_loadItems:function(opts){
				var target = this;
				var page = $.extend({startRow:0, rowCount:11}, target._contentDiv.data("resourceId"));
				oc.util.ajax({
		    		url:oc.resource.getUrl('simple/search.htm'),
		    		data: $.extend(page, opts.params),
		    		failureMsg:'搜索失败！',
		    		success:function(result){
		    			var datas = result.data,
		    					datas = datas!=-1 ? datas : [];
		    			if(datas.length>0){
		    				target.noDataDiv.hide();
		    				if(opts.single){
		    					opts.div&&opts.div.show();
		    				}
		    			}else{
		    				if(opts.single){
		    					target.noDataDiv.show();
		    					opts.div&&opts.div.hide();
		    				}
		    			}
		    			target._render(opts, datas);
		    		},
		    		error:function(e){
		    			target.noDataDiv.show();
    					opts.div&&opts.div.hide();
		    		}
		    	});
				
			},
			_renderknoledge:function(opts, datas){
				var target = this;
				if(datas.length>0){
    				if(opts.type==='init'){
						var content = $('<div class="seach_tab_res"/>')
						.append($('<div class="seach_tab_title"/>').html("<h4>"+opts.title+"</h4>")),
						height = 320,
						ul = $("<ul class='scroll_content' style='height:"+height+"px;overflow-y: auto;'></ul>")
						.scroll(function(){
							var li = $(this).find("li"), 
							height = (li.first().height()+2)*li.size() - $(this).scrollTop() - $(this).height(),	//2px是border样式的像素
							that = this;
							if(height<=0){
								var keyword = target._keyword.val().trim();
								if(keyword!=''){
									opts = $(this).data("opts");
									if(opts){
										target._loadKnowledge.call(target,{
											type:'scroll',
											title:opts.title,
											params:{keyword:keyword, startRow:$(that).find("li").size() + 1},
											contentDiv : $(that).parent()
										});
									}
								}
							}
						})
						.data("opts", $.extend({}, opts.params, {title:opts.title}));
						
						for(var i=0; i<datas.length; i++){
							ul.append(
									$("<li>"+datas[i].sourceContent+"-<span>"+opts.title+"</span></li>")
									.css('cursor','pointer')
									.data("data", datas[i])
							);
						}
						opts.div.append(content.append(ul));
					}else{
						var contentDiv = opts.contentDiv;
						if(contentDiv){
							var ul = contentDiv.find("ul");
							for(var i=0; i<datas.length; i++){
								ul.append(
										$("<li>"+datas[i].keywords+"-<span>"+opts.title+"</span></li>")
										.css('cursor','pointer')
										.data("data", datas[i])
								);
							}
						}
					}
    			}
			},
			_loadKnowledge:function(opts){
				var target = this,
				page = {startRow:0, rowCount:11};
				var data = $.extend(page, opts.params);
				data.type = undefined;
				oc.util.ajax({
		    		url:oc.resource.getUrl('simple/search/knowledge.htm'),
		    		data: data,
		    		failureMsg:'搜索失败！',
		    		success:function(result){
		    			var datas = result.code==200 ? result.data : [];
		    			if(datas.length>0){
		    				target.noDataDiv.hide();
		    				if(opts.single){
		    					opts.div&&opts.div.show();
		    				}
		    			}else{
		    				if(opts.single){
		    					target.noDataDiv.show();
		    					opts.div&&opts.div.hide();
		    				}
		    			}
		    			target._renderknoledge(opts, datas);
		    		},
		    		error:function(e){
		    			target.noDataDiv.show();
    					opts.div&&opts.div.hide();
		    		}
		    	});
			},
			_goDetailPageFn:{	//此函数的this作用域是当前点击的li
				"3":function(item){	//资源管理
					oc.resource.loadScripts(['resource/third/highcharts/js/highcharts.js','resource/third/highcharts/js/highcharts-more.js','resource/third/highcharts/js/exporting.js','resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js'], function(){
						oc.module.resmanagement.resdeatilinfonew.open({instanceId:item.bizId});
					});

				},
				"4":function(item){	//业务管理
					var canvas = null;
					var lane = null;
					
					var dlg=$('<div/>').dialog({
						title:'业务管理查看',
//						maximizable : true,
						content:'<div class="svg" style="width:100%; height:100%;overflow:hidden;"/>', 
						height:600,
						width:1000,
						_onOpen:function(){
							oc.util.ajax({
								url:oc.resource.getUrl('portal/business/service/get.htm'),
								data:{id:item.bizId},
								success:function(data){
									var svgData = data.data.topology||'{}';
									svgData = eval("(" + svgData + ")");
									var svgJq = dlg.find('.svg');
									canvas= new Canvas(svgJq[0]);
									var param = {ifAdapt:true,id:item.bizId};
									canvas.restore(svgData,param);
									
//									var bg = svgData.bg;
//									bg['bizId'] = data.data.id;
//									if(bg){
//										if(isIeAndVersionLt9()){
//		                    				canvas.setViewBox(0,0,svgJq.width(),svgJq.height());
//		                    			}else{
//		                    				canvas.setViewBox(0,0,bg.w,bg.h);
//		                    			}
//										
//		                    		}
//									adapt(canvas,bg);
									
								}
							});
						},
						onResize:function(){
							if(dlg){
								var svg = dlg.find('.svg').first();
								canvas=svg.canvas();
								var param = {ifAdapt:true,id:item.bizId};
								canvas.restore(svg.data('svgData'),param);
								canvas.isEditable=false;
								hideChdEdit(lane);
							}
						}
					});
				},
				"5":function(item){	//拓扑管理
					var dlg=$('<div/>').dialog({
						title:'拓扑查看',
//						maximizable : true,
						href:oc.resource.getUrl('resource/module/topo/api/simple.jsp?topoId='+item.bizId),
						height:600,
						width:700,
						_onOpen:function(){
						},
						onResize:function(){
						}
					});
				},
				"6":function(item){	//告警管理
					 oc.resource.loadScript('resource/module/alarm-management/js/alarm-detailed-information.js',function(){
						 oc.alarm.detail.inform.open(item.eventID,item.recovered ? 2 : 1,item.sysID);
 	     			});
				},
				"8":function(item){	//配置文件管理
					oc.util.ajax({
			    		url:oc.resource.getUrl('portal/config/device/get.htm'),
			    		data: {id:item.bizId},
			    		failureMsg:'获取配置文件详情失败！',
			    		success:function(result){
			    			if(result.code!=200){
			    				return ;
			    			}
			    			var row = result.data||{};
			    			oc.resource.loadScript('resource/module/config-file/device/js/device.js', function() {
			    			//	oc.module.config.file.device.viewBackupHitoryWin
//			    				oc.module.config.file.device.viewBackupHitoryWin({
//									id: item.bizId,
//									row:row
//									
//								});
			    				oc.module.config.file.device.viewBackupHitoryWin(item.bizId,row);
							});
			    		}
			    	});
				},
				"10":function(item){	//巡检管理
					oc.resource.loadScript('resource/module/inspect-management/js/inspect_report_preview.js', function() {
						oc.module.inspect.report.preview.open({
							id: item.bizId//id是巡检报告的id
						});
					});
				},
				"11":function(item){	//报表管理
					
					oc.resource.loadCss("resource/themes/"+oc.index.getSkin()+"/css/oc.css");
					oc.resource.loadScripts(['resource/third/highcharts/js/highcharts.js','resource/third/highcharts/js/highcharts-more.js','resource/third/highcharts/js/exporting.js','resource/module/report-management/js/resourcesOutlook.js'],function(){
						oc.module.reportmanagement.report.rol.open({reportXmlDataId:item.bizId});
			 		});

				},
				knowledge:function(item){	//知识
					oc.resource.loadCss("resource/third/umeditor/themes/classic/css/umeditor.css");
					oc.resource.loadScripts(['resource/module/knowledge/local/js/knowledgeDetail.js'
					                         ],
					                         function() {
												oc.module.knowledge.local.open({
													type:'view',
													id:item.id
													});
												});
											}
				}
	};
	
	//判断浏览器是否是ie9 以下版本
    function isIeAndVersionLt9 (){
    	var  browser = $.browser;
    	
    	return (browser.msie && parseInt(browser.version) < 9);
    }
	
	//找到泳道，全局引用
	function findLane(canvas,edit){
		var unit,service,app,physics;
		for(var i in canvas.topContainers){
			var con = canvas.topContainers[i];
			if(con.data.type=="unit"){
				unit = con;
			}else if(con.data.type=="service"){
				service = con;
			}else if(con.data.type=="app"){
				app = con;
			}else if(con.data.type=="physics"){
				physics = con;
			}else{
				break;
			}
			con.click = function(){
				if(edit){
					edit.setCon(this);
					//没使用的编辑点
		            var unuse = ["lt","tr","r","rb","bl","l","t"];
		            for(var i=0;i<unuse.length;i++){
		            	edit.P[unuse[i]].hide();
		            }
					edit.edit = function(bbox){
						getLaneEdit(bbox,this,unit,service,app,physics);
					};
				}
			}
			//不让拖动
			con.proxyDrag = function(dx,dy,x,y){}
		}
	}
	function checkEdit(){
	}
	function adapt(canvas,bg){
		var edit = new Edit(canvas);
		var unit,service,app,physics;
		
		for(var i in canvas.topContainers){
			var con = canvas.topContainers[i];
			switch(con.data.type){
				case "unit":
					unit = con;
					break;
				case "service":
					service = con;
					break;
				case "app":
					app = con;
					break;
				case "physics":
					physics = con;
					break;
				default :
					break;
			}
			
			con.click = function(){
				if(edit){
					edit.setCon(this);
					//没使用的编辑点
		            var unuse = ["lt","tr","r","rb","bl","l","t"];
		            for(var i=0;i<unuse.length;i++){
		            	edit.P[unuse[i]].hide();
		            }
					edit.edit = function(bbox){
						getLaneEdit(bbox,this,unit,service,app,physics);
					};
				}
			}
			//不让拖动
			con.proxyDrag = function(dx,dy,x,y){}
		}
		
		//宽度自适应
		var dom_width = $(canvas.dom).width();
		var dom_height = $(canvas.dom).height();
		canvas.raphael.setSize(dom_width,dom_height);
	
		var old_new = null,width = null,textWidthSource = null,textWidth = null;
		if(isIeAndVersionLt9()){
			canvas.raphael.image(bg.src,0,0,dom_width,dom_height).toBack();
			old_new = (physics.y+physics.h)/$(canvas.dom).height();
			width = $(canvas.dom).width();
			textWidth = bg.w*.12;
		}else{
			var ratio = dom_width/dom_height;
			var handled_canvasH = bg.w/ratio;
			//如果根据宽高比得出的height比dom,height高,则按dom,height重新计算宽度
			if(handled_canvasH < bg.h){
				var newWidth = bg.h*ratio;
				canvas.raphael.image(bg.src,0,0,newWidth,bg.h).toBack();
				old_new = 1;
				width = newWidth;
				textWidth = bg.w*.12;
			}else{
				canvas.raphael.image(bg.src,0,0,bg.w,handled_canvasH).toBack();
				old_new = (physics.y+physics.h)/handled_canvasH;
				width = bg.w;
				textWidth = width*.12;
			}
			
		}
		
		expand(edit);
		Container.prototype.dblclick = function(){};
		
		unit.click();
		edit.edit({x:textWidth,y:1,x1:width,y1:unit.h/old_new});
		service.click();
		edit.edit({x:textWidth,y:unit.y+unit.h,x1:width,y1:unit.y+unit.h+service.h/old_new});
		app.click();
		edit.edit({x:textWidth,y:service.y+service.h,x1:width,y1:service.y+service.h+app.h/old_new});
		physics.click();
		edit.edit({x:textWidth,y:app.y+app.h,x1:width,y1:app.y+app.h+physics.h/old_new});
		edit.hide();
		
		unit.click = function(){}
		service.click = function(){}
		app.click = function(){}
		physics.click = function(){}
		
		hideChdEdit(unit);
		hideChdEdit(service);
		hideChdEdit(app);
		hideChdEdit(physics);
	}
//	function adapt(canvas,bg){
//		var edit = new Edit(canvas);
//		var unit,service,app,physics;
//		for(var i in canvas.topContainers){
//			var con = canvas.topContainers[i];
//			switch(con.data.type){
//				case "unit":
//					unit = con;
//					break;
//				case "service":
//					service = con;
//					break;
//				case "app":
//					app = con;
//					break;
//				case "physics":
//					physics = con;
//					break;
//				default :
//					break;
//			}
//		}
//		
//		//宽度自适应
//		canvas.raphael.setSize($(canvas.dom).width(),$(canvas.dom).height());
//		//canvas.raphael.setSize(bg.w,bg.h);
//		var old_new = (physics.y+physics.h)/$(canvas.dom).height(),
//			width = $(canvas.dom).width();
//			var textWidthSource = bg.w*.12;
//			textWidth = textWidthSource;//width*.17;
//		expand(edit);
//		findLane(canvas,edit);
//		
//		unit.click();
//		edit.edit({x:textWidth,y:1,x1:width,y1:unit.h/old_new});
//		service.click();
//		edit.edit({x:textWidth,y:unit.y+unit.h,x1:width,y1:unit.y+unit.h+service.h/old_new});
//		app.click();
//		edit.edit({x:textWidth,y:service.y+service.h,x1:width,y1:service.y+service.h+app.h/old_new});
//		physics.click();
//		edit.edit({x:textWidth,y:app.y+app.h,x1:width,y1:app.y+app.h+physics.h/old_new});
//		edit.hide();
//		
//		hideChdEdit(unit);
//		hideChdEdit(service);
//		hideChdEdit(app);
//		hideChdEdit(physics);
//	}
	
	/**
		隐藏子容器编辑框，重写子容器拖拽函数
		params C 单指泳道
	**/
	function hideChdEdit(C){
		if(C != null && C.containers!= null){
			for(var chd in C.containers){
				var con = C.C.containers[chd];
				con.proxyDrag = function(dx,dy,x,y){};
			}
		}
	}
	
	oc.ns('oc.simple.engineer.workbench.search');
	
	oc.simple.engineer.workbench.search.open = function(args){
		return new Search(args)._open();
	};
})(jQuery);