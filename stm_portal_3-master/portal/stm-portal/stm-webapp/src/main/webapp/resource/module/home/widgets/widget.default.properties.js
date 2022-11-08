(function() {
	var properties = {};

	//业务视图的配置信息
	properties.businessView = {

	}

	//业务能力的配置信息
	properties.capacityStatisticView = {
		pieColors: ['#112a50', '#0a9ce7'],
		centerIcoColor: 'white',
		titleTextStyle: {
			color: '#fff',
		},
		subtextStyle: {
			color: 'blue'
		}
	}

	//关注指标的配置信息
	properties.focusMetric = {
		metricCircle: {
			CRITICAL: {
				color: '#e95e48'
			},
			SERIOUS: {
				color: '#f09722'
			},
			WARN: {
				color: '#eee000'
			},
			NORMAL: {
				color: '#17c27c'
			},
			default: {
				color: '#17c27c'
			}
		},
		//以时间为坐标轴折线图
		plotLineChartOption: {
			title: {
				text: '单位:',
				textStyle: {
					color: '#s',
					fontSize: 10,
				},
				subtext: '',
				show: false
			},
			color: ['#ff6e3c', '#17C27C', '#EEE000', '#0C8BE3', '#837AFE'],
			grid: {
				left: '3%',
				right: '4%',
				bottom: '3%',
				containLabel: true
			},
			legend: {
				data: [],
				textStyle: {
					color: '#666',
					fontWeight: 'normal'
				}
			},
			tooltip: {
				trigger: 'cross',
				axisPointer: {
					animation: true
				},
				confine: true,
				formatter: function(params) {
					var t = '';
					var lab = '';

					for(var i = params.length - 1; i >= 0; i--) {
						var p = params[i];
						if(!p)
							continue;
						if(p.name && !isNaN(p.name)) {
							t = (new Date(p.name)).stringify() + '<br/>';
						}
						if(p.data) {
							var fa = '--';
							if(p.data.formatValue != undefined && p.data.formatValue != '') {
								fa = p.data.formatValue;
							}
							var cricle = "<span style='color:" + p.color + ";'>●</span>";
							lab += cricle + p.seriesName + "：" + fa + '<br/>';
						} else {
							var fa = '--';
							fa = "0";
							var cricle = "<span style='color:" + p.color + ";'>●</span>";
							lab += cricle + p.seriesName + "：" + fa + '<br/>';
						}
					}

					return t + lab;
				}
			},
			xAxis: {
				type: 'time',
				splitLine: {
					show: true,
					lineStyle:{
						color:'#c4c9d2'
					}
				},
				axisLine: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisTick: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisLabel: {
					show: true,
					textStyle: {
						color: '#666'
					},
					formatter: function(v) {
						var b = new Date(v);
						if(b) {
							var h = b.getHours();
							h = (h >= 10) ? h : ('0' + h);
							var m = b.getMinutes();
							m = (m >= 10) ? m : ('0' + m);
							b = [h, m].join(':') + '\n' + [b.getMonth() + 1, b.getDate()].join('-');
						}
						return b;
					}
				}
			},
			yAxis: {
				type: 'value',
				boundaryGap: [0, '100%'],
				splitLine: {
					show: false
				},
				axisLine: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisTick: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisLabel: {
					show: true,
					formatter: '{value}',
					textStyle: {
						color: '#666'
					}
				},
				color: '#666'
			},
			series: [{
				symbol: 'circle',
				showSymbol: false,
				name: '',
				type: 'line',
				hoverAnimation: false,
				data: []
			}]
		},
		//当前是面积视图
		areaStyle: {
			normal: {
				color: 'rgba(233,94,72,.7)'
				/*color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
				    offset: 0,
				    color: 'rgb(255, 158, 68)'
				}, {
				    offset: 1,
				    color: 'rgb(255, 70, 131)'
				}])//*/
			}
		},
		//雷达图
		plotRadarChartv2Option: {
			title: {
				text: '雷达图',
				textStyle: {
					color: '#666'
				},
				show: false
			},
			color: ['#0c547c', '#0d8e34'],
			tooltip: {
				show: true,
				confine: true,
				trigger: 'item',
				formatter: function(params) {
					var out = '';
					var data = params.data;
					var value = data.rawValue;
					var valueUnit = data.valueUnit;
					var inctor = data.indicator;
					for(var j = 0; j < value.length; j++) {
						out += inctor[j].name + ':' + properties.UnitTransformUtil.transform(value[j], valueUnit[j]) + '<br/>';
					}
					return out;
				}
			},
			legend: {
				data: [{
					name: '设备雷达图',
					icon: 'diamond'
				}],
				y: 'bottom',
				textStyle: {
					color: '#666',
					fontSize: 15
				},
				itemWidth: 14,
				itemHight: 14,
				show: false
			},
			radar: {
				name: {
					textStyle: {
						color: '#666',
						fontSize: 12
					}
				},
				splitLine: {
					lineStyle: {
						color: '#C4C9D2'
					}
				},
				axisLine: {
					lineStyle: {
						color: '#C4C9D2',
						width: 1
					}
				},
				splitArea: {
					show: false
				},
				indicator: []
			},
			series: [{
				name: '设备雷达图',
				type: 'radar',
				data: [{
					//symbolSize:6,
					value: [],
					indicator: [],
					valueUnit: [],
					name: '设备雷达图',
					lineStyle: {
						normal: {
							width: 1,
						}
					},
					areaStyle: {
						normal: {
							color: '#17c27c',
							opacity: 0.7
						}
					},
					itemStyle: {
						normal: {
							borderColor: '#17c27c',
							borderWidth: 4
						}
					}
				}]
			}]
		},
		//漏斗图
		plotFunnelChartOption: {
			color: ["#17C27C", "#837AFE", "#609EE9", "#FCAA43", "#F7A095"],
			title: {
				text: '漏斗图',
				subtext: '',
				show: false
			},
			tooltip: {
				trigger: 'item',
				formatter: function(params) {
					var c = params.data.name.replace('\n', "<br/>");
					return c;
				}
			},
			toolbox: {
				show: false
			},
			legend: {
				data: [],
				show: false
			},
			calculable: true,
			series: [{
				name: '漏斗图',
				type: 'funnel',
				left: '10%',
				width: '50%',
				min: 0,
				max: 100,
				minSize: '0%',
				maxSize: '90%',
				sort: 'descending',
				gap: 1,
				label: {
					normal: {
						show: true,
					},
					emphasis: {
						textStyle: {
							fontSize: 20
						}
					}
				},
				labelLine: {
					normal: {
						length: 10,
						lineStyle: {
							width: 1,
							type: 'solid'
						}
					}
				},
				itemStyle: {
					normal: {
						borderColor: '#fff',
						borderWidth: 1
					}
				},
				data: []
			}]
		},
		//温度表
		plotThermometerChart: {
			bottomText: "0",
			topText: "100",
			animationSpeed: 300,
			maxValue: 100,
			minValue: 0,
			textColour: '#666',
			tickColour: '#666',
			pathToSVG: 'module/home/widgets/js/thermometer/thermo-bottom.svg',
			liquidColour: ["#00ff00", "#ff0000"],
			colorLevel: {
				CRITICAL: {
					color: '#e95e48'
				},
				SERIOUS: {
					color: '#f09722'
				},
				WARN: {
					color: '#eee000'
				},
				NORMAL: {
					color: '#17c27c'
				},
				default: {
					color: '#17c27c'
				}
			},
			option: {
				tooltip: {
					show: true,
					formatter: function(item) {
						return ' ' + item.data.rawValue + '°';
					}
				},
				title: [{
					text: '测试温度计',
					subtext: '测试温度计指标',
					textAlign: 'center',
					left: '50%',
					top: '65%',
					subtextStyle: {
						color: '#666',
						fontSize: 12,
						fontWeight: 'normal'
					},
					textStyle: {
						color: '#666',
						fontSize: 12,
						fontWeight: 'normal'
					}
				}],
				series: [{
					animation: true,
					waveAnimation: false,

					color: ['rgb(253, 77, 73)'],
					center: ['50%', '40%'],
					radius: '63%',
					type: 'liquidFill',
					//shape: 'path://M229.844,151.547v-166.75c0-11.92-9.662-21.582-21.58-21.582s-21.581,9.662-21.581,21.582v166.75c-9.088,6.654-14.993,17.397-14.993,29.524c0,20.2,16.374,36.575,36.574,36.575c20.199,0,36.574-16.375,36.574-36.575C244.838,168.944,238.932,158.201,229.844,151.547z',
					shape: 'path://M131.823,84.947V-30.087h-0.021c0.004-0.131,0.021-0.259,0.021-0.391c0-7.18-5.82-13-13-13c-7.181,0-13,5.82-13,13 c0,0.132,0.016,0.26,0.02,0.391h-0.02V84.947C93.598,90.043,85,102.105,85,116.178C85,134.857,100.145,150,118.823,150 s33.823-15.144,33.823-33.822C152.646,102.105,144.05,90.043,131.823,84.947z',
					outline: {
						show: false
					},
					amplitude: 0,
					waveLength: '20%',
					backgroundStyle: {
						color: 'none',
						borderColor: '#ccc',
						borderWidth: 3
					},
					data: [{
						// -60 到 100 度
						value: 90,
						rawValue: 90
					}],
					itemStyle: {
						normal: {
							shadowBlur: 0
						}
					},
					label: {
						normal: {
							position: 'insideBottom',
							distance: 15,
							formatter: function(item) {
								return ' ' + item.data.rawValue + '°';
							},
							textStyle: {
								color: '#666',
								fontSize: 12
							}
						}
					}
				}]
			}
		},
		//仪表盘
		plotGaugeChartV2Option: {
			tooltip: {
				formatter: "{b} : {c}%"
			},
			toolbox: {
				show: false
			},
			grid: {
				left: '1%',
				right: '1%',
				bottom: '1%',
				top: '1%',
				containLabel: true
			},
			title: {
				text: 'data.metric',
				subtext: 'data.name',
				textStyle: {
					color: '#666',
					fontSize: 12,
				},
				subtextStyle: {
					color: '#666',
					fontSize: 12,
					//fontWeight:'blod'
				},
				x: 'center',
				y: '75%',
			},
			series: [{
				name: '业务指标',
				type: 'gauge',
				center: ['50%', '40%'],
				//radius : '75%',
				axisLine: {
					show: true,
					lineStyle: {
						color: [
							[0.2, '#17c27c'],
							[0.8, '#eee000'],
							[1, '#f09722']
						],
						width: 6
					}
				},
				axisTick: {
					show: false,
					splitNumber: 10,
					length: 8,
					lineStyle: {
						color: '#eee',
						width: 1,
						type: 'solid'
					}
				},
				axisLabel: {
					show: true,
					textStyle: {
						color: '#666'
					},
					formatter: function(v) {
						return v;
					}
				},
				title: {
					show: false
				},
				splitLine: {
					show: true,
					length: 6,
					lineStyle: {
						color: '#fff',
						width: 2,
						type: 'solid'
					}
				},
				pointer: {
					length: '80%',
					width: 3,
					color: 'auto'
				},
				detail: {
					show: true,
					formatter: '{value}%',
					textStyle: {
						color: '#666',
						fontSize: 12
					}
					},
				data: [{
					value: 50,
					name: '完成率'
				}]
			}]
		},
		//指标圈
		plotMetricCircleChartOption: {
			tooltip: {
				trigger: 'item',
				formatter: function(p) {
					return p.data.name + '<br/>' + p.data.metric + p.data.label;
				},
				show: true
			},
			color: [],
			title: {
				text: '',
				subtext: '',
				textStyle: {
					color: '#666',
					fontSize: 12,
				},
				subtextStyle: {
					color: '#666',
					fontSize: 12,
					//fontWeight:'blod'
				},
				x: 'center',
				y: '75%'
			},
			legend: {
				orient: 'vertical',
				x: 'left',
				data: [],
				show: false
			},
			series: [{
				name: '',
				type: 'pie',
				radius: ['65%', '75%'],
				center: ['50%', '40%'],
				avoidLabelOverlap: false,
				clockwise: false,
				label: {
					normal: {
						show: true,
						position: 'center',
						textStyle: {
							fontSize: '16',
							//fontWeight: 'bold'
						},
						formatter: function(p) {
							return p.data.label;
						}
					},
					emphasis: {
						show: true,
						textStyle: {
							fontSize: '24',
							fontWeight: 'bold'
						},
						formatter: function(p) {
							return p.data.label;
						}
					}
				},
				labelLine: {
					normal: {
						show: false
					}
				},
				data: []
			}]
		},
		plotMetricCircleChartColor: ['#e0e5e9']

	}

	//关注资源的配置信息
	properties.focusResource = {
        gaugeOption : {
            series: [
                {
                    name: '',
                    type: 'gauge',
                    center : ['50%', '70%'],
                    radius : '100%',
                    startAngle: 180,
                    endAngle : 0,
                    axisLine: {
                        show: true,
                        lineStyle: {
                            color: [[0.2, '#17c27c'],[0.8, '#eee000'],[1, '#f09723']],
                            width: 12
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel :{
                        show : false
                    },
                    splitLine : {
                        show : false
                    },
                    title:{
                        offsetCenter : [0,'30%'],
                        textStyle:{
                            color:'#666'
                        }
                    },
                    pointer : {
                        length : '70%',
                        width : 3,
                        color : 'auto'
                    },
                    detail : {
                        show : true,
                        formatter:'{value}%',
                        textStyle: {
                            color: '#333',
                            fontSize : 16
                        },
                        offsetCenter :[0,'-40%']
                    },
                    data: [{value: 0, name: ''}]
                }
            ]
        }
	}


	//地图的配置信息
	properties.map = {

	}

	//外部页面的配置信息
	properties.outerPage = {

	}

	//告警一览的配置信息
	properties.showWarning = {

	}

	//统计视图的配置信息
	properties.statisticView = {
		//告警图
		warmingOption: {
			color: ['#fb2f5d', '#eee000', '#F09722', '#fd6589', '#fafb2c', '#e95e48', '#f5e032', '#f99008', '#da7e08'],
			tooltip: {
				trigger: 'item',
				formatter: function(p) {
					var val = p.name + '<br/>';
					if(p.seriesIndex > 0)
						val += p.data.level;
					val += '告警<br/>';
					val += p.value;
					val += '(' + p.percent + '%)';
					return val;
				}
			},
			legend: {
				orient: 'vertical',
				x: 'left',
				data: [],
				show: false
			},
			series: [{
					name: '告警数量',
					type: 'pie',
					selectedMode: 'single',
					radius: [0, '30%'],

					label: {
						normal: {
							position: 'inner'
						}
					},
					labelLine: {
						normal: {
							show: false
						}
					},
					data: []
				},
				{
					name: '告警详细信息',
					label: {
						normal: {
							textStyle: {
								color: "#666"
							}
						}
					},
					type: 'pie',
					radius: ['40%', '55%'],
					data: []
				}
			]
		},
		//资产图
		assetOption: {
			color: ['#7ee716 ', '#8dff18', '#12b346', '#14cd52', '#16e75c', '#18ff66'],
			title: {
				text: '',
				left: 'center',
				top: 20,
				textStyle: {
					color: '#666'
				}
			},
			legend: {
				orient: 'vertical',
				x: 'right',
				data: [],
				selectedMode: 'single',
				selected: {
					'网络': true,
					'主机': false,
					'应用': false
				},
				textStyle: {
					color: '#666',
					fontWeight: 'bold'
				},
				inactiveColor: '#666'

			},
			tooltip: {
				trigger: 'item',
				formatter: "{a} <br/>{b} : {c} ({d}%)"
			},
			series: [{
				name: '',
				type: 'pie',
				radius: '55%',
				center: ['50%', '50%'],
				data: [],
				label: {
					normal: {
						textStyle: {
							color: '#666'
						}
					}
				},
				labelLine: {
					normal: {
						lineStyle: {
							color: '#666'
						},
						smooth: 0.2,
						length: 10,
						length2: 20
					}
				},
				itemStyle: {
					emphasis: {
						shadowBlur: 10,
						shadowOffsetX: 0,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					}
				},
				animationType: 'scale',
				animationEasing: 'elasticOut',
				animationDelay: function(idx) {
					return Math.random() * 200;
				}
			}]
		}

	}

	//TOPN的配置信息
	properties.topN = {
		//color : ['#fee155','#8acb67','#83cdb6','#7bcfe7','#6a9bd3'],
		color: {
			CRITICAL: {
				color: '#e95e48'
			},
			SERIOUS: {
				color: '#f09722'
			},
			WARN: {
				color: '#eee000'
			},
			NORMAL: {
				color: '#17c27c'
			},
			default: {
				color: '#17c27c'
			}
		},
		plotHorizontalChartV2Option: {
			title: {
				text: '单位:',
				textStyle: {
					color: '#666',
					fontSize: 10,
				},
				subtext: '',
				show: true,
				x: 'right',
				y: '81%'
			},
			tooltip: {
				trigger: 'axis',
				axisPointer: {
					type: 'shadow'
				},
				confine: true,
				formatter: function(params) {
					var p = params[0].data;
					return p.name + '<br/>' + p.metricName + "：" + p.formatValue;
				}
			},
			legend: {
				show: false
			},
			grid: {
				left: '3%',
				right: '10%',
				bottom: '9%',
				top: '6%',
				containLabel: true
			},
			xAxis: [{
				type: 'value',
				//interval:10,
				axisLine: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6
					}
				},
				axisTick: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisLabel: {
					textStyle: {
						color: '#666'
					},
					rotate: 30
				},
				splitLine: {
					lineStyle: {
						color: '#c4c9d2'
					}
				}
			}],
			yAxis: [{
				type: 'category',
				axisTick: {
					show: false
				},
				data: [],
				axisLine: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6
					}
				},
				axisTick: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisLabel: {
					textStyle: {
						color: '#666'
					}
				}
			}],
			series: [{
				name: '排名',
				type: 'bar',
				label: {
					normal: {
						show: false,
						position: 'insideleft',
						formatter: function(p) {
							return p.data.formatValue;
						}
					}
				},
				barWidth: 18,
				data: []
			}]
		},
		plotVerticalChartOption: {
			color: ['#e0e5e9'],
			title: {
				text: '单位:',
				textStyle: {
					color: '#666',
					fontSize: 10,
				},
				subtext: '',
				show: true,
				bottom: '40%'
			},
			tooltip: {
				trigger: 'axis',
				axisPointer: { // 坐标轴指示器，坐标轴触发有效
					type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				},
				confine: true,
				formatter: function(params) {
					var p = params[0].data;
					return p.name + '<br/>' + p.metricName + "：" + p.formatValue;
				}
			},
			grid: {
				top: '9%',
				left: '10%',
				right: '4%',
				bottom: '12%',
				containLabel: true
			},
			xAxis: [{
				type: 'category',
				data: [],
				axisLine: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisTick: {
					alignWithLabel: false,
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisLabel: {
					textStyle: {
						color: '#666',
						fontSize: 10
					},
					rotate: 30,
					interval: 0
				}
			}],
			yAxis: [{
				type: 'value',
				splitLine: {
					show: true,
					lineStyle: {
						color: '#c4c9d2',
						width: 1,
						opacity:0.6,
					}
				},
				axisLine: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisTick: {
					lineStyle: {
						color: '#a4a9b1',
						width: 1,
						opacity:0.6,
					}
				},
				axisLabel: {
					show: true,
					textStyle: {
						color: '#666'
					}
				}
			}],
			series: [{
				name: '直接访问TOP10',
				type: 'bar',
				barWidth: 18,
				data: []
			}]
		}

	}

	//拓扑视图的配置信息
	properties.topoView = {

	}

	properties.UnitTransformUtil = {

		transform: function(value, unit) {
			var decimalFormat = {
				format: function(v) {
					return v.toFixed(2);
				}
			}

			switch(unit) {
				case "ms":
				case "毫秒":
					value = transformMillisecond(parseFloat(value));
					break;
				case "分":
					value = transformMinute(parseFloat(value));
					break;
				case "s":
				case "秒":
					value = transformSecond(parseFloat(value));
					break;
				case "Bps":
					value = transformByteps(parseFloat(value));
					break;
				case "bps":
				case "比特/秒":
					value = transformBitps(parseFloat(value));
					break;
				case "KB/s":
					value = transformKBs(parseFloat(value));
					break;
				case "Byte":
					value = transformByte(parseFloat(value));
					break;
				case "KB":
					value = transformKB(parseFloat(value));
					break;
				case "MB":
					value = transformMb(parseFloat(value));
					break;
				case "MB/s":
					value = transformMb(parseFloat(value)) + '/s';
					break;
				case "MHz":
					value = transformMHz(parseFloat(value));
					break;
				case "%":
					value = transformPrecent(parseFloat(value)) + '%';
					break;
				case "KBps":
					value = transformPrecent(parseFloat(value)) + 'KBps';
					break;
				case "次":
					value = parseInt(value) + unit;
					break;
				case "包/秒":
					value = parseInt(value) + unit;
					break;
				case "":
					value = value + unit;
					break;
				default:
					value = value + unit;
					//value = decimalFormat.format(parseFloat(value)) + unit;
					break;
			}
			return value;

			function transformByte(byteDouble) {
				var sb = '';
				if(byteDouble > 1024 * 1024 * 1024 * 1024 * 1024) {
					sb = decimalFormat.format(byteDouble / (1024 * 1024 * 1024 * 1024 * 1024)) + "PB";
				} else if(byteDouble > 1024 * 1024 * 1024 * 1024) {
					sb = decimalFormat.format(byteDouble / (1024 * 1024 * 1024 * 1024)) + "TB";
				} else if(byteDouble > 1024 * 1024 * 1024) {
					sb = decimalFormat.format(byteDouble / (1024 * 1024 * 1024)) + "GB";
				} else if(byteDouble > 1024 * 1024) {
					sb = decimalFormat.format(byteDouble / (1024 * 1024)) + "MB";
				} else if(byteDouble > 1024) {
					sb = decimalFormat.format(byteDouble / 1024) + "KB";
				} else {
					sb = decimalFormat.format(byteDouble) + "Byte";
				}
				return sb;
			}

			function transformMHz(MHzDouble) {
				var sb = '';
				if(MHzDouble > 1024) {
					sb = decimalFormat.format(MHzDouble / 1024) + "GHz";
				} else {
					sb = MHzDouble + "MHz";
				}
				return sb;
			}

			function transformKB(KB) {
				var sb = '';
				if(KB > 1024 * 1024 * 1024 * 1024) {
					sb = (decimalFormat.format(KB / (1024 * 1024 * 1024 * 1024)) + "PB");
				} else if(KB > 1024 * 1024 * 1024) {
					sb = (decimalFormat.format(KB / (1024 * 1024 * 1024)) + "TB");
				} else if(KB > 1024 * 1024) {
					sb = (decimalFormat.format(KB / (1024 * 1024)) + "GB");
				} else if(KB > 1024) {
					sb = (decimalFormat.format(KB / 1024) + "MB");
				} else {
					sb = (KB + "KB");
				}
				return sb;
			}

			// 1024Bps=1MBps 1024KBps=1MBps 1024MBps=1GBps
			function transformByteps(bps) {
				var sb = '';
				if(bps > 1000 * 1000 * 1000) {
					sb = (decimalFormat.format(bps / (1000 * 1000 * 1000)) + "GBps");
				} else if(bps > 1000 * 1000) {
					sb = (decimalFormat.format(bps / (1000 * 1000)) + "MBps");
				} else if(bps > 1000) {
					sb = (decimalFormat.format(bps / 1000) + "KBps");
				} else {
					sb = (bps + "Bps");
				}
				return sb;
			}

			// 1024bps=1Kbps 1024Kbps=1Mbps 1024Mbps=1Gbps
			function transformBitps(bps) {
				var sb = '';
				if(bps > 1000 * 1000 * 1000) {
					sb = (decimalFormat.format(bps / (1000 * 1000 * 1000)) + "Gbps");
				} else if(bps > 1000 * 1000) {
					sb = (decimalFormat.format(bps / (1000 * 1000)) + "Mbps");
				} else if(bps > 1000) {
					sb = (decimalFormat.format(bps / 1000) + "Kbps");
				} else {
					sb = (bps + "bps");
				}
				return sb;
			}

			function transformKBs(KBs) {
				var sb = '';
				if(KBs > 1024 * 1024) {
					sb = (decimalFormat.format(KBs / (1024 * 1024)) + "GB/s");
				} else if(KBs > 1024) {
					sb = (decimalFormat.format(KBs / 1024) + "MB/s");
				} else {
					sb = (decimalFormat.format(KBs) + "KB/s");
				}
				return sb;
			}

			function transformMb(mb) {
				var sb = '';
				if(mb >= 1024 * 1024 * 1024) {
					sb = (decimalFormat.format(mb / 1024 / 1024 / 1024) + "PB");
				} else if(mb >= 1024 * 1024) {
					sb = (decimalFormat.format(mb / 1024 / 1024) + "TB");
				} else if(mb >= 1024) {
					sb = (decimalFormat.format(mb / 1024) + "GB");
				} else {
					sb = (decimalFormat.format(mb) + "MB");
				}
				return sb;
			}

			function transformMillisecond(milliseconds) {
				var millisecond = milliseconds;
				var seconds = 0,
					second = 0;
				var minutes = 0,
					minute = 0;
				var hours = 0,
					hour = 0;
				var day = 0;
				if(milliseconds > 1000) {
					millisecond = milliseconds % 1000;
					second = seconds = (milliseconds - millisecond) / 1000;
				}
				if(seconds > 60) {
					second = seconds % 60;
					minute = minutes = (seconds - second) / 60;
				}
				if(minutes > 60) {
					minute = minutes % 60;
					hour = hours = (minutes - minute) / 60;
				}
				if(hours > 24) {
					hour = hours % 24;
					day = (hours - hour) / 24;
				}
				var sb = '';
                if(day != 0){
                    sb += day > 0 ? parseInt(day) + "天" : sb;
                }
                if(hour != 0){
                    sb += hour > 0 ? parseInt(hour) + "小时" : sb;
                }
                if(minute != 0){
                    sb += minute > 0 ? parseInt(minute) + "分" : sb;
                }
                if(second != 0){
                    sb += second > 0 ? parseInt(second) + "秒" : sb;
                }
                if(millisecond != 0){
                    sb += millisecond > 0 ? parseInt(millisecond) + "毫秒" : sb;
                }
				sb = ("" == sb) ? parseInt(millisecond) + "毫秒" : sb;
				return sb;
			}

			function transformSecond(seconds) {
				return transformMillisecond(seconds * 1000)
			}

            function transformMinute(minute){
                return transformMillisecond(minute * 60 * 1000);
            }

			function transformPrecent(value) {
				return decimalFormat.format(value);
			}

		}
	}

	oc.ns('oc.index.home');
	oc.index.home.widgetproperties = properties;
})();