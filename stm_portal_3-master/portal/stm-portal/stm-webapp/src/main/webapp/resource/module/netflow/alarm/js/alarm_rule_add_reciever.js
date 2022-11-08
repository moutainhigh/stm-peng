(function($){
	
	function NetflowAlarmRuleAddReciever(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	NetflowAlarmRuleAddReciever.prototype = {
			
		open : function() {
			var dlg = this._mainDiv = $('<div/>'), 
				that = this, 
				type = that.cfg.type;
			
			that._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/netflow/alarm/alarm_rule_add_reciever.html'),
				title: ' ',
				width: 250,
				height : 450,
				top: '20%',
				resizable : false,
				cache : false,
				onLoad : function() {
					that._init(dlg);
				},
				onClose:function(){
				},
				buttons:[{
					text: '确定',
					hanlder: function() {
						//TODO
						var ids = '', names = '';
						var selections = that._mainDiv.find('.oc-netflow-alarm-rule-reciever-main').datagrid('getSelections');
						if(!selections || selections.length == 0) {
							alert('请选择接收人员');
							return;
						}
						$.each(selections, function(i ,item) {
							ids += item.id + ',';
							names += '<'+item.name+'>';
						});
						if(ids != '') {
							ids = ids.substring(0, ids.length - 1);
						}
						var getIds = that.cfg.getIds;
						if(getIds) {
							getIds(ids, names);
						}
						dlg.dialog('close');
					}
				}, {
					text: '取消',
					handler: function() {
						//cancel dialog
						dlg.dialog('close');
					}
				}]
			});
		},
		_defaults : {
			type : 'add',
			id : undefined
		},
		_mainDiv : undefined,
		_id : '#oc_netflow_alarm_rule_reciever_main',
		_init : function(dlg) {
			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			
			that._initMethods.initBaiscInfo(that);
			
		},
		_initMethods: {
			'initBaiscInfo': function(that) {
				oc.ui.datagrid({
					selector: that._mainDiv.find('#netflow_alarm_reciever_datagrid'),
					url: '',
					columns:[[{
						field: 'id', checkbox: true, width: 20
					}, {
						field: 'name', title: '名称', width: 20
					}]]
				});
			}
		}	
	};
	
	oc.ns('oc.module.netflow.alarm.rule.add.reciever');

	oc.module.netflow.alarm.rule.add.reciever = {
		open : function(cfg) {
			new NetflowAlarmRuleAddReciever(cfg).open();
		}
	};
	
})(jQuery);