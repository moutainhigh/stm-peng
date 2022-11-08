$(function(){
	
	oc.ns('oc.resourced.resource.detail.file');
	//记录当前主资源实例ID
	var mainInstanceId = null;
	
	var mainInstanceState = null;
	
	var scanFileButton = null;
	
	var filterInfoListButton = null;
	
	var fileMetricDataGridId = null;
	
	var dataGrid = null;

	function initFileGrid(){
		
		scanFileButton = 'scan_file_button';
		filterInfoListButton = 'filter_info_list_button';
		deleteFileButton = 'delete_file_button';
		fileMetricDataGridId = 'file_metric_data_grid';
		
		var scanDataGrid = null;
		
		if(oc.index._activeRight == 4){
			$('#fileIpt').hide();
			$('#scan_file_path').hide();
			$('#' + scanFileButton).hide();
			$('#' + deleteFileButton).hide();
			
		}else{
			
			//初始化扫描按钮
			$('#' + scanFileButton).linkbutton('RenderLB',{
				text:'扫描',
				onClick:function(){
					//扫描
					
					var scanFilePath = $('#scan_file_path').val().trim();
					if(!scanFilePath){
						alert('请输入需要扫描的文件路径！');
						return;
					}
					
					//filePath不能以/结尾,否则后台逻辑出错 ,李骋要求不做处理
//				    if(scanFilePath.charAt(scanFilePath.length-1)=='\/'){
//				    	if(scanFilePath.length==1){
//				    		scanFilePath="";
//				    	}else{
//				    		scanFilePath = scanFilePath.substring(0,scanFilePath.length-1);
//				    	}
//					}
					
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
									alert('至少选择一个文件!');
									return;
								}
								
								//发送加入监控请求
								oc.util.ajax({
									
									url: oc.resource.getUrl('portal/resourceManager/fileMetricData/addFileToMonitor.htm'),
									timeout:null,
									data:{fileList:JSON.stringify(scanDataGrid.getSelections()),mainInstanceId:mainInstanceId,filePath:scanFilePath},
									success:function(data){
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
											
											oc.ui.confirm('加入完成!<br>失败文件:' + failCount + '个<br>重复文件:' + repeatCount + '个');
											dataGrid.reLoad();
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
						url:oc.resource.getUrl('portal/resourceManager/fileMetricData/scanFileData.htm' ),//?mainInstanceId=' + mainInstanceId+ '&queryPath=' + scanFilePath
						queryParams: {
							mainInstanceId: mainInstanceId,
							queryPath: scanFilePath
						},
						pagination:false,
						columns:[[
						          {field:'checkedId',checkbox:true},
						          {field:'fileName',title:'文件名称',align:'left',ellipsis:true,width:'225px',formatter:function(value,row,rowIndex){
						        	  return '<div  style="width:100%;text-overflow: ellipsis;overflow:hidden;display:inline-block;"  title="'+value+'" >'+value+'</div>';
						          }},
						          {field:'fileSize',title:'文件大小',align:'left',width:'216px',ellipsis:true},
						          {field:'fileModifyTime',title:'文件修改时间',align:'left',width:'140px'}
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
			
			//删除按钮
			$('#' + deleteFileButton).linkbutton('RenderLB',{
				  text:'删除',
				  onClick:function(){
						if(dataGrid.selector.datagrid('getChecked') == null || dataGrid.selector.datagrid('getChecked').length <= 0){
							alert('至少选择一个文件进行删除!');
							return;
						}
						
						var checkedInstanceIds = '';
						
						for(var i = 0 ; i < dataGrid.selector.datagrid('getChecked').length ; i++){
							checkedInstanceIds += dataGrid.selector.datagrid('getChecked')[i].resourceId + ",";
						}
						
						oc.ui.confirm("确认删除所有勾选的文件吗？",function() {
							//发送加入删除请求
							oc.util.ajax({
								  
								  url: oc.resource.getUrl('portal/resourceManager/fileMetricData/deleteFileInstance.htm'),
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
				selector:$('#' + fileMetricDataGridId),
				url:oc.resource.getUrl('portal/resourceManager/fileMetricData/getFileData.htm?mainInstanceId=' + mainInstanceId),
				pagination:false,
				fitColumns:false,
				columns:[[
			         {field:'resourceId',checkbox:true,isDynamicHidden:false},
				     {field:'fileName',title:'文件名',ellipsis:true,width:220,formatter:function(value,row,rowIndex){
				    	 return '<div  style="width:100%;text-overflow: ellipsis;overflow:hidden;display:inline-block;"  title="'+value+'" >'+value+'</div>';
				     }},
				     {field:'fileAvail',title:'可用性',width:100,
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
			         {field:'filePath',title:'文件路径',width:410,ellipsis:true},
			         {field:'fileSize',title:'文件大小',width:170},
			         {field:'fileMdCollect',title:'MD5值',width:200},
			         {field:'fileModifyTime',title:'文件修改时间',width:200}
			     ]],
			     dynamicColumnsSelect:$('#' + filterInfoListButton),
			     onLoadSuccess:function(){
			    	 
			    	 //注册输入框失去焦点事件
			    	 $('.updateRemarkInput').blur(function(e){
			    		 
			    		var remarkContent = $(e.target).parent().find('a');
			    		 
			    		var newRemark = $(e.target).val();
			    		var oldRemark = remarkContent.text();
			    		if(newRemark != oldRemark && newRemark.trim() != ""){
			    			//发送加入监控请求
			    			oc.util.ajax({
			    				
			    				url: oc.resource.getUrl('portal/resourceManager/processMetricData/updateProcessInstanceRemark.htm'),
			    				data:{mainInstanceId:$(e.target).attr('resourceId'),newRemark:newRemark},
			    				success:function(data){
			    					
			    					if(data.data){
			    						alert('更新成功!');
			    						$(e.target).css('display','none');
			    						remarkContent.text(newRemark);
			    						remarkContent.css('display','block');
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
				
				if(fieldName == 'filePath' || fieldName == 'fileAvail' || fieldName == 'fileName' || fieldName == 'fileModifyTime'){
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
	
	oc.resourced.resource.detail.file = {
			
		//入口方法
		open:function(instanceId,state){
			
			if(state){
				mainInstanceState = state;
			}
			mainInstanceId = instanceId;
			initFileGrid();
		}
			
	};
	
});