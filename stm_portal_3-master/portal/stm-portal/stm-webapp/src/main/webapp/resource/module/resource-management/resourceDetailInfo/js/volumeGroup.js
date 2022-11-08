$(function(){
	
	oc.ns('oc.resourced.resource.detail.volumegroup');
	//记录当前主资源实例ID
	var mainInstanceId = null;
	var mainInstanceState = null;
	var dataGrid = null;

	function initVolumeGroupGrid(){
		var mainDiv = $("#volume_group_Div").attr('id',oc.util.generateId());
		//隐藏图表
		mainDiv.find('#volumeGroupChart').css('display','none');
		var datagridHeight = mainDiv.height() - mainDiv.find('#volume_group_metric_data_grid_title').height();
		mainDiv.find('#volume_group_metric_data_gridDIV').height(datagridHeight);
		
		var scanDataGrid = null;
		
		//初始化扫描按钮
		if(oc.index._activeRight == 4){
			
			$('#delete_volume_group_button').hide();
			mainDiv.find('#scan_volume_group_button').hide();
			
		}else{
			
			mainDiv.find('#scan_volume_group_button').linkbutton('RenderLB',{
				text:'扫描',
				onClick:function(){
					//扫描
					var scanContent = $('<div id="scanDataGridId"/>');
					var scanDialog = $('<div/>').dialog({
						title: '扫描结果',
						width: 400,
						height: 420,
						content:scanContent,
						modal: true,
						buttons:[{
							text:'加入监控',
							handler:function(){
								
								if(scanDataGrid.getSelections() == null || scanDataGrid.getSelections().length <= 0){
									alert('至少选择一个卷组!');
									return;
								}
								
								//发送加入监控请求
								oc.util.ajax({
									url: oc.resource.getUrl('portal/resourceManager/volumeGroup/addVolumeGroupToMonitor.htm'),
									timeout:null,
									data:{volumeList:JSON.stringify(scanDataGrid.getSelections()),mainInstanceId:mainInstanceId},
									success:function(data){
//										  addMonitorDialog.panel('destroy');
										scanDialog.panel('destroy');
										if(data.data){
											var result = data.data;
											
											var repeatCount = 0;
											if(result.repeatInstanceList && result.repeatInstanceList.length > 0){
												repeatCount = result.repeatInstanceList.length;											  	
											}
											
											var failCount = 0;
											if(result.failInstanceList && result.failInstanceList.length > 0){
												failCount = result.failInstanceList.length;
											}
											
											oc.ui.confirm('加入完成!<br>失败卷组:' + failCount + '个<br>重复卷组:' + repeatCount + '个');
											dataGrid.reLoad();
											curClickChartId = null;
//											  chartObj1.clear();
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
								scanDialog.panel('destroy');
							}
						}]
					});
					oc.ui.progress();
					//渲染弹出框的表格
					scanDataGrid = oc.ui.datagrid({
						selector:$('#scanDataGridId'),
						url:oc.resource.getUrl('portal/resourceManager/volumeGroup/scanVolumeGroupData.htm?mainInstanceId=' + mainInstanceId),
						pagination:false,
						columns:[[
						          {field:'checkedId',checkbox:true},
						          {field:'instanceId',hidden:true,isDynamicHidden:false},
						          {field:'volumeGroupName',title:'卷组名称',align:'left',width:80,ellipsis:true}
						          ]],
						          onLoadSuccess:function(){
						        	  $.messager.progress('close');
						        	  
						        	  //添加排序方法
//							var rows = $.extend(true,[],scanDataGrid.selector.datagrid('getRows'));
//							scanDataGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().unbind('click');
//							scanDataGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().on('click',function(){
//
//								 var target = $(this);
//					    		 
//					    		 var fieldId = target.parent().attr('field');
//					    		 
//					    		 if(fieldId == 'volumeGroupName' || fieldId == 'volumeGroupState' || fieldId == 'instanceId'){
//					    			 addSort(rows,fieldId,scanDataGrid,target);
//					    		 }
//					    		 
//					    	 });
						        	  
						          }
					});
					
				}
			});
			
			//删除按钮
			$('#delete_volume_group_button').linkbutton('RenderLB',{
				  text:'删除',
				  onClick:function(){
						if(dataGrid.selector.datagrid('getChecked') == null || dataGrid.selector.datagrid('getChecked').length <= 0){
							alert('至少选择一个卷组进行删除!');
							return;
						}
						
						var checkedInstanceIds = '';
						
						for(var i = 0 ; i < dataGrid.selector.datagrid('getChecked').length ; i++){
							checkedInstanceIds += dataGrid.selector.datagrid('getChecked')[i].resourceId + ",";
						}
						
						oc.ui.confirm("确认删除所有勾选的卷组吗？",function() {
							//发送加入删除请求
							oc.util.ajax({
								  
								  url: oc.resource.getUrl('portal/resourceManager/volumeGroup/deleteVolumeGroupInstance.htm'),
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
				selector:mainDiv.find('#volume_group_metric_data_grid'),
				url:oc.resource.getUrl('portal/resourceManager/volumeGroup/getVolumeGroupData.htm?mainInstanceId=' + mainInstanceId),
				pagination:false,
				fitColumns:false,
				columns:[[
			         {field:'instanceId',hidden:true,isDynamicHidden:false},
				     {field:'volumeGroupName',title:'卷组名称',width:315},
				     {field:'volumeGroupState',title:'卷组状态',width:155,
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
			         {field:'ppSize',title:'物理分区',width:155,ellipsis:true},
			         {field:'totalPpSize',title:'总物理分区',width:155},
			         {field:'lvsNumber',title:'逻辑卷数',width:155},
			         {field:'pvsNumber',title:'物理卷数',width:155}
			     ]],
			     dynamicColumnsSelect:mainDiv.find('#filter_info_list_button'),
			     onLoadSuccess:function(){
			    	 //注册图标按钮事件
//			    	 $('.showMetricDataChart').on('click',function(e){
//			    		 if(curClickChartId != null){
//			    			 $('#' + curClickChartId).attr('class','ico icon-chart margin-top-seven showMetricDataChart');
//			    		 }
//			    		 curClickChartId = $(e.target).attr('id');
//			    		 $(e.target).attr('class','ico icon-chartred margin-top-seven showMetricDataChart');
//			    		 chartObj1.setIds($(e.target).attr('metric'),$(e.target).attr('resourceId'));
//			    		 
//			    	 });
			    	 
			    	 //添加排序方法
					var rows = $.extend(true,[],dataGrid.selector.datagrid('getRows'));
					dataGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().unbind('click');
					dataGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().on('click',function(){

						 var target = $(this);
			    		 
			    		 var fieldId = target.parent().attr('field');
			    		 
			    		 if(fieldId == 'volumeGroupName' || fieldId == 'volumeGroupState' || fieldId == 'instanceId'){
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
				
				if(fieldName == 'processCommand' || fieldName == 'processAvail'){
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
	
	oc.resourced.resource.detail.volumegroup = {
			
		//入口方法
		open:function(instanceId,state){
			
			if(state){
				mainInstanceState = state;
			}
			mainInstanceId = instanceId;
			initVolumeGroupGrid();
		}
			
	};
	
});