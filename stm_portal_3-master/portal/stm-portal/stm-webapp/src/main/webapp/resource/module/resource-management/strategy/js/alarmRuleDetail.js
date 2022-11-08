(function($) {
	function alarmRuleDetail(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
		this.strategyId = cfg.strategyId;
		this.open();
	}
	alarmRuleDetail.prototype={
			constructor : alarmRuleDetail,
			cfg:undefined,
			_dialog:undefined,
			_dialogDiv:undefined,
			_datagrid:undefined,
			strategyId:undefined,
			_defaults:{
				id : undefined
			},
			open:function(){
				var dlg = this._dialogDiv = $('<div/>'), that = this, type = that.cfg.type;
				this._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/resource-management/strategy/alarmRuleDetail.html'),
					title : '添加用户',
					height : 470,
					width:450,
					resizable : true,
					pagination : false,
					cache : false,
					onLoad : function() {
						var mainDiv = $('#oc_module_resource_management_alarmruledetail').attr('id',
								oc.util.generateId()); 
						var datagridDiv = mainDiv.find('.oc_module_resource_management_alarmruledetail_datagrid').first(); 
						that._datagrid = oc.ui.datagrid({
							selector : datagridDiv,
							url : oc.resource.getUrl('portal/syslog/getAllUser.htm'),
							pagination:false,
							columns : [[{
								field : 'id',
								title : '-',
								checkbox : true,
								width : 20
							},{
								field : 'name',
								title : '用户名',
								sortable : true,
								width : 40
							},{
								field:'mobile',
								title:'手机',
								width : 40
							},{
								field:'email',
								title:'邮件',
								width : 40
							}]]
						});
					},
					onClose:function(){
						dlg.dialog('destroy');
						if(that.cfg.callback){
							that.cfg.callback();//关闭弹出框后刷新域列表
						}
					},
					buttons : [{
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							var userIds = that._datagrid.getSelectIds();
							if(userIds == undefined || userIds.length == 0) {
								alert("请至少选择一位用户！",'danger');
							}else{
								oc.util.ajax({
									url : oc.resource.getUrl('portal/syslog/saveAlarmRuleUser.htm'),
									data : {ids:userIds.join(),strategyId:that.strategyId},
									async:false,
									success:function(data) {
										if(data && data.data) {
											dlg.dialog('close');
										}
									},
									successMsg:"告警规则添加成功"
								});
							}
						}
					},{
						text:"取消",
						iconCls:"fa fa-times-circle",
						handler:function(){
							dlg.dialog('close');
						}
					}]
				});
			}
	};
	
	oc.ns('oc.strategy.alarmruledetail');
	oc.strategy.alarmruledetail = {
		open : function(cfg) {
			new alarmRuleDetail(cfg);
		}
	};
})(jQuery);