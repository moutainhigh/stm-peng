(function($){
	function changeSkin(){
		
	}
	changeSkin.prototype = {
		constructor : changeSkin,
		init : function(){
			var that = this;
			oc.util.ajax({
				url:oc.resource.getUrl('system/skin/get.htm'),
				successMsg:null,
				failureMsg:'',
				success:function(d){
					var data=$.parseJSON(d.data);
					var skin = data.selected;
					var skin_easyui = $("#skin_easyui"), skin_oc = $("#skin_oc"), skin_topo = $("#skin_topo"), skin_icon = $("#skin_icon"),
									skin_simple = $("#skin_simple"), skin_newSkin=$("#skin_newSkin"),skin_topSkin=$("#skin_topSkin"),skin_main=$("#skin_main"),
									skin_login=$("#skin_login"),skin_menu=$("#skin_menu");
//					debugger;
					if(skin != null && skin != undefined && skin != ""){
						switch (skin) {
						case "darkgreen":
						//	skin_easyui.attr("href", "third/jquery-easyui-1.4/themes/default/easyui.css");
							skin_topo.attr("href", "themes/default/css/topo.css");
							skin_topSkin.attr("href","themes/default/css/Skin.css");
							skin_simple.attr("href", "themes/default/simple/css/simple.css");
							skin_main.attr("href","themes/default/css/main.css");
							skin_newSkin.attr("href","themes/default/css/Newskin.css");
							skin_icon.attr("href", "themes/default/css/icon.css");
							skin_oc.attr("href", "themes/default/css/oc.css");
							skin_login.attr("href","themes/default/css/login.css");

							break;
						default:
							skin_easyui.attr("href", "third/jquery-easyui-1.4/themes/" + skin + "/easyui.css");
							skin_topo.attr("href", "themes/" + skin + "/css/topo.css");
							skin_topSkin.attr("href","themes/"+skin+"/css/Skin.css");
							skin_simple.attr("href", "themes/" + skin + "/simple/css/simple.css");
							skin_main.attr("href","themes/"+skin+"/css/main.css");
							skin_icon.attr("href", "themes/" + skin + "/css/icon.css");
							skin_oc.attr("href", "themes/" + skin + "/css/oc.css");
							skin_newSkin.attr("href","themes/"+skin+"/css/Newskin.css");
							skin_login.attr("href","themes/"+skin+"/css/login.css");
							break;
						}
						oc.index.getSystemLogo(function(url){
							$(".header .header-left").css("background","url("+url+")  no-repeat");
						});
					}
				
					that.initHighchartTheme(skin);
				}
			});
		},
		initHighchartTheme : function(skin){
		//	debugger;
			switch (skin) {
			case "blue":
				this.initHighchartThemeBlue();
				break;
			case "red":
				this.initHighchartThemeRed();
				break;
			default:
				this.initHighchartThemeDefault();
				break;
			}
			Highcharts.setOptions(Highcharts.theme);
		},
		initHighchartThemeDefault : function(){
			Highcharts.theme = {
				currentSkin : 'default',
				reportTitleColor : '#23BB00',
				reportPieNoAlarm : 'themes/default/images/comm/table/no-alarm.png',
				reportColumnColor : '#790306,#6A0676,#0D366E,#8F541A,#2A5D1A,#275E59,#586A2C,#544775,#2E3061,#163C47',
				topnColumnColor : ["#8B0504","#BD4910","#AD9701","#027D05","#014486"],
				topnColumnHighlightColor : ['rgb(188, 9, 6)','rgb(209, 102, 35)','rgb(211, 205, 1)','rgb(5, 194, 10)','rgb(2, 101, 207)'],
				topnLineColor : '#18370B',
				topnTickColor : '#13790e',
				//业务泳道名字背景
				bizLaneTitleBgColor:'#17C27C',
				bizTextColor:"#666",
				//业务泳道名称右边界线
				bizLaneTitleRightBorderColor:'#17C27C',
				//泳道边界线
				bizLaneBorderColor:'#17C27C',
				
				/***************拓扑地图*********************/
				topoMapfillColor:'rgba(0,0,0,0)',
				topoMapstrokeColor:'#ADD8F7',
				topoMapfontColor:'#e1e8ed',
				
				/*********topn柱状图颜色begin**********/
				topnRedColor:'#820502',
				topnRedBorderColor:'#a40302',
				topnRedWearColor:'#b90908',
				topnOrangeColor:'#953502',
				topnOrangeBorderColor:'#a93d02',
				topnOrangeWearColor:'#d16422',
				topnYellowColor:'#958001',
				topnYellowBorderColor:'#a99d03',
				topnYellowWearColor:'#d1cc02',
				topnGreenColor:'#017d03',
				topnGreenBorderColor:'#019505',
				topnGreenWearColor:'#04c209',
				topnGrayColor:'#7a7979',
				topnGrayBorderColor:'#8c8b8b',
				topnGrayWearColor:'#bfbfbf',
				/*********topn柱状图颜色end**********/
				
		/*		resourceProgressBorderColor : '#1B1E20',
				resourceProgressFillColor : '#1B1E20',
				resourceLitMeterBorderColor : '#1B1E20',
				resourceLitMeterBackgroundColor : '#506070',
				resourceLitMeterFillBorderColor : '#1B1E20',
				resourceLitMeterFillBackgroundColor : '#1B1E27',*/
				/**************资源详情界面颜色start********************/
				resourceDetailMetricColors:["#17c27c","#eee000","#f09722","#e95e48"],//指标颜色 正常-警告-严重-致命
				resourceDetailNoAlarm:"#35a6c5",//无告警时，告警块边框border颜色
				resourceDetailNoAlarmFontColor:"#f9fafc",//无告警时告警块字体颜色
				meterAndStorageBackgroundPanelColor:"#0b1c31",
				meterAndStorageFillStrokeColor:"#0f3566",
				//区域图
				areasplineStopColor:[  [0,'#d0f2fe'],
			                                [0.8, '#b2dff2']],//区域渐变色
			    areasplinegsubTitleColor:"#666",//标题字体颜色
			    areasplineColor:["rgba(28,139,196,.2)"],//区域图顶部加粗线颜色
				areasplinegridLineColor:"rgba(0,0,0,0)",//x轴刻度线颜色，使其透明不显示
				areasplinelineColor:"#a4a9b1",//坐标轴线颜色
				areasplinelabelsColor:"#666",//坐标轴下字体颜色
				areasplinetickColor:"#a4a9b1",//刻度线颜色
				/******资源总览外的区域图特殊属性start(echart图)*************/
				areastartColor:"#2894FF",//单个线图定义的默认区域颜色值
				areaendColor:"rgba(0, 136, 212, 0.3)",//子资源0.8处颜色值
				areashadowColor:"rgba(0, 0, 0, 0.1)",//子资源区域图阴影颜色值
				areaitemColor:"#2894FF",//子资源线条颜色
				areaitemborderColor:"#2894FF",//子资源线条颜色
				/********资源总览外的区域图特殊属性 end*********/
				//bar
				bargridLineColor:"#a4a9b1",//轴刻度线颜色
				barlineColor:"#a4a9b1",//轴线颜色
				barlabelsColor:"#666",//字体颜色
				//温度计
				
				//gauge 仪表盘
				gaugetextColor:"#666",//仪表盘底部字体颜色
				
				//temp温度计
				tempTextcolor:"#666",
				tempborderColor:"#ccc",
				
				resourceLitMeterFillBackgroundColor : '#fff',//圆圈中心背景颜色
				resourceLitMeterPanelBackgroundColor:"#e0e5e9",//圆圈底色
				//pie单饼图
				pieitemColor:"#9EA0A0",
				pieactiveColor:"#9EA0A0",
				pieinactiveColor:"#9EA0A0",
				pieFontColor:"#9EA0A0",
				//mulitPie 多饼图
				pieaxisLabelColor:"#666",//饼图底部字体颜色
				pieitemColor:'#e0e5e9',//饼图圆圈底色
				/**************资源详情界面颜色end******************/
				/****************虚拟化柱状图颜色start*******************/
				vmgridLineColor:'#C4C9D2',
				vmyAxisColor:'#666',
				vmColumlineColor:"#a4a9b1",
				vmColumtickColor:"#a4a9b1",
				vmColumborderColor:"rgba(0,0,0,0)",
				vmhighlightColor:['rgb(188, 9, 6)','rgb(209, 102, 35)','rgb(211, 205, 1)','rgb(5, 194, 10)','rgb(2, 101, 207)'],
				vmColor:["#8B0504","#BD4910","#AD9701","#027D05","#014486"],
				/*****************虚拟化柱状图颜色end********************/
				/*****首页资源详情仪表盘********/
				homeSolidgaugeFontColor:"#666",
				/***************业务管理颜色start*********************/
				//业务响应速度
				bizSubTitleColor:"#666",//标题字体颜色
				bizseriesColor:"#4098e0",//柱子颜色
				bizgridColor:"#ccc",//业务响应轴线颜色
				bizlabelColor:"#999",//业务柱状图及其饼图字体颜色
				/****************业务管理颜色end********************************/
				
				
				
				colors: ["#DDDF0D", "#7798BF", "#55BF3B", "#DF5353", "#aaeeee", "#ff0066", "#eeaaee",
					"#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
				chart: {
					backgroundColor:null,
					borderWidth: 0,
					borderRadius: 0,
					plotBackgroundColor: null,
					plotShadow: false,
					plotBorderWidth: 0
				},
				title: {
					style: {
						color: '#FFFFFF',
						font: '16px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
					}
				},
				subtitle: {
					style: {
						color: '#DDDDDD',
						font: '12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
					}
				},
				credits : {
					enabled : false
				},
				exporting : {
					enabled : false
				},
				xAxis: {
					labels: {
						style: {
							color : '#FFFFFF',
							fontWeight: 'bold'
						}
					},
					title: {
						style: {
							color: '#AAA',
							font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
						}
					},
					tickWidth: 0,
					lineColor : '#0E1910',
					lineWidth : 1,
					gridLineWidth : 0
				},
				yAxis: {
					alternateGridColor: null,
					minorTickInterval: null,
					
					minorGridLineColor: 'rgba(255,255,255,0.07)',
					labels: {
						style: {
							color: '#FFFFFF',
							fontWeight: 'bold'
						}
					},
					title: {
						style: {
							color: '#FFFFFF',
							font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
						}
					},
		            tickWidth:0,
					lineWidth : 1,
	    			lineColor : '#101116',
					gridLineWidth : 0,
	                minorTickColor : '#1E212B',
	                tickColor : '#1E212B'
				},
				legend: {
					itemStyle: {
						color: '#CCC'
					},
					itemHoverStyle: {
						color: '#FFF'
					},
					itemHiddenStyle: {
						color: '#333'
					},
					navigation : {
		                activeColor: '#FFFFFF',
		                inactiveColor: '#9EA0A0'
					}
				},
				labels: {
					style: {
						color: '#CCC'
					}
				},
				tooltip: {
					backgroundColor: {
						linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
						stops: [
							[0, 'rgba(96, 96, 96, .8)'],
							[1, 'rgba(16, 16, 16, .8)']
						]
					},
					borderWidth: 0,
					style: {
						color: '#FFF'
					}
				},
				pane : {//首页仪表盘使用
					background : {
						borderColor : '#CCCCCC',//边框颜色
						backgroundColor : "#CCCCCC"//背景色
					}
				},


				plotOptions: {
					series: {
						dataLabels : {
							color : '#666'
						}
					},
					line: {
						dataLabels: {
							color: '#666'
						},
						marker: {
							lineColor:'#666'
						},
						title:{
							color:'#666'
						},
						gridLineColor:'#666',
						legend:{
							itemStyle:{
								color:'#9EA0A0'
							}
						},
						navigation:{
							activeColor:'#666',
							 inactiveColor:'#9EA0A0',
							style:{
								color:'#9EA0A0'
							}
						}
					},
					spline: {
						marker: {
							lineColor: '#333'
						}
					},
					scatter: {
						marker: {
							lineColor: '#333'
						}
					},
					candlestick: {
						lineColor: '#666'
					},
					column : {
						dataLabels:{
							color:'#666'
						},
						borderWidth : 0,
                        borderColor:'#58BBF4',
                        title:{
                        	color:'#666'
                        },
                        gridLineColor:'#000000',
                        tickColor:'#000000',
                        legend:{
                        	borderColor : '#000000',
                        	itemStyle:{
                        		color:'#9EA0A0'
                        	}
                        },
                        navigation:{
                        	style:{
                        		color:'#9EA0A0'
                        	}
                        }
					},
					solidgauge : {
						dataLabels : {
							color : "#666"
						}
					},
					gauge : {
						dial: {
							backgroundColor: '#666',
							borderColor: '#666'
						},
						pivot : {
							backgroundColor : '#666'
						},
						series:{//首页仪表盘使用
							dataLabels:{
								color:"#666"//中心字体颜色
							}	
						},
						minorTickColor:'#666',
						 tickColor: '#666',
						panel:{
							backgroundColor:'#666'//业务存储容量圆圈背景颜色
						}
					},
					pie : {
						dataLabels : {
							color:"#666"
						},
						title:{
							color:'#666'
						},
						legend:{
							itemStyle:{
								color:'#9EA0A0'
							}
						},
						navigation:{
							 activeColor:'#666',
							 inactiveColor:'#9EA0A0',
							 style:{
								 color:'#9EA0A0'
							 },
							color:"#666"
						}
					},
					bar : {
						dataLabels : {
							color:'#666'
						},
						borderColor:'#08101B'
					}
				},

				toolbar: {
					itemStyle: {
						color: '#CCC'
					}
				},

				navigation: {
					buttonOptions: {
						symbolStroke: '#DDDDDD',
						hoverSymbolStroke: '#FFFFFF',
						theme: {
							fill: {
								linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
								stops: [
									[0.4, '#606060'],
									[0.6, '#333333']
								]
							},
							stroke: '#000000'
						}
					}
				},

				// scroll charts
				rangeSelector: {
					buttonTheme: {
						fill: {
							linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
							stops: [
								[0.4, '#888'],
								[0.6, '#555']
							]
						},
						stroke: '#000000',
						style: {
							color: '#CCC',
							fontWeight: 'bold'
						},
						states: {
							hover: {
								fill: {
									linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
									stops: [
										[0.4, '#BBB'],
										[0.6, '#888']
									]
								},
								stroke: '#000000',
								style: {
									color: 'white'
								}
							},
							select: {
								fill: {
									linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
									stops: [
										[0.1, '#000'],
										[0.3, '#333']
									]
								},
								stroke: '#000000',
								style: {
									color: 'yellow'
								}
							}
						}
					},
					inputStyle: {
						backgroundColor: '#333',
						color: 'silver'
					},
					labelStyle: {
						color: 'silver'
					}
				},

				navigator: {
					handles: {
						backgroundColor: '#666',
						borderColor: '#AAA'
					},
					outlineColor: '#CCC',
					maskFill: 'rgba(16, 16, 16, 0.5)',
					series: {
						color: '#7798BF',
						lineColor: '#A6C7ED'
					}
				},

				scrollbar: {
					barBackgroundColor: {
							linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
							stops: [
								[0.4, '#888'],
								[0.6, '#555']
							]
						},
					barBorderColor: '#CCC',
					buttonArrowColor: '#CCC',
					buttonBackgroundColor: {
							linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
							stops: [
								[0.4, '#888'],
								[0.6, '#555']
							]
						},
					buttonBorderColor: '#CCC',
					rifleColor: '#FFF',
					trackBackgroundColor: {
						linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
						stops: [
							[0, '#000'],
							[1, '#333']
						]
					},
					trackBorderColor: '#666'
				},

				// special colors for some of the demo examples
				legendBackgroundColor: 'rgba(48, 48, 48, 0.8)',
				background2: 'rgb(70, 70, 70)',
				dataLabelsColor: '#444',
				textColor: '#E0E0E0',
				maskColor: 'rgba(255,255,255,0.3)'
			};
		},
		initHighchartThemeBlue : function(){
			Highcharts.theme = {
				currentSkin : 'blue',
				reportTitleColor : '#333',
				reportPieNoAlarm : 'themes/blue/images/comm/table/no-alarm.png',
				reportColumnColor : '#df592f,#5350a4,#6990c7,#be8b58,#85ca77,#44aba2,#90af45,#826db7,#5659a5,#3797b3',
				topnColumnColor : ["#69C1F3","#69C1F3","#69C1F3","#69C1F3","#69C1F3"],
				topnColumnHighlightColor : ['rgb(113, 197, 244)','rgb(113, 197, 244)','rgb(113, 197, 244)','rgb(113, 197, 244)','rgb(113, 197, 244)'],
				topnLineColor : '#CFD9DB',
				topnTickColor : '#AAC8EA',
				//业务泳道名字背景
				bizLaneTitleBgColor:'#1776b1',
				bizTextColor:"#fff",
				//业务泳道名称右边界线
				bizLaneTitleRightBorderColor:'#1b7dae',
				//泳道边界线
				bizLaneBorderColor:'#71a8c9',
				
				
				/***************拓扑地图*********************/
				topoMapfillColor:'#1166B7',
				topoMapstrokeColor:'#7abbf3',
				topoMapfontColor:'#e1e8ed',
				
				/*********topn柱状图颜色begin**********/
				topnRedColor:'#ba1612',
				topnRedBorderColor:'#910630',
				topnRedWearColor:'#de4d4d',
				topnOrangeColor:'#d88207',
				topnOrangeBorderColor:'#b76d02',
				topnOrangeWearColor:'#feab33',
				topnYellowColor:'#cbb901',
				topnYellowBorderColor:'#b7a805',
				topnYellowWearColor:'#ecdc2b',
				topnGreenColor:'#0eb00e',
				topnGreenBorderColor:'#0aa80a',
				topnGreenWearColor:'#3cdd3c',
				topnGrayColor:'#909090',
				topnGrayBorderColor:'#959595',
				topnGrayWearColor:'#c8c6c6',
				/*********topn柱状图颜色end**********/
				
			/*	resourceProgressBorderColor : '#D0D0CE',
				resourceProgressFillColor : '#D7D7D7',
				resourceLitMeterBorderColor : '#9cdefd',
				resourceLitMeterBackgroundColor : '#9cdefd',
				resourceLitMeterFillBorderColor : '#199fea',
				resourceLitMeterFillBackgroundColor : '#199fea',*/
				/**************资源详情界面颜色start********************/
				resourceDetailMetricColors:["#02bf00","#edb805","#ff7d03","#DE2022"],//指标颜色 正常-警告-严重-致命
				resourceDetailNoAlarm:"#35a6c5",//无告警时，告警块边框border颜色
				resourceDetailNoAlarmFontColor:"#f9fafc",//无告警时告警块字体颜色
				meterAndStorageBackgroundPanelColor:"#0b1c31",
				meterAndStorageFillStrokeColor:"#0f3566",
				//区域图
				areasplineStopColor:[  [0,'rgba(0, 136, 212, 1)'],
			                                [0.8, 'rgba(0, 136, 212, 0.4)']],//区域渐变色
			     areasplinegsubTitleColor:"#9bc5f7",//标题字体颜色
			     areasplineColor:["#027DC7"],//区域图顶部加粗线颜色
				areasplinegridLineColor:"rgba(0,0,0,0)",//x轴刻度线颜色，使其透明不显示
				areasplinelineColor:"#027DC7",//坐标轴线颜色
				areasplinelabelsColor:"#9bc5f7",//坐标轴下字体颜色
				areasplinetickColor:"#027DC7",//刻度线颜色
				/******资源总览外的区域图特殊属性start(echart图)*************/
				areastartColor:"#2894FF",//单个线图定义的默认区域颜色值
				areaendColor:"rgba(0, 136, 212, 0.3)",//子资源0.8处颜色值
				areashadowColor:"rgba(0, 0, 0, 0.1)",//子资源区域图阴影颜色值
				areaitemColor:"#2894FF",//子资源线条颜色
				areaitemborderColor:"#2894FF",//子资源线条颜色
				/********资源总览外的区域图特殊属性 end*********/
				
				//bar
				bargridLineColor:"#027DC7",//轴刻度线颜色
				barlineColor:"#027DC7",//轴线颜色
				barlabelsColor:"#9bc5f7",//字体颜色
				//温度计
				
				//gauge 仪表盘
				gaugetextColor:"#9bc5f7",//仪表盘底部字体颜色
				
				//temp温度计
				tempTextcolor:"#fff",
				tempborderColor:"#6caeff",
				
				resourceLitMeterFillBackgroundColor : '#0f3566',//圆圈中心背景颜色
				resourceLitMeterPanelBackgroundColor:"#0b1c31",//圆圈底色
				//pie单饼图
				pieitemColor:"#9EA0A0",
				pieactiveColor:"#9EA0A0",
				pieinactiveColor:"#9EA0A0",
				pieFontColor:"#9EA0A0",
				//mulitPie 多饼图
				pieaxisLabelColor:"#9bc5f7",//饼图底部字体颜色
				pieitemColor:'#0b1c31',//饼图圆圈底色
				/**************资源详情界面颜色end******************/
				/****************虚拟化柱状图颜色start*******************/
				vmgridLineColor:'#13467A',
				vmyAxisColor:'#6CAEFF',
				vmColumlineColor:"#6CAEFF",
				vmColumtickColor:"#6CAEFF",
				vmColumborderColor:"#000000",
				vmhighlightColor:['rgb(188, 9, 6)','rgb(209, 102, 35)','rgb(211, 205, 1)','rgb(5, 194, 10)','rgb(2, 101, 207)'],
				vmColor:["#8B0504","#BD4910","#AD9701","#027D05","#014486"],
				/*****************虚拟化柱状图颜色end********************/
				
				/*****首页资源详情仪表盘********/
				homeSolidgaugeFontColor:"#ccc",
				/***************业务管理颜色start*********************/
				//业务响应速度
				bizSubTitleColor:"#ffffff",//标题字体颜色
				bizseriesColor:"#4098e0",//柱子颜色
				bizgridColor:"rgba(210,210,210,.6)",//业务响应轴线颜色
				bizlabelColor:"white",//业务柱状图及其饼图字体颜色
				/****************业务管理颜色end********************************/
				
				
				
				colors: ["#DDDF0D", "#7798BF", "#55BF3B", "#DF5353", "#aaeeee", "#ff0066", "#eeaaee",
						"#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
					chart: {
						backgroundColor:null,
						borderWidth: 0,
						borderRadius: 0,
						plotBackgroundColor: null,
						plotShadow: false,
						plotBorderWidth: 0
					},
					title: {
						style: {
							color: '#000000',
							font: '16px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
						}
					},
					subtitle: {
						style: {
							color: '#DDDDDD',
							font: '12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
						}
					},
					credits : {
						enabled : false
					},
					exporting : {
						enabled : false
					},
					xAxis: {
						labels: {
							style: {
								color : '#FFFFFF',
								whiteSpace: 'nowrap'
							}
						},
						title: {
							style: {
								color: '#666',
								font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
							}
						},
						tickWidth: 0,
						lineColor : '#0E1910',
						lineWidth : 1,
						gridLineWidth : 0
					},
					yAxis: {
						alternateGridColor: null,
						minorTickInterval: null,
						
						minorGridLineColor: 'rgba(255,255,255,0.07)',
						labels: {
							style: {
								color: '#FFFFFF'
							}
						},
						title: {
							style: {
								color: '#AAA',
								font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
							}
						},
			            tickWidth:0,
						lineColor : '#D0D0CE',
						lineWidth : 1,
						gridLineWidth : 1,
		                minorTickColor : '#1E212B',
		                tickColor : '#1E212B',
		                gridLineColor : '#D0D9DC'
					},
					legend: {
						itemStyle: {
							color: '#222'
						},
						itemHoverStyle: {
							color: '#EEE'
						},
						itemHiddenStyle: {
							color: '#CCC'
						},
						navigation : {
			                activeColor: '#9EA0A0',
			                inactiveColor: '#9EA0A0'
						}
					},
					labels: {
						style: {
							color: '#CCC'
						}
					},
					tooltip: {
						backgroundColor: {
							linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
							stops: [
								[0, 'rgba(231, 234, 234, .8)'],
								[1, 'rgba(244, 244, 244, .8)']
							]
						},
						borderWidth: 1,
						borderColor : '#AEC2DC',
						style: {
							color: '#333'
						}
					},
					pane : {
						background : {
							borderColor : '#D0D0CE',
							backgroundColor: '#D7D7D7'
						}
					},

					plotOptions: {
						series: {
							dataLabels : {
								color : '#9EA0A0'
							}
						},
						line: {
							dataLabels: {
								color: '#666'
							},
							marker: {
								lineColor: '#333'
							},
							title:{
								color:'#333'
							},
							gridLineColor:'#FFFFFF',
							legend:{
								itemStyle:{
									color:'#9EA0A0'
								}
							},
							navigation:{
								activeColor:'#FFFFFF',
								 inactiveColor: '#9EA0A0',
								style:{
									color:'#9EA0A0'
								}
							}
						},
						spline: {
							marker: {
								lineColor: '#333'
							}
						},
						scatter: {
							marker: {
								lineColor: '#333'
							}
						},
						candlestick: {
							lineColor: 'white'
						},
						column : {
							dataLabels:{
								color:'#FFFFFF'
							},
							borderWidth : 0,
	                        borderColor:'#58BBF4',
	                        title:{
	                        	color:'#333'
	                        },
	                        gridLineColor:'#000000',
                        	tickColor:'#000000',
	                        legend:{
	                        	borderColor:'#000000',
	                        	itemStyle:{
	                        		color:'#9EA0A0'
	                        	}
	                        },
	                        navigation:{
	                        	style:{
	                        		color:'#9EA0A0'
	                        	}
	                        }
						},
						solidgauge : {
							dataLabels : {
								color : "#000000"
							}
						},
						gauge : {
							dial: {
								backgroundColor: '#ABABAB',
								borderColor: '#ABABAB'
							},
							pivot : {
								backgroundColor : '#ABABAB'
							},
							series:{
								dataLabels:{
									color:"white"
								}	
							},
							
								minorTickColor:'#ABABAB',
								 tickColor: '#ABABAB',
						
							panel:{
								backgroundColor:'#ABABAB'//业务存储容量圆圈背景颜色
							}
							
						},
						
					
						pie : {
							dataLabels : {
								"color" : "#FFFFFF"
							},
							title:{
								color:'#333'
							},
							legend:{
								itemStyle:{
									color:'#9EA0A0'
								}
							},
							navigation:{
								 activeColor:'#FFFFFF',
								 inactiveColor:'#9EA0A0',
								 style:{
									 color:'#9EA0A0'
								 }
							}
						},
						bar : {
							dataLabels : {
								color:'#FFFFFF'
							},
							borderColor:'#FFFFFF'
						}
					},

					toolbar: {
						itemStyle: {
							color: '#CCC'
						}
					},

					navigation: {
						buttonOptions: {
							symbolStroke: '#DDDDDD',
							hoverSymbolStroke: '#FFFFFF',
							theme: {
								fill: {
									linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
									stops: [
										[0.4, '#606060'],
										[0.6, '#333333']
									]
								},
								stroke: '#000000'
							}
						}
					},

					// scroll charts
					rangeSelector: {
						buttonTheme: {
							fill: {
								linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
								stops: [
									[0.4, '#888'],
									[0.6, '#555']
								]
							},
							stroke: '#000000',
							style: {
								color: '#CCC',
								fontWeight: 'bold'
							},
							states: {
								hover: {
									fill: {
										linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
										stops: [
											[0.4, '#BBB'],
											[0.6, '#888']
										]
									},
									stroke: '#000000',
									style: {
										color: 'white'
									}
								},
								select: {
									fill: {
										linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
										stops: [
											[0.1, '#000'],
											[0.3, '#333']
										]
									},
									stroke: '#000000',
									style: {
										color: 'yellow'
									}
								}
							}
						},
						inputStyle: {
							backgroundColor: '#333',
							color: 'silver'
						},
						labelStyle: {
							color: 'silver'
						}
					},

					navigator: {
						handles: {
							backgroundColor: '#666',
							borderColor: '#AAA'
						},
						outlineColor: '#CCC',
						maskFill: 'rgba(16, 16, 16, 0.5)',
						series: {
							color: '#7798BF',
							lineColor: '#A6C7ED'
						}
					},

					scrollbar: {
						barBackgroundColor: {
								linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
								stops: [
									[0.4, '#888'],
									[0.6, '#555']
								]
							},
						barBorderColor: '#CCC',
						buttonArrowColor: '#CCC',
						buttonBackgroundColor: {
								linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
								stops: [
									[0.4, '#888'],
									[0.6, '#555']
								]
							},
						buttonBorderColor: '#CCC',
						rifleColor: '#FFF',
						trackBackgroundColor: {
							linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
							stops: [
								[0, '#000'],
								[1, '#333']
							]
						},
						trackBorderColor: '#666'
					},

					// special colors for some of the demo examples
					legendBackgroundColor: 'rgba(48, 48, 48, 0.8)',
					background2: 'rgb(70, 70, 70)',
					dataLabelsColor: '#444',
					textColor: '#E0E0E0',
					maskColor: 'rgba(255,255,255,0.3)'
				};
			},
		initHighchartThemeRed : function(){
				Highcharts.theme = {
					currentSkin : 'red',
					reportTitleColor : '#333',
					reportPieNoAlarm : 'themes/blue/images/comm/table/no-alarm.png',
					reportColumnColor : '#df592f,#5350a4,#6990c7,#be8b58,,#85ca77,#44aba2,#90af45,#826db7,#5659a5,#3797b3',
					topnColumnColor : ["#69C1F3","#69C1F3","#69C1F3","#69C1F3","#69C1F3"],
					topnColumnHighlightColor : ['rgb(113, 197, 244)','rgb(113, 197, 244)','rgb(113, 197, 244)','rgb(113, 197, 244)','rgb(113, 197, 244)'],
					topnLineColor : '#CFD9DB',
					topnTickColor : '#AAC8EA',
					//业务泳道名字背景
					bizLaneTitleBgColor:'#1776b1',
					//业务泳道名称右边界线
					bizLaneTitleRightBorderColor:'#1b7dae',
					//泳道边界线
					bizLaneBorderColor:'#71a8c9',
					
					/*********topn柱状图颜色begin**********/
					topnRedColor:'#820502',
					topnRedBorderColor:'#a40302',
					topnRedWearColor:'#b90908',
					topnOrangeColor:'#953502',
					topnOrangeBorderColor:'#a93d02',
					topnOrangeWearColor:'#d16422',
					topnYellowColor:'#958001',
					topnYellowBorderColor:'#a99d03',
					topnYellowWearColor:'#d1cc02',
					topnGreenColor:'#017d03',
					topnGreenBorderColor:'#019505',
					topnGreenWearColor:'#04c209',
					topnGrayColor:'#7a7979',
					topnGrayBorderColor:'#8c8b8b',
					topnGrayWearColor:'#bfbfbf',
					/*********topn柱状图颜色end**********/
					
					resourceProgressBorderColor : '#D0D0CE',
					resourceProgressFillColor : '#D7D7D7',
					resourceLitMeterBorderColor : '#9cdefd',
					resourceLitMeterBackgroundColor : '#9cdefd',
					resourceLitMeterFillBorderColor : '#199fea',
					resourceLitMeterFillBackgroundColor : '#199fea',
					colors: ["#DDDF0D", "#7798BF", "#55BF3B", "#DF5353", "#aaeeee", "#ff0066", "#eeaaee",
							"#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
						chart: {
							backgroundColor:null,
							borderWidth: 0,
							borderRadius: 0,
							plotBackgroundColor: null,
							plotShadow: false,
							plotBorderWidth: 0
						},
						title: {
							style: {
								color: '#000000',
								font: '16px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
							}
						},
						subtitle: {
							style: {
								color: '#DDDDDD',
								font: '12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
							}
						},
						credits : {
							enabled : false
						},
						exporting : {
							enabled : false
						},
						xAxis: {
							labels: {
								style: {
									color : '#666'
								}
							},
							title: {
								style: {
									color: '#AAA',
									font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
								}
							},
							tickWidth: 0,
							lineColor : '#0E1910',
							lineWidth : 1,
							gridLineWidth : 0
						},
						yAxis: {
							alternateGridColor: null,
							minorTickInterval: null,
							
							minorGridLineColor: 'rgba(255,255,255,0.07)',
							labels: {
								style: {
									color: '#666'
								}
							},
							title: {
								style: {
									color: '#666',
									font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
								}
							},
				            tickWidth:0,
							lineColor : '#D0D0CE',
							lineWidth : 1,
							gridLineWidth : 1,
			                minorTickColor : '#1E212B',
			                tickColor : '#1E212B',
			                gridLineColor : '#D0D9DC'
						},
						legend: {
							itemStyle: {
								color: '#222'
							},
							itemHoverStyle: {
								color: '#EEE'
							},
							itemHiddenStyle: {
								color: '#CCC'
							},
							navigation : {
				                activeColor: '#9EA0A0',
				                inactiveColor: '#9EA0A0'
							}
						},
						labels: {
							style: {
								color: '#CCC'
							}
						},
						tooltip: {
							backgroundColor: {
								linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
								stops: [
									[0, 'rgba(231, 234, 234, .8)'],
									[1, 'rgba(244, 244, 244, .8)']
								]
							},
							borderWidth: 1,
							borderColor : '#AEC2DC',
							style: {
								color: '#333'
							}
						},
						pane : {
							background : {
								borderColor : '#D0D0CE',
								backgroundColor: '#D7D7D7'
							}
						},

						plotOptions: {
							series: {
								dataLabels : {
									color : '#555555'
								}
							},
							line: {
								dataLabels: {
									color: '#CCC'
								},
								marker: {
									lineColor: '#333'
								}
							},
							spline: {
								marker: {
									lineColor: '#333'
								}
							},
							scatter: {
								marker: {
									lineColor: '#333'
								}
							},
							candlestick: {
								lineColor: 'white'
							},
							column : {
								borderWidth : 0,
		                        borderColor:'#58BBF4'
							},
							solidgauge : {
								dataLabels : {
									color : "#000000"
								}
							},
							gauge : {
								dial: {
									backgroundColor: '#ABABAB',
									borderColor: '#ABABAB'
								},
								pivot : {
									backgroundColor : '#ABABAB'
								}
							},
							pie : {
								dataLabels : {
									"color" : "#000000"
								}
							},
							bar : {
								dataLabels : {
									color:'#FFFFFF'
								},
								borderColor:'#FFFFFF'
							}
						},

						toolbar: {
							itemStyle: {
								color: '#CCC'
							}
						},

						navigation: {
							buttonOptions: {
								symbolStroke: '#DDDDDD',
								hoverSymbolStroke: '#FFFFFF',
								theme: {
									fill: {
										linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
										stops: [
											[0.4, '#606060'],
											[0.6, '#333333']
										]
									},
									stroke: '#000000'
								}
							}
						},

						// scroll charts
						rangeSelector: {
							buttonTheme: {
								fill: {
									linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
									stops: [
										[0.4, '#888'],
										[0.6, '#555']
									]
								},
								stroke: '#000000',
								style: {
									color: '#CCC',
									fontWeight: 'bold'
								},
								states: {
									hover: {
										fill: {
											linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
											stops: [
												[0.4, '#BBB'],
												[0.6, '#888']
											]
										},
										stroke: '#000000',
										style: {
											color: 'white'
										}
									},
									select: {
										fill: {
											linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
											stops: [
												[0.1, '#000'],
												[0.3, '#333']
											]
										},
										stroke: '#000000',
										style: {
											color: 'yellow'
										}
									}
								}
							},
							inputStyle: {
								backgroundColor: '#333',
								color: 'silver'
							},
							labelStyle: {
								color: 'silver'
							}
						},

						navigator: {
							handles: {
								backgroundColor: '#666',
								borderColor: '#AAA'
							},
							outlineColor: '#CCC',
							maskFill: 'rgba(16, 16, 16, 0.5)',
							series: {
								color: '#7798BF',
								lineColor: '#A6C7ED'
							}
						},

						scrollbar: {
							barBackgroundColor: {
									linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
									stops: [
										[0.4, '#888'],
										[0.6, '#555']
									]
								},
							barBorderColor: '#CCC',
							buttonArrowColor: '#CCC',
							buttonBackgroundColor: {
									linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
									stops: [
										[0.4, '#888'],
										[0.6, '#555']
									]
								},
							buttonBorderColor: '#CCC',
							rifleColor: '#FFF',
							trackBackgroundColor: {
								linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
								stops: [
									[0, '#000'],
									[1, '#333']
								]
							},
							trackBorderColor: '#666'
						},

						// special colors for some of the demo examples
						legendBackgroundColor: 'rgba(48, 48, 48, 0.8)',
						background2: 'rgb(70, 70, 70)',
						dataLabelsColor: '#444',
						textColor: '#E0E0E0',
						maskColor: 'rgba(255,255,255,0.3)'
					};
				}
	};
	new changeSkin().init();
})(jQuery);