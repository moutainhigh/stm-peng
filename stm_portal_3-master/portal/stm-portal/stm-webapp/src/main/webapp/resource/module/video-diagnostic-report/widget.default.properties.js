(function(){
	var properties = {};

	//摄像头报表
	properties.cameraReport = {
	    //在线率
        onlineRate : {
                chart: {
                    zoomType: 'xy',
                    backgroundColor:'rgba(0,0,0,0)'
                },
                title: {
                    text: '在线率报表',
                    style: {
                        color : "#333"
                    },
					
                },
                xAxis: [{
                    labels : {
                        style : {
                            color : "#666666"
                        }
                    }, lineColor : "#e3e3e3",
                    categories: [],
                    crosshair: true,
                }],
                yAxis: [{ // Primary yAxis
                    labels: {
                        style: {
                            color: '#666666'
                        }
                    }, lineColor : "#e3e3e3",
                    allowDecimals : false,
                    title: {
                        text: '数量',
                        style: {
                            color: "#666666"
                        }
                    },
					 lineColor : "#e3e3e3",
					gridlineColor: '#e3e3e3',
				    gridLineWidth: 1,
                }, { // Secondary yAxis 
				  lineColor : "#e3e3e3",
                    allowDecimals : false,
                    min:0,
                    max:100,
                    title: {
                        text: '在线率',
                        style: {
                            color: "#666666"
                        }
                    },
                    labels: {
                        style: {
                            color: "#666666"
                        }
                    },
                    opposite: true,
					lineColor : "#e3e3e3",
					gridlineColor:'#e3e3e3',
					 gridLineWidth: 1,
                }],
                tooltip: {
                    shared: true
                },
                legend: {
                    layout: 'horizontal',
                    align: 'center',
                    x: 150,
                    verticalAlign: 'top',
                    floating: true
                },
                series: [{
                    name : "在线数",
                    type : "column",
                    yAxis : 0,
                    data : [],
                    tooltip: {
                        valueSuffix: ' 个'
                    },
                    color : "#40de5a"
                },{
                    name : "总数",
                    type : "column",
                    yAxis : 0,
                    data : [],
                    tooltip: {
                        valueSuffix: ' 个'
                    },
                    color : "#00BBD3"
                },{
                    name: '在线率',
                    type: 'spline',
                    yAxis : 1,
                    data: [],
                    tooltip: {
                        valueSuffix: ' %'
                    },
                    color : "#6DA0C1"
                }]
            },

            //完好率
        serviceabilityRate : {
            chart: {
                zoomType: 'xy',
                backgroundColor:'rgba(0,0,0,0)'
            },
            title: {
                text: '完好率报表',
                style: {
                    color : "#333"
                }
            },
            xAxis: [{
                labels : {
                    style : {
                        color : "#666666"
                    }
                },
                categories: [],
                crosshair: true, lineColor : "#e3e3e3",
            }],
            yAxis: [{ // Primary yAxis
                allowDecimals : false, lineColor : "#e3e3e3",
				gridlineColor:'#e3e3e3',
				 gridLineWidth: 1,
                labels: {
                    style: {
                        color: "#666666"
                    }
                },
                title: {
                    text: '数量',
                    style: {
                        color: "#666666"
                    }
                }
            }, { // Secondary yAxis 
			  lineColor : "#e3e3e3",
				gridlineColor:'#e3e3e3',
                max : 100,
                min : 0,
                title: {
                    text: '完好率',
                    style: {
                        color: "#666666"
                    }
                },
                labels: {
                    style: {
                        color: "#666666"
                    }
                },
                opposite: true
            }],
            tooltip: {
                shared: true
            },
            legend: {
                layout: 'horizontal',
                align: 'center',
                x: 150,
                verticalAlign: 'top',
                floating: true,
				itemStyle: {
    color: '#666'
  },
				 itemHoverStyle: {
                color: '#333'
            }
            },
            series: [{
                name : "完好数",
                type : "column",
                yAxis : 0,
                data : [],
                tooltip: {
                    valueSuffix: ' 个'
                },
                color : "#40de5a"
            },{
                name : "总数",
                type : "column",
                yAxis : 0,
                data : [],
                tooltip: {
                    valueSuffix: ' 个'
                },
                color : "#00BBD3"
            },{
                name: '完好率',
                type: 'spline',
                yAxis : 1,
                data: [],
                tooltip: {
                    valueSuffix: ' %'
                },
                color : "#6DA0C1"
            }]
        },

        //故障率
        failureRate : {
            chart: {
                zoomType: 'xy',
                margin: 65,
                backgroundColor:'rgba(0,0,0,0)'
            },
            title: {
                text: '故障分析结果',
                style: {
                    color : "#333"
                }
            },
            xAxis: [{
                labels : {
                    style : {
                        color : "#666666"
                    }
                }, lineColor : "#e3e3e3",
                categories: [],
                crosshair: true
            }],
            yAxis: [{ // Primary yAxis 
			lineColor : "#e3e3e3",
				gridlineColor:'#e3e3e3',
				 gridLineWidth: 1,
                labels: {
                    style: {
                        color: "#666666"
                    }
                },
                title: {
                    text: '数量',
                    style: {
                        color: "#666666"
                    }
                }
            }],
            tooltip: {
                shared: true
            },
            legend: {
                layout: 'horizontal',
                align: 'center',
                x: 150,
                verticalAlign: 'top',
                floating: true
            },
            series: [{
                name : "故障总数量",
                type : "column",
                yAxis : 0,
                data : [],
                tooltip: {
                    valueSuffix: ' 个'
                },
                color : "#00BBD3"
            }]
        },

        //考核图
        assessRate : {
            chart: {
                type: 'column',
                margin: 65,
                backgroundColor:'rgba(0,0,0,0)'
            },
            title: {
                text: '',
				  style: {
                        color : "#333"
                    }
            },
            xAxis: {
                type: 'category',
                labels: {
                    style: {
                        color: '#666666'
                    }
                },
				lineColor:'#e3e3e3',
            },
            yAxis: { lineColor : "#e3e3e3",
                max : 100,
                min : 0,
                title: {
                    text: ''
                },
                labels: {
                    style: {
                        color: '#666666'
                    }
                },
				 gridlineColor:'#e3e3e3',
				 gridLineWidth: 1,
				             },
            legend: {
                enabled: false
            },
            tooltip: {
                shared: true
            },
            plotOptions : {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.1f}'
                    }
                }
            },
            series: []
        }
    }

	oc.ns('oc.index.camera.report');
    oc.index.camera.report.properties = properties;
})();