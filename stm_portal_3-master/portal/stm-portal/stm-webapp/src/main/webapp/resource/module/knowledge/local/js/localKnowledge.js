(function($) {
	var cloudyKnowledge = $("#oc-module-localKnowledge").attr("id",oc.util.generateId());
	/*var octoolbar={
		left : [ {
			text : '添加',
			iconCls : "fa fa-plus",
			onClick : function() {
				_open('add');
			}
		},{
			text : '编辑',
			iconCls : "icon-edit",
			onClick : function() {
				_open('edit');
			}
		},{
			text : '删除',
			iconCls : "fa fa-trash-o",
			onClick : function() {
				deleteLocalKnowledge();
			}
		},{
			text : '全部导出',
			iconCls : 'fa fa-sign-out',
			onClick : function() {
				window.open(oc.resource.getUrl('knowledgezip/exprotZip.htm'));
			}
		},{
			text : '云端知识离线导入',
			iconCls : "fa fa-sign-in",
			onClick : function() {
				oc.resource.loadScript('resource/module/knowledge/cloudy/js/importCloudyKnowledge.js',function(){
					oc.module.cloudyknowledge.open(function(){
						datagrid.load();
					});
				});
			}
		} ]
	};*/
	
	var toolbar = [];
	toolbar.push( {
		text : '添加',
		iconCls : "fa fa-plus",
		onClick : function() {
			_open('add');
		}
	},{
		text : '编辑',
		iconCls : "icon-edit",
		onClick : function() {
			_open('edit');
		}
	},{
		text : '删除',
		iconCls : "fa fa-trash-o",
		onClick : function() {
			deleteLocalKnowledge();
		}
	},{
		text : '全部导出',
		iconCls : 'fa fa-sign-out',
		onClick : function() {
			window.open(oc.resource.getUrl('knowledgezip/exprotZip.htm'));
		}
	},{
		text : '云端知识离线导入',
		iconCls : "fa fa-sign-in",
		onClick : function() {
			oc.resource.loadScript('resource/module/knowledge/cloudy/js/importCloudyKnowledge.js',function(){
				oc.module.cloudyknowledge.open(function(){
					datagrid.load();
				});
			});
		}
	});
	
	//添加搜索的表单
	var _dialog = $('<div class="oc-dialog-float"/>');
	var form = $('<form class="oc-form float"/>')
	.append($('<div class="form-group"/>')
			.append('<input type="text" class="oc-enter" name="search" placeholder=""/>')
			.append('<input type="text" name="config-hidden-text" style="display:none;"/>'))
	.append($('<div class="form-group"/>').append('<a class="queryBtn">查询</a>'))
	.append($('<div class="form-group"/>').append('<a class="resetBtn">重置</a>'));
	var queryForm = oc.ui.form({ selector : form });
	form.fixPlaceHolder();
	form.find('.oc-enter').keypress(function(e,i){
		if(e.keyCode==13)
			datagrid.reLoad({search:$("input[name='search']").val()});
		});
	form.find(".queryBtn").linkbutton('RenderLB',{
		iconCls:'icon-search',
		onClick : function(){
			datagrid.reLoad({search:$("input[name='search']").val()});
		}
	})
	form.find(".resetBtn").linkbutton('RenderLB',{
		iconCls:'icon-reset',
		onClick : function(){
			queryForm.reset();
		}
	});
	
//	oc.util.ajax({
//		url:oc.resource.getUrl("knowledge/local/getDownloadAddr.htm"),
//		successMsg:"",
//		success:function(data){
//			if(data.code==200){
//				cloudyKnowledge.find(".oc-subspanbg a:first").attr("href",data.data);
//			}
//		}
//	});
	var datagrid = oc.ui.datagrid({
		selector : cloudyKnowledge.find(".localKnowledge_datagrid"),
		url : oc.resource.getUrl('knowledge/local/queryLocalKnowledge.htm'),
		queryForm:queryForm,
		queryConditionPrefix:'',
		hideReset:true,
		hideSearch:true,
		width : 'auto',
		height : 'auto',
//		octoolbar : octoolbar,
		octoolbar:{
			left:toolbar,
			right:[queryForm.selector]
		},
		columns : [[{
			field:'ck',
			title:'-',
			checkbox : true,
			width:20
		},
 		{
			field : 'id',
			title : '知识编号',
			width : 50,
			formatter:function(value,row,index){
				var title = '点击编辑知识详情！';
				return "<span title='"+title+"' class='icon-knowledge-detail oc-pointer-operate' style='text-decoration:underline;'  kng-id='"+row.id+"'>" + value + "</span>";
			}
		}, {
			field : 'knowledgeTypeCode',
			title : '知识分类编码',
			align:'left',
			width : 160,
			ellipsis:true,
			formatter:function(value,row,index){
				var title = '点击编辑知识详情！';
				return "<span title='"+title+"' class='icon-knowledge-detail oc-pointer-operate' style='text-decoration:underline;'  kng-id='"+row.id+"'>" + value + "</span>";
			}
		},
		{
			field : 'knowledgeTypeName',
			title : '知识分类名称',
			align:'left',
			width : 160,
			ellipsis:true,
			formatter:function(value,row,index){
				var title = '点击编辑知识详情！';
				return "<span title='"+title+"' class='icon-knowledge-detail oc-pointer-operate' style='text-decoration:underline;'  kng-id='"+row.id+"'>" + value + "</span>";
			}
		},
		{
			field : 'sourceContent',
			title : '故障原因',
			align:'left',
			width : 360,
			ellipsis:true,
			formatter:function(value,row,index){
				var title = '点击编辑知识详情！';
				return "<span title='"+title+"' class='icon-knowledge-detail oc-pointer-operate' style='text-decoration:underline;'  kng-id='"+row.id+"'>" + value + "</span>";
			}
		}]],
		onLoadSuccess : function() {
			cloudyKnowledge.find("span.icon-knowledge-detail").bind('click',
					function(e) {
						openEditKng('edit', $(this).attr("kng-id"));
						e.stopPropagation();
					});
		}
	});
	
	function deleteLocalKnowledge(){
		var ids = datagrid.getSelectIds();
		if(ids==undefined || ids.length==0){
			alert("请至少选择一条知识！", 'danger');
		}else{
			oc.ui.confirm("确认删除所选的知识吗？",function(){
				oc.util.ajax({
					url : oc.resource.getUrl('knowledge/local/removeLocalKnowledge.htm'),
					data : {ids:ids.join()},
					async:false,
					success : function(data) {
						if(data && data.data){
							datagrid.reLoad();
						}
					},
					successMsg:"知识删除成功"
				});
			});
		}
	}
	
	 function _open(type, name) {
		 var id = undefined;
			if (type == 'edit' || type == 'view') {
				id = datagrid.getSelectId();
				if (!id) {
					alert('请选择一条知识！', 'danger');
					return;
				}
			}
			oc.resource.loadScript('resource/module/knowledge/local/js/knowledgeDetail.js', function() {
				oc.module.knowledge.local.open({
					type:type,
					id:id,
					callback : function() {
						datagrid.reLoad();
					}
				});
			});
		}
	 
	 function openEditKng(type,id){
		 oc.resource.loadScript('resource/module/knowledge/local/js/knowledgeDetail.js', function() {
				oc.module.knowledge.local.open({
					type:type,
					id:id,
					callback : function() {
						datagrid.reLoad();
					}
				});
			});
	 }
})(jQuery);