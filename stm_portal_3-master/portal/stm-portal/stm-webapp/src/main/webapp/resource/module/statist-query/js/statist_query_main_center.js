$(function(){
	oc.ns('oc.statist.query.maincenter');
	function statistQueryMainCenter(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
		this._init();
	}
	statistQueryMainCenter.prototype = {
		constructor : statistQueryMainCenter,
		_defaults : {},
		dataOption : undefined,
		centerDiv : undefined,
		centerFormDiv : undefined,
		centerContentDiv : undefined,
		chartName : undefined,
		chartStartTime : undefined,
		chartEndTime : undefined,
		spanToolBar : undefined,
		chartAllData : [],
		_init : function(){
			var id = oc.util.generateId(), that = this;
			this.centerDiv = $('#statistQueryMainCenter').attr('id',id);
			this.centerFormDiv = this.centerDiv.find(".statQMCForm");
			this.centerContentDiv = this.centerDiv.find(".statQMCContent");
		},
		open : function(dataOption){
			var that = this;
			this.dataOption = dataOption;
			if(dataOption.type == "1"){
				that.addQueryTime();
				// 默认查询一天内的数据
				that.dataOption.startTime = new Date((new Date()).getTime() - (1000 * 60 * 60 * 24)).stringify();
				that.dataOption.endTime = new Date().stringify();
				that.queryStatQData(that.dataOption);
			}else if(dataOption.type == "2"){
				this.addExportTools();
				that.queryStatQData(that.dataOption);
			}
		},
		addQueryTime : function(){
			var that = this;
			var startTime = $("<input>"), endTime = $("<input>"), queryBtn = $("<a/>"),
				exportExcel = $('<span/>').attr('class','ico ico-excel').attr('title','excel下载!'),
				exportWord = $('<span/>').attr('class','ico ico-word').attr('title','word下载!'),
				exportPdf = $('<span/>').attr('class','ico ico-pdf').attr('title','pdf下载!');
			this.spanToolBar = $('<span/>').css({'margin-top':'7px','float':'right'});
			this.centerFormDiv.append("<span>查询：</span>")
				.append("从").append(startTime).append("到").append(endTime)
				.append(queryBtn)
				.append(that.spanToolBar.append(exportExcel).append(exportWord).append(exportPdf));
			startTime.datetimebox({
				width : '188px',
				showSeconds : false,
				required: true,
				value : new Date((new Date()).getTime() - (1000 * 60 * 60 * 24)).stringify()
			});
			endTime.datetimebox({
				width : '188px',
				showSeconds : false,
				required: true,
				value : new Date().stringify()
			});
			queryBtn.linkbutton("RenderLB", {
				text : '查询',
				onClick : function(){
					var dateStartStr = startTime.datetimebox('getValue');	
					var dateEndStr = endTime.datetimebox('getValue');
					
					var dateStart = new Date(dateStartStr.replace(/-/g,"/"));
					var dateEnd = new Date(dateEndStr.replace(/-/g,"/"));
					
					var timeSub = dateEnd.getTime() - dateStart.getTime();
					if(timeSub < 0 || timeSub == 0){
						alert('开始日期应在结束日期之前！');
						return false;
					}
					that.dataOption.startTime = startTime.datetimebox('getValue');
					that.dataOption.endTime = endTime.datetimebox('getValue');
					that.queryStatQData(that.dataOption);
				}
			});
			// 
			exportExcel.on('click', function(){
				that.addExportEvent(that.dataOption.id, startTime.datetimebox('getValue'), endTime.datetimebox('getValue'), 'excel');
			});
			exportWord.on('click', function(){
				that.addExportEvent(that.dataOption.id, startTime.datetimebox('getValue'), endTime.datetimebox('getValue'), 'word');
			});
			exportPdf.on('click', function(){
				that.addExportEvent(that.dataOption.id, startTime.datetimebox('getValue'), endTime.datetimebox('getValue'), 'pdf');
			});
		},
		addExportTools : function(){
			var that = this;
			var exportExcel = $('<span/>').attr('class','ico ico-excel').attr('title','excel下载!'),
			exportWord = $('<span/>').attr('class','ico ico-word').attr('title','word下载!'),
			exportPdf = $('<span/>').attr('class','ico ico-pdf').attr('title','pdf下载!');
			this.spanToolBar = $('<span/>').css({'margin-top':'7px','float':'right'});
			this.centerFormDiv.append(that.spanToolBar.append(exportExcel).append(exportWord).append(exportPdf));
			// 
			exportExcel.on('click', function(){
				that.addExportEvent(that.dataOption.id, "", "", 'excel');
			});
			exportWord.on('click', function(){
				that.addExportEvent(that.dataOption.id, "", "", 'word');
			});
			exportPdf.on('click', function(){
				that.addExportEvent(that.dataOption.id, "", "", 'pdf');
			});
		},
		addExportEvent : function(statQMId, startTime, endTime, type){
			var dateStart = new Date(startTime.replace(/-/g,"/"));
			var dateEnd = new Date(endTime.replace(/-/g,"/"));
			var timeSub = dateEnd.getTime() - dateStart.getTime();
			if(timeSub<0 || timeSub==0){
				alert('开始日期应在结束日期之前！');
				return false;
			}
			var iframeHtml = $("<iframe style='display:none' src='"+oc.resource.getUrl('portal/statistQuery/data/downloadStatQChart.htm?id='+
					statQMId+'&startTime='+startTime+'&endTime='+endTime+'&type='+type)+"'/>");
			iframeHtml.appendTo("body");
		},
		queryStatQData : function(data){
			var that = this;
			oc.util.ajax({
				url : oc.resource.getUrl('portal/statistQuery/data/getStatQDataByStatQMainId.htm'),
				timeout : null,
				data : data,
				success : function(data){
					if(data.code == 200){
						that.analysisData(data.data);
						that.createLayout();
						that.addcenterDivScrollEvent();
					}else{
						alert("查询报表数据出错!");
						that.spanToolBar.hide();
					}
				}
			});
		},
		analysisData : function(data){
			var that = this;
			that.chartName = data.name;
			that.chartStartTime = data.startTime;
			that.chartEndTime = data.endTime;
			that.chartAllData = [];
			for(var i = 0; data.chartBoList != undefined && i < data.chartBoList.length; i++){
				var chartData = data.chartBoList[i];
				switch (chartData.type) {
				case "Title":
					chartData.dom = $("<div class='reportTitle'/>");
					break;
				case "Table":
					chartData.dom = $("<div class='reportTable'/>");
					break;
				case "Line":
					chartData.dom = $("<div class='reportLineChart'/>").height('300px');
					break;
				case "Pie":
					chartData.dom = $("<div class='reportPieChart'/>").width('32.4%').height('240px');
					break;
				}
				that.chartAllData.push(chartData);
			}
		},
		addcenterDivScrollEvent : function(){
			var that = this;
			that.centerDiv.scroll(function(){
				var centerTop = that.centerDiv.offset().top;
				var contentTop = that.centerContentDiv.offset().top;
				contentTop = Math.abs(contentTop - centerTop);
				if(that.centerContentDiv.height() - contentTop - that.centerDiv.height() < 100){
					if(that.chartAllData != undefined && that.chartAllData.length > 0){
						that.renderChart();
					}
				}
			});
		},
		createLayout : function(){
			var that = this;
			that.centerContentDiv.empty();
			that.centerContentDiv.append("<div class='statQTitle'>" + that.chartName + "</div>");
			that.centerContentDiv.append("<div class='statQTitleTime'>("
					+ (that.dataOption.type != '2' ? (that.chartStartTime + " -- " + that.chartEndTime) : that.chartStartTime)
					+ ")</div>");
			that.renderChart();
		},
		renderChart : function(){
			var addContentHeight = 0, copyChartAllData = $.extend([], this.chartAllData);
			this.chartAllData.reverse();
			for(var i = 0; i < copyChartAllData.length; i++){
				if(addContentHeight > 1000){
					break;
				}
				var chartData = copyChartAllData[i];
				this.centerContentDiv.append(chartData.dom);
				switch (chartData.type) {
				case 'Title':
					this.createTitle(chartData);
					break;
				case 'Table':
					this.createTable(chartData);
					break;
				case 'Line':
					this.createLineChart(chartData);
					break;
				case 'Pie':
					this.createPieChart(chartData);
					break;
				}
				addContentHeight += chartData.dom.height();
				this.chartAllData.pop();
			}
			this.chartAllData.reverse();
		},
		createTitle : function(chartData){
			chartData.dom.html(chartData.title);
		},
		createTable : function(chartData){
			var that = this, tableDom = chartData.dom;
			var table = $("<table border='1'/>");
			tableDom.append(table);
			var head = chartData.head, tr = $("<tr/>");
			for(var i = 0; i < head.length; i++){
				var th = $("<th/>").html(head[i]);
				tr.append(th);
			}
			table.append(tr);
			// data
			var allData = chartData.data, trs = "";
			for(var i = 0; i < allData.length; i++){
				var trData = allData[i], tr = "<tr>";
				for(var j = 0; j < trData.length; j++){
					var tdData = trData[j];
					var tdTextData = trData[j].text == 'null' ? '-' : trData[j].text;
					if(tdData.type == 'progress'){
						if(tdTextData != '-'){
							tr += "<td style='width:180px;'>"
									+ "<div style='text-align:left;height:auto;'>"
									+ '<div class="rate rate-t-' + tdData.metricStatColor + '">'
									+ '<span class="rate-' + tdData.metricStatColor + '" style="width:' + tdTextData + '"></span>'
									+ '</div><span class="rt">' + tdTextData + '</span>'
									+ "</div>"
								+ "</td>";
						}else{
							tr += "<td style='width:180px;'>" + tdTextData + "</td>";
						}
					}else{
						tr += "<td>" + tdTextData + "</td>";
					}
				}
				// 数据列不足时自动补全
				for(var j = 0; j < Math.abs(head.length - trData.length); j ++){
					tr += "<td>-</td>";
				}
				trs += tr + "</tr>";
			}
			table.append(trs);
		},
		createLineChart : function(chartData){
			var lineDom = chartData.dom;
			var xGridLineWidth = 0, rotation = 0, radius = 4, connectNulls = false, labelStep = null, labelNum = 10;
			var dataArray = new Array(), series = chartData.series, categories = chartData.categories;
			for(var i = 0; i < series.length; i++){
				var chartData = series[i];
				var floatData = new Array();
				for(var j = 0; j < chartData.data.length; j++){
					floatData.push(chartData.data[j] == 'null' ? null : parseFloat(chartData.data[j]));
				}
				dataArray.push({
					name : chartData.name,
					data : floatData,
					color : '#37BAFC'
				});
			}
			labelStep = Math.ceil(categories.length / Math.min(categories.length, labelNum))
			
			// 新增一层样式div
			var container = $("<div/>").addClass('highchart-report').height(lineDom.height());
			lineDom.append(container);
			container.highcharts({
				chart : {
		            type : 'line',
		            backgroundColor: 'rgba(0,0,0,0)',
		            margin : [null, 50, 52, null]
		        },
				title : {
					text : chartData.title, // title
					align : 'left',
		            style : {
		            	"color" : Highcharts.theme.reportTitleColor,
		            	"font-size" : "12px",
		            	"font-weight" : "bolder"
		            }
				},
		        xAxis : {
		        	type : 'category',
		        	categories : categories, // xAxis
		        	gridLineWidth : xGridLineWidth,
		        	gridLineColor : '#FFFFFF',
		        	gridLineDashStyle : "Dot",
		        	lineWidth : 0.1,
		        	tickLength : 0,
		        	labels : {
		        		style : {
		        			"font-family" : 'Arial',
		        			"font-size" : '12px',
		        			"color" : '#9EA0A0',
		        			"font-weight" : 'normal'
		        		},
		        		rotation : rotation,
		        		maxStaggerLines : 1,
		        		step : labelStep
		        	},
//		        	tickInterval : labelStep,
		        	tickmarkPlacement : 'on'
		        },
		        yAxis : {
		        	min: 0,
		        	title: null,
		        	lineWidth : 0,
		        	tickWidth : 0.1,
		        	tickLength : 1
		        },
		        legend: {
		            layout: 'horizontal',
		            align: 'left',
		            verticalAlign: 'top',
		            itemStyle : {
		            	"color" : "#9EA0A0",
		            	"font-size" : "12px",
		            	"font-weight" : "normal"
		            },
		            navigation: {
		                activeColor: '#FFFFFF',
		                animation: true,
		                arrowSize: 12,
		                inactiveColor: '#9EA0A0',
		                style: {
		                    "font-weight" : 'bold',
		                    "color" : '#9EA0A0',
		                    "font-size" : '12px'
		                }
		            }
		        },
		        plotOptions: {
			    	series: {
		                marker: {
		                    enabled: false
		                }
		            },
		            line: {
		                dataLabels: {
		                    enabled: false
		                },
		                connectNulls: connectNulls
		            }
		        },
		        series : dataArray, // data=[{name:string,data:array}]
				tooltip : {
					enabled: true,
					headerFormat : "",
					pointFormat : "名称:{series.name}<br/>值:<b>{point.y}</b>"
				},
				credits : {
					enabled: false
				},
	    	    exporting : {
	    	    	enabled: false
	    	    }
			});
		},
		createPieChart : function(chartData){
			var pieDom = chartData.dom;

			var container = $("<div/>").addClass('highchart-report').height(pieDom.height()).width(pieDom.width());
			pieDom.append(container);
			container.highcharts({
				chart: {
					type : 'pie',
		            backgroundColor: 'rgba(0,0,0,0)'
				},
				title : {
					text : chartData.title, // title
					floating : true,
					align : 'left',
					y:5,
		            style : {
		            	"color" : Highcharts.theme.reportTitleColor,
		            	"font-size" : "12px",
		            	"font-weight" : "bolder",
		            	"font-family" : 'Arial'
		            }
				},
				legend : {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'middle',
		            itemStyle : {
		            	"color" : "#9EA0A0",
		            	"font-size" : "12px"
		            },
		            navigation: {
		                activeColor: '#FFFFFF',
		                animation: true,
		                arrowSize: 12,
		                inactiveColor: '#9EA0A0',
		                style: {
		                    "font-weight" : 'bold',
		                    "color" : '#9EA0A0',
		                    "font-size" : '12px'
		                }
		            }
				},
				plotOptions: {
					pie : {
						borderWidth : 0,
		                allowPointSelect: true,
	                    dataLabels: {
	                        enabled: true,
	                        color: '#FFFFFF',
	                        distance : 5,
	                        formatter : function(){
	                        	if(this.y < 0.00001){
	                        		return this.percentage.toFixed(2) + '%'
	                        	}else{
	                        		return this.percentage.toFixed(2) + '%'
	                        	}
	                        }
	                    },
	                    visible : true,
	                    showInLegend: true
					}
				},
				series : [{
		            type: 'pie',
		            data: chartData.series
		        }],
				tooltip : {
					enabled: false
				},
				credits : {
					enabled: false
				},
	    	    exporting : {
	    	    	enabled: false
	    	    }
			});
			
		}
	}
	oc.statist.query.maincenter = {
		open : function(dataOption){
			var statQMainCenter = new statistQueryMainCenter();
			statQMainCenter.open(dataOption);
		}
	}
});