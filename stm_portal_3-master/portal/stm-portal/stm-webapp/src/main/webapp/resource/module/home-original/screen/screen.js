
(function(){
	var newValue="",level=0;
	oc.util.ajax({
		url:oc.resource.getUrl('home/screen/getScreenSetData.htm'),
		async:false,
		success:function(d){
			d=d.data;
			userData=d.user;
			$.each(userData,function(i,item){
				if(item.bizType==4){
					newValue=item.bizId;
					if(newValue==9){
						newValue=0;
					}
					level=item.showlevel;
				}
			});
		}
	});
	var svgArray = [];
	//存放左侧dom宽高，因全屏后会影响左侧dom高度，故将其数据保存下来
	var old_dom = {} , full_dom = {};
	oc.ns('oc.home.workbench');
	Highcharts.setOptions(Highcharts.theme);
	oc.home.workbench.userWorkbenches=oc.home.workbench.userWorkbenches||{};
	var id=oc.util.generateId(),bigScreen=$('#oc-home-screen').attr('id',id),
		content=bigScreen.find('div.w-full-content:first'),
		topoCon = content.find('.topo'),
		topoMapCon = content.find('.topomap'),
		workbenchCon=topoCon.next(),
		workbenchMapCon=topoMapCon.next(),
		//记录上次全屏状态
		lastFullScreen=false,
		nodataCon=content.find('.oc_home_nodata').hide(),	//初始状态为隐藏
		moduleLocate=bigScreen.find('div.module-locate:first'),
		layout=moduleLocate.find('div.easyui-layout:first').layout(),
		picDom='<div class="layout-screen-pic" style="display:none;"><img/></div>',
		updateUserBizRelUrl=oc.resource.getUrl('home/screen/updateUserBizRel.htm'),
		hiddenDiv=0,
		moduleLocate_height = moduleLocate.height(),//记录全屏前center部分
		maxmin=bigScreen.find('span.ico-max').click(function(){	//注册【全屏按钮】点击事件
			$("#isLoad").val(false);	//地图拓扑特殊处理（╮(╯▽╰)╭）
			var _self=$(this),isMax=_self.is('.ico-max');
			if(isMax){//变为全屏
				lastFullScreen = true;
				oc.index.body.append(moduleLocate.width('100%').height(window.screen.height));
				hiddenDiv=moduleLocate.siblings('div:not(:hidden)').hide();
				maxmin.removeClass('ico-max').addClass('ico-narro').attr('title','点我退出全屏模式');	//设为窄屏
				
				//加if判断主要防止下面方法对业务绘图的内容产生干扰,add by tandl
				if(!$(this).attr("type") || $(this).attr("type") != 'biz')
					oc.util.fullScreen(document.documentElement);
				var h=$('.module-locate.easyui-layout.layout.easyui-fluid').next();
			
				var fullWidth = moduleLocate.width();
				var fullheight = moduleLocate.height();
				if(!full_dom.w || full_dom.w<fullWidth){
					full_dom.w = fullWidth;
				}
				if(!full_dom.h || full_dom.h<fullheight){
					full_dom.h = fullheight;
				}
				if(navigator.appName=='Microsoft Internet Explorer'){
					oc.index.indexLayout.layout();
				}
				
				// 从window.resize事件里面移过来 start
				$('body').keyup(function(e){
					var code = e.keyCode ? e.keyCode : e.which;
				    if(code == 27 ) {	//Esc键
				    	hiddenDiv.show();
				    	bigScreen.prepend(moduleLocate.width('80%'));
				    	maxmin.removeClass('ico-narro').addClass('ico-max').attr('title','点我进入全屏模式');
				    	oc.index.indexLayout.layout();
				    	lastFullScreen = false;	
				    }
				});
				// 从window.resize事件里面移过来 end
				
			}else{//还原为窄屏
				oc.util.cancelFullScreen();	//取消全屏
				// 从window.onresize事件里面移过来 取消全屏 start
				hiddenDiv.show();
				bigScreen.prepend(moduleLocate.width('80%').height(moduleLocate_height));
				maxmin.removeClass('ico-narro').addClass('ico-max').attr('title','点我进入全屏模式');
//				oc.index.indexLayout.layout();
				lastFullScreen = false;
				/*for(var i = 0 ; i < pics.length ; i ++){
					var d = pics[i].data('biz-data');
					 旧业务大屏相关，不适用于自4.2.1及后续版本新业务模块
					 * 
					 * if(d.bizType == 1 && d.id != currentPic.data('biz-data').id){
						//业务服务
						d = indexData(d);
						$(pics[i]).html('');
						if(d != undefined){
							var svg = oc.ui.svg({
								selector : pics[i],
								data : d.bizType == 1 ? d.thumbnail : d.content,
								type : d.bizType == 1 ? 'biz' : 'topo',
								id : d.bizId,
								isEditable : true,
								isMax : false
							});
							_screen.cachSvg(svg);
						}
					}
				}*/
				// 从window.onresize事件里面移过来 取消全屏 end
			}
			layout.layout();
			
			// 如果用户点击退出全屏再点击退出全屏按钮则不会刷新大屏内加载的内容所以这里和window.resize里面相同的内容
			windowResize();
		}),
		imageNav=bigScreen.find('div.pic_zzjs:first'),
		up=imageNav.prev(),
		down=imageNav.next(),
		renderBizDom=0,
		pics=[],
		currentPic=0,
		picsL=0,
		showFirst=-1,
		showLast=4,
		showLen=4,
		bizData=0,//所有业务列表
		topData=0,//所有top列表
		selectTopDiv=maxmin.prev(),
		cycleExecute=0,
		setWorkbench=function(pic,workbenchId,sort,ext){
			pic.attr('data-workbench-id',workbenchId)
				.attr('data-workbench-sort',sort)
				.attr('data-workbench-ext',ext)
				.attr('data-workbench-title','screen');
		},
		autoRun=function(){
			cycleExecute=setInterval(function(){
				if($('#'+id).length==0){
					clearInterval(cycleExecute);
					return;
				}

				var data=currentPic.data('biz-data'),idx=pics.indexOf(function(o){
				
					return o.data('biz-data').sort==data.sort;
					
				});
				if(data.bizType!=3){
					selectTopCombobox.jq.combobox('hidePanel');
				}
				
				if(idx==(pics.length-1)){
					showFirst=-1;
					showLast=4;
					show();
				}else if(idx%showLen==3){
					down.click();
				}else{
					pics[idx+1].click();
				}
			
			},30*1000);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(cycleExecute);
			}else{
				tasks = new Array();
				tasks.push(cycleExecute);
				oc.index.indexLayout.data("tasks", tasks);
			}
		},
		runBtn=bigScreen.find('.runBtn').click(function(){	//注册轮播按钮事件
			if(runBtn.is('.start')){
				runBtn.removeClass('start').addClass('stop');
				autoRun();
			
			}else{
				
				runBtn.removeClass('stop').addClass('start');
				cycleExecute&&clearInterval(cycleExecute);
				cycleExecute=0;
			}
		}),

		indexData=function(d){
			//业务类型 1-业务管理；2-拓扑管理 3-首页工作台
			if(d.bizType==1){
				return bizData[bizData.indexOf(function(o){
					return o.bizId==d.bizId;
				})];
			}else if(d.bizType==2){
				return topData[topData.indexOf(function(o){
					return o.bizId==d.bizId;
				})];
			}else{
				return workbenches[workbenches.indexOf(function(o){
					return o.id==d.bizId;
				})];
			}
		},
		updateSet=function(d,fn){
			oc.util.ajax({
				url:updateUserBizRelUrl,
				data:d,
				success:function(){
					fn&&fn(d,currentPic);
				}
			});
		},
		//TODO:地图拓扑下拉框
		selectCombotree=selectTopDiv.find('select.selectTop').combotree({
			  width:'200px',
			  value:newValue,
			  url:oc.resource.getUrl("resource/module/map/json/chinaTree.json"),
			  onSelect:function(node){
				  eve("topo.map.sreen.tomap",this,node.id,node.level);
				  var value=$("#screenId").attr("value");
					oc.util.ajax({
						url:oc.resource.getUrl('home/screen/updateUserBizRelSelect.htm'),
						data:{bizid:node.id,level:node.level,id:value,title:node.text},
						success:function(data){
							var d=currentPic.data('biz-data');
							renderCurrentPic(d);
//							currentPic.click();
						}
					});
			  },
			  onChange : function(nv, oldValue){
			  }
		}),

		selectTopCombobox=oc.ui.combobox({
			selector:selectTopDiv.find('select.selectTopMap'),
			valueField:'bizId',
			textField:'title',
			onChange:function(n,o){
				var d=currentPic.data('biz-data');
				if((n||n==0)&&(n!=d.bizId)){
					d.bizId=n;
					d.title=indexData(d).title;
					updateSet(d,function(data,pic){
						renderBizDom(data,pic);
						currentPic.click();
						_screen.init(svgArray);
					});
				}
			},
			onSelect:function(){
			}
		}),
		titleSpan=selectTopDiv.prev(),
		generateDom=function(ds){
			imageNav.children().remove();
			showFirst=-1;
			showLast=4;
			pics.length=0;
			ds=$.isArray(ds)?ds:[];
			picsL=ds.length;
			if(picsL>0){
				nodataCon.hide();
				for(var i=0;i<picsL;i++){
					
					renderBizDom(ds[i]);
				}
			}else{
				nodataCon.show().siblings().hide();
				if(oc.isSimple)nodataCon.html('<div class="seach_nodata_img"></div><p>首页大屏设置内容</p>');
				selectTopDiv.hide();
			}
			
		},
		registerClick=function(){
		
			up.addClass('disable').off('click');
			down.addClass('disable').off('click');
			if(showFirst!=-1){
				up.removeClass('disable').click(function(){
					showFirst-=showLen;
					showLast-=showLen;
					show();
				});
			}
			
			if(showLast<picsL){
				down.removeClass('disable').click(function(){
					showFirst+=showLen;
					showLast+=showLen;
					show();
				});
			}
		},
		show=function(){
			for(var i=0,pic;i<picsL;i++){
				pic=pics[i];
				if(i>showFirst&&i<showLast){
					pic.fadeIn();
				}else{
					pic.hide();
				}
			}
			if(pics.length>0)pics[showFirst+1].click();
			registerClick();
		};
	renderPic=function(pic,screenD,bizD){
		var d=bizD||screenD;
		pic.empty().attr('title',d.title).data('biz-data',screenD);
		if(screenD.bizType==3){//工作台
			d=screenD;
			if(Highcharts.theme.currentSkin=='default'){
				pic.append('<img src="'+oc.resource.getUrl('resource/themes/default/images/comm/table/'+
						(d.bizId==1?'topn':d.bizId==7?'alarm-list':'attention-resource')+'.png"></img>'));
			}else{
				pic.append('<img src="'+oc.resource.getUrl('resource/themes/blue/images/comm/table/'+
						(d.bizId==1?'topn':d.bizId==7?'alarm-list':'attention-resource')+'.png"></img>'));
			}
			
			//console.log(Highcharts.);
		}else if(screenD.bizType==4){
			if(Highcharts.theme.currentSkin=='default'){
				pic.append('<img src="'+oc.resource.getUrl('resource/themes/default/images/comm/table/top-map.png"></img>'));
			}else{
				pic.append('<img src="'+oc.resource.getUrl('resource/themes/blue/images/comm/table/top-map.png"></img>'));
			}
		
			
		}else if(screenD.bizType==5){
			if(Highcharts.theme.currentSkin=='default'){
				pic.append('<img src="'+oc.resource.getUrl('resource/themes/default/images/comm/table/biz-summary.png"></img>'));
			}else{
				pic.append('<img src="'+oc.resource.getUrl('resource/themes/blue/images/comm/table/biz-summary.png"></img>'));
			}
		
			
		}else{//业务、拓扑
			if(d.bizId){
				//$(pic).append("<div style='width:150px;height:110px;'></div>")
//				var svg = oc.ui.svg({
//					selector: pic,
//					data:d.bizType==1?d.thumbnail:d.content,
//					type:d.bizType==1?'biz':'topo',
//					id:d.bizId,
//					isEditable:true,
//					isMax:false
//					});
//				_screen.cachSvg(svg);
				if(d.bizType==1){//业务
					if(Highcharts.theme.currentSkin=='default'){
						pic.append('<img src="'+oc.resource.getUrl('resource/themes/default/images/comm/table/biz_canvas.png"></img>'));
					}else{
						pic.append('<img src="'+oc.resource.getUrl('resource/themes/blue/images/comm/table/biz_canvas.png"></img>'));
					}
				}else{
					var svg = oc.ui.svg({
						selector: pic,
						data:d.bizType==1?d.thumbnail:d.content,
						type:d.bizType==1?'biz':'topo',
						id:d.bizId,
						isEditable:true,
						isMax:false
						});
					_screen.cachSvg(svg);
				}
			}else{
				pic.append('<img src="'+oc.resource.getCss('images/comm/table/'+
					(d.bizType==1?'ywst':'top_picter')+'.png"></img>'));
			}
		}
	},
	renderCurrentPic=function(screenD){
		var indexD=indexData(screenD)||screenD;
		currentPic.addClass('active').siblings().removeClass('active');
//		topoCon.css('display','none').children().remove();
//		workbenchCon.css('display','none').children().remove();
		titleSpan.text(indexD.title);

		if(screenD.bizType==3){
			topoCon.css('display','none');
			selectTopDiv.hide();
			/* titleSpan.hide(); */
			setWorkbench(workbenchCon,screenD.bizId,screenD.sort,screenD.selfExt);
			workbenchCon.css('display','block').load(oc.resource.getUrl(indexD.url),function(){
				var layout=workbenchCon.find('.module');
				layout.find('.ico-cancel:first').hide();
				if(oc.isSimple)layout.find('.r-set').hide();
				layout.layout();
				oc.home.workbench.userWorkbenches[screenD.sort].reLoad(10);
			});
		}else if(screenD.bizType==5){
			topoCon.css('display','none');
			selectTopDiv.hide();
			/* titleSpan.hide(); */
			setWorkbench(workbenchCon,screenD.bizId,screenD.sort,screenD.selfExt);
			workbenchCon.css('display','block').load(oc.resource.getUrl('resource/module/home/workbench/biz_summary_model.html'),function(){
				var layout=workbenchCon.find('.module');
				layout.find('.ico-cancel:first').hide();
				if(oc.isSimple)layout.find('.r-set').hide();
				layout.layout();
				oc.home.workbench.userWorkbenches[screenD.sort].reLoad(10);
			});
		}else{
			var type='topo';
			if(screenD.bizType==1){
				type='biz';
			}else if(screenD.bizType==2){
				type="topo";
			}else if(screenD.bizType==4){
				type="map";
			}

			bigScreen.find('span.ico-max').attr("type",type);
			
			titleSpan.show();
			selectTopDiv.show();	//地图拓扑的下拉框显示
			if(screenD.bizType==1){
				
				topoMapCon.css('display','none').children().remove();
				workbenchMapCon.css('display','none').children().remove();
				_screen.cereateCom(screenD.bizType);
				selectTopCombobox.load(bizData);
			}else if(screenD.bizType==4){
				var d=currentPic.data('biz-data');
				//TODO:地图拓扑大屏
				_screen.cereateCom(screenD.bizType);
				oc.util.ajax({
				url:oc.resource.getUrl('home/screen/getBizByID.htm'),
				data:{id:screenD.id},
				success:function(data){
					if("false" == $("#isLoad").val() || !$("#isLoad").val()){
						$("#isLoad").val(true);
						toMap(data.data.bizId,1);
					}
				}
			});
			//	toMap(screenD.bizId,1);//
				
				
//				console.info(screenD);
//				
//				console.debug($("#oc-home-screen"));
//				if($("#oc-home-screen").find('#selectTopMap') &&　$("#oc-home-screen").find('#selectTopMap').length > 0){
//					$("#oc-home-screen").find('#selectTopMap').combotree('setValue',screenD.bizId);
//				}
		
			}else{
				_screen.cereateCom(screenD.bizType);
				selectTopCombobox.load(topData);
			}
			if(screenD.bizId||screenD.bizId==0){
				selectTopCombobox.jq.combobox('setValue',screenD.bizId);
//				topoCon.css('display','block');
				if(indexD.title=='地图拓扑'){//地图拓扑
					topoCon.css('display','none');
					topoMapCon.css('display','block');
					var svg = oc.ui.svg({
						selector:topoMapCon.empty(),
						showText:true,
						data:indexD.content,
						type:type,
						id:indexD.bizId
					});
					if(!old_dom.w && !old_dom.h){
						old_dom.w = topoMapCon.width();
						old_dom.h = topoMapCon.height();
					}
					
					_screen.cachSvg(svg);
				}else{//其他
					topoCon.css('display','block');
					topoMapCon.css('display','none');
//					console.info(indexD.title);
//					if(indexD.content  ){
//						console.info(type);
						if(type == 'biz'){
							topoCon.empty().load(oc.resource.getUrl('resource/module/business-service/buz/business.html'),function(){

								oc.resource.loadScript('resource/module/business-service/buz/business.js',
				                        function(){
				                            new businessTopo({
				                                bizId:indexD.bizId,
				                                showControlPanel:false,
			                            });
			                    });
								
							});
						}else{
							var svg = oc.ui.svg({
								selector:topoCon.empty(),
								showText:true,
								data:indexD.content,
								type:type,
								id:indexD.bizId
							});
							if(!old_dom.w && !old_dom.h){
								old_dom.w = topoCon.width();
								old_dom.h = topoCon.height();
							}
							
							_screen.cachSvg(svg);
						}
					}	
//				}

			}else{
				if(indexD.bizType==1){
				
					topoCon.append('<div class="emptyContent">请设置要显示的业务视图</div>');
				}else{
					topoCon.append('<div class="emptyContent">请设置要显示的TOP视图</div>');
				}
			}
			if(oc.isSimple)selectTopCombobox.jq.combobox('disable');
		}
	},
	toMap=function(key,level){//TODO:comboboxtree需要重新渲染值
		if(key==9){
			key=0;
		}
		selectCombotree=selectTopDiv.find('select.selectTop').combotree({
			  width:'200px',
			  value:key,
			  url:oc.resource.getUrl("resource/module/map/json/chinaTree.json"),
			  onSelect:function(node){
				  eve("topo.map.sreen.tomap",this,node.id,node.level);
				  var value=$("#screenId").attr("value");
					oc.util.ajax({
						url:oc.resource.getUrl('home/screen/updateUserBizRelSelect.htm'),
						data:{bizid:node.id,level:node.level,id:value,title:node.text},
						success:function(data){
					
							currentPic.click();
						}
					});
			  },
			  onChange : function(nv, oldValue){
			  }
		})
		//如果newValue不为空，自动跳转地图拓扑
		if(!isNaN(key) && !isNaN(level)){
			var flag={flag:false};
			eve("topo.map.sreen.tomap",this,key,level,flag);
//			var tid = setInterval(function(){
//				eve("topo.map.sreen.tomap",this,key,level,flag);
//				if(flag.flag){
//					clearInterval(tid);
//				}
//			},500);
//			var tasks = oc.index.indexLayout.data("tasks");
//			if(tasks && tasks.length > 0){
//				oc.index.indexLayout.data("tasks").push(tid);
//			}else{
//				tasks = new Array();
//				tasks.push(tid);
//				oc.index.indexLayout.data("tasks", tasks);
//			}
		}
	},
	renderBizDom=function(d,pic){
		if(!pic){
			pics.push(pic=$(picDom));
			imageNav.append(pic.click(function(){
				currentPic=pic;
				renderCurrentPic(d);
				$("#screenId").val(d.id);	
			}));
		}
		renderPic(pic,d,indexData(d));
		return pic;
	};
//	topoCon.width(oc.index.body.width());
//	topoCon.height(oc.index.body.height());
//	topoCon.width(content.find('.topo').width());
//	topoCon.height(content.find('.topo').height());
	
	oc.ns('oc.home.screen');
	
	oc.home.workbench.setExt=function(workbenchId,sort,ext,fn,module){
		var d=currentPic.data('biz-data');
		d.selfExt=ext;
		updateSet(d,function(data,pic){
			fn&&fn(data);
			if(module){
				workbenchCon.attr('data-workbench-ext',ext);
			}
		});
	};
	var workbenches;
	oc.util.ajax({
		url:oc.resource.getUrl('home/workbench/main/getAllWorkbenchs.htm'),
		successMsg:null,
		async:false,
		success:function(d){
			workbenches=d.data;
		}
	});
	oc.home.screen.reLoad=function(){
		oc.util.ajax({
			url:oc.resource.getUrl('home/screen/getScreenSetData.htm'),
			success:function(d){
				d=d.data;
				topData=d.top;
				bizData=d.biz;
				for(var i=0,len=d.user.length,u;i<len;i++){
					u=d.user[i];
				}
				generateDom(d.user);
				
				if(d.user.length>0)oc.home.workbench.domainId=d.user[0].domainId;
				show();
				
				
				_screen.init(svgArray);
			}
		});
	};
	oc.home.screen.reLoad();
//	toMap(newValue,level);
	
	var _screen = {
			
		/**
		 * 初始化开始准备做画布适应
		 * */
		init:function(array){
			if(array != null && array.length >0){
				var data=currentPic.data('biz-data');
				for(var i  in array){
					var _svg = array[i];
					if(_svg.cas ){
						_svg.cas.editable = false;
						if( _screen.isIeAndVersionLt9()){
							//_svg.cas.setViewBox(0,0,760,560);
							//_svg.cas.setViewBox(0,0,380,145);
							_svg.cas.raphael.setSize($(_svg.cas.dom).width() ,$(_svg.cas.dom).height());
						}else{
							//_screen.autoAdapt(_svg.cas,_svg);
							_svg.cas.adapt({});
						}
						
					}
				}
				_screen.adaptFont();
			}
		},
		 //判断浏览器是否是ie9 以下版本
        isIeAndVersionLt9 : function (){
        	var  browser = $.browser;
        	return (browser.msie && parseInt(browser.version) < 9);
        },
		/***自适应快照尺寸*/
	    autoAdapt : function(canvas,svg){
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
    					edit.hide();
    					break;
    			}
    		}
    		var _dom = $(canvas.dom)
    		
    		var dom_height = unit.w / (_dom.width()/_dom.height());
    	
    		var old_new = (physics.y+physics.h)/dom_height,width = unit.w ;
    		
    		var textWidth = 0;
    		if(svg.cfg.isMax){
    			textWidth = canvas.raphael.width*.12;
    			width = _dom.width();
    			old_new = (physics.y+physics.h)/_dom.height();
    			//canvas.setViewBox(0,0,width + 100,dom_height  );
    		}else{
    			canvas.setViewBox(0,0,unit.w + 100,dom_height);
    			textWidth = canvas.raphael.width + 20;
    		}
    		
    		_screen.findLane(canvas,_screen.getLaneEdit,edit,svg.cfg.isMax);
    	
    		unit.click();
    		
    		edit.edit({x:textWidth,y:1,x1:width,y1:unit.h/old_new});
    		
    		
    		service.click();
    		edit.edit({x:textWidth,y:unit.y+unit.h,x1:width,y1:unit.y+unit.h+service.h/old_new});
    		
    		app.click();
    		edit.edit({x:textWidth,y:service.y+service.h,x1:width,y1:service.y+service.h+app.h/old_new});
    		
    		physics.click();
    		edit.edit({x:textWidth,y:app.y+app.h,x1:width,y1:app.y+app.h+physics.h/old_new});
    		edit.hide();
    		
        },
       
        /**
         * 找到泳道，全局引用
         */
		findLane : function(canvas,fn,edit,isMax){
			
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
							fn(bbox,this,unit,service,app,physics,isMax);
						};
					}
				}
				//不让拖动
				con.proxyDrag = function(dx,dy,x,y){}
			}
		},
		//字体适应..有点小问题暂时不使用
		adaptFont:function (canvas){
			 if($.browser.safari){  //苹果+chromen Webkit内核
                 $(".pic_zzjs text").each(function(){
                     var tspan = $(this).find("tspan");
                     var html = tspan.html().trim();
                     if(html != "业务应用" && html != "业务单位" && html != "业务服务" && html != "支撑层" && html != "应用层" && html != "单位层" && html != "服务层" ){
                         tspan.html("...");
                     }else{
                         $(this).attr("transform","translate(-10,0)");
                         tspan.html(tspan.html().replace("业务应用","应用层").replace("业务单位","单位层").replace("业务服务","服务层"));
                     }
                 })
             }

		},
		
		getLaneEdit : function(bbox,_this,unit,service,app,physics,_isMax){
			_this.con.texts[0].ry = (bbox.y1-bbox.y)/2 ;
			if(!_isMax){
//						_this.con.texts[0].attr({"font-size":"6px"});
//						var o = _this.con.texts[0];
//						o.attr({x:bbox.x1+o.rx,y:bbox.y1+o.ry});
//						_this.con.texts[0].remove();	
			}
			_this.con.rects[0].attr({height:bbox.y1-bbox.y,width:bbox.x1});
			_this.con.rects[1].attr({height:bbox.y1-bbox.y});
			_this.con.setBBox(bbox);
			unit.drag(0,0,unit.x,unit.y);
			service.drag(0,0,service.x,unit.y+unit.h);
			app.drag(0,0,app.x,service.y+service.h);
			physics.drag(0,0,physics.x,app.y+app.h);
		},
		cereateCom :function(id){
		//	selectTopDiv.find('select').remove();
			if(id==4){
				selectTopDiv.find('span').each(function(i,item){
					if(i>=2){
						$(this).hide();
					}else{
						$(this).show();
					}
				});
				//selectTopDiv.find('select')
			
			}else{
				selectTopDiv.find('span').each(function(i,item){
					if(i>=2){
						$(this).show();
					}else{
						$(this).hide();
					}
					/*if(i<2){
						$(this).hide();
					}else{
						$(this).show();
					}*/
				});
			}
			
			
		},
		resize:function(){
			var has_zoom_in = bigScreen.find('span.ico-max').is('.ico-max');
			if(svgArray){
				for(var i in svgArray){
					var _svg = svgArray[i];
					if( _screen.isIeAndVersionLt9()){
						
					}else{
						if(_svg.cfg.isMax){
							if(has_zoom_in){
								topoCon.height(old_dom.h);
							}else{
								topoCon.height( content.find('.topo').height() + old_dom.diff);
								if(!old_dom.diff){
									old_dom.diff = topoCon.height() - old_dom.h;
								}
							}
							_svg.cas.setSize(topoCon.width(),topoCon.height());
							_screen.init([_svg]);
						}
					}
				}
			}
		},
		cachSvg : function (svg){
			if(svg !=null){
				svgArray.push(svg);
			}
		}
	}
	
	windowResize = function(){
		//_screen.resize();
		var topo = moduleLocate.find('.topo');
		var winHeight = window.innerHeight;
		var winWidth = window.innerWidth;
		
		if(lastFullScreen){
			topo.width('100%');
			//topo.height(winHeight-80);
			topo.height(winHeight-62);
		}else{
			topo.width(old_dom.w);topo.height(old_dom.h);
		}
		
		var data=currentPic.data('biz-data');
		renderCurrentPic(data);
		
		if(svgArray != null && svgArray.length >0){
			for(var i  in svgArray){
				var _svg = svgArray[i];
				if(_svg.cas && data.bizId && data.bizId == _svg.cfg.id){
					_screen.init([_svg]);
				}
			}
		}
	}
	window.onresize = function (){
		/**  这个if判断会有问题  把相应的操作放到按钮事件点击里面
		if((window.outerHeight!=screen.height || window.outerWidth!=screen.width) && lastFullScreen ){
			if(window.outerWidth<screen.width ){
				$('body').keyup(function(e){
				    var code = e.keyCode ? e.keyCode : e.which;
				    if(code == 27 ) {
				    		hiddenDiv.show();
							bigScreen.prepend(moduleLocate.width('80%'));
							maxmin.removeClass('ico-narro').addClass('ico-max').attr('title','点我进入全屏模式');
							oc.index.indexLayout.layout();
							lastFullScreen = false;	
				    
				    }
				  });
			}else{
				//取消全屏
				hiddenDiv.show();
				bigScreen.prepend(moduleLocate.width('80%'));
				maxmin.removeClass('ico-narro').addClass('ico-max').attr('title','点我进入全屏模式');
				oc.index.indexLayout.layout();
				lastFullScreen = false;
				for(var i = 0 ; i < pics.length ; i ++){
					var d = pics[i].data('biz-data');
					if(d.bizType == 1 && d.id != currentPic.data('biz-data').id){
						//业务服务
						d = indexData(d);
						$(pics[i]).html('');
						var svg = oc.ui.svg({
							selector: pics[i],
							data:d.bizType==1?d.thumbnail:d.content,
									type:d.bizType==1?'biz':'topo',
											id:d.bizId,
											isEditable:true,
											isMax:false
						});
						_screen.cachSvg(svg);
					}
				}
			}
	    }
		*/
		windowResize();
	}

	
	
})();
//# sourceURL=screen.js