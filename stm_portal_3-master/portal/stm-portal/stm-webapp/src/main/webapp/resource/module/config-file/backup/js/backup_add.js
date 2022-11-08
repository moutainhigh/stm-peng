(function($) {
	oc.ns('oc.module.config.plan');	
	function Facade(cfg) {
		var dialog = $("<div/>"),user = oc.index.getUser();
		var buttons = [];
		if(user.systemUser==true){
			buttons.push({
				text : '确定',
				iconCls:'fa fa-check-circle',
				handler : function() {
					if(form.validate()) {
						var beginDate = new Date($("#beginDate").datebox('getValue').replace(/-/g,"/")).getTime(),
						endDate = new Date($("#endDate").datebox('getValue').replace(/-/g,"/")).getTime(),
						currentDate = new Date(),currentD;
						currentD = new Date((currentDate.getFullYear()+"-"+(currentDate.getMonth()+1)
								+"-" +currentDate.getDate()).replace(/-/g,"/")).getTime();
//						if(currentD>beginDate){
//							alert("任务开始时间不能小于当前时间");
//							return ;
//						}
						if(beginDate>endDate){
							alert("任务开始时间不能大于结束时间");
							return ;
						} 
						var formdata = form.val();
						formdata.id = cfg.id;
						oc.util.ajax({
							url : oc.resource.getUrl('portal/config/plan/addPlan.htm'),
							data : formdata,
							success : function(data){
								if(data.code != 200){
									alert(data.data);
									return;
								}
								var deviceData = {};
								var right = [];
								var left = [];
								var leftRows = pickGrid.getLeftRows();
								var rightRows = pickGrid.getRightRows();
								for(var i in rightRows){
									right.push(rightRows[i].id);
								}
								for(var i in leftRows){
									//在该计划下被取消的
									if(cfg.id == leftRows[i].backupId){
										left.push(leftRows[i].id);
									}
								}
								if(cfg.id){
									deviceData.planId = cfg.id;
								}else{
									deviceData.planId = data.data.id;
								}
								deviceData.planDevices = right.join();
								deviceData.notPlanDevices = left.join();
								oc.util.ajax({
									url:oc.resource.getUrl('portal/config/plan/upateDevicePlan.htm'),
									data:deviceData,
									success:function(data){
										//刷新备份计划列表
										cfg.datagrid.reLoad();
										dialog.dialog('close');
									}								
								})
							}
						})
					}
				}
			});
		}
		buttons.push({
			iconCls:'fa fa-times-circle',
			text : '取消',
			handler : function() {
				dialog.dialog('close');
			}
		});
		oc.module.config.plan.planId = cfg.id;
		this.open = function(){		
			dialog.dialog({
				href:oc.resource.getUrl('resource/module/config-file/backup/backup_add.html'),
				title:(cfg.type=='add'?'新建':'编辑')+'备份任务',
				height:570,
				width:800,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					if(cfg.id){
						oc.util.ajax({
							url:oc.resource.getUrl('portal/config/plan/getPlan.htm'),
							data:{id:cfg.id},
							success : function(data){
								var plan = data.data;
								plan.beginDate = plan.beginDateStr;
								plan.endDate = plan.endDateStr;
								form.val(plan);
								typeSelect(plan.type);
								checkDefault(plan.id);
							}
						})
					}else{
						typeSelect(1);
					}
				}
			});
		}
	}
	oc.module.config.plan = {
		open : function(cfg) {
			var detail = new Facade(cfg);
			detail.open();
		}
	};
})(jQuery);