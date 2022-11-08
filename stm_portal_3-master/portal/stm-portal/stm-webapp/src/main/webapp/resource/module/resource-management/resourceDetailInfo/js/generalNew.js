$(function() {
	function general(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		var id = oc.util.generateId();
		this.generalDom = $("#generalContent").attr('id', id);
	}
	function formatDate(date){
		return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
	}
	general.prototype = {
		constructor : general,
		cfg : undefined, // 外界传入的配置参数 json对象格式
		generalDom : undefined,
//		rightDom : undefined,
		flag : true,
		showName : undefined,
		moduleType:undefined,
		chartObj : undefined,
		layout : {},
		_datagrid:undefined,
		child_no_monitor : 0,
		child_up : 0,
		child_up_down : 0,
		child_down : 0,
		child_unkown : 0,
		_defaults : {},
		_perMetrics:undefined,
		open : function() {
			var that = this;
			
			oc.util.ajax({
				url : oc.resource.getUrl("portal/resource/resourceDetailInfo/getMetric4General.htm"),
				data : {instanceId : this.cfg.instanceId},
				timeout : null,
				async:false,
				successMsg:null,
				success : function(json){

					if(json.code == 200){
						var tableDom;
						that.createTab(json.data,json.data.length);
					}else{
						return false;
					}
				}
			});
		},
		createTab : function(jsonData, length){

		var tinyToolDom = $("<div/>").addClass("tinytool");
	

		var tabOutDom = $("<div/>");//.css("margin-bottom","9px");
		if($("#mainInfolayoutCenter").length!=0){
			//tabOutDom.css({"overflow-y":"auto","overflow-x":"hidden"});
		}	
		var tabDom=$("<table/>").width("100%").height("100%");
			tabDom.addClass("resource-table");
	
			this.generalDom.append(tabOutDom.append(tabDom));
			if($(".tinytool").length==0){//not create tinytool
			    $(".main-resource").after(tinyToolDom);
			    if($("#mainInfolayoutCenter").length!=0){
			    	tinyToolDom.addClass("biz-tinyToolDom");//.css('margin','10px 0px');
			    	tabOutDom.append(tinyToolDom);
			    }
			    
				this.createTinyTool(tinyToolDom);
				}else{
					$(".tinytool").html("");
				this.createTinyTool($(".tinytool"));	
				}
			if(length==3){//3个td 的布局
				this.createOneTr(tabDom,jsonData,length);
			}else{//2个tr的布局
				for(var i=0;i<jsonData.length;i++){
					this.createTwoTr(tabDom,jsonData[i],length);
				}
				
			}

			
		},
		createOneTr : function(tabDom, trData,length){
			var trDom=$("<tr/>");//.width("100%");
			tabDom.append(trDom);
			for(var i=0;i<trData.length;i++){
				this.createTd(trDom, trData[i], trData[i].length,1);
			}
			
		},
		createTwoTr : function(tabDom,trData,length){
				var trDom=$("<tr/>");//.width("100%");
			tabDom.append(trDom);

			for(var i=0;i<trData.length;i++){
				this.createTd(trDom, trData[i], trData[i].length,2);
			}
		},
		createTd : function(trDom, tdData, tdSize,num){
			var that=this;

			var tdDom=$("<td/>");//.width(tdData[0].width).css("word-break","break-all");
			if(num==1){
				tdDom.width(tdData[0].width).css("word-break","break-all").css("vertical-align","top");
			}else{
				tdDom.css("vertical-align","top");
			}
			trDom.append(tdDom);
			if(num==2){
				this.createDiv(tdDom,tdData);
					for(var i=0;i<tdData.length;i++){

			}
			}else{
					for(var i=0;i<tdData.length;i++){

				this.createDiv(tdDom,tdData[i]);
			}
			}
		
			
			
			
		},
		createDiv :function(tdDom,divData){
			var divDom=$("<div/>").width(divData.width);
			var textDom=$("<div/>").width(divData.width);;//this.createPanel(tdDom, divData, divData.length);
		var testDom=	this.createTextPanel(textDom, divData, divData.length);
	var testDom=	this.createTextPanel(textDom, divData, divData.length);

		
	
	/*	testDom.css("height","260px");
		testDom.css("width","260px");
	if(this.cfg.resourceType=='Hardware' || this.cfg.resourceType=='UniversalModel'  ){
		testDom.css("height","220px");
		}else if(this.cfg.resourceType=='VMware'){
			if( this.cfg.resourceId=='VMWareDatastore' ){
				testDom.css("width","320px");
				testDom.css("height","220px");
			} 	
		}else if( this.cfg.resourceType=='Storage'){
			testDom.css("height","217px");
		}else if(this.cfg.resourceType=='WeatherFile' || this.cfg.resourceType=='SignalDetection'){
			testDom.css("height","220px");
		}*/
		
		var testinnerDiv=$("<div/>");
		var testinnerbgDiv=$("<div/>");//.addClass("layout-td-panel-bg");
	
		if(divData.length==4){
			divDom.addClass("center-layout");
		}
		if(divData.length>5){
			divDom.addClass("layout-td-panel-bg");
			var title=new Array();
			var num=0;
			testDom.css("height",divData[0].height);
			testDom.css("width",divData[0].width);
			for(var i=0;i<divData.length;i++){
				if(divData[i].id==null || divData[i].id=='statusLight'){//标题
				if(divData[i].id=='statusLight'){
					var values=new Array()
					values.push(divData[i]);
					title.push(values);
				}else{
						var values=new Array()
					values.push(divData[i]);
					values.push(divData[i+1]);
					title.push(values);
				}
			
				}
			}
		
				
				divDom.append(	testDom.append('<span class="layout-title">基础信息</span><br/>'));//.height("40%");
					for(var j=0;j<title.length;j++){
				this.createText2(testDom, title[j],title.length);
			}
			}else if(divData[0].type=="multiMeter"){
			//	divDom.append(textDom).height("50%");
			}
		//	tdDom.append(testinnerDiv.append(testinnerbgDiv.append(divDom)));
			tdDom.append(divDom);
			var formDom=$("<form/>");
			if(divData.length==2 ){//gauge
				switch(divData[0].type){
				case "mulitpie" :
					this.createmulitpie(divDom, divData,divData.length);
					break;
				case "tabPanel":
				tdDom.attr('colspan',divData[0].colspan);
				this.createtabPanel(divDom, divData,divData.length);
					break;
				}
				
				
			}else if(divData.length==4 || divData.length==3  || divData.length==2){
				if(divData[0].type=='tabPanel'){
				tdDom.attr('colspan',divData[0].colspan);
				this.createtabPanel(divDom, divData,divData.length);	
				}else{
				this.createmoreDiv(divDom,divData,divData.length);
				}
				
			
				
			}else{
				for(var i=0;i<divData.length;i++){
				
					switch(divData[i].type){
					case "meter":
						this.createMeter(divDom, divData[i],divData.length);
						break;
					case "mulitgauge" :
					this.createmultigauge(divDom, divData[i],divData.length);
					break;
					case "gauge":
					this.createsinglegauge(divDom, divData[i],divData.length,1);
						break;
		/*				case "progress":
						this.createProgress(divDom, divData[i],divData.length);
						break;*/
					case "multiMeter":case "table":case "fan":case "cpu":case "node":case "battery":case "camera":
					case "multiInterface" :
					case "multiprocess":
					
					case "microphone":case "display":case "commonChildrenPanel":
						this.createChildrenRes(divDom, divData[i],divData.length);
						break;
					case "tabs":
					case "tabs-full":
						this.createTabs(divDom, divData[i],divData.length);
						break;
					case "datagrid":
						tdDom.attr('colspan',divData[i].colspan);
						this.createAvailabilityMetric(divDom, divData[i],divData.length);
						break;
					case "graph":
						this.createGraph(divDom, divData[i],divData.length);
						break;
					case "panel":
						this.createChildPanel(divDom, divData[i],divData.length);
						break;
					case "pie":
						this.createPie(divDom, divData[i],divData.length);
						break;
					case "pie2"://添加new pie
						this.createPie(divDom, divData[i],divData.length);
						break;
					case "commonMetricPie":
						this.createPie(divDom, divData[i],divData.length);
						break;
					case "table":
					//	this.createChildTable(divDom, divData[i],divData.length);
						break;
					case "environmentPanel":
						this.createEnvironmentPanelDiv(divDom, divData[i],divData.length);
						break;
					case "metricTabs":
						this.createMetricTabs(divDom, divData[i],divData.length);
						break;
					case "tableMetric":
						this.createTableMetric(divDom, divData[i],divData.length);
						break;
					case "commonOneChildrenPanel":
						this.createCommonOneChildrenPanel(divDom, divData[i],divData.length);
						break;
					case "commonOneMetricPanel":
						this.createCommonOneMetricPanel(divDom, divData[i],divData.length);
						break;
					case "solidgauge":
						this.createSolidgauge(divDom, divData[i],divData.length);
						break;
					case "alarm":
					if(this.cfg.resourceType=='Hardware' ){//|| this.cfg.resourceType  =='VirtualStorage'
						//	tdDom.width(300);
						
					}
				//		this.createChildPanel(divDom, divData[i],divData.length);
						this.createAlarm(divDom, divData[i],divData.length);
						break;
					case "bar":
						this.createBar(divDom,divData[i],divData.length);
						break;
					case "others":
						this.createothers(divDom,divData[i],divData.length);
						break;
					}
				}
			}
		
		
		},
		createmoreDiv:function(tdDom,datas,tdSize){
			tdDom.attr('rowspan','3');
			var mores = this.createPanel(tdDom, datas, tdSize);
				var gaugedata=[];
				var newdatas=[];
				var that=this;
				if(this.cfg.resourceType=='UniversalModel'){

//					for(var i=0;i<datas.length;i++){
						//查询所有的性能指标，默认显示前三个
						oc.util.ajax({
							url : oc.resource.getUrl('portal/resource/resourceDetailInfo/getMetric.htm'),
							dataType:"json",
							data : {
								instanceId : this.cfg.instanceId,
								metricType : "PerformanceMetric"
							},
							async:false,
							success:function(json){
								var rows = json.data.rows;
							if(rows.length==0){
								mores.html("<div class='otherTabTip'>抱歉，没有可展示的数据！</div>");
								return;
							}
								for(var i = 0; i < rows.length; i ++){
									if(i<=2){
									
									var row = rows[i];
									that.createmultigraph(mores, row,i);
									}
								}
							}
						});
						
						
//					}
				}else{
					for(var i=0;i<datas.length;i++){
						
						if(datas[0].type=='mulitgauge'){//one
						if(i<2){
							gaugedata[i]=datas[i];
							
						}else{
						this.createmultigraph(mores, datas[i],datas[i].length);
						}
					}else{
						if(i<1){
							gaugedata[i]=datas[i];
							
						}else{
							if(datas[i].type=='others'){
								this.createothers(mores, datas[i],datas[i].length)
							}else{
								this.createmultigraph(mores, datas[i],datas[i].length);
							}
						
						}
					}
						
					}
					
						this.createmultigauge(mores, gaugedata,gaugedata.length);	
				}
			
					
		},
		createothers:function(tdDom, tdData,tdSize){
				var others = this.createPanel(tdDom, tdData, tdSize);
				others.append("<div class='otherTabTip'>抱歉，没有可展示的数据！</div>");
		},
		createBar:function(tdDom, tdData,tdSize){
			var that=this;
			var bar = this.createPanel(tdDom, tdData, tdSize).css('height','95%');
	
		if(tdData.value.Sortdata=="" || tdData.value.Sortdata==undefined){
			bar.append('<div class="otherTabTip">抱歉，没有可展示的数据！</div>');
		}else{
			var categorArr=new Array();
			if(tdData.value.categories.length!=0){
				var categories=tdData.value.categories;
				for(var i=0;i<categories.length;i++){
					var cate=categories[i];
					if(cate.length>7){
						cate=cate.substring(0,6);
					}
					categorArr.push(cate);
				}
			}
			try{
				var data=tdData.value.Sortdata;
				for(var i=0;i<data.length;i++){
					var d=data[i];
					d.y=d.y*1;
					d.color=(d.state=="NORMAL"?Highcharts.theme.resourceDetailMetricColors[0]:d.color=="WARN"?Highcharts.theme.resourceDetailMetricColors[1]:d.color=="SERIOUS"?Highcharts.theme.resourceDetailMetricColors[2] : Highcharts.theme.resourceDetailMetricColors[0] )
				
				}
				bar.highcharts({
			        chart: {
			            type: 'bar'
			        },
			        title: {
			            text: ''
			        },
			        xAxis: {
			        	 lineColor: Highcharts.theme.barlineColor,
			        	 min:0,
			            categories: tdData.value.categories,
			            title: {
			                text: null
			            },
			            labels:{
			            	
			            	style:{
			            		color:Highcharts.theme.barlabelsColor
			            	},
			            	  useHTML:true,
			                  formatter:function(){
			                  	var cate=this.value;
			            		if(cate.length>7){
									cate=cate.substring(0,6);
								}
			                      return '<span title="'+this.value+'" >'+ cate+'</span>' ;
			                  }
			            }
			        },
			        yAxis: {
			       	 lineColor: Highcharts.theme.barlineColor,
			       	 lineWidth:1,
			       	 gridLineColor:Highcharts.theme.bargridLineColor,
			            min: 0,
			            title: {
			                text: '',
			                align: 'high'
			            },
			            labels:{
			            	enabled:true,
			            	style:{
			            		color:Highcharts.theme.barlabelsColor
			            	}
			            }
			        },
			        tooltip: {
			            valueSuffix: ' ',
			            padding:22,
			            formatter : function() {
							return "<b>"+tdData.value.categories[this.point.x]+':</b><br/><b>'+this.point.name+"</b>";
					 }
			            
			        },
			        plotOptions: {
			            bar: {
			            	borderColor:'',
			                dataLabels: {
			                    enabled: false,
			                    allowOverlap: true
			                }
			            },
			            series: {  
			                animation: false  
			            } 
			            
			        },
			        legend: {
			          enabled:false
			        },
			        credits: {
			            enabled: false
			        },
			        exporting:{
			            enabled:false
			        },
			        series: [{
			            name: '接口流量',
			            data: tdData.value.Sortdata
			        }]
			    });
			}catch(e){
				
			}
	/*			*/
		}
		
		},
		createAlarm: function(tdDom, tdData,tdSize,num){
		//	debugger;
			var that=this;
			var alarmpie=$("<div/>");
			if(num==1){
				var centerDiv=$("<div/>").addClass("centerAlarm").css({"margin":"0px 30px"});
				tdDom.append(centerDiv);
				alarmpie=centerDiv;
				//加border
			}else{
			alarmpie = this.createPanel(tdDom, tdData, tdSize);
			}
						
			var alarmArr=new Array();

			var obj = tdData.chartData;
			var chartData=[];
			if(obj!=null){
			
				var chartData = [{
					name : "致命",
					type:'critical',
					y : 0,
					num:0,
					color : Highcharts.theme.resourceDetailMetricColors[3]
				}, {
					name : "严重",
					type:'serious',
					y : 0,
					num:0,
					color : Highcharts.theme.resourceDetailMetricColors[2]
				}, {
					name : "警告",
					type:'warn',
					y : 0,
					num:0,
					color : Highcharts.theme.resourceDetailMetricColors[1]
				}

				];
				var newChart = null;
				if(obj.total>0){
					var criticalPe = obj.total==0?0:(obj.critical/obj.total)*100;
					var seriousPe = obj.total==0?0:(obj.serious/obj.total)*100;
					var warnPe = obj.total==0?0:(obj.warn/obj.total)*100;
					if(criticalPe>0 && criticalPe<1){
						criticalPe=1;
					}else{
						criticalPe = Math.round(criticalPe);
					}
					if(seriousPe>0 && seriousPe<1){
						seriousPe=1;
					}else{
						seriousPe = Math.round(seriousPe);
					}
					if(warnPe>0 && warnPe<1){
						warnPe=1;
					}else{
						warnPe = Math.round(warnPe);
					}
				//	chartData[0].y = criticalPe;
					chartData[0].y = criticalPe;
					chartData[1].y = seriousPe;
					chartData[2].y = warnPe;
					//chartData[0].num = obj.total;
					chartData[0].num = obj.critical;
					chartData[1].num = obj.serious;
					chartData[2].num = obj.warn;
					
					var newChart = chartData;
//					var newChart = chartData.sort(function(a,b){
//						 return (a['y']<b['y'])?1:-1; 
//					});
					//百分比省略小数点，导入百分比总数小于100%，将第一个数加上总数与100相差的数，构成100%
					var count = ((newChart[0].y)+(newChart[1].y)+(newChart[2].y));
					if(count<100 && count>0){
						newChart[0].y=newChart[0].y+(100-count);
					}else if(count>100){
						newChart[0].y=newChart[0].y-(count-100);
					}
				}
				var  loadData = {data:newChart,label:[obj.critical,obj.serious,obj.warn],title:obj.total,type:tdData.type};
				that.createalarmPie(alarmpie,loadData,obj.total);
				var time="";
				if(tdData.divData){
				 time=		formatDate(new Date(tdData.divData.collectionTime));
				 var levelStr="";var classStr="";
				 if(tdData.divData.level=="SERIOUS"){
					 levelStr="严重";
					 classStr="orangelight";
				 }else if(tdData.divData.level=="WARN"){
					 levelStr="警告";
					 classStr="yellowlight";
				 }else{
					 levelStr="致命";
					 classStr="redlight";
				 }
				 var content=tdData.divData.content;
				 var contentStr=content;
				 if(content.length>0 && content.length>35){

					 contentStr=contentStr.substring(0,35)+"...";
				 }
				 alarmpie.append("<div><fieldset class='alarm-fieldset' style=width: 320px'><legend>最新告警</legend><label>告警级别：<label style='vertical-align:middle' class='light-ico "+classStr+"'>"+levelStr+"</label></label><br/><span style='cursor:pointer;' title='"+content+"' >告警内容："+contentStr+"</span><br/><label>产生时间："+time+"</label></fieldset></div>");

				}else{
//132****
					alarmpie.append("<div><fieldset class='alarm-fieldset' style='height: 120px;width: 286px'><legend>最新告警</legend><label><div class='otherTabTip'>暂无告警</div></label></fieldset></div>");

				}

				
			}
			
		
			
		},
		createalarmPie:function(alarmPie,data,num){
			var type=data.type;
			var titleY = 10,that = this,clas_name="alarm-cue";
			
			var centerDiv=$("<div/>").css({"margin":"0px 20px"});
			if(type=="tabPanel"){
				clas_name="alarm-panel-cue"
			}
		if(data.data==null){
			for(var i=0;i<3;i++){
				var name="";var color="";
				if(i==2){
					name="警告";
					color=Highcharts.theme.resourceDetailMetricColors[1];
				}else if(i==1){
					name="严重";
					color=Highcharts.theme.resourceDetailMetricColors[2];
				
				}else if(i==0){
					name="致命";
					color=Highcharts.theme.resourceDetailMetricColors[3];
				}
				var html="";
				html+='<div class="'+clas_name+'" style="border-color:'+color+'"><font size="6" color="#f9fafc">0</font><br><span>'+name+'告警</span></div>';
				alarmPie.append(html);
			}
		}else{
			for(var i=0;i<data.data.length;i++){
				var html="";
				var numColor=data.data[i].color;
				if(data.data[i].num==0){
					numColor=Highcharts.theme.resourceDetailNoAlarmFontColor;
				}
				var numtemp=data.data[i].num+"";
				var numShow=numtemp.length>2 ? numtemp.substring(0,2) :data.data[i].num;
				html+='<div class="'+clas_name+'" style="border-color:'+data.data[i].color+'"><font size="6" title="'+data.data[i].num+'" color="'+numColor+'">'+numShow+'</font><br><span>'+data.data[i].name+'告警</span></div>';
				alarmPie.append(html);
			}
//			alarmPie.css({"padding-top":"30px"});
			/*
			alarmPie.highcharts({
		        chart: {
		        	  plotBackgroundColor: null,
		              plotBorderWidth: null,
		              plotShadow: false
		        },
		        title: {
		            text: '告警',
		            	align:'left',
		            	floating:true
		        },
		        tooltip: {
		        	 formatter : function() {
							return "<b>"+this.point.name+":</b>"+this.y+"%";
					 }
		        },
		        plotOptions: {
		        	  pie: {
		                  allowPointSelect: true,
		                  cursor: 'pointer',
		                  dataLabels: {
		                      enabled: false
		                  },
		                  showInLegend: true
		              }
		        },
		        legend: {//控制图例显示位置
		        	  title: {
		                  text: '告警数: '+num,
		                  style: {
		                      fontStyle: 'italic'
		                  }
		              },
		              floating:true,
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'top',
		            borderWidth: 0,
		            labelFormatter : function(){
		            	var name =this.name+": "+this.num;
		            
		            	return name;
		            }
		        },
		        
		        series: [{
		            type: 'pie',
		            name: '',
		            
		            data:data.data,
					dataLabels : {
						formatter : function() {
							if(parseInt(data.title)>0){
								return '<span>' + this.y+ '%' + '</span>';
							}else{
								return null;
							}
							
						}
					}
		        },
		        ]
		    });
		*/}
/*			this.selector.find("#fatalLabel").text(labels[0]+"个");
			this.selector.find("#seriousLabel").text(labels[1]+"个");
			this.selector.find("#warningLabel").text(labels[2]+"个");*/
//			this.selector.find("#unkownLabel").text(labels[3]+"个");
//			this.selector.find("#normalLabel").text(labels[4]+"个");
		},
		createText2:function(tdDom, tdData,tdSize){
				var that = this;
			var user = undefined;
			//只有在显示名称的时候才去调用getUser
			var html='';
			var status="";
			for(var i=0;i<tdData.length;i++){
				if(i==0){
		
					if(tdData[i].id == 'statusLight'){	
						html+='<span style="display:none;">状态:</span>';
						var clas=this.returnStatusLight(tdData[i].value, 'mainInstance');
					html+='<span style="height:20px;width:20px; display:none;" class="light-status '+clas+'"></span>';			
					status=clas;
					}else{
						html+='<span>'+tdData[i].value+':</span>';
					}
				}else{
				}
				 
			}
			var id=tdData[0].id;
			if(tdData.length>1){
				id=tdData[1].id;
			}
			var content = $("<div/>").addClass(id).addClass("resource-baseinfo").css({
			
			}).html(html);
			tdDom.append(content);
			if(tdData[0].id == 'statusLight'){
			//	that.czspan(tdData[0],tdDom,content);
			//	console.info($(".light-status"));
			}else{
			that.czspan(tdData[1],tdDom,content,status);
			}
		},
		czspan:function(tdData,tdDom,content,clas){
			
			var html='';
			var user = undefined;
			var that=this;
			//var tdDom = this.createPanel(td2Dom, tdData, tdSize);
			//只有在显示名称的时候才去调用getUser
		
			if(tdData.id == 'showname'){			
				user = oc.index.getUser();			
			}	
		
			if(tdData.id == 'ip'){
				//若待显示资源的类别为DTCenter虚拟机或麒麟虚拟机时，将ip显示为纯文本框
				if(this.cfg.categoryId == 'DTCenterECSs' || this.cfg.categoryId == 'KyLinVms'){
					tdDom.find("."+tdData.id).append('<span>'+tdData.value[0].id+'</span>');
				}else{
						var 	IP = $("<span/>");
							//	tdDom.append(IP);//.append("<br>");
							var sel=$("<select/>");
									IP.append(sel );
									tdDom.find("."+tdData.id).append(IP);
								oc.ui.combobox({
									selector : sel,
									placeholder : false,
									data : tdData.value,
									value :tdData.value.length==0 ? "": tdData.value[0].id
								});
				}
			}else if(tdData.id == 'liablePerson'){
								oc.util.ajax({
										url:oc.resource.getUrl('system/login/getUserByInstanceId.htm'),		
										data:{instanceId: this.cfg.instanceId},
										success:function(d){
											var liableUser=d.data;
											if(liableUser.name!=null && liableUser.name != undefined ){		
											var content=liableUser.name;
											if(liableUser.mobile==null){
												liableUser.mobile="";
											}
											if(liableUser.email==null){
												liableUser.email="";
											}					
											var tdHtml = liableUser.name+" "+liableUser.mobile;
									
											if(liableUser.mobile=="" || liableUser.mobile==null){
													if(liableUser.name.length >=20){
												tdHtml = liableUser.name.substring(0, 12)+ "...";//+liableUser.mobile;
											
											}
											}else{
											
													if(liableUser.name.length >= 20){
														tdHtml = liableUser.name.substring(0, 6)+ "..."+liableUser.mobile;
													
													}
											}
											
										
											tdDom.find("."+tdData.id).append("<span title='"+liableUser.name+"'>"+tdHtml+"</span>");	
											    		
												
											}
										}
									});			
								}else if(tdData.id == 'showname'/* && typeof(that.cfg.callback) != 'undefined'*/
									/*&& */){
									
									var editname=tdData.value;
									var value="";
									if(tdData.value != undefined && tdData.value != null  && tdData.value.length > 10){//&& that.cfg.resourceType=='Hardware'

										value = tdData.value.substring(0, 10) + "...";
									}else{
										value = tdData.value;
									}
									
									that.showName = value;//tdData.value;

									var out=$("<span/>");
									var outEditNode = $("<div/>");//.width('120px').height('100%');
									var editBtn = $("<span/>").addClass('uodatethetext fa fa-edit').attr('title', '编辑名称').css('display','none');//.css('float','right')
									var clas= tdDom.find(".statusLight span").eq(1).attr("class");
									var statusHtml=$('<a style="height:20px;width:20px;vertical-align:middle;position:relative;top:-2px;" class="'+clas+'"></a>');
									var editNode = $("<input name='editName' type='text'>").width('100px');//.css('float','left')
									editNode.validatebox({
										validType : 'maxLength[30]'
									});
									var showNode = $("<div name='showName' />").addClass('generalShowName')
													.attr('title', editname).html(that.showName.htmlspecialchars());//.width('100px');//.css('float','left')
									if(user.domainUser || user.systemUser){
										outEditNode.append(statusHtml).append(editNode.hide()).append(showNode).append(editBtn);
									}else{
										outEditNode.append(statusHtml).append(editNode.hide()).append(showNode);
									}
									
								//	out.append(statusHtml);
									out.append(outEditNode);
									//statusHtml
								//	tdDom.find("."+tdData.id).append(statusHtml);
									tdDom.find("."+tdData.id).append(out);

									//动态显示修改图标
									tdDom.find('.showname').on('mouseenter',function(){  
										var target = $(this);
										target.find('.uodatethetext').attr('style','display:block');
									 });
									tdDom.find('.showname').on('mouseleave',function(){  
										var target = $(this);
										target.find('.uodatethetext').attr('style','display:none');
									});

									editBtn.on('click', function() {
										if (that.flag) {
											editNode.val(editname).show();
											showNode.html('').hide();
											editBtn.attr('title', '确认名称修改').removeClass().addClass('uodatethetextok fa fa-check');
											that.flag = false;
										} else {
											if(editNode.validatebox('isValid')){
												var newShowName = editNode.val();
												if(newShowName != undefined && newShowName.replace(/(^\s*)(\s*$)/g, '') != ''){
													oc.util.ajax({
														
														url : oc.resource.getUrl("portal/resource/discoverResource/updateInstanceName.htm"),
														data : {newInstanceName:newShowName,instanceId:that.cfg.instanceId},
														successMsg:null,
														success : function(json){
															if(json.code == 200){
																if(json.data == 0){
																	alert('修改显示名称失败！');
																}else if(json.data == 1){
																	alert('修改显示名称成功！');
																	editname=newShowName;
														var value="";
									                  if(newShowName != undefined && newShowName != null && newShowName.length > 10){

									                	  value = newShowName.substring(0, 10) + "...";
									                        }else{
									                        	value =newShowName;// tdData.value
									                        }
																	showNode.html(value.htmlspecialchars()).attr('title', newShowName).show();
											
																	that.showName = value;
																	editNode.val('').hide();
																	editBtn.attr('title', '编辑名称').removeClass().addClass('uodatethetext  fa fa-edit');
																	that.flag = true;
																	oc.module.resmanagement.resdeatilinfonew.updateDlgTitle(newShowName);
																}else{
																	alert('资源显示名称重复');
																}
															}else{
																alert('修改显示名称失败！');
															}
														}
													});
												}
											}
										}
									});
								}else if(tdData.id == 'availability'){
									var content = '可用';
									if(tdData.value == 'CRITICAL' || tdData.value == 'CRITICAL_NOTHING'){
										content = '不可用';
									}else if(tdData.value == 'UNKOWN'){
//										content = '未知';
									}
									tdDom.find("."+tdData.id).append('<span>'+content+'</span>');
								//	tdDom.find("."+tdData.id).html(content);				
								}else if(tdData.id == 'resourceImg'){
									if(tdData.value){
										tdDom.find("."+tdData.id).append('<img id="tvImg" style="width:80px;height:60px;" title="点击更新图片!" src="/mainsteam-stm-webapp/platform/file/getFileInputStream.htm?fileId='+tdData.value+'"></img>');
									}else{
										tdDom.find("."+tdData.id).append('<img id="tvImg" style="width:80px;height:60px;" title="点击更新图片!" src=""></img>');
									}
									var user = oc.index.getUser();
//									tdDom.append('<p id="tvImgP">点击上传图片!</p>');
									var img = tdDom.find('#tvImg');
									img.on('click',function(){
										if(!user.systemUser){
											alert("更新图片,请联系系统管理员!");
											return;
										}
										
										var dlg = $('<div/>');
										var fileUpload = $('<form id="fileForm" class="oc-form col1 h-pad-mar oc-table-ocformbg">');
										var fileTable = $('<table style="width:100%;" class="octable-pop"/></table>');
										var fileTableTr3=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" align style="width:30%"><label>上传图片：</label></td><td><input name="file" type="text" required="required" /></td></tr>');

										fileTable.append(fileTableTr3);
										fileUpload.append(fileTable);
										dlg.append(fileUpload);
										
										var form = oc.ui.form({
											selector:dlg.find('#fileForm'),
											filebox:[{selector:'[name=file]'}]
										});
										
										var buttons  = [{
										    	text: '确定',
										    	iconCls:"icon-ok",
										    	handler:function(){
										    		var filebox=form.filebox[0];
													var filePath = filebox.selector.filebox('getValue');
													var fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
													
													if(fileName.match(/([^\u0000-\u00FF])/g)){
														alert('文件名请不要有中文字符!');
														return ;
													}
													if(fileName && fileName.length>99){
														alert('文件名过长,请不要超过100个字符!');
														return ;
													}
													
													
										    		if(filePath){
								    						form.jq.ajaxSubmit($.extend({}, filebox,{
																url : oc.resource.getUrl('portal/resource/resourceDetailInfo/resouceImgUpload.htm'),
																data : {instanceId : that.cfg.instanceId},
																type : 'POST',
																iframe : true,
																timeout: 300000,
																async:false,
																dataType:'json',
																success : function(data) {
																	
																	if(data>0){
//																		tdDom.find('#tvImgP').hide();
																		img.attr('src','/mainsteam-stm-webapp/platform/file/getFileInputStream.htm?fileId='+data);
																		alert("上传成功！");
																	}else{
																		alert("上传失败！");
																	}
																	dlg.window("destroy");
																},
																error : function(XMLHttpRequest, textStatus, errorThrown) {
																	alert('文件上传失败！');
																	log(XMLHttpRequest, textStatus, errorThrown);
																}
															}));
										    			}
										    	}
										    },{
										    	text: '取消',
										    	iconCls:"icon-cancel",
										    	handler:function(){
										    		dlg.dialog('destroy');
										    	}
										    }];
										
										dlg.dialog({
										    title: '台标上传',
										    width : 350,
											height : 112,
											buttons:buttons,
										    onLoad:function(){}
										});
									});
								}else{
									if(tdData.value!=undefined && tdData.value!=null ){
										tdData.value=tdData.value.replace("null","--")
									}
									var value="";
									if(tdData.value != undefined && tdData.value != null && tdData.value.length > 12){

										value = tdData.value.substring(0, 12) + "...";
									}else{
										value=tdData.value;
									}
								//tdData.value=""Red Hat Enterprise Linux Server release 5.7 (Tikanga)"";
									tdData.value=tdData.value==null || tdData.value=="null" ?"":tdData.value.replace("\"","");//.replace(/(\")/g, "\\"")
								html='<span title="'+tdData.value+'">'+tdData.value+'</span>';
								tdDom.find("."+tdData.id).append(html);
							}
		
		},
		createText : function(tdDom, tdData,tdSize){

			var that = this;
			var user = undefined;
			//var tdDom = this.createPanel(td2Dom, tdData, tdSize);
			//只有在显示名称的时候才去调用getUser
			if(tdData.id == 'showname'){			
				user = oc.index.getUser();			
			}	
			if(tdData.id == 'statusLight'){
				
				$(".titleshow").addClass(this.returnStatusLight(tdData.value, 'mainInstance'));
			}
			else if(tdData.id == 'liablePerson')
			{
			oc.util.ajax({
					url:oc.resource.getUrl('system/login/getUserByInstanceId.htm'),		
					data:{instanceId: this.cfg.instanceId},
					success:function(d){
						var liableUser=d.data;
						if(liableUser.name!=null && liableUser.name != undefined ){		
						var content=liableUser.name;
						if(liableUser.mobile==null){
							liableUser.mobile="";
						}
						if(liableUser.email==null){
							liableUser.email="";
						}					
						var tdHtml = liableUser.name;
						if(liableUser.name.length > 4){
							tdHtml = liableUser.name.substring(0, 5)+ "..."+liableUser.mobile;
							var content = $("<div/>").css({
							
							}).html(tdHtml).addClass('values').attr("title",liableUser.name);
							tdDom.append(content);	
						}
						else{
							tdDom.html(tdHtml);	
						}			
						//	tdDom.html(content);		
							
						    		
							
						}
					}
				});			
			}else if(tdData.id == 'ip'){
			var IP = $("<div/>").addClass("titles");
			var sel=$("<select/>");
					IP.append(sel ).removeClass("titles").addClass("values");
				tdDom.append(IP);
				oc.ui.combobox({
					selector : sel,
					placeholder : false,
					data : tdData.value,
					value : tdData.value[0].id
				});
				
			}else if(tdData.id == 'showname'/* && typeof(that.cfg.callback) != 'undefined'*/
						&& (user.domainUser || user.systemUser)){
							
				that.showName = tdData.value;
				var outEditNode = $("<div/>").addClass("values");//.width('120px').height('100%');
				var editBtn = $("<span/>").addClass('uodatethetext fa fa-edit').attr('title', '编辑名称');//.css('float','right')
				var editNode = $("<input name='editName' type='text'>").css('float','left').width('100px');
				editNode.validatebox({
					validType : 'maxLength[30]'
				});
				var showNode = $("<div name='showName' />").addClass('generalShowName')
								.attr('title', that.showName).html(that.showName.htmlspecialchars());//.width('100px');//.css('float','left')
				outEditNode.append(editNode.hide()).append(showNode).append(editBtn);
				tdDom.append(outEditNode);
				editBtn.on('click', function() {
					if (that.flag) {
						editNode.val(that.showName).show();
						showNode.html('').hide();
						editBtn.attr('title', '确认名称修改').removeClass().addClass('uodatethetextok fa fa-check');
						that.flag = false;
					} else {
						if(editNode.validatebox('isValid')){
							var newShowName = editNode.val();
							if(newShowName != undefined && newShowName.replace(/(^\s*)(\s*$)/g, '') != ''){
								oc.util.ajax({
									
									url : oc.resource.getUrl("portal/resource/discoverResource/updateInstanceName.htm"),
									data : {newInstanceName:newShowName,instanceId:that.cfg.instanceId},
									successMsg:null,
									success : function(json){
										if(json.code == 200){
											if(json.data == 0){
												alert('修改显示名称失败！');
											}else if(json.data == 1){
												alert('修改显示名称成功！');
												showNode.html(newShowName.htmlspecialchars()).attr('title', newShowName).show();
												that.showName = newShowName;
												editNode.val('').hide();
												editBtn.attr('title', '编辑名称').removeClass().addClass('uodatethetext  fa fa-edit');
												that.flag = true;
												oc.module.resmanagement.resdeatilinfo.updateDlgTitle(newShowName);
											}else{
												alert('资源显示名称重复');
											}
										}else{
											alert('修改显示名称失败！');
										}
									}
								});
							}
						}
					}
				});
			}else if(tdData.id == 'availability'){
				var content = '可用';
				if(tdData.value == 'CRITICAL' || tdData.value == 'CRITICAL_NOTHING'){
					content = '不可用';
				}else if(tdData.value == 'UNKOWN'){
//					content = '未知';
				}
				tdDom.html(content);				
			}else if(tdData.id == 'resourceImg'){
				if(tdData.value){
					tdDom.append('<img id="tvImg" style="width:80px;height:60px;" title="点击更新图片!" src="/mainsteam-stm-webapp/platform/file/getFileInputStream.htm?fileId='+tdData.value+'"></img>');
				}else{
					tdDom.append('<img id="tvImg" style="width:80px;height:60px;" title="点击更新图片!" src=""></img>');
				}
				var user = oc.index.getUser();
//				tdDom.append('<p id="tvImgP">点击上传图片!</p>');
				var img = tdDom.find('#tvImg');
				img.on('click',function(){
					if(!user.systemUser){
						alert("更新图片,请联系系统管理员!");
						return;
					}
					
					var dlg = $('<div/>');
					var fileUpload = $('<form id="fileForm" class="oc-form col1 h-pad-mar oc-table-ocformbg">');
					var fileTable = $('<table style="width:100%;" class="octable-pop"/></table>');
					var fileTableTr3=$('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" align style="width:30%"><label>上传图片：</label></td><td><input name="file" type="text" required="required" /></td></tr>');

					fileTable.append(fileTableTr3);
					fileUpload.append(fileTable);
					dlg.append(fileUpload);
					
					var form = oc.ui.form({
						selector:dlg.find('#fileForm'),
						filebox:[{selector:'[name=file]'}]
					});
					
					var buttons  = [{
					    	text: '确定',
					    	iconCls:"icon-ok",
					    	handler:function(){
					    		var filebox=form.filebox[0];
								var filePath = filebox.selector.filebox('getValue');
								var fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
								
								if(fileName.match(/([^\u0000-\u00FF])/g)){
									alert('文件名请不要有中文字符!');
									return ;
								}
								if(fileName && fileName.length>99){
									alert('文件名过长,请不要超过100个字符!');
									return ;
								}
								
								
					    		if(filePath){
			    						form.jq.ajaxSubmit($.extend({}, filebox,{
											url : oc.resource.getUrl('portal/resource/resourceDetailInfo/resouceImgUpload.htm'),
											data : {instanceId : that.cfg.instanceId},
											type : 'POST',
											iframe : true,
											timeout: 300000,
											async:false,
											dataType:'json',
											success : function(data) {
												
												if(data>0){
//													tdDom.find('#tvImgP').hide();
													img.attr('src','/mainsteam-stm-webapp/platform/file/getFileInputStream.htm?fileId='+data);
													alert("上传成功！");
												}else{
													alert("上传失败！");
												}
												dlg.window("destroy");
											},
											error : function(XMLHttpRequest, textStatus, errorThrown) {
												alert('文件上传失败！');
												log(XMLHttpRequest, textStatus, errorThrown);
											}
										}));
					    			}
					    	}
					    },{
					    	text: '取消',
					    	iconCls:"icon-cancel",
					    	handler:function(){
					    		dlg.dialog('destroy');
					    	}
					    }];
					
					dlg.dialog({
					    title: '台标上传',
					    width : 350,
						height : 112,
						buttons:buttons,
					    onLoad:function(){}
					});
				});
			}else{
				// 处理字符串过长的问题
				var tdHtml = tdData.value;

				if(tdData.value != undefined && tdData.value != null && tdData.value.length > 17){

					tdHtml = tdData.value.substring(0, 14) + "...";
				}
				var content = $("<div/>").css({
				
				}).html(tdData.value == undefined || tdData.value == null ? '' : tdData.companType==null?tdHtml:tdHtml==""?'&nbsp;':tdHtml)
				.attr("title", tdData.value == undefined || tdData.value == null ? '' : tdData.value);
			 var brhtml="<br/>";

			 var tr=$("<tr/>");
				if(tdData.companType!=null || tdData.companType=='statusLight'){
			 }
			if(tdData.companType==null || tdData.companType=='statusLight'){
					content.addClass("titles");
			 }else{
				 	content.addClass("values");
			 }
				tdDom.append(content);

			}
		
		},
		createtabPanel: function(tdDom,tdData,tdSize){
			var tdDatanew= new Array();
			
			var tablePanels = this.createPanel(tdDom, tdData, 2);
			
			for(var i=0;i<tdData.length;i++){

				var name_cls="."+tdData[i].id;
				if(tdData[i].id==null && tdData[i].metrics=='none'){
					name_cls=".tabs";
				}
				var currentDiv=tablePanels.find(name_cls).css('height','80%');
				 if(i==1 && tdData[0].childtype=='other'){
						currentDiv.parent().html("<div class='otherTabTip'>抱歉，没有可展示的数据！</div>");
				 }
				 if(tdData[i].childtype=='environmentPanel'){
					 this.createEnvironmentPanel(currentDiv,tdData[i]);
				 }
				if(tdData[i].childtype=='temp'){//温度计
								
					this.createTemp(currentDiv[0],tdData[i]);
						currentDiv.before("<div style='text-align:left;padding:0px 10px;'>"+tdData[i].title+"</div>");	
				}else if(tdData[i].childtype=='mulitgraph'){
				
					this.createGraph(currentDiv,tdData[i],tdSize,true);
				}else if(tdData[i].childtype=='alarm'){

				//业务详情整体页面与资源详情不一致，因此需要做特殊处理
					var alarmPanelWidth=411;
					var alarmPanelHeight=243;
				
				    if($("#mainInfolayoutCenter").length!=0 /*&& (this.cfg.resourceType=="Hardware" || this.cfg.resourceType=="Storage")*/){
				    	
				    	if(tdData[0].type=="tabPanel"){
				    		alarmPanelWidth=388;
				    	}
				    	if(this.cfg.resourceType=='Storage' || this.cfg.resourceType=='DiskArray' ){
				    		alarmPanelHeight=259;
				    	}
				    	
				    	
				    }else{
				    	if(this.cfg.resourceType=='Storage' || this.cfg.resourceType=='DiskArray'){
				    		alarmPanelHeight=259;
				    	}
				    	
				    }
					tdDom.append("<div class='alarmpanel layout-td-panel-bg' style='float:left;height:"+alarmPanelHeight+"px;width:"+alarmPanelWidth+"px;'></div>");

		
					var alarmdiv=$(".alarmpanel");
					
					alarmdiv.append("<div style='padding: 3px 11px;'>告警</div>");
				
					this.createAlarm(alarmdiv,tdData[i],tdSize,1);
				}else if(tdData[i].childtype=='gauge'){
				this.createsinglegauge(currentDiv, tdData[i],tdData.length,2);
					
				}else if(tdData[i].metrics=='none'){//组件信息
					this.createTabs(currentDiv,tdData[i],1)
				}
			}
		/*	var AmbientTemps=tablePanels.find(".AmbientTemps");
			var AmbientTemp=tablePanels.find(".AmbientTemp");
			this.createTemp(AmbientTemps[0],tdData[0]);
			this.createGraph(AmbientTemp,tdData[1],tdSize);
			//this.createGraph(AmbientTemp[0],tdData,tdSize);*/
			/*	var tablePanels = this.createPanel(tdDom, tdData, tdSize);
					var AmbientTemp=tablePanels.find(".AmbientTemp");
						var cpuline=tablePanels.find(".cpuline");
				//	this.createTemp(AmbientTemp[0],tdData);
					this.createGraph(cpuline[0],tdData[1],tdSize);*/
		},
createTemp : function(div,tdData){

var series = [];
var data = [tdData.value];
var status=tdData.status;
var color = this.returnColorByStatus(status);
var countries = [tdData.title];
var titles = [];

    var x = 7 + 1 / 7 * 100 + '%';

    titles.push({
        text: '',
        textAlign: 'center',
        left: '30%',
        bottom: 10,
        padding: 0,
        textStyle: {
            color: Highcharts.theme.tempTextcolor,
            fontSize: 20,
            fontWeight: 'normal'
        }
    });

    series.push({
        animation: true,
        waveAnimation: true,

        color: [color],
        center: ['50%', '50%'],
        radius: '63%',
        type: 'liquidFill',
        shape: 'path://M229.844,151.547v-166.75c0-11.92-9.662-21.582-21.58-21.582s-21.581,9.662-21.581,21.582v166.75c-9.088,6.654-14.993,17.397-14.993,29.524c0,20.2,16.374,36.575,36.574,36.575c20.199,0,36.574-16.375,36.574-36.575C244.838,168.944,238.932,158.201,229.844,151.547z',
        outline: {
            show: false
        },
        amplitude: 0,
        waveLength: '20%',
        backgroundStyle: {
            borderColor: Highcharts.theme.tempborderColor, 
            borderWidth:3
        },
        data: [{
            // -60 到 100 度
			name:'',//tdData.title,
            value: tdData.value==''?'':(tdData.value/100), 
            rawValue: tdData.value

        }],
        itemStyle: {
            normal: {
                shadowBlur: 0
            }
        },
        label: {
            normal: {
                position: 'insideBottom',
                distance: 10,
                textStyle: {
                    color:  Highcharts.theme.tempTextcolor, 
                    fontSize: 12
                },
                formatter: function(item) {
            
                	if(item.data.rawValue==undefined || item.data.rawValue==''){
                		return '--';
                	}else{
                		return item.data.rawValue + '°';
                	}
                	//(item.data.value=='' || item.data.value=='NaN' ) ?'--': ' ' + item.data.value + '°'
                   // return (item.data.value=='' || item.data.value=='NaN' ) ?'--': ' ' + item.data.value + '°';
                }
            }
        }
    });
    try{
    	var myChart = echarts.init(div);
    	myChart.setOption({
    	    title: titles,
    	    series: series,
    	    tooltip: {
    	        show: true
    	    }
    	});	
    }catch(e){
    }

/*	var myChart = echarts.init(div[0]);
myChart.setOption({
    title: titles,
    series: series
});*/
		},
		createmulitpie: function(tdDom,tdData,tdSize){
	
			var pie = this.createPanel(tdDom, tdData, tdSize);
			var that = this;

			var availability=  this.cfg.availability;
			for(var i=0;i<tdData.length;i++){
	
				var pieCurrent=pie.find(".appCpuRate");
			//	var color="yellow";
				if(tdData[i].id=="appMemRate"){
					pieCurrent=pie.find(".appMemRate");
				//	color="green";
				}
			//	pieCurrent.css("margin","-30px 0px");
				pieCurrent.css("height","75%");
				var color = this.returnColorByStatus(tdData[i].status);
				this.initPieChart(pieCurrent[0],tdData[i],availability,color,pieCurrent);
			
				
				
			}
		},
		initPieChart : function(div,data,availability,color,pieCurrent){
			
		//	data.name="Websphere Application Server CPU利用率";
			var showName=data.name.length>9?data.name.substring(0,9)+"...":data.name;
			var percent = data.value!=null && data.value != undefined ?(data.value == 'N/A' ? 0 : data.value):0 ;// != null && data.value != undefined && availability!='CRITICAL_NOTHING' && availability!="CRITICAL"
    			//? (data.value == 'N/A' ? 'N/A' : data.value + data.unit) : '--';//data.value;
    		//	percent=0.6;
	
			var percentStr=data.value!=null && data.value != undefined ?(data.value == 'N/A' ? 'N/A' : data.value+"%"):'--' ;

			var option = {
				    tooltip: {
				        trigger: 'item',
				        formatter: function(params, ticket, callback) {
				            var res = '';//params.seriesName;
				         //   var  value=params.data.lable.emphasis.formatter;
				            res +=data.name + ' : ' + percentStr;
				            return res;
				        }
				    },
				    title: {
				        text: '',
				        subtext: '',
				        x: 'center',
				        y: '45%',
				        textStyle: {
//				            color: Highcharts.theme.pieTextColor,
				            fontSize: 12
				        },
				        subtextStyle: {
				            color: ' ',
				            fontSize: 12
				        }
				    },
				    grid: {
				        bottom: 50
				    },
				    xAxis: [{
				        type: 'category',
				        axisLine: {
				            show: false
				        },
				        axisTick: {
				            show: false
				        },
				        axisLabel: {
				            interval: 0,
				            textStyle: {
	                            color: Highcharts.theme.pieaxisLabelColor,
	                            fontSize:'12',
	                            fontFamily: '微软雅黑'
	                            
	                        }
				        },
				        data: [showName]
				    }],
				    yAxis: [{
				        show: false
				    }],
				    series: [{
				        name: showName,
				        animation:false,
				        hoverAnimation:false,
				        animation:false,
				        center: [
				                 '50.0%',
				                 '40%'
				             ],
				         radius: ['35%', '42%'],
				        type: 'pie',
				        labelLine: {
				            normal: {
				                show: false
				            }
				        },
				        data: [{
				            value: percent,
				            name: '占用率：',
				            label: {
				                normal: {
				                    formatter:(percentStr!='--' && percentStr!='N/A')?percentStr :percentStr,
				                    position: 'center',
				                    show: true,
				                    textStyle: {
				                        fontSize: '12',
				                        color: Highcharts.theme.pieTextColor,
				                        letterSpacing:'1px'
				                    }
				                }
				            },
				            itemStyle: {
					            normal: {
					                color: color/*,
					                shadowBlur: 10,
					                shadowColor: color*/
					            }/*,
					            emphasis:{
					            	 shadowColor: color
					            }*/
					        }
				        }, {
				            value: 100-percent,
				            name: '',
				            tooltip: {
				                show: false
				            },
				            itemStyle: {
				                normal: {
				                    color: Highcharts.theme.pieitemColor
				                }
				            },
				            hoverAnimation: false
				        }]
				    }]
				};
		
		try{
			var myChart = echarts.init(div);
			myChart.setOption(option);	
		//	pieCurrent.append("<div align='center' style='color:#9bc5f7;margin:-8px 0px;' title='"+data.name+"'>"+showName+"</div>");
		}catch(e){
		//	pieCurrent.append("<div><div>")
		}
			
			
		},
		handleForMainValues:function(data){},
		handleSourceData : function(data,metricDataType){	
			var seriesDatas = new Array();
		var xAxisTemp ;
		var tooltip ;
		var yAxisTemp ; 
		var yAxisMaxValue = 0;
		var yAxisMinValue = 0;
		var metricUnitName = "";
		
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

		if(data.data.dataMap){
			var that = this;
			
			var dataMap = data.data.dataMap;
			
				var metricId = this.meticId; 
				var metricItemData = dataMap["merticValue"];
				var dDouArr = new Array();
				var seriesTemp = {data:dDouArr};
				seriesDatas.push(seriesTemp);
				
				if(metricItemData){
					for(var dDouStrArr = 0;dDouStrArr<metricItemData.length;dDouStrArr++){
						var metricItemDataObj = metricItemData[dDouStrArr];
						if("null" == metricItemDataObj['data']){
							var date = new Date(parseInt(metricItemDataObj['collectTime']));
							var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate() , parseInt(date.getHours()),parseInt(date.getMinutes()));//, parseInt(date.getSeconds())
							
							dDouArr.push([dateUtc,null]);
						}else{
							var date = new Date(parseInt(metricItemDataObj['collectTime']));
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
		xAxisTemp = {
				type: 'datetime',
	            dateTimeLabelFormats: { // don't display the dummy year
	            	minute: '%H:%M',
        			hour: '%H:%M',
        			day: '%b-%e',
        			week: '%b-%e',
        			month: '%y-%b'
	            },
				tickmarkPlacement: 'on',
				   gridLineColor:Highcharts.theme.areasplinegridLineColor,
		            lineColor:Highcharts.theme.areasplinelineColor,
		            gridLineWidth : 1,
		            labels: {
		                enabled : true,
		                //	                	step : 1,
		                staggerLines : 1,
		                style:{
		            		color:Highcharts.theme.areasplinelabelsColor
		            	}
		            },
		            tickWidth : 1,
		         //   tickInterval : 3,
		            minorTickWidth : 0,
		            tickColor:Highcharts.theme.areasplinetickColor,
		            tickmarkPlacement: 'on',
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
		yAxisTemp = {
						max:yAxisMaxValue,
			        	min : yAxisMinValue,
			           title: {
			            	text: ''
			            },
			            lineColor:Highcharts.theme.areasplinelineColor,
			            gridLineColor:Highcharts.theme.areasplinegridLineColor,
			            gridLineWidth:0,
			            tickWidth:1,
			            lineWidth:1,
			            tickColor:Highcharts.theme.areasplinetickColor,
			            labels:{
			            	style:{
			            		color:Highcharts.theme.areasplinelabelsColor
			            	},
			            	enabled:true //设置刻度是否显示
			         ,   staggerLines : 1,
			         
			            	},
			            	enabled:true
			        };
			
			switch(metricDataType){
			case "min":
				tooltip = {
					enabled: true,
					  useHTML:true,
		            formatter: function() {

		            	//highchart BUG this.x该时间比当前时间快8小时
		            	var dateTool = new Date(this.x-8*3600*1000);
		            	var valueNum = this.y;
		            	var alertStr = unitTransform(valueNum,data.data.metricUnitName);
		            	var month=dateTool.getMonth()+1;
		            	var date=dateTool.getDate();
		            	if(month<10){
		            		month="0"+month;
		            	}
		            	if(date<10){
		            		date="0"+date;
		            	}
		            	return "<div>"+(month+'-'+date)+' '+(dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+"&nbsp;&nbsp;<span style='font-size:16px;'>"+alertStr+"</span></div>";
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
			
			return {xAxisTemp : xAxisTemp,
						   yAxisTemp : yAxisTemp,
						   tooltip : tooltip,
						   seriesData : seriesDatas};
						   },
		handlegraphData:function(metricId,tdData){
			var that=this;
			var Datas= new Array();
			var dateEnd = new Date();
	  		var dateStart = new Date(dateEnd.getTime()-4*60*1000*60);
	  		var dateEndStr = formatDate(dateEnd);
	  		var dateStartStr = formatDate(dateStart);
	  		var historyDatas=[];
	  		var historyDataColTim=[];
	  		var unit="%";
	  		var dataObj=new Array();
	  		oc.util.ajax({
				url : oc.resource.getUrl("portal/resource/metricChartsData/getSpecialMetricData.htm"),
				data : {dateStart : dateStartStr ,dateEnd : dateEndStr,instanceId : that.cfg.instanceId,metricId:metricId,metricDataType:"min"},
				timeout : null,
				async:false,
				successMsg:null,
				success : function(d){
					dataObj=that.handleSourceData(d,"min");
					var memRates=d.data.dataMap.merticValue;
					if(that.cfg.resourceType=='UniversalModel'){
						unit=tdData.unit;
					}else{
						unit=d.data.metricUnitName;
					}
				
						historyData=memRates;
					if(memRates!=undefined){
						for(var i=0;i<memRates.length;i++){
					
							var str ="";// date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
							if(memRates[i].collectTime!=null){
					
								var value=memRates[i].collectTime;
								var dateTool = new Date((value-3600*1000)+1000*60*60);
				        	var	valueStr =/*dateTool.getDate()+'-'+*/dateTool.getHours()+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes());
			
							}
					
							
							if(memRates[i].data!="null"){
								historyDatas[i]=memRates[i].data*1;
								historyDataColTim[i]=value;//valueStr;
								
							}else{
								//historyDatas[i]=0.0;
								historyDatas[i]=null;
								historyDataColTim[i]=value;//valueStr;
							}
							
						}
				}
						Datas.push(historyDatas);
						Datas.push(historyDataColTim);
						Datas.push(unit);
					//	tdData.historyData=historyDatas;
					//	tdData.historyDataColTim=historyDataColTim;
				}
				});
			
			return dataObj;// Datas;
		},
		createmultigraph : function(tdDom,tdData,tdSize){
			var unit="%";var index=tdSize;
			var graphCurrent='';
			if(tdData.id=="cpuline" || index==0){
				graphCurrent=tdDom.find(".cpuline").css("width","100%").css("height","30%").removeClass("gaugeShow");
			}
			else if(tdData.id=="memline" || index==1){

				graphCurrent=tdDom.find(".memline").css("width","100%").css("height","30%").removeClass("gaugeShow");

			}else if(tdData.id=="otherline" || index==2){
				graphCurrent=tdDom.find(".otherline").css("width","100%").css("height","30%").removeClass("gaugeShow");
			}
			var that = this;
			var metricId="";
	
			var titleshow=tdData.title;
			if(tdData.type=='others'){//不显示
				
			}else{
				if(this.cfg.resourceType=='UniversalModel'){//通用模型动态读取显示的性能指标
					titleshow=tdData.text;
					metricId=tdData.id;
					
				}else{
					if(tdData.id=='AmbientTemp' || tdData.id=='DiskUsed' || tdData.id=='DiskUsagePercentage' || tdData.id=='NetworkUsed' || tdData.id=='discardsRate'  || tdData.id=='physicalRate'){

						metricId=tdData.id;
					}else if(tdData.id=='throughput'){

						metricId=tdData.id;
					}else{

						metricId=tdData.metric;
					}	
				}
			}
			var Datas=that.handlegraphData(metricId,tdData);


			var dataid=tdData.metric;
		
			var	step=3;
			//var step=historyDataColTim.length>2?Math.floor(historyDataColTim.length / 4):1;
//					step = tdData.historyDataColTim.length > 4 ? Math.floor(tdData.historyDataColTim.length / 4) : null;
			unit=(unit==null || unit=="")?"%":unit;
			 Highcharts.setOptions({
			        lang: {
			            shortMonths : ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
			            weekdays: ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
			        }
		    });
		var legend = {enabled:false};
		graphCurrent.highcharts({
	        chart: {
	            type: 'areaspline'
	        },
	        title: {
	            text: ''
	        },
	        subtitle: {
                text: titleshow+"(单位:"+unit+")",
                style:{
            		color:Highcharts.theme.areasplinegsubTitleColor
            	}
            },
	        xAxis:Datas.xAxisTemp,
	        yAxis: Datas.yAxisTemp,
	        colors:Highcharts.theme.areasplineColor,
			credits: {
		    	enabled: false
		    },
		    exporting : {
		    	enabled: false
		    },
		    legend: legend,
		    plotOptions : {
		    	series: {
		    		 animation: false,   
	                marker: {
	                    enabled: true,
	                    radius: 1
	                }
	            },

	            areaspline: {
	                fillOpacity: 0.1,
	                lineWidth:2,
	                marker: {
	                    enabled: true,
	                    radius: 1
	                },
	                fillColor: {
                        linearGradient: {
                            x1: 0,
                            y1: 0,
                            x2: 0,
                            y2: 1
                        },
                        
                        stops: Highcharts.theme.areasplineStopColor
                    
						 }
	            }
		    },
		    tooltip: Datas.tooltip,
	        series: Datas.seriesData
		},function(chart){
			//回调
//				console.log(chart);
		});
},
		createsinglegauge : function(currentDiv, tdData, tdSize,num){

		var value=tdData.value;

		//currentDiv.css('height','60%');
		//var value=tdData.value!=null && tdData.value != undefined ?(tdData.value == 'N/A' ? 'N/A' : tdData.value+"%"): '--' ;
		if(tdData.value!=null && tdData.value!=undefined){
			value=value.replace('%','');
			value=value == 'N/A' ? 'N/A' : Number(value).toFixed(2);
			
		}else{
			value='--';
		}
		value=Number(value).toFixed(2)-0;

			 var alarm=tdData.isAlarm;
			 var color=[];
			 if(alarm==true){//告警
				 var currentColor=Highcharts.theme.resourceDetailMetricColors[0];
					switch(tdData.status){
					case 'CRITICAL':
						currentColor = Highcharts.theme.resourceDetailMetricColors[3];
						break;
					case 'SERIOUS':
						currentColor = Highcharts.theme.resourceDetailMetricColors[2];
						break;
					case 'WARN':
						currentColor = Highcharts.theme.resourceDetailMetricColors[1];
						break;
					case 'NORMAL':
					case 'NORMAL_NOTHING':
						currentColor = Highcharts.theme.resourceDetailMetricColors[0];
						break;
					default :
						currentColor = Highcharts.theme.resourceDetailMetricColors[0];
						break;
					}
			 color=[[0,currentColor],[1,Highcharts.theme.resourceDetailMetricColors[2]]];
			 }else{
				 color=[[0,Highcharts.theme.resourceDetailMetricColors[0]],[1,Highcharts.theme.resourceDetailMetricColors[0]]]; 
			 }
		//var value=tdData.value!=null && tdData.value != undefined ?(tdData.value == 'N/A' ? 'N/A' : Number(tdData.value)): '--' ;
	//	value=1.00;
			if(tdData.isAlarm == false){
				tdData.status = 'NORMAL';
			}
		var option = {
			    tooltip : {
			    	show:false,
			        formatter: "{a} <br/>{b} : {c}%"
			    },
			    toolbox: {
			        show : false
			      
			    },
			    series : [
			        {
			            name:'',
			            type: 'gauge',
			            center : ['50%', '50%'],    // 默认全局居中
			            radius : '100%',
			            startAngle:220,
			            endAngle:-40,
			            min: 0,                     // 最小值
			            max: 100,                   // 最大值
			            precision: 0,               // 小数精度，默认为0，无小数点
			            splitNumber: 10,             // 分割段数，默认为5
			            axisLine: {            // 坐标轴线
			                show: true,        // 默认显示，属性show控制显示与否
			                lineStyle: {       // 属性lineStyle控制线条样式
			                	  color:color,// [[one, tdData.isAlarm==false?'#02bf00':'#02bf00'],[two, tdData.isAlarm==false?'#02bf00':'#edb805'],[1, tdData.isAlarm==false?'#02bf00':'#ff7d03']], 
			                    width: 8
			                }
			            },
			            axisTick: {            // 坐标轴小标记
			                show: true,        // 属性show控制显示与否，默认不显示
			                splitNumber: 1,    // 每份split细分多少段
			                length :8,         // 属性length控制线长
			                lineStyle: {       // 属性lineStyle控制线条样式
			                    width: 1,
			                    type: 'solid'
			                }
			            },
			            axisLabel: {           // 坐标轴文本标签，详见axis.axisLabel
			                show: true,
			                formatter: function(v){
			                    switch (v+''){
			                    case '0': return '0';
		                        case '20': return '20';
		                        case '40': return '40';
		                        case '60': return '60';
		                        case '80': return '80';
		                        case '100': return '100';
		                        default: return '';
			                    }
			                },
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
			                }
			            },
			            splitLine: {           // 分隔线
			                show: true,        // 默认显示，属性show控制显示与否
			                length :8,         // 属性length控制线长
			                lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
			                    width: 2,
			                    type: 'solid'
			                }
			            },
			            pointer : {
			                length : '80%',
			                width : 8,
			                color : 'auto'
			            },
			            title : {
			                show : true,
			                offsetCenter: ['10%', 60],       // x, y，单位px
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
			                    color:  Highcharts.theme.gaugetextColor,
			                    fontSize : 12
			                }
			            },
			            detail : {
			                show : true,
//			                backgroundColor: 'rgba(0,0,0,0)',
			                borderWidth: 0,
			                width: 100,
			                height: 40,
			                offsetCenter: ['0%', 30],       // x, y，单位px
			                formatter:function(a,b){
								
								return tdData.value!=null && tdData.value != undefined ?(tdData.value == 'N/A' ? 'N/A' : value+"%"): '--' ;
							},
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
			                    color: 'auto',
			                    fontSize : 12
			                }
			            },
			            data:[{value: value, name: tdData.title}]
			        }
			    ]
			};

				if(num==1){
					var tdDoms = this.createPanel(currentDiv, tdData, tdSize);
					tdDoms.css('margin','0px 80px');
					tdDoms.width("140px");
					tdDoms.height("161px");
					var myChart = echarts.init(tdDoms[0]);
				myChart.setOption(option);
				}else{
					//currentDiv.css('margin','0px 80px');
				//	currentDiv.width("120px");
				//	currentDiv.width("141px");
					var myChart = echarts.init(currentDiv[0]);
					myChart.setOption(option);
				}
				
			
			
	
			
		
		},
		createmultigauge : function(tdDoms, tdData, tdSize){
		//var tdDoms = this.createPanel(tdDom, tdData, tdSize);
		for(var i=0;i<tdData.length;i++){
			if(tdData[i].isAlarm == false){
				tdData[i].status = 'NORMAL';
			}
		var value=tdData[i].value!=null && tdData[i].value != undefined ?(tdData[i].value == 'N/A' ? 'N/A' : Number(tdData[i].value)): '--' ;
		var unit='%';
		 if(tdData[i].unit!=undefined || tdData[i].unit!=''){
			 unit=tdData[i].unit;
		 }
		 var alarm=tdData[i].isAlarm;
		 var color=[];

		 if(alarm==true){//告警
			 var currentColor=Highcharts.theme.resourceDetailMetricColors[0];
				switch(tdData[i].status){
				case 'CRITICAL':
					currentColor = Highcharts.theme.resourceDetailMetricColors[3];
					break;
				case 'SERIOUS':
					currentColor = Highcharts.theme.resourceDetailMetricColors[2];
					break;
				case 'WARN':
					currentColor = Highcharts.theme.resourceDetailMetricColors[1];
					break;
				case 'NORMAL':
				case 'NORMAL_NOTHING':
					currentColor = Highcharts.theme.resourceDetailMetricColors[0];
					break;
				default :
					currentColor = Highcharts.theme.resourceDetailMetricColors[0];
					break;
				}
		 color=[[0,currentColor],[1,currentColor]];
		 }else{
			 color=[[0,Highcharts.theme.resourceDetailMetricColors[0]],[1,Highcharts.theme.resourceDetailMetricColors[0]]]; 
		 
		 }
		var option = {
			    tooltip : {
			    	show:false,
			        formatter: "{a} <br/>{b} : {c}"+unit
			    },
			    toolbox: {
			        show : false
			      
			    },
			    series : [
			        {
			            name:'',
			            type: 'gauge',
			            center : ['50%', '50%'],    // 默认全局居中
			            radius : '100%',
			            startAngle:220,
			            endAngle:-40,
			            min: 0,                     // 最小值
			            max: 100,                   // 最大值
			            precision: 0,               // 小数精度，默认为0，无小数点
			            splitNumber: 10,             // 分割段数，默认为5
			            axisLine: {            // 坐标轴线
			                show: true,        // 默认显示，属性show控制显示与否
			                lineStyle: {       // 属性lineStyle控制线条样式
			                    color:color,// [[one, tdData[i].isAlarm==false?'#02bf00':'#02bf00'],[two, tdData[i].isAlarm==false?'#02bf00':'#edb805'],[1, tdData[i].isAlarm==false?'#02bf00':'#ff7d03']], 
			                    width: 8
			                }
			            },
			            axisTick: {            // 坐标轴小标记
			                show: true,        // 属性show控制显示与否，默认不显示
			                splitNumber: 1,    // 每份split细分多少段
			                length :8,         // 属性length控制线长
			                lineStyle: {       // 属性lineStyle控制线条样式
//			                    color: Highcharts.theme.gaugelineColor,
			                    width: 1,
			                    type: 'solid'
			                }
			            },
			            axisLabel: {           // 坐标轴文本标签，详见axis.axisLabel
			                show: true,
			                formatter: function(v){
			                	switch (v+''){
		                    case '0': return '0';
	                        case '20': return '20';
	                        case '40': return '40';
	                        case '60': return '60';
	                        case '80': return '80';
	                        case '100': return '100';
	                        default: return '';
		                    }
			                },
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
//			                    color: Highcharts.theme.gaugetextColor,
			                    fontSize:12
			                }
			            },
			            splitLine: {           // 分隔线
			                show: true,        // 默认显示，属性show控制显示与否
			                length :8,         // 属性length控制线长
			                lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
//			                    color: Highcharts.theme.gaugelineColor,
			                    width: 2,
			                    type: 'solid'
			                }
			            },
			            pointer : {
			                length : '60%',
			                width : 3,
			                color : 'auto'
			            },
			            title : { 
			                show : true, 
			                offsetCenter: ['10%',54],       // x, y，单位px
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
			                    color: Highcharts.theme.gaugetextColor,
			                    fontSize : 12
			                }
			            },
			            detail : {
			                show : true,
//			                backgroundColor: Highcharts.theme.gaugebackgroundColor,
			                borderWidth: 0,
//			                borderColor: Highcharts.theme.gaugeborderColor,
			                width: 100,
			                height: 40,
			                offsetCenter: ['0%', 36],       // x, y，单位px
			                formatter:function(a,b){
							var value=	Number(tdData[i].value).toFixed(2);
						
								return tdData[i].value!=null && tdData[i].value != undefined ?(tdData[i].value == 'N/A' ? 'N/A' : value+unit): '--' ;
							},
			                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
			                    color: 'auto',
			                    fontSize : 12
			                }
			            },
			            data:[{value: value, name: tdData[i].title}]
			        }
			    ]
			};
			if(tdData[i].id=="cpuRate" ){
		
				tdDoms.find(".cpuRate").width("200px");
				tdDoms.find(".cpuRate").height("131px");
				tdDoms.find(".memRate").width("200px");
				tdDoms.find(".memRate").height("131px");
				var myChart = echarts.init(tdDoms.find(".cpuRate")[0]);
				myChart.setOption(option);
			}else if(tdData[i].id=='icmpDelayTime'){
					tdDoms.find(".icmpDelayTime").width("200px");
				tdDoms.find(".icmpDelayTime").height("131px");
				tdDoms.find(".discardsRate").width("200px");
				tdDoms.find(".discardsRate").height("131px");
			
					var myChart = echarts.init(tdDoms.find(".icmpDelayTime")[0]);
				myChart.setOption(option);
			}else if(tdData[i].id=='discardsRate'){
				var myChart = echarts.init(tdDoms.find(".discardsRate")[0]);
				myChart.setOption(option);
			}else if(tdData[i].id=='CPUPercent'){
					tdDoms.find(".CPUPercent").width("200px");
				tdDoms.find(".CPUPercent").height("131px");
				tdDoms.find(".MEMPercent").width("200px");
				tdDoms.find(".MEMPercent").height("131px");
			
					var myChart = echarts.init(tdDoms.find(".CPUPercent")[0]);
				myChart.setOption(option);
			}else if(tdData[i].id=='MEMPercent'){
			
					var myChart = echarts.init(tdDoms.find(".MEMPercent")[0]);
				myChart.setOption(option);
			}else if(tdData[i].id=="physicalRate"){
				tdDoms.find(".physicalRate").width("300px");
				tdDoms.find(".physicalRate").height("161px").css("padding-left","75px");
					var myChart = echarts.init(tdDoms.find(".physicalRate")[0]);
				myChart.setOption(option);
			}
			else if(tdData[i].id=="memRate"){
				var myChart = echarts.init(tdDoms.find(".memRate")[0]);
				myChart.setOption(option);
			}
			
			
		}
			
		
		},
		createMeter : function(tdDom, tdData,tdSize){
			// 如果没有设置告警则状态为正常(特殊设置)
			if(tdData.isAlarm == false){
				tdData.status = 'NORMAL';
			}
			var meter = this.createPanel(tdDom, tdData, tdSize);
			var color = this.returnColorByStatus(tdData.status);
			var unit = tdData.unit == undefined ? '' : tdData.unit;
			var size = tdSize == 4 ? '130%' : '160%';
			var textSize = tdSize == 4 ? '30px' : '40px';
			var availability=  this.cfg.availability;
	
			meter.highcharts({
				
				chart : {
					type : 'solidgauge',
					margin : [ 0, 0, 0, 0 ],
					spacing : [ 0, 0, -35, 0 ],
					backgroundColor : Highcharts.theme.gaugebackgroundColor
				},
	    	    title: null,
	    	    pane: {
	    	    	center: ['50%', '90%'],
	    	    	size: size,
	    	        startAngle: -90,
	    	        endAngle: 89.5, // 在ie8下90度 值为百分百 指针会越界
	                background: {
	                	borderWidth : 1,
	                    innerRadius: '80%',
	                    outerRadius: '101%',
	                    shape: 'arc'
	                },
	                width : 200
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
	    	    // the value axis
	    	    yAxis: {
	    	        min: 0,
	    	        max: 100,
	    			stops: [
	    				[0.1, color],
	    	        	[0.5, color],
	    	        	[0.9, color]
	    			],
	    			lineWidth: 1,
	                tickPixelInterval: 80,
	                gridLineWidth : 0,
	                minorGridLineWidth : 0,
	                minorTickWidth : 0,
	                tickWidth : 0,
	                labels: {
	                	distance: 10,
	                    x : 1,
	                    style : {
	                    	fontSize: '13px'
	                    }
	                }
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
	    	        data: [0],
	    	        dataLabels: {
	    	        	align : 'center',
	    	        	y : -10,
	    	        	style : {
	    	        		fontSize:textSize//,
//	    	        		color :  '#555555'//#EC5707
	    	        	},
	    	        	format:   tdData.value != null && tdData.value != undefined && availability!='CRITICAL_NOTHING' && availability!="CRITICAL"
	    	        			? (tdData.value == 'N/A' ? 'N/A' : '{y} ' + unit) : '--'
	    	        },
	    	        innerRadius : '81.5%'
	    	    },{
	    	    	type : 'gauge',
	    	    	dial: {
	    	    		backgroundColor : Highcharts.theme.plotOptions.gauge.dial.backgroundColor,
	    	    		borderColor : Highcharts.theme.plotOptions.gauge.dial.borderColor,
		    	    	baseLength: '0%',
		    	    	baseWidth: 6,
		    	    	topWidth: 1,
		    	    	borderWidth: 0,
		    	    	radius : '80%',
		    	    	rearLength : '0%'
	    	    	},
	    	    	pivot : {
	    	    		backgroundColor : Highcharts.theme.plotOptions.gauge.pivot.backgroundColor,
	    	    		radius : 3
					},
					dataLabels: {
						enabled:false
					},
	    	    	data : [0]
	    	    }]
		   },function(chart){
				if(tdData.value != null && tdData.value != undefined && tdData.value != 'N/A'){
					var series = chart.series;
					for(var index in series){
						var points = series[index].points;
					
						if(!!points){
							if(availability=='CRITICAL_NOTHING' || availability=="CRITICAL"){
							}else{
								points[0].update(parseFloat(tdData.value));
							}
				
						}
					}
				}
		    });
		},
		createSolidgauge : function(tdDom, tdData, tdSize){

			var green=Highcharts.theme.resourceDetailMetricColors[0],yellow=Highcharts.theme.resourceDetailMetricColors[1],red=Highcharts.theme.resourceDetailMetricColors[2],orange=Highcharts.theme.resourceDetailMetricColors[2];
			var param = {},labelStr='';
			param.stops= [[0, green]];
			param.value = null;
			param.collectTime = '--';
			
			if(tdData.collectTime){
				var date = new Date(tdData.collectTime);
				var str = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
				
				param.collectTime = str;
			}
			
			if('instancestate'==tdData.metricType){
				param.value = 100;
				//此处子资源告警依然认为状态变化
				switch(tdData.status){
				case "CRITICAL":
				case "CRITICAL_NOTHING":
				case "NORMAL_CRITICAL":
					labelStr = '致命';
					param.stops= [[0, red]];
					break;
				case "SERIOUS":
					labelStr = '严重';
					param.stops= [[0, orange]];
					break;
				case "WARN":
					labelStr = '告警';
					param.stops= [[0, yellow]];
					break;
				
				default:
					labelStr = '正常';
				param.stops= [[0, green]];
					break;
				}
			}else if('availability'==tdData.metricType){
				param.value = 100;
				
				switch(tdData.status){
				case "CRITICAL":
					param.stops= [[0, red]];
					labelStr = '不可用';
					break;
				case "SERIOUS":
				case "WARN":
				default :
					labelStr = '可用';
					param.stops= [[0, green]];
					break;
				}
			}else{
				if(tdData.value){
					if(tdData.soliType&&tdData.soliType=='number'){
						param.value = tdData.value;
					}else{
						param.value = 100;
					}
					labelStr = tdData.value;//+tdData.unit;
					
					// #55BF3B green  #DDDF0D yellow #DF5353 red   #EE9A00 orange
					if(tdData.status){
						switch(tdData.status){
						case "CRITICAL":
							param.stops= [[0, red]];
							break;
						case "SERIOUS":
							param.stops= [[0, orange]];
							break;
						case "WARN":
							param.stops= [[0, yellow]];
							break;
						default :
							param.stops= [[0, green]];
							break;
						}
					}
					
				}else{
					labelStr = '--';
					param.collectTime = '--';
				}
			}
			param.label = labelStr;
			
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({'width':'100%','height':'100%'});
			var divSo = $("<div/>").css({'width':'100%','height':'80%'});
			var divText = $('<div/>').attr('align','center').css({'width':'100%','height':'20%'})
			.html('<span name="textSpan" >'+param.collectTime+'</span>');
//			.attr('name','metricTextDiv').attr('instanceId',dataItem.instanceId).attr('metricType',dataItem.metricType)
//			.attr('metricId',dataItem.metricId).attr('unit',dataItem.metricUnit);
			
			divPanel.append(divSo).append(divText);
			childTable.append(divPanel);
			divSo.highcharts({
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
					size: '80%',
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
		
		createGraph : function(tdDom, tdData, tdSize,tabPanel){

			var that = this;
			var graph=$("<div/>");
			var metricId="";
			var unit="%";
			if(tdData.type=='others'){//不显示
				
			}else{
			if(tdData.id=='AmbientTemp' || tdData.id=='DiskUsed' || tdData.id=='DiskUsagePercentage' || tdData.id=='SystemTemperature'){
				
		
				graph=tdDom;
				metricId=tdData.id;
			}else if(tdData.id=='throughput' || tdData.id=='NetworkUsed' || tdData.id=='KBytesTransSpeed' || tdData.id=='icmpDelayTime' || tdData.id=='cpuLoad' ){
				graph = this.createPanel(tdDom, tdData, tdSize);
				
				metricId=tdData.id;
			}else{
				graph = this.createPanel(tdDom, tdData, tdSize).css('width','90%');
				metricId=tdData.metric;
			}


			var dataid=tdData.metric;

			if(tdData.historyData == undefined){
				
			
				tdData.historyData = [];
			}else{
			if(tdData.historyData.length == 0){
//				graph.append('当前无数据');
//				return false;
				tdData.historyData = [];
			}
			}
			var step=null;
			if(tdData.historyDataColTim == undefined){
				tdData.historyDataColTim = [];
					//graph.css('height','100%');
			}else{
				if(tdData.id=='throughput' || tdData.id=='NetworkUsed' || tdData.id=='KBytesTransSpeed' || tdData.id=='icmpDelayTime' || tdData.id=='cpuLoad' || tdData.id=='SystemTemperature'){
				//	graph.css('width','95%');
							graph.css('height','80%');
					
						}else{
								//graph.css('width','97%');
						}
		
			}
	
			if(unit==null){
				unit="%"
			}else{
				unit=tdData.unit;
			}
		
			var html='<fieldset style="width: 100%; height: 100%;"><legend>'+tdData.title+'(最近4小时)</legend>'+'单位:'+unit+'</span><div class="lines" style="width: 100%; height: 80%;"></div></fieldset>';//width: 100%; height: 35%; float: left;

	if(tdData.id=='throughput' || tdData.id=='NetworkUsed' || tdData.id=='KBytesTransSpeed' || tdData.id=='icmpDelayTime' || tdData.id=='cpuLoad' ){

		var currentgraph=graph;
		currentgraph.css({"height":"85%"});
		if(tdData.width=="30%"){
			currentgraph.css({"margin":"0px 30px"});
		}
	
	}else{
		graph.append(html);
			var currentgraph=$(".lines");	
	}		
	var Datas=that.handlegraphData(metricId,tdData);

		var mydate = new Date();
		var dayShow=mydate.getDate();
		// step=historyDataColTim.length>2?Math.floor(historyDataColTim.length / 4):1;
		 Highcharts.setOptions({
		        lang: {
		            shortMonths : ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
		            weekdays: ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
		        }
	    });
	var legend = {enabled:false};
		currentgraph.highcharts({
	        chart: {
	            type: 'areaspline'
	        },
	        title: {
	            text: ''
	        },
	        subtitle: {
                text: ''
            },
	        xAxis:Datas.xAxisTemp,
	        yAxis: Datas.yAxisTemp,
	        colors:Highcharts.theme.areasplineColor,
			credits: {
		    	enabled: false
		    },
		    exporting : {
		    	enabled: false
		    },
		    legend: legend,
		    plotOptions : {
		    	series: {
		    		 animation: false  ,
	                marker: {
	                    enabled: true,
	                    radius: 1
	                }
	            },

	            areaspline: {
	                fillOpacity: 0.1,
	                lineWidth:2,
	                marker: {
	                    enabled: true,
	                    radius: 1
	                },
	                fillColor: {
                        linearGradient: {
                            x1: 0,
                            y1: 0,
                            x2: 0,
                            y2: 1
                        },
                        
                        stops:Highcharts.theme.areasplineStopColor
                    
						 }
	            }
		    },
		    tooltip: Datas.tooltip,
	        series: Datas.seriesData
		},function(chart){
			//回调
//				console.log(chart);
		});
			
			}
		
			if(tdData.id=='throughput' || tdData.id=='NetworkUsed'){

		currentgraph.before("<div style='margin:0px 10px;'>单位:"+unit+"</div>");
	
	}
		/*	if(tabPanel==true){
			graph.append("<div style='text-align: center; margin: 15px 0px;font-size: 20px;'>"+tdData.title+"</div>");
			}*/
		},
		createChildrenRes : function(tdDom, tdData, tdSize){
			var that = this;
			var childrenRes = this.createPanel(tdDom, tdData, tdSize);
			
			var outerHigChar = $("<div/>").width('100%').height('160px');//136px
			if(tdData.type=="multiInterface"){//接口展示
				outerHigChar.height('110px');//127
			}
			var higContainer = $("<div/>").width('100%').height('100%');
			if(tdData.type=="multiprocess"){
				childrenRes.append(outerHigChar.append(higContainer));
			//	d_page.hide();
				this.createLitProcess(higContainer, tdData,false);
				
			}else{
				
		
			
			if(tdData.value.length == 0){
				childrenRes.html('<div class="otherTabTip">抱歉，没有可展示的数据！</div>');
				return false;
			}
	
			var d_page = $("<div/>").attr('style', 'text-align: center;');
			d_page.css({"position":"relative"});
			childrenRes.append(outerHigChar.append(higContainer)).append(d_page);
			var pageShowNo=4;
			if(tdData.type=="multiInterface"){//接口展示
				pageShowNo=4;
			}
			var pageNum = Math.ceil(parseInt(tdData.value.length)/pageShowNo);
			
			for(var i = 0; i < pageNum; i++){
				var aNode = $("<a/>").attr('pageNo', i + 1).attr('type', 'show').addClass("d-page-ico").addClass('d-page-dot');
				if(i == 0){
					var left = $("<a/>").attr('type', 'prev').addClass("d-page-ico").addClass("d-page-l");
					aNode.addClass('d-page-greendot');
					d_page.append(left).append(aNode);
				}
				if(i>7){
					
				}
				d_page.append(aNode);
				if(i + 1 == pageNum){
					var rightNode = $("<a/>").attr('type', 'next').addClass("d-page-ico").addClass("d-page-r");
					d_page.append(rightNode);
				}
			}
			// 对分页按钮添加事件
			d_page.find("a").on('click', function(){
				var clickA = $(this);
				var currentA = d_page.find(".d-page-greendot");
				var firstA = d_page.find("a[type='show']:first");
				var lastA = d_page.find("a[type='show']:last");
				
				currentA.removeClass("d-page-greendot").addClass("d-page-dot");
				switch(clickA.attr('type')){
				case 'prev':
					if(currentA.prev("a").attr('type') == 'show'){
						currentA = currentA.prev("a");
					}else{
						currentA = lastA;
					}
					break;
				case 'next':
					if(currentA.next("a").attr('type') == 'show'){
						currentA = currentA.next("a");
					}else{
						currentA = firstA;
					}
					break;
				case 'show':
					currentA = clickA;
					break;
				default:
					break;
				}
				that.changePageContent(currentA, pageShowNo, tdData.type, tdData.value, higContainer,tdData)
		
			
			});
			// 如果只有一页则隐藏翻页按钮
			
			if(pageNum == 1){
				d_page.hide();
			}

				// 初始化第一页的显示
				this.createPageChildRes(1, pageShowNo, tdData.type, tdData.value, higContainer,tdData);	
	
			}
			
		},
		changePageContent : function(currentA, pageSize, type, value, container,tdData){
			var that = this;
			currentA.removeClass("d-page-dot").addClass("d-page-greendot");
			container.fadeOut('fast', function(){
				that.createPageChildRes(parseInt(currentA.attr('pageNo')), pageSize, type, value, container,tdData);
				container.fadeIn('fast');
			});
		},
		createPageChildRes : function(pageNo, pageSize, type, value, container,tdData){
			
			container.empty();
			var startNo = parseInt(pageNo) == 1 ? 0 : (parseInt(pageNo - 1) * parseInt(pageSize));
			var endNo = Math.min((parseInt(pageNo) * parseInt(pageSize)), value.length);
			for(var i = startNo; i < endNo; i ++){
				switch(type){
				case "multiMeter":
					
					var dataResult = value[i];
					var dataTemp;
					var childtypeTemp = tdData.childtype[0];
					switch(childtypeTemp){
					case "Partition":
						//分区
						dataTemp  = {isAlarm:dataResult.isAlarm,status:dataResult.fileSysRateStatus,
								name:dataResult.fileSysName,unUsedSize:dataResult.fileSysFreeSize,
								usedSize:dataResult.fileSysUsedSize,rate:dataResult.fileSysRate,prefixStr:""};
						break;
					case "VCSHost":
						dataTemp  = {isAlarm:dataResult.isAlarm,status:dataResult.fileSysRateStatus,
							name:dataResult.fileSysName,unUsedSize:dataResult.fileSysFreeSize,
							usedSize:dataResult.fileSysUsedSize,rate:dataResult.fileSysRate,prefixStr:""};
						break;
					default :
						dataTemp={};
						break;
					}
					this.createLitMeter(container, dataTemp,"multiMeter");
					
					break;
				case "multiInterface":
					var dataResult = value[i];
					var showPic=true;
					if(i>1){
						showPic=false;
					}
				this.createLitInterface(container, dataResult,showPic,"multiInterface");
				break;
				case "table":
				var dataResult = value[i];
					var dataTemp;
				
				
					var childtypeTemp = tdData.value;
				
					for(var i = startNo; i < endNo;i++){
						var temp=childtypeTemp[i];
						var num=parseInt(temp.DataStorageFreeSpace.replace("GB",""));
									var total=parseInt(temp.DataStorageVolume.replace("GB",""));
					
						var temprate=Math.round((total-num) / total * 10000) / 100.00;//已使用
						if(temp.DataStorageFreeSpace=="" && temp.DataStorageVolume==""){
							temprate=null;
						}
						dataTemp  = {availabilityStatus:temp.availabilityStatus,
								name:temp.Name,NameStatus:temp.NameStatus,total:temp.DataStorageVolume==""?"--":temp.DataStorageVolume,
								usedSize:temp.DataStorageFreeSpace==""?"--":temp.DataStorageFreeSpace,rate:temprate,prefixStr:"存储器:"};
									this.createLitstoreage(container, dataTemp,"multiMeter");
					}
			
				break;
				case "fan":
					this.createLitFan(container, value[i]);
					break;
				case "battery":
					this.createLitBattery(container, value[i]);
					break;
				case "cpu":
					this.createLitCpu(container, value[i]);
					break;
				case "camera":
					this.createCamera(container, value[i]);
					break;
				case "microphone":
					this.createMike(container, value[i]);
					break;
				case "display":
					this.createVideo(container, value[i]);
					break;
				case "node":
					this.createNode(container, value[i]);
					break;
				case "commonChildrenPanel":
					this.createCommonChildrenPanel(container, value[i]);
					break;
				}
//				if(type == 'multiMeter'){
//					var dataResult = value[i];
//					var dataTemp;
//					var childtypeTemp = childtype[0];
//					switch(childtypeTemp){
//					case "Partition":
//						//分区
//						dataTemp  = {isAlarm:dataResult.isAlarm,status:dataResult.fileSysRateStatus,
//								name:dataResult.fileSysName,unUsedSize:dataResult.fileSysFreeSize,
//								usedSize:dataResult.fileSysUsedSize,rate:dataResult.fileSysRate,prefixStr:"分区:"};
//						break;
//					case "VCSHost":
//						dataTemp  = {isAlarm:dataResult.isAlarm,status:dataResult.fileSysRateStatus,
//							name:dataResult.fileSysName,unUsedSize:dataResult.fileSysFreeSize,
//							usedSize:dataResult.fileSysUsedSize,rate:dataResult.fileSysRate,prefixStr:""};
//						break;
//					default :
//						dataTemp={};
//						break;
//					}
//					this.createLitMeter(container, dataTemp);
//				} else if(type == 'fan'){
//					this.createLitFan(container, value[i]);
//				} else if(type == 'battery'){
//					this.createLitBattery(container, value[i]);
//				} else if(type == 'cpu'){
//					this.createLitCpu(container, value[i]);
//				}else if(type == 'camera'){
//					this.createCamera(container, value[i]);
//				}else if(type == 'microphone'){
//					this.createMike(container, value[i]);
//				}else if(type == 'display'){
//					this.createVideo(container, value[i]);
//				}else if(type == 'node'){
//					this.createNode(container, value[i]);
//				}
			}
		},
		createCommonChildrenPanel : function(container, data){
			
			if(!container.hasClass("fan")){
				container.addClass("fan");
			}
			if(container.find("ul").length == 0){
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var normalClass = data.normalClass;
			var criticalClass = data.criticalClass;
			var width = data.width;
			var adaptClass = '';
			if(width=="33%"||width=="33%"){
				adaptClass = ' width103';
			}
			
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='"+criticalClass+"' />" : "<span class='"+normalClass+"' />";
			li += "<span class='hardware_name"+adaptClass+"'>" + data.resourceName + "</span>"
			container.find("ul").append("<li title='" + data.resourceName + "'>" + li + "</li>");
		},
		createCommonOneChildrenPanel : function(tdDom, tdData, tdSize){
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({'width':'100%'});
			
			var values = tdData.value;
			if(values)
			for(var i=0;i<values.length;i++){
				var temp = values[i];
				var nameP = temp['resourceName'];
				var tempClass = 'allocation-margin ';
				var value = temp['instance_state'];
				
				var green = temp['normalClass'];
				var red = temp['criticalClass'];
				
				if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
					tempClass += red;//"ico-allocation ico-source-red";
				}else{
					tempClass += green;//"ico-allocation ico-source-green";
				}	
				
				var classTemp = "";
				if(values.length==3){
					classTemp = "margin: 16px 15px 0px 45px";
				}
				var div = $("<div class='video_class clearl textc' style='"+classTemp+"'/>");
				var span = $("<span />").addClass(tempClass);
				var p = $("<p></p>").html(nameP).addClass('p-width');
				div.append(span).append(p);
				divPanel.append(div);
			}
			
			childTable.addClass('oc-wbg').css({"overflow-y":"auto"}).append(divPanel);
			
		},
		createCommonOneMetricPanel : function(tdDom, tdData, tdSize){
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({'width':'100%'});
			
			var value = tdData.value;
			var tempClass = 'allocation-margin ';
			tempClass += tdData.normalClass;
			
			var div = $("<div class='video_class clearl textc' />");
			var span = $("<span />").addClass(tempClass);
			var p = $("<p></p>").html(value).addClass('p-width');
			div.append(span).append(p);
			divPanel.append(div);
			
			childTable.addClass('oc-wbg').css({"overflow-y":"auto"}).append(divPanel);
			
		},
		createLitProcess:function(container, data,showPic){
				var datas=data.mapvalue;
				var dataArr=new Array();
				var isJDBC=false;
				if(this.cfg.resourceType=='UniversalModel'){
					isJDBC=true;
//					for(var i=0;i<datas.length;i++){
						//查询所有的性能指标，默认显示前三个
						oc.util.ajax({
							url : oc.resource.getUrl('portal/resource/resourceDetailInfo/getAllMetricTop.htm'),
							dataType:"json",
							data : {
								instanceId : this.cfg.instanceId
							},
							async:false,
							success:function(json){	
								var rows=json.data.rows;
								var perMetrics=json.data.perMetrics;
								
								if(rows.length==0){//无数据
								}else{
									if(perMetrics!=""){
										var perMetricsArr=perMetrics.split(",");
										for(var i=0;i<rows.length;i++){
											var row=rows[i];
										for(var j=0;j<perMetricsArr.length;j++){
											if(row.id==perMetricsArr[j]){
											}else{
												if(i>(perMetricsArr.length-1) && i<(perMetricsArr.length+4)){
													dataArr[i-perMetricsArr.length]=row;
												}		
											}
										}
												
									
											
										}
									}else{
										for(var i=0;i<rows.length;i++){
											var row=rows[i];
											if(i<4){
												dataArr[i]=row;
											}
												
									
											
										}
									}
								
									datas=dataArr;
								}
								}
							});
				}else{
					oc.util.ajax({
						url : oc.resource.getUrl('portal/resource/resourceDetailInfo/getMerticInfos.htm'),
						successMsg:null,
						async:false,
						data : {
							instanceId : this.cfg.instanceId,
							type:data.childtype[0]
						},
						success : function(json) {
							datas=json.data;
						}
					});
				}
		
		
			
			var hd = $("<div/>").addClass("w-parbg process-parbg").width('100%');//.height(50);
			var hdul=$("<ul/>").addClass("process-ul");
			hd.append(hdul);
			container.append(hd);
			if(datas!=undefined){
					for(var i=0;i<datas.length;i++){//contmap.put("status", map.get("status"));
					var color=Highcharts.theme.resourceDetailMetricColors[0];
					var title='';var value='';
					if(this.cfg.resourceType=='UniversalModel'){
						value=(datas[i].currentVal==null || datas[i].currentVal=="")?"--":datas[i].currentVal;
						title=datas[i].text;
					}else{
						value=(datas[i].currentVal==null || datas[i].currentVal=="")?"--":datas[i].isMonitor==false?"未监控":datas[i].currentVal;
						title=datas[i].text;
					}
				
					//var color = '#00C803';
					if(datas[i].isAlarm==false){
						color=Highcharts.theme.resourceDetailNoAlarm;
						
					}else{
					switch(datas[i].status){
					case 'CRITICAL':
						color = Highcharts.theme.resourceDetailMetricColors[3];
						break;
					case 'SERIOUS':
						color = Highcharts.theme.resourceDetailMetricColors[2];
						break;
					case 'WARN':
						color =Highcharts.theme.resourceDetailMetricColors[1];
						break;
//							case 'UNKOWN':
//								color = 'gray'
//								break;
							case 'NORMAL':
							case 'NORMAL_NOTHING':
								color = Highcharts.theme.resourceDetailMetricColors[0];
								break;
							default :
								color = Highcharts.theme.resourceDetailMetricColors[0];
								break;
							}
					}
					
				var	li="";
				
				li+= '<div class="squareBox-resource" style="border-color:'+color+'">'+
				'<div class="square-resource">'+
				'<div class="square-fluid-resource">'+
				'<div class="square-word-resource" title="'+value+'">'+value+'</div>'+
'<span class="square-value-resource" title="'+title+'">'+title+
'</div></div>';
			//	li += "<div class='square-bg-"+color+"'><div>"+datas[i].title+"</div><div>"+datas[i].value+"</div></div>";
			container.find("ul").append("<li title='" + title + "'>" + li + "</li>");
			}
			}else{
				container.append("<div class='otherTabTip'>抱歉，没有可展示的数据！</div>");
			}
		
			
				//hd.css({"margin-top": "25px"});
		/*	var innerhd1 = $("<div/>").addClass("").attr('style','margin: 8px 0px 0px 27px;');
			var innerhd2 = $("<div/>").addClass("").attr('style','margin: 8px 0px 0px 27px;');
			
			
			var inner1=$("<div/>").addClass("square-bg-green").attr('style','float:left').css({"margin-right": "25px","margin-top":"30px"}).append(innerhd1);
			innerhd1.append("<span/>").append("<br>").append("<span/>");
		
			var inner2=$("<div/>").addClass("square-bg-yellow").attr('style','float:left').css({"margin-right": "25px","margin-top":"30px"}).append(innerhd2);
			innerhd2.append("<span/>").append("<br>").append("<span/>");
		
			hd.append(inner1);
			hd.append(inner2);

			if(data.value.length==0){
				container.append("<div>当前无数据</div>");
			}else{
			container.append(hd);
			}
			var inner1spans=$(hd).find("span");
			var inner2spans=$(".square-bg-yellow").find("span");
			$(inner1spans[0]).html(data.firstNumTxt);
			$(inner1spans[1]).html(data.firstNum );
			$(inner1spans[2]).html(data.secondNumTxt);
			$(inner1spans[3]).html(data.secondNum );
			var spans = hd.find("span");
			$(inner1spans[0]).html(data.firstNum );
				$(spans[1]).html(data.secondNum ).attr('title', data.secondNum);*/
			
		},
		createLitInterface: function(container, data,showPic,cls_name){
			
			var name = typeof(data.resourceRealName) == 'undefined' ? '' : data.resourceRealName;	
			var state=data.ifStatus=='NORMAL'?'可用':'不可用';
			var nameStr =name;
				if(nameStr.length > 30){
		            		nameStr = nameStr.substring(0, 29) + "...";
		            	}
				var hd = $("<div/>").addClass("w-parbg-"+cls_name).css("float","left");//.width('100%').height(60);
			/*	if(showPic==false){
					hd.css({'display':'none'});//w-parbg-multiMeter  w-parbg-multiInterface
				}else{
					hd.css({'display':'block'});
				}*/
				var innerhd = $("<div/>").addClass("svg-name");
			var innerhd2 = $("<div/>").addClass("svg-panel")
				//.append($("<span/>").addClass("svg-num").attr('style', 'float:left;'))
			var inner1=$("<div/>").addClass("interface-green").attr('style','float:left');//.css({'width':'40px','height':'40px'}))
			var inner2=$("<div/>").addClass("svg-txt").attr('style','').append($("<span/>").addClass("svg-num").attr('style', '')).append("<br>").append($("<span/>").addClass("svg-num").attr('style', ''));
				//.append($("<span/>").addClass("svg-num").attr('style', 'float:right;'));
			hd.append(inner1);
			hd.append(inner2);
			container.append(hd);
			var spans = hd.find("span");
			$(spans[0]).html(state );
				$(spans[1]).html(nameStr ).attr('title', name);
		},
		createLitstoreage: function(container, data,cls_name){

			var name = typeof(data.name) == 'undefined' ? '' : data.name;
			var total = typeof(data.total) == 'undefined' ? '' : data.total;
			var usedSize = typeof(data.usedSize) == 'undefined' ? '' : data.usedSize;
			var rate = typeof(data.rate) != undefined && typeof(data.rate) != 'null'
						&& data.rate != 'N/A' ? parseFloat(data.rate) : 0;
						//console.info(rate);
			var hd = $("<div/>").addClass("w-parbg-"+cls_name).width(161).height(60);
			hd.attr('style','float:left');
			var nameStr =name;
			var namecolor = data.NameStatus;//this.returnColorByStatus(data.NameStatus);
			if(data.prefixStr){
				nameStr = '<label class="name-'+namecolor+'">'+name+'</label>';
			}
			var innerhd = $("<div/>").addClass("svg-name").html(nameStr).attr('title', name);
			var innerhd2 = $("<div/>").addClass("svg-panel")
				//.append($("<span/>").addClass("svg-num").attr('style', 'float:left;'))
				.append($("<div/>").addClass("svg-pic").attr('style','float:left').css({'width':'40px','height':'40px'}))
				.append($("<div/>").addClass("svg-txt").attr('style','').append($("<span/>").addClass("svg-num").attr('style', 'float:left;')).append("<br>").append($("<span/>").addClass("svg-num").attr('style', 'float:left;')));
				//.append($("<span/>").addClass("svg-num").attr('style', 'float:right;'));
			container.append(hd.append(innerhd).append(innerhd2));
			var spans = hd.find("span");
			$(spans[0]).attr("title", total).html('总容量' + total);
			$(spans[1]).attr("title", usedSize).html('可用容量' + usedSize);
			var color = this.returnColorByStatus(data.availabilityStatus);
			hd.find(".svg-pic").highcharts({
				chart: {
					type: 'solidgauge',
					margin : [0, 0, 0, 0],
					spacing : [0, 0, 0, 0]
				},
				title : {
					text : ''
				},
				pane: {
					center: ['50%', '50%'],
					size: '100%',
					startAngle: 0,
					endAngle: 360,
					background: {                      
//	                	borderColor : Highcharts.theme.resourceLitMeterBorderColor,
	                	borderWidth : 0,
						backgroundColor:Highcharts.theme.resourceLitMeterPanelBackgroundColor,
						innerRadius: '80%',
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
					stops: [
	    				[0.1, color],
	    	        	[0.5, color],
	    	        	[0.9, color]
		    		],
	                gridLineWidth : 0,
	                minorGridLineWidth : 0,
			        lineWidth: 0,
			        minorTickWidth : 0,
			        tickWidth: 0,
			        labels : {
			        	enabled : false
			        },
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
					data: [rate],
					dataLabels: {
						align : 'center',
						crop : false,
						overflow : 'none',
	    	        	y : -11,
						style : {
							fontSize : 12,
	    	        		fontWeight : 'normal'
						},
						format: '<span style="font-size:12px;color:#FFFFFF;letter-spacing:-1px">'
							+ (data.rate != null && data.rate != undefined
									? (data.rate == 'N/A' ? 'N/A' : '{y}%') : '--')
							+ '</span>'
					},
					innerRadius : '81.5%'
				}]
			},function(chart){
				chart.renderer.circle(20, 20, 16.2)
	            .attr({
	                'stroke-width': 0,
//	                stroke: Highcharts.theme.resourceLitMeterFillBorderColor,
	                fill: Highcharts.theme.resourceLitMeterFillBackgroundColor,
	                zIndex: -1
	            }).add();
			});
		},
		createLitMeter : function(container, data,cls_name){
			// 如果没有设置告警则状态为正常(特殊设置)
			if(data.isAlarm == false){
				data.status = 'NORMAL';
			}
			var name = typeof(data.name) == 'undefined' ? '' : data.name;
			var unUsedSize = typeof(data.unUsedSize) == 'undefined' ? '' : data.unUsedSize;
			var usedSize = typeof(data.usedSize) == 'undefined' ? '' : data.usedSize;
			var rate = typeof(data.rate) != undefined && typeof(data.rate) != 'null'
						&& data.rate != 'N/A' ? parseFloat(data.rate) : 0;
			var hd = $("<div/>").addClass("w-parbg-"+cls_name).width(161).height(60);
			hd.attr('style','float:left');
			var nameStr =name;
			if(data.prefixStr){
				nameStr = data.prefixStr+name;
			}
			var innerhd = $("<div/>").addClass("svg-name").html(nameStr).attr('title', name);
			var innerhd2 = $("<div/>").addClass("svg-panel")
				//.append($("<span/>").addClass("svg-num").attr('style', 'float:left;'))
				.append($("<div/>").addClass("svg-pic").attr('style','float:left').css({'width':'40px','height':'40px'}))
				.append($("<div/>").addClass("svg-txt").attr('style','').append($("<span/>").addClass("svg-num").attr('style', 'float:left;')).append("<br>").append($("<span/>").addClass("svg-num").attr('style', 'float:left;')));
				//.append($("<span/>").addClass("svg-num").attr('style', 'float:right;'));
			container.append(hd.append(innerhd).append(innerhd2));
			container.css({"height":"80%"});
			container.parent().css({"float":"left"});
			var spans = hd.find("span");
			$(spans[0]).attr("title", unUsedSize).html('可用' + unUsedSize);
			$(spans[1]).attr("title", usedSize).html('已用' + usedSize);
			var color = this.returnColorByStatus(data.status);
			hd.find(".svg-pic").highcharts({
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
					size: '100%',
					startAngle: 0,
					endAngle: 360,
					background: {                     
//	                	borderColor :Highcharts.theme.resourceLitMeterPanelBorderColor,
	                	borderWidth : 0,
						backgroundColor: Highcharts.theme.resourceLitMeterPanelBackgroundColor,
						innerRadius: '80%',
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
					stops: [
	    				[0.1, color],
	    	        	[0.5, color],
	    	        	[0.9, color]
		    		],
	                gridLineWidth : 0,
	                minorGridLineWidth : 0,
			        lineWidth: 0,
			        minorTickWidth : 0,
			        tickWidth: 0,
			        labels : {
			        	enabled : false
			        },
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
					data: [rate],
					dataLabels: {
						align : 'center',
						crop : false,
						overflow : 'none',
	    	        	y : -11,
						style : {
							fontSize : 12,
	    	        		fontWeight : 'normal'
						},
						format: '<span style="font-size:12px;color:#FFFFFF;letter-spacing:-1px">'
							+ (data.rate != null && data.rate != undefined
									? (data.rate == 'N/A' ? 'N/A' : '{y}%') : '--')
							+ '</span>'
					},
					innerRadius : '81.5%'
				}]
			},function(chart){
				chart.renderer.circle(20, 20, 16.2)
	            .attr({
	                'stroke-width': 0,
//	                stroke: Highcharts.theme.resourceLitMeterFillBorderColor,
	                fill: Highcharts.theme.resourceLitMeterFillBackgroundColor,
	                zIndex: -1
	            }).add();
			});
		},
		createMike : function(container, data){
			if(!container.hasClass("fans")){
				container.addClass("fans");
			}
			if(container.find("ul").length == 0){
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='valnet-ico mike-red' />" : "<span class='valnet-ico mike-green' />";
			li += "<span class='hardware_name'>" + data.microphoneName + "</span>"
			container.find("ul").append("<li title='" + data.microphoneName + "'>" + li + "</li>");
		},
		createVideo : function(container, data){
			if(!container.hasClass("fans")){
				container.addClass("fans");
			}
			if(container.find("ul").length == 0){
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='valnet-ico video-red' />" : "<span class='valnet-ico video-green' />";
			li += "<span class='hardware_name'>" + data.displayName + "</span>"
			container.find("ul").append("<li title='" + data.displayName + "'>" + li + "</li>");
		},
		createNode : function(container, data){
			if(!container.hasClass("fans")){
				container.addClass("fans");
			}
			if(container.find("ul").length == 0){
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='valnet-ico v-node-red' />" : "<span class='valnet-ico v-node-red' />";
			li += "<span class='hardware_name'>" + data.nodeName + "</span>"
			container.find("ul").append("<li title='" + data.nodeName + "'>" + li + "</li>");
		},
		createCamera : function(container, data){
			if(!container.hasClass("fans")){
				container.addClass("fans");
			}
			if(container.find("ul").length == 0){
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='valnet-ico camera-red' />" : "<span class='valnet-ico camera-green' />";
			li += "<span class='hardware_name'>" + data.cameraName + "</span>"
			container.find("ul").append("<li title='" + data.cameraName + "'>" + li + "</li>");
		},
		createLitFan : function(container, data){
			if(!container.hasClass("fans")){
				container.addClass("fans");
			}
			if(container.find("ul").length == 0){
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='res-size-state fan-red' />" : "<span class='res-size-state fan-green' />";
			li += "<span class='hardware_name'>" + data.fanName + "</span>"
			container.find("ul").append("<li title='" + data.fanName + "'>" + li + "</li>");
		},
		createLitBattery : function(container, data){
			if(!container.hasClass("batterys")){
				container.addClass("batterys");
			}
			if(container.find("ul").length == 0){
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='res-size-state battery-red' />" : "<span class='res-size-state battery-green' />";
			li += "<span class='hardware_name'>" + data.powerName + "</span>"
			container.find("ul").append("<li title='" + data.powerName + "'>" + li + "</li>");
		},
		createLitCpu : function(container, data){
			if(!container.hasClass("cpus")){
				container.addClass("cpus");
			}
			if(container.find("ul").length == 0){
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='res-size-state processer-red' />" : "<span class='res-size-state processer-green' />";
			li += "<span class='hardware_name'>" + data.processorName + "</span>"
			container.find("ul").append("<li title='" + data.processorName + "'>" + li + "</li>");
		},
		createTabs : function(tdDom, tdData, tdSize){
			var that = this;
			var tabsDom =tdDom;// this.createPanel(tdDom, tdData, tdSize);
			var tabsHeight = '206px';
			for(var i = 0; i < tdData.childtype.length; i ++){
				var childType = tdData.childtype[i];
				
				switch(childType){
				case "prefmetric":
					this.createPrefMetric(tabsDom);
					break;
				case "infometric":
					this.createInfoMetric(tabsDom);
					break;
				case "storageDevice":
					tabsHeight = '236px';
					this.createStorageDevice(tdData.value, tabsDom);
					break;
				case "accessDevice":
					tabsHeight = '243px';
					this.createAccessDevice(tdData.value, tabsDom);
					break;
				case "alarm-unRestore":
					tabsHeight = '245px';
					
					this.createAlarmTabs(tabsDom,'unRecovered');
					break;
				case "alarm-restore":
					tabsHeight = '245px';
					this.createAlarmTabs(tabsDom,'recovered');
					break;
					
				default :
					this.createChildPanel(tabsDom, childType);
					break;
				}
			}
			
			var insId = this.cfg.instanceId;
			tabsDom.addClass('easyui-tabs').tabs({
				width : '100%',
				height : tabsHeight,
				fit : false,
				onSelect:function(title,index){
			        //信息指标添加刷新按钮
					if(index == 1){
			        	var InfoMetricDom = tabsDom.find('div .tabs-wrap > ul');
//			        	InfoMetricDom.append('<span style="height: 27px; float: right; margin-right: 10px;">'+
//				        			'<span class="ico ico-refrash" style="margin-top: 5px;" title="手动采集"></span>'+
//				        			'</span>');
//			        	InfoMetricDom.append(
////			        			'<div class="right">'+
//			        			'<a id="collectBtn" style="height: 27px; float: right; class="l-btn l-btn-small l-btn-plain" group="">'+
//			        			'<span class="l-btn-left l-btn-icon-left">'+
//			        			'<div class="btn-l">'+
//			        			'<div class="btn-r">'+
//			        			'<div class="btn-m">'+
//			        			'<span class="l-btn-text">采集信息指标</span>'+
//			        			'<span class="l-btn-icon icon-refrash"> </span>'+
//			        			'</div>'+
//			        			'</div>'+
//			        			'</div>'+
//			        			'</span>'+
//			        			'</a>');
//			        			'</div>');
			        		InfoMetricDom.find('#collectBtn').click(function(){
				        		oc.util.ajax({
								  url: oc.resource.getUrl('portal/resource/resourceDetailInfo/getMetricHand.htm'),
								  data:{instanceId : insId},
								  timeout:60000,
								  success:function(data){
									  if(data.data=="FALSE"){
										  alert("采集失败");
									  }else{
										  alert("指标采集在1分钟以后完成，请稍后进入该页面...");
										  InfoMetricDom.find('#collectBtn').unbind("click");
									  }
									  
								  }
							  });
				        	})
			        }else{
			        	$('div .tabs-wrap > ul > #collectBtn').remove();
			        }
			    }
			});
		},
		createAlarmTabs : function(tabsDom,alarmType){
			var that = this;
			var insId = that.cfg.instanceId;
			var title = '未恢复';
			if('recovered'==alarmType){
				title = '已恢复';
			}
			var infoMetricDom = $("<div/>").addClass('infoMetric').attr('title', title);
			var detailDom = $("<div/>").addClass(alarmType);
			infoMetricDom.append(detailDom);
			tabsDom.append(infoMetricDom);
			
			
			var detailDg = infoMetricDom.find("."+alarmType);
			var datagrid = oc.ui.datagrid({
				selector : detailDg,
				fit : true,
				queryParams: {
					instanceid : insId,//2172
					alarmType : alarmType
				},
				url : oc.resource.getUrl("alarm/alarmManagement/getNotRestoreAlarmForResourceInfoPage.htm"),
				columns : [ [
						{
							field : 'content',
							title : '告警内容',
							width : '70%'
						},
						{
							field : 'collectionTime',
							title : '产生时间',
							width : '30%',
							ellipsis : true,
							formatter:function(value){
								var date = new Date(value);
								var str = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
								return str;
							}
						}] ],
//				loadFilter : function(data){
//					var rows = new Array();
//					for(var i = 0; i < data.data.rows.length; i ++){
//						var row = data.data.rows[i];
//						if(!row.isTable){
//							var currentVal = row.currentVal;
//							row.currentVal = (currentVal != null && currentVal != undefined) ? currentVal : "--";
//							var lastCollTime = row.lastCollTime;
//							row.lastCollTime = !!lastCollTime ? lastCollTime : "--";
//							rows.push(row);
//						}
//					}
//					data.data.rows = rows;
//					return data.data;
//				},
				onLoadSuccess:function(data){
					that.updateTableWidth(datagrid);
				},
				fitColumns : false,
				pagination : true,
				singleSelect : true
			});
			
			return infoMetricDom;
		},
		createStorageDevice : function(tdData, tabsDom){
			var storageDeviceDom = $("<div/>").addClass('storageDevice').attr('title', '存储设备');
			tabsDom.append(storageDeviceDom);
			
			if(tdData == undefined || JSON.stringify(tdData) == "{}"){
				return false;
			}
			var controlComponent = $("<div/>").addClass("Storage"), controlUl = $("<ul/>");
			controlComponent.append("<h6>控制组件</h6>").append(controlUl);
			if(tdData.StorageProcessorSystem != null && tdData.StorageProcessorSystem != undefined){
				this.createChildResInfo(tdData.StorageProcessorSystem, controlUl);
			}
			if(tdData.Node != null && tdData.Node != undefined){
				this.createChildResInfo(tdData.Node, controlUl);
			}
			this.createChildResInfo(tdData.FCPort, controlUl);
			
			var storageComponent = $("<div/>").addClass("Storage"), storageUl = $("<ul/>");
			storageComponent.append("<h6>存储组件</h6>").append(storageUl);
			this.createChildResInfo(tdData.DiskDrive, storageUl);
			this.createChildResInfo(tdData.StoragePool, storageUl);
			this.createChildResInfo(tdData.StorageVolume, storageUl);
			// this.createChildResInfo(tdData.MDisk, storageUl);
			
			storageDeviceDom.append(controlComponent);
			storageDeviceDom.append(storageComponent);
			storageDeviceDom.append("<div class='clear'></div>");
		},
		createAccessDevice : function(tdData, tabsDom){
			return false;
			var accessDeviceDom = $("<div/>").addClass('accessDevice').attr('title', '接入设备');
			tabsDom.append(accessDeviceDom);
		},
		createPrefMetric : function(tabsDom){
			var that = this;
			var prefMetricDom = $("<div/>").addClass('perfMetric').attr('title', '性能指标');
			tabsDom.append(prefMetricDom);
			var introDom = $("<div/>").addClass('intro').css('float', 'left').append($("<div/>").append("<ul/>"));
			prefMetricDom.append(introDom);
			var detailDom = $("<div/>").addClass('detail');
			prefMetricDom.append(detailDom);
			var infoDom = $("<div/>").addClass('info').width('300px');
			detailDom.append(infoDom);
			var valueArray = ['status', 'thresholds', 'currentVal', 'lastCollTime'];
			var descArray = ['状态', '阈值', '当前值', '最近采集时间'];
			for(var i = 0; i < valueArray.length; i ++){
				var innerDom = $("<div/>").addClass('oc-info-boxbg');
				var descDom = $("<div/>").addClass('oc-info-title').html(descArray[i]);
				var valueDom = $("<div/>").addClass(valueArray[i]).addClass('oc-info-value');
				innerDom.append(descDom).append(valueDom);
				infoDom.append(innerDom);
			}
			var hicharDom = $("<div/>").addClass('hichar').css('float', 'right').width('533px').height('152px');
			var highChartContentDom = $("<div/>").addClass('highChartContent').width('100%').height('100%');
			hicharDom.append(highChartContentDom);
			detailDom.append(hicharDom);

			// 性能指标
			prefMetricDom.find(".intro div").panel({
				width : '210px',
				height : '157px',
				fit : false
			});
			
			// HIGHCHART
			this.createHigchar(prefMetricDom.find(".hichar > .highChartContent"));
			prefMetricDom.find("ul").tree({
				fit : true,
				url : oc.resource
						.getUrl("portal/resource/resourceDetailInfo/getMetric.htm?dataType=json" +
								"&instanceId=" + that.cfg.instanceId
								+ "&metricType=PerformanceMetric"),
				loadFilter : function(data, parent){
					var newData = new Array();
					var rows = data.data.rows;
					for(var i = 0; i < rows.length; i ++){
						var row = rows[i];
						var color = Highcharts.theme.resourceDetailMetricColors[0];
						if(row.isAlarm){
							switch(row.status){
							case 'CRITICAL':
								color = Highcharts.theme.resourceDetailMetricColors[3];
								break;
							case 'SERIOUS':
								color = Highcharts.theme.resourceDetailMetricColors[2];
								break;
							case 'WARN':
								color = Highcharts.theme.resourceDetailMetricColors[1];
								break;
//							case 'UNKOWN':
//								color = 'gray'
//								break;
							case 'NORMAL':
							case 'NORMAL_NOTHING':
								color = Highcharts.theme.resourceDetailMetricColors[0];
								break;
							default :
								color = Highcharts.theme.resourceDetailMetricColors[0];
								break;
							}
						}
						
//						if(that.cfg.availability == 'CRITICAL' || that.cfg.availability == 'CRITICAL_NOTHING' //|| that.cfg.availability == 'UNKOWN' || that.cfg.availability == 'UNKNOWN_NOTHING'
//							){
//							color = 'gray';
//						}
						
						row.text = "<span class='w-ico w-" + color + "ico'/><span title='" + row.text + "'>" + row.text + "</span>";
						newData.push({id:row.id,text:row.text,attributes:row});
						if(i == 0){
							that.clickTree(newData[0], prefMetricDom);
						}
					}
					return newData;
				},
				onBeforeLoad : function(node, param){
					oc.ui.progress();
				},
				onLoadSuccess : function(node, data){
					$.messager.progress('close');
					prefMetricDom.find('.tree-icon').each(function(){
						var treeIcon = $(this);
						treeIcon.removeClass();
					});
					$(prefMetricDom.find("ul > li").get(0)).find(".tree-node").addClass('tree-node-selected');
				},
				onLoadError : function(arguments){
					$.messager.progress('close');
				},
				onClick : function(node){
					that.clickTree(node, prefMetricDom);
				}
			});
			
			return prefMetricDom;
		},
		createInfoMetric : function(tabsDom){
			var that = this;
			var infoMetricDom = $("<div/>").addClass('infoMetric').attr('title', '信息指标');
			var detailDom = $("<div/>").addClass('detail');
			infoMetricDom.append(detailDom);
			tabsDom.append(infoMetricDom);

			// 信息指标
			var detailDg = infoMetricDom.find(".detail");
			that._datagrid = datagrid = oc.ui.datagrid({
				selector : detailDg,
				fit : true,
				url : oc.resource
						.getUrl("portal/resource/resourceDetailInfo/getMetric.htm?dataType=json" +
								"&instanceId=" + that.cfg.instanceId
								+ "&metricType=InformationMetric"),
				columns : [ [
						{
							field : 'text',
							title : '指标名称',
							width : '300px'
						},
						{
							field : 'currentVal',
							title : '当前值',
							width : '558px',
							ellipsis : true
						}, {
							field : 'lastCollTime',
							title : '最近采集时间',
							width : '200px'
						} ] ],
				loadFilter : function(data){
					var rows = new Array();
					for(var i = 0; i < data.data.rows.length; i ++){
						var row = data.data.rows[i];
						if(!row.isTable){
							var currentVal = row.currentVal;
							row.currentVal = (currentVal != null && currentVal != undefined) ? currentVal : "--";
							var lastCollTime = row.lastCollTime;
							row.lastCollTime = !!lastCollTime ? lastCollTime : "--";
							rows.push(row);
						}
					}
					data.data.rows = rows;
					return data.data;
				},
				onLoadSuccess:function(data){
					that.updateTableWidth(datagrid);
				},
				fitColumns : false,
				pagination : false,
				singleSelect : true
			});
			
			return infoMetricDom;
		},
		createAvailabilityMetric : function(tdDom, tdData, tdSize){
			var that = this;
			var datagridDom = this.createPanel(tdDom, tdData, tdSize);
			
			var availabilityMetricDom = $("<div/>").addClass('availabilityMetric').attr('title', '可用性指标');
			var detailDom = $("<div/>").addClass('detail');
			availabilityMetricDom.append(detailDom);
			datagridDom.append(availabilityMetricDom);

			// 信息指标
			var detailDg = availabilityMetricDom.find(".detail");
			var datagrid = oc.ui.datagrid({
				selector : detailDg,
				fit : false,
				height : '360px',
				url : oc.resource
						.getUrl("portal/resource/resourceDetailInfo/getMetric.htm?dataType=json" +
								"&instanceId=" + that.cfg.instanceId
								+ "&metricType=AvailabilityMetric"),
				columns : [ [
						{
							field : 'text',
							title : '指标名称',
							width : '300px',
							formatter:function(value, row, rowIndex){
								//var colorAndTitle = that.returnStatusLight(row.status, 'metric');
								//colorAndTitle.attr('title', value).html(value);
								return value;
							}
						},
						{
							field : 'currentVal',
							title : '当前值',
							width : '558px',
							formatter:function(value, row, rowIndex){
								if("1" == value){
									value = '可用';
								}else if("0" == value){
									value = '不可用';
								}else if("" == value){
									value = '--';
								}
								return value;
							}
						}, {
							field : 'lastCollTime',
							title : '最近采集时间',
							width : '200px'
						} ] ],
				loadFilter : function(data){
					var rows = new Array();
					for(var i = 0; i < data.data.rows.length; i ++){
						var row = data.data.rows[i];
						if(!row.isTable){
							var currentVal = row.currentVal;
							row.currentVal = (currentVal != null && currentVal != undefined) ? currentVal : "--";
							var lastCollTime = row.lastCollTime;
							row.lastCollTime = !!lastCollTime ? lastCollTime : "--";
							rows.push(row);
						}
					}
					data.data.rows = rows;
					return data.data;
				},
				onLoadSuccess:function(data){
					that.updateTableWidth(datagrid);
				},
				fitColumns : false,
				pagination : false,
				singleSelect : true
			});
			
			return availabilityMetricDom;
		},
		clickTree : function(node, prefMetricDom){
			var that = this;
			var status = prefMetricDom.find(".status");
			var currentVal = prefMetricDom.find(".currentVal");
			var lastCollTime = prefMetricDom.find(".lastCollTime");
			var thresholds = prefMetricDom.find(".thresholds");
			var colorAndTitle = this.returnStatusLight(node.attributes.status, 'metric');
			status.empty();
			if(!node.attributes.isAlarm){
				status.append($("<div/>").html("未设置告警"));
			}else{
				status.append(colorAndTitle);
			}
			currentVal.html('').html(node.attributes.currentVal);
			lastCollTime.html('').html(node.attributes.lastCollTime);
			// 阈值
			thresholds.empty();
			if(typeof(node.attributes.thresholds) != 'undefined'){
				thresholds.append($("<div/>").width('140px').target(eval(node.attributes.thresholds), true));
			}
			// highcharts
			this.chartObj.setIds(node.id, this.cfg.instanceId);
		},
		createHigchar : function(container){
			var highChartsId = 'general_metric_data_chart';
			var outerDom = $("<div/>").width('100%').height('100%');
			
			var innerBtnDom1 = $("<div/>").attr('id','metricChartsShowValues')
			.html('<span><font class="resouse-txt-color">Max</font> : </span><span name="valueMax" ></span><span>&nbsp;&nbsp;</span>'+
					'<span><font class="resouse-txt-color">Min</font> : </span><span name="valueMin" ></span><span>&nbsp;&nbsp;</span>'+
					'<span><font class="resouse-txt-color">Avg</font> : </span><span name="valueAvg" ></span>'+
					'<div class="locate-right"><select id="queryTimeType" class="btnlistarry locate-center"></div></select>');
			
			var innerBtnDom = $("<div/>").addClass('btnlistarry locate-right').attr('align', 'right').attr('style', 'z-index:999;').attr('id','metricChartsUl');
			var btnArray = ['1H', '1D', '7D', '30D', '自定义'];
			var btnClassArray = ['1H', '1D', '7D', '30D', 'dateSelect'];
			var btnDom = $("<ul/>");
//			for(var i = 0; i < btnArray.length; i ++){
//				var btn = btnArray[i];
//				var btnClass = btnClassArray[i];
//				btnDom.append($("<li/>").addClass(btnClass).attr('name', 'chartsType' + highChartsId).html(btn));
//			}
			var innerHigDom = $("<div/>").attr('id', highChartsId).width('100%').height('120px');
			container.append(outerDom.append(innerBtnDom1).append(innerBtnDom).append(innerHigDom));
			this.chartObj = new chartObj(innerHigDom, 1);
		},
		createChildPanel : function(tabsDom, childType){
			var that = this;
			var panel = $("<div/>").addClass('perfMetric').attr('title', '面板图'); //this.createPanel(tdDom, tdData, tdSize);
			tabsDom.append(panel);
			var leftDom = $("<div/>"), leftUpDom = $("<div/>"), leftDownDom = $("<div/>");
			leftDom.append(leftUpDom).append(leftDownDom);
//			this.rightDom = $("<div/>");
			panel.append(leftDom);
//			.append(this.rightDom);
			
			leftDom.addClass("intro").css('float', 'left').width('100%').height('166px');
			leftUpDom.addClass("intro").css('overflow-y', 'auto').css('border', 'none').width('100%').height('117px');
			leftDownDom.addClass("intro intro-bgcolor").width('100%').height('30px');
//			this.rightDom.css('float', 'right').css('overflow-y', 'auto').css('display','none').width('20%').height('166px').addClass("intro");
			
//			var state = ['green', 'green-red', 'red', 'gray', 'white'];
//			var stateName = ['UP', '协议 DOWN', '管理 DOWN', '未知', '未监控'];
			
			var state = ['green', 'green-red', 'red', 'white'];
			var stateName = ['UP', '协议 DOWN', '管理 DOWN', '未监控'];
			
			for(var i = 0; i < state.length; i ++){
				var stateNode = $("<div/>").css('float', 'left').css('cursor', 'pointer').attr('state', state[i]).width('120px')
								.append($("<span/>").addClass("interface-eq interface-" + state[i]))
								.append($("<span/>").addClass('oc-spanlocate').height('100%').html(stateName[i]));
				leftDownDom.append(stateNode);
			}
			leftDownDom.append($("<div/>").addClass('locate-right')
				.append("<span><input class='logicInterfaceOnOff' type='checkbox' checked=checked style='vertical-align:middle;'></span>").append("<span>显示逻辑接口</span>"));
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/resourceDetailInfo/getChildInstance.htm'),
				data : {
					instanceId : this.cfg.instanceId,
					childType : childType
				},
				successMsg:null,
				success : function(json) {
					if (json.code == 200) {
						var childArray = [], noLogicInterfaceArray = [], noLogicInterfaceNo = 0;
						for(var i = 0 ; i < json.data.length; i ++){
							var child = json.data[i];
							child.no = i;
							childArray.push(child);
							if(child.ifType == 'ethernetCsmacd' || child.ifType == 'gigabitEthernet' || child.ifType == 'fibreChannel'){
								child.noLogicNo = noLogicInterfaceNo ++;
								noLogicInterfaceArray.push(child);
							}
							// 默认显示第一个接口指标信息
//							if(i == 0){
								// that.childResourceEvent(child.id, child.ifIndex);
//							}
						}
						// 加入子资源
						that.addChild(leftUpDom, childArray, false);
						// 新增接口状态点击事件
						// that.addChildClickEvent(leftUpDom, leftDownDom, childArray, false);
						// 接口状态记数
						that.countInterfaceState(childArray);
						// 修改相应状态总数
						that.updateStateNodeCount(leftDownDom, state, stateName);
						// 过滤逻辑接口事件
						leftDownDom.find(".logicInterfaceOnOff").on('click', function(e){
							if($(this).is(":checked")){
								// 接口状态记数
								that.countInterfaceState(childArray);
								// 修改相应状态总数
								that.updateStateNodeCount(leftDownDom, state, stateName);
								that.addChild(leftUpDom, childArray, false);
							}else{
								// 接口状态记数
								that.countInterfaceState(noLogicInterfaceArray);
								// 修改相应状态总数
								that.updateStateNodeCount(leftDownDom, state, stateName);
								that.addChild(leftUpDom, noLogicInterfaceArray, true);
							}
						});
					} else {
						alert('没有查询到对应的资源信息');
						return false;
					}
				}
			});
		},
		countInterfaceState : function(childArray){
			var that = this;
			that.child_up = 0;
			that.child_down = 0;
			that.child_unkown = 0;
			that.child_up_down = 0;
			that.child_no_monitor = 0;
			for(var i = 0 ; i < childArray.length; i ++){
				var child = childArray[i];
				switch (child.state) {
				case 'NOT_MONITORED':
					that.child_no_monitor++;
					break;
				case 'ADMNORMAL_OPERCRITICAL':
					that.child_up_down++;
					break;
				case 'CRITICAL':
				case 'CRITICAL_NOTHING':
					that.child_down++;
					break;
//				case 'UNKOWN':
//				case 'UNKNOWN_NOTHING':
//					that.child_unkown++;
//					break;
				default:
					that.child_up++;
					break;
				}
			}
		},
		updateStateNodeCount : function(leftDownDom, state, stateName){
			var that = this;
			for(var i = 0; i < state.length; i ++){
				var stateNode = leftDownDom.find("div[state='" + state[i] + "']").find('.oc-spanlocate');
				var count = 0;
				if(state[i] == 'green'){
					count = that.child_up;
				}else if(state[i] == 'red'){
					count = that.child_down;
				}else if(state[i] == 'gray'){
					count = that.child_unkown;
				}else if(state[i] == 'green-red'){
					count = that.child_up_down;
				}else{
					count = that.child_no_monitor;
				}
				stateNode.html(stateName[i] + " [" + count + "]");
			}
		},
		createDownDevice : function(subInstanceId, ifIndex){
			var that = this;
			oc.resource.loadScript("resource_new/module/topo/backboard/DownDeviceDia.js",function(){
				$.post(oc.resource.getUrl("topo/backboard/downinfo.htm"),{
					subInstanceId:subInstanceId,
					mainInstanceId:that.cfg.instanceId
				},function(result){
					new DownDeviceDia({
						ip:result.ip,		//上联 设备ip
						intface:result.IfName,//上联设备接口
						deviceName:result.deviceShowName,//上联设备名称
						intfaceIndex:ifIndex	//接口索引
					});
				},"json");
			});
		},
		createChildrenResInfo : function(tdData, name){
			var div = $("<div/>").addClass("Storage").append("<h6>" + name + "</h6>");
			var ul = $("<ul/>");
			div.append(ul);
		},
		createChildResInfo : function(tdData, ul){
			if(tdData == undefined){
				return false;
			}
			var li = $("<li/>");
			var icoDiv = $("<div/>").addClass("stroage_img");
			var desDiv = $("<div/>").addClass("stroage_txt");
			switch (tdData.type) {
			case "StorageProcessorSystem":
				icoDiv.addClass("controller-l");//3
				break;
			case "Node":
				icoDiv.addClass("controller-l");//3
				break;
			case "FCPort":
				icoDiv.addClass("fiber-l");//5
				break;
			case "DiskDrive":
				icoDiv.addClass("physics-disk-l");//2
				break;
			case "StoragePool":
				icoDiv.addClass("storage-disk-l");//1
				break;
			case "StorageVolume":
				icoDiv.addClass("storage-volume-l");//4
				break;
			case "MDisk":
				icoDiv.addClass("storage-volume-l");//4
				break;
			default:
				break;
			}
			desDiv.append("<p>" + tdData.name + "</p>");
			desDiv.append("<p>总数：" + tdData.count + "</p>");
			desDiv.append("<p>故障：<span>" + tdData.critical + "</span></p>");
			li.append(icoDiv).append(desDiv);
			ul.append(li);
		},
		addChildClickEvent : function(leftUpDom, leftDownDom, childArray, isNoLogicInterface){
			var that = this;
			leftDownDom.find("div").on('click', function(){
				var node = $(this);
				var newDataArray = [], childNo = 0;
				for(var j = 0; j < childArray.length; j ++){
					var child = childArray[j];
					switch (node.attr('state')) {
					case 'green':
						if(child.state != 'NOT_MONITORED' && child.state != 'CRITICAL'
//							&& child.state != 'UNKOWN' && child.state != 'UNKNOWN_NOTHING' 
								&& child.state != 'CRITICAL_NOTHING' && child.state != 'ADMNORMAL_OPERCRITICAL'){
							child.no = childNo ++;
							newDataArray.push(child);
						}
						break;
					case 'green-red':
						if(child.state == 'ADMNORMAL_OPERCRITICAL'){
							child.no = childNo ++;
							newDataArray.push(child);
						}
						break;
					case 'red':
						if(child.state == 'CRITICAL' || child.state == 'CRITICAL_NOTHING'){
							child.no = childNo ++;
							newDataArray.push(child);
						}
						break;
					case 'white':
						if(child.state == 'NOT_MONITORED'){
							child.no = childNo ++;
							newDataArray.push(child);
						}
						break;
					case 'gray':
//						if(child.state == 'UNKOWN' || child.state == 'UNKNOWN_NOTHING'){
//							child.no = childNo ++;
//							newDataArray.push(child);
//						}
						break;
					default:
						break;
					}
				}
				that.addChild(leftUpDom, newDataArray, isNoLogicInterface);
			});
		},
		addChild : function(leftUpDom, childData, isNoLogicInterface){
			leftUpDom.empty();
//			this.rightDom.empty();
//			debugger;
			var groupCnt = Math.ceil(parseInt(childData.length)/6);
			var rowCnt = Math.ceil(parseInt(groupCnt)/4);
			for(var i = 0; i < rowCnt; i ++){
				var rowDom = $("<div/>").addClass('row').width('100%').height('31px');
				for(var j = 0; j < 4; j ++){
					this.addChildGroup(rowDom, i, j, childData, isNoLogicInterface);
				}
				leftUpDom.append(rowDom);
			}
		},
		addChildGroup : function(rowDom, rowNo, groupNo, childData, isNoLogicInterface){
			var that = this;
			var groupDom = $("<div/>").addClass('group').css('float', 'left').width('190px').height('100%');
			var startNo = (rowNo * 4 * 6) + (groupNo * 6);
			var endNo = Math.min((startNo + 6), childData.length);
			for(var i = startNo; i < endNo; i ++){
				var child = childData[i];
				var childDom = $("<span/>").css('cursor', 'pointer').addClass("interface-eq");
				switch (child.state) {
				case 'NOT_MONITORED':
					childDom.addClass("interface-white").attr('no', child.no);
					break;
				case 'ADMNORMAL_OPERCRITICAL':
					childDom.addClass("interface-green-red").attr('no', child.no);
					break;
				case 'CRITICAL':
				case 'CRITICAL_NOTHING':
					childDom.addClass("interface-red").attr('no', child.no);
					break;
//				case 'UNKOWN':
//				case 'UNKNOWN_NOTHING':
//					childDom.addClass("interface-gray").attr('no', child.no);
//					break;
				default:
					childDom.addClass("interface-green").attr('no', child.no);
					break;
				}
				if(child.noLogicNo != undefined){
					childDom.attr('noLogicNo', child.noLogicNo);
				}
//				childDom.tooltip({
//					position : 'right',
//					content : child.name
//				});
//				childDom.attr('title', child.name);
				groupDom.append(childDom);
			}
			//初始化右键menu
			$('#portMenu').menu({
				onClick:function(item){
					var condition = '';
					var instanceId = $(item.target).attr('instanceid');
					if(item.text.indexOf('打开') > -1){
						condition = 'ifSetAdminUp';
					}else if(item.text.indexOf('关闭') > -1){
						condition = 'ifSetAdminDown';
					}
					oc.util.ajax({
						url:oc.resource.getUrl("portal/resource/resourceDetailInfo/editPortStatus.htm"),
						data:{
							instanceId:instanceId,
							condition:condition
						},
						success:function(data){
							if(data.data==1){
								alert('操作成功！');
							}else{
								alert('操作失败！');
							}
						}
					});
				},
			});
			groupDom.find('span').each(function(){
				var interDom = $(this);
				var no = isNoLogicInterface ? parseInt(interDom.attr('noLogicNo')) : parseInt(interDom.attr('no'));
				interDom.on('mousedown', function(e){
					if(1==e.which){	//左键点击打开详情列表
						that.childResourceEvent(childData[no].id, childData[no].ifIndex);
					}else if(3==e.which){	//右键菜单
						var user = oc.index.getUser();
						if(user.systemUser){
							$(document).bind('contextmenu.interface-eq',function(e){
								e.preventDefault();
								$('#portMenu').find('#menu_open').attr('instanceId',childData[no].id);
								$('#portMenu').find('#menu_close').attr('instanceId',childData[no].id);
								$('#portMenu').menu('show',{
									left:e.pageX,
									top:e.pageY
								});
								$(document).unbind(".interface-eq");
							});
						}
					}
				});
				interDom.on('mouseover', function(e){
					if(that.tipTimeId){
						clearTimeout(that.tipTimeId);
					}
					that.tipTimeId=null;
					that.tipTimeId=setTimeout(function(){
						oc.topo.tips.show({
							type:"port",
							x:e.pageX,
							y:e.pageY-215*1.9,
							value:{
								instanceId:childData[no].id,
								ifIndex:childData[no].ifIndex
							}
						});
						that.tipTimeId=null;
					},500);
				});
				interDom.mouseout(function(){
					if(that.tipTimeId){
						clearTimeout(that.tipTimeId);
						that.tipTimeId=null;
					}
					oc.topo.tips.hide();
				});
			});
			if(groupDom.find(".interface-eq").length > 0)
				rowDom.append(groupDom);
		},
		createPie : function(tdDom, tdData, tdSize){
			var pie = this.createPanel(tdDom, tdData, tdSize);
			pie.css("height","90%");
			var unit = tdData.unit == undefined ? "" : tdData.unit;
			var tmpValue = [];
			var value = tdData.value;
		if(value==undefined || value==null || value=="" || value.length==0){
			pie.html("<div class='otherTabTip'>抱歉，没有可展示的数据！</div>");
			return ;
		}else{
			
		}
			for(var i = 0; i < tdData.value.length; i++){
				var onePoint = tdData.value[i];
				tmpValue.push([onePoint[0], parseFloat(onePoint[1])]);
				
			}
			value = tmpValue;
			
			var title = tdData.resourceId == 'IBMDS' && tdData.totalSpace != undefined ? {text:tdData.totalSpace} : null;
			 pie.highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: title
        },
		legend: {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'middle',
		            itemStyle : {
		            	"color" : Highcharts.theme.pieaxisLabelColor,
		            	"font-size" : "12px"
		            },
		            itemHoverStyle:{
		            	color:Highcharts.theme.pieaxisLabelColor
		            },
		           navigation: {
		                activeColor: Highcharts.theme.pieactiveColor,
		                animation: true,
		                arrowSize: 12,
		                inactiveColor: Highcharts.theme.pieinactiveColor,
		                style: {
		                    "font-weight" : 'bold',
		                    "color" :Highcharts.theme.pieFontColor,
		                    "font-size" : '12px'
		                }
		            },
		      //      useHTML:true,
		            labelFormatter : function(){
		            	var name = this.name;
		            	if(name.length > 10){
		            		name = name.substring(0, 9) + "...";
		            	}
		            	// return "<span title='"+this.name+"' >"+name+"</span>";
		            	return name;
		            }
		        },
        tooltip: {
           enabled: true,
           useHTML:true,
					formatter : function(){
						var y = this.y;
						var newUnit = unit;
						if(unit == 'Byte'){
							if(this.y > (1024 * 1024 * 1024)){
								y = (parseFloat(y) / (parseFloat(1024) * parseFloat(1024) * parseFloat(1024))).toFixed(2);
								newUnit = 'GB';
							}else if(this.y > (1024 * 1024)){
								y = (parseFloat(y) / (parseFloat(1024) * parseFloat(1024))).toFixed(2);
								newUnit = 'MB';
							}else if(this.y > 1024){
								y = (parseFloat(y) / parseFloat(1024)).toFixed(2);
								newUnit = 'KB';
							}
						}
					    $("div.highcharts-tooltip span").css("white-space", "inherit");//允许换行
		                //重新生成
		               
		               var content = '<div style="font-size: 10px;width: 200px;display:block;word-break: break-all;word-wrap: break-word;">' + this.key+'：'+y+newUnit +'<br/>' 
		               +'</div>';
		               return content;
						//return this.key + ":"+"<br/>"+ y + newUnit;
					}
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            name: '',
               data: value
        }]
    });
			/*pie.highcharts({
				chart: {
					type : 'pie',
		            backgroundColor: 'rgba(0,0,0,0)',
		            spacing: [0, 0, 0, 0],
		            options3d: {
		                enabled: true,
		                alpha: 45,
		                beta: 0
		            }
				},
				title : title,
				legend: {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'middle',
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
		            },
		            labelFormatter : function(){
		            	var name = this.name;
		            	if(name.length > 6){
		            		name = name.substring(0, 5) + "...";
		            	}
		            	return name;
		            }
		        },
		        plotOptions: {
					pie : {
						innerSize : '5',
						showInLegend: true,
						depth: 35,
						borderWidth : 0,
						dataLabels : {
							enabled : false
						}
					}
		        },
		        series : [{
		            type: 'pie',
		            data: value
		        }],
				tooltip : {
					enabled: true,
					formatter : function(){
						var y = this.y;
						var newUnit = unit;
						if(unit == 'Byte'){
							if(this.y > (1024 * 1024 * 1024)){
								y = (parseFloat(y) / (parseFloat(1024) * parseFloat(1024) * parseFloat(1024))).toFixed(2);
								newUnit = 'GB';
							}else if(this.y > (1024 * 1024)){
								y = (parseFloat(y) / (parseFloat(1024) * parseFloat(1024))).toFixed(2);
								newUnit = 'MB';
							}else if(this.y > 1024){
								y = (parseFloat(y) / parseFloat(1024)).toFixed(2);
								newUnit = 'KB';
							}
						}
						return this.key + " : " + y + newUnit;
					}
				},
				credits : {
					enabled: false
				},
	    	    exporting : {
	    	    	enabled: false
	    	    }
			});*/
		},
		createChildTable : function(tdDom, tdData, tdSize){
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var table = $("<table/>").css({'width':'100%'});
			childTable.addClass('oc-wbg').css({"overflow-y":"auto"}).append(table);
			table.append("<tr style='height:30px;text-align:center'><th>存储资源</th><th>状态</th><th>容量</th><th>可用空间</th></tr>");
			for(var i = 0; tdData.value != null && i < tdData.value.length; i++){
				var value = tdData.value[i];
				var name = "<span class='ico ico_vdata ico-vdatatext' title='" + value.Name + "' style='cursor:auto;'>" + value.Name + "</span>";
//				var name = "<span class='ico ico_vdata' title='"+value.Name+"' style='width:120px;cursor:auto;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;'>" + value.Name + "</span>";
				var status = value.availabilityStatus == 'CRITICAL' ? "<span>不可用</span>" : "<span class='ico ico_vright' style='cursor:auto;'>正常</span>";
				table.append("<tr style='height:30px;text-align:center'><td>" + name + "</td><td>" + status + "</td><td>"
						+ value.DataStorageVolume + "</td><td>" + value.DataStorageFreeSpace + "</td></tr>");
			}
		},
		createEnvironmentPanelDiv:function(tdDom, tdData, tdSize){

			var childTable=this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({'width':'100%'});
			
			var values = tdData.value;
			if(values==undefined || values==null || values==""){
				childTable.addClass('oc-wbg').css({"overflow-y":"auto"}).append("<div class='otherTabTip'>抱歉，没有可展示的数据！</div>");
				return;
			}
		values=[{"metricStr":"rtcBatteryStatus","metricStatus":"CRITICAL"},
			        {"metricStr":"temperatureStatus","metricStatus":"CRITICAL"},
			        {"metricStr":"voltagesStatus","metricStatus":"CRITICAL_NOTHING"},
			        {"metricStr":"fanStatus","metricStatus":"NORMAL"}];
			if(values)
			for(var i=0;i<values.length;i++){
				var temp = values[i];
				var nameP,tempClass="valnet-ico ";
				var metricId = temp['metricStr'];
				var value = temp['metricStatus'];
				switch (metricId) {
				case 'rtcBatteryStatus':
					nameP = "电池";
					if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
						tempClass += "bat-red";
					}else{
						tempClass += "bat-green";
					}
					break;
				case 'temperatureStatus':
					nameP = "温度";
					if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
						tempClass += "temper-red";
					}else{
						tempClass += "temper-green";
					}
					break;
				case 'voltagesStatus':
					nameP = "电压";
					if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
						tempClass += "voltage-red";
					}else{
						tempClass += "voltage-green";
					}
					break;
				case 'fanStatus':
					nameP = "风扇";
					if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
						tempClass += "v-fan-red";
					}else{
						tempClass += "v-fan-green";
					}
					break;
				}
				
				var classTemp = "";
				if(values.length==3){
					classTemp = "margin: 16px 15px 0px 45px";
				}
				var div = $("<div class='video_class' style='"+classTemp+"'/>");
				var span = $("<span />").addClass(tempClass);
				var p = $("<p></p>").html(nameP);
				div.append(span).append(p);
				divPanel.append(div);
			}
			
			childTable.addClass('oc-wbg').css({"overflow-y":"auto","padding":"16px 90px"}).append(divPanel);
			
		
		},
		createEnvironmentPanel : function(tdDom, tdData, tdSize){
			var childTable=tdDom;// = this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({'width':'100%'});
			
			var values = tdData.value;
			if(values==undefined || values==null || values==""){
				childTable.addClass('oc-wbg').css({"overflow-y":"auto"}).append("<div class='otherTabTip'>抱歉，没有可展示的数据！</div>");
				return;
			}
	/*		values=[{"metricStr":"rtcBatteryStatus","metricStatus":"CRITICAL"},
			        {"metricStr":"temperatureStatus","metricStatus":"CRITICAL"},
			        {"metricStr":"voltagesStatus","metricStatus":"CRITICAL_NOTHING"},
			        {"metricStr":"fanStatus","metricStatus":"NORMAL"}];*/
			if(values)
			for(var i=0;i<values.length;i++){
				var temp = values[i];
				var nameP,tempClass="valnet-ico ";
				var metricId = temp['metricStr'];
				var value = temp['metricStatus'];
				switch (metricId) {
				case 'rtcBatteryStatus':
					nameP = "电池";
					if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
						tempClass += "bat-red";
					}else{
						tempClass += "bat-green";
					}
					break;
				case 'temperatureStatus':
					nameP = "温度";
					if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
						tempClass += "temper-red";
					}else{
						tempClass += "temper-green";
					}
					break;
				case 'voltagesStatus':
					nameP = "电压";
					if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
						tempClass += "voltage-red";
					}else{
						tempClass += "voltage-green";
					}
					break;
				case 'fanStatus':
					nameP = "风扇";
					if(value=="CRITICAL" || value=="CRITICAL_NOTHING"){
						tempClass += "v-fan-red";
					}else{
						tempClass += "v-fan-green";
					}
					break;
				}
				
				var classTemp = "";
				if(values.length==3){
					classTemp = "margin: 16px 15px 0px 45px";
				}
				var div = $("<div class='video_class' style='"+classTemp+"'/>");
				var span = $("<span />").addClass(tempClass);
				var p = $("<p></p>").html(nameP);
				div.append(span).append(p);
				divPanel.append(div);
			}
			
			childTable.addClass('oc-wbg').css({"overflow-y":"auto","padding":"16px 90px"}).append(divPanel);
			
		},
		createMetricTabs : function(tdDom, tdData, tdSize){
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var table = $("<table/>").css({'width':'100%'});
			childTable.addClass('oc-wbg').css({"overflow-y":"auto"}).append(table);
			
			var values = tdData.value;
		//	values=[{"metricName":"sd","value":"s"},{"metricName":"sds","value":"ssssssssss"},{"metricName":"sd2","value":"s"},{"metricName":"sd3","value":"s"}];
			if(values && values.length>0){
				table.append("<tr style='height:30px;text-align:center'><th>指标名称</th><th>指标值</th></tr>");
			}
			for(var i=0;i<values.length;i++){
				var temp = values[i];
				var name = temp['metricName'];
				var value = temp['value'];
				
				var nameSpan = "<span style='cursor:auto;'>" + name + "</span>";
//				var nameSpan = "<div title='"+name+"' style='width:120px;cursor:auto;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;'>" + name + "</div>";
//				var status = value.availabilityStatus == 'CRITICAL' ? "<span>不可用</span>" : "<span class='ico ico_vright' style='cursor:auto;'>正常</span>";
				var status = "<span>"+value+"</span>";
				
				table.append("<tr style='height:30px;text-align:center'><td>" + nameSpan + "</td><td>" + status + "</td></tr>");
			}
		},
		createTableMetric : function(tdDom, tdData, tdSize){
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var table = $("<table/>").css({'width':'100%'});

			
			var values = tdData.value;
			if(values==undefined || values==null || values==""){
				childTable.addClass('oc-wbg').css({"overflow-y":"auto"}).append("<div class='otherTabTip'>抱歉，没有可展示的数据！</div>");
			}else{
				childTable.addClass('oc-wbg').css({"overflow-y":"auto"}).append(table);
			}
			if(values && values.length>0){
				var valueTemp = values[0]['value']+'';
				
				if(valueTemp && valueTemp.length>0){
					var valueArr = valueTemp.split(',');
					for(var i=1;i<valueArr.length;i++){
						var value = valueArr[i];
						var name = "<div title='"+value+"' style='width:220px;cursor:auto;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;'>" + value + "</div>";
						table.append("<tr style='height:30px;text-align:left'><td>" + name + "</td></tr>");
					}
				}
				
			}
		},
		childResourceEvent : function(instanceId, ifIndex){
			var that = this;
			this.createDownDevice(instanceId, ifIndex);
			/*
			var that = this;
			that.rightDom.empty();
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource_new/resourceDetailInfo/getAllMetric.htm'),
				data : {
					instanceId : instanceId
				},
				successMsg:null,
				success : function(json) {
					if (json.code == 200) {
						for(var j = 0; j < json.data.length; j ++){
							var metric = json.data[j];
							if(metric.status != 'UNKOWN'){
								that.rightDom.append(metric.text + " : "
										+ (typeof(metric.currentVal) == 'undefined'
											|| metric.currentVal == '' || metric.currentVal == null 
											? '--' : metric.currentVal) + "<br>");
							}
						}
					}
				}
			});
			*/
		},
		createTextPanel: function(tdDom,tdData,tdSize){
			var panelDom = $("<div/>").height('100%');
			var content = $("<div/>");//.width('100%').height('100%');
			var container = $("<div/>").addClass("layout-td-panel-bg");
			tdDom.append(container);
			tdDom.height(tdData.height);
				container.append(panelDom.append(content));
//				container.append(panelDom.append(content));
					panelDom.panel({
							title : "基础信息",
							height : "34%"
//							,headerCls:'panel_kuan'
						});
					return content;
		},
		createPanel : function(tdDom, tdData, tdSize){
	
		


			var width = tdData.type == 'tabs' || tdData.type == 'panel' || tdData.type == 'datagrid'||  tdData.type == 'tabs-full'  ? '100%' : tdData.width=='25%' ? 260 : 320;



	
		
			/*if(tdData.type == 'commonChildrenPanel' || tdData.type == 'commonOneChildrenPanel' || tdData.type == 'commonOneMetricPanel'){
				if(tdData.width=='33%'||tdData.width=='34%'){
					width = '352px';
				}else{
					width = '263px';
				}
			}else if(tdData.type == 'commonMetricPie'){
				width = '263px';
			}else if(tdData.type == 'tabs-full'){
				width = '100%';
			}*/
		//	width = tdData.type == 'pie' && (tdData.resourceId == 'IBMDS' || tdData.resourceId == 'InspurAS' || tdData.categoryId == 'DiskArray') ? '250px' : width;


		//	var height = tdData.type == 'tabs' && tdData.metrics != 'none'  ? 236 : tdData.type=='fan' || tdData.type=='battery' || tdData.type=='cpu' ?260:tdData.type=='alarm'|| (tdData.type=='multiprocess' && tdData.height=='60%') || (tdData.type=='bar' && tdData.height=='60%')?320:tdData.type=='multiMeter'?340: 240;
			//
		//var height = (tdData.type == 'tabs' && tdData.metrics != 'none') || tdData.type == 'panel'   ? 235:tdData.type=='fan' || tdData.type=='battery' || tdData.type=='cpu'?260:tdData.type=='multiMeter'? 255 : tdData.type=='alarm' ? 300:tdData.type=='multiprocess'?215:(tdData.type=='multiMeter' && tdData.height=='40%') ? 330: tdData.type=='multiprocess' ? 255:385;
		var height=385;
		if( (tdData.type == 'tabs' && tdData.metrics != 'none') || tdData.type == 'panel' ){
			height=215;
			/*if(tdData.categoryId=='DiskArray' && tdData.type=='pie'){
				height=220;
			}*/
		}else if(tdData.width=='25%' || tdData.type=='pie'  ){
			height=220;
		}else if(tdData.type=='alarm'){
			height=260;
		}else if((tdData.type=='multiMeter' || tdData.type=='graph' || tdData.type=='table' || tdData.type=='graph' || tdData.type=="node") && tdData.height=='40%' ){
			height=217;
		}else if(tdData.type=='multiprocess' || tdData.type=='bar'){
			if(tdData.height=='60%'){
				height=353;
			}else{
				height=217;
			}
			
		}else if(tdData.type=='multiInterface'){
			height=170;
		}else if( tdData.type=='gauge' || tdData.type=='pie'  ){
			height=220;
		}else if(tdData.type=='mulitpie' || tdData.type=='graph'){
			height=217;
		}else if(tdData.type=='datagrid'){
			height=345;
			tdDom.attr('colspan','3');
		}else if(tdData.type=='others' ){
			if(tdData.height=='35%'){
				height=126;
			}else{
				height=217;
			}
			
		}

		

			if(tdData.type == 'tabs-full'){
				height = 280;
			}
			var titleName = (tdData.name == undefined || tdData.name == '' || tdData.name == null)
							? tdData.title==undefined?"":tdData.title : tdData.name;
			if(tdData.id == 'throughput'){
				titleName += '(最近4小时)';
			}
			if(tdData.length!=undefined){
				var container = $("<div/>").addClass("layout-td-panel-bg");
				tdDom.append(container);
				var panelDom = $("<div/>");
				var contents=$("<div/>").width('100%').height('100%').css({"position":"relative"});
				var childwidth="100%"; var childHeight="100%";var titleShow="";
				if(tdData[0].type=="mulitgraph"){
					if(this.cfg.categoryId=="JDBCModel"){
						titleShow="性能信息";
						height=482;//575;
						width=412;
					 
					}else{
						titleShow="";
					}
					
				}else if(tdData[0].type=="mulitgauge"  ){//|| tdData[0].type=="gauge" 
					//gaugeShowTop
					contents.addClass("gaugeShowTop");
						childwidth="50%";
					height=482;//575;
					width=412;
					titleShow="性能信息";    
				}else if(tdData[0].type=="mulitpie"){
					height=217;
					
					childwidth="120px";
				//	height="220px";
					titleShow=tdData[0].title;//"数据库占用性能";
				}else if(tdData[0].type=="tabPanel"){
					container.css("float","left");
					width=700;
					height=243;
					//width=760;
				}

				for(var i=0;i<tdData.length;i++){
				
			
				var content = $("<div/>").width(childwidth).height(childHeight);
				 
				content.width(tdData[i].width);
				/*	if(tdData[0].type=="tabPanel"){
						if(i==0){
							content.width(tdData[i].width);
						}else if(i==1){
							content.width(tdData[i].width);
						}else{
							content.width(tdData[i].width);
						}
					
				}*/
						content.css("float","left");
						if(tdData[i].id==null && tdData[i].metrics=='none'){
							content.addClass("tabs");
						}else{
							
							content.addClass(tdData[i].id);
						}
						
						if(tdData[0].type=="mulitgauge" || tdData[0].type=="mulitgraph"){//中间td增加单独的样式，作为单独处理背景颜色调试的class
							content.addClass("gaugeShow");
						}
					
						/*	if(tdData.type == 'environmentPanel'){
							width = 440;
					}
					else if(tdData.type == 'metricTabs'){
						var widthNum ;
						if(tdData.width){
							widthNum = tdData.width.replace('%','');
						}
						if(widthNum>30){
							width = 352;
						}else{
							//设置为0,自动分配宽度
							width = 0; 
						}
					}else if(tdData.type == 'tableMetric'){
						width = 0;
					}else if(tdData.type == 'node'){
						width = 352;
					}*/

				//	titleName="";
					if(tdData[i].childtype!='alarm' ){//tabpanel布局下，alarm不形成一个单独的div,而是append in tabpanel
					if(tdData[i].type!='others'){
						contents.append(content);
					}
					}
				
				
				
				
				}
					panelDom.append("<span class='layout-title'>"+titleShow+"</span>");
					container.append(panelDom.append(contents));

				panelDom.css({
				//	title : titleShow,
					"width" : width,
					"height" : height
				});

				return contents;
			}else{
				var container = $("<div/>").addClass("layout-td-panel-bg");
				tdDom.append(container);
	
				var panelDom = $("<div/>");
				var content = $("<div/>").width('100%').height('100%').css({"position":"relative"});
/*
				if(tdData.type == 'environmentPanel'){
						width = 440;
				}
				else if(tdData.type == 'metricTabs'){
					var widthNum ;
					if(tdData.width){
						widthNum = tdData.width.replace('%','');
					}
					if(widthNum>30){
						width = 352;
					}else{
						//设置为0,自动分配宽度
						width = 0; 
					}
				}else if(tdData.type == 'tableMetric'){
					width = 0;
				}else if(tdData.type == 'node'){
					width = 352;
				}else if(tdData.type=='datagrid'){
					height=345;
					
				}*/
				if(tdData.type=='gauge'){
					titleName='性能信息';
				}
				panelDom.append("<span class='layout-title'>"+titleName+"</span>");
				container.append(panelDom.append(content));
				if(tdSize==3 && tdDom[0].cellIndex==1){
				//	panelDom.addClass('panel-content-Cls');
						panelDom.css({
							//title : titleName,
							"width" : width,
							"height" : height
//							,headerCls:'panel_kuan'
						});
				}else{
					if(tdData.type=='fan' || tdData.type=='battery' || tdData.type=='cpu'){
						//height='100%';
					}
				
					panelDom.css({
						//title : titleName,
						"width" : width,
						"height" : height
					});
					
				}
				return content;
			}
		
			
	
		},
		returnColorByStatus : function(status){
			var newStatus = status
//			if(this.cfg.availability == 'CRITICAL' || this.cfg.availability == 'CRITICAL_NOTHING'
//				|| this.cfg.availability == 'UNKNOWN_NOTHING' || this.cfg.availability == 'UNKOWN'){
//				newStatus = 'UNKOWN';
//			}
			var color = Highcharts.theme.resourceDetailMetricColors[0];//'#00C803';
			switch(newStatus){
			case 'CRITICAL':
				color = Highcharts.theme.resourceDetailMetricColors[3];
				break;
			case 'SERIOUS':
				color = Highcharts.theme.resourceDetailMetricColors[2];
				break;
			case 'WARN':
				color = Highcharts.theme.resourceDetailMetricColors[1];
				break;
//			case 'UNKOWN':
//				color = '#797979';
//				break;
			case 'NORMAL':
			case 'NORMAL_NOTHING':
				color = Highcharts.theme.resourceDetailMetricColors[0];
				break;
			default :
				color = Highcharts.theme.resourceDetailMetricColors[0];
					break;
			}
			return color;
		},
		returnStatusLight : function(status, type){
			var newStatus = status
			if(type == 'mainInstance'){
				var color = 'light-ico_resource res_unknown_nothing';
				switch (newStatus) {
				case 'CRITICAL':
					color = "light-ico_resource res_critical";
					break;
				case 'CRITICAL_NOTHING':
					color = "light-ico_resource res_critical_nothing";
					break;
				case 'SERIOUS':
					color = "light-ico_resource res_serious";
					break;
				case 'WARN':
					color = "light-ico_resource res_warn";
					break;
				case 'NORMAL':
				case 'NORMAL_NOTHING':
					color = "light-ico_resource res_normal_nothing";
					break;
				case 'NORMAL_CRITICAL':
					color = "light-ico_resource res_normal_critical";
					break;
				case 'NORMAL_UNKNOWN':
					color = "light-ico_resource res_normal_unknown";
					break;
//				case 'UNKNOWN_NOTHING':
//					color = "light-ico_resource res_unknown_nothing";
//					break;
//				case 'UNKOWN':
//					color = "light-ico_resource res_unkown";
//					break;
				default :
					color = "light-ico_resource res_normal_nothing";
					break;
				}
				var html='<div style="min-width: 25px !important;" class="'+color+'"></div><br>'
				return color;
			}else{
//				if(this.cfg.availability == 'CRITICAL' || this.cfg.availability == 'CRITICAL_NOTHING' || this.cfg.availability == 'UNKOWN' || this.cfg.availability == 'UNKNOWN_NOTHING'){
//					newStatus = 'UNKOWN';
//				}
				var color = Highcharts.theme.resourceDetailMetricColors[0];
				var title = '未知';
				switch (newStatus) {
				case 'CRITICAL':
					color = Highcharts.theme.resourceDetailMetricColors[3];
					title = '致命';
					break;
				case 'SERIOUS':
					color = Highcharts.theme.resourceDetailMetricColors[2];
					title = '严重';
					break;
				case 'WARN':
					color = Highcharts.theme.resourceDetailMetricColors[1];
					title = '警告';
					break;
//				case 'UNKOWN':
//					color = 'gray';
//					title = '未知';
//					break;
				case 'UNKOWN':
				case 'UNKNOWN_NOTHING':
				case 'NORMAL':
					color = Highcharts.theme.resourceDetailMetricColors[0];
					title = '正常';
					break;
				}
				return $("<label/>").css('cssText', 'min-width:25px !important').addClass("light-ico " + color + "light").attr('title', title);
			}
		},
		createTinyTool : function(container){
			var that = this;
			
	/*		if(this.cfg.hasCustomMetric){
				if(this.cfg.resourceType == 'Host' || this.cfg.resourceType == 'NetworkDevice'){
					var customMetric = $("<a class='resource_alarm'/>");
					container.append(customMetric);
					customMetric.linkbutton("RenderLB", {
						text : '自定义指标',
						onClick : function(){
							that.customMetricBtnEvent(that.cfg.instanceId);
						}
					});
				}
			}*/
			
			//创建小工具
			var ResourceCategory = 0;
			if(this.cfg.resourceType == 'Host'){
				ResourceCategory = 1;
			}else if(this.cfg.resourceType == 'NetworkDevice' || this.cfg.resourceType == 'SnmpOthers'){
				ResourceCategory = 2;
			}else if(this.cfg.resourceType == 'Database'){
				ResourceCategory = 3;
			}
			if(ResourceCategory != 0){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool.js',function(){
					oc.module.resmanagement.resdeatilinfo.tinytools.init(ResourceCategory,that.cfg, container);
				});
			}
			
		
			
		},
		alarmBtnEvent : function(instanceId){},
		bizBtnEvent : function(instanceId){},
		customMetricBtnEvent : function(instanceId){},
		updateTableWidth : function(grid){}
	}
	
	//-- 单位转换方法开始 --
	function unitTransform(value,unit){
		var str;
		var valueTemp;
		if(null==value){
			return '--';
		}
		if(isNaN(value)){
			valueTemp = new Number(value.trim());	
		}else{
			valueTemp = new Number(value);
		}
			
		if(isNaN(valueTemp)){
			return value + unit;
		}
		switch(unit){
		case "ms":
		case "毫秒":
			str = transformMillisecond(valueTemp);
			break;
		case "s":
		case "秒":
			str = transformSecond(valueTemp);
			break;
		case "Bps":
			str = transformByteps(valueTemp);
			break;
		case "bps":
		case "比特/秒":
			str = transformBitps(valueTemp);
			break;
		case "KB/s":
			str = transformKBs(valueTemp);
			break;
		case "Byte":
			str = transformByte(valueTemp);
			break;
		case "KB":
			str = transformKB(valueTemp);
			break;
		case "MB":
			str = transformMb(valueTemp);
			break;
		default:
			str = value + unit;
			break;
		}
		return str;
		
	}
	function transformMillisecond(milliseconds){
		var millisecond = milliseconds;
		var seconds = 0, second = 0;
		var minutes = 0, minute = 0;
		var hours = 0, hour = 0;
		var day = 0;
		if(milliseconds > 1000){
			millisecond = milliseconds % 1000;
			second = seconds = (milliseconds - millisecond) / 1000;
		}
		if(seconds > 60){
			second = seconds % 60;
			minute = minutes = (seconds - second) / 60;
		}
		if(minutes > 60){
			minute = minutes % 60;
			hour = hours = (minutes - minute) / 60;
		}
		if(hours > 24){
			hour = hours % 24;
			day = (hours - hour) / 24;
		}
		var sb = "";
		sb = day > 0 ? (sb+=(day + "天")) : sb;
		sb = hour > 0 ? (sb+=(hour + "小时")) : sb;
		sb = minute > 0 ? (sb+=(minute + "分")) : sb;
		sb = second > 0 ? (sb+=(second + "秒")) : sb;
		sb = millisecond > 0 ? (sb+=(millisecond + "毫秒")) : sb;
		sb = ""==sb ? (sb+=(millisecond + "毫秒")) : sb;
		return sb;
	}
	
	function transformSecond(seconds){
		var second = seconds;
		var minutes = 0, minute = 0;
		var hours = 0, hour = 0;
		var day = 0;
		if(seconds > 60){
			second = seconds % 60;
			minute = minutes = (seconds - second) / 60;
		}
		if(minutes > 60){
			minute = minutes % 60;
			hour = hours = (minutes - minute) / 60;
		}
		if(hours > 24){
			hour = hours % 24;
			day = (hours - hour) / 24;
		}
		var sb = "";
		sb = day > 0 ? (sb+=(day + "天")) : sb;
		sb = hour > 0 ? (sb+=(hour + "小时")) : sb;
		sb = minute > 0 ? (sb+=(minute + "分")) : sb;
		sb = second > 0 ? (sb+=(second + "秒")) : sb;
		sb = ""==sb.toString() ? (sb+=(second + "秒")) : sb;
		return sb;
	}

	function transformByteps(bpsNum){
		var sb = "";
		var precision = 2;
		if(bpsNum > 1000 * 1000 * 1000){
			sb+=(bpsNum / (1000 * 1000 * 1000)).toFixed(precision) + "GBps";
		}else if(bpsNum > 1000 * 1000){
			sb+=(bpsNum / (1000 * 1000)).toFixed(precision) + "MBps";
		}else if(bpsNum > 1000){
			sb+=(bpsNum / 1000).toFixed(precision) + "KBps";
		}else{
			sb+=bpsNum.toFixed(precision) + "Bps";
		}
		return sb;
	} 
	function transformBitps(bpsNum){
		var sb = "";
		var precision = 2;
		if(bpsNum > 1000 * 1000 * 1000){
			sb+=(bpsNum / (1000 * 1000 * 1000)).toFixed(precision) + "Gbps";
		}else if(bpsNum > 1000 * 1000){
			sb+=(bpsNum / (1000 * 1000)).toFixed(precision) + "Mbps";
		}else if(bpsNum > 1000){
			sb+=(bpsNum / 1000).toFixed(precision) + "Kbps";
		}else{
			sb+=bpsNum.toFixed(precision) + "bps";
		}
		return sb;
	} 
	
	function transformKBs(KBsNum){
		var sb = "";
		var precision = 2;
		if(KBsNum > 1024 * 1024){
			sb+=(KBsNum / (1024 * 1024)).toFixed(precision) + "GB/s";
		}else if(KBsNum > 1024){
			sb+=(KBsNum / 1024).toFixed(precision) + "MB/s";
		}else{
			sb+=KBsNum.toFixed(precision) + "KB/s";
		}
		return sb;
	}
	
	function transformByte(byteNum){
		var sb = "";
		var precision = 2;
		
		if(byteNum > 1024 * 1024 * 1024){
			sb+=(byteNum / (1024 * 1024 * 1024)).toFixed(precision) + "GB";
		}else if(byteNum > 1024 * 1024){
			sb+=(byteNum / (1024 * 1024)).toFixed(precision) + "MB";
		}else if(byteNum > 1024){
			sb+=(byteNum / 1024).toFixed(precision) + "KB";
		}else{
			sb+=byteNum.toFixed(precision) + "Byte";
		}
		return sb;
	}
	
	function transformKB(KBNum){
		var sb = "";
		var precision = 2;
		
		if(KBNum > 1024 * 1024){
			sb+=(KBNum / (1024 * 1024)).toFixed(precision) + "GB";
		}else if(KBNum > 1024){
			sb+=(KBNum / 1024).toFixed(precision) + "MB";
		}else{
			sb+=KBNum.toFixed(precision) + "KB";
		}
		return sb;
	}
	
	function transformMb(mbNum){
		var sb = "";
		var precision = 2;
		
		if(mbNum > 1024){
			sb+=(mbNum / 1024).toFixed(precision) + "GB";
		}else{
			sb+=mbNum.toFixed(precision) + "MB";
		}
		return sb;
	}
	//-- 单位转换方法结束 --
	
	oc.ns('oc.module.resmanagement.resdeatilinfo');
	oc.module.resmanagement.resdeatilinfo.general = {
		open : function(cfg) {
			var gen = new general(cfg);
			gen.open();

		
			
		},
		unitTransform : function(value,unit){
			return unitTransform(value,unit);
		}
	}
});
function clickInfo(){
	alert("clicjkkk");
}