/**
 * 管理者模式公用ＪＳ
 */
(function($){
	
	/**
	 * Highchart柱状图
	 * cfg:{
	 * 	selector 对象选择器
	 * 	option highchart附加选项
	 * }
	 * */
	function SimpleManagerColumn(cfg){
		if(cfg.selector!=undefined && cfg.selector!=null ){
			this.selector = (typeof cfg.selector == 'string') ? $(cfg.selector): cfg.selector;
			if(cfg.option){
				this._default = $.extend(true,{},this._default,cfg.option);
			}
			this._cfg = cfg;
			this.initChart();
		}
	}
	SimpleManagerColumn.prototype={
			constructor:SimpleManagerColumn,
			selector:undefined,
			_cfg:undefined,
			chart:undefined,
			initChart:function(){
				var that = this;
				that.chart = that.selector.highcharts(Highcharts.merge(that._default,{
					chart:{
						width:that._cfg.width,
						height:that._cfg.height
					},
					title:{
						text:that._cfg.title
					},
					xAxis:{
						categories:that._cfg.categories,
						labels:{
			                formatter:function(){
			                	var str = this.value;
			                	return str.length>7?(str.substring(0,7)+"..."):str;
			                }
			            }
					},
					tooltip : {
				    	enabled:true,
						formatter : function() {
							return this.key+that._cfg.title+":"+this.y;
						}
					},
					plotOptions: {
			            column: {
			                stacking: 'normal'
			            }
			        },
					series:[{
						name:'正常',
						data:that._cfg.normalData,
						stack: 'male',
						color:'#05C309'
					},{
						name:'超标',
						data:that._cfg.overData,
						stack: 'male',
						color:'#B70806'
					}]
				}),function(chart){
					var seriesList = chart.series;
					for(var i=0;i<seriesList.length;i++){
						var ocolor = "#0A8E0C";
			        	var hcolor = "#05C309";
			        	if(seriesList[i].color=='#05C309'){
			        		ocolor = "#0A8E0C"
			        		hcolor = '#05C309';
			        	}else if(seriesList[i].color=='#B70806'){
			        		ocolor = "#871B17";
			        		hcolor = '#B70806';
			        	}
			        	var pointsList = seriesList[i].points;
			        	//遍历设置每一个数据点颜色
				        for (var j = 0; j < pointsList.length; j++) {
				        	pointsList[j].update({
				                color: {
				                    linearGradient: { x1: 0, y1: 0, x2: 1, y2: 0 }, //横向渐变效果 如果将x2和y2值交换将会变成纵向渐变效果
				                    stops: [
				                            [0, ocolor],
				                            [0.4,hcolor],
				                            [0.6,hcolor],
				                            [1,ocolor]
				                        ]  
				                },
				                borderColor:ocolor
				            });
				        }
					}
				});
			},
			_default:{
		        chart: {
		            type: 'column'
		        },
		        title:{
		        	style:{
		                color:'#fff',
		                fontSize:'14px',
		                fontWeight:'bold'
		            }
		        },
		        xAxis: {
		            tickWidth:0,
		            lineColor : "#a2b6a7",
		            labels: {
						style: {
							color: '#fff',
							fontWeight:'normal'
						}
					}
		        },
		        yAxis: {
		            min:0,//Y轴最小值
//		            max:100,//Y轴最大值
		            title:null,
//		            tickInterval: 20,//Y轴刻度
		            lineColor : "#a2b6a7",
		            labels:{
		                formatter:function(){
		                	return this.value;
		                }
		            },
		            labels: {
						style: {
							color: '#fff',
							fontWeight:'normal'
						}
					},
					gridLineWidth : 1,
					gridLineColor : '#D0D9DC'
		        },
		        legend:{
		            enabled:false,
		            verticalAlign:'top',
		            align:'right',
		            floating:true,
		            layout:'vertical',
		            x:-10,
		            y:30
		        },
		        credits : {
					enabled : false
				},
				exporting : {
					enabled : false
				},
		        plotOptions: {
		        	column: {
		                pointPadding: 0.2,
		                borderWidth: 1,
		                borderColor:'#138E13'
		            }
		        }
		    }
	}
	
	oc.ns("oc.simple.manager.report");
	oc.simple.manager.report={
		columnchart:function(cfg){
			return new SimpleManagerColumn(cfg);
		}
	} 
})(jQuery);