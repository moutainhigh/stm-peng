(function($){
	function HomeAppInfo(config){
		this.config = $.extend({}, this._defaults,config);
		//this.selector = $("#oc-module-home-workbench-app").attr("id",oc.util.generateId());
		//oc.home.workbench.userWorkbenches[this.selector.parent("div").attr("data-workbench-sort")]=this;

		var innerMainDivId = oc.util.generateId();
//		$("#oc-module-home-workbench-app-main").attr('id', "oc-module-home-workbench-app-main-" + config.resourctId);
//		this.selector = $("#" + config.outerMainDivId).attr('id', innerMainDivId);
		this.selector = $("#" + config.outerMainDivId).find("#oc-module-home-workbench-app-main").attr('id', innerMainDivId);;
//		this.init();
	}
	HomeAppInfo.prototype={
			selector:undefined,
			constructor:HomeAppInfo,
			_defaults : {
				id:undefined
			},
			resourctId:undefined,
			_CPUCharts:undefined,
			_AppCPUCharts:undefined,
			_MenoryCharts:undefined,
			_AppMenoryCharts:undefined,
			loadData:undefined,
			_CPUChartsIns:undefined,
			_AppCPUChartsIns:undefined,
			_MenoryChartsIns:undefined,
			_AppMenoryChartsIns:undefined,
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
				//UNKOWN:'#737373'
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
				this.resourctId = this.config.resourctId;
				this.loadChartsData();
				//通过AJAX查询后台数据，判断是否已经选择过资源
//				if(this.resourctId!=undefined && this.resourctId!=null && this.resourctId!=""){
//					this.selector.find("#appMainNull").hide();
//					this.selector.find("#oc-module-home-workbench-app-main-" + this.resourctId).show();
//					this.loadChartsData();
//				}else{
//					this.selector.find("#appMainNull").show();
//					this.selector.find("#oc-module-home-workbench-app-main-" + this.resourctId).hide();
//				}
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
							if(d.data == null){
								that.selector.css({"text-align":"center","line-height":that.selector.innerHeight() + "px"});
								that.selector.html('<span class="table-dataRemind">该资源已被删除或不存在</span>');
								return;
							}
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
//							that.selector.find("#cancelMonitorSubbox").show();
						}else{
							that.selector.find("#monitorSubbox").show();
//							that.selector.find("#cancelMonitorSubbox").hide();
							var thisCfg = that._cfg;
							thisCfg.selector = that.selector.find("#CPUCharts");
                            /*thisCfg.option.title.text="主机CPU平均利用率";
                            thisCfg.option.title.verticalAlign="bottom";
                            thisCfg.option.title.align="center";
                            thisCfg.color = objcolor?objcolor:that.colors[obj.parent.metric.cpuRateState];
                            if(obj.parent.metric.cpuRate!=null && obj.parent.metric.cpuRate!=undefined){
                                thisCfg.data=obj.parent.metric.cpuRate== 'N/A' ? 'N/A' : obj.parent.metric.cpuRate;
                            }else{
                                thisCfg.data=null;
                            }*/

                            var servers =  $.extend(true,{},that.config.properties.focusResource.gaugeOption["series"],true,true);
                            var server = servers[0];
                            server.name = '主机CPU平均利用率';
                            if(obj.parent.metric.cpuRate!=null && obj.parent.metric.cpuRate!=undefined){
                                server.data[0].value = obj.parent.metric.cpuRate== 'N/A' ? 'N/A' : obj.parent.metric.cpuRate;
                            }else{
                                server.data[0].value = null;
                            }
                            server.data[0].name = '主机CPU平均利用率';
                            var option = {
                                series: [server]
                            };
                            if(thisCfg.selector[0] == null){
                                return;
                            }
                            var myChartHost = echarts.init(thisCfg.selector[0]);
                            myChartHost.setOption(option);
                            this._CPUCharts = myChartHost;
							//thisCfg.data=obj.parent.metric.cpuRate?obj.parent.metric.cpuRate:0;
							// this._CPUCharts = oc.home.highchart.solidgauge(thisCfg);
							
							thisCfg.selector = that.selector.find("#appCPUCharts");
							/*thisCfg.option.title.text="应用CPU平均利用率";//obj.parent.resourceName+"当前占用";
							thisCfg.color = objcolor?objcolor:that.colors[obj.parent.metric.appcpuRateState];
							if(obj.parent.metric.appCpuRate!=null && obj.parent.metric.appCpuRate!=undefined){
								thisCfg.data=obj.parent.metric.appCpuRate== 'N/A' ? 'N/A' : obj.parent.metric.appCpuRate;
							}else{
								thisCfg.data=null;
							}*/
							
							//thisCfg.data = obj.parent.metric.appCpuRate?obj.parent.metric.appCpuRate:0;
							// this._AppCPUCharts = oc.home.highchart.solidgauge(thisCfg);
                            server.name = '应用CPU平均利用率';
                            if(obj.parent.metric.appCpuRate!=null && obj.parent.metric.appCpuRate!=undefined){
                                server.data[0].value = obj.parent.metric.appCpuRate== 'N/A' ? 'N/A' : obj.parent.metric.appCpuRate;
                            }else{
                                server.data[0].value = null;
                            }
                            server.data[0].name = '应用CPU平均利用率';
                            option.series[0] = server;
                            var myChartApp = echarts.init(thisCfg.selector[0]);
                            myChartApp.setOption(option);
                            this._AppCPUCharts = myChartApp;

							
							thisCfg.selector = that.selector.find("#menoryCharts");
						/*	thisCfg.option.title.text="主机内存利用率";
							thisCfg.color =objcolor?objcolor: that.colors[obj.parent.metric.memRateState];
							thisCfg.option.yAxis.stops=[[0,that.colors[obj.parent.metric.memRateState]]];
							if(obj.parent.metric.memRate!=null && obj.parent.metric.memRate!=undefined){
								thisCfg.data=obj.parent.metric.memRate== 'N/A' ? 'N/A' : obj.parent.metric.memRate;
							}else{
								thisCfg.data=null;
							}*/
							//thisCfg.data = obj.parent.metric.memRate?obj.parent.metric.memRate:'--';
							// this._MenoryCharts = oc.home.highchart.solidgauge(thisCfg);
                            server.name = '主机内存利用率';
                            if(obj.parent.metric.memRate!=null && obj.parent.metric.memRate!=undefined){
                                server.data[0].value = obj.parent.metric.memRate== 'N/A' ? 'N/A' : obj.parent.metric.memRate;
                            }else{
                                server.data[0].value = null;
                            }
                            server.data[0].name = '主机内存利用率';
                            option.series[0] = server;
                            var myChartMem = echarts.init(thisCfg.selector[0]);
                            myChartMem.setOption(option);
                            this._MenoryCharts = myChartMem;

							thisCfg.selector = that.selector.find("#appMenoryCharts");
							/*thisCfg.option.title.text="应用内存利用率";//obj.parent.resourceName+"当前占用";
							thisCfg.color =objcolor?objcolor: that.colors[obj.parent.metric.appmemRateState];
							if(obj.parent.metric.appMemRate!=null && obj.parent.metric.appMemRate!=undefined){
								thisCfg.data=obj.parent.metric.appMemRate== 'N/A' ? 'N/A' : obj.parent.metric.appMemRate;
							}else{
								thisCfg.data=null;
							}*/
							//thisCfg.data = obj.parent.metric.appMemRate?obj.parent.metric.appMemRate:0;
							// this._AppMenoryCharts = oc.home.highchart.solidgauge(thisCfg);
                            server.name = '应用内存利用率';
                            if(obj.parent.metric.appMemRate!=null && obj.parent.metric.appMemRate!=undefined){
                                server.data[0].value = obj.parent.metric.appMemRate== 'N/A' ? 'N/A' : obj.parent.metric.appMemRate;
                            }else{
                                server.data[0].value = null;
                            }
                            server.data[0].name = '应用内存利用率';
                            option.series[0] = server;
                            var myChartAppMem = echarts.init(thisCfg.selector[0]);
                            myChartAppMem.setOption(option);
                            this._AppMenoryCharts = myChartAppMem;


							that.selectMetric(objcolor);
						}
					}else{
//						this.selector.find("#appMainNull").show();
						this.selector.find("#oc-module-home-workbench-app-main-" + this.resourctId).hide();
					}
				}
			},
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
	


	oc.ns('oc.index.home.widget.focusresource');
	oc.index.home.widget.focusresource.showAppInfo = function(opt){
  			var app = new HomeAppInfo(opt);
			app.init();
    	return app;
    }

	/*
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
	//*/

})(jQuery);