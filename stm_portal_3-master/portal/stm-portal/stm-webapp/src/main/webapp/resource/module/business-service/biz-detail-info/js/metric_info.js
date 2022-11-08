(function(){
	function bizMetricInfo(cfg){
		this.cfg = cfg;
	}
	
	bizMetricInfo.prototype={
			cfg : undefined,
			selector : undefined,
			metricInfo : undefined,
			init : function(){
				var that = this;
				that.selector = that.cfg.selector;
				
				that.getBizMetricData();
			},
			getBizMetricData:function(){
				var that = this;
				oc.util.ajax({
					url:oc.resource.getUrl('portal/business/canvas/getMetricInfoByNodeId.htm'),
					data:{nodeId:that.cfg.nodeId,parentInstanceId:that.cfg.parentInstanceId},
					successMsg:null,
					success:function(data){ 
						var queryArr = {};
						var parentInstanceId = that.cfg.parentInstanceId;
						
						if(data&&data.data==1){
						
							alert('关联资源未监控!');
							$(".tinytool").attr('style','display:none');
						}else if(data&&data.data==2){
							alert('无相关数据!');
							$(".tinytool").attr('style','display:none');
						}else if(data&&data.data){
							that.metricInfo = data.data;
							var metricInfoArr = new Array();
							for(key in data.data){
								var dataObj = data.data[key];
								var instanceId = key;
								
								for(var x=0;x<dataObj.length;x++){
									var item = dataObj[x];
									if(!item) continue;
									
									item['instanceId'] = key;
									metricInfoArr.push(item);
								}
							}
							that.metricInfoArr = metricInfoArr;
							var pageInfo = {};
							if(metricInfoArr.length>5){
								pageInfo = {startNo:0,endNo:4,total:metricInfoArr.length};
							}
							that.pageInfo = pageInfo;
							that.renderTop();
						}else{
							alert('无相关数据!');
							$(".tinytool").attr('style','display:none');
						}
					}
				})
				
			},
			renderTop:function(){
				var that = this;
				var height = 140;
				var width = 210;
				var topDiv = $('<div/>').height(height).addClass('margin15px');
				var metricInfoArr = that.metricInfoArr;
//				
				var scrollerLeft = $('<div/>');
				if(metricInfoArr.length>5)scrollerLeft.html('<div class="scrollerLeft tabs-header" style="display:none;"><div class="tabs-scroller-left" name="scroller"  style="display:block"></div>');
				topDiv.append(scrollerLeft);
				that.selector.append(topDiv);
				
				var flag = true;
				for(var i=0;i<metricInfoArr.length;i++){
//				for(var i=0;i<11;i++){
					var display = 'block';
					if(i>4) display = 'none';
					var dataItem = metricInfoArr[i];
					if(!dataItem) continue;
					
					var chartHeight = height-40;
					var divMetric = $('<div/>').attr('style','float:left;display:'+display+';').height(height).width(width)
					.attr('id','parentChartDiv_'+i).attr('name','parentChartDiv').attr('class','oc-pointer-operate');
					var divChart = $('<div/>').attr('align','center').height(chartHeight).width(width)
					.html('<div id="metricDiv_'+i+'" style="width:'+chartHeight+'px;height:'+chartHeight+'px;"></div>');//.attr('style','background-color:green;')
					
					var textStyle= '';//'style="font-weight:bold;"';
					if(flag && 'PerformanceMetric'==dataItem.metricType){
						textStyle= 'class="biz_metric_selected"';
						flag = false;
					}
					var divText = $('<div/>').attr('align','center').height(40).width(width)
					.html('<div title="'+dataItem.metricName+'" style="width:170px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;" name="textSpan" '+textStyle+'>'+dataItem.metricName+'</div><span name="textSpan" '+textStyle+'>'+dataItem.metricDate+'</span>')
					.attr('name','metricTextDiv').attr('instanceId',dataItem.instanceId).attr('metricType',dataItem.metricType)
					.attr('metricId',dataItem.metricId).attr('unit',dataItem.metricUnit);//.attr('style','line-height:20px;margin-top:5px;');
					divMetric.append(divChart).append(divText);
					topDiv.append(divMetric);
					
					var param = {};
					param.stops= [[0, '#55BF3B']];
					if('PerformanceMetric'==dataItem.metricType){
						if('%'==dataItem.metricUnit){
							if('--'==dataItem.metricValue){
								param.value=dataItem.metricValue;
								param.label = '--';
								param.stops= null;
							}else{
								param.value=dataItem.metricValue;
								param.label = dataItem.metricValue;
							}
						}else{
							if('--'==dataItem.metricValue){
								param.label = '--';
								param.stops= null;
								param.value = null;
							}else{
								param.value = 100;
								param.label = dataItem.metricValue;
							}
							
						}
						
						if(dataItem.metricState){
							if(dataItem.metricState == 'NORMAL'){
								param.stops = [[0, '#55BF3B']];
							}else if(dataItem.metricState == 'WARNING'){
								param.stops = [[0, '#FFFF37']];
							}else if(dataItem.metricState == 'SERIOUS'){
								param.stops = [[0, '#FF5809']];
							}else if(dataItem.metricState == 'CRITICAL'){
								param.stops = [[0, '#DF5353']];
							}
						}
						
					}else if('AvailabilityMetric'==dataItem.metricType){
						param.value = 100;
						if('CRITICAL'==dataItem.metricValue){
							param.label = '不可用';
							param.stops= [[0, '#DF5353']];
						}else{
							param.label = '可用';
							param.stops= [[0, '#428ad6']];
						}
					}
					that.createCharts(topDiv.find('#metricDiv_'+i),param);
				}
				that.renderLow();
				var scrollerRight = $('<div/>');
				if(metricInfoArr.length>5)scrollerRight.html('<div class="scrollerRight  tabs-header"><div class="tabs-scroller-right" name="scroller" style="display:block"></div></div>');
				topDiv.append(scrollerRight);
				
				if(metricInfoArr.length>5){
					topDiv.find('div[name=scroller]').on('click',function(){
						var target = $(this);
						var pageInfo = that.pageInfo;
						if(target.attr('class')=='tabs-scroller-right'){
							if(pageInfo.endNo<(pageInfo.total-1)){
								var newEnd = pageInfo.endNo+1;
								var newStart = pageInfo.startNo+1;
								topDiv.find('#parentChartDiv_'+newEnd).show();
								topDiv.find('#parentChartDiv_'+pageInfo.startNo).hide();
								
								if(newEnd==(pageInfo.total-1)){
									target.parent().hide();
								}
								if(newStart>0){
									topDiv.find('div[class=tabs-scroller-left]').parent().show();
								}
								pageInfo.endNo = newEnd;
								pageInfo.startNo = newStart;
							}
						}else if(target.attr('class')=='tabs-scroller-left'){
							if(pageInfo.startNo>0){
								var newEnd = pageInfo.endNo-1;
								var newStart = pageInfo.startNo-1;
								topDiv.find('#parentChartDiv_'+pageInfo.endNo).hide();
								topDiv.find('#parentChartDiv_'+newStart).show();
								
								if(newStart==0){
									target.parent().hide();
								}
								if(newEnd<(pageInfo.total-1)){
									topDiv.find('div[class=tabs-scroller-right]').parent().show();
								}
								pageInfo.endNo = newEnd;
								pageInfo.startNo = newStart;
							}
						}
					});
				}
				//空间阻止了冒泡,多绑定一次
				topDiv.find('div[class=highcharts-container]').on('click',function(){
					var targetChild = $(this);
					var targetParent = targetChild.parent().parent().parent();
					var target = targetParent.find('div[name=metricTextDiv]');
					if(target.attr('metricType')=='PerformanceMetric'){
						topDiv.find('span[name=textSpan]').attr('class','');
						topDiv.find('div[name=textSpan]').attr('class','');
						
						//biz_metric_selected
						target.find('span[name=textSpan]').addClass('biz_metric_selected');
						target.find('div[name=textSpan]').addClass('biz_metric_selected');
						var instaceId = target.attr('instanceId');
						var metricId = target.attr('metricId');
						var unit = target.attr('unit');
						
						that.chartObj.setIdsByBizType(metricId, instaceId);
						that.lowDiv.find('#unitSpan').html(unit);
					}else{
						alert('该指标无线图!');
					}
				})
				
				topDiv.find('div[name=parentChartDiv]').on('click',function(){
					var targetParent = $(this);
					var target = targetParent.find('div[name=metricTextDiv]');
					if(target.attr('metricType')=='PerformanceMetric'){
//					if(true){
						topDiv.find('span[name=textSpan]').attr('class','');
						topDiv.find('div[name=textSpan]').attr('class','');
						//biz_metric_selected
						target.find('span[name=textSpan]').addClass('biz_metric_selected');
						target.find('div[name=textSpan]').addClass('biz_metric_selected');
						var instaceId = target.attr('instanceId');
						var metricId = target.attr('metricId');
						var unit = target.attr('unit');
						
						that.chartObj.setIdsByBizType(metricId, instaceId);
						that.lowDiv.find('#unitSpan').html(unit);
//						that.chartObj.setIdsByBizType('cpuIdleTimePer', 279001);
					}else{
						alert('该指标无线图!');
					}
				})
			},
			createCharts : function(div,param){
				div.highcharts({
					chart: {
						type: 'solidgauge',
						margin : [0, 0, 0, 0],
						spacing : [0, 0, 0, 0],
						backgroundColor: 'rgba(0,0,0,0)'
					},
					title : {
						text : ''
					},
					pane: {
						center: ['50%', '50%'],
						size: '90%',
						startAngle: 0,
						endAngle: 360,
						background: {
		                	borderWidth : 0,
							backgroundColor: Highcharts.theme.plotOptions.gauge.dial.backgroundColor,
							innerRadius: '90%',
							outerRadius: '100%',
							shape: 'arc'
						}
					},
					tooltip: {
						enabled: false
					},
					credits: {
						enabled: false
					},
		    	    exporting : {
		    	    	enabled: false
		    	    },
					// the value axis
					yAxis: {
						min: 0,
						max: 100,
		                gridLineWidth : 0,
		                minorGridLineWidth : 0,
				        lineWidth: 0,
				        minorTickWidth : 0,
				        tickWidth: 0,
				        labels : {
				        	enabled : false
				        },
				        stops: param.stops,
				        opposite : true
					},
					plotOptions: {
						solidgauge: {
							dataLabels: {
								y: 10,
								borderWidth: 0,
								useHTML: true,
								shadow : true,
							}
						}
					},
					series: [{
						data: [parseInt(param.value)],
						dataLabels: {
							align : 'center',
							crop : false,
							overflow : 'none',
		    	        	y : -11,
							style : {
								fontSize : 12,
		    	        		fontWeight : 'normal'
							},
							format: param.label
						},
						innerRadius : '81.5%'
					}]
				});
			},
			renderLow:function(){
				var that = this;
				var lowDiv = $('<div/>').attr('style','padding:20px;');
				that.selector.append('<div class="line-style"></div>');
				that.selector.append(lowDiv);
				that.lowDiv = lowDiv;
				lowDiv.html('');
				lowDiv.load('module/business-service/biz-detail-info/metric_info_chart.html',function(){
					var chartParentDiv = lowDiv.find('#showMetricCharts');
					var chartObj1 = new chartObj(chartParentDiv,1);
					
					that.chartObj = chartObj1;
					var metricInfoArr = that.metricInfoArr;
					
					var flag = false;
					if(metricInfoArr.length>0){
						for(var i=0;i<metricInfoArr.length;i++){
							var dataItem = metricInfoArr[i];
							if('PerformanceMetric'==dataItem.metricType){
								flag = true;
								lowDiv.find('#unitSpan').html(dataItem.metricUnit);
								that.chartObj.setIdsByBizType(dataItem.metricId, dataItem.instanceId);	
								break;
							}
						}
						
						if(!flag){
							var dataItem = metricInfoArr[0];
							lowDiv.find('#unitSpan').html(dataItem.metricUnit);
							that.chartObj.setIdsByBizType(dataItem.metricId, dataItem.instanceId);	
						}
					}
				});
			}
	}
	
	oc.ns('oc.module.biz.bizmetricinfo');
	oc.module.biz.bizmetricinfo={
		open:function(cfg){
			var info = new bizMetricInfo(cfg);
			info.init();
		},
		dialog:function(cfg){
			var dlgItem = $('<div>');
			dlgItem.dialog({
			    title: '关联指标',
			    width : 1100,
				height : 630,
			    onLoad:function(){
			    }
			});
			
			oc.resource.loadScripts(['resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js',
			                         'resource/module/resource-management/resourceDetailInfo/js/generalNew.js',
			                         'resource/third/highcharts/js/highcharts-more.js',
			                         'resource/module/resource-management/resourceDetailInfo/js/resourceCommonChart.js',
			                         'resource/third/highcharts/js/modules/solid-gauge.js'],function(){
				cfg['selector']=dlgItem;
				var info = new bizMetricInfo(cfg);
				info.init();
			});
			
		}
	};
})()