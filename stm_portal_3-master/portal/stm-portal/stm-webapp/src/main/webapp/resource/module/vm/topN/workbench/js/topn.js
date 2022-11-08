(function($) {
	function VmTopN(top){
		this.selector = $("#oc-module-vm-workbench-topn").attr("id",oc.util.generateId());
		oc.vm.workbench.userWorkbenches[this.selector.parent("div").attr("data-workbench-sort")]=this;
		this.top=top;
	}
	VmTopN.prototype={
			constructor:VmTopN,
			selector:undefined,
			_dlg:$("<div></div>"),
			showType:"0",//默认显示为图，0图，1表
			settingForm:undefined,
			color:Highcharts.theme.vmColor,//["#8B0504","#BD4910","#AD9701","#027D05","#014486"],
			highlightColor:Highcharts.theme.vmhighlightColor,//['rgb(188, 9, 6)','rgb(209, 102, 35)','rgb(211, 205, 1)','rgb(5, 194, 10)','rgb(2, 101, 207)'],
			_resourceGroupCombobox:undefined,
			loadData:undefined,
			loadSetting:function(){
				var setting = this.selector.parent("div").attr("data-workbench-ext"),
				workbenchName = this.selector.parent('div').attr('data-workbench-name'),
				workbenchTemplate = this.selector.parent('div').attr('data-workbench-template'),
				workbenchTemplateName = this.selector.parent('div').attr('data-workbench-templateName'),
				workbenchTopNum = this.selector.parent('div').attr('data-workbench-topNum'),
				workbenchResourceIds = this.selector.parent('div').attr('data-workbench-resourceIds'),
				workMetricName = this.selector.parent('div').attr('data-workbench-metricName'),titleText='',
				workMetric = this.selector.parent('div').attr('data-workbench-metric'),titleText='';
				titleText += workbenchName==null||workbenchName==""?"":(workbenchName+"-");
				titleText += workbenchTemplateName==null||workbenchTemplateName==undefined?"":(workbenchTemplateName);
				titleText += '-Top'+(parseInt(workbenchTopNum)+1);
//				titleText += '-Top'+(parseInt(workbenchTopNum)+1)+"-";
			//	titleText += workMetricName==null||workMetricName==undefined?"":(workMetricName);
				this.selector.find("#workbench-panel-title").text(titleText);
				this.showType=this.selector.parent('div').attr('data-workbench-showType');
			},
			saveSetting:function(resource,metric,group){
			},
			init:function(){
				var that = this,domainId=oc.vm.workbench.domainId;
				that.loadSetting();
				oc.util.ajax({
					url:oc.resource.getUrl("portal/vm/topN/getTopNGraphData.htm"),
					data:{sortMetric:that.selector.parent('div').attr('data-workbench-metric'),
						resourceIds:that.selector.parent('div').attr('data-workbench-resourceIds'),
						sortOrder:that.selector.parent('div').attr('data-workbench-sortOrder'),
						topNum:parseInt(that.selector.parent('div').attr('data-workbench-topNum'))+1},
					startProgress:null,
					stopProgress:null,
					success:function(d){
						if(d.code && d.code ==200){
							var myData = d.data;
							if(that.showType=="1"){
								that.loadData = myData;
								that.initDataGrid(myData);
								return;
							}
							var showData = [];
							if(myData!=null && myData.data!=null){
								for(var i=0;i<myData.data.length;i++){
									var myObj = {name:myData.data[i].name,
											y : parseFloat(myData.data[i].y),
											unit:myData.data[i].unit,
											color : that.color[i>4?i-5:i]};
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
							for(var i=0;i<myData.categories.length;i++){
								myData.categories[i] = "";
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
					href:oc.resource.getUrl("resource/module/vm/topN/workbench/topnSetting.html"),
				    title: '设置',
				    width: 470,
				    height: 520,
				    cache: false,
				    modal: true,
				    buttons : [{
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							//保存topN工作台设置数据
							if(that.settingForm.validate()){
								var formval = that.settingForm.val();
								formval.userId = oc.index.getUser().id,
								formval.sort = that.selector.parent("div").attr("data-workbench-sort"),
								formval.workbenchId = that.selector.parent("div").attr("data-workbench-id");
								formval.templateTypeName = that.settingForm.ocui[0].jq.combobox('getText');
								formval.sortMetricName = that.settingForm.ocui[1].jq.combobox('getText');
								var checkedResources = that.settingForm.find("#nodeTree").tree('getChecked');
								
								
								if(checkedResources && checkedResources.length>0){
									var checkedIds = "";
									for(var i=0;i<checkedResources.length;i++){
										if(checkedResources[i].id==-1) continue;
										checkedIds += checkedResources[i].id+",";
									}
									formval.resourceIds = checkedIds.substring(0,checkedIds.lastIndexOf(","));
								}else{
									formval.resourceIds = "";
								}
						    	oc.util.ajax({
						    		url:oc.resource.getUrl("portal/vm/topN/updateTopNSetting.htm"),
						    		data:formval,
									startProgress:null,
									stopProgress:null,
									success:function(d){
										if(d.code && d.code ==200){
											that.selector.parent("div").attr("data-workbench-name",formval.name);
											that.selector.parent("div").attr("data-workbench-template",formval.templateType);
											that.selector.parent("div").attr("data-workbench-templateName",formval.templateTypeName);
											that.selector.parent("div").attr("data-workbench-topNum",formval.topNum);
											that.selector.parent("div").attr("data-workbench-metric",formval.sortMetric);
											that.selector.parent("div").attr("data-workbench-metricName",formval.sortMetricName);
											that.selector.parent("div").attr("data-workbench-sortOrder",formval.sortOrder);
											that.selector.parent("div").attr("data-workbench-resourceIds",formval.resourceIds);
											that.selector.parent("div").attr("data-workbench-showType",formval.showType);
											that.showType = formval.showType;
											that.init();
											that._dlg.dialog("close");
										}
									}
						    	})
							}
						}
					},{
						text:"取消",
						iconCls:"fa fa-times-circle",
						handler:function(){
							that._dlg.dialog("close");
						}
					}],
				    onLoad:function(){
				    	var sortMetric,templateType,resourceIds;
				    	var domains = oc.index.getDomains();
				    	var topnSettingContentForm = oc.ui.form({ 
				    			selector : $("#topnSettingContentForm"),
				    			combobox:[
			    			          {selector : '[name=sortMetric]', fit : false,
			    			        	  valueField:'metricId',
			    			        	  textField:'name',
			    			        	  filter:function(data){
			    			        		  return data&&data.data?data.data:[];
			    			        	  },
			    			        	  onLoadSuccess:function(data){
			    			        		  setTimeout(function(){
			    			        			  for(var i=0;i<data.length;i++){
				    			        			  if(data[i].metricId==sortMetric){
				    			        				  topnSettingContentForm.ocui[0].jq.combobox("select",sortMetric);
				    			        				  break;
				    			        			  }
			    			        			  }
			    			        		  },100);
			    			        	  },
    			        				  placeholder:null
			    			          } ,{selector : '[name=topNum]', fit : false,value : 1,
			    			        	  data : [{id : '0', name : '1', selected : true},{ id : '1', name : '2' }, {  id : '2', name : '3' }, {
		    			        				  id : '3', name : '4' }, { id : '4', name : '5' }, { id : '5', name : '6' }, {  id : '6', name : '7' } ],
    			        				  placeholder:null
			    			          } ,{selector : '[name=domainId]', fit : false,value : 1,
			    			        	  data : domains,
			    			        	  placeholder : '${oc.local.ui.select.placeholder}',
			    			        	  onSelect:function(rec){
												if(rec.id!=null && rec.id!=undefined && topnSettingContentForm){
													//根据模型获取模型相应的资源树
													loadTreeData(topnSettingContentForm.ocui[3].jq.combobox('getValue'),rec.id,topnSettingContentForm.selector.find('input[name=nameOrIp]').val());
												}
											}
			    			          }        
						         ],
						         combotree:[{
						        	 	selector : '[name=templateType]', 
										placeholder:null,
										url:oc.resource.getUrl('portal/vm/topN/getVmCategory.htm'),
										onSelect:function(rec){
											if(rec.id!=null && topnSettingContentForm){
												//根据模型获取性能指标
												topnSettingContentForm.ocui[0].reLoad(oc.resource.getUrl('portal/vm/topN/getVmMetricByCategoryId.htm?categoryId='+rec.id));
												//根据模型获取模型相应的资源树
												loadTreeData(rec.id,topnSettingContentForm.ocui[2].jq.combobox('getValue'),topnSettingContentForm.selector.find('input[name=nameOrIp]').val());
											}
										},
				    			          filter:function(data){
				    			        	  return data.data;
				    			          }
			    			          }]
				    		});
				    	topnSettingContentForm.find(":input[name=nameOrIp]").fixPlaceHolder().keyup(function(e){
				    		loadTreeData(topnSettingContentForm.ocui[3].jq.combotree('getValue'),
				    				topnSettingContentForm.ocui[2].jq.combobox('getValue'),
				    				topnSettingContentForm.selector.find('input[name=nameOrIp]').val());
						});
				    	function loadTreeData(d,domainId,nameOrIp){
				    		if(!domainId)return;
				    		
					    	$("#nodeTree").tree({
					    		animate : true,
					    		dataTyep:'json',
					    		method:'post',
					    		fit:true,
					    		checkbox:true,
					    		loader : function(p, success) {
					    			var treeUrl = oc.resource.getUrl('portal/vm/topN/getVmInstances.htm');
					    			oc.util.ajax({
					    				url : treeUrl,
					    				timeout:null,
					    				data:{categoryId:d,domainId:(domainId?domainId:-1),nameOrIp:nameOrIp},
					    				successMsg : null,
//					    				startProgress:'数据加载中',
					    				failureMsg : '加载数据失败！',
					    				success : function(data) {
					    					if(data.data&&data.data.length>0){
					    						var nodes = [],node = {"id":-1,"text":"全选","checked":false,"state":"closed"};
					    						var nodeResult = [];
					    						for(var i=0;i<data.data.length;i++){
					    							if(resourceIds&&resourceIds!=null){
					    								var arr = resourceIds.split(","),selectedFlag = false;
					    								for(var m=0;m<arr.length;m++){
					    									if(parseInt(arr[m])==parseInt(data.data[i].instanceId)){
					    										selectedFlag = true;
					    										break;
					    									}
					    								}
					    								nodeResult.push({"id":data.data[i].instanceId,"text":data.data[i].name,"checked":selectedFlag});
					    							}else{
					    								nodeResult.push({"id":data.data[i].instanceId,"text":data.data[i].name,"checked":false});
					    							}
					    						}
					    						node.children = nodeResult;
					    						nodes.push(node);
					    						success(nodes);
					    					}
					    					else{success([])}
					    				}
					    			});
					    		}
					    	});
				    	}
				    	//请求topN工作台设置数据
				    	oc.util.ajax({
				    		url:oc.resource.getUrl("portal/vm/topN/getTopNSettingData.htm"),
							data:{userId:oc.index.getUser().id,
									sort:that.selector.parent("div").attr("data-workbench-sort"),
									workbenchId:that.selector.parent("div").attr("data-workbench-id")},
							startProgress:null,
							stopProgress:null,
							success:function(d){
								if(d.code && d.code ==200){
									templateType=d.data.templateType,delete d.data.templateType;
									sortMetric=d.data.sortMetric,delete d.data.sortMetric;
									resourceIds=d.data.resourceIds;
									topnSettingContentForm.ocui[3].jq.combotree('setValue',templateType);
									topnSettingContentForm.val(d.data);
									//topnSettingContentForm.val(d.data)之后重置域下的资源
									var domainId = d.data.domainId;
									if(templateType){
										loadTreeData(templateType,domainId,topnSettingContentForm.selector.find('input[name=nameOrIp]').val());
									}
								}
							}
				    	})
				    	that.settingForm = topnSettingContentForm;
				    }
				});
			},
			initDataGrid:function(config){
				var that = this,datagGridDiv = $("<div style='height: 100%;'/>").appendTo(that.selector.find("#topnPic").empty());
				//渲染datagrid
				var datagrid = oc.ui.datagrid({
					selector:datagGridDiv,
					pagination:false,
//					height:50,
					data:config.data,
					columns:[[
				         {field:'id',title:'-',hidden:true},
				         {field:'name',title:'资源名称',sortable:false,align:'center',width:80},
			        	 {field:'y',title:that.selector.parent("div").attr("data-workbench-templatename"),
				        	 sortable:false,align:'center',width:80,formatter:function(value,row,index){
				        		 return null==value?"N/A":(value+row.unit);
				        	 }
				         }
				     ]]
				});
			},
			initChart:function(config){
				var that = this;
				that.selector.find("#topnPic").empty();
				var SetEveryOnePointColor =function(chart) {            
			        //获得第一个序列的所有数据点
			        var pointsList = chart.series[0].points;
			        //遍历设置每一个数据点颜色
			        for (var i = 0; i < pointsList.length; i++) {
			            chart.series[0].points[i].update({
			                color: {
			                    linearGradient: { x1: 0, y1: 0, x2: 1, y2: 0 }, //横向渐变效果 如果将x2和y2值交换将会变成纵向渐变效果
			                    stops: [
			                            [0, Highcharts.Color(that.color[i>4?i-5:i]).setOpacity(1).get('rgba')],
			                            [0.4,that.highlightColor[i>4?i-5:i]],
			                            [0.6,that.highlightColor[i>4?i-5:i]],
			                            [1, Highcharts.Color(that.color[i>4?i-5:i]).setOpacity(1).get('rgba')]
			                        ]  
			                }
			            });
			        }
			    }
				var tickInterval = 20,maxD=0;
				for(var i=0;i<config.data.length;i++){
					if(null!=config.data[i].y && parseFloat(config.data[i].y)>maxD){
						maxD=parseInt(config.data[i].y);
					}
				}
				if(maxD>100){
					tickInterval = Math.floor(maxD/5);
				}
				that.selector.find("#topnPic").highcharts({
					chart : {
						type : 'column'
					},
					xAxis : {
						title : {
							enabled : false,
						},
						lineColor : Highcharts.theme.vmColumlineColor,
						lineWidth : 2,
						categories:config.categories
					},
					yAxis : {
						min:0,//Y轴最小值
						max:maxD>100?maxD:100,//Y轴最大值
						title : {
							enabled : false
						},
						labels:{
							style:{
								color:Highcharts.theme.vmyAxisColor
							}
						},
						lineColor :Highcharts.theme.vmColumlineColor,
						lineWidth : 2,
						tickInterval: tickInterval,//Y轴刻度
//						tickPixelInterval:20,
						tickColor:Highcharts.theme.vmColumtickColor,
			            tickWidth:6,
			            tickLength:6,
						gridLineWidth:1,
						gridLineColor:Highcharts.theme.vmgridLineColor
//			            tickPosition:'inside'
					},
					title :{
						text:''
					},
					legend: {
						enabled : false
					},
					tooltip : {
						formatter : function() {//data-workbench-metricName
							return "资源名称："+this.key+"<br>"+that.selector.parent("div").attr("data-workbench-templatename")+"："+this.y+(config.data[0]?config.data[0].unit:"");
						}
					},
					plotOptions: {
	                    column: {
	                        pointPadding:0.2,
	                        borderWidth: 1,
	                        borderColor:Highcharts.theme.vmColumborderColor
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
					this.showType=="0"?this.initChart(this.loadData):this.initDataGrid(this.loadData);
				}else{
					this.init();
				}
			}
	};
	
	
	oc.vm.workbench.topn=function(top){
		return new VmTopN(top);;
	};
	
	var topnChart =oc.vm.workbench.topn(5);
	
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