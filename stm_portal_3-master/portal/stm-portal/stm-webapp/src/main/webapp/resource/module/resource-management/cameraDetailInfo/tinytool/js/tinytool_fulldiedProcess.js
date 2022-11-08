$(function(){
	var mainInstanceId = null;
	
	function tinytoolDiedProcessTable(instanceId){
		this.mainInstanceId = instanceId;
		//this.cfg = $.entend({}, this,_defaults, cfg);
	}
	
	tinytoolDiedProcessTable.prototype = {
			constructor:tinytoolDiedProcessTable,
			cfg:undefined,
			dialogDiv:undefined,
			_defaults:{},
			open:function(){
				var that = this;
				var contentDiv = $('<div id="diedProcessGrid"/>')
				this.dialogDiv = $("<div/>");
				this.dialogDiv.dialog({
					title:'僵死进程',
					width: 635,
				    height: 420,
				    content:contentDiv,
				    modal:true,
				    /*buttons:[{
				    	text:'关闭',
				    	handler:function(){
				    		//取消
				    		this.dialogDiv.panel('destroy');
				    	}
				    }]*/
				});
				oc.ui.progress();
				var grid = oc.ui.datagrid({
					selector:$('#diedProcessGrid'),
					url:oc.resource.getUrl('portal/resourceManager/processMetricData/diedProcessData.htm?mainInstanceId=' + this.mainInstanceId),
					pagination:false,
					columns:[[
					     {field:'pid',title:'进程PID',align:'left',width:'80px'},
				         {field:'processCommand',title:'进程名称',align:'left',width:'230px',ellipsis:true},
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
	
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolDiedProcessTable = {
			open:function(instanceId){
				var selfObj = new tinytoolDiedProcessTable(instanceId);
				//mainInstanceId = instanceId;
				selfObj.open();
			}
	}
});