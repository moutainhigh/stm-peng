(function ($) {
	oc.ns('oc.resourced.resource.detail.showmetric');
	var instanceIdPage,metricTypePage,metricTypeNamePage,
	mainResourceStatePage,mainResourceTypePage,bizOpenFilterInstanceId;
	// openResourceDisplayType ：normalResource   VMResource
	var openResourceDisplayType = null,vminstanceIdArr = null,ifParentRes,argsLengthFlag=false;
	var bottomObj = {isBottom : false};
	//将数据缓存起来,用于名称或可用性搜索
	var globalData = {};
	
	
function  init(){
	var showMetricNew = oc.util.generateId();
	var chartDivId =  oc.util.generateId();
    var selectNewId = oc.util.generateId();
    var showMetricDatagridNewId = oc.util.generateId();
    var showMetricDatagridDivId = oc.util.generateId();
    var randomNum = oc.util.generateId();
    var datagrid ;
    
	var showMetric = $('#showMetric').attr('id',showMetricNew);
	var showMetricChartsDiv =  showMetric.find('#showMetricChartsDiv');
	showMetric.find('#infoDatagrid').attr('id','infoDatagrid'+randomNum).html(metricTypeNamePage+'信息列表');
	var showHideChart = showMetric.find('#infoCharts').attr('id','infoCharts'+randomNum).html(metricTypeNamePage+'信息图表');
	showMetricChartsDiv.height(showHideChart.parent().height());
	
	var showMetricColumnSelect = showMetric.find('#showMetricColumnSelect').attr('id',selectNewId);
    var showMetricCharts = showMetric.find('#showMetricCharts').attr('id',chartDivId);
    var showMetricDatagrid = showMetric.find('#showMetricDatagrid').attr('id',showMetricDatagridNewId).attr('type',metricTypePage).attr('reourceType',mainResourceTypePage);
    //根据业务,子资源类型个数,屏蔽tab,决定高度
    var showMetricDatagridDivHeight ;
    if(argsLengthFlag){
    	showMetricDatagridDivHeight = showMetric.height() ;
    }else{
    	showMetricDatagridDivHeight = showMetric.height() - showHideChart.parent().height();
    }
    
    
    var showMetricDatagridDiv = showMetric.find('#showMetricDatagridDiv').attr('id',showMetricDatagridDivId).height(showMetricDatagridDivHeight);

    showMetricDatagridDiv.find('#showMetricDatagridParent').height(showMetricDatagridDivHeight- showHideChart.parent().height()-15+43);
//  showMetricDatagridDiv.find('#showMetricDatagridParent').attr('style','width:100%;height:99%;');

    
    showMetric.find("li[name=chartsType]").each(function(){
    	$(this).attr('name','chartsType'+chartDivId);
    });
    
    //定义datagrid渲染完成,后关闭  请稍后...
//    var extendView = $.extend({},$.fn.datagrid.defaults.view,{
//    	onAfterRender:function(target){
//    		$.messager.progress('close');
//    	}
//    });
    var datagridHeadUrl , datagridDataUrl , datagridHeadQuery;
    switch (openResourceDisplayType) {
	case "VMResource":
		datagridHeadUrl = oc.resource.getUrl('portal/resource/resourceApply/getDatagridHeaderInfoByResourceId.htm');
		datagridHeadQuery = {instanceId : instanceIdPage , resourceId :metricTypePage};
		
		datagridDataUrl = oc.resource.getUrl('portal/resource/resourceApply/getMetricInfoByResourceId.htm?mainInstanceId='+instanceIdPage+'&resourceId='+showMetricDatagrid.attr('type')+'&instanceIdArr='+vminstanceIdArr+'&parentType='+showMetricDatagrid.attr('reourceType'));
		break;

	case "normalResource":
	case "normalResourceForBizOpen":
		datagridHeadUrl = oc.resource.getUrl('portal/resource/resourceApply/getDatagridHeaderInfo.htm');
		datagridHeadQuery = {instanceId : instanceIdPage , metricType :metricTypePage};
		
		datagridDataUrl = oc.resource.getUrl('portal/resource/resourceApply/getMetricInfo.htm?instanceId='+instanceIdPage+'&metricType='+showMetricDatagrid.attr('type')+'&resourceType='+showMetricDatagrid.attr('reourceType'));
		break;
	}
    

	//获取datagrid的头信息
	oc.util.ajax({
		url:datagridHeadUrl,
		data:datagridHeadQuery,
		successMsg:null,
		success:function(data){
			if(null == data.data||undefined == data.data||0==data.data.length ){
				showMetricDatagridDiv.attr('style','height:100%;');
			}else{
				var dataLen = data.data.length;
//				var datagridColumus = [dataLen-1];
//				var frozenColumns = [1];
				var datagridColumus = new Array();
				var frozenColumns = new Array();
				
				//设置是否显示图表
				var showChartBool = false;
				//设置数据表格是否自适应宽度
				var showDatagridScroll = false;
				//定义固定长度宽度
				var rootDivWidth = 1085;
				var widthNum ;
				var dataLenForColumnsWidth = dataLen;
				if("NetInterface"==metricTypePage && "NetworkDevice"==mainResourceTypePage){
					dataLenForColumnsWidth = dataLen+1;
				}
				if(dataLenForColumnsWidth<10){
//					showDatagridScroll = false;
					//rootDivWidth-10避免显示所有列时,底部出现滚动条,影响显示
					widthNum = parseInt((rootDivWidth-10)/dataLenForColumnsWidth);
				}else{
					widthNum = 140;
				}
				
				//记录是否有可用性指标
				var useableFlag = false ,useableMetricIdArr = new Array();
				for(var i=0; i<dataLen; i++){
				  var dataRow = data.data[i];
				  //i=0时
				  var datagridColumusIndex = i-1;
				  var frozenColumnsIndex = 0;
				  
				  //设置datagrid默认显示行数
				  var hiddenBool = false;
				  //第一列固定显示
				  var isDynamicHidden = true;
				  
				  
				  //避免dataRow.name过长,修改datagrid列长  按一个字2个长度,一个长度7px动态修改列长
				  var nameLengthInside = getStrLength(dataRow.name);
				  if(nameLengthInside>22){
					  widthNum = nameLengthInside*7;
				  }
				  
//				  if(i==0){
//					  isDynamicHidden = false;
//				  }
//				  if(i>12){
//					  hiddenBool = true;
//				  }
				  if(null!=dataRow.style && "PerformanceMetric"==dataRow.style){
					  //接口的接口总流量指标不展示
					  if("NetInterface"==metricTypePage && "ifOctetsSpeed"==dataRow.metricId){
						  continue;
					  }
					  
					  showChartBool = true;
//					  datagridColumus[datagridColumusIndex] = 
					  datagridColumus.push(
						{field:dataRow.metricId,isDynamicHidden:isDynamicHidden,hidden:hiddenBool,title:dataRow.name,width:widthNum,sortable:true,ellipsis:true,formatter:function(value,row,rowIndex){
							if(undefined==value || null==value || '--'==value){
							  return '<a  class="show_charts" ><span metric="'+this.field+'" instanceId="'+row.instanceid+'" class="ico ico-chart margin-top-seven show_charts'+showMetricDatagrid.attr('type')+'">--</span></a>';
						  }else if('N/A'==value){
							  return 'N/A';
						  }else{
							  return '<a  class="show_charts" title="' + value + '" ><span metric="'+this.field+'" instanceId="'+row.instanceid+'" class="ico ico-chart margin-top-seven show_charts'+showMetricDatagrid.attr('type')+'">'+value+'</span></a>';
						  }
					  }});
				  }else if(null!=dataRow.style && "AvailabilityMetric"==dataRow.style){
					  useableFlag = true;
					  useableMetricIdArr.push(dataRow.metricId);
					  
//					  datagridColumus[datagridColumusIndex] = 
					  datagridColumus.push(
					  {field:dataRow.metricId,isDynamicHidden:isDynamicHidden,hidden:hiddenBool,title:dataRow.name,width:widthNum,sortable:false,ellipsis:true,formatter:function(value,row,rowIndex){
//						  if(mainResourceStatePage=='CRITICAL' ||  mainResourceStatePage=='CRITICAL_NOTHING' 
////							  || mainResourceStatePage=='UNKOWN'|| mainResourceStatePage=='UNKNOWN_NOTHING'
//									  ){
////							  return '未知';
//							  return '不可用';
//						  }else
						  // 主资源不可用则显示为不可用 dfw 20161226

						  if(mainResourceStatePage == 'CRITICAL' || mainResourceStatePage == 'CRITICAL_NOTHING'){
							  return '不可用';
						  }
						  if(undefined==value || null==value){
							  return "--";
						  }else{
							  var stateStr = '';
							  switch (value) {
								case 'CRITICAL':
								case 'CRITICAL_NOTHING':
									stateStr = '不可用';
									break;
								case 'NORMAL':
								case 'SERIOUS':
								case 'WARN':
								case 'NORMAL_CRITICAL':
								case 'NORMAL_UNKNOWN':
								case 'NORMAL_NOTHING':
									stateStr = '可用';
									break;
								default :
//									stateStr = '未知';
									stateStr = '可用';
								    break;
								}
							  return stateStr;
						  }
					  }});

				  }else if(null!=dataRow.style && "InformationMetric"==dataRow.style){
					  if('resourceNameAndState'==dataRow.metricId ){
//						  frozenColumns[frozenColumnsIndex] = 
						  frozenColumns.push({field:dataRow.metricId,isDynamicHidden:isDynamicHidden,hidden:hiddenBool,title:dataRow.name,width:widthNum,sortable:false,ellipsis:true,formatter:function(value,row,rowIndex){
							  
							  var nameSubStr = row.resourceName;
//							  var nameStr = row.resourceName;
//							  if(null!=nameStr && nameStr.length>30){
//								  nameSubStr = nameStr.substring(0,25)+'...';
//							  }
							  
//							  if(mainResourceStatePage=='CRITICAL' || mainResourceStatePage=='CRITICAL_NOTHING' 
////									|| mainResourceStatePage=='UNKOWN' || mainResourceStatePage=='UNKNOWN_NOTHING'
//										){
//								  if(undefined==row.resourceName || null==row.resourceName){
//									  return '<span class="light-ico graylight" >--</span>';
//								  }else{
//									  return '<div id="resourceName-show-'+rowIndex+'" style="width:100%;"><span markId="resourceNameSpan" name="resourceName-'+rowIndex+'" style="width:'+(widthNum-26)+'px;text-overflow: ellipsis;" class="light-ico_resource res_unknown_nothing oc-datagrid-spanwdth" title="'+nameSubStr+'">'+nameSubStr+'</span><span class="uodatethetext" index='+rowIndex+' title="编辑名称"></span></div>'+
//									  '<div id="resourceName-edit-'+rowIndex+'" style="width:100%;display:none;"><input markId="resourceNameSpan" type="text" value="'+nameSubStr+'" instanceId="'+row.instanceid+'" class="validatebox-box" style="width:'+(widthNum-30)+'px;" /><span class="uodatethetextok" title="确认名称修改" index='+rowIndex+'  style="float: right;"></span></div>';
//								  }
//							  }else 
							  if(undefined==row.resourceState || null==row.resourceState){
								  if(undefined==row.resourceName || null==row.resourceName){
									  return '<span >--</span>';
								  }else{
									  return '<div id="resourceName-show-'+rowIndex+'" style="width:100%;position:relative;"><span markId="resourceNameSpan" name="resourceName-'+rowIndex+'" style="width:'+(widthNum-26)+'px;text-overflow: ellipsis;white-space: nowrap;overflow: hidden;" class="light-ico_resource res_unknown_nothing oc-datagrid-spanwdth" title="'+nameSubStr+'">'+nameSubStr+'</span><span name="editName-'+index+'" class="uodatethetext fa fa-edit" style="display:none;" title="编辑名称" index='+rowIndex+'></span></div>'+
									  '<div id="resourceName-edit-'+rowIndex+'" style="width:100%;display:none;position:relative;"><input markId="resourceNameInput" type="text" value="'+nameSubStr+'" instanceId="'+row.instanceid+'" class="validatebox-box" style="width:'+(widthNum-30)+'px;" /><span class="uodatethetextok fa fa-check" title="确认名称修改" index='+rowIndex+'  ></span></div>';
								  }
								  
							  }else{
								  var color = '';
								  switch (row.resourceState) {
								  	case 'CRITICAL':
									  color = "res_critical";
										break;
//									case 'CRITICAL_NOTHING':
//										color = "res_critical";
//										break;
									case 'SERIOUS':
										color = "res_serious";
										break;
									case 'WARN':
										color = "res_warn";
										break;
									case 'NORMAL':
										color ="res_normal_nothing";
										break;
//									case 'NORMAL_NOTHING':
//										color = "ava_notavailable";
//										break;
									case 'NORMAL_CRITICAL':
										color = "res_normal_critical";
										break;
									case 'NORMAL_UNKNOWN':
										color = "res_unknown_nothing";
										break;
//									case 'UNKNOWN_NOTHING':
//										color = "res_unknown_nothing";
//										break;
//									case 'UNKOWN':
//										color = "res_unkown";
//										break;
//									case 'CRITICAL':
//										color = 'red';
//										break;
//									case 'SERIOUS':
//										color = 'orange';
//										break;
//									case 'WARN':
//										color = 'yellow';
//										break;
//									case 'UNKOWN':
//										color = 'gray';
//										break;
//									case 'NORMAL':
//										color = 'green';
//										break;
									default :
//										color = 'res_unkown';
										color = "res_normal_nothing";
									    break;
									}
//								  if(undefined==row.resourceName || null==row.resourceName){
								  if(!row.resourceName){
									  return '<span class="light-ico_resource '+color+' oc-datagrid-spanwdth" >--</span>';
								  }else{//light-ico_resource res_unkown
//									  return '<div id="resourceName-show-'+rowIndex+'" style="width:100%;"><span name="resourceName-'+rowIndex+'" style="width:'+(widthNum-26)+'px;text-overflow: ellipsis;" class="light-ico oc-datagrid-spanwdth '+color+'light" title="'+row.resourceName+'">'+nameSubStr+'</span><span class="uodatethetext" index='+rowIndex+' title="编辑名称" ></span></div>'+
									  return '<div id="resourceName-show-'+rowIndex+'" style="width:100%;position:relative;"><span markId="resourceNameSpan" name="resourceName-'+rowIndex+'" style="width:'+(widthNum-26)+'px;text-overflow: ellipsis;white-space: nowrap;overflow: hidden;" class="light-ico_resource '+color+' oc-datagrid-spanwdth" title="'+nameSubStr+'">'+nameSubStr+'</span><span name="editName-'+rowIndex+'" class="uodatethetext fa fa-edit" style="display:none;" index='+rowIndex+' title="编辑名称" ></span></div>'+
									  '<div id="resourceName-edit-'+rowIndex+'" style="width:100%;display:none;position:relative;"><input markId="resourceNameInput" type="text" value="'+nameSubStr+'" instanceId="'+row.instanceid+'" class="validatebox-box" style="width:'+(widthNum-30)+'px;" /><span class="uodatethetextok fa fa-check" title="确认名称修改" index='+rowIndex+'></span></div>';
								  }
							  }
						  }});
					  }else if('resourceName'==dataRow.metricId){
//						  frozenColumns[frozenColumnsIndex] = {field:dataRow.metricId,isDynamicHidden:isDynamicHidden,hidden:hiddenBool,title:dataRow.name,sortable:false,width:widthNum,ellipsis:true};
						  frozenColumns.push({field:dataRow.metricId,isDynamicHidden:isDynamicHidden,hidden:hiddenBool,title:dataRow.name,sortable:false,width:widthNum,ellipsis:true,formatter:function(value,row,rowIndex){
							  if(value){
								  return '<div id="resourceName-show-'+rowIndex+'" style="width:100%;height:100%;position:relative;"><div markId="resourceNameDiv" name="resourceName-'+rowIndex+'" style="width:'+(widthNum-38)+'px;text-overflow: ellipsis;overflow:hidden;display:inline-block;"  title="'+value+'" >'+value+'</div><span name="editName-'+rowIndex+'" class="uodatethetext fa fa-edit" style="display:none;" title="编辑名称" index='+rowIndex+' ></span></div>'+
								  '<div id="resourceName-edit-'+rowIndex+'" style="width:100%;display:none;position:relative;"><input markId="resourceNameInput" type="text" value="'+value+'" instanceId="'+row.instanceid+'" class="validatebox-box" style="width:'+(widthNum-30)+'px;" /><span class="uodatethetextok fa fa-check" title="确认名称修改" index='+rowIndex+'  ></span></div>';
								   }else{
								  return '--';
							  }
						  }});
					  }else{
						  if("NetInterface"==metricTypePage && dataRow.metricId=="ifSpeed"){
							  datagridColumus.push({field:dataRow.metricId,isDynamicHidden:isDynamicHidden,hidden:hiddenBool,title:dataRow.name,sortable:false,width:widthNum,ellipsis:true,formatter:function(value,row,rowIndex){
								  var metricId = "ifSpeed";
								  var handsetflag = row[metricId+"_HandSet"];
								  var instanceId = row['instanceid'];
								  var ifSpeedHandleState = row['ifSpeedHandleState'];
								  var spanClass = "ico bandwide-recovery";
								  
								  var result="";
								  if(ifSpeedHandleState && "false"==ifSpeedHandleState){
									  spanClass = "ico bandwide-no-recovery";
								  }
								  if(handsetflag && "true"==handsetflag){
									  //记录原始值,用于p排序显示
									  value = row[metricId+"_HandSetValue"];
									  result = "<a name='ifSpeedValue' instanceId='"+instanceId+"' index='"+rowIndex+"' title='"+value+"' class='bandwide-edit bandwide-color' >"+value+"</a><span name='ifSpeedHandle' instanceId='"+instanceId+"' index='"+rowIndex+"' class='"+spanClass+"' title='恢复采集'></span>";
								  }else{
									  result = "<a name='ifSpeedValue' instanceId='"+instanceId+"' index='"+rowIndex+"' title='"+value+"' class='bandwide-edit ' >"+value+"</a><span name='ifSpeedHandle' style='display:none;' instanceId='"+instanceId+"' index='"+rowIndex+"' class='"+spanClass+"' title='恢复采集'></span>";
								  }
								  return result;
							  }});
						  }else{
							  datagridColumus.push({field:dataRow.metricId,isDynamicHidden:isDynamicHidden,hidden:hiddenBool,title:dataRow.name,sortable:false,width:widthNum,ellipsis:true});  
						  }
					  }
					  
				  }else{
					datagridColumus.push({field:dataRow.metricId,isDynamicHidden:isDynamicHidden,hidden:hiddenBool,title:dataRow.name,sortable:false,ellipsis:true,width:widthNum});
					  
				  }
				  
				  //重置长度
				  if(dataLen<10){
//						showDatagridScroll = false;
						widthNum = parseInt(rootDivWidth/dataLenForColumnsWidth);
					}else{
						widthNum = 140;
					}
				}
				
				//如果是接口类型添加下联设备信息列,且添加到数组的第x位置上
				if("NetInterface"==metricTypePage && "NetworkDevice"==mainResourceTypePage){
//				if("NetInterface"==metricTypePage ){
					var columusObj = {field:"deviceIp",isDynamicHidden:true,hidden:false,title:"下联设备",sortable:false,width:widthNum,ellipsis:true,formatter : function(value,row,rowIndex){
						if(value){
							return '<div>'+value+'<span style="float:right;" class="downDevice_Detail fa fa-ellipsis-h" name="NetInterfaceDevice" instance='+row.instanceid+' index='+row.ifIndex+'  deviceName='+row.resourceRealName+' title="点击查看详情!"></span></div>';
						}else{
							return '无';
						}
					}};
					datagridColumus.splice(2,0,columusObj);
//					datagridColumus.push({field:"metric02",isDynamicHidden:true,hidden:false,title:"下联设备名称",sortable:false,width:widthNum,ellipsis:true});
//					datagridColumus.push({field:"metric03",isDynamicHidden:true,hidden:false,title:"下联设备MAC",sortable:false,width:widthNum,ellipsis:true});
					
				}

				//加载charts
				var chartObj1 ;
				if(showChartBool){
					//传入div,宽度参数        chart图宽度 = 宽度参数*div宽度
					chartObj1 = new chartObj(showMetricCharts,1,metricTypePage);
					showMetricChartsDiv.attr('style','display:none;');
					//绑定chartDiv收缩
					showMetricChartsDiv.find('.w-table-title').on('click',function(){
				    	showHideChartMethod(showMetric,showMetricDatagridDiv,showMetricChartsDiv,showHideChart);
						showMetricChartsDiv.attr('style','display:none;');
					});
				}else{
					//图标div不显示
					var showMetricHeight = showMetric.height();
					var showHideChartHeight = showHideChart.parent().height();
					
					showMetricDatagridDiv.height(showMetricHeight);
					showMetricChartsDiv.attr('style','display:none;');
					
					//如果表底无滚动条,调整高度以便对齐,有滚动条不用调整
					if(dataLenForColumnsWidth<10){
//					if(false){
//						showMetricDatagrid.parent().height(showMetricHeight- showHideChartHeight - 20);
						showMetricDatagrid.parent().height(471);
					}
				}
				
				datagrid = oc.ui.datagrid({
				selector:showMetricDatagrid,
				fitColumns:false,//showDatagridScroll,
//				height:'280px',
//				width: '1096px',
				pagination:false,
				dynamicColumnsSelect:showMetricColumnSelect,
//				url:datagridDataUrl ,
				columns:[datagridColumus],
				frozenColumns:[frozenColumns],
			     onLoadSuccess:function(data){
			    	 
			    	 //重置列头resourceName列宽  
			    	 var resourceNameSpanJQ = showMetric.find('span[markId=resourceNameSpan]');
			    	 resourceNameSpanJQ.width(resourceNameSpanJQ.parent().width()-34);
			    	 var resourceNameInputJQ = showMetric.find('span[markId=resourceNameInput]');
			    	 resourceNameInputJQ.width(resourceNameInputJQ.parent().width()-20);
			    	 var resourceNameDivJQ = showMetric.find('div[markId=resourceNameDiv]');
			    	 resourceNameDivJQ.width(resourceNameDivJQ.parent().width()-20);
			    	 
			    	 var rows = datagrid.selector.datagrid('getRows');
			    	 showMetric.find(".show_charts"+showMetricDatagrid.attr('type')).on('click', function(){ 
						 showMetricChartsDiv.attr('style','display:block;');
			    		 showMetric.find(".ico.ico-chartred.margin-top-seven.show_charts"+showMetricDatagrid.attr('type')).each(function(){
			    			 $(this).attr('class','ico ico-chart margin-top-seven show_charts'+showMetricDatagrid.attr('type'));
			    		 });
			    		 $(this).attr('class','ico ico-chartred margin-top-seven show_charts'+showMetricDatagrid.attr('type'));
			    		 var metricId = $(this).attr('metric');
			         	 var instanceId = $(this).attr('instanceId');
			         	 
				    	 chartObj1.setIds(metricId, instanceId);
				    	 
				    	 showHideChartMethod(showMetric,showMetricDatagridDiv,showMetricChartsDiv,showHideChart,'show');
			   	     });
			    	 
			    	 //接口下联设备点击查看
			    	 showMetric.find('span[name=NetInterfaceDevice]').on('click',function(){
			    		var target = $(this);
			    		
			    		var childrenInstanceId = target.attr('instance');
//			    		var childrenDeviceName = target.attr('name');
			    		var index = target.attr('index');
			    		
						oc.resource.loadScript("resource/module/topo/backboard/DownDeviceDia.js",function(){
							$.post(oc.resource.getUrl("topo/backboard/downinfo.htm"),{
								subInstanceId:childrenInstanceId,
								mainInstanceId:instanceIdPage
							},function(result){
								new DownDeviceDia({
									ip:result.ip,		//上联 设备ip
									intface:result.IfName,//上联设备接口
									deviceName:result.deviceShowName,//上联设备名称
									intfaceIndex:index	//接口索引
								});
							},"json");
						});
			    		 
			    	 });
			    	 
			    	 //接口带宽修改  先判断是否有权限修改   rows[0]['ifSpeedHandleState']+"";//
			    	 var ifSpeedHandleState ='';
			    	 if(rows[0]&&rows[0]['ifSpeedHandleState']){
			    		 ifSpeedHandleState = rows[0]['ifSpeedHandleState'];
			    	 }
			    	 if(ifSpeedHandleState=="true"){
				    	 showMetricDatagridDiv.find('a[name=ifSpeedValue]').on('click',function(){
				    		 var objThis = $(this); 
				    		 var instanceId = objThis.attr('instanceId');
				    		 var index = objThis.attr('index');
				    		 
				    		 var dlg = $("<div/>");
					 			dlg.append($("<div/>").css('top', '45px').css('text-align','center').html("<div>带宽：<input name='ifSpeedInputValue' type='text' style='margin: 14px 0px 0px 0px' />Mbps</div>"));
					 			dlg.dialog({
				 				title : '修改带宽',
				 				width : '300px',
				 				height : '160px',
				 				buttons : [{
							    	text: '</span>确定',
							    	iconCls:"fa fa-check-circle",
							    	handler:function(){
							    		var value = dlg.find('input[name=ifSpeedInputValue]').val();
							    		var unit = 'Mbps';
							    		if(null==value || ""==value ){
							    			alert('请填写带宽值!');
							    			return ;
							    		}else if(isNaN(value)){
							    			alert('请填写数字!');
							    			return ;
							    		}
							    		//后台存储按bps数据存储故*1000*1000
							    		var speedValue = value*1000*1000;
							    		oc.util.ajax({
						    				url:oc.resource.getUrl('portal/resource/resourceApply/updateIfSpeedValue.htm'),
						    				data:{instanceId:instanceId,value:speedValue,realTimeValue:rows[index]['ifSpeed_RealTimeForSort'],key:"ifSpeed"},
						    				successMsg:null,
						    				type:'post',
						    				success:function(data){
						    					if(data.data){
						    						objThis.html(value+unit).addClass('bandwide-color');
										    		objThis.parent().find('span[name=ifSpeedHandle]').show();
										    		
										    		//修改rows数据以便排序  输入数据单位为Mbps 排序按最小单位排
										    		rows[index]['ifSpeed_ForSort'] = value*1000*1000;
										    		rows[index]['ifSpeed_HandSet'] = 'true';
										    		rows[index]['ifSpeed_HandSetValue'] = value+unit;
										    		
										    		var rowsTemp = globalData[metricTypePage].data.rows;
										    		rowsTemp[index]['ifSpeed_ForSort'] = value*1000*1000;
										    		rowsTemp[index]['ifSpeed_HandSet'] = 'true';
										    		rowsTemp[index]['ifSpeed_HandSetValue'] = value+unit;
										    		
										    		alert('修改成功!');
										    		dlg.dialog('destroy');
						    					}
						    				}
						    			});
							    	}
							    },{
							    	text: '取消',
							    	iconCls:"fa fa-times-circle",
							    	handler:function(){
							    		dlg.dialog('destroy');
							    	}
							    }],
				 				onClose : function(){
				 					dlg.dialog('destroy');
				 				}
				 			});
				    	 });
				    	 
				    	 //恢复采集
				    	 showMetricDatagridDiv.find('span[name=ifSpeedHandle]').on('click',function(){
				    		 var ObjTemp = $(this);
				    		 var instanceId = ObjTemp.attr('instanceId');
				    		 
				    		 oc.util.ajax({
				    				url:oc.resource.getUrl('portal/resource/resourceApply/updateIfSpeedCollection.htm'),
				    				data:{instanceId:instanceId,key:"ifSpeed"},
				    				successMsg:null,
				    				type:'post',
				    				success:function(data){
				    					if(data.data){
				    						ObjTemp.hide();
				    						
								    		var index = ObjTemp.attr('index');
								    		var valueCollect = rows[index]['ifSpeed'];
								    		ObjTemp.parent().find('a[name=ifSpeedValue]').attr('class','bandwide-edit').html(rows[index]['ifSpeed']);
								    
								    		//修改rows数据以便排序
								    		rows[index]['ifSpeed_ForSort'] = rows[index]['ifSpeed_RealTimeForSort'];
								    		rows[index]['ifSpeed_HandSet'] = 'false';
								    		rows[index]['ifSpeed_HandSetValue'] = '';
								    		
								    		var rowsTemp = globalData[metricTypePage].data.rows;
								    		rowsTemp[index]['ifSpeed_ForSort'] = rows[index]['ifSpeed_RealTimeForSort'];
								    		rowsTemp[index]['ifSpeed_HandSet'] = 'false';
								    		rowsTemp[index]['ifSpeed_HandSetValue'] = '';
								    		
								    		alert('恢复采集!');
				    					}
				    				}
				    			});
				    	 });
			    	 }
			    	 
			    	 
			    	 //修改资源显示名字 
			    	 showMetricDatagridDiv.find('.uodatethetext').on('click',function(){
			    		 var target = $(this);
			    		 var index = target.attr('index');
			    		 
			    		 showMetricDatagridDiv.find('#resourceName-show-'+index).css('display','none');
			    		 showMetricDatagridDiv.find('#resourceName-edit-'+index).css('display','block');
			    		 
			    	 });

					 //datagrid错位滚动条调整
					 showMetricDatagridDiv.find('.datagrid-view2>.datagrid-body').scroll(function(){
						if($(this).get(0).scrollWidth - $(this).width() < $(this).scrollLeft() + 15){
							$(this).scrollLeft($(this).get(0).scrollWidth - $(this).width() - 15);
						}
					 });

					//动态调整宽度
					//head tr
					var headerTr = $(".datagrid-view2>.datagrid-header>.datagrid-header-inner table tr:first-child");

					//table  datagrid-btable
					var bodyTable = $(".datagrid-view2>.datagrid-body table:first-child");

					//释放
					$('#showMetricToolForm').mouseup(function(){
						var bodyTableh = $(bodyTable.get(1));
						var headerTrh = $(headerTr.get(2));

						setTimeout(function(){
							bodyTableh.width(headerTrh.width());
						},100);
					});

					 //修改资源名称浮动显示
					 showMetricDatagridDiv.find('.datagrid-row').mouseenter(function(){  
						 var target = $(this);
						 var index = target.attr('datagrid-row-index');
						 target.find('span[name=editName-'+index+']').attr('style','display:block');

						 //$(".uodatethetext").show();   'span[name=editName-'+index+']'
					 });
		
					 showMetricDatagridDiv.find('.datagrid-row').mouseleave(function(){  
						 var target = $(this);
						 var index = target.attr('datagrid-row-index');
						 target.find('span[name=editName-'+index+']').attr('style','display:none');
						//$(".uodatethetext").show();  
					 });

			    	 showMetricDatagridDiv.find('.uodatethetextok').on('click',function(){
			    		 var target = $(this);
			    		 var index = target.attr('index');
			    		
			    		 var showObj = showMetricDatagridDiv.find('#resourceName-show-'+index);
			    		 var editObj = showMetricDatagridDiv.find('#resourceName-edit-'+index);
			    		 var showSpan = showObj.find('[name=resourceName-'+index+']');
			    		 var inputObj = editObj.find('.validatebox-box');
			    		 var childrenInstanceId = inputObj.attr('instanceId');
			    		 var showName = showSpan.html();
			    		 var editName = inputObj.val();
			    		 
			    		 if(!editName){
			    			 alert('请填写名称!');
			    			 return ;
			    		 }else if(editName == showName){
			    			 showObj.css('display','block');
	    					 editObj.css('display','none');
			    			 return ;
			    		 }else if(checkHaveSameName(editName,globalData[metricTypePage])){
			    			 alert('修改后的名字跟其他资源重名,请更改!');
			    			 return ;
			    		 }
			    		 
			    		 oc.util.ajax({
			    				url:oc.resource.getUrl('portal/resource/resourceApply/updateResourceShowName.htm'),
			    				data:{instanceId:childrenInstanceId,showName:editName},
			    				successMsg:null,
			    				type:'post',
			    				success:function(data){
			    					if(data.data && true==data.data){
			    						showObj.css('display','block');
			    						editObj.css('display','none');
			    						alert("修改显示名称成功！");
			    						//将修改后的名字更新至缓存及页面
			    						showSpan.html(editName).attr('title',editName);
			    						
			    						var rowsTemp = globalData[metricTypePage].data.rows;
			    						for(var i=0;i<rowsTemp.length;i++){
			    							var rowsObj = rowsTemp[i];
			    							if(childrenInstanceId == rowsObj['instanceid']){
			    								rowsObj['resourceName'] = editName;
			    								rowsObj['resourceName_ForSort'] = editName;
			    								rowsObj['resourceNameAndState_ForSort'] = editName;
			    							}
			    						}
			    					}else{
			    						alert("修改失败,请刷新重试!");
			    					}
			    				}
			    			});
			    	 });
			    	 
			    	 
			    	 
			    	 //添加排序方法
			    	 showMetric.find('.datagrid-sort-icon').parent().unbind('click');
			    	 showMetric.find('.datagrid-sort-icon').parent().on('click',function(){
			    		 
			    		 //检测滚动条是否在底部,滚动到指定行以便对齐datagrid行
				    	var isBottomSet = false;
			    		 if(bottomObj.isBottom){
			    			 datagrid.selector.datagrid('scrollTo',0);
			    			 isBottomSet = true;
			    		 }
			    		 
			    		 var target = $(this);
			    		 //datagrid-sort-asc
			    		 var targetRows ;
//			    		 target.removeClass('datagrid-sort-asc').removeClass('datagrid-sort-desc');
			    		 if(target.attr('class').indexOf('datagrid-sort-asc') < 0 && target.attr('class').indexOf('datagrid-sort-desc') < 0){
			    			 showMetric.find('.datagrid-sort-icon').parent().each(function(){
			    				 $(this).removeClass('datagrid-sort-asc').removeClass('datagrid-sort-desc');
			    			 });
			    			 
			    			 target.addClass('datagrid-sort-asc');
			    			 targetRows = sortRowsByMetricId(rows,target.parent().attr('field'),'asc');
			    		 }else if(target.attr('class').indexOf('datagrid-sort-asc')>0 || target.attr('class').indexOf('datagrid-sort-asc')==0){
			    			 showMetric.find('.datagrid-sort-icon').parent().each(function(){
			    				 $(this).removeClass('datagrid-sort-asc').removeClass('datagrid-sort-desc');
			    			 });
			    			 
			    			 target.addClass('datagrid-sort-desc');
			    			 targetRows = sortRowsByMetricId(rows,target.parent().attr('field'),'desc');
			    		 }else if(target.attr('class').indexOf('datagrid-sort-desc')>0 || target.attr('class').indexOf('datagrid-sort-desc')==0){
			    			 showMetric.find('.datagrid-sort-icon').parent().each(function(){
			    				 $(this).removeClass('datagrid-sort-asc').removeClass('datagrid-sort-desc');
			    			 });
			    			 
			    			 target.addClass('datagrid-sort-asc');
			    			 targetRows = sortRowsByMetricId(rows,target.parent().attr('field'),'asc');
			    		 }
			    		 
			    		 var localData = {"data":{"startRow":0,"rowCount":rows.length,"totalRecord":rows.length,"condition":null,
			    			"rows":targetRows,"total":rows.length},"code":200};
			    		datagrid.selector.datagrid('loadData',localData);
			    		
			    		
			    		if(isBottomSet){
			    			datagrid.selector.datagrid('scrollTo',rows.length-1);
			    		 }
			    		
			    	 });
			    	 
					//增加一行空tr,填充滚动条所占高度
			    	 var targetDiv = showMetricDatagridDiv.find('#showMetricDatagridParent');
			    	 var targetDivDatagridView = targetDiv.find('[class=datagrid-view1]').find('[class=table-datanull]');//.find('[class=datagrid-btable]');
			    	 targetDivDatagridView.height(targetDivDatagridView.height()+20);
			    	 
			    	 targetDiv.find('[class=datagrid-view2]').find('[class=datagrid-body]').scroll(function(){
			    			var scrollTop = $(this).scrollTop();
			    			var scrollHeight = targetDiv.find('[class=datagrid-view2]').find('[class=table-datanull]').height();
			    			var windowHeight = $(this).height();
			    			
			    			if((scrollTop + windowHeight) > scrollHeight){
			    				bottomObj.isBottom = true;
			    			}else{
			    				bottomObj.isBottom = false;
			    			}
			    		});
			    	 
			    	 
			    	 
//			    	 var targetDivDatagridView2 = targetDiv.find('[class=datagrid-view2]').find('[class=table-datanull]').find('[class=datagrid-btable]');
////			    	 targetDivDatagridView.height(targetDivDatagridView.height()+20);
//			    	 targetDivDatagridView2.append('<tr style="height: 1px;"></tr>');
//			    	 targetDivDatagridView.append('<div style="height: 20px;"></div>');
					
		         },
		         onResizeColumn : function(field, width){
		        	 if(field=='resourceNameAndState' || field=='resourceName'){
		        		 //修改span及input宽度
		        		 showMetric.find('span[markId=resourceNameSpan]').width(width-26);
		        		 showMetric.find('input[markId=resourceNameInput]').width(width-30);
		        		 showMetric.find('div[markId=resourceNameDiv]').width(width-30);
		        	 }
		         }//,view:extendView
			});
				
				//根据useableFlag,查询下拉框是否显示
				var showMetricToolForm = showMetric.find("#showMetricToolForm");
				if(useableFlag && "NetInterface"==metricTypePage){
					showMetricToolForm.find('#useableSelect').attr('useableFlag',useableFlag).attr('metricId',useableMetricIdArr[0]);
				    oc.ui.form({
						selector:showMetricToolForm,
						combobox:[{
							selector:'#useableSelect',
							placeholder : false,
							width:70,
							data:[
							      {id:'ALL',name:'全部'},
							      {id:'CRITICAL',name:'不可用'},
							      {id:'NORMAL',name:'可用'}
//							      ,{id:'UNKOWN',name:'未知'}
					        ],
					        onChange:function(){
					        	if(getCurrentGlobalData(showMetricDatagrid.attr('type'))){
					        		queryMethod(showMetricToolForm,datagrid,showMetricDatagrid.attr('type'));
					        	}
					        }
						}]
					});
				    showMetricToolForm.find(".logicInterfaceOnOff").on('click', function(e){
				    	if(getCurrentGlobalData(showMetricDatagrid.attr('type'))){
			        		queryMethod(showMetricToolForm,datagrid,showMetricDatagrid.attr('type'));
			        	}
				    });
				}else{
					showMetricToolForm.find("#useableQuerySpan").css('display','none');
				}
				showMetricDatagridDiv.find('#queryResourceShowName').linkbutton('RenderLB',{
					  text:'查询',
					  iconCls:"fa fa-search",
					  onClick:function(){
						  if(getCurrentGlobalData(showMetricDatagrid.attr('type'))){
				        		queryMethod(showMetricToolForm,datagrid,showMetricDatagrid.attr('type'));
				        	}
					  }
				});
				
				oc.util.ajax({
					url:datagridDataUrl,
					successMsg:null,
					success:function(data){
						if(data ){
							//测试数据
//							var rowsTemp = [{"cpuSpeed":"2GHz","cpuMulCpuRate":"5.3%","resourceName_ForSort":"CPU1","cpuSpeed_ForSort":"2.00","cpuMulCpuRate_ForSort":"5.30","resourceRealName":"CPU1","cpuModel":"Intel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","resourceState":"NORMAL","resourceNameAndState_ForSort":"CPU1","cpuModel_ForSort":"Intel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","instanceid":"667503","resourceName":"CPU1"},
//							                {"cpuSpeed":"2GHz","cpuMulCpuRate":"4.8%","resourceName_ForSort":"CPU0","cpuSpeed_ForSort":"2.00","cpuMulCpuRate_ForSort":"4.80","resourceRealName":"CPU0","cpuModel":"Intel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","resourceState":"NORMAL","resourceNameAndState_ForSort":"CPU0","cpuModel_ForSort":"Intel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","instanceid":"667502","resourceName":"CPU0"},
//							                {"cpuSpeed":"2GHz","cpuMulCpuRate":"29.93%","resourceName_ForSort":"CPU2","cpuSpeed_ForSort":"2.00","cpuMulCpuRate_ForSort":"29.93","resourceRealName":"CPU2","cpuModel":"Intel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","resourceState":"NORMAL","resourceNameAndState_ForSort":"CPU2","cpuModel_ForSort":"Intel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","instanceid":"667504","resourceName":"CPU2"},
//							                {"cpuSpeed":"2GHz","cpuMulCpuRate":"19.1%","resourceName_ForSort":"CPU3","cpuSpeed_ForSort":"2.00","cpuMulCpuRate_ForSort":"19.10","resourceRealName":"CPU3","cpuModel":"--","resourceState":"NORMAL","resourceNameAndState_ForSort":"CPU3","cpuModel_ForSort":"--","instanceid":"667505","resourceName":"CPU3"}];
//							data = {"data":{"startRow":0,"rowCount":4,"totalRecord":4,"condition":null,"rows":[
//						            {"cpuSpeed":"2GHz","cpuMulCpuRate":"5.3%","resourceName_ForSort":"CPU1","cpuSpeed_ForSort":"2.00","cpuMulCpuRate_ForSort":"5.30","resourceRealName":"CPU1","cpuModel":"Intel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","resourceState":"NORMAL","resourceNameAndState_ForSort":"CPU1","cpuModel_ForSort":"Intel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","instanceid":"667503","resourceName":"CPU1"},
//						            {"cpuSpeed":"2GHz","cpuMulCpuRate":"4.8%","resourceName_ForSort":"CPU0","cpuSpeed_ForSort":"2.00","cpuMulCpuRate_ForSort":"4.80","resourceRealName":"CPU0","cpuModel":"--","resourceState":"NORMAL","resourceNameAndState_ForSort":"CPU0","cpuModel_ForSort":"--","instanceid":"667502","resourceName":"CPU0"},
//						            {"cpuSpeed":"2GHz","cpuMulCpuRate":"29.93%","resourceName_ForSort":"CPU2","cpuSpeed_ForSort":"2.00","cpuMulCpuRate_ForSort":"29.93","resourceRealName":"CPU2","cpuModel":"kntel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","resourceState":"NORMAL","resourceNameAndState_ForSort":"CPU2","cpuModel_ForSort":"kntel(R) Xeon(R) CPU E5-2650 0 @ 2.00GHz","instanceid":"667504","resourceName":"CPU2"},
//						            {"cpuSpeed":"2GHz","cpuMulCpuRate":"19.1%","resourceName_ForSort":"CPU3","cpuSpeed_ForSort":"2.00","cpuMulCpuRate_ForSort":"19.10","resourceRealName":"CPU3","cpuModel":"--","resourceState":"NORMAL","resourceNameAndState_ForSort":"CPU3","cpuModel_ForSort":"--","instanceid":"667505","resourceName":"CPU3"}],"total":4},"code":200};
							
							var rowsTemp = data.data.rows;
							//如果是业务点击打开,进行instanceid过滤
							if(openResourceDisplayType == 'normalResourceForBizOpen'&& !ifParentRes){
								rowsTemp = datagridDataFilter(rowsTemp);
								data.data.rows = rowsTemp;
							}
							
							var queryData = new Array();
							if("NetInterface"==metricTypePage && "NetworkDevice"==mainResourceTypePage){
								
								for(var i=0;i<rowsTemp.length;i++){
									var rowsObj = rowsTemp[i];
									var interfaceStr = null;
//									if(rowsObj["resourceNameAndState"]){
//										interfaceStr = rowsObj["resourceNameAndState"];
//									}else if(rowsObj["resourceName"]){
//										interfaceStr = rowsObj["resourceName"];
//									}
									if(rowsObj["resourceRealName"]){
										interfaceStr = rowsObj["resourceRealName"];
									}
									var query = {"instanceId":instanceIdPage,"interface":interfaceStr};
//									query = JSON.stringify(query).replace(",","@");
									queryData.push(query);
								}
								if(rowsTemp.length>0){
									queryDownDevice(queryData,rowsTemp,data,datagrid);
								}
							}else{
								globalData[metricTypePage] = data;
								datagrid.selector.datagrid('loadData',data);
							}
						}
			    		
					}
				});
				
			}
		}
	});
}
function queryDownDevice(queryData,rowsTemp,data,datagrid){
	var queryNum = 50;
	
	if(queryData.length>queryNum){
		var queryDataArrOne = queryData.slice(0,queryNum);
		var queryDataArrTwo = queryData.slice(queryNum,queryData.length);
		
//		console.log(queryDataArrOne);
//		console.log(queryDataArrTwo);
		var queryObj = "";
		for(var i=0;i<queryDataArrOne.length;i++){
			queryObj += JSON.stringify(queryDataArrOne[i]).replace(",","@");
			queryObj +=",";
		}
		queryObj = queryObj.substr(0,queryObj.length-1);
		if(!queryObj) return;
//		var queryObj = JSON.stringify(queryDataArrOne).replace(/\\/g,"");
		
		oc.util.ajax({
			url:oc.resource.getUrl("topo/mac/updevice/batchInfos.htm"),
			data:{ipInterfaces:queryObj},
			successMsg:null,
			type:'post',
			success:function(dataIn){
				if(dataIn.data){
					var dataArr = dataIn.data;
					var dataArrObj = {};
					for(var i=0;i<dataArr.length;i++){
						var dataObj = dataArr[i];
						if(dataObj.datas){
							dataArrObj[dataObj['interface']] = dataObj.datas;
						}
					}
					
					for(var i=0;i<rowsTemp.length;i++){
						var rowsObj = rowsTemp[i];
						var rowsObjName = null;
						if(rowsObj["resourceNameAndState"]){
							rowsObjName = rowsObj["resourceNameAndState"];
						}else if(rowsObj["resourceName"]){
							rowsObjName = rowsObj["resourceName"];
						}
						if(dataArrObj[rowsObjName] && dataArrObj[rowsObjName][0] && dataArrObj[rowsObjName][0].ip && dataArrObj[rowsObjName][0].upDeviceName && dataArrObj[rowsObjName][0].mac){
							var ipStr = dataArrObj[rowsObjName][0].ip.split(",")[0];
							rowsObj['deviceIp'] = ipStr;
//							rowsObj['metric02'] = dataArrObj[rowsObjName][0].upDeviceName;
//							rowsObj['metric03'] = dataArrObj[rowsObjName][0].mac;
						}
					}
				}
				
				queryDownDevice(queryDataArrTwo,rowsTemp,data,datagrid);
					
			}
		});
		
	}else{
		var queryObj2 = "";
		for(var x=0;x<queryData.length;x++){
			queryObj2 += JSON.stringify(queryData[x]).replace(",","@");
			queryObj2 +=",";
		}
		queryObj2 = queryObj2.substr(0,queryObj2.length-1);
		
		oc.util.ajax({
			url:oc.resource.getUrl("topo/mac/updevice/batchInfos.htm"),
			data:{ipInterfaces:queryObj2},
			successMsg:null,
			type:'post',
			success:function(dataIn){
				if(dataIn.data){
					var dataArr = dataIn.data;
					var dataArrObj = {};
					for(var i=0;i<dataArr.length;i++){
						var dataObj = dataArr[i];
						if(dataObj.datas){
							dataArrObj[dataObj['interface']] = dataObj.datas;
						}
					}
					
					for(var i=0;i<rowsTemp.length;i++){
						var rowsObj = rowsTemp[i];
						var rowsObjName = null;
						if(rowsObj["resourceNameAndState"]){
							rowsObjName = rowsObj["resourceNameAndState"];
						}else if(rowsObj["resourceName"]){
							rowsObjName = rowsObj["resourceName"];
						}
						if(dataArrObj[rowsObjName] && dataArrObj[rowsObjName][0] && dataArrObj[rowsObjName][0].ip && dataArrObj[rowsObjName][0].upDeviceName && dataArrObj[rowsObjName][0].mac){
							var ipStr = dataArrObj[rowsObjName][0].ip.split(",")[0];
							rowsObj['deviceIp'] = ipStr;
							
//							rowsObj['metric02'] = dataArrObj[rowsObjName][0].upDeviceName;
//							rowsObj['metric03'] = dataArrObj[rowsObjName][0].mac;
						}
						
					}
					
					
					data.data.rows = rowsTemp;
					globalData[metricTypePage] = data;
					datagrid.selector.datagrid('loadData',data);
				}
					
			}
		});
	}
}

function checkHaveSameName(name,data){
	var rowsTemp = data.data.rows;
	for(var i=0;i<rowsTemp.length;i++){
		var rowsObj = rowsTemp[i];
		if(rowsObj["resourceName"] && rowsObj["resourceName"]==name){
			return true;
		}
	}
	return false;
}

function getCurrentGlobalData(type){
	if(globalData[type]){
		return globalData[type];
	}
}
function getCurrentMetricType(){
	var currentMetricTypePage = metricTypePage;
	return function (){
		return currentMetricTypePage;
	};
}

/******************** 
* 取窗口滚动条高度  
******************/  
function getScrollTop()  
{  
    var scrollTop=0;  
    if(document.documentElement&&document.documentElement.scrollTop)  
    {  
        scrollTop=document.documentElement.scrollTop;  
    }  
    else if(document.body)  
    {  
        scrollTop=document.body.scrollTop;  
    }  
    return scrollTop;  
}  
  
  
/******************** 
* 取窗口可视范围的高度  
*******************/  
function getClientHeight()  
{  
    var clientHeight=0;  
    if(document.body.clientHeight&&document.documentElement.clientHeight)  
    {  
        var clientHeight = (document.body.clientHeight<document.documentElement.clientHeight)?document.body.clientHeight:document.documentElement.clientHeight;          
    }  
    else  
    {  
        var clientHeight = (document.body.clientHeight>document.documentElement.clientHeight)?document.body.clientHeight:document.documentElement.clientHeight;      
    }  
    return clientHeight;  
}
  
/******************** 
* 取文档内容实际高度  
*******************/  
function getScrollHeight()  
{  
    return Math.max(document.body.scrollHeight,document.documentElement.scrollHeight);  
}  

//function test(){  
//	
//	
//	if (getScrollTop()+getClientHeight()==getScrollHeight()){  
//	    alert("到达底部");  
//	}else{  
//	    alert("没有到达底部");  
//	}  
//} 


/******************** 
* 图标tab显示隐藏 
*******************/  
function showHideChartMethod(rootDiv,datagridDiv,chartDiv,tabTitleDiv,showType){
	var rootDivHeight = rootDiv.height();
    //显示图标时,datagridDiv高度
    var showMetricDatagridDivHeight = rootDivHeight*0.45;
    var tabTitleHeight = tabTitleDiv.parent().height();
    
    if(argsLengthFlag){
    	showMetricDatagridDivHeight = 300;
    	if(!(datagridDiv.height()>showMetricDatagridDivHeight)){
    		//表示收缩
    		chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_down');
    		
    		//有showType表示点击小图标展示线图
    		if(showType && 'show'==showType){
    			chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_open');
    		}else{
    			showMetricDatagridDivHeight = rootDivHeight ;//- tabTitleHeight;
    		}
    		
    	}else{
    		chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_open');
    	}
    	//表格和图标高度控制	
    	datagridDiv.height(showMetricDatagridDivHeight);
    	var showMetricDatagridParentDiv = datagridDiv.find('#showMetricDatagridParent');
    	showMetricDatagridParentDiv.height(showMetricDatagridDivHeight-tabTitleHeight-10);
    	chartDiv.height(rootDivHeight-showMetricDatagridDivHeight);
    	
    	var headerHeight = showMetricDatagridParentDiv.find('.datagrid-header').height();
    	showMetricDatagridParentDiv.find('.datagrid-body').each(function(e){
    		var targetTemp =  $(this);
    		targetTemp.height(showMetricDatagridDivHeight-tabTitleHeight-headerHeight);
    	});
    }else{
    	if(!(datagridDiv.height()>showMetricDatagridDivHeight)){
    		//表示收缩
    		chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_down');
    		
    		//有showType表示点击小图标展示线图
    		if(showType && 'show'==showType){
    			chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_open');
    		}else{
    			showMetricDatagridDivHeight = rootDivHeight - tabTitleHeight + 35;
    		}
    		
    	}else{
    		chartDiv.find('#metricChart_DOWN_OPEN').attr('class','metricChart_open');
    	}
    	//表格和图标高度控制	
    	datagridDiv.height(showMetricDatagridDivHeight);
    	var showMetricDatagridParentDiv = datagridDiv.find('#showMetricDatagridParent');
    	showMetricDatagridParentDiv.height(showMetricDatagridDivHeight-tabTitleHeight-10);
    	chartDiv.height(rootDivHeight-showMetricDatagridDivHeight+2);
    	
    	var headerHeight = showMetricDatagridParentDiv.find('.datagrid-header').height();
    	showMetricDatagridParentDiv.find('.datagrid-body').each(function(e){
    		var targetTemp =  $(this);
    		targetTemp.height(showMetricDatagridDivHeight-tabTitleHeight-10-headerHeight);
    	});
    }
}

/******************** 
* 页面内查询
*******************/  
function datagridDataFilter(data){
	var rowsNew = new Array();
	if(data)
	for(var i=0;i<data.length;i++){
		var instanceId = data[i].instanceid;
		if(bizOpenFilterInstanceId[instanceId]){
			rowsNew.push(data[i]);
		}
	}
	
	return rowsNew;
}
function queryMethod(showMetricToolForm, datagrid, type){
	// 查询是否逻辑接口
	var isCheckedLogic = showMetricToolForm.find(".logicInterfaceOnOff").is(":checked");
	// 查询可用性
	var selectObj = showMetricToolForm.find('#useableSelect');
	var selectValue = null,result = new Array();
	if(selectObj.attr('useableFlag')){
		selectValue = selectObj.combobox('getValue');
		if(selectValue && 'ALL'==selectValue){
			selectValue = null;
		}
		//如果主资源可用，使用过滤条件；如果主资源不可用，则结果全为不可用 dfw 20161226
		//查询不可用
		if(selectValue == 'CRITICAL'){
            selectValue = mainResourceStatePage == 'CRITICAL' || mainResourceStatePage == 'CRITICAL_NOTHING' ? null : selectValue;
		} else if(selectValue == 'NORMAL'){
			//查询可用，但主资源是不可用，设置可用状态为一个不存在的字符串
			selectValue = mainResourceStatePage == 'CRITICAL' || mainResourceStatePage == 'CRITICAL_NOTHING' ? 'noResult' : selectValue;
		}
	}
	// 查询名称
	var showName = showMetricToolForm.find("input[id=resourceShowName]").val();
	
	result = getCurrentGlobalData(type).data.rows;
	if(selectValue){
//		if(mainResourceStatePage=='CRITICAL' ){
//			if(selectValue=='CRITICAL'){
//				return ;
//			}
//		}
//		else if(mainResourceStatePage=='UNKOWN'){
//			if(selectValue=='UNKOWN'){
//				return ;
//			}
//		}
		var metricIdArr = new Array();
		metricIdArr.push(selectObj.attr('metricId'));
		if(getCurrentGlobalData(type)){
			result = queryValueByMetricId(metricIdArr, selectValue, result);
		}
	}
	if(showName){
		var metricIdArr2 = new Array();
		metricIdArr2.push('resourceNameAndState');
		metricIdArr2.push('resourceName');
		if(getCurrentGlobalData(type)){
			result = queryLikeByMetricId(metricIdArr2, showName, result);
		}
	}
	if(!isCheckedLogic){
		var metricIdArr3 = new Array();
		metricIdArr3.push('ifType');
		if(getCurrentGlobalData(type)){
			result = queryValueByMetricId(metricIdArr3, isCheckedLogic, result);
		}
	}
	var localData = {"data":{"startRow":0,"rowCount":result.length,"totalRecord":result.length,"condition":null,
		"rows":result,"total":result.length},"code":200};
	
	datagrid.selector.datagrid('loadData', localData);
}
function queryValueByMetricId(metricIdArr, value, rowsData){
	var queryResult = new Array();
	if(!rowsData){
		return queryResult;
	}
	for(var i=0;i<rowsData.length;i++){
		var rowDataObj = rowsData[i];
		for(var x=0; x < metricIdArr.length; x++){
			if("ifType" == metricIdArr[x] && (rowDataObj[metricIdArr[x]] == 'ethernetCsmacd' || rowDataObj[metricIdArr[x]] == 'gigabitEthernet')){
				queryResult.push(rowDataObj);
				break;
			}
			if("UNKOWN" == value && rowDataObj[metricIdArr[x]] && (rowDataObj[metricIdArr[x]].toLowerCase()==value.toLowerCase() || rowDataObj[metricIdArr[x]]=='--')){
				queryResult.push(rowDataObj);
				break;
			}else if(rowDataObj[metricIdArr[x]] && rowDataObj[metricIdArr[x]]==value){
				queryResult.push(rowDataObj);
				break;
			}
		}
	}
	return queryResult;
}
function queryLikeByMetricId(metricIdArr,value,rowsData){
	var queryResult = new Array();
	if(!rowsData){
		return queryResult;
	}
	for(var i=0;i<rowsData.length;i++){
		var rowDataObj = rowsData[i];
		for(var x=0;x<metricIdArr.length;x++){
			if(rowDataObj[metricIdArr[x]] && rowDataObj[metricIdArr[x]].toLowerCase().indexOf(value.toLowerCase())!=-1){
				queryResult.push(rowDataObj);
				break;
			}
		}
	}
	return queryResult;
}

//将datagrid,Rows按指标排序
function sortRowsByMetricId(rows,targetMetricId,sortName){
	//sortName :  asc   desc
	var rowsLength = rows.length-1;
	
	if(sortName == 'desc'){
		for(var i=0;i<rowsLength;i++){
			for(var x=0;x<rowsLength-i;x++){
				var sourceX_NUM = rows[x][targetMetricId+"_ForSort"];
				var sourceXOne_NUM = rows[x+1][targetMetricId+"_ForSort"];
				
				var metricX_NUM = -1;
				var metricXOne_NUM = -1;
				
				if(null!=sourceX_NUM && '--'!=sourceX_NUM && 'N/A'!=sourceX_NUM && undefined!=sourceX_NUM){
					var sourceX_NUMFloat = parseFloat(sourceX_NUM);
					if(isNaN(sourceX_NUMFloat)){
						metricX_NUM = sourceX_NUM.toLowerCase();
					}else{
						metricX_NUM = sourceX_NUMFloat;
					}
				}else if('N/A'==sourceX_NUM){
					metricX_NUM = -2;
				}
				
				if(null!=sourceXOne_NUM && '--'!=sourceXOne_NUM && 'N/A'!=sourceXOne_NUM && undefined!=sourceXOne_NUM){
					var sourceXOne_NUMFloat = parseFloat(sourceXOne_NUM);
					if(isNaN(sourceXOne_NUMFloat)){
						metricXOne_NUM = sourceXOne_NUM.toLowerCase();
					}else{
						metricXOne_NUM = sourceXOne_NUMFloat;
					}
				}else if('N/A'==sourceXOne_NUM){
					metricXOne_NUM = -2;
				}
				
				if(sourceX_NUM=='--' || sourceXOne_NUM=='--' || sourceX_NUM=='N/A' || sourceXOne_NUM=='N/A'){
					//比较值其中之一是-- , --比较值应为小
					if(sourceX_NUM=='N/A' && sourceXOne_NUM!='N/A'){
						var sourceRow = rows[x];
						var sourceRowOne = rows[x+1];
						
						rows[x] = sourceRowOne;
						rows[x+1] = sourceRow;
					}
					//不用调整
//					if(sourceXOne_NUM=='N/A'){
//						
//					}
					if(sourceX_NUM=='--' && (sourceXOne_NUM!='N/A' || sourceXOne_NUM!='--')){
						var sourceRow = rows[x];
						var sourceRowOne = rows[x+1];
						
						rows[x] = sourceRowOne;
						rows[x+1] = sourceRow;
					}
					//不用调整
//					if(sourceXOne_NUM=='--'){
//						
//					}
					
				}else if(metricXOne_NUM>metricX_NUM){
					var sourceRow = rows[x];
					var sourceRowOne = rows[x+1];
					
					rows[x] = sourceRowOne;
					rows[x+1] = sourceRow;
				}
			}
		}
	}else if(sortName == 'asc'){
		for(var i=0;i<rowsLength;i++){
			for(var x=0;x<rowsLength-i;x++){
				var sourceX_NUM = rows[x][targetMetricId+"_ForSort"];
				var sourceXOne_NUM = rows[x+1][targetMetricId+"_ForSort"];
				
				var metricX_NUM = -1;
				var metricXOne_NUM = -1;
				
				if(null!=sourceX_NUM && '--'!=sourceX_NUM && 'N/A'!=sourceX_NUM && undefined!=sourceX_NUM){
					var sourceX_NUMFloat = parseFloat(sourceX_NUM);
					if(isNaN(sourceX_NUMFloat)){
						metricX_NUM = sourceX_NUM.toLowerCase();
					}else{
						metricX_NUM = sourceX_NUMFloat;
					}
				}else if('N/A'==sourceX_NUM){
					metricX_NUM = -2;
				}
				
				if(null!=sourceXOne_NUM && '--'!=sourceXOne_NUM && 'N/A'!=sourceXOne_NUM && undefined!=sourceXOne_NUM){
					var sourceXOne_NUMFloat = parseFloat(sourceXOne_NUM);
					if(isNaN(sourceXOne_NUMFloat)){
						metricXOne_NUM = sourceXOne_NUM.toLowerCase();
					}else{
						metricXOne_NUM = sourceXOne_NUMFloat;
					}
				}else if('N/A'==sourceXOne_NUM){
					metricXOne_NUM = -2;
				}
				
				if(sourceX_NUM=='--' || sourceXOne_NUM=='--' || sourceX_NUM=='N/A' || sourceXOne_NUM=='N/A'){
					//比较值其中之一是-- , --比较值应为小
					if(sourceXOne_NUM=='N/A' && sourceX_NUM!='N/A'){
						var sourceRow = rows[x];
						var sourceRowOne = rows[x+1];
						
						rows[x] = sourceRowOne;
						rows[x+1] = sourceRow;
					}
					
					if(sourceXOne_NUM=='--' && (sourceX_NUM!='N/A' || sourceX_NUM!='--')){
						var sourceRow = rows[x];
						var sourceRowOne = rows[x+1];
						
						rows[x] = sourceRowOne;
						rows[x+1] = sourceRow;
					}
					
					
				}else if(metricXOne_NUM<metricX_NUM){
					var sourceRow = rows[x];
					var sourceRowOne = rows[x+1];
					
					rows[x] = sourceRowOne;
					rows[x+1] = sourceRow;
				}
			}
		}
	}
	return rows;
}

//将字符串内中文换算为两个长度
function getStrLength(str){
	var strNew = str.replace(/([^\u0000-\u00FF])/g, function ($) { return escape('--'); });
	return strNew.length;
}

	oc.resourced.resource.detail.showmetric = {
		//入口方法
		open:function(instanceId , metricType ,metricTypeName ,mainResourceState,mainResourceType){
			openResourceDisplayType = 'normalResource';
			
			instanceIdPage = instanceId;
			metricTypePage = metricType;
			metricTypeNamePage = metricTypeName;
			mainResourceStatePage = mainResourceState;
			mainResourceTypePage = mainResourceType;
			bottomObj.isBottom = false;
			vminstanceIdArr = null;
			init();
		},
		openVMResource:function(instanceId , resourceId ,metricTypeName ,mainResourceState,vminstanceIdList,param){
			openResourceDisplayType = 'VMResource';
			
			metricTypeNamePage = "";
			instanceIdPage = instanceId;
			metricTypePage = resourceId;
			metricTypeNamePage = metricTypeName;
			mainResourceStatePage = mainResourceState;
			mainResourceTypePage = "VMResource";
			bottomObj.isBottom = false;
			vminstanceIdArr = vminstanceIdList;
			if(param){
				argsLengthFlag = param.argsLengthFlag;
				if(argsLengthFlag){
					$('#showMetric').height(595);
				}
			}
			init();
		},
		openByBiz:function(args){
			openResourceDisplayType = 'normalResourceForBizOpen';
			instanceIdPage = args.instanceId;
			metricTypePage = args.metricType;
			metricTypeNamePage = args.metricTypeName;
			mainResourceStatePage = args.mainResourceState;
			mainResourceTypePage = args.mainResourceType;
			bottomObj.isBottom = false;
			bizOpenFilterInstanceId = args.childIdMap;
			vminstanceIdArr = null;
			ifParentRes = args.ifParentRes;
			argsLengthFlag = args.argsLengthFlag;
			if(argsLengthFlag){
				$('#showMetric').height(595);
			}
			
			init();
		}
	};
})(jQuery);

//var localData = {"data":
//{"startRow":0,"rowCount":6,"totalRecord":6,"condition":null,
//	"rows":[{"instanceId":1,"cpuRateValue":1.0,"cpumhz":1.0,"cpumodel":'123'},
//	{"instanceId":0,"cpuRateValue":0.0,"cpumhz":0.0,"cpumodel":null},
//	{"instanceId":0,"cpuRateValue":0.0,"cpumhz":0.0,"cpumodel":null},
//	{"instanceId":0,"cpuRateValue":0.0,"cpumhz":0.0,"cpumodel":null},
//	{"instanceId":0,"cpuRateValue":0.0,"cpumhz":0.0,"cpumodel":null},
//	{"instanceId":0,"cpuRateValue":0.0,"cpumhz":0.0,"cpumodel":null}],
//	"total":6},"code":200};
//datagrid.selector.datagrid('loadData',localData);