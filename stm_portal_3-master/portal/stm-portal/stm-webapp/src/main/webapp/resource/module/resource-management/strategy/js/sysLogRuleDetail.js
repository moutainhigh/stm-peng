(function($) {
	function SysLogRuleDetail(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
		this.sysLogRuleId = cfg.id;
		this._strategyId = cfg.strategyId;
		this.open();
	}
	SysLogRuleDetail.prototype={
			constructor : SysLogRuleDetail,
			cfg:undefined,
			_dialog:undefined,
			_strategyId:undefined,
			_dialogDiv:undefined,
			_form:undefined,
			sysLogRuleId:undefined,
			_defaults:{
				type : 'add',
				id : undefined
			},
			initForm:function(){//初始化表单
				this._form = oc.ui.form({
					selector:this._dialogDiv.find(".syslogrule-detail-form:first"),
					combobox:[{
						selector:'[name=alarmLevel]',
						data:[{id:'CRITICAL',name:'致命'},{id:'SERIOUS',name:'严重'},{id:'WARN',name:'警告'}],
//						,{id:'UNKOWN',name:'未知'}
						placeholder:null
					}]
				});
				var object = oc.util.getDictObj('syslog_level');
				for(var i in object) {
					this._form.find("#logLevel")
						.append("<span style='display: inline-block; width: 22%; min-width: 98px; padding-bottom: 5px;'><input type='checkbox' name='logLevel' value='"+object[i].code+"'>"+object[i].name + "</span>");
				}
				this._form.find("#strategyId").val(this._strategyId);
			},
			loadform:function(that){//加载表单数据
				oc.util.ajax({
					url:oc.resource.getUrl('portal/syslog/getSysLogRuleById.htm'),
					data:{
						id:that.sysLogRuleId
					},
					async:false,
					successMsg:null,
					success:function(data){
						var level = data.data.logLevel;
						data.data.logLevel=level&&level.split(",");
						that._form.val(data.data);
					}
				});
			},
			saveForm:function(that){
				if(that._form.find("input[name='logLevel']:checked").length == 0){
					alert("请选择日志级别","danger");
					return;
				};
				if(that._form.validate()){
					oc.util.ajax({
						url : oc.resource.getUrl((that.cfg.type == 'add') ? 'portal/syslog/saveSingleStrateRule.htm': 'portal/syslog/updateStrateRule.htm'),
						data : that._form.val(),
						async:false,
						success : function(data) {
							if(data.code == 200) {
								that._dialogDiv.dialog('close');
							}
						},
						successMsg:"规则"+(that.cfg.type == 'add'?"添加":"更新")+"成功"
					});
				}
			},
			open:function(){
				var dlg = this._dialogDiv = $('<div/>'), that = this, type = that.cfg.type;
				this._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/resource-management/strategy/sysLogRuleDetail.html'),
					title : ((type == 'edit') ?'编辑' : '新建') + '规则',
					height : 600,
					resizable : true,
					cache : false,
					onLoad : function() {
						that.initForm();
						if(type=="edit"){
							that.loadform(that);
						}
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
							var ruleName = that._form.find("input[name='name']").val();
							var keywords = that._form.find("textarea[name='keywords']").val();
							var description = that._form.find("textarea[name='description']").val();
							if(ruleName.length > 16) {
								alert("规则名称不能超过16个字!!!","danger");
								return;
							}
							if(keywords.length > 32) {
								alert("关键字不能超过32个字！","danger");
								return;
							}
							if(description.length > 100) {
								alert("备注不能超过100个字！","danger");
								return;
							}
							that.saveForm(that);
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
	
	oc.ns('oc.strategy.syslogruledetail');
	oc.strategy.syslogruledetail = {
		open : function(cfg) {
			new SysLogRuleDetail(cfg);
		}
	};
})(jQuery);