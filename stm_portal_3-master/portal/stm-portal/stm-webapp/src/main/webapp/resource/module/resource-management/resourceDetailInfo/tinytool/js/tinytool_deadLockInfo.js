$(function(){
	var mainInstanceId = null;
	
	function tinytoolDeadLockInfoTable(instanceId){
		this.mainInstanceId = instanceId;
	}
	
	tinytoolDeadLockInfoTable.prototype = {
		constructor:tinytoolDeadLockInfoTable,
		cfg:undefined,
		dialogDiv:undefined,
		_defaults:{},
		open:function(){
			var that = this;
			var contentDiv = $('<div id="deadLockInfoGrid"/>');
			this.dialogDiv = $('<div/>');
			this.dialogDiv.dialog({
				title:"死锁信息",
				width:695,
				height:420,
				content:contentDiv,
				modal:true,
			});
			oc.ui.progress();

			var deadLockGrid = oc.ui.datagrid({
				selector:$('#deadLockInfoGrid'),
				url:oc.resource.getUrl('portal/resourceManager/processMetricData/deadLockInfoData.htm?mainInstanceId=' + this.mainInstanceId),
				pagination:false,
				columns:[[{
					field:'sid',
					title:'进程ID',
					width:'80px'
					},{
					field:'dataBaseUserName',
					title:'数据库用户',
					width:'80px',
					ellipsis:true
					},{
					field:'dataBaseLockWait',
					title:'死锁地址',
					width:'120px',
					formatter:function(value,rowData,rowIndex){
						return value.replace('<','&lt;').replace('>','&gt;');
					}
					},{
					field:'dataBaseStatus',
					title:'状态',
					width:'80px'
					},{
					field:'dataBaseMachine',
					title:'死锁语句所在的机器',
					width:'150px',
					ellipsis:true
					},{
					field:'dataBaseProgram',
					title:'产生死锁的应用程序',
					width:'150px',
					ellipsis:true
					},
				]],
				onLoadSuccess:function(){
					$.messager.progress('close');
					//增加排序方法 
					var rows = $.extend(true,[],deadLockGrid.selector.datagrid('getRows'));
					deadLockGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().unbind('click');
					deadLockGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().on('click',function(){
						var target = $(this);
						var fieldId = target.parent().attr('field');
						addSort(rows,fieldId,deadLockGrid,target);
					});
				}

			});
		}

	}

	//
	function addSort(rows,fieldId,grid,target){

		var targetRows = null;

		if(target.attr('class').indexOf('datagrid-sort-asc') < 0 && target.attr('class').indexOf('datagrid-sort-desc') < 0){
			deadLockGrid.selector.datagrid('getPanel').find('.datagrid-sort-icon').parent().removeClass('datagrid-sort-asc').removeClass('datagrid-sort-desc');
			target.addClass('datagrid-sort-asc');
			targetRows = sortRowsByMetricId(rows,fieldId,'asc');
		}else if(target.attr('class').indexOf('datagrid-sort-asc') > 0 || target.attr('class').indexOf('datagrid-sort-asc') == 0){
			target.attr('class',target.attr('class').replace('datagrid-sort-asc','datagrid-sort-desc'));
			targetRows = sortRowsByMetricId(rows,fieldId,'desc');
		}else if(target.attr('class').indexOf('datagrid-sort-desc') > 0 || target.attr('class').indexOf('datagrid-sort-desc') == 0){
			target.attr('class',target.attr('class').replace('datagrid-sort-desc','datagrid-sort-asc'));
			targetRows = sortRowsByMetricId(rows,fieldId,'asc');
		}

		var loadData = {"data":{"startRow":0,"rowCount":rows.length,"condition":null,"total":rows.length,"totalRecord":rows.length,
			"rows":targetRows},"code":200};
		deadLockGrid.selector.datagrid('loadData',loadData);
	}

	function sortRowsByMetricId(rows,fieldName,sortType){
		
	   for (var i = 0; i < rows.length; i++) {
			for (var j = i; j < rows.length; j++) {
				var value_1 = null;
				var value_2 = null;
				
				if(fieldName == 'dataBaseUserName' || fieldName == 'dataBaseLockWait' || fieldName == 'dataBaseMachine' || fieldName == 'dataBaseProgram'){
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

	//
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytool');
	oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolDeadLockInfoTable = {
		open:function(instanceId){
			var selfObj = new tinytoolDeadLockInfoTable(instanceId);
			selfObj.open();
		}
	}

});