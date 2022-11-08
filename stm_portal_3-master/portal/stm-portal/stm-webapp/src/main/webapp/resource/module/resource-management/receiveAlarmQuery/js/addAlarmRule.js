(function($){
	oc.ns('oc.resource.resource.management.alarmrulecontent');
	var alarmRuleContent ;
	var openType,profileNameType,domainId,profileId,profileType,alarmPersonIds,callback,alarmLevelStr,hiddenSendTimeSet,clpproType;
	var myForm,form;
	var myAlarmEditor;
	//当前选择的发送方式1:短信;2:邮件;3:alert;
	var currentSendWay = 1;
	//第一次点击自定义,才对自定义内的控件进行渲染提高弹出框打开时间
	var autoRenderFlag = true;
	var alarmLevelArrTemp = new Array();
	var userIdArr = new Array();
	
	//存放发送时间数据  {type:'everyDay'/'everyWeek',dayOfWeek:1,startTimeNum:1,endTimeNum:2};   alarmSendTimeType={'allTime','everyDay','everyWeek'}
	var alarmSendTimeSetArr = new Array();
	
	var sendContentUser = {receiveUser:userIdArr,receiveUserTextarea:""};
	var sendContentSMS = {receiveUser:"",receiveUserTextarea:"",isSmsAlarm:"checked",alarmLevel:alarmLevelArrTemp,sendTimeSet:"",sendTimeNum:0,templateId:0,sendTimeType:"minute",alarmSendTimeType:'allTime',ifSendUnalarmTimeAlarm:'unChecked',alarmForDay:alarmSendTimeSetArr,alarmForWeek:alarmSendTimeSetArr};
	var sendContentEMAIL = {receiveUser:"",receiveUserTextarea:"",isSmsAlarm:"checked",alarmLevel:alarmLevelArrTemp,sendTimeSet:"",sendTimeNum:0,templateId:0,sendTimeType:"minute",alarmSendTimeType:'allTime',ifSendUnalarmTimeAlarm:'unChecked',alarmForDay:alarmSendTimeSetArr,alarmForWeek:alarmSendTimeSetArr};
	var sendContentALERT = {receiveUser:"",receiveUserTextarea:"",isSmsAlarm:"checked",alarmLevel:alarmLevelArrTemp,sendTimeSet:"",sendTimeNum:0,templateId:0,sendTimeType:"minute",alarmSendTimeType:'allTime',ifSendUnalarmTimeAlarm:'unChecked',alarmForDay:alarmSendTimeSetArr,alarmForWeek:alarmSendTimeSetArr};
	
	function init(){
		
		var dlg = $('<div/>');
		dlg.dialog({
		    title: '告警规则',
		    width : 900,
			height : 630,
			buttons:[{
		    	text: '保存',
				iconCls:"fa fa-save",
		    	handler:function(){
		    		//点击保存前,设置一次值
					switch (currentSendWay) {
					case 1:
						//设置sendContentSMS
						if(!setFormValue(form,sendContentSMS,1)){
							return false;
						}
						break;
					case 2:
						if(!setFormValue(form,sendContentEMAIL,2)){
							return false;
						}
						break;
					case 3:
						if(!setFormValue(form,sendContentALERT,3)){
							return false;
						}
						break;
					}
					
					if(sendContentUser.receiveUser==""||null==sendContentUser.receiveUser){
						alert('请选择告警接收人！');
						return false;
					}
		    		if(sendContentSMS.isSmsAlarm == "checked" && (undefined == sendContentSMS.alarmLevel || null == sendContentSMS.alarmLevel || 0 == sendContentSMS.alarmLevel.length)){
		    			alert('请选择短信告警规则级别！');
						return false;
		    		}
		    		if(sendContentEMAIL.isSmsAlarm == "checked" && (undefined == sendContentEMAIL.alarmLevel || null == sendContentEMAIL.alarmLevel || 0 == sendContentEMAIL.alarmLevel.length)){
		    			alert('请选择邮件告警规则级别！');
						return false;
		    		}
		    		if(sendContentALERT.isSmsAlarm == "checked" && (undefined == sendContentALERT.alarmLevel || null == sendContentALERT.alarmLevel || 0 == sendContentALERT.alarmLevel.length)){
		    			alert('请选择Alert告警规则级别！');
						return false;
		    		}
		    		
		    		if(undefined!=sendContentSMS.sendTimeSet && "checked"==sendContentSMS.sendTimeSet){
		    			if(0<sendContentSMS.sendTimeNum){
		    				if(sendContentSMS.sendTimeNum>10000){
		    					alert('短信告警发送条件的时间间隔过大!');
			    				return false;
		    				}
		    			}else{
		    				alert('请确认短信告警发送条件的时间间隔!');
		    				return false;
		    			}
		    			//短信告警最多发送次数数据验证 dfw 20161227
		    			if(0<sendContentSMS.sendTimes){
		    				if(sendContentSMS.sendTimes>10000){
		    					alert('短信告警最多发送次数过大!');
			    				return false;
		    				}
		    			}
		    		}
		    		if(undefined!=sendContentEMAIL.sendTimeSet && "checked"==sendContentEMAIL.sendTimeSet){
		    			if(0<sendContentEMAIL.sendTimeNum){
		    				if(sendContentEMAIL.sendTimeNum>10000){
		    					alert('邮件告警发送条件的时间间隔过大!');
			    				return false;
		    				}
		    			}else {
		    				alert('请确认邮件告警发送条件的时间间隔!');
		    				return false;
		    			}
                        //邮件告警最多发送次数数据验证 dfw 20161227
                        if(0<sendContentEMAIL.sendTimes){
                            if(sendContentEMAIL.sendTimes>10000){
                                alert('邮件告警最多发送次数过大!');
                                return false;
                            }
                        }
		    		}
		    		if(undefined!=sendContentALERT.sendTimeSet && "checked"==sendContentALERT.sendTimeSet){
		    			if(0<sendContentALERT.sendTimeNum){
		    				if(sendContentALERT.sendTimeNum>10000){
		    					alert('Alert告警发送条件的时间间隔过大!');
			    				return false;
		    				}
		    			}else{
		    				alert('请确认Alert告警发送条件的时间间隔!');
		    				return false;
		    			}
		    		}
		    		
					
		    		var alarmLevel_SMS = formatArray(sendContentSMS.alarmLevel);
		    		var alarmLevel_EMAIL = formatArray(sendContentEMAIL.alarmLevel);
	    			var alarmLevel_ALERT = formatArray(sendContentALERT.alarmLevel);
		    		var receiveUser = formatArray(sendContentUser.receiveUser);
		    		
		    		var alarmSendTimeTypeSms = sendContentSMS.alarmSendTimeType;
	    			var alarmSendTimeTypeEmail = sendContentEMAIL.alarmSendTimeType;
    				var alarmSendTimeTypeAlert = sendContentALERT.alarmSendTimeType;
    				var ifSendUnalarmTimeAlarmSms = sendContentSMS.ifSendUnalarmTimeAlarm;
					var ifSendUnalarmTimeAlarmEmail = sendContentEMAIL.ifSendUnalarmTimeAlarm;
					var ifSendUnalarmTimeAlarmAlert = sendContentALERT.ifSendUnalarmTimeAlarm;
					var templateIdSms=sendContentSMS.templateId;
					var templateIdEmail=sendContentEMAIL.templateId;
					var templateIdAlert=sendContentALERT.templateId;
    				var alarmForDayDataSms = '';
    				var alarmForDayDataEmail = '';
    				var alarmForDayDataAlert = '';
    				
    				var alarmForWeekDataSms = '';
    				var alarmForWeekDataEmail = '';
    				var alarmForWeekDataAlert = '';

    				for(var q=0;q<sendContentSMS.alarmForDay.length;q++){
    					if(''==alarmForDayDataSms){
    						alarmForDayDataSms = sendContentSMS.alarmForDay[q];
    					}else{
    						alarmForDayDataSms += ','+sendContentSMS.alarmForDay[q];
    					}
    				}
    				for(var w=0;w<sendContentEMAIL.alarmForDay.length;w++){
    					if(''==alarmForDayDataEmail){
    						alarmForDayDataEmail = sendContentEMAIL.alarmForDay[w];
    					}else{
    						alarmForDayDataEmail += ','+sendContentEMAIL.alarmForDay[w];
    					}
    				}
    				for(var e=0;e<sendContentALERT.alarmForDay.length;e++){
    					if(''==alarmForDayDataAlert){
    						alarmForDayDataAlert = sendContentALERT.alarmForDay[e];
    					}else{
    						alarmForDayDataAlert += ','+sendContentALERT.alarmForDay[e];
    					}
    				}
    				
    				for(var i=0;i<sendContentSMS.alarmForWeek.length;i++){
    					if(''==alarmForWeekDataSms){
    						alarmForWeekDataSms = sendContentSMS.alarmForWeek[i];
    					}else{
    						alarmForWeekDataSms += ','+sendContentSMS.alarmForWeek[i];
    					}
    				}
    				for(var x=0;x<sendContentEMAIL.alarmForWeek.length;x++){
    					if(''==alarmForWeekDataEmail){
    						alarmForWeekDataEmail = sendContentEMAIL.alarmForWeek[x];
    					}else{
    						alarmForWeekDataEmail += ','+sendContentEMAIL.alarmForWeek[x];
    					}
    				}
					for(var z=0;z<sendContentALERT.alarmForWeek.length;z++){
						if(''==alarmForWeekDataAlert){
							alarmForWeekDataAlert = sendContentALERT.alarmForWeek[z];
    					}else{
    						alarmForWeekDataAlert += ','+sendContentALERT.alarmForWeek[z];
    					}
					}
    				if(myForm.validate()){
    					oc.util.ajax({
    		    			successMsg:null,
    		    			url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/addAlarmRuleContent.htm'),
							//增加短信和邮件告警的最多发送次数参数 dfw 20170104
    		    			data:{receiveUser:receiveUser,profileTypeId:profileId,profileType:profileType,
    		    				isSmsAlarm_SMS:sendContentSMS.isSmsAlarm,alarmLevel_SMS:alarmLevel_SMS,sendTimeSet_SMS:sendContentSMS.sendTimeSet,sendTimeNum_SMS:sendContentSMS.sendTimeNum,sendTimeType_SMS:sendContentSMS.sendTimeType,
    		    				isSmsAlarm_EMAIL:sendContentEMAIL.isSmsAlarm,alarmLevel_EMAIL:alarmLevel_EMAIL,sendTimeSet_EMAIL:sendContentEMAIL.sendTimeSet,sendTimeNum_EMAIL:sendContentEMAIL.sendTimeNum,sendTimeType_EMAIL:sendContentEMAIL.sendTimeType,
    		    				isSmsAlarm_ALERT:sendContentALERT.isSmsAlarm,alarmLevel_ALERT:alarmLevel_ALERT,sendTimeSet_ALERT:sendContentALERT.sendTimeSet,sendTimeNum_ALERT:sendContentALERT.sendTimeNum,sendTimeType_ALERT:sendContentALERT.sendTimeType,
    		    				alarmSendTimeTypeSms:alarmSendTimeTypeSms,alarmSendTimeTypeEmail:alarmSendTimeTypeEmail,alarmSendTimeTypeAlert:alarmSendTimeTypeAlert,ifSendUnalarmTimeAlarmSms:ifSendUnalarmTimeAlarmSms,ifSendUnalarmTimeAlarmEmail:ifSendUnalarmTimeAlarmEmail,
    		    				ifSendUnalarmTimeAlarmAlert:ifSendUnalarmTimeAlarmAlert,alarmForDayDataSms:alarmForDayDataSms,alarmForDayDataEmail:alarmForDayDataEmail,alarmForDayDataAlert:alarmForDayDataAlert,alarmForWeekDataSms:alarmForWeekDataSms,
    		    				alarmForWeekDataEmail:alarmForWeekDataEmail,alarmForWeekDataAlert:alarmForWeekDataAlert,templateIdSms:sendContentSMS.templateId,templateIdEmail:sendContentEMAIL.templateId,templateIdAlert:0,sendTimes_SMS:sendContentSMS.sendTimes,sendTimes_EMAIL:sendContentEMAIL.sendTimes
    		    			},
    		    			success:function(data){
    		    				pageReset();
    		    				callback();
    		    				dlg.dialog('destroy');
    		    			}
    		    		})
    				}
		    	}
		    },{
		    	text: '取消',
				iconCls:"fa fa-times-circle",
		    	handler:function(){
		    		pageReset();
		    		dlg.dialog('destroy');
		    	}
		    }],
		    onClose:function(){
		    	pageReset();
	    		dlg.dialog('destroy');
		    },
		    href: oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRuleContent.html'),
		    onLoad:function(){
		    	_initPage(dlg);
		    
		    }
		});
	}
	function UMinstance(){
		UM.delEditor("myalarmContentEditor");
		myAlarmEditor = UM.getEditor('myalarmContentEditor');
		 UM.getEditor('myalarmContentEditor').setDisabled('fullscreen');
		 disableBtn("enable");
		$(".edui-container #myalarmContentEditor").width("100%");
//		$(".edui-container .edui-toolbar").hide();
		

	}
	 function disableBtn(str) {
	        var div = document.getElementById('btns');
	        var btns = domUtils.getElementsByTagName(div, "button");
	        for (var i = 0, btn; btn = btns[i++];) {
	            if (btn.id == str) {
	                domUtils.removeAttributes(btn, ["disabled"]);
	            } else {
	                btn.setAttribute("disabled", "true");
	            }
	        }
	    }
	function formatArray(arr){
		var arrStr;
		if(null!=arr&&""!=arr&&0<arr.length){
			for(var i=0;i<arr.length;i++){
				if(i==0){
					arrStr = arr[i];
				}else{
					arrStr += ','+arr[i];
				}
			}
		}else{
			return "";
		}
		return arrStr;
	}
	function _initPage(dlg){
		alarmRuleContent = $('#alarmRuleContent');
	var alarmRuleContentForm = $('<form/>').attr('name','alarmRuleContentForm').attr('class','alarmRuleContentForm').css('height','100%').css('width','100%').css('overflow-y','auto');
	var alarmRuleContentTable = $('<table/>').css('height','100%').css('width','100%').addClass('table-pop').attr('cellspacing','0').attr('cellpadding','0').attr('border','0');
	var alarmRuleContentTableTr1 = $('<tr/>').css('height','8%');
	var alarmRuleContentTableTr2 = $('<tr/>').css('height','20%');
	var alarmRuleContentTableTr3 = $('<tr/>').css('height','20%');
	var alarmRuleContentTableTr4 = $('<tr/>').css('height','8%');
	
	if(hiddenSendTimeSet){
		alarmRuleContentTableTr4.css('display','none');
	}
	
	var alarmRuleContentTableTr5 = $('<tr/>').css('height','10%');
	var alarmRuleContentTableTr1Td1 = $('<td/>').html('告警方式:').addClass('tab-l-tittle').attr('style','line-height:30px;');
	var alarmRuleContentTableTr1Td2 = $('<td/>').css('height','45px').html('<div class="oc-open"  ><a type="1" class="oc-message active" name="alarmSendWay">短信</a><a type="2" class="oc-email" name="alarmSendWay">邮件</a><a type="3" class="oc-alert" name="alarmSendWay">Alert</a></div>');
	var alarmRuleContentTableTr2Td1 = $('<td/>').html('接收人员:').addClass('tab-l-tittle');
	var alarmRuleContentTableTr2Td2 = $('<td/>').attr('valign','middle').html(
			'<div class="margin5"><span class="fa fa-info-circle yellow"></span><span class="yellow">资源的告警只能由该资源所在域的用户及系统管理员接收。<span></p></div>'+
			'<div style="height:65%;"><textarea disabled = "true" name="textareaPerson" style="resize: none;margin: 0px 2px; height: 50px; width: 60%;"></textarea><a name="addPerson" title="选择告警接收人!" class="ico ico-change" style="float: none;"></a></div>');
	
	
	var alarmRuleContentTableTr3Td1 = $('<td/>').html('告警规则:').addClass('tab-l-tittle').attr('valign','middle');
	
	var htmlStr = '<input type="checkbox" value="" name="isSmsAlarm" checked=true style="vertical-align:middle;" ><label name="currentSendWayNameTitle">启用短信告警</label><br><br>'; 
//	if(profileType != 'netFlow'){
//		htmlStr = '<input type="checkbox" value="" name="isSmsAlarm" checked=true style="vertical-align:middle;" ><label name="currentSendWayNameTitle">启用短信告警</label><br><br>'+
//		'<input type="checkbox" value="1" name="level" checked=true style="vertical-align:middle;"><label>'+alarmLevelStr.one+'</label><br>'+
//		'<input type="checkbox" value="2" name="level" style="vertical-align:middle;"><label>'+alarmLevelStr.two+'</label><br>'+
//		'<input type="checkbox" value="3" name="level" style="vertical-align:middle;"><label>'+alarmLevelStr.three+'</label><br>'+
//		'<input type="checkbox" value="4" name="level" style="vertical-align:middle;"><label>'+alarmLevelStr.four+'</label><br>'+
//		'<input type="checkbox" value="5" name="level" style="vertical-align:middle;"><label>'+alarmLevelStr.five+'</label><br>'
//	}else{
//		htmlStr = '<input type="checkbox" value="" name="isSmsAlarm" checked=true style="vertical-align:middle;" ><label name="currentSendWayNameTitle">启用短信告警</label><br><br>'+
//		'<input type="checkbox" value="1" name="level" checked=true style="vertical-align:middle;"><label>'+alarmLevelStr.one+'</label><br>'+
//		'<input type="checkbox" value="2" name="level" style="vertical-align:middle;"><label>'+alarmLevelStr.two+'</label><br>'
//	}
	for(var x=0;x<alarmLevelStr.length;x++){
		if(alarmLevelStr[x].checked){
			htmlStr += '<input type="checkbox" value="'+alarmLevelStr[x].level+'" name="level" checked=true style="vertical-align:middle;"><label>'+alarmLevelStr[x].content+'</label><br>';
		}else{
			htmlStr += '<input type="checkbox" value="'+alarmLevelStr[x].level+'" name="level" style="vertical-align:middle;"><label>'+alarmLevelStr[x].content+'</label><br>';
		}
	}
	
	var alarmRuleContentTableTr3Td2 = $('<td/>').attr('valign','middle').html(htmlStr);
	
	var alarmRuleContentTableTr4Td1=$('<td/>').html('邮件内容:').addClass('tab-l-tittle').addClass('notifycontent');
	var alarmRuleContentTableTr4Td2=$('<td/>').attr('valign','middle').html(
			'<div style="width: 100%;" >'+
	'<div style="display:block" id="sel1">选择模板：<select  name="mailRulechoice" class="mailRulechoice" ></select></div>'+
	'<div style="display:none" id="sel2">选择模板：<select  name="mailRulechoice2" class="mailRulechoice2" ></select></div>'
			+	'<div id="myalarmContentEditor" class="umfontSize" name="content" style="height:100px;max-height: 100px;overflow-y:auto; "></div>'
+'</div>'
	);
	
	var alarmRuleContentTableTr5Td1 = $('<td/>').html('发送条件:').addClass('tab-l-tittle');
	
	var alarmRuleContentTableTr5Td2 = $('<td/>').attr('valign','middle').html(
			'<label>发送条件：</label>'+
			'<input type="checkbox" value="sendCondition" name="sendCondition" style="vertical-align:middle;">'+
			'<label>同一条告警间隔&nbsp;</label><input type="text" validType="[\'int\',\'maxLength[6]\']"  name="continusCount" class="continusCount" style="margin-right:5px">'+
			'<select name="localDatatest" class="localData" ></select>'+
			//增加最多发送次数输入框 dfw 20161227
			'<label>&nbsp;连续发送，<span id="sendTimes">最多发送&nbsp;<input type="text" validType="[\'int\',\'maxLength[6]\']"  name="sendTimes" class="sendTimes">&nbsp;次，</span>直到系统确认告警被恢复</label>'
	);
	var alarmRuleContentTableTr6 = $('<tr/>').css('height','34%');
	var alarmRuleContentTableTr6Td1 = $('<td/>').html('发送告警时间:').addClass('tab-l-tittle');
	var alarmRuleContentTableTr6Td2 = $('<td/>');
	
	var alarmRuleContentTableTr6Td2DivSms = $('<div/>').attr('type','sms').attr('name','alarmRuleContentAlarmTimeSetDiv').css('width','100%').css('height','100%');
	var alarmRuleContentTableTr6Td2DivEmail = $('<div/>').attr('type','email').attr('name','alarmRuleContentAlarmTimeSetDiv').css('width','100%').css('height','100%').css('display','none');
	var alarmRuleContentTableTr6Td2DivAlert = $('<div/>').attr('type','alert').attr('name','alarmRuleContentAlarmTimeSetDiv').css('width','100%').css('height','100%').css('display','none');
	
	var alarmRuleContentTableTr6Td2DivSmsSendTimeSet = $('<div/>').css('height','20px').html('<input type="radio" checked=true value=1 name="sendAlarmSmsTimeSet" style="vertical-align:middle;">24小时*7天&nbsp;&nbsp;</input><input type="radio" value=2 name="sendAlarmSmsTimeSet" style="vertical-align:middle;">自定义</input>');
	var alarmRuleContentTableTr6Td2DivEmailSendTimeSet = $('<div/>').css('height','20px').html('<input type="radio" checked=true value=1 name="sendAlarmEmailTimeSet" style="vertical-align:middle;">24小时*7天&nbsp;&nbsp;</input><input type="radio" value=2 name="sendAlarmEmailTimeSet" style="vertical-align:middle;">自定义</input>');
	var alarmRuleContentTableTr6Td2DivAlertSendTimeSet = $('<div/>').css('height','20px').html('<input type="radio" checked=true value=1 name="sendAlarmAlertTimeSet" style="vertical-align:middle;">24小时*7天&nbsp;&nbsp;</input><input type="radio" disabled="true" value=2 name="sendAlarmAlertTimeSet" style="vertical-align:middle;">自定义</input>');
	var alarmRuleContentTableTr6Td2DivSmsRandomTime = $('<div/>').css('width','60%').css('height','145px');
	var alarmRuleContentTableTr6Td2DivEmailRandomTime = $('<div/>').css('width','60%').css('height','80%');
	var alarmRuleContentTableTr6Td2DivAlertRandomTime = $('<div/>').css('width','60%').css('height','80%');
	var alarmRuleContentTableTr6Td2DivSmsRandomTimeSet = $('<div/>').attr('name','sendAlarmTimeSetCustom').attr('class','sendAlarmTimeSetCustom').css('width','100%').css('height','100%').css('display','none').html('<table><tr><td><select id="sendAlarmTimeRangeType" name="sendAlarmTimeRangeType"></select></td><td><input type="checkbox" name="sendUnAlarmTimeAlarmCheckbox" style="vertical-align:middle;">发送非告警时间段产生的告警</td><td><a name="addAlarmSendTimeWeekAndDay" style="float:right;" class="r-h-ico r-h-add"></a></td></tr></table>');
	var alarmRuleContentTableTr6Td2DivEmailRandomTimeSet = $('<div/>').attr('name','sendAlarmTimeSetCustom').attr('class','sendAlarmTimeSetCustom').css('width','100%').css('height','100%').css('display','none').html('<table><tr><td><select id="sendAlarmTimeRangeType" name="sendAlarmTimeRangeType"></select></td><td><input type="checkbox" name="sendUnAlarmTimeAlarmCheckbox" style="vertical-align:middle;">发送非告警时间段产生的告警</td><td><a name="addAlarmSendTimeWeekAndDay" style="float:right;" class="r-h-ico r-h-add"></a></td></tr></table>');
	var alarmRuleContentTableTr6Td2DivAlertRandomTimeSet = $('<div/>').attr('name','sendAlarmTimeSetCustom').attr('class','sendAlarmTimeSetCustom').css('width','100%').css('height','100%').css('display','none').html('<table><tr><td><select id="sendAlarmTimeRangeType" name="sendAlarmTimeRangeType"></select></td><td><input type="checkbox" name="sendUnAlarmTimeAlarmCheckbox" style="vertical-align:middle;">发送非告警时间段产生的告警</td><td><a name="addAlarmSendTimeWeekAndDay" style="float:right;" class="r-h-ico r-h-add"></a></td></tr></table>');
	
	var alarmRuleContentTableTr6Td2DivSmsRandomTimeSetDayContent = $('<div/>').attr('style','overflow-X:hidden;overflow-Y:auto;').css('width','100%').css('height','105px').attr('name','sendAlarmTimeSetCustomInsideDay')
	.html(
			""
//			'<div name="sendAlarmTimeSetCustomInsideDayChildren"><form>'
//			+'<table style="width:85%;"><tr><td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
//			+'<td align="center">:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
//			+'<td align="center">至</td>'
//			+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
//			+'<td align="center">:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
//			+'<td><a name="delAlarmTimeSetWeek" title="删除!" style="vertical-align:middle;"  class="r-h-ico r-h-delete"></a></td></tr></table></form></div>'
	);
	var alarmRuleContentTableTr6Td2DivEmailRandomTimeSetDayContent = $('<div/>').attr('style','overflow-X:hidden;overflow-Y:auto;').css('width','100%').css('height','105px').attr('name','sendAlarmTimeSetCustomInsideDay').html(
			""
//			'<div name="sendAlarmTimeSetCustomInsideDayChildren"><form>'
//			+'<table style="width:85%;"><tr><td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
//			+'<td>:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
//			+'<td>至</td>'
//			+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
//			+'<td>:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
//			+'<td><a name="delAlarmTimeSetWeek" title="删除!" style="vertical-align:middle;"  class="r-h-ico r-h-delete"></a></td></tr></table></form></div>'
	);
	var alarmRuleContentTableTr6Td2DivAlertRandomTimeSetDayContent = $('<div/>').attr('style','overflow-X:hidden;overflow-Y:auto;').css('width','100%').css('height','105px').attr('name','sendAlarmTimeSetCustomInsideDay').html(
			""
//			'<div name="sendAlarmTimeSetCustomInsideDayChildren"><form>'
//			+'<table style="width:85%;"><tr><td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
//			+'<td>:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
//			+'<td>至</td>'
//			+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
//			+'<td>:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
//			+'<td><a name="delAlarmTimeSetWeek" title="删除!" style="vertical-align:middle;"  class="r-h-ico r-h-delete"></a></td></tr></table></form></div>'
	);
	
	var alarmRuleContentTableTr6Td2DivSmsRandomTimeSetWeekContent = $('<div/>').attr('style','overflow-X:hidden;overflow-Y:auto;').css('width','100%').css('height','105px').css('display','none').attr('name','sendAlarmTimeSetCustomInsideWeek').html("");
	var alarmRuleContentTableTr6Td2DivEmailRandomTimeSetWeekContent = $('<div/>').attr('style','overflow-X:hidden;overflow-Y:auto;').css('width','100%').css('height','105px').css('display','none').attr('name','sendAlarmTimeSetCustomInsideWeek').html("");
	var alarmRuleContentTableTr6Td2DivAlertRandomTimeSetWeekContent = $('<div/>').attr('style','overflow-X:hidden;overflow-Y:auto;').css('width','100%').css('height','105px').css('display','none').attr('name','sendAlarmTimeSetCustomInsideWeek').html("");
	
	
	alarmRuleContentTableTr6Td2DivSmsRandomTimeSet.append(alarmRuleContentTableTr6Td2DivSmsRandomTimeSetDayContent);
	alarmRuleContentTableTr6Td2DivSmsRandomTimeSet.append(alarmRuleContentTableTr6Td2DivSmsRandomTimeSetWeekContent);
	alarmRuleContentTableTr6Td2DivSmsRandomTime.append(alarmRuleContentTableTr6Td2DivSmsRandomTimeSet);
	alarmRuleContentTableTr6Td2DivSms.append(alarmRuleContentTableTr6Td2DivSmsSendTimeSet);
	alarmRuleContentTableTr6Td2DivSms.append(alarmRuleContentTableTr6Td2DivSmsRandomTime);
	
	alarmRuleContentTableTr6Td2DivEmailRandomTimeSet.append(alarmRuleContentTableTr6Td2DivEmailRandomTimeSetDayContent);
	alarmRuleContentTableTr6Td2DivEmailRandomTimeSet.append(alarmRuleContentTableTr6Td2DivEmailRandomTimeSetWeekContent);
	alarmRuleContentTableTr6Td2DivEmailRandomTime.append(alarmRuleContentTableTr6Td2DivEmailRandomTimeSet);
	alarmRuleContentTableTr6Td2DivEmail.append(alarmRuleContentTableTr6Td2DivEmailSendTimeSet);
	alarmRuleContentTableTr6Td2DivEmail.append(alarmRuleContentTableTr6Td2DivEmailRandomTime);
	
	alarmRuleContentTableTr6Td2DivAlertRandomTimeSet.append(alarmRuleContentTableTr6Td2DivAlertRandomTimeSetDayContent);
	alarmRuleContentTableTr6Td2DivAlertRandomTimeSet.append(alarmRuleContentTableTr6Td2DivAlertRandomTimeSetWeekContent);
	alarmRuleContentTableTr6Td2DivAlertRandomTime.append(alarmRuleContentTableTr6Td2DivAlertRandomTimeSet);
	alarmRuleContentTableTr6Td2DivAlert.append(alarmRuleContentTableTr6Td2DivAlertSendTimeSet);
	alarmRuleContentTableTr6Td2DivAlert.append(alarmRuleContentTableTr6Td2DivAlertRandomTime);
	
	alarmRuleContentTableTr6Td2.append(alarmRuleContentTableTr6Td2DivSms);
	alarmRuleContentTableTr6Td2.append(alarmRuleContentTableTr6Td2DivEmail);
	alarmRuleContentTableTr6Td2.append(alarmRuleContentTableTr6Td2DivAlert);
	alarmRuleContentTableTr1.append(alarmRuleContentTableTr1Td1).append(alarmRuleContentTableTr1Td2);
	alarmRuleContentTableTr2.append(alarmRuleContentTableTr2Td1).append(alarmRuleContentTableTr2Td2);
	alarmRuleContentTableTr3.append(alarmRuleContentTableTr3Td1).append(alarmRuleContentTableTr3Td2);
	alarmRuleContentTableTr4.append(alarmRuleContentTableTr4Td1).append(alarmRuleContentTableTr4Td2);
	alarmRuleContentTableTr5.append(alarmRuleContentTableTr5Td1).append(alarmRuleContentTableTr5Td2);
	alarmRuleContentTableTr6.append(alarmRuleContentTableTr6Td1).append(alarmRuleContentTableTr6Td2);
	alarmRuleContentTable.append(alarmRuleContentTableTr1).append(alarmRuleContentTableTr2)
	.append(alarmRuleContentTableTr3).append(alarmRuleContentTableTr4).append(alarmRuleContentTableTr5).append(alarmRuleContentTableTr6);
	
	
	
	alarmRuleContentForm.append(alarmRuleContentTable);
	alarmRuleContent.append(alarmRuleContentForm);
	
	form = alarmRuleContent.find('form[name="alarmRuleContentForm"]');
	
	
	
	formatForm(form);
	UMinstance();
//	
	}
	
	function formatForm(form){
		alarmRuleContent.find('input[name=continusCount]').css('width','50px');
		//设置最多发送次数输入框的宽度 dfw 20161227
		alarmRuleContent.find('input[name=sendTimes]').css('width','50px');
		alarmRuleContent.find('[name="alarmSendWay"]').on('click',function(){
				//当前sendWay
				switch (currentSendWay) {
				case 1:
					//设置sendContentSMS
					if(!setFormValue(form,sendContentSMS,1)){
						return false;
					}
					break;
				case 2:
					if(!setFormValue(form,sendContentEMAIL,2)){
						return false;
					}
					break;
				case 3:
					if(!setFormValue(form,sendContentALERT,3)){
						return false;
					}
					break;
				}
				//切换sendWay
				var sendTarget = $(this);
				
				if(parseInt(sendTarget.attr('type'))!=currentSendWay){
					form.find('a[name="alarmSendWay"]').each(function(){
						var aTarget = $(this);
						if(parseInt(aTarget.attr('type'))==currentSendWay){
							aTarget.removeClass('active');
						}
					})
					sendTarget.addClass('active');
					switch (sendTarget.attr('type')) {
					case "1":
						currentSendWay = 1;
						//重置所有选择项
						formReset(form);
						
						fillValueToPage(form,sendContentSMS);
						//告警发送时间面板切换
						form.find('div[name="alarmRuleContentAlarmTimeSetDiv"]').each(function(){
							var target = $(this);
							if(target.attr('type')=='sms'){
								target.css('display','block');
							}else{
								target.css('display','none');
							}
						})
						break;
					case "2":
						currentSendWay = 2;
						//重置所有选择项
						formReset(form);
						
						fillValueToPage(form,sendContentEMAIL);
						
						form.find('div[name="alarmRuleContentAlarmTimeSetDiv"]').each(function(){
							var target = $(this);
							if(target.attr('type')=='email'){
								target.css('display','block');
							}else{
								target.css('display','none');
							}
						})
						break;
					case "3":
						currentSendWay = 3;
						//重置所有选择项
						formReset(form);
						
						fillValueToPage(form,sendContentALERT);
						
						form.find('div[name="alarmRuleContentAlarmTimeSetDiv"]').each(function(){
							var target = $(this);
							if(target.attr('type')=='alert'){
								target.css('display','block');
							}else{
								target.css('display','none');
							}
						})
						break;
					}
				}
			})
			
			form.find('.ico.ico-change').on('click',function(e){
				if(form.find('.ico.ico-change').attr('name')=='addPerson'){
					var addPerson = $('<div/>');
					if(clpproType==undefined || profileNameType==3){
						clpproType=3;
					}
					oc.util.ajax({
						successMsg:null,
						url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/getUser.htm'),
						data:{domainId:domainId,domainType:clpproType},
						success:function(data){
							var personList = data.data.datas;
							var personDiv= $('<div/>').attr('style','height:100%;width:100%;overflow-X:hidden;overflow-Y:scroll;');
							var personListDiv = $('<div/>').addClass("table-datanull");
							if(null==personList||undefined==personList||""==personList){
								personListDiv.html('无人员可选!<br>');
							}else{
								var pickAllFlag = false;
								var Tabhtml='<table  id="useTab" border-collapse="separate" border="0" cellspacing="0" cellpadding="0" style="display:block">';
								Tabhtml+='<tr class="datagrid-row">';
								Tabhtml+='<td >';
								Tabhtml+='<input type="checkbox"  name="pickAll" style="vertical-align:middle;">用户名';
								Tabhtml+='</td>';
								Tabhtml+='<td style="padding-left:40px"><span>姓名</span></td>';
								Tabhtml+='<td style="padding-left:60px"><span>角色</span></td>';
								Tabhtml+='</tr>';
								for(var i=0;i<personList.length;i++){
									var flag = false;
									var personObj = personList[i];
									//去除已添加告警规则的人员
								
									for(var x=0;x<alarmPersonIds.length;x++){
										if(personObj.id == alarmPersonIds[x]){
											flag = true;
											break;
										}
									}
									
									if(!flag){
										if(!pickAllFlag){
											pickAllFlag = true;
										}
										//获取哪些人员已被选择,添加选中状态
										var personPicked = sendContentUser.receiveUser;
										var flagChecked = '';
									
										for(var y=0;y<personPicked.length;y++){
											if(personObj.id == personPicked[y]){
												flagChecked = 'checked="checked"';
												break;
											}
										}
										if(personObj.roleName==null){
											personObj.roleName='---';
										}
										Tabhtml+='<tr class="datagrid-row">';
										if(personObj.account.length>6){
											var	account=	personObj.account.substring(0,6)+"....";
											Tabhtml+='<td><input type="checkbox" '+flagChecked+' name="pickItem" title="'+personObj.account+'" userName="'+personObj.account+'" userId="'+personObj.id+'" style="vertical-align:middle;"><label title="'+personObj.account+'">'+account+'</label></input></td>';
											}else{
											Tabhtml+='<td><input type="checkbox" '+flagChecked+' name="pickItem" title="'+personObj.account+'" userName="'+personObj.account+'" userId="'+personObj.id+'" style="vertical-align:middle;"><label title="'+personObj.account+'">'+personObj.account+'</label></input></td>';
											}
										if(personObj.name.length>6){
											var	name=	personObj.name.substring(0,6)+"....";
												Tabhtml+='<td style="padding-left:40px" title="'+personObj.name+'">'+name+'</td>';
											}else{
												Tabhtml+='<td style="padding-left:40px" title="'+personObj.name+'">'+personObj.name+'</td>';
											}
//										Tabhtml+='<td style="padding-left:100px;cursor:pointer" title="'+personObj.name+'">'+personObj.name+'</td>';
										if(personObj.roleName.length>8){
										var	role_name=	personObj.roleName.substring(0,8)+"....";
											Tabhtml+='<td style="padding-left:60px" title="'+personObj.roleName+'">'+role_name+'</td>';
										}else{
											Tabhtml+='<td style="padding-left:60px" title="'+personObj.roleName+'">'+personObj.roleName+'</td>';
										}
										
										Tabhtml+='</tr>';
									
									}
								}
								Tabhtml+='</table>';
								personListDiv.append(Tabhtml);
								personDiv.append(personListDiv);
								//所有人员都已添加告警规则
								if(!pickAllFlag){
									
									personListDiv.html('无人员可选!<br>');
								}
							}
							
							addPerson.append(personDiv);
							addPerson.dialog({
							    title: '选择告警接收人员',
							    width : 400,
								height : 200,
								top : parseInt($(e.target).offset().top),
								left : parseInt($(e.target).offset().left + $(e.target).width()),
								content:'',
								buttons:[{
							    	text: '确定',
									iconCls:"fa fa-check-circle",
							    	handler:function(){
							    		//选择的发送方式
							    		switch (currentSendWay) {
										case 1:
											getChooseUser(personListDiv,sendContentUser,form);
											break;
										case 2:
											getChooseUser(personListDiv,sendContentUser,form);
											break;
										case 3:
											getChooseUser(personListDiv,sendContentUser,form);
											break;
										}
							    		addPerson.dialog('destroy');
							    	}
							    },{
							    	text: '取消',
									iconCls : "fa fa-times-circle",
							    	handler:function(){
							    		addPerson.dialog('destroy');
							    	}
							    }]
							});
							
							addPerson.find('input[name="pickAll"]').on('click',function(){
								var pickAll = $(this);
								var checkFlag = false;
								if(pickAll.attr('checked')){
									checkFlag = true;
								}
								addPerson.find('input[name="pickItem"]').each(function(){
									$(this).attr('checked',checkFlag);
								});
							});
							
							//每个元素选中与取消,关联全选的状态
							addPerson.find('input[name="pickItem"]').on('click',function(){
								var pickItem = $(this);
								
								if(pickItem.attr('checked')){
									var checkFlag = true;
									addPerson.find('input[name="pickItem"]').each(function(){
										var pickItemIn = $(this);
										if(!pickItemIn.attr('checked')){
											checkFlag = false;
										}
									});
									if(checkFlag){
										addPerson.find('input[name="pickAll"]').attr('checked',checkFlag);
									}
								}else{
									addPerson.find('input[name="pickAll"]').attr('checked',false);
								}
								
							});
							
						}
					})
				}
			})
				
			
			
			form.find('.r-h-ico.r-h-add').on('click',function(e){
				if($(e.target).attr('name')=='addAlarmSendTimeWeekAndDay'){
				//sendAlarmTimeRangeType
				var sendAlarmTimeRangeType = $(e.target).parent().parent().parent().find('#sendAlarmTimeRangeType').combobox('getValue');

				if('everyWeek'==sendAlarmTimeRangeType){
					  var targetWeekDaySelect = $(e.target).parent().parent().parent().parent().parent().find('div[name="sendAlarmTimeSetCustomInsideWeek"]');
					  addWeekChildren(targetWeekDaySelect);
				}else if('everyDay'==sendAlarmTimeRangeType){
					  var targetWeekDaySelect = $(e.target).parent().parent().parent().parent().parent().find('div[name="sendAlarmTimeSetCustomInsideDay"]');
					  addDayChildren(targetWeekDaySelect);
				}
				 
			  }
			
			})
			form.find('.r-h-ico.r-h-delete').on('click',function(e){
//				if($(e.target).attr('name')=='delPerson'){
//					form.find('[name="textareaPerson"]').val('');
//					var userIdArr = new Array();
//					sendContentUser = {receiveUser:userIdArr,receiveUserTextarea:""};
//				}else 
				if($(e.target).attr('name')=='delAlarmTimeSetWeek'){
					$(e.target).parent().parent().parent().parent().parent().parent().remove();
				}
			})
			
			form.find('input[name="sendAlarmSmsTimeSet"]').on('change',function(){
				var target = $(this);
				autoAddChildren(target);
				
				if(form.find('input[name="sendAlarmSmsTimeSet"]').attr('checked')){
					if(target.val()==2){
						target.parent().parent().find('div[name="sendAlarmTimeSetCustom"]').css('display','block');
						
					}else{
						target.parent().parent().find('div[name="sendAlarmTimeSetCustom"]').css('display','none');
					}
				}
			})
			form.find('input[name="sendAlarmEmailTimeSet"]').on('change',function(){
				var target = $(this);
				autoAddChildren(target);
				if(form.find('input[name="sendAlarmSmsTimeSet"]').attr('checked')){
					if(target.val()==2){
						target.parent().parent().find('div[name="sendAlarmTimeSetCustom"]').css('display','block');
						
					}else{
						target.parent().parent().find('div[name="sendAlarmTimeSetCustom"]').css('display','none');
					}
				}
			})
			form.find('input[name="sendAlarmAlertTimeSet"]').on('change',function(){
				var target = $(this);
				autoAddChildren(target);
				if(form.find('input[name="sendAlarmSmsTimeSet"]').attr('checked')){
					if(target.val()==2){
						target.parent().parent().find('div[name="sendAlarmTimeSetCustom"]').css('display','block');
						
					}else{
						target.parent().parent().find('div[name="sendAlarmTimeSetCustom"]').css('display','none');
					}
				}
			})
			
			myForm = bindFormConstentTag(form);
	}
	function autoAddChildren(target){
		var targetInsideDay = target.parent().parent().find('div[name=sendAlarmTimeSetCustomInsideDay]');
		var targetInsideWeek = target.parent().parent().find('div[name=sendAlarmTimeSetCustomInsideWeek]');
		
		if(targetInsideDay.html()==""){
			addDayChildren(targetInsideDay);
	
		}
		if(targetInsideWeek.html()==""){
			addWeekChildren(targetInsideWeek);
		}
	}
	function addDayChildren(targetWeekDaySelect){
		var id = oc.util.generateId();
		  var appendDiv = $('<div/>').attr('name','sendAlarmTimeSetCustomInsideDayChildren').html(
			  '<form id='+id+'>'
				+'<table style="width:85%;" class="alarmTab"><tr><td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
				+'<td>:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
				+'<td>至</td>'
				+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
				+'<td>:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
				+'<td><a name="delAlarmTimeSetWeek" title="删除!" style="vertical-align:middle;"  class="r-h-ico r-h-delete"></a></td></tr></table>'
				+'</form>'
		  );
		  targetWeekDaySelect.append(appendDiv);
		  bindFormTempTag(targetWeekDaySelect.find('#'+id));
	}
	function addWeekChildren(targetWeekDaySelect){
		var id = oc.util.generateId();
		  var appendDiv = $('<div/>').attr('name','sendAlarmTimeSetCustomInsideWeekChildren').html(
			  '<form id='+id+'>'
			  +'<table style="width:90%;" class="alarmTab"><tr><td><select id="sendAlarmTimeRangeTypeWeekDay"  name="sendAlarmTimeRangeTypeWeekDay" class="sendAlarmTimeRangeTypeWeekDay" ></select></td>'
				+'<td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
				+'<td>:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
				+'<td>至</td>'
				+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
				+'<td>:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
				+'<td><a name="delAlarmTimeSetWeek" title="删除!" style="vertical-align:middle;"  class="r-h-ico r-h-delete"></a></td></tr></table>'
				+'</form>'
		  );
		  targetWeekDaySelect.append(appendDiv);
		  bindFormTempTag(targetWeekDaySelect.find('#'+id));
	}
	
	function fillDataToFormTempTag(form){
		
		//当前选择的每天,每周
		var sendAlarmTimeRangeType = '';
		var tempTagData ;
		switch (currentSendWay) {
		case 1:
			if(sendContentSMS.alarmSendTimeType=='everyDay'){
				tempTagData = sendContentSMS.alarmForDay;
			}else{
				tempTagData = sendContentSMS.alarmForWeek;
			}
			break;
		case 2:
			if(sendContentEMAIL.alarmSendTimeType=='everyDay'){
				tempTagData = sendContentEMAIL.alarmForDay;
			}else{
				tempTagData = sendContentEMAIL.alarmForWeek;
			}
			break;
		case 3:
			if(sendContentALERT.alarmSendTimeType=='everyDay'){
				tempTagData = sendContentALERT.alarmForDay;
			}else{
				tempTagData = sendContentALERT.alarmForWeek;
			}
			break;
		}
		
	}
	function bindFormTempTag(form){
		form.find('.r-h-ico.r-h-delete').on('click',function(e){
			$(e.target).parent().parent().parent().parent().parent().parent().remove();
		});
		
		return oc.ui.form({
			selector:form,
			combobox:[{
				selector:'[name=sendAlarmTimeRangeTypeWeekDay]',
				placeholder : false,
				width:50,
				data:[
				      {id:'1',name:'周一'},
				      {id:'2',name:'周二'},
				      {id:'3',name:'周三'},
				      {id:'4',name:'周四'},
				      {id:'5',name:'周五'},
				      {id:'6',name:'周六'},
				      {id:'7',name:'周日'}
		        ]
			},{
				selector:'[name=sendAlarmTimeStartHour]',
				placeholder : false,
				width:50,
				data:[
				      {id:'0',name:'00'},{id:'1',name:'01'},{id:'2',name:'02'},
				      {id:'3',name:'03'},{id:'4',name:'04'},{id:'5',name:'05'},
				      {id:'6',name:'06'},{id:'7',name:'07'},{id:'8',name:'08'},
				      {id:'9',name:'09'},{id:'10',name:'10'},{id:'11',name:'11'},
				      {id:'12',name:'12'},{id:'13',name:'13'},{id:'14',name:'14'},
				      {id:'15',name:'15'},{id:'16',name:'16'},{id:'17',name:'17'},
				      {id:'18',name:'18'},{id:'19',name:'19'},{id:'20',name:'20'},
				      {id:'21',name:'21'},{id:'22',name:'22'},{id:'23',name:'23'}
		        ]
			},{
				selector:'[name=sendAlarmTimeStartMinite]',
				placeholder : false,
				width:50,
				data:[
				      {id:'0',name:'00'},{id:'5',name:'05'},{id:'10',name:'10'},
				      {id:'15',name:'15'},{id:'20',name:'20'},{id:'25',name:'25'},
				      {id:'30',name:'30'},{id:'35',name:'35'},{id:'40',name:'40'},
				      {id:'45',name:'45'},{id:'50',name:'50'},{id:'55',name:'55'}
		        ]
			},{
				selector:'[name=sendAlarmTimeEndMinite]',
				placeholder : false,
				width:50,
				data:[
				      {id:'0',name:'00'},{id:'5',name:'05'},{id:'10',name:'10'},
				      {id:'15',name:'15'},{id:'20',name:'20'},{id:'25',name:'25'},
				      {id:'30',name:'30'},{id:'35',name:'35'},{id:'40',name:'40'},
				      {id:'45',name:'45'},{id:'50',name:'50'},{id:'55',name:'55'}
		        ]
			},{
				selector:'[name=sendAlarmTimeEndHour]',
				placeholder : false,
				width:50,
				data:[
				      {id:'0',name:'00'},{id:'1',name:'01'},{id:'2',name:'02'},
				      {id:'3',name:'03'},{id:'4',name:'04'},{id:'5',name:'05'},
				      {id:'6',name:'06'},{id:'7',name:'07'},{id:'8',name:'08'},
				      {id:'9',name:'09'},{id:'10',name:'10'},{id:'11',name:'11'},
				      {id:'12',name:'12'},{id:'13',name:'13'},{id:'14',name:'14'},
				      {id:'15',name:'15'},{id:'16',name:'16'},{id:'17',name:'17'},
				      {id:'18',name:'18'},{id:'19',name:'19'},{id:'20',name:'20'},
				      {id:'21',name:'21'},{id:'22',name:'22'},{id:'23',name:'23'},{id:'24',name:'24'}
		        ],
		        onChange : function(){
		        	var target = $(this);
		        	var targetObj = target.parent().parent().find('#sendAlarmTimeEndMinite');
		        	if(target.combobox('getValue')=='24'){
		        		targetObj.combobox('setValue','0');
		        		targetObj.combobox('disable');
		        	}else{
		        		targetObj.combobox('enable');
		        	}
		        	
		        }
			}]
		});
	}
	
	function bindFormConstentTag(form){
		if(profileType=="model_profile"){
			profileNameType=2;
		}else if(profileType=="biz_profile"){
			profileNameType=3;
		}
		return oc.ui.form({
			selector:form,
			combobox:[{
				selector:'[name=localDatatest]',
				placeholder : false,
				width:50,
				data:[
				      {id:'minute',name:'分钟'},
				      {id:'hour',name:'小时'}
		        ]
			},{
				selector:'[name=sendAlarmTimeRangeType]',
				placeholder : false,
				width:50,
				data:[
				      {id:'everyDay',name:'每天'},
				      {id:'everyWeek',name:'每周'}
		        ],
		        onChange : function(){
					var target = $(this);
					var targetWeekDaySelect = target.parent().parent().parent().parent().parent();
//					var targetWeekAdd = target.parent().find('a[name="addAlarmSendTimeWeek"]');
					
					if(target.combobox('getValue')=='everyDay'){
						targetWeekDaySelect.find('div[name="sendAlarmTimeSetCustomInsideDay"]').css('display','block');
						targetWeekDaySelect.find('div[name="sendAlarmTimeSetCustomInsideWeek"]').css('display','none');
						
					}else{
						targetWeekDaySelect.find('div[name="sendAlarmTimeSetCustomInsideDay"]').css('display','none');
						targetWeekDaySelect.find('div[name="sendAlarmTimeSetCustomInsideWeek"]').css('display','block');
						
					}
				}
			},{
				selector:'[name=sendAlarmTimeStartHour]',
				placeholder : false,
				width:50,
				data:[
				      {id:'0',name:'00'},{id:'1',name:'01'},{id:'2',name:'02'},
				      {id:'3',name:'03'},{id:'4',name:'04'},{id:'5',name:'05'},
				      {id:'6',name:'06'},{id:'7',name:'07'},{id:'8',name:'08'},
				      {id:'9',name:'09'},{id:'10',name:'10'},{id:'11',name:'11'},
				      {id:'12',name:'12'},{id:'13',name:'13'},{id:'14',name:'14'},
				      {id:'15',name:'15'},{id:'16',name:'16'},{id:'17',name:'17'},
				      {id:'18',name:'18'},{id:'19',name:'19'},{id:'20',name:'20'},
				      {id:'21',name:'21'},{id:'22',name:'22'},{id:'23',name:'23'}
		        ]
			},{
				selector:'[name=sendAlarmTimeStartMinite]',
				placeholder : false,
				width:50,
				data:[
				      {id:'0',name:'00'},{id:'5',name:'05'},{id:'10',name:'10'},
				      {id:'15',name:'15'},{id:'20',name:'20'},{id:'25',name:'25'},
				      {id:'30',name:'30'},{id:'35',name:'35'},{id:'40',name:'40'},
				      {id:'45',name:'45'},{id:'50',name:'50'},{id:'55',name:'55'}
		        ]
			},{
				selector:'[name=sendAlarmTimeEndMinite]',
				placeholder : false,
				width:50,
				data:[
				      {id:'0',name:'00'},{id:'5',name:'05'},{id:'10',name:'10'},
				      {id:'15',name:'15'},{id:'20',name:'20'},{id:'25',name:'25'},
				      {id:'30',name:'30'},{id:'35',name:'35'},{id:'40',name:'40'},
				      {id:'45',name:'45'},{id:'50',name:'50'},{id:'55',name:'55'}
		        ]
			},{
				selector:'[name=sendAlarmTimeEndHour]',
				placeholder : false,
				width:50,
				data:[
				      {id:'0',name:'00'},{id:'1',name:'01'},{id:'2',name:'02'},
				      {id:'3',name:'03'},{id:'4',name:'04'},{id:'5',name:'05'},
				      {id:'6',name:'06'},{id:'7',name:'07'},{id:'8',name:'08'},
				      {id:'9',name:'09'},{id:'10',name:'10'},{id:'11',name:'11'},
				      {id:'12',name:'12'},{id:'13',name:'13'},{id:'14',name:'14'},
				      {id:'15',name:'15'},{id:'16',name:'16'},{id:'17',name:'17'},
				      {id:'18',name:'18'},{id:'19',name:'19'},{id:'20',name:'20'},
				      {id:'21',name:'21'},{id:'22',name:'22'},{id:'23',name:'23'},{id:'24',name:'24'}
		        ],
		        onChange : function(){
		        	var target = $(this);
		        	var targetObj = target.parent().parent().find('#sendAlarmTimeEndMinite');
		        	if(target.combobox('getValue')=='24'){
		        		targetObj.combobox('setValue','0');
		        		targetObj.combobox('disable');
		        	}else{
		        		targetObj.combobox('enable');
		        	}
		        	
		        }
			},{
				selector:'[name=sendAlarmTimeRangeTypeWeekDay]',
				placeholder : false,
				width:50,
				data:[
				      {id:'1',name:'周一'},
				      {id:'2',name:'周二'},
				      {id:'3',name:'周三'},
				      {id:'4',name:'周四'},
				      {id:'5',name:'周五'},
				      {id:'6',name:'周六'},
				      {id:'7',name:'周日'}
		        ]
			},{//

				selector:'[name=mailRulechoice2]',
				placeholder : false,
				width:100,
				url:oc.resource.getUrl('system/alarm/getSelector.htm?type=2&profileNameType='+profileNameType),
				filter:function(data){
					return data.data;
				}
				,
		        onChange : function(newValue,oldValue){
		        	var type=form.find('[name="alarmSendWay"].active').attr('type')
		        	var data=form.find(".mailRulechoice2").combobox("getData");
		        	if(newValue==undefined){
		        		newValue=data[0].id;
		        	}
		        	//根据选中的值选择相应的邮件模板进行显示
		        	oc.util.ajax({
						url : oc.resource.getUrl("system/alarm/getAllById.htm"),
						data:{id:newValue},
						successMsg : null,
						sync:false,
						success : function(data) {
							if(data.data){
								myAlarmEditor.setContent(data.data.content,false);
								
							}
						}
					});
		        	
		        },
		        onLoadSuccess :function(){
		        }
				
				
				
			},{
				selector:'[name=mailRulechoice]',
				placeholder : false,
				width:100,
				url:oc.resource.getUrl('system/alarm/getSelector.htm?type=1&profileNameType='+profileNameType),
				filter:function(data){
					return data.data;
				}
				,
		        onChange : function(newValue,oldValue){
		        	var type=form.find('[name="alarmSendWay"].active').attr('type')
		        	var data=form.find(".mailRulechoice").combobox("getData");
		        	if(newValue==undefined){
		        		newValue=data[0].id;
		        	}
		        	//根据选中的值选择相应的邮件模板进行显示
		        		oc.util.ajax({
							url : oc.resource.getUrl("system/alarm/getAllById.htm"),
							data:{id:newValue},
							successMsg : null,
							sync:false,
							success : function(data) {
								if(data.data){
									myAlarmEditor.setContent(data.data.content,false);
									
								}
							}
						});
		        	
		        },
		        onLoadSuccess :function(){
		        }
			}]
//			,
//			numberbox:[{
//				width:50,
//				selector:'input[name="continusCount"]'
//			}]
		});
	}
	
//	function formatSendAlarmTimeSetCustomDiv(sendAlarmTimeSetCustomDiv,){
//		
//	}
	function fillValueToPage(form,sendContent){
		if("checked"==sendContent.isSmsAlarm){
			form.find('input[name="isSmsAlarm"]').attr('checked',true);
		}
		if("checked"==sendContent.sendTimeSet){
			form.find('input[name="sendCondition"]').attr('checked',true);
		}
		if(0<sendContent.sendTimeNum){
//			form.find('.continusCount').numberbox('setValue', sendContent.sendTimeNum);
			form.find('.continusCount').val(sendContent.sendTimeNum);
		}
        if(0<sendContent.sendTimes){
            form.find('.sendTimes').val(sendContent.sendTimes);
        }
		if(0<sendContent.templateId){
//			form.find('.continusCount').numberbox('setValue', sendContent.sendTimeNum);
			//form.find('.mailRulechoice').combobox('getValue')
			var type=form.find('[name="alarmSendWay"].active').attr('type');
			if(type==1){
				form.find('.mailRulechoice').combobox('setValue',sendContent.templateId);
			}else{
				form.find('.mailRulechoice2').combobox('setValue',sendContent.templateId);
			}
		
//			myForm = bindFormConstentTag(form)
		}
		if(null!=sendContent.sendTimeType&&undefined!=sendContent.sendTimeType&&""!=sendContent.sendTimeType){
			form.find('.localData').combobox('setValue', sendContent.sendTimeType);
		}
		if(null!=sendContent.alarmLevel&&undefined!=sendContent.alarmLevel&&0<sendContent.alarmLevel.length){
			$('input[name="level"]').each(function(){
				var inputLevel = $(this);
				for(var s=0;s<sendContent.alarmLevel.length; s++){
					if(sendContent.alarmLevel[s]==inputLevel.attr('value')){
						inputLevel.attr('checked',true);
						break;
					}
				}
			})
		}
		if(null!=sendContent.receiveUserTextarea&&undefined!=sendContent.receiveUserTextarea&&""!=sendContent.receiveUserTextarea){
//			form.find('textarea[name="textareaPerson"]').val(sendContent.receiveUserTextarea);
		}
	}
	function setFormValue(form,sendContent,type){
		if(!myForm.validate()){
			return myForm.validate();
		}
		
		var target ; 
		var sendAlarmTimeType ; 
		//保存告警时间设置
		switch (type) {
		case 1:
			form.find('div[name="alarmRuleContentAlarmTimeSetDiv"]').each(function(){
				var targetIn = $(this);
				if(targetIn.attr('type')=='sms'){
					target = targetIn;
				}
			})
			target.find('input[name="sendAlarmSmsTimeSet"]').each(function(){
				var etarget = $(this);
				if(etarget.prop('checked')){
					sendAlarmTimeType = etarget.val();
				}
			})
			break;
		case 2:
			form.find('div[name="alarmRuleContentAlarmTimeSetDiv"]').each(function(){
				var targetIn = $(this);
				if(targetIn.attr('type')=='email'){
					target = targetIn;
				}
			})
			target.find('input[name="sendAlarmEmailTimeSet"]').each(function(){
				var etarget = $(this);
				if(etarget.prop('checked')){
					sendAlarmTimeType = etarget.val();
				}
			})
			break;
		case 3:
			form.find('div[name="alarmRuleContentAlarmTimeSetDiv"]').each(function(){
				var targetIn = $(this);
				if(targetIn.attr('type')=='alert'){
					target = targetIn;
				}
			})
			target.find('input[name="sendAlarmAlertTimeSet"]').each(function(){
				var etarget = $(this);
				if(etarget.prop('checked')){
					sendAlarmTimeType = etarget.val();
				}
			})
			break;
		}
		
		if(sendAlarmTimeType==1){
			sendAlarmTimeType = 'allTime';
		}else{
			sendAlarmTimeType = target.find('#sendAlarmTimeRangeType').combobox('getValue');
			if('everyWeek'==sendAlarmTimeType){
				var valueStrArr = new Array();
				var valueNumObj = new Array();
				var flag = true;
				target.find('div[name="sendAlarmTimeSetCustomInsideWeekChildren"]').each(function(i){
					var targetChildren = $(this);
					var dayOfWeek = targetChildren.find('#sendAlarmTimeRangeTypeWeekDay').combobox('getValue');
					var startHour = targetChildren.find('#sendAlarmTimeStartHour').combobox('getValue');
					var startMinite = targetChildren.find('#sendAlarmTimeStartMinite').combobox('getValue');
					var endHour = targetChildren.find('#sendAlarmTimeEndHour').combobox('getValue');
					var endMinite = targetChildren.find('#sendAlarmTimeEndMinite').combobox('getValue');
					
					var startTimeNum = parseInt(startHour)*60+parseInt(startMinite);
					var endTimeNum = parseInt(endHour)*60+parseInt(endMinite);
					var valueNumObjInside = {index:i,dayofWeek:dayOfWeek,startTimeNum:startTimeNum,endTimeNum:endTimeNum};
					valueNumObj.push(valueNumObjInside);
					
					if(endTimeNum>startTimeNum){
						var valueStr = dayOfWeek+'-'+startTimeNum+'-'+endTimeNum;
						valueStrArr.push(valueStr);
					}else{
						flag = false;
					}
					
				});
				
				if(flag){
					//判断选择的时间不能重叠
					for(var i=0;i<valueNumObj.length;i++){
						for(var x=0;x<valueNumObj.length;x++){
							if(valueNumObj[i].dayofWeek==valueNumObj[x].dayofWeek && valueNumObj[i].index!=valueNumObj[x].index){
								if(valueNumObj[i].startTimeNum==valueNumObj[x].startTimeNum || valueNumObj[i].endTimeNum==valueNumObj[x].endTimeNum){
									flag = false;
									alert('每周的告警发送时间不能重叠!');
									return false;
								}
								if(valueNumObj[i].startTimeNum>valueNumObj[x].startTimeNum ){
									if(valueNumObj[i].startTimeNum<valueNumObj[x].endTimeNum){
										flag = false;
										alert('每周的告警发送时间不能重叠!');
										return false;
									}
								}else if(valueNumObj[i].startTimeNum<valueNumObj[x].startTimeNum){
									if(valueNumObj[i].endTimeNum>valueNumObj[x].startTimeNum){
										flag = false;
										alert('每周的告警发送时间不能重叠!');
										return false;
									}
								}
							}
						}
					}
					
					sendContent.alarmForWeek = valueStrArr;
				}else{
					alert('每周的告警发送结束时间须大于开始时间!');
					return false;
				}
				
				
			}else if('everyDay'==sendAlarmTimeType){
				var valueStrArr = new Array();
				var valueNumObj = new Array();
				var flag = true;
				target.find('div[name="sendAlarmTimeSetCustomInsideDayChildren"]').each(function(i){
					var targetChildren = $(this);
					var dayOfWeek = 0;
					var startHour = targetChildren.find('#sendAlarmTimeStartHour').combobox('getValue');
					var startMinite = targetChildren.find('#sendAlarmTimeStartMinite').combobox('getValue');
					var endHour = targetChildren.find('#sendAlarmTimeEndHour').combobox('getValue');
					var endMinite = targetChildren.find('#sendAlarmTimeEndMinite').combobox('getValue');
					
					var startTimeNum = parseInt(startHour)*60+parseInt(startMinite);
					var endTimeNum = parseInt(endHour)*60+parseInt(endMinite);
					var valueNumObjInside = {index:i,startTimeNum:startTimeNum,endTimeNum:endTimeNum};
					valueNumObj.push(valueNumObjInside);
					
					if(endTimeNum>startTimeNum){
						var valueStr = dayOfWeek+'-'+startTimeNum+'-'+endTimeNum;
						valueStrArr.push(valueStr);
					}else{
						flag = false;
					}
					
				});
				
				if(flag){
					//判断选择的时间不能重叠
					for(var i=0;i<valueNumObj.length;i++){
						for(var x=0;x<valueNumObj.length;x++){
							if(valueNumObj[i].index!=valueNumObj[x].index){
								if(valueNumObj[i].startTimeNum==valueNumObj[x].startTimeNum || valueNumObj[i].endTimeNum==valueNumObj[x].endTimeNum){
									flag = false;
									alert('每天的告警发送时间不能重叠!');
									return false;
								}
								if(valueNumObj[i].startTimeNum>valueNumObj[x].startTimeNum ){
									if(valueNumObj[i].startTimeNum<valueNumObj[x].endTimeNum){
										flag = false;
										alert('每天的告警发送时间不能重叠!');
										return false;
									}
								}else if(valueNumObj[i].startTimeNum<valueNumObj[x].startTimeNum){
									if(valueNumObj[i].endTimeNum>valueNumObj[x].startTimeNum){
										flag = false;
										alert('每天的告警发送时间不能重叠!');
										return false;
									}
								}
							}
						}
					}
					
					sendContent.alarmForDay = valueStrArr;
				}else{
					alert('每天的告警发送结束时间须大于开始时间!');
					return false;
				}
//				var flag = true;
//				var valueStr;
//				target.find('div[name="sendAlarmTimeSetCustomInsideDay"]').each(function(){
//					var targetChildren = $(this);
//					var startHour = targetChildren.find('#sendAlarmTimeStartHour').combobox('getValue');
//					var startMinite = targetChildren.find('#sendAlarmTimeStartMinite').combobox('getValue');
//					var endHour = targetChildren.find('#sendAlarmTimeEndHour').combobox('getValue');
//					var endMinite = targetChildren.find('#sendAlarmTimeEndMinite').combobox('getValue');
//					
//					var startTimeNum = parseInt(startHour)*60+parseInt(startMinite);
//					var endTimeNum = parseInt(endHour)*60+parseInt(endMinite);
//					
//					if(endTimeNum>startTimeNum){
//						valueStr = '0-'+startTimeNum+'-'+endTimeNum;
//					}else{
//						flag = false;
//					}
//					
//				});
//				if(flag){
//					sendContent.alarmForDay = valueStr;
//				}else{
//					alert('告警发送结束时间须大于开始时间!');
//					return false;
//				}
			}
		}
		//发送告警时间数据保存
		sendContent.alarmSendTimeType = sendAlarmTimeType;
		target.find('input[name="sendUnAlarmTimeAlarmCheckbox"]').each(function(){
			var targetCheckbox = $(this);
			if(targetCheckbox.attr('checked')){
				sendContent.ifSendUnalarmTimeAlarm = 'checked';
			}else{
				sendContent.ifSendUnalarmTimeAlarm = 'unChecked';
			}
		})
		
		
		//其余部分数据保存
		var isSmsAlarmTemp = form.find('input[name="isSmsAlarm"]').attr('checked');
		var checkedLevelArr = new Array();
		form.find('input[name="level"]').each(function(){
			var checkedLevelTemp = $(this);
			if(checkedLevelTemp.attr('checked')){
				checkedLevelArr.push(checkedLevelTemp.attr('value'));
			}
		})
		
		var sendTimeSet = form.find('input[name="sendCondition"]').attr('checked');
		sendTimeSet = "checked"==sendTimeSet?sendTimeSet:"";
		var sendTimeNum = form.find('input[name="continusCount"]').val();
        sendTimeNum = 0<sendTimeNum?sendTimeNum:0;

		var type=form.find('[name="alarmSendWay"].active').attr('type');
		if(type==1){
			var templateId=form.find('.mailRulechoice').combobox('getValue');
			form.find('.mailRulechoice').combobox('setValue',templateId);
		}else{
			var templateId=form.find('.mailRulechoice2').combobox('getValue');
			form.find('.mailRulechoice2').combobox('setValue',templateId);
		}

        //获取最多发送次数，只有短信和邮件告警方式才有此参数 dfw 20161228
		if(type == 1 || type == 2){
            var sendTimes = form.find('input[name="sendTimes"]').val();
            sendTimes = 0<sendTimes?sendTimes:0;
            //暂存最多发送次数 dfw 20161228
            sendContent.sendTimes = sendTimes;
		}
		
		var sendTimeType = form.find('.localData').combobox('getValue');
		
		//修改sendContent值
		sendContent.isSmsAlarm = isSmsAlarmTemp;
		if(checkedLevelArr.length==0){
			sendContent.alarmLevel = "";
		}else{
			sendContent.alarmLevel = checkedLevelArr;
		}
		sendContent.sendTimeSet = sendTimeSet;
		sendContent.sendTimeNum = sendTimeNum;
		sendContent.templateId = templateId;
		sendContent.sendTimeType = sendTimeType;
		
		return true;
	}
	function getChooseUser(dialog,sendContent,form){
		var receiveUserTemp = new Array();
		var userNameTextarea = sendContent.receiveUserTextarea = "";
		
		dialog.find('input[name="pickItem"]').each(function(){
			var pickItemTemp = $(this);
			if(pickItemTemp.attr('checked')){
//				var flag = true;
//				for(var x=0;x<receiveUserTemp.length;x++){
//					if(receiveUserTemp[x]==pickItemTemp.attr('userId')){
//						flag = false;
//					}
//				}
//				if(flag){
//					receiveUserTemp.push(pickItemTemp.attr('userId'));
//					if(""==userNameTextarea){
//						userNameTextarea = ' < '+pickItemTemp.attr('userName')+' > ';
//					}else{
//						userNameTextarea += ' ,  < '+pickItemTemp.attr('userName')+' > ';
//					}
//				}
				
				receiveUserTemp.push(pickItemTemp.attr('userId'));
				if(""==userNameTextarea){
					userNameTextarea = ' < '+pickItemTemp.attr('userName')+' > ';
				}else{
					userNameTextarea += ' ,  < '+pickItemTemp.attr('userName')+' > ';
				}
				
			}
			
		})
		
		sendContent.receiveUser = receiveUserTemp;
		sendContent.receiveUserTextarea = userNameTextarea;
		
		form.find('textarea[name="textareaPerson"]').val(sendContent.receiveUserTextarea);
	}
	function formReset(form){
		var type=form.find('[name="alarmSendWay"].active').attr('type');
		if(type==1){//sms
			form.find("#sel1").attr('style','display:block');
			form.find("#sel2").attr('style','display:none');
		}else if(type==2){
			form.find("#sel1").attr('style','display:none');
			form.find("#sel2").attr('style','display:block');
		}else{
			form.find("#sel1").attr('style','display:none');
			form.find("#sel2").attr('style','display:none');
		}
        //如果是短信或邮件告警方式，显示最多发送次数设置，其他告警方式则隐藏 dfw 20161228
        type == 1 || type == 2 ? form.find("#sendTimes").show() : form.find("#sendTimes").hide();

		if(type==3){
		form.find(".notifycontent").parent().attr('style','display:none');
		form.find("table tr").eq(4).attr('style','height:10%');
		}else{
			form.find("table tr").eq(3).attr('style','height:8%');
			
		}
		form.find(":checkbox").each(function(){
			$(this).attr('checked',false);
		})
//		form.find('.continusCount').numberbox('setValue', '');
		form.find('.continusCount').val(null);
		//重置最多发送次数的值 dfw 20170104
		form.find('.sendTimes').val(null);
		form.find('.localData').combobox('setValue', 'minute');
		reloadTemplate(form);
		var nameTitle;
		switch(currentSendWay){
		case 1:
			nameTitle = '启用短信告警';
			break;
		case 2:
			nameTitle = '启用邮件告警';
			break;
		case 3:
			nameTitle = '启用Alert告警';
			break;
		}
		
		form.find('label[name="currentSendWayNameTitle"]').html(nameTitle);
	//	myForm =bindFormConstentTag(form)
	}
	function reloadTemplate(form){
		if(profileType=="model_profile"){
			profileNameType=2;
		}else if(profileType=="biz_profile"){
			profileNameType=3;
		}
		var type=form.find('[name="alarmSendWay"].active').attr('type')
		oc.util.ajax({
			url:oc.resource.getUrl('system/alarm/getSelector.htm?type='+type+'&profileNameType='+profileNameType),
			success:function(d){
				
				if(type==1){//sms
					form.find('.mailRulechoice').combobox('loadData',d.data);
					if(sendContentSMS.templateId==0 || sendContentEMAIL.templateId==undefined){
						form.find('.mailRulechoice').combobox('setValue',d.data[0].id);
					}else{
						form.find('.mailRulechoice').combobox('setValue',sendContentSMS.templateId);
					}
					var newValue=form.find('.mailRulechoice').combobox('getValue');
					oc.util.ajax({
						url : oc.resource.getUrl("system/alarm/getAllById.htm"),
						data:{id:newValue},
						successMsg : null,
						sync:false,
						success : function(data) {
							if(data.data){
								myAlarmEditor.setContent(data.data.content,false);
								
							}
						}
					});
				}else if(type==2){//email
					form.find('.mailRulechoice2').combobox('loadData',d.data);
					if(sendContentEMAIL.templateId==0 || sendContentEMAIL.templateId==undefined){
						form.find('.mailRulechoice2').combobox('setValue',d.data[0].id);
					}else{
						form.find('.mailRulechoice2').combobox('setValue',sendContentEMAIL.templateId);
					}
					
					var newValue=form.find('.mailRulechoice2').combobox('getValue');
					oc.util.ajax({
						url : oc.resource.getUrl("system/alarm/getAllById.htm"),
						data:{id:newValue},
						successMsg : null,
						sync:false,
						success : function(data) {
							if(data.data){
								myAlarmEditor.setContent(data.data.content,false);
								
							}
						}
					});
				}
			
				
			}
		});
		
	}

	
	function pageReset(){
		currentSendWay = 1;
		var alarmLevelArrReset = new Array();
		var userIdArr = new Array();
		var alarmForWeek = new Array();
		sendContentUser = {receiveUser:userIdArr,receiveUserTextarea:""};
		sendContentSMS = {receiveUser:"",receiveUserTextarea:"",isSmsAlarm:"checked",alarmLevel:alarmLevelArrReset,sendTimeSet:"",sendTimeNum:0,sendTimeType:"minute",alarmSendTimeType:'allTime',ifSendUnalarmTimeAlarm:'unChecked',alarmForDay:alarmForWeek,alarmForWeek:alarmForWeek};
		sendContentEMAIL = {receiveUser:"",receiveUserTextarea:"",isSmsAlarm:"checked",alarmLevel:alarmLevelArrReset,sendTimeSet:"",sendTimeNum:0,sendTimeType:"minute",alarmSendTimeType:'allTime',ifSendUnalarmTimeAlarm:'unChecked',alarmForDay:alarmForWeek,alarmForWeek:alarmForWeek};
		sendContentALERT = {receiveUser:"",receiveUserTextarea:"",isSmsAlarm:"checked",alarmLevel:alarmLevelArrReset,sendTimeSet:"",sendTimeNum:0,sendTimeType:"minute",alarmSendTimeType:'allTime',ifSendUnalarmTimeAlarm:'unChecked',alarmForDay:alarmForWeek,alarmForWeek:alarmForWeek};
	}
	
	oc.resource.resource.management.alarmrulecontent={
			openAdd:function(profileNameTypeTemp,domainIdTemp,profileIdTemp,profileTypeTemp,alarmPersonIdsTemp,callbackTemp,alarmLevelStrTemp,clTypetemp){
//				pageReset();
				openType = 'add';
				profileNameType = profileNameTypeTemp;
				var domainIdStr ;
				domainId = domainIdTemp;
				profileId = profileIdTemp;
				profileType = profileTypeTemp;
				alarmPersonIds = alarmPersonIdsTemp;
				callback = callbackTemp;
				alarmLevelStr = alarmLevelStrTemp;
				clpproType=clTypetemp;
				switch(profileTypeTemp){
				case 'netFlow':
					hiddenSendTimeSet = true;
					break;
				case 'sysLog':
					hiddenSendTimeSet = true;
					break;
				default:
					hiddenSendTimeSet = false;
				    break;	
				}
				
				alarmLevelArrTemp = new Array();
				for(var i=0;i<alarmLevelStrTemp.length;i++){
					if(alarmLevelStrTemp[i].checked){
						alarmLevelArrTemp.push(alarmLevelStrTemp[i].level);
					}
				}
				sendContentSMS.alarmLevel = alarmLevelArrTemp;
				sendContentEMAIL.alarmLevel = alarmLevelArrTemp;
				sendContentALERT.alarmLevel = alarmLevelArrTemp;
				
				init();
			
			}
	}
})(jQuery)