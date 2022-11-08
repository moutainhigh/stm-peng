$(function(){
	
	oc.ns('oc.resourced.resource.detail.process');
	//记录当前主资源实例ID
	var mainInstanceId = null;
	
	var mainInstanceState = null;
	
	var scanProcessButton = null;
	
	var filterInfoListButton = null;
	
	var processMetricDataGridId = null;
	
	var dataGrid = null;
	
	var curClickChartId = null;
	
	var argsLengthFlag = false;

	function initProcessGrid(){
        var scanDataGrid = null;
		//加载charts
		var chartObj1 = new chartObj($('#process_metric_data_chart'),1.5);
		scanProcessButton = 'scan_process_button';
		deleteProcessButton = 'delete_process_button';
		filterInfoListButton = 'filter_info_list_button';
		processMetricDataGridId = 'process_metric_data_grid';
		
		
		//控制高度,找出各个div,减去title高度
		var showProcessDiv = $('#showProcess');
		var showMetricDatagridDiv = showProcessDiv.find('#showMetricDatagridDiv');
		var barDiv = showMetricDatagridDiv.find('.tablelefttopico').parent();
		var showMetricDatagridParent = showMetricDatagridDiv.find('#showMetricDatagridParent');
		var showMetricChartsDiv = showProcessDiv.find('#showMetricChartsDiv');
		var barDivHeight = barDiv.height();
		var showMetricDatagridDivHeight = showProcessDiv.height();
		showMetricDatagridDiv.height(showMetricDatagridDivHeight);
		showMetricDatagridParent.height(showMetricDatagridDivHeight - barDivHeight);
		if(argsLengthFlag){
			showMetricDatagridParent.height(showMetricDatagridDivHeight - 10);
		}
		showMetricChartsDiv.height(barDivHeight);
		showMetricChartsDiv.attr('style','display:none;');
		
		showProcessDiv.find("#useableQuerySpan").css('display','none');
		//绑定chartDiv收缩
		showMetricChartsDiv.find('.w-table-title').on('click',function(){
	    	showHideChartMethod(showProcessDiv,showMetricDatagridDiv,showMetricChartsDiv,barDiv.find('#infoDatagrid'));
			showMetricChartsDiv.attr('style','display:none;');
		});
		
		//初始化扫描按钮
		if(oc.index._activeRight == 4){
			$('#' + scanProcessButton).hide();
		}else{
			$('#' + scanProcessButton).linkbutton('RenderLB',{
				text:'扫描',
				onClick:function(){
					//扫描
					var scanContent = $('<div id="scanDataGridId"/>');
					var scanDialog = $('<div/>').dialog({
						title: '扫描结果',
						width: 635,
						height: 420,
						content:scanContent,
						modal: true,
						buttons:[{
							text:'加入监控',
							handler:function(){
								
								if(scanDataGrid.getSelections() == null || scanDataGrid.getSelections().length <= 0){
									alert('至少选择一个进程!');
									return;
								}
								
								var selectContent = $('<div radio></div>');
								
								selectContent.append('<br><label><input name="monitorType" type="radio" value="1" checked="checked" />&nbsp&nbspPID  +  进程名</label><br><br>');
								selectContent.append('<label><input name="monitorType" type="radio" value="0" />&nbsp&nbsp进程名</label>');
								
								//加入监控
								var addMonitorDialog = $('<div/>').dialog({
									title: '选择方式',
									width: 220,
									height: 170,
									content:selectContent,
									modal: true,
									buttons:[{
										text:'确定',
										handler:function(){
											console.info(selectContent.find('input[name="monitorType"]:checked').val());
											//发送加入监控请求
											oc.util.ajax({
												
												url: oc.resource.getUrl('portal/resourceManager/processMetricData/addProcessToMonitor.htm'),
												timeout:null,
												data:{processList:JSON.stringify(scanDataGrid.getSelections()),mainInstanceId:mainInstanceId,type:selectContent.find('input[name="monitorType"]:checked').val()},
												success:function(data){
													addMonitorDialog.panel('destroy');
													scanDialog.panel('destroy');
													if(data.data){
														var result = data.data;
														console.log(data);
//														  var repeatString = '';
//														  if(result.repeatInstanceList && result.repeatInstanceList.length > 0){
//															  for(var i = 0 ; i < result.repeatInstanceList.length ; i ++){  
//																  repeatString += result.repeatInstanceList[i] + ",";
//															  }
//															  repeatString = repeatString.substring(0,repeatString.length - 1);														  	
//														  }else{
//															  repeatString = '无';
//														  }
														
														var repeatCount = 0;
														if(result.repeatInstanceList && result.repeatInstanceList.length > 0){
															repeatCount = result.repeatInstanceList.length;											  	
														}
														
//														  var failString = '';
//														  if(result.failInstanceList && result.failInstanceList.length > 0){
//															  for(var i = 0 ; i < result.failInstanceList.length ; i ++){  
//																  failString += result.failInstanceList[i] + ",";
//															  }
//															  failString = failString.substring(0,failString.length - 1);	
//														  }else{
//															  failString = '无';
//														  }
														
														var failCount = 0;
														if(result.failInstanceList && result.failInstanceList.length > 0){
															failCount = result.failInstanceList.length;
														}
														
														oc.ui.confirm('加入完成!<br>失败进程:' + failCount + '个<br>重复进程:' + repeatCount + '个');
														dataGrid.reLoad();
														curClickChartId = null;
														chartObj1.clear();
													}else if(data.data == null){
														alert('加入失败!');
													}else{
														alert('加入失败!');
													}
													
													
												}
												
											});
											
										}
									},{
										text:'取消',
										handler:function(){
											//取消
											addMonitorDialog.panel('destroy');
										}
									}]
								});
								
							}
						},{
							text:'取消',
							handler:function(){
								//取消
								scanDialog.panel('destroy');
							}
						}]
					});
					oc.ui.progress();
					//渲染弹出框的表格
					scanDataGrid = oc.ui.datagrid({
						selector:$('#scanDataGridId'),
						url:oc.resource.getUrl('portal/resourceManager/processMetricData/scanProcessData.htm?mainInstanceId=' + mainInstanceId),
						pagination:false,
						columns:[[
						          {field:'checkedId',checkbox:true},
						          {field:'pid',title:'进程PID',align:'left',width:'80px'},
						          {field:'processCommand',title:'进程名称',align:'left',width:'130px',ellipsis:true},
						          {field:'processPath',title:'进程命令行',align:'left',width:'100px',ellipsis:true,
									 formatter:function(value,row,rowIndex){
										if(value == null || value ==' ' || value =='null'){
											return '--';
										}else{
											return '<div data-toggle="tooltip" style="overflow: hidden;text-overflow:ellipsis;white-space:nowrap;" title="'+value+'">'+ value +'</div>';;
										}
									 } 
								  },
								  {field:'processCPUAvgRate',title:'CPU利用率',align:'left',width:'90px',
						        	  formatter:function(value,row,rowIndex){
						        		  return value + '%';
						        	  }
						          },
						          {field:'physicalMemory',title:'物理内存',align:'left',width:'100px'},
						          {field:'visualMemory',title:'虚拟映像',align:'left',width:'80px'}
						          ]],
						          onLoadSuccess:function(){
						        	  $.messager.progress('close');
						        	  
						        	  //添加排序方法
						        	  var rows = $.extend(true,[],scanDataGrid.selector.datagrid('getRows'));
						        	  scanDataGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().unbind('click');
						        	  scanDataGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().on('click',function(){
						        		  
						        		  var target = $(this);
						        		  
						        		  var fieldId = target.parent().attr('field');
						        		  
						        		  addSort(rows,fieldId,scanDataGrid,target);
						        	  });
						        	  
						          }
					});
					
				}
			});
			
		}
		
		if(oc.index._activeRight == 4){
			$('#' + deleteProcessButton).hide();
		}else{
			
			//删除按钮
			$('#' + deleteProcessButton).linkbutton('RenderLB',{
				text:'删除',
				onClick:function(){
					if(dataGrid.selector.datagrid('getChecked') == null || dataGrid.selector.datagrid('getChecked').length <= 0){
						alert('至少选择一个进程进行删除!');
						return;
					}
					
					var checkedInstanceIds = '';
					
					for(var i = 0 ; i < dataGrid.selector.datagrid('getChecked').length ; i++){
						checkedInstanceIds += dataGrid.selector.datagrid('getChecked')[i].resourceId + ",";
					}
					
					oc.ui.confirm("确认删除所有勾选的进程吗？",function() {
						//发送加入删除请求
						oc.util.ajax({
							
							url: oc.resource.getUrl('portal/resourceManager/processMetricData/deleteProcessInstance.htm'),
							timeout:null,
							data:{instanceIds:checkedInstanceIds},
							success:function(data){
								if(data.data){
									alert('删除成功!');
									dataGrid.reLoad();
								}else{
									alert('删除失败!');
								}
							}
							
						});
					});
					
				}
			});
		}
		
		var dataGridCfg = {
				selector:$('#' + processMetricDataGridId),
				url:oc.resource.getUrl('portal/resourceManager/processMetricData/getProcessData.htm?mainInstanceId=' + mainInstanceId),
				pagination:false,
				fitColumns:false,
				columns:[[
			         {field:'resourceId',checkbox:true,isDynamicHidden:false},
				     {field:'pid',title:'进程PID',width:80},
				     {field:'processAvail',title:'可用性',width:100,
				    	 formatter:function(value,row,rowIndex){
//				    		 if(mainInstanceState && mainInstanceState == 'CRITICAL'){
//				    			 return '不可用';
//				    		 }
                             // 主资源不可用则显示为不可用 dfw 20161226
                             if(mainInstanceState == 'CRITICAL' || mainInstanceState == 'CRITICAL_NOTHING'){
                                 return '不可用';
                             }
				    		 if(value == 'yes'){
				    			 return '可用';
				    		 }else if(value == 'no'){
				    			 return '不可用';
				    		 }else{
				    			 return '未知';
				    		 }
				    	 }
				     },
			         {field:'processCommand',title:'进程名称',width:200,ellipsis:true},
					 {field:'processPath',title:'进程命令行',align:'left',width:'200px',ellipsis:true,
						 formatter:function(value,row,rowIndex){
			        		if(value == null || value ==' '){
			        			return '--';
			        		}else{
								return '<div data-toggle="tooltip" style="overflow: hidden;text-overflow:ellipsis;white-space:nowrap;" title="'+value+'">'+ value +'</div>';
							}
			        	 } 
					 },
			         {field:'processCPUAvgRate',title:'CPU利用率',width:120,
			        	 formatter:function(value,row,rowIndex){
			        		if(value == '--'){
			        			return value;
			        		}else{
			        			var id = 'chartCpu' + row.resourceId;
			        			return '<a id="' + id + '" resourceId="' + row.resourceId + '" metric="procMulCpuRate"  class="ico icon-chart margin-top-seven showMetricDataChart">' + value + '</a>';
			        		}
			        	 }
			         },
			         {field:'physicalMemory',title:'物理内存',width:120,
			        	 formatter:function(value,row,rowIndex){
				        		if(value == '--'){
				        			return value;
				        		}else{
				        			var id = 'chartPhy' + row.resourceId;
				        			return '<a id="' + id + '" resourceId="' + row.resourceId + '" metric="processPhysicalMem"  class="ico icon-chart margin-top-seven showMetricDataChart">' + value + '</a>';
				        		}
				        	 }
			         },
			         {field:'visualMemory',title:'虚拟映像',width:120,
			        	 formatter:function(value,row,rowIndex){
				        		if(value == '--'){
				        			return value;
				        		}else{
				        			var id = 'chartVis' + row.resourceId;
				        			return '<a id="' + id + '" resourceId="' + row.resourceId + '" metric="processVirtualMap"  class="ico icon-chart margin-top-seven showMetricDataChart">' + value + '</a>';
				        		}
				        	 }
			         },
			         {field:'processRemark',title:'备注',width:340,
			        	 formatter:function(value,row,rowIndex){
			        		 	var text = "点击添加备注";
			        		 	if(value != undefined && value != null && value != '' && value != 'null' && value != '--'){
			        		 		text = value;
			        		 	}
			        		 	return '<form onkeydown="if(event.keyCode==13)return false;"><div ><a class="updateRemark">' + text + '</a><input type="text" resourceId="' + row.resourceId + '" class="updateRemarkInput" style="width:320px;display:none" maxLength=20/></div></form>';
			        	 }
			         }
			     ]],
			     dynamicColumnsSelect:$('#' + filterInfoListButton),
			     onLoadSuccess:function(){
			    	 //注册图标按钮事件
			    	 $('.showMetricDataChart').on('click',function(e){
						 showMetricChartsDiv.attr('style','display:block;');
			    		 if(curClickChartId){
			    			 $('#' + curClickChartId).attr('class','ico icon-chart margin-top-seven showMetricDataChart');
			    		 }
			    		 curClickChartId = $(e.target).attr('id');
			    		 $(e.target).attr('class','ico icon-chartred margin-top-seven showMetricDataChart');
			    		 chartObj1.setIds($(e.target).attr('metric'),$(e.target).attr('resourceId'));
			    		 
			    		 showHideChartMethod(showProcessDiv,showMetricDatagridDiv,showMetricChartsDiv,barDiv.find('#infoDatagrid'),'show');
			    	 });
			    	 
			    	 //注册修改备注事件
			    	 $('.updateRemark').on('click',function(e){
			    		 e.stopPropagation();
			    		 var inputContent = $(e.target).parent().find('input');
			    		 if($(e.target).text() == '点击添加备注'){
			    			 inputContent.val('');
			    		 }else{
			    			 inputContent.val($(e.target).text());
			    		 }
			    		 $(e.target).css('display','none');
			    		 inputContent.css('display','block');
			    		 //设置焦点在输入框上
			    		 inputContent.focus();
			    	 });
			    	 
			    	 //注册输入框失去焦点事件
			    	 $('.updateRemarkInput').blur(function(e){
			    		 
			    		var remarkContent = $(e.target).parent().find('a');
			    		 
			    		var newRemark = $(e.target).val();
			    		var oldRemark = remarkContent.text();
			    		
			    		//输入为空格
			    		if(newRemark.length>0 && newRemark.trim()==""){
			    			alert("不能全输入空格!");
			    			$(e.target).css('display','none');
    						remarkContent.css('display','block');
			    		}else if(newRemark != oldRemark){//输入为空也可提交
			    			//发送加入监控请求
			    			oc.util.ajax({
			    				url: oc.resource.getUrl('portal/resourceManager/processMetricData/updateProcessInstanceRemark.htm'),
			    				data: {
			    					mainInstanceId : $(e.target).attr('resourceId'),
			    					newRemark : newRemark
			    				},
			    				success:function(data){
			    					if(data.data){
			    						alert('更新成功!');
			    						if(newRemark && newRemark.trim()!=""){
			    							$(e.target).css('display','none');
				    						remarkContent.text(newRemark);
				    						remarkContent.css('display','block');
			    						}else{
				    						$(e.target).css('display','none');
			    							remarkContent.text("点击添加备注");
			    							remarkContent.css('display','block');
			    						}
			    					}else{
			    						alert('更新失败!');
			    					}
			    				}
			    			});
			    		}else{
			    			$(e.target).css('display','none');
    						remarkContent.css('display','block');
			    		}
			    	 });
			    	 
			    	 //添加排序方法
					var rows = $.extend(true,[],dataGrid.selector.datagrid('getRows'));
					dataGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().unbind('click');
					dataGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().on('click',function(){

						 var target = $(this);
			    		 
			    		 var fieldId = target.parent().attr('field');
			    		 
			    		 if(fieldId == 'processRemark'){
			    			 return;
			    		 }
			    		 addSort(rows,fieldId,dataGrid,target);
			    	 });
			    	 
			     }
				
			};
		
		dataGrid = oc.ui.datagrid(dataGridCfg);
		
		
	}
	
	function sortRowsByMetricId(rows,fieldName,sortType){
		
	   for (var i = 0; i < rows.length; i++) {
			for (var j = i; j < rows.length; j++) {
				var value_1 = null;
				var value_2 = null;
				
				if(fieldName == 'processCommand' || fieldName == 'processAvail' || fieldName == 'processPath'){
					value_1 = rows[i][fieldName];
					value_2 = rows[j][fieldName];
				}else{
					
					if(rows[i][fieldName + "Value"]){
						value_1 = rows[i][fieldName + "Value"];
					}else{
						value_1 = rows[i][fieldName];
					}
					if(rows[j][fieldName + "Value"]){
						value_2 = rows[j][fieldName + "Value"];
					}else{
						value_2 = rows[j][fieldName];
					}
					value_1 = parseInt(value_1);
					value_2 = parseInt(value_2);
					if(isNaN(value_1)){
						value_1 = -1;
					}
					if(isNaN(value_2)){
						value_2 = -1;
					}
				}
				
				if(sortType == 'asc'){
					if (value_1 > value_2) {
						var temp = rows[i];
						rows[i] = rows[j];
						rows[j] = temp;
					}
				}else{
					if (value_1 < value_2) {
						var temp = rows[i];
						rows[i] = rows[j];
						rows[j] = temp;
					}
				}
			}
		}
	   
		return rows;
		
	}
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
					showMetricDatagridDivHeight = rootDivHeight ;
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
					showMetricDatagridDivHeight = rootDivHeight - tabTitleHeight+ 35;
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
				targetTemp.height(showMetricDatagridDivHeight-tabTitleHeight-headerHeight-10);
			});
	    }
	}
	
	
	function addSort(rows,fieldId,grid,target){
		
		 var targetRows = null;
		 if(target.attr('class').indexOf('datagrid-sort-asc') < 0 && target.attr('class').indexOf('datagrid-sort-desc') < 0){
			 grid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().removeClass('datagrid-sort-asc').removeClass('datagrid-sort-desc');
			 target.addClass('datagrid-sort-asc');
			 targetRows = sortRowsByMetricId(rows,fieldId,'asc');
		 }else if(target.attr('class').indexOf('datagrid-sort-asc')>0 || target.attr('class').indexOf('datagrid-sort-asc')==0){
			 target.attr('class',target.attr('class').replace('datagrid-sort-asc','datagrid-sort-desc'));
			 targetRows = sortRowsByMetricId(rows,fieldId,'desc');
		 }else if(target.attr('class').indexOf('datagrid-sort-desc')>0 || target.attr('class').indexOf('datagrid-sort-desc')==0){
			 target.attr('class',target.attr('class').replace('datagrid-sort-desc','datagrid-sort-asc'));
			 targetRows = sortRowsByMetricId(rows,fieldId,'asc');
		 }
		 
		 var localData = {"data":{"startRow":0,"rowCount":rows.length,"totalRecord":rows.length,"condition":null,
			"rows":targetRows,"total":rows.length},"code":200};
		 grid.selector.datagrid('loadData',localData);
		 
	}
	
	oc.resourced.resource.detail.process = {
			
		//入口方法
		open:function(instanceId,state,param){
			
			if(state){
				mainInstanceState = state;
			}
			mainInstanceId = instanceId;
			
			if(param){
				argsLengthFlag = param.argsLengthFlag;
				if(argsLengthFlag){
					$('#showProcess').height(595);
				}
			}

			initProcessGrid();
		}
			
	};
	
});