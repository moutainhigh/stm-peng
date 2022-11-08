(function($) {
	var cloudyKnowledge = $("#oc-module-localKnowledge").attr("id",oc.util.generateId());
	var octoolbar={
		left : [ {
			text : '模型部署',
			iconCls : "fa fa-plus",
			onClick : function() {
				_open('add',0);
			}
		}]
	};
//	oc.util.ajax({
//		url:oc.resource.getUrl("knowledge/capacity/getDownloadAddr.htm"),
//		successMsg:"",
//		success:function(data){
//			if(data.code==200){
//				cloudyKnowledge.find(".oc-subspanbg a:first").attr("href",data.data);
//			}
//		}
//	});
	var caplist = [];
	var datagrid = oc.ui.datagrid({
		selector : cloudyKnowledge.find(".capacityKnowledge_datagrid"),
		url : oc.resource.getUrl('knowledge/capacity/queryAllCapacityKnowledge.htm'),
		width : 'auto',
		height : 'auto',
		sortName:'name',
		sortOrder:'DESC',
		fit:true,
		octoolbar : octoolbar,
		columns : [[{
			field : 'id',
			title : '知识编号',
			width:50
		}, {
			field : 'model-name',
			title : '模型名称',
			sortable : true,
			align:'left',
			width : 80,
			formatter:function(value,row){
				return row.name.substring(0, row.name.lastIndexOf("."));
			}
		},{
			field : 'name',
			title : '模型文件',
			sortable : true,
			align:'left',
			width : 80
		},{
			field : 'deployTime',
			title : '部署时间',
			align:'center',
			width : 80
		},{
			field : 'cz',
			title : '操作',
			align:'left',
			width : 50,
			formatter:function(value, row, index){
				if(index==0){
					caplist = [];
				}
				var flog = false;
				for(var i=0;i<caplist.length;i++){
					if(caplist[i]==row.name){
						flog = true;
					}
				}
				caplist.push(row.name)
				if(flog){
					return "";
				}else{
					return "<span class='icon-knowledge-capacity-deployment' title='点我查看部署结果'  index='"
					+ index
					+ "'><img style='width:18px; height:18px;' src='"
					+ oc.resource
							.getUrl('resource/third/jquery-easyui-1.4/themes/icons/search.png')
					+ "'/></span>";
				}
			}
		}]],
		onLoadSuccess : function() {
			cloudyKnowledge.find("span.icon-knowledge-capacity-deployment").bind(
					'click',
					function(e) {
						datagrid.selector.datagrid('unselectAll');
						datagrid.selector.datagrid('selectRow', $(this).attr("index"));
						var currentRow = datagrid.selector.datagrid("getSelected");
						_open('view',currentRow.id);
						e.stopPropagation();
					});

		}
	});
	
	 function _open(type, id) {
			oc.resource.loadScript('resource/module/knowledge/capacity/js/capacityDetail.js', function() {
				oc.module.knowledge.capacity.open({
					type:type,
					id:id,
					callback : function() {
						caplist=[];
						datagrid.reLoad();
					}
				});
			});
		}
})(jQuery);