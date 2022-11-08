$(function () {
	//命名空间
	oc.ns('oc.module.vm.report.template.detail.pickgrid');
	//全局变量统一存储
	var globalVariableData = {};
	
	oc.module.vm.report.template.detail.pickgrid = {
			open : function(){

				globalVariableData.curIsUpdateTemplate = false;
				globalVariableData.mainDialog = $('<div/>').dialog({
					title : '定制报告模板',
					href : oc.resource.getUrl('resource/module/vm/report/vm_report_template_detail.html'),
					width : 1050,
					height : 585,
					onLoad : function() {
							init();
					},
					onClose : function(){
						globalVariableData.mainDialog.panel('destroy');
					},
					onDestroy : function(){
						globalVariableData = {};
					},
					buttons:[{
				    	text:'完成',
				    	iconCls:"fa fa-check-circle",
				    	handler:function(){
				    		
				    		var emailAddressInputValue = $('#emailAddressId').val();
				    		emailAddressInputValue = emailAddressInputValue.replace(/\s+/g,"");
				    		
				    		var reportTemplateName = $('#reportTemplateNameInput').val().trim();
				    		
				    		if(!reportTemplateName){
				    			alert('请输入报表模板名称!');
				    			return;
				    		}
				    		
				    		//获取email文件格式
				    		var emailFormat = '';
		    				globalVariableData.templateMain.find('.templateEmailTypeClass:checked').each(function(){
								 emailFormat += $(this).val() + ',';
							});
							if(emailFormat != ''){
								emailFormat = emailFormat.substring(0,emailFormat.length - 1);
							}
							
							var directoryData = getSortDirectoryData();
							
				    		if(!globalVariableData.directory || !directoryData || directoryData.length <= 0){
				    			alert('未设置模板内容!');
				    			return;
				    		}
				    		
				    		if(emailAddressInputValue && !validateEmail(emailAddressInputValue)){
				    			return;
				    		}
				    		
				    		if(globalVariableData.curSelectTemplateEmailStatus == 1){
		    						var comfirmDialog = $('<div/>').dialog({
									    title: '确认保存',
									    width: 220,
									    height: 120,
									    content:'邮件订阅未启用,是否继续保存?',
									    modal: true,
									    closable:false,
									    buttons:[{
									    	text:'确定',
									    	iconCls:"fa fa-check-circle",
											handler:function(){
												addTemplateToDatabase(emailAddressInputValue,reportTemplateName,emailFormat);
												comfirmDialog.panel('destroy');
											}
									    },{
									    	text:'取消',
									    	iconCls:"fa fa-times-circle",
									    	handler:function(){
									    		//取消
												comfirmDialog.panel('destroy');
									    	}
									    }]
									});
				    		}else{
				    			if(!emailAddressInputValue){
									alert('没有设置邮件地址不能启用邮件订阅!');
									return;
								}if(emailFormat == ''){
									alert('没有设置报告文件格式不能启用邮件订阅!');
									return;
								}else{
								    addTemplateToDatabase(emailAddressInputValue,reportTemplateName,emailFormat);
								}
				    		}
				    		
				    	}
				    }]
				});
			},
			update:function(templateId){
				//修改模板
				globalVariableData.curIsUpdateTemplate = true;
				oc.util.ajax({
					  successMsg:null,
					  url: oc.resource.getUrl('portal/report/reportTemplate/getTemplateDetailByTemplateId.htm'),
					  data:{templateId:templateId},
					  success:function(data){
						  
						  if(data.data){
							    var template = data.data;
								globalVariableData.mainDialog = $('<div/>').dialog({
									title : '修改' + template.reportTemplateName + '报告模板',
									href : oc.resource.getUrl('resource/module/vm/report/vm_report_template_detail.html'),
									width : 1050,
									height : 585,
									onLoad : function() {
										init();
										updateTemplateInitData(template);
									},
									onClose : function(){
										globalVariableData.mainDialog.panel('destroy');
									},
									onDestroy : function(){
										globalVariableData = {};
									},
									buttons:[{
								    	text:'完成',
								    	iconCls:"fa fa-check-circle",
								    	handler:function(){
								    		
								    		var emailAddressInputValue = $('#emailAddressId').val();
								    		emailAddressInputValue = emailAddressInputValue.replace(/\s+/g,"");
								    		
								    		var reportTemplateName = $('#reportTemplateNameInput').val().trim();

								    		if(!reportTemplateName){
								    			alert('请输入报表模板名称!');
								    			return;
								    		}
								    		
								    		//获取email文件格式
								    		var emailFormat = '';
						    				globalVariableData.templateMain.find('.templateEmailTypeClass:checked').each(function(){
												 emailFormat += $(this).val() + ',';
											});
											if(emailFormat != ''){
												emailFormat = emailFormat.substring(0,emailFormat.length - 1);
											}
								    													
								    		if(emailAddressInputValue && !validateEmail(emailAddressInputValue)){
								    			return;
								    		}
								    		
						    				var directoryData = null;
				    						if(!globalVariableData.directory){
				    							directoryData = template.directoryList;
				    						}else{
												switch(globalVariableData.curType){
													case globalVariableData.vmPerformanceReportType: directoryData = makeDirectoryPerformanceData();break;
													case globalVariableData.vmAlarmReportType: directoryData = makeDirectoryAlarmData();break;
													
													default : break;return;
												
												}
												
												if(directoryData == null || directoryData.length <= 0){
													alert('至少创建一条章节!');
													return;
												}
				    						}
								    		
    						    			if(!emailAddressInputValue && globalVariableData.curSelectTemplateEmailStatus == 0){
												alert('没有设置邮件地址不能启用邮件订阅!');
												return;
											}if(emailFormat == '' && globalVariableData.curSelectTemplateEmailStatus == 0){
												alert('没有设置报告文件格式不能启用邮件订阅!');
												return;
											}
								    		
											
											//添加模板
											var cycle = globalVariableData.curSelectTemplateCycle;
											var thirdGenerateTime = null;
											if((cycle == 2 || cycle == 3) && globalVariableData.generateTimeThird){
												thirdGenerateTime = globalVariableData.generateTimeThird.jq.combobox('getValue');
											}else{
												thirdGenerateTime = -1;
											}
											
											var beginTime = '';
											var endTime = null;
											if(globalVariableData.curType != globalVariableData.trendReportType && globalVariableData.curType != globalVariableData.analysisReportType){
									    		if(cycle == 2){
									    			//周类型
													var checkedTimes = $('.weekTimeScopeCheckboxClass:checked');
									    			for(var i = 0 ; i < checkedTimes.length ; i++){
									    				beginTime += $(checkedTimes[i]).val() + ",";
									    			}
									    			beginTime = beginTime.substring(0,beginTime.length - 1);
									    			endTime = '';
									    		}else if(cycle == 1){
									    			//日报
									    			beginTime = parseInt(globalVariableData.timeScopeDayFirstCombobox.jq.combobox('getValue')) + parseFloat(globalVariableData.timeScopeSubDayFirstCombobox.jq.combobox('getValue'));
									    			endTime = parseInt(globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue')) + parseFloat(globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('getValue'));
									    		}else{
									    			beginTime = globalVariableData.timeScopeDayFirstCombobox.jq.combobox('getValue');
									    			endTime = globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue');
									    		}
											}
											
											var updateTemplate = {
												    reportTemplateId:templateId,
													reportTemplateType:globalVariableData.reportTemplateTypeSelectNameCombobox.jq.combobox('getValue'),
													reportTemplateName:reportTemplateName,
													reportTemplateDomainId:globalVariableData.reportTemplateDomainSelectNameCombobox.jq.combobox('getValue'),
													reportTemplateCycle:cycle,
													reportTemplateStatus:globalVariableData.curSelectTemplateStatus,
													reportTemplateEmailStatus:globalVariableData.curSelectTemplateEmailStatus,
													reportTemplateEmailAddress:emailAddressInputValue,
													reportTemplateEmailFormat:emailFormat,
													directoryList:directoryData
											};
											
											if(globalVariableData.curType != globalVariableData.trendReportType && globalVariableData.curType != globalVariableData.analysisReportType){
												updateTemplate.reportTemplateBeginTime = beginTime;
												updateTemplate.reportTemplateEndTime = endTime;
												updateTemplate.reportTemplateFirstGenerateTime = globalVariableData.generateTimeFirst.jq.combobox('getValue');
												updateTemplate.reportTemplateSecondGenerateTime = globalVariableData.generateTimeSecond.jq.combobox('getValue');
												updateTemplate.reportTemplateThirdGenerateTime = thirdGenerateTime;
											}
											
											if(globalVariableData.curType != globalVariableData.businessReportType){
												updateTemplate.reportTemplateDomainId = globalVariableData.reportTemplateDomainSelectNameCombobox.jq.combobox('getValue');
											}

											//修改模板
											oc.util.ajax({
												  successMsg:null,
												  timeout:null,
												  url: oc.resource.getUrl('portal/report/reportTemplate/updateTemplate.htm'),
												  data:{template:updateTemplate},
												  success:function(data){
													  
													  if(data.data){
														  alert('修改模板成功!');
														  oc.vm.indexpage.editNavsublistItem(templateId,reportTemplateName,globalVariableData.curType);
														  globalVariableData.mainDialog.panel('destroy');
													  }else{
														  alert('修改模板失败!');
													  }
												  }
											});
											
								    		
								    	}
								    }]
								});
						  }else{
							  alert('获取模板数据失败!');
						  }
					  }
				});
			}
		};
	
	
	function init(){
		
		globalVariableData.vmPerformanceReportType = 8;
		
		globalVariableData.vmAlarmReportType = 9;
		
		globalVariableData.maxMetricSize = 5;
		
		globalVariableData.curType = globalVariableData.vmPerformanceReportType;
		
		globalVariableData.typeData = [{id:globalVariableData.vmPerformanceReportType,name:'性能报表(虚拟化)'},
			{id:globalVariableData.vmAlarmReportType,name:'告警统计(虚拟化)'}];
		
		globalVariableData.curSelectTemplateCycle = 1;
		
		globalVariableData.curSelectTemplateStatus = 0;
		
		globalVariableData.curSelectTemplateEmailStatus = 1;
		
		globalVariableData.isShowApartLeftPickGridInfo = false;
		
		globalVariableData.isShowMetricInfo = false;
		
		globalVariableData.isSetMetricInfo = false;
		
//		globalVariableData.selectChildInstanceTitle = '请选择子资源';
		
		globalVariableData.templateMain = $('#vm_report_template_detail_main');
		
		globalVariableData.comprehensiveType = globalVariableData.vmPerformanceReportType;
		
//		var centerContent = $('#vm_report_template_detail_main_content');
		
		var basicInfo = $('#vm_report_template_detail_basic_info');
		
		var contentSetting = $('#vm_report_template_detail_content_setting');
		
		var emailInfo = $('#vm_report_template_detail_email_info');
		
		//初始化基本信息
		var basicInfoForm = $('<form class="oc-form col1 h-pad-mar oc-table-ocformbg" id="basicInfoForm"></form>');
		var basicInfoTable = $('<table style="width:100%;" class="octable-pop"/>');
		basicInfoTable.append('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" style="width:15%">报告类型：</td><td><div><select id="reportTemplateTypeSelectName"></select></div></td></tr>');
		basicInfoTable.append('<tr class="form-group h-pad-mar" id="domainGroupSelectDiv"><td class="tab-l-tittle">域：</td><td><div><select id="reportTemplateDomainSelectName"></select></div></td></tr>');
		basicInfoTable.append('<tr class="form-group h-pad-mar"><td class="tab-l-tittle">报告名称：</td><td><div><input id="reportTemplateNameInput" type="text" name="reportTemplateInputName" maxLength=15 placeholder="输入长度不得长于15个字符" required/><span class="oc-valid-required-star">*</span></div></td></tr>');
		basicInfoTable.append('<tr class="form-group h-pad-mar"><td class="tab-l-tittle">报告周期：</td><td><div radio><label><input class="radioCheckedTemplateType" type="radio" name="templateCycle" value="1" checked="checked"/><a>日报</a></label>' + 
				'<label><input class="radioCheckedTemplateType" type="radio" name="templateCycle" value="2"/><a>周报</a></label>' + 
				'<label><input class="radioCheckedTemplateType" type="radio" name="templateCycle" value="3"/><a>月报</a></label>' +
				'</div></td></tr>');
		basicInfoTable.append('<tr class="form-group h-pad-mar" id="timeScopeId"><td class="tab-l-tittle">时间范围：</td><td><div>每天&emsp;&emsp;<select id="timeScopeDayFirst"></select>：<select id="timeScopeSubDayFirst"></select>&emsp;&emsp;至&emsp;&emsp;<select id="timeScopeDaySecond"></select>：<select id="timeScopeSubDaySecond"></select></div></td></tr>');
		basicInfoTable.append('<tr class="form-group h-pad-mar" id="generateTimeId"><td class="tab-l-tittle">生成时间：</td><td><div><select id="generateTimeDayFirst"></select><select id="generateTimeDaySecond"></select></div></td></tr>');
		basicInfoTable.append('<tr class="form-group h-pad-mar"><td class="tab-l-tittle">状态：</td><td><div radio><label><input class="templateStatusClass" type="radio" name="templateStatus" value="0" checked="checked"/>启用</label>' + 
				'<label><input class="templateStatusClass" type="radio" name="templateStatus" value="1"/>停用</label></div></td></tr>');
		
		basicInfoForm.append(basicInfoTable);
		
		//初始化报告内容
		var contentSettingInfo = $('<div class="oc-window-rightmenutwo" id="contentAccordionDiv"></div>');
		
		globalVariableData.performanceTitleSubContent = '<input type="checkbox" checked="checked" disabled="true"><label>汇总数据</label>' + 
	    	'<input type="checkbox" id="detailInfoCheckBox"><label>详细信息</label>';
		
		globalVariableData.performanceTitleContent = '<label>章节标题：</label><div><input id="directoryNameInputId" style="width:160px" type="text" maxLength=12/><span class="oc-valid-required-star">*</span>' + 
			globalVariableData.performanceTitleSubContent + '</div>';
		
		globalVariableData.alarmTitleSubContent = '<input type="checkbox" checked="checked" disabled="true"><label>汇总数据</label>' +
			'<input type="checkbox" id="detailInfoCheckBox"><label>详细告警信息</label>(' +
			'<input type="checkbox" class="alarmCheckBoxClass" id="alarmDeadlyId"><label>致命</label>' + 
			'<input type="checkbox" class="alarmCheckBoxClass" id="alarmSeriousId"><label>严重</label>' +
			'<input type="checkbox" class="alarmCheckBoxClass" id="alarmWarningId"><label>警告</label>)';
		
		globalVariableData.alarmTitleContent = '<label>章节标题：</label><div><input id="directoryNameInputId" style="width:160px" type="text" maxLength=12/><span class="oc-valid-required-star">*</span>' + 
			globalVariableData.alarmTitleSubContent + '</div>';
			
		globalVariableData.topnTitleSubContent = '<label><input type="radio" id="topnPerformanceType" name="topnReportRadioType"  value="1"  checked="checked"/>性能</label><label><input type="radio" id="topnAlarmType" name="topnReportRadioType"  value="2"/>告警</label>&emsp;&emsp;<label>N : </label><select id="topnCountSelect"></select>';
		
		globalVariableData.topnTitleContent = '<label>章节标题：</label><div><input id="directoryNameInputId" style="width:160px" type="text" maxLength=12/><span class="oc-valid-required-star">*</span>' + 
			globalVariableData.topnTitleSubContent + '</div>';
		
		globalVariableData.availabilitySubContent = '<input type="checkbox" checked="checked" disabled="true"><label>汇总数据</label>' + 
	    	'<input type="checkbox" id="detailInfoCheckBox"><label>可用性告警</label>';
		
		globalVariableData.availabilityContent = '<label>章节标题：</label><div><input id="directoryNameInputId" style="width:160px" type="text" maxLength=12/><span class="oc-valid-required-star">*</span>' + 
			globalVariableData.availabilitySubContent + '</div>';
			
		globalVariableData.trendContent = '<label>章节标题：</label><div><input id="directoryNameInputId" style="width:160px" type="text" maxLength=12/><span class="oc-valid-required-star">*</span></div>';
		
		globalVariableData.analysisSubContent = '指标取值：<label><input type="radio" class="analysisReportRadioClass" name="analysisReportRadioValueType"  value="0" checked="checked"/>平均值</label><label><input class="analysisReportRadioClass" type="radio" name="analysisReportRadioValueType"  value="1"/>最大值</label><label><input class="analysisReportRadioClass" type="radio" name="analysisReportRadioValueType"  value="2"/>最小值</label>';
		
		globalVariableData.analysisContent = '<label>章节标题：</label><div><input id="directoryNameInputId" style="width:160px" type="text" maxLength=12/><span class="oc-valid-required-star">*</span>' + globalVariableData.analysisSubContent + '</div>';
		
		globalVariableData.businessContent = '<label>章节标题：</label><div><input id="directoryNameInputId" style="width:160px" type="text" maxLength=12/><span class="oc-valid-required-star">*</span></div>';
		
		globalVariableData.comprehensiveContent = '<label>章节标题：</label><div style="width:80%"><input id="directoryNameInputId" style="width:160px" type="text" maxLength=12/><span class="oc-valid-required-star">*</span>' + 
			'<select id="reportDirectoryTypeSelectName"></select>' + 
			'<span id="comprehensiveTypeChangeSpan"><input type="checkbox" checked="checked" disabled="true"><label>汇总数据</label>' + 
		    '<input type="checkbox" id="detailInfoCheckBox"><label>详细信息</label></span></div>';
		
		contentSettingInfo.append('<div title="章节设置"><form class="oc-form"><div id="titleContentGroupId" class="form-group" style="margin:6px 0;"></div>' +		// col1
				'<div id="choseResourceCategorySelect" class="form-group"><label>资源类型：</label><div><select id="mainResourceSelect"></select><span class="oc-valid-required-star">*</span></div></div></form>' +
				'<div class="choose-sublist"><div><div class="win-titlebg">选择资源：'+
				'<input id="searchInstanceListInput" type="text" placeholder="已选类型中搜索IP,资源名称"/><a id="searchInstanceListButton" class="fa fa-search" style="position: relative;top: 0;left:4px"></a></div>' +
				'<div id="parentResourceInstanceGrid" style="margin-top:0px;width:620px;height:280px;float:left;"  class="tablist-bg"><div id="selectResourceInstancePickGrid" style="float:left"></div></div></div></div>' +
				'<div class="choose-sublistsecond"><div style="height:31px;line-height:31px; "><div class="win-titlebg" style="margin-bottom:-4px;">选择指标：<a id="resetMetricThresholdValueButton"></a></div><div id="metricSelectGridDiv" style="height:280px;width:287px;float:left; margin-top:8px;"><div id="selectMetricGrid"></div></div></div></div><div style="clear:both; margin-top:6px;"><a class="locate-right" id="addTemplateDirectoryButton"></a></div></div>' + 
				'<div title="目录"><div id="templateDirectoryDataGrid"></div></div>');
		
		//初始化邮件订阅
		var emailForm = $('<form class="oc-form col1 h-pad-mar oc-table-ocformbg"></form>');
		var emailTable = $('<table style=" width:100%;" class="octable-pop"/>');
		emailTable.append('<tr class="form-group h-pad-mar"><td class="tab-l-tittle">报告名称：</td><td><div><label id="reportTemplateEmailInputNameId"></label></div></td></tr>');
		emailTable.append('<tr class="form-group h-pad-mar"><td class="tab-l-tittle">邮件订阅：</td><td><div radio><label><input type="radio" class="templateEmailStatusClass" name="templateEmailStatus" value="0"/>启用</label>' + 
				'<label><input type="radio" name="templateEmailStatus" class="templateEmailStatusClass"  value="1" checked="checked"/>停用</label></div></td></tr>');
		emailTable.append('<tr class="form-group h-pad-mar"><td class="tab-l-tittle">收件人：</td><td><div><textarea style="resize: none;height:80px; width:435px;" placeholder="多个邮箱请用（“;”）分号隔开" id="emailAddressId" name="emailAddress" required></textarea></div></td></tr>');
		emailTable.append('<tr class="form-group h-pad-mar"><td class="tab-l-tittle">报告文件格式：</td><td><div radio><label><input type="checkbox" class="templateEmailTypeClass re-input-locate" value="1"/><label class="re-excel lacate-none" title="excel"></label></label>' + 
				'<label><input type="checkbox" class="templateEmailTypeClass re-input-locate" value="2"/><label class="re-word lacate-none" title="word"></label></label><label><input type="checkbox" class="templateEmailTypeClass re-input-locate" value="3"/><label class="re-pdf lacate-none" title="pdf"></label></label></div></td></tr>');
		
		emailForm.append(emailTable);
		
		globalVariableData.templateMain.layout({
			fit : true
		});
		
		emailInfo.hide();
		contentSetting.hide();
		basicInfo.append(basicInfoForm);
		emailInfo.append(emailForm);
		
		// 为导航按钮绑定点击事件
		globalVariableData.templateMain.find(".nav_account_btn").on('click', function(){
			emailInfo.hide();
			contentSetting.hide();
			basicInfo.show();
			baseInfoClick(basicInfo,contentSetting,emailInfo);
		});
		globalVariableData.templateMain.find(".nav_single_btn").on('click', function(){
			emailInfo.hide();
			basicInfo.hide();
			
			globalVariableData.templateMain.find('.oc-basic-information').removeClass('active');
			globalVariableData.templateMain.find('.oc-content-setting').addClass('active');
			globalVariableData.templateMain.find('.oc-email-subscribe').removeClass('active');
			globalVariableData.templateMain.find('#accounttwo_basic_id').hide();
			globalVariableData.templateMain.find('#accounttwo_content_id').show();
			globalVariableData.templateMain.find('#accounttwo_email_id').hide();
			
			if(contentSetting.find('.oc-window-rightmenutwo').length > 0){
				//已创建
				contentSetting.show();
				if(globalVariableData.titleContentGroup.html() == ''){
					resetAllContentSetting();
					switchTemplateType();
				}
			}else{
				//未创建
				contentSetting.show();
				contentSetting.append(contentSettingInfo);
				var nowSelected = 0;
				if(globalVariableData.curIsUpdateTemplate){
					nowSelected = 1;
				}
				globalVariableData.contentAccordion = $('#contentAccordionDiv').accordion({selected:nowSelected});
				globalVariableData.titleContentGroup = $('#titleContentGroupId');
				switchTemplateType();
				
			  	if(globalVariableData.curType == globalVariableData.businessReportType){
					$('#choseResourceCategorySelect').hide();
					globalVariableData.templateMain.find('.choose-sublistsecond').css('top','76px');
					$('#searchInstanceListInput').attr('placeholder','搜索业务名称|负责人');
			  	}
				
				$('#addTemplateDirectoryButton').linkbutton('RenderLB',{
					  text:'保存',
					  iconCls:"fa fa-check-circle",
					  onClick:function(){
						  
							switch(globalVariableData.curType){
								
								case globalVariableData.vmPerformanceReportType: addDirectoryPerformance();break;
								case globalVariableData.vmAlarmReportType: addDirectoryAlarm();break;
								default : break;return;
							
							}
							//注册拖曳排序功能
							registerSortable();
					  }
				});
				
				$('#searchInstanceListButton').on('click',function(e){
					if(!globalVariableData.currentResourceType){
						alert('请先选择资源类型!');
						return;
					}
					var searchContent = $('#searchInstanceListInput').val().trim();
					
					oc.util.ajax({
						  url: oc.resource.getUrl('portal/vm/vmReport/getVmResourceByType.htm'),
						  data:{type:globalVariableData.currentResourceType,domainId:globalVariableData.reportTemplateDomainSelectNameCombobox.jq.combobox('getValue')},
						  success:function(data){
							  if(data.data){
								  var dataLeft = [];
								  var dataRightSelected = globalVariableData.pickGrid.getRightRows();
								  
								  for(var i=0;i<data.data.length;i++){
									  var dataObjTemp = data.data[i];
									  var dataFlag = true;
									  
									  for(var x=0;x<dataRightSelected.length;x++){
										  if(dataRightSelected[x].id == dataObjTemp.id){
											  dataFlag = false;
										  }
									  }
									  if(dataFlag && searchContent){
										  if(!(dataObjTemp.showName.indexOf(searchContent)>-1)){
										  dataFlag = false;
										  }
									  }
									  if(dataFlag){
										  dataLeft.push(dataObjTemp);
									  }
								  }
								  setPickgridLeftData(dataLeft);
							  }
						  }
						  
					  });
					
//					var searchContent = $('#searchInstanceListInput').val().trim();
//					
//					if(searchContent == ''){
//						if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
//							setDatagridData(globalVariableData.pickGrid,globalVariableData.curLeftGridResourceInstanceList);
//						}else{
//							setPickgridLeftData(globalVariableData.curLeftGridResourceInstanceList);
//						}
//						return;
//					}
//					
//					var leftData = $.extend(true,[],globalVariableData.curLeftGridResourceInstanceList);
//					if(leftData == null || leftData == undefined || leftData.length <= 0){
//						return;
//					}
//					var resourceIdArray = '';
//					for(var i = 0 ; i < leftData.length ; i ++){
//						resourceIdArray += leftData[i].id + ",";
//					}
//					resourceIdArray = resourceIdArray.substring(0,resourceIdArray.length - 1);
//					//过滤资源实例
//					if(globalVariableData.curType == globalVariableData.businessReportType){
//						//业务报表
//						getBusinessResourceList(resourceIdArray,searchContent);
//					}else{
//						oc.util.ajax({
//							  url: oc.resource.getUrl('portal/report/reportTemplate/filterInstanceInfoByContent.htm'),
//							  data:{instanceIds:resourceIdArray,content:searchContent},
//							  success:function(data){
//								  
//								  if(data.data){
//				  						if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
//											setDatagridData(globalVariableData.pickGrid,data.data);
//										}else{
//											setPickgridLeftData(data.data);
//										}
//								  }else{
//									    alert('过滤资源实例失败!');
//								  }
//							  }
//						});
//					}
				});
				
				globalVariableData.currentResourceType = '';
				//渲染资源选择和指标选择的
				globalVariableData.comboTree = oc.ui.combotree({
					  selector:$('#mainResourceSelect'),
					  width:'200px',
					  placeholder:'请选择资源类型',
					  url:oc.resource.getUrl('portal/vm/vmReport/getVmResourceCategory.htm'),
					  filter:function(data){
						  return data.data;
					  },
					  onChange : function(newValue, oldValue){
							 if(newValue && ""!=newValue){
								 if(globalVariableData.isShowApartLeftPickGridInfo){
									  globalVariableData.currentResourceType = newValue;
									  oc.util.ajax({
										  url: oc.resource.getUrl('portal/vm/vmReport/getVmResourceByType.htm'),
										  data:{type:globalVariableData.currentResourceType,domainId:globalVariableData.reportTemplateDomainSelectNameCombobox.jq.combobox('getValue')},
										  success:function(data){
											  if(data.data){
												  var dataLeft = [];
												  var dataRight = [];
												  
												  for(var i=0;i<data.data.length;i++){
													  var dataFlag = false;
													  var rightResource = globalVariableData.curUpdateRowData.resourceIds.split(",");
													  for(var x=0;x<rightResource.length;x++){
														  if(data.data[i].id == rightResource[x]){
															  dataFlag = true;
															  break;
														  }
													  }
													  if(dataFlag){
														  dataRight.push(data.data[i]);
													  }else{
														  dataLeft.push(data.data[i]);
													  }
												  }
//												  globalVariableData.pickGrid.reload(dataLeft,'left');
//												  globalVariableData.pickGrid.reload(globalVariableData.curUpdateRowData.childComboValue,'right');
												  setPickgridLeftData(dataLeft);
												  setPickgridRightData(dataRight);
												  
//												  if(globalVariableData.metricDatagridData){
//														setDatagridData(globalVariableData.dataGrid,globalVariableData.metricDatagridData);
//													}else{
//														oc.util.ajax({
//															  url: oc.resource.getUrl('portal/vm/vmReport/getVmResourceMetricListByType.htm'),
//															  data:{type:globalVariableData.currentResourceType},
//															  success:function(data){
//																  globalVariableData.metricDatagridData = data.data;
//																  setDatagridData(globalVariableData.dataGrid,data.data);
//															  }
//														});
//													}
											  }
										  }
										  
									  });
									  
									  
									  globalVariableData.isShowApartLeftPickGridInfo = false;
									  globalVariableData.isShowMetricInfo = true;
								  }else{
									  globalVariableData.currentResourceType = newValue;
									  oc.util.ajax({
										  url: oc.resource.getUrl('portal/vm/vmReport/getVmResourceByType.htm'),
										  data:{type:globalVariableData.currentResourceType,domainId:globalVariableData.reportTemplateDomainSelectNameCombobox.jq.combobox('getValue')},
										  success:function(data){
//											  globalVariableData.pickGrid.reload(data.data,'left');
//											  globalVariableData.pickGrid.reload([],'right');
											  setPickgridLeftData(data.data);
											  setPickgridRightData([]);
											  
											  setDatagridData(globalVariableData.dataGrid,[]);
										  }
										  
									  });
								  }
							  }else{
								  globalVariableData.currentResourceType = "";
								  if(globalVariableData.pickGrid){
//									  globalVariableData.pickGrid.reload([],'left');
//									  globalVariableData.pickGrid.reload([],'right');
									  setPickgridLeftData([]);
									  setPickgridRightData([]);
								  }
								  if(globalVariableData.dataGrid){
									  setDatagridData(globalVariableData.dataGrid,[]);
								  }
								  
//								  globalVariableData.currentResourceType = "vCenter";
//								  oc.util.ajax({
//									  url: oc.resource.getUrl('portal/vm/vmReport/getVmResourceByType.htm'),
//									  data:{type:globalVariableData.currentResourceType},
//									  success:function(data){
//										  globalVariableData.pickGrid.reload(data.data,'left');
//										  
//										  globalVariableData.pickGrid.reload([],'right');
//										  setDatagridData(globalVariableData.dataGrid,[]);
//									  }
//									  
//								  });
							  }
					  }
				});
				
				globalVariableData.selectChildResourceCombobox = oc.ui.combobox({
					  selector:$('#childResourceSelect'),
					  width:'160px',
					  selected:false,
					  placeholder:globalVariableData.selectChildInstanceTitle,
					  data:null,
					  onChange : function(newValue, oldValue){}
				});
				
				//指标表格
				globalVariableData.dataGrid = oc.ui.datagrid({
					selector:'#selectMetricGrid',
					pagination:false,
					fitColumns:false,
					noDataMsg:'未选择数据！',
					checkOnSelect:false,
					selectOnCheck:false,
					columns:[[
					          {field:'id',checkbox:true},
					          {field:'radioId',title:'',width:28,hidden:true,
						        	 formatter:function(value,row,rowIndex){
				        			    return '<input type="radio" class="metricGridRadioClass" name="metricGridRadio" value="' + rowIndex + '" />';
						         	 }
				         	  },
					          {field:'name',title:'指标名称',width:160,ellipsis:true},
					          {field:'metricSort',title:'排序',width:84,hidden:true,
						        	 formatter:function(value,row,rowIndex){
					        		 	 if(value == 1){
					        		 	 	return '<span class="pop-arrow-down"></span>';
					        		 	 }else if(value == 0){
					        		 	 	return '<span class="pop-arrow-up"></span>'
					        		 	 }else{
					        			    return '<span>-</span>';
					        		 	 }
						         	 }
						      },
						      {field:'metricExpectValue',title:'期望值',width:84,hidden:true},
						      {field:'unit',hidden:true}
					]],
				    onLoadSuccess:function(data){
				    	 if(!globalVariableData.dataGrid){
				    	 	return;
				    	 }
				    	 if(globalVariableData.isSetMetricInfo){
				    		 //勾选指标及选中排序指标
				    		 var rows = data.rows;
				    		 if(globalVariableData.curType == globalVariableData.topnReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.topnReportType)){
					    		var metricIdList = globalVariableData.curUpdateRowData.metricIds.split(';');
				    		 	next : for(var i = 0 ; i < rows.length ; i ++){
					    			 for(var j = 0 ; j < metricIdList.length ; j ++){
					    				 var metricObject = JSON.parse(metricIdList[j]);
					    				 if(rows[i].id == metricObject.id){
						    				 var nowIndex = globalVariableData.dataGrid.selector.datagrid('getRowIndex',rows[i]);
						    				 var newData = $.extend(true,{},rows[i]);
					    				 	 newData.metricSort = parseInt(metricObject.sort);
											 globalVariableData.dataGrid.selector.datagrid('updateRow',{
												index:nowIndex,
												row:newData
											 });
					    					 globalVariableData.dataGrid.selector.datagrid('checkRow',nowIndex);
					    					 continue next;
					    				 }
					    				 
					    			 }
					    			 
					    		 }
				    		 }else if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
				    		 	var metricIdList = globalVariableData.curUpdateRowData.metricIds.split(';');
				    		 	next : for(var i = 0 ; i < rows.length ; i ++){
					    			 for(var j = 0 ; j < metricIdList.length ; j ++){
					    				 var metricObject = JSON.parse(metricIdList[j]);
					    				 if(rows[i].id == metricObject.id){
						    				 var nowIndex = globalVariableData.dataGrid.selector.datagrid('getRowIndex',rows[i]);
						    				 var newData = $.extend(true,{},rows[i]);
						    				 var nowReg = null;
						    				 if(newData.unit == '%'){
						    				 	nowReg = globalVariableData.regThresholdPercentageValue;
						    				 }else{
						    				 	nowReg = globalVariableData.regThresholdValue;
						    				 }
					    				 	 newData.metricExpectValue = '<input ' + nowReg + ' id="metricExpectInputId' + nowIndex + '" type="text" value="' + parseInt(metricObject.value) + '" style="width:40px"/>';
											 globalVariableData.dataGrid.selector.datagrid('updateRow',{
												index:nowIndex,
												row:newData
											 });
					    					 globalVariableData.dataGrid.selector.datagrid('checkRow',nowIndex);
					    					 continue next;
					    				 }
					    				 
					    			 }
					    			 
					    		 }
				    		 }else if(globalVariableData.curType == globalVariableData.trendReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.trendReportType)){
					    		 for(var i = 0 ; i < rows.length ; i ++){
				    				 if(rows[i].id == globalVariableData.curUpdateRowData.metricIds){
						    		 	 $('.metricGridRadioClass[value="' + globalVariableData.dataGrid.selector.datagrid('getRowIndex',rows[i]) + '"]').attr('checked','checked');
				    					 break;
				    				 }
					    		 }
				    		 }else{
				    		 	 var metricIdList = globalVariableData.curUpdateRowData.metricIds.split(',');
					    		 next : for(var i = 0 ; i < rows.length ; i ++){
					    			 
					    			 for(var j = 0 ; j < metricIdList.length ; j ++){
					    				 
					    				 if(rows[i].id == metricIdList[j]){
					    					 globalVariableData.dataGrid.selector.datagrid('checkRow',globalVariableData.dataGrid.selector.datagrid('getRowIndex',rows[i]));
					    					 continue next;
					    				 }
					    				 
					    			 }
					    			 
					    		 }
				    		 }
				    		 
				    		 globalVariableData.isSetMetricInfo = false;
				    		 
				    	 }else{
				    	 	 if(globalVariableData.curType == globalVariableData.vmAlarmReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.vmAlarmReportType)){
								 //告警报表指标默认全选
								 globalVariableData.dataGrid.selector.datagrid('checkAll');
				    	 	 }else if($('#topnAlarmType').attr('checked') == 'checked' && (globalVariableData.curType == globalVariableData.topnReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.topnReportType))){
				    	 	 	 globalVariableData.dataGrid.selector.datagrid('checkAll');
				    	 	 }else if(globalVariableData.curType == globalVariableData.businessReportType){
				    	 	 	 globalVariableData.dataGrid.selector.datagrid('checkAll');
				    	 	 }else{
					    	 	 if(globalVariableData.dataGrid.selector.datagrid('getRows').length > 0){
						    		 globalVariableData.dataGrid.selector.datagrid('checkRow',0);
					    	 	 }
				    	 	 }
				    	 }
				    	 
				     },
				     onCheck:function(rowIndex, rowData){
				     	if(globalVariableData.curType == globalVariableData.topnReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.topnReportType)){
					     	var newData = $.extend(true,{},rowData);
					     	if(newData.metricSort == -1){
						     	newData.metricSort = 1;
								globalVariableData.dataGrid.selector.datagrid('updateRow',{
									index:rowIndex,
									row:newData
								});
								//easyui bug修改后需再次选中
								globalVariableData.dataGrid.selector.datagrid('checkRow',rowIndex);
					     	}
				     	}
				     	if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
					     	var newData = $.extend(true,{},rowData);
					     	if(newData.metricExpectValue == ''){
			    				 var nowReg = null;
			    				 if(newData.unit == '%'){
			    				 	nowReg = globalVariableData.regThresholdPercentageValue;
			    				 }else{
			    				 	nowReg = globalVariableData.regThresholdValue;
			    				 }
						     	newData.metricExpectValue = '<input ' + nowReg + ' id="metricExpectInputId' + rowIndex + '" type="text" value="' + globalVariableData.cacheThresholdData[rowIndex] + '" style="width:40px"/>';
								globalVariableData.dataGrid.selector.datagrid('updateRow',{
									index:rowIndex,
									row:newData
								});
								//easyui bug修改后需再次选中
								globalVariableData.dataGrid.selector.datagrid('checkRow',rowIndex);
					     	}
				     	}
				     	
				     },
				     onCheckAll:function(rows){
				     	if(globalVariableData.curType == globalVariableData.topnReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.topnReportType)){
					     	var allRowData = $.extend(true,[],rows);
					     	for(var i = 0 ; i < allRowData.length ; i ++){
					     		var row = allRowData[i];
					     		if(row.metricSort == -1){
							     	row.metricSort = 1;
									globalVariableData.dataGrid.selector.datagrid('updateRow',{
										index:i,
										row:row
									});
									//easyui bug修改后需再次选中
									globalVariableData.dataGrid.selector.datagrid('checkRow',i);
					     		}
					     	}
				     	}
				     	if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
					     	var allRowData = $.extend(true,[],rows);
					     	for(var i = 0 ; i < allRowData.length ; i ++){
					     		var row = allRowData[i];
						     	if(row.metricExpectValue == ''){
					    				 var nowReg = null;
				    				 if(row.unit == '%'){
				    				 	nowReg = globalVariableData.regThresholdPercentageValue;
				    				 }else{
				    				 	nowReg = globalVariableData.regThresholdValue;
				    				 }
							     	row.metricExpectValue = '<input ' + nowReg + ' id="metricExpectInputId' + i + '" type="text" value="' + globalVariableData.cacheThresholdData[i] + '" style="width:40px"/>';
									globalVariableData.dataGrid.selector.datagrid('updateRow',{
										index:i,
										row:row
									});
									//easyui bug修改后需再次选中
									globalVariableData.dataGrid.selector.datagrid('checkRow',i);
						     	}
					     	}
				     	}
				     },
				     onUncheck:function(rowIndex,rowData){
				     	if(globalVariableData.curType == globalVariableData.topnReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.topnReportType)){
					     	var newData = $.extend(true,{},rowData);
					     	if(newData.metricSort != -1){
						     	newData.metricSort = -1;
								globalVariableData.dataGrid.selector.datagrid('updateRow',{
									index:rowIndex,
									row:newData
								});
								//easyui bug
								globalVariableData.dataGrid.selector.datagrid('uncheckRow',rowIndex);
					     	}
				     	}
				     	if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
					     	var newData = $.extend(true,{},rowData);
					     	if(newData.metricExpectValue != ''){
						     	newData.metricExpectValue = '';
								globalVariableData.dataGrid.selector.datagrid('updateRow',{
									index:rowIndex,
									row:newData
								});
								//easyui bug
								globalVariableData.dataGrid.selector.datagrid('uncheckRow',rowIndex);
							}
				     	}
				     },
				     onUncheckAll:function(rows){
				     	if(globalVariableData.curType == globalVariableData.topnReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.topnReportType)){
					     	var allRowData = $.extend(true,[],rows);
					     	for(var i = 0 ; i < allRowData.length ; i ++){
					     		var row = allRowData[i];
						     	if(row.metricSort != -1){
							     	row.metricSort = -1;
									globalVariableData.dataGrid.selector.datagrid('updateRow',{
										index:i,
										row:row
									});
									//easyui bug
									globalVariableData.dataGrid.selector.datagrid('uncheckRow',i);
						     	}
					     	}
				     	}
				     	if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
					     	var allRowData = $.extend(true,[],rows);
					     	for(var i = 0 ; i < allRowData.length ; i ++){
					     		var row = allRowData[i];
						     	if(row.metricExpectValue != ''){
							     	row.metricExpectValue = '';
									globalVariableData.dataGrid.selector.datagrid('updateRow',{
										index:i,
										row:row
									});
									//easyui bug
									globalVariableData.dataGrid.selector.datagrid('uncheckRow',i);
								}
					     	}
				     	}
				     },
				     onClickCell:function(index,field,value){
				     	if((globalVariableData.curType == globalVariableData.topnReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.topnReportType)) && (value == 0 || value == 1) && field == 'metricSort'){
					     	var newData = $.extend(true,{},globalVariableData.dataGrid.selector.datagrid('getRows')[index]);
					     	newData.metricSort = (value == 0 ? 1 : 0);
							globalVariableData.dataGrid.selector.datagrid('updateRow',{
								index:index,
								row:newData
							});
							//easyui bug修改后需再次选中
							globalVariableData.dataGrid.selector.datagrid('checkRow',index);
				     	}
				     }
				});
				
				//模板目录表格
				globalVariableData.directory = oc.ui.datagrid({
					selector:'#templateDirectoryDataGrid',
					pagination:false,
					fitColumns:false,
					noDataMsg:'未添加章节',
					singleSelect:true,
					rownumbers:true,
					columns:[[
						{field:'directoryId',hidden:true},
						{field:'directoryTitle',title:'章节标题',width:170},
						{field:'directoryTypeName',title:'章节类型',width:150,hidden:true},
						{field:'resourceType',title:'资源类型',width:170},
						{field:'resourceCount',title:'资源数量',width:150},
						{field:'metricCount',title:'指标数量',width:150},
						{field:'operation',title:'操作',width:120},
						{field:'resourceIds',hidden:true},
						{field:'metricIds',hidden:true},
						{field:'isShowDetail',hidden:true},
						{field:'childComboValue',hidden:true},
						{field:'parentComboValue',hidden:true},
						{field:'isShowDeadly',hidden:true},
						{field:'isShowSerious',hidden:true},
						{field:'isShowWarning',hidden:true},
						{field:'topNCount',hidden:true},
						{field:'directoryType',hidden:true},
						{field:'metricValueType',hidden:true}
					]],
					onSelect:function(rowIndex, rowData){
						if(globalVariableData.isSorting){
							return;
						}
						globalVariableData.curOpenDirectoryName = rowData.directoryTitle;
					  	globalVariableData.curUpdateRowData = rowData;
						
						if(globalVariableData.curType == globalVariableData.comprehensiveReportType){
							globalVariableData.reportDirectoryTypeSelectNameCombobox.jq.combobox('setValue',globalVariableData.curUpdateRowData.directoryType);
						}
						
					  	resetAllData();
					  	globalVariableData.isShowApartLeftPickGridInfo = true;
					  	globalVariableData.isSetMetricInfo = true;
				  		
					  	//赋值
					  	globalVariableData.comboTree.jq.combotree('setValue',rowData.parentComboValue);
//					  	globalVariableData.pickGrid.reload(globalVariableData.curUpdateRowData.parentComboValue,'left');
//					  	globalVariableData.pickGrid.reload(globalVariableData.curUpdateRowData.childComboValue,'right');
					  	
						  //设置资源类型
						if(globalVariableData.curUpdateRowData.childComboValue != null && globalVariableData.curUpdateRowData.childComboValue != undefined && globalVariableData.curUpdateRowData.childComboValue != ''){
							  globalVariableData.curChoseChildTypeValue = globalVariableData.curUpdateRowData.childComboValue;
						}
						
						//注入数据
						$('#directoryNameInputId').val(globalVariableData.curUpdateRowData.directoryTitle);
			
						if(globalVariableData.curType == globalVariableData.vmPerformanceReportType ){
							  if(globalVariableData.curUpdateRowData.isShowDetail == "true"){
								  $('#detailInfoCheckBox').attr('checked',"checked");
							  }else{
								  $('#detailInfoCheckBox').removeAttr('checked');
							  }
						}else if(globalVariableData.curType == globalVariableData.vmAlarmReportType ){
							  //修改目录
							  if(globalVariableData.curUpdateRowData.isShowDetail == "true"){
								  $('#detailInfoCheckBox').attr('checked',"checked");
							  }else{
								  $('#detailInfoCheckBox').removeAttr('checked');
							  }
							  
							  if(globalVariableData.curUpdateRowData.isShowDeadly == 'true'){
								  $('#alarmDeadlyId').attr('checked',"checked");
							  }else{
								  $('#alarmDeadlyId').removeAttr('checked');
							  }
							  
							  if(globalVariableData.curUpdateRowData.isShowSerious == 'true'){
								  $('#alarmSeriousId').attr('checked',"checked");
							  }else{
								  $('#alarmSeriousId').removeAttr('checked');
							  }
							  
							  if(globalVariableData.curUpdateRowData.isShowWarning == 'true'){
								  $('#alarmWarningId').attr('checked',"checked");
							  }else{
								  $('#alarmWarningId').removeAttr('checked');
							  }
							  
						}
							  
						globalVariableData.contentAccordion.accordion('select',0);
					
					},
					onAppendRow:function(){
						refreshDeleteOperate();
					},
					onUpdateRow:function(){
						refreshDeleteOperate();
					}
				});
				
			    switch(globalVariableData.curType){
		
							case globalVariableData.topnReportType: showOrHideField(globalVariableData.dataGrid,'metricSort','show');break;
							case globalVariableData.trendReportType: showOrHideField(globalVariableData.dataGrid,'radioId','show');
								showOrHideField(globalVariableData.dataGrid,'id','hide');break;
							case globalVariableData.analysisReportType: {
								showOrHideField(globalVariableData.dataGrid,'metricExpectValue','show');
								$('#resetMetricThresholdValueButton').linkbutton('RenderLB',{
									  text:'恢复默认',
									  iconCls:"fa fa-check-circle",
									  onClick:function(){
									  		resetMetircThresholdValue();
									  }
								});
								break;
							}
							case globalVariableData.businessReportType : 
								showOrHideField(globalVariableData.directory,'resourceType','hide');
								globalVariableData.directory.selector.datagrid('getColumnOption','resourceCount').title = '业务数量';
								globalVariableData.directory.selector.datagrid();
								break;
							case globalVariableData.comprehensiveReportType:showOrHideField(globalVariableData.directory,'directoryTypeName','show');break;
							default:break;
							
			    }
				if(globalVariableData.curIsUpdateTemplate){
					for(var i = 0 ; i < globalVariableData.curUpdateDirectoryDataArray.length ; i ++){
						globalVariableData.directory.selector.datagrid('appendRow',globalVariableData.curUpdateDirectoryDataArray[i]);
					}
					//注册拖拽功能
					registerSortable();
				}
			}
		
		
		});
		
		globalVariableData.templateMain.find(".nav_batch_btn").on('click', function(){
			basicInfo.hide();
			contentSetting.hide();
			emailInfo.show();
			globalVariableData.templateMain.find('.oc-basic-information').removeClass('active');
			globalVariableData.templateMain.find('.oc-content-setting').removeClass('active');
			globalVariableData.templateMain.find('.oc-email-subscribe').addClass('active');
			globalVariableData.templateMain.find('#accounttwo_basic_id').hide();
			globalVariableData.templateMain.find('#accounttwo_content_id').hide();
			globalVariableData.templateMain.find('#accounttwo_email_id').show();
			
			//报告名称和模板输入名称一样
			$('#reportTemplateEmailInputNameId').text($('#reportTemplateNameInput').val());
		});
		
		//注册类型改变事件
		globalVariableData.templateMain.find('.radioCheckedTemplateType').on('change',function(e){
			 if($(e.target).prop('checked')){
				 globalVariableData.curSelectTemplateCycle = $(e.target).val();
				 switchTemplateCycle($(e.target).attr('value'));
			 }
		});
		
		globalVariableData.templateMain.find('.templateStatusClass').on('change',function(e){
			 if($(e.target).prop('checked')){
				 globalVariableData.curSelectTemplateStatus = $(e.target).val();
			 }
		});
		
		globalVariableData.templateMain.find('.templateEmailStatusClass').on('change',function(e){
			 if($(e.target).prop('checked')){
				 globalVariableData.curSelectTemplateEmailStatus = $(e.target).val();
			 }
		});
		
		var allTemplateType = [{id:globalVariableData.vmPerformanceReportType,name:'性能报表(虚拟化)'},
				{id:globalVariableData.vmAlarmReportType,name:'告警统计(虚拟化)'}];
			
		
		//渲染组件
		globalVariableData.reportTemplateTypeSelectNameCombobox = oc.ui.combobox({
			  selector:$('#reportTemplateTypeSelectName'),
			  width:'150px',
			  placeholder:false,
			  selected:false,
			  value:globalVariableData.vmPerformanceReportType,
			  data:allTemplateType,
			  onChange : function(newValue, oldValue){
  				  //切换类别
				  globalVariableData.curType = parseInt(newValue);
			  	  //检查是否有业务模块权限
			  	  if(newValue == globalVariableData.businessReportType && !globalVariableData.isHaveBusinessAuthor && !globalVariableData.curIsUpdateTemplate){
			  	  	  
	  	  	  			var comfirmDialog = $('<div/>').dialog({
						    title: '确认消息',
						    width: 220,
						    height: 120,
						    content:'业务管理模块未授权！',
						    modal: true,
						    closable:false,
						    buttons:[{
						    	text:'确定',
						    	iconCls:"fa fa-check-circle",
								handler:function(){
						  	  	  	globalVariableData.reportTemplateTypeSelectNameCombobox.jq.combobox('setValue',oldValue);
									comfirmDialog.panel('destroy');
								}
						    },{
						    	text:'取消',
						    	iconCls:"fa fa-times-circle",
						    	handler:function(){
						  	  	  	globalVariableData.reportTemplateTypeSelectNameCombobox.jq.combobox('setValue',oldValue);
									comfirmDialog.panel('destroy');
						    	}
						    }]
						});
	  	  			    return;
			  	  }
				  
				  //清除更新信息
				  if(globalVariableData.curUpdateRowData){
					  globalVariableData.curUpdateRowData = null;
					  globalVariableData.curOpenDirectoryName = null;
				  }
				  //更新信息
				  globalVariableData.isShowApartLeftPickGridInfo = false;
				  if(globalVariableData.pickGrid){
//					  globalVariableData.pickGrid.reload([],'left');
//					  globalVariableData.pickGrid.reload([],'right');
					  setPickgridLeftData([]);
					  setPickgridRightData([]);
				  }
				  if(globalVariableData.dataGrid){
					  setDatagridData(globalVariableData.dataGrid,[]);
				  }
				  if(globalVariableData.directory){
					  setDatagridData(globalVariableData.directory,[]);
				  }
				  
				  
				  
			  	 if(oldValue == globalVariableData.trendReportType){
				  	 globalVariableData.templateMain.find('.radioCheckedTemplateType[value="1"]').parent().show();
 				  	 globalVariableData.templateMain.find('.radioCheckedTemplateType[value="1"]').attr('checked','checked');
				  	 globalVariableData.curSelectTemplateCycle = 1;
				  	 switchTemplateCycle(1);
				  	 globalVariableData.templateMain.find('.radioCheckedTemplateType[value="2"]').parent().find('a').text('周报');
				  	 globalVariableData.templateMain.find('.radioCheckedTemplateType[value="3"]').parent().find('a').text('月报');
			  	 }else if(oldValue == globalVariableData.analysisReportType){
			  	 	 globalVariableData.templateMain.find('.radioCheckedTemplateType[value="1"]').parent().find('a').text('日报');
 				  	 globalVariableData.templateMain.find('.radioCheckedTemplateType[value="1"]').attr('checked','checked');
				  	 globalVariableData.curSelectTemplateCycle = 1;
				  	 switchTemplateCycle(1);
				  	 globalVariableData.templateMain.find('.radioCheckedTemplateType[value="2"]').parent().find('a').text('周报');
				  	 globalVariableData.templateMain.find('.radioCheckedTemplateType[value="3"]').parent().find('a').text('月报');
				  	 
				  	 //需要切换资源选择组件
			  	 	 globalVariableData.pickGrid = null;
				  	 }else if(oldValue == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType){
				  		 //需要切换资源选择组件
				  	 	 globalVariableData.pickGrid = null;
				  	 }
			  	//需要切换资源选择组件
		  	 	 globalVariableData.pickGrid = null;
				  
				  if(globalVariableData.titleContentGroup != null){
					  globalVariableData.titleContentGroup.html('');
				  }
			  }
		});
		
		var domains = oc.index.getDomains();
		
		var firstDomains = -1;
		if(domains && domains.length > 0){
			firstDomains = domains[0].id;
		}
		
		globalVariableData.reportTemplateDomainSelectNameCombobox = oc.ui.combobox({
			  selector:$('#reportTemplateDomainSelectName'),
			  width:'105px',
			  placeholder:false,
			  selected:false,
			  value:firstDomains,
			  data:domains,
  			  onChange : function(newValue, oldValue){
				  //切换类别
  				  
  				//更新信息
				  globalVariableData.isShowApartLeftPickGridInfo = false;
				  if(globalVariableData.pickGrid){
//					  globalVariableData.pickGrid.reload([],'left');
//					  globalVariableData.pickGrid.reload([],'right');
					  setPickgridLeftData([]);
					  setPickgridRightData([]);
				  }
				  if(globalVariableData.dataGrid){
					  setDatagridData(globalVariableData.dataGrid,[]);
				  }
				  if(globalVariableData.directory){
					  setDatagridData(globalVariableData.directory,[]);
				  }
				  
				  //重置内容设置
				  if(globalVariableData.titleContentGroup){
					    alert('内容设置已被重置');
				  		globalVariableData.titleContentGroup.html('');
				  }				  
			  }
		});
		
		dayCycleValidate();
		
	}
	
	function baseInfoClick(){
		globalVariableData.templateMain.find('.oc-basic-information').addClass('active');
		globalVariableData.templateMain.find('.oc-content-setting').removeClass('active');
		globalVariableData.templateMain.find('.oc-email-subscribe').removeClass('active');
		globalVariableData.templateMain.find('#accounttwo_basic_id').show();
		globalVariableData.templateMain.find('#accounttwo_content_id').hide();
		globalVariableData.templateMain.find('#accounttwo_email_id').hide();
	}
	function reportSetClick(basicInfo,contentSetting,emailInfo){}
	function emailSetClick(){
		
	}
	//切换模板类型
	function switchTemplateCycle(type){
		globalVariableData.timeScopeDayFirstCombobox = null;
		globalVariableData.timeScopeDaySecondCombobox = null;		
		globalVariableData.generateTimeFirst = null;
		globalVariableData.generateTimeSecond = null;
		globalVariableData.generateTimeThird = null;
		if(type == 1 && globalVariableData.curType == globalVariableData.analysisReportType){
			 $('#timeScopeId').html('');
			 $('#timeScopeId').append('<td class="tab-l-tittle">时间范围：</td><td><div>每日 00:00 至 24:00</div></td>');
			 $('#generateTimeId').html('');
			 $('#generateTimeId').append('<td class="tab-l-tittle">生成时间：</td><td><div>次日 08:00</div></td>');
			 return;
		}else if(type == 2 && globalVariableData.curType == globalVariableData.analysisReportType){
			 $('#timeScopeId').html('');
			 $('#timeScopeId').append('<td class="tab-l-tittle">时间范围：</td><td><div>每周一 00:00 至 周日 24:00</div></td>');
			 $('#generateTimeId').html('');
			 $('#generateTimeId').append('<td class="tab-l-tittle">生成时间：</td><td><div>下周一 08:00</div></td>');
			 return;
		}else if(type == 3 && globalVariableData.curType == globalVariableData.analysisReportType){
			 $('#timeScopeId').html('');
			 $('#timeScopeId').append('<td class="tab-l-tittle">时间范围：</td><td><div>每月 1号 00:00 至 最后一天 24:00</div></td>');
			 $('#generateTimeId').html('');
			 $('#generateTimeId').append('<td class="tab-l-tittle">生成时间：</td><td><div>下月 1号 08:00</div></td>');
			 return;
		}else if(type == 2 && globalVariableData.curType == globalVariableData.trendReportType){
			 $('#timeScopeId').html('');
			 $('#timeScopeId').append('<td class="tab-l-tittle">时间范围：</td><td><div>每周 周一 00:00 至 周日 24:00</div></td>');
			 $('#generateTimeId').html('');
			 $('#generateTimeId').append('<td class="tab-l-tittle">生成时间：</td><td><div>下周 周一 08:00</div></td>');
			 return;
		}else if(type == 3 && globalVariableData.curType == globalVariableData.trendReportType){
			 $('#timeScopeId').html('');
			 $('#timeScopeId').append('<td class="tab-l-tittle">时间范围：</td><td><div>每月 1号 00:00 至 最后一天 24:00</div></td>');
			 $('#generateTimeId').html('');
			 $('#generateTimeId').append('<td class="tab-l-tittle">生成时间：</td><td><div>下月 1号 08:00</div></td>');
			 return;
		}else if(type == 1){
			//日报
			
			if(globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.reportDirectoryTypeSelectNameCombobox){
				globalVariableData.reportDirectoryTypeSelectNameCombobox.load(globalVariableData.typeData);
				globalVariableData.titleContentGroup.html('');
			}
			
			$('#timeScopeId').html('');
			$('#timeScopeId').append('<td class="tab-l-tittle">时间范围：</td><td><div>每天&emsp;&emsp;<select id="timeScopeDayFirst"></select>：<select id="timeScopeSubDayFirst"></select>&emsp;&emsp;至&emsp;&emsp;<select id="timeScopeDaySecond"></select>：<select id="timeScopeSubDaySecond"></select></div></td>');
			$('#generateTimeId').html('');
			$('#generateTimeId').append('<td class="tab-l-tittle">生成时间：</td><td><div><select id="generateTimeDayFirst"></select><select id="generateTimeDaySecond"></select></div></td>');
			
			dayCycleValidate();
			
		}else if(type == 2){
			//周报
			if(globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.reportDirectoryTypeSelectNameCombobox){
				var nowTypeData = $.extend(true,[],globalVariableData.typeData);
				nowTypeData.splice(nowTypeData.length - 1,0,{id:globalVariableData.trendReportType,name:'趋势报告'});
				globalVariableData.reportDirectoryTypeSelectNameCombobox.load(nowTypeData);
				globalVariableData.titleContentGroup.html('');
			}
			
			$('#timeScopeId').html('');
			$('#timeScopeId').append('<td class="tab-l-tittle">时间范围：</td><td><div>每周&emsp;&emsp;<input type="checkbox" class="weekTimeScopeCheckboxClass" checked="checked" id="weekTimeScopeCheckbox1" value="1"/><label>周一</label>&emsp;' + 
					'<input type="checkbox" class="weekTimeScopeCheckboxClass" checked="checked" id="weekTimeScopeCheckbox2" value="2"/><label>周二</label>&emsp;' + 
					'<input type="checkbox" class="weekTimeScopeCheckboxClass" checked="checked" id="weekTimeScopeCheckbox3" value="3"/><label>周三</label>&emsp;' + 
					'<input type="checkbox" class="weekTimeScopeCheckboxClass" checked="checked" id="weekTimeScopeCheckbox4" value="4"/><label>周四</label>&emsp;' + 
					'<input type="checkbox" class="weekTimeScopeCheckboxClass" checked="checked" id="weekTimeScopeCheckbox5" value="5"/><label>周五</label>&emsp;' +
					'<input type="checkbox" class="weekTimeScopeCheckboxClass" checked="checked" id="weekTimeScopeCheckbox6" value="6"/><label>周六</label>&emsp;' + 
					'<input type="checkbox" class="weekTimeScopeCheckboxClass" checked="checked" id="weekTimeScopeCheckbox7" value="7"/><label>周日</label>&emsp;</div></td>');
			$('#generateTimeId').html('');
			$('#generateTimeId').append('<td class="tab-l-tittle">生成时间：</td><td><div><select id="generateTimeWeekFirst"></select><select id="generateTimeWeekSecond"></select><select id="generateTimeWeekThird"></select></div></td>');
			
			$('.weekTimeScopeCheckboxClass').on('change',function(e){
				var nowId = $(e.target).attr("id");
				if($(e.target).attr('checked') == 'checked'){
					if(nowId == 'weekTimeScopeCheckbox7'){
						var oldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
						globalVariableData.generateTimeFirst.load([{id:1,name:'下周'}]);
						globalVariableData.generateTimeSecond.load([{id:1,name:'周一'},{id:2,name:'周二'},{id:3,name:'周三'}]);
						if(oldValue > 3){
							globalVariableData.generateTimeSecond.jq.combobox('setValue',1);
						}else{
							globalVariableData.generateTimeSecond.jq.combobox('setValue',oldValue);
						}
					}else{
						var nowIndex = parseInt(nowId.substring(nowId.length - 1,nowId.length));
						if(globalVariableData.generateTimeFirst.jq.combobox('getValue') == 0){
							  for(var i = 6 ; i > nowIndex ; i --){
								  if($('#weekTimeScopeCheckbox' + i).attr('checked') == 'checked'){
									  return;
								  }
							  }
							  changeSecondGenerateTimeSelect(nowIndex);
						}
					}
				}else{
					if(checkWeekScope()){
						if(nowId == 'weekTimeScopeCheckbox7'){
							globalVariableData.generateTimeFirst.load([{id:0,name:'当周'},{id:1,name:'下周'}]);
						}else{
							var nowIndex = parseInt(nowId.substring(nowId.length - 1,nowId.length));
							if(globalVariableData.generateTimeFirst.jq.combobox('getValue') == 0){
								  for(var i = 6 ; i > (nowIndex - 1) ; i --){
									  if($('#weekTimeScopeCheckbox' + i).attr('checked') == 'checked'){
										  return;
									  }
								  }
								  for(var i = nowIndex ; i > 0 ; i --){
									  if($('#weekTimeScopeCheckbox' + i).attr('checked') == 'checked'){
									  	  nowIndex = i;
										  break;
									  }
								  }
								  changeSecondGenerateTimeSelect(nowIndex);
							}
						}
					}else{
						alert('至少选择一天');
						$(e.target).attr('checked','checked');
						nowId = $(e.target).attr("id");
						if(nowId == 'weekTimeScopeCheckbox7'){
							var oldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
							globalVariableData.generateTimeFirst.load([{id:1,name:'下周'}]);
							globalVariableData.generateTimeSecond.load([{id:1,name:'周一'},{id:2,name:'周二'},{id:3,name:'周三'}]);
							if(oldValue > 3){
								globalVariableData.generateTimeSecond.jq.combobox('setValue',1);
							}else{
								globalVariableData.generateTimeSecond.jq.combobox('setValue',oldValue);
							}
						}else{
							var nowIndex = parseInt(nowId.substring(nowId.length - 1,nowId.length));
							if(globalVariableData.generateTimeFirst.jq.combobox('getValue') == 0){
								  for(var i = 6 ; i > nowIndex ; i --){
									  if($('#weekTimeScopeCheckbox' + i).attr('checked') == 'checked'){
										  return;
									  }
								  }
								  changeSecondGenerateTimeSelect(nowIndex);
							}
						}
					}
				}
				
			});
			
			globalVariableData.generateTimeFirst = oc.ui.combobox({
				  selector:$('#generateTimeWeekFirst'),
				  width:'85px',
				  placeholder:false,
				  selected:false,
				  value:1,
				  data:[{id:1,name:'下周'}],
				  onChange : function(newValue, oldValue){
				  	  newValue = parseInt(newValue);
					  if(newValue == 0){
						  
						  //判断复选框所选的最后一天
						  var lastUnCheckedIndex = 0;
						  for(var i = 6 ; i > 0 ; i --){
							  if($('#weekTimeScopeCheckbox' + i).attr('checked') == 'checked'){
								  lastUnCheckedIndex = i;
								  break;
							  }
						  }
						  changeSecondGenerateTimeSelect(lastUnCheckedIndex);
						  
					  }else{
					  	  var generateOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
						  globalVariableData.generateTimeSecond.load([{id:1,name:'周一'},{id:2,name:'周二'},{id:3,name:'周三'}]);
  						  if(generateOldValue > 3){
								globalVariableData.generateTimeSecond.jq.combobox('setValue',3);
						  }else{
								globalVariableData.generateTimeSecond.jq.combobox('setValue',generateOldValue);
						  }
					  }
				  }
			});
			
			globalVariableData.generateTimeSecond = oc.ui.combobox({
				  selector:$('#generateTimeWeekSecond'),
				  width:'85px',
				  placeholder:false,
				  selected:false,
				  value:1,
				  data:[{id:1,name:'周一'},{id:2,name:'周二'},{id:3,name:'周三'}]
			});
			
			var generateTimeThirdData = new Array();
			for(var i = 0 ; i < 24 ; i ++){
				generateTimeThirdData.push({
					id:i,
					name:i + ":00"
				});
			}
			
			globalVariableData.generateTimeThird = oc.ui.combobox({
				  selector:$('#generateTimeWeekThird'),
				  width:'85px',
				  placeholder:false,
				  selected:false,
				  value:8,
				  data:generateTimeThirdData
			});
			
		}else if(type == 3){
			//月报
			
			if(globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.reportDirectoryTypeSelectNameCombobox){
				var nowTypeData = $.extend(true,[],globalVariableData.typeData);
				nowTypeData.splice(nowTypeData.length - 1,0,{id:globalVariableData.trendReportType,name:'趋势报告'});
				globalVariableData.reportDirectoryTypeSelectNameCombobox.load(nowTypeData);
				globalVariableData.titleContentGroup.html('');
			}
			
			$('#timeScopeId').html('');
			$('#timeScopeId').append('<td class="tab-l-tittle">时间范围：</td><td><div>每月&emsp;&emsp;<select id="timeScopeMonthFirst"></select>&emsp;&emsp;至&emsp;&emsp;<select id="timeScopeMonthSecond"></select></div></td>');
			$('#generateTimeId').html('');
			$('#generateTimeId').append('<td class="tab-l-tittle">生成时间：</td><td><div><select id="generateTimeMonthFirst"></select><select id="generateTimeMonthSecond"></select><select id="generateTimeMonthThird"></select></div></td>');
			
			var timeScopeDayFirstData = new Array();
			for(var i = 1 ; i < 31 ; i ++){
				timeScopeDayFirstData.push({
					id:i,
					name:i + "号"
				});
			}
			
			globalVariableData.timeScopeDayFirstCombobox = oc.ui.combobox({
				  selector:$('#timeScopeMonthFirst'),
				  width:'85px',
				  placeholder:false,
				  selected:false,
				  value:1,
				  data:timeScopeDayFirstData,
				  onChange : function(newValue, oldValue){
				  		newValue = parseInt(newValue);
						var timeScopeDaySecondData = new Array();
						if(newValue == 30){
							var curTimeFirst = parseInt(globalVariableData.generateTimeFirst.jq.combobox('getValue'));
			  	      	    if(curTimeFirst == 0){
	  	    					var generateTimeSecondData = new Array();
								for(var i = 1 ; i < 11 ; i ++){
									generateTimeSecondData.push({
										id:i,
										name:i + "号"
									});
								}
								var generateTimeSecondOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
								globalVariableData.generateTimeSecond.load(generateTimeSecondData);
								if(generateTimeSecondOldValue > 10){
									globalVariableData.generateTimeSecond.jq.combobox('setValue',1);
								}else{
									globalVariableData.generateTimeSecond.jq.combobox('setValue',generateTimeSecondOldValue);
								}
			  	      	    }
							globalVariableData.generateTimeFirst.load([{id:1,name:'次月'}]);
						}
						timeScopeDaySecondData.push({
							id:-1,
							name:'最后一天'
						});
						for(var i = (newValue + 1) ; i < 32 ; i ++){
							timeScopeDaySecondData.push({
								id:i,
								name:i + "号"
							});
						}
						var scopeSecondOldValue = parseInt(globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue'));
						globalVariableData.timeScopeDaySecondCombobox.load(timeScopeDaySecondData);
						if(scopeSecondOldValue > newValue || scopeSecondOldValue == -1){
							globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',scopeSecondOldValue);
						}else{
							globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',newValue + 1);
						}
						
				  }
			});
			
			var timeScopeDaySecondData = new Array();
			timeScopeDaySecondData.push({
				id:-1,
				name:'最后一天'
			});
			for(var i = 2 ; i < 32 ; i ++){
				timeScopeDaySecondData.push({
					id:i,
					name:i + "号"
				});
			}
			
			globalVariableData.timeScopeDaySecondCombobox = oc.ui.combobox({
				  selector:$('#timeScopeMonthSecond'),
				  width:'85px',
				  placeholder:false,
				  selected:false,
				  value:-1,
				  data:timeScopeDaySecondData,
				  onChange : function(newValue, oldValue){
				  	  newValue = parseInt(newValue);
				  	  oldValue = parseInt(oldValue);
		  	    	  if(oldValue == 31 || oldValue == -1){
			  	    		var generateOldValue = globalVariableData.generateTimeFirst.jq.combobox('getValue');
			  	    		globalVariableData.generateTimeFirst.load([{id:0,name:'当月'},{id:1,name:'次月'}]);
			  	    		globalVariableData.generateTimeFirst.jq.combobox('setValue',generateOldValue);
			  	      }
			  	      if(newValue == 31 || newValue == -1){
			  	      		var curTimeFirst = parseInt(globalVariableData.generateTimeFirst.jq.combobox('getValue'));
			  	      	    if(curTimeFirst == 0){
	  	    					var generateTimeSecondData = new Array();
								for(var i = 1 ; i < 11 ; i ++){
									generateTimeSecondData.push({
										id:i,
										name:i + "号"
									});
								}
								var generateTimeSecondOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
								globalVariableData.generateTimeSecond.load(generateTimeSecondData);
								if(generateTimeSecondOldValue > 10){
									globalVariableData.generateTimeSecond.jq.combobox('setValue',1);
								}else{
									globalVariableData.generateTimeSecond.jq.combobox('setValue',generateTimeSecondOldValue);
								}
			  	      	    }
			  	    		globalVariableData.generateTimeFirst.load([{id:1,name:'次月'}]);
							
			  	      }
					  if(globalVariableData.generateTimeFirst.jq.combobox('getValue') == 0){
						  var timeScopeDaySecondData = new Array();
						  timeScopeDaySecondData.push({
							  id:-1,
							  name:'最后一天'
						  });
						  if(newValue == -1){
							  globalVariableData.generateTimeSecond.load(timeScopeDaySecondData);
							  globalVariableData.generateTimeSecond.jq.combobox('setValue',-1);
						  }else{
							  for(var i = (newValue + 1) ; i < 32 ; i ++){
								  timeScopeDaySecondData.push({
									  id:i,
									  name:i + "号"
								  });
							  }
							  globalVariableData.generateTimeSecond.load(timeScopeDaySecondData);
							  globalVariableData.generateTimeSecond.jq.combobox('setValue',newValue + 1);
						  }
					  }
				  }
			});
			
			globalVariableData.generateTimeFirst = oc.ui.combobox({
				  selector:$('#generateTimeMonthFirst'),
				  width:'85px',
				  placeholder:false,
				  selected:false,
				  value:1,
				  data:[{id:1,name:'次月'}],
				  onChange:function(newValue,oldValue){
				  	  newValue = parseInt(newValue);
					  if(newValue == 0){
						    var time = parseInt(globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue'));
							var generateTimeSecondData = new Array();
							for(var i = (time + 1) ; i < 32 ; i ++){
								generateTimeSecondData.push({
									id:i,
									name:i + "号"
								});
							}
							generateTimeSecondData.push({
								  id:-1,
								  name:'最后一天'
							});
							var genrateTimeOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
							globalVariableData.generateTimeSecond.load(generateTimeSecondData);
							if(genrateTimeOldValue > time){
								globalVariableData.generateTimeSecond.jq.combobox('setValue',genrateTimeOldValue);
							}else{
								globalVariableData.generateTimeSecond.jq.combobox('setValue',time + 1);
							}
					  }else{
						    var generateTimeSecondData = new Array();
						    for(var i = 1 ; i < 11 ; i ++){
								generateTimeSecondData.push({
									id:i,
									name:i + "号"
								});
							}
						    generateTimeSecondData.push({
								  id:-1,
								  name:'最后一天'
							});
							var genrateTimeOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
							globalVariableData.generateTimeSecond.load(generateTimeSecondData);
							if(genrateTimeOldValue > 10){
								globalVariableData.generateTimeSecond.jq.combobox('setValue',1);
							}else{
								globalVariableData.generateTimeSecond.jq.combobox('setValue',genrateTimeOldValue);
							}
					  }
				  }
			});
			
			var generateTimeSecondData = new Array();
			for(var i = 1 ; i < 11 ; i ++){
				generateTimeSecondData.push({
					id:i,
					name:i + "号"
				});
			}
			
			globalVariableData.generateTimeSecond = oc.ui.combobox({
				  selector:$('#generateTimeMonthSecond'),
				  width:'85px',
				  placeholder:false,
				  selected:false,
				  value:1,
				  data:generateTimeSecondData
			});
			
			var generateTimeThirdData = new Array();
			for(var i = 0 ; i < 24 ; i ++){
				generateTimeThirdData.push({
					id:i,
					name:i + ":00"
				});
			}
			
			globalVariableData.generateTimeThird = oc.ui.combobox({
				  selector:$('#generateTimeMonthThird'),
				  width:'85px',
				  placeholder:false,
				  selected:false,
				  value:8,
				  data:generateTimeThirdData
			});
			
		}
		
	}
	
	//验证周报的checkbobox是否全部取消勾选
	function checkWeekScope(){
		
		for(var i = 1 ; i < 8 ; i ++){
			  if($('#weekTimeScopeCheckbox' + i).attr('checked') == 'checked'){
				  return true;
			  }
		}
		
		return false;
		
	}
	
	function changeSecondGenerateTimeSelect(index){
		  var oldValue = globalVariableData.generateTimeSecond.jq.combobox('getValue');
		  switch(index){
		  
			  case 1: globalVariableData.generateTimeSecond.load([
	                       {id:2,name:'周二'},
	                       {id:3,name:'周三'},
	                       {id:4,name:'周四'},
	                       {id:5,name:'周五'},
	                       {id:6,name:'周六'},
	                       {id:7,name:'周日'}
	                   ]);
	                    if(oldValue < 2){
				  			globalVariableData.generateTimeSecond.jq.combobox('setValue',2);
	                    }else{
	                    	globalVariableData.generateTimeSecond.jq.combobox('setValue',oldValue);
	                    }
			  			break;
			  case 2:globalVariableData.generateTimeSecond.load([
	                       {id:3,name:'周三'},
	                       {id:4,name:'周四'},
	                       {id:5,name:'周五'},
	                       {id:6,name:'周六'},
	                       {id:7,name:'周日'}
	                   ]);
			  			if(oldValue < 3){
				  			globalVariableData.generateTimeSecond.jq.combobox('setValue',3);
	                    }else{
	                    	globalVariableData.generateTimeSecond.jq.combobox('setValue',oldValue);
	                    }
			  			break;
			  case 3:globalVariableData.generateTimeSecond.load([
	                       {id:4,name:'周四'},
	                       {id:5,name:'周五'},
	                       {id:6,name:'周六'},
	                       {id:7,name:'周日'}
	                   ]);
			  			if(oldValue < 4){
				  			globalVariableData.generateTimeSecond.jq.combobox('setValue',4);
	                    }else{
	                    	globalVariableData.generateTimeSecond.jq.combobox('setValue',oldValue);
	                    }
			  			break;
			  case 4:globalVariableData.generateTimeSecond.load([
	                       {id:5,name:'周五'},
	                       {id:6,name:'周六'},
	                       {id:7,name:'周日'}
	                   ]);
			  			if(oldValue < 5){
				  			globalVariableData.generateTimeSecond.jq.combobox('setValue',5);
	                    }else{
	                    	globalVariableData.generateTimeSecond.jq.combobox('setValue',oldValue);
	                    }
			  			break;
			  case 5:globalVariableData.generateTimeSecond.load([
	                       {id:6,name:'周六'},
	                       {id:7,name:'周日'}
	                   ]);
			  			if(oldValue < 6){
				  			globalVariableData.generateTimeSecond.jq.combobox('setValue',6);
	                    }else{
	                    	globalVariableData.generateTimeSecond.jq.combobox('setValue',oldValue);
	                    }
			  			break;
			  case 6:globalVariableData.generateTimeSecond.load([
	                       {id:7,name:'周日'}
	                   ]);
	                   globalVariableData.generateTimeSecond.jq.combobox('setValue',7);
			  			break;
			  default : break;
			  
		  }
	}
	
	function dayCycleValidate(){
		var dayReportBeginDayData = new Array();
		for(var i = 0 ; i < 24 ; i ++){
			var name = i;
			if(i < 10){
				name = "0" + i;
			}
			dayReportBeginDayData.push({
				id:i,
				name:name
			});
		}
		
		globalVariableData.timeScopeDayFirstCombobox = oc.ui.combobox({
			  selector:$('#timeScopeDayFirst'),
			  width:'60px',
			  placeholder:false,
			  selected:false,
			  value:0,
			  data:dayReportBeginDayData,
			  onChange : function(newValue, oldValue){
			      newValue = parseInt(newValue);
			      var subFirstValue = parseFloat(globalVariableData.timeScopeSubDayFirstCombobox.jq.combobox('getValue'))
			      //第一个选23.5导致第二个为24,但不出发onchange手动让第一生成时间改变
			      if(newValue == 23 || (newValue == 22 && subFirstValue == 0.5)){
			      	    globalVariableData.generateTimeFirst.load([{id:1,name:'次日'}]);
  		  	    		var generateOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
		  	  	 		var generateTimeSecondData = new Array();
						for(var i = 0 ; i < 24 ; i ++){
							generateTimeSecondData.push({
								id:i,
								name:i + ":00"
							});
						}
						globalVariableData.generateTimeSecond.load(generateTimeSecondData);
						globalVariableData.generateTimeSecond.jq.combobox('setValue',generateOldValue)
			      }
			      var dayReportEndDayData = new Array();
			      var beginIndex = newValue;
			      if(subFirstValue == 0.5){
		      		 beginIndex++;
			      }
	  		      for(var i = beginIndex ; i < 25 ; i ++){
						var name = i;
						if(i < 10){
							name = "0" + i;
						}
						dayReportEndDayData.push({
							id:i,
							name:name
						});
				  }
				  var secondOldValue = parseInt(globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue'));
				  globalVariableData.timeScopeDaySecondCombobox.load(dayReportEndDayData);
				  if(secondOldValue > beginIndex){
					  globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',secondOldValue);
				  }else{
					  if(beginIndex == 24){
						  globalVariableData.timeScopeSubDaySecondCombobox.load([{id:0,name:'00'}]);
					  }
				  	  globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',beginIndex);
				  }
			  }
		});
		
		globalVariableData.timeScopeSubDayFirstCombobox = oc.ui.combobox({
			  selector:$('#timeScopeSubDayFirst'),
			  width:'60px',
			  placeholder:false,
			  selected:false,
			  value:0,
			  data:[{id:0,name:'00'},{id:0.5,name:'30'}],
			  onChange : function(newValue, oldValue){
			  		newValue = parseFloat(newValue);
			  		var scopeDayFirstValue = globalVariableData.timeScopeDayFirstCombobox.jq.combobox('getValue');
			  		if(newValue == 0.5){
					      var dayReportEndDayData = new Array();
			  		      for(var i = (parseInt(scopeDayFirstValue) + 1) ; i < 25 ; i ++){
								var name = i;
								if(i < 10){
									name = "0" + i;
								}
								dayReportEndDayData.push({
									id:i,
									name:name
								});
						  }
						  var oldScopeDaySecondValue = globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue');
						  globalVariableData.timeScopeDaySecondCombobox.load(dayReportEndDayData);
						  if(scopeDayFirstValue != oldScopeDaySecondValue){
						  	  globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',oldScopeDaySecondValue);
						  }else{
							  globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',(parseInt(scopeDayFirstValue) + 1));
						  }
						  
						  if(scopeDayFirstValue == 23){
							  globalVariableData.timeScopeSubDaySecondCombobox.load([{id:0,name:'00'}]);
						  }

			  		}else{
					      var dayReportEndDayData = new Array();
			  		      for(var i = parseInt(scopeDayFirstValue) ; i < 25 ; i ++){
								var name = i;
								if(i < 10){
									name = "0" + i;
								}
								dayReportEndDayData.push({
									id:i,
									name:name
								});
						  }
						  var oldScopeDaySecondValue = globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue');
						  globalVariableData.timeScopeDaySecondCombobox.load(dayReportEndDayData);
					  	  globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',oldScopeDaySecondValue);
			  		}
			  		
					if(scopeDayFirstValue == 23 || (scopeDayFirstValue == 22 && newValue == 0.5)){
				  		    globalVariableData.generateTimeFirst.load([{id:1,name:'次日'}]);
	  		  	    		var generateOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
			  	  	 		var generateTimeSecondData = new Array();
							for(var i = 0 ; i < 24 ; i ++){
								generateTimeSecondData.push({
									id:i,
									name:i + ":00"
								});
							}
							globalVariableData.generateTimeSecond.load(generateTimeSecondData);
							globalVariableData.generateTimeSecond.jq.combobox('setValue',generateOldValue);
					}
			  		
			  }
		});
		
		var dayReportEndDayData = new Array();
		for(var i = 0 ; i < 25 ; i ++){
			var name = i;
			if(i < 10){
				name = "0" + i;
			}
			dayReportEndDayData.push({
				id:i,
				name:name
			});
		}
		
		globalVariableData.timeScopeDaySecondCombobox = oc.ui.combobox({
			  selector:$('#timeScopeDaySecond'),
			  width:'60px',
			  placeholder:false,
			  selected:false,
			  value:24,
			  data:dayReportEndDayData,
			  onChange : function(newValue, oldValue){
			  	    if(!globalVariableData.generateTimeFirst){
			  	    	return;
			  	    }
		  	    	oldValue = parseInt(oldValue);
		  	    	var newIntValue = parseInt(newValue);
		  	    	if(oldValue == 24 || oldValue == 23){
		  	    		var generateOldValue = globalVariableData.generateTimeFirst.jq.combobox('getValue');
		  	    		globalVariableData.generateTimeFirst.load([{id:0,name:'当日'},{id:1,name:'次日'}]);
		  	    		globalVariableData.generateTimeFirst.jq.combobox('setValue',generateOldValue);
		  	    	}
		  	    	if(newValue == 24 || newValue == 23){
		  	    		if(newValue == 23){
		  	    			if(newValue == globalVariableData.timeScopeDayFirstCombobox.jq.combobox('getValue')){
			  	    			globalVariableData.timeScopeSubDaySecondCombobox.load([{id:0.5,name:'30'}]);
			  	    			globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('setValue',0.5);
			  	    		}else{
				  	    		var secondSubOldValue = globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('getValue');
				  	    		globalVariableData.timeScopeSubDaySecondCombobox.load([{id:0,name:'00'},{id:0.5,name:'30'}]);
				  	    		globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('setValue',secondSubOldValue);
			  	    		}
		  	    		}else{
		  	    			globalVariableData.timeScopeSubDaySecondCombobox.load([{id:0,name:'00'}]);
		  	    		}
		  	    		globalVariableData.generateTimeFirst.load([{id:1,name:'次日'}]);
		  	    		var generateOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
		  	  	 		var generateTimeSecondData = new Array();
						for(var i = 0 ; i < 24 ; i ++){
							generateTimeSecondData.push({
								id:i,
								name:i + ":00"
							});
						}
						globalVariableData.generateTimeSecond.load(generateTimeSecondData);
						globalVariableData.generateTimeSecond.jq.combobox('setValue',generateOldValue);
		  	    	}else if(newValue == 0){
		  	    		globalVariableData.timeScopeSubDaySecondCombobox.load([{id:0.5,name:'30'}]);
		  	    		globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('setValue',0.5);
		  	    	}else{
		  	    		if(newValue == globalVariableData.timeScopeDayFirstCombobox.jq.combobox('getValue')){
		  	    			globalVariableData.timeScopeSubDaySecondCombobox.load([{id:0.5,name:'30'}]);
		  	    			globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('setValue',0.5);
		  	    		}else{
			  	    		var secondSubOldValue = globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('getValue');
			  	    		globalVariableData.timeScopeSubDaySecondCombobox.load([{id:0,name:'00'},{id:0.5,name:'30'}]);
			  	    		globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('setValue',secondSubOldValue);
		  	    		}
		  	    	}
		  	    	
		  	    	//如果选择的为当日
		  	    	if(globalVariableData.generateTimeFirst.jq.combobox('getValue') == 0){
						var generateTimeSecondData = new Array();
						for(var i = (newIntValue + 1) ; i < 24 ; i ++){
							generateTimeSecondData.push({
								id:i,
								name:i + ":00"
							});
						}
						var secondOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
						globalVariableData.generateTimeSecond.load(generateTimeSecondData);
						if(secondOldValue > newIntValue){
						   globalVariableData.generateTimeSecond.jq.combobox('setValue',secondOldValue);
						}else{
						   globalVariableData.generateTimeSecond.jq.combobox('setValue',newIntValue + 1);
					    }
		  	    	}
		  	    	
			  }
		});
		
		globalVariableData.timeScopeSubDaySecondCombobox = oc.ui.combobox({
			  selector:$('#timeScopeSubDaySecond'),
			  width:'60px',
			  placeholder:false,
			  selected:false,
			  value:0,
			  data:[{id:0,name:'00'}],
			  onChange : function(newValue, oldValue){
			  }
		});
		
		globalVariableData.generateTimeFirst = oc.ui.combobox({
			  selector:$('#generateTimeDayFirst'),
			  width:'85px',
			  placeholder:false,
			  selected:false,
			  value:1,
			  data:[{id:1,name:'次日'}],
			  onChange : function(newValue, oldValue){
			  	  newValue = parseInt(newValue);
			  	  var generateOldValue = parseInt(globalVariableData.generateTimeSecond.jq.combobox('getValue'));
			  	  if(newValue == 0){
			  	    	//当日
			  	    	var scopeSecond = parseInt(globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue'));
			  	    	var generateTimeSecondData = new Array();
						for(var i = (scopeSecond + 1) ; i < 24 ; i ++){
							generateTimeSecondData.push({
								id:i,
								name:i + ":00"
							});
						}
						globalVariableData.generateTimeSecond.load(generateTimeSecondData);
						if(scopeSecond < generateOldValue){
							globalVariableData.generateTimeSecond.jq.combobox('setValue',generateOldValue);
						}else{
							globalVariableData.generateTimeSecond.jq.combobox('setValue',scopeSecond + 1);
						}
			  	  }else{
			  	  	 	//次日
		  	  	 		var generateTimeSecondData = new Array();
						for(var i = 0 ; i < 24 ; i ++){
							generateTimeSecondData.push({
								id:i,
								name:i + ":00"
							});
						}
						globalVariableData.generateTimeSecond.load(generateTimeSecondData);
						globalVariableData.generateTimeSecond.jq.combobox('setValue',generateOldValue);
			  	  }
			  }
		});
		
		var generateTimeSecondData = new Array();
		for(var i = 0 ; i < 24 ; i ++){
			generateTimeSecondData.push({
				id:i,
				name:i + ":00"
			});
		}
		
		globalVariableData.generateTimeSecond = oc.ui.combobox({
			  selector:$('#generateTimeDaySecond'),
			  width:'85px',
			  placeholder:false,
			  selected:false,
			  value:8,
			  data:generateTimeSecondData
		});
	}
	
	function resetAllContentSetting(){
		
		
		globalVariableData.isShowMetricInfo = false;
	    globalVariableData.isShowApartLeftPickGridInfo = false;
	    globalVariableData.isSetMetricInfo = false;
		globalVariableData.comboTree.jq.combotree('setValue',"");
//		globalVariableData.selectChildResourceCombobox.load([]);
		setDatagridData(globalVariableData.dataGrid,[]);
		if(globalVariableData.pickGrid != null){
//			if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
//				setDatagridData(globalVariableData.pickGrid,[]);
//			}else{
//				setPickgridLeftData([]);
//				setPickgridRightData([]);
//			}
			if(!globalVariableData.currentResourceType){
				globalVariableData.currentResourceType = "";
			}
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/vm/vmReport/getVmResourceByType.htm'),
				  data:{type:globalVariableData.currentResourceType,domainId:globalVariableData.reportTemplateDomainSelectNameCombobox.jq.combobox('getValue')},
				  success:function(data){
//					  globalVariableData.pickGrid.reload(data.data,'left');
//					  globalVariableData.pickGrid.reload([],'right');
					  setPickgridLeftData([]);
					  setPickgridRightData([]);
					  
					  setDatagridData(globalVariableData.dataGrid,[]);
				  }
				  
			  });
		}
	    $('#searchInstanceListInput').val('');
		setDatagridData(globalVariableData.directory,[]);
	}
	
function switchTemplateType(){
		
		if(globalVariableData.dataGrid != null){
			  changeMetricDataGridColumnsDisplay(globalVariableData.curType);
		}
		
		if(globalVariableData.directory != null){
			showOrHideField(globalVariableData.directory,'directoryTypeName','hide');
			
			showOrHideField(globalVariableData.directory,'resourceType','show');
			globalVariableData.directory.selector.datagrid('getColumnOption','resourceCount').title = '资源数量';
			globalVariableData.directory.selector.datagrid();
		}
		$('#resetMetricThresholdValueButton').html('');
		
		if(globalVariableData.pickGrid == null){
			createPickGridForInstance();
		}

		switch(globalVariableData.curType){
			case globalVariableData.vmPerformanceReportType: globalVariableData.titleContentGroup.append(globalVariableData.performanceTitleContent);break;
			case globalVariableData.vmAlarmReportType: {
				globalVariableData.titleContentGroup.append(globalVariableData.alarmTitleContent);
				$('#detailInfoCheckBox').on('change',function(e){
					 if($(e.target).attr('checked') == 'checked'){
						 //选中详细告警信息
						 $('.alarmCheckBoxClass').attr('checked','checked');
					 }else{
						 //取消详细告警信息
						 $('.alarmCheckBoxClass').removeAttr('checked');
					 }
				});
				//若勾选一项级别，则勾选详细告警
				$('.alarmCheckBoxClass').on('change',function(e){
					if($(e.target).attr('checked') == 'checked'){
						if($('#detailInfoCheckBox').attr('checked') != 'checked'){
							$('#detailInfoCheckBox').attr('checked','checked');
						}
					}else{
						if($('.alarmCheckBoxClass:checked').length <= 0){
							$('#detailInfoCheckBox').removeAttr('checked');
						}
					}
				});
				break;
			}
			default : break;return;
		
		}
		
	}
function resetGridData(){
	setDatagridData(globalVariableData.dataGrid,[]);
	setPickgridRightData([]);
	
    $('#searchInstanceListInput').val('');
	
}

function registerSortable(){
	//注册拖曳排序功能
	var tbody = globalVariableData.directory.selector.datagrid('getPanel').find('.datagrid-body>.table-datanull>table>tbody');
	tbody.sortable({
		opacity: 0.6,
		revert: true,
		start:function(event, ui){
			//拖曳开始
			globalVariableData.isSorting = true;
		},
		stop:function(event, ui){
			//拖曳结束
			globalVariableData.isSorting = false;
		}
	});
}

function setPickgridLeftData(nowData){
	var data = $.extend(true,[],nowData);
	if(!data || data.length <= 0){
		deleteAllGridData(globalVariableData.pickGrid.leftGrid);
//		globalVariableData.pickGrid.reload([],"left");
		
	}else{
//		globalVariableData.pickGrid.reload([],"left");
		globalVariableData.pickGrid.loadData("left",{"code":200,"data":{"total":0,"rows":data}});
	}
}

function setPickgridRightData(nowData){
	var data = $.extend(true,[],nowData);
	if(!data || data.length <= 0){
		deleteAllGridData(globalVariableData.pickGrid.rightGrid);
		
//		globalVariableData.pickGrid.reload([],"right");
	}else{
//		globalVariableData.pickGrid.reload([],"right");
		globalVariableData.pickGrid.loadData("right",{"code":200,"data":{"total":0,"rows":data}});
	}
}

//性能报告添加目录
function addDirectoryPerformance(directorySubType,directoryTypeName){
	
	  //保存报表目录
	  var directoryName = $('#directoryNameInputId').val().trim();
	  if(directoryName == null || directoryName == undefined || directoryName == ''){
		  alert('请输入章节名称');
		  return;
	  }
	  
	  var rows = globalVariableData.directory.selector.datagrid('getRows');
	  for(var i = 0 ; i < rows.length ; i ++){
		  	if(globalVariableData.curOpenDirectoryName == rows[i].directoryTitle){
		  		continue;
		  	}
			if(rows[i].directoryTitle == directoryName){
				alert('章节名已存在,请修改!');
				return;
			}
      }
	  
	  var detailInfoChecked = $('#detailInfoCheckBox').attr('checked');
	  if(detailInfoChecked == "checked"){
		  detailInfoChecked = "true";
	  }else{
		  detailInfoChecked = "false";
	  }
	  
//	  var selectResourceInstanceData = globalVariableData.pickGrid.getRightTreeData();
	  var selectResourceInstanceData = globalVariableData.pickGrid.getRightRows();
	  if(selectResourceInstanceData == null || selectResourceInstanceData == undefined || selectResourceInstanceData.length <= 0){
		  alert('请选择资源实例');
		  return;
	  }
	  var resourceIdsArray = '';
	  for(var i = 0 ; i < selectResourceInstanceData.length ; i ++){
		  resourceIdsArray += selectResourceInstanceData[i].id + ",";
	  }
	  resourceIdsArray = resourceIdsArray.substring(0,resourceIdsArray.length - 1);
	  var instanceLength = selectResourceInstanceData.length;
	  
	  var selectMetricData = globalVariableData.dataGrid.selector.datagrid('getChecked');
	  if(selectMetricData == null || selectMetricData == undefined || selectMetricData.length <= 0){
		  alert('请选择指标');
		  return;
	  }
	  
	  if(selectMetricData.length > globalVariableData.maxMetricSize){
	  	  alert('指标选择数量不能大于' + globalVariableData.maxMetricSize + '个');
	  	  return;
	  }
	  
	  var metricIdsArray = '';
	  for(var i = 0 ; i < selectMetricData.length ; i ++){
		  metricIdsArray += selectMetricData[i].id + ",";
	  }
	  metricIdsArray = metricIdsArray.substring(0,metricIdsArray.length - 1);
	  var metricLength = selectMetricData.length;
	  
	  //取出资源类型
	  var directoryType = null;
	  var childComboValue = null;
	  var parentComboValue = null;
	  parentComboValue = globalVariableData.comboTree.jq.combobox('getValue');
	  
	  directoryType = globalVariableData.comboTree.jq.combobox('getText');
	  var directoryTypeId = globalVariableData.comboTree.jq.combobox('getValue');
	  
	  var rowData = {
			  "directoryTitle":directoryName,
			  "resourceType":directoryType,
			  "resourceTypeId":directoryTypeId,
			  "resourceCount":instanceLength,
			  "metricCount":metricLength,
			  "operation":"<a name='" + directoryName + "' class='ico icon-del'></a>",
			  "resourceIds":resourceIdsArray,
			  "metricIds":metricIdsArray,
			  "isShowDetail":detailInfoChecked,
			  "childComboValue":childComboValue,
			  "parentComboValue":parentComboValue
	  };
	  if(directorySubType){
	  	  rowData.directoryType = directorySubType;
	  	  rowData.directoryTypeName = directoryTypeName;
	  }
	  
	  if(globalVariableData.curUpdateRowData && globalVariableData.curUpdateRowData.directoryId){
	  	  rowData.directoryId = globalVariableData.curUpdateRowData.directoryId;
	  }
	  
	  if(globalVariableData.curOpenDirectoryName == null){
		  
		  //获取选中的排序指标
		  globalVariableData.directory.selector.datagrid('appendRow',rowData);
		  
	  }else{
		  
		  //修改
		  var updateIndex = globalVariableData.directory.selector.datagrid('getRowIndex',globalVariableData.curUpdateRowData);
		  globalVariableData.directory.selector.datagrid('updateRow',{
			  index:updateIndex,
			  row:rowData
		  });
		  
		  globalVariableData.curUpdateRowData = null;
		  globalVariableData.curOpenDirectoryName = null;
		  
	  }
	  
	  globalVariableData.contentAccordion.accordion('select',1);
		  if(!directorySubType){
		  resetAllData();
	  }else{
	  	 globalVariableData.reportDirectoryTypeSelectNameCombobox.jq.combobox('setValue',-1);
  		 globalVariableData.reportDirectoryTypeSelectNameCombobox.jq.combobox('setValue',1);
	  }
	
}

//告警报告添加
function addDirectoryAlarm(directorySubType,directoryTypeName){
	
	  //保存报表目录
	  var directoryName = $('#directoryNameInputId').val().trim();
	  if(directoryName == null || directoryName == undefined || directoryName == ''){
		  alert('请输入章节名称');
		  return;
	  }
	  
	  var rows = globalVariableData.directory.selector.datagrid('getRows');
	  for(var i = 0 ; i < rows.length ; i ++){
		  	if(globalVariableData.curOpenDirectoryName == rows[i].directoryTitle){
		  		continue;
		  	}
			if(rows[i].directoryTitle == directoryName){
				alert('章节名已存在,请修改!');
				return;
			}
      }
	  
	  var detailInfoChecked = $('#detailInfoCheckBox').attr('checked');
	  if(detailInfoChecked == "checked"){
		  detailInfoChecked = "true";
	  }else{
		  detailInfoChecked = "false";
	  }
	  
	  var alarmDeadly = $('#alarmDeadlyId').attr('checked');
	  if(alarmDeadly == 'checked'){
		  alarmDeadly = 'true';
	  }else{
		  alarmDeadly = 'false';
	  }
	  
	  var alarmSerious = $('#alarmSeriousId').attr('checked');
	  if(alarmSerious == 'checked'){
		  alarmSerious = 'true';
	  }else{
		  alarmSerious = 'false';
	  }
	  
	  var alarmWarning = $('#alarmWarningId').attr('checked');
	  if(alarmWarning == 'checked'){
		  alarmWarning = 'true';
	  }else{
		  alarmWarning = 'false';
	  }

	  var selectResourceInstanceData = globalVariableData.pickGrid.getRightRows();
	  if(selectResourceInstanceData == null || selectResourceInstanceData == undefined || selectResourceInstanceData.length <= 0){
		  alert('请选择资源实例');
		  return;
	  }
	  var resourceIdsArray = '';
	  for(var i = 0 ; i < selectResourceInstanceData.length ; i ++){
		  resourceIdsArray += selectResourceInstanceData[i].id + ",";
	  }
	  resourceIdsArray = resourceIdsArray.substring(0,resourceIdsArray.length - 1);
	  var instanceLength = selectResourceInstanceData.length;
	  
	  var selectMetricData = globalVariableData.dataGrid.selector.datagrid('getChecked');
	  if(selectMetricData == null || selectMetricData == undefined || selectMetricData.length <= 0){
		  alert('请选择指标');
		  return;
	  }
	  
	  if(selectMetricData.length > globalVariableData.maxMetricSize){
	  	  alert('指标选择数量不能大于' + globalVariableData.maxMetricSize + '个');
	  	  return;
	  }
	  
	  var metricIdsArray = '';
	  for(var i = 0 ; i < selectMetricData.length ; i ++){
		  metricIdsArray += selectMetricData[i].id + ",";
	  }
	  metricIdsArray = metricIdsArray.substring(0,metricIdsArray.length - 1);
	  var metricLength = selectMetricData.length;
	  
	//取出资源类型
	  var directoryType = null;
	  var childComboValue = null;
	  var parentComboValue = null;
	  parentComboValue = globalVariableData.comboTree.jq.combobox('getValue');
	  
	  directoryType = globalVariableData.comboTree.jq.combobox('getText');
	  var directoryTypeId = globalVariableData.comboTree.jq.combobox('getValue');
	  
	  var rowData = {
			  "directoryTitle":directoryName,
			  "resourceType":directoryType,
			  "resourceTypeId":directoryTypeId,
			  "resourceCount":instanceLength,
			  "metricCount":metricLength,
			  "operation":"<a name='" + directoryName + "' class='ico icon-del'></a>",
			  "resourceIds":resourceIdsArray,
			  "metricIds":metricIdsArray,
			  "isShowDetail":detailInfoChecked,
			  "childComboValue":childComboValue,
			  "parentComboValue":parentComboValue,
			  "isShowDeadly":alarmDeadly,
			  "isShowSerious":alarmSerious,
			  "isShowWarning":alarmWarning
	  };
	  
		  if(directorySubType){
	  	  rowData.directoryType = directorySubType;
	  	  rowData.directoryTypeName = directoryTypeName;
	  }
	  
		  if(globalVariableData.curUpdateRowData && globalVariableData.curUpdateRowData.directoryId){
	  	  rowData.directoryId = globalVariableData.curUpdateRowData.directoryId;
	  }
	  
	  if(globalVariableData.curOpenDirectoryName == null){
		  
		  //获取选中的排序指标
		  globalVariableData.directory.selector.datagrid('appendRow',rowData);
		  
	  }else{
		  
		  //修改
		  var updateIndex = globalVariableData.directory.selector.datagrid('getRowIndex',globalVariableData.curUpdateRowData);
		  
		  globalVariableData.directory.selector.datagrid('updateRow',{
			  index:updateIndex,
			  row:rowData
		  });
		  
		  globalVariableData.curUpdateRowData = null;
		  globalVariableData.curOpenDirectoryName = null;
		  
	  }
	  
	  globalVariableData.contentAccordion.accordion('select',1);
	  if(!directorySubType){
		  resetAllData();
	  }else{
	  	 globalVariableData.reportDirectoryTypeSelectNameCombobox.jq.combobox('setValue',-1);
  		 globalVariableData.reportDirectoryTypeSelectNameCombobox.jq.combobox('setValue',1);
	  }
	
}

//验证邮箱
function validateEmail(emailAddressInputValue){
    	var emailPrompt = "";  
	    if(emailAddressInputValue != "" && emailAddressInputValue.indexOf(";")>0){  
	        var arremail = emailAddressInputValue.split(";");  
	        for(var i = 0 ; i <arremail.length ; i++){  
	            if(arremail[i].replace(/\s+/g,"") && arremail[i].replace(/\s+/g,"").search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) == -1){  
	                emailPrompt = emailPrompt + "邮箱" + arremail[i] + "格式错误!\n";  
                }  
            }  
	    }else{  
           if(emailAddressInputValue.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) == -1){  
               emailPrompt = "邮箱" + emailAddressInputValue + "格式错误!\n";  
           }  
        }  
	    if(emailPrompt != ""){  
	       alert(emailPrompt); 
	       return false; 
        }else{
		   return true;
        }
}

function refreshDeleteOperate(){
	//绑定删除点击事件
	globalVariableData.templateMain.find('.icon-del').unbind();
	globalVariableData.templateMain.find('.icon-del').on('click',function(e){
		//删除目录
		e.stopPropagation();
		oc.ui.confirm('确认删除该章节吗?',function(){
			var rows = globalVariableData.directory.selector.datagrid('getRows');
			var isContainTrend = false;
			for(var i = 0 ; i < rows.length ; i ++){
				if(rows[i].directoryTitle == $(e.target).attr('name')){
					globalVariableData.directory.selector.datagrid('deleteRow',globalVariableData.directory.selector.datagrid('getRowIndex',rows[i]));
					if(globalVariableData.curOpenDirectoryName == $(e.target).attr('name')){
						resetAllData();
						globalVariableData.curOpenDirectoryName = null;
					}
					break;
				}
				if(rows[i].directoryType && rows[i].directoryType == globalVariableData.trendReportType){
					isContainTrend = true;
				}
			}
			if(globalVariableData.curType == globalVariableData.comprehensiveReportType && !isContainTrend){
				globalVariableData.templateMain.find('.radioCheckedTemplateType[value="1"]').attr('disabled',false);
				globalVariableData.templateMain.find('.radioCheckedTemplateType[value="1"]').parent().unbind('click');
			}
		});
    });
}

function resetAllData(){
	
	globalVariableData.comboTree.jq.combotree('setValue','');
	setDatagridData(globalVariableData.dataGrid,[]);
	if(globalVariableData.pickGrid != null){
		if(globalVariableData.curType == globalVariableData.analysisReportType || (globalVariableData.curType == globalVariableData.comprehensiveReportType && globalVariableData.comprehensiveType == globalVariableData.analysisReportType)){
			setDatagridData(globalVariableData.pickGrid,[]);
		}else{
			if(globalVariableData.curType == globalVariableData.businessReportType){
				//重新加载业务列表
				getBusinessResourceList();
			}else{
				setPickgridLeftData([]);
			}
			setPickgridRightData([]);
		}
	}
    $('#searchInstanceListInput').val('');
    $('#directoryNameInputId').val('');
	switch(globalVariableData.curType){
		case globalVariableData.vmPerformanceReportType: $('#detailInfoCheckBox').removeAttr('checked');break;
		case globalVariableData.vmAlarmReportType: $('#detailInfoCheckBox').removeAttr('checked');
				  $('#alarmDeadlyId').removeAttr('checked');
				  $('#alarmSeriousId').removeAttr('checked');
				  $('#alarmWarningId').removeAttr('checked');
			   	  break;
		case globalVariableData.topnReportType: 
			globalVariableData.topnCountCombobox.jq.combobox('setValue',10);
			$('#topnPerformanceType').attr('checked','checked');
			break;
		case globalVariableData.availabilityReportType: $('#detailInfoCheckBox').removeAttr('checked');break;
		case globalVariableData.trendReportType: break;
		case globalVariableData.analysisReportType: $('.analysisReportRadioClass[value="0"]').attr('checked','checked');break;
		case globalVariableData.comprehensiveReportType: break;
		default : break;return;
	}
    
}

//清空表格数据
function deleteAllGridData(grid){
	var item = grid.selector.datagrid('getRows');  
	if (item) {  
		for (var i = item.length - 1; i >= 0; i--) {  
			var index = grid.selector.datagrid('getRowIndex', item[i]);  
			grid.selector.datagrid('deleteRow', index);  
		}  
		grid.selector.datagrid('uncheckAll');
	}
}

function setDatagridData(grid,nowData,newMsg){
	var data = $.extend(true,[],nowData);
	if(!data || data.length <= 0){
		if(newMsg){
			globalVariableData.dataGrid.updateNoDataMsg(newMsg);
		}else{
			globalVariableData.dataGrid.updateNoDataMsg('未选择数据！');
		}
		deleteAllGridData(grid);
	}else{
		grid.selector.datagrid('loadData',{"code":200,"data":{"total":0,"rows":data}});
	}
}
	
function changeMetricDataGridColumnsDisplay(value){
	
	showOrHideField(globalVariableData.dataGrid,'id','hide');
	showOrHideField(globalVariableData.dataGrid,'radioId','hide');
	showOrHideField(globalVariableData.dataGrid,'name','hide');
	showOrHideField(globalVariableData.dataGrid,'metricSort','hide');
	showOrHideField(globalVariableData.dataGrid,'metricExpectValue','hide');
	
    switch(value){
		case globalVariableData.topnReportType: showOrHideField(globalVariableData.dataGrid,'metricSort','show');
			showOrHideField(globalVariableData.dataGrid,'id','show');
			showOrHideField(globalVariableData.dataGrid,'name','show');break;
		case globalVariableData.trendReportType: showOrHideField(globalVariableData.dataGrid,'radioId','show');
			showOrHideField(globalVariableData.dataGrid,'name','show');break;
		case globalVariableData.analysisReportType:showOrHideField(globalVariableData.dataGrid,'metricExpectValue','show');
			showOrHideField(globalVariableData.dataGrid,'id','show');
			showOrHideField(globalVariableData.dataGrid,'name','show');break;
		default:showOrHideField(globalVariableData.dataGrid,'id','show');
			showOrHideField(globalVariableData.dataGrid,'name','show');
			break;
    }
	
}

function showOrHideField(grid,fieldId,operator){
	
	var dataGrid = grid.selector;
	
	if(operator=='show'){
		dataGrid.datagrid('showColumn',fieldId);
	}else if(operator=='hide'){
		dataGrid.datagrid('hideColumn',fieldId);
	}
	
}

function addTemplateToDatabase(emailAddressInputValue,reportTemplateName,emailFormat){
	
	var directoryData = null;
			    		
	switch(globalVariableData.curType){
		
		case globalVariableData.vmPerformanceReportType: directoryData = makeDirectoryPerformanceData();break;
		case globalVariableData.vmAlarmReportType: directoryData = makeDirectoryAlarmData();break;
		default : break;return;
	
	}
	
	//添加模板
	var cycle = globalVariableData.curSelectTemplateCycle;
	var thirdGenerateTime = null;
	if((cycle == 2 || cycle == 3) && globalVariableData.generateTimeThird){
		thirdGenerateTime = globalVariableData.generateTimeThird.jq.combobox('getValue');
	}else{
		thirdGenerateTime = -1;
	}
	
	var beginTime = '';
	var endTime = null;
	if(globalVariableData.curType != globalVariableData.trendReportType && globalVariableData.curType != globalVariableData.analysisReportType){
		if(cycle == 2){
			//周类型
			var checkedTimes = $('.weekTimeScopeCheckboxClass:checked');
			for(var i = 0 ; i < checkedTimes.length ; i++){
				beginTime += $(checkedTimes[i]).val() + ",";
			}
			beginTime = beginTime.substring(0,beginTime.length - 1);
			endTime = '';
		}else if(cycle == 1){
			//日报
			beginTime = parseInt(globalVariableData.timeScopeDayFirstCombobox.jq.combobox('getValue')) + parseFloat(globalVariableData.timeScopeSubDayFirstCombobox.jq.combobox('getValue'));
			endTime = parseInt(globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue')) + parseFloat(globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('getValue'));
		}else{
			beginTime = globalVariableData.timeScopeDayFirstCombobox.jq.combobox('getValue');
			endTime = globalVariableData.timeScopeDaySecondCombobox.jq.combobox('getValue');
		}
	}
	
	var addTemplate = {
			reportTemplateType:globalVariableData.reportTemplateTypeSelectNameCombobox.jq.combobox('getValue'),
			reportTemplateName:reportTemplateName,
			reportTemplateCycle:cycle,
			reportTemplateStatus:globalVariableData.curSelectTemplateStatus,
			reportTemplateEmailStatus:globalVariableData.curSelectTemplateEmailStatus,
			reportTemplateEmailAddress:emailAddressInputValue,
			reportTemplateEmailFormat:emailFormat,
			directoryList:directoryData
	};
	
	addTemplate.reportTemplateBeginTime = beginTime;
	addTemplate.reportTemplateEndTime = endTime;
	addTemplate.reportTemplateFirstGenerateTime = globalVariableData.generateTimeFirst.jq.combobox('getValue');
	addTemplate.reportTemplateSecondGenerateTime = globalVariableData.generateTimeSecond.jq.combobox('getValue');
	addTemplate.reportTemplateThirdGenerateTime = thirdGenerateTime;
	addTemplate.reportTemplateDomainId = globalVariableData.reportTemplateDomainSelectNameCombobox.jq.combobox('getValue');
	
	
	//添加模板
	oc.util.ajax({
		  successMsg:null,
		  url: oc.resource.getUrl('portal/report/reportTemplate/addTemplate.htm'),
		  timeout:null,
		  data:{template:addTemplate},
		  success:function(data){
			  
			  if(data.data && data.data > 0){
				  alert('添加模板成功!');
				  oc.vm.indexpage.addNavsublistItem(globalVariableData.curType,data.data,reportTemplateName);
				  globalVariableData.mainDialog.panel('destroy');
			  }else{
				  alert('添加模板失败!');
			  }
		  }
	});
	
}

function makeDirectoryPerformanceData(){
	
	var directoryRows = getSortDirectoryData();
	var directoryData = new Array();
	
	for(var i = 0 ; i < directoryRows.length ; i ++){
		
		var resultData = makeDirectoryPerformanceSubData(directoryRows[i]);
		
		directoryData.push(resultData);
		
	}
	
	return directoryData;
	
}

function makeDirectoryPerformanceSubData(rowData){
	//资源实例列表
	var instanceIds = rowData.resourceIds.split(',');
	var instanceIdList = new Array();
	for(var j = 0 ; j < instanceIds.length ; j ++){
		instanceIdList.push({
			instanceId:instanceIds[j]
		});
	}
	
	//指标列表
	var metricIds = rowData.metricIds.split(',');
	var metricIdList = new Array();
	for(var j = 0 ; j < metricIds.length ; j ++){
		metricIdList.push({
			metricId:metricIds[j]
		});
	}
	
	var result = {
		reportTemplateDirectoryName:rowData.directoryTitle,
		reportTemplateDirectoryIsDetail:rowData.isShowDetail == "true"?0:1,
		reportTemplateDirectoryResource:rowData.resourceType,
		reportTemplateDirectoryCategoryId:rowData.parentComboValue,
		reportTemplateDirectorySubResourceId:rowData.childComboValue,
		directoryInstanceList:instanceIdList,
		directoryMetricList:metricIdList
	};
	
	if(rowData.directoryId){
		result.reportTemplateDirectoryId = rowData.directoryId;
	}
	
	return result;
	
}

function editTemplateAlarmDirectoryData(directoryRow){
	var instanceList = "";
	for(var j = 0 ; j < directoryRow.directoryInstanceList.length ; j ++){
		instanceList += directoryRow.directoryInstanceList[j].instanceId + ",";
	}
	instanceList = instanceList.substring(0,instanceList.length - 1);
	
	var metricList = "";
	for(var x = 0 ; x < directoryRow.directoryMetricList.length ; x ++){
		metricList += directoryRow.directoryMetricList[x].metricId + ",";
	}
	metricList = metricList.substring(0,metricList.length - 1);
	var directoryData = {
		  directoryId:directoryRow.reportTemplateDirectoryId,
		  directoryTitle:directoryRow.reportTemplateDirectoryName,
		  resourceType:directoryRow.reportTemplateDirectoryResource,
		  resourceCount:directoryRow.directoryInstanceList.length,
		  metricCount:directoryRow.directoryMetricList.length,
		  operation:"<a name='" + directoryRow.reportTemplateDirectoryName + "' class='ico icon-del'></a>",
		  isShowDetail:directoryRow.reportTemplateDirectoryIsDetail == 0?"true":"false",
		  isShowDeadly:directoryRow.reportTemplateDirectoryDeadly == 0?"true":"false",
		  isShowSerious:directoryRow.reportTemplateDirectorySerious == 0?"true":"false",
		  isShowWarning:directoryRow.reportTemplateDirectoryWarning == 0?"true":"false",
		  parentComboValue:directoryRow.reportTemplateDirectoryCategoryId,
		  childComboValue:directoryRow.reportTemplateDirectorySubResourceId,
		  resourceIds:instanceList,
		  metricIds:metricList
		};
		return directoryData;
}

function makeDirectoryAlarmData(){
	
	var directoryRows = getSortDirectoryData();
	
	var directoryData = new Array();
	
	for(var i = 0 ; i < directoryRows.length ; i ++){
		
		var resultData = makeDirectoryAlarmSubData(directoryRows[i]);
		
		directoryData.push(resultData);
		
	}
	
	return directoryData;
	
}

function makeDirectoryAlarmSubData(rowData){
	
	//资源实例列表
	var instanceIds = rowData.resourceIds.split(',');
	var instanceIdList = new Array();
	for(var j = 0 ; j < instanceIds.length ; j ++){
		instanceIdList.push({
			instanceId:instanceIds[j]
		});
	}
	
	//指标列表
	var metricIds = rowData.metricIds.split(',');
	var metricIdList = new Array();
	for(var j = 0 ; j < metricIds.length ; j ++){
		metricIdList.push({
			metricId:metricIds[j]
		});
	}
	
	var result = {
		reportTemplateDirectoryName:rowData.directoryTitle,
		reportTemplateDirectoryIsDetail:rowData.isShowDetail == "true"?0:1,
		reportTemplateDirectoryResource:rowData.resourceType,
		reportTemplateDirectoryDeadly:rowData.isShowDeadly == "true"?0:1,
		reportTemplateDirectorySerious:rowData.isShowSerious == "true"?0:1,
		reportTemplateDirectoryWarning:rowData.isShowWarning == "true"?0:1,
		reportTemplateDirectoryCategoryId:rowData.parentComboValue,
		reportTemplateDirectorySubResourceId:rowData.childComboValue,
		directoryInstanceList:instanceIdList,
		directoryMetricList:metricIdList
	};
	
	if(rowData.directoryId){
		result.reportTemplateDirectoryId = rowData.directoryId;
	}
	
	return result;
	
}

//获取排序后的目录数据
function getSortDirectoryData(){
	if(!globalVariableData.directory){
		return null;
	}
	var gridTr = globalVariableData.directory.selector.datagrid('getPanel').find('.datagrid-body>.table-datanull>table>tbody>tr');
	var directoryRows = globalVariableData.directory.selector.datagrid('getRows');
	var sortDirectoryRows = new Array();
	for(var i = 0 ; i < gridTr.length ; i ++){
		sortDirectoryRows.push(directoryRows[$(gridTr[i]).attr('datagrid-row-index')]);
	}
	return sortDirectoryRows;
}

//创建资源选择的pickgrid
function createPickGridForInstance(){
	$('#parentResourceInstanceGrid').html('');
	
	var instanceColumns = [[
         {field:'id',checkbox:true},
         {field:'showName',title:'名称',width:125,ellipsis:true},
         {field:'resourceId',hidden:true},
         {field:'resourceType',title:'资源类型',width:125,ellipsis:true}
    ]];
	
	globalVariableData.pickGrid = oc.ui.pickgrid({
		selector:'#parentResourceInstanceGrid',
		leftColumns:instanceColumns,
		rightColumns:instanceColumns,
		isInteractive:true,
		moveBeforeEvent:function(rows,direction){
			if(direction == 'left'){
				//向左移动
				var resultRows = new Array();
				var isSearch = false;
				//判断是否有过滤条件
				var searchContent = $('#searchInstanceListInput').val().trim();
				if(searchContent != ''){
					isSearch = true;
				}
				var newCacheData = $.extend([],globalVariableData.curLeftGridResourceInstanceList);
				for(var i = 0 ; i < rows.length ; i ++){
					newCacheData.push(rows[i]);
					//判断是否有过滤条件，并且是否符合
					if(isSearch && ( rows[i].showName.indexOf(searchContent) != -1 )){
						//符合搜索条件
						resultRows.push(rows[i]);
					}else if(!isSearch){
						//没有搜索条件
						resultRows.push(rows[i]);
					}
				}
				globalVariableData.curLeftGridResourceInstanceList = newCacheData;
				return resultRows;
			}else if(direction == 'right'){
				//向右移动，修改缓存数据
				var cacheInstance = $.extend([],globalVariableData.curLeftGridResourceInstanceList);
				var newInstanceList = new Array();
				out : for(var i = 0 ; i < cacheInstance.length ; i ++ ){
					//找出要移动的数据
					for(var j = 0 ; j < rows.length ; j ++){
						if(cacheInstance[i].id == rows[j].id){
							continue out;
						}
					}
					newInstanceList.push(cacheInstance[i]);
				}
				globalVariableData.curLeftGridResourceInstanceList = newInstanceList;
				return rows;
			}
		},
		onMoveSuccess:function(srcGridData,targetGridData){
//			showMetricDataGridData(globalVariableData.curType,false);
			showMetricDataGridData(srcGridData,targetGridData);
		},
		leftOptions:{
			fitColumns:false,
			onLoadSuccess:function(data){
				if(globalVariableData.isShowApartLeftPickGridInfo){
					//左右分开数据
					var leftData = data.rows;
					var nowLeftData = new Array();
					var nowRightData = new Array();
					next : for(var i = 0 ; i < leftData.length ; i ++){
						var ids = globalVariableData.curUpdateRowData.resourceIds.split(',');
						for(var j = 0 ; j < ids.length ; j ++){
							if(leftData[i].id == ids[j]){
								//右边
								nowRightData.push(leftData[i]);
								continue next;
							}
						}
						nowLeftData.push(leftData[i]);
					}
					globalVariableData.isShowApartLeftPickGridInfo = false;
					globalVariableData.isShowMetricInfo = true;
					setPickgridLeftData(nowLeftData);
					setPickgridRightData(nowRightData);
					
				}
			}
		},
		rightOptions:{
			fitColumns:false,
			onLoadSuccess:function(data){
				if(globalVariableData.isShowMetricInfo){
					showMetricDataGridData(globalVariableData.curType,true);
				}
			}
		}
	});
}

function showMetricDataGridData(srcGridData,targetGridData){
	var rightData = globalVariableData.pickGrid.getRightRows();
	var resourceIdArray = "";
	for(var i = 0 ; i < rightData.length ; i ++){
		resourceIdArray += rightData[i].resourceId + ",";
	}
	resourceIdArray = resourceIdArray.substring(0,resourceIdArray.length - 1);
	if(rightData != null && rightData != undefined && rightData.length > 0){
		if(globalVariableData.curType == 9){
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/vm/vmReport/getDefaultAlarmMetricData.htm'),
				  success:function(data){
					  
					  setDatagridData(globalVariableData.dataGrid,data.data);
				  }
			});
		}else{
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/vm/vmReport/getVmResourceMetricListByType.htm'),
				  data:{type:resourceIdArray},
				  success:function(data){
					  
					  setDatagridData(globalVariableData.dataGrid,data.data);
				  }
			});
		}
			
	}else{
		setDatagridData(globalVariableData.dataGrid,[]);
	}
	
//	function showMetricDataGridData(type,isUpdate,analysisData){	
//	var rightData = new Array();
//	
//	if(analysisData){
//		rightData.push(analysisData);
//	}else{
//		rightData = globalVariableData.pickGrid.getRightRows();
//	}
//	
//	if(rightData == null || rightData == undefined || rightData.length <= 0){
//		setDatagridData(globalVariableData.dataGrid,[]);
//		return;
//	}
//	switch(type){
//		
//		case globalVariableData.performanceReportType:
//			showMetricDataGridDataAjax(rightData,isUpdate);
//			break;
//		case globalVariableData.alarmReportType:
//			if(isUpdate){
//				globalVariableData.isSetMetricInfo = true;
//				globalVariableData.isShowMetricInfo = false;
//			}
//			oc.util.ajax({
//				successMsg:null,
//				url: oc.resource.getUrl('portal/report/reportTemplate/getDefaultAlarmMetricData.htm'),
//				data:null,
//				success:function(data){
//					if(data.data){
//						setDatagridData(globalVariableData.dataGrid,data.data);
//					}else{
//						alert('查询指标失败!');
//					}
//				}
//			});
//			break;
//		
//		default:break;
//	}
}

//修改操作装载数据
function updateTemplateInitData(template){
	
	//基本信息
	globalVariableData.curType = template.reportTemplateType;
	globalVariableData.reportTemplateTypeSelectNameCombobox.jq.combobox('setValue',template.reportTemplateType);
	globalVariableData.reportTemplateTypeSelectNameCombobox.disable();
	if(globalVariableData.curType != globalVariableData.businessReportType){
		//业务报告没有域		
		globalVariableData.reportTemplateDomainSelectNameCombobox.jq.combobox('setValue',template.reportTemplateDomainId);
	}
	$('#reportTemplateNameInput').val(template.reportTemplateName);
	var updateCycle = template.reportTemplateCycle;
	globalVariableData.curSelectTemplateCycle = updateCycle;
	switchTemplateCycle(updateCycle);
	if(globalVariableData.curType != globalVariableData.trendReportType && globalVariableData.curType != globalVariableData.analysisReportType){
		globalVariableData.templateMain.find('.radioCheckedTemplateType[value="' + updateCycle + '"]').attr('checked','checked');
		if(updateCycle == 1){
			//日报
			if((template.reportTemplateBeginTime + '').indexOf(".") != -1){
				globalVariableData.timeScopeDayFirstCombobox.jq.combobox('setValue',template.reportTemplateBeginTime - 0.5);
				globalVariableData.timeScopeSubDayFirstCombobox.jq.combobox('setValue',0.5);
			}else{
				globalVariableData.timeScopeDayFirstCombobox.jq.combobox('setValue',template.reportTemplateBeginTime);
				globalVariableData.timeScopeSubDayFirstCombobox.jq.combobox('setValue',0);
			}
			if((template.reportTemplateEndTime + '').indexOf(".") != -1){
				globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',template.reportTemplateEndTime - 0.5);
				globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('setValue',0.5);
			}else{
				globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',template.reportTemplateEndTime);
				globalVariableData.timeScopeSubDaySecondCombobox.jq.combobox('setValue',0);
			}
		}else if(updateCycle == 2){
			var weeks = template.reportTemplateBeginTime.split(',');
			for(var i = 0 ; i < 7 ; i++){
				$('#weekTimeScopeCheckbox' + (i + 1)).removeAttr('checked');
			}
			var isContainSunday = false;
			for(var i = 0 ; i < weeks.length ; i++){
				$('#weekTimeScopeCheckbox' + parseInt(weeks[i])).attr('checked','checked');
				if(weeks[i] == 7){
					isContainSunday = true;
				}
			}
			if(!isContainSunday){
				//js未调用onchange事件,手动触发
				globalVariableData.generateTimeFirst.load([{id:0,name:'当周'},{id:1,name:'下周'}]);
			}
		}else{
			globalVariableData.timeScopeDayFirstCombobox.jq.combobox('setValue',template.reportTemplateBeginTime);
			globalVariableData.timeScopeDaySecondCombobox.jq.combobox('setValue',template.reportTemplateEndTime);
		}
		globalVariableData.generateTimeFirst.jq.combobox('setValue',template.reportTemplateFirstGenerateTime);
		globalVariableData.generateTimeSecond.jq.combobox('setValue',template.reportTemplateSecondGenerateTime);
		if(updateCycle != 1){
			globalVariableData.generateTimeThird.jq.combobox('setValue',template.reportTemplateThirdGenerateTime);
		}
	}else{
		globalVariableData.templateMain.find('.radioCheckedTemplateType[value="' + updateCycle + '"]').attr('checked','checked');
	}
	globalVariableData.templateMain.find('.templateStatusClass[value="' + template.reportTemplateStatus + '"]').attr('checked','checked');
	
	//目录
	globalVariableData.curUpdateDirectoryDataArray = new Array();
	switch(template.reportTemplateType){
	
		case globalVariableData.vmPerformanceReportType: {
			for(var i = 0 ; i < template.directoryList.length ; i ++){
				var data = editTemplatePerformanceDirectoryData(template.directoryList[i]);
				globalVariableData.curUpdateDirectoryDataArray.push(data);
			}
			break;
		}
		case globalVariableData.vmAlarmReportType: {
	  		for(var i = 0 ; i < template.directoryList.length ; i ++){
				var data = editTemplateAlarmDirectoryData(template.directoryList[i]);
				globalVariableData.curUpdateDirectoryDataArray.push(data);
			}
			break;
		}
		default : break;return;
	
	}
	//邮件信息
	if(template.reportTemplateEmailName != null){
		$('#reportTemplateEmailInputNameId').text(template.reportTemplateEmailName);
	}
	globalVariableData.templateMain.find('.templateEmailStatusClass[value="' + template.reportTemplateEmailStatus + '"]').attr('checked','checked');
	globalVariableData.curSelectTemplateEmailStatus = template.reportTemplateEmailStatus;
	if(template.reportTemplateEmailAddress != null){
		$('#emailAddressId').val(template.reportTemplateEmailAddress);
	}
	if(template.reportTemplateEmailFormat){
		var formatArray = template.reportTemplateEmailFormat.split(',');
		for(var i = 0 ; i < formatArray.length ; i ++){
			globalVariableData.templateMain.find('.templateEmailTypeClass[value="' + formatArray[i] + '"]').attr('checked','checked');
		}
	}
}

function editTemplatePerformanceDirectoryData(directoryRow){
	
	var instanceList = "";
	for(var j = 0 ; j < directoryRow.directoryInstanceList.length ; j ++){
		instanceList += directoryRow.directoryInstanceList[j].instanceId + ",";
	}
	instanceList = instanceList.substring(0,instanceList.length - 1);
	
	var metricList = "";
	for(var x = 0 ; x < directoryRow.directoryMetricList.length ; x ++){
		metricList += directoryRow.directoryMetricList[x].metricId + ",";
	}
	metricList = metricList.substring(0,metricList.length - 1);
	var directoryData = {
		directoryId:directoryRow.reportTemplateDirectoryId,
		directoryTitle:directoryRow.reportTemplateDirectoryName,
		isShowDetail:directoryRow.reportTemplateDirectoryIsDetail == 0?"true":"false",
		resourceType:directoryRow.reportTemplateDirectoryResource,
		resourceCount:directoryRow.directoryInstanceList.length,
		metricCount:directoryRow.directoryMetricList.length,
		operation:"<a name='" + directoryRow.reportTemplateDirectoryName + "' class='ico icon-del'></a>",
		childComboValue:directoryRow.reportTemplateDirectorySubResourceId,
		parentComboValue:directoryRow.reportTemplateDirectoryCategoryId,
		resourceIds:instanceList,
		metricIds:metricList
	};
	
	return directoryData;

}
});