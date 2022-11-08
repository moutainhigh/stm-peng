(function($) {
	var currTitle=null;
	function ResourceChart() {
		this.selector = $("#oc-module-home-workbench-resource").attr("id",oc.util.generateId());
		this.container = this.selector.find("#oc-module-home-resource-container").attr("id",oc.util.generateId());
		var workbenchSort = this.selector.parent("div").attr("data-workbench-sort");
		currTitle = this.selector.parent("div").attr("data-workbench-title");
		oc.home.workbench.userWorkbenches[workbenchSort]=this;
//		this.init();
		this.initResourceTab();
	}
	ResourceChart.prototype = {
		selector : undefined,
		constructor : ResourceChart,
		container:0,
		domainId:0,
		resource : 'Host',
		loadData:undefined,
		setResource : function(value) {
			this.resource = value;
			this.init();
		},
		init : function() {
			var that = this;
			this.domainId = oc.home.workbench.domainId;
			oc.util.ajax({
				url : oc.resource.getUrl("home/workbench/resource/getAll.htm"),
				data : {typeId : that.resource,domainId: that.domainId},
				startProgress:false,
				stopProgress:false,
				success : function(d) {
					if (d.code && d.code == 200) {
						var data = d.data||{};
						that.loadData = data;
						
						var datas = data.data,
						categories = data.categories,
						resultCategories = [],
						resultDatas = [];
						for(var i=0,len=datas.length; i<len; i++){
							if(datas[i]){
								resultCategories.push(categories[i]);
								resultDatas.push(datas[i]);
							}
						}
						data.categories = resultCategories;
						data.data = resultDatas;
						that.initChart(resultCategories, resultDatas);
						$(".sub-resource-rpic").text(data.total + "个");
					}
				}

			});
		},

		initChart : function(categories, data) {
			if(currTitle!='screen'){
				this.container.css('height',categories.length>6 ? categories.length<<5 : '100%');
			}
			var SetEveryOnePointColor =function(chart) { 
		        //获得第一个序列的所有数据点
		        var pointsList = chart.series[0].points;
		        //遍历设置每一个数据点颜色
		        for (var i = 0; i < pointsList.length; i++) {
		            chart.series[0].points[i].update({
		                color: {
		                    linearGradient: { x1: 0, y1: 0, x2: 1, y2: 0 }, //横向渐变效果 如果将x2和y2值交换将会变成纵向渐变效果
		                    stops: [
		                            [0, 'rgb(12, 214, 8)'],
		                            [0.4,'rgb(16, 139, 12)'],
		                            [0.6,'rgb(16, 139, 12)'],
		                            [1, 'rgb(12, 214, 8)']
		                        ]  
		                }
		            });
		        }
		    }
			if(categories){
				for(var i=0;i<categories.length;i++){
					categories[i] = categories[i].replace(/\s/ig,'');
				}
			}
			this.container.highcharts(
							{
								colors : [ '#458B00' ],
								chart : {
									reflow : true,
									type : 'bar',
									backgroundColor : 'rgba(0,0,0,0)'
								},
								title : {
									text : null
								/* 设置标题为空 */
								},
								exporting : {
									enabled : false
								},/* chart context menu设置为不显示 */
								xAxis : {
									labels : {/* 设置纵坐标字体颜色 */
										style : {
											align : 'left'
										}
									},
									categories : categories,
									title : {
										text : null
									},
									lineWidth : 0
								},
								yAxis : {
									title : {
										text : null,
										align : 'high'
									},
									labels : {
										enabled : false,
										style : {
											color : '#CE0000'
										}
									},
									lineWidth : 0,
									gridLineWidth : 0
								},
								tooltip : {
									valueSuffix : ' 台'
								},
								plotOptions : {
									bar : {
										dataLabels : {
											enabled : true
											,x:28
											
										},
										pointWidth : '15',
										borderWidth:3
									}
								},
								legend : {
									enabled : false,
									layout : 'vertical',
									align : 'right',
									verticalAlign : 'top',
									x : -40,
									y : 100,
									floating : true,
									borderWidth : 1,
									backgroundColor : (Highcharts.theme
											&& Highcharts.theme.legendBackgroundColor || '#FFEC8B'),
									shadow : true
								},
								credits : {
									enabled : false
								},
								series : [ {
									name : '数量',
									data : data,
									style:{
									},
								} ]
							},function (chart) {
				                SetEveryOnePointColor(chart);
				            });

		},// end

		initResourceTab : function() {
			var that = this;
			oc.home.workbench.homeResourceTab({
				selector : that.selector.find("#resource_tab"),
				tabs : [ {
					text : "主机",
					onClick : function() {
						that.setResource("Host");
					}
				}, {
					text : "网络",
					onClick : function() {
						that.setResource("NetworkDevice");
					}
				}, {
					text : "应用",
					onClick : function() {
						that.setResource("App");
					}
				} ]
			});
		},
		reLoad:function(){
			this.init();
		},
		render:function(){
			if(this.loadData!=undefined){
				this.initChart(this.loadData.categories, this.loadData.data);
			}else{
				this.init();
			}
		}
	};

	var resourceChar = new ResourceChart();

	oc.home.workbench.resourceHighchart = function() {
		return resourceChar;
	};
	
//	resourceChar.selector.bind(oc.events.resize, function() {
//		resourceChar.render();
//	});

	resourceChar.selector.find("#refrashResource").click(function() {
		resourceChar.reLoad();
	});

})(jQuery);