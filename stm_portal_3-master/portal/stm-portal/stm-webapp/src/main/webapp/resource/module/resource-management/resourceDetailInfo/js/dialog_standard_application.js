(function($){
	oc.ns('oc.resource.management.dialog.standard.application');
	var instanceId,callback;
	function init(){
		var dlg = $('<div/>');
		createPage(dlg);
		
		oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/js/resourceCommonChart.js",function(){
		});

		dlg.dialog({
		    title: '详细信息',
		    width : 837,
			height : 425,
			buttons:[{
		    	text: '关闭',
		    	handler:function(){
		    		if(callback){
		    			callback.reLoadAll();
		    		}
		    		dlg.dialog('destroy');
		    	}
		    }],
		    onClose:function(){
		    	callback.reLoadAll();
		    	dlg.dialog('destroy');
		    },
		    onLoad:function(){
		    	
		    }
		});
		
		return true;
	}
	function initByBiz(div){
		createPage(div);
	}
	
	function createPage(dlg){
		var chartObj1;
		var that = this;
		var div = $('<div/>').attr('class','datagridDiv');
		dlg.append(div);
		//Chart图
		var hicharDom = $("<div/>").addClass('hichar').width('100%');
		var highChartContentDom = $("<div/>").addClass('highChartContent').width('100%').height('80%');

		//收缩
		var chartTitleDiv = $("<div/>").addClass('w-table-title margin-bottom5').height('28px;').attr('id','metric_data_chart_title').attr('title','点击展开或收缩!').html(
				'<span class="tablelefttopico fa fa-caret-down paddingr5"></span>'+
				'<span id="infoCharts" class="color-green w-title-n">信息图表</span>'
			);

		//时间查询
		var metricChartsShowValuesDiv = $("<div/>").attr('id','metricChartsShowValues').html(
			'<span style="margin: 5px;"><font class="resouse-txt-color">最大值</font> : </span><span name="valueMax" ></span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>'
			+'<span style="margin: 5px;"><font class="resouse-txt-color">最小值</font> : </span><span name="valueMin" ></span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>'
			+'<span style="margin: 5px;"><font class="resouse-txt-color">平均值</font> : </span><span name="valueAvg" ></span>'	
		);
		var customTimeDiv = $("<div/>").addClass('locate-right').html('<span id="customTimeName" >自定义 </span>');
		var queryTimeDiv = $("<div/>").addClass('locate-right').html('<select id="queryTimeType" ></select>');
		
		metricChartsShowValuesDiv.append(customTimeDiv).append(queryTimeDiv);
		//highChartContentDom.append(metricChartsShowValuesDiv);
		hicharDom.append(chartTitleDiv).append(metricChartsShowValuesDiv).append(highChartContentDom);
		//hicharDom.append(highChartContentDom);
		hicharDom.attr('style','display:none;');
		// HIGHCHART
		//createHigchar(div.find(".hichar > .highChartContent"));
		var tdData = [];
				
		oc.util.ajax({
			successMsg:null,
			url:oc.resource.getUrl('portal/resource/resourceApply/getStandardApplicationHeaderInfo.htm'),
			data:{instanceId:instanceId},
			success:function(data){
				if(null == data.data||undefined == data.data||0==data.data.length ){
					//无指标信息
					alert('无指标信息!');
				}else{
					var dataLen = data.data.length;
					var datagridColumus = [dataLen+1];
					
					//设置数据表格是否自适应宽度
					var showDatagridScroll = true;
					//定义固定长度宽度
					var widthNum = 140;
					
					if(dataLen>6){
						showDatagridScroll = false;
					}
					
					datagridColumus[dataLen] = {field:'currentState',title:'即时状态',width:widthNum,align:'left',sortable:true,formatter:function(value,row,rowIndex){
						 
						   return '<span name="currentState" ></span><div name="currentState"  class="check-bg">检测</div>';//check-bg  ico ico-testing
					  }};
					
					for(var i=0; i<dataLen; i++){
						  var dataRow = data.data[i];
						//避免dataRow.name过长,修改datagrid列长  按一个字2个长度,一个长度7px动态修改列长
						  var nameLengthInside = getStrLength(dataRow.name);
						  if(nameLengthInside>22){
							  widthNum = nameLengthInside*7;
						  }
						  
						  if(null!=dataRow.style && "PerformanceMetric"==dataRow.style){
							  
							  datagridColumus[i] = {field:dataRow.metricId,title:dataRow.name,width:widthNum,sortable:true,formatter:function(value,row,rowIndex){
								  if(undefined==value || null==value || ''==value){
									  return "--";
								  }else{
									 return '<a  title="' + value + '" ><span metric="'+this.field+'" instanceId="'+row.instanceid+'" class="ico ico-chart margin-top-seven show_charts">'+value+'</span></a>';
								  }
							  }};
						  }else if(null!=dataRow.style && "AvailabilityMetric"==dataRow.style){
							  datagridColumus[i] = {field:dataRow.metricId,title:dataRow.name,width:widthNum,sortable:true,formatter:function(value,row,rowIndex){

								  if(undefined==value || null==value || ''==value){
									  return "--";
								  }else{
									  var stateStr = '';
									  switch (value) {
										case 'CRITICAL':
										case 'CRITICAL_NOTHING':
											stateStr = '不可用';
											break;
										case 'NORMAL':
										case 'NORMAL_NOTHING':
											stateStr = '可用';
											break;
										default :
//											stateStr = '未知';
											stateStr = '可用';
										    break;
										}
									  return stateStr;
								  }
							  }};
						  }else if(null!=dataRow.style && "InformationMetric"==dataRow.style){
//							  datagridColumus[i] = {field:dataRow.metricId,title:dataRow.name,width:100,formatter:function(value,row,rowIndex){
//								  
//								  return '<span title="'+value+'">'+value+'</span>';
//							  }};
							  // 如果是显示名称
							  if('resourceShowName'==dataRow.metricId){
								  datagridColumus[i] = {
										  field : dataRow.metricId,
										  title : dataRow.name,
										  width : widthNum +45,
										  formatter : function(value,row,rowIndex){
											  return "<div style='position:relative;'>" +
											  			"<div class='light-ico_resource res_critical' style='float:left' ></div>"+
												  		 "<div class='standardShowName generalShowName' style='float:left;' title='" + value + "'>"
												  			+ value +
												  		 "</div>" +
												  	     "<input name='editShowName' type='text' style='float:left;width:75px;display:none;'>" +
												  		 "<span style='float:right;margin-right:5px;top:3px;' class='uodatethetext fa fa-edit editShowNameBtn'></span>" +
											  		 "</div>";
										  }
								  };
							  }else if('resourceNameAndState'==dataRow.metricId){
								  datagridColumus[i] = {field:dataRow.metricId,title:dataRow.name,width:widthNum+55,ellipsis:true,formatter:function(value,row,rowIndex){
									  /*
									  var str = '';
									  if(null!=value && value.length>30){
										  str = value.substring(0,25)+'......';
										  return '<span title="'+value+'">'+str+'</span>';
									  }else{
										  return value;
									  }
									  */
									  if(undefined==row.resourceState || null==row.resourceState){
										  if(undefined==row.resourceName || null==row.resourceName){
											  return '<span >--</span>';
										  }else{
											  var name=row.resourceName;
											  if(row.categoryId=="Ping"){
												  if(row.resourceName.length>7){
												  name=row.resourceName.substring(0,7)+'...';
												  }
												  }else{
													if(row.resourceName.length>11){
														var name=row.resourceName.substring(0,11)+'...';
													}  
												  }
											  return '<span title="'+row.resourceName+'">'+name+'</span>';
										  }
									  }else{
										  var color = '';
										  switch (row.resourceState) {
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
//											case 'UNKNOWN_NOTHING':
//												color = "light-ico_resource res_unknown_nothing";
//												break;
//											case 'UNKOWN':
//												color = "light-ico_resource res_unkown";
//												break;
											default :
//												color = "light-ico_resource res_unknown_nothing";
												color = "light-ico_resource res_normal_nothing";
												break;
											}
										  
										  if(undefined==row.resourceName || null==row.resourceName){
											  return '<span class="light-ico '+color+'light" >--</span>';
										  }else{
											  //return '<span class="light-ico '+color+'light" title="'+row.resourceName+'">'+row.resourceName+'</span>';
										//	  return '<span style="width:100%;text-overflow: ellipsis;" class="oc-datagrid-spanwdth '+color+' title="'+row.resourceName+'">'+row.resourceName+'</span>';
											  var name=row.resourceName;
											  if(row.categoryId=="Ping"){
												  if(row.resourceName.length>7){
												  name=row.resourceName.substring(0,7)+'...';
												  }
												  }else{
													if(row.resourceName.length>11){
														var name=row.resourceName.substring(0,11)+'...';
													}  
												  }
											  return "<div style='position:relative;'>" +
									  			"<div class='"+color+"' style='float:left' ></div>"+
										  		 "<div class='standardShowName generalShowName' style='float:left;' title='" + row.resourceName + "' categoryId='"+row.categoryId+"'>"
										  			+ name +
										  		 "</div>" +
										  	     "<input name='editShowName' type='text' style='float:left;width:75px;display:none;'>" +
										  		 "<span style='float:right;margin-right:5px;top:3px;' class='uodatethetext fa fa-edit editShowNameBtn'></span>" +
									  		 "</div>";
										  }
									  }
									  
								  }};
							  }else if('Url'==dataRow.metricId){
								  datagridColumus[i] = {
									  field:dataRow.metricId,
									  title:dataRow.name,
									  width:widthNum,
									  formatter:function(value,row,rowIndex){
										  if(value != null && value != undefined && value != '--'){
											  return "<span style='cursor:pointer;' title='" + value + "'>" + value + "</span>";
										  }else{
											  return "<span title='" + value + "'>" + value + "</span>";
										  }
									  }
								  };
							  }else{
								  datagridColumus[i] = {field:dataRow.metricId,title:dataRow.name,width:widthNum,ellipsis:true
//										  ,formatter:function(value,row,rowIndex){
//									  /*
//									  var str = '';
//									  if(null!=value && value.length>30){
//										  str = value.substring(0,25)+'......';
//										  return '<span title="'+value+'">'+str+'</span>';
//									  }else{
//										  return value;
//									  }
//									  */
//									  if(undefined==value || null==value || ''==value){
//										  return '<span >--</span>';
//									  }else{
//										  //return '<span title="'+value+'">'+value+'</span>';
//									  }
//									  
//								  }
								  };
							  }

						  }else{
							  datagridColumus[i] = {field:dataRow.metricId,title:dataRow.name,ellipsis:true,width:widthNum };//hidden:dataRow.isDisplay
						  }
						  widthNum = 140;						  
						}
					
					var datagrid = oc.ui.datagrid({
						selector:dlg.find('.datagridDiv'),
						pagination:false,
						fitColumns:showDatagridScroll,
						url:oc.resource.getUrl('portal/resource/resourceApply/getStandardApplicationMetricInfo.htm?instanceId='+instanceId),
						columns:[datagridColumus],
						onLoadSuccess:function(data){ 
							var dataGrid = datagrid.selector;
							var downObj = dataGrid.parent().find('div[class=table-datanull]');
							downObj.append(hicharDom);
							//downObj.find(".hichar").append(metricChartsShowValuesDiv);
							// 显示名称修改
							var standardShowName = dlg.find('.standardShowName'), editShowName = dlg.find('input[name="editShowName"]');

							editShowName.validatebox({
								required : true,
								validType : 'maxLength[50]'
							});
							dlg.find(".editShowNameBtn").on('click', function(){
								var btn = $(this);
								var allShowName=$(".standardShowName").prop("title");
								var categoryId=$(".standardShowName").prop("categoryId");
								if(btn.hasClass('uodatethetext')){
					    				btn.removeClass('fa-edit').addClass('fa-check');
									btn.removeClass('uodatethetext').addClass('uodatethetextok');
					    			standardShowName.hide();
					    			editShowName.val(allShowName).show();
					    		}else{
					    			if(editShowName.validatebox('isValid')){
					    			btn.removeClass('fa-check').addClass('fa-edit');
					    				var newShowName =editShowName.val();// editShowName.val();
										oc.util.ajax({
											url : oc.resource.getUrl("portal/resource/discoverResource/updateInstanceName.htm"),
											data : {newInstanceName:newShowName,instanceId:instanceId},
											successMsg:null,
											success : function(json){
												if(json.code == 200){
													if(json.data == 0){
														alert('修改显示名称失败！');
													}else if(json.data == 1){
										    			btn.removeClass('uodatethetextok').addClass('uodatethetext');
										    			editShowName.hide();
										    			
										    			  var name=newShowName;
														  if(categoryId=="Ping"){
															  if(newShowName.length>7){
																  name=newShowName.substring(0,7)+'...'; 
															  }
														  }else{
															 if(newShowName.length>11){
																  name=newShowName.substring(0,11)+'...';
															 } 
														  }
										    			standardShowName.html(name).attr('title', newShowName).show();
										    			alert("修改成功");
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
							});

							//打开线图show_charts
							dlg.find('.show_charts').on('click',function(){
								
								hicharDom.attr('style','display:block;height:280px;');
								//highChartContentDom.attr('height','234px;');
								metricChartsShowValuesDiv.attr('style','display:block;height:28px;');
								var metricId = $(this).attr('metric');
								setIds(metricId,instanceId);
								//downObj.find(".hichar").append(metricChartsShowValuesDiv);
							});

							//收回线图
							dlg.find('.w-table-title').on('click',function(){
								hicharDom.attr('style','display:none;');
							});

					    	// 及时状态
							dlg.find('div[name="currentState"]').on('click',function(){
								var currentState = dlg.find('span[name="currentState"]');
								var currentStateDiv = $(this);
								oc.util.ajax({
									successMsg:null,
									url:oc.resource.getUrl('portal/resource/resourceApply/getStandardApplicationCurrentState.htm'),
									data:{instanceId:instanceId},
									success:function(data){
										var value = data.data;
										var color = '';
										switch (value) {
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
//										case 'UNKNOWN_NOTHING':
//											color = "light-ico_resource res_unknown_nothing";
//											break;
//										case 'UNKOWN':
//											color = "light-ico_resource res_unkown";
//											break;
										default :
//											color = "light-ico_resource res_unknown_nothing";
											color = "light-ico_resource res_normal_nothing";
											break;
												
										}
										
//										  dlg.find('span[name="currentState"]').attr('class','light-ico '+color+'light');
										currentState.attr('class', color);
										currentStateDiv.css('top','-7px');
										
									}
								})
							});
						},
						onClickCell : function(rowIndex, field, value){
							if(field == 'Url' && value != '--' && value != undefined && value != null){
								var newValue = value;
								if(newValue.search(/http:\/\//) == -1){
									newValue = 'http://' + newValue;
								}
								window.open(newValue, field);
							}
						}
					});
				}
				
			}
		})
	}
	
	//将字符串内中文换算为两个长度
	function getStrLength(str){
		var strNew = str.replace(/([^\u0000-\u00FF])/g, function ($) { return escape('--'); });
		return strNew.length;
	}
	function ifGroupDisplayHandle(metricId){
		this.metricGroup = null;
		var metricGroupSource = this.metricGroupSource;
		//var parent = this.divObj.parent();
		//var metricIdSelect = parent.find('#metricChartsLi').css('display','none').html('');
		
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
	}

	function setGroupDisplay(metricGroup,metricId){
		var that = this;
		//var parent = this.divObj.parent();
		
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
		
		showMetricMainValues();
		$('.hichar').find('span[name=showValuesColor]').attr('class',metricGroup[0]['color']);
		var box = {
				selector:$('.hichar').find('#metricIdSelect'),
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
								setMainValues(valuesTempIn);
								flag = false;
								break;
							}
						}
						if(flag){
							setMainValues({'max':"",'min':"",'avg':""});
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
				var checkFlag=thisObj.attr("checked");
				if(checkFlag){
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
					
					getRandomTimeData(queryTimeType,timeSet.timeSub,timeSet.dateStartStr,timeSet.dateEndStr);
				}else{
					queryAdapt(liObj,queryTimeType);
				}
				
		    });
		});
	}

	function setIds(metricId,instanceId){
		var that = this;
		
		this.meticId = metricId;
		this.instanceId = instanceId;
		var chartResourceType = this.resourceType;
		chart = this.chart;//this.chart  this.id.highcharts()
		var newChartId = this.inerId;//this.id;
		//对metricId进行检查,确认是否为按组显示的元素
		// ifGroupDisplayHandle(metricId);
		
		//this.divObj.parent().find('li[name=chartsType'+this.id+']').each(function(i){
		var chartId = this.id;
		//var parent = this.divObj.parent();
		
		//默认查询1H
		getTimeData("1H","min",60*1000*60);
		
//		parent.find('li[name=chartsType'+chartId+']').each(function(i){
//			var _liCls = $(this);
//			if('1H'==_liCls.html()){
//				_liCls.addClass('active');
//			}else{
//				_liCls.removeClass('active');
//			}
//		});
		var customTime = $('.hichar').find('#customTimeName').linkbutton('RenderLB',{
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
					    			 lastRandomTimeSet = lastRandomTime;
					    			 getRandomTimeData(queryTimeType,timeSub,dateStartStr,dateEndStr);
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
				selector:$('.hichar').find('#queryTimeType'),
				value : '1H',
				width : 50,
				placeholder : false,
				data:[{id:'1H',name:'1H'},
				      {id:'1D',name:'1D'},
				      {id:'7D',name:'7D'},
				      {id:'30D',name:'30D'},
				      ],
				onSelect : function(record){
					 queryAdapt(null,record.id);
				}
		};
		 timeBox = oc.ui.combobox(timeBox);
		
		$('.combobox-item').on('click',function(){
			var target = $(this);
			var typeStr = target.html();
			var timeBoxValue = $('.hichar').find('#queryTimeType').combobox('getValue');
			if(typeStr==timeBoxValue){
				 queryAdapt(null,timeBoxValue);
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
	}
	function setIdsByBizType(metricId,instanceId){
		var that = this;
		that.meticId = metricId;
		that.instanceId = instanceId;
		that.openType = 'biz';
		var newChartId = that.inerId;
		
		var chartId = that.id;
		//var parent = that.divObj.parent();
		
		//默认查询1H
		getTimeData("1H","min",60*1000*60);
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
				  $('.hichar').find('li[name=chartsType]').each(function(){
					  $(this).removeClass('active');
				  });
				  thisObj.addClass('active');
				  queryAdapt(thisObj,queryTimeType);
		    });
	    });
	}
	function queryAdapt(thisObj,queryTimeType){
		/*
		 * 5天以内分钟数据
		 * 大于1天  半小时数据
		 * 大于15天 小时数据
		 * 大于60天 6小时数据
		 * */
		var that = this;
		switch (queryTimeType) {
		case '1H':
			 getTimeData(queryTimeType,"min",60*1000*60);
			break;
		case '1D':
			 getTimeData(queryTimeType,"min",24*60*1000*60);
			break;
		case '7D':
			 getTimeData(queryTimeType,"halfHour",7*24*60*1000*60);
			break;
		case '30D':
			 getTimeData(queryTimeType,"hour",30*24*60*1000*60);
	        break;
		}
	}
	function getQueryMetrics(){
		
  		var metricGroup = this.metricGroup;
  		if(metricGroup){
  			var parent = this.divObj.parent();
  			var metricIdSelect = $('.hichar').find('#metricChartsLi');
  			
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
	}
	function getTimeData(queryTimeType,metricDataType,timeSub){
		var that = this;
  		var dateEnd = new Date();
  		var dateStart = new Date(dateEnd.getTime()-timeSub);
  		var dateEndStr = formatDate(dateEnd);
  		var dateStartStr = formatDate(dateStart);
  		
  		var metricId = getQueryMetrics();
  		var instanceId = this.instanceId;
  		var newChartId = this.inerId;
  		
  		ajaxSpecialMetricChartData(dateStartStr,dateEndStr,metricId,instanceId,newChartId,queryTimeType,metricDataType);
  		
	}
	function getRandomTimeData(queryTimeType,timeSub,dateStartStr,dateEndStr){
		
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
  		
  		var metricId = getQueryMetrics();
  		var instanceId = this.instanceId;
  		var newChartId = this.inerId;
  		
  		if(null==metricId||""==metricId) metricId = 'null_metric_query';
  		
  		ajaxSpecialMetricChartData(dateStartStr,dateEndStr,metricId,instanceId,newChartId,queryTimeType,metricDataTypeTemp);
	}
	function setMeticId(metricId){
		meticId = meticId;
	}
	function setInstanceId(instanceId){
		instanceId = instanceId;
	}

	function ajaxSpecialMetricChartData(dateStartStr,dateEndStr,meId,insId,id,queryTimeType,metricDataType){
		var that = this;
		oc.util.ajax({
			//后台只用关心查询数据开始结束时间,以及该查什么时间类型的数据
			url:oc.resource.getUrl('portal/resource/metricChartsData/getSpecialMetricChartsData.htm'),
			data:{dateStart : dateStartStr ,dateEnd : dateEndStr,instanceId : insId, metricId : meId ,metricDataType : metricDataType},
			successMsg:null,
			success:function(data){
				var dataObj = handleSourceData(data,metricDataType);
				if(dataObj.seriesData[0].data[1][1]==null){
					dataObj.seriesData[0].data = new Array();
				}
				renderChart(dataObj,queryTimeType);
			}
		});
	}

	function createCleanCharts(id){
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
	}

	 function renderChart(dataObj,queryTimeType){
		 
	
		var newChartId = this.inerId;
		var legend = {enabled:false};
		var markerShowFlag = false;
		if("1H" == queryTimeType){
			markerShowFlag = true;
		}
		
		var showMetricChartsDiv = $('.table-datanull').find('.highChartContent');
		
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

			return (dateTool.getDate()+'/'+dateTool.getHours())+':'+(dateTool.getMinutes()<10?'0'+dateTool.getMinutes():dateTool.getMinutes())+' : '+unitTransform(params[0].data[1], dataObj.unit);
			
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
	        symbolSize: 1,
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
	}
	
	function handleForMainValues(data){
		var dataMap = data.data.dataMap;
		var unit = data.data['metricUnitName'];
		var metricDataType = data.data['metricDataType'];
		
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
						value['max'] = formatMainValues(maxV.toString(),unit,metricDataType,maxVTime);
						value['min'] = formatMainValues(minV.toString(),unit,metricDataType,minVTime);
					}else{
						//汇总线图,最大最小值按后台瞬时值
						var item = valueInfo[valueInfo.length-1];
						var maxVlaue = item['MaxMetricData'];
						var maxDate = new Number(item['maxDate']);
						var minVlaue = item['MinMetricData'];
						var minDate = new Number(item['minDate']);
						
						value['max'] = formatMainValues(maxVlaue,unit,metricDataType,maxDate);
						value['min'] = formatMainValues(minVlaue,unit,metricDataType,minDate);
					}
					var avgNum = new Number(accountV/(valueInfo.length-2));
					value['avg'] = formatMainValues(avgNum.toFixed(2).toString(),unit,metricDataType);
				}
				arrTemp.push(value);
			}
			this.mainValuesArr = arrTemp;
			
			var parent = this.divObj.parent();
			var selectValue = $('.hichar').find('#metricIdSelect').combobox('getValue');
			
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
					$('.hichar').find('#metricIdSelect').combobox('setValue',selectValue);
					$('.hichar').find('span[name=showValuesColor]').attr('class',color);
					setMainValues(metricObjSelect);
				}else{
					$('.hichar').find('#metricIdSelect').combobox('setValue',comboboxData[0]['id']);
					$('.hichar').find('span[name=showValuesColor]').attr('class',colorIndex1);
					setMainValues(dataIndex1);
				}
			}else{
				this.mainValuesObj.load([]);
				$('.hichar').find('#metricIdSelect').combobox('clear');
				$('.hichar').find('span[name=showValuesColor]').attr('class',"");
				setMainValues({'max':"",'min':"",'avg':""});
			}
		}else{
			var metricQuery = getQueryMetrics();
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
						value['max'] = formatMainValues(maxV.toString(),unit,metricDataType,maxVTime);
						value['min'] = formatMainValues(minV.toString(),unit,metricDataType,minVTime);
					}else{
						//汇总线图,最大最小值按后台瞬时值
						var item = valueInfo[valueInfo.length-1];
						var maxVlaue = item['MaxMetricData'];
						var maxDate = new Number(item['maxDate']);
						var minVlaue = item['MinMetricData'];
						var minDate = new Number(item['minDate']);
						
						value['max'] = formatMainValues(maxVlaue,unit,metricDataType,maxDate);
						value['min'] = formatMainValues(minVlaue,unit,metricDataType,minDate);
					}
					var avgNum = new Number(accountV/(valueInfo.length-2));
					value['avg'] = formatMainValues(avgNum.toFixed(2).toString(),unit,metricDataType);
					
			}
				setMainValues(value);
		}
	}
	function formatMainValues(value,unit,metricDataType,collectTime){
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

	function setMainValues(value){
		$('.hichar').find('span[name=valueMax]').html(value['max']);
		$('.hichar').find('span[name=valueMin]').html(value['min']);
		$('.hichar').find('span[name=valueAvg]').html(value['avg']);
	}
	
	function handleSourceData(data,metricDataType){
		var seriesDatas = new Array();
		var xAxisTemp ;
		var tooltip ;
		var yAxisTemp ; 
		var yAxisMaxValue = 0;
		var yAxisMinValue = 0;
		var metricUnitName = "";
		var seriesDataValues = new Array(); 
		var seriesDataValues0 = new Array(); 
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
		var queryMetric = getQueryMetrics();
		if(data.data.dataMap){
			var that = this;
			
			var dataMap = data.data.dataMap;
			var metricGroup = that.metricGroup;
			//处理数据
			if(that.openType != 'biz'){
				handleForMainValues(data);
			}
			if(metricGroup){
				
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
		
			
			return {
				seriesDataValue:seriesDataValues,
				seriesDataValue0:seriesDataValues0,
				metricUnitName:metricUnitName,
				unit:unit,
				xAxisTemp : xAxisTemp,
			    yAxisTemp : yAxisTemp,
			    tooltip : tooltip,
			    seriesData : seriesDatas};
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
		var precision = 2;
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
		sb = millisecond > 0 ? (sb+=(millisecond.toFixed(precision) + "毫秒")) : sb;
		sb = ""==sb ? (sb+=(millisecond.toFixed(precision) + "毫秒")) : sb;
		return sb;
	}
	
	function transformSecond(seconds){
		var precision = 2;
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
		sb = second > 0 ? (sb+=(second.toFixed(precision) + "秒")) : sb;
		sb = ""==sb.toString() ? (sb+=(second.toFixed(precision) + "秒")) : sb;
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

	oc.resource.management.dialog.standard.application = {
			
		open:function(data){
			callback = data.callback;
			instanceId = data.instanceId;
			init();
		},openByBiz:function(cfg){
			callback = function(){};
			instanceId = cfg.instanceId;
			initByBiz(cfg.selector);
		}
			
	}
})(jQuery);