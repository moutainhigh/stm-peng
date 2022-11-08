(function(){
	Highcharts.setOptions(Highcharts.theme);
	
	oc.ns('oc.home.workbench.user');
	oc.ns("oc.home.highchart");
	oc.home.workbench.userWorkbenches={};
	oc.home.workbench.user.ready='';
	//工作台初始化部分   begin ******************
	var id=oc.util.generateId(),workbenchDiv=$('#oc-home-workbench').attr('id',id),
	content=workbenchDiv.find('.content:first > .panels'),
	pageDiv=workbenchDiv.find('div.d-page:first');
	function workbenchPage(){}
	workbenchPage.prototype={
		constructor:workbenchPage,
		dom:{
			page_a:'<a class="d-page-ico d-page-dot"></a>',
			panel:'<div class="panels" style="width:100%;height:100%;"/>',
			module:'<div class="oc-layout-panel" style="display:none;"></div>'
		},
		activeIdx:0,//当前激活面板索引
		modules:[],//缓存用户设置数据，module属性值为要渲染的页面
		initModules:function(){
			$.messager.progress();
			pageDiv.children().remove();
			content.children().remove();
			var modules=this.modules,that=this,firstPage=0,loadedCount=0,len=modules.length;
			if(modules.length>0)oc.home.workbench.domainId=modules[0].domainId;
			for(var i=0,pageA=0,d,pDom=this.dom.page_a,mDom=this.dom.module;
				i<len;i++){
				d=modules[i];
				
				//分页按钮
				if(i%4==0)pageA=$(pDom).attr('idx',i/4).appendTo(pageDiv).click(function(){
					var self=$(this);
					that.activeIdx=$(this).attr('idx');
					self.removeClass('d-page-dot').addClass('d-page-greendot')
					.siblings().removeClass('d-page-greendot').addClass('d-page-dot');;
					that.drawPage();
				});
				if(i==0)firstPage=pageA;
				//模块初始化
				(function(selfData){
					var module=selfData.module=$(mDom).attr('data-workbench-id',selfData.workbenchId)
						.attr('data-workbench-sort',selfData.sort).attr('data-workbench-ext',selfData.selfExt).attr('data-workbench-ext1',selfData.selfExt1);
					content.append(module);
					module.load(oc.resource.getUrl(selfData.url),function(){
						var layout=module.find('.module:first').layout();
						layout.find('span.ico-cancel:first').click(function(){
							that.delModule(selfData);
						});
						loadedCount++;
					});
				})(d);
			}
			
			if(firstPage){
				var tempTimeout=setInterval(function(){
					if(loadedCount==4||loadedCount==modules.length){
						$.messager.progress('close');
						clearInterval(tempTimeout);
						firstPage.click();
					}
				},200);
				var tasks = oc.index.indexLayout.data("tasks");
				if(tasks && tasks.length > 0){
					oc.index.indexLayout.data("tasks").push(tempTimeout);
				}else{
					tasks = new Array();
					tasks.push(tempTimeout);
					oc.index.indexLayout.data("tasks", tasks);
				}
			}else{
				that.drawPage();
				$.messager.progress('close');
			}
		},
		drawModule:function(module){
			if(oc.home.workbench.userWorkbenches[module.sort]){
				if(module.loaded){
					oc.home.workbench.userWorkbenches[module.sort].render();
				}else{
					oc.home.workbench.userWorkbenches[module.sort].reLoad();
					module.loaded=true;
				}
			}
		},
		cycleExecute:0,
		cycleTime:90*1000,
		drawPage:function(){
			if(this.cycleExecute){
				clearInterval(this.cycleExecute);
			}
			var idx=this.activeIdx,modules=this.modules;
			idx=idx*4;
			if(idx<0)idx=0;
			if(idx>modules.length)idx=modules.length-1;
			var begin=idx,end=((idx+4)<modules.length)?(idx+4):(modules.length);
			if(modules.length>0){
				for(var i=0,len=modules.length,module;i<len;i++){
					module=modules[i];
					if(i>=begin&&i<end){
						module.module.fadeIn().find('.module:first').layout();
						this.drawModule(module);
					}else{
						module.module.hide();
					}
				}
				var startIdx=0,contentLen;
				this.cycleExecute=setInterval(function(){
					contentLen=$('#'+id).length;
					if(contentLen){
						modules[begin+startIdx]&&oc.home.workbench.userWorkbenches[modules[begin+startIdx].sort].reLoad();
						startIdx++;
						if(startIdx>=end)startIdx=0;
					}else{
						if(this.cycleExecute){
							clearInterval(this.cycleExecute);
						}
					}
				},this.cycleTime);
				var tasks = oc.index.indexLayout.data("tasks");
				if(tasks && tasks.length > 0){
					oc.index.indexLayout.data("tasks").push(this.cycleExecute);
				}else{
					tasks = new Array();
					tasks.push(this.cycleExecute);
					oc.index.indexLayout.data("tasks", tasks);
				}
			}else{
				content.html('<div class="oc_home_nodata"></div>');
			}
		},
		delModule:function(module){
			//console.info($(".messager-button span"));
			var t=this,modules=t.modules;
			function delPagePoint(modules, index){
				var len = modules.length,
				page = parseInt(len/4),
				activeBtn = pageDiv.children().filter(".d-page-greendot");
				activePage = parseInt(activeBtn.attr("idx"));
				if(len!=0&&len%4==0){
					if(page==activePage){
						activeBtn.prev().trigger("click");
					}
					pageDiv.children().last().remove();
				}
			}
			oc.ui.confirm('是否确认删除该面板？',function(){
			
				oc.util.ajax({
					url:oc.resource.getUrl('home/workbench/main/delSingleUserWorkbench.htm'),
					data:{
						workbenchId:module.workbenchId,
						sort:module.sort
					},
					success:function(d){
						if(d.data>0){
							var idx=modules.indexOf(function(o){
								return o.sort==module.sort;
							});
							module.module.remove();
							modules.splice(idx, 1);
							t.drawPage();	//重绘当前工作台
							delPagePoint(modules, idx);	//删除分页标签
						}else{
							alert('删除失败!');
						}
					}
				});
			});
		},
		load:function(){
			var _self=this;
			oc.util.ajax({
				url:oc.resource.getUrl('home/workbench/main/getUserWorkbenchs.htm'),
				success:function(ds){
					_self.modules=ds=$.isArray(ds.data)?ds.data:[];
					_self.initModules();
				}
			});
		}
	};
	/*var workbenchObj=new workbenchPage();
	workbenchObj.load();
	oc.home.workbench.loadWorkbench=function(){
		workbenchObj.load();
	};
	
	oc.util.whileOne(window,'resize.homeWorkbench', 500,function(){
		workbenchObj.drawPage();
	});//*/
	
	//工作台初始化部分   end ******************
	function homeSolidgauge(cfg){
		if(cfg.selector!=undefined && cfg.selector!=null ){
			this.selector = (typeof cfg.selector == 'string') ? $(cfg.selector): cfg.selector;
//			if(cfg.option){
//				this._default = $.extend(true,{},this._default,cfg.option);
//			}
			this._cfg = cfg;
			this._init();
		}
	}
	homeSolidgauge.prototype={
			selector:undefined,
			constructor : homeSolidgauge,
			_cfg:{},
			_init:function(){
				var that = this;
				var highOption = {
						title:{
							floating : true,
							align:"left",
							style:{
									color: Highcharts.theme.homeSolidgaugeFontColor,
									font: '12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif',
									fontWeight:'bold'
									},
							x:3,
							y:12
				        },
				        tooltip : {
							enabled : false
						},
						chart: {
							type: 'solidgauge',
				            backgroundColor:null,
				            animation:false,
							width:that._cfg.width?that._cfg.width:200,
							height:that._cfg.height?that._cfg.height:80,
							margin:[22,0,22,0]
				        },
						 yAxis: {
							 title: {
				                 enabled : false
				             },
				             lineWidth: 0,
				             minorTickInterval: null,
				             tickPixelInterval: 100,
				             tickWidth: 0,
				             labels: {
				                 enabled: false
				             },
				             gridLineWidth : 0,
				        	 min: that._cfg.min?that._cfg.min:0,
				             max: that._cfg.max?that._cfg.max:100,
		            		 plotBands: [{
		         	            from: that._cfg.min?that._cfg.min:0,
		         	            to: that._cfg.max?that._cfg.max:100,
		         	            innerRadius: '80%'
		         	        }],
		         	        stops: [
		   						[0, that._cfg.color]
		   		             ]
				        },
				        pane: {
				        	center: ['50%', '100%'],
				            size: '140%',
				            startAngle: -90,
				            endAngle: 90,
				            background: {
				                innerRadius: '80%',
				                outerRadius: '101%',
				                borderWidth:1,
				                shape: 'arc',
				                backgroundColor:Highcharts.theme.pane.background.backgroundColor,
				                borderColor:Highcharts.theme.pane.background.borderColor
				            }
				        },
				        plotOptions: {
				            solidgauge: {
				                dataLabels: {
				                	y: 0,
				                    borderWidth: 0,
				                    useHTML: true
				                }
				            }
				        },
						series: [{
                            type : 'solidgauge',
                            animation : false,
				            data: [that._cfg.data],
				            dataLabels: {
				            	align : 'center',
			    	        	style : {
			    	        		fontSize:'14px',
			    	        		color : '#555555'
			    	        	},
				                format:'<div style="color:'+Highcharts.theme.plotOptions.gauge.series.dataLabels.color+'">'+ (that._cfg.data!=null && that._cfg.data!=undefined? (that._cfg.data == 'N/A' ? 'N/A' : '{y}%'):'--' )+'<div>',
				               // format: (that._cfg.data>=0?'{y}%':"N/A" ),
				                floating : true,
								y : 5,
								zIndex:99
				            },
				            innerRadius : '80%'
				        },{
			    	    	type : 'gauge',
                            animation : false,
			    	    	dial: {
			    	    		backgroundColor :Highcharts.theme.plotOptions.gauge.dial.backgroundColor,
			    	    		borderColor : Highcharts.theme.plotOptions.gauge.dial.borderColor,
				    	    	baseLength: '0%',
				    	    	baseWidth: 4,
				    	    	topWidth: 1,
				    	    	radius : '80%',
				    	    	borderWidth: 0,
				    	    	rearLength : '0%'
			    	    	},
			    	    	pivot : {
			    	    		backgroundColor :Highcharts.theme.plotOptions.gauge.pivot.backgroundColor,
			    	    		radius : 1
							},
							dataLabels: {
								enabled:false
							},
			    	    	data : [that._cfg.data]
			    	    }]
					};
				if(that._cfg.option){
					highOption = $.extend(true,{},highOption,that._cfg.option);
				}
				that.selector.highcharts(highOption);
				that.charts = that.selector.highcharts();
			},
			update:function(newVal){
				var chart = this.selector.highcharts(),
		            point,
		            point1;
		        if (chart) {
		            point = chart.series[0].points[0];
		            point1 = chart.series[1].points[0];
		            point.update(newVal);
		            point1.update(newVal);
		        }
			}
	};
	
	
	oc.home.highchart={
			solidgauge:function(cfg){
				return new homeSolidgauge(cfg);
			}
	};

	oc.home.workbench.setExt=function(workbenchId, sort, ext, fn, module, obj){
		var data = {workbenchId:workbenchId, sort:sort, selfExt:ext};
		if(obj) $.extend(data, obj);
		oc.util.ajax({
			url:oc.resource.getUrl('home/workbench/main/setExt.htm'),
			data:data,
			success:function(d){
				fn&&fn(d);
				if(module){
					module.parent().attr('data-workbench-ext',ext);
					module.find('span.ico-refrash').trigger('click','intervalEvent');
				}
			}
		});
	};
})();