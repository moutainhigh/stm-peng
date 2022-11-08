(function($){
	oc.ns('oc.report.main.center.download');
	var thisReportTemplateId,thisReportTemplateType,tempDiv;
//	//模板是否可以被当前用户编辑,删除
//	var isEdit,isRemove;
	//div结构
	var myForm,topInfoDiv,reportInfoDiv;
	//用于分页
	var pageThis = 1;
	var startRow = 0;
	var rowCount = 20;
	var totalRecord = 0;
	var totalPage = 0 ;
	var pageBtnDisabledClass = 'l-btn-disabled l-btn-plain-disabled';
	var skin=Highcharts.theme.currentSkin;
	function init(){
		var id=oc.util.generateId();
		var mainDiv = $('#reportMainCenterDownload').attr('id',id);
//		mainDiv.css('height','100%');
		var form = $('<form/>').addClass('oc-form').css('height','100%').addClass('padding0');
		
		topInfoDiv = $('<div/>').attr('class','topInfoDiv').addClass('oc-subspanbg');
		reportInfoDiv = $('<div/>').attr('class','reportInfoDiv').height(reportInfoDivHeight)//.css('height','92%');  
		
		var reportToolBarDiv = $('<div/>').attr('class','reportToolBarDiv').addClass('oc-toolbar').addClass('datagrid-toolbar');//.css('height','8%'); 
		var dateStartLabel = $('<label/>').html('生成时间：从').css('padding','0px 8px');
		var dateStartSelect = $('<select/>').attr('name','dateStartSelect').addClass('dateSelect').attr('id','dateStartSelect');
		reportToolBarDiv.append(dateStartLabel);
		reportToolBarDiv.append(dateStartSelect);
		var dateEndLabel = $('<label/>').html(' 到 ');
		var dateEndSelect = $('<select/>').attr('name','dateEndSelect').addClass('dateSelect').attr('id','dateEndSelect');
		var queryButton = $('<a/>').attr('class','reportQueryButton').html('查询');
		var resetButton = $('<a/>').attr('class','reportResetButton').html('重置');
		
		var immediatelyReport = $('<span/>').attr('class','fa fa-play-circle commit-inspect-plan-now light_blue').attr('title','即时报告');
		var exportExcel = $('<span/>').attr('class','fa fa-file-excel-o  light_blue').attr('title','导出EXCEL');
		var exportWord = $('<span/>').attr('class','fa fa-file-word-o  light_blue').attr('title','导出WORD');
		var exportPdf = $('<span/>').attr('class','fa fa-file-pdf-o light_blue').attr('title','导出PDF');
		var removeFile = $('<span/>').attr('class','fa fa-trash-o  light_blue').attr('name','deleteReport').attr('title','删除');
		var spanToolBar = $('<span/>').css('float','right').css('margin','8px 0px');
		
		reportToolBarDiv.append(dateEndLabel);
		reportToolBarDiv.append(dateEndSelect);
		reportToolBarDiv.append(queryButton);
		reportToolBarDiv.append(resetButton);
		//趋势报告无即时报告
		if(thisReportTemplateType!=5){
			spanToolBar.append(immediatelyReport);
		}
		spanToolBar.append(exportExcel);
		spanToolBar.append(exportWord);
		spanToolBar.append(exportPdf);
		spanToolBar.append(removeFile);
		reportToolBarDiv.append(spanToolBar);
		
		var fontpageBtnbox = $('<div/>').attr('class','datagrid-pager pagination').css('height','31px');
		var reportListDiv = $('<div/>').attr('class','reportListDiv').css('height','100%').css('overflow-X','hidden').css('overflow-Y','X:scroll'); 
		var reportListFieldSet = $('<fieldset class="border-none">');
		var reportListInfoDiv = $('<div/>').attr('class','reportListInfoDiv').css('width','100%').css('text-align','center').css('margin','0 auto');
		
		reportListDiv.append(reportListInfoDiv);
		reportInfoDiv.append(reportToolBarDiv);
		reportListFieldSet.append(reportListDiv);
		reportInfoDiv.append(reportListFieldSet);
		
		var fontpageTable = '<table cellspacing="0" cellpadding="0" border="0"><tr>'+
			'<td><a href="javascript:void(0)" class="l-btn l-btn-small l-btn-plain"><span class="l-btn-left l-btn-icon-left oc-page"><span class="l-btn-text l-btn-empty"></span><span class="l-btn-icon pagination-first"></span></span></a></td>'+
			'<td><a href="javascript:void(0)" class="l-btn l-btn-small l-btn-plain"><span class="l-btn-left l-btn-icon-left oc-page"><span class="l-btn-text l-btn-empty"></span><span class="l-btn-icon pagination-prev"></span></span></a></td>'+
			'<td>第</td>'+
			'<td><input class="pagination-num" size="2"></td>'+
			'<td><span class="paginationTotalPage"></span></td>'+
			'<td><a href="javascript:void(0)" class="l-btn l-btn-small l-btn-plain"><span class="l-btn-left l-btn-icon-left oc-page"><span class="l-btn-text l-btn-empty"></span><span class="l-btn-icon pagination-next"></span></span></a></td>'+
			'<td><a href="javascript:void(0)" class="l-btn l-btn-small l-btn-plain"><span class="l-btn-left l-btn-icon-left oc-page"><span class="l-btn-text l-btn-empty"></span><span class="l-btn-icon pagination-last"></span></span></a></td>'+
			'</tr></table><div class="pagination-info"></div>';
		fontpageBtnbox.html(fontpageTable);
		reportInfoDiv.append(fontpageBtnbox);
		
		form.append(topInfoDiv);
		form.append(reportInfoDiv);
		mainDiv.append(form);
		
		var reportInfoDivHeight = mainDiv.height()-topInfoDiv.height();
		var fieldSetHeight = reportInfoDivHeight-reportToolBarDiv.height()-fontpageBtnbox.height()-25;
		reportInfoDiv.height(reportInfoDivHeight);
		reportInfoDiv.find('fieldset[class=border-none]').css('height',fieldSetHeight+'px');
		
		myForm = mainDiv.find('.oc-form');
		oc.ui.form({
			selector:myForm,
			datetimebox:[{
				selector:'.dateSelect',
				editable :false
			}]
		});
		
		tempDiv = $('<div/>').css('text-align','center').css('margin','0 auto').css('width','90%').attr('style','margin:10px 0 0 14px;');
		reportToolBarDiv.find(".reportQueryButton").linkbutton('RenderLB',{
			iconCls: 'icon-search',
			onClick : function(){
				reportListInfoQuery(0,rowCount);
			}
		});
		reportToolBarDiv.find(".reportResetButton").linkbutton('RenderLB',{
			iconCls: 'icon-reset',
			onClick : function(){
				//重置搜索时间
				var initTime = new Date().stringify();
				reportToolBarDiv.find('#dateStartSelect').datetimebox('setValue',initTime);
				reportToolBarDiv.find('#dateEndSelect').datetimebox('setValue',initTime);
				reportListInfoQuery(0,rowCount);
			}
		});
		getReportInfo(tempDiv,reportInfoDiv,topInfoDiv);
		
		reportToolBarDiv.find("span").on('click',function(){
			var toolBarClass = $(this).attr('class');
			switch (toolBarClass) {
			case 'fa fa-file-excel-o  light_blue':
				exportFile(tempDiv,'excel');
				break;
			case 'fa fa-file-word-o  light_blue':
				exportFile(tempDiv,'word');
				break;
			case 'fa fa-file-pdf-o light_blue':
				exportFile(tempDiv,'pdf');
				break;
			case 'fa fa-trash-o  light_blue':
				if(reportInfoDiv.find(":checked").length==0){
					alert('请选择要操作的报表!');
					return ;
				}
				oc.ui.confirm('确认删除该报表?',function(){
					var reportListIdArr,xmlFileIdArr;
					tempDiv.find(":checked").each(function(){
						var modelFileId = $(this).attr('id');
						var xmlFileID = $(this).attr('class');
						if(null==reportListIdArr){
							reportListIdArr = modelFileId;
						}else{
							reportListIdArr += ','+modelFileId;
						}if(null==xmlFileIdArr){
							xmlFileIdArr = xmlFileID;
						}else{
							xmlFileIdArr += ','+xmlFileID;
						}
				    })
					oc.util.ajax({
						url:oc.resource.getUrl('portal/report/reportTemplateList/delReportListByIdArr.htm'),
						data:{reportListIdArr:reportListIdArr,xmlFileIdArr:xmlFileIdArr},
						success:function(data){
							if(!data.data){
								alert('删除失败!');
							}
							//刷新
							reportListInfoQuery(0,rowCount);
						}
					})
				})
				break;
			case 'fa fa-play-circle commit-inspect-plan-now light_blue':
				var reportTypeNameTemp;
				switch (thisReportTemplateType) {
				case 1:
					reportTypeNameTemp = "性能报告";
					break;
				case 2:
					reportTypeNameTemp = "告警统计";
					break;
				case 3:
					reportTypeNameTemp = "TOPN报告";
					break;
				case 4:
					reportTypeNameTemp = "可用性报告";
					break;
				case 5:
					reportTypeNameTemp = "趋势报告";
					break;
				case 6:
					reportTypeNameTemp = "分析报告";
					break;
				case 7:
					reportTypeNameTemp = "业务报告";
					break;
				case 100:
					reportTypeNameTemp = "综合报告";
					break;
				}
				var reportNameTemp ; 
				$('[name="titleName-'+thisReportTemplateId+'"]').each(function(){
					reportNameTemp = $(this).html();
				});
				oc.resource.loadScript('resource/module/report-management/js/resourcesOutlook.js',function(){
					oc.module.reportmanagement.report.rol
							.openPreview({
								reportTemplateId : thisReportTemplateId,
								reportName : $('#reportMainMenu').tree('find', thisReportTemplateId).attributes.reportTemplateName,
								reportTypeCode : thisReportTemplateType,
								reportTypeName : reportTypeNameTemp
							});
				});
				break;
			}
		})
	}
	function exportFile(tempDiv,type){
		if(tempDiv.find(":checked").length==0){
			alert('请选择要操作的报表!');
			return ;
		}else if(tempDiv.find(":checked").length>5){
			alert('批量下载的报表,请不要大于5个!');
			return ;
		}
		var xmlFileIDArr,modelFileIdArr,reportTemplateIdArr,reportCreateTimeStrArr;
	    tempDiv.find(":checked").each(function(){
			var modelFileId = $(this).attr('name');
			var xmlFileID = $(this).attr('class');
			var createTimeStr = $(this).attr('createTimeStr');
			var reportTemplateIdTemp = $(this).attr('reportTemplateId');
			if(null==reportTemplateIdArr){
				reportTemplateIdArr = reportTemplateIdTemp
			}
			if(null==reportCreateTimeStrArr){
				reportCreateTimeStrArr = createTimeStr;
			}else{
				reportCreateTimeStrArr += ','+createTimeStr;
			}
			if(null==modelFileIdArr){
				modelFileIdArr = modelFileId;
			}else{
				modelFileIdArr += ','+modelFileId;
			}
			if(null==xmlFileIDArr){
				xmlFileIDArr = xmlFileID;
			}else{
				xmlFileIDArr += ','+xmlFileID;
			}
	    })
	    var iframeHtml = $("<iframe style='display:none' src='"+oc.resource.getUrl('portal/report/reportTemplateXmlInfo/getFileDownload.htm?xmlFileID='+
	    		xmlFileIDArr+'&modelFileId='+modelFileIdArr+'&reportTemplateId='+reportTemplateIdArr+'&reportCreateTimeStr='+reportCreateTimeStrArr+'&type='+type)+"'/>");
		iframeHtml.appendTo("body");
	}
	function getReportInfo(tempDiv,reportInfoDiv,topInfoDiv){
		oc.util.ajax({
			successMsg:null,
			url:oc.resource.getUrl('portal/report/reportTemplateList/getReportTemplateAndreportListById.htm'),
			data:{reportTemplateId:thisReportTemplateId,startRow:startRow,rowCount:rowCount,totalRecord:totalRecord},
			success:function(data){
				var reportTemplateInfoData = data.data.reportTemplateInfo;
				var reportListData = data.data.reportList.reports;
				pageDescription(data.data.reportList.startRow,data.data.reportList.totalRecord);
				
				var reportTemplateInfo = formatReportTemplateInfo(reportTemplateInfoData);
				
				var topInfoDivInside = $('<div/>').addClass('bbtop').addClass('cl-information').attr('style','border:hidden;');
				var topInfoDivInsideUL = $('<ul/>');
				var topInfoDivInsideLI1 = $('<li/>').html('报告周期：'+reportTemplateInfo.reportTemplateCycle);
				var topInfoDivInsideLI2 = $('<li/>').html('时间范围：'+reportTemplateInfo.timeFrameStr);
				var topInfoDivInsideLI3 = $('<li/>').html('生成时间：'+reportTemplateInfo.generateTimeStr);
				var topInfoDivInsideLI4 = $('<li/>').html('状态：'+reportTemplateInfo.reportTemplateStatus);
				var topInfoDivInsideLI5 = $('<li/>').html('邮件订阅：'+reportTemplateInfo.reportTemplateEmailStatus);
				var topInfoDivInsideLI6 = $('<li/>').html('创建人：'+reportTemplateInfo.reportTemplateCreateUserName);
				var topInfoDivInsideLI7 = $('<li/>').html('模板创建时间：'+reportTemplateInfo.reportTemplateCreateTime);
				//避免dataRow.name过长,修改datagrid列长  按一个字2个长度,一个长度7px动态修改列长
				  var domainName = reportTemplateInfo.reportTemplateDomainName;
				  //业务报告增加域关联，此处显示域信息 20161205 dfw
//				  if(reportTemplateInfoData.reportTemplateType != 7){
					  
					  var domainNameHtml ;
					  
					  if(getStrLength(domainName)>36){
						  domainNameHtml = '<div style="width:100%;"  title='+domainName+' >域：'+domainName.substring(0,domainName.length/2)+'...</div>';
					  }else{
						  domainNameHtml = '<div style="width:100%;"  >域：'+domainName+'</div>';
					  }
					  
					  var topInfoDivInsideLI8 = $('<li/>').html(domainNameHtml);
					  
//				  }
				topInfoDivInsideUL.append(topInfoDivInsideLI1);
				topInfoDivInsideUL.append(topInfoDivInsideLI2);
				topInfoDivInsideUL.append(topInfoDivInsideLI3);
				topInfoDivInsideUL.append(topInfoDivInsideLI4);
				topInfoDivInsideUL.append(topInfoDivInsideLI5);
				topInfoDivInsideUL.append(topInfoDivInsideLI6);
				if(thisReportTemplateType!=7){
					topInfoDivInsideUL.append(topInfoDivInsideLI7);
				}
				topInfoDivInsideUL.append(topInfoDivInsideLI8);
				topInfoDiv.append(topInfoDivInsideUL);
				
				tempDiv.html('');
				if(reportListData.length>0){
					fillTempDiv(reportListData);
				}else{
					mainBindMethod();
				}
			}
		})
	}
	function reportListInfoQuery(startRowTemp,rowCountTemp){
		var dateStartStr = myForm.find('#dateStartSelect').datetimebox('getValue');
		var dateEndStr = myForm.find('#dateEndSelect').datetimebox('getValue');
		
		if(queryDateCheckMethod(dateStartStr,dateEndStr)){
			oc.util.ajax({
				successMsg:null,
				url:oc.resource.getUrl('portal/report/reportTemplateList/getReportListByTemplateId.htm'),
				data:{reportTemplateId:thisReportTemplateId,reportDateStartSelect:dateStartStr,reportDateEndSelect:dateEndStr,startRow:startRowTemp,rowCount:rowCountTemp},
				success:function(data){
					var reportListData = data.data.reports;
					
					tempDiv.html('');
					if(reportListData.length>0){
						fillTempDiv(reportListData);
					}
					pageDescription(data.data.startRow,data.data.totalRecord);
					
				}
			})
		}
	}
	function queryDateCheckMethod(dateStartStrSource,dateEndStrSource){
		var dateStartStr = dateStartStrSource.trim();
		var dateEndStr = dateEndStrSource.trim();
		if(dateStartStr=='' || dateEndStr==''){
			alert('日期输入不能为空！');
			return false;
		}
		var dateStart = new Date(dateStartStr.replace(/-/g,"/"));
		var dateEnd = new Date(dateEndStr.replace(/-/g,"/"));
		
		var timeSub ;
		if(isNaN(dateEnd.getTime()) || isNaN(dateStart.getTime())){
			alert('请输入正确的日期格式！');
			return false;
		}else{
			timeSub = dateEnd.getTime() - dateStart.getTime();
			if(timeSub<0 ){
				alert('开始日期应在结束日期之前！');
				return false;
			}else{
				return true;
			}
		}
	}
	function fillTempDiv(reportListData){
		var tempDivInside = $('<div/>').css('text-align','center').css('margin','0 auto').css('width','90%').attr('style','margin:10px 0 0 14px;');

		for(var x=0;x<reportListData.length;x++){
			var i = x;
			var reSpacing = $('<div/>').addClass('re-spacing');
			var bg = $('<div/>').addClass('report-bg');
			var bgImgSrc ;
			switch (thisReportTemplateType) {
			case 1:
				if(1==reportListData[i].reportStatus){
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-xn-h.png');
				}else{
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-xn.png');
				}
				break;
			case 2:
				if(1==reportListData[i].reportStatus){
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-gj-h.png');
				}else{
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-gj.png');
				}
				break;
			case 3:
				if(1==reportListData[i].reportStatus){
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-top-h.png');
				}else{
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-top.png');
				}
				break;
			case 4:
				if(1==reportListData[i].reportStatus){
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-kyx-h.png');
				}else{
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-kyx.png');
				}
				break;
			case 5:
				if(1==reportListData[i].reportStatus){
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-qs-h.png');
				}else{
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-qs.png');
				}
				break;
			case 6:
				if(1==reportListData[i].reportStatus){
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-fx-h.png');
				}else{
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-fx.png');
				}
				break;
			case 7:
				if(1==reportListData[i].reportStatus){
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-bu-h.png');
				}else{
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-bu.png');
				}
				break;
			case 100:
				if(1==reportListData[i].reportStatus){
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-zh-h.png');
				}else{
					bgImgSrc = oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-zh.png');
				}
				break;
			}
			
			var bgImg = $('<img>').attr('src',bgImgSrc).addClass('re-ico-position').attr('name',reportListData[i].reportXmlData);
			bg.append(bgImg);
			var fot = $('<div/>').addClass('re-fot').append('<input type="checkbox" reportTemplateId="'+reportListData[i].reportTemplateId+'" createTimeStr="'+formatCreateTime(reportListData[i].reportGenerateTime,'day')+'"  name="'+reportListData[i].reportModelName+'" id="'+reportListData[i].reportListId+'" class="'+reportListData[i].reportXmlData+'" >'+formatCreateTime(reportListData[i].reportGenerateTime,'forImg'));
			reSpacing.append(bg).append(fot);
			tempDivInside.append(reSpacing);
		}
		var reportListInfoDivTemp = reportInfoDiv.find('.reportListInfoDiv');
		reportListInfoDivTemp.html('');
		tempDiv = tempDivInside;
		reportListInfoDivTemp.append(tempDiv);
		
		mainBindMethod();
	}
	function mainBindMethod(){
		tempDiv.find('.re-spacing').unbind('click');
		tempDiv.find('.re-spacing').on('click',function(e){
			switch ($(e.target).attr('class')) {
			case 're-ico-position'://re-ico-position   report-bg
				var reportXmlDataId = $(e.target).attr('name');
				oc.resource.loadScript('resource/module/report-management/js/resourcesOutlook.js',function(){
		 			oc.module.reportmanagement.report.rol.open({reportXmlDataId:reportXmlDataId,callback:function(){
		 				switch (thisReportTemplateType) {
						case 1:
							$(e.target).attr('src',oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-xn-h.png'));
							break;
						case 2:
							$(e.target).attr('src',oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-gj-h.png'));
							break;
						case 3:
							$(e.target).attr('src',oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-top-h.png'));
							break;
						case 4:
							$(e.target).attr('src',oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-kyx-h.png'));
							break;
						case 5:
							$(e.target).attr('src',oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-qs-h.png'));
							break;
						case 6:
							$(e.target).attr('src',oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-fx-h.png'));
							break;
						case 7:
							$(e.target).attr('src',oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-bu-h.png'));
							break;
						case 100:
							$(e.target).attr('src',oc.resource.getUrl('resource/themes/'+skin+'/images/ico/report-zh-h.png'));
							break;
						}
		 			
		 			}});
		 		});
				
				break;
			case 're-fot':
				if($(this).find(':checkbox').attr('checked')){
					$(this).find(':checkbox').attr('checked',false);
				}else{
					$(this).find(':checkbox').attr('checked',true);
				}
				break;
			}
		})
		//右上模板操作按钮
		$('.oc-header-m').unbind('click');
		$('.oc-header-m').on('click',function(e){
			switch ($(e.target).attr('class')) {
			case 'ico  locate-right':
				var openIco = $('.oc-header-m').find('.h-open-ico');
				var showStatus = openIco.css('display');
				if(showStatus=='block' || showStatus.indexOf('block')>-1){
					openIco.hide();
				}else{
					openIco.show();
				}
				break;
			case 'icon-edit locate-right':
				oc.resource.loadScript('resource/module/report-management/js/report_template_detail.js', function(){
					  oc.module.reportmanagement.report.template.detail.update(thisReportTemplateId);
				  });
				break;
			case 'fa fa-trash-o  locate-right':
				var dialogSetIds = $('<div/>');
				dialogSetIds.dialog({
				    title: '确认删除',
				    width : 300,
					height : 150,
					buttons:[{
				    	text: '确定',
				    	handler:function(){
				    		var value = dialogSetIds.find(":checked").attr('value');
				    		var clickUrl = oc.resource.getUrl('resource/module/report-management/report_main_center_detail.html');
				    		
							oc.util.ajax({
								url:oc.resource.getUrl('portal/report/reportTemplateList/delReportTemplateById.htm'),
								data:{reportTemplateId:thisReportTemplateId,removeType:value},
								success:function(data){
									$('[name="titleName-'+thisReportTemplateId+'"]').parent().parent().remove();
									//在菜单中去除该栏,右侧菜单变更,刷新
									switch (thisReportTemplateType) {
									case 1:
										oc.report.management.main.reportMainMenuClick(clickUrl,{name:"性能报告",type:1});
										break;
									case 2:
										oc.report.management.main.reportMainMenuClick(clickUrl,{name:"告警统计",type:2});
										break;
									case 3:
										oc.report.management.main.reportMainMenuClick(clickUrl,{name:"TOPN报告",type:3});
										break;
									case 4:
										oc.report.management.main.reportMainMenuClick(clickUrl,{name:"可用性报告",type:4});
										break;
									case 5:
										oc.report.management.main.reportMainMenuClick(clickUrl,{name:"趋势报告",type:5});
										break;
									case 6:
										oc.report.management.main.reportMainMenuClick(clickUrl,{name:"分析报告",type:6});
										break;
									case 7:
										oc.report.management.main.reportMainMenuClick(clickUrl,{name:"业务报告",type:7});
										break;
									case 100:
										oc.report.management.main.reportMainMenuClick(clickUrl,{name:"综合报告",type:100});
										break;
									}
								}
							})
							
				    		dialogSetIds.dialog('destroy');
				    	}
				    },{
				    	text: '取消',
				    	handler:function(){
				    		dialogSetIds.dialog('destroy');
				    	}
				    }],
				    onLoad:function(){
				    	//_init(dialogSetIds);
				    }
				});
				var checkBoxDiv = $('<div/>').append('<table><tr><td><input style="vertical-align:middle;" type="radio" checked="true" name="removeReport" value="1"/></td><td>只删除模板</tr></td>'+
						'<tr><td><input style="vertical-align:middle;" type="radio" name="removeReport" value="2"/></td><td>删除模板及所属报告</td></tr></table>');
				
				dialogSetIds.append(checkBoxDiv);
				dialogSetIds.find(':checkbox').on('click',function(e){
					dialogSetIds.find(':checkbox').each(function(){
						$(this).attr('checked',false);
					})
					$(this).attr('checked',true);
				})
				break;
			
			}
		})
		
		$('.reportInfoDiv').find('.l-btn-icon').unbind('click');
		$('.reportInfoDiv').find('.l-btn-icon').on('click',function(e){
			switch ($(e.target).attr('class')) {
			case 'l-btn-icon pagination-first':
				if(totalPage>0){
					if(totalRecord>0 && pageThis!=1){
						pageThis = 1;
						startRow = 0;
						//请求数据
						reportListInfoQuery(0,rowCount);
					}
				}
				break;
			case 'l-btn-icon pagination-prev':
				if(totalPage>0&&pageThis>1){
					pageThis -=1;
					startRow = startRow-rowCount;
					//请求数据
					reportListInfoQuery(startRow,rowCount);
				}
				break;
			case 'l-btn-icon pagination-next':
				if(totalPage>0&&pageThis<totalPage){
					pageThis +=1;
				    startRow = startRow+rowCount;
				    //请求数据
				    reportListInfoQuery(startRow,rowCount);
				}
				
				break;
			case 'l-btn-icon pagination-last':
				if(totalPage>0){
					if(totalRecord>0 && pageThis!=totalPage){
					pageThis = totalPage;
					startRow = (totalPage-1)*rowCount;
					//请求数据
					reportListInfoQuery(startRow,rowCount);
					}
				}
				break;
			}
		})
		
		$('.reportInfoDiv').find('.pagination-num').unbind('change');
		$('.reportInfoDiv').find('.pagination-num').bind('change',function(){
			var pageTemp = parseInt($('.reportInfoDiv').find('.pagination-num').attr('value'));
			if(pageTemp>0&&pageTemp<totalPage+1){
				if('NaN'!=pageTemp&&(pageTemp<totalPage||pageTemp==totalPage)){
					pageThis = pageTemp;
					startRow = (pageTemp-1)*rowCount;
					//请求数据
					reportListInfoQuery(startRow,rowCount);
				}
			}
		})
		
	}
	function pageDescription(startRowTemp,totalRecordTemp){
		startRow = startRowTemp;
		totalRecord = totalRecordTemp;
		
		if(totalRecord==0){
			totalPage = 0;
			pageThis = 0;
		}else if(0<totalRecord&&totalRecord<rowCount){
			totalPage = 1;
			pageThis = 1;
			
			
		}else {
			var num = Math.floor(totalRecord/rowCount);
			if(totalRecord>num*rowCount){
				totalPage = num+1;
			}else{
				totalPage = num;
			}
		}
		
		pageRange();
		$('.reportInfoDiv').find('.paginationTotalPage').html('共'+totalPage+'页');
		$('.reportInfoDiv').find('.pagination-num').attr('value',pageThis)
		
	}
//	//左侧菜单折叠按钮
//	function collapse_btn(){
//		$("#collapse_btn").click(function(){
//			if($(this).hasClass('fa-angle-double-left')){
//				$('.report_management').layout('collapse','west');
//				$('.report_management').layout('panel','center').parent().css('left','0px');
//				$(this).removeClass('fa-angle-double-left');
//				$(this).addClass('fa-angle-double-right');
//			}else{
//				$('.report_management').layout('expand','west');
//				$(this).removeClass('fa-angle-double-right');
//				$(this).addClass('fa-angle-double-left');
//			}
//		});
//	}
	
	function pageRange(){
		$('.reportInfoDiv').find('.datagrid-pager.pagination').find('a').removeClass(pageBtnDisabledClass);
		
		var pageRange = "";
		
		if(totalRecord==1){
			$('.reportInfoDiv').find('.datagrid-pager.pagination').find('a').addClass(pageBtnDisabledClass);
			pageRange = '显示 1 条记录 , ';
		}else if(totalRecord>1){
			if(pageThis==1){
				$('.reportInfoDiv').find('.datagrid-pager.pagination').find('.pagination-first').parent().parent().addClass(pageBtnDisabledClass);
				$('.reportInfoDiv').find('.datagrid-pager.pagination').find('.pagination-prev').parent().parent().addClass(pageBtnDisabledClass);
			}
			if(totalRecord>pageThis*rowCount){
				pageRange = '显示 '+(startRow+1)+' 到 '+(startRow+rowCount)+' , ';
			}else{
				$('.reportInfoDiv').find('.datagrid-pager.pagination').find('.pagination-next').parent().parent().addClass(pageBtnDisabledClass);
				$('.reportInfoDiv').find('.datagrid-pager.pagination').find('.pagination-last').parent().parent().addClass(pageBtnDisabledClass);
				pageRange = '显示 '+(startRow+1)+' , ';
			}
		}
		$('.reportInfoDiv').find('.pagination-info').html(pageRange+'共'+totalRecord+'记录');
	}
	function formatReportTemplateInfo(reportTemplateInfoData){
		//根据模板是否可被编辑,删除状态,添加右上模板操作
		var htmlStr ='';
//		if(reportTemplateInfoData.edit){
//			htmlStr+='<span class="icon-edit locate-right" title="编辑报表"></span>';
//		}
//		
//		if(reportTemplateInfoData.remove){
//			htmlStr+='<span class="fa fa-trash-o  locate-right" title="删除"></span></div>';
//		}else{
//			//设置产生的报表的删除
//			reportInfoDiv.find('span[name=deleteReport]').css('display','none');
//		}
		
		$('.oc-header-m').find('.panel-title').append(htmlStr);
		
		
		var reportTemplateName,
		reportTemplateCycle,
		reportTemplateStatus,
		reportTemplateEmailStatus,
		reportTemplateCreateUserName,
		reportTemplateCreateTime,
		reportTemplateDomainName;
		
		reportTemplateName = reportTemplateInfoData.reportTemplateName;
		reportTemplateFirstGenerateTime = reportTemplateInfoData.reportTemplateFirstGenerateTime;
		reportTemplateSecondGenerateTime = reportTemplateInfoData.reportTemplateSecondGenerateTime;
		reportTemplateThirdGenerateTime = reportTemplateInfoData.reportTemplateThirdGenerateTime;
		reportTemplateDomainName = reportTemplateInfoData.reportTemplateDomainName
		
		reportTemplateCycle = fromatTemplateCycle(reportTemplateInfoData.reportTemplateCycle);
		var timeFrameStr = formateTemplateTimeFrame(reportTemplateInfoData.reportTemplateCycle,reportTemplateInfoData.reportTemplateBeginTime,reportTemplateInfoData.reportTemplateEndTime);
		var generateTimeStr = formatGenerateTimeStr(reportTemplateInfoData.reportTemplateCycle,reportTemplateInfoData.reportTemplateFirstGenerateTime,
				reportTemplateInfoData.reportTemplateSecondGenerateTime,reportTemplateInfoData.reportTemplateThirdGenerateTime);
		reportTemplateStatus = formatTemplateStatus(reportTemplateInfoData.reportTemplateStatus);
		reportTemplateEmailStatus = formatTemplateEmailStatus(reportTemplateInfoData.reportTemplateEmailStatus);
		reportTemplateCreateUserName = reportTemplateInfoData.reportTemplateCreateUserName;
		reportTemplateCreateTime = formatCreateTime(reportTemplateInfoData.reportTemplateCreateTime,null);
		
		return {reportTemplateName:reportTemplateName,reportTemplateCycle:reportTemplateCycle,timeFrameStr:timeFrameStr,reportTemplateDomainName:reportTemplateDomainName
			,generateTimeStr:generateTimeStr,reportTemplateStatus:reportTemplateStatus,reportTemplateEmailStatus:reportTemplateEmailStatus,reportTemplateCreateUserName:reportTemplateCreateUserName,reportTemplateCreateTime:reportTemplateCreateTime};
	}
	function formatGenerateTimeStr(templateCycle,firstGenerateTime,secondGenerateTime,thirdGenerateTime){
		if(thisReportTemplateType==5){
			//趋势报告
			var timeFrameStr ='';
			switch (templateCycle) {
			case 1:
				timeFrameStr = '下周 周一 08:00 生成报告';
				break;
			case 2:
				timeFrameStr = '下周 周一 08:00 生成报告';
				break;
			case 3:
				timeFrameStr = '下月 1号 08:00 生成报告';
				break;
			}
			return timeFrameStr;
		}else if(thisReportTemplateType==6){
			//分析报告写死
			var timeFrameStr ='';
			switch (templateCycle) {
			case 1:
				timeFrameStr = '次日 08:00 生成报告';
				break;
			case 2:
				timeFrameStr = '下周一 08:00 生成报告';
				break;
			case 3:
				timeFrameStr = '下月 1号 08:00 生成报告';
				break;
			}
			return timeFrameStr;
		}else{
			var firstGenerateTimeStr,secondGenerateTimeStr,thirdGenerateTimeStr;
			switch (templateCycle) {
			case 1:
				switch (firstGenerateTime) {
				case 0:
					firstGenerateTimeStr = '当日';
					break;
				case 1:
					firstGenerateTimeStr = '次日';
					break;
				}
				break;
			case 2:
				switch (firstGenerateTime) {
				case 0:
					firstGenerateTimeStr = '当周';
					break;
				case 1:
					firstGenerateTimeStr = '次周';
					break;
				}
				var dayOfWeek ;
				switch(secondGenerateTime){
				case 1:
					dayOfWeek = '一';
					break;
				case 2:
					dayOfWeek = '二';
					break;
				case 3:
					dayOfWeek = '三';
					break;
				case 4:
					dayOfWeek = '四';
					break;
				case 5:
					dayOfWeek = '五';
					break;
				case 6:
					dayOfWeek = '六';
					break;
				case 7:
					dayOfWeek = '日';
					break;
				}
				secondGenerateTimeStr = '周'+dayOfWeek;
				break;
			case 3:
				switch (firstGenerateTime) {
				case 0:
					firstGenerateTimeStr = '当月';
					break;
				case 1:
					firstGenerateTimeStr = '次月';
					break;
				}
				secondGenerateTimeStr = secondGenerateTime+'号'
				break;
			}
			
			if(templateCycle == 1) {
				secondGenerateTimeStr = formatTemplateTime(secondGenerateTime);
				return firstGenerateTimeStr+' '+secondGenerateTimeStr+' 生成报告';
			}else if(templateCycle == 2){
				thirdGenerateTimeStr = formatTemplateTime(thirdGenerateTime);
				return firstGenerateTimeStr+' '+secondGenerateTimeStr+' '+thirdGenerateTimeStr+' 生成报告';
			}else if(templateCycle == 3){
				thirdGenerateTimeStr = formatTemplateTime(thirdGenerateTime);
				return firstGenerateTimeStr+' '+secondGenerateTimeStr+' '+thirdGenerateTimeStr+' 生成报告';
			}
		}
		
		
	}
	function formatCreateTime(templateCreateTime,type){
		var date = new Date(parseFloat(templateCreateTime));
		if(type=='day'){
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();
		}else if(type=='forImg'){
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'<br>'+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()));	
		}else{
			return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+' '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
		}
	}
	function formatTemplateStatus(templateStatus){
		switch (templateStatus) {
		case 0:
			return '启用';
		case 1:
			return '停用';
		}
	}
	function formatTemplateEmailStatus(templateEmailStatus){
		switch (templateEmailStatus) {
		case 0:
			return '启用';
		case 1:
			return '停用';
		}
	}
	function fromatTemplateCycle(templateCycle){
		var reportTemplateCycle;
		switch (templateCycle) {
		case 1:
			reportTemplateCycle = '日报';
			break;
		case 2:
			reportTemplateCycle = '周报';
			break;
		case 3:
			reportTemplateCycle = '月报';
			break;
		}
		return reportTemplateCycle;
	}
	function formateTemplateTimeFrame(templateCycle,templateBeginTime,templateEndTime){
		if(thisReportTemplateType==5){
			//趋势报告
			var timeFrameStr ='';
			switch (templateCycle) {
			case 1:
				timeFrameStr = '每周 周一 00:00 至 周日 24:00';
				break;
			case 2:
				timeFrameStr = '每周 周一 00:00 至 周日 24:00';
				break;
			case 3:
				timeFrameStr = '每月 1号 至 最后一天';
				break;
			}
			return timeFrameStr;
		}else if(thisReportTemplateType==6){
			//分析报告
			var timeFrameStr ='';
			switch (templateCycle) {
			case 1:
				timeFrameStr = '每日 00:00 至 24:00';
				break;
			case 2:
				timeFrameStr = '每周 周一二三四五六日';
				break;
			case 3:
				timeFrameStr = '每月 1号 至 最后一天';
				break;
			}
			return timeFrameStr;
		}else{
			var timeFrameStr ;
			switch (templateCycle) {
			case 1:
				timeFrameStr = '每天 '+formatTemplateTime(templateBeginTime)+' 至 '+formatTemplateTime(templateEndTime);
				break;
			case 2:
				var dayOfWeekArr = templateBeginTime.split(',');
				var dayOfWeekStr ='';
				for(var length=0;length<dayOfWeekArr.length;length++){
					var dayOfWeek = '';
					switch(dayOfWeekArr[length]){
					case '1':
						dayOfWeek = '一';
						break;
					case '2':
						dayOfWeek = '二';
						break;
					case '3':
						dayOfWeek = '三';
						break;
					case '4':
						dayOfWeek = '四';
						break;
					case '5':
						dayOfWeek = '五';
						break;
					case '6':
						dayOfWeek = '六';
						break;
					case '7':
						dayOfWeek = '日';
						break;
					default:
						dayOfWeek = '';
					break;
					}
					dayOfWeekStr += dayOfWeek;
				}
				
				timeFrameStr = '每周   周'+dayOfWeekStr
				break;
			case 3:
				var strtemplateEndTime;
				if(templateEndTime =='-1'){
					strtemplateEndTime = '最后一天';
				}else{
					strtemplateEndTime = templateEndTime+'号';
				}
				timeFrameStr = '每月 '+templateBeginTime+'号   至 '+strtemplateEndTime;
				break;
			}
			return timeFrameStr;
		}
	}
	function formatTemplateTime(timeStr){
		var timeString = new String(timeStr);
		var timeStrArr = timeString.split(".");
		if(timeStrArr.length>1){
			return timeStrArr[0]+':30';
		}else{
			return timeStrArr[0]+':00';
		}
	}
	//将字符串内中文换算为两个长度
	function getStrLength(str){
		if(str){
			var strNew = str.replace(/([^\u0000-\u00FF])/g, function ($) { return escape('--'); });
			return strNew.length;
		}else{
			return '';
		}
	}
	
	/**
	 * 提供给外部引用的接口
	 */
	oc.report.main.center.download={
		open:function(data){
			thisReportTemplateId = data.reportTemplateId;
			thisReportTemplateType = data.reportTemplateType;
			
			init();
//			collapse_btn();
		}
	};
})(jQuery);