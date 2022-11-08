(function($) {
	
	
	function initFrom(alarmId,alarmDialog,data){
		var datagridDiv=alarmDialog.find('#alarm_knowledge_datagrid');
		datagrid = oc.ui.datagrid({
			selector:datagridDiv,
			url:oc.resource.getUrl('alarm/alarmManagement/queryAlarmKnowledgeList.htm?alarmId='+ data.data.alarmId),
			columns:[[
                 {field:'id',title:'',sortable:true,hidden:true},
                 {field:'schemeId',title:'',sortable:true,hidden:true},
		         {field:'alarmKnowledgeName',title:'知识分类名称',sortable:true,align:'left',ellipsis:true,width:140},
		         {field:'alarmKnowleContent',title:'知识内容',sortable:true,align:'left',ellipsis:true,width:560},
		         {field:'schemeName',title:'解决方案名称',sortable:true,align:'left',ellipsis:true,width:88}
		     ]],
		     onClickCell : function(rowIndex, field, value){
		    	 if(field == 'schemeName'){
		    		 var row = $(this).datagrid('getRows')[rowIndex];
						var schemeId = row.schemeId;
						console.info(schemeId);
						oc.resource.loadScript('resource/module/knowledge/local/js/resolveDetail.js', function() {
							oc.module.knowledge.resolve.open({
								type:'view',
								id:schemeId,
								callback:function(resolve){
								}
							});
						});
		    	 }
		     }
		});
	}
	
	/**
	 * 打开告警详细
	 */
	function open(alarmId){
		var alarmDialogDiv=$('<div/>');
		var alarmDialog;
		alarmDialog=alarmDialogDiv.dialog({
		    title: '知识库关联',
		    flt:false,
		    width: 800, 
		    height: 460,
		    href: oc.resource.getUrl('resource/module/alarm-management/alarm-knowledge-base.html'),
		    onLoad:function(){
		    	oc.util.ajax({
		    		url: oc.resource.getUrl('alarm/alarmManagement/getAlarmById.htm'),
		    		data:{alarmId:alarmId},
		    		successMsg:null,
		    		success:function(data){
		    			initFrom(alarmId,alarmDialog,data);
		    		}
		    	});
		    }
		});
		return alarmDialog;
	}
	
	
	
	
	 //命名空间
	oc.ns('oc.alarm.knowledge.base');
	oc.alarm.knowledge.base={
			open:function(alarmId){
				return open(alarmId);
			}
	};
})(jQuery);