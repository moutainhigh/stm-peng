(function(){
	Highcharts.setOptions(Highcharts.theme);
	
	oc.ns('oc.vm.workbench.user');
	oc.ns("oc.vm.highchart");
	oc.vm.workbench.userWorkbenches={};
	oc.vm.workbench.user.ready='';
	//工作台初始化部分   begin ******************
	var id=oc.util.generateId(),workbenchDiv=$('#oc-vm-workbench').attr('id',id),
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
			if(modules.length>0)oc.vm.workbench.domainId=modules[0].domainId;
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
						.attr('data-workbench-sort',selfData.sort).attr('data-workbench-ext',selfData.selfExt)
						.attr('data-workbench-name',selfData.name==null?" ":selfData.name)
						.attr('data-workbench-template',selfData.templateType==null?" ":selfData.templateType)
						.attr('data-workbench-templateName',selfData.templateTypeName==null?" ":selfData.templateTypeName)
						.attr('data-workbench-topNum',selfData.topNum==null?" ":selfData.topNum)
						.attr('data-workbench-resourceIds',selfData.resourceIds==null?" ":selfData.resourceIds)
						.attr('data-workbench-metric',selfData.sortMetric==null?" ":selfData.sortMetric)
						.attr('data-workbench-sortOrder',selfData.sortOrder==null?" ":selfData.sortOrder)
						.attr('data-workbench-showType',selfData.showType==null?" ":selfData.showType)
						.attr('data-workbench-metricName',selfData.sortMetricName==null?" ":selfData.sortMetricName);
					content.append(module);
					module.css('height',($('#mainMenuContent').height() - pageDiv.height()) * 0.5);
					module.load(oc.resource.getUrl(selfData.url) + '?_=' + new Date().getTime(),function(){
						var layout=module.find('.module:first').layout();
						layout.find('span.fa-times-circle:first').click(function(){
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
			if(oc.vm.workbench.userWorkbenches[module.sort]){
				if(module.loaded){
					oc.vm.workbench.userWorkbenches[module.sort].render();
				}else{
					oc.vm.workbench.userWorkbenches[module.sort].reLoad();
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
						modules[begin+startIdx]&&oc.vm.workbench.userWorkbenches[modules[begin+startIdx].sort].reLoad();
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
				content.html('<div class="oc_vm_nodata"></div>');
			}
		},
		delModule:function(module){
			var t=this,modules=t.modules;
			//删除分页标签
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
			
			oc.ui.confirm('确认删除该面板，删除后可以点击左边设置按钮重新加入该面板！',function(){
				oc.util.ajax({
					url:oc.resource.getUrl('portal/vm/topN/delSingleUserWorkbench.htm'),
					data:{
						workbenchId:module.workbenchId,
						userId:oc.index.getUser().id,
						sort:module.sort
					},
					success:function(d){
						if(d.data>0){
							var idx=modules.indexOf(function(o){
								return o.sort==module.sort;
							});
							module.module.remove();
							modules.splice(idx, 1);
							t.drawPage();
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
			_self.modules = [];
//			for(var i=0;i<5;i++){
//				_self.modules.push(
//				{
//		            "id": null, 
//		            "title": "TOPN", 
//		            "url": "resource/module/vm/topN/workbench/topn.html", 
//		            "icon": "topn", 
//		            "userId": null, 
//		            "workbenchId": 1, 
//		            "sort": i, 
//		            "selfExt": null, 
//		            "domainId": 1
//		        });
//			}
			_self.initModules();
			oc.util.ajax({
				url:oc.resource.getUrl('portal/vm/topN/getTopNUserWorkbenchs.htm'),
				success:function(ds){
					_self.modules=ds=$.isArray(ds.data)?ds.data:[];
					_self.initModules();
				}
			});
		}
	};
	var workbenchObj=new workbenchPage();
	workbenchObj.load();
	oc.vm.workbench.loadWorkbench=function(){
		workbenchObj.load();
	};
	
	oc.util.whileOne(window,'resize.vmWorkbench', 500,function(){
		workbenchObj.drawPage();
	});
	
	//工作台初始化部分   end ******************
	function vmSolidgauge(cfg){
		if(cfg.selector!=undefined && cfg.selector!=null ){
			this.selector = (typeof cfg.selector == 'string') ? $(cfg.selector): cfg.selector;
			if(cfg.option){
				this._default = $.extend(true,{},this._default,cfg.option);
			}
			this._cfg = cfg;
			this._init();
		}
	}
	vmSolidgauge.prototype={
			selector:undefined,
			constructor : vmSolidgauge,
			_cfg:{},
			_init:function(){
				var that = this;
				that.selector.highcharts(Highcharts.merge(that._default,{
					title:{
						style:{
								color: that._cfg.color
							}
			        },
					chart: {
						width:that._cfg.width?that._cfg.width:200,
						height:that._cfg.height?that._cfg.height:80
			        },
					 yAxis: {
			        	 min: that._cfg.min?that._cfg.min:0,
			             max: that._cfg.max?that._cfg.max:100,
	            		 plotBands: [{
	         	            from: that._cfg.min?that._cfg.min:0,
	         	            to: that._cfg.max?that._cfg.max:100,
	         	            color: '#262828',
	         	            innerRadius: '80%'
	         	        }],
	         	       stops: [
	   						[0, that._cfg.color]
	   		             ]
			        },
					series: [{
			            data: [that._cfg.data],
			            dataLabels: {
			                format: '<div style="text-align:center"><span style="font-size:13px;color:#FFFFFF">'+(that._cfg.data>=0?'{y}%':"N/A" )+'</span>',
			                floating : true,
							y : 5
			            },
			            innerRadius : '80%'
			        },{
		    	    	type : 'gauge',
		    	    	dial: {
			    	    	backgroundColor: '#1B6D19',
			    	    	baseLength: '0%',
			    	    	baseWidth: 5,
			    	    	topWidth: 1,
			    	    	borderColor: '#1B6D19',
			    	    	borderWidth: 0
		    	    	},
		    	    	pivot : {
		    	    		radius : 0
						},
						dataLabels: {
							enabled:false
						},
		    	    	data : [that._cfg.data]
		    	    }]
				}));
			},
			_default:{
				chart: {
		            type: 'solidgauge',
		            backgroundColor:null
		        },
		        title:{
		        	floating : true,
					align:"left",
					style:{
							color: '#FF9C00',
							font: '12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif',
							fontWeight:'bold'
							},
					y :5,
					x:3
		        },
		        tooltip : {
					enabled : false
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
		             gridLineWidth : 0
		        },
		        pane: {
		        	center: ['50%', '100%'],
		            size: '140%',
		            startAngle: -90,
		            endAngle: 90,
		            background: [{
		                backgroundColor: null,
		                innerRadius: '80%',
		                outerRadius: '100%',
		                borderWidth:0,
		                shape: 'arc'
//		            },{
//		                backgroundColor:"#27342A",
//		                borderWidth: 0
		            }]
		        },
		        plotOptions: {
		            solidgauge: {
		                dataLabels: {
		                	y: 0,
		                    borderWidth: 0,
		                    useHTML: true
		                }
		            }
		        }
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
	
	
	oc.vm.highchart={
			solidgauge:function(cfg){
				return new vmSolidgauge(cfg);
			}
	};

//	oc.vm.workbench.setExt=function(workbenchId,sort,ext,fn,module){
//		oc.util.ajax({
//			url:oc.resource.getUrl('vm/workbench/main/setExt.htm'),
//			data:{
//				workbenchId:workbenchId,
//				sort:sort,
//				selfExt:ext
//			},
//			success:function(d){
//				fn&&fn(d);
//				if(module){
//					module.parent().attr('data-workbench-ext',ext);
//					module.find('span.ico-refrash').trigger('click','intervalEvent');
//				}
//			}
//		});
//	};
})();