/**链路告警设置*/
function LinkAlarmRules(args){
	this.args = args;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/instanceTable/linkAlarmRules.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};

LinkAlarmRules.prototype={
	init:function(html){
		this.root = $(html);
		this.root.dialog({
			title:"链路告警列表",
			width:750,
			height:450
		});
		//搜索全局域
		this.searchField("field");
		this.initUi();
		this.regEvent();
	},
	searchField:function(type){	//搜索域
		var ctx = this;
		this.fields={};
		this.root.find("[data-"+type+"]").each(function(index,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-"+type)] = tmp;
		});
	},
	//初始化按钮
	initUi:function(){
		this.fields.addBtn.linkbutton("RenderLB",{
			text:"添加"
		});
		this.fields.delBtn.linkbutton("RenderLB",{
			text:"删除"
		});
		this.fields.useBtn.linkbutton("RenderLB",{
			text:"应用"
		});
		//初始化表格
		this.initDatagrid();
	},
	//告警规则表格
	initDatagrid:function(){
		var ctx = this;
		oc.ui.datagrid({
			selector:ctx.fields.ruleDatagrid,
			url : oc.resource.getUrl("topo/instanceTable/link/warn/setting/get.htm"),
			singleSelect:false,
			columns:[[
		        {field:"id",checkbox:true},
				{field:'userName',title:'接收人员',sortable:true,align:'left',width:'40%'},
				{field:'alarmType',title:'告警方式',align:'left',width:'59%',formatter:function(value,row,index){
					return ctx.formatAalarm(value,row,index);
				}}
	         ]],
	         onLoadSuccess:function(data){
				//数据加载完成后初始化启用告警条件变量
				ctx.enabledConditons = new Array();
			}
		});
	},
	formatAalarm:function (value,row,rowIndex){
		//1.复制告警修改内容html
		var warnDiv = this.fields.warnEdit.clone().css("display","block");
		var span = warnDiv.find("span").remove();

		//2.初始化[邮件、短信、Alert编辑html]
		var tmp = "",ctx = this;
		$(row.sendConditions).each(function(index,condition){
            var disabled = (null == condition.alarmLevels || condition.alarmLevels.length == 0);
			var alarmName = ctx.getAlarmName(condition.sendWay);
			span.find("label").text(alarmName).end()
			.find("[data-type]").attr("disabled",disabled).attr("data-type",condition.sendWay).attr("data-index",rowIndex)
			.prop("checked",condition.enabled).end()
			.clone().appendTo(warnDiv);
		});
		//warnDiv.find("[data-type]").on("click",function(event){event.stopPropagation();ctx.changeConditionEnable($(this))});
		//warnDiv.find("a").on("click",function(event){event.stopPropagation();ctx.openAlarmEditDialog($(this));});
		
		return warnDiv.get(0);
	},
	//获取告警名称
	getAlarmName:function(sendWay){
		var alarmName = "";
		switch(sendWay){
		case "sms":alarmName = "短信";break;
		case "email":alarmName = "邮件";break;
		case "alert":alarmName = "Alert";break;
		};
		return alarmName;
	},
	//绑定事件
	regEvent:function(){
		var ctx = this;
		//添加
		this.fields.addBtn.on("click",function(){
			ctx.openAddAlarmRuleDialog();
		});
		//删除
		this.fields.delBtn.on("click",function(){
			ctx.delAlarmRule();
		});
		//应用
		this.fields.useBtn.on("click",function(){
			ctx.applyEnableCondition();
		});
		var ctx = this;
		//...
		this.root.on("click",".topo_alarm_sms",function(event){
			event.stopPropagation();
			ctx.changeConditionEnable($(this));
		});
		this.root.on("click",".topo_alarm_alink",function(event){
			event.stopPropagation();
			ctx.openAlarmEditDialog($(this));
		});
	},
	//改变告警条件是否启用值
	changeConditionEnable:function(condition){
		//1.获取行数据
		var row = this.getClickRowData(condition.attr("data-index"));
		var newEnable = {ruleId:row.id,enabled:condition.is(":checked"),sendWay:condition.attr("data-type")};
		
		//2.设置需要引用的告警
		this.enabledConditons = $.grep(this.enabledConditons,function(oldEnable,index){//过滤出唯一值
			return (oldEnable.ruleId == newEnable.ruleId && oldEnable.sendWay == newEnable.sendWay);
		},true);
		this.enabledConditons.push(newEnable);
	},
	//获取点击行的数据
	getClickRowData:function(index){
		var rowData;
		var rows = this.fields.ruleDatagrid.datagrid("getRows");
		$(rows).each(function(idx,row){
			if(idx == index) rowData = row;
		});
		return rowData;
	},
	//应用告警设置信息
	applyEnableCondition:function(){
		if(this.enabledConditons.length == 0){
			alert("请修改告警发送条件后再点击【应用】按钮","danger");
			return;
		}
		
		var ctx = this;
		oc.ui.confirm('告警发送条件已修改，是否确定应用？',function() {
			oc.util.ajax({
				url:oc.resource.getUrl('topo/instanceTable/link/warn/setting/condition/enable.htm'),
				dataType: "json", 
				data:{enables:JSON.stringify(ctx.enabledConditons)},
				successMsg:null,
				success:function(data){
					alert(data.data);
					ctx.fields.ruleDatagrid.datagrid("reload");
				}
			});
		});
	},
	//删除告警设置
	delAlarmRule:function(){
		var warnDatagrid = this.fields.ruleDatagrid;
		var rows = warnDatagrid.datagrid("getSelections");
		if (rows.length == 0) {
			alert('请至少选择一条数据', 'danger');
			return;
		}
		var warnRuleIds = new Array();
		$(rows).each(function(index,row){
			warnRuleIds.push(row.id);
		});
		oc.ui.confirm('是否删除选中数据？',function() {
			oc.util.ajax({
				url:oc.resource.getUrl('topo/instanceTable/link/warn/setting/delete.htm'),
				type: 'POST',
				dataType: "json", 
				data:{ids:warnRuleIds.join(",")},
				successMsg:null,
				success:function(data){
					alert(data.data);
					warnDatagrid.datagrid("reload");
				}
			});
		});
	},
	//打开添加告警设置窗口
	openAddAlarmRuleDialog:function(){
		var ctx = this;
		var linkWarnSetting = null;
		var dia = null;
		dia= $("<div/>").dialog({
			title: '链路告警设置',
			href:oc.resource.getUrl("resource/module/topo/instanceTable/addLinkWarnSetting.html"),
			onLoad:function(){
				linkWarnSetting = new TopoLinkWarnSetting();
				linkWarnSetting.init();
			},
			width: 900,    
			height: 518,
			cache: false,    
			modal: true ,
			buttons:[{
				text:'确定',
				handler:function(){
					var warnDatagrid = ctx.fields.ruleDatagrid;
					linkWarnSetting.save(warnDatagrid,dia);
				}
			},{
				text:'取消',
				handler:function(){
					dia.dialog("close");
				}
			}]
		});
	},
	//打开编辑告警条件窗口
	openAlarmEditDialog:function(condition){
		//1.获取点击行数据
		var alarmBtn = condition.siblings("input");
		var alarmType = alarmBtn.attr("data-type");
		var alarmName = this.getAlarmName(alarmType);
		var row = this.getClickRowData(alarmBtn.attr("data-index"));

		//2.过滤出点击的告警规则值
		var curAlarmVal = $.grep(row.sendConditions,function(warn,index){
			return (warn.sendWay == alarmType);
		});
		
		//3.组装数据
        var down = error = warn = unknown = recover = metricRecover = false;
		$(curAlarmVal[0].alarmLevels).each(function(index,level){
			if(level == "down") down = true;
			if(level == "metric_error") error = true;
			if(level == "metric_warn") warn = true;
			if(level == "metric_unkwon") unknown = true;
			if(level == "metric_recover") recover = true;
            if (level == "perf_metric_recover") metricRecover = true;
		});
		var curVal = {
			ruleId:row.id,sendWay:curAlarmVal[0].sendWay,
			name:alarmName,enable:curAlarmVal[0].enabled,
			send:curAlarmVal[0].continus,continus:curAlarmVal[0].continusCount,
			continusUnit:curAlarmVal[0].continusCountUnit,
            down: down, error: error, warn: warn, unknown: unknown, recover: recover, metricRecover: metricRecover
		};
		//4.打开编辑窗口
		this.openEdit(alarmName,curVal);
	},
	openEdit:function(alarmName,initData){
		var ctx = this;
		var editSetting = null;
		var dia = null;
		dia= $("<div/>").dialog({
			title: alarmName+'设置',
			href:oc.resource.getUrl("resource/module/topo/instanceTable/editLinkWarnSetting.html"),
			onLoad:function(){
				editSetting = new EditLinkWarnSetting ();
				editSetting.init(initData);
			},
			width:750,
			height:303,
			cache: false,    
			modal: true,
			buttons:[{
				text:'确定',
				handler:function(){
					var warnDatagrid = ctx.fields.ruleDatagrid;
					editSetting.save(warnDatagrid,dia);
				}
			},{
				text:'取消',
				handler:function(){
					dia.dialog("close");
				}
			}]
		});
	}
};
//# sourceURL=linkAlarmRules.js