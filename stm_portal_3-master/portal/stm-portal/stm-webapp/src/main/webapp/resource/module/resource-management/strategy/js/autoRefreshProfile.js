(function($) {
	var mainDiv = $('#oc_module_resource_management_autoRefresh').attr('id',
			oc.util.generateId()).panel({
		fit : true,
		isOcAutoWidth : true
	}), 
	datagridDiv = mainDiv.find('.oc_module_resource_management_autoRefresh_datagrid').first(); 
	var datagrid = null;
	var _dialog = $('<div class="oc-dialog-float"/>');
	var toolbar = [ {
		text : '新建',
		iconCls : 'fa fa-plus',
		onClick : function() {
			_open("add");
		}
	}, {
		text : '删除',
		iconCls : 'fa fa-trash-o',
		onClick : function() {
			_delStrategy();
		}
	} ];
	var userId = "";
	if(!oc.index.getUser().systemUser){
		userId = oc.index.getUser().id;
	}
	datagrid = oc.ui.datagrid({
		selector : datagridDiv,
		pageSize:$.cookie('pageSize_autoRefreshProfile')==null ? 15 : $.cookie('pageSize_autoRefreshProfile'),
		url : oc.resource.getUrl('portal/autoRefresh/getProfileList.htm'),
		queryParams:{creatorId:userId},
		octoolbar : {
			right : toolbar
		},
		checkOnSelect:false,
		selectOnCheck:false,
		columns : [ [
				{
					field : 'id',
					checkbox : true,
					width : 20
				},
		        {field:'isUse',title:'是否启用',sortable:true,width:30,formatter: function(value,row,index){
		        		return "<span class='oc-top0 locate-left status oc-switch "+(value == 1 ? "open" : "close")+"' data-data='"+(value == 1)+"'></span>";
		        	}
				},
				{
					field : 'profileName',
					title : '策略名称',
					sortable : true,
					width : 40
				},
				{
					field : 'executRepeat',
					title : '刷新周期',
					sortable : true,
					width : 30,
					formatter: function(value,row,index){
		        		return value + "天";
		        	}
				},
				{
					field : 'isRemoveHistory',
					title : '是否保留已消失的资源',
					sortable : true,
					width : 30,
					formatter: function(value,row,index){
						if(value == 0){
							return '是';
						}else{
							return '否';
						}
		        	}
				},
				{
					field : 'createUserName',
					title : '创建者',
					width : 40
				}] ],
		onClickCell : function(rowIndex, field, value, e) {
			var row = datagrid.selector.datagrid("getRows")[rowIndex];
			if(field=='profileName'){
				datagrid.selector.datagrid('selectRow',rowIndex);
				_open('edit');
			}else if(field=='isUse'){
				row.isUse = row.isUse == 1 ? 0 : 1;
				oc.util.ajax({
					url : oc.resource.getUrl('portal/autoRefresh/updateProfileStatus.htm'),
					timeout : null,
					data : {
						profileId : row.id,
					},
					successMsg:null,
					success : function(d){
						datagrid.selector.datagrid("updateRow",{
							index : rowIndex,
							row : row
						});
						alert("操作成功！");
					}
				});
			}
		}
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_autoRefreshProfile');
				$.cookie('pageSize_autoRefreshProfile',pageSize);
			}
		});
	}
	
	function _delStrategy() {
		var checkedRows = datagrid.selector.datagrid('getChecked');
		var checkedIds = new Array();
		for(var i = 0 ; i < checkedRows.length ; i++){
			checkedIds.push(checkedRows[i].id);
		}
		
		if(checkedIds==undefined || checkedIds.length == 0) {
			alert("请至少选择一条策略！", 'danger');
		} else {
			oc.ui.confirm('确认删除所选择的数据？', function(){
				oc.util.ajax({
					url : oc.resource.getUrl('portal/autoRefresh/delelteProfile.htm'),
					data : {ids : checkedIds.join()},
					async:false,
					success:function(data) {
						if(data && data.data) {
							datagrid.reLoad();
						}
					},
					successMsg:"策略信息删除成功"
				});
			})
		}
	}
	function _open(type, name) {
		var id = undefined;
		if (type == 'edit') {
			id = datagrid.getSelectId();
			if (!id) {
				alert('请选择一条数据！', 'danger');
				return;
			}
		}
		oc.resource.loadScript(
			'resource/module/resource-management/strategy/js/autoRefreshProfileDetail.js', function() {
				oc.profile.auto.refresh.open({
					type : type,
					name : name,
					id : id,
					callback : function() {
						datagrid.reLoad();
					}
				});
			});
	}
})(jQuery);