function showWarning(selector,opt){
	this.opt = {
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);
	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}
var swp = showWarning.prototype;

swp.init = function(){
	this.opt.selector.html("");
	this.dataObj = $("<div/>");
	this.dataObj.css({width:'100%',height:'100%'});
	this.opt.selector.html(this.dataObj);
	
	this.genGrid();
}
swp.genGrid = function(){
	var that = this;
	var opt = that.opt;
	if(opt.domains.substring(opt.domains.length-1) == ','){
		opt.domains = opt.domains.substring(0,opt.domains.length-1);
	}

	var domainId = 0;
	 
	var end = +new Date;
	var start = +new Date;
	switch(opt.time){
		case '24h': {
			start = start - 24*60*60*1000;
			break;
		}
		case '1w' : {
			start = start - 7*24*60*60*1000;
			break;
		}
		case'1m' : {
			start = start - 30*24*60*60*1000;
			break;
		}
		case'all' : {
			start = 0;
			break;
		}
	}
	//*全屏的时候字符串显示*//
	var isFullscreen = that.opt.index.toString().indexOf('fullscreen_');
	var fullClass = '';
	 if(isFullscreen >= 0){
		 fullClass = 'full';
	 }
	 //**//

	that.dg = oc.ui.datagrid({
		selector : that.dataObj ,
		url:oc.resource.getUrl('home/warning/get.htm'),
		queryParams:{
			groupId:0,
			alarmLevel:opt.alarmLevel,
			start:start,
			end:end,
			domainId: opt.domains,
			count:opt.warningCount
		},
		pagination : false,
		width : '100%',
		height : '100%',
		fit:true,
		columns : [ [
				{
			field : 'eventId',
			align : 'left',
			title : '',
			hidden : true
		},{
			field : 'sourceId',
			align : 'left',
			title : '',
			hidden : true
		},
		{
			field : 'instanceStatus',
			title:'',
			width : 30,
			formatter : function(value, row, index) {
				return "<span class='light-ico "+row.instanceStatus+"light' ></span>";
			}
		},
		{
			field : 'alarmContent',
			title : '告警内容',
			width : 280,
			align : 'left',
			ellipsis:true,
			formatter:function(value,row,rowIndex){
	        	 var formatterString = '';
                 if(value.length > 45){
            		 formatterString = '<label class="'+fullClass+'" style="cursor:pointer;" title="' + value.htmlspecialchars() + '" >'+value.htmlspecialchars().substr(0, 45)+'...'+'</label>';
                 }else{
            		 formatterString = '<label class="'+fullClass+'" style="cursor:pointer;" title="' + value.htmlspecialchars() + '" >'+value.htmlspecialchars()+'</label>';
                 }
                 return formatterString; 		
	         }
		}, {
			field : 'resourceName',
			title : '告警来源',
			width : 80,
			align : 'left',
			ellipsis:true,
			formatter:function(value,row,rowIndex){
	        	var formatterString = '';
	        	if(value != null){
	        		if(value.length > 15){
	        			formatterString = '<label class="'+fullClass+'" style="cursor:pointer;" title="' + value.htmlspecialchars() + '">'+ value.htmlspecialchars().substr(0, 15)+'...' +'</label>'
	        		}else{
	        			formatterString = '<label class="'+fullClass+'"  style="cursor:pointer;" title="' + value.htmlspecialchars() + '">'+ value.htmlspecialchars() +'</label>'
	        		}
	        	}
	        	return formatterString; 
	        }
		}, {
			field : 'ipAddress',
			title:'IP地址',
			width : 115,
			align : 'left',
			ellipsis:true
		},{
			field : 'eventTime',
			title : '产生时间',
			width : 115,
			align : 'left',
			ellipsis:true,
			 formatter:function(value,row,index){
	        		 if(value != null){
	        			 var date=new Date(value);
	        			 if(isNaN(date)){
	        				 return '<lable class="'+fullClass+'">'+value+'</lable>';
	        			 }else{
	        				 return '<lable class="'+fullClass+'">'+date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()))+'</lable>';
	        			 }
	        		 }
	        		 return null;
	        	 }
		} ]],
		//告警一览点击事件
		onClickCell : function(rowIndex, field, value){
			//告警内容
			if(field == 'alarmContent'){
				var row = that.dg.selector.datagrid('getRows')[rowIndex];
				var alarmId = row.eventID;
				var type = '1';
				var sysId = row.sysID;
				oc.resource.loadScript('resource/module/alarm-management/js/alarm-detailed-information.js',function(){
					 oc.alarm.detail.inform.open(alarmId,type,sysId);
	     			});
				
			};
			//告警来源
			if(field == 'resourceName'){
				var row = that.dg.selector.datagrid('getRows')[rowIndex];
				var resourceId = row.sourceID;
				var sysID = row.sysID;
				if(sysID == "LINK"){
					oc.resource.loadScripts([
				                                "resource/module/topo/unitTransform.js",
				                                "resource/module/topo/contextMenu/topoLinkInfoChart.js",
				                                "resource/module/topo/contextMenu/TopoLinkInfo.js"
				                            ], function () {
				                                new TopoLinkInfo({
				                                    type: "map",
				                                    onLoad: function () {
				                                        var $this = this;

				                                        var showMetric = $('#showMetric');
				                                        var showMetricChartsDiv = showMetric.find('#showMetricChartsDiv');
				                                        var showHideChart = showMetric.find('#infoCharts').html('指标信息图表');
				                                        showMetricChartsDiv.height(showHideChart.parent().height());
				                                        var showMetricDatagridDivHeight;
				                                        showMetricDatagridDivHeight = showMetric.height() - 10;
				                                        var showMetricDatagridDiv = showMetric.find('#showMetricDatagridDiv').height(showMetricDatagridDivHeight);
				                                        showMetricDatagridDiv.find('#showMetricDatagridParent').height(showMetricDatagridDivHeight - showHideChart.parent().height() - 15);
				                                        showMetricDatagridDiv.find('#showMetricDatagridParent').attr('style', 'width:100%;height:99%;');

				                                        //初始化曲线图
				                                        var chart = new TopoLinkInfoChart(this.fields.chart, 1);
				                                        showMetricChartsDiv.attr('style', 'display:none;');
				                                        //绑定chartDiv收缩
				                                        showMetricChartsDiv.find('.w-table-title').on('click', function () {
				                                            $this.showHideChartMethod(showMetric, showMetricDatagridDiv, showMetricChartsDiv, showHideChart);
				                                            showMetricChartsDiv.attr('style', 'display:none;');
				                                        });
				                                        this.root.on("click", ".topo_link_cell", function () {
				                                            var tmp = $(this);

				                                            showMetricChartsDiv.attr('style', 'display:block;');
				                                            showMetric.find(".ico-chartred").each(function () {
				                                                $(this).addClass("ico-chart");
				                                                $(this).removeClass("ico-chartred");
				                                            });
				                                            tmp.addClass("ico-chartred");
				                                            tmp.removeClass("ico-chart");

				                                            var metricId = tmp.attr("metricId");
				                                            var instanceId = tmp.attr("instanceId");

				                                            chart.setIds(metricId, instanceId);

				                                            $this.showHideChartMethod(showMetric, showMetricDatagridDiv, showMetricChartsDiv, showHideChart, 'show');
				                                        });
				                                        $this.load(resourceId);
				                                    }
				                                });
				                            });
				}else{
					oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
						oc.module.resmanagement.resdeatilinfonew.open({instanceId:resourceId});
					});
				}
			}
			//告警来源
			if(field == 'ipAddress'){
				var row = that.dg.selector.datagrid('getRows')[rowIndex];
				var resourceId = row.sourceID;
				var sysID = row.sysID;
				if(sysID == "LINK"){
					oc.resource.loadScripts([
				                                "resource/module/topo/unitTransform.js",
				                                "resource/module/topo/contextMenu/topoLinkInfoChart.js",
				                                "resource/module/topo/contextMenu/TopoLinkInfo.js"
				                            ], function () {
				                                new TopoLinkInfo({
				                                    type: "map",
				                                    onLoad: function () {
				                                        var $this = this;

				                                        var showMetric = $('#showMetric');
				                                        var showMetricChartsDiv = showMetric.find('#showMetricChartsDiv');
				                                        var showHideChart = showMetric.find('#infoCharts').html('指标信息图表');
				                                        showMetricChartsDiv.height(showHideChart.parent().height());
				                                        var showMetricDatagridDivHeight;
				                                        showMetricDatagridDivHeight = showMetric.height() - 10;
				                                        var showMetricDatagridDiv = showMetric.find('#showMetricDatagridDiv').height(showMetricDatagridDivHeight);
				                                        showMetricDatagridDiv.find('#showMetricDatagridParent').height(showMetricDatagridDivHeight - showHideChart.parent().height() - 15);
				                                        showMetricDatagridDiv.find('#showMetricDatagridParent').attr('style', 'width:100%;height:99%;');

				                                        //初始化曲线图
				                                        var chart = new TopoLinkInfoChart(this.fields.chart, 1);
				                                        showMetricChartsDiv.attr('style', 'display:none;');
				                                        //绑定chartDiv收缩
				                                        showMetricChartsDiv.find('.w-table-title').on('click', function () {
				                                            $this.showHideChartMethod(showMetric, showMetricDatagridDiv, showMetricChartsDiv, showHideChart);
				                                            showMetricChartsDiv.attr('style', 'display:none;');
				                                        });
				                                        this.root.on("click", ".topo_link_cell", function () {
				                                            var tmp = $(this);

				                                            showMetricChartsDiv.attr('style', 'display:block;');
				                                            showMetric.find(".ico-chartred").each(function () {
				                                                $(this).addClass("ico-chart");
				                                                $(this).removeClass("ico-chartred");
				                                            });
				                                            tmp.addClass("ico-chartred");
				                                            tmp.removeClass("ico-chart");

				                                            var metricId = tmp.attr("metricId");
				                                            var instanceId = tmp.attr("instanceId");

				                                            chart.setIds(metricId, instanceId);

				                                            $this.showHideChartMethod(showMetric, showMetricDatagridDiv, showMetricChartsDiv, showHideChart, 'show');
				                                        });
				                                        $this.load(resourceId);
				                                    }
				                                });
				                            });
				}else{
					oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
						oc.module.resmanagement.resdeatilinfonew.open({instanceId:resourceId});
					});
				}
			}
		},
		onLoadSuccess:function(){
			//全屏后字取消字符串截取
			if(isFullscreen){
				$(".full").parent().addClass("datagrid-full");
       	 	}
		}
	});
}

swp.refresh = function(){
	this.init();
}

