(function($){
	oc.ns('oc.resource.alarmrules');
	
	var dataReload,datagrid,toolBarTemp,div;
	var profileId ,profileType ,domainId,profileNameType,openState,alarmLevelStr,queryType,clType;
	var defaultRuleId=0;
	//var profileType = 'biz_profile';profileType = 'model_profile'; 
	
//	var alarmRule_MessAll = new Array(),alarmRule_EmailAll = new Array(),alarmRule_AlertAll = new Array();
	//修改过告警方式是否启用状态的告警规则
	var editAlarmRule_Mess = new Array(),editAlarmRule_Email = new Array(),editAlarmRule_Alert = new Array();
	
	function open(managerId){
		
	
	div=$('#alarmRules').attr('id',oc.util.generateId());
	var datagridDiv=div.find('#alarmRulesDatagrid');
	var	form=div.find("form[name='alarmRulesToolBar']");
	
	//表格查询表单对象
	queryForm=oc.ui.form({selector:form});
	
	if(openState==2){
		form.css('display','none');
		toolBarTemp = null;
	}else{
		toolBarTemp = {right:[queryForm.selector]};
	}
	
	 //定义datagrid渲染完成,执行的操作
    var extendView = $.extend({},$.fn.datagrid.defaults.view,{
    	onAfterRender:function(target){
    		var rows = datagrid.selector.datagrid('getRows');
    		if(openState==1){
    			//全选按钮是否disable
    	    	var flag = true;
    	    	 for(var i=0;i<rows.length;i++){
    	    		 if(!rows[i].operation){
    	    			 $(target).parent().find('tr[datagrid-row-index='+i+']').find(":checkbox").each(function(){
    	    				 var temp = $(this);
    	    				 //列头checkbox可以操作,以便删除
    	    				 if(temp.attr('name') && temp.attr('name')=='id'){
    	    					 temp.attr('disabled',false);
    	    				 }else{
    	    					 temp.attr('disabled',true);
    	    				 }
    	    			 })
    	    			 
    	    		 }else{
    	    			 flag = false;
    	    		 }
    	    	 }
    	    	if(rows.length>0){
    	    		var targetHeader = $(target).parent().find('.datagrid-header');
	        		targetHeader.find(":checkbox").each(function(){
	        			$(this).attr('disabled',flag);
	         		})
    	    	}
    	    	
    		}else{
    	    	 for(var i=0;i<rows.length;i++){
    	    		 $(target).parent().find('tr[datagrid-row-index='+i+']').find(":checkbox").attr('disabled',true);
    	    	 }
    	    	var targetHeader = $(target).parent().find('.datagrid-header');
        		targetHeader.find(":checkbox").each(function(){
        			$(this).attr('disabled',true);
         		})
    		}
    	}
    });
	
	datagrid = oc.ui.datagrid({
		selector:datagridDiv,
		pagination:false,
//		width : 588,
//		height : 412,
//		fit : false,
		url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/profileAlarmRules.htm?profileId='+profileId+'&profileType='+profileType+'&queryType='+queryType+'&domainIdArr='+domainId+'&profileNameType='+profileNameType),
		//queryForm:queryForm,
		octoolbar:toolBarTemp,
		columns:[[
	         {field:'id',title:'id',checkbox:true,sortable:true,isDisabled:function(value,row,index){
	        	 if(row.userID==managerId){
	        		 defaultRuleId=row.alarmRulesID;
	        		 return true
	        	 }else{
	        		 return false;
	        	 }
	        	 
	         }},
	         {field:'userName',title:'接收人员',width:40,align:'left'},
	         {field:'sendWayChoice',title:'告警方式',align:'left',width:100,formatter:sendWayFormat}
	     ]],
	     onCheckAll:function(){
	    	 var rows = datagrid.selector.datagrid('getRows');
	 		  for(var i=0;i<rows.length;i++){
	    		 if(!rows[i].operation){
	    			 var targetTr = div.find('tr[datagrid-row-index='+i+']');
	 	     		 
	 	     		 targetTr.find(":checkbox").each(function(){
//	 	     		    $(this).attr('checked',false);
	 	     			var temp = $(this);
	    				 //列头checkbox可以操作,以便删除(影响全选按钮选中效果)
	    				 if(temp.attr('name') && temp.attr('name')=='id'){
	    					 temp.attr('checked',true);
	    				 }else{
	    					 temp.attr('checked',false);
	    				 }
	 	     		 })
	    		 }
	    	  }
	     }
	     ,
	     onClickRow:function(rowIndex, rowData){
	     	if(!rowData.operation){
	     		datagrid.selector.datagrid('unselectRow',rowIndex);
	     		var targetTr = div.find('tr[datagrid-row-index='+rowIndex+']');
	     		
	     		targetTr.find(":checkbox").each(function(){
	     		    $(this).attr('checked',false);
	     		})
	     		
	     		alert('该用户域内资源已被移除或已不在域内,告警方式不能操作!');
	     	}
	     },
	     onLoadSuccess:function(data){ 
	    	 
//	    	 $('.checkbox_disabled').on('click', function(){
//	    		 $(this).attr("checked", false);
//                 alert('请先设置告警方式中相应的告警规则!');
//	         });
	    	 
	    	 div.find('input[enableSetContent=true]').on('click', function(){
	    		 $(this).attr("checked", false);
                 alert('请先设置告警方式中相应的告警规则!');
	         });
	    	 
	    	 div.find('input[enableSetContent=false]').on('click',function(e){
	    		 var checkState = $(e.target).attr('checked')=='checked'?true:false;
	    		 
	    		 switch ($(e.target).attr('name')) {
				case 'checkboxMessage':
					editAlarmRule_Mess = pushIdToArr($(e.target).val(),checkState,editAlarmRule_Mess);
					break;
				case 'checkboxEmail':
					editAlarmRule_Email = pushIdToArr($(e.target).val(),checkState,editAlarmRule_Email);
					break;
				case 'checkboxAlert':
					editAlarmRule_Alert = pushIdToArr($(e.target).val(),checkState,editAlarmRule_Alert);
					break;
				}
	    	 });
	    	 
	    	 div.find('.ico-edit.lacate-none').on('click',function(e){
	    		 stopProp(e);
	    		 var jObj = $(e.target);
        		 var objId = jObj.attr('id');
        		 var objName = jObj.attr('name');
        		 if(objId!=undefined && objName!=undefined){
	        		 oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRulesSet.js',function(){
	        			 oc.resource.management.alarmrulesset.open(objId,objName,oc.resource.alarmrules.datagridReloadWithNewDomainId,profileType,alarmLevelStr,openState);
	    			 });
        		 }
	    	 });
	    	 
//	    	 var rows = datagrid.selector.datagrid('getRows');
//	    	 alarmRule_MessAll = new Array();
//	    	 alarmRule_EmailAll = new Array();
//    		 alarmRule_AlertAll = new Array();
//	    	 for(var x=0;x<rows.length;x++){
//	    		 alarmRule_MessAll.push({alarmRulesID:rows.alarmRulesID,checkState:rows.messEnable});
//	    		 alarmRule_EmailAll.push({alarmRulesID:rows.alarmRulesID,checkState:rows.emailEnable});
//	    		 alarmRule_AlertAll.push({alarmRulesID:rows.alarmRulesID,checkState:rows.alertEnable});
//	    	 }
//	    	 if(openState==1){
//	    			//全选按钮是否disable
//	    	    	var flag = true;
//	    	    	 for(var i=0;i<rows.length;i++){
//	    	    		 if(!rows[i].operation){
//	    	    			 datagridDiv.parent().find('tr[datagrid-row-index='+i+']').find(":checkbox").attr('disabled',true);
//	    	    		 }else{
//	    	    			 flag = false;
//	    	    		 }
//	    	    	 }
//	    	    	if(rows.length>0){
//	    	    		var targetHeader = datagridDiv.parent().find('.datagrid-header');
//		        		targetHeader.find(":checkbox").each(function(){
//		        			$(this).attr('disabled',flag);
//		         		})
//	    	    	}
//	    	    	
//	    	 }else{
//	    	    	 for(var i=0;i<rows.length;i++){
//	    	    		 datagridDiv.parent().find('tr[datagrid-row-index='+i+']').find(":checkbox").attr('disabled',true);
//	    	    	 }
//	    	    	var targetHeader = datagridDiv.parent().find('.datagrid-header');
//	        		targetHeader.find(":checkbox").each(function(){
//	        			$(this).attr('disabled',true);
//	         		})
//	    	 }
	    	 
         },view:extendView
	});
	
	form.find("a[name='addAlarmUser']").linkbutton('RenderLB',{
		iconCls:"fa fa-plus",
		onClick : function(){
			
			var alarmPersonData = datagridDiv.datagrid('getData');
			var alarmPersonIds = new Array();
			for(var i = 0; i < alarmPersonData.rows.length; i ++){
				var row = alarmPersonData.rows[i];
				alarmPersonIds.push(row.userID);
			}
			
//			oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/addAlarmRule.js',function(){
//				oc.demo.addAlarmUser.open(profileNameType,domainId,dataReload,profileId,profileType,alarmPersonIds);
			oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRuleContent.js',function(){
//				console.info("clType: "+clType);
				oc.resource.resource.management.alarmrulecontent.openAdd(profileNameType,domainId,profileId,profileType,alarmPersonIds,oc.resource.alarmrules.datagridReloadWithNewDomainId,alarmLevelStr,clType);
			});
		}
	});
	form.find("a[name='enableAlarmUser']").linkbutton('RenderLB',{
		onClick : function(){
			saveAlarmSendSet();
		}
	});
	form.find("a[name='deleteAlarmUser']").linkbutton('RenderLB',{
		iconCls : 'fa fa-trash-o',
		onClick : function(){
			
			var userSelected = datagridDiv.datagrid('getSelections');
			if(null == userSelected || 0==userSelected.length){
				alert('请选择一个告警规则！');
			}else{
				var userSelectedRuleId ='' ;
				var rows = datagrid.selector.datagrid('getRows');
		 		  for(var i=0;i<rows.length;i++){
		    		 if(!rows[i].operation){
		    			 var targetTr = div.find('#datagrid-row-r7-2-'+i);
		 	     		 
		 	     		 targetTr.find(":checkbox").each(function(){
		 	     		    $(this).attr('disabled',true);
		 	     		 })
		    		 }
		    	  }
			
				for(var i=0;i<userSelected.length;i++){
//					if(userSelected[i].operation){
						if(userSelectedRuleId==''){
							if(defaultRuleId != userSelected[i].alarmRulesID){
							userSelectedRuleId = userSelected[i].alarmRulesID;
							}
						}else{
							if(defaultRuleId != userSelected[i].alarmRulesID){//默认用户不能删除
							userSelectedRuleId+=','+userSelected[i].alarmRulesID;
							
							}
						}
//					}
				}
				if(userSelectedRuleId==""){
					if(userSelected.length==1){//只有一条
						alert("默认告警规则不能删除！");
					}else{
						alert("请选择一个告警规则！");
					}
				
				}else{
				oc.ui.confirm('是否删除该告警规则！',function(){
					oc.util.ajax({
						url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/deleteAlarmRule.htm'),
						data:{ruleId : userSelectedRuleId},
						successMsg:null,
						success:function(data){
							dataReloadMethod();
						}
					});
				},function(){
				});
				}
			}
		}
	});

	function sendWayFormat(value,row,rowIndex){
		var messCheckState = "";
		var emailCheckState = "";
		var alertCheckState = "";
		var messEnableSetContent = "false";
		var emailEnableSetContent = "false";
		var alertEnableSetContent = "false";
		
		if(row.haveMess){
			if(row.messEnable){
				messCheckState = 'checked="true"';
		    }
			if(row.messEnableSetContent){
				messEnableSetContent = "true";
			}
		}else{
			//messCheckState = 'disabled = true ';
			messCheckState = 'class=\"checkbox_disabled\" ';
		}
		if(row.haveEmail){
			if(row.emailEnable){
				emailCheckState = 'checked="true"';
			}
			if(row.emailEnableSetContent){
				emailEnableSetContent = "true";
			}
		}else{
			//emailCheckState = 'disabled = true ';
			emailCheckState = 'class=\"checkbox_disabled\" ';
		}
		if(row.haveAlert){
			if(row.alertEnable){
				alertCheckState = 'checked="true"';
			}
			if(row.alertEnableSetContent){
				alertEnableSetContent = "true";
			}
		}else{
			//emailCheckState = 'disabled = true ';
			alertCheckState = 'class=\"checkbox_disabled\" ';
		}
		
		if(row.operation){
			return '<table width="100%" class="borno"><tr width="100%"><td width="33%"><input type="checkbox" onClick="stopProp(event)" id="checkbox'+rowIndex+'" '+messCheckState+' enableSetContent="'+messEnableSetContent+'"  class="alarmCheckBox" name="checkboxMessage" value="'+row.alarmRulesID+'" style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;<a> 短信&nbsp;&nbsp;</a><a id='+row.alarmRulesID+' style="vertical-align:middle;" name="message" class="ico-edit icon-edit light_blue lacate-none"></a></td>'
			+'<td width="33%"><input type="checkbox" onClick="stopProp(event)" id="checkbox'+rowIndex+'" '+emailCheckState+' enableSetContent="'+emailEnableSetContent+'" class="alarmCheckBox" name="checkboxEmail" value="'+row.alarmRulesID+'" style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;<a>邮件&nbsp;&nbsp;</a><a id='+row.alarmRulesID+' style="vertical-align:middle;" name="email"  class="ico-edit icon-edit light_blue lacate-none"></a></td>'
			+'<td width="34%"><input type="checkbox" onClick="stopProp(event)" id="checkbox'+rowIndex+'" '+alertCheckState+' enableSetContent="'+alertEnableSetContent+'" class="alarmCheckBox" name="checkboxAlert" value="'+row.alarmRulesID+'" style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;<a>Alert&nbsp;&nbsp;</a><a id='+row.alarmRulesID+' style="vertical-align:middle;" name="alert" class="ico-edit icon-edit light_blue lacate-none"></a></td>';
		}else{
			return '<table width="100%" class="borno"><tr width="100%"><td width="33%"><input type="checkbox"  style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a> 短信&nbsp;&nbsp;&nbsp;&nbsp;</a><a style="vertical-align:middle;"class="ico-edit icon-edit light_blue lacate-none"></a></td>'
			+'<td width="33%"><input type="checkbox" style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a>邮件&nbsp;&nbsp;&nbsp;&nbsp;</a><a style="vertical-align:middle;" class="ico icon-edit light_blue lacate-none"></a></td>'
			+'<td width="34%"><input type="checkbox" style="vertical-align:middle;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a>Alert&nbsp;&nbsp;&nbsp;&nbsp;</a><a style="vertical-align:middle;" class="ico icon-edit light_blue lacate-none"></a></td>';
		}
	}

	function format(value,row,rowIndex){
		return '<a href="javascript:void(0)" onClick="openDialog('+row.id+')"> '+ value + ' </a>';
	}
	
	function openDialog(userId){
		alert(userId);
		oc.resource.loadScript('resource/module/resource-management/discresource/js/dialogAlarm.js',function(){
			oc.demo.dialogAlarm.open(userId);
		});
	}
	
	var openSendWayDialog = function(event,alarmRulesID,type){
		stopProp(event);
		
		oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/alarmRulesSet.js',function(){
			oc.alarmRulesSet.open(alarmRulesID,type,dataReload);
		});
	}

	}
	dataReload = function (){
		dataReloadMethod();
	}
	function dataReloadMethod(){
		editAlarmRule_Mess = new Array();
		editAlarmRule_Email = new Array();
		editAlarmRule_Alert = new Array();
		datagrid.reLoad();
	}
	//记录修改过启用状态的告警规则,不记录偶数次点击的告警规则
	function pushIdToArr(id,checkState,array){
		var arrayTemp = new Array();
		if(array.length==0){
			arrayTemp.push({alarmRuleId:id,checkState:checkState});
		}else{
			var flag = true;
			for(var i=0;i<array.length;i++){
				if(id!=array[i].alarmRuleId){
//					array[i] = {alarmRuleId:id,checkState:checkState};
					arrayTemp.push(array[i]);
				}else{
					flag = false;
				}
			}
			if(flag){
				arrayTemp.push({alarmRuleId:id,checkState:checkState});
			}
		}
		
		return arrayTemp;
	}
	
	function saveAlarmSendSet(){
		//oc.ui.confirm('是否应用当前告警方式设置？',function(){
			//如果编辑过的发送方式都为空,不用提交请求
			if(0==editAlarmRule_Mess.length&&0==editAlarmRule_Email.length&&0==editAlarmRule_Alert.length){
				//alert('告警规则的启用状态无修改!');
			}else{

				var messageRul = "";
				var enableMessageChecked = "";
				var emailRul = "";
				var enableEmailChecked = "";
				var alertRul = "";
				var enableAlertChecked = "";
				
				for(var i=0;i<editAlarmRule_Mess.length;i++){
					   if(0==i){
				    	   messageRul = editAlarmRule_Mess[i].alarmRuleId;
				       }else{
				    	   messageRul += ","+editAlarmRule_Mess[i].alarmRuleId;
				       }
				}
				for(var i=0;i<editAlarmRule_Email.length;i++){
					   if(0==i){
						   emailRul = editAlarmRule_Email[i].alarmRuleId;
				       }else{
				    	   emailRul += ","+editAlarmRule_Email[i].alarmRuleId;
				       }
				}
				for(var i=0;i<editAlarmRule_Alert.length;i++){
				   if(0==i){
					   alertRul = editAlarmRule_Alert[i].alarmRuleId;
			       }else{
			    	   alertRul += ","+editAlarmRule_Alert[i].alarmRuleId;
			       }
				}
				div.find("input:checkbox:checked").each(function(e){
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
						if('success'==data.data){
							//alert('保存告警的启用状态成功!');
						}else{
							//alert('保存告警的启用状态失败!');
						}
						//保存过后,清空
						editAlarmRule_Mess = new Array();
						editAlarmRule_Email = new Array();
						editAlarmRule_Alert = new Array();
						//dataReloadMethod();
					}
				});
			}
			
		 // },function(){
			//});
	}
	
/**
 * 提供给外部引用的接口
 */
oc.resource.alarmrules={
	open:function(profileIdAlarm , profileTypeAlarm,domainIdAlarm,profileNameTypeAlarm){
		profileId = profileIdAlarm;
		profileType = profileTypeAlarm;
		domainId = domainIdAlarm;
		//profileNameTypeAlarm  0:默认策略,1:自定义策略,2:个性化策略  ,3:其他模块调用,无权限限制,返回所有用户,4:其他模块调用,也根据域过滤告警接收人
		clType=profileNameTypeAlarm;
		profileNameType = profileNameTypeAlarm;
		//openState = 1  支持编辑,type = 2  只能查看
		openState = 1;
		//queryType  partQuery本模块调用(根据域对资源的权限过滤), allQuery其他模块调用,sourceLimitQuery根据用户对资源的权限过滤
		queryType = 'partQuery';
		
		alarmLevelStr = [{content:'资源不可用（级别：致命）',level:'down',checked:true},
			{content:'资源满足橙色阈值（级别：严重）',level:'metric_error',checked:false},
			{content:'资源满足黄色阈值（级别：警告）',level:'metric_warn',checked:false},
			//{content:'资源的指标未知时报警（级别：未知）',level:'metric_unkwon',checked:false},
			{content:'资源恢复可用（级别：正常）',level:'metric_recover',checked:false},
			{content:'资源指标恢复正常（级别：正常）',level:'perf_metric_recover',checked:false}];
		
		return open();
	},
	updateDomainId:function(domainIdAlarm){
//		domainId = '';
//		for(var i=0;i<domainIdAlarm.length;i++){
//			if(null==domainId || ""==domainId){
//				domainId = domainIdAlarm[i];
//			}else{
//				domainId += ','+domainIdAlarm[i];
//			}
//		}
		if(null!=domainIdAlarm && domainIdAlarm.length>0){
			domainId = domainIdAlarm.join(',');
		}
		this.datagridReloadWithNewDomainId();
		
	},
	datagridReloadWithNewDomainId:function(){
		//domainId修改,默认策略域减少,告警规则,清除相应信息
		oc.util.ajax({
			successMsg:null,
			url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/profileAlarmRules.htm'),
			data:{domainIdArr:domainId,profileId:profileId,profileType:profileType,queryType:queryType,profileNameType:profileNameType},
			success:function(data){
				//domainId更新,datagrid刷新
				var alarmRulesEnableSet = data.data.alarmRules;
				
				if(null!=alarmRulesEnableSet && alarmRulesEnableSet.length>0){
					for(var i=0;i<alarmRulesEnableSet.length;i++){
						var targetAlarmRules = alarmRulesEnableSet[i];
						
						for(var mess=0;mess<editAlarmRule_Mess.length;mess++){
							if(editAlarmRule_Mess[mess].alarmRuleId==targetAlarmRules.alarmRulesID){
								targetAlarmRules.messEnable = editAlarmRule_Mess[mess].checkState;
							}
						}
						for(var email=0;email<editAlarmRule_Email.length;email++){
							if(editAlarmRule_Email[email].alarmRuleId==targetAlarmRules.alarmRulesID){
								targetAlarmRules.emailEnable = editAlarmRule_Email[email].checkState;
							}
						}
						for(var alert=0;alert<editAlarmRule_Alert.length;alert++){
							if(editAlarmRule_Alert[alert].alarmRuleId==targetAlarmRules.alarmRulesID){
								targetAlarmRules.alertEnable = editAlarmRule_Alert[alert].checkState;
							}
						}
					}
				}
				
				var localData = {"data":{"startRow":data.data.startRow,"rowCount":data.data.rowCount,"totalRecord":data.data.totalRecord,
	    			"rows":alarmRulesEnableSet},"code":200};
	    		datagrid.selector.datagrid('loadData',localData);
			}
		})
	}
	,
	bizOpen:function(profileIdAlarm,managerId){
		profileId = profileIdAlarm;
		profileType = 'biz_profile';
		domainId = -1;
		profileNameType = 3;
		openState = 1;
		queryType = 'allQuery';
		
		alarmLevelStr = [{content:'业务不可用（级别：致命）',level:'down',checked:true},
		     			{content:'业务满足橙色阈值（级别：严重）',level:'metric_error',checked:false},
		    			{content:'业务满足黄色阈值（级别：警告）',level:'metric_warn',checked:false},
		    			//{content:'业务的指标未知时报警（级别：未知）',level:'metric_unkwon',checked:false},
		    			{content:'业务恢复可用（级别：正常）',level:'metric_recover',checked:false},
		    			{content:'业务指标恢复正常（级别：正常）',level:'perf_metric_recover',checked:false}];
		
		return open(managerId);
	}
	,
	netFlowOpen:function(profileIdAlarm,instanceIdArr){
		profileId = profileIdAlarm;
		profileType = 'netFlow';
		domainId = -1;
		profileNameType = 4;
		openState = 1;
		queryType = 'partQuery';
		
		alarmLevelStr = [{content:'资源满足橙色阈值（级别：严重）',level:'down',checked:true},
		                 {content:'资源满足黄色阈值（级别：警告）',level:'metric_error',checked:false}];
		
		if(null!=instanceIdArr && instanceIdArr.length>0){
			var instanceIds = instanceIdArr.join(',');
			oc.util.ajax({
				successMsg:null,
				url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/getDomainIdByinstanceId.htm'),
				data:{instanceIds:instanceIds},
				success:function(data){
					if(data.code == 200){
						if(null!=data.data && data.data.length>0){
							domainId = data.data.join(',');
						}
					}
					return open();
				}
			})
		}else{
			//如果未传资源id,则显示全部人员
			profileNameType = 3;
			queryType = 'allQuery';
			
			return open();
		}
		
		
	}
	,
	openDataCheck:function(profileIdAlarm , profileTypeAlarm,domainIdAlarm,profileNameTypeAlarm){
		profileId = profileIdAlarm;
		profileType = profileTypeAlarm;
		domainId = domainIdAlarm;
		profileNameType = profileNameTypeAlarm
		queryType = 'partQuery';
		//type = 2,只能查看
		openState = 2;
		
		alarmLevelStr = [{content:'资源不可用（级别：致命）',level:'down',checked:true},
		     			{content:'资源满足橙色阈值（级别：严重）',level:'metric_error',checked:false},
		    			{content:'资源满足黄色阈值（级别：警告）',level:'metric_warn',checked:false},
		    			//{content:'资源的指标未知时报警（级别：未知）',level:'metric_unkwon',checked:false},
		    			{content:'资源恢复可用（级别：正常）',level:'metric_recover',checked:false},
		    			{content:'资源指标恢复正常（级别：正常）',level:'perf_metric_recover',checked:false}];
		
		return open();
	},
	saveAlarmSendSet:function(){
		saveAlarmSendSet();
	},
	reSetPage:function(){
		editAlarmRule_Mess = new Array();
		editAlarmRule_Email = new Array();
		editAlarmRule_Alert = new Array();
	},
	ifAlarmRuleSendEnableChange:function(){
		if(0==editAlarmRule_Mess.length&&0==editAlarmRule_Email.length&&0==editAlarmRule_Alert.length){
			return false;
		}else{
			return true;
		}
	}
};

})(jQuery);
function stopProp(event){
	event.stopPropagation();
}


