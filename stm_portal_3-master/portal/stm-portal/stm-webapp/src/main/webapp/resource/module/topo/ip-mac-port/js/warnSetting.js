$(function(){
/**ip-mac-port告警设置*/
function IpmacAlarmSetting(args){
	this.args=args;
	this.policyForm = null;
};
IpmacAlarmSetting.prototype = {
	//初始化页面
	init:function(){
		this.bindBtn();
		this.initWarnForm();
		this.initPolicyForm();
		this.initPageVal();
	},
	//初始化页面值
	initPageVal:function (){
		var ctx = this;
		$.post(
			oc.resource.getUrl("topo/mac/ipMacAlarmSetting.htm"),
			function(data){
				if(!data.smsSenders){
					data = {"warnLeavel":{"ip":"","upDevice":"","upDeviceInterface":""},"warnPolicy":[],"smsSenders":[],"emailSenders":[],"alertSenders":[]};
				}
				ctx.initData = data;
				var wanrnLeavel = ctx.initData.warnLeavel;
				var warnPolicy = ctx.initData.warnPolicy;
				var smsSenders = ctx.initData.smsSenders;
				
				ctx.initWarnLeavel(wanrnLeavel);
				ctx.initWarnPolicy(warnPolicy);
				ctx.appendsended(smsSenders);
			}
			,"json");
	},
	//初始化告警策略
	initWarnPolicy:function (data){
		var lastIp = $("#lastIp");
		for(var i=0;i<data.length;i++){
			if("" != data[i]){
				this.addWarnIp(lastIp,data[i]);
			}
		}
	},
	//初始化告警级别
	initWarnLeavel:function (data){
		$('#ipChange').combobox('setValue',data.ip);
		$('#deviceChange').combobox('setValue',data.upDevice);
		$('#devicePortChange').combobox('setValue',data.upDeviceInterface);
	},
	//初始化绑定按钮事件
	bindBtn:function(){
		var ctx = this;
		$("#addIp").on("click",function(){
			var lastIp = $("#lastIp");
			var lastIpVal = $.trim(lastIp.val());
			if("" == lastIpVal){
				alert("IP不能为空","danger");
			}else if(ctx.policyForm.validate() && ctx.checkSpecialIP(lastIpVal)){
				ctx.addWarnIp(lastIp,lastIpVal);
			}else{
				alert("特殊IP不允许输入："+lastIpVal,"danger");
			}
		});
		$("#delIp").on("click",function(){
			var delIpDivs = ctx.policyForm.find("input:checked").parent('div').parent('div');
			if(delIpDivs.length == 0){
				alert("至少选择一个IP","danger");
			}else{
				oc.ui.confirm('是否删除选中IP？',function() {
					delIpDivs.remove();
					$("#warnPolicyCheckboxAll").attr("checked",false);
				});
			}
		});
		$("#warnPolicyCheckboxAll").on("click",function(){
			var allCheckboxs = ctx.policyForm.find("input[type='checkbox']");
			if($(this).is(':checked')){
				allCheckboxs.attr("checked",true);
			}else{
				allCheckboxs.attr("checked",false);
			}
		});
		//告警方式
		$("[data-type]").on("click",function(){
			$(this).addClass("active").siblings().removeClass("active");
				ctx.initWarnSenders();
		});
		$("#addSender").on("click",function(){
			ctx.openWarnSenderDlg();
		});
		$("#delSender").on("click",function(){
			ctx.delSender();
		});
		$("#saveWarn").on("click",function(){
			ctx.saveWarnSetting();
		});
		//初始化按钮
		setTimeout(function(){
			$("#saveWarn").linkbutton("RenderLB");
		},0);
	},
	//特殊IP过滤
	checkSpecialIP:function(ip){
		var check = true,ips = [];
		if(ip instanceof Array){
			for(var i=0;i<ip.length;i++){
				if(ip[i] == "0.0.0.0" || ip[i]=="255.255.255.255"){
					check = false;
					ips.push(ip[i]);
				}
			}
		}else{
			if(ip == "0.0.0.0" || ip=="255.255.255.255") check = false;
		}
		
		if(!check) alert("特殊IP不允许输入："+ips,"danger");
		return check;
	},
	//初始化[短信、邮件、Alert]相应状态的发送人
	initWarnSenders:function(){
		//1.获取当前告警数据
		var senders = this.getCurrentWarnSenders();
		
		//2.初始化已接受的人员数据
		this.appendsended(senders);
	},
	//获取当前被选中的告警方式数据
	getCurrentWarnSenders:function(){
		var curWarn = this.getCurrentWarn().attr("data-type");
		var senders;
		if("sms" == curWarn){
			senders = this.initData.smsSenders;
		}else if("email" == curWarn){
			senders = this.initData.emailSenders;
		}else{
			senders =this.initData.alertSenders;
		}
		return senders;
	},
	//获取当前告警
	getCurrentWarn:function(){
		return $("[data-type].active");
	},
	//初始化warmform
	initWarnForm:function (){
		var jibieForm,jbForm = $("#warnjbForm");
		jibieForm = oc.ui.form({	//告警级别form
			selector:jbForm,
			combobox:[{
				selector:'[name="ipChange"]',
				data:[
				      {id:'1',name:'警告'},
				      {id:'2',name:'严重'},
				      {id:'3',name:'致命'}
				],
				value:"1"
			},{
				selector:'[name="deviceChange"]',
				data:[
				      {id:'1',name:'警告'},
				      {id:'2',name:'严重'},
				      {id:'3',name:'致命'}
				],
				value:"1"
			},{
				selector:'[name="devicePortChange"]',
				onChange:function(val){},
				data:[
				      {id:'1',name:'警告'},
				      {id:'2',name:'严重'},
				      {id:'3',name:'致命'}
				],
				value:"1"
			}]
		});
	},
	//初始化PolicyForm
	initPolicyForm:function (){
		var fm = $("#warnPolicyForm");
		this.policyForm = oc.ui.form({
			selector:fm
		});
	},
	//添加告警ip
	addWarnIp:function (lastIp,lastIpVal){
		var addIp = $('<div class="form-group" style="margin-top:0px;"><div style="width: 100%;"><input name="ipCheckbox" type="checkbox"/><label>IP地址:</label><input name="ip" type="text" validtype="ip" value="'+lastIpVal+'"></div></div>');
		addIp.insertBefore($("#warnPlicyFirst"));
		lastIp.val("");
		this.initPolicyForm();
		this.bindCheckbox();
	},
	//获取告警策略值
	getPolicyVal:function(){
		var policy = [];
		$("input[name=ip]").each(function(){
			if("" != $(this).val()) policy.push($(this).val());
		});
		return policy;
	},
	checkPolicyVal:function(val){
		var check = true,repeatIp = [];
		for(var i=0;i<val.length;i++){
			for(var j=i+1;j<val.length;j++){
				if(val[i] == val[j]){
					check = false;
					repeatIp.push(val[i]);
					break;
				}
			}
		}
		if(!check) alert("IP重复:"+repeatIp,"danger");
		
		return check;
	},
	//保存告警设置
	saveWarnSetting:function (){
		//告警级别
		var ipChange = $("#ipChange").combobox('getValue');
		var deviceChange = $("#deviceChange").combobox('getValue');
		var devicePortChange = $("#devicePortChange").combobox('getValue');
		
		//告警策略
		var policy = this.getPolicyVal();
		var settingVal = {
			"warnLeavel": {"ip": ipChange,"upDevice": deviceChange,"upDeviceInterface": devicePortChange},
	        "warnPolicy": policy,
	        "smsSenders": this.initData.smsSenders,
	        "emailSenders": this.initData.emailSenders,
	        "alertSenders": this.initData.alertSenders
		};
		//保存
		if(this.policyForm.validate() && this.submitCheck(settingVal)){
			oc.util.ajax({
				url : oc.resource.getUrl('topo/setting/save.htm'),
				failureMsg:'保存失败',
				successMsg:"",
				data : {
					"key" : "ipMacAlarmSetting",
					"value" : JSON.stringify(settingVal)
				},
				success : function(data) {
					alert("保存成功");
				}
			});
		}
	},
	//提交数据检查
	submitCheck:function(settingVal){
		var submit = true;
		if("" == settingVal.warnLeavel.ip && "" == settingVal.warnLeavel.upDevice && "" == settingVal.warnLeavel.upDeviceInterface){
			submit = false;
			alert("请确认告警级别","danger");
			return;
		}
		if(this.initData.smsSenders.length == 0 && this.initData.emailSenders.length == 0 && this.initData.alertSenders.length == 0){
			submit = false;
			alert("请确认告警发送人","danger");
			return;
		}
		var ipval = this.getPolicyVal();
		if(!this.checkSpecialIP(ipval)){
			submit = false;
			return;
		}
		if(!this.checkPolicyVal(ipval)){
			submit = false;
			return;
		}
		return submit;
	},
	//给新增checkbox绑定事件
	bindCheckbox:function (){
		var ctx = this;
		$("input[name=ipCheckbox]").on("click",function(){
			var allCheckboxs = ctx.policyForm.find("input[type='checkbox']");
			var totalLength = allCheckboxs.length;
			var checkedLength = $("input[name=ipCheckbox]:checked").length;
			if(totalLength == checkedLength){
				$("#warnPolicyCheckboxAll").attr("checked",true);
			}else{
				$("#warnPolicyCheckboxAll").attr("checked",false);
			}
		});
	},
	//获取已选人
	getSelectedUsers:function (){
		var senders = [];
		$("input[name=sender]").each(function(){
			var sender = {"id":$(this).attr("id"),"name":$(this).parent('label').text()};
			senders.push(sender);
		});
		return senders;
	},
	//弹出添加发送者窗口
	openWarnSenderDlg:function (){
		var ctx = this;
		var topoWarnUsers = null;
		var dia = $("<div/>").dialog({
			title: '选择告警人',
			href:oc.resource.getUrl("resource/module/topo/ip-mac-port/addWarnSender.html"),
			onLoad:function(){
				topoWarnUsers = oc.topo.topoWarnUsers;
				var curWarn = ctx.getCurrentWarn().attr("data-type");
				var type = ("sms" == curWarn)?"sms":"email";
				topoWarnUsers.init("ipmacport",type,ctx.getSelectedUsers());
			},
		    width: 170,    
		    height: 400,    
		    cache: false,    
		    modal: true ,
		    buttons:[{
				text:'确定',
				handler:function(){
					var selectedUsers = topoWarnUsers.getSelectedUsers(dia);	//选中的人
					//添加到已选列表
					ctx.appendsended(selectedUsers);
					
					//重新设置告警人
					ctx.setWarnSenders();
				}
			},{
				text:'取消',
				handler:function(){
					dia.dialog("close");
				}
			}]
		})
	},
	//设置当前告警已选择的接收人
	setWarnSenders:function(){
		//1.获取当前页面显示的接收人
		var checkedUsers = this.getSelectedUsers();
		//2.修改当前告警接收人数据
		var curWarn = this.getCurrentWarn().attr("data-type");
		if("sms" == curWarn){
			this.initData.smsSenders = checkedUsers;
		}else if("email" == curWarn){
			this.initData.emailSenders = checkedUsers;
		}else{
			this.initData.alertSenders = checkedUsers;
		}
	},
	//删除发送人
	delSender:function(){
		var ctx = this;
		if($("input[name=sender]:checked").length == 0){
			alert("请先选择不发警告的人","danger");
		}else{
			$("input[name=sender]:checked").each(function(){
				$(this).parent().parent("span").remove();
			});
			
			ctx.setWarnSenders();
		}
	},
	//添加发送人
	appendsended:function (senders){
		//删除所有接收人
		$("#sendedDiv").html("");
		
		var addSender = "";
		for(var i=0;i<senders.length;i++){
			var id = senders[i].id;
			var name = senders[i].name;
			addSender += '<span class="senderSpan"><label><input type="checkbox" id="'+id+'" name="sender">'+name+'</label></span>';
		}
		$("#sendedDiv").append(addSender);
	}
}

var ipmacAlarm = new IpmacAlarmSetting();
ipmacAlarm.init();
});