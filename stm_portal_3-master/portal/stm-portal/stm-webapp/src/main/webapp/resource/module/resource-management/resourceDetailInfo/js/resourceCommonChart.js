function chartObj(div,divWidthParameter,childrenResourceType){
	this.id = div.attr('id');
	this.divObj = div;
	var inerHeight = div.css('height');
	var inerWidth = div.css('width');
	div.css({
		'overflow-X' : 'hidden',
		'overflow-Y' : 'hidden'
	});
	var inerWidthNum = inerWidth.substring(0,inerWidth.length-2);
	
	var showMetricInerCharts = $('<div/>').attr('id','showMetricInerCharts'+this.id).attr('style','height:'+inerHeight+';width:'+inerWidth+';');
	div.append(showMetricInerCharts);
	this.inerId = 'showMetricInerCharts'+this.id;
	this.divInerObj = showMetricInerCharts;
	this.chart = this.createCleanCharts(this.inerId);
	if(childrenResourceType){
		this.resourceType = childrenResourceType;
	}else{
		this.resourceType = "default";
	}
	//metricGroup 内指标的颜色顺序,按线图render时设置的颜色顺序 
	this.metricGroupSource = [[{metricId : 'ifBandWidthUtil',name : '带宽利用率',color : '#2894FF'},
	                           {metricId : 'ifInBandWidthUtil',name : '接收带宽利用率',color : '#f96e03'},
	                           {metricId : 'ifOutBandWidthUtil',name : '发送带宽利用率',color : '#55BF3B'}],
	                    [{metricId : 'ifInOctetsSpeed',name : '接收速率',color : '#2894FF'},
	                     {metricId : 'ifOutOctetsSpeed',name : '发送速率',color : '#f96e03'}
//	                    ,{metricId : 'ifOctetsSpeed',name : '接口总流量',color : 'linegreen'}
	                    ]];

}

chartObj.prototype={
	id : undefined,
	inerId : undefined,
	divObj : undefined, 
	divInerObj : undefined, 
	meticId : undefined,
	instanceId : undefined,
	constructor:chartObj,
	chart : undefined,
	dialog : undefined,
	ajaxMethod:undefined,
	mainValuesObj:undefined,
	lastRandomTimeSet:null,
	metricGroupSource:null,
	metricGroup:null,
	mainValuesArr:[],
	openType:'normal',
	clear : function(){
		this.chart.series[0].setData([null]);
		this.chart.xAxis[0].setCategories([null]);
		this.chart.yAxis[0].setTitle({text: '单位 : '});
	},
	showMetricMainValues : function(){
		var parent = this.divObj.parent();
		parent.find(".metricIdClass").show();
	}
	,
	ifGroupDisplayHandle : function(metricId){
		this.metricGroup = null;
		var metricGroupSource = this.metricGroupSource;
		var parent = this.divObj.parent();
		var metricIdSelect = parent.find('#metricChartsLi').css('display','none').html('');
		
		var mainValueDivObj = parent.find('#metricChartsShowValues');
		var selectSpan = parent.find('span[name=metricIdClassSpan]').hide();
		
//		if(mainValueDivObj.length<1){
//			var mainValueDiv = '<div id="metricChartsShowValues" style="background: #d5edf9;padding: 5px 0px;margin-top: -5px;">'+//style="display:block;float:left" class="btnlistarry"
//			'<span class="metricIdClass" style="display:none;"><select id="metricIdSelect" ></select></span>'+
//			'<span class="metricIdClass" style="display:none;"><span name="showValuesColor" ></span></span>'+
//			'<span>Max : </span><span name="valueMax" ></span><span>&nbsp;&nbsp;</span>'+
//			'<span>Min : </span><span name="valueMin" ></span><span>&nbsp;&nbsp;</span>'+
//			'<span>Avg : </span><span name="valueAvg" ></span><select id="queryTimeType" class="btnlistarry locate-right"></select></div>';
//			
//			parent.find('#metricChartsUl').before(mainValueDiv);
//		}
		
		for(var i=0;i<metricGroupSource.length;i++){
			var objInner = metricGroupSource[i];
			for(var x=0;x<objInner.length;x++){
				if(metricId == objInner[x]["metricId"]){
					this.metricGroup = objInner;
					selectSpan.show();
					this.setGroupDisplay(objInner,metricId);
					return ;
				}
			}
		}
	},
	setGroupDisplay : function(metricGroup,metricId){
		var that = this;
		var parent = this.divObj.parent();
		
		var metricIdSelect = parent.find('#metricChartsLi').css('display','block').html('');
		var comboboxData = new Array();
		for(var i=0;i<metricGroup.length;i++){
			var metricObj = metricGroup[i];
			var checkStr = "";
			if(metricObj.metricId == metricId) checkStr = "checked = true";
			var span = $('<span/>').attr('class','metricIdClass')
			.html("<span class='"+metricObj.color+"'></span><input type='checkbox' "+checkStr+" metricId='"+metricObj.metricId+"' style='vertical-align:middle;'/><span>"+metricObj.name+"</span>");
			metricIdSelect.append(span);
			comboboxData.push({id:metricObj['metricId'],name:metricObj['name']});
		}
		
		that.showMetricMainValues();
		parent.find('span[name=showValuesColor]').attr('class',metricGroup[0]['color']);
		var box = {
				selector:parent.find('#metricIdSelect'),
				value : metricGroup[0]['metricId'],
				width : 110,
				placeholder : false,
				data:[{id:metricGroup[0]['metricId'],name:metricGroup[0]['name']}],
				onSelect : function(record){
					var valuesTemp = that.mainValuesArr ;
					for(var i=0;i<metricGroup.length;i++){
						var meg = metricGroup[i];
						if(meg['metricId']==record['id']){
							var color = meg['color'];
							parent.find('span[name=showValuesColor]').attr('class',color);
							break;
						}
					}
					if(valuesTemp && valuesTemp.length>0){
						var flag = true;
						for(var x=0;x<valuesTemp.length;x++){
							var valuesTempIn = valuesTemp[x];
							if(valuesTempIn['metricId'] && valuesTempIn['metricId']==record['id']){
								that.setMainValues(valuesTempIn);
								flag = false;
								break;
							}
						}
						if(flag){
							that.setMainValues({'max':"",'min':"",'avg':""});
						}
					}
				}
		}
		var mainValues = oc.ui.combobox(box);
		that.mainValuesObj = mainValues;
		
		var chartId = this.id;
		metricIdSelect.find('input[type=checkbox]').each(function(i){
			var thisObj = $(this);
			thisObj.on('click', function(){
				var checkFlag=thisObj.prop("checked");
				if(checkFlag!=true){
					thisObj.removeAttr("checked");
				}else{
					thisObj.attr("checked","true");
				}
				
				//启动查询
				var queryTimeType;
				var liObj;
//				parent.find('li[name=chartsType'+chartId+']').each(function(i){
//					var thisObj = $(this);
//					if('active' == thisObj.attr('class')){
//						liObj = thisObj;
//						queryTimeType = $(this).html();
//					}
//			    });
				var queryTimeType = parent.find('#queryTimeType').combobox('getValue');
				var timeSet = that.lastRandomTimeSet;
				if('自定义'==queryTimeType){
					if(!timeSet) return ;
					
					that.getRandomTimeData(queryTimeType,timeSet.timeSub,timeSet.dateStartStr,timeSet.dateEndStr);
				}else{
					that.queryAdapt(liObj,queryTimeType);
				}
				
		    });
		});
	},
	init : function(){
		
		
		
//		var chartsMainValueHtml = 
//						'<span class="metricIdClass" style="display:none;"><select name="metricIdSelect" ></select>&nbsp;&nbsp;</span>'+
//						'<span class="metricIdClass" style="display:none;"><span class="lineblue"></span>&nbsp;&nbsp;&nbsp;&nbsp;</span>'+
//						'<span name="valueMax" >Max : 123</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>'+
//						'<span name="valueMin" >Min : 123</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>'+
//						'<span name="valueAvg" >Avg : 123</span>';
//			
//		this.divObj.parent().find("#metricChartsShowValues").html(chartsMainValueHtml);
	},
	setIds : function(metricId,instanceId){
		var that = this;
		
		this.meticId = metricId;
		this.instanceId = instanceId;
		var chartResourceType = this.resourceType;
		chart = this.chart;//this.chart  this.id.highcharts()
		var newChartId = this.inerId;//this.id;
		//对metricId进行检查,确认是否为按组显示的元素
		that.ifGroupDisplayHandle(metricId);
		
		//this.divObj.parent().find('li[name=chartsType'+this.id+']').each(function(i){
		var chartId = this.id;
		var parent = this.divObj.parent();
		
		//默认查询1H
		that.getTimeData("1H","min",60*1000*60);
		
//		parent.find('li[name=chartsType'+chartId+']').each(function(i){
//			var _liCls = $(this);
//			if('1H'==_liCls.html()){
//				_liCls.addClass('active');
//			}else{
//				_liCls.removeClass('active');
//			}
//		});
		var customTime = parent.find('#customTime').linkbutton('RenderLB',{
			  text:'自定义',
			  //iconCls:"icon-ok",
			  onClick:function(){
				  var dialogSetIds = $('<div/>');
					var newChartId = this.inerId;
					
					dialogSetIds.dialog({
					    title: '自定义时间',
					    width : 300,
						height : 150,
						buttons:[{
					    	text: '</span>确定',
					    	iconCls:"fa fa-check-circle",
					    	handler:function(){
					    		var dateStartStr = dialogSetIds.find('#dateStartSelect'+newChartId).datetimebox('getValue');	
					    		var dateEndStr = dialogSetIds.find('#dateEndSelect'+newChartId).datetimebox('getValue');
					    		
					    		if(dateStartStr=='' || dateEndStr==''){
					    			alert('日期输入不能为空！');
					    			return false;
					    		}
					    		
					    		var dateStart = new Date(dateStartStr.replace(/-/g,"/"));
					    		var dateEnd = new Date(dateEndStr.replace(/-/g,"/"));
					    		
					    		var timeSub = dateEnd.getTime() - dateStart.getTime();
					    		if(timeSub<0 || timeSub==0){
					    			alert('开始日期应在结束日期之前！');
					    			return false;
					    		}else if(timeSub > 365*24*60*60*1000){
					    			alert('开始日期,结束日期时间间隔不能大于一年！');
					    			return false;
					    		}else{
					    			destroy = dialogSetIds.dialog('destroy');
					    			var lastRandomTime = {timeSub:timeSub,dateStartStr:dateStartStr,dateEndStr:dateEndStr};
					    			that.lastRandomTimeSet = lastRandomTime;
					    			that.getRandomTimeData(queryTimeType,timeSub,dateStartStr,dateEndStr);
					    		}
					    	}
					    },{
					    	text: '取消',
					    	iconCls:"fa fa-times-circle",
					    	handler:function(){
					    		dialogSetIds.dialog('destroy');
					    	}
					    }],
					    onLoad:function(){
					    	//_init(dialogSetIds);
					    }
					});
					//弹出日期选择框
//						var dlg = $('<div/>');
					var form = $('<form/>').addClass('oc-form '+newChartId).attr('id','dateForm'+newChartId);
					var dateStartDiv = $('<div/>').addClass('margin10');
					var dateStartLabel = $('<label/>').html('开始时间 : ');
					var dateStartSelect = $('<select/>').attr('name','dateSelect'+newChartId).attr('id','dateStartSelect'+newChartId);
					dateStartDiv.append(dateStartLabel);
					dateStartDiv.append(dateStartSelect);
					form.append(dateStartDiv);
					
					var dateEndDiv = $('<div/>').addClass('margin10 margintop-10');
					var dateEndLabel = $('<label/>').html('结束时间 : ');
					var dateEndSelect = $('<select/>').attr('name','dateSelect'+newChartId).attr('id','dateEndSelect'+newChartId);
					dateEndDiv.append(dateEndLabel);
					dateEndDiv.append(dateEndSelect);
					form.append(dateEndDiv);
					dialogSetIds.append(form);
					
//						this.dialog = dlg;
					var myForm = dialogSetIds.find('#dateForm'+newChartId);
//					var myDateSelect = dialogSetIds.find('[name=dateSelect'+newChartId+']');
					
					oc.ui.form({
						selector:myForm,
						datetimebox:[{
							selector:'[name=dateSelect'+newChartId+']'
						}]
					});
			  }
		});
		
		var timeBox = {
				selector:parent.find('#queryTimeType'),
				value : '1H',
				width : 50,
				placeholder : false,
				data:[{id:'1H',name:'1H'},
				      {id:'1D',name:'1D'},
				      {id:'7D',name:'7D'},
				      {id:'30D',name:'30D'},
				      ],
				onSelect : function(record){
					that.queryAdapt(null,record.id);
				}
		};
		that.timeBox = oc.ui.combobox(timeBox);
		
		$('.combobox-item').on('click',function(){
			var target = $(this);
			var typeStr = target.html();
			var timeBoxValue = parent.find('#queryTimeType').combobox('getValue');
			if(typeStr==timeBoxValue){
				that.queryAdapt(null,timeBoxValue);
			}
		})
		
//		parent.find('li[name=chartsType'+chartId+']').each(function(i){
//			var thisObj = $(this);
//			var queryTimeType = $(this).html();
//		
//			thisObj.unbind("click");
//			thisObj.on('click', function(){
//				  //设置点击变色
//				  parent.find('li[name=chartsType'+chartId+']').each(function(){
//					  $(this).removeClass('active');
//				  });
//				  thisObj.addClass('active');
//				  that.queryAdapt(thisObj,queryTimeType);
//		        });
//	    });
	},
	setIdsByBizType : function(metricId,instanceId){
		var that = this;
		that.meticId = metricId;
		that.instanceId = instanceId;
		that.openType = 'biz';
		var newChartId = that.inerId;
		
		var chartId = that.id;
		var parent = that.divObj.parent();
		
		//默认查询1H
		that.getTimeData("1H","min",60*1000*60);
		parent.find('li[name=chartsType]').each(function(i){
			var _liCls = $(this);
			if('1H'==_liCls.html()){
				_liCls.addClass('active');
			}else{
				_liCls.removeClass('active');
			}
		});
		
		parent.find('li[name=chartsType]').each(function(i){
			var thisObj = $(this);
			var queryTimeType = $(this).html();
		
			thisObj.unbind("click");
			thisObj.on('click', function(){
				  //设置点击变色
				  parent.find('li[name=chartsType]').each(function(){
					  $(this).removeClass('active');
				  });
				  thisObj.addClass('active');
				  that.queryAdapt(thisObj,queryTimeType);
		    });
	    });
	},
	queryAdapt : function(thisObj,queryTimeType){
		/*
		 * 5天以内分钟数据
		 * 大于1天  半小时数据
		 * 大于15天 小时数据
		 * 大于60天 6小时数据
		 * */
		var that = this;
		switch (queryTimeType) {
		case '1H':
			that.getTimeData(queryTimeType,"min",60*1000*60);
			break;
		case '1D':
			that.getTimeData(queryTimeType,"min",24*60*1000*60);
			break;
		case '7D':
			that.getTimeData(queryTimeType,"halfHour",7*24*60*1000*60);
			break;
		case '30D':
			that.getTimeData(queryTimeType,"hour",30*24*60*1000*60);
	        break;
		case '自定义': 
			var dialogSetIds = $('<div/>');
			var newChartId = this.inerId;
			
			dialogSetIds.dialog({
			    title: '自定义时间',
			    width : 300,
				height : 150,
				buttons:[{
			    	text: '</span>确定',
			    	iconCls:"fa fa-check-circle",
			    	handler:function(){
			    		var dateStartStr = dialogSetIds.find('#dateStartSelect'+newChartId).datetimebox('getValue');	
			    		var dateEndStr = dialogSetIds.find('#dateEndSelect'+newChartId).datetimebox('getValue');
			    		
			    		if(dateStartStr=='' || dateEndStr==''){
			    			alert('日期输入不能为空！');
			    			return false;
			    		}
			    		
			    		var dateStart = new Date(dateStartStr.replace(/-/g,"/"));
			    		var dateEnd = new Date(dateEndStr.replace(/-/g,"/"));
			    		
			    		var timeSub = dateEnd.getTime() - dateStart.getTime();
			    		if(timeSub<0 || timeSub==0){
			    			alert('开始日期应在结束日期之前！');
			    			return false;
			    		}else if(timeSub > 365*24*60*60*1000){
			    			alert('开始日期,结束日期时间间隔不能大于一年！');
			    			return false;
			    		}else{
			    			destroy = dialogSetIds.dialog('destroy');
			    			var lastRandomTime = {timeSub:timeSub,dateStartStr:dateStartStr,dateEndStr:dateEndStr};
			    			that.lastRandomTimeSet = lastRandomTime;
			    			that.getRandomTimeData(queryTimeType,timeSub,dateStartStr,dateEndStr);
			    		}
			    	}
			    },{
			    	text: '取消',
			    	iconCls:"fa fa-times-circle",
			    	handler:function(){
			    		dialogSetIds.dialog('destroy');
			    	}
			    }],
			    onLoad:function(){
			    	//_init(dialogSetIds);
			    }
			});
			//弹出日期选择框
//				var dlg = $('<div/>');
			var form = $('<form/>').addClass('oc-form '+newChartId).attr('id','dateForm'+newChartId);
			var dateStartDiv = $('<div/>').addClass('margin10');
			var dateStartLabel = $('<label/>').html('开始时间 : ');
			var dateStartSelect = $('<select/>').attr('name','dateSelect'+newChartId).attr('id','dateStartSelect'+newChartId);
			dateStartDiv.append(dateStartLabel);
			dateStartDiv.append(dateStartSelect);
			form.append(dateStartDiv);
			
			var dateEndDiv = $('<div/>').addClass('margin10');
			var dateEndLabel = $('<label/>').html('结束时间 : ');
			var dateEndSelect = $('<select/>').attr('name','dateSelect'+newChartId).attr('id','dateEndSelect'+newChartId);
			dateEndDiv.append(dateEndLabel);
			dateEndDiv.append(dateEndSelect);
			form.append(dateEndDiv);
			dialogSetIds.append(form);
			
//				this.dialog = dlg;
			var myForm = dialogSetIds.find('#dateForm'+newChartId);
//			var myDateSelect = dialogSetIds.find('[name=dateSelect'+newChartId+']');
			
			oc.ui.form({
				selector:myForm,
				datetimebox:[{
					selector:'[name=dateSelect'+newChartId+']'
				}]
			});
	        break;
		}
	},
	getQueryMetrics : function(){
		
  		var metricGroup = this.metricGroup;
  		if(metricGroup){
  			var parent = this.divObj.parent();
  			var metricIdSelect = parent.find('#metricChartsLi');
  			
  			var metricIds = new Array();
  			metricIdSelect.find('input[type=checkbox]').each(function(i){
  				var thisObj = $(this);
  				var checkFlag = thisObj.attr('checked');
  				if(checkFlag){
  					metricIds.push(thisObj.attr('metricId'));
  				}
  			});
  			return metricIds.join(',');
//  			var metricIds = new Array();
//  			for(var i=0;i<metricGroup.length;i++){
//  				metricIds.push(metricGroup[i]['metricId']);
//  			}
//  			return metricIds.join(',');
  		}else{
  			return this.meticId;
  		}
	},
	getTimeData : function(queryTimeType,metricDataType,timeSub){
		var that = this;
  		var dateEnd = new Date();
  		var dateStart = new Date(dateEnd.getTime()-timeSub);
  		var dateEndStr = formatDate(dateEnd);
  		var dateStartStr = formatDate(dateStart);
  		
  		var metricId = this.getQueryMetrics();
  		var instanceId = this.instanceId;
  		var newChartId = this.inerId;
  		
  		that.ajaxSpecialMetricChartData(dateStartStr,dateEndStr,metricId,instanceId,newChartId,queryTimeType,metricDataType);
  		
	},
	getRandomTimeData : function(queryTimeType,timeSub,dateStartStr,dateEndStr){
		
		/*
		 * 5天以内分钟数据
		 * 大于1天  半小时数据
		 * 大于15天 小时数据
		 * 大于60天 6小时数据
		 * */
		var metricDataTypeTemp = "min";
  		var dayNum = 24*60*60*1000;
  		var halfHourPosition = 1*dayNum;
  		var hourPosition = 15*dayNum;
  		var sixHourPosition = 60*dayNum;
  		
  		if(timeSub>halfHourPosition && timeSub<hourPosition){
  			metricDataTypeTemp = "halfHour";
  		}else if(timeSub>(hourPosition-1) && timeSub<sixHourPosition){
  			metricDataTypeTemp = "hour";
  		}else if(timeSub>(sixHourPosition-1)){
  			metricDataTypeTemp = "sixHour";
  		}
  		
  		var metricId = this.getQueryMetrics();
  		var instanceId = this.instanceId;
  		var newChartId = this.inerId;
  		
  		if(null==metricId||""==metricId) metricId = 'null_metric_query';
  		
  		this.ajaxSpecialMetricChartData(dateStartStr,dateEndStr,metricId,instanceId,newChartId,queryTimeType,metricDataTypeTemp);
	},
	setMeticId : function(metricId){
		this.meticId = meticId;
	},
	setInstanceId : function(instanceId){
		this.instanceId = instanceId;
	},
	ajaxSpecialMetricChartData : function(dateStartStr,dateEndStr,meId,insId,id,queryTimeType,metricDataType){
		var that = this;
		oc.util.ajax({
			//后台只用关心查询数据开始结束时间,以及该查什么时间类型的数据
			url:oc.resource.getUrl('portal/resource/metricChartsData/getSpecialMetricChartsData.htm'),
			data:{dateStart : dateStartStr ,dateEnd : dateEndStr,instanceId : insId, metricId : meId ,metricDataType : metricDataType},
			successMsg:null,
			success:function(data){
				var dataObj = that.handleSourceData(data,metricDataType);
				if(dataObj.seriesData[2]){
					if(dataObj.seriesDataValue0.length==2){
						dataObj.seriesDataValue0 = new Array();
					}else if(dataObj.seriesDataValue1.length==2){
						dataObj.seriesDataValue1 = new Array();
					}else if(dataObj.seriesDataValue1.length==2){
						dataObj.seriesDataValue1 = new Array();
					}
					that.renderCharts(dataObj,queryTimeType);
				}else if(dataObj.seriesData.length<2){
					if(dataObj.seriesData[0].data[1][1]==null){
						dataObj.seriesData[0].data = new Array();
					}
					that.renderChart(dataObj,queryTimeType);
				}else{
					if(dataObj.seriesDataValue0.length==2){
						dataObj.seriesDataValue0 = new Array();
					}else if(dataObj.seriesDataValue1.length==2){
						dataObj.seriesDataValue1 = new Array();
					}
					that.renderChartsTwo(dataObj,queryTimeType);
				}
			}
		});
	},
	createCleanCharts : function(id){
		var chart = new Highcharts.Chart({//divId.highcharts({//Highcharts.Chart({
			chart: {
		        type: 'area',
	            backgroundColor : 'rgba(0,0,0,0)',
	            renderTo: id 
	        },
	        title: null
	        ,
	        xAxis: {
	        	categories: []
	        },
	        yAxis: {
	        	min : 0,
	            title: {
	            	text: ''
	            }
	        },
			credits: {
		    	enabled: false
		    },
		    exporting : {
		    	enabled: false
		    },
			legend: {
		    	enabled:false
		    },

	        tooltip: {
	            enabled: true,
	            formatter: function() {
	            	return this.x +': '+ this.y ;
	            }
	        },
	        plotOptions: {
	            line: {
		    		lineWidth : 1,
	                dataLabels: {
	                    enabled: true
	                },
	                enableMouseTracking: false
	            }
	        },
	        series: [{
	        	data: []
	        }]
		});
		return chart;
	},
	renderChart : function(dataObj,queryTimeType){
		var newChartId = this.inerId;
		var legend = {enabled:false};
		var markerShowFlag = false;
		if("1H" == queryTimeType){
			markerShowFlag = true;
		}
		
		var showMetricChartsDiv = this.divObj.parent().find('.showMetricCharts');
		
		var myChart = echarts.init(showMetricChartsDiv[0]);
	
		var option = {
	    backgroundColor: 'rgba(0,0,0,0)',
	    title: {
	        text: ''
	    },
	    tooltip: {
	        trigger: 'axis',
	        axisPointer: {
	            lineStyle: {
	                color: 'rgba(0,0,0,0)'
	            }
	        },
		 formatter:function(params){
			var dateTool = new Date(params[0].data[0]);

			return ((dateTool.getMonth()<10?'0'+(dateTool.getMonth()+1):(dateTool.getMonth()+1))+'-'+(dateTool.getDate()<10?'0'+dateTool.getDate():dateTool.getDate())+' '+dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+'  '+unitTransform(params[0].data[1], dataObj.unit);
			
		}
	    },
	    grid: {
	        left: '3%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true
	    },
	    xAxis: [{
	        type: 'time',
	        boundaryGap: false,
	        axisLine: {
	            lineStyle: {
	                color: Highcharts.theme.areasplinelineColor
	            }
	        },
			axisLabel: {
				//margin: 35,
				interval:3,
				textStyle: {
					 color: Highcharts.theme.areasplinelabelsColor,
					fontSize: 12
				}
			},
			splitLine: {
				lineStyle: {
					color: 'rgba(0,0,0,0)'
				}
	        }
	    }],
	    yAxis: [{
			type: 'value',
			min:0,
			minInterval : 1,
			name: dataObj.metricUnitName,
				nameTextStyle:{
  color:Highcharts.theme.areasplinelabelsColor
},
			splitNumber:2,
/*		        min:0,
			max:100,*/
			axisTick: {
				show: true
			},
			axisLine: {
				
				// margin: 30,
				lineStyle: {
					color: Highcharts.theme.areasplinelineColor
				}
			},
			axisLabel: {
				//margin: 35,
				interval:3,
				textStyle: {
					 color: Highcharts.theme.areasplinelabelsColor,
					fontSize: 12
				},
				formatter:function(value){
					//return unitTransform(value);	
					if (value >= 100000) {
						value = Math.round(value / 10000 * 100) / 10000 +' M';
					} else if(value >= 1000) {
						value = Math.round(value / 1000 * 100) / 100 + ' K';
					} else {
						value = value;
					}
					return value;	 
				}
			},
			splitLine: {
				lineStyle: {
					color: 'rgba(0,0,0,0)'
				}
			}
		}],
	    series: //dataObj.seriesData
			[{
	        type: 'line',
	        smooth: true,
	        symbol: 'circle',
	        symbolSize: 5,
	        showSymbol: false,
			animation:false,
	        lineStyle: {
	            normal: {
	                width: 1
	            }
	        },
	        areaStyle: {
	        	 normal: {
	                 color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
	                     offset: 0,
	                     color: Highcharts.theme.areastartColor
	                 }, {
	                     offset: 0.8,
	                     color: Highcharts.theme.areaendColor // 100% 处的颜色
	                 }], false),
					shadowColor: Highcharts.theme.areashadowColor,
					shadowBlur: 10
	             }
	        },
	        itemStyle: {
	            normal: {
	            	  color: Highcharts.theme.areaitemColor,
		                borderColor: Highcharts.theme.areaitemborderColor,
	                borderWidth: 12
	
	            }
	        },
	        data: dataObj.seriesData[0].data
	    }]
	};
		myChart.setOption(option);
	},
	renderCharts : function(dataObj,queryTimeType){
		var newChartId = this.inerId;
		var legend = {enabled:false};
		var markerShowFlag = false;
		if("1H" == queryTimeType){
			markerShowFlag = true;
		}
		
		var showMetricChartsDiv = this.divObj.parent().find('.showMetricCharts');
		
		var myChart = echarts.init(showMetricChartsDiv[0]);
	
		var option = {
				
			backgroundColor: 'rgba(0,0,0,0)',
			title: {
	        text: ''
			},
			
			tooltip: {
	        trigger: 'axis',
			confine: true,
	        axisPointer: {
	            lineStyle: {
	                color:'rgba(0,0,0,0)'
	            }
	        	},
			formatter:function(params){
				//var dateTool = new Date(params[0].data[0]);
				//var dateTool1 = new Date(params[1].data[0]);
				if(params[1].data == undefined && params[2].data == undefined){
					var dateTool = new Date(params[0].data[0]);
					return params[0].seriesName+"<br/>"+(dateTool.getMonth()<10?'0'+(dateTool.getMonth()+1):(dateTool.getMonth()+1))+'-'+(dateTool.getDate()<10?'0'+dateTool.getDate():dateTool.getDate())+' '+(dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+'  '+unitTransform(params[0].data[1], dataObj.unit)
				}
				if(params[0].data == undefined && params[2].data == undefined){
					var dateTool1 = new Date(params[1].data[0]);
					return params[1].seriesName+"<br/>"+(dateTool1.getMonth()<10?'0'+(dateTool1.getMonth()+1):(dateTool1.getMonth()+1))+'-'+(dateTool1.getDate()<10?'0'+dateTool1.getDate():dateTool1.getDate())+' '+(dateTool1.getHours())+':'+(dateTool1.getMinutes()<10?'0'+dateTool1.getMinutes():dateTool1.getMinutes())+'  '+unitTransform(params[1].data[1], dataObj.unit)
				}
				if(params[0].data == undefined && params[1].data == undefined){
					var dateTool2 = new Date(params[2].data[0]);
					return params[2].seriesName+"<br/>"+(dateTool2.getMonth()<10?'0'+(dateTool2.getMonth()+1):(dateTool2.getMonth()+1))+'-'+(dateTool2.getDate()<10?'0'+dateTool2.getDate():dateTool2.getDate())+' '+(dateTool2.getHours())+':'+(dateTool2.getMinutes()<10?'0'+dateTool2.getMinutes():dateTool2.getMinutes())+'  '+unitTransform(params[2].data[1], dataObj.unit)
				}
				if(params[1].data == undefined){
					var dateTool = new Date(params[0].data[0]);
					var dateTool2 = new Date(params[2].data[0]);
					return params[0].seriesName+"<br/>"+(dateTool.getMonth()<10?'0'+(dateTool.getMonth()+1):(dateTool.getMonth()+1))+'-'+(dateTool.getDate()<10?'0'+dateTool.getDate():dateTool.getDate())+' '+(dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+'  '+unitTransform(params[0].data[1], dataObj.unit)+'<br/>'
					+params[2].seriesName+"<br/>"+(dateTool2.getMonth()<10?'0'+(dateTool2.getMonth()+1):(dateTool2.getMonth()+1))+'-'+(dateTool2.getDate()<10?'0'+dateTool2.getDate():dateTool2.getDate())+' '+(dateTool2.getHours())+':'+(dateTool2.getMinutes()<10?'0'+dateTool2.getMinutes():dateTool2.getMinutes())+'  '+unitTransform(params[2].data[1], dataObj.unit)
				}
				if(params[0].data == undefined){
					var dateTool1 = new Date(params[1].data[0]);
					var dateTool2 = new Date(params[2].data[0]);
					return params[1].seriesName+"<br/>"+(dateTool1.getMonth()<10?'0'+(dateTool1.getMonth()+1):(dateTool1.getMonth()+1))+'-'+(dateTool1.getDate()<10?'0'+dateTool1.getDate():dateTool1.getDate())+' '+(dateTool1.getHours())+':'+(dateTool1.getMinutes()<10?'0'+dateTool1.getMinutes():dateTool1.getMinutes())+'  '+unitTransform(params[1].data[1], dataObj.unit)+'<br/>'
					+params[2].seriesName+"<br/>"+(dateTool2.getMonth()<10?'0'+(dateTool2.getMonth()+1):(dateTool2.getMonth()+1))+'-'+(dateTool2.getDate()<10?'0'+dateTool2.getDate():dateTool2.getDate())+' '+(dateTool2.getHours())+':'+(dateTool2.getMinutes()<10?'0'+dateTool2.getMinutes():dateTool2.getMinutes())+'  '+unitTransform(params[2].data[1], dataObj.unit)
				}
				if(params[2].data == undefined){
					var dateTool = new Date(params[0].data[0]);
					var dateTool1 = new Date(params[1].data[0]);
					return params[0].seriesName+"<br/>"+(dateTool.getMonth()<10?'0'+(dateTool.getMonth()+1):(dateTool.getMonth()+1))+'-'+(dateTool.getDate()<10?'0'+dateTool.getDate():dateTool.getDate())+' '+(dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+' : '+unitTransform(params[0].data[1], dataObj.unit)+'<br/>'
					+params[1].seriesName+"<br/>"+(dateTool1.getMonth()<10?'0'+(dateTool1.getMonth()+1):(dateTool1.getMonth()+1))+'-'+(dateTool1.getDate()<10?'0'+dateTool1.getDate():dateTool1.getDate())+' '+(dateTool1.getHours())+':'+(dateTool1.getMinutes()<10?'0'+dateTool1.getMinutes():dateTool1.getMinutes())+' : '+unitTransform(params[1].data[1], dataObj.unit)
				}
				if(params[0].data[0] && params[1].data[0]){
					var dateTool = new Date(params[0].data[0]);
					var dateTool1 = new Date(params[1].data[0]);
					var dateTool2 = new Date(params[2].data[0]);
					return params[0].seriesName+"<br/>"+(dateTool.getMonth()<10?'0'+(dateTool.getMonth()+1):(dateTool.getMonth()+1))+'-'+(dateTool.getDate()<10?'0'+dateTool.getDate():dateTool.getDate())+' '+(dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+'  '+unitTransform(params[0].data[1], dataObj.unit)+'<br/>'
					+params[1].seriesName+"<br/>"+(dateTool1.getMonth()<10?'0'+(dateTool1.getMonth()+1):(dateTool1.getMonth()+1))+'-'+(dateTool1.getDate()<10?'0'+dateTool1.getDate():dateTool1.getDate())+' '+(dateTool1.getHours())+':'+(dateTool1.getMinutes()<10?'0'+dateTool1.getMinutes():dateTool1.getMinutes())+'  '+unitTransform(params[1].data[1], dataObj.unit)+'<br/>'
					+params[2].seriesName+"<br/>"+(dateTool2.getMonth()<10?'0'+(dateTool2.getMonth()+1):(dateTool2.getMonth()+1))+'-'+(dateTool2.getDate()<10?'0'+dateTool2.getDate():dateTool2.getDate())+' '+(dateTool2.getHours())+':'+(dateTool2.getMinutes()<10?'0'+dateTool2.getMinutes():dateTool2.getMinutes())+'  '+unitTransform(params[2].data[1], dataObj.unit);
				}

			}
			},
			
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    xAxis: [{
	        type: 'time',
	        boundaryGap: false,
	        axisLine: {
	            lineStyle: {
	                color: Highcharts.theme.areasplinelineColor
	            }
	        },
			axisLabel: {
				//margin: 35,
				interval:3,
				textStyle: {
					 color: Highcharts.theme.areasplinelabelsColor,
					fontSize: 12
				}
			},
			splitLine: {
				lineStyle: {
					color: 'rgba(0,0,0,0)'
				}
	        }
	    }],
	    yAxis: [{
			type: 'value',
			name: dataObj.metricUnitName,
				nameTextStyle:{
  color:Highcharts.theme.areasplinelabelsColor 
},
			splitNumber:2,
/*		        min:0,
			max:100,*/
			axisTick: {
				show: true
			},
			axisLine: {
				
				// margin: 30,
				lineStyle: {
					color:Highcharts.theme.areasplinelineColor
				}
			},
			axisLabel: {
				//margin: 35,
				interval:3,
				textStyle: {
					 color: Highcharts.theme.areasplinelabelsColor ,
					fontSize: 12
				},
				formatter: function(value){
					//return unitTransform(value);	
					if (value >= 100000) {
						value = Math.round(value / 10000 * 100) / 10000 +' M';
					} else if(value >= 1000) {
						value = Math.round(value / 1000 * 100) / 100 + ' K';
					} else {
						value = value;
					}
					return value;			
				}
			},
			splitLine: {
				lineStyle: {
					color: 'rgba(0,0,0,0)'
				}
			}
		}],
		    series: //dataObj.seriesData
				[{
		        name: dataObj.seriesData[1].name,
		        type: 'line',
		        smooth: true,
		        symbol: 'circle',
		        symbolSize: 5,
		        showSymbol: false,
				animation:false,
		        lineStyle: {
		            normal: {
		                width: 1
		            }
		        },
		        areaStyle: {
		        	 normal: {
		                 color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
		                     offset: 0,
		                     color: dataObj.seriesData[1].color
		                 }, {
		                     offset: 0.8,
		                     color: Highcharts.theme.areaendColor // 100% 处的颜色
		                 }], false),
						shadowColor: Highcharts.theme.areashadowColor,
						shadowBlur: 10
		             }
		        },
		        itemStyle: {
		            normal: {
		                color: dataObj.seriesData[1].color,
		                borderColor: dataObj.seriesData[1].color,
		                borderWidth: 12
		
		            }
		        },
		        data: dataObj.seriesDataValue1
		    },{
		        name: dataObj.seriesData[0].name,
		        type: 'line',
		        smooth: true,
		        symbol: 'circle',
		        symbolSize: 5,
		        showSymbol: false,
				animation:false,
		        lineStyle: {
		            normal: {
		                width: 1
		            }
		        },
		        areaStyle: {
		        	 normal: {
		                 color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
		                     offset: 0,
		                     color: dataObj.seriesData[0].color
		                 }, {
		                     offset: 0.8,
		                     color: Highcharts.theme.areaendColor // 100% 处的颜色
		                 }], false),
						shadowColor: Highcharts.theme.areashadowColor,
						shadowBlur: 10
		             }
		        },
		        itemStyle: {
		            normal: {
		                color: dataObj.seriesData[0].color,
		                borderColor: dataObj.seriesData[0].color,
		                borderWidth: 12
		
		            }
		        },
		        data: dataObj.seriesDataValue0
		    },{
		        name: dataObj.seriesData[2].name,
		        type: 'line',
		        smooth: true,
		        symbol: 'circle',
		        symbolSize: 5,
		        showSymbol: false,
				animation:false,
		        lineStyle: {
		            normal: {
		                width: 1
		            }
		        },
		        areaStyle: {
		        	 normal: {
		                 color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
		                     offset: 0,
		                     color: dataObj.seriesData[2].color
		                 }, {
		                     offset: 0.8,
		                    color: Highcharts.theme.areaendColor // 100% 处的颜色
		                 }], false),
						shadowColor: Highcharts.theme.areashadowColor,
						shadowBlur: 10
		             }
		        },
		        itemStyle: {
		            normal: {
		                color: dataObj.seriesData[2].color,
		                borderColor: dataObj.seriesData[2].color,
		                borderWidth: 12
		
		            }
		        },
		        data: dataObj.seriesDataValue2
		    	}]
			};
		myChart.setOption(option);
	},
	renderChartsTwo : function(dataObj,queryTimeType){
			var newChartId = this.inerId;
			var legend = {enabled:false};
			var markerShowFlag = false;
			if("1H" == queryTimeType){
				markerShowFlag = true;
			}
			
			var showMetricChartsDiv = this.divObj.parent().find('.showMetricCharts');
			
			var myChart = echarts.init(showMetricChartsDiv[0]);
		
		var option = {
	    backgroundColor: 'rgba(0,0,0,0)',
	    title: {
	        text: ''
	    },
	    tooltip: {
	        trigger: 'axis',
			confine: true,
	        axisPointer: {
	            lineStyle: {
	                color:'rgba(0,0,0,0)'
	            }
	        },
			formatter:function(params){
				//var dateTool = new Date(params[0].data[0]);
				//var dateTool1 = new Date(params[1].data[0]);
				if(params[1].data == undefined){
					var dateTool = new Date(params[0].data[0]);
					return params[0].seriesName+"<br/>"+(dateTool.getMonth()<10?'0'+(dateTool.getMonth()+1):(dateTool.getMonth()+1))+'-'+(dateTool.getDate()<10?'0'+dateTool.getDate():dateTool.getDate())+' '+(dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+'  '+unitTransform(params[0].data[1], dataObj.unit)
				}
				if(params[0].data == undefined){
					var dateTool1 = new Date(params[1].data[0]);
					return params[1].seriesName+"<br/>"+(dateTool1.getMonth()<10?'0'+(dateTool1.getMonth()+1):(dateTool1.getMonth()+1))+'-'+(dateTool1.getDate()<10?'0'+dateTool1.getDate():dateTool1.getDate())+' '+(dateTool1.getHours())+':'+(dateTool1.getMinutes()<10?'0'+dateTool1.getMinutes():dateTool1.getMinutes())+'  '+unitTransform(params[1].data[1], dataObj.unit)
				}
				if(params[0].data[0] && params[1].data[0]){
					var dateTool = new Date(params[0].data[0]);
					var dateTool1 = new Date(params[1].data[0]);
					return params[0].seriesName+"<br/>"+(dateTool.getMonth()<10?'0'+(dateTool.getMonth()+1):(dateTool.getMonth()+1))+'-'+(dateTool.getDate()<10?'0'+dateTool.getDate():dateTool.getDate())+' '+(dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+'  '+unitTransform(params[0].data[1], dataObj.unit)+'<br/>'
					+params[1].seriesName+"<br/>"+(dateTool1.getMonth()<10?'0'+(dateTool1.getMonth()+1):(dateTool1.getMonth()+1))+'-'+(dateTool1.getDate()<10?'0'+dateTool1.getDate():dateTool1.getDate())+' '+(dateTool1.getHours())+':'+(dateTool1.getMinutes()<10?'0'+dateTool1.getMinutes():dateTool1.getMinutes())+'  '+unitTransform(params[1].data[1], dataObj.unit);
				}

			}
	    },
	    grid: {
	        left: '3%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true
	    },
	    xAxis: [{
	        type: 'time',
	        boundaryGap: false,
	        axisLine: {
	            lineStyle: {
	                color:Highcharts.theme.areasplinelineColor 
	            }
	        },
			axisLabel: {
				//margin: 35,
				interval:3,
				textStyle: {
					 color: Highcharts.theme.areasplinelabelsColor ,
					fontSize: 12
				}
			},
			splitLine: {
				lineStyle: {
					color: 'rgba(0,0,0,0)'
				}
	        }
	    }],
	    yAxis: [{
			type: 'value',
			name: dataObj.metricUnitName,
				nameTextStyle:{
  color:Highcharts.theme.areasplinelabelsColor   
},
			splitNumber:2,
/*		        min:0,
			max:100,*/
			axisTick: {
				show: true
			},
			axisLine: {
				
				// margin: 30,
				lineStyle: {
					color: Highcharts.theme.areasplinelineColor 
				}
			},
			axisLabel: {
				//margin: 35,
				interval:3,
				textStyle: {
					 color: Highcharts.theme.areasplinelabelsColor ,
					fontSize: 12
				},
				formatter: function(value){
					 //return unitTransform(value);	
					if (value >= 100000) {
						value = Math.round(value / 10000 * 100) / 10000 +' M';
					} else if(value >= 1000) {
						value = Math.round(value / 1000 * 100) / 100 + ' K';
					} else {
						value = value;
					}
					return value;
				}
			},
			splitLine: {
				lineStyle: {
					color: 'rgba(0,0,0,0)'
				}
			}
		}],
	    series: //dataObj.seriesData
			[{
	        name: dataObj.seriesData[1].name,
	        type: 'line',
	        smooth: true,
	        symbol: 'circle',
	        symbolSize: 5,
	        showSymbol: false,
			animation:false,
	        lineStyle: {
	            normal: {
	                width: 1
	            }
	        },
	        areaStyle: {
	        	 normal: {
	                 color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
	                     offset: 0,
	                     color: dataObj.seriesData[1].color
	                 }, {
	                     offset: 0.8,
	                     color: Highcharts.theme.areaendColor // 100% 处的颜色
		                 }], false),
						shadowColor: Highcharts.theme.areashadowColor,
						shadowBlur: 10
	             }
	        },
	        itemStyle: {
	            normal: {
	                color: dataObj.seriesData[1].color,
	                borderColor: dataObj.seriesData[1].color,
	                borderWidth: 12

	            }
	        },
	        data: dataObj.seriesDataValue1
	    },{
	        name: dataObj.seriesData[0].name,
	        type: 'line',
	        smooth: true,
	        symbol: 'circle',
	        symbolSize: 5,
	        showSymbol: false,
			animation:false,
	        lineStyle: {
	            normal: {
	                width: 1
	            }
	        },
	        areaStyle: {
	        	 normal: {
	                 color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
	                     offset: 0,
	                     color: dataObj.seriesData[0].color
	                 }, {
	                     offset: 0.8,
	                     color: Highcharts.theme.areaendColor // 100% 处的颜色
		                 }], false),
						shadowColor: Highcharts.theme.areashadowColor,
						shadowBlur: 10
	             }
	        },
	        itemStyle: {
	            normal: {
	                color: dataObj.seriesData[0].color,
	                borderColor: dataObj.seriesData[0].color,
	                borderWidth: 12

	            }
	        },
	        data: dataObj.seriesDataValue0
	    }]
	};
		myChart.setOption(option);
	},
	handleForMainValues : function(data){
		console.log(data);
		var dataMap = data.data.dataMap;
		var unit = data.data['metricUnitName'];
		var metricDataType = data.data['metricDataType'];
		console.log(dataMap);
		
		var metricGroup = this.metricGroup;
		if(metricGroup){
			//组查询只维护mainValuesArr
			var arrTemp =  new Array();
				
			for(key in dataMap){
				var value = {};
				value['metricId'] = key;
				value['max'] = "";
				value['min'] = "";
				value['avg'] = "";
				var valueInfo = dataMap[key];
				if(valueInfo && valueInfo.length>2){
					var maxV=new Number(valueInfo[1]['data']),minV=new Number(valueInfo[1]['data']),accountV=0;
					var maxVTime = new Number(valueInfo[1]['collectTime']),minVTime = new Number(valueInfo[1]['collectTime']);
				
					for(var i=0;i<valueInfo.length;i++){
						var tempObj = valueInfo[i];
						//不计算头尾null值点
						if(tempObj['data'] && tempObj['data']!="null"){
							var valNum = new Number(tempObj['data']);
							var timeNum = new Number(tempObj['collectTime']);
							
							if(valNum>maxV){
								maxV = valNum;
								maxVTime = timeNum;
							}else if(valNum<minV){
								minV = valNum;
								minVTime = timeNum;
							}
							accountV +=valNum;
						}
					}
					if("min"==metricDataType){
						value['max'] = this.formatMainValues(maxV.toString(),unit,metricDataType,maxVTime);
						value['min'] = this.formatMainValues(minV.toString(),unit,metricDataType,minVTime);
					}else{
						//汇总线图,最大最小值按后台瞬时值
						var item = valueInfo[valueInfo.length-1];
						var maxVlaue = item['MaxMetricData'];
						var maxDate = new Number(item['maxDate']);
						var minVlaue = item['MinMetricData'];
						var minDate = new Number(item['minDate']);
						
						value['max'] = this.formatMainValues(maxVlaue,unit,metricDataType,maxDate);
						value['min'] = this.formatMainValues(minVlaue,unit,metricDataType,minDate);
					}
					var avgNum = new Number(accountV/(valueInfo.length-2));
					value['avg'] = this.formatMainValues(avgNum.toFixed(2).toString(),unit,metricDataType);
				}
				arrTemp.push(value);
			}
			this.mainValuesArr = arrTemp;
			
			var parent = this.divObj.parent();
			var selectValue = parent.find('#metricIdSelect').combobox('getValue');
			
			var flagSelectValue = false;
			var comboboxData = new Array(),colorIndex1,dataIndex1;
			var color,metricObjSelect;
			for(var x=0;x<arrTemp.length;x++){
				var value = arrTemp[x];
				for(var i=0;i<metricGroup.length;i++){
					var metricObj = metricGroup[i];
					if(value['metricId']==metricObj['metricId']){
						//用于更新combobox
						comboboxData.push({id:metricObj['metricId'],name:metricObj['name']});
						if(!colorIndex1){
							colorIndex1 = metricObj['color'];
							dataIndex1 = value;
						}
					}
					if(metricObj['metricId']==selectValue && !color){
						color = metricObj['color'];
					}
				}
				if(value['metricId']==selectValue){
					flagSelectValue = true;
					metricObjSelect = value;
				}
			}
			
			if(arrTemp.length>0){
				this.mainValuesObj.load(comboboxData);
				if(flagSelectValue){
					parent.find('#metricIdSelect').combobox('setValue',selectValue);
					parent.find('span[name=showValuesColor]').attr('class',color);
					this.setMainValues(metricObjSelect);
				}else{
					parent.find('#metricIdSelect').combobox('setValue',comboboxData[0]['id']);
					parent.find('span[name=showValuesColor]').attr('class',colorIndex1);
					this.setMainValues(dataIndex1);
				}
			}else{
				this.mainValuesObj.load([]);
				parent.find('#metricIdSelect').combobox('clear');
				parent.find('span[name=showValuesColor]').attr('class',"");
				this.setMainValues({'max':"",'min':"",'avg':""});
			}
		}else{
			var metricQuery = this.getQueryMetrics();
			var valueInfo = dataMap[metricQuery];
			
				var value = {}; 
				value['metricId'] = metricQuery;
				value['max'] = "";
				value['min'] = "";
				value['avg'] = "";
				if(valueInfo && valueInfo.length>2){
				var maxV=new Number(valueInfo[1]['data']),minV=new Number(valueInfo[1]['data']),accountV=0;
				var maxVTime = new Number(valueInfo[1]['collectTime']),minVTime = new Number(valueInfo[1]['collectTime']);
					for(var i=0;i<valueInfo.length;i++){
						var tempObj = valueInfo[i];
						//不计算头尾null值点
						if(tempObj['data'] && tempObj['data']!="null"){
							var valNum = new Number(tempObj['data']);
							var timeNum = new Number(tempObj['collectTime']);
							
							if(valNum>maxV){
								maxV = valNum;
								maxVTime = timeNum;
							}else if(valNum<minV){
								minV = valNum;
								minVTime = timeNum;
							}
							accountV +=valNum;
						}
					}
					if("min"==metricDataType){
						value['max'] = this.formatMainValues(maxV.toString(),unit,metricDataType,maxVTime);
						value['min'] = this.formatMainValues(minV.toString(),unit,metricDataType,minVTime);
					}else{
						//汇总线图,最大最小值按后台瞬时值
						var item = valueInfo[valueInfo.length-1];
						var maxVlaue = item['MaxMetricData'];
						var maxDate = new Number(item['maxDate']);
						var minVlaue = item['MinMetricData'];
						var minDate = new Number(item['minDate']);
						
						value['max'] = this.formatMainValues(maxVlaue,unit,metricDataType,maxDate);
						value['min'] = this.formatMainValues(minVlaue,unit,metricDataType,minDate);
					}
					var avgNum = new Number(accountV/(valueInfo.length-2));
					value['avg'] = this.formatMainValues(avgNum.toFixed(2).toString(),unit,metricDataType);
					
			}
				this.setMainValues(value);
		}
	},
	formatMainValues : function(value,unit,metricDataType,collectTime){
		var valueStr = unitTransform(value,unit);
		if(collectTime && collectTime>0){
			switch(metricDataType){
//			case "min":
//	        	var dateTool = new Date(collectTime);
//	        	valueStr = valueStr+' '+(dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes());
//				break;
//			case "halfHour":
//				var dateTool = new Date(collectTime);
//				valueStr = valueStr+' '+dateTool.getDate()+'/'+dateTool.getHours()+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes());
//				break;
//			case "hour":
//			case "sixHour":
//				var dateTool = new Date(collectTime);
//				valueStr = valueStr+' '+dateTool.getDate()+'/'+dateTool.getHours()+':00';
//				break;
			default :
				var dateTool = new Date(collectTime);
				valueStr = valueStr+' ['+(dateTool.getFullYear()+'-'+(dateTool.getMonth()+1)+'-'+dateTool.getDate()+' '+dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+ ':' + (dateTool.getSeconds()<10?'0'+dateTool.getSeconds():dateTool.getSeconds()) +'] ';
        		//valueStr = valueStr+' ['+((dateTool.getMonth()+1)+'-'+dateTool.getDate()+' '+dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+'] ';
			break;
			}
		}
		return valueStr;
	}
	,
	setMainValues : function(value){
		var parent = this.divObj.parent();
		parent.find('span[name=valueMax]').html(value['max']);
		parent.find('span[name=valueMin]').html(value['min']);
		parent.find('span[name=valueAvg]').html(value['avg']);
	}
	,
	handleSourceData : function(data,metricDataType){
		var seriesDatas = new Array();
		var xAxisTemp ;
		var tooltip ;
		var yAxisTemp ; 
		var yAxisMaxValue = 0;
		var yAxisMinValue = 0;
		var metricUnitName = "";
		var seriesDataValues = new Array(); 
		var seriesDataValues0 = new Array(); 
		var seriesDataValues1 = new Array(); 
		var seriesDataValues2 = new Array(); 
		var unit = data.data.metricUnitName;
		
		if(null!=data.data.metricUnitName){
			metricUnitName = '单位 : '+data.data.metricUnitName;
		}
		
		var chartArr = new Array();
		if(data.data.dataStrArr){
			for(var dStrArr = 0;dStrArr<data.data.dataStrArr.length;dStrArr++){
				if("null" == data.data.dataStrArr[dStrArr]){
					chartArr.push(null);
				}else{
					var dStrArrData = parseFloat(data.data.dataStrArr[dStrArr]);
					chartArr.push(dStrArrData);
					if(dStrArrData>yAxisMaxValue){
						yAxisMaxValue = dStrArrData;
					}
					if(dStrArrData<yAxisMinValue){
						yAxisMinValue = dStrArrData;
					}
					haveValue = true;
				}
			}
		}
		var queryMetric = this.getQueryMetrics();
		if(data.data.dataMap){
			var that = this;
			
			var dataMap = data.data.dataMap;
			var metricGroup = that.metricGroup;
			//处理数据
			if(that.openType != 'biz'){
				that.handleForMainValues(data);
			}
			if(metricGroup){
				for(var x=0;x<metricGroup.length;x++){
					var metricObj = metricGroup[x];
					var metricItemData = dataMap[metricObj.metricId];
					var dDouArr = new Array();
					var dDouArr0 = new Array();
					var dDouArr1 = new Array();
					var dDouArr2 = new Array();
					var metricIdName = metricObj.metricId;

					if(metricItemData){
						for(var dDouStrArr = 0;dDouStrArr<metricItemData.length;dDouStrArr++){
							var metricItemDataObj = metricItemData[dDouStrArr];
							if("null" == metricItemDataObj['data']){
								switch (metricIdName) {
								case "ifBandWidthUtil":
								case "ifInOctetsSpeed":
									var date = new Date(parseInt(metricItemDataObj['collectTime']-8*3600*1000));
									
									var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//parseInt(date.getSeconds()));
									
									dDouArr0.push([dateUtc,null]);
									seriesDataValues0 = dDouArr0;
									break;
								case "ifInBandWidthUtil":
								case "ifOutOctetsSpeed":
									var date = new Date(parseInt(metricItemDataObj['collectTime']-8*3600*1000));
									
									var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//parseInt(date.getSeconds()));

									dDouArr1.push([dateUtc,null]);
									seriesDataValues1 = dDouArr1;
									break;
								default:
									var date = new Date(parseInt(metricItemDataObj['collectTime']-8*3600*1000));
									
									var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//parseInt(date.getSeconds()));
									
									dDouArr2.push([dateUtc,null]);
									seriesDataValues2 = dDouArr2;
									break;
								}
							}else{
								switch (metricIdName) {
								case "ifBandWidthUtil":
								case "ifInOctetsSpeed":
									var date = new Date(parseInt(metricItemDataObj['collectTime']-8*3600*1000));
									var dDouArrData = parseFloat(metricItemDataObj['data']);
									var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//parseInt(date.getSeconds()));
									
									if(dDouArrData>yAxisMaxValue){
										yAxisMaxValue = dDouArrData;
									}
									if(dDouArrData<yAxisMinValue){
										yAxisMinValue = dDouArrData;
									}
									dDouArr0.push([dateUtc,dDouArrData]);
									seriesDataValues0 = dDouArr0;
									break;
								case "ifInBandWidthUtil":
								case "ifOutOctetsSpeed":
									var date = new Date(parseInt(metricItemDataObj['collectTime']-8*3600*1000));
									var dDouArrData = parseFloat(metricItemDataObj['data']);
									var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//parseInt(date.getSeconds()));
									
									if(dDouArrData>yAxisMaxValue){
										yAxisMaxValue = dDouArrData;
									}
									if(dDouArrData<yAxisMinValue){
										yAxisMinValue = dDouArrData;
									}
									dDouArr1.push([dateUtc,dDouArrData]);
									seriesDataValues1 = dDouArr1;
									break;
								default:
									var date = new Date(parseInt(metricItemDataObj['collectTime']-8*3600*1000));
									var dDouArrData = parseFloat(metricItemDataObj['data']);
									var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//parseInt(date.getSeconds()));
									
									if(dDouArrData>yAxisMaxValue){
										yAxisMaxValue = dDouArrData;
									}
									if(dDouArrData<yAxisMinValue){
										yAxisMinValue = dDouArrData;
									}
									dDouArr2.push([dateUtc,dDouArrData]);
									seriesDataValues2 = dDouArr2;
									break;
								}
							}
						}
					}
					var seriesTemp = {name:metricObj.name,data:dDouArr,type: 'line',color:metricObj.color};
	 				seriesDataValues = dDouArr;
	 				
					seriesDatas.push(seriesTemp);
				}
			}else{
				var metricId = this.meticId; 
				var metricItemData = dataMap[metricId];
				var dDouArr = new Array();
				var seriesTemp = {data:dDouArr};
				seriesDatas.push(seriesTemp);
				
				if(metricItemData){
					for(var dDouStrArr = 0;dDouStrArr<metricItemData.length;dDouStrArr++){
						var metricItemDataObj = metricItemData[dDouStrArr];
						if("null" == metricItemDataObj['data']){
							var date = new Date(parseInt(metricItemDataObj['collectTime'])-8*3600*1000);
							var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//, parseInt(date.getSeconds())
							
							dDouArr.push([dateUtc,null]);
						}else{
							var date = new Date(parseInt(metricItemDataObj['collectTime'])-8*3600*1000);
							var dDouArrData = parseFloat(metricItemDataObj['data']);
							var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//parseInt(date.getSeconds()));
							
							if(dDouArrData>yAxisMaxValue){
								yAxisMaxValue = dDouArrData;
							}
							if(dDouArrData<yAxisMinValue){
								yAxisMinValue = dDouArrData;
							}
							dDouArr.push([dateUtc,dDouArrData]);
						}
					}
				}
				
			}
		}else if(null==queryMetric||queryMetric==""){
			seriesDatas.push({});
		}
		xAxisTemp = {
			type: 'category',
			boundaryGap: false,
			axisLine: {
            lineStyle: {
                color: '#57617B'
            	}
			},
			data: ['周一','周二','周三','周四','周五','周六','周日']
		};
		
		//没有值不显示单位
//			if(haveValue){
			//处理设置最大值
//			if((yAxisMaxValue%100)!=0){
//				yAxisMaxValue = parseInt(yAxisMaxValue/100)*100+100;
//			}
			if(yAxisMaxValue == 0){
				yAxisMaxValue = 100;
			}
			if(that.openType != 'biz'){
				yAxisTemp = {
						max:yAxisMaxValue,
			        	min : yAxisMinValue,
			        	gridLineWidth: 0,
						splitLine: false,
			            title: {
			            	text: metricUnitName
			            }
			        };
			}else{
				yAxisTemp = {
						max:yAxisMaxValue,
			        	min : yAxisMinValue,
			        	gridLineWidth: 0,
			            title: {
			            	enabled:false
			            }
			        };
			}
			
			switch(metricDataType){
			case "min":
				tooltip = {
					
					trigger: 'axis',
					formatter: function() {
		            	//highchart BUG this.x该时间比当前时间快8小时
		            	var dateTool = new Date(this.x-8*3600*1000);
		            	var valueNum = this.y;
		            	var alertStr = unitTransform(valueNum,data.data.metricUnitName);
		            	
		            	return (dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+' : '+alertStr;
		            },

										axisPointer: {
						lineStyle: {
							color: '#57617B'
						}
					},
		        };
				break;
			case "halfHour":
				tooltip = {
					
					trigger: 'axis',

					axisPointer: {
						lineStyle: {
							color: '#57617B'
						}
					},
		            formatter: function() {
		            	//highchart BUG 该时间比当前时间快8小时
		            	var dateTool = new Date(this.x-8*3600*1000);
		            	var valueNum = this.y;
		            	var alertStr = unitTransform(valueNum,data.data.metricUnitName);
		            	
		            	return dateTool.getDate()+'/'+dateTool.getHours()+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+' : '+alertStr;
		            }
		        };
				break;
			case "hour":
			case "sixHour":
				tooltip = {
					enabled: true,
		            formatter: function() {
		            	//highchart BUG 该时间比当前时间快8小时
		            	var dateTool = new Date(this.x-8*3600*1000);
		            	var valueNum = this.y;
		            	var alertStr = unitTransform(valueNum,data.data.metricUnitName);
		            	
		            	return dateTool.getDate()+'/'+dateTool.getHours()+':00 : '+alertStr;
		            }
		        };
				break;
			default :
				tooltip = {
					enabled: true,
		            formatter: function() {
		            	//highchart BUG this.x该时间比当前时间快8小时
		            	var dateTool = new Date(this.x-8*3600*1000);
		            	var valueNum = this.y;
		            	var alertStr = unitTransform(valueNum,data.data.metricUnitName);
		            	
		            	return (dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+' : '+alertStr;
		            }
		     };
				break;
			}
			
			return {
				seriesDataValue:seriesDataValues,
				seriesDataValue0:seriesDataValues0,
				seriesDataValue1:seriesDataValues1,
				seriesDataValue2:seriesDataValues2,
				metricUnitName:metricUnitName,
				unit:unit,
				xAxisTemp : xAxisTemp,
			    yAxisTemp : yAxisTemp,
			    tooltip : tooltip,
			    seriesData : seriesDatas};
	}
	
};

function formatDate(date){
	return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
}
function unitTransform(value,unit){
	return oc.module.resmanagement.resdeatilinfo.general.unitTransform(value,unit);
}
