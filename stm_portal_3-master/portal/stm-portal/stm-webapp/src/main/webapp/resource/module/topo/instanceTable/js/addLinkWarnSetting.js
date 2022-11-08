/**
 * 添加链路告警设置
 */
function TopoLinkWarnSetting(args){
	this.args=args;
	this.root = $("#link_warn_setting");
	this.sendedDiv = $("#sendedDiv");
};

TopoLinkWarnSetting.prototype={
	//初始化页面
	init:function(){
		this.initUi();
		this.getInitWarnData();
	},
	searchField:function(type){	//搜索域
		var ctx = this;
		this[type]={};
		this.root.find("[data-"+type+"]").each(function(idx,dom){
			var tmp = $(dom);
			ctx[type][tmp.attr("data-"+type)]=tmp;
		});
	},
	initUi:function(){
		var ctx = this;
		//搜索页面的操作按钮
		this.searchField("field");
		
		//初始化按钮
		this.field.addBtn.linkbutton("RenderLB",{//添加
			iconCls:"fa fa-plus"
		});
		this.field.delBtn.linkbutton("RenderLB",{//删除
			iconCls:"fa fa-trash-o"
		});
		//发送条件输入框
		this.field.continusBtn.numberbox({
		    min:1,
		    value:1,
		    onChange:function(newValue,oldValue){
		    	//设置当前告警发送条件
				ctx.getCurrentWarnRuleVal().condition = ctx.getCondition();
		    }
		});
		//发送条件下拉单位框
		this.field.continusUnitBtn.combobox({
			width:60,
			panelHeight:56,
			editable:false,
			valueField:'id',    
		    textField:'name',
			data:[
				{id:'minute',name:'分钟'},
				{id:'hour',name:'小时'}
	        ],
	        value:'minute',
	        onChange:function(newValue,oldValue){
		    	//设置当前告警发送条件
				ctx.getCurrentWarnRuleVal().condition = ctx.getCondition();
		    }
		});
		
		//注册事件
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this;
		//报警方式
		$("[data-type]").on("click",function(){
			$(this).addClass("active").siblings().removeClass("active");
			ctx.initWarnRule();
		});
		//添加按钮
		this.field.addBtn.on("click",function(){
			ctx.openDialog();
		});
		//删除按钮
		this.field.delBtn.on("click",function(){
			ctx.delCheckedSenders();
		});
		//告警规则
		$("[data-field='enableBtn']").change(function(){
			//设置当前告警是否启用
			ctx.getCurrentWarnRuleVal().enable = ctx.field.enableBtn.is(":checked");
            // console.log(JSON.stringify(ctx.initData));
		});
		$(".rule_div [type='checkbox']").change(function(){
			//设置当前告警规则的值
			ctx.getCurrentWarnRuleVal().rules = ctx.getWarnRules();
		});
		//发送条件
		$("[data-field='sendConditionBtn']").change(function(){
			//设置当前告警发送条件
			ctx.getCurrentWarnRuleVal().condition = ctx.getCondition();
		});
	},
	//初始化[短信、邮件、Alert]相应状态的值
	initWarnRule:function(){
		//1.获取当前告警数据
		var curWarnVal = this.getCurrentWarnRuleVal();
		
		//2.初始化已接受的人员数据
		this.appendSenders(curWarnVal.senders);
		
		//3.初始化告警规则数据
		this.intiWarnRuleVal(curWarnVal);
		
		//4.初始化发送条件
		this.initSendConditon(curWarnVal);
	},
	//初始化告警规则值
	intiWarnRuleVal:function(curWarnVal){
		var curWanrn = this.getCurrentWarnRule();
		this.field.enableBtn.prop("checked",curWarnVal.enable);
		this.field.enableBtn.next().text("启用"+curWanrn.text()+"告警");
		this.field.down.prop("checked",curWarnVal.rules.down);			//链路不可用（级别：致命）
		this.field.error.prop("checked",curWarnVal.rules.error);		//链路性能指标违反红色阈值（级别：严重）
		this.field.warn.prop("checked",curWarnVal.rules.warn);			//链路性能指标违反黄色阈值（级别：警告）
//		this.field.unknown.attr("checked",curWarnVal.rules.unknown);	//链路未知（级别：未知）
		this.field.recover.prop("checked",curWarnVal.rules.recover);	//链路性能恢复（信息）
        this.field.metricRecover.prop("checked", curWarnVal.rules.metricRecover);	//链路指标恢复正常（级别：正常）
	},
	//初始化发送条件数据
	initSendConditon:function(curWarnVal){
		var condition = curWarnVal.condition;
		this.field.sendConditionBtn.prop("checked",condition.send);
		this.field.continusBtn.numberbox("setValue",condition.continus);
		this.field.continusUnitBtn.combobox("setValue",condition.continusUnit);
	},
	//获取当前告警
	getCurrentWarnRule:function(){
		return $("[data-type].active");
	},
	//获取当前被选中的告警方式数据
	getCurrentWarnRuleVal:function(){
		var curWarn = this.getCurrentWarnRule().attr("data-type");
		var curWarnVal;
		if("sms" == curWarn){
			curWarnVal = this.smsWarn;
		}else if("email" == curWarn){
			curWarnVal = this.emailWarn;
		}else{
			curWarnVal = this.alertWarn;
		}
		return curWarnVal;
	},
	//获取发送条件
	getCondition:function(){
		var condition = {
			send:this.field.sendConditionBtn.is(":checked"),
			continus:this.field.continusBtn.numberbox("getValue"),
			continusUnit:this.field.continusUnitBtn.combobox("getValue")
		};
		return condition;
	},
	//获取告警规则
	getWarnRules:function(){
		var rules = {
			down:this.field.down.is(":checked"),
			error:this.field.error.is(":checked"),
			warn:this.field.warn.is(":checked"),
//			unknown:this.field.unknown.is(":checked"),
            recover: this.field.recover.is(":checked"),
            metricRecover: this.field.metricRecover.is(":checked")
		};
		return rules;
	},
	//初始化页面值，默认显示短信告警信息
	getInitWarnData:function(){
		//页面初始数据
		var data = {
            sms: {
                enable: false,
                condition: {send: false, continus: 1, continusUnit: 'minute'},
                senders: [],
                rules: {down: false, error: false, warn: false, unknown: false, recover: false, metricRecover: false}
            },
            email: {
                enable: false,
                condition: {send: false, continus: 1, continusUnit: 'minute'},
                senders: [],
                rules: {down: false, error: false, warn: false, unknown: false, recover: false, metricRecover: false}
            },
            alert: {
                enable: false,
                condition: {send: false, continus: 1, continusUnit: 'minute'},
                senders: [],
                rules: {down: false, error: false, warn: false, unknown: false, recover: false, metricRecover: false}
            }
		};
		//初始化值
		this.initData = {
            sms: {
                enable: false,
                condition: {send: false, continus: 1, continusUnit: 'minute'},
                senders: [],
                rules: {down: false, error: false, warn: false, unknown: false, recover: false, metricRecover: false}
            },
            email: {
                enable: false,
                condition: {send: false, continus: 1, continusUnit: 'minute'},
                senders: [],
                rules: {down: false, error: false, warn: false, unknown: false, recover: false, metricRecover: false}
            },
            alert: {
                enable: false,
                condition: {send: false, continus: 1, continusUnit: 'minute'},
                senders: [],
                rules: {down: false, error: false, warn: false, unknown: false, recover: false, metricRecover: false}
            }
		};
		
		//1.获取后台值
		this.smsWarn = data.sms;
		this.emailWarn = data.email;
		this.alertWarn = data.alert;
		
		//2.初始化页面短信用户数据
		this.initWarnRule();
	},
	//添加发送人
	appendSenders:function(senders){
		//1.删除所有接收人
		this.delAllSenders();
		
		//2.重新添加接收人
		var addSender = "";
		for(var i=0;i<senders.length;i++){
			var userId = senders[i].id;
			var userName = senders[i].name;
			addSender += '<span class="senderSpan"><label><input type="checkbox" id="'+userId+'" name="sender">'+userName+'</label></span>';
		}
		this.sendedDiv.append(addSender);
		
		//3.修改当前告警的接收人数据
		this.setWarnSenders();
	},
	//删除所有已选接收人员
	delAllSenders:function(){
		this.sendedDiv.html("");
	},
	//删除勾选的接收人员
	delCheckedSenders:function(){
		if($("input[name=sender]:checked").length == 0){
			alert("请先选择不发警告的人","danger");
		}else{
			//1.删除页面显示的接受人员
			$("input[name=sender]:checked").parent().parent("span").remove();
			
			//2.修改当前告警的接收人数据
			this.setWarnSenders();
		}
	},
	//获取页面显示的接收人
	getSelectedUsers:function(){
		var selectedUsers = new Array();
		$("input[name=sender]").each(function(){
			var sender = {"id":$(this).attr("id"),"name":$(this).parent('label').text()};
			selectedUsers.push(sender);
		});
		return selectedUsers;
	},
	//设置当前告警已选择的接收人
	setWarnSenders:function(){
		//1.获取当前页面显示的接收人
		var checkedUsers = this.getSelectedUsers();
		
		//2.修改当前告警接收人数据
		this.getCurrentWarnRuleVal().senders = checkedUsers;
	},
	//弹窗选择告警人
	openDialog:function(){
		var ctx = this;
		var topoWarnUsers = null;
		var dia = $("<div />").dialog({
			title: '选择告警人',
			href:oc.resource.getUrl("resource/module/topo/ip-mac-port/addWarnSender.html"),
			onLoad:function(){
				topoWarnUsers = oc.topo.topoWarnUsers;
				topoWarnUsers.init("linkalarm",null,ctx.getSelectedUsers());
			},
			width: 170,
		    height: 400,
		    cache: false,
		    modal: true ,
		    buttons:[{
				text:'确定',
				handler:function(){
					var selectedUsers = topoWarnUsers.getSelectedUsers(dia);	//选中的人
					ctx.appendSenders(selectedUsers);
				}
			},{
				text:'取消',
				handler:function(){
					dia.dialog("close");
				}
			}]
		})
	},
	//保存告警设置信息
	save:function(warnDatagrid,dia){
		var initData = JSON.stringify(this.initData);
		var val = JSON.stringify(this.getSettingVals());
		if(initData == val){
			alert("请添加告警设置信息再确定保存","danger");
		}else{
			oc.util.ajax({
				url : oc.resource.getUrl('topo/instanceTable/link/warn/setting/save.htm'),
				failureMsg:'保存失败',
				successMsg:"",
				data : {
					"alarmSetting":JSON.stringify(this.getSettingVals())
				},
				success : function(data) {
					alert("保存成功");
					dia.dialog("close");
					warnDatagrid.datagrid("reload");
				}
			});
		}
	},
	//获取告警设置数据
	getSettingVals:function(){
		var settingVal = {
			sms:this.smsWarn,
			email:this.emailWarn,
			alert:this.alertWarn
		};
		return settingVal;
	}
};