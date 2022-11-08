$(function () {

	var id=oc.util.generateId();
	
	var formDiv;
	var form;
	function initFrom(cfg,operatingDialog){
		formDiv=operatingDialog.find('#sysLogOperating');
		formDiv.find("input[name='useOperated']").val(cfg.row.strategyName);
		formDiv.find("input[name='id']").val(cfg.row.id);
		var userId = "";
		if(!oc.index.getUser().systemUser){
			userId = oc.index.getUser().id;
		}
		form=oc.ui.form({
			selector:formDiv,
			combobox:[{
				selector : '[name=strategyId]',
				url : oc.resource.getUrl('portal/syslog/querySetStrategy.htm?typeId=1&domainIds='+cfg.row.domainId+"&creatorId="+userId),
				placeholder:null,
				filter : function(data) {
					return data.data.rows;
				}
			}]
		});
		return form;
	}
	
	/**
	 * 打开syslog操作的dialog
	 */
	function open(cfg){
		var operatingDialogDiv=$('<div/>');
		
		var operatingDialog;
		
		operatingDialog=operatingDialogDiv.dialog({
		    title: '选择监控策略',
		    flt:false,
		    width: 400, 
		    height: 200,
		    buttons:[{
		    	text: '确定',
		    	iconCls:"fa fa-check-circle",
		    	handler:function(){
		    		oc.util.ajax({
						url : oc.resource.getUrl('portal/syslog/updateResourceStrategy.htm'),
						data : form.val(),
						async:false,
						success : function(data) {
							cfg.callback();
							operatingDialogDiv.dialog('close');
						},
						successMsg:"策略更改成功"
					});
		    	}
		    },{
		    	text: '取消',
		    	iconCls:"fa fa-times-circle",
		    	handler:function(){
		    		operatingDialogDiv.dialog('close');;
		    	}
		    }],
		    href: oc.resource.getUrl('resource/module/resource-management/dailyRecord/sysLogOperating.html'),
		    onLoad:function(){
		    	initFrom(cfg,operatingDialog);
		    }
		});
		return operatingDialog;
	}
	
	 //命名空间
	oc.ns('oc.syslogoperating');
	oc.syslogoperating.dialog={
			open:function(cfg){
				return open(cfg);
			}
	};
});
