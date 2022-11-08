(function($) {
	/**
	 * 获取云端知识最新更新时间，和知识总条数
	 * */
	oc.util.ajax({
		url:oc.resource.getUrl("knowledge/cloudy/getCloudyUpdateTimeAndTotal.htm"),
		successMsg:"",
		success:function(data){
			if(data.data){
				$("#cloudyUpdateTime").text(data.data.updateTime);
				$("#cloudyTotal").text(data.data.count);
			}
		}
	});
//	$("#importCloudyKnowledge").linkbutton('RenderLB', {
//		iconCls : 'fa fa-sign-in',
//		onClick : function(){
//			oc.resource.loadScript('resource/module/knowledge/cloudy/js/importCloudyKnowledge.js',function(){
//				oc.module.cloudyknowledge.open(function(){
//					datagrid.load();
//				});
//			});
//		}
//	});
	var cloudyKnowledge = $("#oc-module-cloudyKnowledge").attr("id",oc.util.generateId()),datagrid=null;
	var octoolbar={
		right : [ {
			text : '云端库导入',
			onClick : function() {
				oc.resource.loadScript('resource/module/knowledge/cloudy/js/importCloudyKnowledge.js',function(){
					oc.module.cloudyknowledge.open(function(){
						datagrid.load();
					});
				});
			}
		} ]
	};
	datagrid=oc.ui.datagrid({
		selector : cloudyKnowledge.find(".cloudyKnowledge_datagrid"),
		url : oc.resource.getUrl('knowledge/cloudy/queryCloucyKnowledge.htm'),
		octoolbar : octoolbar,
		columns : [[
 		{
			field : 'typeCode',
			title : '知识分类',
			width : 120
		},{
			field : 'typeName',
			title : '分类名称',
			width : 120
		}, {
			field : 'count',
			title : '总数（个）',
			align:'center',
			width : 120
		}]]
	});
})(jQuery);