(function($) {
	var mainDiv = $('#oc_module_resource_management_syslogtactics').attr('id',
			oc.util.generateId()).panel({
		fit : true,
		isOcAutoWidth : true
	}), 
	datagridDiv = mainDiv.find('.oc_module_resource_management_syslogtactics_datagrid').first(); 
	var datagrid = null;
	var _dialog = $('<div class="oc-dialog-float"/>');
	var toolbar = [ {
		text : '新建',
		iconCls : 'fa fa-plus',
		onClick : function() {
			_open("add");
		}
	}, {
		text : '复制',
		iconCls:'icon-copy',
		onClick : function() {
			_copyStrategy();
		}
	},{
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
		pageSize:$.cookie('pageSize_sysLogTactics')==null ? 15 : $.cookie('pageSize_sysLogTactics'),
		url : oc.resource.getUrl('portal/syslog/queryStrategy.htm'),
		queryParams:{typeId:1,creatorId:userId},//syslog类型1
		octoolbar : {
			right : toolbar
		},
		columns : [ [
				{
					field : 'id',
					title : '-',
					checkbox : true,
					width : 20
				},
				{
					field : 'name',
					title : '策略名称',
					sortable : true,
					width : 40,
					formatter : function(value, row, index) {
						return "<span title='点我编辑详情' class='oc-pointer-operate'>" + value + "</span>";
					}
				},
				{
					field : 'domainName',
					title : '域',
					sortable : true,
					width : 30
				},
				{
					field : 'creator',
					title : '创建者',
					width : 40
				}] ],
		onClickCell : function(rowIndex, field, value, e) {
			var row = datagrid.selector.datagrid("getRows")[rowIndex];
			if(field=='name'){
				datagrid.selector.datagrid('selectRow',rowIndex);
				_open('edit');
			}
		}
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_sysLogTactics');
				$.cookie('pageSize_sysLogTactics',pageSize);
			},
		});
	}
	
	function _copyStrategy() {
		var id = datagrid.getSelectIds();
		if(0 == id.length){
			alert('请选择一条数据！', 'danger');
			return;
		}
		if (id.length > 1) {
			alert('只能选择一条数据复制！', 'danger');
			return;
		}
		oc.resource.loadScript(
			'resource/module/resource-management/strategy/js/syslogCopy.js', function() {
				oc.syslog.strategy.copy.open({
					id : id[0],
					callback : function() {	
						datagrid.reLoad();
					}
				});
			});
	}
	function _delStrategy() {
		var delIds = datagrid.getSelectIds();
		
		if(delIds==undefined || delIds.length == 0) {
			alert("请至少选择一条策略！", 'danger');
		} else {
			oc.ui.confirm('确认删除所选择的数据？', function(){
				oc.util.ajax({
					url : oc.resource.getUrl('portal/syslog/removeStrategy.htm'),
					data : {delIds : delIds.join()},
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
			'resource/module/resource-management/strategy/js/sysLogTacticsDetail.js', function() {
				oc.syslogtacticsdetail.open({
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