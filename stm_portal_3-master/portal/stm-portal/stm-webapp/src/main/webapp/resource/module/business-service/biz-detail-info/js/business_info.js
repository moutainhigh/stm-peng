$(function(){
	function businessInfo(cfg){
		this.cfg = cfg;
	}
	
	businessInfo.prototype={
		cfg : undefined,
		selector : undefined,
		init : function(){
			var that = this;
			that.selector = that.cfg.selector;
			
			var data = {};
			
			that.createTable();
			that.selector.find('.alert_list_btn').on('click',function(){
				var addWindow = $('<div/>');	
				addWindow.dialog({
						  			  title:"告警信息",
						  			  href:oc.resource.getUrl('resource/module/business-service/alarm.html'),
						  			  width:800,
						  			  height:510,
						  			  modal:true,
						  			  onLoad:function(){
						  				oc.resource.loadScript('resource/module/business-service/js/alarm.js',function(){
						  					oc.module.biz.ser.alarm.open({id:that.cfg.bizId,name:that.cfg.bizName});
						  				});
						  			  }
						  			});

			});
		},
		createTable : function(data){
			var that = this;
			var parentObj = that.selector;
			var bizName = that.cfg.bizName;
			var tableDiv = parentObj.find('#business_info_table');
			
			//基本信息  默认7天
			var dateEnd = new Date();
	  		var dateStart = new Date(dateEnd.getTime()-7*24*60*60*1000);
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/getRunInfoForDetail.htm'),
				data:{bizId:that.cfg.bizId,startTime:dateStart.getTime(),endTime:dateEnd.getTime()},
				successMsg:null,
				success:function(data){
					var dataValue = data.data;
					that.defaultInfo = dataValue;
					var outageTimes = dataValue.outageTimes,
					bizStatus = dataValue.bizStatus,
					downTime = dataValue.downTime,
					MTTR = dataValue.mttr,
					MTBF = dataValue.mtbf,
					totalRunTime = 0;
					if(dataValue.totalRunTime){
						totalRunTime = dataValue.totalRunTime;
					}
					//0正常1警告,2严重,3致命
					var resourceClass ;
					switch(bizStatus){
					case 0:
					default :
						resourceClass = 'light-ico greenlight';//status_ico status_ico-green float_6// pos-top8
						break;
					case 1:
						resourceClass = 'light-ico yellowlight';// pos-top8
						break;
					case 2:
						resourceClass = 'light-ico orangelight';// pos-top8
						break;
					case 3:
						resourceClass = 'light-ico redlight';//pos-top8
						break;
					}
					
					var tr1 = $('<tr />');
					tr1.append('<td colspan="5" style="font-size:14px;"><span class="'+resourceClass+'"></span>业务名称：<span class="bule-color">'+bizName+'</span></td>');
					var tr2 = $('<tr />');
//					tr2.append('<td> 宕机次数：'+outageTimes+'次 </td>'+
//					          '<td>宕机时长：'+downTime+'	</td>'+
//					          '<td>MTTR：'+MTTR+'</td>'+
//					          '<td>MTBF:'+MTBF+'</td>');
//					var tr3 = $('<tr />');
//					tr3.append('<td colspan="4"> 当前可用性：'+(bizStatus==3?'不可用':'可用')+'</td>'
////					          +'<td>运行时长：'+totalRunTime+'</td>'
//					          );
					tr2.append('<td> 宕机次数：'+outageTimes+'次 </td>'+
					          '<td>宕机时长：'+downTime+'	</td>'+
					          '<td>MTTR：'+MTTR+'</td>'+
					          '<td>MTBF：'+MTBF+'</td>'+
							  '<td>当前可用性：'+(bizStatus==3?'不可用':'可用')+'</td>');
					
					tableDiv.append(tr1);
					tableDiv.append(tr2);
//					tableDiv.append(tr3);
					that.createContent();
				}
			});
		},
		createContent : function(data){
			var that = this;
			that.createHealth();
			that.createQuota();
			that.createCurve();
		},
		createHealth : function(data){
			var that = this;
			var parentObj = that.selector;
			var healthDiv = parentObj.find('.health');
			
			var addSpanFractionClass = '';
			switch(that.defaultInfo.bizStatus){
			case 0:
			default :
				break;
			case 1:
				addSpanFractionClass = 'yellow_fraction_bg';
				break;
			case 2:
				addSpanFractionClass = 'orange_fraction_bg';
				break;
			case 3:
				addSpanFractionClass = 'red_fraction_bg';
				break;
			}
			healthDiv.find('.spanFraction').html(that.defaultInfo.healthScore+'分').parent().addClass(addSpanFractionClass);
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/getHealthDetail.htm'),
				data:{bizId:that.cfg.bizId},
				successMsg:null,
				success:function(data){
					var nodeList = healthDiv.find('.health_dl_list');
					var nodeArr = data.data;
					for(var i=0;i<nodeArr.length;i++){
						var nodeObj = nodeArr[i];
						var resourceClass;
						
						var nodeStutasParent =1;
//						nodeObj.nodeStatus = -1;
						switch(nodeObj.nodeStatus){
						//0正常1警告 2严重 3致命-1未监控
						case -1:
							nodeStutasParent = -1;
							resourceClass = 'light-ico graylight';// pos-top8
							break;
						case 0:
						default :
							resourceClass = 'light-ico greenlight';//status_ico status_ico-green float_6// pos-top8
							break;
						case 1:
							resourceClass = 'light-ico yellowlight';// pos-top8
							break;
						case 2:
							resourceClass = 'light-ico orangelight';// pos-top8
							break;
						case 3:
							resourceClass = 'light-ico redlight';// pos-top8
							break;
						}
						
						var showName = nodeObj.showName;
						var dl = $('<dl/>');
						var dt = $('<dt/>').append('<div title="'+showName+'" style="width:170px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis; "><span class="'+resourceClass+'"></span>'+showName+'</div>');
						dl.append(dt);
						
						var nodeMetricArr = nodeObj.bind;
						
						if(nodeMetricArr){
							for(var x=0;x<nodeMetricArr.length;x++){
								var nodeMetricObj = nodeMetricArr[x];
								if(nodeStutasParent<0){
									//都为未监控状态
									nodeMetricObj.status=-1;
								}
								
								var childClass ;
								switch(nodeObj.type){
//								1主资源 2子资源 3指标
								case 1:
								case 2:
								default:
									switch(nodeMetricObj.status){
									//0正常1警告 2严重 3致命
									case -1:
										childClass = 'light-ico graylight';
										break;
									case 0:
									default :
										childClass = 'status_ico status_ico-green';
										break;
									case 1:
										childClass = 'light-ico yellowlight';
										break;
									case 2:
										childClass = 'light-ico orangelight';
										break;
									case 3:
										childClass = 'light-ico redlight';
										break;
									}
									break;
								case 3:
									switch(nodeMetricObj.status){
									//0正常1警告 2严重 3致命
									case -1:
										childClass = 'light-ico graylight';
										break;
									case 0:
									case 0:
									default :
										childClass = 'status_ico status_ico-green';
										break;
									case 1:
										childClass = 'light-ico yellowlight';
										break;
									case 2:
										childClass = 'light-ico orangelight';
										break;
									case 3:
										childClass = 'light-ico redlight';
										break;
									}
									break;
								}
								var dd = $('<dd />').html('<div class="health_L" title="'+nodeMetricObj.name+'">'+nodeMetricObj.name+'</div><div class="dotted_line"></div><div class="health_R"><span class="'+childClass+'"></span></div>');
								
								dl.append(dd);
							}
						}
						nodeList.append(dl);
					}
					
				}
			});
			
		},
		createQuota : function(data){
			var that = this;
			var parentObj = that.selector;
			var quotaDiv = parentObj.find('.quota');
			
			quotaDiv.find('.quota_number').html(that.defaultInfo.availableRate);
			quotaDiv.find('.inside_green').css('width',that.defaultInfo.availableRate);
			
			oc.util.ajax({
				url:oc.resource.getUrl('portal/business/service/getCapacityMetric.htm'),
				data:{bizId:that.cfg.bizId},
				successMsg:null,
				success:function(data){
					var dataMetric = data.data;
					//0-计算容量 0.75-0.9  绿黄橙
					//1-存储容量 0.8-0.9  绿黄橙
					//2-数据库容量 0.8-0.9  绿黄橙
					//3-带宽容量 0.7-0.8  绿黄橙
					
					for(var i=0;i<dataMetric.length;i++){
						var obj = dataMetric[i];
						var value,total,use,stops;
						switch(i){
						case 0:
							value = obj['cpuRate'];
							stops = [
				                [0, '#55BF3B'], // green
				                [0.75, '#DDDF0D'], // yellow
				                [0.9, '#DF5353'] // red
				            ];
							break;
						case 1:
							value = obj['useRate'];
							total = obj['totalStore'];
							use = obj['useStore'];
							stops = [
						                [0, '#55BF3B'], // green
						                [0.8, '#DDDF0D'], // yellow
						                [0.9, '#DF5353'] // red
						            ];
							break;
						case 2:
							value = obj['useRate'];
							total = obj['totalTableSpace'];
							use = obj['useTableSpace'];
							if(value<80){
								stops = 'green';
							}else if(value>90){
								stops = 'red';
							}else{
								stops = 'yellow';
							}
							break;
						case 3:
							value = obj['useRate'];
							total = obj['totalFlow'];
							use = obj['useFlow'];
							if(value<70){
								stops = '#55BF3B';
							}else if(value>80){
								stops = '#DF5353';
							}else{
								stops = '#DDDF0D';
							}
							break;
						}
						var x = i+1;
						var parentDiv = quotaDiv.find('#parent_capacity_box_'+x);
						var param = {type:x,value:value,total:total,use:use,stops:stops};
						if(x>1){
							parentDiv.find('div[name=valueDiv]').html('<span title="'+total+'">总容量：'+total+'</span><span  title="'+use+'">已使用：'+use+'</span>');
						}
						that.createPageComponent(param,quotaDiv.find('#capacity_box_'+x));
					}
					
					
				}
			})
			
		},
		createCurve : function(data){
			var that = this;
			var parentObj = that.selector;
			var curveDiv = parentObj.find('.curve');
			var tableDiv = parentObj.find('#business_metric_table');
			
			var bizCreateTime = that.defaultInfo.totalRunTime;//1474339941000 that.defaultInfo.totalRunTime
			curveDiv.find('li[name=titleTabs]').on('click',function(){
				var target = $(this);
				
				curveDiv.find('li').each(function(){
					$(this).removeClass();
				})
				target.addClass('curve-bar_active');
				
				switch(target.attr('id')){
				case 'healthLi':
					tableDiv.find('.thresholds').parent().show();
					oc.module.biz.bizchart.open({bizCreateTime:bizCreateTime,selector:curveDiv.find("#showMetricChartsDiv"),
						param:{url:oc.resource.getUrl('portal/business/service/getHealthHistoryData.htm'),bizId : that.cfg.bizId, metricId : '1'},
						callback:that.fillCurveTableData,ifAddPoint:true});
					break;
				case 'responseLi':
					tableDiv.find('.thresholds').parent().hide();
					oc.module.biz.bizchart.open({bizCreateTime:bizCreateTime,selector:curveDiv.find("#showMetricChartsDiv"),
						param:{url:oc.resource.getUrl('portal/business/service/getResponseTimeHistoryData.htm'),bizId : that.cfg.bizId, metricId : '2'},
						callback:that.fillCurveTableData,ifAddPoint:false});
					break;
				}
				
			})
			oc.module.biz.bizchart.open({bizCreateTime:bizCreateTime,selector:curveDiv.find("#showMetricChartsDiv"),
				param:{url:oc.resource.getUrl('portal/business/service/getHealthHistoryData.htm'),bizId : that.cfg.bizId, metricId : '1'},
				callback:that.fillCurveTableData,ifAddPoint:true});
		},
		fillCurveTableData:function(tableDiv,data){
			//{status:dataTemp.status,curValue:dataTemp.curValue,lastCollectTime:dataTemp.lastCollect,threshold:dataTemp.threshold}
			tableDiv.find('#curveStatus').html('');
			tableDiv.find('.thresholds').html('');
			tableDiv.find('#curveCurrentVal').html('');
			tableDiv.find('#curveCurrentCollectTime').html('');
			
			if(!data.unit){
				data.unit = '';
			}
			var title = '正常';
			var color = 'green';
			switch(data.status){
			case 1:
				title = '警告';
				color = 'yellow';
				break;
			case 2:
				title = '严重';
				color = 'orange';
				break;
			case 3:
				title = '致命';
				color = 'red';
				break;
			case 0:
			default :
				;
			}
			tableDiv.find('#curveStatus').html('<div class="status oc-info-value"><label class="light-ico '+color+'light" title="'+title+'" style="min-width: 25px !important;"></label></div>');
			if(!data.threshold || data.threshold==','){
//				tableDiv.find('.thresholds').append($("<div/>").width('140px').target(eval('["'+data.unit+'",'+data.threshold+']'), true));
			}else{
				tableDiv.find('.thresholds').append($("<div/>").width('140px').target(eval('["'+data.unit+'",'+data.threshold+']'), true));
			}
			
			tableDiv.find('#curveCurrentVal').html(data.curValue);
			tableDiv.find('#curveCurrentCollectTime').html(data.lastCollectTime);
		},
		createPageComponent : function(param,div){
			switch(param.type){
			case 1:
					$(div).highcharts({
						chart : {
							type : 'solidgauge',
							margin : [ 0, 0, 0, 0 ],
							spacing : [ 0, 0, -35, 0 ],
							backgroundColor : 'rgba(0,0,0,0)'
						},
			    	    title: null,
			    	    pane: {
			    	    	center: ['50%', '70%'],
			    	    	size: '90%',
			    	        startAngle: -90,
			    	        endAngle: 89.5, // 在ie8下90度 值为百分百 指针会越界
			                background: {
			                	borderWidth : 1,
			                    innerRadius: '80%',
			                    outerRadius: '101%',
			                    shape: 'arc'
			                }
			    	    },
			    	    credits: {
			    	    	enabled: false
			    	    },
			    	    exporting : {
			    	    	enabled: false
			    	    },
			    	    tooltip: {
			    	    	enabled: false
			    	    },
			    	    yAxis: {
			    	        min: 0,
			    	        max: 100,
			    			lineWidth: 0,
			                tickPixelInterval: 80,
			                gridLineWidth : 0,
			                minorGridLineWidth : 0,
			                minorTickWidth : 0,
			                tickWidth : 0,
			                labels: {
			                	enabled:false
			                },
					        stops: param.stops
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
//			    	        	y : -10,
			    	        	style : {
			    	        		fontSize:'8px',
			    	        		color :  '#555555'//#EC5707
			    	        	},
			    	        	format : '<b style="color:'+Highcharts.theme.plotOptions.gauge.series.dataLabels.color+'">'+'{y}' +'%</b>'
			    	        },
			    	        innerRadius : '81.5%'
			    	    },{
			    	    	type : 'gauge',
			    	    	dial: {
			    	    		backgroundColor : Highcharts.theme.plotOptions.gauge.dial.backgroundColor,
			    	    		borderColor : Highcharts.theme.plotOptions.gauge.dial.borderColor,
				    	    	baseLength: '0%',
				    	    	baseWidth: 3,
				    	    	topWidth: 1,
				    	    	borderWidth: 0,
				    	    	radius : '75%',
				    	    	rearLength : '0%'
			    	    	},
			    	    	pivot : {
			    	    		backgroundColor : Highcharts.theme.plotOptions.gauge.pivot.borderColor,
			    	    		radius : 0
							},
							dataLabels: {
								enabled:false
							},
			    	    	data : [parseInt(param.value)],
			    	    }]});
				
				break;
			case 2 :
				
				$(div).highcharts({
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
							backgroundColor: Highcharts.theme.plotOptions.gauge.panel.backgroundColor,
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
							format: '<b style="color:'+Highcharts.theme.plotOptions.gauge.series.dataLabels.color+'">'+'{y}' +'%</b>'
						},
						innerRadius : '81.5%'
					}]
				});
				
				break;
			case 3:
				//param
				div.find('#db_bg_color').height(param.value+'%').addClass('fill_'+param.stops);
				div.find('#db_bg_value').html(param.value+'%');
				break;
			case 4:
					$(div).highcharts({
				        chart: {
				            type: 'gauge',
				            plotBackgroundColor: null,
				            plotBackgroundImage: null,
				            plotBorderWidth: 0,
				            plotShadow: false
				        },
				        title: {
				            text: null
				        },
				        pane: {
				        	size: '120%',
				            startAngle: -150,
				            endAngle: 150
				        },
				        tooltip: {
				            enabled: false
				        },
				        yAxis: {
				            min: 0,
				            max: 100,
				            minorTickInterval: 'auto',
				            minorTickWidth: 1,
				            minorTickLength: 4,
				            minorTickPosition: 'inside',
				            minorTickColor:  Highcharts.theme.plotOptions.gauge.minorTickColor,
				            tickWidth: 1,
				            tickPixelInterval: 20,
				            tickPosition: 'inside',
				            tickLength: 6,
				            tickColor:  Highcharts.theme.plotOptions.gauge.tickColor,
				            labels: {
//				                step: 4,
//				                rotation: 'auto'
				            	enabled:false
				            },
				            plotBands: [{
				                from: 0,
				                to: param.value,
				                color: param.stops,
				                thickness: 5
				            }],
//					        stops: param.stops
				        },
				        plotOptions : {
				            gauge : {
					            dataLabels: {
					            	borderWidth: 0,
					                format : '<b style="color:'+Highcharts.theme.plotOptions.gauge.series.dataLabels.color+'">'+'{y}' +'%</b>',
					                y : 0
					                
					            },
				    	    	dial: {
				    	    		backgroundColor : Highcharts.theme.plotOptions.gauge.dial.backgroundColor,
					    	    	baseLength: '0%',
					    	    	baseWidth: 3,
					    	    	topWidth: 1,
					    	    	borderWidth: 0,
					    	    	radius : '80%',
					    	    	rearLength : '0%'
				    	    	},
				    	    	pivot : {
				    	    		backgroundColor : Highcharts.theme.plotOptions.gauge.pivot.borderColor,
				    	    		radius : 0
								}
				            }
				        },
				        series: [{
				        	name : '带宽容量',
				            data: [parseInt(param.value)]
				        }]
				    });
				
				break;
				
			
				
			}
		},
		formatDate:function(date){
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
		}
		
	}
	
	oc.ns('oc.module.biz.businessinfo');
	oc.module.biz.businessinfo={
		open:function(cfg){
			var info = new businessInfo(cfg);
			info.init();
		},
		dialog:function(cfg){
			var dlgItem = $('<div>').attr('style','position:relative;');
			dlgItem.dialog({
			    title: '关联子业务',
			    href: 'module/business-service/biz-detail-info/biz_info.html',
			    width : 1110,
				height : 630,
			    onLoad:function(){
			    	cfg['selector']=dlgItem;
					var info = new businessInfo(cfg);
					info.init();
			    },
			    onClose:function(){
			    	dlgItem.dialog('destroy');
			    }
			});
			
		}
	};
});