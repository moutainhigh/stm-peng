(function() {
	function snmptrapCopy(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	snmptrapCopy.prototype = {
		constructor : snmptrapCopy,
		_baseInfoForm : undefined,
		_isCopySuccess:false,
		_snmpTrapForm : undefined,
		_strategyId:undefined,
		_tacticsUserForm : undefined,
		_pickgrid:undefined,
		_dialog:undefined,
		cfg : undefined,
		_ruleDatagrid:undefined,
		_alarmDatagrid:undefined,
		open : function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), that = this;
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/resource-management/strategy/snmptrapCopy.html'),
				title : '复制SNMPTrap策略',
				height : 550,
				width:1000,
				resizable : true,
				cache : false,
				onLoad : function() {
					that._init(dlg);
					that._loadForm(that);//如果是编辑，加载域基本信息
				},
				buttons : [ {
					text : "确定复制",
					iconCls : "fa fa-check-circle",
					handler : function() {
						if (that.cfg.callback) {
							that.cfg.callback();// 关闭弹出框后刷新域列表
						}
						// 保存策略基本信息
						that._saveMethods.baseInfo(that);
						//保存资源
						//that._saveMethods.saveResource(that);
						//保存告警规则
						//that._saveMethods.saveAlarmRule(that);
						if(that._isCopySuccess){
							// 保存snmptrap
							that._saveMethods.saveRules(that);
							dlg.dialog('close');
						}
					}
				}, {
					text : "取消",
					iconCls : "fa fa-times-circle",
					handler : function() {
						dlg.dialog('close');
					}
				} ]
			});
		},
		_defaults : {
			id : undefined
		},
		_flag:'add',
		_mainDiv : undefined,
		_id : '#oc_resource_management_snmpstrategy_copy',
		_init : function(dlg) {
			var that = this;
			that._mainDiv=dlg.find(that._id).attr('id',oc.util.generateId());
			that._initMethods['基本信息'](that);//初始化基本信息
			that._initMethods['规则定义'](that);//初始化规则定义
			that._initMethods['策略使用者'](that);//初始化选择资源
			that._initMethods['告警规则'](that);//初始化告警规则
			that._mainDiv.accordion({
				onSelect:function(title,index){
					that._initMethods[title](that);
				}
			});
		},
		_initMethods : {
			'基本信息' : function(that) {
				if(!that._baseInfoForm) {
					var domain = $.extend({},{selector:'[name=domainId]'},
				        that.cfg.loginUserType!=2 ? {data:oc.index.getDomains()} : {url:oc.resource.getUrl('system/user/getLoginUserDomains.htm'),
							filter:function(data){
								return data.data;
							}
						});
					that._baseInfoForm = oc.ui.form({
						selector : that._mainDiv.find("form.baseInfo_form"),
						combobox:[domain],
						placeholder:false
					});
				}
			},
			'规则定义':function(that) {
				if (!that._ruleDatagrid) {
					that._ruleDatagrid = oc.ui.datagrid({
						selector : that._mainDiv
								.find(".tactics_datagrid"),
						url : oc.resource.getUrl('portal/syslog/getSyslogRules.htm'),
						queryParams : {
							strategyId : that.cfg.id
						},
						fit : true,
//						octoolbar : {
//							right : toolbar
//						},
						pagination : false,
						selectedCfg : {
							field : 'isChecked',
							fieldValue : 1
						},
						columns : [ [
								{
									field : 'id',
									title : '-',
									checkbox : true,
									width : 20
								},
								{
									field : 'isOpen',
									title : '规则状态',
									width : 60,
									formatter : function(value, row,
											index) {
										return "<span class='oc-top0 locate-left status oc-switch "
												+ (value == 1 ? "open"
														: "close")
												+ "' data-data='"
												+ value + "'></span>";
									}
								},
								{
									field : 'isAlarm',
									title : '触发警告',
									width : 60,
									formatter : function(value, row,
											index) {
										return "<span class='oc-top0 locate-left status oc-switch "
												+ (value == 1 ? "open"
														: "close")
												+ "' data-data='"
												+ value + "'></span>";
									}
								},
								{
									field : 'name',
									title : '规则名称',
									sortable : true,
									width : 100
								},
								{
									field : 'alarmLevel',
									title : '级别',
									sortable : true,
									width : 150,
									formatter : function(value, row,index) {
										var select = $("<select name='alarmLevel'/>");
										var option1 = $("<option/>").val('CRITICAL').html('致命');
										var option2 = $("<option/>").val('SERIOUS').html('严重');
										var option3 = $("<option/>").val('WARN').html('警告');
//										var option4 = $("<option/>").val('UNKOWN').html('未知');
										if (value == "CRITICAL") {
											option1.attr("selected",true);
										}
										if (value == "SERIOUS") {
											option2.attr("selected",true);
										}
										if (value == "WARN") {
											option3.attr("selected",true);
										}
//										if (value == "UNKOWN") {
//											option4.attr("selected",true);
//										}
										select.append(option1);
										select.append(option2);
										select.append(option3);
//										select.append(option4);
										return $("<div/>").append(select).html();
									}
								},
								{
									field : 'keywords',
									title : '关键字',
									width : 100
								},
								{
									field : 'snmptrapCategory',
									title : '日志级别',
									sortable : true,
									width : 300,
//									formatter : function(value, row,index) {
//										var logLevelArr = value.split(",");
//										var returnValue = [];
//										var object = oc.util.getDictObj('syslog_level');
//										for (var i = 0; i < logLevelArr.length; i++) {
//											returnValue[i] = object[logLevelArr[i]].name;
//										}
//										return returnValue.join();
//									}
								},{
									field : 'logicType',
									title : '逻辑',
									width : 10,
									hidden:true
								},{
									field : 'description',
									title : '说明',
									width : 10,
									hidden:true
								} ] ],
						onLoadSuccess : function(data) {
							oc.ui.combobox({
								selector : $("select[name='alarmLevel']")
							});
						}
					});
				}
			},
			'策略使用者' : function(that) {
				var resourceDiv = $("#selectResource");
				var ipAdressDiv = $("#ipAdress");
				$("input[name=radio]").click(function() {
					var radioValue = $.trim($("input[name='radio']:checked").val());
					if(radioValue==1){
						ipAdressDiv.hide();
						resourceDiv.show();
						if(!that._tacticsUserForm) {
							var columns = [[
			                    {field:'resourceId',title:'资源ID',checkbox:true,sortable:true},
			                    {field:'resourceIp',title:'IP地址',width:40},
			                    {field:'name',title:'资源名称',width:50},
			                    {field:'resourceType',title:'资源类型',width:30}
		                    ]];
							that._pickgrid = oc.ui.pickgrid({
								selector:that._mainDiv.find('#resource_select_strategrid'),
								height:50,
								leftColumns:columns,
								rightColumns:columns,
								isInteractive:true
							});
							//初始化右侧树
//							oc.util.ajax({
//								  url: oc.resource.getUrl('portal/syslog/getResourceByStrategy.htm'),
//								  timeout:'5000',
//								  data:{strategyId:that.cfg.id},
//								  success:function(data){
//									  if(data.data){
//										  that._pickgrid.loadData("right",{"code":200,"data":{"total":0,"rows":data.data}});
//									  }else{
//										  alert('查询资源实例失败!');
//									  }
//								  }
//							  });
						}
					}else if(radioValue==2){
						resourceDiv.hide();
						ipAdressDiv.show();
					}
				});
				$("input[name='radio']:checked").trigger("click");
			},
			'告警规则' : function(that) {
				if(!that._alarmDatagrid){
					that._alarmDatagrid = oc.ui.datagrid({
						selector :  that._mainDiv.find(".alarmRuleDatagrid"),
						//url : oc.resource.getUrl('portal/syslog/getAlarmUserByStrategyId.htm'),
						queryParams:{strategyId:that.cfg.id},
						fit:true,
//						octoolbar : {
//							right : toolbar
//						},
						pagination : false,
						selectedCfg:{
							field:'isChecked',
							fieldValue:1
						},
						columns : [ [{
							field : 'userId',
							title:'-',
							checkbox : true,
							width : 20
						},{
							field : 'userName',
							title:'接收人员',
							width : 50
						}, {
							field : 'alarmMode',
							title : '告警方式',
							sortable : true,
							width : 100,
							formatter:function(value, row, rowIndex) {
								var msgCheckState = "";
								var emailCheckState = "";
								
								if(row.isMsg == 1){
									msgCheckState = 'checked="true"';
								}else{
									msgCheckState = 'class=\"checkbox_disabled\" ';
								}
								if(row.isMail == 1){
									emailCheckState = 'checked="true"';
								}else{
									emailCheckState = 'class=\"checkbox_disabled\" ';
								}
								return '<input type="checkbox" '+msgCheckState+' id="checkbox'+rowIndex+'" name="checkboxMessage" value="'+row.userId+'" style="vertical-align:middle;" >&nbsp;&nbsp;<a id='+rowIndex+' class=\"show_alarmRuleMess\" > 短信</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
								+'<input type="checkbox"'+emailCheckState+' name="checkboxEmail" value="'+row.userId+'" style="vertical-align:middle;" >&nbsp;&nbsp;<a id='+rowIndex+' class=\"show_alarmRuleEmail\" >邮件</a>';
							
							}
						}]]
					});
				}
			}
		},
		_loadForm : function() {
			//加载策略基本信息
			var that = this;
//			that._strategyId = that.cfg.id;
			oc.util.ajax({
				url:oc.resource.getUrl('resourcelog/snmptrap/getBasicStrategy.htm'),
				data:{
					id:that.cfg.id
				},
				async:false,
				successMsg:null,
				success:function(data){
					data.data.name += "_copy";
					that._baseInfoForm.val(data.data);
					that._baseInfoForm.find(".createUser").text(oc.index.getUser().account);
					that._baseInfoForm.find("#creatorId").val(oc.index.getUser().id);
					that._baseInfoForm.find(".createDate").text(formatDate(new Date(data.data.createDate)));
//					that._baseInfoForm.find(".updateUser").text(data.data.updater);
//					that._baseInfoForm.find(".updateDate").text(formatDate(new Date(data.data.updateDate)));
					that._baseInfoForm.find("#createDate").val(formatDate(new Date(data.data.createDate)));
//					that._baseInfoForm.find("#updateDate").val(formatDate(new Date(data.data.updateDate)));
					
//					var commonType = data.data.commonType;
//					data.data.commonType=commonType!=0&&commonType&&commonType.split(",");
//					that._snmpTrapForm.val(data.data);
				}
			});
		},
		_saveMethods : {
			'baseInfo' : function(that) {
				if(that._baseInfoForm.validate()){
					oc.util.ajax({
						url : oc.resource.getUrl('resourcelog/snmptrap/saveSyslogStrategyBasic.htm'),
						data : that._baseInfoForm.val(),
						async:false,
						success : function(data) {
							if(data.code == 200) {
								that._isCopySuccess = true;
								that._strategyId = data.data.strategyId;
							}
						},
						successMsg:"基本信息复制成功"
					});
				}
			},
			'saveResource':function(that){
				var ids = that._pickgrid.getRightRows();
				oc.util.ajax({
					url : oc.resource.getUrl('resourcelog/snmptrap/saveStrategyResource.htm'),
					data : {strategyId:that._strategyId,rightDatas:ids,strategyType:2},
					async:false,
					success : function(data) {
					},
					successMsg:"资源复制成功"
				});
			},
			'saveRules' : function(that) {
				// 保存规则定义
				var gridData = that._ruleDatagrid.selector.datagrid('getData');
				if(gridData.datas != null && gridData.datas != "") {
					oc.util.ajax({
						url : oc.resource.getUrl('portal/syslog/saveRuleList.htm'),
						data : {
							gridData : gridData.datas,strategyId:that._strategyId
						},
						async : false,
						success : function(data) {
						}
					});
				}
			},
			'saveAlarmRule':function(that){
				var gridData = that._alarmDatagrid.selector.datagrid('getData');
				oc.util.ajax({
//					url : oc.resource.getUrl('portal/syslog/saveAlarmRuleList.htm'),
					data : {
						gridData : gridData.datas,strategyId:that._strategyId
					},
					async : false,
					success : function(data) {
					},
					successMsg : "告警规则复制成功"
				});
			}
		}
	};
	
	function formatDate(date){
		var currentDate = "";
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		
		var hour = date.getHours();
		var minutes = date.getMinutes();
		var sencondes = date.getSeconds();
		currentDate = year+'-'+month+'-'+day
		if(hour<10){
			currentDate+=' '+'0'+hour;
		}else{
			currentDate+=' '+hour;
		}
		if(minutes<10){
			currentDate+=':'+'0'+minutes;
		}else{
			currentDate+=':'+minutes;
		}
		if(sencondes<10){
			currentDate+=':'+'0'+sencondes;
		}else{
			currentDate+=':'+sencondes;
		}
		return currentDate;
	}
	oc.ns('oc.snmptrap.strategy.copy');

	oc.snmptrap.strategy.copy = {
		open : function(cfg) {
			new snmptrapCopy(cfg).open();
		}
	};
})(jQuery);