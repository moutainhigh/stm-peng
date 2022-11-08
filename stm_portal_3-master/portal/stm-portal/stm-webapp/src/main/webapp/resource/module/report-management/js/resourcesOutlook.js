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
		allChartData : [],
		chartHeight : 300,
		chartMargin : 5,
		curContentOffsetTop : 0,
		reportTitleClassHeight : 24,
		reportSubTitleClassHeight : 28,
		reportTableTdClassHeight : 31,//height + border
		isAutoScrolling : false,
		isNavClickTop : null,
		lastContentTop : 0,
		defaultColor : Highcharts.theme.reportColumnColor,
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
			if(initPage == null){
				return alert('报表模板缺失');
			}
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
			this.navBtn.on('click',function(){
				if ($(".reportFloatnav").is(":hidden")){
					$(".reportFloatnav").show();
				}else{
					$(".reportFloatnav").hide();
				}
			})
		},
		addExportEvent : function(reportTemplateId, startTime, endTime, type){
			var dateStart = new Date(startTime.replace(/-/g,"/"));
			var dateEnd = new Date(endTime.replace(/-/g,"/"));
			var timeSub = dateEnd.getTime() - dateStart.getTime();
			if(timeSub<0 || timeSub==0){
				alert('开始日期应在结束日期之前！');
				return false;
			}
			var iframeHtml = $("<iframe style='display:none' src='"+oc.resource.getUrl('portal/report/reportTemplateXmlInfo/exportReportListByReportTemplateIdAndTime.htm?reportTemplateId='+
					reportTemplateId+'&dateStartStr='+startTime+'&dateEndStr='+endTime+'&type='+type)+"'/>");
			iframeHtml.appendTo("body");
		},
		initPreviewPage : function(){
			var that = this;
			this.previewDom = $("<div/>").addClass('reportQueryToolbar');
			var start = $("<input>"), end = $("<input>"), query = $("<a/>"),
				exportExcel = $('<span/>').attr('class','fa fa-file-excel-o  light_blue').attr('title','导出EXCEL'),
				exportWord = $('<span/>').attr('class','fa fa-file-word-o  light_blue').attr('title','导出WORD'),
				exportPdf = $('<span/>').attr('class','fa fa-file-pdf-o light_blue').attr('title','导出PDF'),
				spanToolBar = $('<span/>').css({'margin-top':'7px','float':'right','floaico ico-excelt':'right','display':'none'});
			this.previewDom.append($("<span/>").html('报告名称：'))
				.append($("<span/>").html('<B>' + this.reportName + '</B>&nbsp;'))
				.append($("<span/>").html('报告类型：'))
				.append($("<span/>").html('<B>' + this.reportTypeName + '</B>&nbsp;'))
				.append($("<span/>").html('&nbsp;开始时间：'))
				.append($("<span/>").append(start))
				.append($("<span/>").html('&nbsp;结束时间：'))
				.append($("<span/>").append(end))
				.append(query)
				.append(spanToolBar.append(exportExcel).append(exportWord).append(exportPdf));
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
					spanToolBar.hide();
					
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
										that.contentDom.empty()
											.append(that.navBtn)
											.append(that.floatnav);
										that.createLayout(json.data);
										spanToolBar.show();
										//数据加载完成后重新计算导航的高度
										that.resetPosition();
									}else if(json.code == 201){
										spanToolBar.hide();
										//没有资源实例
										alert(json.data);
									}else{
										spanToolBar.show();
										alert('请求错误!')
									}
								}
							});
					}
				}
			});
			// 
			exportExcel.on('click', function(){
				that.addExportEvent(that.reportTemplateId, start.datetimebox('getValue'), end.datetimebox('getValue'), 'excel');
			});
			exportWord.on('click', function(){
				that.addExportEvent(that.reportTemplateId, start.datetimebox('getValue'), end.datetimebox('getValue'), 'word');
			});
			exportPdf.on('click', function(){
				that.addExportEvent(that.reportTemplateId, start.datetimebox('getValue'), end.datetimebox('getValue'), 'pdf');
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
				if(li.attr('top')){
					return true;
				}
				var no = li.attr('no');
				var contentTop = that.contentDom.offset().top;
				var divTop = that.contentDom.find("div[no='" + no + "']").offset().top;
				li.attr('top', Math.abs(contentTop - divTop) + (that.isPreview ? 72 : 0));
			});
		},
		addNavDom : function(name, top){
			li = $('<li/>').addClass('nav-section').attr('top', top)
				.append($('<span/>').addClass('nav-name').attr('title', name).html(name))
				.append($('<span/>').addClass('nav-stamp'));
			this.floatnav.find('ul').append(li);
			
//			navContentDom.attr('no', no);
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
				var top = parseFloat(li.attr('top'));
				if(that.isPreview){
					top += 30;
				}
				li.on('click', function(){
					//由于高度是重新计算过的，所以需要获取最新的高度
					top = $(this).attr('top');
					that.isAutoScrolling = true;
					that.isNavClickTop = parseFloat(top);
					that.dlgDom.animate({
						//高度是重新计算的，所以需要判断是否增加查询条件栏的高度30px
						scrollTop : parseFloat(that.isPreview ? that.isNavClickTop + 30 : that.isNavClickTop)
					}, 600,function(){
						that.isAutoScrolling = false;
					});
				});
			});
		},
		//计算导航的高度
		resetPosition: function(){
			var that = this;
			//遍历导航菜单，逐个重置高度
			$('div.reportFloatnav li').each(function(){
				//获取top属性，便于查找对应元素
				var _postion = $(this).attr('top');
				//获取报表内容的绝对高度（相对于浏览器）
				var contentTop = that.contentDom.offset().top;
				//获取导航对应的元素的绝对高度（相对于浏览器），因为有显示在同样高度的元素（如饼状图），所以需要过滤.reportTitle
				var divTop = that.contentDom.find("div[postion='" + _postion + "']").filter('.reportTitle').offset().top;
				//计算元素相对高度
				_top = Math.abs(contentTop - divTop);
				//重新设置导航和对应元素的位置
				$(this).attr('top', _top);
				$('div[postion="' + _postion + '"]').filter('.reportTitle').attr('postion', _top);
			});
		},
		addContentDomScrollEvent : function(){
			var that = this;
			// 初始化第一个导航的背景色
			var lis = this.floatnav.find("li");
			$(lis[0]).addClass('reportNavSelected');
			var dValue = parseFloat($(lis[0]).attr('top'));
			this.dlgDom.scroll(function(){
				
				if(that.isAutoScrolling){
					return;
				}
				
				
				var showPosition = 0;
				var contentTop = that.contentDom.offset().top;
				var dlgTop = that.dlgDom.offset().top;
				var contentTop = Math.abs(contentTop - dlgTop - dValue);
				var direction = "down";
				if(contentTop - that.lastContentTop < 0){
					//向上滚动
					direction = "up";
				}
				that.lastContentTop = contentTop;
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
				
				if(that.allChartData && that.allChartData.length > 0){
					if(that.isNavClickTop){
						//点击导航栏
						var minimumDistance = Math.abs(that.allChartData[0].dom.attr('postion') - that.isNavClickTop);
						var index = 0;
						for(var i = 0 ; i < that.allChartData.length ; i++){
							var distance = Math.abs(that.allChartData[i].dom.attr('postion') - that.isNavClickTop);
							if(minimumDistance > distance){
								minimumDistance = distance;
								index = i;
							}
						}
						if(minimumDistance <= (that.reportTitleClassHeight * 2 + that.reportSubTitleClassHeight)){
							if(that.allChartData[index].dom.height() < that.dlgDom.height() 
									&& that.allChartData[index + 1] 
									&& (that.allChartData[index + 1].dom.attr('postion') - that.allChartData[index].dom.attr('postion')) < that.dlgDom.height()){
								that.renderChartForScroll(index,true);
							}else{
								that.renderChartForScroll(index);
							}
						}
						
						that.isNavClickTop = null;
					}else{
						var distance = 0;
						var minimumDistance = Math.abs(contentTop - (that.allChartData[0].dom.attr('postion') - that.dlgDom.height()));
						var index = 0;
						for(var i = 0 ; i < that.allChartData.length ; i++){
							distance = Math.abs(contentTop - (that.allChartData[i].dom.attr('postion') - that.dlgDom.height()));
							if(minimumDistance > distance){
								minimumDistance = distance;
								index = i;
							}
						}
						if(direction == 'up'){
							if(minimumDistance < that.allChartData[index].dom.height()){
								// 滚动加载页面数据
								that.renderChartForScroll(index);
							}
						}else{
							if(minimumDistance < 180){
								// 滚动加载页面数据
								that.renderChartForScroll(index);
							}
						}
					}
				}
				
			});
		},
		// 总布局
		createLayout : function(initPage){
			var that = this;
			this.allChartData = [];
			var cycle = initPage.cycle;
			this.reportTypeCode = initPage.type;
			var directorys = initPage.reportDirectory;
			// 清空导航条
			this.floatnav.find('ul').empty();
			this.curContentOffsetTop = 0;
			for(var i = 0; i < directorys.length; i ++){
				this.addDirectory(cycle, directorys, i);
			}
			
			var tempData = new Array();
			for(var i = 0; i < this.allChartData.length; i++){
				this.contentDom.append(this.allChartData[i].dom);
				if(this.allChartData[i].type != 'title'){
					tempData.push(this.allChartData[i]);
				}
			}
			
			this.allChartData = tempData;
			
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
//				'height' : floatHeight + 'px',
				'width' : '180px',
				'overflow-y' : 'scroll'
			}).hide();
			// 添加事件
			this.addNavBtnEvent();
			this.addNavScrollEvent();
			this.addContentDomScrollEvent();
			
			// 初始化页面数据
			oc.ui.progress();
			this.renderChart();
			$.messager.progress('close');
		},
		renderChart : function(){
			var addContentHeight = 0, copyAllChartData = $.extend([], this.allChartData);
			this.allChartData.reverse();
			for(var i = 0; i < copyAllChartData.length; i++){
				if(addContentHeight > (this.dlgDom.height() * 2)){
					break;
				}
				var chartData = copyAllChartData[i];
				switch (chartData.type) {
				case 'table':
					this.createTable(chartData.dom, chartData.args[0], chartData.args[1],chartData.args[2]);
					break;
				case 'stackedBarChart':
					this.createStackedBarChart(chartData.dom, chartData.args[0]);
					break;
				case 'barChart':
					this.createBarChart(chartData.dom, chartData.args[0], chartData.args[1]);
					break;
				case 'pieChart':
					this.createPieChart(chartData.dom, chartData.args[0]);
					break;
				case 'lineChart':
					this.createLineChart(chartData.dom, chartData.args[0], chartData.args[1], chartData.args[2]);
					break;
				}
				addContentHeight += chartData.dom.height();
				this.allChartData.pop();
			}
			// 导航
			this.allChartData.reverse();
		},
		renderChartForScroll : function(index,isContinue){
			switch (this.allChartData[index].type) {
			case 'table':
				this.createTable(this.allChartData[index].dom, this.allChartData[index].args[0], this.allChartData[index].args[1],this.allChartData[index].args[2]);
				break;
			case 'stackedBarChart':
				this.createStackedBarChart(this.allChartData[index].dom, this.allChartData[index].args[0]);
				break;
			case 'barChart':
				this.createBarChart(this.allChartData[index].dom, this.allChartData[index].args[0], this.allChartData[index].args[1]);
				break;
			case 'pieChart':
				this.createPieChart(this.allChartData[index].dom, this.allChartData[index].args[0]);
				break;
			case 'lineChart':
				this.createLineChart(this.allChartData[index].dom, this.allChartData[index].args[0], this.allChartData[index].args[1], this.allChartData[index].args[2]);
				break;
			}
			
			this.allChartData.splice(index,1);
			
			if(isContinue){
				this.renderChartForScroll(index);
			}
			//滚动加载数据后重新计算导航的高度
			this.resetPosition();
		},
		// 目录
		addDirectory : function(cycle, directorys, i){
			// 每个directory都有一个标题
			var directory = directorys[i];
			// 新增一个title
			var directoryTitleDom = $("<div/>");
			//this.contentDom.append(directoryTitleDom);
			//this.createTitle(directoryTitleDom, 'reportTitle', directory.name);
			//加入导航
			//this.addNavDom(i, directory.name, directoryTitleDom);
			// push data in array
			this.allChartData.push({
				type : 'title',
				dom : directoryTitleDom,
				args : ['reportTitle', directory.name]
			});
			this.createTitle(directoryTitleDom, 'reportTitle', directory.name);
			this.addNavDom(directory.name, this.curContentOffsetTop);
			directoryTitleDom.attr('postion',this.curContentOffsetTop);
			this.curContentOffsetTop += this.reportTitleClassHeight;
			
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
			//this.contentDom.append(chapterTitleDom);
			// 业务报表没有二级菜单
			if(directoryType != '7'){
				//this.createTitle(chapterTitleDom, 'reportTitle', chapter.name);
				//加入导航
				if(!!chapter.name){
					//this.addNavDom('' + i + j, chapter.name, chapterTitleDom);
					this.addNavDom(chapter.name, this.curContentOffsetTop);
				}
				// push data in array
				this.allChartData.push({
					type : 'title',
					dom : chapterTitleDom,
					args : ['reportTitle', chapter.name]
				});
				this.createTitle(chapterTitleDom, 'reportTitle', chapter.name);
				chapterTitleDom.attr('postion',this.curContentOffsetTop);
				this.curContentOffsetTop += this.reportTitleClassHeight;
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
			var that = this;
			var typeObj = typeObjs[l];
			if(typeObj.name == 'table'){
				var tables = typeObj.data;
				for(var k = 0; k < tables.length; k ++){
					var table = tables[k];
					if(!!table.name){
						var tableTitleDom = $("<div/>");
						//this.contentDom.append(tableTitleDom);
						//this.createTitle(tableTitleDom, 'reportSubTitle', table.name);
						// push data in array
						this.allChartData.push({
							type : 'title',
							dom : tableTitleDom,
							args : ['reportSubTitle', table.name]
						});
						this.createTitle(tableTitleDom, 'reportSubTitle', table.name);
						tableTitleDom.attr('postion',this.curContentOffsetTop);
						that.curContentOffsetTop += that.reportSubTitleClassHeight;
					}
					var addTd = 2;
					if(this.reportTypeCode == 4){
						addTd = 1;
					}
					var tableDataRows = 0;
					if(table.columnsData && table.columnsData.tableData && table.columnsData.tableData.length > 0){
						tableDataRows = table.columnsData.tableData.length;
					}
					//去掉高度设置，因为table的tr高度并不是固定的，所以这样设置高度会导致后面元素的高度计算错误
//					var tableDom = $("<div/>").addClass('reportTable').height(((tableDataRows + 2) * that.reportTableTdClassHeight) + 'px');
					var tableDom = $("<div/>").addClass('reportTable');
					tableDom.attr('postion',this.curContentOffsetTop);
					//this.contentDom.append(tableDom);
					//this.createTable(tableDom, table.columnsTitle, table.columnsData);
					// push data in array
					this.allChartData.push({
						type : 'table',
						dom : tableDom,
						args : [table.columnsTitle, table.columnsData,directoryType]
					});
					that.curContentOffsetTop += ((tableDataRows + 2) * that.reportTableTdClassHeight);
				}
			}else if(typeObj.name == 'chart'){
				var charts = typeObj.data;
				var isPieChart = false;
				for(var k = 0; k < charts.length; k ++){
					var chart = charts[k], charType = null, args = null;
					var chartDom = $("<div/>").height(that.chartHeight + 'px');
					chartDom.attr('postion',this.curContentOffsetTop);
					//this.contentDom.append(chartDom);
					switch (chart.type) {
					case '1':
						chartDom.addClass('reportBarChart');
						//this.createStackedBarChart(chartDom, chart);
						charType = 'stackedBarChart';
						args = [chart];
						break;
					case '2':
						chartDom.addClass('reportLineChart');
						//this.createLineChart(chartDom, chart, directoryType, cycle);
						charType = 'lineChart';
						args = [chart, directoryType, cycle];
						break;
					case '3':
						chartDom.width('572px');
						chartDom.addClass('reportPieChart');
						isPieChart = true;
						//this.createPieChart(chartDom, chart);
						charType = 'pieChart';
						args = [chart];
						break;
					case '4':
						chartDom.addClass('reportBarChart');
						//this.createBarChart(chartDom, chart, directoryType);
						charType = 'barChart';
						args = [chart, directoryType];
						break;
					}
					// push data in array
					this.allChartData.push({
						type : charType,
						dom : chartDom,
						args : args
					});
					that.curContentOffsetTop += (that.chartHeight + that.chartMargin * 2);
				}
				if(isPieChart){
					that.curContentOffsetTop -= (that.chartHeight + that.chartMargin * 2) * 2;
				}
			}
		},
		createTitle : function(titleDom, className,  title){
			titleDom.addClass(className).html(title);
		},
		createTable : function(tableDom, columnsTitle, columnsData,directoryType){
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
			
			if((this.reportTypeCode == 2 || (!!directoryType && directoryType == 2)) && trNo2.find("th").length <= 0){
				$(table).css('table-layout','fixed');
			}
			
			// 数据
			var trs = "";
			if(columnsData!=null){
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
			}

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
				var chartDataValues = chartData.value.split(this.data_reg);
				chartDataValueSize = chartDataValues.length;
				// 判断空值
				for(var j = 0; j < chartDataValues.length; j ++){
					chartDataValues[j] = {
						y : chartDataValues[j] == 'null' ? null : parseFloat(chartDataValues[j])
					};
				}
				dataArray.push({
					color : this.getDefaultColor(i),
					name : chartData.name,
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
				/*if(minIndex >= 0 && maxIndex >= 0){
					if(dataArray[maxIndex].data[i].y >=0){
						dataArray[maxIndex].data[i].dataLabels = {enabled : true, y : -12};
					}
					if(dataArray[minIndex].data[i].y >= 0){
						dataArray[minIndex].data[i].dataLabels = {enabled : true, y : 12};
					}
				}*/
			}
            
			$(dataArray).each(function(){
			  this.data && $(this.data).each(function(){
			      if(this.y && parseInt(this.y) > 0){
					  this.dataLabels = {y : -20};
				   }else if(this.y && parseInt(this.y) < 0){  
					  this.dataLabels={y: 20}; 
				   }
			   })
			});

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
		            	"color" : Highcharts.theme.plotOptions.column.dataLabels.color,
		            	"font-size" : "12px",
		            	"font-weight" : "bolder"
		            }
		        },
		        xAxis : {
		        	categories : xAxis, // xAxis
		        	gridLineWidth : 0.2,
		        	gridLineColor : Highcharts.theme.plotOptions.column.gridLineColor,
//		        	gridLineDashStyle : "Dot",
		        	tickLength : 10,
		        	lineWidth : 0,
		        	tickColor : Highcharts.theme.plotOptions.column.tickColor,
		        	labels : {
		        		style : {
		        			"font-family" : 'Arial',
		        			"font-size" : '12px',
		        			"color" : Highcharts.theme.plotOptions.column.dataLabels.color
		        		},
		        		formatter: function () {
		        			var str = this.value.split('<br>');
		        			var name = str[str.length-1].length > 8 ? str[str.length-1].substring(0, 8) + '...' : this.value;
		        			var titleStr = this.value.replace('<br>',' ');
		        			var returnValue = this.value.replace(str[str.length-1], name);
		                    return '<span title="' + titleStr + '">' + returnValue + '</span>';
		                },
		                useHTML:true,
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
		        	gridLineColor : Highcharts.theme.plotOptions.column.gridLineColor,
//		        	gridLineDashStyle : "Dot",
		        	opposite : opposite,
		        	labels : {
		        		style : {
		        			"color" : Highcharts.theme.plotOptions.column.dataLabels.color
		        		}
		        	}
		        },
		        legend : {
		        	layout : 'vertical',
		        	align: 'right',
		        	verticalAlign: legendVerticalAlign,
		            borderWidth : legendBorderWidth,
		            borderColor : Highcharts.theme.plotOptions.column.legend.borderColor,
		            itemStyle : {
	        			"font-family" : 'Arial',
		                "color" : Highcharts.theme.plotOptions.column.legend.itemStyle.color,
		            	"font-size" : "12px",
		            	"font-weight" : "normal"
		            },
		            navigation: {
		                animation: true,
		                arrowSize: 12,
		                style: {
		                    "font-weight": 'bold',
		                    "color" : Highcharts.theme.plotOptions.column.navigation.style.color,
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
							color : Highcharts.theme.plotOptions.column.dataLabels.color,
			                style: {
			                    "font-size" : '13px'
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
				categoryName += chartData.name;
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
		            	"color" : Highcharts.theme.plotOptions.column.dataLabels.color,
		            	"font-size" : "12px",
		            	"font-weight" : "bolder"
		            }
		        },
		        xAxis : {
		        	categories : xAxis, // xAxis
		        	gridLineWidth : 0.2,
		        	gridLineColor : Highcharts.theme.plotOptions.column.gridLineColor,
//		        	gridLineDashStyle : "Dot",
		        	tickLength : 10,
		        	lineWidth : 1,
		        	tickColor : Highcharts.theme.plotOptions.column.tickColor,
		        	labels : {
		        		style : {
		        			"font-family" : 'Arial',
		        			"font-size" : '12px',
		        			"color" : Highcharts.theme.plotOptions.column.dataLabels.color,
		        			"font-weight" : 'normal'
		        		},
		        		formatter: function () {
		        			var str = this.value.split('<br>');
		        			var name = str[str.length-1].length > 8 ? str[str.length-1].substring(0, 8) + '...' : this.value;
		        			var titleStr = this.value.replace('<br>',' ');
		        			var returnValue = this.value.replace(str[str.length-1], name);
		                    return '<span title="' + titleStr + '">' + returnValue + '</span>';
		                },
		                useHTML:true,
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
		        	gridLineWidth : 0.2,
		        	gridLineColor :Highcharts.theme.plotOptions.column.gridLineColor,
//		        	gridLineDashStyle : "Dot",
		        	opposite : opposite,
		        	labels : {
		        		style : {
		        			"color" : Highcharts.theme.plotOptions.column.dataLabels.color
		        		}
		        	}
		        },
		        legend : {
		        	layout : 'vertical',
		        	align: 'right',
		        	verticalAlign: legendVerticalAlign,
		            borderWidth : legendBorderWidth,
		            borderColor : Highcharts.theme.plotOptions.column.legend.borderColor,
		            itemStyle : {
	        			"font-family" : 'Arial',
		                "color" : Highcharts.theme.plotOptions.column.legend.itemStyle.color,
		            	"font-size" : "12px",
		            	"font-weight" : "normal"
		            },
		            navigation: {
		                animation: true,
		                arrowSize: 12,
		                style: {
		                    "font-weight" : 'bold',
		                    "color" :  Highcharts.theme.plotOptions.column.navigation.style.color,
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
							color : Highcharts.theme.plotOptions.column.dataLabels.color,
			                style: {
			                    "font-size" : '13px'
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
				connectNulls = false;
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
		            	"color" : Highcharts.theme.plotOptions.line.title.color,
		            	"font-size" : "12px",
		            	"font-weight" : "bolder"
		            }
				},
		        xAxis : {
		        	type : 'category',
		        	categories : xAxis, // xAxis
		        	gridLineWidth : xGridLineWidth,
		        	gridLineColor : Highcharts.theme.plotOptions.line.gridLineColor,
		        	gridLineDashStyle : "Dot",
		        	lineWidth : 0.1,
		        	tickLength : 0,
		        	labels : {
		        		style : {
		        			"font-family" : 'Arial',
		        			"font-size" : '12px',
		        			"color" : Highcharts.theme.plotOptions.line.dataLabels.color,
		        			"font-weight" : 'normal'
		        		},
		        		formatter: function () {
		        			var str = this.value.split('<br>');
		        			var name = str[str.length-1].length > 8 ? str[str.length-1].substring(0, 8) + '...' : this.value;
		        			var titleStr = this.value.replace('<br>',' ');
		        			var returnValue = this.value.replace(str[str.length-1], name);
		                    return '<span title="' + titleStr + '">' + returnValue + '</span>';
		                },
		                useHTML:true,
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
		        	gridLineColor : Highcharts.theme.plotOptions.line.gridLineColor,
		        	gridLineDashStyle : "Dot",
		        	labels : {
		        		style : {
		        			"color" : Highcharts.theme.plotOptions.line.dataLabels.color
		        		},
		        		y : 12
		        	}
		        },
		        legend: {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'middle',
		            itemStyle : {
		            	"color" : Highcharts.theme.plotOptions.line.legend.itemStyle.color,
		            	"font-size" : "12px",
		            	"font-weight" : "normal"
		            },
		            navigation: {
		                activeColor: Highcharts.theme.plotOptions.line.navigation.activeColor,
		                animation: true,
		                arrowSize: 12,
		                inactiveColor: Highcharts.theme.plotOptions.line.navigation.inactiveColor,
		                style: {
		                    "font-weight" : 'bold',
		                    "color" : Highcharts.theme.plotOptions.line.navigation.style.color,
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
			var chartDataName="";
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
		            	"color" : Highcharts.theme.plotOptions.pie.title.color,
		            	"font-size" : "12px",
		            	"font-weight" : "bolder",
		            	"font-family" : 'Arial'
		            }
				},
		        legend: {
		            layout: 'vertical',
		            align: 'left',
		            verticalAlign: 'bottom',
		            itemStyle : {
		            	"color" : Highcharts.theme.plotOptions.pie.legend.itemStyle.color,
		            	"font-size" : "12px"
		            },
		            navigation: {
		                activeColor:Highcharts.theme.plotOptions.pie.navigation.activeColor,// '#FFFFFF',
		                animation: true,
		                arrowSize: 12,
		                inactiveColor: Highcharts.theme.plotOptions.pie.navigation.inactiveColor,
		                style: {
		                    "font-weight" : 'bold',
		                    "color" : Highcharts.theme.plotOptions.pie.navigation.style.color,
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
	                        color: '#666',//'#FFFFFF',
	                        formatter : function(){
	                        	if(this.y < 0.00001){
	                        		return '<b style="color:'+Highcharts.theme.plotOptions.pie.dataLabels.color+'">'+this.percentage.toFixed(2) + '%(0)'+'</b>'
	                        	}else{
	                        		return '<b style="color:'+Highcharts.theme.plotOptions.pie.dataLabels.color+'">'+this.percentage.toFixed(2) + '%(' + this.y + ')'+'</b>'
	                        	}
	                        }
	                    },
	                    visible : visible,
	                    showInLegend: true
					}
				},
				series : [{
		            type: 'pie',
		            data: dataArray,
		            dataLabels: {
		                floating : true
			 }
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
					chart.renderer.image(Highcharts.theme.reportPieNoAlarm, 265, 104, 130, 82).add();
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
	oc.ns('oc.module.reportmanagement.report.rol');
	oc.module.reportmanagement.report.rol = {
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