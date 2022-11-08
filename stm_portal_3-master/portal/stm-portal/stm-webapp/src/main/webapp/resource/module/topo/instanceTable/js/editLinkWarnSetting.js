/**
 * 编辑链路告警设置
 */
function EditLinkWarnSetting(args){
	this.args = args;
	this.root = $("#link_warn_setting");
};

EditLinkWarnSetting.prototype={
	//初始化页面
	init:function(data){
		this.initData = data;
		this.initUi();
		this.initVal(data);
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
		
		//发送条件输入框
		this.field.continusBtn.numberbox({
		    min:1,
		    value:1
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
	        value:'minute'
		});
	},
	//初始化告警规则值
	initVal:function(data){
		this.field.enableBtn.next().text("启用"+data.name+"告警");
		this.field.enableBtn.prop("checked",data.enable);
		this.field.down.prop("checked",data.down);			//链路不可用（级别：致命）
		this.field.error.prop("checked",data.error);		//链路可用性恢复（级别：严重）
		this.field.warn.prop("checked",data.warn);			//链路性能指标违反红色阈值（级别：警告）
        // this.field.unknown.prop("checked",data.unknown);	//链路性能指标违反黄色阈值（级别：未知）
		this.field.recover.prop("checked",data.recover);	//链路性能恢复（信息）
        this.field.metricRecover.prop("checked", data.metricRecover);	//链路性能恢复（信息）

		this.field.sendConditionBtn.prop("checked",data.send);
		this.field.continusBtn.numberbox("setValue",data.continus);
		this.field.continusUnitBtn.combobox("setValue",data.continusUnit);
	},
	//保存告警设置信息
	save:function(warnDatagrid,dia){
		oc.util.ajax({
			url : oc.resource.getUrl('topo/instanceTable/link/warn/setting/edit.htm'),
			type: 'POST',
			dataType: "json", 
			data : {
				"alarmSetting":JSON.stringify(this.getSettingVals())
			},
			success : function(data) {
				alert(data.data);
				dia.dialog("close");
				warnDatagrid.datagrid("reload");
			}
		});
	},
	//获取告警设置数据
	getSettingVals:function(){
		var settingVal = {
			ruleId:this.initData.ruleId,
			sendWay:this.initData.sendWay,
			enable:this.field.enableBtn.is(":checked"),
			down:this.field.down.is(":checked"),
			error:this.field.error.is(":checked"),
			warn:this.field.warn.is(":checked"),
            // unknown:this.field.unknown.is(":checked"),
			recover:this.field.recover.is(":checked"),
            metricRecover: this.field.metricRecover.is(":checked"),
			send:this.field.sendConditionBtn.is(":checked"),
			continus:this.field.continusBtn.numberbox("getValue"),
			continusUnit:this.field.continusUnitBtn.combobox("getValue")
		};
		return settingVal;
	}
};