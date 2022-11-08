$(function(){
	function resourcesOutlook(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		this.reportXmlDataId = this.cfg.reportXmlDataId;
		this.reportTemplateId = this.cfg.reportTemplateId;
		this.reportName = this.cfg.reportName;
		this.reportTypeCode = this.cfg.reportTypeCode;
		this.reportTypeName = this.cfg.reportTypeName;
	}

	resourcesOutlook.prototype = {
		constructor : resourcesOutlook,
		cfg : undefined, // 外界传入的配置参数 json对象格式
		_defaults : {},
		reportXmlDataId : undefined,
		reportTemplateId : undefined,
		reportName : undefined,
		reportTypeCode : undefined,
		reportTypeName : undefined,
		url : oc.resource.getUrl("portal/report/reportTemplateXmlInfo/getXmlInfo.htm"),
		priviewUrl : oc.resource.getUrl("portal/report/reportTemplateList/getReportListByReportTemplateIdAndTime.htm"),
		dlgDom : undefined,
		contentDom : undefined,
		navBtn : undefined,
		floatnav : undefined,
		previewDom : undefined,
		isPreview : false,
		data_reg : /#--!!--#/,
		info_reg : /,/,
		defaultColor : '#790306,#6A0676,#0D366E,#8F541A,#2A5D1A,#275E59,#586A2C,#544775,#2E3061,#163C47',
		open : function(){
			var that = this;
			oc.util.ajax({
				url : this.url,
				timeout : null,
				data : {
					reportXmlDataFileId : this.reportXmlDataId
				},
				success : function(json) {
					if(json.code == 200){
						that.initDialog(json.data, '1');
					}else{
						alert('请求错误!')
					}
				}
			});
		},
		openPreview : function(){
			this.isPreview = true;
			this.initDialog({}, '2');
		},
		// type : 1、数据报表 2、查询预览
		initDialog : function(initPage, type){
			var that = this;
			// 初始化导航条
			this.createFloatNav();
			this.contentDom = $("<div/>").append(this.navBtn).append(this.floatnav);
			// 初始化dialog
			this.dlgDom = $("<div/>").css({
				'overflow-y' : 'auto',
				'overflow-x' : 'hidden'
			});
			this.dlgDom.append(this.contentDom);
			
			var dlgTitle = '';
			if(type == '1'){
				dlgTitle = initPage.name + "(" + initPage.timeScope + ")";
			}
			// 新建窗口
			this.dlgDom.dialog({
				title : "<div class='pop-tittle'>" +
							"<div class='pop-tittle-l'>" +
								"<div class='pop-tittle-m'>" +
								dlgTitle +
								"</div>" +
							"</div>" +
						"</div>",
				width : '1200px',
				height : '560px',
				onClose : function(){
					that.dlgDom.panel('destroy');
					if(typeof(that.cfg.callback) != 'undefined'){
						that.cfg.callback();
					}
				}
			});
			
			if(type == '1'){
				this.createLayout(initPage);
			}else{
				this.initPreviewPage();
				this.floatnav.hide();
				this.navBtn.hide();
			}
		},
		createFloatNav : function(){
			this.navBtn = $("<div/>").addClass('nav-btn');
			var reportNavTop = $("<div/>").addClass("reportNavTop").addClass('blur_div');
			var reportNavBottom = $("<div/>").addClass("reportNavBottom");
			var reportNavMiddle = $("<div/>").addClass("reportNavMiddle");
			reportNavTop.append(reportNavBottom.append(reportNavMiddle.append("<ul/>")));
			this.floatnav = $("<div/>").addClass('reportFloatnav')
							.append(reportNavTop)
							.append($("<div/>").addClass('blur_Bg'));
		},
		initPreviewPage : function(){
			var that = this;
			this.previewDom = $("<div/>").addClass('reportQueryToolbar');
			var start = $("<input>"), end = $("<input>"), query = $("<a/>").css({'float':'right'});
			this.previewDom.append($("<span/>").html('报告名称：'))
				.append($("<span/>").html('<B>' + this.reportName + '</B>&nbsp;'))
				.append($("<span/>").html('报告类型：'))
				.append($("<span/>").html('<B>' + this.reportTypeName + '</B>&nbsp;'))
				.append($("<span/>").html('&nbsp;开始时间：'))
				.append($("<span/>").append(start))
				.append($("<span/>").html('&nbsp;结束时间：'))
				.append($("<span/>").append(end))
				.append(query);
			start.datetimebox({
				width : '188px',
				showSeconds : false,
				required: true,
				value : new Date().stringify()
			});
			
			end.datetimebox({
				width : '188px',
				showSeconds : false,
				required: true,
				value : new Date().stringify()
			});
			query.linkbutton("RenderLB", {
				text : '执行',
				onClick : function(){
					that.floatnav.hide();
					that.navBtn.hide();
					
					var dateStartStr = start.datetimebox('getValue');	
					var dateEndStr = end.datetimebox('getValue');
					
					var dateStart = new Date(dateStartStr.replace(/-/g,"/"));
					var dateEnd = new Date(dateEndStr.replace(/-/g,"/"));
					
					var timeSub = dateEnd.getTime() - dateStart.getTime();
					if(timeSub<0 || timeSub==0){
						alert('开始日期应在结束日期之前！');
						return false;
					}else{
//						if(start.datetimebox('getValue') && end.datetimebox('getValue'))
							oc.util.ajax({
								url : that.priviewUrl,
								timeout : null,
								data : {
									reportTemplateId : that.reportTemplateId,
									dateStartStr : start.datetimebox('getValue'),
									dateEndStr : end.datetimebox('getValue')
								},
								success : function(json) {
									if(json.code == 200){
										that.createFloatNav();
										that.contentDom.empty().append($("<div/>").height('42px'))
											.append(that.navBtn)
											.append(that.floatnav);
										that.createLayout(json.data);
									}else{
										alert('请求错误!')
									}
								}
							});
					}
					
				}
			});
			// 新增一个占位div
			this.contentDom.append($("<div/>").height(this.previewDom.height()));
			this.dlgDom.prepend(this.previewDom);
		},
		addNav : function(){
			var that = this;
			// 记录各个章节的位置
			var dlgTop = this.dlgDom.offset().top;
			this.floatnav.find('ul > li').each(function(){
				var li = $(this);
				var no = li.attr('no');
				var divTop = that.contentDom.find("div[no='" + no + "']").offset().top;
				li.attr('top', divTop - dlgTop);
			});
			// 定位导航按钮的导航面板的位置
			var space = 12;
			var navBtnTop = this.navBtn.position().top;
			var navBtnLeft = this.navBtn.position().left;
			var navBtnWidth = this.navBtn.width();
			var floatWidth = this.floatnav.width();
			var floatHeight = Math.min(parseFloat(this.floatnav.height()), 400);
			this.floatnav.css({
				'top' : navBtnTop - space - floatHeight,
				'left' : navBtnLeft + navBtnWidth - floatWidth,
				'height' : floatHeight + 'px',
				'width' : '180px',
				'overflow-y' : 'scroll'
			}).hide();
			// 添加事件
			this.addNavBtnEvent();
			this.addNavScrollEvent();
			this.addContentDomScrollEvent();
		},
		addNavDom : function(no, name, navContentDom){
			li = $('<li/>').addClass('nav-section').attr('no', no)
				.append($('<span/>').addClass('nav-name').attr('title', name).html(name))
				.append($('<span/>').addClass('nav-stamp'));
			this.floatnav.find('ul').append(li);
			
			navContentDom.attr('no', no);
		},
		addNavBtnEvent : function(){
			var that = this;
			this.navBtn.toggle(function(){
				that.floatnav.show();
			}, function(){
				that.floatnav.hide();
			});
		},
		addNavScrollEvent : function(){
			var that = this;
			this.floatnav.find("li").each(function(){
				var li = $(this);
				var top = li.attr('top');
				if(that.isPreview){
					top -= 42;
				}
				li.on('click', function(){
					that.dlgDom.animate({
						scrollTop : parseFloat(top)
					}, 600);
				});
			});
		},
		addContentDomScrollEvent : function(){
			var that = this;
			// 初始化第一个导航的背景色
			var lis = this.floatnav.find("li");
			$(lis[0]).addClass('reportNavSelected');
			var dValue = parseFloat($(lis[0]).attr('top'));
			this.dlgDom.scroll(function(){
				var showPosition = 0;
				var contentTop = that.contentDom.offset().top;
				var dlgTop = that.dlgDom.offset().top;
				var contentTop = Math.abs(contentTop - dlgTop - dValue);
				for(var i = 0; i < lis.length; i ++){
					var liCurrent = $(lis[i]);
					var liCurrentTop = parseFloat(liCurrent.attr('top'));
					var liNext = $(lis[Math.min(i + 1, lis.length - 1)]);
					var liNextTop = parseFloat(liNext.attr('top'));
					if(contentTop < dValue || (contentTop >= liCurrentTop && contentTop < liNextTop)){
						showPosition = i;
						break;
					}
					showPosition = i;
				}
				lis.removeClass('reportNavSelected');
				$(lis[showPosition]).addClass('reportNavSelected');
			});
		},
		// 总布局
		createLayout : function(initPage){
			var that = this;
			var cycle = initPage.cycle;
			this.reportTypeCode = initPage.type;
			var directorys = initPage.reportDirectory;
			// 清空导航条
			this.floatnav.find('ul').empty();
			for(var i = 0; i < directorys.length; i ++){
				this.addDirectory(cycle, directorys, i);
			}
			// 导航
			this.addNav();
		},
		// 目录
		addDirectory : function(cycle, directorys, i){
			// 每个directory都有一个标题
			var directory = directorys[i];
			// 新增一个title
			var directoryTitleDom = $("<div/>");
			this.contentDom.append(directoryTitleDom);
			this.createTitle(directoryTitleDom, 'reportTitle', directory.name);
			//加入导航
			this.addNavDom(i, directory.name, directoryTitleDom);
			
			var directoryType = directory.type;
			var chapters = directory.chapter;
			for(var j = 0; j < chapters.length; j ++){
				this.addChapter(directoryType, i, cycle, chapters, j);
			}
		},
		// 章
		addChapter : function(directoryType, i, cycle, chapters, j){
			var chapter = chapters[j];
			// 新增一个title
			var chapterTitleDom = $("<div/>");
			this.contentDom.append(chapterTitleDom);
			// 业务报表没有二级菜单
			if(directoryType != '7'){
				this.createTitle(chapterTitleDom, 'reportTitle', chapter.name);
				//加入导航
				if(!!chapter.name){
					this.addNavDom('' + i + j, chapter.name, chapterTitleDom);
				}
			}
			// table的内容
			var typeObjs = new Array();
			typeObjs.push({
				name : 'table',
				data : chapter.table
			});
			if (!!chapter.chart && chapter.chart.length > 0) {
				typeObjs.push({
					name : 'chart',
					data : chapter.chart
				});
			}
			// 如果sort等于2则图表放在表格上面
			if (chapter.sort == '2') {
				typeObjs = typeObjs.reverse();
			}
			for(var l = 0; l < typeObjs.length; l ++){
				this.addSection(directoryType, cycle, typeObjs, l);
			}
		},
		// 节
		addSection : function(directoryType, cycle, typeObjs, l){
			var typeObj = typeObjs[l];
			if(typeObj.name == 'table'){
				var tables = typeObj.data;
				for(var k = 0; k < tables.length; k ++){
					var table = tables[k];
					if(!!table.name){
						var tableTitleDom = $("<div/>");
						this.contentDom.append(tableTitleDom);
						this.createTitle(tableTitleDom, 'reportSubTitle', table.name);
					}
					var tableDom = $("<div/>").addClass('reportTable');
					this.contentDom.append(tableDom);
					this.createTable(tableDom, table.columnsTitle, table.columnsData);
				}
			}else if(typeObj.name == 'chart'){
				var charts = typeObj.data;
				for(var k = 0; k < charts.length; k ++){
					var chart = charts[k];
					var chartDom = $("<div/>").height('300px');
					this.contentDom.append(chartDom);
					switch (chart.type) {
					case '1':
						chartDom.addClass('reportBarChart');
						this.createStackedBarChart(chartDom, chart);
						break;
					case '2':
						chartDom.addClass('reportLineChart');
						this.createLineChart(chartDom, chart, directoryType, cycle);
						break;
					case '3':
						chartDom.width('573px');
						chartDom.addClass('reportPieChart');
						this.createPieChart(chartDom, chart);
						break;
					case '4':
						chartDom.addClass('reportBarChart');
						this.createBarChart(chartDom, chart, directoryType);
						break;
					}
				}
			}
		},
		createTitle : function(titleDom, className,  title){
			titleDom.addClass(className).html(title);
		},
		createTable : function(tableDom, columnsTitle, columnsData){
			var table = $("<table border='1'/>");
			tableDom.append(table);
			// 表头
			var columnCnt = 0;
			var trNo1 = $("<tr/>");
			var trNo2 = $("<tr/>");
			table.append(trNo1);
			var columns = columnsTitle.columns;
			for(var i = 0; i < columns.length; i ++){
				var column = columns[i];
				var th = $("<th/>").html(column.text);
				if(column.apart == null){
					columnCnt++;
				}else{
					var aparts = column.apart.split(this.info_reg);
					for(var j = 0; j < aparts.length; j++){
						var th2 = $("<th/>").html(aparts[j]);
						trNo2.append(th2);
						columnCnt++;
					}
					th.attr('colSpan', aparts.length);
				}
				trNo1.append(th);
			}
			if(trNo2.find("th").length > 0){
				table.append(trNo2);
				trNo1.find("th").each(function(){
					if(!!!$(this).attr('colSpan') || (!!$(this).attr('colSpan') && $(this).attr('colSpan') == '1')){
						$(this).attr('rowSpan', 2);
					}
				});
			}
			
			// 数据
			var trs = "";
			var tableDatas = columnsData.tableData;
			for(var i = 0; !!tableDatas && i < tableDatas.length; i ++){
				var tableData = tableDatas[i];
				var tr = "<tr>";
				var changedatas=tableData.value.replace("#--!!--##--!!--#","#--!!--#null#--!!--#");
				var dataArray = changedatas.split(this.data_reg);
				for(var j = 0; j < dataArray.length; j ++){
					var tdData = dataArray[j] == 'null' ? '-' : dataArray[j];
					tr += "<td>" + tdData + "</td>";
				}
				// 数据列不足时自动补全
				for(var j = 0; j < Math.abs(columnCnt - dataArray.length); j ++){
					tr += "<td>-</td>";
				}
				trs += tr + "</tr>";
			}
			table.append(trs);
		},
		createBarChart : function(barDom, chart, directoryType){
			var dataArray = new Array();
			var chartDatas = chart.chartData;
			var chartDataSize = !!chartDatas ? chartDatas.length : 0;
			var barNormalSize = 5;
			var coverPosSize = 5;
			var horMaxSize = 10;
			var chartWidth = 1154;
			var chartDataValueSize = 0;
			for(var i = 0; i < chartDataSize; i ++){
				var chartData = chartDatas[i];
				var chartDataName=chartData.name;
				var chartDataValues = chartData.value.split(this.data_reg);
				chartDataValueSize = chartDataValues.length;
				// 判断空值
				for(var j = 0; j < chartDataValues.length; j ++){
					chartDataValues[j] = {
						y : chartDataValues[j] == 'null' ? null : parseFloat(chartDataValues[j])
					};
					// chartDataValues[j] = chartDataValues[j] == 'null' ? null : parseFloat(chartDataValues[j]);
				}
				if(chartDataName.length>10){
					chartDataName=chartDataName.substring(0,10)+"...";
				}else{
					chartDataName=chartDataName;
				}
				dataArray.push({
					color : this.getDefaultColor(i),
					name : chartDataName,
					data : chartDataValues
				});
			}
			var xAxis = chart.info.split(this.info_reg);
			// 数据处理
			if(chartDataValueSize < barNormalSize){
				for(var i = 0; i < coverPosSize - chartDataValueSize; i ++){
					for(var j = 0; j < dataArray.length; j ++){
						dataArray[j].data.push({
							y : null
						});
					}
					xAxis.push('');
				}
			}
			var firstData = dataArray[0].data;
			for(var i = 0; i < firstData.length; i++){
				var minIndex = 0;
				var maxIndex = 0;
				var firstY = firstData[i].y;
				if(firstY == null){
					minIndex = -1;
					maxIndex = -1;
					continue;
				}
				var min = firstY; max = firstY;
				for(var j = 1; j < dataArray.length; j++){
					var secondY = dataArray[j].data[i].y;
					if(secondY == null){
						minIndex = -1;
						maxIndex = -1;
						break;
					}else{
						min = Math.min(min, secondY);
						if(min == secondY){
							minIndex = j;
						}
						max = Math.max(max, secondY);
						if(max == secondY){
							maxIndex = j;
						}
					}
				}
				if(minIndex >= 0 && maxIndex >= 0){
					if(dataArray[maxIndex].data[i].y >=0){
						dataArray[maxIndex].data[i].dataLabels = {enabled : true, y : -12};
					}
					if(dataArray[minIndex].data[i].y >= 0){
						dataArray[minIndex].data[i].dataLabels = {enabled : true, y : 12};
					}
				}
			}
			// 新增一层样式div
			var container = $("<div/>").addClass('highchart-report').height(barDom.height());
			barDom.append(container);
			
			var rotation = 0, y = null, legendBorderWidth = 0, legendVerticalAlign = 'middle';
			// 纵向显示
			var chartContainer = container;
			var inverted = false, opposite = false;
			if(chartDataSize > horMaxSize){
				inverted = true;
				opposite = true;
			}
			if(inverted){
				chartWidth = 1138;
				rotation = 0;
				legendVerticalAlign = 'middle';
				
				barDom.height(barSize * chartDataSize);
				container.height(barDom.height());
			}
			// 图表开始
			chartContainer.highcharts({
				chart : {
		            type: 'column',
		            width : chartWidth,
		            backgroundColor: 'rgba(0,0,0,0)',
		            marginBottom : 70,
//		            spacingBottom : 0,
		            inverted : inverted,
		            options3d: {
		                enabled: true,
		                alpha: 0,
		                beta: 0,
		                depth: 5,
		                viewDistance: 300
		            }
		        },
		        title : {
		            text : chart.name, // title
		            align : 'left',
		            style : {
		            	"color" : "#23BB00",
		            	"font-size" : "12px",
		            	"font-weight" : "bolder"
		            }
		        },
		        xAxis : {
		        	categories : xAxis, // xAxis
		        	gridLineWidth : 0.2,
		        	gridLineColor : '#000000',
//		        	gridLineDashStyle : "Dot",
		        	tickLength : 10,
		        	lineWidth : 0,
		        	tickColor : '#000000',
		        	labels : {
		        		style : {
		        			"font-family" : 'Arial',
		        			"font-size" : '10px',
		        			"color" : '#9EA0A0'
		        		},
		        		y : y,
		        		rotation : rotation,
		        		maxStaggerLines : 1
		        	},
		        	showEmpty : true
		        },
		        yAxis : {
		        	title: null,
		        	lineWidth : 0,
		        	tickWidth : 0.1,
		        	tickLength : 0,
		        	gridLineWidth : 0.1,
		        	gridLineColor : '#000000',
//		        	gridLineDashStyle : "Dot",
		        	opposite : opposite,
		        	labels : {
		        		style : {
		        			"color" : '#9EA0A0'
		        		}
		        	}
		        },
		        legend : {
		        	layout : 'vertical',
		        	align: 'right',
		        	verticalAlign: legendVerticalAlign,
		            borderWidth : legendBorderWidth,
		            borderColor : '#000000',
		            itemStyle : {
	        			"font-family" : 'Arial',
		                "color" : "#9EA0A0",
		            	"font-size" : "8px",
		            	"font-weight" : "normal"
		            },
		            navigation: {
		                activeColor: '#FFFFFF',
		                animation: true,
		                arrowSize: 12,
		                inactiveColor: '#9EA0A0',
		                style: {
		                    "font-weight": 'bold',
		                    "color" : '#9EA0A0',
		                    "font-size": '12px'
		                }
		            }
		        },
		        plotOptions : {
		            column : {
		            	depth: 25,
		            	pointPadding : 0.2,
						dataLabels : {
							enabled : true,
							color : '#FFFFFF',
			                style: {
			                    "font-size" : '13px',
			                    "textShadow" : '0 0 3px black'
			                }
						},
						minPointLength : 5,
		                borderWidth : 0
		            }
		        },
		        series : dataArray, // data=[{name:string,data:array}]
				tooltip : {
					enabled: false
				},
				credits : {
					enabled: false
				},
	    	    exporting : {
	    	    	enabled: false
	    	    }
			}, function(chart){
				chart.redraw();
				if(rotation > 0)
					chartContainer.find('tspan').attr('style', 'letter-spacing:-1.4');
			});
		},
		createStackedBarChart : function(barDom, chart){
			var that = this;
			var xAxis = new Array();
			var dataArray = new Array();
			var chartDatas = chart.chartData;
			var chartDataSize = !!chartDatas ? chartDatas.length : 0;
			var barNormalSize = 10;
			var coverPosSize = 5;
			var horMaxSize = 25;
			var barSize = 40;
			var chartWidth = 1154;
			// 初始化数据
			for(var i = 0; i < chartDataSize; i ++){
				var chartData = chartDatas[i];
				var categoryName = "";
				if(chartData.ip != null && chartData.ip != undefined && chartData.ip != ""){
					categoryName = chartData.ip + "<br>";
				}
				// 如果大于标准显示个数则名称做处理
				categoryName += chartData.name.length > 10
								? chartData.name.substring(0, 10) + '...' : chartData.name;
				xAxis.push(categoryName);
				var data = new Array();
				for(var j = 0; j < chartDataSize; j ++){
					if(i == j){
						data.push(chartData.value == 'null' ? null : parseFloat(chartData.value));
					}else{
						data.push(null);
					}
				}
				var legendName = "";
				if(chartData.ip != null && chartData.ip != undefined && chartData.ip != ""){
					legendName = chartData.ip + "<br>" + chartData.name;
				}else{
					legendName = chartData.name;
				}
				dataArray.push({
					color : this.getDefaultColor(i),
					name : legendName,
					data : data
				});
			}
			// 数据处理
			var inverted = false;
			if(chartDataSize <= barNormalSize){
				if(chartDataSize <= coverPosSize){
					for(var i = 1; i < xAxis.length; i += 2){
						var tmp = xAxis.splice(i, xAxis.length - i, '');
						xAxis = xAxis.concat(tmp);
						var data = dataArray[i].data;
						tmp = dataArray.splice(i, dataArray.length - i, {
							type : 'column',
							showInLegend : false,
							data : []
						});
						dataArray = dataArray.concat(tmp);
						for(var j = i + 1; j < dataArray.length; j ++){
							var data = dataArray[j].data;
							tmp = data.splice(i, data.length - i, null);
							data = data.concat(tmp);
							dataArray[j].data = data;
						}
					}
				}
				var nowChartDataSize = xAxis.length;
				for(var i = 0; i < barNormalSize; i ++){
					var data;
					if(i < nowChartDataSize){
						data = dataArray[i].data;
					}else{
						data = new Array();
						xAxis.push('');
						dataArray.push({
							type : 'column',
							showInLegend : false,
							data : []
						});
					}
					var dValue = barNormalSize - data.length;
					for(var j = 0; j < dValue; j ++){
						data.push(null);
					}
					dataArray[i].data = data;
				}
			}
			// 横向坐标过多
			var rotation = 0, y = null, legendBorderWidth = 0, legendVerticalAlign = 'middle';
			if (chartDataSize > 10) {
				rotation = 60;
				y = 6;
			}
			// lenged过多
			if(chartDataSize > 8){
				legendBorderWidth = 1;
				legendVerticalAlign = 'top';
			}
			// 新增一层样式div
			var container = $("<div/>").addClass('highchart-report').height(barDom.height());
			barDom.append(container);
			// 纵向显示
			var chartContainer = container;
			var inverted = false, opposite = false;
			if(chartDataSize > horMaxSize){
				inverted = true;
				opposite = true;
			}
			if(inverted){
				chartWidth = 1138;
				rotation = 0;
				legendVerticalAlign = 'middle';
				
				barDom.height(barSize * chartDataSize);
				container.height(barDom.height());
			}
			
			// 图表开始
			chartContainer.highcharts({
				chart : {
		            type: 'column',
		            width : chartWidth,
		            backgroundColor: 'rgba(0,0,0,0)',
		            marginBottom : 75,
//		            spacingBottom : 0,
		            inverted : inverted,
		            options3d: {
		                enabled: true,
		                alpha: 0,
		                beta: 0,
		                depth: 5,
		                viewDistance: 300
		            }
		        },
		        title : {
		            text : chart.name, // title
		            align : 'left',
		            style : {
		            	"color" : "#23BB00",
		            	"font-size" : "12px",
		            	"font-weight" : "bolder"
		            }
		        },
		        xAxis : {
		        	categories : xAxis, // xAxis
		        	gridLineWidth : 0.2,
		        	gridLineColor : '#000000',
//		        	gridLineDashStyle : "Dot",
		        	tickLength : 10,
		        	lineWidth : 1,
		        	tickColor : '#000000',
		        	labels : {
		        		style : {
		        			"font-family" : 'Arial',
		        			"font-size" : '10px',
		        			"color" : '#9EA0A0',
		        			"font-weight" : 'normal'
		        		},
		        		y : y,
		        		rotation : rotation,
		        		maxStaggerLines : 1
		        	},
		        	showEmpty : true
		        },
		        yAxis : {
		        	min: 0,
		        	title: null,
		        	lineWidth : 0,
		        	tickWidth : 1,
		        	tickLength : 0,
		        	gridLineWidth : 0.1,
		        	gridLineColor : '#000000',
//		        	gridLineDashStyle : "Dot",
		        	opposite : opposite,
		        	labels : {
		        		style : {
		        			"color" : '#9EA0A0'
		        		}
		        	}
		        },
		        legend : {
		        	layout : 'vertical',
		        	align: 'right',
		        	verticalAlign: legendVerticalAlign,
		            borderWidth : legendBorderWidth,
		            borderColor : '#000000',
		            itemStyle : {
	        			"font-family" : 'Arial',
		                "color" : "#9EA0A0",
		            	"font-size" : "8px",
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
		        plotOptions : {
		            column : {
		                stacking: 'normal',
		                depth: 30,
		            	pointPadding : 0.2,
						dataLabels : {
							enabled : true,
							inside : false,
							color : '#FFFFFF',
			                style: {
			                    "font-size" : '13px',
			                    "textShadow" : '0 0 3px black'
			                }
						},
						minPointLength : 5,
		                borderWidth : 0
		            }
		        },
		        series : dataArray, // data=[{name:string,data:array}]
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
		},
		createLineChart : function(lineDom, chart, directoryType, cycle){
			var dates = chart.info.split(this.info_reg);
			var xAxis = new Array();
			for(var i = 0; i < dates.length; i ++){
				xAxis.push(this.formatterDate(dates[i]));
			}
			var dataArray = new Array();
			var chartDatas = chart.chartData;
			for(var i = 0; i < chartDatas.length; i ++){
				var chartData = chartDatas[i];
				var floatData = new Array();
				var stringDatas = chartData.value.split(this.data_reg);
				for(var j = 0; j < stringDatas.length; j ++){
					floatData.push(stringDatas[j] == 'null' ? null : parseFloat(stringDatas[j]));
				}
				var legendName = chartData.ip + "<br>(" + chartData.name + ")";
				if((this.reportTypeCode == '5') || (!!directoryType && directoryType == '5'))
					legendName = chartData.name;
				dataArray.push({
					color : this.getDefaultColor(i),
					name : legendName,
					data : floatData
				});
			}
			
			var step = 0;
			if(cycle == '1'){
				step = 13;
			}else if(cycle == '2'){
				step = 4;
			}else{
				step = 0;
			}
			var labelStep = step; // Math.ceil(xAxis.length / Math.min(xAxis.length, step));
			// 报表类型为趋势图
			var xGridLineWidth = 1;
			var plotLines = new Array();
			var connectNulls = false;
			var radius = 4;
			var rotation = 60;
			if((this.reportTypeCode == '5') || (!!directoryType && directoryType == '5')){
				radius = 1;
				xGridLineWidth = 0;
				labelStep = 0;
				rotation = 60;
				for(var i = 0; i < xAxis.length; i ++){
					if(/^ /g.test(xAxis[i])){
						plotLines.push({
							color: '#FF0000',
							width: 2,
							value: i
						});
						dataArray[0].data[i] = {
							y : dataArray[0].data[i],
							dataLabels : {
								enabled : true
							}
						};
					}
					xAxis[i] = xAxis[i].replace(" ", "");
				}
				dataArray[2].dashStyle = 'Dash';
				connectNulls = true;
			}
			// 新增一层样式div
			var container = $("<div/>").addClass('highchart-report').height(lineDom.height());
			lineDom.append(container);
			container.highcharts({
		        chart : {
		            type : 'line',
		            backgroundColor: 'rgba(0,0,0,0)',
		            marginBottom : 72
		        },
				title : {
					text : chart.name, // title
					align : 'left',
		            style : {
		            	"color" : "#23BB00",
		            	"font-size" : "12px",
		            	"font-weight" : "bolder"
		            }
				},
		        xAxis : {
		        	type : 'category',
		        	categories : xAxis, // xAxis
		        	gridLineWidth : xGridLineWidth,
		        	gridLineColor : '#FFFFFF',
		        	gridLineDashStyle : "Dot",
		        	lineWidth : 0.1,
		        	tickLength : 0,
		        	labels : {
		        		style : {
		        			"font-family" : 'Arial',
		        			"font-size" : '10px',
		        			"color" : '#9EA0A0',
		        			"font-weight" : 'normal'
		        		},
		        		rotation : rotation,
		        		maxStaggerLines : 1
		        	},
//		        	tickInterval : labelStep,
		        	tickmarkPlacement : 'on',
		            plotLines: plotLines
		        },
		        yAxis : {
		        	min: 0,
		        	title: null,
		        	lineWidth : 0,
		        	tickWidth : 0.1,
		        	tickLength : 0,
		        	gridLineWidth : 0.1,
		        	gridLineColor : '#FFFFFF',
		        	gridLineDashStyle : "Dot",
		        	labels : {
		        		style : {
		        			"color" : '#FFFFFF'
		        		},
		        		y : 12
		        	}
		        },
		        legend: {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'middle',
		            itemStyle : {
		            	"color" : "#9EA0A0",
		            	"font-size" : "8px",
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
		            line: {
		                dataLabels: {
		                    enabled: false
		                },
		                marker : {
		                	radius : radius
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
		createPieChart : function(pieDom, chart){
			var that = this;
			var dataArray = new Array();
			var chartDatas = chart.chartData;
			var amount = 0;
			for(var i = 0; !!chartDatas && i < chartDatas.length; i ++){
				var chartData = chartDatas[i];
				var category = new Array();
				category.push(chartData.name);
				var value = chartData.value == 'null' ? 0 : parseInt(chartData.value);
				category.push(value);
				if(chartData.color != null && chartData.color != undefined && chartData.color != ""){
					dataArray.push({
						name:chartData.name,
						y:value != 0 ? value : 0.000000000000000000000000000001,
						color : chartData.color
					});
				}else{
					dataArray.push({
						name:chartData.name,
						y:value != 0 ? value : 0.000000000000000000000000000001
					});
				}
				amount += value;
			}
			var options3d = {
	                enabled: true,
	                alpha: 45,
	                beta: 0
	            };
			var depth = 35;
			var visible = true;
			if(amount == 0){
				visible = false;
				options3d = null;
				depth = 0;
				for(var i = 0; i < dataArray.length; i++){
					dataArray[i].y = 0;
				}
			}
			// 排序解决datalabels重叠问题
			dataArray = dataArray.sort(function(a, b){
				return b.y - a.y;
			});
			// 新增一层样式div
			var container = $("<div/>").addClass('highchart-report').height(pieDom.height());
			pieDom.append(container);

			container.highcharts({
				chart: {
					type : 'pie',
		            backgroundColor: 'rgba(0,0,0,0)',
		            options3d: options3d
				},
//				colors : that.defaultColor.split(","),
				title : {
					text : chart.name + ' (警告总数：' + amount + '个)', // title
					floating : true,
					align : 'left',
					y:5,
		            style : {
		            	"color" : "#23BB00",
		            	"font-size" : "10px",
		            	"font-weight" : "bolder",
		            	"font-family" : 'Arial'
		            }
				},
		        legend: {
		            layout: 'vertical',
		            align: 'left',
		            verticalAlign: 'bottom',
		            itemStyle : {
		            	"color" : "#9EA0A0",
		            	"font-size" : "8px"
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
		                depth: depth,
						innerSize : '8',
						borderWidth : 0,
		                allowPointSelect: true,
	                    dataLabels: {
	                        enabled: true,
	                        color: '#FFFFFF',
	                        formatter : function(){
	                        	if(this.y < 0.00001){
	                        		return this.percentage.toFixed(2) + '%(0)'
	                        	}else{
	                        		return this.percentage.toFixed(2) + '%(' + this.y + ')'
	                        	}
	                        }
	                    },
	                    visible : visible,
	                    showInLegend: true
					}
				},
				series : [{
		            type: 'pie',
		            data: dataArray
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
			},function(chart){
				if(amount == 0){
					chart.renderer.image('themes/'+Highcharts.theme.currentSkin+'/images/comm/table/no-alarm.png', 265, 104, 130, 82).add();
				}
			});
		},
		getDefaultColor : function(i){
			var colors = this.defaultColor.split(',');
			if(i > colors.length){
				return colors[colors.length % i];
			}else{
				return colors[i];
			}
		},
		SetEveryOnePointColor : function(chart) {
			var series = chart.series;
			for(var i = 0; i < series.length; i++){
				//获得第一个序列的所有数据点
				var pointsList = series[i].points;
				//遍历设置每一个数据点颜色
				for (var j = 0; j < pointsList.length; j++) {
					var point = pointsList[j];
					var color = point.series.color;
					console.info(Highcharts.Color(color).setOpacity(1).get('rgba'));
					point.update({
						color: {
							linearGradient: { x1: 0, y1: 0, x2: 1, y2: 0 }, //横向渐变效果 如果将x2和y2值交换将会变成纵向渐变效果
							stops: [
							        [0, Highcharts.Color(color).setOpacity(1).get('rgba')],
							        [0.4, 'rgb(188, 9, 6)'],
							        [0.6, 'rgb(188, 9, 6)'],
							        [1, Highcharts.Color(color).setOpacity(1).get('rgba')]
							        ]
						}
					});
				}
			}
	    },
		formatterDate : function(date){
			return date;
//			return date.substring(date.lastIndexOf(' ') + 1, date.lastIndexOf(':'));
		}
	}
	oc.ns('oc.module.reportmanagement.vm.rol');
	oc.module.reportmanagement.vm.rol = {
		open : function(cfg) {
			var outlook = new resourcesOutlook(cfg);
			outlook.open();
		},
		openPreview : function(cfg) {
			var outlook = new resourcesOutlook(cfg);
			outlook.openPreview();
		}
	}
});