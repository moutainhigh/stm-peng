(function($){
	var myAlarmRuleEditor;
	oc.ns('oc.resource.management.alarmrulesset');
	var alarmRulesIDPage,typePage,datagridReload,profileTypePage,alarmLevelStr,openState,hiddenSendTimeSet;

	var myFormPage,myForm;
	
	var id=oc.util.generateId();
	//初始化
	function _init(dlg){
		var div=$('#alarmRules').attr('id',oc.util.generateId());
		var datagridDiv=div.find('#alarmRulesDatagrid');
		var formDiv=$('#alarmRulesSet').attr('id',id);
		myForm = formDiv.find("form[name='alarmRulesDialog']");
		
		
		myForm.find('input[name="sendAlarmTimeSet"]').each(function(){
			var target = $(this);
			if("alert"==typePage && target.val()==2){
				target.attr('disabled',true);
			}
		})
		
		
		myForm.find('input[name="sendAlarmTimeSet"]').on('change',function(){
			var target = $(this);
			if(target.prop('checked')){
				if(target.val()==2){
					target.parent().parent().find('div[name="sendAlarmTimeSetCustom"]').css('display','block');
					
				}else{
					target.parent().parent().find('div[name="sendAlarmTimeSetCustom"]').css('display','none');
				}
			}
		})
		
		myForm.find('.r-h-ico.r-h-add').on('click',function(e){
				if($(e.target).attr('name')=='addAlarmSendTimeWeekAndDay'){
				//sendAlarmTimeRangeType
				var sendAlarmTimeRangeType = $(e.target).parent().parent().parent().find('#sendAlarmTimeRangeType').combobox('getValue');

				if('everyWeek'==sendAlarmTimeRangeType){
					  var targetWeekDaySelect = $(e.target).parent().parent().parent().parent().parent().find('div[name="sendAlarmTimeSetCustomInsideWeek"]');
					  var id = oc.util.generateId();
					  var appendDiv = $('<div/>').attr('name','sendAlarmTimeSetCustomInsideWeekChildren').html(
						  '<form id='+id+'>'
						  +'<table style="width:80%;"><tr><td><select id="sendAlarmTimeRangeTypeWeekDay"  name="sendAlarmTimeRangeTypeWeekDay" class="sendAlarmTimeRangeTypeWeekDay" ></select></td>'
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
				}else if('everyDay'==sendAlarmTimeRangeType){
					  var targetWeekDaySelect = $(e.target).parent().parent().parent().parent().parent().find('div[name="sendAlarmTimeSetCustomInsideDay"]');
					  var id = oc.util.generateId();
					  var appendDiv = $('<div/>').attr('name','sendAlarmTimeSetCustomInsideDayChildren').html(
						  '<form id='+id+'>'
							+'<table style="width:80%;"><tr><td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
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
			  }
			})
		
		oc.ui.form({
			selector:myForm,
			combobox:[{
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
					
					if(target.combobox('getValue')=='everyDay'){
						targetWeekDaySelect.find('div[name="sendAlarmTimeSetCustomInsideDay"]').css('display','block');
						targetWeekDaySelect.find('div[name="sendAlarmTimeSetCustomInsideWeek"]').css('display','none');
					}else{
						targetWeekDaySelect.find('div[name="sendAlarmTimeSetCustomInsideDay"]').css('display','none');
						targetWeekDaySelect.find('div[name="sendAlarmTimeSetCustomInsideWeek"]').css('display','block');
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
				      {id:'21',name:'21'},{id:'22',name:'22'},{id:'23',name:'23'}
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
			}]
		});
		
		_initForm(formDiv);
		UMinstance();
	}
	function appendInsideDayDiv(targetWeekDaySelect){
		  var id = oc.util.generateId();
		  var appendDiv = $('<div/>').attr('name','sendAlarmTimeSetCustomInsideDayChildren').html(
			  '<form id='+id+'>'
				+'<table style="width:80%;"><tr><td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
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
	function appendInsideWeekDiv(targetWeekDaySelect){
		
		  var id = oc.util.generateId();
		  var appendDiv = $('<div/>').attr('name','sendAlarmTimeSetCustomInsideWeekChildren').html(
			  '<form id='+id+'>'
			  +'<table style="width:80%;"><tr><td><select id="sendAlarmTimeRangeTypeWeekDay"  name="sendAlarmTimeRangeTypeWeekDay" class="sendAlarmTimeRangeTypeWeekDay" ></select></td>'
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
	function bindFormTempTag(form){
		form.find('.r-h-ico.r-h-delete').on('click',function(e){
			$(e.target).parent().parent().parent().parent().parent().parent().remove();
		
		})
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
	//初始化form
	function _initForm(form){
		var sendAlarmTimeSetCustomInsideDay = form.find('div[name=sendAlarmTimeSetCustomInsideDay]:first');
		var sendAlarmTimeSetCustomInsideWeek = form.find('div[name=sendAlarmTimeSetCustomInsideWeek]:first');
		appendInsideDayDiv(sendAlarmTimeSetCustomInsideDay);
		appendInsideWeekDiv(sendAlarmTimeSetCustomInsideWeek);
		
		var alarmLevels ,continus ,continusCount ,continusCountUnit, sendTimes ;
		
		if(hiddenSendTimeSet){
			form.find('tr[name="sendConditionTr"]:first').css('display','none');
		}
		var alarmStr ;
		switch (typePage) {
		case "message":
			alarmStr = '启用短信告警';
			break;
		case "email":
			alarmStr = '启用邮件告警';
			break;
		case "alert":
			alarmStr = '启用Alert告警';
			form.find("#emailContent").attr('style','display:none;');
			//alert告警方式不需要最多发送次数 dfw 20170105
            form.find("#sendTimes").attr('style','display:none;');
			break;
		}
		form.find('label[name="currentSendWayNameTitle"]').html(alarmStr);
		form.find("label[name=levelStr]").each(function(i){
			var target = $(this);
			var flag = true;
			for(var x=0;x<alarmLevelStr.length;x++){
				if(alarmLevelStr[x].level==target.attr('type')){
					target.html(alarmLevelStr[x].content);
					flag = false;
				}
			}
			if(flag){
				target.parent().css('display','none');
			}
			
	    });
			    
		//查询该告警规则下的AlarmSendCondition信息
		oc.util.ajax({
			url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/getAlarmSendCondition.htm'),
			data:{sendWay : typePage , alarmRulesID :alarmRulesIDPage},
			successMsg:null,
			success:function(data){
			
				if(null == data.data){
					//设置默认值
					//默认值增加最多发送次数的设置 dfw 20170105
					myFormPage = formatForm(form,'[name=localDatatest]','minute','input[name=continusCount]',10,typePage,'[name=mailchoice]','input[name=sendTimes]',0);
					$("input:checkbox[name=sendCondition]").prop("checked", true);
					$("input:checkbox[name=level]").each(function(i){
						var thisLevelChecked = false;
						if($(this).val() == 'down'){
							thisLevelChecked = true;
						}
						$(this).attr("checked", thisLevelChecked);
				    });
					
				}else{
					var alarmLevels = data.data.alarmLevels;
					var continus = data.data.continus;
					var continusCount = data.data.continusCount;
					var continusCountUnit = data.data.continusCountUnit;
					var allTime = data.data.allTime;
					var dayPeriodes = data.data.dayPeriodes;
					var weekPeriodes = data.data.weekPeriodes;
					var enabled = data.data.enabled;
					var sendIntime = data.data.sendIntime;
					var templateIdSelect=data.data.templateId;
					//获取最多可发送次数的数据 dfw 20170105
					var sendTimes = data.data.sendTimes;
					
					if(enabled){
						form.find('input[name="isSmsAlarm"]:first').attr('checked','checked');
					}
					if(sendIntime){
						form.find('input[name="sendUnAlarmTimeAlarmCheckbox"]:first').attr('checked','checked');
					}
					if(null!=alarmLevels){
						form.find("input:checkbox[name=level]").each(function(i){
							var thisLevelChecked = false;
							for(var i=0;i<alarmLevels.length;i++){
								if($(this).val() == alarmLevels[i]){
									thisLevelChecked = true;
								}
							}
							$(this).attr("checked", thisLevelChecked);
					    });
					}
//					console.info("templateId:"+templateId);
//					form.find(".mailchoice").combobox('setValue',templateId);
					//过滤最多可发送次数的数据 dfw 20170105
                    sendTimes = sendTimes || 0;
					if(continus){
						form.find("input:checkbox[name=sendCondition]").prop("checked", true);
						if(0 == continusCount){
							continusCount=null;
						}
						//表单赋值增加最多可发送次数 dfw 20170105
						myFormPage = formatForm(form,'[name=localDatatest]',continusCountUnit,'input[name=continusCount]',continusCount,typePage,'[name=mailchoice]',templateIdSelect,'input[name=sendTimes]',sendTimes);
					}else{
						form.find("input:checkbox[name=sendCondition]").prop("checked", false);
						if(0 == continusCount){
							continusCount=null;
						}
                        //表单赋值增加最多可发送次数 dfw 20170105
						myFormPage = formatForm(form,'[name=localDatatest]',continusCountUnit,'input[name=continusCount]',continusCount,typePage,'[name=mailchoice]',templateIdSelect,'input[name=sendTimes]',sendTimes);
					}
					
					if(allTime){
					}else{
						//判断每天发送，还是每周内发送
						if(null!=dayPeriodes){
							//显示相应信息
							form.find('input[name="sendAlarmTimeSet"]').each(function(e){
								var etarget = $(this);
								if(etarget.val()==2){
									etarget.prop('checked',true);
								}else{
									etarget.prop('checked',false);
								}
							})
							form.find('div[name="sendAlarmTimeSetCustom"]:first').css('display','block');
							var targetWeekDaySelect = form.find('div[name="sendAlarmTimeSetCustomInsideDay"]').html('');
							
							addDayperiodesDiv(targetWeekDaySelect,dayPeriodes);
						}else if(null!=weekPeriodes){
							//显示相应信息
							form.find('input[name="sendAlarmTimeSet"]').each(function(e){
								var etarget = $(this);
								if(etarget.val()==2){
									etarget.attr('checked',true);
								}else{
									etarget.attr('checked',false);
								}
							})
							form.find('div[name="sendAlarmTimeSetCustom"]:first').css('display','block');
							form.find('#sendAlarmTimeRangeType').combobox('setValue','everyWeek');
							
							var targetWeekDaySelect = form.find('div[name="sendAlarmTimeSetCustomInsideWeek"]').html('');
							addWeekperiodesDiv(targetWeekDaySelect,weekPeriodes);
						}
					}
//					form.find(".mailchoice").combobox('setText','ss');
				}
				
				//页面设置不能编辑
				if(openState==2){
					disabledPage(form);
				}
			}
		});
		
		return true;
		
	}
	function addWeekperiodesDiv(targetWeekDaySelect,weekPeriodes){
		for(var i=0;i<weekPeriodes.length;i++){
			var id = oc.util.generateId();
			
			var appendDivStr = "";
			if(openState==1){
				appendDivStr = '<form id='+id+'>'
				+'<table style="width:80%;"><tr><td><select id="sendAlarmTimeRangeTypeWeekDay"  name="sendAlarmTimeRangeTypeWeekDay" class="sendAlarmTimeRangeTypeWeekDay" ></select></td>'
				+'<td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
				+'<td>:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
				+'<td>至</td>'
				+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
				+'<td>:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
				+'<td><a name="delAlarmTimeSetWeek" title="删除!" style="vertical-align:middle;"  class="r-h-ico r-h-delete"></a></td></tr></table>'
				+'</form>';
			}else{
				appendDivStr = '<form id='+id+'>'
				+'<table style="width:80%;"><tr><td><select id="sendAlarmTimeRangeTypeWeekDay"  name="sendAlarmTimeRangeTypeWeekDay" class="sendAlarmTimeRangeTypeWeekDay" ></select></td>'
				+'<td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
				+'<td>:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
				+'<td>至</td>'
				+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
				+'<td>:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
				+'</tr></table>'
				+'</form>';
			}
			
			  var appendDiv = $('<div/>').attr('name','sendAlarmTimeSetCustomInsideWeekChildren').html(appendDivStr);
			  targetWeekDaySelect.append(appendDiv);
			  var formTemp = targetWeekDaySelect.find('#'+id);
			  bindFormTempTag(formTemp);
			  var weekPeriodesItem = weekPeriodes[i];
			  
			  var dayOfWeek,startHour,startMinite,endHour,endMinite ;
			  dayOfWeek = weekPeriodesItem.dayOfWeek;
			  if(weekPeriodesItem.start<60){
				  startHour = 0 ;
				  startMinite = weekPeriodesItem.start;
			  }else{
				  startHour = parseInt(weekPeriodesItem.start/60) ;
				  startMinite = weekPeriodesItem.start%60;
			  }
			  if(weekPeriodesItem.end<60){
				  endHour = 0 ;
				  endMinite = weekPeriodesItem.end;
			  }else{
				  endHour = parseInt(weekPeriodesItem.end/60) ;
				  endMinite = weekPeriodesItem.end%60;
			  }
			  formTemp.find('#sendAlarmTimeRangeTypeWeekDay').combobox('setValue',dayOfWeek);
			  
			  formTemp.find('#sendAlarmTimeStartHour').combobox('setValue',startHour);
			  
			  formTemp.find('#sendAlarmTimeStartMinite').combobox('setValue',startMinite);
			  
			  formTemp.find('#sendAlarmTimeEndHour').combobox('setValue',endHour);
			  
			  formTemp.find('#sendAlarmTimeEndMinite').combobox('setValue',endMinite);
		}
	}
	function addDayperiodesDiv(targetWeekDaySelect,dayPeriodes){
		for(var i=0;i<dayPeriodes.length;i++){
			var id = oc.util.generateId();
			
			var appendDivStr = "";
			if(openState==1){
				appendDivStr = '<form id='+id+'>'
				  +'<table style="width:80%;"><tr>'
					+'<td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
					+'<td>:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
					+'<td>至</td>'
					+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
					+'<td>:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
					+'<td><a name="delAlarmTimeSetWeek" title="删除!" style="vertical-align:middle;"  class="r-h-ico r-h-delete"></a></td></tr></table>'
					+'</form>';
			}else{
				appendDivStr = '<form id='+id+'>'
				  +'<table style="width:80%;"><tr>'
					+'<td><select  id="sendAlarmTimeStartHour" name="sendAlarmTimeStartHour" ></select></td>'
					+'<td>:</td><td><select id="sendAlarmTimeStartMinite" name="sendAlarmTimeStartMinite" ></select></td>'
					+'<td>至</td>'
					+'<td><select id="sendAlarmTimeEndHour" name="sendAlarmTimeEndHour" ></select></td>'
					+'<td>:</td><td><select id="sendAlarmTimeEndMinite" name="sendAlarmTimeEndMinite" ></select></td>'
					+'</tr></table>'
					+'</form>';
			}
			  var appendDiv = $('<div/>').attr('name','sendAlarmTimeSetCustomInsideDayChildren').html(appendDivStr);
			  targetWeekDaySelect.append(appendDiv);
			  var formTemp = targetWeekDaySelect.find('#'+id);
			  bindFormTempTag(formTemp);
			  var dayPeriodesItem = dayPeriodes[i];
			  var startHour ,startMinite ,endHour ,endMinite ;
			  if(dayPeriodesItem.start<60){
				  startHour = 0 ;
				  startMinite = dayPeriodesItem.start;
			  }else{
				  startHour = parseInt(dayPeriodesItem.start/60) ;
				  startMinite = dayPeriodesItem.start%60;
			  }
			  if(dayPeriodesItem.end<60){
				  endHour = 0 ;
				  endMinite = dayPeriodesItem.end;
			  }else{
				  endHour = parseInt(dayPeriodesItem.end/60) ;
				  endMinite = dayPeriodesItem.end%60;
			  } 
			  formTemp.find('#sendAlarmTimeStartHour').combobox('setValue',startHour);
			  
			  formTemp.find('#sendAlarmTimeStartMinite').combobox('setValue',startMinite);
			  
			  formTemp.find('#sendAlarmTimeEndHour').combobox('setValue',endHour);
			  
			  formTemp.find('#sendAlarmTimeEndMinite').combobox('setValue',endMinite);
		}
	}
	function disabledPage(form){
		alert('disabledPage');
		form.find('a[name="addAlarmSendTimeWeekAndDay"]:first').parent().css('display','none');
		form.find(":input").each(function(){
						
			$(this).attr('disabled',true);
			
		})
		form.find("#sendAlarmTimeRangeType").combobox('disable');
		form.find("#localDatatest").combobox('disable');
		form.find("div[name=sendAlarmTimeSetCustomInsideWeekChildren]").each(function(){
			$(this).find('#sendAlarmTimeRangeTypeWeekDay').combobox('disable');
			$(this).find('#sendAlarmTimeStartHour').combobox('disable');
			$(this).find('#sendAlarmTimeStartMinite').combobox('disable');
			$(this).find('#sendAlarmTimeEndHour').combobox('disable');
			$(this).find('#sendAlarmTimeEndMinite').combobox('disable');
		})
		form.find("div[name=sendAlarmTimeSetCustomInsideDayChildren]").each(function(){
			$(this).find('#sendAlarmTimeStartHour').combobox('disable');
			$(this).find('#sendAlarmTimeStartMinite').combobox('disable');
			$(this).find('#sendAlarmTimeEndHour').combobox('disable');
			$(this).find('#sendAlarmTimeEndMinite').combobox('disable');
		})
		
	}
	function checkSendCondition(){
		if(!myFormPage.validate()){
			return myFormPage.validate();
		}
		
		if(myForm.find("input:checkbox[name=sendCondition]").prop("checked")){
		  var fromData=JSON.stringify(myFormPage.val());
		  var obj = JSON.parse(fromData);
		  var timeTypeValue =obj.localDatatest;
		  var timeNumValue = obj.continusCount;
		  if(null == timeNumValue || "" == timeNumValue || 0 > timeNumValue || 0 == timeNumValue){
			  alert("请确认时间间隔数！");
			  return false;
		  }else if(null == timeTypeValue || "" == timeTypeValue ){
			  alert("请确认时间类型！");
			  return false;
		  }
		}
		return true;
	}
	//表单赋值增加最多可发送次数的参数
	function formatForm(form,comName,comValue,numboxName,numboxValue,typePage,templateName,templateIdSelect,sendTimesName,sendTimesValue){
		var type=1;
		if(typePage=="message"){
			type=1;
		}else if(typePage=="email"){
			type=2;
		}else{
			type=3;
		}
		var profileNameType=0;
		if(profileTypePage=="biz_profile"){
			profileNameType=3;
		}else if(profileTypePage=="model_profile"){
			profileNameType=2;
		}
		if(templateIdSelect==0){
			oc.util.ajax({
				url:oc.resource.getUrl('system/alarm/getSelector.htm?type='+type+'&profileNameType='+profileNameType),
				async:false,
				success:function(d){
//					form.find('.mailchoice').combobox('loadData',d.data);
					templateIdSelect=d.data[0].id;
					
				}
			});
		}
		form.find('input[name=continusCount]').css('width','50px');
		//设置最多发送次数文本框宽度 dfw 20170104
        form.find('input[name=sendTimes]').css('width','50px');
		form.find(numboxName).val(numboxValue);//parseInt(numboxValue)
		form.find(sendTimesName).val(sendTimesValue || null);
		 return oc.ui.form({
			selector:form,
			combobox:[{
				selector:comName,
				placeholder : false,
				selected:comValue,
				width:70,
				data:[
				      {id:'minute',name:'分钟'},
				      {id:'hour',name:'小时'}
		        ]
			},
			{
				selector:'[name=mailchoice]',
				placeholder : false,
				width:120,
				url:oc.resource.getUrl('system/alarm/getSelector.htm?type='+type+'&profileNameType='+profileNameType),
				filter:function(data){
					return data.data;
				},
				selected:false,
				value:templateIdSelect,
		        onChange : function(newValue,oldValue){
		        	var data=form.find(".mailchoice").combobox("getData");
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
								myAlarmRuleEditor.setContent(data.data.content,false);
								
							}
						}
					});
		      },
		        onLoadSuccess :function(){
		        	var data=form.find(".mailchoice").combobox("getData");
		        	if(templateIdSelect==undefined){
		        		templateIdSelect=data[0].id;
		        		form.find(".mailchoice").combobox('select',templateIdSelect=data[0].id);
			        	form.find(".mailchoice").combobox('setValue',templateIdSelect=data[0].id);
			        	form.find(".mailchoice").combobox('setText',templateIdSelect=data[0].name);
		        	}
		        	oc.util.ajax({
						url : oc.resource.getUrl("system/alarm/getAllById.htm"),
						data:{id:templateIdSelect},
						successMsg : null,
						sync:false,
						success : function(data) {
							if(data.data){
								myAlarmRuleEditor.setContent(data.data.content,false);
								
							}
						}
					});
		        }
			}]
//		 	,
//			numberbox:[{
//				width:30,
//				value : numboxValue,
//				selector:numboxName
//			}]
		});
		
	}
	function UMinstance(){
		UM.delEditor("myalarmRuleContentEditor");
//		UM.getEditor("myalarmRuleContentEditor").setEnabled();
		myAlarmRuleEditor = UM.getEditor('myalarmRuleContentEditor');
		 UM.getEditor('myalarmRuleContentEditor').setDisabled('fullscreen');
	        disableBtn("enable");
		$(".edui-container #myalarmRuleContentEditor").width("100%");
		
		$(".edui-container .edui-toolbar").hide();
	

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
	function open(type){
		type=type;
		var typeTitle;
		switch (type) {
		case "message":
			typeTitle = '短信设置';
			break;
		case "email":
			typeTitle = '邮件设置';
			break;
		case "alert":
			typeTitle = 'Alert设置';
			break;
		}
		var dlg = $('<div/>');
		var buttons ;
		if(2==openState){
			//只能查看
			buttons = null;
		}else{
			buttons = [{
		    	text: '保存',
	            iconCls:"fa fa-save",

		    	handler:function(){
		    		if(checkSendCondition()){
		    			var dataObj = getAjaxData();
		    			if(!dataObj.flag){
		    				return false;
		    			}
		    			
						oc.util.ajax({
						url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/addAlarmCondition.htm'),
						data:dataObj,
						successMsg:null,
						success:function(data){
							if('success'==data.data){
								alert('修改成功!');
							}else{
								alert('修改失败!');
							}
							dlg.dialog('destroy');
							datagridReload();
						}
					    })
					}
		    	}
		    },{
		    	text: '取消',
				iconCls:"fa fa-times-circle",
		    	handler:function(){
		    		dlg.dialog('destroy');
		    	}
		    }];
		}
		
		dlg.dialog({
		    title: typeTitle,
			//修改弹出框宽度，避免增加最多可发送次数后换行的问题 dfw 20170105
		    width : 700,
			height : 520,
			buttons:buttons,
		    href: oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/alarmRulesSet.html'),
		    onLoad:function(){
		    	_init(dlg);
		    }
		});
		
		return true;
	}
	
	function getAjaxData(){
		var formDiv=$('#'+id);
		var target ; 
		formDiv.find("div[name='sendAlarmTimeSetCustom']").each(function(){
			target = $(this);
		})
		var templateId=0;
		var sendAlarmTimeType;
		var alarmForWeek = new Array();
		var alarmForDay = new Array();
		templateId=formDiv.find('.mailchoice').combobox('getValue');
		formDiv.find('input[name="sendAlarmTimeSet"]').each(function(i){
			var etarget = $(this);
			if(etarget.prop('checked')){
				sendAlarmTimeType = etarget.val();
			}
		})
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
				
				
				if(flag ){
					//判断选择的时间不能重叠
					for(var i=0;i<valueNumObj.length;i++){
						for(var x=0;x<valueNumObj.length;x++){
							if(valueNumObj[i].dayofWeek==valueNumObj[x].dayofWeek && valueNumObj[i].index!=valueNumObj[x].index){
								if(valueNumObj[i].startTimeNum==valueNumObj[x].startTimeNum || valueNumObj[i].endTimeNum==valueNumObj[x].endTimeNum){
									flag = false;
									alert('每周的告警发送时间不能重叠!');
									return {flag:false};
								}
								if(valueNumObj[i].startTimeNum>valueNumObj[x].startTimeNum ){
									if(valueNumObj[i].startTimeNum<valueNumObj[x].endTimeNum){
										flag = false;
										alert('每周的告警发送时间不能重叠!');
										return {flag:false};
									}
								}else if(valueNumObj[i].startTimeNum<valueNumObj[x].startTimeNum){
									if(valueNumObj[i].endTimeNum>valueNumObj[x].startTimeNum){
										flag = {flag:false};
										alert('每周的告警发送时间不能重叠!');
										return {flag:false};
									}
								}
							}
						}
					}
					if(valueStrArr.length==0){
						alert('请添加每周告警发送时间!');
						return {flag:false};
					}
					alarmForWeek = valueStrArr;
				}else{
					alert('每周的告警发送结束时间须大于开始时间!');
					return {flag:false};
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
									flag = {flag:false};
									alert('每天的告警发送时间不能重叠!');
									return {flag:false};
								}
								if(valueNumObj[i].startTimeNum>valueNumObj[x].startTimeNum ){
									if(valueNumObj[i].startTimeNum<valueNumObj[x].endTimeNum){
										flag = {flag:false};
										alert('每天的告警发送时间不能重叠!');
										return {flag:false};
									}
								}else if(valueNumObj[i].startTimeNum<valueNumObj[x].startTimeNum){
									if(valueNumObj[i].endTimeNum>valueNumObj[x].startTimeNum){
										flag = {flag:false};
										alert('每天的告警发送时间不能重叠!');
										return {flag:false};
									}
								}
							}
						}
					}
					if(valueStrArr.length==0){
						alert('请添加每天告警发送时间!');
						return {flag:false};
					}
					alarmForDay = valueStrArr;
				}else{
					alert('每天的告警发送结束时间须大于开始时间!');
					return {flag:false};
				}
			}
		}
		
		var alarmForWeekDataArr = '';
		var alarmForDayDataArr = '';
		
		for(var q=0;q<alarmForWeek.length;q++){
			if(''==alarmForWeekDataArr){
				alarmForWeekDataArr = alarmForWeek[q];
			}else{
				alarmForWeekDataArr += ','+alarmForWeek[q];
			}
		}
		for(var q=0;q<alarmForDay.length;q++){
			if(''==alarmForDayDataArr){
				alarmForDayDataArr = alarmForDay[q];
			}else{
				alarmForDayDataArr += ','+alarmForDay[q];
			}
		}
		var ifSendUnalarmTimeAlarm;
		target.find('input[name="sendUnAlarmTimeAlarmCheckbox"]').each(function(){
			var targetCheckbox = $(this);
			if(targetCheckbox.prop('checked')){
				ifSendUnalarmTimeAlarm = 'checked';
			}else{
				ifSendUnalarmTimeAlarm = 'unChecked';
			}
		})
		
		//其余数据
		var alarmLev = "";
		var sendCon = "";
		var isAlarmTemp = formDiv.find('input[name="isSmsAlarm"]:first').prop('checked');
		isAlarmTemp=isAlarmTemp==true?'checked':'';
		formDiv.find("input:checkbox[name=level]:checked").each(function(i){
	       if(0==i){
	    	   alarmLev = $(this).val();
	       }else{
	    	   alarmLev += (","+$(this).val());
	       }
	      });
	      
		formDiv.find("input:checkbox[name=sendCondition]:checked").each(function(i){
	       if(0==i){
	    	   sendCon = $(this).val();
	       }else{
	    	   sendCon += (","+$(this).val());
	       }
	      });
	      
	      var fromData=JSON.stringify(myFormPage.val());
		  var obj = JSON.parse(fromData);
		  var timeTypeValue =obj.localDatatest;
		  var timeNumValue = obj.continusCount;
		  //获取数据增加最多可发送次数 dfw 20170105
		  var sendTimes = obj.sendTimes;
		  
		  if("checked" == isAlarmTemp && !alarmLev){
			  alert('请设置告警规则级别！');
			  return false;
		  }
	      
	      return {flag:true,alarmRulesID : alarmRulesIDPage,sendWay:typePage,alarmLevel:alarmLev,sendCondition:sendCon,
	    	  continusCount:timeNumValue,continusCountUnit:timeTypeValue,alarmSendTimeType:sendAlarmTimeType,
	    	  alarmForDayData:alarmForDayDataArr,alarmForWeekData:alarmForWeekDataArr,
	    	  ifSendUnalarmTimeAlarm:ifSendUnalarmTimeAlarm,isAlarm:isAlarmTemp,templateId:templateId,sendTimes:sendTimes};
	}
	
	/**
	 * 提供给外部引用的接口
	 */
	oc.resource.management.alarmrulesset={
		open:function(alarmRulesID,type,datagrid,profileType,alarmLevelStrObj,openStateTemp){
			//alert('你传入的参数：'+alarmRulesID+','+type);
			alarmRulesIDPage = alarmRulesID;
			typePage = type;
			datagridReload = datagrid;
			profileTypePage = profileType;
			openState = openStateTemp;
			alarmLevelStr = alarmLevelStrObj;
			switch(profileType){
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
			
			return open(type);
		}
	};
})(jQuery);


