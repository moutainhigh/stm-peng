$(function(){
	var $alertWindow = $('#alertPageContainer'),reportFileId = $alertWindow.data("reportFileId"),reportId=$alertWindow.data("reportId"),reportName=$alertWindow.data("reportName"),$reportManage = $('#reportManage');
	var expect = $alertWindow.data("expect");//?$alertWindow.data("expect")[0]:undefined;
	var errorBizs = [];//出现告警的业务ID集合
//	{
//			available:95,
//			mttr:1,
//			mtbf:15,
//			downTimes:3,
//			downDuration:3,
//			alarmTimes:10,
//			unrecoveryAlarmTimes:2};
	
	if(expect){
		showExpectBtn();
	}else{
		$alertWindow.find("#isNotExpect").show();
		$alertWindow.find("#isExpect").hide();
	}
	if(reportFileId){
		if(reportName){
			$alertWindow.find('.pop_title').html(reportName);
		}
		loadReportInfo(reportFileId);
	}
	
	function showExpectBtn(){
		$alertWindow.find("#isNotExpect").hide();
		$alertWindow.find("#isExpect").show();
		if(!expect.id || expect.id == ""){
			$alertWindow.find("#saveExpect").show();
		}else{
			$alertWindow.find("#saveExpect").hide();
		}
	}
	
	/**
	 * 加载报表详细数据
	 * */
	function loadReportInfo(report){
		oc.util.ajax({
			url:oc.resource.getUrl("simple/manager/workbench/getReportDetailInfo.htm"),
			data:{reportFileId:reportFileId},
			successMsg:'',
			success:function(data){
				if(data.data){
					var title = data.data.reportTitle;//报表title
					$alertWindow.find('.pop_title').html(title);
					initReportList(data.data);
				}
			}
		});
	}
	
	/**
	 * 初始化报表页面
	 * */
	function initReportList(reportData){
		if(reportData){
			var directories = reportData.directories;//报表章节
			if(directories){
				for(var i=0;i<directories.length;i++){
					var dirHtml = '<div style="width:100%;" id="directorie_div_'+i+'"></div>';
					$alertWindow.find('#report_detail_list_info').append(dirHtml);
					var directiorie = directories[i];
					var dirName = directiorie.name;//章节名称
					var dirDatas = directiorie.businessDatas;//一个章节的数据，多个表格
					if(dirDatas){
						for(var j=0;j<dirDatas.length;j++){
							var tableHtml = '<div class="Mtable" style="border:0px;"><h2>'+dirName+'</h2></div><table class="Mtable" id="directorie_div_'+i+'_table_'+j+'" ><thead></thead><tbody></tbody></table>'
							$alertWindow.find('#report_detail_list_info #directorie_div_'+i).append(tableHtml);
							var tables = dirDatas[j];//章节里的表格
							if(tables){
								//表格里数据的每一行
								var chartDatas = [];//用于存放图形报表所需要的数据
								for(var k=0;k<tables.length;k++){
									var row = tables[k];
									if(row){
										var columns = row.metricDatas;
										var rowTitles = [];
										rowTitles.push("");
										
										//用于存放表格显示的每一行的数据
										var rowData = [];
										var rowObj = {id:row.id,name:row.name,value:row.name};//列头。存放业务ID、业务名称 
										rowData.push(rowObj);
										for(var l=0;l<columns.length;l++){
											var rd = columns[l];
											if(k==0){
												rowTitles.push(rd.metricName);
											}
											rowObj = {id:rd.metricId,name:rd.metricName,value:rd.metridValue}
											rowData.push(rowObj);
											var charData = {
														metridName:rd.metricName,
														bizName:row.name,
														value:rd.metridValue
													};
											chartDatas.push(charData);
										}
										if(k==0){
											createTableTitle('directorie_div_'+i+'_table_'+j,rowTitles);
										}
										createTableRow('directorie_div_'+i+'_table_'+j,rowData,k%2==0?'odd_bg':'even_bg');
									}
								}
								createHighchart('directorie_div_'+i,chartDatas);
							}
						}
					}
					
				}
				//如果分析报表，无异常报表不显示通过联系人和处理问题的按钮
				if(expect){
					if(errorBizs && errorBizs.length>0){
						showExpectBtn();
					}else{
						$alertWindow.find("#noticeContact,#handlingProblem").hide();
					}
				}
			}
		}
	}
	
	/**
	 * 创建表格表头
	 * */
	function createTableTitle(tableId,titles){
		if(titles){
			var titleRowHtml = "<tr>";
			for(var i=0;i<titles.length;i++){
				if(i==0){
					titleRowHtml+='<th class="Mtable_th_l">'+titles[i]+'</th>';
				}else if(i==(titles.length-1)){
					titleRowHtml+='<th class="Mtable_th_r">'+titles[i]+'</th>';
				}else{
					titleRowHtml+='<th>'+titles[i]+'</th>';
				}
			}
			titleRowHtml+="</tr>";
			$alertWindow.find('#'+tableId+' thead').append(titleRowHtml);
			
			if(expect){
				var expectRow = "<tr class='Mdefeat_bg'>";
				for(var i=0;i<titles.length;i++){
					if(i==0){
						expectRow+="<td>参考值</td>"
					}else{
						if(titles[i]=='可用率(%)'){
							if(expect.available && expect.available!=-1){
								expectRow+="<td>"+expect.available+"</td>";
							}else{
								expectRow+="<td>-</td>";
							}
						}else if(titles[i]=='MTTR(小时)'){
							if(expect.mttr && expect.mttr!=-1){
								expectRow+="<td>"+expect.mttr+"</td>";
							}else{
								expectRow+="<td>-</td>";
							}
						}else if(titles[i]=='MTBF(天)'){
							if(expect.mtbf && expect.mtbf!=-1){
								expectRow+="<td>"+expect.mtbf+"</td>";
							}else{
								expectRow+="<td>-</td>";
							}
						}else if(titles[i]=='宕机次数(次)'){
							if(expect.downTimes && expect.downTimes!=-1){
								expectRow+="<td>"+expect.downTimes+"</td>";
							}else{
								expectRow+="<td>-</td>";
							}
						}else if(titles[i]=='宕机时长(小时)'){
							if(expect.downDuration && expect.downDuration!=-1){
								expectRow+="<td>"+expect.downDuration+"</td>";
							}else{
								expectRow+="<td>-</td>";
							}
						}else if(titles[i]=='告警数量'){
							if(expect.alarmTimes && expect.alarmTimes!=-1){
								expectRow+="<td>"+expect.alarmTimes+"</td>";
							}else{
								expectRow+="<td>-</td>";
							}
						}else if(titles[i]=='未恢复告警数量'){
							if(expect.unrecoveryAlarmTimes && expect.unrecoveryAlarmTimes!=-1){
								expectRow+="<td>"+expect.unrecoveryAlarmTimes+"</td>";
							}else{
								expectRow+="<td>-</td>";
							}
						}
					}
				}
				expectRow+="</tr>";
			}
			$alertWindow.find('#'+tableId+' tbody').append(expectRow);
		}
	}
	
	
	/**
	 * 创建表格行数据
	 * */
	function createTableRow(tableId,rowData,rowClass){
		if(rowData){
			var errorObj;
			var isErrorData = false;
			if (rowData[0].value != null) {
				var rowHtml = '<tr class="' + rowClass + '">';
				var title = [];
				for (var i = 0; i < rowData.length; i++) {
					var rowValue = '-';
					var tdClass = "";
					if (i == 0) {
						tdClass="Mtable_td_one";
						title.push(rowData[i].name);
					}
					if(rowData[i].value!=null && rowData[i].value!="null"){
						if(expect){
							var cellValue = parseFloat(rowData[i].value);
							if(rowData[i].name=='可用率(%)'){
								if(!isEmpty(expect.available)){
									if(cellValue<=parseFloat(expect.available)){
										tdClass='color_red';
										isErrorData=true;
									}
								}
							}else if(rowData[i].name=='MTTR(小时)'){
								if(!isEmpty(expect.mttr)){
									if(cellValue>parseFloat(expect.mttr)){
										tdClass='color_red';
										isErrorData=true;
									}
								}
							}else if(rowData[i].name=='MTBF(天)'){
								if(!isEmpty(expect.mtbf)){
									if(cellValue<=parseFloat(expect.mtbf)){
										tdClass='color_red';
										isErrorData=true;
									}
								}
							}else if(rowData[i].name=='宕机次数(次)'){
								if(!isEmpty(expect.downTimes)){
									if(cellValue>parseFloat(expect.downTimes)){
										tdClass='color_red';
										isErrorData=true;
									}
								}
								
							}else if(rowData[i].name=='宕机时长(小时)'){
								if(!isEmpty(expect.downDuration)){
									if(cellValue>parseFloat(expect.downDuration)){
										tdClass='color_red';
										isErrorData=true;
									}
								}
							}else if(rowData[i].name=='告警数量'){
								if(!isEmpty(expect.alarmTimes)){
									if(cellValue>parseFloat(expect.alarmTimes)){
										tdClass='color_red';
										isErrorData=true;
									}
								}
							}else if(rowData[i].name=='未恢复告警数量'){
								if(!isEmpty(expect.unrecoveryAlarmTimes)){
									if(cellValue>parseFloat(expect.unrecoveryAlarmTimes)){
										tdClass='color_red';
										isErrorData=true;
									}
								}
							}
						}
						rowValue = rowData[i].value;
					}
					rowHtml += '<td class="'+tdClass+'">' + rowValue + '</td>';
				}
				rowHtml += '</tr>';
				$alertWindow.find('#' + tableId + ' tbody').append(rowHtml);
			}
			if(isErrorData){
				errorObj = new Object();
				errorObj.bizName = rowData[0].name;
				errorObj.bizId = rowData[0].id;
			}
			if(errorObj){
				errorBizs.push(errorObj);
			}
		}
	}
	/**
	 * 创建Highchart图形报表
	 * */
	function createHighchart(chartDiv,datas){
		var chartDatas = {};
		for(var i=0;i<datas.length;i++){
			var metric = datas[i];
			if(!chartDatas[metric.metridName]){
				chartDatas[metric.metridName] = [];
			}
			chartDatas[metric.metridName].push(metric);
		}
		if(chartDatas){
			for(var metric in chartDatas){
				var chartDivId = oc.util.generateId();
				var chartHtml = '<div class="Mreport_img" id="'+chartDivId+'"></div>';
				$alertWindow.find('#'+chartDiv).append(chartHtml);
				var cdata = chartDatas[metric];
				var categories = [];
				
				var normalData = [];//正常数据data
				var overData = [];//超标数据data
				var options = {};
				if(cdata){
					for(var i=0;i<cdata.length;i++){
						if(cdata[i].bizName!=null && cdata[i].value!=null){
							categories.push(cdata[i].bizName);
							//color #0A8E0C 正常绿色 ;#871B17 异常
							var color = "#0A8E0C";
							var cellValue = 0;
							if(cdata[i].value && cdata[i].value!=null && cdata[i].value!="null"){
								var cellValue = parseFloat(cdata[i].value);
							}
							var flag = true;
							if(metric=='可用率(%)'){
								options = {
										yAxis: {
											max:100,
											tickInterval: 20,
								            labels:{
								                formatter:function(){
								                	return this.value+"%";
								                }
								            }
								        }
								};
								if(expect){
									if(!isEmpty(expect.available)){
										if(cellValue<=parseFloat(expect.available)){
											flag= false;
										}
									}
								}
								
							}else if(metric=='MTTR(小时)'){
								options = {
										yAxis: {
											max:50,
											tickInterval: 5
								        }
								};
								if(expect){
									if(!isEmpty(expect.mttr)){
										if(cellValue>parseFloat(expect.mttr)){
											flag= false;
										}
									}
								}
							}else if(metric=='MTBF(天)'){
								options = {
										yAxis: {
											max:50,
											tickInterval: 5
								        }
								};
								if (expect) {
									if (!isEmpty(expect.mtbf)) {
										if (cellValue <= parseFloat(expect.mtbf)) {
											flag= false;
										}
									}
								}
								
							}else if(metric=='宕机次数(次)'){
								options = {
										yAxis: {
											max:50,
											tickInterval: 5
								        }
								};
								if(expect){
									if(!isEmpty(expect.downTimes)){
										if(cellValue>parseFloat(expect.downTimes)){
											flag= false;
										}
									}
								}
							}else if(metric=='宕机时长(小时)'){
								options = {
										yAxis: {
											max:50,
											tickInterval: 5
								        }
								};
								if(expect){
									if(!isEmpty(expect.downDuration)){
										if(cellValue>parseFloat(expect.downDuration)){
											flag= false;
										}
									}
								}
							}else if(metric=='告警数量'){
								options = {
										yAxis: {
											max:100,
											tickInterval: 10
								        }
								};
								if(expect){
									if(!isEmpty(expect.alarmTimes)){
										if(cellValue>parseFloat(expect.alarmTimes)){
											flag= false;
										}
									}
								}
							}else if(metric=='未恢复告警数量'){
								options = {
										yAxis: {
											max:100,
											tickInterval: 10
								        }
								};
								if(expect){
									if(!isEmpty(expect.unrecoveryAlarmTimes)){
										if(cellValue>parseFloat(expect.unrecoveryAlarmTimes)){
											flag= false;
										}
									}
								}
							}
//							var cobj ={name:cdata[i].bizName,y :cellValue,color : color};
							if(flag){
								normalData.push(cellValue);
								overData.push(null);
							}else{
								normalData.push(null);
								overData.push(cellValue);
							}
						}
					}
				}
				
				if (expect) {
					options = $.extend(true,{},options,{
						legend:{
				            enabled:true
				        }
					});
				}
				oc.simple.manager.report.columnchart({
					selector:$alertWindow.find("#"+chartDivId),
					width:805,
					height:220,
					title:metric,
					categories:categories,
					normalData:normalData,
					overData:overData,
					option:options
				});
			}
		}
	}
	
	
	function isEmpty(val){
		if(val && val!="undefined" && val!=null && val !="null" && val!=""){
			return false;
		}else{
			return true;
		}
	}
	
	
	/**
	 * 保存报表期望值
	 * */
	function saveExpect(callback,_this){
		if(expect && (!expect.id || expect.id == "")){
			oc.util.ajax({
				url:oc.resource.getUrl("simple/manager/workbench/saveReportExpect.htm"),
				data:expect,
				successMsg:"",
				success:function(data){
					//$('.symbol_hover').closest('li').click();
					refrashList();
					if(callback){
						callback(data.data);
					}
				}
			});
		}else{
			if(callback){
				callback(expect);
			}
		}
	}
	
	/**
	 * 通知联系人
	 * */
	$alertWindow.find("#noticeContact").click(function(){
		saveExpect(function(data){
			$alertWindow.data('errorBizs',errorBizs);
			$alertWindow.data('expectId',data.id);
			$alertWindow.load('module/simple/manager/workbench/report/noticeContacts.html');
		});
	});
	/**
	 * 处理问题
	 * */
	$alertWindow.find("#handlingProblem").click(function(){
		saveExpect(function(data){
			$alertWindow.load('module/simple/manager/workbench/report/disposeProblem.html');
		});
	});
	
	/**
	 * 保存期望值
	 * */
	$alertWindow.find("#saveExpect").click(function(){
		saveExpect(function(data){
			emptyDataAndClose();
		});
	});
	/**
	 * 刷新分析列表
	 * */
	function refrashList(_this){
		var $ico = $alertWindow.data('currentObj').siblings('.symbol');
		if($ico.hasClass('symbol_hover')){
			$ico.parent().click().click();
		}else{
			$ico.parent().click();
		}
	}
	
	$alertWindow.on('click','.close_btn',function(){
		emptyDataAndClose();
	})
	
	function emptyDataAndClose(){
		$alertWindow.removeData(["reportFileId","expect","reportId","errorBizs","reportName"]);
		$alertWindow.empty();
	}
	
	//启动问题报表分析引擎
	$alertWindow.find("#problemAnalysis").click(function(){
		$alertWindow.load('module/simple/manager/workbench/report/problemAnalysisEngine.html');
	});
})