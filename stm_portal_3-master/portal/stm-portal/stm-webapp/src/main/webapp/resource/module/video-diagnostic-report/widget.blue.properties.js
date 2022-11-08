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
                        color : "#c0d9ef"
                    },
					
                },
                xAxis: [{
                    labels : {
                        style : {
                            color : "#c0d9ef"
                        }
                    },
                   lineColor : "#2f5bac",
                    categories: [],
                    crosshair: true
                }],
                yAxis: [{ // Primary yAxis
                    labels: {
                        style: {
                            color: '#c0d9ef'
                        }
                    },
                    lineColor : "#2f5bac",
                    allowDecimals : false,
                    title: {
                        text: '数量',
                        style: {
                            color: "#c0d9ef"
                        }
                    },
					gridLineColor: '#2f5bac',
                }, { // Secondary yAxis
                    lineColor : "#2f5bac",
                    allowDecimals : false,
                    min:0,
                    max:100,
                    title: {
                        text: '在线率',
                        style: {
                            color: "#c0d9ef"
                        }
                    },
                    labels: {
                        style: {
                            color: "#c0d9ef"
                        }
                    },
                    opposite: true,
					gridLineColor: '#2f5bac',
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
                    color : "#c0d9ef"
                }
            },
            xAxis: [{
                labels : {
                    style : {
                        color : "#c0d9ef"
                    }
                },
                categories: [],
                crosshair: true,
                lineColor : "#2f5bac",
            }],
            yAxis: [{ // Primary yAxis
                allowDecimals : false,
                 lineColor : "#2f5bac",
				gridLineColor: '#2f5bac',
                labels: {
                    style: {
                        color: "#c0d9ef"
                    }
                },
                title: {
                    text: '数量',
                    style: {
                        color: "#c0d9ef"
                    }
                }
            }, { // Secondary yAxis
                lineColor : "#2f5bac",
				gridLineColor: '#2f5bac',
                max : 100,
                min : 0,
                title: {
                    text: '完好率',
                    style: {
                        color: "#c0d9ef"
                    }
                },
                labels: {
                    style: {
                        color: "#c0d9ef"
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
                floating: true
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
                    color : "#c0d9ef"
                }
            },
            xAxis: [{
                labels : {
                    style : {
                        color : "#c0d9ef"
                    }
                },
                 lineColor : "#2f5bac",
                categories: [],
                crosshair: true
            }],
            yAxis: [{ // Primary yAxis
                 lineColor : "#2f5bac",
				gridLineColor: '#2f5bac',
                labels: {
                    style: {
                        color: "#c0d9ef"
                    }
                },
                title: {
                    text: '数量',
                    style: {
                        color: "#c0d9ef"
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
                        color : "#c0d9ef"
                    }
            },
            xAxis: {
                type: 'category',
                labels: {
                    style: {
                        color: '#c0d9ef'
                    }
                },
				lineColor:'#2f5bac',
            },
            yAxis: {
                lineColor : "#2f5bac",
                max : 100,
                min : 0,
                title: {
                    text: ''
                },
                labels: {
                    style: {
                        color: '#c0d9ef'
                    }
                },
				 gridLineColor: '#2f5bac',
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