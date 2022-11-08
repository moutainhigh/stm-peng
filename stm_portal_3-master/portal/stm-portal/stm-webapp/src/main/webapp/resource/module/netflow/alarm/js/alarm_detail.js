(function($){
	
	function NetflowAlarmDetail(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	
	NetflowAlarmDetail.prototype = {
			
		_alarmRuleDatagrid: undefined,
		_resourcegrid: undefined,
		_accObj: undefined,
		
		//id
		_netflowAlarmObj: undefined,
		_returnProfileId: undefined,
		
		//form
		_basicForm: undefined,
		_ruleSettingForm: undefined,
		_thresholdForm: undefined,
		_ruleTabCached: false,
		
		open : function() {
			var dlg = this._mainDiv = $('<div/>'), 
				that = this, 
				type = that.cfg.type;
			
			that._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/netflow/alarm/alarm_detail.html'),
				title : (type && type == 'edit' ? '编辑' : '添加') + '告警',
				width: 850,
				height : 450,
				top: 60,
				resizable : false,
				cache : false,
				onLoad : function() {
					that._init(dlg);
				},
				onClose:function(){
				},
				buttons:[{
					text: '添加',
					handler: function() {
						//save alarm setting
						var objj = that._mainDiv.find('.netflow-alarm-basicinfo-form').find('input:radio:checked').val();
						var msg2 = that._checkMethods.checkResource(that, objj);
						var msg3 = that._checkMethods.checkRuleSetting(that);
						var msg4 = that._checkMethods.thresholdSetting(that);
						var msg5 = that._checkMethods.checkReceiver(that);
						if(msg2 != '') {
							alert(msg2);
							return;
						}
						if(msg3 != '') {
							alert(msg3);
							return;
						}
						if(msg4 != '') {
							alert(msg4);
							return;
						}
						if(msg5 != '') {
							alert(msg5);
							return;
						}
						if(that._returnProfileId != undefined) {
							that._saveMethods.saveResource(that);
							that._saveMethods.saveRules(that);
							that._saveMethods.saveThreshold(that);
							var refresh = that.cfg.refresh;
							if(refresh) {
								refresh();
							}
							dlg.dialog('close');
						} else {
							alert('请先保存基本信息');
						}
					}
				}, {
					text: '取消',
					handler: function() {
						//cancel dialog
						var refresh = that.cfg.refresh;
						if(refresh) {
							refresh();
						}
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
		_id : '#oc_netflow_alarm_detail_main',
		_init : function(dlg) {
			var that = this;
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			
			var acc = that._accObj = that._mainDiv.accordion({
				onSelect: function(title, index) {
					if(index == 4){
						var objs = null;
						var type = that._mainDiv.find('.netflow-alarm-basicinfo-form').find('input:radio:checked').val();
						if(type == 1) {
							objs = that._resourcegrid.getRightTreeeCheckedData();
						} else if(type == 2) {
							objs = that._resourcegrid.getRightRows();
						} else if(type == 3) {
							// objs = that._resourcegrid.getRightRows();
							that._alarm_panel.panel({
								height: '200px',
								href: oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRules.html'),
								onLoad:function(){
									oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRules.js',function(){
										oc.resource.alarmrules.netFlowOpen(that._returnProfileId);
									});
								}
							});
						}
						if(objs != null && objs.length > 0){
							var ids = new Array();
							for(var i = 0;i < objs.length;i++){
								if(objs[i].resourceIds){
									ids.concat(objs[i].resourceIds.split(","));
								}
							}
							that._alarm_panel.panel({
								height: '200px',
								href: oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRules.html'),
								onLoad:function(){
									oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRules.js',function(){
										oc.resource.alarmrules.netFlowOpen(that._returnProfileId, ids);
									});
								}
							});
						}
					}
				}
			});
			
			//init the dialog all accordions
			that._initMethods.initBaiscInfo(that);
			that._initMethods.initResource(that);
			that._initMethods.initRuleSetting(that);
			that._initMethods.initThreshholdSetting(that);
//			that._initMethods.alarmRule(that);
			
			//if edit mode
			if(that.cfg.type != 'add') {
				that._returnProfileId = that.cfg.id;
				that._ruleTabCached = true;
				
				that._loadEdit.loadBasicInfo(that);
				setTimeout(function(){
					that._loadEdit.loadResource(that);
				},200);
				
//				that._loadEdit.loadRuleSetting(that);
				that._loadEdit.loadThresholdSetting(that);
//				that._loadEdit.loadAlarmRuleList(that);
				
				var alarmRulesDiv = $('<div/>');
				var accordionAddCfg4 = {
						title: '告警规则',
						content: alarmRulesDiv,
						selected: false
				};
				that._accObj.accordion('add', accordionAddCfg4);
				that._alarm_panel = alarmRulesDiv.panel({
					height: '200px',
					href:oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRules.html'),
					onLoad:function(){
						oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRules.js',function(){
							oc.resource.alarmrules.netFlowOpen(that._returnProfileId);
						});
					}
				});
			}
			
		},
		_renderInterface: function(that) {
			var tarDiv = that._mainDiv.find('.netflow-alarm-resource-choosen');
			tarDiv.empty();
			that._resourcegrid = oc.ui.picktree({
				selector: tarDiv,
				lUrl: oc.resource.getUrl('netflow/alarm/getResource.htm?type=1'),
				dataType: 'array',
				requestType: 'sync',
				isInteractive: true
			
			});
		},
		_loadInterface: function(that) {
			var tarDiv = that._mainDiv.find('.netflow-alarm-resource-choosen');
			tarDiv.empty();
			that._resourcegrid = oc.ui.picktree({
				selector: tarDiv,
				lUrl: oc.resource.getUrl('netflow/alarm/getInterfaceResourceleft.htm?profileID='+that._returnProfileId),
				rUrl: oc.resource.getUrl('netflow/alarm/getInterfaceResourceright.htm?profileID='+that._returnProfileId),
				dataType: 'array',
				requestType: 'sync',
				isInteractive: true
			});
		},
		_renderIfgroup: function(that) {
			var tarDiv = that._mainDiv.find('.netflow-alarm-resource-choosen');
			tarDiv.empty();
			var ifgroup = that._resourcegrid = oc.ui.pickgrid({
				selector: tarDiv,
				//leftUrl:oc.resource.getUrl('netflow/alarm/getResource.htm?type=2'),
				isInteractive:true,
				leftColumns: [[
							    {field:'',checkbox:true},
								{field:'id',title:'',hidden:true},
								{field:'resourceIds',title:'',hidden:true},
								{field:'name',title:'接口组名称'},
				]],
				rightColumns: [[
				                {field:'',checkbox:true},
					           	{field:'id',title:'',hidden:true},
					           	{field:'resourceIds',title:'',hidden:true},
								{field:'name',title:'接口组名称'},
				]]
			});
			 oc.util.ajax({
				  url: oc.resource.getUrl('netflow/alarm/getResource.htm'),
				  data:{
					  type:2
				  },
				  success:function(data){
					  if(data.data){
						  ifgroup.loadData("left",{"code":200,"data":{"total":0,"rows":data.data}});
					  }else{
						  alert('查询指标失败!');
					  }
				  }
			  });
		},
		_loadIfgroup: function(that) {
			var tarDiv = that._mainDiv.find('.netflow-alarm-resource-choosen');
			tarDiv.empty();
			var ifgroup = that._resourcegrid = oc.ui.pickgrid({
				selector: tarDiv,
				//leftUrl:oc.resource.getUrl('netflow/alarm/getResource.htm?type=2'),
				isInteractive:true,
				leftColumns: [[
							    {field:'',checkbox:true},
								{field:'id',title:'',hidden:true},
								{field:'resourceIds',title:'',hidden:true},
								{field:'name',title:'接口组名称'},
				]],
				rightColumns: [[
				                {field:'',checkbox:true},
					           	{field:'id',title:'',hidden:true},
					           	{field:'resourceIds',title:'',hidden:true},
								{field:'name',title:'接口组名称'},
				]]
			});
			 oc.util.ajax({
				  url: oc.resource.getUrl('netflow/alarm/getleftIfGroup.htm'),
				  data:{
					  profileId:that._returnProfileId
				  },
				  success:function(data){
					  if(data.data){
						  ifgroup.loadData("left",{"code":200,"data":{"total":0,"rows":data.data}});
					  }else{
						  alert('查询指标失败!');
					  }
				  }
			  });
			 oc.util.ajax({
				  url: oc.resource.getUrl('netflow/alarm/getrightIfGroup.htm'),
				  data:{
					  profileId:that._returnProfileId
				  },
				  success:function(data){
					  if(data.data){
						  ifgroup.loadData("right",{"code":200,"data":{"total":0,"rows":data.data}});
					  }else{
						  alert('查询指标失败!');
					  }
				  }
			  });
		},
		_renderIpgroup: function(that) {
			var tarDiv = that._mainDiv.find('.netflow-alarm-resource-choosen');
			tarDiv.empty();
			var ipgroup = that._resourcegrid = oc.ui.pickgrid({
				selector: tarDiv,
				isInteractive:true,
				//leftUrl:oc.resource.getUrl('netflow/alarm/getResource.htm?type=3'),
				leftColumns: [[
				            {field:'',checkbox:true},
				           	{field:'id',title:'',hidden:true},
							{field:'name',title:'IP分组名称'},
				]],
				rightColumns: [[
				                {field:'',checkbox:true},
					           	{field:'id',title:'',hidden:true},
								{field:'name',title:'IP分组名称'},
				]]
			});
			 oc.util.ajax({
				  url: oc.resource.getUrl('netflow/alarm/getResource.htm'),
				  data:{
					  type:3
				  },
				  success:function(data){
					  if(data.data){
						  ipgroup.loadData("left",{"code":200,"data":{"total":0,"rows":data.data}});
					  }else{
						  alert('查询指标失败!');
					  }
				  }
			  });
		},
		_loadIpgroup: function(that) {
			var tarDiv = that._mainDiv.find('.netflow-alarm-resource-choosen');
			tarDiv.empty();
			var ipgroup = that._resourcegrid = oc.ui.pickgrid({
				selector: tarDiv,
				isInteractive:true,
				//leftUrl:oc.resource.getUrl('netflow/alarm/getResource.htm?type=3'),
				leftColumns: [[
				            {field:'',checkbox:true},
				           	{field:'id',title:'',hidden:true},
							{field:'name',title:'IP分组名称'},
				]],
				rightColumns: [[
				                {field:'',checkbox:true},
					           	{field:'id',title:'',hidden:true},
								{field:'name',title:'IP分组名称'},
				]]
			});
			
			oc.util.ajax({
				  url: oc.resource.getUrl('netflow/alarm/getleftIPGroup.htm'),
				  data:{
					  profileId:that._returnProfileId
				  },
				  success:function(data){
					  if(data.data){
						  ipgroup.loadData("left",{"code":200,"data":{"total":0,"rows":data.data}});
					  }else{
						  alert('查询指标失败!');
					  }
				  }
			  });
			 oc.util.ajax({
				  url: oc.resource.getUrl('netflow/alarm/getrightIPGroup.htm'),
				  data:{
					  profileId:that._returnProfileId
				  },
				  success:function(data){
					  if(data.data){
						  ipgroup.loadData("right",{"code":200,"data":{"total":0,"rows":data.data}});
					  }else{
						  alert('查询指标失败!');
					  }
				  }
			  });
			
		},
		_initMethods: {
			'initBaiscInfo': function(that) {
				that._basicForm = oc.ui.form({selector: that._mainDiv.find('.netflow-alarm-basicinfo-form')});
				
				that._mainDiv.find('.netflow-alarm-basicinfo-alarmobj').on('click', function() {
					if($(this).val() == 1) { //接口,重新渲染接口资源，使用picktree
						that._renderInterface(that);
					} else if($(this).val() == 2) { // 接口组,使用datagrid
						that._renderIfgroup(that);
					} else if($(this).val() == 3) { // IP分组，使用datagrid
						that._renderIpgroup(that);
					}
				});
				
				that._mainDiv.find('.netflow-alarm-save-basicinfo').linkbutton('RenderLB', {
					onClick: function() {
						var msg1 = that._checkMethods.checkBaiscInfo(that);
						if(msg1 != '') {
							alert(msg1);
							return;
						}
						that._saveMethods.saveBasicInfo(that);
					}
				});
			},
			'initResource': function(that) {
				that._renderInterface(that);				
			},
			'initRuleSetting': function(that) {
				//init combobox s
				var appDataObj = {};
				var protoDataObj = {};
				
				var comboboxes = [{
					placeholder: false,
					selector: '.netflow-alarm-rule-net',
					data: [{
						id: '1', name: 'IP地址'
					}, {
						id: '2', name: 'IP范围'
					}],
					onSelect: function(d) {
						if(d.id == 1) {
							that._mainDiv.find('.netflow-alarm-if-ip-range').addClass('hide');
						} else {
							that._mainDiv.find('.netflow-alarm-if-ip-range').removeClass('hide');
						}
					}
				}, {
					selector: '.netflow-alarm-threshold-filter-in-out',
					placeholder: false,
					data: [{
						id: '1', name: '流入'
					}, {
						id: '2', name: '流出'
					}, {
						id: '3', name: '流入流出'
					}]
				}, {
					selector: '.netflow-alarm-threshold-filter-type',
					placeholder: false,
					data: [{
						id: '1', name: '流量'
					}, {
						id: '2', name: '数据包'
					}, {
						id: '3', name: '速度'
					}, {
						id: '4', name: '使用率'
					}],
					onSelect: function(d) {
						that._common.initThresholdDisplay(that);
					}
				}];
				if(that.cfg.type == 'add') {
					appDataObj = {
							placeholder: false,
							selector: '.netflow-alarm-rule-app',
							url:oc.resource.getUrl('netflow/alarm/getApplication.htm'),
							filter:function(d){
								return d.data;
							}
					};
					protoDataObj = {
							placeholder: false,
							selector: '.netflow-alarm-rule-proto-port',
							url:oc.resource.getUrl('netflow/alarm/getProtocol.htm'),
							filter:function(d){
								return d.data;
							}
					};
					comboboxes.push(appDataObj);
					comboboxes.push(protoDataObj);
				}
				var ruleForm = that._ruleSettingForm = oc.ui.form({
					selector: that._mainDiv.find('.netflow-alarm-rule-form'),
					combobox: comboboxes
				});
				
				//the default option of network is ipaddress
				that._mainDiv.find('.netflow-alarm-if-ip-range').addClass('hide');
				
			},
			'initThreshholdSetting': function(that) {
				that._thresholdForm = oc.ui.form({selector: '.netflow-alarm-threshold-form'});
				
				that._mainDiv.find('.netflow-alarm-add-threshold').linkbutton('RenderLB', {
					onClick: function() {
						that._common._addThreshold(that);
					}
				}).trigger('click');
			}
		},
		_loadEdit: {
			'loadBasicInfo': function(that) {
				if(that.cfg.id) {
					oc.util.ajax({
						url : oc.resource.getUrl('netflow/alarm/loadBasic.htm'),
						data : {
							profileId: that.cfg.id
						},
						async:false,
						success : function(data) {
							if(data.code == 200) {
								
								var basicInfo = data.data.basicInfo;
								var apps = data.data.apps;
								var protocols = data.data.protocols;
								
								//init id
								that._mainDiv.find('#basic_form_id').val(that._returnProfileId);
								that._mainDiv.find('#rule_setting_profile_id').val(basicInfo.id);
								that._mainDiv.find('#threshold_setting_profile_id').val(basicInfo.id);
								//that._returnProfileId = basicInfo.id;

								that._netflowAlarmObj = basicInfo.netflowAlarmObj
								that._basicForm.val(basicInfo);

								//if endip is null then choose IP地址else IP范围
								var endIp = basicInfo.netflowNetworkIpAddrEnd;
								if(endIp && endIp != '') {
									that._mainDiv.find('.netflow-alarm-if-ip-range').removeClass('hide');
									that._mainDiv.find('.netflow-alarm-rule-net').combobox('setValue', 2);
								} else {
									that._mainDiv.find('.netflow-alarm-if-ip-range').addClass('hide');
									that._mainDiv.find('.netflow-alarm-rule-net').combobox('setValue', 1);
								}
								
								//load rule setting
								oc.ui.combobox({
									placeholder: false,
									selector: '.netflow-alarm-rule-app',
									data: apps
								});
								oc.ui.combobox({
									placeholder: false,
									selector: '.netflow-alarm-rule-proto-port',
									data: protocols
								});
								
								that._ruleSettingForm.val(basicInfo);
							}
						}
					});					
				}
			},
			'loadResource': function(that) {
				var obj = that._netflowAlarmObj;
				if(obj == 1) {
					that._loadInterface(that);
				} else if(obj == 2) {
					that._loadIfgroup(that);
				} else if(obj == 3) {
					that._loadIpgroup(that);
				}
			},
			'loadRuleSetting': function(that) {
				//load protocal list,app list
//				if(that.cfg.id) {
//					oc.util.ajax({
//						url : oc.resource.getUrl('netflow/alarm/loadRuleSetting.htm'),
//						data : {
//							
//						},
//						async:false,
//						success : function(data) {
//							if(data.code == 200) {
//								
//							}
//						}
//					});
//					
//				}
			},
			'loadThresholdSetting': function(that) {
				if(that.cfg.id) {
					oc.util.ajax({
						url : oc.resource.getUrl('netflow/alarm/loadThreshold.htm'),
						data : {
							profileId: that.cfg.id
						},
						async:false,
						success : function(data) {
							if(data.code == 200) {
								var thresholds = data.data; // 返回值为数组

								that._mainDiv.find('.netflow-alarm-threshold-tb').empty();
								for(var i=0,len=thresholds.length; i<len; i++) {
									that._mainDiv.find('.netflow-alarm-add-threshold').trigger('click');
								}
								
								that._common.initThresholdDisplay(that);

								var lines = that._mainDiv.find('.netflow-alarm-threshold-tb').find('tr');
								for(var j=0,len2=lines.length; j<len2; j++) {
									var t = thresholds[j];
									var q = $(lines[j]);
									q.find('.netflow-alarm-threshold-minute').val(t.netflowAlarmThresholdMinute);
									q.find('.netflow-alarm-threshold-count').val(t.netflowAlarmThresholdCount);
									q.find('.netflow-alarm-threshold-value').val(t.netflowAlarmThresholdValue);
									
									q.find('.netflow-alarm-threshold-unit').combobox('setValue', t.netflowAlarmFlowUnit);
									q.find('.netflow-alarm-threshold-level').combobox('setValue', t.netflowAlarmThresholdLevel);
								}
							}
						}
					});
				}
			},
//			'loadAlarmRuleList': function(that) {
//				if(that.cfg.id) {
//					oc.util.ajax({
//						url : oc.resource.getUrl(''),
//						data : {
//							
//						},
//						async:false,
//						success : function(data) {
//							if(data.code == 200) {
//								that._alarmRuleDatagrid.selector.datagrid('load', {
//									profileId: 0
//								});
//							}
//						}
//					});
//				}
//			}
		},
		_common: {
			'_addThreshold': function(that) {
				var flowDiv = '<div style="margin: auto;">' +
								'<div class="netflow-alarm-threshold-first locate-left">' +
									'<input type="text" name="netflowAlarmThresholdMinute" class="netflow-alarm-threshold-minute" style="width: 90px;" />' +
									'<span>分钟</span>' +
									'<input type="text" name="netflowAlarmThresholdCount" class="netflow-alarm-threshold-count" style="width: 90px;" />' +
									'<span>次&nbsp;&nbsp;&nbsp;&nbsp;</span>' +
									'<span>超过</span>' +
									'<input type="text" name="netflowAlarmThresholdValue" class="netflow-alarm-threshold-value" style="width: 90px;" />' +
									'<span class="netflow-alarm-threshold-unit-p">' +
										'<input type="text" name="netflowAlarmFlowUnit" class="netflow-alarm-threshold-unit"/>' +
									'</span>' +
									'<span class="netflow-alarm-threshold-packet-label">数据包</span>' +
									'<span class="netflow-alarm-threshold-pctge-label">%</span>' +
									'<span>&nbsp;&nbsp;&nbsp;&nbsp;严重度</span>' +
									'<input type="text" name="netflowAlarmThresholdLevel" class="netflow-alarm-threshold-level" />' +
								'</div>' +
								'<span class="netflow-alarm-delete-threshold-item locate-right r-h-ico r-h-delete"></span>' +
							'</div>';
				
				var tr = $('<tr class="netflow-alarm-threshold-flow-tr" />'), td = $('<td/>'), table = that._mainDiv.find('.netflow-alarm-threshold-tb');
				td.append(flowDiv);
				tr.append(td);
				table.append(tr);

				var unitGridData = that._common.initThresholdDisplay(that, td);
				
				oc.ui.combobox({
					selector: td.find('.netflow-alarm-threshold-level'), 
					placeholder: false,
					width: 90,
					data: [{
						id: '1', name: '严重'
					}, {
						id: '2', name: '警告'
					}]
				});
				
				td.find('.netflow-alarm-delete-threshold-item').linkbutton({
					onClick: function() {
						$(this).parents('tr:first').remove();
					}
				});
			},
			'initThresholdDisplay': function(that, td) {
				var ret = [];
				var unitGridData = [{id: '1', name: 'KB'}, {id: '1024', name: 'MB'}, {id: '1048576', name: 'GB'}];
				var flowSpeedData = [{id: '1', name: 'Kbps'}, {id: '1024', name: 'Mbps'}, {id: '1048576', name: 'Gbps'}];
	
				//得到规则设置中选择的类型
				var type = that._mainDiv.find('.netflow-alarm-threshold-filter-type').combobox('getValue');
				if(type == 1) { // 流量
					ret = unitGridData;
					that._mainDiv.find('.netflow-alarm-threshold-unit-p').removeClass('hide');
					that._mainDiv.find('.netflow-alarm-threshold-packet-label').addClass('hide');
					that._mainDiv.find('.netflow-alarm-threshold-pctge-label').addClass('hide');
				} else if(type == 2) {//数据包
					that._mainDiv.find('.netflow-alarm-threshold-packet-label').removeClass('hide');
					that._mainDiv.find('.netflow-alarm-threshold-unit-p').addClass('hide');
					that._mainDiv.find('.netflow-alarm-threshold-pctge-label').addClass('hide');
				} else if(type == 3) {//速度
					ret = flowSpeedData;
					
					that._mainDiv.find('.netflow-alarm-threshold-unit-p').removeClass('hide');
					that._mainDiv.find('.netflow-alarm-threshold-packet-label').addClass('hide');
					that._mainDiv.find('.netflow-alarm-threshold-pctge-label').addClass('hide');
				} else if(type == 4) {//使用率
					that._mainDiv.find('.netflow-alarm-threshold-pctge-label').removeClass('hide');
					that._mainDiv.find('.netflow-alarm-threshold-packet-label').addClass('hide');
					that._mainDiv.find('.netflow-alarm-threshold-unit-p').addClass('hide');
				}
				
				oc.ui.combobox({
					selector: (td ? td.find('.netflow-alarm-threshold-unit') : '.netflow-alarm-threshold-unit'),
					placeholder: false,
					width: 90,
					data: ret
				});
				return ret;
			},
			'isIpFormat': function(what) {
				var ipReg = /^([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])$/;
			    var ret = ipReg.test(what);
			    if(ret) { // ip
			    	return true;
			    }
			    return false;
			}
		},
		_checkMethods: {
			'checkBaiscInfo': function(that) {
				var form = that._mainDiv.find('.netflow-alarm-basicinfo-form');
				var name = form.find('.netflow-alarm-basicinfo-name').val();
				if(!name || $.trim(name) == '') {
					return '请填写配置名称';
				}
				var objs = form.find('.netflow-alarm-basicinfo-alarmobj') || [];
				var checkedOne = false;
				for(var i=0,len=objs.length; i<len; i++) {
					if( $(objs[i]).attr('checked') == 'checked' ) {
						checkedOne = true;
						break;
					}
				}
				if(!checkedOne) {
					return '请选择对象';
				}
				return '';
			},
			'checkResource': function(that, type) {
				if(type == 1) {
					//get picktree right data
					//var selectedIfs = that._resourcegrid.getRightTreeData();
					var selectedIfs = that._resourcegrid.getRightTreeeCheckedData();
					if(selectedIfs.length == 0) {
						return '请选择资源中要告警的接口';
					}
				} else if(type == 2) {
					var selectedIfgroups = that._resourcegrid.getRightRows();
					if(selectedIfgroups.length == 0) {
						return '请选择资源中要告警的接口组';
					}
				} else if(type == 3) {
					var selectedIpgroups = that._resourcegrid.getRightRows();
					if(selectedIpgroups.length == 0) {
						return '请选择资源中要告警的IP分组';
					}
				}
				return '';

			},
			'checkRuleSetting': function(that) {
				var port = that._mainDiv.find('.netflow-alarm-rule-proto-port-value').val();
				var portReg = new RegExp('\d+|[\d+-\d+]')
				if(!port || $.trim(port) == '') {
					return '请填写端口';
				}
				if(!portReg.test(port)) {
					return '规则设置端口输入不正确';
				}
				if(port.indexOf('-') != -1) {
					var sport = port.substring(0, port.indexOf('-'));
					var eport = port.substring(port.indexOf('-')+1, port.length);
					if(sport > eport) {
						return '规则设置端口范围不正确';
					}
				}
				var ipType = that._mainDiv.find('.netflow-alarm-rule-net').combobox('getValue');
				if(ipType == 1) {
					var ip = that._mainDiv.find('.netflow-alarm-ip-range-start').val();
					if(!ip || $.trim(ip) == '') {
						return '请填写IP地址';
					}
					if(!that._common.isIpFormat(ip)) {
						return 'IP格式不正确';
					}
				} else if(ipType == 2) {
					var ip1 = that._mainDiv.find('.netflow-alarm-ip-range-start').val();
					var ip2 = that._mainDiv.find('.netflow-alarm-ip-range-end').val();
					if(!ip1 || $.trim(ip1) == '') {
						return '请填写开始IP地址';
					}
					if(!ip2 || $.trim(ip2) == '') {
						return '亲填写结束IP地址';
					}
					if(!that._common.isIpFormat(ip1)) {
						return '规则设置开始IP格式不正确';
					}
					if(!that._common.isIpFormat(ip2)) {
						return '规则设置结束IP格式不正确';
					}
				}
				return '';
			},
			'thresholdSetting': function(that) {
				var thresholdTable = that._mainDiv.find('.netflow-alarm-threshold-tb');
				var trs = thresholdTable.find('tr');
				var numberReg = /\d+/;
				for(var i=0,len=trs.length; i<len; i++) {
					var n = $(trs[i]);
					var minutes = n.find('.netflow-alarm-threshold-minute').val();
					var count = n.find('.netflow-alarm-threshold-count').val();
					var tvalue = n.find('.netflow-alarm-threshold-value').val();
					if((!minutes || $.trim(minutes) == '') || (!count || $.trim(count) == '') || (!tvalue || $.trim(tvalue) == '')) {
						return '请完善第' + (i+1) + '条阈值信息';
					}
					if(!numberReg.test(minutes) || !numberReg.test(count) || !numberReg.test(tvalue)) {
						return '阈值请填写数字';
					}
				}
				return '';
			},
			'checkReceiver': function(that) {
				var div = that._mainDiv.find('#alarmRulesDatagrid');
				if(div==null||div==undefined||div.length==0){
					return "请先在基本信息处保存基本信息";
				}
				var rows = that._mainDiv.find('#alarmRulesDatagrid').datagrid('getRows');
				if(!rows || rows.length == 0) {
					return '请设置告警规则';
				}
				return '';
			}
		},
		_deleteRow: function(ids) {
			var ret = false;
			oc.util.ajax({
				url : oc.resource.getUrl(''),
				data : {
					ids: ids
				},
				async:false,
				errorMsg : '获取资源列表出错',
				success : function(data) {
					if(data.code == 200) {
						ret = true;
					}
				}
			});
			return ret;
		},
		_saveMethods: {
			'saveBasicInfo': function(that) {
				var f = that._mainDiv.find('.netflow-alarm-basicinfo-form');
				oc.util.ajax({
					url: oc.resource.getUrl('netflow/alarm/getCount.htm'),
					data :{
						"name" : f.find("input[name='netflowAlarmConfigName']").val(),
						"id" : f.find("#basic_form_id").val()
					},
					success: function(data) {
						if(data && data.code == 200 && data.data == 0){
							var form = that._mainDiv.find('.netflow-alarm-basicinfo-form').serialize();
							oc.util.ajax({
								url: oc.resource.getUrl('netflow/alarm/addAlarmBasic.htm'),
								data: form,
								async: false,
								errorMsg: '保存基本信息出错',
								successMsg: '保存基本信息成功',
								success: function(data) {
									if(data.code == 200) {
										//得到profileId
										if(data.data>-1){
											if(that.cfg.type == 'add') {
												that._returnProfileId = data.data;
											}
											that._mainDiv.find('#basic_form_id').val(that._returnProfileId);
											that._mainDiv.find('#rule_setting_profile_id').val(that._returnProfileId);
											that._mainDiv.find('#threshold_setting_profile_id').val(that._returnProfileId);
											if(that._returnProfileId && !that._ruleTabCached) {
												var alarmRulesDiv = $('<div/>');
												var accordionAddCfg4 = {
														title: '告警规则',
														content: alarmRulesDiv,
														selected: false
												};
												that._accObj.accordion('add', accordionAddCfg4);
												that._alarm_panel = alarmRulesDiv.panel({
													height: '200px',
													href:oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRules.html'),
													onLoad:function(){
														oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRules.js',function(){
															oc.resource.alarmrules.netFlowOpen(that._returnProfileId);
														});
													}
												});
												that._ruleTabCached = true;
											}
										}
									}
								}
							});
						}else{
							alert("【配置名称】已经存在!");
						}
					}
				});
			},
			'saveResource': function(that) {
				//获取对象类型
				var objs = that._mainDiv.find('.netflow-alarm-basicinfo-alarmobj');
				var type = that._mainDiv.find('.netflow-alarm-basicinfo-form').find('input:radio:checked').val();
				var data = {};
				if(type == 1) {
					var deviceId = '', ifId = '';
					var rows = that._resourcegrid.getRightTreeData();
					for(var i=0,len=rows.length; i<len; i++) {
						var fakeId = rows[i].id;
						if(fakeId.indexOf('p') != -1) {
							deviceId = fakeId.substring(fakeId.indexOf('p')+1, fakeId.length);
						} else if(fakeId.indexOf('c') != -1) {
							var tid = fakeId.substring(fakeId.indexOf('c')+1, fakeId.length);
							if(ifId != '') {
								ifId += ',';
							}
							ifId += tid;
						}
					}
					data = {
						deviceId: deviceId,
						ids: ifId,
						profileId: that._returnProfileId,
						netflowAlarmObj: type
					}
				} else if(type == 2 || type == 3) {
					var tid = '';
					var rows = that._resourcegrid.getRightRows();
					for(var i=0,len=rows.length; i<len; i++) {
						if(tid != '') {
							tid += ',';
						}
						tid += rows[i].id;
					}
					data = {
						ids: tid,
						profileId: that._returnProfileId,
						netflowAlarmObj: type
					}
				}
				oc.util.ajax({
					url: oc.resource.getUrl('netflow/alarm/addAlarmResource.htm'),
					data: data,
					async: false,
					errorMsg: '保存资源出错',
					success: function(data) {
						
					}
				});
				
			},
			'saveRules': function(that) {
				var ruleForm = that._mainDiv.find('.netflow-alarm-rule-form').serialize();
				oc.util.ajax({
					url: oc.resource.getUrl('netflow/alarm/addAlarmRules.htm'),
					data: ruleForm,
					async: false,
					errorMsg: '保存规则出错',
					success: function(data) {
					}
				});
			},
			'saveThreshold': function(that) {
				var thresholdForm = that._mainDiv.find('.netflow-alarm-threshold-form').serialize();
				oc.util.ajax({
					url: oc.resource.getUrl('netflow/alarm/addAlarmThreshold.htm'),
					data: thresholdForm,
					async: false,
					errorMsg: '保存阈值出错',
					success: function(data) {
					}
				});
			}
		}
			
	};
	
	oc.ns('oc.module.netflow.alarm');

	oc.module.netflow.alarm = {
		open : function(cfg) {
			new NetflowAlarmDetail(cfg).open();
		}
	};
	
})(jQuery);