(function($){
	function HomeAppInfo(){
		this.selector = $("#oc-module-home-workbench-app").attr("id",oc.util.generateId());
		oc.home.workbench.userWorkbenches[this.selector.parent("div").attr("data-workbench-sort")]=this;
//		this.init();
	}
	HomeAppInfo.prototype={
			selector:undefined,
			constructor:HomeAppInfo,
			resourctId:undefined,
			_CPUCharts:undefined,
			_AppCPUCharts:undefined,
			_MenoryCharts:undefined,
			_AppMenoryCharts:undefined,
			loadData:undefined,
			state:{
				CRITICAL:'不可用',
				SERIOUS:'可用',
				WARN:'可用',
				UNKOWN:'未知',
				NORMAL:'可用'
			},
			colors:{
				CRITICAL:'#CE0109',
				SERIOUS:'#FF7D02',
				WARN:'#FBEB07',
				NORMAL:'#42b74c',
//				UNKOWN:'#737373'
				UNKOWN:'#42b74c'
			},
			_cfg:{
				min:0,
				max:100,
				height:70,
				width:220,
				data:0,
				option:{
					title : {
						text : "",
						style:{
							color:undefined
						}
					},
					yAxis:{
						stops:undefined
					}
				}
			},
			setResourceId:function(value){
				var workbenchId = this.selector.parent("div").attr("data-workbench-id");
				var workbenchSort = this.selector.parent("div").attr("data-workbench-sort");
				oc.home.workbench.userWorkbenches[workbenchSort]=this;
				this.resourctId = value;
				this.selector.parent("div").attr("data-workbench-ext",this.resourctId);
				oc.home.workbench.setExt(workbenchId,workbenchSort,this.resourctId);
				
			},
			init:function(){
				this.resourctId = this.selector.parent("div").attr("data-workbench-ext");
				//通过AJAX查询后台数据，判断是否已经选择过资源
				if(this.resourctId!=undefined && this.resourctId!=null && this.resourctId!=""){
					this.selector.find("#appMainNull").hide();
					this.selector.find("#oc-module-home-workbench-app-main").show();
					this.loadChartsData();
				}else{
					this.selector.find("#appMainNull").show();
					this.selector.find("#oc-module-home-workbench-app-main").hide();
				}
			},
			loadChartsData:function(){
				var that = this;
				if(that.resourctId!=undefined && that.resourctId!=null && that.resourctId!=""){
					oc.util.ajax({
						url:oc.resource.getUrl("system/home/getHomeAppData.htm"),
						data:{resourceId:that.resourctId},
						startProgress:null,
						stopProgress:null,
						success:function(d){
							that.loadData = d;
							that.initChart(that.loadData);
						}
					});
				}
			},
			initChart:function(d){
				if(d.code && d.code==200){
					var obj = d.data,that=this;
					that._cfg.width = that.selector.find(".charts:first .oc-border-bottom").width();
					that._cfg.height = that.selector.find(".charts:first .oc-border-bottom").height();
					if(obj!=undefined && obj!=null){
						var objcolor = undefined;
						var objState = that.state[obj.parent.metric.availability];
						if(objState){
							if(objState==that.state.UNKOWN || objState == that.state.CRITICAL){
								objcolor = that.colors.UNKOWN;
							}
						}
						var state = that.state[obj.parent.metric.availability]?that.state[obj.parent.metric.availability]:"";
						that.selector.find("label[field='status']").text(state).attr("title",state);
						var appIp = "";
						if(obj.parent.ip && obj.parent.ip.length>0){
							appIp = obj.parent.ip[0].id;
						}
						that.selector.find("label[field='IP']").text(appIp).attr("title",appIp);
						var appType = obj.parent.resourceType?obj.parent.resourceType:"";
						that.selector.find("label[field='resourceType']").text(appType).attr("title",appType);
						var appName = obj.parent.name?obj.parent.name:"";
						that.selector.find("label[field='name']").text(appName).attr("title",appName);
						that.selector.find("#metricInfo #metricTitle").text((obj.parent.resourceType?obj.parent.resourceType:"")+"性能指标");
						if(obj.parent.lifeState=="NOT_MONITORED"){
							that.selector.find("#monitorSubbox").hide();
							that.selector.find("#cancelMonitorSubbox").show();
						}else{
							that.selector.find("#monitorSubbox").show();
							that.selector.find("#cancelMonitorSubbox").hide();
							var thisCfg = that._cfg;
							thisCfg.selector = that.selector.find("#CPUCharts");
							thisCfg.option.title.text="主机CPU平均利用率";
							thisCfg.color = objcolor?objcolor:that.colors[obj.parent.metric.cpuRateState];
							if(obj.parent.metric.cpuRate!=null && obj.parent.metric.cpuRate!=undefined){
								thisCfg.data=obj.parent.metric.cpuRate== 'N/A' ? 'N/A' : obj.parent.metric.cpuRate;
							}else{
								thisCfg.data=null;
							}
							
//							thisCfg.data=obj.parent.metric.cpuRate?obj.parent.metric.cpuRate:0;
							this._CPUCharts = oc.home.highchart.solidgauge(thisCfg);
							
							thisCfg.selector = that.selector.find("#appCPUCharts");
							thisCfg.option.title.text="应用CPU平均利用率";//obj.parent.resourceName+"当前占用";
							thisCfg.color = objcolor?objcolor:that.colors[obj.parent.metric.appcpuRateState];
							if(obj.parent.metric.appCpuRate!=null && obj.parent.metric.appCpuRate!=undefined){
								thisCfg.data=obj.parent.metric.appCpuRate== 'N/A' ? 'N/A' : obj.parent.metric.appCpuRate;
							}else{
								thisCfg.data=null;
							}
							
//							thisCfg.data = obj.parent.metric.appCpuRate?obj.parent.metric.appCpuRate:0;
							this._AppCPUCharts = oc.home.highchart.solidgauge(thisCfg);
							
							thisCfg.selector = that.selector.find("#menoryCharts");
							thisCfg.option.title.text="主机内存利用率";
							thisCfg.color =objcolor?objcolor: that.colors[obj.parent.metric.memRateState];
							thisCfg.option.yAxis.stops=[[0,that.colors[obj.parent.metric.memRateState]]];
							if(obj.parent.metric.memRate!=null && obj.parent.metric.memRate!=undefined){
								thisCfg.data=obj.parent.metric.memRate== 'N/A' ? 'N/A' : obj.parent.metric.memRate;
							}else{
								thisCfg.data=null;
							}
//							thisCfg.data = obj.parent.metric.memRate?obj.parent.metric.memRate:'--';
							this._MenoryCharts = oc.home.highchart.solidgauge(thisCfg);
							
							thisCfg.selector = that.selector.find("#appMenoryCharts");
							thisCfg.option.title.text="应用内存利用率";//obj.parent.resourceName+"当前占用";
							thisCfg.color =objcolor?objcolor: that.colors[obj.parent.metric.appmemRateState];
							if(obj.parent.metric.appMemRate!=null && obj.parent.metric.appMemRate!=undefined){
								thisCfg.data=obj.parent.metric.appMemRate== 'N/A' ? 'N/A' : obj.parent.metric.appMemRate;
							}else{
								thisCfg.data=null;
							}
//							thisCfg.data = obj.parent.metric.appMemRate?obj.parent.metric.appMemRate:0;
							this._AppMenoryCharts = oc.home.highchart.solidgauge(thisCfg);
							
							that.selectMetric(objcolor);
						}
					}else{
						this.selector.find("#appMainNull").show();
						this.selector.find("#oc-module-home-workbench-app-main").hide();
					}
				}
			},
//			reLoadChartData:function(){
//				var that = this;
//				oc.util.ajax({
//					url:oc.resource.getUrl("system/home/getHomeAppData.htm"),
//					data:{resourceId:that.resourctId},
//					successMsg:"",
//					startProgress:null,
//					stopProgress:null,
//					success:function(d){
//						that.loadData = d;
//						that.initChart(that.loadData);
//					}
//				});
//			},
			selectMetric:function(objcolor){
				var that = this;
				oc.util.ajax({
					url:oc.resource.getUrl("system/home/getResourceMetricList.htm"),
					data:{resourctId:that.resourctId},
					startProgress:null,
					stopProgress:null,
					success:function(d){
						if(d.code && d.code==200){
							if(d.data!=null && d.data.length>0){
								that.selector.find("#noMetric").hide();
								that.selector.find("#metricInfo").show();
								var metricsHtml = that.selector.find("#metric").empty();
								var metrics = d.data;
								var state = {};
								oc.util.getDict("res_indicator_alarm_status",function(data){
									for(var i=0;i<data.length;i++){
										var d = data[i];
										state[d.code] = d.name; 
									}
						        });
								
								for(var i=0;i<metrics.length;i++){
									var m = metrics[i];
									var currentVal = m.currentVal==undefined?"":m.currentVal;
									//m.status //指标状态
									var color = objcolor?objcolor:state[m.status];
									if(m.status=="WARN"){
										color="#F5CC00";
									}
									var thisHtml = $('<li class="home-line oc-home-liheight"><div style="max-width: 65%;float:left; height:22px;word-wrap:normal;overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="'+m.text+'">'+m.text+'</div><div style="max-width: 35%;height:22px;word-wrap:normal;overflow:hidden;white-space:nowrap;text-overflow: ellipsis;float: right; color:'+color+';" title="'+currentVal+'">'+currentVal+'</div></li>');
									thisHtml.appendTo(metricsHtml);
								}
							}else{
								that.selector.find("#noMetric").show();
								that.selector.find("#metricInfo").hide();
							}
						}else{
							that.selector.find("#noMetric").show();
							that.selector.find("#metricInfo").hide();
						}	
					}
				});
			},
			reLoad:function(){
				this.init();
			},
			render:function(){
				if(this.loadData!=undefined){
					this.initChart(this.loadData);
				}else{
					this.init();
				}
			}
	};
	var homeAppCharts = new HomeAppInfo();
	oc.home.workbench.app=function(){
		return homeAppCharts;
	};
	homeAppCharts.selector.find("#appSetting,#addMyApp").click(function(){
		oc.home.workbench.resource.select.open({
			type:'app',//默认展示所有资源
			title:'资源选择',
			value:homeAppCharts.resourctId,
			confirmFn:function(value,obj){
				homeAppCharts.selector.find("#appMainNull").hide();
				homeAppCharts.selector.find("#oc-module-home-workbench-app-main").show();
				homeAppCharts.setResourceId(value);
				homeAppCharts.loadChartsData();
			}
		});
	});
	
	homeAppCharts.selector.find("#reLoadAppData").click(function(){
		homeAppCharts.reLoad();
	});
//	homeAppCharts.selector.bind(oc.events.resize,function(){
//		homeAppCharts.render();
//	});
})(jQuery);