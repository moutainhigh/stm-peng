(function($){
	
	function NetflowAlarmRuleAdd(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	NetflowAlarmRuleAdd.prototype = {
			
		open : function() {
			var dlg = this._mainDiv = $('<div/>'), 
				that = this, 
				type = that.cfg.type;
			
			that._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/netflow/alarm/alarm_rule_add.html'),
				title: ' ',
				width: 750,
				height : 'auto',
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
						//save alarm setting  TODO
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
		_id : '#oc_netflow_alarm_rule_main',
		_init : function(dlg) {
			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			
			that._initMethods.initBaiscInfo(that);
			
		},
		_initMethods: {
			'initBaiscInfo': function(that) {

				/*************************接收人员初始化*****************************************/
				that._mainDiv.find('.what-alarm-type').on('click', function() {
					$(this).addClass('active');
					$(this).siblings('a').removeClass('active');
					var type = $(this).attr('alarmtype');
//					that._mainDiv.find('.alarm-type-value').val(type);
					if(type == 'message') {
						disCls('.netflow-alarm-message-reciever');//接收人员的隐藏显示
						disCls('.netflow-alarm-message-rule-node');//规则的隐藏显示
						disCls('.netflow-message-alarm-time-node');//时间隐藏显示
					} else if(type == 'email') {
						disCls('.netflow-alarm-email-reciever');
						disCls('.netflow-alarm-email-rule-node');
						disCls('.netflow-email-alarm-time-node');
					} else if(type == 'alert') {
						disCls('.netflow-alarm-alert-reciever');
						disCls('.netflow-alarm-alert-rule-node');
						disCls('.netflow-alert-alarm-time-node');
					}
				});
				
				var disCls = function(cls) {
					that._mainDiv.find(cls).removeClass('hide').siblings('div').addClass('hide');
				}
				
				/*添加接收人员按钮事件绑定*/
				that._mainDiv.find('.alarm-rule-add-message-reciever').linkbutton({
					onClick: function(e) {
						oc.resource.loadScript('resource/module/netflow/alarm/js/alarm_rule_add_reciever.js',function(){
							oc.module.netflow.alarm.rule.add.reciever.open({
								getIds: function(ids, names) {
									that._mainDiv.find('.netflow-alarm-message-reciever-ta').val(names);
									that._mainDiv.find('.netflow-alarm-message-reciever-ids').val(ids);
								}
							});
						});
					}
				});
				that._mainDiv.find('.alarm-rule-add-email-reciever').linkbutton({
					onClick: function() {
						oc.resource.loadScript('resource/module/netflow/alarm/js/alarm_rule_add_reciever.js',function(){
							oc.module.netflow.alarm.rule.add.reciever.open({
								getIds: function(ids, names) {
									that._mainDiv.find('.netflow-alarm-email-reciever-ta').val(names);
									that._mainDiv.find('.netflow-alarm-email-reciever-ids').val(ids);
								}
							});
						});
					}
				});
				that._mainDiv.find('.alarm-rule-add-alert-reciever').linkbutton({
					onClick: function() {
						oc.resource.loadScript('resource/module/netflow/alarm/js/alarm_rule_add_reciever.js',function(){
							oc.module.netflow.alarm.rule.add.reciever.open({
								getIds: function(ids, names) {
									that._mainDiv.find('.netflow-alarm-alert-reciever-ta').val(names);
									that._mainDiv.find('.netflow-alarm-alert-reciever-ids').val(ids);
								}
							});
						});
					}
				});
				
				
				/*删除接收人员按钮初始化*/
				that._mainDiv.find('.alarm-rule-delete-message-reciever').linkbutton({
					onClick: function() {
						that._mainDiv.find('.netflow-alarm-message-reciever-ta').val('');
					}
				});
				that._mainDiv.find('.alarm-rule-delete-email-reciever').linkbutton({
					onClick: function() {
						that._mainDiv.find('.netflow-alarm-email-reciever-ta').val('');
					}
				});
				that._mainDiv.find('.alarm-rule-delete-alert-reciever').linkbutton({
					onClick: function() {
						that._mainDiv.find('.netflow-alarm-alert-reciever-ta').val('');
					}
				});
				
				//默认触发短信操作
				that._mainDiv.find('.send-alert-type-sms').trigger('click');
				disCls('.netflow-alarm-message-rule-node');
				disCls('.netflow-message-alarm-time-node');
				
				
				/*************************发送告警时间初始化*****************************************/
				var hourData = [{id: '00', name: '00'}, {id: '01', name: '01'}, {id: '02', name: '02'}, {id: '03', name: '03'}
				, {id: '04', name: '04'}, {id: '05', name: '05'}, {id: '06', name: '06'}, {id: '07', name: '07'}
				, {id: '08', name: '08'}, {id: '09', name: '09'}, {id: '10', name: '10'}, {id: '11', name: '11'}
				, {id: '12', name: '12'}, {id: '13', name: '13'}, {id: '14', name: '14'}, {id: '15', name: '15'}
				, {id: '16', name: '16'}, {id: '17', name: '17'}, {id: '18', name: '18'}, {id: '19', name: '19'}
				, {id: '20', name: '20'}, {id: '21', name: '21'}, {id: '22', name: '22'}, {id: '23', name: '23'}];
				
				var minuteData = [{id: '00', name: '00'}, {id: '05', name: '05'}, {id: '10', name: '10'}, {id: '15', name: '15'}, 
				                  {id: '20', name: '20'}, {id: '25', name: '25'}, {id: '30', name: '30'}, {id: '35', name: '35'},
				                  {id: '40', name: '40'}, {id: '45', name: '45'}, {id: '50', name: '50'}, {id: '55', name: '55'}];
				
				var weekData = [{id: '1', name: '周一'}, {id: '2', name: '周一'}, {id: '2', name: '周二'}, {id: '3', name: '周三'}
								, {id: '4', name: '周四'}, {id: '5', name: '周五'}, {id: '6', name: '周六'}, {id: '7', name: '周日'}]
				
				/*******************************短信时间初始化***************************************/
				var initTimeCombobox = function(table, weekDis, addBtn, cls1, cls2, startHourCls, startMiniteCls, endHourCls, endMinuteCls, weekCls) {
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: that._mainDiv.find(cls1),
						data: [{id: '1', name: '每天'}, {id: '2', name: '每周'}],
						onSelect: function(d) {
							if(d.id == '1') {
								weekDis.addClass('hide');
								addBtn.addClass('hide');
								
								//删除table除第一行的所有行
								table.find(cls2).remove();
							} else {
								weekDis.removeClass('hide');
								addBtn.removeClass('hide');
							}
						}
					});
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: that._mainDiv.find(startHourCls),
						data: hourData
					})
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: that._mainDiv.find(startMiniteCls),
						data: minuteData
					});
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: that._mainDiv.find(endHourCls),
						data: hourData
					}); 
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: that._mainDiv.find(endMinuteCls),
						data: minuteData
					})
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: that._mainDiv.find(weekCls),
						data: weekData
					});
				}
				
				var getDiv = function(type) {
					var a = '';
					var b = '';
					if(type) {
						if(type == 'email') {
							a = '-email';
							b = 'Email';
						} else if(type == 'alert') {
							a = '-alert';
							b = 'Alert';
						} else if(type == 'sms') {
							b = 'Sms';
							a = '';
						}
					}
					var div = '<div class="netflow-alarm-time'+a+'-item">' +
						'<span style="width: 485px;">'+
						'<span class="netflow-alarm-aux-time'+a+'-week-display">'+
							'<input type="text" name="netflowAlarm'+b+'TimeWeek" class="netflow-alarm-aux-time'+a+'-week-choosen">'+	
						'</span>' +
						'<input type="text" name="" class="netflow-alarm-aux-time'+a+'-starthour">'+						
						'<input type="text" name="" class="netflow-alarm-aux-time'+a+'-startminute">'+
						'<span> 至 </span>' +						
						'<input type="text" name="" class="netflow-alarm-aux-time'+a+'-endhour">' +						
						'<input type="text" name="" class="netflow-alarm-aux-time'+a+'-endminute">' +
						'<span class="netflow-rule-aux'+a+'-buttons locate-right">' +
							'<span class="alarm-rule-add-aux'+a+'-time r-h-ico r-h-add"></span>' +
							'<span class="alarm-rule-delete-aux'+a+'-time r-h-ico r-h-delete"></span>' +				
						'</span>' +
					'</div>';
					return div;
				}
				
				var addAlarmTime = function(td, table, weekChoosenCls, startHourCls, startMinuteCls, endHourMinute, endHourCls, endMinuteCls, delCls, addCls, type) {
					var clsSuffix = '';
					if(type = 'email') {
						clsSuffix = '-email';
					} else if(type == 'alert') {
						clsSuffix = '-alert';
					}
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: (td ? td.find(weekChoosenCls) : weekChoosenCls),
						data: weekData
					});
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: (td ? td.find(startHourCls) : startHourCls),
						data: hourData
					});
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: (td ? td.find(startMinuteCls) : startMinuteCls),
						data: minuteData
					});
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: (td ? td.find(endHourCls) : endHourCls),
						data: hourData
					});
					oc.ui.combobox({
						placeholder: null,
						width: 70,
						selector: (td ? td.find(endMinuteCls) : endMinuteCls),
						data: minuteData
					});
					
					td.find(delCls).linkbutton({
						onClick: function() {
							$(this).parents('tr:first').remove();
							//找到最后一个TR，显示按钮
							table.find('tr:last').find(addCls).removeClass('hide');
						}
					});
					
					//显示最后一个TR的添加按钮
					that._mainDiv.find(addCls).addClass('hide');
					td.find(addCls).removeClass('hide');
					
					td.find(addCls).linkbutton({
						onClick: function() {
							var trInner = $('<tr class="netflow-alarm-time'+clsSuffix+'-weekly-append" />');
							var tdInner = $('<td/>');
							tdInner.append(div);
							trInner.append(tdInner);
							table.append(trInner);
							addAlarmTime(tdInner, table, weekChoosenCls, startHourCls, startMinuteCls, endHourMinute, endHourCls, endMinuteCls, delCls, addCls, type);
						}
					});
					
				}
				
				//sms combobox init
				var table = that._mainDiv.find('.netflow-alarm-time-tb');
				var weekDis = that._mainDiv.find('.netflow-alarm-aux-time-week-display');
				var addBtn = that._mainDiv.find('.alarm-rule-add-aux-time');
				initTimeCombobox(table, weekDis, addBtn, '.netflow-alarm-aux-time-day-week', '.netflow-alarm-time-weekly-append', 
						'.netflow-alarm-aux-time-starthour', '.netflow-alarm-aux-time-startminute', 
						'.netflow-alarm-aux-time-endhour', '.netflow-alarm-aux-time-endminute',
						'.netflow-alarm-aux-time-week-choosen');
				if(that._mainDiv.find('.netflow-alarm-aux-time-day-week').combobox('getValue') == 1) {
					weekDis.addClass('hide');
					addBtn.addClass('hide');
				} else {
					weekDis.removeClass('hide');
					addBtn.removeClass('hide');
				}
				var div = getDiv('sms');
				//添加时间
				that._mainDiv.find('.alarm-rule-add-aux-time').linkbutton({
					onClick: function() {
						var tr2 = $('<tr class="netflow-alarm-time-weekly-append"/>');
						var td2 = $('<td/>');
						td2.append(div);
						tr2.append(td2);
						table.append(tr2);
						
						addAlarmTime(td2, table, '.netflow-alarm-aux-time-week-choosen', 
								'.netflow-alarm-aux-time-starthour', '.netflow-alarm-aux-time-startminute', 
								'.netflow-alarm-aux-time-endhour', '.netflow-alarm-aux-time-endminute',
								'.alarm-rule-delete-aux-time', '.alarm-rule-add-aux-time', 'sms')
					}
				});

				//7*24 or custom init
				var tb = that._mainDiv.find('.netflow-alarm-time-tb');
				that._mainDiv.find('.netflow-alarm-time-7-or-custom').on('click', function() {
					if($(this).val() == 1) {
						tb.addClass('hide');
					} else {
						tb.removeClass('hide');
					}
				});
				//默认选择7*24小时，所以隐藏table
				that._mainDiv.find('.netflow-alarm-time-7days').attr('checked', true).trigger('click');
				
				addAlarmTime(null, table, '.netflow-alarm-aux-time-week-choosen', 
						'.netflow-alarm-aux-time-starthour', '.netflow-alarm-aux-time-startminute', 
						'.netflow-alarm-aux-time-endhour', '.netflow-alarm-aux-time-endminute',
						'.alarm-rule-delete-aux-time', '.alarm-rule-add-aux-time', 'sms')

				
				//email combobox init
				var tableEmail = that._mainDiv.find('.netflow-alarm-time-email-tb');
				var emailWeekDis = that._mainDiv.find('.netflow-alarm-aux-time-email-week-display');
				var emailAddBtn = that._mainDiv.find('.alarm-rule-add-aux-email-time');
				initTimeCombobox(tableEmail, emailWeekDis, emailAddBtn, '.netflow-alarm-aux-time-email-day-week', '.netflow-alarm-time-email-weekly-append', 
						'.netflow-alarm-aux-time-email-starthour', '.netflow-alarm-aux-time-email-startminute', 
						'.netflow-alarm-aux-time-email-endhour', '.netflow-alarm-aux-time-email-endminute',
						'.netflow-alarm-aux-time-email-week-choosen');
				if(that._mainDiv.find('.netflow-alarm-aux-time-email-day-week').combobox('getValue') == 1) {
					emailWeekDis.addClass('hide');
					emailAddBtn.addClass('hide');
				} else {
					emailWeekDis.removeClass('hide');
					emailAddBtn.removeClass('hide');
				}
				var divEmail = getDiv('email');
				//添加时间
				that._mainDiv.find('.alarm-rule-add-aux-email-time').linkbutton({
					onClick: function() {
						var trEmail = $('<tr class="netflow-alarm-time-email-weekly-append"/>');
						var tdEmail = $('<td/>');
						tdEmail.append(divEmail);
						trEmail.append(tdEmail);
						tableEmail.append(trEmail);
						
						addAlarmTime(tdEmail, tableEmail);
					}
				});

				//7*24 or custom init
				var tbEmail = that._mainDiv.find('.netflow-alarm-time-email-tb');
				that._mainDiv.find('.netflow-alarm-time-email-7-or-custom').on('click', function() {
					if($(this).val() == 1) {
						tbEmail.addClass('hide');
					} else {
						tbEmail.removeClass('hide');
					}
				});
				//默认选择7*24小时，所以隐藏table
				that._mainDiv.find('.netflow-alarm-time-email-7days').attr('checked', true).trigger('click');
				
				
				//alert combobox init
				var tableAlert = that._mainDiv.find('.netflow-alarm-time-alert-tb');
				var alertWeekDis = that._mainDiv.find('.netflow-alarm-aux-time-alert-week-display');
				var alertAddBtn = that._mainDiv.find('.alarm-rule-add-aux-alert-time');
				initTimeCombobox(tableAlert, alertWeekDis, alertAddBtn, '.netflow-alarm-aux-time-alert-day-week', '.netflow-alarm-time-alert-weekly-append', 
						'.netflow-alarm-aux-time-alert-starthour', '.netflow-alarm-aux-time-alert-startminute', 
						'.netflow-alarm-aux-time-alert-endhour', '.netflow-alarm-aux-time-alert-endminute',
						'.netflow-alarm-aux-time-alert-week-choosen');

				
			}
		},
		_checkMethods: {
			'checkAlarmRule': function(that) {
				var msg = ''; 
				//1.only email supported right now.

				//2.reciever validate
				var ta = that._mainDiv.find('.netflow-alarm-rule-reciever');
				for(var i=0,len=ta.length; i<len; i++) {
					if(!$(ta[i]) || $(ta[i]).val() == '') {
						msg = '请填写接收人员';
						break;
					}
				}
				return msg;

				//3.All checkboxed have default value, validation not needed.
			}
		}
			
	};
	
	oc.ns('oc.module.netflow.alarm.rule');

	oc.module.netflow.alarm.rule = {
		open : function(cfg) {
			new NetflowAlarmRuleAdd(cfg).open();
		}
	};
	
})(jQuery);