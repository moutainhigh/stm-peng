(function($) {
	var mainDiv = $('#oc_module_system_itsmuser').attr('id',
			oc.util.generateId()).panel({
		fit : true,
		isOcAutoWidth : true
	}), 
	datagridDiv = mainDiv.find('.oc_module_system_itsmuser_datagrid').first(); 
	var datagrid = null;
	var _dialog = $('<div class="oc-dialog-float"/>');
	var toolbar = [ {
		text : '添加',
		iconCls : 'fa fa-plus',
		onClick : function() {
			_open("add");
		}
	},{
		text : '删除',
		iconCls : 'fa fa-trash-o',
		onClick : function() {
			_delete();
		}
	},{
		text : '启用',
		iconCls : 'fa fa-key',
		onClick : function() {
			_startOrStop(1);
		}
	},{
		text : '停用',
		iconCls : 'fa fa-ban',
		onClick : function() {
			_startOrStop(0);
		}
	} ];
	var userId = "";
	if(!oc.index.getUser().systemUser){
		userId = oc.index.getUser().id;
	}
	datagrid = oc.ui.datagrid({
		selector : datagridDiv,
//		url : oc.resource.getUrl('system/itsmUser/queryItsmSystem.htm'),
		url : oc.resource.getUrl('system/SSOForThird/querySSOForThird.htm'),
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
					field:'isOpen',
					title:'启用状态',
					width:30,
					formatter:function(value,row,index){
						return "<span class='oc-top0 locate-left status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"'></span>";
					}
				},
//				{
//					field:'state',
//					title:'同步状态',
//					width:30,
//					formatter:function(value,row,index){
//						if(value=="SUCCESS"){
//							return "<span title='成功' class='light-ico_resource res_normal_nothing'></span>";
//						}else if(value="FAIL"){
//							return "<span title='失败' class='light-ico_resource res_critical_nothing'></span>";
//						}else {
//							return "<span title='未知' class='light-ico_resource res_unknown_nothing'></span>";
//						}
//					}
//				},
				{
					field : 'name',
					title : '系统名称',
					sortable : true,
					width : 40,
					formatter : function(value, row, index) {
						return "<span title='点我编辑详情' class='oc-pointer-operate'>" + value + "</span>";
					}
				},
				{
					field : 'wsdlURL',
					title : 'URL地址',
					sortable : true,
					width : 120
				},
				{
					field : 'describle',
					title : '描述',
					width : 60
//				},
//				{
//					field : 'manualChange',
//					title : '手动同步',
//					width : 20,
//					formatter : function(value, row, index) {
//						return "<span title='手动同步' class='icon_synchron'></span>";
//					}
				}] ],
		onClickCell : function(rowIndex, field, value, e) {
			var row = datagrid.selector.datagrid("getRows")[rowIndex];
			var status = (value+1)%2;
			if(field=='name'){
				datagrid.selector.datagrid('selectRow',rowIndex);
				_open('edit');
			}else if(field=='isOpen'){
				oc.util.ajax({
     	    		url:oc.resource.getUrl('system/SSOForThird/updateSSOForThirdStartState.htm'),
     	    		data:{ids:row.id,isOpen:status},
//     	    		failureMsg:'操作失败！',
     	    		success:function(data){
     	    			if(data.code==200){
     	    				datagrid.selector.datagrid('reload');
                            // console.log(row.id);
     	    				if(status==0){
     	    					oc.index.navigator.moduleChange(row.id.toString(),false);
     	    				}else{
     	    					oc.index.navigator.moduleChange(row.id.toString(),true);
     	    				}
     	    			}
     	    		},
     	    		successMsg:"操作成功"
     	    	});
			}else if(field=="manualChange"){
				if(row.isOpen == 0) {
					alert("未开启同步");
					return;
				}else {
					oc.util.ajax({
	     	    		url:oc.resource.getUrl('thirdSystem/itsmUser/syncAllUser.htm'),
	     	    		data:{systemId:row.id},
	     	    		success:function(data){
	     	    			if(data.code==200){
	     	    				datagrid.selector.datagrid('reload');
	     	    				alert(data.data);
	     	    			}
	     	    		}
//	     	    		successMsg:"操作成功"
	     	    	});
				}
			}
		}
	});
	function _startOrStop(isOpen) {
//		debugger;
		var ids = datagrid.getSelectIds();
		
		if(ids==undefined || ids.length == 0) {
			alert("请至少选择一条记录！", 'danger');
		} else {
			oc.util.ajax({
				url : oc.resource.getUrl('system/SSOForThird/updateSSOForThirdStartState.htm'),
				data : {ids:ids.join(),isOpen:isOpen},
				async:false,
				success : function(data) {
					datagrid.reLoad();
					if(isOpen==0){
						$.each(ids,function(index,id){
							oc.index.navigator.moduleChange(id.toString(),false);
						})
					}else{
						$.each(ids,function(index,id){
							oc.index.navigator.moduleChange(id.toString(),true);
						})
					}
					
				},
				successMsg:isOpen == 0 ? "停用成功":"启用成功"
			});
		}
	
	}
	function _delete() {
		var delIds = datagrid.getSelectIds();
		
		if(delIds==undefined || delIds.length == 0) {
			alert("请至少选择一条记录！", 'danger');
		} else {
			oc.ui.confirm('确认删除所选择的数据？', function(){
				oc.util.ajax({
					url : oc.resource.getUrl('system/SSOForThird/removeSystem.htm'),
					data : {delIds : delIds.join()},
					async:false,
					success:function(data) {
						if(data && data.data) {
							datagrid.reLoad();
							$.each(delIds,function(index,id){
								oc.index.navigator.moduleChange(id.toString(),false);
							})
							
						}
					},
					successMsg:"删除成功"
				});
			})
		}
	}
	function _open(type) {
		var id = undefined;
		if (type == 'edit') {
			id = datagrid.getSelectId();
			if (!id) {
				alert('请选择一条数据！', 'danger');
				return;
			}
		}
		oc.resource.loadScript(
			'resource/module/system/ssoforthird/js/ssoDetail.js', function() {
				oc.module.sso.detail.open({
					type : type,
					id : id,
					callback : function() {
						datagrid.reLoad();
					}
				});
			});
	}
})(jQuery);