(function($) {
	function HomeTopn(top){
		this.selector = $("#oc-module-home-workbench-topn").attr("id",oc.util.generateId());
		oc.home.workbench.userWorkbenches[this.selector.parent("div").attr("data-workbench-sort")]=this;
		this.top=top;
//		this.init();
	}
	function CpuMemoryTab(){
		this.init();
	}
	/**
	 * 柱状图颜色映射关系
	 *	致命：#d24748   
		严重：#e99349 
		警告：#e5ba47
		正常：#5cc25b
		未知：#91969c 
	 * 	*/
	var colorMap =  {
			"red":{},
			"orange":{},
			"yellow":{},
			"green":{},
			"gray":{}
	}
	CpuMemoryTab.prototype={
			init:function(){
				$("#topnSettingContent").find("#cpuOrMemory a").bind("click",function(){
		    		$("#topnSettingContent").find("#cpuOrMemory a").removeClass("active");
		    		$(this).addClass("active");
		    	});
			},
			getSelect:function(){
				var value = "";
				$("#topnSettingContent").find("#cpuOrMemory a").each(function(){
		    		if($(this).hasClass("active")){
		    			value = $(this).attr("value");
		    		}
		    	});
				return value;
			},
			select:function(value){
				$("#topnSettingContent").find("#cpuOrMemory a").each(function(){
					if($(this).attr("value")==value){
						$("#topnSettingContent").find("#cpuOrMemory a").removeClass("active");
						$(this).addClass("active");
					}
		    	});
			}
	};
	HomeTopn.prototype={
			constructor:HomeTopn,
			selector:undefined,
			_dlg:$("<div></div>"),
			resourceTab:undefined,
			cmTab:undefined,
			resources:{Host:'主机',NetworkDevice:'网络',App:'应用'},
			resource:"Host",
			metric:"cpu",
			groupId:0,
			top:5,
			groupName:'全部',
			color:Highcharts.theme.topnColumnColor,
			highlightColor:Highcharts.theme.topnColumnHighlightColor,
			_resourceGroupCombobox:undefined,
			loadData:undefined,
			loadSetting:function(){
				var setting = this.selector.parent("div").attr("data-workbench-ext");
				if(setting!=undefined && setting!=""){
					var set = setting.split(";");
					this.resource = set[0];
					this.metric = set[1];
					var group = set[2].split(",");
					this.groupId = group[0];
					this.groupName= group[1];
				}
				this.selector.find("#workbench-panel-title").text("TOPN-"+this.resources[this.resource]+(this.metric=="cpu"?'CPU':'内存')+'利用率');
			},
			saveSetting:function(resource,metric,group){
				this.resource = resource;
				this.metric = metric;
				this.groupId = group.groupId;
				this.groupName = group.groupName;
				var workbenchId = this.selector.parent("div").attr("data-workbench-id");
				var workbenchSort = this.selector.parent("div").attr("data-workbench-sort");
				this.selector.parent("div").attr("data-workbench-ext",this.resource+";"+this.metric+";"+this.groupId+","+this.groupName);
				oc.home.workbench.setExt(workbenchId,workbenchSort,this.resource+";"+this.metric+";"+this.groupId+","+this.groupName);
			},
			getThemeColor:function(){
				colorMap.red={
						color:Highcharts.theme.topnRedColor,
						borderColor:Highcharts.theme.topnRedBorderColor,
						wearColor:Highcharts.theme.topnRedWearColor
				};
				colorMap.orange={
						color:Highcharts.theme.topnOrangeColor,
						borderColor:Highcharts.theme.topnOrangeBorderColor,
						wearColor:Highcharts.theme.topnOrangeWearColor
				};
				colorMap.yellow={
						color:Highcharts.theme.topnYellowColor,
						borderColor:Highcharts.theme.topnYellowBorderColor,
						wearColor:Highcharts.theme.topnYellowWearColor
				};
				colorMap.green={
						color:Highcharts.theme.topnGreenColor,
						borderColor:Highcharts.theme.topnGreenBorderColor,
						wearColor:Highcharts.theme.topnGreenWearColor
				};
				colorMap.gray={
						color:Highcharts.theme.topnGrayColor,
						borderColor:Highcharts.theme.topnGrayBorderColor,
						wearColor:Highcharts.theme.topnGrayWearColor
				};
			},
			init:function(){
				var that = this,domainId=oc.home.workbench.domainId;
				that.loadSetting();
				oc.util.ajax({
					url:oc.resource.getUrl("system/home/getHomeTopnData.htm"),
					data:{resource:this.resource,metric:this.metric,groupId:that.groupId,domainId:domainId==null?0:domainId,top:this.top},
					startProgress:false,
					stopProgress:false,
					success:function(d){
						if(d.code && d.code ==200){
							var myData = d.data;
							var showData = [];
							if(myData!=null && myData.data!=null){
								for(var i=0;i<myData.data.length;i++){
									var myObj = {name:myData.data[i].name,y : parseFloat(myData.data[i].y),color : that.color[i>4?i-5:i],t_status:myData.data[i].status};
//									var myObj = {name:myData.data[i].name,y : parseFloat(myData.data[i].y),color : colorMap[myData.data[i].status]};
									showData.push(myObj);
								}
							}
							if(showData.length<=that.top && showData.length>=0){
								var length = parseInt(that.top-showData.length)
								for(var j=0;j<length;j++){
									showData.push({});
									if(myData.categories==undefined)myData.categories=[];
									myData.categories.push("");
								}
							}
							var config = {
									data:showData,
									categories:myData.categories
							};
							that.loadData = config;
							that.initChart(that.loadData);
						}
					}
				});
				
			},
			openSetting:function(){
				var that = this;
				that._dlg.dialog({
					href:oc.resource.getUrl("resource/module/home/workbench/topnSetting.html"),
				    title: 'TOPN',
				    width: 400,
				    height: 280,
				    cache: false,
				    modal: true,
				    buttons : [{
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							var resource = that.resourceTab.getSelect();
							var groupId = that._dlg.find("#form_group_select").combotree("getValue");
							var group = {
								groupId:groupId==""?0:groupId,
								groupName:that._dlg.find("#form_group_select").combotree("getText")
							}
							that.saveSetting(resource.value, that.cmTab.getSelect(),group);
							that.init();
							that._dlg.dialog("close");
						}
					},{
						text:"取消",
						iconCls:"fa fa-times-circle",
						handler:function(){
							that._dlg.dialog("close");
						}
					}],
				    onLoad:function(){
				    	that.resourceTab =  oc.home.workbench.homeResourceTab({
				    		selector : "#topnSettingContent",
				    		tabs : [{
				    			text : "主机",
				    			value:"Host"
				    		}, {
				    			text : "网络",
				    			value:"NetworkDevice"
				    		}, {
				    			text : "应用",
				    			value:"App"
				    		}]
				    	});
				    	
				    	oc.util.ajax({
				    		url:oc.resource.getUrl("home/workbench/resource/getResourceGroupByUser2Tree.htm"),
							startProgress:false,
							stopProgress:false,
				    		success:function(data){
				    			/*
				    			var group = data.data;
				    			group.push({id:'',name:'全部'});
				    			group.reverse(); 
				    			that._dlg.find("#form_group_select").combobox({
				    				data:group,
				    				width:150,
				    				panelHeight:120,
				    				valueField:'id',
				    				textField:'name',
				    				onLoadSuccess:function(){
						    			that._dlg.find("#form_group_select").combobox("setValue",(that.groupId==0?"":that.groupId)+"");
						    		}
				    			});
				    			*/
				    			var result = eval(data), allTreeData = [{id:'',name:'全部',text:'全部'}];
				    			for(var i = 0; i < result.data.length; i++){
									var data = result.data[i];
									allTreeData.push(that.createCustomGroupTreeData(data));
								}
				    			that._dlg.find("#form_group_select").combotree({
				    				data:allTreeData,
				    				width:250,
				    				panelHeight:200,
				    				valueField:'id',
				    				textField:'name',
				    				onLoadSuccess:function(){
				    					that.groupId = that.hasIdInAllTreeData(allTreeData, that.groupId==0?"":that.groupId) ? that.groupId : 0;
				    					that._dlg.find("#form_group_select").combotree("setValue",(that.groupId==0?"":that.groupId)+"");
				    				}
				    			});
				    			
				    		}
				    	})
				    	that.cmTab = new CpuMemoryTab();
				    	that.cmTab.select(that.metric);
				    	that.resourceTab.select(that.resource);
				    }
				});
			},
			createCustomGroupTreeData : function(data){
				var that = this;
				var parentData = {
					id : data.id,
					name : data.name,
					text : data.name,
					pid : data.pid,
					children : []
				};
				for(var i = 0; data.childCustomGroupVo && i < data.childCustomGroupVo.length; i++){
					var customGroupVo = data.childCustomGroupVo[i];
					parentData.children.push(that.createCustomGroupTreeData(customGroupVo));
				}
				return parentData;
			},
			hasIdInAllTreeData : function(allTreeData, id){
				var flag = false, that = this;
				for(var i = 0; i < allTreeData.length; i++){
					var treeData = allTreeData[i];
					if(treeData.id == id){
						flag = true;
					}else if(treeData.children != undefined && treeData.children.length > 0){
						flag = that.hasIdInAllTreeData(treeData.children, id);
					}
					if(flag){
						break;
					}
				}
				return flag;
			},
			initChart:function(config){
				var that = this;
				var borderChangeColor="";
				if(Highcharts.theme.currentSkin=="default"){//默认皮肤
					borderChangeColor="";
				}else{
					borderChangeColor='#FFFFFF';
				}
				that.getThemeColor();
				
				that.selector.find(".oc-highcharts-subtitle").html(that.groupName+'的'+(that.metric=="cpu"?'CPU':'内存')+'利用率');
				
				var SetEveryOnePointColor =function(chart) {  
					//获得第一个序列的所有数据点
			        var pointsList = chart.series[0].points;
			        //遍历设置每一个数据点颜色
			        for (var i = 0; i < pointsList.length; i++) {
			        	if(!pointsList[i].t_status){
			        		continue;
			        	}
			        	pointsList[i].update({
				        		color:{
				        			linearGradient: { x1: 0, y1: 0, x2: 1, y2: 0 },
				        			stops:[
											[0, colorMap[pointsList[i].t_status].color],
											[0.4,colorMap[pointsList[i].t_status].wearColor],
											[0.6,colorMap[pointsList[i].t_status].wearColor],
											[1, colorMap[pointsList[i].t_status].color]
				        			       ]
				        		},
				        		borderColor:colorMap[pointsList[i].t_status].borderColor
			        	      });
			        }
			    }
				that.selector.find("#topnPic").highcharts({
					chart : {
						type : 'column'
					},
					xAxis : {
						title : {
							enabled : false,
						},
						lineColor : Highcharts.theme.topnLineColor,
						lineWidth : 2,
						categories:config.categories
					},
					yAxis : {
						min:0,//Y轴最小值
						max:100,//Y轴最大值
						title : {
							enabled : false,
						},
						labels: {
			                formatter: function () {
			                    return this.value + ' %';
			                }
			            },
						lineColor : Highcharts.theme.topnLineColor,
						lineWidth : 2,
						tickInterval: 20,//Y轴刻度
						tickPixelInterval:20,
						tickColor:Highcharts.theme.topnTickColor,
			            tickWidth:6,
			            tickLength:6
//			            tickPosition:'inside'
					},
					title :{
						text:''
					},
					legend: {
						enabled : false
					},
					tooltip : {
						formatter : function() {
							return "资源名称："+this.key+"<br>利用率："+this.y+"%";
						}
					},
					plotOptions: {
	                    column: {
	                        pointPadding:0.2,
	                        borderWidth: 1,
	                        borderColor:borderChangeColor
	                    }
	                },
					series : [ {
						data : config.data
					} ]
				},function (chart) {
	                SetEveryOnePointColor(chart);
	            });
				this.selector.find("#topnPic").find("div:first").addClass("chart-none");
			},
			reLoad:function(top){
				if(top){
					this.top = top;
				}
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
	
	
	oc.home.workbench.topn=function(top){
		return new HomeTopn(top);;
	};
	
	var topnChart =oc.home.workbench.topn(5);
	
	topnChart.selector.find("#topNsetting").click(function(){
		topnChart.openSetting();
	});
	topnChart.selector.find("#refrash").click(function(){
		topnChart.reLoad();
	});
//	topnChart.selector.bind(oc.events.resize,function(){
//		topnChart.render();
//	});
})(jQuery);