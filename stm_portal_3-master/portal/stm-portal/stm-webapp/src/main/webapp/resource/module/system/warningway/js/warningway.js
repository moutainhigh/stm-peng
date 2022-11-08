(function($){
	var mailForm,mform=$("#EmailWarningwayForm");
	var messageForm,msgForm = $("#MessageWarningwayForm");
	var TimeForm,tForm = $("#TimeWarningwayForm");
	/**初始化邮件告警设置表单*/
	mailForm = oc.ui.form({
		selector:mform
	});
	/**初始化告警抑制期设置表单**/
	TimeForm = oc.ui.form({
		selector:tForm
	});
	/**初始化短信告警设置表单*/
	messageForm = oc.ui.form({
		selector:msgForm,
		combobox:[{
			selector:'[name=messageSendType]',
			dictType:'message_send_type',
			placeholder:null,
			selected:false,
	        onChange:function(val){
	        	if(val=="SMSModem"){
	        		checkedSMSModem();
	        	}else if(val=="SMSGateway"){
	        		checkedGateway();
	        	}
	        }
		},{
			selector:'[name=messageType]',
			dictType:'SMSModem',
			selected:false,
			placeholder:null,
		}]
	});
	
	//加载邮件告警表单
	function loadEmailForm(){
		oc.util.ajax({
			url:oc.resource.getUrl("system/alarm/getAlarmSetting.htm"),
			async:false,
			success:function(data){
				var sinfo = data.data;
				if(!sinfo || !sinfo.emailPort){
					sinfo.emailPort=25;
				}
				mailForm.val(sinfo);
			},
			successMsg:""
		});
		//绑定邮件测试按钮事件
		mform.find("+p #testEmail").linkbutton('RenderLB', {
			iconCls : 'icon-filter',
			onClick : function(){
				if(mailForm.validate()){
					if($.trim(mform.find("input[name=emailTestEmail]:first").val())!=null && $.trim(mform.find("input[name=emailTestEmail]:first").val())!=""){
						oc.util.ajax({
							url:oc.resource.getUrl("system/alarm/testSendEmail.htm"),
							data:mailForm.val(),
							successMsg:'',
							errorMsg:'',
							failureMsg:'',
							success:function(data){
								if(data.code==200){
									data.data?alert("测试邮件发送成功"):alert("测试邮件发送失败");
								}
							},
							error:function(XMLHttpRequest, textStatus, errorThrown){
								$.messager.progress('close');
								if(textStatus=="timeout"){
									alert("邮件服务器连接超时，请检查SMTP服务器及端口是否设置正确！");
								}
							}
						});
					}else{
						alert("测试邮件地址不能为空！");
					}
				}
			}
		});
		//绑定邮件设置保存按钮事件
		mform.find("+p #saveEmail").linkbutton('RenderLB', {
			iconCls : 'fa fa-check-circle',
			onClick : function(){
				if(mailForm.validate()){
					oc.util.ajax({
						url:oc.resource.getUrl("system/alarm/emailAlarmSetting.htm"),
						data:mailForm.val(),
						successMsg:"邮件告警设置成功！"
					});
				}
			}
		});
	};
	
	//加载短信告警设置表单
	function loadMessageForm(){
		oc.util.ajax({
			url:oc.resource.getUrl("system/alarm/getAlarmSetting.htm"),
			success:function(data){
				var sinfo = data.data;
				if(!sinfo || !sinfo.clientPort){
					sinfo.clientPort=9050;
				}
				messageForm.val(sinfo);
				sinfo.messageSendType=="SMSGateway"?checkedGateway(sinfo.messageType):checkedSMSModem();
			},
			successMsg:""
		});
		
		msgForm.find("+p #testMessage").linkbutton('RenderLB', {
			iconCls : 'icon-filter',
			onClick : function(){
				if(messageForm.validate()){
					if($.trim(msgForm.find("input[name=messageTestPhone]:first").val())!=null && $.trim(msgForm.find("input[name=messageTestPhone]:first").val())!=""){
						oc.util.ajax({
							url:oc.resource.getUrl("system/alarm/testSendMessage.htm"),
							data: messageForm.val(),
							errorMsg:null,
							failureMsg:null,
							successMsg:null,
							success:function(data){
								if(data.code ==200){
									if(data.data){
										alert("测试短信提交到发送队列成功");
									}else{
										alert("测试短信发送失败");
									}
								}else if(data.code==550){
									alert(data.data);
								}
							}
						});
					}else{
						alert("测试手机号码不能为空！");
					}
				}
			}
		});
		msgForm.find("+p #saveMessage").linkbutton('RenderLB', {
			iconCls : 'fa fa-check-circle',
			onClick : function(){
				if(messageForm.validate()){
					oc.util.ajax({
						url:oc.resource.getUrl("system/alarm/messageAlarmSetting.htm"),
						data:messageForm.val(),
						successMsg:"短信告警设置成功！"
					});
				}
			}
		});
	};
	
	
	function loadWarnTime(){
		oc.util.ajax({
			url:oc.resource.getUrl("system/alarm/getAlarmSetting.htm"),
			async:false,
			success:function(data){
				var sinfo = data.data;
				if(sinfo.time==null){
					sinfo.time=60;
				}
				TimeForm.val(sinfo);
			},
			successMsg:""
		});
		tForm.find("+p #saveTime").linkbutton('RenderLB', {
			iconCls : 'fa fa-check-circle',
			onClick : function(){
				if(TimeForm.validate()){
					oc.util.ajax({
						url:oc.resource.getUrl("system/alarm/alarmWanrnSetting.htm"),
						data:TimeForm.val(),
						successMsg:"告警抑制期设置成功！"
					});
				}
			}
		});
	};
	function loadBtn(){
		var user = oc.index.getUser();
		if(user.domainUser || user.systemUser){
			
		}else{
			tForm.find("+p #saveTime").hide();
		}
	};
	//设置短信表单为短信猫
	function checkedSMSModem(){
		msgForm.find('#messageSendType').combobox('setValue',"SMSModem");
		msgForm.find(".smsmodem").show();
		msgForm.find(".gateway").hide();
		msgForm.find(".gateway input").attr("disabled",true);
		msgForm.find(".smsmodem input").attr("disabled",false);
		messageForm.disableValidation(["messageGatewayIp","messagePort","messageLoginAccount","messagePassword"]);
		messageForm.enableValidation(["clientIp","clientPort"]);
	};
	//设置短信表单为短信网关
	function checkedGateway(msgtype){
        msgForm.find('#messageSendType').combobox('setValue',"SMSGateway");
        oc.util.getDict('SMSGateway',function(data){
        	msgForm.find('#messageType').combobox('loadData',data);
        	msgForm.find('#messageType').combobox('setValue',msgtype?msgtype:"CMPP");
        });
		msgForm.find(".gateway").show();
		msgForm.find(".smsmodem").hide();
		msgForm.find(".smsmodem input").attr("disabled",true);
		msgForm.find(".gateway input").attr("disabled",false);
		messageForm.disableValidation(["clientIp","clientPort"]);
		messageForm.enableValidation(["messageGatewayIp","messagePort","messageLoginAccount","messagePassword"]);
		messageForm.find("input[name=messageGatewayIp]").focus().blur();
	};
	loadEmailForm();loadMessageForm();loadWarnTime();loadBtn();
})(jQuery);
