(function() {
	function tacticsDetail(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		this._strategyId = cfg.id;
	}
	tacticsDetail.prototype = {
		constructor : tacticsDetail,
		editAlarmRule_Mess: new Array(),
		editAlarmRule_Email: new Array(),
		editAlarmRule_Alert: new Array(),
		profileType:"sysLog",
		queryType:"allQuery",
		dataReload:undefined,
		alarmRow:undefined,
		_form : undefined,
		_strategyId:undefined,
		_resourceForm:undefined,
		_domainId:undefined,
		_dialog:undefined,
		_saveBaseFlag:false,
		cfg : undefined,
		_datagrid:undefined,
		_alarmDatagrid:undefined,
		_pickgrid:undefined,
		open : function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), that = this, type = that.cfg.type;
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/resource-management/strategy/sysLogTacticsDetail.html'),
				title : ((type == 'edit') ? '编辑' : '添加') + '策略',
				height : 550,
				width:1000,
				resizable : true,
				cache : false,
				onLoad : function() {
					that._init(dlg);
					(type == 'edit') && that._loadForm(that);//如果是编辑，加载策略基本信息
				},
				buttons : [{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						if(that.cfg.callback){
							that.cfg.callback();//关闭弹出框后刷新域列表
						}
						dlg.dialog('close');
					}
				},{
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						dlg.dialog('close');
					}
				}]
			});
		},
		_defaults : {
			type : 'add',
			id : undefined
		},
		_flag:'add',
		_mainDiv : undefined,
		_id : '#oc_module_resource_management_tactics_detail',
		_init : function(dlg) {
			var that = this;
			that._flag = that.cfg.type;
			that._mainDiv=dlg.find(that._id).attr('id',oc.util.generateId());
			that._initMethods['基本信息'](that);//初始化基本信息
			if(that.cfg.type=='edit'){
				//更改基本信息保存状态
				that._saveBaseFlag = true;
//				that._initMethods['选择资源'](that);//初始化选择资源
				that._initMethods['规则定义'](that);//初始化规则定义
				that._initMethods['告警规则'](that);//初始化告警规则
			}
			that._mainDiv.accordion({
				onSelect:function(title,index){
					that._initMethods[title](that);
				},
				onBeforeSelect:function(title, index){
					//如果没有保存基本信息
					if(!that._saveBaseFlag){
						alert("请先保存基本信息","danger");
						return false;
					}
					this.selectedIdx=index;
					return true;
				}
			});
		},
		_loadForm : function(that) {
			//加载策略基本信息
			var that = this;
			oc.util.ajax({
				url:oc.resource.getUrl('portal/syslog/getBasicStrategy.htm'),
				data:{
					id:that._strategyId
				},
				async:false,
				successMsg:null,
				success:function(data){
					that._domainId = data.data.domainId;
					that._form.val(data.data);
					that._form.find(".createUser").text(data.data.creator);
					that._form.find(".createDate").text(formatDate(new Date(data.data.createDate)));
					if(data.data.updater != null) {
						that._form.find(".updateUser").text(data.data.updater);
					}
					if(data.data.updateDate != null) {
						that._form.find(".updateDate").text(formatDate(new Date(data.data.updateDate)));
						that._form.find("#updateDate").val(formatDate(new Date(data.data.updateDate)));
					}
					that._form.find("#createDate").val(formatDate(new Date(data.data.createDate)));
					that._form.find("#updaterId").val(oc.index.getUser().id);
				}
			});
		},
		_initMethods : {
			'基本信息' : function(that) {
				if(!that._form) {
					var domain = $.extend({},{selector:'[name=domainId]',placeholder:null},
				        that.cfg.loginUserType!=2 ? {data:oc.index.getDomains(),
							onSelect:function(data){
								that._domainId = data.id;
						}
						} : {url:oc.resource.getUrl('system/user/getLoginUserDomains.htm'),
							filter:function(data){
								return data.data;
							},
							onSelect:function(data){
								that._domainId = data.id;
							}
						});
					that._form = oc.ui.form({
						selector : that._mainDiv.find("form.tactics_form"),
						combobox :[domain]
					});
					if(that.cfg.type == "add") {
						that._form.find(".createUser").text(oc.index.getUser().account);
						that._form.find("#creatorId").val(oc.index.getUser().id);
						that._form.find(".createDate").text(formatDate(new Date()));
						that._form.find("#createDate").val(formatDate(new Date()));
//						that._form.find(".updateUser").text(oc.index.getUser().account);
//						that._form.find("#updaterId").val(oc.index.getUser().id);
//						that._form.find(".updateDate").text(formatDate(new Date()));
					}
					that._form.find(".defineBtn").first().linkbutton('RenderLB', {
						iconCls : 'fa fa-save',
						text : '保存',
						onClick : function(){
							var name = that._form.find("input[name='name']").val();
							if(name.length > 16) {
								alert("策略名称不能超过16个字!","danger");
								return;
							}
							if(that._strategyId != undefined){
								that._form.find("#updaterId").val(oc.index.getUser().id);
							}
							//如果保存成功刷新域列表
							that._saveMethods.baseInfo(that)
							if(that._saveBaseFlag) {
								if(that.cfg.callback){
									that.cfg.callback();
								}
							}
						}
					});
				}
			},
			'选择资源' : function(that) {
				if(!that._resourceForm) {
					that._resourceForm = oc.ui.form({
						selector : that._mainDiv.find('#select_resource_form'),
						combobox :[{
							selector:'[name=categoryId]',
							data:[{id:"Host",name:'主机'},{id:"NetworkDevice",name:'网络设备'},{id:"app",name:'应用'}],
							placeholder:false
						}]
					});
					that._resourceForm.find(".search_resource").linkbutton('RenderLB', {
						iconCls : 'icon-search',
						text : '查找',
						onClick : function(){
							that._searchResource();
						}
					});
					var columns = [[
	                    {field:'resourceId',title:'资源ID',checkbox:true,sortable:true},
	                    {field:'resourceIp',title:'IP地址',width:40},
	                    {field:'name',title:'资源名称',width:50},
	                    {field:'resourceType',title:'资源类型',width:50}
                    ]];
					that._pickgrid = oc.ui.pickgrid({
						selector:that._mainDiv.find('#resource_select_strategrid'),
						leftColumns:columns,
						rightColumns:columns,
						leftOptions: {
							fitColumns:true
						},
						isInteractive:true
					});
					that._mainDiv.find(".resourceOKBtn").first().linkbutton('RenderLB', {
						iconCls : 'fa fa-save',
						text : '保存',
						onClick : function(){
							that._saveMethods.saveResource(that);
						}
					});
					that._resourceForm.find("input[name='domainIdsList']").val(that._domainId);
					//初始化左侧树
					oc.util.ajax({
						  url: oc.resource.getUrl('portal/syslog/findStrategyResource.htm'),
						  //timeout:'5000',
						  data:that._resourceForm.val(),
						  success:function(data){
							  if(data.data){
								  that._pickgrid.loadData("left",{"code":200,"data":{"total":0,"rows":data.data}});
							  }else{
								  alert('查询资源实例失败!');
							  }
						  }
					  });
					//初始化右侧树
					oc.util.ajax({
						  url: oc.resource.getUrl('portal/syslog/getResourceByStrategy.htm'),
						  //timeout:'5000',
						  data:{strategyId:that._strategyId},
						  success:function(data){
							  if(data.data){
								  that._pickgrid.loadData("right",{"code":200,"data":{"total":0,"rows":data.data}});
							  }else{
								  alert('查询资源实例失败!');
							  }
						  }
					  });
				}else{
					that._searchResource();
				}
			},
			'规则定义' : function(that) {
				if(!that._datagrid){
					var toolbar = [ {
						text : '新建',
						iconCls : 'fa fa-plus',
						onClick : function() {
							_open('add',that);
						}
					}, {
						text : '删除',
						iconCls : 'fa fa-trash-o',
						onClick : function() {
							_delRule(that);
						}
					} ];
					that._datagrid = oc.ui.datagrid({
						selector :  that._mainDiv.find(".tactics_datagrid"),
						url : oc.resource.getUrl('portal/syslog/getSyslogRules.htm'),
						queryParams:{strategyId:that._strategyId},
						fit:true,
						octoolbar : {
							right : toolbar
						},
						pagination : false,
						selectedCfg:{
							field:'isChecked',
							fieldValue:1
						},
						columns : [ [{
							field : 'id',
							title:'-',
							checkbox : true,
							width : 20
						},{
							field:'isOpen',
							title:'规则状态',
							width:60,
							formatter:function(value,row,index){
								return "<span class='oc-top0 locate-left status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"'></span>";
							}
						},{
							field : 'isAlarm',
							title:'触发告警',
							width : 60,
							formatter:function(value,row,index){
								return "<span class='oc-top0 locate-left status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"'></span>";
							}
						}, {
							field : 'name',
							title : '规则名称',
							ellipsis:true,
							sortable : true,
							width : 100,
							formatter : function(value, row, index) {
								return "<span title='点我编辑详情' class='oc-pointer-operate'>" + value + "</span>";
							}
						}, {
							field : 'alarmLevel',
							title : '级别',
							sortable : true,
							width : 150,
							formatter:function(value,row,index){
					        	var select = $("<select name='alarmLevel'/>");
					        	var option1 = $("<option/>").val('CRITICAL').html('致命');
					        	var option2 = $("<option/>").val('SERIOUS').html('严重');
					        	var option3 = $("<option/>").val('WARN').html('警告');
//					        	var option4 = $("<option/>").val('UNKOWN').html('未知');
					        	if(value=="CRITICAL"){
					        		option1.attr("selected",true);
					        	}
					        	if(value=="SERIOUS"){
					        		option2.attr("selected",true);
					        	}
					        	if(value=="WARN"){
					        		option3.attr("selected",true);
					        	}
//					        	if(value=="UNKOWN"){
//					        		option4.attr("selected",true);
//					        	}
					        	select.append(option1);
					        	select.append(option2);
					        	select.append(option3);
//					        	select.append(option4);
					        	return $("<div/>").append(select).html();
							}
						}, {
							field : 'keywords',
							title : '关键字',
							width : 100,
							ellipsis:true
						},{
							field : 'logLevel',
							title : '日志级别',
							sortable : true,
							width : 300,
							formatter:function(value,row,index){
								var logLevelArr = value.split(",");
								var returnValue = [];
								var object = oc.util.getDictObj('syslog_level');
								for(var i in object) {
									for(var j in logLevelArr){
										if(object[i].code == logLevelArr[j]) {
											returnValue[j] = object[i].name;
										}
									}
								}
								return returnValue.join();
							}
						}]],
						onClickCell:function(rowIndex,field,value,e){
							var row=that._datagrid.selector.datagrid("getRows")[rowIndex];
							var status = (value+1)%2;
							if(field=='isOpen'){
								var isAlarm = row['isAlarm'];
								if(status==0) {
									isAlarm = status;
								}
								oc.util.ajax({
				     	    		url:oc.resource.getUrl('portal/syslog/updateRuleStatus.htm'),
				     	    		data:{id:row.id,isOpen:status,isAlarm:isAlarm,type:'isOpen',strategyId:row.strategyId},
				     	    		failureMsg:'操作失败！',
				     	    		success:function(data){
				     	    			if(data.code==200){
				     	    				if(that.cfg.type == "edit"){
												saveUpdater(that);
											}
				     	    				that._datagrid.selector.datagrid('reload');
				     	    			}
				     	    		}
				     	    	});
							}else if(field=='isAlarm'){
								var isOpen = row['isOpen'];
								if(isOpen == 0 && status == 1) {
									alert("规则状态关闭，不能开启！",'danger');
									return;
								}
								oc.util.ajax({
				     	    		url:oc.resource.getUrl('portal/syslog/updateRuleStatus.htm'),
				     	    		data:{id:row.id,isOpen:isOpen,isAlarm:status,type:'isAlarm',strategyId:row.strategyId},
				     	    		failureMsg:'操作失败！',
				     	    		success:function(data){
				     	    			if(data.code==200){
				     	    				if(that.cfg.type == "edit"){
												saveUpdater(that);
											}
				     	    				that._datagrid.selector.datagrid('reload');
				     	    			}
				     	    		}
				     	    	});
							}else if(field=='name'){
								that._datagrid.selector.datagrid('selectRow',rowIndex);
								//编辑规则
								_open('edit',that);
							}else if(field=="alarmLevel"){
								that._datagrid.selector.datagrid('selectRow',rowIndex);
								that.alarmRow=that._datagrid.selector.datagrid("getRows")[rowIndex];
							}
						},
						onLoadSuccess:function(data){ 
				        	 oc.ui.combobox({
				        		 selector : $("select[name='alarmLevel']"),
				        		 onSelect:function(r){
				        			 var rowId = that.alarmRow.id;
				        			 $(".alarmLevel" + rowId).val(r.id);
				        			 _apply(rowId,r.id,that);
				     			}
				        	 });
				         }
					});
				}
			},
			'告警规则' : function(that) {
				if(!that._alarmDatagrid){
					var toolbar = [ {
						text : '添加',
						iconCls : 'fa fa-plus',
						onClick : function() {
							_openAlarmRule('add',that);
						}
					}, {
						text : '删除',
						iconCls : 'fa fa-trash-o',
						onClick : function() {
							_delAlarmRule(that);
						}
					},{
						text : '应用',
						iconCls : 'fa fa-check-circle',
						onClick : function() {
							_saveAlarmRule(that);
						}
					} ];
					//callback回调
					that.dataReload = function(){
						if(that.cfg.type == "edit"){
							saveUpdater(that);
						}
						dataReloadMethod(that);
					}
					that._alarmDatagrid = oc.ui.datagrid({
						selector :  that._mainDiv.find(".alarmRuleDatagrid"),
						url : oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/profileAlarmRules.htm?profileId='+that._strategyId+'&profileType='+that.profileType+'&queryType='+that.queryType),
						fit:true,
						checkOnSelect:false,
						octoolbar : {
							right : toolbar
						},
						pagination : false,
						selectedCfg:{
							field:'isChecked',
							fieldValue:1
						},
						columns : [[{field : 'id',title:'id',checkbox : true,width : 20},
						             {field : 'userName',title:'接收人员',width : 50}, 
						             {field : 'sendWayChoice',title : '告警方式',sortable : true,width : 100,formatter:sendWayFormat}
						          ]],
			          onLoadSuccess:function(data){ 
			 	    	 $('.checkbox_disabled').on('click', function(){ 
			 	    		 $(this).attr("checked", false);
			                  alert('请先设置告警方式中相应的告警规则!');
			 	         });
			 	    	 
			 	    	that._mainDiv.find('.alarmCheckBox').on('click',function(e){
			 	    		 switch ($(e.target).attr('name')) {
			 				case 'checkboxMessage':
			 					pushIdToArr($(e.target).val(),that.editAlarmRule_Mess);
			 					break;
			 				case 'checkboxEmail':
			 					pushIdToArr($(e.target).val(),that.editAlarmRule_Email);
			 					break;
			 				case 'checkboxAlert':
			 					pushIdToArr($(e.target).val(),that.editAlarmRule_Alert);
			 					break;
			 				}
			 	    	 })
			 	    	 
			 	    	 that._mainDiv.find('.lacate-none').on('click',function(e){
			 	    		 stopProp(e);
			 	    		 var jObj = $(e.target);
			         		 var openState = 1;
			         		 var alarmLevelStr = [{content:'级别：致命',level:'down',checked:true},
		         				     			{content:'级别：严重',level:'metric_error',checked:false},
		         				    			{content:'级别：警告',level:'metric_warn',checked:false},
		         				    			{content:'级别：未知',level:'metric_unkwon',checked:false}];
			     			 oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRulesSet.js',function(){
			     				oc.resource.management.alarmrulesset.open(jObj.attr('id'),jObj.attr('name'),that.dataReload,that.profileType,alarmLevelStr,openState);
			     			 });
			 	    	 })
			 	    	 
			          }
					});
				}
			}
		},
		_searchResource:function(){
			var that = this;
			that._resourceForm.find("input[name='domainIdsList']").val(that._domainId);
			var searchValue = that._resourceForm.find("#search_resource_name").val();
			var selectValue = that._resourceForm.find("input[name='categoryId']").val();
			if("app" == selectValue) {
				that._resourceForm.find("input[name='categoryId']").val("Database,Directory,J2EEAppServer,LotusDomino,MailServer,WebServer,Middleware,StandardService");
			}
			that._resourceForm.find("input[name='name']").val(searchValue);
			var selected = that._pickgrid.getRightRows();
			var resourceIds = [];
			if(selected != undefined && selected != null) {
				for(var i = 0; i < selected.length; i++) {
					resourceIds.push(selected[i].resourceId);
				}
			}
			that._resourceForm.find("input[name='selecedResourceIds']").val(resourceIds.join());
//			if(oc.util.IPCheck(searchValue)){
//				that._resourceForm.find("input[name='resourceIp']").val(searchValue);
//			}else{
//			}
			//过滤资源实例
			console.debug(that._resourceForm.val());
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/syslog/findStrategyResource.htm'),
				  data:that._resourceForm.val(),
//				  timeout:'5000',
				  success:function(data){
					  if(data.data){
						  that._pickgrid.loadData("left",{"code":200,"data":{"total":0,"rows":data.data}});
					  }else{
						  alert('过滤资源实例失败!');
					  }
				  }
			});
		
		},
		_saveMethods : {
			'baseInfo' : function(that) {
				if(that._form.validate()){
					oc.util.ajax({
						url : oc.resource.getUrl((that.cfg.type == 'add' && that._strategyId == undefined) ? 'portal/syslog/saveSyslogStrategyBasic.htm': 'portal/syslog/updateSyslogStrategyBasic.htm'),
						data : that._form.val(),
						async:false,
						success : function(data) {
							that._strategyId = data.data.strategyId;
							that._form.find("input[name='id']").val(data.data.strategyId);
							that._domainId = that._form.find("input[name='domainId']").val();
							that._saveBaseFlag = true;
						},
						successMsg:"基本信息"+((that.cfg.type == 'add'&& that._strategyId == undefined)?"添加":"更新")+"成功"
					});
				}
			},
			'saveResource':function(that){
				var rightDatas = that._pickgrid.getRightRows();
				if(rightDatas.length <= 0) {
					alert("请从左侧选择资源","danger");
					return;
				}
				oc.util.ajax({
					url : oc.resource.getUrl('portal/syslog/saveStrategyResource.htm'),
					data : {strategyId:that._strategyId,rightDatas:rightDatas,strategyType:1},
					async:false,
					success : function(data) {
						if(that.cfg.type == "edit"){
							saveUpdater(that);
						}
					},
					successMsg:"资源添加成功"
				});
			}
		}
	};
	function _open(type, that) {
		 var id = undefined;
			if (type == 'edit') {
				id = that._datagrid.getSelectId();
			}
			oc.resource.loadScript('resource/module/resource-management/strategy/js/sysLogRuleDetail.js', function() {
				oc.strategy.syslogruledetail.open({
					type:type,
					id:id,
					strategyId:that._strategyId,
					callback : function() {
						if(that.cfg.type == "edit"){
							saveUpdater(that);
						}
						that._datagrid.reLoad();
					}
				});
			});
		}
	function _delRule(that) {
		var ids= that._datagrid.getSelectIds();
		if(ids==undefined || ids.length == 0) {
			alert("请至少选择一条规则!",'danger');
		} else {
			oc.ui.confirm('是否删除该规则！',function(){
				oc.util.ajax({
					url : oc.resource.getUrl('portal/syslog/removeRules.htm'),
					data : {ids : ids.join()},
					async:false,
					success:function(data) {
						if(data && data.data) {
							that._datagrid.reLoad();
						}
					},
					successMsg:"规则删除成功"
				});
			});
		}
	}
	function _apply(ruleId,alarmLevel,that) {
		oc.util.ajax({
			url : oc.resource.getUrl('portal/syslog/updateRuleLevel.htm'),
			data : {ruleId : ruleId,alarmLevel:alarmLevel},
			async:false,
			success:function(data) {
				if(data && data.data) {
					that._datagrid.reLoad();
					if(that.cfg.type == "edit"){
						saveUpdater(that);
					}
				}
			},
			successMsg:"级别更改成功"
		});
	}
	function sendWayFormat(value,row,rowIndex){
		var messCheckState = "";
		var emailCheckState = "";
		var alertCheckState = "";
		if(row.haveMess){
			if(true == row.messEnable){
				messCheckState = 'checked="true"';
		    }
		}else{
			messCheckState = 'class=\"checkbox_disabled\" ';
		}
		if(row.haveEmail){
			if(true == row.emailEnable){
				emailCheckState = 'checked="true"';
			}
		}else{
			emailCheckState = 'class=\"checkbox_disabled\" ';
		}
		if(row.haveAlert){
			if(true == row.alertEnable){
				alertCheckState = 'checked="true"';
			}
		}else{
			alertCheckState = 'class=\"checkbox_disabled\" ';
		}
		return '<table width="100%" class="borno"><tr width="100%"><td width="33%"><input type="checkbox" onClick="stopProp(event)" id="checkbox'+rowIndex+'" '+messCheckState+' class="alarmCheckBox" name="checkboxMessage" value="'+row.alarmRulesID+'" style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a> 短信&nbsp;&nbsp;&nbsp;&nbsp;</a><a id='+row.alarmRulesID+' style="vertical-align:middle;" name="message" class="icon-edit lacate-none"></a></td>'
		+'<td width="33%"><input type="checkbox" onClick="stopProp(event)" id="checkbox'+rowIndex+'" '+emailCheckState+' class="alarmCheckBox" name="checkboxEmail" value="'+row.alarmRulesID+'" style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a>邮件&nbsp;&nbsp;&nbsp;&nbsp;</a><a id='+row.alarmRulesID+' style="vertical-align:middle;" name="email"  class="icon-edit lacate-none"></a></td>'
		+'<td width="34%"><input type="checkbox" onClick="stopProp(event)" id="checkbox'+rowIndex+'" '+alertCheckState+' class="alarmCheckBox" name="checkboxAlert" value="'+row.alarmRulesID+'" style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a>Alert&nbsp;&nbsp;&nbsp;&nbsp;</a><a id='+row.alarmRulesID+' style="vertical-align:middle;" name="alert" class="icon-edit lacate-none"></a></td>';
	}
	function _openAlarmRule(type,that) {
			var profileId = that._strategyId;//策略ID
			var profileType = that.profileType;
			var domainId = that._domainId;
			var profileNameType = 1;
			var dataReload = that.dataReload;
			var alarmPersonData = that._alarmDatagrid.selector.datagrid('getData');
			var alarmPersonIds = new Array();
			for(var i = 0; i < alarmPersonData.rows.length; i ++){
				var row = alarmPersonData.rows[i];
				alarmPersonIds.push(row.userID);
			}
			var alarmLevelStr = [{content:'级别：致命',level:'down',checked:true},
  				     			{content:'级别：严重',level:'metric_error',checked:false},
 				    			{content:'级别：警告',level:'metric_warn',checked:false}];
// 				    			,{content:'（级别：未知）',level:'metric_unkwon',checked:false}
			oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRuleContent.js', function() {
				oc.resource.resource.management.alarmrulecontent.openAdd(profileNameType,domainId,profileId,profileType,alarmPersonIds,dataReload,alarmLevelStr);
			});
		}
	function _delAlarmRule(that) {
		var userSelected= that._alarmDatagrid.selector.datagrid('getSelections');
		if(null == userSelected || 0==userSelected.length) {
			alert("请至少选择一条告警规则!",'danger');
		} else {
			var userSelectedRuleId ='' ;
			for(var i=0;i<userSelected.length;i++){
				if(i==0){
					userSelectedRuleId = userSelected[i].alarmRulesID;
				}else{
					userSelectedRuleId+=','+userSelected[i].alarmRulesID;
				}
			}
			oc.ui.confirm('是否删除该告警规则！',function(){
				oc.util.ajax({
					url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/deleteAlarmRule.htm'),
					data:{ruleId : userSelectedRuleId},
					successMsg:null,
					success:function(data){
						dataReloadMethod(that);
					}
				});
			},function(){
			});
		}
	}
	function _saveAlarmRule(that) {
		oc.ui.confirm('是否应用当前告警规则？',function(){
			var messageRul = "";
			var enableMessageChecked = "";
			var emailRul = "";
			var enableEmailChecked = "";
			var alertRul = "";
			var enableAlertChecked = "";
			
			for(var i=0;i<that.editAlarmRule_Mess.length;i++){
				   if(0==i){
			    	   messageRul = that.editAlarmRule_Mess[i];
			       }else{
			    	   messageRul += ","+that.editAlarmRule_Mess[i];
			       }
				}
			for(var i=0;i<that.editAlarmRule_Email.length;i++){
				   if(0==i){
					   emailRul = that.editAlarmRule_Email[i];
			       }else{
			    	   emailRul += ","+that.editAlarmRule_Email[i];
			       }
				}
			for(var i=0;i<that.editAlarmRule_Alert.length;i++){
			   if(0==i){
				   alertRul = that.editAlarmRule_Alert[i];
		       }else{
		    	   alertRul += ","+that.editAlarmRule_Alert[i];
		       }
			}
			that._mainDiv.find("input:checkbox:checked").each(function(e){
				var jqObj = $(this);
				switch(jqObj.attr('name')){
				case 'checkboxMessage':
					if(""==enableMessageChecked){
			    	   enableMessageChecked = $(this).val();
			       }else{
			    	   enableMessageChecked += (","+$(this).val());
			       }
					break;
				case 'checkboxEmail':
					if(""==enableEmailChecked){
						enableEmailChecked = $(this).val();
			       }else{
			    	   enableEmailChecked += (","+$(this).val());
			       }
					break;
				case 'checkboxAlert':
					if(""==enableAlertChecked){
						enableAlertChecked = $(this).val();
			       }else{
			    	   enableAlertChecked += (","+$(this).val());
			       }
					break;
				}
			})
			oc.util.ajax({
				url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/enableAlarmCondition.htm'),
				data:{enableMessageRulList : enableMessageChecked,enableEmailRulList : enableEmailChecked,
					messageRulList : messageRul,emailRulList : emailRul,
					enableAlertRulList:enableAlertChecked,alertRulList:alertRul
				},
				successMsg:null,
				success:function(data){
					dataReloadMethod(that);
				}
			});
		  },function(){
		});
	}
	function saveUpdater(that){
		oc.util.ajax({
			url : oc.resource.getUrl('portal/syslog/updateSyslogStrategyBasic.htm'),
			data : that._form.val(),
			async:false,
			success : function(data) {},
			successMsg:null
		});
	}
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
	function dataReloadMethod(that){
		that.editAlarmRule_Mess = new Array();
		that.editAlarmRule_Email = new Array();
		that.editAlarmRule_Alert = new Array();
		that._alarmDatagrid.reLoad();
	}
	function pushIdToArr(id,array){
		if(array.length==0){
			array.push(id);
		}else{
			var flag = true;
			for(var i=0;i<array.length;i++){
				if(id==array[i]){
					flag = false;
					return ;
				}
			}
			if(flag){
				array.push(id);
			}
		}
	}
	oc.ns('oc.syslogtacticsdetail');

	oc.syslogtacticsdetail = {
		open : function(cfg) {
			new tacticsDetail(cfg).open();
		}
	};
})(jQuery);
function stopProp(event){
	event.stopPropagation();
}