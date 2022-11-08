(function($) {
	var loginUser = oc.index.getUser();
	var mainId = oc.util.generateId(),
	// 模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
	mainDiv = $('#oc_module_system_resourcegroup_main').attr('id', mainId).panel({
		fit : true,
		isOcAutoWidth : true
	}),
	// 域列表文档对象
	datagridDiv = mainDiv.find('.oc_module_system_resourcegroup_main_datagrid'),
	// 域列表实例
	datagrid = null;
	var octoolbar = null;
	if(loginUser.systemUser || loginUser.domainUser){
		octoolbar={
			left : [ {
				text : '添加',
				iconCls : 'fa fa-plus',
				onClick : function() {
					_open('add');
				}
			},{
				text : '删除',
				iconCls : "fa fa-trash-o",
				onClick : function() {
					delResourceGroup();
				}
			} ]
		};
	}
	var dlg = $("<div class = 'oc-dialog-float'/>");
	// 初始化域列表
	datagrid = oc.ui
			.datagrid({
				selector : datagridDiv,
				url : oc.resource.getUrl('system/resourceGroup/queryAllResourceGroup.htm'),
				idField:"id",
//				sortName:'CREATED_TIME',
//				sortOrder:'DESC',
				octoolbar :octoolbar,
				columns : [ [
						{
							field : 'id',
							title : '-',
							checkbox : true,
							width : 20,
							isDisabled:function(value,row,index){
								if(!loginUser.systemUser && loginUser.id!=row.entryId){
									return true;
								}
					        	return false;
					         }
						},
						{
							field : 'name',
							title : '资源集合名称',
							width : 60,
							sortable : false,
							formatter : function(value, row, index) {
								return "<span title='点我编辑详情' class='icon-group-detail oc-pointer-operate' style='text-decoration:underline;' index='"
										+ index + "'>" + value + "</span>";
							}
						},{
							field : 'domainName',
							title : '域',
							width : 60
						},{
							field:'entryId',
							hidden:true
						},
						{
							field : 'createUserName',
							title : '创建人',
							align : 'center'
						}] ],
				onLoadSuccess : function() {
					var execution = function(index) {
						datagridDiv.datagrid('unselectAll');
						datagridDiv.datagrid('selectRow', index);
						var currentRow = datagridDiv.datagrid("getSelected");
						_open('edit', currentRow.name);
					};
					mainDiv.find("span.icon-group-detail").bind('click',
						function() {
							execution($(this).attr("index"));
						}
					);
				}
			});

	function delResourceGroup(){
		var selRows = datagrid.getSelections();
		var ids=[],len;
		if(!loginUser.systemUser){
			for(var i=0;i<selRows.length;i++){
				var row = selRows[i];
				if(loginUser.id==row.entryId){
					ids.push(row.id);
				}
			}
		}else{
			ids=datagrid.getSelectIds();
		}
		len=ids.length;
		if(len){
			oc.ui.confirm("确认删除所选的资源集合吗？",function(){
				oc.util.ajax({
					url:oc.resource.getUrl('system/resourceGroup/deleteResourceGroup.htm'),
					data:{groupIds:ids.join()},
					successMsg:"",
					success:function(data){
						if(data && data.code==200){
							if(data.data && data.data!=""){
								alert("资源集合删除完成，部分资源集正在使用中无法删除！");
							}else{
								alert("资源集合删除成功");
							}
							datagrid.reLoad();
						}else{
							alert("资源集合删除失败");
						}
					}
				});
			});
		}else{
			alert("请选择至少一个资源集合!",'danger');
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
				'resource/module/system/resourcegroup/js/resourcegroupDetail.js', function() {
					oc.module.system.resourcegroup.open({
						type : type,
						name : name,
						id : id,
						callback:function(){
							datagrid.reLoad();
						}
					});
				});
	}
	
})(jQuery);